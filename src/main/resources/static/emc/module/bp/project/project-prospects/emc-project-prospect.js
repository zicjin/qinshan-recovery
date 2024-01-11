var emcProjectProspect = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/project/projects',
    
    getProspectFormFields : function() {
        return [ {
            field : 'prospectDate',
            title : 'T_EMC_PROJECT.PROSPECTDATE',
            type : 'date'
        }, {
            field : 'opinion',
            title : 'T_EMC_PROJECT.OPINION',
            type : 'textarea'
        }, {
            field : 'safetyMeasures',
            title : 'T_EMC_PROJECT.SAFETYMEASURES',
            type : 'textarea'
        }, {
            field : 'monitorCondition',
            title : 'T_EMC_PROJECT.MONITORCONDITION',
            type : 'textarea'
        }, {
            field : 'prospecRemark',
            title : 'T_EMC_PROJECT.PROSPECREMARK',
            type : 'textarea',
            height : '300'
        } ]
    },

    workflow : {
        dbTable : 'T_EMC_PROJECT',
        columns : [ {
            field : 'id',
            title : 'T_EMC_PROJECT.ID'
        } ]
    }
});
