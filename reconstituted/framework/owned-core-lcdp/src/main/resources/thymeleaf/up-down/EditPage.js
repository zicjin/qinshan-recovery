
var pageParam = CURRENT_WINDOW.getPageParam();
var baseUrl = "[(${moduleTmpl.masterScriptPath})]";
[# th:each="vo:${moduleTmpl.childTableList}"]
var base[(${vo.childUrlName})]Url = "[(${vo.scriptPath})]";
[/]

function insert() {
    Gikam.getComp('[(${moduleTmpl.moduleName})]-edit-grid').insert(baseUrl + '.insertData',[{}]);
}

function deleteRows() {
    Gikam.getComp('[(${moduleTmpl.moduleName})]-edit-grid').deleteRows(baseUrl + '.deleteData');
}

function getGridRequestData() {
    return pageParam;
}


[# th:if="${moduleTmpl.bpFlag}"]
function submit() {
    var grid = Gikam.getComp('[(${moduleTmpl.moduleName})]-edit-grid');

    Gikam.create('workflow').submit({
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

[# th:each="vo:${moduleTmpl.childTableList}"]
function get[(${vo.functionName})]RequestData() {
    var grid =  Gikam.getComp('[(${moduleTmpl.moduleName})]-edit-grid');
    return {
        [(${vo.associatedField})]_SEQ: grid.getActivedRow().id
    }
}
[/]
[# th:each="vo:${moduleTmpl.childTableList}"]
function insert[(${vo.functionName})]() {

    Gikam.getComp('[(${moduleTmpl.moduleName})]-edit-[(${vo.compName})]-grid').insert(base[(${vo.childUrlName})]Url+".insertData", [{
        [(${vo.associatedField})]: Gikam.getComp('[(${moduleTmpl.moduleName})]-edit-grid').getActivedRow().id
    }]);
}

function delete[(${vo.functionName})]() {

    Gikam.getComp('[(${moduleTmpl.moduleName})]-edit-[(${vo.compName})]-grid').deleteRows(base[(${vo.childUrlName})]Url+".deleteData");
}
[/]