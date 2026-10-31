emcAuditedAnalysis.editPage = {

    getProjGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-analysis-audited-list-proj-order-category-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/project/projects/analysis-projects/queries',
            columns : emcAuditedAnalysis.getEditListColumns(),
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-audited-analysis-list-run-grid').done(function(grid) {
                    grid.setOptions({
                        requestData : {
                            // monitorTmplId : row.monitorTmplId,
                            projId : row.projId,
                            scheme_only : '',
            	            orgId_filter : '1',


                        },
                        url : IFM_CONTEXT + '/secure/emc/module/bp/result/runs/queries/undoable'
                    });

                    grid.refresh();
                });

            },
            requestData : {
                status_SEQ : 'auditQuery',
	            org_filter : '1',
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    _this.runGrid.cleanData();
                }
            },
            checkOnActive : true,
        }
    },

    getRunGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-audited-analysis-list-run-grid',
            initRequest : false,
            columns : emcAuditedAnalysis.getRunColumns(),
            onCellClick : function(field, row) {
                field === 'id' && _this.loadRunDetailPage(row.id);
            },
            onRendered : function() {
                _this.runGrid = this;
            },
            requestData : {
                targetType : 'analysisAudited'
            },
        }
    },

    loadRunDetailPage : function(runId) {
        var _this = this;
        Gikam.create('modal', {
            url : emcAuditedAnalysis.baseUrl + '/page/audited-analysis-run-detail' + Gikam.param({
                id : runId,
                status_SEQ : 'done'
            }),
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
                    id : 'emc-audited-analysis-list-report-iframe',
                    src : '',
                    onRendered : function() {
                    },
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            west : {
                width : '40%',
                items : [ this.getProjGridParam() ]
            },
            center : {
                items : [ this.getRunGridParam() ]
            },
            east : {
                width : '20%',
                items : [ this.getViewReportIframe() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
