#!/usr/bin/env python3
"""Replay bounded equipment creation using the shipped application and synthetic Oracle."""
import argparse
import datetime
import hashlib
import json
from pathlib import Path
import re
import subprocess
import tempfile
import time
import traceback
import uuid

import sys
ROOT = Path(__file__).resolve().parents[4]
sys.path.insert(0, str(ROOT / 'scripts'))
sys.path.insert(0, str(Path(__file__).resolve().parents[4] / 'tools'))
from owned_readiness import wait_until
from vertical_slices.matrix import VERIFIER_PATHS

WEB = ROOT / 'owned-web'
IMAGE = 'ghcr.io/gvenzl/oracle-free@sha256:cd3dff6649f5d67ca3121f5e064b5a5680611e5d34f03442423110778c47dda4'
BASE = '/secure/emc/module/mdm/equipt/equipts'
GRID = '#emc-equipt-edit-list-grid'
SOURCE_SCOPES = (
    'owned-framework/src', 'owned-personnel/src', 'owned-equipment/src', 'owned-web/src',
    'scripts/build-owned-framework.sh', 'scripts/build-owned-personnel.sh',
    'scripts/build-owned-equipment.sh', 'scripts/prepare-owned-public-deps.py',
    'scripts/reject-supplier-runtime.sh', 'owned-web/build-owned-web.sh',
    'owned-web/original-personnel-sha256.json', 'owned-web/original-inventory-sha256.json',
)


def run(command, timeout=300, **kwargs):
    return subprocess.run([str(value) for value in command], capture_output=True,
                          text=True, timeout=timeout, **kwargs)


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


