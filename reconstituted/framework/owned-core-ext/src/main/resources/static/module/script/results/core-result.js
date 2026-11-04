var coreResult = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'testId',
            title : 'T_CORE_RESULT.TESTID'
        }, {
            field : 'testName',
            title : 'T_CORE_RESULT.TESTNAME'
        }, {
            field : 'itemName',
            title : 'T_CORE_RESULT.ITEMNAME'
        }, {
            field : 'repeatNo',
            title : 'T_CORE_RESULT.REPEATNO'
        }, {
            field : 'originalResult',
            title : 'T_CORE_RESULT.ORIGINALRESULT',
            editor : true,
            type : 'number',
            onBeforeEditorRender : function(row) {
                return (Gikam.isEmpty(row.ext$.script)) ? true : false;
            }
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/script/results'
});
