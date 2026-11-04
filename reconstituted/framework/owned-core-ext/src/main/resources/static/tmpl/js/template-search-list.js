${data.jsObjectName}.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : '${data.jsFileName}-search-list-grid',
            url : ${data.jsObjectName}.baseUrl + '/queries/searchable',
            columns : ${data.jsObjectName}.getSearchListColumns(),
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
            }
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : '${data.jsFileName}-search-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(${data.jsObjectName}.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function() {
        this.create();
    }
};
