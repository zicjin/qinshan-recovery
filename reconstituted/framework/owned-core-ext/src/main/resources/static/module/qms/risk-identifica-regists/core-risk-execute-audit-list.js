coreRiskIdentificaRegist.executeAuditPage = {
    getToolbarButtons : function() {
        var _this = this;
        return [ {
            type : 'button',
            text : 'GIKAM.WORKFLOW.BUTTON.PASS',
            id : 'core-risk-execute-audit-list-pass-button',
            icon : 'pass',
            onClick : function() {
                _this.pass();
            }
        }, {
            type : 'button',
            text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
            icon : 'reject',
            id : 'core-risk-execute-audit-list-reject-button',
            onClick : function() {
                _this.reject();
            }
        }, {
            type : 'button',
            text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.PRINT',
            icon : 'print',
            onClick : function() {
                var rows = Gikam.getComp('core-risk-execute-audit-list-regist-grid').getSelections();
                if (rows.length != 1) {
                    Gikam.alert('QMS.MODULE.ANNUALPLAN.PRINT.ONLY_SELECT_ONE_ROW');
                    return;
                }
                Gikam.viewReport('QMS_RISK_CONTROL', {
                    id : rows[0].id
                });
            }
        } ]
    },

    pass : function() {
        var grid = Gikam.getComp('core-risk-execute-audit-list-regist-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : coreRiskIdentificaRegist
        }).done(function() {
            grid.refresh();
            Gikam.getComp('core-risk-execute-audit-list-factor-grid').cleanData();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('core-risk-execute-audit-list-regist-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : coreRiskIdentificaRegist
        }).done(function() {
            grid.refresh();
            Gikam.getComp('core-risk-execute-audit-list-factor-grid').cleanData();
        });
    },

    getRegistDataGrid : function() {
        var _this = this;
        return {
            id : 'core-risk-execute-audit-list-regist-grid',
            type : 'grid',
            renderTo : workspace.window.$dom,
            columns : coreRiskIdentificaRegist.getExecuteAuditColumns(),
            toolbar : _this.getToolbarButtons(),
            url : coreRiskIdentificaRegist.baseUrl + '/queries/tasks',
            activateFirstRow : true,
            requestData : {
                bpmn_statusCode : _this.param.bpmn_statusCode
            },
            onRowActive : function(index, row) {
                Gikam.getComp('core-risk-execute-audit-list-factor-grid').refresh({
                    url : Gikam.printf(coreRiskIdentificaRegist.registFactorBaseUrl + '/queries', {
                        id : row.id
                    })
                });
            }
        }
    },

    getRegistFactorDataGrid : function() {
        return {
            id : 'core-risk-execute-audit-list-factor-grid',
            type : 'grid',
            renderTo : workspace.window.$dom,
            columns : coreRiskIdentificaRegist.getExecuteAuditRegistFactorColumns(),
            checkOnActive : false,
            autoSave : true,
            service : 'coreRiskIdentificaFactorServiceImpl',
        }
    },

    createPage : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-risk-execute-audit-list-layout',
            west : {
                width : '50%',
                items : [ _this.getRegistDataGrid() ]
            },
            center : {
                items : [ _this.getRegistFactorDataGrid() ]
            },
            renderTo : workspace.window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    },
};
