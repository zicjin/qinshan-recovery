emcRun.preprocessingResultInputPage = {

    getGridParam: function () {
        var _this = this, requestData = _this.param;
        return {
            type: 'grid',
            id: 'emc-run-preprocessing-result-input-list-grid',
            url: '',
            columns: emcRun.getPreprocessingResultListColumns(),
            requestData: requestData,
            onRendered: function () {
                _this.refreshTitleColumns();
            },
            onBeforeUpdate: function (row, keys) {
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
                        _this.refreshTitleColumns();
                    }).fail(function () {
                        Gikam.getLastModal().window.closeMask();
                    });
                });
            },
            toolbar: [{
                type: 'button',
                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.BATCH_INPUT',
                icon: 'add',
                onClick: function () {
                    _this.batchInput();
                }
            }]
        }
    },

    refreshTitleColumns: function () {
        let _this = this;
        let columns = emcRun.getPreprocessingResultListColumns();
        var dataArr = [];
        Gikam.getLastModal().window.showMask(true);
        Gikam.post(emcRun.resultBaseUrl + '/preprocessing-result/table-heads/queries', Gikam.getJsonWrapper({projIds: _this.param.projIds}, ['', [{}]]))
            .done(function (itemColumns) {
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
                Gikam.post(emcRun.resultBaseUrl + '/preprocessing-result/table-rows/queries', Gikam.getJsonWrapper({projIds: _this.param.projIds}, ['', [{}]]))
                    .done(function (data) {
                        dataArr = data;
                        Gikam.getLastModal().window.closeMask();
                        Gikam.getComp('emc-run-preprocessing-result-input-list-grid').refreshColumns(columns);
                        Gikam.getComp('emc-run-preprocessing-result-input-list-grid').loadData(dataArr);
                    });
            });
    },

    refreshParameterData: function () {
        let _this = this;
        Gikam.post(emcRun.resultBaseUrl + '/preprocessing-result/table-rows/queries', Gikam.getJsonWrapper({projIds: _this.param.projIds}, ['', [{}]]))
            .done(function (data) {
                Gikam.getLastModal().window.closeMask();
                Gikam.getComp('emc-run-preprocessing-result-input-list-grid').loadData(data);
            });
    },

    /** 批录入 */
    batchInput: function () {
        var _this = this;
        var selections = Gikam.getComp('emc-run-preprocessing-result-input-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var modal = Gikam.create('modal', {
            title: "EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.BATCH_INPUT",
            width: 400,
            height: 450,
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

                                Gikam.getLastModal().window.showMask(true);
                                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/action/batch-input-gas-parameter',
                                    Gikam.getJsonWrapper({}, ['emcOrderTaskServiceImpl', selections], ['emcOrderTaskResultServiceImpl', [formData]])).done(function () {

                                    _this.refreshParameterData();
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

        let formFields = [];
        Gikam.getLastModal().window.showMask(true);
        Gikam.postSync(emcRun.resultBaseUrl + '/preprocessing-result/table-heads/queries',
            Gikam.getJsonWrapper({projIds: _this.param.projIds}, ['', [{}]]))
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

    createPage: function () {
        var _this = this;
        Gikam.create('layout', {
            renderTo: Gikam.getLastModal().window.$dom,
            id: 'emc-run-preprocessing-result-input-list-layout',
            center: {
                items: [_this.getGridParam()]
            }
        });
        return this;
    },

    init: function (param) {
        this.param = param;
        this.createPage();
    }
};
