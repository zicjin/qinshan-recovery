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
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.create('modal', {
                        title : '选择分析项组',
                        width : 1000,
                        height : 580,
                        // url : emcDetectionfactor.schemeUrl + '/page/select-monitor-tmpl-detection' + Gikam.param({
                        //     editMonitorInfo : '1'
                        // }),
                        url : emcDetectionfactor.testUrl + '/page/item-group-choose-list'+ Gikam.param({
                            target : 'detectionFactor'
                        }),
                        onAfterClose : function(data) {
                            if (!Gikam.isEmpty(data)) {
                                workspace.window.showMask(true);
                                Gikam.post(emcDetectionfactor.baseUrl + '/insertByItemGroup' , Gikam.getJsonWrapper({},[ 'emcItemGroupServiceImpl', data ])).done(function() {
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
                                        // monitorTmplGroupName : data.monitorTmplGroupName,
                                        testCategoryId : data.testCategoryId,
                                        samplePrice : data.samplePrice,
                                        itemGroupName : data.itemGroupName,
                                        testName : data.itemGroupName,
                                        // trialscope : data.trialscope,
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
