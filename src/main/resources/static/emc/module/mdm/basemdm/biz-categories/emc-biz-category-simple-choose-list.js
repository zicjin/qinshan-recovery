emcBizCategory.simpleChoosePage = {

    getGridParam : function() {
        var _this = this, columns = emcBizCategory.getChooseableColumns(), requestData = this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-biz-category-simple-choose-list-grid',
            url : emcBizCategory.baseUrl + '/queries/choosable',
            requestData : requestData,
            columns : columns,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-biz-category-simple-choose-list-grid').getSelections());
                }
            } ]
        }
    },


    create : function() {
        var _this = this;
        Gikam.create('layout', {
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
