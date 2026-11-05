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

[[ "$RUN_ID" =~ ^[a-zA-Z0-9_-]+$ ]] || { echo 'invalid RUN_ID' >&2; exit 2; }
[[ "$HOST_PORT" =~ ^[0-9]+$ && "$HOST_PORT" -ge 1024 && "$HOST_PORT" -le 65535 ]] || { echo 'invalid HOST_PORT' >&2; exit 2; }
[[ -f "$DIST/app/lims-owned-application.jar" && -f "$DIST/manifest.json" ]] || { echo 'missing owned distribution' >&2; exit 2; }

# Persistent Oracle is the default.  Keep the historical disposable runtime
# only as an explicit opt-out for migration diagnostics.
if [[ "${REUSE_ORACLE:-1}" == "1" ]]; then
  exec "$ROOT/scripts/start-reused-sss2-api-runtime.sh"
fi

WORK="$(mktemp -d -t strict-api-${RUN_ID}.XXXXXX)"
trap 'rm -rf "$WORK"' EXIT
mkdir -p "$WORK/distribution"
tar -C "$DIST" -czf "$WORK/distribution.tgz" .
ssh -o BatchMode=yes -o ConnectTimeout=5 -o ServerAliveInterval=15 -o ServerAliveCountMax=20 "$SSH_HOST" "mkdir -p '$REMOTE_ROOT/incoming' && chmod 700 '$REMOTE_ROOT' '$REMOTE_ROOT/incoming'"
scp -q -o ServerAliveInterval=15 -o ServerAliveCountMax=20 "$WORK/distribution.tgz" "$SSH_HOST:$REMOTE_ROOT/incoming/distribution.tgz"

ssh -o BatchMode=yes -o ConnectTimeout=5 -o ServerAliveInterval=15 -o ServerAliveCountMax=20 "$SSH_HOST" bash -s -- \
  "$REMOTE_ROOT" "$RUN_ID" "$HOST_PORT" "$SOURCE_COMMIT" "$SOURCE_DIRTY" "$ARTIFACT_SHA256" "$BINDING_JSON" <<'REMOTE' > "$WORK/manifest.json"
set -euo pipefail
ROOT="$1"; RUN_ID="$2"; PORT="$3"; SOURCE_COMMIT="$4"; SOURCE_DIRTY="$5"; ARTIFACT_SHA256="$6"; BINDING_JSON="$7"
NET="lims-strict-api-${RUN_ID}-net"
ORA="lims-strict-api-${RUN_ID}-oracle"
REDIS="lims-strict-api-${RUN_ID}-redis"
APP="lims-strict-api-${RUN_ID}-app"
VOL="lims-strict-api-${RUN_ID}-oracle-data"
IMAGE="lims-strict-api-${RUN_ID}:owned"
SOURCE_ORA=lims-owned-sss2-oracle
SOURCE_APP=lims-owned-sss2-app
TARGET_CONFIG="$ROOT/application.properties"
DUMP_NAME="strict_${RUN_ID}.dmp"

fail(){ echo "strict-runtime: FAIL: $*" >&2; exit 1; }
PHASE_LOG="$ROOT/strict-phase.log"
phase(){ printf '%s %s\n' "$(date -u +%FT%TZ)" "$*" | tee -a "$PHASE_LOG" >&2; }
for n in "$SOURCE_ORA" "$SOURCE_APP" lims-owned-sss2-redis; do
  docker inspect "$n" >/dev/null 2>&1 || fail "missing existing container $n"
  [[ "$(docker inspect "$n" --format '{{.State.Status}}')" == running ]] || fail "$n is not running"
done
[[ "$(docker network inspect lims-owned-sss2-net --format '{{.Internal}}')" == true ]] || fail 'existing owned network is not internal'
docker image inspect lims-owned-sss2-owned:current >/dev/null 2>&1 || fail 'current base owned image is missing'
for n in "$APP" "$ORA" "$REDIS"; do docker inspect "$n" >/dev/null 2>&1 && fail "$n already exists" || true; done
docker network ls --format '{{.Name}}' | grep -Fxq "$NET" && fail "$NET already exists" || true

