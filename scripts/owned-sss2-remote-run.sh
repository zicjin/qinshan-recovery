#!/usr/bin/env bash
set -euo pipefail
ROOT="$1"; PORT="$2"
SOURCE_COMMIT="${SOURCE_COMMIT:-unknown}"; SOURCE_DIRTY="${SOURCE_DIRTY:-unknown}"
NET=lims-owned-sss2-net; ORA=lims-owned-sss2-oracle; REDIS=lims-owned-sss2-redis; APP=lims-owned-sss2-app; VOL=lims-owned-sss2-oracle-data; IMAGE=lims-owned-sss2-owned:current
IN="$ROOT/incoming"; DIST="$ROOT/distribution"; LOG="$ROOT/migration.log"; REPORT="$ROOT/deployment-report.json"
die(){ echo "deploy-owned-sss2: FAIL: $*" >&2; exit 1; }
cleanup_source_dump(){ docker exec lims-replay-demo-s1-oracle sh -c 'for d in /opt/oracle/admin/XE/dpdump/*; do rm -f "$d/owned_full.dmp" "$d/owned_full.log"; done; rm -f /opt/oracle/admin/XE/dpdump/owned_full.dmp /opt/oracle/admin/XE/dpdump/owned_full.log /tmp/owned_exp.par' >/dev/null 2>&1 || true; rm -f "$ROOT/schema.sql" "$ROOT/audit-line-schema.sql"; }
trap cleanup_source_dump EXIT
for n in lims-replay-demo-s1-app lims-replay-demo-s1-oracle lims-replay-demo-s1-redis; do docker inspect "$n" >/dev/null 2>&1 || die "missing existing $n"; done
[[ "$(docker network inspect lims-replay-demo-s1-net -f '{{.Internal}}')" == true ]] || die 'existing sss2 network is not internal'
for n in "$APP" "$ORA" "$REDIS"; do docker ps -a --format '{{.Names}}' | grep -Fxq "$n" && die "$n already exists; run cleanup first" || true; done
docker network ls --format '{{.Name}}' | grep -Fxq "$NET" && die "$NET already exists; run cleanup first" || true
rm -rf "$DIST"; mkdir -p "$DIST"; tar -xzf "$IN/distribution.tgz" -C "$DIST"; chmod 0755 "$DIST/start.sh"
SRC_USER=$(docker exec lims-replay-demo-s1-app sh -c "grep '^spring.datasource.username=' /data/LIMS/jiankeyuan/lims-czjky-emc/application.properties | tail -1 | cut -d= -f2-" | tr -d '\r')
SRC_PASS=$(docker exec lims-replay-demo-s1-app sh -c "grep '^spring.datasource.password=' /data/LIMS/jiankeyuan/lims-czjky-emc/application.properties | tail -1 | cut -d= -f2-" | tr -d '\r')
[[ -n "$SRC_USER" && -n "$SRC_PASS" ]] || die 'existing sss2 datasource credentials unavailable'
SYS_PASS=$(openssl rand -hex 12); APP_PASS=$(openssl rand -hex 12); TARGET_USER=LIMS_OWNED_SSS2
mkdir -p "$ROOT/logs"; : > "$LOG"
docker network create --internal --driver bridge "$NET" >/dev/null
docker volume create "$VOL" >/dev/null
docker run -d --name "$ORA" --network "$NET" --network-alias oracle-owned --restart no --shm-size 1g --volume "$VOL:/opt/oracle/oradata" -e ORACLE_PASSWORD="$SYS_PASS" gvenzl/oracle-xe:21-slim >/dev/null
docker run -d --name "$REDIS" --network "$NET" --network-alias redis-owned --restart no redis:7.4-alpine redis-server --save '' --appendonly no >/dev/null
ready=0
for _ in $(seq 1 180); do docker exec "$ORA" /opt/oracle/healthcheck.sh >/dev/null 2>&1 && { ready=1; break; }; sleep 5; done
[[ "$ready" == 1 ]] || die 'owned Oracle readiness timeout'
python3 - "$ROOT/schema.sql" "$SYS_PASS" "$APP_PASS" <<'PY'
from pathlib import Path
import sys
p,sys_pw,app=sys.argv[1:]
Path(p).write_text(f'''whenever sqlerror exit failure\nalter session set container = CDB$ROOT;\nalter user system identified by "{sys_pw}" container=ALL;\nalter session set container = XEPDB1;\ncreate user LIMS_OWNED_SSS2 identified by "{app}" quota unlimited on users;\ngrant create session, create table, create view, create sequence, create procedure, create type, create trigger, create materialized view to LIMS_OWNED_SSS2;\nexit\n'''); Path(p).chmod(0o600)
PY
docker exec -i "$ORA" bash -lc 'export ORACLE_HOME=${ORACLE_HOME:-/opt/oracle/product/21c/dbhomeXE}; export PATH=$ORACLE_HOME/bin:$PATH; sqlplus -s / as sysdba' < "$ROOT/schema.sql" > "$ROOT/logs/schema.log" 2>&1 || { cat "$ROOT/logs/schema.log" >&2; die 'target schema creation failed'; }
rm -f "$ROOT/schema.sql"
echo 'source test-schema export' | tee -a "$LOG"
# Existing sss2 Oracle is the test-data source; no sss/production endpoint is used.
# Keep Data Pump filters in a temporary parfile so nested shell quoting cannot
# change the TABLE expression. The file and dump are removed on exit.
docker exec lims-replay-demo-s1-oracle sh -c 'for d in /opt/oracle/admin/XE/dpdump/*; do rm -f "$d/owned_full.dmp" "$d/owned_full.log"; done; rm -f /opt/oracle/admin/XE/dpdump/owned_full.dmp /opt/oracle/admin/XE/dpdump/owned_full.log /tmp/owned_exp.par'
docker exec -i -e SRC_USER="$SRC_USER" -e SRC_PASS="$SRC_PASS" lims-replay-demo-s1-oracle bash -lc 'umask 077; cat >/tmp/owned_exp.par <<EOF
userid=$SRC_USER/$SRC_PASS@//localhost/XEPDB1
schemas=$SRC_USER
content=ALL
directory=DATA_PUMP_DIR
dumpfile=owned_full.dmp
logfile=owned_full.log
exclude=DB_LINK
exclude=TABLE:"IN (!T_CORE_FILE!,!T_EMC_EQUIPT_ACQ_FILE!,!T_EMC_ORDER_ITEM_GROUP!,!T_EMC_ORDER_TASK_RESULT!,!T_EMC_REPORT_RESULT!,!T_CORE_AT_RECORD_LINE!,!T_EMC_ORDER_TASK_LOG!,!T_EMC_ORDER_TASK!,!T_CORE_LOG!)"
EOF
sed -i "s/!/$(printf \\047)/g" /tmp/owned_exp.par
export ORACLE_HOME=${ORACLE_HOME:-/opt/oracle/product/21c/dbhomeXE}; export PATH=$ORACLE_HOME/bin:$PATH
expdp parfile=/tmp/owned_exp.par
rm -f /tmp/owned_exp.par' >> "$LOG" 2>&1
SRC_DUMP=$(docker exec lims-replay-demo-s1-oracle bash -lc 'for d in /opt/oracle/admin/XE/dpdump/*; do if [ -f "$d/owned_full.dmp" ]; then printf "%s" "$d/owned_full.dmp"; break; fi; done')
[[ -n "$SRC_DUMP" ]] || die 'source Data Pump dump not found'
SOURCE_DUMP_SHA256=$(docker exec lims-replay-demo-s1-oracle sha256sum "$SRC_DUMP" | awk '{print $1}')
[[ "$SOURCE_DUMP_SHA256" =~ ^[0-9a-f]{64}$ ]] || die 'source dump hash unavailable'
docker cp "lims-replay-demo-s1-oracle:$SRC_DUMP" "$ROOT/owned_full.dmp"
TARGET_DUMP_ROOT=/opt/oracle/admin/XE/dpdump
DUMP_SUBDIR=${SRC_DUMP%/*}; DUMP_SUBDIR=${DUMP_SUBDIR##*/}
TARGET_DUMP_DIR="$TARGET_DUMP_ROOT/$DUMP_SUBDIR"
docker exec "$ORA" mkdir -p "$TARGET_DUMP_DIR"
docker exec "$ORA" test -d "$TARGET_DUMP_DIR" || die "target Data Pump directory unavailable"
echo "target dump directory: $TARGET_DUMP_DIR" >> "$LOG"
docker cp "$ROOT/owned_full.dmp" "$ORA:$TARGET_DUMP_DIR/owned_full.dmp"
docker exec -u 0 "$ORA" chown oracle:oinstall "$TARGET_DUMP_DIR/owned_full.dmp"
docker exec -u 0 "$ORA" chmod 640 "$TARGET_DUMP_DIR/owned_full.dmp"
docker exec "$ORA" test -r "$TARGET_DUMP_DIR/owned_full.dmp" || die "target dump copy failed"
rm -f "$ROOT/owned_full.dmp"
echo 'target data import' | tee -a "$LOG"
set +e
docker exec -i -e SYS_PASS="$SYS_PASS" "$ORA" bash -lc "export ORACLE_HOME=\${ORACLE_HOME:-/opt/oracle/product/21c/dbhomeXE}; export PATH=\$ORACLE_HOME/bin:\$PATH; impdp system/\$SYS_PASS@//localhost/XEPDB1 directory=DATA_PUMP_DIR dumpfile=owned_full.dmp logfile=owned_imp_full.log remap_schema=$SRC_USER:$TARGET_USER remap_tablespace=$SRC_USER:USERS transform=OID:N" >> "$LOG" 2>&1
IMPORT_RC=$?
set -e
IMPORT_WARNINGS=$(grep -Eo 'ORA-(39346|39082)' "$LOG" | sort -u | tr "\n" "," | sed 's/,$//' || true)
if [[ "$IMPORT_RC" != 0 && -z "$IMPORT_WARNINGS" ]]; then die "target Data Pump import failed (rc=$IMPORT_RC)"; fi
echo "target import rc=$IMPORT_RC warnings=${IMPORT_WARNINGS:-none}" >> "$LOG"
# T_CORE_AT_RECORD_LINE is intentionally excluded from the approximate source
# export because the production-sized table is about 1.2 GB.  Create an empty
# schema-compatible table in the owned target so the original audit-line
# endpoint can return a real empty page instead of a missing-table error.  No
# source rows are copied and no foreign key is added; this is only an isolated
# compatibility seam and must not be reported as audit-history parity.
SYNTHETIC_COMPAT_TABLES=""
cat > "$ROOT/audit-line-schema.sql" <<EOF3
whenever sqlerror exit failure
connect $TARGET_USER/"$APP_PASS"@//localhost/XEPDB1
declare
  present number;
