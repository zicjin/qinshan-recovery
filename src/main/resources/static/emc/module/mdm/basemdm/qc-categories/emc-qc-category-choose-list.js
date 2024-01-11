emcQcCategory.choosePage = {

    getGridParam : function() {
        var _this = this, columns = emcQcCategory.getChooseListColumns(), requestData = this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-qc-category-choose-list-grid',
            url : emcQcCategory.baseUrl + '/queries/choosable',
            requestData : requestData,
            columns : columns,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-qc-category-choose-list-grid').getSelections());
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};