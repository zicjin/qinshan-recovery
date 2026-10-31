var emcYearTrainPlan = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'trainingContent',
            title : 'T_EMC_YEAR_TRAIN_PLAN.TRAININGCONTENT'
        }, {
            field : 'participants',
            title : 'T_EMC_YEAR_TRAIN_PLAN.PARTICIPANTS'
        }, {
            field : 'trainingWay',
            title : 'T_EMC_YEAR_TRAIN_PLAN.TRAININGWAY'
        }, {
            field : 'editor',
            title : 'T_EMC_YEAR_TRAIN_PLAN.EDITOR'
        }, {
            field : 'auditer',
            title : 'T_EMC_YEAR_TRAIN_PLAN.AUDITER'
        }, {
            field : 'approval',
            title : 'T_EMC_YEAR_TRAIN_PLAN.APPROVAL'
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
        }, {
            field : 'dept',
            title : 'T_EMC_YEAR_TRAIN_PLAN.DEPT'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'trainingContent',
            title : 'T_EMC_YEAR_TRAIN_PLAN.TRAININGCONTENT',
            validators : [ 'strLength[0,266]' ]
        }, {
            field : 'participants',
            title : 'T_EMC_YEAR_TRAIN_PLAN.PARTICIPANTS',
            validators : [ 'strLength[0,133]' ]
        }, {
            field : 'trainingWay',
            title : 'T_EMC_YEAR_TRAIN_PLAN.TRAININGWAY',
            validators : [ 'strLength[0,133]' ]
        }, {
            field : 'editor',
            title : 'T_EMC_YEAR_TRAIN_PLAN.EDITOR',
            validators : [ 'strLength[0,6]' ]
        }, {
            field : 'auditer',
            title : 'T_EMC_YEAR_TRAIN_PLAN.AUDITER',
            validators : [ 'strLength[0,6]' ]
        }, {
            field : 'approval',
            title : 'T_EMC_YEAR_TRAIN_PLAN.APPROVAL',
            validators : [ 'strLength[0,6]' ]
        }, {
            field : 'dept',
            title : 'T_EMC_YEAR_TRAIN_PLAN.DEPT',
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
        }, ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/year-train-plans'
});
