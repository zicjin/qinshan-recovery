var coreRole = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/roles',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            title : 'T_CORE_ROLE.ROLENAME',
            field : 'roleName',
            width : 250
        } ]
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'roleName',
            title : 'T_CORE_ROLE.ROLENAME',
            preInsert : true,
            validators : [ 'notEmpty', 'strLength[0,21]' ]
        }, {
            field : 'passwordPolicyId',
            title : 'T_CORE_ROLE.PASSWORDPOLICYID',
            type : 'select',
            category : 'password-policy'
        }, {
            field : 'usedByOrgName',
            title : 'T_CORE_ROLE.USEDBYORGNAME',
            type : 'choose',
            category : 'org',
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        usedByOrgId : rows.map(function(row) {
                            return row.id;
                        }).join(),
                        usedByOrgName : rows.map(function(row) {
                            return row.orgName;
                        }).join(),
                    });
                }
            },
            onClean : function() {
                this.setData({
                    usedByOrgId : '',
                    usedByOrgName : ''
                });
            }
        }, {
            field : 'roleDesc',
            title : 'T_CORE_ROLE.ROLEDESC',
            validators : [ 'strLength[0,64]' ]
        } ]
    },

    getUserListColumns : function() {
        return [ {
            checkbox : true
        }, {
            title : 'T_CORE_ROLE.USERID',
            field : 'userId'
        }, {
            title : 'T_CORE_ROLE.USERNAME',
            field : 'ext$.username'
        }, {
            title : 'T_CORE_ROLE.ORGID',
            field : 'ext$.orgid'
        }, {
            title : 'T_CORE_ROLE.ORGNAME',
            field : 'ext$.orgname',
            width : 140
        } ]
    },

    getChooseListColumns : function() {
        return [ {
            title : 'T_CORE_ROLE.ROLENAME',
            field : 'roleName',
            type : 'link',
            width : 300,
            genericQuery : true
        }, {
            title : 'T_CORE_ROLE.ROLEDESC',
            field : 'roleDesc',
            width : 300
        } ]
    },
});