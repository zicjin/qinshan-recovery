coreBpmnPresetComment.editPage = {

    getGridParam : function () {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-bpmn-preset-comment-edit-list-grid',
            url : coreBpmnPresetComment.baseUrl + '/queries/raw',
            columns : coreBpmnPresetComment.getEditListColumns(),
            deleteFormatter : function (row) {
                return row.presetComment || row.Id;
            },
            service : 'coreBpmnPresetCommentServiceImpl',
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function () {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.SYS.BPMNPRESETCOMMENTS.MODAL.ADD',
                        fields : coreBpmnPresetComment.getBaseInfoFormFields(),
                        url : coreBpmnPresetComment.baseUrl
                    }).done(function (id) {
                        var grid = Gikam.getComp('core-bpmn-preset-comment-edit-list-grid');

                        grid.refresh().done(function () {
                            grid.activeRowById(id);
                        });
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function () {
                    Gikam.getComp('core-bpmn-preset-comment-edit-list-grid').deleteRows(coreBpmnPresetComment.baseUrl);
                }
            }]
        }
    },

    create : function () {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-bpmn-preset-comment-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function () {
        this.create();
    }
};
