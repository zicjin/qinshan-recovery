coreNotificationTemplate.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-notification-template-edit-list-grid',
            url : coreNotificationTemplate.baseUrl + '/queries/raw',
            columns : coreNotificationTemplate.getEditListColumns(),
            service : 'coreNotificationTemplateServiceImpl',
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ "core-notification-template-edit-list-base-info-form" ]);
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
            Gikam.getAsyncComp('core-notification-template-edit-list-base-info-form').done(function(form) {
                form.refresh({
                    url : Gikam.printf(coreNotificationTemplate.baseUrl + '/{id}', {
                        id : row.id
                    }),
                });
            })
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.NOTIFICATION.T_CORE_NOTIFICATION_TMPL.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    columns : 2,
                    id : 'core-notification-template-edit-list-base-info-form',
                    fields : coreNotificationTemplate.getBaseInfoFormFields(),
                    service : 'coreNotificationTemplateServiceImpl',
                    autoSave : true,
                    panels : [ {
                        title : 'T_CORE_NOTIFICATION_TMPL.TMPL_INFO',
                        fields : coreNotificationTemplate.getBaseInfoFormFields(),
                    }, {
                        title : 'T_CORE_NOTIFICATION_TMPL.PREPARE_INFO',
                        fields : coreNotificationTemplate.getBaseInfoPrepareFormFields(),
                    } ],
                    onLoadSuccess : function(data) {
                        var viewMethod = data.viewMethod;
                        if (viewMethod === 'menu') {
                            Gikam.getComp('core-notification-template-edit-list-base-info-form').showFields(['menuName']);
                            Gikam.getComp('core-notification-template-edit-list-base-info-form').hideFields(['pageUrl']);
                        } else if (viewMethod === 'page') {
                            Gikam.getComp('core-notification-template-edit-list-base-info-form').showFields(['pageUrl']);
                            Gikam.getComp('core-notification-template-edit-list-base-info-form').hideFields(['menuName']);
                        } else if (viewMethod === 'no') {
                            Gikam.getComp('core-notification-template-edit-list-base-info-form').hideFields(['menuName', 'pageUrl']);
                        }
                    }
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-notification-template-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 400,
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getBaseInfoTab() ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
