coreTrainPlan.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-train-plan-audit-list-grid',
            url : coreTrainPlan.baseUrl + '/queries/tasks',
            toolbar : [ {
                type : 'btnGroup',
                items : [ {
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
            } ],
            columns : coreTrainPlan.getAuditListColumns(),
            onCellClick : function(field, row) {
                field === 'theme' && _this.loadDetailPage(row.id);
            }
        };
    },

    pass : function() {
        var grid = Gikam.getComp('core-train-plan-audit-list-grid');
        Gikam.create('workflow').pass({
            pageObject : coreTrainPlan,
            data : grid.getSelections()
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('core-train-plan-audit-list-grid');
        Gikam.create('workflow').reject({
            pageObject : coreTrainPlan,
            data : grid.getSelections()
        }).done(function() {
            grid.refresh();
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreTrainPlan.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-train-plan-audit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        var _this = this;
        _this.param = param;
        _this.create();
    }
};