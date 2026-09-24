emcMtl.mtlRecievePage = {

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
            fields : emcMtl.getMtlStockRecieveFormFields(),
        }
    },

    save : function() {
        var _this = this;
        var form = Gikam.getComp('emc-mtl-recieve-base-info-form');
        if(form.validate()) {
            let data = form.getData();
            data.qty = 1;
            if (data.stockId == undefined) {
                Gikam.alert('请先选择库存');
                return;
            }
            Gikam.getLastModal().window.showMask(true);
            Gikam.put(Gikam.printf(emcMtl.baseUrl + '/stocks/{id}/action/recieve', {
                id : data.stockId
            }), Gikam.getJsonWrapper({}, [ null, [data] ])).always(function() {
                Gikam.getLastModal().close(data);
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
