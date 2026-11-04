var coreSystemMonitorErrorlog = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index: true
        }, {
            field : 'logDesc',
            title : 'T_CORE_SYSTEM_MONITOR_ERRORLOG.LOGDESC',
            width : 500,
            type: 'text',
            styleFormatter : function () {
                return {
                    height:'72px'
                }
            },
        }, {
            field : 'logModule',
            title : 'T_CORE_SYSTEM_MONITOR_ERRORLOG.LOGMODULE',
            contentAlign : 'center',
            width : 120,
        }, {
            field : 'logInterfaceName',
            title : 'T_CORE_SYSTEM_MONITOR_ERRORLOG.LOGINTERFACENAME',
            width : 320,
        },
            {
            field : 'logTime',
            title : 'T_CORE_SYSTEM_MONITOR_ERRORLOG.LOGTIME',
            width : 150,
        }, {
            field : 'logTimes',
            title : 'T_CORE_SYSTEM_MONITOR_ERRORLOG.LOGTIMES',
            contentAlign : 'center',
            width : 70,
        },{
            field: 'triggerCategory',
            title: 'T_CORE_SYSTEM_MONITOR_ERRORLOG.TRIGGERCATEGORY',
            contentAlign: 'center',
            width: 130,
        },{
            field : 'remark',
            title : 'T_CORE_SYSTEM_MONITOR_ERRORLOG.REMARK',
            width : 150,
        },{
            field : 'viewErrorLog',
            title : 'T_CORE_SYSTEM_MONITOR_ERRORLOG.OPERATION',
            contentAlign : 'center',
            type : 'link',
            width : 150,
            filter : false
        }];
    },
    getBaseColumns : function (){
        return [{
            field : 'remark',
            title : 'T_CORE_SYSTEM_MONITOR_ERRORLOG.REMARK',
            validators : ['strLength[0,170]','notEmpty'],
            colspan : 3,
            type : 'textarea'
        }]
    },
    baseUrl : IFM_CONTEXT + '/secure/module/sys/monitor/error-logs'
});

