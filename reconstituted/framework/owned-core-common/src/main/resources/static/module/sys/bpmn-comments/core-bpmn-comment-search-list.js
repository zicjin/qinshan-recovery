coreBpmnComment.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-bpmn-draft-list-grid',
            url : coreBpmnComment.baseUrl + '/queries/raw',
            columns : coreBpmnDraft.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.procCode || row.id;
            },
            onCellClick : function(field, row) {
                field === 'procCode' && _this.loadDetailPage(row.id);
            }
        }
    },

    getBtnToolbarParam : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'btnGroup',
                items : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add',
                    onClick : function() {
                        _this.loadDetailPage();
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.MODIFY',
                    icon : 'edit',
                    onClick : function() {
                        _this.modifyProc();
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove',
                    onClick : function() {
                        Gikam.getComp('core-bpmn-draft-list-grid').deleteRows(coreBpmnDraft.baseUrl);
                    }
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-bpmn-draft',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getBtnToolbarParam(), this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreBpmnDraft.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },
    
    modifyProc : function() {
        var _this = this;
        
        Gikam.create('modal', {
            title : 'CORE.MODULE.SYS.T_CORE_BPMN_PROC.MODAL.TITLE_CHOOSE',
            url : IFM_CONTEXT + '/secure/core/module/sys/bpmn-procs/page/choose-list?service=modifiable',
            onAfterClose : function(row) {
                if (row) {
                    var insertUrl = Gikam.printf('/secure/core/module/sys/bpmn-drafts/source-procs/{procId}', {
                    	procId : row[0].id
                    });
                    
                    Gikam.postText(insertUrl).done(function(data) {
                        _this.loadDetailPage(data);
                    })
                }
            }
        });
    },

    init : function() {
        this.create();
    }
};