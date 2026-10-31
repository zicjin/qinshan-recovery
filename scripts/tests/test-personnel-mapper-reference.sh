#!/usr/bin/env bash
# Reference-only MyBatis XML -> BoundSql -> isolated Oracle observation.
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
default_path() { [ -f "$1" ] && printf '%s' "$1" || printf '%s' "$2"; }
JDK="${JAVA_HOME:-$(default_path "$ROOT/build/jdk1.8.0_391/bin/java" /home/ts/qinshan/lims-recovery/build/jdk1.8.0_391/bin/java)}"
JDK="${JDK%/bin/java}"
RUN_JAR="${RUN_JAR:-$(default_path "$ROOT/materials/gikam-third-czjky-14.1.5-run.jar" /home/ts/qinshan/lims-recovery/materials/gikam-third-czjky-14.1.5-run.jar)}"
GLOBAL_XML="${GLOBAL_XML:-$(default_path "$ROOT/build/owned-contract-reference/global-oracle-mapper.xml" /home/ts/qinshan/lims-recovery/build/owned-contract-reference/global-oracle-mapper.xml)}"
PERSONNEL_XML="${PERSONNEL_XML:-$(default_path "$ROOT/project/src/main/java/com/sunwayworld/emc/module/mdm/emp/emp/mapper/conf/emc-emp-oracle-mapper.xml" /home/ts/qinshan/lims-recovery/project/src/main/java/com/sunwayworld/emc/module/mdm/emp/emp/mapper/conf/emc-emp-oracle-mapper.xml)}"
IMAGE="${ORACLE_IMAGE:-ghcr.io/gvenzl/oracle-free@sha256:cd3dff6649f5d67ca3121f5e064b5a5680611e5d34f03442423110778c47dda4}"
EVIDENCE_OUT="${EVIDENCE_OUT:-$ROOT/build/personnel-mapper-reference.json}"
for path in "$RUN_JAR" "$GLOBAL_XML" "$PERSONNEL_XML"; do [ -f "$path" ] || { echo "missing input: $path" >&2; exit 2; }; done
"$ROOT/scripts/build-owned-framework.sh" >/dev/null
tmp="$(mktemp -d)"; trap 'rm -rf "$tmp"' EXIT
"$JDK/bin/java" -cp "$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/test-classes:$ROOT/owned-framework/target/public-deps/*" \
  com.sunwayworld.framework.PersonnelMapperBoundSqlReferenceTest "$GLOBAL_XML" "$PERSONNEL_XML" "$tmp/cases.jsonl"
sha256sum "$GLOBAL_XML" "$PERSONNEL_XML" > "$tmp/source.sha256"
python3 - "$tmp/cases.jsonl" "$tmp/source.sha256" <<'PY'
import json, pathlib, sys
cases = [json.loads(line) for line in pathlib.Path(sys.argv[1]).read_text().splitlines()]
assert [c["case"] for c in cases] == ["list-all", "list-org-filter", "list-no-match", "list-standard-name", "list-advanced-name", "list-multi-name", "ordered-name-desc", "detail-by-id", "detail-org-filter-e2"]
assert any("T_CORE_USER_PERMISSION" in c["sql"] for c in cases if c["case"] == "list-org-filter")
assert any("missing-department" in c["sql"] for c in cases if c["case"] == "list-no-match")
assert any('WHERE A.ID = \'e-1\'' in c["sql"] for c in cases if c["case"] == "detail-by-id")
print("personnel BoundSql shape: PASS")
PY
if ! docker image inspect "$IMAGE" >/dev/null 2>&1; then
  python3 - "$EVIDENCE_OUT" <<'PY'
import json,pathlib,sys
path=pathlib.Path(sys.argv[1]); path.parent.mkdir(parents=True,exist_ok=True)
path.write_text(json.dumps({'schema':1,'status':'not-executed','reason':'oracle-image-unavailable'})+'\n')
PY
  echo "Oracle image unavailable; execution not performed" >&2
  exit 2
fi
nonce="$(date +%s%N)"; name="lims-personnel-ref-$nonce"; network="lims-personnel-ref-net-$nonce"
cleanup() { docker rm --force --volumes "$name" >/dev/null 2>&1 || true; docker network rm "$network" >/dev/null 2>&1 || true; rm -rf "$tmp"; }
trap cleanup EXIT
docker network create --internal --driver bridge "$network" >/dev/null
docker run --detach --name "$name" --network "$network" --restart=no --memory=3g --cpus=2 \
  --env ORACLE_PASSWORD="Probe$(date +%s%N)" "$IMAGE" >/dev/null
