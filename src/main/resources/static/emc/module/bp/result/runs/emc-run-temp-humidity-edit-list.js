emcRun.tempHumidityPage = {

    getGridParam : function() {
        var _this = this, 
        columns = emcRun.getTempHumidityListColumns(), 
        requestData = this.param;
        this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-run-temp-humidity-edit-list-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/result/temphum/queries/raw',
            service : 'PfRunTempHumidityServiceImpl',
	        requestData : {
				id_DIFFER : _this.param.id,
				runid_SEQ : _this.param.runId,
	            orgId_SB : workspace.user.ext$.parentorgid,
	        },
            group : {
                fields : [ "ext$.orderno"],
                formatter : function(row) {
                    return row.ext$.orderno;
                }
            },
            page :true,
            columns : columns,
            order : {
                'projno' : 'desc',
                'ext$.orderno' : 'desc'
            },
            toolbar : [ {
                type : 'button',
                text : '生成项目温湿度数据',
                icon : 'save',
                onClick : function () {
                    _this.createDate();
                }
            }, {
                type : 'button',
                text : '同步温湿度',
                icon : 'select',
                onClick : function() {
                    _this.synTempHumidity();
                }
            }, {
                type : 'button',
                text : '同步全部温湿度',
                icon : 'select',
                onClick : function() {
                    _this.synAllTempHumidity();
                }
            },{
                type : 'button',
                text : '批录入',
                icon : 'add',
                onClick : function() {
                    _this.batchInputTempHum();
                }
            },{
                type : 'button',
                text : '金属批录入',
                icon : 'add',
                onClick : function() {
                    _this.batchInputOrderTempHum();
                }
            },{
                type : 'button',
                text : '有机批录入',
                icon : 'add',
                onClick : function() {
                    _this.batchInputOrderForMetalTempHum();
                }
            },{
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    _this.tempDelete();
                }    
            }  ]
        }
    },


    
    synTempHumidity() {
        var _this = this;
        var selections = Gikam.getComp('emc-run-temp-humidity-edit-list-grid').getSelections();
        //同步该项目测试下的温湿度信息
        var url = Gikam.printf(IFM_CONTEXT + '/secure/emc/module/bp/result/temphum/action/syn-temp-humidity');
        Gikam.postText(url, Gikam.getJsonWrapper(null, [ '', selections ])).done(function(data) {
        	Gikam.toast('同步成功', 3, 'top');
            Gikam.getComp('emc-run-temp-humidity-edit-list-grid').refresh();
        }).always(function() {}); 	
    },
    
    synAllTempHumidity() {
        var _this = this;
        var selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        var runId = selections[0].id;
        //同步该项目测试下的温湿度信息
        var url = Gikam.printf(IFM_CONTEXT + '/secure/emc/module/bp/result/temphum/action/syn-all-temp-humidity');
        Gikam.postText(url, Gikam.getJsonWrapper({
            f : {
                runId : runId
            }
        }, [])).done(function(data) {
        	Gikam.toast('同步成功', 3, 'top');
            Gikam.getComp('emc-run-temp-humidity-edit-list-grid').refresh();
        }).always(function() {}); 	
    },
    
    
    tempDelete : function () {
        Gikam.getComp('emc-run-temp-humidity-edit-list-grid').deleteRows(IFM_CONTEXT + '/secure/emc/module/bp/result/temphum').done(function() {
            Gikam.getComp('emc-run-temp-humidity-edit-list-grid').refresh();
        });
    },
    
    
        // 批录入样品修改
    batchInputTempHum : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-run-temp-humidity-edit-list-grid').getSelections();
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
                                    item.temperature = formData.temperature;
                                    item.humidity = formData.humidity;
                                    item.beginDate = formData.beginDate;
                                    item.endDate = formData.endDate;
                                    item.beginDateOne = formData.beginDateOne;
                                    item.endDateOne = formData.endDateOne;
                                    item.beginDateTwo = formData.beginDatetwo;
                                    item.endDateTwo = formData.endDateTwo;
                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/result/temphum/' + activeRow.id, Gikam.getJsonWrapper({}, [ 'pfRunTempHumidityService', selections ])).done(function (data) {
                                    Gikam.getComp('emc-run-temp-humidity-edit-list-grid').refresh();
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
            fields: emcRun.getBatchInputTempHumColumns(),
            autoSave: false
        });

    },
    
    createDate : function() {
        var rows = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        var useUrl = Gikam.printf(emcRun.baseUrl+'/action/create-temp-humidity');
        Gikam.postText(useUrl, Gikam.getJsonWrapper({
            f : {
                runId : rows[0].id
            }
        })).done(function(data) {
            Gikam.getComp('emc-run-temp-humidity-edit-list-grid').refresh();
        }).always(function() {
            Gikam.getComp('emc-run-temp-humidity-edit-list-grid').refresh();
        }); 	
    },

    batchInputOrderTempHum : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        var id = selections[0].id;
        
        Gikam.create('modal', {
            width : '50%',
            title : '样品',
            url : emcRun.baseUrl + '/page/run-temp-order' + Gikam.param({
                runId : id
            }),
            onAfterClose : function(chooseData) {
                if (Gikam.isEmpty(chooseData)) {
                    workspace.window.closeMask();
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
                                        Gikam.getLastModal().window.showMask(true);
                                        var arr = [];
                                        arr.push(formData);
                                        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/result/temphum/action/batch-update-temp-order', Gikam.getJsonWrapper({},[ 'pfRunTempHumidityServiceImpl', arr ], [ 'pfOrderTaskServiceImpl', chooseData ])).done(function (data) {
                                            Gikam.getComp('emc-run-temp-humidity-edit-list-grid').refresh();
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
                    fields: emcRun.getBatchInputTempHumColumns(),
                    autoSave: false
                });
                
            }
        });
        

    },
    
    batchInputOrderForMetalTempHum : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        var id = selections[0].id;
        
        Gikam.create('modal', {
            width : '50%',
            title : '样品',
            url : emcRun.baseUrl + '/page/run-temp-order-formetal' + Gikam.param({
                runId : id
            }),
            onAfterClose : function(chooseData) {
                if (Gikam.isEmpty(chooseData)) {
                    workspace.window.closeMask();
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
                                        Gikam.getLastModal().window.showMask(true);
                                        var arr = [];
                                        arr.push(formData);
                                        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/result/temphum/action/batch-update-temp-order', Gikam.getJsonWrapper({},[ 'pfRunTempHumidityServiceImpl', arr ], [ 'pfOrderTaskServiceImpl', chooseData ])).done(function (data) {
                                            Gikam.getComp('emc-run-temp-humidity-edit-list-grid').refresh();
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
                    fields: emcRun.getBatchInputTempHumColumns(),
                    autoSave: false
                });
                
            }
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