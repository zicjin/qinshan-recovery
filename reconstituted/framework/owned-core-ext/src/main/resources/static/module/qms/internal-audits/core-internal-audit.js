var coreInternalAudit = Gikam.getPageObject({

    getEditListColumns : function() {
        return [{
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreInternalAudit.workflow.dbTable), {
            field : 'annualPlanNo',
            title : 'T_CORE_INTERNAL_AUDIT.ANNUALPLANNO',
            type : 'select',
            category : 'annual-plan'
        }, {
            field : 'auditNo',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITNO',
            type : 'link'
        }, {
            field : 'auditPurpose',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITPURPOSE'
        }, {
            field : 'auditContent',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITCONTENT'
        }, {
            field : 'auditRule',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITRULE'
        }, {
            field : 'auditSchedule',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITSCHEDULE'
        }, {
            field : 'auditMethod',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITMETHOD'
        }, {
            field : 'auditDate',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITDATE'
        }, {
            field : 'auditResult',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITRESULT'
        }, {
            field : 'remark',
            title : 'T_CORE_INTERNAL_AUDIT.REMARK'
        }];
    },

    getChooseListColumns : function(isSingle) {
        var columns = [{
            checkbox : true
        }, {
            field : 'auditNo',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITNO',
            type : 'link'
        }, {
            field : 'auditContent',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITCONTENT'
        }, {
            field : 'auditDate',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITDATE'
        }, {
            field : 'remark',
            title : 'T_CORE_INTERNAL_AUDIT.REMARK'
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
            title : 'T_CORE_INTERNAL_AUDIT.AUDITNO',
            validators : ['notEmpty', 'strLength[0,15]'],
            readonly : true
        }, {
            field : 'annualPlanNo',
            title : 'T_CORE_INTERNAL_AUDIT.ANNUALPLANNO',
            validators : ['strLength[0,15]'],
            type : 'select',
            category : 'annual-plan',
            firstBlank : false,
            param : {
                planCategory : 'inAuditPlan'
            },
            onChange : function(field, value, rowIndex, oldValue, text) {
                var comp = Gikam.getComp('core-internal-audit-detail-base-info-form');
                workspace.window.showMask(true);
                Gikam.getText(Gikam.printf(IFM_CONTEXT + '/secure/core/module/qms/annual-plans/{id}/internal-audit-plan', {
                    id : value
                })).done(function(item) {
                    var data = Gikam.parseJson(item);
                    comp.setData({'index' : comp.getData().id, 'annualPlanId' : data.annualPlanId}, true);
                    comp.setData({'index' : comp.getData().id, 'auditPurpose' : data.auditPurpose}, true);
                    comp.setData({'index' : comp.getData().id, 'auditContent' : data.auditContent}, true);
                    comp.setData({'index' : comp.getData().id, 'auditRule' : data.auditRule}, true);
                    comp.setData({'index' : comp.getData().id, 'auditMethod' : data.auditMethod}, true);
                    comp.setData({'index' : comp.getData().id, 'auditDate' : data.auditDate}, true);
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        }, {
            field : 'auditDate',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITDATE',
            validators : ['notEmpty'],
            type : 'date'
        }, {
            field : 'auditPurpose',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITPURPOSE',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'auditContent',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITCONTENT',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'auditRule',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITRULE',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'auditSchedule',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITSCHEDULE',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'auditMethod',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITMETHOD',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'orgName',
            title : 'T_CORE_INTERNAL_AUDIT.ORGNAME',
            validators : ['notEmpty', 'strLength[0,85]'],
            colspan : 3,
            type : 'choose',
            category : 'org',
            single : true,
            targetFields : [{orgId : 'id'}, {orgName : 'orgName'}]
        }, {
            field : 'reportRange',
            title : 'T_CORE_INTERNAL_AUDIT.REPORTRANGE',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            toolbarStartupExpanded : false

        }, {
            field : 'remark',
            title : 'T_CORE_INTERNAL_AUDIT.REMARK',
            validators : ['strLength[0,170]'],
            colspan : 3,
            type : 'textarea'
        }];
    },

    getAuditInfoFormFields1 : function() {
        return [{
            field : 'auditNo',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITNO',
            validators : ['notEmpty', 'strLength[0,12]'],
            readonly : true
        }, {
            field : 'annualPlanNo',
            title : 'T_CORE_INTERNAL_AUDIT.ANNUALPLANNO',
            validators : ['strLength[0,12]'],
            type : 'select',
            category : 'annual-plan',
            readonly : true
        }, {
            field : 'auditDate',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITDATE',
            validators : ['notEmpty'],
            type : 'date',
            readonly : true
        }, {
            field : 'auditPurpose',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITPURPOSE',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            readonly : true
        }, {
            field : 'auditContent',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITCONTENT',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            readonly : true
        }, {
            field : 'auditRule',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITRULE',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            readonly : true
        }, {
            field : 'auditSchedule',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITSCHEDULE',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            readonly : true
        }, {
            field : 'auditMethod',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITMETHOD',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            readonly : true
        }, {
            field : 'orgName',
            title : 'T_CORE_INTERNAL_AUDIT.ORGNAME',
            validators : ['notEmpty', 'strLength[0,85]'],
            colspan : 3,
            type : 'choose',
            category : 'org',
            readonly : true
        }, {
            field : 'reportRange',
            title : 'T_CORE_INTERNAL_AUDIT.REPORTRANGE',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            readonly : true
        }, {
            field : 'remark',
            title : 'T_CORE_INTERNAL_AUDIT.REMARK',
            validators : ['strLength[0,170]'],
            colspan : 3,
            type : 'textarea',
            readonly : true
        }];
    },

    getAuditInfoFormFields2 : function() {
        return [{
            field : 'auditNo',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITNO',
            validators : ['notEmpty', 'strLength[0,12]'],
            readonly : true
        }, {
            field : 'annualPlanNo',
            title : 'T_CORE_INTERNAL_AUDIT.ANNUALPLANNO',
            validators : ['strLength[0,12]'],
            type : 'select',
            category : 'annual-plan',
            readonly : true
        }, {
            field : 'auditDate',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITDATE',
            validators : ['notEmpty'],
            type : 'date',
            readonly : true
        }, {
            field : 'auditPurpose',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITPURPOSE',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            readonly : true
        }, {
            field : 'auditContent',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITCONTENT',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            readonly : true
        }, {
            field : 'auditRule',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITRULE',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            readonly : true
        }, {
            field : 'auditSchedule',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITSCHEDULE',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            readonly : true
        }, {
            field : 'auditMethod',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITMETHOD',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            readonly : true
        }, {
            field : 'orgName',
            title : 'T_CORE_INTERNAL_AUDIT.ORGNAME',
            validators : ['notEmpty', 'strLength[0,85]'],
            colspan : 3,
            type : 'choose',
            category : 'org',
            readonly : true
        }, {
            field : 'reportRange',
            title : 'T_CORE_INTERNAL_AUDIT.REPORTRANGE',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            readonly : true
        }, {
            field : 'reportContent',
            title : 'T_CORE_INTERNAL_AUDIT.REPORTCONTENT',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'auditResult',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITRESULT',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'remark',
            title : 'T_CORE_INTERNAL_AUDIT.REMARK',
            validators : ['strLength[0,170]'],
            colspan : 3,
            type : 'textarea',
            readonly : true
        }];
    },

    getAuditInfoFormFields3 : function() {
        return [{
            field : 'auditNo',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITNO',
            validators : ['notEmpty', 'strLength[0,12]'],
            readonly : true
        }, {
            field : 'annualPlanNo',
            title : 'T_CORE_INTERNAL_AUDIT.ANNUALPLANNO',
            validators : ['strLength[0,12]'],
            type : 'select',
            category : 'annual-plan',
            readonly : true
        }, {
            field : 'auditDate',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITDATE',
            validators : ['notEmpty'],
            type : 'date',
            readonly : true
        }, {
            field : 'auditPurpose',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITPURPOSE',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            readonly : true
        }, {
            field : 'auditContent',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITCONTENT',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            readonly : true
        }, {
            field : 'auditRule',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITRULE',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            readonly : true
        }, {
            field : 'auditSchedule',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITSCHEDULE',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            readonly : true
        }, {
            field : 'auditMethod',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITMETHOD',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            readonly : true
        }, {
            field : 'orgName',
            title : 'T_CORE_INTERNAL_AUDIT.ORGNAME',
            validators : ['notEmpty', 'strLength[0,85]'],
            colspan : 3,
            type : 'choose',
            category : 'org',
            readonly : true
        }, {
            field : 'reportRange',
            title : 'T_CORE_INTERNAL_AUDIT.REPORTRANGE',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            readonly : true
        }, {
            field : 'reportContent',
            title : 'T_CORE_INTERNAL_AUDIT.REPORTCONTENT',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            readonly : true
        }, {
            field : 'auditResult',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITRESULT',
            validators : ['notEmpty', 'strLength[0,1333]'],
            colspan : 3,
            type : 'ckeditor',
            readonly : true
        }, {
            field : 'remark',
            title : 'T_CORE_INTERNAL_AUDIT.REMARK',
            validators : ['strLength[0,170]'],
            colspan : 3,
            type : 'textarea',
            readonly : true
        }];
    },

    getAuditGroupListColumns : function() {
        return [{
            checkbox : true
        }, {
            field : 'groupName',
            title : 'T_CORE_INTERNAL_AUDIT_GROUP.GROUPNAME',
            validators : ['notEmpty', 'strLength[0,11]'],
            editor : true
        }, {
            field : 'groupLeaderName',
            title : 'T_CORE_INTERNAL_AUDIT_GROUP.GROUPLEADERNAME',
            editor : true,
            validators : ['notEmpty', 'strLength[0,21]'],
            type : 'choose',
            category : 'user',
            single : true,
            targetFields : [{groupLeaderId : 'id'}, {groupLeaderName : 'userName'}]
        }, {
            field : 'groupUserName',
            title : 'T_CORE_INTERNAL_AUDIT_GROUP.GROUPUSERNAME',
            validators : ['notEmpty', 'strLength[0,682]'],
            editor : true,
            type : 'choose',
            category : 'mult-user',
            single : false,
            onBeforeChoose : function(index) {
                var comp = Gikam.getComp('core-internal-audit-edit-list-audit-group-grid');
                var ids = comp.getData()[index].groupUserId;
                if(Gikam.isNotEmpty(ids)) {
                    return {
                        id_DIFFER : ids.split(','),
                    };
                }
            },
            onClean : function(field, value, rowIndex) {
                var comp = Gikam.getComp('core-internal-audit-edit-list-audit-group-grid');
                comp.setData({'index' : rowIndex, 'groupUserId' : ''}, true);
                comp.setData({'index' : rowIndex, 'groupUserName' : ''}, true);
            },
            onAfterChoose : function(rows, index) {
                var comp = Gikam.getComp('core-internal-audit-edit-list-audit-group-grid');
                var ids = comp.getData()[index].groupUserId;
                var names = comp.getData()[index].groupUserName;
                var idList = new Array();
                var nameList = new Array();
                if(Gikam.isNotEmpty(rows)) {
                    Gikam.each(rows, function(index, row) {
                        idList[index] = row.id;
                        nameList[index] = row.userName;
                    });

                    comp.setData({
                        'index' : index,
                        'groupUserId' : (Gikam.isEmpty(ids) ? '' : ids + ',') + idList.join(',')
                    }, true);
                    comp.setData({
                        'index' : index,
                        'groupUserName' : (Gikam.isEmpty(names) ? '' : names + ',') + nameList.join(',')
                    }, true);
                }
            }
        }, {
            field : 'beAuditOrgName',
            title : 'T_CORE_INTERNAL_AUDIT_GROUP.BEAUDITORGNAME',
            validators : ['notEmpty', 'strLength[0,85]'],
            editor : true,
            type : 'choose',
            category : 'org',
            single : false,
            onBeforeChoose : function(index) {
                var grid = Gikam.getComp('core-internal-audit-edit-list-audit-group-grid');
                var ids = grid.getData()[index].beAuditOrgId;
                if(Gikam.isNotEmpty(ids)) {
                    return {
                        id_DIFFER : ids.split(','),
                    };
                }
            },
            onClean : function(field, value, rowIndex) {
                var grid = Gikam.getComp('core-internal-audit-edit-list-audit-group-grid');
                grid.setData({'index' : rowIndex, 'beAuditOrgId' : ''}, true);
                grid.setData({'index' : rowIndex, 'beAuditOrgName' : ''}, true);
            },
            onAfterChoose : function(rows, index) {
                var grid = Gikam.getComp('core-internal-audit-edit-list-audit-group-grid');
                var ids = grid.getData().beAuditOrgId;
                var names = grid.getData().beAuditOrgName;
                var idList = new Array();
                var nameList = new Array();
                if(Gikam.isNotEmpty(rows)) {
                    Gikam.each(rows, function(index, row) {
                        idList[index] = row.id;
                        nameList[index] = row.orgName;
                    });
                    grid.setData({
                        'index' : index,
                        'beAuditOrgId' : (Gikam.isEmpty(ids) ? '' : ids + ',') + idList.join(',')
                    }, true);
                    grid.setData({
                        'index' : index,
                        'beAuditOrgName' : (Gikam.isEmpty(names) ? '' : names + ',') + nameList.join(',')
                    }, true);
                }
            }
        }, {
            field : 'beAuditUserName',
            title : 'T_CORE_INTERNAL_AUDIT_GROUP.BEAUDITUSERNAME',
            validators : ['notEmpty', 'strLength[0,21]'],
            editor : true,
            type : 'choose',
            category : 'mult-user',
            single : false,
            onBeforeChoose : function(index) {
                var grid = Gikam.getComp('core-internal-audit-edit-list-audit-group-grid');
                var ids = grid.getData()[index].beAuditUserId;
                if(Gikam.isNotEmpty(ids)) {
                    return {
                        id_DIFFER : ids.split(','),
                    };
                }
            },
            onClean : function(field, value, rowIndex) {
                var grid = Gikam.getComp('core-internal-audit-edit-list-audit-group-grid');
                grid.setData({'index' : rowIndex, 'beAuditUserId' : ''}, true);
                grid.setData({'index' : rowIndex, 'beAuditUserName' : ''}, true);
            },
            onAfterChoose : function(rows, index) {
                var grid = Gikam.getComp('core-internal-audit-edit-list-audit-group-grid');
                var ids = grid.getData().beAuditUserId;
                var names = grid.getData().beAuditUserName;
                var idList = new Array();
                var nameList = new Array();
                if(Gikam.isNotEmpty(rows)) {
                    Gikam.each(rows, function(index, row) {
                        idList[index] = row.id;
                        nameList[index] = row.userName;
                    });
                    grid.setData({
                        'index' : index,
                        'beAuditUserId' : (Gikam.isEmpty(ids) ? '' : ids + ',') + idList.join(',')
                    }, true);
                    grid.setData({
                        'index' : index,
                        'beAuditUserName' : (Gikam.isEmpty(names) ? '' : names + ',') + nameList.join(',')
                    }, true);
                }
            }
        }];
    },

    getAuditNcrListColumns : function() {
        return [{
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_CORE_INTERNAL_AUDIT_NCR.ORDERNO',
            editor : true,
            validators : ['notEmpty', 'number'],
        }, {
            field : 'groupLeaderId',
            title : 'T_CORE_INTERNAL_AUDIT_NCR.GROUPLEADERID',
            editor : true,
            validators : ['notEmpty', 'strLength[0,1333]']
        }, {
            field : 'groupLeaderName',
            title : 'T_CORE_INTERNAL_AUDIT_NCR.GROUPLEADERNAME',
            width : 180,
            editor : true,
            validators : ['notEmpty', 'strLength[0,1333]']
        }, {
            field : 'discoveryOrgName',
            title : 'T_CORE_INTERNAL_AUDIT_NCR.DISCOVERYORGNAME',
            validators : ['notEmpty', 'strLength[0,85]'],
            width : 180,
            editor : true
        }, {
            field : 'beAuditOrgName',
            title : 'T_CORE_INTERNAL_AUDIT_NCR.BEAUDITORGNAME',
            validators : ['notEmpty', 'strLength[0,85]'],
            width : 180,
            editor : true
        }, {
            field : 'rectifyTime',
            title : 'T_CORE_INTERNAL_AUDIT_NCR.RECTIFYTIME',
            validators : ['notEmpty'],
            editor : true,
            type : 'date'
        }, {
            field : 'auditUserName',
            title : 'T_CORE_INTERNAL_AUDIT_NCR.AUDITUSERNAME',
            validators : ['notEmpty', 'strLength[0,682]'],
            editor : true
        }];
    },

    getAuditListColumns : function() {
        return [{
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreInternalAudit.workflow.dbTable), {
            field : 'annualPlanNo',
            title : 'T_CORE_INTERNAL_AUDIT.ANNUALPLANNO'
        }, {
            field : 'auditNo',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITNO',
            type : 'link'
        }, {
            field : 'auditPurpose',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITPURPOSE'
        }, {
            field : 'auditContent',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITCONTENT'
        }, {
            field : 'auditRule',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITRULE'
        }, {
            field : 'auditSchedule',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITSCHEDULE'
        }, {
            field : 'auditMethod',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITMETHOD'
        }, {
            field : 'auditDate',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITDATE'
        }, {
            field : 'auditResult',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITRESULT'
        }, {
            field : 'remark',
            title : 'T_CORE_INTERNAL_AUDIT.REMARK'
        }];
    },

    getSearchListColumns : function() {
        return [{
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreInternalAudit.workflow.dbTable), {
            field : 'annualPlanNo',
            title : 'T_CORE_INTERNAL_AUDIT.ANNUALPLANNO'
        }, {
            field : 'auditNo',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITNO',
            type : 'link'
        }, {
            field : 'auditPurpose',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITPURPOSE'
        }, {
            field : 'auditContent',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITCONTENT'
        }, {
            field : 'auditRule',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITRULE'
        }, {
            field : 'auditSchedule',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITSCHEDULE'
        }, {
            field : 'auditMethod',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITMETHOD'
        }, {
            field : 'auditDate',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITDATE'
        }, {
            field : 'auditResult',
            title : 'T_CORE_INTERNAL_AUDIT.AUDITRESULT'
        }, {
            field : 'remark',
            title : 'T_CORE_INTERNAL_AUDIT.REMARK'
        }];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/qms/internal-audits',

    workflow : {
        dbTable : 'T_CORE_INTERNAL_AUDIT',
        columns : [{
            field : 'id',
            title : 'T_CORE_INTERNAL_AUDIT.ID'
        }]
    }

});
