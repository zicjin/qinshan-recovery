coreInternalAudit.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-internal-audit-search-list-grid',
            url : coreInternalAudit.baseUrl + '/queries/searchable',
            activateFirstRow : true,
            columns : coreInternalAudit.getSearchListColumns(),
            onCellClick : function(field, row) {
                field === 'auditNo' && _this.loadDetailPage(row.id);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-internal-audit-search-list-audit-group-grid', 'core-internal-audit-search-list-audit-ncr-grid' ]);
                    Gikam.getComp('qqms-internal-audit-search-list-uploader').grid.cleanData();
                }
            },
            onRowActive  : function(index, row) {
                Gikam.getAsyncComp('qms-internal-audit-search-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
                Gikam.getComp('core-internal-audit-search-list-audit-group-grid').refresh({
                    url : Gikam.printf(coreInternalAudit.baseUrl + '/{id}/audit-groups/queries', {
                        id : row.id
                    })
                });
                Gikam.getComp('core-internal-audit-search-list-audit-ncr-grid').refresh({
                    url : Gikam.printf(coreInternalAudit.baseUrl + '/{id}/audit-ncrs/queries', {
                        id : row.id
                    })
                });
            },
        }
    },

    getSouthTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ _this.getAttachment(), _this.getAuditGroupGrid(), _this.getAuditNcrGrid(), ]
        }
    },
    
    getAuditGroupGrid : function() {
        return {
            title : 'MODULE.QMS.INTERNALAUDITS.TAB.AUDIT_GROUP',
            items : [ {
                type : 'grid',
                id : 'core-internal-audit-search-list-audit-group-grid',
                service : 'coreInternalAuditGroupServiceImpl',
                url : Gikam.printf(coreInternalAudit.baseUrl + '/{id}/audit-groups/queries', {
                    id : 0
                }),
                deleteFormatter : function(row) {
                    return row.id;
                },
                columns : coreInternalAudit.getAuditGroupListColumns()
            } ]
        };
    },

    getAttachment : function() {
        return {
            title : 'GIKAM.FILE.ATTACHMENT',
            items : [ {
                type : 'uploader',
                id : 'qms-internal-audit-search-list-uploader',
                dbTable : 'T_CORE_INTERNAL_AUDIT',
                bizId : 0,
                onBeforeDelete : function() {
                    var rows = this.grid.data;
                    var canDelete = true;
                    Gikam.each(rows, function(index, row) {
                        if (workspace.user.id != row.createdById) {
                            canDelete = false;
                            Gikam.alert('QMS.UPLOADER.ALERT.CANNOT_DELETE_FILE_UPLOADED_BY_OTHERS');
                            return;
                        }
                    })
                    return canDelete;
               }
            } ]
        }
    },
    
    getAuditNcrGrid : function() {
        return {
            title : 'MODULE.QMS.INTERNALAUDITS.TAB.AUDIT_NCR',
            items : [ {
                type : 'grid',
                id : 'core-internal-audit-search-list-audit-ncr-grid',
                service : 'coreInternalAuditNcrServiceImpl',
                url : Gikam.printf(coreInternalAudit.baseUrl + '/{id}/audit-ncrs/queries', {
                    id : 0
                }),
                deleteFormatter : function(row) {
                    return row.id;
                },
                columns : coreInternalAudit.getAuditNcrListColumns()
            } ]
        };
    },
    
    create : function() {
        Gikam.create('layout', {
            id : 'core-internal-audit-search-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '50%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getSouthTab() ]
            }
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreInternalAudit.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            sourcePage : 'search'
        }));
    },

    init : function() {
        this.create();
    }
};
