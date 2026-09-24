var emcBizCategory = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/biz-categories',

    getBizClassEditListColumns : function () {
        return [ {
            checkbox : true
        }, {
            field : 'sortNo',
            title : 'T_EMC_BIZ_CLASS.SORTNO',
            editor : true,
            hasArrow : true,
            isInteger : true,
        }, {
            field : 'bizCode',
            title : 'T_EMC_BIZ_CLASS.BIZCODE'
        }, {
            field : 'bizName',
            title : 'T_EMC_BIZ_CLASS.BIZNAME',
            editor : true,
            width : '300',
        } ];
    },

    getBizClassFormFields : function() {
        return [ {
            field : 'sortNo',
            title : 'T_EMC_BIZ_CLASS.SORTNO'
        }, {
            field : 'bizCode',
            title : 'T_EMC_BIZ_CLASS.BIZCODE',
            validators : [ 'strLength[0,12]' ],
            preInsert : true,
        }, {
            field : 'bizName',
            title : 'T_EMC_BIZ_CLASS.BIZNAME',
            validators : [ 'strLength[0,40]' ],
            preInsert : true,
        }, ];
    },

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_BIZ_CATEGORY.ORDERNO',
            type : 'number',
            hasArrow : true,
            isInteger : true,
            editor : true
        }, {
            field : 'bizCategoryId',
            title : 'T_EMC_BIZ_CATEGORY.BIZCATEGORYID'
        }, {
            field : 'bizCategoryName',
            title : 'T_EMC_BIZ_CATEGORY.BIZCATEGORYNAME',
            editor : true
        }, {
            field : 'reportName',
            title : 'T_EMC_BIZ_CATEGORY.REPORTNAME',
            editor : true
        }, {
            field : 'ncCode',
            title : '检测类型代码(NC)',
            editor : true
        }, {
            field : 'type',
            title : 'T_EMC_BIZ_CATEGORY.TYPE',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'bizType',
            },
            width : 110
        }, {
            field : 'sample',
            title : 'T_EMC_BIZ_CATEGORY.SAMPLE',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            width : 100
        },{
            field : 'cycleTaskNo',
            title : '是否生成周期任务流水号',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            width : 100
        }, {
            field : 'showSample',
            title : 'T_EMC_BIZ_CATEGORY.SHOWSAMPLE',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            width : 200
        }/*, {
            field : 'ext$.sampleorgname',
            title : 'T_EMC_BIZ_CATEGORY.SAMPLEORGNAME',
            type : 'choose',
            single : true,
            category : 'org-simple',
            onBeforeChoose : function(rows) {
                return {
                    orgCategory_SEQ : 'department',
                    sampleDepartment_SEQ : '1'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-biz-category-edit-list-grid').setData({
                        id : Gikam.getComp('emc-biz-category-edit-list-grid').getActivedRow().id,
                        sampleOrgId : rows[0].id,
                        ext$ : {
                            sampleorgname : rows[0].orgName,
                        }
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-biz-category-edit-list-grid').setData({
                    id : Gikam.getComp('emc-biz-category-edit-list-grid').getActivedRow().id,
                    sampleOrgId : '',
                    ext$ : {
                        sampleorgname : ''
                    }
                });
            },
            editor : true
        }, {
            field : 'ext$.reporteditorgname',
            title : 'T_EMC_BIZ_CATEGORY.REPORTEDITORGNAME',
            type : 'choose',
            single : true,
            category : 'org-simple',
            onBeforeChoose : function(rows) {
                return {
                    orgCategory_SEQ : 'department'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-biz-category-edit-list-grid').setData({
                        id : Gikam.getComp('emc-biz-category-edit-list-grid').getActivedRow().id,
                        reportEditOrgId : rows[0].id,
                        ext$ : {
                            reporteditorgname : rows[0].orgName,
                        }
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-biz-category-edit-list-grid').setData({
                    id : Gikam.getComp('emc-biz-category-edit-list-grid').getActivedRow().id,
                    reportEditOrgId : '',
                    ext$ : {
                        reporteditorgname : ''
                    }
                });
            },
            editor : true
        }, {
            field : 'ext$.reportsendorgname',
            title : 'T_EMC_BIZ_CATEGORY.REPORTSENDORGNAME',
            type : 'choose',
            single : true,
            category : 'org-simple',
            onBeforeChoose : function(rows) {
                return {
                    orgCategory_SEQ : 'department'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-biz-category-edit-list-grid').setData({
                        id : Gikam.getComp('emc-biz-category-edit-list-grid').getActivedRow().id,
                        reportSendOrgId : rows[0].id,
                        ext$ : {
                            reportsendorgname : rows[0].orgName,
                        }
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-biz-category-edit-list-grid').setData({
                    id : Gikam.getComp('emc-biz-category-edit-list-grid').getActivedRow().id,
                    reportSendOrgId : '',
                    ext$ : {
                        reportsendorgname : ''
                    }
                });
            },
            editor : true
        }, {
            field : 'entrustOrg',
            title : 'T_EMC_BIZ_CATEGORY.ENTRUSTORG',
            type : 'choose',
            single : true,
            category : 'client',
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-biz-category-edit-list-grid').setData({
                        id : Gikam.getComp('emc-biz-category-edit-list-grid').getActivedRow().id,
                        entrustOrg : rows[0].clientName,
                        entrustOrgId : rows[0].id,
                        entrustOrgNo : rows[0].clientNo
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-biz-category-edit-list-grid').setData({
                    id : Gikam.getComp('emc-biz-category-edit-list-grid').getActivedRow().id,
                    entrustOrg : '',
                    entrustOrgId : '',
                    entrustOrgNo : ''
                });
            },
            editor : true
        }, {
            field : 'testOrg',
            title : 'T_EMC_BIZ_CATEGORY.TESTORG',
            type : 'choose',
            single : true,
            category : 'client',
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-biz-category-edit-list-grid').setData({
                        id : Gikam.getComp('emc-biz-category-edit-list-grid').getActivedRow().id,
                        testOrg : rows[0].clientName,
                        testOrgId : rows[0].id,
                        testOrgNo : rows[0].clientNo
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-biz-category-edit-list-grid').setData({
                    id : Gikam.getComp('emc-biz-category-edit-list-grid').getActivedRow().id,
                    testOrg : '',
                    testOrgId : '',
                    testOrgNo : ''
                });
            },
            editor : true
        }*/, {
            field : 'ndCal',
            title : 'T_EMC_BIZ_CATEGORY.NDCAL',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'nDComputingMethod',
            },
            editor : true,
            width : 105
        }/*, {
            field : 'projName',
            title : 'T_EMC_BIZ_CATEGORY.PROJNAME',
            editor : true
        }*/, {
            field : 'encryption',
            title : 'T_EMC_BIZ_CATEGORY.ENCRYPTION',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true,
            width : 85
        }, {
            field : 'editMonitorInfo',
            title : 'T_EMC_BIZ_CATEGORY.EDITMONITORINFO',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true,
            width : 125
        }/*, {
            field : 'reportWord',
            title : 'T_EMC_BIZ_CATEGORY.REPORTWORD',
            editor : true
        }*/, {
            field : 'formStyle',
            title : 'T_EMC_BIZ_CATEGORY.FORMSTYLE',
            type : 'choose',
            single : true,
            category : 'formStyle',
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-biz-category-edit-list-grid').setData({
                        id : Gikam.getComp('emc-biz-category-edit-list-grid').getActivedRow().id,
                        formStyle : rows[0].code
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-biz-category-edit-list-grid').setData({
                    id : Gikam.getComp('emc-biz-category-edit-list-grid').getActivedRow().id,
                    formStyle : ''
                });
            },
            editor : true
        }, {
            field : 'ext$.orgnames',
            title : 'T_EMC_BIZ_CATEGORY.ORGNAMES',
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="emcBizCategory.editPage.loadOrgPermission(\'{id}\')">{name}</a>', {
                    id : r.id,
                    name : Gikam.isEmpty(r.ext$.orgnames) ? Gikam.propI18N('T_EMC_BIZ_CATEGORY.ADD') : r.ext$.orgnames
                });
            },
            width : 200
        }, ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'bizCategoryId',
            preInsert : true,
            title : 'T_EMC_BIZ_CATEGORY.BIZCATEGORYID',
            validators : [ 'notEmpty', 'strLength[0,16]' ]
        }, {
            field : 'bizCategoryName',
            title : 'T_EMC_BIZ_CATEGORY.BIZCATEGORYNAME',
            preInsert : true,
            validators : [ 'strLength[0,21]' ]
        }, {
            field : 'reportName',
            title : 'T_EMC_BIZ_CATEGORY.REPORTNAME',
            preInsert : true,
            validators : [ 'strLength[0,21]' ]
        }, {
            field : 'type',
            title : 'T_EMC_BIZ_CATEGORY.TYPE',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'bizType',
            }
        }, {
            field : 'sample',
            title : 'T_EMC_BIZ_CATEGORY.SAMPLE',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'ext$.sampleorgname',
            title : 'T_EMC_BIZ_CATEGORY.SAMPLEORGNAME',
            category : 'org-simple',
            type : 'choose',
            single : true,
            onBeforeChoose : function(rows) {
                return {
                    orgCategory_SEQ : 'department'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-biz-category-detail-base-info-form').setData({
                        sampleOrgId : rows[0].id,
                        ext$ : {
                            sampleorgname : rows[0].orgName,
                        }
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-biz-category-detail-base-info-form').setData({
                    sampleOrgId : '',
                    ext$ : {
                        sampleorgname : ''
                    }
                });
            },
        }, {
            field : 'ext$.reporteditorgname',
            title : 'T_EMC_BIZ_CATEGORY.REPORTEDITORGNAME',
            category : 'org-simple',
            type : 'choose',
            single : true,
            onBeforeChoose : function(rows) {
                return {
                    orgCategory_SEQ : 'department'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-biz-category-detail-base-info-form').setData({
                        reportEditOrgId : rows[0].id,
                        ext$ : {
                            reporteditorgname : rows[0].orgName,
                        }
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-biz-category-detail-base-info-form').setData({
                    reportEditOrgId : '',
                    ext$ : {
                        reporteditorgname : ''
                    }
                });
            },
        }, {
            field : 'ext$.reportsendorgname',
            title : 'T_EMC_BIZ_CATEGORY.REPORTSENDORGNAME',
            category : 'org-simple',
            type : 'choose',
            single : true,
            onBeforeChoose : function(rows) {
                return {
                    orgCategory_SEQ : 'department'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-biz-category-detail-base-info-form').setData({
                        reportSendOrgId : rows[0].id,
                        ext$ : {
                            reportsendorgname : rows[0].orgName,
                        }
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-biz-category-detail-base-info-form').setData({
                    reportSendOrgId : '',
                    ext$ : {
                        reportsendorgname : ''
                    }
                });
            },
        }, {
            field : 'entrustOrg',
            title : 'T_EMC_BIZ_CATEGORY.ENTRUSTORG',
            category : 'client',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-biz-category-detail-base-info-form').setData({
                        entrustOrg : rows[0].clientName
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-biz-category-detail-base-info-form').setData({
                    entrustOrg : ''
                });
            }
        }, {
            field : 'testOrg',
            title : 'T_EMC_BIZ_CATEGORY.TESTORG',
            category : 'client',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-biz-category-detail-base-info-form').setData({
                        testOrg : rows[0].clientName
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-biz-category-detail-base-info-form').setData({
                    testOrg : ''
                });
            },
        }, {
            field : 'ndCal',
            title : 'T_EMC_BIZ_CATEGORY.NDCAL',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'nDComputingMethod',
            }
        }, {
            field : 'projName',
            title : 'T_EMC_BIZ_CATEGORY.PROJNAME',
            validators : [ 'strLength[0,21]' ]
        }, {
            field : 'encryption',
            title : 'T_EMC_BIZ_CATEGORY.ENCRYPTION',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'editMonitorInfo',
            title : 'T_EMC_BIZ_CATEGORY.EDITMONITORINFO',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            validators : [ 'strLength[0,10]' ]
        }, {
            field : 'reportWord',
            title : 'T_EMC_BIZ_CATEGORY.REPORTWORD',
            validators : [ 'strLength[0,21]' ]
        }, {
            field : 'formStyle',
            title : 'T_EMC_BIZ_CATEGORY.FORMSTYLE',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'formStyle',
            }
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
        }, {
             field : 'ext$.comp',
             title : '公司',
         } ];
    },

    getFormStyleListColumns : function() {
        return [ {
            field : 'code',
            title : 'T_EMC_ORG_PERMISSION.CODE',
            width : 90
        }, {
            field : 'codeName',
            title : 'T_EMC_ORG_PERMISSION.CODENAME',
            width : 90
        } ];
    },

    getChooseableColumns : function() {
        return [ {
            field : 'bizCategoryId',
            title : 'T_EMC_BIZ_CATEGORY.BIZCATEGORYID'
        }, {
            field : 'bizCategoryName',
            title : 'T_EMC_BIZ_CATEGORY.BIZCATEGORYNAME',
        }, {
            field : 'reportName',
            title : 'T_EMC_BIZ_CATEGORY.REPORTNAME',
        }, {
            field : 'type',
            title : 'T_EMC_BIZ_CATEGORY.TYPE',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'bizType',
            },
            width : 110
        }, {
            field : 'sample',
            title : 'T_EMC_BIZ_CATEGORY.SAMPLE',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            width : 100
        } ]
    },

    getProjectEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'projName',
            title : 'T_EMC_BIZ_CATEGORY_PROJECT.PROJNAME',
            editor : true,
            width : 300
        }, {
            field : 'activatedFlag',
            title : 'T_EMC_BIZ_CATEGORY_PROJECT.ACTIVATEDFLAG',
            type : 'select',
            editor : true,
            category : 'sys',
            param : {
                codeCategoryId : 'activatedFlag',
            },
        } ];
    },

    getProjectFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_EMC_BIZ_CATEGORY_PROJECT.ID'
        }, {
            field : 'bizCategoryId',
            title : 'T_EMC_BIZ_CATEGORY_PROJECT.BIZCATEGORYID'
        }, {
            field : 'projName',
            title : 'T_EMC_BIZ_CATEGORY_PROJECT.PROJNAME',
            preInsert : true
        }, {
            field : 'activatedFlag',
            title : 'T_EMC_BIZ_CATEGORY_PROJECT.ACTIVATEDFLAG'
        }, ];
    },

    getChooseableProjectColumns : function() {
        return [ {
            field : 'projName',
            title : 'T_EMC_BIZ_CATEGORY_PROJECT.PROJNAME',
            width : 300
        }, {
            field : 'activatedFlag',
            title : 'T_EMC_BIZ_CATEGORY_PROJECT.ACTIVATEDFLAG',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'activatedFlag',
            },
        } ];
    },
});
