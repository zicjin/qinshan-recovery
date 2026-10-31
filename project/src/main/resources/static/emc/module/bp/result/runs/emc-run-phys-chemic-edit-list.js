emcRun.physChemicPage = {

    getGridParam : function() {
        var _this = this, 
        columns = emcRun.getPhysChemicListColumns(), 
        requestData = this.param;
        this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-run-phys-chemic-edit-list-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/result/physchemic/queries/raw',
            service : 'PfPhysChemicService',
	        requestData : {
				runid_SEQ : _this.param.runId,
	        },
            page :true,
            columns : columns,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/result/physchemic/insert', Gikam.getJsonWrapper({ runId : _this.param.runId}, [  ])).done(function() {
                        Gikam.getComp('emc-run-phys-chemic-edit-list-grid').refresh();
                    }).always(function () {
                    });

                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    _this.tempDelete();
                }    
            },{
                type : 'button',
                text : '批录入',
                icon : 'add',
                onClick : function() {
                    _this.batchInput();
                }
            }  ]
        }
    },

    // 批录入样品修改
    batchInput : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-run-phys-chemic-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var activeRow = selections[0];
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
                                    item.waterTemp = formData.waterTemp;
                                    item.stdSol = formData.stdSol;
                                    item.measuredValue = formData.measuredValue;
                                    item.calibrationValues = formData.calibrationValues;
                                    item.zeroOxygenCalibration = formData.zeroOxygenCalibration;
                                    item.fullnessCalibration = formData.fullnessCalibration;
                                    item.remark = formData.remark;
                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/result/physchemic/' + activeRow.id, Gikam.getJsonWrapper({}, [ 'PfPhysChemicService', selections ])).done(function (data) {
                                    Gikam.getComp('emc-run-phys-chemic-edit-list-grid').refresh();
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
                    }  ]
                }]
            }
        });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            columns: 1,
            titleWidth: 100,
            fields: emcRun.getBatchInputPhysChemicColumns(),
            autoSave: false
        });

    },
    
    
    tempDelete : function () {
        Gikam.getComp('emc-run-phys-chemic-edit-list-grid').deleteRows(IFM_CONTEXT + '/secure/emc/module/bp/result/physchemic').done(function() {
            Gikam.getComp('emc-run-phys-chemic-edit-list-grid').refresh();
        });
    },
    
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [  this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};