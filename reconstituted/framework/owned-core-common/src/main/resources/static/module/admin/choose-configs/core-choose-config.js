var coreChooseConfig = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/admin/choose-configs',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'name',
            title : 'T_CORE_ADMIN_CHOOSE_CONFIG.NAME',
            readonly : true,
        } ];
    },

    getBasePreInsertFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_ADMIN_CHOOSE_CONFIG.ID'
        }, {
            field : 'name',
            title : 'T_CORE_ADMIN_CHOOSE_CONFIG.NAME',
            validators : [ 'unique[/secure/core/module/admin/choose-configs/action/validate-unique]', 'notEmpty', 'strLength[0,64]' ],
            preInsert : true
        }, {
        }, {
            field : 'category',
            title : 'T_CORE_ADMIN_CHOOSE_CONFIG.CATEGORY',
            validators : [ 'unique[/secure/core/module/admin/choose-configs/action/validate-unique]', 'notEmpty', 'strLength[0,32]' ],
            preInsert : true,
        }, {
            field : 'title',
            title : 'T_CORE_ADMIN_CHOOSE_CONFIG.TITLE',
            validators : [ 'notEmpty', 'strLength[0,64]' ],
            preInsert : true,
        }, {
            field : 'width',
            title : 'T_CORE_ADMIN_CHOOSE_CONFIG.WIDTH',
        }, {
            field : 'url',
            title : 'T_CORE_ADMIN_CHOOSE_CONFIG.URL',
            validators : [ 'notEmpty', 'strLength[0,256]' ],
            preInsert : true,
            colspan : 3
        } ];
    },
    
    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_ADMIN_CHOOSE_CONFIG.ID'
        }, {
            field : 'name',
            title : 'T_CORE_ADMIN_CHOOSE_CONFIG.NAME',
            validators : [ 'unique[/secure/core/module/admin/choose-configs/action/validate-unique]', 'notEmpty', 'strLength[0,64]' ],
        }, {
        }, {
            field : 'category',
            title : 'T_CORE_ADMIN_CHOOSE_CONFIG.CATEGORY',
            validators : [ 'unique[/secure/core/module/admin/choose-configs/action/validate-unique]', 'notEmpty', 'strLength[0,32]' ],
        }, {
            field : 'title',
            title : 'T_CORE_ADMIN_CHOOSE_CONFIG.TITLE',
            validators : [ 'notEmpty', 'strLength[0,64]' ],
        }, {
            field : 'width',
            title : 'T_CORE_ADMIN_CHOOSE_CONFIG.WIDTH',
        }, {
            field : 'i18nCode',
            title : 'T_CORE_ADMIN_CHOOSE_CONFIG.I18NCODE',
            validators : [ 'strLength[0,128]' ],
        }, {
            field : 'url',
            title : 'T_CORE_ADMIN_CHOOSE_CONFIG.URL',
            validators : [ 'notEmpty', 'strLength[0,256]' ],
            colspan : 3
        } ];
    },
});
