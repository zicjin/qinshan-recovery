var coreTest = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'id',
            title : 'T_CORE_TEST.ID'
        }, {
            field : 'testName',
            title : 'T_CORE_TEST.TESTNAME'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'testName',
            title : 'T_CORE_TEST.TESTNAME',
            validators : [ 'strLength[0,40]' ],
            preInsert : true
        } ];
    },

    getItemListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'itemName',
            title : 'T_CORE_ITEM.ITEMNAME',
            editor : true
        }, {
            field : 'repeatQty',
            title : 'T_CORE_ITEM.REPEATQTY',
            editor : true
        }, {
            field : 'script',
            title : 'T_CORE_ITEM.SCRIPT',
            formatter : function(index, value, row, text) {
                return '<a href="javascript:;" onclick="coreTest.editPage.loadScriptDetailPage(\'' + row.id + '\',\'' + row.testId + '\')">' + Gikam.propI18N('T_CORE_ITEM.SCRIPT') + '</a>';
            }
        } ];
    },

    getItemFormFields : function() {
        return [ {
            field : 'itemName',
            title : 'T_CORE_ITEM.ITEMNAME',
            validators : [ 'strLength[0,40]' ],
            preInsert : true
        }, {
            field : 'repeatQty',
            title : 'T_CORE_ITEM.REPEATQTY',
            type : 'number',
            preInsert : true
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/script/tests'
});
