var emcResult = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'sampleCompleteDate',
            title : 'T_EMC_ORDER.SAMPLEDATE',
            width : 100,
            fixed : true
        }, {
            field : 'sampleCompleteTime',
            title : 'T_EMC_ORDER.SAMPLETIME',
            width : 100,
            fixed : true
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_FOLDER.MONITORPOINTNAME',
            fixed : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO',
            width : 120
        }, {
            field : 'orderDesc',
            title : 'T_EMC_ORDER.ORDERDESC',
            width : 100
        }, {
            field : 'testOrgName',
            title : 'T_EMC_PROJECT.TESTORGNAME'
        }, {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME'
        }, {
            field : 'folderNo',
            title : 'T_EMC_FOLDER.FOLDERNO',
            width : 100
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_FOLDER.MONITORPOINTNO',
            width : 100
        } ];
    },

});
