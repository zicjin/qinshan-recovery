var coreFileType = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'sortor',
            title : 'T_CORE_FILE_TYPE.SORTOR',
            width : 55,
            editor : true
        }, {
            field : 'fileTypeNo',
            title : 'T_CORE_FILE_TYPE.FILETYPENO',
            width : 110
        }, {
            field : 'fileTypeName',
            title : 'T_CORE_FILE_TYPE.FILETYPENAME',
            width : 95
        }, {
            field : 'activatedFlag',
            title : 'T_CORE_FILE_TYPE.ACTIVATEDFLAG',
            width : 85,
            type : 'select',
            category : 'activatedFlag',
            editor : true,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('core-file-type-edit-list-grid');
                var urlSuffix = value == '1' ? '/action/activate' : '/action/deactivate';
                workspace.window.showMask(true);
                Gikam.put(IFM_CONTEXT + '/secure/core/module/qms/file-types' + urlSuffix, Gikam.getJsonWrapper({}, [ '', [grid.getActivedRow()] ])).done(function(item) {
                    
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        }, {
            field : 'createdByName',
            title : 'T_CORE_FILE_TYPE.CREATEDBYNAME',
            width : 80
        }, {
            field : 'createdTime',
            title : 'T_CORE_FILE_TYPE.CREATEDTIME',
            width : 80
        } ];
    },

    getInsertFileTypeFields : function() {
        return [ {
            field : 'fileTypeNo',
            title : 'T_CORE_FILE_TYPE.FILETYPENO',
            validators : [ 'notEmpty', 'strLength[0,20]' ],
            preInsert : true
        }, {
            field : 'fileTypeName',
            title : 'T_CORE_FILE_TYPE.FILETYPENAME',
            validators : [ 'notEmpty', 'strLength[0,90]' ],
            preInsert : true
        } ];
    },

    getCategoryListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'sortor',
            title : 'T_CORE_FILE_CATEGORY.SORTOR',
            editor : true,
            width : 55
        }, {
            field : 'fileCategoryNo',
            title : 'T_CORE_FILE_CATEGORY.FILECATEGORYNO',
            width : 110
        }, {
            field : 'fileCategoryName',
            title : 'T_CORE_FILE_CATEGORY.FILECATEGORYNAME',
            width : 95
        }, {
            field : 'activatedFlag',
            title : 'T_CORE_FILE_CATEGORY.ACTIVATEDFLAG',
            editor : true,
            type : 'select',
            category : 'activatedFlag',
            width : 85,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('core-file-type-edit-list-category-grid');
                var urlSuffix = value == '1' ? '/action/activate' : '/action/deactivate';
                workspace.window.showMask(true);
                Gikam.put(IFM_CONTEXT + '/secure/core/module/qms/file-types/file-categorys' + urlSuffix, Gikam.getJsonWrapper({}, [ '', [grid.getActivedRow()] ])).done(function(item) {
                    
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        }, {
            field : 'fileDraftTemplateId',
            title : 'T_CORE_FILE_CATEGORY.FILEDRAFTTEMPLATEID',
            width : 110
        }, {
            field : 'createdByName',
            title : 'T_CORE_FILE_CATEGORY.CREATEDBYNAME',
            width : 80
        }, {
            field : 'createdTime',
            title : 'T_CORE_FILE_CATEGORY.CREATEDTIME',
            width : 80
        } ];
    },

    getInsertFileCategoryFields : function() {
        return [ {
            field : 'fileCategoryNo',
            title : 'T_CORE_FILE_CATEGORY.FILECATEGORYNO',
            validators : [ 'notEmpty', 'strLength[0,20]' ]
        }, {
            field : 'fileCategoryName',
            title : 'T_CORE_FILE_CATEGORY.FILECATEGORYNAME',
            validators : [ 'notEmpty', 'strLength[0,90]' ]
        } ];
    },
    
    getCategoryRoleAuthListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'sortor',
            title : 'T_CORE_FILE_CATEGORY_ROLE_AUTH.SORTOR',
            editor : true,
            width : 55
        }, {
            field : 'roleId',
            title : 'T_CORE_FILE_CATEGORY_ROLE_AUTH.ROLEID',
            width : 150
        }, {
            field : 'roleName',
            title : 'T_CORE_FILE_CATEGORY_ROLE_AUTH.ROLENAME',
            width : 180
        }, {
            field : 'readFlag',
            title : 'T_CORE_FILE_CATEGORY_ROLE_AUTH.READFLAG',
            type : 'simpleCheckbox',
            editor : true
        }, {
            field : 'printFlag',
            title : 'T_CORE_FILE_CATEGORY_ROLE_AUTH.PRINTFLAG',
            type : 'simpleCheckbox',
            editor : true
        } ];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'sortor',
            title : 'T_CORE_FILE_TYPE.SORTOR',
            width : 55
        }, {
            field : 'fileTypeNo',
            title : 'T_CORE_FILE_TYPE.FILETYPENO',
            width : 110
        }, {
            field : 'fileTypeName',
            title : 'T_CORE_FILE_TYPE.FILETYPENAME',
            width : 95
        }, {
            field : 'activatedFlag',
            title : 'T_CORE_FILE_TYPE.ACTIVATEDFLAG',
            width : 85,
            type : 'select',
            category : 'activatedFlag'
        }, {
            field : 'createdByName',
            title : 'T_CORE_FILE_TYPE.CREATEDBYNAME',
            width : 80
        }, {
            field : 'createdTime',
            title : 'T_CORE_FILE_TYPE.CREATEDTIME',
            width : 80
        } ];
    },
    
    getCategorySearchListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'sortor',
            title : 'T_CORE_FILE_CATEGORY.SORTOR',
            width : 55
        }, {
            field : 'fileCategoryNo',
            title : 'T_CORE_FILE_CATEGORY.FILECATEGORYNO',
            width : 110
        }, {
            field : 'fileCategoryName',
            title : 'T_CORE_FILE_CATEGORY.FILECATEGORYNAME',
            width : 95
        }, {
            field : 'activatedFlag',
            title : 'T_CORE_FILE_CATEGORY.ACTIVATEDFLAG',
            type : 'select',
            category : 'activatedFlag',
            width : 85
        }, {
            field : 'fileDraftTemplateId',
            title : 'T_CORE_FILE_CATEGORY.FILEDRAFTTEMPLATEID',
            width : 110
        }, {
            field : 'createdByName',
            title : 'T_CORE_FILE_CATEGORY.CREATEDBYNAME',
            width : 80
        }, {
            field : 'createdTime',
            title : 'T_CORE_FILE_CATEGORY.CREATEDTIME',
            width : 80
        } ];
    },

    getCategoryRoleAuthSearchListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'sortor',
            title : 'T_CORE_FILE_CATEGORY_ROLE_AUTH.SORTOR',
            width : 55
        }, {
            field : 'roleId',
            title : 'T_CORE_FILE_CATEGORY_ROLE_AUTH.ROLEID',
            width : 150
        }, {
            field : 'roleName',
            title : 'T_CORE_FILE_CATEGORY_ROLE_AUTH.ROLENAME',
            width : 180
        }, {
            field : 'readFlag',
            title : 'T_CORE_FILE_CATEGORY_ROLE_AUTH.READFLAG',
            type : 'simpleCheckbox'
        }, {
            field : 'printFlag',
            title : 'T_CORE_FILE_CATEGORY_ROLE_AUTH.PRINTFLAG',
            type : 'simpleCheckbox'
        } ];
    },
    
    getChooseListColumns : function(isSingle) {
        var columns = [ {
            field : 'id',
            title : 'T_CORE_FILE_TYPE.ID'
        }, {
            field : 'sortor',
            title : 'T_CORE_FILE_TYPE.SORTOR'
        }, {
            field : 'fileTypeNo',
            title : 'T_CORE_FILE_TYPE.FILETYPENO'
        }, {
            field : 'fileTypeName',
            title : 'T_CORE_FILE_TYPE.FILETYPENAME'
        }, {
            field : 'activatedFlag',
            title : 'T_CORE_FILE_TYPE.ACTIVATEDFLAG'
        }, {
            field : 'activatedById',
            title : 'T_CORE_FILE_TYPE.ACTIVATEDBYID'
        }, {
            field : 'activatedByName',
            title : 'T_CORE_FILE_TYPE.ACTIVATEDBYNAME'
        }, {
            field : 'activatedTime',
            title : 'T_CORE_FILE_TYPE.ACTIVATEDTIME'
        }, {
            field : 'remark',
            title : 'T_CORE_FILE_TYPE.REMARK'
        } ];
        if (isSingle == 1) {
            columns.splice(0, 1, {
                radio : true
            })
        }
        return columns;
    },
    
    baseUrl : IFM_CONTEXT + '/secure/core/module/qms/file-types'
});
