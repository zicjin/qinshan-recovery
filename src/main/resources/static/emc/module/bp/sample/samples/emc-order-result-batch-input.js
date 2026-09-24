emcSample.orderResultBatchInputPage = {

    getBatchInputGrid : function() {
        var _this = this, columns = emcSample.getOrderResultBatchInputColumns(), requestData = this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });

        return {
            type : 'grid',
            id : 'emc-sample-order-result-batch-input-list',
            service : 'emcOrderTaskServiceImpl',
            url : '/secure/emc/module/bp/bp/order-tasks/queries/order-task',
            requestData : requestData,
            columns : columns,
            page : true,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-sample-order-result-batch-input-list').getSelections());
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        };
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBatchInputGrid() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    },

}
