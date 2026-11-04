coreNfssiOrder.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-nfssi-order-edit-list-grid',
            url : coreNfssiOrder.baseUrl + '/queries',
            columns : coreNfssiOrder.getEditListColumns(),
            activateFirstRow : true,
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-nfssi-order-base-info').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(coreNfssiOrder.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
                Gikam.getAsyncComp('core-nfssi-order-report-list-grid').done(function(grid) {
                    grid.refresh({
                        url : IFM_CONTEXT + '/secure/core/module/nfssi/reports/queries',
                        requestData : {
                            orderId_SEQ : row.id
                        }
                    });
                });
                Gikam.getAsyncComp('core-nfssi-order-test-item-list-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreNfssiOrder.baseUrl + '/{id}/order-test-items/queries', {
                            id : row.id
                        })
                    });
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.addOrdersFromNfssi();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.NFSSI.REQUEST_AGAIN',
                icon : 'refresh',
                onClick : function() {
                    var grid = Gikam.getComp('core-nfssi-order-edit-list-grid');
                    var selections = grid.getSelections();
                    workspace.window.showMask(true);
                    Gikam.put(coreNfssiOrder.baseUrl + '/action/re-request', Gikam.getJsonWrapper({}, ['', selections])).done(function() {
                        grid.refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            } ]
        }
    },

    loadReportDetailPage : function(id) {
        workspace.window.load(IFM_CONTEXT + '/secure/core/module/nfssi/reports/page/detail' + Gikam.param({
            id : id
        }));
    },

    getTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ _this.getOderInfo(), _this.getTestItem(), _this.getReport() ]
        }
    },
    
    getOderInfo : function() {
        return {
            title : 'T_CORE_NFSSI_ORDER.SERVICE_NAME',
            items : [ {
                type : 'form',
                service : 'coreNfssiOrderServiceImpl',
                id : 'core-nfssi-order-base-info',
                fields : coreNfssiOrder.getOrderInfoFormFields(),
                titleWidth : 120,
                columns : 2
            } ]
        }
    },
    
    getReport : function() {
        var _this = this;
        return {
            title : 'T_CORE_NFSSI_REPORT.SERVICE_NAME',
            items : [ {
                type : 'grid',
                service : 'coreNfssiReportServiceImpl',
                id : 'core-nfssi-order-report-list-grid',
                columns : coreNfssiOrder.getReportListColumns(),
                onCellClick : function(field, row) {
                    field === 'id' && _this.loadReportDetailPage(row.id);
                },
                toolbar : [ {
                    type : 'button',
                    text : 'CORE.MODULE.NFSSI.RESEND_AGAIN',
                    icon : 'refresh',
                    onClick : function() {
                        var grid = Gikam.getComp('core-nfssi-order-report-list-grid');
                        if (grid.getSelections().length != 1) {
                            Gikam.alert('CORE.MODULE.NFSSI.CAN_ONLY_SELECT_ONE_ROW');
                            return;
                        }
                        workspace.window.showMask(true);
                        Gikam.put(IFM_CONTEXT + '/secure/core/module/nfssi/reports/action/re-push', Gikam.getJsonWrapper({}, ['', grid.getSelections()])).done(function() {
                            grid.refresh();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                } ]
            } ]
        }
    },
    
    getTestItem : function() {
        return {
            title : 'T_CORE_NFSSI_ORDER_TEST_ITEM.SERVICE_NAME',
            items : [ {
                type : 'grid',
                height : 250,
                service : 'coreNfssiOrderTestItemServiceImpl',
                id : 'core-nfssi-order-test-item-list-grid',
                columns : coreNfssiOrder.getOrderTestItemListColumns(),
                activateFirstRow : true,
                onRowActive : function(index, row) {
                    Gikam.getAsyncComp('core-nfssi-order-test-reson-list-grid').done(function(grid) {
                        grid.refresh({
                            url : Gikam.printf(coreNfssiOrder.baseUrl + '/order-test-items/{testItemId}/test-reasons/queries', {
                                testItemId : row.id
                            })
                        });
                    });
                    Gikam.getAsyncComp('core-nfssi-order-verify-reason-list-grid').done(function(grid) {
                        grid.refresh({
                            url : Gikam.printf(coreNfssiOrder.baseUrl + '/order-test-items/{testItemId}/verify-reasons/queries', {
                                testItemId : row.id
                            })
                        });
                    });
                }
            }, {
                type : 'tab',
                height : 295,
                panels : [ {
                    title : 'CORE.MODULE.NFSSI.TEST_REASON_INFO',
                    items : [ {
                        type : 'grid',
                        service : 'coreNfssiTestReasonServiceImpl',
                        id : 'core-nfssi-order-test-reson-list-grid',
                        columns : coreNfssiOrder.getTestReasonListColumns()
                    } ]
                }, {
                    title : 'CORE.MODULE.NFSSI.VERIFY_REASON_INFO',
                    items : [ {
                        type : 'grid',
                        service : 'coreNfssiVerifyReasonServiceImpl',
                        id : 'core-nfssi-order-verify-reason-list-grid',
                        columns : coreNfssiOrder.getVerifyReasonListColumns()
                    } ]
                } ]
            } ]
        }
    },
    
    addOrdersFromNfssi : function() {
        var modal = Gikam.create('modal', {
            title: 'CORE.MODULE.NFSSI.MODEL_ADD_REQUEST',
            width: 400,
            height: 240
        });
        Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type : 'form',
                    id : 'core-nfssi-model-order-insert-form',
                    columns : 1,
                    fields : coreNfssiOrder.getAddNfssiOrderFormFields()
                }, {
                    type: 'btnToolbar',
                    items: [ {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CONFIRM',
                        'class': 'blue',
                        onClick : function() {
                            var form = Gikam.getComp('core-nfssi-model-order-insert-form');
                            if (Gikam.isFalse(form.validate()))
                                return;
                            var insertData = form.getData();
                            if (insertData.receivedStartTime > insertData.receivedEndTime) {
                                Gikam.alert('CORE.MODULE.NFSSI.STARTTIME_CAN_NOT_AFTER_ENDTIME');
                                return;
                            }
                            modal.window.showMask(true);
                            Gikam.postText(coreNfssiOrder.baseUrl, Gikam.getJsonWrapper({}, ['', [ insertData ]])).done(function(item) {
                                modal.close();
                                if (item != null) {
                                    Gikam.getComp('core-nfssi-order-edit-list-grid').refresh();
                                }
                            }).always(function() {
                                modal.window.closeMask();
                            });
                        }
                    }, {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CANCEL',
                        onClick : function() {
                            modal.close();
                        }
                    } ]
                } ]
            }
        });
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-nfssi-order-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '30%',
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getTab() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
