var coreApplyComplaint = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreApplyComplaint.workflow.dbTable), {
            field : 'applyComplaintNo',
            title : 'T_CORE_APPLY_COMPLAINT.APPLYCOMPLAINTNO',
            width : 170,
            type : 'link'
        }, {
            field : 'createdByName',
            title : 'T_CORE_APPLY_COMPLAINT.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_CORE_APPLY_COMPLAINT.CREATEDTIME'
        }, {
            field : 'applyComplaintCorp',
            title : 'T_CORE_APPLY_COMPLAINT.APPLYCOMPLAINTCORP',
            width : 230
        }, {
            field : 'applyComplaintDate',
            title : 'T_CORE_APPLY_COMPLAINT.APPLYCOMPLAINTDATE'
        }, {
            field : 'folderNo',
            title : 'T_CORE_APPLY_COMPLAINT.FOLDERNO',
            width : 170
        }, {
            field : 'matName',
            title : 'T_CORE_APPLY_COMPLAINT.MATNAME'
        } ];
    },

    getInsertApplyComplainFields : function() {
        return [ {
            field : 'applyComplaintCorp',
            title : 'CORE.MODULE.QMS.APPLYCOMPLAINT.TITLE.CUSTOMER_ORG',
            validators : [ 'notEmpty', 'strLength[0,90]' ]
        }, {
            field : 'folderNo',
            title : 'T_CORE_APPLY_COMPLAINT.FOLDERNO',
            validators : [ 'strLength[0,12]' ]
        }, {
            field : 'matName',
            title : 'T_CORE_APPLY_COMPLAINT.MATNAME',
            validators : [ 'strLength[0,90]' ]
        }, {
            field : 'applyComplaintDate',
            title : 'T_CORE_APPLY_COMPLAINT.APPLYCOMPLAINTDATE',
            validators : [ 'notEmpty' ],
            type : 'date'
        } ];
    },
    
    getBaseInfoFormFields : function() {
        return [ {
            field : 'applyComplaintCorp',
            title : 'T_CORE_APPLY_COMPLAINT.APPLYCOMPLAINTCORP',
            validators : [ 'notEmpty', 'strLength[0,90]' ],
            colspan : 3
        }, {
            field : 'delegateName',
            title : 'T_CORE_APPLY_COMPLAINT.DELEGATENAME',
            validators : [ 'notEmpty', 'strLength[0,20]' ]
        }, {
            field : 'delegatePhone',
            title : 'T_CORE_APPLY_COMPLAINT.DELEGATEPHONE',
            validators : [ 'notEmpty', 'phone' ]
        }, {
            field : 'applyComplaintDate',
            title : 'T_CORE_APPLY_COMPLAINT.APPLYCOMPLAINTDATE',
            validators : [ 'notEmpty' ],
            type : 'date'
        }, {
            field : 'folderNo',
            title : 'T_CORE_APPLY_COMPLAINT.FOLDERNO',
            validators : [ 'strLength[0,12]' ]
        }, {
            field : 'matName',
            title : 'T_CORE_APPLY_COMPLAINT.MATNAME',
            validators : [ 'notEmpty', 'strLength[0,90]' ]
        }, {
            field : 'batchNo',
            title : 'T_CORE_APPLY_COMPLAINT.BATCHNO',
            validators : [ 'strLength[0,12]' ]
        }, {
            field : 'producCorp',
            title : 'T_CORE_APPLY_COMPLAINT.PRODUCCORP',
            validators : [ 'notEmpty', 'strLength[0,90]' ]
        }, {
            field : 'sampledCorp',
            title : 'T_CORE_APPLY_COMPLAINT.SAMPLEDCORP',
            validators : [ 'notEmpty', 'strLength[0,90]' ]
        }, {
            field : 'reportPrintDate',
            title : 'T_CORE_APPLY_COMPLAINT.REPORTPRINTDATE',
            validators : [ 'notEmpty' ],
            type : 'date'
        }, {
            field : 'applyComplaintType',
            title : 'T_CORE_APPLY_COMPLAINT.APPLYCOMPLAINTTYPE',
            validators : [ 'notEmpty', 'strLength[0,20]' ],
            colspan : 3,
            type : 'select',
            firstBlank : false,
            category : 'apply-complaint-type'
        }, {
            field : 'applyComplaintContent',
            title : 'T_CORE_APPLY_COMPLAINT.APPLYCOMPLAINTCONTENT',
            colspan : 3,
            type : 'textarea',
            height : 80
        }, {
            field : 'investigationUser',
            title : 'T_CORE_APPLY_COMPLAINT.INVESTIGATIONUSER',
            validators : [ 'notEmpty', 'strLength[0,20]' ],
            readonly : true,
            type : 'select',
            search : true,
            category : 'user-apply-complain'
        }, {
            field : 'investigationDate',
            title : 'T_CORE_APPLY_COMPLAINT.INVESTIGATIONDATE',
            type : 'date',
            readonly : true,
        }, {
            field : 'beAuditOrgNo',
            title : 'T_CORE_APPLY_COMPLAINT.BEAUDITORGNAME',
            validators : [ 'notEmpty', 'strLength[0,90]' ],
            readonly : true,
            type : 'select',
            search : true,
            category : 'org',
            onChange : function(field, value, rowIndex, oldValue, text) {
                Gikam.getComp('core-apply-complaint-detail-base-info-form').setData({
                    index : 0,
                    beAuditOrgName : text
                });
            }
        }, {
            field : 'investigationResult',
            title : 'T_CORE_APPLY_COMPLAINT.INVESTIGATIONRESULT',
            validators : [ 'notEmpty' ],
            readonly : true,
            colspan : 3,
            type : 'textarea',
            height : 80
        }, {
            field : 'suggestion',
            title : 'T_CORE_APPLY_COMPLAINT.SUGGESTION',
            validators : [ 'notEmpty' ],
            readonly : true,
            colspan : 3,
            type : 'textarea',
            height : 80
        }, {
            field : 'qmResult',
            title : 'T_CORE_APPLY_COMPLAINT.QMRESULT',
            validators : [ 'notEmpty' ],
            readonly : true,
            colspan : 3,
            type : 'textarea',
            height : 80
        }, {
            field : 'feedback',
            title : 'T_CORE_APPLY_COMPLAINT.FEEDBACK',
            validators : [ 'notEmpty' ],
            readonly : true,
            colspan : 3,
            type : 'textarea',
            height : 80
        }, {
            field : 'remark',
            title : 'T_CORE_APPLY_COMPLAINT.REMARK',
            validators : [ 'strLength[0,200]' ],
            colspan : 3,
            type : 'textarea',
            height : 80
        } ];
    },
    
    getAuditInfoFormFields : function(statusCode) {
        var fields = [ {
            field : 'applyComplaintCorp',
            title : 'T_CORE_APPLY_COMPLAINT.APPLYCOMPLAINTCORP',
            validators : [ 'notEmpty', 'strLength[0,90]' ],
            colspan : 3,
            type : 'select',
            readonly : true
        }, {
            field : 'delegateName',
            title : 'T_CORE_APPLY_COMPLAINT.DELEGATENAME',
            validators : [ 'notEmpty', 'strLength[0,20]' ],
            readonly : true
        }, {
            field : 'delegatePhone',
            title : 'T_CORE_APPLY_COMPLAINT.DELEGATEPHONE',
            validators : [ 'notEmpty', 'strLength[0,6]' ],
            readonly : true
        }, {
            field : 'applyComplaintDate',
            title : 'T_CORE_APPLY_COMPLAINT.APPLYCOMPLAINTDATE',
            validators : [ 'notEmpty' ],
            type : 'date',
            readonly : true
        }, {
            field : 'folderNo',
            title : 'T_CORE_APPLY_COMPLAINT.FOLDERNO',
            validators : [ 'strLength[0,12]' ],
            readonly : true
        }, {
            field : 'matName',
            title : 'T_CORE_APPLY_COMPLAINT.MATNAME',
            validators : [ 'notEmpty', 'strLength[0,90]' ],
            readonly : true
        }, {
            field : 'batchNo',
            title : 'T_CORE_APPLY_COMPLAINT.BATCHNO',
            validators : [ 'strLength[0,12]' ],
            readonly : true
        }, {
            field : 'producCorp',
            title : 'T_CORE_APPLY_COMPLAINT.PRODUCCORP',
            validators : [ 'notEmpty', 'strLength[0,90]' ],
            type : 'select',
            category : 'org-custom-survey',
            readonly : true
        }, {
            field : 'sampledCorp',
            title : 'T_CORE_APPLY_COMPLAINT.SAMPLEDCORP',
            validators : [ 'notEmpty', 'strLength[0,90]' ],
            type : 'select',
            category : 'org-custom-survey',
            readonly : true
        }, {
            field : 'reportPrintDate',
            title : 'T_CORE_APPLY_COMPLAINT.REPORTPRINTDATE',
            validators : [ 'notEmpty' ],
            type : 'date',
            readonly : true
        }, {
            field : 'applyComplaintType',
            title : 'T_CORE_APPLY_COMPLAINT.APPLYCOMPLAINTTYPE',
            validators : [ 'notEmpty', 'strLength[0,20]' ],
            colspan : 3,
            type : 'select',
            readonly : true,
            firstBlank : false,
            category : 'apply-complaint-type'
        }, {
            field : 'applyComplaintContent',
            title : 'T_CORE_APPLY_COMPLAINT.APPLYCOMPLAINTCONTENT',
            colspan : 3,
            type : 'textarea',
            height : 80,
            readonly : true
        }, {
            field : 'investigationUser',
            title : 'T_CORE_APPLY_COMPLAINT.INVESTIGATIONUSER',
            validators : [ 'notEmpty', 'strLength[0,20]' ],
            readonly : true,
            type : 'select',
            category : 'user-apply-complain'
        }, {
            field : 'investigationDate',
            title : 'T_CORE_APPLY_COMPLAINT.INVESTIGATIONDATE',
            type : 'date',
            readonly : true,
        }, {
            field : 'beAuditOrgNo',
            title : 'T_CORE_APPLY_COMPLAINT.BEAUDITORGNAME',
            validators : [ 'notEmpty', 'strLength[0,90]' ],
            readonly : true,
            type : 'select',
            category : 'org',
            onChange : function(field, value, rowIndex, oldValue, text) {
                Gikam.getComp('core-apply-complaint-detail-base-info-form').setData({
                    index : 0,
                    beAuditOrgName : text
                });
            }
        }, {
            field : 'investigationResult',
            title : 'T_CORE_APPLY_COMPLAINT.INVESTIGATIONRESULT',
            validators : [ 'notEmpty' ],
            readonly : true,
            colspan : 3,
            type : 'textarea',
            height : 80
        }, {
            field : 'suggestion',
            title : 'T_CORE_APPLY_COMPLAINT.SUGGESTION',
            validators : [ 'notEmpty' ],
            readonly : true,
            colspan : 3,
            type : 'textarea',
            height : 80
        }, {
            field : 'qmResult',
            title : 'T_CORE_APPLY_COMPLAINT.QMRESULT',
            validators : [ 'notEmpty' ],
            readonly : true,
            colspan : 3,
            type : 'textarea',
            height : 80
        }, {
            field : 'feedback',
            title : 'T_CORE_APPLY_COMPLAINT.FEEDBACK',
            validators : [ 'notEmpty' ],
            readonly : true,
            colspan : 3,
            type : 'textarea',
            height : 80
        }, {
            field : 'remark',
            title : 'T_CORE_APPLY_COMPLAINT.REMARK',
            validators : [ 'strLength[0,200]' ],
            colspan : 3,
            type : 'textarea',
            height : 80
        } ];
        switch (statusCode) {
        case 'accept':
            fields[12].readonly = false;
            fields[13].readonly = false;
            fields[14].readonly = false;
            fields[15].readonly = false;
            break;
        case 'quaAudit':
            break;
        case 'beAuditDone':
            break;
        case 'quaOrgAudit' :
            fields[16].readonly = false;
            break;
        case 'quasuggest' :
            fields[17].readonly = false;
            break;
        case 'feedback':
            fields[18].readonly = false;
            break;
        default :
        };
        return fields;
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreApplyComplaint.workflow.dbTable), {
            field : 'applyComplaintNo',
            title : 'T_CORE_APPLY_COMPLAINT.APPLYCOMPLAINTNO',
            width : 170,
            type : 'link'
        }, {
            field : 'createdByName',
            title : 'T_CORE_APPLY_COMPLAINT.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_CORE_APPLY_COMPLAINT.CREATEDTIME'
        }, {
            field : 'applyComplaintCorp',
            title : 'T_CORE_APPLY_COMPLAINT.APPLYCOMPLAINTCORP',
            width : 230
        }, {
            field : 'applyComplaintDate',
            title : 'T_CORE_APPLY_COMPLAINT.APPLYCOMPLAINTDATE'
        }, {
            field : 'folderNo',
            title : 'T_CORE_APPLY_COMPLAINT.FOLDERNO',
            width : 170
        }, {
            field : 'matName',
            title : 'T_CORE_APPLY_COMPLAINT.MATNAME'
        } ];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreApplyComplaint.workflow.dbTable), {
            field : 'applyComplaintNo',
            title : 'T_CORE_APPLY_COMPLAINT.APPLYCOMPLAINTNO',
            width : 170,
            type : 'link'
        }, {
            field : 'createdByName',
            title : 'T_CORE_APPLY_COMPLAINT.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_CORE_APPLY_COMPLAINT.CREATEDTIME'
        }, {
            field : 'applyComplaintCorp',
            title : 'T_CORE_APPLY_COMPLAINT.APPLYCOMPLAINTCORP',
            width : 230
        }, {
            field : 'applyComplaintDate',
            title : 'T_CORE_APPLY_COMPLAINT.APPLYCOMPLAINTDATE'
        }, {
            field : 'folderNo',
            title : 'T_CORE_APPLY_COMPLAINT.FOLDERNO',
            width : 170
        }, {
            field : 'matName',
            title : 'T_CORE_APPLY_COMPLAINT.MATNAME'
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/qms/apply-complaints',

    workflow : {
        dbTable : 'T_CORE_APPLY_COMPLAINT',
        columns : [ {
            field : 'id',
            title : 'T_CORE_APPLY_COMPLAINT.ID'
        } ]
    }

});
