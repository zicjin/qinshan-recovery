coreExternalAudit.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-external-audit-audit-list-grid',
            url : coreExternalAudit.baseUrl + '/queries/tasks',
            requestData : {bpmn_statusCode : _this.param.bpmn_statusCode},
            onCellClick : function(field, row) {
                field === 'auditNo' && coreExternalAudit.loadDetailPage(row.id, 'detailPageReview');
            },
            columns : coreExternalAudit.getListColumns(),
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-external-audit-audit-list-attachment-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
                var groupGridUrl = Gikam.printf(coreExternalAudit.baseUrl + '/{id}/audit-groups/queries', {
                    id : row.id
                });
                var itemGridUrl = Gikam.printf(coreExternalAudit.baseUrl + '/{id}/audit-ncrs/queries', {
                    id : row.id
                });
                Gikam.getComp('core-external-audit-audit-list-group-grid').refresh({url : groupGridUrl});
                Gikam.getComp('core-external-audit-audit-list-ncr-grid').refresh({url : itemGridUrl});
            },
            toolbar : [{
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                id : 'core-external-audit-audit-list-grid-pass-button',
                icon : 'pass',
                onClick : function() {
                    _this.pass();
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                id : 'core-external-audit-audit-list-grid-reject-button',
                icon : 'reject',
                onClick : function() {
                    _this.reject();
                }
            }]
        }
    },

    pass : function() {
        var _this = this;
        var grid = Gikam.getComp('core-external-audit-audit-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : coreExternalAudit
        }).done(function() {
            _this.refreshGrids();
        });
    },

    reject : function() {
        var _this = this;
        var grid = Gikam.getComp('core-external-audit-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : coreExternalAudit
        }).done(function() {
            _this.refreshGrids();
        });
    },

    refreshGrids : function() {
        Gikam.getComp('core-external-audit-audit-list-grid').refresh();
        Gikam.getComp('core-external-audit-audit-list-ncr-grid').cleanData();
        Gikam.getComp('core-external-audit-audit-list-group-grid').cleanData();
    },

    getAttachmentPanel : function() {
        return {
            title : 'GIKAM.FILE.ATTACHMENT',
            items : [{
                id : 'core-external-audit-audit-list-attachment-uploader',
                type : 'uploader',
                multiple : true,
                dbTable : 'T_CORE_EXTERNAL_AUDIT',
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

    getAuditGroupPanel : function() {
        return {
            title : 'T_CORE_EXTERNAL_AUDIT_GROUP.GROUPNAME',
            items : [{
                id : 'core-external-audit-audit-list-group-grid',
                type : 'grid',
                columns : coreExternalAudit.getAuditGroupListColumns(),
                service : 'coreExternalAuditGroupServiceImpl'
            }],
        }
    },

    getAuditNcrPanel : function() {
        return {
            title : 'T_CORE_EXTERNAL_AUDIT_NCR',
            items : [{
                id : 'core-external-audit-audit-list-ncr-grid',
                type : 'grid',
                columns : coreExternalAudit.getAuditNcrListColumns(),
                service : 'coreExternalAuditNcrServiceImpl'
            }]
        }
    },

    getTabs : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            showIndex : 1,
            shrink : false,
            panels : [_this.getAttachmentPanel(), _this.getAuditGroupPanel(), _this.getAuditNcrPanel()]
        }
    },

    create : function() {
        var _this = this;
        var pageConfig = {
            pageObject : coreExternalAudit,
            config : coreExternalAudit.pageConfig,
            prior : this.param.bpmn_statusCode
        };
        Gikam.create('layout', {
            id : 'core-external-audit-edit-list-layout',
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

    init : function(param) {
        this.param = param;
        this.create();
    }
};
