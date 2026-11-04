/**
 * 待办查询页面
 */
coreTodo.queryPage = {
        
        getGridParam : function() {
        var _this = this;
        return {
            type : 'simpleGrid',
            id : 'core-todo-query-list-grid',
            url : coreTodo.baseUrl,
            columns : coreTodo.getQueryListColumns(),
            genericQueryFields : [ {
                field : 'modelName'
            } ],
            onCellClick : function(field, row) {
                field === 'modelName' && _this.loadDetailPage(row);
                field === 'qty' && _this.loadDetailPage(row);
            },
        }
    },

    loadDetailPage : function(row){
        window.workspace.window.load(row.href);
        window.workspace.setActiveMenu(null, row.modelName.substr(0,row.modelName.length-2), row.modelName);
        Gikam.getLastModal().close();
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    },
};