SOURCE_USER="$(docker exec "$SOURCE_APP" sh -c "grep '^spring.datasource.username=' /run/config/application.properties | tail -1 | cut -d= -f2-" | tr -d '\r')"
SOURCE_PASS="$(docker exec "$SOURCE_APP" sh -c "grep '^spring.datasource.password=' /run/config/application.properties | tail -1 | cut -d= -f2-" | tr -d '\r')"
[[ -n "$SOURCE_USER" && -n "$SOURCE_PASS" ]] || fail 'source datasource credentials unavailable'
SOURCE_SCHEMA="$SOURCE_USER"
SYS_PASS="$(openssl rand -hex 12)"; APP_PASS="$(openssl rand -hex 12)"
TARGET_USER="LIMS_STRICT_${RUN_ID^^}"; TARGET_USER="${TARGET_USER//[^A-Z0-9_]/_}"

cleanup_source(){
  local rc="$1"
  if [[ "$rc" == 0 && -f "$ROOT/start-success" ]]; then
    docker exec -e DUMP_NAME="$DUMP_NAME" "$SOURCE_ORA" sh -c 'for d in /opt/oracle/admin/XE/dpdump/*; do rm -f "$d/$DUMP_NAME"; done' >/dev/null 2>&1 || true
    rm -f "$ROOT/strict-export.par" "$ROOT/source-config.properties" "$ROOT/application.properties" "$ROOT/target-schema.sql" "$ROOT/port-forward.py"
  else
    echo "strict-runtime: failed; retaining $ROOT/export.log and source dump $DUMP_NAME for diagnosis" >&2
  fi
  return "$rc"
}
trap 'rc=$?; cleanup_source "$rc"; exit "$rc"' EXIT

docker network create --internal --driver bridge "$NET" >/dev/null
phase "create isolated network and Oracle volume"
docker volume create "$VOL" >/dev/null
docker run -d --name "$ORA" --network "$NET" --network-alias oracle-strict --restart no --shm-size 1g --volume "$VOL:/opt/oracle/oradata" -e ORACLE_PASSWORD="$SYS_PASS" gvenzl/oracle-xe:21-slim >/dev/null
ready=0
for _ in $(seq 1 180); do docker exec "$ORA" /opt/oracle/healthcheck.sh >/dev/null 2>&1 && { ready=1; break; }; sleep 3; done
[[ "$ready" == 1 ]] || fail 'strict Oracle readiness timeout'
phase "strict Oracle ready"
cat > "$ROOT/strict-schema.sql" <<SQL
whenever sqlerror exit failure
alter session set container = XEPDB1;
create user $TARGET_USER identified by "$APP_PASS" quota unlimited on users;
grant create session, create table, create view, create sequence, create procedure, create type, create trigger, create materialized view to $TARGET_USER;
exit
SQL
docker exec -i "$ORA" bash -lc 'export ORACLE_HOME=${ORACLE_HOME:-/opt/oracle/product/21c/dbhomeXE}; export PATH=$ORACLE_HOME/bin:$PATH; sqlplus -s / as sysdba' < "$ROOT/strict-schema.sql" > "$ROOT/schema.log" 2>&1 || { tail -40 "$ROOT/schema.log" >&2; fail 'strict target schema creation failed'; }
rm -f "$ROOT/strict-schema.sql"
phase "target schema created"

