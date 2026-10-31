var emcTestCategory = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/test-categories',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'testCategoryDesc',
            title : 'T_EMC_TEST_CATEGORY.TESTCATEGORYDESC',
            validators : [ 'strLength[0,21]' ],
            editor : true,
        }, {
            field : 'ext$.orgname',
            title : 'T_EMC_TEST_CATEGORY.DEPARTMENTORGNAME',
            editor : true,
            type : 'choose',
            category : 'org-simple',
            single : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        departmentOrgId : rows[0].id,
                        ext$ : {
                            orgname : rows[0].orgName,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    departmentOrgId : '',
                    ext$ : {
                        orgname : ''
                    }
                });
            },
            onBeforeChoose : function() {
                return {
                    orgCategory_SEQ : 'department'
                }
            },
            onClean:function(field,value,index){
                this.setData({
                    index : index,
                    departmentOrgId : '',
                    ext$ : {
                        orgname : '',
                    }
                })
            }
        }, ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'testCategoryDesc',
            title : 'T_EMC_TEST_CATEGORY.TESTCATEGORYDESC',
            preInsert : true,
            validators : [ 'strLength[0,21]' ],
        }, {
            field : 'ext$.orgname',
            title : 'T_EMC_TEST_CATEGORY.DEPARTMENTORGNAME',
            type : 'choose',
            category : 'org-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        departmentOrgId : rows[0].id,
                        ext$ : {
                            orgname : rows[0].orgName,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    departmentOrgId : '',
                    ext$ : {
                        orgname : ''
                    }
                });
            },
            onBeforeChoose : function() {
                return {
                    orgCategory_SEQ : 'department'
                }
            },
        }, ];
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
    
    getChooseListColumns : function() {
        return [ {
            field : 'testCategoryDesc',
            title : 'T_EMC_TEST_CATEGORY.TESTCATEGORYDESC',
            type : 'link'
        }, {
            field : 'ext$.orgname',
            title : 'T_EMC_TEST_CATEGORY.DEPARTMENTORGNAME',
        }, ];
    },
});
