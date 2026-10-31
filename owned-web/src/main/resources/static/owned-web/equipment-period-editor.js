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
  function selectEvents(equiptId, path, pageType, current, maint, save, error) {
    global.OwnedRequest(BASE + '/' + encodeURIComponent(equiptId) + path, {
      method: 'POST', headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
      body: JSON.stringify({ p: { f: { pageType: pageType } } })
    }).then(function (rows) {
      if (current !== revision) return;
      if (!Array.isArray(rows) || !rows.length) throw new Error('当前设备没有可用维护事件');
      maint.replaceChildren(global.OwnedUI.el('option', { value: '', text: '请选择维护事件' }));
      rows.forEach(function (row) { if (row && row.id != null) maint.appendChild(global.OwnedUI.el('option', { value: String(row.id), text: row.maintEvent || String(row.id) })); });
      maint.removeAttribute('disabled'); maint.disabled = false; save.disabled = false;
    }).catch(function (failure) { if (current === revision) error.textContent = failure.message; });
  }
  function openPlan(equiptId, onSaved) {
    reset();
    var current = revision, el = global.OwnedUI.el, dialog = el('dialog', { id: 'equipment-period-plan-editor', 'aria-label': '新增期间核查计划' });
    var form = el('form'), error = el('p', { role: 'alert' }); form.appendChild(el('h2', { text: '新增期间核查计划' }));
    var maint = el('select', { 'aria-label': '维护事件', required: true, disabled: true }, [el('option', { value: '', text: '正在读取维护事件…' })]);
    var cycle = el('input', { type: 'text', 'aria-label': '核查周期', required: true, value: '2' });
    var date = el('input', { type: 'text', 'aria-label': '核查时间', required: true, value: '2027-02-01' });
    var warning = el('input', { type: 'number', 'aria-label': '提醒时间（天）', required: true, value: '7' });
    var reason = el('input', { type: 'text', 'aria-label': '核查依据', value: 'browser-period' });
    var equipment = el('input', { type: 'text', 'aria-label': '核查所用仪器设备', value: 'Reference' });
    var way = el('input', { type: 'text', 'aria-label': '核查方式', value: 'visual' });
    var remark = el('input', { type: 'text', 'aria-label': '备注', value: 'browser period plan lifecycle' });
    [['维护事件', maint], ['核查周期', cycle], ['核查时间', date], ['提醒时间（天）', warning], ['核查依据', reason], ['核查所用仪器设备', equipment], ['核查方式', way], ['备注', remark]].forEach(function (pair) { form.appendChild(el('label', { className: 'owned-field', text: pair[0] }, [pair[1]])); });
    var save = el('button', { type: 'submit', text: '保存期间核查计划', disabled: true }); var cancel = el('button', { type: 'button', text: '取消', onClick: reset });
    form.appendChild(error); form.appendChild(save); form.appendChild(cancel);
    selectEvents(equiptId, '/maints/queries/selectable', 'during', current, maint, save, error);
    form.addEventListener('submit', function (event) {
      event.preventDefault(); if (save.disabled || !form.reportValidity()) return;
      save.disabled = true; cancel.disabled = true; error.textContent = '';
      var value = { equiptId: Number(equiptId), maintId: Number(maint.value), cycle: cycle.value, maintDate: date.value, warningDays: Number(warning.value), checkReason: reason.value, checkEquipt: equipment.value, checkWay: way.value, remark: remark.value };
      global.OwnedRequest(BASE + '/per-check-plan', { method: 'POST', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ b: ['', [value]] }) }).then(function () { return finish(current, onSaved, '期间核查计划已新增'); }).catch(function (failure) { if (current === revision) { error.textContent = failure.message; save.disabled = false; cancel.disabled = false; } });
    });
    dialog.addEventListener('cancel', function (event) { event.preventDefault(); reset(); }); dialog.appendChild(form); document.body.appendChild(dialog); activeDialog = dialog; dialog.showModal();
  }
  function openRecord(equiptId, onSaved) {
    reset();
    var current = revision, el = global.OwnedUI.el, dialog = el('dialog', { id: 'equipment-period-record-editor', 'aria-label': '新增期间核查记录' });
    var form = el('form'), error = el('p', { role: 'alert' }); form.appendChild(el('h2', { text: '新增期间核查记录' }));
    var maint = el('select', { 'aria-label': '维护事件', required: true, disabled: true }, [el('option', { value: '', text: '正在读取维护事件…' })]);
    var reason = el('input', { type: 'text', 'aria-label': '核查依据', value: 'browser-period-record' });
    var equipment = el('input', { type: 'text', 'aria-label': '核查所用仪器设备', value: 'Reference' });
    var remark = el('input', { type: 'text', 'aria-label': '备注', value: 'browser period record lifecycle' });
    [['维护事件', maint], ['核查依据', reason], ['核查所用仪器设备', equipment], ['备注', remark]].forEach(function (pair) { form.appendChild(el('label', { className: 'owned-field', text: pair[0] }, [pair[1]])); });
    var save = el('button', { type: 'submit', text: '保存期间核查记录', disabled: true }); var cancel = el('button', { type: 'button', text: '取消', onClick: reset }); form.appendChild(error); form.appendChild(save); form.appendChild(cancel);
    global.OwnedRequest(BASE + '/' + encodeURIComponent(equiptId) + '/per-check-records/maints/queries/selectable', { headers: { Accept: 'application/json' } }).then(function (rows) {
      if (current !== revision) return; if (!Array.isArray(rows) || !rows.length) throw new Error('当前设备没有可用期间核查记录事件');
      maint.replaceChildren(el('option', { value: '', text: '请选择维护事件' })); rows.forEach(function (row) { if (row && row.id != null) maint.appendChild(el('option', { value: String(row.id), text: row.maintEvent || String(row.id) })); }); maint.removeAttribute('disabled'); maint.disabled = false; save.disabled = false;
    }).catch(function (failure) { if (current === revision) error.textContent = failure.message; });
    form.addEventListener('submit', function (event) {
      event.preventDefault(); if (save.disabled || !form.reportValidity()) return; save.disabled = true; cancel.disabled = true; error.textContent = '';
      var value = { equiptId: Number(equiptId), maintId: Number(maint.value), checkReason: reason.value, checkEquipt: equipment.value, remark: remark.value };
      global.OwnedRequest(BASE + '/per-check-record', { method: 'POST', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ b: ['', [value]] }) }).then(function () { return finish(current, onSaved, '期间核查记录已新增'); }).catch(function (failure) { if (current === revision) { error.textContent = failure.message; save.disabled = false; cancel.disabled = false; } });
    });
    dialog.addEventListener('cancel', function (event) { event.preventDefault(); reset(); }); dialog.appendChild(form); document.body.appendChild(dialog); activeDialog = dialog; dialog.showModal();
  }
  function remove(kind, row, onSaved) {
    reset(); var current = revision, el = global.OwnedUI.el, record = kind === 'record', dialog = el('dialog', { id: record ? 'equipment-period-record-delete' : 'equipment-period-plan-delete', 'aria-label': record ? '删除期间核查记录' : '删除期间核查计划' });
    var form = el('form'); form.appendChild(el('h2', { text: record ? '删除期间核查记录' : '删除期间核查计划' })); form.appendChild(el('p', { text: '确认删除当前期间核查条目？' }));
    var confirm = el('button', { type: 'submit', text: record ? '确认删除期间核查记录' : '确认删除期间核查计划' }); var cancel = el('button', { type: 'button', text: '取消', onClick: reset }); form.appendChild(confirm); form.appendChild(cancel);
    form.addEventListener('submit', function (event) { event.preventDefault(); confirm.disabled = true; cancel.disabled = true; var path = record ? '/per-check-record' : '/per-check-plan'; global.OwnedRequest(BASE + path, { method: 'DELETE', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ b: ['', [{ id: row.id }]] }) }).then(function () { return finish(current, onSaved, record ? '期间核查记录已删除' : '期间核查计划已删除'); }).catch(function (failure) { if (current === revision) { global.OwnedUI.notify(failure.message); confirm.disabled = false; cancel.disabled = false; } }); });
    dialog.addEventListener('cancel', function (event) { event.preventDefault(); reset(); }); dialog.appendChild(form); document.body.appendChild(dialog); activeDialog = dialog; dialog.showModal();
  }
  function completeRecord(row, onSaved) {
    reset();
    var current = revision, el = global.OwnedUI.el, dialog = el('dialog', { id: 'equipment-period-record-complete', 'aria-label': '完成期间核查记录' });
    var form = el('form'), error = el('p', { role: 'alert' }); form.appendChild(el('h2', { text: '完成期间核查记录' }));
    var cycle = el('input', { type: 'text', 'aria-label': '核查周期', required: true, value: '2' });
    var date = el('input', { type: 'text', 'aria-label': '核查时间', required: true, value: '2027-03-01' });
    var reason = el('input', { type: 'text', 'aria-label': '核查依据', value: 'Browser Period Complete' });
    var equipment = el('input', { type: 'text', 'aria-label': '核查所用仪器设备', value: 'Reference Complete' });
    var remark = el('input', { type: 'text', 'aria-label': '备注', value: 'browser period completion' });
    [['核查周期', cycle], ['核查时间', date], ['核查依据', reason], ['核查所用仪器设备', equipment], ['备注', remark]].forEach(function (pair) { form.appendChild(el('label', { className: 'owned-field', text: pair[0] }, [pair[1]])); });
    var save = el('button', { type: 'submit', text: '确认完成期间核查记录' }); var cancel = el('button', { type: 'button', text: '取消', onClick: reset }); form.appendChild(error); form.appendChild(save); form.appendChild(cancel);
    form.addEventListener('submit', function (event) {
      event.preventDefault(); if (!form.reportValidity()) return; save.disabled = true; cancel.disabled = true; error.textContent = '';
      var value = { id: row.id, equiptId: row.equiptId, maintId: row.maintId, maintDate: date.value, cycle: cycle.value, checkReason: reason.value, checkEquipt: equipment.value, remark: remark.value };
      global.OwnedRequest(BASE + '/per-check-records/action/complete', { method: 'PUT', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ b: ['', [value]] }) }).then(function () { return finish(current, onSaved, '期间核查记录已完成'); }).catch(function (failure) { if (current === revision) { error.textContent = failure.message; save.disabled = false; cancel.disabled = false; } });
    });
    dialog.addEventListener('cancel', function (event) { event.preventDefault(); reset(); }); dialog.appendChild(form); document.body.appendChild(dialog); activeDialog = dialog; dialog.showModal();
  }
  global.OwnedEquipmentPeriodEditor = Object.freeze({ openPlan: openPlan, openRecord: openRecord, removePlan: function (row, cb) { remove('plan', row, cb); }, removeRecord: function (row, cb) { remove('record', row, cb); }, completeRecord: completeRecord, reset: reset });
})(window);
