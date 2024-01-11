var emcMonitorPointCategory = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/monitor-point-categories',

    getMonitorPointCategoryColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'id',
            title : 'T_EMC_MONITOR_POINT_CATEGORY.ID'
        }, {
            field : 'monitorPointType',
            title : 'T_EMC_MONITOR_POINT_CATEGORY.MONITORPOINTTYPE',
            type : 'select',
            search : true,
            category : 'propertyTmpl',
            param : {
                templateUse : 'monitorPointCategory',
            },
            editor : true
        }, {
            field : 'monitorTmplGroupId',
            title : 'T_EMC_MONITOR_POINT_CATEGORY.MONITORTMPLGROUPID',
            type : 'select',
            search : true,
            category : 'monitorTmplGroup',
            editor : true
        }, ];
    },

    getMonitorTmplColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'monitorTmplId',
            title : 'T_EMC_MPC_TMPL.MONITORTMPLID',
            width : 120,
        }, {
            field : 'ext$.monitortmplname',
            title : 'T_EMC_MPC_TMPL.MONITORTMPLNAME',
        }, ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'id',
            title : 'T_EMC_MONITOR_POINT_CATEGORY.ID',
        }, {
            field : 'monitorPointType',
            title : 'T_EMC_MONITOR_POINT_CATEGORY.MONITORPOINTTYPE',
            type : 'select',
            search : true,
            category : 'propertyTmpl',
        }, {
            field : 'monitorTmplGroupId',
            title : 'T_EMC_MONITOR_POINT_CATEGORY.MONITORTMPLGROUPID',
            type : 'select',
            search : true,
            category : 'monitorTmplGroup',
        }, ];
    },

});
