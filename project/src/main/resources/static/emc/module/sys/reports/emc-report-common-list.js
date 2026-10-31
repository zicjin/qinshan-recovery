coreReportConfig.commonPage = {

    getGridParam: function () {
        var _this = this;
        return {
            type: 'iframe',
            id: 'emc-report-common-list-iframe',
            src: '',
            onRendered: function () {
                var iframe = Gikam.getComp('emc-report-common-list-iframe');
                if ((_this.param.reportConfigName && _this.param.reportName)) {
                    var url = Gikam.printf(IFM_CONTEXT + '/secure/core/module/sys/report-configs/{reportName}/lines/queries', {
                        reportName: _this.param.reportName
                    })
                    Gikam.postSync(url,
                        Gikam.getJsonWrapper({
                            f: {
                                reportConfigLineNo_SEQ: _this.param.reportConfigName
                            }
                        }, ['', []])).done(function (data) {
                        if (!Gikam.isEmpty(data) && !Gikam.isEmpty(data.rows)) {
                            var reportConfigLine = data.rows[0];
                            var reportCategory = reportConfigLine.reportCategory;
                            if (reportCategory === 'fanruan') {
                                reportCategory = 'reportlet';
                            }
                            var reportPath = reportConfigLine.reportConfigLineNo + '/' + encodeURIComponent(reportConfigLine.ext$.filename);
                            var reportUrl = Gikam.getViewReportUrl(reportCategory, reportPath);

                            iframe.refresh({
                                src: reportUrl
                            })
                        }

                    });
                }
            },
        }
    },

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-report-common-list-layout',
            renderTo: workspace.window.$dom,
            center: {
                items: [this.getGridParam()]
            }
        });
    },

    init: function (param) {
        this.param = param;
        this.create();
    }
};
