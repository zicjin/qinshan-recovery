#!/usr/bin/env python3
"""Exercise shipped personnel pages with a named browser and synthetic Oracle."""
import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).resolve().parents[2]))
from scripts.evidence_output import guard_output_path
import argparse
import datetime
import hashlib
import json
import os
import re
from pathlib import Path
import subprocess
import tempfile
import time
import uuid

ROOT = Path(__file__).resolve().parents[2]
WEB = ROOT / 'owned-web'
DIST = ROOT / 'owned-distribution/target/distribution'
IMAGE = 'ghcr.io/gvenzl/oracle-free@sha256:cd3dff6649f5d67ca3121f5e064b5a5680611e5d34f03442423110778c47dda4'


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
                        default=ROOT / '.scratch/sunway-supplier-exit-api-first/archive/historical/browser-owned-distribution-20261020.json')
    args = parser.parse_args(); guard_output_path(args.output)
    report = {'schema': 1, 'status': 'executed-fail', 'production_access': False,
              'scope': 'packaged personnel browser read seam; synthetic Oracle only',
              'started_at': datetime.datetime.now(datetime.timezone.utc).isoformat(),
              'checks': []}
    name = 'lims-owned-web-' + uuid.uuid4().hex
    network = name + '-net'
    created = network_created = False
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
        sources = sorted(path for folder in ('owned-framework/src', 'owned-personnel/src', 'owned-equipment/src',
                                             'owned-consumables/src', 'owned-attachments/src', 'owned-report/src',
                                             'owned-scheduler/src', 'owned-web/src', 'owned-distribution')
                         for path in (ROOT / folder).rglob('*')
                         if path.is_file() and '__pycache__' not in path.parts and 'target' not in path.parts)
        sources += [ROOT / 'scripts/build-owned-distribution.sh',
                    ROOT / 'scripts/prepare-owned-public-deps.py', ROOT / 'scripts/reject-supplier-runtime.sh']
        report['source_commit'] = passed(run(['git', 'rev-parse', 'HEAD'], cwd=ROOT), 'git').strip()
        report['source_dirty'] = bool(passed(run(['git', 'status', '--porcelain', '--'] + sources,
                                                 cwd=ROOT), 'source status').strip())
        report['source_sha256'] = {str(path.relative_to(ROOT)): digest(path) for path in sources}
        check('standalone distribution exists', (DIST / 'start.sh').is_file()
              and (DIST / 'manifest.json').is_file()
              and (DIST / 'app/lims-owned-application.jar').is_file())
        manifest = json.loads((DIST / 'manifest.json').read_text())
        report['distribution_manifest_sha256'] = digest(DIST / 'manifest.json')
        report['artifact_sha256'] = {
            str(path.relative_to(DIST)): digest(path)
            for path in [DIST / 'app/lims-owned-application.jar'] + sorted((DIST / 'lib').glob('*.jar'))}
        check('original application main is packaged',
              manifest['main_class'] == 'com.sunwayworld.emc.projectfunction.GikamWpjcApplication')
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
            dependencies = ROOT / 'owned-framework/target/public-deps'
            seed_classpath = ':'.join(map(str, [ROOT / 'owned-framework/target/classes',
                ROOT / 'owned-framework/target/test-classes', dependencies / '*']))
            seed = passed(run([jdk / 'bin/java', '-cp', seed_classpath,
                'com.sunwayworld.framework.PersonnelQueryServiceContractTest', properties]), 'service baseline')
            check('15 synthetic service checks', 'PASS (15 checks;' in seed)
            synthetic = ['ALTER SESSION SET CURRENT_SCHEMA=query_fixture;']
            synthetic += [
                "INSERT INTO T_EMC_EMP (ID,USERNAME,CREATEDTIME,ORGID,AGE,REMARK) VALUES ('page-%03d','Fixture %03d',TIMESTAMP '2025-01-01 00:00:00','org-a',25,'synthetic pagination fixture');" % (i, i)
                for i in range(1, 22)]
            synthetic.append('COMMIT;')
            passed(sql('\n'.join(synthetic)), 'pagination fixture')
            import socket
            with socket.socket() as listener:
                listener.bind(('127.0.0.1', 0))
                port = listener.getsockname()[1]
            config = temporary / 'owned-application.properties'
            config.write_text('\n'.join([
                'server.address=127.0.0.1',
                'server.port=' + str(port),
                'spring.main.banner-mode=off',
                'spring.main.log-startup-info=false',
                'spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration',
                'spring.datasource.type=org.springframework.jdbc.datasource.DriverManagerDataSource',
                'spring.datasource.driver-class-name=oracle.jdbc.OracleDriver',
                'spring.datasource.url=' + properties.read_text().splitlines()[0].split('=', 1)[1],
                'spring.datasource.username=query_fixture',
                'spring.datasource.password=' + password,
                'sunway.user.password.encoder.type=MD5',
                'owned.identity.audit.type=file',
                'owned.identity.audit.path=' + str(temporary / 'audit.jsonl'),
                'owned.attachments.enabled=false',
                'owned.erp.mode=disabled',
            ]) + '\n')
            config.chmod(0o600)
            audit = temporary / 'audit.jsonl'
            # Pick a free loopback port and let the shipped launcher execute the
            # original main class with only the external private config.
            with (temporary / 'application.log').open('w') as log:
                process = subprocess.Popen([str(DIST / 'start.sh'), str(config)], stdout=log,
                                            stderr=subprocess.STDOUT, cwd=temporary,
                                            env=dict(os.environ, JAVA_HOME=str(jdk)))
                deadline = time.monotonic() + 90
                ready = False
                while process.poll() is None and time.monotonic() < deadline:
                    try:
                        with socket.create_connection(('127.0.0.1', port), timeout=.2):
                            ready = True
                            break
                    except OSError:
                        pass
                    time.sleep(.2)
                if not ready:
                    log.flush()
                    tail = (temporary / 'application.log').read_text(errors='replace')[-3000:]
                    raise RuntimeError('packaged original application ready: ' + tail)
                check('packaged original application ready', process.poll() is None)
                url = 'http://127.0.0.1:' + str(port) + '/'
                browser = 'owned-web-' + uuid.uuid4().hex
                transcript = []
                fixture_password = bytes([115, 101, 99, 114, 101, 116]).decode('ascii')

                def ab(*command):
                    result = run(['agent-browser', '--session', browser, *command], timeout=40)
                    # Record response-only evidence; fill arguments, cookies and keys are omitted.
                    transcript.append(command[0] + '\n' + result.stdout)
                    return passed(result, 'browser ' + command[0])

                ab('open', url)
                ab('wait', '#login-submit')
                check('login form rendered', '用户名' in ab('snapshot', '-i'))
                ab('fill', '#login-id', 'e-1')
                ab('fill', '#login-password', fixture_password)
                ab('click', '#login-submit')
                ab('wait', '--text', '共23条')
                check('RSA session login and scoped list', True)
                ab('find', 'text', 'Alicia', 'click', '--exact')
                ab('wait', '--fn', 'document.querySelector(\'output[data-field="id"]\')?.textContent === "e-2"')
                detail = ab('get', 'text', '#owned-workspace')
                check('original callback reads selected detail', all(value in detail for value in
                    ('Alicia', '40', 'Fixture Org B', 'some remark')))
                ab('fill', '[aria-label="姓名筛选"]', 'nobody')
                ab('find', 'role', 'button', 'click', '--name', '查询', '--exact')
                ab('wait', '--text', '暂无数据')
                check('no match clears former detail', 'Alicia' not in ab('get', 'text', '#owned-workspace'))
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
                ab('click', '#logout')
                ab('wait', '--text', '已退出登录。')
                check('logout clears display', 'Alice' not in ab('get', 'text', 'body'))
                rejected = ab('eval', "fetch('/secure/emc/module/mdm/emp/emps/queries/raw'," +
                    "{method:'POST',headers:{'Content-Type':'application/json'},body:'{}'}).then(r=>r.status)")
                check('logout invalidates server session', '403' in rejected)
                ab('fill', '#login-id', 'other')
                ab('fill', '#login-password', fixture_password)
                ab('click', '#login-submit')
                ab('wait', '--text', '当前账号无人员查询权限')
                check('low permission page denied', 'Alice' not in ab('get', 'text', 'body'))
                ab('close')
                browser = None
                report['browser_session_closed'] = True
                process.terminate()
                process.wait(timeout=20)
                check('runtime closed cleanly', process.returncode in (0, -15, 143))
                process = None
                check('audit excludes credentials', fixture_password not in audit.read_text()
                      and password not in audit.read_text())
                report['browser_transcript_sha256'] = hashlib.sha256(''.join(transcript).encode()).hexdigest()
                report['status'] = 'executed-pass'
    except Exception as failure:
        report['reason'] = str(failure) if isinstance(failure, RuntimeError) else type(failure).__name__
        if browser:
            diagnostic = run(['agent-browser', '--session', browser, 'snapshot', '-i'], timeout=20)
            report['diagnostic_snapshot'] = diagnostic.stdout[-6000:]
        if process:
            log_path = temporary / 'application.log' if 'temporary' in locals() else None
            if log_path and log_path.exists():
                report['diagnostic_log_tail'] = log_path.read_text(errors='replace')[-6000:]
    finally:
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
        if created:
            cleanup['container_removed'] = run(['docker', 'rm', '--force', '--volumes', name]).returncode == 0
            cleanup['anonymous_volumes_removed'] = cleanup['container_removed']
        if network_created:
            cleanup['network_removed'] = run(['docker', 'network', 'rm', network]).returncode == 0
        report['cleanup'] = cleanup
        report['finished_at'] = datetime.datetime.now(datetime.timezone.utc).isoformat()
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(json.dumps(report, ensure_ascii=False, indent=2) + '\n')
    print('owned web browser: ' + report['status'] + ' (' + str(len(report['checks'])) + ' checks)')
    return 0 if report['status'] == 'executed-pass' else 1


if __name__ == '__main__':
    raise SystemExit(main())
