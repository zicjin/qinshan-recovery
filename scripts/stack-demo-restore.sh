#!/usr/bin/env bash
# Start one new, offline replay stack from an already-restored Oracle volume.
# It never calls stack-up.sh, never creates lims-copy, never pulls an image,
# and only creates resources whose names begin with lims-replay-.
set -euo pipefail
umask 077
export LC_ALL=C

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd -P)"
# shellcheck source=scripts/instance-env.sh
. "$ROOT/scripts/instance-env.sh"

SNAPSHOT_FORMAT="lims-oracle-volume-cold-v1"
ORACLE_IMAGE="gvenzl/oracle-xe:21-slim"
REDIS_IMAGE="redis:7.4-alpine"
ARCHIVER_IMAGE="debian:bullseye-slim"
APP_PORT="8081"
APP_ROOT_PATH="/data/LIMS/jiankeyuan/lims-czjky-emc"
MATERIAL_INDEX_ROOT="$ROOT/materials/index"
ORACLE_SNAPSHOT_SCRIPT="$ROOT/scripts/oracle-snapshot.sh"
ISOLATION_SCRIPT="$ROOT/scripts/isolation-check.sh"
SOURCE_IDENTITY_FILE="${SOURCE_IDENTITY_FILE:-$ROOT/private/source-identity.env}"
REPLAY_LAUNCH_LOCK="$ROOT/private/lims-replay-launch.lock"
SNAPSHOT_LEASE_DIR="$ROOT/private/snapshot-leases"
REPLAY_LOCK_FD=""

die() {
  echo "[stack-demo-restore] ERROR: $*" >&2
  exit 1
}