cat > "$ROOT/strict-export.par" <<EOF
userid=$SOURCE_USER/$SOURCE_PASS@//localhost/XEPDB1
schemas=$SOURCE_SCHEMA
content=ALL
directory=DATA_PUMP_DIR
dumpfile=$DUMP_NAME
logfile=${DUMP_NAME%.dmp}.log
exclude=DB_LINK
EOF
chmod 600 "$ROOT/strict-export.par"
set +e
docker exec -i "$SOURCE_ORA" bash -c 'umask 077; cat >/tmp/strict-export.par; export ORACLE_HOME=${ORACLE_HOME:-/opt/oracle/product/21c/dbhomeXE}; export PATH=$ORACLE_HOME/bin:$PATH; expdp parfile=/tmp/strict-export.par; rm -f /tmp/strict-export.par' < "$ROOT/strict-export.par" > "$ROOT/export.log" 2>&1
EXPORT_RC=$?
set -e
phase "source schema export complete rc=$EXPORT_RC"
[[ "$EXPORT_RC" == 0 ]] || { tail -80 "$ROOT/export.log" >&2; fail "strict source export failed (rc=$EXPORT_RC)"; }
phase "source schema export complete"
SRC_DUMP=""
for _ in $(seq 1 30); do
  SRC_DUMP="$(docker exec -e DUMP_NAME="$DUMP_NAME" "$SOURCE_ORA" bash -lc 'for d in /opt/oracle/admin/XE/dpdump/*; do if [ -f "$d/$DUMP_NAME" ]; then printf "%s" "$d/$DUMP_NAME"; break; fi; done' 2>/dev/null || true)"
  [[ -n "$SRC_DUMP" ]] && break
  sleep 1
done
[[ -n "$SRC_DUMP" ]] || fail 'strict source dump not found'
echo "strict-runtime: source dump path=$SRC_DUMP" >&2
echo "strict-runtime: source dump located in Data Pump directory" >&2
SOURCE_DUMP_SHA256="$(docker exec "$SOURCE_ORA" sha256sum "$SRC_DUMP" | awk '{print $1}')"
echo "strict-runtime: source dump hash computed" >&2
echo "strict-runtime: copying source dump from Oracle container" >&2
docker cp "$SOURCE_ORA:$SRC_DUMP" "$ROOT/$DUMP_NAME" || fail 'copying source dump to remote workspace failed'
TARGET_DUMP_DIR="/opt/oracle/admin/XE/dpdump"
SOURCE_DUMP_SUBDIR="${SRC_DUMP%/*}"; SOURCE_DUMP_SUBDIR="${SOURCE_DUMP_SUBDIR##*/}"
TARGET_DUMP_DIR="/opt/oracle/admin/XE/dpdump/$SOURCE_DUMP_SUBDIR"
docker exec "$ORA" mkdir -p "$TARGET_DUMP_DIR"
docker exec "$ORA" test -d "$TARGET_DUMP_DIR" || fail 'target Data Pump directory unavailable'
echo "strict-runtime: copying dump into target Data Pump directory" >&2
docker cp "$ROOT/$DUMP_NAME" "$ORA:$TARGET_DUMP_DIR/$DUMP_NAME" || fail 'copying dump into target Oracle failed'
echo "strict-runtime: dump copied; starting target import" >&2
docker exec -u 0 "$ORA" chown oracle:oinstall "$TARGET_DUMP_DIR/$DUMP_NAME"
docker exec -u 0 "$ORA" chmod 640 "$TARGET_DUMP_DIR/$DUMP_NAME"
docker exec "$ORA" test -r "$TARGET_DUMP_DIR/$DUMP_NAME" || fail 'target dump copy failed'
rm -f "$ROOT/$DUMP_NAME"
set +e
printf '%s\n' "$SYS_PASS" | docker exec -i "$ORA" bash -c 'IFS= read -r SYS_PASS; export PATH=${ORACLE_HOME:-/opt/oracle/product/21c/dbhomeXE}/bin:$PATH; impdp "system/$SYS_PASS@//localhost/XEPDB1" directory=DATA_PUMP_DIR dumpfile="$1" logfile=strict-import.log remap_schema="$2:$3" remap_tablespace="$2:USERS" transform=OID:N' -- "$DUMP_NAME" "$SOURCE_SCHEMA" "$TARGET_USER" > "$ROOT/import.log" 2>&1
IMPORT_RC=$?
set -e
IMPORT_WARNINGS="$(grep -Eo 'ORA-(39346|39082)' "$ROOT/import.log" | sort -u | tr '\n' ',' | sed 's/,$//' || true)"
if [[ "$IMPORT_RC" != 0 && -z "$IMPORT_WARNINGS" ]]; then tail -80 "$ROOT/import.log" >&2; fail "strict target import failed (rc=$IMPORT_RC)"; fi
phase "target import complete rc=$IMPORT_RC"
if [[ -n "$IMPORT_WARNINGS" ]]; then phase "known import warnings recorded"; fi
echo "strict-runtime: target import complete" >&2

