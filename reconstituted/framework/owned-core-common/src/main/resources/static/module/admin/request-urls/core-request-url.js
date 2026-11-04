var coreRequestUrl = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'note',
            title : 'T_CORE_REQUEST_URL.NOTE',
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,64]' ],
            width : 100
        }, {
            field : 'url',
            title : 'T_CORE_REQUEST_URL.URL',
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,768]' ],
            width : 200
        }, {
            field : 'method',
            title : 'T_CORE_REQUEST_URL.METHOD',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'requestUrlMethod'
            }
        } ];
    },

    getBaseInfoFormFields : function() {
        return [  {
            field : 'note',
            title : 'T_CORE_REQUEST_URL.NOTE',
            validators : [ 'notEmpty', 'strLength[0,64]' ],
            preInsert : true
        }, {
            field : 'url',
            title : 'T_CORE_REQUEST_URL.URL',
            validators : [ 'notEmpty', 'strLength[0,768]' ],
            preInsert : true
        }, {
            field : 'method',
            title : 'T_CORE_REQUEST_URL.METHOD',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'requestUrlMethod'
            }
        }, ];
    },

    baseUrl : IFM_CONTEXT + '/secure/module/admin/request-urls'
});
