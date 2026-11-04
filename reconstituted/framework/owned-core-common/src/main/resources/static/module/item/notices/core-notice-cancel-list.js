coreNotice.cancelPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-notice-cancel-list-grid',
            url : coreNotice.baseUrl + '/queries/undoable',
            requestData : {
                status_SEQ : 'normal',
            },
            columns : coreNotice.getCancelListColumns(),
            onLoadSuccess : function(data) {
                if (Gikam.isNotEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ "core-notice-cancel-list-base-info-form", "core-notice-cancel-list-user-grid", "core-notice-cancel-list-role-grid", "core-notice-cancel-list-org-grid" ]);
                    Gikam.getComp('core-notice-cancel-list-uploader').grid.cleanData();
                }
            },
            onRowActive : function(index, row) {
                if (row) {

                    Gikam.getAsyncComp('core-notice-cancel-list-base-info-form').done(function(form) {
                        form.refresh({
                            url : _this.getBaseInfoUrl(row.id),
                        });
                    });

                    if (row.noticeCategory == 'whole') {
                        Gikam.getComp('core-notice-cancel-list-tab').removePanel('core-notice-cancel-list-user-panel');
                        Gikam.getComp('core-notice-cancel-list-tab').removePanel('core-notice-cancel-list-role-panel');
                        Gikam.getComp('core-notice-cancel-list-tab').removePanel('core-notice-cancel-list-org-panel');
                    } else {
                        Gikam.getComp('core-notice-cancel-list-tab').revertPanel('core-notice-cancel-list-user-panel');
                        Gikam.getComp('core-notice-cancel-list-tab').revertPanel('core-notice-cancel-list-role-panel');
                        Gikam.getComp('core-notice-cancel-list-tab').revertPanel('core-notice-cancel-list-org-panel');
                    }

                    Gikam.getAsyncComp('core-notice-cancel-list-uploader').done(function(grid) {
                        grid.setOptions({
                            bizId : row.id
                        });
                    });

                    Gikam.getAsyncComp('core-notice-cancel-list-user-grid').done(function(grid) {
                        grid.refresh({
                            url : _this.getPermissionUrl(row.id, "T_CORE_USER"),
                        });
                    });

                    Gikam.getAsyncComp('core-notice-cancel-list-role-grid').done(function(grid) {
                        grid.refresh({
                            url : _this.getPermissionUrl(row.id, "T_CORE_ROLE"),
                        });
                    });

                    Gikam.getAsyncComp('core-notice-cancel-list-org-grid').done(function(grid) {
                        grid.refresh({
                            url : _this.getPermissionUrl(row.id, "T_CORE_ORG"),
                        });
                    });
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.ITEM.NOTICES.BUTTON.CANCEL',
                icon : 'reject',
                onClick : function() {

                    Gikam.getComp('core-notice-cancel-list-grid').processRows({
                        url : coreNotice.baseUrl + '/action/cancel',
                        confirm : {
                            msg : 'CORE.MODULE.ITEM.NOTICES.TIP.CONFIRM_TO_CANCEL_FOLLOWING_ITEMS',
                            title : 'CORE.MODULE.ITEM.NOTICES.TIP.CONFIRM_CANCEL',
                        },
                        formatter : function(row) {
                            return row.title || row.id;
                        },
                        method : 'put'
                    });
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'core-notice-cancel-list-tab',
            fill : true,
            panels : [ {
                title : 'CORE.MODULE.ITEM.NOTICES.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-notice-cancel-list-base-info-form',
                    readonly : true,
                    columns : 3,
                    fields : coreNotice.getBaseInfoFormFields(),
                } ]
            }, {
                title : 'CORE.MODULE.ITEM.NOTICES.TAB.USER',
                id : 'core-notice-cancel-list-user-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-notice-cancel-list-user-grid',
                    columns : coreNotice.getUserColumns(),
                } ]
            }, {
                title : 'CORE.MODULE.ITEM.NOTICES.TAB.ROLE',
                id : 'core-notice-cancel-list-role-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-notice-cancel-list-role-grid',
                    columns : coreNotice.getRoleColumns(),
                } ]
            }, {
                title : 'CORE.MODULE.ITEM.NOTICES.TAB.ORG',
                id : 'core-notice-cancel-list-org-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-notice-cancel-list-org-grid',
                    columns : coreNotice.getOrgColumns(),
                } ]
            }, {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'core-notice-cancel-list-uploader',
                    dbTable : 'T_CORE_NOTICE',
                    readonly : true
                } ]
            } ]
        }
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

    create : function() {
        var _this = this;                                   
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            west : {
                width : 300,
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getBaseInfoTab() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