begin
  select count(*) into present from user_tables where table_name = 'T_CORE_AT_RECORD_LINE';
  if present = 0 then
    execute immediate 'create table T_CORE_AT_RECORD_LINE (' ||
      'ID NUMBER NOT NULL, RECORDID NUMBER NOT NULL, TARGETID VARCHAR2(129 BYTE), ' ||
      'RECORDTYPE VARCHAR2(12 BYTE), "COLUMN" VARCHAR2(60 BYTE), OLDVALUE CLOB, ' ||
      'NEWVALUE CLOB, TABLENAME VARCHAR2(192 BYTE), CREATEDBYID VARCHAR2(36 BYTE), ' ||
      'CREATEDBYNAME VARCHAR2(60 BYTE), CREATEDTIME DATE, CREATEDBYORGID VARCHAR2(36 BYTE), ' ||
      'CREATEDBYORGNAME VARCHAR2(270 BYTE))';
    execute immediate 'alter table T_CORE_AT_RECORD_LINE add constraint PK_T_CORE_AT_RECORD_LINE primary key (ID)';
    execute immediate 'create index IDX_CORE_ATRECORDLINE_RECORDID on T_CORE_AT_RECORD_LINE (RECORDID)';
  end if;
end;
/
declare
  present number;
begin
  select count(*) into present from user_tables where table_name = 'T_CORE_FILE';
  if present = 0 then
    execute immediate 'create table T_CORE_FILE (' ||
      'ID NUMBER NOT NULL, TARGETID VARCHAR2(200), NAME VARCHAR2(256), FILEEXT VARCHAR2(32), ' ||
      'SCOPE VARCHAR2(32), MD5NAME VARCHAR2(128), CREATEDTIME TIMESTAMP, ' ||
      'BIZCATEGORY VARCHAR2(128), EXT VARCHAR2(64), DOWNLOADABLE VARCHAR2(8))';
    execute immediate 'alter table T_CORE_FILE add constraint PK_OWNED_CORE_FILE primary key (ID)';
  end if;
