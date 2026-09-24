emcSampleGasParameter.editPage = {

    getParameterTab : function() {
        let _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : '参数信息',
                items : [ _this.getGridParam() ]
            }, {
                title : '参数结果录入',
                items : [ _this.getResultParam() ]
            } ]
        }
    },

    getGridParam : function() {
        var _this = this, requestData = _this.param;
        return {
            type : 'grid',
            id : 'emc-sample-gas-parameter-list-grid',
            url : '',
            columns : emcSampleGasParameter.getGridListColumns(),
            requestData : requestData,
            onRendered : function() {
                _this.refreshParameterTitleColumns();
            },
            onBeforeUpdate : function(row, keys) {
                var dataVal = row[keys[3]];
                var resultInfo = row.row.ext$[keys[3] + '_ext$'];
                var saveData = {
                    id: resultInfo.id,
                    originalResult: dataVal
                };
                var orderTaskResultUrl = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results';
                Gikam.getLastModal().window.showMask(true);
                Gikam.put(orderTaskResultUrl + '/instant', Gikam.getJsonWrapper({}, ['emcOrderTaskResultServiceImpl', [saveData]])).done(function (id) {
                }).always(function () {
                    Gikam.put(orderTaskResultUrl + '/action/change-result', Gikam.getJsonWrapper({}, ['', [saveData]])).done(function () {
                        Gikam.post(emcSampleGasParameter.baseUrl + '/gas-parameter/table-rows/queries', Gikam.getJsonWrapper({

                        }, ['', [{ id: _this.param.sampleId, projId: _this.param.projId }]])).done(function (data) {
                            Gikam.getComp('emc-sample-gas-parameter-list-grid').loadData(data);
                        });
                    }).fail(function () {
                        Gikam.getLastModal().window.closeMask();
                    });
                    Gikam.getLastModal().window.closeMask();
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    let selections = Gikam.getComp('emc-sample-gas-parameter-list-grid').getSelections();
                    if (selections.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    Gikam.getLastModal().window.showMask(true);
                    Gikam.postText('/secure/emc/module/bp/bp/orders/action/add-param-order', Gikam.getJsonWrapper({}, [ '', selections ])).done(function() {
                        _this.refreshParameterData();
                    }).always(function() {
                        Gikam.getLastModal().window.closeMask();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    let selections = Gikam.getComp('emc-sample-gas-parameter-list-grid').getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }

                    Gikam.getLastModal().window.showMask(true);
                    Gikam.postText('/secure/emc/module/bp/bp/orders/action/delete-param-order', Gikam.getJsonWrapper({}, [ '', selections ])).done(function() {
                        _this.refreshParameterData();
                    }).always(function() {
                        Gikam.getLastModal().window.closeMask();
                    });
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.BATCH_INPUT',
                icon : 'add',
                onClick : function() {
                    _this.batchInput();
                }
            } ]
        }
    },

    refreshParameterData : function() {
        let _this = this;
        Gikam.post(emcSampleGasParameter.baseUrl + '/gas-parameter/table-rows/queries', Gikam.getJsonWrapper({

        }, ['', [{ id: _this.param.sampleId, projId: _this.param.projId }]])).done(function (data) {
            Gikam.getLastModal().window.closeMask();
            Gikam.getComp('emc-sample-gas-parameter-list-grid').loadData(data);
        });
    },

    refreshParameterTitleColumns : function() {
        let _this = this;
        let columns = emcSampleGasParameter.getGridListColumns();
        var dataArr = [];
        Gikam.getLastModal().window.showMask(true);
        Gikam.post(emcSampleGasParameter.baseUrl + '/gas-parameter/table-heads/queries', Gikam.getJsonWrapper({

        }, ['', [{ id: _this.param.sampleId, projId: _this.param.projId }]])).done(function (itemColumns) {
            itemColumns.forEach(function (e) {
                let itemColumns_ext$ = e.field + '_ext$';

                e.editor = true;
                e.width = 10 * e.title.length;
                e.styleFormatter = function (row) {
                    if (Gikam.isEmpty(row.ext$[itemColumns_ext$])
                        || (row.ext$ && row.ext$[itemColumns_ext$] && row.ext$[itemColumns_ext$]['scriptflag'] && row.ext$[itemColumns_ext$]['scriptflag'] === '1')) {

                    } else {
                        return {
                            backgroundColor: 'palegreen'
                        }
                    }
                },

                e.onBeforeEditorRender = function (row) {
                    if ((row.ext$ && row.ext$[itemColumns_ext$] && row.ext$[itemColumns_ext$]['scriptflag'] && row.ext$[itemColumns_ext$]['scriptflag'] === '1') || Gikam.isEmpty(row.ext$[itemColumns_ext$])) {
                        return false;
                    }
                    return true;
                },
                e.onFocus = function (field, value, index) {

                };

            });
            columns = columns.concat(itemColumns);
            Gikam.post(emcSampleGasParameter.baseUrl + '/gas-parameter/table-rows/queries', Gikam.getJsonWrapper({

            }, ['', [{ id: _this.param.sampleId, projId: _this.param.projId }]])).done(function (data) {
                dataArr = data;
                Gikam.getLastModal().window.closeMask();
                Gikam.getComp('emc-sample-gas-parameter-list-grid').refreshColumns(columns);
                Gikam.getComp('emc-sample-gas-parameter-list-grid').loadData(dataArr);
            });
        });
    },


    getResultParam : function() {
        var _this = this, requestData = _this.param;
        return {
            type : 'grid',
            id : 'emc-sample-gas-parameter-result-list-grid',
            url : '',
            columns : emcSampleGasParameter.getResultListColumns(),
            requestData : requestData,
            onRendered : function() {
                _this.refreshResultTitleColumns();
            },
            onBeforeUpdate : function(row, keys) {
                var dataVal = row[keys[3]];
                var resultInfo = row.row.ext$[keys[3] + '_ext$'];
                var saveData = {
                    id: resultInfo.id,
                    originalResult: dataVal
                };
                var orderTaskResultUrl = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results';
                Gikam.getLastModal().window.showMask(true);
                Gikam.put(orderTaskResultUrl + '/instant', Gikam.getJsonWrapper({}, ['emcOrderTaskResultServiceImpl', [saveData]])).done(function (id) {
                }).always(function () {
                    Gikam.put(orderTaskResultUrl + '/action/change-result', Gikam.getJsonWrapper({}, ['', [saveData]])).done(function () {
                        Gikam.post(emcSampleGasParameter.baseUrl + '/gas-parameter-result/table-rows/queries', Gikam.getJsonWrapper({

                        }, ['', [{ id: _this.param.sampleId}]])).done(function (data) {
                            Gikam.getLastModal().window.closeMask();
                            Gikam.getComp('emc-sample-gas-parameter-result-list-grid').loadData(data);
                        });
                    }).fail(function () {
                        Gikam.getLastModal().window.closeMask();
                    });
                });
            },
            toolbar : [{
                type : 'button',
                text : '按测试排序',
                icon : 'preview',
                onClick : function() {
                    _this.refreshResultTitleColumns('test');
                }
            },{
                type : 'button',
                text : '按分析项排序',
                icon : 'preview',
                onClick : function() {
                    _this.refreshResultTitleColumns();
                }
            }  ]
        }
    },

    refreshResultTitleColumns : function(sorttype) {
        let _this = this;
        let columns = emcSampleGasParameter.getResultListColumns();
        var dataArr = [];
        Gikam.getLastModal().window.showMask(true);
        Gikam.post(emcSampleGasParameter.baseUrl + '/gas-parameter-result/table-heads/queries', Gikam.getJsonWrapper({
            sorttype: sorttype,
        }, ['', [{ id: _this.param.sampleId, projId: _this.param.projId }]])).done(function (itemColumns) {
            itemColumns.forEach(function (e) {
                let itemColumns_ext$ = e.field + '_ext$';

                e.editor = true;
                e.width = 10 * e.title.length;
                e.styleFormatter = function (row) {
                    if (Gikam.isEmpty(row.ext$[itemColumns_ext$])
                        || (row.ext$ && row.ext$[itemColumns_ext$] && row.ext$[itemColumns_ext$]['scriptflag'] && row.ext$[itemColumns_ext$]['scriptflag'] === '1')) {

                    } else {
                        return {
                            backgroundColor: 'palegreen'
                        }
                    }
                },

                    e.onBeforeEditorRender = function (row) {
                        if ((row.ext$ && row.ext$[itemColumns_ext$] && row.ext$[itemColumns_ext$]['scriptflag'] && row.ext$[itemColumns_ext$]['scriptflag'] === '1') || Gikam.isEmpty(row.ext$[itemColumns_ext$])) {
                            return false;
                        }
                        return true;
                    },
                    e.onFocus = function (field, value, index) {

                    };

            });
            columns = columns.concat(itemColumns);
            Gikam.post(emcSampleGasParameter.baseUrl + '/gas-parameter-result/table-rows/queries', Gikam.getJsonWrapper({

            }, ['', [{ id: _this.param.sampleId}]])).done(function (data) {
                dataArr = data;
                Gikam.getLastModal().window.closeMask();
                Gikam.getComp('emc-sample-gas-parameter-result-list-grid').refreshColumns(columns);
                Gikam.getComp('emc-sample-gas-parameter-result-list-grid').loadData(dataArr);
            });
        });
    },

    /** 批录入 */
    batchInput : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-sample-gas-parameter-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var modal = Gikam.create('modal', {
            title: "EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.BATCH_INPUT",
            width : 400,
            height : 450,
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

                                Gikam.getLastModal().window.showMask(true);
                                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/action/batch-input-gas-parameter',
                                    Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', selections ], [ 'emcOrderTaskResultServiceImpl', [formData]])).done(function() {

                                    _this.refreshParameterData();
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

        let formFields = [];
        Gikam.getLastModal().window.showMask(true);
        Gikam.postSync(emcSampleGasParameter.baseUrl + '/gas-parameter/table-heads/queries',
            Gikam.getJsonWrapper({}, ['', [{ id: _this.param.sampleId, projId: _this.param.projId }]]))
            .done(function (itemColumns) {
                itemColumns.forEach(item => item.title = item.title.replace('<br>', '-'));
                formFields = itemColumns;
                Gikam.getLastModal().window.closeMask();
            });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            columns: 1,
            titleWidth: 120,
            fields: formFields,
            autoSave: false
        });
    },

    // 创建页面
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ _this.getParameterTab() ]
            },
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
