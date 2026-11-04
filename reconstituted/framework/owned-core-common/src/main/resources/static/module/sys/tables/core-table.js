var coreTable = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/tables',

    getChooseListColumns : function() {
        return [ {
            title : 'T_CORE_TABLE.ID',
            field : 'id',
            genericQuery : true,
            width : 300
        }, {
            title : 'T_CORE_TABLE.TABLENAME',
            field : 'tableName',
            width : 300,
            formatter : function(i, v, row) {
                return Gikam.propI18N(row.id + '.SERVICE_NAME');
            },
            filter : false,
            sort : false
        } ]
    },

    getTableColumnChooseListColumns : function() {
        return [ {
            title : 'T_CORE_TABLE.COLUMNID',
            field : 'columnName',
            genericQuery : true,
            width : 200
        }, {
            title : 'T_CORE_TABLE.COLUMNNAME',
            field : 'columnNameI18N',
            width : 300,
            formatter : function(i, v, row) {
                return Gikam.propI18N(coreTable.columnChoosePage.tableName + '.' + row.columnName.toUpperCase());
            }
        } ]
    }
});