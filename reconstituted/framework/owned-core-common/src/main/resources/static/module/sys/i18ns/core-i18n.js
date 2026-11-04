var coreI18n = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_I18N.CODE',
            field : 'code',
            type : 'link',
            width : 300,
            search : true
        }, {
            title : 'T_CORE_I18N.DESCRIPTION',
            field : 'description',
            genericQuery : true,
            search : true
        } ]
    },

    getBaseInfoFormFields : function() {
        return [ {
            title : 'T_CORE_I18N.CODE',
            field : 'code',
            preInsert : true,
            readonly : true
        }, {
            /* ignore */
        }, {
            title : 'T_CORE_I18N.DESCRIPTION',
            field : 'description',
            validators : [ 'strLength(1,64]', 'notEmpty' ],
            preInsert : true,
        }, {
            title : 'T_CORE_I18N.DEFAULTMESSAGE',
            field : 'defaultMessage',
            validators : [ 'strLength(1,64]', 'notEmpty' ],
            preInsert : true,
        } ]
    },

    getMessageListColumns : function() {
        return [ {
            index : true
        }, {
            title : 'T_CORE_I18N_MESSAGE.I18NCONFIGID',
            field : 'i18nConfigId',
            genericQuery : true,
            readonly : true
        }, {
            title : 'T_CORE_I18N_MESSAGE.LOCALENAME',
            field : 'ext$.localename',
            genericQuery : true,
            readonly : true
        }, {
            title : 'T_CORE_I18N_MESSAGE.MESSAGE',
            field : 'message',
            genericQuery : true,
            editor : true
        } ]
    },
    
    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/i18ns'
});