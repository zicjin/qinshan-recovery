emcProjectInfo.editPage = {

    // 项目列表
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-project-info-edit-list-grid',
            url : emcProjectInfo.baseUrl + '/queries/searchable',
            columns : emcProjectInfo.getEditListColumns(),
            service : 'emcProjectServiceImpl',
            page : true,
            filterOpen : true,
            requestData : {
                targetType : 'modifyProjectInfo',
	            orgId_SB : workspace.user.ext$.parentorgid
            },
            deleteFormatter : function(row) {
                return row.projName || row.id;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ "emc-project-info-edit-list-order-grid", "emc-project-info-edit-list-order-item-group-grid", "emc-project-info-edit-list-director-grid" ]);
                    Gikam.getComp('emc-project-info-edit-list-base-info-tab').removePanel('emc-project-info-edit-list-order-panel');
                }
            },
            onRowActive : function(index, row) {
                _this.onProjectActive(row);
            },
            onBeforeRefresh : function(requestData) {
				if (requestData.status_CISC === Gikam.propI18N('EMC.MODULE.BP.EDIT.PROJECTINFOS.TITLE.NORMAL_STATUS')) {
					requestData.status_CISC = undefined;
					requestData.status_SEQ = undefined;
					requestData.status_DIFFER = 'cancel';
				} else if (requestData.status_CISC === Gikam.propI18N('EMC.MODULE.BP.EDIT.PROJECTINFOS.TITLE.CANCEL_STATUS')) {
				    requestData.status_DIFFER = undefined;
					requestData.status_CISC = undefined;
					requestData.status_SEQ = 'cancel';
				}
			},
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.BUTTON.STOP',
                icon : 'cancel',
                onClick : function() {
                    _this.cancelProject();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.BUTTON.RESTORE',
                icon : 'revert',
                onClick : function() {
                    var selections = Gikam.getComp('emc-project-info-edit-list-grid').getSelections();
                    if (selections.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    if (selections[0].status !== 'cancel') {
                        Gikam.alert('EMC.MODULE.BP.EDIT.PROJECTINFOS.TIP.SELECTED_IS_NORMAL_PROJECT');
                        return;
                    }
                    Gikam.confirm('EMC.MODULE.BP.EDIT.PROJECTINFOS.TIP.CONFIRM_RESTORE_PROJECT', 'EMC.MODULE.BP.EDIT.PROJECTINFOS.TIP.CONFIRM_RESTORE_PROJECT', function() {
                        var saveData = selections.map(function(row) {
                            return {
                                id : row.id,
                                status : row.beforeStatus,
                                beforeStatus : '',
                                cancelReason : ''
                            };
                        });

                        workspace.window.showMask(true);
                        Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcProjectServiceImpl', saveData ])).done(function(id) {
                            Gikam.getComp('emc-project-info-edit-list-grid').refresh();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    })
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('rasproject', Gikam.getComp('emc-project-info-edit-list-grid').getSelections());
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.PROJECT_CREATED_DELETE',
                icon : 'remove-row',
                onClick : function() {
                    var selections =  Gikam.getComp('emc-project-info-edit-list-grid').getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    Gikam.getComp('emc-project-info-edit-list-grid').deleteRows(IFM_CONTEXT + '/secure/emc/module/bp/project/projects');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    var grid = Gikam.getComp('emc-project-info-edit-list-grid');
                    Gikam.viewAudit('T_EMC_PROJECT', grid.getSelections());
                }
            } ]
        }
    },

    /** 终止项目 */
    //查CRM核销记录表，核销金额为0就可以作废，不为0不能作废，要作废就需要先到crm把发票作废或到款认领删除，然后再到LIMS作废项目
    cancelProject : function() {
        var selections = Gikam.getComp('emc-project-info-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections) || selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        if (selections[0].status === 'cancel') {
            Gikam.alert('EMC.MODULE.BP.EDIT.PROJECTINFOS.TIP.SELECTED_IS_STOPPED');
            return;
        }
        var modal = Gikam.create('modal', {
            title: "EMC.MODULE.BP.EDIT.PROJECTINFOS.BUTTON.STOP",
            width : 500,
            height : 250,
        });
        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type: 'btnToolbar',
                    items: [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        class : 'blue',
                        onClick : function() {
                            if (form.validate()) {
                                var saveData = selections.map(function(row) {
                                    return {
                                        id : row.id,
                                        beforeStatus : row.status,
                                        status : 'cancel',
                                        cancelReason : form.options.data.cancelReason
                                    };
                                });
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.postText('/secure/emc/module/bp/project/projects/action/cancel-check', Gikam.getJsonWrapper({}, [ 'emcProjectServiceImpl', saveData ])).done(function(data) {
                                	Gikam.getComp('emc-project-info-edit-list-grid').refresh();
                                }).always(function() {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close();
                                });
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
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
            fields: [
                {
                    field : 'cancelReason',
                    title : 'T_EMC_PROJECT.CANCEL_REASON',
                    validators : [ 'notEmpty', 'strLength[0, 120]' ],
                    preInsert : true,
                    type : 'textarea',
                    height : 100,
                }
            ],
            autoSave: false
        });
    },

    // 报告打印
    viewReport : function() {
        var selections = Gikam.getComp('emc-project-info-edit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var data = Gikam.getComp('emc-report-config-line-select-base-info-form').getData();
        if (Gikam.isEmpty(data.reportName)) {
            Gikam.alert('EMC.MODULE.SYS.REPORTS.TIP.SELECT_REPORT_FIRST');
            return;
        }

        Gikam.viewReport(data.reportName, selections[0]);
    },

    // 选中项目
    onProjectActive : function(row) {
        var _this = this
        var tab = Gikam.getComp('emc-project-info-edit-list-base-info-tab');
        if (row.sample === "1") {
            // 采样任务，展示监测方案、采样、采样单页签，隐藏样品登记页签
            tab.revertPanel('emc-project-info-edit-list-scheme-panel');
            tab.revertPanel('emc-project-info-edit-list-sampling-panel');
            tab.revertPanel('emc-project-info-edit-list-sample-panel');
            tab.removePanel('emc-project-info-edit-list-order-registration-panel');
        } else {
            // 送样任务，展示样品登记页签，隐藏监测方案、采样、采样单页签
            tab.revertPanel('emc-project-info-edit-list-order-registration-panel');
            tab.removePanel('emc-project-info-edit-list-scheme-panel');
            tab.removePanel('emc-project-info-edit-list-sampling-panel');
            tab.removePanel('emc-project-info-edit-list-sample-panel');
        }
        _this.addOrRemoveValidator(row);
        _this.onPanelActived(row);
    },

    addOrRemoveValidator : function(project) {
        Gikam.getAsyncComp('emc-project-info-edit-list-base-info-form').done(function (form) {
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

    // 基本信息
    getBaseInfoPanel : function() {
        var _this = this;
        return {
            title : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.TAB.BASEINFO',
            id : 'emc-project-info-edit-list-base-info-panel',
            items : [ {
                type : 'layout',
                north : {
                    height : '60%',
                    items : [ {
                        type : 'form',
                        columns : 3,
                        service : 'emcProjectServiceImpl',
                        id : 'emc-project-info-edit-list-base-info-form',
                        fields : emcProjectInfo.getBaseInfoFormFields(),
                        onBeforeUpdate  : function(row, keys) {
                            if (row.ext$ && row.ext$.projectamount) {
                                //根据projid获取金额
                                var arr = [];
                                arr.push(row);
                                var editAmount = row.ext$.projectamount;
                                if( editAmount == null ||  editAmount === ''){
                                    var formData = Gikam.getComp('emc-project-info-edit-list-base-info-form').getData();
                                    editAmount = formData.ext$.projectamount;
                                }
                                var flag = Gikam.postSync(emcProjectInfo.baseUrl + '/action/check-money', Gikam.getJsonWrapper({
                                    f : {
                                        editAmount : editAmount
                                    }}, [ 'pfProjectServiceImpl', arr ])).responseText;
                                if (flag === 'more'){
                                    return true;
                                } else if (flag.indexOf('less') > -1){
                                    Gikam.alert("开案金额不能小于发票和收款金额最大值");
                                    Gikam.getComp('emc-project-info-edit-list-base-info-form').refresh();
                                    return false;
                                } else if (flag.indexOf('error') > -1){
                                    Gikam.alert("开案金额不能大于合同总额");
                                    Gikam.getComp('emc-project-info-edit-list-base-info-form').refresh();
                                    return false;
                                } else if (flag.indexOf('over') > -1){
                                    Gikam.alert("合同已开案金额不能大于合同总额");
                                    Gikam.getComp('emc-project-info-edit-list-base-info-form').refresh();
                                    return false;
                                } else {
                                    Gikam.getComp('emc-project-info-edit-list-base-info-form').refresh();
                                    return false;
                                }
                            }
                        },
                        onUpdated : function(data) {
                            var grid = Gikam.getComp('emc-project-info-edit-list-grid');
                            grid.refreshRowById(grid.getActivedRow().id);
                            Gikam.getComp('emc-project-info-edit-list-base-info-form').refresh();
                            Gikam.postText(emcProjectInfo.baseUrl + '/action/sync-projinfo-after-edit', Gikam.getJsonWrapper(null, [ 'emcProjectServiceImpl', [data] ]));
                        },

                        onLoadSuccess : function(data) {
                            var date = new Date(data.projCreatedTime);
                            var date1 = Gikam.DateUtils.formatter(date, 'yyyy-MM-dd');
                            Gikam.getComp('emc-project-info-edit-list-base-info-form').setData({
                                projCreatedTime : date1
                            },false);
                                Gikam.refreshGrids({
                                'emc-project-info-edit-list-director-grid' : '/secure/emc/module/bp/project/projects/{id}/directors/queries',
                            }, {
                                id : data.id
                            });

                        }
                    } ]
                },
                center : {
                    items : [ {
                        type : 'tab',
                        id : 'emc-project-info-edit-list-director-tab',
                        fill : true,
                        width : Gikam.width.XL,
                        panels : [ {
                            id : 'emc-project-info-edit-list-director-panel',
                            title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TITLE.PROJECT_DIRECTOR',
                            items : [ {
                                type : 'grid',
                                id : 'emc-project-info-edit-list-director-grid',
                                filterOpen : false,
                                service : 'emcProjectDirectorServiceImpl',
                                page : false,
                                deleteFormatter : function(row) {
                                    return row.ext$.username || row.empId || row.id;
                                },
                                toolbar : [ {
                                    type : 'button',
                                    text : 'GIKAM.BUTTON.INSERT',
                                    icon : 'add-row',
                                    onClick : function() {
                                        _this.insertDirectorRow();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'GIKAM.BUTTON.DELETE',
                                    icon : 'remove-row',
                                    onClick : function() {
                                        _this.deleteDirectorRow();
                                    }
                                } ],
                                columns : emcProjectInfo.getProjectDirectorListColumns()
                            } ]
                        } ]

                    } ]
                }
            } ]
        }
    },

    // 监测方案
    getSchemePanel : function() {
        var _this = this
        return {
            title : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.TAB.MONITORSCHEME',
            id : 'emc-project-info-edit-list-scheme-panel',
            show : false,
            items : [ {
                type : 'layout',
                north : {
                    height : '50%',
                    items : [ {
                        type : 'grid',
                        id : 'emc-project-info-scheme-list-grid',
                        columns : emcProjectInfo.getSchemeEditListColumns(),

                        deleteFormatter : function(row) {
                            return row.folderNo || row.id;
                        },
                        onUpdated : function(data) {
                        	var array = [];
                        	array.unshift(data);
                    		var rows = Gikam.getComp('emc-project-info-scheme-list-grid').getSelections();
                            Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/project/proj-schemes/action/sync-folder', Gikam.getJsonWrapper({},  [ 'PfProjSchemeServiceImpl', array])).done(function() {
                                Gikam.getComp('emc-project-info-scheme-list-grid').refreshRowById(data.id);
                            });
                        },
                        onLoadSuccess : function(data) {
                            if (!Gikam.isEmpty(data)) {
                                this.activeRowByIndex(0);
                            } else {
                                Gikam.cleanCompData([ "emc-project-info-scheme-list-order-grid" ]);
                            }
                        },
                        onRowActive : function(index, row) {
                            Gikam.getComp('emc-project-info-scheme-list-order-grid').refresh({
                                url : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/bp/project/proj-schemes/{id}/proj-orders/queries', {
                                    id : row.id
                                })
                            });
                        },
                    }, ]
                },
                center : {
                    items : [ {
                        type : 'grid',
                        id : 'emc-project-info-scheme-list-order-grid',// 方法分析项组
                        columns : emcProjectInfo.getOrderEditListColumns(),
                    } ]
                }
            } ]
        }
    },

    // 样品登记
    getOrderRegistrationPanel : function() {
        var _this = this;
        return {
            id : 'emc-project-info-edit-list-order-registration-panel',
            title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.ORDER',
            show : false,
            items : [ {
                type : 'layout',
                north : {
                    height : '50%',
                    items : [ {
                        type : 'grid',
                        id : 'emc-project-info-edit-list-order-registration-grid',
                        columns : emcProjectInfo.getOrderRegistrationColumns(),
                        service : 'emcOrderServiceImpl',
                        page :true,
                        onBeforeUpdate  : function(row, keys) {
                            if (row && row.sendDate) {
                                let gridData = Gikam.getComp('emc-project-info-edit-list-order-registration-grid').getData();
                                let targetRows = gridData.filter(item => item.id === row.id);
                                if (targetRows && targetRows[0] && targetRows[0].acceptedDate) {
                                    let sendDate = new Date(row.sendDate);
                                    let acceptedDate = new Date(targetRows[0].acceptedDate);
                                    if (sendDate.getTime() > acceptedDate.getTime()) {
                                        Gikam.alert('EMC.MODULE.BP.PROJECT.PROJECTS.TIP.SEND_DATE_CAN_NOT_OVER_ACCEPT_DATE');
                                        return false;
                                    }
                                }
                            }
                        },
                        onLoadSuccess : function(data) {
                            if (!Gikam.isEmpty(data)) {
                                this.activeRowByIndex(0);
                            } else {
                                Gikam.cleanCompData([ 'emc-project-info-edit-list-order-item-group-grid' ]);
                            }
                        },
                        onRowActive : function(index, row) {
                            Gikam.getComp('emc-project-info-edit-list-order-item-group-grid').refresh({
                                url : Gikam.printf('/secure/emc/module/bp/bp/orders/{id}/item-groups/queries', {
                                    id : row.id
                                })
                            });
                        },
                        toolbar : [ {
                            type : 'button',
                            text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.BATCH_UPDATE',
                            icon : 'add',
                            onClick : function() {
                                _this.batchUpdate();
                            }
                        }, {
                            type : 'dropDownMenu',
                            text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.ORDER_QUICK_INPUT',
                            items : [ {
                                type : 'button',
                                text : 'GIKAM.BUTTON.EXPORT',
                                icon : 'move-up',
                                onClick : function() {
                                    _this.orderQuickExport();
                                }
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.IMPORT',
                                icon : 'move-down',
                                onClick : function() {
                                    _this.orderQuickImport();
                                }
                            } ]
                        }],
                    } ]
                },

                center : {
                    items : [ {
                        type : 'grid',
                        id : 'emc-project-info-edit-list-order-item-group-grid',// 样品分析项组
                        columns : emcProjectInfo.getOrderItemGroupColumns(),
                        service : 'emcOrderItemGroupServiceImpl',
                    } ]
                },


            } ]
        }
    },

    // 采样
    getSamplingPanel : function() {
        var _this = this;
        return {
            title : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.TAB.SAMPLING',
            id : 'emc-project-info-edit-list-sampling-panel',
            show : false,
            items : [ {
                type : 'tab',
                panels : [ {
                    title : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.TAB.ORDERGROUP',
                    items : [ {
                        type : 'grid',
                        id : 'emc-project-info-sampling-grid', // 点位信息
                        columns : emcProjectInfo.getSamplePlanEditListColumns(),
                        service : 'emcFolderServiceImpl',
                        toolbar : [ {
                            type : 'button',
                            text : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.BUTTON.CONFIG_COMPARISON',
                            icon : 'copy',
                            onClick : function() {
                                _this.configComparison();
                            }
                        } ]
                    } ]
                }, {
                    title : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.TAB.ORDER_TEST',
                    items : [ {
                        type : 'layout',
                        north : {
                            height : '50%',
                            items : [ {
                                type : 'grid',
                                id : 'emc-project-info-sampling-order-grid', // 样品信息
                                columns : emcProjectInfo.getSamplePrepareOrderColumns(),
                                service : 'emcOrderServiceImpl',
                                deleteFormatter : function(row) {
                                    return row.orderNo || row.id;
                                },
                                onLoadSuccess : function(data) {
                                    if (!Gikam.isEmpty(data)) {
                                        this.activeRowByIndex(0);
                                    } else {
                                        Gikam.cleanCompData([ "emc-project-info-sampling-order-test-grid" ]);
                                    }
                                },
                                onRowActive : function(index, row) {
                                    Gikam.getComp('emc-project-info-sampling-order-test-grid').refresh({
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
                                id : 'emc-project-info-sampling-order-test-grid', // 样品测试
                                columns : emcProjectInfo.getSamplePrepareOrderContainerColumns(),
                                service : 'emcOrderTaskServiceImpl',
                                onBeforeUpdate : function(data, keys) {
                                    var sampleWeight = data.row.ext$.sampleweight;
                                    if (!Gikam.isEmpty(sampleWeight)) {
                                        var multiTestId = data.row.ext$.multitestid
                                        if (!Gikam.isEmpty(multiTestId)) {
                                            var ids = multiTestId.split(",");
                                            var relationTests = [];
                                            ids.forEach(function(id) {
                                                if (id !== data.row.id + '') {
                                                    relationTests.push({
                                                        id : id,
                                                        ext$ : {
                                                            sampleweight : sampleWeight
                                                        }
                                                    });
                                                }
                                            });

                                            if (!Gikam.isEmpty(relationTests)) {
                                                Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/instant', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', relationTests ]));
                                            }
                                        }
                                    }
                                },
                            } ]
                        }
                    } ]
                } ]
            } ]
        }
    },

    /** 配置比对信息 */
    configComparison : function() {
        var selections = Gikam.getComp('emc-project-info-sampling-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        if (Gikam.isEmpty(selections[0].monitorPointId)){
            Gikam.alert('EMC.MODULE.BP.BP.FOLDERS.TIP.MONITOR_UNMAINTAINED');
            return;
        }

        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.BUTTON.CONFIG_COMPARISON',
            width : '80%',
            url : IFM_CONTEXT + "/secure/emc/module/mdm/bizmdm/comparisons/page/edit-list" + Gikam.param({
                monitorPointId_SEQ : selections[0].monitorPointId,
            })
        });
    },

    // 采样单
    getSamplePanel : function() {
        var _this = this;
        return {
            title : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.TAB.SAMPLEORDER',
            id : 'emc-project-info-edit-list-sample-panel',
            show : false,
            items : [ {
                type : 'layout',
                north : {
                    height : '50%',
                    items : [ {
                        type : 'grid',
                        id : 'emc-project-info-edit-list-sample-grid', // 采样单
                        columns : emcProjectInfo.getProjectSampleColumns(),
                        service : 'emcSampleServiceImpl',
                        deleteFormatter : function(row) {
                            return row.orderNo || row.id;
                        },
                        onLoadSuccess : function(data) {
                            if (!Gikam.isEmpty(data)) {
                                this.activeRowByIndex(0);
                            } else {
                                Gikam.cleanCompData([ "emc-project-info-edit-list-order-test-grid" ]);
                            }
                        },
                        onRowActive : function(index, row) {
                            Gikam.getComp('emc-project-info-edit-list-order-test-grid').refresh({
                                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/queries',
                                requestData : {
                                    sampleId_SEQ : row.id
                                }
                            });
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
                            text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.INSTRUMENT_ADJUSTMENT',
                            icon : 'material-to-check',
                            onClick : function() {
                                var selections = Gikam.getComp('emc-project-info-edit-list-sample-grid').getSelections();
                                if (selections.length !== 1) {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                    return;
                                }

                                Gikam.create('modal', {
                                    width : '80%',
                                    title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.INSTRUMENT_ADJUSTMENT',
                                    url : IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipt-calibrations/page/edit-list',
                                    onAfterClose : function (data) {
                                        if (!Gikam.isEmpty(data)) {
                                            var calibrationIds = data.map(function (item) {
                                                return item.id;
                                            }).join();
                                            selections.forEach(function (item) {
                                                item.calibrationId = calibrationIds;
                                            });
                                            Gikam.put( IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/' + selections[0].id, Gikam.getJsonWrapper({}, [ null, selections ]));
                                        }
                                    },
                                });
                            }
                        },{
                            type : 'button',
                            text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SAMPLING_EQUIPMENT',
                            icon : 'modify',
                            hidden : false,
                            onClick : function() {
                                var selections = Gikam.getComp('emc-project-info-edit-list-sample-grid').getSelections();
                                if (Gikam.isEmpty(selections)) {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                    return;
                                }

                                var ids = selections.map(row => row.id).join();

                                Gikam.create('modal', {
                                    width : '60%',
                                    title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SAMPLING_EQUIPMENT',
                                    url : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/page/sampling-equipt' + Gikam.param({
                                        ids : ids
                                    })
                                });
                            }
                        } ]
                    } ]
                },
                center : {
                    items : [ {
                        type : 'layout',
                        west : {
                            width : '50%',
                            items : [ {
                                type : 'grid',
                                id : 'emc-project-info-edit-list-order-test-grid',// 样品
                                columns : emcProjectInfo.getSampleTestColumns(),
                                service : 'emcOrderServiceImpl',
                                onLoadSuccess : function(data) {
                                    if (!Gikam.isEmpty(data)) {
                                        this.activeRowByIndex(0);
                                    } else {
                                        Gikam.cleanCompData([ "emc-project-info-edit-list-order-container-grid" ]);
                                    }
                                },
                                onRowActive : function(index, row) {
                                    Gikam.getComp('emc-project-info-edit-list-order-container-grid').refresh({
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
                                id : 'emc-project-info-edit-list-order-container-grid',// 测试信息
                                columns : emcProjectInfo.getSamplePrepareOrderContainerColumns(),
                                service : 'emcOrderTaskServiceImpl',
                                onBeforeUpdate : function(data, keys) {
                                    var sampleWeight = data.row.ext$.sampleweight;
                                    if (!Gikam.isEmpty(sampleWeight)) {
                                        var multiTestId = data.row.ext$.multitestid
                                        if (!Gikam.isEmpty(multiTestId)) {
                                            var ids = multiTestId.split(",");
                                            var relationTests = [];
                                            ids.forEach(function(id) {
                                                if (id !== data.row.id + '') {
                                                    relationTests.push({
                                                        id : id,
                                                        ext$ : {
                                                            sampleweight : sampleWeight
                                                        }
                                                    });
                                                }
                                            })

                                            if (!Gikam.isEmpty(relationTests)) {
                                                Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/instant', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', relationTests ]));
                                            }
                                        }
                                    }
                                },
                                toolbar : [ {
                                    type : 'button',
                                    text : 'GIKAM.BUTTON.CONTAINER_UPDATE',
                                    icon : 'modify',
                                    onClick : function() {
                                        _this.updateOrderTask();
                                    },
                                } ]
                            } ]
                        }
                    } ]
                }
            } ]
        }
    },

    uploadSampleFile : function() {
        var _this = this
        var rows = Gikam.getComp('emc-project-info-edit-list-sample-grid').getSelections();
        if (rows && rows.length === 1) {
            Gikam.create('modal', {
                width : 800,
                height : 500,
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ANNEX',
                url : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/page/sample-annex-list?id=' + rows[0].id
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    // 采样准备 容器信息修改
    updateOrderTask : function() {
        var selections = Gikam.getComp('emc-project-info-edit-list-order-container-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_CONTAINER_DATA');
            return;
        }

        var orderSelections = Gikam.getComp('emc-project-info-edit-list-order-test-grid').getSelections();
        if (orderSelections.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ORDER_DATA');
            return;
        }

        Gikam.create('modal', {
            width : 800,
            height : 600,
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.TAB.CONTAINER_INFO',
            url : IFM_CONTEXT + '/secure/emc/module/bp/edit/project-infos/page/container',
            onAfterClose : function(data) {
                Gikam.getLastModal().close();
            }
        });
    },

    // 项目附件
    getProjectAnnexPanel : function() {
        return {
            title : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.TAB.ANNEX',
            items : [ {
                type : 'uploader',
                id : 'emc-project-info-edit-list-uploader',
                dbTable : 'T_EMC_PROJECT',
                readOnly : true
            } ]
        }
    },

    // 项目附件
    getContractFilePanel : function() {
        return {
            title : '合同附件',
            items : [ {
                type : 'uploader',
                id : 'emc-project-info-edit-list-contract-uploader',
                dbTable : 'T_CRM_OPPORTUNITY_CONTRACT',
                readonly : false,
                formatterGridToolbar : function () {
                    return [ {
                        type: 'button',
                        text: 'GIKAM.FILE.BUTTON.DELETE_FILE',
                        icon: 'file-remove',
                        onClick: function() {
                            Gikam.getAsyncComp('emc-project-info-edit-list-contract-uploader').done(function (uploader) {
                                uploader.grid.deleteRows(uploader.options.deleteUrl);
                            });
                        }
                    } ]
                }
            } ]
        }
    },

    // 审核记录
    getAuditHistoryPanel : function() {
        return {
            title : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.TAB.AUDITRECORD',
            items : [ {
                type : 'grid',
                id : 'emc-project-info-edit-list-audit-record-grid',
                url : '',
                columns : emcProjectInfo.getAuditRecordListColumns(),
                page : true,
                group : {
                    groupCheckbox : false,
                    fields : [ 'auditCategory' ],
                    formatter : function(row) {
                        return row.stepCode + '、' + row.auditCategory;
                    }
                }
            } ]
        }
    },

    // 报告数据
    getReportDataPanel : function() {
        return {
            title : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.TAB.REPORTDATA',
            items : [ {
                type : 'layout',
                north : {
                    height : '50%',
                    items : [ {
                        type : 'grid',
                        id : 'emc-project-info-report-data-grid',
                        columns : emcProjectInfo.getReportEditListColumns(),
                        service : 'emcReportServiceImpl',
                        onLoadSuccess : function(data) {
                            if (!Gikam.isEmpty(data)) {
                                this.activeRowByIndex(0);
                            }
                        },
                        onRowActive : function(index, row) {

                            Gikam.getComp('emc-report-edit-list-uploader').setOptions({
                                bizId : row.id,
                            })
                            Gikam.getAsyncComp('emc-project-info-report-data-folder-grid').done(function(grid) {
                                grid.refresh({
                                    url : IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/queries/searchable',
                                    requestData : {
                                        projId_SEQ : row.projId
                                    }
                                });
                            });
                            Gikam.getAsyncComp('emc-project-info-report-data-order-grid').done(function(grid) {
                                grid.refresh({
                                    url : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/queries/searchable',
                                    requestData : {
                                        projId_SEQ : row.projId,
                                        monitorTmplId_SEQ : row.monitorTmplId
                                    }
                                });
                            });
                        },
                    } ]
                },
                center : {
                    items : [ {
                        type : 'tab',
                        width : Gikam.width.XL,
                        fill : true,
                        panels : [ {
                            title : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.TAB.FOLDER_INFO', // 点位信息
                            items : [ {
                                type : 'grid',
                                id : 'emc-project-info-report-data-folder-grid',
                                columns : emcProjectInfo.getReportFolderEditListColumns(),
                                toolbar : [ {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.BUTTON.VIEW_INDEX_MODIFY_HISTORY',
                                    icon : 'edit',
                                    onClick : function() {
                                    }
                                } ]
                            } ]
                        }, {
                            title : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.TAB.ORDER_INFO', // 样品信息
                            items : [ {
                                type : 'grid',
                                id : 'emc-project-info-report-data-order-grid',
                                columns : emcProjectInfo.getReportOrderEditListColumns()
                            } ]
                        }, {
                            title : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.TAB.REPORT_ANNEX', // 报告附件
                            items : [ {
                                type : 'uploader',
                                id : 'emc-report-edit-list-uploader',
                                dbTable : 'T_EMC_REPORT'
                            } ]
                        } ]
                    } ]
                }
            } ]
        }
    },

    // 查询结果
    getQueryResultPanel : function() {
        return {
            title : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.TAB.QUERYRESULT',
            items : [ {
                type : 'layout',
                north : {
                    height : '50%',
                    items : [ {
                        type : 'layout',
                        west : {
                            width : '50%',
                            items : [ {
                                type : 'tab',
                                panels : [ {
                                    title : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.TAB.REPORT', // 报告
                                    items : [ {
                                        type : 'grid',
                                        id : 'emc-project-info-query-result-report-grid',
                                        columns : emcProjectInfo.getReportEditListColumns(),
                                        onLoadSuccess : function(data) {
                                            if (!Gikam.isEmpty(data)) {
                                                this.activeRowByIndex(0);
                                            } else {
                                                Gikam.cleanCompData([ "emc-project-info-query-result-order-grid", "emc-project-info-query-result-test-grid", "emc-project-info-query-result-test-result-grid" ]);
                                            }
                                        },
                                        onRowActive : function(index, row) {
                                            Gikam.getAsyncComp('emc-project-info-query-result-order-grid').done(function(grid) {
                                                grid.refresh({
                                                    url : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/queries/searchable',
                                                    requestData : {
                                                        projId_SEQ : row.projId,
                                                        monitorTmplId_SEQ : row.monitorTmplId
                                                    }
                                                });
                                            });
                                        },
                                    } ]
                                } ]
                            } ]
                        },
                        center : {
                            width : '50%',
                            items : [ {
                                type : 'tab',
                                panels : [ {
                                    title : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.TAB.ORDER_INFO', // 样品信息
                                    items : [ {
                                        type : 'grid',
                                        id : 'emc-project-info-query-result-order-grid',
                                        columns : emcProjectInfo.getQueryResultOrderColumns(),
                                        onLoadSuccess : function(data) {
                                            if (!Gikam.isEmpty(data)) {
                                                this.activeRowByIndex(0);
                                            } else {
                                                Gikam.cleanCompData([ "emc-project-info-query-result-test-grid", "emc-project-info-query-result-test-result-grid" ]);
                                            }
                                        },
                                        onRowActive : function(index, row) {
                                            Gikam.getComp('emc-project-info-query-result-test-grid').refresh({
                                                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group',
                                                requestData : {
                                                    orderId_SEQ : row.id,
                                                    virtual_IN: ','
                                                }
                                            });

                                            /*
                                             * Gikam.getAsyncComp('emc-project-info-query-result-test-grid').done(function(grid) {
                                             * grid.refresh({ url :
                                             * Gikam.printf(IFM_CONTEXT +
                                             * '/secure/emc/module/bp/bp/orders/{id}/order-tasks/queries', {
                                             * id : row.id }) }); });
                                             */
                                        },
                                    } ]
                                } ]
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
                                type : 'tab',
                                width : Gikam.width.XL,
                                panels : [ {
                                    title : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.TAB.TEST_INFO', // 测试信息
                                    items : [ {
                                        type : 'grid',
                                        id : 'emc-project-info-query-result-test-grid',
                                        columns : emcProjectInfo.getQueryResultTestColumns(),
                                        onLoadSuccess : function(data) {
                                            if (!Gikam.isEmpty(data)) {
                                                this.activeRowByIndex(0);
                                            } else {
                                                Gikam.cleanCompData([ "emc-project-info-query-result-test-result-grid" ]);
                                            }
                                        },
                                        onRowActive : function(index, row) {
                                            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/std-set-categorys/field-sets/queries', Gikam.getJsonWrapper({
                                                f : {
                                                    qcCategoryId : row.qcCategoryId,
                                                    qcCategoryCode : row.qcCategoryCode
                                                },
                                                n : -1,
                                                s : 50
                                            })).done(function(str) {
                                                var jsonArr = JSON.parse(str);
                                                var arr = [];
                                                jsonArr.forEach(function(e) {
                                                    if (e.display === 'displayedit') {
                                                        arr.push({
                                                            field : e.fieldName,
                                                            title : e.fieldTitle,
                                                            editor : true,
                                                            width : e.width,
                                                        });
                                                    } else if (e.display === 'displaynoedit') {
                                                        arr.push({
                                                            field : e.fieldName,
                                                            title : e.fieldTitle,
                                                            editor : false,
                                                            width : e.width,
                                                        });
                                                    } else if (Gikam.isEmpty(e.display)) {
                                                        arr.push({
                                                            field : e.fieldName,
                                                            title : e.fieldTitle,
                                                            width : e.width,
                                                        });
                                                    }
                                                });
                                                var publicColumns = emcProjectInfo.getResultPublicColumns();
                                                var columns = publicColumns.concat(arr);
                                                Gikam.getComp('emc-project-info-query-result-test-result-grid').refreshColumns(columns);
                                                Gikam.getComp('emc-project-info-query-result-test-result-grid').refresh({
                                                    url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/queries',
                                                    requestData : {
                                                        orderId_SEQ : row.orderId,
                                                        runId_SEQ : row.runId,
                                                        orderTaskId_SEQ : '',
                                                        virtual_IN: ','
                                                    }
                                                });

                                            });
                                        },
                                    } ]
                                } ]
                            } ]
                        },
                        center : {
                            items : [ {
                                type : 'tab',
                                width : Gikam.width.XL,
                                fill : true,
                                panels : [ {
                                    title : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.TAB.RESULT', // 结果
                                    items : [ {
                                        type : 'grid',
                                        id : 'emc-project-info-query-result-test-result-grid',
                                        columns : emcProjectInfo.getResultColumns(),
                                    } ]
                                } ]
                            } ]
                        }
                    } ]
                }
            } ]
        }
    },

    // 激活基本信息标签页
    onBaseInfoPanelActived : function(projectId) {
        Gikam.getAsyncComp('emc-project-info-edit-list-base-info-form').done(function (form) {
            form.refresh({
                url : Gikam.printf(emcProjectInfo.baseUrl + '/{id}', {
                    id : projectId
                }),
            })
        });
    },

    // 激活监测方案标签页
    onSchemePanelActived : function(projectId) {
        Gikam.getComp('emc-project-info-scheme-list-grid').refresh({
            url : IFM_CONTEXT + '/secure/emc/module/bp/project/proj-schemes/queries/raw',
            requestData : {
                projId_SEQ : projectId
            },
        });
    },

    // 激活样品登记标签页
    onOrderRegistionPanelActived : function(projectId) {
        Gikam.getComp('emc-project-info-edit-list-order-registration-grid').refresh({
            url : Gikam.printf(emcProjectInfo.baseUrl + '/{id}/orders/queries', {
                id : projectId
            })
        });
    },

    // 激活采样标签页
    onSamplingPanelActived : function(projectId) {
        Gikam.getComp('emc-project-info-sampling-grid').refresh({
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/queries',
            requestData : {
                projId_SEQ : projectId
            },
        });

        Gikam.getComp('emc-project-info-sampling-order-grid').refresh({
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/queries/raw',
            requestData : {
                projId_SEQ : projectId
            },
        });
    },

    // 激活采样单标签页
    onSamplePanelActived : function(projectId) {
        Gikam.getComp('emc-project-info-edit-list-sample-grid').refresh({
            url : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/queries/all',
            requestData : {
                projId_SEQ : projectId
            },
        });
    },

    // 激活项目附件标签页
    onProjectAnnexPanelActived : function(projectId) {
        Gikam.getComp('emc-project-info-edit-list-uploader').setOptions({
            bizId : projectId,
        });
    },

    // 查询合同附件
    onContractFilePanelActived : function(contractNo) {
        Gikam.getAsyncComp('emc-project-info-edit-list-contract-uploader').done(function (uploader) {
            Gikam.post(IFM_CONTEXT + '/secure/emc/module/mdm/crm/contracts/queries', Gikam.getJsonWrapper({
                f : {
                    contractNo_SEQ : contractNo
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
    },

    // 激活审核记录标签页
    onAuditHistoryPanelActived : function(projectId) {
        Gikam.getComp('emc-project-info-edit-list-audit-record-grid').refresh({
            url : IFM_CONTEXT + '/secure/emc/module/bp/audit-records/queries',
            requestData : {
                projId_SEQ : projectId
            },
        });
    },

    // 激活报告数据标签页
    onReportDataPanelActived : function(projectId) {
        Gikam.getComp('emc-project-info-report-data-grid').refresh({
            url : IFM_CONTEXT + '/secure/emc/module/bp/report/reports/queries',
            requestData : {
                projId_SEQ : projectId
            },
        });
    },

    // 激活查询结果标签页
    onQueryResultPanelActived : function(projectId) {
        Gikam.getComp('emc-project-info-query-result-report-grid').refresh({
            url : IFM_CONTEXT + '/secure/emc/module/bp/report/reports/queries',
            requestData : {
                projId_SEQ : projectId
            },
        });
    },

    // 标签页
    activedIndex : 0,
    tabPanelActivedEvents : [ 'onBaseInfoPanelActived', 'onSchemePanelActived', 'onOrderRegistionPanelActived', 'onSamplingPanelActived', 'onSamplePanelActived', 'onProjectAnnexPanelActived', 'onContractFilePanelActived','onAuditHistoryPanelActived', 'onReportDataPanelActived', 'onQueryResultPanelActived', ],
    onPanelActived : function(row) {
        var _this = this;
        if (row) {
            let methodName = _this.tabPanelActivedEvents[this.activedIndex];
            if (methodName === 'onContractFilePanelActived') {
                eval('_this.' + methodName + '(\'' + row.ext$.contractno + '\')')
            } else {
                eval('_this.' + methodName + '(' + row.id + ')')
            }
        }
    },
    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            id : 'emc-project-info-edit-list-base-info-tab',
            panels : [
                _this.getBaseInfoPanel(),
                _this.getSchemePanel(),
                _this.getOrderRegistrationPanel(),
                _this.getSamplingPanel(),
                _this.getSamplePanel(),
                _this.getProjectAnnexPanel(),
                _this.getContractFilePanel(),
                _this.getAuditHistoryPanel(),
                _this.getReportDataPanel(),
                _this.getQueryResultPanel()
            ],
            onActived : function(data) {
                _this.activedIndex = data.index;
                var grid = Gikam.getComp('emc-project-info-edit-list-grid');
                if (grid) {
                    var selections = grid.getSelections();
                    if (!Gikam.isEmpty(selections)) {
                        _this.onPanelActived(selections[0]);
                    }
                }
            }
        }
    },

    insertDirectorRow : function() {
        var formData = Gikam.getComp('emc-project-info-edit-list-base-info-form').getData();
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('emp').title,
            url : Gikam.choose.getConfig('emp').url + Gikam.param({
                targetType : 'insertDirector',
                projectId : formData.id
            }),
            width : 310,
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        empId : item.id,
                        projectId : formData.id
                    }
                });
                Gikam.getComp('emc-project-info-edit-list-director-grid').insert('/secure/emc/module/bp/project/projects/directors', chooseData);
            }
        });
    },

    deleteDirectorRow : function() {
        var url = '/secure/emc/module/bp/project/projects/directors';
        Gikam.getComp('emc-project-info-edit-list-director-grid').deleteRows(url).done(function() {
        });
    },

    orderQuickExport : function() {
        var orderData = Gikam.getComp('emc-project-info-edit-list-order-registration-grid').getData();
        if (Gikam.isEmpty(orderData)) {
            Gikam.alert('EMC.MODULE.BP.PROJECT.PROJECTS.ORDER_CAN_NOT_EXPORT');
            return;
        }
        var projectId = orderData[0].projId;
        Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}/{id}', {
            templateCode : 'pfOrderExportTemplate',
            service : 'pfOrderExportTemplate',
            id : projectId
        })).done(function(fileUri) {
            var fileUrl = IFM_CONTEXT + fileUri;
            Gikam.download(fileUrl);
        });

    },

    orderQuickImport : function() {
        var orderData = Gikam.getComp('emc-project-info-edit-list-order-registration-grid').getData();
        if (Gikam.isEmpty(orderData)) {
            Gikam.alert('EMC.MODULE.BP.PROJECT.PROJECTS.TIP.ORDER_CAN_NOT_IMPORT');
            return;
        }
        var id = orderData[0].projId;

        Gikam.create('simpleUploader', {
            dbTable : 'T_EMC_ORDER',
            bizId : id,
            multiple : false,
            onAfterClose : function(files) {
                if (Gikam.isEmpty(files)) {
                    return;
                }
                Gikam.window.showMask(true);

                Gikam.post(
                    Gikam.printf(IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'pfOrderImportFileFunction', {
                        id: files[0].id
                    })
                ).done(function(result) {
                        if (result.url) {
                            Gikam.download(result.url);
                        } else if (result.message) {
                            Gikam.alert(result.message);
                        }
                        Gikam.getComp('emc-project-info-edit-list-order-registration-grid').refresh();
                    }).always(function() {
                        Gikam.window.closeMask();
                    });
            }
        });
    },

    batchUpdate : function(){
        var _this = this;
        var selections = Gikam.getComp('emc-project-info-edit-list-order-registration-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
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
                        onClick : function() {
                            if (form.validate()) {
                                var formData = form.getData();
                                selections.forEach(function (item) {
                                    item.sendDate = formData.sendDate;
                                    item.sendTime = formData.sendTime;
                                    item.orderDesc = formData.orderDesc;
                                    item.remark = formData.remark;
                                    item.volume = formData.ext$.volume;
                                    item.packing = formData.ext$.packing;
                                    item.ext$.sender = formData.ext$.sender;
                                    item.ext$.checker = formData.ext$.checker;
                                    item.labelNo = formData.labelNo;
                                    item.clientOrderName = formData.clientOrderName;
                                    item.ext$.isblank = formData.ext$.isblank;

                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/project/projects/' + selections[0].id, Gikam.getJsonWrapper({}, [ 'pfOrderServiceImpl', selections ])).done(function (data) {
                                    Gikam.getComp('emc-project-info-edit-list-order-registration-grid').refresh();
                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close();
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
            fields: emcProjectInfo.getBatchInputFields(),
            autoSave: false
        });

    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-project-info-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 480,
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getBaseInfoTab() ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
