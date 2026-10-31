(function (global) {
  'use strict';
  var QUERY = '/secure/core/module/bp/result/runs/queries/raw';
  var UPDATE = '/secure/core/module/bp/result/runs/';
  var RESULT = '/secure/core/module/bp/result/runs/';
  function request(body) {
    return fetch(QUERY, { method: 'POST', credentials: 'same-origin', cache: 'no-store',
      headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ p: body || {} }) })
      .then(function (response) { return response.text().then(function (text) {
        var value = null; try { value = text ? JSON.parse(text) : null; } catch (ignored) { }
        if (!response.ok) throw new Error(value && value.message ? value.message : '结果查询失败（HTTP ' + response.status + '）');
        return value || {};
      }); });
  }
  function init(target) {
    target.replaceChildren();
    var card = document.createElement('section'); card.className = 'owned-card result-runs';
    var title = document.createElement('h1'); title.textContent = '样品测试批结果'; card.appendChild(title);
    var form = document.createElement('form'); form.className = 'owned-inline-form';
    var run = document.createElement('input'); run.id = 'result-run-no'; run.setAttribute('aria-label', '批次编号');
    var status = document.createElement('input'); status.id = 'result-run-status'; status.setAttribute('aria-label', '状态');
    var submit = document.createElement('button'); submit.type = 'submit'; submit.textContent = '查询';
    form.appendChild(run); form.appendChild(status); form.appendChild(submit); card.appendChild(form);
    var table = document.createElement('table'); table.id = 'result-runs-table'; card.appendChild(table); target.appendChild(card);
    var resultItems = document.createElement('section'); resultItems.id = 'result-run-items'; card.appendChild(resultItems);
    function resultRequest(runId) {
      return fetch(RESULT + encodeURIComponent(runId) + '/results/queries', { method: 'POST', credentials: 'same-origin', cache: 'no-store', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: '{}' })
        .then(function (response) { return response.text().then(function (text) { var value = null; try { value = text ? JSON.parse(text) : null; } catch (ignored) { } if (!response.ok) throw new Error(value && value.message ? value.message : '结果项查询失败（HTTP ' + response.status + '）'); return value || {}; }); });
    }
    function resultAction(runId, id, path, method, body) {
      return fetch(RESULT + encodeURIComponent(runId) + '/results/' + encodeURIComponent(id) + path, { method: method, credentials: 'same-origin', cache: 'no-store', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: body == null ? undefined : JSON.stringify({ p: body }) })
        .then(function (response) { return response.text().then(function (text) { var value = null; try { value = text ? JSON.parse(text) : null; } catch (ignored) { } if (!response.ok) throw new Error(value && value.message ? value.message : '结果项更新失败（HTTP ' + response.status + '）'); return value || {}; }); });
    }
    function renderResultItems(runId, model) {
      resultItems.replaceChildren(); var title = document.createElement('h2'); title.textContent = '结果项'; resultItems.appendChild(title);
      var itemTable = document.createElement('table'); itemTable.id = 'result-items-table'; var head = document.createElement('tr');
      ['分析项', '原始结果', '报出结果', '状态', '操作'].forEach(function (label) { var cell = document.createElement('th'); cell.textContent = label; head.appendChild(cell); });
      var thead = document.createElement('thead'); thead.appendChild(head); itemTable.appendChild(thead); var body = document.createElement('tbody');
      (model.rows || []).forEach(function (item) {
        var row = document.createElement('tr'); row.setAttribute('data-result-item-id', item.id);
        [item.itemName || item.id].forEach(function (value) { var cell = document.createElement('td'); cell.textContent = value == null ? '' : value; row.appendChild(cell); });
        var originalCell = document.createElement('td'); var input = document.createElement('input'); input.id = 'result-item-value-' + item.id; input.setAttribute('aria-label', '原始结果'); input.value = item.originalResult == null ? '' : item.originalResult; originalCell.appendChild(input); row.appendChild(originalCell);
        var reportCell = document.createElement('td'); reportCell.textContent = item.reportResult == null ? '' : item.reportResult; reportCell.id = 'result-item-report-' + item.id; row.appendChild(reportCell);
        var statusCell = document.createElement('td'); statusCell.textContent = item.resultStatus == null ? '' : item.resultStatus; statusCell.id = 'result-item-status-' + item.id; row.appendChild(statusCell);
        var actionCell = document.createElement('td');
        function button(id, text, handler) { var button = document.createElement('button'); button.type = 'button'; button.id = id; button.textContent = text; button.addEventListener('click', handler); actionCell.appendChild(button); }
        button('result-item-save-' + item.id, '保存结果', function () { resultAction(runId, item.id, '', 'PUT', { originalResult: input.value }).then(function () { global.OwnedUI.notify('结果已录入。', false); return loadItems(runId); }).catch(function (error) { global.OwnedUI.notify(error.message, true); }); });
        button('result-item-calculate-' + item.id, '计算', function () { resultAction(runId, item.id, '/calculate', 'POST').then(function () { global.OwnedUI.notify('结果已计算。', false); return loadItems(runId); }).catch(function (error) { global.OwnedUI.notify(error.message, true); }); });
        button('result-item-review-' + item.id, '审核', function () { resultAction(runId, item.id, '/review', 'PUT').then(function () { global.OwnedUI.notify('结果已审核。', false); return loadItems(runId); }).catch(function (error) { global.OwnedUI.notify(error.message, true); }); });
        button('result-item-approve-' + item.id, '审批', function () { resultAction(runId, item.id, '/approve', 'PUT').then(function () { global.OwnedUI.notify('结果已审批。', false); return loadItems(runId); }).catch(function (error) { global.OwnedUI.notify(error.message, true); }); });
        row.appendChild(actionCell); body.appendChild(row);
      });
      itemTable.appendChild(body); resultItems.appendChild(itemTable);
    }
    function loadItems(runId) { return resultRequest(runId).then(function (model) { renderResultItems(runId, model); }); }
    function render(model) {
      table.replaceChildren(); var head = document.createElement('tr');
      ['批次编号', '组织', '状态', '设备', '测试'].forEach(function (label) { var cell = document.createElement('th'); cell.textContent = label; head.appendChild(cell); });
      var thead = document.createElement('thead'); thead.appendChild(head); table.appendChild(thead);
      var body = document.createElement('tbody'); (model.rows || []).forEach(function (row) {
        var tr = document.createElement('tr'); tr.setAttribute('data-result-run-id', row.id);
        [row.runNo || row.id, row.orgId || '', row.status || '', row.equiptId || '', row.testId || ''].forEach(function (value) { var td = document.createElement('td'); td.textContent = value == null ? '' : value; tr.appendChild(td); });
        var action = document.createElement('td');
        var view = document.createElement('button'); view.type = 'button'; view.id = 'result-run-view-' + row.id; view.textContent = '查看结果'; view.addEventListener('click', function () { loadItems(row.id).catch(function (error) { global.OwnedUI.notify(error.message, true); }); }); action.appendChild(view);
        var editor = document.createElement('select'); editor.id = 'result-run-status-editor-' + row.id; editor.setAttribute('aria-label', '结果状态');
        ['entered', 'audit', 'done', 'approved'].forEach(function (value) { var option = document.createElement('option'); option.value = value; option.textContent = value; option.selected = value === row.status; editor.appendChild(option); });
        var update = document.createElement('button'); update.type = 'button'; update.id = 'result-run-update-' + row.id; update.textContent = '更新状态';
        update.addEventListener('click', function () {
          update.disabled = true;
          fetch(UPDATE + encodeURIComponent(row.id), { method: 'PUT', credentials: 'same-origin', cache: 'no-store', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ p: { status: editor.value } }) })
            .then(function (response) { return response.text().then(function (text) { var value = null; try { value = text ? JSON.parse(text) : null; } catch (ignored) { } if (!response.ok) throw new Error(value && value.message ? value.message : '结果更新失败（HTTP ' + response.status + '）'); return value || {}; }); })
            .then(function () { global.OwnedUI.notify('结果状态已更新。', false); return load(); })
            .catch(function (error) { global.OwnedUI.notify(error.message, true); })
            .finally(function () { update.disabled = false; });
        });
        action.appendChild(editor); action.appendChild(update); tr.appendChild(action);
        body.appendChild(tr);
      }); table.appendChild(body);
    }
    function load() { submit.disabled = true; return request({ runNo: run.value.trim(), status: status.value.trim() }).then(render).catch(function (error) { global.OwnedUI.notify(error.message, true); }).finally(function () { submit.disabled = false; }); }
    form.addEventListener('submit', function (event) { event.preventDefault(); load(); });
    return load();
  }
  global.OwnedResultRuns = Object.freeze({ init: init });
})(window);