class Replay:
    def __init__(self, temporary):
        self.temporary = Path(temporary)
        self.name = 'lims-equipment-browser-' + uuid.uuid4().hex
        self.network = self.name + '-net'
        self.created = self.network_created = False
        self.process = self.browser = self.log_handle = None
        self.volumes = []
        self.secrets = ['secret']  # Synthetic authentication baseline only.
        self.transcript = []
        self.audit_paths = []
        self.step = 'source inventory'
        self.report = {
            'schema': 1, 'status': 'executed-fail', 'production_access': False,
            'scope': 'equipment creation through owned workbench and original HTTP resource; default-denied and isolated modes; synthetic Oracle only',
            'started_at': self.now(), 'checks': [], 'http_cases': [],
            'baseline': {'manager': 'e-1', 'low_permission': 'other',
                         'organization': 'org-a', 'visible_category': '11',
                         'hidden_category': '12', 'visible_equipment': 23,
                         'all_equipment': 24},
            'fixture': 'equipment-only-fixture', 'organization': 'org-a',
            'role': 'manager', 'mode': 'isolated',
            'cleanup': {}, 'phase_timings_ms': {},
        }

    @staticmethod
    def now():
        return datetime.datetime.now(datetime.timezone.utc).isoformat()

    def redact(self, value):
        text = str(value)
        for secret in self.secrets:
            text = text.replace(secret, '<REDACTED>')
        return re.sub(r'(?i)(password\s*[=:]\s*)[^\s,;]+', r'\1<REDACTED>', text)

    def passed(self, result, label):
        self.step = label
        if result.returncode:
            raise RuntimeError(label + ' failed: ' + self.redact((result.stdout + result.stderr)[-3000:]))
        return result.stdout

    def check(self, label, condition):
        self.step = label
        if not condition:
            raise RuntimeError(label)
        self.report['checks'].append(label)

    def sql(self, source, fixture=True):
        schema = 'ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\n' if fixture else ''
        text = ('whenever sqlerror exit failure rollback\n'
                'set pagesize 0 feedback off heading off echo off verify off linesize 32767 trimspool on\n'
                'ALTER SESSION SET CONTAINER=FREEPDB1;\n' + schema + source + '\nexit\n')
        return run(['docker', 'exec', '-i', self.name, 'sqlplus', '-s', '/ as sysdba'],
                   input=text, timeout=60)

    def query(self, source, label):
        result = self.passed(self.sql(source), label)
        if re.search(r'ORA-\d{5}', result):
            raise RuntimeError(label + ' returned Oracle error')
        return result.strip()

    def projection(self):
        return self.query("""SELECT ID||'|'||EQUIPTID||'|'||EQUIPTNAME||'|'||SPECIFICATION||'|'||FACTORYNO||'|'||MANUFACTURER||'|'||STATUS||'|'||EQUIPTCATEGORYID||'|'||USEDDEPARTMENTID||'|'||USEDDEPOSTID||'|'||SHARING||'|'||MAINTAIN||'|'||SOURCEMETHOD||'|'||TO_CHAR(SOURCEDATE,'YYYY-MM-DD')||'|'||EQUIPTBELONG||'|'||EQUIPTRECORD||'|'||MAINTAINUSERID||'|'||KEEPUSERID||'|'||ORGID FROM T_EMC_EQUIPT ORDER BY ID;""", 'Oracle baseline projection')

    def build(self, compile_modules=True):
        listing = self.passed(run(['git', 'ls-files', '-z', '--', *SOURCE_SCOPES], cwd=ROOT), 'source inventory')
        sources = [ROOT / item for item in listing.split('\0') if item]
        self.report['source_commit'] = self.passed(run(['git', 'rev-parse', 'HEAD'], cwd=ROOT), 'source commit').strip()
        self.report['source_dirty'] = bool(self.passed(run(['git', 'status', '--porcelain', '--', *sources], cwd=ROOT), 'source status').strip())
        self.report['source_sha256'] = {str(path.relative_to(ROOT)): digest(path) for path in sources}
        self.report['runner'] = str(Path(__file__).relative_to(ROOT))
        self.report['source_coverage'] = {
            'roots': list(SOURCE_SCOPES), 'files': dict(self.report['source_sha256']),
            'diff': {'added': [], 'deleted': []},
        }
        self.report['verifier_sha256'] = {
            path: digest(ROOT / path) for path in VERIFIER_PATHS
        }
        if compile_modules:
            for command in ('scripts/build-owned-personnel.sh', 'scripts/build-owned-equipment.sh', 'owned-web/build-owned-web.sh'):
                self.passed(run([ROOT / command], timeout=300), command)
            self.record_built_artifacts()

    def record_built_artifacts(self):
        self.jars = [ROOT / folder / 'target' / ('lims-' + folder + '-1.0.0.jar')
                     for folder in ('owned-framework', 'owned-personnel', 'owned-equipment', 'owned-web')]
        self.dependencies = ROOT / 'owned-framework/target/public-deps'
        self.report['artifact_sha256'] = {str(path.relative_to(ROOT)): digest(path) for path in self.jars}
        self.report['public_dependency_sha256'] = {path.name: digest(path) for path in sorted(self.dependencies.glob('*.jar'))}
        originals = {}
        for manifest in ('original-personnel-sha256.json', 'original-inventory-sha256.json'):
            originals.update(json.loads((WEB / manifest).read_text())['files'])
        self.report['business_resource_sha256'] = originals
        self.check('historical business resources retain manifest hashes', all(
            digest(WEB / 'src/main/resources' / path) == expected for path, expected in originals.items()))
        self.report['historical_source_manifest_sha256'] = digest(ROOT / 'owned-equipment/target/historical-source.sha256')
        top_level = self.passed(run(['git', 'rev-parse', '--show-toplevel'], cwd=ROOT), 'top-level checkout').strip()
        expected_project = self.passed(run(['git', 'ls-tree', self.report['source_commit'], 'lims-recovery/project'], cwd=top_level), 'historical project pointer').split()[2]
        actual_project = self.passed(run(['git', 'rev-parse', 'HEAD'], cwd=ROOT / 'project'), 'historical checkout').strip()
        self.report['historical_project_commit'] = actual_project
        self.check('historical source checkout is pinned', actual_project == expected_project)

    def database(self):
        image = json.loads(self.passed(run(['docker', 'image', 'inspect', IMAGE]), 'image inspect'))[0]
        self.check('pinned Linux amd64 Oracle image', image['Os'] == 'linux' and image['Architecture'] == 'amd64')
        self.report['image_reference'], self.report['image_id'] = IMAGE, image['Id']
        password = 'Ephemeral' + uuid.uuid4().hex[:16]
        self.secrets.append(password)
        env = self.temporary / 'oracle.env'
        env.write_text('ORACLE_PASSWORD=' + password + '\n')
        env.chmod(0o600)
        self.passed(run(['docker', 'network', 'create', '--internal', '--driver', 'bridge', self.network]), 'network create')
        self.network_created = True
        # Oracle Free needs more than 2 GiB while its compressed datafiles are
        # first expanded.  Keep the browser fixture aligned with the inventory
        # runner's 4 GiB limit so readiness does not become a host-dependent
        # false failure.
        container_id = self.passed(run(['docker', 'create', '--name', self.name, '--network', self.network,
            '--restart', 'no', '--memory', '4g', '--cpus', '2', '--env-file', env, IMAGE]), 'container create').strip()
        self.created = True
        self.passed(run(['docker', 'start', self.name]), 'container start')
        self.step = 'Oracle readiness'
        def oracle_ready():
            ready = self.sql("SELECT 'READY' FROM V$PDBS WHERE NAME='FREEPDB1' AND OPEN_MODE='READ WRITE';", fixture=False)
            logs = run(['docker', 'logs', self.name])
            return (ready.returncode == 0 and 'READY' in ready.stdout and
                    'DATABASE IS READY TO USE!' in logs.stdout + logs.stderr)

        ready = wait_until('Oracle readiness', oracle_ready, timeout=300, interval=2)
        self.report['phase_timings_ms']['oracle_readiness'] = ready['elapsed_ms']
        container = json.loads(self.passed(run(['docker', 'inspect', self.name]), 'container inspect'))[0]
        net = json.loads(self.passed(run(['docker', 'network', 'inspect', self.network]), 'network inspect'))[0]
        self.volumes = [mount['Name'] for mount in container['Mounts'] if mount['Type'] == 'volume']
        isolation = {'network_internal': net['Internal'], 'sole_endpoint': set(net['Containers']) == {container_id},
                     'attached_network_count': len(container['NetworkSettings']['Networks']),
                     'port_bindings': container['HostConfig']['PortBindings'],
                     'bind_mount_count': sum(mount['Type'] == 'bind' for mount in container['Mounts'])}
        self.report['isolation'] = isolation
        self.check('Oracle has one internal network without published ports or bind mounts',
                   isolation['network_internal'] and isolation['sole_endpoint'] and isolation['attached_network_count'] == 1
                   and not isolation['port_bindings'] and isolation['bind_mount_count'] == 0)
        password = 'Fixture' + uuid.uuid4().hex[:16]
        self.secrets.append(password)
        self.passed(self.sql('CREATE USER query_fixture IDENTIFIED BY ' + password + ';\n'
                            'GRANT CREATE SESSION, CREATE TABLE, UNLIMITED TABLESPACE TO query_fixture;', fixture=False), 'fixture schema')
        self.properties = self.temporary / 'jdbc.properties'
        self.properties.write_text('url=jdbc:oracle:thin:@//' + container['NetworkSettings']['Networks'][self.network]['IPAddress']
                                   + ':1521/FREEPDB1\nuser=query_fixture\npassword=' + password + '\n')
        self.properties.chmod(0o600)
        cp = ':'.join(map(str, [ROOT / 'owned-framework/target/classes', ROOT / 'owned-framework/target/test-classes', self.dependencies / '*']))
        seeded = self.passed(run([ROOT / 'build/jdk1.8.0_391/bin/java', '-cp', cp,
            'com.sunwayworld.framework.PersonnelQueryServiceContractTest', self.properties]), 'personnel identity seed')
        self.check('synthetic personnel baseline service and filter checks pass',
                   'personnel query service: PASS' in seeded and 'personnel filter parity: PASS' in seeded)
        self.query((WEB / 'src/test/browser/equipment-only-fixture.sql').read_text(), 'equipment fixture')
        self.baseline = self.projection()
        self.report['baseline']['projection_sha256'] = hashlib.sha256(self.baseline.encode()).hexdigest()
        self.check('equipment baseline contains 24 complete rows', len(self.baseline.splitlines()) == 24)
        self.classes = self.temporary / 'classes'
        self.classes.mkdir()
        self.runtime = ':'.join(map(str, self.jars + [self.dependencies / '*']))
        self.passed(run([ROOT / 'build/jdk1.8.0_391/bin/javac', '-source', '8', '-target', '8', '-cp', self.runtime,
            '-d', self.classes, WEB / 'src/test/java/OwnedEquipmentBrowserApplication.java']), 'browser lifecycle compile')

    def start(self, mode):
        self.mode = mode
        self.role = 'anonymous'
        self.report.setdefault('runtime_configuration', {})[mode] = {
            'equipment_write_property': 'isolated' if mode == 'isolated' else 'unset'}
        self.portfile, self.stop, self.audit_path, self.log = [self.temporary / (mode + suffix) for suffix in ('.port', '.stop', '.audit.jsonl', '.log')]
        self.audit_paths.append(self.audit_path)
        self.log_handle = self.log.open('w')
        self.process = subprocess.Popen([str(ROOT / 'build/jdk1.8.0_391/bin/java'), '-Xmx768m', '-cp', str(self.classes) + ':' + self.runtime,
            'OwnedEquipmentBrowserApplication', str(self.properties), str(self.portfile), str(self.stop), str(self.audit_path), mode],
            stdout=self.log_handle, stderr=subprocess.STDOUT, cwd=self.temporary)
        ready = wait_until(
            mode + ' application loopback readiness',
            lambda: self.portfile.exists() and self.process.poll() is None,
            timeout=180, interval=.2)
        self.report['phase_timings_ms']['application_readiness_' + mode] = ready['elapsed_ms']
        self.check(mode + ' packaged application starts on loopback', self.portfile.exists() and self.process.poll() is None)
        self.url = 'http://127.0.0.1:' + self.portfile.read_text() + '/'
        # agent-browser encodes the session name into a Unix socket path.  A
        # full UUID plus the descriptive prefix can exceed the 103-byte path
        # limit on this runner before Chrome is even contacted.  Keep the
        # diagnostic meaning in the mode and use a short random suffix.
        self.browser = 'eq-' + mode[:1] + '-' + uuid.uuid4().hex[:8]
        self.ab('open', self.url)
        self.ab('wait', '#login-submit')

    def ab(self, *command):
        self.step = self.mode + ': browser ' + command[0]
        result = run(['agent-browser', '--session', self.browser, '--json', *command], timeout=40)
        body = json.loads(self.passed(result, self.step))
        if not body.get('success'):
            raise RuntimeError(self.step + ': ' + self.redact(body.get('error')))
        self.transcript.append({'command': command[0], 'data': body.get('data')})
        return body.get('data') or {}

    def text(self, selector):
        return self.ab('get', 'text', selector)['text']

    def evaluate(self, source):
        return self.ab('eval', source)['result']

    def http(self, route, expected, label, payload=None):
        options = {'method': 'POST' if payload is not None else 'GET', 'cache': 'no-store'}
        if payload is not None:
            options.update(headers={'Content-Type': 'application/json'}, body=json.dumps(payload))
        response = self.evaluate('fetch(' + json.dumps(route) + ',' + json.dumps(options) + ').then(async r => ({status:r.status,body:await r.text()}))')
        self.report['http_cases'].append({'mode': self.mode, 'route': options['method'] + ' ' + route,
                                          'role': self.role, 'organization': 'org-a',
                                          'expected_status': expected, 'status': response['status'],
                                          'response_body': self.redact(response['body'])})
        self.check(label, response['status'] == expected)
        return response

    def login(self, user='e-1'):
        self.ab('fill', '#login-id', user)
        self.ab('fill', '#login-password', self.secrets[0])
        self.ab('click', '#login-submit')
        self.ab('wait', '--fn', "document.querySelector('#login-view').hidden === true")
        if user == 'e-1':
            self.ab('wait', '--text', '共2条')
        else:
            self.ab('wait', '--text', '当前账号没有可用的查询入口')
        self.role = 'manager' if user == 'e-1' else 'low-permission'
        self.wait_for_data_plane(user == 'e-1' and self.mode == 'isolated')

    def wait_for_data_plane(self, manager_isolated):
        """Do not open the editor until Oracle-backed capability data is ready.

        A listening Tomcat port and a successful login are insufficient: the
        equipment category query can still be transiently unavailable while
        the application finishes wiring its Oracle/Redis data plane.  Retry
        that exact request so a browser timeout is not misreported as a
        business-form failure.
        """
        expected = 200 if manager_isolated else 403
        def data_plane_ready():
            try:
                result = self.evaluate("fetch('/owned/equipment/categories',{cache:'no-store'}).then(async r=>({status:r.status,body:await r.text()}))")
                if result.get('status') == expected:
                    if expected == 200:
                        rows = json.loads(result.get('body') or 'null')
                        if not isinstance(rows, list) or not rows:
                            return False
                    return True
            except (RuntimeError, ValueError, TypeError, json.JSONDecodeError):
                return False
            return False

        ready = wait_until(
            self.mode + ' Oracle/Redis equipment data-plane readiness',
            data_plane_ready, timeout=60, interval=1)
        self.report['phase_timings_ms']['business_readiness_' + self.mode] = ready['elapsed_ms']
        self.check(self.mode + ' Oracle/Redis equipment data-plane ready', True)

    def equipment(self):
        self.ab('click', '#equipment-link')
        self.ab('wait', GRID)
        self.ab('wait', '--fn', "document.querySelector('" + GRID + " .grid-count')?.textContent === '共23条'")
        page = self.text(GRID)
        self.check(self.mode + ' first page is scoped and paginated', 'EQ-PAGE-021' in page and 'EQ-002' not in page)

    def logout(self):
        self.ab('click', '#logout')
        self.ab('wait', '--text', '已退出登录。')
        self.role = 'anonymous'
        self.check(self.mode + ' logout clears workbench', self.evaluate("document.querySelector('#owned-workspace').children.length") == 0)

    def close(self):
        if self.browser:
            self.ab('close')
            self.browser = None
        self.stop.touch()
        self.process.wait(timeout=20)
        self.check(self.mode + ' application closes cleanly', self.process.returncode == 0)
        self.process = None
        self.log_handle.close()
        self.log_handle = None
        errors = sorted(set(re.findall(r'ORA-\d{5}', self.log.read_text(errors='replace'))))
        self.report.setdefault('oracle_errors', {})[self.mode] = errors
        self.check(self.mode + ' application has no Oracle errors', not errors)

    def denied_mode(self):
        self.start('default')
        self.login()
        self.equipment()
        self.check('default mode hides creation control', '新增设备' not in self.text(GRID))
        self.http('/owned/equipment/write-available', 403, 'default mode capability denied')
        self.http('/owned/equipment/categories', 403, 'default mode categories denied')
        self.http(BASE, 403, 'default mode original POST denied', {'b': ['', [{'equiptId': 'EQ-DENIED', 'equiptName': 'Denied', 'equiptCategoryId': 11}]]})
        self.check('default mode leaves full equipment baseline unchanged', self.projection() == self.baseline)
        self.logout()
        self.close()

    def isolated_mode(self):
        self.start('isolated')
        self.login()
        self.equipment()
        self.ab('find', 'role', 'button', 'click', '--name', '下一页', '--exact')
        self.ab('wait', '--text', '第 2 / 2 页')
        page = self.text(GRID)
        self.check('second page includes final three scoped rows', all(value in page for value in ('EQ-PAGE-001', 'EQ-003', 'EQ-001')) and 'EQ-002' not in page)
        self.ab('find', 'role', 'button', 'click', '--name', '上一页', '--exact')
        self.ab('wait', '--text', '第 1 / 2 页')
        self.http('/owned/equipment/write-available', 200, 'isolated manager capability allowed')
        response = self.http('/owned/equipment/categories', 200, 'isolated manager categories allowed')
        self.check('category response excludes foreign organization', json.loads(response['body']) == [{'id': '11', 'name': 'Microscope'}])
        self.http(BASE, 403, 'cross organization category creation denied', {'b': ['', [{'equiptId': 'EQ-FOREIGN', 'equiptName': 'Foreign', 'equiptCategoryId': 12}]]})
        self.http(BASE, 400, 'missing category creation rejected', {'b': ['', [{'equiptId': 'EQ-INVALID', 'equiptName': 'Invalid'}]]})
        self.check('rejected writes leave full baseline unchanged', self.projection() == self.baseline)
        self.ab('find', 'role', 'button', 'click', '--name', '新增设备', '--exact')
        self.ab('wait', '#equipment-editor')
        self.ab('wait', '--fn', "document.querySelector('[aria-label=\"设备类别\"]')?.disabled === false")
        choices = self.evaluate("Array.from(document.querySelector('[aria-label=\"设备类别\"]').options).map(o=>({id:o.value,name:o.text}))")
        self.check('dialog offers only authorized named category with empty initial choice', choices == [{'id': '', 'name': '请选择设备类别'}, {'id': '11', 'name': 'Microscope'}])
        self.ab('fill', '[aria-label="设备编号"]', 'EQ-BROWSER')
        self.ab('fill', '[aria-label="设备名称"]', 'Browser Equipment')
        self.ab('select', '[aria-label="设备类别"]', '11')
        self.check('optional empty fields allow valid minimal form', self.evaluate("document.querySelector('#equipment-editor form').checkValidity()"))
        self.evaluate("""(() => {
          const original = window.fetch;
          window.__equipmentCreate = null;
          window.fetch = function (url, options) {
            return original.apply(this, arguments).then(async response => {
              if (new URL(url, location.origin).pathname === '/secure/emc/module/mdm/equipt/equipts' && options?.method === 'POST') {
                window.__equipmentCreate = {status: response.status, body: await response.clone().text()};
                window.fetch = original;
              }
              return response;
            });
          };
          return true;
        })()""")
        self.ab('find', 'role', 'button', 'click', '--name', '保存设备', '--exact')
        self.ab('wait', '--text', '设备已新增')
        created = self.evaluate('window.__equipmentCreate')
        self.report['http_cases'].append({'mode': self.mode, 'route': 'POST ' + BASE,
                                          'role': self.role, 'organization': 'org-a',
                                          'expected_status': 200,
                                          'status': created['status'], 'response_body': created['body'],
                                          'trigger': 'workbench-form-submit'})
        self.check('real form submission uses original POST and returns sequence identity',
                   created['status'] == 200 and int(created['body']) >= 1000)
        persisted = self.query("SELECT ID||'|'||EQUIPTCATEGORYID||'|'||ORGID||'|'||EQUIPTBELONG||'|'||EQUIPTRECORD||'|'||NVL(STATUS,'NULL')||'|'||NVL(SPECIFICATION,'NULL')||'|'||NVL(MANUFACTURER,'NULL')||'|'||NVL(FACTORYNO,'NULL') FROM T_EMC_EQUIPT WHERE EQUIPTID='EQ-BROWSER' AND EQUIPTNAME='Browser Equipment';", 'created equipment projection')
        parts = persisted.split('|')
        self.check('UI creation persists sequence identity and server organization with blank optionals',
                   len(parts) == 9 and int(parts[0]) >= 1000 and parts[1:] == ['11', 'org-a', '自有', '1', 'NULL', 'NULL', 'NULL', 'NULL'])
        self.report['created_equipment'] = {'id': int(parts[0]), 'category': parts[1], 'organization': parts[2], 'optional_fields_null': True}
        self.check('browser POST identity matches persisted equipment identity', int(created['body']) == int(parts[0]))
        seq = self.query("SELECT LAST_NUMBER FROM ALL_SEQUENCES WHERE SEQUENCE_OWNER='QUERY_FIXTURE' AND SEQUENCE_NAME='SEQ_CORE';", 'sequence advancement')
        self.check('creation advances SEQ_CORE', int(seq) > int(parts[0]))
        self.ab('fill', '[aria-label="设备名称筛选"]', 'Browser Equipment')
        self.ab('click', GRID + ' .filter-controls button')
        self.ab('wait', '--text', '共1条')
        self.check('created equipment appears in filtered list', 'EQ-BROWSER' in self.text(GRID))
        self.ab('find', 'text', 'EQ-BROWSER', 'click', '--exact')
        self.ab('wait', '--fn', "document.querySelector('output[data-field=\"equiptId\"]')?.textContent === 'EQ-BROWSER'")
        self.check('created equipment detail is readable', 'Browser Equipment' in self.text('#emc-equipt-edit-list-detail-form'))
        screenshot = self.temporary / 'created-equipment.png'
        self.ab('screenshot', screenshot)
        self.report['created_equipment_screenshot_sha256'] = digest(screenshot)
        self.query("DELETE FROM T_EMC_EQUIPT WHERE EQUIPTID='EQ-BROWSER';\nCOMMIT;", 'fixture rollback')
        self.check('fixture rollback restores every baseline equipment field', self.projection() == self.baseline)
        self.ab('click', GRID + ' .filter-controls button:last-child')
        self.ab('wait', '--text', '共23条')
        self.check('browser refresh shows restored baseline', 'EQ-BROWSER' not in self.text(GRID))
        self.report['fixture_cleanup'] = {'restored': True, 'all_rows': 24, 'visible_rows': 23, 'projection_sha256': hashlib.sha256(self.projection().encode()).hexdigest()}
        self.logout()
        self.http('/owned/equipment/categories', 403, 'logged out category query denied')
        self.http(BASE, 403, 'logged out original POST denied', {'b': ['', [{'equiptId': 'EQ-LOGOUT', 'equiptName': 'Logout', 'equiptCategoryId': 11}]]})
        self.login('other')
        self.check('low permission menu hides equipment', self.evaluate("document.querySelector('#equipment-link') === null"))
        self.http('/owned/equipment/write-available', 403, 'low permission capability denied')
        self.http('/owned/equipment/categories', 403, 'low permission categories denied')
        self.http(BASE, 403, 'low permission original POST denied', {'b': ['', [{'equiptId': 'EQ-LOW', 'equiptName': 'Low', 'equiptCategoryId': 11}]]})
        self.logout()
        self.check('all denial checks leave restored baseline unchanged', self.projection() == self.baseline)
        self.close()

    def audit(self):
        text = '\n'.join(path.read_text() for path in self.audit_paths)
        events = [json.loads(line) for line in text.splitlines() if line.strip()]
        writes = [event for event in events if event['action'] == 'equipment-insert' and event['outcome'] == 'ALLOWED']
        self.check('audit has exactly one allowed insert by authenticated manager', len(writes) == 1 and writes[0]['actorId'] == 'e-1' and writes[0]['resource'] == str(self.report['created_equipment']['id']))
        self.check('audit excludes authentication and database credentials', all(secret not in text for secret in self.secrets))
        self.report['audit'] = {'allowed_insert_count': len(writes), 'insert_events': writes, 'event_count': len(events),
                                'events': writes,
                                'expected': [{'action': 'equipment-insert', 'actorId': 'e-1',
                                              'resource': str(self.report['created_equipment']['id']),
                                              'outcome': 'ALLOWED'}],
                                'sha256': hashlib.sha256(text.encode()).hexdigest(), 'credentials_excluded': True}
        self.report['browser_transcript_sha256'] = hashlib.sha256(json.dumps(self.transcript, sort_keys=True).encode()).hexdigest()

    def diagnostics(self):
        details = {'failed_step': self.step}
        if self.browser:
            for label, command in (('snapshot', ['snapshot']), ('errors', ['errors'])):
                try:
                    result = run(['agent-browser', '--session', self.browser, *command], timeout=20)
                    details[label] = self.redact((result.stdout + result.stderr)[-12000:])
                except Exception as failure:
                    details[label] = type(failure).__name__
        if hasattr(self, 'log') and self.log.exists():
            details['application_log'] = self.redact(self.log.read_text(errors='replace')[-16000:])
        self.report['diagnostic'] = details

    def cleanup(self):
        cleanup = self.report['cleanup']
        if self.browser:
            cleanup['browser_closed'] = run(['agent-browser', '--session', self.browser, 'close'], timeout=30).returncode == 0
            self.browser = None
        else:
            cleanup['browser_closed'] = True
        if self.process and self.process.poll() is None:
            self.process.terminate()
            try:
                self.process.wait(timeout=10)
            except subprocess.TimeoutExpired:
                self.process.kill()
                self.process.wait(timeout=10)
        cleanup['application_stopped'] = self.process is None or self.process.poll() is not None
        if self.log_handle:
            self.log_handle.close()
        if self.created:
            cleanup['container_removed'] = run(['docker', 'rm', '--force', '--volumes', self.name]).returncode == 0 and run(['docker', 'container', 'inspect', self.name]).returncode != 0
            cleanup['anonymous_volumes_removed'] = all(run(['docker', 'volume', 'inspect', volume]).returncode != 0 for volume in self.volumes)
        else:
            cleanup['container'] = 'not-created'
        if self.network_created:
            cleanup['network_removed'] = run(['docker', 'network', 'rm', self.network]).returncode == 0 and run(['docker', 'network', 'inspect', self.network]).returncode != 0
        else:
            cleanup['network'] = 'not-created'
        cleanup['browser_session_closed'] = cleanup.get('browser_closed', False)
        if any(value is False for value in cleanup.values()):
            self.report['status'] = 'executed-fail'
            self.report['reason'] = 'resource cleanup failed'


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--output', type=Path, default=ROOT / '.scratch/sunway-supplier-exit-api-first/archive/historical/equipment-only-browser-20261022.json')
    args = parser.parse_args()
    temporary = tempfile.TemporaryDirectory(prefix='lims-equipment-browser-private-')
    replay = Replay(temporary.name)
    try:
        replay.build()
        replay.database()
        replay.denied_mode()
        replay.isolated_mode()
        replay.audit()
        replay.report['status'] = 'executed-pass'
    except Exception as failure:
        replay.report['reason'] = replay.redact(str(failure))
        replay.report['diagnostic_traceback'] = replay.redact(traceback.format_exc())
        replay.diagnostics()
    finally:
        replay.cleanup()
        temporary.cleanup()
        replay.report['cleanup']['private_temporary_directory_removed'] = not Path(temporary.name).exists()
        replay.report['cleanup']['temporary_files_removed'] = replay.report['cleanup']['private_temporary_directory_removed']
        replay.report['finished_at'] = replay.now()
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(replay.redact(json.dumps(replay.report, ensure_ascii=False, indent=2)) + '\n')
    print('equipment-only browser: %s (%d checks)' % (replay.report['status'], len(replay.report['checks'])))
    return 0 if replay.report['status'] == 'executed-pass' else 1


if __name__ == '__main__':
    raise SystemExit(main())
