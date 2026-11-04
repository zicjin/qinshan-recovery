var coreMonitorConfig = Gikam.getPageObject({

    getPanelFields : function() {
        return [ {
            title : 'CORE.MODULE.MONITOR.MONITOR_CONFIG.PANEL.SERVER',
            fields : [ {
                field : 'serverCpuAlarm',
                title : 'T_CORE_MONITOR_CONFIG.SERVERCPUALARM',
                validators : [ 'notEmpty', 'integer' ]
            }, {
                field : 'serverCpuAbnormal',
                title : 'T_CORE_MONITOR_CONFIG.SERVERCPUABNORMAL',
                validators : [ 'notEmpty', 'integer' ]
            }, {
                field : 'serverRamAlarm',
                title : 'T_CORE_MONITOR_CONFIG.SERVERRAMALARM',
                validators : [ 'notEmpty', 'integer' ]
            }, {
                field : 'serverRamAbnormal',
                title : 'T_CORE_MONITOR_CONFIG.SERVERRAMABNORMAL',
                validators : [ 'notEmpty', 'integer' ]
            } ]
        }, {
            title : 'CORE.MODULE.MONITOR.MONITOR_CONFIG.PANEL.APP',
            fields : [ {
                field : 'appCpuAlarm',
                title : 'T_CORE_MONITOR_CONFIG.APPCPUALARM',
                validators : [ 'notEmpty', 'integer' ]
            }, {
                field : 'appCpuAbnormal',
                title : 'T_CORE_MONITOR_CONFIG.APPCPUABNORMAL',
                validators : [ 'notEmpty', 'integer' ]
            }, {
                field : 'appRamAlarm',
                title : 'T_CORE_MONITOR_CONFIG.APPRAMALARM',
                validators : [ 'notEmpty', 'integer' ]
            }, {
                field : 'appRamAbnormal',
                title : 'T_CORE_MONITOR_CONFIG.APPRAMABNORMAL',
                validators : [ 'notEmpty', 'integer' ]
            } ]
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/monitor/configs'
});
