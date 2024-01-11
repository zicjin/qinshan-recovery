emcConsume.recievePage = {

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
            id : 'emc-consume-recieve-base-info-form',
            fields : emcConsume.getStockRecieveFormFields(),
        }
    },

    save : function() {
        var _this = this;
        var form = Gikam.getComp('emc-consume-recieve-base-info-form');
        if (form.validate()) {
            data = form.getData();
            var selections = Gikam.getComp('emc-consume-detail-stock-list-grid').getSelections();
            var insertData = selections.map(function(value, index) {
                data.stockId = value.id;
                return data;
            });
            Gikam.getLastModal().window.showMask(true);
            Gikam.put(Gikam.printf(emcConsume.baseUrl + '/stocks/{id}/action/recieve', {
                id : selections[0].id
            }), Gikam.getJsonWrapper({}, [ null, insertData ])).always(function() {
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