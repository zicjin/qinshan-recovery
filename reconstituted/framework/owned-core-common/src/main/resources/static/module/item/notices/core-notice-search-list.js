coreNotice.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-notice-search-list-grid',
            url : coreNotice.baseUrl + '/queries/searchable',
            columns : coreNotice.getSearchListColumns(),
            onLoadSuccess : function(data) {
                if (Gikam.isNotEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ "core-notice-search-list-base-info-form", "core-notice-search-list-user-grid", "core-notice-search-list-role-grid", "core-notice-search-list-org-grid" ]);
                    Gikam.getComp('core-notice-search-list-uploader').grid.cleanData();
                }
            },
            onRowActive : function(index, row) {
                if (row) {
                    Gikam.getAsyncComp('core-notice-search-list-base-info-form').done(function(form) {
                        form.refresh({
                            url : _this.getBaseInfoUrl(row.id),
                        });
                    });

                    if (row.noticeCategory == 'whole') {
                        Gikam.getComp('core-notice-search-list-tab').removePanel('core-notice-search-list-user-panel');
                        Gikam.getComp('core-notice-search-list-tab').removePanel('core-notice-search-list-role-panel');
                        Gikam.getComp('core-notice-search-list-tab').removePanel('core-notice-search-list-org-panel');
                    } else {
                        Gikam.getComp('core-notice-search-list-tab').revertPanel('core-notice-search-list-user-panel');
                        Gikam.getComp('core-notice-search-list-tab').revertPanel('core-notice-search-list-role-panel');
                        Gikam.getComp('core-notice-search-list-tab').revertPanel('core-notice-search-list-org-panel');
                    }

                    Gikam.getAsyncComp('core-notice-search-list-uploader').done(function(grid) {
                        grid.setOptions({
                            bizId : row.id
                        });
                    });

                    Gikam.getAsyncComp('core-notice-search-list-user-grid').done(function(grid) {
                        grid.refresh({
                            url : _this.getPermissionUrl(row.id, "T_CORE_USER"),
                        });
                    });

                    Gikam.getAsyncComp('core-notice-search-list-role-grid').done(function(grid) {
                        grid.refresh({
                            url : _this.getPermissionUrl(row.id, "T_CORE_ROLE"),
                        });
                    });

                    Gikam.getAsyncComp('core-notice-search-list-org-grid').done(function(grid) {
                        grid.refresh({
                            url : _this.getPermissionUrl(row.id, "T_CORE_ORG"),
                        });
                    });
                }
            },
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'core-notice-search-list-tab',
            fill : true,
            panels : [ {
                title : 'CORE.MODULE.ITEM.NOTICES.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-notice-search-list-base-info-form',
                    readonly : true,
                    columns : 3,
                    fields : coreNotice.getBaseInfoFormFields(),
                } ]
            }, {
                title : 'CORE.MODULE.ITEM.NOTICES.TAB.USER',
                id : 'core-notice-search-list-user-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-notice-search-list-user-grid',
                    columns : coreNotice.getUserColumns(),
                } ]
            }, {
                title : 'CORE.MODULE.ITEM.NOTICES.TAB.ROLE',
                id : 'core-notice-search-list-role-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-notice-search-list-role-grid',
                    columns : coreNotice.getRoleColumns(),
                } ]
            }, {
                title : 'CORE.MODULE.ITEM.NOTICES.TAB.ORG',
                id : 'core-notice-search-list-org-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-notice-search-list-org-grid',
                    columns : coreNotice.getOrgColumns(),
                } ]
            }, {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'core-notice-search-list-uploader',
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
                width : 360,
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
