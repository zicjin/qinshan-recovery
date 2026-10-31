/* Original owned UI facade. This is deliberately bounded to verified domain reads.
 * Original business config, callbacks and templates stay byte-for-byte intact.
 * Unsupported requests reject; they never yield a success or fabricated data. */
(function (global) {
  'use strict';
  var BASE = '/secure/emc/module/mdm/emp/emps', EQUIPMENT = '/secure/emc/module/mdm/equipt/equipts', CONSUMABLES = '/secure/emc/module/item/consume/consumes', ATTACHMENTS = '/secure/emc/module/item/home-attachments';
  var supportedForms = ['emc-emp-detail-base-info-form', 'emc-equipt-edit-list-detail-form', 'owned-consume-detail-form'];
  var supportedGrids = ['emc-emp-edit-list-grid', 'emc-emp-detail-qual-list-grid', 'emc-emp-detail-test-list-grid', 'emc-equipt-edit-list-grid', 'emc-equipt-plan-edit-list--grid', 'emc-equipt-record-edit-list-grid', 'emc-equipt-per-check-plan-edit-list-grid', 'emc-equipt-per-check-record-edit-list-grid', 'emc-equipt-maintain-repair-edit-list-grid', 'emc-consume-edit-list-grid', 'emc-consume-detail-stock-list-grid', 'emc-consume-detail-stock-log-list-grid', 'emc-home-attachment-download-list-grid'];
  var components = Object.create(null), pending = [], generation = 0, mount = null;
  var capabilities = { consumableImportExport: false, consumableStockWrite: false, consumableMaterialWrite: false, personnelMainWrite: false, equipmentWrite: false, equipmentPlanWrite: false };
  var labels = global.OwnedLabels || {
    'T_EMC_EMP.ID': '用户名', 'T_EMC_EMP.USERNAME': '姓名', 'T_EMC_EMP.BIRTHDAY': '出生年月',
    'T_EMC_EMP.AGE': '年龄', 'T_EMC_EMP.SEX': '性别', 'T_EMC_EMP.DEPARTMENTNAME': '科室名称',
    'T_EMC_EMP.ORGNAME': '单位名称', 'T_EMC_EMP.PROFESSIONALTITLE': '专业技术职称',
    'T_EMC_EMP.POSITION': '职务', 'T_EMC_EMP.REMARK': '备注', 'T_EMC_EMP.ELECTRONICSIGNATURE': '签名',
    'T_EMC_EMP.PIC': '照片', 'EMC.MODULE.MDM.EMP.EMPS.TAB.BASE_INFO': '基本信息',
    'EMC.MODULE.MDM.EMP.EMPS.TAB.TRAIN': '人员培训', 'EMC.MODULE.MDM.EMP.EMPS.TAB.QUAL': '资格证书',
    'EMC.MODULE.MDM.EMP.EMPS.TAB.WORKLICENSE': '分析员上岗证', 'EMC.MODULE.MDM.EMP.EMPS.TAB.TEST': '可见测试',
    'EMC.MODULE.MDM.EMP.EMPS.TAB.FILE': '附件', 'EMC.MODULE.MDM.EMP.EMPS.TAB.METHOD': '方法证书',
    'GIKAM.BUTTON.INSERT': '新增', 'GIKAM.BUTTON.DELETE': '删除', 'GIKAM.BUTTON.PREVIEW': '预览',
    'GIKAM.BUTTON.EXCEL_EXPORT': '导出', 'GIKAM.BUTTON.EXCEL_IMPORT': '导入'
  };
  labels = Object.assign({}, labels, { 'T_EMC_HOME_ATTACHMENT.FILENAME': '附件名称', 'T_EMC_HOME_ATTACHMENT.ENDDATE': '到期日期' });
  function label(key) { return labels[key] || key || ''; }
  function empty(value) { return value == null || value === '' || (Array.isArray(value) && !value.length); }
  function deferred(promise) {
    // Original auxiliary callbacks may ignore the return; mark handled without
    // converting the returned operation to a successful promise.
    promise.catch(function () {});
    return { done: function (fn) { promise.then(fn).catch(function () {}); return this; }, fail: function (fn) { promise.catch(fn); return this; }, always: function (fn) { promise.then(fn, fn).catch(function () {}); return this; }, promise: promise };
  }
  function unsupported(message) {
    var error = new Error(message || '此操作未验证/不可用，未执行。');
    global.OwnedUI.notify(error.message);
    return deferred(Promise.reject(error));
  }
  function request(url, options) {
    var target = new URL(url, location.origin), current = generation;
    if (target.origin !== location.origin) return Promise.reject(new Error('不允许跨源请求'));
    return fetch(target.href, Object.assign({ credentials: 'same-origin', headers: { Accept: 'application/json' } }, options || {})).then(function (response) {
      return response.text().then(function (body) {
        var parsed;
        try { parsed = body ? JSON.parse(body) : null; } catch (error) { throw new Error('服务器未返回 JSON（HTTP ' + response.status + '）'); }
        if (!response.ok) { var failure = new Error(parsed && (parsed.message || parsed.error) || '请求失败（HTTP ' + response.status + '）'); failure.status = response.status; if (current === generation && (response.status === 401 || response.status === 403) && [BASE, EQUIPMENT, CONSUMABLES].some(function (base) { return target.pathname.indexOf(base + '/') === 0; })) global.dispatchEvent(new Event('owned-session-denied')); throw failure; }
        return parsed;
      });
    });
  }
  function safeRead(url, options) {
    var method = (options && options.method || 'GET').toUpperCase();
    var detail = [BASE, EQUIPMENT, CONSUMABLES].some(function (base) { return url.indexOf(base + '/') === 0 && url.slice(base.length + 1).indexOf('/') < 0; });
    if (method === 'GET' && detail) return request(url, options);
    if (method === 'POST' && ([BASE, EQUIPMENT, CONSUMABLES, ATTACHMENTS].some(function (base) { return url === base + '/queries/raw'; }) ||
        new RegExp('^' + BASE + '/[^/]+/(quals|tests|credentials|methods)/queries$').test(url) ||
        new RegExp('^' + EQUIPMENT + '/[0-9]+/plans/queries$').test(url) ||
        new RegExp('^' + EQUIPMENT + '/[0-9]+/records/queries$').test(url) ||
        new RegExp('^' + EQUIPMENT + '/[0-9]+/per-check-plan/queries$').test(url) ||
        new RegExp('^' + EQUIPMENT + '/[0-9]+/per-check-record/queries$').test(url) ||
        new RegExp('^' + EQUIPMENT + '/[0-9]+/maintain/queries$').test(url) ||
        new RegExp('^' + CONSUMABLES + '/[^/]+/stocks/queries$').test(url) || new RegExp('^' + CONSUMABLES + '/stocks/[^/]+/logs/queries$').test(url))) return request(url, options);
    return Promise.reject(new Error('此接口未验证/不可用，未执行。'));
  }
  function downloadAttachment(row) {
    if (!row || row.id == null) return Promise.reject(new Error('附件标识缺失'));
    var url = ATTACHMENTS + '/' + encodeURIComponent(row.id) + '/action/get-url';
    return fetch(url, { method: 'POST', credentials: 'same-origin', headers: { Accept: 'text/plain' } })
      .then(function (response) {
        if (!response.ok) throw new Error('附件地址读取失败（HTTP ' + response.status + '）');
        return response.text();
      })
      .then(function (path) {
        if (!/^\/files\/secure\/[0-9]{8}\/[a-f0-9]{32}\/[^/]+$/.test(path)) throw new Error('附件地址格式不受支持');
        var link = document.createElement('a');
        link.href = path; link.download = String(row.ext$ && row.ext$.filename || path.split('/').pop() || 'download');
        document.body.appendChild(link); link.click(); link.remove();
      });
  }
  function node(tag, attrs, children) { return global.OwnedUI.el(tag, attrs, children); }
  function materialId(url) {
    var match = new RegExp('^' + CONSUMABLES.replace(/[.*+?^${}()|[\]\\]/g, '\\$&') + '/([^/]+)/stocks').exec(url || '');
    return match ? decodeURIComponent(match[1]) : null;
  }
  function notice(target, message) { target.replaceChildren(node('div', { className: 'owned-unavailable', text: message || '未验证/不可用；未发送请求。' })); }
  function register(config, element) {
    var component = { config: config, options: config, element: element, data: null, active: null, revision: 0,
      getData: function () { return this.data; },
      setReadonly: function () { config.readonly = true; },
      toFieldsReadonly: function () { config.readonly = true; },
      removeFieldValidator: function () { /* Forms are output-only; no edit validators execute. */ },
      getSelections: function () { return this.active ? [this.active] : []; },
      getActivedRow: function () { return this.active; },
      setOptions: function (options) { Object.assign(config, options || {}); },
      setSize: function (options) { if (options && options.west && element.firstElementChild) element.firstElementChild.style.flexBasis = options.west.width; },
      show: function () { element.hidden = false; },
      hide: function () { element.hidden = true; },
      cleanData: function () { this.revision++; this.data = null; this.active = null; notice(element, '暂无所选数据。'); },
      refresh: function () { notice(element); return deferred(Promise.reject(new Error('关联页签未验证/不可用'))); },
      insert: function () { return unsupported('新增未验证/不可用，未执行。'); },
      deleteRows: function () { return unsupported('删除未验证/不可用，未执行。'); },
      setData: function () { return unsupported('修改未验证/不可用，未执行。'); },
      activeRowByIndex: function () { throw new Error('此组件不支持行选择'); }
    };
    if (config.id) { components[config.id] = component; element.id = config.id; }
    component.grid = { cleanData: function () { component.cleanData(); }, refresh: function () { return unsupported('附件未验证/不可用，未发送请求。'); } };
    return component;
  }
  function renderForm(config, target) {
    var element = node('section', { className: 'owned-card' }), component = register(config, element);
    var supported = supportedForms.indexOf(config.id) >= 0;
    function draw(data) {
      element.replaceChildren();
      if (!data) { notice(element, '请选择列表中的条目查看基本信息。'); return; }
      var fields = node('div', { className: 'owned-fields' });
      (config.fields || []).filter(function (field) { return field.field; }).forEach(function (field) {
        var item = node('div', { className: 'owned-field' + (field.colspan ? ' wide' : '') });
        item.appendChild(node('span', { text: label(field.title || field.field) }));
        if (field.type === 'image') item.appendChild(node('output', { className: 'image-unavailable', text: '图片/上传未验证，未加载。' }));
        else item.appendChild(node('output', { 'data-field': field.field, text: global.OwnedUI.text(data, field.field) }));
        fields.appendChild(item);
      });
      element.appendChild(fields);
    }
    if (supported) {
      component.cleanData = function () { component.revision++; component.data = null; draw(null); };
      component.refresh = function (options) {
        Object.assign(config, options || {});
        var current = generation;
        if (!config.url) { component.cleanData(); return deferred(Promise.reject(new Error('尚未选择人员'))); }
        // Clear old data before a read so failed/out-of-scope reads never keep
        // a previously selected person's fields visible.
        component.cleanData(); var revision = component.revision;
        var promise = safeRead(config.url).then(function (data) {
          if (current !== generation || revision !== component.revision) return;
          component.data = data; draw(data);
          if (data && config.onLoadSuccess) config.onLoadSuccess.call(component, data);
        }).catch(function (error) { if (current === generation) { component.cleanData(); global.OwnedUI.notify(error.message); } throw error; });
        return deferred(promise);
      };
      draw(null);
      if (config.url) pending.push(function () { component.refresh(); });
    } else notice(element, '此原始表单未验证/不可用。');
    target.appendChild(element);
    if (supported && config.onRendered) config.onRendered.call(component);
    return component;
  }
  function renderGrid(config, target) {
    var element = node('section', { className: 'owned-card' }), component = register(config, element);
    component.data = [];
    if (supportedGrids.indexOf(config.id) < 0) {
      notice(element, '此关联查询及写入未验证/不可用；未发送请求。');
      target.appendChild(element); return component;
    }
    var primary = /edit-list-grid$/.test(config.id), equipment = config.id === 'emc-equipt-edit-list-grid', consume = config.id === 'emc-consume-edit-list-grid', equipmentPlan = config.id === 'emc-equipt-plan-edit-list--grid', equipmentRecord = config.id === 'emc-equipt-record-edit-list-grid', periodPlan = config.id === 'emc-equipt-per-check-plan-edit-list-grid', periodRecord = config.id === 'emc-equipt-per-check-record-edit-list-grid', maintainRepair = config.id === 'emc-equipt-maintain-repair-edit-list-grid';
    var page = 1, size = 20, total = 0, filters = { name: '', id: '' };
    var fields = !primary || equipmentRecord || periodPlan || periodRecord || maintainRepair ? [] : equipment ? [{ key: 'name', label: '设备名称', filter: 'EQUIPTNAME_CISC' }, { key: 'id', label: '设备编号', filter: 'EQUIPTID_SEQ' }] : consume ? [{ key: 'name', label: '材料名称', filter: 'MTLNAME_CISC' }, { key: 'id', label: '材料编号', filter: 'ID_SEQ' }] : [{ key: 'name', label: '姓名', filter: 'USERNAME_CISC' }, { key: 'id', label: '用户名', filter: 'ID_SEQ' }];
    function clearRelated() {
      var ids = equipment ? ['emc-equipt-edit-list-detail-form'] : consume ? ['owned-consume-detail-form', 'emc-consume-detail-stock-list-grid', 'emc-consume-detail-stock-log-list-grid'] : config.id === 'emc-consume-detail-stock-list-grid' ? ['emc-consume-detail-stock-log-list-grid'] : config.id === 'emc-emp-edit-list-grid' ? ['emc-emp-detail-base-info-form', 'emc-emp-detail-qual-list-grid', 'emc-emp-detail-test-list-grid'] : [];
      if (config.id === 'emc-emp-edit-list-grid') {
        if (global.OwnedPersonnelTestEditor) global.OwnedPersonnelTestEditor.reset();
        if (components['emc-emp-detail-test-list-grid']) components['emc-emp-detail-test-list-grid'].config.url = null;
      }
      ids.forEach(function (id) { if (components[id]) components[id].cleanData(); });
    }
    component.activeRowByIndex = function (index) {
      component.active = component.data[index] || null;
      clearRelated();
      if (component.active && config.onRowActive) config.onRowActive.call(component, index, component.active);
      if (component.active && consume && components['owned-consume-detail-form']) components['owned-consume-detail-form'].refresh({ url: CONSUMABLES + '/' + encodeURIComponent(component.active.id) });
    };
    function draw() {
      function editStock(action) {
        global.OwnedConsumableStockEditor.open(action, materialId(config.url), component.active, function () {
          return component.refresh().promise.then(function () {
            var parent = components['emc-consume-edit-list-grid'];
            var id = materialId(config.url);
            return (parent ? parent.refreshRowById(id).promise : Promise.resolve()).then(function () {
              if (components['owned-consume-detail-form']) return components['owned-consume-detail-form'].refresh({ url: CONSUMABLES + '/' + encodeURIComponent(id) }).promise;
            });
          });
        });
      }
      function editTest(action) {
        var match = new RegExp('^' + BASE + '/([^/]+)/tests/queries(?:\\?.*)?$').exec(config.url || '');
        global.OwnedPersonnelTestEditor.open(action, match && decodeURIComponent(match[1]), component.active, function () { return component.refresh().promise; });
      }
      global.OwnedUI.renderGrid(element, component.data, total, page, size, filters, {
        gridId: config.id,
        columns: config.columns, labels: labels, toolbar: config.toolbar, filterFields: fields,
        importExport: consume && capabilities.consumableImportExport && global.OwnedConsumablesImportExport ? {
          available: true,
          downloadTemplate: function () { return global.OwnedConsumablesImportExport.downloadTemplate(); },
          importFile: function (file) { return global.OwnedConsumablesImportExport.importFile(file); }
        } : null,
        writeActions: config.id === 'emc-emp-edit-list-grid' && capabilities.personnelMainWrite && global.OwnedPersonnelEditor ? {
          available: true,
          insert: function () { global.OwnedPersonnelEditor.open('insert', null, function () { return component.refresh().promise; }); },
          update: function () { global.OwnedPersonnelEditor.open('update', component.active, function () { return component.refresh().promise; }); },
          remove: function () { global.OwnedPersonnelEditor.open('delete', component.active, function () { return component.refresh().promise; }); }
        } : null,
        consumableWriteActions: config.id === 'emc-consume-edit-list-grid' && capabilities.consumableMaterialWrite && global.OwnedConsumableEditor ? {
          available: true,
          insert: function () { global.OwnedConsumableEditor.open('insert', null, function () { return component.refresh().promise; }); },
          update: function () { global.OwnedConsumableEditor.open('update', component.active, function () { return component.refresh().promise; }); },
          remove: function () { global.OwnedConsumableEditor.open('delete', component.active, function () { return component.refresh().promise; }); }
        } : null,
        stockWriteActions: config.id === 'emc-consume-detail-stock-list-grid' && capabilities.consumableStockWrite && global.OwnedConsumableStockEditor ? {
          insert: function () { editStock('insert'); }, storage: function () { editStock('storage'); },
          inventory: function () { editStock('inventory'); }, abandon: function () { editStock('abandon'); },
          receive: function () { editStock('receive'); }, remove: function () { editStock('delete'); }
        } : null,
        equipmentWriteActions: config.id === 'emc-equipt-edit-list-grid' && capabilities.equipmentWrite && global.OwnedEquipmentEditor ? {
          insert: function () { global.OwnedEquipmentEditor.open(function () { return component.refresh().promise; }); }
        } : null,
        equipmentPlanWriteActions: equipmentPlan && capabilities.equipmentPlanWrite && global.OwnedEquipmentPlanEditor ? {
          insert: function () {
            var equipmentRow = components['emc-equipt-edit-list-grid'] && components['emc-equipt-edit-list-grid'].getActivedRow();
            if (!equipmentRow) return global.OwnedUI.notify('请先选择设备。');
            global.OwnedEquipmentPlanEditor.open(equipmentRow.id, function () { return component.refresh().promise; });
          },
          remove: function () {
            var row = component.getActivedRow();
            if (!row) return global.OwnedUI.notify('请先选择维护计划。');
            global.OwnedEquipmentPlanEditor.remove(row, function () { return component.refresh().promise; });
          }
        } : null,
        equipmentRecordWriteActions: equipmentRecord && capabilities.equipmentPlanWrite && global.OwnedEquipmentRecordEditor ? {
          insert: function () {
            var equipmentRow = components['emc-equipt-edit-list-grid'] && components['emc-equipt-edit-list-grid'].getActivedRow();
            if (!equipmentRow) return global.OwnedUI.notify('请先选择设备。');
            global.OwnedEquipmentRecordEditor.open(equipmentRow.id, function () { return component.refresh().promise; });
          },
          remove: function () {
            var row = component.getActivedRow();
            if (!row) return global.OwnedUI.notify('请先选择维护记录。');
            global.OwnedEquipmentRecordEditor.remove(row, function () { return component.refresh().promise; });
          },
          complete: function () {
            var row = component.getActivedRow();
            if (!row) return global.OwnedUI.notify('请先选择维护记录。');
            global.OwnedEquipmentRecordEditor.complete(row, function () {
              return component.refresh().promise.then(function () {
                var plans = components['emc-equipt-plan-edit-list--grid'];
                return plans ? plans.refresh().promise : null;
              });
            });
          }
        } : null,
        equipmentPeriodWriteActions: (periodPlan || periodRecord) && capabilities.equipmentPlanWrite && global.OwnedEquipmentPeriodEditor ? {
          insert: function () {
            var equipmentRow = components['emc-equipt-edit-list-grid'] && components['emc-equipt-edit-list-grid'].getActivedRow();
            if (!equipmentRow) return global.OwnedUI.notify('请先选择设备。');
            if (periodPlan) global.OwnedEquipmentPeriodEditor.openPlan(equipmentRow.id, function () { return component.refresh().promise; });
            else global.OwnedEquipmentPeriodEditor.openRecord(equipmentRow.id, function () { return component.refresh().promise; });
          },
          remove: function () {
            var row = component.getActivedRow();
            if (!row) return global.OwnedUI.notify(periodPlan ? '请先选择期间核查计划。' : '请先选择期间核查记录。');
            if (periodPlan) global.OwnedEquipmentPeriodEditor.removePlan(row, function () { return component.refresh().promise; });
            else global.OwnedEquipmentPeriodEditor.removeRecord(row, function () { return component.refresh().promise; });
          },
          complete: function () {
            var row = component.getActivedRow();
            if (!row) return global.OwnedUI.notify('请先选择期间核查记录。');
            if (periodRecord) global.OwnedEquipmentPeriodEditor.completeRecord(row, function () {
              return component.refresh().promise;
            });
          }
        } : null,
        equipmentMaintainRepairWriteActions: maintainRepair && capabilities.equipmentPlanWrite && global.OwnedEquipmentMaintainRepairEditor ? {
          insert: function () {
            var equipmentRow = components['emc-equipt-edit-list-grid'] && components['emc-equipt-edit-list-grid'].getActivedRow();
            if (!equipmentRow) return global.OwnedUI.notify('请先选择设备。');
            global.OwnedEquipmentMaintainRepairEditor.open(equipmentRow.id, function () { return component.refresh().promise; });
          },
          remove: function () {
            var row = component.getActivedRow();
            if (!row) return global.OwnedUI.notify('请先选择维护维修记录。');
            global.OwnedEquipmentMaintainRepairEditor.remove(row, function () { return component.refresh().promise; });
          }
        } : null,
        testWriteActions: config.id === 'emc-emp-detail-test-list-grid' && capabilities.personnelMainWrite && global.OwnedPersonnelTestEditor ? {
          insert: function () { editTest('insert'); }, remove: function () { editTest('delete'); }, copy: function () { editTest('copy'); }
        } : null,
        qualificationWriteActions: config.id === 'emc-emp-detail-qual-list-grid' && capabilities.personnelMainWrite && global.OwnedPersonnelQualificationEditor ? {
          insert: function () { var match = new RegExp('^' + BASE + '/([^/]+)/quals/queries(?:\\?.*)?$').exec(config.url || ''); global.OwnedPersonnelQualificationEditor.open('insert', match && decodeURIComponent(match[1]), null, function () { return component.refresh().promise; }); },
          remove: function () { var match = new RegExp('^' + BASE + '/([^/]+)/quals/queries(?:\\?.*)?$').exec(config.url || ''); global.OwnedPersonnelQualificationEditor.open('delete', match && decodeURIComponent(match[1]), component.active, function () { return component.refresh().promise; }); }
        } : null,
        cell: (config.id === 'emc-home-attachment-download-list-grid'
          || (config.columns || []).some(function (column) { return column && column.field === 'ext$.filename'; }))
          ? function (field, row) {
              if (typeof config.onCellClick === 'function') config.onCellClick.call(component, field, row);
              else if (field === 'ext$.filename') downloadAttachment(row).catch(function (error) { global.OwnedUI.notify(error.message); });
            }
          : null,
        showAll: config.id === 'emc-consume-detail-stock-list-grid' && config.requestData && config.requestData.status_SEQ === null,
        refresh: function () { component.refresh(); },
        search: function (next) { filters = next; page = 1; component.refresh(); },
        page: function (number) { page = number; component.refresh(); },
        size: function (number) { size = number; page = 1; component.refresh(); },
        select: function (row) { component.activeRowByIndex(component.data.indexOf(row)); },
        status: global.OwnedUI.notify
      });
    }
    component.cleanData = function () { component.revision++; component.data = []; component.active = null; total = 0; draw(); clearRelated(); };
    component.refreshRowById = function (id) {
      if (!consume) return deferred(Promise.reject(new Error('此列表不支持单行刷新')));
      var current = generation, revision = component.revision;
      return deferred(safeRead(CONSUMABLES + '/' + encodeURIComponent(id)).then(function (row) {
        if (current !== generation || revision !== component.revision || !row) return;
        component.data.forEach(function (value) { if (value.id === row.id) Object.assign(value, row); });
        draw();
      }));
    };
    component.refresh = function (options) {
      Object.assign(config, options || {});
      var hiddenPanel = element.closest ? element.closest('.owned-tab-panel') : null;
      if (hiddenPanel && hiddenPanel.hidden) {
        config._pendingRefresh = true;
        return deferred(Promise.resolve());
      }
      var current = generation, revision = ++component.revision, f = {}, envelope = { n: page, s: size }, requestData = config.requestData || {};
      Object.keys(requestData).forEach(function (key) {
        if (key === 'readonly' || key === 'userOrg_filter' || key.toLowerCase() === 'orgid_seq' && empty(requestData[key]) || requestData[key] === undefined) return;
        if (equipment || !primary) envelope[key] = requestData[key]; else f[key] = requestData[key];
      });
      fields.forEach(function (field) { if (String(filters[field.key] || '').trim()) f[field.filter] = String(filters[field.key]).trim(); });
      if (Object.keys(f).length) envelope.f = f;
      if (!config.url) { component.cleanData(); return deferred(Promise.reject(new Error('尚未选择数据'))); }
      var promise = safeRead(config.url, { method: 'POST', headers: { 'Content-Type': 'application/json', Accept: 'application/json' }, body: JSON.stringify({ p: envelope }) }).then(function (data) {
        if (current !== generation || revision !== component.revision) return;
        if (!data || !Array.isArray(data.rows) || !Number.isFinite(Number(data.total))) throw new Error('列表响应格式无效');
        component.data = data.rows; total = Number(data.total); component.active = null; draw(); clearRelated();
        if (config.onLoadSuccess) config.onLoadSuccess.call(component, component.data);
        if (component.data.length && !component.active && config.onRowActive) component.activeRowByIndex(0);
      }).catch(function (error) {
        if (current === generation && revision === component.revision) { component.cleanData(); global.OwnedUI.notify(error.message); }
        throw error;
      });
      return deferred(promise);
    };
    target.appendChild(element); draw();
    if (config.onRendered) config.onRendered.call(component);
    if (config.url) pending.push(function () { component.refresh(); });
    return component;
  }
  function renderTab(config, target) {
    var element = node('section', { className: 'owned-tabs' }), component = register(config, element);
    var buttons = node('div', { className: 'owned-tab-buttons', role: 'tablist' });
    element.appendChild(buttons);
    (config.panels || []).forEach(function (panel, index) {
      var container = node('div', { className: 'owned-tab-panel', role: 'tabpanel' });
      var title = label(panel.title), available = (panel.items || []).some(function (item) { return supportedForms.indexOf(item.id) >= 0 || supportedGrids.indexOf(item.id) >= 0; });
      var button = node('button', { type: 'button', role: 'tab', text: title + (available ? '' : '（未验证/不可用）'), 'aria-selected': index === 0 ? 'true' : 'false', onClick: function () {
        Array.from(buttons.children).forEach(function (child) { child.setAttribute('aria-selected', child === button ? 'true' : 'false'); });
        Array.from(element.children).slice(1).forEach(function (child) { child.hidden = child !== container; });
        function activate(items) {
          (items || []).forEach(function (item) {
            if (!item) return;
            if (item.id && components[item.id] && components[item.id].config && components[item.id].config._pendingRefresh) {
              components[item.id].config._pendingRefresh = false;
              components[item.id].refresh();
            }
            activate(item.items);
            ['north', 'south', 'east', 'west', 'center'].forEach(function (region) { if (item[region] && item[region].items) activate(item[region].items); });
          });
        }
        activate(panel.items);
        if (config.onActived) config.onActived.call(component, { index: index }, panel.title);
      } });
      buttons.appendChild(button); element.appendChild(container); container.hidden = index !== 0;
      (panel.items || []).forEach(function (item) { render(item, container); });
    });
    target.appendChild(element); if (config.onRendered) config.onRendered.call(component); return component;
  }
  function render(config, target) {
    if (!config) return null;
    config.type = String(config.type || '').toLowerCase();
    if (config.type === 'grid' || config.type === 'simpleGrid') return renderGrid(config, target);
    if (config.type === 'form') return renderForm(config, target);
    if (config.type === 'tab') return renderTab(config, target);
    if (config.type === 'layout') {
      var element = node('div', { className: 'owned-layout' }), component = register(config, element);
      ['north', 'west', 'center', 'south', 'east'].forEach(function (region) {
        if (!config[region]) return;
        var part = node('section', { className: 'owned-region' + (region === 'west' ? ' west' : '') });
        (config[region].items || []).forEach(function (item) { render(item, part); }); element.appendChild(part);
      });
      target.appendChild(element); return component;
    }
    var unavailable = node('section', { className: 'owned-unavailable', text: '此组件及相关操作未验证/不可用。' });
    var blocked = register(config, unavailable); target.appendChild(unavailable); return blocked;
  }
  global.IFM_CONTEXT = '';
  // Missing org context is represented as missing, never inferred from account
  // orgId or substituted with a concrete parent organization.
  global.workspace = { user: { ext$: {} }, window: { $dom: null, goBack: function () { history.back(); } } };
  var loaded = ["/static/emc/module/item/home-attachments/emc-home-attachment.js", "/static/emc/module/item/home-attachments/emc-home-attachment-download-list.js", "/static/emc/module/mdm/emp/emps/emc-emp.js", "/static/emc/module/mdm/emp/emps/emc-emp-edit-list.js", "/static/emc/module/mdm/emp/emps/emc-emp-detail.js", "/static/emc/module/mdm/equipt/equipts/emc-equipt-plan.js", "/static/emc/module/mdm/equipt/equipts/emc-equipt-record.js", "/static/emc/module/mdm/equipt/equipts/emc-equipt.js", "/static/emc/module/mdm/equipt/equipts/emc-equipt-edit-list.js", "/static/emc/module/mdm/equipt/equipts/emc-equipt-use-record.js", "/static/emc/module/mdm/equipt/equipts/emc-equipt-per-check-plan.js", "/static/emc/module/mdm/equipt/equipts/emc-equipt-per-check-plan-add.js", "/static/emc/module/mdm/equipt/equipts/emc-equipt-per-check-record.js", "/static/emc/module/mdm/equipt/equipts/emc-equipt-per-check-record-add.js", "/static/emc/module/item/consume/consumes/emc-consume.js", "/static/emc/module/item/consume/consumes/emc-consume-edit-list.js", "/static/emc/module/qrcode/qrcode.js"];
  global.Gikam = {
    width: { XL: 720 }, getPageObject: function (object) { return object; }, getElParam: function (value) { return value || {}; },
    requirejs: function (paths) { paths.forEach(function (path) { if (loaded.indexOf(path) < 0) throw new Error('未提供的页面脚本：' + path); }); },
    isEmpty: empty,
    printf: function (template, params) { return String(template).replace(/\{([^}]+)\}/g, function (_, key) { return params && params[key] != null ? params[key] : ''; }); },
    getAsyncComp: function (id) { return deferred(components[id] && (supportedForms.indexOf(id) >= 0 || supportedGrids.indexOf(id) >= 0) ? Promise.resolve(components[id]) : Promise.reject(new Error('关联组件未验证/不可用'))); },
    param: function (params) { return '?' + new URLSearchParams(params).toString(); },
    getComp: function (id) { if (!components[id]) throw new Error('未提供的页面组件：' + id); return components[id]; },
    create: function (type, config) {
      if (type !== 'layout') return unsupported('此组件未验证/不可用，未执行。');
      config.type = type; var component = render(config, config.renderTo === document.body && mount ? mount : config.renderTo);
      if (components['emc-consume-edit-list-grid']) renderForm({ type: 'form', id: 'owned-consume-detail-form', fields: global.emcConsume.getBaseInfoFormFields() }, config.renderTo);
      var work = pending; pending = []; work.forEach(function (fn) { fn(); }); return component;
    },
    alert: function (message) { global.OwnedUI.notify(label(message)); },
    getJson: function (url) { return deferred(safeRead(url)); },
    getJsonWrapper: function (value) { return { p: value }; },
    post: function (url, body) { return deferred(safeRead(url, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) })); },
    setMount: function (element) { mount = element; },
    propI18N: function (key) { return label(key); },
    postText: function (url) {
      if (!new RegExp('^' + ATTACHMENTS + '/[1-9][0-9]*/action/get-url$').test(url)) return unsupported();
      return deferred(fetch(url, { method: 'POST', credentials: 'same-origin', headers: { Accept: 'text/plain' } }).then(function (response) {
        if (!response.ok) throw new Error('附件地址读取失败（HTTP ' + response.status + '）');
        return response.text();
      }).then(function (url) {
        if (!/^\/files\/secure\/[0-9]{8}\/[a-f0-9]{32}\/[^/]+$/.test(url)) throw new Error('附件地址格式不受支持');
        return url;
      }).catch(function (error) { global.OwnedUI.notify(error.message); throw error; }));
    },
    setCapabilities: function (next) { capabilities = Object.assign({ consumableImportExport: false, consumableStockWrite: false, consumableMaterialWrite: false, personnelMainWrite: false, equipmentWrite: false, equipmentPlanWrite: false }, next || {}); },
    reset: function () { generation++; mount = null; components = Object.create(null); pending = []; if (global.OwnedPersonnelEditor) global.OwnedPersonnelEditor.reset(); if (global.OwnedPersonnelTestEditor) global.OwnedPersonnelTestEditor.reset(); if (global.OwnedEquipmentEditor) global.OwnedEquipmentEditor.reset(); if (global.OwnedConsumableStockEditor) global.OwnedConsumableStockEditor.reset(); if (global.OwnedConsumableEditor) global.OwnedConsumableEditor.reset(); }
  };
  ['put', 'del', 'confirm', 'viewReport', 'viewAudit', 'importExcel', 'emcDownloadFile', 'openFile', 'getText'].forEach(function (name) { global.Gikam[name] = function () { return unsupported('此操作未验证/不可用，未执行。'); }; });
  global.OwnedRequest = request;
})(window);
