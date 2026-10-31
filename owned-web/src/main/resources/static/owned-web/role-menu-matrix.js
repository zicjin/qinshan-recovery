(function (global) {
  'use strict';
  var HEADERS = '/secure/core/module/sys/menu-roles/queries/matrix-headers';
  var ROWS = '/secure/core/module/sys/menu-roles/queries/matrix-rows';
  var WRITE = '/secure/core/module/sys/menu-roles/instant';
  var revision = 0;
  function request(url, options) {
    return fetch(url, Object.assign({ credentials: 'same-origin', cache: 'no-store', headers: { Accept: 'application/json' } }, options || {}))
      .then(function (response) { return response.text().then(function (body) {
        var value = null; try { value = body ? JSON.parse(body) : null; } catch (ignored) { }
        if (!response.ok) { var error = new Error(value && value.message ? value.message : '角色菜单操作失败（HTTP ' + response.status + '）'); error.status = response.status; throw error; }
        return value;
      }); });
  }
  function state(value) { return value === '1' || value === 1 ? '1' : value === '2' || value === 2 ? '2' : '0'; }
  function cellId(menuId, field) { return 'role-menu-cell-' + String(menuId).replace(/[^A-Za-z0-9_-]/g, '_') + '-' + String(field).replace(/[^A-Za-z0-9_-]/g, '_'); }
  function render(target, headers, model, load) {
    target.replaceChildren();
    var card = document.createElement('section'); card.className = 'owned-card role-menu-matrix';
    var title = document.createElement('h1'); title.textContent = '角色菜单矩阵'; card.appendChild(title);
    var note = document.createElement('p'); note.className = 'role-menu-note'; note.textContent = '勾选或取消菜单授权后会立即保存；父节点的半选状态表示子菜单尚未完全授权。'; card.appendChild(note);
    var table = document.createElement('table'), head = document.createElement('thead'), tr = document.createElement('tr');
    ['菜单', '地址'].forEach(function (label) { var th = document.createElement('th'); th.textContent = label; tr.appendChild(th); });
    headers.forEach(function (header) { var th = document.createElement('th'); th.textContent = header.title || header.roleId || header.field; th.setAttribute('data-role-field', header.field); tr.appendChild(th); });
    head.appendChild(tr); table.appendChild(head);
    var body = document.createElement('tbody');
    function appendNode(node, depth) {
      var row = document.createElement('tr'); row.setAttribute('data-menu-id', node.id);
      var name = document.createElement('td'); name.className = 'role-menu-name'; name.style.paddingLeft = (8 + depth * 18) + 'px'; name.textContent = node.menuname || node.id; row.appendChild(name);
      var href = document.createElement('td'); href.textContent = node.href || ''; row.appendChild(href);
      headers.forEach(function (header) {
        var td = document.createElement('td'), value = state(node[header.field]);
        {
          var checkbox = document.createElement('input'); checkbox.type = 'checkbox'; checkbox.id = cellId(node.id, header.field); checkbox.checked = value === '1'; checkbox.indeterminate = value === '2';
          checkbox.setAttribute('data-role-field', header.field); checkbox.setAttribute('data-menu-id', node.id); checkbox.setAttribute('aria-label', (node.menuname || node.id) + ' · ' + (header.title || header.roleId || header.field));
          checkbox.addEventListener('change', function () {
            var expected = checkbox.checked ? '1' : '0', current = ++revision; checkbox.disabled = true;
            var payload = { id: node.id }; payload[header.field] = expected;
            request(WRITE, { method: 'PUT', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ p: payload }) })
              .then(function () { if (current === revision) { global.OwnedUI.notify('角色菜单已保存', false); return load(); } })
              .catch(function (error) { checkbox.checked = value === '1'; checkbox.indeterminate = value === '2'; global.OwnedUI.notify(error.message, true); })
              .finally(function () { checkbox.disabled = false; });
          });
          td.appendChild(checkbox);
        }
        row.appendChild(td);
      });
      body.appendChild(row); (node.children || []).forEach(function (child) { appendNode(child, depth + 1); });
    }
    (model.rows || []).forEach(function (node) { appendNode(node, 0); });
    table.appendChild(body); card.appendChild(table); target.appendChild(card);
  }
  function init(target) {
    revision++;
    function load() {
      return Promise.all([request(HEADERS, { method: 'POST', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: '{}' }), request(ROWS, { method: 'POST', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: '{}' })])
        .then(function (values) { render(target, values[0] || [], values[1] || {}, load); })
        .catch(function (error) { global.OwnedUI.notify(error.message, true); });
    }
    return load();
  }
  global.OwnedRoleMenuMatrix = Object.freeze({ init: init });
})(window);
