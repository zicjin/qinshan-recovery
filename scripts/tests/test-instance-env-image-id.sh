#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SCRIPT="$ROOT/scripts/instance-env.sh"
bash -n "$SCRIPT"
grep -Fq 'IMAGE_ID=' "$SCRIPT" || {
  echo "instance env image test: IMAGE_ID template missing" >&2; exit 1;
}
grep -Fq 'ORACLE_IMAGE_ID=' "$SCRIPT" || { echo "instance env image test: Oracle image pin missing" >&2; exit 1; }
grep -Fq 'REDIS_IMAGE_ID=' "$SCRIPT" || { echo "instance env image test: Redis image pin missing" >&2; exit 1; }

tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT
mkdir -p "$tmp/private/instances/test/lic" "$tmp/private/instances/test/files"
chmod 700 "$tmp/private" "$tmp/private/instances" "$tmp/private/instances/test" \
  "$tmp/private/instances/test/lic" "$tmp/private/instances/test/files"
printf 'spring.datasource.url=jdbc:oracle:thin:@oracle:1521/XEPDB1\n' > "$tmp/private/instances/test/application-isolated.properties"
chmod 600 "$tmp/private/instances/test/application-isolated.properties"
cat > "$tmp/private/instances/test/runtime.env" <<'EOF'
IMAGE_REF=lims-app:14.1.5-built
IMAGE_ID=sha256:0000000000000000000000000000000000000000000000000000000000000000
ORACLE_IMAGE_ID=sha256:1111111111111111111111111111111111111111111111111111111111111111
REDIS_IMAGE_ID=sha256:2222222222222222222222222222222222222222222222222222222222222222
ORACLE_SYS_PASSWORD=oraclepass
REDIS_PASSWORD=redispass
MAC_ADDRESS=02:00:00:00:00:01
HOST_PORT=18081
EOF
chmod 600 "$tmp/private/instances/test/runtime.env"

bash -c '
  set -euo pipefail
  source "$1"
  ROOT="$2"
  INSTANCE_ROOT="$ROOT/private/instances"
  instance_env_load test
  [ "$INSTANCE_IMAGE_ID" = "sha256:0000000000000000000000000000000000000000000000000000000000000000" ]
' _ "$SCRIPT" "$tmp"

sed -i 's/^IMAGE_ID=.*/IMAGE_ID=/' "$tmp/private/instances/test/runtime.env"
if bash -c 'source "$1"; ROOT="$2"; INSTANCE_ROOT="$ROOT/private/instances"; instance_env_load test' _ "$SCRIPT" "$tmp" >/dev/null 2>&1; then
  echo "instance env image test: blank IMAGE_ID was accepted" >&2
  exit 1
fi

sed -i 's/^IMAGE_ID=.*/IMAGE_ID=sha256:0000000000000000000000000000000000000000000000000000000000000000/' "$tmp/private/instances/test/runtime.env"
sed -i '/^ORACLE_IMAGE_ID=/d' "$tmp/private/instances/test/runtime.env"
if bash -c 'source "$1"; ROOT="$2"; INSTANCE_ROOT="$ROOT/private/instances"; instance_env_load test' _ "$SCRIPT" "$tmp" >/dev/null 2>&1; then
  echo "instance env image test: missing ORACLE_IMAGE_ID was accepted without legacy manifest" >&2
  exit 1
fi

echo "instance env image test PASS"
