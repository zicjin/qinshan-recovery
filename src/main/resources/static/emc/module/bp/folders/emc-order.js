var emcOrder = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders',

    getItemGroupShuttleFrameColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.testmethodtype',
            title : 'T_EMC_TEST_METHOD_TESTMETHODTYPE',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'testMethodType',
            }
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_PROJ_ORDER.TESTNAME',
            width : 100
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_PROJ_ORDER.METHODNAME'
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_PROJ_ORDER.TESTMETHODBASIS'
        }, {
            field : 'itemGroup',
            title : 'T_EMC_PROJ_ORDER.ITEMGROUP'
        } ];
    },

    getItemListColumns : function() {
        return [ {
            index : true
        }, {
            field : 'itemName',
            title : 'T_EMC_ITEM.ITEMNAME'
        }, {
            field : 'itemAliasName',
            title : 'T_EMC_ITEM.ITEMALIASNAME',

        }, {
            field : 'repeatItem',
            title : 'T_EMC_ITEM.REPEATITEM'

        }, {
            field : 'lowLimit',
            title : 'T_EMC_ITEM.LOWLIMIT'
        }, {
            field : 'revisionRule',
            title : 'T_EMC_ITEM.REVISIONRULE'
        }, {
            field : 'unitName',
            title : 'T_EMC_ITEM.UNITNAME'
        }, {
            field : 'defaultResult',
            title : 'T_EMC_ITEM.DEFAULTRESULT',

        } ];
    },

});
