coreImportTmpl.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-import-tmpl-edit-list-grid',
            url : coreImportTmpl.baseUrl + '/queries/raw',
            service : 'coreImportTmplServiceImpl',
            dbTable : 'T_CORE_IMPORT_TMPL',
            columns : coreImportTmpl.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-import-tmpl-edit-list-tmpl-config-grid' ]);
                }
            },
            onCellClick : function(field, row) {
                field === 'ext$.filename' && Gikam.isNotEmpty(row.ext$.fileid) && Gikam.downloadFileById(row.ext$.fileid);
            },
            deleteFormatter : function(row) {
                return row.tmplName || row.id;
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('core-import-tmpl-edit-list-tmpl-config-grid').done(function(grid) {

                    _this.refreshConfigColumns(grid, row.tableName);

                    grid.refresh({
                        url : Gikam.printf(coreImportTmpl.baseUrl + '/{id}/configs/queries', {
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
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.ITEM.IMPORTTMPLS.MODAL.ADD',
                        fields : coreImportTmpl.getBaseInfoFormFields(),
                        url : coreImportTmpl.baseUrl,
                        param : {
                            startRowIndex : 2
                        }
                    }).done(function(id) {
                        Gikam.getComp('core-import-tmpl-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-import-tmpl-edit-list-grid').deleteRows(coreImportTmpl.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.IMPORT',
                icon : 'edit',
                onClick : function() {

                    var selections = Gikam.getComp('core-import-tmpl-edit-list-grid').getSelections();

                    if (selections.length != 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    Gikam.importExcel({
                        url : IFM_CONTEXT + '/secure/core/module/item/import-tmpls/' + selections[0].id + '/files/{id}/action/import',
                        dbTable : 'T_CORE_IMPORT_TMPL',
                        onImportSuccess : function(files) {
                            Gikam.alert('CORE.MODULE.ITEM.IMPORTTMPLS.TIP.IMPORT_SUCCSS');
                        }
                    });
                }
            }, {
                type : 'dropDownMenu',
                text : 'CORE.MODULE.ITEM.IMPORTTMPLS.BUTTON.TMPL_OPERATE',
                icon : 'finished-product-to-check',
                items : [ {
                    type : 'button',
                    text : 'CORE.MODULE.ITEM.IMPORTTMPLS.BUTTON.UPLOAD_TMPL',
                    icon : 'upload',
                    onClick : function() {
                        _this.uploadTmpl();
                    }
                }, {
                    type : 'button',
                    text : 'CORE.MODULE.ITEM.IMPORTTMPLS.BUTTON.DOWNLOAD_TMPL',
                    icon : 'export',
                    onClick : function() {
                        _this.downloadTmpl();
                    }
                } ]
            } ]
        }
    },

    refreshConfigColumns : function(grid, tableName) {

        var configColumns = coreImportTmpl.getTmplConfigListColumns();

        configColumns[3].category = 'table-field';
        configColumns[3].param = {
            tableName : tableName
        };

        grid.refreshColumns(configColumns);

    },

    // 上传附件
    uploadTmpl : function() {
        var _this = this;

        var selections = Gikam.getComp('core-import-tmpl-edit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var selectedRow = selections[0];
        var bizId = selectedRow.id;

        Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
            f : {
                targetId : 'T_CORE_IMPORT_TMPL$' + bizId
            },
            n : -1,
            s : 50
        })).done(function(r) {
            if (r.rows.length > 0) {
                Gikam.confirm('GIKAM.FILE.BUTTON.UPLOAD_FILE', 'CORE.MODULE.ITEM.IMPORTTMPLS.TIP.ATTACHMENT_EXISTS', function() {
                    Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', r.rows ])).done(function(r) {
                        var uploader = Gikam.create('simpleUploader', {
                            id : 'core-import-tmpl-edit-list-uploader',
                            dbTable : 'T_CORE_IMPORT_TMPL',
                            bizId : bizId,
                            onBeforeUpload : function() {
                                this.setUploadData({
                                    scope : 'imp'
                                })
                            },
                            onAfterClose : function() {
                                Gikam.getComp('core-import-tmpl-edit-list-grid').refreshRowById(bizId);
                            }
                        });
                    });
                })
            } else {
                Gikam.create('simpleUploader', {
                    id : 'core-import-tmpl-edit-list-uploader',
                    dbTable : 'T_CORE_IMPORT_TMPL',
                    bizId : bizId,
                    onBeforeUpload : function() {
                        this.setUploadData({
                            scope : 'imp'
                        })
                    },
                    onAfterClose : function() {
                        Gikam.getComp('core-import-tmpl-edit-list-grid').refreshRowById(bizId);
                    }
                });
            }
        });
    },

    // 下载模版
    downloadTmpl : function() {
        var selections = Gikam.getComp('core-import-tmpl-edit-list-grid').getSelections();
        if (selections.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
            f : {
                targetId_SEQ : 'T_CORE_IMPORT_TMPL$' + selections[0].id
            },
            n : -1,
            s : 50
        })).done(function(r) {
            var data = r.rows;
            if (data.length == 0) {
                Gikam.alert('CORE.MODULE.ITEM.IMPORTTMPLS.TIP.NO_FILE');
                return;
            } else {
                Gikam.download(IFM_CONTEXT + data[0].downloadUrl);
            }
        });

    },

    getConfigInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ _this.getTmplConfigGrid() ]
        }
    },

    getTmplConfigGrid : function() {
        var _this = this;
        return {
            title : 'CORE.MODULE.ITEM.IMPORTTMPLS.TAB.TMPL_CONFIG',
            items : [ {
                type : 'grid',
                id : 'core-import-tmpl-edit-list-tmpl-config-grid',
                service : 'coreImportTmplConfigServiceImpl',
                dbTable : 'T_CORE_IMPORT_TMPL_CONFIG',
                deleteFormatter : function(row) {
                    return row.excelColumn || row.id;
                },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add-row',
                    onClick : function() {
                        Gikam.getComp('core-import-tmpl-edit-list-tmpl-config-grid').insert(Gikam.printf(coreImportTmpl.baseUrl + '/{id}/configs', {
                            id : Gikam.getComp('core-import-tmpl-edit-list-grid').getActivedRow().id
                        }), [ {} ]);
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function() {
                        Gikam.getComp('core-import-tmpl-edit-list-tmpl-config-grid').deleteRows(Gikam.printf(coreImportTmpl.baseUrl + '/{id}/configs', {
                            id : Gikam.getComp('core-import-tmpl-edit-list-grid').getActivedRow().id
                        }));
                    }
                }, {
                    type : 'button',
                    text : 'CORE.MODULE.ITEM.IMPORTTMPLS.BUTTON.INIT',
                    icon : 'add-row',
                    onClick : function() {

                        var activeRow = Gikam.getComp('core-import-tmpl-edit-list-grid').getActivedRow();

                        if (Gikam.isEmpty(activeRow.tableName)) {
                            Gikam.alert('CORE.MODULE.ITEM.IMPORTTMPLS.TIP.CHOOSE_TABLE');
                            return;
                        }

                        var initConfigUrl = Gikam.printf(coreImportTmpl.baseUrl + '/{id}/action/init-config', {
                            id : activeRow.id
                        });

                        Gikam.postText(initConfigUrl).done(function() {
                            Gikam.getComp('core-import-tmpl-edit-list-tmpl-config-grid').refresh();
                        });
                    }

                } ],
                columns : coreImportTmpl.getTmplConfigListColumns()
            } ]
        };
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-import-tmpl-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 733,
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getConfigInfoTab() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
