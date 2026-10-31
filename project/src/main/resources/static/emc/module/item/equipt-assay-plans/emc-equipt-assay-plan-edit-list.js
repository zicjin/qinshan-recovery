emcEquiptAssayPlan.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-equipt-assay-plan-edit-list-grid',
            url : emcEquiptAssayPlan.baseUrl + '/queries/raw',
            service : 'emcEquiptAssayPlanServiceImpl',
            dbTable : 'T_EMC_EQUIPT_ASSAY_PLAN',
            columns : emcEquiptAssayPlan.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-equipt-assay-plan-edit-list-base-info-form' ]);
                }
                var uploader = Gikam.getComp('emc-equipt-assay-plan-edit-list-uploader');
                uploader.setOptions({
                    bizId : ''
                });
                uploader.grid.cleanData();
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('emc-equipt-assay-plan-edit-list-base-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcEquiptAssayPlan.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
                var uploader = Gikam.getComp('emc-equipt-assay-plan-edit-list-uploader');
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
                        modalTitle : 'EMC.MODULE.ITEM.EQUIPTASSAYPLANS.MODAL.ADD',
                        fields : emcEquiptAssayPlan.getBaseInfoFormFields(),
                        url : emcEquiptAssayPlan.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-equipt-assay-plan-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-equipt-assay-plan-edit-list-grid').deleteRows(emcEquiptAssayPlan.baseUrl);
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.ITEM.EQUIPTASSAYPLANS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    service : 'emcEquiptAssayPlanServiceImpl',
                    dbTable : 'T_EMC_EQUIPT_ASSAY_PLAN',
                    id : 'emc-equipt-assay-plan-edit-list-base-info-form',
                    fields : emcEquiptAssayPlan.getBaseInfoFormFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('emc-equipt-assay-plan-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            }, {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'emc-equipt-assay-plan-edit-list-uploader',
                    dbTable : 'T_EMC_EQUIPT_ASSAY_PLAN',
                    onBeforeUpload : function() {
                        this.setUploadData({})
                    }
                } ]
            }  ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-equipt-assay-plan-edit-list-layout',
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
