var coreCheckConfig = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/admin/check-configs',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'name',
            title : 'T_CORE_ADMIN_CHECK_CONFIG.NAME'
        } ];
    },

    getBasePreInsertFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_ADMIN_CHECK_CONFIG.ID',
            validators : [ 'remote[/secure/core/module/admin/check-configs/{id}/action/validate-unique]', 'notEmpty', 'strLength[0,36]' ],
            preInsert : true,
        }, {
            field : 'name',
            title : 'T_CORE_ADMIN_CHECK_CONFIG.NAME',
            validators : [ 'notEmpty', 'strLength[0,64]' ],
            preInsert : true
        }, {
            field : 'url',
            title : 'T_CORE_ADMIN_CHECK_CONFIG.URL',
            validators : [ 'notEmpty', 'strLength[0,270]' ],
            preInsert : true
        }, {
            field : 'groupDisplay',
            title : 'T_CORE_ADMIN_CHECK_CONFIG.GROUPDISPLAY',
            type : 'select',
            category : 'yesorno',
            preInsert : true,
            firstBlank : false,
            selectedIndex : 0
        }, {
            field : 'cacheable',
            title : 'T_CORE_ADMIN_CHECK_CONFIG.CACHEABLE',
            type : 'select',
            category : 'yesorno',
            firstBlank : false,
            preInsert : true,
            selectedIndex : 1
        }, {
            field : 'displayOther',
            title : 'T_CORE_ADMIN_CHECK_CONFIG.DISPLAYOTHER',
            type : 'select',
            category : 'yesorno',
            preInsert : true,
            firstBlank : false,
            selectedIndex : 0
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_ADMIN_CHECK_CONFIG.ID',
            readonly : true
        }, {
            field : 'name',
            title : 'T_CORE_ADMIN_CHECK_CONFIG.NAME',
            validators : [ 'notEmpty', 'strLength[0,64]' ],
            colspan : 2
        }, {
            field : 'url',
            title : 'T_CORE_ADMIN_CHECK_CONFIG.URL',
            validators : [ 'notEmpty', 'strLength[0,270]' ],
            colspan : 3
        }, {
            field : 'groupDisplay',
            title : 'T_CORE_ADMIN_CHECK_CONFIG.GROUPDISPLAY',
            type : 'select',
            category : 'yesorno',
            firstBlank : false,
            selectedIndex : 0
        }, {
            field : 'cacheable',
            title : 'T_CORE_ADMIN_CHECK_CONFIG.CACHEABLE',
            type : 'select',
            category : 'yesorno',
            firstBlank : false,
            selectedIndex : 1
        }, {
            field : 'displayOther',
            title : 'T_CORE_ADMIN_CHECK_CONFIG.DISPLAYOTHER',
            type : 'select',
            category : 'yesorno',
            firstBlank : false,
            selectedIndex : 0
        }, {
            field : 'otherRequired',
            title : 'T_CORE_ADMIN_CHECK_CONFIG.OTHERREQUIRED',
            type : 'select',
            category : 'yesorno',
            firstBlank : false,
            selectedIndex : 0
        } ];
    },

});
