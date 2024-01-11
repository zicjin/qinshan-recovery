emcOrderRetain.editPage = {

    // 列表布局参数
    getGridParam : function() {
        var _this = this;
        var columns = emcOrderRetain.getEditListColumns();
        var url = emcOrderRetain.baseUrl + '/queries/raw';
        if (_this.param.remind === 'overdue') {
            url = emcOrderRetain.baseUrl + '/queries-overdue';
            columns = emcOrderRetain.getEditListColumns_Overdue();
        }
        return {
            type : 'grid',
            id : 'emc-order-retain-edit-list-grid',
            url : url,
            columns : columns,
            checkOnActive : true,
            service : 'emcOrderRetainServiceImpl',
            requestData : {
            	isretain_SEQ : "true",
            },
            page : true,
            deleteFormatter : function(row) {
                return row.ext$.projno || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.ORDER.ORDERRETAINS.BUTTON.HANDLE_APPLY',
                icon : 'edit',
                onClick : function() {
                    _this.handleRetainOrder();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.ORDER.ORDERRETAINS.BUTTON.RETAIN',
                icon : 'add',
                onClick : function() {
                    _this.retainOrder();
                }
            }, {
                type : 'button',
                text : '批录入',
                icon : 'edit',
                onClick : function() {
                	_this.batchUpdate();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-order-retain-edit-list-grid').deleteRows(emcOrderRetain.baseUrl);
                }
            } ]
        }
    },

    handleRetainOrder : function() {
        var selections = Gikam.getComp('emc-order-retain-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var hasHandledOrder = false
        selections.forEach(function (row) {
            if (row.handleRecord) {
                hasHandledOrder = true
            }
        })
        if (hasHandledOrder) {
            Gikam.alert('EMC.MODULE.BP.ORDER.ORDERRETAINS.TIP.HASHANDLEDORDER');
            return;
        }
        var modal = Gikam.create('modal', {
            title: "EMC.MODULE.BP.ORDER.ORDERRETAINS.MODAL.HANDLE",
            width : 500,
            height : 250,
        });
        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type: 'btnToolbar',
                    items: [ {
                        type : 'button',
                        text : 'EMC.MODULE.BP.ORDER.ORDERRETAINS.MODAL.BTN.CONFIRM',
                        class : 'blue',
                        onClick : function() {
                            if (form.validate()) {
                                selections.forEach(function (item) {
                                    item.handleRecord = form.options.data.handleRecord
                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.postText(emcOrderRetain.baseUrl + "/action/handle", Gikam.getJsonWrapper({}, [ '', selections ])).done(function (data) {
                                    Gikam.getComp('emc-order-retain-edit-list-grid').refresh();
                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close();
                                });
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.BP.ORDER.ORDERRETAINS.MODAL.BTN.CANCEL',
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
            fields: [
                {
                    field : 'handleRecord',
                    title : 'T_EMC_ORDER_RETAIN.HANDLERECORD',
                    validators : [ 'notEmpty', 'strLength[0, 120]' ],
                    preInsert : true,
                    type : 'textarea',
                    height : 100,
                }
            ],
            autoSave: false
        });
    },

    retainOrder : function() {
        Gikam.create('modal', {
            width : 900,
            height : 600,
            title : Gikam.choose.getConfig('orderRetainCategory').title,
            url : Gikam.choose.getConfig('orderRetainCategory').url,
            onAfterClose : function(data) {
                if (Gikam.isEmpty(data.selections)) {
                    return;
                }
                data.selections.forEach(function (row) {
                    row.retainDays = data.formData.retainDays
                    row.retainLoc = data.formData.retainLoc
                    row.retainQty = data.formData.retainQty
                    row.retainUserId = data.formData.retainUserId
                    row.ext$.retainDate = data.formData.ext$.retainDate
                   
                })
                workspace.window.showMask(true);
                Gikam.postText(emcOrderRetain.baseUrl + '/action/confirm', Gikam.getJsonWrapper({}, [ '', data.selections ])).done(function (data) {
                    Gikam.getComp('emc-order-retain-edit-list-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    
    batchUpdate : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-order-retain-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
            width : 400,
            height : 270,
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
                                    item.handleDate = formData.handleDate;
                                    item.handleRecord = formData.handleRecord;
                                    item.handleUserId = formData.handleUserId;
                                    item.ext$.handleusernames = formData.ext$.handleusernames;
                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/order/order-retains/' + selections[0].id, Gikam.getJsonWrapper({}, [ 'PfOrderRetainServiceImpl', selections ])).done(function (data) {
                                    Gikam.getComp('emc-order-retain-edit-list-grid').refresh();
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
            fields: emcOrderRetain.getBatchInputFields(),
            autoSave: false
        });

    },
    
    
    // 创建页面
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    // 初始化
    init : function(param) {
        this.param = param;
        this.create();
    }
};
