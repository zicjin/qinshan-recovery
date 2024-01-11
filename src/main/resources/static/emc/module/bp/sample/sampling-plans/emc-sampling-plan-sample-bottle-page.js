emcSamplingPlan.sampleBottlePage = {
    getSampleBottlePageGridParam : function() {
        var _this = this;
        var columns = emcSamplingPlan.getSampleBottlePageColumns();

        return {
            type : 'grid',
            id : 'emc-sampling-plan-sample-bottle-page-grid',
            service : 'EmcSampleBottleServiceImpl',
            columns : columns,
            filterOpen : true,
            page : true,
            requestData : {
                orgId_SEQ : workspace.user.ext$.parentorgid,
            },

            toolbar:[ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.addSampleBottle();
                },
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-sampling-plan-sample-bottle-page-grid').deleteRows(IFM_CONTEXT + '/secure/emc/module/bp/sample-bottles')
                },

            },{
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.BUTTON.BATCH_UPDATE_SAMPLE_BOTTLE',
                icon : 'edit',
                onClick : function() {
                    _this.batchUpdateSampleBottle();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('sampleBottle', Gikam.getComp('emc-sampling-plan-sample-bottle-page-grid').getSelections());
                }
            }
            ],

            url : '/secure/emc/module/bp/sample-bottles/queries',
        }
    },

    addSampleBottle : function() {
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.MODAL.BATCH_UPDATE_SAMPLE_BOTTLE',
            width : 400,
            height : 200,
            url : IFM_CONTEXT + '/secure/emc/module/bp/sample/sampling-plans/page/add-sample-bottle',
            onAfterClose : function(data) {
                var addnum = data.addnum;
                if(addnum == null || addnum == ''){
                    return;
                }
                workspace.window.showMask(true);
                var url = IFM_CONTEXT + '/secure/emc/module/bp/sample-bottles/add-sample-bottle';
                Gikam.post(url, Gikam.getJsonWrapper({
                    f : {
                        addnum: addnum
                    }
                }, [])).always(function()
                {
                    workspace.window.closeMask(true);
                    Gikam.getComp('emc-sampling-plan-sample-bottle-page-grid').refresh();
                });
            }

        });

    },

    batchUpdateSampleBottle : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-sampling-plan-sample-bottle-page-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var activeRow = selections[0];
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
                                    item.weighingUserName = formData.weighingUserName;
                                    item.emptyBottleWeight = formData.emptyBottleWeight;
                                    item.weightTime = formData.weightTime;
                                    item.projNo = formData.projNo;
                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(emcSamplingPlan.baseUrl + '/' + activeRow.id, Gikam.getJsonWrapper({}, [ 'EmcSampleBottleServiceImpl', selections ])).done(function() {
                                    Gikam.getComp('emc-sampling-plan-sample-bottle-page-grid').refresh();
                                }).always(function() {
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
                    }  ]
                }]
            }
        });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            columns: 1,
            titleWidth: 100,
            fields: emcSamplingPlan.getBatchUpdateSampleBottleFormFields(),
            autoSave: false
        });

    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getSampleBottlePageGridParam() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    },

}
