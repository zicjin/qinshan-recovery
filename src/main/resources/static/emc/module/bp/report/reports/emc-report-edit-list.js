emcReport.editPage = {

    getProjectParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-report-edit-project-list-grid',
            service : 'emcProjectServiceImpl',
            url : '/secure/emc/module/bp/project/projects/select-by-reportedit/queries',
            columns : emcReport.getProjectListColumns(),
            page : true,
            filterOpen : true,
            order: {
                urgent: 'desc'
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-report-edit-list-grid' ]);
                }
            },
            requestData : {
                status_SEQ : 'reportEdit',
                targetType : 'reportEdit',
                currentUserId : workspace.user.id,
                org_filter : '1',
            },
            onRowActive : function(index, row) {
                if(row.sample === '1') {
                    Gikam.getComp('emc-report-edit-grid-tab').removePanel('emc-report-edit-grid-sample-send-tab');
                    Gikam.getComp('emc-report-edit-grid-tab').revertPanel('emc-report-edit-grid-sample-tab');
                } else {
                    Gikam.getComp('emc-report-edit-grid-tab').removePanel('emc-report-edit-grid-sample-tab');
                    Gikam.getComp('emc-report-edit-grid-tab').revertPanel('emc-report-edit-grid-sample-send-tab');
                }
                Gikam.getAsyncComp('emc-report-edit-list-grid').done(function(grid) {
                    grid.refresh({
                        url : emcReport.baseUrl + '/queries/raw',
                        requestData : {
                            projId_SEQ : row.id,
                            oa_filter : '1',
                        }
                    });
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.UNDO',
                icon : 'back',
                hidden : false,
                onClick : function() {
                    _this.undo();
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submitProject();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.CREATE_REPORT',
                icon : 'add',
                onClick : function() {
                    var selections = Gikam.getComp('emc-report-edit-project-list-grid').getSelections();
                    if (selections.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    Gikam.create('modal', {
                        title : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.SELECT_FOLDER',
                        url : emcReport.baseUrl + '/page/folder-choose-list' + Gikam.param({
                            projId_SEQ : selections[0].id,
                            targetType : 'reportFolder',
                            // status_SEQ : 'done'
                        }),
                        onAfterClose : function (data) {
                            if (Gikam.isEmpty(data)) {
                                return;
                            }

                            workspace.window.showMask(true);
                            Gikam.postText(emcReport.baseUrl + '/action/create', Gikam.getJsonWrapper({}, [ 'emcFolderServiceImpl', data.folders ], [ 'emcOrderTaskServiceImpl', data.orderTasks ])).done(function () {
                                Gikam.getComp('emc-report-edit-list-grid').refresh();
                            }).always(function () {
                                workspace.window.closeMask();
                            });
                        }
                    });
                }
            }, {
                     type : 'button',
                     text : '报表预览',
                     icon : 'print',
                     onClick : function() {
                         Gikam.viewReport('rasproject', Gikam.getComp('emc-report-edit-project-list-grid').getSelections());
                     }
             }, {
                type : 'button',
                text : '按测试出报告',
                icon : 'add',
                onClick : function() {
                    var selections = Gikam.getComp('emc-report-edit-project-list-grid').getSelections();
                    if (selections.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    Gikam.create('modal', {
                        title : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.SELECT_FOLDER',
                        url : emcReport.baseUrl + '/page/ordertask-choose-list' + Gikam.param({
                            projId_SEQ : selections[0].id,
                            status_SEQ : 'done',
                            TESTCAPCITYORGID_SEQ : workspace.user.ext$.parentorgid,

                        }),
                        onAfterClose : function (data) {
                            if (Gikam.isEmpty(data)) {
                                return;
                            }

                            workspace.window.showMask(true);
                            Gikam.postText(emcReport.baseUrl + '/action/create', Gikam.getJsonWrapper({},[ 'emcProjectServiceImpl', selections],  [ 'emcOrderTaskServiceImpl', data.orderTasks ])).done(function () {
                                Gikam.getComp('emc-report-edit-list-grid').refresh();
                            }).always(function () {
                                workspace.window.closeMask();
                            });
                        }
                    });
                }
            // },
//            {
//                type : 'dropDownMenu',
//                text : 'GIKAM.BUTTON.PREVIEW',
//                items : [ {
//                    type : 'button',
//                    text : 'GIKAM.BUTTON.PREVIEW',
//                    icon : 'print',
//                    onClick : function() {
//                        Gikam.viewReport('reportRecord', Gikam.getComp('emc-report-edit-project-list-grid').getSelections());
//                    }
//                // }, {
//                //     type : 'button',
//                //     text : '查看原始记录',
//                //     icon : 'print',
//                //     onClick : function() {
//                //         let selections = Gikam.getComp('emc-report-edit-project-list-grid').getSelections();
//                //         if (selections.length !== 1) {
//                //             Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
//                //             return;
//                //         }
//                //
//                //         Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
//                //             f : {
//                //                 targetId_SEQ : 'T_EMC_PROJECT$' + selections[0].id,
//                //                 ext_SEQ : 'analysisRecordPdf',
//                //             },
//                //             n : -1,
//                //             s : 50
//                //         })).done(function (r) {
//                //             // 如果有生成原始记录PDF，直接加载PDF文件，否则加载报表
//                //             if (r.rows && r.rows.length > 0) {
//                //                 Gikam.openFile(r.rows[0].id)
//                //             } else {
//                //                 workspace.window.showMask();
//                //                 Gikam.post(emcReport.baseUrl + '/action/compose-project-run-record', Gikam.getJsonWrapper({},[ 'emcProjectServiceImpl', selections])).done(function (data) {
//                //                     Gikam.openFile(r.rows[0].id)
//                //                 }).always(function () {
//                //                     workspace.window.closeMask();
//                //                 });
//                //             }
//                //         })
//                //     }
//                } ]
//            },
//             {
//                 type : 'button',
//                 text : 'GIKAM.WORKFLOW.BUTTON.UNDO',
//                 icon : 'back',
//                 hidden : false,
//                 onClick : function() {
//                     _this.projectUndo();
//                 }
            },{
                type: 'form',
                autoSave: false,
                fields: _this.getShowAllField()
            } ]
        }
    },

    getShowAllField: function () {
        var _this = this;
        return [{
            title: '显示全部',
            field: 'showFlag',
            type: 'simpleCheckbox',
            value: '1',
            onChange: function (field, value, index) {
                // workspace.window.showMask(true);
                var grid = Gikam.getComp('emc-report-edit-project-list-grid');

                if (value === '1') {
                    grid.refresh({
                        requestData: {
                            org_filter: '',
                        }
                    }).done(function () {
                        // workspace.window.closeMask();
                    });
                } else {
                    grid.refresh({
                        requestData: {
                            org_filter : '1',
                        }
                    }).done(function () {
                        // workspace.window.closeMask();
                    });
                }
            },
        }]
    },

    // 报告数据-上部
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-report-edit-list-grid',
            service : 'emcReportServiceImpl',
            columns : emcReport.getEditListColumns(),
            group : {
                fields : [ 'ext$.projname' ],
                formatter : function(row) {
                    return row.ext$.projname;
                }
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(1);
                } else {
                    Gikam.cleanCompData([ 'emc-folder-edit-list-grid', 'emc-order-edit-list-grid', 'emc-sample-edit-list-gridsample', 'emc-sample-edit-list-gridsend', 'emc-run-edit-list-grid', 'emc-audit-record-edit-list-grid' ])
                    Gikam.getComp('emc-report-edit-list-uploader').grid.cleanData();
                    Gikam.getComp('emc-project-edit-list-uploader').grid.cleanData();
                }
            },
            requestData : {
                status_SEQ : 'reportEdit',
                // targetType : 'reportEdit',
                currentUserId : workspace.user.id,
                org_filter : '1',
            },
            onRowActive : function(index, row) {
                _this.onReportActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.GENERATE_DATA',
                icon : 'add',
                onClick : function() {
                    _this.generateData();
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                hidden : true,
                onClick : function() {
                    _this.submit();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-report-edit-list-grid').deleteRows(emcReport.baseUrl);
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_REPORT',
                icon : 'browse',
                onClick : function() {
                    _this.viewReport();
                }
            }, {
                type : 'button',
                text : '查看word报告(新)',
                icon : 'browse',
                onClick : function() {
                    _this.viewNewReport();
                }
            },{
                type : 'button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_QC_REPORT',
                icon : 'browse',
                hidden : false,
                onClick : function() {
                    _this.viewQcDocument();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_RS_REPORT',
                icon : 'browse',
                hidden : true,
                onClick : function() {
                    _this.viewRsDocument();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_DATA_SHEET',
                icon : 'browse',
                onClick : function() {
                    _this.viewDataSheet();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.POINT_MAP',
                icon : 'edit',
                onClick : function() {
                    _this.loadReportFilePage();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DATA_SUMMARY',
                icon : 'browse',
                hidden : true,
                onClick : function() {
                    _this.dataSummaryProject();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_DATASHEET',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('rasproject', Gikam.getComp('emc-report-edit-list-grid').getSelections());
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.REGROUP_REPORT',
                icon : 'browse',
                hidden : true,
                onClick : function() {
                    _this.regroupProject();
                }
            }, {
                type : 'button',
                text : '查看所有报告',
                icon : 'browse',
                onClick : function() {
                    _this.viewAllReport();
                }
            }, {
                type : 'button',
                text : '批录入',
                icon : 'add',
                onClick : function() {
                    _this.batchInput();
                }
            } ]
        }
    },

    // 报告数据-下部
    getOtherGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.XL,
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.REPORT_FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-report-edit-list-uploader',
                    dbTable : 'T_EMC_REPORT',
                    formatterGridColumns : function() {
                        return emcReport.getReportFileListColumns();
                    },
                } ]
            }, {
                title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.FOLDER_INFO',
                items : [ {
                    type : 'grid',
                    id : 'emc-folder-edit-list-grid',
                    columns : emcReport.getFolderEditListColumns(),
                    toolbar : [ {
                        type : 'button',
                        text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.UPDATE_INDEX',
                        icon : 'edit',
                        onClick : function() {
                            _this.updateTaskIndex();
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.POINT_DIAGRAM',
                        icon : 'edit',
                        onClick : function() {
                            _this.loadFolderFilePage();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.REVIEW',
                        icon : 'browse',
                        onClick : function() {
                            _this.folderReview();
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.COMPARISON_RESULT',
                        icon : 'browse',
                        onClick : function() {
                            _this.comparisonResult();
                        }
                    } ]
                } ]
            }, {
                title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.ORDER_INFO',
                items : [ {
                    type : 'grid',
                    id : 'emc-order-edit-list-grid',
                    columns : emcReport.getOrderEditListColumns()
                } ]
            } ]
        }
    },

    // 详细信息 - 原始数据
    showAllReportResult : true,
    getDetailOriginalGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-detail-original-edit-list-grid',
            url : '',
            filterOpen : true,
            page : true,
            pageSize : 50,
            service : 'emcOrderTaskResultServiceImpl',
            columns : emcReport.getDetailOriginalListColumns(),
            toolbar : [ {
                type : 'form',
                id : 'emc-detail-folder-query-form',
                titleWidthAuto : true,
                titleWidth : 60,
                fields : [ {
                    title : 'T_EMC_REPORT_RESULT.FOLDERNO',
                    field : 'folderId_SEQ',
                    type : 'select',
                    search : true,
                    firstBlank : false,
                    onChange : function(field, folderId) {
                        if (Gikam.isEmpty(folderId)) {
                            return;
                        }
                        Gikam.getComp("emc-detail-original-edit-list-grid").refresh({
                            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/queries/searchable',
                            requestData : {
                                folderId_SEQ : folderId,
                                virtual_IN: ','
                            },
                            headRequestData : {
                                /* 默认展示只在报告中显示的分析项 */
                                reportDisplay_EQ : '1'
                            }
                        });
                        Gikam.getComp("emc-detail-report-edit-list-grid").refresh({
                            url : emcReport.baseUrl + '/results/queries',
                            requestData : {
                                folderId_SEQ : folderId,
                                exceedStandard_SEQ : _this.showAllReportResult ? '' : '0',
                                reportDisplay_SEQ : '1',
                            },
                            order : {
                                testname : 'asc'
                            }
                        });
                    }
                } ]
            }, {
                type : 'button',
                id : 'show-exceed-report-result-button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.SHOW_EXCEED_REPORT_RESULT',
                icon : 'select',
                hidden : true,
                onClick : function() {
                    Gikam.getComp('show-exceed-report-result-button').hide();
                    Gikam.getComp('show-all-report-result-button').show();
                    _this.showAllReportResult = false;
                    var queryForm = Gikam.getComp("emc-detail-folder-query-form");
                    if (queryForm && queryForm.options.data.folderId_SEQ) {
                        Gikam.getComp('emc-detail-report-edit-list-grid').refresh({
                            requestData : {
                                folderId_SEQ : queryForm.options.data.folderId_SEQ,
                                exceedStandard_SEQ : '0'
                            }
                        })
                    }
                }
            }, {
                type : 'button',
                id : 'show-all-report-result-button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.SHOW_REPORT_RESULT_BY_FOLDER',
                icon : 'select',
                hidden : true,
                onClick : function() {
                    Gikam.getComp('show-all-report-result-button').hide();
                    Gikam.getComp('show-exceed-report-result-button').show();
                    _this.showAllReportResult = true;
                    var queryForm = Gikam.getComp("emc-detail-folder-query-form");
                    if (queryForm && queryForm.options.data.folderId_SEQ) {
                        Gikam.getComp('emc-detail-report-edit-list-grid').refresh({
                            requestData : {
                                folderId_SEQ : queryForm.options.data.folderId_SEQ,
                                exceedStandard_SEQ : ''
                            }
                        })
                    }
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.GENERATE_REPORT_DATA',
                icon : 'add',
                onClick : function() {
                    Gikam.confirm('EMC.MODULE.BP.REPORT.REPORTS.MODAL.CONFIRM_GENERATE_DATA_TITLE', 'EMC.MODULE.BP.REPORT.REPORTS.MODAL.CONFIRM_GENERATE_DATA_CONTENT', function() {
                        var activedRow = Gikam.getComp('emc-report-edit-list-grid').getActivedRow();
                        if (activedRow) {
                            var queryForm = Gikam.getComp("emc-detail-folder-query-form");
                            workspace.window.showMask(true);
                            Gikam.postText(emcReport.baseUrl + '/action/generate-report-result', Gikam.getJsonWrapper({
                                folderId : queryForm.options.data.folderId_SEQ
                            }, [ 'emcReportServiceImpl', [ activedRow ] ])).done(function() {
                                workspace.window.closeMask();
                                Gikam.alert('EMC.MODULE.BP.REPORT.REPORTS.TIP.GENERATE_DATA_SUCCESS');
                            }).always(function() {
                                workspace.window.closeMask();
                                Gikam.getComp("emc-detail-report-edit-list-grid").refresh({
                                    url : emcReport.baseUrl + '/results/queries',
                                    requestData : {
                                        folderId_SEQ : queryForm.options.data.folderId_SEQ,
                                        exceedStandard_SEQ : _this.showAllReportResult ? '' : '0'
                                    }
                                });
                            });
                        } else {
                            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        }
                    })
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.DISPLAY_IN_REPORT',
                icon : 'edit',
                onClick : function() {
                    _this.batchEditDisplayInReport('1', 'EMC.MODULE.BP.REPORT.REPORTS.MODAL.CONFIRM_DISPLAY_IN_REPORT');
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.NOT_DISPLAY_IN_REPORT',
                icon : 'edit',
                onClick : function() {
                    _this.batchEditDisplayInReport('0', 'EMC.MODULE.BP.REPORT.REPORTS.MODAL.CONFIRM_NOT_DISPLAY_IN_REPORT');
                }
            } ]
        }
    },

    /** 批量操作分析项是否在报告中显示 */
    batchEditDisplayInReport : function(visible, tips) {
        var selections = Gikam.getComp('emc-detail-original-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var taskResults = selections.map(row => {
            return {
                id : row.id,
                reportDisplay : visible
            }
        });

        Gikam.confirm('EMC.MODULE.BP.REPORT.REPORTS.MODAL.CONFIRM_TO_EDIT', tips, function() {
            workspace.window.showMask(true);
            Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/instant', Gikam.getJsonWrapper(null, [ 'emcOrderTaskResultServiceImpl', taskResults ])).done(function() {
                Gikam.getComp('emc-detail-original-edit-list-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        })
    },

    // 详细信息 - 报告数据
    getDetailReportGridParam : function() {
        return {
            type : 'grid',
            id : 'emc-detail-report-edit-list-grid',
            url : '',
            service : 'emcReportResultServiceImpl',
            columns : emcReport.getDetailReportListColumns(),
            page : true,
            pageSize : 50
        }
    },

    // 采样单
    getSampleGridParam : function(type) {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sample-edit-list-grid'+type,
            columns : emcReport.getSampleEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    //this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-sample-order-edit-list-grid'+type, 'emc-sample-test-edit-list-grid'+type ]);
                }
            },
            onRowActive : function(index, row) {
                _this.onSampleActive(row,type);
            },
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.ANNEX',
                icon : 'upload',
                onClick : function() {
                    _this.uploadSampleFile(type);
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_SAMPLE',
                icon : 'print',
                onClick : function() {
                    _this.viewSampleReport(type);
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_HANDOVER_SAMPLE',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('samplingNumber', Gikam.getComp('emc-sample-edit-list-grid'+type).getSelections());
                }
            } ]
        }
    },

    // 采样单--样品
    getSampleOrderGridParam : function(type) {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sample-order-edit-list-grid'+type,
            columns : emcReport.getSampleOrderListColumns(),
            onRowActive : function(index, row) {
                _this.onSampleOrderActive(row,type);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    //this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-sample-test-edit-list-grid'+type ]);
                }
            }
        }
    },

    // 采样单--测试信息
    getSampleTestGridParam : function(type) {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sample-test-edit-list-grid'+type,
            columns : emcReport.getSampleTestListColumns()
        }
    },

    getRunGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-run-edit-list-grid',
            columns : emcReport.getRunEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    //this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-run-order-task-edit-list-grid', 'emc-run-order-task-result-edit-list-grid' ]);
                }
            },
            onRowActive : function(index, row) {
                _this.onRunActive(row)
            },
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_RUN',
                icon : 'print',
                onClick : function() {
                    _this.viewRunReport();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.LOOK_ATLAS',
                icon : 'preview',
                onClick : function() {
                    _this.loadAtlas();
                }
            }, {
                type : 'button',
                text : 'GIKAM.FILE.ATTACHMENT',
                icon : 'browse',
                onClick : function() {
                    _this.loadFilePage();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.REVIEW',
                icon : 'browse',
                onClick : function() {
                    _this.runReview();
                }
            // }, {
            //     type : 'button',
            //     text : 'GIKAM.BUTTON.DATA_SUMMARY',
            //     icon : 'browse',
            //     onClick : function() {
            //         _this.dataSummaryRun();
            //     }
            },{
                type: 'button',
                text: '分析数据汇总',
                icon: 'print',
                onClick: function () {
                    var selections = Gikam.getComp('emc-run-edit-list-grid').getSelections();
                    if (selections.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    Gikam.viewReport('fenxishujuchakan', selections);
                }
            } ,{
                type: 'button',
                text: '查看质控指标',
                icon: 'browse',
                onClick: function () {
                    var selections = Gikam.getComp('emc-run-edit-list-grid').getSelections();
                    if (selections.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    Gikam.create('modal', {
                        width : '80%',
                        height : '80%',
                        title : '质控指标',
                        url : emcReport.baseUrl + '/page/item-qc-index-list' + Gikam.param({
                            testmethodids : selections[0].testMethodId,
                        })
                    });
                }
            } ]
        }
    },

    folderReview : function() {
        var selections = Gikam.getComp('emc-folder-edit-list-grid').getSelections();
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
                Gikam.getComp('emc-folder-edit-list-grid').refresh();
            }
        });
    },

    comparisonResult : function() {
        var selections = Gikam.getComp('emc-folder-edit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            width : '30%',
            title : 'EMC.MODULE.BP.REPORT.REPORTS.MODAL.COMPARISON_RESULT',
            url : IFM_CONTEXT + "/secure/emc/module/mdm/bizmdm/comparisons/page/result-list" + Gikam.param({
                folderId : selections[0].id,
            })
        });
    },

    runReview : function() {
        var selections = Gikam.getComp('emc-run-edit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group', Gikam.getJsonWrapper({
            f : {
                runId_SEQ : selections[0].id,
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
                onAfterClose : function(rows) {
                    Gikam.getComp('emc-run-edit-list-grid').refresh();
                }
            });
        });

    },

    loadFilePage : function() {
        var selections = Gikam.getComp('emc-run-edit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : 'GIKAM.FILE.ATTACHMENT',
            url : '/secure/emc/module/mdm/file/files/page/view' + Gikam.param({
                id : selections[0].id,
                table : 'T_EMC_RUN',
                readonly : true,
            }),
            onAfterClose : function(rows) {
                Gikam.getComp('emc-run-edit-list-grid').refresh();
            }
        });
    },

    loadAtlas : function() {
        var selections = Gikam.getComp('emc-run-order-task-edit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : 'GIKAM.TAB.LOOK_ATLAS',
            url : '/secure/emc/module/bp/bp/order-tasks/page/atlas' + Gikam.param({
                orderTaskIds : selections[0].id,
                targetIds : 'T_EMC_ORDER_TASK$' + selections[0].id,
            }),
            onAfterClose : function(rows) {
                Gikam.getComp('emc-run-edit-list-grid').refresh();
            }
        });
    },

    getRunOrderTaskGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-run-order-task-edit-list-grid',
            columns : emcReport.getRunOrderTaskListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    //this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-run-order-task-result-edit-list-grid' ])
                }
            },
            onRowActive : function(index, row) {
                _this.onRunOrderTaskActive(row)
            }
        }
    },

    getRunOrderTaskResultGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-run-order-task-result-edit-list-grid',
            columns : emcReport.getRunOrderTaskResultListColumns()
        }
    },

    getAuditRecordGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-audit-record-edit-list-grid',
            columns : emcReport.getAuditRecordListColumns(),
            group : {
                groupCheckbox : false,
                fields : [ 'auditCategory' ],
                formatter : function(row) {
                    return row.stepCode + '、' + row.auditCategory;
                }
            }
        }
    },

    loadDataInputPage : function(id) {
        var _this = this;
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.DATA_INPUT',
            url : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/page/data-input-detail' + Gikam.param({
                id : id,
                pageSource : 'report'
            }),
        });
    },

    uploadSampleFile : function(type) {
        var selections = Gikam.getComp('emc-sample-edit-list-grid'+type).getSelections();
        if (selections.length < 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        if (selections.length == 1) {
            Gikam.create('modal', {
                title : 'GIKAM.FILE.ATTACHMENT',
                url : '/secure/emc/module/mdm/file/files/page/view' + Gikam.param({
                    id : selections[0].id,
                    table : 'T_EMC_SAMPLE',
                })
            });
        } else {
            var ids = selections.map(function (item) {
                return item.id;
            }).join(',');
            Gikam.create('modal', {
                title : 'GIKAM.FILE.ATTACHMENT',
                url : '/secure/emc/module/mdm/file/files/page/view' + Gikam.param({
                    ids : ids,
                    table : 'T_EMC_SAMPLE',
                    readonly: true
                })
            });
        }
    },

    editRemark : function(index) {
        var row = Gikam.getComp('emc-report-edit-list-grid').getData()[index];
        Gikam.create('modal', {
            width : 600,
            height : 400,
            title : 'EMC.MODULE.BP.REPORT.REPORTS.MODAL.EDIT_REMARK',
            url : emcReport.baseUrl + '/page/edit-remark' + Gikam.param({
                remark : row.remark
            }),
            onAfterClose : function(data) {
                if (!Gikam.isEmpty(data)) {
                    row.remark = data.remark;
                    workspace.window.showMask(true);
                    Gikam.put(emcReport.baseUrl + '/' + row.id, Gikam.getJsonWrapper(null, [ 'emcReportServiceImpl', [ row ] ])).done(function() {
                        Gikam.getComp('emc-report-edit-list-grid').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }

            }
        });
    },

    // 按测试更换指标
    updateTaskIndex : function() {
        var reportRow = Gikam.getComp('emc-report-edit-list-grid').getActivedRow();
        var selecttions = Gikam.getComp('emc-report-edit-list-grid').getSelections();
        var gridData = Gikam.getComp('emc-folder-edit-list-grid').getSelections();
        console.log(reportRow.monitorTmplId);
        if (gridData.length > 0) {
            var ids = "";
            gridData.map(function(item, i) {
                ids += gridData[i].id + ',';
            });
            ids = ids.substring(0, ids.length - 1);
            Gikam.create('modal', {
                width : 1500,
                height : 650,
                title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.UPDATE_INDEX',
                url : emcReport.baseUrl + '/page/order-task-update-index' + Gikam.param({
                    ids : ids,
                    monitorTmplId : gridData[0].monitorTmplId
                }),
                onAfterClose : function() {
                    Gikam.getComp('emc-folder-edit-list-grid').refresh();
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    /** 生成报告数据 */
    generateData : function() {
        let selections = Gikam.getComp('emc-report-edit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        console.log(selections);
        workspace.window.showMask(true);
        var url = "";
        if(selections[0].ext$.mergeflag != 1){
            url = emcReport.baseUrl + '/action/generate-data'
        }else{
            url = emcReport.baseUrl + '/action/generate-merge-data'
        }
        console.log(url);
        Gikam.postText(url, Gikam.getJsonWrapper({}, [ 'emcReportServiceImpl', selections ])).done(function () {
            workspace.window.closeMask();
            Gikam.getComp("emc-detail-report-edit-list-grid").refresh();
            Gikam.alert('EMC.MODULE.BP.REPORT.REPORTS.TIP.GENERATE_DATA_SUCCESS');
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    submitProject : function() {
        let projects = Gikam.getComp('emc-report-edit-project-list-grid').getSelections();
        if (projects.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var grid = Gikam.getComp('emc-report-edit-list-grid');
        let reportData = grid.getData().filter(item => !isNaN(item.id));
        var requiredMissed = reportData.some(function (item) {
            return Gikam.isEmpty(item.ext$.reportreviewerid);
        });
        if (requiredMissed) {
            Gikam.alert('EMC.MODULE.BP.REPORT.TIP.REPORT_REVIEWER_IS_EMPTY');
            return;
        }

        Gikam.create('workflow').submit({
            data : reportData,
            pageObject : emcReport
        }).done(function() {
            grid.refresh();
            Gikam.getComp('emc-report-edit-project-list-grid').refresh();
        });
    },

    submit : function() {
        var grid = Gikam.getComp('emc-report-edit-list-grid');
        var requiredMissed = grid.getSelections().some(function (item) {
            return Gikam.isEmpty(item.ext$.reportreviewerid);
        });
        if (requiredMissed) {
            Gikam.alert('EMC.MODULE.BP.REPORT.TIP.REPORT_REVIEWER_IS_EMPTY');
            return;
        }

        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : emcReport
        }).done(function() {
            grid.refresh();
        });
    },

    undo : function() {
        var grid = Gikam.getComp('emc-report-edit-project-list-grid');
        var selections = grid.getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var selection = selections[0];

        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.REPORT.REPORTS.MODAL.UNDO',
            width : '80%',
            url : emcReport.baseUrl + '/page/undo' + Gikam.param({
                projId : selection.id,
            }),
            onAfterClose : function(res) {
                if (Gikam.isEmpty(res)) {
                    return;
                }

                Gikam.confirm('GIKAM.MODAL.UNDO', 'GIKAM.TIP.UNDO', function() {
                    workspace.window.showMask(true);
                    Gikam.put(emcReport.baseUrl + '/action/biz-undo', Gikam.getJsonWrapper({
                        projId : selection.id,
                        projNo : selection.projNo,
                        refuseReason : res.refuseReason,
                        // analysisType : res.analysisType
                    }, [ 'emcSampleServiceImpl', res.data.sample],[ 'emcRunServiceImpl', res.data.run])).done(function() {
                        Gikam.getComp('emc-report-edit-project-list-grid').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                });
            }
        });

    },

    projectUndo : function() {
        var grid = Gikam.getComp('emc-report-edit-project-list-grid');
        var selections = grid.getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.confirm('GIKAM.MODAL.UNDO', 'GIKAM.TIP.UNDO', function() {
            workspace.window.showMask(true);
            Gikam.post(emcReport.baseUrl + '/action/project-undo', Gikam.getJsonWrapper({
            }, [ '', selections ])).done(function() {
                grid.refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });

    },
    viewSampleReport : function(type) {
        var selections = Gikam.getComp('emc-sample-edit-list-grid'+type).getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.viewReport(selections[0].reportConfigLineId, selections[0]);
    },

    viewRunReport : function() {
        var selections = Gikam.getComp('emc-run-edit-list-grid').getSelections();
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

    /** 查看报告 */
    viewReport : function() {
        var _this = this;
        var rows = Gikam.getComp('emc-report-edit-list-grid').getSelections();
        if (rows.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        if (rows[0].reportConfigLineId) {
            workspace.window.showMask(true);
            Gikam.post(emcReport.baseUrl + '/action/view-report', Gikam.getJsonWrapper(null, [ '', rows ])).done(function(fileId) {
                var grid = Gikam.getComp('emc-report-edit-list-grid');
                grid.refreshRowById(rows[0].id);
                if (grid.getActivedRow()) {
                    _this.onReportActive(grid.getActivedRow());
                }
                // _this.openReportBrowseRecord(rows[0].id);
                Gikam.openFile(fileId, false);
            }).always(function () {
                workspace.window.closeMask();
            });
        } else {
            Gikam.alert('EMC.MODULE.BP.REPORT.REPORTS.TIP.NO_FILE');
        }
    },

    viewNewReport : function() {
        var _this = this;
        var rows = Gikam.getComp('emc-report-edit-list-grid').getSelections();
        if (rows.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        if (rows[0].reportTemplateId) {
            workspace.window.showMask(true);
            Gikam.post(emcReport.baseUrl + '/action/view-new-report', Gikam.getJsonWrapper(null, [ '', rows ])).done(function(fileId) {
                var grid = Gikam.getComp('emc-report-edit-list-grid');
                grid.refreshRowById(rows[0].id);
                if (grid.getActivedRow()) {
                    _this.onReportActive(grid.getActivedRow());
                }
                Gikam.openFile(fileId, false);
            }).always(function () {
                workspace.window.closeMask();
            });
        } else {
            Gikam.alert('EMC.MODULE.BP.REPORT.REPORTS.TIP.NO_FILE');
        }
    },

    openReportBrowseRecord : function(reportId) {
        var _this = this,
            _choose = Gikam.choose.getConfig('browseReportRecords');

        Gikam.create('modal', {
            width : _choose.width,
            height : '80%',
            title : _choose.title,
            url : _choose.url+Gikam.param({
                reportId : reportId,
            }),
        });
    },
    /** 查看质控报告 */
    viewQcDocument : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-report-edit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var qcReportConfigLineId = selections[0].ext$.qcreportlineid;
        if (Gikam.isEmpty(qcReportConfigLineId)) {
            Gikam.alert('EMC.MODULE.BP.REPORT.REPORTS.TIP.NO_QC_REPORT_CONFIG_LINE');
            return;
        }

        workspace.window.showMask(true);
        Gikam.post(emcReport.baseUrl + '/action/validate-result-status', Gikam.getJsonWrapper(null, [ '', selections ])).done(function (res) {
            if (res.valid) {
                Gikam.post(emcReport.baseUrl + '/action/view-qc-report', Gikam.getJsonWrapper({}, [ '', selections ])).done(function(fileId) {
                    var grid = Gikam.getComp('emc-report-edit-list-grid');
                    grid.refreshRowById(selections[0].id);
                    if (grid.getActivedRow()) {
                        _this.onReportActive(grid.getActivedRow());
                    }
                    Gikam.openFile(fileId, true);
                }).always(function () {
                    workspace.window.closeMask();
                });

                return;
            }

            workspace.window.closeMask();
            Gikam.confirm('GIKAM.TIP.CONFIRM', res.message, function() {
                workspace.window.showMask(true);
                Gikam.post(emcReport.baseUrl + '/action/view-qc-report', Gikam.getJsonWrapper({}, [ '', selections ])).done(function(fileId) {
                    var grid = Gikam.getComp('emc-report-edit-list-grid');
                    grid.refreshRowById(selections[0].id);
                    if (grid.getActivedRow()) {
                        _this.onReportActive(grid.getActivedRow());
                    }
                    Gikam.openFile(fileId, true);
                }).always(function () {
                    workspace.window.closeMask();
                });
            });
        });
    },

    /** 查看Reporting Service报告（比对报告） */
    viewRsDocument : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-report-edit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var rsReportConfigLineId = selections[0].rsReportConfigLineId;
        if (Gikam.isEmpty(rsReportConfigLineId)) {
            Gikam.alert('EMC.MODULE.BP.REPORT.REPORTS.TIP.NO_RS_REPORT_CONFIG_LINE');
            return;
        }

        workspace.window.showMask(true);
        Gikam.post(emcReport.baseUrl + '/view/rs-report', Gikam.getJsonWrapper(null, [ '', selections ])).done(function(fileId) {
            var grid = Gikam.getComp('emc-report-edit-list-grid');
            grid.refreshRowById(selections[0].id);
            if (grid.getActivedRow()) {
                _this.onReportActive(grid.getActivedRow());
            }
            Gikam.openFile(fileId, true);
        }).always(function () {
            workspace.window.closeMask();
        });
    },

    /** 查看数据表 */
    viewDataSheet : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-report-edit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var dataSheetId = selections[0].dataSheetId;
        if (Gikam.isEmpty(dataSheetId)) {
            Gikam.alert('EMC.MODULE.BP.REPORT.REPORTS.TIP.NO_DATA_SHEET_REPORT_CONFIG_LINE');
            return;
        }

        workspace.window.showMask(true);
        Gikam.post(emcReport.baseUrl + '/view/data-sheet', Gikam.getJsonWrapper(null, [ '', selections ])).done(function(fileId) {
            var grid = Gikam.getComp('emc-report-edit-list-grid');
            grid.refreshRowById(selections[0].id);
            if (grid.getActivedRow()) {
                _this.onReportActive(grid.getActivedRow());
            }
            Gikam.openFile(fileId, true);
        }).always(function () {
            workspace.window.closeMask();
        });
    },

    dataSummaryProject : function() {
        var gridData = Gikam.getComp('emc-report-edit-list-grid').getSelections();
        if (gridData.length > 0) {
            var ids = "";
            gridData.map(function(item, i) {
                ids += gridData[i].projId + ',';
            });
            ids = ids.substring(0, ids.length - 1);
            Gikam.create('modal', {
                width : 1000,
                height : 500,
                title : 'GIKAM.MODAL.DATA_SUMMARY',
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/page/data-summary' + Gikam.param({
                    summaryType : 'project',
                    ids : ids
                })
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    regroupProject : function() {

        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.REPORT.REPORTS.MODAL.REGROUP_REPORT',
            url : emcReport.baseUrl + '/page/regroup-report'
        });

    },

    viewAllReport : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-report-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : '所有报告',
            url : emcReport.baseUrl + '/page/browse-all-report' + Gikam.param({
                reportIds : selections.map(item => item.id).join()
            })
        });
    },

    batchInput : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-report-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
            width : 400,
            height : 300,
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
                                    item.reportConfigLineId = formData.reportConfigLineId || undefined;
                                    item.ext$.reportname = formData.ext$.reportname;
                                    item.ext$.qcreportlineid = formData.ext$.qcreportlineid;
                                    item.ext$.qcreportname = formData.ext$.qcreportname;
                                })
                                workspace.window.showMask(true);

                                Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcReportServiceImpl', selections ])).done(function () {
                                    Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcProjectServiceImpl', [ {
                                        id : selections[0].projId,
                                        ext$ : {
                                            reportreviewername : formData.ext$.reportreviewername,
                                            reportreviewerid : formData.ext$.reportreviewerid,
                                            reportissueusername : formData.ext$.reportissueusername,
                                            reportissueuserid : formData.ext$.reportissueuserid
                                        }
                                    } ] ])).done(function () {
                                        Gikam.getComp('emc-report-edit-list-grid').refresh();
                                    }).always(function () {
                                        workspace.window.closeMask();
                                        modal.close();
                                    });
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
            fields: emcReport.getBatchInputFields(),
            autoSave: false
        });

    },

    dataSummaryRun : function() {
        var gridData = Gikam.getComp('emc-run-edit-list-grid').getSelections();
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

    onSampleActive : function(row,type) {
        Gikam.getAsyncComp('emc-sample-order-edit-list-grid'+type).done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/queries/searchable',
                requestData : {
                    sampleId_SEQ : row.id
                }
            });
        });
    },

    onSampleOrderActive : function(row,type) {
        Gikam.getAsyncComp('emc-sample-test-edit-list-grid'+type).done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/searchable',
                requestData : {
                    orderId_SEQ : row.id
                }
            });
        });
    },

    onRunActive : function(row) {
        Gikam.getAsyncComp('emc-run-order-task-edit-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/searchable',
                requestData : {
                    runId_SEQ : row.id,
                    TESTCAPCITYORGID_SEQ: workspace.user.ext$.parentorgid,
                }
            });
        });
    },

    onRunOrderTaskActive : function(row) {
        Gikam.getAsyncComp('emc-run-order-task-result-edit-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/queries/searchable',
                requestData : {
                    orderTaskId_SEQ : row.id
                }
            });
        });
    },

    onReportActive : function(row) {
        if (isNaN(row.id)) {
            return;
        }

        Gikam.getComp('emc-report-edit-list-uploader').setOptions({
            bizId : row.id,
        });

        Gikam.getComp('emc-project-edit-list-uploader').setOptions({
            bizId : row.projId,
        });
        Gikam.getAsyncComp('emc-folder-edit-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/queries/searchable',
                requestData : {
                    projId_SEQ : row.projId
                }
            });
        });
        Gikam.getAsyncComp('emc-order-edit-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/queries/searchable',
                requestData : {
                    projId_SEQ : row.projId,
                    monitorTmplId_SEQ : row.monitorTmplId
                }
            });
        });
        Gikam.getAsyncComp('emc-sample-edit-list-gridsample').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/queries/searchable',
                requestData : {
                    projId_SEQ : row.projId
                }
            });
        });
        Gikam.getAsyncComp('emc-sample-edit-list-gridsend').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/queries/searchable',
                requestData : {
                    projId_SEQ : row.projId
                }
            });
        });
        // Gikam.getAsyncComp('emc-sample-edit-send-list-grid').done(function(grid) {
        //     grid.refresh({
        //         url : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/queries/searchable',
        //         requestData : {
        //             projId_SEQ : row.projId
        //         }
        //     });
        // });
        Gikam.getAsyncComp('emc-run-edit-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/result/runs/queries',
                requestData : {
                    projId : row.projId,
                	targetType : 'reportData'
                }
            });
        });
        Gikam.getAsyncComp('emc-audit-record-edit-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/audit-records/queries',
                requestData : {
                    projId_SEQ : row.projId
                }
            });
        });
        // 根据样品类型查询对应的点位列表
        var queryFolderUrl = IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/queries/selectable';
        Gikam.getJson(queryFolderUrl + Gikam.param({
            orderCategory : row.orderCategory,
            projId : row.projId
        })).done(function(data) {
            console.info(data)
            var queryFolderForm = Gikam.getComp('emc-detail-folder-query-form');
            if (Gikam.isEmpty(data)) {
                Gikam.cleanCompData([ 'emc-detail-original-edit-list-grid', 'emc-detail-report-edit-list-grid' ])
            } else {
                queryFolderForm.setData({
                    folderId_SEQ : data[0].id
                });
            }
            Gikam.getComp('emc-detail-folder-query-form').setSelectOptions("folderId_SEQ", data.map(function(item) {
                return {
//                    text : item.folderNo + '-' + (item.ext$.foldername || ''),
                  text : item.folderNo ,
                  value : item.id
                };
            }));
        });

        // 查询合同附件
        Gikam.getAsyncComp('emc-project-edit-list-contract-uploader').done(function (uploader) {
            Gikam.post(IFM_CONTEXT + '/secure/emc/module/mdm/crm/contracts/queries', Gikam.getJsonWrapper({
                f : {
                    contractNo_SEQ : row.ext$.contractno
                }
            }, [])).done(function (data) {
                if (!Gikam.isEmpty(data.rows)) {
                    let targetIds = ['T_CRM_OPPORTUNITY_CONTRACT$' + data.rows[0].id, 'T_CRM_OPPORTUNITY_MAIN$' + data.rows[0].opportunityId];
                    uploader.grid.refresh({
                        requestData : {
                            targetId : '',
                            targetId_IN : targetIds.join()
                        },
                        url : uploader.options.searchUrl
                    })
                }
            });
        });
    },

    uploadReportFile : function() {
        var rows = Gikam.getComp('emc-report-edit-list-grid').getSelections();
        if (rows.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var bizId = rows[0].id;
        Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
            f : {
                targetId : 'T_EMC_REPORT$' + bizId
            },
            n : -1,
            s : 50
        })).done(function(r) {
            if (r.total > 0) {
                Gikam.confirm('EMC.MODULE.BP.REPORT.REPORTS.TIP.FILE_REPEAT', 'EMC.MODULE.BP.REPORT.REPORTS.TIP.PDF_FILE_OVERRIDE', function() {
                    workspace.window.showMask(true);
                    Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', r.rows ])).done(function(r) {
                        Gikam.create('simpleUploader', {
                            id : 'emc-report-pdf-uploader',
                            dbTable : 'T_EMC_REPORT',
                            bizId : bizId,
                            multiple : false,
                            onAfterClose : function() {
                                Gikam.getComp('emc-report-edit-list-grid').refresh()
                            }
                        });
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                })
            } else {
                Gikam.create('simpleUploader', {
                    id : 'emc-report-pdf-uploader',
                    dbTable : 'T_EMC_REPORT',
                    bizId : bizId,
                    multiple : false,
                    onAfterClose : function() {
                        Gikam.getComp('emc-report-edit-list-grid').refresh()
                    }
                });
            }
        });
    },

    loadFolderFilePage : function() {

        var selections = Gikam.getComp('emc-folder-edit-list-grid').getSelections();

        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : 'GIKAM.FILE.ATTACHMENT',
            url : '/secure/emc/module/mdm/file/files/page/view' + Gikam.param({
                id : selections[0].id,
                table : 'T_EMC_FOLDER',
                bizCategory : 'pointDiagram',
            }),
            onAfterClose : function(rows) {
                Gikam.getComp('emc-folder-edit-list-grid').refresh();
            }
        });
    },

    loadReportFilePage : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-report-edit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : 'GIKAM.FILE.ATTACHMENT',
            url : '/secure/emc/module/mdm/file/files/page/view' + Gikam.param({
                id : selections[0].id,
                table : 'T_EMC_REPORT',
                bizCategory : 'pointDiagram',
            }),
            onAfterClose : function(rows) {
                var grid = Gikam.getComp('emc-report-edit-list-grid');
                grid.refreshRowById(selections[0].id);
                if (grid.getActivedRow()) {
                    _this.onReportActive(grid.getActivedRow());
                }
            }
        });
    },

    getSampleTab : function (id, tabTitle,type) {
        return {
            id : id,
            title : tabTitle,
            items : [ {
                type : 'layout',
                center : {
                    items : [ {
                        type : 'layout',
                        north : {
                            height : 200,
                            items : [ this.getSampleGridParam(type) ]
                        },
                        center : {
                            items : [ {
                                type : 'layout',
                                west : {
                                    width : '50%',
                                    items : [ this.getSampleOrderGridParam(type) ]
                                },
                                center : {
                                    width : '50%',
                                    items : [ this.getSampleTestGridParam(type) ]
                                }
                            } ]
                        }
                    } ]
                },
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-folder-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '32%',
                items : [ this.getProjectParam() ]
            },
            center : {
                items : [ {
                    type : 'tab',
                    id : 'emc-report-edit-grid-tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.REPORT_DATA',
                        items : [ {
                            type : 'layout',
                            north : {
                                height : "50%",
                                items : [ _this.getGridParam() ]
                            },
                            center : {
                                items : [ _this.getOtherGridParam() ]
                            }
                        } ]
                    }, {
                        title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.DETAIL_INFO',
                        items : [ {
                            type : 'layout',
                            north : {
                                height : "50%",
                                items : [ _this.getDetailOriginalGridParam() ]
                            },
                            center : {
                                items : [ _this.getDetailReportGridParam() ]
                            }
                        } ]
                    }, _this.getSampleTab('emc-report-edit-grid-sample-tab','EMC.MODULE.BP.REPORT.REPORTS.TAB.SAMPLE','sample'),
                        _this.getSampleTab('emc-report-edit-grid-sample-send-tab','送样单','send'),{
                        title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.RUN_RECORD',
                        items : [ {
                            type : 'layout',
                            center : {
                                items : [ {
                                    type : 'layout',
                                    north : {
                                        height : 200,
                                        items : [ this.getRunGridParam() ]
                                    },
                                    center : {
                                        items : [ {
                                            type : 'layout',
                                            west : {
                                                width : '50%',
                                                items : [ this.getRunOrderTaskGridParam() ]
                                            },
                                            center : {
                                                width : '50%',
                                                items : [ this.getRunOrderTaskResultGridParam() ]
                                            }
                                        } ]
                                    }
                                } ]
                            },
                        } ]
                    }, {
                        title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.AUDIT_RECORD',
                        items : [ {
                            type : 'layout',
                            center : {
                                items : [ this.getAuditRecordGridParam() ]
                            },
                        } ]
                    }, {
                        title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.PROJECT_FILE',
                        items : [ {
                            type : 'uploader',
                            id : 'emc-project-edit-list-uploader',
                            dbTable : 'T_EMC_PROJECT'
                        } ]
                    }, {
                        title : '合同附件',
                        items : [ {
                            type : 'uploader',
                            id : 'emc-project-edit-list-contract-uploader',
                            dbTable : 'T_CRM_OPPORTUNITY_CONTRACT',
                            readonly : true,
                        } ]
                    }  ]
                } ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
