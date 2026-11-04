
var baseUrl = "[(${moduleTmpl.masterScriptPath})]";
var pageParam = CURRENT_WINDOW.getPageParam();

function loadDetailPage(field, row) {
    if ( field === '[(${link.fieldName})]'){
        workspace.window.loadPage('[(${moduleTmpl.detailPagePath})]', {id : row.id,sourcePage: 'audit'});
    }
}
function getGridRequestData() {
    return pageParam;
}
[# th:if="${moduleTmpl.bpFlag}"]
function approve() {
    var grid = Gikam.getComp('[(${moduleTmpl.moduleName})]-audit-grid');

    Gikam.create('workflow').pass({
        data: grid.getSelections(),
        pageObject: pageObject
    }).done(function () {
        grid.refresh();
    });
}

function reject() {
    var grid = Gikam.getComp('[(${moduleTmpl.moduleName})]-audit-grid');

    Gikam.create('workflow').reject({
        data: grid.getSelections(),
        pageObject: pageObject
    }).done(function () {
        grid.refresh();
    });
}

var pageObject = {
    baseUrl: baseUrl,
    workflow: {
        dbTable: '[(${masterTableName})]'
    }
}

[/]