emcAdmArea.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-adm-area-edit-list-grid',
            url : emcAdmArea.baseUrl + '/queries/raw',
            service : 'emcAdmAreaServiceImpl',
            dbTable : 'T_EMC_ADM_AREA',
            columns : emcAdmArea.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-adm-area-edit-list-base-info-form' ]);
                }
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('emc-adm-area-edit-list-base-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcAdmArea.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });

            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.ITEM.ADMAREAS.MODAL.ADD',
                        fields : emcAdmArea.getBaseInfoFormFields(),
                        url : emcAdmArea.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-adm-area-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-adm-area-edit-list-grid').deleteRows(emcAdmArea.baseUrl);
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.ITEM.ADMAREAS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    service : 'emcAdmAreaServiceImpl',
                    dbTable : 'T_EMC_ADM_AREA',
                    id : 'emc-adm-area-edit-list-base-info-form',
                    fields : emcAdmArea.getBaseInfoFormFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('emc-adm-area-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-adm-area-edit-list-layout',
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
