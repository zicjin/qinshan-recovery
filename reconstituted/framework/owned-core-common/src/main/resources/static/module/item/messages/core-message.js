var coreMessage = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/core/module/item/messages',

    dbTable : 'T_CORE_MESSAGE',

    getBaseInfoFormFields : function() {
        return [ {
            field : 'messageSubject',
            title : 'T_CORE_MESSAGE.MESSAGESUBJECT',
            validators : [ 'strLength[0,85]' ]
        }, {
            field : 'messageContent',
            title : 'T_CORE_MESSAGE.MESSAGECONTENT',
            type : 'ckeditor'
        } ]
    },

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'activatedFlag',
            title : 'T_CORE_MESSAGE.ACTIVATEDFLAG',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'messagestatus'
            },
            width : 100
        }, {
            title : 'T_CORE_MESSAGE.MESSAGESUBJECT',
            field : 'messageSubject',
            type : 'link',
            genericQuery : true,
            width : 500,
            search : true
        }, {
            title : 'T_CORE_MESSAGE_RECEIPENT.ACTIVATEDTIME',
            field : 'activatedTime',
            width : 150
        } ]
    },

    getReceipentEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_MESSAGE_RECEIPENT.RECEIPENTID',
            field : 'receipentId'
        }, {
            title : 'T_CORE_MESSAGE_RECEIPENT.RECEIPENTNAME',
            field : 'receipentName',
            genericQuery : true,
            width : 200
        }, {
            field : 'receivedFlag',
            title : 'T_CORE_MESSAGE_RECEIPENT.RECEIVEDFLAG',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'messagereceiveflag'
            }
        }, {
            title : 'T_CORE_MESSAGE_RECEIPENT.RECEIVEDTIME',
            field : 'receivedTime',
            width : 150
        } ]
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'ext$.receivedflag',
            title : 'T_CORE_MESSAGE_RECEIPENT.RECEIVEDFLAG',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'messagereceiveflag'
            },
            width : 100
        }, {
            title : 'T_CORE_MESSAGE.MESSAGESUBJECT',
            field : 'messageSubject',
            type : 'link',
            width : 500,
            search : true
        }, {
            title : 'T_CORE_MESSAGE_RECEIPENT.ACTIVATEDTIME',
            field : 'activatedTime',
            width : 150
        }, {
            title : 'T_CORE_MESSAGE_RECEIPENT.RECEIVEDTIME',
            field : 'ext$.receivedtime',
            width : 150
        } ]
    },

    getQueryListColumns : function() {
        return [ {
            index : true
        }, {
            title : 'T_CORE_MESSAGE.MESSAGESUBJECT',
            field : 'ext$.messagesubject',
            type : 'link',
            search : true
        }, {
            title : 'T_CORE_MESSAGE_RECEIPENT.ACTIVATEDTIME',
            field : 'ext$.activatedtime'
        } ]
    }

});