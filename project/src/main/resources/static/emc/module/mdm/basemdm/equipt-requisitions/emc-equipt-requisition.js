var emcEquiptRequisition = Gikam.getPageObject({

    getEditListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'ext$.equiptcode',
            title: 'T_EMC_EQUIPT_REQUISITION.EQUIPTCODE',
            width: 110,
        }, {
            field: 'ext$.equiptname',
            title: 'T_EMC_EQUIPT_REQUISITION.EQUIPTNAME',
            width: 110,
        }, {
            field: 'ext$.specification',
            title: 'T_EMC_EQUIPT_REQUISITION.SPECIFICATION',
            width: 100,
        }, {
            field: 'ext$.factoryno',
            title: 'T_EMC_EQUIPT_REQUISITION.FACTORYNO',
            width: 100
        }, {
            field: 'ext$.status',
            title: 'T_EMC_EQUIPT_REQUISITION.EQUIPTSTATUS',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'equiptStatus',
            }
        }, {
            field: 'equiptManager',
            title: 'T_EMC_EQUIPT_REQUISITION.EQUIPTMANAGER',
        }, {
            field: 'rentUser',
            title: '使用人',
        }, {
            field: 'rentOrg',
            title: '使用人部门',
        },{
            field: 'humidity',
            title: '湿度',
        }, {
            field: 'temperature',
            title: '温度',
        }, {
            field: 'rentDate',
            title: '开始使用日期',
            editor : true,
            type: 'date',
        }, {
            field: 'rentTime',
            title: '开始使用时间',
            type: 'time',
            editor : true,
        }, {
            field: 'rentStatus',
            title: '使用前状态',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'workstate'
            },
        }, {
            field: 'backUser',
            title: '使用人',
        }, {
            field: 'backOrg',
            title: '使用人部门',
        }, {
            field: 'backDate',
            title: '结束使用日期',
            editor : true,
            type: 'date',
        }, {
            field: 'backTime',
            title: '结束使用时间',
            type: 'time',
            editor : true,
        }, {
            field: 'backStatus',
            title: '使用后状态',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'workstate'
            },
        }/*,{
            field : 'isBack',
            title : 'T_EMC_EQUIPT_REQUISITION.ISBACK',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }*/, {
            field: 'remark',
            title: 'T_EMC_EQUIPT_REQUISITION.REMARK',
            editor: true,
        }, {
            field: 'projNos',
            title: 'T_EMC_EQUIPT_REQUISITION.PROJNOS',
        }, {
            field: 'confirmUserName',
            title: '确认人',
        },{
            field: 'accessory',
            title: '配件',
        },];
    },
    getInstrumentOutEditListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'ext$.equiptcode',
            title: 'T_EMC_EQUIPT_REQUISITION.EQUIPTCODE',
            width: 110,
        }, {
            field: 'ext$.equiptname',
            title: 'T_EMC_EQUIPT_REQUISITION.EQUIPTNAME',
            width: 110,
        }, {
            field: 'ext$.specification',
            title: 'T_EMC_EQUIPT_REQUISITION.SPECIFICATION',
            width: 100,
        }, {
            field: 'equiptManager',
            title: 'T_EMC_EQUIPT_REQUISITION.EQUIPTMANAGER',
        }, {
            field: 'rentUser',
            title: 'T_EMC_EQUIPT_REQUISITION.RENTUSER',
        }, {
            field: 'rentOrg',
            title: 'T_EMC_EQUIPT_REQUISITION.RENTORG',
        }, {
            field: 'rentDate',
            title: 'T_EMC_EQUIPT_REQUISITION.RENTDATE',
        }, {
            field: 'rentTime',
            title: 'T_EMC_EQUIPT_REQUISITION.RENTTIME',
        }, {
            field: 'rentStatus',
            title: 'T_EMC_EQUIPT_REQUISITION.RENTSTATUS',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'workstate'
            },
            editor: true,
        }, {
            field: 'remark',
            title: 'T_EMC_EQUIPT_REQUISITION.REMARK',
            editor: true,
        }, {
            field: 'projNos',
            title: 'T_EMC_EQUIPT_REQUISITION.PROJNOS',
        }];
    },
    getRentColumns: function () {
        return [{
            field: 'ext$.equiptname',
            title: 'T_EMC_EQUIPT_REQUISITION.EQUIPTNAME',
            preInsert: true,
            type: 'choose',
            category: 'equiptSimple',
            single: false,
            onBeforeChoose: function () {
                return {
                    // equiptCategoryId_SEQ : '201823507144448',
                    // isOnStock_SEQ: '1',
                    status_SEQ : 'inUse',
                    orgid_seq:workspace.user.ext$.parentorgid,
                    equiptCategoryName_DIFFER : '分析',
                    target_filter: JSON.stringify({
                        type: 'unassigned',
                        targetTable: "T_EMC_EQUIPT_REQUISITION",
                        targetMatchColumn: "EQUIPTID",
                        filter: [{
                            targetFilterColumn: 'isBack',
                            targetFilterValue: '0'
                        }]
                    })
                }
            },
            onAfterChoose: function (rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    // var isOnStock = rows[0].isOnStock;
                    // if ('0' === isOnStock) {
                    //     Gikam.alert("T_EMC_EQUIPT_REQUISITION.EQUIPT_NOSTOCK");
                    //     return
                    // }

                    var equiptIds = rows.map(item => item.id);
                    var equiptNames = rows.map(item => item.equiptName);

                    this.setData({
                        index: index,
                        equiptId: rows[0].id,
                        ext$: {
                            equiptcode: rows[0].equiptId,
                            equiptname: equiptNames.join(),
                            specification: rows[0].specification,
                            factoryno: rows[0].factoryNo,
                            mulitequiptids: equiptIds.join(),
                            status: rows[0].status,
                        }
                    });
                }
            },
            onClean: function () {
                this.setData({
                    equiptId: '',
                    ext$: {
                        equiptCode: '',
                        equiptName: '',
                        specification: '',
                        factoryNo: '',
                        status: '',
                    }
                });
            },
        }, {
            field: 'rentUser',
            title: '使用人',
            preInsert: true,
            readonly : true,
            editor : false,
            value: workspace.user.userName,
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
        }, {
            field: 'rentOrg',
            title: '使用人部门',
            preInsert: true,
            readonly : true,
            value: workspace.user.orgName,
        }, {
            field: 'rentDate',
            title: '开始使用日期',
            preInsert: true,
            type: 'date',
            value: Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd'),
        }, {
            field: 'rentTime',
            title: '开始使用时间',
            preInsert: true,
            type: 'time',
            value: Gikam.DateUtils.formatter(new Date(), 'hh:mm'),
        }, {
            field: 'rentStatus',
            title: '使用前状态',
            preInsert: true,
            value: '1',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'workstate'
            }
        }, {
            field: 'accessory',
            title: '配件',
            preInsert: true,
        }, {
            field: 'humidity',
            title: '湿度',
            preInsert: true,
        }, {
            field: 'temperature',
            title: '温度',
            preInsert: true,
        }]
    },

    getBackColumns: function () {
        return [{
            field: 'backUser',
            title: '使用人',
            preInsert: true,
            validators: ['notEmpty',],
            value: workspace.user.userName,
            type: "choose",
            category: 'emp',
            single: true,
            onAfterChoose: function (rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index: index,
                        backUser: rows[0].userName,
                        backOrg: rows[0].ext$.orgname,
                    });
                }
            },
            onClean: function (field, value, index) {
                this.setData({
                    index: index,
                    backUser: '',
                    backOrg: '',
                });
            }
        }, {
            field: 'backOrg',
            title: '使用人部门',
            preInsert: true,
            validators: ['notEmpty',],
            value: workspace.user.orgName,
        }, {
            field: 'backDate',
            title: '结束使用日期',
            preInsert: true,
            validators: ['notEmpty',],
            type: 'date',
            value: Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd'),
        }, {
            field: 'backTime',
            title: '结束使用时间',
            preInsert: true,
            validators: ['notEmpty',],
            type: 'time',
            value: Gikam.DateUtils.formatter(new Date(), 'hh:mm'),
        }, {
            field: 'backStatus',
            title: 'T_EMC_EQUIPT_REQUISITION.BACKSTATUS',
            preInsert: true,
            validators: ['notEmpty',],
            value: '1',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'workstate'
            },
        }]
    },

    getBatchChangeRentStatusColumns: function () {
        return [{
            field: 'rentStatus',
            title: 'T_EMC_EQUIPT_REQUISITION.STATUS',
            preInsert: true,
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'workstate'
            },
        }, {
            field: 'rentDate',
            title: 'T_EMC_EQUIPT_REQUISITION.RENTDATE',
            preInsert: true,
            type: 'date',
            value: Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd'),
        }, {
            field: 'rentTime',
            title: 'T_EMC_EQUIPT_REQUISITION.RENTTIME',
            preInsert: true,
            type: 'time',
            value: Gikam.DateUtils.formatter(new Date(), 'hh:mm'),
        }];
    },

    getBatchChangeBackStatusColumns: function () {
        return [{
            field: 'backStatus',
            title: 'T_EMC_EQUIPT_REQUISITION.STATUS',
            preInsert: true,
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'workstate'
            },
        }];
    },
    
    getShowAllField : function() {
        var flag = true;
        return [ {
            title : 'EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.SHOW_ALL',
            field : 'showFlag',
            type : 'simpleCheckbox',
            refreshGrid : false,
            styleFormatter : function() {
                return {
                    paddingLeft : '0'
                }
            },
            onChange : function(field, value, index) {
                workspace.window.showMask(true);
                var grid = Gikam.getComp('emc-equipt-requisition-edit-list-grid-search');

                var showFlags = value;
                if (showFlags === '1') {
                	grid.refresh({
                        requestData : {
                            isBack_SEQ : ''
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                } else {
                	grid.refresh({
                        requestData : {
                            isBack_SEQ : '0'
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                }
            },
        } ]
    },

    getBackShowAllField : function() {
        var flag = true;
        return [ {
            title : 'EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.SHOW_ALL',
            field : 'showFlag',
            type : 'simpleCheckbox',
            refreshGrid : false,
            styleFormatter : function() {
                return {
                    paddingLeft : '0'
                }
            },
            onChange : function(field, value, index) {
                workspace.window.showMask(true);
                var grid = Gikam.getComp('emc-equipt-requisition-edit-list-grid-back');

                var showFlags = value;
                if (showFlags === '1') {
                    grid.refresh({
                        requestData : {
                            rentUserId_SEQ : ''
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                } else {
                    grid.refresh({
                        requestData : {
                            rentUserId_SEQ : workspace.user.id
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                }
            },
        } ]
    },

    baseUrl: IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/equipt-requisitions'
});