end;
/
exit
EOF3
chmod 600 "$ROOT/audit-line-schema.sql"
docker exec -i "$ORA" bash -lc 'export ORACLE_HOME=${ORACLE_HOME:-/opt/oracle/product/21c/dbhomeXE}; export PATH=$ORACLE_HOME/bin:$PATH; sqlplus -s /nolog' < "$ROOT/audit-line-schema.sql" > "$ROOT/logs/audit-line-schema.log" 2>&1 || { cat "$ROOT/logs/audit-line-schema.log" >&2; rm -f "$ROOT/audit-line-schema.sql"; die 'audit-line compatibility table creation failed'; }
rm -f "$ROOT/audit-line-schema.sql"
SYNTHETIC_COMPAT_TABLES="T_CORE_AT_RECORD_LINE,T_CORE_FILE"
cat > "$ROOT/application.properties" <<EOF2
server.address=0.0.0.0
server.port=8080
# Browser cookies are host-scoped, not port-scoped. Keep the owned 18087
# session isolated from the supplier 18081 and debug 18088 instances when a
# user opens them from the same browser profile.
server.servlet.session.cookie.name=OWNED_SSS2_SESSION
spring.main.banner-mode=off
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
spring.datasource.type=org.springframework.jdbc.datasource.DriverManagerDataSource
spring.datasource.url=jdbc:oracle:thin:@//oracle-owned:1521/XEPDB1
spring.datasource.username=$TARGET_USER
spring.datasource.password=$APP_PASS
spring.redis.url=redis://redis-owned:6379/0
sunway.user.password.encoder.type=MD5
owned.web.security.mode=owned
owned.sss2.write.mode=isolated
owned.identity.audit.type=file
owned.identity.audit.path=/tmp/lims-owned-audit.jsonl
owned.identity.attachment-menu-ids=9325
# Observed result-entry menu grants; this is an explicit allowlist, never a wildcard.
owned.identity.result-menu-ids=2023110301,1520
owned.identity.result-write-menu-ids=152001,152002,152004,152005
owned.result.write.mode=isolated
owned.attachments.enabled=true
owned.attachments.root=/var/lib/lims-owned-attachments
owned.attachments.write.mode=isolated
owned.erp.mode=disabled
owned.report.replay.mode=disabled
owned.scheduler.mode=disabled
EOF2
chmod 600 "$ROOT/application.properties"
if ! docker image inspect "$IMAGE" >/dev/null 2>&1; then
  cat > "$ROOT/Dockerfile" <<EOF2
