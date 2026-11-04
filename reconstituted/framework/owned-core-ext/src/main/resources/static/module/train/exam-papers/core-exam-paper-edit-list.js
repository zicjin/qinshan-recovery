coreExamPaper.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-exam-paper-edit-list-grid',
            url : coreExamPaper.baseUrl + '/queries/raw',
            deleteFormatter : function(row) {
                return row.ext$.theme || row.id;
            },
            requestData : {
                userId_SEQ : workspace.user.id
            },
            columns : coreExamPaper.getEditListColumns(),
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.TRAIN.EXAM_PAPER.BUTTON.BEGIN',
                icon : 'activate',
                onClick : function() {
                    _this.startExam();
                }
            } ]
        }
    },

    startExam : function() {
        var _this = this;
        var selectedRows = Gikam.getComp('core-exam-paper-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selectedRows)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
            return;
        }

        var selectedRow = selectedRows[0];
        if (selectedRow.status === 'done') {
            Gikam.alert("CORE.MODULE.TRAIN.EXAM_PAPER.TIP.EXAM_DONE");
            return;
        }
        
        var url = coreExamPaper.baseUrl + '/action/start-exam';

        workspace.window.showMask(true);

        Gikam.put(url, Gikam.getJsonWrapper({}, [ '', [ selectedRow ] ])).done(function(r) {
            var answerUrl = Gikam.printf(coreExamPaper.baseUrl + '/{id}/answers/{orderNo}', {
                id : selectedRow.id,
                orderNo : 1
            });
            var examPaper = JSON.parse(r);
            Gikam.getJson(answerUrl).done(function(data) {
                _this.loadDetailPage(data.id, data.examPaperId, selectedRow.ext$.questionqty, examPaper.ext$.countdown);
            });
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-exam-paper-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id, examPaperId, questionQty, countDown) {
        workspace.window.load(coreExamPaper.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            examPaperId : examPaperId,
            questionQty : questionQty,
            countDown : countDown
        }));
    },

    init : function(param) {
        var _this = this;
        _this.param = param;
        _this.create();
    }
};