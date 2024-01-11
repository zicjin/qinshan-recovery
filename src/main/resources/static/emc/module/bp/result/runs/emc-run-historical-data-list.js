emcRun.historicalDataPage = {

    getBaseGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-run-historical-data-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/queries',
            columns : emcRun.getHistoricalDataColumns(),
            requestData : {
                itemId_SEQ : _this.param.itemId,
                testId_SEQ : _this.param.testId,
                monitorPointName_SEQ : _this.param.monitorPointName,
                analysisDate_DGOE : _this.date,
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.SEARCH',
                icon : 'select',
                onClick : function() {
                    var data = Gikam.getComp('emc-run-historical-data-form').getData();
                    Gikam.getComp('emc-run-historical-data-grid').refresh({
                        url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/queries',
                        requestData : {
                            analysisDate_DGOE : data.beginDate,
                            analysisDate_DLOE : data.endDate,
                        }
                    });
                }
            } ]
        }
    },

    getBaseForm : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-run-historical-data-form',
            fields : emcRun.getHistoricalDataFields(),
            columns : 2
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            /*center : {
                items : [ this.getBaseForm(), this.getBaseGrid() ]
            },*/
            north : {
                height : '7%',
                items : [ this.getBaseForm() ]
            },
            center : {
                items : [ this.getBaseGrid() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;

        var date = new Date();
        this.date = date.setMonth(date.getMonth() - 3);
        this.createPage();
    }
}
