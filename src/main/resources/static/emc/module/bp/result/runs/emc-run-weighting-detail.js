emcRunWeighing.detailPage = {

    getAnalysisGrid : function (){
        var _this = this;
        var analysisGridId = 'emc-run-detail-result-input-list-grid';
        var analysisType = 'analysis';
        return {
            type : 'grid',
            id : analysisGridId,
            service : 'emcOrderTaskResultServiceImpl',
            instantSavePath : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/instant',
            filter : false,
            readonly : _this.param.readOnly === 'true',
            generalButtonGroup : [],
            toolbar : [{
                type : 'button',
                text : 'GIKAM.BUTTON.BATCHUPDADE',
                icon : 'modify',
                onClick : function() {
                    _this.batchInput();
                }
            },{
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.INVALID_DATA',
                icon : 'edit',
                onClick : function() {
                    _this.invalidData();
                }
            },{
                type : 'button',
                text : '新增VOC送样瓶号',
                icon : 'edit',
                onClick : function() {
                    _this.insertDeliverBottle();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS_WEIGHTING.BUTTON.SYNC_VOC_BOTTLE_NUMBER',
                icon : 'modify',
                onClick : function() {
                    _this.syncVocBottleNumber();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS_WEIGHTING.BUTTON.SYNC_OIL_FILMNO',
                icon : 'modify',
                onClick : function() {
                    _this.syncOilFilmNo();
                }
            }, {
                type : 'button',
                text : '同步采样瓶瓶号',
                icon : 'modify',
                onClick : function() {
                    _this.syncSampleBottlemNo();
                }
            }],
            onRendered : function() {
                _this.renderResultInputGrid(_this.param.runId, analysisGridId, analysisType, _this.getResultInputColumns());
            },
            beforeUpdate : function(data, keys) {
            	var cellField = keys[2];
            	console.log(keys);
            	console.log(data);
                for(var i = 0;i<keys.length; i++){
                    if(keys[i] !== 'id' && keys[i] !== 'row' && keys[i] !== 'ext$'){
                        cellField = keys[i];
                    }
                }

                if (cellField === 'weighingDate') {
                    Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/update-weighing-date', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', [{
                        orderId : data.row.orderId,
                        runId : _this.param.runId,
                        weighingDate : data[cellField]
                    }] ]));
                    return false;
                }

                if (cellField === 'weightEndDate') {
                    Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/update-weighing-end-date', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', [{
                        orderId : data.row.orderId,
                        runId : _this.param.runId,
                        weightEndDate : data[cellField]
                    }] ]));
                    return false;
                }
                
                if (cellField === 'vocBottle') {
                    var orderId = data?.row?.orderId;
                    if (Gikam.isEmpty(orderId)) {
                        orderId = Gikam.getComp(analysisGridId).getActivedRow().orderId;
                    }
                    Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/update-voc-bottle', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', [{
                        orderId : orderId,
                        runId : _this.param.runId,
                        vocBottle : data[cellField]
                    }] ])).done(function (){
                        _this.refreshResultInputGrid(_this.param.runId, 'emc-run-detail-result-input-list-grid', 'analysis', _this.getResultInputColumns());
                    });
                    return false;
                }

                if (cellField === 'oilFilmNo') {
                    var orderId = data?.row?.orderId;
                    if (Gikam.isEmpty(orderId)) {
                        orderId = Gikam.getComp(analysisGridId).getActivedRow().orderId;
                    }
                    Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/update-oil-filmno', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', [{
                        orderId : orderId,
                        runId : _this.param.runId,
                        oilFilmNo : data[cellField]
                    }] ])).done(function (){
                        _this.refreshResultInputGrid(_this.param.runId, 'emc-run-detail-result-input-list-grid', 'analysis', _this.getResultInputColumns());
                    });
                    return false;
                }

                if (cellField === 'sampleBottleNo') {
                    var orderId = data?.row?.orderId;
                    if (Gikam.isEmpty(orderId)) {
                        orderId = Gikam.getComp(analysisGridId).getActivedRow().orderId;
                    }
                    Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/update-sample-bottle', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', [{
                        orderId : orderId,
                        runId : _this.param.runId,
                        sampleBottleNo : data[cellField]
                    }] ])).done(function (){
                        _this.refreshResultInputGrid(_this.param.runId, 'emc-run-detail-result-input-list-grid', 'analysis', _this.getResultInputColumns());
                    });
                    return false;
                }
                
                var cellValue = data[cellField];
                var cellExt = data.row.ext$[cellField + '_ext$'];
                var submitFlag = cellExt['submitflag'];
                var orderTaskId = data.row.id;
                var resultId = cellExt['id'];
                // 判断结果类型
                var resultCategory = cellExt['resultcategory'];
                if (resultCategory === 'number') {
                    var forbidden = false;
                    if (Gikam.isNotEmpty(cellValue)) {
                        var regF = /^(-?\d+)(\.\d+)?$/;
                        if (cellValue.indexOf('0') === 0 && cellValue.length > 1 && cellValue.indexOf('0.') === -1) {
                            forbidden = true;
                        }
                        if ((cellValue.indexOf('-0') === 0 && cellValue.length === 2) || (cellValue.indexOf('-0.') === 0 && cellValue.length === 3)) {
                            forbidden = true;
                        }
                        if (!regF.test(cellValue)) {
                            forbidden = true;
                        }
                        if (forbidden) {
                            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.FORMAT_ERROR');
                            _this.refreshResultInputGrid(_this.param.runId, 'emc-run-detail-result-input-list-grid', 'analysis', _this.getResultInputColumns());
                            return false;
                        }
                    }
                }
