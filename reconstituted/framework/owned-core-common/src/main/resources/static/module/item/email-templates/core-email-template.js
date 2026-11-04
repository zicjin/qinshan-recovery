var coreEmailTemplate = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/core/module/item/email-templates',
    
    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_EMAIL_TEMPLATE.ID',
            readonly : true
        }, {
            field : 'templateName',
            title : 'T_CORE_EMAIL_TEMPLATE.TEMPLATENAME',
            readonly : true
        }, {
            field : 'remark',
            title : 'T_CORE_EMAIL_TEMPLATE.REMARK',
            validators : [ 'strLength[0,256]' ],
            type : 'textarea',
            colspan : 2,
            rows : 2
        }, {
            field : 'emailSubject',
            title : 'T_CORE_EMAIL_TEMPLATE.EMAILSUBJECT',
            validators : [ 'notEmpty', 'strLength[0,128]' ],
            type : 'textarea',
            colspan : 2
        }, {
            field : 'emailBody',
            title : 'T_CORE_EMAIL_TEMPLATE.EMAILBODY',
            validators : [ 'notEmpty' ],
            type : 'ckeditor',
            colspan : 2
        } ]
    },
    
    getEditListColumns : function() {
        return [ {
            index : true
        }, {
            title : 'T_CORE_EMAIL_TEMPLATE.ID',
            field : 'id',
            type : 'link',
            search : true,
            width : 300
        }, {
            title : 'T_CORE_EMAIL_TEMPLATE.TEMPLATENAME',
            field : 'templateName',
            search : true,
            width : 300
        } ]
    },
    
    getChooseListColumns : function() {
        return [ {
            index : true
        }, {
            title : 'T_CORE_EMAIL_TEMPLATE.ID',
            field : 'id',
            search : true
        }, {
            title : 'T_CORE_EMAIL_TEMPLATE.TEMPLATENAME',
            field : 'templateName',
            search : true,
            width : 150
        } ]
    }
});