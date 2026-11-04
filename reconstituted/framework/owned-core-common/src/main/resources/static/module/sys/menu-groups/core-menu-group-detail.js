//@ sourceURL=core-menu-group-detail.js
coreMenuGroup.detailPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_CORE_MENU_GROUP', _this.param.id);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;

        return {
            type : 'tab',
            width : 900,
            panels : [ {
                title : 'CORE.MODULE.SYS.MENU_GROUPS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-menu-group-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreMenuGroupServiceImpl',
                    fields : coreMenuGroup.getBaseInfoFormFields()
                } ]
            } ]
        }
    },

    getMenuGroupLineTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            width : '1000',
            panels : [ {
                title : 'CORE.MODULE.SYS.MENU_GROUPS.TAB.MENU',
                items : [ {
                    type : 'treeGrid',
                    id : 'core-menu-group-line-list-grid',
                    url : _this.getCompUrl("lineListGrid"),
                    service : 'coreMenuGroupLineServiceImpl',
                    deleteFormatter : function(row) {
                        return row.menuName || row.id;
                    },
                    page : false,
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            if (_this.param.id) {
                                _this.insertMenuGroupLineRow();
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            if (_this.param.id) {
                                _this.deleteMenuGroupLineRow();
                            }
                        }
                    } ],
                    genericQuery : false,
                    columns : coreMenuGroup.getGroupLineListColumns(),
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            center : {
                items : [ _this.getBtnToolbar(), _this.getBaseInfoTab(), _this.getMenuGroupLineTab() ]
            },
            renderTo : workspace.window.$dom
        });

        return this;
    },

    urlManager : {
        baseInfoForm : coreMenuGroup.baseUrl + '/{id}',
        lineListGrid : coreMenuGroup.baseUrl + '/{id}/menu-group-lines/tree-grid'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-menu-group-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'core-menu-group-line-list-grid' : this.getCompUrl('lineListGrid')
        });
    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        });
    },

    insertMenuGroupLineRow : function() {
        var selectedGroupLineRows = Gikam.getComp('core-menu-group-line-list-grid').getSelectedNode();

        if (selectedGroupLineRows.length > 1) {
            Gikam.alert('CORE.MODULE.SYS.MENU_GROUPS.TIP.PLEASE_SELECT_ONE_FIRST_CLASS_MENU');
            return false;
        }

        if (selectedGroupLineRows.length == 1 && selectedGroupLineRows[0].parentId_) {
            Gikam.alert('CORE.MODULE.SYS.MENU_GROUPS.TIP.PLEASE_SELECT_FIRST_CLASS_MENU');
            return false;
        }

        var fromComp = Gikam.getComp('core-menu-group-detail-base-info-form');

        if (!fromComp.validate()) {
            return false;
        }

        var formData = fromComp.getData();

        var insertUrl = Gikam.printf(IFM_CONTEXT + '/secure/core/module/sys/menu-groups/{id}/menu-group-lines', {
            id : formData.id
        });

        var filterJson = JSON.stringify({
            type : 'unassigned',
            targetTable : 'T_CORE_MENU_GROUP_LINE',
            targetMatchColumn : 'MENUID',
            thisMatchColumn : 'ID',
            filter : [ {
                targetFilterColumn : 'GROUPID',
                targetFilterValue : formData.id
            } ]
        });

        Gikam.create('modal', {
            title : 'CORE.MODULE.SYS.MENU_GROUPS.MODAL.MENU_CHOOSE',
            url : IFM_CONTEXT + '/secure/core/module/sys/menus/page/choose-list' + Gikam.param({
                menuGroupChoose : 1,
                target_filter : encodeURI(filterJson)
            }),
            onAfterClose : function(rows) {
                if (rows) {
                    var chooseData = rows.map(function(item) {
                        return {
                            parentId : item.parentId,
                            menuId : item.id,
                            menuName : item.menuName
                        }
                    });

                    Gikam.getComp('core-menu-group-line-list-grid').insert(insertUrl, chooseData);
                }
            }
        });

    },

    deleteMenuGroupLineRow : function() {
        var formData = Gikam.getComp('core-menu-group-detail-base-info-form').getData();

        var deleteUrl = Gikam.printf(IFM_CONTEXT + '/secure/core/module/sys/menu-groups/{id}/menu-group-lines', {
            id : formData.id
        });

        Gikam.getComp('core-menu-group-line-list-grid').deleteRows(deleteUrl);
    },

    init : function(param) {
        this.param = param;

        this.create();
    }
};