/*                if (!Gikam.isEmpty(cellValue) && submitFlag === '1') {
                    Gikam.create('modal', {
                        width : 400,
                        height : 200,
                        title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.TITLE.UPDATE_REASON',
                        url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/page/update-result-reason',
                        onAfterClose : function(reasonData) {
                            if (!Gikam.isEmpty(reasonData)) {
                                workspace.window.showMask(true);
                                var resultLog = {
                                    orderTaskId : orderTaskId,
                                    resultId : resultId,
                                    reason : reasonData.reason,
                                    value : cellValue,
                                };
                                Gikam.post(IFM_CONTEXT + Gikam.printf('/secure/emc/module/bp/bp/order-tasks/{id}/result-logs', {
                                    id : orderTaskId
                                }), Gikam.getJsonWrapper({
                                    f : {
                                        folderId : data.row.folderId,
                                    }
                                }, [ '', [ resultLog ] ])).done(function() {
                                    _this.refreshResultInputGrid(_this.param.runId, 'emc-run-detail-result-input-list-grid', 'analysis', _this.getResultInputColumns());
                                }).always(function() {
                                    workspace.window.closeMask();
                                });
                            }
                        }
                    });
                    return false;
                }*/
            },
            onUpdated : function(data, fields, index) {
                workspace.window.showMask(true);
                var orderTaskResultUrl = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results';
                data['originalResult'] = data['colVal'];

                var url = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/action/update-weighing-item';
                Gikam.postText(url, Gikam.getJsonWrapper({}, [ 'emcOrderTaskResultServiceImpl', [ data ] ])).done(function () {
                    // 查询数据
                    var activedRow = { id : _this.param.runId, analysisType : 'analysis' };
                    Gikam.post(orderTaskResultUrl + '/result-inputs/table-rows/queries', Gikam.getJsonWrapper({ f : { useType_SEQ : 'weighing'} }, [ '', [ activedRow ] ])).done(function(rows) {
                        var grid = Gikam.getComp('emc-run-detail-result-input-list-grid');
                        Gikam.each(rows, function(index, row) {
                            grid.setData(row, false, true);
                        });
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }).fail(function() {
                    workspace.window.closeMask();
                });

            }
        };
    },

    renderResultInputGrid : function(runId, gridId, analysisType, initColumns) {
        var _this = this,
            inputGrid = Gikam.getComp(gridId);
        var baseUrl = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/result-inputs';
        // 获取动态列表头字段
        Gikam.post(baseUrl + '/table-heads/queries', Gikam.getJsonWrapper({
            f: {
                USETYPE_SEQ : 'weighing'
            }
        }, [ '', [ {id : runId, analysisType : analysisType } ] ])).done(function(columns) {
            var oldColumns = initColumns;
            var textStyleFormatter = function() {
                return {
                    backgroundColor : 'palegreen'
                }
            };

            var condArr = [];
            columns.forEach(function(column) {
                var columnfield_ext$ = column.field + '_ext$';

                column['width'] = 100;
                column['editor'] = column.ext$.script === '0';
                column['sort'] = false;
                if (column.ext$.script === '0') {
                    column['textStyleFormatter'] = textStyleFormatter;
                };
                column['onBeforeEditorRender'] = function(row) {

                    if (row.ext$ && row.ext$[columnfield_ext$] && row.ext$[columnfield_ext$]['scriptflag'] && row.ext$[columnfield_ext$]['scriptflag'] === '0') {
                        return true;
                    }

                    return false;
                };
                column['formatter'] = function(i, v, r) {

                    if (r.ext$ && r.ext$[columnfield_ext$] === undefined) {
                        return '/';
                    }

                    return v;
                };

                condArr.push(column);
            });
            var newColumns = Gikam.isEmpty(condArr) || condArr.length == 0 ? oldColumns : oldColumns.slice(0).concat(condArr);
            newColumns.forEach(item => {
                item.fixed = true;
            })
            inputGrid.refreshColumns(newColumns);

            // 查询数据
            Gikam.post(baseUrl + '/table-rows/queries', Gikam.getJsonWrapper({ f : { useType_SEQ : 'weighing'} }, [ '', [ {id : runId, analysisType : analysisType} ] ])).done(function(data) {
                inputGrid.loadData(data);
            });
        });
    },

    getResultInputColumns : function() {
        var _this = this;
        return [ {
            checkbox : true,
            fixed : true
        }, {
            index : true,
            fixed : true
        }, {
            field : 'qcName',
            title : 'T_EMC_ORDER_TASK.QCNAME',
            width : 100,
            fixed : true
        }, {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
            width : 150,
            fixed : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER_TASK.ORDERID',
            width : 150,
            fixed : true,
            formatter : function (field, value, row) {
                return row.relatedQcOrderNo || value;
            }
        }, {
            field : 'weighingDate',
            title : 'T_EMC_ORDER.WEIGHINGDATE',
            type : 'date',
            width : 100,
            fixed : true,
            editor : true
        }, {
            field : 'weightEndDate',
            title : 'T_EMC_ORDER.WEIGHTENDDATE',
            type : 'date',
            width : 100,
            fixed : true,
            editor : true
        } , {
            field : 'vocBottle',
            title : 'T_EMC_ORDER_TASK.VOCBOTTLE',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'vocBottle',
            width : 150,
            fixed : true,
            single : true,
            onBeforeChoose : function(index) {
            	var row = Gikam.getComp('emc-run-detail-result-input-list-grid').getActivedRow();
                return {
                    orderId : this.getData()[index].orderId,
                    orgId_SEQ: workspace.user.ext$.parentorgid
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                        vocBottle : rows[0].vocNumber
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    vocBottle : '',
                });
            }
        }, {
            field : 'oilFilmNo',
            title : 'T_EMC_ORDER.OILFILMNO',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'oilFilm',
            single : true,
            onBeforeChoose : function(index) {
            	var row = Gikam.getComp('emc-run-detail-result-input-list-grid').getActivedRow();
                return {
                    orderId : this.getData()[index].orderId,
                    orgId_SEQ: workspace.user.ext$.parentorgid
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                        oilFilmNo : rows[0].oilFilmNo
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    oilFilmNo : ''
                });
            }
        } , {
            field : 'sampleBottleNo',
            title : '采样瓶瓶号',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'sampleBottle',
            width : 150,
            fixed : true,
            single : true,
            onBeforeChoose : function(index) {
                var row = Gikam.getComp('emc-run-detail-result-input-list-grid').getActivedRow();
                return {
                    orderId : this.getData()[index].orderId,
                    orgId_SEQ: workspace.user.ext$.parentorgid
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                        sampleBottleNo : rows[0].sampleBottleNo
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    sampleBottleNo : '',
                });
            }
        }];
    },

    refreshResultInputGrid : function(runId, gridId, analysisType) {
        // 查询数据
        var baseUrl = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/result-inputs';
        Gikam.post(baseUrl + '/table-rows/queries', Gikam.getJsonWrapper({}, [ '', [ {id : runId, analysisType : analysisType} ] ])).done(function(data) {
            Gikam.getComp(gridId).loadData(data);
        });
    },

    /** 批录入 */
    batchInput : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-run-detail-result-input-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
            width : 360,
            height : 200,
        });
        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type: 'btnToolbar',
                    items: [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        icon : 'select',
                        onClick : function() {
                            if (form.validate()) {
                                let formData = form.getData();
                                let updateData = selections.map(item => {
                                    return {
                                        orderId : item.orderId,
                                        runId : _this.param.runId,
                                        weighingDate : formData.weighingDate,
                                        weightEndDate : formData.weightEndDate,

                                    }
                                });

                                Gikam.getLastModal().window.showMask(true);
                                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/update-weighing-date', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', updateData ])).done(function () {
                                    _this.refreshResultInputGrid(_this.param.runId, 'emc-run-detail-result-input-list-grid', 'analysis');
                                });
                                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/update-weighing-end-date', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', updateData ])).done(function () {
                                    _this.refreshResultInputGrid(_this.param.runId, 'emc-run-detail-result-input-list-grid', 'analysis');
                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close();
                                });
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
                        icon : 'cancel',
                        onClick : function () {
                            modal.close();
                        }
                    } ]
                }]
            }
        });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            id : 'emc-weighing-detail-batch-input-form',
            columns: 1,
            titleWidth: 70,
            fields: [{
                field : 'weighingDate',
                title : 'T_EMC_ORDER.WEIGHINGDATE',
                type : 'date',
                editor : true
            },{
                field : 'weightEndDate',
                title : 'T_EMC_ORDER.WEIGHTENDDATE',
                type : 'date',
                editor : true
            }],
            autoSave: false
        });
    },

    invalidData : function(){
        var _this = this;
            var orderSelections = Gikam.getComp('emc-run-detail-result-input-list-grid').getSelections();
            var row = Gikam.getComp('emc-run-weighing-list-test-run-grid').getActivedRow();

            if (Gikam.isEmpty(orderSelections)) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                return;
            }
            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/action/invalid-weight-result', Gikam.getJsonWrapper({
                f : {
                    runId : row.id
                }
            }, [ 'emcOrderTaskServiceImpl', orderSelections ])).done(function () {
                _this.refreshResultInputGrid(_this.param.runId, 'emc-run-detail-result-input-list-grid', 'analysis');
            	Gikam.toast('无效成功', 3, 'top');
            }).always(function () {
            });

    },

    syncVocBottleNumber : function(){
        var _this = this;
        var orderSelections = Gikam.getComp('emc-run-detail-result-input-list-grid').getSelections();
        if (Gikam.isEmpty(orderSelections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let updateData = orderSelections.map(item => {
            return {
                orderId : item.orderId,
                runId : _this.param.runId
            }
        });

        Gikam.getLastModal().window.showMask(true);
        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/sync-voc-bottle', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', updateData ])).done(function () {
            _this.refreshResultInputGrid(_this.param.runId, 'emc-run-detail-result-input-list-grid', 'analysis');
        }).always(function () {
            Gikam.getLastModal().window.closeMask();
        });
    },
  
    syncOilFilmNo : function(){
        var _this = this;
        var orderSelections = Gikam.getComp('emc-run-detail-result-input-list-grid').getSelections();
        if (Gikam.isEmpty(orderSelections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let updateData = orderSelections.map(item => {
            return {
                orderId : item.orderId,
                runId : _this.param.runId
            }
        });

        Gikam.getLastModal().window.showMask(true);
        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/sync-oil-filmno', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', updateData ])).done(function () {
            _this.refreshResultInputGrid(_this.param.runId, 'emc-run-detail-result-input-list-grid', 'analysis');
        }).always(function () {
            Gikam.getLastModal().window.closeMask();
        });
    },

    syncSampleBottlemNo : function(){
        var _this = this;
        var orderSelections = Gikam.getComp('emc-run-detail-result-input-list-grid').getSelections();
        if (Gikam.isEmpty(orderSelections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let updateData = orderSelections.map(item => {
            return {
                orderId : item.orderId,
                runId : _this.param.runId
            }
        });

        Gikam.getLastModal().window.showMask(true);
        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/sync-sample-bottle', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', updateData ])).done(function () {
            _this.refreshResultInputGrid(_this.param.runId, 'emc-run-detail-result-input-list-grid', 'analysis');
        }).always(function () {
            Gikam.getLastModal().window.closeMask();
        });
    },



    insertDeliverBottle : function(){
        var _this = this;
        var orderSelections = Gikam.getComp('emc-run-detail-result-input-list-grid').getSelections();
        var orderNo = orderSelections[0].orderNo;
        var orderTaskId = orderSelections[0].id;

        if (Gikam.isEmpty(orderSelections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/voc/action/insert-deliver-bottle', Gikam.getJsonWrapper({
        },  [ 'emcOrderTaskServiceImpl', orderSelections ])).done(function () {
            _this.refreshResultInputGrid(_this.param.runId, 'emc-run-detail-result-input-list-grid', 'analysis');
        	Gikam.toast('生成成功', 3, 'top');
        }).always(function () {
            Gikam.getLastModal().window.closeMask();
        });
    },

    createPage : function() {
        Gikam.create('layout', {
            id : 'emc-run-detail-result-input-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getAnalysisGrid() ]
            }
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
