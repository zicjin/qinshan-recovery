var emcAddprice = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'priceName',
            title : 'T_EMC_QUOTATION_ADDPRICE.PRICENAME',
            editor : true,
            width : 200
        }, {
            field : 'price',
            title : 'T_EMC_QUOTATION_ADDPRICE.PRICE',
            editor : true
        }, {
            field : 'remark',
            title : 'T_EMC_QUOTATION_ADDPRICE.REMARK',
            editor : true,
            width : 200
        } ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'priceName',
            title : 'T_EMC_QUOTATION_ADDPRICE.PRICENAME',
            width : 150
        }, {
            field : 'price',
            title : 'T_EMC_QUOTATION_ADDPRICE.PRICE'
        }, {
            field : 'remark',
            title : 'T_EMC_QUOTATION_ADDPRICE.REMARK'
        }, ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/quotation/addprices'
});
