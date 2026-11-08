//@ sourceURL=core-pdf-acq-config-edit-list.js
corePdfAcqConfig.editPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-pdf-acq-config-edit-list-grid',
            url : corePdfAcqConfig.baseUrl + '/queries/raw',
            columns : corePdfAcqConfig.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-pdf-acq-config-edit-list-acq-preprocess-grid', ]);
                }
            },
            deleteFormatter : function(row) {
                return row.configName || row.id;
            },
            onCellClick : function(field, row) {
                field === 'configName' && _this.loadDetailPage(row);
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-pdf-acq-config-edit-list-acq-preprocess-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(corePdfAcqConfig.baseUrl + '/{id}/acq-preprocesses/queries', {
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
                        modalTitle : 'CORE.MODULE.EQUIPT.PDFACQCONFIGS.MODAL.ADD',
                        fields : corePdfAcqConfig.getBaseInfoFormFields(),
                        url : corePdfAcqConfig.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('core-pdf-acq-config-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-pdf-acq-config-edit-list-grid').deleteRows(corePdfAcqConfig.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.FILE.BUTTON.UPLOAD_FILE',
                icon : 'upload',
                onClick : function() {
                    _this.uploadFile();
                }
            }, {
                type : 'button',
                text : 'GIKAM.FILE.BUTTON.DELETE_FILE',
                icon : 'file-remove',
                onClick : function() {
                    _this.deleteFile();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.EQUIPT.PDFACQCONFIGS.BUTTON.TEST',
                icon : 'preview',
                onClick : function() {
                    _this.test();
                }
            } ]
        }
    },

    test : function() {

        var _this = this;

        var selections = Gikam.getComp('core-pdf-acq-config-edit-list-grid').getSelections();

        if (Gikam.isEmpty(selections)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_ONE_ITEM");
            return;
        }

        var row = selections[0];

        if (Gikam.isEmpty(row.ext$.fileid)) {
            Gikam.alert("CORE.MODULE.EQUIPT.PDFACQCONFIGS.TIP.UPLOAD_FILE");
            return;
        }

        Gikam.create('simpleUploader', {
            id : 'core-pdf-acq-config-edit-list-uploader',
            dbTable : 'T_CORE_PDF_ACQ_CONFIG',
            bizId : 'PDFACQTEST',
            accept : 'pdf',
            multiple : false,
            onAfterClose : function(fileList) {

                if (Gikam.isEmpty(fileList)) {
                    return;
                }

                workspace.window.showMask(true);

                Gikam.post(Gikam.printf(corePdfAcqConfig.baseUrl + '/{id}/files/{fileId}/action/test', {
                    id : row.id,
                    fileId : fileList[0].id
                })).done(function(data) {

                    workspace.window.closeMask();

                    var modal = Gikam.create('modal', {
                        title : fileList[0].name,
                        width : 900,
                        height : 650,
                        onAfterClose : function(data) {
                            Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', [ {
                                id : fileList[0].id
                            } ] ]))
                        }
                    });

                    Gikam.create('layout', {
                        renderTo : modal.window.$dom,
                        center : {
                            items : [ {
                                id : 'core-pdf-acq-config-edit-list-test-result-grid',
                                type : 'grid',
                                columns : corePdfAcqConfig.getTestResultColumns(),
                                data : data
                            } ]
                        }
                    });
                });
            }
        });
    },

    deleteFile : function() {
        var _this = this;

        var grid = Gikam.getComp('core-pdf-acq-config-edit-list-grid');

        var selections = grid.getSelections();

        if (selections.length !== 1) {
            Gikam.alert("GIKAM.TIP.CHOOSE_ONE_ITEM");
            return;
        }

        var row = selections[0];

        if (Gikam.isEmpty(row.ext$.fileid)) {
            Gikam.alert("CORE.MODULE.EQUIPT.PDFACQCONFIGS.TIP.UPLOAD_FILE");
            return;
        }

        Gikam.confirm('CORE.MODULE.EQUIPT.PDFACQCONFIGS.TIP.DELETE_FILE', Gikam.propI18N('CORE.MODULE.EQUIPT.PDFACQCONFIGS.TIP.CONFIRM_DELETE_FILE') + row.ext$.filename, function() {

            Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', [ {
                id : row.ext$.fileid
            } ] ])).done(function() {

                grid.refresh();

                Gikam.del(Gikam.printf(corePdfAcqConfig.baseUrl + '/{id}/lines', {
                    id : row.id
                }));

            });
        });

    },

    uploadFile : function() {
        var _this = this;

        var selections = Gikam.getComp('core-pdf-acq-config-edit-list-grid').getSelections();

        if (Gikam.isEmpty(selections)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_ONE_ITEM");
            return;
        }

        var row = selections[0];

        if (!Gikam.isEmpty(row.ext$.fileid)) {
            Gikam.alert("CORE.MODULE.EQUIPT.PDFACQCONFIGS.TIP.PLEASE_DELETE_FILE");
            return;
        }

        Gikam.create('simpleUploader', {
            id : 'core-pdf-acq-config-edit-list-uploader',
            dbTable : 'T_CORE_PDF_ACQ_CONFIG',
            bizId : row.id,
            multiple : false,
            onAfterClose : function(files) {
                if (!Gikam.isEmpty(files)) {
                    row.ext$.fileid = files[0].id;

                    Gikam.getComp('core-pdf-acq-config-edit-list-grid').refreshRowById(row.id);

                    workspace.window.showMask(true);
                    Gikam.postText(Gikam.printf(corePdfAcqConfig.baseUrl + '/{id}/lines', {
                        id : row.id
                    })).done(function() {
                        _this.loadDetailPage(row);
                    });

                }
            }
        });
    },

    loadDetailPage : function(row) {

        if (Gikam.isEmpty(row.ext$.fileid)) {
            Gikam.alert("CORE.MODULE.EQUIPT.PDFACQCONFIGS.TIP.UPLOAD_FILE");
            return;
        }

        var modal = Gikam.create('modal', {
            title : row.configName,
            height : 580
        });

        workspace.window.closeMask();

        Gikam.create('pdfCollection', {
            id : row.id,
            renderTo : modal.window.$dom,
            testHandle : function() {

                modal.window.showMask(true);

                corePdfAcqConfig.acqModal = modal;

                Gikam.post(Gikam.printf(corePdfAcqConfig.baseUrl + '/{id}/files/{fileId}/action/test', {
                    id : row.id,
                    fileId : row.ext$.fileid
                })).done(function(data) {

                    corePdfAcqConfig.acqModal.window.closeMask();

                    var modal = Gikam.create('modal', {
                        title : 'CORE.MODULE.EQUIPT.PDFACQCONFIGS.MODAL.TEST_RESULT',
                        width : 900,
                        height : 650
                    });

                    Gikam.create('layout', {
                        renderTo : modal.window.$dom,
                        center : {
                            items : [ {
                                id : 'core-pdf-acq-config-edit-list-test-result-grid',
                                type : 'grid',
                                columns : corePdfAcqConfig.getTestResultColumns(),
                                data : data
                            } ]
                        }
                    });
                });

            }
        });
    },

    //--------------------------------------

    getPdfAcqPreprocessGrid : function() {
        var _this = this;
        return {
            title : 'CORE.MODULE.EQUIPT.FILEACQCONFIGS.TAB.ACQ_PREPROCESS',
            items : [ {
                type : 'grid',
                id : 'core-pdf-acq-config-edit-list-acq-preprocess-grid',
                service : 'corePdfAcqPreprocessServiceImpl',
                deleteFormatter : function(row) {
                    return row.keyword || row.id;
                },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add-row',
                    onClick : function() {
                        var parents = Gikam.getComp('core-pdf-acq-config-edit-list-grid').getSelections();
                        if (parents.length !== 1) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                            return;
                        }
                        Gikam.preInsert({
                            modalTitle : 'CORE.MODULE.EQUIPT.FILEACQCONFIGS.MODAL.ADD',
                            fields : corePdfAcqConfig.getPdfAcqPreprocessFields(),
                            url : Gikam.printf(corePdfAcqConfig.baseUrl + '/{id}/acq-preprocesses', {
                                id : parents[0].id
                            })
                        }).done(function(id) {
                            Gikam.getComp('core-pdf-acq-config-edit-list-acq-preprocess-grid').refresh();
                        });

                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function() {
                        Gikam.getComp('core-pdf-acq-config-edit-list-acq-preprocess-grid').deleteRows(Gikam.printf(corePdfAcqConfig.baseUrl + '/{id}/acq-preprocesses', {
                            id : Gikam.getComp('core-pdf-acq-config-edit-list-grid').getSelections()[0].id
                        }));
                    }
                } ],
                columns : corePdfAcqConfig.getAcqPreprocessListColumns()
            } ]
        };
    },

    getSouthTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ _this.getPdfAcqPreprocessGrid() ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-pdf-acq-config-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '50%',
                items : [{
                    type : 'tab',
                    panels : [{
                        title : 'PDF采集配置',
                        items : [_this.getGridParam()]
                    }],
                }]
            },
            center : {
                items : [ this.getSouthTab() ]
            }
        });
    },

    init : function() {
        this.create();
    }
}