var emcMtlUseRecord = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'mtlId',
            title : 'T_EMC_MTL_USE_RECORD.MTLID'
        }, {
            field : 'userName',
            title : 'T_EMC_MTL_USE_RECORD.USERNAME'
        }, {
            field : 'amount',
            title : 'T_EMC_MTL_USE_RECORD.AMOUNT'
        }, {
            field : 'usedDate',
            title : 'T_EMC_MTL_USE_RECORD.USEDDATE',
            type : 'date'
        }, {
            field : 'remark',
            title : 'T_EMC_MTL_USE_RECORD.REMARK',
            editor : true,
            width : 200
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_EMC_MTL_USE_RECORD.ID',
            preInsert : true
        }, {
            field : 'applyId',
            title : 'T_EMC_MTL_USE_RECORD.APPLYID',
            validators : [ 'strLength[0,10]' ]
        }, {
            field : 'mtlId',
            title : 'T_EMC_MTL_USE_RECORD.MTLID',
            validators : [ 'strLength[0,10]' ]
        }, {
            field : 'amount',
            title : 'T_EMC_MTL_USE_RECORD.AMOUNT',
            validators : [ 'strLength[0,10]' ]
        }, {
            field : 'remark',
            title : 'T_EMC_MTL_USE_RECORD.REMARK',
            validators : [ 'strLength[0,100]' ]
        } ];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcMtlUseRecord.workflow.dbTable), {
            field : 'id',
            title : 'T_EMC_MTL_USE_RECORD.ID',
            type : 'link'
        }, {
            field : 'applyId',
            title : 'T_EMC_MTL_USE_RECORD.APPLYID'
        }, {
            field : 'mtlId',
            title : 'T_EMC_MTL_USE_RECORD.MTLID'
        }, {
            field : 'amount',
            title : 'T_EMC_MTL_USE_RECORD.AMOUNT'
        }, {
            field : 'remark',
            title : 'T_EMC_MTL_USE_RECORD.REMARK'
        } ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'id',
            title : 'T_EMC_MTL_USE_RECORD.ID'
        }, {
            field : 'applyId',
            title : 'T_EMC_MTL_USE_RECORD.APPLYID'
        }, {
            field : 'mtlId',
            title : 'T_EMC_MTL_USE_RECORD.MTLID'
        }, {
            field : 'amount',
            title : 'T_EMC_MTL_USE_RECORD.AMOUNT'
        }, {
            field : 'remark',
            title : 'T_EMC_MTL_USE_RECORD.REMARK'
        } ];
    },

    getMtlApplyColumns : function() {
    	return [ {
            checkbox : true
        }, {
            field : 'ext$.mtlid',
            title : 'T_EMC_MTL.ID'
        }, {
            field : 'ext$.mtlname',
            title : 'T_EMC_MTL.MTLNAME',
            width : 120
        }, {
            field : 'ext$.batchno',
            title : 'T_EMC_STOCK.BATCHNO',
        }, {
            field : 'ext$.standardlevel',
            title : 'T_EMC_STOCK.STANDARDLEVEL',
        }, {
            field : 'ext$.stockspecification',
            title : 'T_EMC_STOCK.STOCKSPECIFICATION',
        }, {
            field : 'ext$.num',
            title : 'T_EMC_STOCK_LOG.RECIEVEQTY',
        }, {
            field : 'ext$.username',
            title : 'T_EMC_STOCK_LOG.RECEIVEDBYID',
        }, {
            field : 'ext$.orgname',
            title : 'T_EMC_STOCK_LOG.RECEIVEDBYORGID',
            width : 180
        }, {
            field : 'ext$.expiredate',
            title : 'T_EMC_STOCK.EXPIREDATE',
            type : 'date',
            width : 150
        } ];
    },

    getApplyFormFields : function() {
        return [ {
            field : 'amount',
            title : 'T_EMC_MTL_USE_RECORD.AMOUNT',
            preInsert : true
        }, {
            field : 'userName',
            title : 'T_EMC_MTL_USE_RECORD.USERNAME',
            type : 'choose',
            validators : [ 'notEmpty' ],
            preInsert : true,
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        receivedById : rows[0].id,
                        userName : rows[0].userName
                    });
                }
            },
        }, {
            field : 'usedDate',
            title : 'T_EMC_MTL_USE_RECORD.USEDDATE',
            type : 'date',
            validators : [ 'notEmpty' ],
            preInsert : true
        }, {
            field : 'remark',
            title : 'T_EMC_MTL_USE_RECORD.REMARK',
            validators : [ 'strLength[0,20]' ],
            preInsert : true
        } ];

    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/mtl-use-records'
});
