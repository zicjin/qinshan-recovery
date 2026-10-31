emcSamplingPlan.editPage = {

    getProjectGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sampling-plan-project-list-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/project/projects/queries',
            columns : emcSamplingPlan.getProjectListColumns(),
            service : 'emcProjectServiceImpl',
            filterOpen : true,
            page : true,
            requestData : {
                targetType: 'samplePlaning',
	            org_filter : '1',
                sample_DIFFER : '0',
            },
            order: {
                urgent: 'desc'
            },
            onCellClick : function(field, row) {
                if (field === 'projNo') {
                    _this.loadProjDetailPage(row.projNo);
                }
            },
            onRowActive : function(index, row) {
                if (row) {
                    Gikam.getComp('emc-sampling-plan-edit-list-grid').refresh({
                        requestData : {
                            source_SEQ : 'monitorscheme',
                            orgId : workspace.user.ext$.parentorgid,
                            projId_SEQ : row.id,
                            termination_DIFFER : '1',
                        },
                        url : emcSamplingPlan.baseUrl + '/queries/raw',
                    });
                }
            },
            onLoadSuccess : function(data) {
                if (Gikam.isEmpty(data)) {
                    Gikam.cleanCompData([ 'emc-sampling-plan-edit-list-grid' ]);
                } else {
                    this.activeRowByIndex(0);
                }
            },
            toolbar : [ {
                type : 'button',
                text : '批量安排采样信息',
                icon : 'add',
                onClick : function() {
                    _this.updateFolderInfo();
                }
            }, {
                type : 'button',
                text : '批量提交',
                icon : 'submit',
                onClick : function() {
                    _this.submitFolder();
                }
            }, {
                type : 'button',
                text : '批量退回',
                icon : 'reject',
                onClick : function() {
                    _this.batchBack();
                }
            }, {
                type : 'button',
                text : '任务流转单',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('xianchanglianxi', Gikam.getComp('emc-sampling-plan-project-list-grid').getSelections());
                }
            },{
                type : 'form',
                autoSave : false,
                fields : [ {
                    field : 'dateSelect',
                    type : 'select',
                    value : '1',
                    width : '80',
                    items: [{"value": '1', "text": "全部"},
                        {"value": '2', "text": "本月"},
                        {"value": '3', "text": "本季度"},
                        {"value": '4', "text": "历史"}],
                    refreshGrid : false,
                    onChange : function(field, value, index) {
                        var requestData;
                        if(value == '2'){
                            requestData = {
                                requiredCompleteDate_DL : '',
                                requiredCompleteDateFilter : '1',
                                requiredCompleteDateQuarterFilter : ''
                            };
                        }else if(value == '4'){
                            requestData = {
                                requiredCompleteDate_DL : Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd'),
                                requiredCompleteDateFilter : '',
                                requiredCompleteDateQuarterFilter : ''
                            };
                        }else if(value == '3'){
                            requestData = {
                                requiredCompleteDate_DL : '',
                                requiredCompleteDateFilter : '',
                                requiredCompleteDateQuarterFilter : '1'
                            };
                        }else {
                            requestData = {
                                requiredCompleteDate_DL : '',
                                requiredCompleteDateFilter : '',
                                requiredCompleteDateQuarterFilter : ''
                            };
                        }
                        Gikam.getComp('emc-sampling-plan-project-list-grid').refresh({
                            requestData : requestData
                        });
                    },
                } ]
            }]
        }
    },

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sampling-plan-edit-list-grid',
            columns : emcSamplingPlan.getEditListColumns(),
            filterOpen : true,
            page : true,
            service : 'emcFolderServiceImpl',
            checkOnActive : true,
            order : {
                'AREANAME' : 'asc',
                'orderCategory' : 'asc',
                'folderNo' : 'asc'

            },
            deleteFormatter : function(row) {
                return row.folderNo || row.id;
            },
            onRendered : function() {
                this.hideColumns([ 'ext$.projname' ]);
            },
            onCellClick : function(field, row) {
                if (field === 'ext$.projno') {
                    _this.loadProjDetailPage(row.ext$.projno);
                }
            },
            onRowActive : function(index, row) {
                if (row && !isNaN(row.id)) {
                    Gikam.getComp('emc-sampling-plan-edit-list-order-grid').refresh({
                        requestData : {
                            folderId_SEQ : row.id,
                        },
                        url : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/queries/raw',
                    });
                }
            },
            onLoadSuccess : function(data) {
                if (Gikam.isEmpty(data)) {
                    Gikam.cleanCompData([ 'emc-sampling-plan-edit-list-container-info-grid', 'emc-sampling-plan-edit-list-order-grid','emc-sampling-plan-equipt-edit-list-grid' ]);
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.BUTTON.SAMPLEINFO',
                icon : 'add',
                onClick : function() {
                    _this.updateSampleInfo();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.BUTTON.REJECT',
                icon : 'reject',
                onClick : function() {
                    _this.back();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.BUTTON.SYNC_CONTAINER_INFO',
                icon : 'refresh',
                onClick : function() {
                    _this.syncFolderContainerInfo();
                },
            },{
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.BUTTON.DEL_FOLDER',
                icon : 'remove',
                hidden : true,
                onClick : function() {
                    Gikam.getComp('emc-sampling-plan-edit-list-grid').deleteRows(emcSamplingPlan.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.SEARCH',
                icon : 'search',
                hidden : true,
                onClick : function() {
                    Gikam.create('modal', {
                        title : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.MODAL.QUERY_SANPLE_PLAN',
                        width : 800,
                        url : IFM_CONTEXT + '/secure/emc/module/bp/sample/sampling-plans/page/query-list'
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.APPENDIX_INFO',
                icon : 'upload',
                onClick : function () {
                    var modal = Gikam.create('modal', {
                        title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ANNEX',
                        width : '49%'
                    });
                    // 拿合同ID
                    var projId = Gikam.getComp('emc-sampling-plan-edit-list-grid').getSelections()[0].projId;
                    var url = IFM_CONTEXT + '/secure/emc/module/bp/project/projects' + '/' + projId + '/columns/CONTRACTID'
                    var contractId = Gikam.getTextSync(url).responseText;
                    Gikam.create('layout', {
                        center : {
                            items : [ _this.getAnnexGridParam(contractId) ]
                        },
                        renderTo : modal.window.$dom,
                    })
                }
            }],
            group : {
                fields : [ 'orderCategory' ],
            }
        }
    },

    submit : function() {
        var grid = Gikam.getComp('emc-sampling-plan-edit-list-grid');
	 	var selections =  grid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var project = Gikam.getComp('emc-sampling-plan-project-list-grid').getActivedRow();
        if (!Gikam.getComp('emc-sampling-plan-project-list-grid').validate([project])) {
            return;
        }
	    Gikam.postText(emcSamplingPlan.baseUrl + '/action/submit-remind',Gikam.getJsonWrapper({},[ '', selections ])).done(function(message) {
	    	if(Gikam.isEmpty(message)){
		          if (grid.validate(selections)) {
		              Gikam.create('workflow').submit({
		                  data : grid.getSelections(),
		                  pageObject : emcSamplingPlan
		              }).done(function() {
		                  //grid.refresh();
		                  Gikam.getComp('emc-sampling-plan-project-list-grid').refresh();
		              });
		          }
	    	}else{
	    		Gikam.confirm('EMC.MODULE.BP.FOLDER.FOLDERS.TIP.CONTINUESUBMITTING', message, function() {
			          if (grid.validate(selections)) {
			              Gikam.create('workflow').submit({
			                  data : grid.getSelections(),
			                  pageObject : emcSamplingPlan
			              }).done(function() {
			                  //grid.refresh();
			                  Gikam.getComp('emc-sampling-plan-project-list-grid').refresh();
			              });
			          }
                })

	    	}
	     })



    },

    submitFolder : function() {
        var _this = this;

        var selections = Gikam.getComp('emc-sampling-plan-project-list-grid').getSelections();

        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        if (!Gikam.getComp('emc-sampling-plan-project-list-grid').validate(selections)) {
            return;
        }

        let modal = Gikam.create('modal', {
            title: "意见",
            width : 400,
            height : 180,
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
                                let data = {
                                    comment : form.getData().comment
                                }
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/action/submit-project', Gikam.getJsonWrapper(data, [ 'emcProjectServiceImpl', selections ])).done(function() {
                                    Gikam.getComp('emc-sampling-plan-project-list-grid').refresh();
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
                    field : 'comment',
                    title : '意见',
                    type : 'textarea',
                }
            ],
            autoSave: false
        });

        form.setData({
            projNo : selections[0].projNo
        })
    },

    updateFolderInfo : function() {
        var _this = this;

        var selections = Gikam.getComp('emc-sampling-plan-project-list-grid').getSelections();

        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var sampleData = Gikam.getComp('emc-sampling-plan-edit-list-grid').getData();
        let sampleOrgIds = sampleData.map(e => e.sampleOrgId).join(',');

        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.MODAL.SAMPLEINFO',
            width : 800,
            height : 600,
            url : '/secure/emc/module/bp/sample/sampling-plans/page/sample-info' + Gikam.param({
                single : true,
                project : 1,
                ids: sampleOrgIds
            }),
            onAfterClose : function(saveData) {
                if (Gikam.isEmpty(saveData)) {
                    return;
                }
                workspace.window.showMask(true);
                Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/action/update-by-project', Gikam.getJsonWrapper({}
                    , [ 'emcProjectServiceImpl', selections ], [ 'emcFolderServiceImpl', [saveData] ])).done(function(r) {
                    Gikam.getComp('emc-sampling-plan-project-list-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    updateSampleInfo : function() {
        var _this = this;

        var selections = Gikam.getComp('emc-sampling-plan-edit-list-grid').getSelections();

        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var idArr = selections.map(function(item){
            return item.sampleOrgId;
        });
        var ids = idArr.join(",");
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.MODAL.SAMPLEINFO',
            width : 800,
            height : 600,
            url : '/secure/emc/module/bp/sample/sampling-plans/page/sample-info' + Gikam.param({
                ids : ids,
                single : true,
                firstDate : selections[0].planSampleDate,
                sceneUserName : selections[0].sceneUserId,
                sampleUserName : selections[0].sampleUserId,
            }),
            onAfterClose : function(data) {
                if (Gikam.isEmpty(data)) {
                    return;
                }
                let saveData = selections.map(function (item) {
                    return Gikam.extend({
                        id: item.id
                    }, data);
                });
                workspace.window.showMask();
                Gikam.put(IFM_CONTEXT + '/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcFolderServiceImpl', saveData ])).done(function() {
                    Gikam.getComp('emc-sampling-plan-edit-list-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    back : function() {
        // 允许回退多条
        var selections = Gikam.getComp('emc-sampling-plan-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        // 虽然是采样安排退回，但实际上是按照项目ID处理，所以要去重
        var chooseData = [];
        selections.forEach(function(item) {
            var exist = chooseData.some(function(e) {
                if (e.id === item.projId) {
                    return true;
                }
            });
            if (exist)
                return;
            chooseData.push({
                id : item.projId
            });
        });
        // 回退
        Gikam.create('workflow').undo({
            data : chooseData,
            pageObject : {
                baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/project/projects',
                workflow : {
                    dbTable : 'T_EMC_PROJECT',
                    columns : [ {
                        field : 'id',
                        title : 'T_EMC_PROJECT.ID'
                    } ]
                }
            },
            requestParam : {
                projId : selections[0].projId
            }
        }).done(function() {
            //Gikam.getComp('emc-sampling-plan-edit-list-grid').refresh();
            Gikam.getComp('emc-sampling-plan-project-list-grid').refresh();
        });
    },

    batchBack : function() {
        // 允许回退多条
        var grid = Gikam.getComp('emc-sampling-plan-project-list-grid');
        Gikam.create('workflow').undo({
            data : grid.getSelections(),
            pageObject : {
                baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/project/projects',
                workflow : {
                    dbTable : 'T_EMC_PROJECT',
                    columns : [ {
                        field : 'id',
                        title : 'T_EMC_PROJECT.ID'
                    } ]
                }
            }
        }).done(function() {
            grid.refresh();
        });
    },

    /** 加载任务详情页面 */
    loadProjDetailPage : function(projNo) {
        Gikam.create('modal', {
            id : 'projModal',
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.MODAL.PROJ_DETAIL',
            width : 980,
            height : 500,
            showCloseBtn : true,
            url : IFM_CONTEXT + '/secure/emc/module/bp/project/projects/page/project-search-detail' + Gikam.param({
                projNo : projNo
            }),
        });
    },

    /** 样品信息 */
    getOrderGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sampling-plan-edit-list-order-grid',
            columns : emcSamplingPlan.getSamplePlanOrderColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-sampling-plan-edit-list-container-info-grid' ]);
                }
            },
            onRowActive : function(index, row) {
                if (!row) {
                    return;
                }

                Gikam.getComp('emc-sampling-plan-edit-list-container-info-grid').refresh({
                    /*url : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/{id}/order-tasks/queries', {
                        id : row.id
                    })*/
                    url : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/containers/queries',
                    requestData : {
                        orderId : row.id
                    }
                });
                Gikam.getAsyncComp('emc-sampling-plan-equipt-edit-list-grid').done(function(grid) {
                	grid.refresh({
                        url : '/secure/emc/module/bp/bp/order-tasks/queries',
                        requestData : {
                            folderId_IN : row.folderId,
                            orderNo_SEQ : row.orderNo,
                            analysisType_SEQ : 'sampling',
                            sampleId_SEQ : row.sampleId,
                            qcCategoryCode_DIFFER : 'P-BLANK',
                            targetType : 'samplingInput'
                        }
                    });
                    Gikam.getComp('emc-sampling-plan-equipt-edit-list-grid').refresh();
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.BUTTON.SYNC_CONTAINER_INFO',
                icon : 'refresh',
                onClick : function() {
                    _this.syncOrderContainerInfo();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('sampling', Gikam.getComp('emc-sampling-plan-edit-list-order-grid').getSelections());
                }
            } ]
        }
    },

      /** 容器信息和指派仪器 */
    getResultGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.CONTAINER_INFO',
                items : [{
                    type : 'layout',
                    center :{
                        items : [ {
                            type : 'grid',
                            id : 'emc-sampling-plan-edit-list-container-info-grid',
                            service : 'emcOrderTaskServiceImpl',
                            columns : emcSamplingPlan.getSamplePrepareOrderContainerColumns(this.param),
                            readonly : true,
                            onLoadSuccess : function(data) {
                                if (!Gikam.isEmpty(data)) {
                                    this.activeRowByIndex(0);
                                }
                            },
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
                            toolbar : [ {
                                type : 'button',
                                text : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.BUTTON.SYNC_CONTAINER_INFO',
                                icon : 'refresh',
                                onClick : function() {
                                    _this.syncContainerInfo();
                                },
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.CONTAINER_UPDATE',
                                icon : 'modify',
                                onClick : function() {
                                    _this.updateDataInputOrderTask();
                                }
                            } ]
                        } ]

                    }
                }]
            }, {
                title : 'EMC.MODULE.BP.BP.FOLDERS.ORDERS.TAB.APPOINT_EQUIPT.BASE_INFO',
                items : [ {
                    type : 'layout',
                    center : {
                        items : [ {
                            type : 'grid',
                            id : 'emc-sampling-plan-equipt-edit-list-grid',
                            service : 'emcOrderTaskServiceImpl',
                            columns : emcSamplingPlan.getOrderTaskEquiptColumns(),
                            readonly : true,
                            toolbar : [ {
                                hidden : false,
                                type : 'button',
                                text : 'GIKAM.BUTTON.ADD_EQUIPT',
                                icon : 'add',
                                onClick : function() {
                                    _this.addEpuipt();
                                }
                            },{
                                hidden : false,
                                type : 'button',
                                text : 'GIKAM.BUTTON.UPDATE_EQUIPT',
                                icon : 'edit',
                                onClick : function() {
                                    _this.updateEquipt();
                                }
                            }, {
                                hidden : false,
                                type : 'button',
                                text : 'GIKAM.BUTTON.UPDATE_EMP',
                                icon : 'edit',
                                onClick : function() {
                                    _this.updateEmp();
                                }
                            }, {
                                hidden : false,
                                type : 'button',
                                text : 'GIKAM.BUTTON.PARTAKEUSER_INPUT',
                                icon : 'edit',
                                onClick : function() {
                                    _this.updatePartakeUserName();
                                },
                            }, {
                                hidden : false,
                                type : 'button',
                                text : 'GIKAM.BUTTON.ASSIST_EQUIPT',
                                icon : 'edit',
                                onClick : function() {
                                    _this.updateAssitEpuipt();
                                }
                            }, {
                                type : 'button',
                                text : 'EMC.MODULE.BP.BPS.BUTTON.EQUIPT_RECORD',
                                icon : 'browse',
                                hidden : false,
                                onClick : function() {
                                    _this.equiptRecord();
                                }
                            } ]
                        } ]
                    },
                }, ]
            }, ]
        }
    },


    /** 按样品同步容器信息 */
    syncOrderContainerInfo : function() {
        var selections = Gikam.getComp('emc-sampling-plan-edit-list-order-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.confirm('GIKAM.TIP.CONFIRM', 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.TIP.SYNC_OVERRIDE_CONTAINER_INFO', function() {
            workspace.window.showMask(true);
            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/sync-order-container-info', Gikam.getJsonWrapper({}, ['', selections] )).done(function() {
                Gikam.getComp('emc-sampling-plan-edit-list-order-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        })
    },

    syncFolderContainerInfo : function() {
        var selections = Gikam.getComp('emc-sampling-plan-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.confirm('GIKAM.TIP.CONFIRM', 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.TIP.SYNC_OVERRIDE_CONTAINER_INFO', function() {
            workspace.window.showMask(true);
            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/sync-folder-container-info', Gikam.getJsonWrapper({}, ['', selections] )).done(function() {
                Gikam.getComp('emc-sampling-plan-edit-list-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        })
    },

    /** 同步容器信息 */
    syncContainerInfo : function() {
        var selections = Gikam.getComp('emc-sampling-plan-edit-list-container-info-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.confirm('GIKAM.TIP.CONFIRM', 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.TIP.SYNC_OVERRIDE_CONTAINER_INFO', function() {
            workspace.window.showMask(true);
            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/sync-container-info', Gikam.getJsonWrapper({}, ['', selections] )).done(function() {
                Gikam.getComp('emc-sampling-plan-edit-list-container-info-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        })
    },

    /** 容器信息修改 */
    updateDataInputOrderTask : function() {
        var selections = Gikam.getComp('emc-sampling-plan-edit-list-container-info-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_CONTAINER_DATA');
            return;
        }

        Gikam.create('modal', {
            width : 800,
            height : 600,
            id : 'emc-sample-edit-order-task-detail-modal',
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.TAB.CONTAINER_INFO',
            url : IFM_CONTEXT + '/secure/emc/module/bp/sample/sampling-plans/page/container-list',
            onAfterClose : function(data) {
                Gikam.getComp('emc-sampling-plan-edit-list-container-info-grid').refresh();
            }
        });

    },

    // 附件信息
    getAnnexGridParam : function (contractId) {
        return {
            type : 'uploader',
            id : 'emc-sampling-plan-edit-list-uploader',
            dbTable : 'T_EMC_CONTRACT',
            bizId : contractId,
            readonly : true,
        }
    },

    updateEquipt : function() {
        var _this = emcSamplingPlan.editPage;
        var selections = Gikam.getComp('emc-sampling-plan-equipt-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : Gikam.choose.getConfig('equipt').title,
            url : Gikam.choose.getConfig('equipt').url + Gikam.param({
                single : true,
                biz : 'orderTask',
                testMethodId : selections[0].testMethodId
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var saveData = [];
                var saveRecordData = [];
                Gikam.each(selections, function(index, item) {
                    saveData.push({
                        id : item.id,
                        equiptId : rows[0].id,
                        analysisUserId : workspace.user.id,
                        analysisUserName : workspace.user.userName,
                    });
                    saveRecordData.push({
                        projId : selections[0].projId,
                        orderId : selections[index].orderId,
                        sampleId : _this.param.id,
                        equiptId : rows[0].id,
                        testId : item.testId,
                        testName : item.testName,
                        userId : workspace.user.id,
                        userName : workspace.user.userName,
                    });
                });
                Gikam.put('/secure/emc/module/mdm/equipt/equipts/use-records', Gikam.getJsonWrapper({}, [ 'emcEquiptUseRecordServiceImpl', saveRecordData ]));

                workspace.window.showMask(true);
                Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', saveData ])).done(function(data) {
                    Gikam.getComp('emc-sampling-plan-equipt-edit-list-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    updateEmp : function() {
        var _this = emcSamplingPlan.editPage;
        var selections = Gikam.getComp('emc-sampling-plan-equipt-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('user-simple').title,
            url : Gikam.choose.getConfig('user-simple').url + Gikam.param({
                single : true,
            }),
            width : '30%',
            onAfterClose : function(formData) {
                if (Gikam.isEmpty(formData)) {
                    return;
                }
                var saveData = [];
                Gikam.each(selections, function(index, item) {
                    saveData.push({
                        id : item.id,
                        analysisUserId : formData[0].id,
                        analysisUserName : formData[0].userName,
                    });
                });
                workspace.window.showMask(true);
                Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', saveData ])).done(function(id) {
                    var equiptGrid = Gikam.getComp('emc-sampling-plan-equipt-edit-list-grid');
                    var gridData = equiptGrid.getSelections().forEach(function(item) {
                        equiptGrid.setData({
                            id : item.id,
                            analysisUserId : formData[0].id,
                            analysisUserName : formData[0].userName,
                        }, false);
                    });
                    equiptGrid.disableAutoSave();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    updatePartakeUserName : function() {
        var _this = emcSamplingPlan.editPage;
        var selections = Gikam.getComp('emc-sampling-plan-equipt-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            width : '30%',
            title : Gikam.choose.getConfig('user-simple').title,
            url : Gikam.choose.getConfig('user-simple').url + Gikam.param({}),
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                var partakeUserId = "";
                var partakeUserName = "";

                Gikam.each(rows, function(index, emp) {
                    if (Gikam.isEmpty(partakeUserId)) {
                        partakeUserId += emp.id;
                    } else {
                        partakeUserId += ',' + emp.id;
                    }

                    if (Gikam.isEmpty(partakeUserName)) {
                        partakeUserName += emp.userName;
                    } else {
                        partakeUserName += ',' + emp.userName;
                    }
                });

                var saveData = selections.map(function(task) {
                    return {
                        id : task.id,
                        partakeUserId : partakeUserId,
                        partakeUserName : partakeUserName
                    };
                });

                workspace.window.showMask(true);
                Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', saveData ])).done(function(id) {
                    var equiptGrid = Gikam.getComp('emc-sampling-plan-equipt-edit-list-grid');
                    var gridData = equiptGrid.getSelections().forEach(function(item) {
                        equiptGrid.setData({
                            id : item.id,
                            partakeUserId : partakeUserId,
                            partakeUserName : partakeUserName
                        }, false);
                    });
                    equiptGrid.disableAutoSave();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    updateAssitEpuipt : function() {
        var _this = emcSamplingPlan.editPage;
        var selections = Gikam.getComp('emc-sampling-plan-equipt-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('equipt').title,
            url : Gikam.choose.getConfig('equipt').url + Gikam.param({}),
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                var assistEquiptId = "";
                var assistEquiptName = "";

                Gikam.each(rows, function(index, equipt) {
                    if (Gikam.isEmpty(assistEquiptId)) {
                        assistEquiptId += equipt.id;
                    } else {
                        assistEquiptId += ',' + equipt.id;
                    }

                    if (Gikam.isEmpty(assistEquiptName)) {
                        assistEquiptName += equipt.equiptName;
                    } else {
                        assistEquiptName += ',' + equipt.equiptName;
                    }
                });

                var saveData = selections.map(function(task) {
                    return {
                        id : task.id,
                        assistEquiptId : assistEquiptId,
                        assistEquiptName : assistEquiptName,
                    };
                });
                workspace.window.showMask(true);
                Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', saveData ])).done(function() {
                    var equiptGrid = Gikam.getComp('emc-sampling-plan-equipt-edit-list-grid');
                    var gridData = equiptGrid.getSelections().forEach(function(item) {
                        equiptGrid.setData({
                            id : item.id,
                            assistEquiptId : assistEquiptId,
                            assistEquiptName : assistEquiptName,
                        }, false);
                    });
                    equiptGrid.disableAutoSave();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    addEpuipt : function() {
        var _this = emcSamplingPlan.editPage;
        var selections = Gikam.getComp('emc-sampling-plan-equipt-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('equipt').title,
            url : Gikam.choose.getConfig('equipt').url + Gikam.param({}),
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var equiptId = "";
                var equiptName = "";
                var factoryNo = "";
                var specification = "";

                Gikam.each(rows, function(index, equipt) {
                    if (Gikam.isEmpty(equiptId)) {
                        equiptId += equipt.id;
                    } else {
                        equiptId += ',' + equipt.id;
                    }

                    if (Gikam.isEmpty(equiptName)) {
                    	equiptName += equipt.equiptName;
                    } else {
                    	equiptName += ',' + equipt.equiptName;
                    }

                    if (Gikam.isEmpty(factoryNo)) {
                    	factoryNo += equipt.factoryNo;
                    } else {
                    	factoryNo += ',' + equipt.factoryNo;
                    }

                    if (Gikam.isEmpty(specification)) {
                    	specification += equipt.specification;
                    } else {
                    	specification += ',' + equipt.specification;
                    }
                });

                var saveData = selections.map(function(task) {
                    return {
                        id : task.id,
                        equiptId : equiptId,
                        equiptName : equiptName,
                        factoryNo : factoryNo,
                        specification : specification,
                        ext$ : {
                        	assignstatus : '1',
                        },
                    };
                });
                workspace.window.showMask(true);
                Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', saveData ])).done(function() {
                    var equiptGrid = Gikam.getComp('emc-sampling-plan-equipt-edit-list-grid');
                    var gridData = equiptGrid.getSelections().forEach(function(item) {
                        equiptGrid.setData({
                            id : item.id,
                            equiptId : equiptId,
                            equiptName : equiptName,
                            factoryNo : factoryNo,
                            specification : specification,
                            ext$ : {
                            	assignstatus : '1',
                            },
                        }, false);
                    });
                    equiptGrid.disableAutoSave();
                    Gikam.getComp('emc-sampling-plan-equipt-edit-list-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    equiptRecord : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-sampling-plan-equipt-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.BPS.MODAL.EQUIPT_RECORD',
            url : '/secure/emc/module/mdm/equipt/equipts/page/use-record-update/' + Gikam.param({
            	sampleId : selections[0].ext$.sampleid,
                readOnly : true
            })
        });
    },

    vocPage : function() {
        var selections = Gikam.getComp('emc-sampling-plan-project-list-grid').getSelections();
    	if (selections.length > 0) {
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.TAB.VOC_PAGE',
                width : '80%',
                height : '80%',
                url : IFM_CONTEXT + '/secure/emc/module/bp/sample/sampling-plans/page/voc-page',
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },



    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-sampling-plan-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '50%',
                items : [ _this.getProjectGridParam() ]
            },
            center : {
                items : [ _this.getGridParam() ]
            },
            east : {
                width : '50%',
                title : Gikam.propI18N('EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.TITLE.ORDER_INFO'),
                collapse : true,
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ this.getOrderGridParam() ]
                    },
                    center : {
                        items : [ this.getResultGridParam() ]
                    }
                } ]
            }
        });
        Gikam.getAsyncComp('emc-sampling-plan-edit-list-container-info-grid').done(function(grid) {
            grid.options.checkOneOnActive = true;
        });

        Gikam.getAsyncComp('emc-sampling-plan-equipt-edit-list-grid').done(function(grid) {
            grid.options.checkOneOnActive = true;
        });
    },

    init : function() {
        this.create();
    }
};
