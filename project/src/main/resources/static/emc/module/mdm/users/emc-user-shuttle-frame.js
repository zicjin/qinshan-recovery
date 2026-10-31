var emcUser = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/mdm/users',

    getChooseListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'id',
            title : 'T_CORE_USER.ID',
        }, {
            field : 'userName',
            title : 'T_CORE_USER.USERNAME',
        } ];
    },

});
