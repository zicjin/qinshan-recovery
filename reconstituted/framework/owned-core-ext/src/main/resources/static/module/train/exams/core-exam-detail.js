coreExam.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'CORE.MODULE.TRAIN.EXAM.BUTTON.LOCK',
                id : 'core-exam-detail-btn-lock',
                icon : 'batch-to-check',
                onClick : function() {
                    _this.lockExam();
                }
            }, {
                id : 'core-exam-detail-btn-back',
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
                    id : 'core-exam-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreExamServiceImpl',
                    fields : coreExam.getBaseInfoFormFields(),
                    onLoadSuccess : function(data) {
                        if (data.questionMethod === 'random') {
                            Gikam.getAsyncComp('core-exam-detail-question-tab').done(function(tab) {
                                tab.hide();
                            });
                        } else if (data.questionMethod === 'manual') {
                            Gikam.getAsyncComp('core-exam-detail-question-tab').done(function(tab) {
                                tab.show();
                            });
                        }
                    }
                } ]
            } ]
        }
    },

    getExamQuestionTabParam : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'core-exam-detail-question-tab',
            width : Gikam.width.L,
            panels : [ {
                title : 'CORE.MODULE.TRAIN.EXAM.TAB.EXAM_QUESTION',
                id : 'core-exam-detail-question-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-exam-detail-question-list-grid',
                    url : _this.getCompUrl('examQuestionGrid'),
                    service : 'coreExamQuestionServiceImpl',
                    columns : coreExam.getQuestionColumns(),
                    deleteFormatter : function(row) {
                        return row.ext$.questioncontent || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            var examCategory = Gikam.getComp('core-exam-detail-base-info-form').getData().examCategory;
                            if (Gikam.isEmpty(examCategory)) {
                                Gikam.alert('CORE.MODULE.TRAIN.EXAM.TIP.EXAMCATEGORY_CANNOT_EMPTY');
                                return;
                            }
                            var examTmplId = Gikam.getComp('core-exam-detail-base-info-form').getData().examTmplId;
                            if (Gikam.isEmpty(examTmplId)) {
                                Gikam.alert('CORE.MODULE.TRAIN.EXAM.TIP.EXAMTMPL_CANNOT_EMPTY');
                                return;
                            }
                            _this.showQuestionEditPage(_this.param.id, examCategory);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            var url = coreExam.baseUrl + '/questions';
                            var selectedRows = Gikam.getComp('core-exam-detail-question-list-grid').getSelections();
                            workspace.window.showMask(true);
                            Gikam.del(url, Gikam.getJsonWrapper(null, [ '', selectedRows ])).done(function(r) {
                                Gikam.getComp('core-exam-detail-question-list-grid').refresh();
                            }).always(function() {
                                workspace.window.closeMask();
                            });
                        }
                    } ]
                } ]
            } ]
        }
    },

    lockExam : function() {
        var _this = this;
        var form = Gikam.getComp('core-exam-detail-base-info-form');
        if (form.validate()) {
            var url = coreExam.baseUrl + '/action/activate';
            workspace.window.showMask(true);
            Gikam.put(url, Gikam.getJsonWrapper(null, [ '', [ form.getData() ] ])).done(function(r) {
                workspace.window.goBack();
            }).always(function() {
                workspace.window.closeMask();
            });
        }
    },

    showQuestionEditPage : function(id, examCategory) {
        Gikam.create('modal', {
            width : 1000,
            url : coreExam.baseUrl + '/page/question-edit' + Gikam.param({
                id : id,
                questionCategory : examCategory
            }),
            onAfterClose : function() {
                Gikam.getComp('core-exam-detail-base-info-form').refresh();
                Gikam.getComp('core-exam-detail-question-list-grid').refresh();
            }
        });
    },

    urlManager : {
        baseInfoForm : coreExam.baseUrl + '/{id}',
        examQuestionGrid : coreExam.baseUrl + '/{id}/questions/queries'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-exam-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'core-exam-detail-question-list-grid' : this.getCompUrl('examQuestionGrid')
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

    create : function() {
        var _this = this;

        Gikam.create('layout', {
            center : {
                items : [ _this.getBtnToolbar(), _this.getBaseInfoTab(), _this.getExamQuestionTabParam() ]
            },
            renderTo : workspace.window.$dom,
            pageConfig : {
                pageObject : coreExam,
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