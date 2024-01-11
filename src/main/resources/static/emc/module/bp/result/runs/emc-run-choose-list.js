emcRun.choosePage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-run-choose-list-grid',
            url : emcRun.baseUrl + '/queries',
            columns : emcRun.getTestRunColumns(),
            readonly : true,
            page : true,
            service : 'emcRunServiceImpl',
            requestData : _this.param,
            order : {
                'createdTime' : 'desc'
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-run-choose-list-grid').getSelections());
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
            id : 'emc-run-choose-list-layout',
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
