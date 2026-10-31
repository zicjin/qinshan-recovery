#!/usr/bin/env bash
set -euo pipefail

# Refresh only a long-lived diagnostic application on sss2. The existing
# supplier app (18081), owned diagnostic app (18087), Oracle and Redis remain
# in place. This is not a strict evidence runner.

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SSH_HOST="${SSH_HOST:-sss2}"
REMOTE_ROOT="${REMOTE_ROOT:-/data/lims-owned-sss2}"
HOST_PORT="${HOST_PORT:-18088}"
APP_NAME="${APP_NAME:-lims-owned-sss2-debug-app}"
IMAGE_NAME="${IMAGE_NAME:-lims-owned-sss2-owned:debug}"
FORCE_REFRESH="${FORCE_REFRESH:-0}"
DIST="${DIST:-$ROOT/owned-distribution/target/distribution}"
SOURCE_COMMIT="$(git -C "$ROOT" rev-parse HEAD)"

case "$HOST_PORT" in
  ''|*[!0-9]*) echo "HOST_PORT must be numeric" >&2; exit 2 ;;
esac
if (( HOST_PORT < 1024 || HOST_PORT > 65535 )); then
  echo "HOST_PORT must be between 1024 and 65535" >&2
  exit 2
fi
if [[ ! "$APP_NAME" =~ ^[a-z][a-z0-9_.-]{2,62}$ ]]; then
  echo "invalid APP_NAME" >&2
  exit 2
fi
if [[ ! "$IMAGE_NAME" =~ ^[a-z0-9][a-z0-9_.:/-]*$ ]]; then
  echo "invalid IMAGE_NAME" >&2
  exit 2
fi
if [[ ! -f "$DIST/start.sh" || ! -f "$DIST/app/lims-owned-application.jar" ]]; then
  echo "missing owned distribution; run scripts/build-owned-distribution.sh first" >&2
  exit 2
fi

WORK="$(mktemp -d -t owned-sss2-debug.XXXXXX)"
trap 'rm -rf "$WORK"' EXIT
tar -C "$DIST" -czf "$WORK/distribution.tgz" .

ssh "$SSH_HOST" "mkdir -p '$REMOTE_ROOT/incoming-debug' && chmod 700 '$REMOTE_ROOT/incoming-debug'"
scp "$WORK/distribution.tgz" "$SSH_HOST:$REMOTE_ROOT/incoming-debug/distribution.tgz"

ssh "$SSH_HOST" bash -s -- \
  "$REMOTE_ROOT" "$HOST_PORT" "$APP_NAME" "$IMAGE_NAME" "$SOURCE_COMMIT" "$FORCE_REFRESH" <<'REMOTE'
set -euo pipefail
ROOT="$1"
PORT="$2"
APP="$3"
IMAGE="$4"
SOURCE_COMMIT="$5"
FORCE_REFRESH="$6"
NET=lims-owned-sss2-net
ORA=lims-owned-sss2-oracle
REDIS=lims-owned-sss2-redis

for name in "$ORA" "$REDIS"; do
  docker inspect "$name" >/dev/null 2>&1 || {
    echo "required existing container is missing: $name" >&2
    exit 3
  }
  state="$(docker inspect "$name" --format '{{.State.Status}}')"
  [[ "$state" == running ]] || { echo "$name is not running" >&2; exit 3; }
done
[[ "$(docker network inspect "$NET" --format '{{.Internal}}')" == true ]] || {
  echo "$NET must remain internal" >&2
  exit 3
}

if docker inspect "$APP" >/dev/null 2>&1 && [[ "$FORCE_REFRESH" != 1 ]]; then
  config_mount="$(docker inspect -f '{{range .Mounts}}{{if eq .Destination "/run/config/application.properties"}}{{.Source}}{{end}}{{end}}' "$APP" 2>/dev/null || true)"
  if [[ "$config_mount" == "$ROOT/debug-application.properties" ]] \
      && [[ -f "$ROOT/debug-application.properties" ]] \
      && grep -q '^server\.servlet\.session\.cookie\.name=OWNED_SSS2_DEBUG_SESSION$' "$ROOT/debug-application.properties" \
      && curl -fsS --max-time 3 "http://127.0.0.1:${PORT}/open/security/public-key" >/dev/null 2>&1; then
    cat > "$ROOT/debug-app-report.json" <<EOF
{
  "schema": 1,
  "status": "diagnostic-running",
  "execution": "debug-reuse",
  "production_access": false,
  "source_commit": "${SOURCE_COMMIT}",
  "container": "${APP}",
  "loopback_url": "http://127.0.0.1:${PORT}",
  "port_forward_pid_file": "${ROOT}/debug-port-forward.pid",
  "reused_oracle": "${ORA}",
  "reused_redis": "${REDIS}",
  "supplier_port_preserved": 18081,
  "existing_owned_debug_port_preserved": 18087,
  "runtime_mode": "debug-reuse",
  "reuse_runtime": true,
  "oracle_restarted": false,
  "redis_restarted": false,
  "strict_evidence": false
}
EOF
    chmod 600 "$ROOT/debug-app-report.json"
    echo "reused diagnostic app $APP on 127.0.0.1:$PORT (source=$SOURCE_COMMIT)"
    exit 0
  fi
  docker rm --force "$APP" >/dev/null
