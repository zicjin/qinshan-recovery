emcSample.dataInputDetailPage = {

    getDataInputDetailGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sample-data-input-detail-list-grid',
            url : '/secure/emc/module/bp/bp/folders/queries/undoable',
            service : 'emcFolderServiceImpl',
            columns : emcSample.getDataInputColumns(),
            readonly : _this.getHidden(),
            checkOnActive : true,
            requestData : {
                target_filter : JSON.stringify({
                    type : 'assigned',
                    targetTable : "T_EMC_ORDER",
                    targetMatchColumn : "FOLDERID",
                    filter : [ {
                        targetFilterColumn : 'sampleId',
                        targetFilterValue : _this.param.id
                    } ]
                })
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                }
            },
            allSelect : this.getEquiptInfo,
            unAllSelect : this.getEquiptInfo,
            onSelect : this.getEquiptInfo,
            unSelect : this.getEquiptInfo,
            onRowActive : function(index, row) {
                _this.onDataInputActive(row);
            },
            toolbar : [ {
                type : 'dropDownMenu',
                hidden : _this.getHidden(),
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.MORE_OPERATE',
                items : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.REVIEW',
                    icon : 'browse',
                    onClick : function() {
                        _this.review();
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DATA_SUMMARY',
                    icon : 'browse',
                    onClick : function() {
                        _this.dataSummaryFolder();
                    }
                }, {
                    type : 'button',
                    text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.INITIALIZ',
                    icon : 'refresh',
                    onClick : function() {
                        Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.INITIALIZ', 'EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.INITIALIZ', function() {
                            _this.initializeTaskResult();
                        });
                    }
                } ]
            }, {
                type : 'dropDownMenu',
                hidden : _this.getHidden(),
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.RAPID_INPUT',
                items : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.IMPORT',
                    icon : 'edit',
                    onClick : function() {
                        var resultData = Gikam.getComp(_this.idGrid.resultGridId).getData();
                        if (Gikam.isEmpty(resultData)) {
                            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.CAN_NOT_IMPORT');
                            return;
                        }
                        var data = Gikam.getComp(_this.idGrid.resultGridId).getData();
                        var id = data.map(function(e) {
                            return e.id;
                        });

                        Gikam.importExcel({
                            url : IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'emcSampleImportFileFunction',
                            dbTable : 'T_EMC_ORDER_TASK_RESULT',
                            id : id,
                            onImportSuccess : function(files) {
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.getComp(_this.idGrid.resultGridId).refresh({
                                    url : '/secure/emc/module/bp/bp/order-task-results/queries',
                                    requestData : {
                                        folderId_SEQ : Gikam.getComp('emc-sample-data-input-detail-list-grid').getActivedRow().id,
                                        analysisType_SEQ : 'sampling',
                                        targetType : 'samplingInput',
                                        target_filter : JSON.stringify({
                                            type : 'assigned',
                                            targetTable : "T_EMC_ORDER",
                                            targetMatchColumn : "ID",
                                            thisMatchColumn : "ORDERID",
                                            filter : [ {
                                                targetFilterColumn : 'sampleId',
                                                targetFilterValue : _this.param.id
                                            } ]
                                        })
                                    }
                                }).always(function() {
                                    Gikam.getLastModal().window.closeMask();
                                });
                            }
                        });
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.EXPORT',
                    icon : 'export',
                    onClick : function() {
                        var resultData = Gikam.getComp(_this.idGrid.resultGridId).getData();
                        if (Gikam.isEmpty(resultData)) {
                            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.CAN_NOT_EXPORT');
                            return;
                        }
                        var folderData = Gikam.getComp('emc-sample-data-input-detail-list-grid').getSelections();
                        var id = folderData.map(function(e) {
                            return e.id;
                        });
                        Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}/{id}', {
                            templateCode : 'emcSampleExportTemplate',
                            service : 'emcSampleExportTemplate',
                            id : id.join() + '-' + resultData[0].ext$.sampleid
                        })).done(function(fileUri) {
                            var fileUrl = IFM_CONTEXT + fileUri;
                            Gikam.download(fileUrl);
                        });
                    }
                } ]
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.PREVIEW_SAMPLE_REPORT',
                icon : 'print',
                hidden : true,
                onClick : function() {
                    var selections = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
                    Gikam.viewReport(selections[0].reportConfigLineId, selections[0]);
                },
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.FOLDER_FILES',
                icon : 'upload',
                onClick : function() {
                    var selections = Gikam.getComp('emc-sample-data-input-detail-list-grid').getSelections();

                    if (selections.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    Gikam.create('modal', {
                        title : 'GIKAM.FILE.ATTACHMENT',
                        url : IFM_CONTEXT + '/secure/emc/module/mdm/file/files/page/view' + Gikam.param({
                            id : selections[0].id,
                            table : 'T_EMC_FOLDER'
                        })
                    });
                }
            } ]
        }
    },

    dataSummaryFolder : function() {
        var gridData = Gikam.getComp('emc-sample-data-input-detail-list-grid').getSelections();
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
                    summaryType : 'folder',
                    ids : ids
                })
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    getEquiptInfo : function() {
        var _this = this;
        Gikam.finalDelay('equipt', function() {
            var sampleId = emcSample.dataInputDetailPage.param.id;

            var folderIdString = Gikam.getComp('emc-sample-data-input-detail-list-grid').getSelections().map(function(item) {
                return item.id;
            }).join(",");
            Gikam.getAsyncComp('emc-order-task-equipt-edit-list-grid').done(function(grid) {
                grid.refresh({
                    url : '/secure/emc/module/bp/bp/order-tasks/queries',
                    requestData : {
                        folderId_IN : folderIdString === '' ? '0' : folderIdString,
                        analysisType_SEQ : 'sampling',
                        sampleId_SEQ : sampleId,
                        qcCategoryCode_DIFFER : 'P-BLANK',
                        targetType : 'samplingInput'
                    }
                });
                Gikam.getComp('emc-order-task-equipt-edit-list-grid').refresh();
            });
        }, 300);
    },

    review : function() {

        var selections = Gikam.getComp('emc-sample-data-input-detail-list-grid').getSelections();

        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.BP.FOLDERS.TITLE.REVIEW',
            url : '/secure/emc/module/bp/bp/folders/page/review' + Gikam.param({
                folderId : selections[0].id,
            }),
            onAfterClose : function(rows) {
                Gikam.getComp('emc-sample-data-input-detail-list-grid').refresh();
            }
        });
    },

    getResultGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.BP.FOLDERS.ORDERS.MODAL.RESULT',
                items : [ {
                    type : 'layout',
                    center : {
                        items : [ {
                            type : 'grid',
                            id : this.idGrid.resultGridId,
                            service : 'emcOrderTaskResultServiceImpl',
                            columns : emcSample.getOrderTaskResultColumns(this.param),
                            page : true,
                            pageSize : 100,
                            readonly : _this.getHidden(),
                            onLoadSuccess : function(data) {
                                if (!Gikam.isEmpty(data)) {
                                    this.activeRowByIndex(1);
                                }
                            },
                            onRendered : function() {
                                var formData = Gikam.getComp('emc-order-task-result-edit-list-button-group-by-test');
                                if (formData != undefined) {
                                    formData.clickHandle();
                                }
                            },
                            beforeUpdate : function(data) {
                                if (!Gikam.isEmpty(data.originalResult) && data.row && data.row.submitFlag == 1) {
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
                                                    Gikam.getComp(_this.idGrid.resultGridId).refresh();
                                                }).always(function() {
                                                    workspace.window.closeMask();
                                                });
                                            } else {
                                                Gikam.getComp(_this.idGrid.resultGridId).refresh();
                                            }
                                        }
                                    });
                                    return false;
                                }
                            },
                            onUpdated : function(data, fields, index) {
                                var grid = this;

                                var folderRow = Gikam.getComp('emc-sample-data-input-detail-list-grid').getActivedRow();

                                if (fields.indexOf('originalResult') > -1 || fields.indexOf('standQty') > -1 || fields.indexOf('lowLimit') > -1 || fields.indexOf('highLimit') > -1 || fields.indexOf('revisionRule') > -1) {

                                    Gikam.getLastModal().window.showMask(true);
                                    var orderTaskResultUrl = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results';

                                    Gikam.put(orderTaskResultUrl + '/action/change-result', Gikam.getJsonWrapper({}, [ '', [ data ] ])).done(function() {
                                        Gikam.post(orderTaskResultUrl + '/queries', Gikam.getJsonWrapper({
                                            n : -1,
                                            f : {
                                                folderId_SEQ : folderRow.id,
                                                analysisType_SEQ : 'sampling',
                                                targetType : 'samplingInput',
                                                target_filter : JSON.stringify({
                                                    type : 'assigned',
                                                    targetTable : "T_EMC_ORDER",
                                                    targetMatchColumn : "ID",
                                                    thisMatchColumn : "ORDERID",
                                                    filter : [ {
                                                        targetFilterColumn : 'sampleId',
                                                        targetFilterValue : _this.param.id
                                                    } ]
                                                })
                                            }
                                        })).done(function(data) {

                                            grid.refreshByData(data, true);

                                        }).always(function() {
                                            Gikam.getLastModal().window.closeMask();
                                        });

                                    }).fail(function() {
                                        Gikam.getLastModal().window.closeMask();
                                    });
                                }
                            },
                            toolbar : [ {
                                type : 'button',
                                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.GROUP_BY_ORDER',
                                id : 'emc-order-task-result-edit-list-button-group-by-order',
                                icon : 'edit',
                                onClick : function() {
                                    var grid = Gikam.getComp(_this.idGrid.resultGridId);
                                    grid.refreshGroup({
                                        fields : [ 'orderNo' ],
                                        formatter : function(row) {
                                            return row.orderNo;
                                        }
                                    });
                                    Gikam.getComp('emc-order-task-result-edit-list-button-group-by-test').show();
                                    Gikam.getComp('emc-order-task-result-edit-list-button-group-by-order').hide();
                                }
                            }, {
                                type : 'button',
                                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.GROUP_BY_TEST',
                                id : 'emc-order-task-result-edit-list-button-group-by-test',
                                icon : 'edit',
                                onClick : function() {
                                    var grid = Gikam.getComp(_this.idGrid.resultGridId);
                                    grid.refreshGroup({
                                        fields : [ 'ext$.testname' ],
                                        formatter : function(row) {
                                            return row.ext$.testname;
                                        }
                                    });
                                    Gikam.getComp('emc-order-task-result-edit-list-button-group-by-test').hide();
                                    Gikam.getComp('emc-order-task-result-edit-list-button-group-by-order').show();
                                }
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.BATCH_INPUT',
                                icon : 'add',
                                onClick : function() {
                                    _this.batchInputResult();
                                }
                            }, {
                                type : 'dropDownMenu',
                                hidden : _this.getHidden(),
                                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.MORE_OPERATE',
                                items : [ {
                                    hidden : _this.getHidden(),
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SYNC_TMPL_INFO',
                                    icon : 'edit',
                                    onClick : function() {
                                        Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SYNC_TMPL_INFO', 'EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.SYNC_TMPL_INFO', function() {
                                            _this.syncTmplInfo();
                                        });
                                    },
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.DEFAULT_RESULT',
                                    icon : 'edit',
                                    onClick : function() {
                                        Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.DEFAULT_RESULT', 'EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.DEFAULT_RESULT', function() {
                                            _this.orderTaskDefaultResult();
                                        });
                                    },
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SET_FINISHED',
                                    icon : 'edit',
                                    onClick : function() {
                                        _this.setDoneOrderTaskResult();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.REMOVE_FOMULA',
                                    icon : 'edit',
                                    onClick : function() {
                                        Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.REMOVE_FOMULA', 'EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.REMOVE_FOMULA', function() {
                                            _this.removeFomula();
                                        });
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.ADD_REPEATE_NUMBER',
                                    icon : 'add',
                                    onClick : function() {
                                        _this.addRepeateNumber();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.DEL_REPEATE_NUMBER',
                                    icon : 'remove-row',
                                    onClick : function() {
                                        Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.DEL_REPEATE_NUMBER', 'EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.DEL_REPEATE_NUMBER', function() {
                                            _this.deleteRepeateNumber();
                                        });
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.EDIT_ITEM',
                                    icon : 'edit',
                                    onClick : function() {
                                        _this.editItem();
                                    }
                                } ]
                            } ],
                        } ]
                    },
                }, ]
            }, {
                title : 'EMC.MODULE.BP.BP.FOLDERS.ORDERS.TAB.APPOINT_EQUIPT.BASE_INFO',
                items : [ {
                    type : 'layout',
                    center : {
                        items : [ {
                            type : 'grid',
                            id : 'emc-order-task-equipt-edit-list-grid',
                            service : 'emcOrderTaskServiceImpl',
                            columns : emcSample.getOrderTaskEquiptColumns(),
                            readonly : _this.getHidden(),
                            toolbar : [ {
                                hidden : _this.getHidden(),
                                type : 'button',
                                text : 'GIKAM.BUTTON.UPDATE_EQUIPT',
                                icon : 'edit',
                                onClick : function() {
                                    _this.updateEquipt();
                                }
                            }, {
                                hidden : _this.getHidden(),
                                type : 'button',
                                text : 'GIKAM.BUTTON.UPDATE_EMP',
                                icon : 'edit',
                                onClick : function() {
                                    _this.updateEmp();
                                }
                            }, {
                                hidden : _this.getHidden(),
                                type : 'button',
                                text : 'GIKAM.BUTTON.PARTAKEUSER_INPUT',
                                icon : 'edit',
                                onClick : function() {
                                    _this.updatePartakeUserName();
                                },
                            }, {
                                hidden : _this.getHidden(),
                                type : 'button',
                                text : 'GIKAM.BUTTON.ASSIST_EQUIPT',
                                icon : 'edit',
                                onClick : function() {
                                    _this.updateAssitEpuipt();
                                }
                            }, {
                                type : 'button',
                                text : 'EMC.MODULE.BP.BPS.BUTTON.EQUIPT_RECORD',
                                icon : 'browse',
                                hidden : true,
                                onClick : function() {
                                    _this.equiptRecord();
                                }
                            } ]
                        } ]
                    },
                }, ]
            }, ]
        }
    },

    onDataInputActive : function(row) {

        var sampleId = this.param.id;
        var _this = this;
        if (row) {

            // 结果数据
            Gikam.getAsyncComp(_this.idGrid.resultGridId).done(function(grid) {
                grid.options.checkOneOnActive = true;
                grid.setOptions({
                    url : _this.getOrderTaskResultUrl(sampleId),
                    requestData : {
                        folderId_SEQ : row.id,
                        analysisType_SEQ : 'sampling',
                        targetType : 'samplingInput',
                        target_filter : JSON.stringify({
                            type : 'assigned',
                            targetTable : "T_EMC_ORDER",
                            targetMatchColumn : "ID",
                            thisMatchColumn : "ORDERID",
                            filter : [ {
                                targetFilterColumn : 'sampleId',
                                targetFilterValue : _this.param.id
                            } ]
                        })
                    }
                });
                Gikam.getComp(_this.idGrid.resultGridId).refresh().done(function (data) {
                    var grid = Gikam.getComp(_this.idGrid.resultGridId);
                    for(var i = 0; i < data.data.length; i++) {
                        if(data.data[i].scriptFlag === '0' && Gikam.isEmpty(data.data[i].originalResult)) {
                            grid.focusField(i, 'originalResult');
                            break;
                        }
                    }
                });
            });

        }
    },

    /** 批录入结果 */
    batchInputResult : function() {
        var _this = this;
        var selections = Gikam.getComp(_this.idGrid.resultGridId).getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        var folderRow = Gikam.getComp('emc-sample-data-input-detail-list-grid').getActivedRow();

        var orderTaskIds = selections.map(function (item) {
            return item.orderTaskId;
        });
        var uniqueOrderTaskIds = Array.from(new Set(orderTaskIds));

        Gikam.create('modal', {
            width : '60%',
            height : '70%',
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.TITLE.CHOOSE_ORDER_TASK',
            url : emcSample.baseUrl + '/page/order-result-batch-input' + Gikam.param({
                folderId_SEQ : folderRow.id,
                analysisType_SEQ : 'sampling',
                id_DIFFER : uniqueOrderTaskIds.join()
            }),
            onAfterClose : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getLastModal().window.showMask(true);
                    Gikam.postText(emcSample.baseUrl + '/order-tasks/action/batch-input', Gikam.getJsonWrapper({}, [ null, selections ], [ null, data ])).done(function() {
                        Gikam.getComp(_this.idGrid.resultGridId).refresh();
                    }).always(function() {
                        Gikam.getLastModal().window.closeMask();
                    });
                }
            }
        });
    },

    initializeTaskResult : function() {
        var _this = this;
        var formData = Gikam.getComp('emc-sample-data-input-detail-list-grid').getData();
        var url = '/secure/emc/module/bp/bp/orders/' + _this.param.id + '/action/init-task-results';
        workspace.window.showMask(true);
        Gikam.postText(url, Gikam.getJsonWrapper(null, [ '', formData ])).done(function() {
            Gikam.getComp(_this.idGrid.resultGridId).refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    removeFomula : function() {
        var _this = this;
        var selections = Gikam.getComp(_this.idGrid.resultGridId).getSelections();
        if (selections.length == 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }
        workspace.window.showMask(true);
        Gikam.postText('/secure/emc/module/bp/bp/orders/action/remove-fomulas', Gikam.getJsonWrapper({}, [ null, selections ])).done(function() {
            Gikam.getComp(_this.idGrid.resultGridId).refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    addRepeateNumber : function() {
        var _this = this;
        var selections = Gikam.getComp(_this.idGrid.resultGridId).getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
        	width : '20%',
            height : '20%',
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.DATA_INPUT_TASK_RESULT_SET_REPEATENO',
            url : emcSample.baseUrl + '/page/repeat-number-set',
            onAfterClose : function(data) {
                if (!Gikam.isEmpty(data)) {
                    workspace.window.showMask(true);
                    selections.map(function(item) {
                        item.repeatNo = data.repeatNo
                    });
                    Gikam.postText('/secure/emc/module/bp/bp/orders/' + selections[0].id + '/action/add-repreat-nos', Gikam.getJsonWrapper({}, [ null, selections ])).done(function() {
                        Gikam.getComp(_this.idGrid.resultGridId).refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    editItem : function() {
        var _this = this;
        var data = Gikam.getComp(_this.idGrid.resultGridId).getActivedRow();
        if (Gikam.isEmpty(data)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.EDIT_ITEM',
            url : emcSample.baseUrl + '/page/edit-item' + Gikam.param({
                id : data.orderTaskId,
            }),
            onAfterClose : function(afterData) {
                if (Gikam.isEmpty(afterData[0]) || Gikam.isEmpty(afterData[1])) {
                    return;
                }
                workspace.window.showMask(true);
                Gikam.post(emcSample.baseUrl + '/edit-item', Gikam.getJsonWrapper({
                    f : {
                        sampleId : _this.param.id
                    }
                }, [ 'emcOrderTaskResultServiceImpl', [ data ] ], [ 'emcItemServiceImpl', afterData[0] ], [ 'emcFolderServiceImpl', afterData[1] ])).done(function() {
                    Gikam.getComp(_this.idGrid.resultGridId).refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        })
    },

    deleteRepeateNumber : function() {
        var _this = this;
        var selections = Gikam.getComp(_this.idGrid.resultGridId).getSelections();
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
        workspace.window.showMask(true);
        Gikam.postText('/secure/emc/module/bp/bp/orders/action/delete-repreat-nos', Gikam.getJsonWrapper({}, [ null, selections ])).done(function() {
            Gikam.getComp(_this.idGrid.resultGridId).refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    syncTmplInfo : function() {
        var _this = this;
        var selections = Gikam.getComp(_this.idGrid.resultGridId).getSelections();
        if (selections.length == 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }
        selections.map(function(item) {
            item.ext$.analysisType = 'sampling'
        });
        workspace.window.showMask(true);
        Gikam.postText('/secure/emc/module/bp/bp/orders/action/sync-tmpl-infos', Gikam.getJsonWrapper({}, [ null, selections ])).done(function() {
            Gikam.each(selections, function(index, item) {
                Gikam.getComp('emc-order-task-result-edit-list-grid').refreshRowById(item.id);
            });
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    orderTaskDefaultResult : function() {
        var _this = this;
        var selections = Gikam.getComp(_this.idGrid.resultGridId).getSelections();
        if (selections.length == 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        var dataInputSelections = Gikam.getComp('emc-sample-data-input-detail-list-grid').getSelections();
        if (dataInputSelections.length == 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        var url = '/secure/emc/module/bp/bp/orders/' + _this.param.id + '/action/update';

        var id = selections.foreach;
        workspace.window.showMask(true);
        Gikam.postText(url, Gikam.getJsonWrapper(null, [ 'emcFolderServiceImpl', dataInputSelections ], [ 'emcOrderTaskResultServiceImpl', selections ])).done(function() {
            Gikam.each(selections, function(index, item) {
                Gikam.getComp(_this.idGrid.resultGridId).refresh();
            });
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    setDoneOrderTaskResult : function() {
        var _this = this;
        Gikam.create('modal', {
        	width : '20%',
            height : '30%',
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.INVALID_DATA',
            url : emcSample.baseUrl + '/page/invalid-results',
            onAfterClose : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getLastModal().window.showMask(true);
                    if (data.operatedata == undefined) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_OPERATE_DATA');
                        return;
                    }

                    if (data.operatedata == 'line') {
                        var resultSelections = Gikam.getComp(_this.idGrid.resultGridId).getSelections();
                        if (Gikam.isEmpty(resultSelections)) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                            return;
                        }

                        resultSelections.forEach(function(item) {
                            item.ext$.reason = data.reason;
                        });

                        Gikam.put('/secure/emc/module/bp/bp/orders/results/action/set-done', Gikam.getJsonWrapper({}, [ null, resultSelections ])).done(function() {
                            Gikam.getComp(_this.idGrid.resultGridId).refresh();
                            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.UPDATE_SUCCESS');
                        }).always(function() {
                            Gikam.getLastModal().window.closeMask();
                        });
                    } else if (data.operatedata == 'noresult') {

                        var orderSelections = Gikam.getComp('emc-sample-data-input-detail-list-grid').getSelections();
                        if (Gikam.isEmpty(orderSelections)) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                            return;
                        }

                        orderSelections.forEach(function(item) {
                            item.ext$.reason = data.reason;
                            item.ext$.sampleid = _this.param.id;
                        });
                        Gikam.put('/secure/emc/module/bp/bp/orders/folders/action/set-done', Gikam.getJsonWrapper({}, [ null, orderSelections ])).done(function() {
                            Gikam.getComp(_this.idGrid.resultGridId).refresh();
                            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.UPDATE_SUCCESS');
                        }).always(function() {
                            Gikam.getLastModal().window.closeMask();
                        });
                    }
                }
            }
        });
    },

    getOrderTaskResultUrl : function(sampleId) {
        if (sampleId) {
            return Gikam.printf('/secure/emc/module/bp/bp/orders/order-task-results')
        }
    },

    updateEquipt : function() {
        var _this = emcSample.dataInputDetailPage;
        var selections = Gikam.getComp('emc-order-task-equipt-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : Gikam.choose.getConfig('equipt').title,
            url : Gikam.choose.getConfig('equipt').url + Gikam.param({
                single : true,
                biz : 'orderTask',
                testMethodId : selections[0].testMethodId
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var saveData = [];
                var saveRecordData = [];
                Gikam.each(selections, function(index, item) {
                    saveData.push({
                        id : item.id,
                        equiptId : rows[0].id,
                        analysisUserId : workspace.user.id,
                        analysisUserName : workspace.user.userName,
                    });
                    saveRecordData.push({
                        projId : selections[0].projId,
                        orderId : selections[index].orderId,
                        sampleId : _this.param.id,
                        equiptId : rows[0].id,
                        testId : item.testId,
                        testName : item.testName,
                        userId : workspace.user.id,
                        userName : workspace.user.userName,
                    });
                });
                Gikam.put('/secure/emc/module/mdm/equipt/equipts/use-records', Gikam.getJsonWrapper({}, [ 'emcEquiptUseRecordServiceImpl', saveRecordData ]));

                workspace.window.showMask(true);
                Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', saveData ])).done(function(data) {
                    Gikam.getComp('emc-order-task-equipt-edit-list-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    updateAssitEpuipt : function() {
        var _this = emcSample.dataInputDetailPage;
        var selections = Gikam.getComp('emc-order-task-equipt-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('equipt').title,
            url : Gikam.choose.getConfig('equipt').url + Gikam.param({
                orgId_SB: workspace.user.ext$.parentorgid,
                equiptCategoryId_EQ : 615348928847936
            }),
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                var assistEquiptId = "";
                var assistEquiptName = "";

                Gikam.each(rows, function(index, equipt) {
                    if (Gikam.isEmpty(assistEquiptId)) {
                        assistEquiptId += equipt.id;
                    } else {
                        assistEquiptId += ',' + equipt.id;
                    }

                    if (Gikam.isEmpty(assistEquiptName)) {
                        assistEquiptName += equipt.equiptName;
                    } else {
                        assistEquiptName += ',' + equipt.equiptName;
                    }
                });

                var saveData = selections.map(function(task) {
                    return {
                        id : task.id,
                        assistEquiptId : assistEquiptId,
                        assistEquiptName : assistEquiptName,
                    };
                });
                workspace.window.showMask(true);
                Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', saveData ])).done(function() {
                    var equiptGrid = Gikam.getComp('emc-order-task-equipt-edit-list-grid');
                    var gridData = equiptGrid.getSelections().forEach(function(item) {
                        equiptGrid.setData({
                            id : item.id,
                            assistEquiptId : assistEquiptId,
                            assistEquiptName : assistEquiptName,
                        }, false);
                    });
                    equiptGrid.disableAutoSave();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    equiptRecord : function() {
        var _this = this;
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.BPS.MODAL.EQUIPT_RECORD',
            url : '/secure/emc/module/mdm/equipt/equipts/page/use-record-update' + Gikam.param({
                sampleId : _this.param.id,
                readOnly : _this.getHidden()
            })
        });
    },

    updateEmp : function() {
        var _this = emcSample.dataInputDetailPage;
        var selections = Gikam.getComp('emc-order-task-equipt-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('user-simple').title,
            url : Gikam.choose.getConfig('user-simple').url + Gikam.param({
                single : true,
            }),
            width : '30%',
            onAfterClose : function(formData) {
                if (Gikam.isEmpty(formData)) {
                    return;
                }
                var saveData = [];
                Gikam.each(selections, function(index, item) {
                    saveData.push({
                        id : item.id,
                        analysisUserId : formData[0].id,
                        analysisUserName : formData[0].userName,
                    });
                });
                workspace.window.showMask(true);
                Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', saveData ])).done(function(id) {
                    var equiptGrid = Gikam.getComp('emc-order-task-equipt-edit-list-grid');
                    var gridData = equiptGrid.getSelections().forEach(function(item) {
                        equiptGrid.setData({
                            id : item.id,
                            analysisUserId : formData[0].id,
                            analysisUserName : formData[0].userName,
                        }, false);
                    });
                    equiptGrid.disableAutoSave();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    updatePartakeUserName : function() {
        var _this = emcSample.dataInputDetailPage;
        var selections = Gikam.getComp('emc-order-task-equipt-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            width : '30%',
            title : Gikam.choose.getConfig('user-simple').title,
            url : Gikam.choose.getConfig('user-simple').url + Gikam.param({}),
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                var partakeUserId = "";
                var partakeUserName = "";

                Gikam.each(rows, function(index, emp) {
                    if (Gikam.isEmpty(partakeUserId)) {
                        partakeUserId += emp.id;
                    } else {
                        partakeUserId += ',' + emp.id;
                    }

                    if (Gikam.isEmpty(partakeUserName)) {
                        partakeUserName += emp.userName;
                    } else {
                        partakeUserName += ',' + emp.userName;
                    }
                });

                var saveData = selections.map(function(task) {
                    return {
                        id : task.id,
                        partakeUserId : partakeUserId,
                        partakeUserName : partakeUserName
                    };
                });

                workspace.window.showMask(true);
                Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', saveData ])).done(function(id) {
                    var equiptGrid = Gikam.getComp('emc-order-task-equipt-edit-list-grid');
                    var gridData = equiptGrid.getSelections().forEach(function(item) {
                        equiptGrid.setData({
                            id : item.id,
                            partakeUserId : partakeUserId,
                            partakeUserName : partakeUserName
                        }, false);
                    });
                    equiptGrid.disableAutoSave();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    getHidden : function() {
        if (this.param.pageSource == 'report') {
            return true;
        } else {
            return false;
        }
    },

    idGrid : {
        resultGridId : 'emc-order-task-result-edit-list-grid',
    },

    createPage : function() {
        Gikam.create('layout', {
            north : {
                height : 200,
                items : [ this.getDataInputDetailGridParam() ]
            },
            center : {
                items : [ this.getResultGridParam() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });

        Gikam.getAsyncComp('emc-sample-data-input-detail-list-grid').done(function(grid) {
            grid.options.checkOneOnActive = true;
        });

        Gikam.getAsyncComp('emc-order-task-equipt-edit-list-grid').done(function(grid) {
            grid.options.checkOneOnActive = true;
        });
    },

    init : function(param) {
        if (param.pageSource == 'report') {
            resultGridId: 'emc-order-task-result-audit-list-grid'
        }
        this.param = param;
        this.createPage();
    }

}
