coreExamTmpl.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-exam-tmpl-edit-list-grid',
            url : coreExamTmpl.baseUrl + '/queries/raw',
            deleteFormatter : function(row) {
                return row.tmplName || row.id;
            },
            activateFormatter : function(row) {
                return row.tmplName || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.TRAIN.EXAM_TMPL.BUTTON.ADD',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.TRAIN.EXAM_TMPL.MODAL.TITLE.ADD_EXAM_TMPL',
                        fields : coreExamTmpl.getBaseInfoFormFields(),
                        url : coreExamTmpl.baseUrl,
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    _this.deleteTmpl();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.TRAIN.QUESTION.BUTTON.COPY',
                icon : 'copy',
                onClick : function() {
                    _this.clone();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function() {
                    Gikam.getComp('core-exam-tmpl-edit-list-grid').activateRows(coreExamTmpl.baseUrl + '/action/activate');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DEACTIVE',
                icon : 'disable',
                onClick : function() {
                    var selectedRows = Gikam.getComp('core-exam-tmpl-edit-list-grid').getSelections();
                    if (Gikam.isEmpty(selectedRows)) {
                        Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
                        return;
                    }
                    
                    var validFlag = true;
                    for (var i = 0; i < selectedRows.length; i++) {
                        var item = selectedRows[i];
                        if (item.activatedFlag === '0') {
                            validFlag = false;
                            break;
                        }
                    }
                    if (!validFlag) {
                        Gikam.alert("CORE.MODULE.TRAIN.EXAM_TMPL.TIP.DEACTIVATE_VALIDATOR");
                        return;
                    }
                    Gikam.getComp('core-exam-tmpl-edit-list-grid').deactivateRows(coreExamTmpl.baseUrl + '/action/deactivate');
                }
            } ],
            columns : coreExamTmpl.getEditListColumns(),
            onCellClick : function(field, row) {
                field === 'tmplName' && _this.loadDetailPage(row.id);
            }
        }
    },
    
    deleteTmpl : function() {
        var selectedRows = Gikam.getComp('core-exam-tmpl-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selectedRows)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
            return;
        }
        
        Gikam.post(coreExamTmpl.baseUrl + '/action/delete-validate', Gikam.getJsonWrapper({}, [ '', selectedRows ])).done(function(result) {
            if (!result.valid) {
                Gikam.alert(result.message);
                return;
            }
            Gikam.getComp('core-exam-tmpl-edit-list-grid').deleteRows(coreExamTmpl.baseUrl);
        });
    },
    
    clone : function() {
        var selectedRows = Gikam.getComp('core-exam-tmpl-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selectedRows)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
            return;
        }

        var url = coreExamTmpl.baseUrl + '/action/clone';

        workspace.window.showMask(true);
        Gikam.put(url, Gikam.getJsonWrapper(null, [ '', selectedRows ])).done(function(r) {
            Gikam.getComp('core-exam-tmpl-edit-list-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreExamTmpl.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-exam-tmpl-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        var _this = this;
        _this.param = param;
        _this.create();
    }
};