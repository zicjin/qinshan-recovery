var emcEquiptRequisition = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.equiptcode',
            title : 'T_EMC_EQUIPT_REQUISITION.EQUIPTCODE',
            width : 110,
        }, {
            field : 'ext$.equiptname',
            title : 'T_EMC_EQUIPT_REQUISITION.EQUIPTNAME',
            width : 110,
        }, {
            field : 'ext$.specification',
            title : 'T_EMC_EQUIPT_REQUISITION.SPECIFICATION',
            width : 100,
        }, {
            field : 'ext$.factoryno',
            title : 'T_EMC_EQUIPT_REQUISITION.FACTORYNO',
            width : 100
        },{
            field : 'ext$.status',
            title : 'T_EMC_EQUIPT_REQUISITION.EQUIPTSTATUS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'equiptStatus',
            }
        },{
            field : 'equiptManager',
            title : 'T_EMC_EQUIPT_REQUISITION.EQUIPTMANAGER',
        },{
            field : 'rentUser',
            title : 'T_EMC_EQUIPT_REQUISITION.RENTUSER',
        },{
            field : 'rentOrg',
            title : 'T_EMC_EQUIPT_REQUISITION.RENTORG',
        }, {
            field : 'rentDate',
            title : 'T_EMC_EQUIPT_REQUISITION.RENTDATE',
        }, {
            field : 'rentTime',
            title : 'T_EMC_EQUIPT_REQUISITION.RENTTIME',
        }, {
            field : 'rentStatus',
            title : 'T_EMC_EQUIPT_REQUISITION.RENTSTATUS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'workstate'
            },
        },{
            field : 'backUser',
            title : 'T_EMC_EQUIPT_REQUISITION.BACKUSER',
        },{
            field : 'backOrg',
            title : 'T_EMC_EQUIPT_REQUISITION.BACKORG',
        }, {
            field : 'backDate',
            title : 'T_EMC_EQUIPT_REQUISITION.BACKDATE',
        }, {
            field : 'backTime',
            title : 'T_EMC_EQUIPT_REQUISITION.BACKTIME',
        }, {
            field : 'backStatus',
            title : 'T_EMC_EQUIPT_REQUISITION.BACKSTATUS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'workstate'
            },
        }/*,{
            field : 'isBack',
            title : 'T_EMC_EQUIPT_REQUISITION.ISBACK',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }*/,{
            field : 'remark',
            title : 'T_EMC_EQUIPT_REQUISITION.REMARK',
            editor : true,
        }];
    },

    getRentColumns : function() {
        return[{
                field : 'ext$.equiptname',
                title : 'T_EMC_EQUIPT_REQUISITION.EQUIPTNAME',
                preInsert : true,
                type : 'choose',
                category : 'equiptSimple',
                single : false,
                onBeforeChoose : function(){
                    return{
                        // equiptCategoryId_SEQ : '201823507144448',
                        isOnStock_SEQ : '1',
                    }
                },
                onAfterChoose : function(rows,index) {
                    if (!Gikam.isEmpty(rows)) {
                        var isOnStock = rows[0].isOnStock;
                        if ('0' === isOnStock) {
                            Gikam.alert("T_EMC_EQUIPT_REQUISITION.EQUIPT_NOSTOCK");
                            return
                        }

                        var equiptIds = rows.map(item => item.id);
                        var equiptNames = rows.map(item => item.equiptName);

                        this.setData({
                            index : index,
                            equiptId : rows[0].id,
                            ext$ : {
                                equiptcode : rows[0].equiptId,
                                equiptname : equiptNames.join(),
                                specification : rows[0].specification,
                                factoryno : rows[0].factoryNo,
                                mulitequiptids : equiptIds.join(),
                                status : rows[0].status,
                            }
                        });
                    }
                },
                onClean : function() {
                    this.setData({
                        equiptId : '',
                        ext$ : {
                            equiptCode : '',
                            equiptName : '',
                            specification : '',
                            factoryNo : '',
                            status : '',
                        }
                    });
                },
            },{
                field : 'rentUser',
                title : 'T_EMC_EQUIPT_REQUISITION.RENTUSER',
                preInsert : true,
                value : workspace.user.userName,
                /*type : "choose",
                category : 'emp',
                single : true,
                onAfterChoose : function(rows, index) {
                    if (!Gikam.isEmpty(rows)) {
                        this.setData({
                            index : index,
                            rentUser : rows[0].userName,
                            rentOrg : rows[0].ext$.orgname,
                        });
                    }
                },
                onClean : function(field, value, index) {
                    this.setData({
                        index : index,
                        rentUser : '',
                        rentOrg : '',
                    });
                }*/
            },{
                field : 'rentOrg',
                title : 'T_EMC_EQUIPT_REQUISITION.RENTORG',
                preInsert :true,
                value : workspace.user.orgName,
            }, {
                field : 'rentDate',
                title : 'T_EMC_EQUIPT_REQUISITION.RENTDATE',
                preInsert :true,
                type : 'date',
                value : Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd'),
            }, {
                field : 'rentTime',
                title : 'T_EMC_EQUIPT_REQUISITION.RENTTIME',
                preInsert :true,
                type : 'time',
                value : Gikam.DateUtils.formatter(new Date(), 'hh:mm'),
            }, {
                field : 'equiptManager',
                title : 'T_EMC_EQUIPT_REQUISITION.EQUIPTMANAGER',
                preInsert :true,
                type : 'choose',
                category : 'emp',
                single : true,
                onBeforeChoose : function() {
                    return {
                        isEquiptManager_SEQ : '1'
                    }
                },
                onAfterChoose : function(rows, index) {
                    if (!Gikam.isEmpty(rows)) {
                        this.setData({
                            index : index,
                            equiptManager : rows[0].userName,
                            equiptManagerId : rows[0].id,
                        });
                    }
                },
                onClean : function(field, value, index) {
                    this.setData({
                        index : index,
                        equiptManager : '',
                        equiptManagerId : '',
                    });
                }
            }, {
            field : 'rentStatus',
            title : 'T_EMC_EQUIPT_REQUISITION.RENTSTATUS',
            preInsert :true,
            value : '1',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'workstate'
            }
        },{
            field : 'remark',
            title : 'T_EMC_EQUIPT_REQUISITION.REMARK',
            preInsert : true
        }]
    },

    getBackColumns : function() {
        return [{
            field : 'backUser',
            title : 'T_EMC_EQUIPT_REQUISITION.BACKUSER',
            preInsert :true,
            value : workspace.user.userName,
            type : "choose",
            category : 'emp',
            single : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        backUser : rows[0].userName,
                        backOrg : rows[0].ext$.orgname,
                    });
                }
            },
            onClean : function(field, value, index) {
                this.setData({
                    index : index,
                    backUser : '',
                    backOrg : '',
                });
            }
        },{
            field : 'backOrg',
            title : 'T_EMC_EQUIPT_REQUISITION.BACKORG',
            preInsert :true,
            value : workspace.user.orgName,
        }, {
            field : 'backDate',
            title : 'T_EMC_EQUIPT_REQUISITION.BACKDATE',
            preInsert :true,
            type : 'date',
            value : Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd'),
        }, {
            field : 'backTime',
            title : 'T_EMC_EQUIPT_REQUISITION.BACKTIME',
            preInsert :true,
            type : 'time',
            value : Gikam.DateUtils.formatter(new Date(), 'hh:mm'),
        }, {
            field : 'backStatus',
            title : 'T_EMC_EQUIPT_REQUISITION.BACKSTATUS',
            preInsert :true,
            value : '1',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'workstate'
            },
        } ]
    },

    getBatchChangeRentStatusColumns : function(){
        return [{
            field : 'rentStatus',
            title : 'T_EMC_EQUIPT_REQUISITION.STATUS',
            preInsert : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'workstate'
            },
        }, {
            field : 'rentDate',
            title : 'T_EMC_EQUIPT_REQUISITION.RENTDATE',
            preInsert :true,
            type : 'date',
            value : Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd'),
        }, {
            field : 'rentTime',
            title : 'T_EMC_EQUIPT_REQUISITION.RENTTIME',
            preInsert :true,
            type : 'time',
            value : Gikam.DateUtils.formatter(new Date(), 'hh:mm'),
        }];
    },

    getBatchChangeBackStatusColumns : function(){
        return [{
            field : 'backStatus',
            title : 'T_EMC_EQUIPT_REQUISITION.STATUS',
            preInsert : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'workstate'
            },
        }];
    },


    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/equipt-requisitions'
});
