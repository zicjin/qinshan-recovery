emcAdvisoryReport.auditPage = {

    // 列表布局参数
    getGridParam : function() {
        var _this = this;
        _this.param['status_SEQ'] = _this.param.bpmn_statusCode;

        var columns = emcAdvisoryReport.getAuditListColumns();
        var hiddenPassBtn = false;
        if (_this.param.bpmn_statusCode === 'reportArchiving') {
            columns = emcAdvisoryReport.getArchivingListColumns();
            hiddenPassBtn = true;
        } else if (_this.param.bpmn_statusCode === 'reportIssue') {
            columns = emcAdvisoryReport.getIssueListColumns();
        }  else if (_this.param.bpmn_statusCode === 'reportRevise') {
            hiddenPassBtn = true;
        }

        return {
            type : 'grid',
            id : 'emc-advisory-report-audit-list-grid',
            url : emcAdvisoryReport.baseUrl + '/queries/tasks',
            columns : columns,
            service : 'emcAdvisoryReportServiceImpl',
            requestData : _this.param,
            onLoadSuccess : function(data) {
                if (Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-advisory-report-audit-list-uploader').grid.cleanData();
                }
            },
            onRowActive : function(index, row) {
                Gikam.getComp('emc-advisory-report-audit-list-uploader').setOptions({
                    bizId : row.id,
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                icon : 'pass',
                hidden : hiddenPassBtn,
                onClick : function() {
                    _this.pass();
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                icon : 'reject',
                onClick : function() {
                    _this.reject();
                }
            } ]
        }
    },


    pass : function() {
        var grid = Gikam.getComp('emc-advisory-report-audit-list-grid');
        var selections = grid.getSelections();

        Gikam.create('workflow').pass({
            data : selections,
            pageObject : emcAdvisoryReport
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('emc-advisory-report-audit-list-grid');
        var selections = grid.getSelections();

        Gikam.create('workflow').reject({
            data : selections,
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
                            id : 'emc-advisory-report-audit-list-uploader',
                            dbTable : 'T_EMC_ADVISORY_REPORT',
                            readonly : true
                        } ]
                    } ]
                } ]
            }
        });
    },

    // 初始化
    init : function(param) {
        this.param = param;
        this.create();
    }
};
