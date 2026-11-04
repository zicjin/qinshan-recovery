var coreEquiptAcqConfig = Gikam.getPageObject({

    getConnectListColumns : function() {
        return [ {
            radio : true
        }, {
            field : 'connectStatus',
            title : 'T_CORE_EQUIPT_ACQ_CONFIG.CONNECTSTATUS',
            type : 'select',
            category : 'equipt-connect-status'
        }, {
            field : 'id',
            title : 'T_CORE_EQUIPT_ACQ_CONFIG.ID'
        }, {
            field : 'ext$.equiptname',
            title : 'T_CORE_EQUIPT_ACQ_CONFIG.EQUIPTNAME'
        }, {
            field : 'acqCategory',
            title : 'T_CORE_EQUIPT_ACQ_CONFIG.ACQCATEGORY',
            category : 'equipt-acq-category',
            type : 'select'
        }, {
            field : 'ip',
            title : 'T_CORE_EQUIPT_ACQ_CONFIG.IP'
        }, {
            field : 'port',
            title : 'T_CORE_EQUIPT_ACQ_CONFIG.PORT'
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/equipt/acq-configs'
});
