"use strict";

// Execute the real page callbacks at their Gikam component boundary. An optional
// resource root permits the same assertions against captured production files.
const assert = require("assert");
const fs = require("fs");
const path = require("path");
const vm = require("vm");
const resources = path.resolve(process.argv[2] || path.join(__dirname, "../../project/src/main/resources"));
const samplePath = "static/emc/module/bp/sample/samples/";
const baseUrl = "/secure/emc/module/bp/sample/samples";
const failures = [];
let passed = 0;

function check(name, run) {
    try { run(); passed++; console.log("PASS " + name); }
    catch (error) { failures.push(name); console.log("FAIL " + name + ": " + error.message); }
}

function plain(value) { return JSON.parse(JSON.stringify(value)); }

function fixture() {
    const components = {}, creations = [], alerts = [], puts = [];
    const context = {
        console,
        IFM_CONTEXT: "",
        workspace: {user: {id: "TEST-PRINCIPAL"}, window: {$dom: {}}},
        emcSample: {baseUrl, getSampleQuerySearchColumns: () => []},
    };
    context.Gikam = {
        isTrue: value => value === true || value === "true" || value === 1 || value === "1",
        isEmpty: value => value == null || value === "" || (Array.isArray(value) && value.length === 0),
        getComp: id => components[id],
        getLastModal: () => ({window: {$dom: {}}}),
        create(type, options) { creations.push({type, options}); },
        alert(message) { alerts.push(message); },
        getJsonWrapper(params, data) { return {params, data}; },
        put(url, body) { puts.push({url, body}); },
    };
    vm.createContext(context);
    for (const filename of ["emc-sample-query-search-list.js", "emc-sample-annex.js"]) {
        vm.runInContext(fs.readFileSync(path.join(resources, samplePath, filename), "utf8"), context, {filename});
    }
    return {context, components, creations, alerts, puts};
}

function mountAnnex(f, parameters) {
    f.context.emcSample.annexPage.init(parameters);
    const layouts = f.creations.filter(item => item.type === "layout");
    assert.strictEqual(layouts.length, 1, "attachment page must create its declared layout");
    const uploader = layouts[0].options.center.items[0];
    assert.strictEqual(uploader.type, "uploader");
    assert.strictEqual(uploader.id, "emc-sample-file-uploader");
    assert.strictEqual(uploader.dbTable, "T_EMC_SAMPLE");
    assert.strictEqual(uploader.bizId, parameters.id, "attachment target must remain the selected sample");
    assert.strictEqual(uploader.batchDownload, true, "read-only attachment view must retain batch download");
    return uploader;
}

function categoryColumn(uploader) {
    const columns = [{field: "fileName"}, {field: "size"}, {field: "createdTime"}, {field: "operations"}];
    const formatted = uploader.formatterGridColumns(columns);
    assert.deepStrictEqual(Array.from(formatted, column => column.field),
        ["fileName", "size", "createdTime", "bizCategory", "remark", "operations"]);
    const category = formatted.find(column => column.field === "bizCategory");
    assert.strictEqual(category.type, "select");
    assert.strictEqual(category.category, "sys");
    assert.strictEqual(category.param.codeCategoryId, "fileBizCategory");
    return category;
}

function uploaderToolbar() {
    return [
        {id: "upload-icon", icon: "UPLOAD", text: "GIKAM.BUTTON.ADD"},
        {id: "remove-icon", icon: "remove", text: "GIKAM.BUTTON.REMOVE"},
        {id: "delete-icon", icon: "Delete", text: "GIKAM.BUTTON.DELETE"},
        {id: "upload-text", icon: "add", text: "Upload attachment"},
        {id: "delete-text", icon: "minus", text: "Delete attachment"},
        {id: "upload-localized", text: "上传"},
        {id: "delete-localized", text: "删除"},
        {id: "download", icon: "download", text: "GIKAM.BUTTON.DOWNLOAD"},
        {id: "batch-download", icon: "download", text: "批量下载"},
        {id: "view", icon: "view", text: "查看"},
        {id: "custom", icon: "information", text: "附件信息"},
        {id: "empty-descriptor"},
    ];
}

check("every sample query tab opens the selected sample annex with the production read-only action flag", () => {
    const f = fixture();
    const panels = f.context.emcSample.sampleQuerySearchPage.getSampleQueryTab().panels;
    assert.strictEqual(panels.length, 4);
    for (const [index, panel] of Array.from(panels).entries()) {
        const grid = panel.items[0].center.items[0];
        f.components[grid.id] = {getSelections: () => [{id: "SAMPLE-" + index}]};
        const action = grid.toolbar.find(button => button.text === "EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.ANNEX");
        assert(action, "query tab must expose the original annex action");
        action.onClick();
        const modal = f.creations[f.creations.length - 1];
        assert.strictEqual(modal.type, "modal");
        assert.strictEqual(modal.options.width, 800);
        assert.strictEqual(modal.options.height, 500);
        assert.strictEqual(modal.options.title, "EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ANNEX");
        assert.strictEqual(modal.options.url,
            baseUrl + "/page/sample-annex-list?id=SAMPLE-" + index + "&hideUploadAndDelete=true");
    }
});

