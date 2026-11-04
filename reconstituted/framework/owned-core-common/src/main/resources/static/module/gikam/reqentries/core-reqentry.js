var coreReqEntry = Gikam.getPageObject({
    getChooseListColumns : function() {
        return [ {
            index : true
        }, {
            field : 'requestName',
            title : 'GIKAM.REQENTRY.REQUESTNAME'
        }, {
            field : 'requestUrl',
            title : 'GIKAM.REQENTRY.REQUESTURL'
        }, {
            field : 'pageUrl',
            title : 'GIKAM.REQENTRY.PAGEURL'
        } ]
    }
});