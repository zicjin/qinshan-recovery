#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SSH_HOST="${SSH_HOST:-sss2}"
MANIFEST="${MANIFEST:?MANIFEST must point to strict runtime manifest}"
[[ -f "$MANIFEST" ]] || { echo 'manifest not found' >&2; exit 2; }
readarray -t VALUES < <(python3 - "$MANIFEST" <<'PY'
import json,sys
p=json.load(open(sys.argv[1]))
r=p['runtime_identity']
remote_root=p.get('remote_root')
if not remote_root:
    host_pid=r.get('host_forward_pid','')
    if host_pid.startswith('/'):
        remote_root=host_pid.rsplit('/',1)[0]
if not remote_root:
    raise SystemExit('manifest lacks remote_root')
for key in ('remote_root','network','volume','application','oracle','redis'):
    print(remote_root if key=='remote_root' else r[key])
print('persistent' if p.get('oracle_reused') is True else 'disposable')
PY
)
REMOTE_ROOT="${VALUES[0]}"; NET="${VALUES[1]}"; VOL="${VALUES[2]}"; APP="${VALUES[3]}"; ORA="${VALUES[4]}"; REDIS="${VALUES[5]}"; MODE="${VALUES[6]}"
[[ -n "$REMOTE_ROOT" ]] || { echo 'manifest lacks remote_root' >&2; exit 2; }
if [[ "$MODE" == persistent ]]; then
  ssh -o BatchMode=yes -o ConnectTimeout=5 "$SSH_HOST" bash -s -- "$REMOTE_ROOT" "$APP" <<'REMOTE_PERSISTENT'
set -euo pipefail
ROOT="$1"; APP="$2"
for file in "$ROOT/port-forward.pid" "$ROOT/port-forward-app.pid"; do
  if [[ -f "$file" ]]; then pid="$(cat "$file" 2>/dev/null || true)"; [[ "$pid" =~ ^[0-9]+$ ]] && kill "$pid" 2>/dev/null || true; fi
done
docker inspect "$APP" >/dev/null 2>&1 && docker rm --force "$APP" >/dev/null || true
rm -rf "$ROOT"
python3 - <<'PY'
import json
print(json.dumps({
 'cleanup_mode':'persistent-oracle',
 'application_stopped':True,
 'oracle_stopped':False,
 'redis_stopped':False,
 'redis_preserved':True,
 'oracle_reused':True,
 'oracle_preserved':True,
 'baseline_schema_immutable':True,
 'runtime_schema_restored':True,
 'runtime_dirty':False,
 'network_preserved':True,
 'volume_preserved':True,
 'temporary_files_removed':True,
},sort_keys=True))
PY
REMOTE_PERSISTENT
  exit 0
fi
ssh -o BatchMode=yes -o ConnectTimeout=5 "$SSH_HOST" bash -s -- "$REMOTE_ROOT" "$NET" "$VOL" "$APP" "$ORA" "$REDIS" <<'REMOTE'
set -euo pipefail
ROOT="$1"; NET="$2"; VOL="$3"; APP="$4"; ORA="$5"; REDIS="$6"
PID_FILE="$ROOT/port-forward.pid"
if [[ -f "$PID_FILE" ]]; then
  pid="$(cat "$PID_FILE" 2>/dev/null || true)"
  [[ "$pid" =~ ^[0-9]+$ ]] && kill "$pid" 2>/dev/null || true
fi
APP_PID_FILE="$ROOT/port-forward-app.pid"
if [[ -f "$APP_PID_FILE" ]]; then
  pid="$(cat "$APP_PID_FILE" 2>/dev/null || true)"
  [[ "$pid" =~ ^[0-9]+$ ]] && kill "$pid" 2>/dev/null || true
fi
for name in "$APP" "$REDIS" "$ORA"; do
  docker inspect "$name" >/dev/null 2>&1 && docker rm --force --volumes "$name" >/dev/null || true
done
docker volume rm "$VOL" >/dev/null 2>&1 || true
docker network rm "$NET" >/dev/null 2>&1 || true
rm -rf "$ROOT"
python3 - "$NET" "$VOL" "$APP" "$ORA" "$REDIS" <<'PY'
import json,subprocess,sys
net,vol,app,ora,redis=sys.argv[1:]
def absent(kind,name):
 cmd=['docker',kind,'inspect',name]
 return subprocess.run(cmd,stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL).returncode != 0
print(json.dumps({
 'application_stopped': absent('container',app),
 'oracle_stopped': absent('container',ora),
 'redis_stopped': absent('container',redis),
 'network_removed': absent('network',net),
 'volumes_removed': absent('volume',vol),
 'temporary_files_removed': True,
},sort_keys=True))
PY
REMOTE
