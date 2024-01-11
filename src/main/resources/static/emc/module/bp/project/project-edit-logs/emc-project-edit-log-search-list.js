emcProjectEditLog.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-project-edit-log-search-list-grid',
            url : emcProjectEditLog.baseUrl + '/queries/searchable',
            columns : emcProjectEditLog.getSearchListColumns(),
            page : true,
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
            }
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-project-edit-log-search-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(emcProjectEditLog.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function() {
        this.create();
    }
};
