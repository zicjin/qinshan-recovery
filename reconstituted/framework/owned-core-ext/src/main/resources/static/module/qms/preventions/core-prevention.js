var corePrevention = Gikam.getPageObject({

    getListColumns : function() {
        return [{
            checkbox : true
        }, Gikam.status.getBpmnColumn(corePrevention.workflow.dbTable), {
            field : 'preventionNo',
            title : 'T_CORE_PREVENTION.PREVENTIONNO',
            type : 'link'
        }, {
            field : 'createdByName',
            title : 'T_CORE_PREVENTION.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_CORE_PREVENTION.CREATEDTIME'
        }];
    },

    getSearchListColumns : function() {
        return [{
            checkbox : true
        }, {
            index : true
        }, {
            field : 'preventionNo',
            title : 'T_CORE_PREVENTION.PREVENTIONNO',
            type : 'link'
        }, Gikam.status.getBpmnColumn(corePrevention.workflow.dbTable), {
            field : 'createdByName',
            title : 'T_CORE_PREVENTION.CREATEDBYNAME'
        }, {
            field : 'createdByOrgName',
            title : 'T_CORE_PREVENTION.CREATEDBYORGNAME',
            readonly : true
        }, {
            field : 'createdTime',
            title : 'T_CORE_PREVENTION.CREATEDTIME'
        }]
    },

    getDetailPageItemListColumns : function() {
        return [{
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_CORE_RECTIFY_ITEM.ORDERNO',
            editor : true
        }, {
            field : 'sourceType',
            title : 'T_CORE_PREVENTION_ITEM.SOURCETYPE',
            type : 'select',
            editor : true,
            category : 'src-type-category',
            validators : ['notEmpty']
        }, {
            field : 'processNo',
            title : 'T_CORE_PREVENTION_ITEM.PROCESSNO',
            type : 'choose',
            single : true,
            category : 'bpmn-proc',
            targetFields : [{
                processNo : 'procCode'
            }],
            editor : true
        }, {
            field : 'discoveryDate',
            title : 'T_CORE_PREVENTION_ITEM.DISCOVERYDATE',
            type : 'date',
            validators : ['notEmpty'],
            editor : true
        }, {
            field : 'promDesc',
            title : 'T_CORE_PREVENTION_ITEM.PROMDESC',
            validators : ['notEmpty'],
            editor : true
        }]
    },

    getBaseInfoFormFields : function() {
        return [{
            field : 'preventionNo',
            title : 'T_CORE_PREVENTION.PREVENTIONNO',
            readonly : true
        }, {
            field : 'createdByName',
            title : 'T_CORE_PREVENTION.CREATEDBYNAME',
            readonly : true
        }, {
            field : 'createdTime',
            title : 'T_CORE_PREVENTION.CREATEDTIME',
            readonly : true
        }, {
            field : 'createdByOrgName',
            title : 'T_CORE_PREVENTION.CREATEDBYORGNAME',
            readonly : true
        }];
    },

    getItemListColumns : function() {
        return [{
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_CORE_PREVENTION_ITEM.ORDERNO'
        }, {
            field : 'sourceType',
            title : 'T_CORE_PREVENTION_ITEM.SOURCETYPE',
            type : 'select',
            category : 'src-type-category'
        }, {
            field : 'processNo',
            title : 'T_CORE_PREVENTION_ITEM.PROCESSNO',
            type : 'choose'
        }, {
            field : 'discoveryDate',
            title : 'T_CORE_PREVENTION_ITEM.DISCOVERYDATE',
            type : 'date'
        }, {
            field : 'promDesc',
            title : 'T_CORE_PREVENTION_ITEM.PROMDESC'
        }, {
            field : 'beAuditOrgName',
            title : 'T_CORE_PREVENTION_ITEM.BEAUDITORGNAME',
            type : 'choose',
            single : true,
            category : 'org',
            editor : true,
            validators : ['notEmpty'],
            targetFields : [{
                beAuditOrgName : 'orgName'
            }, {
                beAuditOrgId : 'orderNo'
            }]
        }, {
            field : 'preventionGrade',
            title : 'T_CORE_PREVENTION_ITEM.PREVENTIONGRADE',
            editor : true,
            type : 'select',
            category : 'grade-category',
            validators : ['notEmpty']
        }, {
            field : 'deadline',
            title : 'T_CORE_PREVENTION_ITEM.DEADLINE',
            type : 'date',
            editor : true,
            validators : ['notEmpty']
        }, {
            field : 'riskAssessment',
            title : 'T_CORE_PREVENTION_ITEM.RISKASSESSMENT',
            editor : true,
            validators : ['notEmpty']
        }, {
            field : 'preventionGuidance',
            title : 'T_CORE_PREVENTION_ITEM.PREVENTIONGUIDANCE',
            editor : true,
            validators : ['notEmpty']
        }];
    },

    getGroupAuditItemListColumns : function() {
        return [{
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_CORE_PREVENTION_ITEM.ORDERNO'
        }, {
            field : 'sourceType',
            title : 'T_CORE_PREVENTION_ITEM.SOURCETYPE',
            type : 'select',
            category : 'src-type-category'
        }, {
            field : 'processNo',
            title : 'T_CORE_PREVENTION_ITEM.PROCESSNO',
            type : 'choose'
        }, {
            field : 'discoveryDate',
            title : 'T_CORE_PREVENTION_ITEM.DISCOVERYDATE',
            type : 'date'
        }, {
            field : 'promDesc',
            title : 'T_CORE_PREVENTION_ITEM.PROMDESC'
        }, {
            field : 'beAuditOrgName',
            title : 'T_CORE_PREVENTION_ITEM.BEAUDITORGNAME',
            type : 'choose',
            single : true,
            category : 'org',
            targetFields : [{
                beAuditOrgName : 'orgName'
            }, {
                beAuditOrgId : 'orderNo'
            }]
        }, {
            field : 'preventionGrade',
            title : 'T_CORE_PREVENTION_ITEM.PREVENTIONGRADE',
            type : 'select',
            category : 'grade-category'
        }, {
            field : 'deadline',
            title : 'T_CORE_PREVENTION_ITEM.DEADLINE',
            type : 'date'
        }, {
            field : 'riskAssessment',
            title : 'T_CORE_PREVENTION_ITEM.RISKASSESSMENT'
        }, {
            field : 'preventionGuidance',
            title : 'T_CORE_PREVENTION_ITEM.PREVENTIONGUIDANCE'
        }, {
            field : 'reviewResult',
            title : 'T_CORE_PREVENTION_ITEM.REVIEWRESULT',
            editor : true,
            validators : ['notEmpty']
        }];
    },

    getPlanItemListColumns : function() {
        return [{
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_CORE_PREVENTION_ITEM.ORDERNO'
        }, {
            field : 'sourceType',
            title : 'T_CORE_PREVENTION_ITEM.SOURCETYPE',
            type : 'select',
            category : 'src-type-category'
        }, {
            field : 'processNo',
            title : 'T_CORE_PREVENTION_ITEM.PROCESSNO',
            type : 'choose'
        }, {
            field : 'discoveryDate',
            title : 'T_CORE_PREVENTION_ITEM.DISCOVERYDATE',
            type : 'date'
        }, {
            field : 'promDesc',
            title : 'T_CORE_PREVENTION_ITEM.PROMDESC'
        }, {
            field : 'beAuditOrgName',
            title : 'T_CORE_PREVENTION_ITEM.BEAUDITORGNAME',
            type : 'choose',
            single : true,
            category : 'org',
            targetFields : [{
                beAuditOrgName : 'orgName'
            }, {
                beAuditOrgId : 'orderNo'
            }]
        }, {
            field : 'preventionGrade',
            title : 'T_CORE_PREVENTION_ITEM.PREVENTIONGRADE',
            type : 'select',
            category : 'grade-category'
        }, {
            field : 'deadline',
            title : 'T_CORE_PREVENTION_ITEM.DEADLINE',
            type : 'date'
        }, {
            field : 'riskAssessment',
            title : 'T_CORE_PREVENTION_ITEM.RISKASSESSMENT'
        }, {
            field : 'preventionGuidance',
            title : 'T_CORE_PREVENTION_ITEM.PREVENTIONGUIDANCE'
        }, {
            field : 'reviewResult',
            title : 'T_CORE_PREVENTION_ITEM.REVIEWRESULT',
        }, {
            field : 'causeanAlysis',
            title : 'T_CORE_PREVENTION_ITEM.CAUSEANALYSIS',
            editor : true,
            validators : ['notEmpty']
        }, {
            field : 'preventionCarryPlan',
            title : 'T_CORE_PREVENTION_ITEM.PREVENTIONCARRYPLAN',
            editor : true,
            validators : ['notEmpty']
        }, {
            field : 'expectedDate',
            title : 'T_CORE_PREVENTION_ITEM.EXPECTEDDATE',
            type : 'date',
            editor : true,
            validators : ['notEmpty']
        }]
    },

    getExecuteItemListColumns : function() {
        return [{
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_CORE_PREVENTION_ITEM.ORDERNO'
        }, {
            field : 'sourceType',
            title : 'T_CORE_PREVENTION_ITEM.SOURCETYPE',
            type : 'select',
            category : 'src-type-category'
        }, {
            field : 'processNo',
            title : 'T_CORE_PREVENTION_ITEM.PROCESSNO',
            type : 'choose'
        }, {
            field : 'discoveryDate',
            title : 'T_CORE_PREVENTION_ITEM.DISCOVERYDATE',
            type : 'date'
        }, {
            field : 'promDesc',
            title : 'T_CORE_PREVENTION_ITEM.PROMDESC'
        }, {
            field : 'beAuditOrgName',
            title : 'T_CORE_PREVENTION_ITEM.BEAUDITORGNAME',
            type : 'choose',
            single : true,
            category : 'org',
            targetFields : [{
                beAuditOrgName : 'orgName'
            }, {
                beAuditOrgId : 'orderNo'
            }]
        }, {
            field : 'preventionGrade',
            title : 'T_CORE_PREVENTION_ITEM.PREVENTIONGRADE',
            type : 'select',
            category : 'grade-category'
        }, {
            field : 'deadline',
            title : 'T_CORE_PREVENTION_ITEM.DEADLINE',
            type : 'date'
        }, {
            field : 'riskAssessment',
            title : 'T_CORE_PREVENTION_ITEM.RISKASSESSMENT'
        }, {
            field : 'preventionGuidance',
            title : 'T_CORE_PREVENTION_ITEM.PREVENTIONGUIDANCE'
        }, {
            field : 'reviewResult',
            title : 'T_CORE_PREVENTION_ITEM.REVIEWRESULT',
        }, {
            field : 'causeanAlysis',
            title : 'T_CORE_PREVENTION_ITEM.CAUSEANALYSIS'
        }, {
            field : 'preventionCarryPlan',
            title : 'T_CORE_PREVENTION_ITEM.PREVENTIONCARRYPLAN'
        }, {
            field : 'expectedDate',
            title : 'T_CORE_PREVENTION_ITEM.EXPECTEDDATE',
            type : 'date'
        }, {
            field : 'preventionResultDesc',
            title : 'T_CORE_PREVENTION_ITEM.PREVENTIONRESULTDESC',
            editor : true
        }]
    },

    loadDetailPage : function(id, status) {
        workspace.window.load(this.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            status : status
        }));
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/qms/preventions',

    workflow : {
        dbTable : 'T_CORE_PREVENTION',
        columns : [{
            field : 'id',
            title : 'T_CORE_PREVENTION.ID'
        }]
    }

});
