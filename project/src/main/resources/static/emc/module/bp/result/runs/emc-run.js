var emcRun = Gikam.getPageObject({

    baseUrl: IFM_CONTEXT + '/secure/emc/module/bp/result/runs',
    resultBaseUrl: IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results',

    getEditListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'ext$.urgent',
            title: '任务时效',
            type: 'select',
            formatter: function (i, v, r) {
                if (v === '1') {
                    return '<font color="red">' + '超期' + '</font>'
                } else if (v === '2') {
                    return '<font color="red">' + '加急' + '</font>'
                } else if (v === '3') {
                    return '<font color="red">' + '预警' + '</font>'
                } else {
                    return '正常';
                }
            },
            items: [{
                text: '超期',
                value: '1'
            }, {
                text: '加急',
                value: '2'
            }, {
                text: '预警',
                value: '3'
            }, {
                text: '正常',
                value: '4'
            }],
        }, {
            field: 'ext$.orderdate',
            title: '送/采样日期',
            type: 'date'
        }, {
            field: 'ext$.requiredcompletedate',
            title: '需完成日期'
        }, {
            field: 'ext$.projno',
            title: '任务编号',
            type: 'link'
        }, {
            field: 'testName',
            title: 'T_EMC_ORDER_TASK.TESTNAME'
        }, {
            field: 'methodName',
            title: 'T_EMC_ORDER_TASK.METHODNAME',
            type: 'link'
        }, {
            field: 'testMethodBasis',
            title: 'T_EMC_ORDER_TASK.TESTMETHODBASIS',
        }, {
            field: 'orderCategory',
            title: 'T_EMC_ORDER_TASK.ORDERCATEGORY',
        }, {
            field: 'ext$.testclass',
            title: 'T_EMC_TEST_METHOD.TESTCLASS',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'testClass',
            },
        }];
    },

    getPrepareShowAllField: function () {
        return [{
            title: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.SHOW_ALL',
            field: 'showFlag',
            type: 'simpleCheckbox',
            refreshGrid: false,
            styleFormatter: function () {
                return {
                    paddingLeft: '0'
                }
            },
            onChange: function (field, value, index) {
                emcRun.editPage.projId = null;
                // workspace.window.showMask(true);
                var grid = Gikam.getComp('emc-run-edit-list-grid');

                var showFlags = value;
                if (showFlags === '1') {
                    grid.refresh({
                        requestData: {
                            inputUserId: '',
                            orgId: '',
                            // authority_org: ''
                        }
                    }).done(function () {
                        // workspace.window.closeMask();
                    });
                } else {
                    grid.refresh({
                        requestData: {
                            inputUserId: workspace.user.id,
                            orgId: workspace.user.ext$.parentorgid,
                            // authority_org: '1'
                        }
                    }).done(function () {
                        // workspace.window.closeMask();
                    });
                }
            },
        }]
    },

    getTestRunColumns: function () {
        return [{
            checkbox: true,
        }, Gikam.status.getBpmnColumn(emcRun.workflow.dbTable), {
            field: 'projNo',
            title: '任务编号',
        }, {
            field: 'id',
            title: 'T_EMC_RUN.RUNID',
            type: 'link',
            width: 120,
        }, {
            field: 'resultInput',
            title: 'T_EMC_RUN.RESULTINPUT',
            formatter: function (i, v, row) {
                return Gikam.printf('<a href="javascript:;" onclick="emcRun.editPage.loadResultInputPage(\'{id}\')">{name}</a>', {
                    id: row.id,
                    name: '结果录入'
                });
            },
            width: 90,
            sort: false
        }, {
            field: 'orderCategory',
            title: 'T_EMC_RUN.ORDERCATEGORY',
        }, {
            field: 'testName',
            title: 'T_EMC_RUN.TESTNAME',
        }, {
            field: 'testId',
            title: 'T_EMC_RUN.TESTID',
        }, {
            field: 'ext$.testclass',
            title: 'T_EMC_TEST_METHOD.TESTCLASS',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'testClass',
            },
        }, {
            field: 'ext$.methodname',
            title: '方法名称',
            type: 'link',
        }, {
            field: 'methodParam',
            title: 'T_EMC_RUN.METHODPARAM',
            formatter: function (i, v, row) {
                return Gikam.printf('<a href="javascript:;" onclick="emcRun.editPage.loadResultRunMethodParamPage(\'{id}\')">{name}</a>', {
                    id: row.id,
                    name: '方法参数'
                });
            }
        }, {
            field: 'analysisUserName',
            title: 'T_EMC_RUN.ANALYSISUSERNAME',
            editor: true,
            width: 100,
            category: 'emp',
            type: 'choose',
            single: true,
            onBeforeChoose: function () {
                var activedRow = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow();
                return {
                    departmentid_SEQ: activedRow.createdByOrgId
                }
            },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-run-edit-list-test-run-grid').setData({
                        id: Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        analysisUserName: rows[0].userName,
                        analysisUserId: rows[0].id
                    });
                }
            },
            onClean: function () {
                Gikam.getComp('emc-run-edit-list-test-run-grid').setData({
                    id: Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                    analysisUserName: '',
                    analysisUserId: ''
                });
            }
        }, {
            field: 'partakeUserName',
            title: 'T_EMC_RUN.PARTAKEUSERNAME',
            editor: true,
            width: 100,
            category: 'emp',
            type: 'choose',
            onBeforeChoose: function () {
                return {
                    targetType: 'analysisUser'
                }
            },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    var partakeUserNames = [];
                    var partakeUserIds = [];
                    Gikam.each(rows, function (index, item) {
                        partakeUserNames.push(item.userName);
                        partakeUserIds.push(item.id);
                    });
                    this.setData({
                        id: Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        partakeUserName: partakeUserNames.join(','),
                        partakeUserId: partakeUserIds.join(',')
                    });
                }
            },
            onClean: function () {
                this.setData({
                    id: Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                    partakeUserName: '',
                    partakeUserId: ''
                });
            }
        }, {
            field: 'checkUserName',
            title: 'T_EMC_RUN.CHECKUSERNAME',
            editor: true,
            category: 'emp',
            type: 'choose',
            single: true,
            width: 100,
            validators: ['notEmpty'],
            onBeforeChoose: function () {
                var activedRow = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow();
                return {
                    id_DIFFER: activedRow.analysisUserId,
                    parentOrgId_SEQ : workspace.user.ext$.parentorgid
                }
            },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    var checkUserNames = [];
                    var checkUserIds = [];
                    Gikam.each(rows, function (index, item) {
                        checkUserNames.push(item.userName);
                        checkUserIds.push(item.id);
                    });
                    this.setData({
                        id: Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        checkUserName: checkUserNames.join(','),
                        checkUserId: checkUserIds.join(',')
                    });
                }
            },
            onClean: function () {
                Gikam.getComp('emc-run-edit-list-test-run-grid').setData({
                    id: Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                    checkUserId: '',
                    checkUserName: ''
                });
            }
        }, {
            field: 'ext$.equiptcode',
            title: 'T_EMC_RUN.EQUIPTID',
            width: 100,
        }, {
            field: 'curveNo',
            title: 'T_EMC_RUN.SELECTRUN',
            width: 120,
            type: 'choose',
            category: 'curve',
            onBeforeChoose: function (index) {
                var row = Gikam.getComp('emc-run-edit-list-test-run-grid').getData()[index];
                return {
                    equiptId: row.equiptId,
                    testId: row.testId,
                    methodId: row.methodId,
                    analysisBeginDate: row.analysisBeginDate,
                    testMethodId: row.testMethodId,
                }
            },
            onAfterChoose: function (rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    var curveIds = '';
                    var curveNos = '';
                    rows.map(function (item, i) {
                        curveIds += rows[i].id + ',';
                        curveNos += rows[i].curveNo + ',';
                    });

                    this.setData({
                        index: index,
                        curveId: curveIds.substring(0, curveIds.length - 1),
                        curveNo: curveNos.substring(0, curveNos.length - 1)
                    });

                    Gikam.post(emcRun.baseUrl + '/action/set-default-curve-value', Gikam.getJsonWrapper({
                        runId: this.getActivedRow().id
                    }, ['', rows]));
                }
            },
            onClean: function () {
                this.setData({
                    id: this.getActivedRow().id,
                    curveId: '',
                    curveNo: ''
                });
            },
            editor: true
        }, {
            field: 'analysisBeginDate',
            title: 'T_EMC_RUN.ANALYSISBEGINDATE',
            editor: true,
            type: 'date',
            validators: ['notEmpty']
        }, {
            field: 'analysisBeginTime',
            title: 'T_EMC_RUN.ANALYSISBEGINTIME',
            editor: true,
            type: 'time',
            width: 100
        }, {
            field: 'analysisEndDate',
            title: 'T_EMC_RUN.ANALYSISENDDATE',
            editor: true,
            type: 'date',
            validators: ['notEmpty']
        }, {
            field: 'analysisEndTime',
            title: 'T_EMC_RUN.ANALYSISENDTIME',
            editor: true,
            width: 100,
            type: 'time'
        }, {
            field: 'assistEquiptName',
            title: 'T_EMC_RUN.ASSISTEQUIPTID',
            editor: true,
            category: 'equipt',
            type: 'choose',
            onBeforeChoose: function (index) {
                return {
                    //equiptcategoryname_SEQ : '实验',
                    targetIds: this.getData()[index].assistEquiptId,
                    orgId_SEQ: workspace.user.ext$.parentorgid,
                    testMethodIds: this.getData()[index].testMethodId,
                    targetType: 'auxiliaryEquipt',
                    id_differ: this.getData()[index].equiptId,
                }
            },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    var assistEquiptId = [];
                    var assistEquiptName = [];
                    var assistEquiptModel = [];
                    var assistEquiptNo = [];
                    Gikam.each(rows, function (index, item) {
                        assistEquiptId.push(item.id);
                        assistEquiptName.push(item.equiptName);
                        assistEquiptModel.push(item.equiptName + " / " + item.specification);
                        assistEquiptNo.push(item.equiptId);
                    });
                    this.setData({
                        id: Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        assistEquiptId: assistEquiptId.join(','),
                        assistEquiptName: assistEquiptName.join(','),
                        assistEquiptModel: assistEquiptModel.join(),
                        assistEquiptNo: assistEquiptNo.join()
                    });
                }
            },
            onClean: function () {
                Gikam.getComp('emc-run-edit-list-test-run-grid').setData({
                    id: Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                    assistEquiptId: '',
                    assistEquiptName: '',
                    assistEquiptModel: '',
                    assistEquiptNo: ''
                });
            }
        }, {
            field: 'temperature',
            title: 'T_EMC_RUN.TEMPERATURE',
            editor: true,
            width: 100,
            validators: ['strLength[0,30]', 'notEmpty']
        }, {
            field: 'humidity',
            title: 'T_EMC_RUN.HUMIDITY',
            width: 100,
            editor: true,
            validators: ['strLength[0,30]', 'notEmpty']
        }, {
            field: 'receiveDate',
            title: 'T_EMC_RUN.RECEIVEDATE',
        }, {
            field: 'receiveTime',
            title: 'T_EMC_RUN.RECEIVETIME',
            type: 'time'
        }, {
            field: 'handleDate',
            title: 'T_EMC_RUN.HANDLEDATE',
            editor: true,
            type: 'date'
        }, {
            field: 'handleMethod',
            title: 'T_EMC_RUN.HANDLEMETHOD',
            editor: true,
            width: 100,
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'handleMethod'
            },

        }, {
            field: 'beforeHandle',
            title: 'T_EMC_RUN.BEFOREHANDLE',
            type: 'choose',
            category: 'beforeHandle',
            single: true,
            onBeforeChoose: function () {
                return {
                    testMethodId_IN: this.getActivedRow().testMethodId
                }
            },
            onAfterChoose: function (rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index: index,
                        beforeHandleId: rows[0].id,
                        beforeHandle: rows[0].beforeHandle
                    });
                }
            },
            onClean: function () {
                this.setData({
                    id: this.getActivedRow().id,
                    beforeHandleId: '',
                    beforeHandle: ''
                });
            },
            editor: true,
            width: 100,
        }, {
            field: 'reportConfigLineId',
            title: 'T_EMC_RUN.REPORTCONFIGLINEID',
            editor: true,
            type: 'select',
            search: true,
            category: 'reportConfigLine',
            param: {
                reportConfigId: 'analReport',
                filter_userId: workspace.user.id,
            },
            validators: ['notEmpty']
        }, {
            field: 'ext$.sol1no',
            title: 'T_EMC_RUN.SOL1ID',
        }
            // ,{
            //     field : 'ext$.sol1no',
            //     title : 'T_EMC_RUN.SOL1NO',
            // }, {
            //     field : 'ext$.sol2name',
            //     title : 'T_EMC_RUN.SOL2ID',
            // },{
            //     field : 'ext$.sol2no',
            //     title : 'T_EMC_RUN.SOL2NO',
            // }
            , {
                field: 'ext$.mulregentname',
                title: 'T_EMC_RUN.REAGENTID',
            }
            // , {
            //     field : 'relatedRunId',
            //     title : 'T_EMC_RUN.RELATEDRUNID',
            // }
            , {
                field: 'remark',
                title: 'T_EMC_RUN.REMARK',
                editor: true,
                type: 'richText'
            }, {
                field: 'stNo',
                title: '标准物质编号',
                editor: true,
                validators : [ 'notEmpty'],
            }, {
                field: 'stConcent',
                title: '标准物质浓度',
                editor: true,
            }, {
                field: 'stValiDateStr',
                title: '标准物质有效期',
                editor: true,
                validators : [ 'notEmpty'],
            }
            // , {
            //     field : 'ext$.weighingstatus',
            //     title : 'T_EMC_RUN.WEIGHINGSTATUS',
            //     type : 'select',
            //     category : 'sys',
            //     param : {
            //         codeCategoryId : 'completeFlag'
            //     },
            // }, {
            //     field : 'ext$.pretreatstatus',
            //     title : 'T_EMC_RUN.PRETREATSTATUS',
            //     type : 'select',
            //     category : 'sys',
            //     param : {
            //         codeCategoryId : 'completeFlag'
            //     },
            // }
            , {
                field: 'createdTime',
                title: 'T_EMC_RUN.CREATEDTIME',
            }, {
                field: 'ext$.adddate',
                title: 'T_EMC_RUN.ADDDATE',
                type: 'date',
                editor: true
            }, {
                field: 'ext$.isshowstdsolution',
                title: 'T_EMC_RUN.ISSHOWSTDSOLUTION',
                editor: true,
                type: 'select',
                search: true,
                category: 'sys',
                param: {
                    codeCategoryId: 'yesorno',
                },
            }];
    },

    getTestRunSearchColumns: function () {
        return [{
            checkbox: true,
        }, Gikam.status.getBpmnColumn(emcRun.workflow.dbTable), {
            field: 'projNo',
            title: 'T_EMC_RUN.PROJNO',
        }, {
            field: 'id',
            title: 'T_EMC_RUN.RUNID',
            type: 'link',
            width: 120,
        },  {
            field: 'ext$.sampleordernum',
            title: '样品数量',
        }, {
            field: 'resultInput',
            title: 'T_EMC_RUN.RESULTINPUT',
            formatter: function (i, v, row) {
                return Gikam.printf('<a href="javascript:;" onclick="emcRun.runQuerySearchPage.loadResultInputPage(\'{id}\')">{name}</a>', {
                    id: row.id,
                    name: '结果录入'
                });
            },
            width: 90,
            sort: false
        }, {
            field: 'orderCategory',
            title: 'T_EMC_RUN.ORDERCATEGORY',
        }, {
            field: 'testName',
            title: 'T_EMC_RUN.TESTNAME',
        }, {
            field: 'analysisUserName',
            title: 'T_EMC_RUN.ANALYSISUSERNAME',
            width: 100,
            category: 'emp',
            type: 'choose',
            single: true,
            onBeforeChoose: function () {
                var activedRow = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow();
                return {
                    departmentid_SEQ: activedRow.createdByOrgId
                }
            },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-run-edit-list-test-run-grid').setData({
                        id: Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        analysisUserName: rows[0].userName,
                        analysisUserId: rows[0].id
                    });
                }
            },
            onClean: function () {
                Gikam.getComp('emc-run-edit-list-test-run-grid').setData({
                    id: Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                    analysisUserName: '',
                    analysisUserId: ''
                });
            }
        }, {
            field: 'partakeUserName',
            title: 'T_EMC_RUN.PARTAKEUSERNAME',
            width: 100,
            category: 'emp',
            type: 'choose',
            onBeforeChoose: function () {
                return {
                    targetType: 'analysisUser'
                }
            },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    var partakeUserNames = [];
                    var partakeUserIds = [];
                    Gikam.each(rows, function (index, item) {
                        partakeUserNames.push(item.userName);
                        partakeUserIds.push(item.id);
                    });
                    this.setData({
                        id: Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        partakeUserName: partakeUserNames.join(','),
                        partakeUserId: partakeUserIds.join(',')
                    });
                }
            },
            onClean: function () {
                this.setData({
                    id: Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                    partakeUserName: '',
                    partakeUserId: ''
                });
            }
        }, {
            field: 'checkUserName',
            title: 'T_EMC_RUN.CHECKUSERNAME',
            category: 'emp',
            type: 'choose',
            width: 100,
            validators: ['notEmpty'],
            onBeforeChoose: function () {
                var activedRow = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow();
                return {
                    id_DIFFER: activedRow.analysisUserId,
                    departmentid_SEQ: activedRow.createdByOrgId
                }
            },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    var checkUserNames = [];
                    var checkUserIds = [];
                    Gikam.each(rows, function (index, item) {
                        checkUserNames.push(item.userName);
                        checkUserIds.push(item.id);
                    });
                    this.setData({
                        id: Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        checkUserName: checkUserNames.join(','),
                        checkUserId: checkUserIds.join(',')
                    });
                }
            },
            onClean: function () {
                Gikam.getComp('emc-run-edit-list-test-run-grid').setData({
                    id: Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                    checkUserId: '',
                    checkUserName: ''
                });
            }
        }, {
            field: 'ext$.equiptcode',
            title: 'T_EMC_RUN.EQUIPTID',
            width: 100,
        }, {
            field: 'curveNo',
            title: 'T_EMC_RUN.SELECTRUN',
            width: 120,
            type: 'choose',
            category: 'curve',
            onBeforeChoose: function (index) {
                var row = Gikam.getComp('emc-run-edit-list-test-run-grid').getData()[index];
                return {
                    equiptId: row.equiptId,
                    testId: row.testId,
                    methodId: row.methodId,
                    analysisBeginDate: row.analysisBeginDate,
                    testMethodId: row.testMethodId,
                }
            },
            onAfterChoose: function (rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    var curveIds = '';
                    var curveNos = '';
                    rows.map(function (item, i) {
                        curveIds += rows[i].id + ',';
                        curveNos += rows[i].curveNo + ',';
                    });

                    this.setData({
                        index: index,
                        curveId: curveIds.substring(0, curveIds.length - 1),
                        curveNo: curveNos.substring(0, curveNos.length - 1)
                    });

                    Gikam.post(emcRun.baseUrl + '/action/set-default-curve-value', Gikam.getJsonWrapper({
                        runId: this.getActivedRow().id
                    }, ['', rows]));
                }
            },
            onClean: function () {
                this.setData({
                    id: this.getActivedRow().id,
                    curveId: '',
                    curveNo: ''
                });
            },
        }, {
            field: 'analysisBeginDate',
            title: 'T_EMC_RUN.ANALYSISBEGINDATE',
            type: 'date',
            validators: ['notEmpty']
        }, {
            field: 'analysisBeginTime',
            title: 'T_EMC_RUN.ANALYSISBEGINTIME',
            type: 'time',
            width: 100
        }, {
            field: 'analysisEndDate',
            title: 'T_EMC_RUN.ANALYSISENDDATE',
            type: 'date',
            validators: ['notEmpty']
        }, {
            field: 'analysisEndTime',
            title: 'T_EMC_RUN.ANALYSISENDTIME',
            width: 100,
            type: 'time'
        }, {
            field: 'assistEquiptName',
            title: 'T_EMC_RUN.ASSISTEQUIPTID',
            category: 'equipt',
            type: 'choose',
            onBeforeChoose: function (index) {
                return {
                    //equiptcategoryname_SEQ : '实验',
                    targetIds: this.getData()[index].assistEquiptId,
                    orgId_SEQ: workspace.user.ext$.parentorgid,
                    testMethodIds: this.getData()[index].testMethodId,
                    targetType: 'auxiliaryEquipt'
                }
            },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    var assistEquiptId = [];
                    var assistEquiptName = [];
                    var assistEquiptModel = [];
                    var assistEquiptNo = [];
                    Gikam.each(rows, function (index, item) {
                        assistEquiptId.push(item.id);
                        assistEquiptName.push(item.equiptName);
                        assistEquiptModel.push(item.equiptName + " / " + item.specification);
                        assistEquiptNo.push(item.equiptId);
                    });
                    this.setData({
                        id: Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        assistEquiptId: assistEquiptId.join(','),
                        assistEquiptName: assistEquiptName.join(','),
                        assistEquiptModel: assistEquiptModel.join(),
                        assistEquiptNo: assistEquiptNo.join()
                    });
                }
            },
            onClean: function () {
                Gikam.getComp('emc-run-edit-list-test-run-grid').setData({
                    id: Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                    assistEquiptId: '',
                    assistEquiptName: '',
                    assistEquiptModel: '',
                    assistEquiptNo: ''
                });
            }
        }, {
            field: 'temperature',
            title: 'T_EMC_RUN.TEMPERATURE',
            width: 100,
            validators: ['strLength[0,30]', 'notEmpty']
        }, {
            field: 'humidity',
            title: 'T_EMC_RUN.HUMIDITY',
            width: 100,
            validators: ['strLength[0,30]', 'notEmpty']
        }, {
            field: 'receiveDate',
            title: 'T_EMC_RUN.RECEIVEDATE',
        }, {
            field: 'receiveTime',
            title: 'T_EMC_RUN.RECEIVETIME',
            type: 'time'
        }, {
            field: 'handleDate',
            title: 'T_EMC_RUN.HANDLEDATE',
            type: 'date'
        }, {
            field: 'handleMethod',
            title: 'T_EMC_RUN.HANDLEMETHOD',
            width: 100,
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'handleMethod'
            },

        }, {
            field: 'beforeHandle',
            title: 'T_EMC_RUN.BEFOREHANDLE',
            type: 'choose',
            category: 'beforeHandle',
            single: true,
            onBeforeChoose: function () {
                return {
                    testMethodId_IN: this.getActivedRow().testMethodId
                }
            },
            onAfterChoose: function (rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index: index,
                        beforeHandleId: rows[0].id,
                        beforeHandle: rows[0].beforeHandleName
                    });
                }
            },
            onClean: function () {
                this.setData({
                    id: this.getActivedRow().id,
                    beforeHandleId: '',
                    beforeHandle: ''
                });
            },
            width: 100,
        }, {
            field: 'reportConfigLineId',
            title: 'T_EMC_RUN.REPORTCONFIGLINEID',
            type: 'select',
            search: true,
            category: 'reportConfigLine',
            param: {
                reportConfigId: 'analReport',
                filter_userId: workspace.user.id,
            },
            validators: ['notEmpty']
        }, {
            field: 'ext$.sol1name',
            title: 'T_EMC_RUN.SOL1ID',
        }
            // ,{
            //     field : 'ext$.sol1no',
            //     title : 'T_EMC_RUN.SOL1NO',
            // }, {
            //     field : 'ext$.sol2name',
            //     title : 'T_EMC_RUN.SOL2ID',
            // },{
            //     field : 'ext$.sol2no',
            //     title : 'T_EMC_RUN.SOL2NO',
            // }
            , {
                field: 'ext$.mulregentname',
                title: 'T_EMC_RUN.REAGENTID',
            }
            // , {
            //     field : 'relatedRunId',
            //     title : 'T_EMC_RUN.RELATEDRUNID',
            // }
            , {
                field: 'remark',
                title: 'T_EMC_RUN.REMARK',
                type: 'richText'
            }, {
                field: 'stNo',
                title: 'T_EMC_RUN.STNO',
            }, {
                field: 'stConcent',
                title: 'T_EMC_RUN.STCONCENT',
            }, {
                field: 'stValiDate',
                title: 'T_EMC_RUN.STVALIDATE',
                type: 'date'
            }
            // , {
            //     field : 'ext$.weighingstatus',
            //     title : 'T_EMC_RUN.WEIGHINGSTATUS',
            //     type : 'select',
            //     category : 'sys',
            //     param : {
            //         codeCategoryId : 'completeFlag'
            //     },
            // }, {
            //     field : 'ext$.pretreatstatus',
            //     title : 'T_EMC_RUN.PRETREATSTATUS',
            //     type : 'select',
            //     category : 'sys',
            //     param : {
            //         codeCategoryId : 'completeFlag'
            //     },
            // }
            , {
                field: 'createdTime',
                title: 'T_EMC_RUN.CREATEDTIME',
            }, {
                field: 'ext$.adddate',
                title: 'T_EMC_RUN.ADDDATE',
                type: 'date',
            }, {
                field: 'ext$.isshowstdsolution',
                title: 'T_EMC_RUN.ISSHOWSTDSOLUTION',
                type: 'select',
                search: true,
                category: 'sys',
                param: {
                    codeCategoryId: 'yesorno',
                },
            }];
    },

    getTestRunFields: function () {
        return [{
            field: 'id',
            title: 'T_EMC_RUN.RUNID'
        }, {
            field: 'orderCategory',
            title: 'T_EMC_RUN.ORDERCATEGORY',
        }, {
            field: 'testName',
            title: 'T_EMC_RUN.TESTNAME',
        }, {
            field: 'analysisUserName',
            title: 'T_EMC_RUN.ANALYSISUSERNAME',
            category: 'emp',
            type: 'choose',
            single: true,
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-run-input-detail-form').setData({
                        analysisUserName: rows[0].userName,
                        analysisUserId: rows[0].id
                    });
                }
            },
        }, {
            field: 'partakeUserName',
            title: 'T_EMC_RUN.PARTAKEUSERNAME',
            category: 'emp',
            type: 'choose',
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    var partakeUserNames = [];
                    var partakeUserIds = [];
                    Gikam.each(rows, function (index, item) {
                        partakeUserNames.push(item.userName);
                        partakeUserIds.push(item.id);
                    });
                    this.setData({
                        partakeUserName: partakeUserNames.join(','),
                        partakeUserId: partakeUserIds.join(',')
                    });
                }
            },
        }, {
            field: 'checkUserName',
            title: 'T_EMC_RUN.CHECKUSERNAME',
            category: 'emp',
            type: 'choose',
            single: true,
            validators: ['notEmpty'],
            onBeforeChoose: function () {
                var activedRow = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow();
                return {
                    parentOrgId_SEQ : workspace.user.ext$.parentorgid,
                    id_DIFFER: activedRow.analysisUserId
                }
            },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-run-input-detail-form').setData({
                        checkUserId: rows[0].id,
                        checkUserName: rows[0].userName
                    });
                }
            },
        }, {
            field: 'ext$.equiptcode',
            title: 'T_EMC_RUN.EQUIPTID',
        }, {
            field: 'analysisBeginDate',
            title: 'T_EMC_RUN.ANALYSISBEGINDATE',
            type: 'date',
            validators: ['notEmpty'],
        }, {
            field: 'analysisBeginTime',
            title: 'T_EMC_RUN.ANALYSISBEGINTIME',
            type: 'time',
        }, {
            field: 'analysisEndDate',
            title: 'T_EMC_RUN.ANALYSISENDDATE',
            type: 'date',
            validators: ['notEmpty'],
            onChange: function (field, value, rowIndex, oldValue, text) {
                var data = this.getData();
                var startDate = new Date(data.analysisBeginDate);
                var endDate = new Date(data.analysisEndDate);
                if (startDate && endDate && startDate > endDate) {
                    Gikam.alert("分析结束日期不能小于开始日期");
                    return;
                }
            }
        }, {
            field: 'analysisEndTime',
            title: 'T_EMC_RUN.ANALYSISENDTIME',
            type: 'time'
        }, {
            field: 'assistEquiptName',
            title: 'T_EMC_RUN.ASSISTEQUIPTID',
            category: 'equipt',
            type: 'choose',
            onBeforeChoose: function (index) {
                return {
                    //equiptcategoryname_SEQ : '实验',
                    targetIds: this.getData().assistEquiptId,
                    orgId_SEQ: workspace.user.ext$.parentorgid,
                    testMethodIds: this.getData().testMethodId,
                    targetType: 'auxiliaryEquipt',
                    id_differ: this.getData().equiptId,
                    single: true,
                }
            },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    var assistEquiptId = [];
                    var assistEquiptName = [];
                    var assistEquiptModel = [];
                    var assistEquiptNo = [];
                    Gikam.each(rows, function (index, item) {
                        assistEquiptId.push(item.id);
                        assistEquiptName.push(item.equiptName);
                        assistEquiptModel.push(item.equiptName + " / " + item.specification);
                        assistEquiptNo.push(item.equiptId);
                    });
                    this.setData({
                        id: Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        assistEquiptId: assistEquiptId.join(','),
                        assistEquiptName: assistEquiptName.join(','),
                        assistEquiptModel: assistEquiptModel.join(),
                        assistEquiptNo: assistEquiptNo.join()
                    });
                }
            },
            onClean: function () {
                this.setData({
                    assistEquiptId: '',
                    assistEquiptName: '',
                    assistEquiptModel: '',
                    assistEquiptNo: ''
                });
            }
        }, {
            field: 'ext$.roomno',
            title: 'T_EMC_RUN.ROOMNO',
            editor: true,
            type: 'choose',
            category: 'envRecord',
            single: true,
            onBeforeChoose: function () {
                return {
                    monitorDate_SEQ: this.getData().analysisBeginDate
                }
            },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        ext$: {
                            roomno: rows[0].ext$.roomno
                        },
                        temperature: rows[0].temperature,
                        humidity: rows[0].humidity
                    });
                }
            },
            onClean: function () {
                this.setData({
                    ext$: {
                        roomno: ''
                    },
                    temperature: '',
                    humidity: ''
                });
            }
        }, {
            field: 'temperature',
            title: 'T_EMC_RUN.TEMPERATURE',
            validators: ['strLength[0, 30]', 'notEmpty'],
        }, {
            field: 'humidity',
            title: 'T_EMC_RUN.HUMIDITY',
            validators: ['strLength[0, 30]', 'notEmpty']
        }, {
            field: 'receiveDate',
            title: 'T_EMC_RUN.RECEIVEDATE',
        }, {
            field: 'receiveTime',
            title: 'T_EMC_RUN.RECEIVETIME',
            type: 'time'
        }, {
            field: 'handleDate',
            title: 'T_EMC_RUN.HANDLEDATE',
            type: 'date'
        }, {
            field: 'handleMethod',
            title: 'T_EMC_RUN.HANDLEMETHOD',
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'handleMethod'
            },
        }, {
            field: 'remark',
            title: '备注',
            type: 'textarea',
            colspan: 3,
        }, {
            field: 'beforeHandle',
            title: 'T_EMC_RUN.BEFOREHANDLE',
            type: 'textarea',
            colspan: 3,
        }];
    },

    getSampleTestColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'qcName',
            title: 'T_EMC_ORDER_TASK.QCNAME'
        }, {
            field: 'cupNo',
            title: 'T_EMC_ORDER_TASK.CUPNO',
            width: 70,
            validators: ['number', 'notEmpty']
        }, {
            field: 'ext$.projno',
            title: 'T_EMC_ORDER_TASK.PROJNO',
            width: 120,
        }, {
            field: 'ext$.orderno',
            title: 'T_EMC_ORDER_TASK.ORDERID',
            width: 120,
        }, {
            field: 'resultInput',
            title: 'T_EMC_RUN.RESULTINPUT',
            formatter: function (i, v, row) {
                return Gikam.printf('<a href="javascript:;" onclick="emcRun.editPage.loadResultInputPage(\'{runId}\', \'{index}\')">{name}</a>', {
                    runId: row.runId,
                    index: i,
                    name: '结果录入',
                });
            }
        }, {
            field: 'ext$.monitorpointname',
            title: 'T_EMC_ORDER_TASK.MONITORPOINTNAME',
            width: 100,
        }, {
            field: 'ext$.labelno',
            title: 'T_EMC_ORDER.LABELNO',
            width: 100,
        }, {
            field: 'ext$.orderdesc',
            title: 'T_EMC_ORDER_TASK.ORDERDESC',
        }, {
            field: 'ext$.samplebegindate',
            title: 'T_EMC_ORDER_TASK.SAMPLEBEGINDATE',
            formatter: function (i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        }, {
            field: 'ext$.samplebegintime',
            title: 'T_EMC_ORDER_TASK.SAMPLEBEGINTIME',
            type: 'time',
            width: 100,
        }, {
            field: 'ext$.accepteddate',
            title: 'T_EMC_ORDER_TASK.RECEIVEDATE',
            formatter: function (i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        }, {
            field: 'ext$.labelno',
            title: 'T_EMC_ORDER_TASK.LABELNO',
        }, {
            field: 'equiptAcquNo',
            title: 'T_EMC_ORDER_TASK.EQUIPTACQUNO',
            editor: true,
            validators: ['strLength[0, 120]']
        }, {
            field: 'ext$.parentorderno',
            title: 'T_EMC_ORDER_TASK.PARENTORDERNO',
            width: 80,
            formatter: function (i, v, row) {
                if (row.qcCategoryCode === 'P-DUP') {
                    return '*****';
                }
                return v;
            }
        }, {
            field: 'stockBatchNo',
            title: 'T_EMC_ORDER_TASK.STOCKBATCHNO',
        }, {
            field: 'ext$.relatedorderno',
            title: 'T_EMC_ORDER.RELATEDORDERNO',
            filter: false,
            formatter: function (i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="emcRun.editPage.viewRelatedOrder(\'{orderId}\', \'{runId}\')">查看</a>', {
                    orderId: r.orderId,
                    runId: r.runId,
                });
            }
        }, {
            field: 'ext$.relatedqcorderno',
            title: 'T_EMC_ORDER.RELATEDQCORDERNO',
            filter: false,
            formatter: function (i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="emcRun.editPage.viewRelatedQcOrder(\'{orderId}\')">查看</a>', {
                    orderId: r.orderId
                });
            }
        }, {
            field: 'ext$.originalorder',
            title: 'T_EMC_ORDER.ORIGINALORDER',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno'
            }
        }, {
            field: 'ext$.isconvert',
            title: 'T_EMC_ORDER.ISCONVERT',
            editor: false,
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno',
            },
        }];
    },

    getSampleTestSecondColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'qcName',
            title: 'T_EMC_ORDER_TASK.QCNAME'
        }, {
            field: 'cupNo',
            title: 'T_EMC_ORDER_TASK.CUPNO',
            width: 70,
            validators: ['number', 'notEmpty']
        }, {
            field: 'ext$.projno',
            title: 'T_EMC_PROJECT.PROJNO'
        }, {
            field: 'ext$.orderno',
            title: 'T_EMC_ORDER_TASK.ORDERID',
            width: 120,
        }, {
            field: 'ext$.monitorpointname',
            title: 'T_EMC_ORDER_TASK.MONITORPOINTNAME',
            width: 100,
        }, {
            field: 'ext$.clientordername',
            title: 'T_EMC_ORDER.CLIENTORDERNAME',
            width: 120,
        }, {
            field: 'ext$.concattestname',
            title: 'T_EMC_ORDER_TASK.TESTNAME',
        }, {
            field: 'ext$.orderdesc',
            title: 'T_EMC_ORDER_TASK.ORDERDESC',
        }, {
            field: 'ext$.sampledate',
            title: 'T_EMC_ORDER_TASK.SAMPLEBEGINDATE',
            formatter: function (i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        }, {
            field: 'ext$.sampletime',
            title: 'T_EMC_ORDER_TASK.SAMPLEBEGINTIME',
            type: 'time',
        }, {
            field: 'sampleReceiverDate',
            title: 'T_EMC_ORDER_CONTAINER.SAMPLERECEIVERDATE',
            formatter: function (i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        }, {
            field: 'sampleReceiverTime',
            title: 'T_EMC_ORDER_CONTAINER.SAMPLERECEIVERTIME',
            type: 'time',
        }, {
            field: 'ext$.cycle',
            title: '周期',
        }, {
            field: 'ext$.frequency',
            title: '时段',
        }, {
            field: 'ext$.ordercontainerno',
            title: 'T_EMC_ORDER_TASK.LABELNO',
        }, {
            field: 'equiptAcquNo',
            title: 'T_EMC_ORDER_TASK.EQUIPTACQUNO',
            editor: true,
            validators: ['strLength[0, 120]']
        }, {
            field: 'ext$.parentorderno',
            title: 'T_EMC_ORDER_TASK.PARENTORDERNO',
            width: 80,
            formatter: function (i, v, row) {
                if (row.qcCategoryCode === 'P-DUP') {
                    return '*****';
                }
                return v;
            }
        }, {
            field: 'stockBatchNo',
            title: 'T_EMC_ORDER_TASK.STOCKBATCHNO',
            editable: true,
            type: 'choose',
            category: 'stockBatchNo',
            single: true,
            onBeforeChoose: function (index) {
                var row = Gikam.getComp('emc-run-sample-test-grid').getActivedRow();
                return {
                    orgId_SEQ: workspace.user.ext$.parentorgid,
                    runId: row.runId,
                    isReceive: '1',
                    managementType_SEQ: 'stdMaterials',
//                    expireDate_DGOE : Gikam.isEmpty(row.analysisDate) ? Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd') : row.analysisDate
                }
            },
            onAfterChoose: function (rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                this.setData({
                    id: this.getActivedRow().id,
                    stockBatchNo: rows[0].batchNo,
                });

            },
            onClean: function () {
                this.setData({
                    id: this.getActivedRow().id,
                    stockBatchNo: '',
                });
            },
            refreshGrid: false,
            editor: true
        }, {
            field: 'ext$.relatedorderno',
            title: 'T_EMC_ORDER.RELATEDORDERNO',
        }];
    },

    getResultPublicColumns: function () {
        return [{
            checkbox: true,
            fixed: true
        }, {
            index: true,
            fixed: true
        }, {
            field: 'ext$.testname',
            title: 'T_EMC_ORDER_TASK_RESULT.TESTNAME',
            sort: false,
            fixed: true
        }, {
            field: 'repeatNo',
            title: 'T_EMC_ORDER_TASK_RESULT.REPEATNO',
            sort: false,
            fixed: true
        }, {
            field: 'itemAliasName',
            title: 'T_EMC_ORDER_TASK_RESULT.ITEMNAME',
            sort: false,
            fixed: true
        }, {
            field: 'originalResult',
            title: 'T_EMC_ORDER_TASK_RESULT.ORIGINALRESULT',
            editor: true,
            sort: false,
            textStyleFormatter: function () {
                return {
                    backgroundColor: 'palegreen'
                }
            },
            onBeforeEditorRender: function (row) {
                if (row.scriptFlag == '1') {
                    return false;
                }
                return true;
            },
            onFocus: function (field, value, index) {
                var _this = this;
                var data = _this.getData()[index];
                if ('select' === data.resultCategory) {
                    Gikam.create('modal', {
                        width: 400,
                        height: 300,
                        title: Gikam.choose.getConfig('itemResult').title,
                        url: Gikam.choose.getConfig('itemResult').url + Gikam.param({
                            single: true,
                            itemId_SEQ: data.itemId,
                        }),
                        onAfterClose: function (rows) {
                            if (Gikam.isEmpty(rows)) {
                                return;
                            }
                            Gikam.getComp('emc-run-result-grid').setData({
                                index: index,
                                originalResult: rows[0].result,
                                reportResult: rows[0].result,
                            });
                        }
                    });
                }
            },
            onChange: function (field, value, index) {
                var data = this.getData()[index];
                if ('number' === data.resultCategory) {
                    var forbidden = false;
                    if (Gikam.isNotEmpty(value)) {
                        var regF = /^(-?\d+)(\.\d+)?$/;
                        if (value.indexOf('0') == 0 && value.length > 1 && value.indexOf('0.') == -1) {
                            forbidden = true;
                        }
                        if ((value.indexOf('-0') == 0 && value.length == 2) || (value.indexOf('-0.') == 0 && value.length == 3)) {
                            forbidden = true;
                        }
                        if (!regF.test(value)) {
                            forbidden = true;
                        }

                        if (forbidden) {
                            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.FORMAT_ERROR');
                            this.setData({
                                index: index,
                                originalResult: ''
                            });
                        }

                    }

                }
            }
        }, {
            field: 'reportResult',
            title: 'T_EMC_ORDER_TASK_RESULT.REPORTRESULT',
            sort: false,
            formatter: function (i, v, r) {
                return Gikam.scientificNotation(v);
            }
        }, {
            field: 'convertFactor',
            title: 'T_EMC_ORDER_TASK_RESULT.CONVERTFACTOR',
            sort: false,
            editor: true
        }, {
            field: 'resultRule',
            title: 'T_EMC_ORDER_TASK_RESULT.RESULTRULE',
            editor: true,
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'resultRule'
            },
        }, {
            field: 'lowLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.LOWLIMIT',
            editor: true,
            validators: ['number'],
            sort: false
        }, {
            field: 'revisionRule',
            title: 'T_EMC_ORDER_TASK_RESULT.REVISIONRULE',
            editor: true,
            type: 'choose',
            category: 'rounding-rule',
            single: true,
            onAfterChoose: function (back, index) {
                if (back) {
                    this.setData({
                        index: index,
                        'revisionRule': back
                    });
                }
            },
            sort: false
        }, {
            field: 'unitName',
            title: 'T_EMC_ORDER_TASK_RESULT.UNITNAME',
            editor: true,
            type: 'choose',
            category: 'unit',
            single: true,
            onAfterChoose: function (rows, index) {
                if (rows) {
                    this.setData({
                        index: index,
                        unitName: rows[0].unitName
                    })
                }
            },
            sort: false
        }, {
            field: 'highLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.HIGHLIMIT',
            editor: true,
            validators: ['number'],
            sort: false
        }, {
            field: 'indexLowLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.INDEXLOWLIMIT',
            sort: false
        }, {
            field: 'indexHighLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.INDEXHIGHLIMIT',
            sort: false
        }, {
            field: 'resultStatus',
            title: 'T_EMC_ORDER_TASK_RESULT.RESULTSTATUS',
            sort: false,
            styleFormatter: function (row) {
                if (row.resultStatus === 'OOS') {
                    return {
                        backgroundColor: 'red'
                    }
                }
            }
        },{
            field : 'reportDisplay',
            title : 'T_EMC_ORDER_TASK_RESULT.REPORTDISPLAY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            field: 'ext$.methodname',
            title: 'T_EMC_ORDER_TASK_RESULT.METHODNAME',
            sort: false
        }, {
            field: 'ext$.testmethodbasis',
            title: 'T_EMC_ORDER_TASK_RESULT.TESTMETHODBASIS',
            sort: false
        }]
    },

    getReadOnlyResultColumns: function () {
        return [{
            checkbox: true,
            fixed: true
        }, {
            index: true,
            fixed: true
        }, {
            field: 'ext$.testname',
            title: 'T_EMC_ORDER_TASK_RESULT.TESTNAME',
            sort: false,
            fixed: true
        }, {
            field: 'repeatNo',
            title: 'T_EMC_ORDER_TASK_RESULT.REPEATNO',
            sort: false,
            fixed: true
        }, {
            field: 'itemAliasName',
            title: 'T_EMC_ORDER_TASK_RESULT.ITEMNAME',
            sort: false,
            fixed: true
        }, {
            field: 'originalResult',
            title: 'T_EMC_ORDER_TASK_RESULT.ORIGINALRESULT',
            sort: false,
            textStyleFormatter: function () {
                return {
                    backgroundColor: 'palegreen'
                }
            },
            onBeforeEditorRender: function (row) {
                if (row.scriptFlag == '1') {
                    return false;
                }
                return true;
            },
            onFocus: function (field, value, index) {
                var _this = this;
                var data = _this.getData()[index];
                if ('select' === data.resultCategory) {
                    Gikam.create('modal', {
                        width: 400,
                        height: 300,
                        title: Gikam.choose.getConfig('itemResult').title,
                        url: Gikam.choose.getConfig('itemResult').url + Gikam.param({
                            single: true,
                            itemId_SEQ: data.itemId,
                        }),
                        onAfterClose: function (rows) {
                            if (Gikam.isEmpty(rows)) {
                                return;
                            }
                            Gikam.getComp('emc-run-result-grid').setData({
                                index: index,
                                originalResult: rows[0].result,
                                reportResult: rows[0].result,
                            });
                        }
                    });
                }
            },
            onChange: function (field, value, index) {
                var data = this.getData()[index];
                if ('number' === data.resultCategory) {
                    var forbidden = false;
                    if (Gikam.isNotEmpty(value)) {
                        var regF = /^(-?\d+)(\.\d+)?$/;
                        if (value.indexOf('0') == 0 && value.length > 1 && value.indexOf('0.') == -1) {
                            forbidden = true;
                        }
                        if ((value.indexOf('-0') == 0 && value.length == 2) || (value.indexOf('-0.') == 0 && value.length == 3)) {
                            forbidden = true;
                        }
                        if (!regF.test(value)) {
                            forbidden = true;
                        }

                        if (forbidden) {
                            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.FORMAT_ERROR');
                            this.setData({
                                index: index,
                                originalResult: ''
                            });
                        }

                    }

                }
            }
        }, {
            field: 'reportResult',
            title: 'T_EMC_ORDER_TASK_RESULT.REPORTRESULT',
            sort: false,
            formatter: function (i, v, r) {
                return Gikam.scientificNotation(v);
            }
        }, {
            field: 'convertFactor',
            title: 'T_EMC_ORDER_TASK_RESULT.CONVERTFACTOR',
            sort: false,
        }, {
            field: 'resultRule',
            title: 'T_EMC_ORDER_TASK_RESULT.RESULTRULE',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'resultRule'
            },
        }, {
            field: 'lowLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.LOWLIMIT',
            validators: ['number'],
            sort: false
        }, {
            field: 'revisionRule',
            title: 'T_EMC_ORDER_TASK_RESULT.REVISIONRULE',
            type: 'choose',
            category: 'rounding-rule',
            single: true,
            onAfterChoose: function (back, index) {
                if (back) {
                    this.setData({
                        index: index,
                        'revisionRule': back
                    });
                }
            },
            sort: false
        }, {
            field: 'unitName',
            title: 'T_EMC_ORDER_TASK_RESULT.UNITNAME',
            type: 'choose',
            category: 'unit',
            single: true,
            onAfterChoose: function (rows, index) {
                if (rows) {
                    this.setData({
                        index: index,
                        unitName: rows[0].unitName
                    })
                }
            },
            sort: false
        }, {
            field: 'highLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.HIGHLIMIT',
            validators: ['number'],
            sort: false
        }, {
            field: 'indexLowLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.INDEXLOWLIMIT',
            sort: false
        }, {
            field: 'indexHighLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.INDEXHIGHLIMIT',
            sort: false
        }, {
            field: 'resultStatus',
            title: 'T_EMC_ORDER_TASK_RESULT.RESULTSTATUS',
            sort: false,
            styleFormatter: function (row) {
                if (row.resultStatus === 'OOS') {
                    return {
                        backgroundColor: 'red'
                    }
                }
            }
        },{
            field : 'reportDisplay',
            title : 'T_EMC_ORDER_TASK_RESULT.REPORTDISPLAY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            field: 'ext$.methodname',
            title: 'T_EMC_ORDER_TASK_RESULT.METHODNAME',
            sort: false
        }, {
            field: 'ext$.testmethodbasis',
            title: 'T_EMC_ORDER_TASK_RESULT.TESTMETHODBASIS',
            sort: false
        }]
    },


    getResultColumns: function () {
        return [{
            checkbox: true,
            fixed: true
        }, {
            index: true,
            fixed: true
        }, {
            field: 'ext$.testname',
            title: 'T_EMC_ORDER_TASK_RESULT.TESTNAME',
            sort: false,
            fixed: true
        }, {
            field: 'repeatNo',
            title: 'T_EMC_ORDER_TASK_RESULT.REPEATNO',
            width: 80,
            sort: false,
            fixed: true
        }, {
            field: 'itemName',
            title: 'T_EMC_ORDER_TASK_RESULT.ITEMNAME',
            sort: false,
            fixed: true
        }, {
            field: 'itemAliasName',
            title : 'T_EMC_ORDER_TASK_RESULT.ITEMALIASNAME',
            sort: false,
            fixed: true
        }, {
            field: 'originalResult',
            title: 'T_EMC_ORDER_TASK_RESULT.ORIGINALRESULT',
            editor: true,
            sort: false,
            textStyleFormatter: function () {
                return {
                    backgroundColor: 'palegreen'
                }
            },
            onBeforeEditorRender: function (row) {
                if (row.scriptFlag == '1') {
                    return false;
                }
                return true;
            },
            onFocus: function (field, value, index) {
                var _this = this;
                var data = _this.getData()[index];
                if ('select' === data.resultCategory) {
                    Gikam.create('modal', {
                        width: 400,
                        height: 300,
                        title: Gikam.choose.getConfig('itemResult').title,
                        url: Gikam.choose.getConfig('itemResult').url + Gikam.param({
                            single: true,
                            itemId_SEQ: data.itemId,
                        }),
                        onAfterClose: function (rows) {
                            if (Gikam.isEmpty(rows)) {
                                return;
                            }
                            Gikam.getComp('emc-run-result-grid').setData({
                                index: index,
                                originalResult: rows[0].result,
                                reportResult: rows[0].result,
                            });
                        }
                    });
                }
            },
            onChange: function (field, value, index) {
                var data = this.getData()[index];
                if ('number' === data.resultCategory) {
                    var forbidden = false;
                    if (Gikam.isNotEmpty(value)) {
                        var regF = /^(-?\d+)(\.\d+)?$/;
                        if (value.indexOf('0') == 0 && value.length > 1 && value.indexOf('0.') == -1) {
                            forbidden = true;
                        }
                        if ((value.indexOf('-0') == 0 && value.length == 2) || (value.indexOf('-0.') == 0 && value.length == 3)) {
                            forbidden = true;
                        }
                        if (!regF.test(value)) {
                            forbidden = true;
                        }

                        if (forbidden) {
                            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.FORMAT_ERROR');
                            this.setData({
                                index: index,
                                originalResult: ''
                            });
                        }

                    }

                }
            },
            width: 130
        }, {
            field: 'reportResult',
            title: 'T_EMC_ORDER_TASK_RESULT.REPORTRESULT',
            width: 130,
            sort: false,
            formatter: function (i, v, r) {
                return Gikam.scientificNotation(v);
            }
        }, {
            field: 'revisionRule',
            title: 'T_EMC_ORDER_TASK_RESULT.REVISIONRULE',
            editor: true,
            type: 'choose',
            category: 'rounding-rule',
            single: true,
            onAfterChoose: function (back, index) {
                if (back) {
                    this.setData({
                        index: index,
                        'revisionRule': back
                    });
                }
            },
            sort: false
        }, {
            field: 'unitName',
            title: 'T_EMC_ORDER_TASK_RESULT.UNITNAME',
            editor: true,
            type: 'choose',
            category: 'unit',
            single: true,
            onAfterChoose: function (rows, index) {
                if (rows) {
                    this.setData({
                        index: index,
                        unitName: rows[0].unitName
                    })
                }
            },
            sort: false
        }, {
            field: 'highLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.HIGHLIMIT',
            editor: true,
            validators: ['number'],
            width: 80,
            sort: false
        }, {
            field: 'lowLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.LOWLIMIT',
            editor: true,
            validators: ['number'],
            width: 80,
            sort: false
        }, {
            field: 'indexLowLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.INDEXLOWLIMIT',
            width: 80,
            sort: false
        }, {
            field: 'indexHighLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.INDEXHIGHLIMIT',
            width: 80,
            sort: false
        }, {
            field: 'resultStatus',
            title: 'T_EMC_ORDER_TASK_RESULT.RESULTSTATUS',
            width: 100,
            sort: false
        }, {
            field : 'reportDisplay',
            title : 'T_EMC_ORDER_TASK_RESULT.REPORTDISPLAY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            field: 'relativeDeviation',
            title: 'T_EMC_ORDER_TASK_RESULT.RELATIVEDEVIATION',
            sort: false
        }, {
            field: 'average ',
            title: 'T_EMC_ORDER_TASK_RESULT.AVERAGE',
            sort: false
        }, {
            field: 'ext$.methodname',
            title: 'T_EMC_ORDER_TASK_RESULT.METHODNAME',
            sort: false
        }, {
            field: 'ext$.testmethodbasis',
            title: 'T_EMC_ORDER_TASK_RESULT.TESTMETHODBASIS',
            sort: false
        }];
    },

    getAddRunListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'ext$.orderno',
            title: 'T_EMC_ORDER_TASK.ORDERNO'
        }, {
            field: 'ext$.projno',
            title: 'T_EMC_ORDER_TASK.PROJNO',
        }, {
            field: 'ext$.compledate',
            title: '完成期限',
            type: 'date'
        }, {
            field: 'ext$.projname',
            title: 'T_EMC_ORDER_TASK.PROJNAME',
        }, {
            field: 'orderCategory',
            title: 'T_EMC_ORDER_TASK.ORDERCATEGORY',
        }, {
            field: 'ext$.monitorpointname',
            title: 'T_EMC_ORDER_TASK.MONITORPOINTNAME',
        }, {
            field: 'qcCategoryCode',
            title: 'T_EMC_ORDER_TASK.QCCATEGORYCODE',
        }, {
            field: 'testName',
            title: 'T_EMC_ORDER_TASK.TESTNAME',
        }, {
            field: 'itemGroup',
            title: 'T_EMC_ORDER_TASK.ITEMGROUP',
        }, {
            field: 'ext$.samplebegindate',
            title: 'T_EMC_ORDER_TASK.SAMPLEBEGINDATE',
            formatter: function (i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        }, {
            field: 'ext$.accepteddate',
            title: 'T_EMC_ORDER_TASK.RECEIVEDATE',
            formatter: function (i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        }, {
            field: 'ext$.acceptedtime',
            title: 'T_EMC_SAMPLE.ACCEPTEDTIME',
        }, {
            field: 'ext$.labelno',
            title: 'T_EMC_ORDER_TASK.LABELNO',
        }, {
            field: 'ext$.phflag',
            title: 'T_EMC_ORDER.PHFLAG',
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno',
            },
        }];
    },

    getAddRunOrderListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'orderNo',
            title: 'T_EMC_ORDER_TASK.ORDERNO'
        }, {
            field: 'ext$.projno',
            title: 'T_EMC_ORDER_TASK.PROJNO',
        }, {
            field: 'ext$.projname',
            title: 'T_EMC_ORDER_TASK.PROJNAME',
        }, {
            field: 'orderCategory',
            title: 'T_EMC_ORDER_TASK.ORDERCATEGORY',
        }, {
            field: 'ext$.monitorpointname',
            title: 'T_EMC_ORDER_TASK.MONITORPOINTNAME',
        }, {
            field: 'sampleBeginDate',
            title: 'T_EMC_ORDER_TASK.SAMPLEBEGINDATE',
            formatter: function (i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        }, {
            field: 'labelNo',
            title: 'T_EMC_ORDER_TASK.LABELNO',
        }];
    },

    getEditItemListColumns: function () {

        return [{
            checkbox: true
        }, {
            field: 'ext$.testname',
            title: 'T_EMC_ORDER_TASK_RESULT.TESTNAME',
        }, {
            field: 'itemName',
            title: 'T_EMC_ORDER_TASK_RESULT.ITEMNAME',
        }, {
            field: 'itemAliasName',
            title: 'T_EMC_ORDER_TASK_RESULT.ITEMALIASNAME',
        }, {
            field: 'unitName',
            title: 'T_EMC_ORDER_TASK_RESULT.UNITNAME',
            type: 'choose',
            category: 'unit',
        }, {
            field: 'highLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.HIGHLIMIT',
        }, {
            field: 'lowLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.LOWLIMIT',
        }];

    },

    getHistoricalDataColumns: function () {
        return [{
            index: true
        }, {
            field: 'ext$.monitorpointname',
            title: 'T_EMC_ORDER_TASK.MONITORPOINTNAME',
            width: 100,
        }, {
            field: 'orderNo',
            title: 'T_EMC_ORDER_TASK.ORDERID',
            width: 120,
        }, {
            field: 'ext$.samplebegindate',
            title: 'T_EMC_ORDER_TASK.SAMPLEBEGINDATE',
            formatter: function (i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            },
            width: 100,
        }, {
            field: 'ext$.samplebegintime',
            title: 'T_EMC_ORDER_TASK.SAMPLEBEGINTIME',
            type: 'time',
            width: 100,
        }, {
            field: 'ext$.analysisdate',
            title: 'T_EMC_ORDER_TASK.ANALYSISDATE',
            type: 'date',
            formatter: function (i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            },
            width: 100,
        }, {
            field: 'itemName',
            title: 'T_EMC_ORDER_TASK_RESULT.ITEMNAME',
        }, {
            field: 'originalResult',
            title: 'T_EMC_ORDER_TASK_RESULT.ORIGINALRESULT'
        }, {
            field: 'reportResult',
            title: 'T_EMC_ORDER_TASK_RESULT.REPORTRESULT',
            formatter: function (i, v, r) {
                return Gikam.scientificNotation(v);
            }
        }];
    },

    getHistoricalDataFields: function () {
        return [{
            field: 'beginDate',
            title: 'T_EMC_ORDER_TASK_RESULT.BEGINDATE',
            type: 'date',
            editor: true,
            width: 400
        }, {
            field: 'endDate',
            title: 'T_EMC_ORDER_TASK_RESULT.ENDDATE',
            type: 'date',
            editor: true,
            width: 400
        }]
    },

    getEquiptChooseListColumns: function () {
        return [{
            radio: true
        }, {
            field: 'ext$.equiptrecorddate',
            title: '检定有效期',
            type: 'date'
        }, {
            field: 'equiptId',
            title: 'T_EMC_EQUIPT.EQUIPTID',
        }, {
            field: 'equiptName',
            title: 'T_EMC_EQUIPT.EQUIPTNAME',
        }, {
            field: 'specification',
            title: 'T_EMC_EQUIPT.SPECIFICATION',
        }, {
            field: 'factoryNo',
            title: 'T_EMC_EQUIPT.FACTORYNO',
        }, {
            field: 'status',
            title: 'T_EMC_EQUIPT.STATUS',
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'equiptStatus',
            },
        }, {
            field: 'equiptCategoryId',
            title: 'T_EMC_EQUIPT.EQUIPTCATEGORYID',
            type: 'select',
            search: true,
            category: 'equiptCategory',
            validators: ['number']
        }, {
            field: 'usedDepartmentId',
            title: 'T_EMC_EQUIPT.USEDDEPARTMENTID',
        }, {
            field: 'sharing',
            title: 'T_EMC_EQUIPT.SHARING',
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno',
            },
        }, {
            field: 'maintain',
            title: 'T_EMC_EQUIPT.MAINTAIN',
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno',
            },
        }, {
            field: 'sourceMethod',
            title: 'T_EMC_EQUIPT.SOURCEMETHOD',
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'sourceMethod',
            },
        }, {
            field: 'sourceDate',
            title: 'T_EMC_EQUIPT.SOURCEDATE',
        }];
    },

    getQcChooseListColumns: function () {
        return [{
            field: 'qcCategoryId',
            title: 'T_EMC_QC_CATEGORY.QCCATEGORYID'
        }, {
            field: 'qcName',
            title: 'T_EMC_QC_CATEGORY.QCNAME',
        }, {
            field: 'addNum',
            title: 'T_EMC_QC_CATEGORY.ADDNUM',
            type: 'number',
            editor: true,
            isInteger: true
            // }, {
            //     field : 'checkPoint',
            //     title : 'T_EMC_TEST_METHOD_DEFAULT_QC.CHECKPOINT',
            //     type : 'number',
            //     editor : true,
            //     isInteger : true
        }];
    },

    getStockNoColumns: function (param) {
        return [{
            field: 'stockBatchNo',
            title: 'T_EMC_ORDER_TASK.STOCKBATCHNO',
            type: 'choose',
            category: 'run',
            single: true,
            // onBeforeChoose : function(index) {
            //     return {
            //         orgId_SEQ: workspace.user.ext$.parentorgid,
            //         runId : param.runId,
            //         isReceive : '1',
            //         managementType_SEQ : 'stdMaterials',
            //         expireDate_DGOE : Gikam.isEmpty(param.analysisDate) ? Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd') : param.analysisDate
            //     }
            // },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        stockBatchNo: rows[0].id,
                    });
                }
            },
            onClean: function () {
                this.setData({
                    stockBatchNo: '',
                });
            },
            refreshGrid: false,
            editor: true
        }];
    },

    getUnCompleteField: function () {
        return [{
            title: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.UN_COMPLETE',
            field: 'showFlag',
            type: 'simpleCheckbox',
            refreshGrid: false,
            styleFormatter: function () {
                return {
                    paddingLeft: '0'
                }
            },
            onChange: function (field, value, index) {
                workspace.window.showMask(true);

                var testGrid = Gikam.getComp('emc-run-edit-list-sample-test-grid');
                var resultGrid = Gikam.getComp('emc-run-edit-list-result-grid');
                var showFlags = value;

                if (showFlags == '1') {
                    // 刷新样品测试
                    testGrid.refresh({
                        requestData: {
                            originalResults: 'empty'
                        }
                    }).done(function () {
                        workspace.window.closeMask();
                    });
                    // 刷新结果
                    resultGrid.refresh({
                        requestData: {
                            originalResults: 'empty'
                        }
                    }).done(function () {
                        workspace.window.closeMask();
                    });
                } else {
                    // 刷新样品测试
                    testGrid.refresh({
                        requestData: {
                            originalResults: ''
                        }
                    }).done(function () {
                        workspace.window.closeMask();
                    });
                    // 刷新结果
                    resultGrid.refresh({
                        requestData: {
                            originalResults: ''
                        }
                    }).done(function () {
                        workspace.window.closeMask();
                    });
                }
            },
        }]
    },

    getRunShowDupField: function () {
        return [{
            title: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.SHOW_DUP_SPKC',
            field: 'showFlag',
            type: 'simpleCheckbox',
            refreshGrid: false,
            styleFormatter: function () {
                return {
                    paddingLeft: '0'
                }
            },
            onChange: function (field, value, index) {
                workspace.window.showMask(true);

                var testGrid = Gikam.getComp('emc-run-edit-list-sample-test-grid');

                if (value === '1') {
                    // 刷新样品测试
                    testGrid.refresh({
                        requestData: {
                            parentFlag_SEQ: '1',
                            qcCategoryCode_IN: 'N/A,L-DUP,L-SPKC'
                        }
                    }).done(function () {
                        workspace.window.closeMask();
                    });
                } else {
                    // 刷新样品测试
                    testGrid.refresh({
                        requestData: {
                            parentFlag_SEQ: '',
                            qcCategoryCode_IN: ''
                        }
                    }).done(function () {
                        workspace.window.closeMask();
                    });
                }
            },
        }]
    },

    getUnCompleteSecondField: function () {
        return [{
            title: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.UN_COMPLETE',
            field: 'showFlag',
            type: 'simpleCheckbox',
            refreshGrid: false,
            styleFormatter: function () {
                return {
                    paddingLeft: '0'
                }
            },
            onChange: function (field, value, index) {
                workspace.window.showMask(true);

                var testGrid = Gikam.getComp('emc-run-sample-test-grid');
                var resultGrid = Gikam.getComp('emc-run-result-grid');
                var showFlags = value;

                if (showFlags == '1') {
                    // 刷新样品测试
                    testGrid.refresh({
                        requestData: {
                            originalResults: 'empty'
                        }
                    }).done(function () {
                        workspace.window.closeMask();
                    });
                    // 刷新结果
                    resultGrid.refresh({
                        requestData: {
                            originalResults: 'empty'
                        }
                    }).done(function () {
                        workspace.window.closeMask();
                    });
                } else {
                    // 刷新样品测试
                    testGrid.refresh({
                        requestData: {
                            originalResults: ''
                        }
                    }).done(function () {
                        workspace.window.closeMask();
                    });
                    // 刷新结果
                    resultGrid.refresh({
                        requestData: {
                            originalResults: ''
                        }
                    }).done(function () {
                        workspace.window.closeMask();
                    });
                }
            },
        }]
    },

    getRunShowAllField: function () {
        var flag = true;
        return [{
            title: 'EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.SHOW_ALL',
            field: 'showFlag',
            type: 'simpleCheckbox',
            refreshGrid: false,
            styleFormatter: function () {
                return {
                    paddingLeft: '0'
                }
            },
            onChange: function (field, value, index) {
                workspace.window.showMask(true);
                var resultRunAllGrid = Gikam.getComp('emc-run-edit-list-test-run-grid');
                var sampleTestGrid = Gikam.getComp('emc-run-edit-list-sample-test-grid');

                var showFlags = value;
                if (showFlags === '1') {
                    /*sampleTestGrid.setOptions({
                        requestData : {
                            analysisUserId_SC : ''
                        }
                    });*/

                    resultRunAllGrid.refresh({
                        requestData: {
                            showAllFlag: '',
                            analysisUserId_SEQ: '',
                            // analysisUserId : workspace.user.id,
                            // checkUserId : workspace.user.id,
                        }
                    }).done(function () {
                        workspace.window.closeMask();
                    });
                } else {
                    /*sampleTestGrid.setOptions({
                        requestData : {
                            analysisUserId_SC : workspace.user.id
                        }
                    });*/

                    resultRunAllGrid.refresh({
                        requestData: {
                            showAllFlag: '0',
                            analysisUserId_SEQ: workspace.user.id,
                        }
                    }).done(function () {
                        workspace.window.closeMask();
                    });
                }
            },
        }]
    },

    getMethodEditColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'ext$.projno',
            title: 'T_EMC_PROJECT.PROJNO',
            formatter: function (i, v, r) {
                if (r.urgent == 1) {
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + v;
                } else {
                    return v;
                }
            },
        }, {
            field: 'ext$.orderno',
            title: 'T_EMC_ORDER.ORDERNO'
        }, {
            field: 'orderCategory',
            title: 'T_EMC_ORDER_TASK.ORDERCATEGORY'
        }, {
            field: 'ext$.monitorpointname',
            title: 'T_EMC_ORDER_TASK.MONITORPOINTNAME'
        }, {
            field: 'ext$.testclass',
            title: 'T_EMC_TEST_METHOD.TESTCLASS',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'testClass',
            },
        }, {
            field: 'testName',
            title: 'T_EMC_ORDER_TASK.TESTNAME'
        }, {
            field: 'ext$.samplebegindate',
            title: 'T_EMC_ORDER_TASK.SAMPLEBEGINDATE',
            formatter: function (i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        }]
    },

    getSchemeMethodChooseListColumns: function () {
        return [{
            radio: true
        }, {
            field: 'schemeName',
            width: 140,
            title: 'T_EMC_MONITOR_SCHEME.SCHEMENAME'
        }, {
            field: 'ext$.testclass',
            title: 'T_EMC_TEST_METHOD.TESTCLASS',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'testClass',
            },
        }, {
            field: 'ext$.testmethodtype',
            title: 'T_EMC_TEST_METHOD_TESTMETHODTYPE',
            type: 'select',
            search: false,
            category: 'sys',
            param: {
                codeCategoryId: 'testMethodType',
            },
        }, {
            field: 'ext$.methodname',
            width: 160,
            title: 'T_EMC_MONITOR_SCHEME.METHODNAME'
        }, {
            field: 'ext$.testmethodbasis',
            width: 160,
            title: 'T_EMC_METHOD_VERSION.TESTMETHODBASIS'
        }
            // , {
            //     field : 'itemGroup',
            //     title : 'T_EMC_ITEM_GROUP.ITEMGROUP'
            // }
        ];
    },

    getBatchInputFormFields: function () {
        return [{
            field: 'partakeUserName',
            title: 'T_EMC_RUN.PARTAKEUSERNAME',
            editor: true,
            category: 'emp',
            type: 'choose',
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    var partakeUserNames = [];
                    var partakeUserIds = [];
                    Gikam.each(rows, function (index, item) {
                        partakeUserNames.push(item.userName);
                        partakeUserIds.push(item.id);
                    });
                    this.setData({
                        /*                        id : Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        */                        partakeUserName: partakeUserNames.join(','),
                        partakeUserId: partakeUserIds.join(',')
                    });
                }
            },
            onClean: function () {
                this.setData({
                    /*                    id : Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                    */                    partakeUserName: '',
                    partakeUserId: ''
                });
            }
        }, {
            field: 'checkUserName',
            title: 'T_EMC_RUN.CHECKUSERNAME',
            editor: true,
            category: 'emp',
            type: 'choose',
            validators: ['notEmpty'],
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    var checkUserNames = [];
                    var checkUserIds = [];
                    Gikam.each(rows, function (index, item) {
                        checkUserNames.push(item.userName);
                        checkUserIds.push(item.id);
                    });
                    this.setData({
                        /*                        id : Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        */                        checkUserName: checkUserNames.join(','),
                        checkUserId: checkUserIds.join(',')
                    });
                }
            },
            onClean: function () {
                Gikam.getComp('emc-run-edit-list-test-run-grid').setData({
                    /*                    id : Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                    */                    checkUserId: '',
                    checkUserName: ''
                });
            }
        }, {
            field: 'analysisBeginDate',
            title: 'T_EMC_RUN.ANALYSISBEGINDATE',
            editor: true,
            value: Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd'),
            type: 'date'
        }, {
            field: 'analysisBeginTime',
            title: 'T_EMC_RUN.ANALYSISBEGINTIME',
            editor: true,
            type: 'time'
        }, {
            field: 'analysisEndDate',
            title: 'T_EMC_RUN.ANALYSISENDDATE',
            editor: true,
            validators: ['notEmpty'],
            value: Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd'),
            type: 'date'
        }, {
            field: 'analysisEndTime',
            title: 'T_EMC_RUN.ANALYSISENDTIME',
            editor: true,
            type: 'time'
        }, {
            field: 'assistEquiptName',
            title: 'T_EMC_RUN.ASSISTEQUIPTID',
            editor: true,
            category: 'equipt',
            type: 'choose',
            onBeforeChoose: function () {
                return {
                    equiptcategoryname_SEQ: '实验'
                }
            },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    var assistEquiptId = [];
                    var assistEquiptName = [];
                    var assistEquiptModel = [];
                    var assistEquiptNo = [];
                    Gikam.each(rows, function (index, item) {
                        assistEquiptId.push(item.id);
                        assistEquiptName.push(item.equiptName);
                        assistEquiptModel.push(item.equiptName + " / " + item.specification);
                        assistEquiptNo.push(item.equiptId);
                    });
                    this.setData({
                        assistEquiptId: assistEquiptId.join(','),
                        assistEquiptName: assistEquiptName.join(','),
                        assistEquiptModel: assistEquiptModel.join(),
                        assistEquiptNo: assistEquiptNo.join()
                    });
                }
            },
            onClean: function () {
                this.setData({
                    assistEquiptId: '',
                    assistEquiptName: '',
                    assistEquiptModel: '',
                    assistEquiptNo: ''
                });
            }
        }, {
            field: 'ext$.roomno',
            title: 'T_EMC_RUN.ROOMNO',
            editor: true,
            type: 'choose',
            category: 'envRecord',
            single: true,
            onBeforeChoose: function () {
                return {
                    monitorDate_SEQ: this.getData().analysisBeginDate
                }
            },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        ext$: {
                            roomno: rows[0].ext$.roomno
                        },
                        temperature: rows[0].temperature,
                        humidity: rows[0].humidity
                    });
                }
            },
            onClean: function () {
                this.setData({
                    ext$: {
                        roomno: ''
                    },
                    temperature: '',
                    humidity: ''
                });
            }
        }, {
            field: 'temperature',
            title: 'T_EMC_RUN.TEMPERATURE',
            editor: true,
            validators: ['strLength[0,30]']
        }, {
            field: 'humidity',
            title: 'T_EMC_RUN.HUMIDITY',
            editor: true,
            validators: ['strLength[0,30]']
        }, {
            field: 'handleDate',
            title: 'T_EMC_RUN.HANDLEDATE',
            editor: true,
            type: 'date'
        }, {
            field: 'handleMethod',
            title: 'T_EMC_RUN.HANDLEMETHOD',
            editor: true,
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'handleMethod'
            },

        }, {
            field: 'beforeHandle',
            title: 'T_EMC_RUN.BEFOREHANDLE',
            editor: true,
        }];
    },

    getCopyBlankToChooseListColumns: function () {
        return [{
            field: 'id',
            title: 'T_EMC_RUN.RUNID',
            width: 120,
        }, {
            field: 'orderCategory',
            title: 'T_EMC_RUN.ORDERCATEGORY',
        }, {
            field: 'testName',
            title: 'T_EMC_RUN.TESTNAME',
        }, {
            field: 'analysisUserName',
            title: 'T_EMC_RUN.ANALYSISUSERNAME',
            width: 100,
            category: 'emp',
            type: 'choose',
        }, {
            field: 'partakeUserName',
            title: 'T_EMC_RUN.PARTAKEUSERNAME',
            width: 100,
            category: 'emp',
            type: 'choose',
        }, {
            field: 'checkUserName',
            title: 'T_EMC_RUN.CHECKUSERNAME',
            category: 'emp',
            type: 'choose',
            width: 100,
        }]
    },

    getCopyBlankSpkcToChooseListColumns: function () {
        return [{
            field: 'runId',
            title: 'T_EMC_RUN.RUNID',
            width: 120,
        }, {
            field: 'qcName',
            title: 'T_EMC_ORDER_TASK.QCNAME'
        }, {
            field: 'ext$.orderno',
            title: 'T_EMC_ORDER_TASK.ORDERID',
            width: 120,
        }, {
            field: 'ext$.samplebegindate',
            title: 'T_EMC_ORDER_TASK.SAMPLEBEGINDATE',
            formatter: function (i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        }, {
            field: 'ext$.samplebegintime',
            title: 'T_EMC_ORDER_TASK.SAMPLEBEGINTIME',
            type: 'time',
            width: 100,
        }, {
            field: 'ext$.labelno',
            title: 'T_EMC_ORDER_TASK.LABELNO',
        }, {
            field: 'equiptAcquNo',
            title: 'T_EMC_ORDER_TASK.EQUIPTACQUNO',
            validators: ['strLength[0, 120]']
        }];
    },

    getOrderTaskChooseListColumns: function () {
        return [{
            field: 'testName',
            title: 'T_EMC_ORDER_TASK.TESTNAME',
        }];
    },

    getOrderItemGroupChooseListColumns: function () {
        return [{
            field: 'testName',
            title: 'T_EMC_ORDER_TASK.TESTNAME',
        }, {
            field: 'itemGroup',
            title: 'T_EMC_ORDER_TASK.ITEMGROUP',
        }];
    },

    getEquiptOrderTaskListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'testName',
            title: 'T_EMC_ORDER_TASK.TESTNAME',
        }, {
            field: 'orderCategory',
            title: 'T_EMC_ORDER_TASK.ORDERCATEGORY',
        }, {
            field: 'ext$.orderno',
            title: 'T_EMC_ORDER_TASK.ORDERNO',
        }, {
            field: 'ext$.projno',
            title: 'T_EMC_PROJECT.PROJNO',
        }, {
            field: 'qcCategoryCode',
            title: 'T_EMC_ORDER_TASK.QCCATEGORYCODE',
        }, {
            field: 'qcName',
            title: 'T_EMC_ORDER_TASK.QCNAME',
        }, {
            field: 'methodName',
            title: 'T_EMC_ORDER_TASK.METHODNAME',
        }, {
            field: 'methodParam',
            title: '方法参数',
            formatter: function (i, v, row) {
                return Gikam.printf('<a href="javascript:;" onclick="emcRun.editPage.loadResultOrderTaskMethodParamPage(\'{id}\')">{name}</a>', {
                    id: row.id,
                    name: '方法参数'
                });
            }
        }, {
            field: 'ext$.equiptsid',
            title: 'T_EMC_RUN.EQUIPTID',
        }, {
            field: 'ext$.equiptname',
            title: 'T_EMC_ORDER_TASK.EQUIPTNAME',
        }];
    },

    getQcRelatedOrderListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'ext$.projno',
            title: 'T_EMC_PROJECT.PROJNO',
        }, {
            field: 'relatedOrderNo',
            title: 'T_EMC_ORDER.ORDERNO',
        }];
    },

    getRelatedQcOrderListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'qcCategoryCode',
            title: 'T_EMC_ORDER.QCCATEGORYCODE',
        }, {
            field: 'qcName',
            title: 'T_EMC_ORDER.QCNAME',
        }, {
            field: 'qcOrderNo',
            title: 'T_EMC_ORDER.ORDERNO',
            width: 150
        }];
    },

    getTempHumidityListColumns: function () {
        return [{
            field: 'ext$.projno',
            title: 'T_EMC_PROJECT.PROJNO',
        }, {
            field: 'ext$.orderno',
            title: 'T_EMC_ORDER_TASK.ORDERID',
            width: 120,
        }, {
            field: 'ext$.testname',
            title: 'T_EMC_ORDER_TASK.TESTNAME',
        }, {
            field: 'temperature',
            title: 'T_EMC_RUN.TEMPERATURE',
            editor: true,
            width: 150
        }, {
            field: 'humidity',
            title: 'T_EMC_RUN.HUMIDITY',
            editor: true,
            width: 150
        }, {
            field: 'ext$.equiptname',
            title: 'T_EMC_EQUIPT.EQUIPTNAME',
        }, {
            field: 'ext$.temphumidityequipt',
            title: 'T_EMC_RUN_TEMP_HUMIDITY.TEMPHUMIDITYEQUIPT',
        }, {
            field: 'beginDate',
            title: 'T_EMC_RUN_TEMP_HUMIDITY.BEGINDATE',
            type: 'date',
            width: 200,
            editor: true,
        }, {
            field: 'endDate',
            title: 'T_EMC_RUN_TEMP_HUMIDITY.ENDDATE',
            type: 'date',
            width: 200,
            editor: true,
        }, {
            field: 'beginDateOne',
            title: 'T_EMC_RUN_TEMP_HUMIDITY.BEGINDATEONE',
            type: 'date',
            width: 200,
            editor: true,
        }, {
            field: 'endDateOne',
            title: 'T_EMC_RUN_TEMP_HUMIDITY.ENDDATEONE',
            type: 'date',
            width: 200,
            editor: true,
        }, {
            field: 'beginDateTwo',
            title: 'T_EMC_RUN_TEMP_HUMIDITY.BEGINDATETWO',
            type: 'date',
            width: 200,
            editor: true,
        }, {
            field: 'endDateTwo',
            title: 'T_EMC_RUN_TEMP_HUMIDITY.ENDDATETWO',
            type: 'date',
            width: 200,
            editor: true,
        }];
    },


    getRunTaskOrderColumns: function () {
        return [{
            checkbox: true
        }, {
            index: true
        }, {
            field: 'ext$.projno',
            title: 'T_EMC_PROJECT.PROJNO'
        }, {
            field: 'ext$.orderno',
            title: 'T_EMC_ORDER_TASK.ORDERID',
            width: 120,
        }, {
            field: 'ext$.ordertaskequiptno',
            title: 'T_EMC_EQUIPT.EQUIPTID',
        }, {
            field: 'methodParam',
            title: '方法参数',
            formatter: function (i, v, row) {
                return Gikam.printf('<a href="javascript:;" onclick="emcRun.editPage.loadResultOrderTaskMethodParamPage(\'{id}\')">{name}</a>', {
                    id: row.id,
                    name: '方法参数'
                });
            }
        }];
    },

    getRunTaskOrderForMetalColumns: function () {
        return [{
            checkbox: true
        }, {
            index: true
        }, {
            field: 'ext$.projno',
            title: 'T_EMC_PROJECT.PROJNO'
        }, {
            field: 'ext$.orderno',
            title: 'T_EMC_ORDER_TASK.ORDERID',
            width: 120,
        }, {
            field: 'ext$.ordertaskequiptno',
            title: 'T_EMC_EQUIPT.EQUIPTID',
        }];
    },

    getResultForBatchColumns: function () {
        var grid = Gikam.getComp('emc-run-result-grid');
        var rows = grid.getData();
        var selectItems = [];
        for (var row of rows) {
            selectItems.push({
                text: row.itemName,
                value: row.itemName
            })
        }
        return [{
            checkbox: true
        }, {
            field: 'itemName',
            title: 'T_EMC_ORDER_TASK_RESULT.ITEMNAME',
            type: 'select',
            items: selectItems
        }, {
            field: 'originalResult',
            title: 'T_EMC_ORDER_TASK_RESULT.ORIGINALRESULT'
        },];
    },

    getBatchInputTempHumColumns: function () {
        return [{
            field: 'temperature',
            title: 'T_EMC_RUN.TEMPERATURE',
            editor: true,
            width: 200
        }, {
            field: 'humidity',
            title: 'T_EMC_RUN.HUMIDITY',
            editor: true,
            width: 200
        }, {
            field: 'beginDate',
            title: 'T_EMC_RUN_TEMP_HUMIDITY.BEGINDATE',
            type: 'date',
            width: 400,
            editor: true,
        }, {
            field: 'endDate',
            title: 'T_EMC_RUN_TEMP_HUMIDITY.ENDDATE',
            type: 'date',
            width: 400,
            editor: true,
        }, {
            field: 'beginDateOne',
            title: 'T_EMC_RUN_TEMP_HUMIDITY.BEGINDATEONE',
            type: 'date',
            width: 400,
            editor: true,
        }, {
            field: 'endDateOne',
            title: 'T_EMC_RUN_TEMP_HUMIDITY.ENDDATEONE',
            type: 'date',
            width: 400,
            editor: true,
        }, {
            field: 'beginDateTwo',
            title: 'T_EMC_RUN_TEMP_HUMIDITY.BEGINDATETWO',
            type: 'date',
            width: 400,
            editor: true,
        }, {
            field: 'endDateTwo',
            title: 'T_EMC_RUN_TEMP_HUMIDITY.ENDDATETWO',
            type: 'date',
            width: 400,
            editor: true,
        }];
    },

    getStdMatchTestListColumns: function () {
        return [{
            checkbox: true
        }, {
            index: true
        }, {
            field: 'testName',
            title: 'T_EMC_ORDER_TASK_RESULT.TESTNAME',
            editor: false
        }, {
            field: 'methodName',
            title: 'T_EMC_ORDER_TASK_RESULT.METHODNAME',
            editor: false
        }];
    },

    getPhysChemicListColumns: function () {
        return [{
            checkbox: true
        }, {
            index: true
        }, {
            field: 'waterTemp',
            title: 'T_EMC_RUN_PHYS_CHEMIC.WATERTEMP',
            editor: true
        }, {
            field: 'stdSol',
            title: 'T_EMC_RUN_PHYS_CHEMIC.STDSOL',
            editor: true
        }, {
            field: 'measuredValue',
            title: 'T_EMC_RUN_PHYS_CHEMIC.MEASUREDVALUE',
            editor: true
        }, {
            field: 'calibrationValues',
            title: 'T_EMC_RUN_PHYS_CHEMIC.CALIBRATIONVALUES',
            editor: true
        }, {
            field: 'zeroOxygenCalibration',
            title: 'T_EMC_RUN_PHYS_CHEMIC.ZEROOXYGENCALIBRATION',
            editor: true
        }, {
            field: 'fullnessCalibration',
            title: 'T_EMC_RUN_PHYS_CHEMIC.FULLNESSCALIBRATION',
            editor: true
        }, {
            field: 'remark',
            title: 'T_EMC_RUN_PHYS_CHEMIC.REMARK',
            editor: true
        }];
    },

    getBatchInputPhysChemicColumns: function () {
        return [{
            checkbox: true
        }, {
            index: true
        }, {
            field: 'waterTemp',
            title: 'T_EMC_RUN_PHYS_CHEMIC.WATERTEMP',
            editor: true
        }, {
            field: 'stdSol',
            title: 'T_EMC_RUN_PHYS_CHEMIC.STDSOL',
            editor: true
        }, {
            field: 'measuredValue',
            title: 'T_EMC_RUN_PHYS_CHEMIC.MEASUREDVALUE',
            editor: true
        }, {
            field: 'calibrationValues',
            title: 'T_EMC_RUN_PHYS_CHEMIC.CALIBRATIONVALUES',
            editor: true
        }, {
            field: 'zeroOxygenCalibration',
            title: 'T_EMC_RUN_PHYS_CHEMIC.ZEROOXYGENCALIBRATION',
            editor: true
        }, {
            field: 'fullnessCalibration',
            title: 'T_EMC_RUN_PHYS_CHEMIC.FULLNESSCALIBRATION',
            editor: true
        }, {
            field: 'remark',
            title: 'T_EMC_RUN_PHYS_CHEMIC.REMARK',
            editor: true
        }];
    },

    getChooseBeforeHandleFields: function () {
        return [{
            field: 'chooseBeforeHander',
            title: '选择前处理',
            editor: true,
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'chooseBeforeHander'
            },
        }];
    },

    getChooseLimitFields: function () {
        return [{
            field: 'chooseLimit',
            title: '选择检出底限',
            editor: true,
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'chooseLimit'
            },
        }];
    },

    getRunTaskProcessEditListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'projNo',
            title: 'T_EMC_RUN.PROJNO',
            readonly: true,
            width: 100,
        }, {
            field: 'projName',
            title: 'T_EMC_PROJECT.PROJNAME'
        }, {
            field: 'biddername',
            title: 'T_EMC_PROJECT.BIDDERNAME'
        }, {
            field: 'orderCategory',
            title: '样品类型'
        }, {
            field: 'detectionMetrics',
            title: '检测指标'
        }, {
            field: 'createdTime',
            title: '下单日期'
        }, {
            field: 'sampleAssign',
            title: '采样指派'
        }, {
            field: 'sampleBegin',
            title: '采样开始'
        }, {
            field: 'sampleEnd',
            title: '采样结束'
        }, {
            field: 'sampleAudit',
            title: '采样审核'
        }, {
            field: 'sampleHandover',
            title: '样品交接'
        }, {
            field: 'taskAssign',
            title: '任务分配'
        }, {
            field: 'physAudit',
            title: '理化审核'
        }, {
            field: 'metalAudit',
            title: '金属审核'
        }, {
            field: 'organicAudit',
            title: '有机审核'
        }, {
            field: 'diohyingAudit',
            title: '二噁英审核'
        }, {
            field: 'reportEdit',
            title: '报告编制'
        },
        ];
    },

    getRunTaskProcessOrgEditListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'projNo',
            title: 'T_EMC_RUN.PROJNO',
            readonly: true,
            width: 100,
        }, {
            field: 'projName',
            title: 'T_EMC_PROJECT.PROJNAME'
        }, {
            field: 'biddername',
            title: 'T_EMC_PROJECT.BIDDERNAME'
        }, {
            field: 'orderCategory',
            title: '样品类型'
        }, {
            field: 'detectionMetrics',
            title: '检测指标'
        }, {
            field: 'createdTime',
            title: '下单日期'
        }, {
            field: 'sampleAssign',
            title: '采样指派'
        }, {
            field: 'sampleBegin',
            title: '采样开始'
        }, {
            field: 'sampleEnd',
            title: '采样结束'
        }, {
            field: 'sampleAudit',
            title: '采样审核'
        }, {
            field: 'sampleHandover',
            title: '样品交接'
        }, {
            field: 'taskAssign',
            title: '任务分配'
        }, {
            field: 'analyzeAudit',
            title: '分析审核'
        }
        ];
    },

    getRunDetailUnCompleteField: function () {
        return [{
            title: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.UN_COMPLETE',
            field: 'showFlag',
            type: 'simpleCheckbox',
            refreshGrid: false,
            styleFormatter: function () {
                return {
                    paddingLeft: '0'
                }
            },
            onChange: function (field, value, index) {
                workspace.window.showMask(true);

                var testGrid = Gikam.getComp('emc-run-detail-sample-test-grid');
                var showFlags = value;

                if (showFlags == '1') {
                    // 刷新样品测试
                    testGrid.refresh({
                        requestData: {
                            originalResults: 'empty'
                        }
                    }).done(function () {
                        workspace.window.closeMask();
                    });

                } else {
                    // 刷新样品测试
                    testGrid.refresh({
                        requestData: {
                            originalResults: ''
                        }
                    }).done(function () {
                        workspace.window.closeMask();
                    });

                }
            },
        }]
    },

    getCurveListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'status',
            title: 'T_EMC_CURVE.STATUS',
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'publishStatus',
            },
            width: 100
        }, {
            field: 'curveNo',
            title: 'T_EMC_CURVE.CURVENO',
            type: 'link',
            width: 100
        }, {
            field: 'curveCategory',
            title: 'T_EMC_CURVE.CURVECATEGORY',
            validators: ['notEmpty'],
            type: 'select',
            value: 'calibration',
            category: 'sys',
            param: {
                codeCategoryId: 'curveCategory'
            }
        }, {
            field: 'equation',
            title: 'T_EMC_CURVE.EQUATION',
            formatter: function (i, v, row) {
                var intercept = row.intercept;
                var slope = row.slope;
                var coefficient = row.coefficient;
                if (!Gikam.isEmpty(intercept) && !Gikam.isEmpty(slope) && !Gikam.isEmpty(coefficient)) {
                    intercept = intercept.startsWith('-') ? intercept : '+' + intercept;
                    var equation = 'y=' + slope + 'x' + intercept + ',r=' + coefficient;
                    return equation;
                }
            },
            width: 200
        }, {
            field: 'ext$.equiptno',
            title: 'T_EMC_CURVE.EQUIPTID',
            width: 100
        }, {
            field: 'ext$.equiptname',
            title: 'T_EMC_CURVE.EQUIPTNAME'
        }, {
            field: 'testName',
            title: 'T_EMC_CURVE.TESTNAME'
        }, {
            field: 'methodName',
            title: 'T_EMC_CURVE.METHODNAME'
        }, {
            field: 'itemName',
            title: 'T_EMC_CURVE.ITEMNAME',
            width: 100
        }, {
            field: 'createdByName',
            title: 'T_EMC_CURVE.CREATEDBYNAME',
            width: 100
        }, {
            field: 'createdTime',
            title: 'T_EMC_CURVE.CREATEDTIME',
            width: 100
        }, {
            field: 'confDate',
            title: 'T_EMC_CURVE.CONFDATE',
            width: 100
        }, {
            field: 'expireDate',
            title: 'T_EMC_CURVE.EXPIREDATE',
            width: 100
        }];
    },

    getEquiptCurveListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'equiptCurveNo',
            title: 'T_EMC_EQUIPT_CURVE.EQUIPTCURVENO',
            type: 'link',
        }, {
            field: 'ext$.equiptno',
            title: 'T_EMC_EQUIPT_CURVE.EQUIPTID',
            width: 100
        }, {
            field: 'ext$.equiptname',
            title: 'T_EMC_EQUIPT_CURVE.EQUIPTNAME'
        }, {
            field: 'testName',
            title: 'T_EMC_EQUIPT_CURVE.TESTNAME',
            width: 100,
            type: 'choose',
            category: 'test',

        }, {
            field: 'createdByName',
            title: 'T_EMC_EQUIPT_CURVE.CREATEDBYNAME',
            width: 100
        }, {
            field: 'createdTime',
            title: 'T_EMC_EQUIPT_CURVE.CREATEDTIME',
            width: 100
        }, {
            field: 'confDate',
            title: 'T_EMC_EQUIPT_CURVE.CONFDATE',
            width: 100
        }, {
            field: 'analysisDate',
            title: 'T_EMC_EQUIPT_CURVE.ANALYSISDATE',
            width: 100
        }, {
            field: 'expireDate',
            title: 'T_EMC_EQUIPT_CURVE.EXPIREDATE',
            width: 100
        }];
    },

    getEquiptConditionListColumns: function () {
        return [{
            field: 'chromatographicColumn',
            title: 'T_EMC_RUN.CHROMATOGRAPHICCOLUMN',
            editor: true
        }, {
            field: 'testEquipt',
            title: 'T_EMC_RUN.TESTEQUIPT',
            editor: true
        }, {
            field: 'flowVelocity',
            title: 'T_EMC_RUN.FLOWVELOCITY',
            editor: true
        }, {
            field: 'sampleSize',
            title: 'T_EMC_RUN.SAMPLESIZE',
            editor: true
        }, {
            field: 'processOfWashing',
            title: 'T_EMC_RUN.PROCESSOFWASHING',
            editor: true,
            type: 'select',
            items: [
                {text: '等度淋洗', value: '等度淋洗'},
                {text: '梯度淋洗', value: '梯度淋洗'}
            ]
        }, {
            field: 'liquidOfWashing',
            title: 'T_EMC_RUN.LIQUIDOFWASHING',
            editor: true
        }, {
            field: 'suppressor',
            title: 'T_EMC_RUN.SUPPRESSOR',
            editor: true
        }];
    },

    getQcGridColumns: function () {
        return [{
            field: 'qcName',
            title: 'T_EMC_ORDER_TASK.QCNAME',
            width: 140
        }, {
            field: 'ext$.qcnums',
            title: 'T_EMC_ORDER_TASK.QCNUMS',
            width: 60,
        }, {
            field: 'ext$.qualified',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno',
            },
            title: 'T_EMC_ORDER_TASK.ISITQUALIFIED',
            width: 90,
        }];
    },
    getStepsPanelObj: function () {
        return {
            run: ['emc-run-detail-base-panel'],
            methodparam: ['emc-run-methodparam-base-panel'],
            qc: ['emc-run-detail-qc-panel'],
            curve: ['emc-run-detail-curve-panel', 'emc-run-detail-equipt-curve-panel', 'emc-run-detail-equipt-condition-panel'],
            origindata: ['emc-run-detail-origindata-panel'],
            record: ['emc-run-detail-record-panel'],
            browserecord: ['emc-run-detail-browse-record-panel'],
            qcdata: ['emc-run-detail-qcdata-panel'],
            datacheck: ['emc-run-detail-datacheck-panel'],
            attach: ['emc-run-detail-attach-panel']
        }
    },

    getRunDataCheckRunListColumns: function () {
        return [{
            index: true
        }, {
            field: 'remind',
            title: 'T_EMC_RUN_REMIND.REMIND'
        }, {
            field: 'value',
            title: 'T_EMC_RUN_REMIND.VALUE',
            width: 150
        }, {
            field: 'checkResult',
            title: 'T_EMC_RUN_REMIND.CHECKRESULT',
            styleFormatter: function (row) {
                return {
                    color: row.checkResult === '异常' ? 'red' : 'green'
                }
            }
        }, {
            field: 'abnormalExplain',
            title: 'T_EMC_RUN_REMIND.ABNORMALEXPLAIN',
        }];
    },
    getRunDataCheckResultListColumns: function () {
        return [{
            index: true
        }, {
            field: 'remind',
            title: 'T_EMC_RUN_REMIND.REMIND'
        }, {
            field: 'value',
            title: '样品编号',
            width: 200
        }, {
            field: 'checkResult',
            title: 'T_EMC_RUN_REMIND.CHECKRESULT',
            styleFormatter: function (row) {
                return {
                    color: row.checkResult === '异常' ? 'red' : 'green'
                }
            }
        }, {
            field: 'abnormalExplain',
            title: 'T_EMC_RUN_REMIND.ABNORMALEXPLAIN',
        }];
    },

    getOrderTaskEditSampleListColumns: function (resultId) {
        return [{
            checkbox: true
        }, {
            index: true
        }, {
            field: 'projNo',
            title: 'T_EMC_PROJECT.PROJNO'
        }, {
            field: 'projName',
            title: '项目名称',
        },]
    },

    getOrderTaskEditListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'ext$.testmethodtype',
            title: 'T_EMC_TEST_METHOD_TESTMETHODTYPE',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'testMethodType',
            },
        }, {
            field: 'testName',
            title: 'T_EMC_ORDER_TASK.TESTNAME'
        }, {
            field: 'ext$.itemgroup',
            title: '分析项组'
        }, {
            field: 'methodName',
            title: 'T_EMC_ORDER_TASK.METHODNAME',
        }, {
            field: 'testMethodBasis',
            title: 'T_EMC_ORDER_TASK.TESTMETHODBASIS',
        }, {
            field: 'orderCategory',
            title: 'T_EMC_ORDER_TASK.ORDERCATEGORY',
        }, {
            field: 'ext$.labelno',
            title: 'T_EMC_ORDER.LABELNO',
        }, {
            field: 'ext$.orderno',
            title: 'T_EMC_ORDER_TASK.ORDERNO',
        }, {
            field: 'inputUserName',
            title: 'T_EMC_ORDER_TASK.INPUTUSERNAME',
            editor: true,
            type: 'choose',
            category: 'user-simple',
            onAfterChoose: function (rows, index) {
                if (rows) {
                    var userIds = [], userNames = [];
                    rows.map(function (row) {
                        userIds.push(row.id);
                        userNames.push(row.userName);
                    });
                    this.setData({
                        index: index,
                        inputUserId: userIds.join(),
                        inputUserName: userNames.join(),
                        analysisOrgId: rows[0].orgId,
                        analysisOrgName: rows[0].orgName,
                        assign: '1'
                    });
                }
            },
            onClean: function () {
                this.setData({
                    id: this.getActivedRow().id,
                    inputUserId: '',
                    inputUserName: '',
                    analysisOrgId: '',
                    analysisOrgName: '',
                });
            },
        }];
    },

    getPreprocessingResultListColumns: function () {
        return [{
            checkbox: true
        }, {
            index: true
        }, {
            title: '样品编号',
            field: 'orderNo',
            width: 180
        }, {
            title: 'qc类型',
            field: 'qcName',
            width: 120
        }, {
            title: '样品日期',
            field: 'sampleBeginDate',
            width: 120,
            type: 'date',
        }, {
            title: '样品时间',
            field: 'sampleBeginTime',
            width: 120
        }, {
            title: '监测点名称',
            field: 'monitorPointName',
            width: 120
        },];
    },

    workflow: {
        dbTable: 'T_EMC_RUN',
        columns: [{
            field: 'id',
            title: 'T_EMC_RUN.ID'
        }]
    }
});
