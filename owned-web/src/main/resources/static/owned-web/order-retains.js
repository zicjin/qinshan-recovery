(function (global) {
  'use strict';
  var QUERY = '/secure/core/module/bp/order/order-retains/queries/raw';
  var WRITE = '/secure/core/module/bp/order/order-retains';
  function parse(response, fallback) {
    return response.text().then(function (text) {
      var value = null; try { value = text ? JSON.parse(text) : null; } catch (ignored) { }
      if (!response.ok) throw new Error(value && value.message ? value.message : fallback + '（HTTP ' + response.status + '）');
      return value || {};
    });
  }
  function request(body) {
    return fetch(QUERY, { method: 'POST', credentials: 'same-origin', cache: 'no-store', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ p: body || {} }) }).then(function (response) { return parse(response, '留样查询失败'); });
  }
  function write(body) {
    return fetch(WRITE, { method: 'POST', credentials: 'same-origin', cache: 'no-store', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ p: body }) }).then(function (response) { return parse(response, '留样写入失败'); });
  }
  function remove(id) {
    return fetch(WRITE + '/' + encodeURIComponent(id), { method: 'DELETE', credentials: 'same-origin', cache: 'no-store', headers: { Accept: 'application/json' } }).then(function (response) { return parse(response, '留样删除失败'); });
  }
  function handle(id, record) {
    return fetch(WRITE + '/action/handle', { method: 'POST', credentials: 'same-origin', cache: 'no-store', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ p: { b: ['', [{ id: id, handleRecord: record || '' }]] } }) }).then(function (response) { return parse(response, '留样处置失败'); });
  }
  function init(target) {
    target.replaceChildren();
    var card = document.createElement('section'); card.className = 'owned-card order-retains';
    var title = document.createElement('h1'); title.textContent = '留样记录'; card.appendChild(title);
    var form = document.createElement('form'); form.className = 'owned-inline-form';
    function field(id, label) { var input = document.createElement('input'); input.id = id; input.setAttribute('aria-label', label); input.placeholder = label; return input; }
    var input = field('order-retain-order-no', '订单编号'); var query = document.createElement('button'); query.type = 'submit'; query.textContent = '查询'; form.appendChild(input); form.appendChild(query);
    var create = document.createElement('button'); create.type = 'button'; create.id = 'order-retain-create'; create.textContent = '新增留样'; form.appendChild(create); card.appendChild(form);
    var editor = document.createElement('div'); editor.id = 'order-retain-editor'; editor.hidden = true;
    var orderId = field('order-retain-order-id', '订单ID'); var projectId = field('order-retain-project-id', '项目ID'); var folderId = field('order-retain-folder-id', '点位ID'); var qty = field('order-retain-qty', '留样数量'); var days = field('order-retain-days', '留样天数'); var location = field('order-retain-location', '留样位置');
    [orderId, projectId, folderId, qty, days, location].forEach(function (item) { editor.appendChild(item); });
    var save = document.createElement('button'); save.type = 'button'; save.id = 'order-retain-save'; save.textContent = '保存留样'; editor.appendChild(save); card.appendChild(editor);
    var table = document.createElement('table'); table.id = 'order-retains-table'; card.appendChild(table); target.appendChild(card);
    var handleEditor = document.createElement('div'); handleEditor.id = 'order-retain-handle-editor'; handleEditor.hidden = true;
    var handleRecord = field('order-retain-handle-record', '处置记录'); var handleSave = document.createElement('button'); handleSave.type = 'button'; handleSave.id = 'order-retain-handle-save'; handleSave.textContent = '保存处置'; handleEditor.appendChild(handleRecord); handleEditor.appendChild(handleSave); card.insertBefore(handleEditor, table); var handleId = null;
    function render(model) {
      table.replaceChildren(); var head = document.createElement('tr');
      ['记录编号', '订单编号', '组织', '数量', '位置', '操作'].forEach(function (label) { var cell = document.createElement('th'); cell.textContent = label; head.appendChild(cell); });
      var thead = document.createElement('thead'); thead.appendChild(head); table.appendChild(thead); var body = document.createElement('tbody');
      (model.rows || []).forEach(function (row) {
        var tr = document.createElement('tr'); tr.setAttribute('data-order-retain-id', row.id);
        [row.id, row.orderNo || row.orderId || '', row.orgId || '', row.retainQty || '', row.retainLoc || ''].forEach(function (value) { var td = document.createElement('td'); td.textContent = value == null ? '' : value; tr.appendChild(td); });
        var action = document.createElement('td'); if (!row.handleDate) { var handleButton = document.createElement('button'); handleButton.type = 'button'; handleButton.id = 'order-retain-handle-' + row.id; handleButton.textContent = '处置'; handleButton.addEventListener('click', function () { handleId = row.id; handleRecord.value = ''; handleEditor.hidden = false; }); action.appendChild(handleButton); } var removeButton = document.createElement('button'); removeButton.type = 'button'; removeButton.id = 'order-retain-delete-' + row.id; removeButton.textContent = '删除'; removeButton.addEventListener('click', function () { remove(row.id).then(function () { global.OwnedUI.notify('留样已删除。', false); return load(); }).catch(function (error) { global.OwnedUI.notify(error.message, true); }); }); action.appendChild(removeButton); tr.appendChild(action); body.appendChild(tr);
      });
      table.appendChild(body);
    }
    function load() { query.disabled = true; return request({ orderNo: input.value.trim() }).then(render).catch(function (error) { global.OwnedUI.notify(error.message, true); }).finally(function () { query.disabled = false; }); }
    create.addEventListener('click', function () { editor.hidden = false; });
    save.addEventListener('click', function () {
      write({ orderId: orderId.value, projId: projectId.value, folderId: folderId.value, retainQty: qty.value, retainDays: days.value, retainLoc: location.value }).then(function () { global.OwnedUI.notify('留样已新增。', false); editor.hidden = true; return load(); }).catch(function (error) { global.OwnedUI.notify(error.message, true); });
    });
    handleSave.addEventListener('click', function () { if (!handleId) return; handle(handleId, handleRecord.value.trim()).then(function () { global.OwnedUI.notify('留样已处置。', false); handleEditor.hidden = true; handleId = null; return load(); }).catch(function (error) { global.OwnedUI.notify(error.message, true); }); });
    form.addEventListener('submit', function (event) { event.preventDefault(); load(); });
    return load();
  }
  global.OwnedOrderRetains = Object.freeze({ init: init });
})(window);
