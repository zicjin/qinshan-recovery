emcConsume.inventoryPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'save',
                onClick : function() {
                    _this.save();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CLOSE',
                icon : 'back',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'form',
            columns : 1,
            id : 'emc-consume-inventory-base-info-form',
            fields : emcConsume.getStockInventoryFormFields(),
        }
    },

    save : function(url) {
        var _this = this;
        var form = Gikam.getComp('emc-consume-inventory-base-info-form');
        if (form.validate()) {
            data = form.getData();
            var selections = Gikam.getComp('emc-consume-detail-stock-list-grid').getSelections();
            var stockId = selections.map(function(value, index) {
                return value.id;
            });
            data.ext$.stockId = stockId.join(',');
            Gikam.getLastModal().window.showMask(true);
            Gikam.put(Gikam.printf(emcConsume.baseUrl + '/{id}/stocks/action/inventory', {
                id : Gikam.getComp('emc-consume-edit-list-grid').getActivedRow().id,
            }), Gikam.getJsonWrapper(null, [ '', [ data ] ])).always(function() {
                Gikam.getLastModal().window.closeMask();
                Gikam.getLastModal().close();
            });
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
};