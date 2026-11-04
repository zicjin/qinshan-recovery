coreLesDs.editPage = {

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
                        _this.dsGrid.deleteRows(coreLesDs.baseUrl);
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.ACTIVE',
                    icon : 'enable',
                    onClick : function() {
                        _this.dsGrid.activateRows(coreLesDs.baseUrl + '/action/activate');
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DEACTIVE',
                    icon : 'disable',
                    onClick : function() {
                        _this.dsGrid.deactivateRows(coreLesDs.baseUrl + '/action/deactivate');
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
            id : 'core-les-ds-edit-list-grid',
            url : coreLesDs.baseUrl + '/queries/raw',
            service : 'coreLesDsServiceImpl',
            columns : coreLesDs.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.lesDsName || row.id;
            },
            activateFormatter : function(row) {
                return row.lesDsName || row.id;
            },
            onCellClick : function(field, row) {
                field === 'lesDsName' && _this.loadDetailPage(row.id);
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
                    _this.dsGrid.deleteRows(coreLesDs.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function() {
                    _this.dsGrid.activateRows(coreLesDs.baseUrl + '/action/activate');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DEACTIVE',
                icon : 'disable',
                onClick : function() {
                    _this.dsGrid.deactivateRows(coreLesDs.baseUrl + '/action/deactivate');
                }
            } ]
        }
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreLesDs.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    preInsert : function() {
        var _this = this;
        Gikam.preInsert({
            modalTitle : 'CORE.MODULE.ITEM.LES_DSES.TAB.DS_FIELD_INFO',
            fields : coreLesDs.getBaseInfoFormFields(),
            param : {},
            url : coreLesDs.baseUrl,
            height : 200,
        }).done(function(id) {
            _this.loadDetailPage(id);
        });
    }

};