var emcMtlApply = Gikam.getPageObject({

    baseUrl: IFM_CONTEXT + "/secure/emc/module/item/mtl-applies",

    workflow: {
        dbTable: 'T_EMC_MTL_APPLY',
        columns: [{
            field: 'id',
            title: 'T_EMC_MTL_APPLY.ID'
        }]
    },

    getEditListColumns: function () {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcMtlApply.workflow.dbTable), {
            field : 'mtlId',
            title : 'T_EMC_MTL_APPLY.MTLID',
            width : 100
        }, {
            field : 'ext$.mtlname',
            title : 'T_EMC_MTL_APPLY.MTLNAME',
        }, {
            field : 'ext$.specification',
            title : 'T_EMC_MTL_APPLY.SPECIFICATION',
            width : 160
        }, {
            field : 'ext$.mtlcategoryid',
            title : 'T_EMC_MTL_APPLY.MTLCATEGORYID',
            type : 'select',
            search : true,
            category : 'mtlCategory',
        }, {
            field : 'ext$.batchno',
            title : 'T_EMC_MTL_APPLY.BATCHNO',
        }, {
            field : 'ext$.stdvalue',
            title : 'T_EMC_MTL_APPLY.STDVALUE',
        }, {
            field : 'ext$.stockqty',
            title : 'T_EMC_MTL_APPLY.STOCKQTY',
        }, {
            field : 'applyQty',
            title : 'T_EMC_MTL_APPLY.APPLYQTY',
            editor : true,
            type : 'number',
            hasArrow : true,
        }, {
            field : 'remark',
            title : 'T_EMC_MTL_APPLY.REMARK',
            editor : true
        }, {
            field : 'ext$.harmfulness',
            title : 'T_EMC_MTL_APPLY.HARMFULNESS',
        }, {
            field : 'createdByName',
            title : 'T_EMC_MTL_APPLY.CREATEDBYNAME',
        }, {
            field : 'createdTime',
            title : 'T_EMC_MTL_APPLY.CREATEDTIME',
        } ];
    },

    getAuditListColumns: function () {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcMtlApply.workflow.dbTable), {
            field : 'mtlId',
            title : 'T_EMC_MTL_APPLY.MTLID',
            width : 100
        }, {
            field : 'ext$.mtlname',
            title : 'T_EMC_MTL_APPLY.MTLNAME',
        }, {
            field : 'ext$.specification',
            title : 'T_EMC_MTL_APPLY.SPECIFICATION',
            width : 160
        }, {
            field : 'ext$.mtlcategoryid',
            title : 'T_EMC_MTL_APPLY.MTLCATEGORYID',
            type : 'select',
            search : true,
            category : 'mtlCategory',
        }, {
            field : 'ext$.harmfulness',
            title : 'T_EMC_MTL_APPLY.HARMFULNESS',
        }, {
            field : 'ext$.batchno',
            title : 'T_EMC_MTL_APPLY.BATCHNO',
        }, {
            field : 'ext$.stdvalue',
            title : 'T_EMC_MTL_APPLY.STDVALUE',
        }, {
            field : 'ext$.stockqty',
            title : 'T_EMC_MTL_APPLY.STOCKQTY',
        }, {
            field : 'applyQty',
            title : 'T_EMC_MTL_APPLY.APPLYQTY',
        }, {
            field : 'remark',
            title : 'T_EMC_MTL_APPLY.REMARK',
        }, {
            field : 'createdByName',
            title : 'T_EMC_MTL_APPLY.CREATEDBYNAME',
        }, {
            field : 'createdTime',
            title : 'T_EMC_MTL_APPLY.CREATEDTIME',
        } ];
    },

    getBaseInfoFormFields: function () {
        return [ {
            field: 'ext$.mtlname',
            title: 'T_EMC_MTL_APPLY.MTLNAME',
            validators: ['strLength[0, 18]'],
            preInsert: true,
            type : 'choose',
            category : 'mtl',
            single : true,
            cleanable : false,
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        mtlId : rows[0].id,
                        ext$ : {
                            mtlname : rows[0].mtlName
                        }
                    });
                }
            }
        }, {
            field: 'applyQty',
            title: 'T_EMC_MTL_APPLY.APPLYQTY',
            preInsert: true,
            value : 1,
            validators : [ 'notEmpty', 'number' ],
            type : 'number',
            hasArrow : true,
        } ];
    }

});
