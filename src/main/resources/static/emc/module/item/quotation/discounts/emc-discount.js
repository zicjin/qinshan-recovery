var emcDiscount = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'discount',
            title : 'T_EMC_QUOTATION_DISCOUNT.DISCOUNT',
            editor : true
        }, {
            field : 'remark',
            title : 'T_EMC_QUOTATION_DISCOUNT.REMARK',
            editor : true
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_EMC_QUOTATION_DISCOUNT.ID',
            preInsert : true
        }, {
            field : 'discount',
            title : 'T_EMC_QUOTATION_DISCOUNT.DISCOUNT',
            validators : [ 'strLength[0,6]' ]
        }, {
            field : 'remark',
            title : 'T_EMC_QUOTATION_DISCOUNT.REMARK',
            validators : [ 'strLength[0,50]' ]
        }, ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'discount',
            title : 'T_EMC_QUOTATION_DISCOUNT.DISCOUNT'
        }, {
            field : 'remark',
            title : 'T_EMC_QUOTATION_DISCOUNT.REMARK'
        }, ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/quotation/discounts'
});
