emcEquiptPerCheckPlan.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-equipt-per-check-plan-edit-list-grid',
            url : emcEquiptPerCheckPlan.baseUrl + '/queries/raw',
            service : 'emcEquiptPerCheckPlanServiceImpl',
            dbTable : 'T_EMC_EQUIPT_PER_CHECK_PLAN',
            columns : emcEquiptPerCheckPlan.getPerCheckListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-equipt-per-check-plan-edit-list-base-info-form' ]);
                }
                var uploader = Gikam.getComp('emc-equipt-per-check-plan-edit-list-uploader');
                uploader.setOptions({
                    bizId : ''
                });
                uploader.grid.cleanData();
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('emc-equipt-per-check-plan-edit-list-base-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcEquiptPerCheckPlan.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
                var uploader = Gikam.getComp('emc-equipt-per-check-plan-edit-list-uploader');
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
                        modalTitle : '新增',
                        fields : emcEquiptPerCheckPlan.getBaseInfoFormFields(),
                        url : emcEquiptPerCheckPlan.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-equipt-per-check-plan-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-equipt-per-check-plan-edit-list-grid').deleteRows(emcEquiptPerCheckPlan.baseUrl);
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : '基本信息',
                items : [ {
                    type : 'form',
                    service : 'emcEquiptPerCheckPlanServiceImpl',
                    dbTable : 'T_EMC_EQUIPT_PER_CHECK_PLAN',
                    id : 'emc-equipt-per-check-plan-edit-list-base-info-form',
                    fields : emcEquiptPerCheckPlan.getBaseInfoFormFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('emc-equipt-per-check-plan-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            }, {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'emc-equipt-per-check-plan-edit-list-uploader',
                    dbTable : 'T_EMC_EQUIPT_PER_CHECK_PLAN',
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
            id : 'emc-equipt-per-check-plan-edit-list-layout',
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
