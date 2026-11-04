coreManageReview.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-manage-review-audit-list-grid',
            url : coreManageReview.baseUrl + '/queries/tasks',
            requestData : {
                bpmn_statusCode : _this.param.bpmn_statusCode
            },
            columns : coreManageReview.getAuditListColumns(),
            onCellClick : function(field, row) {
                field === 'reviewNo' && _this.loadDetailPage(row.id);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-manage-review-audit-list-review-improve-grid' ]);
                    Gikam.getComp('qms-manage-review-audit-list-uploader').grid.cleanData();
                }
            },
            onRowActive  : function(index, row) {
                Gikam.getAsyncComp('qms-manage-review-audit-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
                Gikam.getComp('core-manage-review-audit-list-review-improve-grid').refresh({
                    url : Gikam.printf(coreManageReview.baseUrl + '/{id}/review-improves/queries', {
                        id : row.id
                    })
                });
                var url = coreManageReview.baseUrl + '/{id}/info/status';
                workspace.window.showMask(true);
                Gikam.getTextSync(Gikam.printf(url, {
                    id : row.id
                })).done(function(item) {
                    var statusDto = Gikam.parseJson(item);
                    var statusCode = statusDto.auditStatus.statusCode;
                    if (statusCode == 'editReport') {
                        Gikam.getComp('core-manage-review-audit-improve-button-add').show();
                        Gikam.getComp('core-manage-review-audit-improve-button-delete').show();
                        Gikam.getComp('core-manage-review-audit-improve-button-save').show();
                    } else {
                        Gikam.getComp('core-manage-review-audit-improve-button-add').hide();
                        Gikam.getComp('core-manage-review-audit-improve-button-delete').hide();
                        Gikam.getComp('core-manage-review-audit-improve-button-save').hide();
                    }
                }).always(function() {
                    workspace.window.closeMask();
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
                id : 'core-manage-review-audit-list-review-improve-grid',
                service : 'coreManageReviewImproveServiceImpl',
                autoSave : false,
                url : Gikam.printf(coreManageReview.baseUrl + '/{id}/review-improves/queries', {
                    id : 0
                }),
                deleteFormatter : function(row) {
                    return row.id;
                },
                columns : coreManageReview.getEditImproveListColumns(),
                toolbar : [ {
                    type : 'button',
                    id : 'core-manage-review-audit-improve-button-add',
                    text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.ADD',
                    icon : 'add-row',
                    onClick : function() {
                        var grid = Gikam.getComp('core-manage-review-audit-list-review-improve-grid');
                        var rows = grid.getData();
                        rows[rows.length] = _this.getInsertEmptyGirdRow(rows.length + 1);
                        grid.refreshByData({
                            rows : rows
                        });
                    }
                }, {
                    type : 'button',
                    id : 'core-manage-review-audit-improve-button-delete',
                    text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function() {
                        var grid = Gikam.getComp('core-manage-review-audit-list-review-improve-grid');
                        var gridData = grid.getData();
                        var refreshGridData = grid.getData();
                        var selectRows = grid.getSelections();
                        for (var i = selectRows.length - 1; i > -1; i--) {
                            if (Gikam.isEmpty(selectRows[i].id) || selectRows[i].id === 0) {
                                _this.removeRowByIndex(gridData, selectRows[i].gridIndex - 1);
                            }
                            _this.removeRowByIndex(refreshGridData, selectRows[i].gridIndex - 1);
                        }
                        grid.deleteRows(Gikam.printf(coreManageReview.baseUrl + '/review-improves')).done(function(){

                        }).always(function() {
                            grid.refreshByData({
                                rows : refreshGridData
                            });
                        });
                    }
                }, {
                    type : 'button',
                    id : 'core-manage-review-audit-improve-button-save',
                    text : 'GIKAM.BUTTON.SAVE',
                    icon : 'save',
                    onClick : function() {
                        var grid = Gikam.getComp('core-manage-review-audit-list-review-improve-grid');
                        var activeRowId = Gikam.getComp('core-manage-review-audit-list-grid').getActivedRow().id
                        //先删除无效的行
                        var gridData = grid.getData();
                        for (var i = gridData.length - 1; i > -1; i--){
                            if (Gikam.isEmpty(gridData[i].problems)) {
                                _this.removeRowByIndex(gridData, gridData[i].gridIndex - 1);
                            }
                        }
                        grid.refreshByData({rows : gridData});
                        if (Gikam.isFalse(grid.validate()))
                            return;
                        workspace.window.showMask();
                        Gikam.put(Gikam.printf(coreManageReview.baseUrl + '/{id}/review-improves', {
                            id : activeRowId
                        }), Gikam.getJsonWrapper({}, [ '', gridData ])).done(function() {
                            grid.refresh({
                                url : Gikam.printf(coreManageReview.baseUrl + '/{id}/review-improves/queries', {
                                    id : activeRowId
                                })
                            });
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                } ]
            } ]
        };
    },
    
    getInsertEmptyGirdRow : function(index) {
        return {
            manageReviewId: 0,
            ext$: {rn_: index},
            gridIndex: index,
            id: 0
        }
    },
    
    removeRowByIndex : function(rows, index) {
        rows.splice(index, 1);
        Gikam.each(rows, function(index, row){
            row.gridIndex = index + 1;
        })
    },
    
    getAttachment : function() {
        return {
            title : 'GIKAM.FILE.ATTACHMENT',
            items : [ {
                type : 'uploader',
                id : 'qms-manage-review-audit-list-uploader',
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
    
    pass : function() {
        var grid = Gikam.getComp('core-manage-review-audit-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : coreManageReview
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('core-manage-review-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : coreManageReview
        }).done(function() {
            grid.refresh();
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreManageReview.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            sourcePage : 'review'
        }));
    },

    create : function() {
        Gikam.create('layout', {
            id : 'core-manage-review-audit-list-layout',
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
