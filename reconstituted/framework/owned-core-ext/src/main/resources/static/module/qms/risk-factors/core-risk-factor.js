var coreRiskFactor = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            field : 'riskFactor',
            title : 'T_CORE_RISK_FACTOR.RISKFACTOR',
            type : 'tree'
        } ];
    },
    
    getPreInsertFormFields : function() {
        return [ {
            field : 'riskFactor',
            title : 'T_CORE_RISK_FACTOR.RISKFACTOR',
            validators : [ 'notEmpty' ],
            preInsert : true
        } ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'riskFactor',
            title : 'T_CORE_RISK_FACTOR.RISKFACTOR',
            type : 'tree'
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/qms/risk-factors',

})