var coreScheduleTask = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/schedule-tasks',

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_SCHEDULE_TASK.ID',
            titleWidth : 150
        }, {
            field : 'taskName',
            title : 'T_CORE_SCHEDULE_TASK.TASKNAME',
            preInsert : true
        }, {
            field : 'serviceName',
            title : 'T_CORE_SCHEDULE_TASK.SERVICENAME',
            titleWidth : 150
        }, {
            field : 'methodName',
            title : 'T_CORE_SCHEDULE_TASK.METHODNAME',
            titleWidth : 160
        }, {
            field : 'ifExecutableServiceName',
            title : 'T_CORE_SCHEDULE_TASK.IFEXECUTABLESERVICENAME',
            titleWidth : 150
        }, {
            field : 'ifExecutableMethodName',
            title : 'T_CORE_SCHEDULE_TASK.IFEXECUTABLEMETHODNAME',
            titleWidth : 160
        }, {
            field : 'type',
            title : 'T_CORE_SCHEDULE_TASK.TYPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'scheduleTaskType'
            },
            titleWidth : 150
        }, {
            field : 'lcdpServiceMethod',
            type : 'choose',
            category : 'lcdp-method',
            single : true,
            customData : {
                METHODFLAG_SEQ : 'normal'
            },
            targetFields : [ {
                lcdpServiceMethod : 'methodPath'
            } ],
            title : 'T_CORE_SCHEDULE_TASK.LCDPSERVICEMETHOD',
            validators : [ 'strLength[0,512]' ]
        }, {
            field : 'remark',
            title : 'T_CORE_SCHEDULE_TASK.REMARK',
            type : 'textarea',
            titleWidth : 150,
            colspan : 2
        } ]
    },

    getSearchListColumns : function() {
        return [ {
            index : true
        }, {
            title : 'T_CORE_SCHEDULE_TASK.ID',
            field : 'id',
            type : 'link',
            genericQuery : true,
            search : true,
            width : 200
        }, {
            title : 'T_CORE_SCHEDULE_TASK.TASKNAME',
            field : 'taskName',
            genericQuery : true,
            search : true,
            width : 200
        }, {
            title : 'T_CORE_SCHEDULE_TASK.TYPE',
            field : 'type',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'scheduleTaskType'
            }
        }, {
            title : 'T_CORE_SCHEDULE_TASK.SERVICENAME',
            field : 'serviceName',
            genericQuery : true,
            search : true,
            width : 500
        }, {
            title : 'T_CORE_SCHEDULE_TASK.METHODNAME',
            field : 'methodName',
            genericQuery : true,
            search : true,
            width : 150
        } ]
    },

    getSimpleSearchListColumns : function() {
        return [ {
            radio : true
        }, {
            index : true
        }, {
            title : 'T_CORE_SCHEDULE_TASK.SCHEDULETASKNAME',
            field : 'taskName',
            type : 'link',
            genericQuery : true,
            search : true,
            width : 200
        } ]
    },

    getMaintainListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_SCHEDULE_TASK.SCHEDULETASKNAME',
            field : 'taskName',
            type : 'link',
            genericQuery : true,
            search : true,
            width : 200
        }, {
            title : 'T_CORE_SCHEDULE_TASK.EXECUTECRIPT',
            field : 'scheduleScript',
            type : 'link',
            formatter : function (i, v, r) {
                if (Gikam.isEmpty(r.taskScript)) {
                    return Gikam.propI18N('LCDP.MODULE.BPS.TITLE.SCRIPT_ADD');
                }else{
                    return Gikam.propI18N('LCDP.MODULE.BPS.TITLE.SCRIPT_EDIT');
                }
            }
        } ]
    },

    getConfigListColumns : function() {
        return [ {
            checkbox : true
        },{
            index : true
        }, {
            title : 'T_CORE_SCHEDULE_TASK_CONFIG.UID',
            field : 'uid',
            type : 'link',
            genericQuery : true,
            search : true
        }, {
            title : 'T_CORE_SCHEDULE_TASK_CONFIG.CRON',
            field : 'cron',
            genericQuery : true,
            search : true,
            validators : ['notEmpty']
        }, {
            title : 'T_CORE_SCHEDULE_TASK_CONFIG.PARAM',
            field : 'param',
            search : true
        }, {
            title : 'T_CORE_SCHEDULE_TASK_CONFIG.STARTTIME',
            field : 'startTime',
            width : 150
        }, {
            title : 'T_CORE_SCHEDULE_TASK_CONFIG.ENDTIME',
            field : 'endTime',
            width : 150
        }, Gikam.status.getActiveColumn() ]
    }
});