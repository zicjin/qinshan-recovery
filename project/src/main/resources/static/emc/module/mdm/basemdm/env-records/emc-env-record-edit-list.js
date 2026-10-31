emcEnvRecord.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-env-record-edit-list-grid',
            url : emcEnvRecord.baseUrl + '/queries/raw',
            service : 'emcEnvRecordServiceImpl',
            columns : emcEnvRecord.getEditListColumns(),
	        requestData : {
	            org_filter : '1',
                createdByOrgId_SEQ : workspace.user.orgId,
                createdById_SEQ : workspace.user.id
	        },  
            order : {
            	
            	monitorDate : 'desc',
            	'ext$.recordusertime' : 'desc',
            	room : 'desc'
            },
            page : true,
            pageSize : 100,
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
            },
            deleteFormatter : function(row) {
                return row.qcCategoryId || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add-row',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.BASEMDM.ENVRECORDS.MODAL.ADD',
                        fields : emcEnvRecord.getPreInsertFormFields(),
                        url : emcEnvRecord.baseUrl,
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
                    	if(data !=null && data!=""){
                    		 Gikam.alert("未成功添加！ "+"该日期下已存在 "+data+" 添加的环境记录");
                    	}                   
                        Gikam.getComp('emc-env-record-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-env-record-edit-list-grid').deleteRows(emcEnvRecord.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('envRecord ', Gikam.getComp('emc-env-record-edit-list-grid').getSelections());
                }
            },{
                type : 'button',
                text : '批量新增环境记录',
                icon : 'add-row',
                onClick : function() {
                    _this.batchInputEnv();
                }
            },{
                type : 'button',
                text : '批录入',
                icon : 'add',
                onClick : function() {
                    _this.batchInput();
                }
            }, {
                type : 'form',
                autoSave : false,
                fields : emcEnvRecord.getPrepareShowAllField()
            } ]
        }
    },

    loadDetailPage : function(id) {
        var _this = this;
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BASEMDM.ENVRECORDS.MODAL.DETAIL',
            url : emcEnvRecord.baseUrl + '/page/detail' + Gikam.param({
                id : id
            }),
            width : 900,
            height : 230,
            titleWidth : 100,
            onAfterClose : function() {
                Gikam.getComp('emc-env-record-edit-list-grid').refresh();
            }
        });
    },



    // 批录入环境
    batchInputEnv : function() {
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
    	                        Gikam.post('/secure/emc/module/mdm/basemdm/env-records/action/batch-insert-env', Gikam.getJsonWrapper({}, [ 'pfEnvRecordService',arr])).done(function() {
    	                            }).always(function() {
    	                                workspace.window.closeMask();
    	                                modal.close();
    	                                Gikam.getComp('emc-env-record-edit-list-grid').refresh();
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
            fields: emcEnvRecord.getBatchInputEnvFields(),
            autoSave: false
        });

    },
    
    // 批录入
    batchInput : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-env-record-edit-list-grid').getSelections();
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
                                    item.room = formData.room;
                                    item.ext$.roomid = formData.ext$.roomid;
                                    item.monitorDate = formData.monitorDate;
                                    item.ext$.recordusertime = formData.ext$.recordusertime;
                                    item.temperature = formData.temperature;
                                    item.humidity = formData.humidity;

                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(emcEnvRecord.baseUrl +'/' +selections[0].id, Gikam.getJsonWrapper({}, [ 'pfEnvRecordService', selections ])).done(function (data) {
                                    Gikam.getComp('emc-env-record-edit-list-grid').refresh();
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
            fields: emcEnvRecord.getBatchInputFields(),
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
