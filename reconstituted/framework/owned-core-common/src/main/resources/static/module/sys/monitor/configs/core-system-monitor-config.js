var coreSystemMonitorConfig = Gikam.getPageObject({

    getAutoCleanEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'configName',
            title : 'T_CORE_SYSTEM_MONITOR_CONFIG.CLEANCATEGORY',
            width : 400

        }, {
            field : 'configValue',
            title : 'T_CORE_SYSTEM_MONITOR_CONFIG.RESERVETIME',
            editor : true,
            sort : false,
            onChange : function(field, value) {
                var grid = this;
                grid.disableAutoSave();
                var reg = new RegExp("^([1-9][0-9]?|[12][0-9][0-9]|3[0-5][0-9]|36[0-5])$");
                if (!reg.test(value)) {
                    Gikam.alert("T_CORE_SYSTEM_MONITOR_CONFIG.RETENTION_ TIME_CHECK")
                    return;
                }
                grid.enableAutoSave();
            },
            width : 400
        },
            // 用来做支撑页面的宽度。不然设置时间太长，不太好看
            {
            title : '',
            width : 400
        } ];
    },

    getAlarmSettingEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'configName',
            title : 'T_CORE_SYSTEM_MONITOR_CONFIG.CONFIGNAME',
            width : 400
        }, {
            field : 'configValue',
            title : 'T_CORE_SYSTEM_MONITOR_CONFIG.CONFIGVALUE',
            titleWidth : 200,
            editor : true,
            onChange : function(field, value, index) {
                var grid = this;
                grid.disableAutoSave();
                var data = grid.getData();
                var reg = new RegExp("^(\\d|[1-9]\\d|100)$");
                if (!((data[index].id == "notifierEmail") || (data[index].id == "notifierMobile"))) {
                    if (!reg.test(value)) {
                        Gikam.alert("T_CORE_SYSTEM_MONITOR_CONFIG.THRESHOLD_CHECKING")
                        return;
                    }
                }
                grid.enableAutoSave();
            },
            width : 400
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/module/sys/monitor/configs'
});
