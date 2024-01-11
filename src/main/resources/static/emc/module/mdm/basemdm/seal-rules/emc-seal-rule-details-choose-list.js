emcSealRule.detailsChoosePage = {

    getGridParam : function() {
        var _this = this, columns = emcSealRule.getRuleDetailsChooseListColumns();
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-seal-rule-details-choose-list-grid',
            url : emcSealRule.baseUrl + '/rule-detailss/queries',
            requestData : _this.param,
            columns : columns,
            onCellClick : function(field, row) {
                field === 'ext$.filename' && Gikam.openFile(row.ext$.fileid, true);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-seal-rule-details-choose-list-grid').getSelections());
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
