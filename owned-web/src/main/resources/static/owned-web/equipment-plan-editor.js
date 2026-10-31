(function (global) {
  'use strict';
  var BASE = '/secure/emc/module/mdm/equipt/equipts', activeDialog = null, revision = 0;
  function reset() { revision++; if (activeDialog) { activeDialog.remove(); activeDialog = null; } }
  function field(el, label, control) { return el('label', { className: 'owned-field', text: label }, [control]); }
  function finish(current, onSaved, message) {
    if (current !== revision) return;
    reset();
    var saved = revision;
    return Promise.resolve().then(function () { return onSaved && onSaved(); }).then(function () {
      if (saved === revision) global.OwnedUI.notify(message, false);
    }, function (failure) {
      if (saved === revision) global.OwnedUI.notify(message + '，列表刷新失败，请点击刷新。');
      throw failure;
    });
  }
  function open(equiptId, onSaved) {
    reset();
    var current = revision, el = global.OwnedUI.el, dialog = el('dialog', { id: 'equipment-plan-editor', 'aria-label': '新增维护计划' });
    var form = el('form'), error = el('p', { role: 'alert' }), inputs = {};
    form.appendChild(el('h2', { text: '新增维护计划' }));
    var maint = el('select', { 'aria-label': '维护事件', required: true, disabled: true }, [el('option', { value: '', text: '正在读取维护事件…' })]);
    var cycle = el('select', { 'aria-label': '校检周期', required: true }, [el('option', { value: '1', text: '1' }), el('option', { value: '2', text: '2' }), el('option', { value: '3', text: '3' })]);
    var date = el('input', { type: 'text', 'aria-label': '校检日期', required: true, value: '2027-01-01' });
    var warning = el('input', { type: 'number', 'aria-label': '提前提醒（天）', required: true, min: '0', max: '36500', value: '30' });
    var remark = el('input', { type: 'text', 'aria-label': '备注', maxlength: '200', value: 'browser plan lifecycle' });
    inputs.maintId = maint; inputs.cycle = cycle; inputs.maintDate = date; inputs.warningDays = warning; inputs.remark = remark;
    form.appendChild(field(el, '维护事件', maint)); form.appendChild(field(el, '校检周期', cycle)); form.appendChild(field(el, '校检日期', date));
    form.appendChild(field(el, '提前提醒（天）', warning)); form.appendChild(field(el, '备注', remark));
    var save = el('button', { type: 'submit', text: '保存维护计划', disabled: true });
    var cancel = el('button', { type: 'button', text: '取消', onClick: reset });
    form.appendChild(error); form.appendChild(save); form.appendChild(cancel);
    global.OwnedRequest(BASE + '/' + encodeURIComponent(equiptId) + '/maints/queries/selectable', {
      method: 'POST', headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
      body: JSON.stringify({ p: { f: { pageType: 'equipt' } } })
    }).then(function (rows) {
      if (current !== revision) return;
      if (!Array.isArray(rows) || !rows.length) throw new Error('当前设备没有可用维护事件');
      maint.replaceChildren(el('option', { value: '', text: '请选择维护事件' }));
      rows.forEach(function (row) { if (row && row.id != null) maint.appendChild(el('option', { value: String(row.id), text: row.maintEvent || String(row.id) })); });
      maint.removeAttribute('disabled'); maint.disabled = false; save.disabled = false;
    }).catch(function (failure) { if (current === revision) error.textContent = failure.message; });
    form.addEventListener('submit', function (event) {
      event.preventDefault();
      if (save.disabled || !form.reportValidity()) return;
      var value = { equiptId: Number(equiptId), maintId: Number(maint.value), cycle: cycle.value,
        maintDate: date.value, warningDays: Number(warning.value), remark: remark.value.trim() || null };
      save.disabled = true; cancel.disabled = true; error.textContent = '';
      global.OwnedRequest(BASE + '/plans', { method: 'POST', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ b: ['', [value]] }) })
        .then(function () { return finish(current, onSaved, '维护计划已新增'); })
        .catch(function (failure) { if (current === revision) { error.textContent = failure.message; save.disabled = false; cancel.disabled = false; } });
    });
    dialog.addEventListener('cancel', function (event) { event.preventDefault(); reset(); });
    dialog.appendChild(form); document.body.appendChild(dialog); activeDialog = dialog; dialog.showModal();
  }
  function remove(row, onSaved) {
    reset();
    var current = revision, el = global.OwnedUI.el, dialog = el('dialog', { id: 'equipment-plan-delete', 'aria-label': '删除维护计划' });
    var form = el('form'); form.appendChild(el('h2', { text: '删除维护计划' }));
    form.appendChild(el('p', { text: '确认删除当前维护计划？' }));
    var confirm = el('button', { type: 'submit', text: '确认删除维护计划' });
    var cancel = el('button', { type: 'button', text: '取消', onClick: reset });
    form.appendChild(confirm); form.appendChild(cancel);
    form.addEventListener('submit', function (event) {
      event.preventDefault(); confirm.disabled = true; cancel.disabled = true;
      global.OwnedRequest(BASE + '/plans', { method: 'DELETE', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ b: ['', [{ id: row.id }]] }) })
        .then(function () { return finish(current, onSaved, '维护计划已删除'); })
        .catch(function (failure) { if (current === revision) { global.OwnedUI.notify(failure.message); confirm.disabled = false; cancel.disabled = false; } });
    });
    dialog.addEventListener('cancel', function (event) { event.preventDefault(); reset(); });
    dialog.appendChild(form); document.body.appendChild(dialog); activeDialog = dialog; dialog.showModal();
  }
  global.OwnedEquipmentPlanEditor = Object.freeze({ open: open, remove: remove, reset: reset });
})(window);
