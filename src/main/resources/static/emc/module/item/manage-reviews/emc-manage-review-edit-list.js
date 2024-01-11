emcManageReview.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-manage-review-edit-list-grid',
            url : emcManageReview.baseUrl + '/queries/raw',
            columns : emcManageReview.getEditListColumns(),
            checkOnActive : true,
            deleteFormatter : function(row) {
                return row.qualityControlNo || row.id;
            },
            onLoadSuccess : function(data) {
                if (data.length > 0) {
                    this.activeRowByIndex(0);
                } else {
                    var planForm = Gikam.getComp('emc-manage-review-edit-list-plan-form');
                    var annualPlanForm = Gikam.getComp('emc-manage-review-edit-list-annual-plan-form');                
                    var reportForm = Gikam.getComp('emc-manage-review-edit-list-report-form');
                    planForm.cleanData();
                    annualPlanForm.cleanData();
                    reportForm.cleanData();
                    var uploader = Gikam.getComp('emc-manage-review-edit-list-uploader');
                    uploader.setOptions({
                        bizId : ''
                    });
                    uploader.grid.cleanData();
                }
            },
            onRowActive : function(index, row) {
                var planForm = Gikam.getComp('emc-manage-review-edit-list-plan-form');
                var annualPlanForm = Gikam.getComp('emc-manage-review-edit-list-annual-plan-form');                
                var reportForm = Gikam.getComp('emc-manage-review-edit-list-report-form');
                var url = Gikam.printf(emcManageReview.baseUrl + '/{id}', {
                    id : row.id
                });
                planForm.refresh({
                    url : url
                }, true);
                annualPlanForm.refresh({
                    url : url
                }, true);
                reportForm.refresh({
                    url : url
                }, true);
                var uploader = Gikam.getComp('emc-manage-review-edit-list-uploader');
                uploader.setOptions({
                    bizId : row.id
                })
                uploader.grid.refresh();
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        fields : emcManageReview.getEditListColumns(),
                        url : emcManageReview.baseUrl
                    }).done(function() {
                        Gikam.getComp('emc-manage-review-edit-list-grid').refresh();
                    })
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-manage-review-edit-list-grid').deleteRows(emcManageReview.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('manageReviewPlan', Gikam.getComp('emc-manage-review-edit-list-grid').getSelections());
                }
       	 }  ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'emc-manage-review-edit-list-base-info-tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.ITEM.MANAGEREVIEWS.TAB.IMPLEMENTATION_PLAN',
                items : [ {
                    type : 'form',
                    id : 'emc-manage-review-edit-list-plan-form',
                    service : 'emcManageReviewServiceImpl',
                    columns : 2,
                    fields : emcManageReview.getPlanFormFields()
                } ]
            },{
                title : 'EMC.MODULE.ITEM.MANAGEREVIEWS.TAB.ANNUAL_PLAN',
                items : [ {
                    type : 'form',
                    id : 'emc-manage-review-edit-list-annual-plan-form',
                    service : 'emcManageReviewServiceImpl',
                    columns : 2,
                    fields : emcManageReview.getAnnualPlanFormFields()
                } ]
            },  {
                title : 'EMC.MODULE.ITEM.MANAGEREVIEWS.TAB.REPORT',
                items : [ {
                    type : 'form',
                    id : 'emc-manage-review-edit-list-report-form',
                    titleWidth : 160,
                    service : 'emcManageReviewServiceImpl',
                    columns : 2,
                    fields : emcManageReview.getReportFormFields()
                } ]
            }, {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'emc-manage-review-edit-list-uploader',
                    dbTable : 'T_EMC_MANAGE_REVIEW',
                    onBeforeUpload : function() {
                        this.setUploadData({})
                    }
                } ]
            } ]
        }
    },
    
    submit : function() {
        var grid = Gikam.getComp('emc-manage-review-edit-list-grid');
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : emcManageReview
        }).done(function() {
            grid.refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            west : {
                width : 430,
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getBaseInfoTab() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
