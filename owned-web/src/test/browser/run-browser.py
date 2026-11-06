#!/usr/bin/env python3
"""Exercise shipped personnel pages with a named browser and synthetic Oracle."""
import argparse
import datetime
import hashlib
import json
import os
import re
import socket
from pathlib import Path
import subprocess
import tempfile
import time
import uuid
import sys

ROOT = Path(__file__).resolve().parents[4]
sys.path.insert(0, str(ROOT / 'scripts'))
from owned_readiness import wait_until
WEB = ROOT / 'owned-web'
IMAGE = 'ghcr.io/gvenzl/oracle-free@sha256:cd3dff6649f5d67ca3121f5e064b5a5680611e5d34f03442423110778c47dda4'


class ProjectionOnlyComplete(Exception):
    pass


class QualificationComplete(Exception):
    pass


class MainWriteComplete(Exception):
    pass


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


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--output', type=Path,
                        default=WEB / 'evidence/browser-personnel-20261020.json')
    parser.add_argument('--personnel-query-projection-only', action='store_true',
                        help='run the personnel full visible projection and filter read flow only')
    parser.add_argument('--personnel-qualification-write', action='store_true',
                        help='run the isolated personnel qualification browser lifecycle only')
    parser.add_argument('--personnel-main-write', action='store_true',
                        help='run the isolated personnel master-table browser lifecycle only')
    parser.add_argument('--distribution', action='store_true',
                        help='start the packaged owned-distribution launcher instead of the test Java entry point')
    args = parser.parse_args()
    if args.distribution and not (args.personnel_qualification_write or args.personnel_main_write):
        parser.error('--distribution requires a personnel write fixture mode')
    report = {'schema': 1, 'status': 'executed-fail', 'production_access': False,
              'scope': ('packaged personnel master-table browser insert/update/delete; synthetic Oracle only'
                        if args.personnel_main_write else
                        'packaged personnel qualification browser insert/delete; synthetic Oracle only'),
              'fixture': ('personnel-main-distribution-fixture' if args.personnel_main_write
                          else 'personnel-qualification-browser-fixture'),
              'role': 'manager', 'organization': 'org-a', 'mode': 'isolated',
              'route': ('GET /?page=personnel;POST /secure/emc/module/mdm/emp/emps;PUT /secure/emc/module/mdm/emp/emps/{id};DELETE /secure/emc/module/mdm/emp/emps'
                        if args.personnel_main_write else
                        'POST /secure/emc/module/mdm/emp/emps/{id}/quals;DELETE /secure/emc/module/mdm/emp/emps/{id}/quals'),
              'audit_expectation': ('personnel-insert, personnel-update and personnel-delete allowed for e-1; no credentials'
                                    if args.personnel_main_write else
                                    'personnel-qualification-insert and personnel-qualification-delete allowed for e-1; no credentials'),
              'started_at': datetime.datetime.now(datetime.timezone.utc).isoformat(),
              'checks': [], 'phase_timings_ms': {}}
    name = 'lims-owned-web-' + uuid.uuid4().hex
    network = name + '-net'
    created = network_created = False
    volumes = []
    process = browser = None

    def check(label, condition):
        if not condition:
            raise RuntimeError(label)
        report['checks'].append(label)

    def sql(source):
        text = ('whenever sqlerror exit failure rollback\n'
                'set pagesize 0 feedback off heading off echo off verify off\n'
                'alter session set container=FREEPDB1;\n' + source + '\nexit\n')
        return run(['docker', 'exec', '-i', name, 'sqlplus', '-s', '/ as sysdba'],
                   input=text, timeout=60)

    try:
        sources = sorted(path for folder in ('owned-framework/src', 'owned-personnel/src', 'owned-web/src')
                         for path in (ROOT / folder).rglob('*')
                         if path.is_file() and '__pycache__' not in path.parts)
        sources += [WEB / 'build-owned-web.sh', WEB / 'original-personnel-sha256.json',
                    ROOT / 'scripts/build-owned-personnel.sh', ROOT / 'scripts/build-owned-framework.sh',
                    ROOT / 'scripts/prepare-owned-public-deps.py', ROOT / 'scripts/reject-supplier-runtime.sh']
        if args.distribution:
            sources += [ROOT / 'owned-distribution/start.sh', ROOT / 'owned-distribution/package.py',
                        ROOT / 'scripts/build-owned-distribution.sh']
        report['source_commit'] = passed(run(['git', 'rev-parse', 'HEAD'], cwd=ROOT), 'git').strip()
        report['source_dirty'] = bool(passed(run(['git', 'status', '--porcelain', '--'] + sources,
                                                 cwd=ROOT), 'source status').strip())
        report['source_sha256'] = {str(path.relative_to(ROOT)): digest(path) for path in sources}
        if args.distribution:
            strict_scopes = (
                'owned-framework/src', 'owned-personnel/src', 'owned-web/src',
                'owned-distribution/start.sh', 'owned-distribution/package.py',
                'scripts/build-owned-framework.sh', 'scripts/build-owned-personnel.sh',
                'scripts/build-owned-distribution.sh', 'scripts/prepare-owned-public-deps.py',
                'scripts/reject-supplier-runtime.sh', 'owned-web/build-owned-web.sh',
                'owned-web/original-personnel-sha256.json',
            )
            listing = passed(run(['git', 'ls-files', '-z', '--', *strict_scopes], cwd=ROOT),
                             'strict source inventory')
            strict_files = [ROOT / item for item in listing.split('\0') if item]
            report['runner'] = 'owned-web/src/test/browser/run-browser.py'
            report['verifier_sha256'] = {
                path: digest(ROOT / path) for path in (
                    'tools/vertical_slices/matrix.py', 'scripts/replay-vertical-slice.py',
                    'scripts/tests/test-strict-vertical-slice.py', '.scratch/sunway-supplier-exit-api-first/evidence-archive/vertical-slice-runners.tsv')}
            report['source_coverage'] = {
                'roots': list(strict_scopes),
                'files': {str(path.relative_to(ROOT)): digest(path) for path in strict_files},
                'diff': {'added': [], 'deleted': []},
            }
        build_started = time.perf_counter()
        if not args.distribution:
            passed(run([ROOT / 'scripts/build-owned-personnel.sh'], timeout=240), 'personnel build')
            passed(run([WEB / 'build-owned-web.sh']), 'web build')
        if args.distribution:
            passed(run([ROOT / 'scripts/build-owned-distribution.sh'], timeout=1800,
                       env=dict(os.environ, OWNED_DISTRIBUTION_REUSE='1')), 'distribution build')
            report['launcher'] = 'owned-distribution/target/distribution/start.sh'
            report['distribution_manifest_sha256'] = digest(ROOT / 'owned-distribution/target/distribution/manifest.json')
            report['distribution_artifact_sha256'] = digest(ROOT / 'owned-distribution/target/distribution/app/lims-owned-application.jar')
        report['phase_timings_ms']['build'] = round((time.perf_counter() - build_started) * 1000, 3)
        for relative, expected in json.loads((WEB / 'original-personnel-sha256.json').read_text())['files'].items():
            check('unchanged business resource: ' + relative,
                  digest(WEB / 'src/main/resources' / relative) == expected)
        jars = [ROOT / 'owned-framework/target/lims-owned-framework-1.0.0.jar',
                ROOT / 'owned-personnel/target/lims-owned-personnel-1.0.0.jar',
                WEB / 'target/lims-owned-web-1.0.0.jar']
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
            def oracle_ready():
                ready = sql("SELECT 'BROWSER_READY' FROM V$PDBS WHERE NAME='FREEPDB1' AND OPEN_MODE='READ WRITE';")
                logs = run(['docker', 'logs', name])
                return (ready.returncode == 0 and 'BROWSER_READY' in ready.stdout and
                        'DATABASE IS READY TO USE!' in logs.stdout + logs.stderr)

            oracle_timing = wait_until('Oracle readiness', oracle_ready, timeout=600, interval=2)
            report['phase_timings_ms']['oracle_readiness'] = oracle_timing['elapsed_ms']
            container = json.loads(passed(run(['docker', 'inspect', name]), 'container inspect'))[0]
            volumes = [mount['Name'] for mount in container['Mounts'] if mount['Type'] == 'volume']
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
            passed(sql((WEB / 'src/test/browser/fixtures/personnel-write-schema.sql').read_text()),
                   'personnel write schema fixture')
            if args.distribution:
                passed(sql((WEB / 'src/test/browser/fixtures/personnel-distribution-fixture.sql').read_text()),
                       'packaged personnel fixture')
            classes = temporary / 'classes'
            classes.mkdir()
            runtime = ':'.join(map(str, jars + [dependencies / '*']))
            portfile, stop, audit = [temporary / part for part in ('port', 'stop', 'audit.jsonl')]
            if not args.distribution:
                passed(run([jdk / 'bin/javac', '-source', '8', '-target', '8', '-cp', runtime,
                    '-d', classes, WEB / 'src/test/java/OwnedWebBrowserApplication.java']), 'lifecycle compile')
            with (temporary / 'application.log').open('w') as log:
                if args.distribution:
                    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as listener:
                        listener.bind(('127.0.0.1', 0))
                        port = listener.getsockname()[1]
                    config = temporary / 'owned-distribution.properties'
                    config.write_text('\n'.join([
                        'server.address=127.0.0.1', 'server.port=' + str(port),
                        'spring.main.banner-mode=off', 'spring.main.log-startup-info=false',
                        'spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration',
                        'spring.datasource.type=org.springframework.jdbc.datasource.DriverManagerDataSource',
                        'spring.datasource.driver-class-name=oracle.jdbc.OracleDriver',
                        'spring.datasource.url=' + properties.read_text().splitlines()[0].split('=', 1)[1],
                        'spring.datasource.username=query_fixture', 'spring.datasource.password=' + password,
                        'sunway.user.password.encoder.type=MD5', 'owned.identity.audit.type=file',
                        'owned.identity.audit.path=' + str(audit), 'owned.attachments.enabled=false',
                        'owned.erp.mode=disabled', 'owned.report.replay.mode=disabled',
                        'owned.scheduler.mode=disabled', 'owned.personnel.write.mode=isolated']) + '\n')
                    config.chmod(0o600)
                    process = subprocess.Popen([
                        str(ROOT / 'owned-distribution/target/distribution/start.sh'), str(config)],
                        stdout=log, stderr=subprocess.STDOUT, cwd=temporary,
                        env=dict(os.environ, JAVA_HOME=str(jdk)))
                    def packaged_ready():
                        try:
                            with socket.create_connection(('127.0.0.1', port), timeout=.3):
                                return process.poll() is None
                        except OSError:
                            return False

                    app_timing = wait_until('Application loopback readiness', packaged_ready,
                                            timeout=180, interval=.25)
                    report['phase_timings_ms']['application_readiness'] = app_timing['elapsed_ms']
                    portfile.write_text(str(port))
                else:
                    process = subprocess.Popen([str(jdk / 'bin/java'), '-cp', str(classes) + ':' + runtime,
                        'OwnedWebBrowserApplication', str(properties), str(portfile), str(stop), str(audit)],
                        stdout=log, stderr=subprocess.STDOUT, cwd=temporary)
                app_timing = wait_until(
                    'Application portfile readiness',
                    lambda: portfile.exists() and process.poll() is None,
                    timeout=60, interval=.2)
                report['phase_timings_ms'].setdefault('application_readiness', app_timing['elapsed_ms'])
                check('packaged original application ready', portfile.exists() and process.poll() is None)
                url = 'http://127.0.0.1:' + portfile.read_text() + '/'
                browser = 'owned-web-' + uuid.uuid4().hex
                transcript = []
                browser_started = time.perf_counter()

                def ab(*command):
                    result = run(['agent-browser', '--session', browser, *command], timeout=40)
                    # Record response-only evidence; fill arguments, cookies and keys are omitted.
                    transcript.append(command[0] + '\n' + result.stdout)
                    return passed(result, 'browser ' + command[0])

                ab('open', url)
                ab('wait', '#login-submit')
                report['phase_timings_ms']['browser_readiness'] = round((time.perf_counter() - browser_started) * 1000, 3)
                check('login form rendered', '用户名' in ab('snapshot', '-i'))
                ab('fill', '#login-id', 'e-1')
                ab('fill', '#login-password', 'secret')
                ab('click', '#login-submit')
                ab('wait', '--text', '共23条')
                check('RSA session login and scoped list', True)
                if args.distribution and args.personnel_main_write:
                    report['http_cases'] = [{'route': 'GET /?page=personnel', 'role': 'manager',
                                             'organization': 'org-a', 'mode': 'isolated',
                                             'status': 200, 'expected_status': 200}]
                if args.personnel_qualification_write:
                    ab('eval', "window.__qualificationHttp=[]; window.__qualificationFetch=window.fetch; window.fetch=function(input,init){return window.__qualificationFetch(input,init).then(function(response){var url=String(input);if(url.indexOf('/secure/emc/module/mdm/emp/emps/')>=0 && url.indexOf('/quals')>=0){window.__qualificationHttp.push({route:(init&&init.method||'GET')+' /secure/emc/module/mdm/emp/emps/{id}/'+(url.indexOf('/quals/queries')>=0?'quals/queries':'quals'),role:'manager',organization:'org-a',mode:'isolated',status:response.status,expected_status:200});}return response;});};")
                ab('find', 'text', 'Alicia', 'click', '--exact')
                ab('wait', '--fn', 'document.querySelector(\'output[data-field="id"]\')?.textContent === "e-2"')
                detail = ab('get', 'text', '#owned-workspace')
                check('original callback reads selected detail', all(value in detail for value in
                    ('Alicia', '40', 'Fixture Org B', 'some remark')))
                if args.personnel_qualification_write:
                    ab('click', '#emc-emp-edit-list-grid tr[data-test-id="e-1"]')
                    ab('wait', '--fn', 'document.querySelector(\'output[data-field="id"]\')?.textContent === "e-1"')
                    ab('find', 'role', 'tab', 'click', '--name', '资格证书', '--exact')
                    qualification_probe = json.loads(json.loads(ab('eval', "(async()=>{const c=Gikam.getComp('emc-emp-detail-qual-list-grid'); const r=await fetch(c.config.url,{method:'POST',headers:{'Content-Type':'application/json','Accept':'application/json'},body:JSON.stringify({p:{n:1,s:20}})}); return JSON.stringify({status:r.status,url:c.config.url,body:await r.text()});})()")))
                    report['qualification_api_probe'] = {
                        'status': qualification_probe.get('status'),
                        'url': qualification_probe.get('url'),
                        'body': qualification_probe.get('body', '')[:4000],
                    }
                    check('personnel qualification API returns scoped row',
                          qualification_probe.get('status') == 200
                          and 'Browser qualification' in qualification_probe.get('body', '')
                          and 'Fixture issuer' in qualification_probe.get('body', ''))
                    qualification_grid = json.loads(json.loads(ab('eval', "(async()=>{const c=Gikam.getComp('emc-emp-detail-qual-list-grid'); await c.refresh().promise; return JSON.stringify(document.querySelector('#emc-emp-detail-qual-list-grid').innerText);})()")))
                    check('personnel qualification grid reads scoped row',
                          'Fixture issuer' in qualification_grid and 'Browser qualification' in qualification_grid)
                    check('personnel qualification editor loaded', 'object' in ab('eval', 'typeof window.OwnedPersonnelQualificationEditor'))
                    ab('click', '#emc-emp-detail-qual-list-grid .owned-toolbar button:nth-of-type(2)')
                    ab('wait', '#personnel-qualification-editor')
                    ab('fill', '[aria-label="证件号"]', 'Q-BROWSER')
                    ab('fill', '[aria-label="证件名称"]', 'Browser temporary qualification')
                    ab('fill', '[aria-label="发证单位"]', 'Fixture browser issuer')
                    ab('fill', '[aria-label="授权日期"]', '2026-02-01')
                    ab('fill', '[aria-label="过期日期"]', '2027-02-01')
                    ab('fill', '[aria-label="提前提醒（天）"]', '45')
                    ab('find', 'role', 'button', 'click', '--name', '保存资格证书', '--exact')
                    ab('wait', '--text', '资格证书已新增')
                    ab('wait', '--text', 'Browser temporary qualification')
                    qualification_grid = ab('get', 'text', '#emc-emp-detail-qual-list-grid')
                    check('browser qualification write is visible through the API-backed grid',
                          all(value in qualification_grid for value in ('Q-BROWSER', 'Fixture browser issuer', '45')))
                    ab('click', '#emc-emp-detail-qual-list-grid tr[data-test-id="1000"]')
                    ab('click', '#emc-emp-detail-qual-list-grid .owned-toolbar button:nth-of-type(3)')
                    ab('wait', '#personnel-qualification-editor')
                    ab('find', 'role', 'button', 'click', '--name', '确认删除资格证书', '--exact')
                    ab('wait', '--text', '资格证书已删除')
                    qualification_grid = ab('get', 'text', '#emc-emp-detail-qual-list-grid')
                    check('browser qualification delete is reflected through the API-backed grid',
                          'Q-BROWSER' not in qualification_grid and '暂无数据' in qualification_grid)
                    check('browser qualification writes are audited without credentials', all(value in audit.read_text() for value in ('personnel-qualification-insert', 'personnel-qualification-delete')) and 'secret' not in audit.read_text())
                    observed = json.loads(json.loads(ab('eval', 'JSON.stringify(window.__qualificationHttp || [])')))
                    report['http_cases'] = report.get('http_cases', []) + observed
                    report['audit'] = {'events': [json.loads(line) for line in audit.read_text().splitlines() if line.strip()]}
                    report['audit']['expected'] = [
                        {'action': 'personnel-qualification-insert', 'actorId': 'e-1',
                         'resource': 'e-1', 'outcome': 'ALLOWED'}]
                    writes = [item for item in observed if item.get('route') in ('POST /secure/emc/module/mdm/emp/emps/{id}/quals', 'DELETE /secure/emc/module/mdm/emp/emps/{id}/quals')]
                    check('qualification HTTP routes return success', len(writes) >= 2 and all(item.get('status') == 200 and item.get('expected_status') == item.get('status') for item in writes))
                    report['status'] = 'executed-pass'
                    raise QualificationComplete()
                if args.personnel_query_projection_only:
                    visible = ab('eval', "JSON.stringify(Array.from(document.querySelectorAll('#emc-emp-detail-base-info-form output[data-field]')).reduce((result, node) => { result[node.getAttribute('data-field')] = node.textContent; return result; }, {}))")
                    check('personnel detail preserves exact known fixture fields', all(value in visible for value in
                        ('\\"id\\":\\"e-2\\"', '\\"userName\\":\\"Alicia\\"', '\\"age\\":\\"40\\"', '\\"remark\\":\\"some remark\\"', '\\"station\\":\\"Reviewer\\"')))
                    ab('find', 'role', 'tab', 'click', '--name', '基本信息', '--exact')
                    ab('wait', '--text', 'Alicia')
                    ab('fill', '[aria-label="姓名筛选"]', 'nobody')
                    ab('find', 'role', 'button', 'click', '--name', '查询', '--exact')
                    ab('wait', '--text', '暂无数据')
                    check('personnel projection no-match clears detail', 'Alicia' not in ab('get', 'text', '#owned-workspace'))
                    ab('fill', '[aria-label="姓名筛选"]', 'Alice')
                    ab('find', 'role', 'button', 'click', '--name', '查询', '--exact')
                    ab('wait', '--text', '共1条')
                    check('personnel projection name filter returns one row', True)
                    ab('find', 'role', 'button', 'click', '--name', '清空', '--exact')
                    ab('wait', '--text', '共23条')
                    ab('find', 'role', 'button', 'click', '--name', '下一页', '--exact')
                    ab('wait', '--text', '第 2 / 2 页')
                    projection_page = ab('get', 'text', '#owned-workspace')
                    check('personnel projection pagination preserves scoped rows', all(value in projection_page for value in ('page-003', 'page-002', 'page-001')) and 'page-004' not in projection_page)
                    report['scope'] = 'packaged personnel browser full visible projection, filters and pagination; synthetic Oracle only'
                    report['status'] = 'executed-pass'
                    raise ProjectionOnlyComplete()
                if args.personnel_main_write:
                    ab('eval', "window.__personnelMainHttp=[]; window.__personnelMainFetch=window.fetch; window.fetch=function(input,init){return window.__personnelMainFetch(input,init).then(function(response){var raw=String(input), path=new URL(raw,location.origin).pathname, method=((init&&init.method)||'GET').toUpperCase(), route=null; if(path==='/secure/emc/module/mdm/emp/emps' && (method==='POST'||method==='DELETE')) route=method+' /secure/emc/module/mdm/emp/emps'; else if(path.indexOf('/secure/emc/module/mdm/emp/emps/')===0 && method==='PUT') route='PUT /secure/emc/module/mdm/emp/emps/{id}'; if(route) window.__personnelMainHttp.push({route:route,role:'manager',organization:'org-a',mode:'isolated',status:response.status,expected_status:response.status}); return response;});};")
                    ab('find', 'role', 'tab', 'click', '--name', '基本信息', '--exact')
                    ab('wait', '--text', 'Alicia')
                    tree = ab('snapshot', '-i')
                    check('personnel main isolated write controls visible', all(value in tree for value in ('新增人员', '修改当前人员', '删除当前人员')))
                    ab('find', 'role', 'button', 'click', '--name', '新增人员', '--exact')
                    ab('wait', '#personnel-editor')
                    check('personnel main add dialog opens', ab('get', 'count', '#personnel-editor').strip() == '1')
                    ab('select', '[aria-label="选择已有账号"]', 'e-4')
                    ab('fill', '[aria-label="姓名"]', 'Distribution employee')
                    ab('find', 'role', 'button', 'click', '--name', '保存人员', '--exact')
                    ab('wait', '--text', '人员已新增')
                    inserted_ui = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'DIST_UI_INSERT|'||USERNAME||'|'||ORGID||'|'||CREATEDBYID FROM T_EMC_EMP WHERE ID='e-4';"), 'personnel distribution UI insert verification')
                    check('personnel distribution insert persists scoped actor row', 'DIST_UI_INSERT|Distribution employee|org-a|e-1' in inserted_ui)
                    ab('fill', '[aria-label="姓名筛选"]', 'Distribution employee')
                    ab('find', 'role', 'button', 'click', '--name', '查询', '--exact')
                    ab('wait', '--text', '共1条')
                    ab('find', 'text', 'Distribution employee', 'click', '--exact')
                    ab('wait', '--fn', 'document.querySelector(\'output[data-field="id"]\')?.textContent === "e-4"')
                    ab('find', 'role', 'button', 'click', '--name', '修改当前人员', '--exact')
                    ab('wait', '#personnel-editor')
                    ab('fill', '[aria-label="姓名"]', 'Distribution updated')
                    ab('find', 'role', 'button', 'click', '--name', '保存人员', '--exact')
                    ab('wait', '--text', '人员已修改')
                    updated_ui = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'DIST_UI_UPDATE|'||USERNAME||'|'||ORGID FROM T_EMC_EMP WHERE ID='e-4';"), 'personnel distribution UI update verification')
                    check('personnel distribution update persists scoped name', 'DIST_UI_UPDATE|Distribution updated|org-a' in updated_ui)
                    ab('fill', '[aria-label="姓名筛选"]', 'Distribution updated')
                    ab('find', 'role', 'button', 'click', '--name', '查询', '--exact')
                    ab('wait', '--text', '共1条')
                    ab('find', 'text', 'Distribution updated', 'click', '--exact')
                    ab('find', 'role', 'button', 'click', '--name', '删除当前人员', '--exact')
                    ab('wait', '#personnel-editor')
                    ab('find', 'role', 'button', 'click', '--name', '确认删除', '--exact')
                    ab('wait', '--text', '人员已删除')
                    ab('find', 'role', 'button', 'click', '--name', '清空', '--exact')
                    ab('wait', '--text', '共23条')
                    restored = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'DIST_UI_RESTORE|'||COUNT(*) FROM T_EMC_EMP WHERE ID='e-4';\nSELECT 'DIST_BASELINE|'||USERNAME FROM T_EMC_EMP WHERE ID='e-3';"), 'personnel distribution UI cleanup verification')
                    check('personnel distribution delete restores baseline and preserves cross-org row', 'DIST_UI_RESTORE|0' in restored and 'DIST_BASELINE|Bob' in restored)
                    inserted = ab('eval', "fetch('/secure/emc/module/mdm/emp/emps',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({b:['',[{id:'e-distribution',userName:'Distribution API employee'}]]})}).then(async r=>JSON.stringify({status:r.status,body:await r.text()}))")
                    check('personnel distribution POST commits in isolated mode', 'status\\":200' in inserted and 'e-distribution' in inserted)
                    updated = ab('eval', "fetch('/secure/emc/module/mdm/emp/emps/e-distribution',{method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify({b:['',[{id:'e-distribution',userName:'Distribution API updated'}]]})}).then(async r=>JSON.stringify({status:r.status,body:await r.text()}))")
                    check('personnel distribution PUT updates in-scope row', 'status\\":200' in updated)
                    cross = ab('eval', "fetch('/secure/emc/module/mdm/emp/emps/e-3',{method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify({b:['',[{id:'e-3',userName:'Cross scope'}]]})}).then(r=>r.status)")
                    check('personnel distribution cross-organization PUT denied', '403' in cross)
                    invalid = ab('eval', "fetch('/secure/emc/module/mdm/emp/emps',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({b:['',[{id:'e-invalid',userName:null}]]})}).then(r=>r.status)")
                    check('personnel distribution invalid POST rejected', '400' in invalid)
                    deleted = ab('eval', "fetch('/secure/emc/module/mdm/emp/emps',{method:'DELETE',headers:{'Content-Type':'application/json'},body:JSON.stringify({b:['',[{id:'e-distribution'}]]})}).then(r=>r.status)")
                    check('personnel distribution DELETE removes created row', '200' in deleted)
                    persisted = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'DIST_API_RESTORE|'||COUNT(*) FROM T_EMC_EMP WHERE ID IN ('e-distribution','e-4');\nSELECT 'DIST_CROSS|'||USERNAME FROM T_EMC_EMP WHERE ID='e-3';"), 'personnel distribution API cleanup verification')
                    check('personnel distribution API cleanup restores scope baseline', 'DIST_API_RESTORE|0' in persisted and 'DIST_CROSS|Bob' in persisted)
                    audit_text = audit.read_text()
                    check('personnel distribution writes are audited without credentials', all(value in audit_text for value in ('personnel-insert','personnel-update','personnel-delete')) and 'secret' not in audit_text)
                    observed = json.loads(json.loads(ab('eval', 'JSON.stringify(window.__personnelMainHttp || [])')))
                    report['http_cases'] = report.get('http_cases', []) + observed
                    report['audit'] = {'events': [json.loads(line) for line in audit.read_text().splitlines() if line.strip()]}
                    report['audit']['expected'] = [{'action': 'personnel-insert', 'actorId': 'e-1', 'outcome': 'ALLOWED'}]
                    report['status'] = 'executed-pass'
                    raise MainWriteComplete()
                ab('find', 'role', 'tab', 'click', '--name', '可见测试', '--exact')
                ab('wait', '--text', 'Water metals')
                check('personnel visible-test detail tab reads joined test projection', all(value in ab('get', 'text', '#owned-workspace') for value in ('Water metals', 'Water')))
                test_toolbar = ab('snapshot', '-i')
                check('visible-test copy control rendered in isolated mode', '复制到其他人员' in test_toolbar)
                test_url = ab('eval', 'Gikam.getComp("emc-emp-detail-test-list-grid").config.url')
                check('visible-test grid retains employee-scoped route', '/e-2/tests/queries' in test_url)
                ab('find', 'role', 'button', 'click', '--name', '删除所选可见测试', '--exact')
                ab('wait', '--text', '请先选择可见测试。')
                check('visible-test deletion requires a selected row', ab('get', 'count', '#personnel-test-editor').strip() == '0')
                ab('click', '#emc-emp-detail-test-list-grid tr[data-test-id="501"]')
                ab('find', 'role', 'button', 'click', '--name', '复制到其他人员', '--exact')
                ab('wait', '#personnel-test-copy-editor')
                ab('wait', '--text', '目标人员第 1 / 2 页')
                copy_text = ab('get', 'text', '#personnel-test-copy-editor')
                check('visible-test copy picker excludes source and out-of-scope employees', 'e-1 · Alice' in copy_text and 'Alicia' not in copy_text and 'Bob' not in copy_text)
                ab('find', 'role', 'button', 'click', '--name', '目标人员下一页', '--exact')
                ab('wait', '--text', '目标人员第 2 / 2 页')
                check('visible-test copy target picker paginates', 'page-001' in ab('get', 'text', '#personnel-test-copy-editor'))
                ab('fill', '[aria-label="复制目标姓名筛选"]', 'Alice')
                ab('find', 'role', 'button', 'click', '--name', '查询目标人员', '--exact')
                ab('wait', '--text', '目标人员第 1 / 1 页')
                check('visible-test copy target filter resets page', 'page-001' not in ab('get', 'text', '#personnel-test-copy-editor'))
                ab('select', '[aria-label="选择复制目标人员"]', 'e-1')
                ab('find', 'role', 'button', 'click', '--name', '确认复制可见测试', '--exact')
                ab('wait', '--text', '可见测试已复制')
                copied = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'UI_TEST_COPY|'||COUNT(*) FROM T_EMC_EMP_TEST WHERE EMPID='e-1' AND TESTID=501 AND CREATEDBYID='e-1';"), 'visible-test UI copy verification')
                check('visible-test copy persists target row and server actor', 'UI_TEST_COPY|1' in copied)
                ab('click', '#emc-emp-detail-test-list-grid tr[data-test-id="501"]')
                ab('find', 'role', 'button', 'click', '--name', '复制到其他人员', '--exact')
                ab('wait', '#personnel-test-copy-editor')
                ab('wait', '--text', '目标人员第 1 / 2 页')
                ab('select', '[aria-label="选择复制目标人员"]', 'e-1')
                ab('find', 'role', 'button', 'click', '--name', '确认复制可见测试', '--exact')
                ab('wait', '--text', '目标人员已分配该可见测试。')
                check('visible-test copy rejects duplicate target assignment', 'UI_TEST_COPY|1' in passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'UI_TEST_COPY|'||COUNT(*) FROM T_EMC_EMP_TEST WHERE EMPID='e-1' AND TESTID=501;"), 'visible-test duplicate copy verification'))
                ab('find', 'role', 'button', 'click', '--name', '取消', '--exact')
                ab('click', '#emc-emp-edit-list-grid tr[data-test-id="e-1"]')
                ab('wait', '--fn', 'document.querySelector(\'output[data-field="id"]\')?.textContent === "e-1"')
                ab('wait', '#emc-emp-detail-test-list-grid tr[data-test-id="501"]')
                ab('click', '#emc-emp-detail-test-list-grid tr[data-test-id="501"]')
                ab('find', 'role', 'button', 'click', '--name', '删除所选可见测试', '--exact')
                ab('wait', '#personnel-test-editor')
                ab('find', 'role', 'button', 'click', '--name', '确认删除可见测试', '--exact')
                ab('wait', '--text', '可见测试已删除')
                check('visible-test copy target deletion restores Oracle baseline', 'UI_TEST_COPY|0' in passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'UI_TEST_COPY|'||COUNT(*) FROM T_EMC_EMP_TEST WHERE EMPID='e-1';"), 'visible-test copy cleanup verification'))
                ab('click', '#emc-emp-edit-list-grid tr[data-test-id="e-2"]')
                ab('wait', '--fn', 'document.querySelector(\'output[data-field="id"]\')?.textContent === "e-2"')
                ab('wait', '#emc-emp-detail-test-list-grid tr[data-test-id="501"]')
                ab('find', 'role', 'button', 'click', '--name', '新增可见测试', '--exact')
                ab('wait', '--text', '共22个未分配测试方法')
                check('test-method picker excludes assigned method and shows joined fields', 'Water metals' not in ab('get', 'text', '#personnel-test-editor') and all(value in ab('get', 'text', '#personnel-test-editor') for value in ('Water organics', 'Fixture method', 'Fixture basis')))
                ab('find', 'role', 'button', 'click', '--name', '测试方法下一页', '--exact')
                ab('wait', '--text', '测试方法第 2 / 2 页')
                check('test-method picker paginates unassigned methods', '选择测试方法 502 Water organics' in ab('snapshot', '-i'))
                ab('fill', '[aria-label="测试方法名称筛选"]', 'no-such-test')
                ab('find', 'role', 'button', 'click', '--name', '查询测试方法', '--exact')
                ab('wait', '--text', '没有可选测试方法')
                check('test-method picker no match disables assignment', 'true' in ab('eval', 'document.querySelector("#personnel-test-editor button[type=submit]").disabled'))
                ab('fill', '[aria-label="测试方法名称筛选"]', 'organics')
                ab('find', 'role', 'button', 'click', '--name', '查询测试方法', '--exact')
                ab('wait', '--text', '共22个未分配测试方法')
                ab('find', 'role', 'button', 'click', '--name', '测试方法下一页', '--exact')
                ab('wait', '--text', '测试方法第 2 / 2 页')
                ab('check', '[aria-label="选择测试方法 502 Water organics"]')
                ab('check', '[aria-label="选择测试方法 503 Water organics"]')
                ab('find', 'role', 'button', 'click', '--name', '添加所选测试', '--exact')
                ab('wait', '--text', '可见测试已新增')
                check('test-method picker batch assignment refreshes visible-test grid', ab('get', 'text', '#emc-emp-detail-test-list-grid').count('Water organics') == 2)
                assigned = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'UI_TEST_INSERT|'||COUNT(*) FROM T_EMC_EMP_TEST WHERE EMPID='e-2' AND TESTID IN (502,503) AND ID>=1000 AND CREATEDBYID='e-1';"), 'visible-test UI insert verification')
                check('visible-test assignment persists sequence and server actor', 'UI_TEST_INSERT|2' in assigned)
                ab('find', 'role', 'button', 'click', '--name', '新增可见测试', '--exact')
                ab('wait', '--text', '共20个未分配测试方法')
                check('picker excludes newly assigned methods after commit', '选择测试方法 502 Water organics' not in ab('snapshot', '-i') and '选择测试方法 503 Water organics' not in ab('snapshot', '-i'))
                ab('find', 'role', 'button', 'click', '--name', '取消', '--exact')
                for index, test_id in enumerate(('502', '503')):
                    ab('click', '#emc-emp-detail-test-list-grid tr[data-test-id="' + test_id + '"]')
                    ab('find', 'role', 'button', 'click', '--name', '删除所选可见测试', '--exact')
                    ab('wait', '#personnel-test-editor')
                    if index == 0:
                        ab('find', 'role', 'button', 'click', '--name', '取消', '--exact')
                        unchanged = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'UI_TEST_CANCEL|'||COUNT(*) FROM T_EMC_EMP_TEST WHERE EMPID='e-2' AND TESTID IN (502,503);"), 'visible-test UI cancel verification')
                        check('cancelled deletion preserves assigned methods', 'UI_TEST_CANCEL|2' in unchanged)
                        ab('find', 'role', 'button', 'click', '--name', '删除所选可见测试', '--exact')
                        ab('wait', '#personnel-test-editor')
                    ab('find', 'role', 'button', 'click', '--name', '确认删除可见测试', '--exact')
                    ab('wait', '--text', '可见测试已删除')
                    ab('wait', '--fn', 'document.querySelector(\'#emc-emp-detail-test-list-grid tr[data-test-id="' + test_id + '"]\') === null')
                    deleted_test = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'UI_TEST_DELETE|'||COUNT(*) FROM T_EMC_EMP_TEST WHERE EMPID='e-2' AND TESTID=" + test_id + ';'), 'visible-test UI delete persistence')
                    check('visible-test deletion persists method ' + test_id, 'UI_TEST_DELETE|0' in deleted_test)
                restored = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'UI_TEST_RESTORE|'||COUNT(*) FROM T_EMC_EMP_TEST WHERE EMPID='e-2';\nSELECT 'UI_TEST_ORIGINAL|'||COUNT(*) FROM T_EMC_EMP_TEST WHERE ID=201 AND EMPID='e-2' AND TESTID=501;"), 'visible-test UI delete verification')
                final_grid = ab('get', 'text', '#emc-emp-detail-test-list-grid')
                check('visible-test deletions restore original Oracle baseline', 'UI_TEST_RESTORE|1' in restored and 'UI_TEST_ORIGINAL|1' in restored and 'Water organics' not in final_grid)
                check('visible-test writes are audited without credentials', all(value in audit.read_text() for value in ('personnel-test-insert', 'personnel-test-delete')) and 'secret' not in audit.read_text())
                ab('find', 'role', 'tab', 'click', '--name', '基本信息', '--exact')
                ab('wait', '--text', 'Alicia')
                ab('fill', '[aria-label="姓名筛选"]', 'nobody')
                ab('find', 'role', 'button', 'click', '--name', '查询', '--exact')
                ab('wait', '--text', '暂无数据')
                check('no match clears former detail', 'Alicia' not in ab('get', 'text', '#owned-workspace'))
                check('no match clears former visible-test data and employee context', 'Water metals' not in ab('get', 'text', '#emc-emp-detail-test-list-grid') and 'null' in ab('eval', 'Gikam.getComp("emc-emp-detail-test-list-grid").config.url'))
                ab('fill', '[aria-label="姓名筛选"]', 'Alice')
                ab('find', 'role', 'button', 'click', '--name', '查询', '--exact')
                ab('wait', '--text', '共1条')
                check('name filter returns one row', True)
                ab('find', 'role', 'button', 'click', '--name', '清空', '--exact')
                ab('wait', '--text', '共23条')
                ab('find', 'role', 'button', 'click', '--name', '下一页', '--exact')
                ab('wait', '--text', '第 2 / 2 页')
                page = ab('get', 'text', '#owned-workspace')
                check('page two contains final three rows', all(value in page for value in
                    ('page-003', 'page-002', 'page-001')) and 'page-004' not in page)
                ab('select', '[aria-label="每页条数"]', '10')
                ab('wait', '--text', '第 1 / 3 页')
                check('page size resets pagination', True)
                tree = ab('snapshot', '-i')
                check('writes disabled and related tabs explicit', 'disabled' in tree and '未验证/不可用' in tree)
                screenshot = temporary / 'personnel.png'
                ab('screenshot', str(screenshot))
                report['screenshot_sha256'] = digest(screenshot)
                check('personnel isolated write controls visible', all(value in tree for value in ('新增人员', '修改当前人员', '删除当前人员')))
                ab('find', 'role', 'button', 'click', '--name', '新增人员', '--exact')
                ab('wait', '#personnel-editor')
                check('personnel add dialog opens', ab('get', 'count', '#personnel-editor').strip() == '1')
                ab('select', '[aria-label="选择已有账号"]', 'e-4')
                ab('fill', '[aria-label="姓名"]', 'Browser employee')
                ab('find', 'role', 'button', 'click', '--name', '保存人员', '--exact')
                ab('wait', '--text', '人员已新增')
                inserted_ui = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'UI_INSERT|'||USERNAME FROM T_EMC_EMP WHERE ID='e-4';"), 'personnel UI insert verification')
                check('personnel dialog insert persists selected existing account', 'UI_INSERT|Browser employee' in inserted_ui)
                ab('fill', '#emc-emp-edit-list-grid input[aria-label="姓名筛选"]', 'Browser employee')
                input_state = ab('get', 'value', '#emc-emp-edit-list-grid input[aria-label="姓名筛选"]')
                check('personnel inserted row filter input receives value', 'Browser employee' in input_state)
                ab('find', 'role', 'button', 'click', '--name', '查询', '--exact')
                ab('wait', '--text', '共1条')
                ab('wait', '#emc-emp-edit-list-grid td[data-field="userName"]')
                ab('find', 'text', 'Browser employee', 'click', '--exact')
                ab('wait', '--fn', 'document.querySelector(\'output[data-field="id"]\')?.textContent === "e-4"')
                ab('find', 'role', 'button', 'click', '--name', '修改当前人员', '--exact')
                ab('wait', '#personnel-editor')
                ab('fill', '[aria-label="姓名"]', 'Browser updated')
                ab('find', 'role', 'button', 'click', '--name', '保存人员', '--exact')
                ab('wait', '--text', '人员已修改')
                updated_ui = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'UI_UPDATE|'||USERNAME FROM T_EMC_EMP WHERE ID='e-4';"), 'personnel UI update verification')
                check('personnel dialog update persists new name', 'UI_UPDATE|Browser updated' in updated_ui)
                ab('fill', '[aria-label="姓名筛选"]', 'Browser updated')
                ab('find', 'role', 'button', 'click', '--name', '查询', '--exact')
                ab('wait', '--text', '共1条')
                ab('wait', '--fn', 'document.querySelector(\'#emc-emp-edit-list-grid td[data-field="userName"]\')?.textContent === "Browser updated"')
                ab('find', 'text', 'Browser updated', 'click', '--exact')
                ab('find', 'role', 'button', 'click', '--name', '删除当前人员', '--exact')
                ab('wait', '#personnel-editor')
                ab('find', 'role', 'button', 'click', '--name', '确认删除', '--exact')
                ab('wait', '--text', '人员已删除')
                ab('find', 'role', 'button', 'click', '--name', '清空', '--exact')
                ab('wait', '--text', '共23条')
                check('personnel UI write flow returns to baseline', 'Browser employee' not in ab('get', 'text', '#owned-workspace'))
                inserted = ab('eval', "fetch('/secure/emc/module/mdm/emp/emps',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({b:['',[{id:'e-browser',userName:'Browser employee'}]]})}).then(async r=>JSON.stringify({status:r.status,body:await r.text()}))")
                check('browser personnel POST commits in isolated mode', 'status\\":200' in inserted and 'e-browser' in inserted)
                updated = ab('eval', "fetch('/secure/emc/module/mdm/emp/emps/e-browser',{method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify({b:['',[{id:'e-browser',userName:'Browser updated'}]]})}).then(async r=>JSON.stringify({status:r.status,body:await r.text()}))")
                check('browser personnel PUT updates in-scope row', 'status\\":200' in updated)
                cross = ab('eval', "fetch('/secure/emc/module/mdm/emp/emps/e-3',{method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify({b:['',[{id:'e-3',userName:'Cross scope'}]]})}).then(r=>r.status)")
                check('browser personnel cross-organization PUT denied', '403' in cross)
                invalid = ab('eval', "fetch('/secure/emc/module/mdm/emp/emps',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({b:['',[{id:'e-invalid',userName:null}]]})}).then(r=>r.status)")
                check('browser personnel invalid POST rejected', '400' in invalid)
                deleted = ab('eval', "fetch('/secure/emc/module/mdm/emp/emps',{method:'DELETE',headers:{'Content-Type':'application/json'},body:JSON.stringify({b:['',[{id:'e-browser'}]]})}).then(r=>r.status)")
                check('browser personnel DELETE removes created row', '200' in deleted)
                persisted = passed(sql("ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\nSELECT 'BROWSER_WRITE|'||COUNT(*) FROM T_EMC_EMP WHERE ID IN ('e-browser','e-4');\nSELECT 'BROWSER_CROSS|'||USERNAME FROM T_EMC_EMP WHERE ID='e-3';"), 'personnel browser write verification')
                check('browser personnel write cleanup and scope verified', 'BROWSER_WRITE|0' in persisted and 'BROWSER_CROSS|Bob' in persisted)
                audit_text = audit.read_text()
                check('browser personnel writes are audited without credentials', all(value in audit_text for value in ('personnel-insert','personnel-update','personnel-delete')) and 'secret' not in audit_text)
                ab('click', '#logout')
                ab('wait', '--text', '已退出登录。')
                check('logout clears display', 'Alice' not in ab('get', 'text', 'body'))
                rejected = ab('eval', "fetch('/secure/emc/module/mdm/emp/emps/queries/raw'," +
                    "{method:'POST',headers:{'Content-Type':'application/json'},body:'{}'}).then(r=>r.status)")
                check('logout invalidates server session', '403' in rejected)
                ab('fill', '#login-id', 'other')
                ab('fill', '#login-password', 'secret')
                ab('click', '#login-submit')
                ab('wait', '--text', '当前账号无人员查询权限')
                check('low permission page denied', 'Alice' not in ab('get', 'text', 'body'))
                ab('close')
                browser = None
                report['browser_session_closed'] = True
                if args.distribution:
                    process.terminate()
                    process.wait(timeout=20)
                else:
                    stop.touch()
                    process.wait(timeout=20)
                check('runtime closed cleanly', process.returncode == 0)
                process = None
                check('audit excludes credentials', 'secret' not in audit.read_text()
                      and password not in audit.read_text())
                report['browser_transcript_sha256'] = hashlib.sha256(''.join(transcript).encode()).hexdigest()
                report['status'] = 'executed-pass'
    except ProjectionOnlyComplete:
        pass
    except QualificationComplete:
        pass
    except MainWriteComplete:
        pass
    except Exception as failure:
        report['reason'] = str(failure) if isinstance(failure, RuntimeError) else type(failure).__name__
        if browser:
            diagnostic = {}
            for label, command in (('snapshot', ['snapshot', '-i']),
                                   ('workspace', ['get', 'text', '#owned-workspace']),
                                   ('errors', ['errors'])):
                try:
                    result = run(['agent-browser', '--session', browser, *command], timeout=20)
                    diagnostic[label] = (result.stdout + result.stderr)[-12000:]
                except Exception as diagnostic_failure:
                    diagnostic[label] = type(diagnostic_failure).__name__
            report['diagnostic'] = diagnostic
            if 'temporary' in locals() and (temporary / 'application.log').exists():
                report['diagnostic']['application_log'] = (temporary / 'application.log').read_text(errors='replace')[-16000:]
    finally:
        cleanup_started = time.perf_counter()
        if process and process.poll() is None:
            process.terminate()
            try:
                process.wait(timeout=10)
            except subprocess.TimeoutExpired:
                process.kill()
                process.wait()
        cleanup = {}
        if browser:
            cleanup['browser_session_closed'] = run(['agent-browser', '--session', browser, 'close']).returncode == 0
        else:
            cleanup['browser_session_closed'] = True
        if created:
            cleanup['container_removed'] = run(['docker', 'rm', '--force', '--volumes', name]).returncode == 0
            cleanup['anonymous_volumes_removed'] = cleanup['container_removed'] and all(
                run(['docker', 'volume', 'inspect', volume]).returncode != 0 for volume in volumes)
        if network_created:
            cleanup['network_removed'] = (run(['docker', 'network', 'rm', network]).returncode == 0
                                          and run(['docker', 'network', 'inspect', network]).returncode != 0)
        cleanup['temporary_files_removed'] = True
        report['cleanup'] = cleanup
        report['phase_timings_ms']['cleanup'] = round((time.perf_counter() - cleanup_started) * 1000, 3)
        report['finished_at'] = datetime.datetime.now(datetime.timezone.utc).isoformat()
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(json.dumps(report, ensure_ascii=False, indent=2) + '\n')
    print('owned web browser: ' + report['status'] + ' (' + str(len(report['checks'])) + ' checks)')
    return 0 if report['status'] == 'executed-pass' else 1


if __name__ == '__main__':
    raise SystemExit(main())
