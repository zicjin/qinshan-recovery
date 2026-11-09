"use strict";

const assert = require("assert");
const fs = require("fs");
const path = require("path");
const vm = require("vm");
const root = path.resolve(process.argv[2] || path.join(__dirname, "../.."));
const base = "project/src/main/resources/static/emc/module/mdm/equipt/equipts/";
const common = fs.readFileSync(path.join(root,
    "reconstituted/framework/owned-core-web/src/main/resources/static/gikam/main/js/chunk-common.js"), "utf8");
const oldBase = "/secure/emc/module/mdm/equipt/equipt/equipt-per-check";
const failures = [];
let passed = 0;

function check(name, run) {
    try { run(); passed++; console.log("PASS " + name); }
    catch (error) { failures.push(name); console.log("FAIL " + name + ": " + error.message); }
}

function sourceFunction(text, start) {
    const at = text.indexOf("function", start);
    const open = text.indexOf("{", at);
    let depth = 0, quote = null, escaped = false;
    for (let index = open; index < text.length; index++) {
        const current = text[index];
        if (quote) {
            if (escaped) escaped = false;
            else if (current === "\\") escaped = true;
            else if (current === quote) quote = null;
        } else if (current === '"' || current === "'" || current === "`") quote = current;
        else if (current === "{") depth++;
        else if (current === "}" && --depth === 0) return text.slice(at, index + 1);
    }
    throw new Error("Cannot extract actual bundle function");
}

function method(name, start, context) {
    const at = common.indexOf('key:"' + name + '",value:function', start);
    assert(at >= 0, "Actual bundle method missing: " + name);
    return vm.runInNewContext("(" + sourceFunction(common, at) + ")", context);
}

function extend() {
    const args = Array.from(arguments);
    let deep = false;
    if (typeof args[0] === "boolean") deep = args.shift();
    const result = args.shift() || {};
    args.forEach(source => Object.keys(source || {}).forEach(key => {
        const value = source[key];
        if (deep && value && typeof value === "object") {
            result[key] = extend(true, Array.isArray(value) ? [] : {}, value);
        } else result[key] = value;
    }));
    return result;
}

function deferred() {
    const success = [], failure = [], settled = [];
    let state = "pending", payload;
    return {
        done(callback) { if (state === "resolved") callback(payload); else if (state === "pending") success.push(callback); return this; },
        fail(callback) { if (state === "rejected") callback(payload); else if (state === "pending") failure.push(callback); return this; },
        always(callback) { if (state !== "pending") callback(payload); else settled.push(callback); return this; },
        resolve(value) { state = "resolved"; payload = value; success.splice(0).forEach(callback => callback(value)); settled.splice(0).forEach(callback => callback(value)); return this; },
        reject(error) { state = "rejected"; payload = error; failure.splice(0).forEach(callback => callback(error)); settled.splice(0).forEach(callback => callback(error)); return this; },
        // Deliberately allow a completion after abort to model callbacks already
        // queued by the transport: cancellation alone is not a generation guard.
        aborted: false, abort() { this.aborted = true; }
    };
}

