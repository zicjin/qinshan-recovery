var emcTestmethodBeforehandle = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'beforeHandleName',
            title : 'T_EMC_TESTMETHOD_BEFOREHANDLE.BEFOREHANDLENAME'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'beforeHandleName',
            title : 'T_EMC_TESTMETHOD_BEFOREHANDLE.BEFOREHANDLENAME',
            validators : [ 'strLength[0,40]' ]
        }, ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'beforeHandleName',
            title : 'T_EMC_TESTMETHOD_BEFOREHANDLE.BEFOREHANDLENAME'
        }, ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/testmethod-beforehandles'
});
