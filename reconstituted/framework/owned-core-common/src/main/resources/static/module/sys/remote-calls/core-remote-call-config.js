//@ sourceURL=core-remote-call-config.js
var coreRemoteCallConfig = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/remote-calls',

    getRemoteCallConfigListColumns : function() {
        return [ {
            checkbox : true
        }, {
            title : 'T_CORE_REMOTE_CALL_CONF.NAME',
            field : 'name',
        }, {
            title : 'T_CORE_REMOTE_CALL_CONF.SYNCH',
            field : 'synch',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            width : 90
        }, {
            title : 'T_CORE_REMOTE_CALL_CONF.CALLCLASS',
            field : 'callClass',
        }, {
            title : 'T_CORE_REMOTE_CALL_CONF.CALLMETHOD',
            field : 'callMethod',
            width : 150
        }, {
            title : 'T_CORE_REMOTE_CALL_CONF.DATAFLOW',
            field : 'dataflow',
            width : 100
        }, {
            title : 'T_CORE_REMOTE_CALL_CONF.LASTSUSPENDEDFLAG',
            field : 'lastSuspendedFlag',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            width : 90
        } ]
    },

    getRemoteCallRequestListColumns : function() {
        return [ {
            checkbox : true
        }, {
            title : 'T_CORE_REMOTE_CALL_REQ.REQUESTDATETIME',
            field : 'requestDateTime',
            width : 200,
        }, {
            title : 'T_CORE_REMOTE_CALL_REQ.STATUS',
            field : 'status',
            type : 'select',
            width : 90,
            category : 'sys',
            param : {
                codeCategoryId : 'remoteCallRequestStatus'
            },
        }, {
            title : 'T_CORE_REMOTE_CALL_REQ.FAILEDCOUNT',
            field : 'failedCount',
            width : 90,
        }, {
            title : 'T_CORE_REMOTE_CALL_REQ.REQUESTDATA',
            field : 'requestData',
            type : 'link',
            width : 210
        } ]
    },

    getRemoteCallResponseListColumns : function() {
        return [ {
            checkbox : true
        }, {
            title : 'T_CORE_REMOTE_CALL_RESP.RESPONSEDATETIME',
            field : 'responseDateTime',
            width : 150,
        }, {
            title : 'T_CORE_REMOTE_CALL_RESP.STATUS',
            field : 'status',
            width : 90,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'remoteCallResponseStatus'
            },
        }, {
            title : 'T_CORE_REMOTE_CALL_RESP.RESPONSEDATA',
            field : 'responseData',
            width : 200
        }, {
            title : 'T_CORE_REMOTE_CALL_RESP.LOG',
            field : 'log',
            width : 150,
            type : 'link',
            titleFormatter : function() {
                return "";
            }
        } ]
    },

    getRemoteCallRequestFields : function() {
        return [ {
            title : 'T_CORE_REMOTE_CALL_REQ.REQUESTDATA',
            field : 'requestData',
            type : 'textarea',
            rows : 10,
            height : 300,
            readonly : true
        } ]
    },
});