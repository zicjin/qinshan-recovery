coreDailySupervision.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-daily-supervision-audit-list-grid',
            url : coreDailySupervision.baseUrl + '/queries/tasks',
            requestData : {
                bpmn_statusCode : _this.param.bpmn_statusCode
            },
            columns : coreDailySupervision.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.dailyNo;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-daily-supervision-audit-list-base-info-form', 'core-daily-supervision-audit-list-supervision-item-grid', 'core-daily-supervision-audit-list-supervision-con-grid' ]);
                    Gikam.getComp('qms-daily-supervision-audit-list-uploader').grid.cleanData();
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-daily-supervision-audit-list-base-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(coreDailySupervision.baseUrl + '/{id}', {
                            id : row.id
                        })
                    }).done(function(item) {
                        Gikam.getComp('core-daily-supervision-audit-list-base-info-form').toFieldsReadonly(['remark'])
                    });
                });
                Gikam.getAsyncComp('core-daily-supervision-audit-list-supervision-item-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreDailySupervision.baseUrl + '/{id}/supervision-items/queries', {
                            id : row.id
                        })
                    });
                });
                Gikam.getAsyncComp('qms-daily-supervision-audit-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
                var url = coreDailySupervision.baseUrl + '/{id}/info/status';
                workspace.window.showMask(true);
                Gikam.getTextSync(Gikam.printf(url, {
                    id : row.id
                })).done(function(item) {
                    var statusDto = Gikam.parseJson(item);
                    _this.setStatusCode(statusDto.auditStatus.statusCode)
                }).always(function() {
                    workspace.window.closeMask();
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'pass',
                onClick : function() {
                    var grid = Gikam.getComp('core-daily-supervision-audit-list-grid');
                    Gikam.create('workflow').pass({
                        data : grid.getSelections(),
                        pageObject : coreDailySupervision
                    }).done(function() {
                        grid.refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.RETURN',
                icon : 'reject',
                onClick : function() {
                    var grid = Gikam.getComp('core-daily-supervision-audit-list-grid');
                    Gikam.create('workflow').reject({
                        data : grid.getSelections(),
                        pageObject : coreDailySupervision
                    }).done(function() {
                        grid.refresh();
                    });
                }
            } ]
        }
    },

    getSupervisionTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'MODULE.QMS.DAILYSUPERVISIONS.TAB.BASE_INFO',
                items : [ _this.getSupervisionItemGrid(), _this.getSupervisionConGrid(), {
                    type : 'form',
                    service : 'coreDailySupervisionServiceImpl',
                    id : 'core-daily-supervision-audit-list-base-info-form',
                    fields : coreDailySupervision.getBaseInfoFormFields(),
                    columns : 1,
                    titleWidth : 30
                } ]
            }, _this.getAttachment() ]
        }
    },

    getAttachment : function() {
        return {
            title : 'GIKAM.FILE.ATTACHMENT',
            items : [ {
                type : 'uploader',
                id : 'qms-daily-supervision-audit-list-uploader',
                dbTable : 'T_CORE_DAILY_SUPERVISION',
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

    getSupervisionItemGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            height : 220,
            id : 'core-daily-supervision-audit-list-supervision-item-grid',
            service : 'coreDailySupervisionItemServiceImpl',
            page : false,
            deleteFormatter : function(row) {
                return coreDailySupervision.getAuditNameByCode(row.auditName);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-daily-supervision-audit-list-supervision-con-grid' ]);
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-daily-supervision-audit-list-supervision-con-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreDailySupervision.baseUrl + '/{id}/supervision-cons/queries', {
                            id : row.id
                        })
                    }).done(function(item) {
                        var con = Gikam.getComp('core-daily-supervision-audit-list-supervision-con-grid');
                        if (_this.currentStatusCode == 'supervise') {
                            Gikam.each(con.getData(), function(index, row) {
                                con.toFieldsReadonly(index, ['auditName', 'auditContent']);
                                con.toFieldsEdit(index, ['auditResult', 'remark']);
                            })
                        } else {
                            Gikam.each(con.getData(), function(index, row) {
                                con.toFieldsReadonly(index, ['auditName', 'auditResult', 'auditContent', 'remark']);
                            })
                        }
                    });
                    
                });
            },
            columns : coreDailySupervision.columnUnEditable(coreDailySupervision.getSupervisionItemListColumns(), false),
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    var rows = Gikam.getComp('core-daily-supervision-audit-list-supervision-item-grid').getSelections();
                    if (rows.length != 1) {
                        Gikam.alert('QMS.MODULE.ANNUALPLAN.PRINT.ONLY_SELECT_ONE_ROW');
                        return;
                    }
                    Gikam.viewReport('QMS_DAILY_SUPERVISION', {
                        id : rows[0].id
                    });
                }
            } ]
        };
    },

    getSupervisionConGrid : function() {
        return {
            type : 'grid',
            height : 220,
            id : 'core-daily-supervision-audit-list-supervision-con-grid',
            service : 'coreDailySupervisionConServiceImpl',
            page : false,
            deleteFormatter : function(row) {
                return row.auditContent;
            },
            columns : coreDailySupervision.columnUnEditable(coreDailySupervision.getSupervisionConListColumns(), true)
        };
    },

    setStatusCode : function(statusCode) {
        this.currentStatusCode = statusCode;
    },
    
    currentStatusCode : '',
    
    create : function() {
        Gikam.create('layout', {
            id : 'core-daily-supervision-audit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '35%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getSupervisionTab() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
