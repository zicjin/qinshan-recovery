var emcMtl = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/mtls',

    /** 标准物质列表 */
    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'mtlNo',
            title : 'T_EMC_MTL.MTLNO',
        }, {
            field : 'mtlLabNo',
            title : 'T_EMC_MTL.MTLLABNO',
            editor : true,
            width : 100
        }, {
            field : 'mtlName',
            title : 'T_EMC_MTL.MTLNAME',
            editor : true,
            width : 100
        }, {
            field : 'stockQty',
            title : 'T_EMC_MTL.STOCKQTY',
            readonly : true,
            width : 100
        }, {
            field : 'mtlCategoryId',
            title : 'T_EMC_MTL.MTLCATEGORYID',
            type : 'select',
            search : true,
            category : 'mtlCategory',
            editor : true,
            param : {
                // 只查询标准物质的分类
                managementType_SEQ : 'stdMaterials'
            },
            width : 100
        }, {
            field : 'harmfulness',
            title : 'T_EMC_MTL.HARMFULNESS',
            editor : true
        }, {
            field : 'ext$.reagentcategory',
            title : 'T_EMC_STOCK.REAGENTCATEGORY',
            type : 'comboBox',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'reagentCategory'
            },
            editor: true,
            editable: true
        } ];
    },

    /** 一般试剂列表 */
    getGeneralEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'mtlNo',
            title : 'T_EMC_MTL.MTLNO',
        }, {
            field : 'mtlName',
            title : 'T_EMC_MTL.MTLNAME',
            editor : true,
            width : 80
        }, {
            field : 'specification',
            title : 'T_EMC_MTL.SPECIFICATION',
            editor : true,
            width : 80
        }, {
            field : 'stockQty',
            title : 'T_EMC_MTL.STOCKQTY',
            readonly : true,
            width : 80
        }, {
            field : 'mtlCategoryId',
            title : 'T_EMC_MTL.MTLCATEGORYID',
            type : 'select',
            search : true,
            category : 'mtlCategory',
            editor : true,
            width : 80,
            param : {
                // 只查询一般试剂分类
                managementType_SEQ : 'genericReagent'
            },
        }, {
            field : 'harmfulness',
            title : 'Cas号',
            editor : true
        } ];
    },

    /** 标准物质添加表单 */
    getBaseInfoFormFields : function() {
        return [ {
            field : 'mtlName',
            title : 'T_EMC_MTL.MTLNAME',
            preInsert : true,
            validators : [ 'strLength[0, 60]' ]
        }, {
            field : 'mtlCategoryId',
            title : 'T_EMC_MTL.MTLCATEGORYID',
            type : 'select',
            search : false,
            category : 'mtlCategory',
            param : {
                // 只查询标准物质的分类
                managementType_SEQ : 'stdMaterials'
            },
            firstBlank : false,
            selectedIndex : 0,
            preInsert : true
        } ,{
            field : 'ext$.reagentcategory',
            title : 'T_EMC_STOCK.REAGENTCATEGORY',
            preInsert : true,
            width : 100,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'reagentCategory'
            },
        }, {
            field : 'mtlNo',
            title : 'T_EMC_MTL.MTLNO',
            preInsert : true,
            validators : [ 'remote[' + emcMtl.baseUrl + '/action/validate-unique{mtlNo}]' ],
        }];
    },

    /** 一般试剂添加表单 */
    getGeneralBaseInfoFormFields : function() {
        return [ {
            field : 'mtlName',
            title : 'T_EMC_MTL.MTLNAME',
            preInsert : true,
            validators : [ 'strLength[0, 60]' ]
        }, {
            field : 'mtlCategoryId',
            title : 'T_EMC_MTL.MTLCATEGORYID',
            type : 'select',
            search : false,
            firstBlank : false,
            category : 'mtlCategory',
            param : {
                // 只查询一般试剂分类
                managementType_SEQ : 'genericReagent'
            },
            preInsert : true,
        }, {
            field : 'mtlNo',
            title : 'T_EMC_MTL.MTLNO',
            preInsert : true,
            validators : [ 'remote[' + emcMtl.baseUrl + '/action/validate-unique{mtlNo}]' ],
        } ];
    },

    /** 标准物质库存列表，已在项目层复写了 */
    getStockListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'batchNo',
            title : 'T_EMC_STOCK.BATCHNO',
            validators : [ 'remote[' + emcMtl.baseUrl + '/stocks/{id}/action/validate-unique{batchNo}]', 'strLength[0,32]' ],
            editor : true,
            width : 160
        }, {
            field : 'ext$.reagentcategory',
            title : 'T_EMC_STOCK.REAGENTCATEGORY',
            editor : true,
            width : 100,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'reagentCategory'
            },
        }/*, {
            field : 'ext$.batchnumber',
            title : 'T_EMC_STOCK.BATCHNUMBER',
            editor : true,
        }*/, {
            field : 'ext$.standardlevel',
            title : 'T_EMC_STOCK.STANDARDLEVEL',
            editor : true,
        }, {
            field : 'stockQty',
            title : 'T_EMC_STOCK.STOCKQTY',
            readonly : true
        }, {
            field : 'unitName',
            title : 'T_EMC_STOCK.UNITNAME',
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
            field : 'lowLimit',
            title : 'T_EMC_STOCK.LOWLIMIT',
            type : 'number',
            editor : true,
        }, {
            field : 'expireDate',
            title : 'T_EMC_STOCK.EXPIREDATE',
            type : 'date',
            editor : true,
        }, {
            field : 'remindDays',
            title : 'T_EMC_STOCK.REMINDDAYS',
            type : 'number',
            isInteger : true,
            editor : true,
        }, {
            field : 'ext$.storagelocation',
            title : 'T_EMC_STOCK.STORAGELOCATION',
            editor : true,
        }, {
            field : 'ext$.stocktime',
            title : 'T_EMC_STOCK.STOCKTIME',
            type :'date',
            editor : true,
        }, {
            field : 'ext$.stockstatus',
            title : 'T_EMC_STOCK.STOCKSTATUS',
        }, {
            field : 'lowLimit',
            title : 'T_EMC_STOCK.LOWLIMIT',
            validators : [ 'number' ],
            editor : true,
        }, {
            field : 'ext$.stockspecification',
            title : 'T_EMC_STOCK.STOCKSPECIFICATION',
            editor : true,
        }, {
            field : 'verdorName',
            title : '生产商',
            editor : true,
        }, {
            field : 'ext$.productiondate',
            title : 'T_EMC_STOCK.PRODUCTIONDATE',
            type : 'date',
            editor : true,
        }, {
            field : 'ext$.storagecondition',
            title : 'T_EMC_STOCK.STORAGECONDITION',
            editor : true,
            type : 'comboBox',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'savedition'
            },
        } ];
    },

    getStockBaseInfoFormFields : function() {
        return [ {
            field : 'ext$.standardlevel',
            title : 'T_EMC_STOCK.STANDARDLEVEL',
            preInsert : true
        }, {
            field : 'ext$.reagentcategory',
            title : 'T_EMC_STOCK.REAGENTCATEGORY',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'reagentCategory'
            },
        }/*, {
            field : 'ext$.batchnumber',
            title : 'T_EMC_STOCK.BATCHNUMBER',
            editor : true,
            preInsert : true
        }*/, {
            field : 'qty',
            title : 'T_EMC_STOCK_LOG.ADDQTY',
            validators : [ 'number' ],
            hasArrow : true,
            range : [1, 100],
            preInsert : true
        }, {
            field: 'verdorName',
            title: 'T_EMC_STOCK_LOG.VENDOR',
            type: 'select',
            search : true,
            category: 'vendor',
            param:{
                vendorCategory_SEQ : 'produce'
            },
            preInsert : true
        }, /*{
            field : 'verdorName',
            title : '供应商',
            preInsert : true
            type : 'choose',
            category : 'vendor',
            single : true,
            preInsert : true,
            onBeforeChoose : function() {
                return {
                    vendorCategory_SEQ : 'produce',
                }
            },
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        vendorId : rows[0].vendorName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    vendorId : '',
                });
            },
        }, */{
            field : 'expireDate',
            title : 'T_EMC_STOCK.EXPIREDATE',
            type : 'date',
            preInsert : true,
        }, {
            field : 'remindDays',
            title : 'T_EMC_STOCK.REMINDDAYS',
            type : 'number',
        }, {
            field : 'ext$.stockspecification',
            title : 'T_EMC_STOCK.STOCKSPECIFICATION',
            preInsert : true,
        }, {
            field : 'ext$.productionno',
            title : '出厂编码',
            preInsert : true,
        }, {
            field : 'ext$.productiondate',
            title : 'T_EMC_STOCK.PRODUCTIONDATE',
            type : 'date',
            preInsert : true,
        }, {
            field : 'ext$.storagecondition',
            title : 'T_EMC_STOCK.STORAGECONDITION',
            editor : true,
            type : 'comboBox',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'savedition'
            },
            preInsert : true,
        } ];
    },

    getStockInventoryFormFields : function() {
        return [ {
            field : 'qty',
            title : 'T_EMC_STOCK_LOG.QTY',
            validators : [ 'number' ],
            preInsert : true
        }, {
            field : 'logContent',
            title : 'T_EMC_STOCK_LOG.LOGCONTENT',
            validators : [ 'strLength[0,20]' ],
            preInsert : true
        } ];
    },

    getStockAbandonFormFields : function() {
        return [ {
            field : 'logContent',
            title : 'T_EMC_STOCK_LOG.LOGCONTENT',
            validators : [ 'strLength[0,20]' ],
            preInsert : true
        } ];
    },

    getStockRecieveFormFields : function() {
        return [ {
            field : 'qty',
            title : 'T_EMC_STOCK_LOG.RECIEVEQTY',
            validators : [ 'number' ],
            preInsert : true,
            value: 1
        }, {
            field : 'userName',
            title : 'T_EMC_STOCK_LOG.RECEIVEDBYID',
            type : 'choose',
            validators : [ 'notEmpty' ],
            preInsert : true,
            category : 'user-simple',
            // single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        receivedById : rows.map(r=>r.id).join(','),
                        receivedbyname: rows.map(r=>r.userName).join(','),
                        userName : rows.map(r=>r.userName).join(','),
                        receivedByOrgId: rows.map(r=>r.orgId).join(','),
                        orgName: rows.map(r=>r.orgName).join(','),
                    });
                }
            },
        }, {
            field : 'orgName',
            title : 'T_EMC_STOCK_LOG.RECEIVEDBYORGID',
            type : 'choose',
            category : 'org-simple',
            single : true,
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
            field : 'logContent',
            title : 'T_EMC_STOCK_LOG.LOGCONTENT',
            validators : [ 'strLength[0,20]' ],
            preInsert : true
        } ];
    },

    getStockReturnFormFields : function() {
        return [ {
            field : 'qty',
            title : 'T_EMC_STOCK_LOG.RETURNQTY',
            validators : [ 'number' ],
            preInsert : true,
            value: 1
        }, {
            field : 'ext$.returnbyname',
            title : 'T_EMC_STOCK_LOG.RETURNBYNAME',
            type : 'choose',
            validators : [ 'notEmpty' ],
            preInsert : true,
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        returnById : rows[0].id,
                        returnByOrgId: rows[0].orgId,
                        ext$ :{
                            returnbyname: rows[0].userName,
                            returnbyorgname: rows[0].orgName
                        }
                    });
                }
            },
        }, {
            field : 'ext$.returnbyorgname',
            title : 'T_EMC_STOCK_LOG.RETURNBYORGNAME',
            type : 'choose',
            category : 'org-simple',
            single : true,
            preInsert : true,
            onBeforeChoose : function() {
                return {
                    orgCategory_SEQ : 'department'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        returnByOrgId : rows[0].id,
                        ext$ :{
                            returnbyorgname: rows[0].orgName
                        }
                    });
                }
            },
        }, {
            field : 'logContent',
            title : 'T_EMC_STOCK_LOG.LOGCONTENT',
            validators : [ 'strLength[0,20]' ],
            preInsert : true
        } ];
    },

    getMaintainListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'category',
            title : 'T_EMC_MTL_MAINTAIN.CATEGORY',
            preInsert : true,
        }, {
            field : 'mtlName',
            title : 'T_EMC_MTL_MAINTAIN.MTLNAME',
        } ];
    },

    getStockLogListColumns : function(type) {
        return [ {
            field : 'logType',
            title : 'T_EMC_STOCK_LOG.LOGTYPE',
        }, {
            field : 'ext$.operateqty',
            title : '操作数量',
        }, {
            field : 'previousQty',
            title : 'T_EMC_STOCK_LOG.PREVIOUSQTY',
        }, {
            field : 'qty',
            title : 'T_EMC_STOCK_LOG.QTY',
        }, {
            field : 'price',
            title : 'T_EMC_STOCK_LOG.PRICE',
        }, {
            field : 'createdByName',
            title : 'T_EMC_STOCK_LOG.CREATEDBYNAME',
        }, {
            field : 'createdByOrgName',
            title : 'T_EMC_STOCK_LOG.CREATEDBYORGNAME',
        }, {
            field : 'createdTime',
            title : 'T_EMC_STOCK_LOG.CREATEDTIME',
        }, {
            field : 'logContent',
            title : 'T_EMC_STOCK_LOG.LOGCONTENT',
            editor : true
        }, {
            field : 'ext$.receivetime',
            title : 'T_EMC_STOCK_LOG.RECEIVETIME',
            type :'date',
            editor : true,
        }, {
            field : 'ext$.setvaluedate',
            title : 'T_EMC_STOCK_LOG.SETVALUEDATE',
            type :'date',
            editor : true,
        }, {
            field : 'ext$.receivedbyname',
            title : 'T_EMC_STOCK_LOG.RECEIVEDBYNAME',
            type : 'choose',
            category : 'user-simple',
            editor : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    var grid = this;
                    grid.setData({
                        index : index,
                        receivedById : rows.map(r=>r.id).join(','),
	                    ext$ :{
	                    	   receivedbyname: rows.map(r=>r.userName).join(',')
	                    }
                    });
                }
            },
        }, {
            field : 'ext$.orgname',
            title : 'T_EMC_STOCK_LOG.RECEIVEDBYORGID',
        }, {
            field : 'ext$.returnbyname',
            title : 'T_EMC_STOCK_LOG.RETURNBYNAME',
            type : 'choose',
            category : 'user-simple',
            single : true,
            editor : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    var grid = this;
                    grid.setData({
                        index : index,
                        returnById : rows[0].id,
                        returnByOrgId: rows[0].orgId,
                        ext$ :{
                            returnbyname: rows[0].userName,
                            returnbyorgname: rows[0].orgName
                        }
                    });
                }
            },
        }, {
            field : 'ext$.returnbyorgname',
            title : 'T_EMC_STOCK_LOG.RETURNBYORGNAME',
        } ];
    },

    getStorageFormFields : function() {
        return [ {
            field : 'qty',
            title : 'T_EMC_STOCK_LOG.ADDQTY',
            validators : [ 'number' ],
            preInsert : true
        }, {
            field : 'vendorId',
            title : 'T_EMC_STOCK_LOG.VENDOR',
            type : 'choose',
            category : 'vendor',
            single : true,
            preInsert : true,
            onBeforeChoose : function() {
                return {
                    vendorCategory_SEQ : 'produce',
                }
            },
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        vendorId : rows[0].vendorName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    vendorId : '',
                });
            },
        } ];
    },

    getStockConcentListColumns : function() {
        return [ {
            field : 'category',
            title : 'T_EMC_STOCK_CONCENT.CATEGORY',
        }, {
            field : 'stdValue',
            title : 'T_EMC_STOCK_CONCENT.STDVALUE',
            editor : true,
            validators : [ 'number' ],
        }, {
            field : 'uncertainty',
            title : 'T_EMC_STOCK_CONCENT.UNCERTAINTY',
            editor : true,
            validators : [ 'number' ],
        }, {
            field : 'uncertaintyPercent',
            title : 'T_EMC_STOCK_CONCENT.UNCERTAINTYPERCENT',
            editor : true,
            validators : [ 'number', 'numRange[0,100]' ],
        }, {
            field : 'concentUnitName',
            title : 'T_EMC_STOCK_CONCENT.CONCENTUNITNAME',
            type : 'choose',
            category : 'unit',
            single : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        concentUnitName : rows[0].unitName,
                    });
                }
            },
            editor : true,
            validators : [ 'strLength[0,21]' ]
        }, {
            field : 'remark',
            title : 'T_EMC_STOCK_CONCENT.REMARK',
            editor : true,
            validators : [ 'strLength[0,170]' ]
        }, {
            field : 'lowLimit',
            title : 'T_EMC_STOCK_CONCENT.LOWLIMIT',
            editor : true,
            sort : false,
        }, {
            field : 'highLimit',
            title : 'T_EMC_STOCK_CONCENT.HIGHLIMIT',
            editor : true,
            sort : false,
            formatter : function(i, v ,r) {
                return Gikam.scientificNotation(v);
            }
        }, {
            field : 'revisionRule',
            title : 'T_EMC_STOCK_CONCENT.REVISIONRULE',
            editor : true,
            type : 'choose',
            category : 'rounding-rule',
            single : true,
            onAfterChoose : function(back, index) {
                if (back) {
                    this.setData({
                        index : index,
                        'revisionRule' : back
                    });
                }
            },
            sort : false
        } ];
    },

    getBatchUpdateStockConcentFields : function() {
        return [ {
            field : 'stdValue',
            title : 'T_EMC_STOCK_CONCENT.STDVALUE',
            validators : [ 'number' ],
        }, {
            field : 'uncertainty',
            title : 'T_EMC_STOCK_CONCENT.UNCERTAINTY',
            validators : [ 'number' ],
        }, {
            field : 'uncertaintyPercent',
            title : 'T_EMC_STOCK_CONCENT.UNCERTAINTYPERCENT',
            editor : true,
            validators : [ 'number', 'numRange[0,100]' ],
        }, {
            field : 'concentUnitName',
            title : 'T_EMC_STOCK_CONCENT.CONCENTUNITNAME',
            type : 'choose',
            category : 'unit',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        concentUnitName : rows[0].unitName,
                    });
                }
            },
            validators : [ 'strLength[0,21]' ]
        }, {
            field : 'remark',
            title : 'T_EMC_STOCK_CONCENT.REMARK',
            validators : [ 'strLength[0,170]' ]
        }, {
            field : 'lowLimit',
            title : 'T_EMC_STOCK_CONCENT.LOWLIMIT',
        }, {
            field : 'highLimit',
            title : 'T_EMC_STOCK_CONCENT.HIGHLIMIT',
            formatter : function(i, v ,r) {
                return Gikam.scientificNotation(v);
            }
        }, {
            field : 'revisionRule',
            title : 'T_EMC_STOCK_CONCENT.REVISIONRULE',
            type : 'choose',
            category : 'rounding-rule',
            single : true,
            onAfterChoose : function(back) {
                if (back) {
                    this.setData({
                        'revisionRule' : back
                    });
                }
            }
        } ];
    },
    getItemListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.testid',
            title : 'T_EMC_MTL_MAINTAIN_ITEM.TESTID',
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_MTL_MAINTAIN_ITEM.TESTNAME',
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_MTL_MAINTAIN_ITEM.METHODNAME',
        }, {
            field : 'ext$.itemname',
            title : 'T_EMC_MTL_MAINTAIN_ITEM.ITEMNAME',
        }, {
            field : 'ext$.itemaliasname',
            title : 'T_EMC_MTL_MAINTAIN_ITEM.ITEMALIASNAME',
        }, {
            field : 'ext$.testcategorydesc',
            title : 'T_EMC_TEST_METHOD.TESTCATEGORY',
        }, ];
    },

    getChooseListColumns : function () {
        return [ {
            field : 'id',
            title : 'T_EMC_MTL.ID',
        }, {
            field : 'mtlName',
            title : 'T_EMC_MTL.MTLNAME',
        }, {
            field : 'specification',
            title : 'T_EMC_MTL.SPECIFICATION',
        }, {
            field : 'stockQty',
            title : 'T_EMC_MTL.STOCKQTY',
        }, {
            field : 'mtlCategoryId',
            title : 'T_EMC_MTL.MTLCATEGORYID',
            type : 'select',
            search : true,
            category : 'mtlCategory',
        }, {
            field : 'harmfulness',
            title : 'T_EMC_MTL.HARMFULNESS',
        } ];
    },

    getDetailChooseListColumns : function () {
        return [ {
            field : 'ext$.mtlname',
            title : 'T_EMC_MTL.MTLNAME',
        }, {
            field : 'batchNo',
            title : 'T_EMC_STOCK.BATCHNO',
        }, {
            field : 'expireDate',
            title : 'T_EMC_STOCK.EXPIREDATE',
        }, {
            field : 'ext$.stdvalue',
            title : 'T_EMC_STOCK_CONCENT.STDVALUE',
        }, {
            field : 'ext$.vendorid',
            title : 'T_EMC_STOCK_LOG.VENDORID',
        }, {
            field : 'ext$.stdvalue',
            title : 'T_EMC_STOCK_CONCENT.STDVALUE',
        }, {
            field : 'ext$.concentunitname',
            title : 'T_EMC_STOCK_CONCENT.CONCENTUNITNAME',
        } ];
    },
    getQcStdStockChooseListColumns : function () {
        return [{
            field : 'mtlId',
            title : 'T_EMC_MTL.ID',
        }, {
            field : 'ext$.mtlname',
            title : 'T_EMC_MTL.MTLNAME',
        }, {
            field : 'ext$.specification',
            title : 'T_EMC_MTL.SPECIFICATION',
        }, {
            field : 'ext$.category',
            title : 'T_EMC_STOCK_CONCENT.CATEGORY1',
        }, {
            field : 'ext$.applyname',
            title : 'T_EMC_MTL_APPLY.APPLYNAME',
        }, {
            field : 'ext$.applyorgname',
            title : 'T_EMC_MTL_APPLY.APPLYORGNAME',
        }, {
            field : 'ext$.applytime',
            type : 'dateTime',
            title : 'T_EMC_MTL_APPLY.APPLYTIME',
        }, {
            field : 'batchNo',
            title : 'T_EMC_STOCK.BATCHNO',
        }, {
            field : 'ext$.stdvalue',
            title : 'T_EMC_STOCK_CONCENT.STDVALUE',
        }, {
            field : 'ext$.concentunitname',
            title : 'T_EMC_STOCK_CONCENT.CONCENTUNITNAME',
        }, {
            field : 'ext$.uncertainty',
            title : 'T_EMC_STOCK_CONCENT.UNCERTAINTY',
        }, {
            field : 'stockQty',
            title : 'T_EMC_MTL.STOCKQTY',
        }, {
            field : 'unitName',
            title : 'T_EMC_STOCK.UNITNAME',
        }, {
            field : 'expireDate',
            type : 'date',
            title : 'T_EMC_STOCK.EXPIREDATE',
        } ];
    },
    getStockChooseListColumns : function () {
        return [ {
            field : 'ext$.mtlname',
            title : 'T_EMC_MTL.MTLNAME',
        }, {
            field : 'mtlId',
            title : 'T_EMC_MTL.ID',
        }, {
            field : 'ext$.mtlcategoryid',
            title : 'T_EMC_MTL.MTLCATEGORYID',
            type : 'select',
            search : false,
            category : 'mtlCategory',
        }, {
            field : 'ext$.specification',
            title : 'T_EMC_MTL.SPECIFICATION',
        }, {
            field : 'batchNo',
            title : 'T_EMC_STOCK.BATCHNO',
        }, {
            field : 'createdTime',
            title : 'T_EMC_STOCK.STOCKEDTIME',
        }, {
            field : 'ext$.price',
            title : 'T_EMC_STOCK_LOG.PRICE',
        }, {
            field : 'stockQty',
            title : 'T_EMC_MTL.STOCKQTY',
        }, {
            field : 'unitName',
            title : 'T_EMC_STOCK.UNITNAME',
        }, {
            field : 'expireDate',
            title : 'T_EMC_STOCK.EXPIREDATE',
        }, {
            field : 'ext$.stdvalue',
            title : 'T_EMC_STOCK_CONCENT.STDVALUE',
        }, {
            field : 'ext$.concentunitname',
            title : 'T_EMC_STOCK_CONCENT.CONCENTUNITNAME',
        }, {
            field : 'ext$.uncertainty',
            title : 'T_EMC_STOCK_CONCENT.UNCERTAINTY',
        }, {
            field : 'ext$.uncertaintypercent',
            title : 'T_EMC_STOCK_CONCENT.UNCERTAINTYPERCENT',
        }, {
            field : 'ext$.vendorid',
            title : 'T_EMC_STOCK_LOG.VENDORID',
        } ];
    },

    getProjectStockChooseListColumns : function () {
        return [ {
            field : 'ext$.mtlname',
            title : 'T_EMC_MTL.MTLNAME',
        }, {
            field : 'mtlId',
            title : 'T_EMC_MTL.ID',
        }, {
            field : 'batchNo',
            title : 'T_EMC_ORDER_ITEM_GROUP.BATCHNO',
        }, {
            field : 'ext$.mtlcategoryid',
            title : 'T_EMC_MTL.MTLCATEGORYID',
            type : 'select',
            search : false,
            category : 'mtlCategory',
        }, {
            field : 'ext$.specification',
            title : 'T_EMC_MTL.SPECIFICATION',
        }, {
            field : 'batchNo',
            title : 'T_EMC_STOCK.BATCHNO',
        }, {
            field : 'createdTime',
            title : 'T_EMC_STOCK.STOCKEDTIME',
        }, {
            field : 'ext$.price',
            title : 'T_EMC_STOCK_LOG.PRICE',
        }, {
            field : 'stockQty',
            title : 'T_EMC_MTL.STOCKQTY',
        }, {
            field : 'unitName',
            title : 'T_EMC_STOCK.UNITNAME',
        }, {
            field : 'expireDate',
            title : 'T_EMC_STOCK.EXPIREDATE',
        }, {
            field : 'ext$.stdvalue',
            title : 'T_EMC_STOCK_CONCENT.STDVALUE',
        }, {
            field : 'ext$.concentunitname',
            title : 'T_EMC_STOCK_CONCENT.CONCENTUNITNAME',
        }, {
            field : 'ext$.uncertainty',
            title : 'T_EMC_STOCK_CONCENT.UNCERTAINTY',
        }, {
            field : 'ext$.uncertaintypercent',
            title : 'T_EMC_STOCK_CONCENT.UNCERTAINTYPERCENT',
        }, {
            field : 'ext$.vendorid',
            title : 'T_EMC_STOCK_LOG.VENDORID',
        } ];
    },

    /** 一般试剂库存列表字段 */
    getGeneralStockListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'batchNo',
            title : '批号',
            validators : [ 'remote[' + emcMtl.baseUrl + '/stocks/{id}/action/validate-unique{batchNo}]', 'strLength[0,32]' ],
            editor : true,
        }, {
            field : 'ext$.storagelocation',
            title : '存放位置',
            editor : true,
        }, {
            field : 'stockQty',
            title : 'T_EMC_STOCK.STOCKQTY',
            readonly : true
        }, {
            field : 'unitName',
            title : 'T_EMC_STOCK.UNITNAME',
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
            title : 'T_EMC_STOCK.EXPIREDATE',
            type : 'date',
            editor : true,
        }, {
            field : 'remindDays',
            title : 'T_EMC_STOCK.REMINDDAYS',
            type : 'number',
            isInteger : true,
            editor : true,
        }, {
            field : 'lowLimit',
            title : 'T_EMC_STOCK.LOWLIMIT',
            validators : [ 'number' ],
            editor : true,
        }, {
            field : 'ext$.stockspecification',
            title : 'T_EMC_STOCK.STOCKSPECIFICATION',
            editor : true,
        }, {
            field : 'fineness',
            title : 'T_EMC_STOCK.FINENESS',
            editor : true,
        }, {
            field : 'ext$.productiondate',
            title : 'T_EMC_STOCK.PRODUCTIONDATE',
            type : 'date',
            editor : true,
        }, {
            field: 'ext$.vendorid',
            title: 'T_EMC_STOCK_LOG.VENDOR',
            type: 'select',
            search : true,
            category: 'vendor',
            param:{
                vendorCategory_SEQ : 'produce'
            },
            editor : true
        }, {
            field : 'ext$.storagecondition',
            title : 'T_EMC_STOCK.STORAGECONDITION',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'savedition'
            },
        } ];
    },

    /** 一般试剂添加库存 */
    getGeneralStockBaseInfoFormFields : function() {
        return [  {
            field : 'qty',
            title : 'T_EMC_STOCK_LOG.ADDQTY',
            validators : [ 'number' ],
            hasArrow : true,
            range : [1, 100],
            preInsert : true
        }, {
            field: 'vendorId',
            title: 'T_EMC_STOCK_LOG.VENDOR',
            type: 'select',
            search : true,
            category: 'vendor',
            param:{
                vendorCategory_SEQ : 'produce'
            },
            preInsert : true
        },/*{
            field : 'vendorId',
            title : 'T_EMC_STOCK_LOG.VENDOR',
            type : 'choose',
            category : 'vendor',
            single : true,
            preInsert : true,
            onBeforeChoose : function() {
                return {
                    vendorCategory_SEQ : 'produce',
                }
            },
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        vendorId : rows[0].vendorName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    vendorId : '',
                });
            },
        },*/ {
            field : 'expireDate',
            title : 'T_EMC_STOCK.EXPIREDATE',
            type : 'date',
            preInsert : true,
        }, {
            field : 'remindDays',
            title : 'T_EMC_STOCK.REMINDDAYS',
            type : 'number',
            isInteger : true,
        },{
            field : 'ext$.stockspecification',
            title : 'T_EMC_STOCK.STOCKSPECIFICATION',
            preInsert : true,
        }, {
            field : 'fineness',
            title : 'T_EMC_STOCK.FINENESS',
            preInsert : true,
        }, {
            field : 'ext$.productiondate',
            title : 'T_EMC_STOCK.PRODUCTIONDATE',
            type : 'date',
            preInsert : true,
        }, {
            field : 'ext$.storagecondition',
            title : 'T_EMC_STOCK.STORAGECONDITION',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'savedition'
            },
            preInsert : true,
        }];
    },

    getMtlCheckSearchColumns : function() {
        return [ {
            field : 'mtlName',
            title : 'T_EMC_MTL.MTLNAME',
        }, {
            field : 'batchNo',
            title : 'T_EMC_STOCK.BATCHNO',
        }, {
            field : 'expireDate',
            title : 'T_EMC_STOCK.EXPIREDATE',
        }, {
            field : 'isTermofValidity',
            title : 'T_EMC_STOCK.ISTERMOFVALIDITY',
        }, {
            field : 'isSuitTemperature',
            title : 'T_EMC_STOCK.ISSUITTEMPERATURE',
        }, {
            field : 'isPackagingDamage',
            title : 'T_EMC_STOCK.ISPACKAGINGDAMAGE',
        }, {
            field : 'isSolutionDeposition',
            title : 'T_EMC_STOCK.ISSOLUTIONDEPOSITION',
        }, {
            field : 'other',
            title : 'T_EMC_STOCK.OTHER',
        } , {
            field : 'checkConclusion',
            title : 'T_EMC_STOCK.CHECKCONCLUSION',
        }  ];
    },

    getBatchInputColumns : function() {
        return [ {
            field : 'ext$.reagentcategory',
            title : 'T_EMC_STOCK.REAGENTCATEGORY',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'reagentCategory'
            },
        }, {
            field : 'ext$.batchnumber',
            title : 'T_EMC_STOCK.BATCHNUMBER',
            editor : true,
        }, {
            field : 'ext$.standardlevel',
            title : 'T_EMC_STOCK.STANDARDLEVEL',
            editor : true,
        }, {
            field : 'unitName',
            title : 'T_EMC_STOCK.UNITNAME',
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
            title : 'T_EMC_STOCK.EXPIREDATE',
            type : 'date',
            editor : true,
        }, {
            field : 'remindDays',
            title : 'T_EMC_STOCK.REMINDDAYS',
            type : 'number',
            isInteger : true,
            editor : true,
        }, {
            field : 'ext$.storagelocation',
            title : 'T_EMC_STOCK.STORAGELOCATION',
            editor : true,
        }, {
            field : 'ext$.stocktime',
            title : 'T_EMC_STOCK.STOCKTIME',
            type :'date',
            editor : true,
        }, {
            field : 'lowLimit',
            title : 'T_EMC_STOCK.LOWLIMIT',
            validators : [ 'number' ],
            editor : true,
        }, {
            field : 'ext$.stockspecification',
            title : 'T_EMC_STOCK.STOCKSPECIFICATION',
            editor : true,
        }, {
            field : 'ext$.productiondate',
            title : 'T_EMC_STOCK.PRODUCTIONDATE',
            type : 'date',
            editor : true,
        }, {
            field : 'ext$.storagecondition',
            title : 'T_EMC_STOCK.STORAGECONDITION',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'savedition'
            },
        }, {
            field : 'ext$.istermofvalidity',
            title : 'T_EMC_STOCK.ISTERMOFVALIDITY',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            field : 'ext$.issuittemperature',
            title : 'T_EMC_STOCK.ISSUITTEMPERATURE',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            field : 'ext$.ispackagingdamage',
            title : 'T_EMC_STOCK.ISPACKAGINGDAMAGE',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            field : 'ext$.issolutiondeposition',
            title : 'T_EMC_STOCK.ISSOLUTIONDEPOSITION',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            field : 'ext$.other',
            title : 'T_EMC_STOCK.OTHER',
            editor : true,
        } , {
            field : 'ext$.checkconclusion',
            title : 'T_EMC_STOCK.CHECKCONCLUSION',
            editor : true,
        }  ];
    },

    getPrepareShowAllField : function() {
        var flag = true;
    	var activedRow = Gikam.getComp('emc-run-sample-test-grid').getActivedRow();
        var _this = this;
        var stockBatchNos ="";
        //批下测试还要对分析项组进行筛选,找到批下的分析项组
        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks' + '/action/select-stock-by-order-task', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', [ activedRow ] ])).done(function(data) {
        	stockBatchNos = data;
        });
        return [ {
            title : '显示测试对应库存',
            field : 'showFlag',
            refreshGrid : true,
            type : 'simpleCheckbox',
            styleFormatter : function() {
                return {
                    paddingLeft : '0'
                }
            },
            onChange : function(field, value, index) {
                workspace.window.showMask(true);
                var grid = Gikam.getComp('emc-mtl-stock-choose-list-grid');
                var showFlags = value;
                if (showFlags == '1') {
                	grid.refresh({
                        requestData : {
                            batchNo_IN :stockBatchNos
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                } else {
                	grid.refresh({
                        requestData : {
                            batchNo_IN :'',

                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                }
            },
        } ]
    },
    getMtlStockRecieveFormFields : function() {
        return [ {
            field : 'batchNo',
            title : 'T_EMC_STOCK.BATCHNO',
            onChange : function(field, value) {
                let _form = this;
                _form.setData({batchNo:value,stockQty : '',stockId: '',mtlId:''});
                if(value){
                    Gikam.post(emcMtl.baseUrl + '/0/stocks/queries',Gikam.getJsonWrapper({
                        f:{status_SEQ: "0", stockQty_NG: 0,batchNo_SEQ: value}
                    })).done(function(page){
                        if(page.total==0){
                            Gikam.alert('未查询到相关库存!<br/>'+value);
                            return;
                        }else{
                            _form.setData({stockQty:page.rows[0].stockQty,stockId:page.rows[0].id,mtlId:page.rows[0].mtlId})
                        }
                    });
                }
            }
        }, {
            field : 'stockQty',
            title : 'T_EMC_STOCK.STOCKQTY',
            readonly : true
        }, {
            field : 'userName',
            title : 'T_EMC_STOCK_LOG.RECEIVEDBYID',
            type : 'choose',
            validators : [ 'notEmpty' ],
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        receivedById : rows[0].id,
                        receivedbyname: rows[0].userName,
                        userName : rows[0].userName,
                        receivedByOrgId : rows[0].orgId,
                        orgName : rows[0].orgName,
                    });
                }
            },
        }, {
            field : 'orgName',
            title : 'T_EMC_STOCK_LOG.RECEIVEDBYORGID',
            type : 'choose',
            category : 'org-simple',
            single : true,
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
            field : 'logContent',
            title : 'T_EMC_STOCK_LOG.LOGCONTENT',
            validators : [ 'strLength[0,20]' ],
        } ];
    },
});
