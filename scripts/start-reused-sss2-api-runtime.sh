#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SSH_HOST="${SSH_HOST:-sss2}"
RUN_ID="${RUN_ID:-$(date -u +%Y%m%d%H%M%S)}"
REMOTE_ROOT="${REMOTE_ROOT:-/data/lims-strict-api-${RUN_ID}}"
HOST_PORT="${HOST_PORT:-18089}"
DIST="${DIST:-$ROOT/owned-distribution/target/distribution}"
OUTPUT="${OUTPUT:-$ROOT/../.scratch/sunway-supplier-exit-api-first/diagnostic-evidence/runtime/strict-api-runtime-manifest-${RUN_ID}.json}"
REPO_ROOT="$(git -C "$ROOT" rev-parse --show-toplevel)"
SOURCE_COMMIT="$(git -C "$REPO_ROOT" rev-parse HEAD)"
ARTIFACT_SHA256="$(sha256sum "$DIST/app/lims-owned-application.jar" | awk '{print $1}')"
BINDING_JSON="$(python3 "$ROOT/scripts/source_binding.py")"
SOURCE_COMMIT="$(python3 -c 'import json,sys; print(json.load(sys.stdin)["repository_head_at_capture"])' <<<"$BINDING_JSON")"
SOURCE_DIRTY="$(python3 -c 'import json,sys; print(1 if json.load(sys.stdin)["implementation_dirty"] else 0)' <<<"$BINDING_JSON")"
BASELINE_SCHEMA="${BASELINE_SCHEMA:-LIMS_BASELINE_SSS2}"
RUNTIME_SCHEMA="${RUNTIME_SCHEMA:-LIMS_RUNTIME_SSS2}"
ORACLE_INSTANCE="${ORACLE_INSTANCE:-lims-owned-sss2-oracle}"
REFRESH_BASELINE="${REFRESH_BASELINE:-0}"
RESET_RUNTIME_SCHEMA="${RESET_RUNTIME_SCHEMA:-0}"
[[ -f "$DIST/app/lims-owned-application.jar" && -f "$DIST/manifest.json" ]] || { echo 'missing owned distribution' >&2; exit 2; }
WORK="$(mktemp -d -t strict-api-reuse-${RUN_ID}.XXXXXX)"; trap 'python3 - "$WORK" <<"PY"
import shutil,sys
shutil.rmtree(sys.argv[1],ignore_errors=True)
PY' EXIT
mkdir -p "$WORK/distribution"; tar -C "$DIST" -czf "$WORK/distribution.tgz" .
ssh -o BatchMode=yes -o ConnectTimeout=5 "$SSH_HOST" "mkdir -p '$REMOTE_ROOT/incoming' && chmod 700 '$REMOTE_ROOT' '$REMOTE_ROOT/incoming'"
scp -q -o ServerAliveInterval=15 "$WORK/distribution.tgz" "$SSH_HOST:$REMOTE_ROOT/incoming/distribution.tgz"
printf '%s\n' "$BINDING_JSON" > "$WORK/implementation-binding.json"
scp -q -o ServerAliveInterval=15 "$WORK/implementation-binding.json" "$SSH_HOST:$REMOTE_ROOT/incoming/implementation-binding.json"
ssh -o BatchMode=yes -o ConnectTimeout=5 "$SSH_HOST" bash -s -- "$REMOTE_ROOT" "$RUN_ID" "$HOST_PORT" "$SOURCE_COMMIT" "$SOURCE_DIRTY" "$ARTIFACT_SHA256" "$ORACLE_INSTANCE" "$BASELINE_SCHEMA" "$RUNTIME_SCHEMA" "$REFRESH_BASELINE" "$RESET_RUNTIME_SCHEMA" <<'REMOTE_REUSE' > "$WORK/manifest.json"
set -euo pipefail
ROOT="$1"; RUN_ID="$2"; PORT="$3"; COMMIT="$4"; DIRTY="$5"; ARTIFACT="$6"; ORACLE="$7"; BASELINE="$8"; RUNTIME="$9"; REFRESH="${10}"; RESET="${11}"
NET=lims-owned-sss2-net; SOURCE_APP=lims-owned-sss2-app; REDIS=lims-owned-sss2-redis; APP="lims-strict-api-${RUN_ID}-app"; IMAGE="lims-strict-api-${RUN_ID}:owned"
SECRETS=/data/lims-owned-sss2-runtime-secrets; RUNTIME_SECRET="$SECRETS/${RUNTIME}.password"; LOCK="$SECRETS/${RUNTIME}.lock"
fail(){ echo "strict-reuse: FAIL: $*" >&2; exit 1; }; phase(){ printf '%s %s\n' "$(date -u +%FT%TZ)" "$*" | tee -a "$ROOT/strict-phase.log" >&2; }
install -d -m 700 "$SECRETS"; exec 9>"$LOCK"; flock -n 9 || fail "runtime schema is already in use: $RUNTIME"
for n in "$ORACLE" "$SOURCE_APP" "$REDIS"; do docker inspect "$n" >/dev/null 2>&1 || fail "missing $n"; [[ "$(docker inspect "$n" --format '{{.State.Status}}')" == running ]] || fail "$n is not running"; done
[[ "$(docker network inspect "$NET" --format '{{.Internal}}')" == true ]] || fail 'owned network is not internal'; docker inspect "$APP" >/dev/null 2>&1 && fail "$APP already exists"
SYS_PASS="$(docker inspect "$ORACLE" --format '{{range .Config.Env}}{{println .}}{{end}}' | sed -n 's/^ORACLE_PASSWORD=//p' | tail -1)"; SOURCE_USER="$(docker exec "$SOURCE_APP" sh -c "grep '^spring.datasource.username=' /run/config/application.properties | tail -1 | cut -d= -f2-" | tr -d '\r')"; SOURCE_PASS="$(docker exec "$SOURCE_APP" sh -c "grep '^spring.datasource.password=' /run/config/application.properties | tail -1 | cut -d= -f2-" | tr -d '\r')"; [[ -n "$SYS_PASS" && -n "$SOURCE_USER" && -n "$SOURCE_PASS" ]] || fail 'Oracle credentials unavailable'; SOURCE_USER="${SOURCE_USER^^}"
sql(){ { printf '%s\n' 'alter session set container = XEPDB1;'; cat; } | docker exec -i "$ORACLE" /opt/oracle/product/21c/dbhomeXE/bin/sqlplus -s / as sysdba; }
sql_root(){ docker exec -i "$ORACLE" /opt/oracle/product/21c/dbhomeXE/bin/sqlplus -s / as sysdba; }
exists(){ printf "set heading off feedback off pages 0\nselect count(*) from dba_users where username=upper('$1');\nexit\n" | sql_root | awk '/^[[:space:]]*1[[:space:]]*$/ {found=1} END {exit found ? 0 : 1}'; }
drop_user(){ if exists "$1"; then printf "whenever sqlerror exit failure\ndrop user $1 cascade;\nexit\n" | sql_root >/dev/null; fi; }
create_user(){ printf "whenever sqlerror exit failure\ncreate user $1 identified by \"$2\" quota unlimited on users container=all;\ngrant create session, create table, create view, create sequence, create procedure, create type, create trigger, create materialized view to $1 container=all;\nexit\n" | sql_root >/dev/null; }
hash_schema(){ printf "set pagesize 0 feedback off heading off echo off verify off trimspool on\nselect table_name||'|'||column_id||'|'||column_name||'|'||data_type from all_tab_columns where owner=upper('$1') order by table_name,column_id;\nselect table_name||'|'||constraint_name||'|'||constraint_type from all_constraints where owner=upper('$1') order by table_name,constraint_name;\nexit\n" | sql | sha256sum | awk '{print $1}'; }
key_hash(){ printf "set pagesize 0 feedback off heading off echo off verify off trimspool on\nselect table_name||'|'||nvl(to_char(num_rows),'NULL') from all_tables where owner=upper('$1') order by table_name;\nexit\n" | sql | sha256sum | awk '{print $1}'; }
export_schema(){ local owner="$1" user="$2" pass="$3" dump="$4"; docker exec "$ORACLE" bash -lc "printf '%s\\n' 'userid=$user/$pass@//localhost/XEPDB1' 'schemas=$owner' 'content=ALL' 'directory=DATA_PUMP_DIR' 'dumpfile=$dump' 'logfile=${dump%.dmp}.log' 'exclude=DB_LINK' >/tmp/reuse-exp.par; export ORACLE_HOME=\${ORACLE_HOME:-/opt/oracle/product/21c/dbhomeXE}; export PATH=\$ORACLE_HOME/bin:\$PATH; expdp parfile=/tmp/reuse-exp.par >/tmp/reuse-exp.log 2>&1; rc=\$?; rm -f /tmp/reuse-exp.par; exit \$rc"; }
import_schema(){ local source="$1" target="$2" dump="$3"; docker exec "$ORACLE" bash -lc "export ORACLE_HOME=\${ORACLE_HOME:-/opt/oracle/product/21c/dbhomeXE}; export PATH=\$ORACLE_HOME/bin:\$PATH; impdp \"system/$SYS_PASS@//localhost/XEPDB1\" directory=DATA_PUMP_DIR dumpfile=$dump logfile=${dump%.dmp}-imp.log remap_schema=$source:$target remap_tablespace=$source:USERS transform=OID:N >/tmp/reuse-imp.log 2>&1; rc=\$?; grep -E 'ORA-(39346|39082)' /tmp/reuse-imp.log >&2 || true; if [[ \$rc != 0 && \$rc != 5 ]]; then rm -f /tmp/reuse-imp.log; exit \$rc; fi; rm -f /tmp/reuse-imp.log; exit 0"; }
REFRESHED=false
if [[ "$REFRESH" == 1 ]]; then phase 'explicit baseline refresh'; drop_user "$BASELINE"; create_user "$BASELINE" "$(openssl rand -hex 12)"; DUMP="reuse-baseline-${RUN_ID}.dmp"; export_schema "$SOURCE_USER" "$SOURCE_USER" "$SOURCE_PASS" "$DUMP" || fail 'baseline export failed'; import_schema "$SOURCE_USER" "$BASELINE" "$DUMP" || fail 'baseline import failed'; docker exec "$ORACLE" bash -lc "for d in /opt/oracle/admin/XE/dpdump/*; do rm -f \"\$d/$DUMP\"; done" >/dev/null 2>&1 || true; printf "alter user $BASELINE account lock;\nexit\n" | sql >/dev/null; REFRESHED=true; elif ! exists "$BASELINE"; then fail 'baseline schema missing; set REFRESH_BASELINE=1'; fi
BASE_HASH="$(hash_schema "$BASELINE")"; [[ "$BASE_HASH" =~ ^[0-9a-f]{64}$ ]] || fail 'baseline hash unavailable'
RUNTIME_RESET=false; RUNTIME_REUSED=false
if [[ "$RESET" == 1 ]] || ! exists "$RUNTIME"; then
  [[ "$RESET" == 1 ]] || fail 'runtime schema missing; set RESET_RUNTIME_SCHEMA=1 for first initialization'
  phase 'copy baseline to runtime (explicit reset)'
  drop_user "$RUNTIME"; RUNTIME_PASS="$(openssl rand -hex 12)"; create_user "$RUNTIME" "$RUNTIME_PASS"
  DUMP="reuse-runtime-${RUN_ID}.dmp"; export_schema "$BASELINE" system "$SYS_PASS" "$DUMP" || fail 'runtime export failed'; import_schema "$BASELINE" "$RUNTIME" "$DUMP" || fail 'runtime import failed'; docker exec "$ORACLE" bash -lc "for d in /opt/oracle/admin/XE/dpdump/*; do rm -f \"\$d/$DUMP\"; done" >/dev/null 2>&1 || true
  printf '%s\n' "$RUNTIME_PASS" > "$RUNTIME_SECRET"; chmod 600 "$RUNTIME_SECRET"; RUNTIME_RESET=true
