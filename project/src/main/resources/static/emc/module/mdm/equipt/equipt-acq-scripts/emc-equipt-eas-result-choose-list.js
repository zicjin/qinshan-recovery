emcEquiptAcqScript.easResultchoosePage = {

    getChooseGridParam : function() {
        var _this = this, columns = emcEquiptAcqScript.getEasResultChooseListColumns();
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });

        return {
            type : 'grid',
            id : 'emc-equipt-eas-result-choose-list-grid',
            url : emcEquiptAcqScript.baseUrl + '/eas-results/queries',
            requestData : _this.param,
            columns : columns,
            checkOnActive : _this.param.single,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-equipt-eas-result-choose-list-grid').getSelections());
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
            center : {
                items : [ this.getChooseGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
