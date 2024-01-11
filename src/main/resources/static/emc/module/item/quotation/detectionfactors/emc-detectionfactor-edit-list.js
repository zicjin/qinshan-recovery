emcDetectionfactor.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-detectionfactor-edit-list-grid',
            url : emcDetectionfactor.baseUrl + '/queries/raw',
            columns : emcDetectionfactor.getEditListColumns(),
            service : 'emcDetectionfactorServiceImpl',
            page : true,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                }
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('emc-detectionfactor-edit-list-base-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcDetectionfactor.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });

            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.create('modal', {
                        title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.SELECT_MONITOR_TMPL',
                        width : 1000,
                        height : 580,
                        url : emcDetectionfactor.schemeUrl + '/page/select-monitor-tmpl-detection' + Gikam.param({
                            editMonitorInfo : '1'
                        }),
                        onAfterClose : function(tmlData) {
                            if (!Gikam.isEmpty(tmlData)) {
                                workspace.window.showMask(true);
                                Gikam.post(emcDetectionfactor.baseUrl, Gikam.getJsonWrapper({},[ 'emcDetectionfactorServiceImpl', tmlData ])).done(function() {
                                    Gikam.getComp('emc-detectionfactor-edit-list-grid').refresh();
                                }).always(function() {
                                    workspace.window.closeMask();
                                });
                            }
                        }
                    });
                }
            },{
                type : 'button',
                text : 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONINFORMATION.BUTTON.BATCHINPUT',
                icon : 'add',
                onClick : function() {
                    var selections = Gikam.getComp('emc-detectionfactor-edit-list-grid').getSelections();
                    if (selections.length > 0) {
                        Gikam.create('modal', {
                            width : 400,
                            height : 250,
                            title : 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.BATCH_UPDATE_ORDER',
                            url : emcDetectionfactor.baseUrl  + '/page/batch-update-detection',
                            onAfterClose : function(data) {
                                if (!Gikam.isEmpty(data)) {
                                    workspace.window.showMask(true);
                                    selections.forEach(function(item) {
                                        item.samplePrice = data.samplePrice;
                                        item.analysisPrice = data.analysisPrice;
                                    });

                                    workspace.window.showMask(true);
                                    Gikam.put(emcDetectionfactor.baseUrl + '/instant', Gikam.getJsonWrapper(null, [ 'emcDetectionfactorServiceImpl', selections ])).done(function() {
                                        Gikam.getComp('emc-detectionfactor-edit-list-grid').refresh();
                                    }).always(function() {
                                        workspace.window.closeMask();
                                    });
                                }
                            }
                        });
                    } else {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                    }
                }
            },{
                type : 'button',
                text : 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONINFORMATION.BUTTON.ADDCUSTOMPRICE',
                icon : 'add',
                onClick : function() {
                    Gikam.create('modal', {
                        width : 400,
                        height : 400,
                        title : 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONINFORMATION.BUTTON.ADDCUSTOMPRICE',
                        url : emcDetectionfactor.baseUrl  + '/page/add-custom-price',
                        onAfterClose : function(data) {
                            if (!Gikam.isEmpty(data)) {
                                workspace.window.showMask(true);
                                var insertDataList = []
                                var insertData = {
                                        analysisPrice : data.analysisPrice,
                                        methodId : data.methodId,
                                        methodName : data.methodName,
                                        monitorTmplGroupName : data.monitorTmplGroupName,
                                        samplePrice : data.samplePrice,
                                        itemGroupName : data.itemGroupName,
                                        trialscope : data.trialscope,
                                    };
                                insertDataList.push(insertData);
                                Gikam.post(emcDetectionfactor.baseUrl, Gikam.getJsonWrapper({},[ 'emcDetectionfactorServiceImpl', insertDataList ])).done(function() {
                                    Gikam.getComp('emc-detectionfactor-edit-list-grid').refresh();
                                }).always(function() {
                                    workspace.window.closeMask();
                                });
                            }
                        }
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-detectionfactor-edit-list-grid').deleteRows(emcDetectionfactor.baseUrl);
                }
            } ]
        }
    },


    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-detectionfactor-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                width : 300,
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
