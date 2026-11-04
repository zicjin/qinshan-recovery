coreWorkspacePanel.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-workspace-panel-edit-list-grid',
            url : coreWorkspacePanel.baseUrl + '/queries/raw',
            service : 'coreWorkspacePanelServiceImpl',
            dbTable : 'T_CORE_WK_PANEL',
            columns : coreWorkspacePanel.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.title || row.id;
            },
            suspendFormatter : function(row) {
                return row.title || row.id;
            },
            unsuspendFormatter : function(row) {
                return row.title || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.ITEM.WORKSPACEPANELS.MODAL.ADD',
                        fields : coreWorkspacePanel.getBaseInfoFormFields(),
                        url : coreWorkspacePanel.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('core-workspace-panel-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-workspace-panel-edit-list-grid').deleteRows(coreWorkspacePanel.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEW',
                icon : 'view-audit',
                onClick : function() {
                    var selections = Gikam.getComp('core-workspace-panel-edit-list-grid').getSelections();

                    if (selections.length != 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    var url = selections[0].url;
                    if (Gikam.isEmpty(url)) {
                        Gikam.alert('CORE.MODULE.ITEM.WORKSPACEPANELS.TIP.CHOOSE_URL_PANEL');
                        return;
                    }
                    var modal = Gikam.create('modal', {
                        title : 'CORE.MODULE.ITEM.WORKSPACEPANELS.MODAL.VIEW',
                        width : 490,
                        height : 380
                    });

                    Gikam.create('layout', {
                        renderTo : modal.window.$dom,
                        center : {
                            items : [ {
                                type : 'iframe',
                                id : 'core-data-center-edit-list-preview-iframe',
                                src : IFM_CONTEXT + url
                            } ]
                        }
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function() {
                    Gikam.getComp('core-workspace-panel-edit-list-grid').unsuspendRows(coreWorkspacePanel.baseUrl + '/action/unsuspend');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.SUSPEND',
                icon : 'disable',
                onClick : function() {
                    Gikam.getComp('core-workspace-panel-edit-list-grid').suspendRows(coreWorkspacePanel.baseUrl + '/action/suspend');
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-workspace-panel-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
