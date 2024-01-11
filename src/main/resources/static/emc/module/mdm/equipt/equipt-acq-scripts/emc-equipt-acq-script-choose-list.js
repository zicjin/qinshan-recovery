emcEquiptAcqScript.choosePage = {

    getChooseGridParam : function() {
        var _this = this, columns = emcEquiptAcqScript.getChooseListColumns();
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-equipt-acq-script-choose-list-grid',
            url : emcEquiptAcqScript.baseUrl + '/queries/choosable',
            requestData : _this.param,
            columns : columns,
            checkOnActive : _this.param.single,
            page : true,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-equipt-acq-script-choose-list-grid').getSelections());
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
