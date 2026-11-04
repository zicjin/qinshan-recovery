coreHelpDocument.editPage = {
    activeTreeId : null,

    getGridParam : function() {
        var _this = this;
        return {
            type : 'tree',
            id : 'core-help-document-catalog-edit-list-tree',
            name : 'CORE.MODULE.ITEM.HELP_DOCUMENTS.TREE.CATALOG',
            url : coreHelpDocument.baseUrl + '/edit-tree/root',
            service : 'coreHelpDocumentCatalogServiceImpl',
            dbTable : 'T_CORE_HELP_DOCUMENT_CATALOG',
            async : false,
            baseUrl : coreHelpDocument.baseUrl,
            nodeTextField : 'helpDocumentCatalog',
            draggable : true,
            edit : true,
            onAfterDrop : function(drag, parent, previous) {
                Gikam.put(coreHelpDocument.baseUrl + '/action/drag', Gikam.getJsonWrapper({
                    'currentCatalogId' : drag.id,
                    'dropParentCatalogId' : Gikam.isNotEmpty(parent) ? parent.id : "",
                    'dropPreviousCatalogId' : Gikam.isNotEmpty(previous) ? previous.id : ""
                }, [null, null]));
            },
            onBeforeInsert : function(nodeId) {
                if (!Gikam.isEmpty(nodeId)) {
                    Gikam.alert('CORE.MODULE.ITEM.HELP_DOCUMENTS.EDIT.INSERT_ONLY_ONELEVEL_CATALOG');
                    return false;
                }
            },
            onBeforeDelete : function(nodeId, parentId) {
                if (!Gikam.isEmpty(parentId)) {
                    Gikam.alert('CORE.MODULE.ITEM.HELP_DOCUMENTS.EDIT.DELETE_ONLY_ONELEVEL_CATALOG');
                    return false;
                }
            },
            onNodeSelected : function(nodeId) {
                _this.onTreeSelected(nodeId);
                _this.activeTreeId = nodeId;
            },
            onLoadSuccess : function(data) {
                if (data.length > 0) {
                    this.selectNodeById(data[0].id);
                }
            },
            onAfterEdit : function(nodeId) {
                Gikam.getComp('core-help-document-catalog-edit-list-tree').selectNodeById(nodeId);
            },
            onAfterDelete : function(node) {
                Gikam.getComp('core-help-document-edit-list-grid').cleanData();
            }
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [_this.getDocumentGrid()]
        }
    },

    getDocumentGrid : function() {
        var _this = this;
        return {
            title : 'CORE.MODULE.ITEM.HELP_DOCUMENTS.TAB.DOCUMENT',
            items : [{
                type : 'grid',
                id : 'core-help-document-edit-list-grid',
                service : 'coreHelpDocumentServiceImpl',
                dbTable : 'T_CORE_HELP_DOCUMENT',
                deleteFormatter : function(row) {
                    return row.ext$.name || row.id;
                },
                activateFormatter : function(row) {
                    return row.ext$.name || row.id;
                },
                toolbar : [{
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add-row',
                    onClick : function() {
                        var activeTreeId = Gikam.getComp('core-help-document-catalog-edit-list-tree').getSelectedNodeId();
                        if (Gikam.isEmpty(activeTreeId)) {
                            return;
                        }
                        Gikam.getJson(coreHelpDocument.baseUrl + '/document-id').done(function(data) {
                            console.log(data, 'data');
                            Gikam.create('simpleUploader', {
                                id : 'core-help-document-edit-list-uploader',
                                dbTable : 'T_CORE_HELP_DOCUMENT',
                                bizId : data,
                                multiple : false,
                                onUploadSuccess : function() {
                                    var document = {
                                        id : data,
                                        helpDocumentCatalogId : activeTreeId
                                    }
                                    Gikam.post(coreHelpDocument.baseUrl + '/documents', Gikam.getJsonWrapper(null, ['coreHelpDocumentServiceImpl', [document]]))
                                },
                                onAfterClose : function() {
                                    Gikam.getAsyncComp('core-help-document-edit-list-grid').done(function(grid) {
                                        grid.refresh({
                                            url : Gikam.printf(coreHelpDocument.baseUrl + '/{id}/documents/queries', {
                                                id : activeTreeId
                                            })
                                        });
                                    });
                                }
                            });
                        })
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function() {
                        var selectRows = Gikam.getComp('core-help-document-edit-list-grid').getSelections();
                        for (var i = 0; i < selectRows.length; i++) {
                            if (selectRows[i].activatedFlag == '1') {
                                Gikam.alert('GIKAM.TIP.CHOOSE_DEACTIVATED_ITEM');
                                return;
                            }
                        }
                        Gikam.getComp('core-help-document-edit-list-grid').deleteRows(coreHelpDocument.baseUrl + '/documents');
                    }
                }, {
                    type : 'button',
                    text : 'CORE.MODULE.ITEM.HELP_DOCUMENTS.BUTTON.COVER',
                    icon : 'modify',
                    onClick : function() {
                        var _this = this;
                        var selectRows = Gikam.getComp('core-help-document-edit-list-grid').getSelections();
                        console.log(selectRows, 'rows')
                        if (selectRows.length == 0 || selectRows.length > 1) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                            return;
                        }
                        if (selectRows[0].activatedFlag == '1') {
                            Gikam.alert('GIKAM.TIP.CHOOSE_DEACTIVATED_ITEM');
                            return;
                        }
                        Gikam.create('confirm', {
                            message : Gikam.propI18N('CORE.MODULE.ITEM.HELP_DOCUMENTS.TIP.CONFIRM_COVER'),
                            onYesClick : function() {
                                Gikam.create('simpleUploader', {
                                    id : 'core-help-document-edit-list-uploader',
                                    dbTable : 'T_CORE_HELP_DOCUMENT',
                                    bizId : selectRows[0].id,
                                    multiple : false,
                                    onBeforeUpload : function() {
                                        if (selectRows[0].ext$.fileid) {
                                            Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, ['', [{id : selectRows[0].ext$.fileid}]]));
                                        }
                                    },
                                    onAfterClose : function() {
                                        Gikam.getAsyncComp('core-help-document-edit-list-grid').done(function(grid) {
                                            grid.refresh({
                                                url : Gikam.printf(coreHelpDocument.baseUrl + '/{id}/documents/queries', {
                                                    id : selectRows[0].helpDocumentCatalogId
                                                })
                                            });
                                        });
                                    }
                                });
                            }
                        });
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.ACTIVE',
                    icon : 'enable',
                    onClick : function() {
                        Gikam.getComp('core-help-document-edit-list-grid').activateRows(coreHelpDocument.baseUrl + '/documents/action/activate');
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DEACTIVE',
                    icon : 'disable',
                    onClick : function() {
                        Gikam.getComp('core-help-document-edit-list-grid').deactivateRows(coreHelpDocument.baseUrl + '/documents/action/deactivate');
                    }
                }],
                columns : coreHelpDocument.getDocumentListColumns()
            }]
        };
    },


    onTreeSelected : function(nodeId) {
        var _this = this;
        if (Gikam.isNotEmpty(nodeId)) {
            Gikam.getComp('core-help-document-edit-list-grid') && Gikam.getComp('core-help-document-edit-list-grid').cleanData();
            Gikam.getAsyncComp('core-help-document-edit-list-grid').done(function(grid) {
                grid.refresh({
                    url : Gikam.printf(coreHelpDocument.baseUrl + '/{id}/documents/queries', {
                        id : nodeId
                    })
                });
            });
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-help-document-catalog-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 300,
                items : [this.getGridParam()]
            },
            center : {
                items : [this.getBaseInfoTab()]
            }
        });
    },

    init : function() {
        this.create();
    }
};
