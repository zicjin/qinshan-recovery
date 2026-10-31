var emcDataReporting = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/sa/data-reportings',

    getEditListColumns : function() {
        return [ {
            index : true
        }, {
            field : 'ext$.name',
            title : 'T_CORE_REPORT_CONFIG.NAME',
        }, {
            field : 'reportName',
            title : 'T_CORE_REPORT_CONFIG_LINE.REPORTNAME'
        } ];
    },

});
