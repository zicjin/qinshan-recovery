var coreEquiptAcqScript = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'id',
            title : 'T_CORE_EQUIPT_ACQ_SCRIPT.ID',
            type : 'link'
        }, {
            field : 'description',
            title : 'T_CORE_EQUIPT_ACQ_SCRIPT.DESCRIPTION',
            width : 300
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_EQUIPT_ACQ_SCRIPT.ID',
            readonly : true
        }, {
            field : 'description',
            preInsert : true,
            title : 'T_CORE_EQUIPT_ACQ_SCRIPT.DESCRIPTION',
            validators : [ 'strLength[0,90]', 'notEmpty' ]
        }, {
            field : 'script',
            title : 'T_CORE_EQUIPT_ACQ_SCRIPT.SCRIPT',
            type : 'textarea',
            height : 400,
            colspan : 2
        } ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_EQUIPT_ACQ_SCRIPT.ID'
        }, {
            field : 'description',
            title : 'T_CORE_EQUIPT_ACQ_SCRIPT.DESCRIPTION'
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/equipt/acq-scripts'
});
