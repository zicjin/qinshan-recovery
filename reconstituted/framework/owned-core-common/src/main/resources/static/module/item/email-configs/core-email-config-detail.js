coreEmailConfig.detailPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_CORE_EMAIL_CONFIG', _this.param.id);
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
        return {
            type : 'tab',
            width : Gikam.width.L,
            panels : [ {
                title : 'CORE.MODULE.ITEM.EMAILS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-email-config-detail-base-info-form',
                    url : this.getCompUrl('baseInfoForm'),
                    service : 'coreEmailConfigServiceImpl',
                    fields : coreEmailConfig.getBaseInfoFormFields()
                } ]
            } ]
        }
    },

    getReceipentTabParam : function() {
        var _this = this;

        return {
            type : 'tab',
            id : 'core-email-config-detail-grid-tab',
            width : Gikam.width.L,
            panels : [ {
                title : 'CORE.MODULE.ITEM.EMAIL-CONFIGS.TAB.RECEIPENTNAME',
                items : [ {
                    type : 'grid',
                    id : 'core-email-config-detail-column-list-grid',
                    url : this.getCompUrl('configReceipentGrid'),
                    service : 'coreEmailConfigReceipentServiceImpl',
                    columns : coreEmailConfig.getReceipentEditListColumns(),
                    deleteFormatter : function(row) {
                        return row.receipentId;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            _this.insertReceipentRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteReceipentRow();
                        }
                    } ]
                } ]
            }, {
                title : 'CORE.MODULE.ITEM.EMAIL-CONFIGS.TAB.RECEIPENTROLE',
                items : [ {
                    type : 'grid',
                    id : 'core-email-config-detail-role-list-grid',
                    url : this.getCompUrl('configRoleGrid'),
                    columns : coreEmailConfig.getReceipentRoleListColumns(),
                    deleteFormatter : function(row) {
                        return row.ext$.rolename || row.roleId;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            _this.insertRoleRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteRoleRow();
                        }
                    } ]
                } ]
            }, {
                title : 'CORE.MODULE.EMAIL_CONFIGS.TAB.CC_USER',
                items : [ {
                    type : 'grid',
                    id : 'core-email-config-detail-cc-user-list-grid',
                    url : this.getCompUrl('configCcUserGrid'),
                    columns : coreEmailConfig.getCcUserEditListColumns(),
                    deleteFormatter : function(row) {
                        return row.ext$.username || row.userId;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            _this.insertCcUserRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteCcUserRow();
                        }
                    } ]
                } ]
            }, {
                title : 'CORE.MODULE.EMAIL_CONFIGS.TAB.CONFIG_CC_ROLE',
                items : [ {
                    type : 'grid',
                    id : 'core-email-config-detail-cc-role-list-grid',
                    url : this.getCompUrl('configCcRoleGrid'),
                    columns : coreEmailConfig.getCcRoleListColumns(),
                    deleteFormatter : function(row) {
                        return row.ext$.rolename || row.roleId;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            _this.insertCcRoleRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteCcRoleRow();
                        }
                    } ]
                } ]
            } ]
        }
    },

    urlManager : {
        baseInfoForm : coreEmailConfig.baseUrl + '/{id}',
        configReceipentGrid : coreEmailConfig.baseUrl + '/{id}/email-config-receipents/queries',
        configRoleGrid : coreEmailConfig.baseUrl + '/{id}/email-config-roles/queries',
        configCcUserGrid : coreEmailConfig.baseUrl + '/{id}/email-config-cc-users/queries',
        configCcRoleGrid : coreEmailConfig.baseUrl + '/{id}/email-config-cc-roles/queries'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-email-config-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'core-email-config-detail-column-list-grid' : this.getCompUrl('configReceipentGrid'),
            'core-email-config-detail-role-list-grid' : this.getCompUrl('configRoleGrid'),
            'core-email-config-detail-cc-user-list-grid' : this.getCompUrl('configCcUserGrid'),
            'core-email-config-detail-cc-role-list-grid' : this.getCompUrl('configCcRoleGrid')
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

    insertReceipentRow : function() {
        var configData = Gikam.getComp('core-email-config-detail-base-info-form').getData();

        var insertReceipentUrl = Gikam.printf(coreEmailConfig.baseUrl + '/{id}/email-config-receipents', {
            id : configData.id
        });

        Gikam.create('modal', {
            title : 'CORE.MODULE.ITEM.EMAIL-CONFIGS.TITLE.RECEIPENTNAME_CHOOSE',
            url : IFM_CONTEXT + Gikam.choose.getConfig('mult-user').url
                    + '?target_unassigned=1&targetTable=T_CORE_EMAIL_CONFIG_RECEIPENT&targetUserIdColumn=RECEIPENTID&targetFilterColumn=CONFIGID&targetFilterValue=' + configData.id,
            onAfterClose : function(rows) {
                if (rows) {
                    var chooseData = rows.map(function(item) {
                        return {
                            receipentId : item.id
                        }
                    });

                    Gikam.getComp('core-email-config-detail-column-list-grid').insert(insertReceipentUrl, chooseData).done(function() {
                    });
                }
            }
        });
    },

    deleteReceipentRow : function() {
        var configData = Gikam.getComp('core-email-config-detail-base-info-form').getData();

        var deleteReceipentUrl = Gikam.printf(coreEmailConfig.baseUrl + '/{id}/email-config-receipents', {
            id : configData.id,
        });

        Gikam.getComp('core-email-config-detail-column-list-grid').deleteRows(deleteReceipentUrl);
    },

    insertRoleRow : function() {

        var configData = Gikam.getComp('core-email-config-detail-base-info-form').getData();

        var insertRoleUrl = Gikam.printf(coreEmailConfig.baseUrl + '/{id}/email-config-roles', {
            id : configData.id
        });

        Gikam.create('modal', {
            title : 'CORE.MODULE.ITEM.EMAIL-CONFIGS.TITLE.RECEIPENTROLE_CHOOSE',
            url : IFM_CONTEXT + Gikam.choose.getConfig('role').url + '?target_unassigned=1&targetTable=T_CORE_EMAIL_CONFIG_REC_ROLE&targetRoleIdColumn=ROLEID&targetFilterColumn=CONFIGID&targetFilterValue=' + configData.id,
            onAfterClose : function(rows) {
                if (rows) {
                    var chooseData = rows.map(function(item) {
                        return {
                            roleId : item.id
                        }
                    });

                    Gikam.getComp('core-email-config-detail-role-list-grid').insert(insertRoleUrl, chooseData).done(function() {
                    });
                }
            }
        });
    },

    deleteRoleRow : function() {

        var configData = Gikam.getComp('core-email-config-detail-base-info-form').getData();

        var deleteRoleUrl = Gikam.printf(coreEmailConfig.baseUrl + '/{id}/email-config-roles', {
            id : configData.id
        });

        Gikam.getComp('core-email-config-detail-role-list-grid').deleteRows(deleteRoleUrl);
    },

    insertCcUserRow : function() {
        var configData = Gikam.getComp('core-email-config-detail-base-info-form').getData();

        var insertCcUserUrl = Gikam.printf(coreEmailConfig.baseUrl + '/{id}/email-config-cc-users', {
            id : configData.id
        });

        Gikam.create('modal', {
            title : 'CORE.MODULE.EMAIL_CONFIGS.TITLE.CHOOSE_CC_USER',
            url : IFM_CONTEXT + Gikam.choose.getConfig('mult-user').url + '?target_unassigned=1&targetTable=T_CORE_EMAIL_CONFIG_CC_USER&targetUserIdColumn=USERID&targetFilterColumn=CONFIGID&targetFilterValue=' + configData.id,
            onAfterClose : function(rows) {
                if (rows) {
                    var chooseData = rows.map(function(item) {
                        return {
                            userId : item.id
                        }
                    });

                    Gikam.getComp('core-email-config-detail-cc-user-list-grid').insert(insertCcUserUrl, chooseData).done(function() {
                    });
                }
            }
        });
    },

    deleteCcUserRow : function() {
        var configData = Gikam.getComp('core-email-config-detail-base-info-form').getData();

        var deleteCcUserUrl = Gikam.printf(coreEmailConfig.baseUrl + '/{id}/email-config-cc-users', {
            id : configData.id,
        });

        Gikam.getComp('core-email-config-detail-cc-user-list-grid').deleteRows(deleteCcUserUrl);
    },

    insertCcRoleRow : function() {

        var configData = Gikam.getComp('core-email-config-detail-base-info-form').getData();

        var insertCcRoleUrl = Gikam.printf(coreEmailConfig.baseUrl + '/{id}/email-config-cc-roles', {
            id : configData.id
        });

        Gikam.create('modal', {
            title : 'CORE.MODULE.EMAIL_CONFIGS.TITLE.CHOOSE_CC_ROLE',
            url : IFM_CONTEXT + Gikam.choose.getConfig('role').url + '?target_unassigned=1&targetTable=T_CORE_EMAIL_CONFIG_CC_ROLE&targetRoleIdColumn=ROLEID&targetFilterColumn=CONFIGID&targetFilterValue=' + configData.id,
            onAfterClose : function(rows) {
                if (rows) {
                    var chooseData = rows.map(function(item) {
                        return {
                            roleId : item.id
                        }
                    });

                    Gikam.getComp('core-email-config-detail-cc-role-list-grid').insert(insertCcRoleUrl, chooseData).done(function() {
                    });
                }
            }
        });
    },

    deleteCcRoleRow : function() {

        var configData = Gikam.getComp('core-email-config-detail-base-info-form').getData();

        var deleteCcRoleUrl = Gikam.printf(coreEmailConfig.baseUrl + '/{id}/email-config-cc-roles', {
            id : configData.id
        });

        Gikam.getComp('core-email-config-detail-cc-role-list-grid').deleteRows(deleteCcRoleUrl);
    },

    createPage : function() {
        Gikam.create('layout', {
            cls : 'detail-page',
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab(), this.getReceipentTabParam() ]
            },
            renderTo : workspace.window.$dom
        });

        return this;
    },

    init : function(param) {
        this.param = param;

        this.createPage();
    }
}