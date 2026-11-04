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
if p.get('oracle_reused') is True:
    for key in ('baseline_schema','runtime_schema','baseline_schema_hash',
                'baseline_key_data_hash','runtime_schema_hash_before',
                'runtime_key_data_hash_before'):
        print(p[key])
PY
)
REMOTE_ROOT="${VALUES[0]}"; NET="${VALUES[1]}"; VOL="${VALUES[2]}"; APP="${VALUES[3]}"; ORA="${VALUES[4]}"; REDIS="${VALUES[5]}"; MODE="${VALUES[6]}"
[[ -n "$REMOTE_ROOT" ]] || { echo 'manifest lacks remote_root' >&2; exit 2; }
if [[ "$MODE" == persistent ]]; then
  BASELINE="${VALUES[7]}"; RUNTIME="${VALUES[8]}"; BASE_SCHEMA_HASH="${VALUES[9]}"
  BASE_KEY_HASH="${VALUES[10]}"; RUNTIME_SCHEMA_HASH="${VALUES[11]}"; RUNTIME_KEY_HASH="${VALUES[12]}"
  ssh -o BatchMode=yes -o ConnectTimeout=5 "$SSH_HOST" bash -s -- "$REMOTE_ROOT" "$APP" "$ORA" "$REDIS" "$NET" "$VOL" \
    "$BASELINE" "$RUNTIME" "$BASE_SCHEMA_HASH" "$BASE_KEY_HASH" "$RUNTIME_SCHEMA_HASH" "$RUNTIME_KEY_HASH" <<'REMOTE_PERSISTENT'
set -euo pipefail
ROOT="$1"; APP="$2"; ORA="$3"; REDIS="$4"; NET="$5"; VOL="$6"
BASELINE="$7"; RUNTIME="$8"; BASE_SCHEMA_HASH="$9"; BASE_KEY_HASH="${10}"
RUNTIME_SCHEMA_HASH="${11}"; RUNTIME_KEY_HASH="${12}"
for file in "$ROOT/port-forward.pid" "$ROOT/port-forward-app.pid"; do
  if [[ -f "$file" ]]; then pid="$(cat "$file" 2>/dev/null || true)"; [[ "$pid" =~ ^[0-9]+$ ]] && kill "$pid" 2>/dev/null || true; fi
done
docker inspect "$APP" >/dev/null 2>&1 && docker rm --force "$APP" >/dev/null || true
sql(){ { printf '%s\n' 'alter session set container = XEPDB1;'; cat; } | docker exec -i "$ORA" /opt/oracle/product/21c/dbhomeXE/bin/sqlplus -s / as sysdba; }
hash_schema(){ printf "set pagesize 0 feedback off heading off echo off verify off trimspool on\nselect table_name||'|'||column_id||'|'||column_name||'|'||data_type from all_tab_columns where owner=upper('$1') order by table_name,column_id;\nselect table_name||'|'||constraint_name||'|'||constraint_type from all_constraints where owner=upper('$1') order by table_name,constraint_name;\nexit\n" | sql | sha256sum | awk '{print $1}'; }
key_hash(){ printf "set pagesize 0 feedback off heading off echo off verify off trimspool on\nselect table_name||'|'||nvl(to_char(num_rows),'NULL') from all_tables where owner=upper('$1') order by table_name;\nexit\n" | sql | sha256sum | awk '{print $1}'; }
BASE_SCHEMA_AFTER="$(hash_schema "$BASELINE")"; RUNTIME_SCHEMA_AFTER="$(hash_schema "$RUNTIME")"
BASE_KEY_AFTER="$(key_hash "$BASELINE")"; RUNTIME_KEY_AFTER="$(key_hash "$RUNTIME")"
rm -rf "$ROOT"
TEMPORARY_FILES_REMOVED=false; [[ ! -e "$ROOT" ]] && TEMPORARY_FILES_REMOVED=true
python3 - "$APP" "$ORA" "$REDIS" "$NET" "$VOL" "$BASE_SCHEMA_HASH" "$BASE_SCHEMA_AFTER" \
  "$BASE_KEY_HASH" "$BASE_KEY_AFTER" "$RUNTIME_SCHEMA_HASH" "$RUNTIME_SCHEMA_AFTER" \
  "$RUNTIME_KEY_HASH" "$RUNTIME_KEY_AFTER" "$TEMPORARY_FILES_REMOVED" <<'PY'
import json,subprocess,sys
(app,ora,redis,net,vol,base_schema_before,base_schema_after,base_key_before,
 base_key_after,runtime_schema_before,runtime_schema_after,runtime_key_before,
 runtime_key_after,temporary_files_removed)=sys.argv[1:]
def absent(kind,name):
    cmd=['docker',kind,'inspect',name]
    return subprocess.run(cmd,stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL).returncode != 0
def running(name):
    value=subprocess.check_output(['docker','inspect',name,'--format','{{.State.Status}}'],
                                  universal_newlines=True).strip()
    return value == 'running'
runtime_restored = runtime_key_after == runtime_key_before == base_key_after
baseline_immutable = base_key_before == base_key_after and base_schema_before == base_schema_after
print(json.dumps({
 'cleanup_mode':'persistent-oracle',
 'application_stopped':absent('container',app),
 'oracle_stopped':not running(ora),
 'redis_stopped':not running(redis),
 'redis_preserved':running(redis),
 'oracle_reused':True,
 'oracle_preserved':running(ora),
 'baseline_schema_immutable':baseline_immutable,
 'runtime_schema_restored':runtime_restored,
 'runtime_dirty':not runtime_restored,
 'network_preserved':not absent('network',net),
 'volume_preserved':not absent('volume',vol),
 'temporary_files_removed':temporary_files_removed == 'true',
 'application_absent':absent('container',app),
 'baseline_schema_hash_after':base_schema_after,
 'baseline_key_data_hash_after':base_key_after,
 'runtime_schema_hash_after':runtime_schema_after,
 'runtime_key_data_hash_after':runtime_key_after,
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
