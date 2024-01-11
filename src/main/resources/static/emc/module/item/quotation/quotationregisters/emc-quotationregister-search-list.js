emcQuotationregister.searchPage = {

    getGridParam : function() {
        var _this = this;
        var requestData = _this.param;
        if(_this.param.type == 'coreperson'){
            requestData.corePersonCode_SEQ = workspace.user.id;
        }
        return {
            type : 'grid',
            id : 'emc-quotationregister-audit-list-grid',
            readonly : true,
            url : emcQuotationregister.baseUrl + '/queries',
            requestData : requestData,
            columns : emcQuotationregister.getAuditListColumns(),
            service : 'emcQuotationregisterServiceImpl',
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
            },
            onRowActive : function(index, row) {
                _this.onDetailActive(row);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-quotationinformation-test-audit-list-grid').cleanData();
                    Gikam.getComp('emc-quotationinformation-add-audit-list-grid').cleanData();
                    Gikam.getComp('emc-quotationinformation-total-audit-list-grid').cleanData();
                    Gikam.getComp('emc-quotationinformation-item-audit-list-grid').cleanData();
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.ITEM.QUOTATION.TIP.ADDCONTRACT',
                icon : 'add',
                onClick : function() {
                    _this.addContract();
                }
            }]
        }
    },

    addContract : function() {
        var _this = this;
        var grid = Gikam.getComp('emc-quotationregister-audit-list-grid');
        var selections = grid.getSelections();
        Gikam.postText(emcQuotationregister.baseUrl + '/addContract', Gikam.getJsonWrapper(null, [ 'emcQuotationregisterServiceImpl', selections ])).done(function(){
            Gikam.alert('添加成功');
        }).always(function() {
        });
    },

    onDetailActive : function(row) {
        var _this = this;
        if (row) {
            if (isNaN(row.id)) {
                return;
            }
            Gikam.getComp('emc-quotationregisters-uploader').setOptions({
                bizId : row.id
            });

            Gikam.getAsyncComp('emc-quotationregisters-audit-list-base-info-form').done(function(item) {
                item.refresh({
                    url : Gikam.printf(emcQuotationregister.baseUrl + '/{id}', {
                        id : row.id
                    })
                });
                item.setReadonly(true);
            });

            Gikam.getAsyncComp('emc-quotationinformation-test-audit-list-grid').done(function(item) {
                item.refresh({
                    url : emcQuotationregister.informationBaseUrl + '/queries',
                    requestData : {
                        quotationRegisterId_SEQ : row.id,
                        type_SEQ : 'test'
                    }
                });
            });

            Gikam.getAsyncComp('emc-quotationinformation-add-audit-list-grid').done(function(item) {
                item.refresh({
                    url : emcQuotationregister.informationBaseUrl + '/queries',
                    requestData : {
                        quotationRegisterId_SEQ : row.id,
                        type_SEQ : 'add'
                    }
                });
            });

            Gikam.getAsyncComp('emc-quotationinformation-total-audit-list-grid').done(function(item) {
                item.refresh({
                    url : emcQuotationregister.informationBaseUrl + '/queries',
                    requestData : {
                        quotationRegisterId_SEQ : row.id,
                        type_SEQ : 'total'
                    }
                });
            });

            Gikam.getAsyncComp('emc-quotationinformation-item-audit-list-grid').done(function(item) {
                item.refresh({
                    url : emcQuotationregister.informationBaseUrl + '/queries',
                    requestData : {
                        quotationRegisterId_SEQ : row.id,
                        type_SEQ : 'test',
                        opertype : 'item'

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
                    readonly :true,
                    id : 'emc-quotationregisters-audit-list-base-info-form',
                    panels  : emcQuotationregister.getBaseInfoFormFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('emc-quotationregister-audit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            }, {
                title : 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONREGISTERS.TAB.INFORMATION',
                items: [{
                    type: 'tab',
                    height: 300,
                    panels: [{
                        title: 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONREGISTERS.TAB.TESTINFORMATION',
                        items: [{
                            type: 'grid',
                            filter : false,
                            readonly : true,
                            id: 'emc-quotationinformation-test-audit-list-grid',
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
                            readonly : true,
                            id: 'emc-quotationinformation-add-audit-list-grid',
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
                            readonly : true,
                            id: 'emc-quotationinformation-total-audit-list-grid',
                            columns : emcQuotationregister.getInformationTotalEditListColumns(),
                        }]
                    }]
                }]
            }, {
                // 附件
                title : 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONREGISTERS.TAB.FILE',
                items : [ {
                    type : 'uploader',
                    readonly : true,
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
                        Gikam.getComp('emc-quotationinformation-audit-list-grid').refresh();
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
                        Gikam.getComp('emc-quotationinformation-audit-list-grid').refresh();
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
                        Gikam.getComp('emc-quotationinformation-audit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-quotationinformation-audit-list-grid').deleteRows(emcQuotationregister.baseUrl);
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
            id : 'emc-quotationregister-audit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '25%',
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

    init : function(param) {
        this.param = param;
        this.create();
    }
};