function fixture() {
    const comps = {}, waiting = {}, ticks = [], writes = [], requests = [], details = [];
    const Gikam = {
        getPageObject: options => options,
        isEmpty: value => value == null || value === "" || (Array.isArray(value) && value.length === 0),
        isNotEmpty: value => !Gikam.isEmpty(value),
        extend,
        deepExtend: function() {
            const args = Array.from(arguments);
            return extend.apply(null, [true, args.length === 1 && Array.isArray(args[0]) ? [] : {}].concat(args));
        },
        getDeferred: deferred,
        isEmptyObject: x => !x || Object.keys(x).length === 0, isArray: Array.isArray,
        getJsonWrapper: p => ({p}),
        getJson(url) { const response = deferred(); details.push({url, response}); return response; },
        emptyFunction() {},
        getComp: id => comps[id],
        getAsyncComp: id => ({done(callback) {
            if (comps[id]) callback(comps[id]);
            else (waiting[id] || (waiting[id] = [])).push(callback);
        }}),
        getCurrentWindow: () => ({options: {alwaysButton: ["upload", "file-remove"]}}),
        cleanCompData: ids => ids.forEach(id => comps[id].cleanData()),
        printf: (value, data) => value.replace(/\{(\w+)\}/g, (_, key) => data[key]),
        preInsert() { throw new Error("Global Plan read page must not create CHECK records"); },
        create(type, options) {
            assert.equal(type, "grid");
            const grid = {options, refresh(params) {
                extend(this.options, params);
                if (this.options.url) {
                    requests.push({url: this.options.url, targetId: this.options.requestData.targetId});
                }
            }, cleanData() { this.options.data = []; },
            refreshToolbar(toolbar) { this.options.toolbar = toolbar; }};
            return grid;
        }
    };
    Gikam.jQuery = {extend};
    Gikam.status = {getQuickSearchItems: () => []};
    const extensions = fs.readFileSync(path.join(root, "reconstituted/framework/owned-core-common/src/main/resources/static/gikam/extends/gikam-extend.js"), "utf8");
    Gikam.status.getBpmnColumn = vm.runInNewContext("(" + sourceFunction(extensions, extensions.indexOf("getBpmnColumn: function")) + ")", {Gikam});
    const context = {Gikam, IFM_CONTEXT: "", workspace: {window: {$dom: {}}}};
    const load = name => vm.runInNewContext(fs.readFileSync(path.join(root, base + name), "utf8"), context);
    load("emc-equipt-per-check-plan.js");
    vm.runInNewContext(fs.readFileSync(path.join(root, "project/src/main/resources/static/emc/module/mdm/equipt/equipt-per-checks/emc-equipt-per-check.js"), "utf8"), context);
    load("emc-equipt-per-check-plan-audit-list.js");
    const page = context.emcEquiptPerCheck.planRecordPage || context.emcEquiptPerCheckPlan.auditPage;
    const formOptions = page.getBaseInfoTab().panels[0].items[0];
    const uploaderOptions = page.getBaseInfoTab().panels[1].items[0];
    const env = {o: {a: Gikam}};
    const form = {
        options: extend(true, {data: {ext$: {}}, panels: [{fields: formOptions.fields}]}, formOptions),
        model: {$nextTick: callback => ticks.push(callback), $refs: {vm: {getData: () => ({})}}},
        autoSave(value) { writes.push(value); },
        refresh() { throw new Error("Actual detail GET must be bound to selection generation"); },
        setReadonly(value) { this.options.readonly = value; }
    };
    for (const name of ["getData", "setData", "setOptions", "cleanData", "$cleanPanelsData", "disableAutoSave", "enableAutoSave"]) {
        form[name] = method(name, common.indexOf("var rt={renderTo:null,columns:2"), env);
    }
    const uploader = {
        options: extend(true, {searchUrl: "/core/module/item/files/queries", gridToolbar: [],
            gridOptions: {}, allowWidth: 400, gridSaveService: "coreFileServiceImpl",
            formatterGridColumns: columns => columns, formatterGridToolbar: toolbar => toolbar}, uploaderOptions),
        requestData: {},
        getBatchDownload: () => [], getBatchPreview: () => [], getOtherGridToolbar: () => []
    };
    const uploaderEnv = {Xs: Gikam, pe: {a: value => Array.from(value)},
        N: {a: {prop: value => value}}, window: {}};
    const columnsStart = common.indexOf("function Qs(");
    const columnsEnd = common.indexOf("var Zs,el=", columnsStart);
    assert(columnsEnd > columnsStart, "Actual Uploader default-column function missing");
    uploaderEnv.Qs = vm.runInNewContext("(" + common.slice(columnsStart, columnsEnd) + ")", uploaderEnv);
    const uploaderStart = common.indexOf('key:"createGrid",value:function', common.indexOf("function Qs("));
    for (const name of ["createGrid", "getGridColumns", "getDefaultGridToolbar", "getGridToolbar", "setRequestData", "setOptions"]) {
        uploader[name] = method(name, uploaderStart, uploaderEnv);
    }
    uploader.grid = uploader.createGrid();
    function mount(id, component) {
        comps[id] = component;
        (waiting[id] || []).splice(0).forEach(callback => callback(component));
    }
    return {page, formal: context.emcEquiptPerCheck, shared: context.emcEquiptPerCheckPlan, form, uploader, writes, requests, details, Gikam,
        formOptions, uploaderOptions, waiting,
        createLayout: () => {
            let layout;
            Gikam.create = (type, options) => { assert.equal(type, "layout"); layout = options; };
            page.create();
            return layout;
        },
        mountForm: () => mount(formOptions.id, form),
        mountUploader: () => mount(uploaderOptions.id, uploader),
        flush: () => { while (ticks.length) ticks.shift()(); }};
}

