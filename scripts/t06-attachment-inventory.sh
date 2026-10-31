#!/usr/bin/env bash
# Production attachment metadata inventory.  This is deliberately separate
# from db-copy*.sh: it never creates PRODLINK, never starts Docker, never
# copies files, and never writes to a database.
#
# Usage:
#   scripts/t06-attachment-inventory.sh --production-read-only <0600-config>
#
# The config must name a DBA-provisioned SELECT-only account and a pre-existing
# pinned Oracle JDBC driver.  Raw IDs/names stay in private/t06-inventory/.
set -euo pipefail
umask 077
export LC_ALL=C

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd -P)"
HELPER="$ROOT/tools/t06-attachment-inventory/AttachmentInventory.java"
DEFAULT_PARENT="$ROOT/private/t06-inventory"

die() { echo "[t06-attachment-inventory] ERROR: $*" >&2; exit 1; }

usage() {
  cat >&2 <<'EOF'
Usage:
  scripts/t06-attachment-inventory.sh --production-read-only <0600-config>

The config is restricted material and must contain only the documented
PRODUCTION_READ_ONLY/DB_ACCOUNT_PURPOSE/JDBC_URL/DB_USER/DB_PASSWORD/
JDBC_DRIVER_CLASS/JDBC_DRIVER_SHA256/SCHEMA keys.  The account must be
provisioned as SELECT-only by a DBA; application credentials are rejected by
policy and must not be copied into this file. Include CONSISTENCY_MODE,
CONSISTENCY_REFERENCE, and (for flashback-scn mode) FLASHBACK_SCN.
EOF
  exit 2
}

[ "$#" = 2 ] && [ "$1" = --production-read-only ] || usage
CONFIG="$2"
[ -f "$CONFIG" ] && [ ! -L "$CONFIG" ] || die "config is missing, not a regular file, or is a symlink"
[ "$(realpath "$CONFIG")" != "$ROOT/private" ] || die "config must be a file below private/"
case "$(realpath "$CONFIG")" in "$ROOT"/private/*) ;; *) die "config must be stored below private/" ;; esac
[ "$(stat -c '%a' "$CONFIG")" = 600 ] || die "config must have mode 0600"
[ "$(stat -c '%u' "$CONFIG")" = "$(id -u)" ] || die "config owner does not match invoking user"
[ -f "$HELPER" ] && [ ! -L "$HELPER" ] || die "inventory helper is missing"

read_key() {
  local key="$1"
  awk -F= -v wanted="$key" '
    /^[[:space:]]*#/ || /^[[:space:]]*$/ { next }
    $1 == wanted { if (++n > 1) exit 2; print substr($0, index($0, "=") + 1) }
    END { if (n > 1) exit 2 }
  ' "$CONFIG"
}

# Resolve the driver from the fixed, audited repository path rather than an
# arbitrary config value.  The Java config schema intentionally has no driver
# path key.
DRIVER="$ROOT/tools/db-probe/lib/ojdbc8-19.3.0.0.jar"
[ -f "$DRIVER" ] && [ ! -L "$DRIVER" ] || die "pinned JDBC driver is missing; no extraction or download is performed"
EXPECTED_SHA="$(read_key JDBC_DRIVER_SHA256)" || die "missing or duplicate JDBC_DRIVER_SHA256"
[[ "$EXPECTED_SHA" =~ ^[0-9a-f]{64}$ ]] || die "JDBC_DRIVER_SHA256 is not a SHA-256 value"
[ "$(sha256sum "$DRIVER" | awk '{print $1}')" = "$EXPECTED_SHA" ] \
  || die "pinned JDBC driver hash mismatch"

DRIVER_CLASS="$(read_key JDBC_DRIVER_CLASS)" || die "missing JDBC_DRIVER_CLASS"
[ "$DRIVER_CLASS" = oracle.jdbc.OracleDriver ] || die "only the pinned Oracle driver class is accepted"
[ -n "$(read_key JDBC_URL)" ] || die "missing JDBC_URL"
DB_USER="$(read_key DB_USER)" || die "missing DB_USER"
[ -n "$DB_USER" ] || die "missing DB_USER"
[ -n "$(read_key DB_PASSWORD)" ] || die "missing DB_PASSWORD"
[ "$(read_key PRODUCTION_READ_ONLY)" = 1 ] || die "explicit production-read-only attestation is required"
[ "$(read_key DB_ACCOUNT_PURPOSE)" = t06_attachment_inventory_select_only ] \
  || die "config does not attest to a dedicated SELECT-only inventory account"
SCHEMA="$(read_key SCHEMA)" || die "missing SCHEMA"
[[ "$SCHEMA" =~ ^[A-Z][A-Z0-9_$#]*$ ]] || die "SCHEMA is not a safe Oracle identifier"

APP_PROPS="$ROOT/private/application.properties"
if [ -e "$APP_PROPS" ]; then
  [ -f "$APP_PROPS" ] && [ ! -L "$APP_PROPS" ] || die "local application.properties is not a regular non-symlink file"
  APP_USER="$(awk -F= '
    /^[[:space:]]*#/ || /^[[:space:]]*$/ { next }
    $1 == "spring.datasource.username" { value = substr($0, index($0, "=") + 1); sub(/\r$/, "", value); count += 1 }
    END { if (count == 1) print value; else exit 1 }
  ' "$APP_PROPS")" || die "could not safely parse the local application username"
  [ "$(tr '[:lower:]' '[:upper:]' <<<"$DB_USER")" != "$(tr '[:lower:]' '[:upper:]' <<<"$APP_USER")" ] \
    || die "dedicated inventory account must not be the application schema account"
fi

[ ! -e "$DEFAULT_PARENT" ] || [ ! -L "$DEFAULT_PARENT" ] || die "inventory output parent must not be a symlink"
mkdir -p -m 700 "$DEFAULT_PARENT"
[ "$(stat -c '%u' "$DEFAULT_PARENT")" = "$(id -u)" ] || die "inventory output parent owner mismatch"
(( (8#$(stat -c '%a' "$DEFAULT_PARENT") & 0077) == 0 )) || die "inventory output parent permissions are too broad"
timestamp="$(date -u +%Y%m%dT%H%M%SZ)"
OUT="$DEFAULT_PARENT/$timestamp"
[ ! -e "$OUT" ] || die "inventory output already exists: $OUT"

build_dir="$(mktemp -d /tmp/lims-t06-inventory-build.XXXXXX)"
trap 'rm -rf "$build_dir"' EXIT
chmod 700 "$build_dir"
javac -source 8 -target 8 -cp "$DRIVER" -d "$build_dir" "$HELPER" \
  || die "could not compile the fixed inventory helper"

# Do not echo JDBC_URL, username, password, IDs, filenames, or paths from the
# production source.  The Java helper writes only restricted output files.
java -cp "$build_dir:$DRIVER" AttachmentInventory \
  --production-read-only "$CONFIG" "$OUT" \
  || die "read-only inventory failed; partial restricted output retained at $OUT"

chmod 700 "$OUT"
echo "[t06-attachment-inventory] PASS output=$OUT (restricted; raw metadata stays private)"
