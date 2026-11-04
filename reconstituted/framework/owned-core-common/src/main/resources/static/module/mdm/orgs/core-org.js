var coreOrg = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/mdm/orgs',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'id',
            width : 300,
            title : 'T_CORE_ORG.ID',
            type : 'link',
            genericQuery : true
        }, {
            field : 'orgName',
            width : 300,
            title : 'T_CORE_ORG.ORGNAME',
            genericQuery : true
        }, {
            field : 'orgCategory',
            width : 300,
            title : 'T_CORE_ORG.ORGCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'orgcategory'
            }
        } ]
    },

    getChooseListColumns : function() {
        return [ {
            field : 'id',
            width : 300,
            title : 'T_CORE_ORG.ID',
            genericQuery : true
        }, {
            field : 'orgName',
            title : 'T_CORE_ORG.ORGNAME',
            width : 300,
            genericQuery : true
        }, {
            field : 'orgCategory',
            title : 'T_CORE_ORG.ORGCATEGORY',
            width : 300,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'orgcategory'
            }
        } ]
    },

    getSimpleChooseListColumns : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_ORG.ID', 
        }, {
            field : 'orgName',
            width : 300,
            title : 'T_CORE_ORG.ORGNAME',
            genericQuery : true
        } ]
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_ORG.ID',
            preInsert : true,
            validators : [ 'remote[/secure/core/module/mdm/orgs/{id}/action/validate-unique]', 'notEmpty', 'strLength[0,16]' ]
        }, {
            field : 'orgName',
            title : 'T_CORE_ORG.ORGNAME',
            preInsert : true,
            validators : [ 'notEmpty', 'strLength[0,85]' ]
        }, {
            field : 'orgCategory',
            title : 'T_CORE_ORG.ORGCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'orgcategory'
            }
        } ]
    }
});