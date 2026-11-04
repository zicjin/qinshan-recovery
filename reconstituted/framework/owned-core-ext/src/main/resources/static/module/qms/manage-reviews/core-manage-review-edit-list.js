coreManageReview.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-manage-review-edit-list-grid',
            url : coreManageReview.baseUrl + '/queries/raw',
            activateFirstRow : true,
            columns : coreManageReview.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.reviewNo;
            },
            onCellClick : function(field, row) {
                field === 'reviewNo' && _this.loadDetailPage(row.id);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-manage-review-edit-list-review-improve-grid' ]);
                    Gikam.getComp('qms-manage-review-edit-list-uploader').grid.cleanData();
                }
            },
            onRowActive  : function(index, row) {
                Gikam.getAsyncComp('qms-manage-review-edit-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
                Gikam.getComp('core-manage-review-edit-list-review-improve-grid').refresh({
                    url : Gikam.printf(coreManageReview.baseUrl + '/{id}/review-improves/queries', {
                        id : row.id
                    })
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.ADD',
                icon : 'add',
                onClick : function() {
                    Gikam.postSync(coreManageReview.baseUrl, Gikam.getJsonWrapper({}, [ '', [{}] ])).done(function(item) {
                        _this.loadDetailPage(item);
                    });
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-manage-review-edit-list-grid').deleteRows(coreManageReview.baseUrl).done(function() {
                        Gikam.cleanCompData([ 'core-manage-review-edit-list-review-improve-grid' ]);
                        Gikam.getComp('qms-manage-review-edit-list-uploader').grid.cleanData();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }/*, {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.RETURN',
                icon : 'reject',
                onClick : function() {
                    _this.reject();
                }
            }*/ ]
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
        var _this = this;
        return {
            title : 'MODULE.QMS.MANAGEREVIEWS.TAB.REVIEW_IMPROVE',
            items : [ {
                type : 'grid',
                autoSave : false,
                id : 'core-manage-review-edit-list-review-improve-grid',
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
                id : 'qms-manage-review-edit-list-uploader',
                dbTable : 'T_CORE_MANAGE_REVIEW',
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
    
    submit : function() {
        var grid = Gikam.getComp('core-manage-review-edit-list-grid');
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : coreManageReview
        }).done(function() {
            Gikam.cleanCompData([ 'core-manage-review-edit-list-review-improve-grid' ]);
            Gikam.getComp('qms-manage-review-edit-list-uploader').grid.cleanData();
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('core-manage-review-edit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : coreManageReview
        }).done(function() {
            Gikam.cleanCompData([ 'core-manage-review-edit-list-review-improve-grid' ]);
            Gikam.getComp('qms-manage-review-edit-list-uploader').grid.cleanData();
            grid.refresh();
        });
    },
    
    loadDetailPage : function(id) {
        workspace.window.load(coreManageReview.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            sourcePage : 'edit'
        }));
    },

    create : function() {
        Gikam.create('layout', {
            id : 'core-manage-review-edit-list-layout',
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

    init : function() {
        this.create();
    }
};
