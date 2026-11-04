var coreEmail = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/core/module/item/emails',
    
    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_EMAIL.ID',
            readonly : true,
        }, {
            field : 'ext$.successstatus',
            title : 'T_CORE_EMAIL.SUCCESSSTATUS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'lineSucessStatus'
            },
            readonly : true
        }, {
            field : 'configId',
            title : 'T_CORE_EMAIL.CONFIGID',
            readonly : true
        }, {
            field : 'ext$.configname',
            title : 'T_CORE_EMAIL.CONFIGNAME',
            readonly : true
        }, {
            field : 'ext$.qty',
            title : 'T_CORE_EMAIL.QTY',
            readonly : true
        }, {
            field : 'ext$.successqty',
            title : 'T_CORE_EMAIL.SUCCESSQTY',
            readonly : true
        }, {
            field : 'emailSubject',
            title : 'T_CORE_EMAIL.EMAILTITLE',
            type : 'textarea',
            readonly : true,
            colspan : 2
        }, {
            field : 'emailBody',
            title : 'T_CORE_EMAIL.EMAILCONTENT',
            type : 'ckeditor',
            readonly : true,
            colspan : 2
        } ]
    },
    
    getEmailConfigListColumns : function() {
        return [ {
            radio : true
        }, {
            field : 'id',
            title : 'T_CORE_EMAIL.CONFIGID',
            search : true
        }, {
            field : 'configName',
            title : 'T_CORE_EMAIL.CONFIGNAME',
            search : true,
            width : 150
        } ]
    },
    
    getSearchListColumns : function() {
        return [ {
            title : 'T_CORE_EMAIL.ID',
            field : 'id',
            type : 'link',
            search : true,
            width : 200
        }, {
            field : 'ext$.successstatus',
            title : 'T_CORE_EMAIL.SUCCESSSTATUS',
            type : 'select',
            category : 'sys',
            sort : false,
            width : 200,
            param : {
                codeCategoryId : 'lineSucessStatus'
            }
        }, {
            field : 'ext$.qty',
            title : 'T_CORE_EMAIL.QTY',
            width : 200
        }, {
            field : 'ext$.successqty',
            title : 'T_CORE_EMAIL.SUCCESSQTY',
            width : 200
        }, {
            title : 'T_CORE_EMAIL.CREATEDTIME',
            field : 'createdTime',
            width : 170
        } ]
    },
    
    getEmailReceipentListColumns : function() {
        return [ {
            field : 'receipentId',
            title : 'T_CORE_EMAIL_RECEIPENT.RECEIPENTID'
        }, {
            field : 'receipentName',
            title : 'T_CORE_EMAIL_RECEIPENT.RECEIPENTNAME'
        }, {
            field : 'receipentEmail',
            title : 'T_CORE_EMAIL_RECEIPENT.RECEIPENTEMAIL'
        }, {
            field : 'success',
            title : 'T_CORE_EMAIL_RECEIPENT.SUCCESS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'success'
            }
        }, {
            field : 'reattempt',
            title : 'T_CORE_EMAIL_RECEIPENT.REATTEMPT'
        }, {
            title : 'T_CORE_EMAIL_RECEIPENT.CREATEDTIME',
            field : 'createdTime'
        } ]
    }
});