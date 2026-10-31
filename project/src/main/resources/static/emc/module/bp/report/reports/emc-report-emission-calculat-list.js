emcReport.emissionCalculatListPage = {

    getFolderGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            page: true,
            id: 'emc-report-emission-calculat-list-folder-grid',
            columns: emcReport.getEmissionFolderListColumns(),
            url: '/secure/emc/module/bp/bp/folders/queries',
            requestData: {
                ORDERCATEGORY_SEQ: '有组织废气',
                targetType: 'emission',
                EMISSIONSTATUS_SEQ: '0',
            },
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-report-emission-calculat-list-order-grid').cleanData();
                }
            },
            onRowActive: function (index, row) {
                _this.onFolderActive(row);
            },
            toolbar: [{
                type: 'button',
                text: '完成',
                icon: 'modify',
                onClick: function () {
                    _this.emission();
                }
            }, {
                type: 'form',
                autoSave: false,
                fields: _this.getShowAllField()
            }],
        }
    },

    emission: function () {
        var orderTaskGrid = Gikam.getComp('emc-report-emission-calculat-list-folder-grid');
        var selections = orderTaskGrid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var saveData = [];
        selections.map(function (selection) {
            saveData.push({
                emissionStatus: '1',
                id: selection.id,
            })
        });

        workspace.window.showMask(true);
        Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, ['emcFolderServiceImpl', saveData])).done(function (id) {
            Gikam.getComp('emc-report-emission-calculat-list-folder-grid').refresh();
        }).always(function () {
            workspace.window.closeMask();
        });
    },

    getShowAllField: function () {
        return [{
            title: '显示全部',
            field: 'showFlag',
            type: 'simpleCheckbox',
            value: '0',
            onChange: function (field, value, index) {
                workspace.window.showMask(true);
                var grid = Gikam.getComp('emc-report-emission-calculat-list-folder-grid');

                if (value === '1') {
                    grid.refresh({
                        requestData: {
                            EMISSIONSTATUS_SEQ: '',
                        }
                    }).done(function () {
                        workspace.window.closeMask();
                    });
                } else {
                    grid.refresh({
                        requestData: {
                            EMISSIONSTATUS_SEQ: '0',
                        }
                    }).done(function () {
                        workspace.window.closeMask();
                    });
                }
            },
        }]
    },

    onFolderActive: function (row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-report-emission-calculat-list-order-grid').refresh({
                url: '/secure/emc/module/bp/bp/orders/queries',
                requestData: {
                    folderId_SEQ: row.id
                }
            });
        }
    },

    getOrderGrid: function () {
        var _this = this;
        return {
            type: 'grid',
            page: true,
            id: 'emc-report-emission-calculat-list-order-grid',
            columns: emcReport.getEmissionOrderListColumns(),
            url: '',
            requestData: {},
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-report-emission-calculat-list-result-grid').cleanData();
                }
            },
            onRowActive: function (index, row) {
                _this.onOrderActive(row);
            },
            toolbar: [{
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.TRIGGER_FORMLUA',
                icon: 'activate',
                onClick: function () {
                    _this.triggerFormlua();
                }
            },],
        }
    },

    triggerFormlua: function () {
        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.TRIGGER_FORMLUA', 'EMC.MODULE.BP.RESULT.RUNS.TIP.TRIGGER_FORMLUA', function () {
            var selections = Gikam.getComp('emc-report-emission-calculat-list-order-grid').getSelections();
            if (Gikam.isEmpty(selections)) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                return;
            }
            var url = IFM_CONTEXT + '/secure/emc/module/bp/result/runs/action/trigger-formlua';
            Gikam.window.showMask(true);
            Gikam.postText(url, Gikam.getJsonWrapper({emission: '1'}, ['emcOrderServiceImpl', selections])).done(function () {
                Gikam.getComp('emc-report-emission-calculat-list-order-grid').refresh();
            }).always(function () {
                Gikam.window.closeMask();
            });
        });
    },

    onOrderActive: function (row) {
        var _this = this;
        var readOnly = true;
        Gikam.postText('/secure/emc/module/mdm/bizmdm/std-set-categorys/field-sets/queries', Gikam.getJsonWrapper({
            f: {
                qcCategoryId: row.qcCategoryId,
                qcCategoryCode: row.qcCategoryCode
            },
            n: -1,
            s: 50
        })).done(function (str) {
            var jsonArr = JSON.parse(str);
            var arr = [];
            jsonArr.forEach(e => {

                if (e.display === 'displayedit') {
                    if (e.fieldName === 'standQty' && row.qcCategoryCode === 'QC-STD') {
                        // 标准值特殊处理
                        arr.push({
                            field: e.fieldName,
                            title: e.fieldTitle,
                            editor: readOnly ? false : true,
                            width: e.width,
                            sort: false,
                            textStyleFormatter: function () {
                                return {
                                    backgroundColor: 'palegreen'
                                }
                            },
                            onBeforeEditorRender: function (currData) {
                                //计算质控结果
                                if (currData.ext$.calqcresult == '1') {
                                    return true;
                                }
                                return false;
                            },
                            onChange: function (name, value, index) {
                                let uncertainty = this.getData()[index].rn16;
                                if (Gikam.isNotEmpty(value) && uncertainty) {
                                    this.setData({
                                        index: index,
                                        indexLowLimit: (Number(value) * 10000 - Number(uncertainty) * 10000) / 10000,
                                        indexHighLimit: (Number(value) * 10000 + Number(uncertainty) * 10000) / 10000,
                                        standQty: value
                                    });
                                    return;
                                }
                                this.setData({
                                    index: index,
                                    indexLowLimit: '',
                                    indexHighLimit: '',
                                    standQty: value
                                });
                                return;
                            },
                            formatter: function (i, v, r) {
                                return Gikam.scientificNotation(v);
                            }
                        });
                    } else if (e.fieldName === 'rn16' && row.qcCategoryCode === 'QC-STD') {
                        // 不确定度特殊处理
                        arr.push({
                            field: e.fieldName,
                            title: e.fieldTitle,
                            editor: readOnly ? false : true,
                            width: e.width,
                            sort: false,
                            textStyleFormatter: function () {
                                return {
                                    backgroundColor: 'palegreen'
                                }
                            },
                            onBeforeEditorRender: function (currData) {
                                //计算质控结果
                                if (currData.ext$.calqcresult == '1') {
                                    return true;
                                }
                                return false;
                            },
                            onChange: function (name, value, index) {
                                let stdValue = this.getData()[index].standQty;
                                if (Gikam.isNotEmpty(value) && stdValue) {
                                    this.setData({
                                        index: index,
                                        indexLowLimit: (Number(stdValue) * 10000 - Number(value) * 10000) / 10000,
                                        indexHighLimit: (Number(stdValue) * 10000 + Number(value) * 10000) / 10000,
                                        rn16: value
                                    });
                                    return;
                                }
                                this.setData({
                                    index: index,
                                    indexLowLimit: '',
                                    indexHighLimit: '',
                                    rn16: value
                                });
                            },
                            formatter: function (i, v, r) {
                                return Gikam.scientificNotation(v);
                            }
                        });
                    } else if (e.fieldName === 'rn10' || e.fieldName === 'rn11') {
                        // rn10 : 相对误差修约规则，rn11 : 相对偏差修约规则
                        arr.push({
                            field: e.fieldName,
                            title: e.fieldTitle,
                            editor: readOnly ? false : true,
                            width: e.width,
                            sort: false,
                            type: 'choose',
                            category: 'rounding-rule',
                            single: true,
                            onAfterChoose: function (back, index) {
                                if (back) {
                                    var item = {};
                                    item['index'] = index;
                                    item[e.fieldName] = back;
                                    this.setData(item);
                                }
                            },
                        });
                    } else if (e.fieldName === 'rn12' && row.qcCategoryCode === 'QC-STD') {
                        // rn12：加标量单位
                        arr.push({
                            field: e.fieldName,
                            title: e.fieldTitle,
                            editor: readOnly ? false : true,
                            width: e.width,
                            sort: false,
                            type: 'choose',
                            category: 'unit',
                            single: true,
                            onBeforeEditorRender: function (currData) {
                                //计算质控结果
                                if (currData.ext$.calqcresult == '1') {
                                    return true;
                                }
                                return false;
                            },
                            styleFormatter: function (row) {
                                if (row.ext$.calqcresult == '1') {
                                    return {
                                        backgroundColor: 'palegreen'
                                    }
                                }
                            },
                            onAfterChoose: function (back, index) {
                                if (back) {
                                    var item = {};
                                    item['index'] = index;
                                    item[e.fieldName] = back[0].unitName;
                                    this.setData(item);
                                }
                            },
                        });
                    } else if (e.fieldName === 'rn12') {
                        // rn12：加标量单位
                        arr.push({
                            field: e.fieldName,
                            title: e.fieldTitle,
                            editor: readOnly ? false : true,
                            width: e.width,
                            sort: false,
                            type: 'choose',
                            category: 'unit',
                            single: true,
                            onAfterChoose: function (back, index) {
                                if (back) {
                                    var item = {};
                                    item['index'] = index;
                                    item[e.fieldName] = back[0].unitName;
                                    this.setData(item);
                                }
                            },
                        });
                    } else if (e.fieldName === 'standQty' && (row.qcCategoryCode === 'L-QXCH' || row.qcCategoryCode === 'L-SPKC')) {
                        // L-QXCH 曲线校准点 standQty：标点值
                        // L-SPKC 样品加标 standQty：加标值
                        //背景绿色
                        arr.push({
                            field: e.fieldName,
                            title: e.fieldTitle,
                            editor: readOnly ? false : true,
                            width: e.width,
                            sort: false,
                            textStyleFormatter: function () {
                                return {
                                    backgroundColor: 'palegreen'
                                }
                            },
                            onBeforeEditorRender: function (currData) {
                                //计算质控结果
                                if (currData.ext$.calqcresult == '1') {
                                    return true;
                                }
                                return false;
                            },
                        });
                    } else {
                        arr.push({
                            field: e.fieldName,
                            title: e.fieldTitle,
                            editor: readOnly ? false : true,
                            width: e.width,
                            sort: false
                        });
                    }
                } else if (e.display === 'displaynoedit') {
                    let field = {
                        field: e.fieldName,
                        title: e.fieldTitle,
                        editor: false,
                        width: e.width,
                        sort: false
                    };

                    if (e.fieldName === 'avgResult' || e.fieldName === 'rn4') {
                        field['formatter'] = function (i, v, r) {
                            return Gikam.scientificNotation(v);
                        }
                    }

                    arr.push(field);
                } else if (Gikam.isEmpty(e.display)) {
                    arr.push({
                        field: e.fieldName,
                        title: e.fieldTitle,
                        editor: false,
                        width: e.width,
                        sort: false
                    });
                }
            });
            var publicColumns = emcReport.getResultPublicColumns();
            var columns = publicColumns.slice(0, 10).concat(arr, publicColumns.slice(10));

            Gikam.getComp('emc-report-emission-calculat-list-result-grid').refreshColumns(columns);

            Gikam.getComp('emc-report-emission-calculat-list-result-grid').refresh({
                url: '/secure/emc/module/bp/bp/order-task-results/queries',
                requestData: {
                    orderId_SEQ: row.id,
                    filter_by_item_list: [{
                        analysisType: 'sampling',
                        REPORTDISPLAY: '1'
                    }, {
                        analysisType: 'analysis',
                        REPORTDISPLAY: '1'
                    }, {
                        analysisType: 'Preprocessing',
                        REPORTDISPLAY: '1'
                    }, {
                        analysisType: 'calculate',
                        REPORTDISPLAY: '1'
                    }],
                }
            }).done(function (data) {
                // var grid = Gikam.getComp('emc-report-emission-calculat-list-result-grid');
                // for (var i = 0; i < data.data.length; i++) {
                //     if (data.data[i].scriptFlag == '0' && Gikam.isEmpty(data.data[i].originalResult)) {
                //         grid.focusField(i, 'originalResult');
                //         break;
                //     }
                // }
            });

        });
    },

    getResultGrid: function () {
        var _this = this;
        return {
            type: 'tab',
            fill: true,
            panels: [{
                title: 'EMC.MODULE.BP.RESULT.RUNS.TAB.RESULT',
                items: [{
                    type: 'grid',
                    id: 'emc-report-emission-calculat-list-result-grid',
                    service: 'emcOrderTaskResultServiceImpl',
                    columns: emcReport.getResultPublicColumns(),
                    onLoadSuccess: function (data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        }
                    },
                    toolbar: [{
                        type: 'button',
                        text: '同步分析项',
                        icon: 'modify',
                        onClick: function () {
                            _this.syncTmplInfo();
                        }
                    },{
                        type : 'button',
                        text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.EDIT_ITEM',
                        icon : 'edit',
                        onClick : function() {
                            var data = Gikam.getComp('emc-report-emission-calculat-list-result-grid').getSelections();
                            if (data.length !== 1) {
                                Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                return;
                            }
                            Gikam.create('modal', {
                                title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.EDIT_ITEM',
                                url : emcReport.baseUrl + '/page/edit-item' + Gikam.param({
                                    testMethodId : data[0].testMethodId,
                                    resultGridId : 'emc-report-emission-calculat-list-result-grid',
                                }),
                                onAfterClose : function(rows) {
                                    if (Gikam.isEmpty(rows)) {
                                        return;
                                    }
                                    workspace.window.showMask(true);
                                    Gikam.post(emcReport.baseUrl + '/edit-item', Gikam.getJsonWrapper({},
                                        [ 'emcOrderTaskResultServiceImpl', data ], [ 'emcItemServiceImpl', rows])).done(function() {
                                        Gikam.getComp('emc-report-emission-calculat-list-result-grid').refresh();
                                    }).always(function() {
                                        workspace.window.closeMask();
                                    });
                                }
                            })
                        }
                    }],
                    // onUpdated: function (data, fields, index) {
                    //     var grid = this;
                    //
                    //     var orderRow = Gikam.getComp('emc-report-emission-calculat-list-order-grid').getActivedRow();
                    //     var qcCategoryCode = orderRow.qcCategoryCode;
                    //
                    //     if (fields.indexOf('originalResult') > -1 ||
                    //         fields.indexOf('standQty') > -1 ||
                    //         fields.indexOf('lowLimit') > -1 ||
                    //         fields.indexOf('highLimit') > -1 ||
                    //         fields.indexOf('revisionRule') > -1 ||
                    //         fields.indexOf('convertFactor') > -1 ||
                    //         (fields.indexOf('rn5') > -1 && (qcCategoryCode === 'QXBD' || qcCategoryCode === 'L-QXCH')) ||
                    //         (fields.indexOf('rn15') > -1 && qcCategoryCode === 'L-SPKC') ||
                    //         (fields.indexOf('rn16') > -1 && qcCategoryCode === 'QC-STD') ||
                    //         fields.indexOf('rn10') > -1 ||
                    //         fields.indexOf('rn11') > -1) {
                    //
                    //         Gikam.window.showMask(true);
                    //         var orderTaskResultUrl = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results';
                    //
                    //         Gikam.put(orderTaskResultUrl + '/action/change-result', Gikam.getJsonWrapper({}, ['', [data]])).done(function () {
                    //             Gikam.post(orderTaskResultUrl + '/queries', Gikam.getJsonWrapper({
                    //                 n: -1,
                    //                 f: {
                    //                     orderId_SEQ: orderRow.id,
                    //                 }
                    //             })).done(function (data) {
                    //                 Gikam.each(data.rows, function (index, row) {
                    //                     grid.setData(row, false, true);
                    //                 });
                    //
                    //             }).always(function () {
                    //                 Gikam.window.closeMask();
                    //             });
                    //
                    //         }).fail(function () {
                    //             Gikam.getLastModal().window.closeMask();
                    //         });
                    //     }
                    // },
                }]
            }]
        }
    },

    syncTmplInfo: function () {
        var selections = Gikam.getComp('emc-report-emission-calculat-list-result-grid').getSelections();
        if (selections.length == 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }
        Gikam.window.showMask(true);
        Gikam.postText('/secure/emc/module/bp/bp/orders/action/sync-tmpl-infos', Gikam.getJsonWrapper({emission: '1'}, [null, selections])).done(function () {
            Gikam.getComp('emc-report-emission-calculat-list-result-grid').refresh();

        }).always(function () {
            Gikam.window.closeMask();
        });
    },

    getRightLayout: function () {
        var _this = this;
        return {
            type: 'layout',
            id: 'emc-report-emission-calculat-list-center-layout',
            north: {
                height: '50%',
                items: [_this.getOrderGrid()]
            },
            center: {
                width: 1000,
                items: [{
                    type: 'layout',
                    center: {
                        items: [_this.getResultGrid()]
                    },
                }]
            }
        }
    },

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            renderTo: workspace.window.$dom,
            id: 'emc-report-emission-calculat-list-layout',
            west: {
                width: '30%',
                items: [_this.getFolderGridParam()]
            },
            center: {
                items: [_this.getRightLayout()]
            }
        });
    },

    init: function (param) {
        this.param = param;
        this.create();
    }
};
