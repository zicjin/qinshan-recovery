coreMenu.editChoosePage = {
    activeTreeId : null,

    getMenuTree : function () {
        var _this = this;
        return {
            type : 'tree',
            id : 'core-menu-edit-choose-list-tree-tree',
            name : 'CORE.MODULE.SYS.MENUS.TREE.MENU_CONFIG',
            url : IFM_CONTEXT + '/secure/core/module/sys/menus/edit-tree/root',
            async : false,
            baseUrl : coreMenu.baseUrl,
            nodeTextField : 'menuName',
            edit : true,
            onBeforeInsert : function (nodeId) {
                if (!Gikam.isEmpty(nodeId)) {
                    Gikam.alert('CORE.MODULE.SYS.MENU.EDIT.INSERT_ONLY_ONELEVEL_MENU');
                    return false;
                }
            },
            onBeforeNodeEditorRender : function (node) {
                node.remove = false;
                node.edit = false;
                return node;
            },
            onNodeSelected : function (nodeId, node) {
                _this.onTreeSelected(nodeId,node);
                _this.activeTreeId = nodeId;
            }
        }
    },


    onTreeSelected : function (nodeId,node) {
        if(!node.editable && nodeId){
            Gikam.getJson(coreMenu.baseUrl + '/' + nodeId).done(function(data) {
                Gikam.getLastModal().close(data);
            });
        }
    },

    create : function () {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ {
                    type : 'layout',
                    fill : true,
                    center : {
                        items : [ this.getMenuTree() ]
                    }
                } ]
            }
        });
    },

    init : function (param) {
        this.param = param;
        this.create();
    }
};