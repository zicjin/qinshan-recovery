coreLoginLog.searchPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-login-search-list-grid',
            url : coreLoginLog.baseUrl + '/queries/searchable',
            columns : coreLoginLog.getSearchListColumns()
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};