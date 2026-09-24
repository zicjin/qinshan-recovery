coreReportConfig.previewPage = {

    // 报表预览 - 左右布局
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-report-config-line-grid',
            url : Gikam.printf(coreReportConfig.baseUrl + '/{id}/lines/queries', {
                id : _this.param.reportConfigId
            }),
            service : 'coreReportConfigLineServiceImpl',
            columns : [ {
                checkbox : true
            }, {
                title : 'T_CORE_REPORT_CONFIG_LINE.REPORTNAME',
                field : 'reportName',
            }, {
                field : 'reportCategory',
                title : 'T_CORE_REPORT_CONFIG_LINE.REPORTCATEGORY',
            } ],
            onRowActive : function(index, row) {
                var selections = Gikam.getComp(_this.param.grid).getSelections();
                var reportUrl = Gikam.getReportUrl(row.id, selections[0])
                if (Gikam.isEmpty(reportUrl)) {
                    Gikam.getComp("emc-report-config-line-preview-iframe").refresh({
                        src : ''
                    })
                    Gikam.alert('EMC.MODULE.SYS.REPORTS.TIP.PARAMTER_VALUE_MISSING');
                    return;
                }
                Gikam.getComp("emc-report-config-line-preview-iframe").refresh({
                    src : reportUrl
                })
            },
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.SYS.REPORTS.BUTTON.NEW_WINDOW_PREVIEW',
                icon : 'preview',
                onClick : function() {
                    var currentSelections = Gikam.getComp('emc-report-config-line-grid').getSelections();
                    if (currentSelections.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    var selections = Gikam.getComp(_this.param.grid).getSelections();
                    var reportUrl = Gikam.getReportUrl(currentSelections[0].id, selections[0])
                    if (Gikam.isEmpty(reportUrl)) {
                        Gikam.alert('EMC.MODULE.SYS.REPORTS.TIP.PARAMTER_VALUE_MISSING');
                        return;
                    }
                    window.open(reportUrl);
                }
            } ]
        }
    },

    // 报表预览 - 上下布局
    getFormParam : function() {
        var _this = this
        return {
            type : 'form',
            id : 'emc-report-config-line-select-base-info-form',
            autoSave : false,
            titleWidth : 66,
            fields : [ {
                field : 'reportName',
                title : 'T_CORE_REPORT_CONFIG_LINE.SELECT_REPORT',
                type : 'select',
                search : false,
                category : 'reportConfigLine',
                selectedIndex : 1,
                width : 260,
                param : {
                    reportConfigId : _this.param.reportConfigId,
                    filter_menuId : workspace.activeMenuNode.id,
                    filter_userId : workspace.user.id,
                    id_IN : _this.param.reportConfigLineId
                },
                onChange : function (field, reportConfigLineId) {
                    if (reportConfigLineId) {
                        var reportUrl = Gikam.getReportUrl(reportConfigLineId);
                        if (Gikam.isEmpty(reportUrl)) {
                            Gikam.getComp("emc-report-config-line-preview-iframe").refresh({
                                src : ''
                            });
                            Gikam.alert('EMC.MODULE.SYS.REPORTS.TIP.PARAMTER_VALUE_MISSING');
                            return;
                        }
                        Gikam.getComp("emc-report-config-line-preview-iframe").refresh({
                            src : reportUrl
                        });
                    }
                },
            } ],
            onRendered : function() {
                var reportConfigLineId = this.getData().reportName;
                var reportUrl = Gikam.getReportUrl(reportConfigLineId);
                Gikam.getComp("emc-report-config-line-preview-iframe").refresh({
                    src : reportUrl
                });
            },
        }
    },

    getViewReportIframe : function() {
    	var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.SYS.REPORTS.TAB.REPORT_PREVIEW',
                items : [ {
                    type : 'iframe',
                    id : 'emc-report-config-line-preview-iframe',
                    src : ''
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-report-preview-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            north : {
                height : 52,
                items : [ _this.getFormParam() ]
            },
            center : {
                items : [ _this.getViewReportIframe() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
}
