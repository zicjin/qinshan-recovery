var coreNfssiReport = Gikam.getPageObject({

    getReportItemListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'testItem',
            title : 'T_CORE_NFSSI_REPORT_ITEM.TESTITEM'
        }, {
            field : 'testResult',
            title : 'T_CORE_NFSSI_REPORT_ITEM.TESTRESULT'
        }, {
            field : 'resultJudgment',
            title : 'T_CORE_NFSSI_REPORT_ITEM.RESULTJUDGMENT'
        }, {
            field : 'inspectStandard',
            title : 'T_CORE_NFSSI_REPORT_ITEM.INSPECTSTANDARD',
            type : 'richText'
        }, {
            field : 'judgeStandard',
            title : 'T_CORE_NFSSI_REPORT_ITEM.JUDGESTANDARD',
            type : 'richText'
        }, {
            field : 'methodLimit',
            title : 'T_CORE_NFSSI_REPORT_ITEM.METHODLIMIT'
        }, {
            field : 'detectionLimitOrg',
            title : 'T_CORE_NFSSI_REPORT_ITEM.DETECTIONLIMITORG'
        }, {
            field : 'minLimit',
            title : 'T_CORE_NFSSI_REPORT_ITEM.MINLIMIT'
        }, {
            field : 'minLimitUnit',
            title : 'T_CORE_NFSSI_REPORT_ITEM.MINLIMITUNIT'
        }, {
            field : 'maxLimit',
            title : 'T_CORE_NFSSI_REPORT_ITEM.MAXLIMIT'
        }, {
            field : 'maxLimitUnit',
            title : 'T_CORE_NFSSI_REPORT_ITEM.MAXLIMITUNIT'
        }, {
            field : 'explain',
            title : 'T_CORE_NFSSI_REPORT_ITEM.EXPLAIN'
        }, {
            field : 'resultUnit',
            title : 'T_CORE_NFSSI_REPORT_ITEM.RESULTUNIT'
        }, {
            field : 'remark',
            title : 'T_CORE_NFSSI_REPORT_ITEM.REMARK'
        }, {
            field : 'oldItemName',
            title : 'T_CORE_NFSSI_REPORT_ITEM.OLDITEMNAME'
        }, {
            field : 'itemCategory',
            title : 'T_CORE_NFSSI_REPORT_ITEM.ITEMCATEGORY'
        }, {
            field : 'necessary',
            title : 'T_CORE_NFSSI_REPORT_ITEM.NECESSARY'
        }, {
            field : 'funcCategory',
            title : 'T_CORE_NFSSI_REPORT_ITEM.FUNCCATEGORY'
        }, {
            field : 'status',
            title : 'T_CORE_NFSSI_REPORT_ITEM.STATUS'
        } ];
    },

    getReportInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_NFSSI_REPORT.ID',
            validators : [ 'strLength[0,20]' ],
            readonly : true
        }, {
            field : 'orderId',
            title : 'T_CORE_NFSSI_REPORT.ORDERID',
            readonly : true
        }, {
            field : 'createdTime',
            title : 'T_CORE_NFSSI_REPORT.CREATEDTIME',
            readonly : true
        }, {
            field : 'inspectOrgContact',
            title : 'T_CORE_NFSSI_REPORT.INSPECTORGCONTACT',
            readonly : true
        }, {
            field : 'inspectOrgContactMobile',
            title : 'T_CORE_NFSSI_REPORT.INSPECTORGCONTACTMOBILE',
            readonly : true
        }, {
            field : 'inspectOrgContactEmail',
            title : 'T_CORE_NFSSI_REPORT.INSPECTORGCONTACTEMAIL',
            readonly : true
        }, {
            field : 'sealSampler',
            title : 'T_CORE_NFSSI_REPORT.SEALSAMPLER',
            readonly : true
        }, {
            field : 'sealSamplerMobile',
            title : 'T_CORE_NFSSI_REPORT.SEALSAMPLERMOBILE',
            readonly : true
        }, {
            field : 'sealSamplerEmail',
            title : 'T_CORE_NFSSI_REPORT.SEALSAMPLEREMAIL',
            readonly : true
        }, {
            field : 'superviseReportRemark',
            title : 'T_CORE_NFSSI_REPORT.SUPERVISEREPORTREMARK',
            colspan : 3,
            readonly : true
        }, {
            field : 'riskReportRemark',
            title : 'T_CORE_NFSSI_REPORT.RISKREPORTREMARK',
            colspan : 3,
            readonly : true
        }, {
            field : 'state',
            title : 'T_CORE_NFSSI_REPORT.STATE',
            type : 'select',
            category : 'success',
            readonly : true
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/nfssi/reports'
});
