var coreUser = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'id',
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
        }, {
            field : 'readonly',
            title : 'T_CORE_USER.READONLY',
            type : 'select',
            category : 'yesorno',
            firstBlank : false,
            selectedIndex : 1,
            width: 100
        } ]
    },

    getChooseListColumns : function() {
        return [ {
            index : true
        }, {
            field : 'id',
            title : 'T_CORE_USER.ID',
            genericQuery : true
        }, {
            field : 'userNo',
            title : 'T_CORE_USER.USERNO',
            genericQuery : true
        }, {
            field : 'userName',
            title : 'T_CORE_USER.USERNAME',
            genericQuery : true
        }, {
            field : 'orgName',
            title : 'T_CORE_USER.ORGNAME',
            genericQuery : true,
            width : 140
        } ]
    },

    getSimpleChooseListColumns : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_USER.ID',
            genericQuery : true
        }, {
            field : 'userName',
            title : 'T_CORE_USER.USERNAME',
            genericQuery : true
        } ]
    },

    getBaseInfoFormFields : function() {
        var _this = this;
        return [ {
            field : 'id',
            title : 'T_CORE_USER.ID',
            validators : [ {
                type : 'unique',
                rule : coreUser.baseUrl + '/action/validate-unique',
            }, 'notEmpty', 'strLength[0,32]' ],
            preInsert : true
        }, {
            field : 'userNo',
            title : 'T_CORE_USER.USERNO',
            validators : [ {
                type : 'unique',
                rule : coreUser.baseUrl + '/action/validate-unique',
            }, 'strLength[0,32]' ]
        }, {
            field : 'userName',
            title : 'T_CORE_USER.USERNAME',
            validators : [ 'notEmpty', 'strLength[0,21]' ],
            preInsert : true
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
            },
            preInsert : true
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
                rule : coreUser.baseUrl + '/action/validate-unique',
            } ]
        }, {
            field : 'mobile',
            title : 'T_CORE_USER.MOBILE',
            validators : [ {
                type : 'unique',
                rule : coreUser.baseUrl + '/action/validate-unique',
            }, 'phone' ]
        }, {
            field : 'email',
            title : 'T_CORE_USER.EMAIL',
            validators : [ {
                type : 'unique',
                rule : coreUser.baseUrl + '/action/validate-unique',
            }, 'email' ]
        }, {
            field : 'status',
            title : 'T_CORE_USER.STATUS',
            type : 'select',
            category : 'sys',
            firstBlank : false,
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

                    // 判断adUserId不重复
                    var adUserId;
                    if (Gikam.isNotEmpty(loginConfig.adUserIdIgnoreCase) && loginConfig.adUserIdIgnoreCase === '1') {
                        adUserId = value.toLowerCase();
                    } else {
                        adUserId = value;
                    }

                    var validateUrl = Gikam.printf(IFM_CONTEXT + '/secure/core/module/sys/ad-domains/ad-domain-users/action/validate-unique/{columnName}/{columnValue}', {
                        columnName : 'adUserId',
                        columnValue : adUserId
                    });
                    Gikam.post(validateUrl).done(function(validateReult) {
                        if (validateReult.valid) {
                            var data = {
                                id : id,
                                adUserId : adUserId
                            };
                            var url = IFM_CONTEXT + '/secure/core/module/sys/ad-domains/ad-domain-users';
                            Gikam.post(url, Gikam.getJsonWrapper(null, [ '', [ data ] ])).always(function() {
                                form.enableAutoSave();
                            });
                        } else {
                            // adUserId在T_CORE_AD_DOMAIN_USER重复了
                            Gikam.alert(validateReult.message);
                        }
                    }).always(function() {
                        workspace.window.closeMask();
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
            /* ignore */
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

    getChangePwdInfoFormFields : function() {
        return [ {
            field : 'oldPassword',
            title : 'T_CORE_USER.OLDPASSWORD',
            type : 'password',
            validators : [ 'notEmpty', 'strLength[1,32]' ],
        }, {
            field : 'newPassword',
            title : 'T_CORE_USER.NEWPASSWORD',
            type : 'password',
            validators : [ 'notEmpty', 'strLength[1,32]' ]
        }, {
            field : 'ensurePassword',
            title : 'T_CORE_USER.ENSUREPASSWORD',
            type : 'password',
            validators : [ 'notEmpty' ]
        }, ]
    },

    getChangePwdInfoFormFieldsForGrid : function() {
        return [ {
            field : 'newPassword',
            title : 'T_CORE_USER.NEWPASSWORD',
            type : 'password',
            validators : [ 'notEmpty', 'strLength[1,32]' ]
        }, {
            field : 'ensurePassword',
            title : 'T_CORE_USER.ENSUREPASSWORD',
            type : 'password',
            validators : [ 'notEmpty' ]
        }, ]
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

    getPasswordPolicyInfoFormFields : function() {
        return [ {
            field : 'minNumber',
            title : 'T_CORE_PASSWORD_POLICY.MINNUMBER',
            readonly : true
        }, {
            field : 'minSpecialCharacters',
            title : 'T_CORE_PASSWORD_POLICY.MINSPECIALCHARACTERES',
            readonly : true
        }, {
            field : 'minLowercase',
            title : 'T_CORE_PASSWORD_POLICY.MINLOWERCASE',
            readonly : true
        }, {
            field : 'minUppercase',
            title : 'T_CORE_PASSWORD_POLICY.MINUPPERCASE',
            readonly : true
        }, {
            field : 'minLength',
            title : 'T_CORE_PASSWORD_POLICY.MINLENGTH',
            readonly : true
        }, {
            field : 'maxLength',
            title : 'T_CORE_PASSWORD_POLICY.MAXLENGTH',
            readonly : true
        }, {
            field : 'composition',
            title : 'T_CORE_PASSWORD_POLICY.COMPOSITION',
            readonly : true
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/mdm/users'
});