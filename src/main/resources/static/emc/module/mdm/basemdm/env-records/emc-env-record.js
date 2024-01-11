var emcEnvRecord = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/env-records',

    getPreInsertFormFields : function() {
        return [ {
            checkbox : true
        }, {
            field : 'room',
            title : 'T_EMC_ENV_RECORD.ROOM',
            preInsert : true,
            validators : [ 'strLength[0,36]' ],
            type : 'choose',
            category : 'room',
            single : true,
            onAfterChoose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                this.setData({
                    index : index,
                    room : rows[0].roomNo,
                    standardTemperature : rows[0].standardTemperature,
                    standardHumidity : rows[0].standardHumidity,
                    thermometerNo : rows[0].thermometerNo,
                    ext$ : {
                        roomid : rows[0].id,
                    }
                });
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    room : '',
                    standardTemperature : '',
                    standardHumidity : '',
                    thermometerNo : '',
                    ext$ : {
                        roomid : '',
                    }
                });
            }
        }, {
            field : 'monitorDate',
            title : 'T_EMC_ENV_RECORD.MONITORDATE',
            preInsert : true,
            type : 'date'
        },{
            field : 'ext$.recordusertime',
            title : 'T_EMC_ENV_RECORD.RECORDUSERTIME',
            preInsert : true,
            type : 'time',
        }, {
            field : 'temperature',
            title : 'T_EMC_ENV_RECORD.TEMPERATURE',
            validators : [ 'strLength[0,36]' ]
        }, {
            field : 'humidity',
            title : 'T_EMC_ENV_RECORD.HUMIDITY',
            validators : [ 'strLength[0,36]' ]
        }/*, {
            field : 'ext$.pmtemperature',
            title : 'T_EMC_ENV_RECORD.PMTEMPERATURE',
        }, {
            field : 'ext$.pmhumidity',
            title : 'T_EMC_ENV_RECORD.PMHUMIDITY',
        }, {
            field : 'recordUserName',
            title : 'T_EMC_ENV_RECORD.RECORDUSERNAME',
            preInsert : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onBeforeChoose : function() {
                return {
    	            orgId_SEQ : workspace.user.orgId,
                    status_DIFFER : 'locked'
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    var grid = this;
                    grid.setData({
                        index : index,
                        recordUserId : rows[0].id,
                        recordUserName: rows[0].userName
                    });
                }
            },
            validators : [ 'strLength[0,60]' ]
        }*/,{
            field : 'ext$.section',
            title : 'T_EMC_ENV_RECORD.SECTION',
            editor : true,
            type : 'select',
            search : true,
            preInsert : true,
            category : 'sys',
            param : {
                codeCategoryId : 'section'
            },
        
        } ];
    },

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'id',
            title : 'T_EMC_ENV_RECORD.ID',
            type : 'link'
        }, {
            field : 'ext$.roomno',
            title : 'T_EMC_ENV_RECORD.ROOM',
        }, {
            field : 'standardTemperature',
            title : 'T_EMC_ENV_RECORD.STANDARD_TEMPERATURE',
        }, {
            field : 'standardHumidity',
            title : 'T_EMC_ENV_RECORD.STANDARD_HUMIDITY',
        }, {
            field : 'thermometerNo',
            title : 'T_EMC_ENV_RECORD.THERMOMETERNO',
        }, {
            field : 'monitorDate',
            title : 'T_EMC_ENV_RECORD.MONITORDATE',
            width : 120,
            editor : true,
            type : 'date',
            validators : [ 'notEmpty']
        }, {
            field : 'temperature',
            title : 'T_EMC_ENV_RECORD.TEMPERATURE',
            width : 80,
            editor : true,
            validators : ['strLength[0,36]' ]
        }, {
            field : 'humidity',
            title : 'T_EMC_ENV_RECORD.HUMIDITY',
            width : 80,
            editor : true,
            validators : [ 'strLength[0,36]' ]
        }/*, {
            field : 'ext$.pmtemperature',
            title : 'T_EMC_ENV_RECORD.PMTEMPERATURE',
            editor : true,
            validators : [ 'strLength[0,36]' ]
        }, {
            field : 'ext$.pmhumidity',
            title : 'T_EMC_ENV_RECORD.PMHUMIDITY',
            editor : true,
            validators : [ 'strLength[0,36]' ]
        }*/, {
            field : 'recordUserName',
            title : 'T_EMC_ENV_RECORD.RECORDUSERNAME',
            width : 90,
            editor : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onBeforeChoose : function() {
                return {
    	            orgId_SEQ : workspace.user.orgId,
                    status_DIFFER : 'locked'
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    var grid = this;
                    grid.setData({
                        index : index,
                        recordUserId : rows[0].id,
                        recordUserName: rows[0].userName
                    });
                }
            },
            validators : [ 'notEmpty','strLength[0,60]' ]
        },{
            field : 'ext$.recordusertime',
            title : 'T_EMC_ENV_RECORD.RECORDUSERTIME',
            width : 120,
            editor : true,
            type : 'time',
        }, {
            field : 'remark',
            title : 'T_EMC_ENV_RECORD.REMARK',
            editor : true,
            validators : [ 'strLength[0, 120]' ]
        },{
            field : 'ext$.section',
            title : 'T_EMC_ENV_RECORD.SECTION',
            editor : true,
            width : 100,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'section'
            },        
        } ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'ext$.roomno',
            title : 'T_EMC_ENV_RECORD.ROOM',
        }, {
            field : 'monitorDate',
            title : 'T_EMC_ENV_RECORD.MONITORDATE',
            type : 'date',
            width : 100,
        }, {
            field : 'temperature',
            title : 'T_EMC_ENV_RECORD.TEMPERATURE',
            width : 80
        }, {
            field : 'humidity',
            title : 'T_EMC_ENV_RECORD.HUMIDITY',
            width : 80
        }/*, {
            field : 'ext$.pmtemperature',
            title : 'T_EMC_ENV_RECORD.PMTEMPERATURE',
            editor : true,
        }, {
            field : 'ext$.pmhumidity',
            title : 'T_EMC_ENV_RECORD.PMHUMIDITY',
            editor : true,
        }*/, {
            field : 'ext$.section',
            title : 'T_EMC_ENV_RECORD.SECTION',
            editor : true,
            width : 100,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'section'
            },  
        } ];
    },

    getDetailFormColumns : function(){
        return [ {
            field : 'room',
            title : 'T_EMC_ENV_RECORD.ROOM',
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,36]' ]
        }, {
            field : 'monitorDate',
            title : 'T_EMC_ENV_RECORD.MONITORDATE',
            editor : true,
            type : 'date',
            validators : [ 'notEmpty']
        }, {
            field : 'temperature',
            title : 'T_EMC_ENV_RECORD.TEMPERATURE',
            editor : true,
            validators : [  'strLength[0,36]' ]
        }, {
            field : 'humidity',
            title : 'T_EMC_ENV_RECORD.HUMIDITY',
            editor : true,
            validators : [ 'strLength[0,36]' ]
        }/*, {
            field : 'ext$.pmtemperature',
            title : 'T_EMC_ENV_RECORD.PMTEMPERATURE',
            editor : true,
        }, {
            field : 'ext$.pmhumidity',
            title : 'T_EMC_ENV_RECORD.PMHUMIDITY',
            editor : true,
        }*/, {
            field : 'recordUserName',
            title : 'T_EMC_ENV_RECORD.RECORDUSERNAME',
            editor : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onBeforeChoose : function() {
                return {
    	            orgId_SEQ : workspace.user.orgId,
                    status_DIFFER : 'locked'
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    var grid = this;
                    grid.setData({
                        index : index,
                        recordUserId : rows[0].id,
                        recordUserName: rows[0].userName
                    });
                }
            },
            validators : [ 'notEmpty','strLength[0,60]' ]
        },{
            field : 'ext$.section',
            title : 'T_EMC_ENV_RECORD.SECTION',
            editor : true,
            width : 100,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'section'
            },
        
        } ];
    },
    
    getBatchInputEnvFields : function() {
        return [ {
            checkbox : true
        }, {
            field : 'room',
            title : 'T_EMC_ENV_RECORD.ROOM',
            validators : [ 'strLength[0,36]' ],
            type : 'choose',
            category : 'room',
            single : true,
            onAfterChoose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                this.setData({
                    index : index,
                    room : rows[0].roomNo,
                    standardTemperature : rows[0].standardTemperature,
                    standardHumidity : rows[0].standardHumidity,
                    thermometerNo : rows[0].thermometerNo,
                    ext$ : {
                        roomid : rows[0].id,
                    }
                });
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    room : '',
                    standardTemperature : '',
                    standardHumidity : '',
                    thermometerNo : '',
                    ext$ : {
                        roomid : '',
                    }
                });
            }
        }, {
            field : 'ext$.startdate',
            title : 'T_EMC_ENV_RECORD.STARTDATE',
            type : 'date',
        }, {
            field : 'ext$.enddate',
            title : 'T_EMC_ENV_RECORD.ENDDATE',
            type : 'date',
        },];
    },
    
    getBatchInputFields : function() {
        return [ {
            checkbox : true
        },{
            field : 'monitorDate',
            title : 'T_EMC_ENV_RECORD.MONITORDATE',
            preInsert : true,
            type : 'date'
        },{
            field : 'ext$.recordusertime',
            title : 'T_EMC_ENV_RECORD.RECORDUSERTIME',
            preInsert : true,
            type : 'time',
        }, {
            field : 'temperature',
            title : 'T_EMC_ENV_RECORD.TEMPERATURE',
            validators : [ 'strLength[0,36]' ]
        }, {
            field : 'humidity',
            title : 'T_EMC_ENV_RECORD.HUMIDITY',
            validators : [ 'strLength[0,36]' ]
        }, {
            field : 'recordUserName',
            title : 'T_EMC_ENV_RECORD.RECORDUSERNAME',
            preInsert : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onBeforeChoose : function() {
                return {
    	            orgId_SEQ : workspace.user.orgId,
                    status_DIFFER : 'locked'
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    var grid = this;
                    grid.setData({
                        index : index,
                        recordUserId : rows[0].id,
                        recordUserName: rows[0].userName
                    });
                }
            },
            validators : [ 'strLength[0,60]' ]
        },{
            field : 'ext$.section',
            title : 'T_EMC_ENV_RECORD.SECTION',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'section'
            },
        
        }];
    },
    
    getPrepareShowAllField : function() {
        var flag = true;
        var _this = this;
      /*  var runId = selections[0].id;   
        var testids = "";
        Gikam.postText(emcRunEquiptCrves.baseUrl + '/select-run-testid', Gikam.getJsonWrapper({
            f :{
            	runId : runId,
            }})).done(function(data) {
            	testids = data          
            });*/
        return [ {
            title : '显示全部',
            field : 'showFlag',
            refreshGrid : false,
            type : 'simpleCheckbox',
            styleFormatter : function() {
                return {
                    paddingLeft : '0'
                }
            },
            onChange : function(field, value, index) {
                workspace.window.showMask(true);
                var grid = Gikam.getComp('emc-env-record-edit-list-grid');

                var showFlags = value;
                if (showFlags == '1') {
                	grid.refresh({
                        requestData : {
                            createdById_SEQ  : '',
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                } else {
                	grid.refresh({
                        requestData : {
                            createdById_SEQ : workspace.user.id
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                }
            },
        } ]
    },
    
});
