var coreSystemMonitorPm = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'sql',
            title : 'T_CORE_SYSTEM_MONITOR_SQL.SQL',
            width : 300,
            formatter : function (rowIndex,value,row){
                return '<div style="display : flex"><span style="width : calc(100% - 30px);text-overflow : ellipsis;overflow : hidden;">' +row.sql+ '</span><a href="javascript:;" onclick="viewSql('+ rowIndex+','+ row.revisionFlag +')" style="position : absolute;right : 5px;width : auto;">查看</a></div>';
            },
            titleFormatter : function(row) {
                return row.sql;
            }
        }, {
            field : 'sqlSource',
            title : 'T_CORE_SYSTEM_MONITOR_SQL.SQLSOURCE',
            width : 400
        }, {
            field : 'timeConsuming',
            title : 'T_CORE_SYSTEM_MONITOR_SQL.TIMECONSUMING',
            width : 100
        }, {
            field : 'triggerCategory',
            title : 'T_CORE_SYSTEM_MONITOR_SQL.TRIGGERCATEGORY',
            width : 100
        }, {
            field : 'executeTime',
            title : 'T_CORE_SYSTEM_MONITOR_SQL.EXECUTETIME',
            width : 150
        }, {
            field : 'sqlTimes',
            title : 'T_CORE_SYSTEM_MONITOR_SQL.SQLTIMES',
            width : 80
        } ] ;
    },



    baseUrl : IFM_CONTEXT + '/secure/module/sys/monitor/system-monitor-pms'
});
