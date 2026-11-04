coreI18n.editPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-i18n-list-grid',
            url : coreI18n.baseUrl + '/queries',
            columns : coreI18n.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (data.length > 0) {
                    this.activeRowByIndex(0);
                }
            },
            onRowActive : function(index, row) {
                var i18nId = row.id;

                if (!Gikam.isEmpty(i18nId)) {
                    Gikam.getComp('core-role-edit-list-base-info-form').refresh({
                        url : coreI18n.baseUrl + '/' + i18nId
                    });

                    Gikam.getComp('core-i18n-edit-list-message-list-grid').refresh({
                        url : coreI18n.baseUrl + '/' + i18nId + '/i18n-messages/queries'
                    });
                }
            },
            deleteFormatter : function(row) {
                return row.description || row.code;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.SYS.I18NS.TITLE.ADD_I18N',
                        fields : coreI18n.getBaseInfoFormFields(),
                        url : coreI18n.baseUrl,
                        titleWidth : 110
                    }).done(function(id) {
                        _this.selectRowId = parseInt(id);
                        Gikam.getComp('core-i18n-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('core-i18n-list-grid').deleteRows(coreI18n.baseUrl);
                    _this.selectRowId = null;
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {

                    var selectedRows = Gikam.getComp('core-i18n-list-grid').getSelections();

                    if (selectedRows.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    Gikam.viewAudit('T_CORE_I18N', selectedRows[0].id);
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.SYS.ROLES.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-role-edit-list-base-info-form',
                    service : 'coreI18nServiceImpl',
                    fields : coreI18n.getBaseInfoFormFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('core-i18n-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            } ]
        }
    },

    getMessageGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : "CORE.MODULE.SYS.T_CORE_I18N_MESSAGE.TAB.MESSAGE",
                items : [ {
                    type : 'grid',
                    page : false,
                    id : 'core-i18n-edit-list-message-list-grid',
                    service : 'coreI18nMessageServiceImpl',
                    toolbar : [ {
                        type : 'button',
                        text : 'CORE.MODULE.SYS.T_CORE_I18N_MESSAGE.BTN.INIT_MESSSAGE',
                        icon : 'add-row',
                        onClick : function() {
                            var i18nId = Gikam.getComp('core-i18n-list-grid').getActivedRow().id;

                            if (!Gikam.isEmpty(i18nId)) {
                                var initMessageUrl = Gikam.printf(coreI18n.baseUrl + '/{id}/i18n-messages/action/init', {
                                    id : i18nId
                                });

                                Gikam.put(initMessageUrl).done(function() {
                                    Gikam.getComp('core-i18n-edit-list-message-list-grid').refresh();
                                });
                            }
                        }
                    } ],
                    columns : coreI18n.getMessageListColumns(),
                    genericQuery : false
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-i18n',
            renderTo : workspace.window.$dom,
            west : {
                width : '40%',
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    north : {
                        height : 135,
                        items : [ _this.getBaseInfoTab() ]
                    },
                    center : {
                        items : [ _this.getMessageGridParam() ]
                    }
                } ]
            }
        });
    },

    init : function() {
        this.create();
    }
};