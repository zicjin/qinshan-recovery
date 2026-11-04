var coreTenant = Gikam.getPageObject({

    getEditListColumns : function () {
        return [ {
            checkbox : true
        }, {
            field : 'tenantName',
            title : 'T_CORE_TENANT.TENANTNAME'
        } ];
    },

    getBaseInfoFormFields : function () {
        return [ {
            field : 'tenantName',
            title : 'T_CORE_TENANT.TENANTNAME',
            preInsert : true,
            validators : [ 'strLength[0,40]', 'notEmpty' ]
        }, {
            field : 'tenantUrl',
            title : 'T_CORE_TENANT.TENANTURL',
            preInsert : true,
            validators : [ 'strLength[0,90]', 'notEmpty' ]
        } ];
    },

    getPermissionListColumns : function () {
        return [ {
            checkbox : true
        }, {
            field : 'userId',
            title : 'T_CORE_USER.ID',
            width : 150
        }, {
            field : 'ext$.username',
            title : 'T_CORE_USER.USERNAME',
            width : 200,
        }, {
            field : 'ext$.orgid',
            title : 'T_CORE_USER.ORGID',
            width : 150
        }, {
            field : 'ext$.orgname',
            title : 'T_CORE_USER.ORGNAME',
            width : 300,
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/item/tenants'
});
