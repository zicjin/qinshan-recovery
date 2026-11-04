coreConstant.editPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            service : 'coreConstantServiceImpl',
            id : 'core-constant-edit-list-grid',
            url : coreConstant.baseUrl + '/queries/raw',
            columns : coreConstant.getEditListColumns(),
            requestData : {
                show_SEQ : '1'
            },
            genericQueryFields : [ {
                field : 'id'
            }, {
                field : 'constantValue'
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-constant-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreConstant.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function() {
        this.create();
    }
};