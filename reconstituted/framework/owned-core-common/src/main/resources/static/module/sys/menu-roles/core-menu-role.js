var coreMenuRole = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/menu-roles',

    getMatrixColumns : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_MENU.ID',
            type : 'tree',
            width : 200,
            fixed : true,
        }, {
            field : 'menuname',
            title : 'T_CORE_MENU.MENUNAME',
            width : 250,
            fixed : true,
        } ]
    }
});