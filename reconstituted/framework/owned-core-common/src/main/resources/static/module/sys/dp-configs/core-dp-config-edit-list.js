//@ sourceURL=core-dp-config-edit-list.js
coreDpConfig.editPage = {

    activeTreeId : null,

    getTreeParam : function() {
        var _this = this;
        return {
            type : 'tree',
            id : 'core-dp-config-edit-list-tree',
            name : 'CORE.MODULE.SYS.DPCONFIGS.TREE.DPCONFIG',
            url : coreDpConfig.baseUrl + '/tree/root',
            async : false,
            onNodeSelected : function(nodeId) {
                _this.onTreeSelected(nodeId);
                _this.activeTreeId = nodeId;
            },
            baseUrl : coreDpConfig.baseUrl,
            onLoadSuccess : function(data) {
                if (data.length > 0) {
                    this.selectNodeById(data[0].id);
                }
            }
        }
    },

    onTreeSelected : function(nodeId) {
        var _this = this;
        if (Gikam.isNotEmpty(nodeId)) {
            Gikam.getComp('core-dp-config-edit-list-base-info-form').refresh({
                url : Gikam.printf(coreDpConfig.baseUrl + '/{id}', {
                    id : nodeId
                })
            });

            Gikam.getComp('core-dp-config-edit-list-line-grid').refresh({
                url : Gikam.printf(coreDpConfig.baseUrl + '/{id}/lines/queries', {
                    id : nodeId
                })
            });

        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.SYS.DPCONFIGS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    service : 'coreDpConfigServiceImpl',
                    id : 'core-dp-config-edit-list-base-info-form',
                    fields : coreDpConfig.getBaseInfoFormFields(),
                    onBeforeUpdate : function(data) {
                        if (this.validate()) {
                            return data;
                        }

                        return false;
                    },
                } ]
            } ]
        }
    },

    getConfigLineGrid : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.SYS.DPCONFIGS.TAB.CONFIG_LINE',
                items : [ {
                    type : 'grid',
                    id : 'core-dp-config-edit-list-line-grid',
                    service : 'coreDpConfigLineServiceImpl',
                    deleteFormatter : function(row) {
                        return row.relateTableName || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            Gikam.getComp('core-dp-config-edit-list-line-grid').insert(Gikam.printf(coreDpConfig.baseUrl + '/{id}/lines', {
                                id : _this.activeTreeId
                            }), [ {} ]);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            Gikam.getComp('core-dp-config-edit-list-line-grid').deleteRows(Gikam.printf(coreDpConfig.baseUrl + '/{id}/lines', {
                                id : _this.activeTreeId
                            }));
                        }
                    } ],
                    columns : coreDpConfig.getLineListColumns()
                } ]
            } ]
        };
    },

    insertDpConfig : function() {
        var _this = this;

        var parentId = _this.activeTreeId ? _this.activeTreeId : '';
        Gikam.preInsert({
            modalTitle : 'CORE.MODULE.SYS.DPCONFIGS.MODAL.ADD',
            fields : coreDpConfig.getBaseInfoFormFields(),
            url : coreDpConfig.baseUrl,
            titleWidth : 120,
            param : {
                parentId : parentId
            }
        }).done(function(id) {
            Gikam.getComp('core-dp-config-edit-list-tree').refresh();
        });

    },

    deleteDpConfig : function() {

        var _this = this;

        var tree = Gikam.getComp('core-dp-config-edit-list-tree');

        var treeNode = tree.getSelectedNode();

        if (Gikam.isEmpty(treeNode)) {
            Gikam.alert('CORE.MODULE.SYS.DPCONFIGS.TIP.SELECT_TREE_NODE');
            return;
        }

        if (treeNode.childQty > 0) {
            Gikam.alert('GIKAM.TIP.TREE.CHILDREN-EXISTS');
            return;
        }

        Gikam.create('confirm', {
            title : Gikam.propI18N('GIKAM.TIP.CONFIRM_TO_DELETE'),
            message : Gikam.propI18N('GIKAM.TIP.CONFIRM_TO_DELETE') + treeNode.nodeText,
            height : 250,
            onYesClick : function() {
                Gikam.del(coreDpConfig.baseUrl, Gikam.getJsonWrapper({}, [ '', [ {
                    id : treeNode.nodeId
                } ] ])).done(function() {
                    tree.refresh();
                });
            }
        });

    },

    import : function() {
        var _this = this;

        Gikam.create('simpleUploader', {
            multiple : false,
            dbTable : 'T_CORE_DP_CONFIG',
            onBeforeFileUpload : function(file) {
                if (file.file.type === 'text/plain') {
                    return true;
                }

                Gikam.alert('CORE.MODULE.SYS.DPCONFIGS.TIP.ONLY_TXT');

                return false;
            },
            onAfterClose : function(files) {
                if (Gikam.isEmpty(files)) {
                    return;
                }

                if (files[0].file.type !== 'text/plain') {
                    return;
                }

                var fileId = files[0].id;

                workspace.window.showMask(true);
                Gikam.post(Gikam.printf(coreDpConfig.baseUrl + '/files/{fileId}/action/import-validate', {
                    fileId : fileId
                })).done(function(result) {

                    if (!Gikam.isEmpty(result)) {
                        _this.loadImportValidateModal(result, fileId);
                    } else {
                        _this.importData(fileId, {});
                    }

                }).always(function() {
                    workspace.window.closeMask();
                });
            }

        });

    },

    importData : function(fileId, importTableData) {

        workspace.window.showMask(true);
        Gikam.post(Gikam.printf(coreDpConfig.baseUrl + '/files/{fileId}/action/import', {
            fileId : fileId
        }), Gikam.getJsonWrapper(importTableData, [])).done(function(result) {

            Gikam.alert('CORE.MODULE.SYS.DPCONFIGS.TIP.IMPORT_SUCCESS');
        }).always(function() {
            workspace.window.closeMask();
        });

    },

    loadImportValidateModal : function(data, fileId) {

        var _this = this;
        var modal = Gikam.create('modal', {
            title : 'CORE.MODULE.SYS.DPCONFIGS.MODAL.IMPORT_DATA_REPEAT',
            width : 490,
            height : 380
        });

        Gikam.create('layout', {
            renderTo : modal.window.$dom,
            center : {
                items : [ {
                    type : 'grid',
                    id : 'core-dp-config-edit-list-import-validate-grid',
                    page : false,
                    toolbarHidden : false,
                    columns : coreDpConfig.getImportValidateColumns(),
                    onRendered : function() {
                        this.loadData(data);
                    }
                }, {
                    type : 'btnToolbar',
                    items : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
                        icon : 'cancel',
                        onClick : function() {
                            Gikam.getLastModal().close();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.IMPORT',
                        icon : 'move-left',
                        onClick : function() {

                            Gikam.getLastModal().close();

                            var importTableValidateData = Gikam.getComp('core-dp-config-edit-list-import-validate-grid').getData();

                            var importTableData = {};

                            Gikam.each(importTableValidateData, function(index, rowData) {
                                importTableData[rowData.tableName] = rowData.operation;
                            });

                            _this.importData(fileId, importTableData);

                        }
                    } ]
                } ]
            }
        });

    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-dp-config-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : 45,
                items : [ {
                    type : 'btnGroup',
                    margin : '10px 10px 10px 10px',
                    items : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            _this.insertDpConfig();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteDpConfig();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.IMPORT',
                        icon : 'move-left',
                        onClick : function() {
                            _this.import();
                        }

                    } ]
                } ]
            },
            west : {
                width : 300,
                items : [ this.getTreeParam() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    north : {
                        height : 142,
                        items : [ this.getBaseInfoTab() ]
                    },
                    center : {
                        items : [ this.getConfigLineGrid() ]
                    }
                } ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
