coreFileManage.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-file-manage-edit-list-grid',
            url : coreFileManage.baseUrl + '/queries/raw',
            columns : coreFileManage.getEditListColumns(),
            service : 'coreFileManageServiceImpl',
            deleteFormatter : function(row) {
                return row.fileName;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-file-manage-edit-list-manage-version-grid', 'core-file-manage-edit-list-manage-auth-grid','core-file-manage-edit-list-manage-read-record-grid' ,'core-file-manage-edit-list-manage-print-recor-grid' ]);
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-file-manage-edit-list-manage-version-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreFileManage.baseUrl + '/{id}/manage-versions/queries', {
                            id : row.id
                        }),
                        requestData : {
                            processStatus_SB : 'draft'
                        }
                    });
                });
                Gikam.getAsyncComp('core-file-manage-edit-list-manage-auth-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreFileManage.baseUrl + '/{id}/manage-auths/queries', {
                            id : row.id
                        })
                    });
                });
                Gikam.getAsyncComp('core-file-manage-edit-list-manage-read-record-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreFileManage.baseUrl + '/{id}/manage-read-records/queries', {
                            id : row.id
                        })
                    });
                });
                Gikam.getAsyncComp('core-file-manage-edit-list-manage-print-recor-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreFileManage.baseUrl + '/{id}/manage-print-recors/queries', {
                            id : row.id
                        })
                    });
                });
            },
            toolbar : [ {
                type: 'form',
                id : 'core-file-manage-edit-list-toolbar-form-file-type',
                fields: [{
                    field: 'fileTypeId_EQ',
                    title: '',
                    type: 'select',
                    category : 'file-type',
                    param : {
                        activatedFlag : 1
                    }
                }]
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.insertFileManage();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-file-manage-edit-list-grid').deleteRows(coreFileManage.baseUrl);
                }
            }, {
                type : 'button',
                text : 'MODULE.QMS.FILEMANAGES.BUTTON.BATCH_IMPORT',
                icon : 'move-up',
                onClick : function() {
                    _this.batchImport();
                }
            }, {
                type : 'button',
                text : 'MODULE.QMS.FILEMANAGES.BUTTON.BATCH_FILE_LINK',
                icon : 'link',
                onClick : function() {
                    var grid = Gikam.getComp('core-file-manage-edit-list-grid');
                    if (grid.getSelections().length < 1) {
                        return;
                    }
                    workspace.window.showMask(true);
                    Gikam.put(coreFileManage.baseUrl + '/action/batch-file-link', Gikam.getJsonWrapper({}, [ '', grid.getSelections() ])).done(function(item) {
                        grid.refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }, {
                type: 'form',
                id : 'core-file-manage-edit-list-toolbar-form-file-category',
                fields: [ {
                    field: 'fileVolumeId_EQ',
                    title: '',
                    type: 'select',
                    category : 'file-volume',
                    param : {
                        activatedFlag : 1
                    }
                }]
            }, {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    
                }
            } ]
        }
    },

    insertFileManage : function() {
        var modal = Gikam.create('modal', {
            title: 'CORE.MODULE.QMS.FILEMANAGES.MODAL.ADD',
            width: 400,
            height: 350
        });
        Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type : 'form',
                    id : 'core-file-manage-insert-form',
                    columns : 1,
                    fields : coreFileManage.getInsertFileManageFormFields()
                }, {
                    type: 'btnToolbar',
                    items: [ {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CONFIRM',
                        'class': 'blue',
                        onClick : function() {
                            var form = Gikam.getComp('core-file-manage-insert-form');
                            if (Gikam.isFalse(form.validate()))
                                return;
                            var insertData = form.getData();
                            Gikam.post(coreFileManage.baseUrl, Gikam.getJsonWrapper({}, ['', [ insertData ]])).done(function(item) {
                                modal.close();
                                Gikam.getComp('core-file-manage-edit-list-grid').refresh();
                            });
                        }
                    }, {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CANCEL',
                        onClick : function() {
                            modal.close();
                        }
                    } ]
                } ]
            }
        });
    },
    
    batchImport : function() {
        var modal = Gikam.create('modal', {
            title: 'MODULE.QMS.FILEMANAGES.BUTTON.BATCH_IMPORT',
            width: 600,
            height: 150
        });
        Gikam.create('layout', {
            renderTo: modal.window.$dom,
            west: {
                width : '65%',
                items: [ {
                    type : 'form',
                    id : 'core-file-manage-batch-import-form',
                    columns : 1,
                    fields : [ {
                        field : 'importFileName',
                        title : 'CORE.MODULE.QMS.FILE_MANAGE.TITLE.SELECT_FILE',
                        readonly : true
                    } ]
                } ]
            },
            east : {
                width : '35%',
                type: 'btnToolbar',
                items: [ {
                    type: 'button',
                    margin:'12px 10px 0 0',
                    text: 'QMS.MODULE.FILEMANAGE.TITLE.SELECT_FILE',
                    'class': 'blue',
                    onClick : function() {
                        Gikam.create('simpleUploader', {
                            id : 'core-file-manage-uploader',
                            dbTable : 'T_CORE_FILE_MANAGE',
                            bizId : 'input',
                            multiple : false,
                            onUploadSuccess : function(fileList) {
                                Gikam.getComp('core-file-manage-batch-import-form').setData({
                                    importFileId : fileList[0].id,
                                    importFileName : fileList[0].name
                                });
                            }
                        });
                    }
                }, {
                    type: 'button',
                    margin:'12px 10px 0 0',
                    text: 'QMS.MODULE.FILEMANAGE.TITLE.DOWNLOAD_IMPORT_TEMPLATE',
                    onClick : function() {
                        Gikam.getText(Gikam.printf(IFM_CONTEXT + '/core/module/item/files/{templateCode}/action/download-template/{service}', {
                            templateCode : 'coreFileManageExportTemplate',
                            service : 'coreFileManageExportTemplate'
                        })).done(function(fileUri) {
                            var fileUrl = IFM_CONTEXT + fileUri;
                            Gikam.download(fileUrl);
                        });
                    }
                } ]
            },
            center : {
                items: [ {
                    type: 'btnToolbar',
                    items: [ {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CONFIRM',
                        'class': 'blue',
                        onClick : function() {
                            var form = Gikam.getComp('core-file-manage-batch-import-form');
                            if (Gikam.isEmpty(form.getData().importFileId)) {
                                Gikam.alert('QMS.MODULE.FILEMANAGE.TIP.PLEASE_SELECT_FILE_TO_UPLOAD');
                                return;
                            }
                            workspace.window.showMask(true);
                            Gikam.post(Gikam.printf(IFM_CONTEXT + '/core/module/item/files/{id}/action/import/{service}', {
                                id : form.getData().importFileId,
                                service : 'coreFileManageImportFileFunction'
                            })).done(function(item) {
                                modal.close();
                                Gikam.getComp('core-file-manage-edit-list-grid').refresh();
                            }).always(function() {
                                workspace.window.closeMask();
                            });
                        }
                    }, {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CANCEL',
                        'class': 'blue',
                        onClick : function() {
                            modal.close();
                        }
                    } ]
                } ]
            }
        });
    },

    getSouthTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ _this.getManageVersionGrid(), _this.getManageAuthGrid(), _this.getManageReadRecordGrid(), _this.getManagePrintRecorGrid() ]
        }
    },

    getManageVersionGrid : function() {
        var _this = this;
        return {
            title : 'CORE.MODULE.QMS.FILEMANAGES.TAB.MANAGE_VERSION',
            items : [ {
                type : 'grid',
                id : 'core-file-manage-edit-list-manage-version-grid',
                service : 'coreFileManageVersionServiceImpl',
                columns : coreFileManage.getVersionEditListColumns(),
                onCellClick : function(field, row) {
                    field === 'versionNo' && _this.loadDetailPage(row.id);
                },
                deleteFormatter : function(row) {
                    return row.versionNo;
                },
                toolbar : [ {
                    type : 'button',
                    text : 'CORE.MODULE.QMS.FILEMANAGES.BUTTON.ADD_VERSION',
                    icon : 'add-row',
                    onClick : function() {
                        _this.insertFileVersion();
                    }
                }, {
                    type : 'button',
                    text : 'CORE.MODULE.QMS.FILEMANAGES.BUTTON.DELETE_VERSION',
                    icon : 'remove-row',
                    onClick : function() {
                        Gikam.getComp('core-file-manage-edit-list-manage-version-grid').deleteRows(Gikam.printf(coreFileManage.baseUrl + '/{id}/manage-versions', {
                            id : Gikam.getComp('core-file-manage-edit-list-grid').getActivedRow().id
                        }));
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                    icon : 'submit',
                    onClick : function() {
                        Gikam.create('workflow').submit({
                            data : Gikam.getComp('core-file-manage-edit-list-manage-version-grid').getSelections(),
                            pageObject : coreFileManage
                        }).done(function() {
                            Gikam.getComp('core-file-manage-edit-list-manage-version-grid').refresh();
                        });
                    }
                } ]
            } ]
        };
    },

    insertFileVersion : function() {
        var modal = Gikam.create('modal', {
            title: 'MODULE.QMS.FILEMANAGES.MODAL.ADD_FILE_MANAGE_VERSION',
            width: 400,
            height: 150
        });
        Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type : 'form',
                    id : 'core-file-version-insert-form',
                    columns : 1,
                    fields : [ {
                        field : 'versionNo',
                        title : 'T_CORE_FILE_MANAGE_VERSION.VERSIONNO',
                        validators : [ 'number' ]
                    } ]
                }, {
                    type: 'btnToolbar',
                    items: [ {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CONFIRM',
                        'class': 'blue',
                        onClick : function() {
                            var form = Gikam.getComp('core-file-version-insert-form');
                            if (Gikam.isFalse(form.validate()))
                                return;
                            var url = Gikam.printf(coreFileManage.baseUrl + '/{id}/manage-versions/queries', {
                                id : Gikam.getComp('core-file-manage-edit-list-grid').getActivedRow().id
                            });
                            workspace.window.showMask(true);
                            Gikam.post(url, Gikam.getJsonWrapper({
                                f : {
                                    versionNo_SEQ : form.getData().versionNo
                                }
                            })).done(function(item) {
                                if (Gikam.isEmpty(item.rows)) {
                                    var insertData = form.getData();
                                    Gikam.getComp('core-file-manage-edit-list-manage-version-grid').insert(Gikam.printf(coreFileManage.baseUrl + '/{id}/manage-versions', {
                                        id : Gikam.getComp('core-file-manage-edit-list-grid').getActivedRow().id
                                    }), [ insertData ]);
                                    modal.close();
                                } else {
                                    Gikam.alert('MODULE.QMS.FILEMANAGES.ALERT.HAS_SAME_FILE_VERSIONNO_INPUT_AGIN');
                                }
                            }).always(function() {
                                workspace.window.closeMask();
                            });
                        }
                    }, {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CANCEL',
                        onClick : function() {
                            modal.close();
                        }
                    } ]
                } ]
            }
        });
    },
    
    getManageAuthGrid : function() {
        var _this = this;
        return {
            title : 'CORE.MODULE.QMS.FILEMANAGES.TAB.MANAGE_AUTH',
            items : [ {
                type : 'grid',
                id : 'core-file-manage-edit-list-manage-auth-grid',
                service : 'coreFileManageAuthServiceImpl',
                columns : coreFileManage.getManageAuthListColumns(),
                deleteFormatter : function(row) {
                    return row.roleName;
                },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add-row',
                    onClick : function() {
                        var fileManageGrid = Gikam.getComp('core-file-manage-edit-list-grid');
                        if (Gikam.isEmpty(fileManageGrid.getActivedRow())) {
                            return;
                        }
                        var roleAuthGrid = Gikam.getComp('core-file-manage-edit-list-manage-auth-grid');
                        var roleIdList = roleAuthGrid.getData().map(function(row) {
                            return row.roleId;
                        });
                        coreFileRoleAuth.getRoleAuditData(roleIdList.join(','), _this);
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function() {
                        Gikam.getComp('core-file-manage-edit-list-manage-auth-grid').deleteRows(Gikam.printf(coreFileManage.baseUrl + '/{id}/manage-auths', {
                            id : Gikam.getComp('core-file-manage-edit-list-grid').getActivedRow().id
                        }));
                    }
                } ]
            } ]
        };
    },
    
    insertRoleAuthData : function(data) {
        Gikam.getComp('core-file-manage-edit-list-manage-auth-grid').insert(Gikam.printf(coreFileManage.baseUrl + '/{id}/manage-auths', {
            id : Gikam.getComp('core-file-manage-edit-list-grid').getActivedRow().id
        }), data);
    },
    
    getManageReadRecordGrid : function() {
        return {
            title : 'CORE.MODULE.QMS.FILEMANAGES.TAB.MANAGE_READ_RECORD',
            items : [ {
                type : 'grid',
                id : 'core-file-manage-edit-list-manage-read-record-grid',
                service : 'coreFileManageReadRecordServiceImpl',
                columns : coreFileManage.getManageReadRecordListColumns(),
            } ]
        };
    },

    getManagePrintRecorGrid : function() {
        return {
            title : 'CORE.MODULE.QMS.FILEMANAGES.TAB.MANAGE_PRINT_RECOR',
            items : [ {
                type : 'grid',
                id : 'core-file-manage-edit-list-manage-print-recor-grid',
                service : 'coreFileManagePrintRecorServiceImpl',
                columns : coreFileManage.getManagePrintRecorListColumns()
            } ]
        };
    },
    
    loadDetailPage : function(id) {
        workspace.window.load(coreFileManage.baseUrl + '/page/file-versions/detail' + Gikam.param({
            id : id
        }));
    },

    create : function() {
        Gikam.create('layout', {
            id : 'core-file-manage-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '50%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getSouthTab() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
