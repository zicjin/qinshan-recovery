emcPretreatment.extractMethodPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-extract-method-list-grid',
            service : 'emcExtractMethodServiceImpl',
            columns : emcPretreatment.getExtractMethodListColumns(),
            url : emcPretreatment.baseUrl + '/extract-methods/queries',
            page : true,
            filterOpen : true,
	        requestData : _this.param,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.getComp('emc-extract-method-list-grid').insert(emcPretreatment.baseUrl + '/extract-methods/action/insert', [{pretreatmentId : _this.param.pretreatmentId_SEQ}]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-extract-method-list-grid').deleteRows(emcPretreatment.baseUrl + '/extract-methods/action/delete');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-extract-method-list-grid').getSelections());
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
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
