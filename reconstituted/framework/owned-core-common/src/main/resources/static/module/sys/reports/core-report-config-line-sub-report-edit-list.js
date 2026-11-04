coreReportConfig.subReportEditListPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-report-config-line-sub-report-edit-list-grid',
            url : '/core/module/item/files/queries',
            requestData : {
                targetId_SEQ : 'T_CORE_REPORT_CONFIG_LINE$' + _this.param.id,
                bizCategory_SEQ : 'sub',
            },
            columns : coreReportConfig.getSubReportEditListColumns(),
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.FILE.BUTTON.UPLOAD_FILE',
                icon : 'upload',
                onClick : function() {
                    _this.uploaderOnly();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    var selections = Gikam.getComp('core-report-config-line-sub-report-edit-list-grid').getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.SELECT_BEFORE_ACTION');
                        return;
                    }
                    Gikam.confirm('GIKAM.TIP.CONFIRM_TO_DELETE', 'CORE.MODULE.SYS.REPORTS.TIP.CONFIRM_DELETE', function() {
                        var deleteUrl = '/core/module/item/files';
                        Gikam.del(deleteUrl, Gikam.getJsonWrapper(null, [ '', selections ])).done(function() {
                            Gikam.getComp('core-report-config-line-sub-report-edit-list-grid').refresh();
                        });
                    });
                }
            } ],
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-report-config-line-sub-report-edit-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    },

    // 上传附件
    uploaderOnly : function() {
        var _this = this;
        var bizId = _this.param.id;
        var selectRows = Gikam.getComp('core-report-config-line-sub-report-edit-list-grid').getSelections();
        if (selectRows.length > 1) {
            Gikam.alert('LIMS.MODULE.SP.FOLDERS.TIP.SELECT_ONE');
            return;
        } else if (selectRows.length === 0) {
            var uploader = Gikam.create('simpleUploader', {
                id : 'core-report-config-line-sub-report-edit-list-uploader',
                dbTable : 'T_CORE_REPORT_CONFIG_LINE',
                bizId : bizId,
                bizCategory : 'sub',
                onBeforeUpload : function() {
                    this.setUploadData({
                        scope : 'finereport'
                    })
                },
                onAfterClose : function() {
                    Gikam.getComp('core-report-config-line-sub-report-edit-list-grid').refresh();
                }
            });
        } else if (selectRows.length === 1) {
            Gikam.confirm('GIKAM.FILE.BUTTON.UPLOAD_FILE', 'CORE.MODULE.SYS.REPORTS.TIP.LINE_ATTACHMENT_EXISTS', function() {
                Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', selectRows ])).done(function(r) {
                    var uploader = Gikam.create('simpleUploader', {
                        id : 'core-report-config-line-sub-report-edit-list-uploader',
                        dbTable : 'T_CORE_REPORT_CONFIG_LINE',
                        bizId : bizId,
                        bizCategory : 'sub',
                        onBeforeUpload : function() {
                            this.setUploadData({
                                scope : 'finereport'
                            })
                        },
                        onAfterClose : function() {
                            Gikam.getComp('core-report-config-line-sub-report-edit-list-grid').refresh();
                        }
                    });
                });
            })
        }
    },
};