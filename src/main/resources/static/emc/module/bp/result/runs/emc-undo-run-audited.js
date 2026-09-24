emcAnalysisAudit.undoRunAuditedPage = {
    //显示有批提交到了项目编制，但是没有完全提交的批，批展示已提交的批
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-undo-run-audited-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/project/projects/analysis-projects/queries',
            columns : emcAnalysisAudit.getEditListColumns(),
            page : true,
            requestData : {
                targetType : 'projectRunAudited',
                org_filter : '1',
            },

            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-run-audited-list-grid').done(function(grid) {
                    grid.setOptions({
                        requestData : {
                            // monitorTmplId : row.ext$.monitortmplid,
                            projId : row.id,
                            scheme_only : '',
                            targetType : 'analysisAudited',
                            status_SEQ : 'done'

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
                    var runGrid = Gikam.getComp('emc-run-audited-list-grid');
                    runGrid.setOptions({
                        url : ''
                    });
                    runGrid.cleanData();
                }
            },
            checkOnActive : true,
        }
    },

    getTestRunGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-run-audited-list-grid',
            initRequest : false,
            columns : emcAnalysisAudit.getProjTestRunColumns(),
            service : 'emcRunServiceImpl',
            onCellClick : function(field, row) {
                field === 'id' && _this.loadRunDetailPage(row.id);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                }
            },
            toolbar : [ {
                type : 'button',
                text : '回退批',
                icon : 'save',
                onClick : function () {
                    _this.undoByRun();
                }
            }],
            onRendered : function() {
                _this.runGrid = this;
                this.hideColumns([ 'temperature' ]);
                this.hideColumns([ 'humidity' ]);
                this.hideColumns([ 'analysisBeginDate' ]);
                this.hideColumns([ 'analysisBeginTime' ]);
                this.hideColumns([ 'analysisEndDate' ]);
                this.hideColumns([ 'analysisEndTime' ]);

            },
        }
    },

    loadRunDetailPage : function(runId) {
        var _this = this;
        Gikam.create('modal', {
            url : emcAnalysisAudit.baseUrl + '/page/analysis-run-detail' + Gikam.param({
                id : runId
            }),
        });
    },

    undoByRun : function() {
        var selections = Gikam.getComp('emc-run-audited-list-grid').getSelections();
        var row = Gikam.getComp('emc-undo-run-audited-grid').getActivedRow();
        console.log(row);
        Gikam.confirm('GIKAM.MODAL.UNDO', 'GIKAM.TIP.UNDO', function() {
            workspace.window.showMask(true);
            Gikam.postText(emcAnalysisAudit.baseUrl + '/action/undo-audited-run', Gikam.getJsonWrapper({
                f : {
                    projNo : row.projNo
                }
            }, [ 'PfRunService', selections ])).done(function() {
                Gikam.getComp('emc-run-audited-list-grid').refresh();
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

        });
    },

    init : function() {
        this.create();
    }
};
