(function (global) {
  'use strict';
  var BASE = '/secure/emc/module/item/consume/consumes', activeDialog = null, revision = 0;
  var fields = [
    ['id', '材料编码', 'text', 64, true], ['mtlName', '材料名称', 'text', 100, true],
    ['specification', '规格型号', 'text', 100, false], ['productNo', '货号', 'text', 64, false],
    ['mtlCategoryId', '材料类型', 'text', 64, false], ['harmfulness', '危害性', 'text', 36, false]
  ];
  function reset() { revision++; if (activeDialog) { activeDialog.remove(); activeDialog = null; } }
  function open(action, row, onSaved) {
    reset(); var current = revision, el = global.OwnedUI.el;
    if (action !== 'insert' && !row) { global.OwnedUI.notify('请先选择耗材。'); return; }
    var title = action === 'insert' ? '新增耗材' : action === 'update' ? '修改耗材' : '删除耗材';
    var dialog = el('dialog', { id: 'consumable-editor', 'aria-label': title });
    var form = el('form'), error = el('p', { role: 'alert' }), inputs = {};
    form.appendChild(el('h2', { text: title }));
    if (action === 'delete') form.appendChild(el('p', { text: '确认删除当前耗材及其库存和历史记录？' }));
    else fields.forEach(function (field) {
      var input = el('input', { type: 'text', 'aria-label': field[1], maxlength: field[3], required: field[4], value: action === 'update' ? (row[field[0]] || '') : '' });
      if (action === 'update' && field[0] === 'id') input.disabled = true;
      inputs[field[0]] = input;
      form.appendChild(el('label', { className: 'owned-field', text: field[1] }, [input]));
    });
    var save = el('button', { type: 'submit', text: action === 'delete' ? '确认删除耗材' : '保存耗材' });
    var cancel = el('button', { type: 'button', text: '取消', onClick: reset });
    form.appendChild(error); form.appendChild(save); form.appendChild(cancel);
    form.addEventListener('submit', function (event) {
      event.preventDefault(); if (save.disabled || !form.reportValidity()) return;
      var value = action === 'delete' ? { id: row.id } : {};
      if (action !== 'delete') fields.forEach(function (field) { var text = inputs[field[0]].value.trim(); value[field[0]] = text || null; });
      save.disabled = true; cancel.disabled = true; error.textContent = '';
      var method = action === 'insert' ? 'POST' : action === 'update' ? 'PUT' : 'DELETE';
      var url = action === 'insert' ? BASE : action === 'update' ? BASE + '/' + encodeURIComponent(row.id) : BASE;
      fetch(url, { method: method, credentials: 'same-origin', headers: { 'Content-Type': 'application/json', Accept: 'text/plain, application/json' },
        body: JSON.stringify({ b: ['', [value]] }) }).then(function (response) {
        return response.text().then(function (body) {
          if (!response.ok) {
            var parsed; try { parsed = body ? JSON.parse(body) : null; } catch (ignored) {}
            throw new Error(parsed && parsed.message || body || '保存失败（HTTP ' + response.status + '）');
          }
          return body;
        });
      }).then(function () {
        if (current !== revision) return;
        reset(); var saved = revision;
        return Promise.resolve().then(function () { return onSaved && onSaved(); }).then(function () {
          if (saved === revision) global.OwnedUI.notify(action === 'insert' ? '耗材已新增' : action === 'update' ? '耗材已修改' : '耗材已删除', false);
        }, function () { if (saved === revision) global.OwnedUI.notify('耗材已保存，列表刷新失败，请点击刷新。'); });
      }).catch(function (failure) {
        if (current === revision) { error.textContent = failure.message; save.disabled = false; cancel.disabled = false; }
      });
    });
    dialog.addEventListener('cancel', function (event) { event.preventDefault(); reset(); });
    dialog.appendChild(form); document.body.appendChild(dialog); activeDialog = dialog; dialog.showModal();
  }
  global.OwnedConsumableEditor = Object.freeze({ open: open, reset: reset });
})(window);
