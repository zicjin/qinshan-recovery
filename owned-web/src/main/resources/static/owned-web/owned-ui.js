(function (global) {
  'use strict';
  function el(tag, attributes, children) {
    var node = document.createElement(tag);
    Object.keys(attributes || {}).forEach(function (key) {
      var value = attributes[key];
      if (key === 'text') node.textContent = value;
      else if (key === 'className') node.className = value;
      else if (key === 'onClick') node.addEventListener('click', value);
      else if (key === 'onInput') node.addEventListener('input', value);
      else if (key === 'onChange') node.addEventListener('change', value);
      else if (key === 'value') node.value = value == null ? '' : value;
      else if (key === 'checked') node.checked = !!value;
      else if (key === 'disabled' || key === 'required' || key === 'hidden') { if (value) node.setAttribute(key, key); }
      else if (value != null) node.setAttribute(key, value);
    });
    (children || []).forEach(function (child) { if (child) node.appendChild(typeof child === 'string' ? document.createTextNode(child) : child); });
    return node;
  }
  function value(row, path) {
    return String(path || '').split('.').reduce(function (current, key) { return current == null ? null : current[key]; }, row);
  }
  function text(row, path) {
    var current = value(row, path);
    return current == null ? '' : String(current);
  }
  function button(label, action, disabled) { return el('button', { type: 'button', text: label, onClick: action, disabled: disabled ? 'disabled' : null }); }
  function unavailable(label) { return el('span', { className: 'unavailable-note', text: label + '：未验证/不可用' }); }
  function card(title, body) { return el('section', { className: 'owned-card' }, [el('h2', { text: title }), body]); }
  function label(column) {
    var known = { id: '用户名', userName: '姓名', 'ext$.departmentname': '部门', station: '岗位', 'ext$.status': '状态', filterTest: '是否过滤测试', reportAudit: '是否报告审核人', reportIssue: '是否报告签发人' };
    return known[column.field] || column.title || column.field || '';
  }
  function columnsFrom(original) {
    return (original || []).filter(function (column) { return column && !column.checkbox && !column.index && column.field; }).slice(0, 9);
  }
  function renderGrid(target, rows, total, page, size, filters, callbacks) {
    target.replaceChildren();
    var toolbar = el('div', { className: 'owned-toolbar' });
    toolbar.appendChild(button('刷新', callbacks.refresh));
    (callbacks.toolbar || []).forEach(function (item) {
      if (item.type === 'button') {
        var action = null, disabled = true, text = (callbacks.labels || {})[item.text] || item.text;
        if (callbacks.equipmentPlanWriteActions && item.text === 'GIKAM.BUTTON.INSERT') { action = callbacks.equipmentPlanWriteActions.insert; disabled = false; }
        if (callbacks.equipmentPlanWriteActions && item.text === 'GIKAM.BUTTON.DELETE') { action = callbacks.equipmentPlanWriteActions.remove; disabled = false; }
        if (callbacks.qualificationWriteActions && item.text === 'GIKAM.BUTTON.INSERT') { action = callbacks.qualificationWriteActions.insert; disabled = false; }
        if (callbacks.qualificationWriteActions && item.text === 'GIKAM.BUTTON.DELETE') { action = callbacks.qualificationWriteActions.remove; disabled = false; }
        if (callbacks.equipmentRecordWriteActions && item.text === 'GIKAM.BUTTON.INSERT') { action = callbacks.equipmentRecordWriteActions.insert; disabled = false; }
        if (callbacks.equipmentRecordWriteActions && item.text === 'GIKAM.BUTTON.DELETE') { action = callbacks.equipmentRecordWriteActions.remove; disabled = false; }
        if (callbacks.equipmentRecordWriteActions && item.text === 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.COMPLETE') { action = callbacks.equipmentRecordWriteActions.complete; disabled = false; }
        if (callbacks.equipmentPeriodWriteActions && item.text === 'GIKAM.BUTTON.INSERT') { action = callbacks.equipmentPeriodWriteActions.insert; disabled = false; }
        if (callbacks.equipmentPeriodWriteActions && item.text === 'GIKAM.BUTTON.DELETE') { action = callbacks.equipmentPeriodWriteActions.remove; disabled = false; }
        if (callbacks.equipmentPeriodWriteActions && item.text === 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.COMPLETE') { action = callbacks.equipmentPeriodWriteActions.complete; disabled = false; }
        if (callbacks.equipmentMaintainRepairWriteActions && item.text === 'GIKAM.BUTTON.INSERT') { action = callbacks.equipmentMaintainRepairWriteActions.insert; disabled = false; }
        if (callbacks.equipmentMaintainRepairWriteActions && item.text === 'GIKAM.BUTTON.DELETE') { action = callbacks.equipmentMaintainRepairWriteActions.remove; disabled = false; }
        toolbar.appendChild(button(text, action, disabled));
      }
      else if (item.type === 'form' && item.fields && item.fields[0] && item.fields[0].field === 'showAll') { var field = item.fields[0]; toolbar.appendChild(el('label', {}, [el('input', { type: 'checkbox', 'aria-label': '显示全部库存', checked: !!callbacks.showAll, onChange: function (event) { field.onChange('showAll', event.target.checked ? '1' : '0'); } }), document.createTextNode('显示全部') ])); }
      else toolbar.appendChild(unavailable('高级筛选'));
    });
    if (callbacks.importExport && callbacks.importExport.available) {
      toolbar.appendChild(button('下载导入模板', function () { callbacks.importExport.downloadTemplate().catch(function () {}); }));
      var fileInput = el('input', { type: 'file', accept: '.xls,application/vnd.ms-excel', 'aria-label': '选择耗材导入文件', hidden: 'hidden' });
      fileInput.addEventListener('change', function (event) {
        var file = event.target.files && event.target.files[0];
        if (!file) return;
        callbacks.importExport.importFile(file).then(function () {
          if (target.isConnected && callbacks.refresh) callbacks.refresh();
        }).catch(function () {}).finally(function () { fileInput.value = ''; });
      });
      toolbar.appendChild(fileInput);
      toolbar.appendChild(button('导入耗材', function () { fileInput.click(); }));
    }
    if (callbacks.writeActions && callbacks.writeActions.available) {
      toolbar.appendChild(button('新增人员', callbacks.writeActions.insert));
      toolbar.appendChild(button('修改当前人员', callbacks.writeActions.update));
      toolbar.appendChild(button('删除当前人员', callbacks.writeActions.remove));
    }
    if (callbacks.consumableWriteActions && callbacks.consumableWriteActions.available) {
      toolbar.appendChild(button('新增耗材', callbacks.consumableWriteActions.insert));
      toolbar.appendChild(button('修改当前耗材', callbacks.consumableWriteActions.update));
      toolbar.appendChild(button('删除当前耗材', callbacks.consumableWriteActions.remove));
    }
    if (callbacks.stockWriteActions) {
      toolbar.appendChild(button('新增库存', callbacks.stockWriteActions.insert));
      toolbar.appendChild(button('入库', callbacks.stockWriteActions.storage));
      toolbar.appendChild(button('盘存', callbacks.stockWriteActions.inventory));
      toolbar.appendChild(button('废弃', callbacks.stockWriteActions.abandon));
      toolbar.appendChild(button('领用', callbacks.stockWriteActions.receive));
      toolbar.appendChild(button('删除库存', callbacks.stockWriteActions.remove));
    }
    if (callbacks.equipmentWriteActions) toolbar.appendChild(button('新增设备', callbacks.equipmentWriteActions.insert));
    if (callbacks.equipmentPlanWriteActions) { /* the original plan toolbar owns its labels and actions above */ }
    if (callbacks.testWriteActions) {
      toolbar.appendChild(button('新增可见测试', callbacks.testWriteActions.insert));
      toolbar.appendChild(button('删除所选可见测试', callbacks.testWriteActions.remove));
      toolbar.appendChild(button('复制到其他人员', callbacks.testWriteActions.copy));
    }
    toolbar.appendChild(button('写入（未验证/不可用）', null, true));
    target.appendChild(toolbar);
    if (callbacks.filterFields && callbacks.filterFields.length) {
      var controls = el('div', { className: 'filter-controls' }), inputs = [];
      controls.appendChild(el('span', { text: '筛选' }));
      callbacks.filterFields.forEach(function (field) {
        var input = el('input', { type: 'search', placeholder: field.label, value: filters[field.key] || '', 'aria-label': field.label + '筛选', onInput: function (event) { filters[field.key] = event.target.value; } });
        inputs.push(input); controls.appendChild(input);
      });
      controls.appendChild(button('查询', function () { callbacks.search(filters); }));
      controls.appendChild(button('清空', function () { Object.keys(filters).forEach(function (key) { filters[key] = ''; }); inputs.forEach(function (input) { input.value = ''; }); callbacks.search(filters); })); target.appendChild(controls);
    }
    target.appendChild(el('div', { className: 'grid-count', text: '共' + total + '条' }));
    var table = el('table'), head = el('thead'), header = el('tr');
    var visibleColumns = columnsFrom(callbacks.columns);
    if (!visibleColumns.length) visibleColumns = columnsFrom([{ field: 'id', title: '用户名' }, { field: 'userName', title: '姓名' }, { field: 'ext$.departmentname', title: '部门' }, { field: 'station', title: '岗位' }, { field: 'ext$.status', title: '状态' }]);
    header.appendChild(el('th', { text: '序号', 'data-field': '#' }));
    visibleColumns.forEach(function (column) { header.appendChild(el('th', { text: (callbacks.labels || {})[column.title] || label(column), 'data-field': column.field })); });
    head.appendChild(header); table.appendChild(head);
    var body = el('tbody');
    rows.forEach(function (row, index) {
      var tr = el('tr', { 'data-test-id': row.testId != null ? row.testId : row.id, onClick: function () {
        Array.from(body.children).forEach(function (other) { other.classList.toggle('active', other === tr); other.setAttribute('aria-selected', other === tr ? 'true' : 'false'); });
        callbacks.select(row);
      } });
      [String((page - 1) * size + index + 1)].concat(visibleColumns.map(function (column) { return text(row, column.field); })).forEach(function (cell, cellIndex) {
        var column = visibleColumns[cellIndex - 1], td = el('td', { 'data-field': column ? column.field : '#' });
        if (column && callbacks.cell && (column.type === 'link' || column.field === 'ext$.filename')) {
          var link = button(cell, function (event) { event.stopPropagation(); callbacks.cell(column.field, row); });
          if (callbacks.gridId === 'emc-home-attachment-download-list-grid') link.setAttribute('data-testid', 'attachment-download');
          td.appendChild(link);
        }
        else td.textContent = cell;
        tr.appendChild(td);
      });
      body.appendChild(tr);
    });
    if (!rows.length) { var empty = el('tr'); empty.appendChild(el('td', { colspan: String(visibleColumns.length + 1), className: 'grid-empty', text: '暂无数据' })); body.appendChild(empty); }
    table.appendChild(body); target.appendChild(el('div', { className: 'owned-grid owned-grid-scroll' }, [table]));
    var pager = el('div', { className: 'grid-pager' });
    var pages = Math.max(1, Math.ceil(total / size));
    pager.appendChild(button('上一页', function () { callbacks.page(Math.max(1, page - 1)); }, page <= 1));
    pager.appendChild(el('span', { text: '第 ' + page + ' / ' + pages + ' 页' }));
    pager.appendChild(button('下一页', function () { callbacks.page(Math.min(pages, page + 1)); }, page >= pages));
    pager.appendChild(el('label', { text: '每页' }));
    var pageSize = el('select', { 'aria-label': '每页条数', onChange: function (event) { callbacks.size(Number(event.target.value)); } });
    [10, 20, 50].forEach(function (option) { pageSize.appendChild(el('option', { value: option, text: String(option), selected: option === size ? 'selected' : null })); });
    pager.appendChild(pageSize); target.appendChild(pager);
  }
  function notify(message, error) {
    var status = document.getElementById('page-status');
    if (status) { status.textContent = message || ''; status.className = error === false ? '' : 'error'; }
  }
  global.OwnedUI = Object.freeze({ el: el, value: value, text: text, renderGrid: renderGrid, notify: notify });
})(window);
