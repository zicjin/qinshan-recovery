emcEquiptAcqScript.resultListPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-equipt-acq-script-analy-result-list-grid',
            url : Gikam.printf(emcEquiptAcqScript.baseUrl + '/{id}/action/analy-result-test/{fileId}', {
                id : _this.param.id,
                fileId : _this.param.fileId,
            }),
            columns : emcEquiptAcqScript.getAnalyResultColumns(),
            toolbar : [ {
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
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};