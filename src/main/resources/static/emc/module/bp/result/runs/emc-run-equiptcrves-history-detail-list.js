emcRunEquiptCrves.detailHistoryPage = {

    getEquiptCrvesHistoryFormGridParam: function () {
        var _this = this;	  
        var activeRow = Gikam.getComp('emc-run-history-equiptcrves-list-grid').getActivedRow();
        var solId = activeRow.solId;
        var id = activeRow.id;
        return {
            id: 'emc-equipt-crves-history-form',
            type: 'form',                                                                                                                                          
            columns: 5,
            titleWidth: 130,
            service: 'PfAnalyticalEquiptCrvesServiceImpl',
            url: Gikam.printf(emcRunEquiptCrves.baseUrl + '/{id}', {
                id: id
            }),        
            requestData : {
            	solId_SEQ: solId,
            },
            autoSave: true,
            fields: emcRunEquiptCrves.getRunEquiptCrvesHistoryDetailListColumns(),
            onUpdated: function (data) {
            	Gikam.getComp('emc-equipt-crves-history-form').refresh();	
            }
        }
    },
    
    getRunEquiptCrvesHistoryDetailDescGridParam: function () {
        var _this = this;
        var activeRow = Gikam.getComp('emc-run-history-equiptcrves-list-grid').getActivedRow();
        var solId = activeRow.solId;
        var selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        var runId = selections[0].id;
        return {
            type: 'grid',
            id: 'emc-run-equiptcrves-history-detail-desc-grid',
            requestData : {
            	solId_SEQ: solId,
            },
            order : {
                'analysisNumber' : 'asc'
            },
            columns: emcRunEquiptCrves.getRunEquiptCrvesHistoryParamListColumns(),
            service: 'PfAnalyticalEquiptDetailServiceImpl',
            url: Gikam.printf('/secure/emc/module/bp/equipt-crves-detail/queries'),
            toolbar : [{
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.getComp('emc-run-equiptcrves-history-detail-desc-grid').insert('/secure/emc/module/bp/equipt-crves-detail/insert', [ { 
                        runId: runId,
                        solId: solId
                    } ]);
                    
                }
            },{
                type : 'button',
                text : 'EMC.MODULE.BP.EQUIPTCRVES.BUTTON.INITIALIZ',
                icon : 'add',
                onClick : function() {
                        Gikam.getComp('emc-run-equiptcrves-history-detail-desc-grid').insert('/secure/emc/module/bp/equipt-crves-detail/add-equipt-info', [ { 
                            runId: runId,
                            solId: solId
                        } ]);
                    
                }
            },{
                type : 'button',
                text : 'GIKAM.BUTTON.BATCH_INPUT',
                icon : 'edit',
                onClick : function() {
                    _this.crvesBatchInput();
                },    	
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-run-equiptcrves-history-detail-desc-grid').deleteRows(IFM_CONTEXT + '/secure/emc/module/bp/equipt-crves-detail')
                },
            }
            ],

	}},
	

    
    getEquiptCrvesTab: function () {
        var _this = this;
        return {
            type: 'tab',
            fill: true,
            panels: [{
                title: 'EMC.MODULE.BP.EQUIPTCRVES.MODAL.INSTRUMENT_PARAMETERS',
                items: [_this.getRunEquiptCrvesHistoryDetailDescGridParam()]
            }]
        }
    },

    getEquiptCrvesRecordLayout: function () {
        var _this = this;
        return {
            type: 'layout',
            id: 'emc-equipt-crves-record-layout',
            center: {
                width: '100%',
                items: [_this.getEquiptCrvesTab()]
            }
        }
    },
    

    getDownLayout: function () {
        var _this = this;
        return {
            type: 'layout',
            id: 'emc-equipt-crves-histoy-detail-center-layout',
            center: {
                items: [_this.getEquiptCrvesRecordLayout()]
            }
        }
    },



    equiptCrvesInitilize : function() {

        Gikam.preInsert({
            url: Gikam.printf(emcRunEquiptCrves.baseUrl + '/add-equipt-info', {

            }),

        }).done(function () {
            Gikam.getComp('emc-run-equiptcrves-history-detail-desc-grid').refresh();
        })

    },
  
    crvesBatchInput : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-run-equiptcrves-history-detail-desc-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

/*        var activeRow = Gikam.getComp('emc-run-equiptcrves-history-detail-desc-grid').getActivedRow();
*/        var modal = Gikam.create('modal', {
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
                                    item.sdSubstances = formData.sdSubstances;
                                    item.sdSolutionVolume = formData.sdSolutionVolume;
                                    item.sdConcentration = formData.sdConcentration;
                                    item.remark = formData.remark;
                                    item.stdSubstanceContent = formData.stdSubstanceContent;
                                    item.ext$.peakareaassayvalue = formData.ext$.peakareaassayvalue;
                                    item.qcType = formData.qcType;
                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/equipt-crves-detail/' + selections[0].id, Gikam.getJsonWrapper({}, [ 'PfAnalyticalEquiptDetailServiceImpl', selections ])).done(function (data) {
                                    Gikam.getComp('emc-run-equiptcrves-history-detail-desc-grid').refresh();
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
            fields: emcRunEquiptCrves.getEquiptCrvesBatchInputFields(),
            autoSave: false
        });

    },
    
    createPage: function () {
        Gikam.create('layout', {
            north: {
                height: '30%',
                items: [this.getEquiptCrvesHistoryFormGridParam()]
            },
            center: {
                items: [this.getDownLayout()]
            },
            renderTo: Gikam.getLastModal().window.$dom,
        });

        return this;
    },

    init: function (param) {
        this.param = param;
        this.createPage();
    }
}
