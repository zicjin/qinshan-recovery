emcEquiptTemp.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-equipt-temp-edit-list-grid',
            url : emcEquiptTemp.baseUrl + '/queries/raw',
            service : 'PfEquiptTempServiceImpl',
            columns : emcEquiptTemp.getEditListColumns(),
	        requestData : {
	            orgId_SB : workspace.user.ext$.parentorgid
	        },  
            order : {
            	recordDate : 'desc'
            },
            page : true,
            pageSize : 100,
            onCellClick : function(field, row) {},
            deleteFormatter : function(row) {
                return  row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add-row',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : '添加仪器温度',
                        fields : emcEquiptTemp.getPreInsertFormFields(),
                        url : emcEquiptTemp.baseUrl,
                        width : 800,
                        height : 230,
                        titleWidth : 100,
                        columns : 3,
                        onFormRendered : function(form) {
                            setTimeout(() => {
                                var orgId = workspace.user.ext$.parentorgid;
                                this.setData({orgId : orgId},false);
                            }, 10)
                        }
                    }).done(function(data) {
                    	console.log(data);
                    	if(data == 1){
                   		 Gikam.alert("未成功添加！ "+"该日期下已存在添加的仪器温湿度记录");
                   	}                   
                       Gikam.getComp('emc-equipt-temp-edit-list-grid').refresh();
                   });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-equipt-temp-edit-list-grid').deleteRows(emcEquiptTemp.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('equiptTemp ', Gikam.getComp('emc-equipt-temp-edit-list-grid').getSelections());
                }
            }/*,{
                type : 'button',
                text : '批量新增仪器温度',
                icon : 'add-row',
                onClick : function() {
                    _this.batchInputEquiptTemp();
                }
            }*/,{
                type : 'button',
                text : '批录入',
                icon : 'add',
                onClick : function() {
                    _this.batchInput();
                }
            }, {
                type : 'button',
                text : '批量新增仪器温度',
                icon : 'add-row',
                onClick : function() {
                    _this.batchInsertTemp();
                }
            } ]
        }
    },

    
    // 批录入
    batchInput : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-equipt-temp-edit-list-grid').getSelections();
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
                                	item.equiptId = formData.equiptId;
                                	item.ext$.equiptname = formData.ext$.equiptname;
                                	item.recordDate = formData.recordDate;
                                    item.temperature = formData.temperature;
                                    item.section = formData.section;
                                    item.remark = formData.remark;
                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(emcEquiptTemp.baseUrl +'/' +selections[0].id, Gikam.getJsonWrapper({}, [ 'pfEquiptTempServiceImpl', selections ])).done(function (data) {
                                    Gikam.getComp('emc-equipt-temp-edit-list-grid').refresh();
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
            fields: emcEquiptTemp.getBatchInputFields(),
            autoSave: false
        });

    },
    

    // 批插入环境温度
    batchInsertTemp : function() {
        var _this = this;

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
                                var arr = [];
                                arr.push(formData);
                                workspace.window.showMask(true);
    	                        Gikam.post('/secure/emc/module/mdm/basemdm/equipt-temp/action/batch-insert-temp', Gikam.getJsonWrapper({}, [ 'pfEquiptTempService',arr])).done(function() {
    	                            }).always(function() {
    	                                workspace.window.closeMask();
    	                                modal.close();
    	                                Gikam.getComp('emc-equipt-temp-edit-list-grid').refresh();
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
            fields: emcEquiptTemp.getBatchInsertTempFields(),
            autoSave: false
        });

    },
    
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-env-record-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },
    
    init : function() {
        this.create();
    }
};