function loadGrid(c) {
    const grid = c.page.getGridParam();
    c.mountForm(); c.mountUploader();
    return grid;
}

function detail(c, id) {
    const result = c.details.filter(request => request.url === oldBase + "/" + id);
    assert(result.length > 0, "Selected CHECK must use the real detail GET: " + id);
    return result[result.length - 1];
}

check("407002 preserves CHECK raw scope while using formal CHECK presentation", () => {
    const c = fixture(), grid = c.page.getGridParam();
    assert.equal(grid.url, oldBase + "/queries/raw");
    assert.equal(grid.service, "emcEquiptPerCheckServiceImpl");
    assert.equal(grid.dbTable, "T_EMC_EQUIPT_PER_CHECK");
    assert.equal(grid.readonly, true);
    assert.equal(grid.autoSave, false);
    assert.equal(grid.toolbar.length, 0);
    assert.equal(grid.requestData, undefined, "Page must not replace raw DRAFT/org scope with a new status contract");
    const expected = c.formal.getSearchListColumns().filter(column => !column.checkbox).map(column => column.field);
    assert.deepEqual(Array.from(grid.columns, column => column.field), Array.from(expected));
    assert(!grid.columns.some(column => column.field === "ext$.codename"));
    assert(grid.columns.some(column => column.field === "equiptNo"));
    assert.equal(c.formal.baseUrl, "/secure/emc/module/mdm/equipt/equipt-per-checks");
    assert.equal(c.shared.baseUrl, oldBase);
});

check("actual Grid queryParams keeps nonempty CHECK filters, ordering and pagination", () => {
    const c = fixture(), pageGrid = c.page.getGridParam();
    assert(pageGrid.columns.some(column => column.field === "equiptNo"), "Formal CHECK filter column is missing");
    const grid = {
        options: extend(true, {requestData: {}, customSearchData: {}, page: true, compare: false,
            sum: {fields: [], nonNumberFields: []}, group: {fields: [], sortEnable: false},
            order: {equiptNo: "asc"}}, pageGrid),
        store: {state: {pageNum: 2, pageSize: 7, headRequestData: {checkPerson_SEQ: "实际核查人"},
            generalRequestData: {}, commonSearchData: {}}, getters: {serverSearch: true}}
    };
    const env = {o: {a: c.Gikam}, d: {a: Object.assign}};
    for (const name of ["queryParams", "getOrderArray", "getOrderMap", "removeEmptyRequestData"]) {
        grid[name] = method(name, 890000, env);
    }
    const body = grid.queryParams({equiptNo_CISC: "QA261108A-NOMATCH"}).p;
    assert.equal(body.f.equiptNo_CISC, "QA261108A-NOMATCH");
    assert.equal(body.qf.checkPerson_SEQ, "实际核查人");
    assert.equal(body.n, 2); assert.equal(body.s, 7);
    assert.deepEqual(Array.from(body.o, item => Object.assign({}, item)), [{equiptNo: "asc"}]);
    grid.options.order = {equiptNo: "desc"};
    assert.equal(grid.queryParams({equiptNo_CISC: "EQ-POSITIVE"}).p.f.equiptNo_CISC, "EQ-POSITIVE");
    assert.equal(grid.queryParams({}).p.o[0].equiptNo, "desc");
});

