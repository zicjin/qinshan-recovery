emcYearTrainPlan.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-year-train-plan-audit-list-grid',
            url : emcYearTrainPlan.baseUrl + '/queries/raw',
            service : 'emcYearTrainPlanServiceImpl',
            dbTable : 'T_EMC_YEAR_TRAIN_PLAN',
            columns : emcYearTrainPlan.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-year-train-plan-audit-list-base-info-form' ]);
                }
                var uploader = Gikam.getComp('emc-year-train-plan-audit-list-uploader');
                uploader.setOptions({
                    bizId : ''
                });
                uploader.grid.cleanData();
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('emc-year-train-plan-audit-list-base-info-form').done(function(form) {
                    form.setReadonly(true);
                    form.refresh({
                        url : Gikam.printf(emcYearTrainPlan.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
                var uploader = Gikam.getComp('emc-year-train-plan-audit-list-uploader');
                uploader.setOptions({
                    bizId : row.id
                })
                uploader.grid.refresh();
            },
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.ITEM.YEARTRAINPLANS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    service : 'emcYearTrainPlanServiceImpl',
                    dbTable : 'T_EMC_YEAR_TRAIN_PLAN',
                    id : 'emc-year-train-plan-audit-list-base-info-form',
                    fields : emcYearTrainPlan.getBaseInfoFormFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('emc-year-train-plan-audit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            }, {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'emc-year-train-plan-audit-list-uploader',
                    dbTable : 'T_EMC_YEAR_TRAIN_PLAN',
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
            id : 'emc-year-train-plan-audit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 300,
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getBaseInfoTab() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
