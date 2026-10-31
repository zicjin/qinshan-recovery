emcReport.orderTaskChoosePage = {

    getGridParam : function() {
        var _this = this, columns = emcReport.getReportOrderTaskColumns(), requestData = this.param;
        return {
            type : 'grid',
            id : 'emc-report-order-task-choose-list-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/raw',
            requestData : requestData,
            columns : columns,
            filterOpen : true,

            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close({
                        orderTasks : Gikam.getComp('emc-report-order-task-choose-list-grid').getSelections()
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },


    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                width : '55%',
                items : [ _this.getGridParam() ]
            },
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
