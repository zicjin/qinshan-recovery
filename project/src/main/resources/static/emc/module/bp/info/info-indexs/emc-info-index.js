var emcInfoIndex = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/info/info-indexs',

    getBaseInfoFormFields : function() {
        return [ {
            field : 'indexName',
            title : 'T_EMC_INFO_INDEX.INDEXNAME',
            preInsert : true,
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'indexValue',
            title : 'T_EMC_INFO_INDEX.INDEXVALUE',
            preInsert : true,
            validators : [ 'strLength[0,60]' ]
        } ];
    },

    getEditListColumns : function() {
        return [ {
            checkbox : true,
            width : 30
        }, {
            field : 'indexName',
            title : 'T_EMC_INFO_INDEX.INDEXNAME',
            width : 85,
            editor : true,
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'indexValue',
            title : 'T_EMC_INFO_INDEX.INDEXVALUE',
            width : 60,
            editor : true,
            validators : [ 'strLength[0,60]' ]
        } ];
    },

    getCategoryBaseInfoFormFields : function() {
        return [ {
            field : 'category',
            title : 'T_EMC_INFO_INDEX_CATEGORY.CATEGORY',
            preInsert : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'infoIndexCategory',
            }
        } ];
    },

    getCategoryEditListColumns : function() {
        return [ {
            checkbox : true,
            width : 30
        }, {
            field : 'category',
            title : 'T_EMC_INFO_INDEX_CATEGORY.CATEGORY',
            width : 200,
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'infoIndexCategory',
            },
            validators : [ 'strLength[0,36]' ]
        }, {
            field : 'remark',
            title : 'T_EMC_INFO_INDEX_CATEGORY.REMARK',
            editor : true,
            validators : [ 'strLength[0,60]' ]
        } ];
    },

    getCategoryIndexBaseInfoFormFields : function() {
        return [ {
            field : 'orderNo',
            title : 'T_EMC_CATEGORY_INDEX.ORDERNO',
            preInsert : true,
            type : 'number',
            validators : [ 'number' ]
        }, {
            field : 'itemName',
            title : 'T_EMC_CATEGORY_INDEX.ITEMNAME',
            preInsert : true,
            validators : [ 'strLength[0,60]' ]
        } ];
    },

    getCategoryIndexEditListColumns : function() {
        return [ {
            checkbox : true,
            width : 30
        }, {
            field : 'orderNo',
            title : 'T_EMC_CATEGORY_INDEX.ORDERNO',
            width : 100,
            editor : true,
            type : 'number',
            validators : [ 'number' ]
        }, {
            field : 'itemName',
            title : 'T_EMC_CATEGORY_INDEX.ITEMNAME',
            width : 120,
            editor : true,
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'lowLimit',
            title : 'T_EMC_CATEGORY_INDEX.LOWLIMIT',
            width : 100,
            editor : true,
            type : 'number',
            validators : [ 'number' ]
        }, {
            field : 'highLimit',
            title : 'T_EMC_CATEGORY_INDEX.HIGHLIMIT',
            width : 100,
            editor : true,
            type : 'number',
            validators : [ 'number' ]
        }, {
            field : 'revisionRule',
            title : 'T_EMC_CATEGORY_INDEX.REVISIONRULE',
            width : 100,
            editor : true,
            type : 'choose',
            category : 'rounding-rule',
            single : true,
            onAfterChoose : function(back, index) {
                var grid = this;
                if (back) {
                    grid.setData({
                        index : index,
                        'revisionRule' : back
                    });
                }
            },
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'detectLimit',
            title : 'T_EMC_CATEGORY_INDEX.DETECTLIMIT',
            width : 100,
            editor : true,
            type : 'number',
            validators : [ 'number' ]
        } ];
    },

    getCategorySectionBaseInfoFormFields : function() {
        return [ {
            field : 'monitorPointName',
            title : 'T_EMC_CATEGORY_SECTION.MONITORPOINTNAME',
            preInsert : true,
            validators : [ 'strLength[0,60]' ]
        } ];
    },

    getCategorySectionEditListColumns : function() {
        return [ {
            checkbox : true,
            width : 30
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_CATEGORY_SECTION.MONITORPOINTNAME',
            editor : true,
            validators : [ 'strLength[0,60]' ]
        } ];
    }
});
