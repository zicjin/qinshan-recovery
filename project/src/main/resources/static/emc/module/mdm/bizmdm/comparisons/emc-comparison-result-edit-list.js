emcComparison.resultPage = {

    // 列表布局参数
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-comparison-result-edit-list-grid',
            url : emcComparison.baseUrl + '/results/queries',
            columns : emcComparison.getResultEditListColumns(),
            service : 'emcComparisonResultServiceImpl',
            requestData : {
                folderId_SEQ : !Gikam.isEmpty(_this.param.folderId) ? _this.param.folderId : '',
            },
            onLoadSuccess : function(data) {
                if (Gikam.isEmpty(data)) {
                    let url = Gikam.printf(emcComparison.baseUrl + '/results/{folderId}/action/insert', {
                        folderId : _this.param.folderId
                    });
                    Gikam.postText(url).done(function () {
                        Gikam.getComp('emc-comparison-result-edit-list-grid').refresh();
                    })
                }
            },
        }
    },

    // 创建页面
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    // 初始化
    init : function(param) {
        this.param = param;
        this.create();
    }
};
