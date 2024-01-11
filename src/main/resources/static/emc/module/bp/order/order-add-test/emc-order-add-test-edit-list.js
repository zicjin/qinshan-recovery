emcOrderReceipt.editAddTestPage = {

    getSampleTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TAB.SAMPLE_NAME',
                items : [ {
                    type : 'grid',
                    id : 'emc-order-add-test-edit-list-sample-grid',
                    service : 'emcSampleServiceImpl',
                    url : '/secure/emc/module/bp/sample/samples/queries',
                    page : true,
                    pageSize : 50,
                    requestData : {
                        status_SEQ : 'done',
                        targetType : 'sampleAddTest',
                        orgId_SB: workspace.user.ext$.parentorgid
                    },
                    order : {
                        'PROJID' : 'asc',
                    },
                    columns : emcOrderReceipt.getAddTestListColumns(),
                    checkOnActive : true,
                    group : {
                        fields : [ 'projNo' ],
                        formatter : function(row) {
                            return row.projNo;
                        }
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            //this.activeRowByIndex(1);
                            for (var row of data) {
                                if (row.$rowStyle !== 'group') {
                                    if (Gikam.isEmpty(row.sampleCheck) || row.sampleCheck === undefined) {
                                        this.setData({
                                            id : row.id,
                                            sampleCheck : 'yes',
                                            usedContainer : 'yes',
                                            storageValid : 'yes',
                                            exchangeValid : 'yes',
                                            samplingValid : 'yes',
                                            markValid : 'yes',
                                            containerValid : 'yes'
                                        });
                                    }
                                }
                            }
                        } else {
                            Gikam.cleanCompData([ 'emc-order-add-test-edit-list-order-grid', 'emc-order-receipt-edit-list-container-grid' ]);
                        }
                    },
                    deleteFormatter : function(row) {
                        return row.fileNo || row.id;
                    },
                    onUpdated : function(data) {
                    	var activeRow = Gikam.getComp('emc-order-add-test-edit-list-sample-grid').getActivedRow();
                        Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/action/sync-assign-date', Gikam.getJsonWrapper({},  [ 'pfSampleServiceImpl', [activeRow]])).done(function() {
                        }).always(function () {
                            Gikam.getComp('emc-order-add-test-edit-list-sample-grid').refresh();
                            Gikam.getComp('emc-order-add-test-edit-list-order-grid').refresh();
                            Gikam.getComp('emc-order-add-test-edit-list-test-grid').refresh();

                        });

                    },
                    onRowActive : function(index, row) {
                        if (row) {
                            // 对于605送样任务，隐藏同步容器信息按钮
                            if (row.ext$.bizcategorycode === '605') {
                                Gikam.getComp('emc-order-receipt-sync-container-btn').hide();
                            } else {
                                Gikam.getComp('emc-order-receipt-sync-container-btn').show();
                            }

                            Gikam.getAsyncComp('emc-order-add-test-edit-list-order-grid').done(function (grid) {
                                grid.refresh({
                                    requestData : {
                                        sampleId_SEQ : row.id,
                                        termination_DIFFER : '1'
                                    },
                                    url : _this.urlManager['orderGrid']
                                })
                            });
                        }
                    },
                    toolbar : [   {
                        type : 'button',
                        text : 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.BUTTON.MATCHBARCODE',
                        icon : 'add',
                        onClick : function() {
                            _this.matchBarcode();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.PRINT',
                        icon : 'print',
                        hidden : true,
                        onClick : function() {
                            _this.viewReport();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.LABEL_PRINT',
                        icon : 'print',
                        onClick : function() {
                            Gikam.viewReport('deliveringLabel', Gikam.getComp('emc-order-add-test-edit-list-sample-grid').getSelections());
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELIVERY.PREVIEW',
                        icon : 'print',
                        onClick : function() {
                            var selections = Gikam.getComp('emc-order-add-test-edit-list-sample-grid').getSelections();
                            if (selections.length !== 1) {
                                Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                return;
                            }
                            Gikam.viewReport(selections[0].receiveReportLineId, selections[0]);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.BATCH_INPUT',
                        icon : 'add',
                        onClick : function() {
                            _this.batchInput();
                        }
                    }, {
                        type : 'button',
                        text : '加测确认',
                        icon : 'add',
                        onClick : function() {
                            _this.confirmAddTest();
                        }
                    }  ]
                } ]
            } ]
        }
    },

    // 批录入
    batchInput : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-order-add-test-edit-list-sample-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

