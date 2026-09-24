emcDelivery.editPage = {

    getGridTab : function() {
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : '交接单',
                items : [ this.getGridParam() ]
            } ]
        }
    },

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-delivery-edit-list-grid',
            columns : emcDelivery.getDeliveryListColumns(),
            filterOpen : false,
            url : '/secure/emc/module/bp/deliveries/queries',
            service : 'emcDeliveryServiceImpl',
            page : true,
            checkOnActive : true,
            requestData : {
                org_filter : '1',
                status_SEQ : 'draft'
            },
            onRowActive : function(index, row) {
                if (row && !isNaN(row.id)) {
                    Gikam.getComp('emc-delivery-order-container-list-grid').refresh({
                        url: IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/containers/queries',
                        requestData: {
                            deliveryId_SEQ : row.id
                        }
                    })
                }
            },
            onLoadSuccess : function(data) {
                if (Gikam.isEmpty(data)) {
                    Gikam.cleanCompData([ 'emc-delivery-order-container-list-grid' ]);
                } else {
                    this.activeRowByIndex(0);
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELIVERY.PREVIEW',
                icon : 'print',
                onClick : function() {
                    var selections = Gikam.getComp('emc-delivery-edit-list-grid').getSelections();
                    if (selections.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    Gikam.viewReport(selections[0].reportConfigLineId, selections[0]);
                }
            }],
        }
    },

    getOtherTab : function() {
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : '样品信息',
                items : [ this.getContainerGridParam() ]
            } ]
        }
    },

    getContainerGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-delivery-order-container-list-grid',
            columns : emcDelivery.getOrderContainerListColumns(),
            toolbar : [],
        }
    },

    submit : function() {
        var grid = Gikam.getComp('emc-delivery-edit-list-grid');
	 	var selections =  grid.getSelections();
	 	//至少选择
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.confirm('GIKAM.TIP.CONFIRM', 'EMC.MODULE.BP.DELIVERIES.TIP.CONFRIM_SUBMIT2', function () {
            workspace.window.showMask(true);
            var url = Gikam.printf(emcDelivery.baseUrl + '/action/submit');
            Gikam.postText(url, Gikam.getJsonWrapper({},[ '', selections ])).done(function () {
                grid.refresh();
            }).always(function () {
                workspace.window.closeMask();
            });
        });

    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-delivery-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '50%',
                items : [ _this.getGridTab() ]
            },
            center : {
                items : [ _this.getOtherTab() ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
