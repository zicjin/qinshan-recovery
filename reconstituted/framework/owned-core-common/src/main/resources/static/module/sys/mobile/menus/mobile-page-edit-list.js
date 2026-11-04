mobilePage.editPage = {

    getBaseBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [{
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                }
            }]
        }
    },

    getGridParam: function () {
        var _this = this;
        return {
            type: 'treeGrid',
            id: 'mobile-page-edit-list-grid',
            url: _this.getPageConfigUrl(),
            columns: mobilePage.getEditListColumns(),
            service: 'mobilePageServiceImpl',
            nodeTextField : 'pageName',
            single : true,
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData(["mobile-page-edit-list-base-info-form"]);
                }
            },
            onRowActive: function (index, row) {
                _this.onConfigActive(row);
            },
            toolbar: [{
                type: 'button',
                text: 'CORE.MODULE.SYS.MOBILE_MENUS.BUTTON.ADD_SAME_CLASS_PAGE',
                icon: 'add-row',
                onClick: function () {
                    // _this.save(_this.insertSamelvlPageRow, false);
                    _this.insertSamelvlPageRow();
                }
            }, {
                type: 'button',
                text: 'CORE.MODULE.SYS.MOBILE_MENUS.BUTTON.ADD_NEXT_CLASS_PAGE',
                icon: 'add-row',
                onClick: function () {
                    // _this.save(_this.insertLowerlvlPageRow, false);
                    _this.insertLowerlvlPageRow();
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.DELETE',
                icon: 'remove-row',
                onClick: function () {
                    // _this.save(_this.deletePage, false);
                    _this.deletePage();
                }
            }, {
                type: 'button',
                text: 'CORE.MODULE.SYS.MOBILE_MENUS.BUTTON.MOVEUP',
                icon: 'move-up',
                onClick: function () {
                    Gikam.getComp('mobile-page-edit-list-grid').moveUp(mobilePage.baseUrl + '/action/switch/{firstId}/{secondId}')
                }
            }, {
                type: 'button',
                text: 'CORE.MODULE.SYS.MOBILE_MENUS.BUTTON.MOVEDOWN',
                icon: 'move-down',
                onClick: function () {
                    Gikam.getComp('mobile-page-edit-list-grid').moveDown(mobilePage.baseUrl + '/action/switch/{firstId}/{secondId}')
                }
            }]
        }
    },

    onConfigActive: function (row) {
        var _this = this;
        if (row) {
            Gikam.getComp('mobile-page-edit-list-base-info-form').refresh({
                url: Gikam.printf(mobilePage.baseUrl + '/{id}', {
                    id: row.id
                })
            });
        }
    },

    getPageConfigUrl: function () {
        if (this.param == null || Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(mobilePage.baseUrl + "/{id}/mobile-page/tree-grids", {
            id: this.param.id || '0'
        });
    },

    getBaseInfoTab: function () {
        var _this = this;
        return {
            type: 'tab',
            fill: true,
            panels: [{
                title: 'CORE.MODULE.SYS.MOBILE_MENUS.TAB.BASE_INFO',
                items: [{
                    type: 'form',
                    columns: 3,
                    id: 'mobile-page-edit-list-base-info-form',
                    fields: mobilePage.getBaseInfoFormFields(),
                    service: 'mobilePageServiceImpl',
                    autoSave: true,
                    onUpdated: function () {
                        var grid = Gikam.getComp('mobile-page-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    }
                }]
            }]
        }
    },

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'mobile-page-edit-list-layout',
            renderTo: workspace.window.$dom,
            west: {
                width: 300,
                items: [_this.getGridParam()]
            },
            center: {
                items: [_this.getBaseBtnToolbar(), _this.getBaseInfoTab()]
            },
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    },

    // 添加同级页面
    insertSamelvlPageRow: function () {
        var _this = this;
        var url = Gikam.printf(mobilePage.baseUrl + '/{id}/mobile-page-same-level', {
            id: _this.param.id
        });
        var allPageArr = Gikam.getComp('mobile-page-edit-list-grid').getData();
        var pageArr = Gikam.getComp('mobile-page-edit-list-grid').getSelections();
        if (!Gikam.isEmpty(allPageArr)) {
            if (Gikam.isEmpty(pageArr)) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                return;
            } else if (pageArr.length > 1) {
                Gikam.alert('CORE.MODULE.SYS.MOBILE_MENUS.TIP.CHOOSE_ONE_ITEM');
                return;
            }
        }
        var chooseData = pageArr.map(function (task) {
            return {
                id: task.id,
                lvl: task.lvl,
                parentId: task.parentId
            }
        });
        Gikam.getComp('mobile-page-edit-list-grid').insert(url, chooseData);
    },

    // 添加下级页面
    insertLowerlvlPageRow: function () {
        var _this = this;
        var url = Gikam.printf(mobilePage.baseUrl + '/{id}/mobile-page-lower-level', {
            id: _this.param.id
        });
        var pageArr = Gikam.getComp('mobile-page-edit-list-grid').getSelections();
        if (Gikam.isEmpty(pageArr)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        } else if (pageArr.length > 1) {
            Gikam.alert('CORE.MODULE.SYS.MOBILE_MENUS.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var chooseData = pageArr.map(function (task) {
            return {
                id: task.id,
                lvl: task.lvl,
                taskNo: task.taskNo
            }
        });
        Gikam.getComp('mobile-page-edit-list-grid').insert(url, chooseData);
    },

    // 删除
    deletePage: function () {
        var allPageArr = Gikam.getComp('mobile-page-edit-list-grid').getData();
        var selectedPageArr = Gikam.getComp('mobile-page-edit-list-grid').getSelections();
        var flag = true;
        if (Gikam.isEmpty(selectedPageArr)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        } else if (selectedPageArr.length > 1) {
            Gikam.alert('CORE.MODULE.SYS.MOBILE_MENUS.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        for (var i = 0; i < allPageArr.length; i++) {
            if (allPageArr[i].parentId == selectedPageArr[0].id) {
                flag = false;
                Gikam.alert('CORE.MODULE.SYS.MOBILE_MENUS.TIP.CONTAIN_SUB_TASK');
                return;
            }
        }

        if (flag) {
            var url = Gikam.printf(mobilePage.baseUrl + '/{id}/mobile-page', {
                id: this.param.id
            });
            Gikam.getComp('mobile-page-edit-list-grid').deleteRows(url);
        }

    }
};