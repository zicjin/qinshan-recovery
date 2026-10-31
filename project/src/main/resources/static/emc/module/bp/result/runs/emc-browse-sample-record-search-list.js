emcBrowseRecord.searchPage = {

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-browse-sample-record-search-layout',
            renderTo: Gikam.getLastModal().window.$dom,
            west: {
                width: '80%',
                items: [{
                    type: 'tab',
                    width: Gikam.width.S,
                    panels: [{
                        title: '查看采样单',
                        items: [{
                            type: 'iframe',
                            id: 'emc-browse-sample-record-iframe',
                            src: '',
                            onRendered: function () {
                                var iframe = Gikam.getComp('emc-browse-sample-record-iframe');
                                if (_this.run && !Gikam.isEmpty(_this.run.reportConfigLineId)) {
                                    var reportUrl = Gikam.getReportUrl(_this.run.reportConfigLineId, [_this.run])
                                    iframe.refresh({
                                        src: reportUrl
                                    })
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
                        height: 0,
                        items: [this.getFileSelectForm()]
                    },
                    center: {
                        items: [{
                            type: 'tab',
                            width: Gikam.width.S,
                            panels: [{
                                title: '文件',
                                items: [{
                                    type: 'grid',
                                   title:'文件',
                                   id: 'emc-project-sample-file-list-grid',
                                   columns: emcBrowseRecord.getFileListFields(),
                                   onRendered: function () {
                                               if (_this.param.recordInfo && !Gikam.isEmpty(_this.param.recordInfo)) {
                                                   var data= _this.recordInfo.map(function (item) {
                                                           return {
                                                               otherFileCategory: item.name,
                                                               type:item.bizCategory,
                                                               id: item.id
                                                           };
                                                       })
                                                    Gikam.getComp('emc-project-sample-file-list-grid').loadData(data);
                                               }
                                           },
                                            onRowActive : function(index, row) {

                                             },
                                             onCellClick: function (field ,row) {
                                                if(field==='otherFileCategory'){
                                                 var fileId=row.id;
                                                  if (Gikam.isEmpty(fileId)) {
                                                            Gikam.getComp('emc-browse-other-record-iframe').refresh({
                                                                src: ''
                                                            });
                                                            return;
                                                        }
                                                        Gikam.getJson(IFM_CONTEXT + '/core/module/item/files/' + fileId).done(function (coreFile) {

                                                            if (coreFile.fileExt == 'pdf') {
                                                                var url = IFM_CONTEXT + "/static/plugin/pdfjs/web/viewer.html" + Gikam.param({
                                                                    file: Gikam.getFileContextUrl(decodeURIComponent(coreFile.downloadUrl)),
                                                                    pdfPrint: true,
                                                                    readonly: true,
                                                                });

                                                                Gikam.getComp('emc-browse-other-record-iframe').refresh({
                                                                    src: url
                                                                })
                                                            } else {
                                                                Gikam.openFile(fileId, true);
                                                            }
                                                        });
                                                }
                                             }
                                }]
                            }]
                        }]
                    }
                }]
            }
        });
    },

    viewFile: function (fileId) {
       debugger
        if (Gikam.isEmpty(fileId)) {
            Gikam.getComp('emc-browse-other-record-iframe').refresh({
                src: ''
            });
            return;
        }
        Gikam.getJson(IFM_CONTEXT + '/core/module/item/files/' + fileId).done(function (coreFile) {

            if (coreFile.fileExt == 'pdf') {
                var url = IFM_CONTEXT + "/static/plugin/pdfjs/web/viewer.html" + Gikam.param({
                    file: Gikam.getFileContextUrl(decodeURIComponent(coreFile.downloadUrl)),
                    pdfPrint: true,
                    readonly: true,
                });

                Gikam.getComp('emc-browse-other-record-iframe').refresh({
                    src: url
                })
            } else {
                Gikam.openFile(fileId, true);
            }
        });
    },

    getFileSelectForm: function () {
        var _this = this;
        return {
            type: 'form',
            id: 'emc-select-file-record-search-form',
            titleWidthAuto: true,
            columns: 2,
            fields: emcBrowseRecord.getFileSelectFormFields(),
            onRendered: function () {
                if (_this.recordInfo && !Gikam.isEmpty(_this.recordInfo)) {
                    var form = Gikam.getComp('emc-select-file-record-search-form');
                    form.setSelectOptions('otherFileCategory', _this.recordInfo.map(function (item) {
                        debugger
                        return {
                            text: item.name,
                            type:item.bizCategory,
                            value: item.id
                        };
                    }), true);
                    if (_this.recordInfo.length > 0) {
                        form.setData({
                            otherFileCategory: _this.recordInfo[0].id
                        });
//                        emcBrowseRecord.viewFile(_this.recordInfo[0].id);
                    } else {
                        form.setData({
                            otherFileCategory: "",
                        });
                    }
                }
            }
        };
    },

    init: function (param) {
        var _this = this;
        _this.param = param;


        Gikam.getJson(Gikam.printf(IFM_CONTEXT + "/secure/emc/module/bp/sample/samples/{id}", {
            id: _this.param.sampleId
        })).done(function (run) {
            if (run) {
                _this.run = run;
                //查询附件信息
                Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
                     f : {
                         targetId : 'T_EMC_SAMPLE$' + _this.param.sampleId
                     },
                     n : -1,
                     s : 50
                })).done(function(r) {
                    _this.recordInfo = r.rows;
                     _this.param.recordInfo = r.rows;
                    _this.create();
                });
            }
        })
    },
}