metadata_hash(){
  local container="$1" user="$2" pass="$3"
  {
    printf 'connect %s/%s@//localhost/XEPDB1\n' "$user" "$pass"
    cat <<'SQL'
whenever sqlerror exit failure
set pagesize 0 feedback off heading off echo off verify off trimspool on
select owner||'|'||table_name||'|'||column_id||'|'||column_name||'|'||data_type||'|'||nvl(to_char(data_length),'')||'|'||nvl(to_char(data_precision),'')||'|'||nvl(to_char(data_scale),'') from all_tab_columns where owner=upper(sys_context('USERENV','CURRENT_SCHEMA')) order by owner,table_name,column_id;
select owner||'|'||constraint_name||'|'||constraint_type||'|'||table_name||'|'||r_constraint_name from all_constraints where owner=upper(sys_context('USERENV','CURRENT_SCHEMA')) order by owner,constraint_name;
select owner||'|'||index_name||'|'||table_name||'|'||uniqueness from all_indexes where owner=upper(sys_context('USERENV','CURRENT_SCHEMA')) order by owner,index_name;
exit
SQL
  } | docker exec -i "$container" bash -c 'export ORACLE_HOME=${ORACLE_HOME:-/opt/oracle/product/21c/dbhomeXE}; export PATH=$ORACLE_HOME/bin:$PATH; sqlplus -s /nolog' | sha256sum | awk '{print $1}'
}
SOURCE_SCHEMA_METADATA_SHA256="$(metadata_hash "$SOURCE_ORA" "$SOURCE_USER" "$SOURCE_PASS")"
TARGET_SCHEMA_METADATA_SHA256="$(metadata_hash "$ORA" "$TARGET_USER" "$APP_PASS")"
[[ "$SOURCE_SCHEMA_METADATA_SHA256" =~ ^[0-9a-f]{64}$ && "$TARGET_SCHEMA_METADATA_SHA256" =~ ^[0-9a-f]{64}$ ]] || fail 'schema metadata hash unavailable'
phase "schema metadata hashes computed"

