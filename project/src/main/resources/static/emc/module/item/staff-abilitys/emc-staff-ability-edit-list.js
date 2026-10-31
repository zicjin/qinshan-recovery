emcStaffAbility.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-staff-ability-edit-list-grid',
            url : emcStaffAbility.baseUrl + '/queries/raw',
            service : 'emcStaffAbilityServiceImpl',
            dbTable : 'T_EMC_STAFF_ABILITY',
            columns : emcStaffAbility.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-staff-ability-edit-list-base-info-form' ]);
                }
                var uploader = Gikam.getComp('emc-staff-ability-edit-list-uploader');
                uploader.setOptions({
                    bizId : ''
                });
                uploader.grid.cleanData();
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('emc-staff-ability-edit-list-base-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcStaffAbility.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
                var uploader = Gikam.getComp('emc-staff-ability-edit-list-uploader');
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
                        modalTitle : 'EMC.MODULE.ITEM.STAFFABILITYS.MODAL.ADD',
                        fields : emcStaffAbility.getBaseInfoFormFields(),
                        url : emcStaffAbility.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-staff-ability-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-staff-ability-edit-list-grid').deleteRows(emcStaffAbility.baseUrl);
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.ITEM.STAFFABILITYS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    service : 'emcStaffAbilityServiceImpl',
                    dbTable : 'T_EMC_STAFF_ABILITY',
                    id : 'emc-staff-ability-edit-list-base-info-form',
                    fields : emcStaffAbility.getBaseInfoFormFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('emc-staff-ability-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            }, {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'emc-staff-ability-edit-list-uploader',
                    dbTable : 'T_EMC_STAFF_ABILITY',
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
            id : 'emc-staff-ability-edit-list-layout',
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
