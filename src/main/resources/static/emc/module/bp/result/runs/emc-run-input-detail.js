emcRun.runInputDetailPage = {

    getRunDetailInfoPanel: function () {
        var _this = this;
        return {
            //基本信息
            id: 'emc-run-detail-base-panel',
            title: 'EMC.MODULE.BP.RESULT.RUNS.TAB.RUN_DETAIL',
            items: [{
                type: 'layout',
                north: {
                    height: '40%',
                    items: [_this.getBaseInfo()]
                },
                center: {
                    items: [{
                        type: 'tab',
                        panels: [{
                            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.TAB.EQUIPT_INFO',
                            items: [{
                                type: 'layout',
                                center: {
                                    items: [_this.getEquiptGridParam()]
                                }
                            }]
                        },/* {
                            title: 'EMC.MODULE.BP.RESULT.RUNS.TAB.SOL',
                            items: [{
                                type: 'layout',
                                center: {
                                    items: [_this.getSolGridParam()]
                                }
                            }]
                        // }, {
                        //     title: 'EMC.MODULE.BP.RESULT.RUNS.TAB.STDSOL_CONCOCT',
                        //     items: [{
                        //         type: 'layout',
                        //         center: {
                        //             items: [_this.getStdsolConcoctGridParam()]
                        //         }
                        //     }]
                        // }, {
                        //     title: 'EMC.MODULE.BP.RESULT.RUNS.TAB.SOL_CONFIG_RECORD',
                        //     items: [{
                        //         type: 'layout',
                        //         center: {
                        //             items: [_this.getSolConfigRecordGridParam()]
                        //         }
                        //     }]
                        // }, {
                        //     title: 'EMC.MODULE.BP.RESULT.RUNS.TAB.REAGENT',
                        //     items: [{
                        //         type: 'layout',
                        //         center: {
                        //             items: [_this.getReagentGridParam()]
                        //         }
                        //     }]
                        }*/]
                    }]
                }
            }]
        }
    },
    getReagentGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-run-reagent-edit-list-grid',
            service: 'emcReagentServiceImpl',

            checkOnActive: true,
            checkOneOnActive: true,
            readonly: true,
            columns: emcRun.getReagentEditListColumns(),
            page: false,
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.SELECT',
                icon: 'add',
                onClick: function () {
                    _this.selectRunReagent();
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.DELETE',
                icon: 'remove',
                onClick: function () {
                    _this.clearRunReagent();
                }
            }]
        }
    },
    selectRunReagent: function () {
        var form = Gikam.getComp('emc-run-input-detail-form');
        var methodNames = form.getData().testName;
        Gikam.create('modal', {
            title: Gikam.choose.getConfig('reagentName').title,
            url: Gikam.choose.getConfig('reagentName').url + Gikam.param({
                testMethodNames: methodNames
            }),
            onAfterClose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    form.setData({
                        reagentId: rows.reagentId,
                        ext$: {
                            reagentname: rows.reagentName
                        }
                    });
                    Gikam.getComp('emc-run-reagent-edit-list-grid').refresh({
                        url: IFM_CONTEXT + '/secure/emc/module/bp/reagent-solutions' + '/queries',
                        requestData: {
                            id_IN: rows.reagentId
                        }
                    });
                }
            }
        });
    },
    clearRunReagent: function () {
        var selections = Gikam.getComp('emc-run-reagent-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var tempData = Gikam.getComp('emc-run-reagent-edit-list-grid').getData();
        var allData = [];
        for (var i = 0; i < tempData.length; i++) {
            var flag = false;
            for (var j = 0; j < selections.length; j++) {
                if (tempData[i].id === selections[j].id) {
                    flag = true;
                }
            }
            if (!flag) {
                allData.push(tempData[i]);
            }
        }

        var reagentIdArr = [];
        var reagentNameArr = [];
        allData.forEach(function (item) {
            reagentIdArr.push(item.id);
            reagentNameArr.push(item.solutionName);
        });
        Gikam.getComp('emc-run-input-detail-form').setData({
            reagentId: Gikam.isEmpty(allData) ? "" : reagentIdArr.join(','),
            ext$: {
                reagentname: Gikam.isEmpty(allData) ? "" : reagentNameArr.join(',')
            }
        });
        Gikam.getComp('emc-run-reagent-edit-list-grid').setOptions({
            requestData: { // 当数据为空时设置为0，避免查询到所有数据
                id_IN: Gikam.isEmpty(allData) ? '0' : reagentIdArr.join(',')
            }
        });
        Gikam.getComp('emc-run-reagent-edit-list-grid').loadData(allData);
    },
    getSolConfigRecordGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-run-sol-config-record-edit-list-grid',
            service: 'emcSolConfigRecordServiceImpl',
            checkOnActive: true,

            readonly: true,
            columns: emcRun.getSolConfigRecordEditListColumns(),
            page: false,
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.SELECT',
                icon: 'add',
                onClick: function () {
                    _this.selectRunSolConfigRecord();
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.DELETE',
                icon: 'remove',
                onClick: function () {
                    _this.clearRunSolConfigRecord();
                }
            }]
        }
    },
    clearRunSolConfigRecord: function () {
        var selections = Gikam.getComp('emc-run-sol-config-record-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var tempData = Gikam.getComp('emc-run-sol-config-record-edit-list-grid').getData();
        var allData = [];
        for (var i = 0; i < tempData.length; i++) {
            var flag = false;
            for (var j = 0; j < selections.length; j++) {
                if (tempData[i].id === selections[j].id) {
                    flag = true;
                }
            }
            if (!flag) {
                allData.push(tempData[i]);
            }
        }
        Gikam.getComp('emc-run-input-detail-form').setData({
            solConfigRecordId: '',
            ext$: {
                solname: ''
            }
        });
        Gikam.getComp('emc-run-sol-config-record-edit-list-grid').setOptions({
            requestData: { // 当数据为空时设置为0，避免查询到所有数据
                id_IN: '0'
            }
        });
        Gikam.getComp('emc-run-sol-config-record-edit-list-grid').loadData(allData);
    },
    selectRunSolConfigRecord: function () {
        var form = Gikam.getComp('emc-run-input-detail-form');
        Gikam.create('modal', {
            title: Gikam.choose.getConfig('sol-config-record').title,
            url: Gikam.choose.getConfig('sol-config-record').url,

            onAfterClose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    form.setData({
                        solConfigRecordId: rows[0].id,
                        ext$: {
                            solname: rows[0].solName
                        }
                    });
                    Gikam.getComp('emc-run-sol-config-record-edit-list-grid').refresh({
                        url: IFM_CONTEXT + '/secure/emc/module/bp/sol-config-records' + '/queries/raw',
                        requestData: {
                            id_IN: rows[0].id
                        }
                    });
                }
            }
        });
    },
    getStdsolConcoctGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-run-stdsol-concoct-edit-list-grid',
            service: 'emcStdsolConcoctServiceImpl',
            dbTable: 'T_EMC_STDSOL_CONCOCT',
            checkOnActive: true,
            checkOneOnActive: true,
            readonly: true,
            columns: emcRun.getSolstdConcoctEditListColumns(),
            page: false,
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.SELECT',
                icon: 'add',
                onClick: function () {
                    _this.selectRunSolstdConcoct();
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.DELETE',
                icon: 'remove',
                onClick: function () {
                    _this.clearRunSolstdConcoct();
                }
            }]
        }
    },
    clearRunSolstdConcoct: function () {
        var selections = Gikam.getComp('emc-run-stdsol-concoct-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var tempData = Gikam.getComp('emc-run-stdsol-concoct-edit-list-grid').getData();
        var allData = [];
        for (var i = 0; i < tempData.length; i++) {
            var flag = false;
            for (var j = 0; j < selections.length; j++) {
                if (tempData[i].id === selections[j].id) {
                    flag = true;
                }
            }
            if (!flag) {
                allData.push(tempData[i]);
            }
        }
        Gikam.getComp('emc-run-input-detail-form').setData({
            stdSolConcoct: '',
        });
        Gikam.getComp('emc-run-stdsol-concoct-edit-list-grid').setOptions({
            requestData: { // 当数据为空时设置为0，避免查询到所有数据
                id_IN: '0'
            }
        });
        Gikam.getComp('emc-run-stdsol-concoct-edit-list-grid').loadData(allData);
    },
    selectRunSolstdConcoct: function () {
        var form = Gikam.getComp('emc-run-input-detail-form');
        Gikam.create('modal', {
            title: Gikam.choose.getConfig('std-sol-concoct').title,
            url: Gikam.choose.getConfig('std-sol-concoct').url,

            onAfterClose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    form.setData({
                        stdSolConcoct: rows[0].reagentName
                    });
                    Gikam.getComp('emc-run-stdsol-concoct-edit-list-grid').refresh({
                        url: IFM_CONTEXT + '/secure/emc/module/item/stdsol-concocts' + '/queries/raw',
                        requestData: {
                            id_IN: '',
                            reagentName_SEQ: rows[0].reagentName
                        }
                    });
                }
            }
        });
    },
    getSolGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-run-sol-edit-list-grid',
            service: 'emcSolServiceImpl',
            checkOnActive: true,
            checkOneOnActive: true,
            readonly: true,
            columns: emcRun.getSolEditListColumns(),
            page: false,
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.SELECT',
                icon: 'add',
                onClick: function () {
                    _this.selectRunSol();
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.DELETE',
                icon: 'remove',
                onClick: function () {
                    _this.clearRunSol();
                }
            }]
        }
    },

    clearRunSol: function () {
        var selections = Gikam.getComp('emc-run-sol-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var tempData = Gikam.getComp('emc-run-sol-edit-list-grid').getData();
        var allData = [];
        for (var i = 0; i < tempData.length; i++) {
            var flag = false;
            for (var j = 0; j < selections.length; j++) {
                if (tempData[i].id === selections[j].id) {
                    flag = true;
                }
            }
            if (!flag) {
                allData.push(tempData[i]);
            }
        }
        Gikam.getComp('emc-run-input-detail-form').setData({
            sol1Id: '',
            ext$: {
                sol1no: ''
            }
        });
        Gikam.getComp('emc-run-sol-edit-list-grid').setOptions({
            requestData: { // 当数据为空时设置为0，避免查询到所有数据
                id_IN: '0'
            }
        });
        Gikam.getComp('emc-run-sol-edit-list-grid').loadData(allData);


    },
    selectRunSol: function () {
        var form = Gikam.getComp('emc-run-input-detail-form');
        Gikam.create('modal', {
            title: Gikam.choose.getConfig('solConf').title,
            url: Gikam.choose.getConfig('solConf').url,

            onAfterClose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    form.setData({
                        sol1Id: rows.solId,
                        ext$: {
                            sol1no: rows.solNum
                        }
                    });
                    Gikam.getComp('emc-run-sol-edit-list-grid').refresh({
                        url: IFM_CONTEXT + '/secure/emc/module/bp/sols' + '/queries/raw',
                        requestData: {
                            id_IN: rows.solId
                        }
                    });
                }
            }
        });
    },

    runEditEquipt: function () {
        var form = Gikam.getComp('emc-run-input-detail-form');
        var formData = form.getData();
        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.RESULT.RUNS.MODAL.EDIT_EQUIPT',
            url: emcRun.baseUrl + '/page/edit-equipt' + Gikam.param({
                testMethodId: formData.testMethodId,
                equiptId: formData.equiptId,
            }),
            onAfterClose: function (data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getLastModal().window.showMask(true);
                    Gikam.postText(emcRun.baseUrl + '/action/edit-equipt', Gikam.getJsonWrapper(null, ['emcEquiptServiceImpl', data], ['emcRunServiceImpl', [formData]])).done(function () {
                        form.refresh();
                    }).always(function () {
                        Gikam.getLastModal().window.closeMask();
                    });
                }
            }
        });
    },

    getEquiptGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-run-input-detail-equipt-grid',
            page: false,
            checkOneOnActive: true,
            columns: emcRun.getEquiptChooseListColumns(),
            toolbar: [{
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.EQUIPT_EDIT',
                icon: 'select',
                onClick: function () {
                    _this.runEditEquipt();
                }
            }]
        }
    },

    getBaseInfo: function () {
        var _this = this;
        return {
            type: 'form',
            id: 'emc-run-input-detail-form',
            url : Gikam.printf(emcRun.baseUrl + '/{id}', {
                id: _this.param.runId
            }),
            service: 'emcRunServiceImpl',
            fields: emcRun.getTestRunFields(),
            dbTable : 'T_EMC_RUN',
            columns: 3,
            onRendered: function () {
                if (_this.param.runId) {
                    this.toFieldsReadonly(['id', 'orderCategory', 'testName', 'ext$.equiptcode']);
                }
            },
            onLoadSuccess: function (data) {
                Gikam.getAsyncComp('emc-run-input-detail-equipt-grid').done(function (grid) {
                    grid.refresh({
                        url : '/secure/emc/module/mdm/equipt/equipts/queries',
                        requestData: {
                            id_EQ: data.equiptId
                        }
                    });
                });
                if (!Gikam.isEmpty(data.sol1Id)) {
                    Gikam.getAsyncComp('emc-run-sol-edit-list-grid').done(function (grid) {
                        grid.refresh({
                            url: IFM_CONTEXT + '/secure/emc/module/bp/sols/queries/raw',
                            requestData: {
                                id_IN: data.sol1Id
                            }
                        });
                    });
                }
                // if (!Gikam.isEmpty(data.stdSolConcoct)) {
                //     Gikam.getAsyncComp('emc-run-stdsol-concoct-edit-list-grid').done(function (grid) {
                //         grid.refresh({
                //             url: IFM_CONTEXT + '/secure/emc/module/item/stdsol-concocts/queries/raw',
                //             requestData: {
                //                 reagentName_SEQ: data.stdSolConcoct
                //             }
                //         });
                //     });
                // }
                // if (!Gikam.isEmpty(data.solConfigRecordId)) {
                //     Gikam.getAsyncComp('emc-run-sol-config-record-edit-list-grid').done(function (grid) {
                //         grid.refresh({
                //             url: IFM_CONTEXT + '/secure/emc/module/bp/sol-config-records/queries',
                //             requestData: {
                //                 id_IN: data.solConfigRecordId
                //             }
                //         });
                //     });
                // }
                // if (!Gikam.isEmpty(data.reagentId)) {
                //     Gikam.getAsyncComp('emc-run-reagent-edit-list-grid').done(function (grid) {
                //         grid.refresh({
                //             url: IFM_CONTEXT + '/secure/emc/module/bp/reagent-solutions/queries',
                //             requestData: {
                //                 id_IN: data.reagentId
                //             }
                //         });
                //     });
                // }
            },
        }
    },

    /** 顶部返回按钮 */
    getBackButton: function () {
        var _this = this;
        // var _hidden = _this.param.pageFlag === 'modify' ? true : false;
        // if (_this.param.readOnly != 'true') {
        var _items = [
            {
                type: 'button',
                text: 'GIKAM.BUTTON.BACK',
                icon: 'back',
                id: 'emc-run-detail-reagent-edit-list-back-button',
                onClick: function () {
                    Gikam.getLastModal().close()
                }
            }, {
                type: 'button',
                text: 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon: 'submit',
                hidden: _this.param.runEdit == '1',
                onClick: function () {
                    _this.submit();
                }
            }
        ];
        return {
            type: 'btnToolbar',
            items: _items
        }
    },

    runBatchInput: function () {
        var form = Gikam.getComp('emc-run-input-detail-form');
        Gikam.create('modal', {
            width: 600,
            height: 350,
            title: 'EMC.MODULE.BP.RESULT.RUNS.MODAL.ANALYSIS_DATE',
            url: emcRun.baseUrl + '/page/batch-input',
            onAfterClose: function (data) {
                if (!Gikam.isEmpty(data)) {
                    form.setData({
                        analysisBeginDate: data.analysisBeginDate,
                        analysisBeginTime: data.analysisBeginTime,
                        analysisEndDate: data.analysisEndDate,
                        analysisEndTime: data.analysisEndTime,
                        temperature: data.temperature,
                        humidity: data.humidity
                    });
                }
            }
        });
    },

    runInitializec: function () {
        var _this = this;
        Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.INITIALIZ', 'EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.INITIALIZ', function () {
            var row = Gikam.getComp('emc-run-detail-record-edit-list').getSelections();
            if (Gikam.isEmpty(row)) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                return;
            }
            var resultData = [];
            var orderTaskData = [];
            _this.getRecordResultSelectResultInfo(row, resultData, orderTaskData);

            var url = emcRun.baseUrl + '/action/init';
            Gikam.getLastModal().window.showMask(true);
            Gikam.put(url, Gikam.getJsonWrapper({
                pageType: 'runRecord'
            }, ['', orderTaskData])).done(function () {
                _this.refreshRecordResultData();
            });
        });
    },

    getRunDetailNext: function () {
        var _this = this;
        return {
            type: 'steps',
            id: 'emc-run-detail-next',
            // 展示的节点集合，节点格式为
            items: [{text: '记录首页', value: 'run', status: "next"},
                // {text: '方法参数', value: 'methodparam', status: "wait"},
                {text: '添加质控', value: 'qc', status: "wait"},
                {text: '选择曲线', value: 'curve', status: "wait"},
                {text: '记录数据', value: 'record', status: "wait"},
                {text: '原始数据', value: 'origindata', status: "wait"},
                {text: '质控结果', value: 'qcdata', status: "wait"},
                {text: '数据检查', value: 'datacheck', status: "wait"},
                {text: '浏览记录', value: 'browserecord', status: "wait"},
                {text: '附件', value: 'attach', status: "wait"}],
            onItemClick: function (item) {
                _this.onItemClick(item);
            }
        }
    },

    onItemClick: function (item) {
        var obj = emcRun.getStepsPanelObj();
        var tab = Gikam.getComp('emc-run-detail-tab');
        var next = Gikam.getComp('emc-run-detail-next');
        var items = next.options.items;
        for (var i = 0; i < items.length; i++) {
            if (item.value === items[i].value) {
                next.options.items[i].status = 'next';
                for (var j = 0; j < i; j++) {
                    next.options.items[j].status = 'finish';
                }
                for (var k = i + 1; k < items.length; k++) {
                    next.options.items[k].status = 'wait';
                }
                break;
            }
        }
        for (key in obj) {
            if (item.value == key) {
                obj[key].forEach(function (panel) {
                    tab.revertPanel(panel);
                });
                tab.showPanel(obj[key][0]);
            } else {
                obj[key].forEach(function (panel) {
                    tab.removePanel(panel);
                });
            }
        }
    },

    getRunMethodParamPanel: function () {
        var _this = this;
        //方法参数
        return {
            id: 'emc-run-methodparam-base-panel',
            title: 'T_EMC_TEST_METHOD.METHODPARAM',
            show: false,
            items: [{
                type: 'layout',
                north: {
                    height: 35,
                    items: [{
                        type: 'layout',
                        north: {
                            height: 10,
                            items: []
                        },
                        center: {
                            items: [_this.getMethodParamBtnToolbar()]
                        }
                    }]
                },
                center: {
                    items: [_this.getMethodParamBaseInfoForm()]
                }
            }]
        }
    },

    getMethodParamBtnToolbar: function () {
        var _this = this;
        return {
            type: 'button',
            text: 'GIKAM.BUTTON.SAVE',
            icon: 'select',
            onClick: function () {
                var run = Gikam.getComp('emc-run-input-detail-form').getData();
                var comp = Gikam.getComp('emc-method-param-edit-test-method-list-form');
                if (comp.validate()) {
                    var formData = comp.getData();

                    //没填写值的，默认是""
                    var fields = comp.getOptions().fields;
                    fields.map(function (field) {
                        var name = field.field;
                        if (!formData.hasOwnProperty(name)) {
                            formData[name] = "";
                        }
                    });
                    formData.methodParamType = 'methodParam';

                    Gikam.getLastModal().window.showMask(true);
                    Gikam.put('/secure/emc/module/mdm/basemdm/property-tmpls/result-runs/property-datas/action/save/' + run.id, Gikam.getJsonWrapper({}, ['', [formData]])).done(function (id) {


                    }).always(function () {
                        Gikam.getLastModal().window.closeMask();
                    });
                }
            }
        }
    },

    getMethodParamBaseInfoForm: function () {
        var _this = this;
        return {
            type: 'form',
            titleWidthAuto: true,
            id: 'emc-method-param-edit-test-method-list-form',
            fields: [],
            columns: 4,
            autoSave: false
        }
    },

    loadRunParamPropertyField: function () {
        var run = Gikam.getComp('emc-run-input-detail-form').getData();
        console.info(run)
        if (run.reportConfigLineId) {
            Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/result-runs/property-fields/queries/' + run.id).done(function (filed) {
                var fileds = filed.rows.map(function (item) {
                    var baseFileds = {
                        field: item.internalField,
                        title: item.fieldName,
                    };
                    var getFiledsFromType;// 塞入type相关参数
                    if (item.fieldType === 'fieldMultiSelect') {
                        getFiledsFromType = {
                            type: 'select',
                            multiple: true,
                            category: 'sys',
                            param: {
                                codeCategoryId: item.sysCode
                            }
                        }
                    } else if (item.fieldType === 'fieldSelect') {
                        getFiledsFromType = {
                            type: 'select',
                            search: true,
                            category: 'sys',
                            param: {
                                codeCategoryId: item.sysCode
                            }
                        }
                    } else if (item.fieldType === 'fieldDate') {
                        getFiledsFromType = {
                            type: 'date'
                        }

                    } else if (item.fieldType == 'fieldContext') {
                        if (item.defaultValue) {
                            getFiledsFromType = {
                                value: item.defaultValue
                            }
                        }
                    }
                    var validators = [];// 塞入验证相关参数
                    if (item.required == '1') {
                        validators.push('notEmpty');
                    }
                    if (item.fieldLength) {
                        validators.push('strLength[0,' + item.fieldLength + ']');
                    }
                    return Gikam.deepExtend({}, baseFileds, getFiledsFromType, validators)
                });

                var form = Gikam.getComp('emc-method-param-edit-test-method-list-form');
                form.options.fields = fileds;
                form.registerValidators();
                form.refreshFields([{
                    fields: fileds
                }]);
                Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/result-runs/property-datas/' + run.id ).done(function (data) {
                    form.cleanData();
                    form.setData(data, false);
                });
            });
        }
    },

    getRunQcPanel: function () {
        var _this = this;
        return {
            // 质控样
            id: 'emc-run-detail-qc-panel',
            title: 'EMC.MODULE.BP.RESULT.RUNS.TAB.QC',
            show: false,
            items: [{
                type: 'layout',
                center: {
                    items: [_this.getQcOrderGrid()]
                }
            }]
        }
    },

    getQcOrderGrid: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-run-detail-sample-test-grid',
            service: 'emcOrderTaskServiceImpl',
            checkOneOnActive: true,
            url: IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
            requestData: {
                runId_SEQ: _this.param.runId,
                status_SEQ: 'logged'
            },
            order: {
                cupNo: 'asc'
            },
            page: false,
            columns: emcRun.getSampleTestColumns(),
            deleteFormatter: function (row) {
                return row.cupNo || row.id;
            },
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                }
            },
            onBeforeUpdate: function (data) {
                // 对杯号修改进行特殊处理
                if (!Gikam.isEmpty(data.cupNo)) {
                    var runId = data.row.runId;
                    var cupNo = data.cupNo;
                    var url = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/update-cupno';
                    Gikam.getLastModal().window.showMask(true);
                    Gikam.put(url, Gikam.getJsonWrapper(null, ['emcOrderTaskServiceImpl', [data.row]])).done(function () {
                        Gikam.getComp('emc-run-detail-sample-test-grid').refresh();
                    }).always(function () {
                        Gikam.getLastModal().window.closeMask();
                    });
                    return false;
                }

                // 对仪器采集编号修改进行特殊处理
                if (!Gikam.isEmpty(data.equiptAcquNo)) {
                    var runId = data.row.runId;
                    var equiptAcquNo = data.equiptAcquNo;
                    var url = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/update-equiptacquno';
                    Gikam.getLastModal().window.showMask(true);
                    Gikam.put(url, Gikam.getJsonWrapper(null, ['emcOrderTaskServiceImpl', [data.row]])).done(function () {
                        Gikam.getComp('emc-run-detail-sample-test-grid').refresh();
                    }).always(function () {
                        Gikam.getLastModal().window.closeMask();
                    });
                    return false;
                }
                return true;
            },
            toolbar: _this.getSampleTestButton(),
        }
    },

    getSampleTestButton: function () {
        var _this = this;
        return [{
            type: 'dropDownMenu',
            text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.QC',
            items: [{
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.ADD_QC',
                icon: 'add',
                onClick: function () {
                    _this.addQc();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.DEL_QC',
                icon: 'remove',
                onClick: function () {
                    _this.delQc();
                }
            }]
        }, {
            type: 'dropDownMenu',
            text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.ORDER',
            items: [{
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.ADD_SAMPLE',
                icon: 'add',
                onClick: function () {
                    _this.addSample();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.REMOVE_SAMPLE',
                icon: 'remove',
                onClick: function () {
                    _this.removeSample();
                }
            }]
        }, {
            type: 'form',
            autoSave: false,
            fields: emcRun.getRunDetailUnCompleteField()
        }]
    },

    removeSample: function () {
        var rows = Gikam.getComp('emc-run-detail-sample-test-grid').getSelections();
        var runId = Gikam.getComp('emc-run-input-detail-form').getData().id;
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        // 只能移除正常样和现场质控
        var removeFlag = false;
        rows.map(function (e) {
            if (e.qcCategoryCode != 'N/A' && e.ext$.usecategory != 'scene') {
                removeFlag = true;
            }
        });
        if (removeFlag) {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CAN_NOT_REMOVE_QC');
            return;
        }

        Gikam.getComp('emc-run-detail-sample-test-grid').processRows({
            url: emcRun.baseUrl + '/order-tasks',
            confirm: {
                msg: 'EMC.MODULE.BP.RESULT.RUNS.TIP.DEL_SAMPLE',
                title: 'EMC.MODULE.BP.RESULT.RUNS.MODAL.DEL_SAMPLE',
            },
            formatter: function (row) {
                return row.ext$.orderno + row.orderCategory || row.id;
            },
            method: 'del'
        }).done(function () {
            Gikam.getComp('emc-run-detail-sample-test-grid').refresh();
        });
    },

    addSample: function () {
        var runForm = Gikam.getComp('emc-run-input-detail-form');
        var runFormData = runForm.getData();
        var runId = runFormData.id;
        var testId = runFormData.testId;
        var methodId = runFormData.methodId;
        var orderCategory = runFormData.orderCategory;
        if (Gikam.isEmpty(runId)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.RESULT.RUNS.MODAL.SELECT_SAMPLE',
            url: Gikam.choose.getConfig('orderTask').url + Gikam.param({
                runId: 'empty',
                status_SEQ: 'logged',
                analysisType_SEQ: 'analysis',
                authority_org: '1',
                //testId_IN : testId,
                methodId_SEQ: methodId,
                //orderCategory_IN : orderCategory
            }),
            onAfterClose: function (rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var url = Gikam.printf(emcRun.baseUrl + '/{runId}/order-tasks', {
                    runId: runId
                });
                Gikam.getLastModal().window.showMask(true);
                Gikam.post(url, Gikam.getJsonWrapper(null, ['', rows])).done(function () {
                    Gikam.getComp('emc-run-detail-sample-test-grid').refresh();
                }).always(function () {
                    Gikam.getLastModal().window.closeMask();
                });
            }
        });
    },

    addQc: function () {
        var _this = this;
        var grid = Gikam.getComp('emc-run-detail-sample-test-grid');
        var data = grid.getSelections();
        if (Gikam.isEmpty(data)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var testMethodIds = "";
        for (var i = 0; i < data.length; i++) {
            testMethodIds += data[i].testMethodId;
            if (i != data.length - 1) {
                testMethodIds += ",";
            }
        }
        Gikam.create('modal', {
            width: 500,
            title: 'EMC.MODULE.BP.RESULT.RUNS.MODAL.ADD_QC',
            url: '/secure/emc/module/bp/bp/order-tasks/page/run-qc-category' + Gikam.param({
                USECATEGORY_SEQ: 'lab',
                runId: _this.param.runId,
                analysisDate: _this.param.analysisDate,
                testMethodIds: testMethodIds
            }),
            onAfterClose: function (rows) {
                if (Gikam.isEmpty(rows[0]) || Gikam.isEmpty(rows)) {
                    return;
                }
                var url = emcRun.baseUrl + '/qc-order-tasks';
                Gikam.getLastModal().window.showMask(true);
                Gikam.post(url, Gikam.getJsonWrapper({
                    f: {
                        batchNo: rows[1]
                    }
                }, ['emcOrderTaskServiceImpl', data], ['emcQcCategoryServiceImpl', rows[0]])).done(function () {
                    grid.refresh().done(function () {
                        Gikam.getLastModal().window.closeMask();
                    });
                });
            }
        });
    },

    delQc: function () {
        var data = Gikam.getComp('emc-run-detail-sample-test-grid').getSelections();
        var qcFlag = false;
        //只能删除实验室质控
        data.map(function (e) {
            if (e.ext$.usecategory && e.ext$.usecategory == 'lab') {
                qcFlag = true;
            }
        });
        if (!qcFlag) {
            Gikam.alert('不能删除非实验室质控样');
            return;
        }
        Gikam.getComp('emc-run-detail-sample-test-grid').processRows({
            url: emcRun.baseUrl + '/qc-order-tasks',
            confirm: {
                msg: 'EMC.MODULE.BP.RESULT.RUNS.TIP.DEL_QC',
                title: 'EMC.MODULE.BP.RESULT.RUNS.MODAL.DEL_QC',
            },
            formatter: function (row) {
                return row.ext$.orderno + row.orderCategory || row.id;
            },
            method: 'del'
        }).done(function () {
            Gikam.getComp('emc-run-detail-sample-test-grid').refresh();
        });
    },

    getCurveGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-run-detail-curve-list-grid',
            url: '',
            columns: emcRun.getCurveListColumns(),
            service: 'emcCurveServiceImpl',
            onCellClick: function (field, row) {
                field === 'curveNo' && _this.editCurvePage(row);
            },
            checkOneOnActive: true,
            deleteFormatter: function (row) {
                return row.curveNo || row.id;
            },
            toolbar: [{
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.CURVES.BUTTON.SELECT',
                icon: 'select',
                onClick: function () {
                    _this.selectCurve();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.CURVES.BUTTON.CLEAN',
                icon: 'remove',
                onClick: function () {
                    _this.clearCurve();
                }
            }]
        }
    },

    clearCurve: function () {
        var selections = Gikam.getComp('emc-run-detail-curve-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var tempData = Gikam.getComp('emc-run-detail-curve-list-grid').getData();
        var allData = [];
        for (var i = 0; i < tempData.length; i++) {
            var flag = false;
            for (var j = 0; j < selections.length; j++) {
                if (tempData[i].id === selections[j].id) {
                    flag = true;
                }
            }
            if (!flag) {
                allData.push(tempData[i]);
            }
        }
        var curveIdArr = [];
        var curveNoArr = [];
        allData.forEach(function (item) {
            curveIdArr.push(item.id);
            curveNoArr.push(item.curveNo);
        });
        Gikam.getComp('emc-run-input-detail-form').setData({
            curveId: Gikam.isEmpty(allData) ? "" : curveIdArr.join(','),
            curveNo: Gikam.isEmpty(allData) ? "" : curveNoArr.join(',')
        });
        Gikam.getComp('emc-run-detail-curve-list-grid').setOptions({
            requestData: { // 当数据为空时设置为0，避免查询到所有数据
                id_IN: Gikam.isEmpty(allData) ? "0" : curveIdArr.join(',')
            }
        });
        Gikam.getComp('emc-run-detail-curve-list-grid').loadData(allData);
    },

    selectCurve: function () {
        var form = Gikam.getComp('emc-run-input-detail-form');
        var formData = form.getData();
        Gikam.create('modal', {
            title: Gikam.choose.getConfig('curve').title,
            url: Gikam.choose.getConfig('curve').url + Gikam.param({
                equiptId: formData.equiptId,
                testId: formData.testId,
                methodId: formData.methodId,
                analysisBeginDate: formData.analysisBeginDate,
                testMethodId :  formData.testMethodId,
            }),

            onAfterClose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    var curveIds = [];
                    var curveNos = [];
                    rows.forEach(function (item) {
                        curveIds.push(item.id);
                        curveNos.push(item.curveNo);

                    });
                    form.setData({
                        curveId: curveIds.join(','),
                        curveNo: curveNos.join(',')
                    });
                    Gikam.getComp('emc-run-detail-curve-list-grid').refresh({
                        url: IFM_CONTEXT + '/secure/emc/module/bp/result/curves' + '/queries/raw',
                        requestData: {
                            id_IN: curveIds.join(',')
                        }
                    });
                }
            }
        });
    },

    getCurveInfoPanel: function () {
        var _this = this;
        return {
            //曲线
            id: 'emc-run-detail-curve-panel',
            title: 'T_EMC_RUN.CALIBRATIONCURVE',
            show: false,
            items: [{
                type: 'layout',
                center: {
                    items: [_this.getCurveGridParam()]
                }
            }]
        }
    },

    getEquiptCurveGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-run-detail-equipt-curve-list-grid',
            checkOneOnActive: true,
            columns: emcRun.getEquiptCurveListColumns(),
            service: 'emcCurveServiceImpl',
            onCellClick: function (field, row) {
                field === 'equiptCurveNo' && _this.editEquiptCurvePage(row);
            },
            deleteFormatter: function (row) {
                return row.curveNo || row.id;
            },
            toolbar: [{
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.CURVES.BUTTON.SELECT',
                icon: 'select',
                onClick: function () {
                    _this.selectEquiptCurve();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.CURVES.BUTTON.CLEAN',
                icon: 'remove',
                onClick: function () {
                    _this.clearEquiptCurve();
                }
            }]
        }
    },

    editEquiptCurvePage: function (row) {
        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.RESULT.CURVES.MODAL.RESULT_DATA_INPUT',
            width: '80%',
            height: '80%',
            url: IFM_CONTEXT + '/secure/emc/module/bp/result/equipt-curves' + '/page/data-result-input' + Gikam.param({
                equiptCurveId: row.id,
                createdUserId: row.createdById,
            }),
            onAfterClose: function () {
                Gikam.getComp('emc-run-detail-equipt-curve-list-grid').refresh();
            }
        });
    },

    selectEquiptCurve: function () {
        var form = Gikam.getComp('emc-run-input-detail-form');
        var formData = form.getData();
        Gikam.create('modal', {
            title: Gikam.choose.getConfig('equiptCurve').title,
            url: Gikam.choose.getConfig('equiptCurve').url + Gikam.param({
                equiptId: formData.equiptId,
                testMethodNames: formData.testName
            }),

            onAfterClose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    var curveIds = [];
                    var curveNos = [];
                    rows.forEach(function (item) {
                        curveIds.push(item.id);
                        curveNos.push(item.equiptCurveNo);

                    });
                    form.setData({
                        equiptCurveId: curveIds.join(','),
                        equiptCurveNo: curveNos.join(',')
                    });

                    Gikam.getComp('emc-run-detail-equipt-curve-list-grid').refresh({
                        url: IFM_CONTEXT + '/secure/emc/module/bp/result/equipt-curves' + '/queries/raw',
                        requestData: {
                            id_IN: curveIds.join(',')
                        }
                    });
                }
            }
        });
    },

    clearEquiptCurve: function () {
        var selections = Gikam.getComp('emc-run-detail-equipt-curve-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var tempData = Gikam.getComp('emc-run-detail-equipt-curve-list-grid').getData();
        var allData = [];
        for (var i = 0; i < tempData.length; i++) {
            var flag = false;
            for (var j = 0; j < selections.length; j++) {
                if (tempData[i].id === selections[j].id) {
                    flag = true;
                }
            }
            if (!flag) {
                allData.push(tempData[i]);
            }
        }
        var curveIdArr = [];
        var curveNoArr = [];
        allData.forEach(function (item) {
            curveIdArr.push(item.id);
            curveNoArr.push(item.curveNo);
        });
        Gikam.getComp('emc-run-input-detail-form').setData({
            equiptCurveId: Gikam.isEmpty(allData) ? "" : curveIdArr.join(','),
            equiptCurveNo: Gikam.isEmpty(allData) ? "" : curveNoArr.join(',')
        });
        Gikam.getComp('emc-run-detail-equipt-curve-list-grid').setOptions({
            requestData: { // 当数据为空时设置为0，避免查询到所有数据
                id_IN: Gikam.isEmpty(allData) ? "0" : curveIdArr.join(',')
            }
        });
        Gikam.getComp('emc-run-detail-equipt-curve-list-grid').loadData(allData);
    },

    getEquiptConditionGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-run-detail-equipt-condition-list-grid',
            url: emcRun.baseUrl + '/queries',
            requestData : {
                id_EQ: _this.param.runId
            },
            columns: emcRun.getEquiptConditionListColumns(),
            service: 'emcRunServiceImpl'
        }
    },

    editCurvePage: function (row) {
        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.RESULT.CURVES.MODAL.RESULT_DATA_INPUT',
            width: '75%',
            height: '75%',
            url: IFM_CONTEXT + '/secure/emc/module/bp/result/curves' + '/page/data-result-input' + Gikam.param({
                curveId: row.id,
                folderId: row.folderId,
                status: row.status
            }),
            onAfterClose: function () {
                Gikam.getComp('emc-run-detail-curve-list-grid').refresh();
            }
        });
    },

    getEquiptCurveInfoPanel: function () {
        var _this = this;
        return {
            id: 'emc-run-detail-equipt-curve-panel',
            title: 'T_EMC_RUN_SELECTEQUIPTCURVE',
            show: false,
            items: [{
                type: 'layout',
                center: {
                    items: [_this.getEquiptCurveGridParam()]
                },
            }]
        };
    },

    getEquiptConditionPanel: function () {
        var _this = this;
        return {
            id: 'emc-run-detail-equipt-condition-panel',
            title: '仪器条件',
            show: false,
            items: [{
                type: 'layout',
                center: {
                    items: [_this.getEquiptConditionGridParam()]
                },
            }]
        };
    },

    getAnalysisPanel: function () {
        var _this = this;
        return {
            //分析
            id: 'emc-run-detail-record-panel',
            title: 'EMC.MODULE.BP.RESULT.RUNS.TAB.ANALYSIS_INPUT',
            show: false,
            items: [{
                type: 'grid',
                id: 'emc-run-detail-record-edit-list',
                columns: [],
                checkOnActive : false,
                toolbar: _this.getRecordResultButton(),
                onRendered : function () {
                    _this.refreshRecordColumnAndResultData();
                },
                onBeforeUpdate: function (data, keys) {
                    if (keys.indexOf('equiptAcquNo') !== -1) {
                        let url = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/update-equiptacquno';
                        data.row.runId = _this.param.runId;
                        Gikam.getLastModal().window.showMask(true);
                        Gikam.put(url, Gikam.getJsonWrapper(null, [ 'emcOrderTaskServiceImpl', [ data.row ] ])).always(function() {
                            Gikam.getLastModal().window.closeMask();
                        });
                        return false;
                    }

                    if (keys.indexOf('id') !== -1) {
                        var dataVal = data[keys[3]];
                        var resultInfo = data.row.ext$[keys[3] + '_ext$'];
                        var saveData = {
                            id: resultInfo.id,
                            originalResult: dataVal
                        };
                    }
                    if (keys.indexOf('index') !== -1) {
                        var dataVal = data[keys[1]];
                        var resultInfo = this.getData()[data.index].ext$[keys[1] + '_ext$'];
                        var saveData = {
                            id: resultInfo.id,
                            originalResult: dataVal
                        };
                    }

                    Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, ['emcOrderTaskResultServiceImpl', [saveData]])).done(function (id) {
                        // 判断是否要进行实时公式计算
                        let realTimeCalcFormData = Gikam.getComp('emc-run-input-detail-real-time-calc-form').getData();
                        if (realTimeCalcFormData.realTimeCalc !== '1') {
                            Gikam.getLastModal().window.closeMask();
                            return;
                        }

                        Gikam.getLastModal().window.showMask(true);
                        let orderTaskResultUrl = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results';
                        Gikam.put(orderTaskResultUrl + '/action/change-result', Gikam.getJsonWrapper({}, ['', [saveData]])).done(function () {

                            Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results' + '/quick-inputs/table-rows/queries', Gikam.getJsonWrapper({}, ['', [{id: _this.param.runId}]])).done(function (data) {
                                var grid = Gikam.getComp('emc-run-detail-record-edit-list');
                                Gikam.each(data, function (index, row) {
                                    grid.setData(row, false, true);
                                });
                            });

                        }).always(function () {
                            Gikam.getLastModal().window.closeMask();
                        });
                    });
                }
            }]
        }
    },

    refreshRecordColumnAndResultData: function () {
        var _this = this;
        var columns = [{
            checkbox: true,
            fixed : true
        }, {
            field: 'cupNo',
            title: 'T_EMC_ORDER_TASK.CUPNO',
            width: 80,
            fixed : true
        }, {
            field: 'orderNo',
            title: 'T_EMC_ORDER_TASK.LABELNUMBER',
            width: 160,
            fixed : true
        }, {
            field: 'equiptAcquNo',
            title: 'T_EMC_ORDER_TASK.EQUIPTACQUNO',
            width: 160,
            editor: true,
            fixed : true
        }, {
            field: 'labDisplayQcName',
            title: 'T_EMC_ORDER.QCNAME',
            fixed : true
        }];
        var dataArr = [];
        Gikam.getLastModal().window.showMask(true);
        Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results' + '/quick-inputs/table-heads/queries', Gikam.getJsonWrapper({}, ['', [{id: _this.param.runId}]])).done(function (itemColumns) {
            itemColumns.forEach(function (e) {
                var itemColumns_ext$ = e.field + '_ext$';
                e.editor = Gikam.isEmpty(_this.param.readonly);
                e.textStyleFormatter = function (row) {
                    if (row && row.ext$ && row.ext$[itemColumns_ext$] && row.ext$[itemColumns_ext$]['scriptflag'] == '0') {
                        return {
                            backgroundColor: 'palegreen'
                        }
                    }
                },
                    e.onBeforeEditorRender = function (row) {
                        if (row && row.ext$ && row.ext$[itemColumns_ext$] && row.ext$[itemColumns_ext$]['scriptflag'] == '0') {
                            return true;
                        } else {
                            return false;
                        }
                    },
                    e.onFocus = function (field, value, index) {
                        var _this = this;
                        var data = _this.getData()[index];
                        if ('select' === data.ext$[field + '_ext$']['resultcategory']) {
                            Gikam.create('modal', {
                                width: 400,
                                height: 300,
                                title: Gikam.choose.getConfig('itemResult').title,
                                url: Gikam.choose.getConfig('itemResult').url + Gikam.param({
                                    single: true,
                                    itemId_EQ: field.split('$')[1],
                                }),
                                onAfterClose: function (rows) {
                                    if (Gikam.isEmpty(rows)) {
                                        return;
                                    }
                                    var data = {
                                        index : index
                                    };
                                    data[field] = rows[0].result;
                                    Gikam.getComp('emc-run-detail-record-edit-list').setData(data);
                                }
                            });
                        }
                    };
            });
            columns = columns.concat(itemColumns);
            Gikam.getComp('emc-run-detail-record-edit-list').refreshColumns(columns);
            _this.refreshRecordResultData();
        });
    },

    refreshRecordResultData: function () {
        var _this = this;
        Gikam.getLastModal().window.showMask(true);
        Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results' + '/quick-inputs/table-rows/queries', Gikam.getJsonWrapper({}, ['', [{id: _this.param.runId}]])).done(function (data) {
            Gikam.getComp('emc-run-detail-record-edit-list').loadData(data);
        }).always(function () {
            Gikam.getLastModal().window.closeMask();
        });
    },

    syncTmplInfo: function () {
        var _this = this;
        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.SYNC_TMPL_INFO', 'EMC.MODULE.BP.RESULT.RUNS.TIP.SYNC_TMPL_INFO', function () {
            var selections = Gikam.getComp('emc-run-detail-record-edit-list').getSelections();
            if (selections.length == 0) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                return;
            }
            var resultData = [];
            var orderTaskData = [];
            _this.getRecordResultSelectResultInfo(selections, resultData, orderTaskData);
            Gikam.getLastModal().window.showMask(true);
            Gikam.postText(emcRun.baseUrl + '/action/sync-tmpl-infos', Gikam.getJsonWrapper(null, ['emcOrderTaskServiceImpl', orderTaskData], ['emcOrderTaskResultServiceImpl', resultData])).done(function () {
                _this.refreshRecordResultData();
            }).always(function () {
                Gikam.getLastModal().window.closeMask();
            });
        });
    },

    getRecordResultSelectResultInfo: function (selections, resultData, orderTaskData) {
        var _this = this;
        selections.forEach(function (ot) {
            orderTaskData.push({
                id: ot.id,
                orderId: ot.orderId,
                runId: _this.param.runId,
                ext$: {}
            });
            var resultObj = ot.ext$;
            for (key in resultObj) {
                var resultInfo = resultObj[key];
                var parKey = key.split('_')[0];
                var originalResult = ot[parKey];
                var repeatNo = parKey.split('$')[2];
                if (!Gikam.isEmpty(resultInfo.id)) {
                    var itemId = parseInt(key.split('$')[1]);
                    var orderTaskId = ot.id;
                    resultData.push({
                        id: resultInfo.id,
                        itemId: itemId,
                        originalResult: originalResult,
                        repeatNo: repeatNo,
                        orderTaskId: orderTaskId,
                        ext$: {}
                    });
                }
            }
        });
    },

    runInput: function () {
        var _this = this;
        var data = Gikam.getComp('emc-run-detail-record-edit-list').getSelections();
        if (Gikam.isEmpty(data)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var resultData = [];
        var orderTaskData = [];
        _this.getRecordResultSelectResultInfo(data, resultData, orderTaskData);
        var itemId = resultData.map(function (e) {
            return e.itemId;
        });
        var grid = Gikam.getComp('emc-run-sample-test-grid');
        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.RESULT.RUNS.MODAL.SELECT_SAMPLE',
            url: Gikam.choose.getConfig('orderTask').url + Gikam.param({
                runId_SEQ: _this.param.runId,
                status_SEQ: 'logged',
                target_unassigned: '1',
                targetTable: 'T_EMC_ORDER_TASK_RESULT',
                targetTableIdColumn: 'ORDERTASKID',
                targetIdColumn: 'ID',
                targetFilterColumn: 'ITEMID',
                targetFilterValue: itemId
            }),
            onAfterClose: function (afterData) {
                if (Gikam.isEmpty(afterData)) {
                    return;
                }
                Gikam.getLastModal().window.showMask(true);
                Gikam.post(emcRun.baseUrl + '/action/run-input', Gikam.getJsonWrapper(null, ['emcOrderTaskResultServiceImpl', resultData], ['emcOrderTaskServiceImpl', afterData])).done(function () {
                    _this.refreshRecordResultData();
                });
            }
        });
    },

    invalidData: function () {
        var _this = this;
        Gikam.create('modal', {
            width: '20%',
            height: '30%',
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.INVALID_DATA',
            url: '/secure/emc/module/bp/sample/samples/page/invalid-results' + Gikam.param({
                pageType: 'runRecord'
            }),
            onAfterClose: function (data) {
                if (!Gikam.isEmpty(data)) {

                    var resultSelections = Gikam.getComp('emc-run-detail-record-edit-list').getSelections();
                    if (Gikam.isEmpty(resultSelections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    var resultData = [];
                    var orderTaskData = [];
                    _this.getRecordResultSelectResultInfo(resultSelections, resultData, orderTaskData);

                    resultData.forEach(function (item) {
                        item.ext$.reason = data.reason;
                    });

                    // 在代码请求前打开遮罩，避免遮罩没有关闭
                    Gikam.getLastModal().window.showMask(true);
                    Gikam.put('/secure/emc/module/bp/bp/orders/results/action/set-done', Gikam.getJsonWrapper({}, [null, resultData])).done(function () {
                        _this.refreshRecordResultData();
                    }).always(function () {
                        Gikam.getLastModal().window.closeMask();
                    });
                }
            }
        });
    },

    editItem: function () {
        var _this = this;
        var selections = Gikam.getComp('emc-run-detail-record-edit-list').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var resultData = [];
        var orderTaskData = [];
        _this.getRecordResultSelectResultInfo(selections, resultData, orderTaskData);

        var orderIds = [];
        var labelNumbers = [];
        orderTaskData.map(function (item, i) {
            orderIds.push(item.orderId);
            labelNumbers.push(item.labelNumber);
        });

        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.RESULT.RUNS.MODAL.EDIT_ITEM',
            url: emcRun.baseUrl + '/page/edit-item' + Gikam.param({
                labelNumbers: labelNumbers.join(','),
                runId: _this.param.runId,
                pageType: 'runRecord',
                id: selections[0].id
            }),
            width: '40%',
            onAfterClose: function (afterData) {
                if (Gikam.isEmpty(afterData)) {
                    return;
                }
                Gikam.getLastModal().window.showMask(true);
                Gikam.post(emcRun.baseUrl + '/edit-item', Gikam.getJsonWrapper({
                    orderIds: orderIds.join(','),
                    runId: _this.param.runId
                }, ['emcItemServiceImpl', afterData])).done(function () {
                    var url = emcRun.baseUrl + '/action/trigger-formlua';
                    //Gikam.getLastModal().window.showMask(true);
                    Gikam.postText(url, Gikam.getJsonWrapper(null, ['emcOrderTaskServiceImpl', orderTaskData])).done(function () {
                        Gikam.postText(url, Gikam.getJsonWrapper(null, ['emcOrderTaskServiceImpl', orderTaskData])).done(function () {
                            _this.refreshRecordResultData();
                        }).always(function () {
                            Gikam.getLastModal().window.closeMask();
                        });
                    });
                });
            }
        });
    },

    getRecordResultButton: function () {
        var _this = this;
        return [{
            type: 'button',
            text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SYNC_TMPL_INFO',
            icon: 'refresh',
            onClick: function () {
                _this.syncTmplInfo();
            },
        }, {
            type: 'button',
            text: 'GIKAM.BUTTON.BATCH_INPUT',
            icon: 'edit',
            onClick: function () {
                _this.runInput();
            }
        }, {
            type: 'button',
            text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.INVALID_DATA',
            icon: 'edit',
            onClick: function () {
                _this.invalidData();
            }
        }, {
            type: 'button',
            text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.EDIT_ITEM',
            icon: 'edit',
            onClick: function () {
                _this.editItem();
            }
        }, {
            type: 'button',
            text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.INITIALIZEC',
            icon: 'refresh',
            onClick: function () {
                _this.runInitializec();
            }
        }, {
            type: 'button',
            text: 'GIKAM.BUTTON.EXPORT',
            icon: 'move-up',
            onClick: function () {
                _this.quickExport();
            }
        }, {
            type: 'button',
            text: 'GIKAM.BUTTON.IMPORT',
            icon: 'move-down',
            onClick: function () {
                _this.quickImport();
            }
        }, {
            type: 'button',
            text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.REMOVE_FOMULA',
            icon: 'remove',
            onClick: function () {
                _this.removeFomula();
            }
        }, {
            type: 'button',
            text: 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_RUN',
            icon: 'default',
            onClick: function () {
                var formData = Gikam.getComp('emc-run-input-detail-form').getData();

                var reportConfigLineId = formData.reportConfigLineId;
                if (Gikam.isEmpty(reportConfigLineId)) {
                    Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.HAS_NO_REPORT_TEMPLATE');
                    return;
                }
                Gikam.viewReport(reportConfigLineId, formData);
            }
        }, {
            type : 'button',
            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.EQUIPT_COLLECT',
            icon : 'edit',
            onClick : function() {
                _this.runEquiptCollect();
            }
        }, {
            type : 'button',
            text : 'EMC.MODULE.BP.SAMPLE.BUTTON.VIEW_SAMPLE_RECORD',
            icon : 'print',
            onClick : function() {
                var selections = Gikam.getComp('emc-run-detail-record-edit-list').getSelections();
                if (selections.length != 1) {
                    Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                    return;
                }
                Gikam.viewSampleRecord(selections[0].orderId);
            }
        }, {
            type: 'button',
            text: '仪器图谱',
            icon: 'add',
            hidden : true,
            onClick: function () {
                _this.addRunFile('atlas');
            }
        }, {
            type: 'button',
            text: '分析照片',
            icon: 'add',
            hidden : true,
            onClick: function () {
                _this.addRunFile('analysisPhoto');
            }
        }, {
            type : 'button',
            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.TRIGGER_FORMLUA',
            icon : 'activate',
            onClick : function() {
                _this.triggerFormlua();
            }
        }, {
            type : 'form',
            autoSave : false,
            id : 'emc-run-input-detail-real-time-calc-form',
            fields : [ {
                title : 'EMC.MODULE.BP.RESULT.RUNS.TITLE.REAL_TIME_CALC_FORMULA',
                field : 'realTimeCalc',
                type : 'simpleCheckbox',
                refreshGrid : false,
                value : '1',
                styleFormatter : function() {
                    return {
                        paddingLeft : '0'
                    }
                },
            } ]
        }];
    },

    runEquiptCollect : function() {
        var _this = this;
        Gikam.create('modal', {
            width : '70%',
            title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.CHOOSE_EQUIPT',
            url : emcRun.baseUrl + '/page/choose-acq-equipt' + Gikam.param({
                runId : _this.param.runId,
            }),
        });
    },

    addRunFile: function (bizCategory) {
        var _this = this;
        Gikam.create('modal', {
            title: '附件',
            url: '/secure/emc/module/mdm/file/files/page/view' + Gikam.param({
                table: 'T_EMC_RUN',
                id: _this.param.runId,
                bizCategory: bizCategory
            })
        })
    },

    triggerFormlua : function() {
        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.TRIGGER_FORMLUA', 'EMC.MODULE.BP.RESULT.RUNS.TIP.TRIGGER_FORMLUA', function() {
            var selections = Gikam.getComp('emc-run-detail-record-edit-list').getSelections();
            if (Gikam.isEmpty(selections)) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                return;
            }
            var url = emcRun.baseUrl + '/action/trigger-formlua';
            Gikam.getLastModal().window.showMask(true);
            Gikam.postText(url, Gikam.getJsonWrapper(null, [ 'emcOrderTaskServiceImpl', selections ])).done(function(){
                Gikam.getComp('emc-run-result-grid').refresh();
            }).always(function() {
                Gikam.getLastModal().window.closeMask();
            });
        });
    },

    removeFomula: function () {
        var _this = this;
        var selections = Gikam.getComp('emc-run-detail-record-edit-list').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }
        var resultData = [];
        var orderTaskData = [];
        _this.getRecordResultSelectResultInfo(selections, resultData, orderTaskData);
        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.REMOVE_FOMULA', 'EMC.MODULE.BP.RESULT.RUNS.TIP.REMOVE_FOMULA', function () {

            Gikam.getLastModal().window.showMask(true);
            Gikam.postText(emcRun.baseUrl + '/action/remove-fomulas', Gikam.getJsonWrapper(null, ['emcOrderTaskServiceImpl', orderTaskData], ['emcOrderTaskResultServiceImpl', resultData])).done(function () {
                _this.refreshRecordResultData();
            }).always(function () {
                Gikam.getLastModal().window.closeMask();
            });
        });
    },

    addRepeateNumber: function () {
        var _this = this;
        var selections = Gikam.getComp('emc-run-detail-record-edit-list').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var resultData = [];
        var orderTaskData = [];
        _this.getRecordResultSelectResultInfo(selections, resultData, orderTaskData);

        Gikam.create('modal', {
            width: '20%',
            height: '20%',
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.DATA_INPUT_TASK_RESULT_SET_REPEATENO',
            url: '/secure/emc/module/bp/sample/samples/page/repeat-number-set',
            onAfterClose: function (data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getLastModal().window.showMask(true);
                    resultData.map(function (item) {
                        item.repeatNo = data.repeatNo;
                    });
                    Gikam.postText('/secure/emc/module/bp/bp/orders/' + resultData[0].id + '/action/add-repreat-nos', Gikam.getJsonWrapper({}, [null, resultData])).done(function () {
                        _this.refreshRecordResultData();
                    }).always(function () {
                        Gikam.getLastModal().window.closeMask();
                    });
                }
            }
        });
    },

    delRepeateNumber: function () {
        var _this = this;
        var selections = Gikam.getComp('emc-run-detail-record-edit-list').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var resultData = [];
        var orderTaskData = [];
        _this.getRecordResultSelectResultInfo(selections, resultData, orderTaskData);
        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.DEL_REPEATE_NUMBER', 'EMC.MODULE.BP.RESULT.RUNS.TIP.DEL_REPEATE_NUMBER', function () {

            for (var i = 0; i < resultData.length; i++) {
                if (resultData[i].repeatNo == 1) {
                    Gikam.alert('GIKAM.TIP.CHOOSE_REPEAT_NO_DATA_GREATER_THAN_ONE');
                    return;
                }
            }
            Gikam.getLastModal().window.showMask(true);
            Gikam.postText('/secure/emc/module/bp/bp/orders/action/delete-repreat-nos', Gikam.getJsonWrapper({}, [null, selections])).done(function () {
                Gikam.getComp('emc-run-result-grid').refresh();
            }).always(function () {
                Gikam.getLastModal().window.closeMask();
            });
        });
    },

    quickImport: function () {
        var _this = this;
        var rightData = Gikam.getComp('emc-run-detail-record-edit-list').getData();
        if (Gikam.isEmpty(rightData)) {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CAN_NOT_IMPORT');
            return;
        }
        var resultData = [];
        var data = [];
        _this.getRecordResultSelectResultInfo(rightData, resultData, data);
        var id = data.map(function (e) {
            return e.id;
        });
        Gikam.create('simpleUploader', {
            dbTable: 'T_EMC_ORDER_TASK_RESULT',
            id: id,
            multiple: false,
            onAfterClose: function (files) {
                if (Gikam.isEmpty(files)) {
                    return;
                }
                Gikam.getLastModal().window.showMask(true);
                Gikam.post(
                    Gikam.printf(IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'emcRunImportFileFunction', {
                        id: files[0].id
                    })
                ).done(function (result) {
                    if (result.url) {
                        Gikam.emcDownloadFile(result.url);
                    } else if (result.message) {
                        Gikam.alert(result.message);
                    }
                    var selections = data;
                    var url = emcRun.baseUrl + '/action/trigger-formlua';
                    //Gikam.getLastModal().window.showMask(true);
                    Gikam.postText(url, Gikam.getJsonWrapper(null, ['emcOrderTaskServiceImpl', selections])).done(function () {
                        Gikam.postText(url, Gikam.getJsonWrapper(null, ['emcOrderTaskServiceImpl', selections])).done(function () {
                            _this.refreshRecordResultData();
                        }).always(function () {
                            Gikam.getLastModal().window.closeMask();
                        });
                    });
                });
            }
        });
    },

    quickExport: function () {
        var _this = this;
        var rightData = Gikam.getComp('emc-run-detail-record-edit-list').getData();
        if (Gikam.isEmpty(rightData)) {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CAN_NOT_EXPORT');
            return;
        }
        var runId = _this.param.runId;
        Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}/{id}', {
            templateCode: 'emcRunExportTemplate',
            service: 'emcRunExportTemplate',
            id: runId
        })).done(function (fileUri) {
            var fileUrl = IFM_CONTEXT + fileUri;
            Gikam.emcDownloadFile(fileUrl);
        });

    },

    getOriginDataPanel: function () {
        var _this = this;
        emcRun.resultInputPage.param = {
            runId: _this.param.runId,
        };
        return {
            // 原始数据
            id: 'emc-run-detail-origindata-panel',
            title: 'EMC.MODULE.BP.RESULT.RUNS.TAB.ORIGINDATA',
            show: false,
            items: [{
                type: 'layout',
                west: {
                    width: '45%',
                    items: [emcRun.resultInputPage.getTestSample()]
                },
                center: {
                    items: [emcRun.resultInputPage.getResult()]
                }
            }]
        }
    },
    getBrowseRecordPanel: function () {
        var _this = this;
        return {
            id: 'emc-run-detail-browse-record-panel',
            title: 'EMC.MODULE.BP.RESULT.RUNS.TAB.BROWSE_RECORDS',
            show: false,
            items: [{
                type: 'layout',
                center: {
                    items: [{
                        type: 'iframe',
                        id: 'emc-run-detail-list-report-iframe',
                        src: '',

                    }]
                }
            }]
        }
    },
    getQcDataPanel: function () {
        var _this = this;
        return {
            id: 'emc-run-detail-qcdata-panel',
            title: 'EMC.MODULE.BP.RESULT.RUNS.TAB.QCDATA',
            show: false,
            items: [{
                type: 'layout',
                center: {
                    items: [_this.getQcOrderDataGrid()]
                }
            }]
        };
    },
    onQcOrderDataActive: function (row) {

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
                if (e.display == 'displayedit') {
                    if (e.fieldName == 'standQty' && row.qcCategoryCode === 'QC-STD') {
                        // 标准值特殊处理
                        arr.push({
                            field: e.fieldName,
                            title: e.fieldTitle,
                            editor: true,
                            width: e.width,
                            sort: false,
                            onChange: function (name, value, index) {
                                var uncertainty = this.getData()[index].rn16;
                                // 修约规则要按照标准值来
                                var revisionRule = this.getData()[index].revisionRule;
//                                if(!Gikam.isEmpty(revisionRule)) {
//                                    let tail = revisionRule.substr(revisionRule.indexOf(',') + 1);
//                                    tail = value.indexOf('.') != -1 ? value.length - value.indexOf('.') -1 + '' : '0';
//                                    revisionRule = 'FDA,' + tail;
//                                }

                                if (Gikam.isNotEmpty(value) && uncertainty) {
                                    this.setData({
                                        index: index,
                                        indexLowLimit: (Number(value) * 10000 - Number(uncertainty) * 10000) / 10000,
                                        indexHighLimit: (Number(value) * 10000 + Number(uncertainty) * 10000) / 10000,
                                        standQty: value,
                                        revisionRule: revisionRule
                                    });
                                    return;
                                }
                                this.setData({
                                    index: index,
                                    indexLowLimit: '',
                                    indexHighLimit: '',
                                    standQty: value,
                                    revisionRule: revisionRule
                                });
                                return;
                            },
                        });
                    } else if (e.fieldName == 'rn14' && row.qcCategoryCode === 'QC-STD') {
                        // 不确定度特殊处理
                        arr.push({
                            field: e.fieldName,
                            title: e.fieldTitle,
                            editor: true,
                            width: e.width,
                            sort: false,
                            onChange: function (name, value, index) {
                                var stdValue = this.getData()[index].standQty;
                                if (Gikam.isNotEmpty(value) && stdValue) {
                                    this.setData({
                                        index: index,
                                        indexLowLimit: (Number(stdValue) * 10000 - Number(value) * 10000) / 10000,
                                        indexHighLimit: (Number(stdValue) * 10000 + Number(value) * 10000) / 10000,
                                        rn14: value
                                    });
                                    return;
                                }
                                this.setData({
                                    index: index,
                                    indexLowLimit: '',
                                    indexHighLimit: '',
                                    rn14: value
                                });
                            },
                        });
                    } else if (e.fieldName == 'rn16' && row.qcCategoryCode === 'QC-STD') {
                        arr.push({
                            field: e.fieldName,
                            title: e.fieldTitle,
                            editor: true,
                            width: e.width,
                            type: 'choose',
                            category: 'unit',
                            single: true,
                            targetFields: [{rn16: 'unitName'}],
                            sort: false,
                        });
                    } else if (e.fieldName == 'standQty' && row.qcCategoryCode === 'L-QXCH') {
                        arr.push({
                            field: e.fieldName,
                            title: e.fieldTitle,
                            editor: true,
                            width: e.width,
                            sort: false,
                            onChange: function (name, value, index) {
                                if (Gikam.isEmpty(value)) {
                                    this.setData({
                                        index: index,
                                        standQty: '',
                                        rn5: ''
                                    });
                                }
                            },
                        });
                    } else {
                        arr.push({
                            field: e.fieldName,
                            title: e.fieldTitle,
                            editor: true,
                            width: e.width,
                            sort: false
                        });
                    }
                } else if (e.display == 'displaynoedit') {
                    arr.push({
                        field: e.fieldName,
                        title: e.fieldTitle,
                        editor: false,
                        width: e.width,
                        sort: false
                    });
                } else if (Gikam.isEmpty(e.display)) {
                    arr.push({
                        field: e.fieldName,
                        title: e.fieldTitle,
                        width: e.width,
                        sort: false
                    });
                }
            });

            var publicColumns = emcRun.getQcResultPublicColumns();
            var columns = publicColumns.slice(0, 10).concat(arr, publicColumns.slice(10));
            Gikam.getComp('emc-run-detail-qcdata-result-grid').refreshColumns(columns);
            Gikam.getComp('emc-run-detail-qcdata-result-grid').refresh({
                url: '/secure/emc/module/bp/bp/order-task-results/queries',
                requestData: {
                    orderTaskId_IN: row.ext$.otids,
                    runId_SEQ: row.runId,
                }
            }).done(function (data) {
                var grid = Gikam.getComp('emc-run-result-grid');
                for (var i = 0; i < data.data.length; i++) {
                    if (data.data[i].scriptFlag == '0' && Gikam.isEmpty(data.data[i].originalResult)) {
                        grid.focusField(i, 'originalResult');
                        break;
                    }
                }
            });

        });
    },
    getQcOrderDataGrid: function () {
        var _this = this;

        return {
            type: 'layout',
            west: {
                width: '38%',
                items: [{
                    type: 'grid',
                    id: 'emc-run-detail-qcdata-grid',
                    columns: emcRun.getQcGridColumns(),
                    url: Gikam.printf(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/run-qc/{runId}', {
                        runId: _this.param.runId
                    }),
                    onLoadSuccess: function (data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        } else {
                            Gikam.cleanCompData(["emc-run-detail-qcdata-result-grid"]);
                        }
                    },
                    onRowActive: function (index, row) {
                        _this.onQcOrderDataActive(row)
                    },
                }]
            },
            center: {
                items: [{
                    type: 'grid',
                    id: 'emc-run-detail-qcdata-result-grid',
                    columns: [],
                    service: 'emcOrderTaskResultServiceImpl',
                    onUpdated: function (data, fields, index) {

                        var orderTaskResultRow = Gikam.getComp('emc-run-detail-qcdata-result-grid').getActivedRow();
                        var qcCategoryCode = orderTaskResultRow.qcCategoryCode;

                        if (fields.indexOf('originalResult') > -1 || fields.indexOf('standQty') > -1 || fields.indexOf('lowLimit') > -1 || fields.indexOf('highLimit') > -1 || fields.indexOf('revisionRule') > -1 || (fields.indexOf('rn5') > -1 && qcCategoryCode === 'QXBD') || (fields.indexOf('rn15') > -1 && qcCategoryCode === 'L-SPKC') || (fields.indexOf('rn15') > -1 && qcCategoryCode === 'KBJB') || (fields.indexOf('rn15') > -1 && qcCategoryCode === 'L-SPKC') || (fields.indexOf('rn16') > -1 && qcCategoryCode === 'QC-STD')) {

                            Gikam.getLastModal().window.showMask(true);
                            var orderTaskResultUrl = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results';

                            Gikam.put(orderTaskResultUrl + '/action/change-result', Gikam.getJsonWrapper({}, ['', [data]])).done(function () {
                                Gikam.post(orderTaskResultUrl + '/queries', Gikam.getJsonWrapper({
                                    n: -1,
                                    f: {
                                        orderId_SEQ: orderTaskResultRow.orderId,
                                        runId_SEQ: _this.param.runId
                                    }
                                })).done(function (data) {
                                    Gikam.each(data.rows, function (index, row) {
                                        Gikam.getComp('emc-run-detail-qcdata-result-grid').setData(row, false, true);
                                    });

                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                });

                            }).fail(function () {
                                Gikam.getLastModal().window.closeMask();
                            });
                        }
                    },

                }]
            }
        }
    },
    getDataCheckPanel: function () {
        var _this = this;
        return {
            // 数据检查
            id: 'emc-run-detail-datacheck-panel',
            title: 'EMC.MODULE.BP.RESULT.RUNS.TAB.DATA_CHECK',
            show: false,
            items: [{
                type: 'layout',
                west: {
                    width : '40%',
                    title : '批数据检查',
                    items: [{
                        type: 'grid',
                        id: 'emc-run-detail-list-data-check-run-grid',
                        columns: emcRun.getRunDataCheckRunListColumns(),
                        url: Gikam.printf('/secure/emc/module/bp/result/run-reminds/queries/runRemind/{runId}',{
                            runId : _this.param.runId
                        })
                    }]
                },
                center: {
                    title : '异常结果数据检查',
                    items: [{
                        type: 'grid',
                        id: 'emc-run-detail-list-data-check-result-grid',
                        columns: emcRun.getRunDataCheckResultListColumns(),
                        url: Gikam.printf('/secure/emc/module/bp/result/run-reminds/queries/resultRemind/{runId}',{
                            runId : _this.param.runId
                        })
                    }]
                },
            }]
        }
    },

    getAttachPanel: function () {
        var _this = this;
        return {
            // 附件
            id: 'emc-run-detail-attach-panel',
            title: 'GIKAM.FILE.ATTACHMENT',
            show: false,
            items: [{
                type: 'uploader',
                id: 'emc-run-detail-attach-panel-uploader',
                dbTable: 'T_EMC_RUN'
            }]
        }
    },

    getRunDetailTab: function () {
        var _this = this;
        return {
            type: 'tab',
            id: 'emc-run-detail-tab',
            fill: true,
            panels: [
                _this.getRunDetailInfoPanel(),//记录首页
                // _this.getRunMethodParamPanel(),//方法参数（外层使用选模板后保存方法，不是这种首先固定模板的）
                _this.getRunQcPanel(),//添加质控
                _this.getCurveInfoPanel(),//选择曲线-选择校准曲线
                _this.getEquiptCurveInfoPanel(),//选择曲线-选择仪器曲线
                /*_this.getEquiptConditionPanel(),//选择曲线-仪器条件*/
                _this.getAnalysisPanel(),//记录数据
                _this.getOriginDataPanel(),//原始数据
                _this.getQcDataPanel(),//质控结果
                _this.getDataCheckPanel(),//数据检查
                _this.getBrowseRecordPanel(),//浏览记录
                _this.getAttachPanel()//附件
            ],
            onActived: function (data) {
                // if (data.title === '方法参数') {
                //     _this.loadRunParamPropertyField();
                // }
                if (data.title === '记录数据') {
                    _this.refreshRecordResultData();
                }
                if (data.title === '原始数据') {
                    Gikam.getComp('emc-run-sample-test-grid').refresh();
                }
                if (data.title === '质控结果') {
                    Gikam.getComp('emc-run-detail-qcdata-grid').refresh();
                }
                if (data.title === '选择校准曲线') {
                    var formData = Gikam.getComp('emc-run-input-detail-form').getData();
                    if (!Gikam.isEmpty(formData.curveId)) {
                        Gikam.getAsyncComp('emc-run-detail-curve-list-grid').done(function (grid) {
                            grid.refresh({
                                url: IFM_CONTEXT + '/secure/emc/module/bp/result/curves' + '/queries/raw',
                                requestData: {
                                    id_IN: formData.curveId
                                }
                            }).done(function () {
                                Gikam.getLastModal().window.closeMask();
                            });
                        });
                    }
                }
                if (data.title === '选择仪器曲线') {
                    var formData = Gikam.getComp('emc-run-input-detail-form').getData();
                    if (!Gikam.isEmpty(formData.equiptCurveId)) {
                        Gikam.getAsyncComp('emc-run-detail-equipt-curve-list-grid').done(function (grid) {
                            grid.refresh({
                                url: IFM_CONTEXT + '/secure/emc/module/bp/result/equipt-curves' + '/queries/raw',
                                requestData: {
                                    id_IN: formData.equiptCurveId
                                }
                            });
                        })
                    }
                }
                if (data.title === '数据检查') {
                    Gikam.getAsyncComp('emc-run-detail-list-data-check-run-grid').done(function (grid) {
                        grid.refresh();
                    });
                    Gikam.getAsyncComp('emc-run-detail-list-data-check-result-grid').done(function (grid) {
                        grid.refresh();
                    })
                }
                if (data.title === '浏览记录') {
                    var formData = Gikam.getComp('emc-run-input-detail-form').getData();
                    var reportConfigLineId = formData.reportConfigLineId;
                    if (Gikam.isEmpty(reportConfigLineId)) {
                        Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.HAS_NO_REPORT_TEMPLATE');
                        Gikam.getAsyncComp('emc-run-detail-list-report-iframe').done(function (frame) {
                            frame.refresh({
                                src: ''
                            });
                        });
                    } else {
                        Gikam.getAsyncComp('emc-run-detail-list-report-iframe').done(function (frame) {
                            frame.refresh({
                                src: Gikam.getReportUrl(reportConfigLineId, [formData])
                            })
                        });
                    }
                }
                if (data.title === '附件') {
                    Gikam.getComp('emc-run-detail-attach-panel-uploader').setOptions({
                        bizId: _this.param.runId
                    });
                }
            }
        }
    },
    submit: function () {
        var formData = Gikam.getComp('emc-run-input-detail-form').getData();
        if (Gikam.getComp('emc-run-input-detail-form').validate()) {
            // 提交之前先校验数据
            workspace.window.showMask(true);
            Gikam.post(emcRun.baseUrl + '/action/validate-result-status', Gikam.getJsonWrapper(null, [ 'pfRunServiceImpl', [formData] ])).done(function (res) {
                if (res.valid) {
                    Gikam.create('workflow').submit({
                        data : [formData],
                        pageObject : emcRun
                    }).done(function() {

                    });

                    return;
                }

                Gikam.confirm('GIKAM.TIP.CONFIRMSUBMIT', res.message, function() {
                    Gikam.create('workflow').submit({
                        data : [formData],
                        pageObject : emcRun
                    }).done(function() {

                    });
                });
            }).always(function () {
                workspace.window.closeMask();
            });
        }
    },
    // submit: function () {
    //     var formData = Gikam.getComp('emc-run-input-detail-form').getData();
    //     if (Gikam.getComp('emc-run-input-detail-form').validate()) {
    //         // 提交之前先校验数据
    //         workspace.window.showMask(true);
    //         Gikam.postText(emcRun.baseUrl + '/run-submit-check', Gikam.getJsonWrapper({}, [null, [formData]])).done(function (msg) {
    //             if (!Gikam.isEmpty(msg)) {
    //                 if (msg.indexOf('是否继续提交') != -1) {
    //                     Gikam.create('confirm', {
    //                         message: msg,
    //                         title: '数据超标提醒！',
    //                         yesText: '确认',
    //                         noText: '取消',
    //                         height: 245,
    //                         onYesClick: function () {
    //                             Gikam.create('workflow').submit({
    //                                 data: [{id: formData.id}],
    //                                 pageObject: emcRun
    //                             }).done(function () {
    //                                 Gikam.getLastModal().close();
    //
    //                             });
    //                         },
    //                     });
    //                 } else if ('success' === msg) {
    //                     Gikam.create('workflow').submit({
    //                         data: [{id: formData.id}],
    //                         pageObject: emcRun
    //                     }).done(function () {
    //                         Gikam.getLastModal().close();
    //                     });
    //                 } else if (msg.indexOf('不能提交') != -1) {
    //                     Gikam.alert(msg);
    //                 }
    //             }
    //         }).always(function () {
    //             workspace.window.closeMask();
    //         });
    //         ;
    //     }
    // },

    createPage: function () {
        var _this = this;
        Gikam.create('layout', {
            renderTo: Gikam.getLastModal().window.$dom,
            north: {
                items: [_this.getBackButton()]
            },
            resize: false,
            center: {
                items: [{
                    type: 'layout',
                    id: 'emc-run-edit-detail-list-layout',
                    north: {
                        height: 50,
                        items: [_this.getRunDetailNext()]
                    },
                    center: {
                        items: [_this.getRunDetailTab()]
                    }
                }]
            }
        });
        return this;
    },

    init: function (param) {
        this.param = param;
        this.createPage();
    }
};
