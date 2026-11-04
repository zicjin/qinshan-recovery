coreFileType.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-file-type-edit-list-grid',
            url : coreFileType.baseUrl + '/queries/raw',
            service : 'CoreFileTypeServiceImpl',
            columns : coreFileType.getEditListColumns(),
            autoSave : true,
            deleteFormatter : function(row) {
                return row.fileTypeName;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-file-type-edit-list-category-grid', 'core-file-type-edit-list-category-role-auth-grid' ]);
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-file-type-edit-list-category-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreFileType.baseUrl + '/{id}/categorys/queries', {
                            id : row.id
                        })
                    });
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    /*Gikam.preInsert({
                        modalTitle : 'MODULE.QMS.FILE.FILETYPES.MODAL.ADD_FILE_TYPE',
                        fields : coreFileType.getInsertFileTypeFields(),
                        url : coreFileType.baseUrl
                    }).done(function(id) {
                        
                    });*/
                    _this.insertFileType();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-file-type-edit-list-grid').deleteRows(coreFileType.baseUrl);
                }
            } ]
        }
    },
    
    insertFileType : function() {
        var modal = Gikam.create('modal', {
            title: 'MODULE.QMS.FILE.FILETYPES.MODAL.ADD_FILE_TYPE',
            width: 400,
            height: 200
        });
        Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type : 'form',
                    id : 'core-file-type-insert-form',
                    columns : 1,
                    service : 'coreFileTypeServiceImpl',
                    fields : coreFileType.getInsertFileTypeFields()
                }, {
                    type: 'btnToolbar',
                    items: [ {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CONFIRM',
                        'class': 'blue',
                        onClick : function() {
                            var form = Gikam.getComp('core-file-type-insert-form');
                            if (Gikam.isFalse(form.validate()))
                                return;
                            var grid = Gikam.getComp('core-file-type-edit-list-grid');
                            var rows = grid.getData();
                            var hasSame = false;
                            Gikam.each(rows, function(index, row) {
                                if (row.fileTypeNo == form.getData().fileTypeNo) {
                                    Gikam.alert('QMS.FILE.FILETYPE.ALERT.EXIST_SAME_FILETYPENO_INPUT_AGIN');
                                    hasSame = true;
                                    return false;
                                }
                                if (row.fileTypeName == form.getData().fileTypeName) {
                                    Gikam.alert('QMS.FILE.FILETYPE.ALERT.EXIST_SAME_FILETYPENAME_INPUT_AGIN');
                                    hasSame = true;
                                    return false;
                                }
                            })
                            if (hasSame) {
                                return;
                            }
                            var insertData = form.getData();
                            grid.insert(coreFileType.baseUrl, [ insertData ]);
                            modal.close();
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
    
    getCategoryGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-file-type-edit-list-category-grid',
            service : 'coreFileCategoryServiceImpl',
            columns : coreFileType.getCategoryListColumns(),
            height : 300,
            deleteFormatter : function(row) {
                return row.fileCategoryName;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-file-type-edit-list-category-role-auth-grid' ]);
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-file-type-edit-list-category-role-auth-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreFileType.baseUrl + '/{id}/category-role-auths/queries', {
                            id : row.id
                        })
                    });
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add-row',
                onClick : function() {
                    _this.insertFileCategory();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-file-type-edit-list-category-grid').deleteRows(Gikam.printf(coreFileType.baseUrl + '/{id}/categorys', {
                        id : Gikam.getComp('core-file-type-edit-list-grid').getActivedRow().id
                    }));
                }
            }, {
                type : 'button',
                text : 'QMS.FILE.FILETYPES.BUTTON.MODELCONFIG',
                icon : 'select',
                onClick : function() {
                    var grid = Gikam.getComp('core-file-type-edit-list-category-grid');
                    if (grid.getSelections() < 1) {
                        Gikam.alert('QMS.FILE.FILETYPES.ALERT.AT_LEAST_SELECT_ONE');
                        return;
                    }
                    if (!_this.isSelectSameDraftTemplate()) {
                        Gikam.alert('MODULE.QMS.FILETYPES.ALERT.ONLY_SELECT_ONE_DRAFTTEMPLATE_TO_EDIT');
                        return;
                    }
                    if (Gikam.isEmpty(grid.getSelections()[0].fileDraftTemplateId)) {
                        Gikam.create('simpleUploader', {
                            id : 'glp-file-type-category-edit-list-uploader',
                            dbTable : 'T_CORE_FILE_CATEGORY',
                            multiple : false,
                            bizId : grid.getSelections()[0].id,
                            onUploadSuccess : function(fileList) {
                                workspace.window.showMask(true);
                                Gikam.put(Gikam.printf(coreFileType.baseUrl + '/file-categorys/{id}/action/copyfile', {
                                    id : grid.getSelections()[0].id
                                }), Gikam.getJsonWrapper({}, ['', grid.getSelections()])).done(function(item) {
                                    Gikam.each(Gikam.parseJson(item), function(index, row) {
                                        grid.setData({
                                            id : row.id,
                                            fileDraftTemplateId : row.fileDraftTemplateId
                                        });
                                    });
                                }).always(function() {
                                    workspace.window.closeMask();
                                });
                            },
                            onAfterClose : function(fileList) {
                                Gikam.openFileByOffice({
                                    fileId: fileList[0].id,
                                    mode: 'edit',
                                    viewType: 'modal'
                                });
                            }
                        });
                    } else {
                        Gikam.openFileByOffice({
                            fileId: grid.getSelections()[0].fileDraftTemplateId,
                            mode: 'edit',
                            viewType: 'modal'
                        });
                    }
                }
            } ]
        };
    },

    isSelectSameDraftTemplate : function() {
        var grid = Gikam.getComp('core-file-type-edit-list-category-grid');
        var fileDraftTemplateId = grid.getSelections()[0].fileDraftTemplateId;
        var isSame = true;
        Gikam.each(grid.getSelections(), function(index, row) {
            if (fileDraftTemplateId != row.fileDraftTemplateId) {
                isSame = false;
                return;
            }
        })
        return isSame;
    },
    
    insertFileCategory : function() {
        var activedRow = Gikam.getComp('core-file-type-edit-list-grid').getActivedRow();
        if (Gikam.isEmpty(activedRow)) {
            return;
        }
        var modal = Gikam.create('modal', {
            title: 'MODULE.QMS.FILE.FILETYPES.MODAL.ADD_FILE_CATEGORY',
            width: 400,
            height: 200
        });
        Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type : 'form',
                    id : 'core-file-category-insert-form',
                    columns : 1,
                    service : 'coreFileCategoryServiceImpl',
                    fields : coreFileType.getInsertFileCategoryFields()
                }, {
                    type: 'btnToolbar',
                    items: [ {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CONFIRM',
                        'class': 'blue',
                        onClick : function() {
                            var form = Gikam.getComp('core-file-category-insert-form');
                            if (Gikam.isFalse(form.validate()))
                                return;
                            var grid = Gikam.getComp('core-file-type-edit-list-category-grid');
                            var rows = grid.getData();
                            var hasSame = false;
                            Gikam.each(rows, function(index, row) {
                                if (row.fileCategoryNo == form.getData().fileCategoryNo) {
                                    Gikam.alert('QMS.FILE.FILETYPE.ALERT.EXIST_SAME_FILECATEGORYNO_INPUT_AGIN');
                                    hasSame = true;
                                    return false;
                                }
                                if (row.fileCategoryName == form.getData().fileCategoryName) {
                                    Gikam.alert('QMS.FILE.FILETYPE.ALERT.EXIST_SAME_FILECATEGORYNAME_INPUT_AGIN');
                                    hasSame = true;
                                    return false;
                                }
                            })
                            if (hasSame) {
                                return;
                            }
                            var insertData = form.getData();
                            grid.insert(Gikam.printf(coreFileType.baseUrl + '/{id}/categorys', {
                                id : Gikam.getComp('core-file-type-edit-list-grid').getActivedRow().id
                            }), [ insertData ]);
                            modal.close();
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
    
    getCategoryRoleAuthGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-file-type-edit-list-category-role-auth-grid',
            service : 'coreFileCategoryRoleAuthServiceImpl',
            columns : coreFileType.getCategoryRoleAuthListColumns(),
            height : 300,
            deleteFormatter : function(row) {
                return row.roleName;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add-row',
                onClick : function() {
                    var activedRow = Gikam.getComp('core-file-type-edit-list-category-grid').getActivedRow();
                    if (Gikam.isEmpty(activedRow)) {
                        return;
                    }
                    var rows = Gikam.getComp('core-file-type-edit-list-category-role-auth-grid').getData();
                    var roleIdList = rows.map(function(row) {
                        return row.roleId;
                    });
                    coreFileRoleAuth.getRoleAuditData(roleIdList.join(','), _this);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-file-type-edit-list-category-role-auth-grid').deleteRows(Gikam.printf(coreFileType.baseUrl + '/{id}/category-role-auths', {
                        id : Gikam.getComp('core-file-type-edit-list-grid').getActivedRow().id
                    }));
                }
            } ]
        };
    },

    insertRoleAuthData : function(data) {
        var fileCategoryGrid = Gikam.getComp('core-file-type-edit-list-category-grid');
        Gikam.each(data, function(index, row) {
            row.fileDraftTemplateId = fileCategoryGrid.getActivedRow().fileDraftTemplateId;
        })
        Gikam.getComp('core-file-type-edit-list-category-role-auth-grid').insert(Gikam.printf(coreFileType.baseUrl + '/{id}/category-role-auths', {
            id : fileCategoryGrid.getActivedRow().id
        }), data);
    },
    
    getMaxSortor : function(grid) {
        var maxSortor = 0;
        Gikam.each(grid.getData(), function(index, row) {
            if (maxSortor < row.sortor) {
                maxSortor = row.sortor;
            }
        })
        return maxSortor;
    },
    
    create : function() {
        Gikam.create('layout', {
            id : 'core-file-type-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '45%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getCategoryGrid(), this.getCategoryRoleAuthGrid() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
