emcProjectQuery.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-project-query-edit-list-grid',
            columns : emcProjectQuery.getEditListColumns(),
            url : emcProjectQuery.projectUrl + '/queries/all',
            service : 'emcProjectServiceImpl',
            readonly : true,
            filterOpen : true,
            page : true,
            requestData : {
                targetType : 'modifyProjectInfo',
                orgId_SB : workspace.user.ext$.parentorgid,
            },
            onBeforeRefresh : function (requestData) {
                var orgName = workspace.user.orgName;
                if(orgName.indexOf("质量部") >-1){
                    requestData['projNo_SE'] = 'Z';
                }else{
                    requestData['projNo_DIFFER'] = 'Z';
                }
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ "emc-project-query-edit-list-base-info-form", "emc-project-query-edit-list-proj-scheme-grid", "emc-project-query-edit-list-proj-order-grid", "emc-project-query-edit-list-director-grid" ]);
                    Gikam.getComp('emc-project-query-edit-list-base-info-tab').removePanel('emc-project-query-edit-list-proj-scheme-panel');
                }
            },
            onRowActive : function(index, row) {
                _this.onProjectActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.SEARCH',
                icon : 'preview',
                onClick : function() {
                    Gikam.create('modal', {
                        title : 'EMC.MODULE.SA.PROJECT_QUERYS.MODAL.QUERY_BY_COND',
                        width : '700',
                        height : '450',
                        url : emcProjectQuery.baseUrl + '/page/cond-query',
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('xianchanglianxi', Gikam.getComp('emc-project-query-edit-list-grid').getSelections());
                }
            },{
                type : 'dropDownMenu',
                text : '客服联系文件',
                items : [{
                    type : 'button',
                    text : '导出文件',
                    icon : 'default',
                    onClick : function() {
                        _this.exportSampleFile();
                    },
                },{
                    type : 'button',
                    text : '查看文件',
                    icon : 'upload',
                    onClick : function() {
                        _this.viewExportSampleFile();
                    }
                }]
            }, {
                type : 'dropDownMenu',
                text : '更多操作',
                items : [ {
                    type : 'button',
                    text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.COMPLETERECORD',
                    icon : 'batch-to-check',
                    onClick : function() {
                        _this.completeProject();

                    }
                }, {
                    type : 'button',
                    text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.CANCELCOMPLETERECORD',
                    icon : 'batch-to-check',
                    onClick : function() {
                        _this.cancelCompleteProject();

                    }
                }, {
                    type : 'button',
                    text : 'EMC.MODULE.BP.PROJECT.BUTTON.TASK_PROCESS_DISPLAY',
                    icon : 'print',
                    onClick : function() {
                        Gikam.viewReport('taskProcessDisplay',[{}]);
                        //Gikam.viewReport('taskProcessDisplay', Gikam.getComp('emc-project-query-edit-list-grid').getSelections());
                    }
                }, {
                    type : 'button',
                    text : 'EMC.MODULE.BP.PROJECT.BUTTON.SEND_REPORT',
                    icon : 'export',
                    onClick : function() {
                        var selections = Gikam.getComp('emc-project-query-edit-list-grid').getSelections();
                        if (Gikam.isEmpty(selections)) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                            return;
                        }

                        Gikam.create('modal', {
                            title : 'EMC.MODULE.BP.PROJECT.TITLE.ARCHIVED_REPORT',
                            width : '50%',
                            height : '70%',
                            url : IFM_CONTEXT + '/secure/emc/module/bp/project/projects/page/report-choose-list' + Gikam.param({
                                id : selections[0].id
                            }),
                            onAfterClose : function(rows) {
                                if (Gikam.isEmpty(rows)) {
                                    return;
                                }

                                Gikam.confirm('EMC.MODULE.BP.PROJECT.TITLE.CONFIRM_OPERATE', 'EMC.MODULE.BP.PROJECT.TIP.CONFIRM_SEND_REPORT', function() {
                                    workspace.window.showMask(true);
                                    Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/project/projects/action/send-report', Gikam.getJsonWrapper({
                                        projectNo : selections[0].projNo
                                    }, [ 'coreFileServiceImpl', rows ], [ 'emcProjectServiceImpl', selections ])).done(function() {
                                        Gikam.alert('EMC.MODULE.BP.PROJECT.TIP.SEND_REPORT_SUCCESS');
                                    }).always(function() {
                                        workspace.window.closeMask();
                                    });
                                });
                            }
                        });
                    }
                }, {
                    type : 'button',
                    text : '业务流程图',
                    icon : 'browse',
                    onClick : function() {
                        let selections = Gikam.getComp('emc-project-query-edit-list-grid').getSelections();
                        if (selections.length !== 1) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                            return;
                        }

                        Gikam.create('modal', {
                            title : '业务流程图',
                            width : '90%',
                            height : '90%',
                            url : IFM_CONTEXT + '/secure/emc/module/sa/project-queries/page/business-diagram' + Gikam.param({
                                projId : selections[0].id
                            })
                        });
                    }
                }, {
                    type : 'button',
                    text : '修复送样数据',
                    icon : 'browse',
                    onClick : function() {
                        let selections = Gikam.getComp('emc-project-query-edit-list-grid').getSelections();
                        let targetSelections =  selections.filter(item => item.sample !== '1');
                        if (Gikam.isEmpty(targetSelections)) {
                            Gikam.alert('请选择送样任务');
                            return;
                        }

                        Gikam.confirm('EMC.MODULE.BP.PROJECT.TITLE.CONFIRM_OPERATE', '是否确定修复数据？', function() {
                            workspace.window.showMask(true);
                            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/project/projects/action/repair', Gikam.getJsonWrapper(null, [ 'emcProjectServiceImpl', targetSelections ])).done(function() {
                                Gikam.alert('数据修复成功');
                            }).always(function() {
                                workspace.window.closeMask();
                            });
                        });
                    }
                } ]
            }, ]
        }
    },

    onProjectActive : function(row) {
        var _this = this;
        if (row) {
            var tab = Gikam.getComp('emc-project-query-edit-list-base-info-tab');
            if (row.sample === "1") {
                // 采样任务，展示监测方案、采样、采样单页签，隐藏样品登记页签
                tab.revertPanel('emc-project-query-edit-list-proj-scheme-panel');
                tab.revertPanel('emc-project-query-edit-list-sampling-panel');
                tab.revertPanel('emc-project-query-edit-list-sample-panel');
                tab.removePanel('emc-project-query-edit-list-order-registration-panel');
                tab.revertPanel('emc-project-query-subpackage-info-panel');
                tab.revertPanel('emc-project-query-sample-subpackage-info-panel');

            } else {
                // 送样任务，展示样品登记页签，隐藏监测方案、采样、采样单页签
                tab.revertPanel('emc-project-query-edit-list-order-registration-panel');
                tab.removePanel('emc-project-query-edit-list-proj-scheme-panel');
                tab.removePanel('emc-project-query-edit-list-sampling-panel');
                tab.removePanel('emc-project-query-edit-list-sample-panel');
                tab.revertPanel('emc-project-query-subpackage-info-panel');
                tab.removePanel('emc-project-query-sample-subpackage-info-panel');
            }
            // 刷新基本信息
            workspace.window.showMask(true);
            Gikam.post(Gikam.printf(emcProjectQuery.projectUrl + '/{id}/detail', {
                id : row.id
            }), Gikam.getJsonWrapper({
                f : {
                    targetType : 'modifyProjectInfo',
                }
            })).done(function (data) {
                Gikam.getAsyncComp('emc-project-query-edit-list-base-info-form').done(function (form) {
                    // 先清除原有数据，再加载新数据，可以防止某些无值字段仍然显示旧数据的字段值
                    form.cleanData();
                	form.loadData(data);
                });

            }).always(function () {
                workspace.window.closeMask();
            });

            //this.addOrRemoveValidator(row);
            if (row.sample === '1') {
                // 刷新监测方案
                Gikam.getComp('emc-project-query-edit-list-proj-scheme-grid').refresh({
                    url : IFM_CONTEXT + '/secure/emc/module/bp/project/proj-schemes/queries',
                    requestData : {
                        projId_SEQ : row.id
                    },
                });

                // 刷新采样
                Gikam.getComp('emc-project-query-sampling-grid').refresh({
                    url : IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/queries',
                    requestData : {
                        projId_SEQ : row.id
                    },
                });

                Gikam.getComp('emc-project-query-sampling-order-grid').refresh({
                    url : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/queries',
                    requestData : {
                        projId_SEQ : row.id
                    },
                });

                // 刷新采样单
                Gikam.getComp('emc-project-query-edit-list-sample-grid').refresh({
                    url : Gikam.printf(emcProjectQuery.sampleUrl + '/queries/all', {
                        id : row.id
                    }),
                    requestData : {
                        projId_SEQ : row.id
                    }
                });

                Gikam.getAsyncComp('emc-project-query-list-sample-subpackage-info-grid').done(function(grid) {
                    grid.setOptions({
                        requestData : {
                            projId_SEQ : row.id,
                            subpackageType_SEQ : 'create',
                            orgId_SB: workspace.user.ext$.parentorgid
                        }
                    });
                    grid.refreshColumns(emcProjectQuery.getProjectSubpackageListColumns());
                    grid.refresh({
                        url : IFM_CONTEXT + "/secure/emc/module/bp/subpackages/queries",
                    });
                });

            } else {
                // 刷新样品登记
                Gikam.getComp('emc-project-query-edit-list-order-registration-grid').refresh({
                    url : Gikam.printf(emcProjectQuery.projectUrl + '/{id}/orders/queries', {
                        id : row.id
                    })
                });

                Gikam.getAsyncComp('emc-proj-query-subpackage-info-grid').done(function(grid) {
                    grid.setOptions({
                        requestData : {
                            projId_SEQ : row.id,
                            subpackageType_SEQ : 'create',
                            orgId_SB: workspace.user.ext$.parentorgid
                        }
                    });
                    grid.refreshColumns(emcProjectQuery.getProjectSubpackageListColumns());
                    grid.refresh({
                        url : IFM_CONTEXT + "/secure/emc/module/bp/subpackages/queries",
                    });
                });

            }

            // 刷新附件
            Gikam.getComp('emc-project-query-edit-list-uploader').setOptions({
                bizId : row.id,
            });

            // 刷新报告数据
            Gikam.getComp('emc-project-query-edit-list-report-grid').refresh({
                url : emcProjectQuery.reportUrl + '/queries',
                requestData : {
                    projId_SEQ : row.id
                }
            });
            // 刷新报告数据
            var isDone = _this.param.isDone;
            if(isDone == '0'){
                //获取项目的报告状态，只展示归档的
                Gikam.getComp('emc-project-query-edit-list-report-info-grid').refresh({
                    url : emcProjectQuery.reportUrl + '/queries',
                    requestData : {
                        projId_SEQ : row.id
                    }
                });
            }else{
                Gikam.getComp('emc-project-query-edit-list-report-info-grid').refresh({
                    url : emcProjectQuery.reportUrl + '/queries',
                    requestData : {
                        projId_SEQ : row.id,
                        status_SEQ : 'reportArchiving'
                    }
                });
            }


            Gikam.getAsyncComp('emc-project-query-edit-list-audit-record-grid').done(function(grid) {
                grid.refresh({
                    url : IFM_CONTEXT + '/secure/emc/module/bp/audit-records/queries',
                    requestData : {
                        projId_SEQ : row.id
                    }
                });
            });

            // 查询合同附件
            Gikam.getAsyncComp('emc-project-query-edit-list-contract-uploader').done(function (uploader) {
                Gikam.post(IFM_CONTEXT + '/secure/emc/module/mdm/crm/contracts/queries', Gikam.getJsonWrapper({
                    f : {
                        contractNo_SEQ : row.ext$.contractno
                    }
                }, [])).done(function (data) {
                    if (!Gikam.isEmpty(data.rows)) {
                        let targetIds = ['T_CRM_OPPORTUNITY_CONTRACT$' + data.rows[0].id, 'T_CRM_OPPORTUNITY_MAIN$' + data.rows[0].opportunityId];
                        uploader.grid.refresh({
                            requestData : {
                                targetId : '',
                                targetId_IN : targetIds.join()
                            },
                            url : uploader.options.searchUrl
                        })
                    }
                });
            });
        }
    },

    addOrRemoveValidator : function(project) {
        Gikam.getAsyncComp('emc-project-query-edit-list-base-info-form').done(function (form) {
            if (project.sample === '1') {
                form.addFieldValidator('ext$.samplinglocation', ['notEmpty']);
                form.addFieldValidator('ext$.inspectedcontact', ['notEmpty']);
                form.addFieldValidator('ext$.inspectedphone', ['notEmpty']);
                form.hideFields(['sendUsername', 'sendDate']);
            } else {
                form.removeFieldValidator('ext$.samplinglocation', ['notEmpty']);
                form.removeFieldValidator('ext$.inspectedcontact', ['notEmpty']);
                form.removeFieldValidator('ext$.inspectedphone', ['notEmpty']);
                form.showFields(['sendUsername', 'sendDate']);
            }
        })
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            id : 'emc-project-query-edit-list-base-info-tab',
            panels : [ {
                        // 基本信息
                        title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.BASE_INFO',
                        id : 'emc-project-query-edit-list-base-info-panel',
                        items : [ {
                            type : 'layout',
                            north : {
                                height : '60%',
                                items : [ {
                                    type : 'form',
                                    columns : 3,
                                    readonly : true,
                                    id : 'emc-project-query-edit-list-base-info-form',
                                    fields : emcProjectQuery.getBaseInfoFormFields(),
                                    onLoadSuccess : function(data) {
                                        Gikam.refreshGrids({
                                            'emc-project-query-edit-list-director-grid' : '/secure/emc/module/bp/project/projects/{id}/directors/queries',
                                        }, {
                                            id : data.id
                                        });
                                    }
                                } ]
                            },
                            center : {
                                items : [ {
                                    type : 'tab',
                                    id : 'emc-project-query-edit-list-director-tab',
                                    fill : true,
                                    width : Gikam.width.XL,
                                    panels : [ {
                                        id : 'emc-project-query-edit-list-director-panel',
                                        title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TITLE.PROJECT_DIRECTOR',
                                        items : [ {
                                            type : 'grid',
                                            id : 'emc-project-query-edit-list-director-grid',
                                            filterOpen : false,
                                            service : 'emcProjectDirectorServiceImpl',
                                            page : false,
                                            readonly : true,
                                            columns : emcProjectQuery.getProjectDirectorListColumns()
                                        } ]
                                    } ]
                                } ]
                            }
                        }, ]
                    },
                    _this.getSchemePanel(),
                    _this.getOrderRegistrationPanel(),
                    _this.getSamplingPanel(),
                    {
                        // 采样单
                        title : 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TAB.SAMPLE_NAME',
                        id : 'emc-project-query-edit-list-sample-panel',
                        show : false,
                        items : [ {
                            type : 'tab',
                            fill : true,
                            panels : [
                                    {
                                        title : 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TAB.SAMPLE_NAME',
                                        items : [ {
                                            type : 'layout',
                                            north : {
                                                height : '50%',
                                                items : [ {
                                                    type : 'grid',
                                                    id : 'emc-project-query-edit-list-sample-grid',
                                                    service : 'emcSampleServiceImpl',
                                                    columns : emcProjectQuery.getProjectSampleColumns(),
                                                    onLoadSuccess : function(data) {
                                                        if (!Gikam.isEmpty(data)) {
                                                            this.activeRowByIndex(0);
                                                        } else {
                                                            Gikam.cleanCompData([ 'emc-project-query-edit-list-data-input-order-task-grid', 'emc-project-query-edit-list-data-input-container-info-grid', 'emc-project-query-data-input-detail-list-grid',
                                                                    'emc-project-query-edit-list-order-task-result-grid', 'emc-project-queryt-edit-list-order-task-equip-grid' ]);
                                                        }
                                                    },
                                                    onRowActive : function(index, row) {
                                                        _this.onSampleActive(row);
                                                    },
                                                } ]
                                            },
                                            center : {
                                                items : [ {
                                                    type : 'layout',
                                                    west : {
                                                        width : '50%',
                                                        items : [ {
                                                            type : 'grid',
                                                            id : 'emc-project-query-edit-list-data-input-order-task-grid',
                                                            service : 'emcOrderServiceImpl',
                                                            columns : emcProjectQuery.getSampleTestColumns(),
                                                            deleteFormatter : function(row) {
                                                                return row.orderNo || row.id;
                                                            },
                                                            onRowActive : function(index, row) {
                                                                _this.onSampleOrderActive(row);
                                                            },
                                                            onLoadSuccess : function(data) {
                                                                if (!Gikam.isEmpty(data)) {
                                                                    this.activeRowByIndex(0);
                                                                } else {
                                                                    Gikam.cleanCompData([ 'emc-project-query-edit-list-data-input-container-info-grid' ]);
                                                                }
                                                            },
                                                            rowStyleFormatter : function(row) {
                                                                if (row.termination == '1') {
                                                                    return 'background-color : #FFFF99';
                                                                }
                                                            },
                                                        } ]
                                                    },
                                                    center : {
                                                        items : [ {
                                                            type : 'grid',
                                                            id : 'emc-project-query-edit-list-data-input-container-info-grid',
                                                            service : 'emcOrderTaskServiceImpl',
                                                            columns : emcProjectQuery.getSamplePrepareOrderContainerColumns(),
                                                            onLoadSuccess : function(data) {
                                                                if (!Gikam.isEmpty(data)) {
                                                                    this.activeRowByIndex(0);
                                                                }
                                                            },
                                                        } ]
                                                    }
                                                } ]
                                            }
                                        } ]
                                    }, {
                                        // 现场数据录入
                                        title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.DATA_INPUT',
                                        items : [ {
                                            type : 'layout',
                                            north : {
                                                height : '50%',
                                                items : [ {
                                                    type : 'grid',
                                                    id : 'emc-project-query-data-input-detail-list-grid',
                                                    columns : emcProjectQuery.getDataInputColumns(),
                                                    readonly : true,
                                                    onLoadSuccess : function(data) {
                                                        if (!Gikam.isEmpty(data)) {
                                                            this.activeRowByIndex(0);
                                                        }
                                                    },
                                                    onRowActive : function(index, row) {
                                                        _this.onDataInputActive(row)
                                                    },
                                                } ]
                                            },
                                            center : {
                                                items : [ {
                                                    type : 'tab',
                                                    fill : true,
                                                    panels : [ {
                                                        title : 'EMC.MODULE.BP.BP.FOLDERS.ORDERS.MODAL.RESULT',
                                                        items : [ {
                                                            type : 'layout',
                                                            center : {
                                                                items : [ {
                                                                    type : 'grid',
                                                                    id : 'emc-project-query-edit-list-order-task-result-grid',
                                                                    service : 'emcOrderTaskResultServiceImpl',
                                                                    columns : emcProjectQuery.getOrderTaskResultColumns(),
                                                                    readonly : true,
                                                                    onLoadSuccess : function(data) {
                                                                        if (!Gikam.isEmpty(data)) {
                                                                            this.activeRowByIndex(1);
                                                                        }
                                                                    },
                                                                    onRendered : function() {
                                                                        var formData = Gikam.getComp('emc-order-task-result-edit-list-button-group-by-test');
                                                                        if (formData != undefined) {
                                                                            formData.clickHandle();
                                                                        }
                                                                    },
                                                                } ]
                                                            }
                                                        } ]
                                                    }, {
                                                        title : 'EMC.MODULE.BP.BP.FOLDERS.ORDERS.TAB.APPOINT_EQUIPT.BASE_INFO',
                                                        items : [ {
                                                            type : 'layout',
                                                            center : {
                                                                items : [ {
                                                                    type : 'grid',
                                                                    id : 'emc-project-queryt-edit-list-order-task-equip-grid',
                                                                    service : 'emcOrderTaskServiceImpl',
                                                                    columns : emcProjectQuery.getOrderTaskEquiptColumns(),
                                                                    readonly : true,
                                                                } ]
                                                            }
                                                        } ]
                                                    } ]
                                                } ]
                                            }
                                        } ]
                                    } ]
                        } ]
                    }, {
                        // 项目附件
                        title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.FILE',
                        items : [ {
                            type : 'uploader',
                            id : 'emc-project-query-edit-list-uploader',
                            dbTable : 'T_EMC_PROJECT',
                            readonly : true
                        } ]
                    }, {
                        title : '合同附件',
                        items : [ {
                            type : 'uploader',
                            id : 'emc-project-query-edit-list-contract-uploader',
                            dbTable : 'T_CRM_OPPORTUNITY_CONTRACT',
                            readonly : true,
                        } ]
                    }, {
                        // 报告数据
                        title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.REPORT_DATA',
                        items : [ {
                            type : 'layout',
                            north : {
                                height : '50%',
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-project-query-edit-list-report-grid',
                                    service : 'emcReportServiceImpl',
                                    columns : emcProjectQuery.getReportListColumns(),
                                    onLoadSuccess : function(data) {
                                        if (!Gikam.isEmpty(data)) {
                                            this.activeRowByIndex(0);
                                        } else {
                                            Gikam.cleanCompData([ 'emc-project-query-edit-list-folder-grid', 'emc-project-query-edit-list-order-grid', 'emc-project-query-edit-list-audit-record-grid' ]);
                                            var uploader = Gikam.getComp('emc-project-query-edit-list-report-uploader');
                                            if (uploader && uploader.grid.model) {
                                                uploader.setOptions({
                                                    bizId : '',
                                                });
                                                uploader.grid.cleanData();
                                            }
                                        }
                                    },
                                    onRowActive : function(index, row) {
                                        _this.onReportActive(row);
                                    },
                                } ]
                            },
                            center : {
                                items : [ {
                                    type : 'tab',
                                    fill : true,
                                    panels : [ {
                                        title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.FOLDER_INFO',
                                        items : [ {
                                            type : 'layout',
                                            center : {
                                                items : [ {
                                                    type : 'grid',
                                                    id : 'emc-project-query-edit-list-folder-grid',
                                                    columns : emcProjectQuery.getFolderEditListColumns(),
                                                    toolbar : [ {
                                                        type : 'button',
                                                        text : 'GIKAM.BUTTON.REVIEW',
                                                        icon : 'browse',
                                                        onClick : function() {
                                                            _this.review();
                                                        }
                                                    } ]
                                                } ]
                                            }
                                        } ]
                                    }, {
                                        title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.ORDER_INFO',
                                        items : [ {
                                            type : 'layout',
                                            center : {
                                                items : [ {
                                                    type : 'grid',
                                                    id : 'emc-project-query-edit-list-order-grid',
                                                    columns : emcProjectQuery.getOrderEditListColumns()
                                                } ]
                                            }
                                        } ]
                                    }, {
                                        title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.REPORT_FILE',
                                        items : [ {
                                            type : 'layout',
                                            center : {
                                                items : [ {
                                                    type : 'uploader',
                                                    readonly : true,
                                                    id : 'emc-project-query-edit-list-report-uploader',
                                                    dbTable : 'T_EMC_REPORT'
                                                } ]
                                            }
                                        } ]
                                    } ]
                                } ]
                            }
                        } ]
                    }, {
                        // 审核记录
                        title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.AUDIT_RECORD',
                        items : [ {
                            type : 'grid',
                            id : 'emc-project-query-edit-list-audit-record-grid',
                            columns : emcProjectQuery.getAuditRecordListColumns(),
                            page : true,
                            group : {
                                groupCheckbox : false,
                                fields : [ 'auditCategory' ],
                                formatter : function(row) {
                                    return row.stepCode + '、' + row.auditCategory;
                                }
                            }
                        } ]
                    }, {
                        // 查询结果
                        title : 'EMC.MODULE.SA.PROJECTQUERIES.TAB.QUERYRESULT',
                        items : [ {
                            type : 'layout',
                            north : {
                                height : '50%',
                                items : [ {
                                    type : 'layout',
                                    west : {
                                        width : '50%',
                                        items : [ {
                                            type : 'grid',
                                            readonly : true,
                                            id : 'emc-project-query-edit-list-report-info-grid',
                                            columns : emcProjectQuery.getReportListColumns(),
                                            onLoadSuccess : function(data) {
                                                if (!Gikam.isEmpty(data)) {
                                                    this.activeRowByIndex(0);
                                                } else {
                                                    Gikam.cleanCompData([ 'emc-project-query-edit-list-order-info-grid' ]);
                                                }
                                            },
                                            onRowActive : function(index, row) {
                                                _this.onReportInfoActive(row);
                                            },

                                        } ]
                                    },
                                    center : {
                                        items : [ {
                                            type : 'grid',
                                            readonly : true,
                                            id : 'emc-project-query-edit-list-order-info-grid',
                                            columns : emcProjectQuery.getOrderInfoColumns(),
                                            onLoadSuccess : function(data) {
                                                if (!Gikam.isEmpty(data)) {
                                                    this.activeRowByIndex(0);
                                                } else {
                                                    Gikam.cleanCompData([ 'emc-project-query-edit-list-order-task-info-grid' ]);
                                                }
                                            },
                                            onRowActive : function(index, row) {
                                                _this.onOrderInfoActive(row);
                                            },
                                        } ]
                                    }
                                } ]
                            },
                            center : {
                                items : [ {
                                    type : 'layout',
                                    west : {
                                        width : '50%',
                                        items : [ {
                                            type : 'grid',
                                            readonly : true,
                                            id : 'emc-project-query-edit-list-order-task-info-grid',
                                            columns : emcProjectQuery.getOrderTaskInfoColumns(),
                                            requestData : {
                                                TESTCAPCITYORGID_SEQ: workspace.user.ext$.parentorgid,
                                                virtual_IN: ','
                                            },
                                            onLoadSuccess : function(data) {
                                                if (!Gikam.isEmpty(data)) {
                                                    this.activeRowByIndex(0);
                                                } else {
                                                    Gikam.cleanCompData([ 'emc-project-query-edit-list-order-task-result-info-grid' ]);
                                                }
                                            },
                                            onRowActive : function(index, row) {
                                                _this.onOrderTaskInfoActive(row);
                                            },
                                        } ]
                                    },
                                    center : {
                                        items : [ {
                                            type : 'grid',
                                            readonly : true,
                                            id : 'emc-project-query-edit-list-order-task-result-info-grid',
                                            columns : emcProjectQuery.getOrderTaskResultInfoColumns(),
                                        } ]
                                    }
                                } ]
                            }
                        } ]
                    }, {
                    title : '样品分包单管理',
                    id : 'emc-project-query-subpackage-info-panel',
                    items : [ {
                        type : 'layout',
                        north : {
                            height : '50%',
                            items : [ {
                                type : 'layout',
                                west : {
                                    width : '50%',
                                    items : [ {
                                        type : 'grid',
                                        readonly : true,
                                        id : 'emc-proj-query-subpackage-info-grid',
                                        columns : emcProjectQuery.getProjectSubpackageListColumns(),
                                        requestData : {
                                            subpackageType_SEQ : 'create',
                                            orgId_SB: workspace.user.ext$.parentorgid
                                        },
                                        service : 'emcSubpackageServiceImpl',
                                        toolbar : _this.getSubpackageButton(),
                                        onLoadSuccess : function(data) {
                                            if (!Gikam.isEmpty(data)) {
                                                this.activeRowByIndex(0);
                                            } else {
                                                Gikam.cleanCompData([ 'emc-project-query-list-subpackage-order-grid' ]);
                                                Gikam.cleanCompData([ 'emc-subpackage-query-list-base-info-form' ]);

                                            }
                                        },
                                        onRowActive : function(index, row) {
                                            _this.onQuSubpackageActive(row);
                                        },
                                    } ]
                                },
                                center : {
                                    items : [ {
                                        type : 'layout',
                                        north : {
                                            height : '80%',
                                            items : [ {
                                                type : 'form',
                                                columns : 3,
                                                service : 'emcSubpackageServiceImpl',
                                                id : 'emc-subpackage-query-list-base-info-form',
                                                fields : emcProjectQuery.getSubpackageQueryBaseInfoFormFields(),
                                                onUpdated : function() {
                                                    var grid = Gikam.getComp('emc-proj-query-subpackage-info-grid');
                                                    grid.refreshRowById(grid.getActivedRow().id);
                                                },
                                            } ]
                                        },
                                    } ]
                                }

                            } ]
                        },
                        center : {
                            items : [ {
                                type : 'layout',
                                west : {
                                    width : '50%',
                                    items : [ {
                                        type : 'grid',
                                        readonly : true,
                                        id : 'emc-project-query-list-subpackage-order-grid',
                                        columns : emcProjectQuery.getSubpackageOrderColumns(),
                                        service : 'emcProjectServiceImpl',
                                        requestData: {
                                            subpackageflag_SEQ : '1'
                                        },
                                        onLoadSuccess : function(data) {
                                            if (!Gikam.isEmpty(data)) {
                                                this.activeRowByIndex(0);
                                            } else {
                                                Gikam.cleanCompData([ 'emc-project-query-list-subpackage-order-task-grid' ]);
                                            }
                                        },
                                        onRowActive : function(index, row) {
                                            _this.onSubpackageOrderInfoActive(row);
                                        },
                                    } ]
                                },
                                center : {
                                    items : [ {
                                        type : 'grid',
                                        readonly : true,
                                        id : 'emc-project-query-list-subpackage-order-task-grid',
                                        columns : emcProjectQuery.getSubpackageOrderTaskColumns(),
                                        service : 'emcSubpackageTaskServiceImpl',
                                    } ]
                                }
                            } ]
                        }
                    } ]
                }, {
                    title : '采样前分包单管理',
                    id : 'emc-project-query-sample-subpackage-info-panel',
                    items : [ {
                        type : 'layout',
                        north : {
                            height : '50%',
                            items : [ {
                                type : 'layout',
                                west : {
                                    width : '50%',
                                    items : [ {
                                        type : 'grid',
                                        readonly : true,
                                        id : 'emc-project-query-list-sample-subpackage-info-grid',
                                        columns : emcProjectQuery.getProjectSubpackageListColumns(),
                                        requestData : {
                                            subpackageType_SEQ : 'create',
                                            orgId_SB: workspace.user.ext$.parentorgid
                                        },
                                        service : 'emcSubpackageServiceImpl',
                                        toolbar : _this.getSampleSubpackageButton(),
                                        onLoadSuccess : function(data) {
                                            if (!Gikam.isEmpty(data)) {
                                                this.activeRowByIndex(0);
                                            } else {
                                                Gikam.cleanCompData([ 'emc-project-query-list-subpackage-folder-grid' ]);
                                                Gikam.cleanCompData([ 'emc-subpackage-query-sample-list-base-info-form' ]);

                                            }
                                        },
                                        onRowActive : function(index, row) {
                                            _this.onQuSampleSubpackageActive(row);
                                        },
                                    } ]
                                },
                                center : {
                                    items : [ {
                                        type : 'layout',
                                        north : {
                                            height : '80%',
                                            items : [ {
                                                type : 'form',
                                                columns : 3,
                                                service : 'emcSubpackageServiceImpl',
                                                id : 'emc-subpackage-query-sample-list-base-info-form',
                                                fields : emcProjectQuery.getSubpackageQueryBaseInfoFormFields(),
                                                onUpdated : function() {
                                                    var grid = Gikam.getComp('emc-project-query-list-sample-subpackage-info-grid');
                                                    grid.refreshRowById(grid.getActivedRow().id);
                                                },
                                            } ]
                                        },
                                    } ]
                                },
                            } ]
                        },
                        center : {
                            items : [ {
                                type : 'layout',
                                west : {
                                    width : '50%',
                                    items : [ {
                                        type : 'grid',
                                        readonly : true,
                                        id : 'emc-project-query-list-subpackage-folder-grid',
                                        columns : emcProjectQuery.getSubpackageFolderColumns(),
                                        service : 'emcProjSchemeServiceImpl',
                                        onLoadSuccess : function(data) {
                                            if (!Gikam.isEmpty(data)) {
                                                this.activeRowByIndex(0);
                                            } else {
                                                Gikam.cleanCompData([ 'emc-project-query-list-subpackage-sample-order-task-grid' ]);
                                            }
                                        },
                                        onRowActive : function(index, row) {
                                            _this.onSampleSubpackageFolderInfoActive(row);
                                        },
                                    } ]
                                },
                                center : {
                                    items : [ {
                                        type : 'grid',
                                        readonly : true,
                                        id : 'emc-project-query-list-subpackage-sample-order-task-grid',
                                        columns : emcProjectQuery.getSubpackageSampleOrderTaskColumns(),
                                        service : 'emcSubpackageTaskServiceImpl',
                                    } ]
                                }
                            } ]
                        }
                    } ]
                } ]
        }
    },

    // 监测方案
    getSchemePanel : function() {
        var _this = this
        return {
            title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.TAB.PROJ_SCHEME',
            id : 'emc-project-query-edit-list-proj-scheme-panel',
            show : false,
            items : [ {
                type : 'layout',
                north : {
                    height : '50%',
                    items : [ {
                        type : 'grid',
                        id : 'emc-project-query-edit-list-proj-scheme-grid',
                        columns : emcProjectQuery.getProjSchemeListColumns(),
                        onLoadSuccess : function(data) {
                            if (!Gikam.isEmpty(data)) {
                                this.activeRowByIndex(0);
                            } else {
                                Gikam.cleanCompData([ "emc-project-query-edit-list-proj-order-grid" ]);
                            }
                        },
                        onRowActive : function(index, row) {
                            Gikam.getComp('emc-project-query-edit-list-proj-order-grid').refresh({
                                url : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/bp/project/proj-schemes/{id}/proj-orders/queries', {
                                    id : row.id
                                })
                            });
                        },

                        toolbar : [ {
                            type : 'button',
                            text : 'GIKAM.BUTTON.PREVIEW',
                            icon : 'print',
                            onClick : function() {
                                Gikam.viewReport('samplingLabel', Gikam.getComp('emc-project-query-edit-list-proj-scheme-grid').getSelections());
                            }
                        } ]
                    } ]
                },
                center : {
                    items : [ {
                        type : 'grid',
                        id : 'emc-project-query-edit-list-proj-order-grid',// 方案分析项组
                        columns : emcProjectQuery.getProjOrderListColumns(),
                    } ]
                }
            } ]
        }
    },

    // 样品登记
    getOrderRegistrationPanel : function() {
        var _this = this;
        return {
            id : 'emc-project-query-edit-list-order-registration-panel',
            title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.ORDER',
            show : false,
            items : [ {
                type : 'layout',
                north : {
                    height : '50%',
                    items : [ {
                        type : 'grid',
                        id : 'emc-project-query-edit-list-order-registration-grid',
                        columns : emcProjectQuery.getOrderRegistrationColumns(),
                        service : 'emcOrderServiceImpl',
                        page : true,
                        readonly : true,
                        onLoadSuccess : function(data) {
                            if (!Gikam.isEmpty(data)) {
                                this.activeRowByIndex(0);
                            } else {
                                Gikam.cleanCompData([ 'emc-project-query-edit-list-order-item-group-grid' ]);
                            }
                        },
                        onRowActive : function(index, row) {
                            Gikam.getComp('emc-project-query-edit-list-order-item-group-grid').refresh({
                                url : Gikam.printf('/secure/emc/module/bp/bp/orders/{id}/item-groups/queries', {
                                    id : row.id,
                                })
                            });
                        },
                        toolbar : [ {
                            type : 'button',
                            text : 'GIKAM.BUTTON.PREVIEW',
                            icon : 'print',
                            onClick : function() {
                                Gikam.viewReport('deliveringLabel', Gikam.getComp('emc-project-query-edit-list-order-registration-grid').getSelections());
                            }
                        } ]
                    } ]
                },
                center : {
                    items : [ {
                        type : 'grid',
                        id : 'emc-project-query-edit-list-order-item-group-grid',// 样品分析项组
                        columns : emcProjectQuery.getOrderItemGroupColumns(),
                        service : 'emcOrderItemGroupServiceImpl',
                        /*requestData : {
                           orgId_SEQ : workspace.user.ext$.parentorgid,
                        },*/
                    } ]
                }
            } ]
        }
    },

    // 采样页签
    getSamplingPanel : function() {
        return {
            title : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.TAB.SAMPLING',
            id : 'emc-project-query-edit-list-sampling-panel',
            show : false,
            items : [ {
                type : 'tab',
                panels : [ {
                    title : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.TAB.ORDERGROUP',
                    items : [ {
                        type : 'grid',
                        id : 'emc-project-query-sampling-grid', // 点位信息
                        columns : emcProjectQuery.getSamplePlanEditListColumns(),
                        service : 'emcFolderServiceImpl'
                    } ]
                }, {
                    title : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.TAB.ORDER_TEST',
                    items : [ {
                        type : 'layout',
                        north : {
                            height : '50%',
                            items : [ {
                                type : 'grid',
                                id : 'emc-project-query-sampling-order-grid', // 样品信息
                                columns : emcProjectQuery.getSamplePrepareOrderColumns(),
                                service : 'emcOrderServiceImpl',
                                deleteFormatter : function(row) {
                                    return row.orderNo || row.id;
                                },
                                onLoadSuccess : function(data) {
                                    if (!Gikam.isEmpty(data)) {
                                        this.activeRowByIndex(0);
                                    } else {
                                        Gikam.cleanCompData([ "emc-project-query-sampling-order-test-grid" ]);
                                    }
                                },
                                onRowActive : function(index, row) {
                                    Gikam.getComp('emc-project-query-sampling-order-test-grid').refresh({
                                        url : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/{id}/order-tasks/queries', {
                                            id : row.id
                                        })
                                    });
                                },
                            } ]
                        },
                        center : {
                            items : [ {
                                type : 'grid',
                                id : 'emc-project-query-sampling-order-test-grid', // 样品测试
                                columns : emcProjectQuery.getSamplePrepareOrderContainerColumns(),
                                service : 'emcOrderTaskServiceImpl',
                            } ]
                        }
                    } ]
                } ]
            } ]
        }
    },

    onOrderActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-project-query-edit-list-order-item-group-grid').refresh({
                url : Gikam.printf('/secure/emc/module/bp/bp/orders/{id}/item-groups/queries', {
                    id : row.id
                })
            });
        }
    },

    onSampleActive : function(row) {
        var _this = this;
        if (row) {
            // 刷新采样单
            Gikam.getComp('emc-project-query-edit-list-data-input-order-task-grid').refresh({
                url : '/secure/emc/module/bp/bp/orders/queries',
                requestData : {
                    sampleId_SEQ : row.id,
                }
            });
            // 刷新现场数据录入
            Gikam.getComp('emc-project-query-data-input-detail-list-grid').refresh({
                url : '/secure/emc/module/bp/bp/folders/queries',
                requestData : {
                    target_filter : JSON.stringify({
                        type : 'assigned',
                        targetTable : "T_EMC_ORDER",
                        targetMatchColumn : "FOLDERID",
                        filter : [ {
                            targetFilterColumn : 'sampleId',
                            targetFilterValue : row.id
                        } ]
                    })
                },
            });

        }
    },

    onSampleOrderActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-project-query-edit-list-data-input-container-info-grid').refresh({
                url : Gikam.printf('/secure/emc/module/bp/bp/orders/{id}/order-tasks/queries', {
                    id : row.id
                })
            });
        }
    },

    onReportInfoActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-project-query-edit-list-order-info-grid').refresh({
                url : '/secure/emc/module/bp/bp/orders/queries',
                requestData : {
                    projId_SEQ : row.projId,
                    monitorTmplId_SEQ : row.monitorTmplId,
                }
            });
        }
    },

    onOrderInfoActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-project-query-edit-list-order-task-info-grid').refresh({
                url : '/secure/emc/module/bp/bp/order-tasks/queries',
                requestData : {
                    projId_SEQ : row.projId,
                    orderId_SEQ : row.id,
                    TESTCAPCITYORGID_SEQ : workspace.user.ext$.parentorgid,
                    virtual_IN: ','
                },
                order : {
                    testId : 'asc'
                }
            });
        }
    },

    onOrderTaskInfoActive : function(row) {
        var _this = this;
        if (row) {
            var isDone = _this.param.isDone;
            if(isDone == '1'){
                //获取项目的报告状态，只展示报告中显示的
                Gikam.getComp('emc-project-query-edit-list-order-task-result-info-grid').refresh({
                    url : '/secure/emc/module/bp/bp/order-task-results/queries',
                    requestData : {
                        projId_SEQ : row.projId,
                        orderId_SEQ : row.orderId,
                        orderTaskId_SEQ : row.id,
                        reportDisplay_SEQ : '1',
                    }
                });
            }else{
                Gikam.getComp('emc-project-query-edit-list-order-task-result-info-grid').refresh({
                    url : '/secure/emc/module/bp/bp/order-task-results/queries',
                    requestData : {
                        projId_SEQ : row.projId,
                        orderId_SEQ : row.orderId,
                        orderTaskId_SEQ : row.id,
                    }
                });
            }


        }
    },

    onDataInputActive : function(row) {
        var sampleId = Gikam.getComp('emc-project-query-edit-list-sample-grid').getActivedRow().id;
        var _this = this;
        if (row) {
            Gikam.getComp('emc-project-query-edit-list-order-task-result-grid').refresh({
                url : '/secure/emc/module/bp/bp/orders/order-task-results',
                requestData : {
                    folderId_SEQ : row.id,
                    analysisType_SEQ : 'sampling',
                    target_filter : JSON.stringify({
                        type : 'assigned',
                        targetTable : "T_EMC_ORDER",
                        targetMatchColumn : "ID",
                        thisMatchColumn : "ORDERID",
                        filter : [ {
                            targetFilterColumn : 'sampleId',
                            targetFilterValue : sampleId
                        } ]
                    })
                }
            });
            Gikam.getComp('emc-project-queryt-edit-list-order-task-equip-grid').refresh({
                url : '/secure/emc/module/bp/bp/orders/order-tasks',
                requestData : {
                    sampleId_SEQ : sampleId,
                    analysisType_SEQ : 'sampling',
                    folderId_SEQ : row.id,
                }
            });
        }
    },

    onReportActive : function(row) {
        Gikam.getComp('emc-project-query-edit-list-report-uploader').setOptions({
            bizId : row.projId,
        });
        Gikam.getAsyncComp('emc-project-query-edit-list-folder-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/queries',
                requestData : {
                    projId_SEQ : row.projId
                }
            });
        });
        Gikam.getAsyncComp('emc-project-query-edit-list-order-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/queries',
                requestData : {
                    projId_SEQ : row.projId,
                    monitorTmplId_SEQ : row.monitorTmplId
                }
            });
        });

    },

    loadSamplePropertyPage : function(id, tmplId, monitorTmplId) {
        // 查询 sample 的时候，如果存在样品，则需要更新 采样单的 monitorTmplId，并插入数据到 property data 表
        Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/tmpls/' + id + '/judge-samples/' + monitorTmplId).done(function() {
            Gikam.getComp('emc-sample-edit-list-project-sample-grid').refresh();
        });
        // 打开采样单属性的时候，看 模板 id 是否存在，没有就要提示进行添加；有的话要判断是否添加过
        if (Gikam.isEmpty(monitorTmplId)) {
            Gikam.alert("EMC.MODULE.BP.REPORT.REPORTS.TIP.UNSET_SAMPLE_PROPERTY");
            return;
        }

        var _this = this;
        Gikam.create('modal', {
            width : '60%',
            bodyStyle : {
                padding : '0px 20px'
            },
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.DETAIL_INFO',
            url : emcProjectQuery.sampleUrl + '/page/info-detail' + Gikam.param({
                id : id,
                tmplId : tmplId,
                monitorTmplId : monitorTmplId,
                readonly : true
            }),
        });
    },

    review : function() {

        var selections = Gikam.getComp('emc-project-query-edit-list-folder-grid').getSelections();

        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var folderIds = '';
        selections.forEach(function(item) {
            folderIds += item.id + ',';
        });
        folderIds = folderIds.substring(0, folderIds.length - 1);

        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.BP.FOLDERS.TITLE.REVIEW',
            url : '/secure/emc/module/bp/bp/folders/page/review' + Gikam.param({
                folderId : folderIds,
            }),
            onAfterClose : function(rows) {
                Gikam.getComp('emc-project-query-edit-list-folder-grid').refresh();
            }
        });

    },

    completeProject : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-project-query-edit-list-grid').getSelections();
        var id = selections[0].id;
        var projNo = selections[0].projNo;
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
    	if (selections.length >= 2) {
            Gikam.alert('GIKAM.TIP.CHOOSE_MAX_ITEM_IS_ONE');
            return;
    	}
        var modal = Gikam.create('modal', {
            title: "EMC.MODULE.BP.REPORT.REPORTS.TITLE.COMPLETE_RECORD",
            width : 400,
            height : 270,
        });
        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type: 'btnToolbar',
                    items: [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        icon : 'select',
                        onRendered : function() {
                            var selections = Gikam.getComp('emc-project-query-edit-list-grid').getSelections();
                            var id = selections[0].id;
                            var projNo = selections[0].projNo;
        	                var completeDate;
        	                var deliveryDate;
                            var url = Gikam.printf(emcProjectQuery.reportUrl+'/select-complete-date');
                            Gikam.postText(url, Gikam.getJsonWrapper({
                            	 f : {
                             		projId: id,
                             }
                            })).done(function(data) {
                            	var date = data.split("+");
                            	deliveryDate = date[0];
                            	completeDate = date[1];
                            	var  nowDate = Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd');
                                form.setData({
                                	reportNo : projNo,
    	                            },false);
                            	if(deliveryDate!=null && deliveryDate!=''&&deliveryDate!='undefined'){
                                    form.setData({
        	                            ext$ : {
        	                        		deliverydate : deliveryDate,
        	                				completedate : completeDate
        	                				}
        	                            },false);
                            	}else{
                                    form.setData({
        	                            ext$ : {
        	                        		deliverydate : nowDate,
        	                				completedate : nowDate
        	                				}
        	                            },false);
                            	}

                            }).always(function() {});
                   	    },
                        onClick : function() {
                            if (form.validate()) {

                                var formData = form.getData();
                                Gikam.getLastModal().window.showMask(true);
            	                var url = emcProjectQuery.reportUrl+'/complete-report';
            	                var completedate = formData.ext$.completedate;
            	                var deliverydate = formData.ext$.deliverydate;
            	                Gikam.postText(url, Gikam.getJsonWrapper({
            	                    f : {
            	                    		projId: id,
	            	                    	completedate : completedate,
	            	                    	deliverydate : deliverydate

            	                    }
            	                })).done(function(data) {
            	                	var isDone = data;//已经更新报告状态为done
            	                	if(isDone == 'done'){
            	                		Gikam.alert('EMC.MODULE.BP.REPORT.REPORTS.ISDONE');
            	                	}else{
             	                	    Gikam.alert('EMC.MODULE.BP.REPORT.REPORTS.DONE');
            	                	}

                             }).always(function()
            	                {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close();
                                    Gikam.getComp('emc-project-query-edit-list-grid').refresh();
            	                });

                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
                        icon : 'cancel',
                        onClick : function () {
                            modal.close();
                        }
                    } ]
                }]
            }
        });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            columns: 1,
            titleWidth: 80,
            fields: emcProjectQuery.getProjectCompleteColums(),
            autoSave: false,

        });

    },

    cancelCompleteProject : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-project-query-edit-list-grid').getSelections();
        var id = selections[0].id;
        var projNo = selections[0].projNo;
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        if (selections.length >= 2) {
            Gikam.alert('GIKAM.TIP.CHOOSE_MAX_ITEM_IS_ONE');
            return;
        }
        var url = Gikam.printf(emcProjectQuery.reportUrl+'/cancel-complete-report');
        Gikam.postText(url, Gikam.getJsonWrapper({
            f : {
                projId: id,
            }
        })).done(function(data) {
            if(data == 'failed'){
                Gikam.alert('此项目在CRM系统发票未作废或到款认领未删除,不能取消完成');
            }else{
                Gikam.alert('撤销成功');
            }
        })

    },


    viewExportSampleFile : function() {
        var _this = this
        var rows = Gikam.getComp('emc-project-query-edit-list-grid').getSelections();
        let pending = rows.some(item => item.ext$.datastatus === '1');
        if (pending) {
            Gikam.alert('业务数据未生成，无法查看报表');
            return;
        }

        if (rows && rows.length === 1) {
            Gikam.create('modal', {
                width : 800,
                height : 500,
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ANNEX',
                url : emcProjectQuery.projectUrl + '/page/project-annex-list?id=' + rows[0].id
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    exportSampleFile : function() {
        var _this = this
        var rows = Gikam.getComp('emc-project-query-edit-list-grid').getSelections();
        let pending = rows.some(item => item.ext$.datastatus === '1');
        if (pending) {
            Gikam.alert('业务数据未生成，无法导出报表');
            return;
        }

        if (rows && rows.length === 1) {
            Gikam.create('modal', {
                width : 800,
                height : 500,
                url : Gikam.choose.getConfig('reportLine').url+ Gikam.param({
                	REPORTCONFIGID_IN : 'xianchanglianxi',
                	activatedFlag_SEQ :'1'
                }),
                title : '报表',
                onAfterClose : function(data) {
                    var url1 = Gikam.printf(emcProjectQuery.projectUrl+'/action/sample-contract-file');
                    var reportConfigId = data.reportConfigId;
                    Gikam.postText(url1, Gikam.getJsonWrapper(null,['coreReportConfigLineService',data] ,[ 'pfProjectService', rows ])).done(function(data){
                        data = data.replace(/\s+/g,"");
                    	var fileIds = [];
                        var selections = data.split(",");
                        var ro = selections.filter(function (s) {
                            return s && s.trim();
                        });
                        if (ro.length > 0) {
                        	ro.forEach(function (item) {
                                fileIds.push({id : item})
                            })
                            if(fileIds.length>1){
                                Gikam.postText('/core/module/item/files/action/pack',Gikam.getJsonWrapper({}, [ '', fileIds])
                                ).done(function (fileUrl) {
                                    if (fileUrl !== '' && fileUrl !== undefined) {
                                        Gikam.download(IFM_CONTEXT + fileUrl);
                                    }
                                	Gikam.alert('导出完成！');
                                })
                            }else if (fileIds.length === 1){
                                Gikam.getJson(IFM_CONTEXT + '/core/module/item/files/' + selections[0]).done(function(coreFile) {
                                    Gikam.download(IFM_CONTEXT + coreFile.downloadUrl);
                                });
                            	Gikam.alert('导出完成！');
                            }
                        }else{
                        	Gikam.alert('导出失败！');
                        }
                        Gikam.getComp('emc-project-query-edit-list-grid').refresh();
                    });
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }

    },


    onQuSubpackageActive : function(row) {
        //展示分包单下面的样品信息
        var _this = this;
        if (row) {
            Gikam.getAsyncComp('emc-subpackage-query-list-base-info-form').done(function (form) {
                form.setReadonly(true);
                form.refresh({
                    url : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/{id}', {
                        id : row.id
                    }),
                });
            });
            Gikam.getComp('emc-project-query-list-subpackage-order-grid').refresh({
                url : Gikam.printf(emcProject.baseUrl + '/{id}/orders/queries', {
                    id : row.projId
                })
            });
        }

    },

    onQuSampleSubpackageActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getAsyncComp('emc-subpackage-query-sample-list-base-info-form').done(function (form) {
                form.setReadonly(true);
                form.refresh({
                    url : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/{id}', {
                        id : row.id
                    }),
                });
            });
            Gikam.getComp('emc-project-query-list-subpackage-folder-grid').refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/project/proj-schemes' + '/queries/raw',

                requestData: {
                    projId_SEQ : row.projId,
                    subpackageflag_SEQ : '1'
                }
            });
        }

    },


    onSubpackageOrderInfoActive : function(row) {
        //展示分包单样品下的测试信息
        var _this = this;
        var selections = Gikam.getComp('emc-proj-query-subpackage-info-grid').getSelections();
        console.log(selections);
        if (row) {
            Gikam.getComp('emc-project-query-list-subpackage-order-task-grid').refresh({
                url : IFM_CONTEXT + "/secure/emc/module/bp/subpackages/tasks/queries",
                requestData: {
                    orderId_SEQ : row.id,
                    subpackageId_SEQ : selections[0].id,
                }
            });
        }

    },

    onSampleSubpackageInfoActive : function(row) {
        //展示分包单下面的点位信息
        var _this = this;
        console.log(row);
        if (row) {
            Gikam.getComp('emc-project-query-list-subpackage-folder-grid').refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/project/proj-schemes' + '/queries/raw',

                requestData: {
                    projId_SEQ : row.projId,
                    subpackageflag_SEQ : '1'
                }
            });
        }

    },


    onSubpackageFolderInfoActive : function(row) {
        //展示分包单样品下的测试信息
        var _this = this;
        var selections = Gikam.getComp('emc-proj-query-subpackage-info-grid').getSelections();
        console.log(selections);
        if (row) {
            Gikam.getComp('emc-project-query-list-subpackage-sample-order-task-grid').refresh({
                url : IFM_CONTEXT + "/secure/emc/module/bp/subpackages/tasks/queries",
                requestData: {
                    projSchemeId_SEQ : row.id,
                    subpackageId_SEQ : selections[0].id,
                }
            });
        }

    },

    onSampleSubpackageFolderInfoActive : function(row) {
        //展示分包单样品下的测试信息
        var _this = this;
        var selections = Gikam.getComp('emc-project-query-list-sample-subpackage-info-grid').getSelections();
        console.log(selections);
        if (row) {
            Gikam.getComp('emc-project-query-list-subpackage-sample-order-task-grid').refresh({
                url : IFM_CONTEXT + "/secure/emc/module/bp/subpackages/tasks/queries",
                requestData: {
                    projSchemeId_SEQ : row.id,
                    subpackageId_SEQ : selections[0].id,
                }
            });
        }

    },

    getSampleSubpackageButton : function() {
        var _this = this;
        return  [{
            type : 'button',
            text : '查看分包单',
            icon : 'select',
            onClick : function() {
                Gikam.viewReport('subpackageProject', Gikam.getComp('emc-project-query-list-sample-subpackage-info-grid').getSelections());
            }
        } ]
    },

    getSubpackageButton : function() {
        var _this = this;
        return  [{
            type : 'button',
            text : '查看分包单',
            icon : 'select',
            onClick : function() {
                Gikam.viewReport('subpackageProject', Gikam.getComp('emc-proj-query-subpackage-info-grid').getSelections());
            }
        } ]
    },


    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-project-query-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 400,
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getBaseInfoTab() ]
            },
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
