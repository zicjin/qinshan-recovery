coreElnDs.editPage = {

    init : function() {
        this.create();
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
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
                    icon : 'add',
                    onClick : function() {
                        _this.preInsert();
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove',
                    onClick : function() {
                        _this.dsGrid.deleteRows(coreElnDs.baseUrl);
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.ACTIVE',
                    icon : 'enable',
                    onClick : function() {
                        _this.dsGrid.activateRows(coreElnDs.baseUrl + '/action/activate');
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DEACTIVE',
                    icon : 'disable',
                    onClick : function() {
                        _this.dsGrid.deactivateRows(coreElnDs.baseUrl + '/action/deactivate');
                    }
                } ]
            } ]
        }
    },

    dsGrid : null,
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-eln-ds-edit-list-grid',
            url : coreElnDs.baseUrl + '/queries/raw',
            service : 'coreElnDsServiceImpl',
            columns : coreElnDs.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.elnDsName || row.id;
            },
            activateFormatter : function(row) {
                return row.elnDsName || row.id;
            },
            onCellClick : function(field, row) {
                field === 'elnDsName' && _this.loadDetailPage(row.id);
            },
            onRendered : function() {
                _this.dsGrid = this;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.preInsert();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    _this.dsGrid.deleteRows(coreElnDs.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function() {
                    _this.dsGrid.activateRows(coreElnDs.baseUrl + '/action/activate');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DEACTIVE',
                icon : 'disable',
                onClick : function() {
                    _this.dsGrid.deactivateRows(coreElnDs.baseUrl + '/action/deactivate');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.EXPORT',
                icon : 'export',
                onClick : function() {
                    var gridComp = Gikam.getComp('core-eln-ds-edit-list-grid');
                    var selectedRows = gridComp.getSelections();

                    Gikam.expdp('T_CORE_ELN_DS', selectedRows);
                }
            } ]
        }
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreElnDs.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    preInsert : function() {
        var _this = this;
        Gikam.preInsert({
            modalTitle : 'CORE.MODULE.ITEM.ELNS.TAB.DS_INSERT',
            fields : coreElnDs.getBaseInfoFormFields(),
            param : {},
            url : coreElnDs.baseUrl,
            height : 240,
        }).done(function(id) {
            _this.loadDetailPage(id);
        });
    }

};
