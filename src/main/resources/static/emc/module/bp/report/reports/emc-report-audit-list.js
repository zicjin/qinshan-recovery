emcReport.auditPage = {

    reportArchivingPage : false,
    getGridParam : function() {
        var _this = this;
        _this.param['targetType'] = _this.param.bpmn_statusCode;
        _this.param['currentUserId'] = workspace.user.id;
        var columns = emcReport.getAuditListColumns();
        if (_this.param.bpmn_statusCode === 'reportArchiving') {
            columns = emcReport.getArchivingListColumns();
            this.reportArchivingPage = true;
        }
        if (_this.param.bpmn_statusCode !== 'reportAudit') {
            columns.splice(4, 1);
        }
        _this.param.orgId_SB = workspace.user.ext$.parentorgid;
        return {
            type : 'grid',
            id : 'emc-report-audit-list-grid',
            url : emcReport.baseUrl + '/queries/tasks',
            columns : columns,
            group : {
                fields : [ 'ext$.projname' ],
                formatter : function(row) {
                    return row.ext$.projname;
                }
            },
            order : {
                'ext$.projname' : 'asc',
                projId : 'asc'
            },
            requestData : _this.param,
            page : true,
            onRendered : function() {
                // 报告盖章或报告归档显示归档文件
                if (_this.param.bpmn_statusCode === 'reportSend' || _this.param.bpmn_statusCode === 'reportArchiving') {
                    Gikam.getComp('emc-report-audit-list-tab').revertPanel('emc-report-audit-archive-file-panel');
                }
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    //this.activeRowByIndex(1);
                } else {
                    Gikam.cleanCompData(['emc-folder-audit-list-grid', 'emc-order-audit-list-grid',
                        'emc-sample-audit-list-grid', 'emc-run-audit-list-grid', 'emc-audit-record-audit-list-grid']);
                    Gikam.getComp('emc-report-audit-list-uploader').grid.cleanData();
                    Gikam.getComp('emc-project-audit-list-uploader').grid.cleanData();
                }
            },
            onRowActive : function(index, row) {
                _this.onReportActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                icon : 'pass',
                hidden : _this.getHidden(),
                onClick : function() {
                    if (_this.param.bpmn_statusCode === 'reportSend') {
                        _this.openSendInfoPage();
                        return;
                    }

                    _this.pass();
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                icon : 'reject',
                onClick : function() {
                    _this.reject();
                }
            },{
                type : 'button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_REPORT',
                icon : 'browse',
                onClick : function() {
                    _this.viewDocument();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_QC_REPORT',
                icon : 'browse',
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
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_DATASHEET',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('rasproject', Gikam.getComp('emc-report-audit-list-grid').getSelections());
                }
            }, {
                type : 'dropDownMenu',
                text : 'GIKAM.BUTTON.MORE_OPERATE',
                items : [  {
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
                    onClick : function() {
                        _this.dataSummaryProject();
                    }
                } ]
            } ]
        }
    },

    batchDownloadProjectFiles : function (){
        var selections = Gikam.getComp('emc-project-audit-list-uploader').grid.getSelections();
        if (selections.length > 0) {
            var fileIds = [];
            selections.forEach(function (item) {
                fileIds.push({id : item.id})
            })
            Gikam.postText('/core/module/item/files/action/pack',Gikam.getJsonWrapper({}, [ '', fileIds ])
            ).done(function (fileUrl) {
                if (fileUrl != '' && fileUrl != undefined) {
                    Gikam.download(IFM_CONTEXT + fileUrl);
                }
            })
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    batchDownloadBeforeReportFiles : function (){
        var selections = Gikam.getComp('emc-report-audit-list-uploader').grid.getSelections();
        if (selections.length > 0) {
            var fileIds = [];
            selections.forEach(function (item) {
                fileIds.push({id : item.id})
            });
            console.log(fileIds);
            Gikam.postText('/core/module/item/files/action/pack',Gikam.getJsonWrapper({}, [ '', fileIds ])
            ).done(function (fileUrl) {
                if (fileUrl != '' && fileUrl != undefined) {
                    Gikam.download(IFM_CONTEXT + fileUrl);
                }
            })
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    batchDownloadReportFiles : function (){
        var selections = Gikam.getComp('emc-report-audit-list-after-uploader').grid.getSelections();
        if (selections.length > 0) {
            var fileIds = [];
            selections.forEach(function (item) {
                fileIds.push({id : item.id})
            })
            Gikam.postText('/core/module/item/files/action/pack',Gikam.getJsonWrapper({}, [ '', fileIds ])
            ).done(function (fileUrl) {
                if (fileUrl != '' && fileUrl != undefined) {
                    Gikam.download(IFM_CONTEXT + fileUrl);
                }
            })
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },



    getOtherGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.XL,
            fill : true,
            panels : [ {
                title : '报告初稿',
                items : [ {
                    type : 'uploader',
                    id : 'emc-report-audit-list-uploader',
                    dbTable : 'T_EMC_REPORT',
                    formatterGridColumns : function() {
                        return emcReport.getReportFileListColumns();
                    },
                    onBeforeUpload : function() {
                        this.setUploadData({})
                    },
                    formatterGridToolbar : function () {
                        return [ {
                            type: 'button',
                            text: 'GIKAM.BUTTON.BATCH_DOWNLOAD',
                            icon: 'move-down',
                            onClick: function () {
                                _this.batchDownloadBeforeReportFiles();
                            }
                        } ]
                    }
                } ]
            }, {
                title : '归档报告',
                items : [ {
                    type : 'uploader',
                    id : 'emc-report-audit-list-after-uploader',
                    dbTable : 'T_EMC_REPORT',
                    formatterGridColumns : function() {
                        return emcReport.getFinalReportFileListColumns();
                    },
                    onBeforeUpload : function() {
                        this.setUploadData({})
                    },
                    formatterGridToolbar : function () {
                        return [ {
                            type: 'button',
                            text: 'GIKAM.BUTTON.BATCH_DOWNLOAD',
                            icon: 'move-down',
                            onClick: function () {
                                _this.batchDownloadReportFiles();
                            }
                        }, {
                            type : 'button',
                            text : '上传附件',
                            icon : 'upload',
                            onClick : function() {
                                _this.uploadFile();
                            }
                        }, {
                            type : 'button',
                            text : '删除附件',
                            icon : 'file-remove',
                            onClick : function() {
                                _this.deleteFile();
                            }
                        } ]
                    }
                } ]
            }, {
                title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.PROJECT_FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-project-audit-list-uploader',
                    dbTable : 'T_EMC_PROJECT',
                    formatterGridToolbar : function () {
                        return [ {
                            type: 'button',
                            text: 'GIKAM.BUTTON.BATCH_DOWNLOAD',
                            icon: 'move-down',
                            onClick: function () {
                                _this.batchDownloadProjectFiles();
                            }
                        }, {
                            type : 'button',
                            text : '删除附件',
                            icon : 'file-remove',
                            onClick : function() {
                                _this.deleteProjectFile();
                            }
                        } ]
                    }
                } ]
            }, {
                title : '合同附件',
                items : [ {
                    type : 'uploader',
                    id : 'emc-report-audit-list-contract-uploader',
                    dbTable : 'T_CRM_OPPORTUNITY_CONTRACT',
                    formatterGridToolbar : function () {
                        return [ {
                            type: 'button',
                            text: 'GIKAM.BUTTON.BATCH_DOWNLOAD',
                            icon: 'move-down',
                            onClick: function () {
                                _this.batchDownloadProjectFiles();
                            }
                        } ]
                    }
                } ]
            }, {
                title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.FOLDER_INFO',
                items : [ {
                    type : 'grid',
                    id : 'emc-folder-audit-list-grid',
                    columns : emcReport.getFolderEditListColumns(),
                    page : true,
                    toolbar : [ {
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
                    } ]
                } ]
            }, {
                title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.ORDER_INFO',
                items : [ {
                    type : 'grid',
                    id : 'emc-order-audit-list-grid',
                    columns : emcReport.getOrderEditListColumns(),
                    page : true
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
            service : 'emcOrderTaskResultServiceImpl',
            columns : emcReport.getDetailOriginalListColumns(),
            page : true,
            readonly : true,
            onRendered : function() {
                Gikam.getComp('show-all-report-result-button').hide();
            },
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
                                folderId_SEQ : folderId
                            }
                        });
                        Gikam.getComp("emc-detail-report-edit-list-grid").refresh({
                            url : emcReport.baseUrl + '/results/queries',
                            requestData : {
                                folderId_SEQ : folderId,
                                exceedStandard_SEQ : _this.showAllReportResult ? '' : '0'
                            }
                        });
                    }
                } ]
            }, {
                type : 'button',
                id : 'show-exceed-report-result-button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.SHOW_EXCEED_REPORT_RESULT',
                icon : 'select',
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
                hidden : true,
                icon : 'add',
                onClick : function() {
                    Gikam.confirm('EMC.MODULE.BP.REPORT.REPORTS.MODAL.CONFIRM_GENERATE_DATA_TITLE',
                        'EMC.MODULE.BP.REPORT.REPORTS.MODAL.CONFIRM_GENERATE_DATA_CONTENT', function() {
                            var activedRow = Gikam.getComp('emc-report-edit-list-grid').getActivedRow();
                            if (activedRow) {
                                var queryForm = Gikam.getComp("emc-detail-folder-query-form");
                                workspace.window.showMask(true);
                                Gikam.postText(emcReport.baseUrl + '/action/generate-report-result', Gikam.getJsonWrapper({
                                    folderId : queryForm.options.data.folderId_SEQ
                                }, [ 'emcReportServiceImpl', [ activedRow ] ])).done(function() {
                                    workspace.window.closeMask();
                                    Gikam.alert('EMC.MODULE.BP.REPORT.REPORTS.TIP.GENERATE_DATA_SUCCESS');
                                }).always(function () {
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
            } ]
        }
    },

    // 详细信息 - 报告数据
    getDetailReportGridParam : function() {
        return {
            type : 'grid',
            id : 'emc-detail-report-edit-list-grid',
            url : '',
            service : 'emcReportResultServiceImpl',
            readonly : true,
            columns : emcReport.getDetailReportListColumns(),
            page : true
        }
    },

    // 采样单
    getSampleGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sample-audit-list-grid',
            columns : emcReport.getSampleEditListColumns(),
            page : true,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    //this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData(['emc-sample-order-audit-list-grid', 'emc-sample-test-audit-list-grid']);
                }
            },
            onRowActive : function(index, row) {
                _this.onSampleActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.ANNEX',
                icon : 'upload',
                onClick : function() {
                    _this.uploadSampleFile();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_SAMPLE',
                icon : 'print',
                onClick : function() {
                    _this.viewSampleReport();
                }
            } ]
        }
    },

    // 采样单--样品
    getSampleOrderGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sample-order-audit-list-grid',
            columns : emcReport.getSampleOrderListColumns(),
            page : true,
            onRowActive : function(index, row) {
                _this.onSampleOrderActive(row);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    //this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData(['emc-sample-test-audit-list-grid']);
                }
            }
        }
    },

    // 采样单--测试信息
    getSampleTestGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sample-test-audit-list-grid',
            columns : emcReport.getSampleTestListColumns(),
            page : true
        }
    },

    getRunGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-run-audit-list-grid',
            columns : emcReport.getRunEditListColumns(),
            page : true,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    //this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData(['emc-run-order-task-audit-list-grid', 'emc-run-order-task-result-audit-list-grid']);
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
                type : 'dropDownMenu',
                text : 'GIKAM.BUTTON.MORE_OPERATE',
                items : [ {
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
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DATA_SUMMARY',
                    icon : 'browse',
                    onClick : function() {
                        _this.dataSummaryRun();
                    }
                }, {
                    type : 'button',
                    text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.RECOMPOSE_RUN_PDF',
                    icon : 'revert',
                    hidden : !this.reportArchivingPage,
                    onClick : function() {
                        _this.recomposeProjectRunPdf();
                    }
                } ]
            } ]
        }
    },

    folderReview : function() {
        var selections = Gikam.getComp('emc-folder-audit-list-grid').getSelections();
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
                Gikam.getComp('emc-folder-audit-list-grid').refresh();
            }
        });
    },

    runReview : function() {

        var selections = Gikam.getComp('emc-run-audit-list-grid').getSelections();

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
                    Gikam.getComp('emc-run-audit-list-grid').refresh();
                }
            });
        });

    },

    loadFilePage : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-run-audit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : 'GIKAM.FILE.ATTACHMENT',
            url : '/secure/emc/module/mdm/file/files/page/view' + Gikam.param({
                id : selections[0].id,
                table : 'T_EMC_RUN',
                readonly : !_this.reportArchivingPage
            }),
            onAfterClose : function(rows) {
                Gikam.getComp('emc-run-audit-list-grid').refresh();
            }
        });
    },

    loadAtlas : function() {
        var selections = Gikam.getComp('emc-run-order-task-audit-list-grid').getSelections();
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
                Gikam.getComp('emc-run-audit-list-grid').refresh();
            }
        });
    },

    getRunOrderTaskGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-run-order-task-audit-list-grid',
            columns : emcReport.getRunOrderTaskListColumns(),
            page : true,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    //this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData(['emc-run-order-task-result-audit-list-grid']);
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
            id : 'emc-run-order-task-result-audit-list-grid',
            columns : emcReport.getRunOrderTaskResultListColumns()
        }
    },

    getAuditRecordGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-audit-record-audit-list-grid',
            columns : emcReport.getAuditRecordListColumns(),
            page : true,
            group : {
                groupCheckbox : false,
                fields : [ 'auditCategory' ],
                formatter : function(row) {
                    return row.stepCode + '、' + row.auditCategory;
                }
            }
        }
    },

    /** 重新合成项目原始记录PDF */
    recomposeProjectRunPdf : function() {
        var activedRow = Gikam.getComp('emc-report-audit-list-grid').getActivedRow();
        if (Gikam.isEmpty(activedRow)) {
            return;
        }
        console.info(activedRow)
        var project = {
            id : activedRow.projId
        };

        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.RECOMPOSE_PROJECT_RUN_PDF', 'EMC.MODULE.BP.REPORT.REPORTS.TIP.RECOMPOSE_PROJECT_RUN_PDF', function() {
            workspace.window.showMask(true);
            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/result/runs/action/recompose-project-run-pdf', Gikam.getJsonWrapper({}, [ '', [project] ])).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    /** 归档文件 */
    getArchiveFileGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-audit-list-archive-file-grid',
            columns : emcReport.getArchiveFileListColumns(),
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.GENERATE_ARCHIVE_FILE',
                icon : 'export',
                onClick : function() {
                    _this.archiveFile();
                }
            } ]
        }
    },

    /** 归档文件 */
    archiveFile : function() {
        var selections = Gikam.getComp('emc-audit-list-archive-file-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var activedReport = Gikam.getComp('emc-report-audit-list-grid').getActivedRow();
        Gikam.confirm('EMC.MODULE.BP.REPORT.REPORTS.MODAL.CONFIRM_TO_EDIT', 'EMC.MODULE.BP.REPORT.REPORTS.MODAL.CONFIRM_GENERATE_ARCHIVE_FILE', function() {
            workspace.window.showMask(true);
            Gikam.postText(emcReport.baseUrl + '/action/archive', Gikam.getJsonWrapper({}, [ '', [ activedReport ] ], [ '', selections ])).done(function() {
                workspace.window.closeMask();
                Gikam.getComp('emc-audit-list-archive-file-grid').refresh();
            }).fail(function() {
                workspace.window.closeMask();
            });
        });

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

    uploadSampleFile : function() {
        var selections = Gikam.getComp('emc-sample-audit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : 'GIKAM.FILE.ATTACHMENT',
            url : '/secure/emc/module/mdm/file/files/page/view' + Gikam.param({
                id : selections[0].id,
                table : 'T_EMC_SAMPLE',
                readonly : true
            })
        });
    },

    openSendInfoPage : function() {
        let _this = this;
        var grid = Gikam.getComp('emc-report-audit-list-grid');
        var selections = grid.getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : '发送报告',
            width : 500,
            url : emcReport.baseUrl + '/page/report-send-info' + Gikam.param({
                projId : selections[0].projId
            }),
            onAfterClose : function (data) {
                _this.pass();
            }
        });
    },

    pass : function() {
        var _this = this;
        var grid = Gikam.getComp('emc-report-audit-list-grid');
        var selections = grid.getSelections();

        if (_this.param.bpmn_statusCode === 'reportAudit') {
            var requiredMissed = selections.some(function (item) {
                return Gikam.isEmpty(item.ext$.reportissueuserid);
            });
            if (requiredMissed) {
                Gikam.alert('EMC.MODULE.BP.REPORT.TIP.REPORT_ISSUER_IS_EMPTY');
                return;
            }
        }

        var projIds = [];
        selections.map(function(item, i) {
            if(!projIds.includes(item.projId)){
                projIds.push(item.projId);
            }
        });

        var allData = grid.getData();
        var toDoSelections = [];
        allData.map(function(item, i) {

            if( item.$rowStyle==undefined && projIds.includes(item.projId) ){
                toDoSelections.push(item);
            }
        });

        Gikam.create('workflow').pass({
            data : toDoSelections,
            pageObject : emcReport
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('emc-report-audit-list-grid');
        var selections = grid.getSelections();
        var projIds = [];
        selections.map(function(item, i) {
            if(!projIds.includes(item.projId)){
                projIds.push(item.projId);
            }
        });

        var allData = grid.getData();
        var toDoSelections = [];
        allData.map(function(item, i) {
            if( item.$rowStyle==undefined && projIds.includes(item.projId) ){
                toDoSelections.push(item);
            }
        });
        Gikam.create('workflow').reject({
            data : toDoSelections,
            pageObject : emcReport
        }).done(function() {
            grid.refresh();
        });
    },

    getHidden : function() {
        if (this.param.bpmn_statusCode == 'reportArchiving') {
            return true;
        } else {
            return false;
        }
    },

    viewSampleReport : function() {
        var selections = Gikam.getComp('emc-sample-audit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.viewReport(selections[0].reportConfigLineId, selections[0]);
    },

    viewRunReport : function() {
        var selections = Gikam.getComp('emc-run-audit-list-grid').getSelections();
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

    viewDocument : function() {
        var _this=this;
        var rows = Gikam.getComp('emc-report-audit-list-grid').getSelections();
        if (rows.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        if (rows[0].reportConfigLineId) {
            Gikam.post(emcReport.baseUrl + '/action/view-report', Gikam.getJsonWrapper(null, [ '', rows ])).done(function(fileId) {
                Gikam.getComp('emc-report-audit-list-grid').refresh();

                _this.openReportBrowseRecord(rows[0].id);
                // Gikam.openFile(fileId, true);
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
        var selections = Gikam.getComp('emc-report-audit-list-grid').getSelections();
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
        Gikam.post(emcReport.baseUrl + '/action/view-qc-report', Gikam.getJsonWrapper({}, [ '', selections ])).done(function(fileId) {
            var grid = Gikam.getComp('emc-report-audit-list-grid');
            grid.refreshRowById(selections[0].id);
            if (grid.getActivedRow()) {
                _this.onReportActive(grid.getActivedRow());
            }
            Gikam.openFile(fileId, true);
        }).always(function () {
            workspace.window.closeMask();
        });
    },

    viewRsDocument : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-report-audit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var rsReportConfigLineId = selections[0].rsReportConfigLineId;
        if (Gikam.isEmpty(rsReportConfigLineId)) {
            Gikam.alert('EMC.MODULE.BP.REPORT.REPORTS.TIP.NO_RS_REPORT_CONFIG_LINE');
            return;
        }

        Gikam.viewReport(rsReportConfigLineId, selections[0]);
    },

    dataSummaryProject : function() {
        var gridData = Gikam.getComp('emc-report-audit-list-grid').getSelections();
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

    dataSummaryRun : function() {
        var gridData = Gikam.getComp('emc-run-audit-list-grid').getSelections();
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

    onSampleActive : function(row) {
        Gikam.getAsyncComp('emc-sample-order-audit-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/queries/searchable',
                requestData : {
                    sampleId_SEQ : row.id
                }
            });
        });
    },

    onSampleOrderActive : function(row) {
        Gikam.getAsyncComp('emc-sample-test-audit-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/searchable',
                requestData : {
                    orderId_SEQ : row.id
                }
            });
        });
    },

    onRunActive : function(row) {
        Gikam.getAsyncComp('emc-run-order-task-audit-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/searchable',
                requestData : {
                    runId_SEQ : row.id
                }
            });
        });
    },

    onRunOrderTaskActive : function(row) {
        Gikam.getAsyncComp('emc-run-order-task-result-audit-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/queries/searchable',
                requestData : {
                    orderTaskId_SEQ : row.id
                }
            });
        });
    },

    onReportActive : function(row) {
    	console.log(row);
        Gikam.getComp('emc-report-audit-list-uploader').setOptions({
            bizId : row.id,
        });
        Gikam.getComp('emc-report-audit-list-after-uploader').setOptions({
            bizId : row.id+'F',
        });
        Gikam.getComp('emc-project-audit-list-uploader').setOptions({
            bizId : row.projId,
        });
        Gikam.getAsyncComp('emc-folder-audit-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/queries/searchable',
                requestData : {
                    projId_SEQ : row.projId
                }
            });
        });
        Gikam.getAsyncComp('emc-order-audit-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/queries/searchable',
                requestData : {
                    projId_SEQ : row.projId,
                    monitorTmplId_SEQ : row.monitorTmplId
                }
            });
        });
        Gikam.getAsyncComp('emc-sample-audit-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/queries/searchable',
                requestData : {
                    projId_SEQ : row.projId
                }
            });
        });
        Gikam.getAsyncComp('emc-run-audit-list-grid').done(function(grid) {
            grid.refresh({
            	 url : IFM_CONTEXT + '/secure/emc/module/bp/result/runs/queries',
                 requestData : {
                     projId : row.projId,
                 	targetType : 'reportData'
                 }
            });
        });
        Gikam.getAsyncComp('emc-audit-record-audit-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/audit-records/queries',
                requestData : {
                    projId_SEQ : row.projId
                }
            });
        });

        Gikam.getAsyncComp('emc-audit-list-archive-file-grid').done(function(grid) {
        	console.log(row.ext$.sample);
        	if(row.ext$.sample!='0'){
                grid.refresh({
                    url : emcReport.baseUrl + '/archive-categories/queries',
                    requestData : {

                    },
                    order : {
                        id : 'asc'
                    }
                });
        	}else{
                grid.refresh({
                    url : emcReport.baseUrl + '/archive-categories/queries',
                    requestData : {
                    	sample_SEQ :'0'
                    },
                    order : {
                        id : 'asc'
                    }
                });
        	}

        });

        // 根据样品类型查询对应的点位列表
        var queryFolderUrl = IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/queries/selectable';
        Gikam.getJson(queryFolderUrl + Gikam.param({
            orderCategory : row.orderCategory,
            projId : row.projId
        })).done(function (data) {
            var queryFolderForm = Gikam.getComp('emc-detail-folder-query-form');
            if (Gikam.isEmpty(data)) {
                Gikam.cleanCompData([ 'emc-detail-original-edit-list-grid', 'emc-detail-report-edit-list-grid'])
            } else {
                queryFolderForm.setData({
                    folderId_SEQ : data[0].id
                });
            }
            Gikam.getComp('emc-detail-folder-query-form').setSelectOptions("folderId_SEQ", data.map(function (item) {
                return {
                    text : item.ext$.foldername,
                    value : item.id
                };
            }));
        });

        // 查询合同附件
        Gikam.getAsyncComp('emc-report-audit-list-contract-uploader').done(function (uploader) {
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
        var rows = Gikam.getComp('emc-report-audit-list-grid').getSelections();
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
                                Gikam.getComp('emc-report-audit-list-grid').refresh()
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
                        Gikam.getComp('emc-report-audit-list-grid').refresh()
                    }
                });
            }
        });
    },

    loadFolderFilePage : function() {

        var selections = Gikam.getComp('emc-folder-audit-list-grid').getSelections();

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
                readonly : true
            }),
            onAfterClose : function(rows) {
                Gikam.getComp('emc-folder-audit-list-grid').refresh();
            }
        });
    },

    loadReportFilePage : function() {
        var selections = Gikam.getComp('emc-report-audit-list-grid').getSelections();
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
                readonly : true
            }),
            onAfterClose : function(rows) {
                Gikam.getComp('emc-report-audit-list-grid').refresh();
            }
        });
    },


    uploadFile : function() {
        var grid = Gikam.getComp('emc-report-audit-list-grid');
        var grid2 = Gikam.getComp('emc-report-audit-list-after-uploader').grid;

        var row = grid.getActivedRow();
        var bizId = row.id;
        if (!Gikam.isEmpty(row)) {
            var bizId = row.id;
            Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
                f : {
                    targetId : 'T_EMC_REPORT$' + bizId+'F'
                },
                n : -1,
                s : 50
            })).done(function(r) {

                Gikam.create('simpleUploader', {
                    dbTable : 'T_EMC_REPORT',
                    bizId : bizId+'F',
                    bizCategory : 'fileBizCategory',
                    multiple : false,
                    onAfterClose : function() {
                        grid.refresh();
                        grid2.refresh();
                    }
                });

            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    deleteFile : function() {
        var grid = Gikam.getComp('emc-report-audit-list-after-uploader').grid;
        console.log(grid);
        var selections = grid.getSelections();
        console.log(selections);
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.confirm('提示', '确认删除所选信息的附件？', function() {
            workspace.window.showMask(true);
            var ids = selections.map(function(item) {
            	console.log(item);
                return {
                    id : item.id
                }
            });
            Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', ids ])).done(function() {
                grid.refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    deleteProjectFile : function() {
        var grid = Gikam.getComp('emc-project-audit-list-uploader').grid;
        var selections = grid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.confirm('提示', '确认删除所选信息的附件？', function() {
            workspace.window.showMask(true);
            var ids = selections.map(function(item) {
            	console.log(item);
                return {
                    id : item.id
                }
            });
            Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', ids ])).done(function() {
                grid.refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-folder-audit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ {
                    id : 'emc-report-audit-list-tab',
                    type : 'tab',
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
                    }, {
                        title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.SAMPLE',
                        items : [ {
                            type : 'layout',
                            center : {
                                items : [ {
                                    type : 'layout',
                                    north : {
                                        height : 200,
                                        items : [ this.getSampleGridParam() ]
                                    },
                                    center : {
                                        items : [ {
                                            type : 'layout',
                                            west : {
                                                width : '50%',
                                                items : [ this.getSampleOrderGridParam() ]
                                            },
                                            center : {
                                                width : '50%',
                                                items : [ this.getSampleTestGridParam() ]
                                            }
                                        } ]
                                    }
                                } ]
                            },
                        } ]
                    }, {
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
                        title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.ARCHIVE_FILE',
                        id : 'emc-report-audit-archive-file-panel',
                        show : false,
                        items : [ {
                            type : 'layout',
                            center : {
                                items : [ this.getArchiveFileGridParam() ]
                            },
                        } ]
                    } ]
                } ]
            },
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
