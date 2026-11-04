coreWorkspaceScheme.editPage = {

    getGridParam : function() {
        var _this = this;
		var isMoveClick = false;
        return {
            type : 'grid',
            id : 'core-workspace-scheme-edit-list-grid',
            url : coreWorkspaceScheme.baseUrl + '/queries/raw',
            columns : coreWorkspaceScheme.getEditListColumns(),
            service : 'coreWorkspaceSchemeServiceImpl',
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    !isMoveClick && this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('core-workspace-scheme-edit-list-index-panel-config').clear();
                }
				isMoveClick = false;
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-workspace-scheme-edit-list-index-panel-config').done(function(config) {
                    config.refresh({
                        schemeId : row.id,
                        url : Gikam.printf(coreWorkspaceScheme.baseUrl + '/{id}/panel-configs', {
                            id : row.id
                        })
                    });
                });
            },
            deleteFormatter : function(row) {
                return row.schemeName || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.ITEM.WORKSPACESCHEMES.MODAL.ADD',
                        fields : coreWorkspaceScheme.getBaseInfoFormFields(),
                        url : coreWorkspaceScheme.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('core-workspace-scheme-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-workspace-scheme-edit-list-grid').deleteRows(coreWorkspaceScheme.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.MOVE_UP',
                icon : 'move-up',
                onClick : function() {
					isMoveClick = true;
                    var schemeGrid = Gikam.getComp('core-workspace-scheme-edit-list-grid');

                    schemeGrid.moveUp(coreWorkspaceScheme.baseUrl + '/action/swap/{firstId}/{secondId}').done(function() {
                        schemeGrid.selectRowById(schemeGrid.getSelections()[0].id);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.MOVE_DOWN',
                icon : 'move-down',
                onClick : function() {
					isMoveClick = true;
                    var schemeGrid = Gikam.getComp('core-workspace-scheme-edit-list-grid');

                    schemeGrid.moveDown(coreWorkspaceScheme.baseUrl + '/action/swap/{firstId}/{secondId}').done(function() {
                        schemeGrid.selectRowById(schemeGrid.getSelections()[0].id);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {

                    var selectedRows = Gikam.getComp('core-workspace-scheme-edit-list-grid').getSelections();

                    if (selectedRows.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    Gikam.viewAudit('T_CORE_WK_SCHEME', selectedRows[0].id);
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-workspace-scheme-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 310,
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'IndexPanelConfig',
                    id : 'core-workspace-scheme-edit-list-index-panel-config'
                } ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
