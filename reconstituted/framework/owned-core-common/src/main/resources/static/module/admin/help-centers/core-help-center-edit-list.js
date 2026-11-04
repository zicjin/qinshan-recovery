coreHelpCenter.editPage = {
    getMenuTree : function() {
        var _this = this;
        return {
            type : 'tree',
            id : 'core-help-center-menu-tree',
            name : 'CORE.MODULE.ADMIN.REQUEST-URL.TREE.MENU',
            url : IFM_CONTEXT + '/secure/core/module/sys/menus/tree/{parentId}',
            onNodeSelected : function(nodeId) {
                _this.onMenuTreeSelected(nodeId);
            }
        }
    },

    getHelpCenterBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'MODULE.ADMIN.HELPCENTERS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    service : 'coreHelpCenterServiceImpl',
                    dbTable : 'T_CORE_HELP_CENTER',
                    id : 'core-help-center-base-info-form',
                    instantSavePath : coreHelpCenter.baseUrl + '/instant',
                    fields : coreHelpCenter.getBaseInfoFormFields(),
                    onBeforeUpdate : function(data, keys) {
                        if (Gikam.isEmpty(data.id)) {
                            return false;
                        }
                    },
                    onLoadSuccess : function(data) {
                        if (Gikam.isNotEmpty(data) && Gikam.isNotEmpty(data.id)) {
                            if (Gikam.isNotEmpty(data.manualEditingMethod) && data.manualEditingMethod === 'online') {
                                Gikam.getComp('core-help-center-base-info-form').showFields([ 'manualContent' ]);
                                Gikam.getComp('core-help-center-base-info-form').hideFields([ 'manualDocFileId' ]);
                            } else if (Gikam.isNotEmpty(data.manualEditingMethod) && data.manualEditingMethod === 'file') {
                                Gikam.getComp('core-help-center-base-info-form').showFields([ 'manualDocFileId' ]);
                                Gikam.getComp('core-help-center-base-info-form').hideFields([ 'manualContent' ]);
                            } else {
                                Gikam.getComp('core-help-center-base-info-form').hideFields([ 'manualDocFileId', 'manualContent' ]);
                            }
                            _this.refreshFileField(data.id);
                        }
                    }
                } ]
            } ]
        }
    },

    onMenuTreeSelected : function(nodeId) {
        var _this = this;
        if (Gikam.isNotEmpty(nodeId)) {

            Gikam.getComp('core-help-center-base-info-form') && Gikam.getComp('core-help-center-base-info-form').cleanData();

            Gikam.getComp('core-help-center-base-info-form') && Gikam.getComp('core-help-center-base-info-form').getField('manualDocFileId').setText('') && Gikam.getComp('core-help-center-base-info-form').getField('manualVideoFileId').setText('');

            var selectedNode = Gikam.getComp('core-help-center-menu-tree').getSelectedNode(nodeId);
            if (Gikam.isEmpty(selectedNode.href)) {
                var vedioField = Gikam.getComp('core-help-center-base-info-form-manual-video-file');
                vedioField.setText('');
                vedioField.setFileId('');
                Gikam.alert('MODULE.ADMIN.HELPCENTERS.TIP.CURRENT_MENU_HAS_NO_RELATED_PAGE');
                return;
            }

            Gikam.getJsonSync(coreHelpCenter.baseUrl + '/' + nodeId).done(function(data) {
                if (Gikam.isEmpty(data.id)) {
                    var vedioField = Gikam.getComp('core-help-center-base-info-form-manual-video-file');
                    vedioField.setText('');
                    vedioField.setFileId('');
                    Gikam.alert('MODULE.ADMIN.HELPCENTERS.TIP.CURRENT_MENU_HAS_NO_MANUAL_CONFIG');
                    return;
                }
                Gikam.getComp('core-help-center-base-info-form').refresh({
                    url : _this.getHelpCenterBaseInfoUrl(data.id)
                }).done(function(data) {
                    _this.refreshFileField(data.id);
                });
            });
        }
    },

    refreshFileField : function(id) {
        Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
            f : {
                targetId : 'T_CORE_HELP_CENTER$' + id,
                bizCategory_SEQ : 'HELPCENTER-VEDIO'
            },
            n : -1,
            s : 50
        })).done(function(r) {
            var vedioField = Gikam.getComp('core-help-center-base-info-form-manual-video-file');
            if (r.rows.length > 0) {
                var vedioFile = r.rows[0];
                vedioField.setText(vedioFile.name);
                vedioField.setFileId(vedioFile.id);
            } else {
                vedioField.setText('');
                vedioField.setFileId('');
            }

            Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
                f : {
                    targetId : 'T_CORE_HELP_CENTER$' + id,
                    bizCategory_SEQ : 'HELPCENTER-DOC'
                },
                n : -1,
                s : 50
            })).done(function(r) {
                var docField = Gikam.getComp('core-help-center-base-info-form-manual-doc-file');
                if (r.rows.length > 0) {
                    var docFile = r.rows[0];
                    docField.setText(docFile.name);
                    docField.setFileId(docFile.id);
                } else {
                    docField.setText('');
                    docField.setFileId('');
                }
            });
        });
    },

    getHelpCenterBaseInfoUrl : function(id) {
        if (id) {
            return Gikam.printf(coreHelpCenter.baseUrl + '/{id}', {
                id : id
            })
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-help-center-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 300,
                items : [ this.getMenuTree() ]
            },
            center : {
                items : [ this.getHelpCenterBaseInfoTab() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
