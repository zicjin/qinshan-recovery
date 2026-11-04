corePasswordPolicy.editListPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-password-policy-list-grid',
            url : corePasswordPolicy.baseUrl + '/queries/raw',
            columns : corePasswordPolicy.getEditListColumns(),
            onCellClick : function(field, row) {
                field === 'name' && _this.loadDetailPage(row.id);
            },
            deleteFormatter : function(row) {
                return row.name || row.id;
            },
            suspendFormatter : function(row) {
                return row.name || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.SYS.PASSWORDS.TITLE.ADD_PASSWORD_POLICY',
                        fields : corePasswordPolicy.getBaseInfoFormFields(),
                        url : corePasswordPolicy.baseUrl,
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    if (!_this.testPasswordPolicyRowsIgnoreOperation()) {
                        Gikam.getComp('core-password-policy-list-grid').deleteRows(corePasswordPolicy.baseUrl);
                    }
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.UNSUSPEND',
                icon : 'enable',
                onClick : function() {
                    Gikam.getComp('core-password-policy-list-grid').unsuspendRows(corePasswordPolicy.baseUrl + '/action/unsuspend');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.SUSPEND',
                icon : 'disable',
                onClick : function() {
                    if (!_this.testPasswordPolicyRowsIgnoreOperation()) {
                        Gikam.getComp('core-password-policy-list-grid').suspendRows(corePasswordPolicy.baseUrl + '/action/suspend');
                    }
                }
            } ]
        }
    },

    getBtnToolbarParam : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'btnGroup',
                items : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add',
                    onClick : function() {
                        Gikam.preInsert({
                            modalTitle : 'CORE.MODULE.SYS.PASSWORDS.TITLE.ADD_PASSWORD_POLICY',
                            fields : corePasswordPolicy.getBaseInfoFormFields(),
                            url : corePasswordPolicy.baseUrl,
                        }).done(function(id) {
                            _this.loadDetailPage(id);
                        });
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove',
                    onClick : function() {
                        if (!_this.testPasswordPolicyRowsIgnoreOperation()) {
                            Gikam.getComp('core-password-policy-list-grid').deleteRows(corePasswordPolicy.baseUrl);
                        }
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.UNSUSPEND',
                    icon : 'enable',
                    onClick : function() {
                        Gikam.getComp('core-password-policy-list-grid').unsuspendRows(corePasswordPolicy.baseUrl + '/action/unsuspend');
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.SUSPEND',
                    icon : 'disable',
                    onClick : function() {
                        if (!_this.testPasswordPolicyRowsIgnoreOperation()) {
                            Gikam.getComp('core-password-policy-list-grid').suspendRows(corePasswordPolicy.baseUrl + '/action/suspend');
                        }
                    }
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    testPasswordPolicyRowsIgnoreOperation : function() {
        var selectedPasswordPolicyRows = Gikam.getComp('core-password-policy-list-grid').getSelections();
        if (selectedPasswordPolicyRows.length > 0) {
            if (selectedPasswordPolicyRows.find(function(row) {
                return row.type === 'GLOBAL';
            }) != undefined) {
                Gikam.alert('CORE.MODULE.SYS.T_CORE_PASSWORD_POLICY.TIP.GLOBAL_TYPE_IGNORE_OPERATION');
                return true;
            }
            ;
        }

        return false;
    },

    loadDetailPage : function(id) {
        workspace.window.load(corePasswordPolicy.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function() {
        this.create();
    }
};