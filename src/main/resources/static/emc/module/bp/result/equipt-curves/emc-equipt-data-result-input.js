emcEquiptCurve.resultInputPage = {
    getGridParam : function() {
        var _this = this;
        // var status = this.param.status;
        var createdUserId = this.param.createdUserId;
        var readStatus = true;
        if (createdUserId === workspace.user.id){
            readStatus = false;
        }
        return {
            type : 'grid',
            id : 'emc-equipt-curve-edit-list-grid',
            url : emcEquiptCurve.baseUrl + '/queries/raw',
            columns : emcEquiptCurve.getEditListColumns(),
            activateFirstRow : true,
            service : 'emcEquiptCurveServiceImpl',
            readonly : readStatus,
            // readonly : _this.param.status == 'published',
            requestData : {
                id_SEQ : _this.param.equiptCurveId
            },
            onRowActive : function (index, row) {
                _this.onProjectActive(row);
            },
        }
    },

    onProjectActive : function (row) {
        var tab = Gikam.getComp('emc-curve-result-edit-list-tab');
        if (row) {
            tab.revertPanel('emc-curve-result-edit-list-equipt-curves-config-panel');
            tab.revertPanel('emc-curve-result-edit-list-curves-liquid-config-panel');
        }
    },

    getResultGrid : function() {
        var _this = this;
        var createdUserId = this.param.createdUserId;
        var readStatus = true;
        if (createdUserId === workspace.user.id){
            readStatus = false;
        }
        return {
            id : 'emc-curve-result-edit-list-tab',
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.RESULT.CURVES.TAB.EQUIPT_CURVES_CONFIG',
                id : 'emc-curve-result-edit-list-equipt-curves-config-panel',
                items : [ {
                    type : 'grid',
                    id : 'emc-curve-result-edit-list-equipt-curves-config-grid',
                    service : 'emcEquiptCurveConfigServiceImpl',
                    readonly : readStatus,
                    url : IFM_CONTEXT + '/secure/emc/module/bp/result/curves/equipt-curves-config/queries',
                    columns : emcEquiptCurve.getEquiptCurvesConfigEditListColumns(),
                    onLoadSuccess : function(data) {
                        if (createdUserId !== workspace.user.id){
                            Gikam.getComp('emc-equipt-curve-config-list-insert-button').hide();
                            Gikam.getComp('emc-equipt-curve-config-list-copy-button').hide();
                            Gikam.getComp('emc-equipt-curve-config-list-delete-button').hide();
                            Gikam.getComp('emc-equipt-curve-config-list-edit-button').hide();
                            Gikam.getComp('emc-equipt-curve-config-file-button').hide();
                        }
                    },
                    requestData : {
                        equiptCurveId_SEQ : _this.param.equiptCurveId
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        id : 'emc-equipt-curve-config-list-insert-button',
                        icon : 'add',
                        onClick : function() {
                            Gikam.preInsert({
                                modalTitle : 'EMC.MODULE.BP.RESULT.CURVES.MODAL.ADD',
                                fields : emcEquiptCurve.getEquiptCurvesConfigEditListColumns(),
                                url : IFM_CONTEXT + '/secure/emc/module/bp/result/curves/'+_this.param.equiptCurveId+'/equipt-curves-config'
                            }).done(function(id) {
                                Gikam.getComp('emc-curve-result-edit-list-equipt-curves-config-grid').refresh();
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.COPY',
                        id : 'emc-equipt-curve-config-list-copy-button',
                        icon : 'copy',
                        onClick : function() {
                            var selections = Gikam.getComp('emc-curve-result-edit-list-equipt-curves-config-grid').getSelections();
                            if (selections.length === 1) {
                                var url = IFM_CONTEXT + '/secure/emc/module/bp/result/curves/copy/equipt-curves-config';
                                Gikam.getComp('emc-curve-result-edit-list-equipt-curves-config-grid').insert(url, selections);
                            } else {
                                Gikam.alert('EMC.MODULE.BP.PREHANDLES.TIP.CHOOSE_ONE_COPE');
                                return;
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        id : 'emc-equipt-curve-config-list-delete-button',
                        icon : 'remove',
                        onClick : function() {
                            var url = IFM_CONTEXT + '/secure/emc/module/bp/result/curves/equipt-curves-config';
                            Gikam.getComp('emc-curve-result-edit-list-equipt-curves-config-grid').deleteRows(url).done(function() {
                                Gikam.getComp('emc-curve-result-edit-list-equipt-curves-config-grid').refresh();
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.BP.RESULT.EQUIPTCURVES.BUTTON.BATCHUNIT',
                        id : 'emc-equipt-curve-config-list-edit-button',
                        icon : 'edit',
                        onClick : function() {
                            var selections = Gikam.getComp('emc-curve-result-edit-list-equipt-curves-config-grid').getSelections();
                            if(selections.length < 1){
                                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                return;
                            }
                            var ids = '';
                            for (var j = 0; j < selections.length; j++) {
                                ids = ids + selections[j].id + ",";
                            }
                            Gikam.preInsert({
                                height: '40%',
                                width: '30%',
                                modalTitle: 'EMC.MODULE.BP.RESULT.EQUIPTCURVES.MODAL.BATCHUNIT',
                                fields: emcEquiptCurve.getBatchUnitInfoFormFields(),
                                url: Gikam.printf(emcEquiptCurve.baseUrl + '/{ids}/batchunit', {
                                    ids: ids,
                                }),
                            }).done(function () {
                                Gikam.getComp('emc-curve-result-edit-list-equipt-curves-config-grid').refresh();
                            });
                        }
                    }, {
                        type: 'dropDownMenu',
                        text: '快速录入',
                        id : 'emc-equipt-curve-config-file-button',
                        items: [{
                            type: 'button',
                            text: 'GIKAM.BUTTON.EXCEL_EXPORT',
                            icon: 'edit',
                            onClick: function () {
                                var row = Gikam.getComp('emc-equipt-curve-edit-list-grid').getActivedRow();
                                if(Gikam.isEmpty(row)){
                                    Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                    return;
                                }
                                Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}/{id}', {
                                    templateCode: 'emcEquiptCurveConfigExportTemplate',
                                    service: 'emcEquiptCurveConfigExportTemplate',
                                    id : row.id
                                })).done(function (fileUri) {
                                    var fileUrl = IFM_CONTEXT + fileUri;
                                    Gikam.download(fileUrl);
                                });
                            }
                        }, {
                            type: 'button',
                            text: 'GIKAM.BUTTON.EXCEL_IMPORT',
                            icon: 'edit',
                            onClick: function () {
                                var row = Gikam.getComp('emc-equipt-curve-edit-list-grid').getActivedRow();
                                if(Gikam.isEmpty(row)){
                                    Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                    return;
                                }
                                Gikam.importExcel({
                                    url: IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'emcEquiptCurveConfigImportFileFunction',
                                    dbTable: 'T_EMC_EQUIPT_CURVE',
                                    bizId : row.id,
                                    onImportSuccess: function (files) {
                                        Gikam.getComp('emc-curve-result-edit-list-equipt-curves-config-grid').refresh();
                                    }
                                });
                            }
                        }]
                    } ]
                } ]
            },{
                title : 'EMC.MODULE.BP.RESULT.CURVES.TAB.CURVES_LIQUID_CONFIG',
                id : 'emc-curve-result-edit-list-curves-liquid-config-panel',
                items : [ {
                    type : 'grid',
                    id : 'emc-curve-result-edit-list-curves-liquid-config-grid',
                    service : 'EmcEquiptCurveStandardServiceImpl',
                    readonly : readStatus,
                    url : IFM_CONTEXT + '/secure/emc/module/bp/result/equipt-curve-standards/queries',
                    requestData : {
                        equiptCurveId_SEQ : _this.param.equiptCurveId
                    },
                    columns : emcEquiptCurve.getCurvesLiquidConfigListColumns(),
                    onLoadSuccess : function(data) {
                        if (createdUserId !== workspace.user.id){
                            Gikam.getComp('emc-equipt-curve-standard-list-insert-button').hide();
                            Gikam.getComp('emc-equipt-curve-standard-list-delete-button').hide();
                        }
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        id : 'emc-equipt-curve-standard-list-insert-button',
                        icon : 'add',
                        onClick : function() {
                            Gikam.preInsert({
                                modalTitle : 'EMC.MODULE.BP.RESULT.CURVES.MODAL.ADD',
                                fields : emcEquiptCurve.getCurvesLiquidConfigListColumns(),
                                url : Gikam.printf(emcEquiptCurve.baseUrl + '/{id}/equipt-curves-standard',{
                                    id : _this.param.equiptCurveId,
                                })
                            }).done(function(id) {
                                Gikam.getComp('emc-curve-result-edit-list-curves-liquid-config-grid').refresh();
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        id : 'emc-equipt-curve-standard-list-delete-button',
                        icon : 'remove',
                        onClick : function() {
                            var url = IFM_CONTEXT + '/secure/emc/module/bp/result/equipt-curve-standards';
                            Gikam.getComp('emc-curve-result-edit-list-curves-liquid-config-grid').deleteRows(url).done(function() {
                                Gikam.getComp('emc-curve-result-edit-list-curves-liquid-config-grid').refresh();
                            });
                        }
                    }]
                } ]
            } ]
        }
    },



    
    /*abolish : function() {
        var gridData = Gikam.getComp('emc-equipt-curve-edit-list-grid').getSelections();

        if (gridData.length > 0) {

            gridData.map(function(item) {
                item.status = 'repealed';
            });
            Gikam.put(emcEquiptCurve.baseUrl + '/' + gridData[0].id, Gikam.getJsonWrapper({}, [ 'emcEquiptCurveServiceImpl', gridData ])).always(function() {
                Gikam.getLastModal().close();
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },*/

    /*published : function() {
        var grid = Gikam.getComp('emc-equipt-curve-edit-list-grid');
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
                Gikam.put(emcEquiptCurve.baseUrl + '/' + gridData[0].id, Gikam.getJsonWrapper({}, [ 'emcEquiptCurveServiceImpl', gridData ])).always(function() {
                    Gikam.getLastModal().close();
                });
            } else {
                Gikam.alert('EMC.MODULE.BP.RESULT.CURVES.TIP.EXPIREDATE_EMPTY');
            }
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },*/

    createPage : function() {
        Gikam.create('layout', {
            north : {
                height : "40%",
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
