coreSystemMonitorConfig.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-system-monitor-config-auto-clean-list-grid',
            url : coreSystemMonitorConfig.baseUrl + '/queries/raw',
            service : 'coreSystemMonitorConfigServiceImpl',
            dbTable : 'T_CORE_SYSTEM_MONITOR_CONFIG',
            columns : coreSystemMonitorConfig.getAutoCleanEditListColumns(),
            requestData : {
                configCategory_SEQ : 'autoClean',
            },
        }
    },
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-system-monitor-config-auto-clean-list-layout',
            renderTo : workspace.window.$dom,
            width : 500,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
