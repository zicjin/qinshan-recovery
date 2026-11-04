coreNcr.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-ncr-edit-list-grid',
            url : coreNcr.baseUrl + '/queries/raw',
            requestData : {processStatus_IN : 'draft,draft_rejected'},
            onCellClick : function(field, row) {
                field === 'ncrNo' && coreNcr.loadDetailPage(row.id, 'detailPageEdit');
            },
            columns : coreNcr.getListColumns(),
            deleteFormatter : function(row) {
                return row.ncrNo;
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-ncr-edit-list-attachment-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
            },
            toolbar : [{
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                id : 'core-ncr-edit-list-grid-add-button',
                onClick : function() {
                    workspace.window.showMask(true);
                    Gikam.getComp('core-ncr-edit-list-grid').insert(coreNcr.baseUrl, [{}]).done(function(id) {
                        coreNcr.loadDetailPage(id, 'detailPageEdit');
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                id : 'core-ncr-edit-list-grid-delete-button',
                onClick : function() {
                    Gikam.getComp('core-ncr-edit-list-grid').deleteRows(coreNcr.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                id : 'core-ncr-edit-list-grid-summit-button',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }]
        }
    },

    submit : function() {
        var grid = Gikam.getComp('core-ncr-edit-list-grid');
        Gikam.create('workflow').submit({
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
                id : 'core-ncr-edit-list-attachment-uploader',
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
        Gikam.create('layout', {
            id : 'core-ncr-edit-list-layout',
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
