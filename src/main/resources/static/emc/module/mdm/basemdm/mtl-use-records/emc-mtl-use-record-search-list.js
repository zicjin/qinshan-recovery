emcMtlUseRecord.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-mtl-use-record-search-list-grid',
            url : emcMtlUseRecord.baseUrl + '/queries/searchable',
            columns : emcMtlUseRecord.getSearchListColumns(),
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
            }
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-mtl-use-record-search-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(emcMtlUseRecord.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function() {
        this.create();
    }
};
