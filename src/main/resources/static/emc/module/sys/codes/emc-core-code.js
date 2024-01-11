Gikam.extend(coreCode,{
    getBaseInfoFormFields : function() {
        return [ {
            title : 'T_CORE_CODE.CODE',
            field : 'code',
            validators : [ 'strLength[0,32]' ],
            preInsert : true
        }, {
            title : 'T_CORE_CODE.CODENAME',
            field : 'codeName',
            validators : [ 'strLength[0,300]' ],
            preInsert : true
        }, {
            title : 'T_CORE_CODE.CODECATEGORYID',
            field : 'codeCategoryId',
            type : 'select',
            category : 'codeCategory',
            param : {

            },
            validators : [ 'notEmpty' ],
            search : true,
            preInsert : true
        }, {
            title : 'T_CORE_CODE.REMARK',
            field : 'remark',
            validators : [ 'strLength[0,85]' ]
        }, {
            title : 'T_CORE_CODE.ORDERNO',
            field : 'orderNo',
            validators : [ 'integer' ]
        } ]
    },
});
