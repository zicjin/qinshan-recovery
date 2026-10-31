emcFileApply.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-file-apply-edit-list-grid',
            url : emcFileApply.baseUrl + '/queries/raw',
            columns : emcFileApply.getEditListColumns(),
            service : 'emcFileApplyServiceImpl',
            deleteFormatter : function(row) {
                return row.fileNo || row.id;
            },
            requestData : {
                filterUserId : '1'
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.insert();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-file-apply-edit-list-grid').deleteRows(emcFileApply.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            } , {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    var grid = Gikam.getComp('emc-file-apply-edit-list-grid');
                    Gikam.viewAudit('T_EMC_FILE_APPLY', grid.getSelections());
                }
            } ]
        }
    },
    
    insert : function() {
        Gikam.preInsert({
            modalTitle : 'EMC.MODULE.BP.MDM.FILE.FILEAPPLIES.TAB.ADD',
            fields : emcFileApply.getBaseInfoFormFields(),
            url : emcFileApply.baseUrl
        }).done(function(id) {
            Gikam.getComp('emc-file-apply-edit-list-grid').refresh();
        });
    },
    
    submit : function() {
        var grid = Gikam.getComp('emc-file-apply-edit-list-grid');
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : emcFileApply
        }).done(function() {
            grid.refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-file-apply-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
