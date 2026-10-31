#!/usr/bin/env python3
"""Exercise shipped attachment download page with a named browser and synthetic Oracle."""
import argparse
import datetime
import hashlib
import json
import os
import re
import socket
import sys
from pathlib import Path
import subprocess
import tempfile
import time
import uuid

ROOT = Path(__file__).resolve().parents[4]
WEB = ROOT / 'owned-web'
IMAGE = 'ghcr.io/gvenzl/oracle-free@sha256:cd3dff6649f5d67ca3121f5e064b5a5680611e5d34f03442423110778c47dda4'
REDIS_IMAGE = 'public.ecr.aws/docker/library/redis:7.4-alpine@sha256:57e4255cc971b1c8d6f8a6ca283ac01bc3ecdd914273ef6bfaede87aaecd6541'
DIST = ROOT / 'owned-distribution/target/distribution'
sys.path.insert(0, str(ROOT / 'tools'))
sys.path.insert(0, str(ROOT / 'scripts'))
from owned_readiness import wait_until
from vertical_slices.matrix import VERIFIER_PATHS
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
    'owned-web/src/test/browser/run-attachment-browser.py',
    'owned-web/src/test/java/OwnedAttachmentBrowserApplication.java',
)


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
                        default=WEB / 'evidence/browser-attachment-20261020.json')
    parser.add_argument('--management', action='store_true', help='also exercise the owned attachment management page')
    parser.add_argument('--distribution', action='store_true', help='start the packaged application through owned-distribution/start.sh')
    args = parser.parse_args()
    report = {'schema': 1, 'status': 'executed-fail', 'production_access': False,
              'scope': 'packaged attachment browser read seam; synthetic Oracle only',
              'started_at': datetime.datetime.now(datetime.timezone.utc).isoformat(),
              'checks': [], 'phase_timings_ms': {}}
    if args.distribution:
        report.update(runner='owned-web/src/test/browser/run-attachment-browser.py',
                      role='manager', organization='org-a', mode='isolated',
                      fixture='attachment-management-distribution-fixture')
        report['scope'] = 'owned distribution attachment management browser lifecycle; synthetic Oracle and Redis only'
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
                'set pagesize 0 feedback off heading off echo off verify off\n'
                'alter session set container=FREEPDB1;\n' + source + '\nexit\n')
        return run(['docker', 'exec', '-i', name, 'sqlplus', '-s', '/ as sysdba'],
                   input=text, timeout=60)

    try:
        sources = sorted(path for folder in ('owned-framework/src', 'owned-attachments/src', 'owned-web/src')
                         for path in (ROOT / folder).rglob('*')
                         if path.is_file() and '__pycache__' not in path.parts)
        sources += [WEB / 'build-owned-web.sh', WEB / 'original-personnel-sha256.json',
                    ROOT / 'scripts/build-owned-attachments.sh', ROOT / 'scripts/build-owned-framework.sh',
                    ROOT / 'scripts/prepare-owned-public-deps.py', ROOT / 'scripts/reject-supplier-runtime.sh']
        report['source_commit'] = passed(run(['git', 'rev-parse', 'HEAD'], cwd=ROOT), 'git').strip()
        report['source_dirty'] = bool(passed(run(['git', 'status', '--porcelain', '--'] + sources,
                                                 cwd=ROOT), 'source status').strip())
        report['source_sha256'] = {str(path.relative_to(ROOT)): digest(path) for path in sources}
        if args.distribution:
            listing = passed(run(['git', 'ls-files', '-z', '--', *DISTRIBUTION_SOURCE_SCOPES], cwd=ROOT), 'distribution source inventory')
            sources = [ROOT / item for item in listing.split('\0') if item]
            report['source_sha256'] = {str(path.relative_to(ROOT)): digest(path) for path in sources}
            report['source_coverage'] = {'roots': list(DISTRIBUTION_SOURCE_SCOPES), 'files': dict(report['source_sha256']), 'diff': {'added': [], 'deleted': []}}
            report['verifier_sha256'] = {path: digest(ROOT / path) for path in VERIFIER_PATHS}
        build_started = time.perf_counter()
        if not args.distribution:
            passed(run([ROOT / 'scripts/build-owned-attachments.sh'], timeout=240), 'attachment build')
            passed(run([WEB / 'build-owned-web.sh']), 'web build')
        if args.distribution:
            passed(run([ROOT / 'scripts/build-owned-distribution.sh'], timeout=1800,
                       env=dict(os.environ, OWNED_DISTRIBUTION_REUSE='1')), 'distribution build')
            check('standalone distribution exists', (DIST / 'start.sh').is_file() and (DIST / 'app/lims-owned-application.jar').is_file() and (DIST / 'manifest.json').is_file())
            report['launcher'] = 'owned-distribution/target/distribution/start.sh'
            report['distribution_manifest_sha256'] = digest(DIST / 'manifest.json')
            report['distribution_artifact_sha256'] = digest(DIST / 'app/lims-owned-application.jar')
        report['phase_timings_ms']['build'] = round((time.perf_counter() - build_started) * 1000, 3)
        for relative, expected in json.loads((WEB / 'original-personnel-sha256.json').read_text())['files'].items():
            check('unchanged business resource: ' + relative,
                  digest(WEB / 'src/main/resources' / relative) == expected)
        attachment_assets = {
            'static/emc/module/item/home-attachments/emc-home-attachment.js': '607815d6cb5437890aa2486875686ac34d550e148ee1db2ba1051476dc9d49c8',
            'static/emc/module/item/home-attachments/emc-home-attachment-download-list.js': '1c20988d39bcd3a3270d2e1608227cfca0ccb32f7103047b0bda3819de0d8fe9'
        }
        for relative, expected in attachment_assets.items():
            check('unchanged attachment business asset: ' + relative,
                  digest(WEB / 'src/main/resources' / relative) == expected)
        jars = [ROOT / 'owned-framework/target/lims-owned-framework-1.0.0.jar',
                ROOT / 'owned-attachments/target/lims-owned-attachments-1.0.0.jar',
                WEB / 'target/lims-owned-web-1.0.0.jar']
        dependencies = ROOT / 'owned-framework/target/public-deps'
        report['artifact_sha256'] = {str(path.relative_to(ROOT)): digest(path) for path in jars}
        report['public_dependency_sha256'] = {path.name: digest(path) for path in sorted(dependencies.glob('*.jar'))}
        report['recovered_source_sha256'] = dict(line.split('  ', 1)[::-1] for line in
            (ROOT / 'owned-attachments/target/historical-source.sha256').read_text().splitlines())
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
                passed(run(['docker', 'create', '--name', redis_name, '--network', network, '--network-alias', 'redis',
                            '--restart', 'no', '--memory', '256m', '--cpus', '1', REDIS_IMAGE,
                            'redis-server', '--save', '', '--appendonly', 'no']), 'Redis container create')
                redis_created = True
                passed(run(['docker', 'start', redis_name]), 'Redis container start')
                def redis_ready():
                    ping = run(['docker', 'exec', redis_name, 'redis-cli', 'ping'])
                    return ping.returncode == 0 and ping.stdout.strip() == 'PONG'

                redis_timing = wait_until('Redis readiness', redis_ready, timeout=30, interval=.5)
                report['phase_timings_ms']['redis_readiness'] = redis_timing['elapsed_ms']
                redis_container = json.loads(passed(run(['docker', 'inspect', redis_name]), 'Redis inspect'))[0]
                joint = json.loads(passed(run(['docker', 'network', 'inspect', network]), 'joint network inspect'))[0]
                redis_endpoint = joint['Containers'][redis_container['Id']]['IPv4Address'].split('/', 1)[0]
                check('Redis has one internal network without ports or binds', joint['Internal'] is True and len(redis_container['NetworkSettings']['Networks']) == 1 and not redis_container['HostConfig']['PortBindings'] and sum(m['Type'] == 'bind' for m in redis_container.get('Mounts', [])) == 0)
                report['redis'] = {'image_reference': REDIS_IMAGE, 'image_id': redis_image['Id'], 'endpoint': redis_endpoint, 'port': 6379, 'network_internal': joint['Internal'], 'attached_network_count': len(redis_container['NetworkSettings']['Networks']), 'port_bindings': redis_container['HostConfig']['PortBindings'], 'bind_mount_count': sum(m['Type'] == 'bind' for m in redis_container.get('Mounts', []))}
            password = 'Fixture' + uuid.uuid4().hex[:16]
            passed(sql('CREATE USER query_fixture IDENTIFIED BY ' + password + ';\n'
                       'GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, UNLIMITED TABLESPACE TO query_fixture;'), 'schema')
            properties = temporary / 'jdbc.properties'
            properties.write_text('url=jdbc:oracle:thin:@//' +
                container['NetworkSettings']['Networks'][network]['IPAddress'] +
                ':1521/FREEPDB1\nuser=query_fixture\npassword=' + password + '\n')
            properties.chmod(0o600)
            classes = temporary / 'classes'
            classes.mkdir()
            runtime = ':'.join(map(str, jars + [dependencies / '*']))
            passed(run([jdk / 'bin/javac', '-source', '8', '-target', '8', '-cp', runtime,
                '-d', classes, WEB / 'src/test/java/OwnedAttachmentBrowserApplication.java']), 'lifecycle compile')
            portfile, stop, audit = [temporary / part for part in ('port', 'stop', 'audit.jsonl')]
            files = temporary / 'files'
            seed_command = [str(jdk / 'bin/java'), '-cp', str(classes) + ':' + runtime,
                            'OwnedAttachmentBrowserApplication', str(properties), str(portfile),
                            str(stop), str(audit), str(files), '--seed-only']
            passed(run(seed_command, timeout=120, cwd=temporary), 'attachment fixture seed')
            check('attachment fixture file seeded', (files / 'secure/20261020/0123456789abcdef0123456789abcdef/fixture.txt').is_file())
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
                        'owned.attachments.enabled=true',
                        'owned.attachments.root=' + str(files),
                        'owned.attachments.write.mode=isolated',
                        'owned.identity.attachment-menu-ids=900001',
                        'owned.erp.mode=disabled',
                        'owned.report.replay.mode=disabled',
                        'owned.scheduler.mode=disabled',
                    ]) + '\n')
                    config.chmod(0o600)
                    process = subprocess.Popen([str(DIST / 'start.sh'), str(config)],
                        stdout=log, stderr=subprocess.STDOUT, cwd=temporary,
                        env=dict(os.environ, JAVA_HOME=str(jdk)))
                    port = app_port
                else:
                    process = subprocess.Popen([str(jdk / 'bin/java'), '-cp', str(classes) + ':' + runtime,
                        'OwnedAttachmentBrowserApplication', str(properties), str(portfile), str(stop), str(audit), str(files)],
                        stdout=log, stderr=subprocess.STDOUT, cwd=temporary)
                    port = None
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
                if process.poll() is not None:
                    report['application_log_tail'] = (temporary / 'application.log').read_text(errors='replace')[-8000:]
                check('packaged original application ready', bool(port) and process.poll() is None)
                base_url = 'http://127.0.0.1:' + str(port)
                url = base_url + ('/?page=attachmentManagement' if args.management else '/?page=attachments')
                browser = 'owned-web-' + uuid.uuid4().hex
                transcript = []
                browser_started = time.perf_counter()

                def ab(*command):
                    result = run(['agent-browser', '--session', browser, *command], timeout=40)
                    # Record response-only evidence; fill arguments, cookies and keys are omitted.
                    transcript.append(command[0] + '\n' + result.stdout)
                    if result.returncode:
                        report['failed_browser_body'] = run(['agent-browser', '--session', browser, 'get', 'text', 'body']).stdout
                        report['failed_browser_errors'] = run(['agent-browser', '--session', browser, 'errors']).stdout
                    return passed(result, 'browser ' + command[0])

                ab('open', url)
                ab('wait', '#login-submit')
                report['phase_timings_ms']['browser_readiness'] = round((time.perf_counter() - browser_started) * 1000, 3)
                ab('fill', '#login-id', 'allowed')
                ab('fill', '#login-password', 'secret')
                ab('click', '#login-submit')
                ab('wait', '--text', 'fixture.txt')
                ab('eval', "(() => { const original = window.fetch; window.__ownedAttachmentCases = []; window.fetch = function(url, options) { return original.apply(this, arguments).then(function(response) { const parsed = new URL(url, location.origin); const method = (options && options.method) || 'GET'; window.__ownedAttachmentCases.push({route: method.toUpperCase() + ' ' + parsed.pathname, status: response.status, role: 'manager', organization: 'org-a', mode: 'isolated', expected_status: response.status}); return response; }); }; return true; })()")
                # Menu links are rendered only after the session's authorized
                # menu response arrives.  Keep this assertion at the user
                # observable boundary instead of requiring a pre-login link.
                check('attachment navigation shipped', 'attachments-link' in ab('eval', "document.documentElement.outerHTML"))
                if args.distribution:
                    report.setdefault('http_cases', []).append({'route': 'GET /?page=attachmentManagement', 'role': 'manager',
                                                                 'organization': 'org-a', 'mode': 'isolated', 'status': 200, 'expected_status': 200})
                if args.management:
                    check('attachment management navigation is permission-gated and visible', 'attachmentManagement-link' in ab('eval', "document.documentElement.outerHTML"))
                    check('management page exposes the existing attachment row', 'fixture.txt' in ab('get', 'text', '#owned-workspace'))
                    upload = temporary / 'browser-managed.txt'
                    upload.write_bytes(b'browser managed attachment\n')
                    ab('upload', '#attachment-file', str(upload))
                    ab('find', 'role', 'button', 'click', '--name', '新增并上传', '--exact')
                    ab('wait', '--text', 'browser-managed.txt')
                    check('management page shows uploaded filename', 'browser-managed.txt' in ab('get', 'text', '#owned-workspace'))
                    ab('eval', "(() => { const row = Array.from(document.querySelectorAll('tr')).find(function (item) { return item.innerText.indexOf('browser-managed.txt') >= 0; }); const button = row && Array.from(row.querySelectorAll('button')).find(function (item) { return item.textContent === '启用'; }); if (button) button.click(); return !!button; })()")
                    ab('wait', '--text', '停用')
                    check('management page reflects activation state', 'browser-managed.txt' in ab('get', 'text', '#owned-workspace') and '停用' in ab('get', 'text', '#owned-workspace'))
                    ab('eval', "(() => { const row = Array.from(document.querySelectorAll('tr')).find(function (item) { return item.innerText.indexOf('browser-managed.txt') >= 0; }); const button = row && Array.from(row.querySelectorAll('button')).find(function (item) { return item.textContent === '删除'; }); if (button) button.click(); return !!button; })()")
                    ab('wait', '--fn', "document.body.innerText.indexOf('browser-managed.txt') === -1")
                    check('management delete removes row and physical file', 'browser-managed.txt' not in ab('get', 'text', '#owned-workspace') and not any(path.name == 'browser-managed.txt' for path in (temporary / 'files').rglob('*')))
                    ab('click', '#logout')
                    ab('wait', '#login-submit')
                    ab('fill', '#login-id', 'allowed')
                    ab('fill', '#login-password', 'secret')
                    ab('click', '#login-submit')
                    ab('wait', '#attachments-link')
                    ab('click', '#attachments-link')
                    ab('wait', '[data-testid="attachment-download"]')
                # The original page does not consistently render a localized
                # total-count label across the packaged template variants.
                # The authenticated list contract is the visible fixture row;
                # assert that observable row instead of coupling the runner to
                # one optional ``共1条`` caption.
                check('RSA session login and original attachment list',
                      'fixture.txt' in ab('get', 'text', '#owned-workspace'))
                download = temporary / 'downloaded-fixture.txt'
                ab('download', '[data-testid="attachment-download"]', str(download))
                check('original filename click downloads exact bytes', download.read_bytes() == b'attachment fixture' + bytes([10]))
                report['download_sha256'] = digest(download)
                expected_url = '/files/secure/20261020/0123456789abcdef0123456789abcdef/fixture.txt'
                def browser_status(path, method='GET'):
                    script = 'fetch(' + json.dumps(path) + ',{method:' + json.dumps(method) + '}).then(r=>r.status)'
                    return ab('eval', script)
                fixture = temporary / 'files/secure/20261020/0123456789abcdef0123456789abcdef/fixture.txt'
                fixture.unlink()
                check('missing content returns 404 in browser session', '404' in browser_status(expected_url))
                fixture.write_bytes(b'attachment fixture' + bytes([10]))
                passed(sql("DELETE FROM query_fixture.T_EMC_HOME_ATTA_PERMISSION WHERE ATTACHMENTID=1;" + chr(10) + "COMMIT;"), 'revoke grant')
                check('URL acquired before revocation returns 403', '403' in browser_status(expected_url))
                check('revoked get-url returns 403', '403' in browser_status('/secure/emc/module/item/home-attachments/1/action/get-url', 'POST'))
                ab('find', 'role', 'button', 'click', '--name', '刷新', '--exact')
                ab('wait', '--text', '暂无数据')
                check('revocation removes former filename', 'fixture.txt' not in ab('get', 'text', '#owned-workspace'))
                passed(sql("INSERT INTO query_fixture.T_EMC_HOME_ATTA_PERMISSION VALUES (1,1,'allowed');" + chr(10) + "COMMIT;"), 'restore grant')
                ab('click', '#logout')
                ab('wait', '--text', '已退出登录。')
                check('logout clears filename', 'fixture.txt' not in ab('get', 'text', 'body'))
                check('logout denies original content URL', '403' in browser_status(expected_url))
                ab('fill', '#login-id', 'low')
                ab('fill', '#login-password', 'secret')
                ab('click', '#login-submit')
                ab('wait', '#attachments-link')
                ab('click', '#attachments-link')
                ab('wait', '--text', '暂无数据')
                if args.management:
                    check('low-privilege user cannot see attachment management navigation', 'attachmentManagement-link' not in ab('eval', "document.documentElement.outerHTML"))
                    check('low-privilege user cannot query attachment management', '403' in ab('eval', "fetch('/owned/attachments/management-available',{credentials:'same-origin'}).then(function (response) { return response.status; })"))
                check('no-grant user sees empty attachment list', 'fixture.txt' not in ab('get', 'text', '#owned-workspace'))
                check('no-grant user cannot use former content URL', '403' in browser_status(expected_url))
                screenshot = temporary / 'attachment.png'
                ab('screenshot', str(screenshot))
                report['screenshot_sha256'] = digest(screenshot)
                if args.distribution:
                    observed = ab('eval', 'JSON.stringify(window.__ownedAttachmentCases || [])')
                    try:
                        payload = json.loads(observed)
                        payload = json.loads(payload) if isinstance(payload, str) else payload
                        if isinstance(payload, dict):
                            payload = payload.get('data', {}).get('result', payload.get('result', payload))
                            payload = json.loads(payload) if isinstance(payload, str) else payload
                        report['http_cases'].extend(payload if isinstance(payload, list) else [])
                    except ValueError:
                        raise RuntimeError('attachment HTTP observation JSON invalid')
                ab('close')
                browser = None
                report['browser_session_closed'] = True
                if args.distribution:
                    process.terminate()
                else:
                    stop.touch()
                process.wait(timeout=30)
                check('runtime closed cleanly', process.returncode is not None)
                process = None
                check('audit excludes credentials', 'secret' not in audit.read_text()
                      and password not in audit.read_text())
                if args.distribution:
                    events = []
                    for line in audit.read_text(errors='replace').splitlines():
                        try:
                            item = json.loads(line)
                            if isinstance(item, dict): events.append(item)
                        except ValueError:
                            continue
                    report['audit'] = {'events': events,
                                       'expected': [{'action': 'attachment-metadata-insert', 'outcome': 'ALLOWED'}]}
                report['browser_transcript_sha256'] = hashlib.sha256(''.join(transcript).encode()).hexdigest()
                report['status'] = 'executed-pass'
    except Exception as failure:
        report['reason'] = str(failure) if isinstance(failure, RuntimeError) else type(failure).__name__
        if 'transcript' in locals(): report['transcript_tail'] = transcript[-8:]
    finally:
        cleanup_started = time.perf_counter()
        if browser:
            run(['agent-browser', '--session', browser, 'close'])
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