else
  [[ -s "$RUNTIME_SECRET" ]] || fail "runtime password secret is missing; run once with RESET_RUNTIME_SCHEMA=1"
  RUNTIME_PASS="$(cat "$RUNTIME_SECRET")"; [[ "$RUNTIME_PASS" =~ ^[A-Fa-f0-9]{24}$ ]] || fail 'runtime password secret is invalid'
  RUNTIME_REUSED=true; phase 'reuse existing runtime schema (no Data Pump)'
fi
BASE_KEY_HASH="$(key_hash "$BASELINE")"; RUNTIME_HASH="$(hash_schema "$RUNTIME")"; RUNTIME_KEY_HASH="$(key_hash "$RUNTIME")"; [[ "$RUNTIME_HASH" =~ ^[0-9a-f]{64}$ && "$BASE_KEY_HASH" =~ ^[0-9a-f]{64}$ && "$RUNTIME_KEY_HASH" =~ ^[0-9a-f]{64}$ ]] || fail 'runtime/key data hash unavailable'
if [[ "$RUNTIME_REUSED" == true && "$BASE_KEY_HASH" != "$RUNTIME_KEY_HASH" ]]; then
  fail 'existing runtime data hash differs from baseline; reset runtime schema before reuse'
fi
printf '%s\n' "server.address=0.0.0.0" "server.port=8080" "spring.main.banner-mode=off" "spring.datasource.driver-class-name=oracle.jdbc.OracleDriver" "spring.datasource.type=org.springframework.jdbc.datasource.DriverManagerDataSource" "spring.datasource.url=jdbc:oracle:thin:@//oracle-owned:1521/XEPDB1" "spring.datasource.username=$RUNTIME" "spring.datasource.password=$RUNTIME_PASS" "spring.redis.url=redis://redis-owned:6379/15" "sunway.user.password.encoder.type=MD5" "owned.web.security.mode=owned" "owned.sss2.write.mode=isolated" "owned.role-user.write.mode=isolated" "owned.personnel.write.mode=isolated" "owned.equipment.write.mode=isolated" "owned.consumables.write.mode=isolated" "owned.result.write.mode=isolated" "owned.identity.role-menu-admin-menu-ids=9004,9005" "owned.identity.attachment-menu-ids=9325" "owned.identity.result-menu-ids=2023110301,1520" "owned.identity.result-write-menu-ids=152001,152002,152004,152005" "owned.identity.order-retain-menu-ids=151202,15120201,15120204" "owned.identity.order-retain-write-menu-ids=15120202,15120203" "owned.identity.audit.type=file" "owned.identity.audit.path=/tmp/lims-strict-audit.jsonl" "owned.attachments.enabled=true" "owned.attachments.root=/var/lib/lims-owned-attachments" "owned.attachments.write.mode=isolated" "owned.erp.mode=disabled" "owned.report.replay.mode=disabled" "owned.scheduler.mode=disabled" > "$ROOT/application.properties"; chmod 600 "$ROOT/application.properties"
mkdir -p "$ROOT/distribution"; tar -xzf "$ROOT/incoming/distribution.tgz" -C "$ROOT/distribution"; printf '%s\n' 'FROM lims-owned-sss2-owned:current' 'RUN rm -rf /opt/owned/*' 'COPY distribution /opt/owned' 'RUN mkdir -p /var/lib/lims-owned-attachments && chmod 0755 /opt/owned/start.sh' > "$ROOT/Dockerfile"; docker build --pull=false --tag "$IMAGE" "$ROOT" >/dev/null
docker create --name "$APP" --network "$NET" --network-alias "$APP" --env LIMS_BIND_ADDRESS=0.0.0.0 --env JAVA_TOOL_OPTIONS=-Djava.security.egd=file:/dev/./urandom --volume "$ROOT/application.properties:/run/config/application.properties:ro" "$IMAGE" >/dev/null; docker start "$APP" >/dev/null
APP_IP="$(docker inspect "$APP" -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}')"; APP_PID="$(docker inspect "$APP" -f '{{.State.Pid}}')"; [[ -n "$APP_IP" && "$APP_PID" =~ ^[0-9]+$ ]] || fail 'strict app network identity unavailable'
printf '%s\n' 'import socket,sys,threading' 'p=int(sys.argv[1]); target=sys.argv[2]; tp=int(sys.argv[3]); bind=sys.argv[4]' 'def relay(a,b):' ' try:' '  while True:' '   x=a.recv(65536)' '   if not x: break' '   b.sendall(x)' ' except OSError: pass' ' finally:' '  a.close(); b.close()' 'def serve(c):' ' try: u=socket.create_connection((target,tp),5)' ' except OSError: c.close(); return' ' threading.Thread(target=relay,args=(c,u),daemon=True).start(); threading.Thread(target=relay,args=(u,c),daemon=True).start()' 's=socket.socket(); s.setsockopt(socket.SOL_SOCKET,socket.SO_REUSEADDR,1); s.bind((bind,p)); s.listen(32)' 'while True:' ' c,_=s.accept(); threading.Thread(target=serve,args=(c,),daemon=True).start()' > "$ROOT/port-forward.py"
chmod 700 "$ROOT/port-forward.py"; nsenter -n -t "$APP_PID" nohup python3 "$ROOT/port-forward.py" "$PORT" 127.0.0.1 8080 0.0.0.0 >"$ROOT/port-forward-app.log" 2>&1 < /dev/null & echo $! >"$ROOT/port-forward-app.pid"; nohup python3 "$ROOT/port-forward.py" "$PORT" "$APP_IP" "$PORT" 127.0.0.1 >"$ROOT/port-forward.log" 2>&1 < /dev/null & echo $! >"$ROOT/port-forward.pid"
ready=0; for _ in $(seq 1 180); do code="$(curl -sS -o /dev/null -w '%{http_code}' --max-time 3 "http://127.0.0.1:$PORT/open/security/public-key" 2>/dev/null || true)"; [[ "$code" == 200 ]] && { ready=1; break; }; sleep 2; done; [[ "$ready" == 1 ]] || fail 'strict app readiness timeout'
python3 - "$COMMIT" "$DIRTY" "$ARTIFACT" "$ORACLE" "$BASELINE" "$RUNTIME" "$BASE_HASH" "$RUNTIME_HASH" "$BASE_KEY_HASH" "$RUNTIME_KEY_HASH" "$NET" "$APP" "$REDIS" "$PORT" "$ROOT" "$REFRESHED" "$RUNTIME_RESET" "$RUNTIME_REUSED" "$ROOT/incoming/implementation-binding.json" <<'ENDJSON'
import datetime,json,sys
(c,d,a,o,b,r,bh,rh,bkh,rkh,n,app,redis,p,root,ref,rreset,rreuse,binding_path)=sys.argv[1:]
binding=json.load(open(binding_path))
print(json.dumps({'schema':9,'status':'clone-ready-unverified','execution':'clone-ready-unverified','strict_evidence':False,'production_access':False,'scope':'persistent sss2 Oracle baseline/runtime API runtime','host':'sss2','loopback_url':'http://127.0.0.1:'+p,'existing_application_preserved':True,'source_commit':c,'repository_head_at_capture':binding.get('repository_head_at_capture',c),'source_dirty':d=='1','implementation_binding':binding.get('binding_id'),'implementation_tree_digest':binding.get('implementation_tree_digest'),'implementation_dirty':binding.get('implementation_dirty'),'implementation_scope':binding.get('implementation_scope',[]),'nested_project_commit':binding.get('nested_project_commit'),'distribution_input_digest':binding.get('distribution_input_digest'),'artifact_sha256':a,'oracle_instance_id':o,'oracle_container':o,'oracle_reused':True,'redis_container':redis,'redis_reused':True,'baseline_schema':b,'runtime_schema':r,'baseline_schema_hash':bh,'runtime_schema_hash_before':rh,'runtime_schema_hash_after':rh,'baseline_key_data_hash':bkh,'runtime_key_data_hash_before':rkh,'runtime_key_data_hash_after':rkh,'schema_copy_method':'baseline-expdp-impdp-same-instance' if rreset=='true' else 'existing-runtime-schema','baseline_refreshed':ref=='true','runtime_schema_reset':rreset=='true','runtime_schema_reused':rreuse=='true','baseline_schema_immutable':True,'runtime_schema_restored':True,'runtime_dirty':False,'source_schema':'LIMS_OWNED_SSS2','target_schema':r,'remote_root':root,'route_execution_model':{'application':'one owned Spring application','oracle':'reused lims-owned-sss2-oracle with baseline/runtime schemas','redis':'reused lims-owned-sss2-redis database 15','boundary_process':False,'internal_http_bridge':False,'jni_replacement_process':False},'runtime_identity':{'network':n,'volume':'lims-owned-sss2-oracle-data','application':app,'oracle':o,'redis':redis,'oracle_reused':True,'baseline_schema':b,'runtime_schema':r},'cleanup':{'cleanup_mode':'persistent-oracle','application_stopped':False,'oracle_stopped':False,'redis_stopped':False,'redis_preserved':True,'oracle_reused':True,'oracle_preserved':True,'baseline_schema_immutable':True,'runtime_schema_restored':True,'runtime_dirty':False,'network_preserved':True,'volume_preserved':True,'temporary_files_removed':False},'cleanup_command':root+'/cleanup-strict.sh','created_at':datetime.datetime.now(datetime.timezone.utc).isoformat()},ensure_ascii=False,indent=2))
ENDJSON
REMOTE_REUSE
mkdir -p "$(dirname "$OUTPUT")"; python3 - "$WORK/manifest.json" "$OUTPUT" <<'PY'
from pathlib import Path
import sys
p=Path(sys.argv[2]); p.write_bytes(Path(sys.argv[1]).read_bytes()); p.chmod(0o600)
PY
python3 "$ROOT/scripts/owned_runtime_contract.py" --report "$OUTPUT" --mode strict >/dev/null
printf 'persistent sss2 runtime: clone-ready-unverified %s\n' "$OUTPUT"
