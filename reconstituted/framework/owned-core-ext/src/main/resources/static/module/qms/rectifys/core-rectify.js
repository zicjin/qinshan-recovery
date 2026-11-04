var coreRectify = Gikam.getPageObject({

    getListColumns : function() {
        return [{
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreRectify.workflow.dbTable), {
            field : 'rectifyNo',
            title : 'T_CORE_RECTIFY.RECTIFYNO',
            type : 'link'
        }, {
            field : 'createdByName',
            title : 'T_CORE_RECTIFY.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_CORE_RECTIFY.CREATEDTIME'
        }];
    },

    getSearchListColumns : function() {
        return [{
            checkbox : true
        }, {
            index : true
        },{
            field : 'rectifyNo',
            title : 'T_CORE_RECTIFY.RECTIFYNO',
            type : 'link'
        }, Gikam.status.getBpmnColumn(coreRectify.workflow.dbTable), {
            field : 'createdByName',
            title : 'T_CORE_RECTIFY.CREATEDBYNAME'
        }, {
            field : 'createdByOrgName',
            title : 'T_CORE_RECTIFY.CREATEDBYORGNAME',
            readonly : true
        }, {
            field : 'createdTime',
            title : 'T_CORE_RECTIFY.CREATEDTIME'
        }]
    },

    getBaseInfoFormFields : function() {
        return [{
            field : 'rectifyNo',
            title : 'T_CORE_RECTIFY.RECTIFYNO',
            readonly : true
        }, {
            field : 'createdByName',
            title : 'T_CORE_RECTIFY.CREATEDBYNAME',
            readonly : true
        }, {
            field : 'createdTime',
            title : 'T_CORE_RECTIFY.CREATEDTIME',
            readonly : true
        }, {
            field : 'createdByOrgName',
            title : 'T_CORE_RECTIFY.CREATEDBYORGNAME',
            readonly : true
        }];
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
            title : 'T_CORE_RECTIFY_ITEM.SOURCETYPE',
            type : 'select',
            editor : true,
            category : 'src-type-category',
            validators : ['notEmpty']
        }, {
            field : 'processNo',
            title : 'T_CORE_RECTIFY_ITEM.PROCESSNO',
            type : 'choose',
            single : true,
            category : 'bpmn-proc',
            targetFields : [{
                processNo : 'procCode'
            }],
            editor : true
        }, {
            field : 'discoveryDate',
            title : 'T_CORE_RECTIFY_ITEM.DISCOVERYDATE',
            type : 'date',
            validators : ['notEmpty'],
            editor : true
        }, {
            field : 'promDesc',
            title : 'T_CORE_RECTIFY_ITEM.PROMDESC',
            validators : ['notEmpty'],
            editor : true
        }]
    },

    getItemListColumns : function() {
        return [{
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_CORE_RECTIFY_ITEM.ORDERNO'
        }, {
            field : 'sourceType',
            title : 'T_CORE_RECTIFY_ITEM.SOURCETYPE',
            type : 'select',
            category : 'src-type-category'
        }, {
            field : 'processNo',
            title : 'T_CORE_RECTIFY_ITEM.PROCESSNO',
            type : 'choose'
        }, {
            field : 'discoveryDate',
            title : 'T_CORE_RECTIFY_ITEM.DISCOVERYDATE',
            type : 'date'
        }, {
            field : 'promDesc',
            title : 'T_CORE_RECTIFY_ITEM.PROMDESC'
        }, {
            field : 'beAuditOrgName',
            title : 'T_CORE_RECTIFY_ITEM.BEAUDITORGNAME',
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
            field : 'rectifyGrade',
            title : 'T_CORE_RECTIFY_ITEM.RECTIFYGRADE',
            editor : true,
            type : 'select',
            category : 'grade-category',
            validators : ['notEmpty']
        }, {
            field : 'deadline',
            title : 'T_CORE_RECTIFY_ITEM.DEADLINE',
            type : 'date',
            editor : true,
            validators : ['notEmpty']
        }, {
            field : 'riskAssessment',
            title : 'T_CORE_RECTIFY_ITEM.RISKASSESSMENT',
            editor : true,
            validators : ['notEmpty']
        }, {
            field : 'rectifyGuidance',
            title : 'T_CORE_RECTIFY_ITEM.RECTIFYGUIDANCE',
            editor : true,
            validators : ['notEmpty']
        }];
    },

    getGroupAuditItemListColumns : function() {
        return [{
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_CORE_RECTIFY_ITEM.ORDERNO'
        }, {
            field : 'sourceType',
            title : 'T_CORE_RECTIFY_ITEM.SOURCETYPE',
            type : 'select',
            category : 'src-type-category'
        }, {
            field : 'processNo',
            title : 'T_CORE_RECTIFY_ITEM.PROCESSNO',
            type : 'choose'
        }, {
            field : 'discoveryDate',
            title : 'T_CORE_RECTIFY_ITEM.DISCOVERYDATE',
            type : 'date'
        }, {
            field : 'promDesc',
            title : 'T_CORE_RECTIFY_ITEM.PROMDESC'
        }, {
            field : 'beAuditOrgName',
            title : 'T_CORE_RECTIFY_ITEM.BEAUDITORGNAME',
            type : 'choose',
            single : true,
            category : 'org',
            targetFields : [{
                beAuditOrgName : 'orgName'
            }, {
                beAuditOrgId : 'orderNo'
            }]
        }, {
            field : 'rectifyGrade',
            title : 'T_CORE_RECTIFY_ITEM.RECTIFYGRADE',
            type : 'select',
            category : 'grade-category',
        }, {
            field : 'deadline',
            title : 'T_CORE_RECTIFY_ITEM.DEADLINE',
            type : 'date',
        }, {
            field : 'riskAssessment',
            title : 'T_CORE_RECTIFY_ITEM.RISKASSESSMENT',
        }, {
            field : 'rectifyGuidance',
            title : 'T_CORE_RECTIFY_ITEM.RECTIFYGUIDANCE',
        }, {
            field : 'reviewResult',
            title : 'T_CORE_RECTIFY_ITEM.REVIEWRESULT',
            editor : true,
            validators : ['notEmpty']
        }];
    },

    getPlanItemListColumns : function() {
        return [{
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_CORE_RECTIFY_ITEM.ORDERNO'
        }, {
            field : 'sourceType',
            title : 'T_CORE_RECTIFY_ITEM.SOURCETYPE',
            type : 'select',
            category : 'src-type-category'
        }, {
            field : 'processNo',
            title : 'T_CORE_RECTIFY_ITEM.PROCESSNO',
            type : 'choose'
        }, {
            field : 'discoveryDate',
            title : 'T_CORE_RECTIFY_ITEM.DISCOVERYDATE',
            type : 'date'
        }, {
            field : 'promDesc',
            title : 'T_CORE_RECTIFY_ITEM.PROMDESC'
        }, {
            field : 'beAuditOrgName',
            title : 'T_CORE_RECTIFY_ITEM.BEAUDITORGNAME',
            type : 'choose',
            single : true,
            category : 'org',
            targetFields : [{
                beAuditOrgName : 'orgName'
            }, {
                beAuditOrgId : 'orderNo'
            }]
        }, {
            field : 'rectifyGrade',
            title : 'T_CORE_RECTIFY_ITEM.RECTIFYGRADE',
            type : 'select',
            category : 'grade-category'
        }, {
            field : 'deadline',
            title : 'T_CORE_RECTIFY_ITEM.DEADLINE',
            type : 'date'
        }, {
            field : 'riskAssessment',
            title : 'T_CORE_RECTIFY_ITEM.RISKASSESSMENT'
        }, {
            field : 'rectifyGuidance',
            title : 'T_CORE_RECTIFY_ITEM.RECTIFYGUIDANCE'
        }, {
            field : 'reviewResult',
            title : 'T_CORE_RECTIFY_ITEM.REVIEWRESULT',
        }, {
            field : 'deviationReason',
            title : 'T_CORE_RECTIFY_ITEM.DEVIATIONREASON',
            editor : true,
            type : 'select',
            category : 'deviation-reason-category',
            validators : ['notEmpty']
        }, {
            field : 'causeanAlysis',
            title : 'T_CORE_RECTIFY_ITEM.CAUSEANALYSIS',
            editor : true,
            validators : ['notEmpty']
        }, {
            field : 'rectifyCarryPlan',
            title : 'T_CORE_RECTIFY_ITEM.RECTIFYCARRYPLAN',
            editor : true,
            validators : ['notEmpty']
        }, {
            field : 'expectedDate',
            title : 'T_CORE_RECTIFY_ITEM.EXPECTEDDATE',
            editor : true,
            type : 'date',
            validators : ['notEmpty']
        }];
    },

    getExecuteItemListColumns : function() {
        return [{
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_CORE_RECTIFY_ITEM.ORDERNO'
        }, {
            field : 'sourceType',
            title : 'T_CORE_RECTIFY_ITEM.SOURCETYPE',
            type : 'select',
            category : 'src-type-category'
        }, {
            field : 'processNo',
            title : 'T_CORE_RECTIFY_ITEM.PROCESSNO',
            type : 'choose'
        }, {
            field : 'discoveryDate',
            title : 'T_CORE_RECTIFY_ITEM.DISCOVERYDATE',
            type : 'date'
        }, {
            field : 'promDesc',
            title : 'T_CORE_RECTIFY_ITEM.PROMDESC'
        }, {
            field : 'beAuditOrgName',
            title : 'T_CORE_RECTIFY_ITEM.BEAUDITORGNAME',
            type : 'choose',
            single : true,
            category : 'org',
            targetFields : [{
                beAuditOrgName : 'orgName'
            }, {
                beAuditOrgId : 'orderNo'
            }]
        }, {
            field : 'rectifyGrade',
            title : 'T_CORE_RECTIFY_ITEM.RECTIFYGRADE',
            type : 'select',
            category : 'grade-category'
        }, {
            field : 'deadline',
            title : 'T_CORE_RECTIFY_ITEM.DEADLINE',
            type : 'date'
        }, {
            field : 'riskAssessment',
            title : 'T_CORE_RECTIFY_ITEM.RISKASSESSMENT'
        }, {
            field : 'rectifyGuidance',
            title : 'T_CORE_RECTIFY_ITEM.RECTIFYGUIDANCE'
        }, {
            field : 'reviewResult',
            title : 'T_CORE_RECTIFY_ITEM.REVIEWRESULT',
        }, {
            field : 'deviationReason',
            title : 'T_CORE_RECTIFY_ITEM.DEVIATIONREASON',
            type : 'select',
            category : 'deviation-reason-category'
        }, {
            field : 'causeanAlysis',
            title : 'T_CORE_RECTIFY_ITEM.CAUSEANALYSIS',
        }, {
            field : 'rectifyCarryPlan',
            title : 'T_CORE_RECTIFY_ITEM.RECTIFYCARRYPLAN',
        }, {
            field : 'expectedDate',
            title : 'T_CORE_RECTIFY_ITEM.EXPECTEDDATE',
            type : 'date'
        }, {
            field : 'rectifyResultDesc',
            title : 'T_CORE_RECTIFY_ITEM.RECTIFYRESULTDESC',
            editor : true
        }];
    },

    loadDetailPage : function(id, status) {
        workspace.window.load(this.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            status : status
        }));
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/qms/rectifys',

    workflow : {
        dbTable : 'T_CORE_RECTIFY',
        columns : [{
            field : 'id',
            title : 'T_CORE_RECTIFY.ID'
        }]
    }

});
