emcRunEdit.editUndoRunPage = {

    getTestRunGrid : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.BP.RESULT.RUNS.TAB.TEST_RUN',
                items : [ {
                    type : 'grid',
                    id : 'emc-undo-run-edit-list-test-run-grid',
                    url : emcRunEdit.runUrl + '/queries/all',
                    columns : emcRunEdit.getUndoTestRunColumns(),
                    page : true,
                    requestData : {
                        // analysisUserId_SC : workspace.user.id,
                        isundo_SEQ : '1'
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        } else {
                            Gikam.getComp('emc-undo-run-edit-list-sample-test-grid').cleanData();
                        }
                    },
                    onRowActive : function(index, row) {
                        _this.onTestRunActive(row)
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.SUBMIT',
                        icon : 'submit',
                        onClick : function() {
                            let grid = Gikam.getComp('emc-undo-run-edit-list-test-run-grid');
                            let selections = grid.getSelections();
                            if (selections.length == 0) {
                                Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                return;
                            }

                            Gikam.confirm("GIKAM.TIP.CONFIRMSUBMIT","GIKAM.TIP.CONFIRMSUBMIT",function (){
                                let grid = Gikam.getComp('emc-undo-run-edit-list-test-run-grid');
                                let selections = grid.getSelections();
                                Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/report/reports/submitUndid', Gikam.getJsonWrapper({}, [ '', selections])).done(function (){
                                    grid.refresh();
                                });
                            })
                        }
                    } ]
                } ]
            } ]
        }
    },

    onTestRunActive : function(row) {
        Gikam.getComp('emc-undo-run-edit-list-sample-test-grid').refresh({
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/order-group-runs',
            requestData : {
                runId_SEQ : row.id
            }
        });

    },

    getSampleTestGrid : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.RESULT.RUNS.TAB.TSAMPLE_TEST',
                items : [ {
                    type : 'grid',
                    id : 'emc-undo-run-edit-list-sample-test-grid',
                    service : 'emcOrderTaskServiceImpl',
                    requestData : {
                        isundo_SEQ : '1'
                    },
                    columns : emcRunEdit.getUndoSampleTestColumns(),
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        } else {
                            Gikam.getComp('emc-undo-run-edit-list-result-grid').cleanData();
                        }
                    },
                    onRowActive : function(index, row) {
                        _this.onSampleTestActive(row)
                    },
                    readonly: true
                } ]
            } ]
        }
    },

    onSampleTestActive : function(row) {
        Gikam.postText('/secure/emc/module/mdm/bizmdm/std-set-categorys/field-sets/queries', Gikam.getJsonWrapper({
            f : {
                qcCategoryCode : row.qcCategoryCode
            },
            n : -1,
            s : 50
        })).done(function(str) {
            var jsonArr = JSON.parse(str);
            var arr = [];
            jsonArr.forEach(e => {
                if(e.display=='displayedit') {
                    arr.push({
                        field : e.fieldName,
                        title : e.fieldTitle,
                        editor : true,
                        width : e.width,
                    });
                } else if(e.display=='displaynoedit') {
                    arr.push({
                        field : e.fieldName,
                        title : e.fieldTitle,
                        editor : false,
                        width : e.width,
                    });
                } else if(Gikam.isEmpty(e.display)) {
                    arr.push({
                        field : e.fieldName,
                        title : e.fieldTitle,
                        width : e.width,
                    });
                }
            });
            var publicColumns = emcRunEdit.getResultPublicColumns();
            var columns = publicColumns.concat(arr);
            Gikam.getComp('emc-undo-run-edit-list-result-grid').refreshColumns(columns);
            Gikam.getComp('emc-undo-run-edit-list-result-grid').refresh({
                url : '/secure/emc/module/bp/bp/order-task-results/queries',
                requestData : {
                    orderId_SEQ : row.orderId,
                    runId_SEQ : row.runId,
                    orderTaskId_SEQ : ''
                }
            });

        });
    },

    getResultGrid : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.RESULT.RUNS.TAB.RESULT',
                items : [ {
                    type : 'grid',
                    id : 'emc-undo-run-edit-list-result-grid',
                    service : 'emcOrderTaskResultServiceImpl',
                    columns : emcRunEdit.getUndoResultPublicColumns(),
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        }
                    },
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-undo-run-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '50%',
                items : [ _this.getTestRunGrid() ]
            },
            center : {
                width : 1000,
                items : [ {
                    type : 'layout',
                    west : {
                        width : '50%',
                        items : [ _this.getSampleTestGrid() ]
                    },
                    center : {
                        width : '50%',
                        items : [ _this.getResultGrid() ]
                    }
                } ]

            }
        });
    },

    init : function() {
        this.create();
    }
};
