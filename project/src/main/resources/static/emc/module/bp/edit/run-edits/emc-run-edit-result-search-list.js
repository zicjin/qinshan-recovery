emcRunEdit.searchPage = {

    getTestRunGrid : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.RESULT.RUNS.TAB.TEST_RUN',
                items : [ {
                    type : 'grid',
                    id : 'emc-run-edit-edit-list-test-run-grid',
                    service : 'emcRunServiceImpl',
                    columns : emcRunEdit.getTestRunColumns(),
                    page : true,
                    refreshFromSearch : false,
                    requestData : {
                        analysisUserId_SC : workspace.user.id,
                    },
                    deleteFormatter : function(row) {
                        return row.id;
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        } else {
                            Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').cleanData();
                        }
                    },
                    onRowActive : function(index, row) {
                        _this.onTestRunActive(row)
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.SEARCH',
                        icon : 'preview',
                        onClick : function() {
                            Gikam.create('modal', {
                                title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.QUERY_BY_COND',
                                width : '400',
                                height : '350',
                                url : emcRunEdit.resultUrl + '/page/cond/query',
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.INITIALIZEC',
                        icon : 'refresh',
                        onClick : function() {
                            Gikam.getComp('emc-run-edit-edit-list-test-run-grid').processRows({
                                url : emcRunEdit.runUrl + '/action/init',
                                confirm : {
                                    msg : 'EMC.MODULE.BP.RESULT.RUNS.TIP.INITIALIZEC',
                                    title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.INITIALIZEC',
                                },
                                formatter : function(row) {
                                    return row.id;
                                },
                                method : 'put'
                            }).done(function() {
                                var id = Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').getActivedRow().id;
                                if (!Gikam.isEmpty(id)) {
                                    Gikam.getComp('emc-run-edit-edit-list-result-grid').refresh({
                                        url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/queries',
                                        requestData : {
                                            orderId_SEQ : Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').getActivedRow().orderId
                                        }
                                    });
                                }
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.FILE.ATTACHMENT',
                        icon : 'default',
                        onClick : function() {
                            var selections = Gikam.getComp('emc-run-edit-edit-list-test-run-grid').getSelections();

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
                                onAfterClose : function(rows) {
                                    Gikam.getComp('emc-run-edit-edit-list-test-run-grid').refresh();
                                }
                            });
                        }
                    },{
                        type : 'form',
                        autoSave : false,
                        fields : emcRunEdit.getShowAllField()
                    } ]
                } ]
            } ]
        }
    },

    onTestRunActive : function(row) {
        Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').refresh({
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
            requestData : {
                runId_SEQ : row.id
            }
        });

    },

    getSampleTestGrid : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.RESULT.RUNS.TAB.TSAMPLE_TEST',
                items : [ {
                    type : 'grid',
                    id : 'emc-run-edit-edit-list-sample-test-grid',
                    service : 'emcOrderTaskServiceImpl',
                    columns : emcRunEdit.getSampleTestColumns(),
                    deleteFormatter : function(row) {
                        return row.cupNo || row.id;
                    },
                    onLoadSuccess : function(data) {
                        Gikam.getComp('emc-run-edit-edit-list-button-show').hide();
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        } else {
                            Gikam.getComp('emc-run-edit-edit-list-result-grid').cleanData();
                        }
                    },
                    onRowActive : function(index, row) {
                        _this.onSampleTestActive(row)
                    },

                    toolbar : [ {
                        type : 'button',
                        id : 'emc-run-edit-edit-list-button-hide',
                        text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.HIDE_OTHER',
                        icon : 'select',
                        onClick : function() {
                            Gikam.getComp('emc-run-edit-edit-list-layout').setSize({
                                north : {
                                    height : 0
                                }
                            });
                            Gikam.getComp('emc-run-edit-edit-list-button-show').show();
                            Gikam.getComp('emc-run-edit-edit-list-button-hide').hide();
                        }
                    }, {
                        type : 'button',
                        id : 'emc-run-edit-edit-list-button-show',
                        text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.RESTORE',
                        icon : 'select',
                        onClick : function() {
                            Gikam.getComp('emc-run-edit-edit-list-layout').setSize({
                                north : {
                                    height : '50%'
                                }
                            });
                            Gikam.getComp('emc-run-edit-edit-list-button-show').hide();
                            Gikam.getComp('emc-run-edit-edit-list-button-hide').show();
                        }
                    }, {
                        type : 'dropDownMenu',
                        text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.QC',
                        items : [ {
                            type : 'button',
                            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.ADD_QC',
                            icon : 'add',
                            onClick : function() {
                                var data = Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').getSelections();
                                if (Gikam.isEmpty(data)) {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                    return;
                                }
                                Gikam.create('modal', {
                                    width : 500,
                                    title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.ADD_QC',
                                    url : '/secure/emc/module/bp/bp/order-tasks/page/run-qc-category' + Gikam.param({
                                        ADDQC_SEQ : '1'
                                    }),
                                    onAfterClose : function(rows) {
                                        if (Gikam.isEmpty(rows[0]) || Gikam.isEmpty(rows)) {
                                            return;
                                        }
                                        var url = emcRunEdit.runUrl + '/qc-order-tasks';
                                        workspace.window.showMask(true);
                                        Gikam.post(url, Gikam.getJsonWrapper({
                                            f : {
                                                stockBatchNo : rows[1].stockBatchNo
                                            }
                                        }, [ 'emcOrderTaskServiceImpl', data ], [ 'emcQcCategoryServiceImpl', rows[0] ])).done(function() {
                                            Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').refresh({
                                                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                                                requestData : {
                                                    runId_SEQ : Gikam.getComp('emc-run-edit-edit-list-test-run-grid').getActivedRow().id
                                                }
                                            });
                                        }).always(function() {
                                            workspace.window.closeMask();
                                        });
                                    }
                                });
                            }
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.DEL_QC',
                            icon : 'remove',
                            onClick : function() {
                                var data = Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').getSelections();
                                var qcFlag = false;
                                data.map(function(e) {
                                    if (!Gikam.isEmpty(e.qcCategoryId)) {
                                        qcFlag = true;
                                    }
                                });
                                if (!qcFlag) {
                                    Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CAN_NOT_DEL');
                                    return;
                                }
                                Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').processRows({
                                    url : emcRunEdit.runUrl + '/qc-order-tasks',
                                    confirm : {
                                        msg : 'EMC.MODULE.BP.RESULT.RUNS.TIP.DEL_QC',
                                        title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.DEL_QC',
                                    },
                                    formatter : function(row) {
                                        return row.ext$.orderno + row.orderCategory || row.id;
                                    },
                                    method : 'del'
                                }).done(function() {
                                    Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').refresh({
                                        url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                                        requestData : {
                                            runId_SEQ : Gikam.getComp('emc-run-edit-edit-list-test-run-grid').getActivedRow().id
                                        }
                                    });
                                });
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
                                var grid = Gikam.getComp('emc-run-edit-edit-list-test-run-grid');
                                var runId = grid.getActivedRow().id;
                                var testId = grid.getActivedRow().testId;
                                if (Gikam.isEmpty(runId)) {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                    return;
                                }
                                Gikam.create('modal', {
                                    title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.SELECT_SAMPLE',
                                    url : Gikam.choose.getConfig('orderTask').url + Gikam.param({
                                        runId : 'empty',
                                        status_SEQ : 'preLogged',
                                        analysisType_SEQ : 'analysis',
                                        authority_org : '1',
                                        testId_IN : testId
                                    }),
                                    onAfterClose : function(rows) {
                                        if (Gikam.isEmpty(rows)) {
                                            return;
                                        }
                                        var url = Gikam.printf(emcRunEdit.runUrl + '/{runId}/order-tasks', {
                                            runId : runId
                                        });
                                        workspace.window.showMask(true);
                                        Gikam.post(url, Gikam.getJsonWrapper(null, [ '', rows ])).done(function() {
                                            Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').refresh({
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
                            }
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.REMOVE_SAMPLE',
                            icon : 'remove',
                            onClick : function() {
                                var rows = Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').getSelections();
                                var runId = Gikam.getComp('emc-run-edit-edit-list-test-run-grid').getActivedRow().id;
                                if (Gikam.isEmpty(rows)) {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                    return;
                                }

                                Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').processRows({
                                    url : emcRunEdit.runUrl + '/order-tasks',
                                    confirm : {
                                        msg : 'EMC.MODULE.BP.RESULT.RUNS.TIP.DEL_SAMPLE',
                                        title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.DEL_SAMPLE',
                                    },
                                    formatter : function(row) {
                                        return row.ext$.orderno + row.orderCategory || row.id;
                                    },
                                    method : 'del'
                                }).done(function() {
                                    Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').refresh({
                                        url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                                        requestData : {
                                            runId_SEQ : runId
                                        }
                                    });
                                });
                            }
                        } ]
                    }, {
                        type : 'form',
                        autoSave : false,
                        fields : emcRunEdit.getUnCompleteField()
                    } ]
                } ]
            } ]
        }
    },

    onSampleTestActive : function(row) {
    	if(row.qcCategoryCode.toString() != 'N/A'){
            Gikam.postText('/secure/emc/module/mdm/bizmdm/std-set-categorys/field-sets/queries', Gikam.getJsonWrapper({
                f : {
                    qcCategoryCode : row.qcCategoryCode
                },
                n : -1,
                s : 50
            })).done(function(str) {
            	
                var jsonArr = JSON.parse(str);
                var arr = [];
                jsonArr.forEach(e => {
                    if(e.display=='displayedit') {
                        arr.push({
                            field : e.fieldName,
                            title : e.fieldTitle,
                            editor : true,
                            width : e.width,
                        });
                    } else if(e.display=='displaynoedit') {
                        arr.push({
                            field : e.fieldName,
                            title : e.fieldTitle,
                            editor : false,
                            width : e.width,
                        });
                    } else if(Gikam.isEmpty(e.display)) {
                        arr.push({
                            field : e.fieldName,
                            title : e.fieldTitle,
                            width : e.width,
                        });
                    }
                });
                var publicColumns = emcRunEdit.getResultPublicColumns();
                var columns = publicColumns.concat(arr);
                Gikam.getComp('emc-run-edit-edit-list-result-grid').refreshColumns(columns);
                Gikam.getComp('emc-run-edit-edit-list-result-grid').refresh({
                    url : '/secure/emc/module/bp/bp/order-task-results/queries',
                    requestData : {
                        orderId_SEQ : row.orderId,
                        runId_SEQ : row.runId,
                        orderTaskId_SEQ : ''
                    }
                });
            });
        	}else {
        		Gikam.getComp('emc-run-edit-edit-list-result-grid').cleanData();
        	}
    },

    getResultGrid : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.RESULT.RUNS.TAB.RESULT',
                items : [ {
                    type : 'grid',
                    id : 'emc-run-edit-edit-list-result-grid',
                    service : 'emcOrderTaskResultServiceImpl',
                    columns : emcRunEdit.getResultPublicColumns(),
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        }
                    },
                    beforeUpdate : function(data) {
                        if (!Gikam.isEmpty(data.originalResult) && data.row.submitFlag == 1) {

                            Gikam.create('modal', {
                                width : 400,
                                height : 200,
                                title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.TITLE.UPDATE_REASON',
                                url : '/secure/emc/module/bp/bp/order-tasks/page/update-result-reason',
                                onAfterClose : function(reasonData) {
                                    if (!Gikam.isEmpty(reasonData)) {
                                        workspace.window.showMask(true);
                                        var resultLog = {
                                            orderTaskId : data.row.orderTaskId,
                                            resultId : data.row.id,
                                            reason : reasonData.reason,
                                            value : data.originalResult,
                                        };
                                        Gikam.post(Gikam.printf('/secure/emc/module/bp/bp/order-tasks/{id}/result-logs', {
                                            id : data.row.orderTaskId
                                        }), Gikam.getJsonWrapper({
                                            f : {
                                                folderId : data.row.folderId,
                                            }
                                        }, [ '', [ resultLog ] ])).done(function() {
                                            Gikam.getComp('emc-run-edit-edit-list-result-grid').refresh();
                                        }).always(function() {
                                            workspace.window.closeMask();
                                        });
                                    } else {
                                        Gikam.getComp('emc-run-edit-edit-list-result-grid').refresh();
                                    }
                                }
                            });
                            return false;
                        }
                    },
                    toolbar : [ {
                        type : 'dropDownMenu',
                        text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.MORE',
                        items : [ {
                            type : 'button',
                            text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SYNC_TMPL_INFO',
                            icon : 'refresh',
                            onClick : function() {
                                Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.SYNC_TMPL_INFO', 'EMC.MODULE.BP.RESULT.RUNS.TIP.SYNC_TMPL_INFO', function() {
                                    var selections = Gikam.getComp('emc-run-edit-edit-list-result-grid').getSelections();
                                    var data = Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').getSelections();
                                    if (selections.length == 0) {
                                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                                        return;
                                    }
                                    selections.map(function(item) {
                                        item.ext$.analysisType = 'analysis'
                                    });
                                    workspace.window.showMask(true);
                                    Gikam.postText(emcRunEdit.runUrl + '/action/sync-tmpl-infos', Gikam.getJsonWrapper(null, [ 'emcOrderTaskServiceImpl', data ], [ 'emcOrderTaskResultServiceImpl', selections ])).done(function() {
                                        Gikam.getComp('emc-run-edit-edit-list-result-grid').refresh();
                                    }).always(function() {
                                        workspace.window.closeMask();
                                    });
                                });
                            },
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.RUN_INPUT',
                            icon : 'edit',
                            onClick : function() {
                                var data = Gikam.getComp('emc-run-edit-edit-list-result-grid').getSelections();
                                if (Gikam.isEmpty(data)) {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                    return;
                                }
                                var itemId = data.map(function(e) {
                                    return e.itemId;
                                });
                                var grid = Gikam.getComp('emc-run-edit-edit-list-sample-test-grid');
                                Gikam.create('modal', {
                                    title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.SELECT_SAMPLE',
                                    url : Gikam.choose.getConfig('orderTask').url + Gikam.param({
                                        runId_SEQ : grid.getActivedRow().runId,
                                        target_unassigned : '1',
                                        targetTable : 'T_EMC_ORDER_TASK_RESULT',
                                        targetTableIdColumn : 'ORDERTASKID',
                                        targetIdColumn : 'ID',
                                        targetFilterColumn : 'ITEMID',
                                        targetFilterValue : itemId
                                    }),
                                    onAfterClose : function(afterData) {
                                        if (Gikam.isEmpty(afterData)) {
                                            return;
                                        }
                                        workspace.window.showMask(true);
                                        Gikam.post(emcRunEdit.runUrl + '/action/run-input', Gikam.getJsonWrapper(null, [ 'emcOrderTaskResultServiceImpl', data ], [ 'emcOrderTaskServiceImpl', afterData ])).done(function() {
                                            Gikam.getComp('emc-run-edit-edit-list-result-grid').refresh({
                                                url : '/secure/emc/module/bp/bp/order-task-results/queries',
                                                requestData : {
                                                    runId_SEQ : Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').getActivedRow().runId,
                                                    orderId_SEQ : Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').getActivedRow().orderId
                                                }
                                            });
                                        }).always(function() {
                                            workspace.window.closeMask();
                                        });
                                    }
                                });
                            }
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.REMOVE_FOMULA',
                            icon : 'remove',
                            onClick : function() {
                                Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.REMOVE_FOMULA', 'EMC.MODULE.BP.RESULT.RUNS.TIP.REMOVE_FOMULA', function() {
                                    var selections = Gikam.getComp('emc-run-edit-edit-list-result-grid').getSelections();
                                    var data = Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').getSelections();
                                    if (selections.length == 0) {
                                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                                        return;
                                    }
                                    workspace.window.showMask(true);
                                    Gikam.postText(emcRunEdit.runUrl + '/action/remove-fomulas', Gikam.getJsonWrapper(null, [ 'emcOrderTaskServiceImpl', data ], [ 'emcOrderTaskResultServiceImpl', selections ])).done(function() {
                                        Gikam.getComp('emc-run-edit-edit-list-result-grid').refresh({
                                            url : '/secure/emc/module/bp/bp/order-task-results/queries',
                                            requestData : {
                                                runId_SEQ : Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').getActivedRow().runId,
                                                orderId_SEQ : Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').getActivedRow().orderId
                                            }
                                        });
                                    }).always(function() {
                                        workspace.window.closeMask();
                                    });
                                });
                            }
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.INVALID_DATA',
                            icon : 'remove',
                            onClick : function() {
                                var _this = this;
                                var activedRows = Gikam.getComp('emc-run-edit-edit-list-result-grid').getActivedRow();
                                Gikam.create('modal', {
                                    width : 400,
                                    height : 200,
                                    title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.INVALID_DATA',
                                    url : '/secure/emc/module/bp/sample/samples/page/invalid-results',
                                    onAfterClose : function(data) {
                                        if (!Gikam.isEmpty(data)) {
                                            if (data.operatedata == undefined) {
                                                Gikam.alert('GIKAM.TIP.CHOOSE_ONE_OPERATE_DATA');
                                                return;
                                            }
                                            workspace.window.showMask(true);
                                            activedRows.ext$.operateData = data.operatedata;
                                            activedRows.ext$.reason = data.reason;
                                            activedRows.ext$.analysisType = 'analysis'
                                            Gikam.postText('/secure/emc/module/bp/bp/orders/action/set-done-task-results', Gikam.getJsonWrapper({}, [ null, [ activedRows ] ])).done(function() {
                                                Gikam.getComp('emc-run-edit-edit-list-result-grid').refresh();
                                                Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.UPDATE_SUCCESS');
                                            }).always(function() {
                                                workspace.window.closeMask();
                                            });
                                        }
                                    }
                                });
                            }
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.ADD_REPEATE_NUMBER',
                            icon : 'add',
                            onClick : function() {
                                var selections = Gikam.getComp('emc-run-edit-edit-list-result-grid').getSelections();
                                if (selections.length != 1) {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_ONE_DATA');
                                    return;
                                }
                                Gikam.create('modal', {
                                    width : 400,
                                    height : 183,
                                    title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.DATA_INPUT_TASK_RESULT_SET_REPEATENO',
                                    url : '/secure/emc/module/bp/sample/samples/page/repeat-number-set',
                                    onAfterClose : function(data) {
                                        if (!Gikam.isEmpty(data)) {
                                            workspace.window.showMask(true);
                                            selections.map(function(item) {
                                                item.repeatNo = data.repeatNo;
                                            });
                                            Gikam.postText('/secure/emc/module/bp/bp/orders/' + selections[0].id + '/action/add-repreat-nos', Gikam.getJsonWrapper({}, [ null, selections ])).done(function() {
                                                Gikam.getComp('emc-run-edit-edit-list-result-grid').refresh();
                                            }).always(function() {
                                                workspace.window.closeMask();
                                            });
                                        }
                                    }
                                });
                            }
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.DEL_REPEATE_NUMBER',
                            icon : 'remove-row',
                            onClick : function() {
                                Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.DEL_REPEATE_NUMBER', 'EMC.MODULE.BP.RESULT.RUNS.TIP.DEL_REPEATE_NUMBER', function() {
                                    var selections = Gikam.getComp('emc-run-edit-edit-list-result-grid').getSelections();
                                    if (selections.length != 1) {
                                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_DATA');
                                        return;
                                    }
                                    if (selections[0].repeatNo == 1) {
                                        Gikam.alert('GIKAM.TIP.CHOOSE_REPEAT_NO_DATA_GREATER_THAN_ONE');
                                        return;
                                    }
                                    workspace.window.showMask(true);
                                    Gikam.postText('/secure/emc/module/bp/bp/orders/action/delete-repreat-nos', Gikam.getJsonWrapper({}, [ null, selections ])).done(function() {
                                        Gikam.getComp('emc-run-edit-edit-list-result-grid').refresh();
                                    }).always(function() {
                                        workspace.window.closeMask();
                                    });
                                });
                            }
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.EDIT_ITEM',
                            icon : 'edit',
                            onClick : function() {
                                var data = Gikam.getComp('emc-run-edit-edit-list-result-grid').getActivedRow();
                                if (Gikam.isEmpty(data)) {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                    return;
                                }
                                Gikam.create('modal', {
                                    title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.EDIT_ITEM',
                                    url : emcRunEdit.runUrl + '/page/edit-item' + Gikam.param({
                                        id : data.orderTaskId,
                                    }),
                                    onAfterClose : function(afterData) {
                                        if (Gikam.isEmpty(afterData[0]) || Gikam.isEmpty(afterData[1])) {
                                            return;
                                        }
                                        workspace.window.showMask(true);
                                        Gikam.post(emcRunEdit.runUrl + '/edit-item', Gikam.getJsonWrapper(null, [ 'emcOrderTaskResultServiceImpl', [data] ], [ 'emcItemServiceImpl', afterData[0]], ['emcOrderTaskServiceImpl', afterData[1]])).done(function() {
                                            Gikam.getComp('emc-run-edit-edit-list-result-grid').refresh({
                                                url : '/secure/emc/module/bp/bp/order-task-results/queries',
                                                requestData : {
                                                    orderTaskId_SEQ : Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').getActivedRow().id
                                                }
                                            });
                                        }).always(function() {
                                            workspace.window.closeMask();
                                        });
                                    }
                                })
                            }
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.MODIFY_UNIT',
                            icon : 'edit',
                            onClick : function() {
                                var resultRows = Gikam.getComp('emc-run-edit-edit-list-result-grid').getSelections();
                                if (Gikam.isEmpty(resultRows)) {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                    return;
                                }
                                var orderTaskRows = Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').getSelections();
                                Gikam.create('modal', {
                                    title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.MODIFY_UNIT',
                                    url : Gikam.choose.getConfig('unit').url + Gikam.param({
                                        single : true
                                    }),
                                    onAfterClose : function(data) {
                                        if (Gikam.isEmpty(data)) {
                                            return;
                                        }
                                        var unitName = data[0].unitName;
                                        workspace.window.showMask(true);
                                        var url = emcRunEdit.runUrl + '/action/modify-unit';
                                        Gikam.postText(url, Gikam.getJsonWrapper({
                                            f : {
                                                unitName : unitName
                                            }
                                        }, [ 'emcOrderTaskServiceImpl', orderTaskRows ], [ 'emcOrderTaskResultServiceImpl', resultRows ])).done(function() {
                                            Gikam.getComp('emc-run-edit-edit-list-result-grid').refresh({
                                                url : '/secure/emc/module/bp/bp/order-task-results/queries',
                                                requestData : {
                                                    runId_SEQ : Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').getActivedRow().runId,
                                                    orderId_SEQ : Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').getActivedRow().orderId
                                                }
                                            });
                                        }).always(function() {
                                            workspace.window.closeMask();
                                        });
                                    }
                                });
                            }
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.MODIFY_LIMIT',
                            icon : 'edit',
                            onClick : function() {
                                Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.MODIFY_LIMIT', 'EMC.MODULE.BP.RESULT.RUNS.TIP.MODIFY_LIMIT', function() {
                                    var row = Gikam.getComp('emc-run-edit-edit-list-result-grid').getActivedRow();
                                    if (Gikam.isEmpty(row)) {
                                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                        return;
                                    }
                                    workspace.window.showMask(true);
                                    Gikam.postText(emcRunEdit.runUrl + '/action/modify-limit', Gikam.getJsonWrapper(null, ['', [row]] )).done(function() {
                                        Gikam.getComp('emc-run-edit-edit-list-result-grid').refresh({
                                            url : '/secure/emc/module/bp/bp/order-task-results/queries',
                                            requestData : {
                                                runId_SEQ : Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').getActivedRow().runId,
                                                orderId_SEQ : Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').getActivedRow().orderId
                                            }
                                        });
                                    }).always(function() {
                                        workspace.window.closeMask();
                                    });
                                });
                            }
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.METHOD_SOP',
                            icon : 'preview',
                            onClick : function() {
                                var row = Gikam.getComp('emc-run-edit-edit-list-result-grid').getActivedRow();
                                if (Gikam.isEmpty(row)) {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                    return;
                                }
                                Gikam.create('modal', {
                                    title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.METHOD_SOP',
                                    url : emcRunEdit.runUrl +'/page/method-files' + Gikam.param({
                                        methodVersionId : row.methodVersionId,
                                    }),
                                });
                            }
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.HISTORICAL_DATA',
                            icon : 'preview',
                            onClick : function() {
                                var row = Gikam.getComp('emc-run-edit-edit-list-result-grid').getActivedRow();
                                if (Gikam.isEmpty(row)) {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                    return;
                                }
                                Gikam.create('modal', {
                                    title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.HISTORICAL_DATA',
                                    url : emcRunEdit.runUrl +'/page/historical-data' + Gikam.param({
                                        monitorPointNo : row.ext$.monitorpointno,
                                        itemId : row.itemId,
                                        testId : row.testId
                                    }),
                                });
                            }
                        } ]
                    } ]
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-run-edit-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '50%',
                items : [ _this.getTestRunGrid() ]
            },
            center : {
                width : 1000,
                items : [ {
                    type : 'layout',
                    west : {
                        width : '50%',
                        items : [ _this.getSampleTestGrid() ]
                    },
                    center : {
                        width : '50%',
                        items : [ _this.getResultGrid() ]
                    }
                } ]

            }
        });
    },

    init : function() {
        this.create();
    }
};
