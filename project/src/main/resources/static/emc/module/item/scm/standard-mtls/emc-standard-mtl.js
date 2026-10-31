var emcStandardMtl = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcStandardMtl.workflow.dbTable), {
            field : 'mtlName',
            title : 'T_EMC_STANDARD_MTL.MTLNAME',
            type : 'link'
        }, {
            field : 'concent',
            title : 'T_EMC_STANDARD_MTL.CONCENT'
        }, {
            field : 'quantity',
            title : 'T_EMC_STANDARD_MTL.QUANTITY'
        }, {
            field : 'total',
            title : 'T_EMC_STANDARD_MTL.TOTAL'
        }, {
            field : 'standNo',
            title : 'T_EMC_STANDARD_MTL.STANDNO'
        }, {
            field : 'manufacturer',
            title : 'T_EMC_STANDARD_MTL.MANUFACTURER'
        }, {
            field : 'remark',
            title : 'T_EMC_STANDARD_MTL.REMARK'
        }, {
            field : 'createdByName',
            title : 'T_EMC_STANDARD_MTL.APPLYUSER'
        }, {
            field : 'createdTime',
            title : 'T_EMC_STANDARD_MTL.APPLYDATE'
        } ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcStandardMtl.workflow.dbTable), {
            field : 'mtlName',
            title : 'T_EMC_STANDARD_MTL.MTLNAME',
            type : 'link'
        }, {
            field : 'concent',
            title : 'T_EMC_STANDARD_MTL.CONCENT'
        }, {
            field : 'quantity',
            title : 'T_EMC_STANDARD_MTL.QUANTITY'
        }, {
            field : 'total',
            title : 'T_EMC_STANDARD_MTL.TOTAL'
        }, {
            field : 'standNo',
            title : 'T_EMC_STANDARD_MTL.STANDNO'
        }, {
            field : 'manufacturer',
            title : 'T_EMC_STANDARD_MTL.MANUFACTURER'
        }, {
            field : 'remark',
            title : 'T_EMC_STANDARD_MTL.REMARK'
        }, {
            field : 'createdByName',
            title : 'T_EMC_STANDARD_MTL.APPLYUSER'
        }, {
            field : 'createdTime',
            title : 'T_EMC_STANDARD_MTL.APPLYDATE'
        } ];
    },

    getCheckListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcStandardMtl.workflow.dbTable), {
            field : 'mtlName',
            title : 'T_EMC_STANDARD_MTL.MTLNAME',
            type : 'link'
        }, {
            field : 'concent',
            title : 'T_EMC_STANDARD_MTL.CONCENT'
        }, {
            field : 'quantity',
            title : 'T_EMC_STANDARD_MTL.QUANTITY'
        }, {
            field : 'total',
            title : 'T_EMC_STANDARD_MTL.TOTAL'
        }, {
            field : 'standNo',
            title : 'T_EMC_STANDARD_MTL.STANDNO'
        }, {
            field : 'manufacturer',
            title : 'T_EMC_STANDARD_MTL.MANUFACTURER'
        }, {
            field : 'remark',
            title : 'T_EMC_STANDARD_MTL.REMARK'
        }, {
            field : 'createdByName',
            title : 'T_EMC_STANDARD_MTL.APPLYUSER'
        }, {
            field : 'createdTime',
            title : 'T_EMC_STANDARD_MTL.APPLYDATE'
        }, {
            field : 'lvl',
            title : 'T_EMC_STANDARD_MTL.LVL'
        }, {
            field : 'specification',
            title : 'T_EMC_STANDARD_MTL.SPECIFICATION'
        }, {
            field : 'contentRange',
            title : 'T_EMC_STANDARD_MTL.CONTENTRANGE'
        }, {
            field : 'testMethod',
            title : 'T_EMC_STANDARD_MTL.TESTMETHOD'
        }, {
            field : 'techIndex',
            title : 'T_EMC_STANDARD_MTL.TECHINDEX'
        }, {
            field : 'checkRecord',
            title : 'T_EMC_STANDARD_MTL.CHECKRECORD'
        }, {
            field : 'checkResult',
            title : 'T_EMC_STANDARD_MTL.CHECKRESULT'
        }, {
            field : 'checkUser',
            title : 'T_EMC_STANDARD_MTL.CHECKUSER'
        }, {
            field : 'checkDate',
            title : 'T_EMC_STANDARD_MTL.CHECKDATE'
        }, {
            field : 'checkRespUser',
            title : 'T_EMC_STANDARD_MTL.CHECKRESPUSER'
        }, {
            field : 'checkRespDate',
            title : 'T_EMC_STANDARD_MTL.CHECKRESPDATE'
        }, {
            field : 'auditUser',
            title : 'T_EMC_STANDARD_MTL.AUDITUSER'
        }, {
            field : 'auditDate',
            title : 'T_EMC_STANDARD_MTL.AUDITDATE'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'mtlName',
            title : 'T_EMC_STANDARD_MTL.MTLNAME',
            preInsert : true,
            validators : [ 'notEmpty', 'strLength [0,24]' ]
        }, {
            field : 'concent',
            title : 'T_EMC_STANDARD_MTL.CONCENT',
            validators : [ 'strLength [0,12]' ]
        }, {
            field : 'quantity',
            title : 'T_EMC_STANDARD_MTL.QUANTITY',
            validators : [ 'number' ]
        }, {
            field : 'total',
            title : 'T_EMC_STANDARD_MTL.TOTAL',
            validators : [ 'number' ]
        }, {
            field : 'standNo',
            title : 'T_EMC_STANDARD_MTL.STANDNO',
            validators : [ 'strLength [0,12]' ]
        }, {
            field : 'manufacturer',
            title : 'T_EMC_STANDARD_MTL.MANUFACTURER',
            validators : [ 'strLength [0,40]' ]
        }, {
            field : 'remark',
            title : 'T_EMC_STANDARD_MTL.REMARK',
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'lvl',
            title : 'T_EMC_STANDARD_MTL.LVL',
            validators : [ 'strLength [0,12]' ]
        }, {
            field : 'specification',
            title : 'T_EMC_STANDARD_MTL.SPECIFICATION',
            validators : [ 'strLength [0,12]' ]
        }, {
            field : 'contentRange',
            title : 'T_EMC_STANDARD_MTL.CONTENTRANGE',
            validators : [ 'strLength [0,12]' ]
        }, {
            field : 'testMethod',
            title : 'T_EMC_STANDARD_MTL.TESTMETHOD',
            validators : [ 'strLength [0,12]' ]
        }, {
            field : 'techIndex',
            title : 'T_EMC_STANDARD_MTL.TECHINDEX',
            validators : [ 'strLength [0,12]' ]
        }, {
            field : 'checkRecord',
            title : 'T_EMC_STANDARD_MTL.CHECKRECORD',
            validators : [ 'strLength [0,40]' ]
        }, {
            field : 'checkResult',
            title : 'T_EMC_STANDARD_MTL.CHECKRESULT',
            validators : [ 'strLength [0,40]' ]
        }, {
            field : 'checkUser',
            title : 'T_EMC_STANDARD_MTL.CHECKUSER',
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        checkUser : rows[0].userName
                    });
                }
            }
        }, {
            field : 'checkDate',
            title : 'T_EMC_STANDARD_MTL.CHECKDATE',
            type : 'date',
        }, {
            field : 'checkRespUser',
            title : 'T_EMC_STANDARD_MTL.CHECKRESPUSER',
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
            title : 'T_EMC_STANDARD_MTL.CHECKRESPDATE',
            type : 'date',
        }, {
            field : 'auditUser',
            title : 'T_EMC_STANDARD_MTL.AUDITUSER',
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        auditUser : rows[0].userName
                    });
                }
            }
        }, {
            field : 'auditDate',
            title : 'T_EMC_STANDARD_MTL.AUDITDATE',
            type : 'date'
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/scm/standard-mtls',

    auditGridId : 'emc-standard-mtl-audit-list-grid',

    itemFolder : 'STANDARDMTLS',

    itemName : 'STANDARD_MTL',

    auditModalHeight : 300,

    searchModalHeight : 530,

    workflow : {
        dbTable : 'T_EMC_STANDARD_MTL',
        columns : [ {
            field : 'id',
            title : 'T_EMC_STANDARD_MTL.ID'
        } ]
    }
});
