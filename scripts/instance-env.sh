#!/usr/bin/env bash
# Create and validate the restricted runtime input for one offline replay
# instance.  It deliberately parses key=value data instead of sourcing it:
# replay credentials must never be able to execute shell code.
set -euo pipefail
umask 077
export LC_ALL=C

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd -P)"
INSTANCE_ROOT="$ROOT/private/instances"

instance_env_die() {
  echo "[instance-env] ERROR: $*" >&2
  exit 1
}

instance_env_usage() {
  cat >&2 <<'EOF'
Usage:
  scripts/instance-env.sh init <instance-id> <host-port>
  scripts/instance-env.sh validate <instance-id>
  scripts/instance-env.sh show <instance-id>

init creates private/instances/<instance-id>/runtime.env with mode 0600. Fill
the three image-ID pins and three secret values through the approved restricted
channel. Also place
the reviewed local-only application configuration at
private/instances/<instance-id>/application-isolated.properties (mode 0600),
then run validate before using scripts/stack-demo-restore.sh.
EOF
  exit 2
}

instance_env_validate_id() {
  local id="${1:-}"
  [[ "$id" =~ ^[a-z][a-z0-9-]{0,23}$ ]] \
    || instance_env_die "instance id must match [a-z][a-z0-9-]{0,23}: ${id:-empty}"
}

