var emcRoom = Gikam.getPageObject({

    baseUrl: IFM_CONTEXT + "/secure/emc/module/mdm/basemdm/rooms",

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'roomNo',
            title : 'T_EMC_ROOM.ROOMNO',
            editor : true
        }, {
            field : 'standardTemperature',
            title : 'T_EMC_ROOM.STANDARDTEMPERATURE',
            editor : true
        }, {
            field : 'standardHumidity',
            title : 'T_EMC_ROOM.STANDARDHUMIDITY',
            editor : true
        }, {
            field : 'thermometerNo',
            title : 'T_EMC_ROOM.THERMOMETERNO',
            editor : true
        }, {
            field : 'ext$.roomdirector',
            title : 'T_EMC_ROOM.ROOMDIRECTOR',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'user-simple',
            validators : [ 'notEmpty' ],
            single : true,
            onBeforeChoose : function() {
                return {
    	            orgId_SEQ : workspace.user.orgId,
                    status_DIFFER : 'locked'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                        ext$ : {
                            roomdirectorid : rows[0].id,
                        	roomdirector : rows[0].userName
                        },
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    roomdirectorId : '',
                    ext$ : {
                        roomdirectorid : '',
                    	roomdirector : ''
                    },
                });
            },
            
        }];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'roomNo',
            title : 'T_EMC_ROOM.ROOMNO',
            validators : [ 'strLength[0, 36]' ],
            preInsert : true,
        }, {
            field : 'standardTemperature',
            title : 'T_EMC_ROOM.STANDARDTEMPERATURE',
            validators : [ 'strLength[0, 36]' ],
            preInsert : true,
        }, {
            field : 'standardHumidity',
            title : 'T_EMC_ROOM.STANDARDHUMIDITY',
            validators : [ 'strLength[0, 36]' ],
            preInsert : true,
        }, {
            field : 'thermometerNo',
            title : 'T_EMC_ROOM.THERMOMETERNO',
            validators : [ 'strLength[0, 36]' ],
            preInsert : true
        }, {
            field : 'ext$.roomdirector',
            title : 'T_EMC_ROOM.ROOMDIRECTOR',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'user-simple',
            validators : [ 'notEmpty' ],
            single : true,
            onBeforeChoose : function() {
                return {
    	            orgId_SEQ : workspace.user.orgId,
                    status_DIFFER : 'locked'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                        roomdirectorId : rows[0].id,
                        ext$ : {
                        	roomdirector : rows[0].userName
                        },
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    roomdirectorId : '',
                    ext$ : {
                    	roomdirector : ''
                    },
                });
            },
            
        } ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'roomNo',
            title : 'T_EMC_ROOM.ROOMNO',
        }, {
            field : 'standardTemperature',
            title : 'T_EMC_ROOM.STANDARDTEMPERATURE',
        }, {
            field : 'standardHumidity',
            title : 'T_EMC_ROOM.STANDARDHUMIDITY',
        }, {
            field : 'thermometerNo',
            title : 'T_EMC_ROOM.THERMOMETERNO',
        }, {
            field : 'ext$.roomdirector',
            title : 'T_EMC_ROOM.ROOMDIRECTOR',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'user-simple',
            validators : [ 'notEmpty' ],
            single : true,
            onBeforeChoose : function() {
                return {
    	            orgId_SEQ : workspace.user.orgId,
                    status_DIFFER : 'locked'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        roomdirectorId : rows[0].id,
                        ext$ : {
                        	roomdirector : rows[0].userName
                        },
                    });
                }
            },
            onClean : function() {
                this.setData({
                    roomdirectorId : '',
                    ext$ : {
                    	roomdirector : ''
                    },
                });
            },
            
        } ];
    },

})
