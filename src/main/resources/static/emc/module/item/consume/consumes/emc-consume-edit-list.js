emcConsume.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'Grid',
            id : 'emc-consume-edit-list-grid',
            url : emcConsume.baseUrl + '/queries/raw',
            service : 'emcConsumeServiceImpl',
            columns : emcConsume.getEditListColumns(),
            checkOnActive : true,
            filterOpen : true,
            deleteFormatter : function(row) {
                return row.mtlName || row.id;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-consume-detail-stock-list-grid').cleanData();
                }
            },
            onRowActive : function(index, row) {
                _this.onCsmActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.ITEM.CONSUME.CONSUMES.MODAL.CONSUME_ADD',
                        fields : emcConsume.getBaseInfoFormFields(),
                        url : emcConsume.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-consume-edit-list-grid').refresh();
                        _this.onCsmActive({
                            id : id
                        });
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-consume-edit-list-grid').deleteRows(emcConsume.baseUrl).done(function(id) {
                        Gikam.getComp('emc-consume-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.ITEM.CONSUME.CONSUMES.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    _this.viewReport();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    var grid = Gikam.getComp('emc-consume-edit-list-grid');
                    Gikam.viewAudit('T_EMC_CONSUME', grid.getSelections());
                }
            } ]
        }
    },

    stockGrid : null,
    stockLogGrid : null,
    getStockGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.XL,
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.ITEM.CONSUME.CONSUMES.TAB.STOCK',
                items : [ {
                    type : 'grid',
                    id : 'emc-consume-detail-stock-list-grid',
                    url : _this.getCompUrl('stockGrid'),
                    columns : emcConsume.getStockListColumns(),
                    checkOnActive : true,
                    service : 'emcCsmStockServiceImpl',
                    deleteFormatter : function(row) {
                        return row.batchNo || row.id;
                    },
                    onRendered : function() {
                        _this.stockGrid = this;
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        } else {
                            Gikam.getComp('emc-consume-detail-stock-log-list-grid').cleanData();
                            var uploader = Gikam.getComp('emc-consume-detail-stock-uploader');
                            uploader.setOptions({
                                bizId : ''
                            })
                            uploader.grid.cleanData();
                        }
                    },
                    onRowActive : function(index, row) {
                        if (row) {
                            let
                            _logUrl = Gikam.printf(_this.urlManager['stockLogGrid'], {
                                stockId : row.id
                            });
                            Gikam.getComp('emc-consume-detail-stock-log-list-grid').refresh({
                                url : _logUrl,
                            });
                            var uploader = Gikam.getComp('emc-consume-detail-stock-uploader');
                            uploader.setOptions({
                                bizId : row.id
                            })
                            uploader.grid.refresh();
                        }
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var activeRow = Gikam.getComp('emc-consume-edit-list-grid').getActivedRow();

                            Gikam.preInsert({
                                modalTitle : 'EMC.MODULE.ITEM.CONSUME.CONSUMES.MODAL.STOCK_ADD',
                                fields : emcConsume.getStockBaseInfoFormFields(),
                                url : _this.getCompUrl('stockBaseInfo', activeRow.id)
                            }).done(function(id) {
                                Gikam.getComp('emc-consume-detail-stock-list-grid').refresh({
                                    url : _this.getCompUrl('stockGrid', activeRow.id),
                                    requestData : {
                                        status_SEQ : '0',
                                        stockQty_NG : 0,
                                    },
                                });

                                Gikam.getComp('emc-consume-edit-list-grid').refreshRowById(activeRow.id);
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            var activeRow = Gikam.getComp('emc-consume-edit-list-grid').getActivedRow();
                            Gikam.getComp('emc-consume-detail-stock-list-grid').deleteRows(_this.getCompUrl('stockBaseInfo', activeRow.id)).done(function() {
                                Gikam.getComp('emc-consume-detail-stock-list-grid').refresh({
                                    url : _this.getCompUrl('stockGrid', activeRow.id),
                                    requestData : {
                                        status_SEQ : '0',
                                        stockQty_NG : 0,
                                    },
                                });

                                Gikam.getComp('emc-consume-edit-list-grid').refreshRowById(activeRow.id);
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.ITEM.CONSUME.CONSUMES.STOCK.BUTTON.STORAGE',
                        icon : 'add',
                        onClick : function() {
                            var selections = Gikam.getComp('emc-consume-detail-stock-list-grid').getSelections();
                            if (selections.length === 1) {
                                var activeRow = Gikam.getComp('emc-consume-edit-list-grid').getActivedRow();

                                Gikam.create('modal', {
                                    width : 400,
                                    height : 300,
                                    title : 'EMC.MODULE.ITEM.CONSUME.CONSUMES.MODAL.STOCK_STORAGE',
                                    url : Gikam.printf('/secure/emc/module/item/consume/consumes/page/{id}/stocks/storage', {
                                        id : activeRow.id,
                                    }),
                                    onAfterClose : function() {
                                        Gikam.getComp('emc-consume-detail-stock-list-grid').refresh({
                                            url : _this.getCompUrl('stockGrid', activeRow.id),
                                            requestData : {
                                                status_SEQ : '0',
                                                stockQty_NG : 0,
                                            },
                                        });

                                        Gikam.getComp('emc-consume-edit-list-grid').refreshRowById(activeRow.id);
                                    }
                                });
                            } else {
                                Gikam.alert('EMC.MODULE.ITEM.CONSUME.CONSUMES.STOCK.TIP.CHOOSE_ONE');
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.ITEM.CONSUME.CONSUMES.STOCK.BUTTON.INVENTORY',
                        icon : 'add',
                        onClick : function() {
                            var selections = Gikam.getComp('emc-consume-detail-stock-list-grid').getSelections();
                            if (selections.length > 0) {
                                var activeRow = Gikam.getComp('emc-consume-edit-list-grid').getActivedRow();

                                Gikam.create('modal', {
                                    width : 400,
                                    height : 300,
                                    title : 'EMC.MODULE.ITEM.CONSUME.CONSUMES.MODAL.STOCK_INVENTORY',
                                    url : Gikam.printf('/secure/emc/module/item/consume/consumes/page/{id}/stocks/inventory', {
                                        id : activeRow.id,
                                    }),
                                    onAfterClose : function() {
                                        Gikam.getComp('emc-consume-detail-stock-list-grid').refresh({
                                            url : _this.getCompUrl('stockGrid', activeRow.id),
                                            requestData : {
                                                status_SEQ : '0',
                                                stockQty_NG : 0,
                                            },
                                        });

                                        Gikam.getComp('emc-consume-edit-list-grid').refreshRowById(activeRow.id);
                                    }
                                });
                            } else {
                                Gikam.alert('EMC.MODULE.ITEM.CONSUME.CONSUMES.INVENTORY.TIP.CHOOSE');
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.ITEM.CONSUME.CONSUMES.STOCK.BUTTON.ABANDON',
                        icon : 'cancel',
                        onClick : function() {
                            var selections = Gikam.getComp('emc-consume-detail-stock-list-grid').getSelections();
                            if (selections.length > 0) {
                                var activedRow = Gikam.getComp('emc-consume-edit-list-grid').getActivedRow();

                                Gikam.create('modal', {
                                    width : 400,
                                    height : 300,
                                    title : 'EMC.MODULE.ITEM.CONSUME.CONSUMES.MODAL.STOCK_ABANDON',
                                    url : Gikam.printf('/secure/emc/module/item/consume/consumes/page/{id}/stocks/abandon', {
                                        id : activedRow.id,
                                    }),
                                    onAfterClose : function() {
                                        Gikam.getComp('emc-consume-detail-stock-list-grid').refresh({
                                            url : _this.getCompUrl('stockGrid', activedRow.id),
                                            requestData : {
                                                status_SEQ : '0',
                                                stockQty_NG : 0,
                                            },
                                        });
                                        Gikam.getComp('emc-consume-edit-list-grid').refreshRowById(activedRow.id);
                                    }
                                });
                            } else {
                                Gikam.alert('EMC.MODULE.ITEM.CONSUME.CONSUMES.ABANDON.TIP.CHOOSE');
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.ITEM.CONSUME.CONSUMES.STOCK.BUTTON.RECIEVE',
                        icon : 'remove-row',
                        onClick : function() {
                            var selections = Gikam.getComp('emc-consume-detail-stock-list-grid').getSelections();
                            if (selections.length === 1) {
                                var activeRow = Gikam.getComp('emc-consume-edit-list-grid').getActivedRow();

                                Gikam.create('modal', {
                                    width : 400,
                                    height : 400,
                                    title : 'EMC.MODULE.ITEM.CONSUME.CONSUMES.MODAL.STOCK_RECIEVE',
                                    url : Gikam.printf('/secure/emc/module/item/consume/consumes/page/{id}/stocks/recieve', {
                                        id : activeRow.id,
                                    }),
                                    onAfterClose : function() {
                                        Gikam.getComp('emc-consume-detail-stock-list-grid').refresh({
                                            url : _this.getCompUrl('stockGrid', activeRow.id),
                                            requestData : {
                                                status_SEQ : '0',
                                                stockQty_NG : 0,
                                            },
                                        });

                                        Gikam.getComp('emc-consume-edit-list-grid').refreshRowById(activeRow.id);
                                    }
                                });
                            } else {
                                Gikam.alert('EMC.MODULE.ITEM.CONSUME.CONSUMES.STOCK.TIP.CHOOSE_ONE');
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.ITEM.CONSUME.CONSUMES.STOCK.BUTTON.SHOWALL',
                        icon : 'select',
                        onClick : function() {
                            var activedRow = Gikam.getComp('emc-consume-edit-list-grid').getActivedRow();
                            Gikam.getComp('emc-consume-detail-stock-list-grid').refresh({
                                url : _this.getCompUrl('stockGrid', activedRow.id),
                                requestData : {
                                    status_SEQ : null,
                                    stockQty_NG : null,
                                },
                            });
                        }
                    } ]
                } ]
            } ]
        }
    },

    getStockLogGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'emc-consume-detail-stock-concat-list-grid',
            width : Gikam.width.XL,
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.ITEM.CONSUME.CONSUMES.TAB.STOCKLOG',
                items : [ {
                    type : 'grid',
                    id : 'emc-consume-detail-stock-log-list-grid',
                    url : _this.getCompUrl('stockLogGrid'),
                    columns : emcConsume.getStockLogListColumns(),
                    service : 'emcCsmStockLogServiceImpl',
                    deleteFormatter : function(row) {
                        return row.logType || row.id;
                    },
                    onRendered : function() {
                        _this.stockLogGrid = this;
                    },
                }, ]
            }, {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'emc-consume-detail-stock-uploader',
                    dbTable : 'T_EMC_CSM_STOCK',
                    bizId : '',
                    onBeforeUpload : function() {
                        this.setUploadData({})
                    }
                } ]
            } ]
        }
    },

    onCsmActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-consume-detail-stock-list-grid').refresh({
                url : _this.getCompUrl('stockGrid', row.id),
                requestData : {
                    status_SEQ : '0',
                    stockQty_NG : 0,
                },
            });
        }
    },

    viewReport : function() {
        var gridData = Gikam.getComp('emc-consume-edit-list-grid').getSelections();
        if (gridData.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.viewReport('MTL_REPORT', {
            id : gridData[0].id
        });
    },

    urlManager : {
        baseInfoForm : emcConsume.baseUrl + '/{id}',
        stockGrid : emcConsume.baseUrl + '/{id}/stocks/queries',
        stockBaseInfo : emcConsume.baseUrl + '/{id}/stocks',
        stockLogGrid : emcConsume.baseUrl + '/stocks/{stockId}/logs/queries',
        stockStorage : emcConsume.baseUrl + '/stocks/{stockId}/action/stockin',
        stockInventory : emcConsume.baseUrl + '/{id}/stocks/action/inventory',
        stockAbandon : emcConsume.baseUrl + '/{id}/stocks/action/abandon',
        stockRecieve : emcConsume.baseUrl + '/stocks/{id}/action/recieve'
    },

    getCompUrl : function(name, id) {
        if (Gikam.isEmpty(id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : id
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            west : {
                width : 600,
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ _this.getStockGridParam() ]
                    },
                    center : {
                        items : [ _this.getStockLogGridParam() ]
                    }
                } ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
