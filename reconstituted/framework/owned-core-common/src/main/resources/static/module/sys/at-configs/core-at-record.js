var coreAtRecord = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/at-records',

    getLineListColumns : function() {
        return [ {
            title : 'T_CORE_AT_RECORD_LINE.COLUMNNAME',
            field : 'columnNew'
        }, {
            title : 'T_CORE_AT_RECORD_LINE.OLDVALUE',
            field : 'oldValue'
        }, {
            title : 'T_CORE_AT_RECORD_LINE.NEWVALUE',
            field : 'newValue'
        }, {
            title : 'T_CORE_AT_RECORD_LINE.AUDITTRAILNAME',
            field : 'auditTrailName',
        }, {
            title : 'T_CORE_AT_RECORD_LINE.CREATEDBYNAME',
            field : 'createdByName'
        }, {
            title : 'T_CORE_AT_RECORD_LINE.CREATEDTIME',
            width : 175,
            field : 'createdTime'
        },{
            title : 'T_CORE_AT_RECORD_LINE.COMMENT',
            width : 200,
            field : 'ext$.comment'
        } ];
    }

});