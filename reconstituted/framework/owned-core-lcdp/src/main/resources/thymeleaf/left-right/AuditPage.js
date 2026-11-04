
var baseUrl = "[(${moduleTmpl.masterScriptPath})]";
var pageParam = CURRENT_WINDOW.getPageParam();

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
function getGridRequestData() {
    return pageParam;
}
[# th:each="vo:${moduleTmpl.childTableList}"]
function get[(${vo.functionName})]RequestData() {
    var grid =  Gikam.getComp('[(${moduleTmpl.moduleName})]-audit-grid');
    return {
        [(${vo.associatedField})]_SEQ: grid.getActivedRow().id
    }
}
[/]

