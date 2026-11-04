var coreNcr = Gikam.getPageObject({

    getListColumns : function() {
        return [{
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreNcr.workflow.dbTable), {
            field : 'ncrNo',
            type : 'link',
            title : 'T_CORE_NCR.NCRNO'
        }, {
            field : 'ncrContent',
            title : 'T_CORE_NCR.NCRCONTENT'
        }, {
            field : 'ncrStandard',
            title : 'T_CORE_NCR.NCRSTANDARD'
        }, {
            field : 'beAuditOrgName',
            title : 'T_CORE_NCR.BEAUDITORGNAME'
        }, {
            field : 'beAuditUserName',
            title : 'T_CORE_NCR.BEAUDITUSERNAME'
        }, {
            field : 'ncrSource',
            title : 'T_CORE_NCR.NCRSOURCE'
        }, {
            field : 'ncrSourceNo',
            title : 'T_CORE_NCR.NCRSOURCENO'
        }, {
            field : 'discoveryUserName',
            title : 'T_CORE_NCR.DISCOVERYUSERNAME'
        }, {
            field : 'discoveryDate',
            title : 'T_CORE_NCR.DISCOVERYDATE'
        }, {
            field : 'discoveryOrgName',
            title : 'T_CORE_NCR.DISCOVERYORGNAME'
        }, {
            field : 'deadline',
            title : 'T_CORE_NCR.DEADLINE'
        }, {
            field : 'checkStartDate',
            title : 'T_CORE_NCR.CHECKSTARTDATE'
        }, {
            field : 'checkType',
            title : 'T_CORE_NCR.CHECKTYPE'
        }, {
            field : 'remark',
            title : 'T_CORE_NCR.REMARK'
        }];
    },

    loadDetailPage : function(id, status) {
        workspace.window.load(this.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            status : status
        }));
    },

    getBaseInfoFormFields : function() {
        var _this = this;
        return [{
            field : 'ncrNo',
            title : 'T_CORE_NCR.NCRNO',
            readonly : true,
            validators : ['notEmpty', 'strLength[0,12]']
        }, {
            field : 'ncrSource',
            type : 'select',
            title : 'T_CORE_NCR.NCRSOURCE',
            category : 'ncr-source-category-category',
            validators : ['strLength[0,90]'],
            onChange : function(field, value, rowIndex, oldValue, text) {
                var fields = _this.getBaseInfoFormFields();
                fields[2].category = value;
                fields[2].targetFields = [{
                    ncrSourceNo : 'auditNo'
                }];
                var form = Gikam.getComp('core-ncr-detail-base-info-form');
                var data = form.getData();
                form.refreshFields(fields);
                form.loadData(data);
            }
        }, {
            field : 'ncrSourceNo',
            type : 'choose',
            title : 'T_CORE_NCR.NCRSOURCENO',
            validators : ['strLength[0,30]']
        }, {
            field : 'discoveryOrgName',
            title : 'T_CORE_NCR.DISCOVERYORGNAME',
            validators : ['notEmpty', 'strLength[0,90]']
        }, {
            field : 'deadline',
            title : 'T_CORE_NCR.DEADLINE',
            type : 'date',
            validators : ['notEmpty']
        }, {
            field : 'measuresReq',
            type : 'select',
            category : 'measures-required-category',
            title : 'T_CORE_NCR.MEASURESREQ',
            validators : ['notEmpty', 'strLength[0,1333]']
        }, {
            field : 'discoveryUserName',
            title : 'T_CORE_NCR.DISCOVERYUSERNAME',
            validators : ['notEmpty', 'strLength[0,20]']
        }, {
            field : 'checkType',
            title : 'T_CORE_NCR.CHECKTYPE',
            type : 'select',
            category : 'ncr-check-type-category',
            validators : ['notEmpty', 'strLength[0,90]']
        }, {
            field : 'checkStartDate',
            title : 'T_CORE_NCR.CHECKSTARTDATE',
            type : 'date',
            colspan : 0.5,
            validators : ['notEmpty']
        }, {
            field : 'checkEndDate',
            title : 'T_CORE_NCR.CHECKENDDATE',
            type : 'date',
            colspan : 0.5,
            validators : ['notEmpty']
        }, {
            field : 'discoveryDate',
            title : 'T_CORE_NCR.DISCOVERYDATE',
            type : 'date',
            validators : ['notEmpty']
        }, {
            field : 'beAuditOrgName',
            type : 'choose',
            title : 'T_CORE_NCR.BEAUDITORGNAME',
            validators : ['notEmpty', 'strLength[0,90]'],
            single : true,
            category : 'org',
            targetFields : [{
                beAuditOrgName : 'orgName'
            }, {
                beAuditOrgId : 'orderNo'
            }]
        }, {
            field : 'beAuditUserName',
            type : 'choose',
            title : 'T_CORE_NCR.BEAUDITUSERNAME',
            validators : ['notEmpty', 'strLength[0,20]'],
            category : 'user',
            single : true,
            targetFields : [{
                beAuditUserName : 'userName'
            }, {
                beAuditUserId : 'id'
            }]
        }, {
            field : 'ncrStandard',
            type : 'textarea',
            height : 150,
            title : 'T_CORE_NCR.NCRSTANDARD',
            validators : ['notEmpty', 'strLength[0,90]'],
            colspan : 3
        }, {
            field : 'ncrContent',
            type : 'textarea',
            height : 150,
            title : 'T_CORE_NCR.NCRCONTENT',
            colspan : 3,
            validators : ['notEmpty', 'strLength[0,20]']
        }, {
            field : 'ncrGrade',
            type : 'textarea',
            height : 150,
            title : 'T_CORE_NCR.NCRGRADE',
            colspan : 3,
            validators : ['notEmpty', 'strLength[0,30]']
        }, {
            field : 'ncrRemark',
            type : 'textarea',
            height : 150,
            title : 'T_CORE_NCR.NCRREMARK',
            validators : ['strLength[0,1333]'],
            colspan : 3
        }];
    },

    getDealFormFields : function() {
        return [{
            field : 'ncrConfirm',
            title : 'T_CORE_NCR.NCRCONFIRM',
            type : 'textarea',
            height : 150,
            colspan : 3,
            validators : ['notEmpty', 'strLength[0,1333]']
        }, {
            field : 'ncrReason',
            title : 'T_CORE_NCR.NCRREASON',
            type : 'textarea',
            height : 150,
            colspan : 3,
            validators : ['notEmpty', 'strLength[0,1333]']
        }, {
            field : 'rectifyPlan',
            title : 'T_CORE_NCR.RECTIFYPLAN',
            type : 'textarea',
            height : 150,
            colspan : 3,
            validators : ['notEmpty', 'strLength[0,1333]']
        }, {
            field : 'rectifyCarry',
            title : 'T_CORE_NCR.RECTIFYCARRY',
            type : 'textarea',
            height : 150,
            colspan : 3,
            validators : ['notEmpty', 'strLength[0,1333]']
        }, {
            field : 'verifyDesc',
            title : 'T_CORE_NCR.VERIFYDESC',
            validators : ['notEmpty', 'strLength[0,1333]']
        }, {
            field : 'verifyResult',
            title : 'T_CORE_NCR.VERIFYRESULT',
            validators : ['notEmpty', 'strLength[0,1333]']
        }, {
            field : 'nextVerifyResult',
            title : 'T_CORE_NCR.NEXTVERIFYRESULT',
            validators : ['notEmpty', 'strLength[0,1333]']
        }, {
            field : 'nextVerifyDeadline',
            type : 'date',
            title : 'T_CORE_NCR.NEXTVERIFYDEADLINE',
            validators : ['notEmpty']
        }, {
            field : 'verifyUserName',
            title : 'T_CORE_NCR.VERIFYUSERNAME',
            validators : ['notEmpty', 'strLength[0,20]']
        }, {
            field : 'verifyDate',
            type : 'date',
            title : 'T_CORE_NCR.VERIFYDATE',
            validators : ['notEmpty']
        }, {
            field : 'remark',
            title : 'T_CORE_NCR.REMARK',
            type : 'textarea',
            colspan : 3,
            height : 100,
            validators : ['strLength[0,200]']
        }];
    },

    getChooseListColumns : function(isSingle) {
        var columns = [{
            checkbox : true
        }, {
            field : 'id',
            title : 'T_CORE_NCR.ID'
        }, {
            field : 'ncrNo',
            title : 'T_CORE_NCR.NCRNO'
        }, {
            field : 'ncrContent',
            title : 'T_CORE_NCR.NCRCONTENT'
        }, {
            field : 'discoveryUserName',
            title : 'T_CORE_NCR.DISCOVERYUSERNAME'
        }, {
            field : 'discoveryDate',
            title : 'T_CORE_NCR.DISCOVERYDATE'
        }, {
            field : 'discoveryOrgName',
            title : 'T_CORE_NCR.DISCOVERYORGNAME'
        }, {
            field : 'remark',
            title : 'T_CORE_NCR.REMARK'
        }];
        if(isSingle == 1) {
            columns.splice(0, 1, {
                radio : true
            })
        };
        return columns;
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/qms/ncrs',

    workflow : {
        dbTable : 'T_CORE_NCR',
        columns : [{
            field : 'id',
            title : 'T_CORE_NCR.ID'
        }]
    }

});
