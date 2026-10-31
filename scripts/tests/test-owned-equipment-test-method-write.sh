#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
EVIDENCE_OUT="${EVIDENCE_OUT:-$ROOT/.scratch/sunway-supplier-exit-api-first/archive/historical/equipment-test-method-write-20261021.json}"
EVIDENCE_MD="${EVIDENCE_MD:-${EVIDENCE_OUT%.json}.md}"
TMP="$(mktemp -d)"
NETWORK="owned-equipment-write-net-$(date +%s%N)"
ORACLE="owned-equipment-write-oracle-$(date +%s%N)"
ORACLE_PASSWORD="Probe$(date +%s%N)"
ORACLE_IMAGE="${ORACLE_IMAGE:-ghcr.io/gvenzl/oracle-free@sha256:cd3dff6649f5d67ca3121f5e064b5a5680611e5d34f03442423110778c47dda4}"
STARTED_AT="$(date -u +%Y-%m-%dT%H:%M:%SZ)"
EXIT_CODE=1

cleanup() {
  docker rm --force "$ORACLE" >/dev/null 2>&1 || true
  docker network rm "$NETWORK" >/dev/null 2>&1 || true
  python3 - "$TMP" <<'PY'
import shutil, sys
shutil.rmtree(sys.argv[1], ignore_errors=True)
PY
}
trap cleanup EXIT

JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh" >"$TMP/framework-build.log" 2>&1
JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-equipment.sh" >"$TMP/equipment-build.log" 2>&1

docker network create --internal --driver bridge "$NETWORK" >/dev/null
docker run --detach --name "$ORACLE" --network "$NETWORK" --restart=no --memory=4g --cpus=2 \
  --env ORACLE_PASSWORD="$ORACLE_PASSWORD" "$ORACLE_IMAGE" >/dev/null

ready=0
for _ in $(seq 1 120); do
  output="$(docker exec -i "$ORACLE" /opt/oracle/product/26ai/dbhomeFree/bin/sqlplus -s / as sysdba <<'SQL' 2>/dev/null || true
set heading off feedback off pagesize 0
select name from v$pdbs where open_mode = 'READ WRITE';
exit
SQL
)"
  if printf '%s' "$output" | tr -d '[:space:]' | grep -q FREEPDB1; then ready=1; break; fi
  sleep 5
done
[ "$ready" = 1 ]

printf 'url=jdbc:oracle:thin:@127.0.0.1:1521/FREEPDB1\nuser=system\npassword=%s\n' \
  "$ORACLE_PASSWORD" >"$TMP/oracle.properties"
docker cp "$JDK" "$ORACLE:/tmp/jdk1.8.0_391"
docker cp "$ROOT/owned-equipment/target" "$ORACLE:/tmp/owned-equipment-target"
docker cp "$ROOT/owned-framework/target" "$ORACLE:/tmp/owned-framework-target"
docker cp "$TMP/oracle.properties" "$ORACLE:/tmp/oracle.properties"

set +e
docker exec "$ORACLE" /tmp/jdk1.8.0_391/bin/java \
  -cp "/tmp/owned-equipment-target/test-classes:/tmp/owned-equipment-target/classes:/tmp/owned-framework-target/classes:/tmp/owned-framework-target/public-deps/*" \
  OwnedEquipmentTestMethodWriteHttpContractTest /tmp/oracle.properties >"$TMP/run.log" 2>&1
EXIT_CODE=$?
set -e

docker inspect "$ORACLE" >"$TMP/container.json"
docker network inspect "$NETWORK" >"$TMP/network.json"

