var coreMenu = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/menus',

    menuIconsList : [],

    todoIconsList : [],

    getChooseListColumns : function () {
        return [ {
            field : 'id',
            title : 'T_CORE_MENU.ID',
            type : 'tree',
            width : 200
        }, {
            field : 'menuName',
            title : 'T_CORE_MENU.MENUNAME',
            width : 300
        }, {
            field : 'href',
            title : 'T_CORE_MENU.HREF',
        }, {
            field : 'resourceName',
            title : 'T_CORE_MENU.RESOURCENAME'
        } ]
    },

    getBaseInfoFormFields : function () {
        var _this = this;
        return [ {
            field : 'menuName',
            title : 'T_CORE_MENU.MENUNAME',
        }, {
            field : 'i18nCode',
            title : 'T_CORE_MENU.I18NCODE',
        }, {
            field : 'href',
            title : 'T_CORE_MENU.HREF',
            colspan : 2,
        }, {
            field : 'menuIconUrl',
            title : 'T_CORE_MENU.MENUICON',
            type : 'image',
            height : 30,
            align : 'center',
            enlarged : false,
            onClick : function () {
                _this.getShowImgPanel('menuIconUrl');
            },
            onError : function (image, value) {
                return '/static/icon/menu/peony/white/' + value.substring(value.lastIndexOf('/') + 1);
            }
        }, {
            field : 'todoIconUrl',
            title : 'T_CORE_MENU.TODOICON',
            type : 'image',
            height : 30,
            align : 'center',
            enlarged : false,
            onClick : function (img) {
                _this.getShowImgPanel('todoIconUrl')
            }
        }, {
            field : 'refresh',
            title : 'T_CORE_MENU.REFRESH',
            type : 'select',
            height : 30,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            onChange : function (key, value) {
                var formData = Gikam.getComp('core-menu-edit-list-form').getData();
                if (formData.href) {
                    var simpleUrl = formData.href.split('?')[ 0 ];
                    var param = Gikam.getRequestParam(formData.href);
                    param.refresh = value;
                    if (value === '1') {
                        delete param.refresh;
                    }
                    var url = simpleUrl + Gikam.param(param);
                    if (Gikam.param(param) === '?') {
                        url = simpleUrl;
                    }
                    Gikam.getComp('core-menu-edit-list-form').setData({
                        href : url
                    });
                } else if (formData.resourceId) {

                    if(Gikam.isNotEmpty(formData.menuParam)) {
                         menuParamJsObj = JSON.parse(formData.menuParam);
                         menuParamJsObj.refresh = value;

                         Gikam.getComp('core-menu-edit-list-form').setData({
                             menuParam : JSON.stringify(menuParamJsObj)
                         });


                    } else {
                       Gikam.getComp('core-menu-edit-list-form').setData({
                           menuParam : JSON.stringify({
                              'refresh' : value
                           })
                       });
                    }
                }
            }
        }, {
            field : 'resourceName',
            title : 'T_CORE_MENU.RESOURCENAME',
            type : 'choose',
            category : 'pageChoose',
            single : true,
            targetFields : [ {
                'resourceId' : 'id'
            }, {
                'resourcePath' : 'path'
            }, {
                'resourceName' : 'resourceName'
            } ],
        }, {
            field : 'menuParam',
            title : 'T_CORE_MENU.MENUPARAM',
            type : 'codeEditor',
            language : 'json',
            height : 200
        }]
    },

    getMenuRoleColumns : function () {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.rolename',
            title : 'T_CORE_ROLE.ROLENAME',
        } ]
    },

    getShowImgPanel : function (field) {
        var _this = this;
        var listObj = {
            menuIconUrl : this.menuIconsList,
            todoIconUrl : this.todoIconsList
        }
        var urlObj = {
            menuIconUrl : _this.baseUrl + '/icons/queries',
            todoIconUrl : _this.baseUrl + '/todo-icons/queries'
        };
        if (listObj[ field ].length === 0) {
            Gikam.getJson(urlObj[ field ]).done(function (data) {
                data.forEach(function (item) {
                    item.id = item.iconName;
                    item.src = IFM_CONTEXT + item.iconUrl;
                });
                field === 'menuIconUrl' && (_this.menuIconsList = data);
                field === 'todoIconUrl' && (_this.todoIconsList = data);
                _this.imgPanelHandle(field, false);
            }).fail(function (e) {
                _this.imgPanelHandle(field, true);
            });
        } else {
            this.imgPanelHandle(field, false);
        }
    },

    imgPanelHandle : function (field, error) {
        var _this = this;
        var listObj = {
            menuIconUrl : this.menuIconsList,
            todoIconUrl : this.todoIconsList
        }
        var titleObj = {
            menuIconUrl : '菜单图标配置',
            todoIconUrl : '待办图标配置'
        }
        var iconPanel = Gikam.create('iconPanel', {
            id : 'core-menu-edit-list-icon-panel',
            title : titleObj[ field ],
            data : listObj[ field ],
            noRequest : error,
            onConfirm : function (data) {
                var form = Gikam.getComp('core-menu-edit-list-form');
                var iconUrl = data.iconUrl ? data.iconUrl.replace(/blue|black/g, 'white') : error ? '/static/icon/menu/peony/white/' + data.id + '.svg' : null;
                field === 'menuIconUrl' && form.setData({
                    icon : error ? data.id + '.svg' : data.iconName,
                    menuIconUrl : iconUrl
                });
                field === 'todoIconUrl' && form.setData({
                    todoIcon : data.iconName,
                    todoIconUrl : iconUrl
                });
                iconPanel.cancel();
            }
        });
    }

});