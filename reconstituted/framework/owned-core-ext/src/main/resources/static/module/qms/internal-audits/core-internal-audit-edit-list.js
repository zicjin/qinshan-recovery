coreInternalAudit.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-internal-audit-edit-list-grid',
            url : coreInternalAudit.baseUrl + '/queries/raw',
            activateFirstRow : true,
            columns : coreInternalAudit.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.auditNo;
            },
            onCellClick : function(field, row) {
                field === 'auditNo' && _this.loadDetailPage(row.id);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-internal-audit-edit-list-audit-group-grid', 'core-internal-audit-edit-list-audit-ncr-grid' ]);
                    Gikam.getComp('qqms-internal-audit-edit-list-uploader').grid.cleanData();
                }
            },
            onRowActive  : function(index, row) {
                Gikam.getAsyncComp('qms-internal-audit-edit-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
                Gikam.getComp('core-internal-audit-edit-list-audit-group-grid').refresh({
                    url : Gikam.printf(coreInternalAudit.baseUrl + '/{id}/audit-groups/queries', {
                        id : row.id
                    })
                });
                Gikam.getComp('core-internal-audit-edit-list-audit-ncr-grid').refresh({
                    url : Gikam.printf(coreInternalAudit.baseUrl + '/{id}/audit-ncrs/queries', {
                        id : row.id
                    })
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.ADD',
                icon : 'add',
                onClick : function() {
                    Gikam.postSync(coreInternalAudit.baseUrl, Gikam.getJsonWrapper({}, [ '', [{}] ])).done(function(item) {
                        _this.loadDetailPage(item);
                    });
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-internal-audit-edit-list-grid').deleteRows(coreInternalAudit.baseUrl).done(function() {
                        Gikam.cleanCompData([ 'core-internal-audit-edit-list-audit-group-grid', 'core-internal-audit-edit-list-audit-ncr-grid' ]);
                        Gikam.getComp('qqms-internal-audit-edit-list-uploader').grid.cleanData();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            } ]
        }
    },

    getSouthTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ _this.getAttachment(), _this.getAuditGroupGrid(), _this.getAuditNcrGrid(), ]
        }
    },
    
    getAuditGroupGrid : function() {
        var _this = this;
        return {
            title : 'MODULE.QMS.INTERNALAUDITS.TAB.AUDIT_GROUP',
            items : [ {
                type : 'grid',
                autoSave : false,
                id : 'core-internal-audit-edit-list-audit-group-grid',
                service : 'coreInternalAuditGroupServiceImpl',
                url : Gikam.printf(coreInternalAudit.baseUrl + '/{id}/audit-groups/queries', {
                    id : 0
                }),
                columns : coreInternalAudit.getAuditGroupListColumns(),
                deleteFormatter : function(row) {
                    return row.groupName;
                },
                toolbar : [ {
                    type : 'button',
                    text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.ADD',
                    icon : 'add-row',
                    onClick : function() {
                        var grid = Gikam.getComp('core-internal-audit-edit-list-audit-group-grid');
                        var rows = grid.getData();
                        rows[rows.length] = {id : 0};
                        grid.refreshByData({
                            rows : rows
                        });
                    }
                }, {
                    type : 'button',
                    text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function() {
                        var grid = Gikam.getComp('core-internal-audit-edit-list-audit-group-grid');
                        var gridData = grid.getData();
                        var refreshGridData = grid.getData();
                        var selectRows = grid.getSelections();
                        for (var i = selectRows.length - 1; i > -1; i--) {
                            if (Gikam.isEmpty(selectRows[i].id) || selectRows[i].id === 0) {
                                _this.removeRowByIndex(gridData, selectRows[i].gridIndex - 1);
                            }
                            _this.removeRowByIndex(refreshGridData, selectRows[i].gridIndex - 1);
                        }
                        grid.deleteRows(Gikam.printf(coreInternalAudit.baseUrl + '/audit-groups')).done(function(){
                        }).always(function() {
                            grid.refreshByData({
                                rows : refreshGridData
                            });
                        });
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.SAVE',
                    icon : 'save',
                    onClick : function() {
                        var grid = Gikam.getComp('core-internal-audit-edit-list-audit-group-grid');
                        var activeRowId = Gikam.getComp('core-internal-audit-edit-list-grid').getActivedRow().id
                        //先删除无效的行
                        var gridData = grid.getData();
                        for (var i = gridData.length - 1; i > -1; i--){
                            if (Gikam.isEmpty(gridData[i].groupName)) {
                                _this.removeRowByIndex(gridData, gridData[i].gridIndex - 1);
                            }
                        }
                        grid.refreshByData({rows : gridData});
                        if (Gikam.isFalse(grid.validate()))
                            return;
                        workspace.window.showMask();
                        Gikam.put(Gikam.printf(coreInternalAudit.baseUrl + '/{id}/audit-groups', {
                            id : activeRowId
                        }), Gikam.getJsonWrapper({}, [ '', gridData ])).done(function() {
                            grid.refresh({
                                url : Gikam.printf(coreInternalAudit.baseUrl + '/{id}/audit-groups/queries', {
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
                id : 'qms-internal-audit-edit-list-uploader',
                dbTable : 'T_CORE_INTERNAL_AUDIT',
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
    
    getAuditNcrGrid : function() {
        var _this = this;
        return {
            title : 'MODULE.QMS.INTERNALAUDITS.TAB.AUDIT_NCR',
            items : [ {
                type : 'grid',
                id : 'core-internal-audit-edit-list-audit-ncr-grid',
                autoSave : false,
                service : 'coreInternalAuditNcrServiceImpl',
                columns : coreInternalAudit.getAuditNcrListColumns(),
                url : Gikam.printf(coreInternalAudit.baseUrl + '/{id}/audit-ncrs/queries', {
                    id : 0
                }),
                deleteFormatter : function(row) {
                    return row.groupLeaderName;
                }
            } ]
        };
    },
    
    submit : function() {
        var grid = Gikam.getComp('core-internal-audit-edit-list-grid');
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : coreInternalAudit
        }).done(function() {
            Gikam.cleanCompData([ 'core-internal-audit-edit-list-audit-group-grid', 'core-internal-audit-edit-list-audit-ncr-grid' ]);
            Gikam.getComp('qms-internal-audit-edit-list-uploader').grid.cleanData();
            grid.refresh();
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreInternalAudit.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            sourcePage : 'edit'
        }));
    },

    create : function() {
        Gikam.create('layout', {
            id : 'core-internal-audit-edit-list-layout',
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
