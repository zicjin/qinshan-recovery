(function (global) {
  'use strict';
  var BASE = '/secure/emc/module/item/consume/consumes', activeDialog = null, revision = 0;
  var config = {
    insert: { title: '新增库存', fields: [['batchNo', '批号', 'text', false], ['qty', '数量', 'number', true], ['vendorId', '供应商', 'text', false], ['price', '价格', 'number', false]], route: function (id) { return BASE + '/' + encodeURIComponent(id) + '/stocks'; } },
    storage: { title: '入库', fields: [['qty', '入库数量', 'number', true], ['vendorId', '供应商', 'text', false], ['price', '价格', 'number', false]], route: function (_, row) { return BASE + '/stocks/' + encodeURIComponent(row.id) + '/action/stockin'; } },
    inventory: { title: '盘存', fields: [['qty', '盘存数量', 'number', true], ['logContent', '盘存说明', 'text', false]], route: function (id) { return BASE + '/' + encodeURIComponent(id) + '/stocks/action/inventory'; } },
    abandon: { title: '废弃', fields: [['logContent', '废弃说明', 'text', true]], route: function (id) { return BASE + '/' + encodeURIComponent(id) + '/stocks/action/abandon'; } },
    receive: { title: '领用', fields: [['qty', '领用数量', 'number', true]], route: function (_, row) { return BASE + '/stocks/' + encodeURIComponent(row.id) + '/action/recieve'; } },
    delete: { title: '删除库存', fields: [], route: function (id) { return BASE + '/' + encodeURIComponent(id) + '/stocks'; } }
  };
  function reset() { revision++; if (activeDialog) { activeDialog.remove(); activeDialog = null; } }
  function wrapper(value) { return JSON.stringify({ b: ['', [value]] }); }
  function open(action, materialId, row, onSaved) {
    var spec = config[action];
    if (!spec || !materialId || (action !== 'insert' && (!row || row.id == null))) { global.OwnedUI.notify('未选择有效库存。'); return; }
    reset(); global.OwnedUI.notify('', false); var current = revision, el = global.OwnedUI.el;
    var dialog = el('dialog', { id: 'consumable-stock-editor', 'aria-label': spec.title }), form = el('form'), error = el('p', { role: 'alert' }), inputs = {};
    form.appendChild(el('h2', { text: spec.title }));
    if (action === 'delete') form.appendChild(el('p', { text: '确认删除当前库存及其历史记录？' }));
    spec.fields.forEach(function (field) {
      var input = el('input', { type: field[2], 'aria-label': field[1], required: field[3], min: field[2] === 'number' ? (field[0] === 'qty' && (action === 'storage' || action === 'receive') ? '0.000001' : '0') : null, step: field[2] === 'number' ? 'any' : null, max: field[2] === 'number' ? '1000000000' : null, maxlength: field[2] === 'text' ? (field[0] === 'logContent' ? '20' : '64') : null });
      inputs[field[0]] = input;
      form.appendChild(el('label', { className: 'owned-field', text: field[1] }, [input]));
    });
    var save = el('button', { type: 'submit', text: action === 'delete' ? '确认删除库存' : '保存' }), cancel = el('button', { type: 'button', text: '取消', onClick: reset });
    var recipients = [], recipient;
    if (action === 'receive') {
      recipient = el('select', { 'aria-label': '领用人', required: true, disabled: true });
      form.appendChild(el('label', { className: 'owned-field', text: '领用人' }, [recipient]));
      save.disabled = true;
      global.OwnedRequest('/owned/consumables/stock-recipients', { cache: 'no-store' }).then(function (rows) {
        if (current !== revision) return;
        if (!Array.isArray(rows)) throw new Error('领用人列表不可用');
        recipients = rows;
        recipient.appendChild(el('option', { value: '', text: '请选择领用人' }));
        rows.forEach(function (user) { recipient.appendChild(el('option', { value: user.id, text: (user.name || user.id) + ' · ' + (user.orgName || user.orgId) })); });
        recipient.disabled = false; save.disabled = false;
      }).catch(function (failure) { if (current === revision) error.textContent = failure.message; });
    }
    form.appendChild(error); form.appendChild(save); form.appendChild(cancel);
    form.addEventListener('submit', function (event) {
      event.preventDefault(); if (save.disabled || !form.reportValidity()) return;
      var value = {}; Object.keys(inputs).forEach(function (key) { var text = inputs[key].value.trim(); value[key] = key === 'qty' || key === 'price' ? (text === '' ? null : Number(text)) : (text || null); });
      if (action === 'insert') { value.ext$ = { batchno: value.batchNo }; delete value.batchNo; }
      if (action === 'delete') value = { id: row.id };
      if (action === 'receive') {
        var user = recipients.filter(function (candidate) { return candidate.id === recipient.value; })[0];
        if (!user) { error.textContent = '请选择领用人。'; return; }
        value.receivedById = user.id; value.receivedByOrgId = user.orgId;
      }
      if (action === 'inventory' || action === 'abandon') value.ext$ = { stockId: String(row.id) };
      save.disabled = true; cancel.disabled = true; error.textContent = '';
      request(spec.route(materialId, row), action === 'insert' ? 'POST' : action === 'delete' ? 'DELETE' : 'PUT', wrapper(value), current, onSaved).catch(function (failure) {
        if (current === revision) { error.textContent = failure.message; save.disabled = false; cancel.disabled = false; }
      });
    });
    dialog.addEventListener('cancel', function (event) { event.preventDefault(); reset(); });
    dialog.appendChild(form); document.body.appendChild(dialog); activeDialog = dialog; dialog.showModal();
  }
  function request(url, method, body, current, onSaved) {
    return global.OwnedRequest(url, { method: method, headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: body }).then(function () {
      if (current !== revision) return;
      reset(); var saved = revision;
      return Promise.resolve().then(function () { return onSaved && onSaved(); }).then(function () { if (saved === revision) global.OwnedUI.notify('库存操作已完成', false); }, function () { if (saved === revision) global.OwnedUI.notify('库存操作已完成，列表刷新失败，请点击刷新。'); });
    });
  }
  global.OwnedConsumableStockEditor = Object.freeze({ open: open, reset: reset });
})(window);
