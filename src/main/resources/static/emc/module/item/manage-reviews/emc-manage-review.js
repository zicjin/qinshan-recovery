var emcManageReview = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true,
        }, Gikam.status.getBpmnColumn(emcManageReview.workflow.dbTable), {
            field : 'qualityControlNo',
            title : 'T_EMC_MANAGE_REVIEW.QUALITYCONTROLNO'
        }, {
            field : 'createdByName',
            title : 'T_EMC_MANAGE_REVIEW.CREATEDUSER'
        } ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcManageReview.workflow.dbTable), {
            field : 'qualityControlNo',
            title : 'T_EMC_MANAGE_REVIEW.QUALITYCONTROLNO'
        }, {
            field : 'createdByName',
            title : 'T_EMC_MANAGE_REVIEW.CREATEDUSER'
        } ];
    },

    getPlanFormFields : function() {
        return [ {
            field : 'reviewPurpose',
            title : 'T_EMC_MANAGE_REVIEW.REVIEWPURPOSE',
            type : 'textarea',
            colspan : 2,
            rowspan : 2,
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'reviewScope',
            title : 'T_EMC_MANAGE_REVIEW.REVIEWSCOPE',
            type : 'textarea',
            colspan : 2,
            rowspan : 2,
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'reviewTime',
            title : 'T_EMC_MANAGE_REVIEW.REVIEWTIME',
            type : 'dateTime'
        }, {
            field : 'partakeUser',
            title : 'T_EMC_MANAGE_REVIEW.PARTAKEUSER',
            validators : [ 'strLength [0,120]' ],
            type : 'choose',
            category : 'user-simple',
            editable : true,
            single : false,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var ids = "";
                    var names = "";                    
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                            ids += rows[index].id;
                            names += rows[index].userName;
                            break;
                        }
                        ids += rows[index].id + ",";
                        names += rows[index].userName + ",";
                    }
                    this.setData({
                        id : Gikam.getComp('emc-manage-review-edit-list-grid').getSelections().id,
                        ids : ids,
                    	partakeUser : names
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-manage-review-edit-list-grid').getSelections().id,
                    ids : '',
                    partakeUser : '',
                });
            }
        }, {
            field : 'reviewAddress',
            title : 'T_EMC_MANAGE_REVIEW.REVIEWADDRESS',
            colspan : 2,
            validators : [ 'strLength [0,120]' ]
        }, {
            field : 'reviewWay',
            title : 'T_EMC_MANAGE_REVIEW.REVIEWWAY',
            colspan : 2,
            validators : [ 'strLength [0,120]' ]
        }, {
            field : 'reviewContent',
            title : 'T_EMC_MANAGE_REVIEW.REVIEWCONTENT',
            type : 'textarea',
            colspan : 2,
            rowspan : 2,
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'prepareWork',
            title : 'T_EMC_MANAGE_REVIEW.PREPAREWORK',
            type : 'textarea',
            colspan : 2,
            rowspan : 2,
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'reviewStartTime',
            title : 'T_EMC_MANAGE_REVIEW.REVIEWSTARTTIME',
            type : 'dateTime'
        }, {
            field : 'reviewEndTime',
            title : 'T_EMC_MANAGE_REVIEW.REVIEWENDTIME',
            type : 'dateTime'
        }, {
            field : 'ext$.reviewhost',
            title : 'T_EMC_MANAGE_REVIEW.REVIEWHOST',
            type : 'choose',
            category : 'user-simple',
            editable : true,
            single : false,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var ids = "";
                    var names = "";                    
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                            ids += rows[index].id;
                            names += rows[index].userName;
                            break;
                        }
                        ids += rows[index].id + ",";
                        names += rows[index].userName + ",";
                    }
                    this.setData({
                        id : Gikam.getComp('emc-manage-review-edit-list-grid').getSelections().id,
                        ids : ids,
                        ext$ : {
                        	reviewhost : names
                        },
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-manage-review-edit-list-grid').getSelections().id,
                    ids : '',
                    ext$ : {
                    	reviewhost : '',
                    },
                });
            }
        }, {
            field : 'ext$.nonvotparpants',
            title : 'T_EMC_MANAGE_REVIEW.NONVOTPARPANTS',
            type : 'choose',
            category : 'user-simple',
            editable : true,
            single : false,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var ids = "";
                    var data = Gikam.getComp('emc-manage-review-edit-list-grid').getSelections();
                    var names = "";	
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                            ids += rows[index].id;
                            names += rows[index].userName;
                            break;
                        }
                        ids += rows[index].id + ",";
                        names += rows[index].userName + ",";
                    }
                    this.setData({
                        id : Gikam.getComp('emc-manage-review-edit-list-grid').getSelections().id,
                        ids : ids,
                        ext$ : {
                        	nonvotparpants : names
                        },
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-manage-review-edit-list-grid').getSelections().id,
                    ids : '',
                    ext$ : {
                    	nonvotparpants : '',
                    },
                });
            }
        },  ];
    },
    
    getAnnualPlanFormFields : function() {
        return [ {
            field : 'reviewPurpose',
            title : 'T_EMC_MANAGE_REVIEW.REVIEWPURPOSE',
            type : 'textarea',
            colspan : 2,
            rowspan : 2,
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'reviewWay',
            title : 'T_EMC_MANAGE_REVIEW.REVIEWWAY',
            colspan : 2,
            validators : [ 'strLength [0,120]' ]
        }, {
            field : 'partakeUser',
            title : 'T_EMC_MANAGE_REVIEW.PARTAKEUSER',
            validators : [ 'strLength [0,120]' ],
            type : 'choose',
            category : 'user-simple',
            editable : true,
            single : false,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var ids = "";
                    var names = "";                    
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                            ids += rows[index].id;
                            names += rows[index].userName;
                            break;
                        }
                        ids += rows[index].id + ",";
                        names += rows[index].userName + ",";
                    }
                    this.setData({
                        id : Gikam.getComp('emc-manage-review-edit-list-grid').getSelections().id,
                        ids : ids,
                    	partakeUser : names
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-manage-review-edit-list-grid').getSelections().id,
                    ids : '',
                    partakeUser : '',
                });
            }
        }, {
            field : 'reviewTime',
            title : 'T_EMC_MANAGE_REVIEW.REVIEWTIME',
            type : 'dateTime'
        }, {
            field : 'reviewAddress',
            title : 'T_EMC_MANAGE_REVIEW.REVIEWADDRESS',
            colspan : 2,
            validators : [ 'strLength [0,120]' ]
        }, {
            field : 'reviewContent',
            title : 'T_EMC_MANAGE_REVIEW.REVIEWCONTENT',
            type : 'textarea',
            colspan : 2,
            rowspan : 2,
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'ext$.remark',
            title : 'T_EMC_MANAGE_REVIEW.REMARK',
        }];
    },

    getReportFormFields : function() {
        return [ {
            field : 'reviewBasis',
            title : 'T_EMC_MANAGE_REVIEW.REVIEWBASIS',
            type : 'textarea',
            colspan : 2,
            rowspan : 2,
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'reviewDesc',
            title : 'T_EMC_MANAGE_REVIEW.REVIEWDESC',
            type : 'textarea',
            colspan : 2,
            rowspan : 2,
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'advise',
            title : 'T_EMC_MANAGE_REVIEW.ADVISE',
            type : 'textarea',
            colspan : 2,
            rowspan : 2,
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'reviewOpinion',
            title : 'T_EMC_MANAGE_REVIEW.REVIEWOPINION',
            type : 'textarea',
            colspan : 2,
            rowspan : 2,
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'yearTarget',
            title : 'T_EMC_MANAGE_REVIEW.YEARTARGET',
            type : 'textarea',
            colspan : 2,
            rowspan : 2,
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'reviewGroupUser',
            title : 'T_EMC_MANAGE_REVIEW.REVIEWGROUPUSER',
            colspan : 2,
            validators : [ 'strLength [0,120]' ]
        } ]
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/manage-reviews',

    workflow : {
        dbTable : 'T_EMC_MANAGE_REVIEW',
        columns : [ {
            field : 'id',
            title : 'T_EMC_MANAGE_REVIEW.ID'
        } ]
    }
});
