emcMtl.abandonPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'pass',
                onClick : function() {
                    _this.save();
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'form',
            columns : 1,
            id : 'emc-mtl-abandon-base-info-form',
            fields : emcMtl.getStockAbandonFormFields(),
        }
    },

    save : function() {
        var _this = this;
        var form = Gikam.getComp('emc-mtl-abandon-base-info-form');
        if(form.validate()) {
            data = form.getData();
            var selections = Gikam.getComp('emc-mtl-detail-stock-list-grid').getSelections();
            var stockId = selections.map(function(value, index) {
                return value.id;
            });
            data.ext$.stockId = stockId.join(',');
            Gikam.getLastModal().window.showMask(true);
            Gikam.put(Gikam.printf(emcMtl.baseUrl + '/{id}/stocks/action/abandon', {
                id : Gikam.getComp('emc-mtl-edit-list-grid').getActivedRow().id,
            }), Gikam.getJsonWrapper(null, [ '', [ data ] ])).always(function() {
                Gikam.getComp('emc-mtl-edit-list-grid').refresh();
                Gikam.getLastModal().close();
                Gikam.getLastModal().window.closeMask();
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