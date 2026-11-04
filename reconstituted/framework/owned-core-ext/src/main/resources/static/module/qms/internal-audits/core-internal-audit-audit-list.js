coreInternalAudit.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-internal-audit-audit-list-grid',
            url : coreInternalAudit.baseUrl + '/queries/tasks',
            requestData : {
                bpmn_statusCode : _this.param.bpmn_statusCode
            },
            activateFirstRow : true,
            columns : coreInternalAudit.getAuditListColumns(),
            onCellClick : function(field, row) {
                field === 'auditNo' && _this.loadDetailPage(row.id);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-internal-audit-audit-list-audit-group-grid', 'core-internal-audit-audit-list-audit-ncr-grid' ]);
                    Gikam.getComp('qqms-internal-audit-audit-list-uploader').grid.cleanData();
                }
            },
            onRowActive  : function(index, row) {
                Gikam.getAsyncComp('qms-internal-audit-audit-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
                Gikam.getComp('core-internal-audit-audit-list-audit-group-grid').refresh({
                    url : Gikam.printf(coreInternalAudit.baseUrl + '/{id}/audit-groups/queries', {
                        id : row.id
                    })
                });
                Gikam.getComp('core-internal-audit-audit-list-audit-ncr-grid').refresh({
                    url : Gikam.printf(coreInternalAudit.baseUrl + '/{id}/audit-ncrs/queries', {
                        id : row.id
                    })
                });
                var url = coreInternalAudit.baseUrl + '/{id}/info/status';
                workspace.window.showMask(true);
                Gikam.getTextSync(Gikam.printf(url, {
                    id : row.id
                })).done(function(item) {
                    var statusDto = Gikam.parseJson(item);
                    var statusCode = statusDto.auditStatus.statusCode;
                    if (statusCode == 'auditNcr') {
                        Gikam.getComp('core-internal-audit-ncr-button-add').show();
                        Gikam.getComp('core-internal-audit-ncr-button-remove').show();
                        Gikam.getComp('core-internal-audit-ncr-button-save').show();
                    } else {
                        Gikam.getComp('core-internal-audit-ncr-button-add').hide();
                        Gikam.getComp('core-internal-audit-ncr-button-remove').hide();
                        Gikam.getComp('core-internal-audit-ncr-button-save').hide();
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
            panels : [ _this.getAttachment(), _this.getAuditGroupGrid(), _this.getAuditNcrGrid(), ]
        }
    },
    
    getAuditGroupGrid : function() {
        return {
            title : 'MODULE.QMS.INTERNALAUDITS.TAB.AUDIT_GROUP',
            items : [ {
                type : 'grid',
                id : 'core-internal-audit-audit-list-audit-group-grid',
                service : 'coreInternalAuditGroupServiceImpl',
                autoSave : false,
                url : Gikam.printf(coreInternalAudit.baseUrl + '/{id}/audit-groups/queries', {
                    id : 0
                }),
                deleteFormatter : function(row) {
                    return row.id;
                },
                columns : coreInternalAudit.getAuditGroupListColumns()
            } ]
        };
    },

    getAttachment : function() {
        return {
            title : 'GIKAM.FILE.ATTACHMENT',
            items : [ {
                type : 'uploader',
                id : 'qms-internal-audit-audit-list-uploader',
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
        return {
            title : 'MODULE.QMS.INTERNALAUDITS.TAB.AUDIT_NCR',
            items : [ {
                type : 'grid',
                id : 'core-internal-audit-audit-list-audit-ncr-grid',
                service : 'coreInternalAuditNcrServiceImpl',
                autoSave : false,
                url : Gikam.printf(coreInternalAudit.baseUrl + '/{id}/audit-ncrs/queries', {
                    id : 0
                }),
                deleteFormatter : function(row) {
                    return row.id;
                },
                columns : coreInternalAudit.getAuditNcrListColumns(),
                toolbar : [ {
                    type : 'button',
                    id : 'core-internal-audit-ncr-button-add',
                    text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.ADD',
                    icon : 'add-row',
                    onClick : function() {
                        var grid = Gikam.getComp('core-internal-audit-audit-list-audit-ncr-grid');
                        var rows = grid.getData();
                        rows[rows.length] = {id : 0};
                        grid.refreshByData({
                            rows : rows
                        });
                    }
                }, {
                    type : 'button',
                    id : 'core-internal-audit-ncr-button-remove',
                    text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function() {
                        var grid = Gikam.getComp('core-internal-audit-audit-list-audit-ncr-grid');
                        var gridData = grid.getData();
                        var refreshGridData = grid.getData();
                        var selectRows = grid.getSelections();
                        for (var i = selectRows.length - 1; i > -1; i--) {
                            if (Gikam.isEmpty(selectRows[i].id) || selectRows[i].id === 0) {
                                _this.removeRowByIndex(gridData, selectRows[i].gridIndex - 1);
                            }
                            _this.removeRowByIndex(refreshGridData, selectRows[i].gridIndex - 1);
                        }
                        grid.deleteRows(Gikam.printf(coreInternalAudit.baseUrl + '/audit-ncrs')).done(function(){
                        }).always(function() {
                            grid.refreshByData({
                                rows : refreshGridData
                            });
                        });
                    }
                }, {
                    type : 'button',
                    id : 'core-internal-audit-ncr-button-save',
                    text : 'GIKAM.BUTTON.SAVE',
                    icon : 'save',
                    onClick : function() {
                        var grid = Gikam.getComp('core-internal-audit-audit-list-audit-ncr-grid');
                        var activeRowId = Gikam.getComp('core-internal-audit-audit-list-grid').getActivedRow().id
                        //先删除无效的行
                        var gridData = grid.getData();
                        for (var i = gridData.length - 1; i > -1; i--){
                            if (Gikam.isEmpty(gridData[i].groupLeaderName)) {
                                _this.removeRowByIndex(gridData, gridData[i].gridIndex - 1);
                            }
                        }
                        grid.refreshByData({rows : gridData});
                        if (Gikam.isFalse(grid.validate()))
                            return;
                        workspace.window.showMask();
                        Gikam.put(Gikam.printf(coreInternalAudit.baseUrl + '/{id}/audit-ncrs', {
                            id : activeRowId
                        }), Gikam.getJsonWrapper({}, [ '', gridData ])).done(function() {
                            grid.refresh({
                                url : Gikam.printf(coreInternalAudit.baseUrl + '/{id}/audit-ncrs/queries', {
                                    id : activeRowId
                                })
                            });
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                }]
            } ]
        };
    },
    
    removeRowByIndex : function(rows, index) {
        rows.splice(index, 1);
        Gikam.each(rows, function(index, row){
            row.gridIndex = index + 1;
        })
    },
    
    pass : function() {
        var grid = Gikam.getComp('core-internal-audit-audit-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : coreInternalAudit
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('core-internal-audit-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : coreInternalAudit
        }).done(function() {
            grid.refresh();
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreInternalAudit.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            sourcePage : 'review'
        }));
    },

    create : function() {
        Gikam.create('layout', {
            id : 'core-internal-audit-audit-list-layout',
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
