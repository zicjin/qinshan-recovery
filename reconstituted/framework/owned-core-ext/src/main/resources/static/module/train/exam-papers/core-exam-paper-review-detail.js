coreExamPaper.reviewDetailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'CORE.MODULE.TRAIN.EXAM_PAPER.BUTTON.COMPLETE_REVIEW',
                id : 'core-exam-paper-review-detail-btn-active',
                icon : 'submit',
                onClick : function() {
                    _this.activate();
                }
            }, {
                id : 'core-exam-paper-review-detail-btn-back',
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
                title : 'CORE.MODULE.TRAIN.EXAM.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-exam-paper-review-detail-base-info-form',
                    url : Gikam.printf(coreExamPaper.baseUrl + '/{id}', {
                        id : _this.param.id
                    }),
                    fields : coreExamPaper.getBaseInfoFormFields()
                } ]
            } ]
        }
    },
    
    activate : function() {
        var _this = this;
        var form = Gikam.getComp('core-exam-paper-review-detail-base-info-form');
        var url = coreExamPaper.baseUrl + '/action/activate';
        workspace.window.showMask(true);
        Gikam.put(url, Gikam.getJsonWrapper(null, [ '', [ form.getData() ] ])).done(function(r) {
            workspace.window.goBack();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    getExamPaperAnswerTabParam : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'core-exam-paper-review-detail-answer-tab',
            width : Gikam.width.L,
            panels : [ {
                title : 'CORE.MODULE.TRAIN.EXAM_PAPER.TAB.SHORTANSWER',
                id : 'core-exam-paper-review-detail-shortanswer-answer-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-exam-paper-review-detail-shortanswer-answer-list-grid',
                    url : Gikam.printf(coreExamPaper.baseUrl + '/{id}/answers/queries', {
                        id : _this.param.id
                    }),
                    requestData : {
                        questionType_SEQ : 'shortAnswer'
                    },
                    service : 'coreExamPaperAnswerServiceImpl',
                    columns : coreExamPaper.getExamPaperShortAnswerListColumns(),
                    onBeforeUpdate : function(row, key) {
                        var score = parseInt(row.score);
                        var shortAnswerScore = parseInt(row.row.ext$.shortanswerscore);
                        if (score > shortAnswerScore) {
                            Gikam.alert('CORE.MODULE.TRAIN.EXAM_PAPER.TIP.SCORE_CANNOT_LARGER_THAN_TMPLSCORE');
                            return false;
                        }
                        return true;
                    },
                    onUpdated : function() {
                        Gikam.getComp('core-exam-paper-review-detail-base-info-form').refresh({
                            url : Gikam.printf(coreExamPaper.baseUrl + '/{id}', {
                                id : _this.param.id
                            }),
                        });
                    }
                } ]
            }, {
                title : 'CORE.MODULE.TRAIN.EXAM_PAPER.TAB.SINGLECHOICE',
                id : 'core-exam-paper-review-detail-singlechoice-answer-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-exam-paper-review-detail-singlechoice-answer-list-grid',
                    url : Gikam.printf(coreExamPaper.baseUrl + '/{id}/answers/queries', {
                        id : _this.param.id
                    }),
                    requestData : {
                        questiontype_SEQ : 'singleChoice'
                    },
                    columns : coreExamPaper.getExamPaperSingleChoiceAnswerListColumns(),
                    onRowActive : function(index, row) {
                        if (row) {
                            Gikam.getComp('core-exam-paper-review-detail-question-opt-list-grid').refresh({
                                url : IFM_CONTEXT + Gikam.printf('/secure/core/module/train/questions/{id}/opts/queries', {
                                    id : row.questionId,
                                })
                            });
                        }
                    }
                } ]
            }, {
                title : 'CORE.MODULE.TRAIN.EXAM_PAPER.TAB.MULTIPLECHOICE',
                id : 'core-exam-paper-review-detail-multiplechoice-answer-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-exam-paper-review-detail-multiplechoice-answer-list-grid',
                    url : Gikam.printf(coreExamPaper.baseUrl + '/{id}/answers/queries', {
                        id : _this.param.id
                    }),
                    requestData : {
                        questiontype_SEQ : 'multipleChoice'
                    },
                    columns : coreExamPaper.getExamPaperMultipleChoiceAnswerListColumns(),
                    onRowActive : function(index, row) {
                        if (row) {
                            Gikam.getComp('core-exam-paper-review-detail-question-opt-list-grid').refresh({
                                url :IFM_CONTEXT +  Gikam.printf('/secure/core/module/train/questions/{id}/opts/queries', {
                                    id : row.questionId,
                                })
                            });
                        }
                    }
                } ]
            }, {
                title : 'CORE.MODULE.TRAIN.QUESTION.TAB.COMPLETION',
                id : 'core-exam-paper-review-detail-completion-answer-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-exam-paper-review-detail-completion-answer-list-grid',
                    url : Gikam.printf(coreExamPaper.baseUrl + '/{id}/answers/queries', {
                        id : _this.param.id
                    }),
                    requestData : {
                        questiontype_SEQ : 'completion'
                    },
                    columns : coreExamPaper.getExamPaperCompletionAnswerListColumns()
                } ]
            }, {
                title : 'CORE.MODULE.TRAIN.EXAM_PAPER.TAB.JUDGEMENT',
                id : 'core-exam-paper-review-detail-judgement-answer-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-exam-paper-review-detail-judgement-answer-list-grid',
                    url : Gikam.printf(coreExamPaper.baseUrl + '/{id}/answers/queries', {
                        id : _this.param.id
                    }),
                    requestData : {
                        questiontype_SEQ : 'judgement'
                    },
                    columns : coreExamPaper.getExamPaperJudgementAnswerListColumns()
                } ]
            } ],
            onActived : function(data) {
                var activedQuestionRow = null;
                if (data.index === 0) {
                    Gikam.getComp('core-exam-paper-review-detail-question-opt-tab').hide();
                    _this.questionTab = 'shortAnswer';
                } else if (data.index === 1) {
                    Gikam.getComp('core-exam-paper-review-detail-question-opt-tab').show();
                    _this.questionTab = 'singleChoice';
                    activedQuestionRow = Gikam.getComp('core-exam-paper-review-detail-singlechoice-answer-list-grid').getActivedRow();
                } else if (data.index === 2) {
                    Gikam.getComp('core-exam-paper-review-detail-question-opt-tab').show();
                    _this.questionTab = 'multipleChoice';
                    activedQuestionRow = Gikam.getComp('core-exam-paper-review-detail-multiplechoice-answer-list-grid').getActivedRow();
                } else if (data.index === 3) {
                    Gikam.getComp('core-exam-paper-review-detail-question-opt-tab').hide();
                    _this.questionTab = 'completion';
                } else if (data.index === 4) {
                    Gikam.getComp('core-exam-paper-review-detail-question-opt-tab').hide();
                    _this.questionTab = 'judgement';
                }
                if (Gikam.isNotEmpty(activedQuestionRow)) {
                    Gikam.getComp('core-exam-paper-review-detail-question-opt-list-grid').refresh({
                        url :IFM_CONTEXT +  Gikam.printf('/secure/core/module/train/questions/{id}/opts/queries', {
                            id : activedQuestionRow.questionId
                        })
                    });
                } else {
                    Gikam.getComp('core-exam-paper-review-detail-question-opt-list-grid').cleanData();
                }
            }
        }
    },

    getQuestionOptTabParam : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'core-exam-paper-review-detail-question-opt-tab',
            width : Gikam.width.L,
            panels : [ {
                title : 'CORE.MODULE.TRAIN.EXAM_PAPER.TAB.QUESTION_OPT',
                id : 'core-exam-paper-review-detail-question-opt-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-exam-paper-review-detail-question-opt-list-grid',
                    columns : coreExamPaper.getQuestionOptColumns(),
                    page : false
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            center : {
                items : [ _this.getBtnToolbar(), _this.getBaseInfoTab(), _this.getExamPaperAnswerTabParam(), _this.getQuestionOptTabParam() ]
            },
            renderTo : workspace.window.$dom,
            pageConfig : {
                pageObject : coreExamPaper,
                bizId : _this.param.id
            }
        });
        return this;
    },

    init : function(param) {
        var _this = this;
        _this.param = param;
        _this.create();
    }
}