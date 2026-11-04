corePageConfig.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-page-config-edit-list-grid',
            url : corePageConfig.baseUrl + '/queries/raw',
            columns : corePageConfig.getEditListColumns(),
            service : 'coreAdminPageConfigServiceImpl',
            deleteFormatter : function(row) {
                return row.ext$['menuname'] + ' ' + (row.requestName || '');
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ "core-page-config-edit-list-base-info-form" ]);
                }
            },
            onRowActive : function(index, row) {
                _this.onConfigActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.ADMIN.PAGE_CONFIGS.MODAL.ADD',
                        height : 400,
                        fields : corePageConfig.getBaseInfoPreInsertFormFields(),
                        url : corePageConfig.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('core-page-config-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-page-config-edit-list-grid').deleteRows(corePageConfig.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DESIGN',
                icon : 'edit',
                onClick : function() {
                    _this.designPage();
                }
            } ]
        }
    },

    onConfigActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('core-page-config-edit-list-base-info-form').refresh({
                url : Gikam.printf(corePageConfig.baseUrl + '/{id}', {
                    id : row.id
                }),
            });
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.ADMIN.PAGE_CONFIGS.MODAL.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    columns : 3,
                    id : 'core-page-config-edit-list-base-info-form',
                    fields : corePageConfig.getBaseInfoFormFields(),
                    service : 'coreAdminPageConfigServiceImpl',
                    autoSave : true,
                    onUpdated : function() {
                        var grid = Gikam.getComp('core-page-config-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    }
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-page-config-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 400,
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getBaseInfoTab() ]
            },
        });
    },

    designPage : function() {
        var oldWindow = workspace.window;
        var simpleModal = Gikam.createSimpleModal({
            title : 'CORE.MODULE.ADMIN.PAGE_CONFIGS.MODAL.DESIGN',
            onAfterClose : function() {
                workspace.window = oldWindow
            }
        });
        var form = Gikam.getComp('core-page-config-edit-list-base-info-form');
        workspace.window = simpleModal.modal.window;
        simpleModal.modal.window.load(IFM_CONTEXT + form.getData().requestUrl);
        workspace.window.onReady(function() {
            Gikam.each(this.$compStore.Grid, function() {
                var _this = this;
                this.refreshContextmenu([ {
                    text : 'GIKAM.BUTTON.EDIT',
                    onClick : function() {
                        new Gikam.PageConfig('grid', _this);
                    }
                } ]);
            });
        })
    },

    init : function() {
        this.create();
    }
};
