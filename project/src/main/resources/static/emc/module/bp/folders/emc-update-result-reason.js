emcFolder.updateResultReasonPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var formData = Gikam.getComp('emc-update-result-reason-form').getData();
                    if(Gikam.isEmpty(formData.reason)) {
                        Gikam.alert('EMC.MODULE.BP.BP.FOLDERS.TIP.REASON_IS_NECESSARY');
                        return;
                    }
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
            id : 'emc-update-result-reason-form',
            fields : emcFolder.getUpdateResultReasonFields(),
            columns : 1,
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