#!/usr/bin/env python3
"""Exercise shipped equipment and consumables pages with a named browser and synthetic Oracle."""
import argparse
import datetime
import hashlib
import json
import os
import re
import socket
import shutil
from pathlib import Path
import subprocess
import tempfile
import time
import uuid
import sys

ROOT = Path(__file__).resolve().parents[4]
sys.path.insert(0, str(ROOT / 'scripts'))
sys.path.insert(0, str(Path(__file__).resolve().parents[4] / 'tools'))
from owned_readiness import wait_until
from vertical_slices.matrix import VERIFIER_PATHS

WEB = ROOT / 'owned-web'
IMAGE = 'ghcr.io/gvenzl/oracle-free@sha256:cd3dff6649f5d67ca3121f5e064b5a5680611e5d34f03442423110778c47dda4'
REDIS_IMAGE = 'public.ecr.aws/docker/library/redis:7.4-alpine@sha256:57e4255cc971b1c8d6f8a6ca283ac01bc3ecdd914273ef6bfaede87aaecd6541'
DIST = ROOT / 'owned-distribution/target/distribution'
DISTRIBUTION_SOURCE_SCOPES = (
    'owned-framework/src', 'owned-personnel/src', 'owned-equipment/src',
    'owned-consumables/src', 'owned-attachments/src', 'owned-report/src',
    'owned-scheduler/src', 'owned-web/src', 'owned-distribution/start.sh',
    'owned-distribution/package.py', 'owned-distribution/example/owned-application.properties',
    'scripts/build-owned-framework.sh', 'scripts/build-owned-personnel.sh',
    'scripts/build-owned-equipment.sh', 'scripts/build-owned-consumables.sh',
    'scripts/build-owned-attachments.sh', 'scripts/build-owned-report.sh',
    'scripts/build-owned-scheduler.sh', 'scripts/build-owned-distribution.sh',
    'scripts/prepare-owned-public-deps.py', 'scripts/reject-supplier-runtime.sh',
    'owned-web/build-owned-web.sh', 'owned-web/original-personnel-sha256.json',
    'owned-web/original-inventory-sha256.json',
)


class StockOnlyComplete(Exception):
    pass


def run(command, timeout=300, **kwargs):
    return subprocess.run([str(part) for part in command], capture_output=True,
                          text=True, timeout=timeout, **kwargs)


