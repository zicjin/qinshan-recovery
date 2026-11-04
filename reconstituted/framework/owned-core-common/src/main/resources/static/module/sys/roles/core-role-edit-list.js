//@ sourceURL=core-role-edit-list.js
coreRole.editPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-role-edit-list-grid',
            url : coreRole.baseUrl + '/queries/raw',
            columns : coreRole.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.roleName || row.id;
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('core-role-edit-list-base-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(coreRole.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });

                Gikam.getAsyncComp('core-role-edit-list-user-list-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreRole.baseUrl + '/{id}/role-users/queries', {
                            id : row.id
                        })
                    });
                });

                Gikam.each(_this.typeArray, function(index, value) {
                    Gikam.getAsyncComp('core-role-edit-list-' + value.id + '-permission-list-grid').done(function(grid) {

                        var isAdminUrl = coreRole.baseUrl + "/{id}/role-permission-types/{permissionTypeId}/action/validate-admin";

                        Gikam.getText(Gikam.printf(isAdminUrl, {
                            id : row.id,
                            permissionTypeId : value.id
                        })).done(function(data) {
                            _this.setPermissionComp(value, Gikam.isTrue(data));
                        })
                    });
                });

            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {

                    if (_this.selectRowId) {
                        this.activeRowByIndex(data.length - 1);
                    } else {
                        this.activeRowByIndex(0);
                    }
                } else {
                    Gikam.cleanCompData([ 'core-role-edit-list-base-info-form', 'core-role-edit-list-user-list-grid' ]);
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.SYS.ROLES.TITLE.ADD_ROLE',
                        fields : coreRole.getBaseInfoFormFields(),
                        url : coreRole.baseUrl,
                    }).done(function(id) {
                        _this.selectRowId = parseInt(id);
                        Gikam.getComp('core-role-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    var selectedRows = Gikam.getComp('core-role-edit-list-grid').getSelections();
                    if (Gikam.isEmpty(selectedRows)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    var validUrl = coreRole.baseUrl + '/action/delete-validate';
                    Gikam.post(validUrl, Gikam.getJsonWrapper(null, [ '', selectedRows ])).done(function(result) {
                        if (!result.valid) {
                            Gikam.confirm('GIKAM.TIP.CONFIRM_TO_DELETE', result.message, function() {
                                workspace.window.showMask(true);
                                Gikam.del(coreRole.baseUrl, Gikam.getJsonWrapper({}, [ '', selectedRows ])).done(function() {
                                    Gikam.getComp('core-role-edit-list-grid').refresh();
                                    _this.selectRowId = null;
                                }).always(function() {
                                    workspace.window.closeMask();
                                });
                            });
                        } else {
                            Gikam.getComp('core-role-edit-list-grid').deleteRows(coreRole.baseUrl);
                            _this.selectRowId = null;
                        }
                    });
                }
            }, {
                 type : 'button',
                 text : 'CORE.MODULE.MDM.ORGS.BUTTON.MOVE_UP',
                 icon : 'move-up',
                 onClick : function() {
                     _this.moveUp();
                 }
             }, {
                 type : 'button',
                 text : 'CORE.MODULE.MDM.ORGS.BUTTON.MOVE_DOWN',
                 icon : 'move-down',
                 onClick : function() {
                     _this.moveDown();
                 }
             }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {

                    var selectedRows = Gikam.getComp('core-role-edit-list-grid').getSelections();

                    if (selectedRows.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    Gikam.viewAudit('T_CORE_ROLE', selectedRows[0].id);
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
                    service : 'coreRoleServiceImpl',
                    fields : coreRole.getBaseInfoFormFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('core-role-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            } ]
        }
    },

    getPermissionGridParam : function() {
        var _this = this, panels = [];

        Gikam.each(this.typeArray, function(index, value) {
            panels.push({
                title : value.typeName,
                id : 'core-role-edit-list-' + value.id + '-tab-panel',
                items : [ {
                    type : value.compCategory,
                    id : 'core-role-edit-list-' + value.id + '-permission-list-grid',
                    onBeforeDelete : function(jsonArray) {
                        return jsonArray.map(function(item) {
                            return {
                                targetId : item.id
                            }
                        });
                    },
                    page : (value.compCategory === 'grid') ? true : false,
                    deleteFormatter : function(row) {
                        return row.menuName || row.schemeName || row.id;
                    },
                    onRendered : function() {
                        this.hideBody().appendContent('<span id="admin-span-' + value.id + '" class="red" style="display:none">' + Gikam.propI18N(value.typeName) + Gikam.propI18N('CORE.MODULE.SYS.ROLES.TITLE.ADMIN') + '</span>');
                    },
                    toolbar : [ {
                        type : 'button',
                        id : 'add-' + value.id + '-btn',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        rendered : function() {
                            this.hide();
                        },
                        onClick : function() {
                            _this.insertPermissionRow(value);
                        }
                    }, {
                        type : 'button',
                        id : 'remove-' + value.id + '-btn',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        rendered : function() {
                            this.hide();
                        },
                        onClick : function() {
                            _this.deletePermissionRow(value);
                        }
                    }, {
                        type : 'button',
                        id : 'set-admin-' + value.id + '-btn',
                        text : 'CORE.MODULE.SYS.ROLES.BUTTON.SET_ADMINISTRATOR',
                        icon : 'set-admin',
                        rendered : function() {
                            this.hide();
                        },
                        onClick : function() {
                            _this.setPermissionTypeAdmin(value);
                        }
                    }, {
                        type : 'button',
                        id : 'unset-admin-' + value.id + '-btn',
                        text : 'CORE.MODULE.SYS.ROLES.BUTTON.CANCEL_ADMINISTRATOR',
                        icon : 'unset-admin',
                        rendered : function() {
                            this.hide();
                        },
                        onClick : function() {
                            _this.unsetPermissionTypeAdmin(value);
                        }
                    } ],
                    // grid需要checkbox, treeGrid不需要checkbox
                    columns : ((value.compCategory === 'grid') ? [ {
                        checkbox : true
                    } ] : []).concat(eval('[' + value.gridColumnSettings + ']'))
                } ]
            });
        });

        return {
            type : 'tab',
            id : 'core-role-edit-list-permission-tab',
            panels : panels
        }
    },

    getRoleUserGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : "CORE.MODULE.SYS.ROLES.TAB.OWNED_USERS",
                items : [ {
                    type : 'grid',
                    id : 'core-role-edit-list-user-list-grid',
                    service : 'coreRoleUserServiceImpl',
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            _this.insertRoleUserRow();

                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteRoleUserRow();
                        }
                    } ],
                    columns : coreRole.getUserListColumns(),
                    genericQuery : false,
                    deleteFormatter : function(row) {
                        return row.ext$.username || row.userId || row.id;
                    },
                } ]
            } ]
        }
    },

    setPermissionComp : function(permissionType, admin) {
        var _this = this, permissionTypeId = permissionType.id, activeRow = Gikam.getComp('core-role-edit-list-grid').getActivedRow();
        if (admin) {
            Gikam.jQuery('#admin-span-' + permissionTypeId).show();
            Gikam.getComp('core-role-edit-list-' + permissionTypeId + '-permission-list-grid').hideBody();
            Gikam.getComp('add-' + permissionTypeId + '-btn').hide();
            Gikam.getComp('remove-' + permissionTypeId + '-btn').hide();
            Gikam.getComp('set-admin-' + permissionTypeId + '-btn').hide();
            Gikam.getComp('unset-admin-' + permissionTypeId + '-btn').show();
        } else {
            Gikam.jQuery('#admin-span-' + permissionTypeId).hide();
            Gikam.getComp('core-role-edit-list-' + permissionTypeId + '-permission-list-grid').showBody();
            Gikam.getComp('add-' + permissionTypeId + '-btn').show();
            Gikam.getComp('remove-' + permissionTypeId + '-btn').show();
            Gikam.getComp('set-admin-' + permissionTypeId + '-btn').show();
            Gikam.getComp('unset-admin-' + permissionTypeId + '-btn').hide();
        }
        Gikam.getAsyncComp('core-role-edit-list-' + permissionTypeId + '-permission-list-grid').done(function(grid) {
            var maskPromise = Gikam.showMask();
            grid.refresh({
                url : IFM_CONTEXT + permissionType.gridPrimaryUrl + (permissionType.compCategory === 'treeGrid' ? '/tree-grid' : '/queries'),
                requestData : {
                    'role_assigned' : activeRow.id
                }
            }).done(function() {
                maskPromise.then(function(mask) {
                    Gikam.closeMask(mask);
                })
            });
        });
    },

    insertPermissionRow : function(permissionType) {
        var roleData = Gikam.getComp('core-role-edit-list-base-info-form').getData();

        if (!roleData.id) {
            return;
        }
        if (!permissionType.gridPrimaryUrl) {
            return;
        }
        var title;
        if ("T_CORE_ORG" == permissionType.id){
            title = Gikam.propI18N(permissionType.typeName) + Gikam.propI18N('CORE.MODULE.SYS.ROLES.MODAL.DEPT');
        } else if ("T_CORE_WK_SCHEME" == permissionType.id){
            title = Gikam.propI18N(permissionType.typeName) + Gikam.propI18N('CORE.MODULE.SYS.ROLES.MODAL.PLAN');
        } else if ("T_CORE_MENU" == permissionType.id){
            title = Gikam.propI18N(permissionType.typeName) + Gikam.propI18N('CORE.MODULE.SYS.ROLES.MODAL.MENU');
        }
        Gikam.create('modal', {
            title : title,
            url : IFM_CONTEXT + permissionType.gridPrimaryUrl + '/page/choose-list?role_unassigned=' + roleData.id,
            onAfterClose : function(rows) {
                if (rows) {
                    var chooseData = rows.map(function(item) {
                        return {
                            targetId : item.id
                        }
                    });

                    var rolePermissionUrl = Gikam.printf(coreRole.baseUrl + '/{id}/role-permission-types/{permissionTypeId}/role-permissions', {
                        id : roleData.id,
                        permissionTypeId : permissionType.id
                    });

                    Gikam.getComp('core-role-edit-list-' + permissionType.id + '-permission-list-grid').insert(rolePermissionUrl, chooseData);
                }
            }
        });
    },

    deletePermissionRow : function(permissionType) {
        var roleData = Gikam.getComp('core-role-edit-list-base-info-form').getData();

        if (!roleData.id) {
            return;
        }

        var rolePermissionUrl = Gikam.printf(coreRole.baseUrl + '/{id}/role-permission-types/{permissionTypeId}/role-permissions', {
            id : roleData.id,
            permissionTypeId : permissionType.id
        });

        var grid = Gikam.getComp('core-role-edit-list-' + permissionType.id + '-permission-list-grid');
        var data = permissionType.compCategory === 'treeGrid' ? grid.getCheckedNode() : null;

        grid.deleteRows(rolePermissionUrl, data);
    },

    insertRoleUserRow : function() {
        var roleData = Gikam.getComp('core-role-edit-list-base-info-form').getData();

        if (!roleData.id) {
            return;
        }

        Gikam.create('modal', {
            title : 'CORE.MODULE.SYS.ROLES.MODAL.USER_CHOOSE',
            url : IFM_CONTEXT + Gikam.choose.getConfig('mult-user').url + '?role_unassigned=' + roleData.id,
            onAfterClose : function(rows) {
                if (rows) {
                    var chooseData = rows.map(function(item) {
                        return {
                            userId : item.id
                        }
                    });

                    var roleUserUrl = Gikam.printf(coreRole.baseUrl + '/{id}/role-users', {
                        id : roleData.id
                    });

                    Gikam.getComp('core-role-edit-list-user-list-grid').insert(roleUserUrl, chooseData);
                }
            }
        });
    },

    deleteRoleUserRow : function() {
        var roleData = Gikam.getComp('core-role-edit-list-base-info-form').getData();

        if (!roleData.id) {
            return;
        }

        Gikam.getComp('core-role-edit-list-user-list-grid').deleteRows(coreRole.baseUrl + '/role-users');
    },

    setPermissionTypeAdmin : function(permissionType) {
        var _this = this;

        var roleData = Gikam.getComp('core-role-edit-list-base-info-form').getData();

        if (!roleData.id) {
            return;
        }

        var rolePermissionUrl = Gikam.printf(coreRole.baseUrl + '/{id}/role-permission-types/{permissionTypeId}/role-permissions', {
            id : roleData.id,
            permissionTypeId : permissionType.id
        });

        Gikam.postText(rolePermissionUrl, Gikam.getJsonWrapper(null, [ 'coreRolePermissionServiceImpl', [ {
            targetId : '0'
        } ] ])).done(function() {
            _this.setPermissionComp(permissionType, true);
        });
    },

    unsetPermissionTypeAdmin : function(permissionType) {
        var _this = this;

        var roleData = Gikam.getComp('core-role-edit-list-base-info-form').getData();

        if (!roleData.id) {
            return;
        }

        var rolePermissionUrl = Gikam.printf(coreRole.baseUrl + '/{id}/role-permission-types/{permissionTypeId}/role-permissions', {
            id : roleData.id,
            permissionTypeId : permissionType.id
        });

        Gikam.del(rolePermissionUrl, Gikam.getJsonWrapper(null, [ 'coreRolePermissionServiceImpl', [ {
            targetId : '0'
        } ] ])).done(function() {
            _this.setPermissionComp(permissionType, false);
        });
    },

    moveUp : function() {
        var selections = Gikam.getComp('core-role-edit-list-grid').getSelections();
        if (selections.length === 1) {
            Gikam.getComp('core-role-edit-list-grid').moveUp(coreRole.baseUrl + '/action/swap/{firstId}/{secondId}').done(function() {
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }

    },

    moveDown : function() {
        var selections = Gikam.getComp('core-role-edit-list-grid').getSelections();
        if (selections.length === 1) {
            Gikam.getComp('core-role-edit-list-grid').moveDown(coreRole.baseUrl + '/action/swap/{firstId}/{secondId}').done(function() {
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },



    create : function() {
        Gikam.create('layout', {
            id : 'core-role-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 311,
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getBaseInfoTab(), this.getPermissionGridParam(), this.getRoleUserGridParam() ]
            }
        });
    },

    init : function() {

        var _this = this;
        workspace.window.showMask();
        Gikam.getJson(IFM_CONTEXT + '/core/module/sys/role-permission-types/queries/selectable').done(function(data) {
            _this.typeArray = data;
            _this.create();
            workspace.window.closeMask();
        });
    }
};