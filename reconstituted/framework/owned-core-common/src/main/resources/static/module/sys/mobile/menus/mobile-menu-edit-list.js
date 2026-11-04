mobileMenu.editPage = {

    getBaseBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'CORE.MODULE.SYS.MOBILE_MENUS.BUTTON.CONFIG',
                icon : 'add',
                onClick : function() {
                    _this.openPageConfig();
                }
            }]
        }
    },

    openPageConfig: function (){
        if (this.param == null || Gikam.isEmpty(this.param.id)) {
            return '';
        }
        workspace.window.load(mobileMenu.baseUrl + '/page/detail' + Gikam.param({
            id : this.param.id
        }));
    },

    getGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'mobile-menu-edit-list-grid',
            url: mobileMenu.baseUrl + '/queries/raw',
            columns: mobileMenu.getEditListColumns(),
            group: {
                groupCheckbox : false,
                fields : ['menuCategoryName'],
                formatter : function(row) {
                    return row.menuCategoryName;
                }
            },
            service: 'mobileMenuServiceImpl',
            checkOneOnActive: true,
            checkOnActive: false,
            activateFormatter: function (row) {
                return row.menuName;
            },
            deleteFormatter: function (row) {
                return row.menuName;
            },
            // onRendered : function() {
            //     this.hideColumns([ 'menuCategoryName' ]);
            // },
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(1);
                } else {
                    Gikam.cleanCompData(["mobile-menu-edit-list-base-info-form"]);
                }
            },
            onRowActive: function (index, row) {
                _this.onConfigActive(row);
            },
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.INSERT',
                icon: 'add',
                onClick: function () {
                    Gikam.preInsert({
                        modalTitle: 'CORE.MODULE.SYS.MOBILE_MENUS.MODAL.ADD',
                        height: 250,
                        fields: mobileMenu.getPreInsertFields(),
                        url: mobileMenu.baseUrl,
                        // onFormRendered : function(form) {
                        //     this.options.id = 'mobile-menu-edit-list-preinsert-form';
                        // }
                    }).done(function (id) {
                        Gikam.getComp('mobile-menu-edit-list-grid').refresh();
                    });
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.DELETE',
                icon: 'remove-row',
                onClick: function () {
                    Gikam.getComp('mobile-menu-edit-list-grid').deleteRows(mobileMenu.baseUrl);
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.ACTIVE',
                icon: 'enable',
                onClick: function () {
                    Gikam.getComp('mobile-menu-edit-list-grid').activateRows(mobileMenu.baseUrl + "/action/activate");
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.DEACTIVE',
                icon: 'disable',
                onClick: function () {
                    Gikam.getComp('mobile-menu-edit-list-grid').deactivateRows(mobileMenu.baseUrl + "/action/deactivate");
                }
            }, {
                type: 'button',
                text: 'CORE.MODULE.SYS.MOBILE_MENUS.BUTTON.MOVEUP',
                icon: 'move-up',
                onClick: function () {
                    Gikam.getComp('mobile-menu-edit-list-grid').moveUp(mobileMenu.baseUrl + '/action/swap/{firstId}/{secondId}')
                }
            }, {
                type: 'button',
                text: 'CORE.MODULE.SYS.MOBILE_MENUS.BUTTON.MOVEDOWN',
                icon: 'move-down',
                onClick: function () {
                    Gikam.getComp('mobile-menu-edit-list-grid').moveDown(mobileMenu.baseUrl + '/action/swap/{firstId}/{secondId}')
                }
            }]
        }
    },

    onConfigActive: function (row) {
        var _this = this;
        if (row) {
            this.param = row;
            Gikam.getComp('mobile-menu-edit-list-base-info-form').refresh({
                url: Gikam.printf(mobileMenu.baseUrl + '/{id}', {
                    id: row.id
                })
            });
        }
    },

    getBaseInfoTab: function () {
        var _this = this;
        return {
            panels: [{
                title: 'CORE.MODULE.SYS.MOBILE_MENUS.TAB.BASE_INFO',
                items: [{
                    type: 'form',
                    columns: 3,
                    id: 'mobile-menu-edit-list-base-info-form',
                    fields: mobileMenu.getBaseInfoFormFields(),
                    service: 'mobileMenuServiceImpl',
                    autoSave: true,
                    onUpdated: function () {
                        var grid = Gikam.getComp('mobile-menu-edit-list-grid');
                        if (mobileMenu.change) {
                            grid.refresh();
                        } else {
                            grid.refreshRowById(grid.getActivedRow().id);
                        }
                        mobileMenu.change = false;
                    }
                }]
            }],
            type: 'tab'
        }
    },

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'mobile-menu-edit-list-layout',
            renderTo: workspace.window.$dom,
            west: {
                width: 400,
                items: [_this.getGridParam()]
            },
            center: {
                items: [_this.getBaseBtnToolbar(),_this.getBaseInfoTab()]
            }
        });
    },

    init: function () {
        this.param = null;
        this.create();
    },

    menuIconsList: [],
    getShowImgPanel: function (field) {
        var _this = this;
        var listObj = {
            menuIconUrl: this.menuIconsList
        }
        var urlObj = {
            menuIconUrl: mobileMenu.baseUrl + '/icons/queries'
        }
        if (listObj[field].length === 0) {
            Gikam.getJson(urlObj[field]).done(function (data) {
                data.forEach(function (item) {
                    item.id = item.iconName;
                    item.src = IFM_CONTEXT + item.iconUrl;
                });
                _this.menuIconsList = data;
                _this.imgPanelHandle(field);
            });
        } else {
            _this.imgPanelHandle(field);
        }
    },
    imgPanelHandle: function (field) {
        var formData = Gikam.getComp('mobile-menu-edit-list-base-info-form').getData();
        var listObj = {
            menuIconUrl: this.menuIconsList
        }
        var titleObj = {
            menuIconUrl: '菜单图标配置'
        }
        var iconPanel = Gikam.create('iconPanel', {
            renderTo: workspace.window.$dom,
            id: 'mobile-menu-edit-list-icon-panel',
            title: titleObj[field],
            data: listObj[field],
            onConfirm: function (data) {
                var form = Gikam.getComp('mobile-menu-edit-list-base-info-form');
                form.setData({
                    icon: data.iconName,
                    ext$: {
                        menuiconurl: data.iconUrl
                    }
                });
                iconPanel.cancel();
            }
        });
    },
};