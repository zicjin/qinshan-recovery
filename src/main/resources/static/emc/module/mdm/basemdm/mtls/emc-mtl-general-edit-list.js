emcMtl.generalEditPage = {

    getGridParam : function() {
        var _this = this;
        var requestData = _this.param;
        // 一般试剂编码
        requestData['managementType_SEQ'] = 'genericReagent';
        requestData.orgId_SB = workspace.user.ext$.parentorgid;
        
        return {
            type : 'Grid',
            id : 'emc-mtl-general-edit-list-grid',
            service : 'emcMtlServiceImpl',
            url : emcMtl.baseUrl + '/queries/raw',
            columns : emcMtl.getGeneralEditListColumns(),
            page : true,
            pageSize : 50,
            checkOnActive : true,
            deleteFormatter : function(row) {
                return row.mtlName || row.id;
            },
            requestData : requestData,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    if (Gikam.getComp('emc-mtl-general-detail-stock-list-grid')) {
                        Gikam.getComp('emc-mtl-general-detail-stock-list-grid').cleanData();
                    }
                    if (Gikam.getComp('emc-mtl-detail-maitain-list-grid')) {
                        Gikam.getComp('emc-mtl-detail-maitain-list-grid').cleanData();
                    }
                }
            },
            onRowActive : function(index, row) {
                _this.onMtlActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.BASEMDM.MTLS.MODAL.MTL_ADD',
                        fields : emcMtl.getGeneralBaseInfoFormFields(),
                        url : emcMtl.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-mtl-general-edit-list-grid').refresh();
                        _this.onMtlActive({
                            id : id
                        });
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-mtl-general-edit-list-grid').deleteRows(emcMtl.baseUrl).done(function(id) {
                        Gikam.getComp('emc-mtl-general-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.MDM.BASEMDM.MTLS.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    _this.viewReport();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.EXCEL_EXPORT',
                icon : 'edit',
                onClick : function() {
                    Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}', {
                        templateCode : 'emcMtlExportTemplate',
                        service : 'emcMtlExportTemplate'
                    })).done(function(fileUri) {
                        var fileUrl = IFM_CONTEXT + fileUri;
                        Gikam.download(fileUrl);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.EXCEL_IMPORT',
                icon : 'edit',
                onClick : function() {
                    Gikam.importExcel({
                        url : IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'emcMtlImportFileFunction',
                        dbTable : 'T_EMC_MTL',
                        onImportSuccess : function(files) {
                            Gikam.getComp('emc-mtl-general-edit-list-grid').refresh();
                        }
                    });
                }
            } ]
        }
    },

    stockGrid : null,
    stockLogGrid : null,
    itemGrid : null,

    getStockGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.XL,
            fill : true,
            onActived : function(data) {
                var logTab = Gikam.getComp('emc-mtl-detail-stock-concat-list-grid');
                var itemTab = Gikam.getComp('emc-mtl-detail-maintain-concat-list-grid');
                if (data.index === 0) {
                    logTab && logTab.show();
                    itemTab && itemTab.hide();
                } else {
                    logTab && logTab.hide();
                    itemTab && itemTab.show();
                }
            },
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.MTLS.TAB.STOCK',
                items : [ {
                    type : 'grid',
                    id : 'emc-mtl-general-detail-stock-list-grid',
                    url : _this.getCompUrl('stockGrid'),
                    columns : emcMtl.getGeneralStockListColumns(),
                    checkOnActive : true,
                    service : 'emcStockServiceImpl',
                    deleteFormatter : function(row) {
                        return row.batchNo || row.id;
                    },
                    onRendered : function() {
                        _this.stockGrid = this;
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                            if (!Gikam.isEmpty(data)) {
                                let
                                _logUrl = Gikam.printf(_this.urlManager['stockLogGrid'], {
                                    stockId : data[0].id
                                });
                                Gikam.getComp('emc-mtl-detail-stock-log-list-grid').refresh({
                                    url : _logUrl,
                                });
                            }
                        } else {
                            if (Gikam.getComp('emc-mtl-detail-stock-log-list-grid')) {
                                Gikam.getComp('emc-mtl-detail-stock-log-list-grid').cleanData();
                            }
                        }
                    },
                    onRowActive : function(index, row) {
                        if (row) {
                            let
                            _logUrl = Gikam.printf(_this.urlManager['stockLogGrid'], {
                                stockId : row.id
                            });
                            Gikam.getComp('emc-mtl-detail-stock-log-list-grid').refresh({
                                url : _logUrl,
                            });
                            var uploader = Gikam.getComp('emc-mtl-detail-stock-uploader');
                            uploader.setOptions({
                                bizId : row.id
                            })
                            uploader.grid.refresh();
                        }
                    },
                    toolbar : _this.getStockToolbar()
                } ]
            } ]
        }
    },

    getStockToolbar : function() {
        var _this = this;
        return [ {
            type : 'button',
            text : 'GIKAM.BUTTON.INSERT',
            icon : 'add',
            onClick : function() {
                _this.insertStock();
            }
        }, {
            type : 'button',
            text : 'GIKAM.BUTTON.DELETE',
            icon : 'remove-row',
            onClick : function() {
                _this.deleteStock();
            }
        }, {
            type : 'button',
            text : 'EMC.MODULE.MDM.BASEMDM.MTLS.STOCK.BUTTON.INVENTORY',
            icon : 'add',
            onClick : function() {
                _this.inventoryStock();
            }
        }, {
            type : 'button',
            text : 'EMC.MODULE.MDM.BASEMDM.MTLS.STOCK.BUTTON.ABANDON',
            icon : 'cancel',
            onClick : function() {
                _this.abandonStock();
            }
        }, {
            type : 'button',
            text : 'EMC.MODULE.MDM.BASEMDM.MTLS.STOCK.BUTTON.RECIEVE',
            icon : 'remove-row',
            onClick : function() {
                _this.receiveStock();
            }
        }, {
            type : 'dropDownMenu',
            text : 'EMC.MODULE.MDM.BASEMDM.MTLS.BUTTON.QUICK_INPUT',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.EXCEL_EXPORT',
                icon : 'move-up',
                onClick : function() {
                    _this.quickExport();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.IMPORT',
                icon : 'move-down',
                onClick : function() {
                    _this.quickImport();
                }
            } ]
        }, {
            type : 'button',
            text : 'EMC.MODULE.MDM.BASEMDM.MTLS.STOCK.BUTTON.SHOWALL',
            icon : 'select',
            onClick : function() {
                var activedRow = Gikam.getComp('emc-mtl-general-edit-list-grid').getActivedRow();
                Gikam.getComp('emc-mtl-general-detail-stock-list-grid').refresh({
                    url : _this.getCompUrl('stockGrid', activedRow.id),
                    requestData : {
                        status_SEQ : null,
                        stockQty_NG : null,
                    },
                });
            }
        } ]
    },

    insertStock : function() {
        var _this = this;
        var activeRow = Gikam.getComp('emc-mtl-general-edit-list-grid').getActivedRow();

        Gikam.preInsert({
            modalTitle : 'EMC.MODULE.MDM.BASEMDM.MTLS.MODAL.STOCK_ADD',
            fields : emcMtl.getGeneralStockBaseInfoFormFields(),
            url : _this.getCompUrl('stockBaseInfo', activeRow.id)
        }).done(function(id) {
            Gikam.getComp('emc-mtl-general-detail-stock-list-grid').refresh({
                url : _this.getCompUrl('stockGrid', activeRow.id),
                requestData : {
                    status_SEQ : '0',
                    stockQty_NG : 0,
                },
            });

            Gikam.getComp('emc-mtl-general-edit-list-grid').refreshRowById(activeRow.id);
        });
    },

    deleteStock : function() {
        var _this = this;
        var activeRow = Gikam.getComp('emc-mtl-general-edit-list-grid').getActivedRow();
        Gikam.getComp('emc-mtl-general-detail-stock-list-grid').deleteRows(Gikam.printf(emcMtl.baseUrl + '/{id}/stocks', { id : activeRow.id })).done(function() {
            Gikam.getComp('emc-mtl-general-detail-stock-list-grid').refresh({
                url : _this.getCompUrl('stockGrid', activeRow.id),
                requestData : {
                    status_SEQ : '0',
                    stockQty_NG : 0,
                },
            });

            Gikam.getComp('emc-mtl-general-edit-list-grid').refreshRowById(activeRow.id);
        });
    },

    inventoryStock : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-mtl-general-detail-stock-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('EMC.MODULE.MDM.BASEMDM.MTLS.INVENTORY.TIP.CHOOSE');
            return;
        }

        var activeRow = Gikam.getComp('emc-mtl-general-edit-list-grid').getActivedRow();

        var modal = Gikam.create('modal', {
            title: "EMC.MODULE.MDM.BASEMDM.MTLS.MODAL.STOCK_INVENTORY",
            width : 400,
            height : 270,
        });
        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type: 'btnToolbar',
                    items: [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        icon : 'select',
                        onClick : function() {
                            if (form.validate()) {
                                data = form.getData();
                                var stockId = selections.map(function(value, index) {
                                    return value.id;
                                });
                                data.ext$.stockId = stockId.join(',');
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(Gikam.printf(emcMtl.baseUrl + '/{id}/stocks/action/inventory', {
                                    id : activeRow.id,
                                }), Gikam.getJsonWrapper(null, [ '', [ data ] ])).always(function() {
                                    Gikam.getComp('emc-mtl-general-edit-list-grid').refreshRowById(activeRow.id);
                                    Gikam.getComp('emc-mtl-general-detail-stock-list-grid').refresh({
                                        url : _this.getCompUrl('stockGrid', activeRow.id),
                                        requestData : {
                                            status_SEQ : '0',
                                            stockQty_NG : 0,
                                        },
                                    });
                                    modal.close();
                                    Gikam.getLastModal().window.closeMask();
                                });
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
                        icon : 'cancel',
                        onClick : function () {
                            modal.close();
                        }
                    } ]
                }]
            }
        });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            columns: 1,
            titleWidth: 80,
            fields: emcMtl.getStockInventoryFormFields(),
            autoSave: false
        });
    },

    abandonStock : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-mtl-general-detail-stock-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('EMC.MODULE.MDM.BASEMDM.MTLS.ABANDON.TIP.CHOOSE');
            return;
        }

        var activeRow = Gikam.getComp('emc-mtl-general-edit-list-grid').getActivedRow();

        var modal = Gikam.create('modal', {
            title: "EMC.MODULE.MDM.BASEMDM.MTLS.MODAL.STOCK_ABANDON",
            width : 400,
            height : 270,
        });
        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type: 'btnToolbar',
                    items: [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        icon : 'select',
                        onClick : function() {
                            if (form.validate()) {
                                data = form.getData();
                                var stockId = selections.map(function(value, index) {
                                    return value.id;
                                });
                                data.ext$.stockId = stockId.join(',');
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(Gikam.printf(emcMtl.baseUrl + '/{id}/stocks/action/abandon', {
                                    id : activeRow.id,
                                }), Gikam.getJsonWrapper(null, [ '', [ data ] ])).always(function() {
                                    Gikam.getComp('emc-mtl-general-edit-list-grid').refreshRowById(activeRow.id);
                                    Gikam.getComp('emc-mtl-general-detail-stock-list-grid').refresh({
                                        url : _this.getCompUrl('stockGrid', activeRow.id),
                                        requestData : {
                                            status_SEQ : '0',
                                            stockQty_NG : 0,
                                        },
                                    });
                                    modal.close();
                                    Gikam.getLastModal().window.closeMask();
                                });
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
                        icon : 'cancel',
                        onClick : function () {
                            modal.close();
                        }
                    } ]
                }]
            }
        });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            columns: 1,
            titleWidth: 60,
            fields: emcMtl.getStockAbandonFormFields(),
            autoSave: false
        });
    },

    receiveStock : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-mtl-general-detail-stock-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('EMC.MODULE.MDM.BASEMDM.MTLS.STOCK.TIP.CHOOSE_ONE');
            return;
        }

        var activeRow = Gikam.getComp('emc-mtl-general-edit-list-grid').getActivedRow();

        var modal = Gikam.create('modal', {
            title: "EMC.MODULE.MDM.BASEMDM.MTLS.MODAL.STOCK_ABANDON",
            width : 400,
            height : 270,
        });
        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type: 'btnToolbar',
                    items: [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        icon : 'select',
                        onClick : function() {
                            if (form.validate()) {
                                data = form.getData();
                                if (selections[0].stockQty < data.qty) {
                                    Gikam.alert('EMC.MODULE.MDM.BASEMDM.MTLS.STOCK.TIP.OUT_OF_STOCK');
                                    return;
                                }
                                var insertData = selections.map(function(value, index) {
                                    data.stockId = value.id;
                                    return data;
                                });
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(Gikam.printf(emcMtl.baseUrl + '/stocks/{id}/action/recieve', {
                                    id : selections[0].id
                                }), Gikam.getJsonWrapper({}, [ null, insertData ])).always(function() {
                                    Gikam.getComp('emc-mtl-general-edit-list-grid').refreshRowById(activeRow.id);
                                    Gikam.getComp('emc-mtl-general-detail-stock-list-grid').refresh({
                                        url : _this.getCompUrl('stockGrid', activeRow.id),
                                        requestData : {
                                            status_SEQ : '0',
                                            stockQty_NG : 0,
                                        },
                                    });
                                    modal.close();
                                    Gikam.getLastModal().window.closeMask();
                                });
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
                        icon : 'cancel',
                        onClick : function () {
                            modal.close();
                        }
                    } ]
                }]
            }
        });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            columns: 1,
            titleWidth: 80,
            fields: emcMtl.getStockRecieveFormFields(),
            autoSave: false
        });
    },

    /** 导出模板 */
    quickExport : function() {
        var mtls = Gikam.getComp('emc-mtl-general-edit-list-grid').getSelections();
        var mtlId = mtls.map(function (item) {
            return item.id;
        }).join();
        Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}/{id}', {
            templateCode : 'emcGeneralStockExportTemplate',
            service : 'emcGeneralStockExportTemplate',
            id : mtlId
        })).done(function(fileUri) {
            Gikam.download(IFM_CONTEXT + fileUri);
        });
    },

    /** 导入数据 */
    quickImport : function() {
        var activedMtl = Gikam.getComp('emc-mtl-general-edit-list-grid').getActivedRow();
        if (Gikam.isEmpty(activedMtl)) {
            return;
        }

        Gikam.importExcel({
            url : IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'emcGeneralStockImportFileFunction',
            dbTable : 'T_EMC_MTL',
            bizId : activedMtl.id,
            onImportSuccess : function() {
                Gikam.getComp('emc-mtl-general-detail-stock-list-grid').refresh();
            }
        });
    },

    getStockLogGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'emc-mtl-detail-stock-concat-list-grid',
            width : Gikam.width.XL,
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.MTLS.TAB.STOCKLOG',
                items : [ {
                    type : 'grid',
                    id : 'emc-mtl-detail-stock-log-list-grid',
                    url : _this.getCompUrl('stockLogGrid'),
                    columns : emcMtl.getStockLogListColumns(),
                    service : 'emcStockLogServiceImpl',
                    deleteFormatter : function(row) {
                        return row.logType || row.id;
                    },
                    onRendered : function() {
                        _this.stockLogGrid = this;
                    },
                }, ]
            }, {
                title : 'EMC.MODULE.MDM.BASEMDM.MTLS.TAB.FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-mtl-detail-stock-uploader',
                    dbTable : 'T_EMC_STOCK',
                    bizId : '',
                    onBeforeUpload : function() {
                        this.setUploadData({})
                    }
                } ]
            } ]
        }
    },

    onMtlActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-mtl-general-detail-stock-list-grid').refresh({
                url : _this.getCompUrl('stockGrid', row.id),
                requestData : {
                    status_SEQ : '0',
                    stockQty_NG : 0,
                },
            });
        }
    },

    viewReport : function() {
        var gridData = Gikam.getComp('emc-mtl-general-edit-list-grid').getSelections();
        if (gridData.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.viewReport('MTL_REPORT', {
            id : gridData[0].id
        });
    },

    urlManager : {
        baseInfoForm : emcMtl.baseUrl + '/{id}',
        stockGrid : emcMtl.baseUrl + '/{id}/stocks/queries',
        stockBaseInfo : emcMtl.baseUrl + '/{id}/general-mtl-stocks',
        maintainBaseInfo : emcMtl.baseUrl + '/{id}/maintains',
        stockLogGrid : emcMtl.baseUrl + '/stocks/{stockId}/logs/queries',
        stockStorage : emcMtl.baseUrl + '/stocks/{stockId}/action/stockin',
        stockInventory : emcMtl.baseUrl + '/{id}/stocks/action/inventory',
        stockAbandon : emcMtl.baseUrl + '/{id}/stocks/action/abandon',
        stockRecieve : emcMtl.baseUrl + '/stocks/{id}/action/recieve',
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
                width : '30%',
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

    init : function(param) {
        this.param = param;
        this.create();
    }
};
