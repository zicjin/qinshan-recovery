emcRunAudit.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-run-audit-list-test-run-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/result/runs/queries/tasks',
            requestData : {
                showTotalFlag : '0',
                checkUserId : workspace.user.id,
                analysisUserId_DIFFER : workspace.user.id,
                org_filter : '1',
            },
            service : 'emcRunServiceImpl',
            columns : emcRunAudit.getEditListColumns(),
            onRendered : function() {
                this.hideColumns([ 'temperature' ]);
                this.hideColumns([ 'humidity' ]);
                this.hideColumns([ 'analysisBeginDate' ]);
                this.hideColumns([ 'analysisBeginTime' ]);
                this.hideColumns([ 'analysisEndDate' ]);
                this.hideColumns([ 'analysisEndTime' ]);
            },
            onCellClick : function(field, row) {
                field === 'id' && _this.loadRunDetailPage(row.id);
            },
            order: {
                urgent: 'desc'
            },
            rowStyleFormatter : function(row) {
                if (row.ext$.urgent === '1') {
                    return {
                        backgroundColor: Gikam.emcUrgentColor
                    }
                }
            },
            onLoadSuccess : function(data) {
                // if (!Gikam.isEmpty(data)) {
                    //this.activeRowByIndex(0);
                // } else {
                    // Gikam.getComp("emc-run-audit-list-report-iframe").refresh({
                    //     src : ' '
                    // })
                // }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                icon : 'pass',
                onClick : function() {
                    _this.pass();
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                icon : 'reject',
                onClick : function() {
                    _this.reject();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RUNS.TAB.BROWSE_RECORDS',
                icon : 'browse',
                onClick : function() {
                    _this.openBrowseRecord();
                }
            },  {
                  type : 'button',
                  text : '分析结果检查',
                  icon : 'print',
                  onClick : function() {
                        Gikam.viewReport('EMC_ANALYSIS_AUDIT', Gikam.getComp('emc-run-audit-list-test-run-grid').getSelections());
                  }
              },{
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
            }, {
                type : 'form',
                id : 'emc-run-audit-list-form',
                fields : emcRunAudit.getRunAuditShowAllField(),
                autoSave : false,
            } ],
            // onRowActive : function(index, row) {
            //     var runId = row.id
            //     Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
            //         f : {
            //             targetId_SEQ : 'T_EMC_RUN$' + runId,
            //             ext_SEQ : 'composeAllPdf',
            //         },
            //         n : -1,
            //         s : 50
            //     })).done(function(r) {
            //         // 如果有生成原始记录PDF，直接加载PDF文件，否则加载报表
            //         if (r.rows && r.rows.length > 0) {
            //             Gikam.openFileByOffice({
            //                 fileId: r.rows[0].id,
            //                 mode: 'view',
            //                 iframe: Gikam.getComp("emc-run-audit-list-report-iframe").iframe,
            //                 viewType: 'iframe'
            //             });
            //         } else {
            //             Gikam.getJson(Gikam.printf(emcRunAudit.baseUrl + "/{id}", {
            //                 id : runId
            //             })).done(function(data) {
            //                 if (data) {
            //                     var reportUrl = Gikam.getReportUrl(data.reportConfigLineId, [row])
            //                     Gikam.getComp("emc-run-audit-list-report-iframe").refresh({
            //                         src : reportUrl
            //                     })
            //                 }
            //             })
            //         }
            //     })
            // },
        }
    },

    loadAtlasByRun : function() {

        var runSelections = Gikam.getComp('emc-run-audit-list-test-run-grid').getSelections();
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

    review : function() {

        var selections = Gikam.getComp('emc-run-audit-list-test-run-grid').getSelections();

        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group', Gikam.getJsonWrapper({
            f : {
                runId_SEQ : selections[0].id,
                status_SEQ : 'approve',
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
                    Gikam.getComp('emc-run-audit-list-test-run-grid').refresh();
                }
            });
        });

    },

    /** 重新合成原始记录PDF */
    recomposeRunPdf : function() {
        var selections = Gikam.getComp('emc-run-audit-list-test-run-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.RECOMPOSE_RUN_PDF', 'EMC.MODULE.BP.RESULT.RUNS.TIP.CONFIRM_RECOMPOSE_RUN_PDF', function() {
            workspace.window.showMask(true);
            Gikam.postText(emcRunAudit.baseUrl + '/action/recompose-pdf', Gikam.getJsonWrapper({}, [ '', selections ])).done(function () {
                Gikam.getComp('emc-run-audit-list-test-run-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    loadFilePage : function() {

        var selections = Gikam.getComp('emc-run-audit-list-test-run-grid').getSelections();

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
                Gikam.getComp('emc-run-audit-list-test-run-grid').refresh();
            }
        });
    },

    loadRunDetailPage : function(runId) {
        var _this = this;
        Gikam.create('modal', {
            url : emcRunAudit.baseUrl + '/page/run-detail' + Gikam.param({
                id : runId
            }),
        });
    },

    // getViewReportIframe : function() {
    //     var _this = this;
    //     return {
    //         type : 'tab',
    //         panels : [ {
    //             title : 'EMC.MODULE.BP.RESULT.RUNS.TAB.ORIGINAL_RECORD',
    //             items : [ {
    //                 type : 'iframe',
    //                 id : 'emc-run-audit-list-report-iframe',
    //                 src : ''
    //             } ]
    //         } ]
    //     }
    // },

    pass : function() {
        var grid = Gikam.getComp('emc-run-audit-list-test-run-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : emcRunAudit
        }).done(function() {
            grid.refresh();
            // Gikam.getComp("emc-run-audit-list-report-iframe").refresh({
            //     src : ''
            // })
        });
    },

    openBrowseRecord : function() {
        var _this = this,
            _choose = Gikam.choose.getConfig('browseAnalysisRecords'),
            _grid = Gikam.getComp('emc-run-audit-list-test-run-grid'),
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

    reject : function() {
        var grid = Gikam.getComp('emc-run-audit-list-test-run-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcRunAudit
        }).done(function() {
            grid.refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-analysis-check-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                // width : '50%',
                items : [ this.getGridParam() ]
            },
            // east : {
            //     width : '50%',
            //     items : [ this.getViewReportIframe() ]
            // }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
