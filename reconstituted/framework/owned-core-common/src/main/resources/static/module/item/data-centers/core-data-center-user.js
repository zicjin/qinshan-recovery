var coreDataCenterUser = Gikam.getPageObject({

    getEditListColumns : function () {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'activatedFlag',
            title : 'T_CORE_DATA_CENTER_USER.ACTIVATEDFLAG',
            type : 'simpleCheckbox',
            editor : true,
            width : 200
        }, {
            field : 'ext$.nodename',
            title : 'T_CORE_DATA_CENTER_USER.DATACENTERNODENAME',
            width : 1000
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/item/data-centers'

});
