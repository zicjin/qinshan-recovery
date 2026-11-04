var coreManageReview = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreManageReview.workflow.dbTable), {
            field : 'annualPlanNo',
            title : 'T_CORE_MANAGE_REVIEW.ANNUALPLANNO',
            type : 'select',
            category : 'annual-plan'
        }, {
            field : 'reviewNo',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWNO',
            type : 'link'
        }, {
            field : 'reviewPurpose',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWPURPOSE'
        }, {
            field : 'reviewContent',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWCONTENT'
        }, {
            field : 'reviewReq',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWREQ'
        }, {
            field : 'reviewDate',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWDATE'
        }, {
            field : 'reviewUserName',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWUSERNAME'
        }, {
            field : 'reviewStandard',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWSTANDARD'
        }, {
            field : 'reviewPlace',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWPLACE'
        }, {
            field : 'remark',
            title : 'T_CORE_MANAGE_REVIEW.REMARK'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'reviewNo',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWNO',
            validators : [ 'notEmpty', 'strLength[0,15]' ],
            readonly : true
        }, {
            field : 'annualPlanNo',
            title : 'T_CORE_MANAGE_REVIEW.ANNUALPLANNO',
            validators : [ 'strLength[0,15]' ],
            type : 'select',
            category : 'annual-plan',
            firstBlank : false,
            param : {
                planCategory : 'mReviewPlan'
            },
            onChange : function(field, value, rowIndex, oldValue, text) {
                var comp = Gikam.getComp('core-manage-review-detail-base-info-form');
                workspace.window.showMask(true);
                Gikam.getText(Gikam.printf(IFM_CONTEXT + '/secure/core/module/qms/annual-plans/{id}/manage-review-plan', {
                    id : value
                })).done(function(item) {
                    var data = Gikam.parseJson(item);
                    comp.setData({'index' : comp.getData().id, 'annualPlanId': data.annualPlanId}, true);
                    comp.setData({'index' : comp.getData().id, 'reviewPurpose': data.reviewPurpose}, true);
                    comp.setData({'index' : comp.getData().id, 'reviewContent': data.reviewContent}, true);
                    comp.setData({'index' : comp.getData().id, 'reviewReq': data.reviewReq}, true);
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        }, {
            field : 'reviewMethod',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWMETHOD',
            validators : [ 'notEmpty', 'strLength[0,1333]' ]
        }, {
            field : 'reviewDate',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWDATE',
            validators : [ 'notEmpty' ],
            type : 'date'
        }, {
            field : 'chairUserName',
            title : 'T_CORE_MANAGE_REVIEW.CHAIRUSERNAME',
            validators : [ 'notEmpty', 'strLength[0,21]' ],
            type : 'choose',
            category : 'user',
            single : true,
            targetFields : [{ chairUserId : 'id' }, { chairUserName : 'userName' }]
        }, {
            field : 'reviewPlace',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWPLACE',
            validators : [ 'notEmpty', 'strLength[0,42]' ]
        }, {
            field : 'reviewPurpose',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWPURPOSE',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'reviewStandard',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWSTANDARD',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'reviewContent',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWCONTENT',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'reviewUserName',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWUSERNAME',
            validators : [ 'notEmpty', 'strLength[0,682]' ],
            colspan : 3,
            type : 'choose',
            category : 'mult-user',
            single : false,
            onBeforeChoose : function(index) {
                var comp = Gikam.getComp('core-manage-review-detail-base-info-form');
                var ids = comp.getData().reviewUserId;
                if (Gikam.isNotEmpty(ids)) {
                    return {
                        id_DIFFER : ids.split(','),
                    };
                }
            },
            onClean : function(index) {
                var form = Gikam.getComp('core-manage-review-detail-base-info-form');
                form.setData({'index' : index, 'reviewUserId': ''}, true);
                form.setData({'index' : index, 'reviewUserName': ''}, true);
            },
            onAfterChoose : function(rows, index) {
                var comp = Gikam.getComp('core-manage-review-detail-base-info-form');
                var ids = comp.getData().reviewUserId;
                var names = comp.getData().reviewUserName;
                var idList = new Array();
                var nameList = new Array();
                if (Gikam.isNotEmpty(rows)) {
                    Gikam.each(rows, function(index, row){
                        idList[index] = row.id;
                        nameList[index] = row.userName;
                    });
                    comp.setData({'index' : index, 'reviewUserId': (Gikam.isEmpty(ids)?'':ids + ',') + idList.join(',')}, true);
                    comp.setData({'index' : index, 'reviewUserName': (Gikam.isEmpty(names)?'':names + ',') + nameList.join(',')}, true);
                }
            }
        }, {
            field : 'reviewReq',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWREQ',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor',
            toolbarStartupExpanded : false
        }, {
            field : 'remark',
            title : 'T_CORE_MANAGE_REVIEW.REMARK',
            validators : [ 'strLength[0,170]' ],
            colspan : 3
        } ];
    },

    getAuditInfoFormFields1 : function() {
        return [ {
            field : 'reviewNo',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWNO',
            validators : [ 'notEmpty', 'strLength[0,15]' ],
            readonly : true
        }, {
            field : 'annualPlanNo',
            title : 'T_CORE_MANAGE_REVIEW.ANNUALPLANNO',
            readonly : true,
            validators : [ 'strLength[0,15]' ],
            type : 'choose',
            category : 'annual-plan',
            single : true,
            onBeforeChoose : function(index) {
                return {
                    planCategory_SEQ : 'mReviewPlan',
                };
            },
            targetFields : [{ annualPlanId : 'id' }, { annualPlanNo : 'annualPlanNo' }]
        }, {
            field : 'reviewMethod',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWMETHOD',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ]
        }, {
            field : 'reviewDate',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWDATE',
            readonly : true,
            validators : [ 'notEmpty' ],
            type : 'date'
        }, {
            field : 'chairUserName',
            title : 'T_CORE_MANAGE_REVIEW.CHAIRUSERNAME',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,21]' ],
            type : 'choose',
            category : 'user',
            targetFields : [{ chairUserId : 'id' }, { chairUserName : 'userName' }]
        }, {
            field : 'reviewPlace',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWPLACE',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,42]' ]
        }, {
            field : 'reviewPurpose',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWPURPOSE',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'reviewStandard',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWSTANDARD',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'reviewContent',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWCONTENT',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'reviewUserName',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWUSERNAME',
            validators : [ 'notEmpty', 'strLength[0,682]' ],
            readonly : true,
            colspan : 3
        }, {
            field : 'reviewReq',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWREQ',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'remark',
            title : 'T_CORE_MANAGE_REVIEW.REMARK',
            readonly : true,
            validators : [ 'strLength[0,170]' ],
            colspan : 3
        } ];
    },
    
    getAuditInfoFormFields2 : function() {
        return [ {
            field : 'reviewNo',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWNO',
            validators : [ 'notEmpty', 'strLength[0,15]' ],
            readonly : true
        }, {
            field : 'annualPlanNo',
            title : 'T_CORE_MANAGE_REVIEW.ANNUALPLANNO',
            readonly : true,
            validators : [ 'strLength[0,15]' ],
            type : 'choose',
            category : 'annual-plan',
            single : true,
            onBeforeChoose : function(index) {
                return {
                    planCategory_SEQ : 'mReviewPlan',
                };
            },
            targetFields : [{ annualPlanId : 'id' }, { annualPlanNo : 'annualPlanNo' }]
        }, {
            field : 'reviewMethod',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWMETHOD',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ]
        }, {
            field : 'reviewDate',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWDATE',
            readonly : true,
            validators : [ 'notEmpty' ],
            type : 'date'
        }, {
            field : 'chairUserName',
            title : 'T_CORE_MANAGE_REVIEW.CHAIRUSERNAME',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,21]' ],
            type : 'choose',
            category : 'user',
            targetFields : [{ chairUserId : 'id' }, { chairUserName : 'userName' }]
        }, {
            field : 'reviewPlace',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWPLACE',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,42]' ]
        }, {
            field : 'reviewPurpose',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWPURPOSE',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'reviewStandard',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWSTANDARD',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'reviewContent',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWCONTENT',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'reviewUserName',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWUSERNAME',
            validators : [ 'notEmpty', 'strLength[0,682]' ],
            readonly : true,
            colspan : 3
        }, {
            field : 'reviewDescription',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWDESCRIPTION',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'reviewResult',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWRESULT',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'reviewReq',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWREQ',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'remark',
            title : 'T_CORE_MANAGE_REVIEW.REMARK',
            readonly : true,
            validators : [ 'strLength[0,170]' ],
            colspan : 3
        } ];
    },
    
    getAuditInfoFormFields3 : function() {
        return [ {
            field : 'reviewNo',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWNO',
            validators : [ 'notEmpty', 'strLength[0,15]' ],
            readonly : true
        }, {
            field : 'annualPlanNo',
            title : 'T_CORE_MANAGE_REVIEW.ANNUALPLANNO',
            readonly : true,
            validators : [ 'strLength[0,15]' ],
            type : 'choose',
            category : 'annual-plan',
            single : true,
            onBeforeChoose : function(index) {
                return {
                    planCategory_SEQ : 'mReviewPlan',
                };
            },
            targetFields : [{ annualPlanId : 'id' }, { annualPlanNo : 'annualPlanNo' }]
        }, {
            field : 'reviewMethod',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWMETHOD',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ]
        }, {
            field : 'reviewDate',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWDATE',
            readonly : true,
            validators : [ 'notEmpty' ],
            type : 'date'
        }, {
            field : 'chairUserName',
            title : 'T_CORE_MANAGE_REVIEW.CHAIRUSERNAME',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,21]' ],
            type : 'choose',
            category : 'user',
            targetFields : [{ chairUserId : 'id' }, { chairUserName : 'userName' }]
        }, {
            field : 'reviewPlace',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWPLACE',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,42]' ]
        }, {
            field : 'reviewPurpose',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWPURPOSE',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'reviewStandard',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWSTANDARD',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'reviewContent',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWCONTENT',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'reviewUserName',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWUSERNAME',
            validators : [ 'notEmpty', 'strLength[0,682]' ],
            readonly : true,
            colspan : 3
        }, {
            field : 'reviewDescription',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWDESCRIPTION',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            readonly : true,
            type : 'ckeditor'
        }, {
            field : 'reviewResult',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWRESULT',
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            readonly : true,
            type : 'ckeditor'
        }, {
            field : 'reviewReq',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWREQ',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'remark',
            title : 'T_CORE_MANAGE_REVIEW.REMARK',
            readonly : true,
            validators : [ 'strLength[0,170]' ],
            colspan : 3
        } ];
    },
    
    getSearchInfoFormFields : function() {
        return [ {
            field : 'reviewNo',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWNO',
            validators : [ 'notEmpty', 'strLength[0,15]' ],
            readonly : true
        }, {
            field : 'annualPlanNo',
            title : 'T_CORE_MANAGE_REVIEW.ANNUALPLANNO',
            validators : [ 'strLength[0,15]' ],
            type : 'choose',
            category : 'annual-plan',
            single : true,
            readonly : true,
            onBeforeChoose : function(index) {
                return {
                    planCategory_SEQ : 'mReviewPlan',
                };
            },
            targetFields : [{ annualPlanId : 'id' }, { annualPlanNo : 'annualPlanNo' }]
        }, {
            field : 'reviewMethod',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWMETHOD',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ]
        }, {
            field : 'reviewDate',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWDATE',
            readonly : true,
            validators : [ 'notEmpty' ],
            type : 'date'
        }, {
            field : 'chairUserName',
            title : 'T_CORE_MANAGE_REVIEW.CHAIRUSERNAME',
            validators : [ 'notEmpty', 'strLength[0,21]' ],
            readonly : true,
            type : 'choose',
            category : 'user',
            targetFields : [{ chairUserId : 'id' }, { chairUserName : 'userName' }]
        }, {
            field : 'reviewPlace',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWPLACE',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,42]' ]
        }, {
            field : 'reviewPurpose',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWPURPOSE',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'reviewStandard',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWSTANDARD',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'reviewContent',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWCONTENT',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'reviewUserName',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWUSERNAME',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,682]' ],
            colspan : 3
        }, {
            field : 'reviewDescription',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWDESCRIPTION',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'reviewResult',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWRESULT',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'reviewReq',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWREQ',
            readonly : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'remark',
            title : 'T_CORE_MANAGE_REVIEW.REMARK',
            readonly : true,
            validators : [ 'strLength[0,170]' ],
            colspan : 3
        } ];
    },
    
    getEditImproveListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'problems',
            title : 'T_CORE_MANAGE_REVIEW_IMPROVE.PROBLEMS',
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ]
        }, {
            field : 'improvePlan',
            title : 'T_CORE_MANAGE_REVIEW_IMPROVE.IMPROVEPLAN',
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ]
        }, {
            field : 'improveOrgName',
            title : 'T_CORE_MANAGE_REVIEW_IMPROVE.IMPROVEORGNAME',
            width : 300,
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,682]' ],
            type : 'choose',
            category : 'org',
            targetFields : [{ improveOrgId : 'id' }, { improveOrgName : 'orgName' }]
        }, {
            field : 'deadline',
            title : 'T_CORE_MANAGE_REVIEW_IMPROVE.DEADLINE',
            editor : true,
            validators : [ 'strLength[0,1333]' ]
        } ];
    },

    getSearchImproveListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'problems',
            title : 'T_CORE_MANAGE_REVIEW_IMPROVE.PROBLEMS',
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ]
        }, {
            field : 'improvePlan',
            title : 'T_CORE_MANAGE_REVIEW_IMPROVE.IMPROVEPLAN',
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,1333]' ]
        }, {
            field : 'improveOrgName',
            title : 'T_CORE_MANAGE_REVIEW_IMPROVE.IMPROVEORGNAME',
            width : 300,
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,682]' ],
            type : 'choose',
            category : 'org',
            targetFields : [{ improveOrgId : 'id' }, { improveOrgName : 'orgName' }]
        }, {
            field : 'deadline',
            title : 'T_CORE_MANAGE_REVIEW_IMPROVE.DEADLINE',
            editor : true,
            validators : [ 'strLength[0,1333]' ]
        } ];
    },
    
    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreManageReview.workflow.dbTable), {
            field : 'annualPlanNo',
            title : 'T_CORE_MANAGE_REVIEW.ANNUALPLANNO'
        }, {
            field : 'reviewNo',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWNO',
            type : 'link'
        }, {
            field : 'reviewPurpose',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWPURPOSE'
        }, {
            field : 'reviewContent',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWCONTENT'
        }, {
            field : 'reviewReq',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWREQ'
        }, {
            field : 'reviewDate',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWDATE'
        }, {
            field : 'reviewUserName',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWUSERNAME'
        }, {
            field : 'reviewStandard',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWSTANDARD'
        }, {
            field : 'reviewPlace',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWPLACE'
        }, {
            field : 'remark',
            title : 'T_CORE_MANAGE_REVIEW.REMARK'
        } ];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, Gikam.status.getBpmnColumn(coreManageReview.workflow.dbTable), {
            field : 'annualPlanNo',
            title : 'T_CORE_MANAGE_REVIEW.ANNUALPLANNO'
        }, {
            field : 'reviewNo',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWNO',
            type : 'link'
        }, {
            field : 'reviewPurpose',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWPURPOSE'
        }, {
            field : 'reviewContent',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWCONTENT'
        }, {
            field : 'reviewReq',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWREQ'
        }, {
            field : 'reviewDate',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWDATE'
        }, {
            field : 'reviewUserName',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWUSERNAME'
        }, {
            field : 'reviewStandard',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWSTANDARD'
        }, {
            field : 'reviewPlace',
            title : 'T_CORE_MANAGE_REVIEW.REVIEWPLACE'
        }, {
            field : 'remark',
            title : 'T_CORE_MANAGE_REVIEW.REMARK'
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/qms/manage-reviews',

    workflow : {
        dbTable : 'T_CORE_MANAGE_REVIEW',
        columns : [ {
            field : 'id',
            title : 'T_CORE_MANAGE_REVIEW.ID'
        } ]
    }

});
