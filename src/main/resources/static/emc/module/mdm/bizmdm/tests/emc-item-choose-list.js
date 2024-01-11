emcTestMethod.itemChoosePage = {

    getGridParam : function() {
        var _this = this, columns = emcTestMethod.getItemChooseListColumns(), requestData = this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-test-item-choose-list-grid',
            url : emcTestMethod.baseUrl + '/items/queries/choosable',
            requestData : requestData,
            page : true,
            columns : columns,
            order : {
                'testMethodId' : !Gikam.isEmpty(_this.param.testMethodId_order) ? _this.param.testMethodId_order : ''
            },
            filterOpen : true,
            onLoadSuccess : function(rows) {
                if (!Gikam.isEmpty(_this.param.orderTaskId)) {
                    Gikam.post(Gikam.printf('/secure/emc/module/bp/project/projects/order-tasks/{orderTaskId}/items/queries', {
                        orderTaskId : _this.param.orderTaskId
                    })).done(function(items) {
                        items.rows.map(function(row) {
                            Gikam.getComp('emc-test-item-choose-list-grid').selectRowById(row.itemId);
                        });
                    });
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-test-item-choose-list-grid').getSelections());
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