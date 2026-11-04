coreTopology.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-topology-edit-list-grid',
            url : coreTopology.baseUrl + '/queries/raw',
            columns : coreTopology.getEditListColumns(),
            service : 'coreTopologyServiceImpl',
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ "core-topology-edit-list-base-info-form" ]);
                }
            },
            onRowActive : function(index, row) {
                _this.onTopologyActive(row);
            },
            deleteFormatter : function(row) {
                return row.code || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.TOPOLOGY.INFO.TOPOLOGY.MODAL.ADD',
                        fields : coreTopology.getBaseInfoFormFields(),
                        url : coreTopology.baseUrl,
                        width : 350,
                        height : 250,
                        titleWidth : 70
                    }).done(function(id) {
                        Gikam.getComp('core-topology-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-topology-edit-list-grid').deleteRows(coreTopology.baseUrl).done(function() {
                        _this.cleanTopologySvgInfoTab();
                    });
                    
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.TOPOLOGY.BUTTON.TOPOLOGY_DESIGN',
                icon : 'edit',
                onClick : function() {
                    var grid = Gikam.getComp('core-topology-edit-list-grid');
                    var activeRow = grid.getActivedRow();
                    
                    if (activeRow && activeRow.id) {
                        _this.design(activeRow.id);
                    }
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {

                    var selectedRows = Gikam.getComp('core-topology-edit-list-grid').getSelections();

                    if (selectedRows.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    Gikam.viewAudit('T_CORE_TOPOLOGY', selectedRows[0].id);
           
                }
            } ]
        }
    },
    
    design : function(id) {
        var _this = this;
        
        var requestSvgUrl = coreTopology.baseUrl + '/' + id + '/columns/json';
        
        Gikam.getText(requestSvgUrl).done(function(svg) {
            var modal = Gikam.create('modal', {
                title : 'CORE.MODULE.TOPOLOGY.MODAL.TOPOLOGY_DESIGN_TITLE',
                headerShow : true,
                toolbarTop : true
            });
            
            Gikam.create('diagramDraw', {
                id: 'topology-diagram',
                type: 'diagramDraw',
                renderTo : modal.window.$dom,
                data : svg,
                onSave(data, svgImage) {
                    var window = modal.window;
                    window.showMask();
                    Gikam.put(Gikam.printf(coreTopology.baseUrl + '/{id}', {
                        id : id
                    }), Gikam.getJsonWrapper(null, [ 'coreTopologyServiceImpl', [ {
                        id : id,
                        json : data,
                        svg : svgImage
                    } ] ])).done(function() {
                        Gikam.toast('CORE.MODULE.TOPOLOGY.MODAL.TIP.TOPOLOGY_DESIGN_SAVED');
                        window.closeMask();
                        _this.refreshTopologySvgInfoTab();
                    });
                }
            });
        });
        
    },
    
    onTopologyActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('core-topology-edit-list-base-info-form').refresh({
                url : Gikam.printf(coreTopology.baseUrl + '/{id}', {
                    id : row.id
                }),
            });
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.TOPOLOGY.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    columns : 3,
                    height : 100,
                    id : 'core-topology-edit-list-base-info-form',
                    fields : coreTopology.getBaseInfoFormFields(),
                    service : 'coreTopologyServiceImpl',
                    onUpdated : function() {
                        var grid = Gikam.getComp('core-topology-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                    onLoadSuccess : function(data) {
                        if (data) {
                            _this.refreshTopologySvgInfoTab();
                        }
                    }
                } ]
            } ]
        }
    },
    
    getTopologySvgInfoTab : function() {
        return {
            id : 'core-topology-edit-list-base-info-iframe-tab',
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.TOPOLOGY.TAB.VIWERINFO',
                items : [ {
                    type : 'diagramDraw',
                    id : 'core-topology-edit-list-topology-view',
                    readonly : true
                } ]
            } ]
        }
    },
    
    refreshTopologySvgInfoTab : function() {
        var grid = Gikam.getComp('core-topology-edit-list-grid');
        var requestSvgUrl = coreTopology.baseUrl + '/' + grid.getActivedRow().id + '/columns/json';
        var topology = Gikam.getComp('core-topology-edit-list-topology-view');
        Gikam.getJson(requestSvgUrl).done(function(json) {
            topology.setData(json);
        }).fail(function() {
            topology.setData({});
        });
    },
    
    cleanTopologySvgInfoTab : function() {
        Gikam.cleanCompData([ "core-topology-edit-list-base-info-form" ]);
        Gikam.getComp('core-topology-edit-list-topology-view').setData({});
        
    },
    
    getViewButton : function() {
        var _this = this;
        return {
            type : 'btnGroup',
            margin : '10px 10px 10px 10px',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {

                    var gridRow = Gikam.getComp('core-topology-edit-list-grid').getActivedRow();

                    if (!gridRow.id) {
                        return;
                    }

                    Gikam.viewAudit('T_CORE_TOPOLOGY', gridRow.id);
                }
            } ]
        }
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-topology-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 450,
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    north : {
                        height : 138,
                        items : [
                             _this.getBaseInfoTab()
                        ]
                    },
                    center : {
                        items : [ _this.getTopologySvgInfoTab() ]
                    }
                } ]
            },
        });
    },

    init : function() {
        this.create();
    }
};