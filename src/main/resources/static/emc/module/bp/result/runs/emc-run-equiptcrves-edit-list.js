emcRunEquiptCrves.editPage = {

    getGridParam : function() {
        var selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        var _this = this;
        var runId = selections[0].id;
        var myDate = new Date();
    	var todayDate = myDate.toLocaleDateString();
        return {
            type : 'grid',
            id : 'emc-run-equiptcrves-edit-list-grid',
            service : 'PfAnalyticalEquiptCrvesServiceImpl',
            url : '/secure/emc/module/bp/equipt-crves/queries/raw',
            columns : emcRunEquiptCrves.getRunEquiptCrvesEditListColumns(),
            filterOpen : true,
            filter : true,
            requestData : {
                runId_SEQ : runId,
                createdByOrgId_SB : workspace.user.orgId
            },
            onRendered : function() {
                this.hideColumns([ 'addDate' ]);
            },
            onCellClick : function(field, row) {
                 field === 'solId' && _this.loadDetailPage(row.solId);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.getComp('emc-run-equiptcrves-edit-list-grid').insert(emcRunEquiptCrves.baseUrl+'/insert', [ {
                        runId: runId,
                    } ]);

                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-run-equiptcrves-edit-list-grid').deleteRows(emcRunEquiptCrves.baseUrl)
                },
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEW',
                icon : 'browse',
                onClick : function() {
                    Gikam.viewReport('equiptCrves', Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections());
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.EQUIPTCRVES.BUTTON.HISTORYCRVES',
                icon : 'browse',
                onClick : function() {
                    _this.historyCrves();

                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.EQUIPTCRVES.BUTTON.COPYNOWCRVES',
                icon : 'add',
                onClick : function() {
                    _this.copyNowCrves();

                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BATCH_INPUT',
                icon : 'add',
                onClick : function() {
                    _this.batchInput();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.EQUIPTCRVES.TITLE.SET_DEFAULT_CURVE',
                icon : 'edit',
                onClick : function() {
                    _this.setDefaultCurve();
                }
            } ],
        }
    },


    historyCrves : function(solId) {
        var selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        var _this = this;
        var runId = selections[0].id;
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.EQUIPTCRVES.MODAL.HISTORY_EQUIPT_CRVES',
            url : '/secure/emc/module/bp/result/runs/page/history-equipt-crves',
            onAfterClose : function() {
                Gikam.getComp('emc-run-equiptcrves-edit-list-grid').refresh();

            },
            height: '95%'
        })
    },

    loadDetailPage : function(solId) {
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.EQUIPTCRVES.MODAL.ANALYSIS_EQUIPT_CRVES',
            url : '/secure/emc/module/bp/result/runs/page/equipt-crves-detail' + Gikam.param({
                solId_SEQ : solId
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-run-equiptcrves-edit-list-grid').refresh();
            },
            height: '95%'
        })
    },

    copyNowCrves : function() {
	    var _this = this;
        var selections = Gikam.getComp('emc-run-equiptcrves-edit-list-grid').getSelections();
        var selectionRun = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        var runId = selectionRun[0].id;
	    if (Gikam.isEmpty(selections)) {
	        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
	        return;
	    }
        var modal = Gikam.create('modal', {
	        title: "EMC.MODULE.BP.EQUIPTCRVES.MODAL.COPY_NOW_CRVES",
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
                            var formData = form.getData();
	                    	var crvesNums =  formData.crvesNums;
	                        Gikam.postText(emcRunEquiptCrves.baseUrl + '/copy-now-crves', Gikam.getJsonWrapper({
	                            f :{
	                            	runId : runId,
	                            	crvesNums : crvesNums
	                            }}, [ 'PfAnalyticalEquiptCrvesService', selections])).done(function() {
	                                Gikam.getComp('emc-run-equiptcrves-edit-list-grid').refresh();
	                                Gikam.getLastModal().close();
	                            }).always(function() {});

	                        workspace.window.closeMask();
	                        Gikam.getComp('emc-run-equiptcrves-edit-list-grid').refresh();
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
        fields: emcRunEquiptCrves.getCrvesNumFields(),
        autoSave: false
    });
    },

    // 批录入
    batchInput : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-run-equiptcrves-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
            width : 300,
            height : 400,
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
                                    item.sdReserveConcentration = formData.sdReserveConcentration;
                                    item.sdUseFluidConcentration = formData.sdUseFluidConcentration;
                                    item.curveFactorFixedTo = formData.curveFactorFixedTo;
                                    item.adder = formData.adder;
                                    item.ext$.internalnumber = formData.ext$.internalnumber;
                                    item.testMethodId = formData.testMethodId;
                                    item.testName = formData.testName;
                                    item.expirationTime = formData.expirationTime;
                                    item.ext$.sdsolutionvolumeunit = formData.ext$.sdsolutionvolumeunit;
                                    item.ext$.sdconcentrationunit = formData.ext$.sdconcentrationunit;
                                    item.setDate = formData.setDate;
                                    item.methodId = formData.methodId;
                                    item.methodName = formData.methodName;
                                    item.ext$.equiptid = formData.ext$.equiptid;
                                    item.ext$.equiptname = formData.ext$.equiptname;
                                    item.itemGroup = formData.itemGroup;
                                    item.ext$.aslope = formData.ext$.aslope;
                                    item.ext$.bintercept = formData.ext$.bintercept;
                                    item.ext$.rrelatedfactor = formData.ext$.rrelatedfactor;


                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/equipt-crves/' + selections[0].id, Gikam.getJsonWrapper({}, [ 'PfAnalyticalEquiptCrvesService', selections ])).done(function (data) {
                                    Gikam.getComp('emc-run-equiptcrves-edit-list-grid').refresh();
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
            fields: emcRunEquiptCrves.getBatchInputFields(),
            autoSave: false
        });

    },

    setDefaultCurve : function() {
        let selections = Gikam.getComp('emc-run-equiptcrves-edit-list-grid').getData();
        if (!Gikam.isEmpty(selections)) {
            Gikam.alert('EMC.MODULE.BP.EQUIPTCRVES.TIP.CAN_NOT_SET_DEFAULT_CURVE');
            return;
        }

        var selectedRuns = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();

        Gikam.confirm('EMC.MODULE.BP.EQUIPTCRVES.TITLE.SET_DEFAULT_CURVE', 'EMC.MODULE.BP.EQUIPTCRVES.TIP.CONFRIM_SET_DEFAULT_CURVE', function() {
            Gikam.getLastModal().window.showMask(true);
            Gikam.postText(emcRunEquiptCrves.baseUrl + '/set-default-curve', Gikam.getJsonWrapper({ runId : selectedRuns[0].id }, [  ])).done(function(r) {
                Gikam.getComp('emc-run-equiptcrves-edit-list-grid').refresh();
                if(r!=null&&r!=""){
                    Gikam.alert("批下的测试"+r+"未设置曲线成功，请检查曲线配置");
                }else{
                    Gikam.alert('EMC.MODULE.BP.EQUIPTCRVES.TIP.SET_DONE');
                }
            }).always(function () {
                Gikam.getLastModal().window.closeMask();
            });
        })
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-run-equiptcrves-edit-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    },


};
