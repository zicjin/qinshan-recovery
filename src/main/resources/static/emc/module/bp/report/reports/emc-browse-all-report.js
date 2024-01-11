emcReport.browseAllReportPage = {

    recordInfo : null,
    reportInfo : null,

    getReportFileSelectForm :function () {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-select-word-report-search-form',
            titleWidthAuto : true,
            columns : 2,
            fields : [ {
                title : '报告文件',
                field : 'wordReport',
                type : 'select',
                firstBlank : false,
                onChange : function(field, value) {
                    var iframe = Gikam.getComp('emc-browse-report-record-iframe');
                    if (value) {
                        Gikam.openFileByOffice({
                            fileId: value,
                            mode: 'view',
                            iframe: Gikam.getComp("emc-browse-report-record-iframe").iframe,
                            viewType: 'iframe'
                        });
                    }
                },
            } ],
            onRendered : function() {
                if (reportInfo && !Gikam.isEmpty(reportInfo)) {
                    var form = Gikam.getComp('emc-select-word-report-search-form');
                    form.setSelectOptions('wordReport', reportInfo.map(function(item) {
                        return {
                            text : item.name,
                            value : item.id
                        };
                    }), true);
                    if (reportInfo.length > 0) {
                        form.setData({
                            wordReport : reportInfo[0].id
                        });
                    } else {
                        form.setData({
                            wordReport : "",
                        });
                    }
                }
            }
        };
    },

    getOriginalFileSelectForm :function () {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-select-original-record-search-form',
            titleWidthAuto : true,
            columns : 2,
            fields : [ {
                title : 'EMC.MODULE.BP.RUNS.TAB.OTHER_BROWSE_RECORDS_CATEGORY',
                field : 'otherFileCategory',
                type : 'select',
                firstBlank : false,
                onChange : function(field, value) {
                    var iframe = Gikam.getComp('emc-browse-other-report-record-iframe');
                    if (value) {
                        Gikam.openFileByOffice({
                            fileId: value,
                            mode: 'view',
                            iframe: Gikam.getComp("emc-browse-other-report-record-iframe").iframe,
                            viewType: 'iframe'
                        });
                    }
                },
            } ],
            onRendered : function() {
                if (recordInfo && !Gikam.isEmpty(recordInfo.otherRecordList)) {
                    var form = Gikam.getComp('emc-select-original-record-search-form');
                    form.setSelectOptions('otherFileCategory', recordInfo.otherRecordList.map(function(item) {
                        return {
                            text : item.name,
                            value : item.id
                        };
                    }), true);
                    if (recordInfo.otherRecordList.length > 0) {
                        form.setData({
                            otherFileCategory : recordInfo.otherRecordList[0].id
                        });
                    } else {
                        form.setData({
                            otherFileCategory : "",
                        });
                    }
                }
            }
        };
    },

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-browse-report-record-search-layout',
            renderTo: Gikam.getLastModal().window.$dom,
            west: {
                width: '50%',
                items: [{
                    type: 'layout',
                    north: {
                        height: 55,
                        items: [this.getReportFileSelectForm() ]
                    },
                    center: {
                        items: [{
                            type: 'tab',
                            width: Gikam.width.S,
                            panels: [{
                                title: 'EMC.MODULE.BP.RUNS.TAB.BROWSE_REPORT_WORD',
                                items: [{
                                    type: 'iframe',
                                    id: 'emc-browse-report-record-iframe',
                                    src: '',
                                }]
                            }]
                        }]
                    }
                }]
            },
            center: {
                items: [{
                    type: 'layout',
                    north: {
                        height: 55,
                        items: [this.getOriginalFileSelectForm() ]
                    },
                    center: {
                        items: [{
                            type: 'tab',
                            width: Gikam.width.S,
                            panels: [{
                                title: 'EMC.MODULE.BP.RUNS.TAB.OTHER_BROWSE_RECORDS',
                                items: [{
                                    type: 'iframe',
                                    id: 'emc-browse-other-report-record-iframe',
                                    src: '',
                                }]
                            }]
                        }]
                    }
                }]
            }
        });
    },

    init: function (param) {
        this.param = param;

        let reportIds = this.param.reportIds.split(',');
        //查询原始记录附件信息
        Gikam.postSync(IFM_CONTEXT + '/secure/emc/module/bp/report/reports/browse-record/queries',
            Gikam.getJsonWrapper({reportId: reportIds[0]}, ['', []])).done(function (data) {
            recordInfo = data;
        });

        // 查询报告附件信息
        let targetIds = reportIds.map(id => 'T_EMC_REPORT$' + id).join();
        Gikam.postSync(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
            f : {
                targetId_IN : targetIds,
                bizCategory_IN : 'wordReport,qcReport'
            },
        }, ['', []])).done(function (data) {
            console.info(data)
            reportInfo = data.rows;
        });

        this.create();
    },
}
