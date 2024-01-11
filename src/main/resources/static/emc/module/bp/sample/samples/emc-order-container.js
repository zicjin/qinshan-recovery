emcSample.orderContainerPage = {

    getDataInputDetailGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-order-container-order-task-detail-list-grid',
            url : _this.getOrderTaskUrl(),
            columns : emcSample.getBatchInputContainerColumns(),
            checkOnActive : true,
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

    getOrderTaskUrl : function() {
        Gikam.post('/secure/emc/module/bp/bp/orders/order-tasks/queries', Gikam.getJsonWrapper({}, [ '', this.order ], [ '', this.orderTask ], [ '', this.orderIds ])).done(function(data) {
            Gikam.getComp('emc-order-container-order-task-detail-list-grid').refreshByData(data);
        }).always(function() {
            workspace.window.closeMask();
            Gikam.getComp('emc-sample-edit-list-data-input-container-info-grid').refresh();
        });
    },
    
    // 数据录入容器信息
    updateDataInputOrderTask : function() {
        var selections = Gikam.getComp('emc-order-container-order-task-detail-list-grid').getSelections();
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
            url : emcSample.baseUrl + '/page/order-container-batch',
            onAfterClose : function(data) {
                if (!Gikam.isEmpty(data)) {
                    workspace.window.showMask(true);
                    Gikam.put('/secure/emc/module/bp/bp/orders/tasks/action/batch-update-container', Gikam.getJsonWrapper({}, [ '', originSelections ], [ '', [ data ] ])).done(function() {
                        Gikam.getComp('emc-order-container-order-task-detail-list-grid').refresh();
                        Gikam.getComp('emc-sample-edit-list-data-input-container-info-grid').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
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
        this.order = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getSelections();
        // 控制，只能选择一个
        this.orderTask = Gikam.getComp('emc-sample-edit-list-data-input-container-info-grid').getSelections();

        var orderId = [];
        Gikam.each(this.order, function(index, item) {
            orderId.push({
                id : item.id,
            });
        });
        this.orderIds = orderId;
        
        this.param = param;
        this.createPage();
    },

}