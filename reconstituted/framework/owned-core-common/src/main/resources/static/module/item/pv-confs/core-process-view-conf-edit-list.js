coreProcessViewConf.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-process-view-conf-edit-list-grid',
            url : coreProcessViewConf.baseUrl + '/queries/raw',
            columns : coreProcessViewConf.getEditListColumns(),
            service : 'coreProcessViewConfServiceImpl',
            readonly : true,
            deleteFormatter : function(row) {
                return row.confName || row.id;
            },
            activateFormatter : function(row) {
                return row.confName || row.id;
            },
            deactivateFormatter : function(row) {
                return row.confName || row.id;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-process-view-conf-edit-list-form', 'core-process-view-conf-edit-list-master-grid', 'core-process-view-conf-edit-list-sub-grid' ]);
                }
            },
            onRowActive : function(index, row) {
                _this.onPvConfActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {

                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.ITEM.PVCONFS.TITLE.ADD',
                        fields : coreProcessViewConf.getBaseInfoFormFields(),
                        url : coreProcessViewConf.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('core-process-view-conf-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('core-process-view-conf-edit-list-grid').deleteRows(coreProcessViewConf.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function() {
                    var selections = Gikam.getComp('core-process-view-conf-edit-list-grid').getSelections();
                    if (selections.length != 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    Gikam.getComp('core-process-view-conf-edit-list-grid').activateRows(coreProcessViewConf.baseUrl + '/action/activate');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DEACTIVE',
                icon : 'disable',
                onClick : function() {
                    Gikam.getComp('core-process-view-conf-edit-list-grid').deactivateRows(coreProcessViewConf.baseUrl + '/action/deactivate');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {

                    var selectedRows = Gikam.getComp('core-process-view-conf-edit-list-grid').getSelections();

                    if (selectedRows.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    Gikam.viewAudit('T_CORE_PV_CONF', selectedRows[0].id);

                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.ITEM.PVCONFS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    service : 'coreProcessViewConfServiceImpl',
                    id : 'core-process-view-conf-edit-list-form',
                    columns : 2,
                    fields : coreProcessViewConf.getBaseInfoFormFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('core-process-view-conf-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    }
                } ]
            } ]
        }
    },

    getMasterConfGridParam : function() {
        var _this = this;
        var isMoveFlag = false;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'CORE.MODULE.ITEM.PVCONFS.TAB.MASTER_CONF',
                items : [ {
                    type : 'grid',
                    id : 'core-process-view-conf-edit-list-master-grid',
                    service : 'coreProcessViewMasterConfServiceImpl',
                    columns : coreProcessViewConf.getMasterConfListColumns(),
                    deleteFormatter : function(row) {
                        return row.masterConfName || row.id;
                    },
                    onRowActive : function(index, row) {
                        _this.onMasterConfActive(row);
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            !isMoveFlag && this.activeRowByIndex(0);
                        } else {
                            Gikam.cleanCompData([ 'core-process-view-conf-edit-list-sub-grid' ]);
                        }
                        isMoveFlag = false;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertMasterConfRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteMasterConfRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.MOVE_UP',
                        icon : 'move-up',
                        onClick : function() {
                            isMoveFlag = true;
                            Gikam.getComp('core-process-view-conf-edit-list-master-grid').moveUp(coreProcessViewConf.baseUrl + '/master-confs/action/swap/{firstId}/{secondId}')
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.MOVE_DOWN',
                        icon : 'move-down',
                        onClick : function() {
                            isMoveFlag = true;
                            Gikam.getComp('core-process-view-conf-edit-list-master-grid').moveDown(coreProcessViewConf.baseUrl + '/master-confs/action/swap/{firstId}/{secondId}')
                        }
                    } ]
                } ]
            } ]
        }
    },

    getSubConfGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'CORE.MODULE.ITEM.PVCONFS.TAB.SUB_CONF',
                items : [ {
                    type : 'grid',
                    id : 'core-process-view-conf-edit-list-sub-grid',
                    service : 'coreProcessViewSubConfServiceImpl',
                    columns : coreProcessViewConf.getSubConfListColumns(),
                    deleteFormatter : function(row) {
                        return row.subConfName || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertSubConfRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteSubConfRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.MOVE_UP',
                        icon : 'move-up',
                        onClick : function() {
                            Gikam.getComp('core-process-view-conf-edit-list-sub-grid').moveUp(coreProcessViewConf.baseUrl + '/sub-confs/action/swap/{firstId}/{secondId}')
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.MOVE_DOWN',
                        icon : 'move-down',
                        onClick : function() {
                            Gikam.getComp('core-process-view-conf-edit-list-sub-grid').moveDown(coreProcessViewConf.baseUrl + '/sub-confs/action/swap/{firstId}/{secondId}')
                        }
                    } ]
                } ]
            } ]
        }
    },

    onPvConfActive : function(row) {
        var _this = this;
        Gikam.getAsyncComp('core-process-view-conf-edit-list-form').done(function(grid) {
            grid.refresh({
                url : Gikam.printf(coreProcessViewConf.baseUrl + '/{id}', {
                    id : row.id
                })
            });
        });

        Gikam.getAsyncComp('core-process-view-conf-edit-list-master-grid').done(function(grid) {
            grid.refresh({
                url : _this.getMasterConfUrl(row.id)
            });
        });
    },

    getMasterConfUrl : function(confId) {
        if (confId) {
            return Gikam.printf(coreProcessViewConf.baseUrl + '/{id}/master-confs/queries', {
                id : confId
            })
        }
    },

    onMasterConfActive : function(row) {
        var _this = this;
        Gikam.getAsyncComp('core-process-view-conf-edit-list-sub-grid').done(function(grid) {
            grid.refresh({
                url : _this.getSubConfUrl(row.id)
            });
        });
    },

    getSubConfUrl : function(masterConfId) {
        if (masterConfId) {
            return Gikam.printf(coreProcessViewConf.baseUrl + '/master-confs/{masterConfId}/sub-confs/queries', {
                masterConfId : masterConfId
            })
        }
    },

    insertMasterConfRow : function() {
        var activedRow = Gikam.getComp('core-process-view-conf-edit-list-grid').getActivedRow();
        if (Gikam.isEmpty(activedRow.id)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var url = Gikam.printf(coreProcessViewConf.baseUrl + '/{id}/master-confs', {
            id : activedRow.id
        });
        Gikam.getComp('core-process-view-conf-edit-list-master-grid').insert(url, [ {
            confId : activedRow.id
        } ]);
    },

    deleteMasterConfRow : function() {
        var activedRow = Gikam.getComp('core-process-view-conf-edit-list-grid').getActivedRow();
        var url = Gikam.printf(coreProcessViewConf.baseUrl + '/{id}/master-confs', {
            id : activedRow.id
        });
        Gikam.getComp('core-process-view-conf-edit-list-master-grid').deleteRows(url);
    },

    insertSubConfRow : function() {
        var activedRow = Gikam.getComp('core-process-view-conf-edit-list-master-grid').getActivedRow();
        if (Gikam.isEmpty(activedRow.id)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var url = coreProcessViewConf.baseUrl + '/sub-confs';
        Gikam.getComp('core-process-view-conf-edit-list-sub-grid').insert(url, [ {
            masterConfId : activedRow.id
        } ]);
    },

    deleteSubConfRow : function() {
        var url = coreProcessViewConf.baseUrl + '/sub-confs';
        Gikam.getComp('core-process-view-conf-edit-list-sub-grid').deleteRows(url);
    },

    /**
     * 列表数据配置
     */
    loadGridConfPage : function(index, sourceType) {
        _this = this;
        var grid = sourceType == 'master' ? Gikam.getComp('core-process-view-conf-edit-list-master-grid') : Gikam.getComp('core-process-view-conf-edit-list-sub-grid');
        var row = grid.getData()[index];

        Gikam.create('modal', {
            title : 'CORE.MODULE.ITEM.PVCONFS.TITLE.GRID_CONFIG',
            width : 900,
            height : 650,
            url : coreProcessViewConf.baseUrl + '/page/grid-conf' + Gikam.param({
                sourceId : row.id,
                sourceType : sourceType
            })
        });
    },

    editSql : function(index, serviceName, field) {
        var grid = serviceName == 'coreProcessViewMasterConfServiceImpl' ? Gikam.getComp('core-process-view-conf-edit-list-master-grid') : Gikam.getComp('core-process-view-conf-edit-list-sub-grid');
        var row = grid.getData()[index];
        Gikam.create('modal', {
            title : 'CORE.MODULE.ITEM.PVCONFS.TITLE.EDIT_SQL',
            width : 698,
            height : 322,
            url : coreProcessViewConf.baseUrl + '/page/edit-sql' + Gikam.param({
                id : row.id,
                serviceName : serviceName,
                field : field
            }),
            onAfterClose : function(data) {
                grid.refreshRowById(row.id);
            }
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-process-view-conf-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '390',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    north : {
                        height : 211,
                        items : [ _this.getBaseInfoTab() ]
                    },
                    center : {
                        items : [ _this.getMasterConfGridParam() ]
                    },
                    south : {
                        height : '30%',
                        items : [ _this.getSubConfGridParam() ]
                    }
                } ]
            }
        });
    },

    init : function() {
        this.create();
    }
};