emcRunEdit.queryPage = {

    getBaseInfo : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-run-edit-query-form',
            fields : emcRunEdit.getResultQueryFields(),
            columns : 1,
        }
    },

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.SEARCH',
                icon : 'select',
                onClick : function() {
                    var data = Gikam.getComp('emc-run-edit-query-form').getData();
                    Gikam.getComp('emc-run-edit-edit-list-test-run-grid').refresh({
                        url : emcRunEdit.runUrl + '/queries/run-result',
                        requestData : {
                            id_SC : data.runId || '',
                            testName_SC : data.testName || '',
                            analysisBeginDate_DGOE : data.analysisBeginDate || '',
                            projNo_EQ : data.projNo || '',
                            analysisUserName_EQ : data.analysisUserName || '',
                            analysisEndDate_DLOE : data.analysisEndDate || '',
                            methodName_EQ : data.methodName || '',
                        }
                    });
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBaseInfo() ]
            },
            south : {
                height : '100',
                items : [ this.getBtnToolbar() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
};
