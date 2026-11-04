var coreCheckCode = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'code',
            title : 'T_CORE_CHECK_CODE.CODE',
            validators : [ 'notEmpty', 'strLength[0,36]' ],
            editor : true
        }, {
            field : 'codeName',
            title : 'T_CORE_CHECK_CODE.CODENAME',
            validators : [ 'notEmpty', 'strLength[0,90]' ],
            width : 360,
            editor : true
        }, {
            field : 'codeCategory',
            title : 'T_CORE_CHECK_CODE.CODECATEGORY',
            validators : [ 'notEmpty', 'strLength[0,60]' ],
            width : 200,
            editor : true
        }, {
            field : 'groupName',
            title : 'T_CORE_CHECK_CODE.GROUPNAME',
            width : 200,
            validators : [ 'strLength[0,40]' ],
            editor : true
        }, {
            field : 'orderNo',
            title : 'T_CORE_CHECK_CODE.ORDERNO',
            editor : true,
            type : 'number',
            hasArrow : true,
            validators : [ 'notEmpty', 'numRange(0,99999)' ],
            isInteger : true
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'code',
            title : 'T_CORE_CHECK_CODE.CODE',
            validators : [ 'notEmpty', 'strLength[0,36]' ],
            preInsert : true
        }, {
            field : 'codeName',
            title : 'T_CORE_CHECK_CODE.CODENAME',
            validators : [ 'notEmpty', 'strLength[0,90]' ],
            preInsert : true
        }, {
            field : 'codeCategory',
            title : 'T_CORE_CHECK_CODE.CODECATEGORY',
            validators : [ 'notEmpty', 'strLength[0,60]' ],
            preInsert : true
        }, {
            field : 'groupName',
            title : 'T_CORE_CHECK_CODE.GROUPNAME',
            validators : [ 'strLength[0,40]' ]
        }, {
            field : 'orderNo',
            title : 'T_CORE_CHECK_CODE.ORDERNO',
            type : 'number',
            validators : [ 'notEmpty', 'numRange(0,99999)' ],
            hasArrow : true,
            preInsert : true,
            isInteger : true
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/check-codes'
});
