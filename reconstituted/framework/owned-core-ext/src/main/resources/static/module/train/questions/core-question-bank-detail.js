coreQuestion.questionBankDetailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                id : 'core-question-bank-detail-btn-back',
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.TRAIN.QUESTION_BANK.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-question-bank-detail-base-info-form',
                    fields : coreQuestion.getQuestionBankFormFields()
                } ]
            } ]
        }
    },

    getQuestionBankQuestionTabParam : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'core-question-bank-detail-question-tab',
            width : Gikam.width.L,
            panels : [ {
                title : 'CORE.MODULE.TRAIN.QUESTION.TAB.SINGLECHOICE',
                id : 'core-question-bank-detail-singlechoice-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-question-bank-detail-singlechoice-list-grid',
                    url : coreQuestion.baseUrl + '/queries/raw',
                    requestData : {
                        questionType_SEQ : 'singleChoice',
                        lastSuspendedFlag_SEQ : '0'
                    },
                    service : 'coreQuestionServiceImpl',
                    columns : coreQuestion.getQuestionBankQuestionListColumns(),
                    onRowActive : function(index, row) {
                        var _this = this;
                        if (row) {
                            Gikam.getComp('core-question-detail-question-opt-list-grid').refresh({
                                url : Gikam.printf(coreQuestion.baseUrl + '/{id}/opts/queries', {
                                    id : row.id
                                })
                            });
                        }
                    },
                    deleteFormatter : function(row) {
                        return row.questionContent || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            var questionCategory = Gikam.getComp('core-question-bank-detail-base-info-form').getData().questionCategory;
                            _this.insertQuestionRow('singlechoice', questionCategory);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteQuestionRow('singlechoice');
                        }
                    } ]
                } ]
            }, {
                title : 'CORE.MODULE.TRAIN.QUESTION.TAB.MULTIPLECHOICE',
                id : 'core-question-bank-detail-multiplechoice-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-question-bank-detail-multiplechoice-list-grid',
                    url : coreQuestion.baseUrl + '/queries/raw',
                    requestData : {
                        questionType_SEQ : 'multipleChoice',
                        lastSuspendedFlag_SEQ : '0'
                    },
                    service : 'coreQuestionServiceImpl',
                    columns : coreQuestion.getQuestionBankQuestionListColumns(),
                    onRowActive : function(index, row) {
                        if (row) {
                            Gikam.getComp('core-question-detail-question-opt-list-grid').refresh({
                                url : Gikam.printf(coreQuestion.baseUrl + '/{id}/opts/queries', {
                                    id : row.id,
                                })
                            });
                        }
                    },
                    deleteFormatter : function(row) {
                        return row.questionContent || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            var questionCategory = Gikam.getComp('core-question-bank-detail-base-info-form').getData().questionCategory;
                            _this.insertQuestionRow('multiplechoice', questionCategory);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteQuestionRow('multiplechoice');
                        }
                    } ]
                } ]
            }, {
                title : 'CORE.MODULE.TRAIN.QUESTION.TAB.COMPLETION',
                id : 'core-question-bank-detail-completion-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-question-bank-detail-completion-list-grid',
                    url : coreQuestion.baseUrl + '/queries/raw',
                    requestData : {
                        questionType_SEQ : 'completion',
                        lastSuspendedFlag_SEQ : '0'
                    },
                    service : 'coreQuestionServiceImpl',
                    columns : coreQuestion.getQuestionBankQuestionListColumns(),
                    deleteFormatter : function(row) {
                        return row.questionContent || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            var questionCategory = Gikam.getComp('core-question-bank-detail-base-info-form').getData().questionCategory;
                            _this.insertQuestionRow('completion', questionCategory);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteQuestionRow('completion');
                        }
                    } ]
                } ]
            }, {
                title : 'CORE.MODULE.TRAIN.QUESTION.TAB.JUDGEMENT',
                id : 'core-question-bank-detail-judgement-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-question-bank-detail-judgement-list-grid',
                    url : coreQuestion.baseUrl + '/queries/raw',
                    requestData : {
                        questionType_SEQ : 'judgement',
                        lastSuspendedFlag_SEQ : '0'
                    },
                    service : 'coreQuestionServiceImpl',
                    columns : coreQuestion.getQuestionBankJudgementQuestionListColumns(),
                    deleteFormatter : function(row) {
                        return row.questionContent || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            var questionCategory = Gikam.getComp('core-question-bank-detail-base-info-form').getData().questionCategory;
                            _this.insertQuestionRow('judgement', questionCategory);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteQuestionRow('judgement');
                        }
                    } ]
                } ]
            }, {
                title : 'CORE.MODULE.TRAIN.QUESTION.TAB.SHORTANSWER',
                id : 'core-question-bank-detail-shortanswer-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-question-bank-detail-shortanswer-list-grid',
                    url : coreQuestion.baseUrl + '/queries/raw',
                    requestData : {
                        questionType_SEQ : 'shortAnswer',
                        lastSuspendedFlag_SEQ : '0'
                    },
                    service : 'coreQuestionServiceImpl',
                    columns : coreQuestion.getQuestionBankShortAnswerQuestionListColumns(),
                    deleteFormatter : function(row) {
                        return row.questionContent || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            var questionCategory = Gikam.getComp('core-question-bank-detail-base-info-form').getData().questionCategory;
                            _this.insertQuestionRow('shortanswer', questionCategory);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteQuestionRow('shortanswer');
                        }
                    } ]
                } ]
            } ],
            onActived : function(data) {
                var activedQuestionRow = null;

                if (data.index === 0) {
                    Gikam.getComp('core-question-bank-detail-question-opt-tab').show();
                    _this.questionTab = 'singleChoice';
                    activedQuestionRow = Gikam.getComp('core-question-bank-detail-singlechoice-list-grid').getActivedRow();
                } else if (data.index === 1) {
                    Gikam.getComp('core-question-bank-detail-question-opt-tab').show();
                    _this.questionTab = 'multipleChoice';
                    activedQuestionRow = Gikam.getComp('core-question-bank-detail-multiplechoice-list-grid').getActivedRow();
                } else if (data.index === 2) {
                    Gikam.getComp('core-question-bank-detail-question-opt-tab').hide();
                    _this.questionTab = 'judgement';
                } else if (data.index === 3) {
                    Gikam.getComp('core-question-bank-detail-question-opt-tab').hide();
                    _this.questionTab = 'shortAnswer';
                }
                if (Gikam.isNotEmpty(activedQuestionRow)) {
                    Gikam.getComp('core-question-detail-question-opt-list-grid').refresh({
                        url : Gikam.printf(coreQuestion.baseUrl + '/{id}/opts/queries', {
                            id : activedQuestionRow.id,
                        })
                    });
                } else {
                    Gikam.getComp('core-question-detail-question-opt-list-grid').cleanData();
                }
            }
        }
    },

    getQuestionOptTabParam : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'core-question-bank-detail-question-opt-tab',
            width : Gikam.width.L,
            panels : [ {
                title : 'CORE.MODULE.TRAIN.QUESTION.TAB.QUESTION_OPT',
                id : 'core-question-detail-question-opt-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-question-detail-question-opt-list-grid',
                    service : 'coreQuestionOptServiceImpl',
                    columns : coreQuestion.getOptColumns(),
                    deleteFormatter : function(row) {
                        return row.optCode || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            _this.insertOptRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteOptRow();
                        }
                    } ],
                    page : false
                } ]
            } ]
        }
    },

    insertQuestionRow : function(gridType, questionCategory) {
        var questionCategory = Gikam.getComp('core-question-bank-detail-base-info-form').getData().questionCategory;
        if (Gikam.isEmpty(questionCategory)) {
            Gikam.alert('CORE.MODULE.TRAIN.QUESTION.TIP.EXAMCATEGORY_CANNOT_EMPTY');
            return;
        }

        var url = coreQuestion.baseUrl;
        if (gridType === 'singlechoice') {
            Gikam.getComp('core-question-bank-detail-singlechoice-list-grid').insert(url, [ {
                questionCategory : questionCategory,
                questionType : 'singleChoice',
                questionTypeOrderNo : 1
            } ]);
        } else if (gridType === 'multiplechoice') {
            Gikam.getComp('core-question-bank-detail-multiplechoice-list-grid').insert(url, [ {
                questionCategory : questionCategory,
                questionType : 'multipleChoice',
                questionTypeOrderNo : 2
            } ]);
        } else if (gridType === 'completion') {
            Gikam.getComp('core-question-bank-detail-completion-list-grid').insert(url, [ {
                questionCategory : questionCategory,
                questionType : 'completion',
                questionTypeOrderNo : 3
            } ]);
        } else if (gridType === 'judgement') {
            Gikam.getComp('core-question-bank-detail-judgement-list-grid').insert(url, [ {
                questionCategory : questionCategory,
                questionType : 'judgement',
                questionTypeOrderNo : 4
            } ]);
        } else if (gridType === 'shortanswer') {
            Gikam.getComp('core-question-bank-detail-shortanswer-list-grid').insert(url, [ {
                questionCategory : questionCategory,
                questionType : 'shortAnswer',
                questionTypeOrderNo : 5
            } ]);
        }
    },

    deleteQuestionRow : function(gridType) {
        var url = coreQuestion.baseUrl;
        var gridId = null;
        if (gridType === 'singlechoice') {
            gridId = 'core-question-bank-detail-singlechoice-list-grid';
        } else if (gridType === 'multiplechoice') {
            gridId = 'core-question-bank-detail-multiplechoice-list-grid';
        } else if (gridType === 'judgement') {
            gridId = 'core-question-bank-detail-judgement-list-grid';
        } else if (gridType === 'shortanswer') {
            gridId = 'core-question-bank-detail-shortanswer-list-grid';
        } else if (gridType === 'completion') {
            gridId = 'core-question-bank-detail-completion-list-grid';
        }

        var selectedRows = Gikam.getComp(gridId).getSelections();
        if (Gikam.isEmpty(selectedRows)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
            return;
        }

        var url = coreQuestion.baseUrl + '/action/suspend';

        workspace.window.showMask(true);
        Gikam.put(url, Gikam.getJsonWrapper(null, [ '', selectedRows ])).done(function(r) {
            Gikam.getComp(gridId).refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    insertOptRow : function() {
        var _this = this;

        var questionRow = null;

        if (_this.questionTab === 'singleChoice') {
            questionRow = Gikam.getComp('core-question-bank-detail-singlechoice-list-grid').getActivedRow();
        } else if (_this.questionTab === 'multipleChoice') {
            questionRow = Gikam.getComp('core-question-bank-detail-multiplechoice-list-grid').getActivedRow();
        }

        if (Gikam.isEmpty(questionRow)) {
            Gikam.alert('CORE.MODULE.TRAIN.QUESTION.TIP.QUESTION_ACTIVED_CANNOT_EMPTY');
            return;
        }
        
        if (questionRow.activatedFlag === '1') {
            Gikam.alert("CORE.MODULE.TRAIN.QUESTION.TIP.QUESTION_OPT_VALIDATOR");
            return;
        }

        var url = Gikam.printf(coreQuestion.baseUrl + '/{id}/opts', {
            id : questionRow.id,
        });
        Gikam.getComp('core-question-detail-question-opt-list-grid').insert(url, [ {
            questionId : questionRow.id
        } ]);
    },
    
    deleteOptRow : function() {
        var selectedRows = Gikam.getComp('core-question-detail-question-opt-list-grid').getSelections();
        if (Gikam.isEmpty(selectedRows)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
            return;
        }
        
        var validFlag = true;
        for (var i = 0; i < selectedRows.length; i++) {
            var item = selectedRows[i];
            if (item.ext$.activatedflag === '1') {
                validFlag = false;
                break;
            }
        }
        if (!validFlag) {
            Gikam.alert("CORE.MODULE.TRAIN.QUESTION.TIP.QUESTION_OPT_VALIDATOR");
            return;
        }
        Gikam.getComp('core-question-detail-question-opt-list-grid').deleteRows(coreQuestion.baseUrl + '/opts');
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            center : {
                items : [ _this.getBtnToolbar(), _this.getBaseInfoTab(), _this.getQuestionBankQuestionTabParam(), _this.getQuestionOptTabParam() ]
            },
            renderTo : workspace.window.$dom
        });
        return this;
    },

    init : function(param) {
        var _this = this;
        _this.param = param;
        _this.create();
    }
}