emcEquiptRepairPlan.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-equipt-repair-plan-edit-list-grid',
            url : emcEquiptRepairPlan.baseUrl + '/queries/raw',
            service : 'emcEquiptMaintainRepairServiceImpl',
            dbTable : 'T_EMC_EQUIPT_MAINTAIN_REPAIR',
            columns : emcEquiptRepairPlan.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-equipt-repair-plan-edit-list-base-info-form' ]);
                }
                var uploader = Gikam.getComp('emc-equipt-repair-plan-edit-list-uploader');
                uploader.setOptions({
                    bizId : ''
                });
                uploader.grid.cleanData();
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('emc-equipt-repair-plan-edit-list-base-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcEquiptRepairPlan.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
                var uploader = Gikam.getComp('emc-equipt-repair-plan-edit-list-uploader');
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
                        fields : emcEquiptRepairPlan.getBaseInfoFormFields(),
                        url : emcEquiptRepairPlan.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-equipt-repair-plan-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-equipt-repair-plan-edit-list-grid').deleteRows(emcEquiptRepairPlan.baseUrl);
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
                    service : 'emcEquiptMaintainRepairServiceImpl',
                    dbTable : 'T_EMC_EQUIPT_MAINTAIN_REPAIR',
                    id : 'emc-equipt-repair-plan-edit-list-base-info-form',
                    fields : emcEquiptRepairPlan.getBaseInfoFormFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('emc-equipt-repair-plan-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            }, {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'emc-equipt-repair-plan-edit-list-uploader',
                    dbTable : 'T_EMC_EQUIPT_MAINTAIN_REPAIR',
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
            id : 'emc-equipt-repair-plan-edit-list-layout',
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