check("query annex action refuses absent or multiple selections without opening a modal", () => {
    for (const rows of [undefined, [], [{id: "SAMPLE-A"}, {id: "SAMPLE-B"}]]) {
        const f = fixture();
        f.components["query-grid"] = {getSelections: () => rows};
        f.context.emcSample.sampleQuerySearchPage.getSampleButton("query-grid")[0].onClick();
        assert.strictEqual(f.creations.length, 0);
        assert.deepStrictEqual(f.alerts, ["GIKAM.TIP.CHOOSE_ONE_ITEM"]);
        assert.strictEqual(f.puts.length, 0);
    }
});

check("query modal parameters reach the real annex initializer and disable category edits", () => {
    const f = fixture();
    f.components["query-grid"] = {getSelections: () => [{id: "SAMPLE-READ"}]};
    f.context.emcSample.sampleQuerySearchPage.getSampleButton("query-grid")[0].onClick();
    const modal = f.creations[0];
    const url = new URL(modal.options.url, "http://127.0.0.1");
    const parameters = Object.fromEntries(url.searchParams.entries());
    assert.strictEqual(parameters.hideUploadAndDelete, "true", "query action did not pass the production flag");
    const uploader = mountAnnex(f, parameters);
    assert.strictEqual(uploader.readonly, false, "hide flag must not impersonate uploader readonly");
    assert.strictEqual(categoryColumn(uploader).editor, false);
    assert.strictEqual(f.puts.length, 0, "opening a query attachment must not write metadata");
});

check("hidden attachment actions remove upload and delete while preserving download, view and unrelated actions", () => {
    for (const flag of [true, "true", "1"]) {
        const f = fixture();
        const uploader = mountAnnex(f, {id: "SAMPLE-READ", hideUploadAndDelete: flag});
        assert.strictEqual(typeof uploader.formatterGridToolbar, "function",
            "production uploader action formatter is missing");
        const toolbar = uploaderToolbar(), before = plain(toolbar);
        const formatted = uploader.formatterGridToolbar(toolbar);
        assert.deepStrictEqual(Array.from(formatted, button => button.id),
            ["download", "batch-download", "view", "custom", "empty-descriptor"]);
        assert.deepStrictEqual(toolbar, before, "formatting must not mutate shared toolbar descriptors");
        assert.strictEqual(categoryColumn(uploader).editor, false);
        assert.strictEqual(f.puts.length, 0);
    }
});

check("ordinary attachment pages retain editable category and the complete default toolbar", () => {
    for (const flag of [undefined, false, "false", "0"]) {
        const f = fixture();
        const uploader = mountAnnex(f, {id: "SAMPLE-EDIT", hideUploadAndDelete: flag});
        const toolbar = uploaderToolbar();
        const formatted = typeof uploader.formatterGridToolbar === "function" ? uploader.formatterGridToolbar(toolbar) : toolbar;
        assert.strictEqual(formatted, toolbar, "ordinary attachment actions must remain untouched");
        assert.strictEqual(categoryColumn(uploader).editor, true);
        assert.strictEqual(uploader.readonly, false);
    }
});

check("uploader readonly remains independent of the production action-visibility flag", () => {
    for (const readonly of [true, "true", false, "false"]) {
        for (const hide of [true, false]) {
            const f = fixture();
            const uploader = mountAnnex(f, {id: "SAMPLE-FLAGS", readonly, hideUploadAndDelete: hide});
            assert.strictEqual(uploader.readonly, readonly === true || readonly === "true");
            assert.strictEqual(categoryColumn(uploader).editor, !hide);
            const toolbar = uploaderToolbar();
            const formatted = typeof uploader.formatterGridToolbar === "function" ? uploader.formatterGridToolbar(toolbar) : toolbar;
            assert.deepStrictEqual(Array.from(formatted, button => button.id), hide ?
                ["download", "batch-download", "view", "custom", "empty-descriptor"] : toolbar.map(button => button.id));
        }
    }
});

check("ordinary upload success still assigns the declared business category through the existing file API", () => {
    const f = fixture();
    const uploader = mountAnnex(f, {id: "SAMPLE-EDIT", bizCategory: "SAMPLING-ANNEX"});
    uploader.onUploadSuccess([]);
    assert.strictEqual(f.puts.length, 0);
    uploader.onUploadSuccess([{id: "FILE-A", name: "a.txt"}, {id: "FILE-B", name: "b.txt"}]);
    assert.deepStrictEqual(plain(f.puts), [{url: "/core/module/item/files/instant", body: {
        params: {}, data: ["coreFileServiceImpl", [
            {id: "FILE-A", bizCategory: "SAMPLING-ANNEX"},
            {id: "FILE-B", bizCategory: "SAMPLING-ANNEX"},
        ]],
    }}]);
    const noCategory = fixture();
    mountAnnex(noCategory, {id: "SAMPLE-EDIT"}).onUploadSuccess([{id: "FILE-C"}]);
    assert.strictEqual(noCategory.puts.length, 0);
});

console.log(JSON.stringify({passed, failed: failures}));
if (failures.length) process.exitCode = 1;
