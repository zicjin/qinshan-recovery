var coreSelectConfig = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/admin/select-configs',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'name',
            title : 'T_CORE_ADMIN_SELECT_CONFIG.NAME',
            readonly : true,
        } ];
    },

    getBasePreInsertFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_ADMIN_SELECT_CONFIG.ID'
        }, {
            field : 'name',
            title : 'T_CORE_ADMIN_SELECT_CONFIG.NAME',
            validators : [ 'unique[/secure/core/module/admin/select-configs/action/validate-unique]', 'notEmpty', 'strLength[0,64]' ],
            preInsert : true
        }, {}, {
            field : 'category',
            title : 'T_CORE_ADMIN_SELECT_CONFIG.CATEGORY',
            validators : [ 'unique[/secure/core/module/admin/select-configs/action/validate-unique]', 'notEmpty', 'strLength[0,32]' ],
            preInsert : true,
        }, {
            field : 'textField',
            title : 'T_CORE_ADMIN_SELECT_CONFIG.TEXTFIELD',
            validators : [ 'notEmpty', 'strLength[0,64]' ],
            preInsert : true,
        }, {
            field : 'valueField',
            title : 'T_CORE_ADMIN_SELECT_CONFIG.VALUEFIELD',
            validators : [ 'notEmpty', 'strLength[0,64]' ],
            preInsert : true,
        }, {
            field : 'url',
            title : 'T_CORE_ADMIN_SELECT_CONFIG.URL',
            validators : [ 'notEmpty', 'strLength[0,256]' ],
            preInsert : true,
            colspan : 3
        }, {
            field : 'cacheable',
            title : 'T_CORE_ADMIN_SELECT_CONFIG.CACHEABLE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            preInsert : true,
            firstBlank : false,
            selectedIndex : 1
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_ADMIN_SELECT_CONFIG.ID'
        }, {
            field : 'name',
            title : 'T_CORE_ADMIN_SELECT_CONFIG.NAME',
            validators : [ 'unique[/secure/core/module/admin/select-configs/action/validate-unique]', 'notEmpty', 'strLength[0,64]' ],
        }, {}, {
            field : 'category',
            title : 'T_CORE_ADMIN_SELECT_CONFIG.CATEGORY',
            validators : [ 'unique[/secure/core/module/admin/select-configs/action/validate-unique]', 'notEmpty', 'strLength[0,32]' ],
        }, {
            field : 'textField',
            title : 'T_CORE_ADMIN_SELECT_CONFIG.TEXTFIELD',
            validators : [ 'notEmpty', 'strLength[0,64]' ],
        }, {
            field : 'valueField',
            title : 'T_CORE_ADMIN_SELECT_CONFIG.VALUEFIELD',
            validators : [ 'notEmpty', 'strLength[0,64]' ],
        }, {
            field : 'url',
            title : 'T_CORE_ADMIN_SELECT_CONFIG.URL',
            validators : [ 'notEmpty', 'strLength[0,256]' ],
            colspan : 3
        }, {
            field : 'cacheable',
            title : 'T_CORE_ADMIN_SELECT_CONFIG.CACHEABLE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            firstBlank : false
        } ];
    },

});
