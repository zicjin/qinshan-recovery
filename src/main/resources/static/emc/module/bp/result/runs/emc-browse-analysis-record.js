var emcBrowseRecord = Gikam.getPageObject({

    getFileSelectFormFields: function () {
        var _this = this;
        return [{
            title: '文件',
            field: 'otherFileCategory',
            type: 'select',
            firstBlank: false,
            onChange: function (field, value) {
                _this.viewFile(value);
            },
        },{
            title: '文件类型',
            field:'type',
            type:'select',
            category : 'sys',
            editor: true,
             param : {
                 codeCategoryId : 'fileBizCategory',
             }
        }];
    },
        getFileListFields: function () {
            var _this = this;
            return [{
                title: '文件',
                field: 'otherFileCategory',
                type :'link',
                width: 200
            },{
                 title: '文件类型',
                 field:'type',
                 type:'select',
                 category : 'sys',
                 editor: true,
                  param : {
                      codeCategoryId : 'fileBizCategory',
                  }
             }];
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
});
