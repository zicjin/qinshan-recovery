var emcEquiptTemp = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/equipt-temp',

    getPreInsertFormFields : function() {
        return [ {
            checkbox : true
        },{
            field : 'ext$.equiptname',
            title : 'T_EMC_EQUIPT.EQUIPTNAME',
            validators : [ 'notEmpty' ],
            preInsert : true,
            single : true,
            type : 'choose',
            category : 'equipt',
            onBeforeChoose : function() {
                return {
                    equiptcategoryname_SEQ : '辅助',
                    orgId_SB : workspace.user.ext$.parentorgid         
                    }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                    	equiptId : rows[0].id,
                        ext$ : {
                            equiptname : rows[0].equiptName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    equiptId : '',
                    ext$ : {
                        equiptname : ''
                    }
                });
            }
        }/*,  {
            field : 'recordUserName',
            title : 'T_EMC_EQUIPT_TEMP.RECORDUSERNAME',
            preInsert : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
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
        }*/, {
            field : 'recordDate',
            title : 'T_EMC_EQUIPT_TEMP.RECORDDATE',
            editor : true,
            preInsert : true,
            type : 'date',
            validators : [ 'notEmpty']
        },{
            field : 'section',
            title : 'T_EMC_EQUIPT_TEMP.SECTION',
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
        },{
            field : 'ext$.equiptname',
            title : 'T_EMC_EQUIPT.EQUIPTNAME',
            validators : [ 'notEmpty' ],
            single : true,
            type : 'choose',
            editor : true,
            category : 'equipt',
            onBeforeChoose : function() {
                return {
                    equiptcategoryname_SEQ : '辅助',
                    orgId_SB : workspace.user.ext$.parentorgid         
                    }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                    	id : Gikam.getComp('emc-equipt-temp-edit-list-grid').getActivedRow().id,
                    	equiptId : rows[0].id,
                        ext$ : {
                            equiptname : rows[0].equiptName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                	id : Gikam.getComp('emc-equipt-temp-edit-list-grid').getActivedRow().id,
                	equiptId : '',
                    ext$ : {
                        equiptname : ''
                    }
                });
            }
        }, {
            field : 'temperature',
            title : 'T_EMC_EQUIPT_TEMP.TEMPERATURE',
            width : 80,
            editor : true,
            validators : ['strLength[0,36]' ]
        }/*, {
            field : 'recordUserName',
            title : 'T_EMC_EQUIPT_TEMP.RECORDUSERNAME',
            width : 90,
            editor : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
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
        }*/, {
            field : 'recordDate',
            title : 'T_EMC_EQUIPT_TEMP.RECORDDATE',
            width : 120,
            editor : true,
            type : 'date',
            validators : [ 'notEmpty']
        },{
            field : 'section',
            title : 'T_EMC_EQUIPT_TEMP.SECTION',
            editor : true,
            width : 100,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'section'
            },        
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT_TEMP.REMARK',
            editor : true,
            validators : [ 'strLength[0, 120]' ]
        } ];
    },

    
    getBatchInputFields : function() {
        return [ {
            checkbox : true
        },{
            field : 'ext$.equiptname',
            title : 'T_EMC_EQUIPT.EQUIPTNAME',
            single : true,
            type : 'choose',
            category : 'equipt',
            editor : true,
            onBeforeChoose : function() {
                return {
                    equiptcategoryname_SEQ : '辅助',
                    orgId_SB : workspace.user.ext$.parentorgid         
                    }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                    	equiptId : rows[0].id,
                        ext$ : {
                            equiptname : rows[0].equiptName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                	equiptId : '',
                    ext$ : {
                        equiptname : ''
                    }
                });
            }
        },{
            field : 'temperature',
            title : 'T_EMC_EQUIPT_TEMP.TEMPERATURE',
            validators : [ 'strLength[0,36]' ]
        }/*, {
            field : 'recordUserName',
            title : 'T_EMC_EQUIPT_TEMP.RECORDUSERNAME',
            type : 'choose',
            category : 'user-simple',
            single : true,
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
            field : 'recordDate',
            title : 'T_EMC_EQUIPT_TEMP.RECORDDATE',
            type : 'dateTime'
        },{
            field : 'section',
            title : 'T_EMC_EQUIPT_TEMP.SECTION',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'section'
            },
        
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT_TEMP.REMARK',
            editor : true,
            validators : [ 'strLength[0, 120]' ]
        }];
    },
    
    getBatchInsertTempFields : function() {
        return [ {
            checkbox : true
        },{
            field : 'ext$.equiptname',
            title : 'T_EMC_EQUIPT.EQUIPTNAME',
            single : true,
            type : 'choose',
            editor : true,
            category : 'equipt',
            onBeforeChoose : function() {
                return {
                    equiptcategoryname_SEQ : '辅助',
                    orgId_SB : workspace.user.ext$.parentorgid         
                    }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                    	equiptId : rows[0].id,
                        ext$ : {
                            equiptname : rows[0].equiptName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                	equiptId : '',
                    ext$ : {
                        equiptname : ''
                    }
                });
            }
        }, {
            field : 'startDate',
            title : 'T_EMC_ENV_RECORD.STARTDATE',
            type : 'date',
        }, {
            field : 'endDate',
            title : 'T_EMC_ENV_RECORD.ENDDATE',
            type : 'date',
        }/*, {
            field : 'recordUserName',
            title : 'T_EMC_ENV_RECORD.RECORDUSERNAME',
            type : 'choose',
            category : 'user-simple',
            single : true,
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
        }*/,];
    },
    
});
