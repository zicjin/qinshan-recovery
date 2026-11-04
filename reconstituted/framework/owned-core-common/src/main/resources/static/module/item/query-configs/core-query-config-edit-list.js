coreQueryConfig.editListPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-query-config-list-grid',
            url : coreQueryConfig.baseUrl + '/queries/raw',
            columns : coreQueryConfig.getEditListColumns(),
            search : [ {
                title : 'CORE.MODULE.ITEM.QUERY-CONFIGS.TITLE.QUERY-GROUP',
                field : 'queryGroup_SEQ',
                type : 'select',
                category : 'sys',
                param : {
                    codeCategoryId : 'queryGroup'
                }
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
                        modalTitle : 'CORE.MODULE.ITEM.QUERY-CONFIGS.TITLE.ADD',
                        fields : coreQueryConfig.getBaseInfoFormFields(),
                        url : coreQueryConfig.baseUrl,
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('core-query-config-list-grid').deleteRows(coreQueryConfig.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function() {
                    Gikam.getComp('core-query-config-list-grid').activateRows(coreQueryConfig.baseUrl + '/action/activate');
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.ITEM.QUERY-CONFIGS.BUTTON.DEACTIVE',
                icon : 'disable',
                onClick : function() {
                    Gikam.getComp('core-query-config-list-grid').deactivateRows(coreQueryConfig.baseUrl + '/action/deactivate');
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
        workspace.window.load(coreQueryConfig.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function() {
        this.create();
    }
};