emcBrowseReportRecord.browseReportRecordPage = {

    recordInfo: null,

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-browse-report-record-search-layout',
            renderTo: Gikam.getLastModal().window.$dom,
            west: {
                width: '50%',
                items: [{
                    type: 'tab',
                    width: Gikam.width.S,
                    panels: [{
                        title: 'EMC.MODULE.BP.RUNS.TAB.BROWSE_REPORT_WORD',
                        items: [{
                            type: 'iframe',
                            id: 'emc-browse-report-record-iframe',
                            src: '',
                            onRendered : function() {
                                var iframe = Gikam.getComp('emc-browse-report-record-iframe');
                                if (recordInfo && !Gikam.isEmpty(recordInfo.reportFile)) {
                                    Gikam.openFileByOffice({
                                        fileId: recordInfo.reportFile.id,
                                        mode: 'view',
                                        iframe: Gikam.getComp("emc-browse-report-record-iframe").iframe,
                                        viewType: 'iframe'
                                    });
                                }
                            },
                        }]
                    }]
                }]
            },
            center: {
                items: [{
                    type: 'layout',
                    north: {
                        height: 55,
                        items: [this.getFileSelectForm() ]
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

    getFileSelectForm :function () {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-select-report-file-record-search-form',
            titleWidthAuto : true,
            columns : 2,
            fields : emcBrowseReportRecord.getFileSelectFormFields(),
            onRendered : function() {
                if (recordInfo && !Gikam.isEmpty(recordInfo.otherRecordList)) {
                    var form = Gikam.getComp('emc-select-report-file-record-search-form');
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

    init: function (param) {
        this.param = param;

        //查询附件信息
        Gikam.postSync(IFM_CONTEXT + '/secure/emc/module/bp/report/reports/browse-record/queries',
            Gikam.getJsonWrapper({reportId: this.param.reportId}, ['', []])).done(function (data) {
            recordInfo = data;
        });
        this.create();
    },
}