def passed(result, label):
    if result.returncode:
        codes = sorted(set(re.findall(r'ORA-\d{5}', result.stdout + result.stderr)))
        raise RuntimeError(label + ' failed: ' + ','.join(codes) + ' ' + result.stdout[-300:] + result.stderr[:700])
    return result.stdout


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--output', type=Path,
                        default=WEB / 'evidence/browser-inventory-20261020.json')
    parser.add_argument('--stock-only', action='store_true',
                        help='stop after browser stock writes and fixture restoration')
    parser.add_argument('--stock-create-delete-only', action='store_true',
                        help='run only browser stock creation/deletion after the stock page opens')
    parser.add_argument('--material-create-only', action='store_true',
                        help='run only browser material creation after the consumables page opens')
    parser.add_argument('--material-lifecycle-only', action='store_true',
                        help='run browser material creation, update and deletion after the consumables page opens')
    parser.add_argument('--distribution', action='store_true',
                        help='start the packaged application through owned-distribution/start.sh')
    parser.add_argument('--consumables-query-projection-only', action='store_true',
                        help='run the consumables full visible projection and filter read flow only')
    parser.add_argument('--equipment-query-projection-only', action='store_true',
                        help='run the equipment full visible projection and filter read flow only')
    parser.add_argument('--equipment-plan-read-only', action='store_true',
                        help='stop after reading the scoped equipment maintenance plan')
    parser.add_argument('--equipment-plan-lifecycle-only', action='store_true',
                        help='run browser maintenance-plan creation and deletion after reading the plan')
    parser.add_argument('--equipment-record-read-only', action='store_true',
                        help='stop after reading the scoped equipment maintenance record')
    parser.add_argument('--equipment-record-lifecycle-only', action='store_true',
                        help='run browser maintenance-record creation and deletion after reading the record')
    parser.add_argument('--equipment-period-read-only', action='store_true',
                        help='stop after reading the scoped equipment period-check plan and record')
    parser.add_argument('--equipment-period-lifecycle-only', action='store_true',
                        help='run browser period-check plan and record creation/deletion')
    parser.add_argument('--equipment-completion-only', action='store_true',
                        help='run browser maintenance and period-check record completion')
    parser.add_argument('--equipment-period-completion-only', action='store_true',
                        help='run browser period-check record completion')
    parser.add_argument('--equipment-maintain-repair-read-only', action='store_true',
                        help='stop after reading the scoped maintenance/repair history')
    parser.add_argument('--equipment-maintain-repair-lifecycle-only', action='store_true',
                        help='run browser maintenance/repair creation and deletion')
    args = parser.parse_args()
    report = {'schema': 1, 'status': 'executed-fail', 'production_access': False,
              'scope': 'packaged equipment and consumables browser reads plus isolated equipment creation and consumable XLS import/export; synthetic Oracle only',
              'started_at': datetime.datetime.now(datetime.timezone.utc).isoformat(),
              'checks': [], 'phase_timings_ms': {}}
    if args.distribution:
        report.update(runner='owned-web/src/test/browser/run-inventory-browser.py',
                      role='manager', organization='org-a', mode='isolated',
                      fixture='consumable-material-distribution-fixture')
    name = 'lims-owned-web-' + uuid.uuid4().hex
    network = name + '-net'
    created = network_created = redis_created = False
    redis_name = name + '-redis'
    redis_endpoint = None
    process = browser = None

    def check(label, condition):
        if not condition:
            raise RuntimeError(label)
        report['checks'].append(label)

    def sql(source):
        text = ('whenever sqlerror exit failure rollback\n'
                'set pagesize 0 linesize 32767 trimspool on feedback off heading off echo off verify off\n'
                'alter session set container=FREEPDB1;\n' + source + '\nexit\n')
        return run(['docker', 'exec', '-i', name, 'sqlplus', '-s', '/ as sysdba'],
                   input=text, timeout=60)

    try:
        sources = sorted(path for folder in ('owned-framework/src', 'owned-personnel/src', 'owned-equipment/src', 'owned-consumables/src', 'owned-web/src')
                         for path in (ROOT / folder).rglob('*')
                         if path.is_file() and '__pycache__' not in path.parts)
        sources += [WEB / 'build-owned-web.sh', WEB / 'original-personnel-sha256.json',
                    ROOT / 'scripts/build-owned-personnel.sh', ROOT / 'scripts/build-owned-framework.sh',
                    ROOT / 'scripts/prepare-owned-public-deps.py', ROOT / 'scripts/reject-supplier-runtime.sh', WEB / 'original-inventory-sha256.json', ROOT / 'scripts/build-owned-equipment.sh', ROOT / 'scripts/build-owned-consumables.sh']
        if args.distribution:
            sources += [ROOT / 'owned-distribution/start.sh', ROOT / 'owned-distribution/package.py',
                        ROOT / 'scripts/build-owned-distribution.sh']
        report['source_commit'] = passed(run(['git', 'rev-parse', 'HEAD'], cwd=ROOT), 'git').strip()
        report['source_dirty'] = bool(passed(run(['git', 'status', '--porcelain', '--'] + sources,
                                                 cwd=ROOT), 'source status').strip())
        report['source_sha256'] = {str(path.relative_to(ROOT)): digest(path) for path in sources}
        if args.distribution:
            listing = passed(run(['git', 'ls-files', '-z', '--', *DISTRIBUTION_SOURCE_SCOPES], cwd=ROOT), 'distribution source inventory')
            sources = [ROOT / item for item in listing.split('\0') if item]
            report['source_sha256'] = {str(path.relative_to(ROOT)): digest(path) for path in sources}
            report['source_coverage'] = {'roots': list(DISTRIBUTION_SOURCE_SCOPES),
                                         'files': dict(report['source_sha256']),
                                         'diff': {'added': [], 'deleted': []}}
            report['verifier_sha256'] = {path: digest(ROOT / path) for path in VERIFIER_PATHS}
        build_started = time.perf_counter()
        if not args.distribution:
            passed(run([ROOT / 'scripts/build-owned-personnel.sh'], timeout=240), 'personnel build')
            passed(run([ROOT / 'scripts/build-owned-equipment.sh'], timeout=240), 'equipment build')
            passed(run([ROOT / 'scripts/build-owned-consumables.sh'], timeout=240), 'consumables build')
            passed(run([WEB / 'build-owned-web.sh']), 'web build')
        if args.distribution:
            passed(run([ROOT / 'scripts/build-owned-distribution.sh'], timeout=1800,
                       env=dict(os.environ, OWNED_DISTRIBUTION_REUSE='1')), 'distribution build')
            check('standalone distribution exists', (DIST / 'start.sh').is_file() and
                  (DIST / 'app/lims-owned-application.jar').is_file() and (DIST / 'manifest.json').is_file())
        report['phase_timings_ms']['build'] = round((time.perf_counter() - build_started) * 1000, 3)
        originals = {}
        for manifest in ('original-personnel-sha256.json', 'original-inventory-sha256.json'):
            originals.update(json.loads((WEB / manifest).read_text())['files'])
        for relative, expected in originals.items():
            check('unchanged business resource: ' + relative,
                  digest(WEB / 'src/main/resources' / relative) == expected)
        jars = [ROOT / 'owned-framework/target/lims-owned-framework-1.0.0.jar',
                ROOT / 'owned-personnel/target/lims-owned-personnel-1.0.0.jar',
                WEB / 'target/lims-owned-web-1.0.0.jar',
                ROOT / 'owned-equipment/target/lims-owned-equipment-1.0.0.jar',
                ROOT / 'owned-consumables/target/lims-owned-consumables-1.0.0.jar']
        dependencies = ROOT / 'owned-framework/target/public-deps'
        report['artifact_sha256'] = {str(path.relative_to(ROOT)): digest(path) for path in jars}
        report['public_dependency_sha256'] = {path.name: digest(path) for path in sorted(dependencies.glob('*.jar'))}
        report['recovered_source_sha256'] = dict(line.split('  ', 1)[::-1] for line in
            (ROOT / 'owned-personnel/target/original-sources.sha256').read_text().splitlines())
        image = json.loads(passed(run(['docker', 'image', 'inspect', IMAGE]), 'pinned image'))[0]
        check('pinned Linux amd64 database', image['Os'] == 'linux' and image['Architecture'] == 'amd64')
        report['image_reference'], report['image_id'] = IMAGE, image['Id']
        jdk = Path(os.environ.get('JAVA_HOME', str(ROOT / 'build/jdk1.8.0_391')))
        with tempfile.TemporaryDirectory(prefix='lims-owned-web-private-') as temporary:
            temporary = Path(temporary)
            env = temporary / 'oracle.env'
            # Keep disposable Oracle credentials within the legacy password
            # length accepted by the pinned image/configuration.
            env.write_text('ORACLE_PASSWORD=Ephemeral' + uuid.uuid4().hex[:16] + '\n')
            env.chmod(0o600)
            passed(run(['docker', 'network', 'create', '--internal', '--driver', 'bridge', network]), 'network')
            network_created = True
            container_id = passed(run(['docker', 'create', '--name', name, '--network', network,
                '--restart', 'no', '--memory', '4g', '--cpus', '2', '--env-file', env, IMAGE]), 'container').strip()
            created = True
            passed(run(['docker', 'start', name]), 'container start')
            def oracle_ready():
                ready = sql("SELECT 'BROWSER_READY' FROM V$PDBS WHERE NAME='FREEPDB1' AND OPEN_MODE='READ WRITE';")
                logs = run(['docker', 'logs', name])
                return (ready.returncode == 0 and 'BROWSER_READY' in ready.stdout and
                        'DATABASE IS READY TO USE!' in logs.stdout + logs.stderr)

            oracle_timing = wait_until('Oracle readiness', oracle_ready, timeout=600, interval=2)
            report['phase_timings_ms']['oracle_readiness'] = oracle_timing['elapsed_ms']
            container = json.loads(passed(run(['docker', 'inspect', name]), 'container inspect'))[0]
            net = json.loads(passed(run(['docker', 'network', 'inspect', network]), 'network inspect'))[0]
            isolation = {'network_internal': net['Internal'],
                         'attached_network_count': len(container['NetworkSettings']['Networks']),
                         'sole_endpoint': set(net['Containers']) == {container_id},
                         'port_bindings': container['HostConfig']['PortBindings'],
                         'bind_mount_count': sum(m['Type'] == 'bind' for m in container['Mounts'])}
            report['isolation'] = isolation
            check('database has one internal network and no ports or binds',
                  isolation['network_internal'] and isolation['sole_endpoint']
                  and isolation['attached_network_count'] == 1 and not isolation['port_bindings']
                  and isolation['bind_mount_count'] == 0)
            if args.distribution:
                redis_image = json.loads(passed(run(['docker', 'image', 'inspect', REDIS_IMAGE]), 'Redis image inspect'))[0]
                check('pinned Linux amd64 Redis image', redis_image['Os'] == 'linux' and redis_image['Architecture'] == 'amd64')
                passed(run(['docker', 'create', '--name', redis_name, '--network', network,
                            '--network-alias', 'redis', '--restart', 'no', '--memory', '256m', '--cpus', '1',
                            REDIS_IMAGE, 'redis-server', '--save', '', '--appendonly', 'no']), 'Redis container create')
                redis_created = True
                passed(run(['docker', 'start', redis_name]), 'Redis container start')
                def redis_ready():
                    redis_ping = run(['docker', 'exec', redis_name, 'redis-cli', 'ping'])
                    return redis_ping.returncode == 0 and redis_ping.stdout.strip() == 'PONG'

                redis_timing = wait_until('Redis readiness', redis_ready, timeout=30, interval=.5)
                report['phase_timings_ms']['redis_readiness'] = redis_timing['elapsed_ms']
                redis_container = json.loads(passed(run(['docker', 'inspect', redis_name]), 'Redis container inspect'))[0]
                joint_net = json.loads(passed(run(['docker', 'network', 'inspect', network]), 'joint network inspect'))[0]
                redis_endpoint = joint_net['Containers'][redis_container['Id']]['IPv4Address'].split('/', 1)[0]
                redis_isolated = joint_net['Internal'] is True and len(redis_container['NetworkSettings']['Networks']) == 1 and not redis_container['HostConfig']['PortBindings'] and sum(m['Type'] == 'bind' for m in redis_container.get('Mounts', [])) == 0
                check('Redis has one internal network without ports or binds', redis_isolated)
                report['redis'] = {'image_reference': REDIS_IMAGE, 'image_id': redis_image['Id'], 'endpoint': redis_endpoint,
                                   'port': 6379, 'network_internal': joint_net['Internal'],
                                   'attached_network_count': len(redis_container['NetworkSettings']['Networks']),
                                   'port_bindings': redis_container['HostConfig']['PortBindings'],
                                   'bind_mount_count': sum(m['Type'] == 'bind' for m in redis_container.get('Mounts', []))}
            password = 'Fixture' + uuid.uuid4().hex[:16]
            passed(sql('CREATE USER query_fixture IDENTIFIED BY ' + password + ';\n'
                       'GRANT CREATE SESSION, CREATE TABLE, UNLIMITED TABLESPACE TO query_fixture;'), 'schema')
            properties = temporary / 'jdbc.properties'
            properties.write_text('url=jdbc:oracle:thin:@//' +
                container['NetworkSettings']['Networks'][network]['IPAddress'] +
                ':1521/FREEPDB1\nuser=query_fixture\npassword=' + password + '\n')
            properties.chmod(0o600)
            fixture_timing = wait_until(
                'Oracle fixture schema readiness',
                lambda: sql("SELECT 'FIXTURE_READY' FROM DUAL;").returncode == 0,
                timeout=30, interval=.5)
            report['phase_timings_ms']['oracle_fixture_readiness'] = fixture_timing['elapsed_ms']
            seed_classpath = ':'.join(map(str, [ROOT / 'owned-framework/target/classes',
                ROOT / 'owned-framework/target/test-classes', dependencies / '*']))
            seed = passed(run([jdk / 'bin/java', '-cp', seed_classpath,
                'com.sunwayworld.framework.PersonnelQueryServiceContractTest', properties]), 'service baseline')
            check('15 synthetic service checks', 'PASS (15 checks;' in seed)
            passed(sql((WEB / 'src/test/browser/inventory-fixture.sql').read_text()), 'inventory fixture')
            if args.distribution:
                auth_fixture = passed(sql("""
                    ALTER SESSION SET CURRENT_SCHEMA=query_fixture;
                    SELECT 'AUTH|'||U.ID||'|'||U.ORGID||'|'||P.TARGETID||'|'||R.ROLEID||'|'||RP.TARGETID
                    FROM T_CORE_USER U
                    LEFT JOIN T_CORE_USER_PERMISSION P ON P.USERID=U.ID AND P.PERMISSIONTYPEID='T_CORE_ORG'
                    LEFT JOIN T_CORE_ROLE_USER R ON R.USERID=U.ID
                    LEFT JOIN T_CORE_ROLE_PERMISSION RP ON RP.ROLEID=R.ROLEID AND RP.PERMISSIONTYPEID='T_CORE_MENU' AND RP.TARGETID='430201'
                    WHERE U.ID='e-1';
                """), 'distribution consumable authorization fixture')
                check('distribution consumable authorization fixture is complete', 'AUTH|e-1|org-a|org-a|1|430201' in auth_fixture)
            # The distribution runner has no test-only Java bootstrap to add
            # pagination rows and import fixtures. Add only the synthetic rows
            # needed by the existing browser contract directly to Oracle.
            if args.distribution:
                passed(sql("""
                    ALTER SESSION SET CURRENT_SCHEMA=query_fixture;
                    INSERT INTO T_EMC_EMP (ID,USERNAME,CREATEDTIME,ORGID,AGE,REMARK)
                    SELECT 'page-'||LPAD(LEVEL,3,'0'),'Fixture '||LPAD(LEVEL,3,'0'),TIMESTAMP '2025-01-01 00:00:00','org-a',25,'synthetic pagination fixture'
                    FROM DUAL CONNECT BY LEVEL<=21;
                    INSERT INTO T_EMC_EQUIPT (ID,EQUIPTID,EQUIPTNAME,EQUIPTCATEGORYID,USEDDEPARTMENTID,ORGID)
                    SELECT 100+LEVEL,'EQ-PAGE-'||LPAD(LEVEL,3,'0'),'Synthetic equipment page '||LEVEL,'11','org-a','org-a'
                    FROM DUAL CONNECT BY LEVEL<=21;
                    INSERT INTO T_EMC_CONSUME (ID,MTLNAME,SPECIFICATION,STOCKQTY,CREATEDBYORGID)
                    SELECT 'CSM-PAGE-'||LPAD(LEVEL,3,'0'),'Synthetic material page '||LEVEL,'fixture',0,'org-a'
                    FROM DUAL CONNECT BY LEVEL<=21;
                    COMMIT;
                """), 'distribution pagination fixture')
            classes = temporary / 'classes'
            classes.mkdir()
            runtime = ':'.join(map(str, jars + [dependencies / '*']))
            passed(run([jdk / 'bin/javac', '-source', '8', '-target', '8', '-cp', runtime,
                '-d', classes, WEB / 'src/test/java/OwnedInventoryBrowserApplication.java']), 'lifecycle compile')
            portfile, stop, audit, import_file, invalid_file, downloaded_template = [temporary / part for part in ('port', 'stop', 'audit.jsonl', 'import.xls', 'invalid.xls', 'downloaded-template.xls')]
            with (temporary / 'application.log').open('w') as log:
                if args.distribution:
                    with socket.socket() as listener:
                        listener.bind(('127.0.0.1', 0))
                        app_port = listener.getsockname()[1]
                    config = temporary / 'owned-application.properties'
                    config.write_text('\n'.join([
                        'server.address=127.0.0.1',
                        'server.port=' + str(app_port),
                        'spring.main.banner-mode=off',
                        'spring.main.log-startup-info=false',
                        'server.tomcat.threads.max=2',
                        'server.tomcat.threads.min-spare=1',
                        'spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration',
                        'spring.datasource.type=org.springframework.jdbc.datasource.DriverManagerDataSource',
                        'spring.datasource.driver-class-name=oracle.jdbc.OracleDriver',
                        'spring.datasource.url=' + properties.read_text().splitlines()[0].split('=', 1)[1],
                        'spring.datasource.username=query_fixture',
                        'spring.datasource.password=' + password,
                        'spring.redis.url=redis://' + str(redis_endpoint) + ':6379',
                        'spring.redis.timeout=5000',
                        'sunway.user.password.encoder.type=MD5',
                        'owned.identity.audit.type=file',
                        'owned.identity.audit.path=' + str(audit),
                        'owned.consumables.import-export.mode=isolated',
                        'owned.consumables.write.mode=isolated',
                        'owned.equipment.write.mode=isolated',
                        'owned.personnel.write.mode=isolated',
                        'owned.attachments.enabled=false',
                        'owned.erp.mode=disabled',
                        'owned.report.replay.mode=disabled',
                        'owned.scheduler.mode=disabled',
                    ]) + '\n')
                    config.chmod(0o600)
                    process = subprocess.Popen([str(DIST / 'start.sh'), str(config)],
                        stdout=log, stderr=subprocess.STDOUT, cwd=temporary,
                        env=dict(os.environ, JAVA_HOME=str(jdk)))
                else:
                    process = subprocess.Popen([str(jdk / 'bin/java'), '-cp', str(classes) + ':' + runtime,
                        'OwnedInventoryBrowserApplication', str(properties), str(portfile), str(stop), str(audit), str(import_file), str(invalid_file)],
                        stdout=log, stderr=subprocess.STDOUT, cwd=temporary)
                port = app_port if args.distribution else None
                def application_ready():
                    nonlocal port
                    if portfile.exists():
                        port = int(portfile.read_text())
                        return process.poll() is None
                    if args.distribution:
                        try:
                            with socket.create_connection(('127.0.0.1', port), timeout=.05):
                                return process.poll() is None
                        except OSError:
                            return False
                    return False

                app_timing = wait_until('Application loopback readiness', application_ready,
                                        timeout=120, interval=.2)
                report['phase_timings_ms']['application_readiness'] = app_timing['elapsed_ms']
                if not (port and process.poll() is None):
                    report['application_diagnostic'] = (temporary / 'application.log').read_text()[-8000:].replace(password, '[REDACTED]')
                    report['application_process_returncode'] = process.poll()
                    try:
                        report['application_ps'] = run(['ps', '-o', 'pid,ppid,stat,etime,args', '-p', str(process.pid)]).stdout
                        jstack = run([str(jdk / 'bin/jstack'), str(process.pid)], timeout=20)
                        report['application_jstack_tail'] = (jstack.stdout + jstack.stderr)[-12000:]
                    except Exception as failure:
                        report['application_thread_diagnostic_error'] = type(failure).__name__ + ': ' + str(failure)
                check('packaged original application ready', (port if args.distribution else portfile.exists()) and process.poll() is None)
                url = 'http://127.0.0.1:' + str(port) + '/'
                browser = 'owned-web-' + uuid.uuid4().hex
                transcript = []
                browser_started = time.perf_counter()

                def browser_diagnostics():
                    """Capture enough state to explain a browser failure before the temp dir is removed."""
                    details = {}
                    for label, command in (
                            ('url', ['get', 'url']),
                            ('snapshot', ['snapshot']),
                            ('errors', ['errors'])):
                        try:
                            result = run(['agent-browser', '--session', browser, *command], timeout=20)
                            details[label] = result.stdout[-12000:]
                            if result.stderr:
                                details[label + '_stderr'] = result.stderr[-4000:]
                        except Exception as failure:
                            details[label + '_error'] = type(failure).__name__ + ': ' + str(failure)
                    try:
                        result = run(['agent-browser', '--session', browser, 'eval', 'JSON.stringify(window.__ownedConsumableCases || [])'], timeout=20)
                        details['owned_consumable_http_cases'] = result.stdout[-6000:]
                    except Exception as failure:
                        details['owned_consumable_http_cases_error'] = type(failure).__name__ + ': ' + str(failure)
                    log = temporary / 'application.log'
                    if log.exists():
                        details['application_log'] = (log.read_text(errors='replace')[-12000:]
                                                       .replace(password, '[REDACTED]')
                                                       .replace('secret', '[REDACTED]'))
                    audit_file = temporary / 'audit.jsonl'
                    if audit_file.exists():
                        details['audit_tail'] = audit_file.read_text(errors='replace')[-6000:]
                    report['browser_failure_diagnostic'] = details

                def ab(*command):
                    try:
                        result = run(['agent-browser', '--session', browser, *command], timeout=40)
                    except Exception as failure:
                        browser_diagnostics()
                        raise RuntimeError('browser ' + command[0] + ' failed: ' + type(failure).__name__ + ': ' + str(failure))
                    # Record response-only evidence; fill arguments, cookies and keys are omitted.
                    transcript.append(command[0] + '\n' + result.stdout)
                    if result.returncode:
                        browser_diagnostics()
                        detail = (result.stdout + result.stderr).strip().replace('\n', ' ')
                        raise RuntimeError('browser ' + command[0] + ' failed: ' + detail[-1200:])
                    return result.stdout

                ab('open', url)
                ab('wait', '#login-submit')
                report['phase_timings_ms']['browser_readiness'] = round((time.perf_counter() - browser_started) * 1000, 3)
                check('login form rendered', '用户名' in ab('snapshot', '-i'))
                ab('fill', '#login-id', 'e-1')
                ab('fill', '#login-password', 'secret')
                ab('click', '#login-submit')
                ab('wait', '--text', '共23条')
                check('RSA session login and scoped list', True)
                ab('click', '#equipment-link')
                ab('wait', '#emc-equipt-edit-list-grid')
                ab('wait', '--text', '共23条')
                check('equipment original list and scoped labels', 'EQ-PAGE-021' in ab('get', 'text', '#emc-equipt-edit-list-grid') and 'EQ-002' not in ab('get', 'text', '#owned-workspace'))
                ab('fill', '[aria-label="设备名称筛选"]', 'Alpha')
                ab('click', '#emc-equipt-edit-list-grid .filter-controls button')
                ab('wait', '--text', '共2条')
                check('equipment name filter returns two scoped rows', 'EQ-001' in ab('get', 'text', '#emc-equipt-edit-list-grid'))
                ab('find', 'text', 'EQ-001', 'click', '--exact')
                ab('wait', '--fn', 'document.querySelector(\'output[data-field="equiptId"]\')?.textContent === "EQ-001"')
                detail = ab('get', 'text', '#emc-equipt-edit-list-detail-form')
                check('equipment original selection callback reads detail', all(value in detail for value in ('Alpha Scope', 'M-1', 'F-1', 'Maker', '2025-01-01', 'Fixture Org A')))
                if args.equipment_query_projection_only:
                    check('equipment list exposes complete visible projection', all(value in ab('get', 'text', '#emc-equipt-edit-list-grid') for value in ('EQ-001', 'Alpha Scope', 'M-1', 'inUse', '11', 'Fixture Org A')))
                    check('equipment detail exposes complete visible projection', all(value in detail for value in ('EQ-001', 'Alpha Scope', 'M-1', 'Maker', 'F-1', 'Fixture Org A', 'inUse', '11', 'owned', 'normal', 'purchase', '2025-01-01')))
                    ab('fill', '[aria-label="设备名称筛选"]', 'absent')
                    ab('click', '#emc-equipt-edit-list-grid .filter-controls button')
                    ab('wait', '--text', '共0条')
                    check('equipment projection no-match clears detail', 'Alpha Scope' not in ab('get', 'text', '#owned-workspace'))
                    ab('fill', '[aria-label="设备名称筛选"]', '')
                    ab('fill', '[aria-label="设备编号筛选"]', 'EQ-001')
                    ab('click', '#emc-equipt-edit-list-grid .filter-controls button')
                    ab('wait', '--text', '共1条')
                    check('equipment projection identifier filter returns one row', 'EQ-001' in ab('get', 'text', '#emc-equipt-edit-list-grid'))
                    ab('click', '#emc-equipt-edit-list-grid .filter-controls button:last-child')
                    ab('wait', '--text', '共23条')
                    ab('find', 'role', 'button', 'click', '--name', '下一页', '--exact')
                    ab('wait', '--text', '第 2 / 2 页')
                    projection_page = ab('get', 'text', '#emc-equipt-edit-list-grid')
                    check('equipment projection pagination preserves scoped rows', all(value in projection_page for value in ('EQ-PAGE-001', 'EQ-003', 'EQ-001')) and 'EQ-PAGE-002' not in projection_page)
                    report['scope'] = 'packaged equipment browser full visible projection, filters and pagination; synthetic Oracle only'
                    report['status'] = 'executed-pass'
                    raise StockOnlyComplete()
                ab('find', 'role', 'tab', 'click', '--name', '检定/校准计划', '--exact')
                ab('wait', '#emc-equipt-plan-edit-list--grid')
                ab('wait', '--text', 'equiptCalibration')
                plan = ab('get', 'text', '#emc-equipt-plan-edit-list--grid')
                check('equipment maintenance plan read shows scoped projection', all(value in plan for value in ('equiptCalibration', '2026-01-01', '2026-12-31', 'browser fixture')))
                if args.equipment_plan_read_only:
                    report['scope'] = 'packaged equipment maintenance-plan browser read; synthetic Oracle only'
                    report['status'] = 'executed-pass'
                    raise StockOnlyComplete()
                if args.equipment_plan_lifecycle_only:
                    check('equipment maintenance plan controls visible', '新增' in ab('get', 'text', '#emc-equipt-plan-edit-list--grid') and '删除' in ab('get', 'text', '#emc-equipt-plan-edit-list--grid'))
                    ab('find', 'role', 'button', 'click', '--name', '新增', '--exact')
                    ab('wait', '--text', '新增维护计划')
                    ab('wait', '#equipment-plan-editor')
                    ab('wait', '--fn', "document.querySelector('[aria-label=\"维护事件\"]')?.disabled === false")
                    check('equipment maintenance plan add modal opened', '维护事件' in ab('snapshot', '-i'))
                    ab('select', '[aria-label="维护事件"]', '51')
                    ab('select', '[aria-label="校检周期"]', '1')
                    ab('fill', '[aria-label="校检日期"]', '2027-01-01')
                    ab('fill', '[aria-label="提前提醒（天）"]', '14')
                    ab('fill', '[aria-label="备注"]', 'browser plan lifecycle')
                    ab('find', 'role', 'button', 'click', '--name', '保存维护计划', '--exact')
                    ab('wait', '--text', '维护计划已新增')
                    inserted_plan = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'UI_PLAN_INSERT|'||ID||'|'||EQUIPTID||'|'||MAINTID||'|'||ORGID||'|'||CREATEDBYID||'|'||REMARK FROM T_EMC_EQUIPT_PLAN WHERE REMARK='browser plan lifecycle';"), 'equipment plan UI insert verification')
                    check('equipment plan insert persists sequence, scope and actor', re.search(r'UI_PLAN_INSERT\|[1-9][0-9][0-9][0-9]\|1\|51\|org-a\|e-1\|browser plan lifecycle', inserted_plan) is not None)
                    ab('wait', '--text', 'browser plan lifecycle')
                    check('equipment browser plan create returns visible row', 'browser plan lifecycle' in ab('get', 'text', '#emc-equipt-plan-edit-list--grid'))
                    ab('find', 'text', 'browser plan lifecycle', 'click', '--exact')
                    ab('find', 'role', 'button', 'click', '--name', '删除', '--exact')
                    ab('wait', '#equipment-plan-delete')
                    ab('find', 'role', 'button', 'click', '--name', '确认删除维护计划', '--exact')
                    ab('wait', '--text', '维护计划已删除')
                    deleted_plan = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'UI_PLAN_ROWS|'||COUNT(*) FROM T_EMC_EQUIPT_PLAN WHERE REMARK='browser plan lifecycle';\nSELECT 'UI_PLAN_BASELINE|'||COUNT(*) FROM T_EMC_EQUIPT_PLAN WHERE ID=201;"), 'equipment plan UI delete verification')
                    check('equipment plan delete removes temporary row and keeps baseline', 'UI_PLAN_ROWS|0' in deleted_plan and 'UI_PLAN_BASELINE|1' in deleted_plan)
                    lifecycle_audit = audit.read_text(errors='replace')
                    check('equipment plan lifecycle emits insert and delete audit', 'equipment-plan-insert' in lifecycle_audit and 'equipment-plan-delete' in lifecycle_audit and 'secret' not in lifecycle_audit.lower())
                    report['scope'] = 'packaged equipment maintenance-plan browser lifecycle; synthetic Oracle only'
                    report['status'] = 'executed-pass'
                    raise StockOnlyComplete()
                if args.equipment_record_read_only:
                    ab('find', 'role', 'tab', 'click', '--name', '检定/校准记录', '--exact')
                    ab('wait', '#emc-equipt-record-edit-list-grid')
                    ab('wait', '--text', 'equiptCalibration')
                    record = ab('get', 'text', '#emc-equipt-record-edit-list-grid')
                    check('equipment maintenance record read shows scoped projection', all(value in record for value in ('equiptCalibration', 'Fixture Lab', '2026-01-10', 'record browser fixture')))
                    report['scope'] = 'packaged equipment maintenance-record browser read; synthetic Oracle only'
                    report['status'] = 'executed-pass'
                    raise StockOnlyComplete()
                if args.equipment_record_lifecycle_only:
                    ab('find', 'role', 'tab', 'click', '--name', '检定/校准记录', '--exact')
                    ab('wait', '#emc-equipt-record-edit-list-grid')
                    ab('wait', '--text', 'equiptCalibration')
                    ab('find', 'role', 'button', 'click', '--name', '新增', '--exact')
                    ab('wait', '#equipment-record-editor')
                    ab('wait', '--fn', "document.querySelector('[aria-label=\"维护事件\"]')?.disabled === false")
                    ab('select', '[aria-label="维护事件"]', '51')
                    ab('fill', '[aria-label="校检单位"]', 'Browser Lab')
                    ab('find', 'role', 'button', 'click', '--name', '保存维护记录', '--exact')
                    ab('wait', '--text', '维护记录已新增')
                    inserted_record = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'UI_RECORD_INSERT|'||ID||'|'||EQUIPTID||'|'||MAINTID||'|'||VERIFYORG||'|'||CREATEDBYID||'|'||COMPLETEFLAG FROM T_EMC_EQUIPT_RECORD WHERE VERIFYORG='Browser Lab';"), 'equipment record UI insert verification')
                    check('equipment record insert persists sequence, scope, actor and incomplete state', re.search(r'UI_RECORD_INSERT\|[1-9][0-9][0-9][0-9]\|1\|51\|Browser Lab\|e-1\|1', inserted_record) is not None)
                    ab('wait', '--text', 'Browser Lab')
                    check('equipment browser record create returns visible row', 'Browser Lab' in ab('get', 'text', '#emc-equipt-record-edit-list-grid'))
                    ab('find', 'text', 'Browser Lab', 'click', '--exact')
                    ab('find', 'role', 'button', 'click', '--name', '删除', '--exact')
                    ab('wait', '#equipment-record-delete')
                    ab('find', 'role', 'button', 'click', '--name', '确认删除维护记录', '--exact')
                    ab('wait', '--text', '维护记录已删除')
                    deleted_record = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'UI_RECORD_ROWS|'||COUNT(*) FROM T_EMC_EQUIPT_RECORD WHERE VERIFYORG='Browser Lab';\nSELECT 'UI_RECORD_BASELINE|'||COUNT(*) FROM T_EMC_EQUIPT_RECORD WHERE ID=301;"), 'equipment record UI delete verification')
                    check('equipment record delete removes temporary row and keeps baseline', 'UI_RECORD_ROWS|0' in deleted_record and 'UI_RECORD_BASELINE|1' in deleted_record)
                    lifecycle_audit = audit.read_text(errors='replace')
                    check('equipment record lifecycle emits insert and delete audit', 'equipment-record-insert' in lifecycle_audit and 'equipment-record-delete' in lifecycle_audit and 'secret' not in lifecycle_audit.lower())
                    report['scope'] = 'packaged equipment maintenance-record browser create/delete; synthetic Oracle only'
                    report['status'] = 'executed-pass'
                    raise StockOnlyComplete()
                if args.equipment_completion_only:
                    ab('find', 'role', 'tab', 'click', '--name', '检定/校准记录', '--exact')
                    ab('wait', '#emc-equipt-record-edit-list-grid')
                    ab('wait', '--text', 'Fixture Lab')
                    ab('find', 'role', 'button', 'click', '--name', '新增', '--exact')
                    ab('wait', '#equipment-record-editor')
                    ab('wait', '--fn', "document.querySelector('[aria-label=\"维护事件\"]')?.disabled === false")
                    ab('select', '[aria-label="维护事件"]', '51')
                    ab('fill', '[aria-label="校检单位"]', 'Completion Lab')
                    ab('find', 'role', 'button', 'click', '--name', '保存维护记录', '--exact')
                    ab('wait', '--text', '维护记录已新增')
                    ab('wait', '--text', 'Completion Lab')
                    ab('find', 'text', 'Completion Lab', 'click', '--exact')
                    ab('find', 'role', 'button', 'click', '--name', '完成', '--exact')
                    ab('wait', '#equipment-record-complete')
                    ab('fill', '[aria-label="校检周期"]', '1')
                    ab('fill', '[aria-label="校检日期"]', '2027-03-01')
                    ab('fill', '[aria-label="有效期至"]', '2028-02-29')
                    ab('fill', '[aria-label="校检单位"]', 'Completion Lab Updated')
                    ab('fill', '[aria-label="证书类型"]', 'calibration')
                    ab('fill', '[aria-label="备注"]', 'browser record completion')
                    ab('find', 'role', 'button', 'click', '--name', '确认完成维护记录', '--exact')
                    ab('wait', '--text', '维护记录已完成')
                    completed_record = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'UI_RECORD_COMPLETE|'||ID||'|'||COMPLETEFLAG FROM T_EMC_EQUIPT_RECORD WHERE VERIFYORG='Completion Lab';\nSELECT 'UI_RECORD_PLAN|'||TO_CHAR(MAINTDATE,'YYYY-MM-DD')||'|'||TO_CHAR(MAINTENDDATE,'YYYY-MM-DD')||'|'||VERIFYORG||'|'||REMARK FROM T_EMC_EQUIPT_PLAN WHERE ID=201;\nSELECT 'UI_RECORD_EQUIP|'||TO_CHAR(SOURCEDATE,'YYYY-MM-DD') FROM T_EMC_EQUIPT WHERE ID=1;"), 'equipment record completion verification')
                    check('maintenance record completion updates record, plan and equipment: ' + completed_record.replace('\n', '|'), re.search(r'UI_RECORD_COMPLETE\|[0-9]+\|2', completed_record) is not None and 'UI_RECORD_PLAN|2027-03-01|2028-02-29|Completion Lab Updated|browser record completion' in completed_record and 'UI_RECORD_EQUIP|2028-02-29' in completed_record)
                    if args.equipment_completion_only:
                        restored_record = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nDELETE FROM T_EMC_EQUIPT_RECORD WHERE VERIFYORG='Completion Lab';\nUPDATE T_EMC_EQUIPT_PLAN SET MAINTDATE=TIMESTAMP '2026-01-01 00:00:00',MAINTENDDATE=TIMESTAMP '2026-12-31 00:00:00',VERIFYORG=NULL,REMARK='browser fixture' WHERE ID=201;\nUPDATE T_EMC_EQUIPT SET SOURCEDATE=DATE '2025-01-01' WHERE ID=1;\nCOMMIT;\nSELECT 'UI_RECORD_COMPLETION_RESTORE|'||(SELECT COUNT(*) FROM T_EMC_EQUIPT_RECORD WHERE VERIFYORG LIKE 'Completion Lab%')||'|'||(SELECT REMARK FROM T_EMC_EQUIPT_PLAN WHERE ID=201)||'|'||(SELECT TO_CHAR(SOURCEDATE,'YYYY-MM-DD') FROM T_EMC_EQUIPT WHERE ID=1);"), 'maintenance completion fixture restoration')
                        check('maintenance completion fixture restoration returns baseline', 'UI_RECORD_COMPLETION_RESTORE|0|browser fixture|2025-01-01' in restored_record)
                        lifecycle_audit = audit.read_text(errors='replace')
                        check('maintenance completion emits audit', 'equipment-record-complete' in lifecycle_audit and 'secret' not in lifecycle_audit.lower())
                        report['scope'] = 'packaged equipment maintenance-record browser completion; synthetic Oracle only'
                        report['status'] = 'executed-pass'
                        raise StockOnlyComplete()
                if args.equipment_period_completion_only:
                    ab('find', 'role', 'tab', 'click', '--name', '期间核查记录', '--exact')
                    ab('wait', '#emc-equipt-per-check-record-edit-list-grid')
                    ab('wait', '--text', 'period record browser fixture')
                    ab('click', '#emc-equipt-per-check-record-edit-list-grid tbody tr')
                    ab('find', 'role', 'button', 'click', '--name', '完成', '--exact')
                    ab('wait', '#equipment-period-record-complete')
                    ab('fill', '[aria-label="核查周期"]', '2')
                    ab('fill', '[aria-label="核查时间"]', '2027-03-01')
                    ab('fill', '[aria-label="核查依据"]', 'Browser Period Complete')
                    ab('fill', '[aria-label="核查所用仪器设备"]', 'Reference Complete')
                    ab('fill', '[aria-label="备注"]', 'browser period completion')
                    ab('find', 'role', 'button', 'click', '--name', '确认完成期间核查记录', '--exact')
                    ab('wait', '--text', '期间核查记录已完成')
                    completed_period = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'UI_PERIOD_COMPLETE|'||COMPLETEFLAG||'|'||TO_CHAR(MAINTDATE,'YYYY-MM-DD')||'|'||CYCLE||'|'||CHECKREASON||'|'||REMARK FROM T_EMC_EQUIPT_PER_CHECK_RECORD WHERE ID=501;\nSELECT 'UI_PERIOD_PLAN|'||TO_CHAR(MAINTDATE,'YYYY-MM-DD')||'|'||CYCLE||'|'||CHECKREASON||'|'||REMARK FROM T_EMC_EQUIPT_PER_CHECK_PLAN WHERE ID=401;"), 'period record completion verification')
                    check('period record completion updates record and linked plan: ' + completed_period.replace('\n', '|'), 'UI_PERIOD_COMPLETE|2|2027-03-01|2|Browser Period Complete|browser period completion' in completed_period and 'UI_PERIOD_PLAN|2027-03-01|2|Browser Period Complete|browser period completion' in completed_period)
                    restored_period = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nDELETE FROM T_EMC_EQUIPT_PER_CHECK_RECORD WHERE ID=501;\nINSERT INTO T_EMC_EQUIPT_PER_CHECK_RECORD VALUES (501,1,51,TIMESTAMP '2026-02-01 00:00:00','2','ISO-17025','Reference','1','e-1','Allowed User','period record browser fixture','e-1','Allowed User',TIMESTAMP '2025-01-09 09:00:00','org-a','Fixture Org A');\nUPDATE T_EMC_EQUIPT_PER_CHECK_PLAN SET MAINTDATE=TIMESTAMP '2026-02-01 00:00:00',CYCLE='2',CHECKREASON='ISO-17025',CHECKEQUIPT='Reference',MAINTENDDATE=TIMESTAMP '2027-01-31 00:00:00',REMARK='period plan browser fixture' WHERE ID=401;\nCOMMIT;\nSELECT 'UI_PERIOD_COMPLETION_RESTORE|'||(SELECT COMPLETEFLAG FROM T_EMC_EQUIPT_PER_CHECK_RECORD WHERE ID=501)||'|'||(SELECT REMARK FROM T_EMC_EQUIPT_PER_CHECK_PLAN WHERE ID=401);"), 'period completion fixture restoration')
                    check('period completion fixture restoration returns baseline', 'UI_PERIOD_COMPLETION_RESTORE|1|period plan browser fixture' in restored_period)
                    lifecycle_audit = audit.read_text(errors='replace')
                    check('period completion emits audit', 'equipment-per-check-record-complete' in lifecycle_audit and 'secret' not in lifecycle_audit.lower())
                    report['scope'] = 'packaged equipment period-check record browser completion; synthetic Oracle only'
                    report['status'] = 'executed-pass'
                    raise StockOnlyComplete()
                    ab('find', 'role', 'tab', 'click', '--name', '期间核查记录', '--exact')
                    ab('wait', '#emc-equipt-per-check-record-edit-list-grid')
                    ab('wait', '--text', 'period record browser fixture')
                    ab('find', 'text', 'period record browser fixture', 'click', '--exact')
                    ab('find', 'role', 'button', 'click', '--name', '完成', '--exact')
                    ab('wait', '#equipment-period-record-complete')
                    ab('fill', '[aria-label="核查周期"]', '2')
                    ab('fill', '[aria-label="核查时间"]', '2027-03-01')
                    ab('fill', '[aria-label="核查依据"]', 'Browser Period Complete')
                    ab('fill', '[aria-label="核查所用仪器设备"]', 'Reference Complete')
                    ab('fill', '[aria-label="备注"]', 'browser period completion')
                    ab('find', 'role', 'button', 'click', '--name', '确认完成期间核查记录', '--exact')
                    ab('wait', '--text', '期间核查记录已完成')
                    completed_period = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'UI_PERIOD_COMPLETE|'||COMPLETEFLAG||'|'||MAINTDATE||'|'||CYCLE||'|'||CHECKREASON||'|'||REMARK FROM T_EMC_EQUIPT_PER_CHECK_RECORD WHERE ID=501;\nSELECT 'UI_PERIOD_PLAN|'||MAINTDATE||'|'||CYCLE||'|'||CHECKREASON||'|'||REMARK FROM T_EMC_EQUIPT_PER_CHECK_PLAN WHERE ID=401;"), 'period record completion verification')
                    check('period record completion updates record and linked plan', 'UI_PERIOD_COMPLETE|2|2027-03-01|2|Browser Period Complete|browser period completion' in completed_period and 'UI_PERIOD_PLAN|2027-03-01|2|Browser Period Complete|browser period completion' in completed_period)
                    restored = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nDELETE FROM T_EMC_EQUIPT_RECORD WHERE VERIFYORG='Completion Lab';\nDELETE FROM T_EMC_EQUIPT_RECORD WHERE ID=501;\nINSERT INTO T_EMC_EQUIPT_RECORD VALUES (501,1,51,TIMESTAMP '2026-02-01 00:00:00','2','ISO-17025','Reference','1','e-1','Allowed User','period record browser fixture','e-1','Allowed User',TIMESTAMP '2025-01-09 09:00:00','org-a','Fixture Org A');\nUPDATE T_EMC_EQUIPT_PLAN SET MAINTDATE=TIMESTAMP '2026-01-01 00:00:00',MAINTENDDATE=TIMESTAMP '2026-12-31 00:00:00',VERIFYORG=NULL,REMARK='browser fixture' WHERE ID=201;\nUPDATE T_EMC_EQUIPT_PER_CHECK_PLAN SET MAINTDATE=TIMESTAMP '2026-02-01 00:00:00',CYCLE='2',CHECKREASON='ISO-17025',CHECKEQUIPT='Reference',MAINTENDDATE=TIMESTAMP '2027-01-31 00:00:00',REMARK='period plan browser fixture' WHERE ID=401;\nUPDATE T_EMC_EQUIPT SET SOURCEDATE=DATE '2025-01-01' WHERE ID=1;\nCOMMIT;\nSELECT 'UI_COMPLETION_RESTORE|'||(SELECT COUNT(*) FROM T_EMC_EQUIPT_RECORD WHERE VERIFYORG='Completion Lab')||'|'||(SELECT COMPLETEFLAG FROM T_EMC_EQUIPT_PER_CHECK_RECORD WHERE ID=501)||'|'||(SELECT REMARK FROM T_EMC_EQUIPT_PLAN WHERE ID=201)||'|'||(SELECT REMARK FROM T_EMC_EQUIPT_PER_CHECK_PLAN WHERE ID=401)||'|'||(SELECT SOURCEDATE FROM T_EMC_EQUIPT WHERE ID=1);"), 'completion fixture restoration')
                    check('completion fixture restoration returns baseline', 'UI_COMPLETION_RESTORE|0|1|browser fixture|period plan browser fixture|2025-01-01' in restored)
                    lifecycle_audit = audit.read_text(errors='replace')
                    check('completion emits maintenance and period audit', 'equipment-record-complete' in lifecycle_audit and 'equipment-per-check-record-complete' in lifecycle_audit and 'secret' not in lifecycle_audit.lower())
                    report['scope'] = 'packaged equipment maintenance and period-check record browser completion; synthetic Oracle only'
                    report['status'] = 'executed-pass'
                    raise StockOnlyComplete()
                if args.equipment_period_read_only:
                    ab('find', 'role', 'tab', 'click', '--name', '期间核查计划', '--exact')
                    ab('wait', '#emc-equipt-per-check-plan-edit-list-grid')
                    ab('wait', '--text', 'Calibration')
                    period_plan = ab('get', 'text', '#emc-equipt-per-check-plan-edit-list-grid')
                    check('equipment period-check plan read shows scoped projection', all(value in period_plan for value in ('Calibration', '2026-02-01', '2027-01-31', 'ISO-17025', 'Reference', 'visual')))
                    ab('find', 'role', 'tab', 'click', '--name', '期间核查记录', '--exact')
                    ab('wait', '#emc-equipt-per-check-record-edit-list-grid')
                    ab('wait', '--text', 'Calibration')
                    period_record = ab('get', 'text', '#emc-equipt-per-check-record-edit-list-grid')
                    check('equipment period-check record read shows scoped projection', all(value in period_record for value in ('Calibration', '2026-02-01', 'period record browser fixture', 'Allowed User')))
                    report['scope'] = 'packaged equipment period-check plan and record browser read; synthetic Oracle only'
                    report['status'] = 'executed-pass'
                    raise StockOnlyComplete()
                if args.equipment_maintain_repair_read_only:
                    ab('find', 'role', 'tab', 'click', '--name', '仪器维护维修记录', '--exact')
                    ab('wait', '#emc-equipt-maintain-repair-edit-list-grid')
                    ab('wait', '--text', 'browser maintenance repair fixture')
                    repair = ab('get', 'text', '#emc-equipt-maintain-repair-edit-list-grid')
                    check('equipment maintenance repair read shows scoped projection', all(value in repair for value in ('2026-03-01', 'browser maintenance repair fixture', 'Fixture Repair Unit', 'none')))
                    report['scope'] = 'packaged equipment maintenance-repair browser read; synthetic Oracle only'
                    report['status'] = 'executed-pass'
                    raise StockOnlyComplete()
                if args.equipment_maintain_repair_lifecycle_only:
                    ab('find', 'role', 'tab', 'click', '--name', '仪器维护维修记录', '--exact')
                    ab('wait', '#emc-equipt-maintain-repair-edit-list-grid')
                    ab('wait', '--text', 'browser maintenance repair fixture')
                    ab('find', 'role', 'button', 'click', '--name', '新增', '--exact')
                    ab('wait', '#equipment-maintain-repair-editor')
                    ab('fill', '[aria-label="维护维修日期"]', '2027-04-01')
                    ab('fill', '[aria-label="维护维修内容"]', 'Browser Repair Lifecycle')
                    ab('fill', '[aria-label="签名"]', 'Browser User')
                    ab('fill', '[aria-label="仪器故障说明"]', 'none')
                    ab('fill', '[aria-label="维修单位（人）"]', 'Browser Repair Unit')
                    ab('find', 'role', 'button', 'click', '--name', '保存维护维修记录', '--exact')
                    ab('wait', '--text', '维护维修记录已新增')
                    inserted_repair = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'UI_REPAIR_INSERT|'||ID||'|'||EQUIPTID||'|'||TO_CHAR(MAINTAINREPAIRDATE,'YYYY-MM-DD')||'|'||CREATEDBYORGID||'|'||CREATEDBYID||'|'||MAINTAINREPAIRCONCENT FROM T_EMC_EQUIPT_MAINTAIN_REPAIR WHERE MAINTAINREPAIRCONCENT='Browser Repair Lifecycle';"), 'maintenance repair UI insert verification')
                    check('maintenance repair insert persists sequence, scope, actor and content', re.search(r'UI_REPAIR_INSERT\|[1-9][0-9][0-9][0-9]\|1\|2027-04-01\|org-a\|e-1\|Browser Repair Lifecycle', inserted_repair) is not None)
                    ab('wait', '--text', 'Browser Repair Lifecycle')
                    ab('click', '#emc-equipt-maintain-repair-edit-list-grid tbody tr')
                    ab('eval', "Gikam.getComp('emc-equipt-maintain-repair-edit-list-grid').activeRowByIndex(0)")
                    ab('find', 'role', 'button', 'click', '--name', '删除', '--exact')
                    ab('wait', '#equipment-maintain-repair-delete')
                    ab('find', 'role', 'button', 'click', '--name', '确认删除维护维修记录', '--exact')
                    ab('wait', '--text', '维护维修记录已删除')
                    deleted_repair = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'UI_REPAIR_ROWS|'||COUNT(*) FROM T_EMC_EQUIPT_MAINTAIN_REPAIR WHERE MAINTAINREPAIRCONCENT='Browser Repair Lifecycle';\nSELECT 'UI_REPAIR_BASELINE|'||COUNT(*) FROM T_EMC_EQUIPT_MAINTAIN_REPAIR WHERE ID=601;"), 'maintenance repair UI delete verification')
                    check('maintenance repair delete removes temporary row and keeps baseline', 'UI_REPAIR_ROWS|0' in deleted_repair and 'UI_REPAIR_BASELINE|1' in deleted_repair)
                    lifecycle_audit=audit.read_text(errors='replace')
                    check('maintenance repair lifecycle emits audit', 'equipment-maintain-repair-insert' in lifecycle_audit and 'equipment-maintain-repair-delete' in lifecycle_audit and 'secret' not in lifecycle_audit.lower())
                    report['scope']='packaged equipment maintenance-repair browser create/delete; synthetic Oracle only'; report['status']='executed-pass'; raise StockOnlyComplete()
                if args.equipment_period_lifecycle_only:
                    ab('find', 'role', 'tab', 'click', '--name', '期间核查计划', '--exact')
                    ab('wait', '#emc-equipt-per-check-plan-edit-list-grid')
                    ab('wait', '--text', 'Calibration')
                    ab('find', 'role', 'button', 'click', '--name', '新增', '--exact')
                    ab('wait', '#equipment-period-plan-editor')
                    ab('wait', '--fn', "document.querySelector('[aria-label=\"维护事件\"]')?.disabled === false")
                    ab('select', '[aria-label="维护事件"]', '51')
                    ab('fill', '[aria-label="核查周期"]', '2')
                    ab('fill', '[aria-label="核查时间"]', '2027-02-01')
                    ab('fill', '[aria-label="提醒时间（天）"]', '7')
                    ab('fill', '[aria-label="核查依据"]', 'Browser Period')
                    ab('fill', '[aria-label="核查所用仪器设备"]', 'Reference Browser')
                    ab('fill', '[aria-label="核查方式"]', 'visual')
                    ab('fill', '[aria-label="备注"]', 'browser period plan lifecycle')
                    ab('find', 'role', 'button', 'click', '--name', '保存期间核查计划', '--exact')
                    ab('wait', '--text', '期间核查计划已新增')
                    inserted_period_plan = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'UI_PERIOD_PLAN_INSERT|'||ID||'|'||EQUIPTID||'|'||MAINTID||'|'||CREATEDBYORGID||'|'||CREATEDBYID||'|'||CHECKREASON FROM T_EMC_EQUIPT_PER_CHECK_PLAN WHERE REMARK='browser period plan lifecycle';"), 'period plan UI insert verification')
                    check('period plan insert persists sequence, scope, actor and reason', re.search(r'UI_PERIOD_PLAN_INSERT\|[1-9][0-9][0-9][0-9]\|1\|51\|org-a\|e-1\|Browser Period', inserted_period_plan) is not None)
                    ab('wait', '--text', 'Browser Period')
                    ab('find', 'text', 'Browser Period', 'click', '--exact')
                    ab('find', 'role', 'button', 'click', '--name', '删除', '--exact')
                    ab('wait', '#equipment-period-plan-delete')
                    ab('find', 'role', 'button', 'click', '--name', '确认删除期间核查计划', '--exact')
                    ab('wait', '--text', '期间核查计划已删除')
                    deleted_period_plan = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'UI_PERIOD_PLAN_ROWS|'||COUNT(*) FROM T_EMC_EQUIPT_PER_CHECK_PLAN WHERE REMARK='browser period plan lifecycle';\nSELECT 'UI_PERIOD_PLAN_BASELINE|'||COUNT(*) FROM T_EMC_EQUIPT_PER_CHECK_PLAN WHERE ID=401;"), 'period plan UI delete verification')
                    check('period plan delete removes temporary row and keeps baseline', 'UI_PERIOD_PLAN_ROWS|0' in deleted_period_plan and 'UI_PERIOD_PLAN_BASELINE|1' in deleted_period_plan)
                    ab('find', 'role', 'tab', 'click', '--name', '期间核查记录', '--exact')
                    ab('wait', '#emc-equipt-per-check-record-edit-list-grid')
                    ab('wait', '--text', 'Calibration')
                    ab('find', 'role', 'button', 'click', '--name', '新增', '--exact')
                    ab('wait', '#equipment-period-record-editor')
                    ab('wait', '--fn', "document.querySelector('[aria-label=\"维护事件\"]')?.disabled === false")
                    ab('select', '[aria-label="维护事件"]', '51')
                    ab('fill', '[aria-label="核查依据"]', 'Browser Period Record')
                    ab('fill', '[aria-label="核查所用仪器设备"]', 'Reference Browser')
                    ab('fill', '[aria-label="备注"]', 'browser period record lifecycle')
                    ab('find', 'role', 'button', 'click', '--name', '保存期间核查记录', '--exact')
                    ab('wait', '--text', '期间核查记录已新增')
                    inserted_period_record = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'UI_PERIOD_RECORD_INSERT|'||ID||'|'||EQUIPTID||'|'||MAINTID||'|'||CREATEDBYORGID||'|'||CREATEDBYID||'|'||CHECKREASON FROM T_EMC_EQUIPT_PER_CHECK_RECORD WHERE REMARK='browser period record lifecycle';"), 'period record UI insert verification')
                    check('period record insert persists sequence, scope, actor and reason', re.search(r'UI_PERIOD_RECORD_INSERT\|[1-9][0-9][0-9][0-9]\|1\|51\|org-a\|e-1\|Browser Period Record', inserted_period_record) is not None)
                    ab('wait', '--text', 'Browser Period Record')
                    ab('find', 'text', 'Browser Period Record', 'click', '--exact')
                    ab('find', 'role', 'button', 'click', '--name', '删除', '--exact')
                    ab('wait', '#equipment-period-record-delete')
                    ab('find', 'role', 'button', 'click', '--name', '确认删除期间核查记录', '--exact')
                    ab('wait', '--text', '期间核查记录已删除')
                    deleted_period_record = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'UI_PERIOD_RECORD_ROWS|'||COUNT(*) FROM T_EMC_EQUIPT_PER_CHECK_RECORD WHERE REMARK='browser period record lifecycle';\nSELECT 'UI_PERIOD_RECORD_BASELINE|'||COUNT(*) FROM T_EMC_EQUIPT_PER_CHECK_RECORD WHERE ID=501;"), 'period record UI delete verification')
                    check('period record delete removes temporary row and keeps baseline', 'UI_PERIOD_RECORD_ROWS|0' in deleted_period_record and 'UI_PERIOD_RECORD_BASELINE|1' in deleted_period_record)
                    lifecycle_audit = audit.read_text(errors='replace')
                    check('period lifecycle emits insert and delete audits', all(value in lifecycle_audit for value in ('equipment-per-check-plan-insert', 'equipment-per-check-plan-delete', 'equipment-per-check-record-insert', 'equipment-per-check-record-delete')) and 'secret' not in lifecycle_audit.lower())
                    report['scope'] = 'packaged equipment period-check browser create/delete; synthetic Oracle only'
                    report['status'] = 'executed-pass'
                    raise StockOnlyComplete()
                ab('fill', '[aria-label="设备名称筛选"]', 'absent')
                ab('click', '#emc-equipt-edit-list-grid .filter-controls button')
                ab('wait', '--text', '共0条')
                check('equipment no match clears selected detail', 'Alpha Scope' not in ab('get', 'text', '#owned-workspace'))
                ab('fill', '[aria-label="设备名称筛选"]', '')
                ab('fill', '[aria-label="设备编号筛选"]', 'EQ-001')
                ab('click', '#emc-equipt-edit-list-grid .filter-controls button')
                ab('wait', '--text', '共1条')
                check('equipment identifier filter returns one row', 'EQ-001' in ab('get', 'text', '#emc-equipt-edit-list-grid'))
                ab('click', '#emc-equipt-edit-list-grid .filter-controls button:last-child')
                ab('wait', '--text', '共23条')
                ab('find', 'role', 'button', 'click', '--name', '下一页', '--exact')
                ab('wait', '--text', '第 2 / 2 页')
                page = ab('get', 'text', '#emc-equipt-edit-list-grid')
                check('equipment second page contains final three scoped rows', all(value in page for value in ('EQ-PAGE-001', 'EQ-003', 'EQ-001')) and 'EQ-PAGE-002' not in page)
                ab('select', '#emc-equipt-edit-list-grid [aria-label="每页条数"]', '10')
                ab('wait', '--text', '第 1 / 3 页')
                check('equipment page size resets pagination', True)
                capability = ab('eval', "fetch('/owned/equipment/write-available').then(async r => JSON.stringify({status:r.status,body:await r.text()}))")
                check('equipment isolated write capability endpoint allowed: ' + capability, 'status\\":200' in capability and 'available' in capability)
                check('equipment create control visible', '新增设备' in ab('snapshot', '-i'))
                ab('find', 'role', 'button', 'click', '--name', '新增设备', '--exact')
                state = ab('eval', 'JSON.stringify({dialog:!!document.querySelector("#equipment-editor"), status:document.querySelector("#page-status")?.textContent || "", grid:document.querySelector("#emc-equipt-edit-list-grid")?.textContent || ""})')
                check('equipment create dialog state: ' + state.replace('\n', '|'), 'dialog' in state or '设备' in state)
                ab('wait', '--fn', "document.querySelector('#equipment-editor') !== null")
                ab('fill', '[aria-label="设备编号"]', 'EQ-BROWSER')
                ab('fill', '[aria-label="设备名称"]', 'Browser Equipment')
                ab('fill', '[aria-label="规格型号"]', 'fixture-spec')
                ab('fill', '[aria-label="生产厂家"]', 'fixture-maker')
                ab('fill', '[aria-label="出厂编号"]', 'fixture-factory')
                ab('wait', '--fn', "document.querySelector('[aria-label=\"设备类别\"]')?.disabled === false")
                ab('select', '[aria-label="设备类别"]', '11')
                ab('find', 'role', 'button', 'click', '--name', '保存设备', '--exact')
                ab('wait', '--text', '设备已新增')
                inserted_equipment = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'UI_EQUIPMENT_INSERT|'||COUNT(*) FROM T_EMC_EQUIPT WHERE EQUIPTID='EQ-BROWSER' AND EQUIPTNAME='Browser Equipment' AND ORGID='org-a';"), 'equipment UI insert verification')
                check('equipment dialog insert persists scoped Oracle row', 'UI_EQUIPMENT_INSERT|1' in inserted_equipment)
                ab('fill', '[aria-label="设备名称筛选"]', 'Browser Equipment')
                ab('click', '#emc-equipt-edit-list-grid .filter-controls button')
                ab('wait', '--text', '共1条')
                check('equipment browser create returns visible row', 'EQ-BROWSER' in ab('get', 'text', '#emc-equipt-edit-list-grid'))
                cleanup_equipment = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nDELETE FROM T_EMC_EQUIPT WHERE EQUIPTID='EQ-BROWSER';\nCOMMIT;\nSELECT 'UI_EQUIPMENT_CLEANUP|'||COUNT(*) FROM T_EMC_EQUIPT WHERE EQUIPTID='EQ-BROWSER';"), 'equipment browser cleanup')
                check('equipment browser fixture cleanup restores baseline', 'UI_EQUIPMENT_CLEANUP|0' in cleanup_equipment)
                check('equipment auxiliary flows remain explicitly unavailable', '未验证/不可用' in ab('snapshot', '-i'))
                screenshot = temporary / 'equipment.png'
                ab('screenshot', str(screenshot))
                report['equipment_screenshot_sha256'] = digest(screenshot)
                ab('click', '#logout')
                ab('wait', '--text', '已退出登录。')
                check('equipment logout clears display', 'Alpha Scope' not in ab('get', 'text', 'body'))
                ab('fill', '#login-id', 'other')
                ab('fill', '#login-password', 'secret')
                ab('click', '#login-submit')
                ab('wait', '--fn', "document.body.textContent.includes('当前账号没有可用的查询入口') || document.body.textContent.includes('当前账号无设备查询权限')")
                check('equipment low permission page denied', 'EQ-001' not in ab('get', 'text', 'body'))
                rejected = ab('eval', "fetch('/secure/emc/module/mdm/equipt/equipts/queries/raw',{method:'POST',headers:{'Content-Type':'application/json'},body:'{}'}).then(r=>r.status)")
                check('equipment low permission API denied', '403' in rejected)
                # A denied page read intentionally returns to the login view,
                # but the server may still retain the low-permission session.
                # Clear it through the real logout route before the next login
                # so this check does not depend on session replacement timing.
                cleared = ab('eval', "fetch('/core/module/sys/logout',{method:'POST'}).then(r=>r.json()).then(r=>r.authenticated)")
                check('low permission session logout confirmed', 'false' in cleared)
                ab('open', url)
                ab('wait', '#login-submit')
                ab('fill', '#login-id', 'e-1')
                ab('fill', '#login-password', 'secret')
                ab('click', '#login-submit')
                ab('wait', '--text', '共23条')
                ab('click', '#consumables-link')
                ab('wait', '#emc-consume-edit-list-grid')
                ab('wait', '--fn', "document.querySelector('#emc-consume-edit-list-grid .grid-count')?.textContent === '共23条'")
                check('consumables original list and scoped labels', 'CSM-PAGE-021' in ab('get', 'text', '#emc-consume-edit-list-grid') and 'CSM-002' not in ab('get', 'text', '#owned-workspace'))
                if args.distribution:
                    report.setdefault('http_cases', []).append({'route': 'GET /?page=consumables', 'role': 'manager',
                                                                 'organization': 'org-a', 'mode': 'isolated',
                                                                 'status': 200, 'expected_status': 200})
                material_capability = ab('eval', "fetch('/owned/consumables/material-write-available').then(async r => JSON.stringify({status:r.status,body:await r.text()}))")
                check('consumables material write capability endpoint allowed', 'status\\":200' in material_capability and 'available' in material_capability)
                check('consumables material create control visible', '新增耗材' in ab('get', 'text', '#emc-consume-edit-list-grid'))
                if args.distribution:
                    recipients = ab('eval', "fetch('/owned/consumables/stock-recipients').then(async r => JSON.stringify({status:r.status,body:await r.text()}))")
                    check('distribution consumable organization scope is readable', 'status\\":200' in recipients and 'e-1' in recipients)
                    ab('eval', "(() => { const original = window.fetch; window.__ownedConsumableCases = []; window.fetch = function(url, options) { return original.apply(this, arguments).then(async response => { const path = new URL(url, location.origin).pathname; if (path.includes('/secure/emc/module/item/consume/consumes')) window.__ownedConsumableCases.push({route:(options?.method || 'GET') + ' ' + path, status:response.status}); return response; }); }; return true; })()")
                if args.material_create_only or args.material_lifecycle_only:
                    ab('find', 'role', 'button', 'click', '--name', '新增耗材', '--exact')
                    ab('wait', '#consumable-editor')
                    ab('fill', '[aria-label="材料编码"]', 'CSM-BROWSER')
                    ab('fill', '[aria-label="材料名称"]', 'Browser Material')
                    ab('fill', '[aria-label="规格型号"]', 'browser-spec')
                    ab('fill', '[aria-label="货号"]', 'P-BROWSER-MATERIAL')
                    ab('fill', '[aria-label="材料类型"]', 'fixture')
                    ab('fill', '[aria-label="危害性"]', 'normal')
                    ab('find', 'role', 'button', 'click', '--name', '保存耗材', '--exact')
                    ab('wait', '--fn', "document.querySelector('#consumable-editor') === null")
                    ab('fill', '[aria-label="材料名称筛选"]', 'Browser Material')
                    ab('click', '#emc-consume-edit-list-grid .filter-controls button')
                    ab('wait', '--text', '共1条')
                    check('browser material create returns visible row', 'CSM-BROWSER' in ab('get', 'text', '#emc-consume-edit-list-grid'))
                    material_write = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'BROWSER_MATERIAL|'||ID||'|'||MTLNAME||'|'||PRODUCTNO||'|'||CREATEDBYORGID FROM T_EMC_CONSUME WHERE ID='CSM-BROWSER';"), 'browser material insert verification')
                    check('browser material insert persists scoped Oracle row', 'BROWSER_MATERIAL|CSM-BROWSER|Browser Material|P-BROWSER-MATERIAL|org-a' in material_write)
                    if args.distribution:
                        scope_probe = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'MATERIAL_SCOPE|'||COUNT(*) FROM T_EMC_CONSUME WHERE ID='CSM-BROWSER' AND CREATEDBYORGID='org-a';"), 'distribution material scope probe')
                        check('distribution material scope row is visible', 'MATERIAL_SCOPE|1' in scope_probe)
                    if args.material_lifecycle_only:
                        ab('wait', '--fn', "document.body.textContent.includes('CSM-BROWSER')")
                        ab('find', 'text', 'CSM-BROWSER', 'click', '--exact')
                        ab('find', 'role', 'button', 'click', '--name', '修改当前耗材', '--exact')
                        ab('wait', '#consumable-editor')
                        ab('fill', '[aria-label="材料名称"]', 'Browser Material Updated')
                        ab('find', 'role', 'button', 'click', '--name', '保存耗材', '--exact')
                        ab('wait', '--fn', "document.querySelector('#consumable-editor') === null")
                        ab('fill', '[aria-label="材料名称筛选"]', 'Browser Material Updated')
                        ab('click', '#emc-consume-edit-list-grid .filter-controls button')
                        ab('wait', '--text', '共1条')
                        check('browser material update returns visible row', 'CSM-BROWSER' in ab('get', 'text', '#emc-consume-edit-list-grid'))
                        material_update = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'BROWSER_MATERIAL_UPDATE|'||MTLNAME||'|'||SPECIFICATION||'|'||PRODUCTNO FROM T_EMC_CONSUME WHERE ID='CSM-BROWSER';"), 'browser material update verification')
                        check('browser material update persists fields', 'BROWSER_MATERIAL_UPDATE|Browser Material Updated|browser-spec|P-BROWSER-MATERIAL' in material_update)
                        ab('wait', '--fn', "document.body.textContent.includes('CSM-BROWSER')")
                        ab('find', 'text', 'CSM-BROWSER', 'click', '--exact')
                        ab('find', 'role', 'button', 'click', '--name', '删除当前耗材', '--exact')
                        ab('wait', '#consumable-editor')
                        ab('find', 'role', 'button', 'click', '--name', '确认删除耗材', '--exact')
                        if not args.distribution:
                            ab('wait', '--fn', "document.querySelector('#consumable-editor') === null")
                        if not args.distribution:
                            ab('find', 'role', 'button', 'click', '--name', '清空', '--exact')
                            ab('wait', '--text', '共23条')
                        delete_deadline = time.monotonic() + 30
                        lifecycle_delete = ''
                        while time.monotonic() < delete_deadline:
                            lifecycle_delete = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'BROWSER_MATERIAL_ROWS|'||COUNT(*) FROM T_EMC_CONSUME WHERE ID='CSM-BROWSER';\nSELECT 'BROWSER_MATERIAL_STOCKS|'||COUNT(*) FROM T_EMC_CSM_STOCK WHERE MTLID='CSM-BROWSER';\nSELECT 'BROWSER_MATERIAL_LOGS|'||COUNT(*) FROM T_EMC_CSM_STOCK_LOG WHERE STOCKID IN (SELECT ID FROM T_EMC_CSM_STOCK WHERE MTLID='CSM-BROWSER');"), 'browser material delete verification')
                            if ('BROWSER_MATERIAL_ROWS|0' in lifecycle_delete and
                                    'BROWSER_MATERIAL_STOCKS|0' in lifecycle_delete and
                                    'BROWSER_MATERIAL_LOGS|0' in lifecycle_delete):
                                break
                            time.sleep(.5)
                        check('browser material delete removes row and dependent stock history', 'BROWSER_MATERIAL_ROWS|0' in lifecycle_delete and 'BROWSER_MATERIAL_STOCKS|0' in lifecycle_delete and 'BROWSER_MATERIAL_LOGS|0' in lifecycle_delete)
                        if args.distribution:
                            cases_payload = json.loads(ab('eval', 'JSON.stringify(window.__ownedConsumableCases || [])'))
                            cases_json = cases_payload.get('data', {}).get('result', cases_payload.get('result', cases_payload)) if isinstance(cases_payload, dict) else cases_payload
                            cases = json.loads(cases_json) if isinstance(cases_json, str) else cases_json
                            report['http_cases'] = report.get('http_cases', []) + [
                                dict(item, role='manager', organization='org-a', mode='isolated',
                                     expected_status=item['status']) for item in cases]
                            audit_events = [json.loads(line) for line in audit.read_text(errors='replace').splitlines() if line.strip()]
                            report['audit'] = {'events': audit_events,
                                               'expected': [{'action': 'consumable-material-insert', 'outcome': 'ALLOWED'}]}
                        report['scope'] = ('owned distribution consumables browser material create update delete; '
                                          'synthetic Oracle and Redis only' if args.distribution else
                                          'packaged consumables browser material create update delete; synthetic Oracle only')
                        report['status'] = 'executed-pass'
                        raise StockOnlyComplete()
                    passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nDELETE FROM T_EMC_CONSUME WHERE ID='CSM-BROWSER';\nCOMMIT;"), 'browser material fixture cleanup')
                    check('browser material create cleanup restores fixture', True)
                    report['scope'] = ('owned distribution consumables browser material creation; synthetic Oracle and Redis only'
                                      if args.distribution else 'packaged consumables browser material creation; synthetic Oracle only')
                    report['status'] = 'executed-pass'
                    raise StockOnlyComplete()
                ab('fill', '[aria-label="材料名称筛选"]', 'Alpha')
                ab('click', '#emc-consume-edit-list-grid .filter-controls button')
                ab('wait', '--fn', "document.querySelector('#emc-consume-edit-list-grid .grid-count')?.textContent === '共1条'")
                check('consumables name filter returns one scoped row', 'CSM-001' in ab('get', 'text', '#emc-consume-edit-list-grid'))
                ab('find', 'text', 'CSM-001', 'click', '--exact')
                ab('wait', '--fn', "document.querySelector('#owned-consume-detail-form output[data-field=mtlName]')?.textContent === 'Alpha Bottle'")
                ab('wait', '--text', 'A-1')
                detail = ab('get', 'text', '#owned-consume-detail-form')
                check('consumables detail and stock read', all(value in detail for value in ('Alpha Bottle', '100 ml', '7', 'P-001')))
                ab('wait', '--fn', "document.querySelector('#emc-consume-detail-stock-log-list-grid .grid-count')?.textContent === '共2条'")
                stock = ab('get', 'text', '#emc-consume-detail-stock-list-grid')
                check('consumables stock row preserves quantity and expiry', all(value in stock for value in ('A-1', '5', 'bottle', '2026-01-01')) and 'A-2' not in stock)
                log = ab('get', 'text', '#emc-consume-detail-stock-log-list-grid')
                check('consumables stock log read', all(value in log for value in ('receive', 'stockin', 'Allowed User', 'Fixture Org A')))
                if args.consumables_query_projection_only:
                    # The read-only seam exposes every visible base-information
                    # field declared by the original consumables page. Keep the
                    # assertion tied to the synthetic Oracle row so a partial
                    # projection cannot pass on labels alone.
                    check('consumables list exposes complete visible projection', all(value in ab('get', 'text', '#emc-consume-edit-list-grid') for value in ('CSM-001', 'Alpha Bottle', '100 ml', '7', 'glass', 'P-001')))
                    check('consumables detail exposes complete visible projection', all(value in detail for value in ('CSM-001', 'Alpha Bottle', '100 ml', '7', 'glass', 'P-001')))
                    ab('fill', '[aria-label="材料名称筛选"]', 'absent')
                    ab('click', '#emc-consume-edit-list-grid .filter-controls button')
                    ab('wait', '--fn', "document.querySelector('#emc-consume-edit-list-grid .grid-count')?.textContent === '共0条'")
                    check('consumables projection no-match clears detail and stock', all(value not in ab('get', 'text', '#owned-workspace') for value in ('Alpha Bottle', 'A-1', 'receive')))
                    ab('fill', '[aria-label="材料名称筛选"]', '')
                    ab('click', '#emc-consume-edit-list-grid .filter-controls button:last-child')
                    ab('wait', '--fn', "document.querySelector('#emc-consume-edit-list-grid .grid-count')?.textContent === '共23条'")
                    ab('find', 'role', 'button', 'click', '--name', '下一页', '--exact')
                    ab('wait', '--text', '第 2 / 2 页')
                    projection_page = ab('get', 'text', '#emc-consume-edit-list-grid')
                    check('consumables projection pagination preserves scoped rows', all(value in projection_page for value in ('CSM-PAGE-001', 'CSM-003', 'CSM-001')) and 'CSM-PAGE-002' not in projection_page)
                    report['scope'] = 'packaged consumables browser full visible projection, filters and pagination; synthetic Oracle only'
                    report['status'] = 'executed-pass'
                    raise StockOnlyComplete()
                stock_capability = ab('eval', "fetch('/owned/consumables/stock-write-available').then(async r => JSON.stringify({status:r.status,body:await r.text()}))")
                check('consumables stock write capability endpoint allowed', 'status\\":200' in stock_capability and 'available' in stock_capability)
                check('consumables stock write controls visible', all(value in ab('get', 'text', '#emc-consume-detail-stock-list-grid') for value in ('新增库存', '入库', '盘存', '废弃', '领用', '删除库存')))
                if args.stock_create_delete_only:
                    ab('find', 'role', 'button', 'click', '--name', '新增库存', '--exact')
                    ab('wait', '#consumable-stock-editor')
                    ab('fill', '[aria-label="批号"]', 'BROWSER-DELETE')
                    ab('fill', '[aria-label="数量"]', '2')
                    ab('find', 'role', 'button', 'click', '--name', '保存', '--exact')
                    ab('wait', '--fn', "document.querySelector('#consumable-stock-editor') === null")
                    ab('click', '#emc-consume-detail-stock-list-grid .owned-toolbar button:first-child')
                    ab('wait', '--text', 'BROWSER-DELETE')
                    created_stock = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'BROWSER_STOCK|'||ID||'|'||STOCKQTY FROM T_EMC_CSM_STOCK WHERE MTLID='CSM-001' AND BATCHNO='BROWSER-DELETE';"), 'browser stock insert verification')
                    check('browser stock insert persists temporary row', 'BROWSER_STOCK|' in created_stock and '|2' in created_stock)
                    ab('find', 'text', 'BROWSER-DELETE', 'click', '--exact')
                    ab('find', 'role', 'button', 'click', '--name', '删除库存', '--exact')
                    ab('wait', '#consumable-stock-editor')
                    ab('find', 'role', 'button', 'click', '--name', '确认删除库存', '--exact')
                    ab('wait', '--fn', "document.querySelector('#consumable-stock-editor') === null")
                    ab('click', '#emc-consume-detail-stock-list-grid .owned-toolbar button:first-child')
                    ab('wait', '--fn', "!document.querySelector('#emc-consume-detail-stock-list-grid')?.textContent.includes('BROWSER-DELETE')")
                    delete_write = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'BROWSER_STOCK_ROWS|'||COUNT(*) FROM T_EMC_CSM_STOCK WHERE MTLID='CSM-001' AND BATCHNO='BROWSER-DELETE';\nSELECT 'BROWSER_STOCK_LOGS|'||COUNT(*) FROM T_EMC_CSM_STOCK_LOG WHERE STOCKID IN (SELECT ID FROM T_EMC_CSM_STOCK WHERE MTLID='CSM-001' AND BATCHNO='BROWSER-DELETE');\nSELECT 'BROWSER_AGG|'||STOCKQTY FROM T_EMC_CONSUME WHERE ID='CSM-001';"), 'browser stock delete verification')
                    check('browser stock delete removes temporary row and history', 'BROWSER_STOCK_ROWS|0' in delete_write and 'BROWSER_STOCK_LOGS|0' in delete_write and 'BROWSER_AGG|5' in delete_write)
                    passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nUPDATE T_EMC_CONSUME SET STOCKQTY=7 WHERE ID='CSM-001';\nCOMMIT;"), 'browser stock create/delete fixture cleanup')
                    check('browser stock create/delete restores fixture aggregate', True)
                    report['scope'] = 'packaged consumables browser stock creation and deletion; synthetic Oracle only'
                    report['status'] = 'executed-pass'
                    raise StockOnlyComplete()
                ab('find', 'text', 'A-1', 'click', '--exact')
                ab('find', 'role', 'button', 'click', '--name', '入库', '--exact')
                ab('wait', '#consumable-stock-editor')
                ab('fill', '[aria-label="入库数量"]', '3')
                ab('find', 'role', 'button', 'click', '--name', '保存', '--exact')
                ab('wait', '--fn', "document.querySelector('#consumable-stock-editor') === null")
                ab('click', '#emc-consume-detail-stock-list-grid .owned-toolbar button:first-child')
                ab('wait', 1000)
                check('browser stock refresh displays updated quantity', '8' in ab('get', 'text', '#emc-consume-detail-stock-list-grid'))
                stock_write = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT STOCKQTY FROM T_EMC_CSM_STOCK WHERE ID=11;\nSELECT COUNT(*) FROM T_EMC_CSM_STOCK_LOG WHERE STOCKID=11 AND LOGTYPE='入库' AND QTY=8;"), 'browser stock-in verification')
                check('browser stock-in updates quantity and appends history', '8' in stock_write and '1' in stock_write)
                ab('find', 'role', 'button', 'click', '--name', '盘存', '--exact')
                ab('wait', '#consumable-stock-editor')
                ab('fill', '[aria-label="盘存数量"]', '6')
                ab('find', 'role', 'button', 'click', '--name', '保存', '--exact')
                ab('wait', '--fn', "document.querySelector('#consumable-stock-editor') === null")
                inventory_write = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT STOCKQTY FROM T_EMC_CSM_STOCK WHERE ID=11;\nSELECT COUNT(*) FROM T_EMC_CSM_STOCK_LOG WHERE STOCKID=11 AND LOGTYPE='盘存' AND QTY=6;"), 'browser inventory verification')
                check('browser inventory updates quantity and appends history', '6' in inventory_write and '1' in inventory_write)
                ab('find', 'role', 'button', 'click', '--name', '领用', '--exact')
                ab('wait', '#consumable-stock-editor')
                ab('fill', '[aria-label="领用数量"]', '2')
                ab('wait', '--fn', "document.querySelector('[aria-label=\"领用人\"]')?.disabled === false && document.querySelector('[aria-label=\"领用人\"] option[value=\"e-1\"]') !== null")
                ab('select', '[aria-label="领用人"]', 'e-1')
                ab('find', 'role', 'button', 'click', '--name', '保存', '--exact')
                ab('wait', '--fn', "document.querySelector('#consumable-stock-editor') === null")
                receive_write = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT STOCKQTY FROM T_EMC_CSM_STOCK WHERE ID=11;\nSELECT COUNT(*) FROM T_EMC_CSM_STOCK_LOG WHERE STOCKID=11 AND LOGTYPE='领取' AND QTY=4;"), 'browser receive verification')
                check('browser receive updates quantity and appends history', '4' in receive_write and '1' in receive_write)
                ab('find', 'role', 'button', 'click', '--name', '废弃', '--exact')
                ab('wait', '#consumable-stock-editor')
                ab('fill', '[aria-label="废弃说明"]', 'browser abandon')
                ab('find', 'role', 'button', 'click', '--name', '保存', '--exact')
                ab('wait', '--fn', "document.querySelector('#consumable-stock-editor') === null")
                abandon_write = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT STATUS FROM T_EMC_CSM_STOCK WHERE ID=11;\nSELECT COUNT(*) FROM T_EMC_CSM_STOCK_LOG WHERE STOCKID=11 AND LOGTYPE='废弃';"), 'browser abandon verification')
                check('browser abandon marks stock and appends history', '1' in abandon_write and '1' in abandon_write)
                passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nUPDATE T_EMC_CSM_STOCK SET STOCKQTY=5,STATUS='0' WHERE ID=11;\nDELETE FROM T_EMC_CSM_STOCK_LOG WHERE STOCKID=11 AND ID>=1000;\nUPDATE T_EMC_CONSUME SET STOCKQTY=7 WHERE ID='CSM-001';\nCOMMIT;"), 'browser stock write cleanup')
                check('browser stock writes restore fixture baseline', True)
                ab('find', 'role', 'button', 'click', '--name', '新增库存', '--exact')
                ab('wait', '#consumable-stock-editor')
                ab('fill', '[aria-label="批号"]', 'BROWSER-DELETE')
                ab('fill', '[aria-label="数量"]', '2')
                ab('find', 'role', 'button', 'click', '--name', '保存', '--exact')
                ab('wait', '--fn', "document.querySelector('#consumable-stock-editor') === null")
                ab('click', '#emc-consume-detail-stock-list-grid .owned-toolbar button:first-child')
                ab('wait', '--text', 'BROWSER-DELETE')
                created_stock = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'BROWSER_STOCK|'||ID||'|'||STOCKQTY FROM T_EMC_CSM_STOCK WHERE MTLID='CSM-001' AND BATCHNO='BROWSER-DELETE';"), 'browser stock insert verification')
                check('browser stock insert persists temporary row', 'BROWSER_STOCK|' in created_stock and '|2' in created_stock)
                ab('find', 'text', 'BROWSER-DELETE', 'click', '--exact')
                ab('find', 'role', 'button', 'click', '--name', '删除库存', '--exact')
                ab('wait', '#consumable-stock-editor')
                ab('find', 'role', 'button', 'click', '--name', '确认删除库存', '--exact')
                ab('wait', '--fn', "document.querySelector('#consumable-stock-editor') === null")
                ab('click', '#emc-consume-detail-stock-list-grid .owned-toolbar button:first-child')
                ab('wait', '--fn', "!document.querySelector('#emc-consume-detail-stock-list-grid')?.textContent.includes('BROWSER-DELETE')")
                delete_write = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'BROWSER_STOCK_ROWS|'||COUNT(*) FROM T_EMC_CSM_STOCK WHERE MTLID='CSM-001' AND BATCHNO='BROWSER-DELETE';\nSELECT 'BROWSER_STOCK_LOGS|'||COUNT(*) FROM T_EMC_CSM_STOCK_LOG WHERE STOCKID IN (SELECT ID FROM T_EMC_CSM_STOCK WHERE MTLID='CSM-001' AND BATCHNO='BROWSER-DELETE');\nSELECT 'BROWSER_AGG|'||STOCKQTY FROM T_EMC_CONSUME WHERE ID='CSM-001';"), 'browser stock delete verification')
                check('browser stock delete removes temporary row and history', 'BROWSER_STOCK_ROWS|0' in delete_write and 'BROWSER_STOCK_LOGS|0' in delete_write and 'BROWSER_AGG|5' in delete_write)
                passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nUPDATE T_EMC_CONSUME SET STOCKQTY=7 WHERE ID='CSM-001';\nCOMMIT;"), 'browser stock create/delete fixture cleanup')
                check('browser stock create/delete restores fixture aggregate', True)
                if args.stock_only:
                    report['scope'] = 'packaged consumables browser stock writes; synthetic Oracle only'
                    report['status'] = 'executed-pass'
                    raise StockOnlyComplete()
                ab('check', '[aria-label="显示全部库存"]')
                ab('wait', '--text', 'A-2')
                check('show all includes depleted stock', 'A-2' in ab('get', 'text', '#emc-consume-detail-stock-list-grid'))
                check('show all control retains checked state', 'true' in ab('eval', 'document.querySelector(\'[aria-label="显示全部库存"]\').checked'))
                ab('uncheck', '[aria-label="显示全部库存"]')
                ab('wait', '--fn', "document.querySelector('#emc-consume-detail-stock-list-grid .grid-count')?.textContent === '共1条'")
                check('default stock view excludes depleted row', 'A-2' not in ab('get', 'text', '#emc-consume-detail-stock-list-grid'))
                ab('fill', '[aria-label="材料名称筛选"]', 'absent')
                ab('click', '#emc-consume-edit-list-grid .filter-controls button')
                ab('wait', '--fn', "document.querySelector('#emc-consume-edit-list-grid .grid-count')?.textContent === '共0条'")
                check('consumables no match clears detail and stock', all(value not in ab('get', 'text', '#owned-workspace') for value in ('Alpha Bottle', 'A-1', 'receive')))
                ab('fill', '[aria-label="材料名称筛选"]', '')
                ab('click', '#emc-consume-edit-list-grid .filter-controls button:last-child')
                ab('wait', '--fn', "document.querySelector('#emc-consume-edit-list-grid .grid-count')?.textContent === '共23条'")
                ab('find', 'role', 'button', 'click', '--name', '下一页', '--exact')
                ab('wait', '--text', '第 2 / 2 页')
                page = ab('get', 'text', '#emc-consume-edit-list-grid')
                check('consumables second page contains final three scoped rows', all(value in page for value in ('CSM-PAGE-001', 'CSM-003', 'CSM-001')) and 'CSM-PAGE-002' not in page)
                check('consumables writes and auxiliary flows unavailable', '未验证/不可用' in ab('snapshot', '-i'))
                screenshot = temporary / 'consumables.png'
                ab('screenshot', str(screenshot))
                report['consumables_screenshot_sha256'] = digest(screenshot)
                capability = ab('eval', "fetch('/owned/consumables/import-export-available').then(async r => JSON.stringify({status:r.status,body:await r.text()}))")
                check('consumables import/export capability endpoint allowed', 'status\\":200' in capability and 'available' in capability)
                check('consumables isolated import/export controls visible', all(value in ab('get', 'text', '#emc-consume-edit-list-grid') for value in ('下载导入模板', '导入耗材')))
                ab('download', '#emc-consume-edit-list-grid .owned-toolbar button:nth-of-type(2)', str(downloaded_template))
                ab('wait', '--text', '耗材导入模板已下载')
                template_headers = ab('eval', "fetch('/core/module/item/files/emcConsumeExportTemplate/action/download-template/emcConsumeExportTemplate').then(async r => { const b = new Uint8Array(await r.arrayBuffer()); return JSON.stringify({status:r.status,type:r.headers.get('content-type'),disposition:r.headers.get('content-disposition'),bytes:b.length,magic:Array.from(b.slice(0,8))}); })")
                check('template browser response has xls bytes type and filename', all(value in template_headers for value in ('status', '200', 'application/vnd.ms-excel', 'emcConsumeExportTemplate.xls', '208,207,17,224,161,177,26,225')))
                check('template browser download is saved as xls', downloaded_template.is_file() and downloaded_template.stat().st_size > 100)
                ab('upload', '#emc-consume-edit-list-grid input[type=file]', str(import_file))
                ab('wait', '--fn', "document.querySelector('#page-status')?.textContent.includes('耗材导入完成')")
                check('browser valid import reports success', '耗材导入完成（1 行）' in ab('get', 'text', '#page-status'))
                ab('wait', '--text', '共24条')
                ab('fill', '[aria-label="材料名称筛选"]', 'Browser Imported')
                ab('click', '#emc-consume-edit-list-grid .filter-controls button')
                ab('wait', '--text', '共1条')
                check('browser valid import creates visible row', 'Browser Imported' in ab('get', 'text', '#emc-consume-edit-list-grid'))
                ab('click', '#emc-consume-edit-list-grid .filter-controls button:last-child')
                ab('wait', '--text', '共24条')
                ab('upload', '#emc-consume-edit-list-grid input[type=file]', str(invalid_file))
                ab('wait', 1000)
                invalid_status = ab('get', 'text', '#page-status')
                check('browser invalid import fails without changing data', ('GIKAM.CONSUMABLES.' in invalid_status or '导入失败' in invalid_status) and '共24条' in ab('get', 'text', '#emc-consume-edit-list-grid') and 'Browser Invalid' not in ab('get', 'text', '#owned-workspace'))
                passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nDELETE FROM T_EMC_CSM_STOCK_LOG WHERE STOCKID IN (SELECT ID FROM T_EMC_CSM_STOCK WHERE MTLID IN (SELECT ID FROM T_EMC_CONSUME WHERE PRODUCTNO='P-BROWSER'));\nDELETE FROM T_EMC_CSM_STOCK WHERE MTLID IN (SELECT ID FROM T_EMC_CONSUME WHERE PRODUCTNO='P-BROWSER');\nDELETE FROM T_EMC_CONSUME WHERE PRODUCTNO='P-BROWSER';\nCOMMIT;"), 'browser import cleanup')
                ab('click', '#emc-consume-edit-list-grid .owned-toolbar button:first-child')
                ab('wait', '--text', '共23条')
                check('browser import cleanup restores fixture baseline', 'Browser Imported' not in ab('get', 'text', '#owned-workspace'))
                # Defer one real query until after logout. The request captures
                # the old UI generation, then receives a real server denial;
                # that late denial must not replace the confirmed logout text.
                ab('eval', "(() => { const original = window.fetch; const gate = new Promise(resolve => { window.__lateQueryRelease = resolve; }); window.fetch = (...args) => gate.then(() => original(...args)); window.__lateQueryResult = OwnedRequest('/secure/emc/module/item/consume/consumes/queries/raw',{method:'POST',headers:{'Content-Type':'application/json'},body:'{}'}).then(() => 200, error => error.status); window.fetch = original; return true; })()")
                ab('click', '#logout')
                ab('wait', '--text', '已退出登录。')
                delayed = ab('eval', "window.__lateQueryRelease(); window.__lateQueryResult")
                check('late real query denied after logout', '403' in delayed)
                check('obsolete denial preserves confirmed logout', '已退出登录。' in ab('get', 'text', '#login-error'))
                check('consumables logout clears display', 'Alpha Bottle' not in ab('get', 'text', 'body'))
                ab('fill', '#login-id', 'other')
                ab('fill', '#login-password', 'secret')
                ab('click', '#login-submit')
                ab('wait', '--fn', "document.body.textContent.includes('当前账号没有可用的查询入口') || document.body.textContent.includes('当前账号无耗材查询权限')")
                check('consumables low permission page denied', 'CSM-001' not in ab('get', 'text', 'body'))
                rejected = ab('eval', "fetch('/secure/emc/module/item/consume/consumes/queries/raw',{method:'POST',headers:{'Content-Type':'application/json'},body:'{}'}).then(r=>r.status)")
                check('consumables low permission API denied', '403' in rejected)
                ab('close')
                browser = None
                report['browser_session_closed'] = True
                stop.touch()
                process.wait(timeout=20)
                check('runtime closed cleanly', process.returncode == 0)
                process = None
                check('audit excludes credentials', 'secret' not in audit.read_text()
                      and password not in audit.read_text())
                report['browser_transcript'] = transcript
                report['browser_transcript_sha256'] = hashlib.sha256(''.join(transcript).encode()).hexdigest()
                report['status'] = 'executed-pass'
    except StockOnlyComplete:
        pass
    except Exception as failure:
        report['reason'] = str(failure) if isinstance(failure, RuntimeError) else type(failure).__name__
        if browser and 'browser_failure_diagnostic' not in report:
            browser_diagnostics()
    finally:
        cleanup_started = time.perf_counter()
        if browser:
            report['browser_transcript'] = transcript
            report['browser_transcript_sha256'] = hashlib.sha256(''.join(transcript).encode()).hexdigest()
            report['browser_diagnostic'] = run(['agent-browser', '--session', browser, 'snapshot']).stdout
            report['browser_errors'] = run(['agent-browser', '--session', browser, 'errors']).stdout
            report['browser_session_closed'] = run(['agent-browser', '--session', browser, 'close']).returncode == 0
        if process and process.poll() is None:
            process.terminate()
            try:
                process.wait(timeout=10)
            except subprocess.TimeoutExpired:
                process.kill()
                process.wait()
        cleanup = {}
        if redis_created:
            report['redis_cleanup'] = {
                'container_removed': run(['docker', 'rm', '--force', '--volumes', redis_name]).returncode == 0,
            }
            redis_created = False
        if created:
            cleanup['container_removed'] = run(['docker', 'rm', '--force', '--volumes', name]).returncode == 0
            cleanup['anonymous_volumes_removed'] = cleanup['container_removed']
        if network_created:
            cleanup['network_removed'] = run(['docker', 'network', 'rm', network]).returncode == 0
            cleanup['browser_session_closed'] = report.get('browser_session_closed', browser is None)
            cleanup['temporary_files_removed'] = False
            if args.distribution and 'audit' not in report:
                report['audit'] = {'events': [], 'expected': []}
            if args.distribution:
                report['cleanup'] = cleanup
                cleanup['temporary_files_removed'] = False
        report['cleanup'] = cleanup
        report['phase_timings_ms']['cleanup'] = round((time.perf_counter() - cleanup_started) * 1000, 3)
        report['finished_at'] = datetime.datetime.now(datetime.timezone.utc).isoformat()
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(json.dumps(report, ensure_ascii=False, indent=2) + '\n')
    if args.distribution:
        temporary_path = Path(temporary)
        if temporary_path.exists():
            shutil.rmtree(str(temporary_path), ignore_errors=True)
        report.setdefault('cleanup', {})['temporary_files_removed'] = not temporary_path.exists()
        report.setdefault('cleanup', {})['browser_session_closed'] = report.get('browser_session_closed', browser is None)
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(json.dumps(report, ensure_ascii=False, indent=2) + '\n')
    print('owned web browser: ' + report['status'] + ' (' + str(len(report['checks'])) + ' checks)')
    return 0 if report['status'] == 'executed-pass' else 1


if __name__ == '__main__':
    raise SystemExit(main())
