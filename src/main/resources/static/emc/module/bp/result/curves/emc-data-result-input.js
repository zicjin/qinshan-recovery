emcCurve.resultInputPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-curve-edit-list-grid',
            url : emcCurve.baseUrl + '/queries/raw',
            columns : emcCurve.getEditListColumns(),
            service : 'emcCurveServiceImpl',
            readonly : _this.param.status === 'published',
            requestData : {
                id_SEQ : _this.param.curveId
            },
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.CURVES.BUTTON.PUBLISHED',
                icon : 'edit',
                onClick : function() {
                    _this.published();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.CURVES.BUTTON.ABOLISH',
                icon : 'edit',
                onClick : function() {
                    _this.abolish();
                }
            } ]
        }
    },

    getResultGrid : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.RESULT.CURVES.TAB.RESULT',
                items : [ {
                    type : 'grid',
                    id : 'emc-curve-result-edit-list-grid',
                    service : 'emcOrderTaskResultServiceImpl',
                    url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/queries',
                    readonly : _this.param.status == 'published',
                    requestData : {
                        folderId_SEQ : _this.param.folderId
                    },
                    columns : emcCurve.getResultEditListColumns(),
                    onUpdated : function(data, fields, index) {
                        var grid = this;

                        if (fields.indexOf('originalResult') > -1 || fields.indexOf('standQty') > -1 || fields.indexOf('lowLimit') > -1 || fields.indexOf('highLimit') > -1 || fields.indexOf('revisionRule') > -1) {

                            Gikam.getLastModal().window.showMask(true);

                            Gikam.put(Gikam.printf(emcCurve.baseUrl + '/{id}/action/compute', {
                                id : _this.param.curveId
                            }), Gikam.getJsonWrapper({}, [ '', [ {
                                id : _this.param.curveId,
                                folderId : _this.param.folderId
                            } ] ])).done(function() {
                                Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/queries', Gikam.getJsonWrapper({
                                    n : -1,
                                    f : {
                                        folderId_SEQ : _this.param.folderId
                                    }
                                })).done(function(data) {
                                    Gikam.each(data.rows, function(index, row) {
                                        grid.setData(row, false, true);
                                    });

                                }).always(function() {
                                    Gikam.getLastModal().window.closeMask();
                                });

                            }).fail(function() {
                                Gikam.getLastModal().window.closeMask();
                            });
                        }
                    },
                    toolbar : [ {
                        type : 'dropDownMenu',
                        text : 'EMC.MODULE.BP.RESULT.CURVES.BUTTON.QUICK_INPUT',
                        items : [ {
                            type : 'button',
                            text : 'GIKAM.BUTTON.EXPORT',
                            icon : 'move-up',
                            onClick : function() {
                                _this.exportFile();
                            }
                        }, {
                            type : 'button',
                            text : 'GIKAM.BUTTON.IMPORT',
                            icon : 'move-down',
                            onClick : function() {
                                _this.importFile();
                            }
                        } ]
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.BP.RESULT.CURVES.BUTTON.LOG_COMPUTE',
                        icon : 'revert',
                        hidden : false,
                        onClick : function() {
                            _this.logCompute();
                        }
                    }, {
                        type : 'button',
                        text : '强制过零点',
                        icon : 'revert',
                        hidden : false,
                        onClick : function() {
                            _this.zeroCompute();
                        }
                    }, {
                        type : 'button',
                        text : '删除分析项',
                        icon : 'remove',
                        hidden : false,
                        onClick : function() {
                            _this.delItem();
                        }
                    } ]
                } ]
            } ]
        }
    },

    delItem : function () {
        Gikam.getComp('emc-curve-result-edit-list-grid').deleteRows(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results').done(function() {
            Gikam.getComp('emc-curve-result-edit-list-grid').refresh();
        });
    },

    logCompute : function() {
    	var _this = this;
    	var grid = Gikam.getComp('emc-curve-result-edit-list-grid');
    	Gikam.getLastModal().window.showMask(true);
        Gikam.put(Gikam.printf(emcCurve.baseUrl + '/{id}/action/compute', {
            id : _this.param.curveId
        }), Gikam.getJsonWrapper({}, [ '', [ {
            id : _this.param.curveId,
            folderId : _this.param.folderId,
            type : 'logCompute'
        } ] ])).done(function() {
            Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/queries', Gikam.getJsonWrapper({
                n : -1,
                f : {
                    folderId_SEQ : _this.param.folderId
                }
            })).done(function(data) {
                Gikam.each(data.rows, function(index, row) {
                    grid.setData(row, false, true);
                });

            }).always(function() {
                Gikam.getLastModal().window.closeMask();
            });

        }).fail(function() {
            Gikam.getLastModal().window.closeMask();
        });
	},

    zeroCompute : function() {
        var _this = this;
        var grid = Gikam.getComp('emc-curve-result-edit-list-grid');
        Gikam.getLastModal().window.showMask(true);
        Gikam.put(Gikam.printf(emcCurve.baseUrl + '/{id}/action/compute', {
            id : _this.param.curveId
        }), Gikam.getJsonWrapper({}, [ '', [ {
            id : _this.param.curveId,
            folderId : _this.param.folderId,
            type : 'zeroCompute'
        } ] ])).done(function() {
            Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/queries', Gikam.getJsonWrapper({
                n : -1,
                f : {
                    folderId_SEQ : _this.param.folderId
                }
            })).done(function(data) {
                Gikam.each(data.rows, function(index, row) {
                    grid.setData(row, false, true);
                });

            }).always(function() {
                Gikam.getLastModal().window.closeMask();
            });

        }).fail(function() {
            Gikam.getLastModal().window.closeMask();
        });
    },

    exportFile : function() {
        var _this = this;
        var rightData = Gikam.getComp('emc-curve-result-edit-list-grid').getData();
        if (Gikam.isEmpty(rightData)) {
            Gikam.alert('EMC.MODULE.BP.RESULT.CURVES.TIP.CAN_NOT_EXPORT');
            return;
        }
        var folderId = _this.param.folderId;
        Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}/{id}', {
            templateCode : 'emcCurveExportTemplate',
            service : 'emcCurveExportTemplate',
            id : folderId
        })).done(function(fileUri) {
            var fileUrl = IFM_CONTEXT + fileUri;
            Gikam.emcDownloadFile(fileUrl);
        });
    },

    importFile : function() {
        var _this = this;
        var rightData = Gikam.getComp('emc-curve-result-edit-list-grid').getData();
        if (Gikam.isEmpty(rightData)) {
            Gikam.alert('EMC.MODULE.BP.RESULT.CURVES.TIP.CAN_NOT_IMPORT');
            return;
        }
        var data = Gikam.getComp('emc-curve-result-edit-list-grid').getData();
        var id = data.map(function(e) {
            return e.id;
        });
        Gikam.importExcel({
            url : IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'emcCurveImportFileFunction',
            dbTable : _this.param.curveId,
            bizId : _this.param.folderId,
            onImportSuccess : function(files) {
                Gikam.getComp('emc-curve-result-edit-list-grid').refresh({
                    url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/queries',
                    requestData : {
                        folderId_SEQ : _this.param.folderId
                    }
                });
            }
        });
    },

    abolish : function() {
        var gridData = Gikam.getComp('emc-curve-edit-list-grid').getSelections();

        if (gridData.length > 0) {

            gridData.map(function(item) {
                item.status = 'repealed';
            });
            Gikam.put(emcCurve.baseUrl + '/' + gridData[0].id, Gikam.getJsonWrapper({}, [ 'emcCurveServiceImpl', gridData ])).always(function() {
                Gikam.getLastModal().close();
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    published : function() {
        var grid = Gikam.getComp('emc-curve-edit-list-grid');
        var gridData = grid.getSelections();
        if (gridData.length > 0) {
            if (gridData[0].status == 'published') {
                Gikam.alert('EMC.MODULE.BP.RESULT.CURVES.TIP.PUBLISH_REPEAT');
                return;
            }
            var resultGrid = Gikam.getComp('emc-curve-result-edit-list-grid').getData();
            var isReady = false;
            resultGrid.map(function(item){
                if (!Gikam.isEmpty(item.rn2) && !Gikam.isEmpty(item.rn3)) {
                    isReady = true;
                    return;
                }
            });
            if (!isReady) {
                Gikam.alert('EMC.MODULE.BP.RESULT.CURVES.TIP.INTERCEPTANDSLOPE_EMPTY');
                return;
            }
            if (!Gikam.isEmpty(gridData[0].expireDate)) {
                gridData.map(function(item) {
                    item.status = 'published';
                    delete item.slope;
                    delete item.intercept;
                    delete item.coefficient;
                });
                Gikam.put(emcCurve.baseUrl + '/' + gridData[0].id, Gikam.getJsonWrapper({}, [ 'emcCurveServiceImpl', gridData ])).always(function() {
                    Gikam.getLastModal().close();
                });
            } else {
                Gikam.alert('EMC.MODULE.BP.RESULT.CURVES.TIP.EXPIREDATE_EMPTY');
            }
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            north : {
                height : "30%",
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getResultGrid() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
