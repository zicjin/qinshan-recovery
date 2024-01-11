emcOvertime.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-overtime-audit-list-grid',
            url : emcOvertime.baseUrl + '/queries',
            columns : emcOvertime.getSearchListColumns(),
            service : 'emcOvertimeServiceImpl',
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.ITEM.LM.OVERTIME.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    _this.viewReport();
                }
            } ]
        }
    },

   

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-overtime-audit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    pass : function() {
        var grid = Gikam.getComp('emc-overtime-audit-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : emcOvertime
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('emc-overtime-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcOvertime
        }).done(function() {
            grid.refresh();
        });
    },
    
    viewReport : function(rows) {
        var selections = Gikam.getComp('emc-overtime-audit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.viewReport('OVERTIME_REPORT', {
            id : selections[0].id
        });
    },

    init : function() {
        this.create();
    }
};
