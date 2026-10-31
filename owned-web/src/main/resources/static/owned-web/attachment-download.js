(function (global) {
  'use strict';
  var QUERY = '/secure/emc/module/item/home-attachments/queries/raw';
  var BASE = '/secure/emc/module/item/home-attachments';
  var root;
  function node(tag, attrs, children) {
    var value = document.createElement(tag);
    Object.keys(attrs || {}).forEach(function (key) {
      if (key === 'text') value.textContent = attrs[key];
      else if (key === 'onClick') value.addEventListener('click', attrs[key]);
      else if (key === 'className') value.className = attrs[key];
      else if (attrs[key] != null) value.setAttribute(key, attrs[key]);
    });
    (children || []).forEach(function (child) { if (child) value.appendChild(child); });
    return value;
  }
  function status(message, error) {
    var target = document.getElementById('page-status');
    if (target) { target.textContent = message || ''; target.className = error ? 'error' : ''; }
  }
  function request(url, options) {
    return fetch(url, Object.assign({ credentials: 'same-origin', cache: 'no-store' }, options || {}))
      .then(function (response) {
        return response.text().then(function (body) {
          if (!response.ok) { var failure = new Error('附件请求失败（HTTP ' + response.status + '）'); failure.status = response.status; throw failure; }
          return body;
        });
      });
  }
  function rows() {
    return request(QUERY, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
      body: JSON.stringify({ p: { targetType: 'download', activatedFlag_SEQ: '1' } })
    }).then(function (body) { return JSON.parse(body || '{}').rows || []; });
  }
  function download(row) {
    return request(BASE + '/' + encodeURIComponent(row.id) + '/action/get-url', {
      method: 'POST', headers: { Accept: 'text/plain' }
    }).then(function (url) {
      if (!/^\/files\/secure\/[0-9]{8}\/[a-f0-9]{32}\/[^/]+$/.test(url)) throw new Error('附件地址格式不受支持');
      var link = document.createElement('a');
      link.href = url; link.download = row.ext$ && row.ext$.filename || url.split('/').pop() || 'download';
      document.body.appendChild(link); link.click(); link.remove();
    });
  }
  function render(items) {
    root.replaceChildren();
    var toolbar = node('div', { className: 'owned-toolbar' });
    toolbar.appendChild(node('button', { type: 'button', text: '刷新', onClick: refresh }));
    root.appendChild(toolbar);
    var table = node('table'), head = node('thead'), header = node('tr');
    ['文件名', '到期日期', '操作'].forEach(function (text) { header.appendChild(node('th', { text: text })); });
    head.appendChild(header); table.appendChild(head);
    var body = node('tbody');
    items.forEach(function (row) {
      var line = node('tr');
      var filename = row.ext$ && row.ext$.filename || '（未上传）';
      var cell = node('td');
      var button = node('button', { type: 'button', 'data-testid': 'attachment-download', text: filename,
        onClick: function () { download(row).catch(function (error) { status(error.message, true); }); } });
      cell.appendChild(button); line.appendChild(cell);
      line.appendChild(node('td', { text: String(row.endDate || '') }));
      line.appendChild(node('td', { text: row.activatedFlag === '1' ? '启用' : '停用' }));
      body.appendChild(line);
    });
    if (!items.length) { var empty = node('tr'); empty.appendChild(node('td', { colspan: '3', text: '暂无数据' })); body.appendChild(empty); }
    table.appendChild(body); root.appendChild(table);
  }
  function refresh() {
    return rows().then(render).catch(function (error) {
      if (error.status === 403) { render([]); status(''); return []; }
      status(error.message, true); throw error;
    });
  }
  global.OwnedAttachmentDownload = { init: function (target) { root = target || document.getElementById('owned-workspace'); root.setAttribute('aria-label', '附件下载'); return refresh(); } };
})(window);
