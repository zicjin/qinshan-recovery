emcSample.expandDetailPage = {

    getSampleExpandNext: function () {
        var _this = this;
        return {
            type: 'steps',
            id: 'emc-sample-expand-next',
            // 展示的节点集合，节点格式为
            items: [
                {text: '采样单信息', value: 'sample', status: "next"},
                {text: '点位信息', value: 'folder', status: "wait"},
                {text: '样品信息', value: 'order', status: "wait"},
                {text: '容器标签信息', value: 'lableNo', status: "wait"},
                {text: '仪器信息', value: 'equipt', status: "wait"},
                {text: '仪器校准信息', value: 'equiptSet', status: "wait"},
                {text: '记录数据', value: 'data', status: "wait"},
                {text: '原始数据', value: 'originData', status: "wait"},
                {text: '质控数据', value: 'qcData', status: "wait"},
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
                {text: '采样单信息', value: 'sample', status: "next"},
                {text: '点位信息', value: 'folder', status: "wait"},
                {text: '样品信息', value: 'order', status: "wait"},
                {text: '容器标签信息', value: 'lableNo', status: "wait"},
                {text: '仪器信息', value: 'equipt', status: "wait"},
                {text: '仪器校准信息', value: 'equiptSet', status: "wait"},
                {text: '记录数据', value: 'data', status: "wait"},
                {text: '原始数据', value: 'originData', status: "wait"},
                {text: '质控数据', value: 'qcData', status: "wait"},
                {text: '记录附件', value: 'file', status: "wait"},
                {text: '数据检查', value: 'check', status: "wait"},
                {text: '浏览记录', value: 'report', status: "wait"}
            ],
            panelIds = [
                'emc-sample-expand-detail-list-sample-panel',
                'emc-sample-expand-detail-list-folder-panel',
                'emc-sample-expand-detail-list-order-panel',
                'emc-sample-expand-detail-list-lableno-panel',
                'emc-sample-expand-detail-list-equipt-panel',
                'emc-sample-expand-detail-list-equipt-set-panel',
                'emc-sample-expand-detail-list-record-data-panel',
                'emc-sample-expand-detail-list-origin-data-panel',
                'emc-sample-expand-detail-list-qc-data-panel',
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
            if (5 === index) {
                tab.revertPanel('emc-sample-expand-detail-list-gas-equipt-set-panel');
                tab.revertPanel('emc-sample-expand-detail-list-sw-equipt-set-panel');
                tab.revertPanel('emc-sample-expand-detail-list-temp-equipt-set-panel');
                tab.showPanel('emc-sample-expand-detail-list-gas-equipt-set-panel');
            } else {
                tab.removePanel('emc-sample-expand-detail-list-gas-equipt-set-panel');
                tab.removePanel('emc-sample-expand-detail-list-sw-equipt-set-panel');
                tab.removePanel('emc-sample-expand-detail-list-temp-equipt-set-panel');
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

    getEquiptSetPanels: function () {
        var _this = this;
        return {
            title: '水质仪器校准信息',
            id: 'emc-sample-expand-detail-list-equipt-set-panel',
            show: false,
            items: [{
                type: 'grid',
                service: 'emcEquiptSetServiceImpl',
                id: 'emc-sample-expand-detail-list-equipt-set-grid',
                dbTable: 'T_EMC_EQUIPT_SET',
                columns: emcSample.getOrderTaskEquiptSetColumns(),
                url: '/secure/emc/module/bp/equipt-sets/queries',
                requestData: {
                    sampleId_SEQ: _this.param.sampleId,
                },
                order: {
                    'sortno': 'asc'
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
                    text: 'pH校准',
                    icon: 'edit',
                    onClick: function () {
                        _this.addEquiptSet('pH值', ['混合磷酸盐', '邻苯二甲酸氢钾', '硼砂', '混合磷酸盐']);
                    }
                }, {
                    type: 'button',
                    text: '电导率校准',
                    icon: 'edit',
                    onClick: function () {
                        _this.addEquiptSet('电导率', ['/']);
                    }
                }, {
                    type: 'button',
                    text: '氧化还原电位校准',
                    icon: 'edit',
                    onClick: function () {
                        _this.addEquiptSet('氧化还原电位', ['/']);
                    }
                }, {
                    type: 'button',
                    text: '删除',
                    icon: 'remove',
                    onClick: function () {
                        Gikam.getComp('emc-sample-expand-detail-list-equipt-set-grid').deleteRows(IFM_CONTEXT + '/secure/emc/module/bp/equipt-sets');
                    }
                },],
            }]
        }
    },

    getGasEquiptSetPanels: function () {
        var _this = this;
        return {
            title: '废气仪器校准信息',
            id: 'emc-sample-expand-detail-list-gas-equipt-set-panel',
            show: false,
            items: [{
                type: 'tab',
                id: 'emc-gas-equipt-set-tab',
                panels: [_this.getEquiptFlowSetPanels(), _this.getEquiptStandardSetPanels(), _this.getEquiptDioxinSetPanels(), _this.getEquiptDioxinSetPanels2()]
            }]
        }
    },

    getTemperatureSetPanels: function () {
        var _this = this;
        return {
            title: '温度传感器性能核查',
            id: 'emc-sample-expand-detail-list-temp-equipt-set-panel',
            show: false,
            items: [{
                type: 'grid',
                service: 'emcEquiptTempSetServiceImpl',
                id: 'emc-sample-expand-detail-list-temp-equipt-set-grid',
                columns: emcSample.getOrderTaskTemperatureEquiptSetColumns(),
                url: '/secure/emc/module/bp/equipt-temp-sets/queries',
                requestData: {
                    sampleId_SEQ: _this.param.sampleId,
                },
                page: true,
                onLoadSuccess: function (data) {
                    if (!Gikam.isEmpty(data)) {
                        // this.activeRowByIndex(0);
                    }
                },
                toolbar: [{
                    type: 'button',
                    text: '添加',
                    icon: 'add',
                    onClick: function () {
                        Gikam.getComp('emc-sample-expand-detail-list-temp-equipt-set-grid').insert('/secure/emc/module/bp/equipt-temp-sets', [{sampleId: _this.param.sampleId}]);
                    }
                }, {
                    type: 'button',
                    text: '删除',
                    icon: 'remove',
                    onClick: function () {
                        Gikam.getComp('emc-sample-expand-detail-list-temp-equipt-set-grid').deleteRows(IFM_CONTEXT + '/secure/emc/module/bp/equipt-temp-sets');
                    }
                }, {
                    type: 'button',
                    text: '修改仪器',
                    icon: 'add',
                    onClick: function () {
                        var selections = Gikam.getComp('emc-sample-expand-detail-list-temp-equipt-set-grid').getSelections();
                        if (selections.length !== 1) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                            return;
                        }
                        Gikam.create('modal', {
                            title: Gikam.choose.getConfig('equipt').title,
                            url: Gikam.choose.getConfig('equipt').url + Gikam.param({
                                // single: true,
                                equiptRequisitionFilterProjId: _this.param.projId
                            }),
                            onAfterClose: function (chooseData) {
                                if (Gikam.isEmpty(chooseData)) {
                                    return;
                                }

                                var equiptIds = [];
                                var equiptNos = [];
                                var equiptNames = [];
                                Gikam.each(chooseData, function(index, item) {
                                    equiptIds.push(item.id);
                                    equiptNos.push(item.equiptId);
                                    equiptNames.push(item.equiptName);
                                });

                                var saveData = selections.map(function(row) {
                                    return {
                                        id : row.id,
                                        equiptId: equiptIds.join(','),
                                        equiptNo: equiptNos.join(','),
                                        equiptName: equiptNames.join(','),
                                    };
                                });

                                workspace.window.showMask(true);
                                Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcEquiptTempSetServiceImpl', saveData ])).done(function(id) {
                                    Gikam.getComp('emc-sample-expand-detail-list-temp-equipt-set-grid').refresh();
                                }).always(function() {
                                    workspace.window.closeMask();
                                });
                            }
                        })
                    }
                },],
            }]
        }
    },
    getSwEquiptSetPanels: function () {
        var _this = this;
        return {
            title: '水温仪器校准信息',
            id: 'emc-sample-expand-detail-list-sw-equipt-set-panel',
            show: false,
            items: [{
                type: 'grid',
                service: 'emcEquiptSwSetServiceImpl',
                id: 'emc-sample-expand-detail-list-sw-equipt-set-grid',
                columns: emcSample.getOrderTaskSwEquiptSetColumns(),
                url: '/secure/emc/module/bp/equipt-sw-sets/queries',
                requestData: {
                    sampleId_SEQ: _this.param.sampleId,
                },
                page: true,
                onLoadSuccess: function (data) {
                    if (!Gikam.isEmpty(data)) {
                        // this.activeRowByIndex(0);
                    }
                },
                toolbar: [{
                    type: 'button',
                    text: '添加',
                    icon: 'add',
                    onClick: function () {

                        Gikam.create('modal', {
                            title: Gikam.choose.getConfig('order').title,
                            url: Gikam.choose.getConfig('order').url + Gikam.param({
                                sampleId_seq: _this.param.sampleId,
                            }),
                            onAfterClose: function (rows, index) {
                                if (Gikam.isEmpty(rows)) {
                                    return;
                                }

                                var datas = rows.map(function (item) {
                                    return {
                                        sampleId: _this.param.sampleId,
                                        orderId:item.id,
                                        orderNo:item.orderNo,
                                        measureSafe: '1',
                                        checkSafe: '1',
                                    };
                                });
                                Gikam.getComp('emc-sample-expand-detail-list-sw-equipt-set-grid').insert('/secure/emc/module/bp/equipt-sw-sets', datas);
                            }
                        });
                    }
                }, {
                    type: 'button',
                    text: '删除',
                    icon: 'remove',
                    onClick: function () {
                        Gikam.getComp('emc-sample-expand-detail-list-sw-equipt-set-grid').deleteRows(IFM_CONTEXT + '/secure/emc/module/bp/equipt-sw-sets');
                    }
                },],
            }]
        }
    },

    getEquiptFlowSetPanels: function () {
        var _this = this;
        return {
            title: '流量校准',
            id: 'emc-sample-expand-detail-list-equipt-flow-set-panel',
            items: [{
                type: 'layout',
                north: {
                    height: '50%',
                    items: [{
                        type: 'grid',
                        service: 'emcEquiptFlowSetServiceImpl',
                        id: 'emc-sample-expand-detail-list-equipt-flow-set-grid',
                        dbTable: 'T_EMC_EQUIPT_FLOW_SET',
                        columns: emcSample.getFlowSetListColumns(),
                        url: '/secure/emc/module/bp/equipt-flow-sets/queries',
                        requestData: {
                            sampleId_SEQ: _this.param.sampleId,
                            bizType_EQ: '1'
                        },
                        page: true,
                        onLoadSuccess: function (data) {
                            if (!Gikam.isEmpty(data)) {
                                this.activeRowByIndex(0);
                            }
                        },
                        toolbar: [{
                            type: 'button',
                            text: '添加',
                            icon: 'add',
                            onClick: function () {
                                Gikam.create('modal', {
                                    title: Gikam.choose.getConfig('equipt').title,
                                    url: Gikam.choose.getConfig('equipt').url + Gikam.param({
                                        equiptRequisitionFilterProjId: _this.param.projId
                                    }),
                                    onAfterClose: function (data) {
                                        if (Gikam.isEmpty(data)) {
                                            return;
                                        }
                                        var chooseData = data.map(function (item) {
                                            return {
                                                sampleId: _this.param.sampleId,
                                                equiptId: item.id,
                                                equiptNo: item.equiptId,
                                                equiptName: item.equiptName,
                                                specification: item.specification,
                                                bizType: '1'
                                            }
                                        })
                                        Gikam.getComp('emc-sample-expand-detail-list-equipt-flow-set-grid').insert('/secure/emc/module/bp/equipt-flow-sets', chooseData);
                                    }
                                })
                            }
                        }, {
                            type: 'button',
                            text: '删除',
                            icon: 'remove',
                            onClick: function () {
                                Gikam.getComp('emc-sample-expand-detail-list-equipt-flow-set-grid').deleteRows('/secure/emc/module/bp/equipt-flow-sets');
                            }
                        },],
                    }]
                },
                center: {
                    title: '仪器校准信息',
                    items: [{
                        type: 'grid',
                        service: 'emcEquiptFlowSetServiceImpl',
                        id: 'emc-sample-expand-detail-list-equipt-flow-set-msg-grid',
                        dbTable: 'T_EMC_EQUIPT_FLOW_SET',
                        columns: emcSample.getFlowSetMsgListColumns(),
                        url: '/secure/emc/module/bp/equipt-flow-sets/queries',
                        requestData: {
                            sampleId_SEQ: _this.param.sampleId,
                            bizType_EQ: '2'
                        },
                        page: true,
                        onLoadSuccess: function (data) {
                            if (!Gikam.isEmpty(data)) {
                                this.activeRowByIndex(0);
                            }
                        },
                        onUpdated: function (data, fields, index) {
                            if (fields.indexOf('beforeSetFlow') > -1 || fields.indexOf('beforeSampleFlow') > -1 || fields.indexOf('afterSetFlow') > -1 || fields.indexOf('afterSampleFlow') > -1) {
                                var row = Gikam.getComp('emc-sample-expand-detail-list-equipt-flow-set-msg-grid').getActivedRow();
                                if (!Gikam.isEmpty(row.beforeSetFlow) && !Gikam.isEmpty(row.beforeSampleFlow) && row.beforeSampleFlow != 0) {
                                    var beforeError = (parseFloat(row.beforeSetFlow) - parseFloat(row.beforeSampleFlow)) / parseFloat(row.beforeSampleFlow) * 100;
                                    beforeError = beforeError.toFixed(1);
                                    this.setData({
                                        index: row.gridIndex,
                                        beforeError: beforeError
                                    })
                                }
                                if (!Gikam.isEmpty(row.afterSetFlow) && !Gikam.isEmpty(row.afterSampleFlow) && row.afterSampleFlow != 0) {
                                    var afterError = (parseFloat(row.afterSetFlow) - parseFloat(row.afterSampleFlow)) / parseFloat(row.afterSampleFlow) * 100;
                                    afterError = afterError.toFixed(1);
                                    this.setData({
                                        index: row.gridIndex,
                                        afterError: afterError
                                    })
                                }
                                if (!Gikam.isEmpty(row.beforeSetFlow) && !Gikam.isEmpty(row.afterSetFlow)) {
                                    var errorValue = ((parseFloat(row.afterSetFlow) - parseFloat(row.beforeSetFlow)) / (parseFloat(row.afterSetFlow) + parseFloat(row.beforeSetFlow))) * 100;
                                    errorValue = errorValue.toFixed(1);
                                    this.setData({
                                        index: row.gridIndex,
                                        errorValue: errorValue
                                    })
                                }
                            }

                        },
                        toolbar: [{
                            type: 'button',
                            text: '添加',
                            icon: 'add',
                            onClick: function () {
                                Gikam.create('modal', {
                                    title: Gikam.choose.getConfig('equipt').title,
                                    url: Gikam.choose.getConfig('equipt').url + Gikam.param({
                                        equiptRequisitionFilterProjId: _this.param.projId
                                    }),
                                    onAfterClose: function (data) {
                                        if (Gikam.isEmpty(data)) {
                                            return;
                                        }
                                        var chooseData = data.map(function (item) {
                                            return {
                                                sampleId: _this.param.sampleId,
                                                equiptId: item.id,
                                                equiptNo: item.equiptId,
                                                equiptName: item.equiptName,
                                                specification: item.specification,
                                                airtight: '合格',
                                                flowUnit: 'L/min',
                                                beforeJudge: '合格',
                                                afterJudge: '合格',
                                                judge: '合格',
                                                bizType: '2'
                                            }
                                        })
                                        Gikam.getComp('emc-sample-expand-detail-list-equipt-flow-set-msg-grid').insert('/secure/emc/module/bp/equipt-flow-sets', chooseData);
                                    }
                                })
                            }
                        }, {
                            type: 'button',
                            text: '删除',
                            icon: 'remove',
                            onClick: function () {
                                Gikam.getComp('emc-sample-expand-detail-list-equipt-flow-set-msg-grid').deleteRows('/secure/emc/module/bp/equipt-flow-sets');
                            }
                        }, {
                            type: 'button',
                            text: '批录入',
                            icon: 'edit',
                            onClick: function () {
                                var grid = Gikam.getComp('emc-sample-expand-detail-list-equipt-flow-set-msg-grid');
                                var selections = grid.getSelections();
                                if (Gikam.isEmpty(selections)) {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                    return;
                                }
                                var modal = Gikam.create('modal', {
                                    title: "批录入",
                                    width: 400,
                                    height: 250,
                                });
                                var layout = Gikam.create('layout', {
                                    renderTo: modal.window.$dom,
                                    center: {
                                        items: [{
                                            type: 'btnToolbar',
                                            items: [{
                                                type: 'button',
                                                text: 'GIKAM.BUTTON.CONFIRM',
                                                class: 'blue',
                                                onClick: function () {
                                                    if (!Gikam.isEmpty(form.getData())) {
                                                        selections.forEach(function (item) {
                                                            grid.setData({
                                                                index: item.gridIndex,
                                                                flowUnit: form.getData().flowUnit
                                                            })
                                                        })
                                                        modal.close();
                                                    }
                                                }
                                            }, {
                                                type: 'button',
                                                text: 'GIKAM.BUTTON.CANCEL',
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
                                    fields: [
                                        {
                                            field: 'flowUnit',
                                            title: 'T_EMC_EQUIPT_FLOW_SET.FLOWUNIT',
                                            type: 'choose',
                                            category: 'unit',
                                            single: true,
                                            onAfterChoose: function (rows, index) {
                                                if (!Gikam.isEmpty(rows)) {
                                                    this.setData({
                                                        flowUnit: rows[0].unitName,
                                                    });
                                                }
                                            },
                                        }
                                    ],
                                    autoSave: false
                                });
                            }
                        }],
                    }]
                }
            }]
        }
    },
    getEquiptStandardSetPanels: function () {
        var _this = this;
        return {
            title: '标气校准',
            id: 'emc-sample-expand-detail-list-equipt-standard-set-panel',
            items: [{
                type: 'layout',
                north: {
                    height: '50%',
                    items: [{
                        type: 'grid',
                        service: 'emcEquiptStandardSetServiceImpl',
                        id: 'emc-sample-expand-detail-list-equipt-standard-set-grid',
                        dbTable: 'T_EMC_EQUIPT_STANDARD_SET',
                        columns: emcSample.getStandardSetListColumns(),
                        url: '/secure/emc/module/bp/equipt-standard-sets/queries',
                        requestData: {
                            sampleId_SEQ: _this.param.sampleId,
                            bizType_EQ: '1'
                        },
                        page: true,
                        onLoadSuccess: function (data) {
                            if (!Gikam.isEmpty(data)) {
                                this.activeRowByIndex(0);
                            }
                        },
                        toolbar: [{
                            type: 'button',
                            text: '添加',
                            icon: 'add',
                            onClick: function () {
                                Gikam.preInsert({
                                    modalTitle: '添加',
                                    fields: [{
                                        field: 'gasName',
                                        title: 'T_EMC_EQUIPT_STANDARD_SET.GASNAME',
                                        type: 'select',
                                        multiple: true,
                                        preInsert: true,
                                        items: [{
                                            text: 'SO2',
                                            value: 'SO2'
                                        }, {
                                            text: 'CO',
                                            value: 'CO'
                                        }, {
                                            text: 'CO2',
                                            value: 'CO2'
                                        }, {
                                            text: 'NO',
                                            value: 'NO'
                                        }, {
                                            text: 'NO2',
                                            value: 'NO2'
                                        }]
                                    }],
                                    url: '/secure/emc/module/bp/equipt-standard-sets',
                                    param: {
                                        sampleId: _this.param.sampleId,
                                    }
                                }).done(function () {
                                    Gikam.getComp('emc-sample-expand-detail-list-equipt-standard-set-grid').refresh();
                                    Gikam.getComp('emc-sample-expand-detail-list-equipt-standard-set-msg-grid').refresh();
                                })
                            }
                        }, {
                            type: 'button',
                            text: '删除',
                            icon: 'remove',
                            onClick: function () {
                                Gikam.getComp('emc-sample-expand-detail-list-equipt-standard-set-grid').deleteRows('/secure/emc/module/bp/equipt-standard-sets');
                            }
                        },],
                    }]
                },
                center: {
                    title: '标气测定记录',
                    items: [{
                        type: 'grid',
                        service: 'emcEquiptStandardSetServiceImpl',
                        id: 'emc-sample-expand-detail-list-equipt-standard-set-msg-grid',
                        dbTable: 'T_EMC_EQUIPT_STANDARD_SET',
                        columns: emcSample.getStandardSetMsgListColumns(),
                        url: '/secure/emc/module/bp/equipt-standard-sets/queries',
                        requestData: {
                            sampleId_SEQ: _this.param.sampleId,
                            bizType_EQ: '2'
                        },
                        page: true,
                        onLoadSuccess: function (data) {
                            if (!Gikam.isEmpty(data)) {
                                this.activeRowByIndex(0);
                            }
                        },
                        onUpdated: function (data, fields, index) {
                            if (fields.indexOf('density') > -1 || fields.indexOf('beforeTestA') > -1 || fields.indexOf('beforeTestB') > -1 || fields.indexOf('afterTestA') > -1 || fields.indexOf('afterTestB') > -1) {
                                var row = Gikam.getComp('emc-sample-expand-detail-list-equipt-standard-set-msg-grid').getActivedRow();
                                if (!Gikam.isEmpty(row.density) && !Gikam.isEmpty(row.beforeTestA) && !Gikam.isEmpty(row.beforeTestB) && row.density != 0) {
                                    var beforeSysError = (parseFloat(row.beforeTestB) - parseFloat(row.beforeTestA)) / parseFloat(row.density) * 100;
                                    beforeSysError = beforeSysError.toFixed(2);
                                    this.setData({
                                        index: row.gridIndex,
                                        beforeError: (parseFloat(row.beforeTestA) - parseFloat(row.density)).toFixed(1),
                                        beforeSysError: beforeSysError
                                    })
                                }
                                if (!Gikam.isEmpty(row.density) && !Gikam.isEmpty(row.afterTestA) && !Gikam.isEmpty(row.afterTestB) && row.density != 0) {
                                    var afterSysError = (parseFloat(row.afterTestB) - parseFloat(row.afterTestA)) / parseFloat(row.density) * 100;
                                    afterSysError = afterSysError.toFixed(2);
                                    this.setData({
                                        index: row.gridIndex,
                                        afterError: (parseFloat(row.afterTestA) - parseFloat(row.density)).toFixed(1),
                                        afterSysError: afterSysError
                                    })
                                }
                            }
                        },
                        toolbar: [{
                            type: 'button',
                            text: '添加',
                            icon: 'add',
                            onClick: function () {
                                Gikam.preInsert({
                                    modalTitle: '添加',
                                    fields: [{
                                        field: 'gasName',
                                        title: 'T_EMC_EQUIPT_STANDARD_SET.GASNAME',
                                        type: 'select',
                                        preInsert: true,
                                        items: [{
                                            text: 'SO2',
                                            value: 'SO2'
                                        }, {
                                            text: 'CO',
                                            value: 'CO'
                                        }, {
                                            text: 'NO',
                                            value: 'NO'
                                        }, {
                                            text: 'NO2',
                                            value: 'NO2'
                                        }]
                                    }],
                                    url: '/secure/emc/module/bp/equipt-standard-sets',
                                    param: {
                                        sampleId: _this.param.sampleId,
                                        bizType: '2',
                                        conclusion: '合格'
                                    }
                                }).done(function () {
                                    Gikam.getComp('emc-sample-expand-detail-list-equipt-standard-set-msg-grid').refresh();
                                })
                            }
                        }, {
                            type: 'button',
                            text: '删除',
                            icon: 'remove',
                            onClick: function () {
                                Gikam.getComp('emc-sample-expand-detail-list-equipt-standard-set-msg-grid').deleteRows('/secure/emc/module/bp/equipt-standard-sets');
                            }
                        },],
                    }]
                }
            }]
        }
    },
    getEquiptDioxinSetPanels: function () {
        var _this = this;
        return {
            title: '有组织二噁英校准',
            id: 'emc-sample-expand-detail-list-equipt-Dioxin-set-panel',
            items: [{
                type: 'layout',
                north: {
                    height: '50%',
                    items: [{
                        type: 'grid',
                        service: 'emcEquiptDioxinSetServiceImpl',
                        id: 'emc-sample-expand-detail-list-equipt-dioxin-set-grid',
                        dbTable: 'T_EMC_EQUIPT_DIOXIN_SET',
                        columns: emcSample.getDioxinSetListColumns(),
                        url: '/secure/emc/module/bp/equipt-dioxin-sets/queries',
                        requestData: {
                            sampleId_SEQ: _this.param.sampleId,
                            bizType_EQ: '1'
                        },
                        page: true,
                        onLoadSuccess: function (data) {
                            if (!Gikam.isEmpty(data)) {
                                this.activeRowByIndex(0);
                            }
                        },
                        toolbar: [{
                            type: 'button',
                            text: '添加',
                            icon: 'add',
                            onClick: function () {
                                Gikam.create('modal', {
                                    title: Gikam.choose.getConfig('equipt').title,
                                    url: Gikam.choose.getConfig('equipt').url + Gikam.param({
                                        equiptRequisitionFilterProjId: _this.param.projId
                                    }),
                                    onAfterClose: function (data) {
                                        if (Gikam.isEmpty(data)) {
                                            return;
                                        }
                                        var chooseData = data.map(function (item) {
                                            return {
                                                sampleId: _this.param.sampleId,
                                                equiptId: item.id,
                                                equiptNo: item.equiptId,
                                                equiptName: item.equiptName,
                                                specification: item.specification,
                                                bizType: '1'
                                            }
                                        })
                                        Gikam.getComp('emc-sample-expand-detail-list-equipt-dioxin-set-grid').insert('/secure/emc/module/bp/equipt-dioxin-sets', chooseData);
                                    }
                                })
                            }
                        }, {
                            type: 'button',
                            text: '删除',
                            icon: 'remove',
                            onClick: function () {
                                Gikam.getComp('emc-sample-expand-detail-list-equipt-dioxin-set-grid').deleteRows('/secure/emc/module/bp/equipt-dioxin-sets');
                            }
                        },],
                    }]
                },
                center: {
                    title: '仪器校准信息',
                    items: [{
                        type: 'grid',
                        service: 'emcEquiptDioxinSetServiceImpl',
                        id: 'emc-sample-expand-detail-list-equipt-dioxin-set-msg-grid',
                        dbTable: 'T_EMC_EQUIPT_DIOXIN_SET',
                        columns: emcSample.getDioxinSetMsgListColumns(),
                        url: '/secure/emc/module/bp/equipt-dioxin-sets/queries',
                        requestData: {
                            sampleId_SEQ: _this.param.sampleId,
                            bizType_EQ: '2'
                        },
                        page: true,
                        onLoadSuccess: function (data) {
                            if (!Gikam.isEmpty(data)) {
                                this.activeRowByIndex(0);
                            }
                        },
                        toolbar: [{
                            type: 'button',
                            text: '添加',
                            icon: 'add',
                            onClick: function () {
                                Gikam.create('modal', {
                                    title: Gikam.choose.getConfig('equipt').title,
                                    url: Gikam.choose.getConfig('equipt').url + Gikam.param({
                                        equiptRequisitionFilterProjId: _this.param.projId
                                    }),
                                    onAfterClose: function (data) {
                                        if (Gikam.isEmpty(data)) {
                                            return;
                                        }
                                        var chooseData = data.map(function (item) {
                                            return {
                                                sampleId: _this.param.sampleId,
                                                equiptId: item.id,
                                                equiptNo: item.equiptId,
                                                equiptName: item.equiptName,
                                                specification: item.specification,
                                                bizType: '2',
                                                conclusion: '合格'
                                            }
                                        })
                                        Gikam.getComp('emc-sample-expand-detail-list-equipt-dioxin-set-msg-grid').insert('/secure/emc/module/bp/equipt-dioxin-sets', chooseData);
                                    }
                                })
                            }
                        }, {
                            type: 'button',
                            text: '删除',
                            icon: 'remove',
                            onClick: function () {
                                Gikam.getComp('emc-sample-expand-detail-list-equipt-dioxin-set-msg-grid').deleteRows('/secure/emc/module/bp/equipt-dioxin-sets');
                            }
                        },],
                    }]
                }
            }]
        }
    },
    getEquiptDioxinSetPanels2: function () {
        var _this = this;
        return {
            title: '无组织二噁英校准',
            id: 'emc-sample-expand-detail-list-equipt-Dioxin-set-panel2',
            items: [{
                type: 'layout',
                north: {
                    height: '50%',
                    items: [{
                        type: 'grid',
                        service: 'emcEquiptDioxinSetServiceImpl',
                        id: 'emc-sample-expand-detail-list-equipt-dioxin-set-grid2',
                        dbTable: 'T_EMC_EQUIPT_DIOXIN_SET',
                        columns: emcSample.getDioxinSetListColumns(),
                        url: '/secure/emc/module/bp/equipt-dioxin-sets/queries',
                        requestData: {
                            sampleId_SEQ: _this.param.sampleId,
                            bizType_EQ: '3'
                        },
                        page: true,
                        onLoadSuccess: function (data) {
                            if (!Gikam.isEmpty(data)) {
                                this.activeRowByIndex(0);
                            }
                        },
                        toolbar: [{
                            type: 'button',
                            text: '添加',
                            icon: 'add',
                            onClick: function () {
                                Gikam.create('modal', {
                                    title: Gikam.choose.getConfig('equipt').title,
                                    url: Gikam.choose.getConfig('equipt').url + Gikam.param({
                                        equiptRequisitionFilterProjId: _this.param.projId
                                    }),
                                    onAfterClose: function (data) {
                                        if (Gikam.isEmpty(data)) {
                                            return;
                                        }
                                        var chooseData = data.map(function (item) {
                                            return {
                                                sampleId: _this.param.sampleId,
                                                equiptId: item.id,
                                                equiptNo: item.equiptId,
                                                equiptName: item.equiptName,
                                                specification: item.specification,
                                                bizType: '3'
                                            }
                                        })
                                        Gikam.getComp('emc-sample-expand-detail-list-equipt-dioxin-set-grid2').insert('/secure/emc/module/bp/equipt-dioxin-sets', chooseData);
                                    }
                                })
                            }
                        }, {
                            type: 'button',
                            text: '删除',
                            icon: 'remove',
                            onClick: function () {
                                Gikam.getComp('emc-sample-expand-detail-list-equipt-dioxin-set-grid2').deleteRows('/secure/emc/module/bp/equipt-dioxin-sets');
                            }
                        },],
                    }]
                },
                center: {
                    title: '仪器校准信息',
                    items: [{
                        type: 'grid',
                        service: 'emcEquiptDioxinSetServiceImpl',
                        id: 'emc-sample-expand-detail-list-equipt-dioxin-set-msg-grid2',
                        dbTable: 'T_EMC_EQUIPT_DIOXIN_SET',
                        columns: emcSample.getDioxinSetMsgListColumns2(),
                        url: '/secure/emc/module/bp/equipt-dioxin-sets/queries',
                        requestData: {
                            sampleId_SEQ: _this.param.sampleId,
                            bizType_EQ: '4'
                        },
                        page: true,
                        onLoadSuccess: function (data) {
                            if (!Gikam.isEmpty(data)) {
                                this.activeRowByIndex(0);
                            }
                        },
                        toolbar: [{
                            type: 'button',
                            text: '添加',
                            icon: 'add',
                            onClick: function () {
                                Gikam.create('modal', {
                                    title: Gikam.choose.getConfig('equipt').title,
                                    url: Gikam.choose.getConfig('equipt').url + Gikam.param({
                                        equiptRequisitionFilterProjId: _this.param.projId
                                    }),
                                    onAfterClose: function (data) {
                                        if (Gikam.isEmpty(data)) {
                                            return;
                                        }
                                        var chooseData = data.map(function (item) {
                                            return {
                                                sampleId: _this.param.sampleId,
                                                equiptId: item.id,
                                                equiptNo: item.equiptId,
                                                equiptName: item.equiptName,
                                                specification: item.specification,
                                                bizType: '4',
                                                conclusion: '合格'
                                            }
                                        })
                                        Gikam.getComp('emc-sample-expand-detail-list-equipt-dioxin-set-msg-grid2').insert('/secure/emc/module/bp/equipt-dioxin-sets', chooseData);
                                    }
                                })
                            }
                        }, {
                            type: 'button',
                            text: '删除',
                            icon: 'remove',
                            onClick: function () {
                                Gikam.getComp('emc-sample-expand-detail-list-equipt-dioxin-set-msg-grid2').deleteRows('/secure/emc/module/bp/equipt-dioxin-sets');
                            }
                        },],
                    }]
                }
            }]
        }
    },

    addEquiptSet: function (name, datas) {
        var _this = this;
        var saveData = [];
        for (j = 0, len = datas.length; j < len; j++) {
            var value = "/";
            var status = "/";
            if (j == 3 || name != 'pH值') {
                value = "";
                status = "合格";
            }
            saveData.push({
                sampleId: _this.param.sampleId,
                cushionNo: '',
                cushionName: datas[j],
                allocationDate: Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd'),
                status: status,
                sortno: j,
                equiptValue: value,
                name: name,
            });
        }
        workspace.window.showMask(true);
        Gikam.postText('/secure/emc/module/bp/equipt-sets/insert', Gikam.getJsonWrapper({}, ['emcEquiptSetServiceImpl', saveData])).done(function (id) {
            Gikam.getComp('emc-sample-expand-detail-list-equipt-set-grid').refresh();
        }).always(function () {
            workspace.window.closeMask();
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
                url: '/secure/emc/module/bp/bp/order-tasks/queries/order-task',
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
                    text: 'GIKAM.BUTTON.UPDATE_EQUIPT',
                    icon: 'edit',
                    onClick: function () {
                        _this.updateEquipt();
                    }
                }, {
                    type: 'button',
                    text: '修改检测人员',
                    icon: 'edit',
                    onClick: function () {
                        _this.updateEmp();
                    }
                }, {
                    type: 'button',
                    text: 'GIKAM.BUTTON.ASSIST_EQUIPT',
                    icon: 'edit',
                    onClick: function () {
                        _this.updateAssitEpuipt();
                    }
                }],
            }]
        }
    },
    updateAssitEpuipt: function () {
        var _this = emcSample.dataInputDetailPage;
        var selections = Gikam.getComp('emc-sample-expand-detail-list-equipt-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title: Gikam.choose.getConfig('equipt').title,
            url: Gikam.choose.getConfig('equipt').url + Gikam.param({
                // org_filter : '1',
                // equiptCategoryId_EQ : 615348928847936,
                ID_DIFFER: selections[0].equiptId,
                equiptRequisitionFilterProjId: selections[0].projId
            }),
            onAfterClose: function (rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                var assistEquiptId = "";
                var assistEquiptName = "";
                var names = [];

                Gikam.each(rows, function (index, equipt) {
                    if (Gikam.isEmpty(assistEquiptId)) {
                        assistEquiptId += equipt.id;
                    } else {
                        assistEquiptId += ',' + equipt.id;
                    }
                    names.push(equipt.equiptId + "/" + equipt.equiptName + "/" + equipt.specification);
                    if (Gikam.isEmpty(assistEquiptName)) {
                        assistEquiptName += equipt.equiptName;
                    } else {
                        assistEquiptName += ',' + equipt.equiptName;
                    }
                });

                var saveData = selections.map(function (task) {
                    return {
                        id: task.id,
                        assistEquiptId: assistEquiptId,
                        assistEquiptName: names.join(','),
                    };
                });
                workspace.window.showMask(true);
                Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, ['emcOrderTaskServiceImpl', saveData])).done(function (data) {
                    Gikam.each(selections, function (index, item) {
                        Gikam.getComp('emc-sample-expand-detail-list-equipt-grid').refreshRowById(item.id);
                    });
                }).always(function () {
                    workspace.window.closeMask();
                });
            }
        });
    },

    updateEquipt: function () {
        var _this = this;
        var grid = Gikam.getComp('emc-sample-expand-detail-list-equipt-grid');
        var selections = grid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
            return;
        }
        // var testMethodId = selections[0].testMethodId;
        // for (var j = 0; j < selections.length; j++) {
        //     if (testMethodId != selections[j].testMethodId) {
        //         Gikam.alert('需选择同一测试方法的监测项目！');
        //         return;
        //     }
        // }
        Gikam.create('modal', {
            title: Gikam.choose.getConfig('equipt').title,
            url: Gikam.choose.getConfig('equipt').url + Gikam.param({
                single: true,
                equiptRequisitionFilterProjId: selections[0].projId
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
                    folderId: selections[0].folderId,
                    orderTaskId: selections[0].id,
                    testName: testName
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
                departmentId_filter: selections[0].ext$.sampleorgid
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
            checkOnActive: false,
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
            order: {
                orderNo: 'asc'
            },
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
                                    orderTaskId_IN: activedRow.ext$.ids
                                }
                            })).done(function (data) {
                                // var grid = Gikam.getComp('emc-sample-expand-detail-list-record-data-order-task-result-grid');
                                // Gikam.each(data.rows, function (index, row) {
                                //     grid.setData(row, false, true);
                                // });
                                Gikam.getComp('emc-sample-expand-detail-list-record-data-order-task-result-grid').loadData(data);

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
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.TRIGGER_FORMLUA',
                icon: 'activate',
                onClick: function () {
                    _this.orderTaskResultDataGridTriggerFormlua();
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
                        Gikam.emcDownloadFile(fileUrl);
                    });
                }
            }, {
                type: 'button',
                text: 'ELN方式录入',
                icon: 'edit',
                onClick: function () {
                    _this.sampleELN();
                }
            }, {
                type: 'button',
                text: '采样基准含氧量',
                icon: 'search',
                onClick: function () {
                    _this.sampleSetGrid();
                }
            }]
        }
    },

    sampleSetGrid: function () {
        var _this = this;
        Gikam.create('modal', {
            width: '60%',
            height: '60%',
            title: '采样基准含氧量',
            url: IFM_CONTEXT + '/secure/emc/module/bp/sample-sets/page/choose-list',
        });
    },

    sampleELN: function () {
        var _this = this;
        var elnTmplId = _this.param.elnTmplId
        var sampleId = _this.param.sampleId
        if (Gikam.isEmpty(elnTmplId)) {
            Gikam.alert("样品类型未配置eln模板");
            return;
        }
        if (parent.isOpenElnBizId == sampleId) {
            Gikam.alert('CORE.MODULE.ITEM.ELN.TMPLS.TIP.CLOSE_ELN');
            return;
        }
        var url = IFM_CONTEXT + '/static/plugin/spread-sheets-11.2.6/designer/index/index.html' + Gikam.param({
            operateType: 1,
            bizId: sampleId,
            SAMPLEID: sampleId,
            versionId: '1',
            elnTmplId: elnTmplId,
            IFM_CONTEXT: IFM_CONTEXT,
            // bizCategory: 'develop',
        });
        window.open(url, 'eln');
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
                title: '结果录入',
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

    refreshRecordResultData: function (row) {
        // 查询数据
        Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/quick-inputs-sample/table-rows/queries', Gikam.getJsonWrapper({
            f: {
                orderTaskId_IN: row.ext$.ids
            }
        })).done(function (data) {
            Gikam.getComp('emc-sample-expand-detail-list-record-data-order-task-result-grid').loadData(data);
        });
    },

    getSamplePanels: function () {
        var _this = this;
        return {
            title: '采样单信息',
            id: 'emc-sample-expand-detail-list-sample-panel',
            items: [{
                type: 'layout',
                center: {

                    items: [{
                        type: 'form',
                        id: 'emc-sample-info-detail-form',
                        fields: [],
                        service: 'emcPropertyDataServiceImpl',
                        onRendered: function (data) {
                            this.disableAutoSave();
                            _this.getSampleDetailColums();
                            this.enableAutoSave();
                        },
                        onLoadSuccess: function () {
                            this.setOptions({url: ''});
                        }
                    }]
                },
                north: {
                    height: '50',
                    items: [{
                        type: 'form',
                        id: 'emc-sample-info-detail-sample-form',
                        url: Gikam.printf(emcSample.baseUrl + '/{id}', {
                            id: _this.param.sampleId
                        }),
                        service: 'emcSampleServiceImpl',
                        titleWidthAuto: true,
                        fields: [{
                            field: 'remark',
                            title: 'T_EMC_SAMPLE.REMARK',
                            editor: true,
                            validators: ['strLength[0,170]']
                        }],
                    }]
                }
            }]
        }
    },

    baseForm: null,
    getBaseInfoForm: function () {
        var _this = this;
        return {
            type: 'form',
            id: 'emc-sample-info-detail-form',
            fields: [],
            service: 'emcPropertyDataServiceImpl',
            onRendered: function (data) {
                this.disableAutoSave();
                _this.getSampleDetailColums();
                this.enableAutoSave();
            },
            onLoadSuccess: function () {
                this.setOptions({url: ''});
            }
        }
    },
    getSampleDetailColums: function () {
        var columns = [];
        var _this = this;
        var fields = [];

        Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/tmpls/' + _this.param.sampleId + '/judge-samples/' + _this.param.monitorTmplId).done(function (data) {
            var formData = Gikam.getComp('emc-sample-info-detail-form');
            var readonly = "true" === _this.param.readonly;
            if (Gikam.isEmpty(readonly)) {
                readonly = false;
            }
            data.rows.sort((item1, item2) => parseInt(item1.orderNo) - parseInt(item2.orderNo));

            fields = data.rows.map(function (e) {
                if (e.fieldType == 'fieldMultiSelect') {
                    var selectFields = {
                        field: e.internalField,
                        title: e.fieldName,
                        type: 'select',
                        multiple: true,
                        search: true,
                        category: 'sys',
                        readonly: readonly,
                        editor: !readonly,
                    };
                    selectFields['param'] = {
                        codeCategoryId: e.sysCode
                    };
                    if (e.required == '1') {
                        selectFields['validators'] = ['notEmpty'];
                    }
                    return selectFields;
                } else if (e.fieldType == 'fieldSelect') {
                    var selectFields = {
                        field: e.internalField,
                        title: e.fieldName,
                        type: 'comboBox',
                        search: true,
                        category: 'sys',
                        readonly: readonly,
                        editor: !readonly,
                    };
                    selectFields['param'] = {
                        codeCategoryId: e.sysCode
                    };
                    if (e.required == '1') {
                        selectFields['validators'] = ['notEmpty'];
                    }
                    return selectFields;
                } else if (e.fieldType == 'fieldContext') {
                    var contextFields = {
                        field: e.internalField,
                        title: e.fieldName,
                        validators: [],
                        readonly: readonly
                    };
                    if (e.required == '1') {
                        contextFields.validators.push('notEmpty');
                    }
                    if (e.fieldLength) {
                        contextFields.validators.push('strLength[0,' + e.fieldLength + ']');
                    }
                    if (e.defaultValue) {
                        contextFields['value'] = e.defaultValue;
                    }
                    return contextFields;
                } else if (e.fieldType == 'fieldContextArea') {
                    var contextAreaFields = {
                        field: e.internalField,
                        title: e.fieldName,
                        validators: [],
                        readonly: readonly
                    };
                    if (e.required == '1') {
                        contextAreaFields.validators.push('notEmpty');
                    }
                    if (e.fieldLength) {
                        contextAreaFields.validators.push('strLength[0,' + e.fieldLength + ']');
                    }
                    return contextAreaFields;
                } else if (e.fieldType == 'fieldDate') {
                    var dateFields = {
                        field: e.internalField,
                        title: e.fieldName,
                        type: 'date',
                        readonly: readonly
                    };
                    if (e.required == '1') {
                        dateFields['validators'] = ['notEmpty'];
                    }
                    return dateFields;
                }
            })
            var equipt = {
                field: 'ext$.samplingequiptname',
                title: '共用仪器',
                type: 'choose',
                category: 'equipment',
                category: 'equipt',
                type: 'choose',
//                       editor : !readonly,
                readonly: readonly,
                onBeforeChoose: function () {
                    var activedRow = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getActivedRow();
                    return {
                        equiptRequisitionFilterProjId: activedRow.projId
                    }
                },
                onAfterChoose: function (rows) {
                    if (!Gikam.isEmpty(rows)) {
                        var equiptId = [];
                        var equiptName = [];
                        Gikam.each(rows, function (index, item) {
                            equiptId.push(item.id);
                            equiptName.push(item.equiptId + '/' + item.equiptName + '/' + item.specification);
                        });
                        Gikam.getComp('emc-sample-edit-list-project-sample-grid').setData({
                            id: Gikam.getComp('emc-sample-edit-list-project-sample-grid').getActivedRow().id,
                            ext$: {
                                samplingequiptid: equiptId.join(','),
                                samplingequiptname: equiptName.join(',')
                            }
                        });
                        this.setData({

                            ext$: {
                                samplingequiptid: equiptId.join(','),
                                samplingequiptname: equiptName.join(',')
                            }
                        });
                    }
                },
                onClean: function () {
                    Gikam.getComp('emc-sample-edit-list-project-sample-grid').setData({
                        id: Gikam.getComp('emc-sample-edit-list-project-sample-grid').getActivedRow().id,
                        ext$: {
                            samplingequiptid: '',
                            samplingequiptname: ''
                        }
                    });
                }

            };
            fields.push(equipt);
            formData.options.fields = fields;
            formData.options.titleWidthAuto = true;
            var panels = [{fields: fields}]
            formData.cacheSelectItems(panels).done(() => {
                formData.cleanData();
                formData.options.panels = panels;
                formData.validators = {};
                formData.registerValidators();
                formData.refresh({
                    url: Gikam.printf('/secure/emc/module/mdm/basemdm/property-tmpls/property-fields/{id}/queries/property-datas/{tmplId}', {
                        id: _this.param.sampleId, // sampleId --> bizId
                        tmplId: _this.param.tmplId
                    }),
                });
            });
        });
    },

    getFolderPanels: function () {
        var _this = this;
        return {
            title: '点位信息',
            id: 'emc-sample-expand-detail-list-folder-panel',
            show: false,
            items: [{
                type: 'layout',
                north: {
                    height: '50%',
                    items: [{
                        type: 'grid',
                        service: 'emcFolderServiceImpl',
                        id: 'emc-sample-expand-detail-list-folder-grid',
                        readonly: false,
                        columns: emcSample.getSamplePointColumns(),
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
                        onRendered: function () {
                            Gikam.getComp('dbs-btn').hide();
                            Gikam.getComp('fs-btn').hide();
                            Gikam.getComp('dxs-btn').hide();
                            Gikam.getComp('tr-btn').hide();
                            Gikam.getComp('gf-btn').hide();
                            Gikam.getComp('gfjc-btn').hide();
                            Gikam.getComp('yzzfq-btn').hide();
                            Gikam.getComp('wzzfq-btn').hide();
                            Gikam.getComp('cjw-btn').hide();
                            Gikam.getComp('shyys-btn').hide();
                            Gikam.getComp('hs-btn').hide();
                            Gikam.getComp('czws-btn').hide();
                            Gikam.getComp('wn-btn').hide();
                            Gikam.getComp('hjkq-btn').hide();
                            Gikam.getComp('zs-btn').hide();
                            Gikam.getComp('zd-btn').hide();
                            Gikam.getComp('ggcs-btn').hide();
                            Gikam.getComp('snkq-btn').hide();
                        },
                        onLoadSuccess: function (data) {
                            var category = data[0].orderCategory;
                            if ("废水" === category) {
                                Gikam.getComp('fs-btn').show();
                            }
                            if ("地下水" === category) {
                                Gikam.getComp('dxs-btn').show();
                            }
                            if ("地表水" === category) {
                                Gikam.getComp('dbs-btn').show();
                            }
                            if ("土壤" === category) {
                                Gikam.getComp('tr-btn').show();
                            }
                            if ("固废" === category) {
                                Gikam.getComp('gf-btn').show();
                            }
                            if ("沉积物" === category) {
                                Gikam.getComp('cjw-btn').show();
                            }
                            if ("有组织废气" === category) {
                                Gikam.getComp('yzzfq-btn').show();
                            }
                            if ("无组织废气" === category) {
                                Gikam.getComp('wzzfq-btn').show();
                            }
                            if ("生活饮用水" === category) {
                                Gikam.getComp('shyys-btn').show();
                            }
                            if ("海水" === category) {
                                Gikam.getComp('hs-btn').show();
                            }
                            if ("城镇污水" === category) {
                                Gikam.getComp('czws-btn').show();
                            }
                            if ("城镇污水处理厂污泥" === category) {
                                Gikam.getComp('wn-btn').show();
                            }
                            if ("环境空气" === category) {
                                Gikam.getComp('hjkq-btn').show();
                            }
                            if ("噪声" === category) {
                                Gikam.getComp('zs-btn').show();
                            }
                            if ("振动" === category) {
                                Gikam.getComp('zd-btn').show();
                            }
                            if ("公共场所" === category) {
                                Gikam.getComp('ggcs-btn').show();
                            }
                            if ("室内空气" === category) {
                                Gikam.getComp('snkq-btn').show();
                            }
                            if (!Gikam.isEmpty(data)) {

                                this.activeRowByIndex(0);
                            }
                        },
                        onRowActive: function (index, row) {
                            Gikam.getComp('emc-sample-expand-detail-list-folder-uploader').setOptions({
                                bizId: row.id
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
                        }, {
                            type: 'button',
                            text: '地表水信息',
                            id: 'dbs-btn',
                            icon: 'edit',
                            onClick: function () {
                                _this.fillTBSDesc();
                            }
                        }, {
                            type: 'button',
                            text: '废水信息',
                            id: 'fs-btn',
                            icon: 'edit',
                            onClick: function () {
                                _this.fillFSDesc();
                            }
                        }, {
                            type: 'button',
                            text: '地下水信息',
                            id: 'dxs-btn',
                            icon: 'edit',
                            onClick: function () {
                                _this.fillDXSDesc();
                            }
                        }, {
                            type: 'button',
                            text: '土壤信息',
                            id: 'tr-btn',
                            icon: 'edit',
                            onClick: function () {
                                _this.fillTRDesc();
                            }
                        }, {
                            type: 'button',
                            text: '沉积物信息',
                            id: 'cjw-btn',
                            icon: 'edit',
                            onClick: function () {
                                _this.fillCJWDesc();
                            }
                        }, {
                            type: 'button',
                            text: '固废含量',
                            id: 'gf-btn',
                            icon: 'edit',
                            onClick: function () {

                            }
                        }, {
                            type: 'button',
                            text: '固废浸出',
                            id: 'gfjc-btn',
                            icon: 'edit',
                            onClick: function () {

                            }
                        }, {
                            type: 'button',
                            text: '有组织废气',
                            id: 'yzzfq-btn',
                            icon: 'edit',
                            onClick: function () {

                            }
                        }, {
                            type: 'button',
                            text: '无组织废气',
                            id: 'wzzfq-btn',
                            icon: 'edit',
                            onClick: function () {

                            }
                        }, {
                            type: 'button',
                            text: '生活饮用水',
                            id: 'shyys-btn',
                            icon: 'edit',
                            onClick: function () {

                            }
                        }, {
                            type: 'button',
                            text: '海水',
                            id: 'hs-btn',
                            icon: 'edit',
                            onClick: function () {

                            }
                        }, {
                            type: 'button',
                            text: '城镇污水',
                            id: 'czws-btn',
                            icon: 'edit',
                            onClick: function () {

                            }
                        }, {
                            type: 'button',
                            text: '城镇污水处理厂污泥',
                            id: 'wn-btn',
                            icon: 'edit',
                            onClick: function () {

                            }
                        }, {
                            type: 'button',
                            text: '环境空气',
                            id: 'hjkq-btn',
                            icon: 'edit',
                            onClick: function () {

                            }
                        }, {
                            type: 'button',
                            text: '噪声',
                            id: 'zs-btn',
                            icon: 'edit',
                            onClick: function () {

                            }
                        }, {
                            type: 'button',
                            text: '振动',
                            id: 'zd-btn',
                            icon: 'edit',
                            onClick: function () {

                            }
                        }, {
                            type: 'button',
                            text: '公共场所',
                            id: 'ggcs-btn',
                            icon: 'edit',
                            onClick: function () {

                            }
                        }, {
                            type: 'button',
                            text: '室内空气',
                            id: 'snkq-btn',
                            icon: 'edit',
                            onClick: function () {

                            }
                        }],
                    }]
                },
                center: {
                    title: '点位附件',
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
                west: {
                    width: '35%',
                    items: [_this.getOrigindataFolderGridParam()]
                },
                center: {
                    items: [_this.getOrigindataResultGridParam()]
                }
            }]
        }
    },

    getQcDataPanels: function () {
        var _this = this;
        return {
            id: 'emc-sample-expand-detail-list-qc-data-panel',
            title: '质控数据',// 原始数据
            show: false,
            items: [{
                type: 'layout',
                west: {
                    width: '20%',
                    items: [_this.getQcdataFolderGridParam()]
                },
                center: {
                    items: [_this.getQcdataResultGridParam()]
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
                id: 'emc-sample-files-uploader',
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
                        }, {
                            type: 'button',
                            text: '批量修改附件类型',
                            icon: 'select',
                            onClick: function () {
                                var selections = Gikam.getComp('emc-sample-files-uploader').grid.getSelections();
                                if (selections.length === 0) {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                    return;
                                }
                                let modal = Gikam.create('modal', {
                                    title: "选择附件类型",
                                    width: 400,
                                    height: 180,
                                });
                                var layout = Gikam.create('layout', {
                                    renderTo: modal.window.$dom,
                                    center: {
                                        items: [{
                                            type: 'btnToolbar',
                                            items: [{
                                                type: 'button',
                                                text: 'GIKAM.BUTTON.CONFIRM',
                                                class: 'blue',
                                                onClick: function () {
                                                    if (form.validate()) {
                                                        var saveData = selections.map(function (item) {
                                                            return {
                                                                id: item.id,
                                                                index: item.index,
                                                                bizCategory: form.getData().bizCategoryId
                                                            }
                                                        });
                                                        Gikam.getLastModal().window.showMask(true);
                                                        Gikam.postText(emcSample.baseUrl + '/update-file', Gikam.getJsonWrapper({}, ['', saveData])).done(function () {
                                                            Gikam.getComp('emc-sample-files-uploader').refresh();
                                                        }).always(function () {
                                                            Gikam.getLastModal().window.closeMask();
                                                            modal.close();
                                                        });
                                                    }
                                                }
                                            }, {
                                                type: 'button',
                                                text: 'GIKAM.BUTTON.CANCEL',
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
                                    fields: [
                                        {
                                            field: 'bizCategoryId',
                                            title: 'T_EMC_PROJECT.BIZCATEGORY',
                                            type: 'select',
                                            category: 'sys',
                                            param: {
                                                codeCategoryId: 'fileBizCategory',
                                            }
                                        }
                                    ],
                                    autoSave: false
                                });
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
                                var category = data[0].orderCategory;
                                if ("废水，地下水，地表水，生活饮用水".includes(category)) {
                                    Gikam.getComp('sydesc-btn').show();
                                }
                                if ("土壤，沉积物".includes(category)) {
                                    Gikam.getComp('tydesc-btn').show();
                                }
                                if ("固废含量，固废浸出".includes(category)) {
                                    Gikam.getComp('gfdesc-btn').show();
                                }
                                if ("城镇污水处理厂污泥".includes(category)) {
                                    Gikam.getComp('wndesc-btn').show();
                                }
                                this.activeRowByIndex(0);
                            }
                        },
                        onCellClick: function (field, row) {
                            // field === 'orderDesc' && _this.loadOrderDescPage(row);
                        },
                        onRendered: function () {
                            Gikam.getComp('sydesc-btn').hide();
                            Gikam.getComp('tydesc-btn').hide();
                            Gikam.getComp('gfdesc-btn').hide();
                            Gikam.getComp('wndesc-btn').hide();
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
                        }, {
                            type: 'button',
                            text: '批量编辑采样时间',
                            icon: 'edit',
                            onClick: function () {
                                _this.updateSampleTestBatch();
                            },
                        }, {
                            type: 'button',
                            text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.ORDER_DESC_FILL',
                            id: 'sydesc-btn',
                            icon: 'edit',
                            onClick: function () {
                                _this.fillOrderDesc();
                            }
                        }, {
                            type: 'button',
                            text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.ORDER_DESC_SOIL',
                            icon: 'edit',
                            id: 'tydesc-btn',
                            onClick: function () {
                                _this.soilOrderDesc();
                            }
                        }, {
                            type: 'button',
                            text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.INPUT_SOLID_WASTE_ORDER_DESC',
                            icon: 'edit',
                            id: 'gfdesc-btn',
                            onClick: function () {
                                _this.solidWasteOrderDesc();
                            }
                        }, {
                            type: 'button',
                            text: '污泥样品描述',
                            icon: 'edit',
                            id: 'wndesc-btn',
                            onClick: function () {
                                _this.wuniOrderDesc();
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
                    Gikam.postText(emcSample.baseUrl + '/editOrderDesc', Gikam.getJsonWrapper({}, ['emcOrderServiceImpl', [row]])).done(function () {
                        Gikam.getComp('emc-sample-expand-detail-list-order-grid').refreshRowById(row.id);
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    fillTBSDesc: function () {
        var grid = Gikam.getComp('emc-sample-expand-detail-list-folder-grid');
        var rows = grid.getSelections();
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        Gikam.create('modal', {
            width: 600,
            height: 313,
            title: '地表水信息',
            url: emcSample.baseUrl + '/page/folder-dbs-desc-detail',
            onAfterClose: function (data) {
                if (!Gikam.isEmpty(data)) {
                    var selections = grid.getSelections();
                    workspace.window.showMask(true);
                    selections.map(function (formData) {
                        formData.longitude = data.longitude;
                        formData.latitude = data.latitude;
                        formData.river = data.river;
                        formData.section = data.section;
                        formData.sectionConditionDesc = data.sectionConditionDesc;
                        formData.sectionWaterWidth = data.sectionWaterWidth;
                        formData.sectionWaterDepth = data.sectionWaterDepth;
                        formData.sectionWaterQuality = data.sectionWaterQuality;
                        formData.waterTemperature = data.waterTemperature;
                        formData.turbidity = data.turbidity;
                    });

                    Gikam.put('/secure/emc/module/bp/bp/folders/instant', Gikam.getJsonWrapper({}, ['emcFolderServiceImpl', selections])).done(function (data) {
                        grid.refresh();
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },
    fillFSDesc: function () {
        var grid = Gikam.getComp('emc-sample-expand-detail-list-folder-grid');
        var rows = grid.getSelections();
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        Gikam.create('modal', {
            width: 600,
            height: 313,
            title: '废水信息',
            url: emcSample.baseUrl + '/page/folder-fs-desc-detail',
            onAfterClose: function (data) {
                if (!Gikam.isEmpty(data)) {
                    var selections = grid.getSelections();
                    workspace.window.showMask(true);
                    selections.map(function (formData) {
                        formData.waterTemperature = data.waterTemperature;
                    });

                    Gikam.put('/secure/emc/module/bp/bp/folders/instant', Gikam.getJsonWrapper({}, ['emcFolderServiceImpl', selections])).done(function (data) {
                        grid.refresh();
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },
    fillDXSDesc: function () {
        var grid = Gikam.getComp('emc-sample-expand-detail-list-folder-grid');
        var rows = grid.getSelections();
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        Gikam.create('modal', {
            width: 600,
            height: 313,
            title: '地下水信息',
            url: emcSample.baseUrl + '/page/folder-dxs-desc-detail',
            onAfterClose: function (data) {
                if (!Gikam.isEmpty(data)) {
                    var selections = grid.getSelections();
                    workspace.window.showMask(true);
                    selections.map(function (formData) {
                        formData.sectionWaterDepth = data.sectionWaterDepth;
                        formData.fairtemperature = data.fairtemperature;
                    });

                    Gikam.put('/secure/emc/module/bp/bp/folders/instant', Gikam.getJsonWrapper({}, ['emcFolderServiceImpl', selections])).done(function (data) {
                        grid.refresh();
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },
    fillCJWDesc: function () {
        var grid = Gikam.getComp('emc-sample-expand-detail-list-folder-grid');
        var rows = grid.getSelections();
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        Gikam.create('modal', {
            width: 600,
            height: 313,
            title: '沉积物信息',
            url: emcSample.baseUrl + '/page/folder-cjw-desc-detail',
            onAfterClose: function (data) {
                if (!Gikam.isEmpty(data)) {
                    var selections = grid.getSelections();
                    workspace.window.showMask(true);
                    selections.map(function (formData) {
                        formData.river = data.river;
                        formData.section = data.section;
                        formData.sectionWaterDepth = data.sectionWaterDepth;
                    });

                    Gikam.put('/secure/emc/module/bp/bp/folders/instant', Gikam.getJsonWrapper({}, ['emcFolderServiceImpl', selections])).done(function (data) {
                        grid.refresh();
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },
    fillTRDesc: function () {
        var grid = Gikam.getComp('emc-sample-expand-detail-list-folder-grid');
        var rows = grid.getSelections();
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        Gikam.create('modal', {
            width: 600,
            height: 313,
            title: '土壤信息',
            url: emcSample.baseUrl + '/page/folder-tr-desc-detail',
            onAfterClose: function (data) {
                if (!Gikam.isEmpty(data)) {
                    var selections = grid.getSelections();
                    workspace.window.showMask(true);
                    selections.map(function (formData) {
                        formData.longitude = data.longitude;
                        formData.latitude = data.latitude;
                    });

                    Gikam.put('/secure/emc/module/bp/bp/folders/instant', Gikam.getJsonWrapper({}, ['emcFolderServiceImpl', selections])).done(function (data) {
                        grid.refresh();
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },
    fillOrderDesc: function () {
        var grid = Gikam.getComp('emc-sample-expand-detail-list-order-grid');
        var rows = grid.getSelections();
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        Gikam.create('modal', {
            width: 600,
            height: 360,
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ORDER_DESC_FILL',
            url: emcSample.baseUrl + '/page/order-desc-detail',
            onAfterClose: function (data) {
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
                    if (!Gikam.isEmpty(data.ext$.samplingdepth)) {
                        orderDesc += data.ext$.samplingdepth + '、';
                    }
                    orderDesc = orderDesc.substring(0, orderDesc.length - 1);
                    rows.map(function (item) {
                        item.orderDesc = orderDesc;
                    });
                    Gikam.put(emcSample.baseUrl + '/' + formData.id, Gikam.getJsonWrapper({}, ['emcOrderServiceImpl', rows])).done(function () {
                        grid.refresh()
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    soilOrderDesc: function () {
        var grid = Gikam.getComp('emc-sample-expand-detail-list-order-grid');
        var rows = grid.getSelections();
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        Gikam.create('modal', {
            width: 600,
            height: 400,
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ORDER_DESC_SOIL',
            url: emcSample.baseUrl + '/page/soli-order-desc-detail',
            onAfterClose: function (data) {
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
                    // if (!Gikam.isEmpty(data.ext$.othermatter)) {
                    //     orderDesc += data.ext$.othermatter + '、';
                    // }
                    // if (!Gikam.isEmpty(data.ext$.odor)) {
                    //     orderDesc += data.ext$.odor + '、';
                    // }
//                    if (!Gikam.isEmpty(data.ext$.humidity)) {
//                        orderDesc += data.ext$.humidity + '、';
//                    }
                    if (!Gikam.isEmpty(data.ext$.ordersmell)) {
                        orderDesc += data.ext$.ordersmell + '、';
                    }
                    orderDesc = orderDesc.substring(0, orderDesc.length - 1);
                    rows.map(function (item) {
                        item.ext$.color = data.ext$.color;
                        item.ext$.odor = data.ext$.ordersmell;
                        item.ext$.texture = data.ext$.texture;
                        item.ext$.othermatter = data.ext$.othermatter;
                        item.ext$.rootcontent = data.ext$.rootcontent;
                        item.ext$.humidity = data.ext$.humidity;
                        item.orderDesc = orderDesc;
                        item.ext$.samplingdepth = data.ext$.samplingdepth;
                    });
                    Gikam.put(emcSample.baseUrl + '/' + formData.id, Gikam.getJsonWrapper({}, ['emcOrderServiceImpl', rows])).done(function () {
                        grid.refresh()
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    solidWasteOrderDesc: function () {
        var grid = Gikam.getComp('emc-sample-expand-detail-list-order-grid');
        var rows = grid.getSelections();
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        var modal = Gikam.create('modal', {
            title: "EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.INPUT_SOLID_WASTE_ORDER_DESC",
            width: 400,
            height: 250,
        });
        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [{
                    type: 'btnToolbar',
                    items: [{
                        type: 'button',
                        text: 'GIKAM.BUTTON.CONFIRM',
                        class: 'blue',
                        onClick: function () {
                            if (form.validate()) {
                                let formData = form.options.data;
                                let orderDesc = [formData.orderType, formData.orderStatusText, formData.color, formData.odor].join(';');
                                rows.forEach(item => {
                                    item.ext$.samplingdepth = formData.samplingDepth;
                                    item.orderDesc = orderDesc;
                                })

                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(emcSample.baseUrl + '/' + rows[0].id, Gikam.getJsonWrapper({}, ['emcOrderServiceImpl', rows])).done(function () {
                                    grid.refresh()
                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close();
                                });
                            }
                        }
                    }, {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CANCEL',
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
                //     field : 'samplingDepth',
                //     title : 'T_EMC_ORDER.SAMPLINGDEPTH',
                //     validators : [ 'notEmpty', 'strLength[0, 60]' ],
                // }, {
                //     field : 'orderType',
                //     title : 'T_EMC_ORDER.ORDERCATEGORY',
                //     validators : [ 'notEmpty', 'strLength[0, 60]' ],
                // }, {
                field: 'orderStatus',
                title: 'T_EMC_ORDER.ORDERSTATUS',
                validators: ['notEmpty', 'strLength[0, 60]'],
                type: 'select',
                category: 'sys',
                param: {
                    codeCategoryId: 'orderStatus',
                },
                onChange: function (field, value, rowIndex, oldValue, text) {
                    this.setData({
                        orderStatus: value,
                        orderStatusText: text
                    });
                }
            }, {
                field: 'color',
                title: 'T_EMC_ORDER.COLOR',
                validators: ['notEmpty', 'strLength[0, 60]'],
            }, {
                field: 'odor',
                title: 'T_EMC_ORDER.ODOR',
                validators: ['notEmpty', 'strLength[0, 60]'],
            }],
            autoSave: false
        });
    },
    wuniOrderDesc: function () {
        var grid = Gikam.getComp('emc-sample-expand-detail-list-order-grid');
        var rows = grid.getSelections();
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        var modal = Gikam.create('modal', {
            title: "污泥样品描述",
            width: 400,
            height: 250,
        });
        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [{
                    type: 'btnToolbar',
                    items: [{
                        type: 'button',
                        text: 'GIKAM.BUTTON.CONFIRM',
                        class: 'blue',
                        onClick: function () {
                            if (form.validate()) {
                                let formData = form.options.data;
                                let orderDesc = [formData.color, formData.odor].join(';');
                                rows.forEach(item => {
                                    item.orderDesc = orderDesc;
                                })

                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(emcSample.baseUrl + '/' + rows[0].id, Gikam.getJsonWrapper({}, ['emcOrderServiceImpl', rows])).done(function () {
                                    grid.refresh()
                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close();
                                });
                            }
                        }
                    }, {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CANCEL',
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
                field: 'color',
                title: 'T_EMC_ORDER.COLOR',
                validators: ['notEmpty', 'strLength[0, 60]'],
            }, {
                field: 'odor',
                title: 'T_EMC_ORDER.ODOR',
                validators: ['notEmpty', 'strLength[0, 60]'],
            }],
            autoSave: false
        });
    },

    getOrderContainerPanels: function () {
        var _this = this;
        return {
            title: '容器标签信息',
            id: 'emc-sample-expand-detail-list-lableno-panel',
            show: false,
            items: [{
                type: 'grid',
                id: 'emc-sample-expand-detail-list-lableno-grid',
                columns: emcSample.getContainerColumns(),
                page: true,
                autoSave: false,
                url: '/secure/emc/module/bp/bp/order-tasks/queries/select-order-container-info',
                requestData: {
                    sampleId: _this.param.sampleId
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
                    text: '批录入',
                    icon: 'edit',
                    onClick: function () {
                        var selections = Gikam.getComp('emc-sample-expand-detail-list-lableno-grid').getSelections();
                        if (Gikam.isEmpty(selections)) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                            return;
                        }

                        var modal = Gikam.create('modal', {
                            title: "GIKAM.BUTTON.BATCH_INPUT",
                            width: 400,
                            height: 150,
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
                                                var formData = form.getData();
                                                var ids = [];
                                                selections.forEach(function (item) {
                                                    ids = ids.concat(item.ext$.ids.split(','));
                                                })
                                                var updateData = ids.map(function (id) {
                                                    return {
                                                        id: id,
                                                        pretreatWay: formData.pretreatWay
                                                    }
                                                });

                                                Gikam.getLastModal().window.showMask(true);
                                                Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, ['emcOrderTaskServiceImpl', updateData])).done(function (data) {
                                                    Gikam.getComp('emc-sample-expand-detail-list-lableno-grid').refresh();
                                                }).always(function () {
                                                    Gikam.getLastModal().window.closeMask();
                                                    modal.close();
                                                });
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
                            fields: [{
                                field: 'pretreatWay',
                                title: '前处理方式',
                                editor: true,
                                type: 'select',
                                multiple: true,
                                category: 'sys',
                                param: {
                                    codeCategoryId: 'pretreatWay',
                                },
                            }],
                            autoSave: false
                        });

                    }
                    // }, {
                    //     type: 'button',
                    //     text: 'GIKAM.BUTTON.VIEWLABEL',
                    //     icon: 'print',
                    //     onClick: function () {
                    //         var selections = Gikam.getComp('emc-sample-expand-detail-list-lableno-grid').getSelections();
                    //         if (Gikam.isEmpty(selections)) {
                    //             Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                    //             return;
                    //         }
                    //         var orderIds = '';
                    //         for (var i = 0; i < selections.length; i++) {
                    //             orderIds += selections[i].orderId + ',';
                    //         }
                    //         if (!Gikam.isEmpty(orderIds)) {
                    //             orderIds = orderIds.substr(0, orderIds.length - 1);
                    //         }
                    //         Gikam.viewReport('PRINT_LABEL_REPORT', selections);
                    //     }
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
            columns: emcSample.getSampleExpandDetailOrderColumns(),
            url: '/secure/emc/module/bp/bp/orders/queries',
            // service: 'emcFolderServiceImpl',
            requestData: {
                sampleId_SEQ: _this.param.sampleId
                // orderDate_filter: '1',
                // target_filter: JSON.stringify({
                //     type: 'assigned',
                //     targetTable: "T_EMC_ORDER",
                //     targetMatchColumn: "FOLDERID",
                //     filter: [{
                //         targetFilterColumn: 'sampleId',
                //         targetFilterValue: _this.param.sampleId
                //     }]
                // })
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

    getQcdataFolderGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-sample-expand-detail-list-qc-data-folder-grid',
            columns: emcSample.getSampleQcNameColumns(),
            onRendered: function () {
                Gikam.post('/secure/emc/module/bp/bp/order-task-results/qcnames/queries', Gikam.getJsonWrapper({sampleId: _this.param.sampleId}, ['', null])).done(function (rows) {
                    var data = rows.map(function (item) {
                        return {
                            qcname: item,
                        };
                    })
                    Gikam.getComp('emc-sample-expand-detail-list-qc-data-folder-grid').loadData(data);
                }).always(function () {
                });
            },
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData(['emc-sample-expand-detail-list-qc-data-order-task-result-grid']);
                }
            },
            onRowActive: function (index, row) {
                _this.onQcdataFolderActive(row);
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
    onQcdataFolderActive: function (row) {
        var _this = this;
        if (row) {
            Gikam.getAsyncComp('emc-sample-expand-detail-list-qc-data-order-task-result-grid').done(function (grid) {
//                    grid.options.checkOneOnActive = true;
                grid.refresh({
                    url: '/secure/emc/module/bp/bp/order-task-results/queries',
                    checkOneOnActive: true,
                    requestData: {
                        qcname_eq: row.qcname,
                        analysisType_SEQ: 'sampling',
                        reportDisplay_seq: '1',
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

    onOrigindataFolderActive: function (row) {
        var _this = this;
        if (row) {
            Gikam.getAsyncComp('emc-sample-expand-detail-list-origin-data-order-task-result-grid').done(function (grid) {
                Gikam.getComp('emc-sample-expand-detail-list-origin-data-order-task-result-grid').refresh({
                    url: '/secure/emc/module/bp/bp/order-task-results/queries',
                    requestData: {
                        // orderno_SEQ: row.ext$.orderno,
                        orderId_SEQ: row.id,
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
                items: [{
                    type: 'grid',
                    id: this.idGrid.resultGridId,
                    service: 'emcOrderTaskResultServiceImpl',
                    columns: emcSample.getOrderTaskResultColumns2(this.param),
                    page: true,
                    order: {
                        itemOrderNo: 'asc'
                    },
                    expand: true,
                    pageSize: 100,
                    onLoadSuccess: function (data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(1);
                        }
                    },
                    onRendered: function () {
                        var formData = Gikam.getComp('emc-order-task-result-edit-list-button-group-by-test');
                        if (formData != undefined) {
                            formData.clickHandle();
                        }
                    },
                    beforeUpdate: function (data) {
                        if (!Gikam.isEmpty(data.originalResult) && data.row && data.row.submitFlag == 1) {
                            Gikam.create('modal', {
                                width: 400,
                                height: 200,
                                title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.TITLE.UPDATE_REASON',
                                url: '/secure/emc/module/bp/bp/order-tasks/page/update-result-reason',
                                onAfterClose: function (reasonData) {
                                    if (!Gikam.isEmpty(reasonData)) {
                                        workspace.window.showMask(true);
                                        var resultLog = {
                                            orderTaskId: data.row.orderTaskId,
                                            resultId: data.row.id,
                                            reason: reasonData.reason,
                                            value: data.originalResult,
                                        };
                                        Gikam.post(Gikam.printf('/secure/emc/module/bp/bp/order-tasks/{id}/result-logs', {
                                            id: data.row.orderTaskId
                                        }), Gikam.getJsonWrapper({
                                            f: {
                                                folderId: data.row.folderId,
                                            }
                                        }, ['', [resultLog]])).done(function () {
                                            Gikam.getComp(_this.idGrid.resultGridId).refresh();
                                        }).always(function () {
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
                    onUpdated: function (data, fields, index) {
                        var grid = this;

                        var folderRow = Gikam.getComp('emc-sample-expand-detail-list-origin-data-folder-grid').getActivedRow();
                        if (fields.indexOf('originalResult') > -1 || fields.indexOf('standQty') > -1 || fields.indexOf('lowLimit') > -1 || fields.indexOf('highLimit') > -1 || fields.indexOf('revisionRule') > -1) {

                            Gikam.getLastModal().window.showMask(true);
                            var orderTaskResultUrl = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results';

                            Gikam.put(orderTaskResultUrl + '/action/change-result', Gikam.getJsonWrapper({}, ['', [data]])).done(function () {
                                Gikam.post(orderTaskResultUrl + '/queries', Gikam.getJsonWrapper({
                                    n: -1,
                                    f: {
                                        folderId_SEQ: folderRow.id,
                                        analysisType_SEQ: 'sampling',
                                        targetType: 'samplingInput',
                                        target_filter: JSON.stringify({
                                            type: 'assigned',
                                            targetTable: "T_EMC_ORDER",
                                            targetMatchColumn: "ID",
                                            thisMatchColumn: "ORDERID",
                                            filter: [{
                                                targetFilterColumn: 'sampleId',
                                                targetFilterValue: _this.param.id
                                            }]
                                        })
                                    }
                                })).done(function (data) {
                                    Gikam.getComp('emc-sample-expand-detail-list-origin-data-order-task-result-grid').refresh();
//                                    grid.refreshByData(data, true);

                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                });

                            }).fail(function () {
                                Gikam.getLastModal().window.closeMask();
                            });
                        }
                    },
                    toolbar: [{
                        type: 'button',
                        text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.GROUP_BY_ORDER',
                        id: 'emc-order-task-result-edit-list-button-group-by-order',
                        icon: 'edit',
                        onClick: function () {
                            var grid = Gikam.getComp(_this.idGrid.resultGridId);
                            grid.refreshGroup({
                                fields: ['orderNo'],
                                expand: true,
                                formatter: function (row) {
                                    return row.orderNo;
                                }
                            });
                            Gikam.getComp('emc-order-task-result-edit-list-button-group-by-test').show();
                            Gikam.getComp('emc-order-task-result-edit-list-button-group-by-order').hide();
                        }
                    }, {
                        type: 'button',
                        text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.GROUP_BY_TEST',
                        id: 'emc-order-task-result-edit-list-button-group-by-test',
                        icon: 'edit',
                        onClick: function () {
                            var grid = Gikam.getComp(_this.idGrid.resultGridId);
                            grid.refreshGroup({
                                fields: ['ext$.testname'],
                                expand: true,
                                formatter: function (row) {
                                    return row.ext$.testname;
                                }
                            });
                            Gikam.getComp('emc-order-task-result-edit-list-button-group-by-test').hide();
                            Gikam.getComp('emc-order-task-result-edit-list-button-group-by-order').show();
                        }
                    }, {
                        type: 'button',
                        text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.TRIGGER_FORMLUA',
                        icon: 'activate',
                        onClick: function () {
                            _this.triggerFormlua();
                        }
                    },
//                      {
//                        type : 'button',
//                        text : 'GIKAM.BUTTON.BATCH_INPUT',
//                        icon : 'add',
//                        onClick : function() {
//                            _this.batchInputResult2();
//                        }
//                    },
                        {
                            type: 'dropDownMenu',
                            text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.MORE_OPERATE',
                            items: [{
                                type: 'button',
                                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SYNC_TMPL_INFO',
                                icon: 'edit',
                                onClick: function () {
                                    Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SYNC_TMPL_INFO', 'EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.SYNC_TMPL_INFO', function () {
                                        _this.syncTmplInfo1();
                                    });
                                },
                            }, {
                                type: 'button',
                                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.DEFAULT_RESULT',
                                icon: 'edit',
                                onClick: function () {
                                    Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.DEFAULT_RESULT', 'EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.DEFAULT_RESULT', function () {
                                        _this.orderTaskDefaultResult();
                                    });
                                },
                            }, {
                                type: 'button',
                                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SET_FINISHED',
                                icon: 'edit',
                                onClick: function () {
                                    _this.setDoneOrderTaskResult();
                                }
                            }, {
                                type: 'button',
                                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.REMOVE_FOMULA',
                                icon: 'edit',
                                onClick: function () {
                                    Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.REMOVE_FOMULA', 'EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.REMOVE_FOMULA', function () {
                                        _this.removeFomula();
                                    });
                                }
                            }, {
                                type: 'button',
                                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.ADD_REPEATE_NUMBER',
                                icon: 'add',
                                onClick: function () {
                                    _this.addRepeateNumber();
                                }
                            }, {
                                type: 'button',
                                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.DEL_REPEATE_NUMBER',
                                icon: 'remove-row',
                                onClick: function () {
                                    Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.DEL_REPEATE_NUMBER', 'EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.DEL_REPEATE_NUMBER', function () {
                                        _this.deleteRepeateNumber();
                                    });
                                }
                            }, {
                                type: 'button',
                                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.EDIT_ITEM',
                                icon: 'edit',
                                onClick: function () {
                                    _this.editItem();
                                }
                            },]
                        }, {
                            type: 'button',
                            text: '仪器文件采集',
                            icon: 'edit',
                            onClick: function () {
                                _this.equiptCollect();
                            }
                        }, {
                            type: 'button',
                            text: '采样基准含氧量',
                            icon: 'search',
                            onClick: function () {
                                _this.sampleSetGrid();
                            }
                        }],
                }]
            }]
        }
    },

    equiptCollect: function () {
        var _this = this;

        Gikam.create('simpleUploader', {
            id: 'emc-sample-expand-detail-list-equipt-collect-uploader',
            dbTable: 'T_EMC_EQUIPT',
            bizId: _this.param.sampleId,
            multiple: true,
            onAfterClose: function (fileList) {
                if (!Gikam.isEmpty(fileList)) {
                    workspace.window.showMask(true);
                    var url = emcSample.baseUrl + '/equipt-collect';
                    fileList.map(function (file) {
                        file.size = 0;
                    });
                    Gikam.post(url, Gikam.getJsonWrapper({}, ['coreFileServiceImpl', fileList])).done(function () {
                        // Gikam.getComp('emc-run-edit-list-test-run-grid').refresh();
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            }
        })

    },

    getQcdataResultGridParam: function () {
        var _this = this;
        return {
            type: 'tab',
            panels: [{
                title: 'EMC.MODULE.BP.BP.FOLDERS.ORDERS.MODAL.RESULT',
                items: [{
                    type: 'grid',
                    id: this.idGrid.qcresultGridId,
                    service: 'emcOrderTaskResultServiceImpl',
                    columns: emcSample.getQcOrderTaskResultColumns(this.param),
                    page: true,
                    pageSize: 100,
                    onLoadSuccess: function (data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(1);
                        }
                    },
                    onRendered: function () {
                        var formData = Gikam.getComp('emc-order-task-qc-result-edit-list-button-group-by-order');
                        if (formData != undefined) {
                            formData.clickHandle();
                        }
                    },
                    beforeUpdate: function (data) {
                        if (!Gikam.isEmpty(data.originalResult) && data.row && data.row.submitFlag == 1) {
                            Gikam.create('modal', {
                                width: 400,
                                height: 200,
                                title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.TITLE.UPDATE_REASON',
                                url: '/secure/emc/module/bp/bp/order-tasks/page/update-result-reason',
                                onAfterClose: function (reasonData) {
                                    if (!Gikam.isEmpty(reasonData)) {
                                        workspace.window.showMask(true);
                                        var resultLog = {
                                            orderTaskId: data.row.orderTaskId,
                                            resultId: data.row.id,
                                            reason: reasonData.reason,
                                            value: data.originalResult,
                                        };
                                        Gikam.post(Gikam.printf('/secure/emc/module/bp/bp/order-tasks/{id}/result-logs', {
                                            id: data.row.orderTaskId
                                        }), Gikam.getJsonWrapper({
                                            f: {
                                                folderId: data.row.folderId,
                                            }
                                        }, ['', [resultLog]])).done(function () {
                                            Gikam.getComp(_this.idGrid.resultGridId).refresh();
                                        }).always(function () {
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
                    onUpdated: function (data, fields, index) {
                        var grid = this;
                        var folderRow = grid.getActivedRow();

                        if (fields.indexOf('originalResult') > -1 || fields.indexOf('standQty') > -1 || fields.indexOf('lowLimit') > -1 || fields.indexOf('highLimit') > -1 || fields.indexOf('revisionRule') > -1) {

                            Gikam.getLastModal().window.showMask(true);
                            var orderTaskResultUrl = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results';

                            Gikam.put(orderTaskResultUrl + '/action/change-result', Gikam.getJsonWrapper({}, ['', [data]])).done(function () {
                                Gikam.post(orderTaskResultUrl + '/queries', Gikam.getJsonWrapper({
                                    n: -1,
                                    f: {
                                        folderId_SEQ: folderRow.folderId,
                                        qccategorycode_differ: 'N/A',
                                        analysisType_SEQ: 'sampling',
                                        targetType: 'samplingInput',
                                        target_filter: JSON.stringify({
                                            type: 'assigned',
                                            targetTable: "T_EMC_ORDER",
                                            targetMatchColumn: "ID",
                                            thisMatchColumn: "ORDERID",
                                            filter: [{
                                                targetFilterColumn: 'sampleId',
                                                targetFilterValue: _this.param.id
                                            }]
                                        })
                                    }
                                })).done(function (data) {

                                    grid.refresh();

                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                });

                            }).fail(function () {
                                Gikam.getLastModal().window.closeMask();
                            });
                        }
                    },
                    toolbar: [],
                }]
            }]
        }
    },
    triggerQcFormlua: function () {
        var _this = this;
        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.TRIGGER_FORMLUA', 'EMC.MODULE.BP.RESULT.RUNS.TIP.TRIGGER_FORMLUA', function () {
            var gridId = _this.idGrid.qcresultGridId;
            var selections = Gikam.getComp(gridId).getSelections();
            if (Gikam.isEmpty(selections)) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                return;
            }
            var orders = [];
            selections.map(function (item) {
                var order = {};
                order.id = item.orderId;
                orders.push(order);
            })
            var url = IFM_CONTEXT + '/secure/emc/module/bp/result/runs/action/trigger-formlua';
            Gikam.getLastModal().window.showMask(true);
            Gikam.postText(url, Gikam.getJsonWrapper({"emission": "1"}, [null, orders])).done(function () {
                Gikam.getComp(gridId).refresh();
            }).always(function () {
                Gikam.getLastModal().window.closeMask();
            });
        });
    },

    triggerFormlua: function () {
        var _this = this;
        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.TRIGGER_FORMLUA', 'EMC.MODULE.BP.RESULT.RUNS.TIP.TRIGGER_FORMLUA', function () {
            var gridId = _this.idGrid.resultGridId;
            var selections = Gikam.getComp(gridId).getSelections();
            if (Gikam.isEmpty(selections)) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                return;
            }
            var orders = [];
            selections.map(function (item) {
                var order = {};
                order.id = item.orderId;
                orders.push(order);
            })
            var url = IFM_CONTEXT + '/secure/emc/module/bp/result/runs/action/trigger-formlua';
            Gikam.getLastModal().window.showMask(true);
            Gikam.postText(url, Gikam.getJsonWrapper({"emission": "1"}, [null, orders])).done(function () {
                Gikam.getComp(gridId).refresh();
            }).always(function () {
                Gikam.getLastModal().window.closeMask();
            });
        });
    },

    orderTaskResultDataGridTriggerFormlua: function () {
        var _this = this;
        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.TRIGGER_FORMLUA', 'EMC.MODULE.BP.RESULT.RUNS.TIP.TRIGGER_FORMLUA', function () {
            var gridId = 'emc-sample-expand-detail-list-record-data-order-task-result-grid';
            var selections = Gikam.getComp(gridId).getSelections();
            if (Gikam.isEmpty(selections)) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                return;
            }
            var orders = [];
            selections.map(function (item) {
                var order = {};
                order.id = item.orderId;
                orders.push(order);
            })
            var url = IFM_CONTEXT + '/secure/emc/module/bp/result/runs/action/trigger-formlua';
            Gikam.getLastModal().window.showMask(true);
            Gikam.postText(url, Gikam.getJsonWrapper({"emission": "1"}, [null, orders])).done(function () {
                Gikam.getComp(gridId).refresh();
            }).always(function () {
                Gikam.getLastModal().window.closeMask();
            });
        });
    },

    /** 批录入结果 */
    batchInputResult2: function () {
        var _this = this;
        var selections = Gikam.getComp(_this.idGrid.resultGridId).getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        var orderRow = Gikam.getComp('emc-sample-expand-detail-list-origin-data-folder-grid').getActivedRow();

        var orderTaskIds = selections.map(function (item) {
            return item.orderTaskId;
        });
        var uniqueOrderTaskIds = Array.from(new Set(orderTaskIds));

        Gikam.create('modal', {
            width: '60%',
            height: '70%',
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.TITLE.CHOOSE_ORDER_TASK',
            url: emcSample.baseUrl + '/page/order-result-batch-input' + Gikam.param({
                orderno_SEQ: orderRow.orderNo,
                analysisType_SEQ: 'sampling',
                id_DIFFER: uniqueOrderTaskIds.join()
            }),
            onAfterClose: function (data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getLastModal().window.showMask(true);
                    Gikam.postText(emcSample.baseUrl + '/order-tasks/action/batch-input', Gikam.getJsonWrapper({}, [null, selections], [null, data])).done(function () {
                        Gikam.getComp(_this.idGrid.resultGridId).refresh();
                    }).always(function () {
                        Gikam.getLastModal().window.closeMask();
                    });
                }
            }
        });
    },

    syncTmplInfo1: function () {
        var _this = this;
        var selections = Gikam.getComp(_this.idGrid.resultGridId).getSelections();
        if (selections.length == 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }
        selections.map(function (item) {
            item.ext$.analysisType = 'sampling'
        });
        workspace.window.showMask(true);
        Gikam.postText('/secure/emc/module/bp/bp/orders/action/sync-tmpl-infos', Gikam.getJsonWrapper({}, [null, selections])).done(function () {
            Gikam.getComp(_this.idGrid.resultGridId).refresh();
        }).always(function () {
            workspace.window.closeMask();
        });
    },

    orderTaskDefaultResult: function () {
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
        Gikam.postText(url, Gikam.getJsonWrapper(null, ['emcFolderServiceImpl', dataInputSelections], ['emcOrderTaskResultServiceImpl', selections])).done(function () {
            Gikam.each(selections, function (index, item) {
                Gikam.getComp(_this.idGrid.resultGridId).refresh();
            });
        }).always(function () {
            workspace.window.closeMask();
        });
    },

    setDoneOrderTaskResult: function () {
        var _this = this;
        Gikam.create('modal', {
            width: '20%',
            height: '30%',
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.INVALID_DATA',
            url: emcSample.baseUrl + '/page/invalid-results',
            onAfterClose: function (data) {
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

                        resultSelections.forEach(function (item) {
                            item.ext$.reason = data.reason;
                        });

                        Gikam.put('/secure/emc/module/bp/bp/orders/results/action/set-done', Gikam.getJsonWrapper({}, [null, resultSelections])).done(function () {
                            Gikam.getComp(_this.idGrid.resultGridId).refresh();
                            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.UPDATE_SUCCESS');
                        }).always(function () {
                            Gikam.getLastModal().window.closeMask();
                        });
                    } else if (data.operatedata == 'noresult') {

                        var orderSelections = Gikam.getComp('emc-sample-data-input-detail-list-grid').getSelections();
                        if (Gikam.isEmpty(orderSelections)) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                            return;
                        }

                        orderSelections.forEach(function (item) {
                            item.ext$.reason = data.reason;
                            item.ext$.sampleid = _this.param.id;
                        });
                        Gikam.put('/secure/emc/module/bp/bp/orders/folders/action/set-done', Gikam.getJsonWrapper({}, [null, orderSelections])).done(function () {
                            Gikam.getComp(_this.idGrid.resultGridId).refresh();
                            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.UPDATE_SUCCESS');
                        }).always(function () {
                            Gikam.getLastModal().window.closeMask();
                        });
                    }
                }
            }
        });
    },

    removeFomula: function () {
        var _this = this;
        var selections = Gikam.getComp(_this.idGrid.resultGridId).getSelections();
        if (selections.length == 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }
        workspace.window.showMask(true);
        Gikam.postText('/secure/emc/module/bp/bp/orders/action/remove-fomulas', Gikam.getJsonWrapper({}, [null, selections])).done(function () {
            Gikam.getComp(_this.idGrid.resultGridId).refresh();
        }).always(function () {
            workspace.window.closeMask();
        });
    },

    addRepeateNumber: function () {
        var _this = this;
        var selections = Gikam.getComp(_this.idGrid.resultGridId).getSelections();
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
                        Gikam.getComp(_this.idGrid.resultGridId).refresh();
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    editItem: function () {
        var _this = this;
        var data = Gikam.getComp(_this.idGrid.resultGridId).getActivedRow();
        if (Gikam.isEmpty(data)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.EDIT_ITEM',
            url: emcSample.baseUrl + '/page/edit-item' + Gikam.param({
                id: data.orderTaskId,
            }),
            onAfterClose: function (afterData) {
                var folderData = Gikam.getComp('emc-sample-expand-detail-list-origin-data-folder-grid').getSelections();
                if (Gikam.isEmpty(afterData) || Gikam.isEmpty(folderData)) {
                    return;
                }
                workspace.window.showMask(true);
                Gikam.post(emcSample.baseUrl + '/edit-item', Gikam.getJsonWrapper({
                    f: {
                        sampleId: _this.param.sampleId
                    }
                }, ['emcOrderTaskResultServiceImpl', [data]], ['emcItemServiceImpl', afterData], ['emcFolderServiceImpl', folderData])).done(function () {
                    Gikam.getComp(_this.idGrid.resultGridId).refresh();
                }).always(function () {
                    workspace.window.closeMask();
                });
            }
        })
    },

    deleteRepeateNumber: function () {
        var _this = this;
        var selections = Gikam.getComp(_this.idGrid.resultGridId).getSelections();
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
            Gikam.getComp(_this.idGrid.resultGridId).refresh();
        }).always(function () {
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
        Gikam.postText('/secure/emc/module/bp/bp/orders/action/sync-tmpl-infos', Gikam.getJsonWrapper({}, [null, allData])).done(function () {
            var activedRow = Gikam.getComp('emc-sample-expand-detail-list-record-data-order-task-grid').getActivedRow()
            _this.refreshRecordResultData(activedRow);
        }).always(function () {
            workspace.window.closeMask();
        });
    },


    // editItem: function () {
    //     var _this = this;
    //     var selections = Gikam.getComp('emc-sample-expand-detail-list-origin-data-order-task-result-grid').getSelections();
    //     if (selections.length != 1) {
    //         Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
    //         return;
    //     }
    //     var data = selections[0];
    //     Gikam.create('modal', {
    //         title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.EDIT_ITEM',
    //         url: emcSample.baseUrl + '/page/edit-item' + Gikam.param({
    //             id: data.orderTaskId,
    //         }),
    //         onAfterClose: function (afterData) {
    //             if (Gikam.isEmpty(afterData) || Gikam.isEmpty(afterData[0]) || Gikam.isEmpty(afterData[1])) {
    //                 return;
    //             }
    //             workspace.window.showMask(true);
    //             Gikam.post(emcSample.baseUrl + '/edit-item', Gikam.getJsonWrapper({
    //                 f: {
    //                     sampleId: _this.param.id
    //                 }
    //             }, ['emcOrderTaskResultServiceImpl', [data]], ['emcItemServiceImpl', afterData[0]], ['emcFolderServiceImpl', afterData[1]])).done(function () {
    //                 Gikam.getComp('emc-sample-expand-detail-list-origin-data-order-task-result-grid').refresh();
    //             }).always(function () {
    //                 workspace.window.closeMask();
    //             });
    //         }
    //     })
    // },

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
            _this.getSamplePanels(),
            _this.getFolderPanels(),
            _this.getOrderPanels(),
            _this.getOrderContainerPanels(),
            _this.getEquiptPanels(),
            _this.getEquiptSetPanels(),
            _this.getGasEquiptSetPanels(),
            _this.getTemperatureSetPanels(),//温度传感器性能核查
            _this.getSwEquiptSetPanels(),
            _this.getRecordDataPanels(),//记录数据
            _this.getOriginDataPanels(),//原始数据
            _this.getQcDataPanels(),//原始数据
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
            readonly: "true" === _this.param.readonly,
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
        Gikam.getAsyncComp('emc-sample-expand-detail-list-lableno-grid').done(function (grid) {
            grid.options.checkOneOnActive = true;
        });
        Gikam.getAsyncComp('emc-sample-expand-detail-list-order-grid').done(function (grid) {
            grid.options.checkOneOnActive = true;
        });
        Gikam.getAsyncComp('emc-sample-expand-detail-list-order-grid').done(function (grid) {
            grid.options.checkOneOnActive = true;
        });
        Gikam.getAsyncComp('emc-sample-expand-detail-list-folder-grid').done(function (grid) {
            grid.options.checkOneOnActive = true;
        });
        Gikam.getAsyncComp('emc-sample-expand-detail-list-equipt-grid').done(function (grid) {
            grid.options.checkOneOnActive = true;
        });
        Gikam.getAsyncComp('emc-sample-expand-detail-list-equipt-set-grid').done(function (grid) {
            grid.options.checkOneOnActive = true;
        });
        Gikam.getAsyncComp('emc-sample-expand-detail-list-record-data-order-task-grid').done(function (grid) {
            grid.options.checkOneOnActive = true;
        });
        Gikam.getAsyncComp('emc-sample-expand-detail-list-origin-data-folder-grid').done(function (grid) {
            grid.options.checkOneOnActive = true;
        });
        Gikam.getAsyncComp('emc-sample-expand-detail-list-record-data-order-task-grid').done(function (grid) {
            grid.options.checkOneOnActive = true;
        });
        Gikam.getAsyncComp('emc-sample-expand-detail-list-origin-data-folder-grid').done(function (grid) {
            grid.options.checkOneOnActive = true;
        });
        Gikam.getAsyncComp('emc-sample-expand-detail-list-qc-data-order-task-result-grid').done(function (grid) {
            grid.options.checkOneOnActive = true;
        });
        Gikam.getAsyncComp('emc-sample-expand-detail-list-origin-data-order-task-result-grid').done(function (grid) {
            grid.options.checkOneOnActive = true;
        });

        Gikam.getAsyncComp('emc-sample-expand-detail-list-check-grid').done(function (grid) {
            grid.options.checkOneOnActive = true;
        });
        Gikam.getAsyncComp('emc-sample-expand-detail-list-report-grid').done(function (grid) {
            grid.options.checkOneOnActive = true;
        });
    },

    idGrid: {
        resultGridId: 'emc-sample-expand-detail-list-origin-data-order-task-result-grid',
        qcresultGridId: 'emc-sample-expand-detail-list-qc-data-order-task-result-grid',
    },

    init: function (param) {
        var _this = this;
        _this.param = param;
        _this.createPage();
    },

}
