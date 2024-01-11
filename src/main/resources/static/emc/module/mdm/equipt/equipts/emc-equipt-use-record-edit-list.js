emcEquiptUseRecord.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-equipt-use-record-edit-list-grid',
            columns : emcEquiptUseRecord.getUseRecordEditListColumns2(),
            url : emcEquipt.baseUrl + '/use-records/queries',
            requestData : _this.param,
            service : 'emcEquiptUseRecordServiceImpl',
            onLoadSuccess : function(data) {
                var selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
                var equiptIds = [];
                var insertData = [];
                selections.forEach(function (run) {
                    var currentRecords = data.filter(function (item) {
                        return item.runId === run.id;
                    });
                    var existedEquiptIds = currentRecords.map(function (item) {
                        return item.equiptId;
                    });

                    if (!Gikam.isEmpty(run.equiptId) && existedEquiptIds.indexOf(run.equiptId) === -1) {
                        equiptIds.push(run.equiptId);
                        insertData.push({
                            runId : run.id,
                            equiptId : run.equiptId,
                            testName : run.testName,
                            testId : run.testId
                        })
                    }
                    if (!Gikam.isEmpty(run.assistEquiptId)) {
                        var assistEquiptIds = run.assistEquiptId.split(',');
                        var notExistedEquiptIds = assistEquiptIds.filter(function (id) {
                            return existedEquiptIds.indexOf(parseInt(id)) === -1;
                        });
                        equiptIds.push(notExistedEquiptIds);
                        notExistedEquiptIds.forEach(function (equiptId) {
                            insertData.push({
                                runId : run.id,
                                equiptId : equiptId,
                                testName : run.testName,
                                testId : run.testId
                            })
                        })
                    }
                });

                if (!Gikam.isEmpty(insertData)) {
                    Gikam.postText(emcEquipt.baseUrl + "/use-records", Gikam.getJsonWrapper({}, [ 'emcEquiptUseRecordServiceImpl', insertData ])).done(function () {
                        Gikam.getComp('emc-equipt-use-record-edit-list-grid').refresh();
                    });
                }

            },
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.BUTTON.BATCH_UPDATE_EQUIPT_RECORD',
                id : 'emc-equipt-use-record-update-button-batch-update',
                icon : 'edit',
                onClick : function() {
                    _this.batchUpdateRecord();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.COPY',
                icon : 'copy',
                onClick : function() {
                    _this.copyRecord();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DEL',
                icon : 'remove',
                onClick : function() {
                    _this.deleteRecoud();
                }
            }]
        }
    },

    batchUpdateRecord : function() {
        var selections = Gikam.getComp('emc-equipt-use-record-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            width : 450,
            height : 400,
            title : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.TITLE.BATCH_UPDATE_EQUIPT_RECORD',
            url : emcEquipt.baseUrl + '/page/batch-update-record-detail',
            onAfterClose : function(data) {
                if (!Gikam.isEmpty(data)) {
                    selections.map(function(item) {
                        Gikam.getComp('emc-equipt-use-record-edit-list-grid').setData({
                            id : item.id,
                            startTime : data.startTime,
                            endTime : data.endTime,
                            beforeUseStatus : data.beforeUseStatus,
                            afterUseStatus : data.afterUseStatus,
                            equiptCondition : data.equiptCondition,
                            orderQty : data.orderQty,
                            userId : data.userId,
                            userName : data.userName
                        });
                    });
                }
            }
        });
    },

    copyRecord : function() {
        var selections = Gikam.getComp('emc-equipt-use-record-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.confirm('GIKAM.TIP.CONFIRM', 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.TIP.CONFRIM_COPY', function() {
            Gikam.postText(emcEquipt.baseUrl + "/use-records", Gikam.getJsonWrapper({}, [ 'emcEquiptUseRecordServiceImpl', selections ])).done(function () {
                Gikam.getComp('emc-equipt-use-record-edit-list-grid').refresh();
            });
        })
    },

    deleteRecoud : function() {
        var selections = Gikam.getComp('emc-equipt-use-record-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.confirm('GIKAM.TIP.CONFIRM', 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.TIP.CONFRIM_DEL', function() {
            Gikam.del(emcEquipt.baseUrl + "/delete-records", Gikam.getJsonWrapper(null,[ 'emcEquiptUseRecordServiceImpl', selections ])).done(function() {
                Gikam.getComp('emc-equipt-use-record-edit-list-grid').refresh();
            });
        })
    },

    createPage : function() {
        var _this = this;
        Gikam.create('layout', {
            center : {
                items : [ _this.getGridParam() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });

        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    },

};
