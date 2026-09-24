emcResultInputSample.editPage = {

    // 结果录入-按采样单
    getResultInputDataInputGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            refreshFromSearch : false,
            service : 'emcResultInputSampleServiceImpl',
            id : 'emc-sample-result-input-list-project-grid',
            url : '/secure/emc/module/bp/project/projects/queries/undoable',
            columns : emcResultInputSample.getProjectListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-sample-result-input-list-project-sample-grid', 'emc-sample-result-input-list-data-input-order-task-grid', 'emc-sample-result-input-list-data-input-container-info-grid','emc-sample-result-input-list-data-input-test-info-grid' ]);
                }
            },
            onRowActive : function(index, row) {
                _this.onResultInputOrderProjectActive(row);
            },
            requestData : {
                targetType : 'resultInput',
                status_SEQ : 'auditFinished',
                sampleUserId : workspace.user.id,
                projectType : 'folderProject',
                folderStatus : 'draw',
                org_filter : '1',
            },
            toolbar : [ {
                type : 'form',
                id : 'emc-sample-result-input-list-form',
                fields : emcResultInputSample.getProjectShowAllField(),
                autoSave : false,
            } ]
        }
    },

    setMonitorProperty : function() {
        var _this = this;

        var selections = Gikam.getComp('emc-sample-result-input-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var testOrgIds = '';
        selections.forEach(function(item) {
            if (item.ext$.testorgid != undefined) {
                testOrgIds += "," + item.ext$.testorgid;
            }
        });
        if (Gikam.isEmpty(testOrgIds)) {
            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.UN_MONITOR_POINT');
            return;
        }
        // 根据 受检单位 查询全部监测点
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.TITLE.MONITORPOINT',
            width : 800,
            height : 600,
            url : '/secure/emc/module/bp/sample/sampling-plans/page/monitor-property' + Gikam.param({
                testOrgId : testOrgIds.slice(1),
            }),
        });

    },

    getAllProjectUrl : function() {
        return Gikam.printf('/secure/emc/module/bp/project/projects/queries/raw');
    },

    updateProjectStatus : function() {
        var selections = Gikam.getComp('emc-sample-result-input-list-project-grid').getSelections();
        if (selections.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        workspace.window.showMask(true);
        Gikam.put('/secure/emc/module/bp/bp/folders/action/update-project-status', Gikam.getJsonWrapper({}, [ null, selections ])).done(function(data) {
            Gikam.getComp('emc-sample-result-input-list-project-grid').refresh();
            Gikam.getComp('emc-sample-result-input-list-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    getOrderUrl : function(folderId) {
        if (folderId) {
            return Gikam.printf(emcResultInputSample.baseUrl + '/{id}/orders/queries', {
                id : folderId
            })
        }
    },

    // 样品容器信息
    getOrderTaskUrl : function(orderId) {
        if (orderId) {
            // 需要处理 testName
            return Gikam.printf('/secure/emc/module/bp/bp/orders/{id}/order-tasks/queries', {
                id : orderId
            })
        }
    },

    getProjectSampleUrl : function(id) {
        if (id) {
            return Gikam.printf(emcResultInputSample.baseUrl + '/queries/raw', {
                id : id
            })
        }
    },

    // 样品和测试
    getSampleTestUrl : function() {
        return Gikam.printf('/secure/emc/module/bp/bp/orders/queries/raw', {})
    },

    // 查询样品
    getOrderSmapleUrl : function() {
        return Gikam.printf('/secure/emc/module/bp/bp/orders/queries/raw')
    },

    onSampleTestActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-sample-result-input-list-data-input-order-task-grid').setOptions({
                requestData : {
                    sampleId_SEQ : row.id,
                    orderId_SEQ : '',
                }
            });
            Gikam.getComp('emc-sample-result-input-list-data-input-order-task-grid').refresh({
                url : _this.getSampleTestUrl(row.id),
            });

        }
    },

    // 激活样品
    onOrderActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-sample-result-input-list-order-container-info-grid').refresh({
                url : _this.getOrderTaskUrl(row.id)
            });
        }
    },

    onOrderFolderActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-sample-result-input-list-order-prepare-grid').setOptions({
                requestData : {
                    // 过滤条件, 要显示 sampleId 为空 的点位
                    folderId_SEQ : row.id,
                    sampleId : 'empty'
                }
            });
            Gikam.getComp('emc-sample-result-input-list-order-prepare-grid').refresh({
                url : _this.getOrderSmapleUrl(row.id),
            });
        }
    },

    onOrderProjectActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-sample-result-input-list-project-sample-grid').setOptions({
                requestData : {
                    projId_SEQ : row.id,
                    status_SEQ : 'draft',
                }
            });
            Gikam.getComp('emc-sample-result-input-list-project-sample-grid').refresh({
                url : _this.getProjectSampleUrl(row.id),
            });

            Gikam.cleanCompData([ 'emc-sample-result-input-list-data-input-order-task-grid', 'emc-sample-result-input-list-data-input-container-info-grid','emc-sample-result-input-list-data-input-test-info-grid' ]);
        }
    },

    onPrepareProjectActive : function(row) {
        var _this = this;
        if (row) {
            var sceneUserId = '';
            var sampleUserId = '';
            var showFlag = Gikam.getComp('emc-sample-result-input-list-prepare-form').getField('showFlag');
            if (showFlag.value !== '1') {
                sceneUserId = workspace.user.id;
                sampleUserId = workspace.user.id;
            }
            Gikam.getComp('emc-sample-result-input-list-grid').setOptions({
                requestData : {
                    targetType : 'sampleprepare',
                    sceneUserId : sceneUserId,
                    sampleUserId : sampleUserId,
                    projectstatus_DIFFER : 'cancel',
                    status_SEQ : 'draw',
                    projId_SEQ : row.id
                },
            });
            Gikam.getComp('emc-sample-result-input-list-grid').refresh({
                url : '/secure/emc/module/bp/bp/folders/queries/undoable',
            });
        }
    },

    onResultInputOrderProjectActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-sample-result-input-list-project-sample-grid').setOptions({
                requestData : {
                    projId_SEQ : row.id,
                    status_SEQ : 'logged',
                }
            });
            Gikam.getComp('emc-sample-result-input-list-project-sample-grid').refresh({
                url : emcResultInputSample.baseUrl + '/queries',
            });

            Gikam.cleanCompData([ 'emc-sample-result-input-list-data-input-order-task-grid', 'emc-sample-result-input-list-data-input-container-info-grid','emc-sample-result-input-list-data-input-test-info-grid' ]);
        }
    },

    // 数据录入 样品 - 容器信息
    onSampleOrderActive : function(row) {

        var _this = this;
        if (row) {
            Gikam.getComp('emc-sample-result-input-list-data-input-container-info-grid').refresh({
                url : _this.getOrderTaskUrl(row.id),
            });

            Gikam.getComp('emc-sample-result-input-list-data-input-test-info-grid').refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/subpackages/queries',
                requestData : {
                    orderId_SEQ : row.id,
                    analysisType_SEQ : 'analysis'
                },
                order : {
                    testId : 'asc'
                }
            });
        }
    },

    // 采样单
    getProjectSampleGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sample-result-input-list-project-sample-grid',
            service : 'emcSampleServiceImpl',
            columns : emcResultInputSample.getProjectSampleColumns(),
            deleteFormatter : function(row) {
                return row.sampleNo || row.id;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-sample-result-input-list-data-input-order-task-grid', 'emc-sample-result-input-list-data-input-container-info-grid','emc-sample-result-input-list-data-input-test-info-grid' ]);
                    Gikam.getComp('emc-sample-result-input-list-data-input-order-task-grid').setOptions({
                        url : null
                    });
                    Gikam.getComp('emc-sample-result-input-list-data-input-container-info-grid').setOptions({
                        url : null
                    });
                    Gikam.getComp('emc-sample-result-input-list-data-input-test-info-grid').setOptions({
                        url : null
                    });
                }
            },
            onRowActive : function(index, row) {
                _this.onSampleTestActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    var grid = Gikam.getComp('emc-sample-result-input-list-project-sample-grid');
                    Gikam.create('workflow').submit({
                        data : grid.getSelections(),
                        pageObject : emcResultInputSample
                    }).done(function() {
                        var projectGrid = Gikam.getComp('emc-sample-result-input-list-project-grid');
                        projectGrid.refresh();
                        grid.refresh();
                        Gikam.cleanCompData([ 'emc-sample-result-input-list-data-input-order-task-grid', 'emc-sample-result-input-list-data-input-container-info-grid','emc-sample-result-input-list-data-input-test-info-grid' ]);
                    });
                },
            }, {
                type : 'dropDownMenu',
                text : 'GIKAM.BUTTON.SAMPLE',
                hidden : true,
                items : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.ADD_SAMPLE',
                    icon : 'add',
                    onClick : function() {
                        var activeRows = Gikam.getComp('emc-sample-result-input-list-project-grid').getActivedRow();
                        Gikam.getComp('emc-sample-result-input-list-project-sample-grid').insert(emcResultInputSample.baseUrl, [ {
                            'projId' : activeRows.id,
                        } ]);
                    },
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DEL_SAMPLE',
                    icon : 'remove-row',
                    onClick : function() {
                        var formData = Gikam.getComp('emc-sample-result-input-list-project-sample-grid').getSelections();
                        var url = Gikam.printf(emcResultInputSample.baseUrl);
                        Gikam.getComp('emc-sample-result-input-list-project-sample-grid').deleteRows(url).done(function() {
                            Gikam.getComp('emc-sample-result-input-list-grid').refresh();
                        });
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.MERGE_SAMPLE',
                    icon : 'edit',
                    hidden : true,
                    onClick : function() {
                        _this.mergeSample();
                    }
                } ]
            }, {
                type : 'dropDownMenu',
                text : 'GIKAM.BUTTON.PREVIEW',
                items : [ {
                    type : 'button',
                    text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.VIEW_SAMPLING_REPORT',
                    icon : 'print',
                    onClick : function() {
                        var selections = Gikam.getComp('emc-sample-result-input-list-project-sample-grid').getSelections();
                        if (selections.length !== 1) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                            return;
                        }
                        Gikam.viewReport(selections[0].reportConfigLineId, selections[0]);
                    },
                }, {
                    type : 'button',
                    text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.VIEW_CALIBRATION_REPORT',
                    icon : 'print',
                    onClick : function() {
                        var selections = Gikam.getComp('emc-sample-result-input-list-project-sample-grid').getSelections();
                        if (selections.length !== 1) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                            return;
                        }
                        Gikam.viewReport(selections[0].caliReportLineId, selections[0]);
                    }
                } ]
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.ANNEX',
                icon : 'upload',
                onClick : function() {
                    _this.uploadSampleFile();
                }
            } , {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.INSTRUMENT_ADJUSTMENT',
                icon : 'material-to-check',
                onClick : function() {
                    var selections = Gikam.getComp('emc-sample-result-input-list-project-sample-grid').getSelections();
                    if (selections.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    Gikam.create('modal', {
                        width : '80%',
                        title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.INSTRUMENT_ADJUSTMENT',
                        url : IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipt-calibrations/page/edit-list',
                        onAfterClose : function (data) {
                            if (!Gikam.isEmpty(data)) {
                                var calibrationIds = data.map(function (item) {
                                    return item.id;
                                }).join();
                                selections.forEach(function (item) {
                                    item.calibrationId = calibrationIds;
                                });
                                Gikam.put(emcSample.baseUrl + '/' + selections[0].id, Gikam.getJsonWrapper({}, [ null, selections ]));
                            }
                        }
                    });
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SAMPLING_EQUIPMENT',
                icon : 'modify',
                hidden : false,
                onClick : function() {
                    var selections = Gikam.getComp('emc-sample-result-input-list-project-sample-grid').getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }

                    var ids = selections.map(row => row.id).join();

                    Gikam.create('modal', {
                        width : '60%',
                        title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SAMPLING_EQUIPMENT',
                        url : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/page/sampling-equipt' + Gikam.param({
                            ids : ids
                        })
                    });
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.BATCH_INPUT_DETAIL',
                icon : 'modify',
                hidden : false,
                onClick : function() {
                    _this.batchInputSampleDetail();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.BATCH_INPUT_DATE',
                icon : 'modify',
                onClick : function() {
                    _this.batchInputSampleDate();
                }
            } ]
        }
    },

    /** 批录入采样单详情 */
    batchInputSampleDetail : function() {
        var selections = Gikam.getComp('emc-sample-result-input-list-project-sample-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var orderCategoryEmpty = selections.some(row => Gikam.isEmpty(row.orderCategory));
        if (orderCategoryEmpty) {
            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.ORDER_CATEGORY_EMPTY');
            return;
        }

        var orderCategories = Array.from(new Set(selections.map(row => row.orderCategory)));
        if (orderCategories.length > 1) {
            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.ORDER_CATEGORY_DIFFERENT');
            return;
        }

        var sampleIds = selections.map(row => row.id).join();
        Gikam.create('modal', {
            width : '80%',
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.BATCH_INPUT_DETAIL',
            url : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/page/sample-detail' + Gikam.param({
                id_IN : sampleIds
            })
        });
    },

    /** 批录入采样时间 */
    batchInputSampleDate : function() {
        var selections = Gikam.getComp('emc-sample-result-input-list-project-sample-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var sampleIds = selections.map(row => row.id).join();
        Gikam.create('modal', {
            width : '80%',
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.BATCH_INPUT_DATE',
            url : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/page/sample-date' + Gikam.param({
                sampleId_IN : sampleIds
            })
        });
    },

    // 样品和测试
    getOrderTestGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sample-result-input-list-data-input-order-task-grid',
            service : 'emcOrderServiceImpl',
            columns : emcResultInputSample.getSampleTestColumns(),
            deleteFormatter : function(row) {
                return row.orderNo || row.id;
            },
            onRowActive : function(index, row) {
                _this.onSampleOrderActive(row);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-sample-result-input-list-data-input-container-info-grid','emc-sample-result-input-list-data-input-test-info-grid' ]);
                }
            },
            rowStyleFormatter : function(row) {
                if (row.termination === '1') {
                    return 'background-color : #FFFF99';
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.ADD_ORDER',
                icon : 'add',
                hidden : true,
                onClick : function() {
                    _this.addSampleTest();
                },
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DEL_ORDER',
                icon : 'remove',
                hidden : true,
                onClick : function() {
                    var delOrder = function() {
                        var selections = Gikam.getComp('emc-sample-result-input-list-data-input-order-task-grid').getSelections();
                        if (Gikam.isEmpty(selections)) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                            return;
                        }
                        var url = emcResultInputSample.baseUrl + '/orders';
                        workspace.window.showMask(true);
                        Gikam.del(url, Gikam.getJsonWrapper(null, [ '', selections ])).done(function() {
                            Gikam.getComp('emc-sample-result-input-list-data-input-order-task-grid').refresh();
                            Gikam.cleanCompData([ 'emc-sample-result-input-list-data-input-container-info-grid','emc-sample-result-input-list-data-input-test-info-grid' ]);
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                    Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.DEL_ORDER', "EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.CONFIRM_DELETE", delOrder, "");
                },
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BATCH_INPUT',
                icon : 'edit',
                hidden : _this.getHidden(),
                onClick : function() {
                    _this.updateSampleTestBatch();
                },
            }, {
                type : 'dropDownMenu',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.TERMINATION',
                hidden : _this.getHidden(),
                items : [ {
                    type : 'button',
                    text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.TERMINATION_FOLDER',
                    icon : 'edit',
                    onClick : function() {
                        _this.terminateFolder('1');
                    }
                }, {
                    type : 'button',
                    text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.TERMINATION_FOLDER_NO',
                    icon : 'edit',
                    onClick : function() {
                        _this.terminateFolder('0');
                    }
                }, {
                    type : 'button',
                    text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.TERMINATION_ORDER',
                    icon : 'edit',
                    onClick : function() {
                        _this.terminateOrder('1');
                    }
                }, {
                    type : 'button',
                    text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.TERMINATION_ORDER_NO',
                    icon : 'edit',
                    onClick : function() {
                        _this.terminateOrder('0');
                    }
                } ]
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.DATE_AUTO_FILL',
                icon : 'edit',
                onClick : function() {
                    _this.autoFillSampleDate();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.ORDER_DESC_FILL',
                icon : 'edit',
                onClick : function() {
                    _this.fillOrderDesc();
                }
            } ]
        }
    },

    fillOrderDesc : function() {
        var grid = Gikam.getComp('emc-sample-result-input-list-data-input-order-task-grid');
        var rows = grid.getSelections();
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        Gikam.create('modal', {
            width : 600,
            height : 313,
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ORDER_DESC_FILL',
            url : emcResultInputSample.baseUrl + '/page/order-desc-detail',
            onAfterClose : function(data) {
                if (!Gikam.isEmpty(data)) {
                    var formData = grid.getActivedRow();
                    workspace.window.showMask(true);
                    var orderDesc = '';
                    if (!Gikam.isEmpty(data.orderColor)) {
                        orderDesc += data.orderColor + '、';
                    }
                    if (!Gikam.isEmpty(data.orderSmell)) {
                        orderDesc += data.orderSmell + '、';
                    }
                    if (!Gikam.isEmpty(data.orderOilSlick)) {
                        orderDesc += data.orderOilSlick + '、';
                    }
                    if (!Gikam.isEmpty(data.orderFloater)) {
                        orderDesc += data.orderFloater + '、';
                    }
                    orderDesc = orderDesc.substring(0, orderDesc.length - 1);
                    rows.map(function(item) {
                        item.orderDesc = orderDesc;
                    });
                    Gikam.put(emcResultInputSample.baseUrl + '/' + formData.id, Gikam.getJsonWrapper({}, [ 'emcOrderServiceImpl', rows ])).done(function() {
                        grid.refresh()
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    terminateFolder : function(termination) {
        var gridData = Gikam.getComp('emc-sample-result-input-list-data-input-order-task-grid').getSelections();
        if (gridData.length > 0) {
            workspace.window.showMask(true);
            Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/action/terminate-folder', Gikam.getJsonWrapper({
                termination : termination
            }, [ 'emcOrderServiceImpl', gridData ])).done(function() {
                Gikam.getComp('emc-sample-result-input-list-data-input-order-task-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    terminateOrder : function(termination) {
        var gridData = Gikam.getComp('emc-sample-result-input-list-data-input-order-task-grid').getSelections();
        if (gridData.length > 0) {
            workspace.window.showMask(true);
            Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/terminate-order', Gikam.getJsonWrapper({
                termination : termination
            }, [ 'emcOrderServiceImpl', gridData ])).done(function() {
                Gikam.getComp('emc-sample-result-input-list-data-input-order-task-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    updateItemGroup : function() {
        var selections = Gikam.getComp('emc-sample-result-input-list-order-prepare-grid').getSelections();
        if (selections.length <= 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var orderIds = "";
        selections.forEach(function(item) {
            orderIds += item.id + ',';
        });
        orderIds = orderIds.substring(0, orderIds.length - 1);
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.TEST_EDIT',
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/page/order-item-group-sample-edit-list' + Gikam.param({
                monitorTmplId : selections[0].monitorTmplId,
                orderCategory : selections[0].orderCategory,
                orderIds : orderIds
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-sample-result-input-list-order-container-info-grid').refresh();
            }
        });
    },

    /** 设置分包商 */
    addSubcontractor : function() {
        var selections = Gikam.getComp('emc-sample-result-input-list-data-input-test-info-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var orders = Gikam.getComp('emc-sample-result-input-list-data-input-order-task-grid').getSelections();
        Gikam.create('modal', {
            width : '20%',
            height : '50%',
            title : Gikam.choose.getConfig('subcontractor').title,
            url : Gikam.choose.getConfig('subcontractor').url + Gikam.param({
                single : true,
            }),
            onAfterClose : function(data) {
                if (!Gikam.isEmpty(data)) {
                    workspace.window.showMask(true);
                    selections.map(function(item) {
                        item.subcontractorId = data[0].id;
                    });
                    Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/edit-subcontractor', Gikam.getJsonWrapper({}, ['', orders], [ '', selections ])).done(function() {
                        Gikam.getComp('emc-sample-result-input-list-data-input-test-info-grid').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    /** 删除分包商 */
    delSubcontractor : function() {
        var selections = Gikam.getComp('emc-sample-result-input-list-data-input-test-info-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.CONFIRM_TO_EDIT', 'EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.CONFIRM_DELETE_SUBCONTRACTOR', function() {
            var orders = Gikam.getComp('emc-sample-result-input-list-data-input-order-task-grid').getSelections();
            workspace.window.showMask(true);
            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/delete-subcontractor', Gikam.getJsonWrapper({}, ['', orders], [ '', selections ])).done(function() {
                Gikam.getComp('emc-sample-result-input-list-data-input-test-info-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        })
    },

    // 数据录入 容器信息
    getOrderContainerGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            width : Gikam.width.XL,
            panels : [ {
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.CONTAINER_INFO',
                items : [ {
                    type : 'grid',
                    id : 'emc-sample-result-input-list-data-input-container-info-grid',
                    service : 'emcOrderTaskServiceImpl',
                    columns : emcResultInputSample.getSamplePrepareOrderContainerColumns(),
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        }
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONTAINER_UPDATE',
                        icon : 'modify',
                        onClick : function() {
                            _this.updateDataInputOrderTask();
                        },
                    } ]
                }, ]
            }, {
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.TAB.SAMPLE_PREPARE_CONTAINER.BASE_INFO',
                items : [ {
                    type : 'grid',
                    id : 'emc-sample-result-input-list-data-input-test-info-grid',
                    service : 'emcOrderTaskServiceImpl',
                    columns : emcResultInputSample.getSampleTestListColumns(),
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        }
                    },
                    toolbar : [ {
                        type : 'dropDownMenu',
                        text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.SUBCONTRACTOR',
                        items : [ {
                            type : 'button',
                            text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.ADD_SUBCONTRACTOR',
                            icon : 'add',
                            onClick : function() {
                                _this.addSubcontractor();
                            }
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.DEL_SUBCONTRACTOR',
                            icon : 'remove-row',
                            onClick : function() {
                                _this.delSubcontractor();
                            }
                        } ]
                    } ]
                }, ]
            } ]
        }
    },

    loadSamplePropertyPage : function(id, tmplId, monitorTmplId) {
        // 查询 sample 的时候，如果存在样品，则需要更新 采样单的 monitorTmplId，并插入数据到 property data 表
        Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/tmpls/' + id + '/judge-samples/' + monitorTmplId).done(function() {
            Gikam.getComp('emc-sample-result-input-list-project-sample-grid').refresh();
        });
        // 打开采样单属性的时候，看 模板 id 是否存在，没有就要提示进行添加；有的话要判断是否添加过
        if (Gikam.isEmpty(monitorTmplId)) {
            Gikam.alert("EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.UNSET_SAMPLE_PROPERTY");
            return;
        }

        var _this = this;
        Gikam.create('modal', {
            width : '60%',
            bodyStyle : {
                padding : '0px 20px'
            },
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.DETAIL_INFO',
            url : emcResultInputSample.baseUrl + '/page/info-detail' + Gikam.param({
                id : id,
                tmplId : tmplId,
                monitorTmplId : monitorTmplId,
            }),
        });
    },

    // 采样日期自动录入
    autoFillSampleDate : function() {
        var sampleRow = Gikam.getComp('emc-sample-result-input-list-project-sample-grid').getActivedRow();
        if (Gikam.isEmpty(sampleRow)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_SAMPLE_DATA');
            return;
        }
        var orderRows = Gikam.getComp('emc-sample-result-input-list-data-input-order-task-grid').getData();
        if (Gikam.isEmpty(orderRows)) {
            return;
        }
        if ((Gikam.isEmpty(orderRows[0].sampleBeginDate) || Gikam.isEmpty(orderRows[0].sampleBeginTime))
            || (Gikam.isEmpty(orderRows[0].sampleCompleteDate) || Gikam.isEmpty(orderRows[0].sampleCompleteTime))) {
            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.ORDER_SAMPLE_DATETIME_ALL_INCOMPLETE');
            return;
        }
        var modal = Gikam.create('modal', {
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ORDER_SAMPLE_INTERVAL',
            width : 310,
            height : 150,
            showCloseBtn : true,
            onAfterClose : function(interval) {
                if(Gikam.isEmpty(interval) || orderRows.length <= 1) {
                    return;
                }
                var firstOrderRow = orderRows[0];
                var firstBeginDateTime = firstOrderRow.sampleBeginDate + ' ' + firstOrderRow.sampleBeginTime;
                var firstCompleteDateTime = firstOrderRow.sampleCompleteDate + ' ' + firstOrderRow.sampleCompleteTime;
                var orders  = [];
                for(var index = 1; index < orderRows.length; index++) {
                    //计算时间
                    var beginDateTime = new Date(Gikam.DateUtils.toDateTime(firstBeginDateTime).valueOf() + parseInt(interval)*index*60*1000);
                    var beginDateTimeStr = Gikam.DateUtils.formatter(beginDateTime, 'yyyy-MM-dd hh:mm');
                    var sampleBeginDateStr = beginDateTimeStr.substr(0, 10);
                    var sampleBeginTimeStr = beginDateTimeStr.substr(11);
                    var completeDateTime = new Date(Gikam.DateUtils.toDateTime(firstCompleteDateTime).valueOf() + parseInt(interval)*index*60*1000);
                    var completeDateTimeStr = Gikam.DateUtils.formatter(completeDateTime, 'yyyy-MM-dd hh:mm');
                    var sampleCompleteDateStr = completeDateTimeStr.substr(0, 10);
                    var sampleCompleteTimeStr = completeDateTimeStr.substr(11);
                    //构造要更新的样品数据
                    var orderRow = orderRows[index];
                    orders.push({
                        id : orderRow.id,
                        sampleBeginDate : sampleBeginDateStr,
                        sampleBeginTime : sampleBeginTimeStr,
                        sampleCompleteDate : sampleCompleteDateStr,
                        sampleCompleteTime : sampleCompleteTimeStr
                    });
                }
                workspace.window.showMask(true);
                Gikam.put(emcResultInputSample.baseUrl + '/' + sampleRow.id + '/orders/sample-date-time', Gikam.getJsonWrapper({}, [ null, orders ])).done(function(data) {
                    Gikam.getComp('emc-sample-result-input-list-data-input-order-task-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
        Gikam.create('layout', {
            renderTo : modal.window.$dom,
            center : {
                items : [ {
                    type : 'form',
                    id : 'sample-interval-modal-form',
                    columns : 1,
                    titleWidth : 90,
                    fields : [ {
                        field : 'interval',
                        title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.ORDER_SAMPLE_INTERVAL',
                        type : 'number'
                    } ]
                } ]
            },
            south : {
                items : [ {
                    type : 'btnToolbar',
                    items : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        onClick : function() {
                            var form = Gikam.getComp('sample-interval-modal-form');
                            var data = form.getData();
                            if(Gikam.isEmpty(data.interval)) {
                                Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.ORDER_SAMPLE_INTERVAL_NULL');
                                return;
                            }
                            modal.close(data.interval);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
                        onClick : function() {
                            modal.close();
                        }
                    } ]
                } ]
            }
        });
    },

    loadDataInputPage : function(id) {
        var _this = this;
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.DATA_INPUT',
            url : emcResultInputSample.baseUrl + '/page/data-input-detail' + Gikam.param({
                id : id,
            }),
        });
    },

    uploadFolderPic : function() {
        var _this = this
        var rows = Gikam.getComp('emc-sample-result-input-list-project-sample-grid').getSelections();
        if (rows && rows.length === 1) {
            Gikam.create('modal', {
                width : 800,
                height : 500,
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.FOLDER_PIC',
                url : emcResultInputSample.baseUrl + '/page/sample-folder-pic-list?id=' + rows[0].id
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    uploadSampleFile : function() {
        var _this = this
        var rows = Gikam.getComp('emc-sample-result-input-list-project-sample-grid').getSelections();
        if (rows && rows.length === 1) {
            Gikam.create('modal', {
                width : 800,
                height : 500,
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ANNEX',
                url : emcResultInputSample.baseUrl + '/page/sample-annex-list?id=' + rows[0].id
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    createResultInput : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-folder-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.TAB.DATA_INPUT.BASE_INFO',
                        items : [ {
                            type : 'layout',
                            west : {
                                width : 230,
                                // 结果录入-数据录入
                                items : [ _this.getResultInputDataInputGridParam() ]
                            },
                            center : {
                                items : [ {
                                    type : 'layout',
                                    north : {
                                        // 按采样单
                                        height : 200,
                                        items : [ this.getProjectSampleGridParam() ]
                                    },
                                    center : {
                                        width : '50%',
                                        items : [ {
                                            id : 'emc-sample-result-input-list-order-container-layout',
                                            type : 'layout',
                                            center : {
                                                title : Gikam.propI18N('EMC.MODULE.BP.SAMPLE.SAMPLES.TAB.ORDERS_TEST.BASE_INFO'),
                                                width : '50%',
                                                // 样品测试
                                                items : [ this.getOrderTestGridParam() ]
                                            },
                                            east : {
                                                width : '50%',
                                                collapse : true,
                                                // 容器信息
                                                items : [ this.getOrderContainerGridParam() ]
                                            }
                                        } ]
                                    }
                                } ]
                            },
                        } ]
                    } ]
                } ]
            },
        });
    },

    deleteOrder : function() {
        var selections = Gikam.getComp('emc-sample-result-input-list-order-prepare-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var url = '/secure/emc/module/bp/bp/orders';
        Gikam.del(url, Gikam.getJsonWrapper(null, [ '', selections ])).done(function() {
            Gikam.getComp('emc-sample-result-input-list-order-prepare-grid').refresh();
            Gikam.getComp('emc-sample-result-input-list-order-container-info-grid').refresh();
            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.DEL_SUCCESS');
        });
    },

    deleteNotQcOrder : function() {
        var selections = Gikam.getComp('emc-sample-result-input-list-order-prepare-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var hasNonQCOrder = false
        selections.forEach(function(row) {
            if (!row.qcCategoryId && !row.qcCategoryCode && !row.qcName) {
                hasNonQCOrder = true
            }
        })
        if (hasNonQCOrder) {
            Gikam.alert('EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.DELETEORDERTIP');
            return;
        }
        Gikam.getComp('emc-sample-result-input-list-order-prepare-grid').deleteRows(IFM_CONTEXT + '/secure/emc/module/bp/order/order-receipts/orders').done(function() {
            Gikam.getComp('emc-sample-result-input-list-grid').refresh();
            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.DEL_SUCCESS');
        });
    },

    updateOrder : function() {
        var selections = Gikam.getComp('emc-sample-result-input-list-order-prepare-grid').getSelections();
        if (selections.length > 0) {
            var activedRow = Gikam.getComp('emc-sample-result-input-list-grid').getActivedRow();
            Gikam.create('modal', {
                width : 600,
                height : 313,
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.BATCH_INPUT',
                url : emcResultInputSample.baseUrl + '/page/order-batch',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        selections.map(function(item) {
                            item.sampleBeginDate = data.sampleBeginDate;
                            item.sampleBeginTime = data.sampleBeginTime;
                            item.sampleCompleteDate = data.sampleCompleteDate;
                            item.sampleCompleteTime = data.sampleCompleteTime;
                            item.orderDesc = data.orderDesc;
                        });
                        Gikam.put(emcResultInputSample.baseUrl + '/' + activedRow.id, Gikam.getJsonWrapper({}, [ 'emcOrderServiceImpl', selections ])).done(function() {
                            Gikam.getComp('emc-sample-result-input-list-order-prepare-grid').refresh();
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

    // 复制为QC样
    copyQcOrder2 : function() {
        var _this = this
        var selections = Gikam.getComp('emc-sample-result-input-list-order-prepare-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        if (selections.length > 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        if (selections[0].qcCategoryId) {
            Gikam.alert('EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.COPYQCORDERTIP');
            return;
        }
        Gikam.create('modal', {
            width : 500,
            title : Gikam.choose.getConfig('orderReceiptQcCategory').title,
            url : Gikam.choose.getConfig('orderReceiptQcCategory').url + Gikam.param({
                category : 'scene',
                addNum : true
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var totalSelections = [];
                rows.forEach(function(row) {
                    var newItem = JSON.parse(JSON.stringify(selections[0]));
                    newItem.projId = selections[0].projId;
                    newItem.qcCategoryId = row.id;
                    newItem.qcCategoryCode = row.qcCategoryId;
                    newItem.qcName = row.qcName;
                    newItem.ext$.parOrder = row.parOrder;
                    newItem.ext$.updateParNo = row.updateParNo;
                    newItem.ext$.addnum = row.addNum;
                    totalSelections.push(newItem);
                })
                workspace.window.showMask(true);
                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/order/order-receipts/qc-orders/action/copy', Gikam.getJsonWrapper({}, [ '', totalSelections ])).done(function(data) {
                    Gikam.getComp('emc-sample-result-input-list-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    copyQcOrder : function() {
        var _this = this
        var selections = Gikam.getComp('emc-sample-result-input-list-order-prepare-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        if (selections.length > 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        if (selections[0].qcCategoryId) {
            Gikam.alert('EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.COPYQCORDERTIP');
            return;
        }
        Gikam.create('modal', {
            width : 500,
            title : Gikam.choose.getConfig('orderReceiptQcCategory').title,
            url : Gikam.choose.getConfig('orderReceiptQcCategory').url + Gikam.param({
                category : 'scene',
                addNum : true
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                var existedAddFolderQc = rows.some(function (qc) {
                    return qc.addFolder === '1';
                });

                workspace.window.showMask(true);
                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/order/order-receipts/qc-orders/action/copy', Gikam.getJsonWrapper({}, [ '', selections ], [ '', rows ] )).done(function(data) {
                    if (existedAddFolderQc) {
                        Gikam.getComp('emc-sample-result-input-list-grid').refresh();
                    } else {
                        Gikam.getComp('emc-sample-result-input-list-order-prepare-grid').refresh();
                    }
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    // 删除QC样
    deleteQcOrder : function() {
        var _this = this
        var selections = Gikam.getComp('emc-sample-result-input-list-order-prepare-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var hasNonQCOrder = false
        selections.forEach(function(row) {
            if (!row.qcCategoryId || !row.qcCategoryCode || !row.qcName) {
                hasNonQCOrder = true
            }
        })
        if (hasNonQCOrder) {
            Gikam.alert('EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.DELETEQCORDERTIP');
            return;
        }
        Gikam.getComp('emc-sample-result-input-list-order-prepare-grid').deleteRows(IFM_CONTEXT + '/secure/emc/module/bp/order/order-receipts/qc-orders').done(function() {
            Gikam.getComp('emc-sample-result-input-list-grid').refresh();
        });
    },

    mergeSample : function() {
        var selections = Gikam.getComp('emc-sample-result-input-list-project-sample-grid').getSelections();
        if (selections.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }
        var orderCategory = selections.map(function(e) {
            return e.orderCategory;
        });
        orderCategory = this.unique(orderCategory);

        if (orderCategory.length > 1) {
            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.TOO_MANY_ORDERCATEGORY');
            return;
        }

        var url = emcResultInputSample.baseUrl + '/action/merge-samples';
        workspace.window.showMask(true);
        Gikam.postText(url, Gikam.getJsonWrapper(null, [ '', selections ])).done(function() {
            Gikam.getComp('emc-sample-result-input-list-order-prepare-grid').refresh();
            Gikam.getComp('emc-sample-result-input-list-project-sample-grid').refresh();
            Gikam.getComp('emc-sample-result-input-list-order-container-info-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    updateSampleTestBatch : function() {
        var selections = Gikam.getComp('emc-sample-result-input-list-data-input-order-task-grid').getSelections();
        if (selections.length > 0) {
            var formData = Gikam.getComp('emc-sample-result-input-list-project-grid').getActivedRow();
            Gikam.create('modal', {
                width : 600,
                height : 313,
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.BATCH_INPUT',
                url : emcResultInputSample.baseUrl + '/page/sample-test-batch',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        selections.map(function(item) {
                            item.sampleBeginDate = data.sampleBeginDate;
                            item.sampleBeginTime = data.sampleBeginTime;
                            item.sampleCompleteDate = data.sampleCompleteDate;
                            item.sampleCompleteTime = data.sampleCompleteTime;
                        });
                        Gikam.put(emcResultInputSample.baseUrl + '/' + formData.id, Gikam.getJsonWrapper({}, [ 'emcOrderServiceImpl', selections ])).done(function() {
                            Gikam.getComp('emc-sample-result-input-list-data-input-order-task-grid').refresh()
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

    // 采样准备 容器信息修改
    updateOrderTask : function() {
        var selections = Gikam.getComp('emc-sample-result-input-list-order-container-info-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_CONTAINER_DATA');
            return;
        }

        var orderSelections = Gikam.getComp('emc-sample-result-input-list-order-prepare-grid').getSelections();
        if (orderSelections.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ORDER_DATA');
            return;
        }
        var originSelections = selections.map(function(item) {
            return Object.assign({}, item)
        });
        var avtivedRow = Gikam.getComp('emc-sample-result-input-list-order-prepare-grid').getActivedRow(); // 样品行
        Gikam.create('modal', {
            width : 800,
            height : 600,
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.TAB.CONTAINER_INFO',
            url : emcResultInputSample.baseUrl + '/page/order-prepare-container',
            onAfterClose : function(data) {
                Gikam.getLastModal().close();
            }
        });
    },

    // 数据录入 容器信息修改
    updateDataInputOrderTask : function() {
        var orderTaskselections = Gikam.getComp('emc-sample-result-input-list-data-input-container-info-grid').getSelections();
        if (orderTaskselections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_CONTAINER_DATA');
            return;
        }
        var orderSelections = Gikam.getComp('emc-sample-result-input-list-data-input-order-task-grid').getSelections();
        if (orderSelections.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ORDER_DATA');
            return;
        }

        Gikam.create('modal', {
            width : 800,
            height : 600,
            id : 'emc-sample-edit-order-task-detail-modal',
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.TAB.CONTAINER_INFO',
            url : emcResultInputSample.baseUrl + '/page/order-container',
            onAfterClose : function(data) {
                Gikam.getLastModal().close();
            }
        });
    },

    // （样品和测试）添加样品
    addSampleTest : function() {
        var _this = emcResultInputSample.editPage;
        var activedRow = Gikam.getComp('emc-sample-result-input-list-project-sample-grid').getActivedRow();
        if (Gikam.isEmpty(activedRow)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_SAMPLE_DATA');
            return;
        }

        if (activedRow.orderCategory === undefined) { // 当前采样单无样品类型，过滤条件无需增加类型判断
            Gikam.create('modal', {
                title : Gikam.choose.getConfig('order').title,
                url : Gikam.choose.getConfig('order').url + Gikam.param({
                    targetOrder_unassigned : '1',
                    targetOrderTable : 'T_EMC_ORDER',
                    targetOrderColumn : 'ID',
                    targetOrderIdFilterColumn : 'SAMPLEID',
                    targetOrderIdFilterValue : activedRow.id,
                    projId_SEQ : activedRow.projId,
                    folderstatus_SEQ : 'done',
                    targetOrderSample_unassigned : '1',
                }),
                onAfterClose : function(rows, index) {
                    if (Gikam.isEmpty(rows)) {
                        return;
                    }
                    // 判断选择的类型是否相同
                    var orders = rows[0].orderCategory;
                    var flag = rows.some(function(item) {
                        return item.orderCategory !== orders;
                    });
                    if (flag) {
                        Gikam.alert('EMC.MODULE.BP.BP.FOLDERS.TIP.CHOOSE_ONE_ORDER_TYPE');
                        return;
                    }
                    workspace.window.showMask(true);
                    Gikam.postText(emcResultInputSample.baseUrl + '/' + activedRow.id + '/orders', Gikam.getJsonWrapper({}, [ null, rows ])).done(function(data) {
                        Gikam.getComp('emc-sample-result-input-list-data-input-order-task-grid').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            });
            return;
        }
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('order').title,
            url : Gikam.choose.getConfig('order').url + Gikam.param({

                target_unassigned : '1',
                targetSampleTable : 'T_EMC_SAMPLE',
                targetSampleIdColumn : 'ORDERCATEGORY',
                targetSampleCategoryFilterColumn : 'ORDERCATEGORY',
                targetSampleCategoryFilterValue : activedRow.orderCategory,
                folderstatus_SEQ : 'done',
                targetOrder_unassigned : '1',
                targetOrderTable : 'T_EMC_ORDER',
                targetOrderColumn : 'ID',
                targetOrderIdFilterColumn : 'SAMPLEID',
                targetOrderIdFilterValue : activedRow.id,
                projId_SEQ : activedRow.projId,
                targetOrderSample_unassigned : '1',
            }),
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var orders = rows[0].orderCategory;
                var flag = rows.some(function(item) {
                    return item.orderCategory !== orders;
                });
                if (flag) {
                    return;
                }
                workspace.window.showMask(true);
                Gikam.postText(emcResultInputSample.baseUrl + '/' + activedRow.id + '/orders', Gikam.getJsonWrapper({}, [ null, rows ])).done(function(data) {
                    Gikam.getComp('emc-sample-result-input-list-data-input-order-task-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    // 去重方法
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

    getHidden : function() {
        if (this.param.sampleStatus == 'reportundo') {
            return true;
        } else {
            return false;
        }
    },

    // 工程详情页面
    loadProjDetailPage : function(projNo) {
        Gikam.create('modal', {
            id : 'projModal',
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.PROJ_DETAIL',
            width : 980,
            height : 500,
            showCloseBtn : true,
            url : IFM_CONTEXT + '/secure/emc/module/bp/project/projects/page/project-search-detail' + Gikam.param({
                projNo : projNo
            }),
        });
    },

    // 仪器校准详情页码
    loadCalibrationPage : function(id) {
        Gikam.create('modal', {
            width : '80%',
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.INSTRUMENT_ADJUSTMENT',
            url : IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipt-calibrations/page/detail-list' + Gikam.param({
                id : id,
            }),
        });
    },

    init : function(param) {
        this.param = param;
        this.createResultInput();
    }
};
