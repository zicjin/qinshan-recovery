coreAdminTenant.editPage = {

    getGridParam : function () {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-admin-tenant-edit-list-grid',
            url : coreAdminTenant.baseUrl + '/queries/raw',
            service : 'coreAdminTenantServiceImpl',
            dbTable : 'T_CORE_ADMIN_TENANT',
            columns : coreAdminTenant.getEditListColumns(),
            deleteFormatter : function (row) {
                return row.tenantName || row.id;
            },
            onLoadSuccess : function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-admin-tenant-edit-list-base-info-form', 'core-admin-tenant-edit-list-user-grid' ]);
                }
            },
            onRowActive : function (index, row) {
                Gikam.getAsyncComp('core-admin-tenant-edit-list-base-info-form').done(function (form) {
                    form.refresh({
                        url : Gikam.printf(coreAdminTenant.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });

                Gikam.getAsyncComp('core-admin-tenant-edit-list-user-grid').done(function (grid) {
                    grid.refresh({
                        url : Gikam.printf(coreAdminTenant.baseUrl + '/{id}/users/queries', {
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
                        modalTitle : 'CORE.MODULE.ADMIN.TENANTS.MODAL.ADD',
                        fields : coreAdminTenant.getBaseInfoFormFields(),
                        url : coreAdminTenant.baseUrl,
                        width : 450,
                        titleWidth : 120
                    }).done(function (id) {
                        Gikam.getComp('core-admin-tenant-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function () {
                    Gikam.getComp('core-admin-tenant-edit-list-grid').deleteRows(coreAdminTenant.baseUrl);
                }
            } ]
        }
    },

    getBaseInfoTab : function () {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.ADMIN.TENANTS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    service : 'coreAdminTenantServiceImpl',
                    dbTable : 'T_CORE_ADMIN_TENANT',
                    id : 'core-admin-tenant-edit-list-base-info-form',
                    columns : 3,
                    fields : coreAdminTenant.getBaseInfoFormFields(),
                    onUpdated : function () {
                        var grid = Gikam.getComp('core-admin-tenant-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            } ]
        }
    },

    getUserGrid : function () {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.ADMIN.TENANTS.TAB.USER',
                items : [ {
                    type : 'grid',
                    id : 'core-admin-tenant-edit-list-user-grid',
                    service : 'coreAdminTenantUserServiceImpl',
                    dbTable : 'T_CORE_ADMIN_TENANT_USER',
                    deleteFormatter : function (row) {
                        return row.ext$.username || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function () {
                            var activeRow = Gikam.getComp('core-admin-tenant-edit-list-grid').getActivedRow();
                            if (Gikam.isEmpty(activeRow)) {
                                Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                return;
                            }

                            Gikam.create('modal', {
                                title : Gikam.choose.getConfig('user-simple').title,
                                url : IFM_CONTEXT + Gikam.choose.getConfig('user-simple').url + Gikam.param({
                                    target_unassigned : '1',
                                    targetTable : 'T_CORE_ADMIN_TENANT_USER',
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

                                    Gikam.getComp('core-admin-tenant-edit-list-user-grid').insert(Gikam.printf(coreAdminTenant.baseUrl + '/{id}/users', {
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
                            Gikam.getComp('core-admin-tenant-edit-list-user-grid').deleteRows(Gikam.printf(coreAdminTenant.baseUrl + '/{id}/users', {
                                id : Gikam.getComp('core-admin-tenant-edit-list-grid').getActivedRow().id
                            }));
                        }
                    } ],
                    columns : coreAdminTenant.getUserListColumns()
                } ]
            } ]
        };
    },

    create : function () {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-admin-tenant-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 350,
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    north : {
                        height : 137,
                        items : [ _this.getBaseInfoTab() ]
                    },
                    center : {
                        items : [ _this.getUserGrid() ]
                    }
                } ]
            }
        });
    },

    init : function () {
        this.create();
    }
};
