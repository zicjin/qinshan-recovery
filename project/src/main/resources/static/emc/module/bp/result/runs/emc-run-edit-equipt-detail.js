emcRun.editEquiptDetailPage = {

    getEquiptGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-run-edit-equipt-detail-grid',
            url : emcRun.baseUrl + Gikam.printf('/equipt/{testMethodId}/{equiptId}', {
                testMethodId :  _this.param.testMethodId,
                equiptId : _this.param.equiptId
            }),
            columns : emcRun.getEquiptChooseListColumns(),
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var data = Gikam.getComp('emc-run-edit-equipt-detail-grid').getSelections();
                    if(Gikam.isEmpty(data)) {
                        Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CHOOSE_AT_LEAST_ONE_EQUIPT');
                        return;
                    }
                    Gikam.getLastModal().close(data);
                }
            } ]
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            id : 'emc-run-edit-equipt-detail-layout',
            center : {
                items : [ this.getEquiptGridParam() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
