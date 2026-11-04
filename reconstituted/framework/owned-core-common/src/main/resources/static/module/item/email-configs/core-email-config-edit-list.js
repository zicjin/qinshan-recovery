coreEmailConfig.editListPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-email-config-list-grid',
            url : coreEmailConfig.baseUrl + '/queries/raw',
            columns : coreEmailConfig.getEditListColumns(),
            genericQueryFields : [ {
                field : 'id'
            }, {
                field : 'configName'
            } ],
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.EMAIL.EMAILCONFIGS.MODAL.ADD',
                        fields : coreEmailConfig.getBaseInfoFormFields(),
                        url : coreEmailConfig.baseUrl,
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            },{
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('core-email-config-list-grid').deleteRows(coreEmailConfig.baseUrl);
                }
            },{
                type : 'button',
                text : 'GIKAM.BUTTON.SUSPEND',
                icon : 'enable',
                onClick : function() {
                    Gikam.getComp('core-email-config-list-grid').suspendRows(coreEmailConfig.baseUrl + '/action/suspend');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.UNSUSPEND',
                icon : 'disable',
                onClick : function() {
                    Gikam.getComp('core-email-config-list-grid').unsuspendRows(coreEmailConfig.baseUrl + '/action/unsuspend');
                }
            } ]
        }
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreEmailConfig.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function() {
        this.create();
    }
};