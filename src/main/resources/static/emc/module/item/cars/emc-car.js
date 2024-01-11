var emcCar = Gikam.getPageObject({

    getApplyEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcCar.workflow.dbTable), {
            field : 'useDeptName',
            title : 'T_EMC_CAR.USEDEPT'
        }, {
            field : 'applyUserName',
            title : 'T_EMC_CAR.APPLYUSERNAME',
            editor : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {

                    this.setData({
                        id : Gikam.getComp('emc-car-edit-list-grid').getActivedRow().id,
                        applyUserId : rows[0].id,
                        applyUserName : rows[0].userName,
                        useDeptId : rows[0].orgId,
                        useDeptName : rows[0].orgName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-car-edit-list-grid').getActivedRow().id,
                    applyUserId : '',
                    applyUserName : '',
                    useDeptId : '',
                    useDeptName : '',

                });
            },
        }, {
            field : 'carNum',
            title : 'T_EMC_CAR.CARNUM',
            validators : [ 'strLength[0, 12]' ],
            editor : true
        },

        {
            field : 'passengerNames',
            title : 'T_EMC_CAR.PASSENGERNAMES',
            editor : true,
            type : 'choose',
            category : 'user-simple',
            single : false,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    // Gikam.getComp('emc-patrol-edit-list-grid').getActivedRow().patrolUserId
                    // =rows[0].id;
                    var ids = "";
                    var names = "";
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                            ids += rows[index].id;
                            names += rows[index].userName;
                            break;
                        }
                        ids += rows[index].id + ",";
                        names += rows[index].userName + ",";
                    }
                    this.setData({
                        id : Gikam.getComp('emc-car-edit-list-grid').getActivedRow().id,
                        passengerIds : ids,
                        passengerNames : names
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-car-edit-list-grid').getActivedRow().id,
                    passengerIds : '',
                    passengerNames : ""
                });
            }
        }, {
            field : 'leaveTime',
            title : 'T_EMC_CAR.LEAVETIME',
            editor : true,
            genericQuery : true,
            type : 'dateTime'
        }, {
            field : 'useReason',
            title : 'T_EMC_CAR.USEREASON',
            validators : [ 'strLength[0, 540]' ],
            editor : true
        }, {
            field : 'destination',
            validators : [ 'strLength[0, 540]' ],
            title : 'T_EMC_CAR.DESTINATION',
            editor : true
        }, {
            field : 'remark',
            title : 'T_EMC_CAR.REMARK',
            validators : [ 'strLength[0, 540]' ],
            editor : true
        }

        ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcCar.workflow.dbTable), {
            field : 'useDeptName',
            title : 'T_EMC_CAR.USEDEPT'
        }
        , {
            field : 'applyUserName',
            title : 'T_EMC_CAR.APPLYUSERNAME',
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    // Gikam.getComp('emc-patrol-edit-list-grid').getActivedRow().patrolUserId
                    // =rows[0].id;

                    this.setData({
                        id : Gikam.getComp('emc-car-edit-list-grid').getActivedRow().id,
                        applyUserId : rows[0].id,
                        applyUserName : rows[0].userName,
                        useDeptId : rows[0].orgId,
                        useDeptName : rows[0].orgName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-car-edit-list-grid').getActivedRow().id,
                    applyUserId : '',
                    applyUserName : '',
                    useDeptId : '',
                    useDeptName : '',

                });
            },
        }, {
            field : 'carNum',
            title : 'T_EMC_CAR.CARNUM',
        },

        {
            field : 'passengerNames',
            title : 'T_EMC_CAR.PASSENGERNAMES',
            type : 'choose',
            category : 'user-simple',
            single : false,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    // Gikam.getComp('emc-patrol-edit-list-grid').getActivedRow().patrolUserId
                    // =rows[0].id;
                    var ids = "";
                    var names = "";
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                            ids += rows[index].id;
                            names += rows[index].userName;
                            break;
                        }
                        ids += rows[index].id + ",";
                        names += rows[index].userName + ",";
                    }
                    this.setData({
                        id : Gikam.getComp('emc-car-edit-list-grid').getActivedRow().id,
                        passengerIds : ids,
                        passengerNames : names
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-car-edit-list-grid').getActivedRow().id,
                    passengerIds : '',
                    passengerNames : ""
                });
            }
        }, {
            field : 'leaveTime',
            title : 'T_EMC_CAR.LEAVETIME',
            type : 'dateTime'
        }, {
            field : 'useReason',
            title : 'T_EMC_CAR.USEREASON'
        }, {
            field : 'destination',
            title : 'T_EMC_CAR.DESTINATION'
        }, {
            field : 'remark',
            title : 'T_EMC_CAR.REMARK'
        }

        ];
    },

    getRecordEditListColumns : function() {
        return [ Gikam.status.getBpmnColumn(emcCar.workflow.dbTable), {
            field : 'useDeptName',
            title : 'T_EMC_CAR.USEDEPT'
        }, {
            field : 'applyUserName',
            title : 'T_EMC_CAR.APPLYUSERNAME',
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : Gikam.getComp('emc-car-edit-list-grid').getActivedRow().id,
                        applyUserId : rows[0].id,
                        applyUserName : rows[0].userName,
                        useDeptId : rows[0].orgId,
                        useDeptName : rows[0].orgName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-car-edit-list-grid').getActivedRow().id,
                    applyUserId : '',
                    applyUserName : '',
                    useDeptId : '',
                    useDeptName : '',

                });
            }
        }, {
            field : 'carNum',
            title : 'T_EMC_CAR.CARNUM'
        },

        {
            field : 'passengerNames',
            title : 'T_EMC_CAR.PASSENGERNAMES',
            type : 'choose',
            category : 'user-simple',
            single : false,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var ids = "";
                    var names = "";
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                            ids += rows[index].id;
                            names += rows[index].userName;
                            break;
                        }
                        ids += rows[index].id + ",";
                        names += rows[index].userName + ",";
                    }
                    this.setData({
                        id : Gikam.getComp('emc-car-edit-list-grid').getActivedRow().id,
                        passengerIds : ids,
                        passengerNames : names
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-car-edit-list-grid').getActivedRow().id,
                    passengerIds : '',
                    passengerNames : ""
                });
            }
        }, {
            field : 'leaveTime',
            title : 'T_EMC_CAR.LEAVETIME',
            type : 'dateTime'
        }, {
            field : 'useReason',
            title : 'T_EMC_CAR.USEREASON'
        }, {
            field : 'destination',
            title : 'T_EMC_CAR.DESTINATION'
        }, {
            field : 'remark',
            title : 'T_EMC_CAR.REMARK'
        },

        {
            field : 'pickCarStatus',
            title : 'T_EMC_CAR.PICKCARSTATUS',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        },

        {
            field : 'driverName',
            title : 'T_EMC_CAR.DRIVER',
            editor : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : Gikam.getComp('emc-car-edit-list-grid').getActivedRow().id,
                        driverId : rows[0].id,
                        driverName : rows[0].userName,

                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-car-edit-list-grid').getActivedRow().id,
                    driverId : '',
                    driverName : '',
                });
            },
        }, {
            field : 'actualUseTime',
            title : 'T_EMC_CAR.ACTUALUSETIME',
            editor : true,
            type : 'dateTime'
        }, {
            field : 'actualRebackTime',
            title : 'T_EMC_CAR.ACTUALREBACKTIME',
            editor : true,
            type : 'dateTime'
        }, {
            field : 'beforeMile',
            title : 'T_EMC_CAR.BEFOREMILE',
            validators : [ 'strLength[0, 30]' ],
            editor : true
        }, {
            field : 'afterMile',
            title : 'T_EMC_CAR.AFTERMILE',
            validators : [ 'strLength[0, 30]' ],
            editor : true
        }, {
            field : 'oiling',
            title : 'T_EMC_CAR.OILING',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            field : 'oil',
            title : 'T_EMC_CAR.OIL',
            validators : [ 'strLength[0, 12]' ],
            editor : true
        }, {
            field : 'oilCardNo',
            title : 'T_EMC_CAR.OILCARDNO',
            validators : [ 'strLength[0, 30]' ],
            editor : true
        }, {
            field : 'oilingTime',
            title : 'T_EMC_CAR.OILINGTIME',
            editor : true,
            type : 'dateTime'
        }, {
            field : 'oilingMile',
            title : 'T_EMC_CAR.OILINGMILE',
            validators : [ 'strLength[0, 30]' ],
            editor : true
        } ];
    },
    
    
    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcCar.workflow.dbTable), {
            field : 'useDeptName',
            title : 'T_EMC_CAR.USEDEPT'
        }, {
            field : 'applyUserName',
            title : 'T_EMC_CAR.APPLYUSERNAME',
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {

                    this.setData({
                        id : Gikam.getComp('emc-car-edit-list-grid').getActivedRow().id,
                        applyUserId : rows[0].id,
                        applyUserName : rows[0].userName,
                        useDeptId : rows[0].orgId,
                        useDeptName : rows[0].orgName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-car-edit-list-grid').getActivedRow().id,
                    applyUserId : '',
                    applyUserName : '',
                    useDeptId : '',
                    useDeptName : '',

                });
            },
        }, {
            field : 'carNum',
            title : 'T_EMC_CAR.CARNUM',
            validators : [ 'strLength[0, 12]' ]
        },

        {
            field : 'passengerNames',
            title : 'T_EMC_CAR.PASSENGERNAMES',
            type : 'choose',
            category : 'user-simple',
            single : false,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    // Gikam.getComp('emc-patrol-edit-list-grid').getActivedRow().patrolUserId
                    // =rows[0].id;
                    var ids = "";
                    var names = "";
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                            ids += rows[index].id;
                            names += rows[index].userName;
                            break;
                        }
                        ids += rows[index].id + ",";
                        names += rows[index].userName + ",";
                    }
                    this.setData({
                        id : Gikam.getComp('emc-car-edit-list-grid').getActivedRow().id,
                        passengerIds : ids,
                        passengerNames : names
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-car-edit-list-grid').getActivedRow().id,
                    passengerIds : '',
                    passengerNames : ""
                });
            }
        }, {
            field : 'leaveTime',
            title : 'T_EMC_CAR.LEAVETIME',
            type : 'dateTime'
        }, {
            field : 'useReason',
            title : 'T_EMC_CAR.USEREASON',
            validators : [ 'strLength[0, 540]' ],
        }, {
            field : 'destination',
            validators : [ 'strLength[0, 540]' ],
            title : 'T_EMC_CAR.DESTINATION'
        }, {
            field : 'remark',
            title : 'T_EMC_CAR.REMARK',
            validators : [ 'strLength[0, 540]' ]
        }

        ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/emc-cars',

    workflow : {
        dbTable : 'T_EMC_CAR',
        columns : [ {
            field : 'id',
            title : 'T_EMC_CAR.ID'
        } ]
    },
    
    getChooseListColumns : function() {
        return [ {
            field : 'useDeptName',
            title : 'T_EMC_CAR.USEDEPT'
        }, {
            field : 'carNum',
            title : 'T_EMC_CAR.CARNUM',
        }, ];
    },
});
