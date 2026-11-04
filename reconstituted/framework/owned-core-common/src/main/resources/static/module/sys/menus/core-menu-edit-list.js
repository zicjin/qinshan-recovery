coreMenu.editPage = {
    activeTreeId : null,

    getMenuTree : function() {
        var _this = this;
        return {
            type : 'tree',
            id : 'core-menu-edit-list-tree',
            name : 'CORE.MODULE.SYS.MENUS.TREE.MENU_CONFIG',
            url : IFM_CONTEXT + '/secure/core/module/sys/menus/edit-tree/root',
            async : false,
            baseUrl : coreMenu.baseUrl,
            nodeTextField : 'menuName',
            draggable : true,
            edit : true,
            onAfterDrop : function(drag, parent, previous) {
                Gikam.put(coreMenu.baseUrl + '/action/drag', Gikam.getJsonWrapper({
                    'currentMenuId' : drag.id,
                    'dropParentMenuId' : Gikam.isNotEmpty(parent) ? parent.id : "",
                    'dropPreviousMenuId' : Gikam.isNotEmpty(previous) ? previous.id : ""
                }, [ null, null ]));
            },
            onBeforeInsert : function(nodeId) {
                if (!Gikam.isEmpty(nodeId)) {
                    Gikam.alert('CORE.MODULE.SYS.MENU.EDIT.INSERT_ONLY_ONELEVEL_MENU');
                    return false;
                }
            },
            onBeforeDelete : function(nodeId, parentId) {
                if (!Gikam.isEmpty(parentId)) {
                    Gikam.alert('CORE.MODULE.SYS.MENU.EDIT.DELETE_ONLY_ONELEVEL_MENU');
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
                Gikam.getComp('core-menu-edit-list-tree').selectNodeById(nodeId);
            },
            onAfterDelete : function(node) {
                Gikam.getComp('core-menu-edit-list-form').cleanData();
            }
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.SYS.MENUS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-menu-edit-list-form',
                    fields : coreMenu.getBaseInfoFormFields(),
                    service : 'coreMenuServiceImpl',
                    autoSave : true,
                    instantSavePath : IFM_CONTEXT + '/secure/core/module/sys/menus/instant',
                    onBeforeUpdate : function(data, keys) {
                    },
                    onUpdated : function(node) {
                        if (node.menuName) {
                            Gikam.getComp('core-menu-edit-list-tree').setNodeText(node.id, node.menuName);
                        }
                    }
                } ]
            } ]
        }
    },

    getMenuRoleTab : function() {
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.MDM.USERS.TAB.ROLE', // 访问角色
                items : [ {
                    type : 'grid',
                    id : 'core-menu-edit-list-role-grid',
                    columns : coreMenu.getMenuRoleColumns(),
                    requestData : {
                        permissionTypeId_SEQ : 'T_CORE_MENU',
                    },
                    page : true,
                    deleteFormatter : function(row) {
                        return row.ext$.rolename
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            Gikam.create('modal', {
                                title : 'CORE.MODULE.SYS.ROLES.MODAL.CHOOSE', // 选择角色
                                url : IFM_CONTEXT + '/secure/core/module/sys/roles/page/choose-list' + Gikam.param({
                                    target_filter : encodeURI(JSON.stringify({
                                        type : 'unassigned',
                                        targetTable : 'T_CORE_ROLE_PERMISSION',
                                        targetMatchColumn : 'ROLEID',
                                        thisMatchColumn : 'ID',
                                        filter : [ {
                                            targetFilterColumn : 'TARGETID',
                                            targetFilterValue : Gikam.getComp('core-menu-edit-list-tree').getSelectedNodeId()
                                        }, {
                                            targetFilterColumn : 'PERMISSIONTYPEID',
                                            targetFilterValue : 'T_CORE_MENU'
                                        } ]
                                    }))
                                }),
                                onAfterClose : function(callbackObj) {
                                    if (Gikam.isEmpty(callbackObj)) {
                                        return;
                                    }
                                    var menuId = Gikam.getComp('core-menu-edit-list-tree').getSelectedNodeId();
                                    var rolePermissions = callbackObj.map(function(item) {
                                        return {
                                            roleId : item.id,
                                            targetId : menuId,
                                            permissionTypeId : 'T_CORE_MENU'
                                        }
                                    })

                                    Gikam.getComp('core-menu-edit-list-role-grid').insert(IFM_CONTEXT + '/core/module/sys/role-permissions', rolePermissions);
                                }
                            })
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            Gikam.getComp('core-menu-edit-list-role-grid').deleteRows(IFM_CONTEXT + '/core/module/sys/role-permissions');
                        }
                    } ]
                } ]
            } ]
        }
    },

    onTreeSelected : function(nodeId) {
        var _this = this;
        if (Gikam.isNotEmpty(nodeId)) {
            Gikam.getComp('core-menu-edit-list-form') && Gikam.getComp('core-menu-edit-list-form').cleanData();
            Gikam.getComp('core-menu-edit-list-role-grid') && Gikam.getComp('core-menu-edit-list-role-grid').cleanData()
            Gikam.getJson(coreMenu.baseUrl + '/' + nodeId).done(function(data) {
                var formComp = Gikam.getComp("core-menu-edit-list-form");
                data.menuIconUrl = data.ext$.menuiconurl ? data.ext$.menuiconurl.replace(/blue|black/g, 'white') : null;
                data.todoIconUrl = data.ext$.todoiconurl ? (data.ext$.todoiconurl) : null;
                data.refresh = '1';
                // 判断页面是否刷新下拉框的值。
                if (data.href && Gikam.getRequestParam(data.href).refresh === '0') {
                    data.refresh = '0';
                } else if(!Gikam.isEmpty(data.menuParam)){
                    var menuParamJsObj = JSON.parse(data.menuParam);
                    if(menuParamJsObj.hasOwnProperty('refresh')){
                        data.refresh = menuParamJsObj.refresh;
                    } else {
                        data.refresh = '0';
                    }

                }

                if (formComp) {
                    if (data && data.i18nCode && Gikam.isNotEmpty(data.i18nCode)) {
                        formComp.toFieldsReadonly([ "menuName" ]);
                    } else {
                        formComp.toFieldsEdit([ "menuName" ]);
                    }
                    formComp.loadData(data);
                }
            });
            // 加载选中菜单权限的角色
            Gikam.getComp('core-menu-edit-list-role-grid').refresh({
                url : IFM_CONTEXT + '/core/module/sys/role-permissions/queries',
                requestData : {
                    targetId_EQ : nodeId
                }
            })
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            north : {
                height : 50,
                items : [ {
                    type : 'btnGroup',
                    margin : '10px 10px 10px 10px',
                    items : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.VIEWAUDIT',
                        icon : 'view-audit',
                        onClick : function() {

                            var menuData = Gikam.getComp('core-menu-edit-list-form').getData();

                            if (!menuData.id) {
                                return;
                            }

                            Gikam.viewAudit('T_CORE_MENU', menuData.id);
                        }
                    } ]
                } ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    fill : true,
                    west : {
                        width : 400,
                        items : [ this.getMenuTree() ]
                    },
                    center : {
                        items : [ this.getBaseInfoTab(), this.getMenuRoleTab() ]
                    }
                } ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};