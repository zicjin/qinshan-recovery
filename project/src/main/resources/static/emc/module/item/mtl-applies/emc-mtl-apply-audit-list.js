emcMtlApply.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-mtl-apply-audit-list-grid',
            url : emcMtlApply.baseUrl + '/queries/tasks',
            columns : emcMtlApply.getAuditListColumns(),
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
                    Gikam.viewAudit('T_EMC_MTL_APPLY', Gikam.getComp('emc-mtl-apply-audit-list-grid').getSelections());
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-mtl-apply-audit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            },
        });
    },

    pass : function() {
        var grid = Gikam.getComp('emc-mtl-apply-audit-list-grid');
        var selections = grid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var validation = selections.every(function (row) {
            return parseFloat(row.ext$.stockqty) >= row.applyQty;
        });
        if (!validation) {
            Gikam.alert('EMC.MODULE.ITEM.MTLAPPLIES.TIP.AUDIT_OVER_STOCK');
            return;
        }

        Gikam.post(emcMtlApply.baseUrl + '/action/validate-stock', Gikam.getJsonWrapper(null, [ '', selections ])).done(function (res) {
            if (res.valid) {
                Gikam.create('workflow').pass({
                    data : selections,
                    pageObject : emcMtlApply
                }).done(function() {
                    grid.refresh();
                });
            } else {
                Gikam.alert('EMC.MODULE.ITEM.MTLAPPLIES.TIP.AUDIT_OVER_STOCK');
            }
        });
    },

    reject : function() {
        var grid = Gikam.getComp('emc-mtl-apply-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcMtlApply
        }).done(function() {
            grid.refresh();
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
