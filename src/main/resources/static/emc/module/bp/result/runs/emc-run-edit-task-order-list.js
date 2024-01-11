emcRun.editTaskOrdertPage = {

    getGridParam : function() {
        var _this = this, columns = emcRun.getRunTaskOrderColumns(), requestData = this.param;
        return {
            type : 'grid',
            id : 'emc-run-edit-task-order-list-grid',
            service : 'emcOrderTaskServiceImpl',
            columns : emcRun.getRunTaskOrderColumns(),
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
                    Gikam.getLastModal().close(Gikam.getComp('emc-run-edit-task-order-list-grid').getSelections()); 
                }
            }, {
                type : 'button',
                text : '方法参数批样品录入1',
                icon : 'modify',
                onClick : function() {
                    var _this = this;
                    var selections = Gikam.getComp('emc-run-edit-task-equipt-list-grid').getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    var id = selections[0].id;

                    Gikam.create('modal', {
                        width : '60%',
                        title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.METHOD_PARAM_PROPERTY_SET',
                        url : '/secure/emc/module/mdm/bizmdm/tests/page/result-order-task/method-param-batch-order-detail'  + Gikam.param({
                            id : id,
                            flag1 : 1
                        }),
                    });
                  }
            }, {
                type : 'button',
                text : '方法参数批样品录入2',
                icon : 'modify',
                onClick : function() {
                    var _this = this;
                    var selections = Gikam.getComp('emc-run-edit-task-equipt-list-grid').getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    var id = selections[0].id;
                    Gikam.create('modal', {
                        width : '60%',
                        title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.METHOD_PARAM_PROPERTY_SET',
                        url : '/secure/emc/module/mdm/bizmdm/tests/page/result-order-task/method-param-batch-order-detail'  + Gikam.param({
                            id : id,
                            flag1 : 0
                        }),
                    });
                  }
            }],
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
