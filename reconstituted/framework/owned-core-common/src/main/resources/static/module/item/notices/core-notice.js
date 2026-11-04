var coreNotice = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/item/notices',

    getEditListColumns : function() {
        return [ {
            checkbox : true,
        }, Gikam.status.getBpmnColumn(coreNotice.workflow.dbTable), {
            field : 'title',
            title : 'T_CORE_NOTICE.TITLE',
        }, {
            field : 'infoCategory',
            title : 'T_CORE_NOTICE.INFOCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'infocategory'
            },
        } ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true,
        }, Gikam.status.getBpmnColumn(coreNotice.workflow.dbTable), {
            field : 'title',
            title : 'T_CORE_NOTICE.TITLE',
        }, {
            field : 'infoCategory',
            title : 'T_CORE_NOTICE.INFOCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'infocategory'
            },
        } ];
    },

    getCancelListColumns : function() {
        return [ {
            checkbox : true,
        }, {
            field : 'title',
            title : 'T_CORE_NOTICE.TITLE',
        }, {
            field : 'infoCategory',
            title : 'T_CORE_NOTICE.INFOCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'infocategory'
            },
        } ];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true,
        }, Gikam.status.getBpmnColumn(coreNotice.workflow.dbTable), {
            field : 'title',
            title : 'T_CORE_NOTICE.TITLE',
        }, {
            field : 'infoCategory',
            title : 'T_CORE_NOTICE.INFOCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'infocategory'
            },
        }, {
            field : 'status',
            title : 'T_CORE_NOTICE.STATUS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'noticestatus'
            },
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'title',
            title : 'T_CORE_NOTICE.TITLE',
            colspan : 3,
            preInsert : true,
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'publishOrgName',
            title : 'T_CORE_NOTICE.PUBLISHORGNAME',
            type : 'choose',
            single : true,
            category : 'org-simple',
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        publishOrgId : rows[0].id,
                        publishOrgName : rows[0].orgName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    publishOrgId : '',
                    publishOrgName : '',
                });
            },
        }, {
            field : 'importance',
            title : 'T_CORE_NOTICE.IMPORTANCE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'importance'
            },
        }, {
            field : 'noticeCategory',
            title : 'T_CORE_NOTICE.NOTICECATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'noticecategory'
            },
            onChange : function(field, value, index) {
                if (value) {
                    if (value == 'whole') {
                        Gikam.getComp('core-notice-edit-list-tab').removePanel('core-notice-edit-list-user-panel');
                        Gikam.getComp('core-notice-edit-list-tab').removePanel('core-notice-edit-list-role-panel');
                        Gikam.getComp('core-notice-edit-list-tab').removePanel('core-notice-edit-list-org-panel');
                    } else {
                        Gikam.getComp('core-notice-edit-list-tab').revertPanel('core-notice-edit-list-user-panel');
                        Gikam.getComp('core-notice-edit-list-tab').revertPanel('core-notice-edit-list-role-panel');
                        Gikam.getComp('core-notice-edit-list-tab').revertPanel('core-notice-edit-list-org-panel');
                    }
                }
            }
        }, {
            field : 'infoCategory',
            title : 'T_CORE_NOTICE.INFOCATEGORY',
            type : 'select',
            category : 'sys',
            preInsert : true,
            param : {
                codeCategoryId : 'infocategory'
            },
        }, {
            field : 'validDate',
            title : 'T_CORE_NOTICE.VALIDDATE',
            type : 'date'
        }, {
            field : 'indexDisplay',
            title : 'T_CORE_NOTICE.INDEXDISPLAY',
            type : 'select',
            category : 'notice-index-display-method'
        }, {
            field : 'content',
            title : 'T_CORE_NOTICE.CONTENT',
            type : 'ckeditor',
            height : 600,
            colspan : 3
        }  ];
    },

    getUserColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'targetId',
            title : 'T_CORE_USER.ID',
        }, {
            field : 'targetName',
            title : 'T_CORE_USER.USERNAME',
            width : 300
        } ];
    },

    getRoleColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'targetName',
            title : 'T_CORE_ROLE_USER.ROLENAME',
            width : 300
        } ];
    },

    getOrgColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'targetId',
            title : 'T_CORE_ORG.ID',
        }, {
            field : 'targetName',
            title : 'T_CORE_ORG.ORGNAME',
            width : 300
        } ];
    },

    workflow : {
        dbTable : 'T_CORE_NOTICE',
        columns : [ {
            field : 'id',
            title : 'T_CORE_NOTICE.ID'
        } ]
    }
});
