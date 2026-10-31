var emcOrder = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders',

    getItemGroupShuttleFrameColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.testclass',
            title : 'T_EMC_TEST_METHOD.TESTCLASS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'testClass',
            },
        },{
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

    getItemGroupEditDelItemGroupColumns : function() {
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

    getItemGroupEditAddItemGroupColumns : function() {
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

    getItemGroupEditDelItemColumns : function() {
        return [{
            field : 'repeatNo',
            title : 'T_EMC_ORDER_TASK_RESULT.REPEATNO',
        }, {
            field : 'itemName',
            title : 'T_EMC_ORDER_TASK_RESULT.ITEMNAME',
        }, {
            field : 'originalResult',
            title : 'T_EMC_ORDER_TASK_RESULT.ORIGINALRESULT',
        }, {
            field : 'reportResult',
            title : 'T_EMC_ORDER_TASK_RESULT.REPORTRESULT',
            formatter : function(i, v, r) {
                return Gikam.scientificNotation(v);
            }
        },,];
    },

    getItemGroupEditAddItemColumns : function() {
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
        // }, {
        //     field : 'analysisCategory',
        //     title : 'T_EMC_ITEM.ANALYSISCATEGORY',
        //     type : 'select',
        //     category : 'sys',
        //     param : {
        //         codeCategoryId : 'analysisCategory',
        //     },
        }, {
            field : 'unitName',
            title : 'T_EMC_ITEM.UNITNAME'
        },  ];
    },

});
