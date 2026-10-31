var emcEquiptRepairPlan = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'maintainRepairDate',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRDATE'
        }, {
            field : 'maintainRepairConcent',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRCONCENT'
        }, {
            field : 'maintainRepairSign',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRSIGN'
        }, {
            field : 'equiptFailure',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.EQUIPTFAILURE'
        }, {
            field : 'maintainrepairunit',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRUNIT'
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

    getBaseInfoFormFields : function() {
        return [ {
            field : 'maintainRepairDate',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRDATE'
        }, {
            field : 'maintainRepairConcent',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRCONCENT',
            validators : [ 'strLength[0,200]' ]
        }, {
            field : 'maintainRepairSign',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRSIGN',
            validators : [ 'strLength[0,66]' ]
        }, {
            field : 'equiptFailure',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.EQUIPTFAILURE',
            validators : [ 'strLength[0,200]' ]
        }, {
            field : 'maintainrepairunit',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRUNIT',
            validators : [ 'strLength[0,66]' ]
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
        }, ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipt-maintain-repairs'
});
