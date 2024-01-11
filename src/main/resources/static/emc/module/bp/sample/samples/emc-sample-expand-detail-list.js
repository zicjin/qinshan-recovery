emcSample.expandDetailPage = {

    getSampleExpandNext: function () {
        var _this = this;
        return {
            type: 'steps',
            id: 'emc-sample-expand-next',
            // 展示的节点集合，节点格式为
            items: [
                {text: '点位信息', value: 'folder', status: "next"},
                {text: '样品信息', value: 'order', status: "wait"},
                {text: '标签信息', value: 'lableNo', status: "wait"},
                {text: '仪器信息', value: 'equipt', status: "wait"},
                {text: '记录数据', value: 'data', status: "wait"},
                {text: '原始数据', value: 'originData', status: "wait"},
                {text: '记录附件', value: 'file', status: "wait"},
                {text: '数据检查', value: 'check', status: "wait"},
                {text: '浏览记录', value: 'report', status: "wait"}]
            ,
            // 点击节点时事件
            onItemClick: function (item) {
                _this.onItemClick(item);
            },
        }
    },

    /**
     * 新增/删除panel时，只需要在newitems和panelIds中对应顺序位置增加对应数据就行，不需要修改其他内容
     * @param item
     */
    onItemClick: function (item) {
        var _this = this,
            tab = Gikam.getComp('emc-sample-expand-detail-tab'),
            newitems = [
                {text: '点位信息', value: 'folder', status: "next"},
                {text: '样品信息', value: 'order', status: "wait"},
                {text: '标签信息', value: 'lableNo', status: "wait"},
                {text: '仪器信息', value: 'equipt', status: "wait"},
                {text: '记录数据', value: 'data', status: "wait"},
                {text: '原始数据', value: 'originData', status: "wait"},
                {text: '记录附件', value: 'file', status: "wait"},
                {text: '数据检查', value: 'check', status: "wait"},
                {text: '浏览记录', value: 'report', status: "wait"}
            ],
            panelIds = [
                'emc-sample-expand-detail-list-folder-panel',
                'emc-sample-expand-detail-list-order-panel',
                'emc-sample-expand-detail-list-lableno-panel',
                'emc-sample-expand-detail-list-equipt-panel',
                'emc-sample-expand-detail-list-record-data-panel',
                'emc-sample-expand-detail-list-origin-data-panel',
                'emc-sample-expand-detail-list-sample-file-panel',
                'emc-sample-expand-detail-list-check-panel',
                'emc-sample-expand-detail-list-report-panel'
            ]
        ;
        var index = 0;
        for (var i = 0; i < newitems.length; i++) {
            if (newitems[i].value === item.value) {
                newitems[i].status = "next";
                index = i;
            } else if (i <= index) {
                newitems[i].status = "finish";
                index++;
            } else {
                newitems[i].status = "wait";
            }
        }
        for (var i = 0; i < panelIds.length; i++) {
            if (i === index) {
                tab.revertPanel(panelIds[i]);
                tab.showPanel(panelIds[i]);
            } else {
                tab.removePanel(panelIds[i]);
            }
        }
        Gikam.getComp('emc-sample-expand-next').refreshItems(newitems);
    },

    updateSampleTestBatch: function () {
        var selections = Gikam.getComp('emc-sample-expand-detail-list-order-grid').getSelections();
        if (selections.length > 0) {
            Gikam.create('modal', {
                width: 400,
                height: 250,
                title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.BATCH_INPUT',
                url: emcSample.baseUrl + '/page/sample-test-batch',
                onAfterClose: function (data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        selections.map(function (item) {
                            item.sampleBeginDate = data.sampleBeginDate;
                            item.sampleBeginTime = data.sampleBeginTime;
                            item.sampleCompleteDate = data.sampleCompleteDate;
                            item.sampleCompleteTime = data.sampleCompleteTime;
                        });
                        Gikam.put(emcSample.baseUrl + '/' + selections[0].id, Gikam.getJsonWrapper({}, ['emcOrderServiceImpl', selections])).done(function () {
                            Gikam.getComp('emc-sample-expand-detail-list-order-grid').refresh()
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

    movein: function () {
        var _this = this;
        if (_this.param.orderCategory == undefined) { // 当前采样单无样品类型，过滤条件无需增加类型判断
            Gikam.create('modal', {
                title: Gikam.choose.getConfig('order').title,
                url: Gikam.choose.getConfig('order').url + Gikam.param({
                    targetOrder_unassigned: '1',
                    targetOrderTable: 'T_EMC_ORDER',
                    targetOrderColumn: 'ID',
                    targetOrderIdFilterColumn: 'SAMPLEID',
                    targetOrderIdFilterValue: _this.param.sampleId,
                    projId_SEQ: _this.param.projId,
                    folderstatus_SEQ: 'done',
                    targetOrderSample_unassigned: '1',
                    sampleUserId: workspace.user.id,
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
                    Gikam.postText(emcSample.baseUrl + '/' + _this.param.sampleId + '/orders', Gikam.getJsonWrapper({}, [null, rows])).done(function (data) {
                        Gikam.getComp('emc-sample-expand-detail-list-order-grid').refresh();
                    }).always(function () {
                        workspace.window.closeMask();
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
                targetSampleCategoryFilterValue: _this.param.orderCategory,
                folderstatus_SEQ: 'done',
                targetOrder_unassigned: '1',
                targetOrderTable: 'T_EMC_ORDER',
                targetOrderColumn: 'ID',
                targetOrderIdFilterColumn: 'SAMPLEID',
                targetOrderIdFilterValue: _this.param.sampleId,
                projId_SEQ: _this.param.projId,
                targetOrderSample_unassigned: '1',
                sampleUserId: workspace.user.id,
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
                Gikam.postText(emcSample.baseUrl + '/' + _this.param.sampleId + '/orders', Gikam.getJsonWrapper({}, [null, rows])).done(function (data) {
                    Gikam.getComp('emc-sample-expand-detail-list-order-grid').refresh();
                }).always(function () {
                    workspace.window.closeMask();
                });
            }
        });
    },

    moveout: function () {
        var _this = this;
        var selections = Gikam.getComp('emc-sample-expand-detail-list-order-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.TITLE.MOVE_OUT', 'EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.MOVE_OUT', function () {
            var url = emcSample.baseUrl + '/orders';
            workspace.window.showMask(true);
            Gikam.del(url, Gikam.getJsonWrapper(null, ['', selections])).done(function () {
                Gikam.getComp('emc-sample-expand-detail-list-order-grid').refresh();
            }).always(function () {
                workspace.window.closeMask();
            });
        });

    },


    getEquiptPanels: function () {
        var _this = this;
        return {
            title: '仪器信息',
            id: 'emc-sample-expand-detail-list-equipt-panel',
            show: false,
            items: [{
                type: 'grid',
                service: 'emcOrderTaskServiceImpl',
                id: 'emc-sample-expand-detail-list-equipt-grid',
                columns: emcSample.getOrderTaskEquiptColumns(),
                url: '/secure/emc/module/bp/bp/order-tasks/queries',
                requestData: {
                    sampleId_SEQ: _this.param.sampleId,
                    analysisType_SEQ: "sampling"
                },
                order: {
                    testName: 'asc'
                },
                page: true,
                onLoadSuccess: function (data) {
                    if (!Gikam.isEmpty(data)) {
                        this.activeRowByIndex(0);
                    }
                },
                toolbar: [{
                    type: 'button',
                    text: 'GIKAM.BUTTON.PREVIOUS',
                    icon: 'move-left',
                    onClick: function () {
                        var target = {};
                        target.value = 'lableNo';
                        _this.onItemClick(target);
                    }
                }, {
                    type: 'button',
                    text: 'GIKAM.BUTTON.NEXT',
                    icon: 'move-right',
                    onClick: function () {
                        var target = {};
                        target.value = 'data';
                        _this.onItemClick(target);
                    }
                }, {
                    type: 'button',
                    text : 'GIKAM.BUTTON.UPDATE_EQUIPT',
                    icon: 'edit',
                    onClick: function () {
                        _this.updateEquipt();
                    }
                }, {
                    type: 'button',
                    text : 'GIKAM.BUTTON.UPDATE_EMP',
                    icon: 'edit',
                    onClick: function () {
                        _this.updateEmp();
                    }
                }, {
                    type: 'button',
                    text: '校准仪器',
                    icon: 'edit',
                    onClick: function () {
                        _this.updateCalibrateEquipt();
                    }
                }],
            }]
        }
    },

    updateEquipt: function () {
        var _this = this;
        var grid = Gikam.getComp('emc-sample-expand-detail-list-equipt-grid');
        var selections = grid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
            return;
        }
        var testMethodId = selections[0].testMethodId;
        for (var j = 0; j < selections.length; j++) {
            if (testMethodId != selections[j].testMethodId) {
                Gikam.alert('需选择同一测试方法的监测项目！');
                return;
            }
        }
        Gikam.create('modal', {
            title: Gikam.choose.getConfig('equipt').title,
            url: Gikam.choose.getConfig('equipt').url + Gikam.param({
                single : true,
                target_equiptassigned : '1',
                testMethodId : selections[0].testMethodId
            }),
            onAfterClose: function (rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var saveData = [];
                var saveRecordData = [];
                Gikam.each(selections, function (index, item) {
                    saveData.push({
                        id: item.id,
                        equiptId: rows[0].id,
                        analysisUserId: workspace.user.id,
                        analysisUserName: workspace.user.userName,
                    });
                    saveRecordData.push({
                        projId: selections[0].projId,
                        sampleId: _this.param.id,
                        equiptId: rows[0].id,
                        testId: item.testId,
                        testName: item.testName,
                        recordDate: item.ext$.samplebegindate,
                        preUseInspection: '1',
                        afterUseInspection: '1',
                        usePersonId: workspace.user.id,
                        usePerson: workspace.user.userName,
                    });
                });
                Gikam.postText('/secure/emc/module/mdm/equipt/equipt-used-records/data-input-add-record', Gikam.getJsonWrapper({}, ['emcEquiptUsedRecordServiceImpl', saveRecordData]));

                workspace.window.showMask(true);
                Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, ['emcOrderTaskServiceImpl', saveData])).done(function (data) {
                    Gikam.each(selections, function (index, item) {
                        grid.refreshRowById(item.id);
                    });
                }).always(function () {
                    workspace.window.closeMask();
                });
            }
        });
    },

    updateCalibrateEquipt: function () {
        var selections = Gikam.getComp('emc-sample-expand-detail-list-equipt-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var testName = selections[0].testName;
        if (testName == 'pH值' || testName == '溶解氧' || testName == '电导率' || testName == '浊度') {
            Gikam.create('modal', {
                width: '80%',
                height: '60%',
                title: testName + '便携仪器校准',
                url: '/secure/emc/module/bp/equipt-calibrations/page/edit-list' + Gikam.param({
                    folderId : selections[0].folderId,
                    orderTaskId : selections[0].id,
                    testName : testName
                })
            });
        } else {
            Gikam.alert('选中的监测项目不是pH值、溶解氧、电导率、浊度，无需校准');
        }
    },

    updateEmp: function () {
        var grid = Gikam.getComp('emc-sample-expand-detail-list-equipt-grid');
        var selections = grid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title: Gikam.choose.getConfig('emp').title,
            url: Gikam.choose.getConfig('emp').url + Gikam.param({
                single: true,
                departmentId_SEQ: selections[0].ext$.sampleorgid
            }),
            width: '30%',
            onAfterClose: function (formData) {
                if (Gikam.isEmpty(formData)) {
                    return;
                }
                var saveData = [];
                Gikam.each(selections, function (index, item) {
                    saveData.push({
                        id: item.id,
                        analysisUserId: formData[0].id,
                        analysisUserName: formData[0].userName,
                    });
                });
                workspace.window.showMask(true);
                Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, ['emcOrderTaskServiceImpl', saveData])).done(function (id) {
                    Gikam.each(selections, function (index, item) {
                        grid.refreshRowById(item.id);
                    });
                }).always(function () {
                    workspace.window.closeMask();
                });
            }
        });
    },

    getOrderTaskDataGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            service: 'emcOrderTaskServiceImpl',
            id: 'emc-sample-expand-detail-list-record-data-order-task-grid',
            columns: emcSample.getSampleExpandTaskDataListColumns(),
            url: '/secure/emc/module/bp/bp/order-tasks/queries/group-by-test',
            readonly: "true" === _this.param.readonly,
            requestData: {
                sampleId: _this.param.sampleId,
            },
            checkOnActive : false,
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData(['emc-sample-expand-detail-list-record-data-order-task-result-grid']);
                }
            },
            onRowActive: function (index, row) {
                _this.refreshRecordColumnAndResultData(row);
            },
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.PREVIOUS',
                icon: 'move-left',
                onClick: function () {
                    var target = {};
                    target.value = 'equipt';
                    _this.onItemClick(target);
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.NEXT',
                icon: 'move-right',
                onClick: function () {
                    var target = {};
                    target.value = 'originData';
                    _this.onItemClick(target);
                }
            }]
        }
    },

    getOrderTaskResultDataGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            service: 'emcOrderTaskResultServiceImpl',
            id: 'emc-sample-expand-detail-list-record-data-order-task-result-grid',
            columns: emcSample.getSampleExpandResultDataListColumns(),
            instantSavePath: IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/instant',
            readonly: "true" === _this.param.readonly,
            onUpdated: function (data, fields, index) {
                workspace.window.showMask(true);
                var orderTaskResultUrl = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results';
                data['originalResult'] = data['colVal'];
                Gikam.put(orderTaskResultUrl + '/instant', Gikam.getJsonWrapper({}, ['emcOrderTaskResultServiceImpl', [data]])).done(function () {
                    Gikam.put(orderTaskResultUrl + '/action/change-result', Gikam.getJsonWrapper({}, ['', [data]])).done(function () {
                        // 查询数据
                        var activedRow = Gikam.getComp('emc-sample-expand-detail-list-record-data-order-task-grid').getActivedRow();
                        if (Gikam.isNotEmpty(activedRow)) {

                            Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/quick-inputs-sample/table-rows/queries', Gikam.getJsonWrapper({
                                f: {
                                    orderTaskId_IN: data.ext$.ids
                                }
                            })).done(function (data) {
                                var grid = Gikam.getComp('emc-sample-expand-detail-list-record-data-order-task-result-grid');
                                Gikam.each(data.rows, function (index, row) {
                                    grid.setData(row, false, true);
                                });
                            });

                        }
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }).fail(function () {
                    workspace.window.closeMask();
                });
            },
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.BATCH_INPUT',
                icon: 'refresh',
                onClick: function () {
                    _this.batchInputResult();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SYNC_TMPL_INFO',
                icon: 'edit',
                onClick: function () {
                    Gikam.confirm('提示', '是否同步模板信息', function () {
                        _this.syncTmplInfo();
                    });
                },
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.INVALID_DATA',
                icon: 'edit',
                onClick: function () {
                    _this.InvalIdData();
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.IMPORT',
                icon: 'edit',
                onClick: function () {
                    Gikam.importExcel({
                        url: IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'emcSampleImportFileFunction',
                        dbTable: 'T_EMC_ORDER_TASK_RESULT',
                        onImportSuccess: function (files) {
                            Gikam.getComp('emc-sample-expand-detail-list-record-data-order-task-grid').refresh();//记录数据页签的左侧列表
                            Gikam.getComp('emc-sample-expand-detail-list-origin-data-folder-grid').refresh();//原始数据页签下的点位列表
                        }
                    });
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.EXPORT',
                icon: 'export',
                onClick: function () {
                    Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}/{id}', {
                        templateCode: 'emcSampleExportTemplate',
                        service: 'emcSampleExportTemplate',
                        id: _this.param.sampleId
                    })).done(function (fileUri) {
                        var fileUrl = IFM_CONTEXT + fileUri;
                        Gikam.download(fileUrl);
                    });
                }
            }]
        }
    },

    /** 批录入结果
     * 1代表按样品展示的界面
     * 2代表按容器展示的界面
     *
     * */
    batchInputResult: function () {
        var _this = this,
            _modalFormId = 'emc-sample-expand-detail-list-modal-form',
            _modalGridId = 'emc-sample-expand-detail-list-modal-grid';

        var activedRow = Gikam.getComp('emc-sample-expand-detail-list-record-data-order-task-grid').getActivedRow();
        var selections = Gikam.getComp('emc-sample-expand-detail-list-record-data-order-task-result-grid').getSelections();
        if (!Gikam.isEmpty(selections)) {
            var modal = Gikam.create('modal', {
                title: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.BATCH_INPUT_RESULT',
                width: '35%',
                height: 450,
                onAfterClose: function (data) {
                    if (!Gikam.isEmpty(data)) {
                        Gikam.post('/secure/emc/module/bp/bp/order-task-results/action/batch-input-result', Gikam.getJsonWrapper({
                            result: data[0].result,
                            itemId: data[1].id
                        }, ['', selections])).done(function () {
                            _this.refreshRecordResultData(activedRow);
                        });
                    }
                }
            });
            Gikam.create('layout', {
                renderTo: modal.window.$dom,
                north: {
                    items: [{
                        type: 'btnToolbar',
                        items: [{
                            type: 'button',
                            text: 'GIKAM.BUTTON.CONFIRM',
                            icon: 'select',
                            onClick: function () {
                                var form = Gikam.getComp(_modalFormId);
                                var selections = Gikam.getComp(_modalGridId).getSelections();
                                if (!Gikam.isEmpty(selections)) {
                                    if (form.validate()) {
                                        Gikam.getLastModal().close([form.getData(), selections[0]]);
                                    }
                                } else {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                }
                            }
                        }]
                    }]
                },
                center: {
                    items: [{
                        type: 'layout',
                        north: {
                            height: 60,
                            items: [{
                                type: 'form',
                                id: _modalFormId,
                                columns: 1,
                                fields: [{
                                    field: 'result',
                                    title: 'T_EMC_ITEM_RESULT.RESULT',
                                    validators: ['notEmpty']
                                }]
                            }]
                        },
                        center: {
                            items: [{
                                type: 'grid',
                                id: _modalGridId,
                                url: Gikam.printf('/secure/emc/module/mdm/bizmdm/tests/{id}/items/queries', {
                                    id: activedRow.testMethodId
                                }),
                                requestData: {
                                    targetType: 'sampleBatchInputResultItem',
                                    analysisCategory_SEQ: 'sampling',
                                    orderTaskId: activedRow.ext$.ids
                                },
                                columns: [{
                                    radio: true
                                }, {
                                    field: 'ext$.testname',
                                    title: 'T_EMC_ITEM.TESTNAME',
                                    width: 230
                                }, {
                                    field: 'itemAliasName',
                                    title: 'T_EMC_ITEM.ITEMNAME',
                                    width: 230
                                }]
                            }]
                        }
                    }]
                },
            })
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    refreshRecordColumnAndResultData: function (row) {
        var _this = this;
        var baseUrl = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/quick-inputs-sample';
        Gikam.post(baseUrl + '/table-heads/queries', Gikam.getJsonWrapper({
            f: {
                orderTaskId_IN: row.ext$.ids
            }
        })).done(function (itemColumns) {
            var oldColumns = emcSample.getSampleExpandResultDataListColumns();
            var condArr = [];
            itemColumns.forEach(function (column) {
                var columnfield_ext$ = column.field + '_ext$';
                column['width'] = 120;
                //column['editor'] = column.ext$.script === '0';
                column['editor'] = true;
                column['sort'] = false;
                column['readonly'] = "true" === _this.param.readonly;
                var textStyleFormatter = function (row) {
                    if (column.ext$.script === '0') {
                        return {
                            backgroundColor: 'palegreen'
                        }
                    }
                };
                column['textStyleFormatter'] = textStyleFormatter;
                column['onBeforeEditorRender'] = function (row) {
                    if (column.ext$.script === '0') {
                        return true;
                    } else {
                        return false;
                    }
                    return false;
                };
                column['formatter'] = function (i, v, r) {
                    return Gikam.scientificNotation(v);
                };
                condArr.push(column);
            });
            var newColumns = oldColumns.slice(0).concat(condArr);
            Gikam.getComp('emc-sample-expand-detail-list-record-data-order-task-result-grid').refreshColumns(newColumns);
            _this.refreshRecordResultData(row);
        });
    },

    refreshRecordResultData : function (row) {
        // 查询数据
        Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/quick-inputs-sample/table-rows/queries', Gikam.getJsonWrapper({
            f: {
                orderTaskId_IN: row.ext$.ids
            }
        })).done(function (data) {
            Gikam.getComp('emc-sample-expand-detail-list-record-data-order-task-result-grid').loadData(data);
        });
    },

    getFolderPanels: function () {
        var _this = this;
        return {
            title: '点位信息',
            id: 'emc-sample-expand-detail-list-folder-panel',
            items: [{
                type: 'layout',
                north: {
                    height : '50%',
                    items: [{
                        type: 'grid',
                        service: 'emcFolderServiceImpl',
                        id: 'emc-sample-expand-detail-list-folder-grid',
                        columns: emcSample.getSamplePrepareFolderColumns(),
                        url: '/secure/emc/module/bp/bp/folders/queries',
                        requestData: {
                            target_filter: JSON.stringify({
                                type: 'assigned',
                                targetTable: "T_EMC_ORDER",
                                targetMatchColumn: "FOLDERID",
                                filter: [{
                                    targetFilterColumn: 'sampleId',
                                    targetFilterValue: _this.param.sampleId
                                }]
                            })
                        },
                        onLoadSuccess : function(data) {
                            if (!Gikam.isEmpty(data)) {
                                this.activeRowByIndex(0);
                            }
                        },
                        onRowActive : function(index, row) {
                            Gikam.getComp('emc-sample-expand-detail-list-folder-uploader').setOptions({
                                bizId :row.id
                            })
                        },
                        toolbar: [{
                            type: 'button',
                            text: 'GIKAM.BUTTON.NEXT',
                            icon: 'move-right',
                            onClick: function () {
                                var target = {};
                                target.value = 'order';
                                _this.onItemClick(target);
                            }
                        }],
                    }]
                },
                center : {
                    title : '点位附件',
                    items: [{
                        type: 'uploader',
                        id: 'emc-sample-expand-detail-list-folder-uploader',
                        dbTable: 'T_EMC_FOLDER'
                    }]
                }
            }]
        }
    },

    getRecordDataPanels: function () {
        var _this = this;
        return {
            id: 'emc-sample-expand-detail-list-record-data-panel',
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.TAB.DATA_INFO',
            show: false,
            items: [{
                type: 'layout',
                west: {
                    width: '35%',
                    items: [_this.getOrderTaskDataGridParam()]
                },
                center: {
                    items: [_this.getOrderTaskResultDataGridParam()]
                }
            }]
        }
    },

    getOriginDataPanels: function () {
        var _this = this;
        return {
            id: 'emc-sample-expand-detail-list-origin-data-panel',
            title: 'EMC.MODULE.BP.RESULT.RUNS.TAB.ORIGINDATA',// 原始数据
            show: false,
            items: [{
                type: 'layout',
                north: {
                    height: '40%',
                    items: [_this.getOrigindataFolderGridParam()]
                },
                center: {
                    items: [_this.getOrigindataResultGridParam()]
                }
            }]
        }
    },

    getSampleFilePanels: function () {
        var _this = this;
        return {
            title: '记录附件',
            id: 'emc-sample-expand-detail-list-sample-file-panel',
            show: false,
            items: [{
                type: 'uploader',
                dbTable: 'T_EMC_SAMPLE',
                bizId: _this.param.sampleId,
                formatterGridToolbar: function (toolbar) {
                    var item = [{
                        type: 'btnGroup',
                        items: [{
                            type: 'button',
                            text: 'GIKAM.BUTTON.PREVIOUS',
                            icon: 'move-left',
                            onClick: function () {
                                var target = {};
                                target.value = 'originData';
                                _this.onItemClick(target);
                            }
                        }, {
                            type: 'button',
                            text: 'GIKAM.BUTTON.NEXT',
                            icon: 'move-right',
                            onClick: function () {
                                var target = {};
                                target.value = 'check';
                                _this.onItemClick(target);
                            }
                        }]
                    }];
                    return item.concat(toolbar);
                }
            }]
        }
    },

    getOrderPanels: function () {
        var _this = this;
        return {
            title: '样品信息',
            id: 'emc-sample-expand-detail-list-order-panel',
            show: false,
            items: [{
                type: 'layout',
                center: {
                    items: [{
                        type: 'grid',
                        service: 'emcOrderServiceImpl',
                        id: 'emc-sample-expand-detail-list-order-grid',
                        columns: emcSample.getDataInputOrderColumns(),
                        url: '/secure/emc/module/bp/bp/orders/queries',
                        requestData: {
                            sampleId_SEQ: _this.param.sampleId
                        },
                        onLoadSuccess: function (data) {
                            if (!Gikam.isEmpty(data)) {
                                this.activeRowByIndex(0);
                            }
                        },
                        onCellClick: function (field, row) {
                            field === 'orderDesc' && _this.loadOrderDescPage(row);
                        },
                        toolbar: [{
                            type: 'button',
                            text: 'GIKAM.BUTTON.PREVIOUS',
                            icon: 'move-left',
                            onClick: function () {
                                var target = {};
                                target.value = 'folder';
                                _this.onItemClick(target);
                            }
                        }, {
                            type: 'button',
                            text: 'GIKAM.BUTTON.NEXT',
                            icon: 'move-right',
                            onClick: function () {
                                var target = {};
                                target.value = 'lableNo';
                                _this.onItemClick(target);
                            }
                        // }, {
                        //     type: 'button',
                        //     text: 'T_EMC_REPORT.BUTTON.MOVE.IN',
                        //     icon: 'move-down',
                        //     onClick: function () {
                        //         _this.movein();
                        //     }
                        // }, {
                        //     type: 'button',
                        //     text: 'T_EMC_REPORT.BUTTON.MOVE.UP',
                        //     icon: 'move-up',
                        //     onClick: function () {
                        //         _this.moveout();
                        //     }
                        }, {
                            type: 'button',
                            text: '批量编辑采样时间',
                            icon: 'edit',
                            onClick: function () {
                                _this.updateSampleTestBatch();
                            },
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.ORDER_DESC_FILL',
                            icon : 'edit',
                            onClick : function() {
                                _this.fillOrderDesc();
                            }
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.ORDER_DESC_SOIL',
                            icon : 'edit',
                            onClick : function() {
                                _this.soilOrderDesc();
                            }
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.INPUT_SOLID_WASTE_ORDER_DESC',
                            icon : 'edit',
                            onClick : function() {
                                _this.solidWasteOrderDesc();
                            }
                        }]
                    }]
                }
            }]
        }
    },

    loadOrderDescPage: function (row) {
        Gikam.create('modal', {
            width: 400,
            height: 300,
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ORDER_DESC_FILL',
            url: emcSample.baseUrl + '/page/order-desc-detail',
            onAfterClose: function (data) {
                if (!Gikam.isEmpty(data)) {
                    workspace.window.showMask(true);
                    row.orderTurbidity = data.orderTurbidity;
                    row.orderColor = data.orderColor;
                    row.orderSmell = data.orderSmell;
                    row.orderOilSlick = data.orderOilSlick;
                    Gikam.postText(emcSample.baseUrl + '/editOrderDesc' , Gikam.getJsonWrapper({}, ['emcOrderServiceImpl', [row]])).done(function () {
                        Gikam.getComp('emc-sample-expand-detail-list-order-grid').refreshRowById(row.id);
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    fillOrderDesc : function() {
        var grid = Gikam.getComp('emc-sample-expand-detail-list-order-grid');
        var rows = grid.getSelections();
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        Gikam.create('modal', {
            width : 600,
            height : 313,
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ORDER_DESC_FILL',
            url : emcSample.baseUrl + '/page/order-desc-detail',
            onAfterClose : function(data) {
                if (!Gikam.isEmpty(data)) {
                    var formData = grid.getActivedRow();
                    workspace.window.showMask(true);
                    var orderDesc = '';
                    if (!Gikam.isEmpty(data.orderColor)) {
                        orderDesc += data.orderColor + '、';
                    }
                    if (!Gikam.isEmpty(data.orderSmell)) {
                        orderDesc += data.orderSmell + '、';
                    }
                    if (!Gikam.isEmpty(data.orderOilSlick)) {
                        orderDesc += data.orderOilSlick + '、';
                    }
                    if (!Gikam.isEmpty(data.orderFloater)) {
                        orderDesc += data.orderFloater + '、';
                    }
                    orderDesc = orderDesc.substring(0, orderDesc.length - 1);
                    rows.map(function(item) {
                        item.orderDesc = orderDesc;
                    });
                    Gikam.put(emcSample.baseUrl + '/' + formData.id, Gikam.getJsonWrapper({}, [ 'emcOrderServiceImpl', rows ])).done(function() {
                        grid.refresh()
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    soilOrderDesc : function() {
        var grid = Gikam.getComp('emc-sample-expand-detail-list-order-grid');
        var rows = grid.getSelections();
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        Gikam.create('modal', {
            width : 600,
            height : 400,
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ORDER_DESC_SOIL',
            url : emcSample.baseUrl + '/page/soli-order-desc-detail',
            onAfterClose : function(data) {
                if (!Gikam.isEmpty(data)) {
                    var formData = grid.getActivedRow();
                    workspace.window.showMask(true);
                    var orderDesc = '';
                    if (!Gikam.isEmpty(data.ext$.color)) {
                        orderDesc += data.ext$.color + '、';
                    }
                    if (!Gikam.isEmpty(data.ext$.texture)) {
                        orderDesc += data.ext$.texture + '、';
                    }
                    if (!Gikam.isEmpty(data.ext$.othermatter)) {
                        orderDesc += data.ext$.othermatter + '、';
                    }
                    if (!Gikam.isEmpty(data.ext$.rootcontent)) {
                        orderDesc += data.ext$.rootcontent + '、';
                    }
                    if (!Gikam.isEmpty(data.ext$.humidity)) {
                        orderDesc += data.ext$.humidity + '、';
                    }
                    orderDesc = orderDesc.substring(0, orderDesc.length - 1);
                    rows.map(function(item) {
                        item.ext$.color = data.ext$.color;
                        item.ext$.texture = data.ext$.texture;
                        item.ext$.othermatter = data.ext$.othermatter;
                        item.ext$.rootcontent = data.ext$.rootcontent;
                        item.ext$.humidity = data.ext$.humidity;
                        item.orderDesc = orderDesc;
                        item.ext$.samplingdepth = data.ext$.samplingdepth;
                    });

                    Gikam.put(emcSample.baseUrl + '/' + formData.id, Gikam.getJsonWrapper({}, [ 'emcOrderServiceImpl', rows ])).done(function() {
                        grid.refresh()
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    solidWasteOrderDesc : function() {
        var grid = Gikam.getComp('emc-sample-expand-detail-list-order-grid');
        var rows = grid.getSelections();
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        var modal = Gikam.create('modal', {
            title: "EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.INPUT_SOLID_WASTE_ORDER_DESC",
            width : 400,
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
                        class : 'blue',
                        onClick : function() {
                            if (form.validate()) {
                                let formData = form.options.data;
                                let orderDesc = [formData.orderType, formData.orderStatusText, formData.color, formData.odor].join(';');
                                console.info(orderDesc)
                                rows.forEach(item => {
                                    item.ext$.samplingdepth = formData.samplingDepth;
                                    item.orderDesc = orderDesc;
                                })

                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(emcSample.baseUrl + '/' + rows[0].id, Gikam.getJsonWrapper({}, [ 'emcOrderServiceImpl', rows ])).done(function() {
                                    grid.refresh()
                                }).always(function() {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close();
                                });
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
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
            fields: [{
                field : 'samplingDepth',
                title : 'T_EMC_ORDER.SAMPLINGDEPTH',
                validators : [ 'notEmpty', 'strLength[0, 60]' ],
            }, {
                field : 'orderType',
                title : 'T_EMC_ORDER.ORDERCATEGORY',
                validators : [ 'notEmpty', 'strLength[0, 60]' ],
            }, {
                field : 'orderStatus',
                title : 'T_EMC_ORDER.ORDERSTATUS',
                validators : [ 'notEmpty', 'strLength[0, 60]' ],
                type : 'select',
                category : 'sys',
                param : {
                    codeCategoryId : 'orderStatus',
                },
                onChange : function(field, value, rowIndex, oldValue, text) {
                    console.info(field, value, rowIndex, oldValue, text)
                    this.setData({
                        orderStatus : value,
                        orderStatusText : text
                    });
                }
            }, {
                field : 'color',
                title : 'T_EMC_ORDER.COLOR',
                validators : [ 'notEmpty', 'strLength[0, 60]' ],
            }, {
                field : 'odor',
                title : 'T_EMC_ORDER.ODOR',
                validators : [ 'notEmpty', 'strLength[0, 60]' ],
            }],
            autoSave: false
        });
    },

    getOrderContainerPanels: function () {
        var _this = this;
        return {
            title: '标签信息',
            id: 'emc-sample-expand-detail-list-lableno-panel',
            show: false,
            items: [{
                type: 'grid',
                service: 'emcOrderContainerServiceImpl',
                id: 'emc-sample-expand-detail-list-lableno-grid',
                columns: emcSample.getContainerColumns(),
                page: true,
                url: '/secure/emc/module/bp/bp/order-tasks/queries/select-order-container-info',
                requestData : {
                    sampleId : _this.param.sampleId
                },
                onLoadSuccess: function (data) {
                    if (!Gikam.isEmpty(data)) {
                        this.activeRowByIndex(0);
                    }
                },
                toolbar: [{
                    type: 'button',
                    text: 'GIKAM.BUTTON.PREVIOUS',
                    icon: 'move-left',
                    onClick: function () {
                        var target = {};
                        target.value = 'order';
                        _this.onItemClick(target);
                    }
                }, {
                    type: 'button',
                    text: 'GIKAM.BUTTON.NEXT',
                    icon: 'move-right',
                    onClick: function () {
                        var target = {};
                        target.value = 'equipt';
                        _this.onItemClick(target);
                    }
                }, {
                    type: 'button',
                    text: 'GIKAM.BUTTON.VIEWLABEL',
                    icon: 'print',
                    onClick: function () {
                        var selections = Gikam.getComp('emc-sample-expand-detail-list-lableno-grid').getSelections();
                        if (Gikam.isEmpty(selections)) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                            return;
                        }
                        var orderIds = '';
                        for (var i = 0; i < selections.length; i++) {
                            orderIds += selections[i].orderId + ',';
                        }
                        if (!Gikam.isEmpty(orderIds)) {
                            orderIds = orderIds.substr(0, orderIds.length - 1);
                        }
                        Gikam.viewReport('PRINT_LABEL_REPORT', selections);
                    }
                }]
            }]
        }
    },

    reset: function () {
        Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.TITLE.RESET', 'EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.RESET', function () {
            var url = '/secure/emc/module/bp/project/folder-indexsets/action/reset';
            var projId = Gikam.getComp('emc-sample-edit-list-sample-grid' + _this.param.idType).getActivedRow().projId;
            workspace.window.showMask(true);
            Gikam.postText(url, Gikam.getJsonWrapper({
                f: {
                    projId: projId
                }
            }, [])).done(function (i) {
                Gikam.getComp('emc-sample-expand-folder-index-grid').refresh();
            }).always(function (i) {
                workspace.window.closeMask();
            });
        });

    },

    getDataCheckPanels: function () {
        var _this = this;
        return {
            id: 'emc-sample-expand-detail-list-check-panel',
            title: '数据检查',
            show: false,
            items: [{
                type: 'grid',
                id: 'emc-sample-expand-detail-list-check-grid',
                columns: emcSample.getSampleDataCheckListColumns(),
                url: IFM_CONTEXT + '/secure/emc/module/bp/sample/sample-reminds/remind/queries',
                requestData: {
                    sampleId: _this.param.sampleId
                },
                readonly: true,
                onLoadSuccess: function (data) {
                    if (!Gikam.isEmpty(data)) {
                        this.activeRowByIndex(0);
                    }
                },
                toolbar: [{
                    type: 'button',
                    text: 'GIKAM.BUTTON.PREVIOUS',
                    icon: 'move-left',
                    onClick: function () {
                        var target = {};
                        target.value = 'file';
                        _this.onItemClick(target);
                    }
                }, {
                    type: 'button',
                    text: 'GIKAM.BUTTON.NEXT',
                    icon: 'move-right',
                    onClick: function () {
                        var target = {};
                        target.value = 'report';
                        _this.onItemClick(target);
                    }
                }]
            }]
        }
    },

    getOrigindataFolderGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-sample-expand-detail-list-origin-data-folder-grid',
            columns: emcSample.getSamplePrepareFolderColumns(),
            url: '/secure/emc/module/bp/bp/folders/queries/undoable',
            service: 'emcFolderServiceImpl',
            requestData: {
                target_filter: JSON.stringify({
                    type: 'assigned',
                    targetTable: "T_EMC_ORDER",
                    targetMatchColumn: "FOLDERID",
                    filter: [{
                        targetFilterColumn: 'sampleId',
                        targetFilterValue: _this.param.sampleId
                    }]
                })
            },
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData(['emc-sample-expand-detail-list-origin-data-order-task-result-grid']);
                }
            },
            onRowActive: function (index, row) {
                _this.onOrigindataFolderActive(row);
            },
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.PREVIOUS',
                icon: 'move-left',
                onClick: function () {
                    var target = {};
                    target.value = 'data';
                    _this.onItemClick(target);
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.NEXT',
                icon: 'move-right',
                onClick: function () {
                    var target = {};
                    target.value = 'file';
                    _this.onItemClick(target);
                }
            }]
        }
    },

    onOrigindataFolderActive: function (row) {
        var _this = this;
        if (row) {
            Gikam.getAsyncComp('emc-sample-expand-detail-list-origin-data-order-task-result-grid').done(function (grid) {
                grid.refresh({
                    url: '/secure/emc/module/bp/bp/order-task-results/queries',
                    requestData: {
                        folderId_SEQ: row.id,
                        analysisType_SEQ: 'sampling',
                        target_filter: JSON.stringify({
                            type: 'assigned',
                            targetTable: "T_EMC_ORDER",
                            targetMatchColumn: "ID",
                            thisMatchColumn: "ORDERID",
                            filter: [{
                                targetFilterColumn: 'sampleId',
                                targetFilterValue: _this.param.sampleId
                            }]
                        })
                    }
                });
            });
        }
    },

    getOrigindataResultGridParam: function () {
        var _this = this;
        return {
            type: 'tab',
            panels: [{
                title: 'EMC.MODULE.BP.BP.FOLDERS.ORDERS.MODAL.RESULT',
                items: [ {
                    type : 'grid',
                    id : this.idGrid.resultGridId,
                    service : 'emcOrderTaskResultServiceImpl',
                    columns : emcSample.getOrderTaskResultColumns(this.param),
                    page : true,
                    pageSize : 100,
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(1);
                        }
                    },
                    onRendered : function() {
                        var formData = Gikam.getComp('emc-order-task-result-edit-list-button-group-by-test');
                        if (formData != undefined) {
                            formData.clickHandle();
                        }
                    },
                    beforeUpdate : function(data) {
                        if (!Gikam.isEmpty(data.originalResult) && data.row && data.row.submitFlag == 1) {
                            Gikam.create('modal', {
                                width : 400,
                                height : 200,
                                title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.TITLE.UPDATE_REASON',
                                url : '/secure/emc/module/bp/bp/order-tasks/page/update-result-reason',
                                onAfterClose : function(reasonData) {
                                    if (!Gikam.isEmpty(reasonData)) {
                                        workspace.window.showMask(true);
                                        var resultLog = {
                                            orderTaskId : data.row.orderTaskId,
                                            resultId : data.row.id,
                                            reason : reasonData.reason,
                                            value : data.originalResult,
                                        };
                                        Gikam.post(Gikam.printf('/secure/emc/module/bp/bp/order-tasks/{id}/result-logs', {
                                            id : data.row.orderTaskId
                                        }), Gikam.getJsonWrapper({
                                            f : {
                                                folderId : data.row.folderId,
                                            }
                                        }, [ '', [ resultLog ] ])).done(function() {
                                            Gikam.getComp(_this.idGrid.resultGridId).refresh();
                                        }).always(function() {
                                            workspace.window.closeMask();
                                        });
                                    } else {
                                        Gikam.getComp(_this.idGrid.resultGridId).refresh();
                                    }
                                }
                            });
                            return false;
                        }
                    },
                    onUpdated : function(data, fields, index) {
                        var grid = this;

                        var folderRow = Gikam.getComp('emc-sample-data-input-detail-list-grid').getActivedRow();

                        if (fields.indexOf('originalResult') > -1 || fields.indexOf('standQty') > -1 || fields.indexOf('lowLimit') > -1 || fields.indexOf('highLimit') > -1 || fields.indexOf('revisionRule') > -1) {

                            Gikam.getLastModal().window.showMask(true);
                            var orderTaskResultUrl = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results';

                            Gikam.put(orderTaskResultUrl + '/action/change-result', Gikam.getJsonWrapper({}, [ '', [ data ] ])).done(function() {
                                Gikam.post(orderTaskResultUrl + '/queries', Gikam.getJsonWrapper({
                                    n : -1,
                                    f : {
                                        folderId_SEQ : folderRow.id,
                                        analysisType_SEQ : 'sampling',
                                        targetType : 'samplingInput',
                                        target_filter : JSON.stringify({
                                            type : 'assigned',
                                            targetTable : "T_EMC_ORDER",
                                            targetMatchColumn : "ID",
                                            thisMatchColumn : "ORDERID",
                                            filter : [ {
                                                targetFilterColumn : 'sampleId',
                                                targetFilterValue : _this.param.id
                                            } ]
                                        })
                                    }
                                })).done(function(data) {

                                    grid.refreshByData(data, true);

                                }).always(function() {
                                    Gikam.getLastModal().window.closeMask();
                                });

                            }).fail(function() {
                                Gikam.getLastModal().window.closeMask();
                            });
                        }
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.GROUP_BY_ORDER',
                        id : 'emc-order-task-result-edit-list-button-group-by-order',
                        icon : 'edit',
                        onClick : function() {
                            var grid = Gikam.getComp(_this.idGrid.resultGridId);
                            grid.refreshGroup({
                                fields : [ 'orderNo' ],
                                formatter : function(row) {
                                    return row.orderNo;
                                }
                            });
                            Gikam.getComp('emc-order-task-result-edit-list-button-group-by-test').show();
                            Gikam.getComp('emc-order-task-result-edit-list-button-group-by-order').hide();
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.GROUP_BY_TEST',
                        id : 'emc-order-task-result-edit-list-button-group-by-test',
                        icon : 'edit',
                        onClick : function() {
                            var grid = Gikam.getComp(_this.idGrid.resultGridId);
                            grid.refreshGroup({
                                fields : [ 'ext$.testname' ],
                                formatter : function(row) {
                                    return row.ext$.testname;
                                }
                            });
                            Gikam.getComp('emc-order-task-result-edit-list-button-group-by-test').hide();
                            Gikam.getComp('emc-order-task-result-edit-list-button-group-by-order').show();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.BATCH_INPUT',
                        icon : 'add',
                        onClick : function() {
                            _this.batchInputResult();
                        }
                    }, {
                        type : 'dropDownMenu',
                        text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.MORE_OPERATE',
                        items : [ {
                            type : 'button',
                            text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SYNC_TMPL_INFO',
                            icon : 'edit',
                            onClick : function() {
                                Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SYNC_TMPL_INFO', 'EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.SYNC_TMPL_INFO', function() {
                                    _this.syncTmplInfo();
                                });
                            },
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.DEFAULT_RESULT',
                            icon : 'edit',
                            onClick : function() {
                                Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.DEFAULT_RESULT', 'EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.DEFAULT_RESULT', function() {
                                    _this.orderTaskDefaultResult();
                                });
                            },
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SET_FINISHED',
                            icon : 'edit',
                            onClick : function() {
                                _this.setDoneOrderTaskResult();
                            }
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.REMOVE_FOMULA',
                            icon : 'edit',
                            onClick : function() {
                                Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.REMOVE_FOMULA', 'EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.REMOVE_FOMULA', function() {
                                    _this.removeFomula();
                                });
                            }
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.ADD_REPEATE_NUMBER',
                            icon : 'add',
                            onClick : function() {
                                _this.addRepeateNumber();
                            }
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.DEL_REPEATE_NUMBER',
                            icon : 'remove-row',
                            onClick : function() {
                                Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.DEL_REPEATE_NUMBER', 'EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.DEL_REPEATE_NUMBER', function() {
                                    _this.deleteRepeateNumber();
                                });
                            }
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.EDIT_ITEM',
                            icon : 'edit',
                            onClick : function() {
                                _this.editItem();
                            }
                        } ]
                    } ],
                }]
            }]
        }
    },

    /** 批录入结果 */
    batchInputResult : function() {
        var _this = this;
        var selections = Gikam.getComp(_this.idGrid.resultGridId).getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        var folderRow = Gikam.getComp('emc-sample-data-input-detail-list-grid').getActivedRow();

        var orderTaskIds = selections.map(function (item) {
            return item.orderTaskId;
        });
        var uniqueOrderTaskIds = Array.from(new Set(orderTaskIds));

        Gikam.create('modal', {
            width : '60%',
            height : '70%',
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.TITLE.CHOOSE_ORDER_TASK',
            url : emcSample.baseUrl + '/page/order-result-batch-input' + Gikam.param({
                folderId_SEQ : folderRow.id,
                analysisType_SEQ : 'sampling',
                id_DIFFER : uniqueOrderTaskIds.join()
            }),
            onAfterClose : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getLastModal().window.showMask(true);
                    Gikam.postText(emcSample.baseUrl + '/order-tasks/action/batch-input', Gikam.getJsonWrapper({}, [ null, selections ], [ null, data ])).done(function() {
                        Gikam.getComp(_this.idGrid.resultGridId).refresh();
                    }).always(function() {
                        Gikam.getLastModal().window.closeMask();
                    });
                }
            }
        });
    },

    syncTmplInfo : function() {
        var _this = this;
        var selections = Gikam.getComp(_this.idGrid.resultGridId).getSelections();
        if (selections.length == 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }
        selections.map(function(item) {
            item.ext$.analysisType = 'sampling'
        });
        workspace.window.showMask(true);
        Gikam.postText('/secure/emc/module/bp/bp/orders/action/sync-tmpl-infos', Gikam.getJsonWrapper({}, [ null, selections ])).done(function() {
            Gikam.each(selections, function(index, item) {
                Gikam.getComp('emc-order-task-result-edit-list-grid').refreshRowById(item.id);
            });
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    orderTaskDefaultResult : function() {
        var _this = this;
        var selections = Gikam.getComp(_this.idGrid.resultGridId).getSelections();
        if (selections.length == 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        var dataInputSelections = Gikam.getComp('emc-sample-data-input-detail-list-grid').getSelections();
        if (dataInputSelections.length == 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        var url = '/secure/emc/module/bp/bp/orders/' + _this.param.id + '/action/update';

        var id = selections.foreach;
        workspace.window.showMask(true);
        Gikam.postText(url, Gikam.getJsonWrapper(null, [ 'emcFolderServiceImpl', dataInputSelections ], [ 'emcOrderTaskResultServiceImpl', selections ])).done(function() {
            Gikam.each(selections, function(index, item) {
                Gikam.getComp(_this.idGrid.resultGridId).refresh();
            });
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    setDoneOrderTaskResult : function() {
        var _this = this;
        Gikam.create('modal', {
            width : '20%',
            height : '30%',
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.INVALID_DATA',
            url : emcSample.baseUrl + '/page/invalid-results',
            onAfterClose : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getLastModal().window.showMask(true);
                    if (data.operatedata == undefined) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_OPERATE_DATA');
                        return;
                    }

                    if (data.operatedata == 'line') {
                        var resultSelections = Gikam.getComp(_this.idGrid.resultGridId).getSelections();
                        if (Gikam.isEmpty(resultSelections)) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                            return;
                        }

                        resultSelections.forEach(function(item) {
                            item.ext$.reason = data.reason;
                        });

                        Gikam.put('/secure/emc/module/bp/bp/orders/results/action/set-done', Gikam.getJsonWrapper({}, [ null, resultSelections ])).done(function() {
                            Gikam.getComp(_this.idGrid.resultGridId).refresh();
                            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.UPDATE_SUCCESS');
                        }).always(function() {
                            Gikam.getLastModal().window.closeMask();
                        });
                    } else if (data.operatedata == 'noresult') {

                        var orderSelections = Gikam.getComp('emc-sample-data-input-detail-list-grid').getSelections();
                        if (Gikam.isEmpty(orderSelections)) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                            return;
                        }

                        orderSelections.forEach(function(item) {
                            item.ext$.reason = data.reason;
                            item.ext$.sampleid = _this.param.id;
                        });
                        Gikam.put('/secure/emc/module/bp/bp/orders/folders/action/set-done', Gikam.getJsonWrapper({}, [ null, orderSelections ])).done(function() {
                            Gikam.getComp(_this.idGrid.resultGridId).refresh();
                            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.UPDATE_SUCCESS');
                        }).always(function() {
                            Gikam.getLastModal().window.closeMask();
                        });
                    }
                }
            }
        });
    },

    removeFomula : function() {
        var _this = this;
        var selections = Gikam.getComp(_this.idGrid.resultGridId).getSelections();
        if (selections.length == 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }
        workspace.window.showMask(true);
        Gikam.postText('/secure/emc/module/bp/bp/orders/action/remove-fomulas', Gikam.getJsonWrapper({}, [ null, selections ])).done(function() {
            Gikam.getComp(_this.idGrid.resultGridId).refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    addRepeateNumber : function() {
        var _this = this;
        var selections = Gikam.getComp(_this.idGrid.resultGridId).getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            width : '20%',
            height : '20%',
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.DATA_INPUT_TASK_RESULT_SET_REPEATENO',
            url : emcSample.baseUrl + '/page/repeat-number-set',
            onAfterClose : function(data) {
                if (!Gikam.isEmpty(data)) {
                    workspace.window.showMask(true);
                    selections.map(function(item) {
                        item.repeatNo = data.repeatNo
                    });
                    Gikam.postText('/secure/emc/module/bp/bp/orders/' + selections[0].id + '/action/add-repreat-nos', Gikam.getJsonWrapper({}, [ null, selections ])).done(function() {
                        Gikam.getComp(_this.idGrid.resultGridId).refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    editItem : function() {
        var _this = this;
        var data = Gikam.getComp(_this.idGrid.resultGridId).getActivedRow();
        if (Gikam.isEmpty(data)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.EDIT_ITEM',
            url : emcSample.baseUrl + '/page/edit-item' + Gikam.param({
                id : data.orderTaskId,
            }),
            onAfterClose : function(afterData) {
                if (Gikam.isEmpty(afterData[0]) || Gikam.isEmpty(afterData[1])) {
                    return;
                }
                workspace.window.showMask(true);
                Gikam.post(emcSample.baseUrl + '/edit-item', Gikam.getJsonWrapper({
                    f : {
                        sampleId : _this.param.id
                    }
                }, [ 'emcOrderTaskResultServiceImpl', [ data ] ], [ 'emcItemServiceImpl', afterData[0] ], [ 'emcFolderServiceImpl', afterData[1] ])).done(function() {
                    Gikam.getComp(_this.idGrid.resultGridId).refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        })
    },

    deleteRepeateNumber : function() {
        var _this = this;
        var selections = Gikam.getComp(_this.idGrid.resultGridId).getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        for(var i=0;i<selections.length;i++){
            if (selections[i].repeatNo == 1) {
                Gikam.alert('GIKAM.TIP.CHOOSE_REPEAT_NO_DATA_GREATER_THAN_ONE');
                return;
            }
        }
        workspace.window.showMask(true);
        Gikam.postText('/secure/emc/module/bp/bp/orders/action/delete-repreat-nos', Gikam.getJsonWrapper({}, [ null, selections ])).done(function() {
            Gikam.getComp(_this.idGrid.resultGridId).refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    InvalIdData: function () {
        var _this = this;
        var orderTaskList = [];
        var grid = Gikam.getComp('emc-sample-expand-detail-list-record-data-order-task-result-grid');
        var selections = grid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            width: '20%',
            height: '30%',
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.INVALID_DATA',
            url: IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/page/invalid-results',
            onAfterClose: function (data) {
                if (!Gikam.isEmpty(data)) {
                    if (data.operatedata == undefined) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_OPERATE_DATA');
                        return;
                    }
                    if (data.operatedata == 'line') {
                        orderTaskList = selections;
                    } else if (data.operatedata == 'noresult') {
                        orderTaskList = grid.getData();
                    }
                    workspace.window.showMask(true);
                    Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/sample/action/set-done', Gikam.getJsonWrapper({}, ['', orderTaskList])).done(function () {
                        var activedRow = Gikam.getComp('emc-sample-expand-detail-list-record-data-order-task-grid').getActivedRow();
                        _this.refreshRecordResultData(activedRow);
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    syncTmplInfo: function () {
        var _this = this;
        var allData = Gikam.getComp('emc-sample-expand-detail-list-record-data-order-task-result-grid').getData();
        if (allData.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }
        workspace.window.showMask(true);
        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/action/sample-sync-tmpl-infos', Gikam.getJsonWrapper({}, [null, allData])).done(function () {
            var activedRow = Gikam.getComp('emc-sample-expand-detail-list-record-data-order-task-grid').getActivedRow()
            _this.refreshRecordResultData(activedRow);
        }).always(function () {
            workspace.window.closeMask();
        });
    },


    editItem: function () {
        var _this = this;
        var selections = Gikam.getComp('emc-sample-expand-detail-list-origin-data-order-task-result-grid').getSelections();
        if (selections.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var data = selections[0];
        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.EDIT_ITEM',
            url: emcSample.baseUrl + '/page/edit-item' + Gikam.param({
                id: data.orderTaskId,
            }),
            onAfterClose: function (afterData) {
                if (Gikam.isEmpty(afterData) || Gikam.isEmpty(afterData[0]) || Gikam.isEmpty(afterData[1])) {
                    return;
                }
                workspace.window.showMask(true);
                Gikam.post(emcSample.baseUrl + '/edit-item', Gikam.getJsonWrapper({
                    f: {
                        sampleId: _this.param.id
                    }
                }, ['emcOrderTaskResultServiceImpl', [data]], ['emcItemServiceImpl', afterData[0]], ['emcFolderServiceImpl', afterData[1]])).done(function () {
                    Gikam.getComp('emc-sample-expand-detail-list-origin-data-order-task-result-grid').refresh();
                }).always(function () {
                    workspace.window.closeMask();
                });
            }
        })
    },

    addRepeateNumber: function () {
        var _this = this;
        var selections = Gikam.getComp('emc-sample-expand-detail-list-origin-data-order-task-result-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            width: '20%',
            height: '20%',
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.DATA_INPUT_TASK_RESULT_SET_REPEATENO',
            url: emcSample.baseUrl + '/page/repeat-number-set',
            onAfterClose: function (data) {
                if (!Gikam.isEmpty(data)) {
                    workspace.window.showMask(true);
                    selections.map(function (item) {
                        item.repeatNo = data.repeatNo
                    });
                    Gikam.postText('/secure/emc/module/bp/bp/orders/' + selections[0].id + '/action/add-repreat-nos', Gikam.getJsonWrapper({}, [null, selections])).done(function () {
                        Gikam.getComp('emc-sample-expand-detail-list-origin-data-order-task-result-grid').refresh();
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    deleteRepeateNumber: function () {
        var _this = this;
        var selections = Gikam.getComp('emc-sample-expand-detail-list-origin-data-order-task-result-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        for (var i = 0; i < selections.length; i++) {
            if (selections[i].repeatNo == 1) {
                Gikam.alert('GIKAM.TIP.CHOOSE_REPEAT_NO_DATA_GREATER_THAN_ONE');
                return;
            }
        }
        workspace.window.showMask(true);
        Gikam.postText('/secure/emc/module/bp/bp/orders/action/delete-repreat-nos', Gikam.getJsonWrapper({}, [null, selections])).done(function () {
            Gikam.getComp('emc-sample-expand-detail-list-origin-data-order-task-result-grid').refresh();
        }).always(function () {
            workspace.window.closeMask();
        });
    },

    getBrowseRecordsProjRcdCategoryGrid: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-sample-expand-detail-list-report-grid',
            columns: emcSample.getBrowseRecordsProjRcdCategoryColumns(),
            url: emcSample.baseUrl + '/queries',
            requestData: {
                id_EQ: _this.param.sampleId,
            },
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp(iframeId).refresh({
                        src: ""
                    })
                }
            },
            onRowActive: function (index, row) {
                var reportConfigLineId = row.reportConfigLineId;
                if (reportConfigLineId) {
                    Gikam.getAsyncComp('emc-sample-expand-detail-list-report-iframe').done(function (frame) {
                        frame.refresh({
                            src: Gikam.getReportUrl(reportConfigLineId, [row])
                        })
                    });
                } else {
                    Gikam.getAsyncComp('emc-sample-expand-detail-list-report-iframe').done(function (frame) {
                        frame.refresh({
                            src: ''
                        });
                    });
                }
            },
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.PREVIOUS',
                icon: 'move-left',
                onClick: function () {
                    var target = {};
                    target.value = 'check';
                    _this.onItemClick(target);
                }
            }, {
                type: 'button',
                text: 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon: 'pass',
                onClick: function () {
                    var grid = Gikam.getComp('emc-sample-expand-detail-list-report-grid');
                    var selections = grid.getSelections();
                    if (selections.length < 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    Gikam.create('workflow').submit({
                        data: grid.getSelections(),
                        pageObject: emcSample
                    }).done(function () {
                        Gikam.getLastModal().close();
                    })
                }
            }]
        }
    },

    tabPanels: function () {
        var _this = this;
        return [
            _this.getFolderPanels(),
            _this.getOrderPanels(),
            _this.getOrderContainerPanels(),
            _this.getEquiptPanels(),
            _this.getRecordDataPanels(),//记录数据
            _this.getOriginDataPanels(),//原始数据
            _this.getSampleFilePanels(),//记录附件
            _this.getDataCheckPanels(),//数据检查
            {//浏览记录
                title: '浏览记录',
                id: 'emc-sample-expand-detail-list-report-panel',
                show: false,
                items: [{
                    type: 'layout',
                    west: {
                        width: '20%',
                        items: [_this.getBrowseRecordsProjRcdCategoryGrid()]
                    },
                    center: {
                        title: 'EMC.MODULE.BP.RESULT.RUNS.TAB.BROWSE_RECORDS_DETAIL',
                        items: [{
                            type: 'iframe',
                            id: 'emc-sample-expand-detail-list-report-iframe',
                            src: ''
                        }]
                    }
                }]
            }];
    },

    createPage: function () {
        var _this = this;
        Gikam.create('layout', {
            renderTo: Gikam.getLastModal().window.$dom,
            readonly: _this.param.readonly,
            north: {
                height: 50,
                items: [_this.getSampleExpandNext()]
            },
            resize: false,
            center: {
                items: [{
                    type: 'tab',
                    id: 'emc-sample-expand-detail-tab',
                    panels: _this.tabPanels(),
                    onActived: function (data) {
                        if (data.title === '记录数据') {
                            Gikam.getComp('emc-sample-expand-detail-list-record-data-order-task-grid').refresh();
                        }
                        if (data.title === '原始数据') {
                            Gikam.getComp('emc-sample-expand-detail-list-origin-data-folder-grid').refresh();
                        }
                        if (data.title === '数据检查') {
                            Gikam.getComp('emc-sample-expand-detail-list-check-grid').refresh();
                        }
                    }
                }]
            },
        });
    },

    idGrid : {
        resultGridId : 'emc-sample-expand-detail-list-origin-data-order-task-result-grid',
    },

    init: function (param) {
        var _this = this;
        _this.param = param;
        console.info(param)
        _this.createPage();
    },

}
