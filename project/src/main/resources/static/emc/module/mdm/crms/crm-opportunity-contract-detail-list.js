crmOpportunityContract.detailPage = {

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
                    columns : 4,
                    service : 'crmOpportunityMainServiceImpl',
                    readonly : false,
                    url: Gikam.printf(IFM_CONTEXT + '/secure/emc/module/mdm/crm/opportunities/{id}', {
                        id: _this.param.opportunityId
                    }),
                    fields: crmOpportunityContract.getOpportunityMainFormFields()
                } ]
            }, {
                title : 'EMC.MODULE.MDM.CRMS.TITLE.EXPENSES_DETAIL',
                id : 'crm-opportunity-expense-panel',
                items : [ _this.getTestItemLayoutConfig()]
            }, {
                title : 'EMC.MODULE.MDM.CRMS.TITLE.CONTRACT_INFO',
                id : 'crm-opportunity-commission-sheet-panel',
                items : [ {
                    type: 'form',
                    id: 'crm-opportunity-contract-grid',
                    url: Gikam.printf(crmOpportunityContract.baseUrl + '/{id}', {
                        id: _this.param.id
                    }),
                    columns : 4,
                    readonly : true,
                    service : 'CrmOpportunityContractServiceImpl',
                    fields: crmOpportunityContract.getContractFormFields(),
                } ]
            }, {
                title : 'EMC.MODULE.MDM.CRMS.TITLE.PAYMENT_INFO',
                id : 'crm-opportunity-contract-base-info-panel',
                items : [ {
                    type: 'grid',
                    id: 'crm-contract-payment-grid',
                    url: IFM_CONTEXT + '/secure/emc/module/mdm/crm/payments/queries',
                    requestData: {
                        contractId_EQ: this.param.id,
                    },
                    columns: crmOpportunityContract.getOpportunityContractPaymentColumns(),
                    service: 'CrmOpportunityPaymentServiceImpl',
                    readonly: true,
                } ]
            }, {
                title : 'EMC.MODULE.MDM.CRMS.TITLE.FILE',
                items : [ _this.getUploadLayoutConfig()]
            } ]
        }
    },

    getUploadLayoutConfig: function () {
        var _this = this;
        return {
            type: 'layout',
            north: {
                height: '50%',
                title : 'EMC.MODULE.MDM.CRMS.TITLE.OPPORTUNITY_FILE',
                items: [_this.getOpportunityUploaderConfig()]
            },
            center: {
                title : 'EMC.MODULE.MDM.CRMS.TITLE.CONTRACT_FILE',
                items: [_this.getContractUploaderConfig()]
            }
        };
    },

    // 附件组件
    getOpportunityUploaderConfig: function () {
        var _this = this;
        return {
            type: 'uploader',
            id: 'crm-opportunity-main-uploader',
            dbTable: 'T_CRM_OPPORTUNITY_MAIN',
            multiple: true,
            autoClose: true,
            readonly: true,
            bizId: _this.param.opportunityId,
        }
    },

    getContractUploaderConfig: function () {
        var _this = this;
        return {
            type: 'uploader',
            id: 'crm-opportunity-contract-uploader',
            bizId: _this.param.id,
            dbTable: 'T_CRM_OPPORTUNITY_CONTRACT',
            multiple: true,
            autoClose: true,
            readonly: true
        }
    },

    // 检验项目标签组件
    getTestItemLayoutConfig: function () {
        var _this = this;
        return {
            type: 'layout',
            id: 'expense-details-layoutId',
            west: {
                // 样品和项目列表
                width: '50%',
                items: [_this.getOrderAndTaskGridConfig()]
            },
            center: {
                // 杂费
                items: [_this.getExpenseGridConfig()]
            }
        };
    },

    // 样品和项目标签组件
    getOrderAndTaskGridConfig: function () {
        var _this = this;
        return {
            type: 'layout',
            north: {
                // 样品列表
                height: '50%',
                items: [_this.getOrderGridConfig()]
            },
            center: {
                // 检验项目列表
                items: [_this.getOrderTaskGridConfig()]
            }
        }
    },

    getExpenseGridConfig: function () {
        var _this = this;
        return {
            type: 'layout',
            north: {
                // 杂费
                height: '50%',
                items: [{
                    type: 'tab',
                    style: {
                        padding: 0
                    },
                    panels: [{
                        title: 'EMC.MODULE.MDM.CRMS.TITLE.SAMPLING_REPORT_EXPENSES',
                        items: [this.getOpportunityExpenses()]
                    }]
                }]
            },
            center: {
                // 检测费用明细
                items: [_this.getExpenseFormConfig()]
            }
        }
    },

    // 样品列表
    getOrderGridConfig: function () {
        var activedRow = Gikam.getComp('emc-contract-search-list-grid').getActivedRow();
        var _this = this;
        return {
            type: 'grid',
            id: 'crm-opportunity-contract-detail-order-grid',
            service: 'crmOpportunityOrdersServiceImpl',
            filterOpen: false,
            filter: true,
            page: false,
            readonly: true,
            url : IFM_CONTEXT + '/secure/emc/module/mdm/crm/orders/queries',
            requestData: {
                opportunityNo_SEQ: this.param.opportunityNo,
                CRMMAINID_SEQ : activedRow.opportunityId
                
            },
            order : {
                'testType' : 'asc',
                'id' : 'desc'
            },
            group : {
                fields : [ "testType"],
                formatter : function(row) {
                    return  "检品类型 ：" + row.testType
                }
            },
            columns: crmOpportunityContract.getHjOrderGridColumns(),
            onRowActive: function (index, row) {
                Gikam.getComp('crm-opportunity-contract-detail-order-task-grid').refresh({
                    url : IFM_CONTEXT + '/secure/emc/module/mdm/crm/ordtasks/queries',
                    requestData: {
                        ordersId_SEQ: row.id
                    }
                })
            },
            onLoadSuccess: function (data) {
               if (Gikam.isEmpty(data)) {
                   Gikam.cleanCompData(['crm-opportunity-contract-detail-order-task-grid']);
               } else {
                   this.activeRowByIndex(1);
               }
            },
        }
    },

    // 检验项目列表
    getOrderTaskGridConfig: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'crm-opportunity-contract-detail-order-task-grid',
            service: 'crmOpportunityOrdtaskServiceImpl',
            columns: crmOpportunityContract.getContractHjOrderTaskGridColumns(),
            filterOpen: false,
            filter: false,
            page: false,
            order : {
                groupfields : 'desc',
            },
            readonly: true,
            group : {
                fields : [ "ext$.groupfields"],
                formatter : function(row) {
                    if(row.schemeName === 'default'){
                        return  "项目 ：" + row.testNo
                    }else{
                        return  "项目 ：" + row.schemeName
                    }
                }
            },
        }
    },

    // 杂费
    getOpportunityExpenses: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'crm-opportunity-other-expenses-grid',
            columns: crmOpportunityContract.getHjExpensesGridColumns(),
            service: 'crmOpportunityExpensesServiceImpl',
            readonly : true,
            autoSave: false,
            url : IFM_CONTEXT + '/secure/emc/module/mdm/crm/expenses/queries',
            requestData : {
                opportunityId_SEQ : _this.param.opportunityId
            }
        }
    },

    // 检测费用明细
    getExpenseFormConfig: function () {
        var _this = this;
        return {
            type: 'form',
            id: 'crm-opportunity-main-test-expenses-form',
            readonly: true,
            columns: 3,
            fields: crmOpportunityContract.getOpportunityExpenseFormFields(),
            service: 'crmOpportunityMainServiceImpl',
            url : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/mdm/crm/opportunities/{id}', {
                id : _this.param.opportunityId
            }),
            onLoadSuccess : function(data) {
                this.flag = false;
                this.setData({
                    testingFee : data.testingFee.toFixed(2),
                    guideFee : data.guideFee.toFixed(2),
                    incidentalFee : data.incidentalFee.toFixed(2),
                    reportFeeRate : data.reportFeeRate.toFixed(2),
                    reportFee : data.reportFee.toFixed(2),
                    acceptReportFee : data.acceptReportFee.toFixed(2),
                    urgentFee : data.urgentFee.toFixed(2),
                    otherExpenses : data.otherExpenses.toFixed(2),
                    amountFee : data.amountFee.toFixed(2),
                    discountAmount : data.discountAmount.toFixed(2),
                    discountRate : data.discountRate.toFixed(2),
                    taxRate : data.taxRate.toFixed(2),
                    taxAmount : data.taxAmount.toFixed(2),
                    dealAmount : data.dealAmount.toFixed(2),
                });

                var form = Gikam.getComp('crm-opportunity-contract-grid');
                form.setData({
                    id : form.getData.id,
                    contractAmount : data.dealAmount
                });
                var paymentGrid = Gikam.getComp('crm-contract-payment-grid');
                var rows = paymentGrid.getData();
                for(var i = 0; i<rows.length;i++){
                    if(!Gikam.isEmpty(rows[i].planProportion)){
                        paymentGrid.setData({
                            id : rows[i].id,
                            planAmount : rows[i].planProportion * data.dealAmount
                        });
                    }
                }

            },
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-opportunity-contract-detail-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ _this.getBaseInfoTab() ]
            },
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
