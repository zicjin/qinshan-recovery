emcOrderReceipt.qcChoosePage = {

    getGridParam : function() {
        var _this = this, columns = emcOrderReceipt.getQcCategoryChooseListColumns();
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        if (_this.param.addNum) {
            columns.push({
                field : 'addNum',
                title : 'T_EMC_QC_CATEGORY.ADDNUM',
                type : 'number',
                isInteger : true,
                editor : true
            });
        }
        var url = Gikam.printf(emcOrderReceipt.baseUrl + '/qc/{category}/queries/choosable', {
            category : _this.param.category
        });
        return {
            type : 'grid',
            id : 'emc-order-receipts-qc-choose-list-grid',
            url : url,
            columns : columns,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var selections = Gikam.getComp('emc-order-receipts-qc-choose-list-grid').getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.SELECTONE');
                        return;
                    }
                    Gikam.getLastModal().close(selections);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    getBtnToolbarParam : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'btnGroup',
                items : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CONFIRM',
                    icon : 'select',
                    onClick : function() {
                        Gikam.getLastModal().close(Gikam.getComp('emc-order-receipts-qc-choose-list-grid').getSelections());
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CANCEL',
                    icon : 'cancel',
                    onClick : function() {
                        Gikam.getLastModal().close();
                    }
                } ]
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
