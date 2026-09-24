emcRun.orderTaskEditPage = {

    projId: null,
    getSampleGrid: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-run-order-task-edit-sample-list-grid',
            url: IFM_CONTEXT + '/secure/emc/module/bp/project/projects/queries',
            filterOpen: true,
            page: true,
            requestData: {
                targetType: 'analysisWithoutReceiver',
                taskAssign: '1',
                orgId_SEQ: workspace.user.ext$.parentorgid
            },
            order: {
                urgent: 'desc'
            },
            rowStyleFormatter: function (row) {
                if (row.urgent === '1') {
                    return {
                        backgroundColor: Gikam.emcUrgentColor
                    }
                }
            },
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData(['emc-run-order-task-edit-list-grid']);
                }
            },
            onRowActive: function (index, row) {
                var _this = this;
                if (row) {
                    Gikam.getComp('emc-run-order-task-edit-list-grid').refresh({
                        url: IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/un-runnable',
                        requestData: {
                            status_SEQ: 'logged',
                            analysisType_SEQ: 'analysis',
                            testMethodType_SEQ: emcRun.orderTaskEditPage.testMethodType,
                            // searchType: 'assign',
                            'projid_EQ': row.id,
                        }
                    });
                }
            },
            columns: emcRun.getOrderTaskEditSampleListColumns(),
            toolbar: [{
                type: 'form',
                id: 'emc-info-data-edit-list-query-form',
                columns: 4,
                fields: [{
                    field: 'testMethodType',
                    title: 'T_EMC_TEST_METHOD_TESTMETHODTYPE',
                    type: 'select',
                    category: 'sys',
                    param: {
                        codeCategoryId: 'testMethodType',
                    },
                    search: true,
                    refreshGrid: false,
                    onChange: function (field, value, text) {
                        emcRun.orderTaskEditPage.testMethodType = Gikam.isEmpty(value) ? '' : value;
                        Gikam.getComp('emc-run-order-task-edit-sample-list-grid').refresh({
                            requestData: {
                                testMethodType: emcRun.orderTaskEditPage.testMethodType,
                            }
                        }).done(function () {
                            // workspace.window.closeMask();
                        });
                    }
                }],
            }, {
                type: 'form',
                autoSave: false,
                fields: _this.getProjectShowAllField()
            }],
        }
    },

    getGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-run-order-task-edit-list-grid',
            // url: IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/un-runnable',
            service: 'emcOrderTaskServiceImpl',
            filterOpen: true,
            page: true,
            order: {
                testName: 'asc'
            },
            rowStyleFormatter: function (row) {
                if (row.assign === '1') {
                    return {
                        backgroundColor: 'palegreen'
                    }
                }
            },
            columns: emcRun.getOrderTaskEditListColumns(),
            toolbar: [/*{
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.FILTER_PROJECT',
                icon: 'preview',
                onClick: function () {
                    Gikam.create('modal', {
                        title: 'EMC.MODULE.BP.RESULT.RUNS.MODAL.FILTER_PROJECT',
                        width: '45%',
                        url: Gikam.choose.getConfig('project').url + Gikam.param({
                            targetType: 'analysis'
                        }),
                        onAfterClose: function (data) {
                            if (Gikam.isEmpty(data)) {
                                return;
                            }

                            var projectId = data.map(function (e) {
                                return e.id;
                            });

                            _this.projId = projectId.join();
                            Gikam.getComp('emc-run-order-task-edit-list-grid').setOptions({
                                requestData: {
                                    projId_IN: _this.projId
                                }
                            });

                            Gikam.getComp('emc-run-order-task-edit-list-grid').refresh();
                        }
                    });
                }
            },*/ {
                type: 'button',
                text: 'EMC.MODULE.MDM.BIZMDM.TESTS.BUTTON.ADD_INPUTUSER',
                icon: 'modify',
                onClick: function () {
                    _this.batchAddInputUser();
                }
            }, {
                type: 'button',
                text: '指派完成',
                icon: 'modify',
                onClick: function () {
                    _this.assign();
                }
            }, {
                type: 'button',
                text: '同步录入人',
                icon: 'edit',
                onClick: function () {
                    _this.syncInputUser();
                }
            }, {
                type: 'form',
                autoSave: false,
                fields: _this.getShowAllField()
            }, {
                type: 'caption',
                text: '以下数据绿色背景为指派完成',
                color: 'red'
            }],
        }
    },

    syncInputUser: function () {
        var orderTaskGrid = Gikam.getComp('emc-run-order-task-edit-list-grid');
        var selections = orderTaskGrid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var saveData = [];
        selections.map(function (selection) {
            saveData.push({
                inputUserId: selection.ext$.capacityinputuserid,
                inputUserName: selection.ext$.capacityinputusername,
                id: selection.id,
            })
        });

        workspace.window.showMask(true);
        Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, ['emcOrderTaskServiceImpl', saveData])).done(function (id) {
            Gikam.getComp('emc-run-order-task-edit-list-grid').refresh();
        }).always(function () {
            workspace.window.closeMask();
        });
    },
    assign: function () {
        var orderTaskGrid = Gikam.getComp('emc-run-order-task-edit-list-grid');
        var selections = orderTaskGrid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var saveData = [];
        selections.map(function (selection) {
            saveData.push({
                assign: '1',
                id: selection.id,
            })
        });

        workspace.window.showMask(true);
        Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, ['emcOrderTaskServiceImpl', saveData])).done(function (id) {
            Gikam.getComp('emc-run-order-task-edit-list-grid').refresh();
        }).always(function () {
            workspace.window.closeMask();
        });
    },

    getProjectShowAllField: function () {
        return [{
            title: '显示全部',
            field: 'showFlag',
            type: 'simpleCheckbox',
            value: '0',
            onChange: function (field, value, index) {
                // workspace.window.showMask(true);
                var grid = Gikam.getComp('emc-run-order-task-edit-sample-list-grid');

                if (value === '1') {
                    grid.refresh({
                        requestData: {
                            taskAssign: '',
                        }
                    }).done(function () {
                        // workspace.window.closeMask();
                    });
                } else {
                    grid.refresh({
                        requestData: {
                            taskAssign: '1',
                        }
                    }).done(function () {
                        // workspace.window.closeMask();
                    });
                }
            },
        }]
    },

    getShowAllField: function () {
        return [{
            title: '显示全部',
            field: 'showFlag',
            type: 'simpleCheckbox',
            value: '1',
            onChange: function (field, value, index) {
                // workspace.window.showMask(true);
                var grid = Gikam.getComp('emc-run-order-task-edit-list-grid');

                if (value === '1') {
                    grid.refresh({
                        requestData: {
                            assign_SEQ: '',
                        }
                    }).done(function () {
                        // workspace.window.closeMask();
                    });
                } else {
                    grid.refresh({
                        requestData: {
                            assign_SEQ: '0',
                        }
                    }).done(function () {
                        // workspace.window.closeMask();
                    });
                }
            },
        }]
    },

    /**
     * 批量修改录入人
     */
    batchAddInputUser: function () {
        var orderTaskGrid = Gikam.getComp('emc-run-order-task-edit-list-grid');
        var selections = orderTaskGrid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var orderTaskIds = "";
        selections.forEach(function (item) {
            orderTaskIds += item.id + ',';
        });
        orderTaskIds = orderTaskIds.substring(0, orderTaskIds.length - 1);

        Gikam.create('modal', {
            title: 'EMC.MODULE.MDM.BIZMDM.TESTS.TITLE.EDIT_INPUTUSER',
            width: 700,
            url: '/secure/core/module/mdm/users/page/shuttle-frame' + Gikam.param({
                orderTaskIds: orderTaskIds,
                firstInputUserIds: selections[0].inputUserId,
                org_filter: '1',
            }),
            onAfterClose: function () {

                Gikam.getComp('emc-run-order-task-edit-list-grid').refresh();
            }
        });
    },

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-run-order-task-edit-list-layout',
            renderTo: workspace.window.$dom,
            west: {
                width: '30%',
                items: [_this.getSampleGrid()]
            },
            center: {
                items: [_this.getGridParam()]
            }
        });
    },

    init: function () {
        this.create();
    }
};
