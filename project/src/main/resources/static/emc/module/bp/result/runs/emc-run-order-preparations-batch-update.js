emcOrderPreparations.orderPreparationsBatchUpdatePage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var formData = Gikam.getComp('emc-run-order-preparations-batch-update-form').getData();
                    Gikam.getLastModal().close(formData);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    getBaseInfoForm : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-run-order-preparations-batch-update-form',
            fields : emcOrderPreparations.getOrderPreparationsBatchUpdateFields(),
            columns : 2,
        };
    },
    
    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoForm() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
        });
        return this;
    },

    init : function() {
        this.createPage();
    },

}