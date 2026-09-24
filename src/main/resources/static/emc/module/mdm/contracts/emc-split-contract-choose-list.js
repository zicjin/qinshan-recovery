emcContract.splitChoosePage = {

    getGridParam : function() {
        var _this = this, columns = emcContract.getSplitChooseListColumns(), requestData = _this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        requestData['org_filter'] = '1';
        requestData['status_SEQ'] = '1';

        return {
            type : 'grid',
            id : 'emc-split-contract-choose-list-grid',
            url : '/secure/emc/module/mdm/contract/split-contracts/queries/choosable',
            requestData : requestData,
            order : {
                contractId : 'desc',
                id : 'asc'
            },
            page : true,
            columns : columns,
            group: {
                fields: ['contractId'],
                formatter: function (row) {
                    return row.ext$.contractno;
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-split-contract-choose-list-grid').getSelections());
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-split-contract-choose-list-layout',
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
