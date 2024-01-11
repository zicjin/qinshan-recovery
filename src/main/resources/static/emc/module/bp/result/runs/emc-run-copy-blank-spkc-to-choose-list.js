emcRun.copyBlankSpkcToChoosePage = {

    getGridParam : function() {
        var _this = this, columns = emcRun.getCopyBlankSpkcToChooseListColumns(), requestData = this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });

        return {
            type : 'grid',
            id : 'emc-run-copy-blank-spkc-to-choose-list-grid',
            url : emcRun.baseUrl + '/blank-order-tasks/queries',
            requestData : requestData,
            columns : columns,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-run-copy-blank-spkc-to-choose-list-grid').getSelections());
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

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            id : 'emc-run-copy-blank-spkc-to-grid-layout',
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
