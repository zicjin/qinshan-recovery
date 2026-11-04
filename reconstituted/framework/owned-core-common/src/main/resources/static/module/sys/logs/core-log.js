var coreLog = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/core/module/sys/logs',

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, {
            title : 'T_CORE_LOG.NAME',
            field : 'name',
            width : 250,
            search : true
        }, {
            title : 'T_CORE_LOG.MODULE',
            field : 'module',
            search : true
        }, {
            title : 'T_CORE_LOG.TYPE',
            field : 'type',
            search : true
        }, {
            title : 'T_CORE_LOG.SIGNATURE',
            field : 'signature',
            search : true
        }, {
            title : 'T_CORE_LOG.REQUESTURI',
            field : 'requestUri',
            search : true
        }, {
            title : 'T_CORE_LOG.CONTENT',
            field : 'content',
            type : 'link',
            search : true,
            renderToText : true
        }, {
            title : 'T_CORE_LOG.USERNAME',
            field : 'userName',
            search : true
        }, {
            title : 'T_CORE_LOG.LOGINIP',
            field : 'loginIp'
        }, {
            title : 'T_CORE_LOG.STARTTIME',
            field : 'startTime',
            type : 'datetime',
            width : 200
        }, {
            title : 'T_CORE_LOG.ENDTIME',
            field : 'endTime',
            type : 'datetime',
            width : 200
        } ]
    }
});