var emcOaElectronicSignature = Gikam.getPageObject({

    baseUrl: IFM_CONTEXT + "/secure/emc/module/mdm/signatures",

    workflow : {
        dbTable : 'T_EMC_OA_ELECTRONIC_SIGNATURE',
        columns : [ {
            field : 'id',
            title : 'T_EMC_OA_ELECTRONIC_SIGNATURE.ID',
            width : 20,
        } ]
    },

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcOaElectronicSignature.workflow.dbTable), {
            field : 'reportOrgName',
            title : 'T_EMC_OA_ELECTRONIC_SIGNATURE.REPORTORGNAME',
            editor : true,
            validators : [ 'notEmpty' ],
            type : 'choose',
            category : 'org-simple',
            single : true,
            onBeforeChoose : function() {
                return {
                    orgCategory_SEQ : 'department'
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        reportOrgId : rows[0].id,
                        reportOrgName : rows[0].orgName
                    });
                }
            },
            onClean : function(field, value, index) {
                this.setData({
                    id : index,
                    reportOrgId : '',
                    reportOrgName : '',
                });
            },
        }, {
            field : 'reportType',
            title : 'T_EMC_OA_ELECTRONIC_SIGNATURE.REPORTTYPE',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'oaReportType'
            },
            validators : [ 'notEmpty' ]
        }, {
            field : 'auditorName',
            title : 'T_EMC_OA_ELECTRONIC_SIGNATURE.AUDITORNAME',
            type : 'choose',
            category : 'emp',
            validators : [ 'strLength[0, 60]', 'notEmpty' ],
            editor : true,
            single : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    var activedRow = Gikam.getComp('emc-oa-electronic-signature-edit-list-grid').getActivedRow();
                    this.setData({
                        id : activedRow.id,
                        auditorId : rows[0].id,
                        auditorName : rows[0].userName
                    });
                }
            },
            onClean : function(field, value, index) {
                var activedRow = Gikam.getComp('emc-oa-electronic-signature-edit-list-grid').getActivedRow();
                this.setData({
                    id : activedRow.id,
                    auditorId : '',
                    auditorName : '',
                });
            }
        }, {
            field : 'createdByName',
            title : 'T_DINGAN_PURCH.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_DINGAN_PURCH.CREATEDTIME'
        }, {
            field : 'remark',
            title : 'T_EMC_OA_ELECTRONIC_SIGNATURE.REMARK',
            editor : true,
        } ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcOaElectronicSignature.workflow.dbTable), {
            field : 'reportOrgName',
            title : 'T_EMC_OA_ELECTRONIC_SIGNATURE.REPORTORGNAME',
            type : 'choose',
            category : 'org-simple',
            single : true,
        }, {
            field : 'reportType',
            title : 'T_EMC_OA_ELECTRONIC_SIGNATURE.REPORTTYPE',
            editor : false,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'oaReportType'
            },
        }, {
            field : 'auditorName',
            title : 'T_EMC_OA_ELECTRONIC_SIGNATURE.AUDITORNAME',
            type : 'choose',
            category : 'emp',
            editor : false,
            single : true,
        }, {
            field : 'approverName',
            title : 'T_EMC_OA_ELECTRONIC_SIGNATURE.APPROVERNAME',
            type : 'choose',
            category : 'emp',
            validators : [ 'strLength[0, 60]', 'notEmpty' ],
            single : true,
            editor : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var grid = Gikam.getComp('emc-oa-electronic-signature-audit-list-grid');
                    grid.setData({
                        id : grid.getActivedRow().id,
                        approverId : rows[0].id,
                        approverName : rows[0].userName
                    });
                }
            },
            onClean : function() {
                var activedRow = Gikam.getComp('emc-oa-electronic-signature-audit-list-grid').getActivedRow();
                this.setData({
                    id : activedRow.id,
                    approverId : '',
                    approverName : '',
                });
            },
        }, {
            field : 'createdByName',
            title : 'T_DINGAN_PURCH.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_DINGAN_PURCH.CREATEDTIME'
        }, {
            field : 'remark',
            title : 'T_EMC_OA_ELECTRONIC_SIGNATURE.REMARK',
        } ];
    },

    getApproveListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcOaElectronicSignature.workflow.dbTable), {
            field : 'reportOrgName',
            title : 'T_EMC_OA_ELECTRONIC_SIGNATURE.REPORTORGNAME',
            type : 'choose',
            category : 'org-simple',
            single : true,
        }, {
            field : 'reportType',
            title : 'T_EMC_OA_ELECTRONIC_SIGNATURE.REPORTTYPE',
            editor : false,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'oaReportType'
            },
        }, {
            field : 'auditorName',
            title : 'T_EMC_OA_ELECTRONIC_SIGNATURE.AUDITORNAME',
            type : 'choose',
            category : 'emp',
            editor : false,
            single : true,
        }, {
            field : 'approverName',
            title : 'T_EMC_OA_ELECTRONIC_SIGNATURE.APPROVERNAME',
            type : 'choose',
            category : 'emp',
            single : true,
        }, {
            field : 'createdByName',
            title : 'T_DINGAN_PURCH.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_DINGAN_PURCH.CREATEDTIME'
        }, {
            field : 'remark',
            title : 'T_EMC_OA_ELECTRONIC_SIGNATURE.REMARK',
        } ];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcOaElectronicSignature.workflow.dbTable), {
            field : 'reportOrgName',
            title : 'T_EMC_OA_ELECTRONIC_SIGNATURE.REPORTORGNAME',
            type : 'choose',
            category : 'org-simple',
            single : true,
        }, {
            field : 'reportType',
            title : 'T_EMC_OA_ELECTRONIC_SIGNATURE.REPORTTYPE',
            editor : false,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'oaReportType'
            },
        }, {
            field : 'auditorName',
            title : 'T_EMC_OA_ELECTRONIC_SIGNATURE.AUDITORNAME',
            type : 'choose',
            category : 'emp',
            editor : false,
            single : true,
        }, {
            field : 'approverName',
            title : 'T_EMC_OA_ELECTRONIC_SIGNATURE.APPROVERNAME',
            type : 'choose',
            category : 'emp',
            single : true,
            editor : false,
        }, {
            field : 'createdByName',
            title : 'T_DINGAN_PURCH.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_DINGAN_PURCH.CREATEDTIME'
        }, {
            field : 'remark',
            title : 'T_EMC_OA_ELECTRONIC_SIGNATURE.REMARK',
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'reportOrgName',
            title : 'T_EMC_OA_ELECTRONIC_SIGNATURE.REPORTORGNAME',
            validators : [ 'strLength[0, 60]' ],
            preInsert : true,
            type : 'choose',
            category : 'org-simple',
            single : true,
            onBeforeChoose : function() {
                return {
                    orgCategory_SEQ : 'department'
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        reportOrgId : rows[0].id,
                        reportOrgName : rows[0].orgName
                    });
                }
            },
            onClean : function(field, value, index) {
                this.setData({
                    id : index,
                    reportOrgId : '',
                    reportOrgName : '',
                });
            },
        }, {
            field : 'reportType',
            title : 'T_EMC_OA_ELECTRONIC_SIGNATURE.REPORTTYPE',
            validators : [ 'strLength[0, 60]' ],
            preInsert : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'oaReportType'
            },
        } ];
    },

});
