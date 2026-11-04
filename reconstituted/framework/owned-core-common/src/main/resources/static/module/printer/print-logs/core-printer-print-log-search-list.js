corePrinterPrintLog.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-printer-print-log-search-list-grid',
            url : corePrinterPrintLog.baseUrl + '/queries/searchable',
            columns : corePrinterPrintLog.getSearchListColumns()
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-printer-print-log-search-list-layout',
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
