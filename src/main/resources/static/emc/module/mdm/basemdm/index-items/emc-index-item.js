var emcIndexItem = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/index-items',

    getItemListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'indexItemName',
            width : 155,
            title : 'T_EMC_INDEX_ITEM.INDEXITEMNAME'
        }, ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'indexItemName',
            title : 'T_EMC_INDEX_ITEM.INDEXITEMNAME',
            preInsert : true,
        } ];
    },

    getTestItemListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.testcategorydesc',
            title : 'T_EMC_INDEX_TEST_ITEM.TESTCATEGORY'
        }, {
            field : 'ext$.testmethodid',
            title : 'T_EMC_INDEX_TEST_ITEM.TESTMETHODID',
            width : 90,
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_INDEX_TEST_ITEM.TESTNAME'
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_INDEX_TEST_ITEM.METHODNAME'
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_INDEX_TEST_ITEM.TESTMETHODBASIS'
        }, {
            field : 'ext$.itemname',
            title : 'T_EMC_INDEX_TEST_ITEM.ITEMNAME'
        }, {
            field : 'ext$.itemaliasname',
            title : 'T_EMC_INDEX_TEST_ITEM.ITEMALIASNAME'
        }, ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'indexItemName',
            title : 'T_EMC_INDEX_ITEM.INDEXITEMNAME',
            width : '210%'
        }, ];
    },

});
