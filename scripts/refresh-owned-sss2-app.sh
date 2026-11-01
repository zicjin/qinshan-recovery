#!/usr/bin/env bash
set -euo pipefail

# Refresh only the owned application on sss2. Oracle, Redis, supplier 18081,
# and debug 18088 remain running.
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SSH_HOST="${SSH_HOST:-sss2}"
REMOTE_ROOT="${REMOTE_ROOT:-/data/lims-owned-sss2}"
HOST_PORT="${HOST_PORT:-18087}"
APP_NAME="${APP_NAME:-lims-owned-sss2-app}"
IMAGE_NAME="${IMAGE_NAME:-lims-owned-sss2-owned:refresh}"
DIST="${DIST:-$ROOT/owned-distribution/target/distribution}"
BASE_IMAGE="${BASE_IMAGE:-lims-owned-sss2-owned:refresh}"
# Documentation, ticket and evidence bookkeeping changes are outside the
# implementation binding. Reuse the shared helper so deployment metadata
# reflects only the code/runtime scope used by strict evidence.
SOURCE_COMMIT="$(python3 "$ROOT/scripts/source_binding.py" | python3 -c 'import json,sys; print(json.load(sys.stdin)["repository_head_at_capture"])')"
SOURCE_DIRTY="$(python3 "$ROOT/scripts/source_binding.py" | python3 -c 'import json,sys; print(1 if json.load(sys.stdin)["implementation_dirty"] else 0)')"

[[ -f "$DIST/start.sh" && -f "$DIST/app/lims-owned-application.jar" ]] || {
  echo "missing owned distribution; run scripts/build-owned-distribution.sh first" >&2
  exit 2
}

WORK="$(mktemp -d -t owned-sss2-app-refresh.XXXXXX)"
trap 'rm -rf "$WORK"' EXIT
tar -C "$DIST" -czf "$WORK/distribution.tgz" .
ssh "$SSH_HOST" "mkdir -p '$REMOTE_ROOT/incoming-owned' && chmod 700 '$REMOTE_ROOT/incoming-owned'"
scp "$WORK/distribution.tgz" "$SSH_HOST:$REMOTE_ROOT/incoming-owned/distribution.tgz"

ssh "$SSH_HOST" bash -s -- \
  "$REMOTE_ROOT" "$HOST_PORT" "$APP_NAME" "$IMAGE_NAME" "$SOURCE_COMMIT" "$SOURCE_DIRTY" "$BASE_IMAGE" <<'REMOTE'
set -euo pipefail
ROOT="$1"; PORT="$2"; APP="$3"; IMAGE="$4"; SOURCE_COMMIT="$5"; SOURCE_DIRTY="$6"; BASE_IMAGE="$7"
NET=lims-owned-sss2-net; ORA=lims-owned-sss2-oracle; REDIS=lims-owned-sss2-redis
for name in "$ORA" "$REDIS"; do
  docker inspect "$name" >/dev/null 2>&1 || { echo "missing required container: $name" >&2; exit 3; }
  [[ "$(docker inspect "$name" -f '{{.State.Status}}')" == running ]] || { echo "$name is not running" >&2; exit 3; }
done
[[ "$(docker network inspect "$NET" -f '{{.Internal}}')" == true ]] || { echo "$NET must remain internal" >&2; exit 3; }
[[ -f "$ROOT/application.properties" ]] || { echo "owned application.properties is missing" >&2; exit 3; }
grep -q '^server\.servlet\.session\.cookie\.name=OWNED_SSS2_SESSION$' "$ROOT/application.properties" || { echo "owned session cookie name is missing" >&2; exit 3; }
if [[ -s "$ROOT/port-forward.pid" ]]; then
  old_pid="$(cat "$ROOT/port-forward.pid" 2>/dev/null || true)"
  [[ "$old_pid" =~ ^[0-9]+$ ]] && kill "$old_pid" >/dev/null 2>&1 || true
  rm -f "$ROOT/port-forward.pid"
fi
if [[ -s "$ROOT/port-forward-app.pid" ]]; then
  old_app_pid="$(cat "$ROOT/port-forward-app.pid" 2>/dev/null || true)"
  [[ "$old_app_pid" =~ ^[0-9]+$ ]] && kill "$old_app_pid" >/dev/null 2>&1 || true
  rm -f "$ROOT/port-forward-app.pid"
