var coreExamTmpl = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/train/exam-tmpls',

    getEditListColumns : function() {
        return [ {
            checkbox : true,
        }, Gikam.status.getActiveColumn(), {
            field : 'tmplName',
            title : 'T_CORE_EXAM_TMPL.TMPLNAME',
            type : 'link'
        }, {
            field : 'singleChoiceQty',
            title : 'T_CORE_EXAM_TMPL.SINGLECHOICEQTY'
        }, {
            field : 'singleChoiceScore',
            title : 'T_CORE_EXAM_TMPL.SINGLECHOICESCORE'
        }, {
            field : 'multipleChoiceQty',
            title : 'T_CORE_EXAM_TMPL.MULTIPLECHOICEQTY'
        }, {
            field : 'multipleChoiceScore',
            title : 'T_CORE_EXAM_TMPL.MULTIPLECHOICESCORE'
        }, {
            field : 'completionQty',
            title : 'T_CORE_EXAM_TMPL.COMPLETIONQTY'
        }, {
            field : 'completionScore',
            title : 'T_CORE_EXAM_TMPL.COMPLETIONSCORE'
        }, {
            field : 'judgementQty',
            title : 'T_CORE_EXAM_TMPL.JUDGEMENTQTY'
        }, {
            field : 'judgementScore',
            title : 'T_CORE_EXAM_TMPL.JUDGEMENTSCORE'
        }, {
            field : 'shortAnswerQty',
            title : 'T_CORE_EXAM_TMPL.SHORTANSWERQTY'
        }, {
            field : 'shortAnswerScore',
            title : 'T_CORE_EXAM_TMPL.SHORTANSWERSCORE'
        }, {
            field : 'totalScore',
            title : 'T_CORE_EXAM_TMPL.TOTALSCORE'
        } ];
    },
    
    getBaseInfoFormFields : function() {
        return [ {
            field : 'tmplName',
            title : 'T_CORE_EXAM_TMPL.TMPLNAME',
            preInsert : true,
            validators : [ 'notEmpty', 'strLength(0,90]' ],
            colspan : 2
        }, {
            field : 'singleChoiceQty',
            title : 'T_CORE_EXAM_TMPL.SINGLECHOICEQTY',
            validators : [ 'integer' ],
            onChange : function(field, value) {
                var totalScore = coreExamTmpl.detailPage.calcTotalScore(field, value);
                Gikam.getComp('core-exam-tmpl-detail-base-info-form').setData({
                    singleChoiceQty : value,
                    totalScore : totalScore
                });
            }
        }, {
            field : 'singleChoiceScore',
            title : 'T_CORE_EXAM_TMPL.SINGLECHOICESCORE',
            validators : [ 'integer' ],
            onChange : function(field, value) {
                var totalScore = coreExamTmpl.detailPage.calcTotalScore(field, value);
                Gikam.getComp('core-exam-tmpl-detail-base-info-form').setData({
                    singleChoiceScore : value,
                    totalScore : totalScore
                });
            }
        }, {
            field : 'multipleChoiceQty',
            title : 'T_CORE_EXAM_TMPL.MULTIPLECHOICEQTY',
            validators : [ 'integer' ],
            onChange : function(field, value) {
                var totalScore = coreExamTmpl.detailPage.calcTotalScore(field, value);
                Gikam.getComp('core-exam-tmpl-detail-base-info-form').setData({
                    multipleChoiceQty : value,
                    totalScore : totalScore
                });
            }
        }, {
            field : 'multipleChoiceScore',
            title : 'T_CORE_EXAM_TMPL.MULTIPLECHOICESCORE',
            validators : [ 'integer' ],
            onChange : function(field, value) {
                var totalScore = coreExamTmpl.detailPage.calcTotalScore(field, value);
                Gikam.getComp('core-exam-tmpl-detail-base-info-form').setData({
                    multipleChoiceScore : value,
                    totalScore : totalScore
                });
            }
        }, {
            field : 'completionQty',
            title : 'T_CORE_EXAM_TMPL.COMPLETIONQTY',
            validators : [ 'integer' ],
            onChange : function(field, value) {
                var totalScore = coreExamTmpl.detailPage.calcTotalScore(field, value);
                Gikam.getComp('core-exam-tmpl-detail-base-info-form').setData({
                    completionQty : value,
                    totalScore : totalScore
                });
            }
        }, {
            field : 'completionScore',
            title : 'T_CORE_EXAM_TMPL.COMPLETIONSCORE',
            validators : [ 'integer' ],
            onChange : function(field, value) {
                var totalScore = coreExamTmpl.detailPage.calcTotalScore(field, value);
                Gikam.getComp('core-exam-tmpl-detail-base-info-form').setData({
                    completionScore : value,
                    totalScore : totalScore
                });
            }
        }, {
            field : 'judgementQty',
            title : 'T_CORE_EXAM_TMPL.JUDGEMENTQTY',
            validators : [ 'integer' ],
            onChange : function(field, value) {
                var totalScore = coreExamTmpl.detailPage.calcTotalScore(field, value);
                Gikam.getComp('core-exam-tmpl-detail-base-info-form').setData({
                    judgementQty : value,
                    totalScore : totalScore
                });
            }
        }, {
            field : 'judgementScore',
            title : 'T_CORE_EXAM_TMPL.JUDGEMENTSCORE',
            validators : [ 'integer' ],
            onChange : function(field, value) {
                var totalScore = coreExamTmpl.detailPage.calcTotalScore(field, value);
                Gikam.getComp('core-exam-tmpl-detail-base-info-form').setData({
                    judgementScore : value,
                    totalScore : totalScore
                });
            }
        }, {
            field : 'shortAnswerQty',
            title : 'T_CORE_EXAM_TMPL.SHORTANSWERQTY',
            validators : [ 'integer' ],
            onChange : function(field, value) {
                var totalScore = coreExamTmpl.detailPage.calcTotalScore(field, value);
                Gikam.getComp('core-exam-tmpl-detail-base-info-form').setData({
                    shortAnswerQty : value,
                    totalScore : totalScore
                });
            }
        }, {
            field : 'shortAnswerScore',
            title : 'T_CORE_EXAM_TMPL.SHORTANSWERSCORE',
            validators : [ 'integer' ],
            onChange : function(field, value) {
                var totalScore = coreExamTmpl.detailPage.calcTotalScore(field, value);
                Gikam.getComp('core-exam-tmpl-detail-base-info-form').setData({
                    shortAnswerScore : value,
                    totalScore : totalScore
                });
            }
        }, {
            field : 'totalScore',
            title : 'T_CORE_EXAM_TMPL.TOTALSCORE',
            readonly : true
        } ];
    },
    
    getChooseListColumns : function() {
        return [ {
            field : 'tmplName',
            title : 'T_CORE_EXAM_TMPL.TMPLNAME',
            type : 'link'
        }, {
            field : 'singleChoiceQty',
            title : 'T_CORE_EXAM_TMPL.SINGLECHOICEQTY'
        }, {
            field : 'singleChoiceScore',
            title : 'T_CORE_EXAM_TMPL.SINGLECHOICESCORE'
        }, {
            field : 'multipleChoiceQty',
            title : 'T_CORE_EXAM_TMPL.MULTIPLECHOICEQTY'
        }, {
            field : 'multipleChoiceScore',
            title : 'T_CORE_EXAM_TMPL.MULTIPLECHOICESCORE'
        }, {
            field : 'completionQty',
            title : 'T_CORE_EXAM_TMPL.COMPLETIONQTY'
        }, {
            field : 'completionScore',
            title : 'T_CORE_EXAM_TMPL.COMPLETIONSCORE'
        }, {
            field : 'judgementQty',
            title : 'T_CORE_EXAM_TMPL.JUDGEMENTQTY'
        }, {
            field : 'judgementScore',
            title : 'T_CORE_EXAM_TMPL.JUDGEMENTSCORE'
        }, {
            field : 'shortAnswerQty',
            title : 'T_CORE_EXAM_TMPL.SHORTANSWERQTY'
        }, {
            field : 'shortAnswerScore',
            title : 'T_CORE_EXAM_TMPL.SHORTANSWERSCORE'
        }, {
            field : 'totalScore',
            title : 'T_CORE_EXAM_TMPL.TOTALSCORE'
        } ];
    }
});