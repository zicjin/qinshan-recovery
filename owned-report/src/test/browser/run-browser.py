#!/usr/bin/env python3
"""Exercise shipped report page with RSA, synthetic Oracle and digest-addressed local replay."""
import argparse
import datetime
import hashlib
import json
import os
import re
from pathlib import Path
import subprocess
import sys
import tempfile
import time
import uuid

ROOT = Path(__file__).resolve().parents[4]
WEB = ROOT / 'owned-web'
REPORT = ROOT / 'owned-report'
sys.path.insert(0, str(ROOT / 'tools'))
from vertical_slices.matrix import VERIFIER_PATHS
IMAGE = 'ghcr.io/gvenzl/oracle-free@sha256:cd3dff6649f5d67ca3121f5e064b5a5680611e5d34f03442423110778c47dda4'
SOURCE_SCOPES = ('owned-framework/src', 'owned-personnel/src', 'owned-web/src',
                 'owned-report/src', 'scripts/build-owned-framework.sh',
                 'scripts/build-owned-personnel.sh', 'scripts/build-owned-report.sh',
                 'scripts/prepare-owned-public-deps.py', 'scripts/reject-supplier-runtime.sh',
                 'owned-web/build-owned-web.sh', 'owned-web/original-personnel-sha256.json')


def run(command, timeout=180, **kwargs):
    return subprocess.run([str(part) for part in command], capture_output=True,
                          text=True, timeout=timeout, **kwargs)


