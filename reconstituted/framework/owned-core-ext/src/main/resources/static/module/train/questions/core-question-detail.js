coreQuestion.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                id : 'core-question-detail-btn-activate',
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function() {
                    _this.activate();
                }
            }, {
                id : 'core-question-detail-btn-back',
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
                title : 'CORE.MODULE.TRAIN.QUESTION.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-question-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreQuestionServiceImpl',
                    fields : coreQuestion.getBaseInfoFormFields(),
                    onLoadSuccess : function(data) {
                        if (data.questionType === 'shortAnswer' || data.questionType === 'judgement' || data.questionType === 'completion') {
                            
                            Gikam.getAsyncComp('core-question-detail-question-opt-tab').done(function(tab) {
                                tab.hide();
                            });
                            
                            if (data.questionType === 'judgement') {
                                Gikam.getComp('core-question-detail-base-info-form').hideFields(['answer']);
                                Gikam.getComp('core-question-detail-base-info-form').showFields(['judgementAnswer']);
                            } else {
                                Gikam.getComp('core-question-detail-base-info-form').showFields(['answer']);
                                Gikam.getComp('core-question-detail-base-info-form').hideFields(['judgementAnswer']);
                            }
                            
                        } else {
                            Gikam.getAsyncComp('core-question-detail-question-opt-tab').done(function(tab) {
                                tab.show();
                            });
                            Gikam.getComp('core-question-detail-base-info-form').showFields(['answer']);
                            Gikam.getComp('core-question-detail-base-info-form').hideFields(['judgementAnswer']);
                        }
                    }
                } ]
            } ]
        }
    },

    activate : function() {
        var _this = this;
        var form = Gikam.getComp('core-question-detail-base-info-form');
        if (form.validate()) {
            var url = coreQuestion.baseUrl + '/action/activate';
            workspace.window.showMask(true);
            Gikam.put(url, Gikam.getJsonWrapper(null, [ '', [ form.getData() ] ])).done(function(r) {
                workspace.window.goBack();
            }).always(function() {
                workspace.window.closeMask();
            });
        }
    },

    getQuestionOptTabParam : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'core-question-detail-question-opt-tab',
            width : Gikam.width.L,
            panels : [ {
                title : 'CORE.MODULE.TRAIN.QUESTION.TAB.QUESTION_OPT',
                id : 'core-question-detail-question-opt-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-question-detail-question-opt-list-grid',
                    url : _this.getCompUrl('questionOptGrid'),
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
                    } ]
                } ]
            } ]
        }
    },

    insertOptRow : function() {
        var _this = this;
        var url = Gikam.printf(coreQuestion.baseUrl + '/{id}/opts', {
            id : _this.param.id,
        });
        Gikam.getComp('core-question-detail-question-opt-list-grid').insert(url, [ {
            questionId : _this.param.id
        } ]);
    },

    deleteOptRow : function() {
        var url = coreQuestion.baseUrl + '/opts';
        Gikam.getComp('core-question-detail-question-opt-list-grid').deleteRows(url);
    },

    urlManager : {
        baseInfoForm : coreQuestion.baseUrl + '/{id}',
        questionOptGrid : coreQuestion.baseUrl + '/{id}/opts/queries'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-question-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'core-question-detail-opt-list-grid' : this.getCompUrl('questionOptGrid')
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
                items : [ _this.getBtnToolbar(), _this.getBaseInfoTab(), _this.getQuestionOptTabParam() ]
            },
            renderTo : workspace.window.$dom,
            pageConfig : {
                pageObject : coreQuestion,
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