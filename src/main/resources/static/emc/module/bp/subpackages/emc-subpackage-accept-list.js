emcSubpackage.acceptPage = {

    // 列表布局参数
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-subpackage-accept-list-grid',
            url : emcSubpackage.baseUrl + '/queries',
            columns : emcSubpackage.getAcceptListColumns(),
            requestData : {
                subpackageType_SEQ : 'accepted',
                orgId_SB: workspace.user.ext$.parentorgid,
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-subpackage-accept-list-base-info-form').done(function (form) {
                    form.refresh({
                        url : Gikam.printf(emcSubpackage.baseUrl + '/{id}', {
                            id : row.id
                        }),
                    });
                });

                Gikam.postSync(emcSubpackage.baseUrl + '/tasks/queries', Gikam.getJsonWrapper({
                    f : {
                        subpackageId_SEQ : row.id
                    }
                }, [ '', [] ])).done(function (page) {
                    if (page && page.rows) {
                        _this.projSchemeIds = page.rows.map(item => item.projSchemeId).join();
                        _this.itemGroupIds = page.rows.map(item => item.itemGroupId).join();
                        _this.orderIds = page.rows.map(item => item.orderId).join();
                    }
                });

                let subpackageCategory = row.subpackageCategory;
                let infoTab = Gikam.getComp('emc-subpackage-accept-list-task-tab');
                infoTab.showPanel('emc-subpackage-accept-list-base-info-panel');
                if (subpackageCategory === 'beforeSampling') {
                    infoTab.revertPanel('emc-subpackage-before-sampling-task-panel');
                    infoTab.removePanel('emc-subpackage-accept-list-task-panel');
                    infoTab.removePanel('emc-subpackage-order-task-panel');

                    Gikam.getAsyncComp('emc-subpackage-schemeedit-audit-list-grid').done(function (grid) {
                        grid.refresh({
                            url : IFM_CONTEXT + '/secure/emc/module/bp/project/proj-schemes/queries/raw',
                            requestData : {
                                id_IN : _this.projSchemeIds
                            }
                        });
                    });
                } else if (subpackageCategory === 'orderSubpackage') {
                    infoTab.revertPanel('emc-subpackage-order-task-panel');
                    infoTab.removePanel('emc-subpackage-accept-list-task-panel');
                    infoTab.removePanel('emc-subpackage-before-sampling-task-panel');
                    Gikam.getAsyncComp('emc-subpackage-accept-list-order-grid').done(function (grid) {
                        grid.refresh({
                            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/queries',
                            requestData : {
                                id_IN : _this.orderIds
                            }
                        });
                    });
                } else {
                    infoTab.revertPanel('emc-subpackage-accept-list-task-panel');
                    infoTab.removePanel('emc-subpackage-before-sampling-task-panel');
                    infoTab.removePanel('emc-subpackage-order-task-panel');

                    Gikam.getComp('emc-subpackage-accept-list-task-grid').refresh({
                        url : emcSubpackage.baseUrl + '/tasks/queries',
                        requestData: {
                            subpackageId_SEQ : row.id
                        }
                    });
                }
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-subpackage-accept-list-task-grid' ])
                }
            },
            toolbar : [ {
                type : 'button',
                text : '创建合同',
                icon : 'add',
                onClick : function() {
                    _this.createContract();
                }
            } ]
        }
    },

    /*getTaskTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            id : 'emc-subpackage-accept-list-task-tab',
            panels : [ {
                title : '分包单测试',
                id : 'emc-subpackage-accept-list-task-panel',
                items : [ {
                    type : 'grid',
                    id : 'emc-subpackage-accept-list-task-grid',
                    columns : emcSubpackage.getTaskListColumns(),
                } ]
            } ]
        }
    },*/

    getTaskTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            id : 'emc-subpackage-accept-list-task-tab',
            panels : [ {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.BASE_INFO',
                id : 'emc-subpackage-accept-list-base-info-panel',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '60%',
                        items : [ {
                            type : 'form',
                            columns : 2,
                            service : 'emcSubpackageServiceImpl',
                            id : 'emc-subpackage-accept-list-base-info-form',
                            fields : emcSubpackage.getBaseInfoFormFields(),
                            readonly : true,
                            onUpdated : function() {
                                var grid = Gikam.getComp('emc-subpackage-accept-list-grid');
                                grid.refreshRowById(grid.getActivedRow().id);
                            },
                        } ]
                    },
                } ]

            },{
                title : '分包单项目明细',
                id : 'emc-subpackage-accept-list-task-panel',
                items : [ {
                    type : 'grid',
                    id : 'emc-subpackage-accept-list-task-grid',
                    columns : emcSubpackage.getTaskListColumns(),
                    readonly : true,
                } ]
            },{
                title : '采样前分包项目明细',
                id : 'emc-subpackage-before-sampling-task-panel',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ {
                            type : 'grid',
                            id : 'emc-subpackage-schemeedit-audit-list-grid',
                            columns : emcSubpackage.getSchemeEditListColumns(),
                            readonly : true,
                            onLoadSuccess : function(data) {
                                if (!Gikam.isEmpty(data)) {
                                    this.activeRowByIndex(1);
                                } else {
                                    Gikam.cleanCompData([ 'emc-subpackage-proj-order-accept-list-grid' ]);
                                }
                            },
                            onRowActive : function(index, row) {
                                Gikam.getComp('emc-subpackage-proj-order-accept-list-grid').refresh({
                                    url : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/bp/project/proj-schemes/{id}/proj-orders/queries', {
                                        id : row.id
                                    }),
                                    requestData : {
                                        itemGroupId_IN: _this.itemGroupIds
                                    },
                                    order : {
                                        schemeName : 'asc'
                                    }
                                });
                            },
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'grid',
                            id : 'emc-subpackage-proj-order-accept-list-grid',
                            columns : emcSubpackage.getOrderEditListColumns(),
                            readonly : true,
                        } ]
                    }
                } ]
            },{
                title : '样品分包项目明细',
                id : 'emc-subpackage-order-task-panel',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ {
                            type : 'grid',
                            id : 'emc-subpackage-accept-list-order-grid',
                            columns : emcSubpackage.getOrderColumns(),
                            service : 'emcOrderServiceImpl',
                            page :true,
                            readonly : true,
                            order : {
                                'orderNo' : 'asc',
                            },
                            onLoadSuccess : function(data) {
                                if (!Gikam.isEmpty(data)) {
                                    this.activeRowByIndex(0);
                                } else {
                                    Gikam.cleanCompData([ 'emc-subpackage-accept-list-order-item-group-grid' ]);
                                }
                            },
                            onRowActive : function(index, row) {
                                Gikam.getComp('emc-subpackage-accept-list-order-item-group-grid').refresh({
                                    url : Gikam.printf('/secure/emc/module/bp/bp/orders/{id}/item-groups/queries', {
                                        id : row.id
                                    }),
                                    requestData : {
                                        itemGroupId_IN : _this.itemGroupIds
                                    }
                                });
                            },
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'grid',
                            id : 'emc-subpackage-accept-list-order-item-group-grid',// 样品分析项组
                            columns : emcSubpackage.getOrderItemGroupColumns(),
                            service : 'emcOrderItemGroupServiceImpl',
                            readonly : true,
                        } ]
                    }
                } ]
            },{
                title : '附件',
                items : [ {
                    type : 'uploader',
                    id : 'emc-subpackage-accept-list-uploader',
                    dbTable : 'T_EMC_PROJECT',
                    readonly : true,
                } ]
            } ]
        }
    },

    createContract : function() {
        var selections = Gikam.getComp('emc-subpackage-accept-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let hasCreated = selections.some(item => item.status === '1');
        if (hasCreated) {
            Gikam.alert('所选分包单已经创建过合同');
            return;
        }

        Gikam.confirm('确认操作', '是否确认创建合同？', function() {
            workspace.window.showMask(true);
            Gikam.postText(emcSubpackage.baseUrl + '/action/create-contract', Gikam.getJsonWrapper({}, [ '', selections ])).done(function () {
                Gikam.getComp('emc-subpackage-accept-list-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    // 创建页面
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            west : {
                width : '50%',
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getTaskTab() ]
            }
        });
    },

    // 初始化
    init : function() {
        this.create();
    }
};
