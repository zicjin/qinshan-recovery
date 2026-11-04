//@ sourceURL=core-file-acq-config-edit-list.js
coreFileAcqConfig.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-file-acq-config-edit-list-grid',
            url : coreFileAcqConfig.baseUrl + '/queries/raw',
            columns : coreFileAcqConfig.getEditListColumns(),
            service : 'coreFileAcqConfigServiceImpl',
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-file-acq-config-edit-list-acq-param-grid', 'core-file-acq-config-edit-list-acq-preprocess-grid', 'core-file-acq-config-edit-list-acq-result-param-grid', ]);
                }
            },
            deleteFormatter : function(row) {
                return row.configDesc || row.id;
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('core-file-acq-config-edit-list-acq-param-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreFileAcqConfig.baseUrl + '/{id}/acq-params/queries', {
                            id : row.id
                        })
                    });
                });

                Gikam.getAsyncComp('core-file-acq-config-edit-list-acq-preprocess-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreFileAcqConfig.baseUrl + '/{id}/acq-preprocesses/queries', {
                            id : row.id
                        })
                    });
                });

                Gikam.getAsyncComp('core-file-acq-config-edit-list-acq-result-param-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreFileAcqConfig.baseUrl + '/{id}/acq-result-params/queries', {
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
                        modalTitle : 'CORE.MODULE.EQUIPT.FILEACQCONFIGS.MODAL.ADD',
                        fields : coreFileAcqConfig.getBaseInfoFormFields(),
                        url : coreFileAcqConfig.baseUrl,
                        titleWidth : 100
                    }).done(function(id) {
                        Gikam.getComp('core-file-acq-config-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-file-acq-config-edit-list-grid').deleteRows(coreFileAcqConfig.baseUrl);
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.EQUIPT.FILEACQCONFIGS.BUTTON.COPY',
                icon : 'copy',
                onClick : function() {
                    var selectedRows = Gikam.getComp('core-file-acq-config-edit-list-grid').getSelections();
                    if (Gikam.isEmpty(selectedRows)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    workspace.window.showMask(true);
                    Gikam.put(coreFileAcqConfig.baseUrl + '/action/copy', Gikam.getJsonWrapper(null, [ 'CoreFileAcqConfigServiceImpl', selectedRows ])).done(function() {
                        Gikam.getComp('core-file-acq-config-edit-list-grid').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            } ]
        }
    },

    getAcqParamGrid : function() {
        var _this = this;
        return {
            title : 'CORE.MODULE.EQUIPT.FILEACQCONFIGS.TAB.ACQ_PARAM',
            items : [ {
                type : 'grid',
                id : 'core-file-acq-config-edit-list-acq-param-grid',
                service : 'coreFileAcqParamServiceImpl',
                deleteFormatter : function(row) {
                    return row.ext$.codename || row.id;
                },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add-row',
                    onClick : function() {
                        Gikam.preInsert({
                            modalTitle : 'CORE.MODULE.EQUIPT.FILEACQPARAM.MODAL.ADD',
                            fields : coreFileAcqConfig.getAcqParamFields(),
                            url : Gikam.printf(coreFileAcqConfig.baseUrl + '/{id}/acq-params', {
                                id : Gikam.getComp('core-file-acq-config-edit-list-grid').getActivedRow().id
                            })
                        }).done(function(id) {
                            Gikam.getComp('core-file-acq-config-edit-list-acq-param-grid').refresh();
                        });

                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function() {
                        Gikam.getComp('core-file-acq-config-edit-list-acq-param-grid').deleteRows(Gikam.printf(coreFileAcqConfig.baseUrl + '/{id}/acq-params', {
                            id : Gikam.getComp('core-file-acq-config-edit-list-grid').getActivedRow().id
                        }));
                    }
                } ],
                columns : coreFileAcqConfig.getAcqParamListColumns()
            } ]
        };
    },

    getAcqPreprocessGrid : function() {
        var _this = this;
        return {
            title : 'CORE.MODULE.EQUIPT.FILEACQCONFIGS.TAB.ACQ_PREPROCESS',
            items : [ {
                type : 'grid',
                id : 'core-file-acq-config-edit-list-acq-preprocess-grid',
                service : 'coreFileAcqPreprocessServiceImpl',
                deleteFormatter : function(row) {
                    return row.keyword || row.id;
                },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add-row',
                    onClick : function() {

                        Gikam.preInsert({
                            modalTitle : 'CORE.MODULE.EQUIPT.FILEACQCONFIGS.MODAL.ADD',
                            fields : coreFileAcqConfig.getAcqPreprocessFields(),
                            url : Gikam.printf(coreFileAcqConfig.baseUrl + '/{id}/acq-preprocesses', {
                                id : Gikam.getComp('core-file-acq-config-edit-list-grid').getActivedRow().id
                            })
                        }).done(function(id) {
                            Gikam.getComp('core-file-acq-config-edit-list-acq-preprocess-grid').refresh();
                        });

                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function() {
                        Gikam.getComp('core-file-acq-config-edit-list-acq-preprocess-grid').deleteRows(Gikam.printf(coreFileAcqConfig.baseUrl + '/{id}/acq-preprocesses', {
                            id : Gikam.getComp('core-file-acq-config-edit-list-grid').getActivedRow().id
                        }));
                    }
                } ],
                columns : coreFileAcqConfig.getAcqPreprocessListColumns()
            } ]
        };
    },

    getAcqResultParamGrid : function() {
        var _this = this;
        return {
            title : 'CORE.MODULE.EQUIPT.FILEACQCONFIGS.TAB.ACQ_RESULT_PARAM',
            items : [ {
                type : 'grid',
                id : 'core-file-acq-config-edit-list-acq-result-param-grid',
                service : 'coreFileAcqResultParamServiceImpl',
                deleteFormatter : function(row) {
                    return row.itemName || row.id;
                },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add-row',
                    onClick : function() {

                        Gikam.getComp('core-file-acq-config-edit-list-acq-result-param-grid').insert(Gikam.printf(coreFileAcqConfig.baseUrl + '/{id}/acq-result-params', {
                            id : Gikam.getComp('core-file-acq-config-edit-list-grid').getActivedRow().id
                        }), [ {} ]);
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function() {
                        Gikam.getComp('core-file-acq-config-edit-list-acq-result-param-grid').deleteRows(Gikam.printf(coreFileAcqConfig.baseUrl + '/{id}/acq-result-params', {
                            id : Gikam.getComp('core-file-acq-config-edit-list-grid').getActivedRow().id
                        }));
                    }
                } ],
                columns : coreFileAcqConfig.getAcqResultParamListColumns()
            } ]
        };
    },

    getAcqResultGrid : function() {
        var _this = this;
        return {
            title : 'CORE.MODULE.EQUIPT.FILEACQCONFIGS.TAB.ACQ_RESULT',
            items : [ {
                type : 'grid',
                id : 'core-file-acq-config-edit-list-acq-result-grid',
                service : 'coreFileAcqResultServiceImpl',
                deleteFormatter : function(row) {
                    return row.id;
                },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add-row',
                    onClick : function() {
                        Gikam.getComp('core-file-acq-config-edit-list-acq-result-grid').insert(Gikam.printf(coreFileAcqConfig.baseUrl + '/{id}/acq-results', {
                            id : Gikam.getComp('core-file-acq-config-edit-list-grid').getActivedRow().id
                        }), [ {} ]);
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function() {
                        Gikam.getComp('core-file-acq-config-edit-list-acq-result-grid').deleteRows(Gikam.printf(coreFileAcqConfig.baseUrl + '/{id}/acq-results', {
                            id : Gikam.getComp('core-file-acq-config-edit-list-grid').getActivedRow().id
                        }));
                    }
                } ],
                columns : coreFileAcqConfig.getAcqResultListColumns()
            } ]
        };
    },

    getSouthTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ _this.getAcqParamGrid(), _this.getAcqPreprocessGrid(), _this.getAcqResultParamGrid() ]
        }
    },

    uploadFile : function() {
        var _this = this;

        var row = Gikam.getComp('core-file-acq-config-edit-list-grid').getActivedRow();

        if (Gikam.isEmpty(row)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_ONE_ITEM");
        }

        var fileAcqConfigId = row.id;
        Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
            f : {
                targetId : 'T_CORE_FILE_ACQ_CONFIG$' + fileAcqConfigId
            },
            n : -1,
            s : 50
        })).done(function(r) {
            if (r.total > 0) {
                Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', r.rows ])).done(function(r) {
                    _this.upload(fileAcqConfigId);
                });
            } else {
                _this.upload(fileAcqConfigId);
            }
        });

    },

    upload : function(fileAcqConfigId) {
        var _this = this;
        Gikam.create('simpleUploader', {
            id : 'core-file-acq-config-edit-list-uploader',
            dbTable : 'T_CORE_FILE_ACQ_CONFIG',
            bizId : fileAcqConfigId,
            multiple : false,
            onUploadSuccess : function(files) {
                _this.setFileContent(fileAcqConfigId, files);
            }
        });
    },

    setFileContent : function(fileAcqConfigId, files) {

        if (!Gikam.isEmpty(files)) {
            workspace.window.showMask(true);
            Gikam.getText(Gikam.printf(coreFileAcqConfig.baseUrl + '/{id}/files/{fileId}/info/content', {
                fileId : files[0].id,
                id : fileAcqConfigId
            })).done(function(content) {
                Gikam.getComp('core-file-acq-config-edit-list-file-content-form').setData({
                    content : Gikam.isEmpty(content) ? '' : content
                });
            }).always(function(resultObject) {
                workspace.window.closeMask();
            })
        }
    },

    // 预处理
    preprocess : function() {
        var _this = this;

        var row = Gikam.getComp('core-file-acq-config-edit-list-grid').getActivedRow();

        if (Gikam.isEmpty(row)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_ONE_ITEM");
            return 
        }

        var fileContentData = Gikam.getComp('core-file-acq-config-edit-list-file-content-form').getData();

        if (Gikam.isEmpty(fileContentData.content)) {
            Gikam.alert("CORE.MODULE.EQUIPT.FILEACQCONFIGS.TIP.UPLOAD_FILE");
            return 
        }

        workspace.window.showMask(true);
        Gikam.put(Gikam.printf(coreFileAcqConfig.baseUrl + '/{id}/action/preprocess', {
            id : row.id,
        })).done(function(content) {
            Gikam.getComp('core-file-acq-config-edit-list-file-content-form').setData({
                content : Gikam.isEmpty(content) ? '' : content
            });
        }).always(function(resultObject) {
            workspace.window.closeMask();
        })

    },

    // 测试解析
    test : function() {
        var _this = this;

        var row = Gikam.getComp('core-file-acq-config-edit-list-grid').getActivedRow();

        if (Gikam.isEmpty(row)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_ONE_ITEM");
            return;
        }

        var fileContentData = Gikam.getComp('core-file-acq-config-edit-list-file-content-form').getData();

        if (Gikam.isEmpty(fileContentData.content)) {
            Gikam.alert("CORE.MODULE.EQUIPT.FILEACQCONFIGS.TIP.UPLOAD_FILE");
            return;
        }

        Gikam.create('modal', {
            title : 'CORE.MODULE.EQUIPT.FILEACQCONFIGS.MODAL.TEST_ANALYSIS',
            url : coreFileAcqConfig.baseUrl + '/page/test-acq-result-list' + Gikam.param({
                id : row.id
            })
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-file-acq-config-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '50%',
                items : [ {
                    type : 'tab',
                    panels : [ {
                        title : 'CORE.MODULE.EQUIPT.FILEACQCONFIGS.TAB.ACQ_CONFIG',
                        items : [ _this.getGridParam() ]
                    }, {
                        title : 'CORE.MODULE.EQUIPT.FILEACQCONFIGS.TAB.FILE',
                        items : [ {
                            type : 'btnGroup',
                            margin : '10px 10px 0px 10px',
                            items : [ {
                                type : 'button',
                                text : 'CORE.MODULE.EQUIPT.FILEACQCONFIGS.BUTTON.UPLOAD',
                                icon : 'upload',
                                onClick : function() {
                                    _this.uploadFile();
                                }
                            }, {
                                type : 'button',
                                text : 'CORE.MODULE.EQUIPT.FILEACQCONFIGS.BUTTON.PREPROCESS',
                                icon : 'activate',
                                onClick : function() {
                                    _this.preprocess();
                                }
                            }, {
                                type : 'button',
                                text : 'CORE.MODULE.EQUIPT.FILEACQCONFIGS.BUTTON.TEST',
                                icon : 'preview',
                                onClick : function() {
                                    _this.test();
                                }
                            } ]
                        }, {
                            type : 'form',
                            id : 'core-file-acq-config-edit-list-file-content-form',
                            fields : coreFileAcqConfig.getFileContentFields(),
                            columns : 1,
                            titleWidth : 0
                        } ]

                    } ]
                } ]
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
