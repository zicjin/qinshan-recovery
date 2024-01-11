emcOrder.itemEditPage = {

    getGridParam : function() {
        var _this = this, columns = emcOrder.getItemListColumns(), requestData = this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-order-item-edit-list-grid',
            url : '/secure/emc/module/mdm/bizmdm/tests/items/queries/choosable',
            requestData : {
                target_filter : JSON.stringify({
                    type : 'assigned',
                    targetTable : "T_EMC_GROUP_ITEM",
                    targetMatchColumn : "ITEMID",
                    filter : [ {
                        targetFilterColumn : 'itemGroupId',
                        targetFilterValue : _this.param.itemGroupId
                    } ]
                })
            },
            columns : columns,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().window.showMask(true);
                    var selections = Gikam.getComp('emc-order-item-edit-list-grid').getSelections()

                    var data = selections.map(function(item, index) {
                        return {
                            itemId : item.id,
                            orderItemGroupId : _this.param.orderItemGroupId
                        };
                    })

                    Gikam.put(Gikam.printf(emcOrder.baseUrl + '/item-groups/{orderItemGroupId}/action/edit-item', {
                        orderItemGroupId : _this.param.orderItemGroupId
                    }), Gikam.getJsonWrapper({}, [ '', data ])).done(function() {
                        Gikam.getLastModal().window.closeMask();
                        Gikam.getLastModal().close();
                    })

                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ],
            onLoadSuccess : function(rows) {
                if (!Gikam.isEmpty(_this.param.orderItemGroupId)) {
                    Gikam.post(Gikam.printf(emcOrder.baseUrl + '/item-groups/{orderItemGroupId}/items/queries', {
                        orderItemGroupId : _this.param.orderItemGroupId
                    })).done(function(items) {
                        items.rows.map(function(row) {
                            Gikam.getComp('emc-order-item-edit-list-grid').selectRowById(row.itemId);
                        });
                    });
                }
            }
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
