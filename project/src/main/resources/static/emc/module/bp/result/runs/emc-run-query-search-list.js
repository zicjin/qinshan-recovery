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
                        org_filter : '1',
                        createdById_SEQ: workspace.user.id,
                    };
                }
                if (data.index == 1) {
                    gridId = 'emc-run-query-search-my-approve-grid';
                    requestData = {
                        org_filter : '1',
                        approveUserId: workspace.user.id,
                    };
                }
                if (data.index == 2) {
                    gridId = 'emc-run-query-search-my-audit-grid';
                    requestData = {
                        org_filter : '1',
                        auditedUserId_SEQ: workspace.user.id,
                    };
                }
                if (data.index == 3) {
                    gridId = 'emc-run-query-search-all-grid';
                    requestData = {
                        org_filter : '1',
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
        }, {
            type: 'button',
            text: '导出原始记录',
            icon: 'export',
            onClick: function () {
                _this.exportOriginalRecordExcel(gridId);
            }
        },{
            type : 'button',
            text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.ATLAS_LOOK',
            icon : 'preview',
            onClick : function() {
                _this.loadAtlasByRun(gridId);
            }
        }, {
            type : 'button',
            text : 'GIKAM.FILE.ATTACHMENT',
            icon : 'add',
            onClick : function() {
                _this.loadFilePage(gridId);
            }
        }, {
            type : 'button',
            text : '查看任务单',
            icon : 'print',
            onClick : function() {
                _this.openTaskReport(gridId);
            }
        },{
            type: 'button',
            text: 'GIKAM.BUTTON.VIEWAUDIT',
            icon: 'view-audit',
            onClick: function () {
                Gikam.viewAudit('T_EMC_RUN', Gikam.getComp(gridId).getSelections());
            }
        }]
    },

    loadFilePage : function(gridId) {

        var selections = Gikam.getComp(gridId).getSelections();

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
            }
        });
    },

    loadAtlasByRun : function(gridId) {

        var runSelections = Gikam.getComp(gridId).getSelections();
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

    openTaskReport : function(gridId) {
        var selections = Gikam.getComp(gridId).getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.getJson(Gikam.printf('/secure/emc/module/bp/project/projects/{id}', {
            id : selections[0].projId
        })).done(function(project) {
            if (!project) {
                Gikam.alert('GIKAM.TIP.OPERATE_FAIL');
                return;
            }

            if (project.sample == '0') {
                Gikam.viewReport('1453125616129984', project);
            } else {
                Gikam.viewReport('1344186996217184', project);
            }
        });
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
    exportOriginalRecordExcel: function (gridId) {
        var selections = Gikam.getComp(gridId).getSelections();
        if (selections.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ITEM');
            return;
        }
        Gikam.toast('正在导出原始记录，请稍候...', 3, 'top');
        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/result/runs/action/export-original-record-excel-zip',
            Gikam.getJsonWrapper({}, ['', selections])).done(function (url) {
            if (Gikam.isEmpty(url)) {
                Gikam.alert('导出失败！');
                return;
            }
            Gikam.emcDownloadFile(url);
            Gikam.alert('导出完成！');
        }).fail(function () {
            Gikam.alert('导出失败！');
        });
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
