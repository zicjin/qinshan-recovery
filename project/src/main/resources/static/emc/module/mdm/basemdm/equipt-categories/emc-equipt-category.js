var emcEquiptCategory = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/equipt-categories',

    getEquiptCategoryColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'equiptCategory',
            title : 'T_EMC_EQUIPT_CATEGORY.EQUIPTCATEGORY',
            validators : [ 'strLength[0,21]' ],
            preInsert : true,
        }, {
            field : 'equiptCategoryDesc',
            title : 'T_EMC_EQUIPT_CATEGORY.EQUIPTCATEGORYDESC',
            validators : [ 'strLength[0,85]' ],
            editor : true
        }, {
            field : 'maint',
            title : 'T_EMC_EQUIPT_CATEGORY.MAINT',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            editor : true
        } ];
    },

    getEquiptMaintColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'maintEvent',
            title : 'T_EMC_EQUIPT_MAINT.MAINTEVENT',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'maintainEvent'
            }
        }, {
            field : 'eventDesc',
            title : 'T_EMC_EQUIPT_MAINT.EVENTDESC',
            validators : [ 'strLength[0,85]' ],
            editor : true,
        }, {
            field : 'warningDays',
            title : 'T_EMC_EQUIPT_MAINT.WARNINGDAYS',
            validators : [ 'number' ],
            editor : true,
            width : 110,
        }, {
            field : 'sameDay',
            title : 'T_EMC_EQUIPT_MAINT.SAMEDAY',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            width : 90,
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