FROM debian:bullseye-slim
COPY incoming/jdk.tgz /tmp/jdk.tgz
RUN mkdir -p /opt && tar -xzf /tmp/jdk.tgz -C /opt && rm /tmp/jdk.tgz
ENV JAVA_HOME=/opt/jdk1.8.0_391
COPY distribution /opt/owned
RUN mkdir -p /var/lib/lims-owned-attachments
RUN chmod 0755 /opt/owned/start.sh
ENTRYPOINT ["/opt/owned/start.sh", "/run/config/application.properties"]
EOF2
  docker build --pull=false --tag "$IMAGE" "$ROOT" > "$ROOT/logs/image-build.log" 2>&1
fi
docker create --name "$APP" --network "$NET" --network-alias app --env LIMS_BIND_ADDRESS=0.0.0.0 --env JAVA_TOOL_OPTIONS=-Djava.security.egd=file:/dev/./urandom --volume "$ROOT/application.properties:/run/config/application.properties:ro" "$IMAGE" >/dev/null
docker start "$APP" >/dev/null
APP_IP=$(docker inspect "$APP" -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}')
[[ -n "$APP_IP" ]] || die "owned app internal IP unavailable"
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
        for s in (src,dst):
            try: s.shutdown(socket.SHUT_RDWR)
            except OSError: pass
            s.close()
