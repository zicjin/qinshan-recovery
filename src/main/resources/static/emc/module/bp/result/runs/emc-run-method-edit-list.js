emcRun.methodEditPage = {

    getOrderTaskParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-run-method-edit-order-task-grid',
            columns : emcRun.getMethodEditColumns(),
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-task',
            requestData : {
                status_SEQ : 'logged',
                methodId_SEQ : _this.param.methodId,
                testId_SEQ : _this.param.testId,
                analysisType_SEQ : 'analysis',
                orderCategory_SEQ : _this.param.orderCategory,
                runId_IN : ',',
                projId_IN : _this.param.projId
            },
            page : true,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var leftGrid = Gikam.getComp('emc-run-method-edit-order-task-grid').getSelections();
                    var rightGrid = Gikam.getComp('emc-run-method-edit-method-grid').getSelections();
                    if (Gikam.isEmpty(leftGrid)) {
                        Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.ORDER_CAN_NOT_EMPTY');
                        return;
                    }
                    if (Gikam.isEmpty(rightGrid)) {
                        Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.METHOD_CAN_NOT_EMPTY');
                        return;
                    }
                    Gikam.getLastModal().close([ leftGrid, rightGrid ]);
                }
            } ]
        }
    },

    getMethodParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-run-method-edit-method-grid',
            columns : emcRun.getSchemeMethodChooseListColumns(),
            url : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/monitor-tmpls/schemes/queries/choosable',
            requestData : {
                methodId_DIFFER : _this.param.methodId,
                testId_SEQ : _this.param.testId,
                monitorTmplName_SEQ : _this.param.orderCategory,
                orgId_SB : workspace.user.ext$.parentorgid,
                isGroupByTestMethod : '1'
            }
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            west : {
                width : '60%',
                items : [ this.getOrderTaskParam() ]
            },
            center : {
                items : [ this.getMethodParam() ]
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
