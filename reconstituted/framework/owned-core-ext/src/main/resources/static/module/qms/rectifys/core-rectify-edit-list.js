coreRectify.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-rectify-edit-list-grid',
            service : 'coreRectifyServiceImpl',
            columns : coreRectify.getListColumns(),
            url : coreRectify.baseUrl + '/queries/raw',
            requestData : {processStatus_IN : 'draft,draft_rejected'},
            onCellClick : function(field, row) {
                field === 'rectifyNo' && coreRectify.loadDetailPage(row.id, 'detailPageEdit');
            },
            deleteFormatter : function(row) {
                return row.rectifyNo;
            },
            toolbar : [{
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                id : 'core-rectify-edit-list-grid-add-button',
                onClick : function() {
                    workspace.window.showMask(true);
                    Gikam.getComp('core-rectify-edit-list-grid').insert(coreRectify.baseUrl, [{}]).done(function(id) {
                        coreRectify.loadDetailPage(id, 'detailPageEdit');
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                id : 'core-rectify-edit-list-grid-delete-button',
                onClick : function() {
                    Gikam.getComp('core-rectify-edit-list-grid').deleteRows(coreRectify.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                id : 'core-rectify-edit-list-grid-summit-button',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }]
        }
    },

    submit : function() {
        var grid = Gikam.getComp('core-rectify-edit-list-grid');
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : coreRectify
        }).done(function() {
            grid.refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-rectify-edit-list-layout',
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
