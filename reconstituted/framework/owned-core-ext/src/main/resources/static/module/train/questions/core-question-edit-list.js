coreQuestion.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-question-edit-list-grid',
            url : coreQuestion.baseUrl + '/queries/raw',
            requestData : {
                lastSuspendedFlag_SEQ : '0'
            },
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.TRAIN.QUESTION.BUTTON.ADD',
                icon : 'add',
                onClick : function() {
                    _this.loadQuestionBankPage();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    _this.deleteQuestion();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.TRAIN.QUESTION.BUTTON.COPY',
                icon : 'copy',
                onClick : function() {
                    _this.clone();
                }
            }, {
                id : 'core-question-detail-btn-activate',
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function() {
                    _this.activate();
                }
            }, {
                id : 'core-question-detail-btn-activate',
                type : 'button',
                text : 'GIKAM.BUTTON.DEACTIVE',
                icon : 'disable',
                onClick : function() {
                    _this.deactivate();
                }
            } ],
            columns : coreQuestion.getEditListColumns(),
            onCellClick : function(field, row) {
                field === 'questionContent' && _this.loadDetailPage(row.id);
            }
        }
    },
    
    deleteQuestion : function() {
        var selectedRows = Gikam.getComp('core-question-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selectedRows)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
            return;
        }

        var url = coreQuestion.baseUrl + '/action/suspend';

        workspace.window.showMask(true);
        Gikam.put(url, Gikam.getJsonWrapper(null, [ '', selectedRows ])).done(function(r) {
            Gikam.getComp('core-question-edit-list-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },
    
    clone : function() {
        var selectedRows = Gikam.getComp('core-question-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selectedRows)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
            return;
        }

        var url = coreQuestion.baseUrl + '/action/clone';

        workspace.window.showMask(true);
        Gikam.put(url, Gikam.getJsonWrapper(null, [ '', selectedRows ])).done(function(r) {
            Gikam.getComp('core-question-edit-list-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },
    
    activate : function() {
        var selectedRows = Gikam.getComp('core-question-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selectedRows)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
            return;
        }
        
        var validFlag = true;
        for (var i = 0; i < selectedRows.length; i++) {
            var item = selectedRows[i];
            if (item.activatedFlag === '1') {
                validFlag = false;
                break;
            }
        }

        if (!validFlag) {
            Gikam.alert("CORE.MODULE.TRAIN.QUESTION.TIP.QUESTION_ALREADY_ACTIVATED");
            return;
        }
        
        var url = coreQuestion.baseUrl + '/action/activate';
        workspace.window.showMask(true);
        Gikam.put(url, Gikam.getJsonWrapper(null, [ '', selectedRows ])).done(function(r) {
            Gikam.getComp('core-question-edit-list-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },
    
    deactivate : function() {
        var selectedRows = Gikam.getComp('core-question-edit-list-grid').getSelections();
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
            Gikam.alert("CORE.MODULE.TRAIN.QUESTION.TIP.QUESTION_ALREADY_DEACTIVATED");
            return;
        }
        
        var url = coreQuestion.baseUrl + '/action/deactivate';
        workspace.window.showMask(true);
        Gikam.put(url, Gikam.getJsonWrapper(null, [ '', selectedRows ])).done(function(r) {
            Gikam.getComp('core-question-edit-list-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'glp-question-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    loadQuestionBankPage : function() {
        workspace.window.load(coreQuestion.baseUrl + '/page/question-bank-detail');
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreQuestion.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function(param) {
        var _this = this;
        _this.param = param;
        _this.create();
    }
};