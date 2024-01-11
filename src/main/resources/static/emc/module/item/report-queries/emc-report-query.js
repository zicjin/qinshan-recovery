var emcReportQuery = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/report-queries',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcReportQuery.workflow.dbTable), {
            field : 'queryContent',
            title : 'T_EMC_REPORT_QUERY.QUERYCONTENT',
            width : 120,
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,60]' ]
        }, {
            field : 'queryForm',
            title : 'T_EMC_REPORT_QUERY.QUERYFORM',
            width : 120,
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'reportqueryform',
                codeClassId : 'biz'
            },
            validators : [ 'notEmpty', 'strLength[0,60]' ]
        }, {
            field : 'purpose',
            title : 'T_EMC_REPORT_QUERY.PURPOSE',
            width : 200,
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,300]' ]
        }, {
            field : 'contact',
            title : 'T_EMC_REPORT_QUERY.CONTACT',
            width : 100,
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,60]' ]
        }, {
            field : 'phone',
            title : 'T_EMC_REPORT_QUERY.PHONE',
            width : 120,
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,30]', 'phone' ]
        }, {
            field : 'submitDate',
            title : 'T_EMC_REPORT_QUERY.SUBMITDATE',
            width : 100,
            editor : true,
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'register',
            title : 'T_EMC_REPORT_QUERY.REGISTER',
            width : 90,
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,60]' ]
        }, {
            field : 'remark',
            title : 'T_EMC_REPORT_QUERY.REMARK',
            width : 300,
            editor : true,
            validators : [ 'strLength[0,540]' ]
        } ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcReportQuery.workflow.dbTable), {
            field : 'queryContent',
            title : 'T_EMC_REPORT_QUERY.QUERYCONTENT',
            width : 120
        }, {
            field : 'queryForm',
            title : 'T_EMC_REPORT_QUERY.QUERYFORM',
            width : 120,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'reportqueryform',
                codeClassId : 'biz'
            }
        }, {
            field : 'purpose',
            title : 'T_EMC_REPORT_QUERY.PURPOSE',
            width : 200
        }, {
            field : 'contact',
            title : 'T_EMC_REPORT_QUERY.CONTACT',
            width : 100
        }, {
            field : 'phone',
            title : 'T_EMC_REPORT_QUERY.PHONE',
            width : 120
        }, {
            field : 'submitDate',
            title : 'T_EMC_REPORT_QUERY.SUBMITDATE',
            width : 100
        }, {
            field : 'register',
            title : 'T_EMC_REPORT_QUERY.REGISTER',
            width : 90
        }, {
            field : 'remark',
            title : 'T_EMC_REPORT_QUERY.REMARK',
            width : 300
        } ];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcReportQuery.workflow.dbTable), {
            field : 'queryContent',
            title : 'T_EMC_REPORT_QUERY.QUERYCONTENT',
            width : 120
        }, {
            field : 'queryForm',
            title : 'T_EMC_REPORT_QUERY.QUERYFORM',
            width : 120,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'reportqueryform',
                codeClassId : 'biz'
            }
        }, {
            field : 'purpose',
            title : 'T_EMC_REPORT_QUERY.PURPOSE',
            width : 200
        }, {
            field : 'contact',
            title : 'T_EMC_REPORT_QUERY.CONTACT',
            width : 100
        }, {
            field : 'phone',
            title : 'T_EMC_REPORT_QUERY.PHONE',
            width : 120
        }, {
            field : 'submitDate',
            title : 'T_EMC_REPORT_QUERY.SUBMITDATE',
            width : 100
        }, {
            field : 'register',
            title : 'T_EMC_REPORT_QUERY.REGISTER',
            width : 90
        }, {
            field : 'remark',
            title : 'T_EMC_REPORT_QUERY.REMARK',
            width : 300
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'queryContent',
            title : 'T_EMC_REPORT_QUERY.QUERYCONTENT',
            preInsert : true,
            validators : [ 'notEmpty', 'strLength[0,60]' ]
        }, {
            field : 'queryForm',
            title : 'T_EMC_REPORT_QUERY.QUERYFORM',
            preInsert : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'reportqueryform',
                codeClassId : 'biz'
            },
            validators : [ 'notEmpty', 'strLength[0,60]' ]
        } ];
    },

    workflow : {
        dbTable : 'T_EMC_REPORT_QUERY',
        columns : [ {
            field : 'ID',
            title : 'T_EMC_REPORT_QUERY.ID'
        } ]
    }
});
