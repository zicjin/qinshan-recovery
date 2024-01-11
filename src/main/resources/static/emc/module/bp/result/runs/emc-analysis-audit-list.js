emcAnalysisAudit.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-analysis-audit-list-proj-order-category-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/project/projects/analysis-projects/queries',
            columns : emcAnalysisAudit.getEditListColumns(),
            page : true,
            requestData : {
                targetType : 'analysisAudit',
                reportEditUserId : workspace.user.id,
                orgId_SB : workspace.user.ext$.parentorgid,
                orderTaskStatus : 'audit'
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                hidden : true,
                onClick : function() {
                    _this.submit();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.CORRELATION_VALIDATION',
                icon : 'review',
                hidden : true,
                onClick : function() {
                    _this.validate();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.VIEW_REPORT',
                icon : 'review',
                hidden : true,
                onClick : function() {
                    Gikam.viewReport('dataCorrelation', Gikam.getComp('emc-analysis-audit-list-proj-order-category-grid').getSelections());
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.VIEW_PROJECT_PROCESS',
                icon : 'review',
                onClick : function() {
                    Gikam.viewReport('projectProcess', Gikam.getComp('emc-analysis-audit-list-proj-order-category-grid').getSelections());
                }
            } ],
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-analysis-audit-list-run-grid').done(function(grid) {
                    grid.setOptions({
                        requestData : {
                            monitorTmplId : row.ext$.monitortmplid,
                            projId : row.id,
                            scheme_only : '',
                        },
                        order : {
                            checkStatus : 'asc'
                        },
                        url : IFM_CONTEXT + '/secure/emc/module/bp/result/runs/queries/undoable'
                    });

                    grid.refresh();
                });

            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    var runGrid = Gikam.getComp('emc-analysis-audit-list-run-grid');
                    runGrid.setOptions({
                        url : ''
                    });
                    runGrid.cleanData();
                    Gikam.getComp("emc-analysis-audit-list-report-iframe").refresh({
                        src : '  '
                    });
                }
            },
            checkOnActive : true,
        }
    },

    loadFilePage : function() {

        var selections = Gikam.getComp('emc-analysis-audit-list-run-grid').getSelections();

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
                Gikam.getComp('emc-analysis-audit-list-run-grid').refresh();
            }
        });
    },

    getViewReportIframe : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.BP.RESULT.RUNS.TAB.ORIGINAL_RECORD',
                items : [ {
                    type : 'iframe',
                    id : 'emc-analysis-audit-list-report-iframe',
                    src : '',
                    onRendered : function() {
                    },
                } ]
            } ]
        }
    },

    getTestRunGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-analysis-audit-list-run-grid',
            initRequest : false,
            columns : emcAnalysisAudit.getTestRunColumns(),
            service : 'emcRunServiceImpl',
            onCellClick : function(field, row) {
                field === 'id' && _this.loadRunDetailPage(row.id);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp("emc-analysis-audit-list-report-iframe").refresh({
                        src : ' '
                    })
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submitRun();
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.UNDO',
                icon : 'back',
                hidden : false,
                onClick : function() {
                    _this.undo();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.CHECK_PASS',
                icon : 'finished-product-to-check',
                onClick : function() {
                    _this.batchCheck();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RUNS.TAB.BROWSE_RECORDS',
                icon : 'browse',
                onClick : function() {
                    _this.openBrowseRecord();
                }
            }, {
                type : 'dropDownMenu',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.MORE',
                items : [ {
                    type : 'button',
                    text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.UNDO_SUBMITTED_RUN',
                    icon : 'back',
                    hidden : false,
                    onClick : function() {
                        _this.undoSubmittedRun();
                    }
                }, {
                    type : 'button',
                    text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.ATLAS_LOOK',
                    icon : 'preview',
                    onClick : function() {
                        _this.loadAtlasByRun();
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.FILE.ATTACHMENT',
                    icon : 'add',
                    onClick : function() {
                        _this.loadFilePage();
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
                    text : 'GIKAM.BUTTON.REVIEW',
                    icon : 'browse',
                    onClick : function() {
                        _this.review();
                    }
                }, {
                    type : 'button',
                    text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.RECOMPOSE_RUN_PDF',
                    icon : 'revert',
                    onClick : function() {
                        _this.recomposeRunPdf();
                    }
                } ]
            } ],

            requestData : {
                targetType : 'analysisAudit',
                checkUserId_DIFFER : workspace.user.id,
                analysisUserId_DIFFER : workspace.user.id,
            },
            onRendered : function() {
                _this.runGrid = this;
                this.hideColumns([ 'temperature' ]);
                this.hideColumns([ 'humidity' ]);
                this.hideColumns([ 'analysisBeginDate' ]);
                this.hideColumns([ 'analysisBeginTime' ]);
                this.hideColumns([ 'analysisEndDate' ]);
                this.hideColumns([ 'analysisEndTime' ]);

            },
            onRowActive : function(index, row) {
                var runId = row.id
                Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
                    f : {
                        targetId_SEQ : 'T_EMC_RUN$' + runId,
                        ext_SEQ : 'composeAllPdf',
                    },
                    n : -1,
                    s : 50
                })).done(function (r) {
                    // 如果有生成原始记录PDF，直接加载PDF文件，否则加载报表
                    if (r.rows && r.rows.length > 0) {
                        Gikam.openFileByOffice({
                            fileId: r.rows[0].id,
                            mode: 'view',
                            iframe: Gikam.getComp("emc-analysis-audit-list-report-iframe").iframe,
                            viewType: 'iframe'
                        });
                    } else {
                        Gikam.getJson(Gikam.printf(IFM_CONTEXT + "/secure/emc/module/bp/result/runs/{id}", {
                            id : runId
                        })).done(function (data) {
                            if (data) {
                                var reportUrl = Gikam.getReportUrl(data.reportConfigLineId, [row])
                                Gikam.getComp("emc-analysis-audit-list-report-iframe").refresh({
                                    src : reportUrl
                                })
                            }
                        })
                    }
                })
            },
        }
    },

    openBrowseRecord : function() {
        var _this = this,
            _choose = Gikam.choose.getConfig('browseAnalysisRecords'),
            _grid = Gikam.getComp('emc-analysis-audit-list-run-grid'),
            _selections = _grid.getSelections();

        if (_selections.length != 1) {
            Gikam.alert("GIKAM.TIP.CHOOSE_ONE_DATA");
            return;
        }

        Gikam.create('modal', {
            width : _choose.width,
            height : '80%',
            title : _choose.title,
            url : _choose.url+Gikam.param({
                runId : _selections[0].id,
            }),
        });
    },

    batchCheck : function() {
        let runSelections = Gikam.getComp('emc-analysis-audit-list-run-grid').getSelections();
        if (Gikam.isEmpty(runSelections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let project = Gikam.getComp('emc-analysis-audit-list-proj-order-category-grid').getActivedRow();
        Gikam.confirm('GIKAM.TIP.CONFIRM', 'EMC.MODULE.BP.RESULT.RUNS.TIP.CONFIRM_CHECK_PASS', function() {
            workspace.window.showMask(true);
            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/result/runs/action/check-pass', Gikam.getJsonWrapper(null, [ 'emcProjectServiceImpl', [ project ] ], [ 'emcRunServiceImpl', runSelections ])).done(function() {
                Gikam.getComp('emc-analysis-audit-list-run-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    loadAtlasByRun : function() {

        var runSelections = Gikam.getComp('emc-analysis-audit-list-run-grid').getSelections();
        if (runSelections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.RUNS.TAB.LOOK_ATLAS',
            url : '/secure/emc/module/bp/bp/order-tasks/page/atlas' + Gikam.param({
                targetIds : [ 'T_EMC_EQUIPT$' + runSelections[0].id ],
                runId_SEQ : runSelections[0].id
            })
        });
    },

    dataSummaryRun : function() {
        var gridData = Gikam.getComp('emc-analysis-audit-list-run-grid').getSelections();
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

    review : function() {

        var selections = Gikam.getComp('emc-analysis-audit-list-run-grid').getSelections();

        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group', Gikam.getJsonWrapper({
            f : {
                runId_SEQ : selections[0].id,
                status_SEQ : 'audit',
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
                    Gikam.getComp('emc-analysis-audit-list-run-grid').refresh();
                }
            });
        });

    },

    loadRunDetailPage : function(runId) {
        var _this = this;
        Gikam.create('modal', {
            url : emcAnalysisAudit.baseUrl + '/page/analysis-run-detail' + Gikam.param({
                id : runId,
                status_SEQ : 'audit'
            }),
        });
    },

    submit : function() {
        var grid = Gikam.getComp('emc-analysis-audit-list-proj-order-category-grid');
        var selections = grid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        //数据相关性检查
        workspace.window.showMask(true);
        Gikam.put('/secure/emc/module/bp/result/runs/correlation-validations', Gikam.getJsonWrapper({}, [ 'emcProjectServiceImpl', selections ])).done(function(r) {
            // 返回值为1表示数据全部合理，可以直接提交
            if (r === '1') {
                //提交
                Gikam.confirm('GIKAM.MODAL.SUBMIT', 'GIKAM.TIP.SUBMIT', function() {
                    workspace.window.showMask(true);
                    Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/result/runs/action/submit', Gikam.getJsonWrapper(null, [ '', selections ])).done(function() {
                        grid.refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                });
            }
            // 否则，表示存在不合理数据，询问是否继续提交
            else {
                //提交
                Gikam.confirm('GIKAM.MODAL.SUBMIT', 'EMC.MODULE.BP.PROJECT.PROJECTS.TIP.DATA_CORRELATION_EXISTS_IRATIONAL', function() {
                    Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/result/runs/action/submit', Gikam.getJsonWrapper(null, [ '', selections ])).done(function() {
                        grid.refresh();
                    });
                });
            }
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    submitRun : function() {
        let selections = Gikam.getComp('emc-analysis-audit-list-run-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        for (let i = 0; i < selections.length; i++) {
            if (selections[i].checkStatus !== '1') {
                Gikam.alert(selections[i].id + "批次未审核通过，请审核通过后提交");
                return;
            }
        }
        let project = Gikam.getComp('emc-analysis-audit-list-proj-order-category-grid').getActivedRow();
        Gikam.confirm('GIKAM.MODAL.SUBMIT', 'GIKAM.TIP.SUBMIT', function() {
            workspace.window.showMask(true);
            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/result/runs/action/submit-project-run', Gikam.getJsonWrapper(null, [ 'emcProjectServiceImpl', [ project ] ], [ 'emcRunServiceImpl', selections ])).done(function() {
                Gikam.getComp('emc-analysis-audit-list-run-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    undo : function() {
        var projOrderCategoryRow = Gikam.getComp('emc-analysis-audit-list-proj-order-category-grid').getActivedRow();
        var grid = Gikam.getComp('emc-analysis-audit-list-run-grid');

        Gikam.create('workflow').undo({
            data : grid.getSelections(),
            pageObject : {
                baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/result/runs',
                workflow : {
                    dbTable : 'T_EMC_RUN',
                    columns : [ {
                        field : 'id',
                        title : 'T_EMC_RUN.ID'
                    } ]
                }
            },
            requestParam : {
                monitorTmplId : projOrderCategoryRow.ext$.monitortmplid,
                projId : projOrderCategoryRow.id
            }
        }).done(function() {
            grid.cleanData();
            Gikam.getComp('emc-analysis-audit-list-proj-order-category-grid').refresh();
        });

    },

    /** 回退已提交的批次 */
    undoSubmittedRun : function() {
        var projectRow = Gikam.getComp('emc-analysis-audit-list-proj-order-category-grid').getActivedRow();
        if (Gikam.isEmpty(projectRow)) {
            return;
        }

        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.RUNS.TITLE.CHOOSE_RUN',
            width : '80%',
            url : IFM_CONTEXT + '/secure/emc/module/bp/result/runs/page/choose-list' + Gikam.param({
                targetType : 'undoSubmittedRun',
                projId : projectRow.id,
                status_SEQ : 'done'
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                Gikam.create('workflow').undo({
                    data : rows,
                    pageObject : {
                        baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/result/runs',
                        workflow : {
                            dbTable : 'T_EMC_RUN',
                            columns : [ {
                                field : 'id',
                                title : 'T_EMC_RUN.ID'
                            } ]
                        }
                    },
                    requestParam : {
                        monitorTmplId : projectRow.ext$.monitortmplid,
                        projId : projectRow.id
                    }
                })
            }
        });
    },

    // 相关性检查
    validate : function() {
        var _this = this;
        var grid = Gikam.getComp('emc-analysis-audit-list-proj-order-category-grid');
        var selections = grid.getSelections();
        if (Gikam.isEmpty(selections)) {
            return;
        }
        workspace.window.showMask(true);
        Gikam.put('/secure/emc/module/bp/result/runs/correlation-validations', Gikam.getJsonWrapper({}, [ 'emcProjectServiceImpl', selections ])).done(function(r) {
            // 返回值为1表示数据全部合理，给出提示
            if (r === '1') {
                Gikam.alert(Gikam.propI18N('EMC.MODULE.BP.PROJECT.PROJECTS.TIP.ANALYSIS_ORDER_TASK_RESULT_RATIONAL'), 80);
            }
            // 否则，表示存在不合理数据，查询展示
            else {
                emcAnalysisAudit.editPage.analysisAuditProjectSelections = selections;
                Gikam.create('modal', {
                    title : Gikam.propI18N('EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.ANALYSIS_ORDER_TASK_RESULT_ILLOGIC'),
                    url : IFM_CONTEXT + '/secure/emc/module/bp/result/runs/page/correlation-validations' + Gikam.param({
                        bizType : 'audit'
                    }),
                    width : 800
                });
            }
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    /** 重新合成原始记录PDF */
    recomposeRunPdf : function() {
        var selections = Gikam.getComp('emc-analysis-audit-list-run-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.RECOMPOSE_RUN_PDF', 'EMC.MODULE.BP.RESULT.RUNS.TIP.CONFIRM_RECOMPOSE_RUN_PDF', function() {
            workspace.window.showMask(true);
            Gikam.postText(emcAnalysisAudit.baseUrl + '/action/recompose-pdf', Gikam.getJsonWrapper({}, [ '', selections ])).done(function () {
                Gikam.getComp('emc-analysis-audit-list-run-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            west : {
                width : '25%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getTestRunGrid() ]
            },
            east : {
                width : '45%',
                items : [ this.getViewReportIframe() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
