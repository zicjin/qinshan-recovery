var coreNotificationTemplate = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/item/notification/notification-templates',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        } , {
            field : 'id',
            title : 'T_CORE_NOTIFICATION_TMPL.ID',
            readonly : true,
        }, {
            field : 'title',
            title : 'T_CORE_NOTIFICATION_TMPL.TITLE',
            readonly : true,
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_NOTIFICATION_TMPL.ID',
            readonly : true
        }, {
            field : 'title',
            title : 'T_CORE_NOTIFICATION_TMPL.TITLE',
            validators : [ 'notEmpty', 'strLength[0,32]' ]
        } ];
    },
    
    getBaseInfoPrepareFormFields : function() {
        return [ {
            field : 'viewMethod',
            title : 'T_CORE_NOTIFICATION_TMPL.VIEWMETHOD',
            type : 'select',
            category : 'notification-view-method',
            onChange : function(field, value, rowIndex, oldValue, text) {
                if (value === 'menu') {
                    Gikam.getComp('core-notification-template-edit-list-base-info-form').showFields(['menuName']);
                    Gikam.getComp('core-notification-template-edit-list-base-info-form').hideFields(['pageUrl']);
                } else if (value === 'page') {
                    Gikam.getComp('core-notification-template-edit-list-base-info-form').showFields(['pageUrl']);
                    Gikam.getComp('core-notification-template-edit-list-base-info-form').hideFields(['menuName']);
                } else if (value === 'no') {
                    Gikam.getComp('core-notification-template-edit-list-base-info-form').hideFields(['menuName', 'pageUrl']);
                }
            }
        }, {
            field : 'menuName',
            title : 'T_CORE_NOTIFICATION_TMPL.MENUNAME',
            type : 'choose',
            category : 'menu',
            single : true,
            targetFields : [{'menuId' : 'id'}, {'menuName' : 'menuName'}]
        }, {
            field : 'pageUrl',
            title : 'T_CORE_NOTIFICATION_TMPL.PAGEURL',
            validators : ['strLength[0,120]' ],
            colspan : 2
        }, {
            field : 'content',
            title : 'T_CORE_NOTIFICATION_TMPL.CONTENT',
            type : 'ckeditor',
            colspan : 2
        } ];
    }
});
