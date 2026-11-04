coreRiskFactor.choosePage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'treeGrid',
            id : 'core-risk-factor-choose-grid',
            url : coreRiskFactor.baseUrl + '/tree-grid',
            requestData : _this.param,
            columns : coreRiskFactor.getChooseListColumns(),
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('core-risk-factor-choose-grid').getSelections());
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-risk-factor-choose-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
}