/*        var activeRow = Gikam.getComp('emc-order-add-test-edit-list-sample-grid').getActivedRow();
*/        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
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
                                var formData = form.getData();
                                selections.forEach(function (item) {
                                    item.sepSampleTime = formData.sepSampleTime;
                                    item.qcUserName = formData.qcUserName;
                                    item.qcUserId = formData.qcUserId;
                                    item.sampleCheck = formData.sampleCheck;
                                    item.usedContainer = formData.sampleCheck;
                                    item.storageValid = formData.sampleCheck;
                                    item.exchangeValid = formData.sampleCheck;
                                    item.samplingValid = formData.sampleCheck;
                                    item.markValid = formData.sampleCheck;
                                    item.containerValid = formData.sampleCheck;
                                    item.printFlag = formData.printFlag;
                                    item.acceptedUserId = formData.acceptedUserId;
                                    item.accepteUserName = formData.accepteUserName;
                                    item.acceptedDate = formData.acceptedDate;
                                    item.ext$.compeletedate = formData.ext$.compeletedate;
   
                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/' + selections[0].id, Gikam.getJsonWrapper({}, [ 'emcSampleServiceImpl', selections ])).done(function (data) {
                                    Gikam.getComp('emc-order-add-test-edit-list-sample-grid').refresh();
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
                    } ]
                }]
            }
        });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            columns: 1,
            titleWidth: 80,
            fields: emcOrderReceipt.getBatchInputFields(),
            autoSave: false
        });

    },

    getOrderTab : function() {
        var _this = this;
        return {
            id : 'emc-order-receipt-edit-list-order-tab',
            type : 'tab',
            width : Gikam.width.S,
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TAB.ORDER',
                items : [ {
                    type : 'Grid',
                    id : 'emc-order-add-test-edit-list-order-grid',
                    url : '',
                    columns : emcOrderReceipt.getOrderListColumns(),
                    service : 'PfOrderServiceImpl',
                    checkOnActive : true,
                    order : {
                        'qcName' : 'desc',
                        'id' : 'asc'
                    },
                    group : {
                        fields : [ 'qcName' ],
                        formatter : function(row) {
                            return row.qcName;
                        }
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            //this.activeRowByIndex(1);
                        }
                    },
                    deleteFormatter : function(row) {
                        return row.orderNo || row.id;
                    },
                    onRowActive : function(index, row) {
                        if (row && !isNaN(row.id)) {
                            var _containerGridUrl = Gikam.printf(_this.urlManager['containerGrid'], {
                                id : row.id
                            });

                            Gikam.getComp('emc-order-add-test-edit-list-test-grid').refresh({
                                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries',
                                requestData : {
                                    orderId_SEQ : row.id,
                                    ADDTESTSTATUS_IN: ',',
                                    TESTCAPCITYORGID_SEQ : workspace.user.ext$.parentorgid,
                                }
                            });

                            Gikam.getAsyncComp('emc-order-receipt-edit-list-container-grid').done(function(grid) {
                                grid.refresh({
                                    url : _containerGridUrl,
                                    requestData : {
                                        // 过滤掉容器名称是空的数据
                                        orderAccept : '1'
                                    }
                                });
                            });
                        }
                    },
                    toolbar : [  {
                        type : 'button',
                        text : 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.BUTTON.BATCHINPUTORDER',
                        icon : 'add',
                        onClick : function() {
                            _this.batchInputOrder();
                        }
                    } ]
                } ]
            } ]
        }
    },

    getTestTab : function() {
        var _this = this;
        return {
            id : 'emc-order-task-container-tab',
            type : 'tab',
            width : Gikam.width.S,
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TAB.TEST',
                items : [ {
                    type : 'grid',
                    id : 'emc-order-add-test-edit-list-test-grid',
                    columns : emcOrderReceipt.getTestListColumns(),
                    service : 'emcOrderTaskServiceImpl',
                    checkOnActive : true,
                } ]
            } ]
        }
    },

    getContainerTab : function() {
        var _this = this;
        return {
            id : 'emc-order-task-container-tab',
            type : 'tab',
            width : Gikam.width.S,
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TAB.TEST',
                items : [ {
                    type : 'grid',
                    id : 'emc-order-add-test-edit-list-test-grid',
                    columns : emcOrderReceipt.getTestListColumns(),
                    service : 'emcOrderTaskServiceImpl',
                    checkOnActive : true,
                } ]
            }, {
                title : 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TAB.CONTAINER',
                items : [ {
                    type : 'grid',
                    id : 'emc-order-receipt-edit-list-container-grid',
                    url : '',
                    columns : emcOrderReceipt.getContainerListColumns(),
                    checkOnActive : true,
                    toolbar : [ {
                        type : 'button',
                        id : 'emc-order-receipt-sync-container-btn',
                        text : 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.BUTTON.SYNC_CONTAINER_INFO',
                        icon : 'refresh',
                        onClick : function() {
                            _this.syncContainerInfo();
                        },
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONTAINER_UPDATE',
                        icon : 'modify',
                        onClick : function() {
                            _this.updateOrder();
                        },
                    } ]
                } ]
            } ]
        }
    },

    submit : function() {
        var selections = Gikam.getComp('emc-order-add-test-edit-list-sample-grid').getSelections();

        var validated = Gikam.getComp('emc-order-add-test-edit-list-sample-grid').validate(selections);
        if (!validated) {
            return;
        }

        Gikam.getComp('emc-order-add-test-edit-list-sample-grid').processRows({
            url : '/secure/emc/module/bp/sample/samples/action/accept',
            confirm : {
                msg : 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.CONFIRMSUBMIT',
                title : 'GIKAM.TIP.CONFIRMSUBMIT',
            },
            formatter : function(row) {
                return row.sampleNo;
            },
            method : 'put'
        }).done(function() {
            Gikam.getComp('emc-order-add-test-edit-list-order-grid').setOptions({
                url : ''
            });
            Gikam.getComp('emc-order-receipt-edit-list-container-grid').setOptions({
                url : ''
            });
            Gikam.getComp('emc-order-add-test-edit-list-sample-grid').refresh();
        });
    },

    undo : function() {
        var grid = Gikam.getComp('emc-order-add-test-edit-list-sample-grid');
        Gikam.create('workflow').undo({
            data : grid.getSelections(),
            pageObject : {
                baseUrl : '/secure/emc/module/bp/sample/samples',
                workflow : emcOrderReceipt.workflow
            }
        }).done(function() {
            grid.refresh();
        });
    },

    matchBarcode : function() {
        var _this = this
        var selections = Gikam.getComp('emc-order-add-test-edit-list-sample-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.confirm('EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.CONFIRM_MATCH_BARCODE', 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.CONFIRM_MATCH_BARCODE', function() {
            workspace.window.showMask(true);
            Gikam.post(_this.urlManager['matchBarcode'], Gikam.getJsonWrapper({}, [ '', selections ])).done(function(data) {
                if (data === 0) {
                    Gikam.alert('EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.NOTMATCH');
                    return;
                }
                Gikam.alert('成功匹配' + data + '个条形码');
            }).always(function() {
                workspace.window.closeMask();
            });
        })
    },

    uploadSampleFile : function() {
        var _this = this;
        var rows = Gikam.getComp('emc-order-add-test-edit-list-sample-grid').getSelections();
        if (rows && rows.length === 1) {
            Gikam.create('modal', {
                width : 800,
                height : 500,
                title : 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.BUTTON.ANNEX',
                url : emcOrderReceipt.baseUrl + '/page/sample-annex-list?id=' + rows[0].id
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    viewSampleFile : function(v) {
        if (v) {
            Gikam.create('modal', {
                width : 800,
                height : 500,
                title : 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.BUTTON.ANNEX',
                url : emcOrderReceipt.baseUrl + '/page/sample-annex-list?id=' + v
            });
        }
    },

    viewReport : function() {
        var selections = Gikam.getComp('emc-order-add-test-edit-list-sample-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var data = Gikam.getComp('emc-report-config-line-select-base-info-form').getData();
        if (Gikam.isEmpty(data.reportName)) {
            Gikam.alert('EMC.MODULE.SYS.REPORTS.TIP.SELECT_REPORT_FIRST');
            return;
        }

        Gikam.viewReport(data.reportName, selections[0]);
    },

    copyQcOrder : function() {
        var _this = this
        var selections = Gikam.getComp('emc-order-add-test-edit-list-order-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        if (selections.length > 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        if (selections[0].qcCategoryId) {
            Gikam.alert('EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.COPYQCORDERTIP');
            return;
        }
        Gikam.create('modal', {
            width : '25%',
            height :  '70%',
            title : Gikam.choose.getConfig('orderReceiptQcCategory').title,
            url : Gikam.choose.getConfig('orderReceiptQcCategory').url + Gikam.param({
                category : 'orderAccept',
                addNum : true
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                workspace.window.showMask(true);
                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/order/order-receipts/qc-orders/action/copy', Gikam.getJsonWrapper({}, [ '', selections ], [ '', rows ] )).done(function(data) {
                    Gikam.getComp('emc-order-add-test-edit-list-order-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });

            }
        });
    },

    deleteQcOrder : function() {
        var _this = this
        var selections = Gikam.getComp('emc-order-add-test-edit-list-order-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var hasNonQCOrder = false
        selections.forEach(function(row) {
            if (!row.qcCategoryId || !row.qcCategoryCode || !row.qcName) {
                hasNonQCOrder = true
            }
        })
        if (hasNonQCOrder) {
            Gikam.alert('EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.DELETEQCORDERTIP');
            return;
        }
        Gikam.getComp('emc-order-add-test-edit-list-order-grid').deleteRows(_this.urlManager['deleteQcOrders']);
    },

    /** 同步容器信息 */
    syncContainerInfo : function() {
        var selections = Gikam.getComp('emc-order-receipt-edit-list-container-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.confirm('GIKAM.TIP.CONFIRM', 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.SYNC_OVERRIDE_CONTAINER_INFO', function() {
            workspace.window.showMask(true);
            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/sync-container-info', Gikam.getJsonWrapper({}, ['', selections] )).done(function() {
                Gikam.getComp('emc-order-receipt-edit-list-container-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        })
    },

    //容器信息修改
    updateOrder : function() {
        var selections = Gikam.getComp('emc-order-receipt-edit-list-container-grid').getSelections();
        if (selections.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_CONTAINER_DATA');
            return;
        }

        var orderSelections = Gikam.getComp('emc-order-add-test-edit-list-order-grid').getSelections();
        if (orderSelections.length == 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ORDER_DATA');
            return;
        }
        var originSelections = selections.map(function(item) {
            return Object.assign({}, item)
        });
        var avtivedRow = Gikam.getComp('emc-order-add-test-edit-list-order-grid').getActivedRow(); // 样品行
        Gikam.create('modal', {
            width : 800,
            height : 600,
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.TAB.CONTAINER_INFO',
            url : emcOrderReceipt.baseUrl + '/page/order-receipt-container',
            onAfterClose : function(data) {
                Gikam.getLastModal().close();
            }
        });
    },

    retainOrder : function() {
        var _this = this
        var selections = Gikam.getComp('emc-order-add-test-edit-list-order-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var modal = Gikam.create('modal', {
            title : "EMC.MODULE.BP.ORDER.ORDERRECEIPTS.MODAL.RETAINORDER",
            width : 500,
            height : 300,
        });
        var layout = Gikam.create('layout', {
            renderTo : modal.window.$dom,
            center : {
                items : [ {
                    type : 'btnToolbar',
                    items : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        class : 'blue',
                        onClick : function() {
                            if (form.validate()) {
                                selections.forEach(function(row) {
                                    row.retainDays = form.options.data.retainDays
                                    row.retainLoc = form.options.data.retainLoc
                                    row.retainQty = form.options.data.retainQty
                                    row.retainUserId = form.options.data.retainUserId
                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.postText(_this.urlManager['retainOrders'], Gikam.getJsonWrapper({}, [ '', selections ])).done(function(data) {
                                    Gikam.getComp('emc-order-add-test-edit-list-order-grid').refresh();
                                }).always(function() {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close();
                                });
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
                        onClick : function() {
                            modal.close();
                        }
                    } ]
                } ]
            }
        });

        var form = Gikam.create('form', {
            renderTo : layout.options.center.$dom,
            id : 'emc-order-receipt-edit-list-form',
            columns : 1,
            titleWidth : 80,
            fields : [ {
                field : 'ext$.retainusername',
                title : 'T_EMC_ORDER_RETAIN.RETAINUSERID',
                validators : [ 'notEmpty', 'strLength[0, 30]' ],
                preInsert : true,
                type : 'choose',
                category : 'user-simple',
                single : true,
                onAfterChoose : function(rows) {
                    if (!Gikam.isEmpty(rows)) {
                        this.setData({
                            retainUserId : rows[0].id,
                            ext$ : {
                                retainusername : rows[0].userName,
                            }
                        });
                    }
                },
                onClean : function() {
                    this.setData({
                        retainUserId : '',
                        ext$ : {
                            retainusername : '',
                        }
                    });
                },
            }, {
                field : 'retainQty',
                title : 'T_EMC_ORDER_RETAIN.RETAINQTY',
                validators : [ 'notEmpty', 'strLength[0, 19]', 'integer' ],
                type : 'number',
                preInsert : true,
            }, {
                field : 'retainDays',
                title : 'T_EMC_ORDER_RETAIN.RETAINDAYS',
                validators : [ 'notEmpty', 'strLength[0, 19]', 'integer' ],
                type : 'number',
                preInsert : true,
            }, {
                field : 'retainLoc',
                title : 'T_EMC_ORDER_RETAIN.RETAINLOC',
                validators : [ 'notEmpty', 'strLength[0, 66]' ],
                preInsert : true,
            }, ],
            autoSave : false,
            onRendered : function(){
                Gikam.getComp('emc-order-receipt-edit-list-form').setData({
                    retainUserId : workspace.user.id,
                    ext$ : {
                        retainusername : workspace.user.userName,
                    }
                });
            }
        });
    },

    loadSamplePropertyPage : function(id, tmplId, monitorTmplId, bizCategorycode) {
        // 查询 sample 的时候，如果存在样品，则需要更新 采样单的 monitorTmplId，并插入数据到 property data 表
        Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/tmpls/' + id + '/judge-samples/' + monitorTmplId).done(function() {
            Gikam.getComp('emc-order-add-test-edit-list-sample-grid').refreshRowById(id);
        });
        // 打开采样单属性的时候，看 模板 id 是否存在，没有就要提示进行添加；有的话要判断是否添加过
        if (Gikam.isEmpty(monitorTmplId)) {
            Gikam.alert("EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.UNSET_SAMPLE_PROPERTY");
            return;
        }

        var param = {
                id : id,
                tmplId : tmplId,
                monitorTmplId : monitorTmplId,
        };
        if (bizCategorycode != 'JS') {
            param['readonly'] = true;
        }

        Gikam.create('modal', {
            width : '60%',
            bodyStyle : {
                padding : '0px 20px'
            },
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.DETAIL_INFO',
            url : '/secure/emc/module/bp/sample/samples/page/info-detail' + Gikam.param(param),
        });
    },

    urlManager : {
        sampleGrid : '/secure/emc/module/bp/sample/samples/queries/undoable',
        orderGrid : '/secure/emc/module/bp/bp/orders/queries',
        containerGrid : '/secure/emc/module/bp/bp/orders/{id}/order-tasks/queries',
        copyQcOrders : emcOrderReceipt.baseUrl + '/qc-orders/action/copy',
        deleteQcOrders : emcOrderReceipt.baseUrl + '/qc-orders',
        retainOrders : emcOrderReceipt.baseUrl + '/orders/action/retain',
        matchBarcode : emcOrderReceipt.baseUrl + '/samples/action/match-barcode',
    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        });
    },

    // 批录入样品修改
    batchInputOrder : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-order-add-test-edit-list-order-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var activeRow = selections[0];
        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
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
                                var formData = form.getData();
                                selections.forEach(function (item) {
                                    item.ext$.storageconditions = formData.ext$.storageconditions;
                                    item.ext$.storagelocation = formData.ext$.storagelocation;
                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/' + activeRow.id, Gikam.getJsonWrapper({}, [ 'pfOrderServiceImpl', selections ])).done(function (data) {
                                    Gikam.getComp('emc-order-add-test-edit-list-order-grid').refresh();
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
                    } ]
                }]
            }
        });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            columns: 1,
            titleWidth: 80,
            fields: emcOrderReceipt.getBatchInputOrderFields(),
            autoSave: false
        });

    },

    confirmAddTest : function() {
        var selections = Gikam.getComp('emc-order-add-test-edit-list-sample-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var validated = Gikam.getComp('emc-order-add-test-edit-list-sample-grid').validate(selections);
        if (!validated) {
            return;
        }
        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/action/confirm-add-test', Gikam.getJsonWrapper({},  [ 'pfsampleServiceImpl', selections])).done(function() {
            Gikam.getComp('emc-order-add-test-edit-list-sample-grid').refresh();
            Gikam.getComp('emc-order-add-test-edit-list-order-grid').refresh();
            Gikam.getComp('emc-order-add-test-edit-list-test-grid').refresh();
           
        });
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-order-receipt-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '50%',
                items : [ _this.getSampleTab() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    west : {
                        width : '60%',
                        items : [ _this.getOrderTab() ]
                    },
                    center : {
                        items : [ _this.getContainerTab() ]
                    }
                } ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
