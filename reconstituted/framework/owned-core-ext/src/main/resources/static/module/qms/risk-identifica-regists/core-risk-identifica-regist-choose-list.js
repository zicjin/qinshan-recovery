coreRiskIdentificaRegist.choosePage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-risk-identifica-regist-choose-list-grid',
            url : coreRiskIdentificaRegist.baseUrl + '/queries/choosable',
            requestData : _this.param,
            columns : coreRiskIdentificaRegist.getChooseListColumns(_this.param.single),
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('core-risk-identifica-regist-choose-list-grid').getSelections());
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-risk-identifica-regist-choose-list-layout',
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