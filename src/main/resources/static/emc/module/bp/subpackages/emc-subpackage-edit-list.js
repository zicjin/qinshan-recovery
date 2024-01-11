emcSubpackage.editPage = {

    // 列表布局参数
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-subpackage-edit-list-grid',
            url : emcSubpackage.baseUrl + '/queries',
            columns : emcSubpackage.getEditListColumns(),
            page : true,
            requestData : {
                subpackageType_SEQ : 'create',
                orgId_SB: workspace.user.ext$.parentorgid,
                status_IN : '0,1,2'
            },
            rowStyleFormatter : function(row) {
                if (row.status === '1') {
                    return 'background-color : #FFFF99';
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-subpackage-edit-list-base-info-form').done(function (form) {
                    form.refresh({
                        url : Gikam.printf(emcSubpackage.baseUrl + '/{id}', {
                            id : row.id
                        }),
                    });
                });
                Gikam.getComp('emc-subpackage-edit-list-uploader').setOptions({
                    bizId : row.id,
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
                let infoTab = Gikam.getComp('emc-subpackage-edit-list-task-tab');
                infoTab.showPanel('emc-subpackage-edit-list-base-info-panel');
                if (subpackageCategory === 'beforeSampling') {
                    infoTab.revertPanel('emc-subpackage-before-sampling-task-panel');
                    infoTab.removePanel('emc-subpackage-edit-list-task-panel');
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
                    infoTab.removePanel('emc-subpackage-edit-list-task-panel');
                    infoTab.removePanel('emc-subpackage-before-sampling-task-panel');
                    Gikam.getAsyncComp('emc-subpackage-edit-list-order-grid').done(function (grid) {
                        grid.refresh({
                            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/queries',
                            requestData : {
                                id_IN : _this.orderIds
                            }
                        });
                    });
                } else {
                    infoTab.revertPanel('emc-subpackage-edit-list-task-panel');
                    infoTab.removePanel('emc-subpackage-before-sampling-task-panel');
                    infoTab.removePanel('emc-subpackage-order-task-panel');

                    Gikam.getComp('emc-subpackage-edit-list-task-grid').refresh({
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
                    Gikam.cleanCompData([ 'emc-subpackage-edit-list-task-grid' ])
                };
            },
            toolbar : [{
                type : 'button',
                text : '提交',
                icon : 'submit',
                onClick : function() {
                    var selections = Gikam.getComp('emc-subpackage-edit-list-grid').getSelections();
                    console.log(selections);
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }

                    let hasAssigned = selections.some(item => item.status === '1');
                    if (hasAssigned) {
                        Gikam.alert('所选分包单已经被委托');
                        return;
                    }

                    let outerSubpackage = selections.some(item => !item.innerSubcontractorId);
                    if (outerSubpackage) {
                        Gikam.alert('外部分包单不用提交');
                        return;
                    }

                    workspace.window.showMask(true);
                    Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/assign', Gikam.getJsonWrapper({}, [ '', selections ])).done(function() {
                        Gikam.getComp('emc-subpackage-edit-list-grid').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                        Gikam.getComp('emc-subpackage-edit-list-grid').refresh();
                    });

                    /*Gikam.create('modal', {
                        title : Gikam.choose.getConfig('org').title,
                        url : Gikam.choose.getConfig('org').url + Gikam.param({
                            orgCategory_SEQ : 'company',
                            single : true
                        }),
                        onAfterClose : function(rows) {
                            if (Gikam.isEmpty(rows)) {
                                return;
                            }

                            workspace.window.showMask(true);
                            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/assign', Gikam.getJsonWrapper({
                                targetOrgId : rows[0].id,
                                targetOrgName : rows[0].orgName
                            }, [ '', selections ])).done(function() {
                                Gikam.getComp('emc-subpackage-edit-list-grid').refresh();
                            }).always(function() {
                                workspace.window.closeMask();
                                Gikam.getComp('emc-subpackage-edit-list-grid').refresh();
                            });
                        }
                    });*/

                }
            },{
                type : 'button',
                text : '完成',
                icon : 'edit',
                onClick : function() {
                    var selections = Gikam.getComp('emc-subpackage-edit-list-grid').getSelections();
                    let hasNoAssigned = selections.some(item => item.status !='1');
                    console.log(hasNoAssigned);
                    if (hasNoAssigned) {
                        Gikam.alert('所选分包单不是委托状态，不能完成！');
                        return;
                    }else{
                        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/complete-package',
                            Gikam.getJsonWrapper({}, [ 'emcSubpackageServiceImpl', selections ],)).done(function() {
                            Gikam.getComp('emc-subpackage-edit-list-grid').refresh();
                        })
                    }
                }
            },{
                type : 'button',
                text : '删除',
                icon : 'remove',
                onClick : function() {
                    //删除未委托的分包单
                    var selections = Gikam.getComp('emc-subpackage-edit-list-grid').getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/undo-subpackage', Gikam.getJsonWrapper({
                    }, [ 'emcSubpackageServiceImpl', selections ])).done(function() {
                        Gikam.getComp('emc-subpackage-edit-list-grid').refresh();
                    });
                }
            },{
                type : 'button',
                text : '撤回',
                icon : 'cancel',
                onClick : function() {
                    //撤回发个其他公司的分包单
                    var selections = Gikam.getComp('emc-subpackage-edit-list-grid').getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    if (selections.length > 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_MAX_ITEM_IS_ONE');
                        return;
                    }
                    console.log(selections);
                    if(selections[0].status != "1"){
                        Gikam.alert('该分包单未委托不需要撤销！');
                    }else{
                        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/cancel-package', Gikam.getJsonWrapper({
                        }, [ 'emcSubpackageServiceImpl', selections ])).done(function(data) {
                            if(data == "NO"){
                                Gikam.alert('撤销失败！分包单已被委托方创建合同！');
                                Gikam.getComp('emc-subpackage-edit-list-grid').refresh();
                            }else{
                                Gikam.alert('撤销成功!');
                                Gikam.getComp('emc-subpackage-edit-list-grid').refresh();
                            }
                        });
                    }

                }
            },{
                type : 'button',
                text : '查看分包单',
                icon : 'select',
                onClick : function() {
                    Gikam.viewReport('subpackageProject', Gikam.getComp('emc-subpackage-edit-list-grid').getSelections());
                }
            } ]
        }
    },

    getTaskTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            id : 'emc-subpackage-edit-list-task-tab',
            panels : [ {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.BASE_INFO',
                id : 'emc-subpackage-edit-list-base-info-panel',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '60%',
                        items : [ {
                            type : 'form',
                            columns : 2,
                            service : 'emcSubpackageServiceImpl',
                            id : 'emc-subpackage-edit-list-base-info-form',
                            fields : emcSubpackage.getBaseInfoFormFields(),
                            onUpdated : function() {
                                var grid = Gikam.getComp('emc-subpackage-edit-list-grid');
                                grid.refreshRowById(grid.getActivedRow().id);
                            },
                        } ]
                    },
                } ]

            },{
                title : '分包单项目明细',
                id : 'emc-subpackage-edit-list-task-panel',
                items : [ {
                    type : 'grid',
                    id : 'emc-subpackage-edit-list-task-grid',
                    columns : emcSubpackage.getTaskListColumns(),
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
                                    Gikam.cleanCompData([ 'emc-subpackage-proj-order-edit-list-grid' ]);
                                }
                            },
                            onRowActive : function(index, row) {
                                Gikam.getComp('emc-subpackage-proj-order-edit-list-grid').refresh({
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
                            id : 'emc-subpackage-proj-order-edit-list-grid',
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
                            id : 'emc-subpackage-edit-list-order-grid',
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
                                    Gikam.cleanCompData([ 'emc-subpackage-edit-list-order-item-group-grid' ]);
                                }
                            },
                            onRowActive : function(index, row) {
                                Gikam.getComp('emc-subpackage-edit-list-order-item-group-grid').refresh({
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
                            id : 'emc-subpackage-edit-list-order-item-group-grid',// 样品分析项组
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
                    id : 'emc-subpackage-edit-list-uploader',
                    dbTable : 'T_EMC_PROJECT',
                } ]
            } ]
        }
    },

    assignSubpackage : function() {
        var selections = Gikam.getComp('emc-subpackage-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let hasAssigned = selections.some(item => item.status === '1');
        if (hasAssigned) {
            Gikam.alert('所选分包单已经被委托');
            return;
        }

        Gikam.create('modal', {
            title : Gikam.choose.getConfig('org').title,
            url : Gikam.choose.getConfig('org').url + Gikam.param({
                orgCategory_SEQ : 'company',
                single : true
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                workspace.window.showMask(true);
                Gikam.postText(emcSubpackage.baseUrl + '/action/assign', Gikam.getJsonWrapper({
                    targetOrgId : rows[0].id,
                    targetOrgName : rows[0].orgName
                }, [ '', selections ])).done(function() {
                    Gikam.getComp('emc-subpackage-edit-list-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
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
