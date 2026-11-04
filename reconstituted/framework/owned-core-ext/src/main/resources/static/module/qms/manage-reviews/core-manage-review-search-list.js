coreManageReview.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-manage-review-search-list-grid',
            url : coreManageReview.baseUrl + '/queries/searchable',
            columns : coreManageReview.getSearchListColumns(),
            onCellClick : function(field, row) {
                field === 'reviewNo' && _this.loadDetailPage(row.id);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-manage-review-search-list-review-improve-grid' ]);
                    Gikam.getComp('qms-manage-review-search-list-uploader').grid.cleanData();
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('qms-manage-review-search-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
                Gikam.getComp('core-manage-review-search-list-review-improve-grid').refresh({
                    url : Gikam.printf(coreManageReview.baseUrl + '/{id}/review-improves/queries', {
                        id : row.id
                    })
                });
            }
        }
    },

    getSouthTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ _this.getAttachment(), _this.getReviewImproveGrid() ]
        }
    },

    getReviewImproveGrid : function() {
        return {
            title : 'MODULE.QMS.MANAGEREVIEWS.TAB.REVIEW_IMPROVE',
            items : [ {
                type : 'grid',
                id : 'core-manage-review-search-list-review-improve-grid',
                service : 'coreManageReviewImproveServiceImpl',
                autoSave : false,
                url : Gikam.printf(coreManageReview.baseUrl + '/{id}/review-improves/queries', {
                    id : 0
                }),
                deleteFormatter : function(row) {
                    return row.id;
                },
                columns : coreManageReview.getSearchImproveListColumns()
            } ]
        };
    },
    
    getAttachment : function() {
        return {
            title : 'GIKAM.FILE.ATTACHMENT',
            items : [ {
                type : 'uploader',
                id : 'qms-manage-review-search-list-uploader',
                dbTable : 'T_CORE_ANNUAL_PLAN',
                bizId : 0,
                onBeforeDelete : function() {
                    var rows = this.grid.data;
                    var canDelete = true;
                    Gikam.each(rows, function(index, row) {
                        if (workspace.user.id != row.createdById) {
                            canDelete = false;
                            Gikam.alert('QMS.UPLOADER.ALERT.CANNOT_DELETE_FILE_UPLOADED_BY_OTHERS');
                            return;
                        }
                    })
                    return canDelete;
               }
            } ]
        }
    },
    
    create : function() {
        Gikam.create('layout', {
            id : 'core-manage-review-search-list-layout',
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

    loadDetailPage : function(id) {
        workspace.window.load(coreManageReview.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            sourcePage : 'search'
        }));
    },

    init : function() {
        this.create();
    }
};
