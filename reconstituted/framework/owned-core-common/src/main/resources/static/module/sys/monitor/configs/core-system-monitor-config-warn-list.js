coreSystemMonitorConfig.detailPage = {
    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'grid',
            url : coreSystemMonitorConfig.baseUrl + '/queries/raw',
            service : 'coreSystemMonitorConfigServiceImpl',
            dbTable : 'T_CORE_SYSTEM_MONITOR_CONFIG',
            id : 'core-system-monitor-config-warn-list-grid',
            columns : coreSystemMonitorConfig.getAlarmSettingEditListColumns(),
            requestData : {
                configCategory_SEQ : 'warn',
            },
        }
    },
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-system-monitor-config-warn-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getBaseInfoTab() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
