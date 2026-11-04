coreExam.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-exam-edit-list-grid',
            url : coreExam.baseUrl + '/queries/raw',
            deleteFormatter : function(row) {
                return row.examName || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.TRAIN.TRAIN.BUTTON.ADD',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.TRAIN.EXAM.MODAL.TITLE.ADD_EXAM',
                        fields : coreExam.getBaseInfoFormFields(),
                        url : coreExam.baseUrl,
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('core-exam-edit-list-grid').deleteRows(coreExam.baseUrl);
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.TRAIN.EXAM.BUTTON.LOCK',
                icon : 'batch-to-check',
                onClick : function() {
                    _this.lockExam();
                }
            }/*, {
                type : 'button',
                text : 'CORE.MODULE.TRAIN.EXAM.BUTTON.PREVIEW',
                icon : 'preview',
                onClick : function() {
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.TRAIN.EXAM.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    _this.printExam();
                }
            }*/ ],
            columns : coreExam.getEditListColumns(),
            onCellClick : function(field, row) {
                field === 'examName' && _this.loadDetailPage(row.id);
            }
        }
    },

    printExam : function() {
        var _this = this;
        var selectedRows = Gikam.getComp('core-exam-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selectedRows)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
            return;
        }

        var validFlag = true;
        for (var i = 0; i < selectedRows.length; i++) {
            var item = selectedRows[i];
            if (item.activatedFlag === '0' || item.questionMethod === 'manual') {
                validFlag = false;
                break;
            }
        }

        if (!validFlag) {
            Gikam.alert("CORE.MODULE.TRAIN.EXAM.TIP.CHOOSEN_EXAM_MUST_BE_ACTIVATE_AND_MANUAL");
            return;
        } else {
            _this.doPrintExam(selectedRows);
        }
    },

    doPrintExam : function(selectedRows) {

    },

    lockExam : function() {
        var selectedRows = Gikam.getComp('core-exam-edit-list-grid').getSelections();
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
            Gikam.alert("CORE.MODULE.TRAIN.EXAM.TIP.EXISTS_LOCKED_DATA");
            return;
        }

        var url = coreExam.baseUrl + '/action/activate';

        workspace.window.showMask(true);
        Gikam.put(url, Gikam.getJsonWrapper(null, [ '', selectedRows ])).done(function(r) {
            Gikam.getComp('core-exam-edit-list-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-exam-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreExam.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function(param) {
        var _this = this;
        _this.param = param;
        _this.create();
    }
};