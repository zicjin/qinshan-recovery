var codeCategory = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/code-categories',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        },{
            field : 'orderNo',
            title : 'T_CORE_CODE_CATEGORY.ORDERNO',
            width : 80
        },{
            field : 'id',
            title : 'T_CORE_CODE_CATEGORY.ID',
            genericQuery : true,
            type : 'link',
            width : 200
        },{
            field : 'codeCategoryName',
            title : 'T_CORE_CODE_CATEGORY.CODECATEGORYNAME',
            width : 200
        },{
            field : 'ext$.codeclassname',
            title : 'T_CORE_CODE_CATEGORY.CODECLASSNAME',
        },{
            field : 'remark',
            title : 'T_CORE_CODE_CATEGORY.REMARK',
            width : 200
        }];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_CODE_CATEGORY.ID',
            validators : ['remote[/secure/core/module/sys/code-categories/{id}/action/validate-unique]','notEmpty', 'strLength[0,32]'],
            preInsert : true
        },{
            field : 'codeCategoryName',
            title : 'T_CORE_CODE_CATEGORY.CODECATEGORYNAME',
            validators : ['notEmpty', 'strLength[0,21]'],
            preInsert : true
        },{
            field : 'ext$.codeclassname',
            title : 'T_CORE_CODE_CATEGORY.CODECLASSNAME',
            readonly:true
        },{
            field : 'orderNo',
            title : 'T_CORE_CODE_CATEGORY.ORDERNO',
            validators : [ 'notEmpty', 'number' ],
            preInsert : true
        }, {
            field : 'remark',
            title : 'T_CORE_CODE_CATEGORY.REMARK',
            type : 'textarea',
            colspan : 2,
            height : 100,
        }];
    },

});
