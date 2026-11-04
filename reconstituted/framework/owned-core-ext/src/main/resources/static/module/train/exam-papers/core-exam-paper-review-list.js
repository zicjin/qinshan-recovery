coreExamPaper.reviewPage = {

    getExamGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'core-exam-review-tab',
            panels : [ {
                title : 'CORE.MODULE.TRAIN.EXAM_PAPER.TAB.EXAM',
                items : [ {
                    type : 'grid',
                    id : 'core-exam-review-list-grid',
                    url : IFM_CONTEXT + '/secure/core/module/train/exams/queries/searchable',
                    requestData : {
                        activatedFlag_SEQ : '1',
                        tmplShortAnswerQty_NG : 0
                    },
                    columns : coreExamPaper.getExamReviewListColumns(),
                    onRowActive : function(index, row) {
                        _this.onExamActive(row);
                    }
                } ]
            } ],
        }
    },

    getExamPaperGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'core-exam-paper-review-tab',
            panels : [ {
                title : 'CORE.MODULE.TRAIN.EXAM_PAPER.TAB.EXAM_PAPER',
                items : [ {
                    type : 'grid',
                    id : 'core-exam-paper-review-list-grid',
                    columns : coreExamPaper.getExamPaperReviewListColumns(),
                    toolbar : [ {
                        type : 'button',
                        text : 'CORE.MODULE.TRAIN.EXAM_PAPER.BUTTON.COMPLETE_REVIEW',
                        icon : 'submit',
                        onClick : function() {
                            _this.completeReview();
                        }
                    } ],
                    onCellClick : function(field, row) {
                        field === 'userName' && _this.loadExamPaperDetailPage(row.id);
                    }
                } ]
            } ],
        }
    },

    completeReview : function() {
        var selectedRows = Gikam.getComp('core-exam-paper-review-list-grid').getSelections();
        if (Gikam.isEmpty(selectedRows)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
            return;
        }

        var validFlag = true;
        for (var i = 0; i < selectedRows.length; i++) {
            var item = selectedRows[i];
            if (item.activatedFlag === '1') {
                validFlag = false;
                break;
            }
        }

        if (!validFlag) {
            Gikam.alert("CORE.MODULE.TRAIN.EXAM_PAPER.TIP.EXISTS_REVIEW_COMPLETED_DATA");
            return;
        }

        var url = coreExamPaper.baseUrl + '/action/activate';

        workspace.window.showMask(true);
        Gikam.put(url, Gikam.getJsonWrapper(null, [ '', selectedRows ])).done(function(r) {
            Gikam.getComp('core-exam-paper-review-list-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    onExamActive : function(row) {
        if (row) {
            Gikam.getComp('core-exam-paper-review-list-grid').refresh({
                url : coreExamPaper.baseUrl + '/queries/searchable',
                requestData : {
                    examId_EQ : row.id,
                    status_SEQ : 'done'
                }
            });
        } else {
            Gikam.getComp('core-exam-item-edit-list-grid').cleanData();
        }
    },

    loadExamPaperDetailPage : function(id) {
        workspace.window.load(coreExamPaper.baseUrl + '/page/review-detail' + Gikam.param({
            id : id
        }));
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-exam-review-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 550,
                items : [ _this.getExamGridParam() ]
            },
            center : {
                items : [ _this.getExamPaperGridParam() ]
            }
        });
    },

    init : function(param) {
        var _this = this;
        _this.param = param;
        _this.create();
    }
};