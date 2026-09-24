emcAnalysisAudit.unAuditPage = {

    getGridParam : function() {
        var _this = this;
        var dateEnd = new Date("2022-10-01");
        return {
            type : 'grid',
            id : 'emc-project-run-unaudit-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/project/projects/analysis-projects/queries',
            columns : emcAnalysisAudit.getEditListColumns(),
            page : true,
            requestData : {
                targetType : 'projectRunUnAudit',
                org_filter : '1',
                LASTESTACCEPTEDDATE_DGOE :Gikam.DateUtils.formatter(dateEnd, 'yyyy-MM-dd')
            },
            rowStyleFormatter : function(row) {
                if (!Gikam.isEmpty(row.ext$.newestauditdate)){
                    var  newestauditdate = Gikam.DateUtils.toDate(row.ext$.newestauditdate);
                    if (Gikam.DateUtils.formatter(newestauditdate, 'yyyy-MM-dd') == Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd')) {
                        return 'background-color : #F08080';
                    }
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-project-run-unaudit-list-grid').done(function(grid) {
                    grid.setOptions({
                        requestData : {
                            // monitorTmplId : row.ext$.monitortmplid,
                            projId : row.id,
                            scheme_only : '',
                            targetType : 'projectRunUnAudit',
                            status_DIFFER : 'done'

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
                    var runGrid = Gikam.getComp('emc-project-run-unaudit-list-grid');
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
            id : 'emc-project-run-unaudit-list-grid',
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
