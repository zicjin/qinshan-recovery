emcOaElectronicSignature.editPage = {

    // 列表布局参数
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-oa-electronic-signature-edit-list-grid',
            url : emcOaElectronicSignature.baseUrl + '/queries/raw',
            columns : emcOaElectronicSignature.getEditListColumns(),
            service : 'emcOaElectronicSignatureServiceImpl',
            page : true,
            requestData : {
                createdById_SEQ : workspace.user.id
            },
            deleteFormatter : function(row) {
                return row.id;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-oa-electronic-signature-edit-list-report-uploader').grid.cleanData();
                    Gikam.getComp('emc-oa-electronic-signature-edit-list-uploader').grid.cleanData();
                }
            },
            onRowActive : function(index, row){
                Gikam.getComp('emc-oa-electronic-signature-edit-list-report-uploader').setOptions({
                    bizId : row.id
                });

                Gikam.getComp('emc-oa-electronic-signature-edit-list-uploader').setOptions({
                    bizId : row.id
                });
            },
            toolbar : [ {
                type: 'button',
                text: 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon: 'submit',
                onClick: function () {
                    _this.submit();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'GIKAM.BUTTON.INSERT',
                        fields : emcOaElectronicSignature.getBaseInfoFormFields(),
                        url : emcOaElectronicSignature.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-oa-electronic-signature-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-oa-electronic-signature-edit-list-grid').deleteRows(emcOaElectronicSignature.baseUrl);
                }
            } ]
        }
    },

    /** 报告附件页签 */
    getReportAttachmentGridParam : function() {
        return {
            type : 'tab',
            width : Gikam.width.S,
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.SIGNATURE.TITLE.REPORT_FILE',
                items: [ {
                    type : 'uploader',
                    id : 'emc-oa-electronic-signature-edit-list-report-uploader',
                    dbTable : 'T_EMC_OA_ELECTRONIC_SIGNATURE',
                    formatterGridColumns : function (columns) {
                        return columns;
                    },
                    onBeforeRefresh : function (requestData) {
                        requestData['bizCategory_SEQ'] = 'signatureReport';
                    },
                    onbeforeFilesUpload : function(fileList) {
                        let reg = new RegExp("document$");
                        let isDocument = fileList.some(function (item) {
                            return reg.test(item.file.type);
                        });

                        if (!isDocument) {
                            Gikam.alert('EMC.MODULE.MDM.BASEMDM.SIGNATURE.TIP.UNSUPPORTED_FILE');
                        }

                        return isDocument;
                    },
                    onAfterClose : function(fileList) {
                        if (Gikam.isEmpty(fileList)) {
                            return;
                        }
                        // 设置默认业务类型为签名报告signatureReport
                        let files = fileList.map(file => {
                            return {
                                id: file.id,
                                bizCategory: 'signatureReport'
                            }
                        });
                        Gikam.put(IFM_CONTEXT + '/core/module/item/files/instant', Gikam.getJsonWrapper({}, ['coreFileServiceImpl', files])).done(function () {
                            let activedRow =  Gikam.getComp('emc-oa-electronic-signature-edit-list-grid').getActivedRow();
                            Gikam.getComp('emc-oa-electronic-signature-edit-list-report-uploader').setOptions({
                                bizId : activedRow.id
                            });
                        });
                    }
                } ]
            } ]
        }
    },

    /** 其他附件页签 */
    getAttachmentGridParam : function() {
        return {
            type : 'tab',
            width : Gikam.width.S,
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.SIGNATURE.TITLE.FILE',
                items: [ {
                    type : 'uploader',
                    id : 'emc-oa-electronic-signature-edit-list-uploader',
                    dbTable : 'T_EMC_OA_ELECTRONIC_SIGNATURE',
                    onBeforeRefresh : function (requestData) {
                        requestData['bizCategory_DIFFER'] = 'signatureReport';
                    },
                    formatterGridColumns : function (columns) {
                        columns.splice(3, 0, {
                            field : 'bizCategory',
                            title : 'T_CORE_FILE.BIZCATEGORY',
                            editor : true,
                            type : 'select',
                            search : true,
                            firstBlank : false,
                            category : 'sys',
                            param : {
                                codeCategoryId : 'oaFileType',
                            }
                        });

                        return columns;
                    },
                    onAfterClose : function(fileList) {
                        if (Gikam.isEmpty(fileList)) {
                            return;
                        }
                        // 设置默认业务类型为普通文件ft1
                        let files = fileList.map(file => {
                            return {
                                id: file.id,
                                bizCategory: 'ft1'
                            }
                        });
                        Gikam.put(IFM_CONTEXT + '/core/module/item/files/instant', Gikam.getJsonWrapper({}, ['coreFileServiceImpl', files])).done(function () {
                            let activedRow =  Gikam.getComp('emc-oa-electronic-signature-edit-list-grid').getActivedRow();
                            Gikam.getComp('emc-oa-electronic-signature-edit-list-uploader').setOptions({
                                bizId : activedRow.id
                            });
                        });
                    }
                } ]
            } ]
        }
    },

    submit : function() {
        var grid = Gikam.getComp('emc-oa-electronic-signature-edit-list-grid');
        var selections = grid.getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }

        if (!grid.validate(grid.getSelections())) {
            return;
        }
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : emcOaElectronicSignature
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
                width : '60%',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ this.getGridParam() ]
                    },
                    center : {
                        height : '50%',
                        items : [ this.getReportAttachmentGridParam() ]
                    }
                } ]
            },
            center : {
                items : [ _this.getAttachmentGridParam() ]
            }
        });
    },

    // 初始化
    init : function() {
        this.create();
    }
};
