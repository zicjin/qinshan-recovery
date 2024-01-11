emcSubpackage.choosePage = {

    // 列表布局参数
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-subpackage-edit-list-grid',
            url : emcSubpackage.baseUrl + '/queries',
            columns : emcSubpackage.getEditListColumns(),
            toolbar : [ ]
        }
    },

    // 创建页面
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    // 初始化
    init : function() {
        this.create();
    }
};
