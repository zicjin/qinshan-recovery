
var baseUrl = "[(${moduleTmpl.masterScriptPath})]";
var pageParam = CURRENT_WINDOW.getPageParam();

function getGridRequestData() {
    return pageParam;
}
function loadDetailPage(field, row) {
    if (field === '[(${link.fieldName})]') {
        workspace.window.loadPage('[(${moduleTmpl.detailPagePath})]', { id: row.id, sourcePage: 'search'});
    }
}