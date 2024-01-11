emcRunEquiptCrves.historyPage = {

    getGridParam : function() {
        var selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-run-history-equiptcrves-list-grid',
            url : '/secure/emc/module/bp/equipt-crves/queries/raw',
            service : 'PfAnalyticalEquiptCrvesServiceImpl',
            columns : emcRunEquiptCrves.getRunEquiptCrvesHistoryEditListColumns(),
            filterOpen : true,
            filter : true,

            page :true,
            onCellClick : function(field, row) {
                 field === 'solId' && _this.loadDetailPage(row.solId);
            },
            onRendered : function() {
                this.hideColumns([ 'addDate' ]);
            },
            requestData : {
                createdByOrgId_SEQ : workspace.user.orgId,
                runId_IN: ',',
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.getComp('emc-run-equiptcrves-edit-list-grid').insert(emcRunEquiptCrves.baseUrl+'/insert_config_curve', []).done(function () {
                        Gikam.getComp('emc-run-history-equiptcrves-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-run-history-equiptcrves-list-grid').deleteRows(emcRunEquiptCrves.baseUrl)
                },
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'edit',
                onClick : function() {
                    _this.confirmHistoryCrves();
                }
            },{
                type : 'button',
                text : 'GIKAM.BUTTON.BATCH_INPUT',
                icon : 'add',
                onClick : function() {
                    _this.batchInput();
                }

            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.EQUIPTCRVES.BUTTON.COPYNOWCRVES',
                icon : 'add',
                onClick : function() {
                    _this.copyConfigCrves();

                }
            }, {
                type : 'dropDownMenu',
                text : '废弃曲线',
                items : [ {
                    type : 'button',
                    text : '废弃',
                    icon : 'add',
                    onClick : function() {
                        _this.abandonCurve();
                    }
                }, {
                    type : 'button',
                    text : '取消废弃',
                    icon : 'remove',
                    onClick : function() {
                        _this.unabandonCurve();
                    }
                } ]
            }, {
                type : 'form',
                autoSave : false,
                fields : emcRunEquiptCrves.getPrepareShowAllField()
            },],

        }
    },

    confirmHistoryCrves : function() {
            var selections = Gikam.getComp('emc-run-history-equiptcrves-list-grid').getSelections();
            var selectionRun = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
            var runId = selectionRun[0].id;
            if (selections.length >= 1) {
            	 Gikam.postText(emcRunEquiptCrves.baseUrl + '/compare-end-date', Gikam.getJsonWrapper({
                     f :{
                     	runId : runId
                     }}, [ 'PfAnalyticalEquiptCrvesService', selections])).done(function(data) {
                    	 if(data == 'false'){
                    		 alert("曲线过期日期早于批下最新分析结束日期");
                    		 return;
                    	 }else{
                    		 Gikam.postText(emcRunEquiptCrves.baseUrl + '/copy-crves', Gikam.getJsonWrapper({
                                 f :{
                                 	runId : runId
                                 }}, [ 'PfAnalyticalEquiptCrvesService', selections])).done(function() {
                                     Gikam.getLastModal().close();
                                     Gikam.getComp('emc-run-equiptcrves-edit-list-grid').refresh();
                                 }).always(function() {});
                    	 }
                     }).always(function() {});


            } else {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                return;
            };
            workspace.window.closeMask();
            Gikam.getComp('emc-run-equiptcrves-edit-list-grid').refresh();
    },

    loadDetailPage : function(solId) {
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.EQUIPTCRVES.MODAL.ANALYSIS_EQUIPT_CRVES',
            url : '/secure/emc/module/bp/result/runs/page/history-equipt-crves-detail' + Gikam.param({
                solId_SEQ : solId
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-run-history-equiptcrves-list-grid').refresh();
            },
            height: '95%'
        })
    },

    abandonCurve : function() {
        var selections = Gikam.getComp('emc-run-history-equiptcrves-list-grid').getSelections();
        selections.forEach(function (item) {
            item.isAbandon = "1";
        })
        Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/equipt-crves/' + selections[0].id, Gikam.getJsonWrapper({}, [ 'PfAnalyticalEquiptCrvesService', selections ])).done(function (data) {
            Gikam.getComp('emc-run-history-equiptcrves-list-grid').refresh();
        }).always(function () {
            Gikam.getLastModal().window.closeMask();
            modal.close();
        });
    },

    unabandonCurve : function() {
        var selections = Gikam.getComp('emc-run-history-equiptcrves-list-grid').getSelections();
        selections.forEach(function (item) {
            item.isAbandon = "0";
        })
        Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/equipt-crves/' + selections[0].id, Gikam.getJsonWrapper({}, [ 'PfAnalyticalEquiptCrvesService', selections ])).done(function (data) {
            Gikam.getComp('emc-run-history-equiptcrves-list-grid').refresh();
        }).always(function () {
            Gikam.getLastModal().window.closeMask();
            modal.close();
        });
    },

    copyConfigCrves : function() {
	    var _this = this;
        var selections = Gikam.getComp('emc-run-history-equiptcrves-list-grid').getSelections();
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
	                        Gikam.postText(emcRunEquiptCrves.baseUrl + '/copy-config-crves', Gikam.getJsonWrapper({
	                            f :{
	                            	crvesNums : crvesNums
	                            }}, [ 'PfAnalyticalEquiptCrvesService', selections])).done(function() {
	                                Gikam.getComp('emc-run-history-equiptcrves-list-grid').refresh();
	                                Gikam.getLastModal().close();
	                            }).always(function() {});

	                        workspace.window.closeMask();
	                        Gikam.getComp('emc-run-history-equiptcrves-list-grid').refresh();
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
        var selections = Gikam.getComp('emc-run-history-equiptcrves-list-grid').getSelections();
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
                                    item.ext$.aslope = formData.ext$.aslope;
                                    item.ext$.bintercept = formData.ext$.bintercept;
                                    item.ext$.rrelatedfactor = formData.ext$.rrelatedfactor;
                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/equipt-crves/' + selections[0].id, Gikam.getJsonWrapper({}, [ 'PfAnalyticalEquiptCrvesService', selections ])).done(function (data) {
                                    Gikam.getComp('emc-run-history-equiptcrves-list-grid').refresh();
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
            fields: emcRunEquiptCrves.getHistoryBatchInputFields(),
            autoSave: false
        });

    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-run-equiptcrves-edit-list-layout1',
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