acquire_replay_launch_lock() {
  local lock_fd lock_mode lock_owner
  [ -d "$ROOT/private" ] && [ ! -L "$ROOT/private" ] \
    || die "private directory is missing or symlinked"
  [ ! -e "$REPLAY_LAUNCH_LOCK" ] || [ ! -L "$REPLAY_LAUNCH_LOCK" ] \
    || die "replay launch lock must not be a symlink"
  if [ -e "$REPLAY_LAUNCH_LOCK" ]; then
    [ -f "$REPLAY_LAUNCH_LOCK" ] || die "replay launch lock is not a regular file"
    lock_mode="$(stat -c '%a' "$REPLAY_LAUNCH_LOCK")"
    lock_owner="$(stat -c '%u' "$REPLAY_LAUNCH_LOCK")"
    (( (8#$lock_mode & 0077) == 0 )) || die "replay launch lock permissions are too broad"
    [ "$lock_owner" = "$(id -u)" ] || die "replay launch lock owner mismatch"
  fi
  exec {lock_fd}>>"$REPLAY_LAUNCH_LOCK"
  REPLAY_LOCK_FD="$lock_fd"
  chmod 600 "$REPLAY_LAUNCH_LOCK"
  flock -n "$lock_fd" || die "another replay launch is in progress; retry after it finishes"
}

reserve_snapshot_lease() {
  local lease="$SNAPSHOT_LEASE_DIR/$SNAPSHOT_VOLUME.lease"
  [ ! -L "$SNAPSHOT_LEASE_DIR" ] || die "snapshot lease directory must not be a symlink"
  [ ! -e "$SNAPSHOT_LEASE_DIR" ] || [ -d "$SNAPSHOT_LEASE_DIR" ] \
    || die "snapshot lease path is not a directory"
  mkdir -p -m 700 "$SNAPSHOT_LEASE_DIR"
  [ "$(stat -c '%u' "$SNAPSHOT_LEASE_DIR")" = "$(id -u)" ] \
    || die "snapshot lease directory owner mismatch"
  (( (8#$(stat -c '%a' "$SNAPSHOT_LEASE_DIR") & 0077) == 0 )) \
    || die "snapshot lease directory permissions are too broad"
  [ ! -L "$lease" ] || die "snapshot volume lease must not be a symlink: $SNAPSHOT_VOLUME"
  [ ! -e "$lease" ] || die "snapshot volume already has a consumption lease: $SNAPSHOT_VOLUME"
  if ! (set -o noclobber; : > "$lease") 2>/dev/null; then
    die "could not atomically reserve snapshot volume: $SNAPSHOT_VOLUME"
  fi
  chmod 600 "$lease"
  {
    printf 'snapshot_volume=%s\n' "$SNAPSHOT_VOLUME"
    printf 'restore_nonce=%s\n' "$SNAPSHOT_RESTORE_NONCE"
    printf 'instance_id=%s\n' "$INSTANCE_ID"
    printf 'reserved_utc=%s\n' "$(date -u +%Y-%m-%dT%H:%M:%SZ)"
  } > "$lease"
  SNAPSHOT_LEASE_FILE="$lease"
}

usage() {
  cat >&2 <<'EOF'
Usage:
  scripts/stack-demo-restore.sh <instance-id> <restored-snapshot-volume>

The instance must have been initialized with scripts/instance-env.sh and its
private runtime.env must pass validation. The volume must already have been
created by scripts/oracle-snapshot.sh restore; this command never creates or
restores a snapshot volume.
EOF
  exit 2
}

need_command() {
  command -v "$1" >/dev/null 2>&1 || die "missing required command: $1"
}

container_exists() {
  docker container inspect "$1" >/dev/null 2>&1
}

network_exists() {
  docker network inspect "$1" >/dev/null 2>&1
}

port_in_use() {
  local output error_file
  error_file="$(mktemp)"
  if ! output="$(ss -ltnH "( sport = :$INSTANCE_HOST_PORT )" 2>"$error_file")"; then
    local error_text
    error_text="$(cat "$error_file")"
    rm -f "$error_file"
    die "cannot verify host port $INSTANCE_HOST_PORT: ${error_text:-ss failed}"
  fi
  if [ -s "$error_file" ]; then
    local error_text
    error_text="$(cat "$error_file")"
    rm -f "$error_file"
    die "cannot verify host port $INSTANCE_HOST_PORT: $error_text"
  fi
  rm -f "$error_file"
  [ -n "$output" ]
}

assert_snapshot_volume() {
  local mounted_by snapshot_archive_hash snapshot_archiver_image_id snapshot_options
  [[ "$SNAPSHOT_VOLUME" =~ ^lims-snapshot-[A-Za-z0-9][A-Za-z0-9._-]{0,48}$ ]] \
    || die "snapshot volume must be a restored lims-snapshot-* volume"
  [ "$SNAPSHOT_VOLUME" != "lims-oracle-data" ] \
    || die "refusing to use the current lims-oracle-data volume"
  docker volume inspect "$SNAPSHOT_VOLUME" >/dev/null 2>&1 \
    || die "missing restored snapshot volume: $SNAPSHOT_VOLUME"
  [ "$(docker volume inspect "$SNAPSHOT_VOLUME" --format '{{.Driver}}')" = "local" ] \
    || die "snapshot volume is not a local Docker volume: $SNAPSHOT_VOLUME"
  snapshot_options="$(docker volume inspect "$SNAPSHOT_VOLUME" --format '{{json .Options}}')"
  case "$snapshot_options" in
    null|{}) ;;
    *) die "snapshot volume has local-driver bind/options and is not an isolated restore volume" ;;
  esac
  [ "$(docker volume inspect "$SNAPSHOT_VOLUME" --format '{{index .Labels "lims.oracle-snapshot.format"}}')" = "$SNAPSHOT_FORMAT" ] \
    || die "snapshot volume lacks the expected restore format label"
  [ "$(docker volume inspect "$SNAPSHOT_VOLUME" --format '{{index .Labels "lims.oracle-snapshot.oracle-image-id"}}')" = "$INSTANCE_ORACLE_IMAGE_ID" ] \
    || die "snapshot volume Oracle image identity does not match the approved replay input"
  SNAPSHOT_RESTORE_NONCE="$(docker volume inspect "$SNAPSHOT_VOLUME" --format '{{index .Labels "lims.oracle-snapshot.restore-nonce"}}')"
  [[ "$SNAPSHOT_RESTORE_NONCE" =~ ^[0-9]{8}T[0-9]{6}Z-[0-9]+-[0-9]+$ ]] \
    || die "snapshot volume lacks the expected restore nonce label"
  snapshot_archive_hash="$(docker volume inspect "$SNAPSHOT_VOLUME" --format '{{index .Labels "lims.oracle-snapshot.archive-sha256"}}')"
  [[ "$snapshot_archive_hash" =~ ^[0-9a-f]{64}$ ]] \
    || die "snapshot volume lacks the expected archive hash label"
  assert_archiver_image
  snapshot_archiver_image_id="$(docker volume inspect "$SNAPSHOT_VOLUME" --format '{{index .Labels "lims.oracle-snapshot.archiver-image-id"}}')"
  [ "$snapshot_archiver_image_id" = "$ARCHIVER_IMAGE_ID" ] \
    || die "snapshot volume archiver image identity does not match the local approved image"
  SNAPSHOT_ARCHIVE_SHA256="$snapshot_archive_hash"
  SNAPSHOT_ARCHIVER_IMAGE_ID="$snapshot_archiver_image_id"
  SNAPSHOT_ORACLE_IMAGE_ID="$(docker volume inspect "$SNAPSHOT_VOLUME" --format '{{index .Labels "lims.oracle-snapshot.oracle-image-id"}}')"
  restore_marker="$(docker run --rm --pull=never --network none --read-only \
    -v "$SNAPSHOT_VOLUME:/data:ro" "$ARCHIVER_IMAGE_ID" \
    sh -ec 'cat /data/.lims-oracle-snapshot-restore-complete' 2>/dev/null)" \
    || die "snapshot volume lacks a completed restore marker"
  grep -Fxq "format=$SNAPSHOT_FORMAT" <<<"$restore_marker" \
    || die "snapshot restore marker format is invalid"
  grep -Fxq "restore_nonce=$SNAPSHOT_RESTORE_NONCE" <<<"$restore_marker" \
    || die "snapshot restore marker nonce does not match its label"
  grep -Fxq "oracle_image_id=$INSTANCE_ORACLE_IMAGE_ID" <<<"$restore_marker" \
    || die "snapshot restore marker Oracle image identity does not match"
  grep -Fxq "archive_sha256=$snapshot_archive_hash" <<<"$restore_marker" \
    || die "snapshot restore marker archive hash does not match its volume label"
  grep -Fxq "archiver_image_id=$snapshot_archiver_image_id" <<<"$restore_marker" \
    || die "snapshot restore marker archiver image identity does not match its volume label"
  mounted_by="$(docker ps -a --filter "volume=$SNAPSHOT_VOLUME" --format '{{.Names}}')"
  [ -z "$mounted_by" ] || die "snapshot volume is already mounted by: $mounted_by"
}

assert_archiver_image() {
  local current_id
  docker image inspect "$ARCHIVER_IMAGE" >/dev/null 2>&1 \
    || die "required local archiver image is absent: $ARCHIVER_IMAGE"
  current_id="$(docker image inspect "$ARCHIVER_IMAGE" --format '{{.Id}}')"
  [[ "$current_id" =~ ^sha256:[0-9a-f]{64}$ ]] \
    || die "local archiver image ID is malformed: $current_id"
  if [ -n "${ARCHIVER_IMAGE_ID:-}" ] && [ "$ARCHIVER_IMAGE_ID" != "$current_id" ]; then
    die "archiver image tag changed during replay preflight"
  fi
  ARCHIVER_IMAGE_ID="$current_id"
  [ "$(docker image inspect "$ARCHIVER_IMAGE_ID" --format '{{.Id}}')" = "$ARCHIVER_IMAGE_ID" ] \
    || die "local archiver image ID cannot be resolved"
}

assert_resource_names_unused() {
  local resource
  [ "$REPLAY_NETWORK" != "lims-copy" ] || die "refusing the copy network name"
  for resource in "$REPLAY_ORACLE" "$REPLAY_REDIS" "$REPLAY_APP"; do
    ! container_exists "$resource" || die "container already exists and will not be changed: $resource"
  done
  ! network_exists "$REPLAY_NETWORK" || die "network already exists and will not be changed: $REPLAY_NETWORK"
  ! docker volume inspect "$REPLAY_ATTACHMENT_VOLUME" >/dev/null 2>&1 \
    || die "attachment volume already exists and will not be changed: $REPLAY_ATTACHMENT_VOLUME"

  [ -z "$(docker ps -aq --filter "label=$REPLAY_INSTANCE_LABEL=$INSTANCE_ID")" ] \
    || die "an existing container already uses replay instance label $INSTANCE_ID"
  [ -z "$(docker network ls -q --filter "label=$REPLAY_INSTANCE_LABEL=$INSTANCE_ID")" ] \
    || die "an existing network already uses replay instance label $INSTANCE_ID"
  [ -z "$(docker volume ls -q --filter "label=$REPLAY_INSTANCE_LABEL=$INSTANCE_ID")" ] \
    || die "an existing volume already uses replay instance label $INSTANCE_ID"

  for resource in "$INSTANCE_REPLAY_MANIFEST" "$INSTANCE_FORWARDER_PID" "$INSTANCE_FORWARDER_LOG"; do
    [ ! -e "$resource" ] || die "instance output already exists and will not be overwritten: $resource"
  done
}

assert_host_port_unused() {
  ! port_in_use || die "127.0.0.1:$INSTANCE_HOST_PORT is already listening"
  [ -z "$(docker ps -q --filter "publish=$INSTANCE_HOST_PORT")" ] \
    || die "a running Docker container already publishes host port $INSTANCE_HOST_PORT"
}

assert_mac_unused() {
  local container actual expected
  expected="$(tr '[:upper:]' '[:lower:]' <<<"$INSTANCE_MAC_ADDRESS")"
  while read -r container; do
    [ -n "$container" ] || continue
    actual="$(docker inspect "$container" --format '{{range .NetworkSettings.Networks}}{{.MacAddress}} {{end}}' 2>/dev/null || true)"
    if tr '[:upper:]' '[:lower:]' <<<"$actual" | tr ' ' '\n' | grep -Fxq "$expected"; then
      die "licensed MAC_ADDRESS is already assigned to running container $container; replay must run serially"
    fi
  done < <(docker ps --format '{{.Names}}')
}

assert_local_images() {
  local index_entry index_listing index_error=""
  docker image inspect "$INSTANCE_IMAGE_REF" >/dev/null 2>&1 \
    || die "required local application image is absent: $INSTANCE_IMAGE_REF"
  docker image inspect "$ORACLE_IMAGE" >/dev/null 2>&1 \
    || die "required local Oracle image is absent: $ORACLE_IMAGE"
  docker image inspect "$REDIS_IMAGE" >/dev/null 2>&1 \
    || die "required local Redis image is absent: $REDIS_IMAGE"
  assert_archiver_image

  APP_IMAGE_ID="$(docker image inspect "$INSTANCE_IMAGE_REF" --format '{{.Id}}')"
  [ "$APP_IMAGE_ID" = "$INSTANCE_IMAGE_ID" ] \
    || die "local application image ID does not match the approved per-instance IMAGE_ID"
  ORACLE_IMAGE_ID="$(docker image inspect "$ORACLE_IMAGE" --format '{{.Id}}')"
  REDIS_IMAGE_ID="$(docker image inspect "$REDIS_IMAGE" --format '{{.Id}}')"
  [ "$ORACLE_IMAGE_ID" = "$INSTANCE_ORACLE_IMAGE_ID" ] \
    || die "local Oracle image ID does not match the approved per-instance ORACLE_IMAGE_ID"
  [ "$REDIS_IMAGE_ID" = "$INSTANCE_REDIS_IMAGE_ID" ] \
    || die "local Redis image ID does not match the approved per-instance REDIS_IMAGE_ID"
  for image_id in "$APP_IMAGE_ID" "$ORACLE_IMAGE_ID" "$REDIS_IMAGE_ID" "$ARCHIVER_IMAGE_ID"; do
    [ "$(docker image inspect "$image_id" --format '{{.Os}}/{{.Architecture}}')" = linux/amd64 ] \
      || die "replay requires linux/amd64 images for the fixed native runtime"
  done
  LICENSE_FILE="$INSTANCE_LICENSE_DIR/license.lic"
  VENDOR_LICENSE_FILE="$INSTANCE_LICENSE_DIR/vendor.lic"
  ATTACHMENT_ROOT="$INSTANCE_FILES_DIR"
  [ -f "$LICENSE_FILE" ] || die "missing regular replay license input: $LICENSE_FILE"
  [ -f "$VENDOR_LICENSE_FILE" ] || die "missing regular replay vendor license input: $VENDOR_LICENSE_FILE"
  [ -d "$ATTACHMENT_ROOT" ] || die "missing replay attachment directory: $ATTACHMENT_ROOT"
  [ -d "$ROOT/materials" ] && [ ! -L "$ROOT/materials" ] \
    || die "materials directory is missing or symlinked"
  [ -d "$MATERIAL_INDEX_ROOT" ] && [ ! -L "$MATERIAL_INDEX_ROOT" ] \
    || die "search index directory is missing or symlinked"
  [ -d "$MATERIAL_INDEX_ROOT" ] || die "missing replay search index directory: $MATERIAL_INDEX_ROOT"
  index_listing="$(mktemp)"
  if ! find -P "$MATERIAL_INDEX_ROOT" -print0 > "$index_listing"; then
    rm -f "$index_listing"
    die "cannot enumerate replay search index directory"
  fi
  while IFS= read -r -d '' index_entry; do
    if { [ ! -d "$index_entry" ] && [ ! -f "$index_entry" ]; } || [ -L "$index_entry" ]; then
      index_error="$index_entry"
      break
    fi
  done < "$index_listing"
  rm -f "$index_listing"
  [ -z "$index_error" ] || die "search index contains a special file or symlink: $index_error"
}

config_value() {
  local key="$1" value
  value="$(awk -v wanted="$key" '
    function property(line, key, value) {
      sub(/\r$/, "", line)
      sub(/^[ \t]*/, "", line)
      if (line == "" || line ~ /^[#!]/) return ""
      key = line
      if (line ~ /[=:]/) {
        sub(/[ \t]*[=:].*$/, "", key)
        value = line
        sub(/^[^:=]*[=:][ \t]*/, "", value)
      } else {
        sub(/[ \t].*$/, "", key)
        value = line
        sub(/^[^ \t]+[ \t]+/, "", value)
      }
      if (key == line || key == "") return ""
      return key SUBSEP value
    }
    {
      parsed = property($0)
      if (parsed != "") {
        split(parsed, fields, SUBSEP)
        if (fields[1] == wanted) {
        if (++count > 1) exit 2
          value = fields[2]
        }
      }
    }
    END { if (count != 1 || value == "") exit 2; print value }
  ' "$INSTANCE_CONFIG_FILE")" \
    || die "isolated config must contain exactly one nonempty $key"
  printf '%s\n' "$value"
}

assert_no_forbidden_profiles() {
  awk '
    function property(line, key, value) {
      sub(/\r$/, "", line)
      sub(/^[ \t]*/, "", line)
      if (line == "" || line ~ /^[#!]/) return ""
      key = line
      if (line ~ /[=:]/) {
        sub(/[ \t]*[=:].*$/, "", key)
        value = line
        sub(/^[^:=]*[=:][ \t]*/, "", value)
      } else {
        sub(/[ \t].*$/, "", key)
        value = line
        sub(/^[^ \t]+[ \t]+/, "", value)
      }
      if (key == line || key == "") return ""
      return key SUBSEP value
    }
    {
      parsed = property($0)
      if (parsed == "") next
      split(parsed, fields, SUBSEP)
      if (fields[1] == "spring.profiles.active" || fields[1] == "spring.profiles.include" || fields[1] ~ /^spring\.profiles\.(active|include)\[/ || fields[1] ~ /^spring\.profiles\.group\./) {
        value = fields[2]
        gsub(/[[:space:]]/, "", value)
        if (value ~ /(^|,)(schedule|equipt-acq-file|ngtodo)(,|$)/) bad = 1
      }
    }
    END { exit bad }
  ' "$INSTANCE_CONFIG_FILE" || die "isolated config enables or includes a scheduler, acquisition, or business-sync profile"
}

assert_instance_isolated_config() {
  local datasource redis_url profiles active_lines
  if LC_ALL=C grep -q $'\f' "$INSTANCE_CONFIG_FILE" || LC_ALL=C grep -q $'\v' "$INSTANCE_CONFIG_FILE"; then
    die "isolated config contains unsupported control characters"
  fi
  if grep -q '\\' "$INSTANCE_CONFIG_FILE"; then
    die "isolated config uses Java-properties escaping/continuation; reject it instead of guessing endpoint or profile semantics"
  fi
  datasource="$(config_value spring.datasource.url)"
  [ "$datasource" = 'jdbc:oracle:thin:@oracle:1521/XEPDB1' ] \
    || die "isolated config datasource is not the replay oracle alias"
  redis_url="$(config_value spring.redis.url)"
  [ "$redis_url" = "redis://:${INSTANCE_REDIS_PASSWORD}@redis:6379" ] \
    || die "isolated config Redis URL does not match the per-instance password/alias"
  profiles="$(config_value spring.profiles.active | tr -d '[:space:]')"
  [ -n "$profiles" ] || die "isolated config has an empty active profile set"
  assert_no_forbidden_profiles
  [ "$(config_value sunway.security.file-access-permission.enabled)" = true ] \
    || die "isolated config must keep signed file access enabled"
  [ "$(config_value sunway.api.check-access-permission)" = false ] \
    || die "isolated config must use the reviewed local query access guard setting"
  active_lines="$(grep -vE '^[[:space:]]*#|^[[:space:]]*$|^[[:space:]]*spring\.mail\.(username|password)[[:space:]]*[=:]' "$INSTANCE_CONFIG_FILE")"
  if grep -qE '10\.100\.|218\.93\.|192\.168\.|58\.56\.|czjky\.com|exmail\.qq\.com|lims\.sunwayworld\.com' <<<"$active_lines"; then
    die "isolated config still contains a production or external endpoint"
  fi
}

assert_licensed_mac() {
  local file="$ROOT/private/license-machine.env" expected
  [ -f "$file" ] && [ ! -L "$file" ] \
    || die "missing restricted license MAC approval file: $file"
  [ "$(stat -c '%a' "$file")" = 600 ] && [ "$(stat -c '%u' "$file")" = "$(id -u)" ] \
    || die "license MAC approval file is not mode 0600/current-user owned"
  expected="$(awk -F= '
    /^[[:space:]]*#/ || /^[[:space:]]*$/ { next }
    $1 == "MAC_ADDRESS" { if (++count > 1) exit 2; value = substr($0, index($0, "=") + 1) }
    END { if (count != 1) exit 2; print value }
  ' "$file")" || die "license MAC approval file has an invalid schema"
  [ "$(tr '[:upper:]' '[:lower:]' <<<"$expected")" = "$(tr '[:upper:]' '[:lower:]' <<<"$INSTANCE_MAC_ADDRESS")" ] \
    || die "instance MAC_ADDRESS is not the approved licensed MAC"
}

assert_replay_subnet_unused() {
  local network subnet networks
  networks="$(docker network ls -q)" || die "cannot list Docker networks for replay subnet preflight"
  while IFS= read -r network; do
    [ -n "$network" ] || continue
    subnet="$(docker network inspect "$network" --format '{{range .IPAM.Config}}{{.Subnet}} {{end}}')" \
      || die "cannot inspect Docker network during replay subnet preflight: $network"
    while IFS= read -r subnet; do
      [[ "$subnet" =~ ^[0-9]{1,3}(\.[0-9]{1,3}){3}/[0-9]{1,2}$ ]] || continue
      if python3 - "$REPLAY_SUBNET" "$subnet" <<'PY'
import ipaddress
import sys
try:
    left = ipaddress.ip_network(sys.argv[1], strict=False)
    right = ipaddress.ip_network(sys.argv[2], strict=False)
except ValueError:
    sys.exit(2)
sys.exit(0 if left.overlaps(right) else 1)
PY
      then
        die "replay subnet $REPLAY_SUBNET overlaps Docker network $network ($subnet); choose another instance id"
      fi
    done < <(tr ' ' '\n' <<<"$subnet")
  done <<< "$networks"
}

assert_source_identity_file() {
  local invalid resolved_private resolved_file
  [ -f "$SOURCE_IDENTITY_FILE" ] && [ ! -L "$SOURCE_IDENTITY_FILE" ] \
    || die "source tree has no Git metadata; provide restricted source identity: $SOURCE_IDENTITY_FILE"
  resolved_private="$(realpath -m "$ROOT/private")"
  resolved_file="$(realpath "$SOURCE_IDENTITY_FILE")" || die "cannot resolve source identity file"
  case "$resolved_file" in
    "$resolved_private"/*) ;;
    *) die "source identity file must be below private/: $SOURCE_IDENTITY_FILE" ;;
  esac
  [ "$(stat -c '%a' "$SOURCE_IDENTITY_FILE")" = 600 ] \
    || die "source identity file must have mode 0600: $SOURCE_IDENTITY_FILE"
  [ "$(stat -c '%u' "$SOURCE_IDENTITY_FILE")" = "$(id -u)" ] \
    || die "source identity file owner does not match invoking user"
  invalid="$(awk -F= '
    /^[[:space:]]*#/ || /^[[:space:]]*$/ { next }
    $1 !~ /^(ROOT_SOURCE_ID|PROJECT_SOURCE_ID)$/ || length($2) != 71 || substr($2, 1, 7) != "sha256:" || substr($2, 8) !~ /^[0-9a-f]+$/ || ++seen[$1] > 1 { bad = 1 }
    END { if (bad || !seen["ROOT_SOURCE_ID"] || !seen["PROJECT_SOURCE_ID"]) exit 1 }
  ' "$SOURCE_IDENTITY_FILE" 2>&1)" || die "source identity file has an invalid schema"
}

read_source_identity() {
  local wanted="$1" value
  value="$(awk -F= -v wanted="$wanted" '
    /^[[:space:]]*#/ || /^[[:space:]]*$/ { next }
    $1 == wanted { if (++count > 1) exit 2; print substr($0, index($0, "=") + 1) }
    END { if (count != 1) exit 2 }
  ' "$SOURCE_IDENTITY_FILE")" \
    || die "source identity file is missing exactly one $wanted"
  [[ "$value" =~ ^sha256:[0-9a-f]{64}$ ]] \
    || die "source identity value is malformed: $wanted"
  printf '%s\n' "$value"
}

collect_tracking_inputs() {
  local root_toplevel project_toplevel
  root_toplevel="$(git -C "$ROOT" rev-parse --show-toplevel 2>/dev/null || true)"
  project_toplevel="$(git -C "$ROOT/project" rev-parse --show-toplevel 2>/dev/null || true)"
  if [ "$root_toplevel" = "$ROOT" ] && [ "$project_toplevel" = "$ROOT/project" ]; then
    WORKSPACE_GIT_HEAD="$(git -C "$ROOT" rev-parse HEAD)"
    PROJECT_GIT_HEAD="$(git -C "$ROOT/project" rev-parse HEAD)"
    if [ -n "$(git -C "$ROOT" status --porcelain)" ]; then
      WORKSPACE_GIT_DIRTY=true
    else
      WORKSPACE_GIT_DIRTY=false
    fi
    if [ -n "$(git -C "$ROOT/project" status --porcelain)" ]; then
      PROJECT_GIT_DIRTY=true
    else
      PROJECT_GIT_DIRTY=false
    fi
    SOURCE_IDENTITY_KIND=git
  else
    assert_source_identity_file
    WORKSPACE_GIT_HEAD="archive:$(read_source_identity ROOT_SOURCE_ID)"
    PROJECT_GIT_HEAD="archive:$(read_source_identity PROJECT_SOURCE_ID)"
    WORKSPACE_GIT_DIRTY=unknown
    PROJECT_GIT_DIRTY=unknown
    SOURCE_IDENTITY_KIND=restricted-archive-manifest
  fi
  STACK_SCRIPT_SHA256="$(sha256sum "$ROOT/scripts/stack-demo-restore.sh" | awk '{print $1}')"
  ATTACHMENT_STAGE_SCRIPT_SHA256="$(sha256sum "$ROOT/scripts/stage-attachments.sh" | awk '{print $1}')"
  INSTANCE_ENV_SCRIPT_SHA256="$(sha256sum "$ROOT/scripts/instance-env.sh" | awk '{print $1}')"
  FORWARDER_SCRIPT_SHA256="$(sha256sum "$ROOT/tools/portfwd.py" | awk '{print $1}')"
  INSTANCE_CONFIG_SHA256="$(sha256sum "$INSTANCE_CONFIG_FILE" | awk '{print $1}')"
  SOURCE_IDENTITY_SHA256="$(sha256sum "$SOURCE_IDENTITY_FILE" 2>/dev/null | awk '{print $1}' || true)"
  ORACLE_SNAPSHOT_SCRIPT_SHA256="$(sha256sum "$ORACLE_SNAPSHOT_SCRIPT" | awk '{print $1}')"
  ISOLATION_SCRIPT_SHA256="$(sha256sum "$ISOLATION_SCRIPT" | awk '{print $1}')"
  DOCKER_SERVER_VERSION="$(docker version --format '{{.Server.Version}}')"
}

write_manifest() {
  # noclobber reserves the record before Docker changes state. A failed start
  # therefore remains inspectable instead of being silently retried in place.
  if ! (set -o noclobber; : > "$INSTANCE_REPLAY_MANIFEST") 2>/dev/null; then
    die "cannot reserve replay manifest without overwriting it: $INSTANCE_REPLAY_MANIFEST"
  fi
  chmod 600 "$INSTANCE_REPLAY_MANIFEST"
  {
    printf 'format=lims-offline-replay-v1\n'
    printf 'start_attempt_utc=%s\n' "$(date -u +%Y-%m-%dT%H:%M:%SZ)"
    printf 'instance_id=%s\n' "$INSTANCE_ID"
    printf 'runtime_env=%s\n' "$INSTANCE_RUNTIME_ENV"
    printf 'config_file=%s\n' "$INSTANCE_CONFIG_FILE"
    printf 'config_sha256=%s\n' "$INSTANCE_CONFIG_SHA256"
    printf 'host_binding=127.0.0.1:%s\n' "$INSTANCE_HOST_PORT"
    printf 'network=%s\n' "$REPLAY_NETWORK"
    printf 'oracle_container=%s\n' "$REPLAY_ORACLE"
    printf 'redis_container=%s\n' "$REPLAY_REDIS"
    printf 'app_container=%s\n' "$REPLAY_APP"
    printf 'snapshot_volume=%s\n' "$SNAPSHOT_VOLUME"
    printf 'snapshot_format=%s\n' "$SNAPSHOT_FORMAT"
    printf 'snapshot_restore_nonce=%s\n' "$SNAPSHOT_RESTORE_NONCE"
    printf 'snapshot_archive_sha256=%s\n' "$SNAPSHOT_ARCHIVE_SHA256"
    printf 'snapshot_oracle_image_id=%s\n' "$SNAPSHOT_ORACLE_IMAGE_ID"
    printf 'snapshot_archiver_image_id=%s\n' "$SNAPSHOT_ARCHIVER_IMAGE_ID"
    printf 'snapshot_lease_file=%s\n' "$SNAPSHOT_LEASE_FILE"
    printf 'app_image_ref=%s\n' "$INSTANCE_IMAGE_REF"
    printf 'app_image_expected_id=%s\n' "$INSTANCE_IMAGE_ID"
    printf 'app_image_id=%s\n' "$APP_IMAGE_ID"
    printf 'oracle_image_ref=%s\n' "$ORACLE_IMAGE"
    printf 'oracle_image_id=%s\n' "$ORACLE_IMAGE_ID"
    printf 'redis_image_ref=%s\n' "$REDIS_IMAGE"
    printf 'redis_image_id=%s\n' "$REDIS_IMAGE_ID"
    printf 'archiver_image_ref=%s\n' "$ARCHIVER_IMAGE"
    printf 'archiver_image_id=%s\n' "$ARCHIVER_IMAGE_ID"
    printf 'network_internal=true\n'
    printf 'image_pull=never\n'
    printf 'workspace_git_head=%s\n' "$WORKSPACE_GIT_HEAD"
    printf 'workspace_git_dirty=%s\n' "$WORKSPACE_GIT_DIRTY"
    printf 'project_git_head=%s\n' "$PROJECT_GIT_HEAD"
    printf 'project_git_dirty=%s\n' "$PROJECT_GIT_DIRTY"
    printf 'source_identity_kind=%s\n' "$SOURCE_IDENTITY_KIND"
    [ -z "$SOURCE_IDENTITY_SHA256" ] || printf 'source_identity_sha256=%s\n' "$SOURCE_IDENTITY_SHA256"
    printf 'stack_script_sha256=%s\n' "$STACK_SCRIPT_SHA256"
    printf 'instance_env_script_sha256=%s\n' "$INSTANCE_ENV_SCRIPT_SHA256"
    printf 'forwarder_script_sha256=%s\n' "$FORWARDER_SCRIPT_SHA256"
    printf 'attachment_stage_script_sha256=%s\n' "$ATTACHMENT_STAGE_SCRIPT_SHA256"
    printf 'oracle_snapshot_script_sha256=%s\n' "$ORACLE_SNAPSHOT_SCRIPT_SHA256"
    printf 'isolation_script_sha256=%s\n' "$ISOLATION_SCRIPT_SHA256"
    printf 'attachment_volume=%s\n' "$REPLAY_ATTACHMENT_VOLUME"
    printf 'attachment_mount_mode=isolated-writable-volume;host-source-read-only\n'
    printf 'docker_server_version=%s\n' "$DOCKER_SERVER_VERSION"
    printf 'scope=one-new-offline-replay-instance; existing-lims-resources-unchanged\n'
  } > "$INSTANCE_REPLAY_MANIFEST"
}

append_manifest() {
  local key="$1" value="$2"
  [[ "$key" =~ ^[a-z0-9_]+$ && "$value" =~ ^[A-Za-z0-9._:/+-]+$ ]] \
    || die "refusing an unsafe replay manifest value for $key"
  printf '%s=%s\n' "$key" "$value" >> "$INSTANCE_REPLAY_MANIFEST"
}

assert_network() {
  [ "$(docker network inspect "$REPLAY_NETWORK" --format '{{.Internal}}')" = "true" ] \
    || die "replay network is not internal: $REPLAY_NETWORK"
  [ "$(docker network inspect "$REPLAY_NETWORK" --format '{{index .Labels "com.sunway.lims.offline-replay"}}')" = "$REPLAY_LABEL_VALUE" ] \
    || die "replay network lacks its tracking label"
  [ "$(docker network inspect "$REPLAY_NETWORK" --format '{{index .Labels "com.sunway.lims.offline-replay.instance"}}')" = "$INSTANCE_ID" ] \
    || die "replay network has an unexpected instance label"
  [ "$(docker network inspect "$REPLAY_NETWORK" --format '{{range .IPAM.Config}}{{.Subnet}}{{end}}')" = "$REPLAY_SUBNET" ] \
    || die "replay network has an unexpected subnet"
}

assert_container() {
  local name="$1" component="$2" expected_image_id="$3" networks network_mode port_bindings extra_hosts
  [ "$(docker inspect "$name" --format '{{.State.Running}}')" = "true" ] \
    || die "replay container is not running: $name"
  [ "$(docker inspect "$name" --format '{{.Image}}')" = "$expected_image_id" ] \
    || die "$name does not use the locally preflighted image ID"
  networks="$(docker inspect "$name" --format '{{range $k,$v := .NetworkSettings.Networks}}{{$k}} {{end}}')"
  [ "$networks" = "$REPLAY_NETWORK " ] || die "$name is attached to unexpected network(s): ${networks:-none}"
  network_mode="$(docker inspect "$name" --format '{{.HostConfig.NetworkMode}}')"
  [ "$network_mode" = "$REPLAY_NETWORK" ] || die "$name has an unexpected network mode: $network_mode"
  port_bindings="$(docker inspect "$name" --format '{{json .HostConfig.PortBindings}}')"
  case "$port_bindings" in null|{}) ;; *) die "$name has published ports" ;; esac
  extra_hosts="$(docker inspect "$name" --format '{{json .HostConfig.ExtraHosts}}')"
  case "$extra_hosts" in null|[]) ;; *) die "$name has extra host mappings" ;; esac
  [ "$(docker inspect "$name" --format '{{index .Config.Labels "com.sunway.lims.offline-replay"}}')" = "$REPLAY_LABEL_VALUE" ] \
    || die "$name lacks its replay tracking label"
  [ "$(docker inspect "$name" --format '{{index .Config.Labels "com.sunway.lims.offline-replay.instance"}}')" = "$INSTANCE_ID" ] \
    || die "$name has an unexpected replay instance label"
  [ "$(docker inspect "$name" --format '{{index .Config.Labels "com.sunway.lims.offline-replay.component"}}')" = "$component" ] \
    || die "$name has an unexpected replay component label"
}

wait_for_oracle() {
  local attempt
  for ((attempt = 1; attempt <= 120; attempt++)); do
    assert_container "$REPLAY_ORACLE" oracle "$ORACLE_IMAGE_ID"
    if docker exec "$REPLAY_ORACLE" healthcheck.sh >/dev/null 2>&1; then
      echo "[stack-demo-restore] Oracle is ready"
      return 0
    fi
    sleep 5
  done
  docker logs --tail 50 "$REPLAY_ORACLE" >&2 || true
  die "Oracle did not become ready within 10 minutes"
}

wait_for_redis() {
  local attempt
  for ((attempt = 1; attempt <= 30; attempt++)); do
    assert_container "$REPLAY_REDIS" redis "$REDIS_IMAGE_ID"
    if docker exec "$REPLAY_REDIS" redis-cli --no-auth-warning -a "$INSTANCE_REDIS_PASSWORD" ping 2>/dev/null | grep -qx PONG; then
      echo "[stack-demo-restore] Redis is ready"
      return 0
    fi
    sleep 1
  done
  docker logs --tail 50 "$REPLAY_REDIS" >&2 || true
  die "Redis did not become ready within 30 seconds"
}

wait_for_app() {
  local attempt
  for ((attempt = 1; attempt <= 120; attempt++)); do
    assert_container "$REPLAY_APP" app "$APP_IMAGE_ID"
    if docker logs --tail 200 "$REPLAY_APP" 2>&1 | grep -q 'Started GikamWpjcApplication' \
        || docker exec "$REPLAY_APP" sh -c \
          'grep -q "Started GikamWpjcApplication" /data/LIMS/jiankeyuan/lims-czjky-emc/logs/app.log 2>/dev/null'; then
      echo "[stack-demo-restore] application startup marker found"
      return 0
    fi
    sleep 2
  done
  docker logs --tail 100 "$REPLAY_APP" >&2 || true
  die "application did not emit its startup marker within 4 minutes"
}

verify_no_database_links() {
  local output
  if ! output="$(docker exec -i "$REPLAY_ORACLE" bash -c '
    export ORACLE_HOME=/opt/oracle/product/21c/dbhomeXE
    export PATH="$ORACLE_HOME/bin:$PATH"
    sqlplus -s / as sysdba
  ' <<'SQL'
whenever oserror exit 1
whenever sqlerror exit sql.sqlcode
set heading off feedback off pages 0 verify off echo off
select 'REPLAY_DB_LINK_COUNT=' || count(*) from cdb_db_links;
exit
SQL
)"; then
    die "local database-link metadata check failed before application startup"
  fi
  [[ "$output" == *"REPLAY_DB_LINK_COUNT=0"* ]] \
    || die "restored Oracle still has database links; refusing application startup"
  append_manifest database_link_count 0
  echo "[stack-demo-restore] local database-link metadata check passed"
}

assert_app_mac_address() {
  local actual expected
  actual="$(docker inspect "$REPLAY_APP" --format "{{(index .NetworkSettings.Networks \"$REPLAY_NETWORK\").MacAddress}}")"
  expected="$(tr '[:upper:]' '[:lower:]' <<<"$INSTANCE_MAC_ADDRESS")"
  actual="$(tr '[:upper:]' '[:lower:]' <<<"$actual")"
  [ "$actual" = "$expected" ] || die "application container MAC address differs from the restricted runtime input"
}

assert_app_config_mount() {
  local mount
  mount="$(docker inspect "$REPLAY_APP" --format '{{range .Mounts}}{{if eq .Destination "/data/LIMS/jiankeyuan/lims-czjky-emc/application.properties"}}{{.Source}} {{.RW}}{{end}}{{end}}')"
  [ "$mount" = "$INSTANCE_CONFIG_FILE false" ] \
    || die "application does not have the expected read-only per-instance config mount"
}

assert_app_restricted_mounts() {
  local mounts attachment_mount
  mounts="$(docker inspect "$REPLAY_APP" --format '{{range .Mounts}}{{.Source}}|{{.Destination}}|{{.RW}}{{"\n"}}{{end}}')"
  printf '%s\n' "$mounts" | grep -Fxq "$LICENSE_FILE|$APP_ROOT_PATH/lic/license.lic|false" \
    || die "application does not have the expected read-only license mount"
  printf '%s\n' "$mounts" | grep -Fxq "$VENDOR_LICENSE_FILE|$APP_ROOT_PATH/lic/vendor.lic|false" \
    || die "application does not have the expected read-only vendor license mount"
  attachment_mount="$(docker inspect "$REPLAY_APP" --format '{{range .Mounts}}{{if eq .Destination "'$APP_ROOT_PATH'/files"}}{{.Type}}|{{.Name}}|{{.RW}}{{end}}{{end}}')"
  [ "$attachment_mount" = "volume|$REPLAY_ATTACHMENT_VOLUME|true" ] \
    || die "application does not have the expected isolated attachment volume mount: ${attachment_mount:-none}"
  ! printf '%s\n' "$mounts" | grep -Fq "$ATTACHMENT_ROOT|" \
    || die "application must not mount the host attachment source directly"
  printf '%s\n' "$mounts" | grep -Fxq "$MATERIAL_INDEX_ROOT|$APP_ROOT_PATH/index|false" \
    || die "application does not have the expected read-only search-index mount"
}

forwarder_matches() {
  local pid="$1" target_ip="$2" command
  [ -r "/proc/$pid/cmdline" ] || return 1
  command="$(tr '\0' ' ' < "/proc/$pid/cmdline")"
  [[ "$command" == *"$ROOT/tools/portfwd.py $INSTANCE_HOST_PORT $target_ip $APP_PORT"* ]]
}

start_forwarder() {
  local target_ip pid attempt
  target_ip="$(docker inspect "$REPLAY_APP" --format "{{(index .NetworkSettings.Networks \"$REPLAY_NETWORK\").IPAddress}}")"
  [[ "$target_ip" =~ ^[0-9]{1,3}(\.[0-9]{1,3}){3}$ ]] \
    || die "application has no IPv4 address on $REPLAY_NETWORK"
  ! port_in_use || die "host port became busy before replay forwarding started: $INSTANCE_HOST_PORT"

  if command -v setsid >/dev/null 2>&1; then
    (
      eval "exec ${REPLAY_LOCK_FD}>&-"
      exec nohup setsid python3 "$ROOT/tools/portfwd.py" "$INSTANCE_HOST_PORT" "$target_ip" "$APP_PORT" \
        </dev/null >> "$INSTANCE_FORWARDER_LOG" 2>&1
    ) &
  else
    (
      eval "exec ${REPLAY_LOCK_FD}>&-"
      exec nohup python3 "$ROOT/tools/portfwd.py" "$INSTANCE_HOST_PORT" "$target_ip" "$APP_PORT" \
        </dev/null >> "$INSTANCE_FORWARDER_LOG" 2>&1
    ) &
  fi
  pid=$!
  printf '%s\n' "$pid" > "$INSTANCE_FORWARDER_PID"

  for ((attempt = 1; attempt <= 20; attempt++)); do
    if kill -0 "$pid" 2>/dev/null && forwarder_matches "$pid" "$target_ip" && port_in_use; then
      FORWARDER_PID="$pid"
      echo "[stack-demo-restore] forwarding 127.0.0.1:$INSTANCE_HOST_PORT to $REPLAY_APP:$APP_PORT"
      return 0
    fi
    sleep 0.1
  done

  tail -20 "$INSTANCE_FORWARDER_LOG" >&2 || true
  die "replay forwarder did not bind its requested localhost port"
}

main() {
  [ "$#" = 2 ] || usage
  need_command docker
  need_command python3
  need_command ss
  need_command stat
  need_command sha256sum
  need_command flock

  acquire_replay_launch_lock
  instance_env_load "$1"
  instance_env_replay_subnet
  assert_instance_isolated_config
  assert_licensed_mac
  SNAPSHOT_VOLUME="$2"
  assert_snapshot_volume
  reserve_snapshot_lease
  assert_resource_names_unused
  assert_host_port_unused
  assert_replay_subnet_unused
  assert_mac_unused
  assert_local_images
  collect_tracking_inputs
  write_manifest

  docker network create --internal \
    --subnet "$REPLAY_SUBNET" \
    --label "$REPLAY_LABEL_KEY=$REPLAY_LABEL_VALUE" \
    --label "$REPLAY_INSTANCE_LABEL=$INSTANCE_ID" \
    --label "$REPLAY_COMPONENT_LABEL=network" \
    "$REPLAY_NETWORK" >/dev/null
  assert_network
  append_manifest network_id "$(docker network inspect "$REPLAY_NETWORK" --format '{{.Id}}')"
  append_manifest network_subnet "$REPLAY_SUBNET"

  docker volume create \
    --label "$REPLAY_LABEL_KEY=$REPLAY_LABEL_VALUE" \
    --label "$REPLAY_INSTANCE_LABEL=$INSTANCE_ID" \
    --label "$REPLAY_COMPONENT_LABEL=attachments" \
    "$REPLAY_ATTACHMENT_VOLUME" >/dev/null
  [ "$(docker volume inspect "$REPLAY_ATTACHMENT_VOLUME" --format '{{index .Labels "com.sunway.lims.offline-replay"}}')" = "$REPLAY_LABEL_VALUE" ] \
    || die "attachment volume lacks replay tracking label"
  [ "$(docker volume inspect "$REPLAY_ATTACHMENT_VOLUME" --format '{{index .Labels "com.sunway.lims.offline-replay.instance"}}')" = "$INSTANCE_ID" ] \
    || die "attachment volume has an unexpected instance label"
  append_manifest attachment_volume_name "$(docker volume inspect "$REPLAY_ATTACHMENT_VOLUME" --format '{{.Name}}')"
  stage_output="$(bash "$ROOT/scripts/stage-attachments.sh" "$REPLAY_ATTACHMENT_VOLUME" "$ATTACHMENT_ROOT" "$APP_IMAGE_ID")"
  printf '%s\n' "$stage_output"
  if [[ "$stage_output" =~ ^\[stage-attachments\]\ volume=[a-z0-9_.-]+\ files=([0-9]+)\ bytes=([0-9]+)\ tree_sha256=([0-9a-f]{64})$ ]]; then
    stage_files="${BASH_REMATCH[1]}"
    stage_bytes="${BASH_REMATCH[2]}"
    stage_tree_sha256="${BASH_REMATCH[3]}"
    append_manifest attachment_source_files "$stage_files"
    append_manifest attachment_source_bytes "$stage_bytes"
    append_manifest attachment_source_tree_sha256 "$stage_tree_sha256"
  else
    die "attachment staging receipt has an unexpected format"
  fi

  docker run -d --pull=never --name "$REPLAY_ORACLE" \
    --network "$REPLAY_NETWORK" --network-alias oracle \
    --label "$REPLAY_LABEL_KEY=$REPLAY_LABEL_VALUE" \
    --label "$REPLAY_INSTANCE_LABEL=$INSTANCE_ID" \
    --label "$REPLAY_COMPONENT_LABEL=oracle" \
    --label "com.sunway.lims.offline-replay.snapshot-volume=$SNAPSHOT_VOLUME" \
    -e ORACLE_PASSWORD="$INSTANCE_ORACLE_SYS_PASSWORD" \
    -e ORACLE_MEMORY=2048 \
    -v "$SNAPSHOT_VOLUME:/opt/oracle/oradata" \
    "$ORACLE_IMAGE_ID" >/dev/null
  assert_container "$REPLAY_ORACLE" oracle "$ORACLE_IMAGE_ID"
  append_manifest oracle_container_id "$(docker inspect "$REPLAY_ORACLE" --format '{{.Id}}')"
  wait_for_oracle
  verify_no_database_links

  docker run -d --pull=never --name "$REPLAY_REDIS" \
    --network "$REPLAY_NETWORK" --network-alias redis \
    --label "$REPLAY_LABEL_KEY=$REPLAY_LABEL_VALUE" \
    --label "$REPLAY_INSTANCE_LABEL=$INSTANCE_ID" \
    --label "$REPLAY_COMPONENT_LABEL=redis" \
    --tmpfs /data \
    "$REDIS_IMAGE_ID" redis-server --requirepass "$INSTANCE_REDIS_PASSWORD" --save '' >/dev/null
  assert_container "$REPLAY_REDIS" redis "$REDIS_IMAGE_ID"
  append_manifest redis_container_id "$(docker inspect "$REPLAY_REDIS" --format '{{.Id}}')"
  wait_for_redis

  docker run -d --pull=never --name "$REPLAY_APP" \
    --network "$REPLAY_NETWORK" --network-alias app \
    --mac-address "$INSTANCE_MAC_ADDRESS" \
    --label "$REPLAY_LABEL_KEY=$REPLAY_LABEL_VALUE" \
    --label "$REPLAY_INSTANCE_LABEL=$INSTANCE_ID" \
    --label "$REPLAY_COMPONENT_LABEL=app" \
    -v "$INSTANCE_CONFIG_FILE:/data/LIMS/jiankeyuan/lims-czjky-emc/application.properties:ro" \
    -v "$LICENSE_FILE:$APP_ROOT_PATH/lic/license.lic:ro" \
    -v "$VENDOR_LICENSE_FILE:$APP_ROOT_PATH/lic/vendor.lic:ro" \
    -v "$REPLAY_ATTACHMENT_VOLUME:$APP_ROOT_PATH/files:rw" \
    -v "$MATERIAL_INDEX_ROOT:$APP_ROOT_PATH/index:ro" \
    "$APP_IMAGE_ID" >/dev/null
  assert_container "$REPLAY_APP" app "$APP_IMAGE_ID"
  assert_app_mac_address
  assert_app_config_mount
  assert_app_restricted_mounts
  append_manifest app_container_id "$(docker inspect "$REPLAY_APP" --format '{{.Id}}')"
  wait_for_app
  start_forwarder
  append_manifest forwarder_pid "$FORWARDER_PID"
  append_manifest startup_result ready

  echo "[stack-demo-restore] replay instance $INSTANCE_ID is running at http://127.0.0.1:$INSTANCE_HOST_PORT/"
  echo "[stack-demo-restore] manifest: $INSTANCE_REPLAY_MANIFEST"
  echo "[stack-demo-restore] This start is not AC-10 evidence until independently exercised and recorded."
}

if [[ "${BASH_SOURCE[0]}" == "$0" ]]; then
  main "$@"
fi
