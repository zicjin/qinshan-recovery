#!/usr/bin/env bash
# Copy a validated, host-read-only attachment tree into an isolated Docker
# volume. The application must receive the volume read/write: the private
# framework creates/updates file-side metadata while resolving attachments.
# The source itself is never mounted at the application's /files path.
set -euo pipefail
umask 077
export LC_ALL=C

die() {
  echo "[stage-attachments] ERROR: $*" >&2
  exit 1
}

assert_plain_local_volume() {
  local options
  docker volume inspect "$VOLUME" >/dev/null 2>&1 || die "missing Docker volume: $VOLUME"
  [ "$(docker volume inspect "$VOLUME" --format '{{.Driver}}')" = local ] \
    || die "attachment volume is not local: $VOLUME"
  options="$(docker volume inspect "$VOLUME" --format '{{json .Options}}')"
  case "$options" in
    null|{}) ;;
    *) die "attachment volume has local-driver bind/options and is not an isolated Docker volume: $VOLUME" ;;
  esac
}

validate_source() {
  SOURCE="$1"
  [[ "$SOURCE" = /* ]] || die "attachment source must be an absolute path"
  [ -d "$SOURCE" ] && [ ! -L "$SOURCE" ] || die "attachment source is not a regular directory: $SOURCE"
  SOURCE="$(cd "$SOURCE" && pwd -P)"
  local unexpected
  unexpected="$(find -P "$SOURCE" ! \( -type f -o -type d \) -print -quit)" \
    || die "unable to inspect attachment source tree: $SOURCE"
  [ -z "$unexpected" ] || die "source tree contains a special file: $unexpected"
}

source_receipt() {
  SOURCE_FILES="$(find -P "$SOURCE" -type f -printf . | wc -c)"
  SOURCE_BYTES="$(find -P "$SOURCE" -type f -printf '%s\n' | awk '{sum += $1} END {print sum + 0}')"
  SOURCE_TREE_SHA256="$(
    cd "$SOURCE"
    find -P . -type f -print0 | LC_ALL=C sort -z | xargs -r -0 sha256sum | sha256sum | awk '{print $1}'
  )"
}

if [ "$#" = 2 ] && [ "$1" = --source-receipt ]; then
  validate_source "$2"
  source_receipt
  printf 'files=%s\nbytes=%s\ntree_sha256=%s\n' \
    "$SOURCE_FILES" "$SOURCE_BYTES" "$SOURCE_TREE_SHA256"
  exit 0
fi

if [ "$#" = 4 ] && [ "$1" = --verify-baseline ]; then
  VOLUME="$2"
  IMAGE_ID="$4"
  STAGE_UID_GID="$(id -u):$(id -g)"
  [[ "$VOLUME" =~ ^[a-z][a-z0-9_.-]{0,127}$ ]] || die "unsafe volume name: $VOLUME"
  [[ "$IMAGE_ID" =~ ^sha256:[0-9a-f]{64}$ ]] || die "unsafe local image ID"
  validate_source "$3"
  assert_plain_local_volume
  docker run --rm --pull=never --network none --read-only \
    --tmpfs /tmp:rw,nosuid,nodev,noexec,size=16m \
    -v "$SOURCE:/input:ro" -v "$VOLUME:/output:ro" --entrypoint bash "$IMAGE_ID" -c '
      set -euo pipefail
      unexpected="$(find -P /output -mindepth 1 ! \( -type f -o -type d \) -print -quit)"
      [ -z "$unexpected" ] || { echo "volume contains special entry: $unexpected" >&2; exit 1; }
      manifest() {
        local root="$1"
        (
          cd "$root"
          find -P . -type f -print0 | LC_ALL=C sort -z | xargs -r -0 sha256sum | sha256sum | awk "{print \$1}"
        )
      }
      directories() {
        local root="$1"
        (
          cd "$root"
          find -P . -type d -print0 | LC_ALL=C sort -z | sha256sum | awk "{print \$1}"
        )
      }
      [ "$(manifest /input)" = "$(manifest /output)" ] \
        || { echo "baseline file tree differs" >&2; exit 1; }
      [ "$(directories /input)" = "$(directories /output)" ] \
        || { echo "baseline directory tree differs" >&2; exit 1; }
    '
  exit 0
fi

[ "$#" = 3 ] || die "usage: $0 <volume> <source-tree> <local-image-id> | --source-receipt <source-tree>"
VOLUME="$1"
IMAGE_ID="$3"
STAGE_UID_GID="$(id -u):$(id -g)"

[[ "$VOLUME" =~ ^[a-z][a-z0-9_.-]{0,127}$ ]] || die "unsafe volume name: $VOLUME"
[[ "$IMAGE_ID" =~ ^sha256:[0-9a-f]{64}$ ]] || die "unsafe local image ID"
validate_source "$2"
source_receipt
assert_plain_local_volume

mounted="$(docker ps -aq --filter "volume=$VOLUME")"
[ -z "$mounted" ] || die "attachment volume is already referenced by a container: $mounted"

docker run --rm --pull=never --network none --read-only \
  -v "$VOLUME:/output:ro" --entrypoint sh "$IMAGE_ID" -c \
  'test -z "$(find /output -mindepth 1 -maxdepth 1 -print -quit)"' \
  || die "attachment volume is not empty; refusing to overwrite: $VOLUME"

# Docker Desktop maps container root separately from the restricted host-user
# tree. Prepare only the empty destination volume as the invoking UID, then
# copy as that UID so the source stays mode-0700/0600 throughout.
docker run --rm --pull=never --network none --read-only \
  -v "$VOLUME:/output:rw" --entrypoint sh "$IMAGE_ID" -c \
  "chown $STAGE_UID_GID /output && chmod 700 /output"

docker run --rm --pull=never --network none --read-only \
  --user "$STAGE_UID_GID" \
  --security-opt=no-new-privileges --cap-drop=ALL \
  --tmpfs /tmp:rw,nosuid,nodev,noexec,size=16m \
  -v "$SOURCE:/input:ro" \
  -v "$VOLUME:/output:rw" \
  --entrypoint sh "$IMAGE_ID" -c '
    set -eu
    cp -a /input/. /output/
  '

volume_tree_sha256="$(docker run --rm --pull=never --network none --read-only \
  --user "$STAGE_UID_GID" \
  -v "$VOLUME:/output:ro" --entrypoint sh "$IMAGE_ID" -c '
    cd /output
    find -P . -type f -print0 | LC_ALL=C sort -z | xargs -r -0 sha256sum
  ' | sha256sum | awk '{print $1}')"
[ "$SOURCE_TREE_SHA256" = "$volume_tree_sha256" ] \
  || die "copied attachment tree hash does not match source"

files="$(docker run --rm --pull=never --network none --read-only \
  --user "$STAGE_UID_GID" \
  -v "$VOLUME:/output:ro" --entrypoint sh "$IMAGE_ID" -c \
  'find /output -type f -printf . | wc -c')"
bytes="$(docker run --rm --pull=never --network none --read-only \
  --user "$STAGE_UID_GID" \
  -v "$VOLUME:/output:ro" --entrypoint sh "$IMAGE_ID" -c \
  'find /output -type f -printf "%s\\n" | awk "{sum+=\$1} END {print sum+0}"')"
[ "$SOURCE_FILES" = "$files" ] && [ "$SOURCE_BYTES" = "$bytes" ] \
  || die "copied attachment counts do not match source"
printf '[stage-attachments] volume=%s files=%s bytes=%s tree_sha256=%s\n' \
  "$VOLUME" "$SOURCE_FILES" "$SOURCE_BYTES" "$volume_tree_sha256"
