emcMtlPerCheck.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-mtl-per-check-edit-list-grid',
            url : emcMtlPerCheck.baseUrl + '/queries/raw',
            service : 'emcMtlPerCheckServiceImpl',
            dbTable : 'T_EMC_MTL_PER_CHECK',
            columns : emcMtlPerCheck.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-mtl-per-check-edit-list-base-info-form' ]);
                }
                var uploader = Gikam.getComp('emc-mtl-per-check-edit-list-uploader');
                uploader.setOptions({
                    bizId : ''
                });
                uploader.grid.cleanData();
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('emc-mtl-per-check-edit-list-base-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcMtlPerCheck.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
                var uploader = Gikam.getComp('emc-mtl-per-check-edit-list-uploader');
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
                        modalTitle : 'EMC.MODULE.ITEM.MTLPERCHECKS.MODAL.ADD',
                        fields : emcMtlPerCheck.getBaseInfoFormFields(),
                        url : emcMtlPerCheck.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-mtl-per-check-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-mtl-per-check-edit-list-grid').deleteRows(emcMtlPerCheck.baseUrl);
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.ITEM.MTLPERCHECKS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    service : 'emcMtlPerCheckServiceImpl',
                    dbTable : 'T_EMC_MTL_PER_CHECK',
                    id : 'emc-mtl-per-check-edit-list-base-info-form',
                    fields : emcMtlPerCheck.getBaseInfoFormFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('emc-mtl-per-check-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            }, {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'emc-mtl-per-check-edit-list-uploader',
                    dbTable : 'T_EMC_MTL_PER_CHECK',
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
            id : 'emc-mtl-per-check-edit-list-layout',
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
