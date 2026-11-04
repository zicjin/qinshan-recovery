var coreScript = Gikam.getPageObject({

    getScriptPannel : function() {
        return [ {
            title: 'CORE.MODULE.ADMIN.SCRIPT.TITLE.SCRIPT',
            fields: [ {
                field : 'script',
                type : 'textarea',
                height : 200,
                validators : [ 'notEmpty' ],
                colspan : 3
            } ]
        }, {
            title: 'CORE.MODULE.ADMIN.SCRIPT.TITLE.RESULT',
            fields: [ {
                field : 'code',
                title : 'CORE.MODULE.ADMIN.SCRIPT.RESULT.CODE',
                readonly : true,
                colspan : 3
            }, {
                field : 'content',
                title : 'CORE.MODULE.ADMIN.SCRIPT.RESULT.CONTENT',
                type : 'textarea',
                height : '200',
                readonly : true,
                colspan : 3
            } ]
        } ]
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/admin/scripts'
});
