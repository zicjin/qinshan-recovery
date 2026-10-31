var emcQcCategory = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/qc-categories',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'qcCategoryId',
            title : 'T_EMC_QC_CATEGORY.QCCATEGORYID',
            editor : true,
            width : 120,
        }, {
            field : 'qcName',
            title : 'T_EMC_QC_CATEGORY.QCNAME',
            width : 130,
            editor : true,
        }, {
            field : 'parentId',
            title : 'T_EMC_QC_CATEGORY.PARENTID',
            type : 'choose',
            category : 'qcCategory',
            editor : true,
            single : true,
            width : 140,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        parentId : rows[0].qcCategoryId
                    });

                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    parentId : ''
                });
            },
        }, {
            field : 'qcShortHand',
            title : 'T_EMC_QC_CATEGORY.QCSHORTHAND',
            editor : true,
            width : 80,
        }, {
            field : 'qcGroup',
            title : 'T_EMC_QC_CATEGORY.QCGROUP',
            type : 'select',
            category : 'standardSetCategory',
            param : {
                standardSet : 'QCSCHEMAS'
            },
            width : 100,
            editor : true,
        }, {
            field : 'updateParNo',
            title : 'T_EMC_QC_CATEGORY.UPDATEPARNO',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            width : 80,
            editor : true,
        }, {
            field : 'useCategory',
            title : 'T_EMC_QC_CATEGORY.USECATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'qualityUseType'
            },
            width : 100,
            editor : true,
        }, {
            field : 'secrecy',
            title : 'T_EMC_QC_CATEGORY.SECRECY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            width : 100,
            editor : true,
        }, {
            field : 'parOrder',
            title : 'T_EMC_QC_CATEGORY.PARORDER',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            width : 100,
            editor : true,
        }, {
            field : 'addQc',
            title : 'T_EMC_QC_CATEGORY.ADDQC',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            width : 100,
            editor : true,
        }, {
            field : 'addFolder',
            title : 'T_EMC_QC_CATEGORY.ADDFOLDER',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            width : 100,
            editor : true,
        }, {
            field : 'addNum',
            title : 'T_EMC_QC_CATEGORY.ADDNUM',
            width : 110,
            type : 'number',
            editor : true,
            isInteger : true
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'qcCategoryId',
            title : 'T_EMC_QC_CATEGORY.QCCATEGORYID',
            preInsert : true
        }, {
            field : 'qcName',
            title : 'T_EMC_QC_CATEGORY.QCNAME',
            preInsert : true
        } ];

    },

    getChooseListColumns : function() {
        return [ {
            field : 'qcCategoryId',
            title : 'T_EMC_QC_CATEGORY.QCCATEGORYID',
        }, {
            field : 'qcName',
            title : 'T_EMC_QC_CATEGORY.QCNAME',
        }, {
            field : 'parentId',
            title : 'T_EMC_QC_CATEGORY.PARENTID'
        }, {
            field : 'qcShortHand',
            title : 'T_EMC_QC_CATEGORY.QCSHORTHAND',
        }, {
            field : 'qcGroup',
            title : 'T_EMC_QC_CATEGORY.QCGROUP',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'qcGroup'
            }
        }, {
            field : 'updateParNo',
            title : 'T_EMC_QC_CATEGORY.UPDATEPARNO',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            field : 'useCategory',
            title : 'T_EMC_QC_CATEGORY.USECATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'qualityUseType'
            }
        }, {
            field : 'secrecy',
            title : 'T_EMC_QC_CATEGORY.SECRECY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            field : 'parorder',
            title : 'T_EMC_QC_CATEGORY.PARORDER',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            field : 'addQc',
            title : 'T_EMC_QC_CATEGORY.ADDQC',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        } ];
    },

    getSimpleChooseListColumns : function() {
        return [ {
            field : 'qcCategoryId',
            title : 'T_EMC_QC_CATEGORY.QCCATEGORYID',
        }, {
            field : 'qcName',
            title : 'T_EMC_QC_CATEGORY.QCNAME',
        } ];
    }
});