sql() { docker exec -i "$name" sqlplus -s '/ as sysdba'; }
ready=0
for _ in $(seq 1 120); do
  PDB_NAME="$(printf 'set heading off feedback off pagesize 0\nselect name from v$pdbs where open_mode = '\''READ WRITE'\'';\nexit\n' | sql 2>/dev/null | awk '/FREEPDB/{print; exit}' | tr -d '[:space:]')"
  if [ -n "$PDB_NAME" ]; then ready=1; break; fi
  [ "$(docker inspect --format '{{.State.Running}}' "$name" 2>/dev/null)" = true ] || { echo "isolated Oracle exited before readiness" >&2; exit 2; }
  sleep 5
done
[ "$ready" = 1 ] || { echo "isolated Oracle did not become ready" >&2; exit 2; }
docker inspect "$name" >"$tmp/container-inspect.json"
docker network inspect "$network" >"$tmp/network-inspect.json"
python3 - "$tmp/container-inspect.json" "$tmp/network-inspect.json" <<'PY'
import json, sys
container=json.load(open(sys.argv[1]))[0]; network=json.load(open(sys.argv[2]))[0]
assert network['Internal'] is True
assert list(container['NetworkSettings']['Networks']) == [network['Name']]
assert not container['HostConfig']['PortBindings']
assert all(mount['Type'] != 'bind' for mount in container.get('Mounts', []))
assert set(network['Containers']) == {container['Id']}
print('personnel Oracle isolation: PASS')
PY
cat >"$tmp/schema.sql" <<'SQL'
whenever sqlerror exit failure rollback
set heading off feedback off pagesize 0 verify off echo off
alter session set container=FREEPDB1;
create table T_CORE_ORG (ID varchar2(64) primary key, ORGNAME varchar2(100), PARENTID varchar2(64), ROOTORGID varchar2(64), SAMPLEDEPARTMENT varchar2(100));
create table T_CORE_USER (ID varchar2(64) primary key, ORGID varchar2(64), ORGNAME varchar2(100), STATUS varchar2(20), PARENTORGID varchar2(64), SAMPLEDEPARTMENT varchar2(100));
create table T_CORE_USER_PERMISSION (PERMISSIONTYPEID varchar2(64), TARGETID varchar2(64), USERID varchar2(64));
create table T_EMC_EMP (ID varchar2(64) primary key, NAME varchar2(100), ORGID varchar2(64), CREATEDTIME timestamp, CREATEDBYORGID varchar2(64), FILTERTEST varchar2(1));
insert into T_CORE_ORG values ('org-a','Lab-A',null,'root','Lab-A');
insert into T_CORE_ORG values ('org-b','Lab-B',null,'root','Lab-B');
insert into T_CORE_USER values ('e-1','org-a','Lab-A','1','org-a','Lab-A');
insert into T_CORE_USER values ('e-2','org-b','Lab-B','1','org-b','Lab-B');
insert into T_CORE_USER_PERMISSION values ('T_CORE_ORG','org-a','u-a');
insert into T_EMC_EMP values ('e-1','Alice','org-a',timestamp '2026-01-02 10:00:00','org-a','0');
insert into T_EMC_EMP values ('e-2','Bob','org-b',timestamp '2026-01-01 10:00:00','org-b','0');
commit;
SQL
sed -i "s/FREEPDB1/$PDB_NAME/g" "$tmp/schema.sql"
cat "$tmp/schema.sql" | sql | tee "$tmp/schema.out"
python3 - "$tmp/cases.jsonl" >"$tmp/assertions.sql" <<'PY'
import json, sys
for item in map(json.loads, open(sys.argv[1])):
    sql=item['sql']
    if item['case'].startswith('list-'):
        print("select 'PERSONNEL|%s|'||count(*) from (%s);" % (item['case'], sql))
    elif item['case'] == 'ordered-name-desc':
        print("select 'PERSONNEL|%s|'||name from (%s) where rownum = 1;" % (item['case'], sql))
    else:
        print("select 'PERSONNEL|%s|'||name from (%s);" % (item['case'], sql))
