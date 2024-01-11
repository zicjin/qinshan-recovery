var emcFileCategory = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/file-categories',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'fileCategoryNo',
            title : 'T_EMC_FILE_CATEGORY.FILECATEGORYNO',
        }, {
            field : 'fileCategoryName',
            title : 'T_EMC_FILE_CATEGORY.FILECATEGORYNAME',
            editor : true
        }];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'fileCategoryNo',
            title : 'T_EMC_FILE_CATEGORY.FILECATEGORYNO',
            validators : [ 'strLength[0,21]' ],
            preInsert : true,
        }, {
            field : 'fileCategoryName',
            title : 'T_EMC_FILE_CATEGORY.FILECATEGORYNAME',
            validators : [ 'strLength[0,85]' ],
            preInsert: true
        }];
    },

    getOrgPermissionColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'orgId',
            title : 'T_EMC_ORG_PERMISSION.ORGID',
        }, {
            field : 'orgName',
            title : 'T_EMC_ORG_PERMISSION.ORGNAME',
        } ];
    },

});
