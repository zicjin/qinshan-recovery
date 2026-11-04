coreExamTmpl.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                id : 'core-exam-tmpl-detail-btn-active',
                icon : 'enable',
                onClick : function() {
                    _this.activate();
                }
            }, {
                id : 'core-exam-tmpl-detail-btn-back',
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
                title : 'CORE.MODULE.TRAIN.EXAM_TMPL.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-exam-tmpl-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreExamTmplServiceImpl',
                    fields : coreExamTmpl.getBaseInfoFormFields()
                } ]
            } ]
        }
    },

    urlManager : {
        baseInfoForm : coreExamTmpl.baseUrl + '/{id}'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-exam-tmpl-detail-base-info-form' : this.getCompUrl('baseInfoForm')
        });
    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        });
    },
    
    activate : function() {
        var _this = this;
        var form = Gikam.getComp('core-exam-tmpl-detail-base-info-form');
        if (form.validate()) {
            var url = coreExamTmpl.baseUrl + '/action/activate';
            workspace.window.showMask(true);
            Gikam.put(url, Gikam.getJsonWrapper(null, [ '', [ form.getData() ] ])).done(function(r) {
                workspace.window.goBack();
            }).always(function() {
                workspace.window.closeMask();
            });
        }
    },

    calcTotalScore : function(fieldName, fieldValue) {
        var formData = Gikam.getComp('core-exam-tmpl-detail-base-info-form').getData();
        var totalScore = null;
        if (fieldName === 'singleChoiceQty') {
            var totalSingleChoiceScore = fieldValue * formData.singleChoiceScore;
            var totalMultipleChoiceScore = formData.multipleChoiceQty * formData.multipleChoiceScore;
            var totalJudgementScore = formData.judgementQty * formData.judgementScore;
            var totalShortAnswerScore = formData.shortAnswerQty * formData.shortAnswerScore;
            var totalCompletionScore = formData.completionQty * formData.completionScore;
            totalScore = totalSingleChoiceScore + totalMultipleChoiceScore + totalJudgementScore + totalShortAnswerScore + totalCompletionScore;
        } else if (fieldName === 'singleChoiceScore') {
            var totalSingleChoiceScore = fieldValue * formData.singleChoiceQty;
            var totalMultipleChoiceScore = formData.multipleChoiceQty * formData.multipleChoiceScore;
            var totalJudgementScore = formData.judgementQty * formData.judgementScore;
            var totalShortAnswerScore = formData.shortAnswerQty * formData.shortAnswerScore;
            var totalCompletionScore = formData.completionQty * formData.completionScore;
            totalScore = totalSingleChoiceScore + totalMultipleChoiceScore + totalJudgementScore + totalShortAnswerScore + totalCompletionScore;
        } else if (fieldName === 'multipleChoiceQty') {
            var totalMultipleChoiceScore = fieldValue * formData.multipleChoiceScore;
            var totalSingleChoiceScore = formData.singleChoiceScore * formData.singleChoiceQty;
            var totalJudgementScore = formData.judgementQty * formData.judgementScore;
            var totalShortAnswerScore = formData.shortAnswerQty * formData.shortAnswerScore;
            var totalCompletionScore = formData.completionQty * formData.completionScore;
            totalScore = totalSingleChoiceScore + totalMultipleChoiceScore + totalJudgementScore + totalShortAnswerScore + totalCompletionScore;
        } else if (fieldName === 'multipleChoiceScore') {
            var totalMultipleChoiceScore = fieldValue * formData.multipleChoiceQty;
            var totalSingleChoiceScore = formData.singleChoiceScore * formData.singleChoiceQty;
            var totalJudgementScore = formData.judgementQty * formData.judgementScore;
            var totalShortAnswerScore = formData.shortAnswerQty * formData.shortAnswerScore;
            var totalCompletionScore = formData.completionQty * formData.completionScore;
            totalScore = totalSingleChoiceScore + totalMultipleChoiceScore + totalJudgementScore + totalShortAnswerScore + totalCompletionScore;
        } else if (fieldName === 'completionQty') {
            var totalCompletionScore = fieldValue * formData.completionScore;
            var totalMultipleChoiceScore = formData.multipleChoiceScore * formData.multipleChoiceQty;
            var totalSingleChoiceScore = formData.singleChoiceScore * formData.singleChoiceQty;
            var totalJudgementScore = formData.judgementQty * formData.judgementScore;
            var totalShortAnswerScore = formData.shortAnswerQty * formData.shortAnswerScore;
            totalScore = totalCompletionScore + totalSingleChoiceScore + totalMultipleChoiceScore + totalJudgementScore + totalShortAnswerScore;
        } else if (fieldName === 'completionScore') {
            var totalCompletionScore = fieldValue * formData.completionQty;
            var totalSingleChoiceScore = formData.singleChoiceScore * formData.singleChoiceQty;
            var totalMultipleChoiceScore = formData.multipleChoiceScore * formData.multipleChoiceQty;
            var totalShortAnswerScore = formData.shortAnswerQty * formData.shortAnswerScore;
            var totalJudgementScore = formData.judgementQty * formData.judgementScore;
            totalScore = totalCompletionScore + totalSingleChoiceScore + totalMultipleChoiceScore + totalJudgementScore + totalShortAnswerScore;
        } else if (fieldName === 'judgementQty') {
            var totalJudgementScore = fieldValue * formData.judgementScore;
            var totalCompletionScore = formData.completionQty * formData.completionScore;
            var totalMultipleChoiceScore = formData.multipleChoiceScore * formData.multipleChoiceQty;
            var totalSingleChoiceScore = formData.singleChoiceScore * formData.singleChoiceQty;
            var totalShortAnswerScore = formData.shortAnswerQty * formData.shortAnswerScore;
            totalScore = totalSingleChoiceScore + totalMultipleChoiceScore + totalJudgementScore + totalShortAnswerScore + totalCompletionScore;
        } else if (fieldName === 'judgementScore') {
            var totalJudgementScore = fieldValue * formData.judgementQty;
            var totalCompletionScore = formData.completionQty * formData.completionScore;
            var totalSingleChoiceScore = formData.singleChoiceScore * formData.singleChoiceQty;
            var totalMultipleChoiceScore = formData.multipleChoiceScore * formData.multipleChoiceQty;
            var totalShortAnswerScore = formData.shortAnswerQty * formData.shortAnswerScore;
            totalScore = totalSingleChoiceScore + totalMultipleChoiceScore + totalJudgementScore + totalShortAnswerScore + totalCompletionScore;
        } else if (fieldName === 'shortAnswerQty') {
            var totalShortAnswerScore = fieldValue * formData.shortAnswerScore;
            var totalCompletionScore = formData.completionQty * formData.completionScore;
            var totalSingleChoiceScore = formData.singleChoiceScore * formData.singleChoiceQty;
            var totalMultipleChoiceScore = formData.multipleChoiceScore * formData.multipleChoiceQty;
            var totalJudgementScore = formData.judgementQty * formData.judgementScore;
            totalScore = totalSingleChoiceScore + totalMultipleChoiceScore + totalJudgementScore + totalShortAnswerScore + totalCompletionScore;
        } else if (fieldName === 'shortAnswerScore') {
            var totalShortAnswerScore = fieldValue * formData.shortAnswerQty;
            var totalCompletionScore = formData.completionQty * formData.completionScore;
            var totalSingleChoiceScore = formData.singleChoiceScore * formData.singleChoiceQty;
            var totalMultipleChoiceScore = formData.multipleChoiceQty * formData.multipleChoiceScore;
            var totalJudgementScore = formData.judgementQty * formData.judgementScore;
            totalScore = totalSingleChoiceScore + totalMultipleChoiceScore + totalJudgementScore + totalShortAnswerScore + totalCompletionScore;
        }
        return totalScore;
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            center : {
                items : [ _this.getBtnToolbar(), _this.getBaseInfoTab() ]
            },
            renderTo : workspace.window.$dom,
            pageConfig : {
                pageObject : coreExamTmpl,
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