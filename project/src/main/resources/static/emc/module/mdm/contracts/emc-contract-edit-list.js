emcContract.editPage = {

    getContractGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-contract-edit-list-grid',
            url : emcContract.baseUrl + '/queries/raw',
            columns : emcContract.getEditListColumns(),
            service : 'emcContractServiceImpl',
            page : true,
            deleteFormatter : function(row) {
                return row.contractName || row.contractNo || row.id;
            },
            requestData : {
                org_filter : '1',
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-contract-edit-list-monitor-item-grid' ])
                    Gikam.getComp('emc-contract-edit-list-base-info-form').cleanData();
                    if(Gikam.getComp('emc-contract-edit-list-uploader')) {
                        Gikam.getComp('emc-contract-edit-list-uploader').grid.cleanData();
                    }
                }
            },
            onRowActive : function(index, row) {
                _this.onContractRowActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.addContract();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-contract-edit-list-grid').deleteRows(emcContract.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                hidden : true,
                onClick : function() {
                    _this.submit();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                hidden : true,
                onClick : function() {
                    _this.viewContract();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.COPY',
                icon : 'copy',
                hidden : true,
                onClick : function() {
                    _this.copyContract();
                }
            } ]
        }
    },

    /** 查看合同报表 */
    viewContract : function() {
        var selections = Gikam.getComp('emc-contract-edit-list-grid').getSelections();
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

    /** 添加合同 */
    addContract : function () {
        Gikam.preInsert({
            modalTitle : 'EMC.MODULE.MDM.CONTRACTS.TITLE.ADD_CONTRACT',
            height : 360,
            fields : emcContract.getPreInsertFields(),
            url : emcContract.baseUrl,
            onFormRendered : function() {
                var _this = this;
                if (!_this.model) {
                    // 此处有时序问题，需要添加延迟
                    setTimeout(function () {
                        _this.hideFields([ 'ext$.consultcategory' ]);
                    }, 100);
                } else {
                    _this.hideFields([ 'ext$.consultcategory' ]);
                }
            }
        }).done(function() {
            Gikam.getComp('emc-contract-edit-list-grid').refresh();
        });
    },

    /** 选中合同 */
    onContractRowActive : function(row) {
        Gikam.getAsyncComp('emc-contract-edit-list-base-info-form').done(function(form){
            form.refresh({
                url : Gikam.printf(emcContract.baseUrl + '/{id}', {
                    id : row.id
                })
            }).done(function (data) {
             });
        });

        let tab = Gikam.getComp('emc-contract-edit-list-base-info-tab');
        if (row.ext$.contractcategory === 'plan') {
            tab.revertPanel('emc-contract-edit-list-split-panel');
            Gikam.getAsyncComp('emc-contract-edit-list-split-grid').done(function (grid) {
                grid.refresh({
                    url : '/secure/emc/module/mdm/contract/split-contracts/queries',
                    requestData : {
                        contractId_SEQ : row.id
                    },
                    order : {
                        'id' : 'asc'
                    },
                })
            })
        } else {
            tab.removePanel('emc-contract-edit-list-split-panel');
        }

        Gikam.getComp('emc-contract-edit-list-uploader').setOptions({
            bizId : row.id,
        });
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            id : 'emc-contract-edit-list-base-info-tab',
            panels : [ {
                title : 'EMC.MODULE.MDM.CONTRACTS.TITLE.BASEINFO',
                id : 'emc-contract-edit-list-base-info-panel',
                items : [ {
                    type : 'form',
                    id : 'emc-contract-edit-list-base-info-form',
                    columns : 3,
                    service : 'emcContractServiceImpl',
                    panels: emcContract.getGroupBaseInfoFormFields()
                } ]
            }, {
                title : 'EMC.MODULE.MDM.CONTRACTS.TITLE.SPLITCONTRACT',
                id : 'emc-contract-edit-list-split-panel',
                items : [ {
                    type : 'grid',
                    id : 'emc-contract-edit-list-split-grid',
                    columns : emcContract.getSplitContractColumns(),
                    deleteFormatter : function(row) {
                        return row.ext$.testname || row.ext$.methodname || row.id;
                    },
                    service : 'emcSplitContractServiceImpl',
                } ]
            }, {
                title : 'EMC.MODULE.MDM.CONTRACTS.TITLE.FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-contract-edit-list-uploader',
                    dbTable : 'T_EMC_CONTRACT',
                } ]
            } ]
        }
    },

    /** 批量修改采样数 */
    batchUpdateSampleNum : function() {
        var selections = Gikam.getComp('emc-contract-edit-list-monitor-item-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var activeRow = Gikam.getComp('emc-contract-edit-list-grid').getActivedRow();

        var modal = Gikam.create('modal', {
            title: "EMC.MODULE.MDM.CONTRACTS.MODAL.BATCH_UPDATE",
            width : 400,
            height : 300,
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
                                    item.ext$.foldernum = formData.ext$.foldernum;
                                    item.ext$.frequency = formData.ext$.frequency;
                                    item.ext$.samplenum = formData.ext$.samplenum;
                                    item.ext$.remark = formData.ext$.remark;
                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(emcContract.baseUrl + '/' + activeRow.id, Gikam.getJsonWrapper({}, [ 'emcMonitorItemServiceImpl', selections ])).done(function (data) {
                                    Gikam.getComp('emc-contract-edit-list-monitor-item-grid').refresh();
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
            fields: emcContract.getBatchUpdateFormFields(),
            autoSave: false
        });
    },


    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-contract-edit-list-layout',
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
    },

    insertExpenseCategory : function() {
        var activeRow = Gikam.getComp('emc-contract-edit-list-grid').getActivedRow();
        if (Gikam.isEmpty(activeRow)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var url = Gikam.printf(emcContract.baseUrl + '/{contractId}/expense-categories', {
            contractId : activeRow.id
        });

        workspace.window.showMask(true);
        Gikam.getComp('emc-contract-edit-list-expense-category-grid').insert(url).always(function() {
            workspace.window.closeMask();
        });
        ;
    },

    loadSchemeItemGroupPage : function(schemeName) {
        var activeRow = Gikam.getComp('emc-contract-edit-list-grid').getActivedRow();
        if (Gikam.isEmpty(activeRow)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.CONTRACTS.TITLE.ADD_MONITOR_SCHEME',
            width : '70%',
            url : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/monitor-tmpls/page/scheme-test-item-choose-list',
            onAfterClose : function(rows) {
                if (rows && rows.length > 0) {
                    workspace.window.showMask(true);
                    var chooseData = rows.map(function(item) {
                        return {
                            contractId : activeRow.id,
                            testId : item.testId,
                            methodId : item.methodId,
                            itemGroup : item.itemGroup,
                            itemGroupId : item.itemGroupId,
                            monitorTmplId : item.monitorTmplId,
                            orderCategory : item.monitorTmplName,
                        }
                    });
                    Gikam.postText(emcContract.baseUrl + '/monitor-items', Gikam.getJsonWrapper(null, [ 'emcMonitorItemServiceImpl', chooseData ])).done(function(id) {
                        Gikam.getComp('emc-contract-edit-list-monitor-item-grid').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    copeExpenseCategory : function() {
        var selections = Gikam.getComp('emc-contract-edit-list-expense-category-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        workspace.window.showMask(true);
        Gikam.post(emcContract.baseUrl + '/expense-categories/action/copy', Gikam.getJsonWrapper(null, [ 'emcExpenseCategoryServiceImpl', selections ])).always(function() {
            workspace.window.closeMask();
            Gikam.getComp('emc-contract-edit-list-expense-category-grid').refresh();
        });
    },

    copyContract : function() {
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.CONTRACTS.TITLE.COPY_CONTRACT',
            url : emcContract.baseUrl + '/page/contract-copy-list',
            width : 700,
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                workspace.window.showMask(true);
                Gikam.post(emcContract.baseUrl + '/action/copy', Gikam.getJsonWrapper(null, [ 'emcContractServiceImpl', rows ])).always(function() {
                    workspace.window.closeMask();
                    Gikam.getComp('emc-contract-edit-list-grid').refresh();
                });
            }
        });
    },

    calculateTotalPrict : function() {
        workspace.window.showMask(true);
        var contractId = Gikam.getComp('emc-contract-edit-list-grid').getActivedRow().id;
        Gikam.post(Gikam.printf(emcContract.baseUrl + '/{id}/action/calculate', {
            id : contractId
        })).always(function() {
            workspace.window.closeMask();
            Gikam.getComp('emc-contract-edit-list-grid').refresh();
            Gikam.getComp('emc-contract-edit-list-expense-category-grid').refresh();
        });
    },

    submit : function() {
        var grid = Gikam.getComp('emc-contract-edit-list-grid');

        var selections = grid.getSelections();
        var reportNotConfigured = selections.some(function (row) {
            return Gikam.isEmpty(row.ext$.reportconfiglineid);
        });
        if (reportNotConfigured) {
            Gikam.alert('EMC.MODULE.MDM.CONTRACTS.TIP.REPORT_NOT_CONFIGURED');
            return;
        }

        var form = Gikam.getComp('emc-contract-edit-list-base-info-form');
        if (form.validate()) {
            Gikam.create('workflow').submit({
                data : grid.getSelections(),
                pageObject : emcContract
            }).done(function() {
                grid.refresh();
            });
        }
    },
};
