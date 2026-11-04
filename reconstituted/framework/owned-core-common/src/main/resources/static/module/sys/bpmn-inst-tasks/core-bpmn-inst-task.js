var coreBpmnInstTask = Gikam.getPageObject({

    getSearchListColumns : function() {
        return [ {
            radio : true
        }, {
            title : 'T_CORE_BPMN_INST_TASK.TASKNAME',
            field : 'taskName',
            width : 200
        }, {
            title : 'T_CORE_BPMN_INST_TASK.STATUSCODE',
            field : 'statusCode',
            width : 230
        } ]
    },
    
    getTaskUserListColumns : function() {
        return [ {
            checkbox : true
        }, {
            title : 'T_CORE_BPMN_INST_TASK_USER.USERID',
            field : 'userId'
        }, {
            title : 'T_CORE_BPMN_INST_TASK_USER.USERNAME',
            field : 'ext$.username',
            width : 180
        } ]
    },
    
    getTaskRoleListColumns : function() {
        return [ {
            checkbox : true
        }, {
            title : 'T_CORE_BPMN_INST_TASK_ROLE.ROLENAME',
            field : 'ext$.rolename',
            width : 300
        } ]
    },
    
    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/bpmn-inst-tasks'
});