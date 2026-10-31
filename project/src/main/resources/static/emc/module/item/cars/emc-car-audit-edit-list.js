emcCar.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-car-audit-list-grid',
            url : emcCar.baseUrl + '/queries/tasks',
            columns : emcCar.getAuditListColumns(),
            toolbar : _this.getBtnToolbarParam()
        }
    },

    getBtnToolbarParam : function() {
        var _this = this;
        return [ {
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
        } ]

    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-car-audit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    pass : function() {
        var grid = Gikam.getComp('emc-car-audit-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : emcCar
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('emc-car-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcCar
        }).done(function() {
            grid.refresh();
        });
    },

    init : function() {
        this.create();
    }
};
