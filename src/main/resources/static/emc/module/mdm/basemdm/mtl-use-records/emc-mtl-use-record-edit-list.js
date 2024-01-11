emcMtlUseRecord.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-mtl-use-record-edit-list-grid',
            url : emcMtlUseRecord.baseUrl + '/queries/raw',
            columns : emcMtlUseRecord.getEditListColumns(),
            service : 'emcMtlUseRecordServiceImpl',
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                	var selections = Gikam.getComp('emc-mtl-apply-edit-list-grid').getSelections();
                    if (selections.length === 1) {
                        var activeRow = Gikam.getComp('emc-mtl-apply-edit-list-grid').getActivedRow();
                        Gikam.create('modal', {
                            width : 400,
                            height : 280,
                            title : 'EMC.MODULE.MDM.BASEMDM.MTLS.MODAL.ADD',
                            url : Gikam.printf('/secure/emc/module/mdm/basemdm/mtl-use-records/page/{id}/apply', {
                                id : activeRow.id,
                            }),
                            onAfterClose : function() {
                                Gikam.getComp('emc-mtl-use-record-edit-list-grid').refresh();
                            }
                        });
                    } else {
                        Gikam.alert('EMC.MODULE.MDM.BASEMDM.MTLS.STOCK.APPLY.TIP.CHOOSE_ONE');
                    }
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-mtl-use-record-edit-list-grid').deleteRows(emcMtlUseRecord.baseUrl);
                }
            } ]
        }
    },

    getMtlApplyGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-mtl-apply-edit-list-grid',
            url : emcMtlUseRecord.baseUrl + '/stocks/logs/queries',
            columns : emcMtlUseRecord.getMtlApplyColumns(),
            checkOnActive : true,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-mtl-use-record-edit-list-grid').cleanData();
                }
            },
            onRowActive : function(index, row) {
                _this.onMtlApplyActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'review',
                onClick : function() {
                    Gikam.viewReport('mtlUseRecord', Gikam.getComp('emc-mtl-apply-edit-list-grid').getSelections());
                }
            } ]
        }

    },

    onMtlApplyActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-mtl-use-record-edit-list-grid').refresh({
                url : _this.getUseRecordUrl(row.id),
            });
        }
    },

    getUseRecordUrl : function(id) {
        if (id) {
            return Gikam.printf(emcMtlUseRecord.baseUrl + '/{id}/use-record/queries', {
                id : id
            })
        }
    },

    loadDetailPage : function(id) {
        workspace.window.load(emcMtlUseRecord.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-mtl-use-record-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
            	height : '50%',
            	items : [ this.getMtlApplyGrid() ]
            },

            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
