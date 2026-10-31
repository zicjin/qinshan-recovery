emcQuotationregister.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-quotationregister-edit-list-grid',
            url : emcQuotationregister.baseUrl + '/queries/raw',
            columns : emcQuotationregister.getEditListColumns(),
            service : 'emcQuotationregisterServiceImpl',
            requestData : {
                corePersonCode_SEQ : workspace.user.id,
            },
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
            },
            onRowActive : function(index, row) {
                _this.onDetailActive(row);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                    _this.onDetailActive(data[0]);
                } else {
                    Gikam.getComp('emc-quotationinformation-test-edit-list-grid').cleanData();
                    Gikam.getComp('emc-quotationinformation-add-edit-list-grid').cleanData();
                    Gikam.getComp('emc-quotationinformation-total-edit-list-grid').cleanData();
                }
            },

            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONREGISTERS.MODAL.ADD',
                        fields : emcQuotationregister.getBaseInfoFormFields(),
                        url : emcQuotationregister.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-quotationregister-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-quotationregister-edit-list-grid').deleteRows(emcQuotationregister.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.QUOTATION_PRINT',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('printquotation', Gikam.getComp('emc-quotationregister-edit-list-grid').getSelections());
                }
            } ]
        }
    },

    submit : function() {
        var grid = Gikam.getComp('emc-quotationregister-edit-list-grid');
        var selection = grid.getSelections();
        var isReturn = false;
        selection.forEach(function(item) {
            if(Gikam.isEmpty(item.quotationName) || Gikam.isEmpty(item.bizCategoryName) || Gikam.isEmpty(item.corePersonName)){
                isReturn = true;
            }
        });
        if(isReturn){
            Gikam.alert('EMC.MODULE.ITEM.QUOTATION.TIP.NOT_COMPLETE');
            return;
        }
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : emcQuotationregister
        }).done(function() {
            grid.refresh();
        });
    },

    onDetailActive : function(row) {
        var _this = this;
        if (row) {
            if (isNaN(row.id)) {
                return;
            }
            try {
                Gikam.getAsyncComp('emc-quotationregisters-uploader').done(function (uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    })
                });
            }catch (e) {

            }

            Gikam.getAsyncComp('emc-quotationregisters-edit-list-base-info-form').done(function(item) {
                item.refresh({
                    url : Gikam.printf(emcQuotationregister.baseUrl + '/{id}', {
                        id : row.id
                    })
                });
            });

            Gikam.getAsyncComp('emc-quotationinformation-test-edit-list-grid').done(function(item) {
                item.refresh({
                    url : emcQuotationregister.informationBaseUrl + '/queries',
                    requestData : {
                        quotationRegisterId_SEQ : row.id,
                        type_SEQ : 'test'
                    }
                });
            });

            Gikam.getAsyncComp('emc-quotationinformation-add-edit-list-grid').done(function(item) {
                item.refresh({
                    url : emcQuotationregister.informationBaseUrl + '/queries',
                    requestData : {
                        quotationRegisterId_SEQ : row.id,
                        type_SEQ : 'add'
                    }
                });
            });

            Gikam.getAsyncComp('emc-quotationinformation-total-edit-list-grid').done(function(item) {
                item.refresh({
                    url : emcQuotationregister.informationBaseUrl + '/queries',
                    requestData : {
                        quotationRegisterId_SEQ : row.id,
                        type_SEQ : 'total'
                    }
                });
            });

        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONREGISTERS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    service : 'emcQuotationregisterServiceImpl',
                    id : 'emc-quotationregisters-edit-list-base-info-form',
                    panels  : emcQuotationregister.getBaseInfoFormFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('emc-quotationregister-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            }, {
                title : 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONREGISTERS.TAB.INFORMATION',
                items: [{
                    type: 'btnToolbar', items: [{
                        type : 'button',
                        text : 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONINFORMATION.BUTTON.TEST',
                        icon : 'add',
                        onClick : function() {
                            var data = Gikam.getComp('emc-quotationregister-edit-list-grid').getActivedRow();
                            if (Gikam.isEmpty(data)) {
                                Gikam.alert('EMC.MODULE.MDM.BIZMDM.STANDARDSETS.TIP.CHOOSE_ONE');
                                return;
                            }
                            Gikam.create('modal', {
                                width : 800,
                                height : 500,


                                title : Gikam.choose.getConfig('detectionfactors').title,
                                url : Gikam.choose.getConfig('detectionfactors').url + Gikam.param({
                                }),
                                onAfterClose : function(rows) {
                                    if (Gikam.isEmpty(rows)) {
                                        return;
                                    }
                                    var url = Gikam.printf(emcQuotationregister.informationBaseUrl);
                                    workspace.window.showMask(true);
                                    Gikam.post(url, Gikam.getJsonWrapper({
                                        f : {
                                            quotationRegisterId : Gikam.getComp('emc-quotationregister-edit-list-grid').getActivedRow().id,
                                            type : 'test'
                                        }
                                    }, [ 'detectionFactorServiceImpl', rows ])).done(function() {
                                        Gikam.getComp('emc-quotationinformation-test-edit-list-grid').refresh({
                                            url : emcQuotationregister.informationBaseUrl + '/queries',
                                            requestData : {
                                                quotationRegisterId_SEQ : Gikam.getComp('emc-quotationregister-edit-list-grid').getActivedRow().id,
                                                type_SEQ : 'test'
                                            }
                                        });
                                    }).always(function() {
                                        workspace.window.closeMask();
                                    });
                                }
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONINFORMATION.BUTTON.ADD',
                        icon : 'add',
                        onClick : function() {
                            var data = Gikam.getComp('emc-quotationregister-edit-list-grid').getActivedRow();
                            if (Gikam.isEmpty(data)) {
                                Gikam.alert('EMC.MODULE.MDM.BIZMDM.STANDARDSETS.TIP.CHOOSE_ONE');
                                return;
                            }
                            Gikam.create('modal', {
                                width : 400,
                                height : 500,
                                title : Gikam.choose.getConfig('detectionfactor').title,
                                url : Gikam.choose.getConfig('detectionfactor').url + Gikam.param({
                                }),
                                onAfterClose : function(rows) {
                                    if (Gikam.isEmpty(rows)) {
                                        return;
                                    }
                                    var url = Gikam.printf(emcQuotationregister.informationBaseUrl);
                                    workspace.window.showMask(true);
                                    Gikam.post(url, Gikam.getJsonWrapper({
                                        f : {
                                            quotationRegisterId : Gikam.getComp('emc-quotationregister-edit-list-grid').getActivedRow().id,
                                            type : 'add'
                                        }
                                    }, [ 'addPriceServiceImpl', rows ])).done(function() {
                                        Gikam.getComp('emc-quotationinformation-test-edit-list-grid').refresh({
                                            url : emcQuotationregister.informationBaseUrl + '/queries',
                                            requestData : {
                                                quotationRegisterId_SEQ : Gikam.getComp('emc-quotationregister-edit-list-grid').getActivedRow().id,
                                                type_SEQ : 'test'
                                            }
                                        });

                                        Gikam.getComp('emc-quotationinformation-add-edit-list-grid').refresh({
                                            url : emcQuotationregister.informationBaseUrl + '/queries',
                                            requestData : {
                                                quotationRegisterId_SEQ : Gikam.getComp('emc-quotationregister-edit-list-grid').getActivedRow().id,
                                                type_SEQ : 'add'
                                            }
                                        });
                                    }).always(function() {
                                        workspace.window.closeMask();
                                    });
                                }
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONINFORMATION.BUTTON.DISCOUNT',
                        icon : 'add',
                        onClick : function() {
                            var data = Gikam.getComp('emc-quotationinformation-test-edit-list-grid').getActivedRow();
                            if (Gikam.isEmpty(data)) {
                                Gikam.alert('EMC.MODULE.MDM.BIZMDM.STANDARDSETS.TIP.CHOOSE_ONE');
                                return;
                            }
                            Gikam.create('modal', {
                                width : 400,
                                height : 300,
                                title : Gikam.choose.getConfig('discounts').title,
                                url : Gikam.choose.getConfig('discounts').url + Gikam.param({
                                    single : true
                                }),
                                onAfterClose : function(rows) {
                                    if (Gikam.isEmpty(rows)) {
                                        return;
                                    }
                                    var url = Gikam.printf(emcQuotationregister.baseUrl + '/addDiscount');
                                    workspace.window.showMask(true);
                                    Gikam.post(url, Gikam.getJsonWrapper({
                                        f : {
                                            testDiscount : rows[0].discount
                                        }
                                    }, [ 'emcQuotationInformationServiceImpl', Gikam.getComp('emc-quotationinformation-test-edit-list-grid').getSelections() ])).done(function() {
                                        Gikam.getComp('emc-quotationinformation-test-edit-list-grid').refresh({
                                            url : emcQuotationregister.informationBaseUrl + '/queries',
                                            requestData : {
                                                quotationRegisterId_SEQ : Gikam.getComp('emc-quotationregister-edit-list-grid').getActivedRow().id,
                                                type_SEQ : 'test'
                                            }
                                        });
                                    }).always(function() {
                                        workspace.window.closeMask();
                                        Gikam.getComp('emc-quotationinformation-test-edit-list-grid').refresh({
                                            url : emcQuotationregister.informationBaseUrl + '/queries',
                                            requestData : {
                                                quotationRegisterId_SEQ : Gikam.getComp('emc-quotationregister-edit-list-grid').getActivedRow().id,
                                                type_SEQ : 'test'
                                            }
                                        });
                                    });
                                }
                            });
                        }
                    },{
                        type : 'button',
                        text : 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONINFORMATION.BUTTON.BATCHINPUT',
                        icon : 'add',
                        onClick : function() {
                            var selections = Gikam.getComp('emc-quotationinformation-test-edit-list-grid').getSelections();
                            if (selections.length > 0) {
                                Gikam.create('modal', {
                                    width : 400,
                                    height : 310,
                                    title : 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.BATCH_UPDATE_ORDER',
                                    url : emcQuotationregister.baseUrl  + '/page/batch-update-information',
                                    onAfterClose : function(data) {
                                        if (!Gikam.isEmpty(data)) {
                                            workspace.window.showMask(true);
                                            selections.forEach(function(item) {
                                                if(String(item.testName).indexOf("报告编制费") == '-1' && String(item.testName).indexOf("检测费用合计") == '-1'){
                                                    item.testMonitorNum = data.testMonitorNum;
                                                    item.testDayNum = data.testDayNum;
                                                    item.testFrequency = data.testFrequency;
                                                }
                                            });

                                            workspace.window.showMask(true);
                                            Gikam.put(emcQuotationregister.informationBaseUrl + '/instant', Gikam.getJsonWrapper(null, [ 'emcQuotationinformationServiceImpl', selections ])).done(function() {
                                                Gikam.getComp('emc-quotationinformation-test-edit-list-grid').refresh();
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
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONINFORMATION.BUTTON.CALCULATION',
                        icon : 'modify',
                        onClick : function() {
                            var url = Gikam.printf(emcQuotationregister.baseUrl + '/calculation');
                            workspace.window.showMask(true);
                            Gikam.postText(url, Gikam.getJsonWrapper(null, [ 'emcQuotationregisterServiceImpl', Gikam.getComp('emc-quotationregister-edit-list-grid').getSelections() ])).always(function() {
                                workspace.window.closeMask();
                                Gikam.getComp('emc-quotationinformation-test-edit-list-grid').refresh({
                                    url : emcQuotationregister.informationBaseUrl + '/queries',
                                    requestData : {
                                        quotationRegisterId_SEQ : Gikam.getComp('emc-quotationregister-edit-list-grid').getActivedRow().id,
                                        type_SEQ : 'test'
                                    }
                                });

                                Gikam.getComp('emc-quotationinformation-add-edit-list-grid').refresh({
                                    url : emcQuotationregister.informationBaseUrl + '/queries',
                                    requestData : {
                                        quotationRegisterId_SEQ : Gikam.getComp('emc-quotationregister-edit-list-grid').getActivedRow().id,
                                        type_SEQ : 'add'
                                    }
                                });

                                Gikam.getComp('emc-quotationinformation-total-edit-list-grid').refresh({
                                    url : emcQuotationregister.informationBaseUrl + '/queries',
                                    requestData : {
                                        quotationRegisterId_SEQ : Gikam.getComp('emc-quotationregister-edit-list-grid').getActivedRow().id,
                                        type_SEQ : 'total'
                                    }
                                });
                            });

                        }
                    }, {
                        type : 'dropDownMenu',
                        text : 'GIKAM.BUTTON.DELETE',
                        items : [ {
                            type : 'button',
                            text : 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONINFORMATION.DELETE.BUTTON.TEST',
                            icon : 'remove-row',
                            onClick : function() {
                                var selection = Gikam.getComp('emc-quotationinformation-test-edit-list-grid').getSelections();
                                var isReturn = false;
                                selection.forEach(function(item) {
                                    if (item.testName == '检测费用合计') {
                                        isReturn = true;
                                    }
                                });
                                if(isReturn){
                                    Gikam.alert('无法删除检测费用合计');
                                    return;
                                }
                                Gikam.getComp('emc-quotationinformation-test-edit-list-grid').deleteRows(emcQuotationregister.informationBaseUrl);
                            }
                        },{
                            type : 'button',
                            text : 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONINFORMATION.DELETE.BUTTON.ADD',
                            icon : 'remove-row',
                            onClick : function() {
                                Gikam.getComp('emc-quotationinformation-add-edit-list-grid').deleteRows(emcQuotationregister.informationBaseUrl);
                            }
                        }]
                    } ]
                },{
                    type: 'tab',
                    height: 300,
                    panels: [{
                        title: 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONREGISTERS.TAB.TESTINFORMATION',
                        items: [{
                            type: 'grid',
                            filter : false,
                            id: 'emc-quotationinformation-test-edit-list-grid',
                            service : 'emcQuotationinformationServiceImpl',
                            columns: emcQuotationregister.getInformationTestEditListColumns(),
                        }]
                    }]
                }, {
                    type: 'tab',
                    height: 220,
                    panels: [{
                        title: 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONREGISTERS.TAB.ADDINFORMATION',
                        items: [{
                            type: 'grid',
                            filter : false,
                            fill : true,
                            id: 'emc-quotationinformation-add-edit-list-grid',
                            service : 'emcQuotationinformationServiceImpl',
                            columns : emcQuotationregister.getInformationAddEditListColumns(),
                        }]
                    }]
                }, {
                    type: 'tab',
                    height: 150,
                    panels: [{
                        title: 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONREGISTERS.TAB.TOTALINFORMATION',
                        items: [{
                            type: 'grid',
                            filter : false,
                            service : 'emcQuotationinformationServiceImpl',
                            id: 'emc-quotationinformation-total-edit-list-grid',
                            columns : emcQuotationregister.getInformationTotalEditListColumns(),
                        }]
                    }]
                }]
            }, {
                // 附件
                title : 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONREGISTERS.TAB.FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-quotationregisters-uploader',
                    dbTable : 'T_EMC_QUOTATION_REGISTER_FILE',
                } ]
            } ]
        }
    },
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONINFORMATION.BUTTON.TEST',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONINFORMATION.MODAL.ADD',
                        // fields : emcQuotationregister.getBaseInfoFormFields(),
                        url : emcQuotationregister.informationBaseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-quotationinformation-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONINFORMATION.BUTTON.ADD',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONINFORMATION.MODAL.ADD',
                        // fields : emcQuotationregister.getBaseInfoFormFields(),
                        url : emcQuotationregister.informationBaseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-quotationinformation-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONINFORMATION.BUTTON.DISCOUNT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONINFORMATION.MODAL.ADD',
                        // fields : emcQuotationregister.getBaseInfoFormFields(),
                        url : emcQuotationregister.informationBaseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-quotationinformation-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-quotationinformation-edit-list-grid').deleteRows(emcQuotationregister.baseUrl);
                }
            } ]
        }
    },
    loadDetailPage : function(id) {
        workspace.window.load(emcQuotationregister.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-quotationregister-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '43%',
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    center : {
                        items : [ _this.getBaseInfoTab() ]
                    },

                } ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
