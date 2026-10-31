var emcStaffAbility = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'name',
            title : 'T_EMC_STAFF_ABILITY.NAME'
        }, {
            field : 'dept',
            title : 'T_EMC_STAFF_ABILITY.DEPT'
        }, {
            field : 'job',
            title : 'T_EMC_STAFF_ABILITY.JOB'
        }, {
            field : 'technicalTitle',
            title : 'T_EMC_STAFF_ABILITY.TECHNICALTITLE'
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
            field : 'name',
            title : 'T_EMC_STAFF_ABILITY.NAME',
            validators : [ 'strLength[0,6]' ]
        }, {
            field : 'dept',
            title : 'T_EMC_STAFF_ABILITY.DEPT',
            validators : [ 'strLength[0,13]' ]
        }, {
            field : 'job',
            title : 'T_EMC_STAFF_ABILITY.JOB',
            validators : [ 'strLength[0,13]' ]
        }, {
            field : 'technicalTitle',
            title : 'T_EMC_STAFF_ABILITY.TECHNICALTITLE',
            validators : [ 'strLength[0,13]' ]
        }, {
            field : 'planCategory',
            title : 'T_EMC_STAFF_ABILITY.PLANCATEGORY',
            validators : [ 'strLength[0,13]' ]
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

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/staff-abilitys'
});
