var coreLoginLog = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/login-logs',

    getSearchListColumns : function() {
        return [ {
            index : true
        }, {
            title : 'T_CORE_LOGIN_LOG.USERID',
            field : 'userId',
            genericQuery : true
        }, {
            title : 'T_CORE_LOGIN_LOG.USERNAME',
            field : 'ext$.username',
            genericQuery : true
        }, {
            title : 'T_CORE_LOGIN_LOG.STATUS',
            field : 'status',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'loginStatus'
            },
            genericQuery : true,
            width : 100
        }, {
            title : 'T_CORE_LOGIN_LOG.LOGINIP',
            field : 'loginIp',
            genericQuery : true
        }, {
            title : 'T_CORE_LOGIN_LOG.DEVICE',
            field : 'device',
            genericQuery : true
        }, {
            title : 'T_CORE_LOGIN_LOG.BROWSER',
            field : 'browser',
            genericQuery : true,
            width : 150
        }, {
            title : 'T_CORE_LOGIN_LOG.PLATFORM',
            field : 'platform',
            genericQuery : true,
            width : 150
        }, {
            title : 'T_CORE_LOGIN_LOG.LOGINTIME',
            field : 'loginTime',
            width : 200
        }, {
            title : 'T_CORE_LOGIN_LOG.LOGOUTTIME',
            field : 'logoutTime',
            width : 200
        }, {
            title : 'T_CORE_LOGIN_LOG.REMARK',
            field : 'remark',
            genericQuery : true
        } ]
    },

    getOnlineListColumns : function() {
        return [  {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_LOGIN_LOG.USERID',
            field : 'userId',
            genericQuery : true
        }, {
            title : 'T_CORE_LOGIN_LOG.USERNAME',
            field : 'ext$.username',
            genericQuery : true
        }, {
            title : 'T_CORE_LOGIN_LOG.ORGNAME',
            field : 'ext$.orgname',
            width : 200,
            genericQuery : true
        }, {
            title : 'T_CORE_LOGIN_LOG.LOGINIP',
            field : 'loginIp',
            genericQuery : true
        }, {
            title : 'T_CORE_LOGIN_LOG.DEVICE',
            field : 'device',
            genericQuery : true
        }, {
            title : 'T_CORE_LOGIN_LOG.BROWSER',
            field : 'browser',
            genericQuery : true,
            width : 150
        }, {
            title : 'T_CORE_LOGIN_LOG.PLATFORM',
            field : 'platform',
            genericQuery : true,
            width : 150
        }, {
            title : 'T_CORE_LOGIN_LOG.LOGINTIME',
            field : 'loginTime',
            width : 200
        } ]
    }
});