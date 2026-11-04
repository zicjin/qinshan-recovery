coreOfficeTmpl.editPage = {

    init : function(param) {
        this.param = param;
        this.create();
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    tmplGrid : null,
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-office-tmpl-edit-list-grid',
            url : coreOfficeTmpl.baseUrl + '/queries/raw',
            columns : coreOfficeTmpl.getEditListColumns(),
            service : 'coreOfficeTmplServiceImpl',
            deleteFormatter : function(row) {
                return row.tmplName || row.id;
            },
            activateFormatter : function(row) {
                return row.tmplName || row.id;
            },
            onRendered : function() {
                _this.tmplGrid = this;
            },
            columnsFill : false,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.preInsert();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    _this.tmplGrid.deleteRows(coreOfficeTmpl.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {

                    var selectedRows = _this.tmplGrid.getSelections();

                    if (selectedRows.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    Gikam.viewAudit('T_CORE_OFFICE_TMPL', selectedRows[0].id);
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.OFFICE.TMPLS.BUTTON.UPLOAD_TEMPLATE_FILE',
                icon : 'submit',
                onClick : function() {
                    var selectedRows = _this.tmplGrid.getSelections();

                    if (selectedRows.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    _this.uploadTmplFile();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.OFFICE.TMPLS.BUTTON.INSERT_BLANK_DOCUMENT',
                icon : 'add-row',
                onClick : function() {
                    var selectedRows = _this.tmplGrid.getSelections();

                    if (selectedRows.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    _this.insertBlankFile();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.OFFICE.TMPLS.BUTTON.EDIT_TEMPLATE_FILE',
                icon : 'modify',
                onClick : function() {
                    var selectedRows = _this.tmplGrid.getSelections();

                    if (selectedRows.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    var fileId = selectedRows[0].ext$.fileid;
                    if (!fileId) {
                        Gikam.alert('CORE.MODULE.OFFICE.TMPLS.TIP.TMPL_FILE_MISSING');
                        return;
                    }
                    var fileExt = selectedRows[0].ext$.fileext;

                    const supportType = [ {
                        code : 'text',
                        typeArray : [ 'doc', 'docx' ]
                    }, {
                        code : 'spreadsheet',
                        typeArray : [ 'xls', 'xlsx' ]
                    } ]; 
                    
                    var typeStr = '';
                    supportType.forEach(item => {
                        if (item.typeArray.indexOf(fileExt) !== -1) {
                            typeStr = item.code;
                        }
                    });
                    
                    if (typeStr) {
                        var tmplId = selectedRows[0].id;

                        Gikam.openFileByOffice({
                            fileId : fileId,
                            mode : 'edit'
                        }, {
                            officeTmplId : tmplId
                        });
                    } else {
                        Gikam.alert('CORE.MODULE.OFFICE.TMPLS.TIP.UPLOAD_FILE_TYPE_LIMIT');
                    }

                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.OFFICE.TMPLS.BUTTON.DELETE_TEMPLATE_FILE',
                icon : 'remove',
                onClick : function() {
                    var selectedRows = _this.tmplGrid.getSelections();

                    if (selectedRows.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    var fileId = selectedRows[0].ext$.fileid;
                    if (!fileId) {
                        Gikam.alert('CORE.MODULE.OFFICE.TMPLS.TIP.TMPL_FILE_MISSING');
                        return;
                    }

                    Gikam.confirm('GIKAM.TIP.CONFIRM_TO_DELETE', 'CORE.MODULE.OFFICE.TMPLS.TIP.DELETE_TEMPLATE_FILE', function() {
                        Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', [ {
                            id : fileId
                        } ] ])).done(function(r) {
                            Gikam.getComp('core-office-tmpl-edit-list-grid').refresh();
                        });
                    })
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function() {
                    _this.tmplGrid.activateRows(coreOfficeTmpl.baseUrl + '/action/activate');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DEACTIVE',
                icon : 'disable',
                onClick : function() {
                    _this.tmplGrid.deactivateRows(coreOfficeTmpl.baseUrl + '/action/deactivate');
                }
            } ]
        }
    },

    uploadTmplFile : function() {
        var _this = this;
        var selectedRow = Gikam.getComp('core-office-tmpl-edit-list-grid').getSelections()[0];
        var bizId = selectedRow.id;

        Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
            f : {
                targetId : 'T_CORE_OFFICE_TMPL$' + bizId
            },
            n : -1,
            s : 50
        })).done(function(r) {
            if (r.rows.length > 0) {
                Gikam.confirm('GIKAM.FILE.BUTTON.UPLOAD_FILE', 'CORE.MODULE.OFFICE.TMPLS.TIP.OVERRIDE', function() {
                    // 删除关联附件
                    Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', r.rows ])).done(function(r) {
                        // 清空模板文件关联数据源缓存
                        Gikam.postText(Gikam.printf(coreOfficeTmpl.baseUrl + '/{id}/action/clear-bind-ds', {
                            id : bizId
                        }), null).done(function() {
                            // 调用文件上传组件
                            var uploader = Gikam.create('simpleUploader', {
                                id : 'core-office-tmpl-edit-list-uploader',
                                dbTable : coreOfficeTmpl.workflow.dbTable,
                                bizId : bizId,
                                multiple : false,
                                onAfterClose : function() {
                                    Gikam.getComp('core-office-tmpl-edit-list-grid').refreshRowById(bizId);
                                    _this.onFileUploadSuccess(bizId);
                                }
                            });
                        });
                    });
                })
            } else {
                Gikam.create('simpleUploader', {
                    id : 'core-office-tmpl-edit-list-uploader',
                    dbTable : coreOfficeTmpl.workflow.dbTable,
                    bizId : bizId,
                    multiple : false,
                    onAfterClose : function() {
                        Gikam.getComp('core-office-tmpl-edit-list-grid').refreshRowById(bizId);
                        _this.onFileUploadSuccess(bizId);
                    }
                });
            }
        });
    },

    insertBlankFile : function() {
        var _this = this;
        var selectedRow = Gikam.getComp('core-office-tmpl-edit-list-grid').getSelections()[0];
        var bizId = selectedRow.id;

        Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
            f : {
                targetId : 'T_CORE_OFFICE_TMPL$' + bizId
            },
            n : -1,
            s : 50
        })).done(function(r) {
            if (r.rows.length > 0) {
                Gikam.confirm('GIKAM.FILE.BUTTON.UPLOAD_FILE', 'CORE.MODULE.OFFICE.TMPLS.TIP.OVERRIDE', function() {
                    Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', r.rows ])).done(function(r) {
                        workspace.window.showMask(true);
                        Gikam.postText(Gikam.printf(coreOfficeTmpl.baseUrl + '/{id}/action/insert-blank-document', {
                            id : bizId
                        }), null).done(function(fileId) {
                            Gikam.getComp('core-office-tmpl-edit-list-grid').refreshRowById(bizId);
                            _this.onInsertBlankFileSuccess(fileId);
                        }).always(function() {
                            workspace && workspace.window && workspace.window.closeMask();
                        });
                    });
                })
            } else {
                workspace.window.showMask(true);
                Gikam.postText(Gikam.printf(coreOfficeTmpl.baseUrl + '/{id}/action/insert-blank-document', {
                    id : bizId
                }), null).done(function(fileId) {
                    Gikam.getComp('core-office-tmpl-edit-list-grid').refreshRowById(bizId);
                    _this.onInsertBlankFileSuccess(fileId);
                }).always(function() {
                    workspace && workspace.window && workspace.window.closeMask();
                });
            }
        });
    },

    preInsert : function() {
        var _this = this;
        Gikam.preInsert({
            modalTitle : 'CORE.MODULE.OFFICE.TMPLS.TAB.TMPL_INSERT',
            fields : coreOfficeTmpl.getBaseInfoFormFields(),
            param : {},
            url : coreOfficeTmpl.baseUrl,
            disableNotEmptyValidate : true
        }).done(function(id) {
            _this.tmplGrid.refresh();
        });
    },

    onFileUploadSuccess : function(bizId) {
        Gikam.getJson(Gikam.printf(coreOfficeTmpl.baseUrl + '/{id}', {
            id : bizId,
        })).done(function(selectedRow) {
            var fileId = selectedRow.ext$.fileid;
            if (!fileId) {
                Gikam.alert('CORE.MODULE.OFFICE.TMPLS.TIP.TMPL_FILE_MISSING');
                return;
            }
            var fileExt = selectedRow.ext$.fileext;

            const supportType = [ {
                code : 'text',
                typeArray : [ 'doc', 'docx' ]
            }, {
                code : 'spreadsheet',
                typeArray : [ 'xls', 'xlsx' ]
            } ]; 
            
            var typeStr = '';
            supportType.forEach(item => {
                if (item.typeArray.indexOf(fileExt) !== -1) {
                    typeStr = item.code;
                }
            });

            if (typeStr) {
                var tmplId = selectedRow.id;

                Gikam.openFileByOffice({
                    fileId : fileId,
                    mode : 'edit'
                }, {
                    officeTmplId : tmplId
                });
            } else {
                Gikam.alert('CORE.MODULE.OFFICE.TMPLS.TIP.UPLOAD_FILE_TYPE_LIMIT');
            }
        });
    },

    onInsertBlankFileSuccess : function(fileId) {
        var selectedRows = Gikam.getComp('core-office-tmpl-edit-list-grid').getSelections();
        var tmplId = selectedRows[0].id;

        Gikam.openFileByOffice({
            fileId : fileId,
            mode : 'edit'
        }, {
            officeTmplId : tmplId
        });
    }

};
