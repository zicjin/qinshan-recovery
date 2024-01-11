var emcEquiptPurch = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcEquiptPurch.workflow.dbTable), {
            field : 'equiptName',
            title : 'T_EMC_EQUIPT_PURCH.EQUIPTNAME',
            type : 'link',
        }, {
            field : 'model',
            title : 'T_EMC_EQUIPT_PURCH.MODEL'
        }, {
            field : 'manufacturer',
            title : 'T_EMC_EQUIPT_PURCH.MANUFACTURER',
        }, {
            field : 'purchLoc',
            title : 'T_EMC_EQUIPT_PURCH.PURCHLOC'
        }, {
            field : 'purchQty',
            title : 'T_EMC_EQUIPT_PURCH.PURCHQTY'
        }, {
            field : 'price',
            title : 'T_EMC_EQUIPT_PURCH.PRICE'
        }, {
            field : 'purchAttachName',
            title : 'T_EMC_EQUIPT_PURCH.PURCHATTACHNAME'
        }, {
            field : 'fee',
            title : 'T_EMC_EQUIPT_PURCH.FEE'
        }, {
            field : 'payWay',
            title : 'T_EMC_EQUIPT_PURCH.PAYWAY'
        }, {
            field : 'totalAmount',
            title : 'T_EMC_EQUIPT_PURCH.TOTALAMOUNT'
        }, {
            field : 'useDesc',
            title : 'T_EMC_EQUIPT_PURCH.USEDESC'
        }, {
            field : 'createdByName',
            title : 'T_EMC_EQUIPT_PURCH.APPLYUSER'
        }, {
            field : 'createdTime',
            title : 'T_EMC_EQUIPT_PURCH.APPLYDATE'
        } ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcEquiptPurch.workflow.dbTable), {
            field : 'equiptName',
            title : 'T_EMC_EQUIPT_PURCH.EQUIPTNAME',
            type : 'link',
        }, {
            field : 'model',
            title : 'T_EMC_EQUIPT_PURCH.MODEL'
        }, {
            field : 'manufacturer',
            title : 'T_EMC_EQUIPT_PURCH.MANUFACTURER'
        }, {
            field : 'purchLoc',
            title : 'T_EMC_EQUIPT_PURCH.PURCHLOC'
        }, {
            field : 'purchQty',
            title : 'T_EMC_EQUIPT_PURCH.PURCHQTY'
        }, {
            field : 'price',
            title : 'T_EMC_EQUIPT_PURCH.PRICE'
        }, {
            field : 'purchAttachName',
            title : 'T_EMC_EQUIPT_PURCH.PURCHATTACHNAME'
        }, {
            field : 'fee',
            title : 'T_EMC_EQUIPT_PURCH.FEE'
        }, {
            field : 'payWay',
            title : 'T_EMC_EQUIPT_PURCH.PAYWAY'
        }, {
            field : 'totalAmount',
            title : 'T_EMC_EQUIPT_PURCH.TOTALAMOUNT'
        }, {
            field : 'useDesc',
            title : 'T_EMC_EQUIPT_PURCH.USEDESC'
        }, {
            field : 'createdByName',
            title : 'T_EMC_EQUIPT_PURCH.APPLYUSER'
        }, {
            field : 'createdTime',
            title : 'T_EMC_EQUIPT_PURCH.APPLYDATE'
        } ];
    },

    getCheckListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcEquiptPurch.workflow.dbTable), {
            field : 'equiptName',
            title : 'T_EMC_EQUIPT_PURCH.EQUIPTNAME',
            type : 'link'
        }, {
            field : 'model',
            title : 'T_EMC_EQUIPT_PURCH.MODEL'
        }, {
            field : 'manufacturer',
            title : 'T_EMC_EQUIPT_PURCH.MANUFACTURER'
        }, {
            field : 'purchLoc',
            title : 'T_EMC_EQUIPT_PURCH.PURCHLOC'
        }, {
            field : 'purchQty',
            title : 'T_EMC_EQUIPT_PURCH.PURCHQTY'
        }, {
            field : 'price',
            title : 'T_EMC_EQUIPT_PURCH.PRICE'
        }, {
            field : 'purchAttachName',
            title : 'T_EMC_EQUIPT_PURCH.PURCHATTACHNAME'
        }, {
            field : 'fee',
            title : 'T_EMC_EQUIPT_PURCH.FEE'
        }, {
            field : 'payWay',
            title : 'T_EMC_EQUIPT_PURCH.PAYWAY'
        }, {
            field : 'totalAmount',
            title : 'T_EMC_EQUIPT_PURCH.TOTALAMOUNT'
        }, {
            field : 'useDesc',
            title : 'T_EMC_EQUIPT_PURCH.USEDESC'
        }, {
            field : 'createdByName',
            title : 'T_EMC_EQUIPT_PURCH.APPLYUSER'
        }, {
            field : 'createdTime',
            title : 'T_EMC_EQUIPT_PURCH.APPLYDATE'
        }, {
            field : 'factoryNo',
            title : 'T_EMC_EQUIPT_PURCH.FACTORYNO'
        }, {
            field : 'factoryDate',
            title : 'T_EMC_EQUIPT_PURCH.FACTORYDATE'
        }, {
            field : 'purchDate',
            title : 'T_EMC_EQUIPT_PURCH.PURCHDATE'
        }, {
            field : 'contractNo',
            title : 'T_EMC_EQUIPT_PURCH.CONTRACTNO'
        }, {
            field : 'amount',
            title : 'T_EMC_EQUIPT_PURCH.AMOUNT'
        }, {
            field : 'checkRecord',
            title : 'T_EMC_EQUIPT_PURCH.CHECKRECORD'
        }, {
            field : 'checkResult',
            title : 'T_EMC_EQUIPT_PURCH.CHECKRESULT'
        }, {
            field : 'checkGroup',
            title : 'T_EMC_EQUIPT_PURCH.CHECKGROUP'
        }, {
            field : 'debugUser',
            title : 'T_EMC_EQUIPT_PURCH.DEBUGUSER'
        }, {
            field : 'debugDate',
            title : 'T_EMC_EQUIPT_PURCH.DEBUGDATE'
        }, {
            field : 'checkRespUser',
            title : 'T_EMC_EQUIPT_PURCH.CHECKRESPUSER'
        }, {
            field : 'checkRespDate',
            title : 'T_EMC_EQUIPT_PURCH.CHECKRESPDATE'
        }, {
            field : 'receiveUser',
            title : 'T_EMC_EQUIPT_PURCH.RECEIVEUSER'
        }, {
            field : 'receiveDate',
            title : 'T_EMC_EQUIPT_PURCH.RECEIVEDATE'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'equiptName',
            title : 'T_EMC_EQUIPT_PURCH.EQUIPTNAME',
            validators : [ 'notEmpty', 'strLength [0,24]' ],
            preInsert : true,
        }, {
            field : 'model',
            title : 'T_EMC_EQUIPT_PURCH.MODEL',
            validators : [ 'strLength [0,12]' ]
        }, {
            field : 'manufacturer',
            title : 'T_EMC_EQUIPT_PURCH.MANUFACTURER',
            validators : [ 'strLength [0,24]' ]
        }, {
            field : 'purchLoc',
            title : 'T_EMC_EQUIPT_PURCH.PURCHLOC',
            validators : [ 'strLength [0,80]' ]
        }, {
            field : 'purchQty',
            title : 'T_EMC_EQUIPT_PURCH.PURCHQTY',
            validators : [ 'number' ]
        }, {
            field : 'price',
            title : 'T_EMC_EQUIPT_PURCH.PRICE',
            validators : [ 'number' ]
        }, {
            field : 'purchAttachName',
            title : 'T_EMC_EQUIPT_PURCH.PURCHATTACHNAME',
            validators : [ 'strLength [0,24]' ]
        }, {
            field : 'fee',
            title : 'T_EMC_EQUIPT_PURCH.FEE',
            validators : [ 'number' ]
        }, {
            field : 'payWay',
            title : 'T_EMC_EQUIPT_PURCH.PAYWAY',
            validators : [ 'strLength [0,12]' ]
        }, {
            field : 'totalAmount',
            title : 'T_EMC_EQUIPT_PURCH.TOTALAMOUNT',
            validators : [ 'number' ]
        }, {
            field : 'useDesc',
            title : 'T_EMC_EQUIPT_PURCH.USEDESC',
            type : 'textarea',
            validators : [ 'strLength [0,360]' ],
            rows : 2,
            colspan : 2
        }, {
            field : 'factoryNo',
            title : 'T_EMC_EQUIPT_PURCH.FACTORYNO',
            validators : [ 'strLength [0,12]' ]
        }, {
            field : 'factoryDate',
            title : 'T_EMC_EQUIPT_PURCH.FACTORYDATE',
            type : 'date',
        }, {
            field : 'purchDate',
            title : 'T_EMC_EQUIPT_PURCH.PURCHDATE',
            type : 'date',
        }, {
            field : 'contractNo',
            title : 'T_EMC_EQUIPT_PURCH.CONTRACTNO',
            validators : [ 'strLength [0,12]' ]
        }, {
            field : 'amount',
            title : 'T_EMC_EQUIPT_PURCH.AMOUNT',
            validators : [ 'number' ]
        }, {
            field : 'checkRecord',
            title : 'T_EMC_EQUIPT_PURCH.CHECKRECORD',
            validators : [ 'strLength [0,360]' ]
        }, {
            field : 'checkResult',
            title : 'T_EMC_EQUIPT_PURCH.CHECKRESULT',
            validators : [ 'strLength [0,24]' ]
        }, {
            field : 'checkGroup',
            title : 'T_EMC_EQUIPT_PURCH.CHECKGROUP',
            validators : [ 'strLength [0,24]' ]
        }, {
            field : 'debugUser',
            title : 'T_EMC_EQUIPT_PURCH.DEBUGUSER',
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        debugUser : rows[0].userName
                    });
                }
            }
        }, {
            field : 'debugDate',
            title : 'T_EMC_EQUIPT_PURCH.DEBUGDATE',
            type : 'date'
        }, {
            field : 'checkRespUser',
            title : 'T_EMC_EQUIPT_PURCH.CHECKRESPUSER',
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        checkRespUser : rows[0].userName
                    });
                }
            }
        }, {
            field : 'checkRespDate',
            title : 'T_EMC_EQUIPT_PURCH.CHECKRESPDATE',
            type : 'date'
        }, {
            field : 'receiveUser',
            title : 'T_EMC_EQUIPT_PURCH.RECEIVEUSER',
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        receiveUser : rows[0].userName
                    });
                }
            }
        }, {
            field : 'receiveDate',
            title : 'T_EMC_EQUIPT_PURCH.RECEIVEDATE',
            type : 'date'
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/scm/equipt-purchs',

    auditGridId : 'emc-equipt-purch-audit-list-grid',

    itemFolder : 'EQUIPTPURCHS',

    itemName : 'EQUIPT_PURCH',
    
    auditModalHeight : 400,

    searchModalHeight : 660,

    workflow : {
        dbTable : 'T_EMC_EQUIPT_PURCH',
        columns : [ {
            field : 'id',
            title : 'T_EMC_EQUIPT_PURCH.ID'
        } ]
    }
});
