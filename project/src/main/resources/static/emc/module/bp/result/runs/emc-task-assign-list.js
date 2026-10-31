emcTaskAssign.editPage = {
    projId : null,
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            fill : true,
            id : 'emc-task-assign-list-grid',
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
                /*inputUserId : workspace.user.id,*/
                /*userid : workspace.user.id,*/
                orgId: workspace.user.ext$.parentorgid,
                samplingSubpackage_IN: ','
            },
            columns : emcTaskAssign.getEditListColumns(),
            onCellClick : function(field, row) {
                field === 'methodName' && _this.loadMethodEdit(row);
            },
            onRendered : function() {
                var grid = Gikam.getComp('emc-task-assign-list-grid');
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
                    var data = Gikam.getComp('emc-task-assign-list-grid').getSelections();
                    if (Gikam.isEmpty(data)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }

                    let methodIdSet = new Set(Array.from(data.map(item => item.methodId)));
                    if (methodIdSet.size !== 1) {
                        Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.METHOD_IS_DIFFERENT');
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
                        url : emcTaskAssign.baseUrl + '/page/add-run-detail' + Gikam.param({
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
                            Gikam.postText(emcTaskAssign.baseUrl, Gikam.getJsonWrapper({
                                projectId : _this.projId
                            }, [ 'emcOrderTaskServiceImpl', data[0] ], [ 'emcEquiptServiceImpl', data[1] ])).done(function() {
                                Gikam.getComp('emc-task-assign-list-grid').refresh();
                                Gikam.getComp('emc-task-assign-list-test-run-grid').refresh();
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
                    var dateEnd = new Date("2022-10-01");
                    Gikam.create('modal', {
                        title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.FILTER_PROJECT',
                        width : '85%',
                        url : Gikam.choose.getConfig('shuttle-project').url + Gikam.param({
                            targetType : 'analysis',
                            org_filter : '1',
                            id : _this.projId,
                            LASTESTACCEPTEDDATE_DGOE :Gikam.DateUtils.formatter(dateEnd, 'yyyy-MM-dd')
                        }),
                        onAfterClose : function(data) {
                            _this.projId = Gikam.isEmpty(data) ? '' : data.map(item => item.id).join();
                            Gikam.getComp('emc-task-assign-list-grid').setOptions({
                                requestData : {
                                    projId_IN : _this.projId
                                }
                            });

                            Gikam.getComp('emc-task-assign-list-grid').refresh();

                        }
                    });
                }
            },{
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('taskAssign', [{
                    	analysisorgid : workspace.user.orgId
                    }]);
                }
            }  ]
        }
    },

    loadMethodEdit : function(row) {
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.RUNS.TITLE.METHOD_EDIT',
            url : emcTaskAssign.baseUrl + '/page/method-edit' + Gikam.param({
                testId : row.testId,
                methodId : row.methodId,
                orderCategory : row.orderCategory
            }),
            width :'70%',
            onAfterClose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    workspace.window.showMask(true);
                    Gikam.postText(emcTaskAssign.baseUrl + '/action/method-edit', Gikam.getJsonWrapper(null, [ 'emcOrderTaskServiceImpl', rows[0] ], [ 'emcMonitorSchemeServiceImpl', rows[1] ])).done(function() {
                        Gikam.getComp('emc-task-assign-list-grid').refresh();
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
                    id : 'emc-task-assign-list-test-run-grid',
                    url : emcTaskAssign.baseUrl + '/queries/raw',
                    service : 'emcRunServiceImpl',
                    columns : emcTaskAssign.getTestRunColumns(),
                    order : {
                        'ext$.adddate' : 'desc',
                        'id' : 'desc'
                    },
                    onCellClick : function(field, row) {
                        field === 'id' && _this.loadRunDetail(row.id);
                    },
                    deleteFormatter : function(row) {
                        return row.id;
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            //this.activeRowByIndex(0);
                        } else {
                            Gikam.getComp('emc-task-assign-list-sample-test-grid').cleanData();
                        }
                    },
                    requestData : {
                        createdById_SEQ : workspace.user.id,
                        org_filter : '1',
                    },
                    onRendered : function() {
                        this.hideColumns([ 'createdTime','ext$.runanalysisorgid' ]);
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
            text : 'GIKAM.BUTTON.DELETE',
            icon : 'remove',
            onClick : function() {
                _this.runDelete();
            }
        },{
            type : 'button',
            text : 'GIKAM.BUTTON.BATCH_INPUT',
            icon : 'edit',
            onClick : function() {
                _this.runBatch();
            }
        } ]
    },

    runDelete : function () {
        Gikam.getComp('emc-task-assign-list-test-run-grid').deleteRows(emcTaskAssign.baseUrl).done(function() {
            Gikam.getComp('emc-task-assign-list-grid').refresh();
            Gikam.getComp('emc-task-assign-list-test-run-grid').refresh();
        });
    },

    loadRunDetail : function(itemId) {
        Gikam.create('modal', {
            height : 400,
            url : emcTaskAssign.baseUrl + '/page/run-input-detail' + Gikam.param({
                runId : itemId
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-task-assign-list-test-run-grid').refreshRowById(itemId);
            }
        });
    },

    onTestRunActive : function(row) {
        Gikam.getComp('emc-task-assign-list-sample-test-grid').refresh({
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
                    id : 'emc-task-assign-list-sample-test-grid',
                    service : 'emcOrderTaskServiceImpl',
                    columns : emcTaskAssign.getSampleTestColumns(),
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
                            Gikam.cleanCompData(['emc-task-assign-list-result-grid']);
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
                                Gikam.getComp('emc-task-assign-list-sample-test-grid').refresh({
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
                                Gikam.getComp('emc-task-assign-list-sample-test-grid').refresh({
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
            id : 'emc-task-assign-list-button-hide',
            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.HIDE_OTHER',
            icon : 'preview',
            color : 'danger',
            onClick : function() {
                _this.hideOther();
            }
        }, {
            type : 'button',
            id : 'emc-task-assign-list-button-show',
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
            fields : emcTaskAssign.getUnCompleteField()
        }  ]
    },

    hideOther : function() {
        Gikam.getComp('emc-task-assign-list-layout').setSize({
            west : {
                width : 0
            }
        });
        Gikam.getComp('emc-task-assign-list-center-layout').setSize({
            north : {
                height : 0
            }
        });
        Gikam.getComp('emc-task-assign-list-button-show').show();
        Gikam.getComp('emc-task-assign-list-button-hide').hide();
    },

    restore : function() {
        Gikam.getComp('emc-task-assign-list-layout').setSize({
            west : {
                width : '400'
            }
        });
        Gikam.getComp('emc-task-assign-list-center-layout').setSize({
            north : {
                height : '50%'
            }
        });
        Gikam.getComp('emc-task-assign-list-button-show').hide();
        Gikam.getComp('emc-task-assign-list-button-hide').show();
    },

    addQc : function() {
        var _this = this;
        var grid = Gikam.getComp('emc-task-assign-list-sample-test-grid');
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
                var url = emcTaskAssign.baseUrl + '/qc-order-tasks';
                workspace.window.showMask(true);
                Gikam.post(url, Gikam.getJsonWrapper({
                    f : {
                        stockBatchNo : rows[1].stockBatchNo
                    }
                }, [ 'emcOrderTaskServiceImpl', data ], [ 'emcQcCategoryServiceImpl', rows[0] ])).done(function() {
                    grid.refresh({
                        url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                        requestData : {
                            runId_SEQ : Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow().id
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
        var data = Gikam.getComp('emc-task-assign-list-sample-test-grid').getSelections();
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
        Gikam.getComp('emc-task-assign-list-sample-test-grid').processRows({
            url : emcTaskAssign.baseUrl + '/qc-order-tasks',
            confirm : {
                msg : 'EMC.MODULE.BP.RESULT.RUNS.TIP.DEL_QC',
                title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.DEL_QC',
            },
            formatter : function(row) {
                return row.ext$.orderno + row.orderCategory || row.id;
            },
            method : 'del'
        }).done(function() {
            Gikam.getComp('emc-task-assign-list-sample-test-grid').refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                requestData : {
                    runId_SEQ : Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow().id
                }
            });
        });
    },

    addSample : function() {
        var grid = Gikam.getComp('emc-task-assign-list-test-run-grid');
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
                var url = Gikam.printf(emcTaskAssign.baseUrl + '/{runId}/order-tasks', {
                    runId : runId
                });
                workspace.window.showMask(true);
                Gikam.post(url, Gikam.getJsonWrapper(null, [ '', rows ])).done(function() {
                    Gikam.getComp('emc-task-assign-list-sample-test-grid').refresh({
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
            url : emcTaskAssign.baseUrl + '/page/result' + Gikam.param({
                runId : runId,
                orderId : orderId,
                qcCategoryId : qcCategoryId,
                qcCategoryCode : qcCategoryCode
            }),
        });
    },

    removeSample : function() {
        var rows = Gikam.getComp('emc-task-assign-list-sample-test-grid').getSelections();
        var runId = Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow().id;
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        // 判断是否是质控样，质控样不能使用删除样品，可以删除NA或现场添加的QC
        let removableQcs = ['N/A', 'P-DUP', 'P-BLANK', 'zcsbkb', 'P-YSKB'];
        let nonRemovable = rows.some(item => removableQcs.indexOf(item.qcCategoryCode) < 0);
        if (nonRemovable) {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CAN_NOT_REMOVE_QC');
            return;
        }

        Gikam.getComp('emc-task-assign-list-sample-test-grid').processRows({
            url : emcTaskAssign.baseUrl + '/order-tasks',
            confirm : {
                msg : 'EMC.MODULE.BP.RESULT.RUNS.TIP.DEL_SAMPLE',
                title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.DEL_SAMPLE',
            },
            formatter : function(row) {
                return row.ext$.orderno + row.orderCategory || row.id;
            },
            method : 'del'
        }).done(function() {
            Gikam.getComp('emc-task-assign-list-sample-test-grid').refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                requestData : {
                    runId_SEQ : runId
                }
            });
        });
    },

    review : function() {
        var selections = Gikam.getComp('emc-task-assign-list-test-run-grid').getSelections();
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
        var activedRow = Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow();
        var rows = Gikam.getComp('emc-task-assign-list-test-run-grid').getSelections();
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
                var url = Gikam.printf(emcTaskAssign.baseUrl + '/{id}', {
                    id : rows[0].id,
                });
                row.id = rows[0].id;
                Gikam.put(url, Gikam.getJsonWrapper(null, [ 'emcRunServiceImpl', [ row ] ])).done(function() {
                    rows.map(function(item) {
                        Gikam.getComp('emc-task-assign-list-test-run-grid').refreshRowById(item.id);
                    });
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    reagentConf : function() {
        var activedRow = Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow();
        var rows = Gikam.getComp('emc-task-assign-list-test-run-grid').getSelections();
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

                var url = Gikam.printf(emcTaskAssign.baseUrl + '/{id}', {
                    id : activedRow.id,
                });
                workspace.window.showMask(true);
                Gikam.put(url, Gikam.getJsonWrapper(null, [ 'emcRunServiceImpl', [ row ] ])).done(function() {
                    rows.map(function(item) {
                        Gikam.getComp('emc-task-assign-list-test-run-grid').refreshRowById(item.id);
                    });
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    submit : function() {
        var grid = Gikam.getComp('emc-task-assign-list-test-run-grid');
        if (grid.validate(grid.getSelections())) {
            Gikam.create('workflow').submit({
                data : grid.getSelections(),
                pageObject : emcTaskAssign
            }).done(function() {
                grid.refresh();
            });
        }
    },

    getRightLayout : function() {
        var _this = this;
        return {
            type : 'layout',
            id : 'emc-task-assign-list-center-layout',
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

    runBatch : function(){
        var _this = this;
        var selections = Gikam.getComp('emc-task-assign-list-test-run-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
            width : 400,
            height : 270,
        });
        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type: 'btnToolbar',
                    items: [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        icon : 'select',
                        onClick : function() {
                            if (form.validate()) {
                                var formData = form.getData();
                                selections.forEach(function (item) {
                                    item.analysisUserId = formData.analysisUserId;
                                    item.analysisUserName = formData.analysisUserName;
                                    item.ext$.weighinguserid = formData.ext$.weighinguserid;
                                    item.ext$.weighingusername = formData.ext$.weighingusername;
                                    item.ext$.pretreatuserid = formData.ext$.pretreatuserid;
                                    item.ext$.pretreatusername = formData.ext$.pretreatusername;
                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(emcTaskAssign.baseUrl + '/' + selections[0].id, Gikam.getJsonWrapper({}, [ 'emcRunServiceImpl', selections ])).done(function (data) {
                                    Gikam.getComp('emc-task-assign-list-test-run-grid').refresh();
                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close();
                                });
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
                        icon : 'cancel',
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
            fields: emcTaskAssign.getRunBatchInputFields(),
            autoSave: false
        });

    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-task-assign-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '350',
                items : [ {
                    type : 'tab',
                    id : 'emc-task-assign-list-west-tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.BP.RESULT.RUNS.TAB.TO_HANDLE_TEST',
                        items : [ _this.getGridParam() ]
                    } ]
                } ]
            },
            center : {
                items : [ this.getRightLayout() ]
            }
        });
    },

    init : function() {
        this.create();
        Gikam.jQuery(function() {
            Gikam.getComp('emc-task-assign-list-button-show').hide();
        });
    }
};
