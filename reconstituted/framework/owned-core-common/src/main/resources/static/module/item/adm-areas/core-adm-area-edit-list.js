coreAdmArea.editPage = {

    getGridParam : function() {
        var _this = this;

        return {
            type : 'grid',
            id : 'core-adm-area-edit-list-grid',
            url : coreAdmArea.baseUrl + '/queries/raw',
            requestData : {
                parentId : 'root'
            },
            service : 'coreAdmAreaServiceImpl',
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
            } ],
            onUpdated : function(data, fields) {
                if (fields.indexOf('admAreaName') > -1) {
                    var node = Gikam.getComp('core-adm-area-edit-list-tree').getSelectedNode();
                    var parentId = node ? node.nodeId : '';
                    _this.refreshTree(parentId);
                }
            },
            columns : coreAdmArea.getEditListColumns()
        }
    },

    add : function() {
        var _this = this;

        var node = Gikam.getComp('core-adm-area-edit-list-tree').getSelectedNode();

        var parentId = node ? node.nodeId : '';
        var parentName = node ? node.nodeText : Gikam.propI18N('T_CORE_ADM_AREA.SERVICE_NAME');

        Gikam.preInsert({
            modalTitle : 'CORE.MODULE.ITEM.ADMAREAS.MODAL.ADD',
            fields : coreAdmArea.getBaseInfoFormFields().concat([ {
                field : 'parentName',
                title : 'T_CORE_ADM_AREA.PARENTNAME',
                value : parentName,
                preInsert : true,
                readonly : true
            } ]),
            url : coreAdmArea.baseUrl,
            displayReadonly : false,
            param : {
                parentId : parentId
            }
        }).done(function(id) {
            Gikam.getComp('core-adm-area-edit-list-grid').refresh();
            _this.refreshTree(parentId);
        });
    },

    remove : function() {
        var _this = this;

        var nodeId = Gikam.getComp('core-adm-area-edit-list-tree').getSelectedNodeId();
        Gikam.getComp('core-adm-area-edit-list-grid').deleteRows(coreAdmArea.baseUrl).done(function() {
            _this.refreshTree(nodeId);
        });

    },

    refreshTree : function(parentId) {
        Gikam.getComp('core-adm-area-edit-list-tree').refreshNode(parentId);
    },

    create : function() {

        var _this = this;

        Gikam.create('layout', {
            id : 'core-adm-area-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 200,
                items : [ {
                    type : 'tree',
                    id : 'core-adm-area-edit-list-tree',
                    name : 'CORE.MODULE.ITEM.ADMAREAS.TREE.ADMAREA',
                    url : coreAdmArea.baseUrl + '/tree/{parentId}',
                    onNodeSelected : function(nodeId) {
                        Gikam.getComp('core-adm-area-edit-list-grid').refresh({
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
    }
};