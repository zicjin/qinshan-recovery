coreNcr.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-ncr-audit-list-grid',
            url : coreNcr.baseUrl + '/queries/tasks',
            requestData : {bpmn_statusCode : _this.param.bpmn_statusCode},
            onCellClick : function(field, row) {
                field === 'ncrNo' && coreNcr.loadDetailPage(row.id, _this.getStatusCode());
            },
            columns : coreNcr.getListColumns(),
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-ncr-audit-list-attachment-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
            },
            toolbar : [{
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                id : 'core-ncr-audit-list-grid-pass-button',
                icon : 'pass',
                onClick : function() {
                    _this.pass();
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                id : 'core-ncr-audit-list-grid-reject-button',
                icon : 'reject',
                onClick : function() {
                    _this.reject();
                }
            }]
        }
    },

    pass : function() {
        var grid = Gikam.getComp('core-ncr-audit-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : coreNcr
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('core-ncr-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : coreNcr
        }).done(function() {
            grid.refresh();
        });
    },

    getAttachmentPanel : function() {
        return {
            title : 'GIKAM.FILE.ATTACHMENT',
            items : [{
                id : 'core-ncr-audit-list-attachment-uploader',
                type : 'uploader',
                multiple : true,
                dbTable : 'T_CORE_NCR',
                bizId : 0,
                preview : false,
                onBeforeDelete : function() {
                    var rows = this.grid.data;
                    var canDelete = true;
                    Gikam.each(rows, function(index, row) {
                        if(workspace.user.id != row.createdById) {
                            canDelete = false;
                            Gikam.alert('QMS.UPLOADER.ALERT.CANNOT_DELETE_FILE_UPLOADED_BY_OTHERS');
                            return;
                        }
                    })
                    return canDelete;
                }
            }]
        }
    },

    getTabs : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            showIndex : 0,
            shrink : false,
            panels : [_this.getAttachmentPanel()]
        }
    },

    create : function() {
        var _this = this;
        var pageConfig = {
            pageObject : coreNcr,
            config : coreNcr.pageConfig,
            prior : this.param.bpmn_statusCode
        };
        Gikam.create('layout', {
            id : 'core-ncr-audit-list-layout',
            renderTo : workspace.window.$dom,
            pageConfig : pageConfig,
            north : {
                height : '50%',
                items : [_this.getGridParam()]
            },
            center : {
                items : [_this.getTabs()]
            }
        });
    },

    getStatusCode : function() {
        var _this = this;
        switch(_this.param.bpmn_statusCode) {
            case 'audit':
                return 'detailPageReview';
            case 'qmRegistAudit':
                return 'detailPageReview';
            case 'ncrConfirm':
                return 'ncrConfirmDetailPage';
            case 'qmConfirmAudit':
                return 'auditPageReview';
            case 'execute':
                return 'executeDetailPage';
            case 'executeAudit':
                return 'executeAuditDetailPage';
            case 'archive':
                return 'auditPageReview';
        }
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
