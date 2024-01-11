var emcMonitorTmplGroup = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/monitor-tmpl-groups',

    getMonitorTmplGroupColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getActiveColumn(), {
            field : 'id',
            title : 'T_EMC_MONITOR_TMPL_GROUP.ID',
            width : 150,
        }, {
            field : 'monitorTmplGroup',
            title : 'T_EMC_MONITOR_TMPL_GROUP.MONITORTMPLGROUP',
            validators : [ 'strLength[0,21]' ],
            preInsert : true,
            editor : true
        }, {
            field : 'tmplGroupDesc',
            title : 'T_EMC_MONITOR_TMPL_GROUP.TMPLGROUPDESC',
            validators : [ 'strLength[0,85]' ],
            editor : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_MONITOR_TMPL_GROUP.ORDERNO',
            validators : [ 'integer' ],
            width : 100,
            editor : true
        }, {
            field : 'erpCategoryCode',
            title : 'T_EMC_MONITOR_TMPL_GROUP.ERPCATEGORYCODE',
            validators : [ 'integer' ],
            editor : true
        } ];
    },

    getOrderDescColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getActiveColumn(), {
            field : 'displayTextId',
            title : 'T_EMC_ORDER_DESC.DISPLAYTEXTID',
            validators : [ 'strLength[0,10]' ],
            editor : true,
        }, {
            field : 'displayText',
            title : 'T_EMC_ORDER_DESC.DISPLAYTEXT',
            validators : [ 'strLength[0,21]' ],
            editor : true,
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER_DESC.ORDERNO',
            validators : [ 'integer' ],
            width : 100,
            editor : true,
        }, ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'id',
            title : 'T_EMC_MONITOR_TMPL_GROUP.ID',
        }, {
            field : 'monitorTmplGroup',
            title : 'T_EMC_MONITOR_TMPL_GROUP.MONITORTMPLGROUP',
        }, {
            field : 'tmplGroupDesc',
            title : 'T_EMC_MONITOR_TMPL_GROUP.TMPLGROUPDESC',
            type : 'textarea',
        }, ];
    },

});