def passed(result, label):
    if result.returncode:
        codes = sorted(set(re.findall(r'ORA-\d{5}', result.stdout + result.stderr)))
        raise RuntimeError(label + ' failed: ' + ','.join(codes) + ' ' + result.stdout[-300:] + result.stderr[:700])
    return result.stdout


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def tracked_source_paths():
    result = run(['git', 'ls-files', '-z', '--', *SOURCE_SCOPES], timeout=30, cwd=ROOT)
    if result.returncode:
        raise RuntimeError('source scope lookup failed')
    values = [item for item in result.stdout.split('\0') if item]
    return [ROOT / value for value in values]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--output', type=Path,
                        default=REPORT / 'evidence/browser-report-20261020.json')
    args = parser.parse_args()
    report = {'schema': 1, 'status': 'executed-fail', 'production_access': False,
              'scope': 'packaged report page, preview and HTML export from local synthetic replay; synthetic Oracle only; third-party renderer and PDF/Excel export disabled',
              'fixture': 'report-replay-fixture', 'role': 'manager', 'organization': 'org-a',
              'mode': 'isolated', 'replay_mode': 'replay',
              'route': 'GET /secure/core/module/sys/report-configs/lines/7001/export',
              'audit_expectation': 'allowed report-export for e-1/7001; denied scope and line access remain report-read audit',
              'started_at': datetime.datetime.now(datetime.timezone.utc).isoformat(),
              'checks': []}
    name = 'lims-owned-report-' + uuid.uuid4().hex
    network = name + '-net'
    created = network_created = False
    process = browser = None

    def check(label, condition):
        if not condition:
            raise RuntimeError(label)
        report['checks'].append(label)

    def sql(source):
        text = ('whenever sqlerror exit failure rollback\n'
                'set pagesize 0 feedback off heading off echo off verify off define off\n'
                'alter session set container=FREEPDB1;\n' + source + '\nexit\n')
        return run(['docker', 'exec', '-i', name, 'sqlplus', '-s', '/ as sysdba'],
                   input=text, timeout=60)

    try:
        sources = sorted(tracked_source_paths())
        report['source_commit'] = passed(run(['git', 'rev-parse', 'HEAD'], cwd=ROOT), 'git').strip()
        report['source_dirty'] = bool(passed(run(['git', 'status', '--porcelain', '--'] + sources,
                                                 cwd=ROOT), 'source status').strip())
        report['runner'] = str(Path(__file__).relative_to(ROOT))
        relative_sources = [path.relative_to(ROOT).as_posix() for path in sources]
        report['source_sha256'] = {path: digest(ROOT / path) for path in relative_sources}
        report['source_coverage'] = {
            'roots': list(SOURCE_SCOPES),
            'files': dict(report['source_sha256']),
            'diff': {'added': [], 'deleted': []},
        }
        verifier_paths = [ROOT / path for path in VERIFIER_PATHS]
        report['verifier_sha256'] = {str(path.relative_to(ROOT)): digest(path) for path in verifier_paths}
        passed(run([ROOT / 'scripts/build-owned-report.sh'], timeout=240), 'report build')
        passed(run([WEB / 'build-owned-web.sh']), 'web build')
        for relative, expected in json.loads((WEB / 'original-personnel-sha256.json').read_text())['files'].items():
            check('unchanged business resource: ' + relative,
                  digest(WEB / 'src/main/resources' / relative) == expected)
        jars = [ROOT / 'owned-framework/target/lims-owned-framework-1.0.0.jar',
                ROOT / 'owned-personnel/target/lims-owned-personnel-1.0.0.jar',
                WEB / 'target/lims-owned-web-1.0.0.jar', REPORT / 'target/lims-owned-report-1.0.0.jar']
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
                '--restart', 'no', '--memory', '3g', '--cpus', '2', '--env-file', env, IMAGE]), 'container').strip()
            created = True
            passed(run(['docker', 'start', name]), 'container start')
            deadline = time.monotonic() + 600
            while time.monotonic() < deadline:
                ready = sql("SELECT 'BROWSER_READY' FROM V$PDBS WHERE NAME='FREEPDB1' AND OPEN_MODE='READ WRITE';")
                logs = run(['docker', 'logs', name])
                if ready.returncode == 0 and 'BROWSER_READY' in ready.stdout and 'DATABASE IS READY TO USE!' in logs.stdout + logs.stderr:
                    break
                time.sleep(2)
            else:
                raise RuntimeError('Oracle readiness timeout')
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
            password = 'Fixture' + uuid.uuid4().hex[:16]
            passed(sql('CREATE USER query_fixture IDENTIFIED BY ' + password + ';\n'
                       'GRANT CREATE SESSION, CREATE TABLE, UNLIMITED TABLESPACE TO query_fixture;'), 'schema')
            properties = temporary / 'jdbc.properties'
            properties.write_text('url=jdbc:oracle:thin:@//' +
                container['NetworkSettings']['Networks'][network]['IPAddress'] +
                ':1521/FREEPDB1\nuser=query_fixture\npassword=' + password + '\n')
            properties.chmod(0o600)
            time.sleep(10)
            seed_classpath = ':'.join(map(str, [ROOT / 'owned-framework/target/classes',
                ROOT / 'owned-framework/target/test-classes', dependencies / '*']))
            seed = passed(run([jdk / 'bin/java', '-cp', seed_classpath,
                'com.sunwayworld.framework.PersonnelQueryServiceContractTest', properties]), 'service baseline')
            check('15 synthetic service checks', 'PASS (15 checks;' in seed)
            passed(sql("CREATE TABLE query_fixture.T_CORE_REPORT_CONFIG_LINE (ID NUMBER PRIMARY KEY,REPORTCONFIGID VARCHAR2(64),REPORTNAME VARCHAR2(100),REPORTCATEGORY VARCHAR2(30),REPORTPATH VARCHAR2(200),PARAM VARCHAR2(200),ACTIVATEDFLAG VARCHAR2(1));\n"
                "INSERT INTO query_fixture.T_CORE_REPORT_CONFIG_LINE VALUES (7001,'homePageReport','合成组织报表','reportingService','/synthetic/organization&rs:Command=Render','ORGID=ORGID&REPORTDATE=REPORTDATE','1');\n"
                "INSERT INTO query_fixture.T_CORE_REPORT_CONFIG_LINE VALUES (7002,'homePageReport','Other role report','reportingService','/synthetic/other&rs:Command=Render','ORGID=ORGID&REPORTDATE=REPORTDATE','1');\n"
                "INSERT INTO query_fixture.T_CORE_REPORT_CONFIG_LINE VALUES (7003,'homePageReport','Inactive report','reportingService','/synthetic/inactive&rs:Command=Render','ORGID=ORGID&REPORTDATE=REPORTDATE','0');\n"
                "INSERT INTO query_fixture.T_CORE_ROLE_PERMISSION VALUES (1,'T_CORE_REPORT_CONFIG_LINE','7001');\n"
                "INSERT INTO query_fixture.T_CORE_ROLE_PERMISSION VALUES (1,'T_CORE_REPORT_CONFIG_LINE','7003');"), 'report fixture')
            report['report_seed_check'] = passed(sql("SELECT 'REPORT='||COUNT(*) FROM query_fixture.T_CORE_REPORT_CONFIG_LINE UNION ALL SELECT 'GRANT='||COUNT(*) FROM query_fixture.T_CORE_ROLE_PERMISSION WHERE PERMISSIONTYPEID='T_CORE_REPORT_CONFIG_LINE' UNION ALL SELECT 'ROLEUSER='||COUNT(*) FROM query_fixture.T_CORE_ROLE_USER WHERE USERID='e-1' UNION ALL SELECT 'JOIN='||COUNT(*) FROM query_fixture.T_CORE_ROLE_USER RU JOIN query_fixture.T_CORE_ROLE R ON R.ID=RU.ROLEID JOIN query_fixture.T_CORE_ROLE_PERMISSION RP ON RP.ROLEID=R.ID WHERE RU.USERID='e-1' AND R.LASTSUSPENDEDFLAG='0' AND RP.PERMISSIONTYPEID='T_CORE_REPORT_CONFIG_LINE' AND RP.TARGETID='7001';"), 'report fixture check')
            replay = temporary / 'replay'
            replay.mkdir()
            report['replay_sha256'] = {}
            for date, status, body in [('2026-10-20',200,'<h1>Synthetic report: org-a / 2026-10-20 / total 2</h1>'),('2026-10-21',503,'Synthetic service unavailable')]:
                request = 'GET /synthetic/organization&rs:Command=Render&ORGID=org-a&REPORTDATE=' + date
                replay_file = replay / (hashlib.sha256(request.encode()).hexdigest() + '.json')
                replay_file.write_text(json.dumps({'source':'synthetic-isolated-replay','request':request,'status':status,'body':body}))
                report['replay_sha256'][replay_file.name] = digest(replay_file)
            classes = temporary / 'classes'
            classes.mkdir()
            runtime = ':'.join(map(str, jars + [dependencies / '*']))
            passed(run([jdk / 'bin/javac', '-source', '8', '-target', '8', '-cp', runtime,
                '-d', classes, REPORT / 'src/test/java/OwnedReportBrowserApplication.java']), 'lifecycle compile')
            portfile, stop, audit = [temporary / part for part in ('port', 'stop', 'audit.jsonl')]
            with (temporary / 'application.log').open('w') as log:
                process = subprocess.Popen([str(jdk / 'bin/java'), '-cp', str(classes) + ':' + runtime,
                    'OwnedReportBrowserApplication', str(properties), str(portfile), str(stop), str(audit), str(replay), 'replay'],
                    stdout=log, stderr=subprocess.STDOUT, cwd=temporary)
                deadline = time.monotonic() + 60
                while not portfile.exists() and process.poll() is None and time.monotonic() < deadline:
                    time.sleep(.2)
                if not portfile.exists() or process.poll() is not None:
                    log.flush()
                    report['application_log_tail'] = (temporary / 'application.log').read_text(errors='replace')[-6000:].replace(password, '<redacted>')
                check('packaged original application ready', portfile.exists() and process.poll() is None)
                url = 'http://127.0.0.1:' + portfile.read_text() + '/'
                browser = 'owned-web-' + uuid.uuid4().hex
                transcript = []

                def ab(*command):
                    result = run(['agent-browser', '--session', browser, '--allowed-domains', '127.0.0.1', *command], timeout=40)
                    # Record response-only evidence; fill arguments, cookies and keys are omitted.
                    transcript.append(command[0] + '\n' + result.stdout)
                    return passed(result, 'browser ' + command[0])

                def fetch_status(path):
                    return ab('eval', "fetch(" + json.dumps(path) + ").then(r=>r.status)")

                def wait_fn(expression, timeout_seconds=60):
                    deadline = time.monotonic() + timeout_seconds
                    while time.monotonic() < deadline:
                        value = ab('eval', expression).strip().lower()
                        if value == 'true':
                            return value
                        time.sleep(0.5)
                    body = ab('get', 'text', 'body').strip().replace('\n', ' ')[-500:]
                    raise RuntimeError('browser condition timed out: ' + expression + ' body=' + body)

                ab('open', url)
                ab('wait', '#login-submit')
                ab('fill', '#login-id', 'e-1')
                ab('fill', '#login-password', 'secret')
                ab('click', '#login-submit')
                ab('wait', '#logout')
                check('RSA session login', True)
                page_url = url + 'secure/core/module/sys/report-configs/page/common-report?lineId=7001'
                ab('open', page_url)
                report['report_page_diagnostic'] = ab('get', 'text', 'body')
                ab('wait', '#report-submit')
                check('authorized real report page opened', '合成组织报表' in ab('get', 'text', 'body'))
                check('parameters start blank', ab('get', 'value', 'input[name="ORGID"]').strip() in ('', '""'))
                ab('fill', 'input[name="ORGID"]', 'org-a')
                ab('fill', 'input[name="REPORTDATE"]', '2026-10-20')
                ab('click', '#report-submit')
                ab('wait', '--fn', "document.getElementById('report-result').dataset.status==='success'")
                body = ab('get', 'text', 'body')
                wait_fn("document.querySelector('#report-preview').contentDocument?.body?.innerText.includes('Synthetic report:')")
                rendered = ab('eval', "document.querySelector('#report-preview').contentDocument.body.innerText")
                check('replay result rendered in sandboxed preview', '已生成' in body and 'Synthetic report: org-a / 2026-10-20 / total 2' in rendered)
                check('HTML export control is visible after successful preview', ab('eval', "!document.getElementById('report-export').hidden && document.getElementById('report-export').href.endsWith('format=html')").strip() == 'true')
                export_probe = ab('eval', "fetch('/secure/core/module/sys/report-configs/lines/7001/export?ORGID=org-a&REPORTDATE=2026-10-20&format=html').then(async r=>JSON.stringify({status:r.status,type:r.headers.get('content-type'),disposition:r.headers.get('content-disposition'),source:r.headers.get('x-owned-report-source'),cache:r.headers.get('cache-control'),csp:r.headers.get('content-security-policy'),hash:r.headers.get('x-owned-report-request-hash'),body:await r.text()}))")
                export_value = json.loads(json.loads(export_probe))
                report['http_cases'] = [{
                    'route': 'GET /secure/core/module/sys/report-configs/lines/7001/export',
                    'role': 'manager', 'organization': 'org-a', 'mode': 'isolated',
                    'status': int(export_value['status']), 'expected_status': 200,
                }]
                check('isolated HTML export downloads the replay body', export_value['status'] == 200
                      and 'text/html' in export_value['type']
                      and export_value['disposition'] == 'attachment; filename="report-7001.html"'
                      and 'synthetic-isolated-replay' in export_probe
                      and 'Synthetic report: org-a / 2026-10-20 / total 2' in export_probe)
                check('HTML export is non-cacheable and restricts active content', export_value['cache'] == 'no-store'
                      and export_value['csp'] == "default-src 'none'; sandbox"
                      and 'Content-Security-Policy' in export_value['body']
                      and "form-action 'none'" in export_value['body'])
                check('HTML export matches digest-addressed replay request', export_value['hash'] == '7ccf0362ecac15620166dc7e33c760f0ce2202b12fdaf7bf182043b685bd816b')
                check('third-party PDF export remains explicitly disabled', '501' in fetch_status('/secure/core/module/sys/report-configs/lines/7001/export?ORGID=org-a&REPORTDATE=2026-10-20&format=pdf'))
                check('HTML export duplicate format rejected', '400' in fetch_status('/secure/core/module/sys/report-configs/lines/7001/export?ORGID=org-a&REPORTDATE=2026-10-20&format=html&format=pdf'))
                check('HTML export unknown parameter rejected', '400' in fetch_status('/secure/core/module/sys/report-configs/lines/7001/export?ORGID=org-a&REPORTDATE=2026-10-20&format=html&roleId=1'))
                check('HTML export other organization denied', '403' in fetch_status('/secure/core/module/sys/report-configs/lines/7001/export?ORGID=org-b&REPORTDATE=2026-10-20&format=html'))
                check('HTML export other report line denied', '403' in fetch_status('/secure/core/module/sys/report-configs/lines/7002/export?ORGID=org-a&REPORTDATE=2026-10-20&format=html'))
                check('HTML export upstream failure has no attachment', '502' in fetch_status('/secure/core/module/sys/report-configs/lines/7001/export?ORGID=org-a&REPORTDATE=2026-10-21&format=html'))
                ab('fill', 'input[name="REPORTDATE"]', '2026-10-21')
                check('editing report parameters hides the stale export control', ab('eval', "document.getElementById('report-export').hidden && !document.getElementById('report-export').hasAttribute('href')").strip() == 'true')
                ab('click', '#report-submit')
                ab('wait', '--text', 'REPORT_REPLAY_UPSTREAM_503')
                check('upstream failure visible and old preview cleared', 'Synthetic report:' not in ab('get', 'text', 'body'))
                ab('fill', 'input[name="REPORTDATE"]', 'not-a-date')
                ab('click', '#report-submit')
                ab('wait', '--text', 'REPORT_INVALID_REQUEST')
                check('invalid parameter visible', True)
                ab('fill', 'input[name="REPORTDATE"]', '2026-10-20')
                ab('fill', 'input[name="ORGID"]', 'org-b')
                ab('click', '#report-submit')
                ab('wait', '--text', 'REPORT_UNAUTHORIZED')
                check('ungranted organization denied', True)
                check('duplicate parameters rejected', '400' in fetch_status('/secure/core/module/sys/report-configs/lines/7001/result?ORGID=org-a&ORGID=org-b&REPORTDATE=2026-10-20'))
                check('unknown parameter rejected', '400' in fetch_status('/secure/core/module/sys/report-configs/lines/7001/result?ORGID=org-a&REPORTDATE=2026-10-20&roleId=1'))
                check('other line permission denied', '403' in fetch_status('/secure/core/module/sys/report-configs/lines/7002'))
                check('inactive report denied', '403' in fetch_status('/secure/core/module/sys/report-configs/lines/7003'))
                screenshot = temporary / 'report.png'
                ab('screenshot', str(screenshot))
                report['screenshot_sha256'] = digest(screenshot)
                passed(sql("DELETE FROM query_fixture.T_CORE_ROLE_PERMISSION WHERE TARGETID='7001';"), 'revoke report grant')
                check('report grant revocation rechecked', '403' in fetch_status('/secure/core/module/sys/report-configs/lines/7001/result?ORGID=org-a&REPORTDATE=2026-10-20'))
                check('HTML export grant revocation rechecked', '403' in fetch_status('/secure/core/module/sys/report-configs/lines/7001/export?ORGID=org-a&REPORTDATE=2026-10-20&format=html'))
                ab('eval', "fetch('/core/module/sys/logout',{method:'POST'}).then(r=>r.status)")
                check('logout invalidates report session', '403' in fetch_status('/secure/core/module/sys/report-configs/lines/7001'))
                check('logout invalidates HTML export session', '403' in fetch_status('/secure/core/module/sys/report-configs/lines/7001/export?ORGID=org-a&REPORTDATE=2026-10-20&format=html'))
                ab('open', url)
                ab('wait', '#login-submit')
                ab('fill', '#login-id', 'other')
                ab('fill', '#login-password', 'secret')
                ab('click', '#login-submit')
                wait_fn("document.body.innerText.includes('当前账号无人员查询权限') || document.body.innerText.includes('当前账号菜单暂不可用') || document.body.innerText.includes('查询入口已关闭')")
                check('low role report denied', '403' in fetch_status('/secure/core/module/sys/report-configs/page/common-report?lineId=7001'))
                check('low role HTML export denied', '403' in fetch_status('/secure/core/module/sys/report-configs/lines/7001/export?ORGID=org-a&REPORTDATE=2026-10-20&format=html'))
                ab('close')
                browser = None
                report['browser_session_closed'] = True
                stop.touch()
                process.wait(timeout=20)
                check('runtime closed cleanly', process.returncode == 0)
                process = None
                check('audit excludes credentials', 'secret' not in audit.read_text()
                      and password not in audit.read_text())
                audit_events = [json.loads(line) for line in audit.read_text().splitlines() if line.strip()]
                check('successful HTML export is audited once for the granted report', len([event for event in audit_events if event.get('action') == 'report-export' and event.get('actorId') == 'e-1' and event.get('resource') == '7001']) == 1)
                report['audit'] = {
                    'events': [event for event in audit_events if event.get('action') == 'report-export'],
                    'expected': [{'action': 'report-export', 'actorId': 'e-1', 'resource': '7001', 'outcome': 'ALLOWED'}],
                }
                report['browser_transcript_sha256'] = hashlib.sha256(''.join(transcript).encode()).hexdigest()
                report['status'] = 'executed-pass'
    except Exception as failure:
        report['reason'] = str(failure) if isinstance(failure, RuntimeError) else type(failure).__name__
        if 'temporary' in locals():
            logs = temporary / 'application.log'
            if logs.exists():
                report['application_log_tail'] = logs.read_text(errors='replace')[-4000:]
    finally:
        if browser:
            report['browser_session_closed'] = run(['agent-browser', '--session', browser, 'close']).returncode == 0
        if process and process.poll() is None:
            process.terminate()
            try:
                process.wait(timeout=10)
            except subprocess.TimeoutExpired:
                process.kill()
                process.wait()
        cleanup = {}
        cleanup['browser_session_closed'] = report.get('browser_session_closed', False)
        cleanup['temporary_files_removed'] = not temporary.exists() if 'temporary' in locals() else True
        if created:
            cleanup['container_removed'] = run(['docker', 'rm', '--force', '--volumes', name]).returncode == 0
            cleanup['anonymous_volumes_removed'] = cleanup['container_removed']
        if network_created:
            cleanup['network_removed'] = run(['docker', 'network', 'rm', network]).returncode == 0
        report['cleanup'] = cleanup
        report['finished_at'] = datetime.datetime.now(datetime.timezone.utc).isoformat()
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(json.dumps(report, ensure_ascii=False, indent=2) + '\n')
    print('owned report browser: ' + report['status'] + ' (' + str(len(report['checks'])) + ' checks)')
    return 0 if report['status'] == 'executed-pass' else 1


if __name__ == '__main__':
    raise SystemExit(main())
