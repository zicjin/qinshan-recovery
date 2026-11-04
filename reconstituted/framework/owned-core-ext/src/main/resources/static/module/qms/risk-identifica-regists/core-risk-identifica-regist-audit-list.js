coreRiskIdentificaRegist.auditPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-identifica-regist-audit-list-grid',
            url : coreRiskIdentificaRegist.baseUrl + '/queries/tasks',
            requestData : {
                bpmn_statusCode : _this.param.bpmn_statusCode
            },
            activateFirstRow : true,
            columns : coreRiskIdentificaRegist.getAuditListColumns(),
            onCellClick : function(field, row) {
                field === 'riskNo' && coreRiskIdentificaRegist.loadDetailPage(row.id, 'review');
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('qms-risk-identifica-regist-audit-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('qms-risk-identifica-regist-audit-list-uploader').grid.cleanData();
                }
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
            }, {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    var rows = Gikam.getComp('core-identifica-regist-audit-list-grid').getSelections();
                    if (rows.length != 1) {
                        Gikam.alert('QMS.MODULE.ANNUALPLAN.PRINT.ONLY_SELECT_ONE_ROW');
                        return;
                    }
                    Gikam.viewReport('QMS_RISK_CONTROL', {
                        id : rows[0].id
                    });
                }
            } ]
        }
    },

    pass : function() {
        var grid = Gikam.getComp('core-identifica-regist-audit-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : coreRiskIdentificaRegist
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('core-identifica-regist-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : coreRiskIdentificaRegist
        }).done(function() {
            grid.refresh();
        });
    },

    getAttachment : function() {
        return {
            type : 'tab',
            panels : [ {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'qms-risk-identifica-regist-audit-list-uploader',
                    dbTable : 'T_CORE_RISK_IDENTIFICA_REGIST',
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
            } ]
        }
    },

    create : function() {
        Gikam.create('layout', {
            id : 'core-identifica-regist-audit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '60%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getAttachment() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }

}