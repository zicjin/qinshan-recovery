coreFileManage.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-file-manage-audit-list-grid',
            url : coreFileManage.baseUrl + '/queries/tasks',
            requestData : {
                bpmn_statusCode : _this.param.bpmn_statusCode
            },
            columns : coreFileManage.getSearchListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-file-manage-audit-list-manage-version-grid', 'core-file-manage-audit-list-manage-auth-grid' ]);
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-file-manage-audit-list-manage-version-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreFileManage.baseUrl + '/{id}/manage-versions/queries', {
                            id : row.id
                        }),
                        requestData : {
                            processStatus_SB : 'approve'
                        }
                    });
                });
                Gikam.getAsyncComp('core-file-manage-audit-list-manage-auth-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreFileManage.baseUrl + '/{id}/manage-auths/queries', {
                            id : row.id
                        })
                    });
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                icon : 'pass',
                onClick : function() {
                    _this.pass();
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                icon : 'reject',
                onClick : function() {
                    _this.reject();
                }
            } ]
        }
    },

    pass : function() {
        var grid = Gikam.getComp('core-file-manage-audit-list-manage-version-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : coreFileManage
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('core-file-manage-audit-list-manage-version-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : coreFileManage
        }).done(function() {
            grid.refresh();
        });
    },
    
    getSouthTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ _this.getManageVersionGrid(), _this.getManageAuthGrid() ]
        }
    },

    getManageVersionGrid : function() {
        var _this = this;
        return {
            title : 'CORE.MODULE.QMS.FILEMANAGES.TAB.MANAGE_VERSION',
            items : [ {
                type : 'grid',
                id : 'core-file-manage-audit-list-manage-version-grid',
                service : 'coreFileManageVersionServiceImpl',
                columns : coreFileManage.getVersionAuditListColumns(),
                onCellClick : function(field, row) {
                    field === 'versionNo' && _this.loadDetailPage(row.id);
                }
            } ]
        };
    },
    
    getManageAuthGrid : function() {
        var _this = this;
        return {
            title : 'CORE.MODULE.QMS.FILEMANAGES.TAB.MANAGE_AUTH',
            items : [ {
                type : 'grid',
                id : 'core-file-manage-audit-list-manage-auth-grid',
                service : 'coreFileManageAuthServiceImpl',
                columns : coreFileManage.getAuthSearchListColumns()
            } ]
        };
    },
    
    loadDetailPage : function(id) {
        workspace.window.load(coreFileManage.baseUrl + '/page/file-versions/detail' + Gikam.param({
            id : id,
            readonly : true
        }));
    },

    create : function() {
        Gikam.create('layout', {
            id : 'core-file-manage-audit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '50%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getSouthTab() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
