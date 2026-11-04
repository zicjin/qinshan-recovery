var coreScheduleTaskConfig = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/schedule-task-configs',

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_SCHEDULE_TASK_CONFIG.ID',
            readonly : true,
            titleWidth : 120,
        }, {
            field : 'ext$.taskname',
            title : 'T_CORE_SCHEDULE_TASK.TASKNAME',
            readonly : true,
        }, {
            field : 'uid',
            title : 'T_CORE_SCHEDULE_TASK_CONFIG.UNIQUEID',
            validators : [ 'notEmpty', 'strLength[1,96]', 'remote/secure/core/module/sys/schedule-task-configs/{id}/action/validate-unique' ],
            titleWidth : 120,
        }, {
            field : 'cron',
            title : 'T_CORE_SCHEDULE_TASK_CONFIG.CRON',
            validators : [ 'notEmpty', 'strLength[1,96]' ],
            type : 'cron'
        }, {
            field : 'startTime',
            title : 'T_CORE_SCHEDULE_TASK_CONFIG.STARTTIME',
            editable : false,
            type : 'dateTime',
            titleWidth : 120,
        }, {
            field : 'endTime',
            title : 'T_CORE_SCHEDULE_TASK_CONFIG.ENDTIME',
            editable : false,
            type : 'dateTime'
        }, {
            field : 'param',
            title : 'T_CORE_SCHEDULE_TASK_CONFIG.PARAM',
            validators : [ 'strLength[0,128]' ],
            colspan : 2,
            titleWidth : 120,
        }, {
            field : 'remark',
            title : 'T_CORE_SCHEDULE_TASK.REMARK',
            type : 'textarea',
            validators : [ 'strLength[0,256]' ],
            colspan : 2,
            titleWidth : 120,
        } ]
    },

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_SCHEDULE_TASK_CONFIG.ID',
            field : 'id',
            type : 'link',
            genericQuery : true
        }, {
            title : 'T_CORE_SCHEDULE_TASK.TASKNAME',
            field : 'ext$.taskname',
            genericQuery : true
        }, {
            title : 'T_CORE_SCHEDULE_TASK_CONFIG.UNIQUEID',
            field : 'uid',
            genericQuery : true
        }, {
            title : 'T_CORE_SCHEDULE_TASK_CONFIG.CRON',
            field : 'cron',
            genericQuery : true
        }, {
            title : 'T_CORE_SCHEDULE_TASK_CONFIG.STARTTIME',
            field : 'startTime'
        }, {
            title : 'T_CORE_SCHEDULE_TASK_CONFIG.ENDTIME',
            field : 'endTime'
        }, Gikam.status.getActiveColumn() ]
    },

    getSimpleSearchListColumns : function() {
        return [ {
            radio : true
        }, {
            index : true
        }, {
            title : 'T_CORE_SCHEDULE_TASK.SCHEDULETASKNAME',
            field : 'ext$.taskname',
            type : 'link',
            genericQuery : true,
            width : 150
        }, {
            title : 'T_CORE_SCHEDULE_TASK_CONFIG.UID',
            field : 'uid',
            genericQuery : true
        }, Gikam.status.getActiveColumn() ]
    },

    getTaskLogSearchListColumns : function() {
        return [ {
            index : true
        }, {
            title : 'T_CORE_SCHEDULE_TASK_LOG.ID',
            field : 'id',
            type : 'link',
            genericQuery : true,
            width : 150
        }, {
            title : 'T_CORE_SCHEDULE_TASK_LOG.SUCCESS',
            field : 'success',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'success'
            },
            genericQuery : true
        }, {
            title : 'T_CORE_SCHEDULE_TASK_LOG.EXECUTEDRESULT',
            field : 'executedResult',
            width : 200
        }, {
            title : 'T_CORE_SCHEDULE_TASK_LOG.STARTTIME',
            field : 'startTime',
            width : 150
        }, {
            title : 'T_CORE_SCHEDULE_TASK_LOG.ENDTIME',
            field : 'endTime',
            width : 150
        }, {
            title : 'T_CORE_SCHEDULE_TASK_LOG.PARAM',
            field : 'param'
        }, {
            title : 'T_CORE_SCHEDULE_TASK_LOG.CRON',
            field : 'cron'
        } ]
    },
});