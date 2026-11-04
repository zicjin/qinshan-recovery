coreRiskIdentificaRegist.archiveSearchListPage = {
    getRegistDataGrid : function() {
        return {
            id : 'core-risk-archive-search-list-regist-grid',
            type : 'grid',
            renderTo : workspace.window.$dom,
            columns : coreRiskIdentificaRegist.getSearchColumns(),
            url : coreRiskIdentificaRegist.baseUrl + '/queries',
            requestData : {
                processStatus_EQ : 'done'
            },
            onCellClick : function(field, row) {
                Gikam.getComp('core-risk-archive-search-list-factor-grid').refresh({
                    url : Gikam.printf(coreRiskIdentificaRegist.registFactorBaseUrl + '/queries', {
                        id : row.id
                    })
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    var rows = Gikam.getComp('core-risk-archive-search-list-regist-grid').getSelections();
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

    getRegistFactorDataGrid : function() {
        return {
            id : 'core-risk-archive-search-list-factor-grid',
            type : 'grid',
            renderTo : workspace.window.$dom,
            columns : coreRiskIdentificaRegist.getRegistFactorColumns(),
            checkOnActive : false,
            autoSave : true,
            service : 'coreRiskIdentificaFactorServiceImpl',
        }
    },

    createPage : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-risk-archive-search-list-layout',
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
}