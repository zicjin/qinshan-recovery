emcAdvisoryReport.editPage = {

    // 列表布局参数
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-advisory-report-edit-list-grid',
            url : emcAdvisoryReport.baseUrl + '/queries/raw',
            columns : emcAdvisoryReport.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-advisory-report-edit-list-uploader').grid.cleanData();
                }
            },
            onRowActive : function(index, row) {
                Gikam.getComp('emc-advisory-report-edit-list-uploader').setOptions({
                    bizId : row.id,
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            } ]
        }
    },

    submit : function() {
        var grid = Gikam.getComp('emc-advisory-report-edit-list-grid');

        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : emcAdvisoryReport
        }).done(function() {
            grid.refresh();
        });
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
