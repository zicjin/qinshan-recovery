var emcHomeAttachment = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.filename',
            title : 'T_EMC_HOME_ATTACHMENT.FILENAME',
            width : 300,
            type : 'link'
        }, {
            field : 'ext$.fileext',
            title : 'T_EMC_HOME_ATTACHMENT.TYPE'
        }, {
            field : 'endDate',
            title : 'T_EMC_HOME_ATTACHMENT.ENDDATE',
            type : 'date',
            editor : true,
            width : 200
        }, {
            field : 'activatedFlag',
            title : 'T_EMC_HOME_ATTACHMENT.ACTIVATEDFLAG',
            category : 'sys',
            param : {
                codeCategoryId : 'activatedFlag'
            },
            type : 'select'
        }, {
            field : 'activatedByName',
            title : 'T_EMC_HOME_ATTACHMENT.ACTIVATEDBYNAME',
            width : 200
        }, {
            field : 'activatedTime',
            title : 'T_EMC_HOME_ATTACHMENT.ACTIVATEDTIME',
            width : 200
        } ];
    },

    getPermissionEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'userId',
            title : 'T_EMC_HOME_ATTA_PERMISSION.USERID'
        }, {
            field : 'ext$.username',
            title : 'T_EMC_HOME_ATTA_PERMISSION.USERNAME'
        } ];
    },

    getChooseDateFields : function() {
        return [ {
            field : 'endDate',
            title : 'T_EMC_HOME_ATTACHMENT.ENDDATE',
            type : 'date',
            width : 300
        } ];
    },
    
    getDownloadListColumns : function(){

        return [ {
            field : 'ext$.filename',
            title : parent.Gikam.propI18N('T_EMC_HOME_ATTACHMENT.FILENAME'),
            type : 'link',
            width : 190
        }, {
            field : 'endDate',
            title : parent.Gikam.propI18N('T_EMC_HOME_ATTACHMENT.ENDDATE'),
            type : 'date',
            width : 90
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/home-attachments'
});
