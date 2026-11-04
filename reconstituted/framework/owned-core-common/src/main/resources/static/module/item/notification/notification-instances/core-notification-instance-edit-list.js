coreNotificationInstance.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-notification-instance-edit-list-grid',
            url : coreNotificationInstance.baseUrl + '/queries',
            columns : coreNotificationInstance.getEditListColumns(),
            service : 'coreNotificationInstanceServiceImpl',
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ "core-notification-instance-edit-list-base-info-form" ]);
                }
            },
            onRowActive : function(index, row) {
                _this.onTemplateActive(row);
            }
        }
    },
    
    onTemplateActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('core-notification-instance-edit-list-base-info-form').refresh({
                url : Gikam.printf(coreNotificationInstance.baseUrl + '/{id}', {
                    id : row.id
                }),
            });
            Gikam.getComp('core-notification-instance-edit-list-instance-file-list-grid').refresh({
                url : Gikam.printf(coreNotificationInstance.baseUrl + '/{id}/files/queries', {
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
                title : 'CORE.MODULE.NOTIFICATION.T_CORE_NOTIFICATION_INST.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    columns : 1,
                    id : 'core-notification-instance-edit-list-base-info-form',
                    fields : coreNotificationInstance.getBaseInfoFormFields(),
                    service : 'coreNotificationInstanceServiceImpl'
                } ]
            } ]
        }
    },
    
    getInstanceFileTab : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'core-notification-instance-edit-list-grid-tab',
            width : Gikam.width.L,
            panels : [ {
                title : 'CORE.MODULE.ITEM.INSTANCE.TAB.ATTACHMENT',
                id : 'core-notification-instance-edit-list-instance-file-panel',
                items : [ {
                    type : 'simpleGrid',
                    id : 'core-notification-instance-edit-list-instance-file-list-grid',
                    columns : coreNotificationInstance.getInstanceFileFields()
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-notification-instance-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 600,
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getBaseInfoTab(), _this.getInstanceFileTab() ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
