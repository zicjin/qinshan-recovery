emcMethod.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-method-edit-list-grid',
            url : emcMethod.baseUrl + '/queries/raw',
            service : 'emcMethodServiceImpl',
            columns : emcMethod.getEditListColumns(),
            page : true,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                }
            },
            deleteFormatter : function(row) {
                return row.methodName || row.id;
            },
            activateFormatter : function(row) {
                return row.methodName || row.id;
            },
            deactivateFormatter : function(row) {
                return row.methodName || row.id;
            },
            onRowActive : function(index, row) {
                if (row) {
                    Gikam.getComp('emc-method-detail-version-list-grid').refresh({
                        url : _this.getVersionUrl(row.id)
                    });
                }
            },
            toolbar : _this.getBtnToolbarParam(),
        }
    },

    getBtnToolbarParam : function() {
        var _this = this;
        return [ {
            type : 'button',
            text : 'GIKAM.BUTTON.INSERT',
            icon : 'add',
            onClick : function() {
                Gikam.preInsert({
                    modalTitle : 'EMC.MODULE.MDM.BIZMDM.METHODS.TAB.METHOD_ADD',
                    fields : emcMethod.getBaseInfoFormFields(),
                    url : emcMethod.baseUrl
                }).done(function(id) {
                    Gikam.getComp('emc-method-edit-list-grid').refresh();
                });
            }
        }, {
            type : 'button',
            text : 'GIKAM.BUTTON.DELETE',
            icon : 'remove',
            onClick : function() {
                var selections = Gikam.getComp('emc-method-edit-list-grid').getSelections();
                if (selections.length > 0) {
                    var url = emcMethod.baseUrl + '/action/check-used'
                    Gikam.post(url, Gikam.getJsonWrapper({}, [ '', selections ])).done(function(data) {
                        if (!Gikam.isEmpty(data)) {
                            Gikam.confirm('GIKAM.TIP.CONFIRM_TO_DELETE', 'EMC.MODULE.MDM.BIZMDM.METHODS.TIP.METHOD_CHECK_USED', function() {
                                Gikam.getComp('emc-method-edit-list-grid').deleteRows(emcMethod.baseUrl);
                            })
                        } else {
                            Gikam.getComp('emc-method-edit-list-grid').deleteRows(emcMethod.baseUrl);
                        }
                    });
                } else {
                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                }
            }
        }, {
            type : 'button',
            text : 'GIKAM.BUTTON.ACTIVE',
            icon : 'enable',
            onClick : function() {
                Gikam.getComp('emc-method-edit-list-grid').activateRows(emcMethod.baseUrl + '/action/activate');
            }
        }, {
            type : 'button',
            text : 'GIKAM.BUTTON.DEACTIVE',
            icon : 'disable',
            onClick : function() {
                Gikam.getComp('emc-method-edit-list-grid').deactivateRows(emcMethod.baseUrl + '/action/deactivate');
            }
        }/*, {
            type : 'button',
            text : 'EMC.MODULE.MDM.BIZMDM.METHODS.BUTTON.SYNC',
            icon : 'copy',
            onClick : function() {
                Gikam.create('modal', {
                    width : 470,
                    title : 'EMC.MODULE.MDM.BIZMDM.METHODS.TITLE.GET_DATA',
                    url : emcMethod.baseUrl + '/page/emc-method-estandard-list',
                    onAfterClose : function(rows) {
                        if (Gikam.isEmpty(rows)) {
                            return;
                        }
                        workspace.window.showMask(true);
                        Gikam.post(emcMethod.baseUrl + '/estand-datas/action/sync', Gikam.getJsonWrapper({}, [ '', rows ])).always(function() {
                            workspace.window.closeMask();
                            Gikam.getComp('emc-method-edit-list-grid').refresh();
                        });
                    }
                });
            }
        }, {
            type : 'button',
            text : 'GIKAM.BUTTON.EXCEL_EXPORT',
            icon : 'edit',
            onClick : function() {
                Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}', {
                    templateCode : 'emcMethodExportTemplate',
                    service : 'emcMethodExportTemplate'
                })).done(function(fileUri) {
                    var fileUrl = IFM_CONTEXT + fileUri;
                    Gikam.emcDownloadFile(fileUrl);
                });
            }
        }, {
            type : 'button',
            text : 'GIKAM.BUTTON.EXCEL_IMPORT',
            icon : 'edit',
            onClick : function() {
                Gikam.importExcel({
                    url : IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'emcMethodImportFileFunction',
                    dbTable : 'T_EMC_METHOD',
                    onImportSuccess : function(files) {
                        Gikam.getComp('emc-method-edit-list-grid').refresh();
                    }
                });
            }
        }*/ ]
    },

    getVersionGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.MDM.BIZMDM.METHODS.TAB.METHOD_VERSION',
                items : [ {
                    type : 'grid',
                    id : 'emc-method-detail-version-list-grid',
                    service : 'emcMethodVersionServiceImpl',
                    onCellClick : function(field, row) {
                        field === 'ext$.filename' && Gikam.openFile(row.ext$.fileid, true);
                    },
                    deleteFormatter : function(row) {
                        return row.versionNo || row.id;
                    },
                    onUpdated : function() {
                        _this.updateVersionInfo();
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertVersionRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteVersionRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.FILE.BUTTON.UPLOAD_FILE',
                        icon : 'upload',
                        onClick : function() {
                            _this.uploadVersionFile();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.FILE.BUTTON.DELETE_FILE',
                        icon : 'file-remove',
                        onClick : function() {
                            _this.deleteVersionFile();
                        }
                    } ],
                    columns : emcMethod.getVersionListColumns()
                } ]
            } ]
        }
    },

    insertVersionRow : function() {
        var activeRow = Gikam.getComp('emc-method-edit-list-grid').getActivedRow();
        var url = Gikam.printf(emcMethod.baseUrl + '/{id}/versions', {
            id : activeRow.id
        });

        Gikam.getComp('emc-method-detail-version-list-grid').insert(url, [ {
            methodId : activeRow.id
        } ]);
    },

    deleteVersionRow : function() {
        var activeRow = Gikam.getComp('emc-method-edit-list-grid').getActivedRow();
        var url = Gikam.printf(emcMethod.baseUrl + '/{id}/versions', {
            id : activeRow.id
        });
        Gikam.getComp('emc-method-detail-version-list-grid').deleteRows(url);
    },

    updateVersionInfo : function() {
        var activeRow = Gikam.getComp('emc-method-edit-list-grid').getActivedRow();
        var url = Gikam.printf(emcMethod.baseUrl + '/{id}/action/update-version', {
            id : activeRow.id
        });
        workspace.window.showMask(true);
        Gikam.put(url, Gikam.getJsonWrapper({}, [ '', [ activeRow ] ])).always(function() {
            workspace.window.closeMask();
        });
    },

    uploadVersionFile : function() {
        var rows = Gikam.getComp('emc-method-detail-version-list-grid').getSelections();
        if (rows && rows.length == 1) {
            var bizId = rows[0].id;
            Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
                f : {
                    targetId : 'T_EMC_METHOD_VERSION$' + bizId
                },
                n : -1,
                s : 50
            })).done(function(r) {
                if (r.total > 0) {
                    Gikam.confirm('EMC.MODULE.MDM.BIZMDM.METHODS.TIP.FILE_OVERRIDE', 'EMC.MODULE.MDM.BIZMDM.METHODS.TIP.FILE_OVERRIDE', function() {
                        Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', r.rows ])).done(function(r) {
                            Gikam.create('simpleUploader', {
                                id : 'emc-method-detail-version-uploader',
                                dbTable : 'T_EMC_METHOD_VERSION',
                                bizId : bizId,
                                multiple : false,
                                onAfterClose : function() {
                                    Gikam.getComp('emc-method-detail-version-list-grid').refresh()
                                }
                            });
                        });
                    })
                } else {
                    Gikam.create('simpleUploader', {
                        id : 'emc-method-detail-version-uploader',
                        dbTable : 'T_EMC_METHOD_VERSION',
                        bizId : bizId,
                        multiple : false,
                        onAfterClose : function() {
                            Gikam.getComp('emc-method-detail-version-list-grid').refresh()
                        }
                    });
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    deleteVersionFile : function() {
        var _this = this;
        var grid = Gikam.getComp('emc-method-detail-version-list-grid');
        var rows = grid.getSelections();
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.confirm('EMC.MODULE.MDM.BIZMDM.METHODS.TITLE.FILE_DELETE', 'EMC.MODULE.MDM.BIZMDM.METHODS.TIP.FILE_DELETE', function() {
            workspace.window.showMask(true);
            var ids = rows.map(function(item) {
                return {
                    id : item.ext$.fileid
                }
            });
            Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', ids ])).done(function() {
                grid.refresh()
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    getVersionUrl : function(methodId) {
        return Gikam.printf(emcMethod.baseUrl + '/{id}/versions/queries', {
            id : methodId
        });
    },

    downloadVersionFile : function(fileId) {
        Gikam.getJson(IFM_CONTEXT + '/core/module/item/files/' + fileId).done(function(coreFile) {
            Gikam.emcDownloadFile(IFM_CONTEXT + coreFile.downloadUrl);
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-method-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getVersionGridParam() ]
            },
            west : {
                width : '25%',
                items : [ this.getGridParam() ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
