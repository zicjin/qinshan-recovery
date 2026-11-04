var coreSms = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_SMS.TYPE',
            field : 'type',
            genericQuery : true,
            search : true
        }, {
            title : 'T_CORE_SMS.USER',
            field : 'user',
            genericQuery : true,
            search : true
        }, {
            title : 'T_CORE_SMS.MOBILE',
            field : 'mobile',
            genericQuery : true,
            search : true
        }, {
            title : 'T_CORE_SMS.STATUS',
            field : 'status',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'smsStatus'
            }
        } ]
    },

    getBaseInfoFormFields : function() {
        return [ {
            title : 'T_CORE_SMS.ID',
            field : 'id',
            readonly : true
        }, {
            title : 'T_CORE_SMS.TYPE',
            field : 'type',
            readonly : true
        }, {
            title : 'T_CORE_SMS.STATUS',
            field : 'status',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'smsStatus'
            },
            readonly : true
        }, {
            title : 'T_CORE_SMS.USER',
            field : 'user',
            readonly : true
        }, {
            title : 'T_CORE_SMS.MOBILE',
            field : 'mobile',
            readonly : true
        }, {
            /* ignore */
        }, {
            title : 'T_CORE_SMS.RETRYQTY',
            field : 'retryQty',
            readonly : true
        }, {
            title : 'T_CORE_SMS.SENDTIME',
            field : 'sendTime',
            readonly : true
        }, {
            /* ignore */
        }, {
            title : 'T_CORE_SMS.MESSAGE',
            field : 'message',
            type : 'textarea',
            height : 50,
            colspan : 3,
            readonly : true
        } ]
    },

    getLineListColumns : function() {
        return [ {
            index : true
        }, {
            title : 'T_CORE_SMS_ERROR_LOG.CREATEDTIME',
            field : 'createdTime',
            type: 'dateTime',
            genericQuery : true,
        }, {
            title : 'T_CORE_SMS_ERROR_LOG.ERRORLOG',
            field : 'errorLog',
            type : 'richText',
            genericQuery : true
        } ]
    },
    
    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/sms'
});