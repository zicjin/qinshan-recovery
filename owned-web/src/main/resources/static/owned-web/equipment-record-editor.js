(function (global) {
  'use strict';
  var BASE = '/secure/emc/module/mdm/equipt/equipts', activeDialog = null, revision = 0;
  function reset() { revision++; if (activeDialog) { activeDialog.remove(); activeDialog = null; } }
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
    var current = revision, el = global.OwnedUI.el, dialog = el('dialog', { id: 'equipment-record-editor', 'aria-label': '新增维护记录' });
    var form = el('form'), error = el('p', { role: 'alert' });
    form.appendChild(el('h2', { text: '新增维护记录' }));
    var maint = el('select', { 'aria-label': '维护事件', required: true, disabled: true }, [el('option', { value: '', text: '正在读取维护事件…' })]);
    var verify = el('input', { type: 'text', 'aria-label': '校检单位', maxlength: '200', value: 'Fixture Lab' });
    form.appendChild(el('label', { className: 'owned-field', text: '维护事件' }, [maint]));
    form.appendChild(el('label', { className: 'owned-field', text: '校检单位' }, [verify]));
    var save = el('button', { type: 'submit', text: '保存维护记录', disabled: true });
    var cancel = el('button', { type: 'button', text: '取消', onClick: reset });
    form.appendChild(error); form.appendChild(save); form.appendChild(cancel);
    global.OwnedRequest(BASE + '/' + encodeURIComponent(equiptId) + '/records/maints/queries/selectable', { headers: { Accept: 'application/json' } }).then(function (rows) {
      if (current !== revision) return;
      if (!Array.isArray(rows) || !rows.length) throw new Error('当前设备没有可用维护记录事件');
      maint.replaceChildren(el('option', { value: '', text: '请选择维护事件' }));
      rows.forEach(function (row) { if (row && row.id != null) maint.appendChild(el('option', { value: String(row.id), text: row.maintEvent || String(row.id) })); });
      maint.removeAttribute('disabled'); maint.disabled = false; save.disabled = false;
    }).catch(function (failure) { if (current === revision) error.textContent = failure.message; });
    form.addEventListener('submit', function (event) {
      event.preventDefault();
      if (save.disabled || !form.reportValidity()) return;
      save.disabled = true; cancel.disabled = true; error.textContent = '';
      var value = { equiptId: Number(equiptId), maintId: Number(maint.value), verifyOrg: verify.value.trim() || null };
      global.OwnedRequest(BASE + '/records', { method: 'POST', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ b: ['', [value]] }) })
        .then(function () { return finish(current, onSaved, '维护记录已新增'); })
        .catch(function (failure) { if (current === revision) { error.textContent = failure.message; save.disabled = false; cancel.disabled = false; } });
    });
    dialog.addEventListener('cancel', function (event) { event.preventDefault(); reset(); });
    dialog.appendChild(form); document.body.appendChild(dialog); activeDialog = dialog; dialog.showModal();
  }
  function remove(row, onSaved) {
    reset();
    var current = revision, el = global.OwnedUI.el, dialog = el('dialog', { id: 'equipment-record-delete', 'aria-label': '删除维护记录' });
    var form = el('form'); form.appendChild(el('h2', { text: '删除维护记录' })); form.appendChild(el('p', { text: '确认删除当前维护记录？' }));
    var confirm = el('button', { type: 'submit', text: '确认删除维护记录' });
    var cancel = el('button', { type: 'button', text: '取消', onClick: reset }); form.appendChild(confirm); form.appendChild(cancel);
    form.addEventListener('submit', function (event) {
      event.preventDefault(); confirm.disabled = true; cancel.disabled = true;
      global.OwnedRequest(BASE + '/records', { method: 'DELETE', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ b: ['', [{ id: row.id }]] }) })
        .then(function () { return finish(current, onSaved, '维护记录已删除'); })
        .catch(function (failure) { if (current === revision) { global.OwnedUI.notify(failure.message); confirm.disabled = false; cancel.disabled = false; } });
    });
    dialog.addEventListener('cancel', function (event) { event.preventDefault(); reset(); });
    dialog.appendChild(form); document.body.appendChild(dialog); activeDialog = dialog; dialog.showModal();
  }
  function complete(row, onSaved) {
    reset();
    var current = revision, el = global.OwnedUI.el, dialog = el('dialog', { id: 'equipment-record-complete', 'aria-label': '完成维护记录' });
    var form = el('form'), error = el('p', { role: 'alert' }); form.appendChild(el('h2', { text: '完成维护记录' }));
    var cycle = el('input', { type: 'text', 'aria-label': '校检周期', required: true, value: '1' });
    var date = el('input', { type: 'text', 'aria-label': '校检日期', required: true, value: '2027-03-01' });
    var end = el('input', { type: 'text', 'aria-label': '有效期至', required: true, value: '2028-02-29' });
    var verify = el('input', { type: 'text', 'aria-label': '校检单位', value: 'Browser Complete Lab' });
    var cert = el('input', { type: 'text', 'aria-label': '证书类型', value: 'calibration' });
    var remark = el('input', { type: 'text', 'aria-label': '备注', value: 'browser record completion' });
    [['校检周期', cycle], ['校检日期', date], ['有效期至', end], ['校检单位', verify], ['证书类型', cert], ['备注', remark]].forEach(function (pair) { form.appendChild(el('label', { className: 'owned-field', text: pair[0] }, [pair[1]])); });
    var save = el('button', { type: 'submit', text: '确认完成维护记录' }); var cancel = el('button', { type: 'button', text: '取消', onClick: reset }); form.appendChild(error); form.appendChild(save); form.appendChild(cancel);
    form.addEventListener('submit', function (event) {
      event.preventDefault(); if (!form.reportValidity()) return; save.disabled = true; cancel.disabled = true; error.textContent = '';
      var value = { id: row.id, equiptId: row.equiptId, maintId: row.maintId, completeFlag: '1', mainDate: date.value, maintEndDate: end.value, verifyOrg: verify.value, certCategory: cert.value, remark: remark.value, ext$: { cycle: cycle.value, gist: 'browser' } };
      global.OwnedRequest(BASE + '/records/action/complete', { method: 'PUT', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ b: ['', [value]] }) }).then(function () { return finish(current, onSaved, '维护记录已完成'); }).catch(function (failure) { if (current === revision) { error.textContent = failure.message; save.disabled = false; cancel.disabled = false; } });
    });
    dialog.addEventListener('cancel', function (event) { event.preventDefault(); reset(); }); dialog.appendChild(form); document.body.appendChild(dialog); activeDialog = dialog; dialog.showModal();
  }
  global.OwnedEquipmentRecordEditor = Object.freeze({ open: open, remove: remove, complete: complete, reset: reset });
})(window);
