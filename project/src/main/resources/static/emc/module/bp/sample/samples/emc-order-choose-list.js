emcSample.chooseOrderPage = {

    getGridParam : function() {
        var _this = this,  requestData = this.param;
        var columns = emcSample.getOrderChooseListColumns();
        if(!Gikam.isEmpty(_this.param.columnsType)&& _this.param.columnsType == '2'){
            //现场采样，添加样品单独展示，防止修改到其他地方
            columns = emcSample.getOrderChooseListColumns2();
        }
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-order-choose-list-grid',
            url : '/secure/emc/module/bp/bp/orders/queries/choosable',
            requestData : requestData,
            columns : columns,
            page : true,
            checkContinuous : false, // 此属性若为true，全选有问题
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var selections = Gikam.getComp('emc-order-choose-list-grid').getSelections();
                    if (_this.param.columnsType === '2' && selections.find(f => !f.ext$.monitorpointno)) {
                        Gikam.alert('监测点编码为空的样品，不允许添加');
                        return;
                    }
                    Gikam.getLastModal().close(selections);
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
