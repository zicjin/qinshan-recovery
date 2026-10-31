(function (global) {
  'use strict';
  var BASE = '/secure/emc/module/mdm/emp/emps', revision = 0, activeDialog = null;
  // These are the existing master-table write fields. Unedited detail values,
  // including signature/photo references, travel back with an update.
  var fields = [
    ['userName', '姓名', 'text', 64], ['birthday', '出生日期', 'date'], ['age', '年龄', 'number'],
    ['mobile', '手机', 'text', 32], ['sex', '性别', 'text', 32], ['filterTest', '过滤测试', 'text', 1],
    ['phone', '电话', 'text', 32], ['nation', '民族', 'text', 32], ['startWorkDate', '参加工作日期', 'date'],
    ['titleDate', '职称日期', 'date'], ['professionalTitle', '专业技术职称', 'text', 64],
    ['position', '职务', 'text', 64], ['remark', '备注', 'text', 512]
  ];
  function reset() {
    revision++;
    if (activeDialog) { activeDialog.remove(); activeDialog = null; }
  }
  function read(url) {
    return fetch(url, { credentials: 'same-origin', cache: 'no-store', headers: { Accept: 'application/json' } })
      .then(function (response) { return response.text().then(function (text) {
        var body; try { body = text ? JSON.parse(text) : null; } catch (ignored) { throw new Error('服务器响应格式无效'); }
        if (!response.ok) throw new Error(body && body.message || '请求失败（HTTP ' + response.status + '）');
        return body;
      }); });
  }
  function open(action, row, onSaved) {
    reset();
    if (action !== 'insert' && !row) { global.OwnedUI.notify('请先选择人员。'); return; }
    var current = revision, el = global.OwnedUI.el;
    var pending = action === 'insert' ? read('/owned/personnel/unassigned-accounts') : read(BASE + '/' + encodeURIComponent(row.id));
    pending.then(function (body) {
      if (current !== revision) return;
      if (action !== 'insert' && (!body || body.id !== row.id)) throw new Error('人员详情不可用');
      if (action === 'insert' && !Array.isArray(body)) throw new Error('账号列表格式无效');
      var dialog = el('dialog', { id: 'personnel-editor', 'aria-label': action === 'insert' ? '新增人员' : action === 'update' ? '修改人员' : '删除人员' });
      var form = el('form'), title = el('h2', { text: action === 'insert' ? '新增人员' : action === 'update' ? '修改人员' : '删除人员' });
      var error = el('p', { role: 'alert', id: 'personnel-editor-error' }), inputs = {}, account;
      form.appendChild(title);
      if (action === 'insert') {
        account = el('select', { id: 'personnel-account', 'aria-label': '选择已有账号', required: 'required' });
        account.appendChild(el('option', { value: '', text: '请选择已有账号' }));
        body.forEach(function (user) { account.appendChild(el('option', { value: user.id, text: user.id + ' · ' + (user.userName || user.id) })); });
        form.appendChild(el('label', { text: '已有账号' }, [account]));
        account.addEventListener('change', function () {
          var user = body.filter(function (item) { return item.id === account.value; })[0];
          if (user) inputs.userName.value = user.userName || '';
        });
      } else form.appendChild(el('p', { text: String(body.userName || body.id) + '（' + body.id + '）' }));
      if (action === 'delete') form.appendChild(el('p', { text: '确认删除该人员记录？' }));
      else {
        var controls = el('div', { className: 'owned-fields' });
        fields.forEach(function (field) {
          var value = action === 'update' ? body[field[0]] : null;
          var input = el('input', { id: 'personnel-' + field[0], type: field[2], value: value, 'aria-label': field[1],
            maxlength: field[3] || null, required: field[0] === 'userName' ? 'required' : null });
          if (field[0] === 'age') { input.min = '0'; input.max = '200'; input.step = '1'; }
          inputs[field[0]] = input;
          controls.appendChild(el('label', { className: 'owned-field', text: field[1] }, [input]));
        });
        form.appendChild(controls);
      }
      var save = el('button', { type: 'submit', text: action === 'delete' ? '确认删除' : '保存人员' });
      var cancel = el('button', { type: 'button', text: '取消', onClick: reset });
      form.appendChild(error); form.appendChild(save); form.appendChild(cancel);
      form.addEventListener('submit', function (event) {
        event.preventDefault();
        var value = action === 'insert' ? { id: account.value } : Object.assign({}, body);
        if (action !== 'delete') fields.forEach(function (field) {
          var text = inputs[field[0]].value.trim();
          value[field[0]] = text === '' ? null : field[2] === 'number' ? Number(text) : text;
        });
        var method = action === 'insert' ? 'POST' : action === 'update' ? 'PUT' : 'DELETE';
        var url = BASE + (action === 'update' ? '/' + encodeURIComponent(value.id) : '');
        save.disabled = true; cancel.disabled = true; error.textContent = '';
        fetch(url, { method: method, credentials: 'same-origin', headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ b: ['', [action === 'delete' ? { id: value.id } : value]] })
        }).then(function (response) { return response.text().then(function (text) {
          if (!response.ok) { var parsed; try { parsed = JSON.parse(text); } catch (ignored) {}
            throw new Error(parsed && parsed.message || '保存失败（HTTP ' + response.status + '）'); }
        }); }).then(function () {
          if (current !== revision) return;
          reset();
          var savedRevision = revision;
          return Promise.resolve(onSaved && onSaved()).then(function () {
            if (savedRevision === revision) global.OwnedUI.notify(action === 'insert' ? '人员已新增' : action === 'update' ? '人员已修改' : '人员已删除', false);
          }).catch(function () {
            if (savedRevision === revision) global.OwnedUI.notify('人员已保存，但列表刷新失败，请重试刷新。');
          });
        }).catch(function (failure) { if (current === revision) { error.textContent = failure.message; save.disabled = false; cancel.disabled = false; } });
      });
      dialog.addEventListener('cancel', function (event) { event.preventDefault(); reset(); });
      dialog.appendChild(form); document.body.appendChild(dialog); activeDialog = dialog; dialog.showModal();
    }).catch(function (failure) { if (current === revision) global.OwnedUI.notify(failure.message); });
  }
  global.OwnedPersonnelEditor = Object.freeze({ open: open, reset: reset });
})(window);
