coreEmail.searchListPage = {
    getEmailTemplateGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-email-config-list-grid',
            url : IFM_CONTEXT + '/secure/core/module/item/email-configs/queries',
            columns : coreEmail.getEmailConfigListColumns(),
            genericQueryFields : [ {
                field : 'id'
            }, {
                field : 'configName'
            } ],
            onSelect : function(row, $row) {
                Gikam.getComp('core-email-list-grid').refresh({
                    url : coreEmail.baseUrl + '/queries',
                    requestData : {
                        configId_EQ : row.id
                    }
                });
            }
        }
    },

    getEmailParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-email-list-grid',
            url : '',
            columns : coreEmail.getSearchListColumns(),
            genericQueryFields : [ {
                field : 'id'
            } ],
            onCellClick : function(field, row) {
                field === 'id' && _this.loadEmailDetailPage(row.id);
            }
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            west : {
                width : 330,
                items : [ _this.getEmailTemplateGridParam() ]
            },
            center : {
                items : [ _this.getEmailParam() ]
            },
            renderTo : workspace.window.$dom,
        });
    },

    loadEmailDetailPage : function(id) {
        if (id) {
            workspace.window.load(coreEmail.baseUrl + '/page/detail' + Gikam.param({
                id : id
            }));
        }
    },

    init : function() {
        this.create();
    }
};