coreOfficeDs.editPage = {

    init : function() {
        this.create();
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
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
                        _this.dsGrid.deleteRows(coreOfficeDs.baseUrl);
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.ACTIVE',
                    icon : 'enable',
                    onClick : function() {
                        _this.dsGrid.activateRows(coreOfficeDs.baseUrl + '/action/activate');
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DEACTIVE',
                    icon : 'disable',
                    onClick : function() {
                        _this.dsGrid.deactivateRows(coreOfficeDs.baseUrl + '/action/deactivate');
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
            id : 'core-office-ds-edit-list-grid',
            url : coreOfficeDs.baseUrl + '/queries/raw',
            service : 'coreOfficeDsServiceImpl',
            columns : coreOfficeDs.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.dsName || row.id;
            },
            activateFormatter : function(row) {
                return row.dsName || row.id;
            },
            onCellClick : function(field, row) {
                field === 'dsName' && _this.loadDetailPage(row.id);
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
                    _this.dsGrid.deleteRows(coreOfficeDs.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function() {
                    _this.dsGrid.activateRows(coreOfficeDs.baseUrl + '/action/activate');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DEACTIVE',
                icon : 'disable',
                onClick : function() {
                    _this.dsGrid.deactivateRows(coreOfficeDs.baseUrl + '/action/deactivate');
                }
            } ]
        }
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreOfficeDs.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    preInsert : function() {
        var _this = this;
        Gikam.preInsert({
            modalTitle : 'CORE.MODULE.OFFICE.DSES.TAB.DS_FIELD_INFO',
            fields : coreOfficeDs.getBaseInfoFormFields(),
            param : {},
            url : coreOfficeDs.baseUrl,
            height : 200,
        }).done(function(id) {
            _this.loadDetailPage(id);
        });
    }

};