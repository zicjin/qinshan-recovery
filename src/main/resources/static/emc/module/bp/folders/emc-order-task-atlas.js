emcOrderTask.atlasPage = {

    getAtlasResultGridParam : function() {
        var _this = this, requestData = Object.assign({}, _this.param);
        requestData['orderTaskId_IN'] = _this.param.orderTaskIds || '0';
        requestData['reportDisplay_SEQ'] = '1';
        return {
            type : 'grid',
            id : 'emc-order-task-atlas-result-grid',
            url : '/secure/emc/module/bp/bp/order-task-results/queries',
            requestData : requestData,
            columns : emcOrderTask.getAtlasResultColumns()
        }
    },

    getAtlasParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            width : Gikam.width.M,
            panels : [ {
                title : 'EMC.MODULE.BP.BP.FOLDERS.TITLE.ATLAS',
                items : [ {
                    type : 'grid',
                    id : 'emc-order-task-atlas-grid1',
                    url : '/core/module/item/files/queries',
                    requestData : {
                        targetId_IN : this.param.targetIds,
                    },
                    columns : emcOrderTask.getAtlasColumns(),
                    onCellClick : function(field, row) {
                        field === 'name' && Gikam.openFile(row.id, true);
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            Gikam.getComp('emc-order-task-atlas-grid1').deleteRows(IFM_CONTEXT + '/core/module/item/files');
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.BATCH_DOWNLOAD',
                        icon : 'batch-download',
                        onClick : function() {
                            let selections = Gikam.getComp('emc-order-task-atlas-grid1').getSelections();
                            console.log(selections);
                            var runGrid = Gikam.getComp('emc-run-edit-list-test-run-grid');
                            var runId = 0;
                            if (!Gikam.isEmpty(runGrid)) {
                                let runSelections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
                                console.log(runSelections);
                                runId = runSelections[0].id;
                            }else{
                                runId = _this.param.runId_SEQ
                            }

                            if (Gikam.isEmpty(selections)) {
                                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                return;
                            }
                            let fileIds = selections.map(item => {
                                return {id: item.id};
                            });
                            Gikam.postText(IFM_CONTEXT + '/core/module/item/files/action/pack',Gikam.getJsonWrapper({}, [ '', fileIds ])).done(function (fileUrl) {
                                if (!Gikam.isEmpty(fileUrl)) {
                                    Gikam.emcDownloadFile(IFM_CONTEXT + fileUrl,runId);
                                }
                            })
                        }
                    },{
                        type : 'button',
                        text : '下载',
                        icon : 'browse',
                        onClick : function() {
                            let selections = Gikam.getComp('emc-order-task-atlas-grid1').getSelections();
                            if (selections.length != 1) {
                                Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                return;
                            }
                            var selectedRow = selections[0];
                            Gikam.emcDownloadFileById(selectedRow.id);

                        }
                    } ]
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-order-task-atlas-layout',
            renderTo : Gikam.getLastModal().window.$dom,

            west : {
                width : '500',
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.BP.BP.FOLDERS.TITLE.RESULT',
                        items : [ _this.getAtlasResultGridParam() ]
                    } ]
                } ]
            },
            center : {
                items : [ this.getAtlasParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
