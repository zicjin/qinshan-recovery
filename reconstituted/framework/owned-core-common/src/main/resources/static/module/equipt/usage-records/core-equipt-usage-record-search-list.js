coreEquiptUsageRecord.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-equipt-usage-record-search-list-grid',
            url : coreEquiptUsageRecord.baseUrl + '/queries/searchable',
            requestData : _this.param,
            columns : coreEquiptUsageRecord.getSearchListColumns(),
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-equipt-usage-record-search-list-layout',
            renderTo : Gikam.isNotEmpty(_this.param.modal) ? Gikam.getLastModal().window.$dom : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }

};