emcConsume.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'Grid',
            id : 'emc-consume-edit-list-grid',
            url : emcConsume.baseUrl + '/queries/raw',
            service : 'emcConsumeServiceImpl',
            columns : emcConsume.getEditListColumns(),
            readonly : !Gikam.isEmpty(_this.param.readonly),
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
            toolbar : !Gikam.isEmpty(_this.param.readonly) ? [] : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.ITEM.CONSUME.CONSUMES.MODAL.CONSUME_ADD',
                        fields : emcConsume.getBaseInfoFormFields(),
                        url : emcConsume.baseUrl,
                        onFormRendered : function() {
                            this.removeFieldValidator( 'productNo' , ['notEmpty'] )
                        }
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
                text : '批录入',
                icon : 'add',
                onClick : function() {
                    _this.batchInput('emc-consume-edit-list-grid','emcConsumeServiceImpl',emcConsume.getEditListColumns().filter(r=>r.editor));
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
                text : '标签',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('consumeTag', Gikam.getComp('emc-consume-edit-list-grid').getSelections());
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    var grid = Gikam.getComp('emc-consume-edit-list-grid');
                    Gikam.viewAudit('T_EMC_CONSUME', grid.getSelections());
                }
            },{
                type : 'dropDownMenu',
                text : 'EMC.MODULE.BP.RESULT.CURVES.BUTTON.QUICK_INPUT',
                items : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.EXPORT',
                    icon : 'move-up',
                    onClick : function() {
                        _this.exportFile();
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.IMPORT',
                    icon : 'move-down',
                    onClick : function() {
                        _this.importFile();
                    }
                } ]
            },]
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
                    readonly : !Gikam.isEmpty(_this.param.readonly),
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
                    toolbar : !Gikam.isEmpty(_this.param.readonly) ? [_this.showAllBtn()] : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var activeRow = Gikam.getComp('emc-consume-edit-list-grid').getActivedRow();

                            Gikam.preInsert({
                                modalTitle : 'EMC.MODULE.ITEM.CONSUME.CONSUMES.MODAL.STOCK_ADD',
                                fields : emcConsume.getStockBaseInfoFormFields(),
                                url : _this.getCompUrl('stockBaseInfo', activeRow.id),
                                onFormRendered: function(){
                                    this.removeFieldValidator('batchNo', ['notEmpty']);
                                    this.removeFieldValidator('price', ['notEmpty']);
                                    this.removeFieldValidator('vendorId', ['notEmpty']);
                                    this.setData({
                                        vendorId: activeRow.ext$.vendorid
                                    });
                                }
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
                    },{
                        type : 'button',
                        text : '批录入',
                        icon : 'add',
                        onClick : function() {
                            _this.batchInput('emc-consume-detail-stock-list-grid','emcCsmStockServiceImpl',emcConsume.getStockListColumns().filter(r=>r.editor));
                        }
                    }, _this.showAllBtn()]
                } ]
            } ]
        }
    },
    showAllBtn  : function() {
        var _this = this;
        return {
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
                    Gikam.getComp('emc-consume-detail-stock-list-grid').refresh({
                        requestData : {
                            status_SEQ :  value === '1' ? null : '0',
                            stockQty_NG :  value === '1' ? null : 0,
                        },
                    });
                },
            } ]
        };
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
                    readonly : !Gikam.isEmpty(_this.param.readonly),
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
                    readonly : !Gikam.isEmpty(_this.param.readonly),
                    dbTable : 'T_EMC_CSM_STOCK',
                    bizId : '',
                    onBeforeUpload : function() {
                        this.setUploadData({})
                    }
                } ]
            } ]
        }
    },
    getQrCodeParam: function(){
        let _this = this;
        return {
            type: 'grid',
            id: 'emc-consume-qrcode-grid',
            generalButtonGroupShow: false,
            page: false,
            onRendered: function(){
                this.hideBody();
                this.appendContent(`
                    <div id="qrcode_print" style="text-align: center;"><div id="qrcode" style="width:128px; height:128px; margin-top:20px;margin-left: auto;margin-right: auto;"></div><div id="mtlInfo" style="margin-top:10px;"></div></div>`);
                Gikam.jQuery('#emc-consume-qrcode-grid .grid-toolbar-total-record').hide();
            },
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.PRINT',
                icon: 'print',
                onClick: function () {
                    _this.printQrcode();
                }
            }]

        }
    },
    onCsmActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getAsyncComp('emc-consume-detail-stock-list-grid').done(function(grid){
                grid.refresh({
                    url : _this.getCompUrl('stockGrid', row.id),
                    requestData : {
                    status_SEQ : '0',
                        stockQty_NG : 0,
                    },
                });
            });
            Gikam.getAsyncComp('emc-consume-qrcode-grid').done(function(){
                let qrcodeText = JSON.stringify({
                    "名称": row.mtlName,
                    "规格": row.specification,
                    "编码": row.id
                });
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
                Gikam.jQuery('#mtlInfo').html(`<p>${row.mtlName}</p><p>${row.id}</p>`);
            });
        }
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

    batchInput : function(gridId,service,columns) {
        var _this = this;
        var selections = Gikam.getComp(gridId).getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var activeRow = selections[0];
        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
            width : 500,
            height : 350,
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
                                var chooseData = selections.map(function (item) {
                                    let newJson = JSON.parse(JSON.stringify(formData));
                                    newJson.id = item.id;
                                    return newJson;
                                });
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(IFM_CONTEXT + '/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ service, chooseData ])).done(function (data) {
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
            fields: columns,
            autoSave: false,

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

    exportFile : function() {
        Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}', {
            templateCode : 'emcConsumeExportTemplate',
            service : 'emcConsumeExportTemplate'
        })).done(function(fileUri) {
            var fileUrl = IFM_CONTEXT + fileUri;
            Gikam.emcDownloadFile(fileUrl);
        });
    },

    importFile : function() {
        Gikam.importExcel({
            url : IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'emcConsumeImportFileFunction',
            dbTable : 'T_EMC_CONSUME',
            onImportSuccess : function(files) {
                Gikam.getComp('emc-consume-edit-list-grid').refresh();
            }
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
