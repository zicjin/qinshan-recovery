emcReportQuery.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-report-query-audit-list-grid',
            url : emcReportQuery.baseUrl + '/queries/tasks',
            columns : emcReportQuery.getAuditListColumns(),
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
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_EMC_REPORT_QUERY', Gikam.getComp('emc-report-query-audit-list-grid').getSelections());
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-report-query-audit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },
    
    pass : function() {
        var grid = Gikam.getComp('emc-report-query-audit-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : emcReportQuery
        }).done(function() {
            grid.refresh();
        });
    },
    
    reject : function() {
        var grid = Gikam.getComp('emc-report-query-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcReportQuery
        }).done(function() {
            grid.refresh();
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
