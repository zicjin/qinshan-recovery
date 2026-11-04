var coreRiskIdentificaRegist = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, Gikam.status.getBpmnColumn(coreRiskIdentificaRegist.workflow.dbTable), {
            field : 'riskNo',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.RISKNO',
            type : 'link',
        }, {
            field : 'riskDesc',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.RISKDESC'
        }, {
            field : 'createdByName',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDTIME'
        }, {
            field : 'createdByOrgName',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDBYORGNAME'
        } ]
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'riskNo',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.RISKNO',
            readonly : true
        }, {
            field : 'createdByName',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDBYNAME',
            readonly : true
        }, {
            field : 'createdByOrgName',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDBYORGNAME',
            readonly : true
        }, {
            field : 'createdTime',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDTIME',
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            type : 'textarea',
            field : 'riskDesc',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.RISKDESC',
            validators : [ 'notEmpty' ],
            colspan : 2
        } ];
    },

    getRiskIdentificaFactorListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'riskDesc',
            title : 'T_CORE_RISK_FACTOR.RISKFACTOR'
        } ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, Gikam.status.getBpmnColumn(coreRiskIdentificaRegist.workflow.dbTable), {
            field : 'riskNo',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.RISKNO',
            type : 'link'
        }, {
            field : 'riskDesc',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.RISKDESC'
        }, {
            field : 'groupReviewResult',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.GROUPREVIEWRESULT'
        }, {
            field : 'createdByName',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDTIME'
        }, {
            field : 'createdByOrgName',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDBYORGNAME'
        } ]
    },

    getChooseListColumns : function(isSingle) {
        var columns = [ {
            checkbox : true
        }, {
            field : 'riskNo',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.RISKNO',
            type : 'link'
        }, {
            field : 'riskDesc',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.RISKDESC'
        }, {
            field : 'processStatus',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.PROCESSSTATUS'
        }, {
            field : 'groupReviewResult',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.GROUPREVIEWRESULT'
        }, {
            field : 'createdByName',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDTIME'
        }, {
            field : 'createdByOrgName',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDBYORGNAME'
        } ];
        if (isSingle == 1) {
            columns.splice(0, 1, {
                radio : true
            })
        }
        return columns;
    },

    getAssessRegistColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, Gikam.status.getBpmnColumn(coreRiskIdentificaRegist.workflow.dbTable), {
            field : 'riskNo',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.RISKNO',
        }, {
            field : 'createdByName',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDBYNAME'
        }, {
            field : 'createdByOrgName',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDBYORGNAME'
        }, {
            field : 'createdTime',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDTIME'
        }, {
            field : 'riskDesc',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.RISKDESC'
        } ]
    },

    getAssessRegistFactorFormFields : function() {
        return [ {
            field : 'severity',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.SEVERITY',
            type : 'number',
            validators : [ 'integer' ]
        }, {
            field : 'incidence',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.INCIDENCE',
            type : 'number',
            validators : [ 'integer' ]
        }, {
            field : 'beAuditOrgName',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.BEAUDITORGNAME',
            type : 'choose',
            category : 'org',
            targetFields : [ {
                beAuditOrgName : 'orgName'
            }, {
                beAuditOrgId : 'id'
            } ],
        }, {
            field : 'beAuditOrgId',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.BEAUDITORGID',
            readonly : true
        }, {
            field : 'refWorkFlowId',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.REFWORKFLOWID',
            type : 'choose',
            category : 'bpmn-proc',
            targetFields : [ {
                refWorkFlowId : 'procCode'
            }, {
                refWorkFlowType : 'ext$.servicename'
            } ]
        }, {
            field : 'refWorkFlowType',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.REFWORKFLOWTYPE',
            readonly : true
        }, {
            field : 'riskResolution',
            type : 'textarea',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.RISKRESOLUTION',
            colspan : 2,
        } ];
    },

    getAssessRegistFactorColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'riskDesc',
            title : 'T_CORE_RISK_FACTOR.RISKFACTOR'
        }, {
            field : 'severity',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.SEVERITY',
            type : 'number',
            editor : true,
            validators : [ 'notEmpty', 'integer' ]
        }, {
            field : 'incidence',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.INCIDENCE',
            type : 'number',
            editor : true,
            validators : [ 'notEmpty', 'integer' ]
        }, {
            field : 'riskRate',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.RISKRATE'
        }, {
            field : 'beAuditOrgName',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.BEAUDITORGNAME',
            type : 'choose',
            editor : true,
            category : 'org',
            validators : [ 'notEmpty' ],
            targetFields : [ {
                beAuditOrgName : 'orgName'
            } ],
            onAfterChoose : function(rows) {
                var orgId = rows[0].id;
                var grid = Gikam.getComp('core-risk-assess-edit-list-factor-grid');
                var id = grid.getActivedRow().id;
                grid.setData({
                    id : id,
                    beAuditOrgId : orgId
                });
            }
        }, {
            field : 'refWorkFlowId',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.REFWORKFLOWID',
            type : 'choose',
            editor : true,
            category : 'bpmn-proc',
            targetFields : [ {
                refWorkFlowId : 'procCode'
            }, {
                refWorkFlowType : 'ext$.servicename'
            } ]
        }, {
            field : 'refWorkFlowType',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.REFWORKFLOWTYPE'
        }, {
            field : 'riskResolution',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.RISKRESOLUTION',
            editor : true,
        } ]
    },

    getAssessAuditColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, Gikam.status.getBpmnColumn(coreRiskIdentificaRegist.workflow.dbTable), {
            field : 'riskNo',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.RISKNO',
        }, {
            field : 'createdByName',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDBYNAME',
        }, {
            field : 'createdByOrgName',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDBYORGNAME',
        }, {
            field : 'createdTime',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDTIME',
        }, {
            field : 'riskDesc',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.RISKDESC',
        }, {
            field : 'groupReviewResult',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.GROUPREVIEWRESULT',
            editor : true,
            validators : [ 'notEmpty' ],
        } ]
    },

    getAssessAuditRegistFactorColumns : function() {
        return [ {
            index : true
        }, {
            field : 'riskDesc',
            title : 'T_CORE_RISK_FACTOR.RISKFACTOR'
        }, {
            field : 'severity',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.SEVERITY',
        }, {
            field : 'incidence',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.INCIDENCE',
        }, {
            field : 'riskRate',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.RISKRATE'
        }, {
            field : 'beAuditOrgName',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.BEAUDITORGNAME',
        }, {
            field : 'refWorkFlowId',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.REFWORKFLOWID',
        }, {
            field : 'refWorkFlowType',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.REFWORKFLOWTYPE'
        }, {
            field : 'riskResolution',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.RISKRESOLUTION'
        } ]
    },

    getExecuteColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, Gikam.status.getBpmnColumn(coreRiskIdentificaRegist.workflow.dbTable), {
            field : 'riskNo',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.RISKNO',
        }, {
            field : 'createdByName',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDBYNAME'
        }, {
            field : 'createdByOrgName',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDBYORGNAME'
        }, {
            field : 'createdTime',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDTIME'
        }, {
            field : 'riskDesc',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.RISKDESC'
        }, {
            field : 'groupReviewResult',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.GROUPREVIEWRESULT',
        } ]
    },

    getExecuteRegistFactorColumns : function() {
        return [ {
            index : true
        }, {
            field : 'riskDesc',
            title : 'T_CORE_RISK_FACTOR.RISKFACTOR'
        }, {
            field : 'riskRate',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.RISKRATE',
        }, {
            field : 'refWorkFlowId',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.REFWORKFLOWID',
        }, {
            field : 'refWorkFlowType',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.REFWORKFLOWTYPE',
        }, {
            field : 'riskResolution',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.RISKRESOLUTION',
        }, {
            field : 'carryMonitorRecord',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.CARRYMONITORRECORD',
            editor : true,
            validators : [ 'notEmpty' ]
        }, {
            field : 'carryVerify',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.CARRYVERIFY',
            editor : true,
            validators : [ 'notEmpty' ]
        } ]
    },

    getExecuteAuditColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, Gikam.status.getBpmnColumn(coreRiskIdentificaRegist.workflow.dbTable), {
            field : 'riskNo',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.RISKNO',
        }, {
            field : 'createdByName',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDBYNAME'
        }, {
            field : 'createdByOrgName',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDBYORGNAME'
        }, {
            field : 'createdTime',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDTIME'
        }, {
            field : 'riskDesc',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.RISKDESC'
        }, {
            field : 'groupReviewResult',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.GROUPREVIEWRESULT',
        } ]
    },

    getExecuteAuditRegistFactorColumns : function() {
        return [ {
            index : true
        }, {
            field : 'riskDesc',
            title : 'T_CORE_RISK_FACTOR.RISKFACTOR'
        }, {
            field : 'riskRate',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.RISKRATE',
        }, {
            field : 'refWorkFlowId',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.REFWORKFLOWID',
        }, {
            field : 'refWorkFlowType',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.REFWORKFLOWTYPE',
        }, {
            field : 'riskResolution',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.RISKRESOLUTION',
        }, {
            field : 'carryMonitorRecord',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.CARRYMONITORRECORD',
        }, {
            field : 'carryVerify',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.CARRYVERIFY',
        } ]
    },

    getSearchColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'riskNo',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.RISKNO',
        }, {
            field : 'createdByName',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDBYNAME'
        }, {
            field : 'createdByOrgName',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDBYORGNAME'
        }, {
            field : 'createdTime',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.CREATEDTIME'
        }, {
            field : 'riskDesc',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.RISKDESC'
        }, {
            field : 'groupReviewResult',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.GROUPREVIEWRESULT',
        } ]
    },

    getRegistFactorColumns : function() {
        return [ {
            index : true
        }, {
            field : 'riskDesc',
            title : 'T_CORE_RISK_FACTOR.RISKFACTOR'
        }, {
            field : 'riskRate',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.RISKRATE',
        }, {
            field : 'refWorkFlowId',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.REFWORKFLOWID',
        }, {
            field : 'refWorkFlowType',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.REFWORKFLOWTYPE',
        }, {
            field : 'riskResolution',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.RISKRESOLUTION',
        }, {
            field : 'carryMonitorRecord',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.CARRYMONITORRECORD',
        }, {
            field : 'carryVerify',
            title : 'T_CORE_RISK_IDENTIFICA_FACTOR.CARRYVERIFY',
        } ]
    },

    loadDetailPage : function(id, type) {
        workspace.window.load(coreRiskIdentificaRegist.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            sourcePage : type
        }));
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/qms/risk-identifica-regists',
    
    registFactorBaseUrl : IFM_CONTEXT + '/secure/core/module/qms/risk-identifica-regists/{id}/risk-identifica-factors',
    workflow : {
        dbTable : 'T_CORE_RISK_IDENTIFICA_REGIST',
        columns : [ {
            field : 'id',
            title : 'T_CORE_RISK_IDENTIFICA_REGIST.ID'
        } ]
    }
});
