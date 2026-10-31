var emcReportInfoReference = Gikam.getPageObject({

    baseUrl: IFM_CONTEXT + "/secure/emc/module/mdm/basemdm/report-info-references",

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'sortNum',
            title : 'T_EMC_REPORT_INFO_REFERENCE.SORTNUM',
            width : 100,
            type : 'number',
            isInteger : true,
            hasArrow : true,
            editor : true,
            range : '[1, 1000]'
        }, {
            field : 'bizCategory',
            title : 'T_EMC_REPORT_INFO_REFERENCE.BIZCATEGORY',
            editor : true
        }, {
            field : 'orderCategory',
            title : 'T_EMC_REPORT_INFO_REFERENCE.ORDERCATEGORY',
            editor : true
        }, {
            field : 'reportName',
            title : 'T_EMC_REPORT_INFO_REFERENCE.REPORTNAME',
            editor : true
        }, {
            field : 'testCode',
            title : 'T_EMC_REPORT_INFO_REFERENCE.TESTCODE',
            editor : true
        }, {
            field : 'itemName',
            title : 'T_EMC_REPORT_INFO_REFERENCE.ITEMNAME',
            editor : true
        }, {
            field : 'reportShowName',
            title : 'T_EMC_REPORT_INFO_REFERENCE.REPORTSHOWNAME',
            editor : true
        }, {
            field : 'method',
            title : 'T_EMC_REPORT_INFO_REFERENCE.METHOD',
            editor : true
        }, {
            field : 'analysisGroup',
            title : 'T_EMC_REPORT_INFO_REFERENCE.ANALYSISGROUP',
            editor : true
        }, {
            field : 'groupConditionOne',
            title : 'T_EMC_REPORT_INFO_REFERENCE.GROUPCONDITIONONE',
            editor : true
        }, {
            field : 'groupConditionTwo',
            title : 'T_EMC_REPORT_INFO_REFERENCE.GROUPCONDITIONTWO',
            editor : true
        }, {
            field : 'groupConditionThree',
            title : 'T_EMC_REPORT_INFO_REFERENCE.GROUPCONDITIONTHREE',
            editor : true
        }, {
            field : 'groupConditionFour',
            title : 'T_EMC_REPORT_INFO_REFERENCE.GROUPCONDITIONFOUR',
            editor : true
        } ];
    }

});
