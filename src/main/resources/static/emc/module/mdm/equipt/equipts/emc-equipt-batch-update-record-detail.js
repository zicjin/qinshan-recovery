emcEquipt.barchUpdateRecordPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    if(Gikam.getComp('emc-equipt-batch-update-record-detail-form').validate()){
                        var formData = Gikam.getComp('emc-equipt-batch-update-record-detail-form').getData();
                        Gikam.getLastModal().close(formData);
                    }
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
            id : 'emc-equipt-batch-update-record-detail-form',
            fields : emcEquiptUseRecord.getBatchUpdateRecordFields(),
            columns : 1,
            titleWidth : 80
        }
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
