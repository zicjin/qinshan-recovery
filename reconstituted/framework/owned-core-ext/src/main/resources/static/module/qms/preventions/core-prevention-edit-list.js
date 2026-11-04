corePrevention.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-prevention-edit-list-grid',
            service : 'corePreventionServiceImpl',
            columns : corePrevention.getListColumns(),
            url : corePrevention.baseUrl + '/queries/raw',
            requestData : {processStatus_IN : 'draft,draft_rejected'},
            onCellClick : function(field, row) {
                field === 'preventionNo' && corePrevention.loadDetailPage(row.id, 'detailPageEdit');
            },
            deleteFormatter : function(row) {
                return row.preventionNo;
            },
            toolbar : [{
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                id : 'core-prevention-edit-list-grid-add-button',
                onClick : function() {
                    workspace.window.showMask(true);
                    Gikam.getComp('core-prevention-edit-list-grid').insert(corePrevention.baseUrl, [{}]).done(function(id) {
                        corePrevention.loadDetailPage(id, 'detailPageEdit');
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                id : 'core-prevention-edit-list-grid-delete-button',
                onClick : function() {
                    Gikam.getComp('core-prevention-edit-list-grid').deleteRows(corePrevention.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                id : 'core-prevention-edit-list-grid-summit-button',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }]
        }
    },

    submit : function() {
        var grid = Gikam.getComp('core-prevention-edit-list-grid');
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : corePrevention
        }).done(function() {
            grid.refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-prevention-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [_this.getGridParam()]
            }
        });
    },

    init : function() {
        this.create();
    }
};
