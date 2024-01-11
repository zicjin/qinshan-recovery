var emcConvenMtl = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcConvenMtl.workflow.dbTable), {
            field : 'mtlName',
            title : 'T_EMC_CONVEN_MTL.MTLNAME',
            type : 'link'
        }, {
            field : 'specification',
            title : 'T_EMC_CONVEN_MTL.SPECIFICATION'
        }, {
            field : 'price',
            title : 'T_EMC_CONVEN_MTL.PRICE'
        }, {
            field : 'quantity',
            title : 'T_EMC_CONVEN_MTL.QUANTITY'
        }, {
            field : 'total',
            title : 'T_EMC_CONVEN_MTL.TOTAL'
        }, {
            field : 'remark',
            title : 'T_EMC_CONVEN_MTL.REMARK'
        }, {
            field : 'createdByName',
            title : 'T_EMC_CONVEN_MTL.APPLYUSER'
        }, {
            field : 'createdTime',
            title : 'T_EMC_CONVEN_MTL.APPLYDATE'
        } ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcConvenMtl.workflow.dbTable), {
            field : 'mtlName',
            title : 'T_EMC_CONVEN_MTL.MTLNAME',
            type : 'link'
        }, {
            field : 'specification',
            title : 'T_EMC_CONVEN_MTL.SPECIFICATION'
        }, {
            field : 'price',
            title : 'T_EMC_CONVEN_MTL.PRICE'
        }, {
            field : 'quantity',
            title : 'T_EMC_CONVEN_MTL.QUANTITY'
        }, {
            field : 'total',
            title : 'T_EMC_CONVEN_MTL.TOTAL'
        }, {
            field : 'remark',
            title : 'T_EMC_CONVEN_MTL.REMARK'
        }, {
            field : 'createdByName',
            title : 'T_EMC_CONVEN_MTL.APPLYUSER'
        }, {
            field : 'createdTime',
            title : 'T_EMC_CONVEN_MTL.APPLYDATE'
        } ];
    },

    getCheckListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcConvenMtl.workflow.dbTable), {
            field : 'mtlName',
            title : 'T_EMC_CONVEN_MTL.MTLNAME',
            type : 'link'
        }, {
            field : 'specification',
            title : 'T_EMC_CONVEN_MTL.SPECIFICATION'
        }, {
            field : 'price',
            title : 'T_EMC_CONVEN_MTL.PRICE'
        }, {
            field : 'quantity',
            title : 'T_EMC_CONVEN_MTL.QUANTITY'
        }, {
            field : 'total',
            title : 'T_EMC_CONVEN_MTL.TOTAL'
        }, {
            field : 'remark',
            title : 'T_EMC_CONVEN_MTL.REMARK'
        }, {
            field : 'createdByName',
            title : 'T_EMC_CONVEN_MTL.APPLYUSER'
        }, {
            field : 'createdTime',
            title : 'T_EMC_CONVEN_MTL.APPLYDATE'
        }, {
            field : 'model',
            title : 'T_EMC_CONVEN_MTL.MODEL'
        }, {
            field : 'lvl',
            title : 'T_EMC_CONVEN_MTL.LVL'
        }, {
            field : 'vendor',
            title : 'T_EMC_CONVEN_MTL.VENDOR'
        }, {
            field : 'purchaseDate',
            title : 'T_EMC_CONVEN_MTL.PURCHASEDATE'
        }, {
            field : 'usedOrgName',
            title : 'T_EMC_CONVEN_MTL.USEDORGNAME'
        }, {
            field : 'checkMethod',
            title : 'T_EMC_CONVEN_MTL.CHECKMETHOD'
        }, {
            field : 'checkRecord',
            title : 'T_EMC_CONVEN_MTL.CHECKRECORD'
        }, {
            field : 'checkResult',
            title : 'T_EMC_CONVEN_MTL.CHECKRESULT'
        }, {
            field : 'handleOpinion',
            title : 'T_EMC_CONVEN_MTL.HANDLEOPINION'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'mtlName',
            title : 'T_EMC_CONVEN_MTL.MTLNAME',
            preInsert : true,
            validators : [ 'notEmpty', 'strLength [0,24]' ]
        }, {
            field : 'specification',
            title : 'T_EMC_CONVEN_MTL.SPECIFICATION',
            validators : [ 'strLength [0,24]' ]
        }, {
            field : 'price',
            title : 'T_EMC_CONVEN_MTL.PRICE',
            validators : [ 'number' ]
        }, {
            field : 'quantity',
            title : 'T_EMC_CONVEN_MTL.QUANTITY',
            validators : [ 'number' ]
        }, {
            field : 'total',
            title : 'T_EMC_CONVEN_MTL.TOTAL',
            validators : [ 'number' ]
        }, {
            field : 'remark',
            title : 'T_EMC_CONVEN_MTL.REMARK',
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'model',
            title : 'T_EMC_CONVEN_MTL.MODEL',
            validators : [ 'strLength [0,12]' ]
        }, {
            field : 'lvl',
            title : 'T_EMC_CONVEN_MTL.LVL',
            validators : [ 'strLength [0,12]' ]
        }, {
            field : 'vendor',
            title : 'T_EMC_CONVEN_MTL.VENDOR',
            validators : [ 'strLength [0,40]' ]
        }, {
            field : 'purchaseDate',
            title : 'T_EMC_CONVEN_MTL.PURCHASEDATE',
            type : 'date'
        }, {
            field : 'usedOrgName',
            title : 'T_EMC_CONVEN_MTL.USEDORGNAME',
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
                        usedOrgName : rows[0].orgName
                    });
                }
            }
        }, {
            field : 'checkMethod',
            title : 'T_EMC_CONVEN_MTL.CHECKMETHOD',
            validators : [ 'strLength [0,24]' ]
        }, {
            field : 'checkRecord',
            title : 'T_EMC_CONVEN_MTL.CHECKRECORD',
            validators : [ 'strLength [0,40]' ]
        }, {
            field : 'checkResult',
            title : 'T_EMC_CONVEN_MTL.CHECKRESULT',
            validators : [ 'strLength [0,40]' ]
        }, {
            field : 'handleOpinion',
            title : 'T_EMC_CONVEN_MTL.HANDLEOPINION',
            validators : [ 'strLength [0,40]' ]
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/scm/conven-mtls',

    auditGridId : 'emc-conven-mtl-audit-list-grid',

    itemFolder : 'CONVENMTLS',

    itemName : 'CONVEN_MTL',

    auditModalHeight : 260,

    searchModalHeight : 460,

    workflow : {
        dbTable : 'T_EMC_CONVEN_MTL',
        columns : [ {
            field : 'id',
            title : 'T_EMC_CONVEN_MTL.ID'
        } ]
    }
});
