coreQualityControlPlan.choosePage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-quality-control-plan-choose-list-grid',
            url : coreQualityControlPlan.baseUrl + '/queries/choosable',
            requestData : _this.param,
            columns : coreQualityControlPlan.getChooseListColumns(_this.param.single),
            toolbar : [{
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('core-quality-control-plan-choose-list-grid').getSelections());
                }
            }]
        }
    },

    create : function() {
        Gikam.create('layout', {
            id : 'core-quality-control-plan-choose-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [this.getGridParam()]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
