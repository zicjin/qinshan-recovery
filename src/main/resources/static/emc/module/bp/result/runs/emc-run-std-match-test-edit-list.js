emcRun.stdMatchTestPage = {

    getGridParam : function() {
        var _this = this, 
        columns = emcRun.getStdMatchTestListColumns(), 
        requestData = this.param;
        var orderTaskRow = Gikam.getComp('emc-run-sample-test-grid').getActivedRow();
        var batchNo = this.param.batchNo;


        return {
            type : 'grid',
            id : 'emc-run-std-match-test-edit-list-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/raw',

            service : 'emcOrderTaskServiceImpl',
	        requestData : {
	        	orderId_SEQ : orderTaskRow.orderId,
                runId_SEQ : orderTaskRow.runId,
	        },
            page :true,
            columns : columns,
            onRendered : function() {
                var testMethodIds = '';
                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks' + '/action/select-match-stock-testmethodId', Gikam.getJsonWrapper({
                    f :{
                     	batchNo : batchNo
                     }
                 }, [ 'emcOrderTaskServiceImpl', [ orderTaskRow ] ])).done(function(data) {
                 	testMethodIds = data;
                    Gikam.getComp("emc-run-std-match-test-edit-list-grid").refresh({
                    	requestData : {
            	        	orderId_SEQ : orderTaskRow.orderId,
                            runId_SEQ : orderTaskRow.runId,
                            testMethodId_IN :testMethodIds,
                            TESTCAPCITYORGID_SEQ: workspace.user.ext$.parentorgid
                            
            	        },
                    });   
                 });


            },

            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                	var selections = Gikam.getComp('emc-run-std-match-test-edit-list-grid').getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                	var flag ='';
                    Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks' + '/action/select-over-test', Gikam.getJsonWrapper({
                        f : {
                            batchNo :  batchNo,
                        }
                    }, [ 'emcOrderTaskServiceImpl', selections  ])).done(function(data) {
                    	flag =data;
                    	   Gikam.confirm('删除测试结果',"是否删除标准样品下的:"+flag+"测试", function() {
                               workspace.window.showMask(true);
                               Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/result/runs' + '/action/edit-sync-QCSTD', Gikam.getJsonWrapper({
                                   f : {
                                       batchNo :  batchNo,
                                   }
                               }, [ 'emcOrderTaskServiceImpl', selections ])).done(function() {
                               }).always(function() {
                                   workspace.window.closeMask();
                                   Gikam.getComp('emc-run-sample-test-grid').refresh();
                                   Gikam.getComp('emc-run-result-grid').refresh();
                                   Gikam.getLastModal().close();
                               });;
                           })
                        Gikam.getComp('emc-run-sample-test-grid').refresh();
                    });
                 
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            }]
        }
    },


    
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [  this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};