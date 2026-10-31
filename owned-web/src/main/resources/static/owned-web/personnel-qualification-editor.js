(function (global) {
  'use strict';
  var BASE = '/secure/emc/module/mdm/emp/emps', revision = 0, activeDialog = null;
  function reset() { revision++; if (activeDialog) { activeDialog.remove(); activeDialog = null; } }
  function field(el, id, label, type, value) { return el('label', { className: 'owned-field', text: label }, [el('input', { id: id, type: type || 'text', 'aria-label': label, value: value || '', required: label === '证件名称' })]); }
  function open(action, employee, row, onSaved) {
    reset(); global.OwnedUI.notify('', false);
    if (!employee) { global.OwnedUI.notify('请先选择人员。'); return; }
    if (action === 'delete' && !row) { global.OwnedUI.notify('请先选择资格证书。'); return; }
    var current = revision, el = global.OwnedUI.el;
    var dialog = el('dialog', { id: 'personnel-qualification-editor', 'aria-label': action === 'delete' ? '删除资格证书' : '新增资格证书' });
    var form = el('form'), error = el('p', { role: 'alert' });
    form.appendChild(el('h2', { text: action === 'delete' ? '删除资格证书' : '新增资格证书' }));
    form.appendChild(el('p', { text: '人员：' + employee }));
    if (action === 'delete') form.appendChild(el('p', { text: '确认删除资格证书“' + (global.OwnedUI.text(row, 'qualName') || row.id) + '”？' }));
    else {
      form.appendChild(field(el, 'personnel-qualification-no', '证件号'));
      form.appendChild(field(el, 'personnel-qualification-name', '证件名称'));
      form.appendChild(field(el, 'personnel-qualification-org', '发证单位'));
      form.appendChild(field(el, 'personnel-qualification-auth-date', '授权日期', 'date'));
      form.appendChild(field(el, 'personnel-qualification-expire-date', '过期日期', 'date'));
      form.appendChild(field(el, 'personnel-qualification-warning-days', '提前提醒（天）', 'number', '0'));
    }
    var save = el('button', { type: 'submit', text: action === 'delete' ? '确认删除资格证书' : '保存资格证书' });
    var cancel = el('button', { type: 'button', text: '取消', onClick: reset });
    form.appendChild(error); form.appendChild(save); form.appendChild(cancel);
    form.addEventListener('submit', function (event) {
      event.preventDefault(); if (current !== revision) return;
      var values = action === 'delete' ? [{ id: row.id }] : [{ qualNo: document.getElementById('personnel-qualification-no').value.trim(), qualName: document.getElementById('personnel-qualification-name').value.trim(), qualOrg: document.getElementById('personnel-qualification-org').value.trim(), authDate: document.getElementById('personnel-qualification-auth-date').value || null, expireDate: document.getElementById('personnel-qualification-expire-date').value || null, warningDays: Number(document.getElementById('personnel-qualification-warning-days').value || 0) }];
      if (action !== 'delete' && !values[0].qualName) { error.textContent = '证件名称不能为空。'; return; }
      Array.from(form.elements).forEach(function (control) { control.disabled = true; });
      global.OwnedRequest(BASE + '/' + encodeURIComponent(employee) + '/quals', { method: action === 'delete' ? 'DELETE' : 'POST', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ b: ['', values] }) }).then(function () {
        if (current !== revision) return; reset(); var savedRevision = revision;
        return Promise.resolve(onSaved && onSaved()).then(function () { if (savedRevision === revision) global.OwnedUI.notify(action === 'delete' ? '资格证书已删除' : '资格证书已新增', false); });
      }).catch(function (failure) { if (current === revision) { error.textContent = failure.message; Array.from(form.elements).forEach(function (control) { control.disabled = false; }); } });
    });
    dialog.addEventListener('cancel', function (event) { event.preventDefault(); reset(); });
    dialog.appendChild(form); document.body.appendChild(dialog); activeDialog = dialog; dialog.showModal();
  }
  global.OwnedPersonnelQualificationEditor = Object.freeze({ open: open, reset: reset });
})(window);
