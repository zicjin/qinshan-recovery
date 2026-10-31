var emcMtlCategory = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/mtl-categories',

    getMtlCategoryColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'mtlCategoryId',
            title : 'T_EMC_MTL_CATEGORY.MTLCATEGORYID',
            preInsert : true,
            validators : [ 'strLength[0,16]', 'notEmpty' ],
            editor : true
        }, {
            field : 'mtlCategoryName',
            title : 'T_EMC_MTL_CATEGORY.MTLCATEGORYNAME',
            preInsert : true,
            validators : [ 'strLength[0,60]', 'notEmpty' ],
            editor : true
        }, {
            field : 'managementType',
            title : 'T_EMC_MTL_CATEGORY.MANAGEMENTTYPE',
            validators : [ 'notEmpty' ],
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'managementType',
            },
            editor : true
        }, {
            field : 'mtlCategoryDesc',
            title : 'T_EMC_MTL_CATEGORY.MTLCATEGORYDESC',
            validators : [ 'strLength[0,120]' ],
            editor : true
        } ];
    },

    getPreInsertFields : function() {
        return [ {
            field : 'mtlCategoryId',
            title : 'T_EMC_MTL_CATEGORY.MTLCATEGORYID',
            preInsert : true,
            validators : [ 'strLength[0,16]' ],
        }, {
            field : 'mtlCategoryName',
            title : 'T_EMC_MTL_CATEGORY.MTLCATEGORYNAME',
            preInsert : true,
            validators : [ 'strLength[0,21]' ],
        }, {
            field : 'managementType',
            title : 'T_EMC_MTL_CATEGORY.MANAGEMENTTYPE',
            validators : [ 'notEmpty' ],
            type : 'select',
            // firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'managementType',
            },
            preInsert : true
        } ];
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
