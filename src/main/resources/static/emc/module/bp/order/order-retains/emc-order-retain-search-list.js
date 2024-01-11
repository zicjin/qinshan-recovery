emcOrderRetain.searchPage = {

    // 列表布局参数
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-order-retain-search-list-grid',
            url : emcOrderRetain.baseUrl + '/queries',
            columns : emcOrderRetain.getEditListColumns(),
            page : true,
            readonly : true,
            toolbar : [{
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('retainLabel', Gikam.getComp('emc-order-retain-search-list-grid').getSelections());
                }
            }]
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