print('exit')
PY
cat >"$tmp/query.sql" <<'SQL'
set heading off feedback off pagesize 0 verify off echo off trimspool on
alter session set container=FREEPDB1;
SQL
sed -i "s/FREEPDB1/$PDB_NAME/g" "$tmp/query.sql"
cat "$tmp/assertions.sql" >>"$tmp/query.sql"
cat "$tmp/query.sql" | sql >"$tmp/oracle.out"
python3 - "$tmp/oracle.out" <<'PY'
import pathlib,sys
actual=[line.strip() for line in pathlib.Path(sys.argv[1]).read_text().splitlines() if line.strip().startswith('PERSONNEL|')]
expected=['PERSONNEL|list-all|2','PERSONNEL|list-org-filter|1','PERSONNEL|list-no-match|0','PERSONNEL|list-standard-name|1','PERSONNEL|list-advanced-name|1','PERSONNEL|list-multi-name|1','PERSONNEL|ordered-name-desc|Bob','PERSONNEL|detail-by-id|Alice','PERSONNEL|detail-org-filter-e2|Bob']
if actual != expected: raise SystemExit('Oracle personnel observation mismatch: '+repr(actual))
print('personnel Oracle execution: PASS (list/filter/no-match/detail)')
PY
python3 - "$tmp" "$EVIDENCE_OUT" "$GLOBAL_XML" "$PERSONNEL_XML" "$RUN_JAR" "$IMAGE" "$ROOT" <<'PY'
import datetime, hashlib, json, pathlib, subprocess, sys
tmp, output, global_xml, personnel_xml, run_jar = map(pathlib.Path, sys.argv[1:6])
image=sys.argv[6]
container=json.loads((tmp/'container-inspect.json').read_text())[0]
network=json.loads((tmp/'network-inspect.json').read_text())[0]
lines=[line.strip() for line in (tmp/'oracle.out').read_text().splitlines() if line.strip().startswith('PERSONNEL|')]
expected=['PERSONNEL|list-all|2','PERSONNEL|list-org-filter|1','PERSONNEL|list-no-match|0','PERSONNEL|list-standard-name|1','PERSONNEL|list-advanced-name|1','PERSONNEL|list-multi-name|1','PERSONNEL|ordered-name-desc|Bob','PERSONNEL|detail-by-id|Alice','PERSONNEL|detail-org-filter-e2|Bob']
project_root=pathlib.Path(str(personnel_xml).split('/src/main/java/',1)[0])
try: project_commit=subprocess.check_output(['git','-C',str(project_root),'rev-parse','HEAD'],text=True).strip()
except Exception: project_commit=None
root=pathlib.Path(sys.argv[7])
bound_test=root/'owned-framework/src/test/java/com/sunwayworld/framework/PersonnelMapperBoundSqlReferenceTest.java'
runner=root/'scripts/tests/test-personnel-mapper-reference.sh'
payload={'schema':1,'status':'executed-pass','scope':'reference XML BoundSql execution only; no Java service, HTTP, page, login or authorization acceptance','production_access':False,'agent_source_commit':subprocess.check_output(['git','rev-parse','HEAD'],text=True).strip(),'source_dirty':bool(subprocess.check_output(['git','status','--porcelain','--',str(bound_test),str(runner)],text=True).strip()),'project_source_commit':project_commit,'sources':{'global_oracle_mapper_sha256':hashlib.sha256(global_xml.read_bytes()).hexdigest(),'personnel_oracle_mapper_sha256':hashlib.sha256(personnel_xml.read_bytes()).hexdigest(),'run_material_size':run_jar.stat().st_size,'bound_test_sha256':hashlib.sha256(bound_test.read_bytes()).hexdigest(),'runner_sha256':hashlib.sha256(runner.read_bytes()).hexdigest(),'synthetic_schema_sha256':hashlib.sha256((tmp/'schema.sql').read_bytes()).hexdigest()},'oracle_image':image,'oracle_image_id':container['Image'],'isolation':{'network_internal':network['Internal'],'network_count':len(container['NetworkSettings']['Networks']),'sole_network_endpoint':set(network['Containers'])=={container['Id']},'port_bindings':container['HostConfig']['PortBindings'],'bind_mount_count':sum(m['Type']=='bind' for m in container.get('Mounts',[]))},'cases':[json.loads(line) for line in (tmp/'cases.jsonl').read_text().splitlines()],'expected':expected,'actual':lines,'observed_at':datetime.datetime.now(datetime.timezone.utc).isoformat(),'limitation':'org_filter restricts list; selectDetail ignores org_filter and returns e-2. Authorization semantics above mapper remain unverified.'}
output.parent.mkdir(parents=True,exist_ok=True); output.write_text(json.dumps(payload,ensure_ascii=False,indent=2)+'\n')
PY
echo "Reference BoundSql cases:"; cat "$tmp/cases.jsonl"
echo "Source hashes:"; cat "$tmp/source.sha256"
echo "Oracle isolation: internal network, no ports, no mounts, disposable container"
docker rm --force --volumes "$name" >/dev/null
docker network rm "$network" >/dev/null
! docker container inspect "$name" >/dev/null 2>&1
! docker network inspect "$network" >/dev/null 2>&1
python3 - "$EVIDENCE_OUT" <<'PY'
import json,pathlib,sys
path=pathlib.Path(sys.argv[1]); data=json.loads(path.read_text()); data['cleanup']={'container_removed':True,'network_removed':True}; path.write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n')
PY
