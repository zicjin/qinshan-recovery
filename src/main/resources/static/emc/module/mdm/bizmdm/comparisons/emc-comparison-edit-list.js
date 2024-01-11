emcComparison.editPage = {

    // 列表布局参数
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-comparison-edit-list-grid',
            url : emcComparison.baseUrl + '/queries/raw',
            columns : emcComparison.getEditListColumns(),
            checkOnActive : false,
            requestData : _this.param,
            service : 'emcComparisonServiceImpl',
            deleteFormatter : function(row) {
                return row.monitorPointName || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.BIZMDM.COMPARISONS.TITLE.ADD_COMPARISON',
                        fields : emcComparison.getBaseInfoFormFields(),
                        url : emcComparison.baseUrl
                    }).done(function() {
                        Gikam.getComp('emc-comparison-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-comparison-edit-list-grid').deleteRows(emcComparison.baseUrl);
                }
            } ]
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
