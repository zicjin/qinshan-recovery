var emcWorkCondition = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox: true
        }, {
            field: "ext$.testorgname",
            title: "企业名称",
        }, {
            field: "ext$.testorgaddress",
            title: "企业地址",
        }, {
            field: "ext$.testorgcontact",
            title: "联系人",
        }, {
            field: "ext$.testorgphone",
            title: "联系电话",
        }, {
            field: "gasSource",
            title: "T_EMC_WORK_CONDITION.GASSOURCE",
            editor: true
        }, {
            field: "gasCraft",
            title: "T_EMC_WORK_CONDITION.GASCRAFT",
            editor: true
        }, {
            field: "gasVentQty",
            title: "T_EMC_WORK_CONDITION.GASVENTQTY",
            editor: true
        }, {
            field: "gasVentNormal",
            title: "T_EMC_WORK_CONDITION.GASVENTNORMAL",
            editor: true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field: "gasMonitor",
            title: "T_EMC_WORK_CONDITION.GASMONITOR",
            editor: true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field: "gasMonitorItem",
            title: "T_EMC_WORK_CONDITION.GASMONITORITEM",
            editor: true
        }, {
            field: "gasMonitorSupplier",
            title: "T_EMC_WORK_CONDITION.GASMONITORSUPPLIER",
            editor: true
        }, {
            field: "gasStd",
            title: "T_EMC_WORK_CONDITION.GASSTD",
            editor: true
        }, {
            field: "waterSource",
            title: "T_EMC_WORK_CONDITION.WATERSOURCE",
            editor: true
        }, {
            field: "waterCraft",
            title: "T_EMC_WORK_CONDITION.WATERCRAFT",
            editor: true
        }, {
            field: "waterVentNormal",
            title: "T_EMC_WORK_CONDITION.WATERVENTNORMAL",
            editor: true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field: "waterMonitor",
            title: "T_EMC_WORK_CONDITION.WATERMONITOR",
            editor: true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field: "waterOutDirection",
            title: "T_EMC_WORK_CONDITION.WATEROUTDIRECTION",
            editor: true
        }, {
            field: "waterOutRegular",
            title: "T_EMC_WORK_CONDITION.WATEROUTREGULAR",
            editor: true
        }, {
            field: "waterMonitorSupplier",
            title: "T_EMC_WORK_CONDITION.WATERMONITORSUPPLIER",
            editor: true
        }, {
            field: "waterStd",
            title: "T_EMC_WORK_CONDITION.WATERSTD",
            editor: true
        }, {
            field: "swName",
            title: "T_EMC_WORK_CONDITION.SWNAME",
            editor: true
        }, {
            field: "swSource",
            title: "T_EMC_WORK_CONDITION.SWSOURCE",
            editor: true
        }, {
            field: "swQty",
            title: "T_EMC_WORK_CONDITION.SWQTY",
            editor: true
        }, {
            field: "swState",
            title: "T_EMC_WORK_CONDITION.SWSTATE",
            editor: true
        }, {
            field: "swRegular",
            title: "T_EMC_WORK_CONDITION.SWREGULAR",
            editor: true
        }, {
            field: "swHandle",
            title: "T_EMC_WORK_CONDITION.SWHANDLE",
            editor: true
        } ];
    },

    getLineListColumns : function() {
        return [ {
            checkbox: true
        }, {
            field: "production",
            title: "T_EMC_WORK_CONDITION_LINE.PRODUCTION",
            editor: true
        }, {
            field: "capacity",
            title: "T_EMC_WORK_CONDITION_LINE.CAPACITY",
            editor: true
        }, {
            field: "monitorYield",
            title: "T_EMC_WORK_CONDITION_LINE.MONITORYIELD",
            editor: true
        }, {
            field: "productLoad",
            title: "T_EMC_WORK_CONDITION_LINE.PRODUCTLOAD",
            editor: true
        }, {
            field: "waterVentQty",
            title: "T_EMC_WORK_CONDITION_LINE.WATERVENTQTY",
            editor: true
        }, {
            field: "productDate",
            title: "T_EMC_WORK_CONDITION_LINE.PRODUCTDATE",
            editor: true,
            type: 'date'
        } ];
    },

    getGasFormFields : function() {
        return [ {
            field: "gasSource",
            title: "T_EMC_WORK_CONDITION.GASSOURCE",
            editor: true
        }, {
            field: "gasCraft",
            title: "T_EMC_WORK_CONDITION.GASCRAFT",
            editor: true
        }, {
            field: "gasVentQty",
            title: "T_EMC_WORK_CONDITION.GASVENTQTY",
            editor: true
        }, {
            field: "gasVentNormal",
            title: "T_EMC_WORK_CONDITION.GASVENTNORMAL",
            editor: true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field: "gasMonitor",
            title: "T_EMC_WORK_CONDITION.GASMONITOR",
            editor: true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field: "gasMonitorItem",
            title: "T_EMC_WORK_CONDITION.GASMONITORITEM",
            editor: true
        }, {
            field: "gasMonitorSupplier",
            title: "T_EMC_WORK_CONDITION.GASMONITORSUPPLIER",
            editor: true
        }, {
            field: "gasStd",
            title: "T_EMC_WORK_CONDITION.GASSTD",
            editor: true
        }, {
            field: "waterSource",
            title: "T_EMC_WORK_CONDITION.WATERSOURCE",
            editor: true
        } ]
    },

    getWaterFormFields : function() {
        return [ {
            field: "waterSource",
            title: "T_EMC_WORK_CONDITION.WATERSOURCE",
            editor: true
        }, {
            field: "waterCraft",
            title: "T_EMC_WORK_CONDITION.WATERCRAFT",
            editor: true
        }, {
            field: "waterVentNormal",
            title: "T_EMC_WORK_CONDITION.WATERVENTNORMAL",
            editor: true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field: "waterMonitor",
            title: "T_EMC_WORK_CONDITION.WATERMONITOR",
            editor: true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field: "waterOutDirection",
            title: "T_EMC_WORK_CONDITION.WATEROUTDIRECTION",
            editor: true
        }, {
            field: "waterOutRegular",
            title: "T_EMC_WORK_CONDITION.WATEROUTREGULAR",
            editor: true
        }, {
            field: "waterMonitorSupplier",
            title: "T_EMC_WORK_CONDITION.WATERMONITORSUPPLIER",
            editor: true
        }, {
            field: "waterStd",
            title: "T_EMC_WORK_CONDITION.WATERSTD",
            editor: true
        } ]
    },

    getSolidWasteFormFields : function() {
        return [ {
            field: "swName",
            title: "T_EMC_WORK_CONDITION.SWNAME",
            editor: true
        }, {
            field: "swSource",
            title: "T_EMC_WORK_CONDITION.SWSOURCE",
            editor: true
        }, {
            field: "swQty",
            title: "T_EMC_WORK_CONDITION.SWQTY",
            editor: true
        }, {
            field: "swState",
            title: "T_EMC_WORK_CONDITION.SWSTATE",
            editor: true
        }, {
            field: "swRegular",
            title: "T_EMC_WORK_CONDITION.SWREGULAR",
            editor: true
        }, {
            field: "swHandle",
            title: "T_EMC_WORK_CONDITION.SWHANDLE",
            editor: true
        } ]
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/sample/conditions'
});
