emcDelivery.choosePage = {

    getGridParam : function() {
        var _this = this, columns = emcDelivery.getOrderColumns(), requestData = this.param;
        return {
            type : 'grid',
            id : 'emc-delivery-order-choose-list-grid',
            url : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/bp/project/projects/{id}/orders/queries', {
                id : _this.param.projId
            }),
            requestData : requestData,
            columns : columns,
            filterOpen : true,
            readonly : true,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-delivery-order-choose-list-grid').getSelections());
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
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
