#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
STACK="$ROOT/scripts/stack-demo-restore.sh"

bash -n "$STACK"
grep -Fq 'SOURCE_IDENTITY_FILE=' "$STACK" || {
  echo "replay source identity test: identity input is missing" >&2; exit 1;
}
grep -Fq 'restricted-archive-manifest' "$STACK" || {
  echo "replay source identity test: clean archive fallback is missing" >&2; exit 1;
}
grep -Fq 'rev-parse --show-toplevel' "$STACK" || {
  echo "replay source identity test: Git top-level boundary is missing" >&2; exit 1;
}
grep -Fq 'assert_instance_isolated_config' "$STACK" || {
  echo "replay source identity test: isolated-config preflight is missing" >&2; exit 1;
}
grep -Fq 'assert_replay_subnet_unused' "$STACK" || {
  echo "replay source identity test: subnet collision preflight is missing" >&2; exit 1;
}
grep -Fq 'REPLAY_SUBNET="10.' "$ROOT/scripts/instance-env.sh" || {
  echo "replay source identity test: replay subnet is not RFC1918 10/8" >&2; exit 1;
}
grep -Fq 'oracle-image-id' "$ROOT/scripts/oracle-snapshot.sh" || {
  echo "replay source identity test: snapshot Oracle image label is missing" >&2; exit 1;
}
grep -Fq 'snapshot-restore-complete' "$STACK" || {
  echo "replay source identity test: completed restore marker is missing" >&2; exit 1;
}
grep -Fq 'archive-sha256' "$ROOT/scripts/oracle-snapshot.sh" || {
  echo "replay source identity test: snapshot archive hash label is missing" >&2; exit 1;
}
grep -Fq 'snapshot restore marker archive hash does not match its volume label' "$STACK" || {
  echo "replay source identity test: restore marker archive hash is not consumed" >&2; exit 1;
}
grep -Fq 'snapshot volume has local-driver bind/options' "$STACK" || {
  echo "replay source identity test: snapshot volume local-option guard is missing" >&2; exit 1;
}
grep -Fq 'archiver_image_id=' "$STACK" || {
  echo "replay source identity test: replay manifest lacks archiver image identity" >&2; exit 1;
}
grep -Fq 'archiver image tag changed during replay preflight' "$STACK" || {
  echo "replay source identity test: mutable archiver tag guard is missing" >&2; exit 1;
}
grep -Fq '"$SNAPSHOT_VOLUME:/data:ro" "$ARCHIVER_IMAGE_ID"' "$STACK" || {
  echo "replay source identity test: marker probe still uses mutable archiver tag" >&2; exit 1;
}
grep -Fq 'snapshot restore marker archiver image identity does not match' "$STACK" || {
  echo "replay source identity test: marker archiver identity is not consumed" >&2; exit 1;
}
grep -Fq 'snapshot_archive_sha256=' "$STACK" || {
  echo "replay source identity test: snapshot archive hash is not recorded in manifest" >&2; exit 1;
}
grep -Fq 'oracle_snapshot_script_sha256=' "$STACK" || {
  echo "replay source identity test: snapshot script hash is not recorded" >&2; exit 1;
}
grep -Fq 'isolation_script_sha256=' "$STACK" || {
  echo "replay source identity test: isolation checker hash is not recorded" >&2; exit 1;
}
grep -Fq 'materials directory is missing or symlinked' "$STACK" || {
  echo "replay source identity test: materials parent symlink guard is missing" >&2; exit 1;
}
grep -Fq 'snapshot-leases' "$STACK" || {
  echo "replay source identity test: snapshot lease is missing" >&2; exit 1;
}
grep -Fq 'lease directory must not be a symlink' "$STACK" || {
  echo "replay source identity test: lease directory symlink guard is missing" >&2; exit 1;
}
grep -Fq 'replay launch lock must not be a symlink' "$STACK" || {
  echo "replay source identity test: launch lock symlink guard is missing" >&2; exit 1;
}
grep -Fq 'exec {lock_fd}>>' "$STACK" || {
  echo "replay source identity test: launch lock must not truncate" >&2; exit 1;
}
grep -Fq 'exec ${REPLAY_LOCK_FD}>&-' "$STACK" || {
  echo "replay source identity test: forwarder must not inherit launch lock" >&2; exit 1;
}
grep -Fq 'need_command flock' "$STACK" || {
  echo "replay source identity test: flock prerequisite guard is missing" >&2; exit 1;
}
grep -Fq 'snapshot volume lease must not be a symlink' "$STACK" || {
  echo "replay source identity test: lease symlink guard is missing" >&2; exit 1;
}
grep -Fq 'error_file' "$STACK" || {
  echo "replay source identity test: host-port probe is not fail-closed" >&2; exit 1;
}
grep -Fq 'if [[ "${BASH_SOURCE[0]}" == "$0" ]]; then' "$STACK" || {
  echo "replay source identity test: helper cannot be safely sourced" >&2; exit 1;
}

tmp="$(mktemp -d "$ROOT/private/source-identity-test.XXXXXX")"
trap 'rm -rf "$tmp"' EXIT
identity="$tmp/source-identity.env"
{
  printf 'ROOT_SOURCE_ID=sha256:%064d\n' 0
  printf 'PROJECT_SOURCE_ID=sha256:%064d\n' 1
} > "$identity"
chmod 600 "$identity"

SOURCE_IDENTITY_FILE="$identity" bash -c '
  set -euo pipefail
  source "$1"
  assert_source_identity_file
  [ "$(read_source_identity ROOT_SOURCE_ID)" = "sha256:0000000000000000000000000000000000000000000000000000000000000000" ]
' _ "$STACK"

printf 'ROOT_SOURCE_ID=not-a-hash\nPROJECT_SOURCE_ID=sha256:%064d\n' 1 > "$identity"
if SOURCE_IDENTITY_FILE="$identity" bash -c 'source "$1"; assert_source_identity_file' _ "$STACK" >/dev/null 2>&1; then
  echo "replay source identity test: malformed archive identity was accepted" >&2
  exit 1
fi

if bash -c '
  source "$1"
  REPLAY_SUBNET=10.20.0.0/16
  docker() {
    if [ "$1 $2 $3" = "network ls -q" ]; then printf "n1\\n"; return 0; fi
    if [ "$1 $2 $3" = "network inspect n1" ]; then printf "10.20.0.0/24\\n"; return 0; fi
    return 1
  }
  assert_replay_subnet_unused
' _ "$STACK" >/dev/null 2>&1; then
  echo "replay source identity test: overlapping CIDR was accepted" >&2
  exit 1
fi
if ! bash -c '
  source "$1"
  REPLAY_SUBNET=10.20.0.0/16
  docker() {
    if [ "$1 $2 $3" = "network ls -q" ]; then printf "n1\\n"; return 0; fi
    if [ "$1 $2 $3" = "network inspect n1" ]; then printf "10.21.0.0/16\\n"; return 0; fi
    return 1
  }
  assert_replay_subnet_unused
' _ "$STACK" >/dev/null 2>&1; then
  echo "replay source identity test: disjoint CIDR was rejected" >&2
  exit 1
fi

echo "replay source identity test PASS"
