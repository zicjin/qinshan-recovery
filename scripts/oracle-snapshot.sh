#!/usr/bin/env bash
# Create and restore a local-only, cold archive of the isolated Oracle volume.
#
# Usage:
#   scripts/oracle-snapshot.sh export --after-clean-stop --database-links-removed [snapshot-name]
#   scripts/oracle-snapshot.sh verify <snapshot-dir>
#   scripts/oracle-snapshot.sh restore <snapshot-dir> <new-volume-name> --create-empty-volume
#
# This script never starts or stops an existing service container, reads no
# application config, and has no production-network code path. It may run a
# short-lived local archiver container with --network none. Export is
# deliberately refused while lims-oracle is running. Restore can only create a
# new lims-snapshot-* volume; it never mounts or changes lims-oracle-data.
set -euo pipefail
umask 077

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SNAPSHOT_ROOT="$ROOT/private/snapshots"
SOURCE_CONTAINER="lims-oracle"
SOURCE_VOLUME="lims-oracle-data"
SOURCE_MOUNT="/opt/oracle/oradata"
ISO_NETWORK="lims-iso"
ARCHIVER_IMAGE="debian:bullseye-slim"
FORMAT="lims-oracle-volume-cold-v1"
RESTORE_MARKER=".lims-oracle-snapshot-restore-complete"
SNAPSHOT_LOCK="$ROOT/private/oracle-snapshot.lock"

die() {
  echo "[oracle-snapshot] ERROR: $*" >&2
  exit 1
}

need_command() {
  command -v "$1" >/dev/null 2>&1 || die "missing required command: $1"
}

