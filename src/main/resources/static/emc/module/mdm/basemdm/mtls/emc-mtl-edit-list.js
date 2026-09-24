emcMtl.editPage = {
    getGridParam : function() {
        var _this = this;
        let reagentCategoryJson = {};
        Gikam.postSync(IFM_CONTEXT +"/secure/core/module/sys/codes/queries",Gikam.getJsonWrapper({f:{codeCategoryId_SEQ:'reagentCategory'}})).done(function(page){
            page.rows.forEach(function(r){
                reagentCategoryJson[r.code] = r.codeName;
            });
        });
        var requestData = _this.param;
        requestData['managementType_SEQ'] = 'stdMaterials';
        requestData.org_filter = '1';
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
            onBeforeLoad: function(data){
              data.forEach(function(r){
                  if(reagentCategoryJson[r.ext$.reagentcategory])
                    r.ext$.reagentcategory = reagentCategoryJson[r.ext$.reagentcategory ];
              })
            },
            readonly : !Gikam.isEmpty(_this.param.readonly),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData(['emc-mtl-detail-stock-list-grid','emc-mtl-detail-maitain-list-grid','emc-mtl-detail-stock-log-list-grid','emc-mtl-detail-stock-concent-list-grid']);
                }
            },
            onRowActive : function(index, row) {
                _this.onMtlActive(row);
            },
            toolbar : !Gikam.isEmpty(_this.param.readonly) ? [] : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.BASEMDM.MTLS.MODAL.MTL_ADD',
                        fields : emcMtl.getBaseInfoFormFields(),
                        url : emcMtl.baseUrl,
                        onFormRendered: function(){
                            this.removeFieldValidator('mtlNo',['notEmpty']);
                        }
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
                text : 'EMC.MODULE.MDM.BASEMDM.MTLS.STOCK.BUTTON.RECIEVE',
                icon : 'select',
                onClick : function() {
                    _this.mtlReceiveStock();
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
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    _this.viewStockRecord();
                 }
            }, {
                type : 'button',
                text : '标签',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('mtlTag', Gikam.getComp('emc-mtl-edit-list-grid').getSelections());
                }
            }, {
                type : 'dropDownMenu',
                text : 'EMC.MODULE.MDM.BASEMDM.MTLS.BUTTON.QUICK_INPUT',
                items : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.EXCEL_EXPORT',
                    icon : 'edit',
                    onClick : function() {
                        Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}', {
                            templateCode : 'emcMtlExportTemplate',
                            service : 'emcMtlExportTemplate'
                        })).done(function(fileUri) {
                            var fileUrl = IFM_CONTEXT + fileUri;
                            Gikam.emcDownloadFile(fileUrl);
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
                }, {
                    type : 'button',
                    text : '初始化模板',
                    icon : 'edit',
                    onClick : function() {
                        Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}', {
                            templateCode : 'emcMtlInitExportTemplate',
                            service : 'emcMtlInitExportTemplate'
                        })).done(function(fileUri) {
                            var fileUrl = IFM_CONTEXT + fileUri;
                            Gikam.emcDownloadFile(fileUrl);
                        });
                    }
                },  {
                    type : 'button',
                    text : '初始化导入',
                    icon : 'move-down',
                    onClick : function() {
                        _this.initImport();
                    }
                }, {
                    type : 'button',
                    text : '数据导出',
                    icon : 'export',
                    onClick : function() {
                        var selections = Gikam.getComp('emc-mtl-edit-list-grid').getSelections();
                        if(Gikam.isEmpty(selections)){
                            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                            return;
                        }
                        let data = selections.map(function(r){return {id: r.id}});
                        workspace.window.showMask(true);
                        Gikam.postText(emcMtl.baseUrl+"/export-file",Gikam.getJsonWrapper({} ,['',data])).done(function(fileUrl) {
                            Gikam.emcDownloadFile((IFM_CONTEXT + fileUrl));
                        }).always(function () {
                            workspace.window.closeMask();
                        });
                    }
                }]
            }]
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
                    readonly : !Gikam.isEmpty(_this.param.readonly),
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
                            Gikam.cleanCompData(['emc-mtl-detail-stock-log-list-grid','emc-mtl-detail-stock-concent-list-grid']);
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
                    toolbar : !Gikam.isEmpty(_this.param.readonly) ? [_this.showAllBtn()] : _this.getStockToolbar()
                } ]
            }, {
                title : 'EMC.MODULE.MDM.BASEMDM.MTLS.TAB.MTLMAITAIN',
                items : [ {
                    type : 'grid',
                    id : 'emc-mtl-detail-maitain-list-grid',
                    url : _this.getCompUrl('maintainGrid'),
                    columns : emcMtl.getMaintainListColumns(),
                    readonly : !Gikam.isEmpty(_this.param.readonly),
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
                            Gikam.cleanCompData(['emc-mtl-detail-maintain-item-list-grid']);
                        }
                    },
                    toolbar :!Gikam.isEmpty(_this.param.readonly) ? [] :  [ {
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
        }, {
            type : 'button',
            text : '批录入',
            icon : 'edit',
            onClick : function() {
                _this.batchInput('emc-mtl-detail-stock-list-grid');
            }
        }, {
            type : 'button',
            text : '批量修改浓度',
            icon : 'edit',
            onClick : function() {
                _this.batchEditConcent('emc-mtl-detail-stock-list-grid');
            }
        }, _this.showAllBtn(), {
            type : 'button',
            text : 'GIKAM.BUTTON.COPY',
            icon : 'copy',
            onClick : function() {
                _this.copyStock();
            }
        }, {
            type : 'button',
            text: '打印条形码',
            icon: 'print',
            onClick : function() {
                _this.printStockBarCodeModal();
            }
        }, {
            type : 'button',
            text: '扫码入库',
            icon: 'edit',
            onClick : function() {
                _this.scanCodeInstock();
            }
        }, {
            type : 'button',
            text : '批量上传附件',
            icon : 'edit',
            onClick : function() {
                _this.batchUpload();
            }
        },]
    },
    showAllBtn : function(){
        let _this = this;
        return  {
            type : 'form',
            autoSave : false,
            fields : [ {
                title : '显示全部',
                field : 'showAll',
                type : 'simpleCheckbox',
                refreshGrid : false,
                styleFormatter : function() {
                    return {
                        paddingLeft : '0'
                    }
                },
                onChange : function(field, value, index) {
                    Gikam.getComp('emc-mtl-detail-stock-list-grid').refresh({
                        requestData : {
                            status_SEQ :  value === '1' ? null : '0',
                            stockQty_NG :  value === '1' ? null : 0,
                        },
                    });
                },
            } ]
        };
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
                this.removeFieldValidator( 'ext$.productionno' , ['notEmpty'] );
                this.removeFieldValidator( 'ext$.standardlevel' , ['notEmpty'] );
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
        if (selections.length > 0) {
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
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
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
            Gikam.emcDownloadFile(IFM_CONTEXT + fileUri);
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

    initImport: function() {
        Gikam.importExcel({
            url : IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'emcStockInitImportFileFunction',
            dbTable : 'T_EMC_MTL',
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
                    readonly : !Gikam.isEmpty(_this.param.readonly),
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
                    readonly : !Gikam.isEmpty(_this.param.readonly),
                    service : 'emcStockConcentServiceImpl',
                    deleteFormatter : function(row) {
                        return row.stockId || row.id;
                    },
                    onRendered : function() {
                        _this.stockConcentGrid = this;
                    },
                    onUpdated: function (data, fields) {
                        if (fields.indexOf('stdValue') !== -1 || fields.indexOf('uncertainty') !== -1 || fields.indexOf('uncertaintyPercent') || fields.indexOf('revisionRule') !== -1) {
                            var concentkRow = Gikam.getComp('emc-mtl-detail-stock-concent-list-grid').getActivedRow();
                            Gikam.put(emcMtl.baseUrl + '/stockConcent/calculate', Gikam.getJsonWrapper({}, ['emcStockConcentServiceImpl', [concentkRow]])).done(function () {
                                Gikam.getComp('emc-mtl-detail-stock-concent-list-grid').refreshRowById(concentkRow.id);
                            });
                        }
                    },
                }, ]
            }, {
                title : 'EMC.MODULE.MDM.BASEMDM.MTLS.TAB.FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-mtl-detail-stock-uploader',
                    readonly : !Gikam.isEmpty(_this.param.readonly),
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
                    readonly : !Gikam.isEmpty(_this.param.readonly),
                    deleteFormatter : function(row) {
                        return row.ext$.itemname || row.id;
                    },
                    onRendered : function() {
                        _this.itemGrid = this;
                    },
                    toolbar : !Gikam.isEmpty(_this.param.readonly) ? [] : [ {
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
                        remind : _this.param.remind
                    },
                });
            })
            Gikam.getComp('emc-mtl-detail-maitain-list-grid').refresh({
                url : _this.getCompUrl('maintainGrid', row.id),
            });
        }
        _this.refreshQrcode(row);
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
    viewStockRecord : function() {
        Gikam.viewReport('mtlStockRecord', Gikam.getComp('emc-mtl-edit-list-grid').getSelections());
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
    batchInput : function(gridId) {
        var _this = this;
        var selections = Gikam.getComp(gridId).getSelections();
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
                                    Gikam.getComp(gridId).refresh();
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
    batchEditConcent : function(gridId) {
        var _this = this;
        var selections = Gikam.getComp(gridId).getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var activeRow = selections[0];
        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
            width : 600,
            height : 500,
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
                                Gikam.post(emcMtl.baseUrl + '/stocks/concents/queries',Gikam.getJsonWrapper({f:{
                                    stockId_IN : selections.map(r=>r.id).join(',')
                                    }})).done(function(page){
                                        let updateData = page.rows.map(function(r){
                                            let data = JSON.parse(JSON.stringify(formData));
                                            data.id = r.id;
                                            return data;
                                        });
                                        Gikam.getLastModal().window.showMask(true);
                                        Gikam.put(IFM_CONTEXT + '/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcStockConcentServiceImpl', updateData ])).always(function () {
                                            if (formData.stdValue || formData.uncertainty || formData.uncertaintyPercent || formData.revisionRule) {
                                                page.rows.forEach(function(r){
                                                    for(var key in formData){
                                                        r[key] = formData[key];
                                                    }
                                                });
                                                Gikam.put(emcMtl.baseUrl + '/stockConcent/calculate', Gikam.getJsonWrapper({}, ['emcStockConcentServiceImpl',  page.rows])).always(function () {
                                                    Gikam.getLastModal().window.closeMask();
                                                    modal.close();
                                                    Gikam.getAsyncComp('emc-mtl-detail-stock-concent-list-grid').done(function(grid){grid.refresh()});
                                                });
                                            }else{
                                                Gikam.getLastModal().window.closeMask();
                                                modal.close();
                                                Gikam.getAsyncComp('emc-mtl-detail-stock-concent-list-grid').done(function(grid){grid.refresh()});
                                            }
                                        });
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
            fields: emcMtl.getBatchUpdateStockConcentFields(),
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
    printStockBarCodeModal : function(){
        var _this = this;
        var selections = Gikam.getComp('emc-mtl-detail-stock-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var modal = Gikam.create('modal', {
            title: "条形码设置",
            width : '80%',
            height : "90%",
        });
        Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [{
                    type: 'grid',
                    id: 'emc-mtl-bar-code-grid',
                    generalButtonGroupShow: false,
                    page: false,
                    onRendered: function(){
                    this.hideBody();
                    let canvases = selections.map(function(r){
                        return `<div style="margin: 0 auto 12px; text-align: center;">
                                    <svg id="${r.id}"></svg>
                                    <div id="barcode_text_${r.id}" style="margin-top: 4px;"></div>
                                </div>`;
                    }).join('');

                    this.appendContent(`<div id="barcode_print" style="text-align: center;">${canvases}</div>`);
                    Gikam.jQuery('#emc-mtl-bar-code-grid .grid-toolbar-total-record').hide();
                    setTimeout(function() {
                        _this.refreshBarCode(selections);
                    },500);
                },
                toolbar: [{
                    type: 'form',
                    id: 'bar-code-form',
                    fields : [{
                        field : 'barWidth',
                        title : '间隔',
                        type: 'number',
                        value: 1,
                        refreshGrid : false,
                    },{
                        field : 'barHeight',
                        title : '高度',
                        type: 'number',
                        value: 50,
                        refreshGrid : false,
                    },{
                        field : 'fontSize',
                        title : '字体大小',
                        type: 'number',
                        value: 12,
                        refreshGrid : false,
                    }]
                },{
                    type: 'button',
                    text: '刷新',
                    icon: 'refresh',
                    onClick: function () {
                        let formData = Gikam.getComp('bar-code-form').getData();
                        _this.refreshBarCode(selections,formData.barWidth,formData.barHeight,formData.fontSize);
                    }
                },{
                    type: 'button',
                    text: 'GIKAM.BUTTON.PRINT',
                    icon: 'print',
                    onClick: function () {
                         _this.printBarCode();
                    }
                }]
            }]
            }
        });
    },
    refreshBarCode : function(selections,width,height,fontSize){
        const _this = this;
        width = width == undefined ? 1 : width;
        height = height == undefined ? 50 : height;
        fontSize = fontSize == undefined ? 12 : fontSize;
        let options = {
            //     format: "CODE128",//选择要使用的条形码类型
                width: width,//3,设置条之间的宽度
                height:height,//100,高度
            //     displayValue:true,//是否在条形码下方显示文字
            //     text:"123456",//覆盖显示的文本
            //     fontOptions:"bold italic",//使文字加粗体或变斜体
            //     font:"fantasy",//设置文本的字体
            //     textAlign:"left",//设置文本的水平对齐方式
            //     textPosition:"top",//设置文本的垂直位置
            //     textMargin:5,//设置条形码和文本之间的间距
            fontSize: fontSize//12,设置文本的大小
            // background: "#cccccc",//设置条形码的背景
            //     lineColor:"#2196f3",//设置条和文本的颜色。
            //     margin:15,//设置条形码周围的空白边距
        };

        selections.forEach(function(r){
            JsBarcode(document.getElementById(r.id), r.batchNo,options );
            Gikam.jQuery('#barcode_text_' + r.id).html(
                `<div class="barcode-extra-text" style="font-size:${Math.max(fontSize - 2, 10)}px; line-height: 1.2; margin-top: 2px;">${_this.getBarCodeDisplayText(r)}</div>`
            );
        });

    },
    getBarCodeDisplayText : function(row) {
        const texts = [];
        const productionDate = this.formatBarCodeDate(row.ext$ && row.ext$.productiondate);
        const expireDate = this.formatBarCodeDate(row.expireDate);
        if (productionDate && expireDate) {
            texts.push(`<div>${productionDate} 至 ${expireDate}</div>`);
        } else if (productionDate) {
            texts.push(`<div>${productionDate}</div>`);
        } else if (expireDate) {
            texts.push(`<div>${expireDate}</div>`);
        }
        return texts.join('');
    },
    formatBarCodeDate : function(value) {
        if (Gikam.isEmpty(value)) {
            return '';
        }
        const text = String(value);
        return text.length >= 10 ? text.substring(0, 10) : text;
    },
    printBarCode : function(){
        const barCodeDiv = document.querySelector('#barcode_print').innerHTML;
        const printWindow = window.open('', '_blank');

        printWindow.document.write('<html><head><title>打印条码</title></head><body><div style="text-align: center;">');
        printWindow.document.write(barCodeDiv);
        printWindow.document.write('</div></body></html>');
        printWindow.document.close();
        printWindow.print();
        printWindow.close();
    },

    scanCodeInstock : function(){
        let _this = this;
        let mtl = Gikam.getComp('emc-mtl-edit-list-grid').getActivedRow();
        var modal = Gikam.create('modal', {
            title: "扫码入库",
            width : '50%',
            height : "80%",
            onAfterClose : function() {
                Gikam.getComp('emc-mtl-detail-stock-list-grid').refresh();
            }
        });
        Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [{
                    type: 'grid',
                    id: 'emc-mtl-scan-code-in-stock-grid',
                    page: false,
                    columns: [ {
                        field : 'batchNo',
                        title : 'T_EMC_STOCK.BATCHNO',
                        width : 200
                    }],
                    url : _this.getCompUrl('stockGrid', mtl.id),
                    requestData : {
                        id_IN: '0'
                    },
                    onBeforeRefresh : function(requestData) {
                        if(requestData.scanlabelcode){
                            debugger
                            _this.inStockByScanLabelCode(requestData.scanlabelcode);
                            delete requestData.scanlabelcode;
                            return false;
                        }
                    },
                    onLoadSuccess:function(){
                        Gikam.jQuery(document).keydown(function (event) {
                            setTimeout(function(){
                                _this.checkScanCode('');
                            },500);
                        });
                    },
                    toolbar: [{
                        type: 'form',
                        id: 'scan_label_code_form',
                        fields : [{
                            field : 'scanlabelcode',
                            title : '条形码',
                            inputWidth : 250,
                            type: 'password',
                            //validators : [ 'remote[' + emcMtl.baseUrl + '/stocks/{mtl.id}/action/validate-unique{scanlabelcode}]', 'strLength[0,32]' ],
                            onFocus : function(){
                                //解决扫码枪输入input时受中文输入法的影响
                               // Gikam.jQuery('#scan_label_code_form input[name="scanlabelcode"]').eq(0).attr("type",'password');
                            }
                        }],
                    },{
                        type: 'caption',
                        text: `${mtl.mtlName}【${mtl.mtlNo}】`,
                        color: 'red'
                    }]
                }]
            }
        });
    },
    //延时监听扫码输入框
    checkScanCode : function (code){
        //debugger
        let _this = this;
        if(code == '' || code == undefined){
            code = Gikam.jQuery("#scan_label_code_form input[name='scanlabelcode']").eq(0).val();
            if(code == '' || code == undefined){
                return;
            }
        }
        setTimeout(function(){
            var code2 = Gikam.jQuery("#scan_label_code_form input[name='scanlabelcode']").eq(0).val();
            code2 = code2.trim();
            if(code == code2){
                _this.inStockByScanLabelCode(code);
            }else{
                _this.checkScanCode(code2);
            }
        },500);
    },
    inStockByScanLabelCode : function(value) {
        // debugger
        let _this = this;
        if (Gikam.isEmpty(value)) {
            return;
        }
        Gikam.getComp('scan_label_code_form').setData({scanlabelcode: ''}, false);
        setTimeout(function () {//重置后input 元素变更需要延时抓取
            Gikam.jQuery("#scan_label_code_form input[name='scanlabelcode']").eq(0).attr("placeholder", value);
            Gikam.jQuery("#scan_label_code_form input[name='scanlabelcode']").eq(0).focus();
        }, 100);
        let mtl = Gikam.getComp('emc-mtl-edit-list-grid').getActivedRow();
        let url = _this.getCompUrl('stockBaseInfo', mtl.id);
        workspace.window.showMask(true);
        Gikam.postText(url, Gikam.getJsonWrapper({}, ['', [{
            mtlId: mtl.id,
            batchNo: value,
            status : '0',
            stockQty : 1,
            qty : 1
        }]])).done(function (id) {
            let grid = Gikam.getComp('emc-mtl-scan-code-in-stock-grid');
           let requestData = grid.options.requestData;
           let ids = requestData == undefined ? id : (requestData.id_IN +','+id);
            grid.refresh({
                requestData:{
                    id_IN : ids
                }
            })
        }).always(function () {
            workspace.window.closeMask();
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
    batchUpload : function(){
        let grid = Gikam.getComp('emc-mtl-detail-stock-list-grid');
        var selections = grid.getSelections();
        if(Gikam.isEmpty(selections)){
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('simpleUploader', {
            bizId: selections[0].id,
            dbTable: 'T_EMC_STOCK',
            onAfterClose : function(fileList) {
                if(selections.length>1){
                    var fileIds = fileList.map(r=>r.id).join(',')
                    var ids = [];
                    selections.forEach(item => {
                        if (item.id != selections[0].id) {
                            ids.push(item.id)
                        }
                    });
                    Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/result/runs/batch-upload-file', Gikam.getJsonWrapper({
                        ids: ids.join(","),
                        fileIds : fileIds,
                        dbTable: 'T_EMC_STOCK'
                    }, ['', []])).always(function(){
                        Gikam.getComp('emc-mtl-detail-stock-uploader').grid.refresh();
                    });
                }
            }
        });
    },
    getQrCodeParam: function(){
        let _this = this;
        return {
            type: 'grid',
            id: 'emc-mtl-qrcode-grid',
            generalButtonGroupShow: false,
            page: false,
            readonly : !Gikam.isEmpty(_this.param.readonly),
            onRendered: function(){
                this.hideBody();
                this.appendContent(`
                    <div id="qrcode_print" style="text-align: center;"><div id="qrcode" style="width:128px; height:128px; margin-top:20px;margin-left: auto;margin-right: auto;"></div><div id="mtlInfo" style="margin-top:10px;"></div></div>`);
                Gikam.jQuery('#emc-mtl-qrcode-grid .grid-toolbar-total-record').hide();
            },
            toolbar: !Gikam.isEmpty(_this.param.readonly) ? [] : [{
                type: 'button',
                text: 'GIKAM.BUTTON.PRINT',
                icon: 'print',
                onClick: function () {
                    _this.printQrcode();
                }
            }]

        }
    },


    refreshQrcode : function(row) {
        var _this = this;
        if(_this.qrcode == undefined){
            let mtlCategoryJosn = {};
            let mtlCategoryList = Gikam.select.getItems({
                category : 'mtlCategory',
                param : {
                    managementType_SEQ : 'stdMaterials'
                },
            });
            mtlCategoryList.forEach(function(r){
                mtlCategoryJosn[r.value] = r.text;
            });
            _this.mtlCategoryJosn = mtlCategoryJosn;
        }
        Gikam.getAsyncComp('emc-mtl-qrcode-grid').done(function(){
            // let qrcodeText = JSON.stringify({
            //     "名称": row.mtlName,
            //     "类型": _this.mtlCategoryJosn[row.mtlCategoryId],
            //     "编码": row.mtlNo
            // });
            let mtlInfo = [
                `名称: ${row.mtlName}`,
                `类型: ${_this.mtlCategoryJosn[row.mtlCategoryId]}`,
                `编码: ${row.mtlNo}`
            ];
            let qrcodeText = mtlInfo.join("\n")
            if(_this.qrcode == undefined){
                _this.qrcode = new QRCode(document.getElementById("qrcode"),{
                    text: qrcodeText,
                    width: 128,
                    height: 128,
                    colorDark : "#000000",
                    colorLight : "#ffffff",
                    correctLevel : QRCode.CorrectLevel.H
                });
            }else{
                _this.qrcode.makeCode(qrcodeText);
            }
            Gikam.jQuery('#mtlInfo').html(`<p>${row.mtlName}</p><p>${row.mtlNo}</p>`);
        });
    },

    printQrcode: function(){
        var modal = Gikam.create('modal', {
            title: "二维码打印",
            width: 400,
            height: 200,
        });
        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [{
                    type: 'btnToolbar',
                    items: [{
                        type: 'button',
                        text: 'GIKAM.BUTTON.CONFIRM',
                        icon: 'select',
                        onClick: function () {
                            if (form.validate()) {
                                let num = form.getData().num;
                                const qrcodeDiv = Gikam.jQuery('#qrcode_print').html();
                                const printWindow = window.open('', '_blank');

                                printWindow.document.write('<html><head><title>打印二维码</title></head><body><div style="text-align: center;">');
                                for(let i=0;i<num;i++){
                                    printWindow.document.write(qrcodeDiv);
                                }

                                printWindow.document.write('</div></body></html>');

                                printWindow.document.close();
                                setTimeout(function(){
                                    printWindow.print();
                                    printWindow.close();
                                },500);
                            }
                        }
                    }, {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CANCEL',
                        icon: 'cancel',
                        onClick: function () {
                            modal.close();
                        }
                    }]
                }]
            }
        });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            columns: 1,
            titleWidth: 80,
            fields: [{
                field: 'num',
                title: '打印数量',
                type : 'number',
                editor: true,
                value: 1,
                validators: ['notEmpty', 'integer'],
            }],
            autoSave: false
        });
    },
    getMtlManageParam : function() {
        var _this = this;
        return {
            type : 'layout',
            west : {
                width : '30%',
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    west : {
                        height : '50%',
                        width: '80%',
                        items : [ _this.getStockGridParam() ]
                    },
                    center : {
                        height : '50%',
                        items : [ _this.getQrCodeParam() ]
                    },
                    south : {
                        height : '50%',
                        items : [ _this.getStockLogGridParam(), _this.getMaintainItem() ]
                    }
                } ]
            }
        };
    },
    getInStockParam : function() {
        let _this = this;
        return {
            type: 'grid',
            id: 'emc-mtl-in-stock-list-grid',
            url: _this.getCompUrl('stockGrid'),
            columns: emcMtl.getStockListColumns(),
            checkOnActive: true,
            service: 'emcStockServiceImpl',
            group: {
                fields: ['ext$.standardlevel'],
                formatter: function (row) {
                    return row.ext$.standardlevel || '-';
                }
            },
            deleteFormatter: function (row) {
                return row.batchNo || row.id;
            },
            toolbar : [{
                type: 'form',
                fields: [{
                    field: 'mtlname',
                    title: 'T_EMC_MTL.MTLNAME',
                    validators: ['strLength[0, 18]'],
                    preInsert: true,
                    type : 'choose',
                    category : 'mtl',
                    single : true,
                    cleanable : false,
                    onAfterChoose : function(rows) {
                        if (rows) {
                            this.setData({
                                mtlId : rows[0].id,
                                mtlName : rows[0].mtlName
                            });
                        }
                    }
                },]
            },{
                type : 'button',
                text: 'GIKAM.BUTTON.PRINT',
                icon: 'print',
                onClick : function() {
                    _this.printBarCode();
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
                text : '批录入',
                icon : 'add',
                onClick : function() {
                    _this.batchInput('emc-mtl-in-stock-list-grid');
                }
            }]
        }
    },

    getOutStockParam : function(){
        let _this = this;
        return {

        }
    },
    mtlReceiveStock : function() {
        var _this = this;
        Gikam.create('modal', {
            width: 400,
            height: 400,
            title: 'EMC.MODULE.MDM.BASEMDM.MTLS.MODAL.STOCK_RECIEVE',
            onAfterClose: function (data) {
                if(data){
                    Gikam.getComp('emc-mtl-edit-list-grid').refreshRowById(data.mtlId);
                    Gikam.getComp('emc-mtl-detail-stock-list-grid').refresh();
                }
            }
        });
        emcMtl.mtlRecievePage.init();
    },
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [ {
                    type: 'tab',
                    panels : [{
                        title: '材料管理',
                        items: [_this.getMtlManageParam()]
                    },/*{
                        title: '扫码入库',
                        items: [_this.getInStockParam()]
                    },{
                        title: '扫码出库',
                        items: [_this.getOutStockParam()]
                    }*/]
                } ]
            }
        });
    },
    init : function(param) {
        this.param = param;
        this.create();
    }
};
