emcSample.orderDescDetailPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var formData = Gikam.getComp('emc-sample-folder-dbs-desc-detail-form').getData();
                    Gikam.getLastModal().close(formData);
                }
            } ]
        }
    },
    getBaseInfoForm : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-sample-folder-dbs-desc-detail-form',
            fields : emcSample.getFolderDbsDescFormFields(),
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

    init : function(param) {
        this.param = param;
        this.createPage();
    },

}