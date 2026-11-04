coreExamPaper.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        _this.createCountdown(_this.param.countDown);
        return {
            type : 'btnToolbar',
            items : [ {
                id : 'core-exam-paper-detail-btn-first-question',
                type : 'button',
                text : 'CORE.MODULE.TRAIN.EXAM_PAPER.BUTTON.FIRST_QUESTION',
                onClick : function() {
                    _this.switchQuestion(1);
                }
            }, {
                id : 'core-exam-paper-detail-btn-previous-question',
                type : 'button',
                icon : 'move-up',
                text : 'CORE.MODULE.TRAIN.EXAM_PAPER.BUTTON.PREVIOUS_QUESTION',
                onClick : function() {
                    var orderNo = Gikam.getComp('core-exam-paper-detail-exam-paper-answer-form').getData().orderNo;
                    if (orderNo === 1) {
                        Gikam.alert('CORE.MODULE.TRAIN.EXAM_PAPER.TIP.ALREADY_FIRST_QUESTION');
                        return;
                    }
                    _this.switchQuestion(orderNo - 1);
                }
            }, {
                id : 'core-exam-paper-detail-btn-next-question',
                type : 'button',
                icon : 'move-down',
                text : 'CORE.MODULE.TRAIN.EXAM_PAPER.BUTTON.NEXT_QUESTION',
                onClick : function() {
                    var questionQty = _this.param.questionQty;
                    var orderNo = Gikam.getComp('core-exam-paper-detail-exam-paper-answer-form').getData().orderNo;
                    if (orderNo + 1 > questionQty) {
                        Gikam.alert('CORE.MODULE.TRAIN.EXAM_PAPER.TIP.ALREADY_LAST_QUESTION');
                        return;
                    }
                    _this.switchQuestion(orderNo + 1);
                }
            }, {
                id : 'core-exam-paper-detail-btn-last-question',
                type : 'button',
                text : 'CORE.MODULE.TRAIN.EXAM_PAPER.BUTTON.LAST_QUESTION',
                onClick : function() {
                    _this.switchQuestion(_this.param.questionQty);
                }
            }, {
                id : 'core-exam-paper-detail-btn-end',
                type : 'button',
                text : 'CORE.MODULE.TRAIN.EXAM_PAPER.BUTTON.END_EXAM',
                icon : 'logout',
                onClick : function() {
                    var examPaperId = _this.param.examPaperId;
                    var url = coreExamPaper.baseUrl + '/action/end-exam';
                    workspace.window.showMask(true);
                    Gikam.put(url, Gikam.getJsonWrapper({}, [ '', [ {
                        id : examPaperId
                    } ] ])).done(function() {
                        workspace.window.goBack();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                    if (Gikam.backTime) {
                        clearTimeout(Gikam.backTime);
                        Gikam.backTime = null;
                    }
                }
            }, {
                id : 'core-exam-paper-detail-btn-countdown',
                type : 'button',
                text : 'CORE.MODULE.TRAIN.EXAM_PAPER.BUTTON.COUNTDOWN'
            } ]
        }
    },

    switchQuestion : function(orderNo) {
        var _this = this;
        
        var examPaperId = _this.param.examPaperId;
        var answerUrl = Gikam.printf(coreExamPaper.baseUrl + '/{id}/answers/{orderNo}', {
            id : examPaperId,
            orderNo : orderNo
        });

        workspace.window.showMask(true);
        Gikam.getJson(answerUrl).done(function(data) {
            if (data.ext$.questiontype === 'singleChoice' || data.ext$.questiontype === 'multipleChoice') {
                Gikam.getComp('core-exam-paper-detail-question-opt-list-grid').refresh({
                    url : Gikam.printf(coreExamPaper.baseUrl + '/answers/{answerId}/question-opts/queries', {
                        answerId : data.id
                    }),
                });
            }
            Gikam.getComp('core-exam-paper-detail-exam-paper-answer-form').refresh({
                url : Gikam.printf(coreExamPaper.baseUrl + '/answers/{id}', {
                    id : data.id
                })
            });
            Gikam.getComp('core-exam-paper-detail-answer-card-list-grid').refresh({
                url : Gikam.printf(coreExamPaper.baseUrl + '/{id}/answer-cards/queries', {
                    id : data.examPaperId
                })
            });
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    getExamPaperAnswerTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                items : [ {
                    type : 'form',
                    id : 'core-exam-paper-detail-exam-paper-answer-form',
                    url : Gikam.printf(coreExamPaper.baseUrl + '/answers/{id}', {
                        id : _this.param.id
                    }),
                    service : 'coreExamPaperAnswerServiceImpl',
                    fields : coreExamPaper.getAnswerFields(),
                    onLoadSuccess : function(data) {
                        if (data.ext$.questiontype === 'shortAnswer' || data.ext$.questiontype === 'judgement' || data.ext$.questiontype === 'completion') {
                            Gikam.getAsyncComp('core-exam-paper-detail-question-opt-tab').done(function(tab) {
                                tab.hide();
                            });
                            if (data.ext$.questiontype === 'judgement') {
                                Gikam.getComp('core-exam-paper-detail-exam-paper-answer-form').hideFields(['answer']);
                                Gikam.getComp('core-exam-paper-detail-exam-paper-answer-form').showFields(['judgementAnswer']);
                            } else {
                                Gikam.getComp('core-exam-paper-detail-exam-paper-answer-form').hideFields(['judgementAnswer']);
                                Gikam.getComp('core-exam-paper-detail-exam-paper-answer-form').showFields(['answer']);
                            }
                        } else {
                            Gikam.getAsyncComp('core-exam-paper-detail-question-opt-tab').done(function(tab) {
                                tab.show();
                            });
                            Gikam.getComp('core-exam-paper-detail-exam-paper-answer-form').showFields(['answer']);
                            Gikam.getComp('core-exam-paper-detail-exam-paper-answer-form').hideFields(['judgementAnswer']);
                        }
                    }
                } ]
            } ]
        }
    },

    getQuestionOptTabParam : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'core-exam-paper-detail-question-opt-tab',
            panels : [ {
                id : 'core-exam-paper-detail-question-opt-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-exam-paper-detail-question-opt-list-grid',
                    url : Gikam.printf(coreExamPaper.baseUrl + '/answers/{answerId}/question-opts/queries', {
                        answerId : _this.param.id
                    }),
                    height : 200,
                    columns : coreExamPaper.getQuestionOptColumns(),
                    page : false,
                    filter : false
                } ]
            } ]
        }
    },

    getAnswerCardTabParam : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'core-exam-paper-detail-answer-card-tab',
            panels : [ {
                title : 'CORE.MODULE.TRAIN.EXAM_PAPER.TAB.ANSWER_CARD',
                id : 'core-exam-paper-detail-answer-card-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-exam-paper-detail-answer-card-list-grid',
                    url : Gikam.printf(coreExamPaper.baseUrl + '/{id}/answer-cards/queries', {
                        id : _this.param.examPaperId
                    }),
                    columns : coreExamPaper.getAnswerCardColumns(),
                    onCellClick : function(field, row) {
                        if (field === 'columnValue1') {
                            _this.switchQuestion(row.columnValue1);
                        } else if (field === 'columnValue2') {
                            _this.switchQuestion(row.columnValue2);
                        } else if (field === 'columnValue3') {
                            _this.switchQuestion(row.columnValue3);
                        } else if (field === 'columnValue4') {
                            _this.switchQuestion(row.columnValue4);
                        }
                    },
                    page : false,
                    filter : false
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            center : {
                items : [ _this.getBtnToolbar(), _this.getExamPaperAnswerTab(), _this.getQuestionOptTabParam(), _this.getAnswerCardTabParam() ]
            },
            renderTo : workspace.window.$dom
        });
        return this;
    },
    
    // 创建倒计时dom
    createCountdown : function(countDown) {
        var _this = this;
        setTimeout(function() {
            Gikam.jQuery("#core-exam-paper-detail-btn-countdown > .button").append("<span id='btnCountdown'></span>");
            _this.countdownHandler(countDown, 10);
        });
    },
    
    // 倒计时
    countdownHandler : function(countdown, surplusTime) {
        var _this = this;
        if (Gikam.backTime) {
            clearTimeout(Gikam.backTime);
            Gikam.backTime = null;
        }

        var timeDiff = countdown.split(':');
        // 设置截止时间
        var endDate = new Date().getTime() + (parseInt(timeDiff[0]) * 60 + parseInt(timeDiff[1])) * 60 * 1000;
        
        function countTime() {
            var now = new Date().getTime();
            // 时间差
            var leftTime = endDate - now;
            var h, m, s;
            var countdownText = '';
            // 剩余时间提醒
            if(leftTime <= surplusTime * 60 * 1000 && leftTime > surplusTime * 60 * 1000-50) {
                _this.surplusTimeHandler(surplusTime);
            }
            if (leftTime >= 0) {
                h = Math.floor((leftTime / 1000 / 60 / 60) % 24);
                m = Math.floor((leftTime / 1000 / 60) % 60);
                s = Math.floor((leftTime / 1000) % 60);
                s < 10 && (s = '0' + s);
                m < 10 && (m = '0' + m);
                h < 10 && (h = '0' + h);
                countdownText = `${h}:${m}:${s}`;
                // 还剩余时间提醒
                
                // 递归每秒调用countTime方法，显示动态时间效果
                Gikam.backTime = setTimeout(countTime, 50);
            } else {
                countdownText = '';
                // 倒计时完成回调
                _this.countdownCallback();
            }
            Gikam.jQuery("#btnCountdown").text(countdownText);
        }
        countTime();
    },
    
    // 还剩余时间回调
    surplusTimeHandler(time) {
        Gikam.alert('还剩余时间' + time + '分钟');
    },
    
    // 倒计时结束回调
    countdownCallback : function() {
        var examPaperId = this.param.examPaperId;
        var url = coreExamPaper.baseUrl + '/action/end-exam';
        workspace.window.showMask(true);
        Gikam.put(url, Gikam.getJsonWrapper({}, [ '', [ {
            id : examPaperId
        } ] ])).done(function() {
            workspace.window.goBack();
        }).always(function() {
            workspace.window.closeMask();
        });
        Gikam.alert('CORE.MODULE.TRAIN.EXAM_PAPER.TIP.EXAM_END');
    },

    init : function(param) {
        var _this = this;
        _this.param = param;
        _this.create();
    }
}