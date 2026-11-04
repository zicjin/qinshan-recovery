coreInternalAudit.choosePage = {

    getGridParam : function() {
        var _this = this, columns = coreInternalAudit.getChooseListColumns(_this.param.single);
        return {
            type : 'grid',
            id : 'core-internal-audit-choose-list-grid',
            url : coreInternalAudit.baseUrl + '/queries/choosable',
            requestData : _this.param,
            columns : columns,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('core-internal-audit-choose-list-grid').getSelections());
                }
            } ]
        }
    },

    create : function() {
        Gikam.create('layout', {
            id : 'core-internal-audit-choose-list-layout',
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
}