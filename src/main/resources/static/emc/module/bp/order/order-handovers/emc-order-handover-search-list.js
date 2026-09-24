emcOrderHandover.searchPage = {

    getSampleTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TAB.SAMPLE_NAME',
                items : [ {
                    type : 'grid',
                    id : 'emc-order-handover-search-list-grid',
                    url : _this.urlManager['sampleGrid'],
                    page : true,
                    requestData : {
                        targetType : 'sampleHandover',
                        status_DIFFER : 'draft',
                        org_filter : '1',
                    },
                    columns : emcOrderHandover.getSampleListColumns(),
                    checkOnActive : true,
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        } else {
                            Gikam.cleanCompData([ 'emc-order-edit-list-grid', 'emc-container-edit-list-grid' ]);
                        }
                    },
                    onRowActive : function(index, row) {
                        if (row) {
                            Gikam.getComp('emc-order-edit-list-grid').setOptions({
                                requestData : {
                                    sampleId_SEQ : row.id
                                },
                                url : _this.urlManager['orderGrid']
                            });
                            Gikam.getComp('emc-order-edit-list-grid').refresh();
                        }
                    },
                    toolbar : [ {
                        type : 'form',
                        id : 'emc-report-config-line-select-base-info-form',
                        autoSave : false,
                        hidden : true,
                        fields : [ {
                            field : 'reportName',
                            title : 'T_CORE_REPORT_CONFIG_LINE.REPORT',
                            type : 'select',
                            search : true,
                            category : 'reportConfigLine',
                            param : {
                                reportConfigId : 'samplingNumber'
                            }
                        } ]
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.PRINT',
                        icon : 'print',
                        hidden : true,
                        onClick : function() {
                            _this.viewReport();
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.BUTTON.HANDOVER_SAMPLE_PRINT',
                        icon : 'print',
                        onClick : function() {
                            Gikam.viewReport('samplingNumber', Gikam.getComp('emc-order-handover-search-list-grid').getSelections());
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_SAMPLE',
                        icon : 'print',
                        onClick : function() {
                            var selections = Gikam.getComp('emc-order-handover-search-list-grid').getSelections();
                            if (selections.length !== 1) {
                                Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                return;
                            }
                            Gikam.viewReport(selections[0].reportConfigLineId, selections[0]);
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.BUTTON.TASK_LIST_PRINT',
                        icon : 'print',
                        onClick : function() {
                            Gikam.viewReport('deliveringLabel', Gikam.getComp('emc-order-handover-search-list-grid').getSelections());
                        }
                    },{
                        type : 'button',
                        text : '样品标签',
                        icon : 'print',
                        onClick : function() {
                            Gikam.viewReport('sampling', Gikam.getComp('emc-order-handover-search-list-grid').getSelections());
                        }
                    } ]
                } ]
            } ],
        }
    },

    getOrderTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.S,
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TAB.ORDER',
                items : [ {
                    type : 'Grid',
                    id : 'emc-order-edit-list-grid',
                    url : '',
                    columns : emcOrderHandover.getOrderListColumns(),
                    checkOnActive : true,
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        }
                    },
                    onRowActive : function(index, row) {
                        if (row) {
                            var _containerGridUrl = Gikam.printf(_this.urlManager['containerGrid'], {
                                id : row.id
                            });

                            Gikam.getAsyncComp('emc-container-edit-list-grid').done(function(grid) {
                                grid.refresh({
                                    url : _containerGridUrl
                                });
                            });
                        }
                    },
                } ]
            } ]
        }
    },

    getContainerTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.S,
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TAB.CONTAINER',
                items : [ {
                    type : 'Grid',
                    id : 'emc-container-edit-list-grid',
                    url : '',
                    columns : emcOrderHandover.getContainerListColumns(),
                    checkOnActive : true,
                    toolbar : [ {
                        type : 'button',
                        text : 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.BUTTON.SYNC_CONTAINER_INFO',
                        icon : 'refresh',
                        onClick : function() {
                            _this.syncContainerInfo();
                        },
                    }],
                } ]
            } ]
        }
    },

    viewReport : function() {
        var selections = Gikam.getComp('emc-order-handover-search-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var data = Gikam.getComp('emc-report-config-line-select-base-info-form').getData();
        if (Gikam.isEmpty(data.reportName)) {
            Gikam.alert('EMC.MODULE.SYS.REPORTS.TIP.SELECT_REPORT_FIRST');
            return;
        }

        Gikam.viewReport(data.reportName, selections[0]);
    },

    viewSampleFile : function(v) {
        if (v) {
            Gikam.create('modal', {
                width : 800,
                height : 500,
                title : 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.BUTTON.ANNEX',
                url : emcOrderHandover.baseUrl + '/page/sample-annex-list?id=' + v
            });
        }
    },

    /** 同步容器信息 */
    syncContainerInfo : function() {
        var selections = Gikam.getComp('emc-container-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.confirm('GIKAM.TIP.CONFIRM', 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.SYNC_OVERRIDE_CONTAINER_INFO', function() {
            workspace.window.showMask(true);
            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/sync-container-info', Gikam.getJsonWrapper({}, ['', selections] )).done(function() {
                Gikam.getComp('emc-container-edit-list-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        })
    },

    urlManager : {
        sampleGrid : '/secure/emc/module/bp/sample/samples/queries',
        orderGrid : '/secure/emc/module/bp/bp/orders/queries',
        containerGrid : '/secure/emc/module/bp/bp/orders/{id}/order-tasks/queries'
    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-order-receipt-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '50%',
                items : [ _this.getSampleTab() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    west : {
                        width : '50%',
                        items : [ _this.getOrderTab() ]
                    },
                    center : {
                        items : [ _this.getContainerTab() ]
                    }
                } ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
