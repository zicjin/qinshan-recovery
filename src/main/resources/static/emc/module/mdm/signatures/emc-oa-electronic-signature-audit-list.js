emcOaElectronicSignature.auditPage = {

    // 列表布局参数
    getGridParam : function() {
        var _this = this;
        var statusCode = _this.param.bpmn_statusCode;
        return {
            type : 'grid',
            id : 'emc-oa-electronic-signature-audit-list-grid',
            url : emcOaElectronicSignature.baseUrl + '/queries/tasks',
            columns : (statusCode === 'audit') ? emcOaElectronicSignature.getAuditListColumns() : emcOaElectronicSignature.getApproveListColumns(),
            service : 'emcOaElectronicSignatureServiceImpl',
            page : true,
            requestData : _this.param,
            deleteFormatter : function(row) {
                return row.id;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-oa-electronic-signature-audit-list-report-uploader').grid.cleanData();
                    Gikam.getComp('emc-oa-electronic-signature-audit-list-uploader').grid.cleanData();
                }
            },
            onRowActive : function(index, row){
                Gikam.getComp('emc-oa-electronic-signature-audit-list-report-uploader').setOptions({
                    bizId : row.id
                });

                Gikam.getComp('emc-oa-electronic-signature-audit-list-uploader').setOptions({
                    bizId : row.id
                });
            },
            toolbar : [ {
                type: 'button',
                text: 'GIKAM.WORKFLOW.BUTTON.PASS',
                icon: 'pass',
                onClick: function () {
                    _this.pass();
                }
            }, {
                type: 'button',
                text: 'GIKAM.WORKFLOW.BUTTON.REJECT',
                icon: 'reject',
                onClick: function () {
                    _this.reject();
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
                    id : 'emc-oa-electronic-signature-audit-list-report-uploader',
                    dbTable : 'T_EMC_OA_ELECTRONIC_SIGNATURE',
                    formatterGridColumns : function (columns) {
                        return columns;
                    },
                    onBeforeRefresh : function (requestData) {
                        requestData['bizCategory_SEQ'] = 'signatureReport';
                    },
                    readonly : true
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
                    id : 'emc-oa-electronic-signature-audit-list-uploader',
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
                    readonly : true
                } ]
            } ]
        }
    },

    pass : function() {
        var grid = Gikam.getComp('emc-oa-electronic-signature-audit-list-grid');
        var selections = grid.getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }

        if (!grid.validate(grid.getSelections())) {
            return;
        }

        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : emcOaElectronicSignature
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('emc-oa-electronic-signature-audit-list-grid');
        Gikam.create('workflow').reject({
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
    init : function(param) {
        this.param = param;
        if ('audit'=== param.bpmn_statusCode){
            param.auditorId_SEQ = workspace.user.id;
        }else if ('approve' === param.bpmn_statusCode){
            param.approverId_SEQ = workspace.user.id;
        }
        this.create();
    }
};
