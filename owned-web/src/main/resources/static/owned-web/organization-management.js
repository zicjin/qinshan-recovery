(function (global) {
  'use strict';
  var QUERY = '/secure/core/module/mdm/orgs/queries/raw';
  var BASE = '/secure/core/module/mdm/orgs';
  function request(url, options) {
    return fetch(url, Object.assign({ credentials: 'same-origin', cache: 'no-store', headers: { Accept: 'application/json' } }, options || {}))
      .then(function (response) { return response.text().then(function (body) {
        var value = null; try { value = body ? JSON.parse(body) : null; } catch (ignored) { }
        if (!response.ok) { throw new Error(value && value.message ? value.message : '组织操作失败（HTTP ' + response.status + '）'); }
        return value;
      }); });
  }
  function input(label, value, id) {
    var field = document.createElement('label'); field.className = 'owned-field'; field.textContent = label;
    var box = document.createElement('input'); box.type = 'text'; box.value = value || ''; box.setAttribute('aria-label', label); if (id) box.id = id;
    field.appendChild(box); return { field: field, box: box };
  }
  function render(target, model, load) {
    target.replaceChildren();
    var card = document.createElement('section'); card.className = 'owned-card organization-management';
    var title = document.createElement('h1'); title.textContent = '组织管理'; card.appendChild(title);
    var note = document.createElement('p'); note.textContent = '组织新增、修改和删除受 role-admin、组织引用检查及 isolated 写入门闸保护。'; card.appendChild(note);
    var add = document.createElement('form'); add.className = 'owned-inline-form';
    var id = input('组织编码', '', 'organization-id'), name = input('组织名称', '', 'organization-name'), parent = input('父组织编码', '', 'organization-parent');
    add.appendChild(id.field); add.appendChild(name.field); add.appendChild(parent.field);
    var save = document.createElement('button'); save.type = 'submit'; save.textContent = '新增组织'; add.appendChild(save);
    add.addEventListener('submit', function (event) { event.preventDefault(); save.disabled = true;
      request(BASE, { method: 'POST', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ p: { id: id.box.value.trim(), orgName: name.box.value.trim(), parentId: parent.box.value.trim() } }) })
        .then(function () { global.OwnedUI.notify('组织已新增', false); id.box.value = ''; name.box.value = ''; parent.box.value = ''; return load(); })
        .catch(function (error) { global.OwnedUI.notify(error.message, true); }).finally(function () { save.disabled = false; });
    });
    card.appendChild(add);
    var table = document.createElement('table'), head = document.createElement('thead'), row = document.createElement('tr');
    ['组织编码', '组织名称', '父组织', '操作'].forEach(function (label) { var th = document.createElement('th'); th.textContent = label; row.appendChild(th); });
    head.appendChild(row); table.appendChild(head);
    var body = document.createElement('tbody');
    (model.rows || []).forEach(function (item) {
      var tr = document.createElement('tr'); tr.setAttribute('data-organization-id', item.id);
      [item.id, item.orgName, item.parentId || ''].forEach(function (value) { var td = document.createElement('td'); td.textContent = value == null ? '' : value; tr.appendChild(td); });
      var actions = document.createElement('td'), edit = document.createElement('button'); edit.type = 'button'; edit.textContent = '修改名称'; edit.setAttribute('aria-label', '修改组织 ' + item.id);
      edit.addEventListener('click', function () {
        var next = window.prompt('组织名称', item.orgName || ''); if (next == null || !next.trim()) return;
        edit.disabled = true; request(BASE + '/' + encodeURIComponent(item.id), { method: 'PUT', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ p: { id: item.id, orgName: next.trim() } }) })
          .then(function () { global.OwnedUI.notify('组织已修改', false); return load(); }).catch(function (error) { global.OwnedUI.notify(error.message, true); }).finally(function () { edit.disabled = false; });
      });
      var remove = document.createElement('button'); remove.type = 'button'; remove.textContent = '删除'; remove.setAttribute('aria-label', '删除组织 ' + item.id);
      remove.addEventListener('click', function () { if (!window.confirm('确认删除组织 ' + item.id + '？')) return; remove.disabled = true;
        request(BASE, { method: 'DELETE', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ ids: [item.id] }) })
          .then(function () { global.OwnedUI.notify('组织已删除', false); return load(); }).catch(function (error) { global.OwnedUI.notify(error.message, true); }).finally(function () { remove.disabled = false; });
      });
      actions.appendChild(edit); actions.appendChild(remove); tr.appendChild(actions); body.appendChild(tr);
    });
    table.appendChild(body); card.appendChild(table); target.appendChild(card);
  }
  function init(target) {
    function load() { return request(QUERY, { method: 'POST', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({}) }).then(function (model) { render(target, model || {}, load); }); }
    return load().catch(function (error) { global.OwnedUI.notify(error.message, true); });
  }
  global.OwnedOrganizationManagement = Object.freeze({ init: init });
})(window);
