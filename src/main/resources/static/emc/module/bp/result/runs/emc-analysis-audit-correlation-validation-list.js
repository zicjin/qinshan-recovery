// 分析审核相关性检查
emcAnalysisAudit.dataCorrelationPage = {

    // 相关性描述
    getGridParam : function() {
        var _this = this;
        var analysisProjects = _this.param.bizType === 'pretrial' ? emcAnalysisPretrial.searchPage.analysisAuditProjectSelections : emcAnalysisAudit.editPage.analysisAuditProjectSelections;
        return {
            type : 'grid',
            id : 'emc-analysis-audit-correlation-validation-list-grid',
            columns : emcAnalysisAudit.getOrderTaskResultValidationListColumns(),
            onRendered : function() {
                Gikam.post('/secure/emc/module/bp/bp/order-tasks/results/validations', Gikam.getJsonWrapper({}, [ '', analysisProjects ])).done(function(r) {
                    if(!r)
                        return;
                    Gikam.getComp('emc-analysis-audit-correlation-validation-list-grid').loadData(r.rows);
                });
            }
        }
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-analysis-audit-correlation-validation-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
