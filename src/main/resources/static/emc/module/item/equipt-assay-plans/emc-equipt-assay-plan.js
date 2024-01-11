var emcEquiptAssayPlan = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'codename',
            title : 'T_EMC_EQUIPT_ASSAY_PLAN.CODENAME'
        }, {
            field : 'verifyOrg',
            title : 'T_EMC_EQUIPT_ASSAY_PLAN.VERIFYORG'
        }, {
            field : 'cycle',
            title : 'T_EMC_EQUIPT_ASSAY_PLAN.CYCLE'
        }, {
            field : 'warningDays',
            title : 'T_EMC_EQUIPT_ASSAY_PLAN.WARNINGDAYS'
        }, {
            field : 'gist',
            title : 'T_EMC_EQUIPT_ASSAY_PLAN.GIST'
        }, {
            field : 'certCategory',
            title : 'T_EMC_EQUIPT_ASSAY_PLAN.CERTCATEGORY'
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT_ASSAY_PLAN.REMARK'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'codename',
            title : 'T_EMC_EQUIPT_ASSAY_PLAN.CODENAME',
            validators : [ 'strLength[0,13]' ]
        }, {
            field : 'verifyOrg',
            title : 'T_EMC_EQUIPT_ASSAY_PLAN.VERIFYORG',
            validators : [ 'strLength[0,13]' ]
        }, {
            field : 'cycle',
            title : 'T_EMC_EQUIPT_ASSAY_PLAN.CYCLE',
            validators : [ 'strLength[0,13]' ]
        }, {
            field : 'warningDays',
            title : 'T_EMC_EQUIPT_ASSAY_PLAN.WARNINGDAYS',
            validators : [ 'strLength[0,13]' ]
        }, {
            field : 'gist',
            title : 'T_EMC_EQUIPT_ASSAY_PLAN.GIST',
            validators : [ 'strLength[0,13]' ]
        }, {
            field : 'certCategory',
            title : 'T_EMC_EQUIPT_ASSAY_PLAN.CERTCATEGORY',
            validators : [ 'strLength[0,13]' ]
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT_ASSAY_PLAN.REMARK',
            validators : [ 'strLength[0,66]' ]
        }, ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/equipt-assay-plans'
});
