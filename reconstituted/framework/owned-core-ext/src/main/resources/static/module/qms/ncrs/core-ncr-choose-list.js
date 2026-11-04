coreNcr.choosePage = {

    getGridParam : function() {
        var _this = this, columns = coreNcr.getChooseListColumns(_this.param.single);
        return {
            type : 'grid',
            id : 'core-ncr-choose-list-grid',
            url : coreNcr.baseUrl + '/queries/choosable',
            requestData : _this.param,
            columns : columns,
            toolbar : [{
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('core-ncr-choose-list-grid').getSelections());
                }
            }]
        }
    },

    create : function() {
        Gikam.create('layout', {
            id : 'core-ncr-choose-list-layout',
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
