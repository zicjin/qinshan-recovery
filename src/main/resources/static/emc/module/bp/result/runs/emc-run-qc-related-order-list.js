emcRun.qcRelatedOrderPage = {

    getGridParam : function() {
        var _this = this, columns = this.param.relatedOrder ? emcRun.getQcRelatedOrderListColumns() : emcRun.getRelatedQcOrderListColumns(), requestData = this.param;
        return {
            type : 'grid',
            id : 'emc-run-qc-related-order-list-grid',
            url : emcRun.baseUrl + '/qc-related-order/queries',
            requestData : requestData,
            order : {
                relatedOrderNo : 'asc'
            },
            service : 'emcQcRelatedOrderServiceImpl',
            columns : columns,
            page : true,
            deleteFormatter : function(row) {
                return _this.param.relatedOrder ? row.relatedOrderNo : row.qcOrderNo || row.id;
            },
            toolbar : _this.getGridToolbar(_this.param)
        }
    },

    getGridToolbar : function (param) {
        var _this = this;
        var toolbars = [];
        if (Gikam.isEmpty(param) || Gikam.isEmpty(param.canDelete)) {
            return toolbars;
        }
        if (param.canDelete) {
            toolbars.push({
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.UNLINK_ORDER',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-run-qc-related-order-list-grid').deleteRows(emcRun.baseUrl + '/qc-related-order');
                }
            })
        }
        return toolbars;
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
