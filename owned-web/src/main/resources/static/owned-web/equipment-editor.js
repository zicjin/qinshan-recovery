(function (global) {
  'use strict';
  var BASE = '/secure/emc/module/mdm/equipt/equipts', activeDialog = null, revision = 0;
  function reset() { revision++; if (activeDialog) { activeDialog.remove(); activeDialog = null; } }
  function open(onSaved) {
    reset();
    var current = revision, el = global.OwnedUI.el, inputs = {};
    var dialog = el('dialog', { id: 'equipment-editor', 'aria-label': '新增设备' });
    var form = el('form'), error = el('p', { role: 'alert' });
    form.appendChild(el('h2', { text: '新增设备' }));
    [['equiptId', '设备编号', 30], ['equiptName', '设备名称', 42], ['specification', '规格型号', 32], ['manufacturer', '生产厂家', 85], ['factoryNo', '出厂编号', 100]].forEach(function (field) {
      var input = el('input', { type: 'text', 'aria-label': field[1], maxlength: field[2], required: field[0] === 'equiptId' || field[0] === 'equiptName' });
      inputs[field[0]] = input;
      form.appendChild(el('label', { className: 'owned-field', text: field[1] }, [input]));
    });
    var category = el('select', { 'aria-label': '设备类别', required: true, disabled: true }, [el('option', { value: '', text: '正在读取可用类别…' })]);
    inputs.equiptCategoryId = category;
    form.appendChild(el('label', { className: 'owned-field', text: '设备类别' }, [category]));
    var save = el('button', { type: 'submit', text: '保存设备', disabled: true });
    var cancel = el('button', { type: 'button', text: '取消', onClick: reset });
    form.appendChild(error); form.appendChild(save); form.appendChild(cancel);
    global.OwnedRequest('/owned/equipment/categories', { cache: 'no-store' }).then(function (rows) {
      if (current !== revision) return;
      if (!Array.isArray(rows)) throw new Error('服务器未返回有效设备类别');
      category.replaceChildren(el('option', { value: '', text: '请选择设备类别' }));
      rows.forEach(function (row) {
        if (row && /^[0-9]+$/.test(String(row.id))) category.appendChild(el('option', { value: String(row.id), text: row.name || String(row.id) }));
      });
      if (category.options.length === 1) throw new Error('当前账号没有可用设备类别');
      category.disabled = false; save.disabled = false;
    }).catch(function (failure) { if (current === revision) error.textContent = failure.message; });
    form.addEventListener('submit', function (event) {
      event.preventDefault();
      if (save.disabled || !form.reportValidity()) return;
      var value = {};
      Object.keys(inputs).forEach(function (key) { var text = inputs[key].value.trim(); value[key] = key === 'equiptCategoryId' ? Number(text) : text || null; });
      save.disabled = true; cancel.disabled = true; error.textContent = '';
      global.OwnedRequest(BASE, { method: 'POST', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ b: ['', [value]] }) }).then(function () {
        if (current !== revision) return;
        reset();
        var saved = revision;
        return Promise.resolve().then(function () { return onSaved && onSaved(); }).then(function () {
          if (saved === revision) global.OwnedUI.notify('设备已新增', false);
        }, function () {
          if (saved === revision) global.OwnedUI.notify('设备已新增，列表刷新失败，请点击刷新。');
        });
      }).catch(function (failure) {
        if (current === revision) { error.textContent = failure.message; save.disabled = false; cancel.disabled = false; }
      });
    });
    dialog.addEventListener('cancel', function (event) { event.preventDefault(); reset(); });
    dialog.appendChild(form); document.body.appendChild(dialog); activeDialog = dialog; dialog.showModal();
  }
  global.OwnedEquipmentEditor = Object.freeze({ open: open, reset: reset });
})(window);
