emcOrder.checkProjecItemEditPage = {

    getGridParam : function() {
        var _this = this, columns = emcOrder.getItemListColumns(), requestData = this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-check-project-order-item-edit-list-grid',
            url : '/secure/emc/module/mdm/bizmdm/tests/items/queries/choosable',
            requestData : {
                testMethodId_SEQ : _this.param.testMethodId,
                analysisCategory_SEQ : _this.param.analysisType,
            },
            columns : columns,
            onLoadSuccess : function(rows) {
                if (!Gikam.isEmpty(_this.param.orderTaskId)) {
                    Gikam.post('/secure/emc/module/bp/bp/order-task-results/queries', Gikam.getJsonWrapper({
                        f : {
                            orderTaskId_SEQ : _this.param.orderTaskId
                        },
                    }, [ '', [] ])).done(function(items) {
	                    	var itemId = "";
	                    	var oldItemId = "";
	                        items.rows.map(function(row) {
	                        	itemId = row.itemId;
	                        	if(itemId !== oldItemId){
	                        		 Gikam.getComp('emc-check-project-order-item-edit-list-grid').selectRowById(row.itemId);
	                        	}
	                            oldItemId = row.itemId;
                        });
                    });
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var selections = Gikam.getComp('emc-check-project-order-item-edit-list-grid').getSelections()

                    workspace.window.showMask(true);
                    Gikam.put(Gikam.printf(emcOrder.baseUrl + '/order-tasks/{orderTaskId}/action/result-edit', {
                        orderTaskId : _this.param.orderTaskId
                    }), Gikam.getJsonWrapper({}, [ '', selections ])).done(function() {
                        Gikam.getLastModal().close();
                    }).always(function() {
                        workspace.window.closeMask();
                    });

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
