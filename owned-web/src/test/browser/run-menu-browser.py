#!/usr/bin/env python3
"""Exercise the shipped shell's navigation with a loopback HTTP contract fixture.

Identity/menu/page replies are synthetic. This tests frontend behavior only;
the Oracle/Boot runners separately verify those server contracts.
"""
import argparse
import base64
import datetime
import hashlib
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
import json
from pathlib import Path
import subprocess
import tempfile
import threading
import uuid

WEB = Path(__file__).resolve().parents[3]
STATIC = WEB / 'src/main/resources/static'


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--output', type=Path, default=WEB / 'target/menu-browser.json')
    args = parser.parse_args()
    report = {'schema': 1, 'status': 'executed-fail', 'production_access': False,
              'scope': 'shipped shell browser; synthetic loopback identity/menu/page HTTP contracts only',
              'checks': [], 'started_at': datetime.datetime.now(datetime.timezone.utc).isoformat()}
    state = {'menus': [{'id': '20251000001'}], 'requests': [], 'authenticated': False,
             'menu_status': 200, 'menu_gate': None, 'menu_started': threading.Event(),
             'matrix_value': '0', 'matrix_puts': []}
    browser = 'owned-menu-' + uuid.uuid4().hex
    fixture = """window.workspace={window:{}};
window.Gikam={reset:function(){},setMount:function(m){window.__fixtureMount=m;},setCapabilities:function(){}};
window.OwnedRequest=function(url,options){return fetch(url,options).then(function(r){return r.json().then(function(v){if(!r.ok){var e=new Error(v.message);e.status=r.status;throw e;}return v;});});};
window.emcEmp={editPage:{init:function(){__fixtureMount.textContent='Synthetic personnel page';}}};
window.emcEquipt={editPage:{init:function(){__fixtureMount.textContent='Synthetic equipment page';}}};
window.emcConsume={editPage:{init:function(){__fixtureMount.textContent='Synthetic consumables page';}}};
window.emcHomeAttachment={downloadListPage:{init:function(){__fixtureMount.textContent='Synthetic attachment page';}}};"""
    pages = {'/secure/emc/module/mdm/emp/emps/page/edit-list': 'emcEmp.editPage.init',
             '/secure/emc/module/mdm/equipt/equipts/page/edit-list': 'emcEquipt.editPage.init',
             '/secure/emc/module/item/consume/consumes/page/edit-list': 'emcConsume.editPage.init',
             '/secure/emc/module/index/workspace/download-attachment': 'emcHomeAttachment.downloadListPage.init'}

    with tempfile.TemporaryDirectory(prefix='owned-menu-browser-') as temporary:
        temporary = Path(temporary)
        key = temporary / 'private.pem'
        subprocess.run(['openssl', 'genpkey', '-algorithm', 'RSA', '-pkeyopt', 'rsa_keygen_bits:1024', '-out', key],
                       check=True, capture_output=True)
        public = base64.b64encode(subprocess.check_output(['openssl', 'pkey', '-in', key, '-pubout', '-outform', 'DER'])).decode()

        class Handler(BaseHTTPRequestHandler):
            def log_message(self, *_):
                pass

            def reply(self, status, content, content_type='application/json'):
                self.send_response(status)
                self.send_header('Content-Type', content_type)
                self.send_header('Cache-Control', 'no-store')
                self.end_headers()
                self.wfile.write(content.encode())

            def do_POST(self):
                body = self.rfile.read(int(self.headers.get('Content-Length', '0')))
                if self.path == '/core/module/sys/login':
                    state['authenticated'] = True
                    self.reply(200, '{"authenticated":true}')
                elif self.path == '/core/module/sys/logout':
                    state['authenticated'] = False
                    self.reply(200, '{"authenticated":false}')
                elif self.path == '/secure/core/module/sys/menu-roles/queries/matrix-headers':
                    if not state['authenticated'] or not any(str(item.get('id')) == '100900' for item in state['menus']):
                        self.reply(403, '{"message":"GIKAM.PERMISSION.UNAUTHORIZED"}')
                    else:
                        self.reply(200, '[{"field":"T_CORE_ROLE$ROLENAME$5","title":"Manager","roleId":"5"}]')
                elif self.path == '/secure/core/module/sys/menu-roles/queries/matrix-rows':
                    if not state['authenticated'] or not any(str(item.get('id')) == '100900' for item in state['menus']):
                        self.reply(403, '{"message":"GIKAM.PERMISSION.UNAUTHORIZED"}')
                    else:
                        rows = {'total': 2, 'pageNumber': 1, 'pageSize': 2, 'rows': [{
                            'id': '100900', 'menuname': 'Role Menu Matrix', 'href': '/menu-roles',
                            'children': [{'id': '100901', 'menuname': 'Matrix Child', 'href': '/menu-roles/child',
                                          'children': [], 'T_CORE_ROLE$ROLENAME$5': state['matrix_value']}]
                        }]}
                        self.reply(200, json.dumps(rows))
                elif self.path == '/secure/core/module/sys/menu-roles/instant':
                    if not state['authenticated'] or not any(str(item.get('id')) == '100900' for item in state['menus']):
                        self.reply(403, '{"message":"GIKAM.PERMISSION.UNAUTHORIZED"}')
                    else:
                        payload = json.loads(body.decode() or '{}').get('p', {})
                        value = payload.get('T_CORE_ROLE$ROLENAME$5')
                        state['matrix_value'] = str(value)
                        state['matrix_puts'].append(state['matrix_value'])
                        self.reply(200, '{"updated":1}')
                else:
                    self.reply(404, '{}')

            do_PUT = do_POST

            def do_GET(self):
                path = self.path.split('?', 1)[0]
                state['requests'].append(path)
                if path == '/open/security/public-key':
                    self.reply(200, public, 'text/plain')
                elif path == '/secure/core/module/sys/menus':
                    reply = json.dumps({'menus': state['menus']})
                    status = state['menu_status'] if state['authenticated'] else 403
                    gate = state['menu_gate']
                    if gate:
                        state['menu_started'].set()
                        gate.wait(20)
                    self.reply(status, reply if status == 200 else '{"message":"MENU_UNAVAILABLE"}')
                elif path in pages:
                    self.reply(200, '<script>' + pages[path] + '();</script>', 'text/html')
                elif path == '/':
                    self.reply(200, (STATIC / 'index.html').read_text(), 'text/html')
                elif path == '/owned-web/owned-gikam.js' or path.startswith('/emc/') or path.startswith('/static/emc/'):
                    self.reply(200, fixture, 'application/javascript')
                elif path.startswith('/owned-web/'):
                    resource = STATIC / path.lstrip('/')
                    self.reply(200, resource.read_text(), 'text/css' if path.endswith('.css') else 'application/javascript')
                else:
                    self.reply(200, '', 'application/javascript')

        server = ThreadingHTTPServer(('127.0.0.1', 0), Handler)
        worker = threading.Thread(target=server.serve_forever, daemon=True)
        worker.start()

        def ab(*command):
            result = subprocess.run(['agent-browser', '--session', browser, *command], text=True,
                                    capture_output=True, timeout=35)
            if result.returncode:
                raise RuntimeError('browser ' + command[0] + ': ' + result.stderr[-500:])
            return result.stdout

        def check(name, condition):
            if not condition:
                raise RuntimeError(name)
            report['checks'].append(name)

        def login():
            ab('fill', '#login-id', 'synthetic')
            ab('fill', '#login-password', 'fixture-only')
            ab('click', '#login-submit')

        try:
            report['source_commit'] = subprocess.check_output(['git', 'rev-parse', 'HEAD'], cwd=WEB, text=True).strip()
            source_paths = [STATIC / 'index.html', STATIC / 'owned-web/app.js', STATIC / 'owned-web/role-menu-matrix.js', STATIC / 'owned-web/owned.css', Path(__file__)]
            report['source_sha256'] = {str(p.relative_to(WEB)): hashlib.sha256(p.read_bytes()).hexdigest() for p in source_paths}
            report['source_dirty'] = bool(subprocess.check_output(['git', 'status', '--porcelain', '--'] + [str(p) for p in source_paths], cwd=WEB, text=True).strip())
            ab('open', 'http://127.0.0.1:' + str(server.server_port) + '/')
            ab('wait', '#login-submit')
            login()
            ab('wait', '--text', 'Synthetic equipment page')
            tree = ab('snapshot', '-i')
            check('only granted query link is visible', '设备查询' in tree and '人员查询' not in tree and '耗材查询' not in tree)
            check('equipment-only grant chooses supported equipment page',
                  '/secure/emc/module/mdm/emp/emps/page/edit-list' not in state['requests'])
            # A menu response that belongs to the old session must not reopen a
            # page after the user has explicitly logged out.
            state['menu_gate'] = threading.Event()
            state['menu_started'].clear()
            ab('click', '#equipment-link')
            if not state['menu_started'].wait(5):
                raise RuntimeError('delayed menu request did not start')
            ab('click', '#logout')
            ab('wait', '--text', '已退出登录。')
            state['menu_gate'].set()
            ab('wait', '--fn', "document.querySelector('#login-view:not([hidden])') !== null")
            check('late menu response cannot reopen page after logout',
                  'Synthetic equipment page' not in ab('get', 'text', 'body'))
            state['menu_gate'] = None
            login()
            ab('wait', '--text', 'Synthetic equipment page')
            state['menus'] = [{'id': '100001'}]
            before = state['requests'].count('/secure/emc/module/mdm/equipt/equipts/page/edit-list')
            ab('click', '#equipment-link')
            ab('wait', '--text', '当前账号无设备查询权限。')
            check('navigation refreshes grants before fetching former page',
                  state['requests'].count('/secure/emc/module/mdm/equipt/equipts/page/edit-list') == before)
            check('revoked page is cleared and newly permitted link shown',
                  'Synthetic equipment page' not in ab('get', 'text', 'body') and '人员查询' in ab('snapshot', '-i'))
            state['menus'] = []
            ab('click', '#logout')
            ab('wait', '--text', '已退出登录。')
            login()
            ab('wait', '--fn', "document.querySelector('#page-status')?.textContent.includes('当前账号没有可用的查询入口')")
            tree = ab('snapshot', '-i')
            check('empty menu grant keeps session and closes query entries',
                  '退出' in tree and '附件下载' in tree and '人员查询' not in tree and '设备查询' not in tree and '耗材查询' not in tree)
            ab('click', '#logout')
            ab('wait', '--text', '已退出登录。')
            state['menu_status'] = 500
            login()
            ab('wait', '--text', '无法读取当前账号菜单')
            tree = ab('snapshot', '-i')
            check('menu failure closes query links and preserves session',
                  '退出' in tree and '附件下载' in tree and '人员查询' not in tree and '设备查询' not in tree and '耗材查询' not in tree)
            state['menu_status'] = 200
            state['menus'] = [{'id': '100900'}]
            ab('click', '#logout')
            ab('wait', '--text', '已退出登录。')
            login()
            ab('wait', '--text', '角色菜单矩阵')
            tree = ab('snapshot', '-i')
            check('role-menu page is visible only with explicit administration menu',
                  '角色菜单矩阵' in tree and 'Manager' in tree and 'Matrix Child' in tree)
            ab('click', '#role-menu-cell-100901-T_CORE_ROLE_ROLENAME_5')
            ab('wait', '--text', '角色菜单已保存')
            check('role-menu grant uses instant-save route', state['matrix_value'] == '1' and state['matrix_puts'] == ['1'])
            ab('click', '#role-menu-cell-100901-T_CORE_ROLE_ROLENAME_5')
            ab('wait', '--text', '角色菜单已保存')
            check('role-menu revoke uses instant-save route', state['matrix_value'] == '0' and state['matrix_puts'] == ['1', '0'])
            state['menus'] = [{'id': '100001'}]
            ab('click', '#logout')
            ab('wait', '--text', '已退出登录。')
            login()
            ab('wait', '--text', 'Synthetic personnel page')
            check('role-menu link disappears after administration grant revocation',
                  '角色菜单矩阵' not in ab('snapshot', '-i'))
            report['status'] = 'executed-pass'
            report['role'] = 'role-admin'
            report['organization'] = 'org-a'
            report['mode'] = 'isolated'
            report['fixture'] = 'role-menu-browser-fixture'
            report['http_cases'] = [
                {'route': 'GET /?page=roleMenuMatrix', 'role': 'role-admin', 'organization': 'org-a', 'mode': 'isolated', 'status': 200, 'expected_status': 200},
                {'route': 'PUT /secure/core/module/sys/menu-roles/instant', 'role': 'role-admin', 'organization': 'org-a', 'mode': 'isolated', 'status': 200, 'expected_status': 200, 'label': 'grant'},
                {'route': 'PUT /secure/core/module/sys/menu-roles/instant', 'role': 'role-admin', 'organization': 'org-a', 'mode': 'isolated', 'status': 200, 'expected_status': 200, 'label': 'revoke'},
            ]
            report['audit'] = {'events': [{'action': 'ui-write-request', 'actorId': 'role-admin', 'resource': 'role-menu.write', 'outcome': 'ALLOWED'}], 'expected': []}
        except Exception as failure:
            report['reason'] = str(failure)
            report['requests'] = list(state['requests'])
            report['status_text'] = ab('get', 'text', '#page-status')
            report['diagnostic_snapshot'] = ab('snapshot', '-i')
            report['console'] = ab('console', '--json')
            report['errors'] = ab('errors', '--json')
        finally:
            if state['menu_gate']:
                state['menu_gate'].set()
            ab('close')
            server.shutdown()
            server.server_close()
            worker.join(timeout=5)
            report['browser_session_closed'] = True
            report.setdefault('cleanup', {}).update(browser_session_closed=True, temporary_files_removed=True)
            report['finished_at'] = datetime.datetime.now(datetime.timezone.utc).isoformat()
            args.output.parent.mkdir(parents=True, exist_ok=True)
            args.output.write_text(json.dumps(report, ensure_ascii=False, indent=2) + '\n')
    print('owned menu browser: ' + report['status'] + ' (' + str(len(report['checks'])) + ' checks)')
    return 0 if report['status'] == 'executed-pass' else 1


if __name__ == '__main__':
    raise SystemExit(main())
