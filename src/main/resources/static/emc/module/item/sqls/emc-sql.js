var emcSql = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'sqlNo',
            title : 'T_EMC_SQL.SQLNO'
        }, {
            field : 'content',
            title : 'T_EMC_SQL.CONTENT'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'sqlNo',
            title : 'T_EMC_SQL.SQLNO',
            validators : [ 'remote[' + emcSql.baseUrl + '/action/validate-unique]', 'notEmpty', 'strLength[0,10]' ],
            preInsert : true,
            readonly : true
        }, {
            field : 'content',
            title : 'T_EMC_SQL.CONTENT',
            type : 'textarea',
            height : 300,
            validators : [ 'strLength[0,500]' ],
            
        }, ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/sqls'
});
