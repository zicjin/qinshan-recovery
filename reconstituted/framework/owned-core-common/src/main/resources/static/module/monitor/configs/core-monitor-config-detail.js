coreMonitorConfig.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_CORE_MONITOR_CONFIG', _this.param.id);
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.MONITOR.MONITOR_CONFIG.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreMonitorConfigServiceImpl',
                    id : 'core-monitor-config-detail-base-info-form',
                    panels : coreMonitorConfig.getPanelFields(),
                    titleWidth : 140,
                    url : Gikam.printf(coreMonitorConfig.baseUrl + '/{id}', {
                        id : 'monitor_config'
                    })
                } ]
            } ]
        }
    },

    urlManager : {
        baseInfoForm : coreMonitorConfig.baseUrl + '/{id}',
    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        });
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab() ]
            },
            renderTo : workspace.window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
