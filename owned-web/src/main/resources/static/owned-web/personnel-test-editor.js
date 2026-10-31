(function (global) {
  'use strict';
  var BASE = '/secure/emc/module/mdm/emp/emps', revision = 0, activeDialog = null;
  function reset() {
    revision++;
    if (activeDialog) { activeDialog.remove(); activeDialog = null; }
  }
  function open(action, employee, row, onSaved) {
    reset();
    global.OwnedUI.notify('', false);
    if (!employee) { global.OwnedUI.notify('请先选择人员。'); return; }
    if ((action === 'delete' || action === 'copy') && !row) { global.OwnedUI.notify('请先选择可见测试。'); return; }
    if (action === 'copy') return copy(employee, row, onSaved);
    var current = revision, requestVersion = 0, page = 1, size = 20, total = 0, name = '', choices = [];
    var el = global.OwnedUI.el, dialog = el('dialog', { id: 'personnel-test-editor', 'aria-label': action === 'delete' ? '删除可见测试' : '选择测试方法' });
    var form = el('form'), error = el('p', { role: 'alert' }), content = el('div');
    form.appendChild(el('h2', { text: action === 'delete' ? '删除可见测试' : '选择测试方法' }));
    form.appendChild(el('p', { text: '人员：' + employee }));
    var save = el('button', { type: 'submit', text: action === 'delete' ? '确认删除可见测试' : '添加所选测试', disabled: action === 'delete' ? null : 'disabled' });
    var cancel = el('button', { type: 'button', text: '取消', onClick: reset });
    function load() {
      var version = ++requestVersion;
      save.disabled = true; choices = []; content.replaceChildren(el('p', { text: '正在读取测试方法…' })); error.textContent = '';
      global.OwnedRequest('/owned/personnel/' + encodeURIComponent(employee) + '/unassigned-tests?' + new URLSearchParams({ n: page, s: size, name: name }), { cache: 'no-store' }).then(function (body) {
        if (current !== revision || version !== requestVersion) return;
        if (!body || !Array.isArray(body.rows) || !Number.isFinite(Number(body.total))) throw new Error('测试方法列表格式无效');
        total = Number(body.total);
        content.replaceChildren(el('p', { text: '共' + total + '个未分配测试方法' }));
        var table = el('table'), head = el('tr'), rows = el('tbody');
        ['选择', '测试名称', '测试类别', '方法名称', '方法依据'].forEach(function (label) { head.appendChild(el('th', { text: label })); });
        table.appendChild(el('thead', {}, [head]));
        body.rows.forEach(function (item) {
          var checkbox = el('input', { type: 'checkbox', 'aria-label': '选择测试方法 ' + item.id + ' ' + item.testName, onChange: function () {
            choices = choices.filter(function (value) { return value !== item.id; });
            if (checkbox.checked) choices.push(item.id);
            save.disabled = !choices.length;
          } });
          var tr = el('tr', {}, [el('td', {}, [checkbox])]);
          ['testName', 'testCategory', 'methodName', 'methodBasis'].forEach(function (field) { tr.appendChild(el('td', { text: item[field] || '' })); });
          rows.appendChild(tr);
        });
        if (!body.rows.length) rows.appendChild(el('tr', {}, [el('td', { colspan: '5', text: '没有可选测试方法' })]));
        table.appendChild(rows); content.appendChild(el('div', { className: 'owned-grid owned-grid-scroll' }, [table]));
        var pages = Math.max(1, Math.ceil(total / size));
        content.appendChild(el('div', { className: 'grid-pager' }, [
          el('button', { type: 'button', text: '测试方法上一页', disabled: page <= 1 ? 'disabled' : null, onClick: function () { page--; load(); } }),
          el('span', { text: '测试方法第 ' + page + ' / ' + pages + ' 页' }),
          el('button', { type: 'button', text: '测试方法下一页', disabled: page >= pages ? 'disabled' : null, onClick: function () { page++; load(); } })
        ]));
      }).catch(function (failure) { if (current === revision && version === requestVersion) { content.replaceChildren(); error.textContent = failure.message; } });
    }
    if (action === 'delete') form.appendChild(el('p', { text: '确认删除可见测试“' + (global.OwnedUI.text(row, 'ext$.testname') || row.testId) + '”？' }));
    else {
      var search = el('input', { type: 'search', maxlength: '100', 'aria-label': '测试方法名称筛选' });
      form.appendChild(el('div', { className: 'filter-controls' }, [search, el('button', { type: 'button', text: '查询测试方法', onClick: function () { name = search.value.trim(); page = 1; load(); } })]));
      search.addEventListener('keydown', function (event) { if (event.key === 'Enter') { event.preventDefault(); name = search.value.trim(); page = 1; load(); } });
      form.appendChild(content);
    }
    form.appendChild(error); form.appendChild(save); form.appendChild(cancel);
    form.addEventListener('submit', function (event) {
      event.preventDefault();
      if (save.disabled || action !== 'delete' && !choices.length) return;
      var values = action === 'delete' ? [{ id: row.id }] : choices.map(function (id) { return { testId: id }; });
      Array.from(form.elements).forEach(function (control) { control.disabled = true; }); error.textContent = '';
      global.OwnedRequest(BASE + '/' + encodeURIComponent(employee) + '/tests', { method: action === 'delete' ? 'DELETE' : 'POST', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ b: ['', values] }) }).then(function () {
        if (current !== revision) return;
        reset(); var savedRevision = revision;
        return Promise.resolve(onSaved && onSaved()).then(function () {
          if (savedRevision === revision) global.OwnedUI.notify(action === 'delete' ? '可见测试已删除' : '可见测试已新增', false);
        }).catch(function () { if (savedRevision === revision) global.OwnedUI.notify('可见测试已保存，但列表刷新失败，请重试刷新。'); });
      }).catch(function (failure) {
        if (current !== revision) return;
        error.textContent = failure.message;
        // Keep the submitted selection fixed; retry or cancel without changing
        // the pending operation's employee or rows.
        save.disabled = false; cancel.disabled = false;
      });
    });
    dialog.addEventListener('cancel', function (event) { event.preventDefault(); reset(); });
    dialog.appendChild(form); document.body.appendChild(dialog); activeDialog = dialog; dialog.showModal();
    if (action !== 'delete') load();
  }
  function copy(sourceEmployee, row, onSaved) {
    var current = revision, requestVersion = 0, page = 1, size = 20, name = '', testId = row.testId;
    var el = global.OwnedUI.el, dialog = el('dialog', { id: 'personnel-test-copy-editor', 'aria-label': '复制可见测试' });
    var form = el('form'), error = el('p', { role: 'alert' }), account = el('select', { id: 'personnel-test-copy-account', 'aria-label': '选择复制目标人员', required: 'required' });
    var save = el('button', { type: 'submit', text: '确认复制可见测试', disabled: 'disabled' }), cancel = el('button', { type: 'button', text: '取消', onClick: reset });
    form.appendChild(el('h2', { text: '复制可见测试' }));
    form.appendChild(el('p', { text: '测试：' + (global.OwnedUI.text(row, 'ext$.testname') || row.testId) + '；来源人员：' + sourceEmployee }));
    var search = el('input', { type: 'search', maxlength: '100', 'aria-label': '复制目标姓名筛选' });
    var searchButton = el('button', { type: 'button', text: '查询目标人员', onClick: function () { name = search.value.trim(); page = 1; load(); } });
    var pager = el('div', { className: 'grid-pager' });
    form.appendChild(el('div', { className: 'filter-controls' }, [search, searchButton]));
    form.appendChild(el('label', { text: '目标人员', className: 'owned-field' }, [account]));
    form.appendChild(pager);
    form.appendChild(error); form.appendChild(save); form.appendChild(cancel);
    function load() {
      var version = ++requestVersion, query = { n: page, s: size };
      if (name) query.f = { USERNAME_CISC: name };
      save.disabled = true; account.disabled = true; account.replaceChildren(); pager.replaceChildren(); error.textContent = '';
      global.OwnedRequest(BASE + '/queries/raw', { method: 'POST', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ p: query }) }).then(function (body) {
        if (current !== revision || version !== requestVersion) return;
        if (!body || !Array.isArray(body.rows) || !Number.isFinite(Number(body.total))) throw new Error('目标人员列表格式无效');
        account.appendChild(el('option', { value: '', text: '请选择目标人员' }));
        body.rows.filter(function (item) { return item && String(item.id) !== String(sourceEmployee); }).forEach(function (item) {
          account.appendChild(el('option', { value: item.id, text: item.id + ' · ' + (item.userName || item.id) }));
        });
        account.disabled = false;
        var pages = Math.max(1, Math.ceil(Number(body.total) / size));
        pager.replaceChildren(
          el('button', { type: 'button', text: '目标人员上一页', disabled: page <= 1 ? 'disabled' : null, onClick: function () { page--; load(); } }),
          el('span', { text: '目标人员第 ' + page + ' / ' + pages + ' 页' }),
          el('button', { type: 'button', text: '目标人员下一页', disabled: page >= pages ? 'disabled' : null, onClick: function () { page++; load(); } })
        );
        if (account.options.length === 1) error.textContent = '当前页没有可选目标人员。';
      }).catch(function (failure) { if (current === revision && version === requestVersion) error.textContent = failure.message; });
    }
    search.addEventListener('keydown', function (event) { if (event.key === 'Enter') { event.preventDefault(); searchButton.click(); } });
    account.addEventListener('change', function () { save.disabled = !account.value; error.textContent = ''; });
    form.addEventListener('submit', function (event) {
      event.preventDefault();
      var target = account.value;
      if (!target || save.disabled) return;
      Array.from(form.elements).forEach(function (control) { control.disabled = true; }); error.textContent = '';
      // Filter at the server so a duplicate beyond the first target page is
      // also detected. Never continue a cancelled preflight into a write.
      global.OwnedRequest(BASE + '/' + encodeURIComponent(target) + '/tests/queries', { method: 'POST', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ p: { n: 1, s: 1, f: { TESTID_EQ: testId } } }) }).then(function (body) {
        if (current !== revision) return;
        if (!body || !Array.isArray(body.rows) || !Number.isFinite(Number(body.total))) throw new Error('目标测试列表格式无效');
        if (Number(body.total) > 0) throw new Error('目标人员已分配该可见测试。');
        return global.OwnedRequest(BASE + '/' + encodeURIComponent(target) + '/tests', { method: 'POST', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ b: ['', [{ testId: testId }]] }) });
      }).then(function () {
        if (current !== revision) return;
        reset(); var savedRevision = revision;
        return Promise.resolve(onSaved && onSaved()).then(function () {
          if (savedRevision === revision) global.OwnedUI.notify('可见测试已复制', false);
        }).catch(function () { if (savedRevision === revision) global.OwnedUI.notify('可见测试已复制，但列表刷新失败，请重试刷新。'); });
      }).catch(function (failure) { if (current === revision) { error.textContent = failure.message; account.disabled = false; save.disabled = false; cancel.disabled = false; } });
    });
    dialog.addEventListener('cancel', function (event) { event.preventDefault(); reset(); });
    dialog.appendChild(form); document.body.appendChild(dialog); activeDialog = dialog; dialog.showModal();
    load();
  }
  global.OwnedPersonnelTestEditor = Object.freeze({ open: open, reset: reset });
})(window);
