#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SSH_HOST="${SSH_HOST:-sss2}"
REMOTE_ROOT="${REMOTE_ROOT:-/data/lims-owned-sss2}"
DIST="${DIST:-$ROOT/owned-distribution/target/distribution}"
JDK_ARCHIVE="${JDK_ARCHIVE:-$ROOT/materials/jdk-8u391-linux-x64.tar.gz}"
HOST_PORT="${HOST_PORT:-18082}"
SOURCE_COMMIT="$(git -C "$ROOT" rev-parse HEAD)"
SOURCE_DIRTY=0
STATUS_OUTPUT="$(git -C "$ROOT" status --porcelain --untracked-files=all)"
if [ -n "$STATUS_OUTPUT" ]; then SOURCE_DIRTY=1; fi
[[ -f "$JDK_ARCHIVE" ]] || { echo "missing JDK archive" >&2; exit 2; }
[[ -f "$DIST/app/lims-owned-application.jar" ]] || { bash "$ROOT/scripts/build-owned-distribution.sh"; }
WORK="$(mktemp -d -t owned-sss2.XXXXXX)"
trap 'rm -rf "$WORK"' EXIT
mkdir -p "$WORK/distribution"
tar -C "$DIST" -czf "$WORK/distribution.tgz" .
cp "$JDK_ARCHIVE" "$WORK/jdk.tgz"
cp "$ROOT/scripts/owned-sss2-remote-run.sh" "$WORK/remote-run.sh"
ssh "$SSH_HOST" "mkdir -p '$REMOTE_ROOT/incoming' && chmod 700 '$REMOTE_ROOT' '$REMOTE_ROOT/incoming'"
scp "$WORK/distribution.tgz" "$SSH_HOST:$REMOTE_ROOT/incoming/distribution.tgz"
scp "$WORK/jdk.tgz" "$SSH_HOST:$REMOTE_ROOT/incoming/jdk.tgz"
scp "$WORK/remote-run.sh" "$SSH_HOST:$REMOTE_ROOT/remote-run.sh"
ssh "$SSH_HOST" "chmod 700 '$REMOTE_ROOT/remote-run.sh'; SOURCE_COMMIT='$SOURCE_COMMIT' SOURCE_DIRTY='$SOURCE_DIRTY' nohup bash '$REMOTE_ROOT/remote-run.sh' '$REMOTE_ROOT' '$HOST_PORT' > '$REMOTE_ROOT/remote-run.log' 2>&1 < /dev/null & echo remote_pid=\$!"
echo "deploy-owned-sss2: started remote job; monitor $SSH_HOST:$REMOTE_ROOT/remote-run.log"
