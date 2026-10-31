(function (global) {
  'use strict';
  var BASE = '/secure/emc/module/item/home-attachments';
  var MANAGEMENT = '/owned/attachments/management/query';
  var root;
  function node(tag, attrs, children) {
    var value = document.createElement(tag);
    Object.keys(attrs || {}).forEach(function (key) { if (key === 'text') value.textContent = attrs[key]; else if (key === 'onClick') value.addEventListener('click', attrs[key]); else if (key === 'onChange') value.addEventListener('change', attrs[key]); else if (key === 'className') value.className = attrs[key]; else if (attrs[key] != null) value.setAttribute(key, attrs[key]); });
    (children || []).forEach(function (child) { if (child) value.appendChild(typeof child === 'string' ? document.createTextNode(child) : child); });
    return value;
  }
  function request(url, options) {
    return fetch(url, Object.assign({ credentials: 'same-origin', cache: 'no-store', headers: { Accept: 'application/json' } }, options || {})).then(function (response) {
      return response.text().then(function (body) { var parsed; try { parsed = body ? JSON.parse(body) : null; } catch (error) { parsed = body; } if (!response.ok) { var failure = new Error(parsed && parsed.message || '请求失败（HTTP ' + response.status + '）'); failure.status = response.status; throw failure; } return parsed; });
    });
  }
  function wrapper(rows) { return JSON.stringify({ b: ['', rows] }); }
  function status(message, error) { var target = document.getElementById('page-status'); if (target) { target.textContent = message || ''; target.className = error ? 'error' : ''; } }
  function value(row, path) { return path.split('.').reduce(function (current, key) { return current == null ? null : current[key]; }, row); }
  function button(text, action) { return node('button', { type: 'button', text: text, onClick: action }); }
  function refresh() {
    return request(MANAGEMENT, { method: 'POST', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: '{}' }).then(function (body) { render(body && body.rows || []); }).catch(function (error) { status(error.message, true); throw error; });
  }
  function render(rows) {
    root.replaceChildren();
    var toolbar = node('div', { className: 'owned-toolbar' });
    toolbar.appendChild(button('刷新', refresh));
    var date = node('input', { type: 'date', id: 'attachment-end-date', value: '2030-01-01' });
    var file = node('input', { type: 'file', id: 'attachment-file' });
    toolbar.appendChild(date); toolbar.appendChild(file);
    toolbar.appendChild(button('新增并上传', function () { create(date.value, file.files[0]); }));
    root.appendChild(toolbar);
    var table = node('table'), head = node('thead'), header = node('tr');
    ['ID', '文件名', '到期日期', '状态', '操作'].forEach(function (text) { header.appendChild(node('th', { text: text })); }); head.appendChild(header); table.appendChild(head);
    var body = node('tbody');
    rows.forEach(function (row) {
      var line = node('tr'); line.appendChild(node('td', { text: String(row.id) })); line.appendChild(node('td', { text: String(value(row, 'ext$.filename') || '（未上传）') })); line.appendChild(node('td', { text: String(row.endDate || '') })); line.appendChild(node('td', { text: row.activatedFlag === '1' ? '启用' : '停用' }));
      var actions = node('td'); actions.appendChild(button(row.activatedFlag === '1' ? '停用' : '启用', function () { activate(row); }));
      var user = node('input', { type: 'text', placeholder: '用户ID', 'aria-label': '授权用户 ' + row.id }); actions.appendChild(user); actions.appendChild(button('授权', function () { empower(row, user.value); })); actions.appendChild(button('删除', function () { remove(row); })); line.appendChild(actions); body.appendChild(line);
    });
    if (!rows.length) { var empty = node('tr'); empty.appendChild(node('td', { colspan: '5', text: '暂无附件' })); body.appendChild(empty); }
    table.appendChild(body); root.appendChild(table);
  }
  function create(endDate, file) {
    return request(BASE, { method: 'POST', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: wrapper([{ activatedFlag: '0', endDate: endDate + 'T00:00:00' }]) }).then(function (body) {
      var id = Number(String(body).trim()); if (!id || !file) return refresh(); var data = new FormData(); data.append('targetId', 'T_EMC_HOME_ATTACHMENT$' + id); data.append('bizCategory', 'document'); data.append('file', file); return request('/core/module/item/files', { method: 'POST', headers: { Accept: 'application/json' }, body: data });
    }).then(refresh).then(function () { status('附件已创建'); }).catch(function (error) { status(error.message, true); });
  }
  function activate(row) { var active = row.activatedFlag !== '1'; return request(BASE + '/action/' + (active ? 'activate' : 'deactivate'), { method: 'PUT', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: wrapper([{ id: row.id }]) }).then(refresh).catch(function (error) { status(error.message, true); }); }
  function empower(row, userId) { if (!userId) { status('用户ID必填', true); return; } return request(BASE + '/permissions', { method: 'POST', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: wrapper([{ attachmentId: row.id, userId: userId }]) }).then(refresh).catch(function (error) { status(error.message, true); }); }
  function remove(row) { return request(BASE, { method: 'DELETE', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: wrapper([{ id: row.id }]) }).then(refresh).catch(function (error) { status(error.message, true); }); }
  global.OwnedAttachmentManagement = { init: function () { root = document.getElementById('owned-workspace'); root.setAttribute('aria-label', '附件管理'); refresh(); } };
})(window);