def serve(conn):
    try: upstream=socket.create_connection((target,target_port),5)
    except OSError: conn.close(); return
    threading.Thread(target=relay,args=(conn,upstream),daemon=True).start()
    threading.Thread(target=relay,args=(upstream,conn),daemon=True).start()
with socket.socket() as server:
    server.setsockopt(socket.SOL_SOCKET,socket.SO_REUSEADDR,1)
    server.bind((bind_host,listen_port)); server.listen(32)
    while True:
        conn,_=server.accept(); threading.Thread(target=serve,args=(conn,),daemon=True).start()
PYFORWARD
chmod 700 "$ROOT/port-forward.py"
command -v python3 >/dev/null || die "python3 is required for loopback forwarding"
APP_PID=$(docker inspect "$APP" -f '{{.State.Pid}}')
[[ "$APP_PID" =~ ^[0-9]+$ && "$APP_PID" -gt 0 ]] || die 'owned app host pid unavailable'
# Enter the app network namespace before forwarding to Tomcat so isolated
# HTTP writes see a real loopback remote address. The host relay then exposes
# that namespace-local port for the SSH tunnel.
nsenter -n -t "$APP_PID" nohup python3 "$ROOT/port-forward.py" "$PORT" 127.0.0.1 8080 0.0.0.0 > "$ROOT/port-forward-app.log" 2>&1 < /dev/null & echo $! > "$ROOT/port-forward-app.pid"
nohup python3 "$ROOT/port-forward.py" "$PORT" "$APP_IP" "$PORT" > "$ROOT/port-forward.log" 2>&1 < /dev/null & echo $! > "$ROOT/port-forward.pid"
ready=0
for _ in $(seq 1 180); do code=$(curl -sS -o /dev/null -w "%{http_code}" --max-time 3 "http://127.0.0.1:$PORT/" 2>/dev/null || echo 000); [[ "$code" != 000 ]] && { ready=1; break; }; sleep 2; done
status=executed-fail; [[ "$ready" == 1 ]] && status=executed-pass
cat > "$ROOT/target-schema-baseline.sql" <<EOF_BASELINE
whenever sqlerror exit failure
set pagesize 0 feedback off heading off echo off verify off trimspool on
connect $TARGET_USER/"$APP_PASS"@//localhost/XEPDB1
select OWNER||'|'||TABLE_NAME||'|'||COLUMN_ID||'|'||COLUMN_NAME||'|'||DATA_TYPE||'|'||NVL(TO_CHAR(DATA_LENGTH),'')||'|'||NVL(TO_CHAR(DATA_PRECISION),'')||'|'||NVL(TO_CHAR(DATA_SCALE),'')
from ALL_TAB_COLUMNS where OWNER=UPPER('$TARGET_USER') order by OWNER,TABLE_NAME,COLUMN_ID;
exit
EOF_BASELINE
TARGET_SCHEMA_METADATA_SHA256=$(docker exec -i "$ORA" bash -lc 'export ORACLE_HOME=${ORACLE_HOME:-/opt/oracle/product/21c/dbhomeXE}; export PATH=$ORACLE_HOME/bin:$PATH; sqlplus -s /nolog' < "$ROOT/target-schema-baseline.sql" | sha256sum | awk '{print $1}')
rm -f "$ROOT/target-schema-baseline.sql"
[[ "$TARGET_SCHEMA_METADATA_SHA256" =~ ^[0-9a-f]{64}$ ]] || die 'target schema metadata hash unavailable'
python3 - "$REPORT" "clone-ready-unverified" "$PORT" "$SRC_USER" "$TARGET_USER" "$IMPORT_RC" "${IMPORT_WARNINGS:-}" "$SYNTHETIC_COMPAT_TABLES" "$ROOT" "$SOURCE_COMMIT" "$SOURCE_DIRTY" "$SOURCE_DUMP_SHA256" "$TARGET_SCHEMA_METADATA_SHA256" <<'PY'
import json,sys,datetime,subprocess
out,status,port,src,target,import_rc,warnings,synthetic_tables,root,source_commit,source_dirty,source_dump_sha256,target_schema_sha256=sys.argv[1:]
def ident(n):
 try:return json.loads(subprocess.check_output(['docker','inspect',n]))[0].get('Id')
 except:return None
