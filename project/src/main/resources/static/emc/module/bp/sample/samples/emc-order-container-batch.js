emcSample.orderContainerBatchPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var formData = Gikam.getComp('emc-folder-order-container-batch-form').getData();
                    Gikam.getLastModal().close(formData);
                }
            } ]
        }
    },

    getBaseInfoForm : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-folder-order-container-batch-form',
            fields : emcSample.getOrderContainerFormFields(),
            columns : 1,
            onRendered : function(){
                Gikam.getComp('emc-folder-order-container-batch-form').setData({
                    containerGroup : _this.param.containerGroup,
                    containerCategoryId : _this.param.containerCategoryId,
                    containerCategoryCode : _this.param.containerCategoryCode,
                    containerCategoryName : _this.param.containerCategoryName,
                    fixative : _this.param.fixative,
                    storeCondition : _this.param.storeCondition,
                });

            },
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