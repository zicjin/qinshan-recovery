emcMtl.recievePage = {

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
            id : 'emc-mtl-recieve-base-info-form',
            fields : emcMtl.getStockRecieveFormFields(),
        }
    },

    save : function() {
        var _this = this;
        var form = Gikam.getComp('emc-mtl-recieve-base-info-form');
        if(form.validate()) {
            let data = form.getData();
            var selections = Gikam.getComp('emc-mtl-detail-stock-list-grid').getSelections();
            let outStockList = selections.filter(r=>r.stockQty < data.qty);
            if (Gikam.isNotEmpty(outStockList)) {
                Gikam.alert('EMC.MODULE.MDM.BASEMDM.MTLS.STOCK.TIP.OUT_OF_STOCK');
                return;
            }
            var insertData = selections.map(function(value, index) {
                data.stockId = value.id;
                return data;
            });
            Gikam.getLastModal().window.showMask(true);
            Gikam.put(Gikam.printf(emcMtl.baseUrl + '/stocks/{id}/action/recieve', {
                id : selections[0].id
            }), Gikam.getJsonWrapper({}, [ null, insertData ])).always(function() {
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
