coreEmailTemplate.editListPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-email-template-list-grid',
            url : coreEmailTemplate.baseUrl + '/queries/raw',
            columns : coreEmailTemplate.getEditListColumns(),
            genericQueryFields : [ {
                field : 'id'
            }, {
                field : 'templateName'
            } ],
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
            }
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
        workspace.window.load(coreEmailTemplate.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function() {
        this.create();
    }
};