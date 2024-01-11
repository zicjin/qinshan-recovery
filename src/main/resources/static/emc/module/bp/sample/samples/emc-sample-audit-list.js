emcSample.auditPage = {

    getProjectSampleGridParam : function() {
        var _this = this;
        _this.param.orgId_SB = workspace.user.ext$.parentorgid;
        return {
            type : 'grid',
            id : 'emc-project-sample-list-grid',
            filterOpen : false,
            page : true,
            pageSize : 100,
            url : emcSample.baseUrl + '/queries/tasks',
            columns : emcSample.getProjectAuditSampleColumns(),
            requestData : _this.param,
            service : 'emcSampleServiceImpl',
            onRendered : function() {
                var formData = Gikam.getComp('emc-order-task-result-edit-list-button-group-by-test');
                var status = _this.param.bpmn_statusCode;
                if (formData !== undefined) {
                    formData.clickHandle();
                };
                if(status === 'mutual'){
                    this.hideColumns([ 'ext$.auditdate' ]);
                }else{
                    this.hideColumns([ 'ext$.reviewdate' ]);
                }
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
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_SAMPLE',
                icon : 'print',
                onClick : function() {
                    var selections = Gikam.getComp('emc-project-sample-list-grid').getSelections();
                    if (selections.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    Gikam.viewReport(selections[0].reportConfigLineId, selections[0]);
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.VIEW_CALIBRATION_REPORT',
                icon : 'print',
                onClick : function() {
                    var selections = Gikam.getComp('emc-project-sample-list-grid').getSelections();
                    if (selections.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    Gikam.viewReport(selections[0].caliReportLineId, selections[0]);
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SAMPLING_EQUIPMENT',
                icon : 'modify',
                hidden : false,
                onClick : function() {
                    var selections = Gikam.getComp('emc-project-sample-list-grid').getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }

                    var ids = selections.map(row => row.id).join();

                    Gikam.create('modal', {
                        width : '60%',
                        title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SAMPLING_EQUIPMENT',
                        url : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/page/sampling-equipt' + Gikam.param({
                            ids : ids,
                            readonly : true
                        })
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELIVERY.PREVIEW',
                icon : 'print',
                onClick : function() {
                    var selections = Gikam.getComp('emc-project-sample-list-grid').getSelections();
                    if (selections.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    Gikam.viewReport(selections[0].receiveReportLineId, selections[0]);
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.ANNEX',
                icon : 'upload',
                onClick : function() {
                    _this.uploadSampleFile();
                }
            } ],
        }
    },

    uploadSampleFile : function() {
        var _this = this
        var rows = Gikam.getComp('emc-project-sample-list-grid').getSelections();
        if (rows && rows.length === 1) {
            Gikam.create('modal', {
                width : 800,
                height : 500,
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ANNEX',
                url : emcSample.baseUrl + '/page/sample-annex-list?id=' + rows[0].id
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    getAuditedSampleDataGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-project-sample-list-grid',
            filterOpen : false,
            page : false,
            url : emcSample.baseUrl + '/queries/undoable',
            columns : emcSample.getAuditedSampleDataColumns(),
            requestData : {
                targetType : 'auditedData',
	            orgId_SB : workspace.user.ext$.parentorgid
            }
        }
    },

/*    pass : function() {
        var grid = Gikam.getComp('emc-project-sample-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : emcSample
        }).done(function() {
            grid.refresh();
        });
    },*/

    pass : function() {
        var grid = Gikam.getComp('emc-project-sample-list-grid');
        var url = Gikam.printf(IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/action/pass-reminds');
        var that = this;
        var selections =  grid.getSelections();
        //至少选择
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.postText(url,Gikam.getJsonWrapper({},[ '', selections ])).done(function(message) {
            console.log(message);
            if(Gikam.isEmpty(message)){
                if (grid.validate(selections)) {
                    Gikam.create('workflow').pass({
                        data : grid.getSelections(),
                        pageObject : emcSample
                    }).done(function() {
                        grid.refresh();
                    });
                }
            }else{
                Gikam.confirm('EMC.MODULE.BP.FOLDER.FOLDERS.TIP.CONTINUESUBMITTING', message, function() {
                    if (grid.validate(selections)) {
                        Gikam.create('workflow').pass({
                            data : grid.getSelections(),
                            pageObject : emcSample
                        }).done(function() {
                            grid.refresh();
                        });
                    }
                })

            }
        })

    },

    reject : function() {
        var grid = Gikam.getComp('emc-project-sample-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcSample
        }).done(function() {
            grid.refresh();
        });
    },

    loadDataInputPage : function(id) {
        var _this = this;
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.DATA_INPUT',
            url : emcSample.baseUrl + '/page/data-input-detail' + Gikam.param({
                id : id,
                // readonly : true,
                pageSource : 'report'
            }),
        });
    },

    loadCalibrationPage : function(id) {
        Gikam.create('modal', {
            width : '80%',
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.INSTRUMENT_ADJUSTMENT',
            url : IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipt-calibrations/page/detail-list' + Gikam.param({
                id : id,
            }),
        });
    },

    createSample : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-project-audit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getProjectSampleGridParam() ]
            }
        });
    },

    createAuditedSampleData : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-project-audit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getAuditedSampleDataGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        // 去掉创建人不能互审的限制
        //param.createdById_DIFFER = workspace.user.id;
        if (param.category === 'auditedSampleData') {
            this.createAuditedSampleData();
        } else {
            this.createSample();
        }
    }
};
