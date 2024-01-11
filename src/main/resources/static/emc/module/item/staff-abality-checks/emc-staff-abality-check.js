var emcStaffAbalityCheck = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'name',
            title : 'T_EMC_STAFF_ABALITY_CHECK.NAME'
        }, {
            field : 'dept',
            title : 'T_EMC_STAFF_ABALITY_CHECK.DEPT'
        }, {
            field : 'job',
            title : 'T_EMC_STAFF_ABALITY_CHECK.JOB'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'name',
            title : 'T_EMC_STAFF_ABALITY_CHECK.NAME',
            validators : [ 'strLength[0,6]' ]
        }, {
            field : 'dept',
            title : 'T_EMC_STAFF_ABALITY_CHECK.DEPT',
            validators : [ 'strLength[0,33]' ]
        }, {
            field : 'job',
            title : 'T_EMC_STAFF_ABALITY_CHECK.JOB',
            validators : [ 'strLength[0,33]' ]
        }, ];
    },



    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/staff-abality-checks'
});
