var emcInconformity = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcInconformity.workflow.dbTable), {
            field : 'qualityControlNo',
            title : 'T_EMC_INCONFORMITY.QUALITYCONTROLNO',
            width : 120
        }, {
            field : 'createdByName',
            title : 'T_EMC_INCONFORMITY.CREATEDBYNAME',
            width : 100
        } ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcInconformity.workflow.dbTable), {
            field : 'qualityControlNo',
            title : 'T_EMC_INCONFORMITY.QUALITYCONTROLNO',
            width : 120
        }, {
            field : 'createdByName',
            title : 'T_EMC_INCONFORMITY.CREATEDBYNAME',
            width : 100
        } ];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcInconformity.workflow.dbTable), {
            field : 'qualityControlNo',
            title : 'T_EMC_INCONFORMITY.QUALITYCONTROLNO',
            width : 120
        }, {
            field : 'createdByName',
            title : 'T_EMC_INCONFORMITY.CREATEDBYNAME',
            width : 100
        } ];
    },

    getInconformityFields : function() {
        return [ {
            field : 'source',
            title : 'T_EMC_INCONFORMITY.SOURCE',
            validators : [ 'strLength[0, 36]' ],
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'source'
            }
        }, {
            field : 'sourceNo',
            title : 'T_EMC_INCONFORMITY.SOURCENO',
            validators : [ 'strLength[0, 60]' ],
            editor : true,
            colspan : 2,
        }, {
            field : 'factDesc',
            title : 'T_EMC_INCONFORMITY.FACTDESC',
            validators : [ 'strLength[0, 540]' ],
            editor : true,
            type : 'textarea',
            colspan : 3,
        }, {
            field : 'inconformityBasis',
            title : 'T_EMC_INCONFORMITY.INCONFORMITYBASIS',
            validators : [ 'strLength[0, 540]' ],
            editor : true,
            type : 'textarea',
            colspan : 3,
        }, {
            field : 'correctActivity',
            title : 'T_EMC_INCONFORMITY.CORRECTACTIVITY',
            validators : [ 'strLength[0, 540]' ],
            editor : true,
            type : 'textarea',
            colspan : 3,
        }, {
            field : 'workCategory',
            title : 'T_EMC_INCONFORMITY.WORKCATEGORY',
            validators : [ 'strLength[0, 36]' ],
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'workCategory'
            }
        }, {
            field : 'evaluation',
            title : 'T_EMC_INCONFORMITY.EVALUATION',
            validators : [ 'strLength[0, 36]' ],
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'evaluation'
            }
        }, {
            field : 'inconformityOrg',
            title : 'T_EMC_INCONFORMITY.INCONFORMITYORG',
            validators : [ 'strLength[0, 270]' ],
            editor : true,
        }, {
            field : 'findUser',
            title : 'T_EMC_INCONFORMITY.FINDUSER',
            validators : [ 'strLength[0, 60]' ],
            editor : true,
        }, {
            field : 'findTime',
            title : 'T_EMC_INCONFORMITY.FINDTIME',
            type : 'date',
            editor : true,
        }, {
            field : 'orderNo',
            title : 'T_EMC_INCONFORMITY.ORDERNO',
            validators : [ 'strLength[0, 60]' ],
            editor : true,
        }, {
            field : 'correctAction',
            title : 'T_EMC_INCONFORMITY.CORRECTACTION',
            validators : [ 'strLength[0, 540]' ],
            editor : true,
            type : 'textarea',
            colspan : 3,
        }, {
            field : 'completeTime',
            title : 'T_EMC_INCONFORMITY.COMPLETETIME',
            type : 'date',
            editor : true,
        }, {
            field : 'responsibleUser',
            title : 'T_EMC_INCONFORMITY.RESPONSIBLEUSER',
            validators : [ 'strLength[0, 60]' ],
            editor : true,
            colspan : 2
        }, {
            field : 'ext$.departmenthead',
            title : 'T_EMC_INCONFORMITY.DEPARTMENTHEAD',
            editor : true,
            colspan : 2
        }, {
            field : 'ext$.recordtime',
            title : 'T_EMC_INCONFORMITY.RECORDTIME',
            type : 'date',
            editor : true,
            colspan : 2
        }, {
            field : 'ext$.causeanalysis',
            title : 'T_EMC_INCONFORMITY.CAUSEANALYSIS',
            editor : true,
        }, {
            field : 'ext$.deadline',
            title : 'T_EMC_INCONFORMITY.DEADLINE',
            type : 'date',
            editor : true,
            colspan : 2
        }, {
            field : 'ext$.analysistime',
            title : 'T_EMC_INCONFORMITY.ANALYSISTIME',
            editor : true,
            colspan : 2
        }, {
            field : 'ext$.approver',
            title : 'T_EMC_INCONFORMITY.APPROVER',
            editor : true,
            colspan : 2
        }, {
            field : 'ext$.approvaltime',
            title : 'T_EMC_INCONFORMITY.APPROVALTIME',
            type : 'date',
            editor : true,
            colspan : 2
        }, {
            field : 'ext$.correctdate',
            title : 'T_EMC_INCONFORMITY.CORRECTDATE',
            editor : true,
        }, {
            field : 'ext$.completionstatus',
            title : 'T_EMC_INCONFORMITY.COMPLETIONSTATUS',
            editor : true,
            colspan : 2
        }, {
            field : 'ext$.verification',
            title : 'T_EMC_INCONFORMITY.VERIFICATION',
            editor : true,
            colspan : 2
        }, {
            field : 'ext$.verifier',
            title : 'T_EMC_INCONFORMITY.VERIFIER',
            editor : true,
            colspan : 2
        }, {
            field : 'ext$.verifdate',
            title : 'T_EMC_INCONFORMITY.VERIFDATE',
            type : 'date',
            editor : true,
            colspan : 2
        }, ];
    },

    getInconformityFieldsNoEdit : function() {
        return [ {
            field : 'source',
            title : 'T_EMC_INCONFORMITY.SOURCE',
            validators : [ 'strLength[0, 36]' ],
            readonly : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'source'
            }
        }, {
            field : 'sourceNo',
            title : 'T_EMC_INCONFORMITY.SOURCENO',
            validators : [ 'strLength[0, 60]' ],
            readonly : true,
            colspan : 2,
        }, {
            field : 'factDesc',
            title : 'T_EMC_INCONFORMITY.FACTDESC',
            validators : [ 'strLength[0, 540]' ],
            readonly : true,
            type : 'textarea',
            colspan : 3,
        }, {
            field : 'inconformityBasis',
            title : 'T_EMC_INCONFORMITY.INCONFORMITYBASIS',
            validators : [ 'strLength[0, 540]' ],
            readonly : true,
            type : 'textarea',
            colspan : 3,
        }, {
            field : 'correctActivity',
            title : 'T_EMC_INCONFORMITY.CORRECTACTIVITY',
            validators : [ 'strLength[0, 540]' ],
            readonly : true,
            type : 'textarea',
            colspan : 3,
        }, {
            field : 'workCategory',
            title : 'T_EMC_INCONFORMITY.WORKCATEGORY',
            validators : [ 'strLength[0, 36]' ],
            readonly : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'workCategory'
            }
        }, {
            field : 'evaluation',
            title : 'T_EMC_INCONFORMITY.EVALUATION',
            validators : [ 'strLength[0, 36]' ],
            readonly : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'evaluation'
            }
        }, {
            field : 'inconformityOrg',
            title : 'T_EMC_INCONFORMITY.INCONFORMITYORG',
            validators : [ 'strLength[0, 270]' ],
            readonly : true,
        }, {
            field : 'findUser',
            title : 'T_EMC_INCONFORMITY.FINDUSER',
            validators : [ 'strLength[0, 60]' ],
            readonly : true,
        }, {
            field : 'findTime',
            title : 'T_EMC_INCONFORMITY.FINDTIME',
            type : 'date',
            readonly : true,
        }, {
            field : 'orderNo',
            title : 'T_EMC_INCONFORMITY.ORDERNO',
            validators : [ 'strLength[0, 60]' ],
            readonly : true,
        }, {
            field : 'correctAction',
            title : 'T_EMC_INCONFORMITY.CORRECTACTION',
            validators : [ 'strLength[0, 540]' ],
            readonly : true,
            type : 'textarea',
            colspan : 3,
        }, {
            field : 'completeTime',
            title : 'T_EMC_INCONFORMITY.COMPLETETIME',
            type : 'date',
            readonly : true,
        }, {
            field : 'responsibleUser',
            title : 'T_EMC_INCONFORMITY.RESPONSIBLEUSER',
            validators : [ 'strLength[0, 60]' ],
            readonly : true,
            colspan : 2
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/inconformitys',

    workflow : {
        dbTable : 'T_EMC_INCONFORMITY',
        columns : [ {
            field : 'id',
            title : 'T_EMC_INCONFORMITY.ID'
        } ]
    }
});
