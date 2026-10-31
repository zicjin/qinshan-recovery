emcProject.orderItemGroupChoosePage = {

    getGridParam : function() {
        var _this = this, columns = emcProject.getOrderItemGroupChooseColumns(), requestData = this.param;
        console.log(_this.param.projId);
        return {
            type : 'grid',
            id : 'emc-order-itemgroup-choose-list-grid',
            requestData : requestData,
            columns : columns,
            filterOpen : true,
            page : true,
            url : IFM_CONTEXT +'/secure/emc/module/bp/bp/orders/'+_this.param.projId+'/project/item-groups/queries',
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    let orderItemGroup = Gikam.getComp('emc-order-itemgroup-choose-list-grid').getSelections();
                    Gikam.getLastModal().close({
                        orderItemGroup : orderItemGroup
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
