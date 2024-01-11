emcContract.copyPage = {

    getGridParam : function() {
        var _this = this, columns = emcContract.getCopyListColumns();
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-contract-copy-list-grid',
            url : emcContract.baseUrl + '/queries/choosable',
            requestData : _this.param,
            columns : columns,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-contract-copy-list-grid').getSelections());
                }
            }, {
                type : 'form',
                id : 'emc-contract-copy-list-form',
                fields : emcContract.getCopyListField(),
                autoSave : false,
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-contract-copy-list-layout',
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