fi
docker image inspect "$BASE_IMAGE" >/dev/null 2>&1 || { echo "missing base image: $BASE_IMAGE" >&2; exit 3; }
CONTEXT="$ROOT/refresh-owned-app-context"
rm -rf "$CONTEXT"; mkdir -p "$CONTEXT/distribution"
tar -xzf "$ROOT/incoming-owned/distribution.tgz" -C "$CONTEXT/distribution"
cat > "$CONTEXT/Dockerfile" <<EOF
FROM $BASE_IMAGE
RUN rm -rf /opt/owned/*
COPY distribution /opt/owned
RUN mkdir -p /var/lib/lims-owned-attachments
RUN chmod 0755 /opt/owned/start.sh
EOF
docker build --pull=false --tag "$IMAGE" "$CONTEXT" >/dev/null
docker tag "$IMAGE" "$BASE_IMAGE"
docker rm --force "$APP" >/dev/null 2>&1 || true
docker create --name "$APP" --network "$NET" --network-alias app \
  --env LIMS_BIND_ADDRESS=0.0.0.0 \
  --env JAVA_TOOL_OPTIONS=-Djava.security.egd=file:/dev/./urandom \
  --volume "$ROOT/application.properties:/run/config/application.properties:ro" \
  "$IMAGE" >/dev/null
docker start "$APP" >/dev/null
APP_IP="$(docker inspect "$APP" -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}')"
[[ -n "$APP_IP" ]] || { echo "owned app internal IP unavailable" >&2; exit 4; }
cat > "$ROOT/port-forward.py" <<'PYFORWARD'
import socket,sys,threading
listen_port=int(sys.argv[1]); target=sys.argv[2]; target_port=int(sys.argv[3])
bind_host=sys.argv[4] if len(sys.argv) > 4 else "127.0.0.1"
def relay(src,dst):
    try:
        while True:
            data=src.recv(65536)
            if not data: break
            dst.sendall(data)
    except OSError: pass
    finally:
        for sock in (src,dst):
            try: sock.shutdown(socket.SHUT_RDWR)
            except OSError: pass
            sock.close()
def serve(conn):
    try: upstream=socket.create_connection((target,target_port),5)
    except OSError: conn.close(); return
    threading.Thread(target=relay,args=(conn,upstream),daemon=True).start()
    threading.Thread(target=relay,args=(upstream,conn),daemon=True).start()
with socket.socket() as server:
    server.setsockopt(socket.SOL_SOCKET,socket.SO_REUSEADDR,1); server.bind((bind_host,listen_port)); server.listen(32)
    while True:
        conn,_=server.accept(); threading.Thread(target=serve,args=(conn,),daemon=True).start()
PYFORWARD
chmod 700 "$ROOT/port-forward.py"
# The host-side relay enters the application network namespace first so the
# Spring request remote address remains loopback.  The second relay exposes
# that namespace-local port on the host for the SSH tunnel.  A direct host to
# container relay makes isolated HTTP writes fail closed because Tomcat sees a
# Docker gateway address instead of 127.0.0.1.
APP_PID="$(docker inspect "$APP" -f '{{.State.Pid}}')"
[[ "$APP_PID" =~ ^[0-9]+$ && "$APP_PID" -gt 0 ]] || { echo "owned app host pid unavailable" >&2; exit 4; }
nsenter -n -t "$APP_PID" nohup python3 "$ROOT/port-forward.py" "$PORT" 127.0.0.1 8080 0.0.0.0 > "$ROOT/port-forward-app.log" 2>&1 < /dev/null &
echo $! > "$ROOT/port-forward-app.pid"
nohup python3 "$ROOT/port-forward.py" "$PORT" "$APP_IP" "$PORT" > "$ROOT/port-forward.log" 2>&1 < /dev/null &
echo $! > "$ROOT/port-forward.pid"
ready=0
for _ in $(seq 1 120); do
  code="$(curl -sS -o /dev/null -w '%{http_code}' --max-time 3 "http://127.0.0.1:${PORT}/open/security/public-key" 2>/dev/null || true)"
  if [[ "$code" == 200 ]]; then ready=1; break; fi
  sleep 2
done
[[ "$ready" == 1 ]] || { echo "owned app did not become ready" >&2; exit 4; }
cat > "$ROOT/app-refresh-report.json" <<EOF
{"schema":1,"status":"diagnostic-running","execution":"app-only-refresh","production_access":false,"source_commit":"$SOURCE_COMMIT","source_dirty":$([[ "$SOURCE_DIRTY" == 1 ]] && echo true || echo false),"container":"$APP","loopback_url":"http://127.0.0.1:$PORT","session_cookie_name":"OWNED_SSS2_SESSION","reused_oracle":"$ORA","reused_redis":"$REDIS","oracle_restarted":false,"redis_restarted":false,"supplier_18081_preserved":true,"debug_18088_preserved":true,"loopback_namespace_relay":true,"boundary_process":false,"internal_http_bridge":false,"strict_evidence":false}
EOF
rm -f "$ROOT/incoming-owned/distribution.tgz"; chmod 600 "$ROOT/app-refresh-report.json"
echo "owned sss2 app refreshed on 127.0.0.1:$PORT source=$SOURCE_COMMIT"
REMOTE

echo "owned sss2 app refresh complete on $SSH_HOST:127.0.0.1:$HOST_PORT"
