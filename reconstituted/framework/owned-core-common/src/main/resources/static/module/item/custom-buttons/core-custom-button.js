var coreCustomButton = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'buttonName',
            title : 'T_CORE_CUSTOM_BUTTON.BUTTONNAME'
        }, {
            field : 'buttonIcon',
            title : 'T_CORE_CUSTOM_BUTTON.BUTTONICON',
            type : 'select',
            category : 'button-icon'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'buttonName',
            title : 'T_CORE_CUSTOM_BUTTON.BUTTONNAME',
            preInsert : true,
            validators : [ 'strLength[0,20]' ]
        }, {
            field : 'buttonIcon',
            title : 'T_CORE_CUSTOM_BUTTON.BUTTONICON',
            preInsert : true,
            type : 'select',
            category : 'button-icon',
            validators : [ 'strLength[0,20]' ]
        }, {
            field : 'buttonLogic',
            type : 'textarea',
            title : 'T_CORE_CUSTOM_BUTTON.BUTTONLOGIC',
            height : 150,
            preInsert : true
        }];
    },

    getChooseListColumns : function() {
        return [  {
            field : 'buttonName',
            title : 'T_CORE_CUSTOM_BUTTON.BUTTONNAME'
        }, {
            field : 'buttonIcon',
            title : 'T_CORE_CUSTOM_BUTTON.BUTTONICON',
            type : 'select',
            category : 'button-icon'
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/item/custom-buttons'
});
