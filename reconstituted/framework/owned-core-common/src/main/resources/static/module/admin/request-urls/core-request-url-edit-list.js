coreRequestUrl.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : false,
            service : 'coreRequestUrlServiceImpl',
            id : 'core-request-url-edit-list-grid',
            url : coreRequestUrl.baseUrl + '/queries/raw',
            columns : coreRequestUrl.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.note || row.url;
            },
            requestData : {
                menuId_EQ : 'root'
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
            } ],
            onCellClick : function(field, row) {
            }
        };
    },

    add : function() {
        var _this = this;
        var node = Gikam.getComp('core-request-url-menu-list-tree').getSelectedNode();

        if (Gikam.isEmpty(node)) {
            Gikam.alert('CORE.MODULE.ADMIN.REQUESTURLS.TIP.SELECT_MENU');
            return;
        }

        Gikam.preInsert({
            modalTitle : 'MODULE.ADMIN.REQUEST-URLS.MODAL.ADD',
            fields : coreRequestUrl.getBaseInfoFormFields(),
            url : coreRequestUrl.baseUrl,
            displayReadonly : false,
            param : {
                menuId : node.nodeId
            }
        }).done(function(id) {
            Gikam.getComp('core-request-url-edit-list-grid').refresh();
        });
    },

    remove : function() {
        var _this = this;
        var nodeId = Gikam.getComp('core-request-url-menu-list-tree').getSelectedNodeId();
        Gikam.getComp('core-request-url-edit-list-grid').deleteRows(coreRequestUrl.baseUrl);
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-request-url-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 300,
                items : [ {
                    type : 'tree',
                    id : 'core-request-url-menu-list-tree',
                    name : 'CORE.MODULE.ADMIN.REQUEST-URL.TREE.MENU',
                    url : IFM_CONTEXT + '/secure/core/module/sys/menus/tree/{parentId}',
                    onNodeSelected : function(nodeId) {
                        Gikam.getComp('core-request-url-edit-list-grid').refresh({
                            requestData : {
                                menuId_EQ : Gikam.isEmpty(nodeId) ? 'root' : nodeId
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