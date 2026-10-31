emcRunPretreat.editPage = {
    projId : null,
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            fill : true,
            id : 'emc-run-edit-list-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/runnable',
            service : 'emcRunServiceImpl',
            refreshFromSearch : false,
            filterOpen : true,
            page : true,
            pageSize : 100,
            requestData : {
                status_SEQ : 'logged',
                analysisType_SEQ : 'analysis',
                authority_org : '1',
                inputUserId : workspace.user.id,
                userid : workspace.user.id,
                orgId: workspace.user.ext$.parentorgid
            },
            columns : emcRunPretreat.getEditListColumns(),
            onCellClick : function(field, row) {
                field === 'methodName' && _this.loadMethodEdit(row);
            },
            onRendered : function() {
                var grid = Gikam.getComp('emc-run-edit-list-grid');
                grid.refreshGroup({
                    fields : [ 'orderCategory' ],
                    formatter : function(row) {
                        return row.orderCategory;
                    }
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.ADD',
                icon : 'add',
                onClick : function() {
                    var data = Gikam.getComp('emc-run-edit-list-grid').getSelections();
                    if (Gikam.isEmpty(data)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    var testMethodId = data.map(function(e) {
                        return e.testMethodId;
                    });
                    var orderCategory = data.map(function(e) {
                        return e.orderCategory;
                    });
                    orderCategory = _this.unique(orderCategory);
                    Gikam.create('modal', {
                        title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.SELECT_SAMPLE_AND_EQUIPT',
                        url : emcRunPretreat.baseUrl + '/page/add-run-detail' + Gikam.param({
                            testMethodId : testMethodId,
                            orderCategory : orderCategory,
                            projId : _this.projId,
                            org_filter : '1',
                        }),
                        onAfterClose : function(data) {
                            if (Gikam.isEmpty(data[0]) || Gikam.isEmpty(data[1])) {
                                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                return;
                            }
                            workspace.window.showMask(true);
                            Gikam.post(emcRunPretreat.baseUrl, Gikam.getJsonWrapper(null, [ 'emcOrderTaskServiceImpl', data[0] ], [ 'emcEquiptServiceImpl', data[1] ])).done(function() {
                                Gikam.getComp('emc-run-edit-list-grid').refresh();
                                Gikam.getComp('emc-run-pretreat-list-test-run-grid').refresh();
                            }).always(function() {
                                workspace.window.closeMask();
                            });
                        }
                    });
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.FILTER_PROJECT',
                icon : 'preview',
                onClick : function() {
                    Gikam.create('modal', {
                        title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.FILTER_PROJECT',
                        width : '45%',
                        url : Gikam.choose.getConfig('project').url + Gikam.param({
                            targetType : 'analysis',
                            org_filter : '1',
                        }),
                        onAfterClose : function(data) {
                            if (Gikam.isEmpty(data)) {
                                return;
                            }

                            var projectId = data.map(function(e) {
                                return e.id;
                            });

                            _this.projId = projectId.join();
                            Gikam.getComp('emc-run-edit-list-grid').setOptions({
                                requestData : {
                                    projId_IN : _this.projId
                                }
                            });

                            Gikam.getComp('emc-run-edit-list-grid').refresh();

                        }
                    });
                }
            }, {
                type : 'form',
                autoSave : false,
                fields : emcRunPretreat.getPrepareShowAllField()
            } ]
        }
    },

    loadMethodEdit : function(row) {
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.RUNS.TITLE.METHOD_EDIT',
            url : emcRunPretreat.baseUrl + '/page/method-edit' + Gikam.param({
                testId : row.testId,
                methodId : row.methodId,
                orderCategory : row.orderCategory
            }),
            width :'70%',
            onAfterClose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    workspace.window.showMask(true);
                    Gikam.postText(emcRunPretreat.baseUrl + '/action/method-edit', Gikam.getJsonWrapper(null, [ 'emcOrderTaskServiceImpl', rows[0] ], [ 'emcMonitorSchemeServiceImpl', rows[1] ])).done(function() {
                        Gikam.getComp('emc-run-edit-list-grid').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    getTestRunGrid : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.RESULT.RUNS.TAB.TEST_RUN',
                items : [ {
                    type : 'grid',
                    id : 'emc-run-pretreat-list-test-run-grid',
                    url : emcRunPretreat.baseUrl + '/queries/raw',
                    service : 'emcRunServiceImpl',
                    columns : emcRunPretreat.getTestRunColumns(),
                    onCellClick : function(field, row) {
                        field === 'id' && _this.loadRunDetail(row.id);
                    },
                    deleteFormatter : function(row) {
                        return row.id;
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        } else {
                            Gikam.getComp('emc-run-pretreat-list-sample-test-grid').cleanData();
                        }
                    },
                    requestData : {
                        org_filter : '1',
                        pretreatUserId_SEQ : workspace.user.id,
                    },
                    onRowActive : function(index, row) {
                        _this.onTestRunActive(row)
                    },
                    toolbar : _this.getRunButton()
                } ]
            } ]
        }
    },

    getRunButton : function() {
        var _this = this;
        return [ {
            type : 'button',
            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.COMPLETE_PRETREAT',
            icon : 'submit',
            onClick : function() {
                _this.completePretreat();
            }
        }, {
            type : 'form',
            id : 'emc-run-edit-list-form',
            fields : emcRunPretreat.getRunShowAllField(),
            autoSave : false,
        }  ]
    },

    completePretreat : function() {
        var selections = Gikam.getComp('emc-run-pretreat-list-test-run-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.COMPLETE_PRETREAT', 'EMC.MODULE.BP.RESULT.RUNS.TIP.COMPLETE_PRETREAT', function() {
            Gikam.postText(emcRunPretreat.baseUrl + '/action/complete-pretreat', Gikam.getJsonWrapper(null, [ '', selections ])).done(function(r) {
                Gikam.getComp('emc-run-pretreat-list-test-run-grid').refresh();
            });
        });
    },

    runDelete : function () {
        Gikam.getComp('emc-run-pretreat-list-test-run-grid').deleteRows(emcRunPretreat.baseUrl).done(function() {
            Gikam.getComp('emc-run-edit-list-grid').refresh();
            Gikam.getComp('emc-run-pretreat-list-test-run-grid').refresh();
        });
    },

    runInitializec : function() {
        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.MODAL.INITIALIZEC', 'EMC.MODULE.BP.RESULT.RUNS.TIP.INITIALIZEC', function() {
            var row = Gikam.getComp('emc-run-pretreat-list-test-run-grid').getActivedRow();
            if (Gikam.isEmpty(row)) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                return;
            }
            var url = emcRunPretreat.baseUrl + '/action/init';
            workspace.window.showMask(true);
            Gikam.put(url, Gikam.getJsonWrapper(null, [ '', [ row ] ])).done(function() {
                var testActivedRow = Gikam.getComp('emc-run-pretreat-list-sample-test-grid').getActivedRow();
                if (testActivedRow && !Gikam.isEmpty(testActivedRow.id)) {
                    Gikam.getComp('emc-run-edit-list-result-grid').refresh({
                        url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/queries',
                        requestData : {
                            orderId_SEQ : Gikam.getComp('emc-run-pretreat-list-sample-test-grid').getActivedRow().orderId
                        }
                    });
                }
            }).always(function () {
                workspace.window.closeMask();
            });
        });
    },

    runUpdatedefaultresults : function() {
        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.MODAL.INITIALIZEC', 'EMC.MODULE.BP.RESULT.RUNS.TIP.UPDATEDEFAULTRESULTS', function() {
            var row = Gikam.getComp('emc-run-pretreat-list-test-run-grid').getActivedRow();
            if (Gikam.isEmpty(row)) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                return;
            }
            var url = emcRunPretreat.baseUrl + '/action/updatedefaultresults';
            workspace.window.showMask(true);
            Gikam.put(url, Gikam.getJsonWrapper(null, [ '', [ row ] ])).done(function() {
                var id = Gikam.getComp('emc-run-pretreat-list-sample-test-grid').getActivedRow().id;
                workspace.window.closeMask();
                if (!Gikam.isEmpty(id)) {
                    Gikam.getComp('emc-run-edit-list-result-grid').refresh({
                        url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/queries',
                        requestData : {
                            orderId_SEQ : Gikam.getComp('emc-run-pretreat-list-sample-test-grid').getActivedRow().orderId
                        }
                    });
                }
            });
        });
    },

    runELN : function() {
        var grid = Gikam.getComp('emc-run-pretreat-list-test-run-grid');

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
            operateType : 1,
            bizId : row.id, // 批ID
            elnTmplId : row.ext$.maxelntmplid, // 模板ID
            IFM_CONTEXT : IFM_CONTEXT,
            // 数据源需要的参数 有多少在后面加就行
            runId : row.id, // 批ID
        })
        window.open(elnUrl, '_blank');
    },

    runEquiptUseRecord : function() {
        var selections = Gikam.getComp('emc-run-pretreat-list-test-run-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
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
            title : 'EMC.MODULE.BP.BPS.MODAL.EQUIPT_RECORD',
            url : '/secure/emc/module/mdm/equipt/equipts/page/use-record-edit-list' + Gikam.param({
                runId_IN : runIds
            })
        });

    },

    runEditEquipt : function() {
        var grid = Gikam.getComp('emc-run-pretreat-list-test-run-grid');
        var rows = grid.getSelections();
        if (rows.length != '1') {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_OPERATE_DATA');
            return;
        }
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.EDIT_EQUIPT',
            url : emcRunPretreat.baseUrl + '/page/edit-equipt' + Gikam.param({
                testMethodId : rows[0].testMethodId,
                equiptId : rows[0].equiptId,
            }),
            onAfterClose : function(data) {
                if (!Gikam.isEmpty(data)) {
                    workspace.window.showMask(true);
                    Gikam.postText(emcRunPretreat.baseUrl + '/action/edit-equipt', Gikam.getJsonWrapper(null, [ 'emcEquiptServiceImpl', data ], [ 'emcRunServiceImpl', rows ])).done(function() {
                        Gikam.getComp('emc-run-pretreat-list-test-run-grid').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    runBatchInput : function() {
        var grid = Gikam.getComp('emc-run-pretreat-list-test-run-grid');
        var rows = grid.getSelections();
        if (!Gikam.isEmpty(rows)) {
            var row = grid.getActivedRow();
            Gikam.create('modal', {
                width : '60%',
                height : 313,
                title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.BATCH_INPUT',
                url : emcRunPretreat.baseUrl + '/page/batch-input',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        rows.map(function(item) {
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
                        Gikam.put(emcRunPretreat.baseUrl + '/' + row.id, Gikam.getJsonWrapper({}, [ 'emcRunServiceImpl', rows ])).done(function() {
                            rows.map(function(item) {
                                grid.refreshRowById(item.id);
                            });
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
        }
    },

    runViewRun : function() {
        var selections = Gikam.getComp('emc-run-pretreat-list-test-run-grid').getSelections();
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

    runEquiptCollect : function() {
        var rows = Gikam.getComp('emc-run-pretreat-list-test-run-grid').getSelections();
        if (rows && rows.length == 1) {
            var bizId = rows[0].id;
            Gikam.create('simpleUploader', {
                id : 'emc-run-edit-list-test-run-uploader',
                dbTable : 'T_EMC_EQUIPT',
                bizId : bizId,
                multiple : true,
                /*onbeforeFilesUpload : function(fileList) {
                    let isImage = fileList.some(function (file) {
                        return file.type.startsWith('image/');
                    });
                    return !isImage;
                },
                onBeforeUpload : function() {
                    Gikam.postSync(Gikam.printf(IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipts/{id}/action/validate-dcu-config', {
                        id : rows[0].equiptId
                    })).done(function (res) {
                        if (!res.valid) {
                            Gikam.alert(res.message);
                            Gikam.getLastModal().window.closeMask();
                            return false;
                        }

                        return true;
                    });
                },*/
                onAfterClose : function(fileList) {
                    if (!Gikam.isEmpty(fileList)) {
                        workspace.window.showMask(true);
                        var url = Gikam.printf(emcRunPretreat.baseUrl + '/{runId}/equipt-collects', {
                            runId : bizId
                        });
                        fileList.map(function(file) {
                            file.size = 0;
                        });
                        Gikam.put(url , Gikam.getJsonWrapper({}, [ 'coreFileServiceImpl', fileList ])).done(function() {
                            Gikam.getComp('emc-run-pretreat-list-test-run-grid').refresh();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    loadRunDetail : function(itemId) {
        Gikam.create('modal', {
            height : 400,
            url : emcRunPretreat.baseUrl + '/page/run-input-detail' + Gikam.param({
                id : itemId
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-run-pretreat-list-test-run-grid').refreshRowById(itemId);
            }
        });
    },

    updateStaticData : function() {
        var gridData = Gikam.getComp('emc-run-pretreat-list-test-run-grid').getSelections();
        if (gridData.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            width : 1000,
            height : 500,
            title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.UPDATE_STATIC_DATA',
            url : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/tests/page/update-list' + Gikam.param({
                testId : gridData[0].testId,
                methodId : gridData[0].methodId
            })
        });
    },

    dataSummaryRun : function() {
        var gridData = Gikam.getComp('emc-run-pretreat-list-test-run-grid').getSelections();
        if (gridData.length > 0) {
            var ids = "";
            gridData.map(function(item, i) {
                ids += gridData[i].id + ',';
            });
            ids = ids.substring(0, ids.length - 1);
            Gikam.create('modal', {
                width : 1000,
                height : 500,
                title : 'GIKAM.MODAL.DATA_SUMMARY',
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/page/data-summary' + Gikam.param({
                    summaryType : 'run',
                    ids : ids
                })
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    loadFilePage : function() {

        var selections = Gikam.getComp('emc-run-pretreat-list-test-run-grid').getSelections();

        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : 'GIKAM.FILE.ATTACHMENT',
            url : '/secure/emc/module/mdm/file/files/page/view' + Gikam.param({
                id : selections[0].id,
                table : 'T_EMC_RUN',
            }),
        });
    },

    loadResultRunMethodParamPage : function(id) {
        var _this = this;
        Gikam.create('modal', {
            width : '60%',
            title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.METHOD_PARAM_PROPERTY_SET',
            url : '/secure/emc/module/mdm/bizmdm/tests/page/result-run/method-param-detail' + Gikam.param({
                id : id,
            }),
        });
    },

    loadAtlasByRun : function() {

        var runSelections = Gikam.getComp('emc-run-pretreat-list-test-run-grid').getSelections();
        if (runSelections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var orderTaskData = Gikam.getComp('emc-run-pretreat-list-sample-test-grid').getData();
        var orderTaskIds = "";
        orderTaskData.forEach(function(item) {
            orderTaskIds += item.id + ',';
        });

        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.RUNS.TAB.LOOK_ATLAS',
            url : '/secure/emc/module/bp/bp/order-tasks/page/atlas' + Gikam.param({
                orderTaskIds : orderTaskIds,
                targetIds : [ 'T_EMC_EQUIPT$' + runSelections[0].id ],
                runId_SEQ : runSelections[0].id
            })
        });
    },

    loadResultInputPage : function(runId, index) {
        Gikam.create('modal', {
            height : '95%',
            width : '95%',
            title : 'EMC.MODULE.BP.RESULT.RUNS.TAB.TSAMPLE_TEST',
            url : emcRunPretreat.baseUrl + '/page/result-input' + Gikam.param({
                runId : runId,
                index : index
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-run-pretreat-list-sample-test-grid').refresh({
                    url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                    requestData : {
                        runId_SEQ : runId
                    }
                });
            }
        });
    },

    onTestRunActive : function(row) {
        Gikam.getComp('emc-run-pretreat-list-sample-test-grid').refresh({
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
            requestData : {
                runId_SEQ : row.id
            }
        });

    },

    rowIndex : null,
    getSampleTestGrid : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.RESULT.RUNS.TAB.TSAMPLE_TEST',
                items : [ {
                    type : 'grid',
                    id : 'emc-run-pretreat-list-sample-test-grid',
                    service : 'emcOrderTaskServiceImpl',
                    columns : emcRunPretreat.getSampleTestColumns(),
                    requestData : {
                        status_SEQ : 'logged'
                    },
                    deleteFormatter : function(row) {
                        return row.cupNo || row.id;
                    },
                    onLoadSuccess : function(data) {
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
                    onBeforeUpdate : function(data) {
                        // 对杯号修改进行特殊处理
                        if (!Gikam.isEmpty(data.cupNo)) {
                            let
                            runId = data.row.runId;
                            let
                            cupNo = data.cupNo;
                            let
                            url = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/update-cupno';
                            workspace.window.showMask(true);
                            Gikam.put(url, Gikam.getJsonWrapper(null, [ 'emcOrderTaskServiceImpl', [ data.row ] ])).done(function() {
                                Gikam.getComp('emc-run-pretreat-list-sample-test-grid').refresh({
                                    url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                                    requestData : {
                                        runId_SEQ : runId
                                    }
                                });
                            }).always(function() {
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
                            Gikam.put(url, Gikam.getJsonWrapper(null, [ 'emcOrderTaskServiceImpl', [ data.row ] ])).done(function() {
                                Gikam.getComp('emc-run-pretreat-list-sample-test-grid').refresh({
                                    url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                                    requestData : {
                                        runId_SEQ : runId
                                    }
                                });
                            }).always(function() {
                                workspace.window.closeMask();
                            });
                            return false;
                        }
                        return true;
                    },
                    toolbar : _this.getSampleTestButton(),
                } ]
            } ]
        }
    },

    getSampleTestButton : function() {
        var _this = this;
        return [  {
            type : 'button',
            id : 'emc-run-edit-list-button-hide',
            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.HIDE_OTHER',
            icon : 'preview',
            color : 'danger',
            onClick : function() {
                _this.hideOther();
            }
        }, {
            type : 'button',
            id : 'emc-run-edit-list-button-show',
            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.RESTORE',
            icon : 'move',
            color : 'warning',
            onClick : function() {
                _this.restore();
            }
        }, {
            type : 'dropDownMenu',
            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.QC',
            items : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.ADD_QC',
                icon : 'add',
                onClick : function() {
                    _this.addQc();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.DEL_QC',
                icon : 'remove',
                onClick : function() {
                    _this.delQc();
                }
            } ]
        }, {
            type : 'dropDownMenu',
            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.ORDER',
            items : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.ADD_SAMPLE',
                icon : 'add',
                onClick : function() {
                    _this.addSample();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.REMOVE_SAMPLE',
                icon : 'remove',
                onClick : function() {
                    _this.removeSample();
                }
            } ]
        }, {
            type : 'form',
            autoSave : false,
            fields : emcRunPretreat.getUnCompleteField()
        }  ]
    },

    hideOther : function() {
        Gikam.getComp('emc-run-edit-list-layout').setSize({
            west : {
                width : 0
            }
        });
        Gikam.getComp('emc-run-edit-list-center-layout').setSize({
            north : {
                height : 0
            }
        });
        Gikam.getComp('emc-run-edit-list-button-show').show();
        Gikam.getComp('emc-run-edit-list-button-hide').hide();
    },

    restore : function() {
        Gikam.getComp('emc-run-edit-list-layout').setSize({
            west : {
                width : '400'
            }
        });
        Gikam.getComp('emc-run-edit-list-center-layout').setSize({
            north : {
                height : '50%'
            }
        });
        Gikam.getComp('emc-run-edit-list-button-show').hide();
        Gikam.getComp('emc-run-edit-list-button-hide').show();
    },

    addQc : function() {
        var _this = this;
        var grid = Gikam.getComp('emc-run-pretreat-list-sample-test-grid');
        var data = grid.getSelections();
        var row = grid.getActivedRow();
        if (Gikam.isEmpty(data)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            width : 500,
            title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.ADD_QC',
            url : '/secure/emc/module/bp/bp/order-tasks/page/run-qc-category' + Gikam.param({
                USECATEGORY_SEQ : 'lab'
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows[0]) || Gikam.isEmpty(rows)) {
                    return;
                }
                var url = emcRunPretreat.baseUrl + '/qc-order-tasks';
                workspace.window.showMask(true);
                Gikam.post(url, Gikam.getJsonWrapper({
                    f : {
                        stockBatchNo : rows[1].stockBatchNo
                    }
                }, [ 'emcOrderTaskServiceImpl', data ], [ 'emcQcCategoryServiceImpl', rows[0] ])).done(function() {
                    grid.refresh({
                        url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                        requestData : {
                            runId_SEQ : Gikam.getComp('emc-run-pretreat-list-test-run-grid').getActivedRow().id
                        }
                    }).done(function() {
                    	 workspace.window.closeMask();
                        _this.rowIndex = grid.getRowIndexById(row.id);
                    }).failed(function () {
                        workspace.window.closeMask();
                    });
                });
            }
        });
    },

    delQc : function() {
        var data = Gikam.getComp('emc-run-pretreat-list-sample-test-grid').getSelections();
        var qcFlag = false;
        data.map(function(e) {
            if (!Gikam.isEmpty(e.qcCategoryId) && e.qcCategoryCode != 'P-DUP' && e.qcCategoryCode != 'P-BLANK' ) {
                qcFlag = true;
            }
        });
        if (!qcFlag) {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CAN_NOT_DEL');
            return;
        }
        Gikam.getComp('emc-run-pretreat-list-sample-test-grid').processRows({
            url : emcRunPretreat.baseUrl + '/qc-order-tasks',
            confirm : {
                msg : 'EMC.MODULE.BP.RESULT.RUNS.TIP.DEL_QC',
                title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.DEL_QC',
            },
            formatter : function(row) {
                return row.ext$.orderno + row.orderCategory || row.id;
            },
            method : 'del'
        }).done(function() {
            Gikam.getComp('emc-run-pretreat-list-sample-test-grid').refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                requestData : {
                    runId_SEQ : Gikam.getComp('emc-run-pretreat-list-test-run-grid').getActivedRow().id
                }
            });
        });
    },

    addSample : function() {
        var grid = Gikam.getComp('emc-run-pretreat-list-test-run-grid');
        var runId = grid.getActivedRow().id;
        var testId = grid.getActivedRow().testId;
        var methodId = grid.getActivedRow().methodId;
        var orderCategory = grid.getActivedRow().orderCategory;
        if (Gikam.isEmpty(runId)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.SELECT_SAMPLE',
            url : Gikam.choose.getConfig('orderTask').url + Gikam.param({
                runId : 'empty',
                status_SEQ : 'logged',
                analysisType_SEQ : 'analysis',
                authority_org : '1',
                testId_IN : testId,
                methodId_SEQ : methodId,
                orderCategory_SEQ : orderCategory
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var url = Gikam.printf(emcRunPretreat.baseUrl + '/{runId}/order-tasks', {
                    runId : runId
                });
                workspace.window.showMask(true);
                Gikam.post(url, Gikam.getJsonWrapper(null, [ '', rows ])).done(function() {
                    Gikam.getComp('emc-run-pretreat-list-sample-test-grid').refresh({
                        url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                        requestData : {
                            runId_SEQ : runId
                        }
                    });
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    loadResultPage : function(orderId, runId, qcCategoryCode, qcCategoryId) {
        Gikam.create('modal', {
            width : '95%',
            height : '95%',
            url : emcRunPretreat.baseUrl + '/page/result' + Gikam.param({
                runId : runId,
                orderId : orderId,
                qcCategoryId : qcCategoryId,
                qcCategoryCode : qcCategoryCode
            }),
        });
    },

    removeSample : function() {
        var rows = Gikam.getComp('emc-run-pretreat-list-sample-test-grid').getSelections();
        var runId = Gikam.getComp('emc-run-pretreat-list-test-run-grid').getActivedRow().id;
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        // 判断是否是质控样，质控样不能使用删除样品
        var removeFlag = false;
        rows.map(function(e) {
            if (e.qcCategoryCode != 'N/A' && e.qcCategoryCode != 'P-DUP' && e.qcCategoryCode != 'P-BLANK') {
                removeFlag = true;
            }
        });
        if (removeFlag) {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CAN_NOT_REMOVE_QC');
            return;
        }

        Gikam.getComp('emc-run-pretreat-list-sample-test-grid').processRows({
            url : emcRunPretreat.baseUrl + '/order-tasks',
            confirm : {
                msg : 'EMC.MODULE.BP.RESULT.RUNS.TIP.DEL_SAMPLE',
                title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.DEL_SAMPLE',
            },
            formatter : function(row) {
                return row.ext$.orderno + row.orderCategory || row.id;
            },
            method : 'del'
        }).done(function() {
            Gikam.getComp('emc-run-pretreat-list-sample-test-grid').refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                requestData : {
                    runId_SEQ : runId
                }
            });
        });
    },

    review : function() {
        var selections = Gikam.getComp('emc-run-pretreat-list-test-run-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group', Gikam.getJsonWrapper({
            f : {
                runId_SEQ : selections[0].id
            }
        }, [ '', [] ])).done(function(data) {
            var folderIds = "";
            data.rows.forEach(function(item) {
                folderIds += item.folderId + ',';
            });
            folderIds = folderIds.substring(0, folderIds.length - 1);

            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.BP.FOLDERS.TITLE.REVIEW',
                url : '/secure/emc/module/bp/bp/folders/page/review' + Gikam.param({
                    folderId : folderIds,
                }),
            });
        });

    },

    solConf : function() {
        var activedRow = Gikam.getComp('emc-run-pretreat-list-test-run-grid').getActivedRow();
        var rows = Gikam.getComp('emc-run-pretreat-list-test-run-grid').getSelections();
        if (Gikam.isEmpty(activedRow)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.SOLS.MODAL.CALIBRATION_SOL_RECORD',
            url : '/secure/emc/module/bp/sols/page/edit-list' + Gikam.param({
                methodId : activedRow.methodId
            }),
            width : '80%',
            onAfterClose : function(row) {
                if (Gikam.isEmpty(row)) {
                    return;
                }
                workspace.window.showMask(true);
                var url = Gikam.printf(emcRunPretreat.baseUrl + '/{id}', {
                    id : rows[0].id,
                });
                row.id = rows[0].id;
                Gikam.put(url, Gikam.getJsonWrapper(null, [ 'emcRunServiceImpl', [ row ] ])).done(function() {
                    rows.map(function(item) {
                        Gikam.getComp('emc-run-pretreat-list-test-run-grid').refreshRowById(item.id);
                    });
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    reagentConf : function() {
        var activedRow = Gikam.getComp('emc-run-pretreat-list-test-run-grid').getActivedRow();
        var rows = Gikam.getComp('emc-run-pretreat-list-test-run-grid').getSelections();
        if (Gikam.isEmpty(activedRow)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.REAGENTS.MODAL.REAGENT',
            url : '/secure/emc/module/bp/reagents/page/edit-list' + Gikam.param({
                methodId : activedRow.methodId
            }),
            onAfterClose : function(data) {
                if (Gikam.isEmpty(data)) {
                    return;
                }

                var row = {
                    id : activedRow.id,
                    ext$ : {
                        mulreagentid : data.map(function (item) {
                            return item.id;
                        }).join(),
                        mulregentname : data.map(function (item) {
                            return item.reagentName;
                        }).join()
                    }
                };

                var url = Gikam.printf(emcRunPretreat.baseUrl + '/{id}', {
                    id : activedRow.id,
                });
                workspace.window.showMask(true);
                Gikam.put(url, Gikam.getJsonWrapper(null, [ 'emcRunServiceImpl', [ row ] ])).done(function() {
                    rows.map(function(item) {
                        Gikam.getComp('emc-run-pretreat-list-test-run-grid').refreshRowById(item.id);
                    });
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    submit : function() {
        var grid = Gikam.getComp('emc-run-pretreat-list-test-run-grid');
        if (grid.validate(grid.getSelections())) {
            Gikam.create('workflow').submit({
                data : grid.getSelections(),
                pageObject : emcRunPretreat
            }).done(function() {
                grid.refresh();
            });
        }
    },

    getRightLayout : function() {
        var _this = this;
        return {
            type : 'layout',
            id : 'emc-run-edit-list-center-layout',
            north : {
                height : '50%',
                items : [ _this.getTestRunGrid() ]
            },
            center : {
                width : 1000,
                items : [ {
                    type : 'layout',
                    center : {
                        items : [ _this.getSampleTestGrid() ]
                    },
                } ]
            }
        }
    },

    unique : function(arr) {
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

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-run-edit-list-layout',
            renderTo : workspace.window.$dom,
            /*west : {
                width : '350',
                items : [ {
                    type : 'tab',
                    id : 'emc-run-edit-list-west-tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.BP.RESULT.RUNS.TAB.TO_HANDLE_TEST',
                        items : [ _this.getGridParam() ]
                    } ]
                } ]
            },*/
            center : {
                items : [ this.getRightLayout() ]
            }
        });
    },

    init : function() {
        this.create();
        Gikam.jQuery(function() {
            Gikam.getComp('emc-run-edit-list-button-show').hide();
        });
    }
};
