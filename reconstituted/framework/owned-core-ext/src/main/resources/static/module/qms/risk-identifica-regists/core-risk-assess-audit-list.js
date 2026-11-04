coreRiskIdentificaRegist.assessAuditPage = {
    getToolbarButtons : function() {
        var _this = this;
        return [ {
            type : 'button',
            text : 'GIKAM.WORKFLOW.BUTTON.PASS',
            icon : 'pass',
            onClick : function() {
                if (Gikam.getComp('core-risk-assess-audit-list-regist-grid').validate()) {
                    _this.pass();
                }
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
                var rows = Gikam.getComp('core-risk-assess-audit-list-regist-grid').getSelections();
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
        var grid = Gikam.getComp('core-risk-assess-audit-list-regist-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : coreRiskIdentificaRegist
        }).done(function() {
            grid.refresh();
            Gikam.getComp('core-risk-assess-audit-list-factor-grid').cleanData();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('core-risk-assess-audit-list-regist-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : coreRiskIdentificaRegist
        }).done(function() {
            grid.refresh();
            Gikam.getComp('core-risk-assess-audit-list-factor-grid').cleanData();
        });
    },

    getRegistDataGrid : function() {
        var _this = this;
        return {
            id : 'core-risk-assess-audit-list-regist-grid',
            type : 'grid',
            renderTo : workspace.window.$dom,
            columns : coreRiskIdentificaRegist.getAssessAuditColumns(),
            toolbar : _this.getToolbarButtons(),
            url : coreRiskIdentificaRegist.baseUrl + '/queries/tasks',
            requestData : {
                isAssessed : '1',
                bpmn_statusCode : _this.param.bpmn_statusCode
            },
            autoSave : true,
            activateFirstRow : true,
            service : 'coreRiskIdentificaRegistServiceImpl',
            onRowActive : function(index, row) {
                Gikam.getComp('core-risk-assess-audit-list-factor-grid').refresh({
                    url : Gikam.printf(coreRiskIdentificaRegist.registFactorBaseUrl + '/queries', {
                        id : row.id
                    }),
                    requestData : {
                        isAssessed : '1'
                    }
                });
            }
        }
    },

    getRegistFactorDataGrid : function() {
        return {
            id : 'core-risk-assess-audit-list-factor-grid',
            type : 'grid',
            renderTo : workspace.window.$dom,
            columns : coreRiskIdentificaRegist.getAssessAuditRegistFactorColumns()
        }
    },

    createPage : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-risk-assess-audit-list-layout',
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
