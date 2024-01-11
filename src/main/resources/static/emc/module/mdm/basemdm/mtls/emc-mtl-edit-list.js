emcMtl.editPage = {

    getGridParam : function() {
        var _this = this;
        var requestData = _this.param;
        requestData['managementType_SEQ'] = 'stdMaterials';
        requestData.orgId_SEQ = workspace.user.ext$.parentorgid;

        return {
            type : 'Grid',
            id : 'emc-mtl-edit-list-grid',
            service : 'emcMtlServiceImpl',
            url : emcMtl.baseUrl + '/queries/raw',
            columns : emcMtl.getEditListColumns(),
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
                    if (Gikam.getComp('emc-mtl-detail-stock-list-grid')) {
                        Gikam.getComp('emc-mtl-detail-stock-list-grid').cleanData();
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
                        fields : emcMtl.getBaseInfoFormFields(),
                        url : emcMtl.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-mtl-edit-list-grid').refresh();
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
                    Gikam.getComp('emc-mtl-edit-list-grid').deleteRows(emcMtl.baseUrl).done(function(id) {
                        Gikam.getComp('emc-mtl-edit-list-grid').refresh();
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
                            Gikam.getComp('emc-mtl-edit-list-grid').refresh();
                        }
                    });
                }
            } ]
        }
    },

    stockGrid : null,
    maintainGrid : null,
    stockLogGrid : null,
    stockConcentGrid : null,
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
                    id : 'emc-mtl-detail-stock-list-grid',
                    url : _this.getCompUrl('stockGrid'),
                    columns : emcMtl.getStockListColumns(),
                    checkOnActive : true,
                    service : 'emcStockServiceImpl',
                    group : {
                        fields : [ 'ext$.standardlevel' ],
                        formatter : function(row) {
                            return row.ext$.standardlevel || '-';
                        }
                    },
                    deleteFormatter : function(row) {
                        return row.batchNo || row.id;
                    },
                    onRendered : function() {
                        _this.stockGrid = this;
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(1);
                            if (!Gikam.isEmpty(data)) {
                                let _logUrl = Gikam.printf(_this.urlManager['stockLogGrid'], {
                                        stockId : data[1].id
                                });
                                Gikam.getComp('emc-mtl-detail-stock-log-list-grid').refresh({
                                    url : _logUrl,
                                });
                                let _concentUrl = Gikam.printf(_this.urlManager['stockConcentGrid'], {
                                        stockId : data[1].id
                                });
                                Gikam.getComp('emc-mtl-detail-stock-concent-list-grid').refresh({
                                    url : _concentUrl,
                                });
                            }
                        } else {
                            if (Gikam.getComp('emc-mtl-detail-stock-log-list-grid')) {
                                Gikam.getComp('emc-mtl-detail-stock-log-list-grid').cleanData();
                            }
                            if (Gikam.getComp('emc-mtl-detail-stock-concent-list-grid')) {
                                Gikam.getComp('emc-mtl-detail-stock-concent-list-grid').cleanData();
                            }
                        }
                    },
                    onRowActive : function(index, row) {
                        if (row) {
                            if (isNaN(row.id)) {
                                return;
                            }

                            let _logUrl = Gikam.printf(_this.urlManager['stockLogGrid'], {
                                    stockId : row.id
                            });
                            Gikam.getComp('emc-mtl-detail-stock-log-list-grid').refresh({
                                url : _logUrl,
                            });
                            let _concentUrl = Gikam.printf(_this.urlManager['stockConcentGrid'], {
                                    stockId : row.id
                            });
                            Gikam.getComp('emc-mtl-detail-stock-concent-list-grid').refresh({
                                url : _concentUrl,
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
            }, {
                title : 'EMC.MODULE.MDM.BASEMDM.MTLS.TAB.MTLMAITAIN',
                items : [ {
                    type : 'grid',
                    id : 'emc-mtl-detail-maitain-list-grid',
                    url : _this.getCompUrl('maintainGrid'),
                    columns : emcMtl.getMaintainListColumns(),
                    checkOnActive : true,
                    service : 'emcMtlMaintainServiceImpl',
                    deleteFormatter : function(row) {
                        return row.category || row.id;
                    },
                    onRendered : function() {
                        _this.maintainGrid = this;
                    },
                    onRowActive : function(index, row) {
                        let
                            _url = Gikam.printf(_this.urlManager['maintainItemGrid'], {
                                mtlMaintainId : row.id
                            });
                        Gikam.getComp('emc-mtl-detail-maintain-item-list-grid').refresh({
                            url : _url
                        });
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        }

                        if (!Gikam.isEmpty(data)) {
                            if (!Gikam.isEmpty(data)) {
                                let
                                    _url = Gikam.printf(_this.urlManager['maintainItemGrid'], {
                                        mtlMaintainId : data[0].id
                                    });
                                Gikam.getComp('emc-mtl-detail-maintain-item-list-grid').refresh({
                                    url : _url
                                });
                            }
                        } else {
                            if (Gikam.getComp('emc-mtl-detail-maintain-item-list-grid')) {
                                Gikam.getComp('emc-mtl-detail-maintain-item-list-grid').cleanData();
                            }
                        }
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var activedRow = Gikam.getComp('emc-mtl-edit-list-grid').getActivedRow();
                            Gikam.preInsert({
                                modalTitle : 'EMC.MODULE.MDM.BASEMDM.MTLS.MODAL.MAINTAIN_ADD',
                                fields : emcMtl.getMaintainListColumns(),
                                url : _this.getCompUrl('maintainBaseInfo', activedRow.id)
                            }).done(function(id) {
                                Gikam.getComp('emc-mtl-detail-maitain-list-grid').refresh({
                                    url : _this.getCompUrl('maintainGrid', activedRow.id)
                                });
                                Gikam.getComp('emc-mtl-detail-stock-concent-list-grid').refresh();
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            var activedRow = Gikam.getComp('emc-mtl-edit-list-grid').getActivedRow();
                            Gikam.getComp('emc-mtl-detail-maitain-list-grid').deleteRows(_this.getCompUrl('maintainBaseInfo', activedRow.id)).done(function() {
                                Gikam.getComp('emc-mtl-detail-maitain-list-grid').refresh({
                                    url : _this.getCompUrl('maintainGrid', activedRow.id)
                                });
                                Gikam.getComp('emc-mtl-detail-stock-concent-list-grid').refresh();
                            });
                        }
                    } ]
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
            text : 'EMC.MODULE.MDM.BASEMDM.MTLS.STOCK.BUTTON.STORAGE',
            icon : 'add',
            hidden : true,
            onClick : function() {
                _this.addStorage();
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
        },{
            type : 'button',
            text : '批录入',
            icon : 'add',
            onClick : function() {
                _this.batchInput();
            }
        }, {
            type : 'button',
            text : 'EMC.MODULE.MDM.BASEMDM.MTLS.STOCK.BUTTON.SHOWALL',
            icon : 'select',
            onClick : function() {
                var activedRow = Gikam.getComp('emc-mtl-edit-list-grid').getActivedRow();
                Gikam.getComp('emc-mtl-detail-stock-list-grid').refresh({
                    url : _this.getCompUrl('stockGrid', activedRow.id),
                    requestData : {
                        status_SEQ : null,
                        stockQty_NG : null,
                    },
                });
            }
        }, {
            type : 'button',
            text : 'GIKAM.BUTTON.COPY',
            icon : 'copy',
            onClick : function() {
                _this.copyStock();
            }
        } ]
    },

    insertStock : function() {
        var _this = this;
        var activeRow = Gikam.getComp('emc-mtl-edit-list-grid').getActivedRow();

        Gikam.preInsert({
            modalTitle : 'EMC.MODULE.MDM.BASEMDM.MTLS.MODAL.STOCK_ADD',
            fields : emcMtl.getStockBaseInfoFormFields(),
            url : _this.getCompUrl('stockBaseInfo', activeRow.id),
            onFormRendered : function() {
                this.removeFieldValidator( 'ext$.productiondate' , ['notEmpty'] );
                this.removeFieldValidator( 'ext$.storagecondition' , ['notEmpty'] );
            }
        }).done(function(id) {
            Gikam.getComp('emc-mtl-detail-stock-list-grid').refresh({
                url : _this.getCompUrl('stockGrid', activeRow.id),
                requestData : {
                    status_SEQ : '0',
                    stockQty_NG : 0,
                },
            });

            Gikam.getComp('emc-mtl-edit-list-grid').refreshRowById(activeRow.id);
        });
    },

    deleteStock : function() {
        var _this = this;
        var activeRow = Gikam.getComp('emc-mtl-edit-list-grid').getActivedRow();
        Gikam.getComp('emc-mtl-detail-stock-list-grid').deleteRows(_this.getCompUrl('stockBaseInfo', activeRow.id)).done(function() {
            Gikam.getComp('emc-mtl-detail-stock-list-grid').refresh({
                url : _this.getCompUrl('stockGrid', activeRow.id),
                requestData : {
                    status_SEQ : '0',
                    stockQty_NG : 0,
                },
            });

            Gikam.getComp('emc-mtl-edit-list-grid').refreshRowById(activeRow.id);
        });
    },

    addStorage : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-mtl-detail-stock-list-grid').getSelections();
        if (selections.length === 1) {
            var activeRow = Gikam.getComp('emc-mtl-edit-list-grid').getActivedRow();

            Gikam.create('modal', {
                width : 400,
                height : 300,
                title : 'EMC.MODULE.MDM.BASEMDM.MTLS.MODAL.STOCK_STORAGE',
                url : Gikam.printf('/secure/emc/module/mdm/basemdm/mtls/page/{id}/stocks/storage', {
                    id : activeRow.id,
                }),
                onAfterClose : function() {
                    Gikam.getComp('emc-mtl-detail-stock-list-grid').refresh({
                        url : _this.getCompUrl('stockGrid', activeRow.id),
                        requestData : {
                            status_SEQ : '0',
                            stockQty_NG : 0,
                        },
                    });

                    Gikam.getComp('emc-mtl-edit-list-grid').refreshRowById(activeRow.id);
                }
            });
        } else {
            Gikam.alert('EMC.MODULE.MDM.BASEMDM.MTLS.STOCK.TIP.CHOOSE_ONE');
        }
    },

    inventoryStock : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-mtl-detail-stock-list-grid').getSelections();
        if (selections.length > 0) {
            var activeRow = Gikam.getComp('emc-mtl-edit-list-grid').getActivedRow();

            Gikam.create('modal', {
                width : 400,
                height : 300,
                title : 'EMC.MODULE.MDM.BASEMDM.MTLS.MODAL.STOCK_INVENTORY',
                url : Gikam.printf('/secure/emc/module/mdm/basemdm/mtls/page/{id}/stocks/inventory', {
                    id : activeRow.id,
                }),
                onAfterClose : function() {
                    Gikam.getComp('emc-mtl-detail-stock-list-grid').refresh({
                        url : _this.getCompUrl('stockGrid', activeRow.id),
                        requestData : {
                            status_SEQ : '0',
                            stockQty_NG : 0,
                        },
                    });

                    Gikam.getComp('emc-mtl-edit-list-grid').refreshRowById(activeRow.id);
                }
            });
        } else {
            Gikam.alert('EMC.MODULE.MDM.BASEMDM.MTLS.INVENTORY.TIP.CHOOSE');
        }
    },

    abandonStock : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-mtl-detail-stock-list-grid').getSelections();
        if (selections.length > 0) {
            var activeRow = Gikam.getComp('emc-mtl-edit-list-grid').getActivedRow();

            Gikam.create('modal', {
                width : 400,
                height : 300,
                title : 'EMC.MODULE.MDM.BASEMDM.MTLS.MODAL.STOCK_ABANDON',
                url : Gikam.printf('/secure/emc/module/mdm/basemdm/mtls/page/{id}/stocks/abandon', {
                    id : activeRow.id,
                }),
                onAfterClose : function() {
                    Gikam.getComp('emc-mtl-detail-stock-list-grid').refresh({
                        url : _this.getCompUrl('stockGrid', activedRow.id),
                        requestData : {
                            status_SEQ : '0',
                            stockQty_NG : 0,
                        },
                    });
                    Gikam.getComp('emc-mtl-edit-list-grid').refreshRowById(activeRow.id);
                }
            });
        } else {
            Gikam.alert('EMC.MODULE.MDM.BASEMDM.MTLS.ABANDON.TIP.CHOOSE');
        }
    },

    receiveStock : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-mtl-detail-stock-list-grid').getSelections();
        if (selections.length === 1) {
            var activeRow = Gikam.getComp('emc-mtl-edit-list-grid').getActivedRow();

            Gikam.create('modal', {
                width : 400,
                height : 400,
                title : 'EMC.MODULE.MDM.BASEMDM.MTLS.MODAL.STOCK_RECIEVE',
                url : Gikam.printf('/secure/emc/module/mdm/basemdm/mtls/page/{id}/stocks/recieve', {
                    id : activeRow.id,
                }),
                onAfterClose : function() {
                    Gikam.getComp('emc-mtl-detail-stock-list-grid').refresh({
                        url : _this.getCompUrl('stockGrid', activeRow.id),
                        requestData : {
                            status_SEQ : '0',
                            stockQty_NG : 0,
                        },
                    });

                    Gikam.getComp('emc-mtl-edit-list-grid').refreshRowById(activeRow.id);
                }
            });
        } else {
            Gikam.alert('EMC.MODULE.MDM.BASEMDM.MTLS.STOCK.TIP.CHOOSE_ONE');
        }
    },

    /** 导出模板 */
    quickExport : function() {
        var mtls = Gikam.getComp('emc-mtl-edit-list-grid').getSelections();
        var mtlId = mtls.map(function (item) {
            return item.id;
        }).join();
        Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}/{id}', {
            templateCode : 'pfStockExportTemplate',
            service : 'pfStockExportTemplate',
            id : mtlId
        })).done(function(fileUri) {
            Gikam.download(IFM_CONTEXT + fileUri);
        });
    },

    /** 导入数据 */
    quickImport : function() {
        var activedMtl = Gikam.getComp('emc-mtl-edit-list-grid').getActivedRow();
        if (Gikam.isEmpty(activedMtl)) {
            return;
        }

        Gikam.importExcel({
            url : IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'emcStockImportFileFunction',
            dbTable : 'T_EMC_MTL',
            bizId : activedMtl.id,
            onImportSuccess : function() {
                Gikam.getComp('emc-mtl-detail-stock-list-grid').refresh();
            }
        });
    },

    addItem : function() {
        var _this = this;
        var activeRow = Gikam.getComp('emc-mtl-detail-maitain-list-grid').getActivedRow();
        if (activeRow) {
            Gikam.create('modal', {
                title : Gikam.choose.getConfig('testItem').title,
                url : Gikam.choose.getConfig('testItem').url + Gikam.param({
                    target_unassigned : '1',
                    targetTable : 'T_EMC_MTL_MAINTAIN_ITEM',
                    targetItemIdColumn : 'ITEMID',
                    targetFilterColumn : 'MTLMAITAINID',
                    targetFilterValue : activeRow.id,
                }),
                onAfterClose : function(rows) {
                    if (Gikam.isEmpty(rows)) {
                        return;
                    }
                    var chooseData = rows.map(function(item) {
                        return {
                            itemId : item.id,
                            testMethodId : item.testMethodId,
                            methodId : item.ext$.methodid,
                            mtlMaitainId : activeRow.id
                        }
                    });

                    let
                    _itemUrl = Gikam.printf(_this.urlManager['maintainItemBaseInfo'], {
                        mtlMaintainId : activeRow.id
                    });
                    Gikam.getComp('emc-mtl-detail-maintain-item-list-grid').insert(_itemUrl, chooseData);
                    let
                    _gridmUrl = Gikam.printf(_this.urlManager['maintainItemGrid'], {
                        mtlMaintainId : activeRow.id
                    });
                    Gikam.getComp('emc-mtl-detail-maintain-item-list-grid').refresh({
                        url : _gridmUrl
                    });
                }
            });
        } else {
            Gikam.alert("EMC.MODULE.MDM.BASEMDM.MTLS.MAINTAIN.TIP.CHOOSE");
        }
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
                title : 'EMC.MODULE.MDM.BASEMDM.MTLS.TAB.STOCKCONCENT',
                items : [ {
                    type : 'grid',
                    id : 'emc-mtl-detail-stock-concent-list-grid',
                    url : _this.getCompUrl('stockConcentGrid'),
                    columns : emcMtl.getStockConcentListColumns(),
                    service : 'emcStockConcentServiceImpl',
                    deleteFormatter : function(row) {
                        return row.stockId || row.id;
                    },
                    onRendered : function() {
                        _this.stockConcentGrid = this;
                    },
                    onUpdated: function (data, fields, index) {
                        var concentkRow = Gikam.getComp('emc-mtl-detail-stock-concent-list-grid').getActivedRow();
                        if (fields.indexOf('revisionRule') > -1){
                            concentkRow.revisionRule = data.revisionRule;
                        }
                        if (fields.indexOf('uncertainty') > -1||fields.indexOf('uncertaintyPercent') > -1){
                            concentkRow.lowLimit = data.lowLimit;
                            concentkRow.highLimit = data.highLimit;
                        }
                        if (fields.indexOf('uncertainty') > -1 && fields.indexOf('stdValue') > -1 && fields.indexOf('revisionRule') > -1 ) {
                            Gikam.put(emcMtl.baseUrl + '/stockConcent/change-result', Gikam.getJsonWrapper({}, ['', [concentkRow]])).done(function () {
                                Gikam.getComp('emc-mtl-detail-stock-concent-list-grid').refresh();
                            });
                        }
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

    getMaintainItem : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'emc-mtl-detail-maintain-concat-list-grid',
            width : Gikam.width.XL,
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.MTLS.TAB.ITEM',
                items : [ {
                    type : 'grid',
                    checkOnActive : true,
                    id : 'emc-mtl-detail-maintain-item-list-grid',
                    url : _this.getCompUrl('maintainItemGrid'),
                    columns : emcMtl.getItemListColumns(),
                    deleteFormatter : function(row) {
                        return row.ext$.itemname || row.id;
                    },
                    onRendered : function() {
                        _this.itemGrid = this;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.addItem();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            var activeRow = Gikam.getComp('emc-mtl-detail-maitain-list-grid').getActivedRow();
                            let
                            _itemUrl = Gikam.printf(_this.urlManager['maintainItemBaseInfo'], {
                                mtlMaintainId : activeRow.id
                            });
                            Gikam.getComp('emc-mtl-detail-maintain-item-list-grid').deleteRows(_itemUrl).done(function(id) {

                            });
                        }
                    } ]
                }, ]
            } ]
        }
    },

    onMtlActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getAsyncComp('emc-mtl-detail-stock-list-grid').done(function (grid) {
                grid.refresh({
                    url : _this.getCompUrl('stockGrid', row.id),
                    requestData : {
                        status_SEQ : '0',
                        stockQty_NG : 0,
                    },
                });
            })
            Gikam.getComp('emc-mtl-detail-maitain-list-grid').refresh({
                url : _this.getCompUrl('maintainGrid', row.id),
            });
        }
    },

    viewReport : function() {
        var gridData = Gikam.getComp('emc-mtl-edit-list-grid').getSelections();
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
        stockBaseInfo : emcMtl.baseUrl + '/{id}/stocks',
        maintainGrid : emcMtl.baseUrl + '/{id}/maintains/queries',
        maintainBaseInfo : emcMtl.baseUrl + '/{id}/maintains',
        stockLogGrid : emcMtl.baseUrl + '/stocks/{stockId}/logs/queries',
        stockStorage : emcMtl.baseUrl + '/stocks/{stockId}/action/stockin',
        stockInventory : emcMtl.baseUrl + '/{id}/stocks/action/inventory',
        stockAbandon : emcMtl.baseUrl + '/{id}/stocks/action/abandon',
        stockRecieve : emcMtl.baseUrl + '/stocks/{id}/action/recieve',
        stockConcentGrid : emcMtl.baseUrl + '/stocks/{stockId}/concents/queries',
        maintainItemGrid : emcMtl.baseUrl + '/maintains/{mtlMaintainId}/maintain-items/queries',
        maintainItemBaseInfo : emcMtl.baseUrl + '/maintains/{mtlMaintainId}/maintain-items',
    },

    batchInput : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-mtl-detail-stock-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var activeRow = selections[0];
        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
            width : 600,
            height : 600,
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
                                var formData = form.getData();
                                selections.forEach(function (item) {
                                    item.ext$.reagentcategory = formData.ext$.reagentcategory;
                                    item.stockQty = formData.stockQty;
                                    item.unitName = formData.unitName;
                                    item.expireDate = formData.expireDate;
                                    item.ext$.batchnumber = formData.ext$.batchnumber;
                                    item.ext$.standardlevel = formData.ext$.standardlevel;
                                    item.ext$.storagelocation = formData.ext$.storagelocation;
                                    item.ext$.stocktime = formData.ext$.stocktime;
                                    item.ext$.stockstatus = formData.ext$.stockstatus;
                                    item.lowLimit = formData.lowLimit;
                                    item.ext$.stockspecification = formData.ext$.stockspecification;
                                    item.ext$.productiondate = formData.ext$.productiondate;
                                    item.ext$.storagecondition = formData.ext$.storagecondition;
                                    item.ext$.istermofvalidity = formData.ext$.istermofvalidity;
                                    item.ext$.issuittemperature = formData.ext$.issuittemperature;
                                    item.ext$.ispackagingdamage = formData.ext$.ispackagingdamage;
                                    item.ext$.issolutiondeposition = formData.ext$.issolutiondeposition;
                                    item.ext$.other = formData.ext$.other;
                                    item.ext$.checkconclusion = formData.ext$.checkconclusion;
                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/stock/' + activeRow.id, Gikam.getJsonWrapper({}, [ 'pfStockService', selections ])).done(function (data) {
                                    Gikam.getComp('emc-mtl-detail-stock-list-grid').refresh();
                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close();
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
                    }  ]
                }]
            }
        });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            columns: 1,
            titleWidth: 100,
            fields: emcMtl.getBatchInputColumns(),
            autoSave: false
        });

    },

    copyStock : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-mtl-detail-stock-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var activeRow = selections[0];
        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.COPY",
            width : 400,
            height : 250,
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
                                var formData = form.getData();
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/stock/action/copy', Gikam.getJsonWrapper({
                                    uniqueNo : formData.batchNo,
                                    expireDate : formData.expireDate
                                }, [ 'pfStockService', selections ])).done(function (data) {
                                    Gikam.getComp('emc-mtl-detail-stock-list-grid').refresh();
                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close();
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
                    }  ]
                }]
            }
        });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            columns: 1,
            titleWidth: 100,
            fields: [{
                field : 'batchNo',
                title : 'T_EMC_STOCK.BATCHNO',
                validators : [ 'strLength[0,32]', 'notEmpty' ],
            },  {
                field : 'expireDate',
                title : 'T_EMC_STOCK.EXPIREDATE',
                type : 'date',
                validators : [ 'notEmpty' ]
            }],
            autoSave: false
        });
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
                        items : [ _this.getStockLogGridParam(), _this.getMaintainItem() ]
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
