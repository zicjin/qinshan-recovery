emcRun.editItemPage = {

    getEditItemGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-run-edit-item-list-grid',
            url : emcRun.baseUrl + Gikam.printf('/select-item/{id}', {
                id : _this.param.id
            }),
            columns : emcRun.getEditItemListColumns(),
            onLoadSuccess : function() {
                var resultGrid = Gikam.getComp('emc-run-result-grid');
                if (Gikam.isEmpty(resultGrid)) {
                    resultGrid = Gikam.getComp('emc-run-edit-edit-list-result-grid');
                }
                
                var orderTaskGrid = Gikam.getComp('emc-run-sample-test-grid');
                if (Gikam.isEmpty(orderTaskGrid)) {
                    orderTaskGrid =  Gikam.getComp('emc-run-edit-edit-list-sample-test-grid');
                }
                // 如果选择的是多个样品,再发一次请求获得多个样品下的所有orderTask
                var selections = orderTaskGrid.getSelections();
                if (selections.length > 1) {
                    var orderIds = '';
                    selections.map(function(e) {
                        orderIds += ',' + e.orderId;
                    });
                    orderIds = orderIds.substring(1);
                    Gikam.post('/secure/emc/module/bp/bp/order-task-results/queries', Gikam.getJsonWrapper({
                        f : {
                            runId_SEQ : selections[0].runId,
                            orderId_IN : orderIds,
                        }
                    }, [ '', [] ])).done(function(data){
                        // 去重
                        var elements = _this.unique(data.rows);
                        // 勾选已经有的分析项
                        elements.map(function(e) {
                            Gikam.getComp('emc-run-edit-item-list-grid').selectRowById(e.itemId);
                        });
                    })
                    
                } else {
                    // 去重
                    var data = _this.unique(resultGrid.getData());
                    // 勾选已经有的分析项
                    data.map(function(data) {
                        Gikam.getComp('emc-run-edit-item-list-grid').selectRowById(data.itemId);
                    });
                }

            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var data = Gikam.getComp('emc-run-edit-item-list-grid').getSelections();
                    if (Gikam.isEmpty(data)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    var orderTaskGrid = Gikam.getComp('emc-run-sample-test-grid');
                    if (Gikam.isEmpty(orderTaskGrid)) {
                        orderTaskGrid =  Gikam.getComp('emc-run-edit-edit-list-sample-test-grid');
                    }
                    Gikam.getLastModal().close([data, orderTaskGrid.getSelections()]);
                }
            } ]
        }
    },

    unique : function(arr) {
        var res = [];
        var json = {};
        for (var i = 0; i < arr.length; i++) {
            if (!json[arr[i].itemId]) {
                res.push(arr[i]);
                json[arr[i].itemId] = 1;
            }
        }
        return res;
    },

    createPage : function() {
        Gikam.create('layout', {
            id : 'emc-run-add-run-detail-layout',
            center : {
                items : [ this.getEditItemGridParam() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
