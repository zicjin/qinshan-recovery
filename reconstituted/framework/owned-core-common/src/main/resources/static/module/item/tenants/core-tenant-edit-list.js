coreTenant.editPage = {

    getGridParam : function () {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-tenant-edit-list-grid',
            url : coreTenant.baseUrl + '/queries/raw',
            service : 'coreTenantServiceImpl',
            dbTable : 'T_CORE_TENANT',
            columns : coreTenant.getEditListColumns(),
            deleteFormatter : function (row) {
                return row.tenantName || row.id;
            },
            onLoadSuccess : function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-tenant-edit-list-base-info-form', 'core-tenant-edit-list-permission-grid' ]);
                }
            },
            onRowActive : function (index, row) {

                Gikam.getAsyncComp('core-tenant-edit-list-base-info-form').done(function (form) {
                    form.refresh({
                        url : Gikam.printf(coreTenant.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });

                Gikam.getAsyncComp('core-tenant-edit-list-permission-grid').done(function (grid) {
                    grid.refresh({
                        url : Gikam.printf(coreTenant.baseUrl + '/{id}/permissions/queries', {
                            id : row.id
                        })
                    });
                });

            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function () {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.ITEM.TENANTS.MODAL.ADD',
                        fields : coreTenant.getBaseInfoFormFields(),
                        url : coreTenant.baseUrl,
                        width : 450,
                        titleWidth : 120
                    }).done(function (id) {
                        Gikam.getComp('core-tenant-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function () {
                    Gikam.getComp('core-tenant-edit-list-grid').deleteRows(coreTenant.baseUrl);
                }
            } ]
        }
    },

    getBaseInfoTab : function () {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.ITEM.TENANTS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    service : 'coreTenantServiceImpl',
                    dbTable : 'T_CORE_TENANT',
                    id : 'core-tenant-edit-list-base-info-form',
                    columns : 1,
                    fields : coreTenant.getBaseInfoFormFields(),
                    onUpdated : function () {
                        var grid = Gikam.getComp('core-tenant-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            } ]
        }
    },

    getPermissionGrid : function () {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.ITEM.TENANTS.TAB.PERMISSION',
                items : [ {
                    type : 'grid',
                    id : 'core-tenant-edit-list-permission-grid',
                    service : 'coreTenantPermissionServiceImpl',
                    dbTable : 'T_CORE_TENANT_PERMISSION',
                    deleteFormatter : function (row) {
                        return row.ext$.username || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function () {
                            var activeRow = Gikam.getComp('core-tenant-edit-list-grid').getActivedRow();
                            if (Gikam.isEmpty(activeRow)) {
                                Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                return;
                            }

                            Gikam.create('modal', {
                                title : Gikam.choose.getConfig('user-simple').title,
                                url : IFM_CONTEXT + Gikam.choose.getConfig('user-simple').url + Gikam.param({
                                    target_unassigned : '1',
                                    targetTable : 'T_CORE_TENANT_PERMISSION',
                                    targetUserIdColumn : 'USERID',
                                    targetFilterColumn : 'TENANTID',
                                    targetFilterValue : activeRow.id,
                                }),
                                width : Gikam.choose.getConfig('user-simple').width,
                                height : 650,
                                onAfterClose : function (rows) {
                                    if (Gikam.isEmpty(rows)) {
                                        return;
                                    }

                                    var users = rows.map(function (row) {
                                        return {
                                            userId : row.id
                                        };
                                    });

                                    Gikam.getComp('core-tenant-edit-list-permission-grid').insert(Gikam.printf(coreTenant.baseUrl + '/{id}/permissions', {
                                        id : activeRow.id
                                    }), users);
                                }
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function () {
                            Gikam.getComp('core-tenant-edit-list-permission-grid').deleteRows(Gikam.printf(coreTenant.baseUrl + '/{id}/permissions', {
                                id : Gikam.getComp('core-tenant-edit-list-grid').getActivedRow().id
                            }));
                        }
                    } ],
                    columns : coreTenant.getPermissionListColumns()
                } ]
            } ]
        };
    },

    create : function () {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-tenant-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 350,
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    north : {
                        height : 137,
                        items : [ this.getBaseInfoTab() ]
                    },
                    center : {
                        items : [ this.getPermissionGrid() ]
                    }
                } ]
            }
        });
    },

    init : function () {
        this.create();
    }
};
