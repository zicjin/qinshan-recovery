"use strict";

const assert = require("assert");
const fs = require("fs");
const path = require("path");
const vm = require("vm");
const root = path.resolve(process.argv[2] || path.join(__dirname, "../.."));
const base = "project/src/main/resources/static/emc/module/mdm/equipt/equipts/";
const common = fs.readFileSync(path.join(root,
    "reconstituted/framework/owned-core-web/src/main/resources/static/gikam/main/js/chunk-common.js"), "utf8");
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
    const pending = [];
    let resolved = false;
    return {
        done(callback) { if (resolved) callback(); else pending.push(callback); return this; },
        resolve() { resolved = true; pending.splice(0).forEach(callback => callback()); return this; }
    };
}

function fixture() {
    const comps = {}, waiting = {}, ticks = [], writes = [], requests = [];
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
                requests.push({url: this.options.url, targetId: this.options.requestData.targetId});
            }, cleanData() { this.options.data = []; },
            refreshToolbar(toolbar) { this.options.toolbar = toolbar; }};
            return grid;
        }
    };
    Gikam.jQuery = {extend};
    const context = {Gikam, IFM_CONTEXT: "", workspace: {window: {$dom: {}}}};
    const load = name => vm.runInNewContext(fs.readFileSync(path.join(root, base + name), "utf8"), context);
    load("emc-equipt-per-check-plan.js");
    load("emc-equipt-per-check-plan-edit-list.js");
    const page = context.emcEquiptPerCheckPlan.editPage;
    const formOptions = page.getBaseInfoTab().panels[0].items[0];
    const uploaderOptions = page.getBaseInfoTab().panels[1].items[0];
    const env = {o: {a: Gikam}};
    const form = {
        options: extend(true, {data: {ext$: {}}, panels: [{fields: formOptions.fields}]}, formOptions),
        model: {$nextTick: callback => ticks.push(callback), $refs: {vm: {getData: () => ({})}}},
        autoSave(value) { writes.push(value); },
        refresh() { throw new Error("Selected Plan must not GET a CHECK detail"); }
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
    return {page, shared: context.emcEquiptPerCheckPlan, form, uploader, writes, requests,
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

check("407001 uses the dedicated global Plan API without mutating shared CHECK route", () => {
    const c = fixture(), grid = c.page.getGridParam();
    assert.equal(grid.url, "/secure/emc/module/mdm/equipt/equipts/per-check-plans/queries");
    assert.equal(c.shared.baseUrl, "/secure/emc/module/mdm/equipt/equipt/equipt-per-check");
    assert.equal(grid.service, "emcEquiptPerCheckPlanServiceImpl");
    assert.equal(grid.dbTable, "T_EMC_EQUIPT_PER_CHECK_PLAN");
    assert.equal(grid.readonly, true);
    assert.equal(grid.autoSave, false);
    assert.equal(grid.toolbar.length, 0, "Wrong CHECK create/delete handlers remain active");
});

check("407001 clones read fields and leaves original device Plan editors intact", () => {
    const c = fixture(), grid = c.page.getGridParam();
    for (const fields of [grid.columns, c.formOptions.fields]) {
        assert(fields.length >= 10, "Original Plan business fields lost");
        assert(fields.every(field => !field.checkbox && field.readonly === true && !field.editor && !field.preInsert));
        assert(fields.every(field => !field.onChange && !field.onAfterChoose && !field.onClean));
    }
    assert.equal(c.formOptions.readonly, true);
    assert.equal(c.formOptions.autoSave, false);
    assert.equal(c.formOptions.onUpdated, undefined);
    assert(c.shared.getPerCheckListColumns().find(field => field.field === "cycle").editor);
    assert(c.shared.getBaseInfoFormFields().find(field => field.field === "cycle").onChange);
    assert.equal(c.shared.getPerCheckPlanTab().items[0].toolbar.length, 2,
        "Original device Plan maintenance toolbar was changed");
    for (const field of ["ext$.equiptno", "ext$.equiptname", "ext$.specification"]) {
        assert(grid.columns.some(column => column.field === field));
        assert(c.formOptions.fields.some(input => input.field === field));
    }
});

check("407001 keeps its original layout and basic-info/attachment tabs", () => {
    const c = fixture(), layout = c.createLayout();
    assert.equal(layout.id, "emc-equipt-per-check-plan-edit-list-layout");
    assert.equal(layout.west.width, 300);
    const panels = layout.center.items[0].panels;
    assert.equal(panels[0].title, "基本信息");
    assert.equal(panels[1].title, "GIKAM.FILE.ATTACHMENT");
});

check("actual Form and Uploader show selected Plan without HTTP detail or autosave", () => {
    const c = fixture(); c.mountForm(); c.mountUploader();
    const row = {id: "PLAN-91", equiptId: "EQ-7", maintId: "MAINT-8", cycle: "3",
        maintDate: "2026-11-01", remark: "Actual Plan row", ext$: {codename: "期间核查"}};
    c.page.getGridParam().onRowActive(0, row); c.flush();
    assert.equal(c.form.getData().id, row.id, "Actual Form.setData retains the previous id unless explicitly reset");
    assert.equal(c.form.getData().remark, row.remark);
    assert.equal(c.form.getData().ext$.codename, row.ext$.codename);
    assert.equal(c.uploader.options.bizId, row.id);
    assert.equal(c.requests.length, 1, "Uploader.setOptions already refreshes; duplicate grid.refresh is wrong");
    assert.equal(c.requests[0].targetId, "T_EMC_EQUIPT_PER_CHECK_PLAN$PLAN-91");
    assert.equal(c.writes.length, 0, "Actual Form.setData must pass false for autosave");
    c.form.options.data.ext$.codename = "view-local-value";
    assert.equal(row.ext$.codename, "期间核查", "UI state must not mutate the grid row");
});

check("nonempty onLoadSuccess keeps selected Plan attachment binding", () => {
    const c = fixture(); c.mountForm(); c.mountUploader();
    const row = {id: "PLAN-1", remark: "selected"}, grid = c.page.getGridParam();
    grid.onLoadSuccess.call({activeRowByIndex(index) { assert.equal(index, 0); grid.onRowActive(index, row); }}, [row]);
    c.flush();
    assert.equal(c.form.getData().id, row.id);
    assert.equal(c.uploader.options.bizId, row.id, "onLoadSuccess cleared the selected Plan attachment");
    assert.equal(c.requests.length, 1);
});

check("late component callbacks cannot resurrect an older Plan selection", () => {
    const c = fixture(), grid = c.page.getGridParam();
    grid.onRowActive(0, {id: "PLAN-A", remark: "old"});
    grid.onRowActive(1, {id: "PLAN-B", cycle: "4"});
    c.mountForm(); c.mountUploader(); c.flush();
    assert.equal(c.form.getData().id, "PLAN-B");
    assert.equal(c.form.getData().remark, undefined);
    assert.equal(c.uploader.options.bizId, "PLAN-B");
    assert.equal(c.requests.length, 1, "Stale mount callback queried the old Plan's attachments");
    assert.equal(c.writes.length, 0);
});

check("late Form.cleanData completion cannot replace a newer selected Plan", () => {
    const c = fixture(), grid = c.page.getGridParam(); c.mountForm(); c.mountUploader();
    grid.onRowActive(0, {id: "PLAN-A", remark: "old"});
    grid.onRowActive(1, {id: "PLAN-B", cycle: "4"}); c.flush();
    assert.equal(c.form.getData().id, "PLAN-B");
    assert.equal(c.form.getData().remark, undefined);
    assert.equal(c.form.getData().cycle, "4");
    assert.equal(c.writes.length, 0);
});

check("fully rendered Plan switches actual entity ID and removes absent old fields", () => {
    const c = fixture(), grid = c.page.getGridParam(); c.mountForm(); c.mountUploader();
    grid.onRowActive(0, {id: "PLAN-A", remark: "old", ext$: {equiptname: "old equipment"}}); c.flush();
    assert.equal(c.form.getData().id, "PLAN-A");
    grid.onRowActive(1, {id: "PLAN-B", cycle: "4", ext$: {equiptno: "EQ-B"}}); c.flush();
    assert.equal(c.form.getData().id, "PLAN-B");
    assert.equal(c.form.getData().ext$.equiptno, "EQ-B");
    assert.equal(c.form.getData().ext$.equiptname, undefined);
    assert.equal(c.form.getData().remark, undefined);
    assert.equal(c.requests.length, 2);
    assert.equal(c.writes.length, 0);
});

check("empty result clears actual Form and attachments including queued stale callbacks", () => {
    const c = fixture(), grid = c.page.getGridParam();
    grid.onRowActive(0, {id: "PLAN-A", remark: "old"});
    grid.onLoadSuccess([]); c.mountForm(); c.mountUploader(); c.flush();
    assert.equal(c.form.getData().id, undefined);
    assert.equal(c.form.getData().remark, undefined);
    assert.equal(c.uploader.options.bizId, "");
    assert.equal(c.uploader.grid.options.url, null);
    assert.equal(c.uploader.grid.options.data.length, 0);
    assert.equal(c.writes.length, 0);
    assert(!c.requests.some(request => request.targetId.endsWith("$PLAN-A")));
});

check("actual Uploader hides write toolbar even if window alwaysButton requests it", () => {
    const c = fixture();
    assert.equal(c.uploaderOptions.readonly, true);
    assert.equal(c.uploader.grid.options.readonly, true);
    assert.equal(c.uploader.grid.options.autoSave, false);
    assert.equal(c.uploader.grid.options.toolbar.length, 0, "Actual Uploader still offers upload/delete");
    assert(c.uploader.grid.options.columns.every(column => !column.editor));
    assert.notEqual(c.uploaderOptions.preview, false, "Existing read preview must remain available");
    assert.notEqual(c.uploaderOptions.download, false, "Existing read download must remain available");
    const malicious = [{field: "name", editor: true}];
    c.uploader.options.columns = malicious;
    assert(c.uploader.getGridColumns().every(column => !column.editor));
});

check("empty Plan selection aborts pending attachment response before clearing", () => {
    const c = fixture(), grid = c.page.getGridParam(); c.mountForm(); c.mountUploader();
    grid.onRowActive(0, {id: "PLAN-A"}); c.flush();
    let aborted = false;
    c.uploader.grid.options.data = [];
    c.uploader.grid.requestDeferred = {abort() { aborted = true; }};
    c.uploader.grid.cleanValidateCache = () => {};
    c.uploader.grid.initDataLock = () => {};
    c.uploader.grid.setOptions = options => extend(c.uploader.grid.options, options);
    c.uploader.grid.refreshByData = data => { c.uploader.grid.options.data = data.rows; };
    const gridEnv = {o: {a: {getDeferred: deferred}}, jt: {a: {beforeRefresh: () => true}}};
    c.uploader.grid.refresh = method("refresh", 890000, gridEnv);
    c.uploader.grid.cleanData = method("cleanData", 890000, gridEnv);
    grid.onLoadSuccess([]); c.flush();
    if (!aborted) c.uploader.grid.options.data = [{id: "OLD-FILE", targetId: "T_EMC_EQUIPT_PER_CHECK_PLAN$PLAN-A"}];
    assert(aborted, "Grid.refresh with no URL does not abort an old request itself");
    assert.equal(c.uploader.grid.options.data.length, 0, "Stale attachment response survived empty selection");
    assert.equal(c.uploader.options.bizId, "");
});

console.log(JSON.stringify({passed, failed: failures}));
if (failures.length) process.exitCode = 1;
