emcEquiptCalibration.detailPage = {

    tabIndex : 0,
    calibrationType : 'dioxinCalibration',
    getCalibrationTab : function() {
        var _this = this;
        //var selections = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
        var sampleId= _this.param.id;
        return {
            type : 'tab',
            fill : true,
            id : 'emc-equipt-calibration-tab',
            panels : [ {
                title : '二噁英校准',
                id : 'emc-equipt-calibration-dioxin-panel',
                items : [ {
                    type : 'grid',
                    id : _this.editGridId.dioxinGrid,
                    url : emcEquiptCalibration.baseUrl + '/queries/raw',
                    service : 'emcEquiptCalibrationServiceImpl',
                    columns : emcEquiptCalibration.getDioxinEditListColumns(),
                    checkOneOnActive : true,
                    requestData : {
                        calibrationType_SEQ : 'dioxinCalibration',
                        sampleId_SEQ : sampleId
                    },
                    deleteFormatter : function(row) {
                        return row.ext$.equiptname || row.id;
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        } else {
                            Gikam.cleanCompData([ _this.lineGridId.dioxinFlowGrid, _this.lineGridId.dioxinSmokeGrid ]);
                        }
                    },
                    onRowActive : function(index, row) {
                        let isSmoke = row.calibrationCategory === 'smoke';
                        let activePanel = isSmoke ? _this.linePanelId.dioxinSmokePanel : _this.linePanelId.dioxinFlowPanel;
                        _this.onCalibrationRowActive(activePanel, isSmoke ? _this.lineGridId.dioxinSmokeGrid : _this.lineGridId.dioxinFlowGrid, row);

                        /*Gikam.getComp('emc-equipt-calibration-line-tab').revertPanel(activePanel);
                        Gikam.getComp(isSmoke ? _this.lineGridId.dioxinSmokeGrid : _this.lineGridId.dioxinFlowGrid).refresh({
                            url : Gikam.printf(emcEquiptCalibration.baseUrl + "/{id}/lines/queries", {
                                id : row.id
                            })
                        });

                        Object.keys(_this.linePanelId).filter(item => item !== activePanel).forEach(panel => Gikam.getComp('emc-equipt-calibration-line-tab').remove(panel));*/
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var selections = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
                            Gikam.preInsert({
                                modalTitle : 'EMC.MODULE.MDM.EQUIPT.CALIBRATIONS.TITLE.ADD',
                                titleWidth : 100,
                                fields : emcEquiptCalibration.getDioxinPreInsertFields(),
                                url : emcEquiptCalibration.baseUrl+'/action/insert/' + selections[0].id,
                            }).done(function() {
                                Gikam.getComp(_this.editGridId.dioxinGrid).refresh();
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp(_this.editGridId.dioxinGrid).deleteRows(emcEquiptCalibration.baseUrl);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.BATCH_INPUT',
                        icon : 'add',
                        onClick : function() {
                            _this.samplingBatchInput();
                        }
                    } ]
                } ]
            }, {
                title : '标气校准',
                id : 'emc-equipt-calibration-std-gas-panel',
                items : [ {
                    type : 'grid',
                    id : _this.editGridId.stdGasGrid,
                    url : emcEquiptCalibration.baseUrl + '/queries/raw',
                    service : 'emcEquiptCalibrationServiceImpl',
                    columns : emcEquiptCalibration.getStdGasEditListColumns(),
                    checkOneOnActive : true,
                    requestData : {
                        calibrationType_SEQ : 'stdGasCalibration',
                        sampleId_SEQ : sampleId
                    },
                    deleteFormatter : function(row) {
                        return row.ext$.equiptname || row.id;
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            //this.activeRowByIndex(0);
                        } else {
                            Gikam.cleanCompData([ _this.lineGridId.stdGasGrid ]);
                        }
                    },
                    onRowActive : function(index, row) {
                        _this.onCalibrationRowActive(_this.linePanelId.stdGasPanel, _this.lineGridId.stdGasGrid, row);
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var selections = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
                            console.log(selections);
                            Gikam.preInsert({
                                modalTitle : 'EMC.MODULE.MDM.EQUIPT.CALIBRATIONS.TITLE.ADD',
                                titleWidth : 100,
                                fields : emcEquiptCalibration.getStdGasPreInsertFields(),
                                url : emcEquiptCalibration.baseUrl+'/action/insert/' + selections[0].id,

                            }).done(function() {
                                Gikam.getComp(_this.editGridId.stdGasGrid).refresh();
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp(_this.editGridId.stdGasGrid).deleteRows(emcEquiptCalibration.baseUrl);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.BATCH_INPUT',
                        icon : 'add',
                        onClick : function() {
                            _this.capabilityBatchInput();
                        }
                    } ]
                } ]
            }, {
                title : '流量校准',
                id : 'emc-equipt-calibration-flow-panel',
                items : [ {
                    type : 'grid',
                    id : _this.editGridId.flowGrid,
                    url : emcEquiptCalibration.baseUrl + '/queries/raw',
                    service : 'emcEquiptCalibrationServiceImpl',
                    columns : emcEquiptCalibration.getFlowEditListColumns(),
                    checkOneOnActive : true,
                    requestData : {
                        calibrationType_SEQ : 'flowCalibration',
                        sampleId_SEQ : sampleId
                    },
                    deleteFormatter : function(row) {
                        return row.ext$.equiptname || row.id;
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            // this.activeRowByIndex(0);
                        } else {
                            Gikam.cleanCompData([ _this.lineGridId.flowGrid ]);
                        }
                    },
                    onRowActive : function(index, row) {
                        _this.onCalibrationRowActive(_this.linePanelId.flowPanel, _this.lineGridId.flowGrid, row);
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var selections = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
                            console.log(selections);
                            Gikam.preInsert({
                                modalTitle : 'EMC.MODULE.MDM.EQUIPT.CALIBRATIONS.TITLE.ADD',
                                titleWidth : 100,
                                fields : emcEquiptCalibration.getFlowPreInsertFields(),
                                url : emcEquiptCalibration.baseUrl+'/action/insert/' + selections[0].id,

                            }).done(function() {
                                Gikam.getComp(_this.editGridId.flowGrid).refresh();
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp(_this.editGridId.flowGrid).deleteRows(emcEquiptCalibration.baseUrl);
                        }
                    } ]
                } ]
            }, {
                title : '水质校准',
                id : 'emc-equipt-calibration-water-panel',
                items : [ {
                    type : 'grid',
                    id : _this.editGridId.waterGrid,
                    url : emcEquiptCalibration.baseUrl + '/queries/raw',
                    service : 'emcEquiptCalibrationServiceImpl',
                    columns : emcEquiptCalibration.getWaterCalibrationEditListColumns(),
                    checkOneOnActive : true,
                    requestData : {
                        calibrationType_IN : 'waterTempCalibration,phCalibration,turbidityCalibration,conductivityCalibration,oxygenCalibration',
                        sampleId_SEQ : sampleId
                    },
                    deleteFormatter : function(row) {
                        return row.ext$.equiptname || row.id;
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            //this.activeRowByIndex(0);
                        } else {
                            Gikam.cleanCompData([ _this.lineGridId.waterTempGrid, _this.lineGridId.phGrid, _this.lineGridId.conductivityGrid, _this.lineGridId.turbidityGrid, _this.lineGridId.oxygenGrid ]);
                        }
                    },
                    onRowActive : function(index, row) {
                        let calibrationType = row.calibrationType;
                        let linePanelKey = calibrationType.replace('Calibration', '') + 'Panel';
                        let lineGridKey = calibrationType.replace('Calibration', '') + 'Grid';
                        _this.onCalibrationRowActive(_this.linePanelId[linePanelKey], _this.lineGridId[lineGridKey], row);
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var selections = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
                            console.log(selections);
                            Gikam.preInsert({
                                modalTitle : 'EMC.MODULE.MDM.EQUIPT.CALIBRATIONS.TITLE.ADD',
                                titleWidth : 100,
                                fields : emcEquiptCalibration.getWaterPreInsertFields(),
                                url : emcEquiptCalibration.baseUrl+'/action/insert/' + selections[0].id,

                            }).done(function() {
                                Gikam.getComp(_this.editGridId.waterGrid).refresh();
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp(_this.editGridId.waterGrid).deleteRows(emcEquiptCalibration.baseUrl);
                        }
                    } ]
                } ]
            }, {
                title : '声校准仪校准',
                id : 'emc-equipt-calibration-sound-panel',
                items : [ {
                    type : 'grid',
                    id : _this.editGridId.soundGrid,
                    url : emcEquiptCalibration.baseUrl + '/queries/raw',
                    service : 'emcEquiptCalibrationServiceImpl',
                    columns : emcEquiptCalibration.getSoundEditListColumns(),
                    checkOneOnActive : true,
                    requestData : {
                        calibrationType_SEQ : 'soundCalibration',
                        sampleId_SEQ : sampleId
                    },
                    deleteFormatter : function(row) {
                        return row.ext$.equiptname || row.id;
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            //this.activeRowByIndex(0);
                        } else {
                            Gikam.cleanCompData([ _this.lineGridId.soundGrid ]);
                        }
                    },
                    onRowActive : function(index, row) {
                        _this.onCalibrationRowActive(_this.linePanelId.soundPanel, _this.lineGridId.soundGrid, row);
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var selections = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
                            Gikam.preInsert({
                                modalTitle : 'EMC.MODULE.MDM.EQUIPT.CALIBRATIONS.TITLE.ADD',
                                titleWidth : 100,
                                fields : emcEquiptCalibration.getSoundPreInsertFields(),
                                url : emcEquiptCalibration.baseUrl+'/action/insert/' + selections[0].id,
                            }).done(function() {
                                Gikam.getComp(_this.editGridId.soundGrid).refresh();
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp(_this.editGridId.soundGrid).deleteRows(emcEquiptCalibration.baseUrl);
                        }
                    } ]
                } ]
            }/*, {
                title : 'EMC.MODULE.MDM.EQUIPT.CALIBRATIONS.TITLE.CHOOSE_CALIBRATION',
                id : 'emc-equipt-calibration-choose-panel',
                items : [ {
                    type : 'grid',
                    id : 'emc-equipt-calibration-edit-list-grid',
                    url : '',
                    columns : emcEquiptCalibration.getCalibrationChooseListColumns(),
                    service : 'emcEquiptCalibrationServiceImpl',
                    checkOneOnActive : true,
                    order : {
                        calibrationDate : 'desc'
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(1);
                        } else {
                            Gikam.cleanCompData([ 'emc-equipt-calibration-line-edit-list-grid' ]);
                        }
                    },
                    onRowActive : function(index, row) {
                        _this.onCalibrationRowActive(row);
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'EMC.MODULE.MDM.EQUIPT.CALIBRATIONS.BUTTON.CHOOSE',
                        icon : 'revert',
                        onClick : function() {
                            var selections = Gikam.getComp('emc-equipt-calibration-edit-list-grid').getSelections();
                            if (Gikam.isEmpty(selections)) {
                                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                return;
                            }
                            Gikam.getLastModal().close(selections);
                        }
                    } ],
                    group : {
                        fields : [ 'calibrationDate' ],
                        formatter : function(row) {
                            return row.calibrationDate  || '-';
                        }
                    }
                } ]
            }*/ ],
            onActived : function (data) {
                _this.tabIndex = data.index;
                let linePanels = Object.keys(_this.linePanelId);
                let linePanelTab = Gikam.getComp('emc-equipt-calibration-line-tab');
                if (data.index === 0) {
                    let hidePanels = linePanels.filter(item => item !== 'dioxinSmokePanel' && item !== 'dioxinFlowPanel').map(item => _this.linePanelId[item]);
                    _this.onPanelShowOrHide([_this.linePanelId.dioxinSmokePanel, _this.linePanelId.dioxinFlowPanel], true);
                    _this.onPanelShowOrHide(hidePanels);
                } else if (data.index === 1) {
                    let hidePanels = linePanels.filter(item => item !== 'stdGasPanel').map(item => _this.linePanelId[item]);
                    _this.onPanelShowOrHide([_this.linePanelId.stdGasPanel], true);
                    _this.onPanelShowOrHide(hidePanels);
                } else if (data.index === 2) {
                    let hidePanels = linePanels.filter(item => item !== 'flowPanel').map(item => _this.linePanelId[item]);
                    _this.onPanelShowOrHide([_this.linePanelId.flowPanel], true);
                    _this.onPanelShowOrHide(hidePanels);
                } else if (data.index === 3) {
                    let showName = ['waterTempPanel', 'phPanel', 'conductivityPanel', 'turbidityPanel', 'oxygenPanel'];
                    let hidePanels = linePanels.filter(item => showName.indexOf(item) === -1).map(item => _this.linePanelId[item]);
                    _this.onPanelShowOrHide([_this.linePanelId.waterTempPanel,_this.linePanelId.phPanel,_this.linePanelId.conductivityPanel,_this.linePanelId.turbidityPanel,_this.linePanelId.oxygenPanel ], true);
                    _this.onPanelShowOrHide(hidePanels);
                } else if (data.index === 4) {
                    let hidePanels = linePanels.filter(item => item !== 'soundPanel').map(item => _this.linePanelId[item]);
                    _this.onPanelShowOrHide([_this.linePanelId.soundPanel], true);
                    _this.onPanelShowOrHide(hidePanels);
                }
            }
        }
    },

    onCalibrationRowActive : function(activePanel, activeLineGrid, row) {
        let _this = this;
        console.info(activePanel, activeLineGrid, row)
        //Gikam.getComp('emc-equipt-calibration-line-tab').revertPanel(activePanel);
        Gikam.getComp(activeLineGrid).refresh({
            url : Gikam.printf(emcEquiptCalibration.baseUrl + "/{id}/lines/queries", {
                id : row.id
            })
        });

        /*Object.keys(_this.linePanelId).filter(item => item !== activePanel).forEach(panel => {
            console.info(_this.linePanelId[panel])
            Gikam.getComp('emc-equipt-calibration-line-tab').removePanel(_this.linePanelId[panel]);
        });*/
    },

    onPanelShowOrHide : function(panels, show) {
        let linePanelTab = Gikam.getComp('emc-equipt-calibration-line-tab');
        if (show) {
            panels.forEach(panel => linePanelTab.revertPanel(panel));
        } else {
            panels.forEach(panel => linePanelTab.removePanel(panel));
        }
    },

    getLineGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            id : 'emc-equipt-calibration-line-tab',
            panels : [ /*{
                title : 'EMC.MODULE.MDM.EQUIPT.CALIBRATIONS.TITLE.CALIBRATION_LINE',
                id : 'emc-equipt-calibration-line-panel',
                items : [ {
                    type : 'grid',
                    id : 'emc-equipt-calibration-line-edit-list-grid',
                    url : '',
                    columns : emcEquiptCalibration.getSamplingLineEditListColumns(),
                    service : 'emcEquiptCalibrationLineServiceImpl',
                    checkOneOnActive : true,
                    deleteFormatter : function(row) {
                        return row.ext$.equiptname || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var activedRow = Gikam.getComp(_this.getGridIds()[_this.tabIndex]).getActivedRow();
                            if (!Gikam.isEmpty(activedRow)) {
                                Gikam.getComp('emc-equipt-calibration-line-edit-list-grid').insert(emcEquiptCalibration.baseUrl + "/lines", [ activedRow ]);
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp('emc-equipt-calibration-line-edit-list-grid').deleteRows(emcEquiptCalibration.baseUrl + "/lines");
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.BATCH_INPUT',
                        icon : 'add',
                        onClick : function() {
                            _this.batchInput();
                        }
                    } ]
                } ]
            }, */{
                title : '烟气校准',
                id : _this.linePanelId.dioxinSmokePanel,
                items : [ {
                    type : 'grid',
                    id : _this.lineGridId.dioxinSmokeGrid,
                    url : '',
                    columns : emcEquiptCalibration.getDioxinSmokeLineEditListColumns(),
                    service : 'emcEquiptCalibrationLineServiceImpl',
                    checkOneOnActive : true,
                    deleteFormatter : function(row) {
                        return row.ext$.equiptname || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var activedRow = Gikam.getComp(_this.editGridId.dioxinGrid).getActivedRow();
                            if (!Gikam.isEmpty(activedRow)) {
                                Gikam.getComp(_this.lineGridId.dioxinSmokeGrid).insert(emcEquiptCalibration.baseUrl + "/lines", [ activedRow ]);
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp(_this.lineGridId.dioxinSmokeGrid).deleteRows(emcEquiptCalibration.baseUrl + "/lines");
                        }
                    }]
                } ]
            }, {
                title : '流量校准',
                id : _this.linePanelId.dioxinFlowPanel,
                items : [ {
                    type : 'grid',
                    id : _this.lineGridId.dioxinFlowGrid,
                    url : '',
                    columns : emcEquiptCalibration.getDioxinFlowLineEditListColumns(),
                    service : 'emcEquiptCalibrationLineServiceImpl',
                    checkOneOnActive : true,
                    deleteFormatter : function(row) {
                        return row.ext$.equiptname || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var activedRow = Gikam.getComp(_this.editGridId.dioxinGrid).getActivedRow();
                            if (!Gikam.isEmpty(activedRow)) {
                                Gikam.getComp(_this.lineGridId.dioxinFlowGrid).insert(emcEquiptCalibration.baseUrl + "/lines", [ activedRow ]);
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp(_this.lineGridId.dioxinFlowGrid).deleteRows(emcEquiptCalibration.baseUrl + "/lines");
                        }
                    }]
                } ]
            }, {
                title : '声校准记录',
                id : _this.linePanelId.soundPanel,
                items : [ {
                    type : 'grid',
                    id : _this.lineGridId.soundGrid,
                    url : '',
                    columns : emcEquiptCalibration.getSoundLineEditListColumns(),
                    service : 'emcEquiptCalibrationLineServiceImpl',
                    checkOneOnActive : true,
                    deleteFormatter : function(row) {
                        return row.ext$.equiptname || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var activedRow = Gikam.getComp(_this.editGridId.soundGrid).getActivedRow();
                            if (!Gikam.isEmpty(activedRow)) {
                                Gikam.getComp(_this.lineGridId.soundGrid).insert(emcEquiptCalibration.baseUrl + "/lines", [ activedRow ]);
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp(_this.lineGridId.soundGrid).deleteRows(emcEquiptCalibration.baseUrl + "/lines");
                        }
                    }]
                } ]
            }, {
                title : '水温校准',
                id : _this.linePanelId.waterTempPanel,
                items : [ {
                    type : 'grid',
                    id : _this.lineGridId.waterTempGrid,
                    url : '',
                    columns : emcEquiptCalibration.getWaterTempLineEditListColumns(),
                    service : 'emcEquiptCalibrationLineServiceImpl',
                    checkOneOnActive : true,
                    deleteFormatter : function(row) {
                        return row.ext$.equiptname || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var activedRow = Gikam.getComp(_this.editGridId.waterGrid).getActivedRow();
                            if (!Gikam.isEmpty(activedRow)) {
                                Gikam.getComp(_this.lineGridId.waterTempGrid).insert(emcEquiptCalibration.baseUrl + "/lines", [ activedRow ]);
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp(_this.lineGridId.waterTempGrid).deleteRows(emcEquiptCalibration.baseUrl + "/lines");
                        }
                    }]
                } ]
            }, {
                title : 'PH溶液校准',
                id : _this.linePanelId.phPanel,
                items : [ {
                    type : 'grid',
                    id : _this.lineGridId.phGrid,
                    url : '',
                    columns : emcEquiptCalibration.getWaterPhLineEditListColumns(),
                    service : 'emcEquiptCalibrationLineServiceImpl',
                    checkOneOnActive : true,
                    deleteFormatter : function(row) {
                        return row.ext$.equiptname || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var activedRow = Gikam.getComp(_this.editGridId.waterGrid).getActivedRow();
                            if (!Gikam.isEmpty(activedRow)) {
                                Gikam.getComp(_this.lineGridId.phGrid).insert(emcEquiptCalibration.baseUrl + "/lines", [ activedRow ]);
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp(_this.lineGridId.phGrid).deleteRows(emcEquiptCalibration.baseUrl + "/lines");
                        }
                    }]
                } ]
            }, {
                title : '电导率溶液校准',
                id : _this.linePanelId.conductivityPanel,
                items : [ {
                    type : 'grid',
                    id : _this.lineGridId.conductivityGrid,
                    url : '',
                    columns : emcEquiptCalibration.getWaterCondLineEditListColumns(),
                    service : 'emcEquiptCalibrationLineServiceImpl',
                    checkOneOnActive : true,
                    deleteFormatter : function(row) {
                        return row.ext$.equiptname || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var activedRow = Gikam.getComp(_this.editGridId.waterGrid).getActivedRow();
                            if (!Gikam.isEmpty(activedRow)) {
                                Gikam.getComp(_this.lineGridId.conductivityGrid).insert(emcEquiptCalibration.baseUrl + "/lines", [ activedRow ]);
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp(_this.lineGridId.conductivityGrid).deleteRows(emcEquiptCalibration.baseUrl + "/lines");
                        }
                    }]
                } ]
            }, {
                title : '浊度溶液校准',
                id : _this.linePanelId.turbidityPanel,
                items : [ {
                    type : 'grid',
                    id : _this.lineGridId.turbidityGrid,
                    url : '',
                    columns : emcEquiptCalibration.getWaterTurbLineEditListColumns(),
                    service : 'emcEquiptCalibrationLineServiceImpl',
                    checkOneOnActive : true,
                    deleteFormatter : function(row) {
                        return row.ext$.equiptname || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var activedRow = Gikam.getComp(_this.editGridId.waterGrid).getActivedRow();
                            if (!Gikam.isEmpty(activedRow)) {
                                Gikam.getComp(_this.lineGridId.turbidityGrid).insert(emcEquiptCalibration.baseUrl + "/lines", [ activedRow ]);
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp(_this.lineGridId.turbidityGrid).deleteRows(emcEquiptCalibration.baseUrl + "/lines");
                        }
                    }]
                } ]
            }, {
                title : '溶解氧校准',
                id : _this.linePanelId.oxygenPanel,
                items : [ {
                    type : 'grid',
                    id : _this.lineGridId.oxygenGrid,
                    url : '',
                    columns : emcEquiptCalibration.getWaterOxygenLineEditListColumns(),
                    service : 'emcEquiptCalibrationLineServiceImpl',
                    checkOneOnActive : true,
                    deleteFormatter : function(row) {
                        return row.ext$.equiptname || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var activedRow = Gikam.getComp(_this.editGridId.waterGrid).getActivedRow();
                            if (!Gikam.isEmpty(activedRow)) {
                                Gikam.getComp(_this.lineGridId.oxygenGrid).insert(emcEquiptCalibration.baseUrl + "/lines", [ activedRow ]);
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp(_this.lineGridId.oxygenGrid).deleteRows(emcEquiptCalibration.baseUrl + "/lines");
                        }
                    }]
                } ]
            }, {
                title : '流量校准记录',
                id : _this.linePanelId.flowPanel,
                items : [ {
                    type : 'grid',
                    id : _this.lineGridId.flowGrid,
                    url : '',
                    columns : emcEquiptCalibration.getFlowLineEditListColumns(),
                    service : 'emcEquiptCalibrationLineServiceImpl',
                    checkOneOnActive : true,
                    deleteFormatter : function(row) {
                        return row.ext$.equiptname || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var activedRow = Gikam.getComp(_this.editGridId.flowGrid).getActivedRow();
                            if (!Gikam.isEmpty(activedRow)) {
                                Gikam.getComp(_this.lineGridId.flowGrid).insert(emcEquiptCalibration.baseUrl + "/lines", [ activedRow ]);
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp(_this.lineGridId.flowGrid).deleteRows(emcEquiptCalibration.baseUrl + "/lines");
                        }
                    }]
                } ]
            }, {
                title : '标气校准记录',
                id : _this.linePanelId.stdGasPanel,
                items : [ {
                    type : 'grid',
                    id : _this.lineGridId.stdGasGrid,
                    url : '',
                    columns : emcEquiptCalibration.getStdGasLineEditListColumns(),
                    service : 'emcEquiptCalibrationLineServiceImpl',
                    checkOneOnActive : true,
                    deleteFormatter : function(row) {
                        return row.ext$.equiptname || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var activedRow = Gikam.getComp(_this.editGridId.stdGasGrid).getActivedRow();
                            if (!Gikam.isEmpty(activedRow)) {
                                Gikam.getComp(_this.lineGridId.stdGasGrid).insert(emcEquiptCalibration.baseUrl + "/lines", [ activedRow ]);
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp(_this.lineGridId.stdGasGrid).deleteRows(emcEquiptCalibration.baseUrl + "/lines");
                        }
                    }]
                } ]
            } ]
        }
    },

    // 批录入
    batchInput : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-equipt-calibration-line-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var fields = emcEquiptCalibration.getBatchInputFields();
        var activeRow = Gikam.getComp(_this.editGridId.dioxinGrid).getActivedRow();
        if (_this.calibrationType === 'flowCalibration') {
            fields = emcEquiptCalibration.getBatchInputFields1();
            activeRow = Gikam.getComp(_this.editGridId.flowGrid).getActivedRow();
        } else if (_this.calibrationType === 'stdGasCalibration') {
            fields = emcEquiptCalibration.getBatchInputFields2();
            activeRow = Gikam.getComp(_this.editGridId.stdGasGrid).getActivedRow();
        }

        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
            width : 400,
            height : 270,
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
                                var formData = form.getData();
                                selections.forEach(function (item) {
                                    item.temperature = formData.temperature;
                                    item.humidity = formData.humidity;
                                    item.pressure = formData.pressure;
                                    item.calibrationResult = formData.calibrationResult;
                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(emcEquiptCalibration.baseUrl + '/' + activeRow.id, Gikam.getJsonWrapper({}, [ 'emcEquiptCalibrationLineServiceImpl', selections ])).done(function (data) {
                                    Gikam.getComp('emc-equipt-calibration-line-edit-list-grid').refresh();
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
            columns: 1,
            titleWidth: 80,
            fields: fields,
            autoSave: false
        });

    },

    // 大气采样类批录入
    samplingBatchInput : function() {
        var _this = this;
        var selections = Gikam.getComp(_this.editGridId.dioxinGrid).getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
            width : 400,
            height : 270,
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
                                var formData = form.getData();
                                selections.forEach(function (item) {
                                    item.temperature = formData.temperature;
                                    item.humidity = formData.humidity;
                                    item.pressure = formData.pressure;
                                    item.calibrationResult = formData.calibrationResult;
                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.postText(emcEquiptCalibration.baseUrl + '/action/sampling-batch-input', Gikam.getJsonWrapper({}, [ '', selections ])).done(function () {
                                    Gikam.getComp('emc-equipt-calibration-line-edit-list-grid').refresh();
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
            columns: 1,
            titleWidth: 80,
            fields: emcEquiptCalibration.getBatchInputFields(),
            autoSave: false
        });
    },

    // 仪器性能类批录入
    capabilityBatchInput : function() {
        var _this = this;
        var selections = Gikam.getComp(_this.editGridId.stdGasGrid).getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var activeRow = Gikam.getComp(_this.editGridId.stdGasGrid).getActivedRow();
        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
            width : 400,
            height : 250,
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
                                var formData = form.getData();
                                selections.forEach(function (item) {
                                    item.envTemperature = formData.envTemperature;
                                    item.envPressure = formData.envPressure;
                                    item.envHumidity = formData.envHumidity;
                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(emcEquiptCalibration.baseUrl + '/' + activeRow.id, Gikam.getJsonWrapper({}, [ 'emcEquiptCalibrationServiceImpl', selections ])).done(function (data) {
                                    Gikam.getComp(_this.editGridId.stdGasGrid).refresh();
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
            columns: 1,
            titleWidth: 80,
            fields: emcEquiptCalibration.getCapabilityBatchInputFields(),
            autoSave: false
        });
    },

    editGridId : {
        dioxinGrid: 'emc-equipt-calibration-dioxin-edit-list-grid',
        stdGasGrid: 'emc-equipt-calibration-std-gas-edit-list-grid',
        flowGrid: 'emc-equipt-calibration-flow-edit-list-grid',
        waterGrid: 'emc-equipt-calibration-water-edit-list-grid',
        soundGrid: 'emc-equipt-calibration-sound-edit-list-grid'
    },

    linePanelId : {
        dioxinFlowPanel: 'emc-equipt-calibration-dioxin-flow-line-panel',
        dioxinSmokePanel: 'emc-equipt-calibration-dioxin-smoke-line-panel',
        stdGasPanel: 'emc-equipt-calibration-std-gas-line-panel',
        flowPanel: 'emc-equipt-calibration-flow-line-panel',
        soundPanel: 'emc-equipt-calibration-sound-line-panel',
        waterTempPanel: 'emc-equipt-calibration-water-temp-line-panel',
        phPanel: 'emc-equipt-calibration-water-ph-line-panel',
        conductivityPanel: 'emc-equipt-calibration-water-cond-line-panel',
        turbidityPanel: 'emc-equipt-calibration-water-turb-line-panel',
        oxygenPanel: 'emc-equipt-calibration-water-oxygen-line-panel'
    },

    lineGridId : {
        dioxinFlowGrid: 'emc-equipt-calibration-dioxin-flow-line-edit-list-grid',
        dioxinSmokeGrid: 'emc-equipt-calibration-dioxin-smoke-line-edit-list-grid',
        stdGasGrid: 'emc-equipt-calibration-std-gas-line-edit-list-grid',
        flowGrid: 'emc-equipt-calibration-flow-line-edit-list-grid',
        soundGrid: 'emc-equipt-calibration-sound-line-edit-list-grid',
        waterTempGrid: 'emc-equipt-calibration-water-temp-line-edit-list-grid',
        phGrid: 'emc-equipt-calibration-water-ph-line-edit-list-grid',
        conductivityGrid: 'emc-equipt-calibration-water-cond-line-edit-list-grid',
        turbidityGrid: 'emc-equipt-calibration-water-turb-line-edit-list-grid',
        oxygenGrid: 'emc-equipt-calibration-water-oxygen-line-edit-list-grid'
    },

    // 创建页面
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            north : {
                height : '50%',
                items : [  _this.getCalibrationTab() ]
            },
            center : {
                items : [ _this.getLineGridParam() ]
            }
        });
        Gikam.getAsyncComp('emc-equipt-calibration-edit-list-grid').done(function(grid) {
            grid.options.checkOneOnActive = true;
        });
        Gikam.getAsyncComp(_this.editGridId.dioxinGrid).done(function(grid) {
            grid.options.checkOneOnActive = true;
        });
        Gikam.getAsyncComp(_this.editGridId.stdGasGrid).done(function(grid) {
            grid.options.checkOneOnActive = true;
        });
        Gikam.getAsyncComp(_this.editGridId.flowGrid).done(function(grid) {
            grid.options.checkOneOnActive = true;
        });
        Gikam.getAsyncComp('emc-equipt-calibration-line-edit-list-grid').done(function(grid) {
            grid.options.checkOneOnActive = true;
        });
    },

    // 初始化
    init : function(param) {
        this.param = param;
        console.info(param)
        this.create();
    }
};
