var modifiedUser = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'userId',
            title : 'T_CORE_USER.ID',
            type : 'link',
            genericQuery : true,
            width: 100
        }, {
            field : 'userNo',
            title : 'T_CORE_USER.USERNO',
            genericQuery : true,
            width: 150
        }, {
            field : 'userName',
            title : 'T_CORE_USER.USERNAME',
            width : 200,
            genericQuery : true
        }, {
            field : 'scheduledTime',
            title : 'T_CORE_M_USER.SCHEDULEDTIME',
            width: 200,
            type : 'dateTime',
            genericQuery : true
        }, {
            field : 'orgId',
            title : 'T_CORE_USER.ORGID',
            width: 150
        }, {
            field : 'orgName',
            title : 'T_CORE_USER.ORGNAME',
            width : 300,
            genericQuery : true
        }, {
            field : 'status',
            title : 'T_CORE_USER.STATUS',
            type : 'select',
            category : 'sys',
            width: 100,
            param : {
                codeCategoryId : 'userStatus'
            }
        } ]
    },
    
    getActivatedListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'userId',
            title : 'T_CORE_USER.ID',
            type : 'link',
            genericQuery : true,
            width: 100
        }, {
            field : 'userNo',
            title : 'T_CORE_USER.USERNO',
            genericQuery : true,
            width: 150
        }, {
            field : 'userName',
            title : 'T_CORE_USER.USERNAME',
            width : 200,
            genericQuery : true
        }, {
            field : 'activatedTime',
            title : 'T_CORE_M_USER.ACTIVATEDTIME',
            width: 200,
            genericQuery : true
        }, {
            field : 'scheduledTime',
            title : 'T_CORE_M_USER.SCHEDULEDTIME',
            type : 'dateTime',
            width: 200,
            genericQuery : true
        }, {
            field : 'orgId',
            title : 'T_CORE_USER.ORGID',
            width: 150
        }, {
            field : 'orgName',
            title : 'T_CORE_USER.ORGNAME',
            width : 300,
            genericQuery : true
        }, {
            field : 'status',
            title : 'T_CORE_USER.STATUS',
            type : 'select',
            category : 'sys',
            width: 100,
            param : {
                codeCategoryId : 'userStatus'
            }
        } ]
    },

    getBaseInfoFormFields : function() {
        var _this = this;
        return [ {
            field : 'userId',
            title : 'T_CORE_USER.ID',
            readonly : true
        }, {
            field : 'userNo',
            title : 'T_CORE_USER.USERNO',
            validators : [ {
                type : 'unique',
                rule : modifiedUser.baseUrl + '/action/validate-unique',
            }, 'strLength[0,32]' ]
        }, {
            field : 'userName',
            title : 'T_CORE_USER.USERNAME',
            validators : [ 'notEmpty', 'strLength[0,21]' ]
        }, {
            field : 'orgId',
            title : 'T_CORE_USER.ORGID',
            type : 'choose',
            category : 'org',
            single : true,
            validators : [ 'notEmpty' ],
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        orgId : rows[0].id,
                        orgName : rows[0].orgName,
                    });
                }
            }
        }, {
            field : 'orgName',
            title : 'T_CORE_USER.ORGNAME',
            readonly : true
        }, {
            field : 'expiryDate',
            title : 'T_CORE_USER.EXPIRYDATE',
            type : 'date'
        }, {
            field : 'idCard',
            title : 'T_CORE_USER.IDCARD',
            validators : [ {
                type : 'unique',
                rule : modifiedUser.baseUrl + '/action/validate-unique',
            } ]
        }, {
            field : 'mobile',
            title : 'T_CORE_USER.MOBILE',
            validators : [ {
                type : 'unique',
                rule : modifiedUser.baseUrl + '/action/validate-unique',
            }, 'phone' ]
        }, {
            field : 'email',
            title : 'T_CORE_USER.EMAIL',
            validators : [ {
                type : 'unique',
                rule : modifiedUser.baseUrl + '/action/validate-unique',
            }, 'email' ]
        }, {
            field : 'status',
            title : 'T_CORE_USER.STATUS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'userStatus'
            }
        }, {
            field : 'ext$.aduserid',
            title : 'T_CORE_AD_DOMAIN_USER.ADUSERID',
            onChange : function(name, value, index) {
                var form = this;
                var id = form.getData().id;
                // 禁用自动保存
                form.disableAutoSave();

                workspace.window.showMask(true);
                var loginConfigUrl = Gikam.printf(IFM_CONTEXT + '/secure/module/sys/login-configs/{id}', {
                    id : 'login-config'
                });
                Gikam.getJson(loginConfigUrl).done(function(loginConfig) {
                    var data = {
                        id : id
                    };
                    if (Gikam.isNotEmpty(loginConfig.adUserIdIgnoreCase) && loginConfig.adUserIdIgnoreCase === '1') {
                        data.adUserId = value.toLowerCase();
                    } else {
                        data.adUserId = value;
                    }
                    var url = IFM_CONTEXT + '/secure/core/module/sys/ad-domains/ad-domain-users';
                    Gikam.post(url, Gikam.getJsonWrapper(null, [ '', [ data ] ])).always(function() {
                        workspace.window.closeMask();
                        form.enableAutoSave();
                    });
                });
            }
        }, {
            /* ignore */
        }, {
            field : 'multiLogin',
            title : 'T_CORE_USER.MULTILOGIN',
            type : 'select',
            category : 'sys',
            firstBlank : false,
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            field : 'readonly',
            title : 'T_CORE_USER.READONLY',
            type : 'select',
            category : 'sys',
            value : '0',
            firstBlank : false,
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            field : 'scheduledTime',
            title : 'T_CORE_M_USER.SCHEDULEDTIME',
            type : 'dateTime',
            validators : [ 'notEmpty' ]
        }, {
            field : 'ext$.electronicsignature',
            type : 'image',
            title : 'T_CORE_USER.ELECTRONICSIGNATURE',
            height : 120,
            onClick : function() {
                _this.detailPage.uploadElectronicSignature();
            },
            onCloseClick : function (field) {
                if (!Gikam.isEmpty(_this.detailPage.electronicSignatureFileId)) {
                    workspace.window.showMask(true);

                    Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper({}, [ '', [ {
                        id : _this.detailPage.electronicSignatureFileId
                    } ] ])).always(function () {
                        workspace.window.closeMask();
                    });
                }
            }

        } ]
    },

    getOrgListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orgId',
            width : 300,
            title : 'T_CORE_ORG_USER.ORGID'
        }, {
            field : 'orgName',
            width : 300,
            title : 'T_CORE_ORG_USER.ORGNAME'
        }, {
            field : 'defaultFlag',
            title : 'T_CORE_ORG_USER.DEFAULTFLAG',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        } ];
    },

    getRoleListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.rolename',
            width : 300,
            title : 'T_CORE_ROLE_USER.ROLENAME'
        }, {
            field : 'defaultFlag',
            title : 'T_CORE_ROLE_USER.DEFAULTFLAG',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/mdm/m-users'
});