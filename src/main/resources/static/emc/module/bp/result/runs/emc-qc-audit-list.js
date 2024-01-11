emcQcAudit.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-qc-audit-list-proj-order-category-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/project/projects/analysis-projects/queries',
            columns : emcQcAudit.getEditListColumns(),
            requestData : {
                targetType : 'analysisAudit',
                reportEditUserId : workspace.user.id,
                orgId_SB : workspace.user.ext$.parentorgid,
                orderTaskStatus : 'qcAudit'
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
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
                onClick : function() {
                    Gikam.viewReport('dataCorrelation', Gikam.getComp('emc-qc-audit-list-proj-order-category-grid').getSelections());
                }
            } ],
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-qc-audit-list-run-grid').done(function(grid) {
                    grid.setOptions({
                        requestData : {
                            monitorTmplId : row.ext$.monitortmplid,
                            projId : row.id,
                            scheme_only : '',
                        },
                        order : {
                            checkStatus : 'asc'
                        },
                        url : IFM_CONTEXT + '/secure/emc/module/bp/result/runs/queries'
                    });

                    grid.refresh();
                });

            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    var runGrid = Gikam.getComp('emc-qc-audit-list-run-grid');
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

        var selections = Gikam.getComp('emc-qc-audit-list-run-grid').getSelections();

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
                Gikam.getComp('emc-qc-audit-list-run-grid').refresh();
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
            id : 'emc-qc-audit-list-run-grid',
            initRequest : false,
            columns : emcQcAudit.getTestRunColumns(),
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
                text : 'GIKAM.WORKFLOW.BUTTON.UNDO',
                icon : 'submit',
                onClick : function() {
                    _this.undo();
                }
            }, {
                type : 'dropDownMenu',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.MORE',
                items : [ {
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
                targetType : 'analysisAudit'
            },
            onRendered : function() {
                _this.runGrid = this;
            },
            onRowActive : function(index, row) {
                var runId = row.id
                Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
                    f : {
                        targetId_SEQ : 'T_EMC_RUN$' + runId,
                        ext_SEQ : 'composePdf',
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


    loadAtlasByRun : function() {

        var runSelections = Gikam.getComp('emc-qc-audit-list-run-grid').getSelections();
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
        var gridData = Gikam.getComp('emc-qc-audit-list-run-grid').getSelections();
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

        var selections = Gikam.getComp('emc-qc-audit-list-run-grid').getSelections();

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
                    Gikam.getComp('emc-qc-audit-list-run-grid').refresh();
                }
            });
        });

    },

    loadRunDetailPage : function(runId) {
        var _this = this;
        Gikam.create('modal', {
            url : emcQcAudit.baseUrl + '/page/analysis-run-detail' + Gikam.param({
                id : runId,
                status_SEQ : 'audit'
            }),
        });
    },

    submit : function() {
        var grid = Gikam.getComp('emc-qc-audit-list-proj-order-category-grid');
        var selections = grid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.confirm('GIKAM.MODAL.SUBMIT', 'GIKAM.TIP.SUBMIT', function() {
            workspace.window.showMask(true);
            // 验证项目下的结果是否超标
            Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/result/runs/action/validate-project-result-status', Gikam.getJsonWrapper(null, [ '', selections ])).done(function (res) {
                workspace.window.closeMask();
                if (res.valid) {
                    workspace.window.showMask(true);
                    Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/result/runs/action/qc-audit-submit', Gikam.getJsonWrapper(null, [ '', selections ])).done(function() {
                        grid.refresh();
                    }).always(function () {
                        workspace.window.closeMask();
                    });

                    return;
                }

                Gikam.confirm('GIKAM.TIP.CONFIRMSUBMIT', res.message, function() {
                    workspace.window.showMask(true);
                    Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/result/runs/action/qc-audit-submit', Gikam.getJsonWrapper(null, [ '', selections ])).done(function() {
                        grid.refresh();
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                });
            });
        });
    },

    undo : function() {
        var projOrderCategoryRow = Gikam.getComp('emc-qc-audit-list-proj-order-category-grid').getActivedRow();
        var grid = Gikam.getComp('emc-qc-audit-list-run-grid');

        var selections = grid.getSelections();
        if (selections.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.confirm('GIKAM.MODAL.UNDO', 'GIKAM.TIP.UNDO', function () {
            Gikam.create('modal', {
                width : 400,
                height : 200,
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.TITLE.UPDATE_REASON',
                url : '/secure/emc/module/bp/result/runs/page/run-undo-reason',
                onAfterClose : function (reasonData) {
                    if (!Gikam.isEmpty(reasonData)) {
                        workspace.window.showMask(true);
                        Gikam.post('/secure/emc/module/bp/result/runs/action/qc-audit-run-undo', Gikam.getJsonWrapper(
                            {
                                reason : reasonData.reason,
                                monitorTmplId : projOrderCategoryRow.ext$.monitortmplid,
                                projId : projOrderCategoryRow.id
                            }, ['emcRunServiceImpl', selections])).done(function (r) {
                            grid.cleanData();
                            Gikam.getComp('szss-qc-audit-list-proj-order-category-grid').refresh();
                        }).always(function () {
                            workspace.window.closeMask();
                        });
                    }
                }
            });
        });

        /*Gikam.create('workflow').undo({
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
            Gikam.getComp('emc-qc-audit-list-proj-order-category-grid').refresh();
        });*/

    },


    // 相关性检查
    validate : function() {
        var _this = this;
        var grid = Gikam.getComp('emc-qc-audit-list-proj-order-category-grid');
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
                emcQcAudit.editPage.analysisAuditProjectSelections = selections;
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
        var selections = Gikam.getComp('emc-qc-audit-list-run-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.RECOMPOSE_RUN_PDF', 'EMC.MODULE.BP.RESULT.RUNS.TIP.CONFIRM_RECOMPOSE_RUN_PDF', function() {
            workspace.window.showMask(true);
            Gikam.postText(emcQcAudit.baseUrl + '/action/recompose-pdf', Gikam.getJsonWrapper({}, [ '', selections ])).done(function () {
                Gikam.getComp('emc-qc-audit-list-run-grid').refresh();
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
