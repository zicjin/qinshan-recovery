coreUser.changePassworldPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.SAVE',
                icon : 'save',
                onClick : function() {
                    _this.ensure();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    getChangeInfoForm : function() {
        var _this = this;
        if (_this.param.dataFrom && _this.param.dataFrom === 'gird') {
            fields = coreUser.getChangePwdInfoFormFieldsForGrid();
        } else {
            fields = coreUser.getChangePwdInfoFormFields();
        }

        return {
            type : 'form',
            columns : 1,
            id : 'core-user-change-password-info-form',
            fields : fields,
            autoSave : false
        }
    },

    getPasswordPolicyInfoForm : function() {
        var _this = this;
        var fields = coreUser.getPasswordPolicyInfoFormFields();

        return {
            type : 'form',
            columns : 2,
            titleWidth : 120,
            id : 'core-password-policy-info-form',
            url : IFM_CONTEXT + Gikam.printf('/open/core/module/mdm/user-passwords/users/{userId}/password-policies/queries', {
                userId : _this.param.userId
            }),
            autoSave : false,
            panels : [ {
                title : 'T_CORE_PASSWORD_POLICY.SERVICE_NAME',
                fields : fields
            } ]
        }
    },

    ensure : function() {
        var _this = this;
        var form = Gikam.getComp('core-user-change-password-info-form');
        var formData = form.getData();
        var url = coreUser.baseUrl + '/' + (_this.param.userId ? _this.param.userId : 'current-user') + '/action/change-password';
        if (form.validate() === false) {
            return;
        }

        if (!_this.param.userId && formData.newPassword === formData.oldPassword) {
            Gikam.alert('CORE.MODULE.MDM.USERS.TIP.SAME_PASSWORD_PLEASE_INPUT_AGAIN');
            form.setData({
                newPassword : ''
            });
            return;
        }

        if (formData.newPassword !== formData.ensurePassword) {
            Gikam.alert('CORE.MODULE.MDM.USERS.TIP.ENSURE_PASSWORD_WRONG_INPUT_AGAIN');
            form.setData({
                ensurePassword : ''
            });
            return;
        }

        form.setData({
            oldPassword : formData.oldPassword,
            newRawPassword : formData.newPassword
        });

        this.save();
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBtnToolbar(), this.getChangeInfoForm(), this.getPasswordPolicyInfoForm() ]
            },

            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    formatFormData : function() {
        var def = Gikam.getDeferred();
        Gikam.RsaEncryptUtils.getPublicKey().then(function(publicKey) {
            var data = Gikam.getComp('core-user-change-password-info-form').getData();
            var fomatData = {};
            Gikam.each(data, function(key) {
                if (key === 'ext$') {
                    return true;
                }
                if (key === 'newRawPassword' || key === 'oldPassword') {
                    fomatData[key] = Gikam.RsaEncryptUtils.encode(publicKey, data[key]);
                }
            });
            def.resolve({
                publicKey : publicKey,
                data : fomatData
            });
        });
        return def;
    },

    save : function() {
        var _this = this;
        var window = Gikam.getLastModal().window;
        var url = coreUser.baseUrl + '/' + (this.param.userId ? this.param.userId : 'current-user') + '/action/change-password';
        window.showMask();
        this.formatFormData().done(function(result) {
            Gikam.put(url, Gikam.getJsonWrapper(result.data), {
                'SECURITY-RSA-KEY' : result.publicKey
            }).done(function() {
                if (Gikam.isTrue(_this.param.reload)) {
                    Gikam.alert('CORE.MODULE.MDM.USERS.TIP.REVISE_SUCCESS_PLEASE_RE_LOGIN');
                    Gikam.getLastModal().close();
                    setTimeout(function() {
                        Gikam.postText(IFM_CONTEXT + '/core/module/sys/logout').done(function() {
                            location.href = IFM_CONTEXT + "/"
                        });
                    }, 3000);
                } else {
                    Gikam.alert('CORE.MODULE.MDM.USERS.TIP.REVISE_SUCCESS');
                    Gikam.getLastModal().close();
                }
            }).fail(function(e) {
                if (e.status === 400) {
                    Gikam.alert(e.responseText);
                }
            }).always(function() {
                Gikam.getLastModal().window.closeMask();
            });
        });
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}