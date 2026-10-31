emcRun.editPage = {
    projId: null,
    getGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            fill: true,
            id: 'emc-run-edit-list-grid',
            url: IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/runnable',
            service: 'emcRunServiceImpl',
            refreshFromSearch: false,
            filterOpen: true,
            page: true,
            pageSize: 100,
            requestData: {
                status_SEQ: 'logged',
                analysisType_SEQ: 'analysis',
                // authority_org : '1',
                inputUserId: workspace.user.id,
                // userid : workspace.user.id,
                orgId: workspace.user.ext$.parentorgid,
                samplingSubpackage_IN: ',',
                // sampleReceiverFlag : '1'
                /*startAcceptedDate : '2023-01-31'*/
            },
            columns: emcRun.getEditListColumns(),
            // rowStyleFormatter : function(row) {
            //     if (row.ext$.urgent === '1' || row.ext$.urgent === '2') {
            //         return {
            //             backgroundColor: Gikam.emcUrgentColor
            //         }
            //     }else {
            //         if (row.ext$.urgent === '3') {
            //             return {
            //                 backgroundColor: 'yellow'
            //             }
            //         }
            //     }
            // },
            onCellClick: function (field, row) {
                field === 'methodName' && _this.loadMethodEdit(row);
                field === 'ext$.projno' && _this.viewProjReport(row.ext$.projno);
            },
            onRendered: function () {
                var grid = Gikam.getComp('emc-run-edit-list-grid');
                grid.refreshGroup({
                    fields: ['orderCategory'],
                    formatter: function (row) {
                        return row.orderCategory;
                    }
                });
            },
            toolbar: [{
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.ADD',
                icon: 'add',
                onClick: function () {
                    var data = Gikam.getComp('emc-run-edit-list-grid').getSelections();
                    if (Gikam.isEmpty(data)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }

                    let methodIdSet = new Set(Array.from(data.map(item => item.methodId)));
                    if (methodIdSet.size !== 1) {
                        Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.METHOD_IS_DIFFERENT');
                        return;
                    }

                    var testMethodId = data.map(function (e) {
                        return e.testMethodId;
                    });
                    var orderCategory = data.map(function (e) {
                        return e.orderCategory;
                    });
                    var projIds = data.map(function (e) {
                        return e.projId;
                    }).join(',');
                    var orderDates = data.map(function (e) {
                        return e.ext$.orderdate;
                    }).join(',');
                    orderCategory = _this.unique(orderCategory);
                    Gikam.create('modal', {
                        title: 'EMC.MODULE.BP.RESULT.RUNS.MODAL.SELECT_SAMPLE_AND_EQUIPT',
                        url: emcRun.baseUrl + '/page/add-run-detail' + Gikam.param({
                            testMethodId: testMethodId,
                            orderCategory: orderCategory,
                            projId: projIds,
                            orderDate: orderDates,
                            org_filter: '1',
                        }),
                        onAfterClose: function (data) {
                            if (Gikam.isEmpty(data[0]) || Gikam.isEmpty(data[1])) {
                                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                return;
                            }
                            workspace.window.showMask(true);
                            Gikam.postText(emcRun.baseUrl, Gikam.getJsonWrapper({
                                projectId: projIds
                            }, ['emcOrderTaskServiceImpl', data[0]], ['emcEquiptServiceImpl', data[1]])).done(function () {
                                Gikam.getComp('emc-run-edit-list-grid').refresh();
                                Gikam.getComp('emc-run-edit-list-test-run-grid').refresh();
                            }).always(function () {
                                workspace.window.closeMask();
                            });
                        }
                    });
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.FILTER_PROJECT',
                icon: 'preview',
                onClick: function () {
                    Gikam.create('modal', {
                        title: 'EMC.MODULE.BP.RESULT.RUNS.MODAL.FILTER_PROJECT',
                        width: '85%',
                        url: Gikam.choose.getConfig('project').url + Gikam.param({
                            targetType: 'analysisWithoutReceiver',
                            org_filter: '1',
                        }),
                        onAfterClose: function (data) {
                            Gikam.getComp('emc-run-edit-list-grid').refresh({
                                requestData: {
                                    projId_IN: _this.projId
                                }
                            });
                        }
                    });
                }
            }, {
                type: 'form',
                autoSave: false,
                fields: emcRun.getPrepareShowAllField()
            }]
        }
    },

    /** 加载任务详情页面 */
    viewProjReport: function (projNo) {
        Gikam.viewReport('rasproject', [{PROJNO: projNo}]);
    },

    loadMethodEdit: function (row) {
        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.RESULT.RUNS.TITLE.METHOD_EDIT',
            url: emcRun.baseUrl + '/page/method-edit' + Gikam.param({
                testId: row.testId,
                methodId: row.methodId,
                testMethodId: row.testMethodId,
                orderCategory: row.orderCategory,
                projId: this.projId
            }),
            width: '85%',
            height: '85%',
            onAfterClose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    workspace.window.showMask(true);
                    Gikam.postText(emcRun.baseUrl + '/action/method-edit', Gikam.getJsonWrapper(null, ['emcOrderTaskServiceImpl', rows[0]], ['emcMonitorSchemeServiceImpl', rows[1]])).done(function () {
                        Gikam.getComp('emc-run-edit-list-grid').refresh();
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    getTestRunGrid: function () {
        var _this = this;
        return {
            type: 'tab',
            fill: true,
            panels: [{
                title: 'EMC.MODULE.BP.RESULT.RUNS.TAB.TEST_RUN',
                items: [{
                    type: 'grid',
                    id: 'emc-run-edit-list-test-run-grid',
                    url: emcRun.baseUrl + '/queries/raw',
                    service: 'emcRunServiceImpl',
                    columns: emcRun.getTestRunColumns(),
                    order: {
                        'ext$.adddate': 'desc',
                        'id': 'desc'
                    },
                    onCellClick: function (field, row) {
                        if (field === 'id') {
                            _this.loadRunDetail(row);
                        } else if (field === 'ext$.methodname') {
                            _this.loadMethodVersionFile(row);
                        }
                    },
                    deleteFormatter: function (row) {
                        return row.id;
                    },
                    /*onRendered : function() {
                        this.hideColumns([ 'ext$.equiptcode' ]);
                        this.hideColumns([ 'createdTime' ]);
                        this.hideColumns([ 'temperature' ]);
                        this.hideColumns([ 'humidity' ]);
                        this.hideColumns([ 'analysisBeginDate' ]);
                        this.hideColumns([ 'analysisBeginTime' ]);
                        this.hideColumns([ 'analysisEndDate' ]);
                        this.hideColumns([ 'analysisEndTime' ]);
                    },*/
                    onLoadSuccess: function (data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        } else {
                            Gikam.getComp('emc-run-edit-list-sample-test-grid').cleanData();
                        }
                    },
                    requestData: {
                        analysisUserId_SEQ: workspace.user.id,
                        org_filter: '1',
                        orgId_SEQ: workspace.user.ext$.parentorgid,
                        projstatus_DIFFER: 'cancel'
                    },
                    onRowActive: function (index, row) {
                        _this.onTestRunActive(row)
                    },
                    toolbar: _this.getRunButton()
                }]
            }]
        }
    },

    getRunButton: function () {
        var _this = this;
        return [{
            type: 'button',
            text: 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
            icon: 'submit',
            onClick: function () {
                _this.submit();
            }
        }, {
            type: 'button',
            text: '样品清单',
            icon: 'print',
            onClick: function () {
                Gikam.viewReport('yangpinqingdan', Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections());
            }
        }, {
            type: 'dropDownMenu',
            text: '原始记录',
            items: [{
                type: 'button',
                text: '查看原始记录',
                icon: 'default',
                onClick: function () {
                    _this.runViewRun();
                }
            }, {
                type: 'button',
                text: '查看标定记录',
                icon: 'default',
                onClick: function () {
                    _this.runViewRunSol();
                }
            }, {
                type: 'button',
                text: '合并原始记录',
                icon: 'default',
                onClick: function () {
                    _this.composePdf();
                },
            }, {
                type: 'button',
                text: '重置合并',
                icon: 'refresh',
                onClick: function () {
                    _this.resetCompose();
                }
            }, {
                type: 'button',
                text: '原始记录文件',
                icon: 'upload',
                onClick: function () {
                    _this.uploadRunReportFile();
                }
            }]
        }, {
            type: 'dropDownMenu',
            text: '文件采集',
            items: [{
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.EQUIPT_COLLECT',
                icon: 'edit',
                onClick: function () {
                    _this.runEquiptCollect();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.ATLAS_LOOK',
                icon: 'preview',
                onClick: function () {
                    _this.loadAtlasByRun();
                }
            }, {
                type: 'button',
                text: '重新采集',
                icon: 'edit',
                onClick: function () {
                    _this.runEquiptRepeateCollect();
                }
            },

            ]
        }, {
            type: 'button',
            text: '批录入',
            icon: 'edit',
            onClick: function () {
                var selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
                if (Gikam.isEmpty(selections)) {
                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                    return;
                }
                Gikam.getComp('emc-run-edit-list-test-run-grid').batchEdit({
                    height: 610,
                    fields: [{
                        field: 'analysisBeginDate',
                        title: 'T_EMC_RUN.ANALYSISBEGINDATE',
                        type: 'date',
                    }, {
                        field: 'analysisBeginTime',
                        title: 'T_EMC_RUN.ANALYSISBEGINTIME',
                        type: 'time',
                    }, {
                        field: 'analysisEndDate',
                        title: 'T_EMC_RUN.ANALYSISENDDATE',
                        type: 'date',
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
                                orgId_SEQ: workspace.user.ext$.parentorgid,
                                testMethodIds: selections[0].testMethodId,
                                targetType: 'auxiliaryEquipt',
                                id_differ: selections[0].equiptId,
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
                                monitorDate_SEQ: selections[0].analysisBeginDate
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
                        validators: ['strLength[0, 30]'],
                    }, {
                        field: 'humidity',
                        title: 'T_EMC_RUN.HUMIDITY',
                        validators: ['strLength[0, 30]']
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
                    },{
                        field: 'stNo',
                        title: '标准物质编号',
                        editor: true,
                    }, {
                        field: 'stConcent',
                        title: '标准物质浓度',
                        editor: true,
                    }, {
                        field: 'stValiDateStr',
                        title: '标准物质有效期',
                        editor: true,
                    }]
                })
            }
        }, {
            type: 'button',
            text: 'GIKAM.BUTTON.DELETE',
            icon: 'remove',
            onClick: function () {
                _this.runDelete();
            }
        }, {
            type: 'button',
            text: '分析结果检查',
            icon: 'print',
            onClick: function () {
                Gikam.viewReport('EMC_ANALYSIS_AUDIT', Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections());
            }
        }, {
            type: 'button',
            text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.ELN',
            icon: 'default',
            hidden: true,
            onClick: function () {
                _this.runELN();
            }
        }, {
            type: 'dropDownMenu',
            text: '仪器相关',
            items: [{
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.EQUIPT_EDIT',
                icon: 'edit',
                onClick: function () {
                    /// _this.runEditEquipt();
                    _this.editEquipt();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.EQUIPTCRVES.BUTTON.EQUIPT_CRVES',
                icon: 'edit',
                onClick: function () {
                    _this.equiptCurves();
                },
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.EQUIPT_USE_RECORD',
                icon: 'edit',
                onClick: function () {
                    _this.runEquiptUseRecord();
                }
            }, {
                type: 'button',
                text: '仪器参数',
                icon: 'edit',
                onClick: function () {
                    _this.runPhysChemicRecord();
                }
            }]
        }, {
            type: 'button',
            text: 'GIKAM.FILE.ATTACHMENT',
            icon: 'add',
            onClick: function () {
                _this.loadFilePage();
            }
        }, {
            type: 'button',
            text: '批量上传',
            icon: 'add',
            onClick: function () {
                _this.batchUploadFile();
            }
        }, {
            type: 'button',
            text: 'GIKAM.BUTTON.BATCH_INPUT',
            icon: 'edit',
            onClick: function () {
                _this.runBatchInput();
            },
        }, {
            type: 'button',
            text: '项目温湿度采集',
            icon: 'edit',
            hidden: true,
            onClick: function () {
                _this.runTempHumidity();
            }
        }, {
            type: 'button',
            text: '选择前处理',
            icon: 'add',
            hidden: true,
            onClick: function () {
                _this.chooseBeforeHandle();
            }
        }, {
            type: 'dropDownMenu',
            text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.MORE',
            items: [{
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.INITIALIZEC',
                icon: 'refresh',
                onClick: function () {
                    _this.runInitializec();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.UPDATEDEFAULTRESULTS',
                icon: 'edit',
                onClick: function () {
                    _this.runUpdatedefaultresults();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.UPDATE_STATIC_DATA',
                icon: 'edit',
                onClick: function () {
                    _this.updateStaticData();
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.DATA_SUMMARY',
                icon: 'browse',
                onClick: function () {
                    _this.dataSummaryRun();
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.REVIEW',
                icon: 'browse',
                onClick: function () {
                    _this.review();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.SOL_CONFIG',
                icon: 'edit',
                onClick: function () {
                    _this.solConf();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.REAGENT_CONFIG',
                icon: 'edit',
                onClick: function () {
                    _this.reagentConf();
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.VIEWAUDIT',
                icon: 'view-audit',
                onClick: function () {
                    Gikam.viewAudit('T_EMC_RUN', Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections());
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.LINK_RUN',
                icon: 'edit',
                hidden: true,
                onClick: function () {
                    _this.linkRun();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.UNLINK_RUN',
                icon: 'cancel',
                hidden: true,
                onClick: function () {
                    _this.unlinkRun();
                }
            }]
        }, {
            type: 'form',
            id: 'emc-run-edit-list-form',
            fields: emcRun.getRunShowAllField(),
            autoSave: false,
        }]
    },

    batchUploadFile: function () {
        var grid = Gikam.getComp('emc-run-edit-list-test-run-grid');
        var selections = grid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var bizId = selections[0].id;
        Gikam.create('simpleUploader', {
            id: 'emc-run-edit-list-test-run-batch-file-uploader',
            dbTable: 'T_EMC_RUN',
            bizId: bizId,
            //multiple : false,
            onAfterClose: function (fileList) {
                if (selections.length > 1) {
                    var fileIds = fileList.map(r => r.id).join(',')
                    var ids = [];
                    selections.forEach(item => {
                        if (item.id != bizId) {
                            ids.push(item.id)
                        }
                    });
                    Gikam.post(emcRun.baseUrl + '/batch-upload-file', Gikam.getJsonWrapper({
                        ids: ids.join(","),
                        fileIds: fileIds
                    }, ['', []])).done(function () {

                    });
                }
            }
        });
    },

    runDelete: function () {
        Gikam.getComp('emc-run-edit-list-test-run-grid').deleteRows(emcRun.baseUrl).done(function () {
            Gikam.getComp('emc-run-edit-list-grid').refresh();
            Gikam.getComp('emc-run-edit-list-test-run-grid').refresh();
        });
    },

    runInitializec: function () {
        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.MODAL.INITIALIZEC', 'EMC.MODULE.BP.RESULT.RUNS.TIP.INITIALIZEC', function () {
            var row = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow();
            if (Gikam.isEmpty(row)) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                return;
            }
            var url = emcRun.baseUrl + '/action/init';
            workspace.window.showMask(true);
            Gikam.put(url, Gikam.getJsonWrapper(null, ['', [row]])).done(function () {
                var testActivedRow = Gikam.getComp('emc-run-edit-list-sample-test-grid').getActivedRow();
                if (testActivedRow && !Gikam.isEmpty(testActivedRow.id)) {
                    Gikam.getComp('emc-run-edit-list-result-grid').refresh({
                        url: IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/queries',
                        requestData: {
                            orderId_SEQ: Gikam.getComp('emc-run-edit-list-sample-test-grid').getActivedRow().orderId
                        }
                    });
                }
            }).always(function () {
                workspace.window.closeMask();
            });
        });
    },

    runUpdatedefaultresults: function () {
        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.MODAL.INITIALIZEC', 'EMC.MODULE.BP.RESULT.RUNS.TIP.UPDATEDEFAULTRESULTS', function () {
            var row = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow();
            if (Gikam.isEmpty(row)) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                return;
            }
            var url = emcRun.baseUrl + '/action/updatedefaultresults';
            workspace.window.showMask(true);
            Gikam.put(url, Gikam.getJsonWrapper(null, ['', [row]])).done(function () {
                var id = Gikam.getComp('emc-run-edit-list-sample-test-grid').getActivedRow().id;
                workspace.window.closeMask();
                if (!Gikam.isEmpty(id)) {
                    Gikam.getComp('emc-run-edit-list-result-grid').refresh({
                        url: IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/queries',
                        requestData: {
                            orderId_SEQ: Gikam.getComp('emc-run-edit-list-sample-test-grid').getActivedRow().orderId
                        }
                    });
                }
            });
        });
    },

    runELN: function () {
        var grid = Gikam.getComp('emc-run-edit-list-test-run-grid');

        var selections = grid.getSelections();

        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var row = selections[0];

        if (Gikam.isEmpty(row.ext$.maxelntmplid)) {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.NOELN');
            return;
        }

        if (parent.isOpenElnBizId == row.id) {
            Gikam.alert('CORE.MODULE.ITEM.ELN.TMPLS.TIP.CLOSE_ELN');
            return;
        }

        var url = IFM_CONTEXT + '/static/plugin/spread-sheets-11.2.6/designer/index/index.html';
        var elnUrl = url + Gikam.param({
            operateType: 1,
            bizId: row.id, // 批ID
            elnTmplId: row.ext$.maxelntmplid, // 模板ID
            IFM_CONTEXT: IFM_CONTEXT,
            // 数据源需要的参数 有多少在后面加就行
            runId: row.id, // 批ID
        })
        window.open(elnUrl, '_blank');
    },

    runEquiptUseRecord: function () {
        var selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        if (Gikam.isEmpty(selections) || selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        if (selections.length > 1) {
            var differentEquipt = selections.some(function (row) {
                return row.equiptId !== selections[0].equiptId;
            });
            if (differentEquipt) {
                Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.SELECTED_EQUIPT_DIFFERENT');
                return;
            }
        }

        var runIds = selections.map(function (run) {
            return run.id;
        }).join();

        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.BPS.MODAL.EQUIPT_RECORD',
            url: '/secure/emc/module/mdm/equipt/equipts/page/use-record-edit-list' + Gikam.param({
                runId_IN: runIds
            })
        });

    },

    runEditEquipt: function () {
        var grid = Gikam.getComp('emc-run-edit-list-test-run-grid');
        var rows = grid.getSelections();
        if (rows.length != '1') {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_OPERATE_DATA');
            return;
        }
        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.RESULT.RUNS.MODAL.EDIT_EQUIPT',
            url: emcRun.baseUrl + '/page/edit-equipt' + Gikam.param({
                testMethodId: rows[0].testMethodId,
                equiptId: rows[0].equiptId,
            }),
            onAfterClose: function (data) {
                if (!Gikam.isEmpty(data)) {
                    workspace.window.showMask(true);
                    Gikam.postText(emcRun.baseUrl + '/action/edit-equipt', Gikam.getJsonWrapper(null, ['emcEquiptServiceImpl', data], ['emcRunServiceImpl', rows])).done(function () {
                        Gikam.getComp('emc-run-edit-list-test-run-grid').refresh();
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    editEquipt: function () {
        var grid = Gikam.getComp('emc-run-edit-list-test-run-grid');
        var selections = grid.getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_OPERATE_DATA');
            return;
        }

        // Gikam.create('modal', {
        //     title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.EDIT_EQUIPT',
        //     url : emcRun.baseUrl + '/page/edit-task-equipt-list' + Gikam.param({
        //         testMethodId : rows[0].testMethodId,
        //         equiptId : rows[0].equiptId,
        //         runId_SEQ : rows[0].id,
        //         TESTCAPCITYORGID_SEQ : workspace.user.ext$.parentorgid,
        //
        //     })
        // });

        Gikam.create('modal', {
            title: '修改仪器',
            url: Gikam.choose.getConfig('equipt').url + Gikam.param({
                orgId_SEQ: workspace.user.ext$.parentorgid,
                id_differ: selections[0].assistEquiptId,
                testMethodIds: selections[0].testMethodId,
                targetType: 'auxiliaryEquipt',
                single: true,
            }),
            onAfterClose: function (rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                workspace.window.showMask(true);
                Gikam.postText(emcRun.baseUrl + '/action/edit-equipt', Gikam.getJsonWrapper(null, ['emcEquiptServiceImpl', rows], ['emcRunServiceImpl', selections])).done(function () {
                    Gikam.getComp('emc-run-edit-list-test-run-grid').refresh();
                }).always(function () {
                    workspace.window.closeMask();
                });
                // grid.setData({
                //     id : selections[0].id,
                //     equiptId : rows[0].id,
                //     ext$ : {
                //         equiptcode : rows[0].equiptId,
                //     }
                // });
            }
        });
    },

    runBatchInput: function () {
        var grid = Gikam.getComp('emc-run-edit-list-test-run-grid');
        var rows = grid.getSelections();
        if (!Gikam.isEmpty(rows)) {
            var row = grid.getActivedRow();
            Gikam.create('modal', {
                width: '60%',
                height: 313,
                title: 'EMC.MODULE.BP.RESULT.RUNS.MODAL.BATCH_INPUT',
                url: emcRun.baseUrl + '/page/batch-input',
                onAfterClose: function (data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        rows.map(function (item) {
                            item.analysisBeginDate = data.analysisBeginDate;
                            item.analysisBeginTime = data.analysisBeginTime;
                            item.analysisEndDate = data.analysisEndDate;
                            item.analysisEndTime = data.analysisEndTime;
                            item.partakeUserId = data.partakeUserId;
                            item.partakeUserName = data.partakeUserName;
                            item.checkUserId = data.checkUserId;
                            item.checkUserName = data.checkUserName;
                            item.assistEquiptId = data.assistEquiptId;
                            item.assistEquiptName = data.assistEquiptName;
                            item.assistEquiptModel = data.assistEquiptModel;
                            item.assistEquiptNo = data.assistEquiptNo;
                            item.temperature = data.temperature;
                            item.humidity = data.humidity;
                            item.handleDate = data.handleDate;
                            item.handleMethod = data.handleMethod;
                            item.beforeHandle = data.beforeHandle;
                            item.ext$.roomno = data.ext$.roomno;
                        });
                        Gikam.put(emcRun.baseUrl + '/' + row.id, Gikam.getJsonWrapper({}, ['emcRunServiceImpl', rows])).done(function () {
                            rows.map(function (item) {
                                grid.refreshRowById(item.id);
                            });
                            //grid.refresh();
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

    runViewRun: function () {
        var selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var reportConfigLineId = selections[0].reportConfigLineId;
        if (Gikam.isEmpty(reportConfigLineId)) {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.HAS_NO_REPORT_TEMPLATE');
            return;
        }

        Gikam.viewReport(reportConfigLineId, selections[0]);
    },

    runViewRunSol: function () {
        var selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.viewReport("903396790157376", selections[0]);
    },

    runEquiptRepeateCollect: function () {
        var rows = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        if (rows && rows.length == 1) {
            var bizId = rows[0].id;
            var equiptId = rows[0].equiptId;
            if (Gikam.isEmpty(equiptId)) {
                Gikam.alert('未维护试验设备');
                return;
            }

            workspace.window.showMask(true);
            var url = Gikam.printf(emcRun.baseUrl + '/{runId}/equipt-collects', {
                runId: bizId,
            });

            Gikam.put(url, Gikam.getJsonWrapper({'type': 'repeate'}, ['', rows])).done(function () {
                Gikam.getComp('emc-run-edit-list-test-run-grid').refresh();
            }).always(function () {
                workspace.window.closeMask();
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },
    runEquiptCollect: function () {
        var rows = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        if (rows && rows.length == 1) {
            var bizId = rows[0].id;

            var equiptId = rows[0].equiptId;
            if (Gikam.isEmpty(equiptId)) {
                Gikam.alert('未维护试验设备');
                return;
            }
            Gikam.create('simpleUploader', {
                id: 'emc-run-edit-list-test-run-uploader',
                dbTable: 'T_EMC_EQUIPT',
                bizId: bizId,
                multiple: true,

                onAfterClose: function (fileList) {
                    if (!Gikam.isEmpty(fileList)) {
                        workspace.window.showMask(true);
                        var url = Gikam.printf(emcRun.baseUrl + '/{runId}/equipt-collects', {
                            runId: bizId,
                        });
                        fileList.map(function (file) {
                            file.size = 0;
                        });
                        Gikam.put(url, Gikam.getJsonWrapper({}, ['coreFileServiceImpl', fileList])).done(function () {
                            Gikam.getComp('emc-run-edit-list-test-run-grid').refresh();
                        }).always(function () {
                            workspace.window.closeMask();
                        });
                    }
                }
            })

            // Gikam.create('modal', {
            //     width : '70%',
            //     title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.CHOOSE_EQUIPT',
            //     url : emcRun.baseUrl + '/page/choose-acq-equipt' + Gikam.param({
            //         runId : bizId,
            //     }),
            /*onAfterClose : function(data) {
                if (!Gikam.isEmpty(data)) {
                    rows.forEach(item => {
                        item.equiptId = data[0].id
                    });

                    workspace.window.showMask(true);
                    Gikam.put(IFM_CONTEXT + '/secure/gikam/actions/instant', Gikam.getJsonWrapper(null, [ 'emcOrderTaskServiceImpl', rows ])).done(function() {
                        grid.refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }*/
            // });


            /*Gikam.create('simpleUploader', {
                id : 'emc-run-edit-list-test-run-uploader',
                dbTable : 'T_EMC_EQUIPT',
                bizId : bizId,
                multiple : true,

                onAfterClose : function(fileList) {
                    if (!Gikam.isEmpty(fileList)) {
                        workspace.window.showMask(true);
                        var url = Gikam.printf(emcRun.baseUrl + '/{runId}/equipt-collects', {
                            runId : bizId
                        });
                        fileList.map(function(file) {
                            file.size = 0;
                        });
                        Gikam.put(url , Gikam.getJsonWrapper({}, [ 'coreFileServiceImpl', fileList ])).done(function() {
                            Gikam.getComp('emc-run-edit-list-test-run-grid').refresh();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                }
            });*/
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    loadRunDetail: function (row) {
        Gikam.create('modal', {
            height: '95%',
            width: '95%',
            title: '测试批详情',
            url: emcRun.baseUrl + '/page/run-input-detail' + Gikam.param({
                runId: row.id,
                analysisDate: row.analysisBeginDate
            }),
            onAfterClose: function () {
                Gikam.getComp('emc-run-edit-list-test-run-grid').refreshRowById(row.id);
            }
        });
    },

    loadMethodVersionFile: function (row) {
        var url = Gikam.printf(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/methods/{id}/versions/queries', {
            id: row.methodId
        });
        Gikam.post(url, Gikam.getJsonWrapper({
            o: [{
                VERSIONNO: 'desc'
            }],
        }, ['', []],)).done(function (data) {
            if (!Gikam.isEmpty(data.rows)) {
                var fileid = data.rows[0].ext$.fileid;
                if (!Gikam.isEmpty(fileid)) {
                    var fileid = data.rows[0].ext$.fileid;
                    Gikam.openFile(fileid, true);
                } else {
                    Gikam.alert('未维护附件');
                }
            }
        });
    },

    updateStaticData: function () {
        var gridData = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        if (gridData.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            width: 1000,
            height: 500,
            title: 'EMC.MODULE.BP.RESULT.RUNS.MODAL.UPDATE_STATIC_DATA',
            url: IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/tests/page/update-list' + Gikam.param({
                testId: gridData[0].testId,
                methodId: gridData[0].methodId,
                testMethodId: gridData[0].testMethodId,

            })
        });
    },

    dataSummaryRun: function () {
        var gridData = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        if (gridData.length > 0) {
            var ids = "";
            gridData.map(function (item, i) {
                ids += gridData[i].id + ',';
            });
            ids = ids.substring(0, ids.length - 1);
            Gikam.create('modal', {
                width: 1000,
                height: 500,
                title: 'GIKAM.MODAL.DATA_SUMMARY',
                url: IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/page/data-summary' + Gikam.param({
                    summaryType: 'run',
                    ids: ids
                })
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    loadFilePage: function () {

        var selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();

        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title: 'GIKAM.FILE.ATTACHMENT',
            url: '/secure/emc/module/mdm/file/files/page/view' + Gikam.param({
                id: selections[0].id,
                table: 'T_EMC_RUN',
            }),
        });
    },

    loadResultRunMethodParamPage: function (id) {
        var _this = this;
        Gikam.create('modal', {
            width: '60%',
            title: 'EMC.MODULE.BP.RESULT.RUNS.MODAL.METHOD_PARAM_PROPERTY_SET',
            url: '/secure/emc/module/mdm/bizmdm/tests/page/result-run/method-param-detail' + Gikam.param({
                id: id,
            }),
        });
    },


    loadResultOrderTaskMethodParamPage: function (id) {
        var _this = this;
        Gikam.create('modal', {
            width: '60%',
            title: 'EMC.MODULE.BP.RESULT.RUNS.MODAL.METHOD_PARAM_PROPERTY_SET',
            url: '/secure/emc/module/mdm/bizmdm/tests/page/result-order-task/method-param-detail' + Gikam.param({
                id: id
            }),
        });
    },

    loadAtlasByRun: function () {

        var runSelections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        if (runSelections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var orderTaskData = Gikam.getComp('emc-run-edit-list-sample-test-grid').getData();
        var orderTaskIds = orderTaskData.map(item => item.id).join();

        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.RESULT.RUNS.TAB.LOOK_ATLAS',
            url: '/secure/emc/module/bp/bp/order-tasks/page/atlas' + Gikam.param({
                orderTaskIds: orderTaskIds,
                targetIds: ['T_EMC_EQUIPT$' + runSelections[0].id],
                runId_SEQ: runSelections[0].id
            })
        });
    },

    //结果录入
    loadResultInputPage: function (runId, index) {
        Gikam.create('modal', {
            height: '95%',
            width: '95%',
            title: 'EMC.MODULE.BP.RESULT.RUNS.TAB.TSAMPLE_TEST',
            url: emcRun.baseUrl + '/page/result-input' + Gikam.param({
                runId: runId,
                index: index
            }),
            onAfterClose: function () {
                Gikam.getComp('emc-run-edit-list-sample-test-grid').refresh({
                    url: IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                    requestData: {
                        runId_SEQ: runId
                    }
                });
            }
        });
    },

    onTestRunActive: function (row) {
        Gikam.getComp('emc-run-edit-list-sample-test-grid').refresh({
            url: IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
            requestData: {
                runId_SEQ: row.id
            }
        });

    },

    rowIndex: null,
    getSampleTestGrid: function () {
        var _this = this;
        return {
            type: 'tab',
            fill: true,
            panels: [{
                title: 'EMC.MODULE.BP.RESULT.RUNS.TAB.TSAMPLE_TEST',
                items: [{
                    type: 'grid',
                    id: 'emc-run-edit-list-sample-test-grid',
                    service: 'emcOrderTaskServiceImpl',
                    columns: emcRun.getSampleTestColumns(),
                    requestData: {
                        status_SEQ: 'logged'
                    },
                    rowStyleFormatter: function (row) {
                        // 对于添加了虚拟测试的样品，用底色进行区分
                        if (row.ext$.originalorder === '1') {
                            return {
                                backgroundColor: 'yellow'
                            }
                        }
                    },
                    deleteFormatter: function (row) {
                        return row.cupNo || row.id;
                    },
                    onLoadSuccess: function (data) {
                        if (!Gikam.isEmpty(data)) {
                            if (!Gikam.isEmpty(_this.rowIndex)) {
                                //this.activeRowByIndex(_this.rowIndex);
                                _this.rowIndex = null;
                            } else {
                                //this.activeRowByIndex(0);
                            }
                        } else {
                            Gikam.cleanCompData(['emc-run-edit-list-result-grid']);
                        }
                    },
                    onBeforeUpdate: function (data) {
                        // 对杯号修改进行特殊处理
                        if (!Gikam.isEmpty(data.cupNo)) {
                            let
                                runId = data.row.runId;
                            let
                                cupNo = data.cupNo;
                            let
                                url = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/update-cupno';
                            workspace.window.showMask(true);
                            Gikam.put(url, Gikam.getJsonWrapper(null, ['emcOrderTaskServiceImpl', [data.row]])).done(function () {
                                Gikam.getComp('emc-run-edit-list-sample-test-grid').refresh({
                                    url: IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                                    requestData: {
                                        runId_SEQ: runId
                                    }
                                });
                            }).always(function () {
                                workspace.window.closeMask();
                            });
                            return false;
                        }

                        // 对仪器采集编号修改进行特殊处理
                        if (!Gikam.isEmpty(data.equiptAcquNo)) {
                            let
                                runId = data.row.runId;
                            let
                                equiptAcquNo = data.equiptAcquNo;
                            let
                                url = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/update-equiptacquno';
                            workspace.window.showMask(true);
                            Gikam.put(url, Gikam.getJsonWrapper(null, ['emcOrderTaskServiceImpl', [data.row]])).done(function () {
                                Gikam.getComp('emc-run-edit-list-sample-test-grid').refresh({
                                    url: IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                                    requestData: {
                                        runId_SEQ: runId
                                    }
                                });
                            }).always(function () {
                                workspace.window.closeMask();
                            });
                            return false;
                        }
                        return true;
                    },
                    toolbar: _this.getSampleTestButton(),
                }]
            }]
        }
    },

    getSampleTestButton: function () {
        var _this = this;
        return [{
            type: 'button',
            id: 'emc-run-edit-list-button-hide',
            text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.HIDE_OTHER',
            icon: 'preview',
            color: 'danger',
            onClick: function () {
                _this.hideOther();
            }
        }, {
            type: 'button',
            id: 'emc-run-edit-list-button-show',
            text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.RESTORE',
            icon: 'move',
            color: 'warning',
            onClick: function () {
                _this.restore();
            }
        }, {
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
            type: 'button',
            text: 'EMC.MODULE.BP.SAMPLE.BUTTON.VIEW_SAMPLE_RECORD',
            icon: 'print',
            onClick: function () {
                var selections = Gikam.getComp('emc-run-edit-list-sample-test-grid').getSelections();
                if (selections.length != 1) {
                    Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                    return;
                }
                Gikam.viewSampleRecord(selections[0].orderId);
            }
        }, {
            type: 'button',
            text: '查看样品交接单',
            icon: 'print',
            onClick: function () {
                var selections = Gikam.getComp('emc-run-edit-list-sample-test-grid').getSelections();
                if (selections.length != 1) {
                    Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                    return;
                }
                Gikam.viewHandOverRecord(selections[0].orderId);
            }
        }, {
            type: 'button',
            text: '查看废气体积',
            icon: 'print',
            onClick: function () {
                var selections = Gikam.getComp('emc-run-edit-list-sample-test-grid').getSelections();
                Gikam.viewReport('feiqitiqi', selections);
            }
        }, {
            type: 'dropDownMenu',
            text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.LINK_QC_ORDER',
            hidden: true,
            items: [{
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.LINK_QC_ORDER',
                icon: 'edit',
                onClick: function () {
                    _this.linkQcOrder();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.UNLINK_QC_ORDER',
                icon: 'cancel',
                onClick: function () {
                    _this.unlinkQcOrder();
                }
            }]
        }, {
            type: 'dropDownMenu',
            text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.LINK_ORDER',
            hidden: true,
            items: [{
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.LINK_ORDER',
                icon: 'edit',
                onClick: function () {
                    _this.linkOrder();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.UNLINK_ORDER',
                icon: 'cancel',
                onClick: function () {
                    _this.unlinkOrder();
                }
            }]
        }, {
            type: 'dropDownMenu',
            text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.COMBINE_ORDER_TASK',
            hidden: true,
            items: [{
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.COMBINE_ORDER_TASK',
                icon: 'edit',
                onClick: function () {
                    _this.combineOrderTask();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.CANCEL_COMBINE_ORDER_TASK',
                icon: 'edit',
                onClick: function () {
                    _this.cancelCombineOrderTask();
                }
            }]
        }, {
            type: 'dropDownMenu',
            text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.COMBINE_ORDER_ITEM_GROUP',
            hidden: true,
            items: [{
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.COMBINE_ORDER_ITEM_GROUP',
                icon: 'edit',
                onClick: function () {
                    _this.combineOrderItemGroup();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.CANCEL_COMBINE_ORDER_ITEM_GROUP',
                icon: 'edit',
                onClick: function () {
                    _this.cancelCombineOrderItemGroup();
                }
            }]
        }, {
            type: 'dropDownMenu',
            text: '编辑分析项组',
            hidden: true,
            items: [{
                type: 'button',
                text: '添加',
                icon: 'edit',
                onClick: function () {
                    _this.addItemGroup();
                }
            }, {
                type: 'button',
                text: '移除',
                icon: 'edit',
                onClick: function () {
                    _this.removeItemGroup();
                }
            }]
        }, {
            type: 'dropDownMenu',
            text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.SHARE_QC_ORDER',
            hidden: true,
            items: [{
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.COPY_QC_ORDER_TO',
                icon: 'edit',
                onClick: function () {
                    _this.copyQcOrderTo();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.REPLACE_QC_ORDER',
                icon: 'edit',
                onClick: function () {
                    _this.replaceQcOrder();
                }
            }]
        }, {
            type: 'button',
            text: '更改折算状态',
            icon: 'edit',
            hidden: true,
            onClick: function () {
                _this.changeIsConvert();
            }
        }, {
            type: 'form',
            autoSave: false,
            fields: emcRun.getUnCompleteField()
        }, {
            type: 'form',
            id: 'emc-run-edit-list-show-dup-form',
            fields: emcRun.getRunShowDupField(),
            hidden: true,
            autoSave: false,
        }]
    },

    hideOther: function () {
        Gikam.getComp('emc-run-edit-list-layout').setSize({
            west: {
                width: 0
            }
        });
        Gikam.getComp('emc-run-edit-list-center-layout').setSize({
            north: {
                height: 0
            }
        });
        Gikam.getComp('emc-run-edit-list-button-show').show();
        Gikam.getComp('emc-run-edit-list-button-hide').hide();
    },

    restore: function () {
        Gikam.getComp('emc-run-edit-list-layout').setSize({
            west: {
                width: '400'
            }
        });
        Gikam.getComp('emc-run-edit-list-center-layout').setSize({
            north: {
                height: '50%'
            }
        });
        Gikam.getComp('emc-run-edit-list-button-show').hide();
        Gikam.getComp('emc-run-edit-list-button-hide').show();
    },

    addQc: function () {
        var _this = this;
        var grid = Gikam.getComp('emc-run-edit-list-sample-test-grid');
        var data = grid.getSelections();
        var row = grid.getActivedRow();
        var runRow = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow();
        if (Gikam.isEmpty(data)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            width: 560,
            title: 'EMC.MODULE.BP.RESULT.RUNS.MODAL.ADD_QC',
            url: '/secure/emc/module/bp/bp/order-tasks/page/run-qc-category' + Gikam.param({
                USECATEGORY_SEQ: 'lab',
                runId: runRow.id,
                analysisDate: runRow.analysisBeginDate
            }),
            onAfterClose: function (rows) {
                if (Gikam.isEmpty(rows[0]) || Gikam.isEmpty(rows)) {
                    return;
                }
                var url = emcRun.baseUrl + '/qc-order-tasks';
                workspace.window.showMask(true);
                Gikam.post(url, Gikam.getJsonWrapper({
                    f: {
                        stockBatchNo: rows[1].stockBatchNo
                    }
                }, ['emcOrderTaskServiceImpl', data], ['emcQcCategoryServiceImpl', rows[0]])).done(function () {
                    grid.refresh({
                        url: IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                        requestData: {
                            runId_SEQ: Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id
                        }
                    }).done(function () {
                        workspace.window.closeMask();
                        _this.rowIndex = grid.getRowIndexById(row.id);
                    }).failed(function () {
                        workspace.window.closeMask();
                    });
                }).always(function () {
                    workspace.window.closeMask();
                })
            }
        });
    },

    delQc: function () {
        var data = Gikam.getComp('emc-run-edit-list-sample-test-grid').getSelections();
        var qcFlag = false;
        data.map(function (e) {
            if (!Gikam.isEmpty(e.qcCategoryId) && e.qcCategoryCode != 'P-DUP' && e.qcCategoryCode != 'P-BLANK') {
                qcFlag = true;
            }
        });
        if (!qcFlag) {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CAN_NOT_DEL');
            return;
        }
        Gikam.getComp('emc-run-edit-list-sample-test-grid').processRows({
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
            Gikam.getComp('emc-run-edit-list-sample-test-grid').refresh({
                url: IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                requestData: {
                    runId_SEQ: Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id
                }
            });
        });
    },

    addSample: function () {
        var grid = Gikam.getComp('emc-run-edit-list-test-run-grid');
        var runId = grid.getActivedRow().id;
        var testId = grid.getActivedRow().testId;
        var methodId = grid.getActivedRow().methodId;
        var orderCategory = grid.getActivedRow().orderCategory;
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
                testId_IN: testId,
                methodId_SEQ: methodId,
                orderCategory_IN: orderCategory
            }),
            onAfterClose: function (rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var url = Gikam.printf(emcRun.baseUrl + '/{runId}/order-tasks', {
                    runId: runId
                });
                workspace.window.showMask(true);
                Gikam.post(url, Gikam.getJsonWrapper(null, ['', rows])).done(function () {
                    Gikam.getComp('emc-run-edit-list-sample-test-grid').refresh({
                        url: IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                        requestData: {
                            runId_SEQ: runId
                        }
                    });
                }).always(function () {
                    workspace.window.closeMask();
                });
            }
        });
    },

    loadResultPage: function (orderId, runId, qcCategoryCode, qcCategoryId) {
        Gikam.create('modal', {
            width: '95%',
            height: '95%',
            url: emcRun.baseUrl + '/page/result' + Gikam.param({
                runId: runId,
                orderId: orderId,
                qcCategoryId: qcCategoryId,
                qcCategoryCode: qcCategoryCode
            }),
        });
    },

    removeSample: function () {
        var rows = Gikam.getComp('emc-run-edit-list-sample-test-grid').getSelections();
        var runId = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id;
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        // 判断是否是质控样，质控样不能使用删除样品，可以删除NA或现场添加的QC
        let removableQcs = ['N/A', 'P-DUP', 'P-BLANK', 'zcsbkb', 'P-YSKB', 'L-Blab', 'P-CF'];
        let nonRemovable = rows.some(item => removableQcs.indexOf(item.qcCategoryCode) < 0);
        if (nonRemovable) {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CAN_NOT_REMOVE_QC');
            return;
        }

        Gikam.getComp('emc-run-edit-list-sample-test-grid').processRows({
            url: emcRun.baseUrl + '/order-tasks',
            confirm: {
                msg: 'EMC.MODULE.BP.RESULT.RUNS.TIP.DEL_SAMPLE',
                title: 'EMC.MODULE.BP.RESULT.RUNS.MODAL.DEL_SAMPLE',
            },
            formatter: function (row) {
                return row.ext$.orderno + row.orderCategory || row.id;
            },
            method: 'del'
        }).done(function (data) {
            console.log(data);
            if (data == 0) {
                Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CANNT_DELETE_UNLOGGED_ORDER_TASK');
            }
            Gikam.getComp('emc-run-edit-list-sample-test-grid').refresh({
                url: IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                requestData: {
                    runId_SEQ: runId
                }
            });
        });
    },

    /** 关联质控样品 */
    linkQcOrder: function () {
        var activedRun = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow();
        var orderCategory = activedRun.orderCategory;
        let relatedRunId = activedRun.relatedRunId || activedRun.id;
        if (Gikam.isEmpty(activedRun)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let selections = Gikam.getComp('emc-run-edit-list-sample-test-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.RESULT.RUNS.MODAL.SELECT_SAMPLE',
            url: Gikam.choose.getConfig('orderTask').url + Gikam.param({
                runId_IN: relatedRunId,
                status_SEQ: 'logged',
                analysisType_SEQ: 'analysis',
                authority_org: '1',
                orderCategory_SEQ: orderCategory,
                qcCategoryCode_SEQ: selections[0].qcCategoryCode,
                orderId_DIFFER: selections[0].orderId,
                single: true
            }),
            onAfterClose: function (rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                let order = {
                    id: selections[0].orderId,
                    ext$: {
                        relatedQcOrderNo: rows[0].ext$.orderno
                    }
                };
                workspace.window.showMask(true);
                Gikam.put(IFM_CONTEXT + '/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, ['emcOrderServiceImpl', [order]])).done(function () {
                    Gikam.getComp('emc-run-edit-list-sample-test-grid').refresh();
                }).always(function () {
                    workspace.window.closeMask();
                });
            }
        });
    },

    /** 解除关联质控样品 */
    unlinkQcOrder: function () {
        let selections = Gikam.getComp('emc-run-edit-list-sample-test-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let orders = selections.map(item => {
            return {
                id: item.orderId,
                ext$: {
                    relatedQcOrderNo: ''
                }
            }
        });

        workspace.window.showMask(true);
        Gikam.put(IFM_CONTEXT + '/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, ['emcOrderServiceImpl', orders])).done(function () {
            Gikam.getComp('emc-run-edit-list-sample-test-grid').refresh();
        }).always(function () {
            workspace.window.closeMask();
        });
    },

    /** 关联样品 */
    linkOrder: function () {
        var activedRun = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow();
        let relatedRunId = activedRun.relatedRunId || activedRun.id;
        if (Gikam.isEmpty(activedRun)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let selections = Gikam.getComp('emc-run-edit-list-sample-test-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.RESULT.RUNS.MODAL.SELECT_SAMPLE',
            url: Gikam.choose.getConfig('order').url + Gikam.param({
                relatedRunId: relatedRunId,
                qcCategoryCode_IN: 'N/A,P-DUP,P-BLANK,P-VOCS,P-YSKB,zcsbkb',
                id_DIFFER: selections[0].orderId,
                id: selections[0].orderId,
            }),
            onAfterClose: function (rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                workspace.window.showMask(true);
                Gikam.postText(emcRun.baseUrl + '/action/link-order', Gikam.getJsonWrapper({
                    relatedRunId: relatedRunId
                }, ['emcOrderTaskServiceImpl', selections], ['emcOrderServiceImpl', rows])).done(function () {

                }).always(function () {
                    workspace.window.closeMask();
                });
            }
        });
    },

    /** 解除关联样品 */
    unlinkOrder: function () {
        let selections = Gikam.getComp('emc-run-edit-list-sample-test-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.confirm('GIKAM.TIP.CONFIRM', 'EMC.MODULE.BP.RESULT.RUNS.TIP.CONFIRM_UNLINK_ORDER', function () {
            workspace.window.showMask(true);
            Gikam.postText(emcRun.baseUrl + '/action/unlink-order', Gikam.getJsonWrapper(null, ['emcOrderTaskServiceImpl', selections])).done(function () {

            }).always(function () {
                workspace.window.closeMask();
            });
        });
    },

    /** 合并样品测试 */
    combineOrderTask: function () {
        var activedRun = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow();
        var orderCategory = activedRun.orderCategory;
        let relatedRunId = activedRun.relatedRunId || activedRun.id;
        if (Gikam.isEmpty(activedRun)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let selections = Gikam.getComp('emc-run-edit-list-sample-test-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.RESULT.RUNS.TITLE.CHOOSE_ORDER_TASK',
            width: '30%',
            url: emcRun.baseUrl + '/page/order-task-choose-list' + Gikam.param({
                runId_IN: relatedRunId,
                status_SEQ: 'logged',
                analysisType_SEQ: 'analysis',
                authority_org: '1',
                orderCategory_IN: orderCategory,
                qcCategoryCode_IN: 'N/A'
            }),
            onAfterClose: function (rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                workspace.window.showMask(true);
                Gikam.postText(emcRun.baseUrl + '/action/combine-order-task', Gikam.getJsonWrapper(null, ['', selections], ['', rows])).done(function () {
                    Gikam.getComp('emc-run-edit-list-sample-test-grid').refresh();
                }).always(function () {
                    workspace.window.closeMask();
                });
            }
        });
    },

    /**
     * 取消合并测试
     */
    cancelCombineOrderTask: function () {
        let selections = Gikam.getComp('emc-run-edit-list-sample-test-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let canCancel = selections.every(item => item.ext$.originalorder === '1');
        if (!canCancel) {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CAN_NOT_CANCEL_COMBINE_TASK');
            return;
        }

        Gikam.confirm('确认操作', 'EMC.MODULE.BP.RESULT.RUNS.TIP.CONFIRM_CANCEL_COMBINE_TASK', function () {
            workspace.window.showMask(true);
            Gikam.postText(emcRun.baseUrl + '/action/cancel-combine-order-task', Gikam.getJsonWrapper(null, ['emcOrderTaskServiceImpl', selections])).done(function () {
                Gikam.getComp('emc-run-edit-list-sample-test-grid').refresh();
            }).always(function () {
                workspace.window.closeMask();
            });
        });
    },

    /** 合并分析项组 */
    combineOrderItemGroup: function () {
        var activedRun = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow();
        var orderCategory = activedRun.orderCategory;
        let relatedRunId = activedRun.relatedRunId || activedRun.id;
        if (Gikam.isEmpty(activedRun)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let selections = Gikam.getComp('emc-run-edit-list-sample-test-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.RESULT.RUNS.TITLE.CHOOSE_ORDER_ITEM_GROUP',
            width: '30%',
            url: emcRun.baseUrl + '/page/order-item-group-choose-list' + Gikam.param({
                runId_IN: relatedRunId,
                status_SEQ: 'logged',
                analysisType_SEQ: 'analysis',
                authority_org: '1',
                orderCategory_IN: orderCategory,
                qcCategoryCode_IN: 'N/A'
            }),
            onAfterClose: function (rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                workspace.window.showMask(true);
                Gikam.postText(emcRun.baseUrl + '/action/combine-order-item-group', Gikam.getJsonWrapper(null, ['', selections], ['', rows])).done(function () {
                    Gikam.getComp('emc-run-edit-list-sample-test-grid').refresh();
                }).always(function () {
                    workspace.window.closeMask();
                });
            }
        });
    },

    /**
     * 取消合并测试
     */
    cancelCombineOrderItemGroup: function () {
        let selections = Gikam.getComp('emc-run-edit-list-sample-test-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let canCancel = selections.every(item => item.ext$.originalorder === '1');
        if (!canCancel) {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CAN_NOT_CANCEL_COMBINE_ORDER_ITEM_GROUP');
            return;
        }

        Gikam.confirm('确认操作', 'EMC.MODULE.BP.RESULT.RUNS.TIP.CONFIRM_CANCEL_COMBINE_ORDER_ITEM_GROUP', function () {
            workspace.window.showMask(true);
            Gikam.postText(emcRun.baseUrl + '/action/cancel-combine-order-item-group', Gikam.getJsonWrapper(null, ['emcOrderTaskServiceImpl', selections])).done(function () {
                Gikam.getComp('emc-run-edit-list-sample-test-grid').refresh();
            }).always(function () {
                workspace.window.closeMask();
            });
        });
    },

    /** 复制质控到 */
    copyQcOrderTo: function () {
        let activedRun = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow();
        let relatedRunId = activedRun.relatedRunId || activedRun.id;
        if (Gikam.isEmpty(activedRun)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let selections = Gikam.getComp('emc-run-edit-list-sample-test-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let nonQc = selections.some(item => !item.qcCategoryId);
        if (nonQc) {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CHOOSE_LAB_QC_ORDER');
            return;
        }

        let otherRunId = relatedRunId.split(',').filter(runId => runId !== activedRun.id).join(',');
        if (!otherRunId) {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.RELATE_RUN_AT_FIRST');
            return;
        }

        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.RESULT.RUNS.TITLE.CHOOSE_RUN',
            width: '80%',
            url: emcRun.baseUrl + '/page/choose-list' + Gikam.param({
                id_IN: otherRunId
            }),
            onAfterClose: function (rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                workspace.window.showMask(true);
                Gikam.postText(emcRun.baseUrl + '/action/copy-qc-order-to', Gikam.getJsonWrapper(null, ['emcOrderTaskServiceImpl', selections], ['emcRunServiceImpl', rows])).done(function () {
                    Gikam.getComp('emc-run-edit-list-sample-test-grid').refresh();
                }).always(function () {
                    workspace.window.closeMask();
                });
            }
        });
    },

    /** 替换质控 */
    replaceQcOrder: function () {
        let activedRun = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow();
        let relatedRunId = activedRun.relatedRunId || activedRun.id;
        if (Gikam.isEmpty(activedRun)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let selections = Gikam.getComp('emc-run-edit-list-sample-test-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let nonQc = selections.some(item => !item.qcCategoryId);
        if (nonQc) {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CHOOSE_LAB_QC_ORDER');
            return;
        }

        let otherRunId = relatedRunId.split(',').filter(runId => runId !== activedRun.id).join(',');
        if (!otherRunId) {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.RELATE_RUN_AT_FIRST');
            return;
        }

        // 如果不是主批，而是被关联的批，则不允许替换质控，防止误操作
        if (!relatedRunId.startsWith(activedRun.id)) {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CAN_NOT_REPLACE_QC_ORDER');
            return;
        }

        let orderIds = selections.map(item => item.orderId).join();
        let qcCategoryCodes = selections.map(item => item.qcCategoryCode).join();

        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.RESULT.RUNS.TITLE.CHOOSE_RUN_ORDER',
            width: '80%',
            url: emcRun.baseUrl + '/page/run-order-choose-list' + Gikam.param({
                relatedRunId: otherRunId,
                qcCategoryCodes: qcCategoryCodes,
                orderIds: orderIds
            }),
            onAfterClose: function (data) {
                if (Gikam.isEmpty(data) || Gikam.isEmpty(data.sourceOrders)) {
                    return;
                }

                let sourceOrders = data.sourceOrders;
                let targetOrders = data.targetOrders;

                let replacedMsg = Gikam.propI18N('EMC.MODULE.BP.RESULT.RUNS.TIP.REPLACE_QC_ORDER');
                let confirmMsg = Gikam.propI18N('EMC.MODULE.BP.RESULT.RUNS.TIP.CONFIRM_REPLACE_QC_ORDER');
                let realMsgs = [];
                sourceOrders.forEach(sourceOrder => {
                    realMsgs.push(Gikam.printf(replacedMsg, {
                        targetOrderNo: sourceOrder.linkedOrderNo,
                        mainOrderNo: sourceOrder.ext$.orderno
                    }));
                });
                let realMsgValue = Gikam.printf(confirmMsg, {replacedMsg: realMsgs.join()});

                Gikam.confirm('确认操作', realMsgValue, function () {
                    workspace.window.showMask(true);
                    Gikam.postText(emcRun.baseUrl + '/action/copy-replace-qc-order', Gikam.getJsonWrapper(null, ['emcOrderTaskServiceImpl', sourceOrders], ['emcOrderTaskServiceImpl', targetOrders])).done(function () {
                        Gikam.getComp('emc-run-edit-list-sample-test-grid').refresh();
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                });
            }
        });
    },

    /** 查看QC关联的样品 */
    viewRelatedOrder: function (orderId, runId) {
        if (!orderId) {
            return;
        }
        if (!runId) {
            return;
        }
        Gikam.postText(emcRun.baseUrl + '/action/select-relate-order', Gikam.getJsonWrapper({
            f: {
                orderId: orderId
            }
        }, [])).done(function (data) {
            console.log(data);
            if (data == '1') {
                console.log("新关联样品");
                Gikam.create('modal', {
                    title: 'EMC.MODULE.BP.RESULT.RUNS.TITLE.RELATED_ORDER',
                    width: '40%',
                    url: emcRun.baseUrl + '/page/qc-related-order-list' + Gikam.param({
                        qcOrderId_SEQ: orderId,
                        taskRunId_SEQ: runId,
                        relatedOrder: true,
                        canDelete: true
                    })
                });
            } else {
                console.log("历史关联样品");
                Gikam.create('modal', {
                    title: 'EMC.MODULE.BP.RESULT.RUNS.TITLE.RELATED_ORDER',
                    width: '40%',
                    url: emcRun.baseUrl + '/page/qc-related-order-list' + Gikam.param({
                        qcOrderId_SEQ: orderId,
                        relatedOrder: true,
                        canDelete: true
                    })
                });
            }
        });

    },

    /** 查看样品被关联的QC样品 */
    viewRelatedQcOrder: function (orderId) {
        if (!orderId) {
            return;
        }

        let activedRun = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow();
        let relatedRunId = activedRun.relatedRunId || activedRun.id;

        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.RESULT.RUNS.TITLE.RELATED_QC_ORDER',
            width: '40%',
            url: emcRun.baseUrl + '/page/qc-related-order-list' + Gikam.param({
                relatedOrderId_SEQ: orderId,
                relatedRunId_IN: relatedRunId + ',',
                canDelete: true
            })
        });
    },

    review: function () {
        var selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group', Gikam.getJsonWrapper({
            f: {
                runId_SEQ: selections[0].id
            }
        }, ['', []])).done(function (data) {
            var folderIds = "";
            data.rows.forEach(function (item) {
                folderIds += item.folderId + ',';
            });
            folderIds = folderIds.substring(0, folderIds.length - 1);

            Gikam.create('modal', {
                title: 'EMC.MODULE.BP.BP.FOLDERS.TITLE.REVIEW',
                url: '/secure/emc/module/bp/bp/folders/page/review' + Gikam.param({
                    folderId: folderIds,
                }),
            });
        });
    },
    solConf: function () {
        var activedRow = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow();
        var rows = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        if (Gikam.isEmpty(activedRow)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.SOLS.MODAL.CALIBRATION_SOL_RECORD',
            url: '/secure/emc/module/bp/sols/page/edit-list' + Gikam.param({
                methodId: activedRow.methodId
            }),
            width: '90%',
            onAfterClose: function (row) {
                if (Gikam.isEmpty(row)) {
                    return;
                }
                workspace.window.showMask(true);
                var url = Gikam.printf(emcRun.baseUrl + '/{id}', {
                    id: rows[0].id,
                });
                row.id = rows[0].id;
                Gikam.put(url, Gikam.getJsonWrapper(null, ['emcRunServiceImpl', [row]])).done(function () {
                    rows.map(function (item) {
                        Gikam.getComp('emc-run-edit-list-test-run-grid').refreshRowById(item.id);
                    });
                }).always(function () {
                    workspace.window.closeMask();
                });
            }
        });
    },

    reagentConf: function () {
        var activedRow = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow();
        var rows = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        if (Gikam.isEmpty(activedRow)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.REAGENTS.MODAL.REAGENT',
            url: '/secure/emc/module/bp/reagents/page/edit-list' + Gikam.param({
                methodId: activedRow.methodId
            }),
            onAfterClose: function (data) {
                if (Gikam.isEmpty(data)) {
                    return;
                }

                var row = {
                    id: activedRow.id,
                    ext$: {
                        mulreagentid: data.map(function (item) {
                            return item.id;
                        }).join(),
                        mulregentname: data.map(function (item) {
                            return item.reagentName;
                        }).join()
                    }
                };

                var url = Gikam.printf(emcRun.baseUrl + '/{id}', {
                    id: activedRow.id,
                });
                workspace.window.showMask(true);
                Gikam.put(url, Gikam.getJsonWrapper(null, ['emcRunServiceImpl', [row]])).done(function () {
                    rows.map(function (item) {
                        Gikam.getComp('emc-run-edit-list-test-run-grid').refreshRowById(item.id);
                    });
                }).always(function () {
                    workspace.window.closeMask();
                });
            }
        });
    },

    /** 关联批次 */
    linkRun: function () {
        let selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        if (Gikam.isEmpty(selections) || selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.RESULT.RUNS.TITLE.CHOOSE_RUN',
            width: '80%',
            url: emcRun.baseUrl + '/page/choose-list' + Gikam.param({
                analysisUserId_SEQ: workspace.user.id,
                id_DIFFER: selections[0].id
            }),
            onAfterClose: function (rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                let mainRunId = selections[0].id;
                let targetRunId = rows.map(item => item.id).join();
                let confirmMsg = Gikam.propI18N('EMC.MODULE.BP.RESULT.RUNS.TIP.CONFIRM_LINK_RUN');
                let realMsgValue = Gikam.printf(confirmMsg, {
                    mainRunId: mainRunId,
                    targetRunId: targetRunId
                });

                Gikam.confirm('确认操作', realMsgValue, function () {
                    rows.forEach(item => selections.push(item));
                    workspace.window.showMask(true);
                    Gikam.postText(emcRun.baseUrl + '/action/link-run', Gikam.getJsonWrapper(null, ['emcRunServiceImpl', selections])).done(function () {
                        Gikam.getComp('emc-run-edit-list-test-run-grid').refresh();
                    }).always(function () {
                        workspace.window.closeMask();
                    })
                });
            }
        });
    },

    /** 解除关联批次 */
    unlinkRun: function () {
        let selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        if (Gikam.isEmpty(selections) || selections.length <= 1) {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CHOOSE_AT_LEAST_TWO_ITEM');
            return;
        }

        let weighingRelatedRunIds = Array.from(new Set(selections.map(row => row.ext$.weighingrelatedrunid)));
        if (weighingRelatedRunIds.length > 1) {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.DIFFERENT_RELATED_RUN_ID');
            return;
        }

        Gikam.confirm('GIKAM.TIP.CONFIRM', 'EMC.MODULE.BP.RESULT.RUNS.TIP.CONFIRM_UNLINK_RUN', function () {
            workspace.window.showMask(true);
            Gikam.postText(emcRun.baseUrl + '/action/unlink-run', Gikam.getJsonWrapper(null, ['emcRunServiceImpl', selections])).done(function () {
                Gikam.getComp('emc-run-edit-list-test-run-grid').refresh();
            }).always(function () {
                workspace.window.closeMask();
            });
        });
    },

    submit: function () {
        var grid = Gikam.getComp('emc-run-edit-list-test-run-grid');
        var selections = grid.getSelections();
        if (grid.validate(grid.getSelections())) {
            workspace.window.showMask(true);
            Gikam.post(emcRun.baseUrl + '/action/validate-result-status', Gikam.getJsonWrapper(null, ['pfRunServiceImpl', selections])).done(function (res) {
                if (res.valid) {
                    Gikam.create('workflow').submit({
                        data: selections,
                        pageObject: emcRun
                    }).done(function () {
                        grid.refresh();
                    });

                    return;
                }

                Gikam.confirm('GIKAM.TIP.CONFIRMSUBMIT', res.message, function () {
                    Gikam.create('workflow').submit({
                        data: selections,
                        pageObject: emcRun
                    }).done(function () {

                    });
                });
            }).always(function () {
                workspace.window.closeMask();
            });
        }
    },

    getRightLayout: function () {
        var _this = this;
        return {
            type: 'layout',
            id: 'emc-run-edit-list-center-layout',
            north: {
                height: '50%',
                items: [_this.getTestRunGrid()]
            },
            center: {
                width: 1000,
                items: [{
                    type: 'layout',
                    center: {
                        items: [_this.getSampleTestGrid()]
                    },
                }]
            }
        }
    },

    unique: function (arr) {
        var res = [];
        var json = {};
        for (var i = 0; i < arr.length; i++) {
            if (!json[arr[i]]) {
                res.push(arr[i]);
                json[arr[i]] = 1;
            }
        }
        return res;
    },


    equiptCurves: function () {
        var selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        if (selections.length > 0) {
            if (selections.length < 2) {
                Gikam.create('modal', {
                    title: 'EMC.MODULE.BP.EQUIPTCRVES.MODAL.EQUIPT_CRVES',
                    width: '80%',
                    height: '80%',
                    url: IFM_CONTEXT + '/secure/emc/module/bp/result/runs/page/equipt-crves',
                });
            } else {
                Gikam.alert('GIKAM.TIP.CHOOSE_MAX_ITEM_IS_ONE');
            }
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    composePdf: function () {
        var selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        var projId = selections[0].projId;
        var reportConfigLineId = selections[0].reportConfigLineId;
        if (selections.length > 0) {
            if (selections.length < 2) {

                if (reportConfigLineId == null || reportConfigLineId == '' || reportConfigLineId == undefined) {
                    Gikam.alert('原始记录模板为空');
                    return;
                }
                var url = Gikam.printf(emcRun.baseUrl + '/action/compose-all-pdf');
                var url1 = Gikam.printf('/secure/emc/module/bp/result/runs/action/select-compose-status');
                Gikam.postText(url1, Gikam.getJsonWrapper(null, ['PfRunService', selections])).done(function (data) {
                    console.log(data);
                    if (data != "ON") {
                        Gikam.alert('原始记录开始合并，请稍等!');
                        Gikam.postText(url, Gikam.getJsonWrapper(null, ['', selections])).done(function (data) {
                            Gikam.alert('合并完成！');
                        });
                    } else {
                        Gikam.alert('原始记录正在合并，请勿重复点击合并！');
                    }
                }).always(function () {
                });

            } else {
                Gikam.alert('GIKAM.TIP.CHOOSE_MAX_ITEM_IS_ONE');
            }
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    resetCompose: function () {
        var selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        Gikam.postText(emcRun.baseUrl + '/action/reset-compose', Gikam.getJsonWrapper(null, ['emcRunServiceImpl', selections])).done(function () {
            Gikam.alert('重置成功');
            Gikam.getComp('emc-run-edit-list-test-run-grid').refresh();
        });
    },


    uploadRunReportFile: function () {
        var _this = this
        var rows = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        if (rows && rows.length === 1) {
            Gikam.create('modal', {
                width: 800,
                height: 500,
                title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ANNEX',
                url: emcRun.baseUrl + '/page/run-annex-list?id=' + rows[0].id
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    runTempHumidity: function () {
        var _this = this
        var rows = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        if (rows && rows.length === 1) {
            //生成温湿度表的数据
            /*            var useUrl = Gikam.printf(emcRun.baseUrl+'/action/create-temp-humidity');
                        Gikam.postText(useUrl, Gikam.getJsonWrapper({
                            f : {
                                runId : rows[0].id
                            }
                        })).done(function(data) {}).always(function() {}); 	*/

            Gikam.create('modal', {
                width: 1000,
                height: 600,
                title: '项目温湿度采集',
                url: emcRun.baseUrl + '/page/run-temp-humidity' + Gikam.param({
                    runId: rows[0].id,
                    org_filter: '1',
                }),
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    runPhysChemicRecord: function () {
        var selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        if (selections.length > 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_MAX_ITEM_IS_ONE');
        }


        Gikam.create('modal', {
            title: '仪器参数',
            url: '/secure/emc/module/bp/result/runs/page/phys-chemic-record' + Gikam.param({
                runId: selections[0].id
            })
        });

    },

    chooseBeforeHandle: function () {
        var _this = this;
        var selection = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        var runId = selection[0].id;
        if (Gikam.isEmpty(selection)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var modal = Gikam.create('modal', {
            title: "选择前处理",
            width: 400,
            height: 270,
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
                            var formData = form.getData();
                            var chooseBeforeHander = formData.chooseBeforeHander;
                            Gikam.postText(emcRun.baseUrl + '/action/choose-beforehandle', Gikam.getJsonWrapper({
                                f: {
                                    runId: runId,
                                    chooseBeforeHander: chooseBeforeHander
                                }
                            }, ['PfRunService', selection])).done(function () {
                                Gikam.getComp('emc-run-edit-list-test-run-grid').refresh();
                                Gikam.getLastModal().close();
                            }).always(function () {
                            });

                            workspace.window.closeMask();
                            Gikam.getComp('emc-run-edit-list-test-run-grid').refresh();
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
            titleWidth: 80,
            fields: emcRun.getChooseBeforeHandleFields(),
            autoSave: false
        });
    },
    addItemGroup: function () {
        var selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        var oselections = Gikam.getComp('emc-run-edit-list-sample-test-grid').getSelections();
        if (Gikam.isEmpty(oselections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        if (oselections.length > 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_MAX_ITEM_IS_ONE');
            return;
        }
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        if (selections.length > 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_MAX_ITEM_IS_ONE');
            return;
        }
        var url = emcRun.baseUrl + '/action/select-itemgroup';
        var runId = selections[0].id;
        Gikam.postText(url, Gikam.getJsonWrapper(
            {
                f: {
                    runId: runId
                }
            }, ['emcOrderTaskServiceImpl', oselections])
        ).done(function (data) {
            var itemgroupids = new Array();
            console.log(data);
            if (data == "" || data == null) {
                Gikam.alert('该样品无需要添加的分析项组');
                return;
            }
            var itemgroupids = data.split(',');
            console.log(itemgroupids);
            Gikam.create('modal', {
                width: '25%',
                height: '70%',
                title: Gikam.choose.getConfig('testItemGroup').title,
                url: Gikam.choose.getConfig('testItemGroup').url + Gikam.param({
                    testMethodId_IN: selections[0].testMethodId,
                    id_IN: itemgroupids
                }),
                onAfterClose: function (rows) {
                    if (Gikam.isEmpty(rows)) {
                        return;
                    }
                    rows.forEach(item => item['itemGroupId'] = item.id);
                    console.log(rows);
                    workspace.window.showMask(true);
                    var url = emcRun.baseUrl + '/action/add-itemgroup';
                    Gikam.postText(url, Gikam.getJsonWrapper({
                            f: {
                                runId: runId
                            }
                        }, ['emcItemGroupServiceImpl', rows]
                        , ['emcOrderTaskServiceImpl', oselections])).done(function (data) {
                        Gikam.getComp('emc-run-edit-list-test-run-grid').refresh();
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            });
        });

    },

    removeItemGroup: function () {
        var selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        var oselections = Gikam.getComp('emc-run-edit-list-sample-test-grid').getSelections();
        if (Gikam.isEmpty(oselections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        if (oselections.length > 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_MAX_ITEM_IS_ONE');
            return;
        }
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        if (selections.length > 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_MAX_ITEM_IS_ONE');
            return;
        }
        var url = emcRun.baseUrl + '/action/select-in-itemgroup';
        var runId = selections[0].id;
        Gikam.postText(url, Gikam.getJsonWrapper({
            f: {
                runId: runId
            }
        }, ['emcOrderTaskServiceImpl', oselections])).done(function (data) {
            var itemgroupids = new Array();
            itemgroupids = data.split(',');
            console.log(data);
            console.log(itemgroupids);
            if (data == "" || data == null) {
                Gikam.alert('该样品无需要移除的分析项组');
                return;
            }
            Gikam.create('modal', {
                width: '25%',
                height: '70%',
                title: Gikam.choose.getConfig('testItemGroup').title,
                url: Gikam.choose.getConfig('testItemGroup').url + Gikam.param({
                    testMethodId_IN: selections[0].testMethodId,
                    id_IN: itemgroupids
                }),
                onAfterClose: function (rows) {
                    if (Gikam.isEmpty(rows)) {
                        return;
                    }
                    rows.forEach(item => item['itemGroupId'] = item.id);
                    console.log(rows);
                    workspace.window.showMask(true);
                    var url = emcRun.baseUrl + '/action/remove-itemgroup';
                    Gikam.postText(url, Gikam.getJsonWrapper({
                            f: {
                                runId: runId
                            }
                        }, ['emcItemGroupServiceImpl', rows]
                        , ['emcOrderTaskServiceImpl', oselections])).done(function (data) {
                        Gikam.getComp('emc-run-edit-list-test-run-grid').refresh();
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            });
        });

    },

    changeIsConvert: function () {
        var selections = Gikam.getComp('emc-run-edit-list-sample-test-grid').getSelections();
        var url = IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/change-is-convert';
        Gikam.postText(url, Gikam.getJsonWrapper({}, ['emcOrderTaskServiceImpl', selections])).done(function () {
            console.log('done');
            Gikam.getComp('emc-run-edit-list-sample-test-grid').refresh();
        });

    },

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-run-edit-list-layout',
            renderTo: workspace.window.$dom,
            west: {
                width: '450',
                items: [{
                    type: 'tab',
                    id: 'emc-run-edit-list-west-tab',
                    fill: true,
                    panels: [{
                        title: 'EMC.MODULE.BP.RESULT.RUNS.TAB.TO_HANDLE_TEST',
                        items: [_this.getGridParam()]
                    }]
                }]
            },
            center: {
                items: [this.getRightLayout()]
            }
        });
    },


    init: function () {
        this.create();
        Gikam.jQuery(function () {
            Gikam.getComp('emc-run-edit-list-button-show').hide();
        });
    }
};
