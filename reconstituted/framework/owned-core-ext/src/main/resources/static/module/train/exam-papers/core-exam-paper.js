var coreExamPaper = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/train/exam-papers',

    getEditListColumns : function() {
        return [ {
            radio : true
        }, {
            field : 'status',
            title : 'T_CORE_EXAM_PAPER.STATUS',
            type : 'select',
            category : 'exam-paper-status'
        }, {
            field : 'ext$.examname',
            title : 'T_CORE_EXAM.EXAMNAME'
        }, {
            field : 'ext$.totalscore',
            title : 'T_CORE_EXAM_TMPL.TOTALSCORE'
        }, {
            field : 'ext$.exambegintime',
            title : 'T_CORE_EXAM.EXAMBEGINTIME'
        }, {
            field : 'ext$.examendtime',
            title : 'T_CORE_EXAM.EXAMENDTIME'
        }, {
            field : 'actBeginTime',
            title : 'T_CORE_EXAM_PAPER.ACTBEGINTIME'
        }, {
            field : 'actEndTime',
            title : 'T_CORE_EXAM_PAPER.ACTENDTIME'
        }, {
            field : 'score',
            title : 'T_CORE_EXAM_PAPER.SCORE'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'ext$.examname',
            title : 'T_CORE_EXAM.EXAMNAME',
            readonly : true
        }, {
            field : 'userName',
            title : 'T_CORE_EXAM_PAPER.USERNAME',
            readonly : true
        }, {
            field : 'ext$.totalscore',
            title : 'T_CORE_EXAM_TMPL.TOTALSCORE',
            readonly : true
        }, {
            field : 'actBeginTime',
            title : 'T_CORE_EXAM_PAPER.ACTBEGINTIME',
            type : 'dateTime',
            readonly : true
        }, {
            field : 'actEndTime',
            title : 'T_CORE_EXAM_PAPER.ACTENDTIME',
            type : 'dateTime',
            readonly : true
        }, {
            field : 'score',
            title : 'T_CORE_EXAM_PAPER.SCORE',
            readonly : true
        } ];
    },

    getAnswerFields : function() {
        return [ {
            field : 'ext$.questiontype',
            title : 'T_CORE_QUESTION.QUESTIONTYPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreQuestionType'
            },
            readonly : true
        }, {
            field : 'ext$.questioncontent',
            title : 'T_CORE_QUESTION.QUESTIONCONTENT',
            colspan : 3,
            readonly : true
        }, {
            field : 'answer',
            title : 'T_CORE_EXAM_PAPER_ANSWER.ANSWER',
            colspan : 3,
            type : 'textarea',
            height : 50
        }, {
            field : 'judgementAnswer',
            title : 'T_CORE_QUESTION.ANSWER',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreJudgementOption'
            },
            onChange : function(field, value) {
                Gikam.getComp('core-exam-paper-detail-exam-paper-answer-form').setData({
                    answer : value,
                    judgementAnswer : value
                });
            }
        } ];
    },

    getQuestionOptColumns : function() {
        return [ {
            field : 'optCode',
            title : 'T_CORE_QUESTION_OPT.OPTCODE',
            contentAlign : 'center'
        }, {
            field : 'optContent',
            title : 'T_CORE_QUESTION_OPT.OPTCONTENT'
        } ];
    },

    getAnswerCardColumns : function() {
        return [ {
            field : 'columnValue1',
            title : '',
            contentAlign : 'center',
            width : 100,
            tagFormatter : function(row) {
                if (Gikam.isNotEmpty(row.columnValue1)) {
                    var type = 'info';
                    if (row.answered1 === '1') {
                        type = 'danger';
                    }
                    return {
                        type : type,
                        value : row.columnValue1
                    }
                }
            }
        }, {
            field : 'columnValue2',
            title : '',
            contentAlign : 'center',
            width : 100,
            tagFormatter : function(row) {
                if (Gikam.isNotEmpty(row.columnValue2)) {
                    var type = 'info';
                    if (row.answered2 === '1') {
                        type = 'danger';
                    }
                    return {
                        type : type,
                        value : row.columnValue2
                    }
                }
            }
        }, {
            field : 'columnValue3',
            title : '',
            contentAlign : 'center',
            width : 100,
            tagFormatter : function(row) {
                if (Gikam.isNotEmpty(row.columnValue3)) {
                    var type = 'info';
                    if (row.answered3 === '1') {
                        type = 'danger';
                    }
                    return {
                        type : type,
                        value : row.columnValue3
                    }
                }
            }
        }, {
            field : 'columnValue4',
            title : '',
            contentAlign : 'center',
            width : 100,
            tagFormatter : function(row) {
                if (Gikam.isNotEmpty(row.columnValue4)) {
                    var type = 'info';
                    if (row.answered4 === '1') {
                        type = 'danger';
                    }
                    return {
                        type : type,
                        value : row.columnValue4
                    }
                }
            }
        } ];
    },

    getExamReviewListColumns : function() {
        return [ {
            index : true,
        }, {
            field : 'examName',
            title : 'T_CORE_EXAM.EXAMNAME',
            type : 'link'
        }, {
            field : 'questionMethod',
            title : 'T_CORE_EXAM.QUESTIONMETHOD',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreQuestionMethod'
            },
        }, {
            field : 'ext$.tmplname',
            title : 'T_CORE_EXAM_TMPL.TMPLNAME'
        }, {
            field : 'ext$.totalscore',
            title : 'T_CORE_EXAM_TMPL.TOTALSCORE'
        }, {
            field : 'examBeginTime',
            title : 'T_CORE_EXAM.EXAMBEGINTIME'
        }, {
            field : 'examEndTime',
            title : 'T_CORE_EXAM.EXAMENDTIME'
        }, {
            field : 'examDuration',
            title : 'T_CORE_EXAM.EXAMDURATION'
        }, {
            field : 'examCategory',
            title : 'T_CORE_EXAM.EXAMCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreQuestionCategory'
            },
        }, {
            field : 'lastUpdatedByName',
            title : 'T_CORE_EXAM.LASTUPDATEDBYNAME'
        }, {
            field : 'lastUpdatedTime',
            title : 'T_CORE_EXAM.LASTUPDATEDTIME'
        } ];
    },

    getExamPaperReviewListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'activatedFlag',
            title : 'T_CORE_EXAM_PAPER.ACTIVATEDFLAG',
            formatter : function(i, v, row) {
                if (row.activatedFlag === '1') {
                    return Gikam.propI18N('CORE.MODULE.TRAIN.EXAM_PAPER.TITLE.STATUS.REVIEW_COMPLETED');
                } else if (row.activatedFlag === '0') {
                    return Gikam.propI18N('CORE.MODULE.TRAIN.EXAM_PAPER.TITLE.STATUS.REVIEW_UNCOMPLETE');
                }
            }
        }, {
            field : 'userName',
            title : 'T_CORE_EXAM_PAPER.USERNAME',
            type : 'link'
        }, {
            field : 'actBeginTime',
            title : 'T_CORE_EXAM_PAPER.ACTBEGINTIME'
        }, {
            field : 'actEndTime',
            title : 'T_CORE_EXAM_PAPER.ACTENDTIME'
        }, {
            field : 'score',
            title : 'T_CORE_EXAM_PAPER.SCORE'
        } ];
    },

    getExamPaperSingleChoiceAnswerListColumns : function() {
        return [ {
            field : 'ext$.questioncontent',
            title : 'T_CORE_QUESTION.QUESTIONCONTENT'
        }, {
            field : 'ext$.correctanswer',
            title : 'CORE.MODULE.TRAIN.EXAM_PAPER_ANSWER.TITLE.CORRECTANSWER'
        }, {
            field : 'ext$.singlechoicescore',
            title : 'CORE.MODULE.TRAIN.EXAM_PAPER_ANSWER.TITLE.TMPLSCORE'
        }, {
            field : 'answer',
            title : 'T_CORE_EXAM_PAPER_ANSWER.ANSWER'
        }, {
            field : 'score',
            title : 'T_CORE_EXAM_PAPER_ANSWER.SCORE'
        } ];
    },
    
    getExamPaperMultipleChoiceAnswerListColumns : function() {
        return [ {
            field : 'ext$.questioncontent',
            title : 'T_CORE_QUESTION.QUESTIONCONTENT'
        }, {
            field : 'ext$.correctanswer',
            title : 'CORE.MODULE.TRAIN.EXAM_PAPER_ANSWER.TITLE.CORRECTANSWER'
        }, {
            field : 'ext$.multiplechoicescore',
            title : 'CORE.MODULE.TRAIN.EXAM_PAPER_ANSWER.TITLE.TMPLSCORE'
        }, {
            field : 'answer',
            title : 'T_CORE_EXAM_PAPER_ANSWER.ANSWER'
        }, {
            field : 'score',
            title : 'T_CORE_EXAM_PAPER_ANSWER.SCORE'
        } ];
    },
    
    getExamPaperCompletionAnswerListColumns : function() {
        return [ {
            field : 'ext$.questioncontent',
            title : 'T_CORE_QUESTION.QUESTIONCONTENT'
        }, {
            field : 'ext$.correctanswer',
            title : 'CORE.MODULE.TRAIN.EXAM_PAPER_ANSWER.TITLE.CORRECTANSWER'
        }, {
            field : 'ext$.completionscore',
            title : 'CORE.MODULE.TRAIN.EXAM_PAPER_ANSWER.TITLE.TMPLSCORE'
        }, {
            field : 'answer',
            title : 'T_CORE_EXAM_PAPER_ANSWER.ANSWER'
        }, {
            field : 'score',
            title : 'T_CORE_EXAM_PAPER_ANSWER.SCORE'
        } ];
    },

    getExamPaperJudgementAnswerListColumns : function() {
        return [ {
            field : 'ext$.questioncontent',
            title : 'T_CORE_QUESTION.QUESTIONCONTENT'
        }, {
            field : 'ext$.correctanswer',
            title : 'CORE.MODULE.TRAIN.EXAM_PAPER_ANSWER.TITLE.CORRECTANSWER',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreJudgementOption'
            }
        }, {
            field : 'ext$.judgementscore',
            title : 'CORE.MODULE.TRAIN.EXAM_PAPER_ANSWER.TITLE.TMPLSCORE'
        }, {
            field : 'answer',
            title : 'T_CORE_EXAM_PAPER_ANSWER.ANSWER',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreJudgementOption'
            }
        }, {
            field : 'score',
            title : 'T_CORE_EXAM_PAPER_ANSWER.SCORE'
        } ];
    },

    getExamPaperShortAnswerListColumns : function() {
        return [ {
            field : 'ext$.questioncontent',
            title : 'T_CORE_QUESTION.QUESTIONCONTENT'
        }, {
            field : 'ext$.correctanswer',
            title : 'CORE.MODULE.TRAIN.EXAM_PAPER_ANSWER.TITLE.CORRECTANSWER'
        }, {
            field : 'ext$.shortanswerscore',
            title : 'CORE.MODULE.TRAIN.EXAM_PAPER_ANSWER.TITLE.TMPLSCORE'
        }, {
            field : 'answer',
            title : 'T_CORE_EXAM_PAPER_ANSWER.ANSWER'
        }, {
            field : 'score',
            title : 'T_CORE_EXAM_PAPER_ANSWER.SCORE',
            editor : true,
            validator : [ 'integer' ]
        } ];
    },

    getQuestionOptColumns : function() {
        return [ {
            field : 'optCode',
            title : 'T_CORE_QUESTION_OPT.OPTCODE'
        }, {
            field : 'optContent',
            title : 'T_CORE_QUESTION_OPT.OPTCONTENT'
        } ];
    }
});