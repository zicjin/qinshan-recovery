emcRun.resultInputPage = {

    getTestSample : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-run-sample-test-grid',
            service : 'emcOrderTaskServiceImpl',
            columns : emcRun.getSampleTestSecondColumns(),
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
            requestData : {
                status_SEQ : 'logged',
                runId_SEQ : _this.param.runId
            },
            deleteFormatter : function(row) {
                return row.cupNo || row.id;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    if (!Gikam.isEmpty(_this.param.index)) {
                        this.activeRowByIndex(_this.param.index);
                        _this.param.index = null;
                    } else {
                        this.activeRowByIndex(0);
                    }
                } else {
                    Gikam.getComp('emc-run-result-grid').cleanData();
                }
            },
            onRowActive : function(index, row) {
                _this.onSampleTestActive(row)
            },
            onBeforeUpdate : function(data) {
                // 对杯号修改进行特殊处理
                if (!Gikam.isEmpty(data.cupNo)) {
                    let runId = data.row.runId;
                    let cupNo = data.cupNo;
                    let url = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/update-cupno';
                    Gikam.getLastModal().window.showMask(true);
                    Gikam.put(url, Gikam.getJsonWrapper(null, [ 'emcOrderTaskServiceImpl', [ data.row ] ])).done(function() {
                        Gikam.getComp('emc-run-sample-test-grid').refresh({
                            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                            requestData : {
                                runId_SEQ : runId
                            }
                        });
                    }).always(function() {
                        Gikam.getLastModal().window.closeMask();
                    });
                    return false;
                }

                // 对仪器采集编号修改进行特殊处理
                if (!Gikam.isEmpty(data.equiptAcquNo)) {
                    let runId = data.row.runId;
                    let url = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/update-equiptacquno';
                    Gikam.getLastModal().window.showMask(true);
                    Gikam.put(url, Gikam.getJsonWrapper(null, [ 'emcOrderTaskServiceImpl', [ data.row ] ])).done(function() {
                        Gikam.getComp('emc-run-edit-list-sample-test-grid').refresh({
                            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                            requestData : {
                                runId_SEQ : runId
                            }
                        });
                    }).always(function() {
                        Gikam.getLastModal().window.closeMask();
                    });
                    return false;
                }

                // 对标准物质质控编号进行处理
                if (!Gikam.isEmpty(data.stockBatchNo)) {
                    let updateData = {
                        id : data.id,
                        orderId : data.row.orderId,
                        stockBatchNo : data.stockBatchNo,
                        runId : data.row.runId
                    };
                    let url = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/update-batch-no';
                    Gikam.getLastModal().window.showMask(true);
                    Gikam.postText(url, Gikam.getJsonWrapper(null, [ 'emcOrderTaskServiceImpl', [ updateData ] ])).done(function() {
                    }).always(function() {
                        Gikam.getLastModal().window.closeMask();
                    });

                    return false;
                }

                return true;
            },
            toolbar : _this.getSampleButton(),
        }
    },

    getSampleButton : function() {
        var _this = this;
        return [ {
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
            type : 'button',
            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.MOVE_UP',
            icon : 'move-up',
            onClick : function() {
                _this.moveUp();
            }
        }, {
            type : 'button',
            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.MOVE_DOWN',
            icon : 'move-down',
            onClick : function() {
                _this.moveDown();
            }
        }, {
            type : 'dropDownMenu',
            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.MORE',
            id : 'emc-run-button-atlas',
            items : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.TRIGGER_FORMLUA',
                icon : 'activate',
                onClick : function() {
                    _this.triggerFormlua();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.COPY_BLANK_TO',
                icon : 'copy',
                hidden : true,
                onClick : function() {
                    _this.copyBlankTo();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.COPY_BLANK_SPKC_TO',
                icon : 'copy',
                hidden : true,
                onClick : function() {
                    _this.copyBlankSpkcTo();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.BUTTON.VIEW_SAMPLE_RECORD',
                icon : 'print',
                onClick : function() {
                    var selections = Gikam.getComp('emc-run-sample-test-grid').getSelections();
                    if (selections.length != 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    Gikam.viewSampleRecord(selections[0].orderId);
                }
            } ]
        }, {
            type : 'form',
            autoSave : false,
            fields : emcRun.getUnCompleteSecondField()
        } ];
    },

    addQc : function() {
        var _this = this;
        var grid = Gikam.getComp('emc-run-sample-test-grid');
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
                USECATEGORY_SEQ : 'lab',
                runId: row.runId,
                analysisDate: row.ext$.analysisbegindate,
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows[0]) || Gikam.isEmpty(rows)) {
                    return;
                }
                var url = emcRun.baseUrl + '/qc-order-tasks';
                Gikam.getLastModal().window.showMask(true);
                Gikam.post(url, Gikam.getJsonWrapper({
                    f : {
                        stockBatchNo : rows[1].stockBatchNo
                    }
                }, [ 'emcOrderTaskServiceImpl', data ], [ 'emcQcCategoryServiceImpl', rows[0] ])).done(function() {
                    grid.refresh({
                        url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                        requestData : {
                            runId_SEQ : Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id
                        }
                    }).done(function() {
                        _this.param.index = grid.getRowIndexById(row.id);
                    });
                }).always(function() {
                    Gikam.getLastModal().window.closeMask();
                });
            }
        });
    },

    delQc : function() {
        var data = Gikam.getComp('emc-run-sample-test-grid').getSelections();
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
        Gikam.getComp('emc-run-sample-test-grid').processRows({
            url : emcRun.baseUrl + '/qc-order-tasks',
            confirm : {
                msg : 'EMC.MODULE.BP.RESULT.RUNS.TIP.DEL_QC',
                title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.DEL_QC',
            },
            formatter : function(row) {
                return row.ext$.orderno + row.orderCategory || row.id;
            },
            method : 'del'
        }).done(function() {
            Gikam.getComp('emc-run-sample-test-grid').refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                requestData : {
                    runId_SEQ : Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id
                }
            });
        });
    },

    addSample : function() {
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
                var url = Gikam.printf(emcRun.baseUrl + '/{runId}/order-tasks', {
                    runId : runId
                });
                Gikam.getLastModal().window.showMask(true);
                Gikam.post(url, Gikam.getJsonWrapper(null, [ '', rows ])).done(function() {
                    Gikam.getComp('emc-run-sample-test-grid').refresh({
                        url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                        requestData : {
                            runId_SEQ : runId
                        }
                    });
                }).always(function() {
                    Gikam.getLastModal().window.closeMask();
                });
            }
        });
    },

    removeSample : function() {
        var rows = Gikam.getComp('emc-run-sample-test-grid').getSelections();
        var runId = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id;
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        // 判断是否是质控样，质控样不能使用删除样品
        var removeFlag = false;
        rows.map(function(e){
        	if (e.qcCategoryCode != 'N/A' && e.qcCategoryCode != 'P-DUP' && e.qcCategoryCode != 'P-BLANK'&&e.qcCategoryCode != 'P-YSKB') {
                removeFlag = true;
            }
        });
        if (removeFlag) {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CAN_NOT_REMOVE_QC');
            return;
        }

        Gikam.getComp('emc-run-sample-test-grid').processRows({
            url : emcRun.baseUrl + '/order-tasks',
            confirm : {
                msg : 'EMC.MODULE.BP.RESULT.RUNS.TIP.DEL_SAMPLE',
                title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.DEL_SAMPLE',
            },
            formatter : function(row) {
                return row.ext$.orderno + row.orderCategory || row.id;
            },
            method : 'del'
        }).done(function() {
            Gikam.getComp('emc-run-sample-test-grid').refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
                requestData : {
                    runId_SEQ : runId
                }
            });
        });
    },

    onSampleTestActive : function(row) {
        var _this = this,readOnly = Gikam.isTrue(_this.param.readonly);
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
                if(e.display === 'displayedit') {
                    if(e.fieldName === 'standQty' && row.qcCategoryCode === 'QC-STD') {
                        // 标准值特殊处理
                        arr.push({
                            field : e.fieldName,
                            title : e.fieldTitle,
                            editor : readOnly ? false : true,
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
                            formatter : function(i, v ,r) {
                                return Gikam.scientificNotation(v);
                            }
                        });
                    } else if(e.fieldName === 'rn16' && row.qcCategoryCode === 'QC-STD') {
                        // 不确定度特殊处理
                        arr.push({
                            field : e.fieldName,
                            title : e.fieldTitle,
                            editor :  readOnly ? false : true,
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
                            formatter : function(i, v ,r) {
                                return Gikam.scientificNotation(v);
                            }
                        });
                    } else if (e.fieldName === 'rn10' || e.fieldName === 'rn11') {
                        // rn10 : 相对误差修约规则，rn11 : 相对偏差修约规则
                        arr.push({
                            field : e.fieldName,
                            title : e.fieldTitle,
                            editor : readOnly ? false : true,
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
                            editor : readOnly ? false : true,
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
                            editor : readOnly ? false : true,
                            width : e.width,
                            sort : false
                        });
                    }
                } else if(e.display === 'displaynoedit') {
                    let field = {
                        field : e.fieldName,
                        title : e.fieldTitle,
                        editor : false,
                        width : e.width,
                        sort : false
                    };

                    if (e.fieldName === 'avgResult' || e.fieldName === 'rn4') {
                        field['formatter'] = function(i, v , r) {
                            return Gikam.scientificNotation(v);
                        }
                    }

                    arr.push(field);
                } else if(Gikam.isEmpty(e.display)) {
                    arr.push({
                        field : e.fieldName,
                        title : e.fieldTitle,
                        width : e.width,
                        sort : false
                    });
                }
            });
            var publicColumns = readOnly ? emcRun.getReadOnlyResultColumns() : emcRun.getResultPublicColumns();
            var columns = publicColumns.slice(0, 10).concat(arr, publicColumns.slice(10));

            var data = Gikam.getComp('emc-run-sample-test-grid').getSelections();
            var qcFlag1 = false;
            data.map(function(e) {
            	if (!Gikam.isEmpty(e.qcCategoryId) && e.qcCategoryCode != 'P-DUP' && e.qcCategoryCode != 'P-BLANK' ) {
            		qcFlag1 = true;
                }
            });

            if(Gikam.isFalse(_this.param.readonly) || Gikam.isEmpty(_this.param.readonly)){
                if (!qcFlag1) {
                    Gikam.getComp('emc-delete-qc-order-task-button').hide();
                }else{
                    Gikam.getComp('emc-delete-qc-order-task-button').show();
                }
            }

            Gikam.getComp('emc-run-result-grid').refreshColumns(columns);

            Gikam.getComp('emc-run-result-grid').refresh({
                url : '/secure/emc/module/bp/bp/order-task-results/queries',
                requestData : {
                    orderId_SEQ : row.orderId,
                    runId_SEQ : row.runId,
                    orderTaskId_SEQ : ''
                }
            }).done(function(data){
                var grid = Gikam.getComp('emc-run-result-grid');
                for(var i = 0; i < data.data.length; i++) {
                    if(data.data[i].scriptFlag == '0' && Gikam.isEmpty(data.data[i].originalResult)) {
                        grid.focusField(i, 'originalResult');
                        break;
                    }
                }
            });

        });
    },

    /** 杯号上移 */
    moveUp : function() {
        var _this = this;
        var grid = Gikam.getComp('emc-run-sample-test-grid');
        var rows = grid.getSelections();
        if (rows.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_OPERATE_DATA');
            return;
        }
        var index = grid.getCheckedRowIndex()[0];
        if (index === 0) {
            return;
        }
        // 要上移的数据
        var upData = rows[0];
        // 要下移的数据
        var downData = grid.getData()[index - 1];
        var tempNo = upData.cupNo;
        upData.cupNo = downData.cupNo;
        downData.cupNo = tempNo;
        var url = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/update-cupno';
        workspace.window.showMask(true);
        _this.param.index = index - 1;
        Gikam.put(url, Gikam.getJsonWrapper(null, [ 'emcOrderTaskServiceImpl', [ upData, downData] ])).done(function(){
            grid.refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    /** 杯号下移 */
    moveDown : function() {
        var _this = this;
        var grid = Gikam.getComp('emc-run-sample-test-grid');
        var rows = grid.getSelections();
        if (rows.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_OPERATE_DATA');
            return;
        }
        var index = grid.getCheckedRowIndex()[0];
        if (index === grid.getData().length - 1) {
            return;
        }
        // 要下移的数据
        var downData = rows[0];
        // 要上移的数据
        var upData = grid.getData()[index + 1];
        var tempNo = upData.cupNo;
        upData.cupNo = downData.cupNo;
        downData.cupNo = tempNo;
        var url = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/update-cupno';
        workspace.window.showMask(true);
        _this.param.index = index + 1;
        Gikam.put(url, Gikam.getJsonWrapper(null, [ 'emcOrderTaskServiceImpl', [ upData, downData] ])).done(function(){
            grid.refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    triggerFormlua : function() {
        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.TRIGGER_FORMLUA', 'EMC.MODULE.BP.RESULT.RUNS.TIP.TRIGGER_FORMLUA', function() {
            var selections = Gikam.getComp('emc-run-sample-test-grid').getSelections();
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

    /** 复制实验室空白到其他批次 */
    copyBlankTo : function() {
        var selections = Gikam.getComp('emc-run-sample-test-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        if (selections[0].qcCategoryCode !== 'L-BLANK') {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.ONLY_CAN_COPY_L_BLANK');
            return;
        }

        var activedRun = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow();

        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.RUNS.TITLE.CHOOSE_RUN',
            url : emcRun.baseUrl + '/page/copy-blank-to' + Gikam.param({
                targetType : 'copyBlank',
                analysisUserId_SEQ : workspace.user.id,
                testId : activedRun.testId,
                id_DIFFER : activedRun.id,
            }),
            onAfterClose : function(selectedData) {
                if (Gikam.isEmpty(selectedData)) {
                    return;
                }
                Gikam.getLastModal().window.showMask(true);
                Gikam.post(emcRun.baseUrl + '/action/copy-blank-to', Gikam.getJsonWrapper(null, [ null, selections ], [ null, selectedData ])).always(function() {
                    Gikam.getLastModal().window.closeMask();
                })
            }
        });
    },

    /** 复制空白加标到其他批次 */
    copyBlankSpkcTo : function() {
        var selections = Gikam.getComp('emc-run-sample-test-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        if (selections[0].qcCategoryCode !== 'L-SPKCK') {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.ONLY_CAN_COPY_L_BLANK_SPKC');
            return;
        }

        var activedRun = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow();

        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.RUNS.TITLE.CHOOSE_ORDER_TASK',
            url : emcRun.baseUrl + '/page/copy-blank-spkc-to' + Gikam.param({
                targetType : 'copyBlank',
                analysisUserId_SEQ : workspace.user.id,
                testId : activedRun.testId,
                id_DIFFER : activedRun.id,
            }),
            onAfterClose : function(selectedData) {
                if (Gikam.isEmpty(selectedData)) {
                    return;
                }
                Gikam.getLastModal().window.showMask(true);
                Gikam.post(emcRun.baseUrl + '/action/copy-blank-spkc-to', Gikam.getJsonWrapper(null, [ null, selections ], [ null, selectedData ])).always(function() {
                    Gikam.getLastModal().window.closeMask();
                })
            }
        });
    },

    getResult : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.RESULT.RUNS.TAB.RESULT',
                items : [ {
                    type : 'grid',
                    id : 'emc-run-result-grid',
                    service : 'emcOrderTaskResultServiceImpl',
                    columns : emcRun.getResultColumns(),
                    rowStyleFormatter : function(row) {
                        // 对于虚拟出来的测试，用底色进行区分
                        if (row.ext$.virtual === '1') {
                            return {
                                backgroundColor: 'yellow'
                            }
                        }
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        }
                    },
                    beforeUpdate : function(data) {
                        /* 当前版本修改退回的数据不需要再弹出修改原因，故屏蔽之 */
                        /*if (!Gikam.isEmpty(data.originalResult) && data.row.submitFlag == 1) {
                            Gikam.create('modal', {
                                width : 400,
                                height : 200,
                                title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.TITLE.UPDATE_REASON',
                                url : '/secure/emc/module/bp/bp/order-tasks/page/update-result-reason',
                                onAfterClose : function(reasonData) {
                                    if (!Gikam.isEmpty(reasonData)) {
                                        Gikam.getLastModal().window.showMask(true);
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
                                            Gikam.getComp('emc-run-result-grid').refresh();
                                        }).always(function() {
                                            Gikam.getLastModal().window.closeMask();
                                        });
                                    } else {
                                        Gikam.getComp('emc-run-result-grid').refresh();
                                    }
                                }
                            });
                            return false;
                        }*/
                    },
                    onUpdated : function(data, fields, index) {
                        // 判断是否要进行实时公式计算
                        let realTimeCalcFormData = Gikam.getComp('emc-run-result-input-real-time-calc-form').getData();
                        if (realTimeCalcFormData.realTimeCalc !== '1') {
                            return;
                        }

                        var grid = this;

                        var orderTaskRow = Gikam.getComp('emc-run-sample-test-grid').getActivedRow();
                        var qcCategoryCode = orderTaskRow.qcCategoryCode;

                        // 哪位老哥写这么长不分行
                        // if (fields.indexOf('originalResult') > -1 || fields.indexOf('standQty') > -1 || fields.indexOf('lowLimit') > -1 || fields.indexOf('highLimit') > -1 || fields.indexOf('revisionRule') > -1 || (fields.indexOf('rn5') > -1 && (qcCategoryCode === 'QXBD' || qcCategoryCode === 'L-QXCH')) || (fields.indexOf('rn15') > -1 && qcCategoryCode === 'L-SPKC') || (fields.indexOf('rn16') > -1 && qcCategoryCode === 'QC-STD') || fields.indexOf('rn10') > -1 || fields.indexOf('rn11') > -1) {
                        if (fields.indexOf('originalResult') > -1 ||
                            fields.indexOf('standQty') > -1 ||
                            fields.indexOf('lowLimit') > -1 ||
                            fields.indexOf('highLimit') > -1 ||
                            fields.indexOf('revisionRule') > -1 ||
                            fields.indexOf('convertFactor') > -1 ||
                            (fields.indexOf('rn5') > -1 && (qcCategoryCode === 'QXBD' || qcCategoryCode === 'L-QXCH')) ||
                            (fields.indexOf('rn15') > -1 && qcCategoryCode === 'L-SPKC') ||
                            (fields.indexOf('rn16') > -1 && qcCategoryCode === 'QC-STD') ||
                            fields.indexOf('rn10') > -1 ||
                            fields.indexOf('rn11') > -1) {

                            Gikam.getLastModal().window.showMask(true);
                            var orderTaskResultUrl = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results';

                            Gikam.put(orderTaskResultUrl + '/action/change-result', Gikam.getJsonWrapper({}, [ '', [ data ] ])).done(function() {
                                Gikam.post(orderTaskResultUrl + '/queries', Gikam.getJsonWrapper({
                                    n : -1,
                                    f : {
                                        orderId_SEQ : orderTaskRow.orderId,
                                        runId_SEQ : orderTaskRow.runId
                                    }
                                })).done(function(data) {
                                    Gikam.each(data.rows, function(index, row) {
                                        grid.setData(row, false, true);
                                    });

                                }).always(function() {
                                    Gikam.getLastModal().window.closeMask();
                                });

                            }).fail(function() {
                                Gikam.getLastModal().window.closeMask();
                            });
                        }
                    },
                    toolbar : _this.getResultButton()
                } ]
            } ]
        }
    },

    getResultButton : function(){
        var _this = this;
        return [ {
            type : 'button',
            text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SYNC_TMPL_INFO',
            icon : 'refresh',
            onClick : function() {
                _this.syncTmplInfo();
            },
        }, {
            type : 'button',
            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.RUN_INPUT',
            icon : 'edit',
            onClick : function() {
                _this.runInput();
            }
        }, {
            type : 'button',
            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.SINGLE_BATCH_INPUT',
            icon : 'edit',
            onClick : function() {
                _this.batchInput();
            }
        }, {
            type : 'button',
            text : '批录入质控',
            icon : 'edit',
            hidden : true,
            onClick : function() {
                _this.batchNewInput();
            }
        }, {
            type : 'button',
            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.INVALID_DATA',
            icon : 'edit',
            onClick : function() {
                _this.invalidData();
            }
        }, {
            type : 'button',
            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.EDIT_ITEM',
            icon : 'edit',
            onClick : function() {
                _this.batchEditItem();
            }
        }, {
            type : 'button',
            text : '删除质控测试',
            icon : 'edit',
            id : 'emc-delete-qc-order-task-button',
            onClick : function() {
                _this.deleteQcOrderTask();
            }
        }, {
            type : 'button',
            text : '选择检出限',
            icon : 'edit',
            hidden : true,
            onClick : function() {
                _this.chooseLimit();
            }
        }, {
            type : 'dropDownMenu',
            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.QUICK_INPUT',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.EXPORT',
                icon : 'move-up',
                onClick : function() {
                    _this.quickExport();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.IMPORT',
                icon : 'move-down',
                onClick : function() {
                    _this.quickImport();
                }
            } ]
        }, {
            type : 'dropDownMenu',
            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.MORE',
            items : [  {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.REMOVE_FOMULA',
                icon : 'remove',
                onClick : function() {
                    _this.removeFomula();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.ADD_REPEATE_NUMBER',
                icon : 'add',
                onClick : function() {
                    _this.addRepeateNumber();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.DEL_REPEATE_NUMBER',
                icon : 'remove-row',
                onClick : function() {
                    _this.delRepeateNumber();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.MODIFY_UNIT',
                icon : 'edit',
                onClick : function() {
                    _this.modifyUnit();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.MODIFY_LIMIT',
                icon : 'edit',
                onClick : function() {
                    _this.modifyLimit();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.METHOD_SOP',
                icon : 'preview',
                onClick : function() {
                    _this.methodSop();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.HISTORICAL_DATA',
                icon : 'preview',
                onClick : function() {
                    _this.historicalData();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.BATCH_INPUT_RESULT_RULE',
                icon : 'edit',
                onClick : function() {
                    _this.batchInputResultRule();
                }
            } , {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.MODIFY_REVISION',
                icon : 'edit',
                onClick : function() {
                    _this.modifyRevision();
                }
            }, {
                type : 'button',
                text : '同步采样体积',
                icon : 'edit',
                onClick : function() {
                    _this.syncVolume();
                }
            }]
        }, {
            type : 'form',
            autoSave : false,
            id : 'emc-run-result-input-real-time-calc-form',
            fields : [ {
                title : 'EMC.MODULE.BP.RESULT.RUNS.TITLE.REAL_TIME_CALC_FORMULA',
                field : 'realTimeCalc',
                type : 'simpleCheckbox',
                refreshGrid : false,
                styleFormatter : function() {
                    return {
                        paddingLeft : '0'
                    }
                },
                onChange : function(field, value, index) {

                },
            } ]
        } ];
    },

    /** 批录入<检出限 */
    batchInputResultRule : function() {
        var _this = this;
        var resultRows = Gikam.getComp('emc-run-result-grid').getSelections();
        if (Gikam.isEmpty(resultRows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var orderTaskRows = Gikam.getComp('emc-run-sample-test-grid').getSelections();

        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
            width : 360,
            height : 200,
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
                        onClick : function(data) {
                            if (Gikam.isEmpty(data)) {
                                return;
                            }
                            var formData = form.getData();
                            var resultRule = formData.resultRule;
                            var url = emcRun.baseUrl + '/action/batch-input-result-rule';
                            Gikam.getLastModal().window.showMask(true);
                            Gikam.postText(url, Gikam.getJsonWrapper({
                                f : {
                                	resultRule: resultRule
                                }
                            }, [ 'emcOrderTaskServiceImpl', orderTaskRows ],
                            [ 'emcOrderTaskResultServiceImpl', resultRows ])).done(function() {
                                Gikam.getComp('emc-run-result-grid').refresh({
                                    url : '/secure/emc/module/bp/bp/order-task-results/queries',
                                    requestData : {
                                        runId_SEQ : Gikam.getComp('emc-run-sample-test-grid').getActivedRow().runId,
                                        orderId_SEQ : Gikam.getComp('emc-run-sample-test-grid').getActivedRow().orderId
                                    }
                                });
                            }).always(function() {
                                Gikam.getLastModal().window.closeMask();
                                modal.close();
                            });
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
            id : 'emc-result-rule-batch-input-form',
            columns: 1,
            titleWidth: 60,
            fields: [{
                field : 'resultRule',
                title : 'T_EMC_ORDER_TASK_RESULT.RESULTRULE',
                editor : true,
                type : 'select',
                category : 'sys',
                param : {
                    codeCategoryId : 'resultRule'
                },
            }],
            autoSave: false
        });
    },

    syncTmplInfo : function(){
        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.SYNC_TMPL_INFO', 'EMC.MODULE.BP.RESULT.RUNS.TIP.SYNC_TMPL_INFO', function() {
            var selections = Gikam.getComp('emc-run-result-grid').getSelections();
            var data = Gikam.getComp('emc-run-sample-test-grid').getSelections();
            if (selections.length == 0) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                return;
            }
            selections.map(function(item) {
                item.ext$.analysisType = 'analysis'
            });
            Gikam.getLastModal().window.showMask(true);
            Gikam.postText(emcRun.baseUrl + '/action/sync-tmpl-infos', Gikam.getJsonWrapper(null, [ 'emcOrderTaskServiceImpl', data ], [ 'emcOrderTaskResultServiceImpl', selections ])).done(function() {
                Gikam.getComp('emc-run-result-grid').refresh();
            }).always(function() {
                Gikam.getLastModal().window.closeMask();
            });
        });
    },

    deleteQcOrderTask : function(){
        var data = Gikam.getComp('emc-run-result-grid').getSelections();
        if (Gikam.isEmpty(data)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.confirm('删除', '是否删除', function() {

	        Gikam.postText(emcRun.baseUrl + '/action/delete-qc-ordertask', Gikam.getJsonWrapper(null, [ 'emcOrderTaskResultServiceImpl', data])).done(function() {
	            Gikam.getComp('emc-run-result-grid').refresh();
	        }).always(function() {
	            Gikam.getLastModal().window.closeMask();
	        });
        });
    },

    runInput : function(){
        var data = Gikam.getComp('emc-run-result-grid').getSelections();
        if (Gikam.isEmpty(data)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var itemId = data.map(function(e) {
            return e.itemId;
        });
        var grid = Gikam.getComp('emc-run-sample-test-grid');

        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.SELECT_SAMPLE',
            url : Gikam.choose.getConfig('order').url + Gikam.param({
                id_DIFFER : grid.getActivedRow().orderId,
                target_assigned : '1',
                targetTable : 'T_EMC_ORDER_TASK',
                targetColumn : 'ORDERID',
                targetFilterColumn1 : 'RUNID',
                targetFilterValue1 : grid.getActivedRow().runId,
            }),
            onAfterClose : function(afterData) {
                if (Gikam.isEmpty(afterData)) {
                    return;
                }

                let resultData = data.map(item => {
                    return {
                        originalResult : item.originalResult,
                        itemId : item.itemId,
                        runId : item.runId
                    }
                });

                let orderData = afterData.map(order => {
                    return { id : order.id};
                });


                Gikam.getLastModal().window.showMask(true);
                Gikam.post(emcRun.baseUrl + '/action/run-input', Gikam.getJsonWrapper(null, [ 'emcOrderTaskResultServiceImpl', resultData ], [ 'emcOrderServiceImpl', orderData ])).done(function() {
                    Gikam.getComp('emc-run-result-grid').refresh({
                        url : '/secure/emc/module/bp/bp/order-task-results/queries',
                        requestData : {
                            runId_SEQ : Gikam.getComp('emc-run-sample-test-grid').getActivedRow().runId,
                            orderId_SEQ : Gikam.getComp('emc-run-sample-test-grid').getActivedRow().orderId
                        }
                    });
                }).always(function() {
                    Gikam.getLastModal().window.closeMask();
                });
            }
        });
    },

    batchNewInput : function(){
        var data = Gikam.getComp('emc-run-result-grid').getSelections();
        if (Gikam.isEmpty(data)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var itemId = data.map(function(e) {
            return e.itemId;
        });
        var grid = Gikam.getComp('emc-run-sample-test-grid');
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.SELECT_SAMPLE',
            url : Gikam.choose.getConfig('orderTask').url + Gikam.param({
                runId_SEQ : grid.getActivedRow().runId,
                status_SEQ : 'logged',
                qcCategoryCode_DIFFER : 'N/A',
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
                var grid = Gikam.getComp('emc-run-result-grid');
                var modal = Gikam.create('modal', {
                    title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.TITLE.BATCH_INPUT',
                    width : 420,
                    height : 240
                });

                var rows = grid.getData();
                var selectItems = [];
                for (var row of rows) {
                    selectItems.push({
                        text : row.itemName,
                        value : row.itemName
                    })
                }

                Gikam.create('layout', {
                    renderTo : modal.window.$dom,
                    center : {
                        padding : '0 28px 0 0',
                        items : [{
                            type : 'form',
                            columns : 1,
                            id : 'emc-run-result-grid-all-batch-input-all-form',
                            fields : [{
                                field : 'itemName',
                                title : 'T_EMC_ORDER_TASK_RESULT.ITEMNAME',
                                type : 'select',
                                items : selectItems
                            }, {
                                field : 'originalResult',
                                title : 'T_EMC_ORDER_TASK_RESULT.ORIGINALRESULT'
                            }],
                            onRendered : function(row) {
                                var selections = Gikam.getComp('emc-run-result-grid').getSelections();
                                var row = selections[0];
                                var formData = Gikam.getComp('emc-run-result-grid-all-batch-input-all-form');
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
                                        if(e.fieldName == 'standQty') {
                                            // 标准值特殊处理
                                            arr.push({
                                                field : e.fieldName,
                                                title : e.fieldTitle,
                                                editor : true,
                                                width : e.width,
                                                sort : false,
                                            });
                                        } else if(e.fieldName == 'rn16') {
                                            // 不确定度特殊处理
                                            arr.push({
                                                field : e.fieldName,
                                                title : e.fieldTitle,
                                                editor : true,
                                                width : e.width,
                                                sort : false,
                                            });
                                        } else if(e.fieldName == 'rn5' ) {
                                            // 标点特殊处理
                                            arr.push({
                                                field : e.fieldName,
                                                title : e.fieldTitle,
                                                editor : true,
                                                width : e.width,
                                                sort : false,
                                            });
                                        }

                                    });
                                    var fields = formData.options.fields;
                                    var columns = fields.slice(0, 10).concat(arr, fields.slice(10));
                                    Gikam.getComp('emc-run-result-grid-all-batch-input-all-form').refreshFields(columns);

                                });
                            },

                           // 标点值 rn5 标准值standQty  不确定度rn16   加标量rn9]
                        }, {
                            type : 'btnToolbar',
                            items : [{
                                type : 'button',
                                text : 'GIKAM.BUTTON.CONFIRM',
                                icon : 'modify',
                                onClick : ()=> {

                                    var formData = Gikam.getComp('emc-run-result-grid-all-batch-input-all-form').getData();
                                    var arr = new Array();
                                    arr.push(formData);
                                        workspace.window.showMask(true);
                                        Gikam.postText(emcRun.baseUrl + '/action/batch-input-qc', Gikam.getJsonWrapper(null, [ 'emcOrderTaskServiceImpl', afterData ], [ 'emcOrderTaskResultServiceImpl', arr])).done(function() {
                                            Gikam.getComp('emc-run-result-grid').refresh();
                                        }).always(function() {
                                            workspace.window.closeMask();
                                        });


                                    //
/*                                    for (var row of rows) {
                                        if (row.itemName === formData.itemName) {
                                            grid.setData({
                                            	id : row.id,
                                                originalResult : formData.originalResult,
                                                rn5 : formData.rn5,
                                                rn16 : formData.rn16,
                                                rn9 : formData.rn9,
                                                standQty : formData.standQty,
                                            })
                                        }
                                    }*/
                                    modal.close();
                                }
                            }]
                        }]
                    }
                })




            }
        });
    },

    batchInput : ()=> {
        var grid = Gikam.getComp('emc-run-result-grid');
        var selections = grid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var modal = Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.TITLE.BATCH_INPUT',
            width : 420,
            height : 400
        });

        let uniqueItems = Array.from(new Set(selections.filter(item => item.scriptFlag === '0').map(item => item.itemName + '-' + item.itemAliasName)));
        let selectItems = uniqueItems.map(itemName => {
            return {
                text : itemName,
                value : itemName.split('-')[0]
            }
        });

        Gikam.create('layout', {
            renderTo : modal.window.$dom,
            center : {
                padding : '0 28px 0 0',
                items : [{
                    type : 'form',
                    columns : 1,
                    id : 'emc-run-result-grid-batch-input-form',
                    fields : [{
                        field : 'itemName',
                        title : 'T_EMC_ORDER_TASK_RESULT.ITEMNAME',
                        type : 'select',
                        items : selectItems
                    }, {
                        field : 'originalResult',
                        title : 'T_EMC_ORDER_TASK_RESULT.ORIGINALRESULT'
                    }],
                    onRendered : function() {
                        var row = selections[0];
                        var formData = Gikam.getComp('emc-run-result-grid-batch-input-form');
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
                                if(e.fieldName === 'standQty') {
                                    // 标准值特殊处理
                                    arr.push({
                                        field : e.fieldName,
                                        title : e.fieldTitle,
                                        editor : true,
                                        width : e.width,
                                        sort : false,
                                    });
                                } else if(e.fieldName === 'rn16') {
                                    // 不确定度特殊处理
                                    arr.push({
                                        field : e.fieldName,
                                        title : e.fieldTitle,
                                        editor : true,
                                        width : e.width,
                                        sort : false,
                                    });
                                } else if(e.fieldName === 'rn5' ) {
                                    // 标点特殊处理
                                    arr.push({
                                        field : e.fieldName,
                                        title : e.fieldTitle,
                                        editor : true,
                                        width : e.width,
                                        sort : false,
                                    });
                                } else if(e.fieldName === 'rn15' ) {
                                    // 加标量体积
                                    arr.push({
                                        field : e.fieldName,
                                        title : e.fieldTitle,
                                        editor : true,
                                        width : e.width,
                                        sort : false,
                                    });
                                } else if(e.fieldName === 'rn12' ) {
                                    // 加标量单位
                                    arr.push({
                                        field : e.fieldName,
                                        title : e.fieldTitle,
                                        editor : true,
                                        width : e.width,
                                        sort : false,
                                        type : 'choose',
                                        category : 'unit',
                                        single : true,
                                        onAfterChoose : function(data) {
                                            if (data) {
                                                let item = {};
                                                item[e.fieldName] = data[0].unitName;
                                                this.setData(item);
                                            }
                                        },
                                    });
                                }

                            });
                            var fields = formData.options.fields;
                            var columns = fields.slice(0, 10).concat(arr, fields.slice(10));
                            Gikam.getComp('emc-run-result-grid-batch-input-form').refreshFields(columns);

                        });
                    },
                   // 标点值 rn5 标准值standQty  不确定度rn16   加标量rn9]
                }, {
                    type : 'btnToolbar',
                    items : [{
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        icon : 'modify',
                        onClick : ()=> {
                            let formData = Gikam.getComp('emc-run-result-grid-batch-input-form').getData();
                            let itemName = formData.itemName;

                            let targetRows = Gikam.isEmpty(itemName) ? selections : selections.filter(item => item.itemName === itemName);
                            let changedRows = targetRows.map(item => {
                                return {
                                    id : item.id,
                                    originalResult : formData.originalResult,
                                    rn16 : formData.rn16,
                                    standQty : formData.standQty,
                                    rn5 : formData.rn5,
                                    rn15 : formData.rn15,
                                    rn12 : formData.rn12
                                }
                            });

                            Gikam.put(IFM_CONTEXT + '/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcOrderTaskResultServiceImpl', changedRows ])).done(function() {
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/action/change-result', Gikam.getJsonWrapper({}, [ '', changedRows ])).done(function () {
                                    grid.refresh();
                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                });
                            });

                            modal.close();
                        }
                    }]
                }]
            }
        })
    },

    invalidData : function(){
        Gikam.create('modal', {
            width : '20%',
            height : '30%',
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.INVALID_DATA',
            url : '/secure/emc/module/bp/sample/samples/page/invalid-results',
            onAfterClose : function(data) {
                if (!Gikam.isEmpty(data)) {

                    if (data.operatedata == undefined) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_OPERATE_DATA');
                        return;
                    }

                    if (data.operatedata == 'line') {

                        var resultSelections = Gikam.getComp('emc-run-result-grid').getSelections();
                        if (Gikam.isEmpty(resultSelections)) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                            return;
                        }

                        resultSelections.forEach(function(item) {
                            item.ext$.reason = data.reason;
                        });

                        // 在代码请求前打开遮罩，避免遮罩没有关闭
                        Gikam.getLastModal().window.showMask(true);
                        Gikam.put('/secure/emc/module/bp/bp/orders/results/action/set-done', Gikam.getJsonWrapper({}, [ null, resultSelections ])).done(function() {
                            Gikam.getComp('emc-run-result-grid').refresh();
                            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.UPDATE_SUCCESS');
                        }).always(function() {
                            Gikam.getLastModal().window.closeMask();
                        });
                    } else if (data.operatedata == 'noresult') {

                        var orderSelections = Gikam.getComp('emc-run-sample-test-grid').getSelections();
                        if (Gikam.isEmpty(orderSelections)) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                            return;
                        }

                        orderSelections.forEach(function(item) {
                            item.ext$.reason = data.reason;
                        });
                        Gikam.getLastModal().window.showMask(true);
                        Gikam.put('/secure/emc/module/bp/bp/orders/action/set-done', Gikam.getJsonWrapper({}, [ null, orderSelections ])).done(function() {
                            Gikam.getComp('emc-run-result-grid').refresh();
                            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.UPDATE_SUCCESS');
                        }).always(function() {
                            Gikam.getLastModal().window.closeMask();
                        });
                    }
                }
            }
        });
    },

    editItem : function(){
        var _this = this;
        var activedRow = Gikam.getComp('emc-run-sample-test-grid').getActivedRow();
        var data = Gikam.getComp('emc-run-result-grid').getActivedRow();
        if (Gikam.isEmpty(data)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.EDIT_ITEM',
            url : emcRun.baseUrl + '/page/edit-item' + Gikam.param({
                id : data.orderTaskId,
            }),
            width :'40%',
            onAfterClose : function(afterData) {
                if (Gikam.isEmpty(afterData[0]) || Gikam.isEmpty(afterData[1])) {
                    return;
                }
                Gikam.getLastModal().window.showMask(true);
                Gikam.post(emcRun.baseUrl + '/edit-item', Gikam.getJsonWrapper(null, [ 'emcOrderTaskResultServiceImpl', [data] ], [ 'emcItemServiceImpl', afterData[0]], ['emcOrderTaskServiceImpl', afterData[1]] )).done(function() {
                    _this.onSampleTestActive(activedRow);
                }).always(function() {
                    Gikam.getLastModal().window.closeMask();
                });
            }
        });
    },

    batchEditItem : function(){
        let _this = this;
        let selections = Gikam.getComp('emc-run-result-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let activedRow =  Gikam.getComp('emc-run-sample-test-grid').getActivedRow();
        let orders =  Gikam.getComp('emc-run-sample-test-grid').getSelections();
        let orderTaskIds = Array.from(new Set(selections.map(item => item.orderTaskId))).join();

        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.EDIT_ITEM',
            url : emcRun.baseUrl + '/page/batch-edit-item' + Gikam.param({
                id_IN : orderTaskIds,
            }),
            width :'60%',
            onAfterClose : function(data) {
                if (Gikam.isEmpty(data)) {
                    return;
                }

                Gikam.getLastModal().window.showMask(true);
                Gikam.postText(emcRun.baseUrl + '/action/batch-edit-item', Gikam.getJsonWrapper(null, [ 'emcItemServiceImpl', data ], ['emcOrderTaskServiceImpl', orders] )).done(function() {
                    _this.onSampleTestActive(activedRow);
                }).always(function() {
                    Gikam.getLastModal().window.closeMask();
                });
            }
        });
    },

    removeFomula : function(){
        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.REMOVE_FOMULA', 'EMC.MODULE.BP.RESULT.RUNS.TIP.REMOVE_FOMULA', function() {
            var selections = Gikam.getComp('emc-run-result-grid').getSelections();
            var data = Gikam.getComp('emc-run-sample-test-grid').getSelections();
            if (selections.length == 0) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                return;
            }
            Gikam.getLastModal().window.showMask(true);
            Gikam.postText(emcRun.baseUrl + '/action/remove-fomulas', Gikam.getJsonWrapper(null, [ 'emcOrderTaskServiceImpl', data ], [ 'emcOrderTaskResultServiceImpl', selections ])).done(function() {
                Gikam.getComp('emc-run-result-grid').refresh({
                    url : '/secure/emc/module/bp/bp/order-task-results/queries',
                    requestData : {
                        runId_SEQ : Gikam.getComp('emc-run-sample-test-grid').getActivedRow().runId,
                        orderId_SEQ : Gikam.getComp('emc-run-sample-test-grid').getActivedRow().orderId
                    }
                });
            }).always(function() {
                Gikam.getLastModal().window.closeMask();
            });
        });
    },

    addRepeateNumber : function(){
        var selections = Gikam.getComp('emc-run-result-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            width : '20%',
            height : '20%',
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.DATA_INPUT_TASK_RESULT_SET_REPEATENO',
            url : '/secure/emc/module/bp/sample/samples/page/repeat-number-set',
            onAfterClose : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getLastModal().window.showMask(true);
                    selections.map(function(item) {
                        item.repeatNo = data.repeatNo;
                    });
                    Gikam.postText('/secure/emc/module/bp/bp/orders/' + selections[0].id + '/action/add-repreat-nos', Gikam.getJsonWrapper({}, [ null, selections ])).done(function() {
                        Gikam.getComp('emc-run-result-grid').refresh();
                    }).always(function() {
                        Gikam.getLastModal().window.closeMask();
                    });
                }
            }
        });
    },

    delRepeateNumber : function(){
        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.DEL_REPEATE_NUMBER', 'EMC.MODULE.BP.RESULT.RUNS.TIP.DEL_REPEATE_NUMBER', function() {
            var selections = Gikam.getComp('emc-run-result-grid').getSelections();
            if (Gikam.isEmpty(selections)) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                return;
            }
            for(var i=0;i<selections.length;i++){
                if (selections[i].repeatNo == 1) {
                    Gikam.alert('GIKAM.TIP.CHOOSE_REPEAT_NO_DATA_GREATER_THAN_ONE');
                    return;
                }
            }
            Gikam.getLastModal().window.showMask(true);
            Gikam.postText('/secure/emc/module/bp/bp/orders/action/delete-repreat-nos', Gikam.getJsonWrapper({}, [ null, selections ])).done(function() {
                Gikam.getComp('emc-run-result-grid').refresh();
            }).always(function() {
                Gikam.getLastModal().window.closeMask();
            });
        });
    },

    modifyUnit : function(){
        var resultRows = Gikam.getComp('emc-run-result-grid').getSelections();
        if (Gikam.isEmpty(resultRows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var orderTaskRows = Gikam.getComp('emc-run-sample-test-grid').getSelections();
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.MODIFY_UNIT',
            url : Gikam.choose.getConfig('unit').url + Gikam.param({
                single : true
            }),
            width :'35%',
            onAfterClose : function(data) {
                if (Gikam.isEmpty(data)) {
                    return;
                }
                var unitName = data[0].unitName;
                var url = emcRun.baseUrl + '/action/modify-unit';
                Gikam.getLastModal().window.showMask(true);
                Gikam.postText(url, Gikam.getJsonWrapper({
                    f : {
                        unitName : unitName
                    }
                }, [ 'emcOrderTaskServiceImpl', orderTaskRows ], [ 'emcOrderTaskResultServiceImpl', resultRows ])).done(function() {
                    Gikam.getComp('emc-run-result-grid').refresh({
                        url : '/secure/emc/module/bp/bp/order-task-results/queries',
                        requestData : {
                            runId_SEQ : Gikam.getComp('emc-run-sample-test-grid').getActivedRow().runId,
                            orderId_SEQ : Gikam.getComp('emc-run-sample-test-grid').getActivedRow().orderId
                        }
                    });
                }).always(function() {
                    Gikam.getLastModal().window.closeMask();
                });
            }
        });
    },
    /*批录入修约规则*/
    modifyRevision : function(){
        var resultRows = Gikam.getComp('emc-run-result-grid').getSelections();
        if (Gikam.isEmpty(resultRows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var orderTaskRows = Gikam.getComp('emc-run-sample-test-grid').getSelections();
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.MODIFY_REVISION',
            url : Gikam.choose.getConfig('rounding-rule').url + Gikam.param({
                single : true
            }),
            width :'35%',
            onAfterClose : function(data) {
                if (Gikam.isEmpty(data)) {
                    return;
                }
                var revisionRule = data;
                var url = emcRun.baseUrl + '/action/modify-revision';
                Gikam.getLastModal().window.showMask(true);
                Gikam.postText(url, Gikam.getJsonWrapper({
                    f : {
                    	revisionRule: revisionRule
                    }
                }, [ 'emcOrderTaskServiceImpl', orderTaskRows ], [ 'emcOrderTaskResultServiceImpl', resultRows ])).done(function() {
                    Gikam.getComp('emc-run-result-grid').refresh({
                        url : '/secure/emc/module/bp/bp/order-task-results/queries',
                        requestData : {
                            runId_SEQ : Gikam.getComp('emc-run-sample-test-grid').getActivedRow().runId,
                            orderId_SEQ : Gikam.getComp('emc-run-sample-test-grid').getActivedRow().orderId
                        }
                    });
                }).always(function() {
                    Gikam.getLastModal().window.closeMask();
                });
            }
        });
    },

    modifyLimit : function(){
        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.MODIFY_LIMIT', 'EMC.MODULE.BP.RESULT.RUNS.TIP.MODIFY_LIMIT', function() {
            var row = Gikam.getComp('emc-run-result-grid').getActivedRow();
            if (Gikam.isEmpty(row)) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                return;
            }
            Gikam.getLastModal().window.showMask(true);
            Gikam.postText(emcRun.baseUrl + '/action/modify-limit', Gikam.getJsonWrapper(null, ['', [row]] )).done(function() {
                Gikam.getComp('emc-run-result-grid').refresh({
                    url : '/secure/emc/module/bp/bp/order-task-results/queries',
                    requestData : {
                        runId_SEQ : Gikam.getComp('emc-run-sample-test-grid').getActivedRow().runId,
                        orderId_SEQ : Gikam.getComp('emc-run-sample-test-grid').getActivedRow().orderId
                    }
                });
            }).always(function() {
                Gikam.getLastModal().window.closeMask();
            });
        });
    },

    methodSop : function(){
        var row = Gikam.getComp('emc-run-result-grid').getActivedRow();
        if (Gikam.isEmpty(row)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.METHOD_SOP',
            url : emcRun.baseUrl +'/page/method-files' + Gikam.param({
                methodVersionId : row.methodVersionId,
            }),
        });
    },

    historicalData : function(){
        var row = Gikam.getComp('emc-run-result-grid').getActivedRow();
        if (Gikam.isEmpty(row)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.HISTORICAL_DATA',
            width : '56%',
            url : emcRun.baseUrl +'/page/historical-data' + Gikam.param({
                monitorPointName : row.ext$.monitorpointname,
                itemId : row.itemId,
                testId : row.testId
            }),
        });
    },

    quickExport : function() {
        var rightData = Gikam.getComp('emc-run-result-grid').getData();
        if (Gikam.isEmpty(rightData)) {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CAN_NOT_EXPORT');
            return;
        }

        Gikam.getLastModal().window.showMask(true);
        var runId = rightData[0].runId;
        Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}/{id}', {
            templateCode : 'emcRunExportTemplate',
            service : 'emcRunExportTemplate',
            id : runId
        })).done(function(fileUri) {
            var fileUrl = IFM_CONTEXT + fileUri;
            Gikam.download(fileUrl);
        }).always(function() {
            Gikam.getLastModal().window.closeMask();
        });

    },

    quickImport : function() {
        var rightData = Gikam.getComp('emc-run-result-grid').getData();
        if (Gikam.isEmpty(rightData)) {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CAN_NOT_IMPORT');
            return;
        }
        var data = Gikam.getComp('emc-run-sample-test-grid').getData();
        var id = data.map(function(e) {
            return e.id;
        });
        Gikam.create('simpleUploader', {
            dbTable : 'T_EMC_ORDER_TASK_RESULT',
            id : id,
            multiple : false,
            accept : [ 'xls' ],
            onAfterClose : function(files) {
                if (Gikam.isEmpty(files)) {
                    return;
                }
                Gikam.getLastModal().window.showMask(true);
                Gikam.post(
                    Gikam.printf(IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'emcRunImportFileFunction', {
                        id: files[0].id
                    })
                ).done(function(result) {
                        if (result.url) {
                            Gikam.download(result.url);
                        } else if (result.message) {
                            Gikam.alert(result.message);
                        }
                        Gikam.getComp('emc-run-result-grid').refresh({
                            url : '/secure/emc/module/bp/bp/order-task-results/queries',
                            requestData : {
                                runId_SEQ : Gikam.getComp('emc-run-sample-test-grid').getActivedRow().runId,
                                orderId_SEQ : Gikam.getComp('emc-run-sample-test-grid').getActivedRow().orderId
                            }
                        })
                    }).always(function() {
                        Gikam.getLastModal().window.closeMask();
                    });
            }
        });
    },

    syncVolume : function(){
        var row = Gikam.getComp('emc-run-result-grid').getActivedRow();
        if (Gikam.isEmpty(row)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.postText(emcRun.baseUrl + '/action/sync-volume', Gikam.getJsonWrapper(null, ['pfOrderTaskResultServiceImpl', [row]] )).done(function() {
            Gikam.getComp('emc-run-result-grid').refresh();
        }).always(function() {
        });
    },

    chooseLimit : function() {
	    var _this = this;
        var selection = Gikam.getComp('emc-run-result-grid').getSelections();
        var runId = selection[0].runId;
	    if (Gikam.isEmpty(selection)) {
	        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
	        return;
	    }
        var modal = Gikam.create('modal', {
	        title: "选择检出限",
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
                            var formData = form.getData();
	                    	var chooseLimit =  formData.chooseLimit;
	                        Gikam.postText(emcRun.baseUrl + '/action/choose-limit', Gikam.getJsonWrapper({
	                            f :{
	                            	runId : runId,
	                            	chooseLimit : chooseLimit
	                            }}, [ 'PfOrderTaskResultService', selection])).done(function() {
	                                Gikam.getLastModal().close();
	                            }).always(function() {
	    	                        Gikam.getComp('emc-run-result-grid').refresh();
	                            });

	                        workspace.window.closeMask();
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
        fields: emcRun.getChooseLimitFields(),
        autoSave: false
    });
    },

    createPage : function() {
        Gikam.create('layout', {
            id : 'emc-run-sample-test-layout',
            north : {
                height : '50%',
                items : [ this.getTestSample() ]
            },
            center : {
                items : [ this.getResult() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        Gikam.getAsyncComp('emc-run-sample-test-grid').done(function(grid) {
            grid.options.checkOneOnActive = true;
        });
        Gikam.getAsyncComp('emc-run-result-grid').done(function(grid) {
            grid.options.checkOneOnActive = true;
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
