emcRun.taskProcessShowOrgPage = {
		getGridParam : function() {
	        var _this = this;
	        requestData = this.param;
        	var analysisorgid = workspace.user.orgId;
	        return {
	            type : 'grid',
	            id : 'emc-task-process-show-org-edit-list',
                url :IFM_CONTEXT + '/secure/emc/module/bp/project/projects/action/task-assign-process/'+analysisorgid,
	            columns : emcRun.getRunTaskProcessOrgEditListColumns(),
	            service : 'PfProjectService',
	            page :true,
                toolbar : [ 
               	 {
                       type : 'button',
                       text : 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.BUTTON.TASK_LIST_PRINT',
                       icon : 'print',
                       onClick : function() {
                           Gikam.viewReport('deliveringLabel', Gikam.getComp('emc-task-process-show-org-edit-list').getSelections());
                       }
                   }
               ]
	        }
	    },



    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-run-task-process-org-edit-list-layout',
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
