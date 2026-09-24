emcOrderStock.editPage = {


    getOrderStockProjectGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-order-stock-proj-edit-list-grid',
            title: 'EMC.MODULE.BP.ORDER.ORDERSTOCK.TAB.PROJINFO',
            url : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/queries',
            columns : emcOrderStock.getOrderStockProjColumns(),
            checkOnActive : true,
            group : {
                fields : [ 'projNo' ],
                formatter : function(row) {
                    return row.projNo;
                }
            },
            filterOpen : true,
            page : true,
            requestData : {
                status_DIFFER : 'draft',
                targetType : 'orderReceipt',
                org_filter : '1',
            },
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.ORDER.ORDERSTOCK.MODAL.STOCKIN',
                icon : 'add',
                onClick : function() {
                    _this.detailsPage();
                }
            }],
            onRowActive : function(index, row) {/*
                if (row) {
                    Gikam.getAsyncComp('emc-order-stock-nosctock-edit-list-grid').done(function (grid) {
                        grid.refresh({
                            requestData : {
                                sampleId_SEQ : row.id,
                                termination_DIFFER : '1',
                                stockStatus_DIFFER : 0
                            },
                            url : '/secure/emc/module/bp/bp/orders/queries',
                        })
                    });
                    Gikam.getAsyncComp('emc-order-stocked-edit-list-grid').done(function (grid) {
                        grid.refresh({
                            requestData : {
                                sampleId_SEQ : row.id,
                                termination_DIFFER : '1',
                                stockStatus_SEQ : 0
                            },
                            url : '/secure/emc/module/bp/bp/orders/queries',
                        })
                    });
                }
            */},
        }
    },


    detailsPage : function() {
        var activeRows = Gikam.getComp('emc-order-stock-proj-edit-list-grid').getActivedRow();
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.ORDER.ORDERSTOCK.MODAL.STOCKIN',
            width : '95%',
            height : '95%',
            url : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/page/order-stock-details'+ Gikam.param({
                id : activeRows.id,
            }),
        });        
    },
    // 创建页面
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            resize : false,
            center : {
                items : [ this.getOrderStockProjectGridParam() ]
            }
           
        });
    },

    // 初始化
    init : function() {
        this.create();
    }
};