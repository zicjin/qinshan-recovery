var coreRoleUser = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/core/module/sys/role-users',

    getMatrixColumns : function() {
        return [ {
            index : true,
            fixed : true,
        }, {
            title : 'T_CORE_USER.STATUS',
            field : 'status',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'userStatus'
            },
            fixed : true,
            width : 100
        }, {
            title : 'T_CORE_USER.USERNAME',
            field : 'username',
            fixed : true,
            width : 100
        } ]
    }
});