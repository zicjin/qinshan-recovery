coreMenuGroup.choosePage = {
    getMenuGroupGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-menu-group-choose-list-grid',
            url : coreMenuGroup.baseUrl + '/queries/choosable',
            requestData : _this.param,
            columns : coreMenuGroup.getChooseListColumns(),
            filterOpen : true,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('core-menu-group-choose-list-grid').getSelections());
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
                items : [ this.getMenuGroupGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;

        this.create();
    }
};