var coreBpmnProcessStatus = Gikam.getPageObject({
	getCommentListColumns : function() {
        return [ {
            index : true
        }, {
            title : 'CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.COMMENT.TASKNAME',
            field : 'taskName',
            sort : false,
            width : 150
        }, {
            title : 'CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.COMMENT.AUDITEDBYNAME',
            field : 'auditedByName',
            sort : false,
            width : 100
        }, {
            title : 'CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.COMMENT.AUDITEDTIME',
            field : 'auditedTime',
            sort : false,
            width : 150
        }, {
            title : 'CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.COMMENT.COMMENT',
            field : 'comment',
            sort : false,
            width : 300
        }, {
            title : 'CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.COMMENT.ROUTE',
            field : 'route',
            sort : false,
            width : 250
        }, {
            title : 'CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.COMMENT.ATTACHMENT',
            field : 'attachmentName',
            sort : false,
            width : 150,
            formatter : function(i, v, row) {
                if (v) {
                    return '<a href="javascript:;" onclick="Gikam.openFile(' + row.attachmentId + ', true);">' + v + '</a>';
                } else {
                    return '';
                }
            }
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
    
    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/bpmn-procs'
});