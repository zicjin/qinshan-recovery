emcRunEdit.editTempOrdertPage = {

    getGridParam : function() {
        var _this = this, columns = emcRunEdit.getRunTaskOrderColumns(), requestData = this.param;
        return {
            type : 'grid',
            id : 'emc-run-edit-temp-order-list-grid',
            service : 'emcOrderTaskServiceImpl',
            columns : emcRunEdit.getRunTaskOrderColumns(),
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
            requestData : {
                status_SEQ : 'logged',
                runId_SEQ : _this.param.runId
            },
            toolbar : [  {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'edit',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-run-edit-temp-order-list-grid').getSelections()); 
                }
            },],
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
