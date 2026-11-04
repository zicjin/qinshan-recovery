var coreSqlGenerateTable = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'tableName',
            title : 'T_CORE_SQL_GEN_TABLE.TABLENAME',
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,40]' ]
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'tableName',
            title : 'T_CORE_SQL_GEN_TABLE.TABLENAME',
            validators : [ 'notEmpty', 'strLength[0,40]' ],
            preInsert : true
        }, ];
    },

    getAddFieldListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'field',
            title : 'T_CORE_SQL_GEN_FIELD.FIELD',
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,40]' ],
        }, {
            title : 'T_CORE_SQL_GEN_FIELD.FIELDNAME',
            field : 'fieldName',
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,20]' ],
        }, {
            field : 'fieldType',
            title : 'T_CORE_SQL_GEN_FIELD.FIELDTYPE',
            editor : true,
            type : 'select',
            category : 'sys',
            editor : true,
            width : 112,
            param : {
                codeCategoryId : 'fieldType'
            }
        }, {
            title : 'T_CORE_SQL_GEN_FIELD.FIELDLENGTH',
            field : 'fieldLength',
            width : 80,
            editor : true
        } ];
    },

    getRemoveFieldListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'field',
            title : 'T_CORE_SQL_GEN_FIELD.FIELD',
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,40]' ],
            width : 100

        } ];
    },

    getSqlFields : function() {
        return [ {
            field : 'oracle',
            title : 'T_CORE_SQL_GEN_TABLE.ORACLE',
            type : 'textarea',
            height : 160,
        }, {
            field : 'mysql',
            title : 'T_CORE_SQL_GEN_TABLE.MYSQL',
            type : 'textarea',
            height : 160,
        }, {
            field : 'sqlserver',
            title : 'T_CORE_SQL_GEN_TABLE.SQLSERVER',
            type : 'textarea',
            height : 160,
        }, {
            field : 'pgsql',
            title : 'T_CORE_SQL_GEN_TABLE.PGSQL',
            type : 'textarea',
            height : 160,
        }];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/codegenerator/sql-generate-tables'
});
