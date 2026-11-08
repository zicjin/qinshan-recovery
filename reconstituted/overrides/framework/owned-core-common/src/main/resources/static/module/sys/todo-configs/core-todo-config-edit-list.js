coreTodoConfig.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-todo-config-edit-list-grid',
            url : coreTodoConfig.baseUrl + '/queries/raw',
            columns : coreTodoConfig.getEditListColumns(),
            service : 'coreTodoConfigServiceImpl',
            activateFormatter : function(row) {
                return row.name;
            },
            deleteFormatter : function(row) {
                return row.name;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getAsyncComp('core-todo-config-edit-list-base-info-form').done(function(form) {
                        form.cleanData().done(function() {
                            form.setData({
                                category : 'business',
                                inheritParam : '1'
                            }, false)
                        });
                    });
                }
            },
            onRowActive : function(index, row) {
                _this.onConfigActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.SYS.TODO_CONFIGS.MODAL.ADD',
                        height : 250,
                        fields : coreTodoConfig.getBaseInfoFormFields(),
                        url : coreTodoConfig.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('core-todo-config-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-todo-config-edit-list-grid').deleteRows(coreTodoConfig.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function() {
                    Gikam.getComp('core-todo-config-edit-list-grid').activateRows(coreTodoConfig.baseUrl + "/action/activate");
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DEACTIVE',
                icon : 'disable',
                onClick : function() {
                    Gikam.getComp('core-todo-config-edit-list-grid').deactivateRows(coreTodoConfig.baseUrl + "/action/deactivate");
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {

                    var selectedRows = Gikam.getComp('core-todo-config-edit-list-grid').getSelections();

                    if (selectedRows.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    Gikam.viewAudit('T_CORE_TODO_CONFIG', selectedRows[0].id);
                }
            } ]
        }
    },

    submit : function() {
        var grid = Gikam.getComp('emc-project-edit-list-grid');
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : emcProject
        }).done(function() {
            grid.refresh();
        });
    },

    onConfigActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('core-todo-config-edit-list-base-info-form').refresh({
                url : Gikam.printf(coreTodoConfig.baseUrl + '/{id}', {
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
                title : 'CORE.MODULE.SYS.TODO_CONFIGS.MODAL.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    columns : 3,
                    id : 'core-todo-config-edit-list-base-info-form',
                    fields : coreTodoConfig.getBaseInfoFormFields(),
                    service : 'coreTodoConfigServiceImpl',
                    autoSave : true,
                    onUpdated : function(updatedData) {
                        var grid = Gikam.getComp('core-todo-config-edit-list-grid');
                        var row = grid.getActivedRow();
                        if (row && updatedData && row.id === updatedData.id) {
                            grid.refreshRowById(updatedData.id);
                        }
                    }
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-todo-config-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 460,
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getBaseInfoTab() ]
            },
        });
    },

    init : function() {
        this.create();
    },

    todoIconsList : [],
    getShowImgPanel : function(field) {
        var _this = this;
        var listObj = {
            todoIconUrl : this.todoIconsList
        }
        var urlObj = {
            todoIconUrl : coreTodoConfig.baseUrl + '/icons/queries'
        }
        if (listObj[field].length === 0) {
            Gikam.getJson(urlObj[field]).done(function(data) {
                data.forEach(function(item) {
                    item.id = item.iconName;
                    item.src = IFM_CONTEXT + item.iconUrl;
                });
                _this.todoIconsList = data;
                _this.imgPanelHandle(field);
            });
        } else {
            _this.imgPanelHandle(field);
        }
    },
    imgPanelHandle : function(field) {
        var formData = Gikam.getComp('core-todo-config-edit-list-base-info-form').getData();
        var listObj = {
            todoIconUrl : this.todoIconsList
        }
        var titleObj = {
            todoIconUrl : '待办图标配置'
        }
        var iconPanel = Gikam.create('iconPanel', {
            id : 'core-todo-config-edit-list-icon-panel',
            title : titleObj[field],
            data : listObj[field],
            onConfirm : function(data) {
                var form = Gikam.getComp('core-todo-config-edit-list-base-info-form');
                form.setData({
                    icon : data.iconName,
                    ext$ : {
                        todoiconurl : data.iconUrl
                    }
                });
                iconPanel.cancel();
            }
        });
    },
};
