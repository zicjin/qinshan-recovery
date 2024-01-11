emcAnalysisPretrial.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-analysis-pretrial-list-proj-order-category-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/project/projects/analysis-projects/queries',
            requestData : {
                targetType : 'analysisPretrial',
                reportEditUserId : workspace.user.id,
                orgId_SB : workspace.user.ext$.parentorgid
            },
            columns : emcAnalysisPretrial.getSearchListColumns(),
            toolbar : [ {
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
                    Gikam.viewReport('dataCorrelation', Gikam.getComp('emc-analysis-pretrial-list-proj-order-category-grid').getSelections());
                }
            } ],
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-analysis-pretrial-list-run-grid').done(function(grid) {
                    grid.setOptions({
                        requestData : {
                            monitorTmplId : row.ext$.monitortmplid,
                            projId : row.id,
                            scheme_only : ''
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
                    _this.runGrid.cleanData();
                    Gikam.getComp("emc-analysis-pretrial-list-report-iframe").refresh({
                        src : '  '
                    })
                }
            },
            checkOnActive : true,
        }
    },

    getViewReportIframe : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.BP.RESULT.RUNS.TAB.ORIGINAL_RECORD',
                items : [ {
                    type : 'iframe',
                    id : 'emc-analysis-pretrial-list-report-iframe',
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
            id : 'emc-analysis-pretrial-list-run-grid',
            initRequest : false,
            columns : emcAnalysisPretrial.getTestRunColumns(),
            service : 'emcRunServiceImpl',
            onCellClick : function(field, row) {
                field === 'id' && _this.loadRunDetailPage(row.id);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp("emc-analysis-pretrial-list-report-iframe").refresh({
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
            } ],
            requestData : {
                targetType : 'analysisAudit'
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
                            iframe: Gikam.getComp("emc-analysis-pretrial-list-report-iframe").iframe,
                            viewType: 'iframe'
                        });
                    } else {
                        Gikam.getJson(Gikam.printf(IFM_CONTEXT + "/secure/emc/module/bp/result/runs/{id}", {
                            id : runId
                        })).done(function (data) {
                            if (data) {
                                var reportUrl = Gikam.getReportUrl(data.reportConfigLineId, [row])
                                Gikam.getComp("emc-analysis-pretrial-list-report-iframe").refresh({
                                    src : reportUrl
                                })
                            }
                        })
                    }
                })
            },
        }
    },

    loadRunDetailPage : function(runId) {
        var _this = this;
        Gikam.create('modal', {
            url : emcAnalysisPretrial.baseUrl + '/page/analysis-run-detail' + Gikam.param({
                id : runId,
                status_SEQ : 'audit',
            }),
        });
    },

    undo : function() {
        var projOrderCategoryRow = Gikam.getComp('emc-analysis-pretrial-list-proj-order-category-grid').getActivedRow();

        var grid = Gikam.getComp('emc-analysis-pretrial-list-run-grid');

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
                monitorTmplId : projOrderCategoryRow.monitorTmplId,
                projId : projOrderCategoryRow.projId
            }
        }).done(function() {
            grid.cleanData();
            Gikam.getComp('emc-analysis-pretrial-list-proj-order-category-grid').refresh();
        });

    },

    // 相关性检查
    validate : function() {
        var _this = this;
        var grid = Gikam.getComp('emc-analysis-pretrial-list-proj-order-category-grid');
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
                emcAnalysisPretrial.searchPage.analysisAuditProjectSelections = selections;
                Gikam.create('modal', {
                    title : Gikam.propI18N('EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.ANALYSIS_ORDER_TASK_RESULT_ILLOGIC'),
                    url : IFM_CONTEXT + '/secure/emc/module/bp/result/runs/page/correlation-validations' + Gikam.param({
                        bizType : 'pretrial'
                    }),
                    width : 800
                });
            }
        }).always(function() {
            workspace.window.closeMask();
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