assert_secure_dir() {
  local path="$1" mode owner
  [ -d "$path" ] && [ ! -L "$path" ] || die "missing, non-directory, or symlinked restricted directory: $path"
  mode="$(stat -c '%a' "$path")" || die "cannot inspect directory permissions: $path"
  owner="$(stat -c '%u' "$path")" || die "cannot inspect directory owner: $path"
  (( (8#$mode & 0077) == 0 )) || die "restricted directory permissions are too broad: $path"
  [ "$owner" = "$(id -u)" ] || die "restricted directory owner does not match invoking user: $path"
}

assert_secure_file() {
  local path="$1" mode owner
  [ -f "$path" ] && [ ! -L "$path" ] || die "missing, non-regular, or symlinked restricted file: $path"
  mode="$(stat -c '%a' "$path")" || die "cannot inspect file permissions: $path"
  owner="$(stat -c '%u' "$path")" || die "cannot inspect file owner: $path"
  (( (8#$mode & 0077) == 0 )) || die "restricted file permissions are too broad: $path"
  [ "$owner" = "$(id -u)" ] || die "restricted file owner does not match invoking user: $path"
}

ensure_private_root() {
  [ ! -L "$ROOT/private" ] || die "private root must not be a symlink: $ROOT/private"
  mkdir -p -m 700 "$ROOT/private"
  chmod 700 "$ROOT/private"
  assert_secure_dir "$ROOT/private"
}

acquire_snapshot_lock() {
  ensure_private_root
  local lock_fd
  [ ! -L "$SNAPSHOT_LOCK" ] || die "snapshot lock must not be a symlink: $SNAPSHOT_LOCK"
  # Append-open avoids truncating an existing lock file while still allowing
  # flock to serialize independent export/restore invocations.
  exec {lock_fd}>>"$SNAPSHOT_LOCK"
  chmod 600 "$SNAPSHOT_LOCK"
  flock -n "$lock_fd" || die "another Oracle snapshot operation is in progress; retry after it finishes"
}

usage() {
  cat >&2 <<'EOF'
Usage:
  scripts/oracle-snapshot.sh export --after-clean-stop --database-links-removed [snapshot-name]
  scripts/oracle-snapshot.sh verify <snapshot-dir>
  scripts/oracle-snapshot.sh restore <snapshot-dir> <new-volume-name> --create-empty-volume

export requires lims-oracle to be cleanly stopped and disconnected from every
network except the internal lims-iso network. It also requires an operator
declaration that database links were removed after a local metadata-only audit.
The script cannot prove that declaration because the source database must be
stopped. It reads lims-oracle-data through a temporary read-only, no-network
container and writes a sensitive archive below private/snapshots/.

restore validates the archive, then creates only a new empty volume whose name
starts with lims-snapshot-. It does not start Oracle and never modifies
lims-oracle-data.
EOF
  exit 2
}

ensure_snapshot_root() {
  ensure_private_root
  [ ! -L "$SNAPSHOT_ROOT" ] || die "snapshot root must not be a symlink: $SNAPSHOT_ROOT"
  mkdir -p -m 700 "$SNAPSHOT_ROOT"
  chmod 700 "$SNAPSHOT_ROOT"
  assert_secure_dir "$SNAPSHOT_ROOT"
  SNAPSHOT_ROOT="$(cd "$SNAPSHOT_ROOT" && pwd -P)"
}

assert_snapshot_root() {
  assert_secure_dir "$ROOT/private"
  assert_secure_dir "$SNAPSHOT_ROOT"
  SNAPSHOT_ROOT="$(cd "$SNAPSHOT_ROOT" && pwd -P)"
}

snapshot_dir() {
  local candidate="$1" root
  assert_snapshot_root
  [ -d "$candidate" ] || die "snapshot directory does not exist: $candidate"
  [ ! -L "$candidate" ] || die "snapshot directory must not be a symlink: $candidate"
  [ -d "$SNAPSHOT_ROOT" ] || die "snapshot root does not exist: $SNAPSHOT_ROOT"
  candidate="$(cd "$candidate" && pwd -P)"
  root="$(cd "$SNAPSHOT_ROOT" && pwd -P)"
  case "$candidate" in
    "$root"/*)
      assert_secure_dir "$candidate"
      printf '%s\n' "$candidate" ;;
    *) die "snapshot directory must be below $root" ;;
  esac
}

container_field() {
  docker inspect "$SOURCE_CONTAINER" --format "$1"
}

assert_source_is_cold_and_isolated() {
  docker inspect "$SOURCE_CONTAINER" >/dev/null 2>&1 || die "missing source container: $SOURCE_CONTAINER"

  local state exit_code oom state_error restart_policy finished_at image source_image_id local_image_id source_volume source_options source_networks internal
  state="$(container_field '{{.State.Status}}')"
  [ "$state" = "exited" ] || die "$SOURCE_CONTAINER must be cleanly stopped before export (current state: $state)"

  exit_code="$(container_field '{{.State.ExitCode}}')"
  # gvenzl/oracle-xe commonly records 143 for a normal SIGTERM shutdown.
  # Accept it only alongside the independent cold-state, no-OOM, no-error,
  # and no-restart checks below; all other exit codes remain fail-closed.
  case "$exit_code" in
    0|143) ;;
    *) die "$SOURCE_CONTAINER did not exit cleanly (exit code: $exit_code)" ;;
  esac

  oom="$(container_field '{{.State.OOMKilled}}')"
  [ "$oom" = "false" ] || die "$SOURCE_CONTAINER was OOM-killed; do not archive an unverified shutdown"
  state_error="$(container_field '{{.State.Error}}')"
  [ -z "$state_error" ] || die "$SOURCE_CONTAINER has a Docker state error: $state_error"
  restart_policy="$(container_field '{{.HostConfig.RestartPolicy.Name}}')"
  case "$restart_policy" in
    ""|no) ;;
    *) die "$SOURCE_CONTAINER has an unexpected restart policy: $restart_policy" ;;
  esac
  finished_at="$(container_field '{{.State.FinishedAt}}')"
  [ -n "$finished_at" ] && [ "$finished_at" != "0001-01-01T00:00:00Z" ] \
    || die "$SOURCE_CONTAINER has no recorded finish time"

  image="$(container_field '{{.Config.Image}}')"
  if [ "$image" != "gvenzl/oracle-xe:21-slim" ] \
      && [[ ! "$image" =~ ^gvenzl/oracle-xe@sha256:[0-9a-f]{64}$ ]]; then
    die "unexpected source image $image; refusing to snapshot an unreviewed container"
  fi
  source_image_id="$(container_field '{{.Image}}')"
  [[ "$source_image_id" =~ ^sha256:[0-9a-f]{64}$ ]] \
    || die "source container image ID is malformed: $source_image_id"
  local_image_id="$(docker image inspect "$image" --format '{{.Id}}' 2>/dev/null || true)"
  [ -n "$local_image_id" ] && [ "$local_image_id" = "$source_image_id" ] \
    || die "source container image ID does not match its local image reference: $image"

  source_volume="$(container_field '{{range .Mounts}}{{if eq .Destination "/opt/oracle/oradata"}}{{.Name}}{{end}}{{end}}')"
  [ "$source_volume" = "$SOURCE_VOLUME" ] || die "expected $SOURCE_MOUNT to be volume $SOURCE_VOLUME, found: ${source_volume:-none}"
  [ "$(docker volume inspect "$SOURCE_VOLUME" --format '{{.Driver}}')" = "local" ] || die "$SOURCE_VOLUME is not a local Docker volume"
  source_options="$(docker volume inspect "$SOURCE_VOLUME" --format '{{json .Options}}')"
  case "$source_options" in
    null|{}) ;;
    *) die "$SOURCE_VOLUME uses local-driver options/bind/NFS configuration; refusing a non-local source" ;;
  esac
  [ -z "$(docker ps -q --filter "volume=$SOURCE_VOLUME")" ] || die "$SOURCE_VOLUME is mounted by a running container"

  source_networks="$(container_field '{{range $k,$v := .NetworkSettings.Networks}}{{$k}} {{end}}')"
  [ "$source_networks" = "$ISO_NETWORK " ] || die "$SOURCE_CONTAINER is attached to unexpected network(s): ${source_networks:-none}"
  internal="$(docker network inspect "$ISO_NETWORK" --format '{{.Internal}}')"
  [ "$internal" = "true" ] || die "$ISO_NETWORK is not an internal Docker network"
}

manifest_value() {
  local manifest="$1" key="$2" value count
  count="$(awk -F= -v key="$key" '$1 == key { count += 1 } END { print count + 0 }' "$manifest")"
  [ "$count" = "1" ] || die "manifest must contain exactly one $key entry"
  value="$(awk -F= -v key="$key" '$1 == key { print substr($0, length(key) + 2) }' "$manifest")"
  [ -n "$value" ] || die "manifest value is empty: $key"
  printf '%s\n' "$value"
}

assert_manifest_schema() {
  local manifest="$1" missing
  if ! awk -F= '
    BEGIN { split("format created_at_utc source_container source_status source_finished_at source_container_created_at source_volume source_volume_created_at source_mount source_network source_network_internal database_links oracle_image_ref oracle_image_id oracle_image_digest archiver_image_ref archiver_image_id archive_filename archive_bytes archive_sha256 scope", required, " "); for (k in required) allowed[required[k]]=1 }
    { if ($1 == "" || !allowed[$1]) bad=1; count[$1]++ }
    END { for (k in count) if (count[k] != 1) bad=1; if (bad) exit 1 }
  ' "$manifest" 2>/dev/null; then
    die "snapshot manifest contains unknown or duplicate keys"
  fi
  missing="$(for key in format created_at_utc source_container source_status source_finished_at source_container_created_at source_volume source_volume_created_at source_mount source_network source_network_internal database_links oracle_image_ref oracle_image_id oracle_image_digest archiver_image_ref archiver_image_id archive_filename archive_bytes archive_sha256 scope; do awk -F= -v k="$key" '$1==k{found=1} END{if(!found)print k}' "$manifest"; done)"
  [ -z "$missing" ] || die "snapshot manifest is missing required keys: $missing"
}

assert_safe_archive_paths() {
  local archive="$1"
  tar -tzf "$archive" | LC_ALL=C awk '
    $0 == "." || $0 == "./" { next }
    /^\.\// {
      path = substr($0, 3)
      if (path == "" || path ~ /(^|\/)\.\.($|\/)/) bad = 1
      next
    }
    { bad = 1 }
    END { exit bad }
  ' || die "archive contains paths outside its volume root"
}

assert_safe_archive_entries() {
  local archive="$1"
  tar -tvzf "$archive" | awk '
    {
      kind = substr($1, 1, 1)
      if (kind != "-" && kind != "d") bad = 1
    }
    END { exit bad }
  ' || die "archive contains a symlink, hardlink, device, FIFO, or other non-regular entry"
}

verify_snapshot() {
  local dir="$1" archive manifest expected_hash actual_hash expected_bytes actual_bytes format source_container source_status source_volume source_mount database_links archiver_image_ref archiver_image_id oracle_image_ref oracle_image_id oracle_image_digest archive_filename source_network source_network_internal scope local_archiver_image_id local_oracle_image_id local_oracle_digest
  archive="$dir/oracle-volume.tar.gz"
  manifest="$dir/manifest.txt"
  assert_secure_dir "$dir"
  assert_secure_file "$archive"
  assert_secure_file "$manifest"
  [ -z "$(find -P "$dir" -mindepth 1 -maxdepth 1 ! -name oracle-volume.tar.gz ! -name manifest.txt -print -quit)" ] \
    || die "snapshot directory contains unexpected files"
  assert_manifest_schema "$manifest"

  format="$(manifest_value "$manifest" format)"
  [ "$format" = "$FORMAT" ] || die "unsupported snapshot format: $format"
  source_container="$(manifest_value "$manifest" source_container)"
  source_status="$(manifest_value "$manifest" source_status)"
  source_volume="$(manifest_value "$manifest" source_volume)"
  source_mount="$(manifest_value "$manifest" source_mount)"
  database_links="$(manifest_value "$manifest" database_links)"
  source_network="$(manifest_value "$manifest" source_network)"
  source_network_internal="$(manifest_value "$manifest" source_network_internal)"
  oracle_image_ref="$(manifest_value "$manifest" oracle_image_ref)"
  archiver_image_id="$(manifest_value "$manifest" archiver_image_id)"
  oracle_image_id="$(manifest_value "$manifest" oracle_image_id)"
  archiver_image_ref="$(manifest_value "$manifest" archiver_image_ref)"
  oracle_image_digest="$(manifest_value "$manifest" oracle_image_digest)"
  archive_filename="$(manifest_value "$manifest" archive_filename)"
  scope="$(manifest_value "$manifest" scope)"
  [ "$source_container" = "$SOURCE_CONTAINER" ] || die "manifest source container is not $SOURCE_CONTAINER"
  [ "$source_status" = exited ] || die "manifest source status is not exited"
  [ "$source_volume" = "$SOURCE_VOLUME" ] || die "manifest source volume is not $SOURCE_VOLUME"
  [ "$source_mount" = "$SOURCE_MOUNT" ] || die "manifest source mount is not $SOURCE_MOUNT"
  [ "$database_links" = "operator-asserted-removed-after-local-metadata-audit" ] \
    || die "snapshot lacks the required local database-link audit declaration"
  [ "$source_network" = "$ISO_NETWORK" ] || die "manifest source network is not $ISO_NETWORK"
  [ "$source_network_internal" = true ] || die "manifest source network is not marked internal"
  [ "$archiver_image_ref" = "$ARCHIVER_IMAGE" ] || die "manifest archiver image reference is unexpected"
  [ "$archive_filename" = oracle-volume.tar.gz ] || die "manifest archive filename is unexpected"
  [ "$scope" = "oracle-volume-only; files, redis, application-image, configuration, and production are excluded" ] \
    || die "manifest scope is unexpected"
  [[ "$oracle_image_ref" == gvenzl/oracle-xe:21-slim || "$oracle_image_ref" =~ ^gvenzl/oracle-xe@sha256:[0-9a-f]{64}$ ]] \
    || die "manifest Oracle image reference is unexpected"
  [[ "$oracle_image_id" =~ ^sha256:[0-9a-f]{64}$ ]] || die "manifest Oracle image ID is malformed"
  [[ "$archiver_image_id" =~ ^sha256:[0-9a-f]{64}$ ]] || die "manifest archiver image ID is malformed"
  [[ "$oracle_image_digest" =~ ^gvenzl/oracle-xe@sha256:[0-9a-f]{64}$ ]] \
    || die "manifest Oracle image digest is malformed"
  local_archiver_image_id="$(docker image inspect "$ARCHIVER_IMAGE" --format '{{.Id}}' 2>/dev/null || true)"
  [ -n "$local_archiver_image_id" ] && [ "$local_archiver_image_id" = "$archiver_image_id" ] \
    || die "local archiver image does not match the snapshot manifest"
  local_oracle_image_id="$(docker image inspect "$oracle_image_ref" --format '{{.Id}}' 2>/dev/null || true)"
  [ -n "$local_oracle_image_id" ] && [ "$local_oracle_image_id" = "$oracle_image_id" ] \
    || die "local Oracle image does not match the snapshot manifest"
  local_oracle_digest="$(docker image inspect "$oracle_image_ref" --format '{{if .RepoDigests}}{{index .RepoDigests 0}}{{end}}' 2>/dev/null || true)"
  [ -n "$local_oracle_digest" ] && [ "$local_oracle_digest" = "$oracle_image_digest" ] \
    || die "local Oracle image digest does not match the snapshot manifest"

  expected_hash="$(manifest_value "$manifest" archive_sha256)"
  [[ "$expected_hash" =~ ^[0-9a-f]{64}$ ]] || die "manifest archive_sha256 is malformed"
  expected_bytes="$(manifest_value "$manifest" archive_bytes)"
  [[ "$expected_bytes" =~ ^[0-9]+$ ]] || die "manifest archive_bytes is malformed"

  gzip -t "$archive" || die "gzip integrity check failed"
  actual_hash="$(sha256sum "$archive" | awk '{print $1}')"
  [ "$actual_hash" = "$expected_hash" ] || die "archive SHA-256 does not match manifest"
  actual_bytes="$(stat -c '%s' "$archive")"
  [ "$actual_bytes" = "$expected_bytes" ] || die "archive byte count does not match manifest"
  assert_safe_archive_paths "$archive"
  assert_safe_archive_entries "$archive"

  # Restore consumes these in-memory values after this validation pass rather
  # than reopening manifest.txt for a second, independently mutable read.
  VERIFIED_ARCHIVER_IMAGE_ID="$archiver_image_id"
  VERIFIED_ORACLE_IMAGE_ID="$oracle_image_id"
  VERIFIED_ARCHIVE_SHA256="$expected_hash"

  echo "[oracle-snapshot] PASS archive=$archive sha256=$actual_hash bytes=$actual_bytes archiver_image_id=$archiver_image_id"
}

export_snapshot() {
  [ "${1:-}" = "--after-clean-stop" ] || usage
  [ "${2:-}" = "--database-links-removed" ] || usage
  shift 2
  [ "$#" -le 1 ] || usage

  local name="${1:-oracle-volume-$(date -u +%Y%m%dT%H%M%SZ)}"
  [[ "$name" =~ ^[A-Za-z0-9][A-Za-z0-9._-]*$ ]] || die "snapshot name may contain only letters, digits, dot, underscore, and hyphen"

  need_command docker
  need_command gzip
  need_command sha256sum
  need_command stat
  need_command tar
  need_command flock
  docker image inspect "$ARCHIVER_IMAGE" >/dev/null 2>&1 || die "required local archiver image is absent: $ARCHIVER_IMAGE"
  acquire_snapshot_lock
  assert_source_is_cold_and_isolated

  ensure_snapshot_root
  local dir archive manifest tmp_archive tmp_manifest archive_hash archive_bytes image_ref image_id image_digest volume_created container_created container_finished archiver_id
  dir="$SNAPSHOT_ROOT/$name"
  [ ! -e "$dir" ] || die "snapshot destination already exists: $dir"
  mkdir "$dir"
  archive="$dir/oracle-volume.tar.gz"
  manifest="$dir/manifest.txt"
  tmp_archive="$dir/.oracle-volume.tar.gz.tmp"
  tmp_manifest="$dir/.manifest.txt.tmp"
  trap 'rm -f -- "$tmp_archive" "$tmp_manifest"' EXIT

  archiver_id="$(docker image inspect "$ARCHIVER_IMAGE" --format '{{.Id}}')"
  [[ "$archiver_id" =~ ^sha256:[0-9a-f]{64}$ ]] || die "local archiver image ID is malformed: $archiver_id"
  echo "[oracle-snapshot] creating cold local archive from $SOURCE_VOLUME; no database or production connection is used"
  docker run --pull=never --rm --network none --read-only \
    -v "$SOURCE_VOLUME:/data:ro" \
    "$archiver_id" \
    tar --numeric-owner -C /data -cf - . | gzip -n > "$tmp_archive"
  gzip -t "$tmp_archive"
  assert_safe_archive_paths "$tmp_archive"
  assert_safe_archive_entries "$tmp_archive"
  # Re-check the cold boundary after the read.  This does not replace an
  # operator lock on the source daemon, but catches a source restart during
  # the archive operation before the result is published.
  assert_source_is_cold_and_isolated

  archive_hash="$(sha256sum "$tmp_archive" | awk '{print $1}')"
  archive_bytes="$(stat -c '%s' "$tmp_archive")"
  image_ref="$(container_field '{{.Config.Image}}')"
  image_id="$(container_field '{{.Image}}')"
  [[ "$image_id" =~ ^sha256:[0-9a-f]{64}$ ]] || die "source container image ID is malformed: $image_id"
  image_digest="$(docker image inspect "$image_ref" --format '{{if .RepoDigests}}{{index .RepoDigests 0}}{{end}}')"
  [ -n "$image_digest" ] || die "source Oracle image has no immutable repository digest"
  [[ "$image_digest" =~ ^gvenzl/oracle-xe@sha256:[0-9a-f]{64}$ ]] \
    || die "source Oracle image repository digest is malformed: $image_digest"
  volume_created="$(docker volume inspect "$SOURCE_VOLUME" --format '{{.CreatedAt}}')"
  container_created="$(container_field '{{.Created}}')"
  container_finished="$(container_field '{{.State.FinishedAt}}')"

  {
    printf 'format=%s\n' "$FORMAT"
    printf 'created_at_utc=%s\n' "$(date -u +%Y-%m-%dT%H:%M:%SZ)"
    printf 'source_container=%s\n' "$SOURCE_CONTAINER"
    printf 'source_status=exited\n'
    printf 'source_finished_at=%s\n' "$container_finished"
    printf 'source_container_created_at=%s\n' "$container_created"
    printf 'source_volume=%s\n' "$SOURCE_VOLUME"
    printf 'source_volume_created_at=%s\n' "$volume_created"
    printf 'source_mount=%s\n' "$SOURCE_MOUNT"
    printf 'source_network=%s\n' "$ISO_NETWORK"
    printf 'source_network_internal=true\n'
    printf 'database_links=operator-asserted-removed-after-local-metadata-audit\n'
    printf 'oracle_image_ref=%s\n' "$image_ref"
    printf 'oracle_image_id=%s\n' "$image_id"
    printf 'oracle_image_digest=%s\n' "$image_digest"
    printf 'archiver_image_ref=%s\n' "$ARCHIVER_IMAGE"
    printf 'archiver_image_id=%s\n' "$archiver_id"
    printf 'archive_filename=oracle-volume.tar.gz\n'
    printf 'archive_bytes=%s\n' "$archive_bytes"
    printf 'archive_sha256=%s\n' "$archive_hash"
    printf 'scope=oracle-volume-only; files, redis, application-image, configuration, and production are excluded\n'
  } > "$tmp_manifest"
  chmod 600 "$tmp_archive" "$tmp_manifest"

  mv "$tmp_archive" "$archive"
  mv "$tmp_manifest" "$manifest"
  trap - EXIT
  echo "[oracle-snapshot] created $dir"
  echo "[oracle-snapshot] archive SHA-256: $archive_hash"
  echo "[oracle-snapshot] next: $0 verify $dir"
}

restore_snapshot() {
  [ "$#" = 3 ] || usage
  local dir target_volume confirmation restore_nonce archiver_image_id oracle_image_id archive_hash restore_tmp copied_hash target_options
  if ! dir="$(snapshot_dir "$1")"; then
    exit 1
  fi
  target_volume="$2"
  confirmation="$3"
  [ "$confirmation" = "--create-empty-volume" ] || usage
  [[ "$target_volume" =~ ^lims-snapshot-[A-Za-z0-9][A-Za-z0-9._-]*$ ]] || die "target volume must start with lims-snapshot-"
  [ "$target_volume" != "$SOURCE_VOLUME" ] || die "refusing to use the source volume"

  need_command docker
  need_command gzip
  need_command sha256sum
  need_command stat
  need_command tar
  need_command cp
  need_command flock
  docker image inspect "$ARCHIVER_IMAGE" >/dev/null 2>&1 || die "required local archiver image is absent: $ARCHIVER_IMAGE"
  acquire_snapshot_lock
  verify_snapshot "$dir"
  archiver_image_id="${VERIFIED_ARCHIVER_IMAGE_ID:-}"
  oracle_image_id="${VERIFIED_ORACLE_IMAGE_ID:-}"
  archive_hash="${VERIFIED_ARCHIVE_SHA256:-}"
  [[ "$archiver_image_id" =~ ^sha256:[0-9a-f]{64}$ ]] || die "validated archiver image ID is malformed"
  [[ "$oracle_image_id" =~ ^sha256:[0-9a-f]{64}$ ]] || die "validated Oracle image ID is malformed"
  [[ "$archive_hash" =~ ^[0-9a-f]{64}$ ]] || die "validated archive hash is malformed"
  [ "$(docker image inspect "$archiver_image_id" --format '{{.Id}}' 2>/dev/null || true)" = "$archiver_image_id" ] \
    || die "validated archiver image is no longer available locally"

  # Pin the exact bytes that passed verification.  Extraction must not reopen
  # the caller-controlled snapshot path after verification, otherwise a
  # replacement between verify and tar would bypass the manifest hash.
  restore_tmp="$(mktemp -d "$ROOT/private/.oracle-restore.XXXXXX")"
  chmod 700 "$restore_tmp"
  trap 'if [ -n "${restore_tmp:-}" ]; then rm -rf -- "$restore_tmp"; fi' EXIT
  cp -- "$dir/oracle-volume.tar.gz" "$restore_tmp/oracle-volume.tar.gz"
  chmod 600 "$restore_tmp/oracle-volume.tar.gz"
  copied_hash="$(sha256sum "$restore_tmp/oracle-volume.tar.gz" | awk '{print $1}')"
  [ "$copied_hash" = "$archive_hash" ] || die "archive changed after verification; refusing to restore"
  gzip -t "$restore_tmp/oracle-volume.tar.gz" || die "verified archive copy failed gzip integrity check"
  assert_safe_archive_paths "$restore_tmp/oracle-volume.tar.gz"
  assert_safe_archive_entries "$restore_tmp/oracle-volume.tar.gz"

  docker volume inspect "$target_volume" >/dev/null 2>&1 && die "target volume already exists: $target_volume"
  restore_nonce="$(date -u +%Y%m%dT%H%M%SZ)-$$-$RANDOM"
  docker volume create \
    --label "lims.oracle-snapshot.format=$FORMAT" \
    --label "lims.oracle-snapshot.restore-nonce=$restore_nonce" \
    --label "lims.oracle-snapshot.oracle-image-id=$oracle_image_id" \
    --label "lims.oracle-snapshot.archiver-image-id=$archiver_image_id" \
    --label "lims.oracle-snapshot.archive-sha256=$archive_hash" \
    "$target_volume" >/dev/null
  [ "$(docker volume inspect "$target_volume" --format '{{.Driver}}')" = local ] \
    || die "target volume is not a local Docker volume: $target_volume"
  target_options="$(docker volume inspect "$target_volume" --format '{{json .Options}}')"
  case "$target_options" in
    null|{}) ;;
    *) die "target volume unexpectedly has local-driver options: $target_volume" ;;
  esac
  [ "$(docker volume inspect "$target_volume" --format '{{index .Labels "lims.oracle-snapshot.format"}}')" = "$FORMAT" ] \
    || die "target volume format label was not created by this restore invocation: $target_volume"
  [ "$(docker volume inspect "$target_volume" --format '{{index .Labels "lims.oracle-snapshot.restore-nonce"}}')" = "$restore_nonce" ] \
    || die "target volume was not created by this restore invocation: $target_volume"
  [ "$(docker volume inspect "$target_volume" --format '{{index .Labels "lims.oracle-snapshot.oracle-image-id"}}')" = "$oracle_image_id" ] \
    || die "target volume Oracle image identity label was not created by this restore invocation: $target_volume"
  [ "$(docker volume inspect "$target_volume" --format '{{index .Labels "lims.oracle-snapshot.archiver-image-id"}}')" = "$archiver_image_id" ] \
    || die "target volume archiver image identity label was not created by this restore invocation: $target_volume"
  [ "$(docker volume inspect "$target_volume" --format '{{index .Labels "lims.oracle-snapshot.archive-sha256"}}')" = "$archive_hash" ] \
    || die "target volume archive hash label was not created by this restore invocation: $target_volume"
  [ -z "$(docker ps -aq --filter "volume=$target_volume")" ] \
    || die "target volume became mounted by a running container: $target_volume"

  echo "[oracle-snapshot] restoring into new, unmounted volume $target_volume; no Oracle container will be started"
  if ! docker run --pull=never --rm --network none --read-only --security-opt=no-new-privileges \
      --tmpfs /tmp:rw,noexec,nosuid,nodev \
      --mount "type=volume,src=$target_volume,dst=/data,volume-nocopy" \
      --mount "type=bind,src=$restore_tmp,dst=/snapshot,readonly" \
      "$archiver_image_id" \
      sh -ec "test -z \"\$(find /data -mindepth 1 -maxdepth 1 -print -quit)\"; cp -- /snapshot/oracle-volume.tar.gz /tmp/oracle-volume.tar.gz; printf '%s  %s\\n' '$archive_hash' /tmp/oracle-volume.tar.gz | sha256sum -c -; tar --numeric-owner --same-owner -xzf /tmp/oracle-volume.tar.gz -C /data"; then
      die "restore failed; $target_volume was left for inspection and is not automatically removed"
  fi

  docker run --pull=never --rm --network none --read-only \
    --mount "type=volume,src=$target_volume,dst=/data,readonly,volume-nocopy" \
    "$archiver_image_id" \
    sh -ec 'test -f /data/XE/control01.ctl; test -d /data/XE/XEPDB1; test -d /data/dbconfig'

  docker run --pull=never --rm --network none --security-opt=no-new-privileges \
    --mount "type=volume,src=$target_volume,dst=/data,volume-nocopy" \
    "$archiver_image_id" \
    sh -ec "printf 'format=%s\\nrestore_nonce=%s\\noracle_image_id=%s\\narchiver_image_id=%s\\narchive_sha256=%s\\n' '$FORMAT' '$restore_nonce' '$oracle_image_id' '$archiver_image_id' '$archive_hash' > /data/$RESTORE_MARKER; chmod 600 /data/$RESTORE_MARKER"

  echo "[oracle-snapshot] restored volume layout: $target_volume"
  echo "[oracle-snapshot] no database has been started or validated; this is not clean-room or AC-10 evidence"
}

main() {
  [ "$#" -ge 1 ] || usage
  case "$1" in
    export) shift; export_snapshot "$@" ;;
    verify)
      [ "$#" = 2 ] || usage
      need_command docker
      need_command gzip
      need_command sha256sum
      need_command stat
      need_command tar
      need_command flock
      acquire_snapshot_lock
      snapshot_path=""
      if ! snapshot_path="$(snapshot_dir "$2")"; then
        exit 1
      fi
      verify_snapshot "$snapshot_path"
      ;;
    restore) shift; restore_snapshot "$@" ;;
    *) usage ;;
  esac
}

main "$@"
