emcLeaveApply.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-leave-apply-audit-list-grid',
            url : emcLeaveApply.baseUrl + '/queries/tasks',
            columns : emcLeaveApply.getAuditListColumns(),
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
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
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_EMC_LEAVE_APPLY', Gikam.getComp('emc-leave-apply-audit-list-grid').getSelections());
                }
            } ]
        }
    },

    loadDetailPage : function(id) {
        var _this = this;
        Gikam.create('modal', {
            title : 'EMC.MODULE.ITEM.LEAVEAPPLIES.MODAL.DETAIL',
            url : emcLeaveApply.baseUrl + '/page/detail' + Gikam.param({
                id : id,
                operType : 'audit'
            }),
            width : 900,
            height : 300,
            onAfterClose : function() {
                Gikam.getComp('emc-leave-apply-audit-list-grid').refresh();
            }
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-leave-apply-audit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            },
        });
    },

    pass : function() {
        var grid = Gikam.getComp('emc-leave-apply-audit-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : emcLeaveApply
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('emc-leave-apply-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcLeaveApply
        }).done(function() {
            grid.refresh();
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
