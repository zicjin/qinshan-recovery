coreNotice.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-notice-edit-list-grid',
            url : coreNotice.baseUrl + '/queries/raw',
            columns : coreNotice.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.title || row.id;
            },
            onLoadSuccess : function(data) {
                if (Gikam.isNotEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ "core-notice-edit-list-base-info-form", "core-notice-edit-list-user-grid", "core-notice-edit-list-role-grid", "core-notice-edit-list-org-grid" ]);
                    Gikam.getComp('core-notice-edit-list-uploader').grid.cleanData();
                }
            },
            onRowActive : function(index, row) {
                _this.onRowActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.ITEM.NOTICES.MODAL.ADD',
                        fields : coreNotice.getBaseInfoFormFields(),
                        url : coreNotice.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('core-notice-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('core-notice-edit-list-grid').deleteRows(coreNotice.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {

                    var selectedRows = Gikam.getComp('core-notice-edit-list-grid').getSelections();

                    if (selectedRows.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    Gikam.viewAudit('T_CORE_NOTICE', selectedRows[0].id);

                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'core-notice-edit-list-tab',
            fill : true,
            panels : [ {
                title : 'CORE.MODULE.ITEM.NOTICES.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-notice-edit-list-base-info-form',
                    service : 'coreNoticeServiceImpl',
                    columns : 3,
                    fields : coreNotice.getBaseInfoFormFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('core-notice-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            }, {
                title : 'CORE.MODULE.ITEM.NOTICES.TAB.USER',
                id : 'core-notice-edit-list-user-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-notice-edit-list-user-grid',
                    columns : coreNotice.getUserColumns(),
                    deleteFormatter : function(row) {
                        return row.targetName || row.targetId || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertUser();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            Gikam.getComp('core-notice-edit-list-user-grid').deleteRows(coreNotice.baseUrl + '/permissions');
                        }
                    } ],
                } ]
            }, {
                title : 'CORE.MODULE.ITEM.NOTICES.TAB.ROLE',
                id : 'core-notice-edit-list-role-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-notice-edit-list-role-grid',
                    columns : coreNotice.getRoleColumns(),
                    deleteFormatter : function(row) {
                        return row.targetName || row.targetId || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertRole();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            var selections = Gikam.getComp('core-notice-edit-list-role-grid').deleteRows(coreNotice.baseUrl + '/permissions');
                        }
                    } ],
                } ]
            }, {
                title : 'CORE.MODULE.ITEM.NOTICES.TAB.ORG',
                id : 'core-notice-edit-list-org-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-notice-edit-list-org-grid',
                    columns : coreNotice.getOrgColumns(),
                    deleteFormatter : function(row) {
                        return row.targetName || row.targetId || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertOrg();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            Gikam.getComp('core-notice-edit-list-org-grid').deleteRows(coreNotice.baseUrl + '/permissions');
                        }
                    } ],
                } ]
            }, {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'core-notice-edit-list-uploader',
                    dbTable : 'T_CORE_NOTICE',
                } ]
            } ]
        }
    },

    onRowActive : function(row) {
        var _this = this;

        Gikam.getAsyncComp('core-notice-edit-list-base-info-form').done(function(form) {
            form.refresh({
                url : _this.getBaseInfoUrl(row.id),
            });
        });

        if (row.noticeCategory != 'whole') {
            Gikam.getComp('core-notice-edit-list-tab').revertPanel('core-notice-edit-list-user-panel');
            Gikam.getComp('core-notice-edit-list-tab').revertPanel('core-notice-edit-list-role-panel');
            Gikam.getComp('core-notice-edit-list-tab').revertPanel('core-notice-edit-list-org-panel');
        } else {
            Gikam.getComp('core-notice-edit-list-tab').removePanel('core-notice-edit-list-user-panel');
            Gikam.getComp('core-notice-edit-list-tab').removePanel('core-notice-edit-list-role-panel');
            Gikam.getComp('core-notice-edit-list-tab').removePanel('core-notice-edit-list-org-panel');
        }

        Gikam.getAsyncComp('core-notice-edit-list-uploader').done(function(grid) {
            grid.setOptions({
                bizId : row.id
            });
        });

        Gikam.getAsyncComp('core-notice-edit-list-user-grid').done(function(grid) {
            grid.refresh({
                url : _this.getPermissionUrl(row.id, "T_CORE_USER"),
            });
        });

        Gikam.getAsyncComp('core-notice-edit-list-role-grid').done(function(grid) {
            grid.refresh({
                url : _this.getPermissionUrl(row.id, "T_CORE_ROLE"),
            });
        });

        Gikam.getAsyncComp('core-notice-edit-list-org-grid').done(function(grid) {
            grid.refresh({
                url : _this.getPermissionUrl(row.id, "T_CORE_ORG"),
            });
        });
    },

    insertUser : function() {
        var _this = this;
        var activeRow = Gikam.getComp('core-notice-edit-list-grid').getActivedRow();
        if (Gikam.isEmpty(activeRow)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            width : 350,
            height : 450,
            title : Gikam.choose.getConfig('user-simple').title,
            url : IFM_CONTEXT + Gikam.choose.getConfig('user-simple').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_CORE_NOTICE_PERMISSION',
                targetUserIdColumn : 'TARGETID',
                targetFilterColumn : 'PERMISSIONTYPE',
                targetFilterValue : 'T_CORE_USER',
                targetFilterColumn1 : 'NOTICEID',
                targetFilterValue1 : activeRow.id
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        targetId : item.id,
                        targetName : item.userName,
                        permissionType : 'T_CORE_USER',
                    }
                });
                Gikam.getComp('core-notice-edit-list-user-grid').insert(Gikam.printf(coreNotice.baseUrl + '/{id}/permissions', {
                    id : activeRow.id
                }), chooseData);
            },
        });
    },

    insertRole : function() {
        var _this = this;
        var activeRow = Gikam.getComp('core-notice-edit-list-grid').getActivedRow();
        if (Gikam.isEmpty(activeRow)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            width : 350,
            height : 450,
            title : Gikam.choose.getConfig('role').title,
            url : IFM_CONTEXT + Gikam.choose.getConfig('role').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_CORE_NOTICE_PERMISSION',
                targetRoleIdColumn : 'TARGETID',
                targetFilterColumn : 'PERMISSIONTYPE',
                targetFilterValue : 'T_CORE_ROLE',
                targetFilterColumn1 : 'NOTICEID',
                targetFilterValue1 : activeRow.id,
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        targetId : item.id,
                        targetName : item.roleName,
                        permissionType : 'T_CORE_ROLE',
                    }
                });
                Gikam.getComp('core-notice-edit-list-role-grid').insert(Gikam.printf(coreNotice.baseUrl + '/{id}/permissions', {
                    id : activeRow.id
                }), chooseData);
            },
        });
    },

    insertOrg : function() {
        var _this = this;
        var activeRow = Gikam.getComp('core-notice-edit-list-grid').getActivedRow();
        if (!activeRow) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            width : 1000,
            title : Gikam.choose.getConfig('org-simple').title,
            url : IFM_CONTEXT + Gikam.choose.getConfig('org-simple').url + Gikam.param({
                target_filter : encodeURI(JSON.stringify({
                    type : 'unassigned',
                    targetTable : 'T_CORE_NOTICE_PERMISSION',
                    targetMatchColumn : 'TARGETID',
                    thisMatchColumn : 'ID',
                    filter : [ {
                        targetFilterColumn : 'PERMISSIONTYPE',
                        targetFilterValue : 'T_CORE_ORG',
                    }, {
                        targetFilterColumn : 'NOTICEID',
                        targetFilterValue : activeRow.id,
                    } ]
                }))
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        targetId : item.id,
                        targetName : item.orgName,
                        permissionType : 'T_CORE_ORG',
                    }
                });

                Gikam.getComp('core-notice-edit-list-org-grid').insert(Gikam.printf(coreNotice.baseUrl + '/{id}/permissions', {
                    id : activeRow.id
                }), chooseData);

            },
        });
    },

    getPermissionUrl : function(id, permissionType) {
        if (id) {
            return Gikam.printf(coreNotice.baseUrl + '/{id}/permission-types/{permissionType}/permissions/queries', {
                id : id,
                permissionType : permissionType
            });
        }
    },

    getBaseInfoUrl : function(id) {
        if (id) {
            return Gikam.printf(coreNotice.baseUrl + '/{id}', {
                id : id
            })
        }
    },

    submit : function() {
        var grid = Gikam.getComp('core-notice-edit-list-grid');
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : coreNotice
        }).done(function() {
            grid.refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            west : {
                width : 370,
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getBaseInfoTab() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
