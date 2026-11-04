var coreAnnualPlan = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, Gikam.status.getBpmnColumn(coreAnnualPlan.workflow.dbTable), {
            field : 'annualPlanNo',
            title : 'T_CORE_ANNUAL_PLAN.ANNUALPLANNO',
            type : 'link'
        }, {
            field : 'planYear',
            title : 'T_CORE_ANNUAL_PLAN.PLANYEAR',
            width : 85
        }, {
            field : 'planCategory',
            title : 'T_CORE_ANNUAL_PLAN.PLANCATEGORY',
            type : 'select',
            category : 'annualPlanCategory',
            width : 150
        }, {
            field : 'estimatedDate',
            title : 'T_CORE_ANNUAL_PLAN.ESTIMATEDDATE'
        }, {
            field : 'createdByName',
            title : 'T_CORE_ANNUAL_PLAN.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_CORE_ANNUAL_PLAN.CREATEDTIME'
        }, {
            field : 'createdByOrgName',
            title : 'T_CORE_ANNUAL_PLAN.CREATEDBYORGNAME'
        }, {
            field : 'remark',
            title : 'T_CORE_ANNUAL_PLAN.REMARK'
        } ];
    },
    
    getAnnualPlanBaseInfoFormFields : function() {
        var nowDate = new Date();
        var year = nowDate.getFullYear();
        return [ {
            field : 'annualPlanNo',
            title : 'T_CORE_ANNUAL_PLAN.ANNUALPLANNO',
            validators : [ 'strLength[0,15]' ],
            readonly : true
        }, {
            field : 'planYear',
            title : 'T_CORE_ANNUAL_PLAN.PLANYEAR',
            type : 'year',
            preInsert : true,
            value : year,
            validators : [ 'notEmpty', 'strLength[0,4]' ],
            onChange : function(field, value, rowIndex, oldValue, text) {
                var form = Gikam.getComp('core-annual-plan-detail-info');
                if (!Gikam.isEmpty(form)) {
                    var estimatedDate = form.getData().estimatedDate;
                    if (!Gikam.isEmpty(estimatedDate)) {
                        var estimatedYear = estimatedDate.substr(0,4);
                        if (estimatedYear < value) {
                            form.setData({
                                index : 0,
                                estimatedDate : ''
                            });
                        }
                    }
                }
            }
        }, {
            field : 'planCategory',
            title : 'T_CORE_ANNUAL_PLAN.PLANCATEGORY',
            validators : [ 'strLength[0,15]' ],
            preInsert : true,
            type : 'select',
            category : 'annualPlanCategory',
            readonly : true
        }, {
            field : 'estimatedDate',
            title : 'T_CORE_ANNUAL_PLAN.ESTIMATEDDATE',
            type : 'date',
            validators : [ 'notEmpty' ],
            onChange : function(field, value, rowIndex, oldValue, text) {
                var form = Gikam.getComp('core-annual-plan-detail-info');
                var planYear = form.getData().planYear;
                if (!Gikam.isEmpty(planYear)) {
                    var estimatedYear = value.substr(0,4);
                    if (estimatedYear < planYear) {
                        Gikam.alert('CORE.MODULE.QMS.ANNUALPLAN.TIP.ESTIMATEDDATE_CANNOT_BEFROE_PLANYEAR');
                        form.setData({
                            index : 0,
                            estimatedDate : ''
                        });
                    }
                }
            }
        }, {
            field : 'createdByName',
            title : 'T_CORE_ANNUAL_PLAN.CREATEDBYNAME',
            validators : [ 'strLength[0,21]' ],
            type : 'choose',
            category : 'user',
            single : true,
            targetFields : [{ createdById : 'id' }, { createdByName : 'userName' }]
        }, {
            field : 'createdByOrgName',
            title : 'T_CORE_ANNUAL_PLAN.CREATEDBYORGNAME',
            validators : [ 'strLength[0,85]' ],
            readonly : true
        }, {
            field : 'createdTime',
            title : 'T_CORE_ANNUAL_PLAN.CREATEDTIME',
            type : 'date'
        }, {
            field : 'remark',
            title : 'T_CORE_ANNUAL_PLAN.REMARK',
            validators : [ 'strLength[0,170]' ]
        }];
    },
    
    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, Gikam.status.getBpmnColumn(coreAnnualPlan.workflow.dbTable), {
            field : 'annualPlanNo',
            title : 'T_CORE_ANNUAL_PLAN.ANNUALPLANNO',
            type : 'link'
        }, {
            field : 'planYear',
            title : 'T_CORE_ANNUAL_PLAN.PLANYEAR'
        }, {
            field : 'planCategory',
            title : 'T_CORE_ANNUAL_PLAN.PLANCATEGORY',
            type : 'select',
            category : 'annualPlanCategory'
        }, {
            field : 'estimatedDate',
            title : 'T_CORE_ANNUAL_PLAN.ESTIMATEDDATE'
        }, {
            field : 'createdByName',
            title : 'T_CORE_ANNUAL_PLAN.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_CORE_ANNUAL_PLAN.CREATEDTIME'
        }, {
            field : 'createdByOrgName',
            title : 'T_CORE_ANNUAL_PLAN.CREATEDBYORGNAME'
        }, {
            field : 'remark',
            title : 'T_CORE_ANNUAL_PLAN.REMARK'
        } ];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, Gikam.status.getBpmnColumn(coreAnnualPlan.workflow.dbTable), {
            field : 'annualPlanNo',
            title : 'T_CORE_ANNUAL_PLAN.ANNUALPLANNO',
            type : 'link'
        }, {
            field : 'planYear',
            title : 'T_CORE_ANNUAL_PLAN.PLANYEAR'
        }, {
            field : 'planCategory',
            title : 'T_CORE_ANNUAL_PLAN.PLANCATEGORY',
            type : 'select',
            category : 'annualPlanCategory'
        }, {
            field : 'estimatedDate',
            title : 'T_CORE_ANNUAL_PLAN.ESTIMATEDDATE'
        }, {
            field : 'createdByName',
            title : 'T_CORE_ANNUAL_PLAN.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_CORE_ANNUAL_PLAN.CREATEDTIME'
        }, {
            field : 'createdByOrgName',
            title : 'T_CORE_ANNUAL_PLAN.CREATEDBYORGNAME'
        }, {
            field : 'remark',
            title : 'T_CORE_ANNUAL_PLAN.REMARK'
        } ];
    },

    getChooseListColumns : function(isSingle) {
        var columns = [  {
            checkbox : true
        }, {
            field : 'annualPlanNo',
            title : 'T_CORE_ANNUAL_PLAN.ANNUALPLANNO'
        }, {
            field : 'planYear',
            title : 'T_CORE_ANNUAL_PLAN.PLANYEAR'
        }, {
            field : 'planCategory',
            title : 'T_CORE_ANNUAL_PLAN.PLANCATEGORY',
            type : 'select',
            category : 'annualPlanCategory'
        }, {
            field : 'estimatedDate',
            title : 'T_CORE_ANNUAL_PLAN.ESTIMATEDDATE'
        }, {
            field : 'createdByName',
            title : 'T_CORE_ANNUAL_PLAN.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_CORE_ANNUAL_PLAN.CREATEDTIME'
        }, {
            field : 'createdByOrgName',
            title : 'T_CORE_ANNUAL_PLAN.CREATEDBYORGNAME'
        }, {
            field : 'remark',
            title : 'T_CORE_ANNUAL_PLAN.REMARK'
        }];
        if (isSingle == 1) {
            columns.splice(0, 1, {
                radio : true
            })
        }
        return columns;
    },
    
    getTrainPlanListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'userName',
            title : 'T_CORE_EMP_TRAIN_PLAN.USERNAME',
            validators : [ 'strLength[0,682]' ],
            editor : true,
            cleanable : false,
            type : 'choose',
            category : 'user',
            single : true,
            targetFields : [{ userId : 'id' }, { userName : 'userName' }, { orgName : 'orgName' }, { orgId : 'orgId' }]
            
        }, {
            field : 'orgName',
            title : 'T_CORE_EMP_TRAIN_PLAN.ORGNAME',
            validators : [ 'strLength[0,682]' ]
        }, {
            field : 'trainPlace',
            title : 'T_CORE_EMP_TRAIN_PLAN.TRAINPLACE',
            width : 200,
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,42]' ]
        }, {
            field : 'trainContent',
            title : 'T_CORE_EMP_TRAIN_PLAN.TRAINCONTENT',
            width : 240,
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,682]' ]
        }, {
            field : 'organizer',
            title : 'T_CORE_EMP_TRAIN_PLAN.ORGANIZER',
            width : 190,
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,42]' ]
        }, {
            field : 'startAndEndDate',
            title : 'T_CORE_EMP_TRAIN_PLAN.STARTANDENDDATE',
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,21]' ]
        }, {
            field : 'period',
            title : 'T_CORE_EMP_TRAIN_PLAN.PERIOD',
            width : 80,
            editor : true,
            validators : [ 'notEmpty', 'number' ]
        }, {
            field : 'remark',
            title : 'T_CORE_EMP_TRAIN_PLAN.REMARK',
            editor : true,
            validators : [ 'strLength[0,170]' ]
        } ];
    },
    
    getManageReviewFormFields : function() {
        return [{
            field : 'reviewPurpose',
            title : 'T_CORE_MANAGE_REVIEW_PLAN.REVIEWPURPOSE',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'reviewContent',
            title : 'T_CORE_MANAGE_REVIEW_PLAN.REVIEWCONTENT',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'reviewReq',
            title : 'T_CORE_MANAGE_REVIEW_PLAN.REVIEWREQ',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'reviewer',
            title : 'T_CORE_MANAGE_REVIEW_PLAN.REVIEWER',
            validators : [ 'notEmpty', 'strLength[0,682]' ],
            type : 'choose',
            category : 'mult-user',
            single : false,
            onBeforeChoose : function(index) {
                var comp = Gikam.getComp('core-annual-plan-detail-child-comp');
                var userIds = comp.getData().reviewerId;
                if (Gikam.isNotEmpty(userIds)) {
                    return {
                        id_DIFFER : userIds.split(','),
                    };
                }
            },
            onAfterChoose : function(rows, index) {
                var comp = Gikam.getComp('core-annual-plan-detail-child-comp');
                var ids = comp.getData().reviewerId;
                var names = comp.getData().reviewer;
                var idList = new Array();
                var nameList = new Array();
                if (Gikam.isNotEmpty(rows)) {
                    Gikam.each(rows, function(index, row){
                        nameList[index] = row.userName;
                        idList[index] = row.id;
                    });
                    comp.setData({'index' : index, 'reviewerId': (Gikam.isEmpty(ids)?'':ids + ',') + idList.join(',')}, true);
                    comp.setData({'index' : index, 'reviewer': (Gikam.isEmpty(names)?'':names + ',') + nameList.join(',')}, true);
                }
            }
        }, {
            field : 'reviewSchedule',
            title : 'T_CORE_MANAGE_REVIEW_PLAN.REVIEWSCHEDULE',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'reviewReport',
            title : 'T_CORE_MANAGE_REVIEW_PLAN.REVIEWREPORT',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'reviewFrequency',
            title : 'T_CORE_MANAGE_REVIEW_PLAN.REVIEWFREQUENCY',
            validators : [ 'notEmpty', 'strLength[0,200]' ],
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'remark',
            title : 'T_CORE_MANAGE_REVIEW_PLAN.REMARK',
            validators : [ 'notEmpty', 'strLength[0,170]' ],
            type : 'textarea'
        }];
    },
    
    getInternalAuditFormFields : function() {
        return [ {
            field : 'auditPurpose',
            title : 'T_CORE_INTERNAL_AUDIT_PLAN.AUDITPURPOSE',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'auditContent',
            title : 'T_CORE_INTERNAL_AUDIT_PLAN.AUDITCONTENT',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'auditRule',
            title : 'T_CORE_INTERNAL_AUDIT_PLAN.AUDITRULE',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'auditMethod',
            title : 'T_CORE_INTERNAL_AUDIT_PLAN.AUDITMETHOD',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'auditDate',
            title : 'T_CORE_INTERNAL_AUDIT_PLAN.AUDITDATE',
            type : 'date',
            validators : [ 'notEmpty' ],
        }, {
            field : 'auditUserAndWork',
            title : 'T_CORE_INTERNAL_AUDIT_PLAN.AUDITUSERANDWORK',
            validators : [ 'strLength[0,1333]' ],
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'remark',
            title : 'T_CORE_INTERNAL_AUDIT_PLAN.REMARK',
            validators : [ 'notEmpty', 'strLength[0,170]' ],
            type : 'textarea'
        }];
    },
    
    getDailySupervisionFormFields : function() {
        return [ {
            field : 'supervisePurpose',
            title : 'T_CORE_DAILY_SUPERVISION_PLAN.SUPERVISEPURPOSE',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'superviseContent',
            title : 'T_CORE_DAILY_SUPERVISION_PLAN.SUPERVISECONTENT',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'superviseReq',
            title : 'T_CORE_DAILY_SUPERVISION_PLAN.SUPERVISEREQ',
            validators : [ 'notEmpty', 'strLength[0,1333]' ]
        }, {
            field : 'superviseType',
            title : 'T_CORE_DAILY_SUPERVISION_PLAN.SUPERVISETYPE',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'supervisorName',
            title : 'T_CORE_DAILY_SUPERVISION_PLAN.SUPERVISORNAME',
            validators : [ 'notEmpty', 'strLength[0,682]' ],
            type : 'choose',
            category : 'mult-user',
            single : false,
            onBeforeChoose : function(index) {
                var form = Gikam.getComp('core-annual-plan-detail-child-comp');
                var ids = form.getData().supervisorId;
                if (Gikam.isNotEmpty(ids)) {
                    return {
                        id_DIFFER : ids.split(','),
                    };
                }
            },
            onClean : function(index) {
                var form = Gikam.getComp('core-annual-plan-detail-child-comp');
                form.setData({'index' : index, 'supervisorId': ''}, true);
                form.setData({'index' : index, 'supervisorName': ''}, true);
            },
            onAfterChoose : function(rows, index) {
                var form = Gikam.getComp('core-annual-plan-detail-child-comp');
                var ids = form.getData().supervisorId;
                var names = form.getData().supervisorName;
                var idList = new Array();
                var nameList = new Array();
                if (Gikam.isNotEmpty(rows)) {
                    Gikam.each(rows, function(index, row){
                        idList[index] = row.id;
                        nameList[index] = row.userName;
                    });
                    
                    form.setData({'index' : index, 'supervisorId': (Gikam.isEmpty(ids)?'':ids + ',') + idList.join(',')}, true);
                    form.setData({'index' : index, 'supervisorName': (Gikam.isEmpty(names)?'':names + ',') + nameList.join(',')}, true);
                }
            }
        }, {
            field : 'remark',
            title : 'T_CORE_DAILY_SUPERVISION_PLAN.REMARK',
            validators : [ 'notEmpty', 'strLength[0,170]' ],
            type : 'textarea'
        } ];
    },
    
    getQualityControlFormFields : function() {
        return [ {
            field : 'itemName',
            title : 'T_CORE_QUALITY_CONTROL_PLAN.ITEMNAME',
            type : 'ckeditor',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            toolbarStartupExpanded : false
        }, {
            field : 'qcType',
            title : 'T_CORE_QUALITY_CONTROL_PLAN.QCTYPE',
            type : 'ckeditor',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            toolbarStartupExpanded : false
        }, {
            field : 'standard',
            title : 'T_CORE_QUALITY_CONTROL_PLAN.STANDARD',
            type : 'ckeditor',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            toolbarStartupExpanded : false
        }, {
            field : 'acceptCriteria',
            title : 'T_CORE_QUALITY_CONTROL_PLAN.ACCEPTCRITERIA',
            type : 'ckeditor',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            toolbarStartupExpanded : false
        }, {
            field : 'carryDate',
            title : 'T_CORE_QUALITY_CONTROL_PLAN.CARRYDATE',
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'sponsorOrgName',
            title : 'T_CORE_QUALITY_CONTROL_PLAN.SPONSORORGNAME',
            validators : [ 'strLength[0,682]' ],
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'carryOrgName',
            title : 'T_CORE_QUALITY_CONTROL_PLAN.CARRYORGNAME',
            validators : [ 'strLength[0,682]' ],
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'remark',
            title : 'T_CORE_QUALITY_CONTROL_PLAN.REMARK',
            validators : [ 'notEmpty', 'strLength[0,170]' ],
            type : 'textarea'
        } ];
    },
    
    getCalibratePlanListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'equCode',
            title : 'T_CORE_VERIFY_CALIBRATE_PLAN.EQUCODE',
            editor : true,
            validators : [ 'strLength[0,11]' ],
            editable : true
        }, {
            field : 'modelNo',
            title : 'T_CORE_VERIFY_CALIBRATE_PLAN.MODELNO',
            validators : [ 'strLength[0,21]' ],
            editor : true
        }, {
            field : 'sn',
            title : 'T_CORE_VERIFY_CALIBRATE_PLAN.SN',
            validators : [ 'strLength[0,21]' ],
            editor : true
        }, {
            field : 'mfg',
            title : 'T_CORE_VERIFY_CALIBRATE_PLAN.MFG',
            validators : [ 'strLength[0,42]' ],
            editor : true
        }, {
            field : 'verifyPeriod',
            title : 'T_CORE_VERIFY_CALIBRATE_PLAN.VERIFYPERIOD',
            validators : [ 'strLength[0,11]' ],
            editor : true
        }, {
            field : 'verifyCorp',
            title : 'T_CORE_VERIFY_CALIBRATE_PLAN.VERIFYCORP',
            validators : [ 'notEmpty', 'strLength[0,42]' ],
            editor : true
        }, {
            field : 'lastVerifyDate',
            title : 'T_CORE_VERIFY_CALIBRATE_PLAN.LASTVERIFYDATE',
            editor : true,
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'nextVerifyDate',
            title : 'T_CORE_VERIFY_CALIBRATE_PLAN.NEXTVERIFYDATE',
            editor : true,
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'equPlace',
            title : 'T_CORE_VERIFY_CALIBRATE_PLAN.EQUPLACE',
            validators : [ 'strLength[0,42]' ],
            editor : true
        }, {
            field : 'manageOrgName',
            title : 'T_CORE_VERIFY_CALIBRATE_PLAN.MANAGEORGNAME',
            validators : [ 'strLength[0,42]' ],
            editor : true,
            type : 'choose',
            category : 'org',
            single : true,
            targetFields : [{ manageOrgId : 'id' }, { manageOrgName : 'orgName' }]
        }, {
            field : 'manageUserName',
            title : 'T_CORE_VERIFY_CALIBRATE_PLAN.MANAGEUSERNAME',
            validators : [ 'strLength[0,21]' ],
            editor : true,
            type : 'choose',
            category : 'user',
            single : true,
            targetFields : [{ manageUserId : 'id' }, { manageUserName : 'userName' }]
        }, {
            field : 'inspectUserName',
            title : 'T_CORE_VERIFY_CALIBRATE_PLAN.INSPECTUSERNAME',
            validators : [ 'notEmpty', 'strLength[0,21]' ],
            editor : true,
            type : 'choose',
            category : 'user',
            single : true,
            targetFields : [{ inspectUserId : 'id' }, { inspectUserName : 'userName' }]
        }, {
            field : 'remark',
            title : 'T_CORE_VERIFY_CALIBRATE_PLAN.REMARK',
            validators : [ 'strLength[0,170]' ],
            editor : true
        } ];
    },
    
    getPeriodVerifyPlanListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'equCode',
            title : 'T_CORE_PERIOD_VERIFY_PLAN.EQUCODE',
            editor : true,
            validators : [ 'strLength[0,11]' ],
            editable : true
        }, {
            field : 'equName',
            title : 'T_CORE_PERIOD_VERIFY_PLAN.EQUNAME',
            validators : [ 'strLength[0,42]' ],
            editor : true
        }, {
            field : 'modelNo',
            title : 'T_CORE_PERIOD_VERIFY_PLAN.MODELNO',
            validators : [ 'strLength[0,21]' ],
            editor : true
        }, {
            field : 'sn',
            title : 'T_CORE_PERIOD_VERIFY_PLAN.SN',
            validators : [ 'strLength[0,21]' ],
            editor : true
        }, {
            field : 'mfg',
            title : 'T_CORE_PERIOD_VERIFY_PLAN.MFG',
            validators : [ 'strLength[0,42]' ],
            editor : true
        }, {
            field : 'verifyPeriod',
            title : 'T_CORE_PERIOD_VERIFY_PLAN.VERIFYPERIOD',
            validators : [ 'strLength[0,11]' ],
            editor : true
        }, {
            field : 'verifyCorp',
            title : 'T_CORE_PERIOD_VERIFY_PLAN.VERIFYCORP',
            validators : [ 'notEmpty', 'strLength[0,42]' ],
            editor : true
        }, {
            field : 'carryDate',
            title : 'T_CORE_PERIOD_VERIFY_PLAN.CARRYDATE',
            editor : true,
            type : 'date'
        }, {
            field : 'verifyCriteria',
            title : 'T_CORE_PERIOD_VERIFY_PLAN.VERIFYCRITERIA',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            editor : true
        }, {
            field : 'carryUserName',
            title : 'T_CORE_PERIOD_VERIFY_PLAN.CARRYUSERNAME',
            validators : [ 'notEmpty', 'strLength[0,21]' ],
            editor : true,
            type : 'choose',
            category : 'user',
            single : true,
            targetFields : [{ carryUserId : 'id' }, { carryUserName : 'userName' }]
        }, {
            field : 'manageOrgName',
            title : 'T_CORE_PERIOD_VERIFY_PLAN.MANAGEORGNAME',
            validators : [ 'strLength[0,42]' ],
            editor : true,
            type : 'choose',
            category : 'org',
            single : true,
            targetFields : [{ manageOrgId : 'id' }, { manageOrgName : 'orgName' }]
        }, {
            field : 'manageUserName',
            title : 'T_CORE_PERIOD_VERIFY_PLAN.MANAGEUSERNAME',
            validators : [ 'strLength[0,21]' ],
            editor : true,
            type : 'choose',
            category : 'user',
            single : true,
            targetFields : [{ manageUserId : 'id' }, { manageUserName : 'userName' }]
        }, {
            field : 'inspectUserName',
            title : 'T_CORE_PERIOD_VERIFY_PLAN.INSPECTUSERNAME',
            validators : [ 'notEmpty', 'strLength[0,21]' ],
            editor : true,
            type : 'choose',
            category : 'user',
            single : true,
            targetFields : [{ inspectUserId : 'id' }, { inspectUserName : 'userName' }]
        }, {
            field : 'remark',
            title : 'T_CORE_PERIOD_VERIFY_PLAN.REMARK',
            editor : true
        } ];
    },
    
    getFuncCheckPlanListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'equCode',
            title : 'T_CORE_EQU_FUNC_CHECK_PLAN.EQUCODE',
            editor : true,
            validators : [ 'strLength[0,10]' ],
            editable : true
        }, {
            field : 'equName',
            title : 'T_CORE_EQU_FUNC_CHECK_PLAN.EQUNAME',
            editor : true,
            validators : [ 'strLength[0,43]' ]
        }, {
            field : 'modelNo',
            title : 'T_CORE_EQU_FUNC_CHECK_PLAN.MODELNO',
            editor : true,
            validators : [ 'strLength[0,31]' ]
        }, {
            field : 'sn',
            title : 'T_CORE_EQU_FUNC_CHECK_PLAN.SN',
            editor : true,
            validators : [ 'strLength[0,31]' ]
        }, {
            field : 'mfg',
            title : 'T_CORE_EQU_FUNC_CHECK_PLAN.MFG',
            editor : true,
            validators : [ 'strLength[0,43]' ]
        }, {
            field : 'checkPeriod',
            title : 'T_CORE_EQU_FUNC_CHECK_PLAN.CHECKPERIOD',
            editor : true,
            validators : [ 'strLength[0,11]' ]
        }, {
            field : 'lastCheckDate',
            title : 'T_CORE_EQU_FUNC_CHECK_PLAN.LASTCHECKDATE',
            editor : true,
            type : 'date',
            validators : [ 'notEmpty' ],
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('core-annual-plan-detail-child-comp');
                var nextCheckDate = grid.getData()[rowIndex].nextCheckDate;
                if (!Gikam.isEmpty(nextCheckDate)) {
                    if (value > nextCheckDate) {
                        grid.setData({
                            index : rowIndex,
                            lastCheckDate : ''
                        });
                        Gikam.alert('CORE.MODULE.QMS.ANNUALPLAN.TIP.LASTCHECKDATE_CANNOT_AFTER_NEXTCHECKDATE');
                    }
                }
            }
        }, {
            field : 'nextCheckDate',
            title : 'T_CORE_EQU_FUNC_CHECK_PLAN.NEXTCHECKDATE',
            editor : true,
            type : 'date',
            validators : [ 'notEmpty' ],
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('core-annual-plan-detail-child-comp');
                var lastCheckDate = grid.getData()[rowIndex].lastCheckDate;
                if (!Gikam.isEmpty(lastCheckDate)) {
                    if (lastCheckDate > value) {
                        Gikam.alert('CORE.MODULE.QMS.ANNUALPLAN.TIP.NEXTCHECKDATE_CANNOT_BEFROE_LASTCHECKDATE');
                        grid.setData({
                            index : rowIndex,
                            nextCheckDate : ''
                        })
                    }
                }
            }
        }, {
            field : 'checkUserName',
            title : 'T_CORE_EQU_FUNC_CHECK_PLAN.CHECKUSERNAME',
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,21]' ],
            type : 'choose',
            category : 'user',
            single : true,
            targetFields : [{ checkUserId : 'id' }, { checkUserName : 'userName' }]
        }, {
            field : 'remark',
            title : 'T_CORE_EQU_FUNC_CHECK_PLAN.REMARK',
            editor : true,
            validators : [ 'strLength[0,170]' ]
        } ];
    },
    
    printReport : function(id) {
        var url = IFM_CONTEXT + '/secure/core/module/qms/annual-plans/queries';
        var jsonWrapper = Gikam.getJsonWrapper({
            f : {
                id_SEQ : id
            }
        });
        Gikam.postSync(url,jsonWrapper).done(function(item) {
            var planCategory = item.rows[0].planCategory;
            var reportGroupCode;
            switch (planCategory) {
            case 'empTrainPlan'://人员培训计划
                reportGroupCode = 'QMS_EMP_TRAIN_PLAN';
                break;
            case 'mReviewPlan'://管理评审计划
                
                break;
            case 'inAuditPlan'://内部审核计划
                
                break;
            case 'dailySupervise'://日常监督计划
                
                break;
            case 'qualityControl'://检验质量控制计划
                
                break;
            case 'verifyCalibrate'://仪器设备检定校准计划
                
                break;
            case 'periodVerify'://期间核查计划
                
                break;
            case 'equFuncCheck'://仪器设备功能检查计划
                
                break;
            };
            if (!Gikam.isEmpty(reportGroupCode)) {
                Gikam.viewReport(reportGroupCode, {
                    id : id
                });
            }
        });
        
    },
    
    baseUrl : IFM_CONTEXT + '/secure/core/module/qms/annual-plans',

    workflow : {
        dbTable : 'T_CORE_ANNUAL_PLAN',
        columns : [ {
            field : 'id',
            title : 'T_CORE_ANNUAL_PLAN.ID'
        } ]
    }

});
