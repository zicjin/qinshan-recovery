emcOrderReceipt.orderReceiptContainerPage = {

    getDataInputDetailGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-order-container-order-receipt-detail-list-grid',
            url : _this.getOrderTaskUrl(),
            columns : emcOrderReceipt.getBatchInputContainerColumns(),
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
        Gikam.post('/secure/emc/module/bp/bp/orders/order-tasks/queries', Gikam.getJsonWrapper({
            f : {
                orderId_IN : this.orderIds.join(','),
                testName_SEQ : this.orderTask[0].testName,
            }
        }, )).done(function(data) {
            Gikam.getComp('emc-order-container-order-receipt-detail-list-grid').refreshByData(data);
        }).always(function() {
            workspace.window.closeMask();
            Gikam.getComp('emc-order-receipt-edit-list-container-grid').refresh();
        });
    },
    
    // 容器信息
    updateDataInputOrderTask : function() {
        var selections = Gikam.getComp('emc-order-container-order-receipt-detail-list-grid').getSelections();
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
            url : emcOrderReceipt.baseUrl + '/page/order-container-batch',
            onAfterClose : function(data) {
                if (!Gikam.isEmpty(data)) {
                    workspace.window.showMask(true);
                    Gikam.put('/secure/emc/module/bp/bp/orders/tasks/action/batch-update-container', Gikam.getJsonWrapper({}, [ '', originSelections ], [ '', [ data ] ])).done(function() {
                        Gikam.getComp('emc-order-container-order-receipt-detail-list-grid').refresh();
                        Gikam.getComp('emc-order-receipt-edit-list-container-grid').refresh();
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

        this.order = Gikam.getComp('emc-order-receipt-edit-list-order-grid').getSelections();

        this.orderTask = Gikam.getComp('emc-order-receipt-edit-list-container-grid').getSelections();

        var orderId = [];
        Gikam.each(this.order, function(index, item) {
            orderId.push(item.id);
        });
        this.orderIds = orderId;

        this.param = param;
        this.createPage();
    },

}