#!/usr/bin/env bash
set -euo pipefail

# Read-only observer for an already-running sss2 clone. It never starts,
# stops, replaces or writes Oracle/Redis/application containers.
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SSH_HOST="${SSH_HOST:-sss2}"
REMOTE_ROOT="${REMOTE_ROOT:-/data/lims-owned-sss2}"
OUTPUT="${OUTPUT:-$ROOT/../.scratch/sunway-supplier-exit-api-first/diagnostic-evidence/runtime/sss2-clone-runtime-manifest-20261027.json}"

mkdir -p "$(dirname "$OUTPUT")"
ssh -o BatchMode=yes -o ConnectTimeout=5 "$SSH_HOST" bash -s -- "$REMOTE_ROOT" <<'REMOTE' > "$OUTPUT.tmp"
set -euo pipefail
REMOTE_ROOT="$1"
DEPLOY="$REMOTE_ROOT/deployment-report.json"
[[ -r "$DEPLOY" ]] || { echo 'missing deployment report' >&2; exit 3; }
for name in lims-replay-demo-s1-app lims-replay-demo-s1-oracle lims-owned-sss2-app lims-owned-sss2-oracle lims-owned-sss2-redis; do
  docker inspect "$name" >/dev/null 2>&1 || { echo "missing required container: $name" >&2; exit 3; }
  [[ "$(docker inspect "$name" --format '{{.State.Status}}')" == running ]] || { echo "$name is not running" >&2; exit 3; }
done
SOURCE_SCHEMA="$(python3 - "$DEPLOY" <<'PY'
import json,sys
print(json.load(open(sys.argv[1]))['source_schema'])
PY
)"
TARGET_SCHEMA="$(python3 - "$DEPLOY" <<'PY'
import json,sys
print(json.load(open(sys.argv[1]))['target_schema'])
PY
)"
SOURCE_COMMIT="$(python3 - "$DEPLOY" <<'PY'
import json,sys
print(json.load(open(sys.argv[1])).get('source_commit','unknown'))
PY
)"
SOURCE_USER="$(docker exec lims-replay-demo-s1-app sh -c "grep '^spring.datasource.username=' /data/LIMS/jiankeyuan/lims-czjky-emc/application.properties | tail -1 | cut -d= -f2-" | tr -d '\r')"
SOURCE_PASS="$(docker exec lims-replay-demo-s1-app sh -c "grep '^spring.datasource.password=' /data/LIMS/jiankeyuan/lims-czjky-emc/application.properties | tail -1 | cut -d= -f2-" | tr -d '\r')"
TARGET_USER="$(awk -F= '/^spring.datasource.username=/{value=$2} END{gsub(/\r/,"",value); print value}' "$REMOTE_ROOT/application.properties")"
TARGET_PASS="$(awk -F= '/^spring.datasource.password=/{value=$2} END{gsub(/\r/,"",value); print value}' "$REMOTE_ROOT/application.properties")"
[[ -n "$SOURCE_USER" && -n "$SOURCE_PASS" && -n "$TARGET_USER" && -n "$TARGET_PASS" ]] || { echo 'datasource credentials unavailable' >&2; exit 4; }
metadata_hash() {
  local container="$1" user="$2" pass="$3" owner="$4"
  docker exec -i -e DB_USER="$user" -e DB_PASS="$pass" -e DB_OWNER="$owner" "$container" bash -lc 'export ORACLE_HOME=${ORACLE_HOME:-/opt/oracle/product/21c/dbhomeXE}; export PATH=$ORACLE_HOME/bin:$PATH; sqlplus -s "$DB_USER/$DB_PASS@//localhost/XEPDB1"' <<'SQL' | sha256sum | awk '{print $1}'
whenever sqlerror exit failure
set pagesize 0 feedback off heading off echo off verify off trimspool on
select owner||'|'||table_name||'|'||column_id||'|'||column_name||'|'||data_type||'|'||nvl(to_char(data_length),'')||'|'||nvl(to_char(data_precision),'')||'|'||nvl(to_char(data_scale),'') from all_tab_columns where owner=upper(sys_context('USERENV','CURRENT_SCHEMA')) order by owner,table_name,column_id;
select owner||'|'||constraint_name||'|'||constraint_type||'|'||table_name||'|'||r_constraint_name from all_constraints where owner=upper(sys_context('USERENV','CURRENT_SCHEMA')) order by owner,constraint_name;
select owner||'|'||index_name||'|'||table_name||'|'||uniqueness from all_indexes where owner=upper(sys_context('USERENV','CURRENT_SCHEMA')) order by owner,index_name;
exit
SQL
}
SOURCE_HASH="$(metadata_hash lims-replay-demo-s1-oracle "$SOURCE_USER" "$SOURCE_PASS" "$SOURCE_SCHEMA")"
TARGET_HASH="$(metadata_hash lims-owned-sss2-oracle "$TARGET_USER" "$TARGET_PASS" "$TARGET_SCHEMA")"
[[ "$SOURCE_HASH" =~ ^[0-9a-f]{64}$ && "$TARGET_HASH" =~ ^[0-9a-f]{64}$ ]] || { echo 'schema metadata hash unavailable' >&2; exit 5; }
python3 - "$SOURCE_SCHEMA" "$TARGET_SCHEMA" "$SOURCE_COMMIT" "$SOURCE_HASH" "$TARGET_HASH" <<'PY'
import datetime,json,sys
source_schema,target_schema,source_commit,source_hash,target_hash=sys.argv[1:]
payload={
  'schema':4,
  'status':'clone-ready-unverified',
  'execution':'clone-ready-unverified',
  'strict_evidence':False,
  'production_access':False,
  'scope':'read-only observation of existing isolated sss2 clone runtime',
  'host':'sss2',
  'loopback_url':'http://127.0.0.1:18087',
  'existing_application_preserved':True,
  'source_commit':source_commit,
  'source_dirty':False,
  'source_schema':source_schema,
  'target_schema':target_schema,
  'clone_baseline':{
    'source_schema_metadata_sha256':source_hash,
    'target_schema_metadata_sha256':target_hash,
    'source_read_only':True,
    'target_isolated':True,
    'source_dump_sha256':None,
  },
  'runtime_observation':{
    'oracle_restarted':False,
    'redis_restarted':False,
    'application_restarted':False,
    'supplier_port_preserved':18081,
  },
  'limitations':[
    'source Data Pump dump was already cleaned; source schema metadata hash is the available baseline',
    'existing application artifact predates current source and cannot be strict API evidence',
    'API fixture, HTTP replay, audit and final cleanup remain separate gates',
  ],
  'created_at':datetime.datetime.now(datetime.timezone.utc).isoformat(),
}
print(json.dumps(payload,ensure_ascii=False,indent=2))
PY
REMOTE
mv "$OUTPUT.tmp" "$OUTPUT"
chmod 0600 "$OUTPUT"
python3 "$ROOT/scripts/owned_runtime_contract.py" --report "$OUTPUT" --mode strict >/dev/null
printf 'sss2 clone observer: PASS %s\n' "$OUTPUT"
