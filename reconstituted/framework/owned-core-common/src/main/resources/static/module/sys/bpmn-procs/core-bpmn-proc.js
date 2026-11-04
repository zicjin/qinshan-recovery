var coreBpmnProc = Gikam.getPageObject({

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_BPMN_PROC.PROCCODE',
            field : 'procCode',
            type : 'link',
            search : true,
            width : 150
        }, {
            title : 'T_CORE_BPMN_PROC.SERVICENAME',
            field : 'ext$.servicename',
            search : true,
            width : 150
        }, {
            title : 'T_CORE_BPMN_PROC.USEDORGNAME',
            field : 'ext$.usedorgname',
            search : true,
            width : 300
        }, {
            title : 'T_CORE_BPMN_PROC.EXPRESSION',
            field : 'expression',
            search : true,
            width : 300
        }, {
            title : 'T_CORE_BPMN_PROC.PROCVERSION',
            field : 'procVersion',
            width : 100
        } ]
    },
    
    getChooseListColumns : function() {
        return [ {
            index : true
        }, {
            title : 'T_CORE_BPMN_PROC.PROCCODE',
            field : 'procCode',
            width : 150
        }, {
            title : 'T_CORE_BPMN_PROC.SERVICENAME',
            field : 'ext$.servicename',
            width : 150
        }, {
            title : 'T_CORE_BPMN_PROC.USEDORGNAME',
            field : 'ext$.usedorgname',
            width : 300
        }, {
            title : 'T_CORE_BPMN_PROC.EXPRESSION',
            field : 'expression',
            width : 300
        }, {
            title : 'T_CORE_BPMN_PROC.PROCVERSION',
            field : 'procVersion',
            width : 100
        } ]
    },

    getBaseInfoFormFields : function() {
        return [  {
            title : 'T_CORE_BPMN_PROC.ID',
            field : 'id'
        }, {
            title : 'T_CORE_BPMN_PROC.SERVICEID',
            field : 'serviceId',
            readonly : true,
            type : 'select',
            category : 'menuExt',
            param : {
                type:'audit_def'
            }
        }, {
            title : 'T_CORE_BPMN_PROC.PROCCODE',
            field : 'procCode',
            readonly : true
        }, {
            title : 'T_CORE_BPMN_PROC.EXPRESSION',
            field : 'expression',
            readonly : true,
            colspan : 2
        }, {
            title : 'T_CORE_BPMN_PROC.PROCVERSION',
            field : 'procVersion',
            readonly : true
        } ]
    },
    
    getTaskCandidatorListColumns : function() {
        return [ {
            index : true
        }, {
            title : 'CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.CANDIDATOR.TASKSTATUSCODE',
            field : 'statusCode',
            sort : false,
            width : 130
        }, {
            title : 'CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.CANDIDATOR.TASKNAME',
            field : 'taskName',
            sort : false,
            width : 150
        }, {
            title : 'CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.CANDIDATOR.SPECIAL',
            field : 'special',
            sort : false,
            width : 300
        }, {
            title : 'CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.CANDIDATOR.ALLUSER',
            field : 'allUser',
            sort : false,
            width : 200
        }, {
            title : 'CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.CANDIDATOR.ROLE',
            field : 'role',
            sort : false,
            width : 200
        }, {
            title : 'CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.CANDIDATOR.USER',
            field : 'user',
            sort : false,
            width : 200
        } ]
    },
    
    getSequenceFlowConditionListColumns : function() {
        return [ {
            index : true
        }, {
            title : 'CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.CANDIDATOR.SOURCETASK',
            field : 'sourceTask',
            sort : false,
            width : 150
        }, {
            title : 'CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.CANDIDATOR.TARGETTASK',
            field : 'targetTask',
            sort : false,
            width : 150
        }, {
            title : 'CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.CANDIDATOR.CONDITION_EXPRESSION',
            field : 'conditionExpression',
            sort : false,
            width : 500
        } ]
    },
    
    getUsedOrgListColumns : function() {
        return [ {
            index : true
        }, {
            title : 'T_CORE_BPMN_DRAFT.USEDORGID',
            field : 'usedOrgId',
            search : true
        }, {
            title : 'T_CORE_BPMN_DRAFT.USEDORGNAME',
            field : 'ext$.usedorgname',
            search : true
        } ]
    },
    
    
    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/bpmn-procs'
});