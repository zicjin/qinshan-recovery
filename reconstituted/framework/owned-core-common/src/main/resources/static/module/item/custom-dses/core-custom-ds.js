var coreCustomDs = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'dsName',
            title : 'T_CORE_CUSTOM_DS.DSNAME',
            type : 'link',
            width : 400
        }, {
            field : 'dsSql',
            title : 'T_CORE_CUSTOM_DS.DSSQL',
            type : 'richText',
            sort : false,
            width : 600
        }, Gikam.status.getActiveColumn()];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'dsName',
            title : 'T_CORE_CUSTOM_DS.DSNAME',
            validators : [ 'strLength[0,90]', 'notEmpty' ],
            colspan : 3,
            preInsert : true
        } ];
    },
    
    getPreviewFormFields : function() {
        return [ {
            field : 'dsSql',
            height : 700,
            colspan : 3,
            type : 'textarea'
        } ];
    },

    getFieldEditListColumns : function() {
        return [{
            checkbox : true
        }, {
            field : 'columnName',
            title : 'T_CORE_CUSTOM_DS_FIELD.COLUMNNAME',
            width : 290
        }, {
            field : 'aliasName',
            title : 'T_CORE_CUSTOM_DS_FIELD.ALIASNAME',
            validators : [ 'strLength[0,64]' ],
            editor : true,
            width : 290
        } ];
    },
    getChooseListColumns : function() {
        return [{
            field : 'dsName',
            title : 'T_CORE_CUSTOM_DS.DSNAME',
            width : 200
        }, {
            field : 'dsSql',
            title : 'T_CORE_CUSTOM_DS.DSSQL',
            type : 'richText',
            sort : false,
            width : 400
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/item/custom-dses'
});