cat > "$ROOT/application.properties" <<EOF
server.address=0.0.0.0
server.port=8080
spring.main.banner-mode=off
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
spring.datasource.type=com.zaxxer.hikari.HikariDataSource
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.maximum-pool-size=8
spring.datasource.hikari.connection-timeout=10000
spring.datasource.hikari.validation-timeout=3000
spring.datasource.hikari.initialization-fail-timeout=5000
spring.datasource.url=jdbc:oracle:thin:@//oracle-strict:1521/XEPDB1
spring.datasource.username=$TARGET_USER
spring.datasource.password=$APP_PASS
spring.redis.url=redis://redis-strict:6379/0
sunway.user.password.encoder.type=MD5
owned.web.security.mode=owned
owned.sss2.write.mode=isolated
owned.role-user.write.mode=isolated
owned.personnel.write.mode=isolated
owned.equipment.write.mode=isolated
owned.consumables.write.mode=isolated
owned.result.write.mode=isolated
owned.identity.audit.type=file
owned.identity.audit.path=/tmp/lims-strict-audit.jsonl
owned.identity.attachment-menu-ids=9325
owned.identity.result-menu-ids=2023110301,1520
owned.identity.result-write-menu-ids=152001,152002,152004,152005
owned.identity.order-retain-menu-ids=151202,15120201,15120204
owned.identity.order-retain-write-menu-ids=15120202,15120203
owned.attachments.enabled=true
owned.attachments.root=/var/lib/lims-owned-attachments
owned.attachments.write.mode=isolated
owned.erp.mode=disabled
owned.report.replay.mode=disabled
owned.scheduler.mode=disabled
EOF
chmod 600 "$ROOT/application.properties"
docker run -d --name "$REDIS" --network "$NET" --network-alias redis-strict --restart no redis:7.4-alpine redis-server --save '' --appendonly no >/dev/null
mkdir -p "$ROOT/distribution"; tar -xzf "$ROOT/incoming/distribution.tgz" -C "$ROOT/distribution"
phase "strict Redis and distribution ready"
cat > "$ROOT/Dockerfile" <<'EOF'
FROM lims-owned-sss2-owned:current
RUN rm -rf /opt/owned/*
COPY distribution /opt/owned
RUN mkdir -p /var/lib/lims-owned-attachments
RUN chmod 0755 /opt/owned/start.sh
EOF
docker build --pull=false --tag "$IMAGE" "$ROOT" >/dev/null
docker create --name "$APP" --network "$NET" --network-alias app-strict --env LIMS_BIND_ADDRESS=0.0.0.0 --env JAVA_TOOL_OPTIONS=-Djava.security.egd=file:/dev/./urandom --volume "$ROOT/application.properties:/run/config/application.properties:ro" "$IMAGE" >/dev/null
docker start "$APP" >/dev/null
APP_IP="$(docker inspect "$APP" -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}')"
[[ -n "$APP_IP" ]] || fail 'strict app IP unavailable'
cat > "$ROOT/port-forward.py" <<'PY'
import socket,sys,threading
port=int(sys.argv[1]); target=sys.argv[2]; target_port=int(sys.argv[3]) if len(sys.argv)>3 else 8080
bind_host=sys.argv[4] if len(sys.argv)>4 else '127.0.0.1'
def relay(a,b):
 try:
  while True:
   data=a.recv(65536)
   if not data: break
   b.sendall(data)
 except OSError: pass
 finally:
  for s in (a,b):
   try:s.close()
   except OSError:pass
def serve(c):
 try:u=socket.create_connection((target,target_port),5)
 except OSError:c.close();return
 u.settimeout(None)
 threading.Thread(target=relay,args=(c,u),daemon=True).start(); threading.Thread(target=relay,args=(u,c),daemon=True).start()
with socket.socket() as s:
 s.setsockopt(socket.SOL_SOCKET,socket.SO_REUSEADDR,1); s.bind((bind_host,port)); s.listen(32)
 while True:
  c,_=s.accept(); threading.Thread(target=serve,args=(c,),daemon=True).start()
PY
chmod 700 "$ROOT/port-forward.py"
APP_PID="$(docker inspect "$APP" -f '{{.State.Pid}}')"
[[ "$APP_PID" =~ ^[0-9]+$ && "$APP_PID" -gt 0 ]] || fail 'strict app host pid unavailable'
# The application enforces a real loopback remote address for isolated writes.
# Run the first relay inside the app network namespace so Spring sees 127.0.0.1;
# the host relay then exposes that namespace port to the SSH loopback forward.
nsenter -n -t "$APP_PID" nohup python3 "$ROOT/port-forward.py" "$PORT" 127.0.0.1 8080 0.0.0.0 > "$ROOT/port-forward-app.log" 2>&1 < /dev/null & echo $! > "$ROOT/port-forward-app.pid"
nohup python3 "$ROOT/port-forward.py" "$PORT" "$APP_IP" "$PORT" > "$ROOT/port-forward.log" 2>&1 < /dev/null & echo $! > "$ROOT/port-forward.pid"
ready=0
for _ in $(seq 1 180); do code="$(curl -sS -o /dev/null -w '%{http_code}' --max-time 3 "http://127.0.0.1:$PORT/open/security/public-key" 2>/dev/null || true)"; [[ "$code" == 200 ]] && { ready=1; break; }; sleep 2; done
[[ "$ready" == 1 ]] || fail 'strict app readiness timeout'
phase "strict app ready on loopback port $PORT"
python3 - "$SOURCE_SCHEMA" "$TARGET_USER" "$SOURCE_COMMIT" "$SOURCE_DIRTY" "$ARTIFACT_SHA256" "$SOURCE_DUMP_SHA256" "$SOURCE_SCHEMA_METADATA_SHA256" "$TARGET_SCHEMA_METADATA_SHA256" "$NET" "$VOL" "$APP" "$ORA" "$REDIS" "$PORT" "$ROOT" "$BINDING_JSON" <<'PY'
import datetime,json,sys
(source_schema,target_schema,source_commit,source_dirty,artifact,source_dump,source_hash,target_hash,net,vol,app,ora,redis,port,root,binding_json)=sys.argv[1:]
binding=json.loads(binding_json)
print(json.dumps({'schema':6,'status':'clone-ready-unverified','execution':'clone-ready-unverified','strict_evidence':False,'production_access':False,'scope':'disposable strict sss2 clone-derived API runtime','host':'sss2','loopback_url':'http://127.0.0.1:'+port,'existing_application_preserved':True,'source_commit':source_commit,'repository_head_at_capture':binding.get('repository_head_at_capture',source_commit),'source_dirty':source_dirty == '1','implementation_binding':binding.get('binding_id'),'implementation_tree_digest':binding.get('implementation_tree_digest'),'implementation_dirty':binding.get('implementation_dirty'),'implementation_scope':binding.get('implementation_scope',[]),'nested_project_commit':binding.get('nested_project_commit'),'distribution_input_digest':binding.get('distribution_input_digest'),'artifact_sha256':artifact,'source_schema':source_schema,'target_schema':target_schema,'remote_root':root,'route_execution_model':{'application':'one owned Spring application','oracle':'one clone-derived Oracle per API wave','redis':'one Redis per API wave','boundary_route':'owned Java source-only route in the same Spring application','boundary_process':False,'internal_http_bridge':False,'jni_replacement_process':False},'clone_baseline':{'source_dump_sha256':source_dump,'source_schema_metadata_sha256':source_hash,'target_schema_metadata_sha256':target_hash,'source_read_only':True,'target_isolated':True},'runtime_identity':{'network':net,'volume':vol,'application':app,'oracle':ora,'redis':redis,'host_forward_pid':root+'/port-forward.pid','app_namespace_forward_pid':root+'/port-forward-app.pid'},'cleanup':{'application_stopped':False,'oracle_stopped':False,'redis_stopped':False,'network_removed':False,'volumes_removed':False,'temporary_files_removed':False},'cleanup_command':root+'/cleanup-strict.sh','created_at':datetime.datetime.now(datetime.timezone.utc).isoformat(),'limitations':['strict evidence remains false until HTTP replay, cleanup and report finalization']},ensure_ascii=False,indent=2))
PY
touch "$ROOT/start-success"
REMOTE
mkdir -p "$(dirname "$OUTPUT")"
mv "$WORK/manifest.json" "$OUTPUT"
[[ -s "$OUTPUT" ]] || { echo 'strict runtime did not produce a manifest; inspect remote diagnostics' >&2; exit 1; }
chmod 0600 "$OUTPUT"
python3 "$ROOT/scripts/owned_runtime_contract.py" --report "$OUTPUT" --mode strict >/dev/null
printf 'strict sss2 runtime: clone-ready-unverified %s\n' "$OUTPUT"
