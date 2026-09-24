emcBrowseRecord.searchPage = {

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-browse-analysis-record-search-layout',
            renderTo: Gikam.getLastModal().window.$dom,
            west: {
                width: '80%',
                items: [{
                    type: 'tab',
                    width: Gikam.width.S,
                    panels: [{
                        title: 'EMC.MODULE.BP.RUNS.TAB.ANALYSIS_BROWSE_RECORDS',
                        items: [{
                            type: 'iframe',
                            id: 'emc-browse-analysis-record-iframe',
                            src: '',
                            onRendered: function () {
                                var iframe = Gikam.getComp('emc-browse-analysis-record-iframe');
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
                                            debugger;
                                             if (_this.param.recordInfo && !Gikam.isEmpty(_this.param.recordInfo)) {
                                                 var data= _this.recordInfo.otherRecordList.map(function (item) {
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
                                                      Gikam.getComp('emc-browse-analysis-record-iframe').refresh({
                                                              src: ''
                                                          });
                                                          return;
                                                      }
                                                      Gikam.getJson(IFM_CONTEXT + '/core/module/item/files/' + fileId).done(function (coreFile) {
                                                            debugger;
                                                          if (coreFile.fileExt == 'pdf') {
                                                              var url = IFM_CONTEXT + "/static/plugin/pdfjs/web/viewer.html" + Gikam.param({
                                                                  file: Gikam.getFileContextUrl(decodeURIComponent(coreFile.downloadUrl)),
                                                                  pdfPrint: true,
                                                                  readonly: true,
                                                              });
                                                         var modal = Gikam.create('modal', {
                                                                      title: "文件",
                                                                      width : 800,
                                                                      height : 600,
                                                                  });
                                                           var layout = Gikam.create('layout', {
                                                                      renderTo: modal.window.$dom,
                                                                      center: {
                                                                          items: [ {
                                                                              type: 'btnToolbar',
                                                                              items: [  {
                                                                                  type : 'button',
                                                                                  text : 'GIKAM.BUTTON.CANCEL',
                                                                                  icon : 'cancel',
                                                                                  onClick : function () {
                                                                                      modal.close();
                                                                                  }
                                                                              }  ]
                                                                          }]
                                                                      }
                                                                  });

                                                                  var form = Gikam.create('iframe', {
                                                                      renderTo: layout.options.center.$dom,
                                                          			src: url,
                                                                  });
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

    getFileSelectForm: function () {
        var _this = this;
        return {
            type: 'form',
            id: 'emc-select-file-record-search-form',
            titleWidthAuto: true,
            columns: 2,
            fields: emcBrowseRecord.getFileSelectFormFields(),
            onRendered: function () {
                if (_this.recordInfo && !Gikam.isEmpty(_this.recordInfo.otherRecordList)) {
                    var form = Gikam.getComp('emc-select-file-record-search-form');
                    form.setSelectOptions('otherFileCategory', _this.recordInfo.otherRecordList.map(function (item) {
                        return {
                            text: item.name,
                            value: item.id
                        };
                    }), true);
                    if (_this.recordInfo.otherRecordList.length > 0) {
                        form.setData({
                            otherFileCategory: _this.recordInfo.otherRecordList[0].id
                        });
//                        emcBrowseRecord.viewFile(_this.recordInfo.otherRecordList[0].id);
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


        Gikam.getJson(Gikam.printf(IFM_CONTEXT + "/secure/emc/module/bp/result/runs/{id}", {
            id: _this.param.runId
        })).done(function (run) {

            if (run) {
                _this.run = run;
                //查询附件信息
                Gikam.postSync(IFM_CONTEXT + '/secure/emc/module/bp/result/runs/run-audit-file/queries',
                    Gikam.getJsonWrapper({
                        runId: _this.param.runId
                    }, ['', []])).done(function (data) {
                    _this.recordInfo = data;
                    debugger;
                    _this.param.recordInfo = data.otherRecordList;
                });
                _this.create();
            }
        })
    },
}