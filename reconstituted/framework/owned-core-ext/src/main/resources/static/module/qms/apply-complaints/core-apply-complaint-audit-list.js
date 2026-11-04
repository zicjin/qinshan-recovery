coreApplyComplaint.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-apply-complaint-audit-list-grid',
            url : coreApplyComplaint.baseUrl + '/queries/tasks',
            requestData : {
                bpmn_statusCode : _this.param.bpmn_statusCode
            },
            columns : coreApplyComplaint.getAuditListColumns(),
            onCellClick : function(field, row) {
                field === 'applyComplaintNo' && _this.loadDetailPage(row.id);
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
                    var rows = Gikam.getComp('core-apply-complaint-audit-list-grid').getSelections();
                    if (rows.length != 1) {
                        Gikam.alert('QMS.MODULE.ANNUALPLAN.PRINT.ONLY_SELECT_ONE_ROW');
                        return;
                    }
                    Gikam.viewReport('QMS_APPLY_COMPLAINT', {
                        id : rows[0].id
                    });
                }
            } ]
        }
    },

    pass : function() {
        var grid = Gikam.getComp('core-apply-complaint-audit-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : coreApplyComplaint
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('core-apply-complaint-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : coreApplyComplaint
        }).done(function() {
            grid.refresh();
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreApplyComplaint.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            sourcePage : 'review'
        }));
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-apply-complaint-audit-list-layout',
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