instance_env_validate_port() {
  local port="${1:-}"
  [[ "$port" =~ ^[1-9][0-9]{3,4}$ ]] \
    || instance_env_die "host port must be a decimal port from 1024 through 65535"
  (( 10#$port >= 1024 && 10#$port <= 65535 )) \
    || instance_env_die "host port must be from 1024 through 65535"
}

instance_env_set_paths() {
  instance_env_validate_id "$1"

  INSTANCE_ID="$1"
  INSTANCE_DIR="$INSTANCE_ROOT/$INSTANCE_ID"
  INSTANCE_RUNTIME_ENV="$INSTANCE_DIR/runtime.env"
  INSTANCE_CONFIG_FILE="$INSTANCE_DIR/application-isolated.properties"
  INSTANCE_LICENSE_DIR="$INSTANCE_DIR/lic"
  INSTANCE_FILES_DIR="$INSTANCE_DIR/files"
  INSTANCE_REPLAY_MANIFEST="$INSTANCE_DIR/replay-manifest.env"
  INSTANCE_FORWARDER_PID="$INSTANCE_DIR/portfwd.pid"
  INSTANCE_FORWARDER_LOG="$INSTANCE_DIR/portfwd.log"

  REPLAY_PREFIX="lims-replay-$INSTANCE_ID"
  REPLAY_NETWORK="${REPLAY_PREFIX}-net"
  REPLAY_ORACLE="${REPLAY_PREFIX}-oracle"
  REPLAY_REDIS="${REPLAY_PREFIX}-redis"
  REPLAY_APP="${REPLAY_PREFIX}-app"
  REPLAY_ATTACHMENT_VOLUME="${REPLAY_PREFIX}-files"
  REPLAY_SUBNET=""

  REPLAY_LABEL_KEY="com.sunway.lims.offline-replay"
  REPLAY_LABEL_VALUE="true"
  REPLAY_INSTANCE_LABEL="com.sunway.lims.offline-replay.instance"
  REPLAY_COMPONENT_LABEL="com.sunway.lims.offline-replay.component"
}

instance_env_replay_subnet() {
  local checksum octet
  checksum="$(printf '%s' "$INSTANCE_ID" | cksum | awk '{print $1}')"
  octet=$((20 + checksum % 200))
  # Use RFC1918 10/8 space.  The collision preflight in stack-demo-restore
  # rejects an already allocated subnet before creating any replay resource.
  REPLAY_SUBNET="10.${octet}.0.0/16"
}

instance_env_assert_private_path() {
  local path="$1" mode owner
  [ -e "$path" ] && [ ! -L "$path" ] || instance_env_die "missing or symlinked restricted path: $path"
  mode="$(stat -c '%a' "$path")" || instance_env_die "cannot inspect permissions: $path"
  [[ "$mode" =~ ^[0-7]+$ ]] || instance_env_die "unexpected mode from stat for $path"
  (( (8#$mode & 0077) == 0 )) || instance_env_die "restricted path is readable by group or others: $path"
  owner="$(stat -c '%u' "$path")" || instance_env_die "cannot inspect owner: $path"
  [ "$owner" = "$(id -u)" ] || instance_env_die "restricted path is not owned by the invoking user: $path"
}

instance_env_assert_private_tree() {
  local root="$1" entry mode owner listing tree_error=""
  [ -d "$root" ] || instance_env_die "restricted path is not a directory: $root"
  listing="$(mktemp)"
  if ! find -P "$root" -print0 > "$listing"; then
    rm -f "$listing"
    instance_env_die "cannot enumerate restricted tree: $root"
  fi
  while IFS= read -r -d '' entry; do
    if { [ ! -d "$entry" ] && [ ! -f "$entry" ]; } || [ -L "$entry" ]; then
      tree_error="contains a special file or symlink: $entry"
      break
    fi
    if ! mode="$(stat -c '%a' "$entry")" || ! owner="$(stat -c '%u' "$entry")"; then
      tree_error="cannot inspect permissions: $entry"
      break
    fi
    if (( (8#$mode & 0077) != 0 )); then
      tree_error="is readable by group or others: $entry"
      break
    fi
    if [ "$owner" != "$(id -u)" ]; then
      tree_error="is not owned by the invoking user: $entry"
      break
    fi
  done < "$listing"
  rm -f "$listing"
  [ -z "$tree_error" ] || instance_env_die "restricted tree $tree_error"
}

instance_env_validate_secret() {
  local key="$1" value="$2"
  [ -n "$value" ] || instance_env_die "$key must not be empty"
  [[ "$value" =~ ^[[:graph:]]+$ ]] \
    || instance_env_die "$key must contain printable non-whitespace ASCII only"
}

instance_env_validate_mac() {
  local mac="$1" first_octet
  [[ "$mac" =~ ^([[:xdigit:]]{2}:){5}[[:xdigit:]]{2}$ ]] \
    || instance_env_die "MAC_ADDRESS must be six hexadecimal octets"
  [ "${mac//:/}" != "000000000000" ] \
    || instance_env_die "MAC_ADDRESS must not be all zeroes"
  first_octet="${mac%%:*}"
  (( (16#$first_octet & 1) == 0 )) \
    || instance_env_die "MAC_ADDRESS must not be multicast"
}

instance_env_load() {
  [ "$#" = 1 ] || instance_env_usage
  instance_env_set_paths "$1"

  instance_env_assert_private_path "$ROOT/private"
  instance_env_assert_private_path "$INSTANCE_ROOT"
  instance_env_assert_private_path "$INSTANCE_DIR"
  instance_env_assert_private_path "$INSTANCE_RUNTIME_ENV"
  instance_env_assert_private_path "$INSTANCE_CONFIG_FILE"
  instance_env_assert_private_path "$INSTANCE_LICENSE_DIR"
  instance_env_assert_private_path "$INSTANCE_FILES_DIR"
  instance_env_assert_private_tree "$INSTANCE_LICENSE_DIR"
  instance_env_assert_private_tree "$INSTANCE_FILES_DIR"
  [ -f "$INSTANCE_RUNTIME_ENV" ] || instance_env_die "runtime environment is not a regular file: $INSTANCE_RUNTIME_ENV"
  [ -f "$INSTANCE_CONFIG_FILE" ] || instance_env_die "isolated application config is not a regular file: $INSTANCE_CONFIG_FILE"

  local raw key value line=0
  local -A seen=()
  INSTANCE_IMAGE_REF=""
  INSTANCE_IMAGE_ID=""
  INSTANCE_ORACLE_IMAGE_ID=""
  INSTANCE_REDIS_IMAGE_ID=""
  INSTANCE_ORACLE_SYS_PASSWORD=""
  INSTANCE_REDIS_PASSWORD=""
  INSTANCE_MAC_ADDRESS=""
  INSTANCE_HOST_PORT=""

  while IFS= read -r raw || [ -n "$raw" ]; do
    line=$((line + 1))
    raw="${raw%$'\r'}"
    [[ "$raw" =~ ^[[:space:]]*$ || "$raw" =~ ^[[:space:]]*# ]] && continue
    [[ "$raw" =~ ^([A-Z_]+)=(.*)$ ]] \
      || instance_env_die "runtime environment line $line is not an exact KEY=value entry"
    key="${BASH_REMATCH[1]}"
    value="${BASH_REMATCH[2]}"
    [ -z "${seen[$key]+set}" ] || instance_env_die "runtime environment repeats $key"
    seen["$key"]=1

    case "$key" in
      IMAGE_REF) INSTANCE_IMAGE_REF="$value" ;;
      IMAGE_ID) INSTANCE_IMAGE_ID="$value" ;;
      ORACLE_IMAGE_ID) INSTANCE_ORACLE_IMAGE_ID="$value" ;;
      REDIS_IMAGE_ID) INSTANCE_REDIS_IMAGE_ID="$value" ;;
      ORACLE_SYS_PASSWORD) INSTANCE_ORACLE_SYS_PASSWORD="$value" ;;
      REDIS_PASSWORD) INSTANCE_REDIS_PASSWORD="$value" ;;
      MAC_ADDRESS) INSTANCE_MAC_ADDRESS="$value" ;;
      HOST_PORT) INSTANCE_HOST_PORT="$value" ;;
      *) instance_env_die "runtime environment contains unsupported key: $key" ;;
    esac
  done < "$INSTANCE_RUNTIME_ENV"

  local required
  for required in IMAGE_REF ORACLE_SYS_PASSWORD REDIS_PASSWORD MAC_ADDRESS HOST_PORT; do
    [ -n "${seen[$required]+set}" ] || instance_env_die "runtime environment is missing $required"
  done

  for required in IMAGE_ID ORACLE_IMAGE_ID REDIS_IMAGE_ID; do
    [ -n "${seen[$required]+set}" ] || instance_env_die "runtime environment is missing $required; create a new instance input instead of reusing a legacy record"
  done

  [[ "$INSTANCE_IMAGE_REF" =~ ^[a-z0-9][a-z0-9._-]{0,95}:[a-z0-9][a-z0-9._-]{0,95}$ ]] \
    || instance_env_die "IMAGE_REF must be a local image:tag reference without a registry path"
  [ "${INSTANCE_IMAGE_REF##*:}" != "latest" ] \
    || instance_env_die "IMAGE_REF must use an explicit non-latest tag"
  [[ "$INSTANCE_IMAGE_ID" =~ ^sha256:[0-9a-f]{64}$ ]] \
    || instance_env_die "IMAGE_ID must be the approved local SHA-256 image ID"
  [[ "$INSTANCE_ORACLE_IMAGE_ID" =~ ^sha256:[0-9a-f]{64}$ ]] \
    || instance_env_die "ORACLE_IMAGE_ID must be the approved local SHA-256 image ID"
  [[ "$INSTANCE_REDIS_IMAGE_ID" =~ ^sha256:[0-9a-f]{64}$ ]] \
    || instance_env_die "REDIS_IMAGE_ID must be the approved local SHA-256 image ID"
  instance_env_validate_secret ORACLE_SYS_PASSWORD "$INSTANCE_ORACLE_SYS_PASSWORD"
  instance_env_validate_secret REDIS_PASSWORD "$INSTANCE_REDIS_PASSWORD"
  instance_env_validate_mac "$INSTANCE_MAC_ADDRESS"
  instance_env_validate_port "$INSTANCE_HOST_PORT"
}

instance_env_init() {
  [ "$#" = 2 ] || instance_env_usage
  instance_env_set_paths "$1"
  instance_env_validate_port "$2"

  [ ! -e "$INSTANCE_DIR" ] || instance_env_die "instance directory already exists and will not be overwritten: $INSTANCE_DIR"
  install -d -m 700 "$INSTANCE_ROOT"
  instance_env_assert_private_path "$ROOT/private"
  instance_env_assert_private_path "$INSTANCE_ROOT"
  mkdir -m 700 "$INSTANCE_DIR"
  mkdir -m 700 "$INSTANCE_LICENSE_DIR" "$INSTANCE_FILES_DIR"

  cat > "$INSTANCE_RUNTIME_ENV" <<EOF
# Restricted, per-instance offline replay input. Do not source this file.
# Supply the blank values through an approved channel; no other keys are allowed.
IMAGE_REF=lims-app:14.1.5-built
IMAGE_ID=
ORACLE_IMAGE_ID=
REDIS_IMAGE_ID=
ORACLE_SYS_PASSWORD=
REDIS_PASSWORD=
MAC_ADDRESS=
HOST_PORT=$2
EOF
  chmod 600 "$INSTANCE_RUNTIME_ENV"

  echo "[instance-env] created $INSTANCE_RUNTIME_ENV"
  echo "[instance-env] add a mode-0600 local-only application-isolated.properties plus approved files under lic/ and files/, fill the blank values, then run: $0 validate $INSTANCE_ID"
}

instance_env_show() {
  instance_env_load "$1"
  printf 'instance_id=%s\n' "$INSTANCE_ID"
  printf 'runtime_env=%s\n' "$INSTANCE_RUNTIME_ENV"
  printf 'config_file=%s\n' "$INSTANCE_CONFIG_FILE"
  printf 'license_dir=%s\n' "$INSTANCE_LICENSE_DIR"
  printf 'files_dir=%s\n' "$INSTANCE_FILES_DIR"
  printf 'image_ref=%s\n' "$INSTANCE_IMAGE_REF"
  printf 'image_id=%s\n' "$INSTANCE_IMAGE_ID"
  printf 'oracle_image_id=%s\n' "$INSTANCE_ORACLE_IMAGE_ID"
  printf 'redis_image_id=%s\n' "$INSTANCE_REDIS_IMAGE_ID"
  printf 'host_port=%s\n' "$INSTANCE_HOST_PORT"
  printf 'network=%s\n' "$REPLAY_NETWORK"
  printf 'oracle_container=%s\n' "$REPLAY_ORACLE"
  printf 'redis_container=%s\n' "$REPLAY_REDIS"
  printf 'app_container=%s\n' "$REPLAY_APP"
}

if [[ "${BASH_SOURCE[0]}" == "$0" ]]; then
  command="${1:-}"
  case "$command" in
    init) shift; instance_env_init "$@" ;;
    validate) shift; [ "$#" = 1 ] || instance_env_usage; instance_env_load "$1"; echo "[instance-env] PASS $INSTANCE_RUNTIME_ENV" ;;
    show) shift; [ "$#" = 1 ] || instance_env_usage; instance_env_show "$1" ;;
    *) instance_env_usage ;;
  esac
fi
