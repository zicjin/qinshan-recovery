var coreAdminTenant = Gikam.getPageObject({

    getEditListColumns : function () {
        return [ {
            checkbox : true
        }, {
            field : 'tenantName',
            title : 'T_CORE_ADMIN_TENANT.TENANTNAME'
        }, {
            field : 'activatedFlag',
            title : 'T_CORE_ADMIN_TENANT.ACTIVATEDFLAG',
            type : 'simpleCheckbox',
            editor : true,
            width : 100
        } ];
    },

    getBaseInfoFormFields : function () {
        return [ {
            field : 'id',
            title : 'T_CORE_ADMIN_TENANT.ID',
            preInsert : true,
            validators : [ 'remote[/secure/core/module/admin/tenants/{id}/action/validate-unique]', 'notEmpty', 'strLength[0,32]' ]
        }, {
            field : 'tenantName',
            title : 'T_CORE_ADMIN_TENANT.TENANTNAME',
            preInsert : true,
            validators : [ 'strLength[0,40]', 'notEmpty' ]
        }, {
        }, {
            field : 'url',
            title : 'T_CORE_ADMIN_TENANT.URL',
            preInsert : true,
            validators : [ 'strLength[0,512]', 'notEmpty' ]
        }, {
            field : 'username',
            title : 'T_CORE_ADMIN_TENANT.USERNAME',
            preInsert : true,
            validators : [ 'strLength[0,64]', 'notEmpty' ]
        }, {
            field : 'password',
            title : 'T_CORE_ADMIN_TENANT.PASSWORD',
            preInsert : true,
            validators : [ 'strLength[0,64]', 'notEmpty' ]
        } ];
    },

    getUserListColumns : function () {
        return [ {
            checkbox : true
        }, {
            field : 'userId',
            title : 'T_CORE_USER.ID',
            width : 150
        }, {
            field : 'ext$.username',
            title : 'T_CORE_USER.USERNAME'
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/admin/tenants'
});
