emcStaffAbilityEvaluation.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-staff-ability-evaluation-edit-list-grid',
            url : emcStaffAbilityEvaluation.baseUrl + '/queries/raw',
            service : 'emcStaffAbilityEvaluationServiceImpl',
            dbTable : 'T_EMC_STAFF_ABILITY_EVALUATION',
            columns : emcStaffAbilityEvaluation.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-staff-ability-evaluation-edit-list-base-info-form' ]);
                }
                var uploader = Gikam.getComp('emc-staff-ability-evaluation-edit-list-uploader');
                uploader.setOptions({
                    bizId : ''
                });
                uploader.grid.cleanData();
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('emc-staff-ability-evaluation-edit-list-base-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcStaffAbilityEvaluation.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
                var uploader = Gikam.getComp('emc-staff-ability-evaluation-edit-list-uploader');
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
                        modalTitle : 'EMC.MODULE.ITEM.STAFFABILITYEVALUATIONS.MODAL.ADD',
                        fields : emcStaffAbilityEvaluation.getBaseInfoFormFields(),
                        url : emcStaffAbilityEvaluation.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-staff-ability-evaluation-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-staff-ability-evaluation-edit-list-grid').deleteRows(emcStaffAbilityEvaluation.baseUrl);
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.ITEM.STAFFABILITYEVALUATIONS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    service : 'emcStaffAbilityEvaluationServiceImpl',
                    dbTable : 'T_EMC_STAFF_ABILITY_EVALUATION',
                    id : 'emc-staff-ability-evaluation-edit-list-base-info-form',
                    fields : emcStaffAbilityEvaluation.getBaseInfoFormFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('emc-staff-ability-evaluation-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            }, {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'emc-staff-ability-evaluation-edit-list-uploader',
                    dbTable : 'T_EMC_STAFF_ABILITY_EVALUATION',
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
            id : 'emc-staff-ability-evaluation-edit-list-layout',
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
