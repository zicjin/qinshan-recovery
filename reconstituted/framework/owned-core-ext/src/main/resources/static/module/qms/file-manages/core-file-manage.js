var coreFileManage = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'sortor',
            title : 'T_CORE_FILE_MANAGE.SORTOR',
            editor : true
        }, {
            field : 'fileNo',
            title : 'T_CORE_FILE_MANAGE.FILENO'
        }, {
            field : 'fileTypeName',
            title : 'T_CORE_FILE_MANAGE.FILETYPENAME'
        }, {
            field : 'fileCategoryName',
            title : 'T_CORE_FILE_MANAGE.FILECATEGORYNAME'
        }, {
            field : 'fileName',
            title : 'T_CORE_FILE_MANAGE.FILENAME'
        }, {
            field : 'auditNo',
            title : 'T_CORE_FILE_MANAGE.AUDITNO',
            editor : true
        }, {
            field : 'fileVolumeName',
            title : 'T_CORE_FILE_MANAGE.FILEVOLUMENAME'
        }, {
            field : 'createdByName',
            title : 'T_CORE_FILE_MANAGE.CREATEDBYNAME',
            width : 80
        }, {
            field : 'createdTime',
            title : 'T_CORE_FILE_MANAGE.CREATEDTIME',
            width : 80
        }, {
            field : 'remark',
            title : 'T_CORE_FILE_MANAGE.REMARK'
        } ];
    },

    getInsertFileManageFormFields : function() {
        return [ {
            field : 'fileTypeId',
            title : 'T_CORE_FILE_MANAGE.FILETYPENAME',
            validators : [ 'notEmpty', 'strLength[0,90]' ],
            preInsert :true,
            type : 'select',
            category : 'file-type',
            firstBlank : false,
            param : {
                activatedFlag : 1
            },
            onChange : function(field, value, rowIndex, oldValue, text) {
                var insertForm = Gikam.getComp('core-file-manage-insert-form');
                Gikam.post(Gikam.printf(IFM_CONTEXT + '/secure/core/module/qms/file-types/{id}/categorys/queries', {
                    id : value
                }), Gikam.getJsonWrapper({
                    f : {
                        activatedFlag_SEQ : 1
                    }
                })).done(function(data) {
                    data = data.rows;
                    var selectData = data.map(function(item) {
                        var selectItem = {};
                        selectItem.text = item.fileCategoryName;
                        selectItem.value = item.id;
                        return selectItem;
                    });
                    Gikam.getComp('core-file-manage-insert-form').setSelectOptions('fileCategoryId', selectData);
                    insertForm.setData({
                        fileTypeName : text
                    }, false);
                });
            }
        }, {
            field : 'fileCategoryId',
            title : 'T_CORE_FILE_MANAGE.FILECATEGORYNAME',
            validators : [ 'notEmpty', 'strLength[0,90]' ],
            preInsert :true,
            type : 'select',
            onChange : function(field, value, rowIndex, oldValue, text) {
                var insertForm = Gikam.getComp('core-file-manage-insert-form');
                insertForm.setData({
                    fileCategoryName : text
                }, false);
                workspace.window.showMask(true);
                Gikam.getText(Gikam.printf(coreFileManage.baseUrl + '/{id}/fileno', {
                    id : value
                })).done(function(item) {
                    insertForm.setData({
                        fileNo : item
                    });
                }).always(function() {
                    workspace.window.closeMask();
                })
            }
        }, {
            field : 'fileVolumeName',
            title : 'T_CORE_FILE_MANAGE.FILEVOLUMENAME',
            validators : [ 'strLength[0,90]' ],
            preInsert :true,
            type : 'choose',
            single : true,
            category : 'file-volume',
            targetFields : [{ fileVolumeId : 'id' }, { fileVolumeName : 'fileVolumeName' }]
        }, {
            field : 'fileNo',
            title : 'T_CORE_FILE_MANAGE.FILENO',
            validators : [ 'unique[/secure/core/module/qms/file-manages/action/validate-unique]', 'notEmpty', 'strLength[0,20]' ],
            preInsert :true,
            
        }, {
            field : 'fileName',
            title : 'T_CORE_FILE_MANAGE.FILENAME',
            validators : [ 'unique[/secure/core/module/qms/file-manages/action/validate-unique]', 'notEmpty', 'strLength[0,90]' ],
            preInsert :true,
        } ];
    },

    getVersionEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'versionState',
            title : 'T_CORE_FILE_MANAGE_VERSION.VERSIONSTATE',
            type : 'select',
            category : 'file-activated-flag'
        }, {
            field : 'versionNo',
            title : 'T_CORE_FILE_MANAGE_VERSION.VERSIONNO',
            type : 'link'
        }, {
            field : 'refFileName',
            title : 'T_CORE_FILE_MANAGE_VERSION.REFFILENAME'
        }, {
            field : 'auditState',
            title : 'T_CORE_FILE_MANAGE_VERSION.AUDITSTATE',
            editor : true,
            type : 'select',
            items : [ {
                value : 1,
                text : Gikam.propI18N('CORE.MODULE.QMS.FILEMANAGE.TITLE.AUDITSTATE_YES'),
            }, {
                value : 0,
                text : Gikam.propI18N('CORE.MODULE.QMS.FILEMANAGE.TITLE.AUDITSTATE_NO'),
            } ]
        }, {
            field : 'draftDate',
            title : 'T_CORE_FILE_MANAGE_VERSION.DRAFTDATE',
            type : 'date',
            editor : true
        }, {
            field : 'effectiveDate',
            title : 'T_CORE_FILE_MANAGE_VERSION.EFFECTIVEDATE',
            type : 'date',
            editor : true
        }, {
            field : 'expiryDate',
            title : 'T_CORE_FILE_MANAGE_VERSION.EXPIRYDATE',
            type : 'date',
            editor : true
        }, {
            field : 'editUserName',
            title : 'T_CORE_FILE_MANAGE_VERSION.EDITUSERNAME',
            editor : true,
            type : 'choose',
            category : 'user',
            single : true,
            targetFields : [{ editUserId : 'id' }, { editUserName : 'userName' }]
        }, {
            field : 'checkUserName',
            title : 'T_CORE_FILE_MANAGE_VERSION.CHECKUSERNAME',
            editor : true,
            type : 'choose',
            category : 'user',
            single : true,
            targetFields : [{ checkUserId : 'id' }, { checkUserName : 'userName' }]
        }, {
            field : 'approveUserName',
            title : 'T_CORE_FILE_MANAGE_VERSION.APPROVEUSERNAME',
            editor : true,
            type : 'choose',
            category : 'user',
            single : true,
            targetFields : [{ approveUserId : 'id' }, { approveUserName : 'userName' }]
        }, {
            field : 'receiveOrgName',
            title : 'T_CORE_FILE_MANAGE_VERSION.RECEIVEORGNAME',
            editor : true,
            type : 'choose',
            category : 'org',
            single : true,
            targetFields : [{ receiveOrgId : 'id' }, { receiveOrgName : 'orgName' }]
        }, {
            field : 'archiveOrgName',
            title : 'T_CORE_FILE_MANAGE_VERSION.ARCHIVEORGNAME',
            editor : true,
            type : 'choose',
            category : 'org',
            single : true,
            targetFields : [{ archiveOrgId : 'id' }, { archiveOrgName : 'orgName' }]
        }, {
            field : 'remark',
            title : 'T_CORE_FILE_MANAGE_VERSION.REMARK',
            editor : true
        }, {
            field : '',
            title : 'CORE.MODULE.QMS.FILEMANAGES.TITLE.ACTION',
            width : 100,
            formatter : function(index, value, row, text) {
                return '&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:;" onclick="coreFileManage.readFile(\'' + row.id + ',' + row.refFileId + ',' + row.fileManageId + '\')">' + Gikam.propI18N('T_CORE_FILE_MANAGE_AUTH.READFLAG') + '</a>'
                    + '&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:;" onclick="coreFileManage.printFile(\'' + row.id + ',' + row.refFileId + ',' + row.fileManageId + '\')">' + Gikam.propI18N('T_CORE_FILE_MANAGE_AUTH.PRINTFLAG') + '</a>';
            }
        } ];
    },

    getVersionAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreFileManage.workflow.dbTable), {
            field : 'versionNo',
            title : 'T_CORE_FILE_MANAGE_VERSION.VERSIONNO',
            type : 'link'
        }, {
            field : 'refFileName',
            title : 'T_CORE_FILE_MANAGE_VERSION.REFFILENAME'
        }, {
            field : 'auditState',
            title : 'T_CORE_FILE_MANAGE_VERSION.AUDITSTATE',
            type : 'select',
            items : [ {
                value : 1,
                text : Gikam.propI18N('CORE.MODULE.QMS.FILEMANAGE.TITLE.AUDITSTATE_YES'),
            }, {
                value : 0,
                text : Gikam.propI18N('CORE.MODULE.QMS.FILEMANAGE.TITLE.AUDITSTATE_NO'),
            } ]
        }, {
            field : 'draftDate',
            title : 'T_CORE_FILE_MANAGE_VERSION.DRAFTDATE',
            type : 'date'
        }, {
            field : 'effectiveDate',
            title : 'T_CORE_FILE_MANAGE_VERSION.EFFECTIVEDATE',
            type : 'date'
        }, {
            field : 'expiryDate',
            title : 'T_CORE_FILE_MANAGE_VERSION.EXPIRYDATE',
            type : 'date'
        }, {
            field : 'editUserName',
            title : 'T_CORE_FILE_MANAGE_VERSION.EDITUSERNAME'
        }, {
            field : 'checkUserName',
            title : 'T_CORE_FILE_MANAGE_VERSION.CHECKUSERNAME'
        }, {
            field : 'approveUserName',
            title : 'T_CORE_FILE_MANAGE_VERSION.APPROVEUSERNAME'
        }, {
            field : 'receiveOrgName',
            title : 'T_CORE_FILE_MANAGE_VERSION.RECEIVEORGNAME'
        }, {
            field : 'archiveOrgName',
            title : 'T_CORE_FILE_MANAGE_VERSION.ARCHIVEORGNAME'
        }, {
            field : 'remark',
            title : 'T_CORE_FILE_MANAGE_VERSION.REMARK'
        }, {
            field : 'remark',
            title : 'CORE.MODULE.QMS.FILEMANAGES.TITLE.ACTION',
            width : 100,
            formatter : function(index, value, row, text) {
                return '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:;" onclick="coreFileManage.readFile(\'' + row.id + ',' + row.refFileId + ',' + row.fileManageId + '\')">' + Gikam.propI18N('T_CORE_FILE_MANAGE_AUTH.READFLAG') + '</a>';
            }
        } ];
    },
    
    getVersionSearchListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'versionState',
            title : 'T_CORE_FILE_MANAGE_VERSION.VERSIONSTATE',
            type : 'select',
            category : 'file-activated-flag'
        }, {
            field : 'versionNo',
            title : 'T_CORE_FILE_MANAGE_VERSION.VERSIONNO',
            type : 'link'
        }, {
            field : 'refFileName',
            title : 'T_CORE_FILE_MANAGE_VERSION.REFFILENAME'
        }, {
            field : 'auditState',
            title : 'T_CORE_FILE_MANAGE_VERSION.AUDITSTATE',
            type : 'select',
            items : [ {
                value : 1,
                text : Gikam.propI18N('CORE.MODULE.QMS.FILEMANAGE.TITLE.AUDITSTATE_YES'),
            }, {
                value : 0,
                text : Gikam.propI18N('CORE.MODULE.QMS.FILEMANAGE.TITLE.AUDITSTATE_NO'),
            } ]
        }, {
            field : 'draftDate',
            title : 'T_CORE_FILE_MANAGE_VERSION.DRAFTDATE',
            type : 'date'
        }, {
            field : 'effectiveDate',
            title : 'T_CORE_FILE_MANAGE_VERSION.EFFECTIVEDATE',
            type : 'date'
        }, {
            field : 'expiryDate',
            title : 'T_CORE_FILE_MANAGE_VERSION.EXPIRYDATE',
            type : 'date'
        }, {
            field : 'editUserName',
            title : 'T_CORE_FILE_MANAGE_VERSION.EDITUSERNAME'
        }, {
            field : 'checkUserName',
            title : 'T_CORE_FILE_MANAGE_VERSION.CHECKUSERNAME'
        }, {
            field : 'approveUserName',
            title : 'T_CORE_FILE_MANAGE_VERSION.APPROVEUSERNAME'
        }, {
            field : 'receiveOrgName',
            title : 'T_CORE_FILE_MANAGE_VERSION.RECEIVEORGNAME'
        }, {
            field : 'archiveOrgName',
            title : 'T_CORE_FILE_MANAGE_VERSION.ARCHIVEORGNAME'
        }, {
            field : 'remark',
            title : 'T_CORE_FILE_MANAGE_VERSION.REMARK'
        }, {
            field : 'remark',
            title : 'CORE.MODULE.QMS.FILEMANAGES.TITLE.ACTION',
            width : 100,
            formatter : function(index, value, row, text) {
                return '&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:;" onclick="coreFileManage.readFile(\'' + row.id + ',' + row.refFileId + ',' + row.fileManageId + '\')">' + Gikam.propI18N('T_CORE_FILE_MANAGE_AUTH.READFLAG') + '</a>'
                    + '&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:;" onclick="coreFileManage.printFile(\'' + row.id + ',' + row.refFileId + ',' + row.fileManageId + '\')">' + Gikam.propI18N('T_CORE_FILE_MANAGE_AUTH.PRINTFLAG') + '</a>';
            }
        } ];
    },
    
    getFileVersionInfoFields : function() {
        return [ {
            field : 'ext$.filename',
            title : 'T_CORE_FILE_MANAGE.FILENAME',
            readonly : true
        }, {
            field : 'fileNo',
            title : 'T_CORE_FILE_MANAGE_VERSION.FILENO',
            readonly : true
        }, {
            field : 'versionNo',
            title : 'T_CORE_FILE_MANAGE_VERSION.VERSIONNO',
            readonly : true
        }, {
            field : 'versionState',
            title : 'T_CORE_FILE_MANAGE_VERSION.VERSIONSTATE',
            readonly : true,
            type : 'select',
            category : 'file-activated-flag'
        }, {
            field : 'createdByName',
            title : 'T_CORE_FILE_MANAGE_VERSION.CREATEDBYNAME',
            type : 'choose',
            category : 'user',
            single : true,
            targetFields : [{ createdById : 'id' }, { createdByName : 'userName' }],
            onBeforeChoose : function() {
                return {
                    orgId_SEQ : workspace.user.orgId
                }
            }
        }, {
            field : 'createdTime',
            title : 'T_CORE_FILE_MANAGE_VERSION.CREATEDTIME',
            type : 'date'
        }, {
            field : 'createdByOrgName',
            title : 'T_CORE_FILE_MANAGE_VERSION.CREATEDBYORGNAME',
            type : 'choose',
            category : 'org',
            single : true,
            targetFields : [{ createdByOrgId : 'id' }, { createdByOrgName : 'orgName' }],
            onBeforeChoose : function() {
                var parentId;
                Gikam.getText(Gikam.printf('/secure/core/module/mdm/orgs/{id}', {
                    id : workspace.user.orgId
                })).done(function(item) {
                    if (!Gikam.isEmpty(item.parentId)) {
                        parentId = item.parentId
                    }
                }).always(function() {

                });
                if (!Gikam.isEmpty(parentId)) {
                    return {
                        parentId_SEQ : parentId
                    }
                }
            }
        }, {
            field : 'auditNo',
            title : 'T_CORE_FILE_MANAGE_VERSION.AUDITNO'
        },  {
            field : 'checkUserName',
            title : 'T_CORE_FILE_MANAGE_VERSION.CHECKUSERNAME',
            type : 'choose',
            category : 'user',
            single : true,
            targetFields : [{ checkUserId : 'id' }, { checkUserName : 'userName' }],
            onBeforeChoose : function() {
                return {
                    orgId_SEQ : workspace.user.orgId
                }
            }
        }, {
            field : 'approveUserName',
            title : 'T_CORE_FILE_MANAGE_VERSION.APPROVEUSERNAME',
            type : 'choose',
            category : 'user',
            single : true,
            targetFields : [{ approveUserId : 'id' }, { approveUserName : 'userName' }],
            onBeforeChoose : function() {
                return {
                    orgId_SEQ : workspace.user.orgId
                }
            }
        }, {
            field : 'releaseDate',
            title : 'T_CORE_FILE_MANAGE_VERSION.RELEASEDATE',
            type : 'date'
        }, {
            field : 'carryDate',
            title : 'T_CORE_FILE_MANAGE_VERSION.CARRYDATE',
            type : 'date'
        } ];
    },
    
    getManageAuthListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'sortor',
            title : 'T_CORE_FILE_MANAGE_AUTH.SORTOR',
            editor : true
        }, {
            field : 'roleId',
            title : 'T_CORE_FILE_MANAGE_AUTH.ROLEID'
        }, {
            field : 'roleName',
            title : 'T_CORE_FILE_MANAGE_AUTH.ROLENAME'
        }, {
            field : 'printFlag',
            title : 'T_CORE_FILE_MANAGE_AUTH.PRINTFLAG',
            type : 'simpleCheckbox',
            editor : true
        }, {
            field : 'readFlag',
            title : 'T_CORE_FILE_MANAGE_AUTH.READFLAG',
            type : 'simpleCheckbox',
            editor : true
        }, {
            field : 'remark',
            title : 'T_CORE_FILE_MANAGE_AUTH.REMARK',
            editor : true
        } ];
    },

    getManageReadRecordListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'fileVersionNo',
            title : 'T_CORE_FILE_MANAGE_READ_RECORD.FILEVERSIONNO',
            editor : true
        }, {
            field : 'readUserId',
            title : 'T_CORE_FILE_MANAGE_READ_RECORD.READUSERID',
            editor : true
        }, {
            field : 'readUserName',
            title : 'T_CORE_FILE_MANAGE_READ_RECORD.READUSERNAME',
            editor : true
        }, {
            field : 'readOrgName',
            title : 'T_CORE_FILE_MANAGE_READ_RECORD.READORGNAME',
            editor : true
        }, {
            field : 'readTime',
            title : 'T_CORE_FILE_MANAGE_READ_RECORD.READTIME',
            editor : true
        } ];
    },

    getManagePrintRecorListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'fileVersionNo',
            title : 'T_CORE_FILE_MANAGE_PRINT_RECOR.FILEVERSIONNO',
            editor : true
        }, {
            field : 'printUserId',
            title : 'T_CORE_FILE_MANAGE_PRINT_RECOR.PRINTUSERID',
            editor : true
        }, {
            field : 'printUserName',
            title : 'T_CORE_FILE_MANAGE_PRINT_RECOR.PRINTUSERNAME',
            editor : true
        }, {
            field : 'printOrgName',
            title : 'T_CORE_FILE_MANAGE_PRINT_RECOR.PRINTORGNAME',
            editor : true
        }, {
            field : 'printTime',
            title : 'T_CORE_FILE_MANAGE_PRINT_RECOR.PRINTTIME',
            editor : true
        } ];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'sortor',
            title : 'T_CORE_FILE_MANAGE.SORTOR'
        }, {
            field : 'fileNo',
            title : 'T_CORE_FILE_MANAGE.FILENO'
        }, {
            field : 'fileTypeName',
            title : 'T_CORE_FILE_MANAGE.FILETYPENAME'
        }, {
            field : 'fileCategoryName',
            title : 'T_CORE_FILE_MANAGE.FILECATEGORYNAME'
        }, {
            field : 'fileName',
            title : 'T_CORE_FILE_MANAGE.FILENAME'
        }, {
            field : 'auditNo',
            title : 'T_CORE_FILE_MANAGE.AUDITNO'
        }, {
            field : 'fileVolumeName',
            title : 'T_CORE_FILE_MANAGE.FILEVOLUMENAME'
        }, {
            field : 'createdByName',
            title : 'T_CORE_FILE_MANAGE.CREATEDBYNAME',
            width : 80
        }, {
            field : 'createdTime',
            title : 'T_CORE_FILE_MANAGE.CREATEDTIME',
            width : 80
        }, {
            field : 'remark',
            title : 'T_CORE_FILE_MANAGE.REMARK'
        } ];
    },
    
    getAuthSearchListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'sortor',
            title : 'T_CORE_FILE_MANAGE_AUTH.SORTOR'
        }, {
            field : 'roleId',
            title : 'T_CORE_FILE_MANAGE_AUTH.ROLEID'
        }, {
            field : 'roleName',
            title : 'T_CORE_FILE_MANAGE_AUTH.ROLENAME'
        }, {
            field : 'printFlag',
            title : 'T_CORE_FILE_MANAGE_AUTH.PRINTFLAG',
            type : 'simpleCheckbox'
        }, {
            field : 'readFlag',
            title : 'T_CORE_FILE_MANAGE_AUTH.READFLAG',
            type : 'simpleCheckbox'
        }, {
            field : 'remark',
            title : 'T_CORE_FILE_MANAGE_AUTH.REMARK'
        } ];
    },
    
    baseUrl : IFM_CONTEXT + '/secure/core/module/qms/file-manages',
    
    workflow : {
        dbTable : 'T_CORE_FILE_MANAGE_VERSION',
        columns : [ {
            field : 'id',
            title : 'T_CORE_FILE_MANAGE_VERSION.ID'
        } ]
    },
    
    readFile : function(item) {
        var list = item.split(',');
        if (Gikam.isEmpty(list[1]) || list[1] == 'undefined') {
            Gikam.alert('QMS.MODULE.FILEMANAGE.TIP.NO_FILE')
            return;
        }
        Gikam.openFile(list[1], true);
        Gikam.post(Gikam.printf(IFM_CONTEXT + '/secure/core/module/qms/file-manages/{id}/manage-read-records', {
            id : list[2]
        }), Gikam.getJsonWrapper({}, [ '', [{'fileVersionId' : list[0]}] ]))
    },

    printFile : function(item) {
        var list = item.split(',');
        if (Gikam.isEmpty(list[1]) || list[1] == 'undefined') {
            Gikam.alert('QMS.MODULE.FILEMANAGE.TIP.NO_FILE')
            return;
        }
        Gikam.printFile(list[1], true, null, false, true);
        Gikam.post(Gikam.printf(IFM_CONTEXT + '/secure/core/module/qms/file-manages/{id}/manage-print-recors', {
            id : list[2]
        }), Gikam.getJsonWrapper({}, [ '', [{'fileVersionId' : list[0]}] ]))
    }
});
