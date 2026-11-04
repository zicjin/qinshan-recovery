//@ sourceURL=core-menu-group-edit-list.js
coreMenuGroup.editPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-menu-group-list-grid',
            url : coreMenuGroup.baseUrl + '/queries/raw',
            columns : coreMenuGroup.getEditListColumns(),
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
            },
            suspendFormatter : function(row) {
                return row.groupName || row.id;
            },
            deleteFormatter : function(row) {
                return row.groupName || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.SYS.MENU_GROUPS.TAB.ADD',
                        fields : coreMenuGroup.getBaseInfoFormFields(),
                        url : coreMenuGroup.baseUrl,
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('core-menu-group-list-grid').deleteRows(coreMenuGroup.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function() {
                    Gikam.getComp('core-menu-group-list-grid').unsuspendRows(coreMenuGroup.baseUrl + '/action/unsuspend');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.SUSPEND',
                icon : 'disable',
                onClick : function() {
                    Gikam.getComp('core-menu-group-list-grid').suspendRows(coreMenuGroup.baseUrl + '/action/suspend');
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-menu-group-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreMenuGroup.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function() {
        this.create();
    }
};