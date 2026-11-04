//@ sourceURL=core-password-policy-detail.js
corePasswordPolicy.detailPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_CORE_PASSWORD_POLICY', _this.param.id);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;

        return {
            type : 'tab',
            width : 900,
            panels : [ {
                title : 'CORE.MODULE.SYS.T_CORE_PASSWORD_POLICY.TAB.BASEINFO',
                items : [ {
                    type : 'form',
                    id : 'core-password-policy-detail-base-info-form',
                    columns : 3,
                    titleWidth : 200,
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'corePasswordPolicyServiceImpl',
                    fields : corePasswordPolicy.getBaseInfoFormFields(),
                    onValidating : function(data) {
                        if (!Gikam.isEmpty(data.minLength) && !Gikam.isEmpty(data.maxLength)) {
                            if (parseInt(data.minLength) > parseInt(data.maxLength)) {
                                return {
                                    result : false,
                                    message : 'CORE.MODULE.SYS.T_CORE_PASSWORD_POLICY.TIP.MINLENGTH_NOT_MORE_MAXLENGTH'
                                };
                            }
                        }

                        if (!Gikam.isEmpty(data.expiryWarningInterval) && !Gikam.isEmpty(data.expiryInterval)) {
                            if (parseInt(data.expiryWarningInterval) > parseInt(data.expiryInterval)) {
                                return {
                                    result : false,
                                    message : 'CORE.MODULE.SYS.PASSWORDPOLICIES.TIP.EXPIRYWARNINGINTERVAL_NOT_MORE_EXPIRYINTERVAL'
                                };
                            }
                        }

                    }
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            center : {
                items : [ _this.getBtnToolbar(), _this.getBaseInfoTab() ]
            },
            renderTo : workspace.window.$dom
        });

        return this;
    },

    urlManager : {
        baseInfoForm : corePasswordPolicy.baseUrl + '/{id}'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-password-policy-detail-base-info-form' : this.getCompUrl('baseInfoForm')
        });
    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        });
    },

    init : function(param) {
        this.param = param;

        this.create();
    }
};