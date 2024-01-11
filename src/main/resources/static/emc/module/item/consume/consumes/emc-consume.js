var emcConsume = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/consume/consumes',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'id',
            title : 'T_EMC_CONSUME.ID',
        }, {
            field : 'mtlName',
            title : 'T_EMC_CONSUME.MTLNAME',
            editor : true,
        }, {
            field : 'specification',
            title : 'T_EMC_CONSUME.SPECIFICATION',
            editor : true,
        }, {
            field : 'stockQty',
            title : 'T_EMC_CONSUME.STOCKQTY',
            styleFormatter : function(row) {
                var result = {};
                if (row.stockQty == '0') {
                    result = {
                        color : 'red'
                    }
                }
                return result
            },
        }, {
            field : 'mtlCategoryId',
            title : 'T_EMC_CONSUME.MTLCATEGORYID',
            type : 'select',
            search : true,
            category : 'mtlCategory',
            param : {},
            editor : true,
        }, {
            field : 'harmfulness',
            title : 'T_EMC_CONSUME.HARMFULNESS',
            editor : true,
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_EMC_CONSUME.ID',
            readonly : true
        }, {
            field : 'mtlName',
            title : 'T_EMC_CONSUME.MTLNAME',
            preInsert : true,
            validators : [ 'strLength[0,21]' ]
        }, {
            field : 'specification',
            title : 'T_EMC_CONSUME.SPECIFICATION',
            validators : [ 'strLength[0,21]' ]
        }, {
            field : 'stockQty',
            title : 'T_EMC_CONSUME.STOCKQTY',
            readonly : true
        }, {
            field : 'mtlCategoryId',
            title : 'T_EMC_CONSUME.MTLCATEGORYID',
            type : 'select',
            search : true,
            category : 'mtlCategory',
            param : {},
            preInsert : true
        }, {
            field : 'harmfulness',
            title : 'T_EMC_CONSUME.HARMFULNESS',
            validators : [ 'strLength[0,10]' ]
        } ];
    },

    getStockListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'batchNo',
            title : 'T_EMC_CSM_STOCK.BATCHNO',
            validators : [ 'strLength[0,32]' ],
            editor : true,
        }, {
            field : 'stockQty',
            title : 'T_EMC_CSM_STOCK.STOCKQTY',
            readonly : true
        }, {
            field : 'unitName',
            title : 'T_EMC_CSM_STOCK.UNITNAME',
            type : 'choose',
            category : 'unit',
            single : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        unitName : rows[0].unitName,
                    });
                }
            },
            editor : true,
        }, {
            field : 'expireDate',
            title : 'T_EMC_CSM_STOCK.EXPIREDATE',
            type : 'date',
            editor : true,
        }, {
            field : 'lowLimit',
            title : 'T_EMC_CSM_STOCK.LOWLIMIT',
            validators : [ 'number' ],
            editor : true,
        } ];
    },

    getStockBaseInfoFormFields : function() {
        return [ {
            field : 'qty',
            title : 'T_EMC_CSM_STOCK_LOG.ADDQTY',
            validators : [ 'number' ],
            preInsert : true
        }, {
            field : 'vendorId',
            title : 'T_EMC_CSM_STOCK_LOG.VENDORID',
            validators : [ 'strLength[0,16]' ],
            preInsert : true
        }, {
            field : 'price',
            title : 'T_EMC_CSM_STOCK_LOG.PRICE',
            validators : [ 'number' ],
            preInsert : true
        } ];
    },

    getStockInventoryFormFields : function() {
        return [ {
            field : 'qty',
            title : 'T_EMC_CSM_STOCK_LOG.QTY',
            validators : [ 'number' ],
            preInsert : true
        }, {
            field : 'logContent',
            title : 'T_EMC_CSM_STOCK_LOG.LOGCONTENT',
            validators : [ 'strLength[0,20]' ],
            preInsert : true
        } ];
    },

    getStockAbandonFormFields : function() {
        return [ {
            field : 'logContent',
            title : 'T_EMC_CSM_STOCK_LOG.LOGCONTENT',
            validators : [ 'strLength[0,20]' ],
            preInsert : true
        } ];
    },

    getStockRecieveFormFields : function() {
        return [ {
            field : 'qty',
            title : 'T_EMC_CSM_STOCK_LOG.RECIEVEQTY',
            validators : [ 'number' ],
            preInsert : true
        }, {
            field : 'orgName',
            title : 'T_EMC_CSM_STOCK_LOG.RECEIVEDBYORGID',
            type : 'choose',
            category : 'org',
            single : true,
            validators : [ 'notEmpty' ],
            preInsert : true,
            onBeforeChoose : function() {
                return {
                    orgCategory_SEQ : 'department'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        receivedByOrgId : rows[0].id,
                        orgName : rows[0].orgName,
                    });
                }
            },
        }, {
            field : 'userName',
            title : 'T_EMC_CSM_STOCK_LOG.RECEIVEDBYID',
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
            field : 'logContent',
            title : 'T_EMC_CSM_STOCK_LOG.LOGCONTENT',
            validators : [ 'strLength[0,20]' ],
            preInsert : true
        } ];
    },

    getStockLogListColumns : function() {
        return [ {
            field : 'logType',
            title : 'T_EMC_CSM_STOCK_LOG.LOGTYPE',
        }, {
            field : 'previousQty',
            title : 'T_EMC_CSM_STOCK_LOG.PREVIOUSQTY',
        }, {
            field : 'qty',
            title : 'T_EMC_CSM_STOCK_LOG.QTY',
        }, {
            field : 'vendorId',
            title : 'T_EMC_CSM_STOCK_LOG.VENDORID',
        }, {
            field : 'price',
            title : 'T_EMC_CSM_STOCK_LOG.PRICE',
        }, {
            field : 'createdByName',
            title : 'T_EMC_CSM_STOCK_LOG.CREATEDBYNAME',
        }, {
            field : 'createdByOrgName',
            title : 'T_EMC_CSM_STOCK_LOG.CREATEDBYORGNAME',
        }, {
            field : 'createdTime',
            title : 'T_EMC_CSM_STOCK_LOG.CREATEDTIME',
        }, {
            field : 'logContent',
            title : 'T_EMC_CSM_STOCK_LOG.LOGCONTENT',
        }, {
            field : 'ext$.username',
            title : 'T_EMC_CSM_STOCK_LOG.RECEIVEDBYID',
        }, {
            field : 'ext$.orgname',
            title : 'T_EMC_CSM_STOCK_LOG.RECEIVEDBYORGID',
        } ];
    },

});
