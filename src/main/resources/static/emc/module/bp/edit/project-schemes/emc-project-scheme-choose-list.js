emcProjectScheme.choosePage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-project-scheme-choose-list-grid',
            url : emcProjectScheme.schemeUrl + '/queries',
            columns : emcProjectScheme.getSchemeEditListColumns(),
            readonly : true,
            page : true,
            requestData : _this.param,
            order : {
                'createdTime' : 'desc'
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-project-scheme-choose-list-grid').getSelections());
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
            id : 'emc-project-scheme-choose-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