check("CHECK form clones readonly original fields without changing canonical editors", () => {
    const c = fixture();
    assert.equal(c.formOptions.service, "emcEquiptPerCheckServiceImpl");
    assert.equal(c.formOptions.dbTable, "T_EMC_EQUIPT_PER_CHECK");
    assert.equal(c.formOptions.readonly, true);
    assert.equal(c.formOptions.autoSave, false);
    assert.equal(c.formOptions.onUpdated, undefined);
    const expected = ["equiptNo", "equiptName", "specification", "checkPerson"].concat(
        Array.from(c.formal.getBaseInfoFormSupFields(), field => field.field));
    assert.deepEqual(Array.from(c.formOptions.fields, field => field.field), Array.from(expected));
    assert(c.formOptions.fields.length >= 16);
    for (const fields of [c.page.getGridParam().columns, c.formOptions.fields]) {
        assert(fields.every(field => !field.checkbox && field.readonly === true));
        assert(fields.every(field => !field.editor && !field.preInsert && !field.onChange && !field.onAfterChoose && !field.onClean));
    }
    assert.equal(typeof c.formal.getBaseInfoFormSupFields().find(field => field.field === "ext$.principalname").onAfterChoose, "function");
    assert(c.shared.getPerCheckListColumns().some(field => field.field === "ext$.codename"));
});

check("actual Form uses selected CHECK detail ID and Uploader refreshes once without writes", () => {
    const c = fixture(), grid = loadGrid(c), row = {id: "CHECK-91", checkResult: "stale row"};
    grid.onRowActive(0, row); c.flush();
    const entity = {id: row.id, checkResult: "actual detail", cycle: "3", ext$: {principalname: "核查负责人"}};
    detail(c, row.id).response.resolve(entity); c.flush();
    assert.equal(c.form.getData().id, row.id, "Actual Form.setData otherwise retains a previous id");
    assert.equal(c.form.getData().checkResult, "actual detail");
    assert.equal(c.form.getData().ext$.principalname, "核查负责人");
    assert.equal(c.uploader.options.bizId, row.id);
    assert.equal(c.requests.length, 1, "Uploader.setOptions already refreshes; duplicate grid.refresh is wrong");
    assert.equal(c.requests[0].targetId, "T_EMC_EQUIPT_PER_CHECK$CHECK-91");
    assert.equal(c.writes.length, 0);
    c.form.options.data.ext$.principalname = "local view change";
    assert.equal(entity.ext$.principalname, "核查负责人", "Form state must not modify the detail response");
});

check("inverse A-B-A detail completion cannot restore stale CHECK fields", () => {
    const c = fixture(), grid = loadGrid(c);
    grid.onRowActive(0, {id: "CHECK-A"}); c.flush();
    const first = detail(c, "CHECK-A");
    grid.onRowActive(1, {id: "CHECK-B"}); c.flush();
    const second = detail(c, "CHECK-B");
    grid.onRowActive(0, {id: "CHECK-A"}); c.flush();
    const last = detail(c, "CHECK-A");
    assert.notEqual(first, last);
    assert(first.response.aborted && second.response.aborted, "Obsolete detail HTTP requests must be aborted");
    last.response.resolve({id: "CHECK-A", checkResult: "latest A"}); c.flush();
    second.response.resolve({id: "CHECK-B", checkResult: "late B", opinion: "old"}); c.flush();
    first.response.resolve({id: "CHECK-A", checkResult: "old A", remark: "old"}); c.flush();
    assert.equal(c.form.getData().id, "CHECK-A");
    assert.equal(c.form.getData().checkResult, "latest A");
    assert.equal(c.form.getData().opinion, undefined);
    assert.equal(c.form.getData().remark, undefined);
    assert.equal(c.uploader.options.bizId, "CHECK-A");
    assert.equal(c.writes.length, 0);
});

