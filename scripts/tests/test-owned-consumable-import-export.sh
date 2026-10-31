#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
IMAGE="${ORACLE_IMAGE:-ghcr.io/gvenzl/oracle-free@sha256:cd3dff6649f5d67ca3121f5e064b5a5680611e5d34f03442423110778c47dda4}"
EVIDENCE_OUT="${EVIDENCE_OUT:-$ROOT/.scratch/sunway-supplier-exit-api-first/archive/historical/consumable-import-export-20261022.json}"
EVIDENCE_MD="${EVIDENCE_MD:-${EVIDENCE_OUT%.json}.md}"
MODE="${CONSUMABLE_IMPORT_EXPORT_MODE:-isolated}"
TMP="$(mktemp -d)"
NETWORK="owned-consumable-write-net-$(date +%s%N)"
ORACLE="owned-consumable-write-oracle-$(date +%s%N)"
PASSWORD="Probe$(date +%s%N)"
STARTED="$(date -u +%Y-%m-%dT%H:%M:%SZ)"
EXIT_CODE=1
cleanup() {
  docker rm --force "$ORACLE" >/dev/null 2>&1 || true
  docker network rm "$NETWORK" >/dev/null 2>&1 || true
  python3 - "$TMP" <<'PY'
import shutil,sys
shutil.rmtree(sys.argv[1],ignore_errors=True)
PY
}
trap cleanup EXIT
JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh" >"$TMP/framework.log" 2>&1
JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-consumables.sh" >"$TMP/consumables.log" 2>&1
docker network create --internal --driver bridge "$NETWORK" >/dev/null
docker run --detach --name "$ORACLE" --network "$NETWORK" --restart=no --memory=4g --cpus=2 --env ORACLE_PASSWORD="$PASSWORD" "$IMAGE" >/dev/null
ready=0
for _ in $(seq 1 120); do
  out="$(docker exec -i "$ORACLE" /opt/oracle/product/26ai/dbhomeFree/bin/sqlplus -s / as sysdba <<'SQL' 2>/dev/null || true
set heading off feedback off pagesize 0
select name from v$pdbs where open_mode = 'READ WRITE';
exit
SQL
)"
  if printf '%s' "$out" | tr -d '[:space:]' | grep -q FREEPDB1; then ready=1; break; fi
  sleep 5
