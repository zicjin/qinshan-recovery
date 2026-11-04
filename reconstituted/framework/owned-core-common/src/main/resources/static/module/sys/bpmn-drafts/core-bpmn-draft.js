var coreBpmnDraft = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_BPMN_DRAFT.PROCCODE',
            field : 'procCode',
            width : 150,
            type : 'link',
            search : true
        }, {
            title : 'T_CORE_BPMN_DRAFT.SERVICENAME',
            field : 'ext$.servicename',
            width : 150,
            genericQuery : true,
            search : true
        }, {
            title : 'T_CORE_BPMN_DRAFT.USEDORGNAME',
            field : 'ext$.usedorgname',
            width : 300,
            search : true
        }, {
            title : 'T_CORE_BPMN_DRAFT.EXPRESSION',
            field : 'expression',
            width : 300,
            search : true
        }, {
            title : 'T_CORE_BPMN_DRAFT.PROCVERSION',
            field : 'procVersion',
            width : 100
        } ]
    },

    getBaseInfoFormFields : function() {
        return [ {
            title : 'T_CORE_BPMN_DRAFT.ID',
            field : 'id',
            readonly : true
        }, {
            title : 'T_CORE_BPMN_DRAFT.PROCCODE',
            field : 'procCode',
            validators : [ 'strLength(1,32]', 'notEmpty' ],
            preInsert : true,
        }, {
            title : 'T_CORE_BPMN_DRAFT.SERVICEID',
            field : 'serviceId',
            preInsert : true,
            validators : [ 'notEmpty' ],
            type : 'select',
            category : 'menuExt',
            param : {
                type : 'audit_def'
            },
            search : true
        }, {
            title : 'T_CORE_BPMN_DRAFT.EXPRESSION',
            field : 'expression',
            colspan : 2
        }, {
            title : 'T_CORE_BPMN_DRAFT.PROCVERSION',
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
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_BPMN_DRAFT.USEDORGID',
            field : 'usedOrgId',
            width : 200,
            sort : false
        }, {
            title : 'T_CORE_BPMN_DRAFT.USEDORGNAME',
            field : 'ext$.usedorgname',
            width : 500,
            sort : false
        } ]
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/bpmn-drafts'
});