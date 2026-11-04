var coreCode = Gikam.getPageObject({

    getCodeCategoryListColumns : function () {
        return [ {
            checkbox : true
        }, {
            field : 'id',
            title : 'T_CORE_CODE_CATEGORY.ID',
            width : 200
        }, {
            field : 'codeCategoryName',
            title : 'T_CORE_CODE_CATEGORY.CODECATEGORYNAME',
            width : 200,
            editor : true
        }, {
            field : 'ext$.codeclassname',
            title : 'T_CORE_CODE_CATEGORY.CODECLASSNAME',
        }, {
            field : 'orderNo',
            title : 'T_CORE_CODE_CATEGORY.ORDERNO',
            width : 80,
            editor : true
        }, {
            field : 'remark',
            title : 'T_CORE_CODE_CATEGORY.REMARK',
            width : 200,
            editor : true
        } ];
    },

    getCodeListColumns : function () {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_CODE.CODE',
            field : 'code',
            width : 150,
            editor : true
        }, {
            title : 'T_CORE_CODE.CODENAME',
            field : 'codeName',
            genericQuery : true,
            width : 150,
            editor : true
        }, {
            title : 'T_CORE_CODE.I18NCODE',
            field : 'i18nCode',
            editor : true
        }, {
            title : 'T_CORE_CODE.ORDERNO',
            field : 'orderNo',
            editor : true
        } ]
    },

    getChooseListColumns : function () {
        return [ {
            title : 'T_CORE_CODE.CODE',
            width : 200,
            field : 'code'
        }, {
            title : 'T_CORE_CODE.CODENAME',
            field : 'codeName',
            width : 350
        } ];
    },

    getBaseInfoFormFields : function () {
        return [ {
            field : 'id',
            title : 'T_CORE_CODE_CATEGORY.ID',
            titleWidth : 120,
            validators : [ 'remote[/secure/core/module/sys/code-categories/{id}/action/validate-unique]', 'notEmpty', 'strLength[0,32]' ],
            preInsert : true
        }, {
            field : 'codeCategoryName',
            title : 'T_CORE_CODE_CATEGORY.CODECATEGORYNAME',
            validators : [ 'notEmpty', 'strLength[0,21]' ],
            titleWidth : 120,
            preInsert : true
        }, {
            field : 'ext$.codeclassname',
            title : 'T_CORE_CODE_CATEGORY.CODECLASSNAME',
            titleWidth : 120,
            readonly : true
        }, {
            field : 'orderNo',
            title : 'T_CORE_CODE_CATEGORY.ORDERNO',
            validators : [ 'notEmpty', 'number' ],
        }, {
            field : 'remark',
            title : 'T_CORE_CODE_CATEGORY.REMARK',
            type : 'textarea',
            height : 100,
            editor : true
        } ];
    },


    getCoreCodePreInsertFields : function () {
        return [ {
            title : 'T_CORE_CODE.CODE',
            field : 'code',
            validators : [ 'strLength[0,32]' ],
            preInsert : true
        }, {
            title : 'T_CORE_CODE.CODENAME',
            field : 'codeName',
            validators : [ 'strLength[0,85]' ],
            preInsert : true
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/codes',
    codeCategoryUrl : IFM_CONTEXT + '/secure/core/module/sys/code-categories'
});