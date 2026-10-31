emcSamplingPlan.orderContainerPage = {

    getDataInputDetailGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sampling-plan-container-detail-list-grid',
            url : '',
            columns : emcSamplingPlan.getBatchInputContainerColumns(),
            checkOnActive : true,
            onRendered : function () {
                _this.queryOrderTask();
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONTAINER_UPDATE',
                icon : 'add',
                onClick : function() {
                    _this.updateDataInputOrderTask();
                },
            } ]
        }
    },

    queryOrderTask : function() {
        var _this = this;
        Gikam.post('/secure/emc/module/bp/bp/orders/order/tasks/queries', Gikam.getJsonWrapper({}, [ 'emcOrderServiceImpl', _this.orders ], [ 'emcOrderTaskServiceImpl', _this.orderTasks ], [ '', _this.orderIds ])).done(function(data) {
            Gikam.getComp('emc-sampling-plan-container-detail-list-grid').refreshByData(data);
        });
    },

    /** 修改容器信息 */
    updateDataInputOrderTask : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-sampling-plan-container-detail-list-grid').getSelections();
        var originSelections = selections.map(function(item) {
            return Object.assign({}, item)
        });
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            width : 800,
            height : 313,
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.CONTAINER_INFO',
            url : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/page/order-container-batch',
            onAfterClose : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.put('/secure/emc/module/bp/bp/orders/tasks/action/batch-update-container', Gikam.getJsonWrapper({}, [ '', originSelections ], [ '', [ data ] ])).always(function() {
                        Gikam.getLastModal().close();
                    });
                }
            }
        });
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getDataInputDetailGridParam() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
        });
        return this;
    },

    init : function(param) {
        this.orders = Gikam.getComp('emc-sampling-plan-edit-list-order-grid').getSelections();
        this.orderTasks = Gikam.getComp('emc-sampling-plan-edit-list-container-info-grid').getSelections();
        this.orderIds = this.orders.map(item => ({id : item.id}));
        this.param = param;
        this.createPage();
    },

};
