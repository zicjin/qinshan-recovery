var coreExam = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/train/exams',

    getEditListColumns : function() {
        return [ {
            checkbox : true,
        }, {
            field : 'activatedFlag',
            title : 'T_CORE_EXAM.ACTIVATEDFLAG',
            type : 'select',
            category : 'exam-locked-status'
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
    
    getChooseListColumns : function() {
        return [ {
            field : 'activatedFlag',
            title : 'T_CORE_EXAM.ACTIVATEDFLAG',
            type : 'select',
            category : 'exam-locked-status'
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

    getBaseInfoFormFields : function() {
        return [ {
            field : 'examName',
            title : 'T_CORE_EXAM.EXAMNAME',
            validators : [ 'strLength(0,90]' ],
            preInsert : true,
            validators : [ 'notEmpty' ]
        }, {
            field : 'questionMethod',
            title : 'T_CORE_EXAM.QUESTIONMETHOD',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreQuestionMethod'
            },
            preInsert : true,
            validators : [ 'notEmpty' ],
            onChange : function(field, value, index) {
                if (value == 'random') {
                    var questionData = Gikam.getComp('core-exam-detail-question-list-grid').getData();
                    if (Gikam.isEmpty(questionData)) {
                        Gikam.getComp('core-exam-detail-question-tab').hide();
                        return;
                    }
                    
                    var url = coreExam.baseUrl + '/questions';
                    
                    workspace.window.showMask(true);
                    Gikam.del(url, Gikam.getJsonWrapper(null, [ '', questionData ])).done(function(r) {
                        Gikam.getComp('core-exam-detail-question-tab').hide();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                } else if (value == 'manual') {
                    Gikam.getComp('core-exam-detail-question-tab').show();
                }
            }
        }, {
            field : 'ext$.tmplname',
            title : 'T_CORE_EXAM_TMPL.TMPLNAME',
            type : 'choose',
            category : 'exam-tmpl',
            preInsert : true,
            single : true,
            editable : false,
            onAfterChoose : function(rows, i) {
                if (rows) {
                    this.setData({
                        examTmplId : rows[0].id,
                        tmplSingleChoiceQty : rows[0].singleChoiceQty,
                        tmplMultipleChoiceQty : rows[0].multipleChoiceQty,
                        tmplJudgementQty : rows[0].judgementQty,
                        tmplShortAnswerQty : rows[0].shortAnswerQty,
                        tmplCompletionQty : rows[0].completionQty,
                        ext$ : {
                            tmplname : rows[0].tmplName,
                            totalscore : rows[0].totalScore
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    examTmplId : null,
                    tmplSingleChoiceQty : 0,
                    tmplMultipleChoiceQty : 0,
                    tmplJudgementQty : 0,
                    tmplShortAnswerQty : 0,
                    tmplCompletionQty : 0,
                    ext$ : {
                        tmplname : null,
                        totalscore : 0
                    }
                });
            },
            validators : [ 'notEmpty' ]
        }, {
            field : 'ext$.totalscore',
            title : 'T_CORE_EXAM_TMPL.TOTALSCORE',
            readonly : true
        }, {
            field : 'examBeginTime',
            title : 'T_CORE_EXAM.EXAMBEGINTIME',
            type : 'dateTime',
            validators : [ 'notEmpty' ]
        }, {
            field : 'examEndTime',
            title : 'T_CORE_EXAM.EXAMENDTIME',
            type : 'dateTime',
            validators : [ 'notEmpty' ]
        }, {
            field : 'examDuration',
            title : 'T_CORE_EXAM.EXAMDURATION',
            validators : [ 'integer' ]
        }, {

        }, {
            field : 'examCategory',
            title : 'T_CORE_EXAM.EXAMCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreQuestionCategory'
            },
            validators : [ 'notEmpty' ]
        } ];
    },
    
    getQuestionEditInfoFormFields : function() {
        return [ {
            field : 'ext$.singlechoiceqtyrate',
            title : 'CORE.MODULE.TRAIN.EXAM_QUESTION.FIELD.TITLE.SINGLECHOICEQTY',
            readonly : true,
        },{
            field : 'ext$.multiplechoiceqtyrate',
            title : 'CORE.MODULE.TRAIN.EXAM_QUESTION.FIELD.TITLE.MULTIPLECHOICEQTY',
            readonly : true,
        },{
            field : 'ext$.completionqtyrate',
            title : 'CORE.MODULE.TRAIN.EXAM_QUESTION.FIELD.TITLE.COMPLETIONQTY',
            readonly : true,
        },{
            field : 'ext$.judgementqtyrate',
            title : 'CORE.MODULE.TRAIN.EXAM_QUESTION.FIELD.TITLE.JUDGEMENTQTY',
            readonly : true,
        },{
            field : 'ext$.shortanswerqtyrate',
            title : 'CORE.MODULE.TRAIN.EXAM_QUESTION.FIELD.TITLE.SHORTANSWERQTY',
            readonly : true,
        } ];
    },

    getQuestionColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'ext$.questiontype',
            title : 'T_CORE_QUESTION.QUESTIONTYPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreQuestionType'
            }
        }, {
            field : 'ext$.questioncontent',
            title : 'T_CORE_QUESTION.QUESTIONCONTENT'
        } ];
    },
    
    getQuestionBankColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
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
            title : 'T_CORE_QUESTION.QUESTIONCONTENT'
        } ];
    }
});