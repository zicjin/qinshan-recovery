emcReport.updatePage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-report-update-list-grid',
            url : emcReport.baseUrl + '/queries/tasks',
            service : 'emcReportServiceImpl',
            columns : emcReport.getUpdateListColumns(),
            page : true,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-folder-update-list-grid').cleanData();
                    Gikam.getComp('emc-order-update-list-grid').cleanData();
                    Gikam.getComp('emc-sample-update-list-grid').cleanData();
                    Gikam.getComp('emc-run-update-list-grid').cleanData();
                    Gikam.getComp('emc-audit-record-update-list-grid').cleanData();
                    Gikam.getComp('emc-report-update-list-uploader').grid.cleanData();
                    Gikam.getComp('emc-project-update-list-uploader').grid.cleanData();
                }
            },
            requestData : {
                // status_SEQ : 'preLogged',
	            org_filter : '1',
            },
            onRowActive : function(index, row) {
                _this.onReportActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_REPORT',
                icon : 'browse',
                onClick : function() {
                    _this.viewDocument();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    _this.viewReport();
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
                text : '重新盖CA章',
                icon : 'edit',
                onClick : function() {
                    _this.seal();
                }
            } ]
        }
    },

    seal : function (){
        var selections = Gikam.getComp('emc-report-update-list-uploader').grid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        workspace.window.showMask(true);
        Gikam.post(emcReport.baseUrl + '/seal', Gikam.getJsonWrapper({

        }, [ 'coreFileServiceImpl', selections ])).done(function() {
            Gikam.getComp('emc-report-update-list-uploader').refresh();
            Gikam.alert('盖章完成');
        }).always(function() {
            workspace.window.closeMask();
        });
    },

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
                    id : 'emc-report-update-list-uploader',
                    dbTable : 'T_EMC_REPORT',
                } ]
            }, {
                title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.PROJECT_FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-project-update-list-uploader',
                    readonly : true,
                    dbTable : 'T_EMC_PROJECT'
                } ]
            }, {
                title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.FOLDER_INFO',
                items : [ {
                    type : 'grid',
                    id : 'emc-folder-update-list-grid',
                    columns : emcReport.getFolderEditListColumns()
                } ]
            }, {
                title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.ORDER_INFO',
                items : [ {
                    type : 'grid',
                    id : 'emc-order-update-list-grid',
                    columns : emcReport.getOrderEditListColumns()
                } ]
            } ]
        }
    },

    // 采样单
    getSampleGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sample-update-list-grid',
            columns : emcReport.getSampleEditListColumns1(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-sample-order-update-list-grid').cleanData();
                    Gikam.getComp('emc-sample-test-update-list-grid').cleanData();
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
            id : 'emc-sample-order-update-list-grid',
            columns : emcReport.getSampleOrderListColumns(),
            onRowActive : function(index, row) {
                _this.onSampleOrderActive(row);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-sample-test-update-list-grid').cleanData();
                }
            }
        }
    },

    // 采样单--测试信息
    getSampleTestGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sample-test-update-list-grid',
            columns : emcReport.getSampleTestListColumns()
        }
    },

    getRunGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-run-update-list-grid',
            columns : emcReport.getRunEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-run-order-task-update-list-grid').cleanData();
                    Gikam.getComp('emc-run-order-task-result-update-list-grid').cleanData();
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
                }, ]
            } ]
        }
    },

    loadFilePage : function() {

        var selections = Gikam.getComp('emc-run-update-list-grid').getSelections();

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
                Gikam.getComp('emc-run-update-list-grid').refresh();
            }
        });
    },

    loadAtlas : function() {

        var selections = Gikam.getComp('emc-run-order-task-update-list-grid').getSelections();

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
                Gikam.getComp('emc-run-update-list-grid').refresh();
            }
        });
    },

    getRunOrderTaskGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-run-order-task-update-list-grid',
            columns : emcReport.getRunOrderTaskListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-run-order-task-result-update-list-grid').cleanData();
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
            id : 'emc-run-order-task-result-update-list-grid',
            columns : emcReport.getRunOrderTaskResultListColumns()
        }
    },

    getAuditRecordGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-audit-record-update-list-grid',
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

    uploadSampleFile : function() {
        var selections = Gikam.getComp('emc-sample-update-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : 'GIKAM.FILE.ATTACHMENT',
            url : '/secure/emc/module/mdm/file/files/page/view' + Gikam.param({
                id : selections[0].id,
                table : 'T_EMC_SAMPLE',
                readonly : true,
            })
        });
    },

    viewReport : function() {
        Gikam.viewReport('REPORT_REPORT', {

        });
    },

    viewSampleReport : function() {
        Gikam.viewReport('SAMPLE_REPORT', {

        });
    },

    viewRunReport : function() {
        Gikam.viewReport('RUN_REPORT', {

        });
    },

    viewDocument : function() {
        var rows = Gikam.getComp('emc-report-update-list-grid').getSelections();
        if (rows.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        if (rows[0].ext$.fileid) {
            Gikam.openFile(rows[0].ext$.fileid, true);
        } else {
            Gikam.alert('EMC.MODULE.BP.REPORT.REPORTS.TIP.NO_FILE');
        }
    },

    onSampleActive : function(row) {
        Gikam.getAsyncComp('emc-sample-order-update-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/queries/searchable',
                requestData : {
                    sampleId_SEQ : row.id
                }
            });
        });
    },

    onSampleOrderActive : function(row) {
        Gikam.getAsyncComp('emc-sample-test-update-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/searchable',
                requestData : {
                    orderId_SEQ : row.id
                }
            });
        });
    },

    onRunActive : function(row) {
        Gikam.getAsyncComp('emc-run-order-task-update-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/searchable',
                requestData : {
                    runId_SEQ : row.id
                }
            });
        });
    },

    onRunOrderTaskActive : function(row) {
        Gikam.getAsyncComp('emc-run-order-task-result-update-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/queries/searchable',
                requestData : {
                    orderTaskId_SEQ : row.id
                }
            });
        });
    },

    onReportActive : function(row) {
        Gikam.getComp('emc-report-update-list-uploader').setOptions({
            bizId : row.id,
        });
        Gikam.getComp('emc-project-update-list-uploader').setOptions({
            bizId : row.projId,
        });
        Gikam.getAsyncComp('emc-folder-update-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/queries/searchable',
                requestData : {
                    projId_SEQ : row.projId
                }
            });
        });
        Gikam.getAsyncComp('emc-order-update-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/queries/searchable',
                requestData : {
                    projId_SEQ : row.projId,
                    monitorTmplId_SEQ : row.monitorTmplId
                }
            });
        });
        Gikam.getAsyncComp('emc-sample-update-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/queries/searchable',
                requestData : {
                    projId_SEQ : row.projId
                }
            });
        });
        Gikam.getAsyncComp('emc-run-update-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/result/runs/queries/searchable',
                requestData : {
                    projId_SEQ : row.projId
                }
            });
        });
        Gikam.getAsyncComp('emc-audit-record-update-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/audit-records/queries',
                requestData : {
                    projId_SEQ : row.projId
                }
            });
        });
    },

    loadFolderFilePage : function() {

        var selections = Gikam.getComp('emc-folder-update-list-grid').getSelections();

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
                readonly : true,
            }),
            onAfterClose : function(rows) {
                Gikam.getComp('emc-folder-update-list-grid').refresh();
            }
        });
    },

    loadReportFilePage : function() {
        var selections = Gikam.getComp('emc-report-update-list-grid').getSelections();

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
                Gikam.getComp('emc-report-update-list-grid').refresh();
            }
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-folder-update-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ {
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
                    } ]
                } ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
