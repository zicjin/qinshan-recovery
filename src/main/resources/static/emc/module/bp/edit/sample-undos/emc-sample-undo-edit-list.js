emcSampleUndo.editPage = {

    getSampleTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TAB.SAMPLE_NAME',
                items : [ {
                    type : 'grid',
                    id : 'emc-sample-undo-edit-list-grid',
                    url : '/secure/emc/module/bp/sample/samples/queries',
                    requestData : {
                        targetType : 'sampleUndo',
                        status_DIFFER : 'draft',
        	            orgId_SB : workspace.user.ext$.parentorgid
                    },
                    page : true,
                    pageSize : 50,
                    columns : emcSampleUndo.getSampleListColumns(),
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
                        type : 'button',
                        text : 'GIKAM.WORKFLOW.BUTTON.UNDO',
                        icon : 'reject',
                        onClick : function() {
                            _this.undo();
                        }
                    } ]
                } ]
            } ]
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
                    columns : emcSampleUndo.getOrderListColumns(),
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
                    columns : emcSampleUndo.getContainerListColumns(),
                    checkOnActive : true,
                } ]
            } ]
        }
    },

    undo : function() {
        // 已接收的采样单退回，不建议多选，误操作数据无法恢复
        var selections = Gikam.getComp('emc-sample-undo-edit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.confirm('GIKAM.TIP.CONFIRM', 'EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.ACCEPTED_UNDO', function() {
            workspace.window.showMask(true);
            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/action/undo-accept', Gikam.getJsonWrapper({}, ['', selections] )).done(function() {
                Gikam.getComp('emc-sample-undo-edit-list-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    viewReport : function() {
        var selections = Gikam.getComp('emc-sample-undo-edit-list-grid').getSelections();
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
                url : emcSampleUndo.baseUrl + '/page/sample-annex-list?id=' + v
            });
        }
    },

    urlManager : {
        sampleGrid : '/secure/emc/module/bp/sample/samples/queries/undoable',
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
