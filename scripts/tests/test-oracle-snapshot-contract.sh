#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd -P)"
SCRIPT="$ROOT/scripts/oracle-snapshot.sh"
bash -n "$SCRIPT"
grep -Fq 'RESTORE_MARKER=' "$SCRIPT" || { echo "oracle snapshot test: completion marker missing" >&2; exit 1; }
grep -Fq 'lims.oracle-snapshot.oracle-image-id' "$SCRIPT" || { echo "oracle snapshot test: image label missing" >&2; exit 1; }
grep -Fq 'archive_sha256=' "$SCRIPT" || { echo "oracle snapshot test: marker lacks archive binding" >&2; exit 1; }
grep -Fq 'VERIFIED_ARCHIVER_IMAGE_ID' "$SCRIPT" || { echo "oracle snapshot test: verified archiver image ID missing" >&2; exit 1; }
grep -Fq 'validated archiver image is no longer available locally' "$SCRIPT" || { echo "oracle snapshot test: verified archiver availability guard missing" >&2; exit 1; }
grep -Fq '"$archiver_id" \' "$SCRIPT" || { echo "oracle snapshot test: export does not run by captured archiver ID" >&2; exit 1; }
if grep -Fq '    "$ARCHIVER_IMAGE" \' "$SCRIPT"; then
  echo "oracle snapshot test: a Docker run still uses mutable archiver tag" >&2
  exit 1
fi
grep -Fq 'lims.oracle-snapshot.archive-sha256' "$SCRIPT" || { echo "oracle snapshot test: archive hash volume label missing" >&2; exit 1; }
grep -Fq 'lims.oracle-snapshot.archiver-image-id' "$SCRIPT" || { echo "oracle snapshot test: archiver image volume label missing" >&2; exit 1; }
grep -Fq 'archiver_image_id=%s' "$SCRIPT" || { echo "oracle snapshot test: archiver identity marker missing" >&2; exit 1; }
grep -Fq 'lims.oracle-snapshot.archiver-image-id' "$SCRIPT" || { echo "oracle snapshot test: archiver image label missing" >&2; exit 1; }
grep -Fq 'archiver_image_id=%s' "$SCRIPT" || { echo "oracle snapshot test: restore marker archiver identity missing" >&2; exit 1; }
grep -Fq 'control01.ctl' "$SCRIPT" || { echo "oracle snapshot test: layout verification missing" >&2; exit 1; }
grep -Fq 'source_finished_at' "$SCRIPT" || { echo "oracle snapshot test: source stop metadata missing" >&2; exit 1; }
grep -Fq 'manifest source status is not exited' "$SCRIPT" || { echo "oracle snapshot test: source status manifest guard missing" >&2; exit 1; }
grep -Fq '{{json .Options}}' "$SCRIPT" || { echo "oracle snapshot test: local-driver option guard missing" >&2; exit 1; }
grep -Fq 'source container image ID does not match its local image reference' "$SCRIPT" || { echo "oracle snapshot test: source image identity guard missing" >&2; exit 1; }
grep -Fq '[ -n "$local_oracle_digest" ]' "$SCRIPT" || { echo "oracle snapshot test: local Oracle digest presence guard missing" >&2; exit 1; }
grep -Fq 'VERIFIED_ARCHIVE_SHA256' "$SCRIPT" || { echo "oracle snapshot test: in-memory verified archive hash missing" >&2; exit 1; }
grep -Fq 'archive changed after verification' "$SCRIPT" || { echo "oracle snapshot test: restore copy hash guard missing" >&2; exit 1; }
grep -Fq 'snapshot lock must not be a symlink' "$SCRIPT" || { echo "oracle snapshot test: lock symlink guard missing" >&2; exit 1; }
grep -Fq 'exec {lock_fd}>>' "$SCRIPT" || { echo "oracle snapshot test: lock open must not truncate" >&2; exit 1; }
grep -Fq -- '--tmpfs /tmp:rw,noexec,nosuid,nodev' "$SCRIPT" || { echo "oracle snapshot test: restore-private temporary copy missing" >&2; exit 1; }
grep -Fq '0|143' "$SCRIPT" || { echo "oracle snapshot test: clean SIGTERM exit guard missing" >&2; exit 1; }
grep -Fq 'docker image inspect "$image_ref"' "$SCRIPT" || { echo "oracle snapshot test: export image reference variable guard missing" >&2; exit 1; }
if grep -Fq 'image_digest="$(docker image inspect "$image" --format' "$SCRIPT"; then
  echo "oracle snapshot test: export uses unbound image variable" >&2
  exit 1
fi

run_mock_source_check() {
  local exit_code="$1" volume_options="$2"
  MOCK_EXIT_CODE="$exit_code" MOCK_VOLUME_OPTIONS="$volume_options" bash -c '
    set -euo pipefail
    source <(sed '\''/^main "\$@"$/d'\'' "$1")
    docker() {
      case "$1" in
        inspect)
          case "$*" in
            *"{{.State.Status}}"*) printf "exited\\n" ;;
            *"{{.State.ExitCode}}"*) printf "%s\\n" "$MOCK_EXIT_CODE" ;;
            *"{{.State.OOMKilled}}"*) printf "false\\n" ;;
            *"{{.State.Error}}"*) : ;;
            *"{{.HostConfig.RestartPolicy.Name}}"*) printf "no\\n" ;;
            *"{{.State.FinishedAt}}"*) printf "2026-09-30T00:00:00Z\\n" ;;
            *"{{.Config.Image}}"*) printf "gvenzl/oracle-xe:21-slim\\n" ;;
            *"{{.Image}}"*) printf "sha256:aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\\n" ;;
            *"{{range .Mounts}}"*) printf "lims-oracle-data\\n" ;;
            *"{{range \$k,\$v := .NetworkSettings.Networks}}"*) printf "lims-iso \\n" ;;
          esac ;;
        image)
          case "$*" in *"{{.Id}}"*) printf "sha256:aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\\n" ;; esac ;;
        volume)
          case "$*" in
            *"{{.Driver}}"*) printf "local\\n" ;;
            *"{{json .Options}}"*) printf "%s\\n" "$MOCK_VOLUME_OPTIONS" ;;
          esac ;;
        ps) : ;;
        network)
          case "$*" in *"{{.Internal}}"*) printf "true\\n" ;; esac ;;
      esac
    }
    assert_source_is_cold_and_isolated
  ' _ "$SCRIPT"
}

run_mock_source_check 0 '{}'
if ! run_mock_source_check 143 '{}'; then
  echo "oracle snapshot test: clean SIGTERM exit 143 was rejected" >&2
  exit 1
fi
if run_mock_source_check 0 '{"type":"none","o":"bind"}'; then
  echo "oracle snapshot test: local bind/NFS-style volume options were accepted" >&2
  exit 1
fi
echo "oracle-snapshot contract test PASS"
