emcFileApply.auditPage = {

    getGridParam : function() {
        var _this = this;
        var filterUserId = '';
        if(_this.param.bpmn_statusCode === 'confirm') {
            filterUserId = '1';
        }
        return {
            type : 'Grid',
            id : 'emc-file-apply-audit-list-grid',
            url : emcFileApply.baseUrl + '/queries/tasks',
            columns : emcFileApply.getAuditListColumns(),
            requestData : {
                filterUserId : filterUserId,
                bpmn_statusCode : _this.param.bpmn_statusCode
            },
            service : 'emcFileApplyServiceImpl',
            deleteFormatter : function(row) {
                return row.fileNo || row.id;
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
    
    pass : function(purchObject) {
        var grid = Gikam.getComp('emc-file-apply-audit-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : emcFileApply
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function(purchObject) {
        var grid = Gikam.getComp('emc-file-apply-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcFileApply
        }).done(function() {
            grid.refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-file-apply-audit-list-layout',
            renderTo : workspace.window.$dom,
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
