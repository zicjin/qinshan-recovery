var coreQuestion = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/train/questions',

    getEditListColumns : function() {
        return [ {
            checkbox : true,
        }, Gikam.status.getActiveColumn(), {
            field : 'questionCategory',
            title : 'T_CORE_QUESTION.QUESTIONCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreQuestionCategory',
                codeClassId : 'biz'
            }
        }, {
            field : 'questionType',
            title : 'T_CORE_QUESTION.QUESTIONTYPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreQuestionType'
            }
        }, {
            field : 'questionContent',
            title : 'T_CORE_QUESTION.QUESTIONCONTENT',
            type : 'link'
        }, {
            field : 'answer',
            title : 'T_CORE_QUESTION.ANSWER',
            formatter : function(i, v, row) {
                if (row.questionType === 'judgement') {
                    if (v === 'correct') {
                        return Gikam.propI18N('GIKAM.SELECT.CORRECT');
                    } else if (v === 'incorrect') {
                        return Gikam.propI18N('GIKAM.SELECT.INCORRECT');
                    } 
                }
                return v;
            }
        }, {
            field : 'lastUpdatedByName',
            title : 'T_CORE_QUESTION.LASTUPDATEDBYNAME'
        }, {
            field : 'lastUpdatedTime',
            title : 'T_CORE_QUESTION.LASTUPDATEDTIME'
        } ];
    },

    getQuestionBankQuestionListColumns : function() {
        return [ {
            checkbox : true,
        }, Gikam.status.getActiveColumn(), {
            field : 'questionContent',
            title : 'T_CORE_QUESTION.QUESTIONCONTENT',
            editor : true,
            onBeforeEditorRender : function(row) {
                if (row.activatedFlag === '1') {
                    return false;
                } else {
                    return true;
                }
            }
        }, {
            field : 'answer',
            title : 'T_CORE_QUESTION.ANSWER',
            editor : true,
            onBeforeEditorRender : function(row) {
                if (row.activatedFlag === '1') {
                    return false;
                } else {
                    return true;
                }
            }
        }, {
            field : 'lastUpdatedByName',
            title : 'T_CORE_QUESTION.LASTUPDATEDBYNAME'
        }, {
            field : 'lastUpdatedTime',
            title : 'T_CORE_QUESTION.LASTUPDATEDTIME'
        } ];
    },
    
    getQuestionBankJudgementQuestionListColumns : function() {
        return [ {
            checkbox : true,
        }, Gikam.status.getActiveColumn(), {
            field : 'questionContent',
            title : 'T_CORE_QUESTION.QUESTIONCONTENT',
            editor : true,
            onBeforeEditorRender : function(row) {
                if (row.activatedFlag === '1') {
                    return false;
                } else {
                    return true;
                }
            }
        }, {
            field : 'answer',
            title : 'T_CORE_QUESTION.ANSWER',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreJudgementOption'
            },
            editor : true,
            onBeforeEditorRender : function(row) {
                console.log(row.activatedFlag === '1')
                if (row.activatedFlag === '1') {
                    return false;
                } else {
                    return true;
                }
            },
            onChange : function(filed, value, index) {
                var grid = Gikam.getComp('core-question-bank-detail-judgement-list-grid');
                var id = grid.getData()[index].id;
                grid.setData({
                    id : id,
                    judgementAnswer : value,
                    answer : value
                });
            }
        }, {
            field : 'lastUpdatedByName',
            title : 'T_CORE_QUESTION.LASTUPDATEDBYNAME'
        }, {
            field : 'lastUpdatedTime',
            title : 'T_CORE_QUESTION.LASTUPDATEDTIME'
        } ];
    },

    getQuestionBankShortAnswerQuestionListColumns : function() {
        return [ {
            checkbox : true,
        }, Gikam.status.getActiveColumn(), {
            field : 'questionContent',
            title : 'T_CORE_QUESTION.QUESTIONCONTENT',
            editor : true,
            onBeforeEditorRender : function(row) {
                if (row.activatedFlag === '1') {
                    return false;
                } else {
                    return true;
                }
            }
        }, {
            field : 'answer',
            title : 'T_CORE_QUESTION.ANSWER',
            editor : true,
            type : 'richText',
            onBeforeEditorRender : function(row) {
                if (row.activatedFlag === '1') {
                    return false;
                } else {
                    return true;
                }
            }
        }, {
            field : 'lastUpdatedByName',
            title : 'T_CORE_QUESTION.LASTUPDATEDBYNAME'
        }, {
            field : 'lastUpdatedTime',
            title : 'T_CORE_QUESTION.LASTUPDATEDTIME'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'questionCategory',
            title : 'T_CORE_QUESTION.QUESTIONCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreQuestionCategory',
                codeClassId : 'biz'
            },
            validators : [ 'notEmpty' ]
        }, {
            field : 'questionType',
            title : 'T_CORE_QUESTION.QUESTIONTYPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreQuestionType'
            },
            readonly : true
        }, {
            field : 'questionContent',
            title : 'T_CORE_QUESTION.QUESTIONCONTENT',
            colspan : 3,
            validators : [ 'notEmpty' ]
        }, {
            field : 'answer',
            title : 'T_CORE_QUESTION.ANSWER',
            colspan : 3,
            type : 'textarea',
            height : 50,
            validators : [ 'notEmpty' ]
        }, {
            field : 'judgementAnswer',
            title : 'T_CORE_QUESTION.ANSWER',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreJudgementOption'
            },
            onChange : function(field, value) {
                Gikam.getComp('core-question-detail-base-info-form').setData({
                    answer : value,
                    judgementAnswer : value
                });
            }
        } ];
    },

    getQuestionBankFormFields : function() {
        return [ {
            field : 'questionCategory',
            title : 'T_CORE_QUESTION.QUESTIONCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreQuestionCategory',
                codeClassId : 'biz'
            },
            onChange : function(field, value) {
                var questionCategory = value;
                var url = coreQuestion.baseUrl + '/queries/raw'
                Gikam.getComp('core-question-bank-detail-singlechoice-list-grid').refresh({
                    url : url,
                    requestData : {
                        questionType_SEQ : 'singleChoice',
                        questionCategory_SEQ : value
                    }
                });
                Gikam.getComp('core-question-bank-detail-multiplechoice-list-grid').refresh({
                    url : url,
                    requestData : {
                        questionType_SEQ : 'multipleChoice',
                        questionCategory_SEQ : value
                    }
                });
                Gikam.getComp('core-question-bank-detail-completion-list-grid').refresh({
                    url : url,
                    requestData : {
                        questionType_SEQ : 'completion',
                        questionCategory_SEQ : value
                    }
                });
                Gikam.getComp('core-question-bank-detail-judgement-list-grid').refresh({
                    url : url,
                    requestData : {
                        questionType_SEQ : 'judgement',
                        questionCategory_SEQ : value
                    }
                });
                Gikam.getComp('core-question-bank-detail-shortanswer-list-grid').refresh({
                    url : url,
                    requestData : {
                        questionType_SEQ : 'shortAnswer',
                        questionCategory_SEQ : value
                    }
                });
                Gikam.getComp('core-question-detail-question-opt-list-grid').cleanData();
            }
        } ];
    },

    getOptColumns : function() {
        return [ {
            checkbox : true,
        }, {
            field : 'optCode',
            title : 'T_CORE_QUESTION_OPT.OPTCODE',
            editor : true,
            onBeforeEditorRender : function(row) {
                if (row.ext$.activatedflag === '1') {
                    return false;
                } else {
                    return true;
                }
            }
        }, {
            field : 'optContent',
            title : 'T_CORE_QUESTION_OPT.OPTCONTENT',
            editor : true,
            onBeforeEditorRender : function(row) {
                if (row.ext$.activatedflag === '1') {
                    return false;
                } else {
                    return true;
                }
            }
        } ];
    }
});