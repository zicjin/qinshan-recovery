var coreScheduleTaskLog = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/schedule-task-logs',

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_SCHEDULE_TASK_LOG.LOGID'
        }, {
            field : 'success',
            title : 'T_CORE_SCHEDULE_TASK_LOG.SUCCESS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'success'
            }
        }, {
            field : 'ext$.taskname',
            title : 'T_CORE_SCHEDULE_TASK.TASKNAME'
        }, {
            field : 'cron',
            title : 'T_CORE_SCHEDULE_TASK_LOG.CRON'
        }, {
            field : 'ext$.uid',
            title : 'T_CORE_SCHEDULE_TASK_CONFIG.CONFIGID'
        }, {
            field : 'ext$.configname',
            title : 'T_CORE_SCHEDULE_TASK_CONFIG.CONFIGNAME'
        }, {
            field : 'startTime',
            title : 'T_CORE_SCHEDULE_TASK_LOG.STARTTIME',
            type : 'date'
        }, {
            field : 'endTime',
            title : 'T_CORE_SCHEDULE_TASK_LOG.ENDTIME',
            type : 'date'
        }, {
            field : 'log',
            title : 'T_CORE_SCHEDULE_TASK_LOG.LOG',
            type : 'textarea',
            colspan : 2,
            rows : 10,
            height : 430
        } ]
    }
});