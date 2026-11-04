coreExternalAudit.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-external-audit-edit-list-grid',
            url : coreExternalAudit.baseUrl + '/queries/raw',
            requestData : {processStatus_IN : 'draft,draft_rejected'},
            onCellClick : function(field, row) {
                field === 'auditNo' && coreExternalAudit.loadDetailPage(row.id, 'detailPageEdit');
            },
            columns : coreExternalAudit.getListColumns(),
            deleteFormatter : function(row) {
                return row.auditNo;
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-external-audit-edit-list-attachment-uploader').done(function(uploader) {
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
                Gikam.getComp('core-external-audit-edit-list-group-grid').refresh({url : groupGridUrl});
                Gikam.getComp('core-external-audit-edit-list-ncr-grid').refresh({url : itemGridUrl});
            },
            toolbar : [{
                type : 'button',
                text : 'GIKAM.AT.INSERT',
                id : 'core-external-audit-edit-list-grid-add-button',
                icon : 'add',
                onClick : function() {
                    workspace.window.showMask(true);
                    Gikam.getComp('core-external-audit-edit-list-grid').insert(coreExternalAudit.baseUrl, [{}]).done(function(id) {
                        coreExternalAudit.loadDetailPage(id, 'detailPageEdit');
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                id : 'core-external-audit-edit-list-grid-delete-button',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-external-audit-edit-list-grid').deleteRows(coreExternalAudit.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                id : 'core-external-audit-edit-list-grid-submit-button',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }]
        }
    },

    submit : function() {
        var _this = this;
        var grid = Gikam.getComp('core-external-audit-edit-list-grid');
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : coreExternalAudit
        }).done(function() {
            _this.refreshGrids();
        });
    },

    refreshGrids : function() {
        Gikam.getComp('core-external-audit-edit-list-grid').refresh();
        Gikam.getComp('core-external-audit-edit-list-ncr-grid').cleanData();
        Gikam.getComp('core-external-audit-edit-list-group-grid').cleanData();
    },

    getAttachmentPanel : function() {
        return {
            title : 'GIKAM.FILE.ATTACHMENT',
            items : [{
                id : 'core-external-audit-edit-list-attachment-uploader',
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
                id : 'core-external-audit-edit-list-group-grid',
                type : 'grid',
                columns : coreExternalAudit.getAuditGroupListColumns(),
                service : 'coreExternalAuditGroupServiceImpl',
                deleteFormatter : function(row) {
                    return row.groupName;
                },
                toolbar : [{
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    id : 'core-external-audit-edit-list-group-grid-add-button',
                    icon : 'add',
                    onClick : function() {
                        var activedRow = Gikam.getComp('core-external-audit-edit-list-grid').getActivedRow() || undefined;
                        if(activedRow == undefined) {
                            Gikam.alert('T_CORE_EXTERNAL_AUDIT.ALERT.MSG.CHOOSE_ONE_ITEM');
                            return;
                        }
                        Gikam.preInsert({
                            modalTitle : 'GIKAM.AT.INSERT',
                            width : 500,
                            height : 375,
                            fields : coreExternalAudit.getAuditGroupFormFields(),
                            validators : ['notEmpty'],
                            url : Gikam.printf(coreExternalAudit.baseUrl + '/{id}/audit-groups', {id : activedRow.id})
                        }).done(function(id) {
                            Gikam.getComp('core-external-audit-edit-list-group-grid').refresh();
                        });
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    id : 'core-external-audit-edit-list-group-grid-delete-button',
                    icon : 'remove-row',
                    onClick : function() {
                        Gikam.getComp('core-external-audit-edit-list-group-grid').deleteRows(Gikam.printf(coreExternalAudit.baseUrl + '/{id}/audit-groups', {
                            id : 0
                        }));
                    }
                }]
            }],
        }
    },

    getAuditNcrPanel : function() {
        return {
            title : 'T_CORE_EXTERNAL_AUDIT_NCR',
            items : [{
                id : 'core-external-audit-edit-list-ncr-grid',
                type : 'grid',
                columns : coreExternalAudit.getAuditNcrListColumns(),
                service : 'coreExternalAuditNcrServiceImpl',
                deleteFormatter : function(row) {
                    return row.ncrItemNos;
                },
                toolbar : [{
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add',
                    id : 'core-external-audit-edit-list-ncr-grid-add-button',
                    onClick : function() {
                        var activedRow = Gikam.getComp('core-external-audit-edit-list-grid').getActivedRow() || undefined;
                        if(activedRow == undefined) {
                            Gikam.alert('T_CORE_EXTERNAL_AUDIT.ALERT.MSG.CHOOSE_ONE_ITEM');
                            return;
                        }
                        Gikam.preInsert({
                            modalTitle : 'GIKAM.AT.INSERT',
                            width : 800,
                            height : 500,
                            titleWidth : 120,
                            fields : coreExternalAudit.getAuditNcrFormFields(),
                            validators : ['notEmpty'],
                            url : Gikam.printf(coreExternalAudit.baseUrl + '/{id}/audit-ncrs', {id : activedRow.id})
                        }).done(function(id) {
                            Gikam.getComp('core-external-audit-edit-list-ncr-grid').refresh();
                        });
                    }
                }, {
                    id : 'core-external-audit-edit-list-ncr-grid-delete-button',
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function() {
                        Gikam.getComp('core-external-audit-edit-list-ncr-grid').deleteRows(Gikam.printf(coreExternalAudit.baseUrl + '/{id}/audit-ncrs', {
                            id : 0
                        }));
                    }
                }]
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
        Gikam.create('layout', {
            id : 'core-external-audit-edit-list-layout',
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
