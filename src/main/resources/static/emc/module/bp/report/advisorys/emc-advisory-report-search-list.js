emcAdvisoryReport.editPage = {

    // 列表布局参数
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-advisory-report-search-list-grid',
            url : emcAdvisoryReport.baseUrl + '/queries/searchable',
            columns : emcAdvisoryReport.getSearchListColumns(),
            onLoadSuccess : function(data) {
                if (Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-advisory-report-search-list-uploader').grid.cleanData();
                }
            },
            onRowActive : function(index, row) {
                Gikam.getComp('emc-advisory-report-search-list-uploader').setOptions({
                    bizId : row.id,
                });
            },
            toolbar : [ ]
        }
    },

    // 创建页面
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            west : {
                width : '50%',
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : '报告附件',
                        items : [ {
                            type : 'uploader',
                            id : 'emc-advisory-report-edit-list-uploader',
                            dbTable : 'T_EMC_ADVISORY_REPORT',
                            readonly : true
                        } ]
                    } ]
                } ]
            }
        });
    },

    // 初始化
    init : function() {
        this.create();
    }
};
