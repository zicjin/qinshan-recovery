emcRun.taskProcessPage = {
		getGridParam : function() {
	        var _this = this;
	        requestData = this.param;
	        return {
	            type : 'grid',
	            id : 'emc-run-task-process-show-edit-list',
                url :IFM_CONTEXT + '/secure/emc/module/bp/project/projects/action/task-process',
	            columns : emcRun.getRunTaskProcessEditListColumns(),
	            service : 'PfProjectService',
	            page : true,
                pageSize : 20,
                toolbar : [ 
                	 {
                        type : 'button',
                        text : 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.BUTTON.TASK_LIST_PRINT',
                        icon : 'print',
                        onClick : function() {
                            Gikam.viewReport('deliveringLabel', Gikam.getComp('emc-run-task-process-show-edit-list').getSelections());
                        }
                    }
                ]
	        }
	    },



    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-run-task-process-edit-list-layout',
            renderTo : workspace.window.$dom,
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