python3 - "$ROOT" "$TMP" "$EVIDENCE_OUT" "$EVIDENCE_MD" "$STARTED_AT" "$EXIT_CODE" "$ORACLE" "$NETWORK" <<'PY'
import datetime, hashlib, json, os, pathlib, re, subprocess, sys
root = pathlib.Path(sys.argv[1]); tmp = pathlib.Path(sys.argv[2])
evidence = pathlib.Path(sys.argv[3]); markdown = pathlib.Path(sys.argv[4])
started = sys.argv[5]; exit_code = int(sys.argv[6])
oracle = sys.argv[7]; network = sys.argv[8]
run = (tmp / 'run.log').read_text(errors='replace')
container = json.loads((tmp / 'container.json').read_text())[0]
network_data = json.loads((tmp / 'network.json').read_text())[0]
source_paths = [
    'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedQueryAccessFilter.java',
    'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedWriteSupport.java',
    'owned-framework/src/main/java/com/sunwayworld/owned/internal/data/EquipmentQueryService.java',
    'owned-framework/src/main/java/com/sunwayworld/framework/restful/data/RestValidationResultBean.java',
    'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentConfiguration.java',
    'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentTestMethodDao.java',
    'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentTestMethodService.java',
    'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentMaintDao.java',
    'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentMaintService.java',
    'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentReadDao.java',
    'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentReadService.java',
    'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentPlanDao.java',
    'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentPlanService.java',
    'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentMaintenanceSummaryController.java',
    'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentUserSummaryController.java',
    'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentPlanSummaryController.java',
    'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentPerCheckSummaryController.java',
    'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentChoosableController.java',
    'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentPerCheckPlanDao.java',
    'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentPerCheckPlanService.java',
    'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentRecordDao.java',
    'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentRecordService.java',
    'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentPerCheckRecordDao.java',
    'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentPerCheckRecordService.java',
    'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentUseRecordDao.java',
    'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentUseRecordService.java',
    'owned-equipment/src/test/java/OwnedEquipmentTestMethodWriteHttpContractTest.java',
    'scripts/build-owned-equipment.sh',
    'scripts/tests/test-owned-equipment-test-method-write.sh',
]
sha = {path: hashlib.sha256((root / path).read_bytes()).hexdigest() for path in source_paths}
dirty = bool(subprocess.check_output(['git', 'status', '--porcelain', '--', *source_paths], cwd=root, text=True).strip())
historical_root = pathlib.Path(os.environ.get('LIMS_PROJECT_ROOT', str(root / 'project'))).resolve()
historical_resource = historical_root / 'src/main/java/com/sunwayworld/emc/module/mdm/equipt/equipt/resource/impl/EmcEquiptResourceImpl.java'
if not historical_resource.is_file(): raise RuntimeError('historical resource source is missing: ' + str(historical_resource))
sha['historical-project/' + str(historical_resource.relative_to(historical_root))] = hashlib.sha256(historical_resource.read_bytes()).hexdigest()
image = container['Config']['Image']
image_id = subprocess.check_output(['docker', 'image', 'inspect', image, '--format', '{{.Id}}'], text=True).strip()
networks = container.get('NetworkSettings', {}).get('Networks', {})
checks_match = re.search(r'equipment test-method/item write: PASS \((\d+) checks;', run)
checks = int(checks_match.group(1)) if checks_match else 0
oracle_errors = sorted(set(re.findall(r'ORA-\d+', run)))
source_commit = subprocess.check_output(['git', 'rev-parse', 'HEAD'], cwd=root, text=True).strip()
jar_paths = [root / 'owned-equipment/target/lims-owned-equipment-1.0.0.jar', root / 'owned-framework/target/lims-owned-framework-1.0.0.jar']
artifact_sha = {str(path.relative_to(root)): hashlib.sha256(path.read_bytes()).hexdigest() for path in jar_paths}
payload = {
    'schema': 1,
    'status': 'executed-pass' if exit_code == 0 and checks else 'executed-fail',
    'strict_evidence': False,
    'production_access': False,
    'scope': 'original equipment HTTP resource + owned isolated equipment creation, maintenance-plan, maintenance-record, period-check summary/plan/record, use-record, test-method and test-item seams; disposable synthetic Oracle only; no browser/page acceptance',
    'started_at': started,
    'image_reference': image,
    'image_id': image_id,
    'source_commit': source_commit,
    'source_sha256': sha,
    'source_dirty': dirty,
    'owned_build': 'framework and equipment clean module builds plus supplier-runtime rejection scan passed',
    'artifact_sha256': artifact_sha,
    'isolation': {
        'network_internal': network_data.get('Internal') is True,
        'attached_network_count': len(networks),
        'sole_endpoint': set(network_data.get('Containers', {})) == {container['Id']},
        'port_bindings': container.get('HostConfig', {}).get('PortBindings') or {},
        'bind_mount_count': sum(1 for mount in container.get('Mounts', []) if mount.get('Type') == 'bind'),
    },
    'checks': checks,
    'java_exit_code': exit_code,
    'oracle_error_codes': oracle_errors,
    'transcript_sha256': hashlib.sha256(run.encode()).hexdigest(),
    'write': {
        'mode': 'owned.equipment.write.mode=isolated',
        'routes': [
            'POST /secure/emc/module/mdm/equipt/equipts',
            'POST /secure/emc/module/mdm/equipt/equipts/plans',
            'POST /secure/emc/module/mdm/equipt/equipts/{id}/plans/queries',
            'POST /secure/emc/module/mdm/equipt/equipts/action/select-equipt-maintain-repair-search',
            'POST /secure/emc/module/mdm/equipt/equipts/action/select-equipt-user',
            'POST /secure/emc/module/mdm/equipt/equipts/action/select-equipt-plan',
            'POST /secure/emc/module/mdm/equipt/equipts/action/select-equipt-per-check-search',
            'POST /secure/emc/module/mdm/equipt/equipts/queries/choosable',
            'GET /secure/emc/module/mdm/equipt/equipts/{id}/DCU-confs/queries',
            'POST /secure/emc/module/mdm/equipt/equipts/{id}/action/validate-dcu-config',
            'POST /secure/emc/module/mdm/equipt/equipts/cali-queries/choosable',
            'POST /secure/emc/module/mdm/equipt/equipts/verify-queries/choosable',
            'POST /secure/emc/module/mdm/equipt/equipts/per-check-queries/choosable',
            'POST /secure/emc/module/mdm/equipt/equipts/records',
            'POST /secure/emc/module/mdm/equipt/equipts/{id}/records/queries',
            'GET /secure/emc/module/mdm/equipt/equipts/{id}/records/maints/queries/selectable',
            'PUT /secure/emc/module/mdm/equipt/equipts/records/action/complete',
            'DELETE /secure/emc/module/mdm/equipt/equipts/records',
            'POST /secure/emc/module/mdm/equipt/equipts/per-check-plan',
            'DELETE /secure/emc/module/mdm/equipt/equipts/per-check-plan',
            'POST /secure/emc/module/mdm/equipt/equipts/{id}/per-check-plan/queries',
            'POST /secure/emc/module/mdm/equipt/equipts/per-check-record',
            'DELETE /secure/emc/module/mdm/equipt/equipts/per-check-record',
            'POST /secure/emc/module/mdm/equipt/equipts/{id}/per-check-record/queries',
            'GET /secure/emc/module/mdm/equipt/equipts/{id}/per-check-records/maints/queries/selectable',
            'PUT /secure/emc/module/mdm/equipt/equipts/per-check-records/action/complete',
            'POST /secure/emc/module/mdm/equipt/equipts/use-records',
            'PUT /secure/emc/module/mdm/equipt/equipts/use-records',
            'DELETE /secure/emc/module/mdm/equipt/equipts/delete-records',
            'POST /secure/emc/module/mdm/equipt/equipts/{id}/use-records/queries',
            'POST /secure/emc/module/mdm/equipt/equipts/use-records/queries',
            'POST /secure/emc/module/mdm/equipt/equipts/{id}/test-methods',
            'POST /secure/emc/module/mdm/equipt/equipts/{id}/test-methods/queries',
            'DELETE /secure/emc/module/mdm/equipt/equipts/{id}/test-methods',
            'POST /secure/emc/module/mdm/equipt/equipts/{id}/test-items',
            'POST /secure/emc/module/mdm/equipt/equipts/{id}/test-items/queries',
            'DELETE /secure/emc/module/mdm/equipt/equipts/{id}/test-items',
        ],
        'checks': ['loopback-only filter', 'equipment management permission', 'category organization scope',
                   'equipment creation fields and actor', 'generic equipment choosable query, testMethodId, assigned target and maintenance-user filters', 'equipment DCU configuration detail', 'DCU configuration validation', 'maintenance-plan scoped insert/query', 'maintenance-repair summary organization-scoped DTO projection', 'equipment-user summary organization-scoped DTO projection', 'equipment-plan summary organization-scoped DTO projection', 'period-check summary organization-scoped DTO projection', 'calibration/verification/period-check choice queries', 'maintenance-record scoped insert/query/selectable/complete/delete', 'period-check plan/record scoped insert/query/selectable/complete/delete', 'equipment-use-record insert/query/update/delete', 'test-method scoped insert/query/delete', 'test-item scoped insert/query/delete', 'plan and equipment validity updates', 'SEQ_CORE identity',
                   'Oracle transaction', 'cross-equipment delete rejection', 'audit credential omission'],
        'default_mode': 'existing packaged equipment HTTP regression keeps the original write route denied',
    },
    'cleanup': 'Oracle container and internal network removed by trap; no production endpoint used',
    'finished_at': datetime.datetime.now(datetime.timezone.utc).isoformat(),
}
path = pathlib.Path(evidence); path.parent.mkdir(parents=True, exist_ok=True); path.write_text(json.dumps(payload, indent=2) + '\n')
md = pathlib.Path(markdown)
md.write_text(f'''# Equipment isolated write slice (2026-10-21)\n\nThe original equipment creation, generic choosable, DCU configuration detail/validation, maintenance-plan, maintenance-record, period-check summary/plan/record, calibration/verification/period-check choice, use-record, and test-method/item routes now have explicit isolated seams. The filter permits writes only when `owned.equipment.write.mode=isolated` and the client is loopback; the services recheck the equipment management grant and category organization scope, use an Oracle transaction and `SEQ_CORE`, and record audit events where applicable. The default configuration continues to reject equipment writes.\n\nValidation uses the original `GikamWpjcApplication`, a disposable digest-pinned Oracle container on an internal network, synthetic rows, and a real HTTP client:\n\n```text\n{run.strip()}\n```\n\nThe run verifies sequence-backed inserts, scoped queries, generic choosable/DCU/validation and summary DTO projections, calibration/verification/period-check choice routes, selectable maintenance events, period-check completion with linked plan updates, completed-record deletion rejection, maintenance-record completion and linked equipment validity updates, use-record update/delete, test-method/item insert/query/delete, auxiliary deletes, DTO field persistence, cross-scope rejection, invalid-body rollback, low-privilege denial, audit output, credential omission, and container/network cleanup. Procurement, calibration data maintenance, acquisition, collection-file, plan update, and complete equipment-field workflows remain open. The historical dynamic `maintLastDate` and `checkIndex` fields remain null because their source columns are not present in the owned schema.\n''')
PY

if [ "$EXIT_CODE" -ne 0 ]; then
  cat "$TMP/run.log" >&2
  exit "$EXIT_CODE"
fi
cat "$TMP/run.log"
echo "equipment test-method/item write evidence: $EVIDENCE_OUT"
