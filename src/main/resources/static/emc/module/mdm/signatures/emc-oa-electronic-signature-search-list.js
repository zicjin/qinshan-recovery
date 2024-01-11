emcOaElectronicSignature.searchPage = {

    // 列表布局参数
    getGridParam : function() {
        var _this = this;
        var pageType = this.param.pageType;
        return {
            type : 'grid',
            id : 'emc-oa-electronic-signature-search-list-grid',
            url : emcOaElectronicSignature.baseUrl + '/queries',
            columns : emcOaElectronicSignature.getSearchListColumns(),
            service : 'emcOaElectronicSignatureServiceImpl',
            page : true,
            requestData : {
                createdById_SEQ : (workspace.user.isAdmin || pageType === 'management') ? '' : workspace.user.id
            },
            deleteFormatter : function(row) {
                return row.id;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                }
            },
            onRowActive : function(index, row){
                Gikam.getComp('emc-oa-electronic-signature-search-list-report-uploader').setOptions({
                    bizId : row.id
                });

                Gikam.getComp('emc-oa-electronic-signature-search-list-uploader').setOptions({
                    bizId : row.id
                });
            }
        }
    },

    /** 报告附件页签 */
    getReportAttachmentGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.S,
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.SIGNATURE.TITLE.REPORT_FILE',
                items: [ {
                    type : 'uploader',
                    id : 'emc-oa-electronic-signature-search-list-report-uploader',
                    dbTable : 'T_EMC_OA_ELECTRONIC_SIGNATURE',
                    formatterGridColumns : function (columns) {
                        return columns;
                    },
                    onBeforeRefresh : function (requestData) {
                        requestData['bizCategory_SEQ'] = 'signatureReport';
                    },
                    readonly : false,
                    formatterGridToolbar : function () {
                        return [ {
                            type: 'button',
                            text: 'EMC.MODULE.MDM.BASEMDM.SIGNATURE.BUTTON.BATCH_DOWNLOAD',
                            icon: 'move-down',
                            onClick: function () {
                                _this.batchDownload();
                            }
                        } ]
                    }
                } ]
            } ]
        }
    },

    /** 其他附件页签 */
    getAttachmentGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.S,
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.SIGNATURE.TITLE.FILE',
                items: [ {
                    type : 'uploader',
                    id : 'emc-oa-electronic-signature-search-list-uploader',
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
                    readonly : true,
                } ]
            } ]
        }
    },

    /** 批量下载报告 */
    batchDownload : function() {
        var selections = Gikam.getComp('emc-oa-electronic-signature-search-list-report-uploader').grid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        selections.forEach(item => Gikam.download(item.downloadUrl, item.name));
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
    init : function(param) {
        this.param = param;
        this.create();
    }
};
