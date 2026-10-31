emcRun.editTaskEquiptPage = {

    getGridParam : function() {
        var _this = this, columns = emcRun.getEquiptOrderTaskListColumns(), requestData = this.param;
        return {
            type : 'grid',
            id : 'emc-run-edit-task-equipt-list-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries',
            service : 'emcOrderTaskServiceImpl',
            requestData : requestData,
            order : {
                'testName' : 'desc',
                'cupNo' : 'asc'
            },
            columns : columns,
            page : true,
            pageSize : 100,
            group : {
                fields : [ 'testName' ],
                formatter : function(row) {
                    return row.testName;
                }
            },
            toolbar : [ {
                type : 'button',
                text : '通用批录入',
                icon : 'modify',
                onClick : function() {
                    _this.batchInput();
                }
            }, {
                type : 'button',
                text : '有机批录入',
                icon : 'modify',
                onClick : function() {
                    _this.batchInputForOrder();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            }, {
                type : 'button',
                text : '方法参数1批录入',
                icon : 'modify',
                onClick : function() {
                    var _this = this;
                    var selections = Gikam.getComp('emc-run-edit-task-equipt-list-grid').getSelections();
                    var id = selections[0].id;
                    Gikam.create('modal', {
                        width : '60%',
                        title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.METHOD_PARAM_PROPERTY_SET',
                        url : '/secure/emc/module/mdm/bizmdm/tests/page/result-order-task/method-param-batch-detail'  + Gikam.param({
                            id : id,
                            flag1 : 1
                        }),
                    });
                  }
            }, {
            //     type : 'button',
            //     text : '方法参数2批录入',
            //     icon : 'modify',
            //     onClick : function() {
            //         var _this = this;
            //         var selections = Gikam.getComp('emc-run-edit-task-equipt-list-grid').getSelections();
            //         var id = selections[0].id;
            //         Gikam.create('modal', {
            //             width : '60%',
            //             title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.METHOD_PARAM_PROPERTY_SET',
            //             url : '/secure/emc/module/mdm/bizmdm/tests/page/result-order-task/method-param-batch-detail'  + Gikam.param({
            //                 id : id,
            //                 flag1 : 0
            //             }),
            //         });
            //       }
            // }, {
                type : 'button',
                text : '有机同步仪器id',
                icon : 'modify',
                onClick : function() {
                    var _this = this;
                    var row = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow();
                    console.log(row);
                    var url = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/sync-no-equiptid-task';
                    Gikam.post(url, Gikam.getJsonWrapper(null, [ 'emcRunService', [row] ])).done(function(data) {
                    	Gikam.toast('同步成功', 3, 'top');
                        Gikam.getComp('emc-run-edit-task-equipt-list-grid').refresh();
                    });
                  }
            } ]
        }
    },

    batchInput : function() {
        var _this = this;
        var grid = Gikam.getComp('emc-run-edit-task-equipt-list-grid');
        var rows = grid.getSelections();
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            width : '70%',
            title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.CHOOSE_EQUIPT',
            url : emcRun.baseUrl + '/page/edit-equipt' + Gikam.param({
                testMethodId : _this.param.testMethodId,
                equiptId : _this.param.equiptId,
            }),
            onAfterClose : function(data) {
            	//这边按测试来修改仪器
                if (!Gikam.isEmpty(data)) {
                    var equiptId = data[0].id;
                    workspace.window.showMask(true);
                    Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/batch-update-order-task', Gikam.getJsonWrapper(
                    		{
                                f : {
                                	equiptId : equiptId
                                }

                    		},[ 'pfOrderTaskServiceImpl', rows ])).done(function() {
                    }).always(function() {
                        workspace.window.closeMask();
                		Gikam.getComp('emc-run-edit-task-equipt-list-grid').refresh();
                    });
                }
            }
        });
    },

    batchInputForOrder : function() {
        var _this = this;
        var rows = Gikam.getComp('emc-run-edit-task-equipt-list-grid').getSelections();
        var selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();

        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var id = selections[0].id;

        Gikam.create('modal', {
            width : '50%',
            title : '样品',
            url : emcRun.baseUrl + '/page/run-task-order' + Gikam.param({
                runId : id
            }),
            onAfterClose : function(chooseData) {
                if (Gikam.isEmpty(chooseData)) {
                    workspace.window.closeMask();
                    return;
                }
                Gikam.create('modal', {
                    width : '70%',
                    title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.CHOOSE_EQUIPT',
                    url : emcRun.baseUrl + '/page/edit-equipt' + Gikam.param({
                        testMethodId : _this.param.testMethodId,
                        equiptId : _this.param.equiptId,
                    }),
                    onAfterClose : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            var equiptId = data[0].id;
                            workspace.window.showMask(true);
                            Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/batch-update-for-order', Gikam.getJsonWrapper(
                            		{
                                        f : {
                                        	equiptId : equiptId
                                        }

                            		},[ 'pfOrderTaskServiceImpl', chooseData ])).done(function() {
                            }).always(function() {
                                workspace.window.closeMask();
                        		Gikam.getComp('emc-run-edit-task-equipt-list-grid').refresh();
                            });
                        }
                    }
                });
            }
        });



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
