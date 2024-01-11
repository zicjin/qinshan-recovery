var emcAdvisoryReport = Gikam.getPageObject({

    baseUrl: IFM_CONTEXT + "/secure/emc/module/bp/report/advisory-reports",

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcAdvisoryReport.workflow.dbTable), {
            field : 'ext$.projno',
            title : 'T_EMC_ADVISORY_REPORT.PROJNO',
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_ADVISORY_REPORT.PROJNAME',
        }, {
            field : 'ext$.projusername',
            title : 'T_EMC_ADVISORY_REPORT.PROJUSERID'
        }, {
            field : 'ext$.testorgname',
            title : 'T_EMC_ADVISORY_REPORT.TESTORGNAME'
        }, {
            field : 'ext$.entrustorgname',
            title : 'T_EMC_ADVISORY_REPORT.ENTRUSTORGNAME'
        }, {
            field : 'ext$.bizcategoryname',
            title : 'T_EMC_ADVISORY_REPORT.BIZCATEGORY'
        }, {
            field : 'remark',
            title : 'T_EMC_ADVISORY_REPORT.REMARK',
            editor : true
        } ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcAdvisoryReport.workflow.dbTable), {
            field : 'ext$.projno',
            title : 'T_EMC_ADVISORY_REPORT.PROJNO',
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_ADVISORY_REPORT.PROJNAME',
        }, {
            field : 'ext$.projusername',
            title : 'T_EMC_ADVISORY_REPORT.PROJUSERID'
        }, {
            field : 'ext$.testorgname',
            title : 'T_EMC_ADVISORY_REPORT.TESTORGNAME'
        }, {
            field : 'ext$.entrustorgname',
            title : 'T_EMC_ADVISORY_REPORT.ENTRUSTORGNAME'
        }, {
            field : 'ext$.bizcategoryname',
            title : 'T_EMC_ADVISORY_REPORT.BIZCATEGORY'
        }, {
            field : 'remark',
            title : 'T_EMC_ADVISORY_REPORT.REMARK'
        } ];
    },

    getIssueListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcAdvisoryReport.workflow.dbTable), {
            field : 'ext$.projno',
            title : 'T_EMC_ADVISORY_REPORT.PROJNO',
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_ADVISORY_REPORT.PROJNAME',
        }, {
            field : 'discuss',
            title : 'T_EMC_ADVISORY_REPORT.DISCUSS',
            validators : [ 'notEmpty' ],
            type : 'select',
            editor : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            field : 'revise',
            title : 'T_EMC_ADVISORY_REPORT.REVISE',
            validators : [ 'notEmpty' ],
            type : 'select',
            editor : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            field : 'ext$.projusername',
            title : 'T_EMC_ADVISORY_REPORT.PROJUSERID'
        }, {
            field : 'ext$.testorgname',
            title : 'T_EMC_ADVISORY_REPORT.TESTORGNAME'
        }, {
            field : 'ext$.entrustorgname',
            title : 'T_EMC_ADVISORY_REPORT.ENTRUSTORGNAME'
        }, {
            field : 'ext$.bizcategoryname',
            title : 'T_EMC_ADVISORY_REPORT.BIZCATEGORY'
        }, {
            field : 'remark',
            title : 'T_EMC_ADVISORY_REPORT.REMARK'
        } ];
    },

    getArchivingListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.projno',
            title : 'T_EMC_ADVISORY_REPORT.PROJNO',
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_ADVISORY_REPORT.PROJNAME',
        }, {
            field : 'ext$.projusername',
            title : 'T_EMC_ADVISORY_REPORT.PROJUSERID'
        }, {
            field : 'ext$.testorgname',
            title : 'T_EMC_ADVISORY_REPORT.TESTORGNAME'
        }, {
            field : 'ext$.entrustorgname',
            title : 'T_EMC_ADVISORY_REPORT.ENTRUSTORGNAME'
        }, {
            field : 'ext$.bizcategoryname',
            title : 'T_EMC_ADVISORY_REPORT.BIZCATEGORY'
        }, {
            field : 'remark',
            title : 'T_EMC_ADVISORY_REPORT.REMARK'
        } ];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcAdvisoryReport.workflow.dbTable), {
            field : 'ext$.projno',
            title : 'T_EMC_ADVISORY_REPORT.PROJNO',
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_ADVISORY_REPORT.PROJNAME',
        }, {
            field : 'ext$.projusername',
            title : 'T_EMC_ADVISORY_REPORT.PROJUSERID'
        }, {
            field : 'ext$.testorgname',
            title : 'T_EMC_ADVISORY_REPORT.TESTORGNAME'
        }, {
            field : 'ext$.entrustorgname',
            title : 'T_EMC_ADVISORY_REPORT.ENTRUSTORGNAME'
        }, {
            field : 'ext$.bizcategoryname',
            title : 'T_EMC_ADVISORY_REPORT.BIZCATEGORY'
        }, {
            field : 'remark',
            title : 'T_EMC_ADVISORY_REPORT.REMARK'
        } ];
    },

    workflow : {
        dbTable : 'T_EMC_ADVISORY_REPORT',
        columns : [ {
            field : 'id',
            title : 'T_EMC_ADVISORY_REPORT.ID'
        } ]
    },

})
