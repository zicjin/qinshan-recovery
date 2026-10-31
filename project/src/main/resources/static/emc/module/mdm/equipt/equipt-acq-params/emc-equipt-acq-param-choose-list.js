emcEquiptAcqParam.choosePage = {

    getChooseGridParam : function() {
        var _this = this, columns = emcEquiptAcqParam.getChooseListColumns();
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-equipt-acq-param-choose-list-grid',
            url : emcEquiptAcqParam.baseUrl + '/queries/choosable',
            columns : columns,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-equipt-acq-param-choose-list-grid').getSelections());
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