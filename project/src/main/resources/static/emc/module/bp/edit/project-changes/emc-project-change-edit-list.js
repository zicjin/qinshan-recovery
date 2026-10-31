emcProjectChange.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-project-change-edit-list-grid',
            url : emcProjectChange.baseUrl + '/queries/raw',
            columns : emcProjectChange.getEditListColumns(),
            service : 'emcProjectChangeServiceImpl',
            page : true,
            deleteFormatter : function(row) {
                return row.projName || row.id;
            },
            requestData : {
                targetType : 'projEdit',
                org_filter : '1',
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ "emc-project-change-edit-list-base-info-form", "emc-project-change-edit-list-order-grid", "emc-project-change-edit-list-order-item-group-grid", "emc-project-change-edit-list-director-grid", "emc-schemeedit-audit-list-grid", "emc-proj-order-edit-list-grid" ]);
                    Gikam.getComp('emc-project-change-edit-list-base-info-tab').removePanel('emc-project-change-edit-list-order-panel');
                }
            },
            onRowActive : function(index, row) {
                _this.onProjectActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                // hidden : true,
                onClick : function() {
                    _this.addProject();
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
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-project-change-edit-list-grid').deleteRows(emcProjectChange.baseUrl);
                }
            } ]
        }
    },

    addProject : function() {
        Gikam.create('modal', {
            title : '选择项目',
            url : Gikam.choose.getConfig('project').url + Gikam.param({
                org_filter : '1',
                status_IN: 'create,auditFinished',
                single: true
            }),
            onAfterClose : function(rows) {
                console.info(rows)
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                workspace.window.showMask(true);
                Gikam.post(emcProjectChange.baseUrl, Gikam.getJsonWrapper(null, [ '', rows ])).done(function() {
                    Gikam.getComp('emc-project-change-edit-list-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    copeOrder : function() {
        var selections = Gikam.getComp('emc-project-change-edit-list-order-grid').getSelections();
        if (selections.length > 0) {
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.COPY_SHARE',
                width : 300,
                height : 200,
                url : IFM_CONTEXT + '/secure/emc/module/bp/project/proj-schemes/page/copy-share',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        var url = emcProjectChange.baseUrl + '/orders/actions/copy';
                        Gikam.post(url, Gikam.getJsonWrapper({
                            copies : data.copyShare
                        }, [ '', selections ])).always(function() {
                            workspace.window.closeMask();
                            Gikam.getComp('emc-project-change-edit-list-order-grid').refresh();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    copeQcOrder : function() {
        var selections = Gikam.getComp('emc-project-change-edit-list-order-grid').getSelections();
        if (selections.length > 0) {
            var copyEnabled = true;
            selections.some(function(item) {
                if (item.qcCategoryId) {
                    copyEnabled = false;
                    return true;
                }
            });
            if (!copyEnabled) {
                Gikam.alert('EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.COPYQCORDERTIP');
                return;
            }
            Gikam.create('modal', {
                title : Gikam.choose.getConfig('qcCategory').title,
                width : '20%',
                height : '50%',
                url : Gikam.choose.getConfig('qcCategory').url + Gikam.param({
                    single : true,
                    useCategory_SEQ : 'scene'
                }),
                onAfterClose : function(rows) {
                    if (Gikam.isEmpty(rows)) {
                        return;
                    }
                    workspace.window.showMask(true);
                    selections.map(function(item) {
                        item.qcCategoryId = rows[0].id;
                        item.qcCategoryCode = rows[0].qcCategoryId;
                        item.qcName = rows[0].qcName;
                    });

                    Gikam.getComp('emc-project-change-edit-list-order-grid').insert(emcProjectChange.baseUrl + '/orders/actions/copy-qc', selections);
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
        Gikam.getComp('emc-project-change-edit-list-order-grid').refresh();
    },


    updateQcCategory : function() {
        var selections = Gikam.getComp('emc-project-change-edit-list-order-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : Gikam.choose.getConfig('qcCategory').title,
            width : '20%',
            height : '50%',
            url : Gikam.choose.getConfig('qcCategory').url + Gikam.param({
                single : true,
                useCategory_SEQ : 'scene'
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                workspace.window.showMask(true);
                selections.forEach(function(item) {
                    item.qcCategoryId = rows[0].id;
                    item.qcCategoryCode = rows[0].qcCategoryId;
                    item.qcName = rows[0].qcName;
                });
                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/edit-qc', Gikam.getJsonWrapper({}, [ 'emcOrderChangeServiceImpl', selections])).done(function() {
                    Gikam.getComp('emc-project-change-edit-list-order-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    editItem : function() {
        var activedRow = Gikam.getComp('emc-project-change-edit-list-order-grid').getActivedRow();
        var selections = Gikam.getComp('emc-project-change-edit-list-order-item-group-grid').getSelections();
        if (selections.length === 1) {

            var orderItemGroupId = selections[0].id;
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.EDIT_ITEM',
                url : '/secure/emc/module/bp/bp/folders/page/order-item-edit-list' + Gikam.param({
                    itemGroupId : selections[0].itemGroupId,
                    orderItemGroupId : orderItemGroupId
                }),
                width : '53%',
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
    },

    editTest : function() {
        var selections = Gikam.getComp('emc-project-change-edit-list-order-grid').getSelections();
        var orderIds = "";
        selections.forEach(function(item) {
            orderIds += item.id + ',';
        });
        orderIds = orderIds.substring(0, orderIds.length - 1);
        if (selections.length > 0) {
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.TEST_EDIT',
                width : '70%',
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/page/order-item-group-edit-list' + Gikam.param({
                    monitorTmplId : selections[0].monitorTmplId,
                    orderCategory : selections[0].orderCategory,
                    orderIds : orderIds
                }),
                onAfterClose : function(chooseData) {
                    if (!Gikam.isEmpty(chooseData)) {
                        workspace.window.showMask(true);
                        Gikam.put('/secure/emc/module/bp/edit/project-change/orders/action/edit', Gikam.getJsonWrapper(null, [ 'emcOrderChangeServiceImpl', selections ], [ 'emcOrderItemGroupChangeServiceImplImpl', chooseData ])).done(function() {
                            Gikam.getComp('emc-project-change-edit-list-order-item-group-grid').refresh();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    submit : function() {
        var grid = Gikam.getComp('emc-project-change-edit-list-grid');
        var selections = grid.getSelections();

        Gikam.create('workflow').submit({
            data : selections,
            pageObject : emcProjectChange
        }).done(function() {
            grid.refresh();
        });
    },

    onProjectActive : function(row) {
        var _this = this;
        var tab = Gikam.getComp('emc-project-change-edit-list-base-info-tab');
        tab.show();
        if (row) {
            if(_this.param === undefined) {
                _this.param = {};
            }
            _this.param.editMonitorInfo = row.ext$.editmonitorinfo;
            _this.param.sampleOrgId = row.ext$.sampleorgid;
            _this.param.projId = row.id;
            // 使用受检单位来过滤数据
            _this.param.testOrgId = row.testOrgId;

            if (row.sample === "1") {
                tab.removePanel('emc-project-change-edit-list-order-panel');
                tab.showPanel('emc-project-change-edit-list-base-info-panel');
                tab.removePanel('emc-project-sample-subpackage-info-panel');
                tab.revertPanel('emc-project-scheme-edit-panel');
            } else {
                tab.revertPanel('emc-project-change-edit-list-order-panel');
                tab.removePanel('emc-project-scheme-edit-panel');
            }

            Gikam.getAsyncComp('emc-project-change-edit-list-base-info-form').done(function (form) {
                var url = IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/biz-categories/projects/queries/selectable';
                url += Gikam.param({
                    bizCategoryId : row.bizCategoryId,
                    activatedFlag : '1',
                });
                Gikam.getJson(url).done(function(items) {
                    form.setSelectOptions('projName', items.map(function(item) {
                        return {
                            text : item.projName,
                            value : item.projName
                        };
                    }), true);
                });

                form.refresh({
                    url : Gikam.printf(emcProjectChange.baseUrl + '/{id}', {
                        id : row.id
                    }),
                });
            });

            if (row.sample === "1") {
                Gikam.getAsyncComp('emc-schemeedit-audit-list-grid').done(function(grid) {
                    grid.setOptions({
                        requestData : {
                            projId_SEQ : row.id
                        }
                    });
                    grid.refreshColumns(_this.param.editMonitorInfo === '1' ? emcProjectChange.getSchemeEditListColumns1() : emcProjectChange.getSchemeEditListColumns());
                    grid.refresh({
                        url : emcProjectChange.schemeUrl + '/queries/raw',
                        order : {
                            'MONITORTMPLID' : 'asc',
                            'FOLDERNO' : 'asc'
                        }
                    });
                });
                Gikam.getAsyncComp('emc-project-change-edit-list-sample-subpackage-info-grid').done(function(grid) {
                    grid.setOptions({
                        requestData : {
                            projId_SEQ : row.id,
                            subpackageType_SEQ : 'create',
                            org_filter : '1',
                        }
                    });
                    grid.refreshColumns(emcProjectChange.getProjectSubpackageListColumns());
                    grid.refresh({
                        url : IFM_CONTEXT + "/secure/emc/module/bp/subpackages/queries",
                    });
                });
            } else {
                Gikam.getComp('emc-project-change-edit-list-order-grid').refresh({
                    url : Gikam.printf(emcProjectChange.baseUrl + '/{id}/orders/queries', {
                        id : row.id
                    })
                });
                Gikam.getAsyncComp('emc-project-change-edit-list-subpackage-info-grid').done(function(grid) {
                    grid.setOptions({
                        requestData : {
                            projId_SEQ : row.id,
                            subpackageType_SEQ : 'create',
                            org_filter : '1',
                        }
                    });
                    grid.refreshColumns(emcProjectChange.getProjectSubpackageListColumns());
                    grid.refresh({
                        url : IFM_CONTEXT + "/secure/emc/module/bp/subpackages/queries",
                    });
                });
            }


            Gikam.getComp('emc-project-change-edit-list-uploader').setOptions({
                bizId : row.id,
            });

            // 查询合同附件
            Gikam.getAsyncComp('emc-project-change-edit-list-contract-uploader').done(function (uploader) {
                Gikam.post(IFM_CONTEXT + '/secure/emc/module/mdm/crm/contracts/queries', Gikam.getJsonWrapper({
                    f : {
                        contractNo_SEQ : row.ext$.contractno
                    }
                }, [])).done(function (data) {
                    if (!Gikam.isEmpty(data.rows)) {
                        let targetIds = ['T_CRM_OPPORTUNITY_CONTRACT$' + data.rows[0].id, 'T_CRM_OPPORTUNITY_MAIN$' + data.rows[0].opportunityId];
                        uploader.grid.refresh({
                            requestData : {
                                targetId : '',
                                targetId_IN : targetIds.join()
                            },
                            url : uploader.options.searchUrl
                        })
                    }
                });
            });

            _this._monitorPointType = '';
            _this._sampleOrgId = '';
            _this._sampleOrgName = '';
            _this._selectId.splice(0, _this._selectId.length);

        }
    },

    onOrderActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-project-change-edit-list-order-item-group-grid').refresh({
                url : Gikam.printf('/secure/emc/module/bp/edit/project-change/orders/{id}/item-groups/queries', {
                    id : row.id
                })
            });
        }
    },

    loadSchemeItemGroupPage : function(schemeName) {
        var activeRow = Gikam.getComp('emc-project-change-edit-list-grid').getActivedRow();
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.ORDER_ADD',
            width : '70%',
            url : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/monitor-tmpls/page/scheme-item-group-choose-list',
            onAfterClose : function(rows) {
                if (rows && rows.length > 0) {
                    workspace.window.showMask(true);
                    Gikam.postText(Gikam.printf(emcProjectChange.baseUrl + '/{id}/orders', {
                        id : activeRow.id
                    }), Gikam.getJsonWrapper(null, [ 'emcOrderChangeServiceImpl', [ {
                        monitorTmplId : rows[0].monitorTmplId,
                        orderCategory : rows[0].monitorTmplName
                    } ] ], [ 'emcOrderItemGroupChangeServiceImpl', rows ])).done(function(id) {
                        Gikam.getComp('emc-project-change-edit-list-order-grid').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });

                }

            }
        });
    },

    batchUpdateOrder : function() {
        var selections = Gikam.getComp('emc-project-change-edit-list-order-grid').getSelections();
        if (selections.length > 0) {
            var activedRow = Gikam.getComp('emc-project-change-edit-list-grid').getActivedRow();
            Gikam.create('modal', {
                width : 400,
                height : '70%',
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.BATCH_UPDATE_ORDER',
                url : emcProjectChange.projectUrl + '/page/batch-update-order',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        selections.map(function(item) {
                            item.sendDate = data.sendDate;
                            item.sendTime = data.sendTime;
                            item.orderDesc = data.orderDesc;
                            item.remark = data.remark;
                            item.volume = data.ext$.volume;
                            item.packing = data.ext$.packing;
                            item.ext$.sender = data.ext$.sender;
                            item.ext$.checker = data.ext$.checker;
                            item.labelNo = data.labelNo;
                            item.clientOrderName = data.clientOrderName;
                            item.ext$.isblank = data.ext$.isblank;

                        });

                        Gikam.getComp('emc-project-change-edit-list-base-info-form').setData({
                            sendUsername : data.ext$.sender,
                            sendDate : data.sendDate,
                            ext$ : {
                                senduserid : data.senderId
                            }
                        });

                        Gikam.put(emcProjectChange.baseUrl + '/' + activedRow.id, Gikam.getJsonWrapper({}, [ 'emcOrderChangeServiceImpl', selections ])).done(function() {
                            Gikam.getComp('emc-project-change-edit-list-order-grid').refresh();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    updateMethod : function() {
        var selections = Gikam.getComp('emc-project-change-edit-list-order-item-group-grid').getSelections();

        if (selections.length === 1) {
            var activedRow = Gikam.getComp('emc-project-change-edit-list-order-grid').getActivedRow();
            Gikam.create('modal', {
                title : Gikam.choose.getConfig('testMethod').title,
                url : Gikam.choose.getConfig('monitorSchemeMethod').url + Gikam.param({
                    single : true,
                    monitorTmplId_SEQ : activedRow.monitorTmplId,
                    schemeName_SEQ : selections[0].schemeName,
                    itemGroup_SEQ : selections[0].itemGroup,
                    methodId_DIFFER : selections[0].methodId
                }),
                width : '30%',
                onAfterClose : function(rows) {
                    if (!Gikam.isEmpty(rows)) {
                        workspace.window.showMask(true);
                        selections.map(function(item) {
                            item.schemeName = rows[0].schemeName;
                            item.testName = rows[0].ext$.testname;
                            item.testId = rows[0].testId;
                            item.testMethodId = rows[0].testMethodId;
                            item.methodId = rows[0].methodId;
                            item.methodName = rows[0].ext$.methodname;
                            item.methodVersionId = rows[0].ext$.methodversionid;
                            item.testMethodBasis = rows[0].ext$.testmethodbasis;
                            item.analysisOrgId = rows[0].ext$.analysisorgid;
                            item.analysisOrgName = rows[0].ext$.analysisorgname;
                            item.inputUserId = rows[0].ext$.inputuserid;
                            item.inputUserName = rows[0].ext$.inputusername;
                        });
                        Gikam.put('/secure/emc/module/bp/bp/orders/item-groups/' + selections[0].id + '/action/edit-method', Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupChangeServiceImplImpl', selections ])).done(function() {
                            Gikam.getComp('emc-project-change-edit-list-order-item-group-grid').refresh();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
    },

    updateContainer : function() {
        var selections = Gikam.getComp('emc-project-change-edit-list-order-item-group-grid').getSelections();
        if (selections.length > 0) {
            var activedRow = Gikam.getComp('emc-project-change-edit-list-grid').getActivedRow();
            Gikam.create('modal', {
                title : Gikam.choose.getConfig('containerCategory').title,
                url : Gikam.choose.getConfig('containerCategory').url + Gikam.param({
                    single : true,
                }),
                width : '55%',
                onAfterClose : function(rows) {
                    if (!Gikam.isEmpty(rows)) {
                        workspace.window.showMask(true);
                        selections.map(function(item) {
                            item.containerCategoryId = rows[0].id;
                            item.containerCategoryCode = rows[0].containerCategoryId;
                            item.containerCategoryName = rows[0].containerCategoryName;
                        });
                        Gikam.put(emcProjectChange.baseUrl + '/' + activedRow.id, Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupChangeServiceImplImpl', selections ])).done(function() {
                            Gikam.getComp('emc-project-change-edit-list-order-item-group-grid').refresh();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    updateContainerGroup : function() {
        var selections = Gikam.getComp('emc-project-change-edit-list-order-item-group-grid').getSelections();
        if (selections.length > 0) {
            var activedRow = Gikam.getComp('emc-project-change-edit-list-grid').getActivedRow();
            Gikam.create('modal', {
                width : 400,
                height : 250,
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.UPDATE_CONTAINER_GROUP',
                url : emcProjectChange.baseUrl + '/page/update-order-container-group',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        selections.map(function(item) {
                            item.containerGroup = data.containerGroup;
                            item.fixative = data.fixative;
                            item.storeCondition = data.storeCondition;
                        });
                        Gikam.put(emcProjectChange.baseUrl + '/' + activedRow.id, Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupChangeServiceImplImpl', selections ])).done(function() {
                            Gikam.getComp('emc-project-change-edit-list-order-item-group-grid').refresh();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            id : 'emc-project-change-edit-list-base-info-tab',
            panels : [ {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.BASE_INFO',
                id : 'emc-project-change-edit-list-base-info-panel',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '60%',
                        items : [ {
                            type : 'form',
                            columns : 3,
                            service : 'emcProjectChangeServiceImpl',
                            id : 'emc-project-change-edit-list-base-info-form',
                            panels : [
                                {title : '合同信息',fields : emcProjectChange.getBaseInfoFormFields11()},
                                {title : '委托单位信息',fields : emcProjectChange.getBaseInfoFormFields12()},
                                {title : '受检单位',fields : emcProjectChange.getBaseInfoFormFields13()},
                                {title : '项目信息',fields : emcProjectChange.getBaseInfoFormFields14()},
                            ],
                            onUpdated : function() {
                                var grid = Gikam.getComp('emc-project-change-edit-list-grid');
                                grid.refreshRowById(grid.getActivedRow().id);
                            },
                            onLoadSuccess : function(data) {
                                Gikam.refreshGrids({
                                    'emc-project-change-edit-list-director-grid' : emcProjectChange.baseUrl + '/{id}/directors/queries',
                                }, {
                                    id : data.id
                                });
                            }
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            id : 'emc-project-change-edit-list-director-tab',
                            fill : true,
                            width : Gikam.width.XL,
                            panels : [ {
                                id : 'emc-project-change-edit-list-director-panel',
                                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TITLE.PROJECT_DIRECTOR',
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-project-change-edit-list-director-grid',
                                    filterOpen : false,
                                    service : 'emcProjectDirectorServiceImpl',
                                    page : false,
                                    deleteFormatter : function(row) {
                                        return row.ext$.username || row.empId || row.id;
                                    },
                                    toolbar : [ {
                                        type : 'button',
                                        text : 'GIKAM.BUTTON.INSERT',
                                        icon : 'add-row',
                                        onClick : function() {
                                            _this.insertDirectorRow();
                                        }
                                    }, {
                                        type : 'button',
                                        text : 'GIKAM.BUTTON.DELETE',
                                        icon : 'remove-row',
                                        onClick : function() {
                                            _this.deleteDirectorRow();
                                        }
                                    } ],
                                    columns : emcProjectChange.getProjectDirectorListColumns()
                                } ]
                            } ]

                        } ]
                    }
                } ]

            }, {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.ORDER',
                id : 'emc-project-change-edit-list-order-panel',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ {
                            type : 'grid',
                            id : 'emc-project-change-edit-list-order-grid',
                            columns : emcProjectChange.getOrderColumns(),
                            service : 'emcOrderChangeServiceImpl',
                            page :true,
                            deleteFormatter : function(row) {
                                return row.orderNo || row.id;
                            },
                            order : {
                                'orderNo' : 'asc',
                            },
                            onBeforeUpdate  : function(row, keys) {
                                if (row && row.sendDate) {
                                    let sendDate = new Date(row.sendDate);
                                    let now = new Date();
                                    if (sendDate.getTime() > now.getTime()) {
                                        Gikam.alert('EMC.MODULE.BP.PROJECT.PROJECTS.TIP.SEND_DATE_CAN_NOT_OVER_NOW');
                                        return false;
                                    }
                                }
                            },
                            onLoadSuccess : function(data) {
                                if (!Gikam.isEmpty(data)) {
                                    this.activeRowByIndex(0);
                                } else {
                                    Gikam.cleanCompData([ 'emc-project-change-edit-list-order-item-group-grid' ]);
                                }
                            },
                            onRendered : function() {
                                this.hideColumns([ 'qcCategoryCode' ]);
                            },
                            onRowActive : function(index, row) {
                                _this.onOrderActive(row);
                            },
                            toolbar : [ {
                                type : 'button',
                                text : 'GIKAM.BUTTON.INSERT',
                                icon : 'add',
                                onClick : function() {
                                    _this.loadSchemeItemGroupPage();
                                }
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.DELETE',
                                icon : 'remove-row',
                                onClick : function() {
                                    Gikam.getComp('emc-project-change-edit-list-order-grid').deleteRows(Gikam.printf(emcProjectChange.baseUrl + '/{id}/orders', {
                                        id : Gikam.getComp('emc-project-change-edit-list-grid').getActivedRow().id
                                    }));
                                }
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.PREVIEW',
                                icon : 'print',
                                onClick : function() {
                                    Gikam.viewReport('deliveringLabel', Gikam.getComp('emc-project-change-edit-list-order-grid').getSelections());
                                }
                            }, {
                                type : 'dropDownMenu',
                                text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.ORDER_QUICK_INPUT',
                                items : [ {
                                    type : 'button',
                                    text : 'GIKAM.BUTTON.EXPORT',
                                    icon : 'move-up',
                                    onClick : function() {
                                        _this.orderQuickExport();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'GIKAM.BUTTON.IMPORT',
                                    icon : 'move-down',
                                    onClick : function() {
                                        _this.orderQuickImport();
                                    }
                                } ]
                            }, {
                                type : 'dropDownMenu',
                                text : 'GIKAM.BUTTON.MORE',
                                items : [ {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.BATCH_UPDATE',
                                    icon : 'add',
                                    onClick : function() {
                                        _this.batchUpdateOrder();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.COPE_NORMAL',
                                    icon : 'copy',
                                    onClick : function() {
                                        _this.copeOrder();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.COPE_QC',
                                    icon : 'copy',
                                    onClick : function() {
                                        _this.copeQcOrder();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.UPDATE_QC_CATEGORY',
                                    icon : 'copy',
                                    onClick : function() {
                                        _this.updateQcCategory();
                                    }
                                } ]
                            } ]
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'grid',
                            id : 'emc-project-change-edit-list-order-item-group-grid',// 样品分析项组
                            columns : emcProjectChange.getOrderItemGroupColumns(),
                            service : 'emcOrderItemGroupChangeServiceImplImpl',
                            toolbar : [ {
                                type : 'dropDownMenu',
                                text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.EDIT',
                                items : [ {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.EDIT_TEST',
                                    icon : 'add',
                                    onClick : function() {
                                        _this.editTest();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.UPDATE_METHOD',
                                    icon : 'add',
                                    onClick : function() {
                                        _this.updateMethod();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.EDIT_ITEM',
                                    icon : 'add',
                                    onClick : function() {
                                        _this.editItem();
                                    }
                                } ]
                            }, {
                                type : 'dropDownMenu',
                                text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.CONTAINER',
                                items : [ {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.UPDATE_CONTAINER',
                                    icon : 'add',
                                    onClick : function() {
                                        _this.updateContainer();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.UPDATE_CONTAINER_GROUP',
                                    icon : 'add',
                                    onClick : function() {
                                        _this.updateContainerGroup();
                                    }
                                } ]
                            } ]
                        } ]
                    }
                } ]
            }, _this.getMonitorSchemePanel(), {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-project-change-edit-list-uploader',
                    dbTable : 'T_EMC_PROJECT',
                } ]
            }, {
                title : '合同附件',
                items : [ {
                    type : 'uploader',
                    id : 'emc-project-change-edit-list-contract-uploader',
                    dbTable : 'T_CRM_OPPORTUNITY_CONTRACT',
                    readonly : true,
                } ]
            } ]
        }
    },

    /** 监测方案 */
    getMonitorSchemePanel : function () {
        var _this = this;
        return {
            title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.TAB.PROJ_SCHEME',
            id : 'emc-project-scheme-edit-panel',
            items : [ {
                type : 'layout',
                north : {
                    height : '50%',
                    items : [ {
                        type : 'grid',
                        id : 'emc-schemeedit-audit-list-grid',
                        url : '',
                        columns : emcProjectChange.getSchemeEditListColumns(),
                        service : 'emcProjSchemeChangeServiceImpl',
                        checkOnActive : false,
                        deleteFormatter : function(row) {
                            return row.folderNo || row.id;
                        },
                        onLoadSuccess : function(data) {
                            if (!Gikam.isEmpty(data)) {
                                this.activeRowByIndex(1);
                            } else {
                                Gikam.cleanCompData([ 'emc-proj-order-edit-list-grid' ]);
                            }
                        },
                        onRowActive : function(index, row) {
                            _this.onSchemeActive(row);
                        },
                        onRendered : function() {
                            this.hideColumns([ 'ext$.monitortmplname' ]);
                        },
                        group : {
                            fields : [ 'ext$.monitortmplname' ],
                            formatter : function(row) {
                                return Gikam.propI18N('T_EMC_PROJ_SCHEME.ORDERCATEGORY') + ':' + row.ext$.monitortmplname;
                            }
                        },
                        toolbar : [ {
                            type : 'button',
                            text : 'GIKAM.BUTTON.INSERT',
                            icon : 'add',
                            onClick : function() {
                                var projId = _this.param.projId;
                                var activeRow = Gikam.getComp('emc-project-change-edit-list-grid').getActivedRow();
                                if (Gikam.isEmpty(activeRow)) {
                                    Gikam.alert("EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.PROJECT_REQUIRED");
                                    return;
                                }

                                var data = Gikam.getComp('emc-project-change-edit-list-base-info-form').getData();
                                var editMonitorInfo = _this.param.editMonitorInfo || '1';
                                // 使用受检单位来过滤数据
                                var testOrgId = data.testOrgId ? data.testOrgId : '-';
                                var sampleOrgId = _this.param.sampleOrgId;
                                _this.addProj(projId, editMonitorInfo, testOrgId, sampleOrgId);
                            }
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.INITIALIZ',
                            icon : 'add',
                            hidden : true,
                            onClick : function() {
                                var projId = _this.param.projId;
                                if (Gikam.isEmpty(projId)) {
                                    Gikam.alert("EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.PROJECT_REQUIRED");
                                    return;
                                }

                                // 使用受检单位来过滤数据
                                var data = Gikam.getComp('emc-project-change-edit-list-base-info-form').getData();
                                var testOrgId = data.testOrgId;
                                _this.initAddProj(projId, testOrgId);
                            }
                        }, {
                            type : 'button',
                            text : 'GIKAM.BUTTON.DELETE',
                            icon : 'remove-row',
                            onClick : function() {
                                Gikam.getComp('emc-schemeedit-audit-list-grid').deleteRows(emcProjectChange.schemeUrl);
                            }
                        }, {
                            type : 'dropDownMenu',
                            text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.COPY_SHARE_OR_POINT',
                            items : [ {
                                type : 'button',
                                text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.COPY_SHARE',
                                icon : 'copy',
                                onClick : function() {
                                    _this.copyShare();
                                }
                            }, {
                                type : 'button',
                                text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.COPY_POINT',
                                icon : 'copy',
                                hidden:true,
                                onClick : function() {
                                    var data = Gikam.getComp('emc-project-change-edit-list-base-info-form').getData();
                                    var testOrgId = data.entrustOrgId;
                                    _this.copyPoint(testOrgId);
                                }
                            }, ]
                        }, {
                            type : 'dropDownMenu',
                            text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.EDIT',
                            items : [ {
                                type : 'button',
                                text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.UPDATE_CYCLEFREQUENCY',
                                icon : 'edit',
                                onClick : function() {
                                    _this.updateCycleAndFrequency();
                                }
                            }, {
                                type : 'button',
                                text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.INDEX',
                                icon : 'edit',
                                onClick : function() {
                                    _this.updateIndex();
                                }
                            }, {
                                type : 'button',
                                text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.UPDATE_ORG',
                                icon : 'edit',
                                onClick : function() {
                                    _this.updateOrg();
                                }
                            } ]
                        }, {
                            type : 'dropDownMenu',
                            text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.ORDER_QUICK_INPUT',
                            items : [ {
                                type : 'button',
                                text : 'GIKAM.BUTTON.EXPORT',
                                icon : 'move-up',
                                onClick : function() {
                                    _this.schemeQuickExport();
                                }
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.IMPORT',
                                icon : 'move-down',
                                onClick : function() {
                                    _this.schemeQuickImport();
                                }
                            } ]
                        } ]
                    } ]
                },
                center : {
                    items : [ {
                        type : 'grid',
                        id : 'emc-proj-order-edit-list-grid',
                        url : _this.getOrderUrl(),
                        columns : emcProjectChange.getOrderEditListColumns(),
                        service : 'emcProjOrderChangeServiceImpl',
                        group : {
                            fields : [ "schemeName"],
                            formatter : function(row) {
                                return row.schemeName;
                            }
                        },
                        toolbar : [ {
                            type : 'button',
                            text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.UPDATE_TEST',
                            icon : 'edit',
                            onClick : function() {
                                _this.updateItemGroup();
                            }
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.BATCH_UPDATE_NUM',
                            icon : 'edit',
                            onClick : function() {
                                _this.batchUpdateSampleNum();
                            }
                        } ]
                    } ]
                }
            } ]
        }
    },

    onSchemeActive : function(row) {
        var _this = this;
        if (Gikam.isNumber(row.id)) {
            Gikam.getAsyncComp('emc-proj-order-edit-list-grid').done(function(grid) {
                grid.refresh({
                    url : _this.getOrderUrl(row.id),
                    order : {
                        schemeName : 'asc'
                    }
                });
            });
        }
    },

    getOrderUrl : function(projSchemeId) {
        if (projSchemeId) {
            return Gikam.printf(emcProjectChange.schemeUrl + '/{id}/proj-orders/queries', {
                id : projSchemeId
            })
        }
    },

    updateCycleAndFrequency : function() {
        var gridData = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
        if (gridData.length > 0) {
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.UPDATE_CYCLE_FREQUENCY',
                width : 400,
                height : 200,
                url : emcProjectChange.schemeUrl + '/page/update-cycle-and-frequency',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        var selectedIds = gridData.map(function(item) {
                            return {
                                id : item.id
                            }
                        });
                        Gikam.put(emcProjectChange.schemeUrl + '/action/update-frequency', Gikam.getJsonWrapper({
                            cycle : data.cycle,
                            frequency : data.frequency
                        }, [ 'emcProjSchemeChangeServiceImpl', gridData ])).done(function() {
                            Gikam.getComp('emc-schemeedit-audit-list-grid').refresh();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    batchUpdateSampleNum : function() {
        var gridData = Gikam.getComp('emc-proj-order-edit-list-grid').getSelections();
        var activeRow = Gikam.getComp('emc-schemeedit-audit-list-grid').getActivedRow();
        if (gridData.length > 0) {
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.UPDATE_SAMPLENUM',
                width : 400,
                height : 200,
                url : IFM_CONTEXT + '/secure/emc/module/bp/project/proj-schemes/page/proj-order-batch-update',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        gridData.map(function(item) {
                            item.sampleNum = data.sampleNum;
                        });
                        Gikam.put(emcProjectChange.schemeUrl + '/' + activeRow.id, Gikam.getJsonWrapper({}, [ 'emcProjOrderChangeServiceImpl', gridData ])).done(function() {
                            Gikam.getComp('emc-proj-order-edit-list-grid').refresh();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    updateItemGroup : function() {
        var gridData = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
        if (gridData.length > 0) {
            var array = new Array();
            for(j = 0,len = gridData.length; j < len; j++) {
                var ext1 = gridData[j].ext$;
                var monitortmplname = ext1.monitortmplname;
                var object1 =new Object();
                object1.monitortmplname = monitortmplname;
                array.push(object1);
            }
            var mnamesame;
            for(i = 0,len=array.length; i < len; i++){
                if(array[0].monitortmplname != array[i].monitortmplname){
                    mnamesame = false;
                    break;
                }
            }
            if(mnamesame  == false){
                Gikam.alert('GIKAM.TIP.MISMATCH_MONITOR_TMPLNAME');
                return;
            }
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.UPDATE_TEST',
                width : 1000,
                height : 580,
                url : IFM_CONTEXT + '/secure/emc/module/bp/project/proj-schemes/page/proj-order-update-item-group' + Gikam.param({
                    monitorTmplId : gridData[0].monitorTmplId,
                    projSchemeId : gridData[0].id,
                    testmethodflag :'1',
                }),
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        var orderData = data.map(function(item) {
                            return {
                                testId : item.testId,
                                methodId : item.methodId,
                                itemGroupId : item.itemGroupId,
                                itemGroup : item.itemGroup,
                                schemeName : item.schemeName
                            }
                        });
                        var url = emcProjectChange.schemeUrl + '/action/proj-orders/update-item-group';
                        Gikam.post(url, Gikam.getJsonWrapper({}, [ 'emcProjSchemeChangeServiceImpl', gridData ], [ 'emcProjOrderChangeServiceImpl', orderData ])).always(function() {
                            Gikam.getComp('emc-proj-order-edit-list-grid').refresh();
                            workspace.window.closeMask();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    updateIndex : function() {
        var gridData = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
        if (gridData.length > 0) {
            var ids = "";
            gridData.map(function(item, i) {
                ids += gridData[i].id + ',';
            });
            ids = ids.substring(0, ids.length - 1);
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.UPDATE_INDEX',
                width : '90%',
                height : '90%',
                url : emcProjectChange.schemeUrl + '/page/update-index' + Gikam.param({
                    ids : ids,
                    monitorTmplGroupId : gridData[0].ext$.monitortmplgroupid
                }),
                onAfterClose : function() {
                    Gikam.getComp('emc-proj-order-edit-list-grid').refresh();
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    updateOrg : function() {
        var gridData = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
        if (gridData.length > 0) {
            Gikam.create('modal', {
                title : Gikam.choose.getConfig('org-simple').title,
                width : 480,
                height : 150,
                url : '/secure/core/module/mdm/orgs/page/select-list',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        gridData.map(function(item) {
                            item.sampleOrgId = data.sampleOrgId;
                        });
                        Gikam.put(emcProjectChange.schemeUrl + '/' + gridData[0].id, Gikam.getJsonWrapper({}, [ 'emcProjSchemeChangeServiceImpl', gridData ])).done(function() {
                            Gikam.getComp('emc-schemeedit-audit-list-grid').refresh();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    copyShare : function() {
        var gridData = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
        if (gridData.length > 0) {
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.COPY_SHARE',
                width : 300,
                height : 200,
                url : emcProjectChange.schemeUrl + '/page/copy-share',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        var url = emcProjectChange.schemeUrl + '/action/copy';
                        Gikam.post(url, Gikam.getJsonWrapper({
                            copies : data.copyShare
                        }, [ '', gridData ])).always(function() {
                            workspace.window.closeMask();
                            Gikam.getComp('emc-schemeedit-audit-list-grid').refresh();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    copyPoint : function(testOrgId) {
        var gridData = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
        if (gridData.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.COPY_POINT',
            width : 800,
            height : 600,
            url : emcProjectChange.schemeUrl + '/page/select-monitoring-point' + Gikam.param({
                testOrgId : testOrgId,
                monitorPointCategoryId : gridData[0].monitorPointCategoryId
            }),
            onAfterClose : function(pointData) {
                if (!Gikam.isEmpty(pointData)) {
                    workspace.window.showMask(true);
                    var chooseSchemeData = pointData.map(function(item) {
                        return {
                            monitorPointCategoryId : item.monitorPointCategoryId,
                            monitorPointCategoryName : item.ext$.templatename,
                            monitorPointId : item.id,
                            monitorPointNo : item.monitorPointNo,
                            monitorPointName : item.monitorPointName
                        }
                    });

                    var url = emcProjectChange.schemeUrl + '/action/copy-point'
                    Gikam.post(url, Gikam.getJsonWrapper({}, [ '', gridData ], [ '', chooseSchemeData ])).always(function() {
                        Gikam.getComp('emc-schemeedit-audit-list-grid').refresh();
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },


    _monitorPointType : '',
    _sampleOrgId : '',
    _sampleOrgName : '',
    _selectId : [],
    addProj : function(projId, editMonitorInfo, testOrgId, sampleOrgId) {
        var _this = this;
        var url = emcProjectChange.schemeUrl;
        if (editMonitorInfo === '1') {// 不选择监测点
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.SELECT_MONITOR_TMPL',
                width : '80%',
                height : '80%',
                url : IFM_CONTEXT + '/secure/emc/module/bp/project/proj-schemes/page/select-monitor-tmpl' + Gikam.param({
                    editMonitorInfo : editMonitorInfo
                }),
                onAfterClose : function(tmlData) {
                    if (!Gikam.isEmpty(tmlData)) {
                        var chooseSchemeData = {
                            projId : projId,
                            monitorTmplId : tmlData[0].monitorTmplId,
                            sampleOrgId : sampleOrgId
                        }

                        workspace.window.showMask(true);
                        Gikam.post(url, Gikam.getJsonWrapper({}, [ 'emcProjSchemeChangeServiceImpl', [ chooseSchemeData ] ], [ 'emcProjOrderChangeServiceImpl', tmlData ])).done(function() {
                            Gikam.getComp('emc-schemeedit-audit-list-grid').refresh();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                }
            });
        } else {
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.SELECT_MONITORING_POINT',
                width : '60%',
                height : '70%',
                url : IFM_CONTEXT + '/secure/emc/module/bp/project/proj-schemes/page/select-monitoring-point' + Gikam.param({
                    testOrgId : testOrgId,
                    monitorPointType : _this._monitorPointType,
                    sampleOrgId : _this._sampleOrgId,
                    sampleOrgName : _this._sampleOrgName,
                    selectIds : _this._selectId,
                    defaultOrgId : sampleOrgId
                }),
                onAfterClose : function(pointData) {
                    if (!Gikam.isEmpty(pointData)) {
                        _this._selectId.splice(0, _this._selectId.length);
                        pointData.map(function(item) {
                            _this._selectId.push(item.id);
                        });
                        _this._monitorPointType = pointData[0].monitorPointType;
                        _this._sampleOrgId = pointData[0].sampleDepartmentId;
                        _this._sampleOrgName = pointData[0].sampleDepartment;
                        Gikam.create('modal', {
                            title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.SELECT_MONITOR_TMPL',
                            width : '80%',
                            height : '80%',
                            url : emcProjectChange.schemeUrl + '/page/select-monitor-tmpl' + Gikam.param({
                                editMonitorInfo : editMonitorInfo,
                                monitorPointCategoryId : pointData[0].monitorPointCategoryId
                            }),
                            onAfterClose : function(tmlData) {
                                if (!Gikam.isEmpty(tmlData)) {
                                    if (tmlData == 'PREVIOUS') {
                                        emcProjectChange.editPage.addProj(projId, editMonitorInfo, testOrgId);
                                    } else {
                                        var chooseSchemeData = pointData.map(function(item) {
                                            return {
                                                projId : projId,
                                                monitorPointCategoryId : item.monitorPointCategoryId,
                                                monitorPointCategoryName : item.ext$.templatename,
                                                monitorPointId : item.id,
                                                monitorPointNo : item.monitorPointNo,
                                                monitorPointName : item.monitorPointName,
                                                monitorTmplId : tmlData[0].monitorTmplId,
                                                sampleOrgId : item.sampleDepartmentId
                                            }
                                        });

                                        var chooseOrderData = tmlData.map(function(item) {
                                            return {
                                                testId : item.testId,
                                                methodId : item.methodId,
                                                itemGroupId : item.itemGroupId,
                                                itemGroup : item.itemGroup,
                                                schemeName : item.schemeName
                                            }
                                        });
                                        workspace.window.showMask(true);
                                        Gikam.post(url, Gikam.getJsonWrapper({}, [ 'emcProjSchemeChangeServiceImpl', chooseSchemeData ], [ 'emcProjOrderChangeServiceImpl', chooseOrderData ])).done(function() {
                                            Gikam.getComp('emc-schemeedit-audit-list-grid').refresh();
                                            _this._monitorPointType = '';
                                            _this._sampleOrgId = '';
                                            _this._sampleOrgName = '';
                                            _this._selectId.splice(0, _this._selectId.length);
                                        }).always(function() {
                                            workspace.window.closeMask();
                                        });
                                    }
                                } else {
                                    _this._monitorPointType = '';
                                    _this._sampleOrgId = '';
                                    _this._sampleOrgName = '';
                                    _this._selectId.splice(0, _this._selectId.length);
                                }
                            }
                        });
                    }
                }
            });
        }
    },

    initAddProj : function(projId, testOrgId) {
        if (Gikam.isEmpty(testOrgId)) {
            Gikam.alert("EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.PROJECT_TESTORGID");
            return;
        }

        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.INITIALIZ_SCHEME',
            width : 1500,
            height : 600,
            url : emcProjectChange.schemeUrl + '/page/init-scheme' + Gikam.param({
                projId : projId,
                testOrgId : testOrgId
            }),
            onAfterClose : function(data) {
                Gikam.getComp('emc-schemeedit-audit-list-grid').refresh();
            }
        });
    },

    orderQuickExport : function() {
        var orderData = Gikam.getComp('emc-project-change-edit-list-order-grid').getData();
        if (Gikam.isEmpty(orderData)) {
            Gikam.alert('EMC.MODULE.BP.PROJECT.PROJECTS.ORDER_CAN_NOT_EXPORT');
            return;
        }
        var projectId = orderData[0].projId;
        Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}/{id}', {
            templateCode : 'pfOrderExportTemplate',
            service : 'pfOrderExportTemplate',
            id : projectId
        })).done(function(fileUri) {
            var fileUrl = IFM_CONTEXT + fileUri;
            Gikam.emcDownloadFile(fileUrl);
        });

    },

    orderQuickImport : function() {
        var orderData = Gikam.getComp('emc-project-change-edit-list-order-grid').getData();
        if (Gikam.isEmpty(orderData)) {
            Gikam.alert('EMC.MODULE.BP.PROJECT.PROJECTS.TIP.ORDER_CAN_NOT_IMPORT');
            return;
        }
        var id = orderData[0].projId;

        Gikam.create('simpleUploader', {
            dbTable : 'T_EMC_ORDER',
            bizId : id,
            multiple : false,
            onAfterClose : function(files) {
                if (Gikam.isEmpty(files)) {
                    return;
                }
                Gikam.window.showMask(true);

                Gikam.post(
                    Gikam.printf(IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'pfOrderImportFileFunction', {
                        id: files[0].id
                    })
                ).done(function(result) {
                    if (result.url) {
                        Gikam.emcDownloadFile(result.url);
                    } else if (result.message) {
                        Gikam.alert(result.message);
                    }
                    Gikam.getComp('emc-project-change-edit-list-order-grid').refresh();
                }).always(function() {
                    Gikam.window.closeMask();
                });
            }
        });
    },

    schemeQuickExport : function() {
        var orderData = Gikam.getComp('emc-schemeedit-audit-list-grid').getData();
        if (Gikam.isEmpty(orderData)) {
            Gikam.alert('监测方案无数据，无法导出');
            return;
        }
        var projectId = orderData[0].projId;
        Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}/{id}', {
            templateCode : 'pfProjSchemeExportTemplate',
            service : 'pfProjSchemeExportTemplate',
            id : projectId
        })).done(function(fileUri) {
            var fileUrl = IFM_CONTEXT + fileUri;
            Gikam.emcDownloadFile(fileUrl);
        });

    },

    schemeQuickImport : function() {
        var orderData = Gikam.getComp('emc-schemeedit-audit-list-grid').getData();
        if (Gikam.isEmpty(orderData)) {
            Gikam.alert('EMC.MODULE.BP.PROJECT.PROJECTS.TIP.ORDER_CAN_NOT_IMPORT');
            return;
        }
        var id = orderData[0].projId;

        Gikam.create('simpleUploader', {
            dbTable : 'T_EMC_PROJ_SCHEME',
            bizId : id,
            multiple : false,
            onAfterClose : function(files) {
                if (Gikam.isEmpty(files)) {
                    return;
                }
                Gikam.window.showMask(true);

                Gikam.post(
                    Gikam.printf(IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'pfProjSchemeImportFileFunction', {
                        id: files[0].id
                    })
                ).done(function() {
                    Gikam.getComp('emc-schemeedit-audit-list-grid').refresh();
                }).always(function() {
                    Gikam.window.closeMask();
                });
            }
        });
    },

    insertDirectorRow : function() {
        var formData = Gikam.getComp('emc-project-change-edit-list-base-info-form').getData();
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('emp').title,
            url : Gikam.choose.getConfig('emp').url + Gikam.param({
                targetType : 'insertDirector',
                projectId : formData.id,
                org_filter : '1',
            }),
            width : 310,
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        empId : item.id,
                        projectId : formData.id
                    }
                });
                Gikam.getComp('emc-project-change-edit-list-director-grid').insert(emcProjectChange.baseUrl + '/directors', chooseData);
            }
        });
    },

    deleteDirectorRow : function() {
        var url = emcProjectChange.baseUrl + '/directors';
        Gikam.getComp('emc-project-change-edit-list-director-grid').deleteRows(url).done(function() {
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-project-change-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '23%',
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getBaseInfoTab() ]
            },
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
