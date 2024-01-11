emcAnalysisAudit.runDetailPage = {
    
    getSampleTestGrid : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.RESULT.RUNS.TAB.TSAMPLE_TEST',
                items : [ {
                    type : 'grid',
                    id : 'emc-run-edit-list-sample-test-grid',
                    service : 'emcOrderTaskServiceImpl',
                    url : '/secure/emc/module/bp/bp/order-tasks/queries',
                    columns : emcRun.getSampleTestSecondColumns(),
                    requestData : {
                        status_SEQ : 'logged',
                        runId_SEQ : _this.param.id,
                        status_SEQ : _this.param.status_SEQ
                    },
                    onLoadSuccess : function(data) {
                        data.forEach(function(item, index){
                            Gikam.getComp('emc-run-edit-list-sample-test-grid').toFieldsReadonly(index, [ 'cupNo' ]);
                        });
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        } else {
                            Gikam.getComp('emc-run-edit-list-result-grid').cleanData();
                        }
                    },
                    onRowActive : function(index, row) {
                        _this.onSampleTestActive(row)
                    },
                } ]
            } ]
        }
    },
    
    onSampleTestActive : function(row) {
        Gikam.postText('/secure/emc/module/mdm/bizmdm/std-set-categorys/field-sets/queries', Gikam.getJsonWrapper({
            f : {
                qcCategoryId : row.qcCategoryId,
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
                        width : e.width
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
            var publicColumns = emcRun.getResultPublicColumns();
            var columns = publicColumns.concat(arr);
            columns.forEach(function(item) {
                item.editor = false
            });
            var grid = Gikam.getComp('emc-run-edit-list-result-grid');
            grid.refreshColumns(columns);
            grid.refresh({
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
                    id : 'emc-run-edit-list-result-grid',
                    service : 'emcOrderTaskResultServiceImpl',
                    columns : emcRun.getResultColumns(),
                    readonly : false
                } ]
            } ]
        }
    },
    
    createPage : function() {
        Gikam.create('layout', {
            center : {
                width : '50%',
                items : [ this.getResultGrid() ]
            },
            west : {
                width : '50%',
                items : [ this.getSampleTestGrid() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
            readonly : true
        });
        return this;
    },
    
    init : function(param) {
        this.param = param;
        this.createPage();
    }
}