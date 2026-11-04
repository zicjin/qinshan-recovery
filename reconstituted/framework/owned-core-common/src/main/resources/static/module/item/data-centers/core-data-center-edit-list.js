//@ sourceURL=core-data-center-edit-list.js
coreDataCenter.editPage = {

    selectNodeId : null,

    getBtnToolbar : function () {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'CORE.MODULE.ITEM.DATACENTERS.BUTTON.SYNC',
                icon : 'refresh',
                onClick : function () {
                    Gikam.confirm('CORE.MODULE.ITEM.DATACENTERS.TIP.CONFIRM', 'CORE.MODULE.ITEM.DATACENTERS.TIP.SYNC_DATA_CENTER_USER', function () {
                        workspace.window.showMask(true);
                        Gikam.postText(coreDataCenter.baseUrl + '/action/sync-user-data-center').done(function () {
                            Gikam.alert('CORE.MODULE.ITEM.DATACENTERS.TIP.SYNC_SUCCESS');
                        }).always(function () {
                            workspace.window.closeMask();
                        });

                    });

                }
            } ]
        }
    },


    getDataCenterTree : function () {
        var _this = this;
        return {
            type : 'tree',
            id : 'core-data-center-edit-list-tree',
            nodeTextField : 'nodeName',
            url : coreDataCenter.baseUrl + '/tree/root',
            name : 'CORE.MODULE.ITEM.DATACENTERS.TREE.DATACENTER',
            draggable : true,
            onAfterDrop : function (drag, parent, previous) {
                Gikam.put(coreDataCenter.baseUrl + '/action/drag', Gikam.getJsonWrapper({
                    'currentId' : drag.id,
                    'dropParentId' : Gikam.isNotEmpty(parent) ? parent.id : "",
                    'dropPreviousId' : Gikam.isNotEmpty(previous) ? previous.id : ""
                }, []));
            },
            onNodeSelected : function (nodeId, node) {

                _this.selectNodeId = null;

                if (Gikam.isEmpty(nodeId)) {
                    return;
                }

                if (node.nodeType != 'data') {
                    return;
                }

                _this.selectNodeId = nodeId;

                _this.refreshPreviewIframe();

                Gikam.getComp('core-data-center-edit-list-role-grid').refresh({
                    url : Gikam.printf(coreDataCenter.baseUrl + '/{id}/roles/queries', {
                        id : nodeId
                    })
                });

                Gikam.getComp('core-data-center-edit-list-param-grid').refresh({
                    url : Gikam.printf(coreDataCenter.baseUrl + '/{id}/params/queries', {
                        id : nodeId
                    })
                });

            },
            onLoadSuccess : function () {
                var $iconButton = Gikam.jQuery('<span title="同步" class="icon icon-refresh"></span>').css({
                    position : 'absolute',
                    right : '10px'
                });
                $iconButton.appendTo(Gikam.jQuery(this.model.$el).find('.tree-container>.tree-header')).on('click', function () {
                    workspace.window.showMask();
                    Gikam.put(coreDataCenter.baseUrl + '/action/sync').done(function () {
                        Gikam.getComp('core-data-center-edit-list-tree').refresh();
                    }).always(function () {
                        workspace.window.closeMask();
                    })
                })
            }
        }
    },

    getDataCenterTab : function () {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.ITEM.DATACENTERS.TAB.PREVIEW',
                items : [ _this.getPreviewIframe() ]
            }, {
                title : 'CORE.MODULE.ITEM.DATACENTERS.TAB.ROLE',
                items : [ _this.getRoleGridParam() ]
            }, {
                title : 'CORE.MODULE.ITEM.DATACENTERS.TAB.PARAM',
                items : [ _this.getParamGrid() ]
            } ]
        }
    },

    getRoleGridParam : function () {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-data-center-edit-list-role-grid',
            columns : coreDataCenter.getRoleListColumns(),
            service : 'coreDataCenterRolesServiceImpl',
            deleteFormatter : function (row) {
                return row.ext$.rolename || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function () {
                    if (Gikam.isEmpty(_this.selectNodeId)) {
                        Gikam.alert('CORE.MODULE.ITEM.DATACENTERS.TIP.CHOOSE_ONE_DATACENTER');
                        return;
                    }

                    var filterJson = JSON.stringify({
                        type : 'unassigned',
                        targetTable : 'T_CORE_DATA_CENTER_ROLE',
                        targetMatchColumn : 'ROLEID',
                        thisMatchColumn : 'ID',
                        filter : [ {
                            targetFilterColumn : 'DATACENTERID',
                            targetFilterValue : _this.selectNodeId,
                        } ]
                    });

                    Gikam.create('modal', {
                        title : Gikam.choose.getConfig('role').title,
                        url : IFM_CONTEXT + Gikam.choose.getConfig('role').url + Gikam.param({
                            target_filter : encodeURI(filterJson),
                        }),
                        onAfterClose : function (rows) {
                            if (Gikam.isEmpty(rows)) {
                                return;
                            }
                            var chooseData = rows.map(function (item) {
                                return {
                                    roleId : item.id
                                }
                            });
                            Gikam.getComp('core-data-center-edit-list-role-grid').insert(Gikam.printf(coreDataCenter.baseUrl + '/{id}/roles', {
                                id : _this.selectNodeId
                            }), chooseData);
                        },
                    })
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function () {
                    if (Gikam.isEmpty(_this.selectNodeId)) {
                        Gikam.alert('CORE.MODULE.ITEM.DATACENTERS.TIP.CHOOSE_ONE_DATACENTER');
                        return;
                    }

                    Gikam.getComp('core-data-center-edit-list-role-grid').deleteRows(Gikam.printf(coreDataCenter.baseUrl + '/{id}/roles', {
                        id : _this.selectNodeId
                    }));
                }
            } ]
        }
    },

    getPreviewIframe : function () {
        return {
            type : 'iframe',
            id : 'core-data-center-edit-list-preview-iframe',
            src : ''
        }
    },

    getParamGrid : function () {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-data-center-edit-list-param-grid',
            columns : coreDataCenter.getParamListColumns(),
            service : 'CoreDataCenterParamServiceImpl',
            deleteFormatter : function (row) {
                return row.parameterName || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function () {
                    if (Gikam.isEmpty(_this.selectNodeId)) {
                        Gikam.alert('CORE.MODULE.ITEM.DATACENTERS.TIP.CHOOSE_ONE_DATACENTER');
                        return;
                    }

                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.ITEM.DATACENTERS.MODAL.ADD',
                        fields : coreDataCenter.getParamFormFields(),
                        disableNotEmptyValidate : true,
                        url : Gikam.printf(coreDataCenter.baseUrl + '/{id}/params', {
                            id : _this.selectNodeId
                        })
                    }).done(function (id) {
                        Gikam.getComp('core-data-center-edit-list-param-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function () {
                    if (Gikam.isEmpty(_this.selectNodeId)) {
                        Gikam.alert('CORE.MODULE.ITEM.DATACENTERS.TIP.CHOOSE_ONE_DATACENTER');
                        return;
                    }

                    Gikam.getComp('core-data-center-edit-list-param-grid').deleteRows(Gikam.printf(coreDataCenter.baseUrl + '/{id}/params', {
                        id : _this.selectNodeId
                    }));
                }
            } ]
        }
    },

    refreshPreviewIframe : function () {
        var _this = this;

        Gikam.getJson(Gikam.printf(coreDataCenter.baseUrl + '/{id}', {
            id : _this.selectNodeId
        })).done(function (item) {
            Gikam.getComp('core-data-center-edit-list-preview-iframe').refresh({
                src : item.url
            })
        });
    },

    create : function () {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getBtnToolbar(), {
                    type : 'layout',
                    fill : true,
                    west : {
                        width : '275',
                        items : [ this.getDataCenterTree() ]
                    },
                    center : {
                        items : [ this.getDataCenterTab() ]
                    }
                } ]
            }

        });
    },

    init : function () {
        this.create();
    }
};
