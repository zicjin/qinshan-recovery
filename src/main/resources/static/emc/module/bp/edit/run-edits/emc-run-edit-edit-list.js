emcRunEdit.editPage = {

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
                    url : emcRunEdit.runUrl + '/queries/all',

                    initRequest : true,
                    refreshFromSearch : false,
                    filterOpen : true,
                    onRendered : function() {
                        this.hideColumns([ 'temperature' ]);
                        this.hideColumns([ 'humidity' ]);
                        this.hideColumns([ 'analysisBeginDate' ]);
                        this.hideColumns([ 'analysisBeginTime' ]);
                        this.hideColumns([ 'analysisEndDate' ]);
                        this.hideColumns([ 'analysisEndTime' ]);
                    },
                    requestData : {
                        analysisUserId_SC : workspace.user.id,
        	            orgId_SB : workspace.user.ext$.parentorgid
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
                                url : emcRunEdit.baseUrl + '/page/cond/query',
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
                        type : 'dropDownMenu',
                        text : '原始记录',
                        items : [{
                            type : 'button',
                            text : '查看原始记录',
                            icon : 'print',
                            onClick : function() {
                                _this.runViewRun();
                            }
                        },{
                            type : 'button',
                            text : '查看标定记录',
                            icon : 'default',
                            onClick : function() {
                                _this.runViewRunSol();
                            }
                        }, {
                            type : 'button',
                            text : '查看称重记录',
                            icon : 'print',
                            onClick : function() {
                                Gikam.viewReport('analReport', Gikam.getComp('emc-run-edit-edit-list-test-run-grid').getSelections());
                            }
                        },{
                            type : 'button',
                            text : '合并原始记录',
                            icon : 'default',
                            onClick : function() {
                                _this.composePdf();
                            },
                        },{
                            type : 'button',
                            text : '重置合并',
                            icon : 'refresh',
                            onClick : function() {
                                _this.resetCompose();
                            }
                        },{
                            type : 'button',
                            text : '原始记录文件',
                            icon : 'upload',
                            onClick : function() {
                                _this.uploadRunReportFile();
                            }
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.RECOMPOSE_RUN_PDF',
                            icon : 'revert',
                            onClick : function() {
                                _this.recomposeRunPdf();
                            }
                        }]
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.ATLAS_LOOK',
                        icon : 'preview',
                        onClick : function() {
                            _this.loadAtlasByRun();
                        }
                    },{
                        type : 'button',
                        text : '项目温湿度采集',
                        icon : 'edit',
                        onClick : function() {
                            _this.runTempHumidity();
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
                                var runRow = Gikam.getComp('emc-run-edit-edit-list-test-run-grid').getActivedRow();
                                if (Gikam.isEmpty(data)) {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                    return;
                                }
                                Gikam.create('modal', {
                                    width : 500,
                                    title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.ADD_QC',
                                    url : '/secure/emc/module/bp/bp/order-tasks/page/run-qc-category' + Gikam.param({
                                        ADDQC_SEQ : '1',
                                        runId: runRow.id,
                                        analysisDate: runRow.analysisBeginDate
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
                                }).done(function(data) {
                                    console.log(data);
                                    if(data == 0){
                                        Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CANNT_DELETE_UNLOGGED_ORDER_TASK');
                                    }
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
                        type : 'button',
                        text : 'EMC.MODULE.BP.EDIT.RUNEDITS.BUTTON.FIXDUPLICATETASK',
                        icon : 'select',
                        hidden : true,
                        onClick : function() {
                            let data = Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').getSelections();
                            if (Gikam.isEmpty(data)) {
                                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                                return;
                            }

                            workspace.window.showMask(true);
                            Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/fix-duplicate-task', Gikam.getJsonWrapper(null, [ '', data ])).done(function() {
                                Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').refresh();
                            }).always(function() {
                                workspace.window.closeMask();
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.TRIGGER_FORMLUA',
                        icon : 'activate',
                        onClick : function() {
                            Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.TRIGGER_FORMLUA', 'EMC.MODULE.BP.RESULT.RUNS.TIP.TRIGGER_FORMLUA', function() {
                                var selections = Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').getSelections();
                                if (Gikam.isEmpty(selections)) {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                    return;
                                }
                                var url = emcRunEdit.runUrl + '/action/trigger-formlua';
                                workspace.window.showMask(true);
                                Gikam.postText(url, Gikam.getJsonWrapper(null, [ 'emcOrderTaskServiceImpl', selections ])).done(function(){
                                    Gikam.getComp('emc-run-edit-edit-list-result-grid').refresh();
                                }).always(function() {
                                    workspace.window.closeMask();
                                });
                            });
                        },
                    }, {
                        type : 'button',
                        text : '更改折算状态',
                        icon : 'edit',
                        onClick : function() {
                            _this.changeIsConvert();
                        }
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
        Gikam.postText('/secure/emc/module/mdm/bizmdm/std-set-categorys/field-sets/queries', Gikam.getJsonWrapper({
            f : {
                qcCategoryId : row.qcCategoryId,
                qcCategoryCode : row.qcCategoryCode
            },
            n : -1,
            s : 50
        })).done(function(str) {
            var jsonArr = JSON.parse(str);
            var arr = [];
            jsonArr.forEach(e => {
                if(e.display=='displayedit') {
                    if(e.fieldName == 'standQty' && row.qcCategoryCode === 'QC-STD') {
                        // 标准值特殊处理
                        arr.push({
                            field : e.fieldName,
                            title : e.fieldTitle,
                            editor : true,
                            width : e.width,
                            sort : false,
                            onChange : function(name, value, index) {
                                let uncertainty = this.getData()[index].rn16;
                                if (Gikam.isNotEmpty(value) && uncertainty) {
                                    this.setData({
                                        index : index,
                                        indexLowLimit : (Number(value) * 10000 - Number(uncertainty) * 10000)/10000,
                                        indexHighLimit : (Number(value) * 10000 + Number(uncertainty) * 10000)/10000,
                                        standQty : value
                                    });
                                    return;
                                }
                                this.setData({
                                    index : index,
                                    indexLowLimit : '',
                                    indexHighLimit : '',
                                    standQty : value
                                });
                                return;
                            },
                        });
                    } else if(e.fieldName == 'rn16' && row.qcCategoryCode === 'QC-STD') {
                        // 不确定度特殊处理
                        arr.push({
                            field : e.fieldName,
                            title : e.fieldTitle,
                            editor : true,
                            width : e.width,
                            sort : false,
                            onChange : function(name, value, index) {
                                let stdValue = this.getData()[index].standQty;
                                if (Gikam.isNotEmpty(value) && stdValue){
                                    this.setData({
                                        index : index,
                                        indexLowLimit : (Number(stdValue)*10000 - Number(value)*10000)/10000,
                                        indexHighLimit : (Number(stdValue)*10000 + Number(value)*10000)/10000,
                                        rn16 : value
                                    });
                                    return;
                                }
                                this.setData({
                                    index : index,
                                    indexLowLimit : '',
                                    indexHighLimit : '',
                                    rn16 : value
                                });
                            },
                        });
                    } else if (e.fieldName === 'rn10' || e.fieldName === 'rn11') {
                        // rn10 : 相对误差修约规则，rn11 : 相对偏差修约规则
                        arr.push({
                            field : e.fieldName,
                            title : e.fieldTitle,
                            editor : true,
                            width : e.width,
                            sort : false,
                            type : 'choose',
                            category : 'rounding-rule',
                            single : true,
                            onAfterChoose : function(back, index) {
                                if (back) {
                                    var item = {};
                                    item['index'] = index;
                                    item[e.fieldName] = back;
                                    this.setData(item);
                                }
                            },
                        });
                    } else if (e.fieldName === 'rn12') {
                        // rn12：加标量单位
                        arr.push({
                            field : e.fieldName,
                            title : e.fieldTitle,
                            editor : true,
                            width : e.width,
                            sort : false,
                            type : 'choose',
                            category : 'unit',
                            single : true,
                            onAfterChoose : function(back, index) {
                                if (back) {
                                    var item = {};
                                    item['index'] = index;
                                    item[e.fieldName] = back[0].unitName;
                                    this.setData(item);
                                }
                            },
                        });
                    } else {
                        arr.push({
                            field : e.fieldName,
                            title : e.fieldTitle,
                            editor : true,
                            width : e.width,
                            sort : false
                        });
                    }
                } else if(e.display=='displaynoedit') {
                    arr.push({
                        field : e.fieldName,
                        title : e.fieldTitle,
                        editor : false,
                        width : e.width,
                        sort : false
                    });
                } else if(Gikam.isEmpty(e.display)) {
                    arr.push({
                        field : e.fieldName,
                        title : e.fieldTitle,
                        width : e.width,
                        sort : false
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
            }).done(function(data){/*
                var grid = Gikam.getComp('emc-run-edit-edit-list-result-grid');
                for(var i = 0; i < data.data.length; i++) {
                    if(data.data[i].scriptFlag == '0' && Gikam.isEmpty(data.data[i].originalResult)) {
                        grid.focusField(i, 'originalResult');
                        break;
                    }
                }
            */});

        });
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

                                            if (data.operatedata == 'line') {

                                                var resultSelections = Gikam.getComp('emc-run-edit-edit-list-result-grid').getSelections();
                                                if (Gikam.isEmpty(resultSelections)) {
                                                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                                    return;
                                                }

                                                resultSelections.forEach(function(item) {
                                                    item.ext$.reason = data.reason;
                                                });

                                                // 在代码请求前打开遮罩，避免遮罩没有关闭
                                                workspace.window.showMask(true);                                                Gikam.put('/secure/emc/module/bp/bp/orders/results/action/set-done', Gikam.getJsonWrapper({}, [ null, resultSelections ])).done(function() {
                                                    Gikam.getComp('emc-run-edit-edit-list-result-grid').refresh();
                                                    Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.UPDATE_SUCCESS');
                                                }).always(function() {
                                                	workspace.window.closeMask();                                                });
                                            } else if (data.operatedata == 'noresult') {

                                                var orderSelections = Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').getSelections();
                                                if (Gikam.isEmpty(orderSelections)) {
                                                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                                    return;
                                                }

                                                orderSelections.forEach(function(item) {
                                                    item.ext$.reason = data.reason;
                                                });
                                                workspace.window.showMask(true);                                                Gikam.put('/secure/emc/module/bp/bp/orders/results/action/set-done', Gikam.getJsonWrapper({}, [ null, resultSelections ])).done(function() {
                                                Gikam.put('/secure/emc/module/bp/bp/orders/action/set-done', Gikam.getJsonWrapper({}, [ null, orderSelections ])).done(function() {
                                                    Gikam.getComp('emc-run-edit-edit-list-result-grid').refresh();
                                                    Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.UPDATE_SUCCESS');
                                                }).always(function() {
                                                	workspace.window.closeMask();                                                });
                                                });
                                            }
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

    runViewRun : function() {
        var selections = Gikam.getComp('emc-run-edit-edit-list-test-run-grid').getSelections();
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

    composePdf : function() {
        var selections = Gikam.getComp('emc-run-edit-edit-list-test-run-grid').getSelections();
        var projId = selections[0].projId;
        var reportConfigLineId = selections[0].reportConfigLineId;
    	if (selections.length > 0) {
        	if (selections.length < 2) {

        		if(reportConfigLineId == null||reportConfigLineId ==''||reportConfigLineId == undefined){
        			Gikam.alert('原始记录模板为空');
        			return;
        		}
                var url = Gikam.printf('/secure/emc/module/bp/result/runs/action/compose-all-pdf');
                var url1 = Gikam.printf('/secure/emc/module/bp/result/runs/action/select-compose-status');
                Gikam.postText(url1, Gikam.getJsonWrapper(null, [ 'PfRunService', selections ])).done(function(data) {
                	console.log(data);
                	if(data!="ON"){
                        Gikam.alert('原始记录开始合并，请稍等!');
                        Gikam.postText(url, Gikam.getJsonWrapper(null, [ '', selections ])).done(function(data){
                            Gikam.alert('合并完成！');
                        });
                	}else{
                        Gikam.alert('原始记录正在合并，请勿重复点击合并！');
                	}
                }).always(function() {});

        	}else {
                Gikam.alert('GIKAM.TIP.CHOOSE_MAX_ITEM_IS_ONE');
            }
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    uploadRunReportFile : function() {
        var _this = this
        var rows = Gikam.getComp('emc-run-edit-edit-list-test-run-grid').getSelections();
        if (rows && rows.length === 1) {
            Gikam.create('modal', {
                width : 800,
                height : 500,
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ANNEX',
                url : '/secure/emc/module/bp/result/runs/page/run-annex-list?id=' + rows[0].id
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    /** 重新合成原始记录PDF */
    recomposeRunPdf : function() {
        var selections = Gikam.getComp('emc-run-edit-edit-list-test-run-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.RECOMPOSE_RUN_PDF', 'EMC.MODULE.BP.RESULT.RUNS.TIP.CONFIRM_RECOMPOSE_RUN_PDF', function() {
            workspace.window.showMask(true);
            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/result/runs/action/recompose-pdf', Gikam.getJsonWrapper({}, [ '', selections ])).done(function () {
                Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.ORIGINAL_RECORD_FILE_IS_GENERATING')
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    loadAtlasByRun : function() {
        let runSelections = Gikam.getComp('emc-run-edit-edit-list-test-run-grid').getSelections();
        if (runSelections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        let orderTaskData = Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').getData();
        let orderTaskIds = orderTaskData.map(item => item.id).join();

        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.RUNS.TAB.LOOK_ATLAS',
            url : '/secure/emc/module/bp/bp/order-tasks/page/atlas' + Gikam.param({
                orderTaskIds : orderTaskIds,
                targetIds : [ 'T_EMC_EQUIPT$' + runSelections[0].id ],
                runId_SEQ : runSelections[0].id
            })
        });
    },

    runTempHumidity : function() {
        var _this = this
        var rows = Gikam.getComp('emc-run-edit-edit-list-test-run-grid').getSelections();
        if (rows && rows.length === 1) {
        	//生成温湿度表的数据
            Gikam.create('modal', {
                width : 1000,
                height : 600,
                title : '项目温湿度采集',
                url : emcRunEdit.runUrl + '/page/edit-run-temp-humidity' +  Gikam.param({
                    runId : rows[0].id,
                    orgId_SB: workspace.user.ext$.parentorgid
                }),
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    runViewRunSol : function() {
        var selections = Gikam.getComp('emc-run-edit-edit-list-test-run-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.viewReport("903396790157376", selections[0]);
    },

    resetCompose : function() {
        var selections = Gikam.getComp('emc-run-edit-edit-list-test-run-grid').getSelections();
        Gikam.postText(emcRunEdit.runUrl + '/action/reset-compose', Gikam.getJsonWrapper(null, [ 'emcRunServiceImpl', selections ])).done(function () {
            Gikam.alert('重置成功');
        	Gikam.getComp('emc-run-edit-edit-list-test-run-grid').refresh();
        });
    },

    changeIsConvert : function(){
        var selections = Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').getSelections();
        var url = IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/change-is-convert';
            Gikam.postText(url, Gikam.getJsonWrapper({
        }, [ 'emcOrderTaskServiceImpl', selections ])).done(function(data) {
                console.log('done');
                Gikam.getComp('emc-run-edit-edit-list-sample-test-grid').refresh();
        });

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
