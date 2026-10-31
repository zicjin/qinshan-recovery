emcFolder.reviewPage = {

    getFolderGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.XL,
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.BP.FOLDERS.TAB.FOLDER',
                items : [ {
                    type : 'grid',
                    id : 'emc-folder-review-list-grid',
                    url : emcFolder.baseUrl + '/queries/searchable',
                    columns : emcFolder.getFolderListColumns(),
                    requestData : {
                        'scheme_only' : Gikam.isEmpty(_this.param.folderId) ? 'true' : '',
                        id_IN : !Gikam.isEmpty(_this.param.folderId) ? _this.param.folderId : '',
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        }
                    },
                    onRowActive : function(index, row) {
                        _this.onFolderActive(row);
                    },
                } ]
            }, ]
        }
    },

    getOrderTaskGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.XL,
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.BP.FOLDERS.TAB.ORDER_TASK',
                items : [ {
                    type : 'grid',
                    id : 'emc-folder-review-list-order-task-grid',
                    url : '',
                    columns : emcFolder.getOrderTaskListColumns(),
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        }
                    },
                    onRowActive : function(index, row) {
                        _this.onOrderTaskActive(row);
                    },
                    toolbar : [ {
                        type : 'btnGroup',
                        items : [ {
                            type : 'button',
                            text : 'GIKAM.BUTTON.LOOK_ATLAS',
                            icon : 'preview',
                            onClick : function() {
                                _this.loadAtlasByOrderTask();
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
                            text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_RUN',
                            icon : 'default',
                            onClick : function() {
                                var selections = Gikam.getComp('emc-folder-review-list-order-task-grid').getSelections();
                                if (selections.length !== 1) {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                    return;
                                }
                                var runId = selections[0].runId;
                                if (!runId) {
                                    return;
                                }

                                var url = Gikam.printf(IFM_CONTEXT + '/secure/emc/module/bp/result/runs/{id}', {
                                    id : runId
                                })
                                Gikam.getJson(url).done(function(item) {
                                    if (item) {
                                        var reportConfigLineId = item.reportConfigLineId;
                                        if (Gikam.isEmpty(reportConfigLineId)) {
                                            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.HAS_NO_REPORT_TEMPLATE');
                                            return;
                                        }
                                        Gikam.viewReport(reportConfigLineId, item);
                                    }
                                });
                            }
                        } ]
                    } ]
                } ]
            }, ]
        }
    },

    getOtherGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.BP.FOLDERS.TAB.EQUIPT',
                items : [ {
                    type : 'layout',
                    west : {
                        width : '30%',
                        items : [ {
                            type : 'tab',
                            width : Gikam.width.XL,
                            fill : true,
                            panels : [ {
                                title : 'EMC.MODULE.BP.BP.FOLDERS.TAB.EQUIPT_INFO',
                                items : [ {
                                    type : 'form',
                                    id : 'emc-folder-review-list-equipt-info-form',
                                    fields : emcFolder.getEquiptInfoFields(),
                                    readonly : true,
                                    columns : 1,
                                } ]
                            }, ]
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            width : Gikam.width.XL,
                            fill : true,
                            panels : [ {
                                title : 'EMC.MODULE.BP.BP.FOLDERS.TAB.EQUIPT_RECORD',
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-folder-review-list-equipt-record-grid',
                                    columns : emcFolder.getEquiptRecordColumns(),
                                } ]
                            }, ]
                        } ]
                    }
                } ]
            }, {
                title : 'EMC.MODULE.BP.BP.FOLDERS.TAB.RESULT_LOG',
                items : [ {
                    type : 'grid',
                    id : 'emc-folder-review-list-result-log-grid',
                    columns : emcFolder.getResultLogListColumns(),

                } ]
            }, {
                title : 'EMC.MODULE.BP.BP.FOLDERS.TAB.ORDER_TASK_LOG',
                items : [ {
                    type : 'grid',
                    id : 'emc-folder-review-list-order-task-log-grid',
                    columns : emcFolder.getOrderTaskLogListColumns(),
                } ]
            }, {
                title : 'EMC.MODULE.BP.BP.FOLDERS.TAB.RESULT',
                items : [ {
                    type : 'grid',
                    id : 'emc-folder-review-list-order-task-result-grid',
                    columns : emcFolder.getOrderTaskResultListColumns(),

                } ]
            }, {
                title : 'EMC.MODULE.BP.BP.FOLDERS.TAB.PROJECT_FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-folder-review-list-project-uploader',
                    dbTable : 'T_EMC_REPORT',
                    readonly : true,
                } ]
            } ]
        }
    },

    onOrderTaskActive : function(row) {
        Gikam.getAsyncComp('emc-folder-review-list-equipt-info-form').done(function(grid) {
            if (!Gikam.isEmpty(row.equiptId)) {
                grid.refresh({
                    url : Gikam.printf('/secure/emc/module/mdm/equipt/equipts/{id}', {
                        id : row.equiptId
                    }),
                });
            }
        });

        Gikam.getAsyncComp('emc-folder-review-list-equipt-record-grid').done(function(grid) {
            if (!Gikam.isEmpty(row.equiptId)) {
                grid.refresh({
                    url : Gikam.printf('/secure/emc/module/mdm/equipt/equipts/{id}/records/queries', {
                        id : row.equiptId
                    }),
                });
            }
        });

        Gikam.getAsyncComp('emc-folder-review-list-order-task-result-grid').done(function(grid) {
            grid.refresh({
                url : '/secure/emc/module/bp/bp/order-task-results/queries',
                requestData : {
                    orderTaskId_SEQ : row.id
                }
            });
        });

        Gikam.getAsyncComp('emc-folder-review-list-result-log-grid').done(function(grid) {
            grid.refresh({
                url : Gikam.printf('/secure/emc/module/bp/bp/order-tasks/{id}/result-logs/queries', {
                    id : row.id
                }),
            });
        });

        Gikam.getAsyncComp('emc-folder-review-list-order-task-log-grid').done(function(grid) {
            grid.refresh({
                url : Gikam.printf('/secure/emc/module/bp/bp/order-tasks/{id}/logs/queries', {
                    id : row.id
                }),
            });
        });
    },

    onFolderActive : function(row) {

        Gikam.getComp('emc-folder-review-list-project-uploader').setOptions({
            bizId : row.projId,
        });

        Gikam.getAsyncComp('emc-folder-review-list-order-task-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries',
                requestData : {
                    folderId_SEQ : row.id
                }
            });
        });
    },

    loadFilePage : function() {

        var selections = Gikam.getComp('emc-folder-review-list-order-task-grid').getSelections();

        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var runId = selections[0].runId;

        if (Gikam.isEmpty(runId)) {
            Gikam.alert('EMC.MODULE.BP.BP.FOLDERS.TIP.NO_RUNID');
            return;
        }

        Gikam.create('modal', {
            title : 'GIKAM.FILE.ATTACHMENT',
            url : '/secure/emc/module/mdm/file/files/page/view' + Gikam.param({
                id : selections[0].runId,
                table : 'T_EMC_RUN',
            }),
            onAfterClose : function(rows) {
                Gikam.getComp('emc-folder-review-list-order-task-grid').refresh();
            }
        });
    },

    loadAtlasByOrderTask : function() {

        var selections = Gikam.getComp('emc-folder-review-list-order-task-grid').getSelections();

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
                Gikam.getComp('emc-folder-review-list-order-task-grid').refresh();
            }
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,

            north : {
                height : '50%',
                items : [ {
                    type : 'layout',
                    west : {
                        width : '50%',
                        items : [ _this.getFolderGridParam() ]
                    },
                    center : {
                        width : '50%',
                        items : [ _this.getOrderTaskGridParam() ]
                    }
                } ]
            },
            center : {
                items : [ _this.getOtherGridParam() ]
            },
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
