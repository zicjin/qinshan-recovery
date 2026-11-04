coreCheckConfig.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-check-config-edit-list-grid',
            url : coreCheckConfig.baseUrl + '/queries/raw',
            columns : coreCheckConfig.getEditListColumns(),
            service : 'coreAdminCheckConfigServiceImpl',
            deleteFormatter : function(row) {
                return row.name;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ "core-check-config-edit-list-base-info-form" ]);
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
                        modalTitle : 'CORE.MODULE.ADMIN.CHECKCONFIGS.MODAL.ADD',
                        titleWidth : 110,
                        fields : coreCheckConfig.getBasePreInsertFields(),
                        url : coreCheckConfig.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('core-check-config-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-check-config-edit-list-grid').deleteRows(coreCheckConfig.baseUrl);
                }
            } ]
        }
    },

    onConfigActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getAsyncComp('core-check-config-edit-list-base-info-form').done(function(form) {
                form.refresh({
                    url : Gikam.printf(coreCheckConfig.baseUrl + '/{id}', {
                        id : row.id
                    }),
                });
            });
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.ADMIN.CHECKCONFIGS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    columns : 3,
                    id : 'core-check-config-edit-list-base-info-form',
                    fields : coreCheckConfig.getBaseInfoFormFields(),
                    service : 'coreAdminCheckConfigServiceImpl',
                    onUpdated : function() {
                        var grid = Gikam.getComp('core-check-config-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    }
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-check-config-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 300,
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getBaseInfoTab() ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
