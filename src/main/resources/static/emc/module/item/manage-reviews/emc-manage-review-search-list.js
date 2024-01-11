emcManageReview.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-manage-review-search-list-grid',
            url : emcManageReview.baseUrl + '/queries/searchable',
            columns : emcManageReview.getEditListColumns(),
            requestData : _this.param,
            checkOnActive : true,
            onLoadSuccess : function(data) {
                if (data.length > 0) {
                    this.activeRowByIndex(0);
                } else {
                    var planForm = Gikam.getComp('emc-manage-review-search-list-plan-form');
                    var reportForm = Gikam.getComp('emc-manage-review-search-list-report-form');
                    var annualPlanForm = Gikam.getComp('emc-manage-review-search-list-annual-plan-form');                
                    planForm.cleanData();
                    reportForm.cleanData();
                    annualPlanForm.cleanData();
                    var uploader = Gikam.getComp('emc-manage-review-search-list-uploader');
                    uploader.grid.cleanData();
                }
            },
            onRowActive : function(index, row) {
                var planForm = Gikam.getComp('emc-manage-review-search-list-plan-form');
                var reportForm = Gikam.getComp('emc-manage-review-search-list-report-form');
                var annualPlanForm = Gikam.getComp('emc-manage-review-search-list-annual-plan-form');
                var url = Gikam.printf(emcManageReview.baseUrl + '/{id}', {
                    id : row.id
                });
                planForm.refresh({
                    url : url
                }, true);
                reportForm.refresh({
                    url : url
                }, true);
                annualPlanForm.refresh({
                    url : url
                }, true);                
                var uploader = Gikam.getComp('emc-manage-review-search-list-uploader');
                uploader.setOptions({
                    bizId : row.id
                })
                uploader.grid.refresh();
            },

            toolbar : [ {
	            type : 'button',
	            text : 'GIKAM.BUTTON.PREVIEW',
	            icon : 'print',
	            onClick : function() {
                    Gikam.viewReport('manageReviewPlan', Gikam.getComp('emc-manage-review-search-list-grid').getSelections());
	            }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'emc-manage-review-search-list-base-info-tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.ITEM.MANAGEREVIEWS.TAB.IMPLEMENTATION_PLAN',
                items : [ {
                    type : 'form',
                    readonly : true,
                    id : 'emc-manage-review-search-list-plan-form',
                    service : 'emcManageReviewServiceImpl',
                    columns : 2,
                    fields : emcManageReview.getPlanFormFields()
                } ]
            }, {
                title : 'EMC.MODULE.ITEM.MANAGEREVIEWS.TAB.ANNUAL_PLAN',
                items : [ {
                	readonly : true,
                	type : 'form',
                    id : 'emc-manage-review-search-list-annual-plan-form',
                    service : 'emcManageReviewServiceImpl',
                    columns : 2,
                    fields : emcManageReview.getAnnualPlanFormFields()
                } ]
            }, {
                title : 'EMC.MODULE.ITEM.MANAGEREVIEWS.TAB.REPORT',
                items : [ {
                    type : 'form',
                    readonly : true,
                    id : 'emc-manage-review-search-list-report-form',
                    titleWidth : 160,
                    service : 'emcManageReviewServiceImpl',
                    columns : 2,
                    fields : emcManageReview.getReportFormFields()
                } ]
            }, {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    readonly : true,
                    type : 'uploader',
                    id : 'emc-manage-review-search-list-uploader',
                    dbTable : 'T_EMC_MANAGE_REVIEW',
                    onBeforeUpload : function() {
                        this.setUploadData({})
                    }
                } ]
            } ]
        }
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

    init : function(param) {
        this.param = param;
        this.create();
    }
};