check("late component mount and cleanData completion only request latest CHECK", () => {
    const c = fixture(), grid = c.page.getGridParam();
    grid.onRowActive(0, {id: "CHECK-A"});
    grid.onRowActive(1, {id: "CHECK-B"});
    c.mountForm(); c.mountUploader(); c.flush();
    assert.equal(c.details.length, 1, "Late mount started an obsolete detail request");
    detail(c, "CHECK-B").response.resolve({id: "CHECK-B", checkResult: "late mount B"}); c.flush();
    assert.equal(c.form.getData().id, "CHECK-B");
    assert.equal(c.uploader.options.bizId, "CHECK-B");
    assert.equal(c.requests.length, 1);
    grid.onRowActive(0, {id: "CHECK-A"});
    grid.onRowActive(1, {id: "CHECK-C"}); c.flush();
    assert(!c.details.some(request => request.url === oldBase + "/CHECK-A"), "Stale cleanData completion started obsolete GET");
    detail(c, "CHECK-C").response.resolve({id: "CHECK-C", cycle: "4"}); c.flush();
    assert.equal(c.form.getData().id, "CHECK-C");
    assert.equal(c.form.getData().checkResult, undefined, "Fields from previous CHECK survived cleanData");
    assert.equal(c.form.getData().cycle, "4");
    assert.equal(c.writes.length, 0);
});

check("nonempty onLoadSuccess keeps the selected CHECK attachment binding", () => {
    const c = fixture(), grid = loadGrid(c), row = {id: "CHECK-FIRST"};
    grid.onLoadSuccess.call({activeRowByIndex(index) { assert.equal(index, 0); grid.onRowActive(index, row); }}, [row]);
    c.flush(); detail(c, row.id).response.resolve({id: row.id, checkResult: "selected"}); c.flush();
    assert.equal(c.form.getData().id, row.id);
    assert.equal(c.uploader.options.bizId, row.id, "onLoadSuccess cleared the selected CHECK attachment");
    assert.equal(c.requests.length, 1);
});

check("actual Uploader exposes no write columns or toolbar even with alwaysButton", () => {
    const c = fixture();
    assert.equal(c.uploaderOptions.dbTable, "T_EMC_EQUIPT_PER_CHECK");
    assert.equal(c.uploaderOptions.readonly, true);
    assert.equal(c.uploaderOptions.onBeforeUpload, undefined);
    assert.equal(c.uploader.grid.options.readonly, true);
    assert.equal(c.uploader.grid.options.autoSave, false);
    assert.equal(c.uploader.grid.options.toolbar.length, 0, "Actual Uploader still offers upload/delete");
    assert(c.uploader.grid.options.columns.every(column => !column.editor));
    assert.notEqual(c.uploaderOptions.preview, false);
    assert.notEqual(c.uploaderOptions.download, false);
    c.uploader.options.columns = [{field: "name", editor: true}];
    assert(c.uploader.getGridColumns().every(column => !column.editor));
});

check("empty CHECK selection aborts detail and attachment requests before clearing", () => {
    const c = fixture(), grid = loadGrid(c);
    grid.onRowActive(0, {id: "CHECK-OLD"}); c.flush();
    const pending = detail(c, "CHECK-OLD");
    let aborted = false;
    c.uploader.grid.options.data = [{id: "OLD-FILE"}];
    c.uploader.grid.requestDeferred = {abort() { aborted = true; }};
    c.uploader.grid.cleanValidateCache = () => {};
    c.uploader.grid.initDataLock = () => {};
    c.uploader.grid.setOptions = options => extend(c.uploader.grid.options, options);
    c.uploader.grid.refreshByData = data => { c.uploader.grid.options.data = data.rows; return deferred().resolve(); };
    c.uploader.grid.oData = [];
    c.uploader.grid.selectIndexValue = {};
    c.uploader.grid.sortData = () => {};
    const gridEnv = {o: {a: Object.assign({}, c.Gikam, {when: () => deferred().resolve()})}, jt: {a: {beforeRefresh: () => true}}};
    c.uploader.grid.refresh = method("refresh", 890000, gridEnv);
    c.uploader.grid.refreshWithoutUrl = method("refreshWithoutUrl", 890000, gridEnv);
    c.uploader.grid.cleanData = method("cleanData", 890000, gridEnv);
    grid.onLoadSuccess([]); c.flush();
    pending.response.resolve({id: "CHECK-OLD", checkResult: "too late"}); c.flush();
    assert(pending.response.aborted, "Empty result must cancel pending detail GET");
    assert(aborted, "Actual Grid.refresh with URL null does not cancel an old attachment request");
    assert.equal(c.form.getData().id, undefined);
    assert.equal(c.form.getData().checkResult, undefined);
    assert.equal(c.uploader.options.bizId, "");
    assert.equal(c.uploader.grid.options.url, null);
    assert.equal(c.uploader.grid.options.data.length, 0);
    assert.equal(c.writes.length, 0);
});

