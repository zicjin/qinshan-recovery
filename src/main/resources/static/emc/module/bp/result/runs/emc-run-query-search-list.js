emcRun.runQuerySearchPage = {
    getRunQueryTab: function () {
        var _this = this, columns = emcRun.getTestRunSearchColumns(), order = {
                'ext$.adddate': 'desc',
                'id': 'desc'
            };
        return {
            id: 'emc-run-query-search-list-tab',
            type: 'tab',
            fill: true,
            panels: [
                {//我的记录
                    title: 'EMC.MODULE.BP.RUN.TAB.MYSELF_RECORD',
                    id: 'emc-run-query-search-myself-panel',
                    items: [
                        _this.getRunQuerySearchPanel(
                            'emc-run-query-search-myself-grid',
                            order,
                            columns,
                            _this.getRunButton('emc-run-query-search-myself-grid'))
                    ]
                },
                {//校核记录
                    title: 'EMC.MODULE.BP.RUN.TAB.MY_APPROVE_RECORD',
                    id: 'emc-run-query-search-my-approve-panel',
                    items: [
                        _this.getRunQuerySearchPanel(
                            'emc-run-query-search-my-approve-grid',
                            order,
                            columns,
                            _this.getRunButton('emc-run-query-search-my-approve-grid'))
                    ]
                },
                {//审核记录
                    title: 'EMC.MODULE.BP.RUN.TAB.MY_AUDIT_RECORD',
                    id: 'emc-run-query-search-my-audit-panel',
                    items: [
                        _this.getRunQuerySearchPanel(
                            'emc-run-query-search-my-audit-grid',
                            order,
                            columns,
                            _this.getRunButton('emc-run-query-search-my-audit-grid'))
                    ]
                },
                {//分析记录
                    title: 'EMC.MODULE.BP.RUN.TAB.ALL_RECORD',
                    id: 'emc-run-query-search-all-panel',
                    items: [
                        _this.getRunQuerySearchPanel(
                            'emc-run-query-search-all-grid',
                            order,
                            columns,
                            _this.getRunButton('emc-run-query-search-all-grid'))
                    ]
                }
            ],
            onActived: function (data) {
                var url = emcRun.baseUrl + '/queries',requestData = {},gridId = '';
                if (data.index == 0) {
                    gridId = 'emc-run-query-search-myself-grid';
                    requestData = {
                        orgId_SB : workspace.user.ext$.parentorgid,
                        createdById_SEQ: workspace.user.id,
                    };
                }
                if (data.index == 1) {
                    gridId = 'emc-run-query-search-my-approve-grid';
                    requestData = {
                        orgId_SB : workspace.user.ext$.parentorgid,
                        approveUserId: workspace.user.id,
                    };
                }
                if (data.index == 2) {
                    gridId = 'emc-run-query-search-my-audit-grid';
                    requestData = {
                        orgId_SB : workspace.user.ext$.parentorgid,
                        auditedUserId_SEQ: workspace.user.id,
                    };
                }
                if (data.index == 3) {
                    gridId = 'emc-run-query-search-all-grid';
                    requestData = {
                        orgId_SB : workspace.user.ext$.parentorgid,
                    };
                }

                Gikam.getAsyncComp(gridId).done(function (grid) {
                    grid.refresh({
                        url: url,
                        requestData: requestData
                    });
                });
            }
        }
    },
    getRunButton: function (gridId) {
        var _this = this;
        return [{
            type: 'button',
            text: '查看原始记录',
            icon: 'default',
            onClick: function () {
                _this.runViewRun(gridId);
            }
        }]
    },
    runViewRun : function(gridId) {
        var selections = Gikam.getComp(gridId).getSelections();
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
    loadResultInputPage : function(runId, index) {
        Gikam.create('modal', {
            height : '95%',
            width : '95%',
            title : 'EMC.MODULE.BP.RESULT.RUNS.TAB.TSAMPLE_TEST',
            url : emcRun.baseUrl + '/page/result-input' + Gikam.param({
                runId : runId,
                index : index,
                readonly : true
            }),
        });
    },
    getRunQuerySearchPanel: function (gridId, order, columns, toolbar) {
        var _this = this;
        return {
            type: 'layout',
            center: {
                items: [{
                    type: 'grid',
                    id: gridId,
                    service: 'emcRunServiceImpl',
                    columns: columns,
                    order: order,
                    page: true,
                    filterOpen : true,
                    onCellClick: function (field, row) {
                        field === 'id' && _this.loadRunDetail(gridId, row);
                    },
                    toolbar: toolbar
                }]
            },
        }
    },
    loadRunDetail: function (gridId, row) {
        Gikam.create('modal', {
            height: '95%',
            width: '95%',
            title: '测试批详情',
            url: emcRun.baseUrl + '/page/run-input-detail' + Gikam.param({
                runId: row.id,
                analysisDate: row.analysisBeginDate,
                readonly: true
            }),
            onAfterClose: function () {
                Gikam.getComp(gridId).refreshRowById(row.id);
            }
        });
    },
    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-run-query-search-list-layout',
            renderTo: workspace.window.$dom,
            center: {
                items: [this.getRunQueryTab()]
            }
        });
    },

    init: function (param) {
        this.param = param;
        this.create();
    }
};
