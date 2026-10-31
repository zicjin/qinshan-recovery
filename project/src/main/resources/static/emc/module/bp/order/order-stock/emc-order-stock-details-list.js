emcOrderStock.detailsPage = {


    getNotStockTab : function() {
        var _this = this;
        var activeRows = Gikam.getComp('emc-order-stock-proj-edit-list-grid').getActivedRow();
        return {
            type : 'tab',
            width : Gikam.width.S,
            panels : [ {
                title: 'EMC.MODULE.BP.ORDER.ORDERSTOCK.TAB.NOTSTOCK',
                items : [ {
                    type : 'grid',
                    id : 'emc-order-stock-nosctock-details-list-grid',
                    columns : emcOrderStock.getOrderLabelNotStockColumns(),
                    url : '/secure/emc/module/bp/bp/orders/queries',
                    filterOpen : true,
                    page : true,
                    requestData : {
                    	sampleId_SEQ : activeRows.id,
                        termination_DIFFER : '1',
                        stockStatus_SEQ : 0
                    },
                    onUpdated : function(data, fields, index) {
                        Gikam.getComp('emc-order-stock-nosctock-details-list-grid').refresh();
                        Gikam.getComp('emc-order-stocked-details-list-grid').refresh();   
                    },
                    service : 'PfOrderServiceImpl',
                    checkOnActive : true,
                    toolbar : [{
                        type : 'button',
                        text : 'GIKAM.BUTTON.BATCHUPDADE',
                        icon : 'modify',
                        onClick : function() {
                            _this.batchInputNoStock();
                        }
                    
                    }],
                } ]
            } ]
        }
    },

    getStockedTab : function() {
        var _this = this;
        var activeRows = Gikam.getComp('emc-order-stock-proj-edit-list-grid').getActivedRow();
        return {
            type : 'tab',
            width : Gikam.width.S,
            panels : [ {
                title: 'EMC.MODULE.BP.ORDER.ORDERSTOCK.TAB.STOCKED',
                items : [ {
                    type : 'grid',
                    id : 'emc-order-stocked-details-list-grid',
                    columns : emcOrderStock.getOrderStockedColumns(),
                    filterOpen : true,
                    url : '/secure/emc/module/bp/bp/orders/queries',
                    page : true,
                    requestData : {
                    	sampleId_SEQ : activeRows.id,
                        termination_DIFFER : '1',
                        stockStatus_IN: '1,'
                    },
                    onUpdated : function(data, fields, index) {
                        Gikam.getComp('emc-order-stock-nosctock-details-list-grid').refresh();
                        Gikam.getComp('emc-order-stocked-details-list-grid').refresh();   
                    },
                    service : 'PfOrderServiceImpl',
                    checkOnActive : true,
                    toolbar : [{
                        type : 'button',
                        text : 'GIKAM.BUTTON.BATCHUPDADE',
                        icon : 'modify',
                        onClick : function() {
                            _this.batchInputStocked();
                        }
                    
                    }],                    
                } ]
            } ]
        }
    },

    batchInputNoStock : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-order-stock-nosctock-details-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

      var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
            width : 400,
            height : 270,
        });
        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type: 'btnToolbar',
                    items: [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        icon : 'select',
                        onClick : function() {
                            if (form.validate()) {
                                var formData = form.getData();
                                selections.forEach(function (item) {
                                    item.ext$.receiver = formData.ext$.receiver;
                                    item.ext$.returner = formData.ext$.returner;
                                    item.ext$.stockdate = formData.ext$.stockdate;
                                    item.ext$.stockstatus = formData.ext$.stockstatus;
                                    item.ext$.returndate = formData.ext$.returndate;
 
                                })                    

                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/' + selections[0].id, Gikam.getJsonWrapper({}, [ 'PfOrderServiceImpl', selections ])).done(function (data) {
                                    Gikam.getComp('emc-order-stock-nosctock-details-list-grid').refresh();
                                    Gikam.getComp('emc-order-stocked-details-list-grid').refresh();
                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close();
                                });
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
                        icon : 'cancel',
                        onClick : function () {
                            modal.close();
                        }
                    } ]
                }]
            }
        });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            columns: 1,
            titleWidth: 80,
            fields: emcOrderStock.getNoStockBatchColumns(),
            autoSave: false
        });

    },
   
    batchInputStocked : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-order-stocked-details-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
            width : 400,
            height : 270,
        });
        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type: 'btnToolbar',
                    items: [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        icon : 'select',
                        onClick : function() {
                            if (form.validate()) {
                                var formData = form.getData();
                                selections.forEach(function (item) {
                                    item.ext$.receiver = formData.ext$.receiver;
                                    item.ext$.returner = formData.ext$.returner;
                                    item.ext$.stockdate = formData.ext$.stockdate;
                                    item.ext$.stockstatus = formData.ext$.stockstatus;
                                    item.ext$.returndate = formData.ext$.returndate;
                                })                    

                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/' + selections[0].id, Gikam.getJsonWrapper({}, [ 'PfOrderServiceImpl', selections ])).done(function (data) {
                                    Gikam.getComp('emc-order-stock-nosctock-details-list-grid').refresh();
                                    Gikam.getComp('emc-order-stocked-details-list-grid').refresh();
                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close();
                                });
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
                        icon : 'cancel',
                        onClick : function () {
                            modal.close();
                        }
                    } ]
                }]
            }
        });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            columns: 1,
            titleWidth: 80,
            fields: emcOrderStock.getStockedBatchColumns(),
            autoSave: false
        });

    },
    
    // 创建页面
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
/*            north : {
                height : 91,
                items : [ _this.getOrderStockGridParam() ]
            },*/
            center : {
                items : [ {
                    type : 'layout',
                    west : {
                        width : '50%',
                        items : [ _this.getStockedTab() ]
                    },
                    east : {
                        width : '50%',
                        items : [ _this.getNotStockTab() ]
                    }
                } ]
            }
           
        });
        return this;
    },



    
    // 初始化
    init : function(param) {
        this.param = param;
        this.create();
    },

};