done
[ "$ready" = 1 ]
printf 'url=jdbc:oracle:thin:@127.0.0.1:1521/FREEPDB1\nuser=system\npassword=%s\n' "$PASSWORD" >"$TMP/oracle.properties"
docker cp "$JDK" "$ORACLE:/tmp/jdk1.8.0_391"
docker cp "$ROOT/owned-consumables/target" "$ORACLE:/tmp/owned-consumables-target"
docker cp "$ROOT/owned-framework/target" "$ORACLE:/tmp/owned-framework-target"
docker cp "$TMP/oracle.properties" "$ORACLE:/tmp/oracle.properties"
set +e
docker exec "$ORACLE" /tmp/jdk1.8.0_391/bin/java -cp "/tmp/owned-consumables-target/test-classes:/tmp/owned-consumables-target/classes:/tmp/owned-framework-target/classes:/tmp/owned-framework-target/public-deps/*" OwnedConsumablesImportExportHttpContractTest /tmp/oracle.properties "$MODE" >"$TMP/run.log" 2>&1
EXIT_CODE=$?
set -e
docker inspect "$ORACLE" >"$TMP/container.json"
docker network inspect "$NETWORK" >"$TMP/network.json"
python3 - "$ROOT" "$TMP" "$EVIDENCE_OUT" "$EVIDENCE_MD" "$STARTED" "$EXIT_CODE" "$MODE" <<'PY'
import datetime,hashlib,json,pathlib,re,subprocess,sys
root=pathlib.Path(sys.argv[1]); tmp=pathlib.Path(sys.argv[2]); evidence=pathlib.Path(sys.argv[3]); markdown=pathlib.Path(sys.argv[4]); started=sys.argv[5]; exit_code=int(sys.argv[6]); mode=sys.argv[7]
run=(tmp/'run.log').read_text(errors='replace'); container=json.loads((tmp/'container.json').read_text())[0]; network=json.loads((tmp/'network.json').read_text())[0]
paths=['project/src/main/java/com/sunwayworld/emc/module/item/consume/consume/resource/impl/EmcConsumeResourceImpl.java','owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedQueryAccessFilter.java','owned-consumables/src/main/java/com/sunwayworld/owned/consumables/OwnedConsumablesConfiguration.java','owned-consumables/src/main/java/com/sunwayworld/owned/consumables/OwnedConsumablesReadDao.java','owned-consumables/src/main/java/com/sunwayworld/owned/consumables/OwnedConsumablesReadService.java','owned-consumables/src/main/java/com/sunwayworld/owned/consumables/OwnedConsumablesStockDao.java','owned-consumables/src/main/java/com/sunwayworld/owned/consumables/OwnedConsumablesStockService.java','owned-consumables/src/main/java/com/sunwayworld/owned/consumables/OwnedConsumablesImportExportController.java','owned-consumables/src/test/java/OwnedConsumablesImportExportHttpContractTest.java','scripts/prepare-owned-public-deps.py','scripts/tests/test-owned-consumable-import-export.sh']
sha={p:hashlib.sha256((root/p).read_bytes()).hexdigest() for p in paths}; dirty=bool(subprocess.check_output(['git','status','--porcelain','--',*paths],cwd=root,text=True).strip()); m=re.search(r'consumable import/export(?: default mode)?: PASS \((\d+) checks;',run); checks=int(m.group(1)) if m else 0; source_commit=subprocess.check_output(['git','rev-parse','HEAD'],cwd=root,text=True).strip(); image=container['Config']['Image']; image_id=subprocess.check_output(['docker','image','inspect',image,'--format','{{.Id}}'],text=True).strip()
routes=['GET /core/module/item/files/{templateCode}/action/download-template/{service}','POST /core/module/item/files/{id}/action/import/emcConsumeImportFileFunction']
checks_detail=['default-mode template/import denial'] if mode != 'isolated' else ['loopback-only filter','consumable management permission','historical XLS template headers/comments','material matching and creation','stock creation/update','inventory log','Oracle transaction rollback for invalid rows','organization scope','SEQ_CORE identity','post-commit audit','audit credential omission']
scope='default-mode consumables template/import HTTP write denial only; disposable synthetic Oracle' if mode != 'isolated' else 'owned isolated consumables template export and Excel import; disposable synthetic Oracle only; no browser/page acceptance'
payload={'schema':1,'status':'executed-pass' if exit_code==0 and checks else 'executed-fail','production_access':False,'scope':scope,'started_at':started,'image_reference':image,'image_id':image_id,'source_commit':source_commit,'source_sha256':sha,'source_dirty':dirty,'checks':checks,'java_exit_code':exit_code,'oracle_error_codes':sorted(set(re.findall(r'ORA-\d+',run))),'transcript_sha256':hashlib.sha256(run.encode()).hexdigest(),'isolation':{'network_internal':network.get('Internal') is True,'attached_network_count':len(container.get('NetworkSettings',{}).get('Networks',{})),'sole_endpoint':set(network.get('Containers',{}))=={container['Id']},'port_bindings':container.get('HostConfig',{}).get('PortBindings') or {},'bind_mount_count':sum(m.get('Type')=='bind' for m in container.get('Mounts',[]))},'write':{'mode':'owned.consumables.write.mode='+mode,'routes':routes,'checks':checks_detail,'default_mode':'bounded consumables HTTP routes remain write-denied without the explicit isolated mode'},'cleanup':'Oracle container and internal network removed by trap; no production endpoint used','finished_at':datetime.datetime.now(datetime.timezone.utc).isoformat()}
evidence.parent.mkdir(parents=True,exist_ok=True); evidence.write_text(json.dumps(payload,indent=2)+'\n'); markdown.write_text(f'''# Consumable import/export slice (2026-10-22)\n\nThe consumables HTTP import/export contract was exercised in `{mode}` mode.\n\n```text\n{run.strip()}\n```\n\nThe isolated mode covers historical XLS template export, valid material/stock import, invalid-row rollback, permission denial and audit. Other consumable workflows remain open.\n''')
PY
if [ "$EXIT_CODE" -ne 0 ]; then cat "$TMP/run.log" >&2; exit "$EXIT_CODE"; fi
cat "$TMP/run.log"
echo "consumable import/export evidence: $EVIDENCE_OUT"
