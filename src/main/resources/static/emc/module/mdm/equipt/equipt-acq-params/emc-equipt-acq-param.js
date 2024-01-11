var emcEquiptAcqParam = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipt-acq-params',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'field',
            title : 'T_EMC_EQUIPT_ACQ_PARAM.FIELD',
            editor : true,
            validators : [ 'strLength[0,12]' ],
        }, {
            field : 'fieldName',
            title : 'T_EMC_EQUIPT_ACQ_PARAM.FIELDNAME',
            editor : true,
            validators : [ 'strLength[0,20]' ],
        } ]
    },

    getChooseListColumns : function() {
        return [ {
            field : 'field',
            title : 'T_EMC_EQUIPT_ACQ_PARAM.FIELD',
        }, {
            field : 'fieldName',
            title : 'T_EMC_EQUIPT_ACQ_PARAM.FIELDNAME',
        } ]
    },

})
