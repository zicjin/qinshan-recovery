emcArea.editPage = {

    getAreaTree : function() {
        var _this = this;
        return {
            type : 'tree',
            id : 'emc-area-edit-list-area-tree',
            nodeTextField : 'areaName',
            name : 'EMC.MODULE.MDM.BASEMDM.AREAS.TREE.AREA',
            url : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/areas/tree/{parentId}',
            onNodeSelected : function(nodeId) {
                _this.onTreeSelected(nodeId);
            },
        }
    },

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-area-edit-list-grid',
            url : emcArea.baseUrl + '/queries/raw',
            service : 'emcAreaServiceImpl',
            requestData : {
                parentId : '-1',
            },
            columns : emcArea.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.areaName || row.id;
            },
            activateFormatter : function(row) {
                return row.areaName || row.id;
            },
            deactivateFormatter : function(row) {
                return row.areaName || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    var tree = Gikam.getComp('emc-area-edit-list-area-tree');
                    var treeId = tree.getSelectedNodeId();
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.BASEMDM.AREAS.MODAL.AREA_ADD',
                        fields : emcArea.getPreInsertFormFields(),
                        param : {
                            parentId : treeId
                        },
                        url : emcArea.baseUrl
                    }).done(function(id) {
                        if (Gikam.isEmpty(treeId)) {
                            tree.refresh();
                        } else {
                            tree.refreshNode(treeId)
                        }
                        Gikam.getComp('emc-area-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-area-edit-list-grid').deleteRows(emcArea.baseUrl).done(function() {
                        var tree = Gikam.getComp('emc-area-edit-list-area-tree');
                        var treeId = tree.getSelectedNodeId();

                        if (Gikam.isEmpty(treeId)) {
                            tree.refresh();
                        } else {
                            tree.refreshNode(treeId)
                        }
                    });
                }
            } ]
        }
    },

    onTreeSelected : function(nodeId) {
        if (nodeId != 'root') {
            Gikam.getComp('emc-area-edit-list-grid').refresh({
                url : emcArea.baseUrl + '/queries/raw',
                requestData : {
                    parentId : nodeId,
                }
            });
        } else {
            Gikam.getComp('emc-area-edit-list-grid').cleanData();
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            west : {
                width : 240,
                items : [ _this.getAreaTree() ]
            },
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
