coreFileManage.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-file-manage-search-list-grid',
            url : coreFileManage.baseUrl + '/queries/searchable',
            columns : coreFileManage.getSearchListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-file-manage-search-list-manage-version-grid' ]);
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-file-manage-search-list-manage-version-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreFileManage.baseUrl + '/{id}/manage-versions/queries', {
                            id : row.id
                        }),
                        requestData : {
                            processStatus_SPECIAL : 'approve_or_done'
                        }
                    });
                });
            },
        }
    },
    
    getManageVersionGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-file-manage-search-list-manage-version-grid',
            service : 'coreFileManageVersionServiceImpl',
            columns : coreFileManage.getVersionSearchListColumns(),
            onCellClick : function(field, row) {
                field === 'versionNo' && _this.loadDetailPage(row.id);
            }
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
            id : 'core-file-manage-search-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '50%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getManageVersionGrid() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
