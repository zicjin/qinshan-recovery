emcBrowseRecord.searchPage = {

    recordInfo: null,

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-browse-analysis-record-search-layout',
            renderTo: Gikam.getLastModal().window.$dom,
            west: {
                width: '50%',
                items: [{
                    type: 'tab',
                    width: Gikam.width.S,
                    panels: [{
                        title: 'EMC.MODULE.BP.RUNS.TAB.ANALYSIS_BROWSE_RECORDS',
                        items: [{
                            type: 'iframe',
                            id: 'emc-browse-analysis-record-iframe',
                            src: '',
                            onRendered : function() {
                                var iframe = Gikam.getComp('emc-browse-analysis-record-iframe');
                                if (recordInfo && !Gikam.isEmpty(recordInfo.analysisRecord)) {
                                    Gikam.openFileByOffice({
                                        fileId: recordInfo.analysisRecord.id,
                                        mode: 'view',
                                        iframe: Gikam.getComp("emc-browse-analysis-record-iframe").iframe,
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
                                    id: 'emc-browse-other-record-iframe',
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
            id : 'emc-select-file-record-search-form',
            titleWidthAuto : true,
            columns : 2,
            fields : emcBrowseRecord.getFileSelectFormFields(),
            onRendered : function() {
                if (recordInfo && !Gikam.isEmpty(recordInfo.otherRecordList)) {
                    var form = Gikam.getComp('emc-select-file-record-search-form');
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
        Gikam.postSync(IFM_CONTEXT + '/secure/emc/module/bp/result/runs/browse-record/queries',
            Gikam.getJsonWrapper({runId: this.param.runId}, ['', []])).done(function (data) {
            recordInfo = data;
        });
        this.create();
    },
}