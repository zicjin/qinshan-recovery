var coreQueryConfig = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/core/module/item/query-configs',

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_QUERY_CONFIG.ID',
            readonly : true,
        }, {
            field : 'queryGroup',
            title : 'T_CORE_QUERY_CONFIG.QUERYGROUP',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'queryGroup'
            },
        }, {
            field : 'configName',
            title : 'T_CORE_QUERY_CONFIG.CONFIGNAME',
            validators : [ 'notEmpty', 'strLength[1,64]' ],
            preInsert : true
        }, {
            field : 'initQuery',
            title : 'T_CORE_QUERY_CONFIG.INITQUERY',
            type : 'simpleCheckbox',
        }, {
            field : 'remark',
            title : 'T_CORE_QUERY_CONFIG.REMARK',
            validators : [ 'strLength[0,256]' ],
            type : 'textarea',
            colspan : 2
        }, {
            field : 'sql',
            title : 'T_CORE_QUERY_CONFIG.SQL',
            type : 'codeEditor',
			language:'sql',
            colspan : 2,
            rows : 5,
            height : 180
        } ]
    },

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_QUERY_CONFIG.ID',
            field : 'id',
            type : 'link',
            search : true,
            genericQuery : true,
        }, {
            field : 'queryGroup',
            title : 'T_CORE_QUERY_CONFIG.QUERYGROUP',
            type : 'select',
            category : 'sys',
            width : 300,
            param : {
                codeCategoryId : 'queryGroup'
            }
        }, {
            title : 'T_CORE_QUERY_CONFIG.CONFIGNAME',
            width : 300,
            field : 'configName',
            search : true,
            genericQuery : true,
        }, Gikam.status.getActiveColumn() ]
    },

    getColumnEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, Gikam.status.getSuspendColumn(), {
            title : 'T_CORE_QUERY_CONFIG_COLUMN.COLUMNID',
            field : 'columnId',
            width : 200
        }, {
            title : 'T_CORE_QUERY_CONFIG_COLUMN.COLUMNNAME',
            field : 'columnName',
            editor : true,
            width : 200,
            validators : [ 'strLength[0,32]' ]
        }, {
            title : 'T_CORE_QUERY_CONFIG_COLUMN.TYPE',
            field : 'type',
            editor : true,
            type : 'select',
            category : 'sys',
            width : 200,
            param : {
                codeCategoryId : 'columnType'
            }
        }, {
            title : 'T_CORE_QUERY_CONFIG_COLUMN.TYPECONF',
            field : 'typeConf',
            editor : true,
            type : 'select',
            search : true,
            category : 'codeCategory',
            width : 200,
            param : {}
        }, {
            title : 'T_CORE_QUERY_CONFIG_COLUMN.FILTER',
            field : 'filter',
            type : 'simpleCheckbox',
            editor : true
        } ]
    },

    getSearchConfigListColumns : function() {
        return [ {
            radio : true
        }, {
            index : true
        }, {
            title : 'T_CORE_QUERY_CONFIG.STATISTICALNAME',
            field : 'configName',
            width : 220,
            search : true
        } ]
    },
});