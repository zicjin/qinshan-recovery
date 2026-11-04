var coreEquiptUsageRecord = Gikam.getPageObject({

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'equiptName',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.EQUIPTNAME',
            width : 200
        }, {
            field : 'equiptCategory',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.EQUIPTCATEGORY',
            width : 200
        }, {
            field : 'upTime',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.UPTIME',
            width : 200
        }, {
            field : 'downTime',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.DOWNTIME',
            width : 200
        }, {
            field : 'usageTime',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.USAGETIME',
            width : 100
        }, {
            field : 'userName',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.USERNAME',
            width : 100
        }, {
            field : 'testSample',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.TESTSAMPLE',
            width : 200
        }, {
            field : 'testItem',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.TESTITEM',
            width : 200
        }, {
            field : 'sampleQty',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.SAMPLEQTY',
            width : 100
        }, {
            field : 'orderTaskQty',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.ORDERTASKQTY',
            width : 100
        }, {
            field : 'amount',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.AMOUNT',
            width : 100
        } ];
    },

    getEquiptCategoryRecordColumns : function() {
        return [ {
            index : true
        }, {
            field : 'equiptName',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.EQUIPTNAME',
            width : 150
        }, {
            field : 'equiptCategory',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.EQUIPTCATEGORY',
            width : 200
        }, {
            field : 'upTime',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.UPTIME',
            width : 150
        }, {
            field : 'downTime',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.DOWNTIME',
            width : 150
        }, {
            field : 'usageTime',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.USAGETIME',
            width : 100
        }, {
            field : 'userName',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.USERNAME',
            width : 100
        }, {
            field : 'testSample',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.TESTSAMPLE',
            width : 200
        }, {
            field : 'testItem',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.TESTITEM',
            width : 200
        }, {
            field : 'amount',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.AMOUNT',
            width : 100
        } ];
    },

    getEquiptRateListColumns : function() {
        return [ {
            index : true
        }, {
            field : 'equiptName',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.EQUIPTNAME',
            width : 100
        }, {
            field : 'equiptCategory',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.EQUIPTCATEGORY',
            width : 200
        }, {
            field : 'yearMonth',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.YEARMONTH',
            filter : false,
            width : 100
        }, {
            field : 'rate',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.RATE',
            filter : false,
            width : 100
        } ];
    },

    getMonthlyContributionColumns : function() {
        return [ {
            index : true
        }, {
            field : 'equiptName',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.EQUIPTNAME',
            width : 100
        }, {
            field : 'equiptCategory',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.EQUIPTCATEGORY',
            width : 200
        }, {
            field : 'ext$.yearmonth',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.YEARMONTH',
            filter : false,
            width : 100
        }, {
            field : 'sampleQty',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.SAMPLEQTY',
            width : 100
        }, {
            field : 'orderTaskQty',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.ORDERTASKQTY',
            width : 100
        }, {
            field : 'amount',
            title : 'T_CORE_EQUIPT_USAGE_RECORD.AMOUNT',
            width : 100
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/equipt/usage-records'
});
