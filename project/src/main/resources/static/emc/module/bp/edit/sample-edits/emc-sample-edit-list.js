emcSampleEdit.editPage = {

    // 采样单
    getProjectSampleGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-sample-edit-list-project-sample-grid',
            service: 'emcSampleServiceImpl',
            columns: emcSampleEdit.getProjectSampleColumns(),
            requestData: {
                org_filter: '1',
                sample_SEQ: '1'
            },
            url: '/secure/emc/module/bp/sample/samples/queries',
            initRequest: true,
            page: true,
            onCellClick: function (field, row) {
                if (field === 'sampleNo') {
                    _this.loadSampleExpandRowPage(row);
                }
            },
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getAsyncComp('emc-sample-edit-list-data-input-order-task-grid').done(function (grid) {
                        grid.cleanData();
                    });
                    Gikam.getAsyncComp('emc-sample-edit-list-data-input-container-info-grid').done(function (grid) {
                        grid.cleanData();
                    });
                }
            },
            onRowActive: function (index, row) {
                _this.onSampleTestActive(row);
            },
            toolbar: [{
                type: 'button',
                text: 'GIKAM.SEARCH',
                icon: 'select',
                onClick: function () {
                    Gikam.create('modal', {
                        title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.HIGH_LEVEL_SEARCH',
                        width: '45%',
                        height: '350',
                        url: emcSampleEdit.baseUrl + '/page/cond/query',
                    });
                }
            }, {
                type: 'button',
                text: '采样单预览',
                icon: 'print',
                onClick: function () {
                    var selections = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
                    if (selections.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    Gikam.viewReport(selections[0].reportConfigLineId, selections[0]);
                }
            }, {
                type: 'caption',
                text: '去掉了数据状态限制，不要对审核数据进行增删',
                color: 'red'
            }]
        }
    },

    /** 加载采样信息页面 */
    loadSampleExpandRowPage: function (row) {
        var _param = {
            sampleId: row.id,
            projId: row.projId,
            propertyTmplId: row.propertyTmplId,
            monitorTmplId: row.monitorTmplId,
            sampleFlag: row.sampleFlag,
            orderCategory: row.orderCategory,
            sampleUserId: row.sampleUserId,
            sampleDate: row.sampleDate,
            tmplId: row.ext$.tmplid,
            elnTmplId: row.ext$.elntmplid,
            readonly: false,
        };
        debugger;
        Gikam.create('modal', {
            title: '采样信息',
            width: '90%',
            height: '90%',
            url: IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/page/sample-expand-detail' + Gikam.param(_param),
        });
    },

    // 样品和测试
    getOrderTestGridParam: function () {
        var _this = this;
        return {
            type: 'tab',
            width: Gikam.width.S,
            fill: true,
            panels: [{
                title: 'EMC.MODULE.BP.EDIT.SAMPLE_EDITS.SAMPLES.TAB.ORDERS_TEST.BASE_INFO',
                items: [{
                    type: 'grid',
                    id: 'emc-sample-edit-list-data-input-order-task-grid',
                    service: 'emcOrderServiceImpl',
                    columns: emcSampleEdit.getSampleTestColumns(),
                    deleteFormatter: function (row) {
                        return row.orderNo || row.id;
                    },
                    onRowActive: function (index, row) {
                        _this.onSampleOrderActive(row);
                    },
                    onLoadSuccess: function (data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        } else {
                            Gikam.getComp('emc-sample-edit-list-data-input-container-info-grid').cleanData();
                        }
                    },
                    rowStyleFormatter: function (row) {
                        if (row.termination == '1') {
                            return 'background-color : #FFFF99';
                        }
                    },
                    toolbar: [{
                        type: 'button',
                        text: 'GIKAM.BUTTON.ADD_ORDER',
                        icon: 'add',
                        onClick: function () {
                            _this.addSampleTest();
                        },
                    }, {
                        type: 'button',
                        text: '采样单中移除',
                        icon: 'remove',
                        onClick: function () {
                            var delOrder = function () {
                                var selections = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getSelections();
                                if (Gikam.isEmpty(selections)) {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                    return;
                                }
                                var url = '/secure/emc/module/bp/sample/samples/orders';
                                workspace.window.showMask(true);
                                Gikam.del(url, Gikam.getJsonWrapper(null, ['', selections])).done(function () {

                                }).always(function () {
                                    workspace.window.closeMask();
                                    Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').refresh();
                                    Gikam.getComp('emc-sample-edit-list-data-input-container-info-grid').cleanData();
                                    Gikam.getComp('emc-sample-edit-list-grid').refresh();
                                });
                            }
                            Gikam.confirm('移除样品', "是否移除样品", delOrder, "");
                        },
                    }, {
                        type: 'dropDownMenu',
                        text: 'GIKAM.BUTTON.COPY',
                        items: [{
                            type: 'button',
                            text: 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.BUTTON.COPYQCORDER',
                            icon: 'copy',
                            onClick: function () {
                                _this.copyQcOrder();
                            }
                        }, {
                            type: 'button',
                            text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.COPY_ORDER',
                            icon: 'copy',
                            onClick: function () {
                                var copyOrder = function () {
                                    var selections = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getSelections();
                                    if (Gikam.isEmpty(selections)) {
                                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                        return;
                                    }
                                    if (selections.length > 1) {
                                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                        return;
                                    }
                                    if (selections[0].qcCategoryId) {
                                        Gikam.alert('无法对质控样复制样品操作');
                                        return;
                                    }
                                    var url = '/secure/emc/module/bp/bp/orders/action/copy-orders';
                                    workspace.window.showMask(true);
                                    Gikam.postText(url, Gikam.getJsonWrapper({sameSample: '1'}, ['', selections])).done(function () {
                                        Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').refresh();
                                    }).always(function () {
                                        workspace.window.closeMask();
                                    });
                                }
                                Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.COPY_ORDER', "EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.CONFIRM_COPY_ORDER", copyOrder, "");
                            }
                        },]
                    }, {
                        type: 'dropDownMenu',
                        text: 'GIKAM.BUTTON.DELETE',
                        items: [{
                            type: 'button',
                            text: 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.BUTTON.DELETEQCORDER',
                            icon: 'remove-row',
                            onClick: function () {
                                _this.deleteQcOrder();
                            }
                        }, {
                            type: 'button',
                            text: 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.BUTTON.DELETEORDER',
                            icon: 'remove-row',
                            onClick: function () {
                                _this.deleteNotQcOrder();
                            }
                        }]
                    }, {
                        type: 'button',
                        text: 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.BUTTON.RETAINORDER',
                        icon: 'add',
                        onClick: function () {
                            _this.retainOrder();
                        }
                    }, {
                        type: 'button',
                        text: 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.BUTTON.SYNC_CONTAINER_INFO',
                        icon: 'refresh',
                        onClick: function () {
                            _this.syncOrderContainerInfo();
                        },
                    }]
                }]
            }]
        }
    },

    deleteNotQcOrder: function () {
        var selections = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var hasNonQCOrder = false
        selections.forEach(function (row) {
            if (!row.qcCategoryId && !row.qcCategoryCode && !row.qcName) {
                hasNonQCOrder = true
            }
        })
        if (hasNonQCOrder) {
            Gikam.alert('EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.DELETEORDERTIP');
            return;
        }
        Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').deleteRows(IFM_CONTEXT + '/secure/emc/module/bp/order/order-receipts/orders').done(function () {
            Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').refresh();
            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.DEL_SUCCESS');
        });
    },

    // 数据录入 容器信息
    getOrderContainerGridParam: function () {
        var _this = this;
        return {
            type: 'tab',
            width: Gikam.width.S,
            fill: true,
            panels: [{
                title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.TAB.CONTAINER_INFO',
                items: [{
                    type: 'grid',
                    id: 'emc-sample-edit-list-data-input-container-info-grid',
                    service: 'emcOrderTaskServiceImpl',
                    columns: emcSampleEdit.getSamplePrepareOrderContainerColumns(),
                    onLoadSuccess: function (data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        }
                    },
                    toolbar: [{
                        type: 'button',
                        text: 'GIKAM.BUTTON.CONTAINER_UPDATE',
                        icon: 'add',
                        onClick: function () {
                            _this.updateDataInputOrderTask();
                        },
                    }]
                }]
            }]
        }
    },

    // 数据录入 容器信息修改
    updateDataInputOrderTask: function () {
        var orderTaskselections = Gikam.getComp('emc-sample-edit-list-data-input-container-info-grid').getSelections();
        if (orderTaskselections.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_CONTAINER_DATA');
            return;
        }
        var orderSelections = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getSelections();
        if (orderSelections.length == 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ORDER_DATA');
            return;
        }

        Gikam.create('modal', {
            width: 800,
            height: 600,
            id: 'emc-sample-edit-order-task-detail-modal',
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.TAB.CONTAINER_INFO',
            url: '/secure/emc/module/bp/sample/samples/page/order-container',
            onAfterClose: function (data) {
                Gikam.getLastModal().close();
            }
        });
    },

    // 样品容器信息
    getOrderTaskUrl: function (orderId) {
        if (orderId) {
            // 需要处理 testName
            return Gikam.printf('/secure/emc/module/bp/bp/orders/{id}/order-tasks/queries', {
                id: orderId
            })
        }
    },

    // 样品和测试
    getSampleTestUrl: function () {
        return Gikam.printf('/secure/emc/module/bp/bp/orders/queries/raw', {})
    },

    onSampleTestActive: function (row) {
        var _this = this;
        var activedRow = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getActivedRow();
        var orderCategory = activedRow.orderCategory;
        let columns = emcSampleEdit.getSampleTestColumns();
        if (orderCategory == null || orderCategory == '') {
            return;
        }
        //需要获取检测模板组的名称用其进行判断
        var url = Gikam.printf('/secure/emc/module/bp/sample/samples/action/select-category-code');
        var that = this;
        Gikam.postText(url, Gikam.getJsonWrapper({}, ['', [activedRow]])).done(function (data) {
            var orderCategoryCode = data;
        }).always(function (str) {
            var orderCategoryCode = str;
            if (orderCategoryCode == 'T' || orderCategoryCode == 'O') {
                columns = emcSampleEdit.getSampleTestColumns1();
            }
            if (row) {
                Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').refreshColumns(columns);
                Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').refresh({
                    url: _this.getSampleTestUrl(row.id),
                    requestData: {
                        sampleId_SEQ: row.id,
                        orderId_SEQ: '',
                    }
                });

            }
        });

    },

    // 数据录入 样品 - 容器信息
    onSampleOrderActive: function (row) {

        var _this = this;
        if (row) {
            Gikam.getComp('emc-sample-edit-list-data-input-container-info-grid').refresh({
                url: _this.getOrderTaskUrl(row.id),
            });
        }
    },

    loadSamplePropertyPage: function (id, tmplId, monitorTmplId) {
        // 查询 sample 的时候，如果存在样品，则需要更新 采样单的 monitorTmplId，并插入数据到 property data 表
        Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/tmpls/' + id + '/judge-samples/' + monitorTmplId).done(function () {
        });
        // 打开采样单属性的时候，看 模板 id 是否存在，没有就要提示进行添加；有的话要判断是否添加过
        if (Gikam.isEmpty(monitorTmplId)) {
            Gikam.alert("EMC.MODULE.BP.EDIT.SAMPLE_EDITS.SAMPLES.TIP.UNSET_SAMPLE_PROPERTY");
            return;
        }

        var _this = this;
        Gikam.create('modal', {
            width: '60%',
            bodyStyle: {
                padding: '0px 20px'
            },
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.DETAIL_INFO',
            url: '/secure/emc/module/bp/sample/samples/page/sample-edit-info-detail' + Gikam.param({
                id: id,
                tmplId: tmplId,
                monitorTmplId: monitorTmplId,
            }),
        });
    },

    loadDataInputPage: function (id) {
        var _this = this;
        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.EDIT.SAMPLE_EDITS.SAMPLES.MODAL.DATA_INPUT',
            url: '/secure/emc/module/bp/sample/samples/page/sample-edit-data-input-detail' + Gikam.param({
                id: id,
            }),
        });
    },

    createResultInput: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-folder-edit-list-layout',
            renderTo: workspace.window.$dom,
            center: {
                items: [{
                    type: 'tab',
                    fill: true,
                    panels: [{
                        title: 'EMC.MODULE.BP.EDIT.SAMPLE_EDITS.SAMPLES.TAB.DATA_INPUT.BASE_INFO',
                        items: [{
                            type: 'layout',
                            center: {
                                items: [{
                                    type: 'layout',
                                    north: {
                                        // 采样单
                                        height: '45%',
                                        items: [this.getProjectSampleGridParam()]
                                    },
                                    center: {
                                        width: '50%',
                                        items: [{
                                            type: 'layout',
                                            west: {
                                                width: '50%',
                                                // 样品测试
                                                items: [this.getOrderTestGridParam()]
                                            },
                                            center: {
                                                width: '50%',
                                                // 容器信息
                                                items: [this.getOrderContainerGridParam()]
                                            }
                                        }]
                                    }
                                }]
                            },
                        }]
                    }]
                }]
            },
        });
    },

    deleteOrder: function () {
        var selections = Gikam.getComp('emc-sample-edit-list-order-prepare-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var hasNonQCOrder = false
        selections.forEach(function (row) {
            if (!row.qcCategoryId && !row.qcCategoryCode && !row.qcName) {
                hasNonQCOrder = true
            }
        })
        if (hasNonQCOrder) {
            Gikam.alert('EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.DELETEORDERTIP');
            return;
        }
        var url = '/secure/emc/module/bp/bp/orders';
        workspace.window.showMask(true);
        Gikam.del(url, Gikam.getJsonWrapper(null, ['', selections])).done(function () {
            Gikam.getComp('emc-sample-edit-list-order-prepare-grid').refresh();
            Gikam.getComp('emc-sample-edit-list-order-container-info-grid').refresh();
            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.DEL_SUCCESS');
        }).always(function () {
            workspace.window.closeMask();
        });
    },

    copyQcOrder: function () {
        var _this = this
        var selections = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getSelections();
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
            width: 500,
            title: Gikam.choose.getConfig('orderReceiptQcCategory').title,
            url: Gikam.choose.getConfig('orderReceiptQcCategory').url + Gikam.param({
                category: 'scene',
                addNum: true,
                single: true
            }),
            onAfterClose: function (rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                let totalSelections = []
                rows.forEach(row => {
                    var newItem = JSON.parse(JSON.stringify(selections[0]));
                    newItem.projId = selections[0].projId;
                    newItem.qcCategoryId = row.id;
                    newItem.qcCategoryCode = row.qcCategoryId;
                    newItem.qcAddFolder = row.addFolder;
                    newItem.qcName = row.qcName;
                    newItem.ext$.parOrder = row.parOrder;
                    newItem.ext$.updateParNo = row.updateParNo;
                    newItem.ext$.addnum = row.addNum;
                    totalSelections.push(newItem);
                })
                workspace.window.showMask(true);
                Gikam.postText('/secure/emc/module/bp/order/order-receipts/qc-orders/action/copy', Gikam.getJsonWrapper({}, ['', totalSelections], ['', rows])).done(function (data) {
                    Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').refresh();
                }).always(function () {
                    workspace.window.closeMask();
                });
            }
        });
    },

    deleteQcOrder: function () {
        var _this = this
        var selections = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var hasNonQCOrder = false
        selections.forEach(row => {
            if (!row.qcCategoryId || !row.qcCategoryCode || !row.qcName) {
                hasNonQCOrder = true
            }
        })
        if (hasNonQCOrder) {
            Gikam.alert('EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.DELETEQCORDERTIP');
            return;
        }
        Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').deleteRows('/secure/emc/module/bp/order/order-receipts/qc-orders');
    },

    retainOrder: function () {
        var _this = this
        var selections = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        const modal = Gikam.create('modal', {
            title: "EMC.MODULE.BP.ORDER.ORDERRECEIPTS.MODAL.RETAINORDER",
            width: 500,
            height: 300,
        });
        const layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [{
                    type: 'btnToolbar',
                    items: [{
                        type: 'button',
                        text: 'GIKAM.BUTTON.CONFIRM',
                        class: 'blue',
                        onClick() {
                            if (form.validate()) {
                                selections.forEach(row => {
                                    row.retainDays = form.options.data.retainDays
                                    row.retainLoc = form.options.data.retainLoc
                                    row.retainQty = form.options.data.retainQty
                                    row.retainUserId = form.options.data.retainUserId
                                })
                                workspace.window.showMask(true);
                                Gikam.postText('/secure/emc/module/bp/order/order-receipts/orders/action/retain', Gikam.getJsonWrapper({}, ['', selections])).done(function (data) {
                                    Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').refresh();
                                }).always(function () {
                                    modal.close();
                                    workspace.window.closeMask();
                                });
                            }
                        }
                    }, {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CANCEL',
                        onClick() {
                            modal.close();
                        }
                    }]
                }]
            }
        });

        const form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            columns: 1,
            titleWidth: 80,
            fields: [
                {
                    field: 'ext$.retainusername',
                    title: 'T_EMC_ORDER_RETAIN.RETAINUSERID',
                    validators: ['notEmpty', 'strLength[0, 30]'],
                    preInsert: true,
                    type: 'choose',
                    category: 'user-simple',
                    single: true,
                    onAfterChoose: function (rows) {
                        if (!Gikam.isEmpty(rows)) {
                            this.setData({
                                retainUserId: rows[0].id,
                                ext$: {
                                    retainusername: rows[0].userName,
                                }
                            });
                        }
                    },
                    onClean: function () {
                        this.setData({
                            retainUserId: '',
                            ext$: {
                                retainusername: '',
                            }
                        });
                    },
                },
                {
                    field: 'retainQty',
                    title: 'T_EMC_ORDER_RETAIN.RETAINQTY',
                    validators: ['notEmpty', 'strLength[0, 19]', 'integer'],
                    type: 'number',
                    preInsert: true,
                },
                {
                    field: 'retainDays',
                    title: 'T_EMC_ORDER_RETAIN.RETAINDAYS',
                    validators: ['notEmpty', 'strLength[0, 19]', 'integer'],
                    type: 'number',
                    preInsert: true,
                },
                {
                    field: 'retainLoc',
                    title: 'T_EMC_ORDER_RETAIN.RETAINLOC',
                    validators: ['notEmpty', 'strLength[0, 66]'],
                    preInsert: true,
                },
            ],
            autoSave: false
        });
    },

    updateSampleTestBatch: function () {
        var selections = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getSelections();
        if (selections.length > 0) {
            var formData = Gikam.getComp('emc-sample-edit-list-project-grid').getActivedRow();
            Gikam.create('modal', {
                width: 600,
                height: 313,
                title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.BATCH_INPUT',
                url: '/secure/emc/module/bp/sample/samples/page/sample-test-batch',
                onAfterClose: function (data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        selections.map(function (item) {
                            item.sampleBeginDate = data.sampleBeginDate;
                            item.sampleBeginTime = data.sampleBeginTime;
                            item.sampleCompleteDate = data.sampleCompleteDate;
                            item.sampleCompleteTime = data.sampleCompleteTime;
                            item.orderDesc = data.orderDesc;
                        });
                        Gikam.put('/secure/emc/module/bp/sample/samples/' + formData.id, Gikam.getJsonWrapper({}, ['emcOrderServiceImpl', selections])).done(function () {
                            Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').refresh()
                        }).always(function () {
                            workspace.window.closeMask();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
        }
    },

    // （样品和测试）添加样品
    addSampleTest: function () {
        var _this = emcSampleEdit.editPage;
        var activedRow = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getActivedRow();
        if (Gikam.isEmpty(activedRow)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_SAMPLE_DATA');
            return;
        }

        if (activedRow.orderCategory == undefined) { // 当前采样单无样品类型，过滤条件无需增加类型判断
            Gikam.create('modal', {
                title: Gikam.choose.getConfig('order').title,
                url: Gikam.choose.getConfig('order').url + Gikam.param({
                    targetOrder_unassigned: '1',
                    targetOrderTable: 'T_EMC_ORDER',
                    targetOrderColumn: 'ID',
                    targetOrderIdFilterColumn: 'SAMPLEID',
                    targetOrderIdFilterValue: activedRow.id,
                    projId_SEQ: activedRow.projId,
                    folderstatus_SEQ: 'done',
                    targetOrderSample_unassigned: '1',
                }),
                onAfterClose: function (rows, index) {
                    if (Gikam.isEmpty(rows)) {
                        return;
                    }
                    // 判断选择的类型是否相同
                    var orders = rows[0].orderCategory;
                    var flag = rows.some(function (item) {
                        return item.orderCategory != orders;
                    });
                    if (flag) {
                        Gikam.alert('EMC.MODULE.BP.BP.FOLDERS.TIP.CHOOSE_ONE_ORDER_TYPE');
                        return;
                    }
                    workspace.window.showMask(true);
                    Gikam.postText('/secure/emc/module/bp/sample/samples/' + activedRow.id + '/orders', Gikam.getJsonWrapper({}, [null, rows])).done(function (data) {

                    }).always(function () {
                        workspace.window.closeMask();
                        Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').refresh();
                        Gikam.getComp('emc-sample-edit-list-project-sample-grid').refresh();
                        Gikam.getComp('emc-sample-edit-list-grid').refresh();
                    });
                }
            });
            return;
        }
        Gikam.create('modal', {
            title: Gikam.choose.getConfig('order').title,
            url: Gikam.choose.getConfig('order').url + Gikam.param({

                target_unassigned: '1',
                targetSampleTable: 'T_EMC_SAMPLE',
                targetSampleIdColumn: 'ORDERCATEGORY',
                targetSampleCategoryFilterColumn: 'ORDERCATEGORY',
                targetSampleCategoryFilterValue: activedRow.orderCategory,
                folderstatus_SEQ: 'done',
                targetOrder_unassigned: '1',
                targetOrderTable: 'T_EMC_ORDER',
                targetOrderColumn: 'ID',
                targetOrderIdFilterColumn: 'SAMPLEID',
                targetOrderIdFilterValue: activedRow.id,
                projId_SEQ: activedRow.projId,
                targetOrderSample_unassigned: '1',
            }),
            onAfterClose: function (rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var orders = rows[0].orderCategory;
                var flag = rows.some(function (item) {
                    return item.orderCategory != orders;
                });
                if (flag) {
                    return;
                }
                workspace.window.showMask(true);
                Gikam.postText('/secure/emc/module/bp/sample/samples/' + activedRow.id + '/orders', Gikam.getJsonWrapper({}, [null, rows])).done(function (data) {

                }).always(function () {
                    workspace.window.closeMask();
                    Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').refresh();
                    Gikam.getComp('emc-sample-edit-list-project-sample-grid').refresh();
                    Gikam.getComp('emc-sample-edit-list-grid').refresh();
                });
            }
        });
    },

    review: function () {

        var selections = Gikam.getComp('emc-sample-edit-list-grid').getSelections();

        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.BP.FOLDERS.TITLE.REVIEW',
            url: '/secure/emc/module/bp/bp/folders/page/review' + Gikam.param({
                id: selections[0].id,
            }),
            onAfterClose: function (rows) {
                Gikam.getComp('emc-sample-edit-list-grid').refresh();
            }
        });
    },

    /** 按样品同步容器信息 */
    syncOrderContainerInfo: function () {
        var selections = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.confirm('GIKAM.TIP.CONFIRM', 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.TIP.SYNC_OVERRIDE_CONTAINER_INFO', function () {
            workspace.window.showMask(true);
            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/sync-order-container-info', Gikam.getJsonWrapper({}, ['', selections])).done(function () {
                Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').refresh();
            }).always(function () {
                workspace.window.closeMask();
            });
        })
    },

    init: function (param) {
        this.param = param;
        this.createResultInput();
    }
};