fi
if [[ "$FORCE_REFRESH" == 1 ]] && docker inspect "$APP" >/dev/null 2>&1; then
  docker rm --force "$APP" >/dev/null
fi
if [[ -f "$ROOT/debug-port-forward.pid" ]]; then
  old_pid="$(cat "$ROOT/debug-port-forward.pid" 2>/dev/null || true)"
  [[ "$old_pid" =~ ^[0-9]+$ ]] && kill "$old_pid" >/dev/null 2>&1 || true
  rm -f "$ROOT/debug-port-forward.pid"
fi

CONTEXT="$ROOT/debug-context"
rm -rf "$CONTEXT"
mkdir -p "$CONTEXT/distribution"
tar -xzf "$ROOT/incoming-debug/distribution.tgz" -C "$CONTEXT/distribution"
cp "$ROOT/application.properties" "$ROOT/debug-application.properties"
cat >> "$ROOT/debug-application.properties" <<'EOF'
# Keep the diagnostic 18088 browser session separate from owned 18087. Cookie
# scope does not include the TCP port, so the default JSESSIONID would collide.
server.servlet.session.cookie.name=OWNED_SSS2_DEBUG_SESSION
EOF
chmod 600 "$ROOT/debug-application.properties"
cat > "$CONTEXT/Dockerfile" <<EOF
FROM lims-owned-sss2-owned:current
RUN rm -rf /opt/owned/*
COPY distribution /opt/owned
RUN chmod 0755 /opt/owned/start.sh
EOF
docker image inspect lims-owned-sss2-owned:current >/dev/null 2>&1 || {
  echo "base image lims-owned-sss2-owned:current is missing; run the full sss2 deployment first" >&2
  exit 3
}
docker build --pull=false --tag "$IMAGE" "$CONTEXT" >/dev/null
docker create --name "$APP" --network "$NET" \
  --env LIMS_BIND_ADDRESS=0.0.0.0 \
  --env JAVA_TOOL_OPTIONS=-Djava.security.egd=file:/dev/./urandom \
  --volume "$ROOT/debug-application.properties:/run/config/application.properties:ro" \
  "$IMAGE" >/dev/null
docker start "$APP" >/dev/null
APP_IP="$(docker inspect "$APP" -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}')"
[[ -n "$APP_IP" ]] || { echo "diagnostic app internal IP unavailable" >&2; exit 4; }
cat > "$ROOT/debug-port-forward.py" <<'PYFORWARD'
import socket
import sys
import threading

listen_port = int(sys.argv[1])
target_host = sys.argv[2]
target_port = int(sys.argv[3])

def relay(source, target):
    try:
        while True:
            data = source.recv(65536)
            if not data:
                break
            target.sendall(data)
    except OSError:
        pass
    finally:
        for sock in (source, target):
            try:
                sock.shutdown(socket.SHUT_RDWR)
            except OSError:
                pass
            sock.close()

def serve(connection):
    try:
        upstream = socket.create_connection((target_host, target_port), 5)
    except OSError:
        connection.close()
        return
    threading.Thread(target=relay, args=(connection, upstream), daemon=True).start()
    threading.Thread(target=relay, args=(upstream, connection), daemon=True).start()

with socket.socket() as server:
    server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server.bind(("127.0.0.1", listen_port))
    server.listen(32)
    while True:
        connection, _ = server.accept()
        threading.Thread(target=serve, args=(connection,), daemon=True).start()
PYFORWARD
chmod 700 "$ROOT/debug-port-forward.py"
nohup python3 "$ROOT/debug-port-forward.py" "$PORT" "$APP_IP" 8080 \
  > "$ROOT/debug-port-forward.log" 2>&1 < /dev/null &
echo $! > "$ROOT/debug-port-forward.pid"

ready=0
for _ in $(seq 1 120); do
  code="$(curl -sS -o /dev/null -w '%{http_code}' --max-time 3 "http://127.0.0.1:${PORT}/open/security/public-key" 2>/dev/null || true)"
  if [[ "$code" == 200 ]]; then ready=1; break; fi
  sleep 2
done
if [[ "$ready" != 1 ]]; then
  echo "diagnostic app did not become ready; container retained for inspection" >&2
  exit 4
fi
cat > "$ROOT/debug-app-report.json" <<EOF
{
  "schema": 1,
  "status": "diagnostic-running",
  "execution": "debug-reuse",
  "production_access": false,
  "source_commit": "${SOURCE_COMMIT}",
  "container": "${APP}",
  "loopback_url": "http://127.0.0.1:${PORT}",
  "port_forward_pid_file": "${ROOT}/debug-port-forward.pid",
  "reused_oracle": "${ORA}",
  "reused_redis": "${REDIS}",
  "supplier_port_preserved": 18081,
  "existing_owned_debug_port_preserved": 18087,
  "runtime_mode": "debug-reuse",
  "reuse_runtime": true,
  "oracle_restarted": false,
  "redis_restarted": false,
  "strict_evidence": false
}
EOF
chmod 600 "$ROOT/debug-app-report.json"
echo "started diagnostic app $APP on 127.0.0.1:$PORT (source=$SOURCE_COMMIT)"
REMOTE

echo "owned sss2 debug app ready on $SSH_HOST:127.0.0.1:$HOST_PORT"
