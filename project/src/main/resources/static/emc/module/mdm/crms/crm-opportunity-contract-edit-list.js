crmOpportunityContract.searchPage = {

    getContractGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-contract-search-list-grid',
            url : crmOpportunityContract.baseUrl + '/queries',
            columns : crmOpportunityContract.getEditListColumns(),
            service : 'crmOpportunityContractServiceImpl',
            readonly : true,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-contract-search-list-monitor-item-grid', 'emc-contract-search-list-review-content-grid' ])
                    Gikam.getComp('emc-contract-search-list-base-info-form').cleanData();
                    Gikam.getComp('emc-contract-search-list-review-basic-info-form').cleanData();
                }
            },
            onRowActive : function(index, row) {
                _this.onContractRowActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.MDM.CRMS.BUTTON.CREATE_PROJECT',
                icon : 'add',
                onClick : function() {
                    _this.createProject();
                }
            } ]
        }
    },


    /** 选中合同 */
    onContractRowActive : function(row) {
        Gikam.getAsyncComp('crm-opportunity-base-info-form').done(function(form){
            form.refresh({
                url : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/mdm/crm/opportunities/{id}', {
                    id : row.opportunityId
                })
            });
        });

        Gikam.getAsyncComp('crm-opportunity-expense-grid').done(function(grid){
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/mdm/crm/expenses/queries',
                requestData : {
                    opportunityId_SEQ : row.opportunityId
                }
            });
        });

        Gikam.getAsyncComp('crm-opportunity-expense-grid').done(function(grid){
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/mdm/crm/expenses/queries',
                requestData : {
                    opportunityId_SEQ : row.opportunityId
                }
            });
        });

        Gikam.getAsyncComp('crm-opportunity-contract-base-info-form').done(function(form){
            form.refresh({
                url : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/mdm/crm/contracts/{id}', {
                    id : row.id
                })
            });
        });

        Gikam.getAsyncComp('crm-opportunity-commission-sheet-grid').done(function(grid){
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/mdm/crm/orders/queries',
                requestData : {
                    opportunityNo_SEQ : row.ext$.opportunityno
                }
            });
        });

        Gikam.getComp('crm-opportunity-contract-uploader').setOptions({
            bizId : row.id,
        });
    },

    createProject : function() {
        var selections = Gikam.getComp('emc-contract-search-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.CRMS.TITLE.DETECT_ORDER',
            width : '70%',
            height : '80%',
            url : crmOpportunityContract.baseUrl + '/page/order-choose-list' + Gikam.param({
                opportunityNo_SEQ : selections[0].ext$.opportunityno,
            }),
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                workspace.window.showMask(true);
                Gikam.postText(crmOpportunityContract.baseUrl + '/action/create-project', Gikam.getJsonWrapper(null, [ '', rows ])).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    createProject0 : function() {
        var selections = Gikam.getComp('emc-contract-search-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        workspace.window.showMask(true);
        Gikam.postText(crmOpportunityContract.baseUrl + '/action/create-project', Gikam.getJsonWrapper(null, [ '', selections ])).always(function() {
            workspace.window.closeMask();
        });
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            id : 'emc-contract-search-list-base-info-tab',
            panels : [ {
                title : 'EMC.MODULE.MDM.CRMS.TITLE.OPPORTUNITY_INFO',
                id : 'crm-opportunity-base-info-panel',
                items : [ {
                    type : 'form',
                    id : 'crm-opportunity-base-info-form',
                    columns : 3,
                    service : '',
                    readonly : true,
                    fields: crmOpportunityContract.getOpportunityFormFields()
                } ]
            }, {
                title : 'EMC.MODULE.MDM.CRMS.TITLE.EXPENSES_DETAIL',
                id : 'crm-opportunity-expense-panel',
                items : [ {
                    type : 'grid',
                    id : 'crm-opportunity-expense-grid',
                    columns : crmOpportunityContract.getContractExpensesColumns(),
                    readonly : true,
                } ]
            }, {
                title : 'EMC.MODULE.MDM.CRMS.TITLE.COMMISSION_SHEET',
                id : 'crm-opportunity-commission-sheet-panel',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ {
                            type : 'grid',
                            id : 'crm-opportunity-commission-sheet-grid',
                            columns : crmOpportunityContract.getCommissionSheetColumns(),
                            readonly : true,
                            onLoadSuccess : function(data) {
                                if (!Gikam.isEmpty(data)) {
                                    this.activeRowByIndex(0);
                                } else {
                                    Gikam.cleanCompData([ 'crm-opportunity-order-task-grid' ]);
                                }
                            },
                            onRowActive : function(index, row) {
                                Gikam.getComp('crm-opportunity-order-task-grid').refresh({
                                    url : IFM_CONTEXT + '/secure/emc/module/mdm/crm/ordtasks/queries',
                                    requestData : {
                                        ordNo_SEQ : row.ordNo
                                    }
                                })
                            },
                        }]
                    },
                    center : {
                        items : [ {
                            type : 'grid',
                            id : 'crm-opportunity-order-task-grid',
                            columns : crmOpportunityContract.getOrderTaskColumns(),
                            readonly : true,
                        }]
                    }
                } ]
            }, {
                title : 'EMC.MODULE.MDM.CRMS.TITLE.CONTRACT_INFO',
                id : 'crm-opportunity-contract-base-info-panel',
                items : [ {
                    type : 'form',
                    id : 'crm-opportunity-contract-base-info-form',
                    columns : 3,
                    service : '',
                    readonly : true,
                    fields: crmOpportunityContract.getContractFormFields()
                } ]
            }, {
                title : 'EMC.MODULE.MDM.CRMS.TITLE.PAYMENT_INFO',
                id : 'crm-opportunity-payment-panel',
                items : [ {
                    type : 'grid',
                    id : 'crm-opportunity-payment-grid',
                    columns : crmOpportunityContract.getContractPaymentColumns(),
                    readonly : true,
                } ]
            }, {
                title : 'EMC.MODULE.MDM.CRMS.TITLE.FILE',
                items : [ {
                    type : 'uploader',
                    id : 'crm-opportunity-contract-uploader',
                    dbTable : 'T_EMC_CONTRACT',
                    readonly : true,
                } ]
            } ]
        }
    },

    getReviewBasicInfoForm : function() {
        return {
            type : 'form',
            id : 'emc-contract-search-list-review-basic-info-form',
            columns : 2,
            titleWidth : 80,
            service : 'emcReviewBasicInfoServiceImpl',
            readonly : true,
            fields: crmOpportunityContract.getReviewBasicInfoFields()
        }
    },

    getReviewContentGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-contract-search-list-review-content-grid',
            columns : crmOpportunityContract.getReviewContentColumns(),
            service : 'emcReviewContentServiceImpl',
            readonly : true,
            group : {
                fields : [ 'contentType' ],
                groupCheckbox : false,
                formatter : function(row) {
                    return row.contentType;
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    _this.viewContractReviewReport();
                }
            } ]
        }
    },

    viewContractReviewReport : function () {
        var contract = Gikam.getComp('emc-contract-search-list-grid').getActivedRow();
        if (Gikam.isEmpty(contract)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var reviewReportLineId = contract.reviewReportLineId;
        if (Gikam.isEmpty(reviewReportLineId)) {
            Gikam.alert('EMC.MODULE.MDM.CRMS.TIP.HAS_NO_REVIEW_REPORT_TEMPLATE');
            return;
        }
        Gikam.viewReport(reviewReportLineId, contract);
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-contract-search-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '23%',
                items : [ _this.getContractGridParam() ]
            },
            center : {
                items : [ _this.getBaseInfoTab() ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
