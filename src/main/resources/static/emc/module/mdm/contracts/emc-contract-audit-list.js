emcContract.auditPage = {
    getContractGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-contract-audit-list-grid',
            url : emcContract.baseUrl + '/queries/tasks',
            columns : emcContract.getAuditListColumns(),
            service : 'emcContractServiceImpl',
            requestData : {
               orgId_SB: workspace.user.ext$.parentorgid,
               bpmn_statusCode : _this.param.bpmn_statusCode
            },
            deleteFormatter : function(row) {
                return row.contractName || row.contractNo || row.id;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-contract-audit-list-monitor-item-grid', 'emc-contract-audit-list-review-content-grid' ])
                    Gikam.getComp('emc-contract-audit-list-base-info-form').cleanData();
                    Gikam.getComp('emc-contract-audit-list-review-basic-info-form').cleanData();
                    if(Gikam.getComp('emc-contract-audit-list-uploader')) {
                        Gikam.getComp('emc-contract-audit-list-uploader').grid.cleanData();
                    }
                }
            },
            onRowActive : function(index, row) {
                _this.onContractRowActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                icon : 'pass',
                onClick : function() {
                    _this.pass();
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                icon : 'reject',
                onClick : function() {
                    _this.reject();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    _this.viewContract();
                }
            } ]
        }
    },
    
    /** 查看合同报表 */
    viewContract : function() {
        var selections = Gikam.getComp('emc-contract-audit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var reportConfigLineId = selections[0].ext$.reportconfiglineid;
        if (Gikam.isEmpty(reportConfigLineId)) {
            Gikam.alert('EMC.MODULE.MDM.CONTRACTS.TIP.HAS_NO_REPORT_TEMPLATE');
            return;
        }
        Gikam.viewReport(reportConfigLineId, selections[0]);
    },
    
    /** 选中合同 */
    onContractRowActive : function(row) {
        Gikam.getAsyncComp('emc-contract-audit-list-base-info-form').done(function(form){
            form.refresh({
                url : Gikam.printf(emcContract.baseUrl + '/{id}', {
                    id : row.id
                })
            });
        });

        Gikam.refreshGrids({
            'emc-contract-audit-list-monitor-item-grid' : emcContract.baseUrl + '/{id}/monitor-items/queries',
            'emc-contract-audit-list-review-content-grid' : emcContract.baseUrl + '/{id}/review-contents'
        }, {
            id : row.id
        });

        Gikam.getAsyncComp('emc-contract-audit-list-review-basic-info-form').done(function(form){
            form.refresh({
                url : Gikam.printf(emcContract.baseUrl + '/{id}/review-basic-info', {
                    id : row.id
                })
            });
        }).done(
            Gikam.getComp('emc-contract-audit-list-review-basic-info-form').setReadonly(this.param.bpmn_statusCode === 'quality' ? false : true)
        );

        Gikam.getComp('emc-contract-audit-list-uploader').setOptions({
            bizId : row.id,
        });
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            id : 'emc-contract-audit-list-base-info-tab',
            panels : [ {
                title : 'EMC.MODULE.MDM.CONTRACTS.TITLE.BASEINFO',
                id : 'emc-contract-audit-list-base-info-panel',
                items : [ {
                    type : 'form',
                    id : 'emc-contract-audit-list-base-info-form',
                    columns : 3,
                    service : '',
                    readonly : true,
                    panels: emcContract.getGroupBaseInfoFormFields()
                } ]
            }, {
                title : 'EMC.MODULE.MDM.CONTRACTS.TITLE.MONITOR_ITEM',
                id : 'emc-contract-audit-list-monitor-item-panel',
                items : [ {
                    type : 'grid',
                    id : 'emc-contract-audit-list-monitor-item-grid',
                    columns : emcContract.getMonitorItemColumns(),
                    service : 'emcMonitorItemServiceImpl',
                    readonly : true,
                } ]
            }, {
                title : 'EMC.MODULE.MDM.CONTRACTS.TITLE.REVIEW_CONTENT',
                id : 'emc-contract-audit-list-review-content-panel',
                items : [ {
                    type : 'layout',
                    north : {
                        height : 100,
                        items : [ _this.getReviewBasicInfoForm() ]
                    },
                    center : {
                        items : [ _this.getReviewContentGrid() ]
                    }
                } ]

            }, {
                title : 'EMC.MODULE.MDM.CONTRACTS.TITLE.FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-contract-audit-list-uploader',
                    dbTable : 'T_EMC_CONTRACT',
                    readonly : true,
                } ]
            } ]
        }
    },
    
    getReviewBasicInfoForm : function() {
        return {
            type : 'form',
            id : 'emc-contract-audit-list-review-basic-info-form',
            columns : 2,
            titleWidth : 80,
            service : 'emcReviewBasicInfoServiceImpl',
            fields: emcContract.getReviewBasicInfoFields()
        }
    },

    getReviewContentGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-contract-audit-list-review-content-grid',
            columns : emcContract.getReviewContentColumns(),
            service : 'emcReviewContentServiceImpl',
            group : {
                fields : [ 'contentType' ],
                groupCheckbox : false,
                formatter : function(row) {
                    return row.contentType;
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.BATCH_INPUT',
                icon : 'add',
                onClick : function() {
                    _this.batchInput();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    _this.viewContractReviewReport();
                }
            } ]
        }
    },
    
    batchInput : function () {
        var _this = this;
        var selections = Gikam.getComp('emc-contract-audit-list-review-content-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
            width : 400,
            height : 220,
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
                                    item.satisfied = formData.satisfied;
                                    item.explain = formData.explain;
                                });
                                workspace.window.showMask(true);
                                Gikam.put(IFM_CONTEXT + '/secure/emc/module/mdm/contracts/instant', Gikam.getJsonWrapper({}, [ 'emcReviewContentServiceImpl', selections ])).done(function () {
                                    Gikam.getComp('emc-contract-audit-list-review-content-grid').refresh();
                                }).always(function () {
                                    workspace.window.closeMask();
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
            titleWidth: 85,
            fields: emcContract.getReviewContentBatchInputColumns(),
            autoSave: false
        });
    },

    /** 查看合同评审记录报表 */
    viewContractReviewReport : function () {
        var contract = Gikam.getComp('emc-contract-audit-list-grid').getActivedRow();
        if (Gikam.isEmpty(contract)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var reviewReportLineId = contract.reviewReportLineId;
        if (Gikam.isEmpty(reviewReportLineId)) {
            Gikam.alert('EMC.MODULE.MDM.CONTRACTS.TIP.HAS_NO_REVIEW_REPORT_TEMPLATE');
            return;
        }
        Gikam.viewReport(reviewReportLineId, contract);
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-contract-audit-list-layout',
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

    pass : function() {
        var _this = this;
        var submitFlag = false;
        var valiDateFlag = _this.param.bpmn_statusCode==='quality'?true:false;
        var grid = Gikam.getComp('emc-contract-audit-list-grid');
        var reviewBasicForm = Gikam.getComp('emc-contract-audit-list-review-basic-info-form');
        //评审记录只校验一次
        if (valiDateFlag){
            if (reviewBasicForm.validate()){
                submitFlag = true;
            }
        }else {
            submitFlag = true;
        }
        if (submitFlag) {
            Gikam.create('workflow').pass({
                data : grid.getSelections(),
                pageObject : emcContract
            }).done(function() {
                grid.refresh();
            });
        }
    },

    reject : function() {
        var grid = Gikam.getComp('emc-contract-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcContract
        }).done(function() {
            grid.refresh();
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
