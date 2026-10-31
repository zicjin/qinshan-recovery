#!/usr/bin/env python3
"""Run the original equipment HTTP runtime against a disposable internal Oracle."""
import argparse, datetime, hashlib, ipaddress, json, os, re, secrets, subprocess, tempfile, time, uuid
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
import sys
sys.path.insert(0, str(ROOT / "scripts"))
from evidence_output import guard_output_path
IMAGE = 'ghcr.io/gvenzl/oracle-free@sha256:cd3dff6649f5d67ca3121f5e064b5a5680611e5d34f03442423110778c47dda4'

def run(args, **kwargs):
    return subprocess.run(args, capture_output=True, text=True, timeout=kwargs.pop('timeout', 30), **kwargs)

def private_file(path, content):
    with os.fdopen(os.open(str(path), os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600), 'w') as out: out.write(content)

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--image', default=IMAGE); parser.add_argument('--output', type=Path, default=ROOT.parent / '.scratch/sunway-supplier-exit-api-first/diagnostic-evidence/legacy/equipment-runtime-oracle.json')
    parser.add_argument('--ready-timeout', type=int, default=600); args = parser.parse_args()
    report = {'schema': 1, 'status': 'not-executed', 'production_access': False,
              'scope': 'actual historical main/resource HTTP; disposable synthetic Oracle only; no browser acceptance',
              'started_at': datetime.datetime.now(datetime.timezone.utc).isoformat()}
    nonce = uuid.uuid4().hex; name = 'lims-equipment-query-' + nonce; network = 'lims-equipment-query-net-' + nonce
    created = network_created = False
    def save(): guard_output_path(args.output); args.output.parent.mkdir(parents=True, exist_ok=True); args.output.write_text(json.dumps(report, indent=2) + '\n')
    def sql(source):
        return run(['docker', 'exec', '-i', name, 'sqlplus', '-s', '/ as sysdba'], input='whenever sqlerror exit failure rollback\nset pagesize 0 feedback off heading off echo off verify off\nalter session set container=FREEPDB1;\n' + source + '\nexit\n', timeout=60)
    try:
        if not re.fullmatch(r'[A-Za-z0-9./_-]+@sha256:[0-9a-f]{64}', args.image): raise RuntimeError('image-must-be-digest-pinned')
        inspection = run(['docker', 'image', 'inspect', args.image])
        if inspection.returncode: raise RuntimeError('pinned-image-unavailable')
        image = json.loads(inspection.stdout)[0]
        if image.get('Os') != 'linux' or image.get('Architecture') != 'amd64': raise RuntimeError('requires-linux-amd64')
        report['image_reference'], report['image_id'] = args.image, image['Id']
        report['source_commit'] = run(['git', 'rev-parse', 'HEAD'], cwd=ROOT).stdout.strip()
        sources = [
            ROOT / 'owned-equipment/src/test/java/OwnedEquipmentRuntimeHttpTest.java',
            ROOT / 'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentConfiguration.java',
            ROOT / 'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentExceptionHandler.java',
            ROOT / 'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentReadDao.java',
            ROOT / 'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentReadService.java',
            ROOT / 'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentPlanDao.java',
            ROOT / 'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentPlanService.java',
            ROOT / 'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentRecordDao.java',
            ROOT / 'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentRecordService.java',
            ROOT / 'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentPerCheckPlanDao.java',
            ROOT / 'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentPerCheckPlanService.java',
            ROOT / 'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentPerCheckRecordDao.java',
            ROOT / 'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentPerCheckRecordService.java',
            ROOT / 'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentUseRecordDao.java',
            ROOT / 'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentUseRecordService.java',
            ROOT / 'owned-equipment/src/main/resources/META-INF/spring.factories',
            ROOT / 'scripts/build-owned-equipment.sh', ROOT / 'scripts/tests/test-owned-equipment-runtime.sh',
            Path(__file__), ROOT / 'scripts/build-owned-framework.sh',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/framework/constant/Constant.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/framework/restful/data/RestJsonWrapperBean.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedReadRequest.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedReadWebConfiguration.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedQueryAccessFilter.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedWriteSupport.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedQueryAccessConfiguration.java',
            ROOT / 'owned-framework/src/main/resources/META-INF/spring.factories',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/QueryRequest.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/data/EquipmentQueryService.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/security/JdbcPersonnelIdentity.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/security/JdbcPersonnelIdentityProvider.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/security/OwnedIdentityProvider.java',
            ROOT / 'owned-framework/src/test/java/com/sunwayworld/framework/EquipmentQueryServiceContractTest.java']
        report['source_sha256'] = {str(p.relative_to(ROOT)): hashlib.sha256(p.read_bytes()).hexdigest() for p in sources}
        report['source_dirty'] = bool(run(['git', 'status', '--porcelain', '--'] + [str(p) for p in sources], cwd=ROOT).stdout.strip())
        build = run([str(ROOT / 'scripts/tests/test-owned-equipment-runtime.sh'), '--build-only'], timeout=180)
        report['build_transcript_sha256'] = hashlib.sha256((build.stdout + build.stderr).encode()).hexdigest()
        if build.returncode: report['build_error_lines'] = [line for line in (build.stdout + build.stderr).splitlines() if 'error:' in line or 'Exception' in line]
        if build.returncode: raise RuntimeError('owned-build-failed')
        report['owned_build'] = 'passed; owned public contracts and supplier-runtime rejection'
        report['owned_jar_sha256'] = hashlib.sha256((ROOT / 'owned-framework/target/lims-owned-framework-1.0.0.jar').read_bytes()).hexdigest()
        report['equipment_jar_sha256'] = hashlib.sha256((ROOT / 'owned-equipment/target/lims-owned-equipment-1.0.0.jar').read_bytes()).hexdigest()
        personnel_jar = ROOT / 'owned-personnel/target/lims-owned-personnel-1.0.0.jar'
        if personnel_jar.is_file():
            report['personnel_jar_sha256'] = hashlib.sha256(personnel_jar.read_bytes()).hexdigest()
        manifest = ROOT / 'owned-equipment/target/historical-source.sha256'
        report['historical_source_sha256'] = {}
        for line in manifest.read_text().splitlines():
            digest, path = line.split('  ', 1)
            report['historical_source_sha256'][path] = digest
        report['historical_source_manifest_sha256'] = hashlib.sha256(manifest.read_bytes()).hexdigest()
        project = Path(os.environ.get('LIMS_PROJECT_ROOT', str(ROOT / 'project'))).resolve()
        resource_roots = [project / 'src/main/resources/templates/emc/module/mdm/equipt/equipts',
                          project / 'src/main/resources/static/emc/module/mdm/equipt/equipts']
        report['historical_resource_sha256'] = {
            str(path.relative_to(project)): hashlib.sha256(path.read_bytes()).hexdigest()
            for root in resource_roots for path in sorted(root.rglob('*')) if path.is_file()
        }
        with tempfile.TemporaryDirectory(prefix='lims-equipment-query-private-') as temporary:
            temporary = Path(temporary); env_file, props = temporary / 'oracle.env', temporary / 'jdbc.properties'
            oracle_password = 'Ephemeral' + secrets.token_hex(8); private_file(env_file, 'ORACLE_PASSWORD=' + oracle_password + '\n')
            if run(['docker', 'network', 'create', '--internal', '--driver', 'bridge', network]).returncode: raise RuntimeError('network-create-failed')
            network_created = True
            result = run(['docker', 'create', '--name', name, '--network', network, '--restart', 'no', '--memory', '3g', '--cpus', '2', '--env-file', str(env_file), args.image])
            if result.returncode: raise RuntimeError('container-create-failed')
            created = True; container_id = result.stdout.strip()
            if run(['docker', 'start', name]).returncode: raise RuntimeError('container-start-failed')
            deadline = time.monotonic() + args.ready_timeout
            while time.monotonic() < deadline:
                startup = run(['docker', 'logs', name]); ready = sql("SELECT 'EQUIPMENT_READY' FROM DUAL;")
                if 'DATABASE IS READY TO USE!' in startup.stdout + startup.stderr and ready.returncode == 0 and 'EQUIPMENT_READY' in ready.stdout: break
                if run(['docker', 'inspect', '--format', '{{.State.Running}}', name]).stdout.strip() != 'true': raise RuntimeError('container-exited-before-ready')
                time.sleep(5)
            else: raise RuntimeError('oracle-not-ready')
            container = json.loads(run(['docker', 'inspect', name]).stdout)[0]; net = json.loads(run(['docker', 'network', 'inspect', network]).stdout)[0]
            isolation = {'network_internal': net['Internal'], 'attached_network_count': len(container['NetworkSettings']['Networks']), 'sole_endpoint': set(net['Containers']) == {container_id}, 'port_bindings': container['HostConfig']['PortBindings'], 'bind_mount_count': sum(m['Type'] == 'bind' for m in container.get('Mounts', []))}
            report['isolation'] = isolation
            if not isolation['network_internal'] or isolation['attached_network_count'] != 1 or not isolation['sole_endpoint'] or isolation['port_bindings'] or isolation['bind_mount_count']: raise RuntimeError('isolation-mismatch')
            address = container['NetworkSettings']['Networks'][network]['IPAddress']
            if not ipaddress.ip_address(address).is_private: raise RuntimeError('non-private-oracle-address')
            fixture_password = 'Fixture' + secrets.token_hex(8)
            result = sql('CREATE USER equipment_fixture IDENTIFIED BY ' + fixture_password + ';\nGRANT CREATE SESSION, CREATE TABLE, UNLIMITED TABLESPACE TO equipment_fixture;')
            if result.returncode: raise RuntimeError('synthetic-user-create-failed')
            private_file(props, 'url=jdbc:oracle:thin:@//' + address + ':1521/FREEPDB1\nuser=equipment_fixture\npassword=' + fixture_password + '\n')
            jdk = Path(os.environ.get('JAVA_HOME', str(ROOT / 'build/jdk1.8.0_391'))); output = ROOT / 'owned-framework/target'; cp = ':'.join(str(output / p) for p in ['classes', 'public-deps/*'])
            result = run([str(jdk / 'bin/java'), '-cp', str(ROOT / 'owned-equipment/target/classes') + ':' + str(ROOT / 'owned-equipment/target/test-classes') + ':' + cp, 'OwnedEquipmentRuntimeHttpTest', str(props), str(temporary / 'audit.jsonl')], cwd=temporary, timeout=120)
            transcript = result.stdout + '\n' + result.stderr; match = re.search(r'equipment runtime HTTP: PASS \((\d+) checks; actual historical main/resource, RSA session and synthetic Oracle only\)', transcript)
            report['checks'] = int(match.group(1)) if match else None; report['java_exit_code'] = result.returncode; report['oracle_error_codes'] = sorted(set(re.findall(r'ORA-\d{5}', transcript))); report['transcript_sha256'] = hashlib.sha256(transcript.encode()).hexdigest(); report['status'] = 'executed-pass' if result.returncode == 0 and match else 'executed-fail'
            if report['status'] != 'executed-pass':
                report['causes'] = [line.strip() for line in transcript.splitlines() if line.strip().startswith('Caused by:')][:8]
                report['assertions'] = re.findall(r'AssertionError: ([^\n]*)', transcript)
                report['failure_trace'] = [line.strip() for line in transcript.splitlines()
                                          if 'ClassCastException' in line or 'IllegalStateException' in line
                                          or line.strip().startswith('at com.sunwayworld.')][:20]
                report['exception_classes'] = sorted(set(re.findall(r'\b(?:java|oracle|org|com)\.[A-Za-z0-9_.$]*(?:Exception|Error)\b', transcript)))
                raise RuntimeError('equipment-test-failed')
            print('equipment runtime HTTP: PASS (' + str(report['checks']) + ' checks; synthetic Oracle only)'); return 0
    except (OSError, ValueError, KeyError, RuntimeError, subprocess.TimeoutExpired) as failure:
        report['reason'] = str(failure) if isinstance(failure, RuntimeError) else type(failure).__name__; print('equipment runtime HTTP: ' + report['reason']); return 1
    finally:
        cleanup = {}
        if created: cleanup['container_removed'] = run(['docker', 'rm', '--force', '--volumes', name]).returncode == 0; cleanup['anonymous_volumes_removed'] = cleanup['container_removed']
        if network_created: cleanup['network_removed'] = run(['docker', 'network', 'rm', network]).returncode == 0
        report['cleanup'] = cleanup; report['finished_at'] = datetime.datetime.now(datetime.timezone.utc).isoformat(); save()

if __name__ == '__main__': raise SystemExit(main())
