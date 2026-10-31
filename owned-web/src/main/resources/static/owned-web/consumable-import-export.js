(function (global) {
  'use strict';
  function notify(message, error) {
    if (global.OwnedUI && global.OwnedUI.notify) global.OwnedUI.notify(message, error !== false);
  }
  function responseBody(response, fallback) {
    return response.text().then(function (body) {
      var parsed = null;
      try { parsed = body ? JSON.parse(body) : null; } catch (ignored) {}
      if (!response.ok) {
        var failure = new Error(parsed && parsed.message || fallback + '（HTTP ' + response.status + '）');
        failure.status = response.status; throw failure;
      }
      return parsed;
    });
  }
  function downloadTemplate() {
    return fetch('/core/module/item/files/emcConsumeExportTemplate/action/download-template/emcConsumeExportTemplate', {
      credentials: 'same-origin', cache: 'no-store'
    }).then(function (response) {
      if (!response.ok) throw new Error('模板下载失败（HTTP ' + response.status + '）');
      if ((response.headers.get('Content-Type') || '').split(';')[0] !== 'application/vnd.ms-excel') throw new Error('模板响应格式无效');
      return response.blob();
    }).then(function (blob) {
      var href = URL.createObjectURL(blob), link = document.createElement('a');
      link.href = href; link.download = 'emcConsumeExportTemplate.xls'; document.body.appendChild(link); link.click(); link.remove();
      setTimeout(function () { URL.revokeObjectURL(href); }, 1000);
      notify('耗材导入模板已下载', false); return blob;
    }).catch(function (error) { notify(error.message); throw error; });
  }
  function importFile(file, onSuccess) {
    if (!file) return Promise.reject(new Error('请选择 .xls 文件。'));
    var data = new FormData(); data.append('file', file);
    return fetch('/core/module/item/files/owned-import/action/import/emcConsumeImportFileFunction', {
      method: 'POST', credentials: 'same-origin', cache: 'no-store', body: data
    }).then(function (response) { return responseBody(response, '耗材导入失败'); })
      .then(function (body) {
        if (!body || body.status !== 'success' || !Number.isInteger(body.rows) || body.rows < 0) throw new Error('导入响应格式无效');
        notify('耗材导入完成（' + body.rows + ' 行）', false); if (onSuccess) onSuccess(body); return body;
      })
      .catch(function (error) { notify(error.message); throw error; });
  }
  global.OwnedConsumablesImportExport = Object.freeze({ downloadTemplate: downloadTemplate, importFile: importFile });
})(window);
