emcContract.searchPage = {

    getContractGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-contract-search-list-grid',
            url : emcContract.baseUrl + '/queries/searchable',
            columns : emcContract.getSearchListColumns(),
            service : 'emcContractServiceImpl',
            readonly : true,
            requestData : {
                processStatus_SEQ : 'done',
                orgId_SB: workspace.user.ext$.parentorgid
            },
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
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    _this.viewContract();
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.UNDO',
                icon : 'reject',
                onClick : function() {
                    _this.undo();
                }
            } ]
        }
    },
    
    /** 退回 */
    undo : function() {
        var grid = Gikam.getComp('emc-contract-search-list-grid');
        var selections = grid.getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.create('workflow').undo({
            data : grid.getSelections(),
            pageObject : emcContract
        }).done(function() {
            grid.refresh();
        });
    },

    
    /** 选中合同 */
    onContractRowActive : function(row) {
        Gikam.getAsyncComp('emc-contract-search-list-base-info-form').done(function(form){
            form.refresh({
                url : Gikam.printf(emcContract.baseUrl + '/{id}', {
                    id : row.id
                })
            });
        });

        Gikam.refreshGrids({
            'emc-contract-search-list-monitor-item-grid' : emcContract.baseUrl + '/{id}/monitor-items/queries',
            'emc-contract-search-list-review-content-grid' : emcContract.baseUrl + '/{id}/review-contents'
        }, {
            id : row.id
        });

        Gikam.getAsyncComp('emc-contract-search-list-review-basic-info-form').done(function(form){
            form.refresh({
                url : Gikam.printf(emcContract.baseUrl + '/{id}/review-basic-info', {
                    id : row.id
                })
            });
        });

        Gikam.getComp('emc-contract-search-list-uploader').setOptions({
            bizId : row.id,
        });
    },

    /** 查看合同报表 */
    viewContract : function() {
        var selections = Gikam.getComp('emc-contract-search-list-grid').getSelections();
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


    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            id : 'emc-contract-search-list-base-info-tab',
            panels : [ {
                title : 'EMC.MODULE.MDM.CONTRACTS.TITLE.BASEINFO',
                id : 'emc-contract-search-list-base-info-panel',
                items : [ {
                    type : 'form',
                    id : 'emc-contract-search-list-base-info-form',
                    columns : 3,
                    service : '',
                    readonly : true,
                    panels: emcContract.getGroupBaseInfoFormFields()
                } ]
            }, {
                title : 'EMC.MODULE.MDM.CONTRACTS.TITLE.MONITOR_ITEM',
                id : 'emc-contract-search-list-monitor-item-panel',
                items : [ {
                    type : 'grid',
                    id : 'emc-contract-search-list-monitor-item-grid',
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
                    id : 'emc-contract-search-list-uploader',
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
            fields: emcContract.getReviewBasicInfoFields()
        }
    },
    
    getReviewContentGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-contract-search-list-review-content-grid',
            columns : emcContract.getReviewContentColumns(),
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
            Gikam.alert('EMC.MODULE.MDM.CONTRACTS.TIP.HAS_NO_REVIEW_REPORT_TEMPLATE');
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
