var corePrinterPrintLog = Gikam.getPageObject({

    getSearchListColumns : function() {
        return [ {
            index : true
        }, {
            field : 'fileName',
            title : 'T_CORE_PRINT_LOG.FILENAME',
            width : 350
        }, {
            field : 'copies',
            title : 'T_CORE_PRINT_LOG.COPIES',
            width : 120
        }, {
            field : 'printerName',
            title : 'T_CORE_PRINT_LOG.PRINTERNAME',
            width : 325
        }, {
            field : 'createdByName',
            title : 'T_CORE_PRINT_LOG.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_CORE_PRINT_LOG.CREATEDTIME'
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/printer/print-logs'
});
