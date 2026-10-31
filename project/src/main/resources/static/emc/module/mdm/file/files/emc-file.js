var emcFile = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/file/files',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'fileNo',
            title : 'T_EMC_FILE.FILENO',
            type : 'link',
        }, {
            field : 'fileName',
            title : 'T_EMC_FILE.FILENAME',
        }, {
            field : 'ext$.filecategoryname',
            title : 'T_EMC_FILE.FILECATEGORYID',
        }, {
            field : 'ext$.archiveorgname',
            title : 'T_EMC_FILE.ARCHIVEORGID'
        }, {
            field : 'archiveName',
            title : 'T_EMC_FILE.ARCHIVENAME'
        }, {
            field : 'registerDate',
            title : 'T_EMC_FILE.REGISTERDATE',
        }, {
            field : 'ext$.registerusername',
            title : 'T_EMC_FILE.REGISTERUSERID',
        }, {
            field : 'ext$.departmentusername',
            title : 'T_EMC_FILE.DEPARTMENTUSERID'
        }, {
            field : 'loc',
            title : 'T_EMC_FILE.LOC'
        }, {
            field : 'ext$.auditusername',
            title : 'T_EMC_FILE.AUDITUSERID',
        }, {
            field : 'ext$.approveusername',
            title : 'T_EMC_FILE.APPROVEUSERID',
        }, {
            field : 'approveDate',
            title : 'T_EMC_FILE.APPROVEDATE',
        }, {
            field : 'viewOrgName',
            title : '可查看科室',
        } ];
    },

    getBaseInfoFormFields : function(operType) {
        var isReadonly = emcFile.isReadonlyDetail(operType);
        return [ {
            field : 'fileNo',
            title : 'T_EMC_FILE.FILENO',
            readonly : isReadonly,
            validators : [ 'notEmpty', 'strLength[0,16]' ],
            preInsert : true
        }, {
            field : 'fileName',
            title : 'T_EMC_FILE.FILENAME',
            readonly : isReadonly,
            validators : [ 'strLength[0,42]' ],
            preInsert : true
        }, {
            field : 'fileCategoryId',
            title : 'T_EMC_FILE.FILECATEGORYID',
            readonly : isReadonly,
            type : 'select',
            search : true,
            category : 'fileCategoryName',
            preInsert : true
        }, {
            field : 'ext$.archiveorgname',
            title : 'T_EMC_FILE.ARCHIVEORGID',
            readonly : isReadonly,
            validators : [ 'strLength[0,32]' ],
            category : 'org-simple',
            type : 'choose',
            single : true,
            onBeforeChoose : function() {
                return {
                    orgCategory_SEQ : 'department',
                    parentId_SB : workspace.user.ext$.parentorgid
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-file-detail-base-info-form').setData({
                        archiveOrgId : rows[0].id,
                        ext$ : {
                            archiveorgname : rows[0].orgName,
                        }
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-file-detail-base-info-form').setData({
                    archiveOrgId : '',
                    ext$ : {
                        archiveorgname : ''
                    }
                });
            },
        }, {
            field : 'archiveName',
            title : 'T_EMC_FILE.ARCHIVENAME',
            readonly : isReadonly,
            validators : [ 'strLength[0,32]' ],
        }, {
            field : 'registerDate',
            title : 'T_EMC_FILE.REGISTERDATE',
            readonly : isReadonly,
            type : 'date'
        }, {
            field : 'ext$.registerusername',
            title : 'T_EMC_FILE.REGISTERUSERID',
            readonly : isReadonly,
            validators : [ 'strLength[0,32]' ],
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        registerUserId : rows[0].id,
                        ext$ : {
                            registerusername : rows[0].userName,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    registerUserId : '',
                    ext$ : {
                        registerusername : ''
                    }
                });
            },
        }, {
            field : 'ext$.departmentusername',
            title : 'T_EMC_FILE.DEPARTMENTUSERID',
            readonly : isReadonly,
            validators : [ 'strLength[0,10]' ],
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        departmentUserId : rows[0].id,
                        ext$ : {
                            departmentusername : rows[0].userName,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    departmentUserId : '',
                    ext$ : {
                        departmentusername : ''
                    }
                });
            },
        }, {
            field : 'loc',
            title : 'T_EMC_FILE.LOC',
            readonly : isReadonly,
            validators : [ 'strLength[0,64]' ],
        }, {
            field : 'ext$.auditusername',
            title : 'T_EMC_FILE.AUDITUSERID',
            readonly : isReadonly,
            validators : [ 'strLength[0,32]' ],
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        auditUserId : rows[0].id,
                        ext$ : {
                            auditusername : rows[0].userName,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    auditUserId : '',
                    ext$ : {
                        auditusername : ''
                    }
                });
            },
        }, {
            field : 'ext$.approveusername',
            title : 'T_EMC_FILE.APPROVEUSERID',
            readonly : isReadonly,
            validators : [ 'strLength[0,32]' ],
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        approveUserId : rows[0].id,
                        ext$ : {
                            approveusername : rows[0].userName,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    approveUserId : '',
                    ext$ : {
                        approveusername : ''
                    }
                });
            },
        }, {
            field : 'approveDate',
            title : 'T_EMC_FILE.APPROVEDATE',
            readonly : isReadonly,
            type : 'date'
        }, {
            field : 'viewOrgName',
            title : '可查看科室',
            readonly : isReadonly,
            category : 'org-simple',
            type : 'choose',
            onBeforeChoose : function() {
                return {
                    orgCategory_SEQ : 'department',
                    parentId_SB : workspace.user.ext$.parentorgid
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-file-detail-base-info-form').setData({
                        viewOrgId : rows[0].id,
                        viewOrgName : rows[0].orgName
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-file-detail-base-info-form').setData({
                    viewOrgId : '',
                    viewOrgName : ''
                });
            },
        } ];
    },

    getFileVersionColumns: function(operType) {
        var isReadonly = emcFile.isReadonlyDetail(operType);
        return [ {
            checkbox : true
        }, {
            field : 'versionNo',
            title : 'T_EMC_FILE_VERSION.VERSIONNO',
            readonly : isReadonly,
            validators : [ 'strLength[0,16]' ],
            editor : !isReadonly,
        }, {
            field : 'ext$.applyusername',
            title : 'T_EMC_FILE_VERSION.APPLYUSERID',
            readonly : isReadonly,
            validators : [ 'strLength[0,32]' ],
            editor : !isReadonly,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                var activeRow = Gikam.getComp('emc-file-detail-file-version-grid').getActivedRow();
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id: activeRow.id,
                        applyUserId : rows[0].id,
                        ext$ : {
                            applyusername : rows[0].userName,
                        }
                    });
                }
            },
            onClean : function() {
                var activeRow = Gikam.getComp('emc-file-detail-file-version-grid').getActivedRow();
                this.setData({
                    id : activeRow.id,
                    applyUserId : '',
                    ext$ : {
                        applyusername : ''
                    }
                });
            },
        }, {
            field : 'usedUserName',
            title : 'T_EMC_FILE_VERSION.USEDUSERID',
            readonly : isReadonly,
            validators : [ 'strLength[0,32]' ],
            editor : !isReadonly,
            type : 'choose',
            category : 'user-simple',
            single : false,
            onAfterChoose : function(rows, index) {
                var activeRow = Gikam.getComp('emc-file-detail-file-version-grid').getActivedRow();
                if (!Gikam.isEmpty(rows)) {
                    var usedUserIds = [];
                    var usedUserNames = [];
                    Gikam.each(rows, function(index, item) {
                        usedUserIds.push(item.id);
                        usedUserNames.push(item.userName);
                    });
                    this.setData({
                        id : activeRow.id,
                        usedUserId : usedUserIds.join(','),
                        usedUserName : usedUserNames.join(','),
                    });
                }
            },
            onClean : function() {
                var activeRow = Gikam.getComp('emc-file-detail-file-version-grid').getActivedRow();
                this.setData({
                    id : activeRow.id,
                    usedUserId : '',
                    usedUserName : ''
                });
            },
        }, {
            field : 'operationType',
            title : 'T_EMC_FILE_VERSION.OPERATIONTYPE',
            readonly : isReadonly,
            search : true,
            editor : !isReadonly,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'fileVersionOperationType',
            }
        }, {
            field : 'modifyReason',
            title : 'T_EMC_FILE_VERSION.MODIFYREASON',
            readonly : isReadonly,
            validators : [ 'strLength[0,10]' ],
            editor : !isReadonly,
        }, {
            field : 'beforeModifyContent',
            title : 'T_EMC_FILE_VERSION.BEFOREMODIFYCONTENT',
            readonly : isReadonly,
            validators : [ 'strLength[0,10]' ],
            editor : !isReadonly,
        }, {
            field : 'afterModifyContent',
            title : 'T_EMC_FILE_VERSION.AFTERMODIFYCONTENT',
            readonly : isReadonly,
            validators : [ 'strLength[0,10]' ],
            editor : !isReadonly,
        } ];
    },

    getFileVersionDistColumns : function(operType) {
        var isReadonly = emcFile.isReadonlyDetail(operType);
        return [ {
            checkbox : true
        }, {
            field : 'controlled',
            title : 'T_EMC_FILE_VERSION_DIST.CONTROLLED',
            readonly : isReadonly,
            search : true,
            editor : !isReadonly,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            field : 'distNo',
            title : 'T_EMC_FILE_VERSION_DIST.DISTNO',
            readonly : isReadonly,
            validators : [ 'strLength[0,10]' ],
            editor : !isReadonly,
        }, {
            field : 'operationType',
            title : 'T_EMC_FILE_VERSION_DIST.OPERATIONTYPE',
            readonly : isReadonly,
            search : true,
            editor : !isReadonly,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'fileVersionDistOperationType',
            }
        }, {
            field : 'ext$.approveusername',
            title : 'T_EMC_FILE_VERSION_DIST.APPROVEUSERID',
            readonly : isReadonly,
            validators : [ 'strLength[0,10]' ],
            editor : !isReadonly,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                var activeRow = Gikam.getComp('emc-file-detail-file-version-dist-grid').getActivedRow();
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id: activeRow.id,
                        approveUserId : rows[0].id,
                        ext$ : {
                            approveusername : rows[0].userName,
                        }
                    });
                }
            },
            onClean : function() {
                var activeRow = Gikam.getComp('emc-file-detail-file-version-dist-grid').getActivedRow();
                this.setData({
                    id : activeRow.id,
                    approveUserId : '',
                    ext$ : {
                        approveusername : ''
                    }
                });
            },
        }, {
            field : 'reqDate',
            title : 'T_EMC_FILE_VERSION_DIST.REQDATE',
            readonly : isReadonly,
            validators : [ 'strLength[0,10]' ],
            editor : !isReadonly,
            type : 'date'
        }, {
            field : 'ext$.requsername',
            title : 'T_EMC_FILE_VERSION_DIST.REQUSERID',
            validators : [ 'strLength[0,10]' ],
            editor : !isReadonly,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                var activeRow = Gikam.getComp('emc-file-detail-file-version-dist-grid').getActivedRow();
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id: activeRow.id,
                        reqUserId : rows[0].id,
                        ext$ : {
                            requsername : rows[0].userName,
                        }
                    });
                }
            },
            onClean : function() {
                var activeRow = Gikam.getComp('emc-file-detail-file-version-dist-grid').getActivedRow();
                this.setData({
                    id: activeRow.id,
                    reqUserId : '',
                    ext$ : {
                        requsername : '',
                    }
                });
            },
        }, {
            field : 'recycleDate',
            title : 'T_EMC_FILE_VERSION_DIST.RECYCLEDATE',
            readonly : isReadonly,
            validators : [ 'strLength[0,10]' ],
            editor : !isReadonly,
            type : 'date'
        }, {
            field : 'ext$.recycleusername',
            title : 'T_EMC_FILE_VERSION_DIST.RECYCLEUSERID',
            readonly : isReadonly,
            validators : [ 'strLength[0,10]' ],
            editor : !isReadonly,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                var activeRow = Gikam.getComp('emc-file-detail-file-version-dist-grid').getActivedRow();
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id: activeRow.id,
                        recycleUserId : rows[0].id,
                        ext$ : {
                            recycleusername : rows[0].userName,
                        }
                    });
                }
            },
            onClean: function() {
                var activeRow = Gikam.getComp('emc-file-detail-file-version-dist-grid').getActivedRow();
                this.setData({
                    id: activeRow.id,
                    recycleUserId : '',
                    ext$ : {
                        recycleusername : '',
                    }
                });
            },
        }, {
            field : 'recycleState',
            title : 'T_EMC_FILE_VERSION_DIST.RECYCLESTATE',
            readonly : isReadonly,
            validators : [ 'strLength[0,10]' ],
            editor : !isReadonly
        } ];
    },

    /**
     * 根据操作类型判断详情页面的一般字段是否只读
     */
    isReadonlyDetail : function(operType) {
        if (operType === 'search') {
            return true;
        }
        return false;
    },

    getChooseListColumns : function() {
        return [ {
            field : 'fileNo',
            title : 'T_EMC_FILE.FILENO',
        }, {
            field : 'fileName',
            title : 'T_EMC_FILE.FILENAME',
        }, {
            field : 'ext$.filecategoryname',
            title : 'T_EMC_FILE.FILECATEGORYID',
        } ];
    },
    getMethodFileListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'name',
            title : 'T_CORE_FILE.NAME',
            width : 300,
            formatter : function(i, v, r) {
                // 如果是word文档则打开编辑否则为下载文件
                var typeArray = [
                    'doc', 'docm', 'docx', 'dot', 'dotm', 'dotx',
                    'epub', 'fodt', 'htm', 'html', 'mht', 'odt',
                    'ott', 'pdf', 'rtf', 'txt', 'djvu', 'xps',
                    'csv', 'fods', 'ods', 'ots', 'xls', 'xlsm',
                    'xlsx', 'xlt', 'xltm', 'xltx','fodp', 'odp', 
                    'otp', 'pot', 'potm', 'potx','pps', 'ppsm', 
                    'ppsx', 'ppt', 'pptm', 'pptx'
                ];
                if (typeArray.includes(r.fileExt)) {
                    return `<a href="javascript:;" onclick="Gikam.openFile('${r.id}', true)">${r.name}</a>`;
                } else {
                    return `<a href="javascript:;" onclick="Gikam.printFile('${r.id}', true, null, false, true)">${r.name}</a>`;
                }
            }
        }, {
            field : 'bizCategory',
            title : 'T_CORE_FILE.FILECATEGORY',
            type : 'select',
            search : true,
            editor : true,
            category : 'sys',
            param : {
                codeCategoryId : 'fileBizCategory',
            }
        }, {
            field : 'createdByName',
            title : 'T_CORE_FILE.CREATEDBYNAME',
        }, {
            field : 'createdTime',
            title : 'T_CORE_FILE.CREATEDTIME',
        }, {
            width: 100,
            title: '操作',
            formatter: function(i, v, r) {
                let str = '<div style= "display: flex; justify-content: space-between ">';
                str += `<a href="javascript:;" onclick="Gikam.emcDownloadFile('${Gikam.IFM_CONTEXT}${r.downloadUrl}','${r.name}')" key = "download" style="flex: 1;text-align: center;">下载</a>`;
                str += '</div>';
                return str;
            }
        } ];
    },
});
