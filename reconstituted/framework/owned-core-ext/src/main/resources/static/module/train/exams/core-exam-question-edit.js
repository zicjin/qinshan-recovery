coreExam.questionEditPage = {
    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : "tab",
            panels : [ {
                items : [ {
                    type : "form",
                    id : "core-exam-question-edit-base-info-form",
                    url : Gikam.printf(coreExam.baseUrl + "/{id}", {
                        id : _this.param.id,
                    }),
                    fields : coreExam.getQuestionEditInfoFormFields(),
                } ],
            } ],
        };
    },

    getQuestionBankParam : function() {
        var _this = this;
        return {
            type : "tab",
            width : Gikam.width.M,
            panels : [ {
                title : "CORE.MODULE.TRAIN.EXAM.TAB.QUESTION_BANK",
                items : [ {
                    type : "grid",
                    id : "core-exam-question-eidt-question-bank-list-grid",
                    url : IFM_CONTEXT + "/secure/core/module/train/questions/queries/searchable",
                    columns : coreExam.getQuestionBankColumns(),
                    filterOpen : true,
                    requestData : {
                        questionCategory_SEQ : _this.param.questionCategory,
                        activatedFlag_SEQ : "1",
                        lastSuspendedFlag_SEQ : "0",
                        target_unassigned : "1",
                        targetTable : "T_CORE_EXAM_QUESTION",
                        targetQuestionIdColumn : "QUESTIONID",
                        targetFilterColumn : "EXAMID",
                        targetFilterValue : _this.param.id,
                    },
                    toolbar : [ {
                        type : "button",
                        text : "GIKAM.BUTTON.INSERT",
                        icon : "add-row",
                        onClick : function() {
                            _this.insertQuestionRow();
                        },
                    } ]
                } ],
            } ],
        };
    },

    getExamQuestionParam : function() {
        var _this = this;
        return {
            type : "tab",
            width : Gikam.width.M,
            panels : [ {
                title : "CORE.MODULE.TRAIN.EXAM.TAB.EXAM_QUESTION",
                items : [ {
                    type : "grid",
                    id : "core-exam-question-edit-question-list-grid",
                    url : IFM_CONTEXT + Gikam.printf("/secure/core/module/train/exams/{id}/questions/queries", {
                        id : _this.param.id,
                    }),
                    toolbar : [ {
                        type : "button",
                        text : "GIKAM.BUTTON.DELETE",
                        icon : "remove",
                        onClick : function() {
                            _this.deleteQuestionRow();
                        },
                    }, ],
                    columns : coreExam.getQuestionColumns(),
                }, ],
            }, ],
        };
    },

    getQuestionInfoLayout : function() {
        var _this = this;
        return {
            type : "layout",
            west : {
                width : "50%",
                items : [ _this.getQuestionBankParam() ],
            },
            center : {
                items : [ _this.getExamQuestionParam() ],
            },
        };
    },

    deleteQuestionRow : function() {
        var selectedRows = Gikam.getComp("core-exam-question-edit-question-list-grid").getSelections();
        if (Gikam.isEmpty(selectedRows)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
            return;
        }

        var url = coreExam.baseUrl + "/questions";

        workspace.window.showMask(true);
        Gikam.del(url, Gikam.getJsonWrapper(null, [ "", selectedRows ])).done(function(r) {
            Gikam.getComp("core-exam-question-edit-base-info-form").refresh();
            Gikam.getComp("core-exam-question-eidt-question-bank-list-grid").refresh();
            Gikam.getComp("core-exam-question-edit-question-list-grid").refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    insertQuestionRow : function() {
        var _this = this;

        var formData = Gikam.getComp("core-exam-question-edit-base-info-form").getData();

        var selectedRows = Gikam.getComp("core-exam-question-eidt-question-bank-list-grid").getSelections();
        if (Gikam.isEmpty(selectedRows)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
            return;
        }

        var limitSingleChoiceQty = formData.tmplSingleChoiceQty - formData.singleChoiceQty;
        var limitMultipleChoiceQty = formData.tmplMultipleChoiceQty - formData.multipleChoiceQty;
        var limitJudgementQty = formData.tmplJudgementQty - formData.judgementQty;
        var limitShortAnswerQty = formData.tmplShortAnswerQty - formData.shortAnswerQty;

        var selectedSingleChoiceQty = 0;
        var selectedMultipleChoiceQty = 0;
        var selectedJudgementQty = 0;
        var selectedShortAnswerQty = 0;

        Gikam.each(selectedRows, function(index, item) {
            if (item.questionType === "singleChoice") {
                selectedSingleChoiceQty += 1;
            } else if (item.questionType === "multipleChoice") {
                selectedMultipleChoiceQty += 1;
            } else if (item.questionType === "judgement") {
                selectedJudgementQty += 1;
            } else if (item.questionType === "shortAnswer") {
                selectedShortAnswerQty += 1;
            }
        });

        if (selectedSingleChoiceQty > limitSingleChoiceQty || selectedMultipleChoiceQty > limitMultipleChoiceQty || selectedJudgementQty > limitJudgementQty
                || selectedShortAnswerQty > limitShortAnswerQty) {
            Gikam.alert("CORE.MODULE.TRAIN.EXAM.TIP.QUESTIONQTY_CANNOT_MEETS_THE_REQUIREMENT");
            return;
        }

        var insertData = selectedRows.map(function(item) {
            return {
                questionId : item.id,
                examId : _this.param.id,
                ext$ : {
                    questiontype : item.questionType,
                },
            };
        });

        workspace.window.showMask(true);
        Gikam.postText(coreExam.baseUrl + "/questions", Gikam.getJsonWrapper(null, [ "", insertData ])).done(function(r) {
            Gikam.getComp("core-exam-question-edit-base-info-form").refresh();
            Gikam.getComp("core-exam-question-eidt-question-bank-list-grid").refresh();
            Gikam.getComp("core-exam-question-edit-question-list-grid").refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    createPage : function() {
        var _this = this;
        var items = [ _this.getBaseInfoTab(), _this.getQuestionInfoLayout() ];
        Gikam.create("layout", {
            center : {
                items : items,
            },
            renderTo : Gikam.getLastModal().window.$dom,
        });
        return this;
    },

    init : function(param) {
        var _this = this;
        _this.param = param;
        _this.createPage();
    },
};