check("empty result before components mount cannot resurrect queued CHECK selection", () => {
    const c = fixture(), grid = c.page.getGridParam();
    grid.onRowActive(0, {id: "CHECK-OLD"});
    grid.onLoadSuccess([]); c.mountForm(); c.mountUploader(); c.flush();
    assert.equal(c.details.length, 0);
    assert.equal(c.form.getData().id, undefined);
    assert.equal(c.uploader.options.bizId, "");
    assert.equal(c.uploader.grid.options.url, null);
    assert.equal(c.uploader.grid.options.data.length, 0);
    assert.equal(c.writes.length, 0);
});

check("detail authorization and server failures do not retain a previous entity or its files", () => {
    for (const status of [403, 500]) {
        const c = fixture(), grid = loadGrid(c);
        grid.onRowActive(0, {id: "CHECK-AUTHORIZED"}); c.flush();
        detail(c, "CHECK-AUTHORIZED").response.resolve({id: "CHECK-AUTHORIZED", checkResult: "old authorized entity"}); c.flush();
        c.uploader.grid.options.data = [{id: "OLD-FILE"}];
        grid.onRowActive(1, {id: "CHECK-DENIED"}); c.flush();
        detail(c, "CHECK-DENIED").response.reject({status, responseJSON: {message: "controlled upstream failure"}}); c.flush();
        assert.equal(c.form.getData().id, undefined, "Detail failure was treated as a successful entity");
        assert.equal(c.form.getData().checkResult, undefined, "Previous entity survived failed selection");
        assert.equal(c.uploader.options.bizId, "", "Failed detail left an attachment target active");
        assert.equal(c.uploader.grid.options.data.length, 0, "Previous attachment survived detail failure");
        assert.equal(c.writes.length, 0);
    }
});

check("an older rejected detail and its always callback cannot clear a newer CHECK", () => {
    const c = fixture(), grid = loadGrid(c);
    grid.onRowActive(0, {id: "CHECK-OLD"}); c.flush();
    const old = detail(c, "CHECK-OLD");
    grid.onRowActive(1, {id: "CHECK-CURRENT"}); c.flush();
    detail(c, "CHECK-CURRENT").response.resolve({id: "CHECK-CURRENT", checkResult: "current data"}); c.flush();
    old.response.reject({status: 500}); c.flush();
    assert.equal(c.form.getData().id, "CHECK-CURRENT");
    assert.equal(c.form.getData().checkResult, "current data");
    assert.equal(c.uploader.options.bizId, "CHECK-CURRENT");
    assert.equal(c.writes.length, 0);
});

check("a CHECK removed after list selection leaves empty detail and attachments", () => {
    for (const data of [null, {}]) {
        const c = fixture(), grid = loadGrid(c);
        grid.onRowActive(0, {id: "CHECK-REMOVED"}); c.flush();
        detail(c, "CHECK-REMOVED").response.resolve(data); c.flush();
        assert.equal(c.form.getData().id, undefined);
        assert.equal(c.uploader.options.bizId, "");
        assert.equal(c.uploader.grid.options.url, null);
        assert.equal(c.uploader.grid.options.data.length, 0);
        assert.equal(c.requests.length, 0, "Missing detail must not bind an attachment target");
        assert.equal(c.writes.length, 0);
    }
});

check("407002 template loads formal CHECK common and isolated read-page initialization", () => {
    const html = fs.readFileSync(path.join(root, "project/src/main/resources/templates/emc/module/mdm/equipt/equipts/emc-equipt-per-check-plan-audit-list.html"), "utf8");
    assert(html.includes("/static/emc/module/mdm/equipt/equipt-per-checks/emc-equipt-per-check.js"));
    assert(html.includes("emcEquiptPerCheck.planRecordPage.init()"));
    assert(!html.includes("/static/emc/module/mdm/equipt/equipts/emc-equipt-per-check-plan.js"));
});

console.log(JSON.stringify({passed, failed: failures}));
if (failures.length) process.exitCode = 1;

