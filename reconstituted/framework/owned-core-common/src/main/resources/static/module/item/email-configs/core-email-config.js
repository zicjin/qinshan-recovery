var coreEmailConfig = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/core/module/item/email-configs',

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_EMAIL_CONFIG.ID',
            readonly : true,
            preInsert : true,
            validators : [ 'remote[/secure/core/module/item/email-configs/{id}/action/validate-unique]', 'notEmpty', 'strLength[0,96]' ]
        }, {
            field : 'configName',
            title : 'T_CORE_EMAIL_CONFIG.CONFIGNAME',
            readonly : true,
            preInsert : true
        }, {
            field : 'templateId',
            title : 'T_CORE_EMAIL_CONFIG.TEMPLATEID',
            category : 'email-template',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                Gikam.getComp('core-email-config-detail-base-info-form').setData({
                    ext$ : {
                        templatename : rows[0].templateName,
                    },
                    templateId : rows[0].id
                });
            },
            onClean : function() {
                Gikam.getComp('core-email-config-detail-base-info-form').setData({
                    ext$ : {
                        templatename : '',
                    },
                    templateId : ''
                });
            }
        }, {
            field : 'ext$.templatename',
            title : 'T_CORE_EMAIL_CONFIG.TEMPLATENAME',
            readonly : true
        }, {
            field : 'remark',
            title : 'T_CORE_EMAIL_CONFIG.REMARK',
            validators : [ 'strLength[0,256]' ],
            type : 'textarea',
            colspan : 2,
            rows : 3
        } ]
    },

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_EMAIL_CONFIG.ID',
            field : 'id',
            type : 'link',
            search : true,
            width : 200
        }, {
            title : 'T_CORE_EMAIL_CONFIG.CONFIGNAME',
            field : 'configName',
            search : true,
            width : 200
        }, {
            title : 'T_CORE_EMAIL_CONFIG.TEMPLATEID',
            field : 'templateId',
            search : true,
            width : 200
        }, {
            title : 'T_CORE_EMAIL_CONFIG.TEMPLATENAME',
            field : 'ext$.templatename',
            search : true,
            width : 200
        }, Gikam.status.getSuspendColumn() ]
    },

    getReceipentEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_EMAIL_RECEIPENT.RECEIPENTID',
            field : 'receipentId'
        }, {
            title : 'T_CORE_EMAIL_RECEIPENT.RECEIPENTNAME',
            field : 'ext$.receipentname'
        }, {
            title : 'T_CORE_EMAIL_RECEIPENT.RECEIPENTEMAIL',
            field : 'ext$.receipentemail',
            width : 300
        } ]
    },

    getReceipentRoleListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_EMAIL_CONFIG_REC_ROLE.ROLENAME',
            field : 'ext$.rolename',
            width : 200
        } ]
    },
    
    getCcUserEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_EMAIL_CONFIG_CC_USER.USERID',
            field : 'userId'
        }, {
            title : 'T_CORE_EMAIL_CONFIG_CC_USER.USERNAME',
            field : 'ext$.username'
        }, {
            title : 'T_CORE_EMAIL_CONFIG_CC_USER.EMAIL',
            field : 'ext$.email',
            width : 300
        } ]
    },
    
    getCcRoleListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_EMAIL_CONFIG_CC_ROLE.ROLENAME',
            field : 'ext$.rolename',
            width : 200
        } ]
    }
});