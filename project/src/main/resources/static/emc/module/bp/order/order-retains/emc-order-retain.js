var emcOrderRetain = Gikam.getPageObject({

    baseUrl: IFM_CONTEXT + "/secure/emc/module/bp/order/order-retains",

    getEditListColumns_Overdue : function () {
        return [ {
            checkbox : true
        },
            {
                field : 'projNo',
                title : 'T_EMC_ORDER_RETAIN.PROJNO',
                width : 130,
            },
            {
                field : 'projName',
                title : 'T_EMC_ORDER_RETAIN.PROJNAME',
            },
            {
                field : 'folderNo',
                title : 'T_EMC_ORDER_RETAIN.FOLDERNO',
                width : 130,
            },
            {
                field : 'orderNo',
                title : 'T_EMC_ORDER_RETAIN.ORDERNO',
                width : 130,
            },
            {
                field : 'sampleCompleteDate',
                title : 'T_EMC_ORDER_RETAIN.SAMPLECOMPLETEDATE',
                formatter : function (i, v) {
                    if (v) {
                        return v.split(' ')[0]
                    }
                },
                width : 100,
            },
            {
                field : 'retainQty',
                title : 'T_EMC_ORDER_RETAIN.RETAINQTY',
                width : 100,
            },
            {
                field : 'retainTimeLimit',
                title : 'T_EMC_ORDER_RETAIN.RETAINTIMELIMIT',
                width : 100,
            },
            {
                field : 'retainLoc',
                title : 'T_EMC_ORDER_RETAIN.RETAINLOC',
            },
            {
                field : 'retainUserName',
                title : 'T_EMC_ORDER_RETAIN.RETAINUSERID',
                width : 100,
            },
            {
                field : 'retainDays',
                title : 'T_EMC_ORDER_RETAIN.RETAINDAYS',
                width : 100,
            },
            {
                field : 'ext$.handleusernames',
                title : 'T_EMC_ORDER_RETAIN.HANDLEUSERID',
                width : 100,
                preInsert : true,
                type : 'choose',
                category : 'user-simple',
                single : true,
                editor :true,
                onAfterChoose : function(rows) {
                    if (!Gikam.isEmpty(rows)) {
                        this.setData({
                            id : this.getActivedRow().id,
                        	handleUserId : rows[0].id,
                        	ext$ :{
                        		handleusernames :rows[0].userName,
                        	}
                        });
                    }
                },
                onClean : function() {
                    this.setData({
                        id : this.getActivedRow().id,
                    	handleUserId : '',
                    	ext$ :{
                    		handleusernames :rows[0].userName,
                    	}
                    });
                },

            },
            {
                field : 'handleDate',
                title : 'T_EMC_ORDER_RETAIN.HANDLEDATE',
                width : 100,
                type : 'date',
            },
            {
                field : 'handleRecord',
                title : 'T_EMC_ORDER_RETAIN.HANDLERECORD',
            },
        ];
    },

    getEditListColumns : function() {
        return [ {
            checkbox : true
        },
        {
            field : 'ext$.projno',
            title : 'T_EMC_ORDER_RETAIN.PROJNO',
            width : 130,
        },
        {
            field : 'ext$.projname',
            title : 'T_EMC_ORDER_RETAIN.PROJNAME',
        },
        {
            field : 'ext$.folderno',
            title : 'T_EMC_ORDER_RETAIN.FOLDERNO',
            width : 130,
        },
        {
            field : 'ext$.orderno',
            title : 'T_EMC_ORDER_RETAIN.ORDERNO',
            width : 130,
        },
        {
            field : 'ext$.samplecompletedate',
            title : 'T_EMC_ORDER_RETAIN.SAMPLECOMPLETEDATE',
            formatter : function (i, v) {
                if (v) {
                    return v.split(' ')[0]
                }
            },
            width : 100,
        },
        {
            field : 'retainQty',
            title : 'T_EMC_ORDER_RETAIN.RETAINQTY',
            width : 100,
        },
        {
            field : 'retainTimeLimit',
            title : 'T_EMC_ORDER_RETAIN.RETAINTIMELIMIT',
            width : 100,
        },
        {
            field : 'retainLoc',
            title : 'T_EMC_ORDER_RETAIN.RETAINLOC',
        },
        {
            field : 'ext$.retainusername',
            title : 'T_EMC_ORDER_RETAIN.RETAINUSERID',
            width : 100,
        },
        {
            field : 'retainDays',
            title : 'T_EMC_ORDER_RETAIN.RETAINDAYS',
            width : 100,
        },
        {
            field : 'ext$.handleusernames',
            title : 'T_EMC_ORDER_RETAIN.HANDLEUSERID',
            width : 100,
            preInsert : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            editor :true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                    	handleUserId : rows[0].id,
                    	ext$ :{
                    		handleusernames :rows[0].userName,
                    	}
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                	handleUserId : '',
                	ext$ :{
                		handleusernames :rows[0].userName,
                	}
                });
            },

        },
        {
            field : 'handleDate',
            title : 'T_EMC_ORDER_RETAIN.HANDLEDATE',
            width : 100,
            type : 'date',
            editor :true
        },
        {
            field : 'handleRecord',
            title : 'T_EMC_ORDER_RETAIN.HANDLERECORD',
            editor :true
        },
        {
            field : 'ext$.retaindate',
            title : 'T_EMC_ORDER_RETAIN.RETAINDATE',
            type : 'date',
            editor :true,
            preInsert : true,
        },
        {
            field : 'ext$.retainindex',
            title : 'T_EMC_ORDER_RETAIN.RETAININDEX',
            preInsert : true,
        },
        ];
    },

    getBaseInfoFormFields : function() {
        return [
            {
                field : 'ext$.retainusername',
                title : 'T_EMC_ORDER_RETAIN.RETAINUSERID',
                validators : [ 'notEmpty', 'strLength[0, 30]' ],
                preInsert : true,
                type : 'choose',
                category : 'user-simple',
                single : true,
                onAfterChoose : function(rows) {
                    if (!Gikam.isEmpty(rows)) {
                        this.setData({
                            retainUserId : rows[0].id,
                            ext$ : {
                                retainusername : rows[0].userName,
                            }
                        });
                    }
                },
                onClean : function() {
                    this.setData({
                        retainUserId : '',
                        ext$ : {
                            retainusername : '',
                        }
                    });
                },
            },
            {
                field : 'retainQty',
                title : 'T_EMC_ORDER_RETAIN.RETAINQTY',
                validators : [ 'notEmpty', 'strLength[0, 19]', 'integer' ],
                type : 'number',
                preInsert : true,
            },
            {
                field : 'retainDays',
                title : 'T_EMC_ORDER_RETAIN.RETAINDAYS',
                validators : [ 'notEmpty', 'strLength[0, 19]', 'integer' ],
                type : 'number',
                preInsert : true,
            },
            {
                field : 'retainLoc',
                title : 'T_EMC_ORDER_RETAIN.RETAINLOC',
                validators : [ 'notEmpty', 'strLength[0, 66]' ],
                preInsert : true,
            },
            {
                field : 'ext$.retainDate',
                title : 'T_EMC_ORDER_RETAIN.RETAINDATE',
                validators : [ 'notEmpty'],
                type : 'date',
                editor :true,
                preInsert : true,
            },
        ]
    },
    
    getOrderChooseListColumns : function () {
        return [
            {
                field : 'ext$.projno',
                title : 'T_EMC_ORDER_RETAIN.PROJNO',
            },
            {
                field : 'ext$.projname',
                title : 'T_EMC_ORDER_RETAIN.PROJNAME',
            },
            {
                field : 'ext$.folderno',
                title : 'T_EMC_ORDER_RETAIN.FOLDERNO',
            },
            {
                field : 'ext$.foldername',
                title : 'T_EMC_ORDER_RETAIN.FOLDERNAME',
            },
            {
                field : 'orderNo',
                title : 'T_EMC_ORDER_RETAIN.ORDERNO',
            },
            {
                field : 'sampleCompleteDate',
                title : 'T_EMC_ORDER_RETAIN.SAMPLECOMPLETEDATE',
                formatter : function (i, v) {
                    if (v) {
                        return v.split(" ")[0]
                    }
                }
            },
        ];
    },
    
    getBatchInputFields : function () {
        return [
            {
                field : 'ext$.handleusernames',
                title : 'T_EMC_ORDER_RETAIN.HANDLEUSERID',
                width : 300,
                preInsert : true,
                type : 'choose',
                category : 'user-simple',
                single : true,
                editor :true,
                onAfterChoose : function(rows) {
                    if (!Gikam.isEmpty(rows)) {
                        this.setData({
                            handleUserId : rows[0].id,
                            ext$ :{
                            	handleusernames : rows[0].userName,
                            }
                        });
                    }
                },
                onClean : function() {
                    this.setData({
                        handleUserId : '',
                        ext$ :{
                        	handleusernames : rows[0].userName,
                        }
                    });
                },

            },
            {
                field : 'handleDate',
                title : 'T_EMC_ORDER_RETAIN.HANDLEDATE',
                width : '30%',
                type : 'date',
            },
            {
                field : 'handleRecord',
                title : 'T_EMC_ORDER_RETAIN.HANDLERECORD',
            },

        ];
    }

}
)