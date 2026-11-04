//@ sourceURL=lcdp-resource.js
var lcdpResource = Gikam.getPageObject({

    getPageChooseListColumns : function() {
        return [ {
            field : 'path',
            title : 'T_LCDP_RESOURCE.PAGENAME',
            width : 300
        }, {
            field : 'resourceDesc',
            title : 'T_LCDP_RESOURCE.PAGEDESC',
            width : 200
        }, {
            field : 'ext$.category',
            title : 'T_LCDP_RESOURCE.PAGE_CATEGORY',
            width : 200
        },{
            field : 'ext$.module',
            title : 'T_LCDP_RESOURCE.PAGE_MODULE',
            width : 200
        },];
    },

    getMethodChooseListColumns : function() {
        return [ {
            field : 'methodPath',
            title : 'T_LCDP_SERVER_SCRIPT_METHOD.PATH',
        }, {
            field : 'mappingType',
            title : 'T_LCDP_SERVER_SCRIPT_METHOD.MAPPINGTYPE',
            width : 200
        }, {
            field : 'methodDesc',
            title : 'T_LCDP_SERVER_SCRIPT_METHOD.METHODDESC',
            width : 200
        }];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/lcdp/resources'
});
