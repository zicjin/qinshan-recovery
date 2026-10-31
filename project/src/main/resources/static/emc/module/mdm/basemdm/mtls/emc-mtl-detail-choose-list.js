emcMtl.detailChoosePage = {

    getGridParam : function() {
        var _this = this, columns = emcMtl.getDetailChooseListColumns(), requestData = this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        requestData.status_SEQ = '0';
        requestData.stockQty_NG = '0';
        return {
            type : 'grid',
            id : 'emc-mtl-detail-choose-list-grid',
            url : emcMtl.baseUrl + '/queries/detail-choosable',
            requestData : requestData,
            columns : columns,
            page : true,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-mtl-detail-choose-list-grid').getSelections());
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
