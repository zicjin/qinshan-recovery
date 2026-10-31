var emcInternaAudit = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/internal-audits',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcInternaAudit.workflow.dbTable), {
            field : 'qualityControlNo',
            title : 'T_EMC_INTERNAL_AUDIT.QUALITYCONTROLNO',
        }, {
            field : 'createdByName',
            title : 'T_EMC_INTERNAL_AUDIT.CREATEDBYNAME',
        } ];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcInternaAudit.workflow.dbTable), {
            field : 'qualityControlNo',
            title : 'T_EMC_INTERNAL_AUDIT.QUALITYCONTROLNO',
        }, {
            field : 'createdByName',
            title : 'T_EMC_INTERNAL_AUDIT.CREATEDBYNAME',
        } ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'qualityControlNo',
            title : 'T_EMC_INTERNAL_AUDIT.QUALITYCONTROLNO',
        }, {
            field : 'createdByName',
            title : 'T_EMC_INTERNAL_AUDIT.CREATEDBYNAME',
        } ];
    },

    getInternaAuditPlanFields : function() {
        return [ {
            field : 'auditPurpose',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITPURPOSE',
            validators : [ 'strLength[0, 540]' ],
            editor : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'auditBasis',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITBASIS',
            validators : [ 'strLength[0, 540]' ],
            editor : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'auditOrg',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITORG',
            validators : [ 'strLength[0, 270]' ],
            editor : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'auditGroupUser',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITGROUPUSER',
            validators : [ 'strLength[0, 360]' ],
            editor : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'auditTime',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITTIME',
            type : 'date',
            editor : true,
        }, {
            field : 'auditContent',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITCONTENT',
            validators : [ 'strLength[0, 540]' ],
            editor : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'userTrain',
            title : 'T_EMC_INTERNAL_AUDIT.USERTRAIN',
            validators : [ 'strLength[0, 540]' ],
            editor : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'recordTable',
            title : 'T_EMC_INTERNAL_AUDIT.RECORDTABLE',
            validators : [ 'strLength[0, 540]' ],
            editor : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'startTime',
            title : 'T_EMC_INTERNAL_AUDIT.STARTTIME',
            type : 'date',
            editor : true,
        }, {
            field : 'endTime',
            title : 'T_EMC_INTERNAL_AUDIT.ENDTIME',
            type : 'date',
            editor : true,
        } ];
    },

    getInternaAuditReportFields : function() {
        return [ {
            field : 'auditPurpose',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITPURPOSE',
            validators : [ 'strLength[0, 540]' ],
            editor : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'auditBasis',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITBASIS',
            validators : [ 'strLength[0, 540]' ],
            editor : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'auditDate',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITDATE',
            type : 'date',
            editor : true,
        }, {
            field : 'lastAuditDate',
            title : 'T_EMC_INTERNAL_AUDIT.LASTAUDITDATE',
            type : 'date',
            editor : true,
        }, {
            field : 'division',
            title : 'T_EMC_INTERNAL_AUDIT.DIVISION',
            validators : [ 'strLength[0, 540]' ],
            editor : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'ext$.auditlgroupleader ',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITLGROUPLEADER',
            editor : true,
        }, {
            field : 'ext$.auditer',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITER',
            editor : true,
        }, {
            field : 'ext$.reviewlocation',
            title : 'T_EMC_INTERNAL_AUDIT.REVIEWLOCATION',
            editor : true,
        }, {
            field : 'auditSituation',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITSITUATION',
            validators : [ 'strLength[0, 540]' ],
            editor : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'inconformity',
            title : 'T_EMC_INTERNAL_AUDIT.INCONFORMITY',
            validators : [ 'strLength[0, 540]' ],
            editor : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'manageSystem',
            title : 'T_EMC_INTERNAL_AUDIT.MANAGESYSTEM',
            validators : [ 'strLength[0, 540]' ],
            editor : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'correct',
            title : 'T_EMC_INTERNAL_AUDIT.CORRECT',
            validators : [ 'strLength[0, 540]' ],
            editor : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'auditRequest',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITREQUEST',
            validators : [ 'strLength[0, 540]' ],
            editor : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'auditReview',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITREVIEW',
            validators : [ 'strLength[0, 540]' ],
            editor : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'ext$.suggestion',
            title : 'T_EMC_INTERNAL_AUDIT.SUGGESTION',
            type : 'textarea',
            colspan : 2,
        } , {
            field : 'sendScope',
            title : 'T_EMC_INTERNAL_AUDIT.SENDSCOPE',
            validators : [ 'strLength[0, 540]' ],
            editor : true,
            type : 'textarea',
            colspan : 2,
        } ];
    },

    getInternaAuditPlanFieldsNoEdit : function() {
        return [ {
            field : 'auditPurpose',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITPURPOSE',
            validators : [ 'strLength[0, 540]' ],
            type : 'textarea',
            readonly : true,
            colspan : 2,
        }, {
            field : 'auditBasis',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITBASIS',
            validators : [ 'strLength[0, 540]' ],
            readonly : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'auditOrg',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITORG',
            validators : [ 'strLength[0, 270]' ],
            readonly : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'auditGroupUser',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITGROUPUSER',
            validators : [ 'strLength[0, 360]' ],
            readonly : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'auditTime',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITTIME',
            type : 'date',
            readonly : true,
        }, {
            field : 'auditContent',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITCONTENT',
            validators : [ 'strLength[0, 540]' ],
            readonly : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'userTrain',
            title : 'T_EMC_INTERNAL_AUDIT.USERTRAIN',
            validators : [ 'strLength[0, 540]' ],
            readonly : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'recordTable',
            title : 'T_EMC_INTERNAL_AUDIT.RECORDTABLE',
            validators : [ 'strLength[0, 540]' ],
            readonly : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'startTime',
            title : 'T_EMC_INTERNAL_AUDIT.STARTTIME',
            type : 'date',
            readonly : true,
        }, {
            field : 'endTime',
            title : 'T_EMC_INTERNAL_AUDIT.ENDTIME',
            type : 'date',
            readonly : true,
        } ];
    },

    getInternaAuditReportFieldsNoEdit : function() {
        return [ {
            field : 'auditPurpose',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITPURPOSE',
            validators : [ 'strLength[0, 540]' ],
            readonly : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'auditBasis',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITBASIS',
            validators : [ 'strLength[0, 540]' ],
            readonly : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'auditDate',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITDATE',
            type : 'date',
            readonly : true,
        }, {
            field : 'lastAuditDate',
            title : 'T_EMC_INTERNAL_AUDIT.LASTAUDITDATE',
            type : 'date',
            readonly : true,
        }, {
            field : 'division',
            title : 'T_EMC_INTERNAL_AUDIT.DIVISION',
            validators : [ 'strLength[0, 540]' ],
            readonly : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'auditSituation',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITSITUATION',
            validators : [ 'strLength[0, 540]' ],
            readonly : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'inconformity',
            title : 'T_EMC_INTERNAL_AUDIT.INCONFORMITY',
            validators : [ 'strLength[0, 540]' ],
            readonly : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'manageSystem',
            title : 'T_EMC_INTERNAL_AUDIT.MANAGESYSTEM',
            validators : [ 'strLength[0, 540]' ],
            readonly : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'correct',
            title : 'T_EMC_INTERNAL_AUDIT.CORRECT',
            validators : [ 'strLength[0, 540]' ],
            readonly : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'auditRequest',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITREQUEST',
            validators : [ 'strLength[0, 540]' ],
            readonly : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'auditReview',
            title : 'T_EMC_INTERNAL_AUDIT.AUDITREVIEW',
            validators : [ 'strLength[0, 540]' ],
            readonly : true,
            type : 'textarea',
            colspan : 2,
        }, {
            field : 'sendScope',
            title : 'T_EMC_INTERNAL_AUDIT.SENDSCOPE',
            validators : [ 'strLength[0, 540]' ],
            readonly : true,
            type : 'textarea',
            colspan : 2,
        } ];
    },

    workflow : {
        dbTable : 'T_EMC_INTERNAL_AUDIT',
        columns : [ {
            field : 'id',
            title : 'T_EMC_INTERNAL_AUDIT.ID'
        } ]
    }
});
