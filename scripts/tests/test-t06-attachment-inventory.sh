#!/usr/bin/env bash
# Static/fail-closed checks for the production read-only attachment inventory.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SCRIPT="$ROOT/scripts/t06-attachment-inventory.sh"
HELPER="$ROOT/tools/t06-attachment-inventory/AttachmentInventory.java"
DRIVER="$ROOT/tools/db-probe/lib/ojdbc8-19.3.0.0.jar"

[ -x "$SCRIPT" ] || { echo "t06 inventory test: script is not executable" >&2; exit 1; }
[ -f "$HELPER" ] || { echo "t06 inventory test: fixed helper is missing" >&2; exit 1; }
bash -n "$SCRIPT"
grep -Fq -- '--production-read-only' "$SCRIPT" || { echo "t06 inventory test: explicit read-only flag missing" >&2; exit 1; }
if grep -nE '^[[:space:]]*(docker|scp|rsync|ftp)[[:space:]]' "$SCRIPT"; then
  echo "t06 inventory test: script invokes a transfer/container command" >&2
  exit 1
fi
if grep -nE '(@PRODLINK|create[[:space:]]+database[[:space:]]+link|drop[[:space:]]+database[[:space:]]+link)' "$HELPER"; then
  echo "t06 inventory test: helper contains database-link behavior" >&2
  exit 1
fi
grep -Fq 'content_lobs_selected=false' "$HELPER" || {
  echo "t06 inventory test: LOB exclusion marker missing" >&2; exit 1;
}
grep -Fq 'expected_relative_path' "$HELPER" || {
  echo "t06 inventory test: expected path reconciliation field missing" >&2; exit 1;
}
grep -Fq 'soft-delete-marker' "$HELPER" || {
  echo "t06 inventory test: target classification missing" >&2; exit 1;
}
grep -Fq 'flashback-scn' "$HELPER" || {
  echo "t06 inventory test: explicit consistency mode is missing" >&2; exit 1;
}
grep -Fq '! -L "$DEFAULT_PARENT"' "$SCRIPT" || {
  echo "t06 inventory test: output parent symlink guard missing" >&2; exit 1;
}
grep -Fq 'FLASHBACK_SCN' "$HELPER" || {
  echo "t06 inventory test: flashback SCN gate is missing" >&2; exit 1;
}

compile_dir="$(mktemp -d)"
bad_config=""
same_config=""
upper_config=""
trap 'rm -rf "$compile_dir" "$bad_config" "$same_config" "$upper_config"' EXIT
javac -source 8 -target 8 -cp "$DRIVER" -d "$compile_dir" "$HELPER" >/dev/null 2>&1

bad_config="$(mktemp "$ROOT/private/t06-bad-config.XXXXXX")"
chmod 600 "$bad_config"
printf '%s\n' 'PRODUCTION_READ_ONLY=0' 'DB_ACCOUNT_PURPOSE=wrong' > "$bad_config"
if "$SCRIPT" --production-read-only "$bad_config" >/dev/null 2>&1; then
  echo "t06 inventory test: invalid attestation was accepted" >&2; exit 1
fi

same_config="$(mktemp "$ROOT/private/t06-same-config.XXXXXX")"
chmod 600 "$same_config"
app_user="$(awk -F= '/^spring\.datasource\.username=/{print $2; exit}' "$ROOT/private/application.properties" | tr -d '\r')"
driver_sha="$(sha256sum "$DRIVER" | awk '{print $1}')"
{
  printf 'PRODUCTION_READ_ONLY=1\n'
  printf 'DB_ACCOUNT_PURPOSE=t06_attachment_inventory_select_only\n'
  printf 'JDBC_URL=jdbc:oracle:thin:@//127.0.0.1:1/XEPDB1\n'
  printf 'DB_USER=%s\n' "$app_user"
  printf 'DB_PASSWORD=not-a-real-password\n'
  printf 'JDBC_DRIVER_CLASS=oracle.jdbc.OracleDriver\n'
  printf 'JDBC_DRIVER_SHA256=%s\n' "$driver_sha"
  printf 'SCHEMA=%s\n' "$(printf '%s' "$app_user" | tr '[:lower:]' '[:upper:]')"
  printf 'CONSISTENCY_MODE=best-effort\n'
  printf 'CONSISTENCY_REFERENCE=fixture-window\n'
} > "$same_config"
if "$SCRIPT" --production-read-only "$same_config" >/dev/null 2>&1; then
  echo "t06 inventory test: application schema account was accepted" >&2; exit 1
fi

upper_config="$(mktemp "$ROOT/private/t06-upper-config.XXXXXX")"
chmod 600 "$upper_config"
sed 's/^DB_USER=.*/DB_USER='"$(printf '%s' "$app_user" | tr '[:lower:]' '[:upper:]')"'/' "$same_config" > "$upper_config"
if "$SCRIPT" --production-read-only "$upper_config" >/dev/null 2>&1; then
  echo "t06 inventory test: case-insensitive application schema account was accepted" >&2; exit 1
fi

echo "t06 attachment inventory test PASS"
