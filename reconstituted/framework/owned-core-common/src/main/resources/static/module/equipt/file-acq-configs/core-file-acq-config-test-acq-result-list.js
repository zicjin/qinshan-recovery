//@ sourceURL=core-file-acq-config-test-acq-result-list.js
coreFileAcqConfig.testAcqResultPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-file-acq-config-test-acq-result-list-grid',
            url : Gikam.printf(coreFileAcqConfig.baseUrl + '/{id}/action/test', {
                id : _this.param.id
            }),
            page : false,
            columns : coreFileAcqConfig.getAcqResultListColumns()
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-file-acq-config-test-acq-result-list-layout',
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
