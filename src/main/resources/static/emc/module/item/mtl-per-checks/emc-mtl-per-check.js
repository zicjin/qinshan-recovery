
var emcMtlPerCheck = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'codename',
            title : 'T_EMC_MTL_PER_CHECK.CODENAME'
        }, {
            field : 'verifyOrg',
            title : 'T_EMC_MTL_PER_CHECK.VERIFYORG'
        }, {
            field : 'cycle',
            title : 'T_EMC_MTL_PER_CHECK.CYCLE'
        }, {
            field : 'remark',
            title : 'T_EMC_MTL_PER_CHECK.REMARK'
        }, {
            field : 'checkReason',
            title : 'T_EMC_MTL_PER_CHECK.CHECKREASON'
        }, {
            field : 'checkEquipt',
            title : 'T_EMC_MTL_PER_CHECK.CHECKEQUIPT'
        }, {
            field : 'checkWay',
            title : 'T_EMC_MTL_PER_CHECK.CHECKWAY'
        }, {
            field : 'checkUserName',
            title : 'T_EMC_MTL_PER_CHECK.CHECKUSERNAME'
        }, {
            field : 'remark',
            title : 'T_EMC_MTL_PER_CHECK.REMARK'
        },{
            field : 'planCategory',
            title : 'T_EMC_YEAR_TRAIN_PLAN.PLANCATEGORY',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'planCategory'
            },
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'codename',
            title : 'T_EMC_MTL_PER_CHECK.CODENAME',
            validators : [ 'strLength[0,13]' ]
        }, {
            field : 'verifyOrg',
            title : 'T_EMC_MTL_PER_CHECK.VERIFYORG',
            validators : [ 'strLength[0,13]' ]
        }, {
            field : 'cycle',
            title : 'T_EMC_MTL_PER_CHECK.CYCLE',
            validators : [ 'strLength[0,13]' ]
        }, {
            field : 'remark',
            title : 'T_EMC_MTL_PER_CHECK.REMARK',
            validators : [ 'strLength[0,66]' ]
        }, {
            field : 'checkReason',
            title : 'T_EMC_MTL_PER_CHECK.CHECKREASON',
            validators : [ 'strLength[0,133]' ]
        }, {
            field : 'checkEquipt',
            title : 'T_EMC_MTL_PER_CHECK.CHECKEQUIPT',
            validators : [ 'strLength[0,13]' ]
        }, {
            field : 'checkWay',
            title : 'T_EMC_MTL_PER_CHECK.CHECKWAY',
            validators : [ 'strLength[0,6]' ]
        }, {
            field : 'checkUserName',
            title : 'T_EMC_MTL_PER_CHECK.CHECKUSERNAME',
            validators : [ 'strLength[0,6]' ]
        }, {
            field : 'remark',
            title : 'T_EMC_MTL_PER_CHECK.REMARK',
            validators : [ 'strLength[0,6]' ]
        },{
            field : 'planCategory',
            title : 'T_EMC_YEAR_TRAIN_PLAN.PLANCATEGORY',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'plancategory'
            },
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/mtl-per-checks'
});
