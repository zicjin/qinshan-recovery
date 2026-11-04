coreBpmnDraft.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-bpmn-draft-list-grid',
            url : coreBpmnDraft.baseUrl + '/queries/raw',
            columns : coreBpmnDraft.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.procCode || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.SYS.T_CORE_BPMN_DRAFT.TAB.ADD_BASEINFO',
                        fields : coreBpmnDraft.getBaseInfoFormFields(),
                        url : coreBpmnDraft.baseUrl,
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
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
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.IMPORT',
                icon : 'upload',
                onClick : function() {
                    Gikam.create('simpleUploader',{
                        multiple : false,
                        autoClose : true,
                        dbTable : 'T_CORE_BPMN_DRAFT',
                        bizId : 'input',
                        onBeforeFileUpload : function(file) {
                            var fileName = file.name;
                            if (fileName.substring(fileName.lastIndexOf('.') + 1) !== 'json') {
                                return {
                                    result: false,
                                    message: 'GIKAM.BPMN.TIP.UPLOAD_FILE_BY_JSON'
                                }
                            } else {
                                return true;
                            }
                        },
                        onUploadSuccess : function (file){
                            Gikam.post(Gikam.printf(IFM_CONTEXT + '/core/module/item/files/{id}/action/import/coreBpmnImportFileFunction', {
                                id : file[0].id
                            })).done(function(data) {
                                Gikam.getComp('core-bpmn-draft-list-grid').refresh();
                            });
                        }
                    })
                }
            } ],
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
                        Gikam.preInsert({
                            modalTitle : 'CORE.MODULE.SYS.T_CORE_BPMN_DRAFT.TAB.ADD_BASEINFO',
                            fields : coreBpmnDraft.getBaseInfoFormFields(),
                            url : coreBpmnDraft.baseUrl,
                        }).done(function(id) {
                            _this.loadDetailPage(id);
                        });
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
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.IMPORT',
                    icon : 'upload',
                    onClick : function() {
                        Gikam.import({
                            url : IFM_CONTEXT + '/core/module/item/files/{id}/action/import/coreBpmnImportFileFunction',
                            dbTable : 'T_CORE_BPMN_DRAFT',
                            bizId : 'input',
                            autoClose : true,
                            onImportSuccess : function() {
                                Gikam.getComp('core-bpmn-draft-list-grid').refresh();
                            }
                        });
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
                items : [ _this.getGridParam() ]
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
            url : IFM_CONTEXT + '/secure/core/module/sys/bpmn-procs/page/choose-list' + Gikam.param({
                service : 'modifiable',
                single : true
            }),
            onAfterClose : function(row) {
                if (row) {
                    var insertUrl = Gikam.printf(IFM_CONTEXT + '/secure/core/module/sys/bpmn-drafts/source-procs/{procId}', {
                        procId : row[0].id
                    });

                    workspace.window.showMask(true);
                    Gikam.postText(insertUrl).done(function(data) {
                        _this.loadDetailPage(data);
                    }).always(function() {
                    	workspace.window.closeMask();
                    });
                }
            }
        });
    },

    init : function() {
        this.create();
    }
};