emcRun.orderChoosePage = {

    getGridParam : function() {
        var _this = this;
        var columns = emcRun.getSampleTestColumns();
        columns.splice(0, 1, {radio : true});
        columns.splice(2, 0, {
            field : 'linkedOrderNo',
            title : 'T_EMC_ORDER.LINKEDORDERNO'
        });
        return {
            type : 'grid',
            id : 'emc-run-order-choose-list-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
            columns : columns,
            readonly : true,
            requestData : {
                orderId_IN : _this.param.orderIds,
            },
            group : {
                fields : [ 'runId' ],
                formatter : function(row) {
                    return row.runId;
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    let leftData = Gikam.getComp('emc-run-order-choose-list-grid').getData();
                    let sourceOrders = leftData.filter(item => !Gikam.isEmpty(item.linkedId));

                    let rightData = Gikam.getComp('emc-run-target-order-choose-list-grid').getData();
                    let targetOrders = rightData.filter(item => !Gikam.isEmpty(item.linkedId));

                    Gikam.getLastModal().close({
                        sourceOrders : sourceOrders,
                        targetOrders : targetOrders
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

    getTargetGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-run-target-order-choose-list-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
            columns : emcRun.getSampleTestColumns(),
            readonly : true,
            requestData : {
                runId_IN : _this.param.relatedRunId,
                qcCategoryCode_IN : _this.param.qcCategoryCodes
            },
            group : {
                fields : [ 'runId' ],
                formatter : function(row) {
                    return row.runId;
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.LINK_ALL',
                icon : 'link',
                onClick : function() {
                    let sourceOrders = Gikam.getComp('emc-run-order-choose-list-grid').getData().filter(item => item.$rowStyle !== 'group');
                    let hasLinked = sourceOrders.some(item => !Gikam.isEmpty(item.linkedId));
                    if (hasLinked) {
                        Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.EXIST_LINKED_ORDER');
                        return;
                    }

                    let targetOrders = Gikam.getComp('emc-run-target-order-choose-list-grid').getData().filter(item => item.$rowStyle !== 'group');
                    if (sourceOrders.length !== targetOrders.length) {
                        Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.NONIDENTICAL_ORDER_COUNT');
                        return;
                    }

                    for (let i = 0; i < sourceOrders.length; i++) {
                        if (sourceOrders[i].qcCategoryCode !== targetOrders[i].qcCategoryCode) {
                            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.NONIDENTICAL_ORDER_CATEGORY');
                            return;
                        }
                    }

                    let timeValue = new Date().getTime();
                    let leftGrid = Gikam.getComp('emc-run-order-choose-list-grid');
                    let rightGrid = Gikam.getComp('emc-run-target-order-choose-list-grid');
                    for (let i = 0; i < sourceOrders.length; i++) {
                        let linkedId = timeValue + i;
                        let sourceOrder = sourceOrders[i];
                        let targetOrder = targetOrders[i];
                        leftGrid.setData({
                            id : sourceOrder.id,
                            linkedId : linkedId,
                            linkedOrderNo : targetOrder.ext$.orderno
                        });
                        rightGrid.setData({
                            id : targetOrder.id,
                            linkedId : linkedId
                        })
                    }
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.LINK',
                icon : 'link',
                onClick : function() {
                    let sourceOrders = Gikam.getComp('emc-run-order-choose-list-grid').getSelections();
                    if (sourceOrders.length !== 1) {
                        Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.SELECT_LEFT_ORDER_FIRST');
                        return;
                    }

                    if (!Gikam.isEmpty(sourceOrders[0].linkedId)) {
                        Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.EXISTED_LINKED_ORDER');
                        return;
                    }

                    let targetOrders = Gikam.getComp('emc-run-target-order-choose-list-grid').getSelections();
                    if (Gikam.isEmpty(targetOrders)) {
                        Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.SELECT_RIGHT_ORDER_FIRST');
                        return;
                    }

                    let sourceQcCategoryCode = sourceOrders[0].qcCategoryCode;
                    let sameQc = targetOrders.every(item => item.qcCategoryCode === sourceQcCategoryCode);
                    if (!sameQc) {
                        Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.SELECT_SAME_QC_ORDER');
                        return;
                    }

                    let linkedId = new Date().getTime();
                    let linkedOrderNo = targetOrders.map(item => item.ext$.orderno).join();
                    Gikam.getComp('emc-run-order-choose-list-grid').setData({
                        id : sourceOrders[0].id,
                        linkedId : linkedId,
                        linkedOrderNo : linkedOrderNo
                    });

                    let rightGrid = Gikam.getComp('emc-run-target-order-choose-list-grid');
                    targetOrders.forEach(item => {
                        rightGrid.setData({
                            id : item.id,
                            linkedId : linkedId
                        })
                    });
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.UNLINK_ALL',
                icon : 'cancel',
                onClick : function() {
                    let sourceOrders = Gikam.getComp('emc-run-order-choose-list-grid').getData().filter(item => item.$rowStyle !== 'group');
                    let targetOrders = Gikam.getComp('emc-run-target-order-choose-list-grid').getData().filter(item => item.$rowStyle !== 'group');

                    let leftGrid = Gikam.getComp('emc-run-order-choose-list-grid');
                    let rightGrid = Gikam.getComp('emc-run-target-order-choose-list-grid');

                    for (let i = 0; i < sourceOrders.length; i++) {
                        let sourceOrder = sourceOrders[i];
                        let targetOrder = targetOrders[i];
                        leftGrid.setData({
                            id : sourceOrder.id,
                            linkedId : '',
                            linkedOrderNo : ''
                        });
                        rightGrid.setData({
                            id : targetOrder.id,
                            linkedId : ''
                        })
                    }
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.UNLINK_ORDER',
                icon : 'cancel',
                onClick : function() {
                    let sourceOrders = Gikam.getComp('emc-run-order-choose-list-grid').getSelections();
                    if (sourceOrders.length !== 1) {
                        Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.SELECT_LEFT_ORDER_FIRST');
                        return;
                    }

                    if (Gikam.isEmpty(sourceOrders[0].linkedId)) {
                        Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.EXISTED_UNLINKED_ORDER');
                        return;
                    }

                    let targetOrders = Gikam.getComp('emc-run-target-order-choose-list-grid').getSelections();
                    if (Gikam.isEmpty(targetOrders)) {
                        Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.SELECT_RIGHT_ORDER_FIRST');
                        return;
                    }

                    let linkedId = sourceOrders[0].linkedId;
                    let matched = targetOrders.every(item => item.linkedId === linkedId);
                    if (!matched) {
                        Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.DIFFERENT_LINKED_ORDER');
                        return;
                    }

                    Gikam.getComp('emc-run-order-choose-list-grid').setData({
                        id : sourceOrders[0].id,
                        linkedId : '',
                        linkedOrderNo : ''
                    });

                    let rightGrid = Gikam.getComp('emc-run-target-order-choose-list-grid');
                    targetOrders.forEach(item => {
                        rightGrid.setData({
                            id : item.id,
                            linkedId : ''
                        })
                    })
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-run-order-choose-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            west : {
                width : '50%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getTargetGridParam() ]
            }
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
