coreExternalAudit.searchPage = {

    getGridParam : function() {
        return {
            type : 'grid',
            id : 'core-external-audit-search-list-grid',
            url : coreExternalAudit.baseUrl + '/queries',
            columns : coreExternalAudit.getListColumns(),
            requestData : {
                processStatus_EQ : 'done'
            },
            onCellClick : function(field, row) {
                field === 'auditNo' && coreExternalAudit.loadDetailPage(row.id, 'detailPageReadonly');
            }
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

    getAttachmentPanel : function() {
        return {
            title : 'GIKAM.FILE.ATTACHMENT',
            items : [{
                id : 'core-external-audit-search-list-attachment-uploader',
                type : 'uploader',
                multiple : true,
                dbTable : 'T_CORE_EXTERNAL_AUDIT',
                bizId : 0,
                preview : false,
                readonly : true
            }]
        }
    },

    getAuditGroupPanel : function() {
        return {
            title : 'T_CORE_EXTERNAL_AUDIT_GROUP.GROUPNAME',
            items : [{
                id : 'core-external-audit-search-list-group-grid',
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
                id : 'core-external-audit-search-list-ncr-grid',
                type : 'grid',
                columns : coreExternalAudit.getAuditNcrListColumns(),
                service : 'coreExternalAuditNcrServiceImpl'
            }]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-external-audit-search-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '50%',
                items : [_this.getGridParam()]
            },
            center : {
                items : [_this.getTabs()]
            }
        });
    },

    init : function() {
        this.create();
    }
};
