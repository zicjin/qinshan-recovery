corePageField.choosePage = {

    getPageFieldParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-page-field-choose-list-grid',
            service : 'corePageFieldServiceImpl',
            url : corePageField.baseUrl + '/queries/choosable',
            columns : corePageField.getChooseListColumns(),
            filterOpen : true,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('core-page-field-choose-list-grid').getSelections());
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
            center : {
                width : 300,
                items : [ _this.getPageFieldParam() ]
            },
            renderTo : workspace.window.$dom,
        });
    },

    init : function() {
        this.create();
    }
};
