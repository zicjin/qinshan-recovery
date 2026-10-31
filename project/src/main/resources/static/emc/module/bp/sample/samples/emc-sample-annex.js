emcSample.annexPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-folder-order-batch-form').getData());
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    getBaseInfoForm : function() {
        var _this = this;
        var  readonly = _this.param.readonly ? Gikam.isTrue(_this.param.readonly) : false;
        return {
            type : 'uploader',
            id : 'emc-sample-file-uploader',
            dbTable : 'T_EMC_SAMPLE',
            bizId : _this.param.id,
            batchDownload : true,
            readonly : readonly,
            formatterGridColumns : function (columns) {
                columns.splice(3, 0, {
                    field : 'bizCategory',
                    title : 'T_CORE_FILE.BIZCATEGORY',
                    editor : true,
                    type : 'select',
                    search : true,
                    category : 'sys',
                    param : {
                        codeCategoryId : 'fileBizCategory',
                    }
                }, {
                    field : 'remark',
                    title : 'T_CORE_FILE.REMARK',
                });

                return columns;
            },
            onUploadSuccess : function (fileList) {
                if (Gikam.isEmpty(fileList)) {
                    return;
                }

                if (_this.param.bizCategory) {
                    // 如果有传业务类型，则在文件上传后设置
                    let files = fileList.map(file => {
                        return {
                            id: file.id,
                            bizCategory: _this.param.bizCategory
                        }
                    });
                    Gikam.put(IFM_CONTEXT + '/core/module/item/files/instant', Gikam.getJsonWrapper({}, ['coreFileServiceImpl', files]));
                }
            }
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBaseInfoForm() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    },

}
