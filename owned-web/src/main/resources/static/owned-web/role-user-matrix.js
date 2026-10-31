(function (global) {
  'use strict';
  var HEADERS = '/secure/core/module/sys/role-users/queries/matrix-headers';
  var ROWS = '/secure/core/module/sys/role-users/queries/matrix-rows';
  var WRITE = '/secure/core/module/sys/role-users/instant';
  var revision = 0;
  function request(url, options) {
    return fetch(url, Object.assign({ credentials: 'same-origin', cache: 'no-store', headers: { Accept: 'application/json' } }, options || {}))
      .then(function (response) { return response.text().then(function (body) {
        var value = null; try { value = body ? JSON.parse(body) : null; } catch (ignored) { }
        if (!response.ok) { var error = new Error(value && value.message ? value.message : '角色用户操作失败（HTTP ' + response.status + '）'); error.status = response.status; throw error; }
        return value;
      }); });
  }
  function cellId(userId, field) { return 'role-user-cell-' + String(userId).replace(/[^A-Za-z0-9_-]/g, '_') + '-' + String(field).replace(/[^A-Za-z0-9_-]/g, '_'); }
  function render(target, headers, model, load) {
    target.replaceChildren();
    var card = document.createElement('section'); card.className = 'owned-card role-user-matrix';
    var title = document.createElement('h1'); title.textContent = '角色用户矩阵'; card.appendChild(title);
    var note = document.createElement('p'); note.className = 'role-menu-note'; note.textContent = '勾选或取消用户角色后会立即保存；所有变更仍受组织范围和隔离写入门闸保护。'; card.appendChild(note);
    var table = document.createElement('table'), head = document.createElement('thead'), tr = document.createElement('tr');
    ['用户', '组织'].forEach(function (label) { var th = document.createElement('th'); th.textContent = label; tr.appendChild(th); });
    headers.forEach(function (header) { var th = document.createElement('th'); th.textContent = header.title || header.roleId || header.field; th.setAttribute('data-role-field', header.field); tr.appendChild(th); });
    head.appendChild(tr); table.appendChild(head);
    var body = document.createElement('tbody');
    (model.rows || []).forEach(function (node) {
      var row = document.createElement('tr'); row.setAttribute('data-user-id', node.id);
      var user = document.createElement('td'); user.textContent = node.username || node.userId || node.id; row.appendChild(user);
      var org = document.createElement('td'); org.textContent = node.orgId || ''; row.appendChild(org);
      headers.forEach(function (header) {
        var td = document.createElement('td'), checkbox = document.createElement('input');
        checkbox.type = 'checkbox'; checkbox.id = cellId(node.id, header.field); checkbox.checked = String(node[header.field]) === '1';
        checkbox.setAttribute('data-role-field', header.field); checkbox.setAttribute('data-user-id', node.id);
        checkbox.setAttribute('aria-label', (node.username || node.id) + ' · ' + (header.title || header.roleId || header.field));
        checkbox.addEventListener('change', function () {
          var expected = checkbox.checked ? '1' : '0', current = ++revision; checkbox.disabled = true;
          var payload = { id: node.id }; payload[header.field] = expected;
          request(WRITE, { method: 'PUT', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ p: payload }) })
            .then(function () { if (current === revision) { global.OwnedUI.notify('角色用户已保存', false); return load(); } })
            .catch(function (error) { checkbox.checked = String(node[header.field]) === '1'; global.OwnedUI.notify(error.message, true); })
            .finally(function () { checkbox.disabled = false; });
        });
        td.appendChild(checkbox); row.appendChild(td);
      });
      body.appendChild(row);
    });
    table.appendChild(body); card.appendChild(table); target.appendChild(card);
  }
  function init(target) {
    revision++;
    function load() {
      return Promise.all([
        request(HEADERS, { method: 'POST', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: '{}' }),
        request(ROWS, { method: 'POST', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: '{}' })
      ]).then(function (values) { render(target, values[0] || [], values[1] || {}, load); })
        .catch(function (error) { global.OwnedUI.notify(error.message, true); });
    }
    return load();
  }
  global.OwnedRoleUserMatrix = Object.freeze({ init: init });
})(window);
