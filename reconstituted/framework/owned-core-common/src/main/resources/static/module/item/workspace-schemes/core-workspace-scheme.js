var coreWorkspaceScheme = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'schemeName',
            title : 'T_CORE_WK_SCHEME.SCHEMENAME',
            validators : [ 'strLength [0,40]', 'remote[/secure/core/module/item/workspace-schemes/action/validate-unique]' ],
            width : 250,
            editor : true
        } ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'schemeName',
            title : 'T_CORE_WK_SCHEME.SCHEMENAME'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'schemeName',
            title : 'T_CORE_WK_SCHEME.SCHEMENAME',
            validators : [ 'strLength [0,40]', 'remote[/secure/core/module/item/workspace-schemes/action/validate-unique]' ],
            preInsert : true
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/item/workspace-schemes'
});
