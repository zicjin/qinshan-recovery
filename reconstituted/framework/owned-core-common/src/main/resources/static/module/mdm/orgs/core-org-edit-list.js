coreOrg.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-org-edit-list-grid',
            url : coreOrg.baseUrl + '/queries/raw',
            columns : coreOrg.getEditListColumns(),
            requestData : {
                parentId : 'root'
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add-row',
                onClick : function() {
                    _this.add();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    _this.remove();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.MDM.ORGS.BUTTON.MOVE_UP',
                icon : 'move-up',
                onClick : function() {
                    _this.moveUp();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.MDM.ORGS.BUTTON.MOVE_DOWN',
                icon : 'move-down',
                onClick : function() {
                    _this.moveDown();
                }
            } ],
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
            }
        };
    },

    getBtnToolbarParam : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'btnGroup',
                items : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add-row',
                    onClick : function() {
                        _this.add();
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function() {
                        _this.remove();
                    }
                } ]
            } ]
        }
    },

    add : function() {
        var _this = this;
        var node = Gikam.getComp('core-org-edit-list-tree').getSelectedNode();
        var parentId = node ? node.nodeId : '';
        var parentName = node ? node.nodeText : Gikam.propI18N('CORE.MODULE.MDM.ORGS.TREE.ORGANIZATION');
        Gikam.preInsert({
            modalTitle : 'CORE.MODULE.MDM.ORGS.MODAL.ADD',
            fields : coreOrg.getBaseInfoFormFields().concat([ {
                field : 'parentName',
                title : 'T_CORE_ORG.PARENTNAME',
                value : parentName,
                preInsert : true,
                readonly : true
            } ]),
            url : coreOrg.baseUrl,
            displayReadonly : false,
            param : {
                parentId : parentId
            }
        }).done(function(id) {
            Gikam.getComp('core-org-edit-list-grid').refresh();
            _this.refreshTree(parentId);
        });
    },

    remove : function() {
        var _this = this;
        var nodeId = Gikam.getComp('core-org-edit-list-tree').getSelectedNodeId();
        Gikam.getComp('core-org-edit-list-grid').deleteRows(coreOrg.baseUrl).done(function() {
            _this.refreshTree(nodeId);
        });
    },

    refreshTree : function(parentId) {
        Gikam.getComp('core-org-edit-list-tree').refreshNode(parentId);
    },

    loadDetailPage : function(id) {
        var _this = this;

        Gikam.create('modal', {
            title : 'CORE.MODULE.MDM.ORGS.MODAL.ORG',
            url : coreOrg.baseUrl + '/page/detail' + Gikam.param({
                id : id
            }),
            onAfterClose : function() {
                Gikam.getComp('core-org-edit-list-grid').refresh();

                var tree = Gikam.getComp('core-org-edit-list-tree');
                var node = tree.getSelectedNode();
                var parentId = node ? node.nodeId : '';

                tree.refreshNode(parentId);
            }
        });

    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-org-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 200,
                items : [ {
                    type : 'tree',
                    id : 'core-org-edit-list-tree',
                    name : 'CORE.MODULE.MDM.ORGS.TREE.ORGANIZATION',
                    url : coreOrg.baseUrl + '/tree/{parentId}',
                    onNodeSelected : function(nodeId) {

                        Gikam.getComp('core-org-edit-list-grid').refresh({
                            requestData : {
                                parentId : Gikam.isEmpty(nodeId) ? 'root' : nodeId
                            }
                        });
                    }
                } ]
            },
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },
    init : function() {
        this.create();
    },

    moveUp : function() {
        var selections = Gikam.getComp('core-org-edit-list-grid').getSelections();
        if (selections.length === 1) {
            Gikam.getComp('core-org-edit-list-grid').moveUp(coreOrg.baseUrl + '/action/swap/{firstId}/{secondId}').done(function() {
                Gikam.getComp('core-org-edit-list-tree').refreshNode(selections[0].parentId);
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }

    },

    moveDown : function() {
        var selections = Gikam.getComp('core-org-edit-list-grid').getSelections();
        if (selections.length === 1) {
            Gikam.getComp('core-org-edit-list-grid').moveDown(coreOrg.baseUrl + '/action/swap/{firstId}/{secondId}').done(function() {
                Gikam.getComp('core-org-edit-list-tree').refreshNode(selections[0].parentId);
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },
};