var coreWorkspacePanel = Gikam.getPageObject({

    getEditListColumns : function () {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'code',
            title : 'T_CORE_WK_PANEL.CODE'
        }, Gikam.status.getSuspendColumn(), {
            field : 'title',
            title : 'T_CORE_WK_PANEL.TITLE',
            validators : [ 'strLength[0,64]' ],
            width : 240,
            editor : true
        }, {
            field : 'refreshFlag',
            title : 'T_CORE_WK_PANEL.REFRESHFLAG',
            type : 'select',
            category : 'yesorno',
            firstBlank : false,
            editor : true
        }, {
            field : 'titleDisplay',
            title : 'T_CORE_WK_PANEL.TITLEDISPLAY',
            type : 'select',
            category : 'yesorno',
            firstBlank : false,
            editor : true
        }, {
            field : 'url',
            title : 'T_CORE_WK_PANEL.URL',
            validators : [ 'strLength[0,256]' ],
            width : 350,
            editor : true
        }, {
            field : 'i18nCode',
            title : 'T_CORE_WK_PANEL.I18NCODE',
            validators : [ 'strLength[0,42]' ],
            editor : true
        } ];
    },

    getBaseInfoFormFields : function () {
        return [ {
            field : 'code',
            title : 'T_CORE_WK_PANEL.CODE',
            validators : [ 'remote[/secure/core/module/item/workspace-panels/action/validate-unique]', 'notEmpty', 'strLength[0,36]' ],
            preInsert : true,
        }, {
            field : 'title',
            title : 'T_CORE_WK_PANEL.TITLE',
            validators : [ 'strLength[0,64]', 'notEmpty' ],
            preInsert : true,
        }, {
            field : 'url',
            title : 'T_CORE_WK_PANEL.URL',
            validators : [ 'strLength[0,256]', 'notEmpty' ],
            preInsert : true,
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/item/workspace-panels'
});
