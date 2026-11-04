var coreExternalAudit = Gikam.getPageObject({

    getListColumns : function() {
        return [{
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreExternalAudit.workflow.dbTable), {
            field : 'auditNo',
            title : 'T_CORE_EXTERNAL_AUDIT.AUDITNO',
            type : 'link'
        }, {
            field : 'auditPurpose',
            title : 'T_CORE_EXTERNAL_AUDIT.AUDITPURPOSE'
        }, {
            field : 'auditDate',
            title : 'T_CORE_EXTERNAL_AUDIT.AUDITDATE'
        }, {
            field : 'auditAddress',
            title : 'T_CORE_EXTERNAL_AUDIT.AUDITADDRESS'
        }, {
            field : 'auditUserName',
            title : 'T_CORE_EXTERNAL_AUDIT.AUDITUSERNAME'
        }, {
            field : 'auditOrgName',
            title : 'T_CORE_EXTERNAL_AUDIT.AUDITORGNAME'
        }, {
            field : 'orgManager',
            title : 'T_CORE_EXTERNAL_AUDIT.ORGMANAGER'
        }, {
            field : 'orgAddress',
            title : 'T_CORE_EXTERNAL_AUDIT.ORGADDRESS'
        }, {
            field : 'orgPostcode',
            title : 'T_CORE_EXTERNAL_AUDIT.ORGPOSTCODE'
        }, {
            field : 'orgPhone',
            title : 'T_CORE_EXTERNAL_AUDIT.ORGPHONE'
        }, {
            field : 'orgFax',
            title : 'T_CORE_EXTERNAL_AUDIT.ORGFAX'
        }, {
            field : 'rectifyResultDesc',
            title : 'T_CORE_EXTERNAL_AUDIT.RECTIFYRESULTDESC'
        }, {
            field : 'remark',
            title : 'T_CORE_EXTERNAL_AUDIT.REMARK'
        }];
    },

    getChooseListColumns : function(isSingle) {
        var columns = [{
            checkbox : true
        }, {
            field : 'auditNo',
            title : 'T_CORE_EXTERNAL_AUDIT.AUDITNO',
            type : 'link'
        }, {
            field : 'auditUserName',
            title : 'T_CORE_EXTERNAL_AUDIT.AUDITUSERNAME'
        }, {
            field : 'auditOrgName',
            title : 'T_CORE_EXTERNAL_AUDIT.AUDITORGNAME'
        }, {
            field : 'auditDate',
            title : 'T_CORE_EXTERNAL_AUDIT.AUDITDATE'
        }, {
            field : 'remark',
            title : 'T_CORE_EXTERNAL_AUDIT.REMARK'
        }];
        if(isSingle == 1) {
            columns.splice(0, 1, {
                radio : true
            })
        }
        ;
        return columns;
    },

    getBaseInfoFormFields : function() {
        return [{
            field : 'auditNo',
            title : 'T_CORE_EXTERNAL_AUDIT.AUDITNO',
            readonly : true,
            validators : ['notEmpty']
        }, {
            field : 'auditAddress',
            title : 'T_CORE_EXTERNAL_AUDIT.AUDITADDRESS',
            validators : ['notEmpty']
        }, {
            field : 'auditDate',
            type : 'date',
            title : 'T_CORE_EXTERNAL_AUDIT.AUDITDATE',
            validators : ['notEmpty']
        }, {
            field : 'auditPurpose',
            title : 'T_CORE_EXTERNAL_AUDIT.AUDITPURPOSE',
            colspan : 3,
            validators : ['notEmpty']
        }, {
            field : 'auditOrgName',
            type : 'choose',
            single : true,
            category : 'org',
            title : 'T_CORE_EXTERNAL_AUDIT.AUDITORG',
            validators : ['notEmpty'],
            targetFields : [{
                auditOrgName : 'orgName'
            }, {
                auditOrgId : 'orderNo'
            }]

        }, {
            field : 'orgManager',
            title : 'T_CORE_EXTERNAL_AUDIT.ORGMANAGER',
            validators : ['notEmpty']
        }, {
            field : 'orgPhone',
            title : 'T_CORE_EXTERNAL_AUDIT.ORGPHONE',
            validators : ['notEmpty', 'phone']
        }, {
            field : 'orgAddress',
            title : 'T_CORE_EXTERNAL_AUDIT.ORGADDRESS',
            validators : ['notEmpty']
        }, {
            field : 'orgPostcode',
            title : 'T_CORE_EXTERNAL_AUDIT.ORGPOSTCODE',
            validators : ['notEmpty', 'number']
        }, {
            field : 'orgFax',
            title : 'T_CORE_EXTERNAL_AUDIT.ORGFAX',
            validators : ['notEmpty']
        }, {
            field : 'rectifyResultDesc',
            type : 'ckeditor',
            title : 'T_CORE_EXTERNAL_AUDIT.RECTIFYRESULTDESC',
            validators : ['notEmpty'],
            toolbarStartupExpanded : false,
            colspan : 3,
        }, {
            field : 'remark',
            type : 'textarea',
            title : 'T_CORE_EXTERNAL_AUDIT.REMARK',
            height : 100,
            colspan : 3,
        }];
    },

    getAuditGroupListColumns : function() {
        return [{
            checkbox : true
        }, {
            index : true
        }, {
            field : 'groupName',
            title : 'T_CORE_EXTERNAL_AUDIT_GROUP.GROUPNAME',
            editor : true,
            validators : ['notEmpty']
        }, {
            field : 'groupUserName',
            title : 'T_CORE_EXTERNAL_AUDIT_GROUP.GROUPUSERNAME',
            editor : true,
            validators : ['notEmpty']
        }, {
            field : 'groupUserOrgName',
            title : 'T_CORE_EXTERNAL_AUDIT_GROUP.GROUPUSERORGNAME',
            editor : true,
            validators : ['notEmpty']
        }, {
            field : 'groupUserPhone',
            title : 'T_CORE_EXTERNAL_AUDIT_GROUP.GROUPUSERPHONE',
            editor : true,
            validators : ['notEmpty', 'phone']
        }, {
            field : 'groupUserEmail',
            title : 'T_CORE_EXTERNAL_AUDIT_GROUP.GROUPUSEREMAIL',
            editor : true,
            validators : ['notEmpty', 'email']
        }, {
            field : 'remark',
            title : 'T_CORE_EXTERNAL_AUDIT_GROUP.REMARK',
            editor : true
        }];
    },

    getAuditGroupFormFields : function() {
        return [{
            field : 'groupName',
            title : 'T_CORE_EXTERNAL_AUDIT_GROUP.GROUPNAME',
            editor : true,
            preInsert : true,
            validators : ['notEmpty']
        }, {
            field : 'groupUserName',
            title : 'T_CORE_EXTERNAL_AUDIT_GROUP.GROUPUSERNAME',
            editor : true,
            preInsert : true,
            validators : ['notEmpty']
        }, {
            field : 'groupUserOrgName',
            title : 'T_CORE_EXTERNAL_AUDIT_GROUP.GROUPUSERORGNAME',
            editor : true,
            preInsert : true,
            validators : ['notEmpty']
        }, {
            field : 'groupUserPhone',
            title : 'T_CORE_EXTERNAL_AUDIT_GROUP.GROUPUSERPHONE',
            editor : true,
            preInsert : true,
            validators : ['notEmpty', 'phone']
        }, {
            field : 'groupUserEmail',
            title : 'T_CORE_EXTERNAL_AUDIT_GROUP.GROUPUSEREMAIL',
            editor : true,
            preInsert : true,
            validators : ['notEmpty', 'email']
        }]
    },

    getAuditNcrFormFields : function() {
        return [{
            field : 'ncrItemNos',
            type : 'choose',
            title : 'T_CORE_EXTERNAL_AUDIT_NCR.NCRITEMNOS',
            editor : true,
            preInsert : true,
            category : 'ncr-item',
            onAfterChoose : function(rows) {
                var ncrItemList = [];
                if(Gikam.isNotEmpty(rows)) {
                    Gikam.each(rows, function(index, row) {
                        ncrItemList[index] = row.ncrNo;
                    });
                    this.setData({ncrItemNos : ncrItemList.join('，')});
                }
            }
        }, {
            field : 'ncrItemNo',
            title : 'T_CORE_EXTERNAL_AUDIT_NCR.NCRITEMNO',
            editor : true,
            preInsert : true
        }, {
            field : 'ncrItemContent',
            type : 'textarea',
            height : 100,
            title : 'T_CORE_EXTERNAL_AUDIT_NCR.NCRITEMCONTENT',
            editor : true,
            preInsert : true
        }, {
            field : 'discoveryOrgName',
            title : 'T_CORE_EXTERNAL_AUDIT_NCR.DISCOVERYORGNAME',
            editor : true,
            preInsert : true
        }, {
            field : 'createdByName',
            title : 'T_CORE_EXTERNAL_AUDIT_NCR.CREATEDBYNAME',
            editor : true,
            preInsert : true
        }, {
            field : 'discoveryDate',
            type : 'date',
            title : 'T_CORE_EXTERNAL_AUDIT_NCR.DISCOVERYDATE',
            editor : true,
            preInsert : true
        }, {
            field : 'judgeResult',
            title : 'T_CORE_EXTERNAL_AUDIT_NCR.JUDGERESULT',
            editor : true,
            preInsert : true
        }, {
            field : 'beAuditOrgName',
            title : 'T_CORE_EXTERNAL_AUDIT_NCR.BEAUDITORGNAME',
            editor : true,
            preInsert : true,
            type : 'choose',
            category : 'org',
            onAfterChoose : function(rows) {
                var nameList = [];
                if(Gikam.isNotEmpty(rows)) {
                    Gikam.each(rows, function(index, row) {
                        nameList[index] = row.orgName;
                    });
                    this.setData({beAuditOrgName : nameList.join('，')});
                }
            }
        }, {
            field : 'rectifyDeadline',
            type : 'date',
            title : 'T_CORE_EXTERNAL_AUDIT_NCR.RECTIFYDEADLINE',
            editor : true,
            preInsert : true
        }, {
            field : 'auditResultDesc',
            type : 'textarea',
            height : 100,
            title : 'T_CORE_EXTERNAL_AUDIT_NCR.AUDITRESULTDESC',
            editor : true,
            preInsert : true
        }];
    },

    getAuditNcrListColumns : function() {
        return [{
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_CORE_EXTERNAL_AUDIT_NCR.ORDERNO',
            editor : true,
        }, {
            field : 'ncrItemNos',
            type : 'choose',
            title : 'T_CORE_EXTERNAL_AUDIT_NCR.NCRITEMNOS',
            editor : true,
            validators : ['notEmpty']
        }, {
            field : 'ncrItemNo',
            title : 'T_CORE_EXTERNAL_AUDIT_NCR.NCRITEMNO',
            editor : true,
            validators : ['notEmpty']
        }, {
            field : 'ncrItemContent',
            title : 'T_CORE_EXTERNAL_AUDIT_NCR.NCRITEMCONTENT',
            editor : true,
            validators : ['notEmpty']
        }, {
            field : 'discoveryOrgName',
            title : 'T_CORE_EXTERNAL_AUDIT_NCR.DISCOVERYORGNAME',
            editor : true,
            validators : ['notEmpty']
        }, {
            field : 'createdByName',
            title : 'T_CORE_EXTERNAL_AUDIT_NCR.CREATEDBYNAME',
            editor : true,
            validators : ['notEmpty']
        }, {
            field : 'discoveryDate',
            type : 'date',
            title : 'T_CORE_EXTERNAL_AUDIT_NCR.DISCOVERYDATE',
            editor : true,
            validators : ['notEmpty']
        }, {
            field : 'judgeResult',
            title : 'T_CORE_EXTERNAL_AUDIT_NCR.JUDGERESULT',
            editor : true,
            validators : ['notEmpty']
        }, {
            field : 'beAuditOrgName',
            title : 'T_CORE_EXTERNAL_AUDIT_NCR.BEAUDITORGNAME',
            editor : true,
            type : 'choose',
            category : 'org',
            validators : ['notEmpty'],
            onAfterChoose : function(rows) {
                var nameList = [];
                if(Gikam.isNotEmpty(rows)) {
                    Gikam.each(rows, function(index, row) {
                        nameList[index] = row.orgName;
                    });
                    this.setData({beAuditOrgName : nameList.join('，')});
                }
            }
        }, {
            field : 'rectifyDeadline',
            type : 'date',
            title : 'T_CORE_EXTERNAL_AUDIT_NCR.RECTIFYDEADLINE',
            editor : true,
            validators : ['notEmpty']
        }, {
            field : 'auditResultDesc',
            title : 'T_CORE_EXTERNAL_AUDIT_NCR.AUDITRESULTDESC',
            editor : true,
            validators : ['notEmpty']
        }, {
            field : 'remark',
            title : 'T_CORE_EXTERNAL_AUDIT_NCR.REMARK',
            editor : true
        }];
    },

    loadDetailPage : function(id, status) {
        workspace.window.load(this.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            status : status
        }));
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/qms/external-audits',

    workflow : {
        dbTable : 'T_CORE_EXTERNAL_AUDIT',
        columns : [{
            field : 'id',
            title : 'T_CORE_EXTERNAL_AUDIT.ID'
        }]
    }
});
