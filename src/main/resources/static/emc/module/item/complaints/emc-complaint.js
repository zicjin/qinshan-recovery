var emcComplaint = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/complaints',
    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcComplaint.workflow.dbTable), {
            field : 'qualityControlNo',
            title : 'T_EMC_COMPLAINT.QUALITYCONTROLNO',
            width : 100
        }, {
            field : 'createdByName',
            title : 'T_EMC_COMPLAINT.CREATEDBYNAME',
            width : 100
        } ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcComplaint.workflow.dbTable), {
            field : 'qualityControlNo',
            title : 'T_EMC_COMPLAINT.QUALITYCONTROLNO',
            width : 100
        }, {
            field : 'createdByName',
            title : 'T_EMC_COMPLAINT.CREATEDBYNAME',
            width : 100
        } ];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcComplaint.workflow.dbTable), {
            field : 'qualityControlNo',
            title : 'T_EMC_COMPLAINT.QUALITYCONTROLNO',
            width : 100
        }, {
            field : 'createdByName',
            title : 'T_EMC_COMPLAINT.CREATEDBYNAME',
            width : 100
        } ];
    },

    getDetailFormFields : function() {
        return [ {
            field : 'complaintOrg',
            title : 'T_EMC_COMPLAINT.COMPLAINTORG',
            validators : [ 'strLength[0,90]' ]
        }, {
            field : 'complaintUser',
            title : 'T_EMC_COMPLAINT.COMPLAINTUSER',
            validators : [ 'strLength[0,80]' ]
        }, {
            field : 'complaintMethod',
            title : 'T_EMC_COMPLAINT.COMPLAINTMETHOD',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'complaintMethod'
            }
        }, {
            field : 'complaintTime',
            title : 'T_EMC_COMPLAINT.COMPLAINTTIME',
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'orderNo',
            title : 'T_EMC_COMPLAINT.ORDERNO',
            validators : [ 'strLength[0,18]' ]
        }, {
            field : 'classification',
            title : 'T_EMC_COMPLAINT.CLASSIFICATION',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'classification'
            }
        }, {
            field : 'complaintContent',
            title : 'T_EMC_COMPLAINT.COMPLAINTCONTENT',
            type : 'textarea',
            colspan : 3,
            height : 70,
            validators : [ 'strLength[0,180]' ]
        }, {
            field : 'solveProblem',
            title : 'T_EMC_COMPLAINT.SOLVEPROBLEM',
            type : 'textarea',
            colspan : 3,
            height : 70,
            validators : [ 'strLength[0,180]' ]
        }, {
            field : 'otherData',
            title : 'T_EMC_COMPLAINT.OTHERDATA',
            type : 'textarea',
            colspan : 3,
            height : 70,
            validators : [ 'strLength[0,180]' ]
        }, {
            field : 'responsibleLeader',
            title : 'T_EMC_COMPLAINT.RESPONSIBLELEADER',
            validators : [ 'strLength[0,80]' ]
        }, {
            field : 'inputUser',
            title : 'T_EMC_COMPLAINT.INPUTUSER',
            validators : [ 'strLength[0,80]' ]
        }, {
            field : 'surveyMember',
            title : 'T_EMC_COMPLAINT.SURVEYMEMBER',
            validators : [ 'strLength[0,80]' ]
        }, {
            field : 'surveyStartTime',
            title : 'T_EMC_COMPLAINT.SURVEYSTARTTIME',
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'surveyEndTime',
            title : 'T_EMC_COMPLAINT.SURVEYENDTIME',
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'surveyMethod',
            title : 'T_EMC_COMPLAINT.SURVEYMETHOD',
            type : 'textarea',
            colspan : 3,
            height : 70,
            validators : [ 'strLength[0,180]' ]
        }, {
            field : 'surveyResult',
            title : 'T_EMC_COMPLAINT.SURVEYRESULT',
            type : 'textarea',
            colspan : 3,
            height : 70,
            validators : [ 'strLength[0,180]' ]
        }, {
            field : 'processResult',
            title : 'T_EMC_COMPLAINT.PROCESSRESULT',
            type : 'textarea',
            colspan : 3,
            height : 70,
            validators : [ 'strLength[0,180]' ]
        }, {
            field : 'remark',
            title : 'T_EMC_COMPLAINT.REMARK',
            type : 'textarea',
            colspan : 3,
            height : 70,
            validators : [ 'strLength[0,180]' ]
        }, {
            field : 'qualityControlNo',
            title : 'T_EMC_COMPLAINT.QUALITYCONTROLNO',
        }, {
            field : 'ext$.contactnum',
            title : 'T_EMC_COMPLAINT.CONTACTNUM',
            editor : true,
        }, {
            field : 'ext$.investigate',
            title : 'T_EMC_COMPLAINT.INVESTIGATE',
            editor : true,
        }, {
            field : 'ext$.investigator',
            title : 'T_EMC_COMPLAINT.INVESTIGATOR',
            editor : true,
        }, {
            field : 'ext$.treatmentresults',
            title : 'T_EMC_COMPLAINT.TREATMENTRESULTS',
            colspan : 3,
            editor : true,
        }, {
            field : 'ext$.responsdate',
            title : 'T_EMC_COMPLAINT.RESPONSDATE',
            type : 'date',
            editor : true,
        }, {
            field : 'ext$.trackverificate',
            title : 'T_EMC_COMPLAINT.TRACKVERIFICATE',
            editor : true,
        }, {
            field : 'ext$.verifier',
            title : 'T_EMC_COMPLAINT.VERIFIER',
            editor : true,
        }, {
            field : 'ext$.verificatedate',
            title : 'T_EMC_COMPLAINT.VERIFICATEDATE',
            type : 'date',
            editor : true,
        }, {
            field : 'ext$.solveintime',
            title : 'T_EMC_COMPLAINT.SOLVEINTIME',
            type: 'radioGroup',
            items: [{"value": '及时', "text": "及时"},
                {"value": '不及时', "text": "不及时"}],
        }, {
            field : 'ext$.explainreason',
            title : 'T_EMC_COMPLAINT.EXPLAINREASON',
            type: 'radioGroup',
            items: [{"value": '解释', "text": "解释"},
                {"value": '未解释', "text": "未解释"}],
        }, {
            field : 'ext$.satisfaction',
            title : 'T_EMC_COMPLAINT.SATISFACTION',
            type: 'radioGroup',
            items: [{"value": '满意', "text": "满意"},
            	{"value": '较满意', "text": "较满意"},
            	{"value": '一般', "text": "一般"},
                {"value": '较差', "text": "较差"}],
        }, {
            field : 'ext$.qualitymanager',
            title : 'T_EMC_COMPLAINT.QUALITYMANAGER',
            editor : true,
        }  ];
    },

    workflow : {
        dbTable : 'T_EMC_COMPLAINT',
        columns : [ {
            field : 'id',
            title : 'T_EMC_COMPLAINT.ID'
        } ]
    }
});
