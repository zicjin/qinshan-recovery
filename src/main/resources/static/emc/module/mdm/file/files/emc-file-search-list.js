emcFile.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            filterOpen : true,      // 展开搜索条件
            url : emcFile.baseUrl + '/queries/searchable',
            columns : emcFile.getEditListColumns(),
            onCellClick : function(field, row) {
                field === 'fileNo' && _this.loadDetailPage(row.id);
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
        workspace.window.load(emcFile.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            operType : 'search'
        }));
    },

    init : function() {
        this.create();
    }
};