def http_code(path):
 import subprocess
 for _ in range(10):
  try:
   result=subprocess.run(['curl','-sS','-o','/dev/null','-w','%{http_code}','--max-time','5','http://127.0.0.1:'+port+path],stdout=subprocess.PIPE,stderr=subprocess.PIPE,universal_newlines=True)
   code=int(result.stdout.strip() or '0')
   if code: return code
  except Exception:
   pass
  import time
  time.sleep(1)
 return 0
resource_paths=['/static/gikam/main/css/chunk-common.css','/static/gikam/main/css/home.css','/static/gikam/main/js/chunk-vendors.js','/static/gikam/main/js/chunk-common.js','/static/gikam/main/js/home.js','/static/emc/icon/logo.png']
resource_checks={path:http_code(path) for path in resource_paths}
payload={'schema':3,'status':status,'execution':'clone-ready-unverified','strict_evidence':False,'production_access':False,'scope':'parallel owned supplier-exit deployment on sss2','host':'sss2','loopback_url':'http://127.0.0.1:'+port,'existing_application_preserved':True,'source_commit':source_commit,'source_dirty':source_dirty == '1','owned_containers':['lims-owned-sss2-app','lims-owned-sss2-oracle','lims-owned-sss2-redis'],'source_schema':src,'target_schema':target,'clone_baseline':{'source_dump_sha256':source_dump_sha256,'target_schema_metadata_sha256':target_schema_sha256,'source_read_only':True,'target_isolated':True},'migration':{'source':'existing sss2 Oracle test schema','source_read_only':True,'import_exit_code':int(import_rc),'known_warnings':[w for w in warnings.split(',') if w],'data_pump_excluded_tables':['T_CORE_FILE','T_EMC_EQUIPT_ACQ_FILE','T_EMC_ORDER_ITEM_GROUP','T_EMC_ORDER_TASK_RESULT','T_EMC_REPORT_RESULT','T_CORE_AT_RECORD_LINE','T_EMC_ORDER_TASK_LOG','T_EMC_ORDER_TASK','T_CORE_LOG'],'synthetic_compatibility_tables_created':[t for t in synthetic_tables.split(',') if t],'source_data_migrated_for_synthetic_tables':False,'attachments_migrated':False},'frontend_acceptance':{'status':'resource-pass' if all(value == 200 for value in resource_checks.values()) else 'executed-fail','root_login_shell':'owned-production-shaped-shell','original_project_static_resources_served':all(value == 200 for value in resource_checks.values()),'resource_checks':resource_checks,'original_workspace_browser_evidence_required':True},'network':{'internal':True,'loopback_forwarder':True,'loopback_namespace_relay':True,'boundary_process':False,'internal_http_bridge':False,'port_forward_pid_file':root+'/port-forward.pid','port_forward_app_pid_file':root+'/port-forward-app.pid'},'cleanup':{'existing_sss2_app_stopped':False,'existing_sss2_oracle_stopped':False,'existing_sss2_redis_stopped':False,'owned_deployment_active':True},'container_ids':{n:ident(n) for n in ['lims-owned-sss2-app','lims-owned-sss2-oracle','lims-owned-sss2-redis']},'created_at':datetime.datetime.now(datetime.timezone.utc).isoformat(),'limitations':['deployment report is clone-ready only and does not prove API parity','T_CORE_AT_RECORD_LINE source rows are omitted; only an empty compatibility table is created','attachments and large operational tables omitted','Redis cache state is not migrated','API fixture and strict replay must be run by a separate runner']}
open(out,'w').write(json.dumps(payload,ensure_ascii=False,indent=2)+'\n')
PY
rm -f "$IN/distribution.tgz" "$IN/jdk.tgz"
chmod 600 "$REPORT" "$LOG"
echo "deploy-owned-sss2: $status http://127.0.0.1:$PORT report=$REPORT"
