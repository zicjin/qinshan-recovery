emcDataCorrelation.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-data-correlation-edit-list-grid',
            url : emcDataCorrelation.baseUrl + '/queries/raw',
            columns : emcDataCorrelation.getEditListColumns(),
            service : 'emcDataCorrelationServiceImpl',
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-data-correlation-edit-list-item-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(emcDataCorrelation.baseUrl + '/{id}/items/queries', {
                            id : row.id
                        }),
                    });
                });
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-data-correlation-edit-list-grid').activeRowByIndex(0);
                } else if(Gikam.getComp('emc-data-correlation-edit-list-item-grid')) {
                    Gikam.getComp('emc-data-correlation-edit-list-item-grid').cleanData();
                }
            },
            deleteFormatter : function(row) {
                return row.relateName;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.BASEMDM.CORRELATIONS.MODAL.DATA_CORRELATION',
                        fields : emcDataCorrelation.getBaseInfoFormFields(),
                        url : emcDataCorrelation.baseUrl,
                        width : 350,
                        height : 190,
                        titleWidth : 70
                    }).done(function(id) {
                        Gikam.getComp('emc-data-correlation-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-data-correlation-edit-list-grid').deleteRows(emcDataCorrelation.baseUrl);
                }
            } ]
        }
    },

    getItemGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-data-correlation-edit-list-item-grid',
            columns : emcDataCorrelation.getItemEditListColumns(),
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.MDM.BASEMDM.CORRELATIONS.BUTTION.EDIT_ITEM',
                icon : 'edit',
                onClick : function() {
                    _this.editCorrelationItem();
                }
            } ]
        }
    },
    
    // 计算公式
    editRelateScript : function(index) {
        _this = this;

        var grid = Gikam.getComp('emc-data-correlation-edit-list-grid');
        var row = grid.getData()[index];
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BASEMDM.CORRELATIONS.MODAL.RELATESCRIPT',
            width : 600,
            height : 450,
            url : emcDataCorrelation.baseUrl + '/page/script-detail' + Gikam.param({
                id : row.id
            }),
            onAfterClose : function(script) {
                grid.refreshRowById(row.id);
            }
        });
    },

    // 编辑分析项
    editCorrelationItem : function() {
        var _this = this;
        var grid = Gikam.getComp('emc-data-correlation-edit-list-grid');
        var row = grid.getActivedRow();
        Gikam.create('modal', {
            title : Gikam.propI18N('EMC.MODULE.MDM.BASEMDM.CORRELATIONS.MODAL.ITEM_CHOOSE_MUL'),
            url : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/data-correlations/page/item-choose-mult-list' + Gikam.param({
                id : row.id
            }),
            onAfterClose : function(rows) {
                Gikam.getComp('emc-data-correlation-edit-list-item-grid').refresh({
                    url : Gikam.printf(emcDataCorrelation.baseUrl + '/{id}/items/queries', {
                        id : row.id
                    }),
                });
            }
        });
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-data-correlation-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.MDM.BASEMDM.CORRELATIONS.TAB.DATA_CORRELATION',
                        items : [ _this.getGridParam() ]
                    } ]
                } ]
            },
            south : {
                height : 300,
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.MDM.BASEMDM.CORRELATIONS.TAB.DATA_CORRELATION_ITEM',
                        items : [ _this.getItemGridParam() ]
                    } ]
                } ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
