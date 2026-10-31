emcMtl.newStockChoosePage = {

    getGridParam : function() {
        var _this = this, requestData = this.param, columns = requestData.projectFlag=='1'? emcMtl.getProjectStockChooseListColumns():emcMtl.getStockChooseListColumns();
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-mtl-stock-choose-list-grid',
            url : emcMtl.baseUrl + '/queries/detail-choosable',
            requestData : requestData,
            columns : columns,
            filterOpen : true,
            page : true,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-mtl-stock-choose-list-grid').getSelections());
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            }, {
                type : 'form',
                autoSave : false,
                fields : emcMtl.getPrepareShowAllField()
            } ],
            group : {
                fields : [ 'ext$.mtlcategoryname' ],
                formatter : function(row) {
                    return row.ext$.mtlcategoryname;
                }
            }
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
