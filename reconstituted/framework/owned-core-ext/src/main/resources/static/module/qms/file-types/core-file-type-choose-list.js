coreFileType.choosePage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-file-type-choose-list-grid',
            url : coreFileType.baseUrl + '/queries/choosable',
            requestData : _this.param,
            columns : coreFileType.getChooseListColumns(_this.param.single),
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('core-file-type-choose-list-grid').getSelections());
                }
            } ]
        }
    },

    create : function() {
        Gikam.create('layout', {
            id : 'core-file-type-choose-list-layout',
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
