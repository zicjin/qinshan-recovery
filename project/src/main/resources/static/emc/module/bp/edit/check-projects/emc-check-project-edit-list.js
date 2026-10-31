emcCheckProject.editPage = {

    getFolderGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            filterOpen : true,
            service : 'emcFolderServiceImpl',
            id : 'emc-check-project-edit-list-grid',
            columns : emcCheckProject.getFolderListColumns(),
            url : '/secure/emc/module/bp/bp/folders/queries',
            requestData : {
                target_filter : JSON.stringify({
                    type : 'assigned',
                    targetTable : "T_EMC_ORDER_TASK",
                    targetMatchColumn : "FOLDERID",
                    thisMatchColumn : "ID",
                    filter : [ {
                        targetFilterColumn : 'STATUS',
                        targetFilterValue : 'logged'
                    }, {
                        targetFilterColumn : 'ANALYSISTYPE',
                        targetFilterValue : 'analysis'
                    } ]
                }),
                targetType : 'checkProjects',
                org_filter : '1',
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-check-project-edit-list-order-grid').cleanData();
                    Gikam.getComp('emc-check-project-edit-list-order-task-grid').cleanData();
                }
            },
            deleteFormatter : function(row) {
                return row.ext$.projname || row.id;
            },
            onRowActive : function(index, row) {
                _this.onFolderActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                hidden : true,
                onClick : function() {
                    _this.delFolder();
                },
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.UNDO',
                icon : 'back',
                hidden : true,
                onClick : function() {
                    _this.undo();
                }
            } ,{
                type: 'caption',
                text: '修改废气的因子时请特别注意，如果删除因子，采样的已经录数的分析项需要重新录入',
                color: 'red'
            }]
        }
    },

    getOrderGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.XL,
            panels : [ {
                title : 'EMC.MODULE.BP.EDIT.CHECKPROJECTS.TITLE.ORDER',
                items : [ {
                    type : 'grid',
                    id : 'emc-check-project-edit-list-order-grid',
                    service : 'emcOrderServiceImpl',
                    columns : emcCheckProject.getFolderOrderColumns(),
                    onRowActive : function(index, row) {
                        _this.onOrderActive(row);
                    },
                    deleteFormatter : function(row) {
                        return row.orderNo || row.id;
                    },
                    requestData : {
                        target_filter : JSON.stringify({
                            type : 'assigned',
                            targetTable : "T_EMC_ORDER_TASK",
                            targetMatchColumn : "ORDERID",
                            thisMatchColumn : "ID",
                            filter : [ {
                                targetFilterColumn : 'STATUS',
                                targetFilterValue : 'logged'
                            }, {
                                targetFilterColumn : 'ANALYSISTYPE',
                                targetFilterValue : 'analysis'
                            } ]
                        })
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        } else {
                            Gikam.getComp('emc-check-project-edit-list-order-task-grid').cleanData();
                        }
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        hidden : true,
                        onClick : function() {
                            _this.delOrder();
                        }
                    } ]
                } ]
            }, ]
        }
    },

    getOrderTaskGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.XL,
            panels : [ {
                title : 'EMC.MODULE.BP.EDIT.CHECKPROJECTS.TITLE.ORDER_TEST',
                items : [ {
                    type : 'grid',
                    id : 'emc-check-project-edit-list-order-task-grid',
                    service : 'emcOrderTaskServiceImpl',
                    columns : emcCheckProject.getOrderTaskColumns(),
                    deleteFormatter : function(row) {
                        return row.testName || row.id;
                    },
                	requestData : {
                        org_filter : '1',
        	        },
                    toolbar : [ {
                        type : 'button',
                        text : 'EMC.MODULE.BP.EDIT.CHECKPROJECTS.BUTTON.ADD_ORDER_TASK',
                        icon : 'add',
                        onClick : function() {
                            _this.addOrderTask();
                        },
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.BP.EDIT.CHECKPROJECTS.BUTTON.DEL_ORDER_TASKD',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.delOrderTask();
                        },
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.BP.EDIT.CHECKPROJECTS.BUTTON.EDIT_ITEM',
                        icon : 'edit',
                        onClick : function() {
                            _this.editItem();
                        },
                    }, {
                        type : 'button',
                        text : '客服方案修改',
                        icon : 'edit',
                        onClick : function() {
                            _this.editOrderTask();
                        },
                    }, {
                        type : 'button',
                        text : '删除分析项组',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.delOrderItemGroup();
                        },
                    },  ]
                } ]
            }, ]
        }
    },

    delOrderItemGroup : function() {
        var selections = Gikam.getComp('emc-check-project-edit-list-order-task-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        let itemGroupIdFlag = selections.some(item => Gikam.isEmpty(item.ext$.itemgroupid));
        if (itemGroupIdFlag) {
            Gikam.alert('分析项组数据有误');
            return;
        }

        let doneFlag = selections.some(item => item.status === 'done');
        if (doneFlag) {
            Gikam.alert('EMC.MODULE.BP.EDIT.CHECKPROJECTS.TIP.CAN_NOT_DEL_TEST_WHEN_DONE');
            return true;
        }

        Gikam.confirm('GIKAM.TIP.CONFIRM_TO_DELETE', 'GIKAM.TIP.CONFIRM_TO_DELETE_FOLLOWING_ITEMS', function() {
            workspace.window.showMask(true);
            Gikam.del('/secure/emc/module/bp/bp/orders/check-projects/action/order-item-group', Gikam.getJsonWrapper({
            }, [ '', selections ])).done(function() {
                Gikam.getComp('emc-check-project-edit-list-order-task-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    editOrderTask : function() {
        var selections = Gikam.getComp('emc-check-project-edit-list-order-task-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var itemGroupId = selections[0].ext$.itemgroupid;
        if (Gikam.isEmpty(itemGroupId)) {
            Gikam.alert('分析项组数据有误');
            return;
        }
        Gikam.create('modal', {
            title : '编辑测试',
            width : '90%',
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/page/check-project-order-item-group-edit' + Gikam.param({
                monitorTmplId : selections[0].monitorTmplId,
                orderCategory : selections[0].orderCategory,
                itemGroupId : selections[0].ext$.itemgroupid,
                orderId : selections[0].orderId,
                testMethodId : selections[0].testMethodId,
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-check-project-edit-list-order-task-grid').refresh();
            }
        });

    },

    addOrderTask : function() {
        var selections = Gikam.getComp('emc-check-project-edit-list-order-grid').getSelections();
        if (selections.length >= 1) {
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.EDIT.CHECKPROJECTS.MODAL.ADD_TEST',
                width : '70%',
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/page/check-project-order-item-group-addition' + Gikam.param({
                    monitorTmplId : selections[0].monitorTmplId,
                    orderCategory : selections[0].orderCategory,
                    orderId : selections[0].id,
                }),
                onAfterClose : function() {
                    Gikam.getComp('emc-check-project-edit-list-order-task-grid').refresh();
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    delOrderTask : function() {
        var selections = Gikam.getComp('emc-check-project-edit-list-order-task-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let doneFlag = selections.some(item => item.status === 'done');
        if (doneFlag) {
            Gikam.alert('EMC.MODULE.BP.EDIT.CHECKPROJECTS.TIP.CAN_NOT_DEL_TEST_WHEN_DONE');
            return true;
        }

        Gikam.confirm('GIKAM.TIP.CONFIRM_TO_DELETE', 'GIKAM.TIP.CONFIRM_TO_DELETE_FOLLOWING_ITEMS', function() {
            workspace.window.showMask(true);
            Gikam.del('/secure/emc/module/bp/bp/orders/check-projects/action/item-group-del', Gikam.getJsonWrapper({
                source : "checkProject",
            }, [ '', selections ])).done(function() {
                Gikam.getComp('emc-check-project-edit-list-order-task-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    editItem : function() {
        var selections = Gikam.getComp('emc-check-project-edit-list-order-task-grid').getSelections();
        if (selections.length === 1) {
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.EDIT.CHECKPROJECTS.MODAL.EDIT_ITEM',
                width : '70%',
                url : '/secure/emc/module/bp/bp/folders/page/check-project-order-item-edit-list' + Gikam.param({
                    orderId : selections[0].orderId,
                    orderTaskId : selections[0].id,
                    testMethodId : selections[0].testMethodId,
                    analysisType : selections[0].analysisType,
                })
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
    },

    undo : function() {
        var grid = Gikam.getComp('emc-check-project-edit-list-grid');
        var selections = grid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var ids = "";
        selections.map(function(item, i) {
            ids += item.id + ',';
        });
        ids = ids.substring(0, ids.length - 1);
        Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries', Gikam.getJsonWrapper({
            f : {
                folderId_IN : ids,
                analysisType_SEQ : 'analysis',
                status_SEQ : 'done'
            },
        })).done(function(r) {
            if (!Gikam.isEmpty(r.rows)) {
                Gikam.alert('EMC.MODULE.BP.EDIT.CHECKPROJECTS.TIP.CAN_NOT_UNDO_FOLDER_WHEN_DONETEST');
            } else {
                Gikam.confirm('EMC.MODULE.BP.EDIT.CHECKPROJECTS.TITLE.UNDO', 'EMC.MODULE.BP.EDIT.CHECKPROJECTS.TIP.UNDO', function() {
                    workspace.window.showMask(true);
                    Gikam.put('/secure/emc/module/bp/bp/folders/action/undo-to-accept', Gikam.getJsonWrapper(null, [ '', selections ])).done(function() {
                        grid.refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                })
            }
        });

    },

    delFolder : function() {
        var selections = Gikam.getComp('emc-check-project-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var ids = "";
        selections.map(function(item, i) {
            ids += item.id + ',';
        });
        ids = ids.substring(0, ids.length - 1);
        Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries', Gikam.getJsonWrapper({
            f : {
                folderId_IN : ids,
                analysisType_SEQ : 'analysis',
                status_SEQ : 'done'
            },
        })).done(function(r) {
            if (!Gikam.isEmpty(r.rows)) {
                Gikam.alert('EMC.MODULE.BP.EDIT.CHECKPROJECTS.TIP.CAN_NOT_DEL_FOLDER_WHEN_DONETEST');
            } else {
                Gikam.getComp('emc-check-project-edit-list-grid').deleteRows('/secure/emc/module/bp/bp/folders');
            }
        });
    },

    delOrder : function() {
        var selections = Gikam.getComp('emc-check-project-edit-list-order-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var ids = "";
        selections.map(function(item, i) {
            ids += item.id + ',';
        });
        ids = ids.substring(0, ids.length - 1);
        Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries', Gikam.getJsonWrapper({
            f : {
                orderId_IN : ids,
                analysisType_SEQ : 'analysis',
                status_SEQ : 'done'
            },
        })).done(function(r) {
            if (!Gikam.isEmpty(r.rows)) {
                Gikam.alert('EMC.MODULE.BP.EDIT.CHECKPROJECTS.TIP.CAN_NOT_DEL_ORDER_WHEN_DONETEST');
            } else {
                Gikam.getComp('emc-check-project-edit-list-order-grid').deleteRows(Gikam.printf('/secure/emc/module/bp/project/projects/{id}/orders', {
                    id : selections[0].projId
                }));
            }
        });
    },

    onFolderActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-check-project-edit-list-order-grid').setOptions({
                requestData : {
                    folderId_SEQ : row.id
                }
            });
            Gikam.getComp('emc-check-project-edit-list-order-grid').refresh({
                url : '/secure/emc/module/bp/bp/orders/queries',
            });
        }
    },

    onOrderActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-check-project-edit-list-order-task-grid').setOptions({
                requestData : {
                    orderId_SEQ : row.id,
                    target_projectEdit : '1'
                }
            });
            Gikam.getComp('emc-check-project-edit-list-order-task-grid').refresh({
                url : '/secure/emc/module/bp/bp/order-tasks/queries',
            });
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ _this.getFolderGridParam() ]
                    },
                    center : {
                        items : [ {
                            type : 'layout',
                            west : {
                                width : '380',
                                items : [ this.getOrderGridParam() ]
                            },
                            center : {
                                items : [ this.getOrderTaskGridParam() ]
                            }
                        } ]
                    }
                } ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
