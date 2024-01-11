emcProjectScheme.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-project-scheme-edit-list-grid',
            url : emcProjectScheme.baseUrl + '/queries',
            columns : emcProjectScheme.getEditListColumns(),
            service : 'emcProjectServiceImpl',
            page : true,
            filterOpen : true,
            deleteFormatter : function(row) {
                return row.projName || row.id;
            },
            requestData : {
                targetType : 'projEdit',
                orgId_SB: workspace.user.ext$.parentorgid,
                status_IN : 'auditFinished,reportEdit',
            },
            onBeforeRefresh : function (requestData) {
                var orgName = workspace.user.orgName;
                if(orgName.indexOf("质量部") >-1){
                    requestData['projNo_SE'] = 'Z';
                }else{
                    requestData['projNo_DIFFER'] = 'Z';
                }
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ "emc-project-scheme-edit-list-base-info-form", "emc-project-scheme-edit-list-order-grid", "emc-project-scheme-edit-list-order-item-group-grid", "emc-project-scheme-edit-list-director-grid", "emc-schemeedit-audit-list-grid",
                        "emc-proj-edit-subpackage-info-grid","emc-proj-edit-sample-subpackage-edit-grid", "emc-proj-order-edit-list-grid" ]);
                    Gikam.getComp('emc-project-scheme-edit-list-base-info-tab').removePanel('emc-project-scheme-edit-list-order-panel');
                }
            },
            onRowActive : function(index, row) {
                _this.onProjectActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                hidden : true,
                onClick : function() {
                    _this.submit();
                }
            } ]
        }
    },

    copyProject : function() {
        var selections = Gikam.getComp('emc-project-scheme-edit-list-grid').getSelections();
        if (selections.length === 1) {
            var url = Gikam.printf(emcProjectScheme.baseUrl + '/{id}/action/copy', {
                id : selections[0].id
            });
            Gikam.getComp('emc-project-scheme-edit-list-grid').insert(url, selections);
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
    },

    copeOrder : function() {
        var selections = Gikam.getComp('emc-project-scheme-edit-list-order-grid').getSelections();
        if (selections.length > 0) {
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.COPY_SHARE',
                width : 300,
                height : 200,
                url : IFM_CONTEXT + '/secure/emc/module/bp/project/proj-schemes/page/copy-share',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        var url = emcProjectScheme.baseUrl + '/orders/actions/copy';
                        Gikam.post(url, Gikam.getJsonWrapper({
                            copies : data.copyShare,
                            operateType : 'schemeEdit'
                        }, [ '', selections ])).always(function() {
                            workspace.window.closeMask();
                            Gikam.getComp('emc-project-scheme-edit-list-order-grid').refresh();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    copeQcOrder : function() {
        var selections = Gikam.getComp('emc-project-scheme-edit-list-order-grid').getSelections();
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
                        item.operateType = 'schemeEdit';
                    });

                    Gikam.getComp('emc-project-scheme-edit-list-order-grid').insert(emcProjectScheme.baseUrl + '/orders/actions/copy-qc', selections);
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
        Gikam.getComp('emc-project-scheme-edit-list-order-grid').refresh();
    },

    editItem : function() {
        var activedRow = Gikam.getComp('emc-project-scheme-edit-list-order-grid').getActivedRow();
        var selections = Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').getSelections();
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
        var selections = Gikam.getComp('emc-project-scheme-edit-list-order-grid').getSelections();
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
                        Gikam.put('/secure/emc/module/bp/bp/orders/action/edit', Gikam.getJsonWrapper({
                            operateType : 'schemeEdit'
                        }, [ 'emcOrderServiceImpl', selections ], [ 'emcOrderItemGroupServiceImpl', chooseData ])).done(function() {
                            Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').refresh();
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
        var selections = Gikam.getComp('emc-project-scheme-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.confirm('GIKAM.MODAL.SUBMIT', 'EMC.MODULE.BP.PROJECT.PROJECTS.TIP.CONFRIM_TO_SUBMIT', function() {
            workspace.window.showMask(true);
            Gikam.post(emcProjectScheme.baseUrl + '/action/scheme-edit-submit', Gikam.getJsonWrapper({}, [ '', selections ])).always(function() {
                Gikam.getComp('emc-project-scheme-edit-list-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    submitToReport : function() {
        var selections = Gikam.getComp('emc-project-scheme-edit-list-grid').getSelections();
        if (selections.length > 0) {
            Gikam.confirm('GIKAM.MODAL.SUBMIT', 'GIKAM.TIP.SUBMIT', function() {
                workspace.window.showMask(true);
                Gikam.post(emcProjectScheme.baseUrl + '/action/submit-to-report', Gikam.getJsonWrapper({}, [ '', selections ])).always(function() {
                    Gikam.getComp('emc-project-scheme-edit-list-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    onProjectActive : function(row) {
        var _this = this;
        var tab = Gikam.getComp('emc-project-scheme-edit-list-base-info-tab');
        tab.show();
        if (row) {
            if(_this.param === undefined) {
                _this.param = {};
            }
            _this.param.editMonitorInfo = row.ext$.editmonitorinfo;
            _this.param.sampleOrgId = row.ext$.sampleorgid;
            _this.param.projId = row.id;
            // 这里使用委托单位来过滤数据，之前是受检单位
            _this.param.testOrgId = row.entrustOrgId;

            if (row.sample === "1") {
                tab.removePanel('emc-project-scheme-edit-list-order-panel');
                tab.showPanel('emc-project-scheme-edit-list-base-info-panel');
                tab.revertPanel('emc-project-scheme-edit-panel');
                tab.revertPanel('emc-project-scheme-edit-list-sampling-prepare-panel');
                tab.revertPanel('emc-project-subpackage-edit-panel');
                tab.revertPanel('emc-project-sample-subpackage-edit-panel');

            } else {
                tab.revertPanel('emc-project-scheme-edit-list-order-panel');
                tab.removePanel('emc-project-scheme-edit-panel');
                tab.removePanel('emc-project-scheme-edit-list-sampling-prepare-panel');
                tab.revertPanel('emc-project-subpackage-edit-panel');
                tab.removePanel('emc-project-sample-subpackage-edit-panel');

            }

            Gikam.getAsyncComp('emc-project-scheme-edit-list-base-info-form').done(function (form) {
                _this.addOrRemoveValidator(row);

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
                    url : Gikam.printf(emcProjectScheme.baseUrl + '/{id}', {
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
                    grid.refreshColumns(_this.param.editMonitorInfo === '1' ? emcProjectScheme.getSchemeEditListColumns1() : emcProjectScheme.getSchemeEditListColumns());
                    grid.refresh({
                        url : emcProjectScheme.schemeUrl + '/queries/raw',
                        order : {
                            'MONITORTMPLID' : 'asc',
                            'FOLDERNO' : 'asc'
                        }
                    });
                });

                Gikam.getAsyncComp('emc-project-scheme-edit-folder-list-grid').done(function (grid) {
                    grid.refresh({
                        url : '/secure/emc/module/bp/bp/folders/queries',
                        requestData : {
                            projId_SEQ : row.id
                        }
                    })
                })
                Gikam.getAsyncComp('emc-proj-edit-sample-subpackage-edit-grid').done(function(grid) {
                    grid.setOptions({
                        requestData : {
                            projId_SEQ : row.id,
                            subpackageType_SEQ : 'create',
                            orgId_SB: workspace.user.ext$.parentorgid,
                            subpackageCategory_SEQ : 'beforeSampling'
                        }
                    });
                    grid.refreshColumns(emcProjectScheme.getProjectSubpackageListColumns());
                    grid.refresh({
                        url : IFM_CONTEXT + "/secure/emc/module/bp/subpackages/queries",
                    });
                });

                Gikam.getAsyncComp('emc-proj-edit-subpackage-info-grid').done(function(grid) {
                    grid.setOptions({
                        requestData : {
                            projId_SEQ : row.id,
                            subpackageType_SEQ : 'create',
                            orgId_SB: workspace.user.ext$.parentorgid,
                            subpackageCategory_SEQ : 'orderSubpackage'
                        }
                    });
                    grid.refreshColumns(emcProjectScheme.getProjectSubpackageListColumns());
                    grid.refresh({
                        url : IFM_CONTEXT + "/secure/emc/module/bp/subpackages/queries",
                    });
                });
            } else {
                Gikam.getComp('emc-project-scheme-edit-list-order-grid').refresh({
                    url : Gikam.printf(emcProjectScheme.baseUrl + '/{id}/orders/queries', {
                        id : row.id
                    })
                });

                Gikam.getAsyncComp('emc-proj-edit-subpackage-info-grid').done(function(grid) {
                    grid.setOptions({
                        requestData : {
                            projId_SEQ : row.id,
                            subpackageType_SEQ : 'create',
                            orgId_SB: workspace.user.ext$.parentorgid,
                            subpackageCategory_SEQ : 'orderSubpackage'
                        }
                    });
                    grid.refreshColumns(emcProjectScheme.getProjectSubpackageListColumns());
                    grid.refresh({
                        url : IFM_CONTEXT + "/secure/emc/module/bp/subpackages/queries",
                    });
                });

            }

            Gikam.getComp('emc-project-scheme-edit-list-uploader').setOptions({
                bizId : row.id,
            });

            // 查询合同附件
            Gikam.getAsyncComp('emc-project-scheme-edit-list-contract-uploader').done(function (uploader) {
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

    addOrRemoveValidator : function(project) {
        Gikam.getAsyncComp('emc-project-scheme-edit-list-base-info-form').done(function (form) {
            if (project.sample === '1') {
                form.addFieldValidator('ext$.samplinglocation', ['notEmpty']);
                form.addFieldValidator('ext$.inspectedcontact', ['notEmpty']);
                form.addFieldValidator('ext$.inspectedphone', ['notEmpty']);
            } else {
                form.removeFieldValidator('ext$.samplinglocation', ['notEmpty']);
                form.removeFieldValidator('ext$.inspectedcontact', ['notEmpty']);
                form.removeFieldValidator('ext$.inspectedphone', ['notEmpty']);
            }
        })
    },

    onOrderActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').refresh({
                url : Gikam.printf('/secure/emc/module/bp/bp/orders/{id}/item-groups/queries', {
                    id : row.id
                })
            });
        }
    },

    loadSchemeItemGroupPage : function(schemeName) {
        var activeRow = Gikam.getComp('emc-project-scheme-edit-list-grid').getActivedRow();
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.ORDER_ADD',
            width : '70%',
            url : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/monitor-tmpls/page/scheme-item-group-choose-list',
            onAfterClose : function(rows) {
                if (rows && rows.length > 0) {
                    rows.forEach(item => {
                        item.syncStatus = '1';
                        item.source = 'schemeEdit';
                    });

                    workspace.window.showMask(true);
                    Gikam.postText(Gikam.printf(emcProjectScheme.baseUrl + '/{id}/orders', {
                        id : activeRow.id
                    }), Gikam.getJsonWrapper(null, [ 'emcOrderServiceImpl', [ {
                        monitorTmplId : rows[0].monitorTmplId,
                        orderCategory : rows[0].monitorTmplName,
                        syncStatus : '1',
                        source : 'schemeEdit'
                    } ] ], [ 'emcOrderItemGroupService', rows ])).done(function(id) {
                        Gikam.getComp('emc-project-scheme-edit-list-order-grid').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });

                }

            }
        });
    },

    batchUpdateOrder : function() {
        var selections = Gikam.getComp('emc-project-scheme-edit-list-order-grid').getSelections();
        if (selections.length > 0) {
            var activedRow = Gikam.getComp('emc-project-scheme-edit-list-grid').getActivedRow();
            Gikam.create('modal', {
                width : 400,
                height : '70%',
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.BATCH_UPDATE_ORDER',
                url : emcProjectScheme.baseUrl + '/page/batch-update-order',
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
                        });

                        Gikam.getComp('emc-project-scheme-edit-list-base-info-form').setData({
                            sendUsername : data.ext$.sender,
                            sendDate : data.sendDate,
                            ext$ : {
                                senduserid : data.senderId
                            }
                        });

                        Gikam.put(emcProjectScheme.baseUrl + '/' + activedRow.id, Gikam.getJsonWrapper({}, [ 'emcOrderServiceImpl', selections ])).done(function() {
                            Gikam.getComp('emc-project-scheme-edit-list-order-grid').refresh();
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
        var selections = Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').getSelections();

        if (selections.length === 1) {
            var activedRow = Gikam.getComp('emc-project-scheme-edit-list-order-grid').getActivedRow();
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
                        Gikam.put('/secure/emc/module/bp/bp/orders/item-groups/' + selections[0].id + '/action/edit-method', Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupServiceImpl', selections ])).done(function() {
                            Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').refresh();
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
        var selections = Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').getSelections();
        if (selections.length > 0) {
            var activedRow = Gikam.getComp('emc-project-scheme-edit-list-grid').getActivedRow();
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
                        Gikam.put(emcProjectScheme.baseUrl + '/' + activedRow.id, Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupServiceImpl', selections ])).done(function() {
                            Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').refresh();
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
        var selections = Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').getSelections();
        if (selections.length > 0) {
            var activedRow = Gikam.getComp('emc-project-scheme-edit-list-grid').getActivedRow();
            Gikam.create('modal', {
                width : 400,
                height : 250,
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.UPDATE_CONTAINER_GROUP',
                url : emcProjectScheme.baseUrl + '/page/update-order-container-group',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        selections.map(function(item) {
                            item.containerGroup = data.containerGroup;
                            item.fixative = data.fixative;
                            item.storeCondition = data.storeCondition;
                        });
                        Gikam.put(emcProjectScheme.baseUrl + '/' + activedRow.id, Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupServiceImpl', selections ])).done(function() {
                            Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').refresh();
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
            id : 'emc-project-scheme-edit-list-base-info-tab',
            panels : [ {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.BASE_INFO',
                id : 'emc-project-scheme-edit-list-base-info-panel',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '60%',
                        items : [ {
                            type : 'form',
                            columns : 3,
                            service : 'emcProjectServiceImpl',
                            id : 'emc-project-scheme-edit-list-base-info-form',
                            fields : emcProjectScheme.getBaseInfoFormFields1(),
                            readonly : true,
                            onUpdated : function() {
                                var grid = Gikam.getComp('emc-project-scheme-edit-list-grid');
                                grid.refreshRowById(grid.getActivedRow().id);
                            },
                            onLoadSuccess : function(data) {
                                Gikam.refreshGrids({
                                    'emc-project-scheme-edit-list-director-grid' : emcProjectScheme.baseUrl + '/{id}/directors/queries',
                                }, {
                                    id : data.id
                                });
                            }
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            id : 'emc-project-scheme-edit-list-director-tab',
                            fill : true,
                            width : Gikam.width.XL,
                            panels : [ {
                                id : 'emc-project-scheme-edit-list-director-panel',
                                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TITLE.PROJECT_DIRECTOR',
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-project-scheme-edit-list-director-grid',
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
                                    columns : emcProjectScheme.getProjectDirectorListColumns()
                                } ]
                            } ]

                        } ]
                    }
                } ]

            }, {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.ORDER',
                id : 'emc-project-scheme-edit-list-order-panel',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ {
                            type : 'grid',
                            id : 'emc-project-scheme-edit-list-order-grid',
                            columns : emcProjectScheme.getOrderColumns(),
                            service : 'emcOrderServiceImpl',
                            deleteFormatter : function(row) {
                                return row.orderNo || row.id;
                            },
                            rowStyleFormatter : function(row) {
                                // 对于方案修改新增的样品，用底色进行区分
                                if (row.ext$.syncstatus === '1') {
                                    return {
                                        backgroundColor: 'yellow'
                                    }
                                }
                            },

                            onLoadSuccess : function(data) {
                                if (!Gikam.isEmpty(data)) {
                                    this.activeRowByIndex(0);
                                } else {
                                    Gikam.cleanCompData([ 'emc-project-scheme-edit-list-order-item-group-grid' ]);
                                }
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
                                text : 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.BUTTON.DELETE_AND_SYNC',
                                icon : 'remove-row',
                                onClick : function() {
                                    let selections = Gikam.getComp('emc-project-scheme-edit-list-order-grid').getSelections();
                                    if (Gikam.isEmpty(selections)) {
                                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                        return;
                                    }

                                    Gikam.confirm('EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TITLE.DELETE_CONFIRM', 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CONFIRM_DELETE_ORDER', function() {
                                        workspace.window.showMask(true);
                                        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/delete-sync-order', Gikam.getJsonWrapper({}, [ '', selections ])).done(function() {
                                        	 Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/after-delete-test', Gikam.getJsonWrapper({}, [ '', selections ]));
                                        	Gikam.getComp('emc-project-scheme-edit-list-order-grid').refresh();
                                        }).always(function() {
                                            workspace.window.closeMask();
                                        });
                                    });
                                }
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.PREVIEW',
                                icon : 'print',
                                onClick : function() {
                                    Gikam.viewReport('deliveringLabel', Gikam.getComp('emc-project-scheme-edit-list-order-grid').getSelections());
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
                                        _this.updateEditQcCategory();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.EMPTY_QC_CATEGORY',
                                    icon : 'copy',
                                    onClick : function() {
                                        _this.emptyEditQcCategory();
                                    }
                                } ]
                            }, {
                                type : 'button',
                                text : 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.BUTTON.SYNC_NEW_ORDER',
                                icon : 'refresh',
                                onClick : function() {
                                    let selections = Gikam.getComp('emc-project-scheme-edit-list-order-grid').getSelections();
                                    if (Gikam.isEmpty(selections)) {
                                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                        return;
                                    }

                                    Gikam.confirm('EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TITLE.SYNC_CONFIRM', 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CONFIRM_SYNC_ORDER', function() {
                                        workspace.window.showMask(true);
                                        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/sync-new-order', Gikam.getJsonWrapper({}, [ '', selections ])).done(function() {
                                            Gikam.getComp('emc-project-scheme-edit-list-order-grid').refresh();
                                        }).always(function() {
                                            workspace.window.closeMask();
                                        });
                                    });
                                }
                            }, {
                                type : 'button',
                                text : 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.BUTTON.SYNC_NEW_TEST',
                                icon : 'refresh',
                                onClick : function() {
                                    let selections = Gikam.getComp('emc-project-scheme-edit-list-order-grid').getSelections();
                                    if (Gikam.isEmpty(selections)) {
                                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                        return;
                                    }

                                    Gikam.confirm('EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TITLE.SYNC_CONFIRM', 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CONFIRM_SYNC_TEST', function() {
                                        workspace.window.showMask(true);
                                        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/sync-new-test', Gikam.getJsonWrapper({}, [ '', selections ])).done(function() {
                                            Gikam.getComp('emc-project-scheme-edit-list-order-grid').refresh();
                                        	Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').refresh();
                                        }).always(function() {
                                            workspace.window.closeMask();
                                        });
                                    });
                                }
                            }, {
                                type : 'button',
                                text : '点位完成',
                                icon : 'add',
                                onClick : function() {
                                    _this.completeFolderForOrder();
                                }
                            }, {
                                type : 'button',
                                text : '取消点位完成',
                                icon : 'remove-row',
                                onClick : function() {
                                    _this.cancelFolderForOrder();
                                }
                            } ]
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'grid',
                            id : 'emc-project-scheme-edit-list-order-item-group-grid',// 样品分析项组
                            columns : emcProjectScheme.getOrderItemGroupColumns(),
                            service : 'emcOrderItemGroupServiceImpl',
                            rowStyleFormatter : function(row) {
                                // 对于方案修改新增的样品，用底色进行区分
                                if (row.ext$.syncstatus === '1') {
                                    return {
                                        backgroundColor: 'yellow'
                                    }
                                }
                            },
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
                                type : 'button',
                                text : 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.BUTTON.DELETE_AND_SYNC',
                                icon : 'remove-row',
                                onClick : function() {
                                    let selections = Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').getSelections();
                                    if (Gikam.isEmpty(selections)) {
                                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                        return;
                                    }

                                    let orders = Gikam.getComp('emc-project-scheme-edit-list-order-grid').getSelections() || [];
                                    if (Gikam.isEmpty(orders)) {
                                        orders.push(Gikam.getComp('emc-project-scheme-edit-list-order-grid').getActivedRow());
                                    }

                                    Gikam.confirm('EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TITLE.DELETE_CONFIRM', 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CONFIRM_DELETE_TEST', function() {
                                        workspace.window.showMask(true);
                                        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/delete-sync-test', Gikam.getJsonWrapper({}, [ '', selections ], [ 'emcOrderServiceImpl', orders ])).done(function() {
                                       	 Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/after-delete-test', Gikam.getJsonWrapper({}, [ 'emcOrderServiceImpl', orders ]));
                                        	Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').refresh();
                                        }).always(function() {
                                            workspace.window.closeMask();
                                        });
                                    });
                                }
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
                            }, {
                                type : 'dropDownMenu',
                                text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.DATASHARING',
                                items : [ {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.ADD_DATASHARING',
                                    icon : 'add',
                                    onClick : function() {
                                        _this.addDataSharing();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.DEL_DATASHARING',
                                    icon : 'remove-row',
                                    onClick : function() {
                                        _this.delDataSharing();
                                    }
                                } ]
                            }, {
                                type : 'button',
                                text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.MTLSELECT',
                                icon : 'submit',
                                onClick : function() {
                                    _this.mtlSelect();
                                }
                            }, {
                                type : 'dropDownMenu',
                                text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.SUBCONTRACTOR',
                                items : [ {
                                    type : 'button',
                                    text : '样品分包',
                                    icon : 'add',
                                    onClick : function() {
                                        _this.addOrderSubcontractor();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.DEL_SUBCONTRACTOR',
                                    icon : 'remove-row',
                                    onClick : function() {
                                        _this.delOrderSubcontractor();
                                    }
                                } ]
                            }  ]
                        } ]
                    }
                } ]
            }, _this.getMonitorSchemePanel(), {
                id : 'emc-project-scheme-edit-list-sampling-prepare-panel',
                title : 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TITLE.SAMPLING_PREPARE',
                items : [ {
                    type : 'layout',
                    north : {
                        height : 260,
                        items : [ {
                            type : 'grid',
                            service : 'emcFolderServiceImpl',
                            id : 'emc-project-scheme-edit-folder-list-grid',
                            columns : emcProjectScheme.getSamplePrepareListColumns(),
                            onLoadSuccess : function(data) {
                                if (!Gikam.isEmpty(data)) {
                                    this.activeRowByIndex(0);
                                } else {
                                    Gikam.getAsyncComp('emc-project-scheme-edit-folder-order-grid').done(function(grid) {
                                        grid.cleanData();
                                    });
                                    Gikam.getAsyncComp('emc-project-scheme-edit-folder-order-task-grid').done(function(grid) {
                                        grid.cleanData();
                                    });
                                }
                            },
                            onRowActive : function(index, row) {
                                if (row) {
                                    Gikam.getComp('emc-project-scheme-edit-folder-order-grid').refresh({
                                        url : Gikam.printf('/secure/emc/module/bp/bp/orders/queries/raw'),
                                        requestData : {
                                            folderId_SEQ : row.id
                                        }
                                    });
                                }
                            },
                            toolbar : [ {
                                type : 'button',
                                text : 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.BUTTON.SYNC_FOLDER_FROM_SCHEME',
                                icon : 'refresh',
                                onClick : function () {
                                    let project = Gikam.getComp('emc-project-scheme-edit-list-grid').getActivedRow();
                                    if (!project) {
                                        return;
                                    }

                                    Gikam.create('modal', {
                                        width : '80%',
                                        title : 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TITLE.CHOOSE_SCHEME',
                                        url : IFM_CONTEXT + '/secure/emc/module/bp/edit/project-schemes/page/choose-scheme' + Gikam.param({
                                            projId_SEQ : project.id,
                                        }),
                                        onAfterClose : function(data) {
                                            if (!Gikam.isEmpty(data)) {
                                                Gikam.confirm('EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TITLE.SYNC_CONFIRM', 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CONFIRM_SYNC_FOLDER_FROM_SCHEME', function() {
                                                    workspace.window.showMask(true);
                                                    Gikam.post(emcProjectScheme.schemeUrl + '/action/sync-folder-from-scheme', Gikam.getJsonWrapper({}, [ '', data ])).always(function() {
                                                        Gikam.getComp('emc-project-scheme-edit-folder-list-grid').refresh();
                                                    }).always(function() {
                                                        workspace.window.closeMask();
                                                    });
                                                });
                                            }
                                        }
                                    });
                                }
                            }, {
                                type : 'button',
                                text : 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.BUTTON.DELETE_FOLDER',
                                icon : 'remove-row',
                                onClick : function () {
                                    var selections = Gikam.getComp('emc-project-scheme-edit-folder-list-grid').getSelections();
                                    if (Gikam.isEmpty(selections)) {
                                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                        return;
                                    }

                                    Gikam.confirm('EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TITLE.DELETE_CONFIRM', 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CONFIRM_DELETE_FOLDER', function() {
                                        workspace.window.showMask(true);
                                        Gikam.post(emcProjectScheme.schemeUrl + '/action/delete-folder', Gikam.getJsonWrapper({}, [ '', selections ])).always(function() {
                                            Gikam.getComp('emc-project-scheme-edit-folder-list-grid').refresh();
                                        }).always(function() {
                                            workspace.window.closeMask();
                                        });
                                    });
                                }
                            }, {
                                type : 'button',
                                text : '点位完成',
                                icon : 'add',
                                onClick : function() {
                                    _this.completeFolder();
                                }
                            }, {
                                type : 'button',
                                text : '取消点位完成',
                                icon : 'remove-row',
                                onClick : function() {
                                    _this.cancelFolder();
                                }
                            }  ]
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'layout',
                            west : {
                                width : '50%',
                                // 样品
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-project-scheme-edit-folder-order-grid',
                                    service : 'emcOrderServiceImpl',
                                    url : _this.getOrderUrl(),
                                    columns : emcProjectScheme.getSamplePrepareOrderColumns(),
                                    onRowActive : function(index, row) {
                                        Gikam.getComp('emc-project-scheme-edit-folder-order-task-grid').refresh({
                                            url : '/secure/emc/module/bp/bp/order-tasks/unique-item-groups/queries',
                                            requestData : {
                                                orderId_SEQ : row.id
                                            }
                                        });
                                    },
                                    deleteFormatter : function(row) {
                                        return row.orderNo || row.id;
                                    },
                                    onLoadSuccess : function(data) {
                                        if (!Gikam.isEmpty(data)) {
                                            this.activeRowByIndex(0);
                                        } else {
                                            Gikam.cleanCompData([ 'emc-project-scheme-edit-folder-order-task-grid' ]);
                                        }
                                    },
                                    toolbar : [ {
                                        type : 'button',
                                        text : 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.BUTTON.SYNC_ORDER_FROM_SCHEME',
                                        icon : 'refresh',
                                        onClick : function () {
                                            let project = Gikam.getComp('emc-project-scheme-edit-list-grid').getActivedRow();
                                            if (!project) {
                                                return;
                                            }

                                            let folder = Gikam.getComp('emc-project-scheme-edit-folder-list-grid').getActivedRow();
                                            if (!folder) {
                                                return;
                                            }

                                            let orders = Gikam.getComp('emc-project-scheme-edit-folder-order-grid').getData();
                                            if (!Gikam.isEmpty(orders)) {
                                                Gikam.alert('EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.PLEASE_USE_COPY_ORDER_BTN');
                                                return;
                                            }

                                            Gikam.create('modal', {
                                                width : '80%',
                                                title : 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TITLE.CHOOSE_SCHEME',
                                                url : IFM_CONTEXT + '/secure/emc/module/bp/edit/project-schemes/page/choose-scheme' + Gikam.param({
                                                    projId_SEQ : project.id,
                                                }),
                                                onAfterClose : function(data) {
                                                    if (!Gikam.isEmpty(data)) {
                                                        Gikam.confirm('EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TITLE.SYNC_CONFIRM', 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CONFIRM_SYNC_ORDER_FROM_SCHEME', function() {
                                                            workspace.window.showMask(true);
                                                            Gikam.post(emcProjectScheme.schemeUrl + '/action/sync-order-from-scheme', Gikam.getJsonWrapper({}, ['', [folder]], [ '', data ])).always(function() {
                                                                Gikam.getComp('emc-project-scheme-edit-folder-order-grid').refresh();
                                                            }).always(function() {
                                                                workspace.window.closeMask();
                                                            });
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    }, {
                                        type : 'button',
                                        text : 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.BUTTON.SYNC_TEST_FROM_SCHEME',
                                        icon : 'refresh',
                                        onClick : function () {
                                            let folder = Gikam.getComp('emc-project-scheme-edit-folder-list-grid').getActivedRow();
                                            if (!folder) {
                                                return;
                                            }

                                            let orders = Gikam.getComp('emc-project-scheme-edit-folder-order-grid').getSelections();
                                            if (Gikam.isEmpty(orders)) {
                                                return;
                                            }

                                            Gikam.create('modal', {
                                                width : '80%',
                                                title : 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TITLE.CHOOSE_SCHEME',
                                                url : IFM_CONTEXT + '/secure/emc/module/bp/edit/project-schemes/page/choose-scheme-test' + Gikam.param({
                                                    projId_SEQ : folder.projId,
                                                    folderNo_SEQ : folder.folderNo
                                                }),
                                                onAfterClose : function(data) {
                                                    if (!Gikam.isEmpty(data)) {
                                                        Gikam.confirm('EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TITLE.SYNC_CONFIRM', 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CONFIRM_SYNC_TEST_FROM_SCHEME', function() {
                                                            workspace.window.showMask(true);
                                                            Gikam.post(emcProjectScheme.schemeUrl + '/action/sync-test-from-scheme', Gikam.getJsonWrapper({}, [ '', [folder] ], ['', orders], ['', [data.scheme]], ['', data.tests])).always(function() {
                                                                Gikam.getComp('emc-project-scheme-edit-folder-order-grid').refresh();
                                                            }).always(function() {
                                                                workspace.window.closeMask();
                                                            });
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    }, {
                                        type : 'button',
                                        text : 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.BUTTON.COPY_ORDER',
                                        icon : 'add',
                                        onClick : function () {
                                            var selections = Gikam.getComp('emc-project-scheme-edit-folder-order-grid').getSelections();
                                            if (Gikam.isEmpty(selections)) {
                                                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                                return;
                                            }

                                            Gikam.confirm('EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TITLE.COPY_CONFIRM', 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CONFIRM_COPY_ORDER', function() {
                                                workspace.window.showMask(true);
                                                Gikam.post(emcProjectScheme.schemeUrl + '/action/copy-order', Gikam.getJsonWrapper({}, [ '', selections ])).always(function() {
                                                    Gikam.getComp('emc-project-scheme-edit-folder-order-grid').refresh();
                                                }).always(function() {
                                                    workspace.window.closeMask();
                                                });
                                            });
                                        }
                                    }, {
                                        type : 'button',
                                        text : 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.BUTTON.DELETE_ORDER',
                                        icon : 'remove-row',
                                        onClick : function () {
                                            var selections = Gikam.getComp('emc-project-scheme-edit-folder-order-grid').getSelections();
                                            if (Gikam.isEmpty(selections)) {
                                                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                                return;
                                            }

                                            Gikam.confirm('EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TITLE.DELETE_CONFIRM', 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CONFIRM_DELETE_ORDER', function() {
                                                workspace.window.showMask(true);
                                                Gikam.post(emcProjectScheme.schemeUrl + '/action/delete-order', Gikam.getJsonWrapper({}, [ '', selections ])).always(function() {
                                                    Gikam.getComp('emc-project-scheme-edit-folder-order-grid').refresh();
                                                }).always(function() {
                                                    workspace.window.closeMask();
                                                });
                                            });
                                        }
                                    }]
                                } ]
                            },
                            center : {
                                width : '50%',
                                // 容器信息
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-project-scheme-edit-folder-order-task-grid',
                                    service : 'emcOrderTaskServiceImpl',
                                    columns : emcProjectScheme.getSamplePrepareOrderContainerColumns(),
                                    toolbar : [ {
                                        type : 'button',
                                        text : 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.BUTTON.DELETE_TEST',
                                        icon : 'remove-row',
                                        onClick : function () {
                                            var selections = Gikam.getComp('emc-project-scheme-edit-folder-order-task-grid').getSelections();
                                            if (Gikam.isEmpty(selections)) {
                                                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                                return;
                                            }

                                            let orders = Gikam.getComp('emc-project-scheme-edit-folder-order-grid').getSelections() || [];
                                            if (Gikam.isEmpty(orders)) {
                                                orders.push(Gikam.getComp('emc-project-scheme-edit-folder-order-grid').getActivedRow());
                                            }

                                            Gikam.confirm('EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TITLE.DELETE_CONFIRM', 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CONFIRM_DELETE_TEST', function() {
                                                workspace.window.showMask(true);
                                                Gikam.post(emcProjectScheme.schemeUrl + '/action/delete-test', Gikam.getJsonWrapper({}, [ '', selections ], [ 'emcOrderServiceImpl', orders ])).always(function() {
                                                    Gikam.getComp('emc-project-scheme-edit-folder-order-task-grid').refresh();
                                                }).always(function() {
                                                    workspace.window.closeMask();
                                                });
                                            });
                                        }
                                    }, {
                                        type : 'dropDownMenu',
                                        text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.SUBCONTRACTOR',
                                        items : [ {
                                            type : 'button',
                                            text : '采样前分包',
                                            icon : 'add',
                                            onClick : function() {
                                                _this.beforeSamplingSubpackage();
                                            }
                                        }, {
                                            type : 'button',
                                            text : '样品分包',
                                            icon : 'add',
                                            onClick : function() {
                                                _this.afterSamplingSubpackage();
                                            }
                                        }, {
                                            type : 'button',
                                            text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.DEL_SUBCONTRACTOR',
                                            icon : 'remove-row',
                                            onClick : function() {
                                                _this.delSubcontractor();
                                            }
                                        } ]
                                    }, {
                                        type : 'dropDownMenu',
                                        text : '按点位分包',
                                        items : [ {
                                            type : 'button',
                                            text : '采样前分包(点位)',
                                            icon : 'add',
                                            onClick : function() {
                                                _this.folderBeforeSamplingSubpackage();
                                            }
                                        }, {
                                            type : 'button',
                                            text : '样品分包(点位)',
                                            icon : 'add',
                                            onClick : function() {
                                                _this.folderAfterSamplingSubpackage();
                                            }
                                        }, {
                                            type : 'button',
                                            text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.DEL_SUBCONTRACTOR',
                                            icon : 'remove-row',
                                            onClick : function() {
                                                _this.folderDelSubcontractor();
                                            }
                                        } ]
                                    }, {
                                        type : 'button',
                                        text : '移动该样品测试',
                                        icon : 'remove-row',
                                        onClick : function() {
                                            var selections = Gikam.getComp('emc-project-scheme-edit-folder-order-task-grid').getSelections();
                                            if (Gikam.isEmpty(selections)) {
                                                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                                return;
                                            }
                                            Gikam.create('modal', {
                                                width : '70%',
                                                title : Gikam.choose.getConfig('order').title,
                                                url : Gikam.choose.getConfig('order').url + Gikam.param({
                                                    projId_EQ : selections[0].projId,
                                                    folderId_EQ :selections[0].folderId,
                                                    id_DIFFER : selections[0].orderId,
                                                }),


                                                onAfterClose : function(data) {
                                                    if (!Gikam.isEmpty(data)) {
                                                        workspace.window.showMask(true);
                                                        var url = emcProjectScheme.schemeUrl + '/action/remove-order-task';
                                                        Gikam.postText(url, Gikam.getJsonWrapper(
                                                            {},[ 'emcOrderServiceImpl', data ],[ 'emcOrderTaskServiceImpl', selections ])).done(function(t) {
                                                            if(t != "" && t!=null){
                                                                    var tt =String(t);
                                                                    if(tt!="true"){
                                                                        Gikam.alert(tt+"测试已经建批不允许移除");
                                                                    }else{
                                                                        Gikam.alert("移除完成!");
                                                                    }
                                                                }else{
                                                                    Gikam.alert("移除失败!");
                                                                }
                                                        }).always(function() {
                                                            workspace.window.closeMask();
                                                            Gikam.getComp('emc-project-scheme-edit-folder-order-task-grid').refresh();
                                                        });
                                                    }
                                                }
                                            });


                                        }
                                    }]
                                } ]
                            }
                        } ]
                    }
                } ]
            }, {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-project-scheme-edit-list-uploader',
                    dbTable : 'T_EMC_PROJECT',
                    readonly : true,
                } ]
            }, {
                title : 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TITLE.CONTRACT_FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-project-scheme-edit-list-contract-uploader',
                    dbTable : 'T_CRM_OPPORTUNITY_CONTRACT',
                    readonly : true,
                } ]
            }, {
                title : '样品分包单管理',
                id : 'emc-project-subpackage-edit-panel',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ {
                            type : 'layout',
                            west : {
                                width : '50%',
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-proj-edit-subpackage-info-grid',
                                    columns : emcProjectScheme.getSubpackageOrderTaskColumns(),
                                    requestData : {
                                        subpackageType_SEQ : 'create',
                                        orgId_SB: workspace.user.ext$.parentorgid,
                                        subpackageCategory_SEQ : 'orderSubpackage'
                                    },
                                    service : 'emcSubpackageServiceImpl',
                                    toolbar : _this.getSubpackageButton(),
                                    onLoadSuccess : function(data) {
                                        if (!Gikam.isEmpty(data)) {
                                            this.activeRowByIndex(0);
                                        } else {
                                            Gikam.cleanCompData([ 'emc-proj-edit-subpackage-order-grid' ])
                                            Gikam.cleanCompData([ 'emc-proj-edit-subpackage-base-info-form' ]);

                                        }
                                    },
                                    onRowActive : function(index, row) {
                                        _this.onPsSubpackageActive(row);
                                    },
                                } ]
                            },
                            center : {
                                items : [ {
                                    type : 'layout',
                                    north : {
                                        height : '80%',
                                        items : [ {
                                            type : 'form',
                                            columns : 3,
                                            service : 'emcSubpackageServiceImpl',
                                            id : 'emc-proj-edit-subpackage-base-info-form',
                                            fields : emcProjectScheme.getSubpackageSchemeBaseInfoFormFields(),
                                            onUpdated : function() {
                                                var grid = Gikam.getComp('emc-proj-edit-subpackage-info-grid');
                                                grid.refreshRowById(grid.getActivedRow().id);
                                            },
                                        } ]
                                    },
                                } ]
                            }

                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'layout',
                            west : {
                                width : '50%',
                                items : [ {
                                    type : 'grid',
                                    readonly : true,
                                    id : 'emc-proj-edit-subpackage-order-grid',
                                    columns : emcProjectScheme.getSubpackageOrderColumns(),
                                    service : 'emcProjectServiceImpl',
                                    requestData: {
                                        subpackageflag_SEQ : '1'
                                    },
                                    toolbar : _this.getOrderSubpackageButton(),
                                    onLoadSuccess : function(data) {
                                        if (!Gikam.isEmpty(data)) {
                                            this.activeRowByIndex(0);
                                        } else {
                                            Gikam.cleanCompData([ 'emc-proj-edit-subpackage-order-task-grid' ]);
                                        }
                                    },
                                    onRowActive : function(index, row) {
                                        _this.onSubpackageOrderInfoActive(row);
                                    },
                                } ]
                            },
                            center : {
                                items : [ {
                                    type : 'grid',
                                    readonly : true,
                                    id : 'emc-proj-edit-subpackage-order-task-grid',
                                    columns : emcProjectScheme.getSubpackageOrderTaskColumns(),
                                    service : 'emcSubpackageTaskServiceImpl',
                                    toolbar : [{
                                        type : 'button',
                                        text : '删除',
                                        icon : 'remove',
                                        onClick : function() {
                                            var selections = Gikam.getComp('emc-proj-edit-subpackage-order-task-grid').getSelections();
                                            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/delete-package-task', Gikam.getJsonWrapper({}, [ 'emcSubpackageTaskServiceImpl', selections ])).done(function() {
                                                Gikam.alert('删除成功');
                                                Gikam.getComp('emc-proj-edit-subpackage-order-task-grid').refresh();
                                            });
                                        }
                                    }
                                    ]
                                } ]
                            }
                        } ]
                    }
                } ]
            }, {
                title : '采样前分包单管理',
                id : 'emc-project-sample-subpackage-edit-panel',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ {
                            type : 'layout',
                            west : {
                                width : '50%',
                                items : [ {
                                    type : 'grid',
                                    readonly : true,
                                    id : 'emc-proj-edit-sample-subpackage-edit-grid',
                                    columns : emcProjectScheme.getProjectSubpackageListColumns(),
                                    requestData : {
                                        subpackageType_SEQ : 'create',
                                        orgId_SB: workspace.user.ext$.parentorgid,
                                        subpackageCategory_SEQ : 'beforeSampling'
                                    },
                                    service : 'emcSubpackageServiceImpl',
                                    toolbar : _this.getSampleSubpackageButton(),
                                    onLoadSuccess : function(data) {
                                        if (!Gikam.isEmpty(data)) {
                                            this.activeRowByIndex(0);
                                        } else {
                                            Gikam.cleanCompData([ 'emc-proj-edit-subpackage-folder-grid' ]);
                                            Gikam.cleanCompData([ 'emc-proj-edit-sample-subpackage-base-info-form' ]);

                                        }
                                    },
                                    onRowActive : function(index, row) {
                                        _this.onPsSampleSubpackageActive(row);
                                    },
                                } ]
                            },
                            center : {
                                items : [ {
                                    type : 'layout',
                                    north : {
                                        height : '80%',
                                        items : [ {
                                            type : 'form',
                                            columns : 3,
                                            service : 'emcSubpackageServiceImpl',
                                            id : 'emc-proj-edit-sample-subpackage-base-info-form',
                                            fields : emcProjectScheme.getSubpackageSchemeBaseInfoFormFields(),
                                            onUpdated : function() {
                                                var grid = Gikam.getComp('emc-proj-edit-sample-subpackage-edit-grid');
                                                grid.refreshRowById(grid.getActivedRow().id);
                                            },
                                        } ]
                                    },
                                } ]
                            },
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'layout',
                            west : {
                                width : '50%',
                                items : [ {
                                    type : 'grid',
                                    readonly : true,
                                    id : 'emc-proj-edit-subpackage-folder-grid',
                                    columns : emcProjectScheme.getSubpackageFolderColumns(),
                                    service : 'emcProjSchemeServiceImpl',
                                    toolbar : _this.getProjSchemeSubpackageButton(),
                                    onLoadSuccess : function(data) {
                                        if (!Gikam.isEmpty(data)) {
                                            this.activeRowByIndex(0);
                                        } else {
                                            Gikam.cleanCompData([ 'emc-proj-edit-subpackage-sample-order-task-grid' ]);
                                        }
                                    },
                                    onRowActive : function(index, row) {
                                        _this.onSampleSubpackageFolderInfoActive(row);
                                    },
                                } ]
                            },
                            center : {
                                items : [ {
                                    type : 'grid',
                                    readonly : true,
                                    id : 'emc-proj-edit-subpackage-sample-order-task-grid',
                                    columns : emcProjectScheme.getSubpackageSampleOrderTaskColumns(),
                                    service : 'emcSubpackageTaskServiceImpl',
                                    toolbar : [{
                                        type : 'button',
                                        text : '删除',
                                        icon : 'remove',
                                        onClick : function() {
                                            var selections = Gikam.getComp('emc-proj-edit-subpackage-sample-order-task-grid').getSelections();
                                            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/delete-package-task', Gikam.getJsonWrapper({}, [ 'emcSubpackageTaskServiceImpl', selections ])).done(function() {
                                                Gikam.alert('删除成功');
                                                Gikam.getComp('emc-proj-edit-subpackage-sample-order-task-grid').refresh();

                                            });
                                        }
                                    }
                                    ]
                                } ]
                            }
                        } ]
                    }
                } ]
            }
            ]
        }
    },

    /*质控标样选择*/
    mtlSelect : function () {
        var selections = Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').getSelections();
        if (selections.length > 0) {
            Gikam.create('modal', {
                width : '60%',
                height : '50%',
                title : Gikam.choose.getConfig('mtlStock').title,
                url : Gikam.choose.getConfig('mtlStock').url + Gikam.param({
                    single : true,
                    projectFlag : '1',
                }),
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        selections.map(function(item) {
                            item.batchNo = data[0].batchNo;
                        });
                        Gikam.put('/secure/emc/module/bp/project/projects/qcMtlSelect', Gikam.getJsonWrapper({}, [ 'pfOrderItemGroupServiceImpl', selections ])).done(function() {
                            Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').refresh();
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

    /** 样品登记设置分包商 */
    addOrderSubcontractor : function() {
        /*var selections = Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').getSelections();
        if (selections.length > 0) {
            Gikam.create('modal', {
                width : '20%',
                height : '50%',
                title : Gikam.choose.getConfig('subcontractor').title,
                url : Gikam.choose.getConfig('subcontractor').url + Gikam.param({
                    single : true,
                }),
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        selections.map(function(item) {
                            item.subcontractorId = data[0].id;
                        });
                        Gikam.put('/secure/emc/module/bp/bp/orders/item-groups/action/subcontractor-edit', Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupServiceImpl', selections ])).done(function() {
                            Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').refresh();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }*/

        var selections = Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var orders = Gikam.getComp('emc-project-scheme-edit-list-order-grid').getSelections();
        if (Gikam.isEmpty(orders)) {
            Gikam.alert('请至少选择一个样品');
            return;
        }
        Gikam.confirm('确认操作', '是否确认进行分包设置？', function() {
            workspace.window.showMask(true);
            Gikam.postText('/secure/emc/module/bp/bp/orders/action/subpackage-test', Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupServiceImpl', selections ], [ 'emcOrderServiceImpl', orders])).done(function() {
            	//若除了该测试，其他测试都完成，分包后改变点位和项目状态
            	Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/after-delete-test', Gikam.getJsonWrapper({}, [ '', orders ]));
            	Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    /** 样品登记删除分包商 */
    delOrderSubcontractor : function() {
        /*var selections = Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').getSelections();
        if (selections.length > 0) {
            var activedRow = Gikam.getComp('emc-project-scheme-edit-list-grid').getActivedRow();
            selections.map(function(item) {
                item.subcontractorId = '';
                item.subPackageNo = '';
            });
            workspace.window.showMask(true);
            Gikam.put(emcProjectScheme.baseUrl + '/' + activedRow.id, Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupServiceImpl', selections ])).done(function() {
                Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }*/

        var selections = Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var orders = Gikam.getComp('emc-project-scheme-edit-list-order-grid').getSelections();
        if (Gikam.isEmpty(orders)) {
            Gikam.alert('请至少选择一个样品');
            return;
        }
        Gikam.confirm('确认操作', '是否确认删除分包？', function() {
            workspace.window.showMask(true);
            Gikam.postText('/secure/emc/module/bp/bp/orders/action/cancel-subpackage-test', Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupServiceImpl', selections ], [ 'emcOrderServiceImpl', orders])).done(function() {
                Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });
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
                        columns : emcProjectScheme.getSchemeEditListColumns(),
                        service : 'emcProjSchemeServiceImpl',
                        checkOnActive : false,
                        deleteFormatter : function(row) {
                            return row.folderNo || row.id;
                        },
                        rowStyleFormatter : function(row) {
                            // 对于方案修改新增的点位，用底色进行区分
                            if (row.ext$.status === '1') {
                                return {
                                    backgroundColor: 'yellow'
                                }
                            }
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
                        onBeforeUpdate : function(data, keys) {
                            // 不限制方案编辑
                            /*if (data.row.ext$.status !== '1') {
                                Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.CAN_NOT_MODIFY');
                                return false;
                            }*/
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
                                var activeRow = Gikam.getComp('emc-project-scheme-edit-list-grid').getActivedRow();
                                if (Gikam.isEmpty(activeRow)) {
                                    Gikam.alert("EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.PROJECT_REQUIRED");
                                    return;
                                }

                                var data = Gikam.getComp('emc-project-scheme-edit-list-base-info-form').getData();
                                var editMonitorInfo = _this.param.editMonitorInfo;
                                // 这里使用委托单位来过滤数据，之前是受检单位
                                var testOrgId = data.entrustOrgId ? data.entrustOrgId : '-';
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

                                // 这里使用委托单位来过滤数据，之前是受检单位
                                var data = Gikam.getComp('emc-project-scheme-edit-list-base-info-form').getData();
                                var testOrgId = data.entrustOrgId;
                                _this.initAddProj(projId, testOrgId);
                            }
                        }, {
                            type : 'button',
                            text : 'GIKAM.BUTTON.DELETE',
                            icon : 'remove-row',
                            onClick : function() {
                                /*let selections = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
                                let nonRemovable = selections.some(item => item.ext$.status !== '1');
                                if (nonRemovable) {
                                    Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.CAN_NOT_DELETE');
                                    return false;
                                }*/

                                Gikam.getComp('emc-schemeedit-audit-list-grid').deleteRows(emcProjectScheme.schemeUrl);
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
                                    var data = Gikam.getComp('emc-project-scheme-edit-list-base-info-form').getData();
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
                                    /*let selections = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
                                    let nonRemovable = selections.some(item => item.ext$.status !== '1');
                                    if (nonRemovable) {
                                        Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.CAN_NOT_MODIFY');
                                        return false;
                                    }*/
                                    _this.updateCycleAndFrequency();
                                }
                            }, {
                                type : 'button',
                                text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.INDEX',
                                icon : 'edit',
                                onClick : function() {
                                    /*let selections = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
                                    let nonRemovable = selections.some(item => item.ext$.status !== '1');
                                    if (nonRemovable) {
                                        Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.CAN_NOT_MODIFY');
                                        return false;
                                    }*/
                                    _this.updateIndex();
                                }
                            }, {
                                type : 'button',
                                text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.UPDATE_ORG',
                                icon : 'edit',
                                onClick : function() {
                                    /*let selections = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
                                    let nonRemovable = selections.some(item => item.ext$.status !== '1');
                                    if (nonRemovable) {
                                        Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.CAN_NOT_MODIFY');
                                        return false;
                                    }*/
                                    _this.updateOrg();
                                }
                            } ]
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.BUTTON.SYNC_NEW_FOLDER',
                            icon : 'refresh',
                            onClick : function() {
                                var selections = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
                                if (Gikam.isEmpty(selections)) {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                    return;
                                }

                                Gikam.confirm('EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TITLE.SYNC_CONFIRM', 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CONFIRM_SYNC_FOLDER', function() {
                                    workspace.window.showMask(true);
                                    Gikam.post(emcProjectScheme.schemeUrl + '/action/sync-new-folder', Gikam.getJsonWrapper({}, [ '', selections ])).always(function() {
                                        Gikam.getComp('emc-project-scheme-edit-list-grid').refresh();
                                    }).always(function() {
                                        workspace.window.closeMask();
                                    });
                                });
                            }
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.BUTTON.SYNC_NEW_TEST',
                            icon : 'refresh',
                            onClick : function() {
                                var selections = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
                                if (Gikam.isEmpty(selections)) {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                    return;
                                }

                                Gikam.confirm('EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TITLE.SYNC_CONFIRM', 'EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CONFIRM_SYNC_TEST', function() {
                                    workspace.window.showMask(true);
                                    Gikam.post(emcProjectScheme.schemeUrl + '/action/sync-new-test', Gikam.getJsonWrapper({}, [ '', selections ])).always(function() {
                                        Gikam.getComp('emc-project-scheme-edit-list-grid').refresh();
                                    }).always(function() {
                                        workspace.window.closeMask();
                                    });
                                });
                            }
                        } ]
                    } ]
                },
                center : {
                    items : [ {
                        type : 'grid',
                        id : 'emc-proj-order-edit-list-grid',
                        url : _this.getOrderUrl(),
                        columns : emcProjectScheme.getOrderEditListColumns(),
                        service : 'emcProjOrderServiceImpl',
                        group : {
                            fields : [ "schemeName"],
                            formatter : function(row) {
                                return row.schemeName;
                            }
                        },
                        rowStyleFormatter : function(row) {
                            // 对于方案修改新增的测试，用底色进行区分
                            if (row.ext$.status === '1') {
                                return {
                                    backgroundColor: 'yellow'
                                }
                            }
                        },
                        toolbar : [ {
                            type : 'button',
                            text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.UPDATE_TEST',
                            icon : 'edit',
                            onClick : function() {
                                /*let selections = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
                                let nonRemovable = selections.some(item => item.ext$.status !== '1');
                                if (nonRemovable) {
                                    Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.CAN_NOT_MODIFY');
                                    return false;
                                }*/
                                _this.updateItemGroup();
                            }
                        }, {
                            type : 'button',
                            text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.BATCH_UPDATE_NUM',
                            icon : 'edit',
                            onClick : function() {
                                /*let activeRow = Gikam.getComp('emc-schemeedit-audit-list-grid').getActivedRow();
                                if (activeRow.ext$.status !== '1') {
                                    Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.CAN_NOT_MODIFY');
                                    return false;
                                }*/
                                _this.batchUpdateSampleNum();
                            }
                        }, {
                            type : 'dropDownMenu',
                            text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.SUBCONTRACTOR',
                            hidden : true,
                            items : [ {
                                type : 'button',
                                text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.ADD_SUBCONTRACTOR',
                                icon : 'add',
                                onClick : function() {
                                    /*let activeRow = Gikam.getComp('emc-schemeedit-audit-list-grid').getActivedRow();
                                    if (activeRow.ext$.status !== '1') {
                                        Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.CAN_NOT_MODIFY');
                                        return false;
                                    }*/
                                    _this.addSubcontractor();
                                }
                            }, {
                                type : 'button',
                                text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.DEL_SUBCONTRACTOR',
                                icon : 'remove-row',
                                onClick : function() {
                                    /*let activeRow = Gikam.getComp('emc-schemeedit-audit-list-grid').getActivedRow();
                                    if (activeRow.ext$.status !== '1') {
                                        Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.CAN_NOT_MODIFY');
                                        return false;
                                    }*/
                                    _this.delSubcontractor();
                                }
                            } ]
                        }, {
                            type : 'dropDownMenu',
                            text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.DATASHARING',
                            items : [ {
                                type : 'button',
                                text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.ADD_DATASHARING',
                                icon : 'add',
                                onClick : function() {
                                    /*let activeRow = Gikam.getComp('emc-schemeedit-audit-list-grid').getActivedRow();
                                    if (activeRow.ext$.status !== '1') {
                                        Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.CAN_NOT_MODIFY');
                                        return false;
                                    }*/
                                    _this.addDataSharing();
                                }
                            }, {
                                type : 'button',
                                text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.DEL_DATASHARING',
                                icon : 'remove-row',
                                onClick : function() {
                                    /*let activeRow = Gikam.getComp('emc-schemeedit-audit-list-grid').getActivedRow();
                                    if (activeRow.ext$.status !== '1') {
                                        Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.CAN_NOT_MODIFY');
                                        return false;
                                    }*/
                                    _this.delDataSharing();
                                }
                            } ]
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
            return Gikam.printf(emcProjectScheme.schemeUrl + '/{id}/proj-orders/queries', {
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
                url : emcProjectScheme.schemeUrl + '/page/update-cycle-and-frequency',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        var selectedIds = gridData.map(function(item) {
                            return {
                                id : item.id
                            }
                        });
                        Gikam.put(emcProjectScheme.schemeUrl + '/action/update-frequency', Gikam.getJsonWrapper({
                            cycle : data.cycle,
                            frequency : data.frequency
                        }, [ 'emcProjSchemeServiceImpl', gridData ])).done(function() {
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
                url : emcProjectScheme.schemeUrl + '/page/proj-order-batch-update',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        gridData.map(function(item) {
                            item.sampleNum = data.sampleNum;
                        });
                        Gikam.put(emcProjectScheme.schemeUrl + '/' + activeRow.id, Gikam.getJsonWrapper({}, [ 'emcProjOrderServiceImpl', gridData ])).done(function() {
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
                url : emcProjectScheme.schemeUrl + '/page/proj-order-update-item-group' + Gikam.param({
                    monitorTmplId : gridData[0].monitorTmplId,
                    projSchemeId : gridData[0].id
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
                                schemeName : item.schemeName,
                                status : '1',
                                source : 'schemeEdit'
                            }
                        });
                        var url = emcProjectScheme.schemeUrl + '/action/proj-orders/update-item-group';
                        Gikam.post(url, Gikam.getJsonWrapper({}, [ 'emcProjSchemeServiceImpl', gridData ], [ 'emcProjOrderServiceImpl', orderData ])).always(function() {
                            Gikam.getComp('emc-proj-order-edit-list-grid').refresh();
                            workspace.window.closeMask();
                            Gikam.alert('修改测试后请到采样准备页签修改！');
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
                url : emcProjectScheme.schemeUrl + '/page/update-index' + Gikam.param({
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
                        Gikam.put(emcProjectScheme.schemeUrl + '/' + gridData[0].id, Gikam.getJsonWrapper({}, [ 'emcProjSchemeServiceImpl', gridData ])).done(function() {
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
                url : emcProjectScheme.schemeUrl + '/page/copy-share',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        var url = emcProjectScheme.schemeUrl + '/action/copy';
                        // 复制出的方案增加标识
                        gridData.forEach(item => {
                            item.status = '1';
                            item.source = 'schemeEdit';
                        });
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
            url : emcProjectScheme.schemeUrl + '/page/select-monitoring-point' + Gikam.param({
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

                    var url = emcProjectScheme.schemeUrl + '/action/copy-point'
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
        var url = emcProjectScheme.schemeUrl;
        if (editMonitorInfo === '1') {// 不选择监测点
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.SELECT_MONITOR_TMPL',
                width : 1000,
                height : 580,
                url : emcProjectScheme.schemeUrl + '/page/select-monitor-tmpl' + Gikam.param({
                    editMonitorInfo : editMonitorInfo
                }),
                onAfterClose : function(tmlData) {
                    if (!Gikam.isEmpty(tmlData)) {
                        var chooseSchemeData = {
                            projId : projId,
                            monitorTmplId : tmlData[0].monitorTmplId,
                            sampleOrgId : sampleOrgId,
                            status : '1',
                            source : 'schemeEdit'
                        };

                        tmlData.forEach(item => {
                            item.status = '1';
                            item.source = 'schemeEdit';
                        });

                        workspace.window.showMask(true);
                        Gikam.post(url, Gikam.getJsonWrapper({}, [ 'emcProjSchemeServiceImpl', [ chooseSchemeData ] ], [ 'emcProjOrderServiceImpl', tmlData ])).done(function() {
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
                width : 800,
                height : 500,
                url : emcProjectScheme.schemeUrl + '/page/select-monitoring-point' + Gikam.param({
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
                            width : 1000,
                            height : 580,
                            url : emcProjectScheme.schemeUrl + '/page/select-monitor-tmpl' + Gikam.param({
                                editMonitorInfo : editMonitorInfo,
                                monitorPointCategoryId : pointData[0].monitorPointCategoryId
                            }),
                            onAfterClose : function(tmlData) {
                                if (!Gikam.isEmpty(tmlData)) {
                                    if (tmlData == 'PREVIOUS') {
                                        emcProjectScheme.editPage.addProj(projId, editMonitorInfo, testOrgId);
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
                                                sampleOrgId : item.sampleDepartmentId,
                                                status : '1',
                                                source : 'schemeEdit'
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
                                        Gikam.post(url, Gikam.getJsonWrapper({}, [ 'emcProjSchemeServiceImpl', chooseSchemeData ], [ 'emcProjOrderServiceImpl', chooseOrderData ])).done(function() {
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
            url : emcProjectScheme.schemeUrl + '/page/init-scheme' + Gikam.param({
                projId : projId,
                testOrgId : testOrgId
            }),
            onAfterClose : function(data) {
                Gikam.getComp('emc-schemeedit-audit-list-grid').refresh();
            }
        });
    },

    orderQuickExport : function() {
        var orderData = Gikam.getComp('emc-project-scheme-edit-list-order-grid').getData();
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
            Gikam.download(fileUrl);
        });

    },

    orderQuickImport : function() {
        var orderData = Gikam.getComp('emc-project-scheme-edit-list-order-grid').getData();
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
                            Gikam.download(result.url);
                        } else if (result.message) {
                            Gikam.alert(result.message);
                        }
                        Gikam.getComp('emc-project-scheme-edit-list-order-grid').refresh();
                    }).always(function() {
                        Gikam.window.closeMask();
                    });
            }
        });
    },

    /** 监测方案设置分包商 */
    addSubcontractor : function() {
        /*var schemeSelections = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
        var orderSelections = Gikam.getComp('emc-proj-order-edit-list-grid').getSelections();
        if (schemeSelections.length <= 0) {
            Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.CHOOSE_AT_LEAST_ONE_SCHEME');
            return;
        }
        if (orderSelections.length <= 0) {
            Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.CHOOSE_AT_LEAST_ONE_ORDER');
            return;
        }
        Gikam.create('modal', {
            width : 300,
            height : 300,
            title : Gikam.choose.getConfig('subcontractor').title,
            url : Gikam.choose.getConfig('subcontractor').url + Gikam.param({
                single : true,
            }),
            onAfterClose : function(data) {
                if (!Gikam.isEmpty(data)) {
                    workspace.window.showMask(true);
                    orderSelections.map(function(item) {
                        item.subcontractorId = data[0].id;
                    });
                    Gikam.post('/secure/emc/module/bp/project/proj-schemes/proj-orders/action/relevance-subcontractor-update', Gikam.getJsonWrapper({},[ 'emcProjSchemeServiceImpl', schemeSelections ], [ 'emcProjOrderServiceImpl', orderSelections ])).done(function() {
                    }).always(function() {
                        Gikam.getComp('emc-proj-order-edit-list-grid').refresh();
                        workspace.window.closeMask();
                    });
                }
            }
        });*/

        var selections = Gikam.getComp('emc-project-scheme-edit-folder-order-task-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var orders = Gikam.getComp('emc-project-scheme-edit-folder-order-grid').getSelections();
        var folders = Gikam.getComp('emc-project-scheme-edit-folder-list-grid').getSelections();
        Gikam.confirm('确认操作', '是否确认进行分包设置？', function() {
            workspace.window.showMask(true);
            Gikam.postText('/secure/emc/module/bp/bp/order-tasks/action/subpackage', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', selections ], [ 'emcOrderServiceImpl', orders], [ 'emcFolderServiceImpl', folders])).done(function() {
                Gikam.getComp('emc-project-scheme-edit-folder-order-task-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    beforeSamplingSubpackage : function() {
        var selections = Gikam.getComp('emc-project-scheme-edit-folder-order-task-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var orders = Gikam.getComp('emc-project-scheme-edit-folder-order-grid').getSelections();
        var folders = Gikam.getComp('emc-project-scheme-edit-folder-list-grid').getSelections();
        if (Gikam.isEmpty(orders)) {
            Gikam.alert('请至少选择一个样品');
            return;
        }
        if (Gikam.isEmpty(folders)) {
            Gikam.alert('请至少选择一个点位');
            return;
        }
        if(folders.length > 1){
            Gikam.alert('只能选择一个点位');
            return;
        }
        Gikam.confirm('确认操作', '是否确认进行分包设置？', function() {
            workspace.window.showMask(true);
            Gikam.postText('/secure/emc/module/bp/bp/order-tasks/action/subpackage', Gikam.getJsonWrapper({
                subpackageType : 'beforeSampling'
            }, [ 'emcOrderTaskServiceImpl', selections ], [ 'emcOrderServiceImpl', orders], [ 'emcFolderServiceImpl', folders])).done(function() {
            	//若除了该测试，其他测试都完成，分包后改变点位和项目状态
            	Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/after-delete-test', Gikam.getJsonWrapper({}, [ '', orders ]));
                Gikam.getComp('emc-project-scheme-edit-folder-order-task-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    afterSamplingSubpackage : function() {
        var selections = Gikam.getComp('emc-project-scheme-edit-folder-order-task-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var orders = Gikam.getComp('emc-project-scheme-edit-folder-order-grid').getSelections();
        var folders = Gikam.getComp('emc-project-scheme-edit-folder-list-grid').getSelections();
        if (Gikam.isEmpty(orders)) {
            Gikam.alert('请至少选择一个样品');
            return;
        }
        if (Gikam.isEmpty(folders)) {
            Gikam.alert('请至少选择一个点位');
            return;
        }
        if(folders.length > 1){
            Gikam.alert('只能选择一个点位');
            return;
        }
        Gikam.confirm('确认操作', '是否确认进行分包设置？', function() {
            workspace.window.showMask(true);
            Gikam.postText('/secure/emc/module/bp/bp/order-tasks/action/subpackage', Gikam.getJsonWrapper({
                subpackageType : 'orderSubpackage'
            }, [ 'emcOrderTaskServiceImpl', selections ], [ 'emcOrderServiceImpl', orders], [ 'emcFolderServiceImpl', folders])).done(function() {
            	//若除了该测试，其他测试都完成，分包后改变点位和项目状态
            	Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/after-delete-test', Gikam.getJsonWrapper({}, [ '', orders ]));
                Gikam.getComp('emc-project-scheme-edit-folder-order-task-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    /** 监测方案删除分包商 */
    delSubcontractor : function() {
        /*var _this = this;
        var schemeSelections = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
        var orderSelections = Gikam.getComp('emc-proj-order-edit-list-grid').getSelections();
        if (schemeSelections.length <= 0) {
            Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.CHOOSE_AT_LEAST_ONE_SCHEME');
            return;
        }
        if (orderSelections.length <= 0) {
            Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.CHOOSE_AT_LEAST_ONE_ORDER');
            return;
        }
        workspace.window.showMask(true);
        Gikam.post('/secure/emc/module/bp/project/proj-schemes/proj-orders/action/relevance-subcontractor-delete', Gikam.getJsonWrapper({},[ 'emcProjSchemeServiceImpl', schemeSelections ], [ 'emcProjOrderServiceImpl', orderSelections ])).done(function() {
        }).always(function() {
            Gikam.getComp('emc-proj-order-edit-list-grid').refresh();
            workspace.window.closeMask();
        });*/

        var selections = Gikam.getComp('emc-project-scheme-edit-folder-order-task-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var orders = Gikam.getComp('emc-project-scheme-edit-folder-order-grid').getSelections();
        var folders = Gikam.getComp('emc-project-scheme-edit-folder-list-grid').getSelections();
        if (Gikam.isEmpty(orders)) {
            Gikam.alert('请至少选择一个样品');
            return;
        }
        if (Gikam.isEmpty(folders)) {
            Gikam.alert('请至少选择一个点位');
            return;
        }
        Gikam.confirm('确认操作', '是否确认删除分包设置？', function() {
            workspace.window.showMask(true);
            Gikam.postText('/secure/emc/module/bp/bp/order-tasks/action/cancel-subpackage', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', selections ], [ 'emcOrderServiceImpl', orders], [ 'emcFolderServiceImpl', folders])).done(function() {
                Gikam.getComp('emc-project-scheme-edit-folder-order-task-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    folderBeforeSamplingSubpackage : function() {
        var selections = Gikam.getComp('emc-project-scheme-edit-folder-order-task-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var folders = Gikam.getComp('emc-project-scheme-edit-folder-list-grid').getSelections();
        if (Gikam.isEmpty(folders)) {
            Gikam.alert('请至少选择一个点位');
            return;
        }
        Gikam.confirm('确认操作', '是否确认进行分包设置？', function() {
            workspace.window.showMask(true);
            Gikam.postText('/secure/emc/module/bp/bp/order-tasks/action/subpackage', Gikam.getJsonWrapper({
                subpackageType : 'beforeSampling',
                byFolder :'1'
            }, [ 'emcOrderTaskServiceImpl', selections ], [ 'emcFolderServiceImpl', folders])).done(function() {
                Gikam.getComp('emc-project-scheme-edit-folder-order-task-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    folderAfterSamplingSubpackage : function() {
        var selections = Gikam.getComp('emc-project-scheme-edit-folder-order-task-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var folders = Gikam.getComp('emc-project-scheme-edit-folder-list-grid').getSelections();
        if (Gikam.isEmpty(folders)) {
            Gikam.alert('请至少选择一个点位');
            return;
        }
        Gikam.confirm('确认操作', '是否确认进行分包设置？', function() {
            workspace.window.showMask(true);
            Gikam.postText('/secure/emc/module/bp/bp/order-tasks/action/subpackage', Gikam.getJsonWrapper({
                subpackageType : 'orderSubpackage',
                byFolder :'1'
            }, [ 'emcOrderTaskServiceImpl', selections ], [ 'emcFolderServiceImpl', folders])).done(function() {
                Gikam.getComp('emc-project-scheme-edit-folder-order-task-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    /** 监测方案删除分包商 */
    folderDelSubcontractor : function() {

        var selections = Gikam.getComp('emc-project-scheme-edit-folder-order-task-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var folders = Gikam.getComp('emc-project-scheme-edit-folder-list-grid').getSelections();
        if (Gikam.isEmpty(folders)) {
            Gikam.alert('请至少选择一个点位');
            return;
        }
        Gikam.confirm('确认操作', '是否确认删除分包设置？', function() {
            workspace.window.showMask(true);
            Gikam.postText('/secure/emc/module/bp/bp/order-tasks/action/cancel-subpackage', Gikam.getJsonWrapper({
                byFolder :'1'
            }, [ 'emcOrderTaskServiceImpl', selections ], [ 'emcFolderServiceImpl', folders])).done(function() {
                Gikam.getComp('emc-project-scheme-edit-folder-order-task-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    addDataSharing : function() {
        var selections = Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').getSelections();
        if (selections.length > 0) {
            var activedRow = Gikam.getComp('emc-project-scheme-edit-list-grid').getActivedRow();
            selections.map(function(item) {
                item.dataSharing = 1;
            });
            workspace.window.showMask(true);
            Gikam.put(emcProjectScheme.baseUrl + '/' + activedRow.id, Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupServiceImpl', selections ])).done(function() {
                Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    delDataSharing : function() {
        var selections = Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').getSelections();
        if (selections.length > 0) {
            var activedRow = Gikam.getComp('emc-project-scheme-edit-list-grid').getActivedRow();
            selections.map(function(item) {
                item.dataSharing = 0;
            });
            workspace.window.showMask(true);
            Gikam.put(emcProjectScheme.baseUrl + '/' + activedRow.id, Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupServiceImpl', selections ])).done(function() {
                Gikam.getComp('emc-project-scheme-edit-list-order-item-group-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    insertDirectorRow : function() {
        var formData = Gikam.getComp('emc-project-scheme-edit-list-base-info-form').getData();
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('emp').title,
            url : Gikam.choose.getConfig('emp').url + Gikam.param({
                targetType : 'insertDirector',
                projectId : formData.id
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
                Gikam.getComp('emc-project-scheme-edit-list-director-grid').insert(emcProjectScheme.baseUrl + '/directors', chooseData);
            }
        });
    },

    deleteDirectorRow : function() {
        var url = emcProjectScheme.baseUrl + '/directors';
        Gikam.getComp('emc-project-scheme-edit-list-director-grid').deleteRows(url).done(function() {
        });
    },

    updateEditQcCategory : function() {
        var selections = Gikam.getComp('emc-project-scheme-edit-list-order-grid').getSelections();
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
                var qcCategoryCode = rows[0].qcCategoryId;
                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/edit-qc', Gikam.getJsonWrapper({}, [ 'emcOrderServiceImpl', selections])).done(function() {
                	if (qcCategoryCode.indexOf('P-DUP') != -1) {
                		Gikam.alert("修改现场平行后请更改父样编号");
                	}
                	Gikam.getComp('emc-project-scheme-edit-list-order-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    emptyEditQcCategory : function() {
        var selections = Gikam.getComp('emc-project-scheme-edit-list-order-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        selections.forEach(function(item) {
            item.qcCategoryId = "";
            item.qcCategoryCode = "N/A";
            item.qcName = "N/A";
        });

        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/edit-qc', Gikam.getJsonWrapper({}, [ 'emcOrderServiceImpl', selections])).done(function() {
            Gikam.getComp('emc-project-scheme-edit-list-order-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    completeFolderForOrder : function() {
        //选中的样品
        var selections = Gikam.getComp('emc-project-scheme-edit-list-order-grid').getSelections();
        var url = emcProjectScheme.schemeUrl + '/action/complete-folder-for-order';
        //将所选样品的点位、项目状态改变
        Gikam.postText(url, Gikam.getJsonWrapper({
        }, [ 'emcOrderServiceImpl', selections ] )).done(function() {
            Gikam.alert('设置完成');
        });
    },

    cancelFolderForOrder : function() {
        //选中的样品
        var selections = Gikam.getComp('emc-project-scheme-edit-list-order-grid').getSelections();
        var url = emcProjectScheme.schemeUrl + '/action/cancel-folder-for-order';
        //将所选样品的点位、项目状态改变
        Gikam.postText(url, Gikam.getJsonWrapper({
        }, [ 'emcOrderServiceImpl', selections ] )).done(function() {
            Gikam.alert('取消完成');
        });
    },

    completeFolder : function() {
        //选中的样品
        var selections = Gikam.getComp('emc-project-scheme-edit-folder-list-grid').getSelections();
        //将所选样品的点位、项目状态改变
        var url = emcProjectScheme.schemeUrl + '/action/complete-folder';
        //将所选样品的点位、项目状态改变
        Gikam.postText(url, Gikam.getJsonWrapper({
        }, [ 'emcFolderServiceImpl', selections ] )).done(function() {
            Gikam.alert('设置完成');
        });
    },

    cancelFolder : function() {
        //选中的样品
        var selections = Gikam.getComp('emc-project-scheme-edit-folder-list-grid').getSelections();
        //将所选样品的点位、项目状态改变
        var url = emcProjectScheme.schemeUrl + '/action/cancel-folder';
        //将所选样品的点位、项目状态改变
        Gikam.postText(url, Gikam.getJsonWrapper({
        }, [ 'emcFolderServiceImpl', selections ] )).done(function() {
            Gikam.alert('取消完成');
        });
    },



    onPsSubpackageActive : function(row) {
        //展示分包单下面的样品信息
        var _this = this;
        if (row) {
            Gikam.getAsyncComp('emc-proj-edit-subpackage-base-info-form').done(function (form) {
                form.refresh({
                    url : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/{id}', {
                        id : row.id
                    }),
                });
            });
            Gikam.getComp('emc-proj-edit-subpackage-order-grid').refresh({
                url : Gikam.printf(emcProjectScheme.baseUrl + '/{id}/orders/queries', {
                    id : row.projId
                })
            });
        }

    },

    onPsSampleSubpackageActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getAsyncComp('emc-proj-edit-sample-subpackage-base-info-form').done(function (form) {
                form.refresh({
                    url : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/{id}', {
                        id : row.id
                    }),
                });
            });
            Gikam.getComp('emc-proj-edit-subpackage-folder-grid').refresh({
                url : emcProjectScheme.schemeUrl + '/queries/raw',
                requestData: {
                    projId_SEQ : row.projId,
                    subpackageflag_SEQ : '1'
                }
            });
        }

    },

    onSubpackageOrderInfoActive : function(row) {
        //展示分包单样品下的测试信息
        var _this = this;
        var selections = Gikam.getComp('emc-proj-edit-subpackage-info-grid').getSelections();
        if (row) {
            Gikam.getComp('emc-proj-edit-subpackage-order-task-grid').refresh({
                url : IFM_CONTEXT + "/secure/emc/module/bp/subpackages/tasks/queries",
                requestData: {
                    orderId_SEQ : row.id,
                    subpackageId_SEQ : selections[0].id,
                }
            });
        }
    },

    onSampleSubpackageInfoActive : function(row) {
        //展示分包单下面的点位信息
        var _this = this;
        if (row) {
            Gikam.getComp('emc-proj-edit-subpackage-folder-grid').refresh({
                url : emcProject.schemeUrl + '/queries/raw',
                requestData: {
                    projId_SEQ : row.projId,
                    subpackageflag_SEQ : '1'
                }
            });
        }
    },

    onSubpackageFolderInfoActive : function(row) {
        //展示分包单样品下的测试信息
        var _this = this;
        var selections = Gikam.getComp('emc-proj-edit-subpackage-info-grid').getSelections();
        if (row) {
            Gikam.getComp('emc-proj-edit-subpackage-sample-order-task-grid').refresh({
                url : IFM_CONTEXT + "/secure/emc/module/bp/subpackages/tasks/queries",
                requestData: {
                    projSchemeId_SEQ : row.id,
                    subpackageId_SEQ : selections[0].id,
                }
            });
        }

    },

    onSampleSubpackageFolderInfoActive : function(row) {
        //展示分包单样品下的测试信息
        var _this = this;
        var selections = Gikam.getComp('emc-proj-edit-sample-subpackage-edit-grid').getSelections();
        if (row) {
            Gikam.getComp('emc-proj-edit-subpackage-sample-order-task-grid').refresh({
                url : IFM_CONTEXT + "/secure/emc/module/bp/subpackages/tasks/queries",
                requestData: {
                    projSchemeId_SEQ : row.id,
                    subpackageId_SEQ : selections[0].id,
                }
            });
        }
    },

    getSubpackageButton : function() {
        var _this = this;
        return  [ {
            type : 'button',
            text : '添加分包单',
            icon : 'add',
            onClick : function() {
                //区分采样和送样的
                var proj = Gikam.getComp('emc-project-scheme-edit-list-grid').getActivedRow();
                    //送样进入样品分析项组选择页面
                    Gikam.create('modal', {
                        title : '选择分包测试',
                        url : emcProjectScheme.baseUrl + '/page/order-itemgroup-choose-list'+ Gikam.param({
                            projId : proj.id,
                            subcontractorid_IN: ',',
                            SUBPACKAGETYPE_SEQ :'orderSubpackage',
                            //subpackageCategory_SEQ :'orderSubpackage',
                        }),
                        onAfterClose : function(orderItemGroup) {
                            let orderItemGroups = Object.values(orderItemGroup);
                            let orderItemGroupArr = Object.values(orderItemGroups[0]);
                            Gikam.confirm('操作提示', '是否确定创建分包单？', function() {
                                workspace.window.showMask(true);
                                if (Gikam.isEmpty(proj) || Gikam.isEmpty(orderItemGroup)) {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                    return;
                                }
                                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/create-proj-package', Gikam.getJsonWrapper({
                                    subpackageCategory : 'orderSubpackage'
                                }, [ 'emcProjectServiceImpl', [ proj ] ], [ 'emcOrderItemGroupServiceImpl', orderItemGroupArr ])).done(function() {
                                    Gikam.getComp('emc-proj-edit-sample-subpackage-edit-grid').refresh();
                                }).always(function() {
                                    workspace.window.closeMask();
                                    Gikam.getComp('emc-proj-edit-subpackage-info-grid').refresh();
                                });
                            });

                        }
                    });



            }
        },{
            type : 'button',
            text : '提交',
            icon : 'submit',
            onClick : function() {
                var selections = Gikam.getComp('emc-proj-edit-subpackage-info-grid').getSelections();
                if (Gikam.isEmpty(selections)) {
                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                    return;
                }

                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/submit-to-subpackage', Gikam.getJsonWrapper({
                }, [ 'emcSubpackageServiceImpl', selections ])).done(function() {
                    Gikam.getComp('emc-proj-edit-subpackage-info-grid').refresh();
                });
            }
        },{
            type : 'button',
            text : '完成',
            icon : 'edit',
            onClick : function() {
                var selections = Gikam.getComp('emc-proj-edit-subpackage-info-grid').getSelections();
                let hasNoAssigned = selections.some(item => item.status !='1');
                if (hasNoAssigned) {
                    Gikam.alert('所选分包单不是委托状态，不能完成！');
                    return;
                }else{
                    Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/complete-package',
                        Gikam.getJsonWrapper({}, [ 'emcSubpackageServiceImpl', selections ],)).done(function() {
                        Gikam.getComp('emc-proj-edit-subpackage-info-grid').refresh();
                    })
                }
            }
        },{
            type : 'button',
            text : '删除',
            icon : 'remove',
            onClick : function() {
                //删除未委托的分包单
                var selections = Gikam.getComp('emc-proj-edit-subpackage-info-grid').getSelections();
                if (Gikam.isEmpty(selections)) {
                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                    return;
                }
                let hasAssigned = selections.some(item => item.status === '1');
                if (hasAssigned) {
                    Gikam.alert('所选分包单已经被委托，请先撤销再删除!');
                    return;
                }
                Gikam.getComp('emc-proj-edit-subpackage-info-grid').deleteRows(IFM_CONTEXT + '/secure/emc/module/bp/subpackages');
            }
        },{
            type : 'button',
            text : '撤回',
            icon : 'cancel',
            onClick : function() {
                var selections = Gikam.getComp('emc-proj-edit-subpackage-info-grid').getSelections();
                if (Gikam.isEmpty(selections)) {
                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                    return;
                }
                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/undo-subpackage', Gikam.getJsonWrapper({
                }, [ 'emcSubpackageServiceImpl', selections ])).done(function() {
                    Gikam.getComp('emc-proj-edit-subpackage-info-grid').refresh();
                });

            }
        },{
            type : 'button',
            text : '查看分包单',
            icon : 'select',
            onClick : function() {
                Gikam.viewReport('subpackageProject', Gikam.getComp('emc-proj-edit-subpackage-info-grid').getSelections());
            }
        } ]
    },

    getSampleSubpackageButton : function() {
        var _this = this;
        return  [ {
            type : 'button',
            text : '添加',
            icon : 'add',
            onClick : function() {
                //区分采样和送样的
                var proj = Gikam.getComp('emc-project-scheme-edit-list-grid').getActivedRow();
                    //采样进入点位测试选择页面
                    Gikam.create('modal', {
                        title : '选择分包测试',
                        url : emcProjectScheme.baseUrl + '/page/proj-order-choose-list' + Gikam.param({
                            projId : proj.id,
                            subcontractorid_IN: ',',
                            SUBPACKAGETYPE_SEQ:'beforeSampling',
                            //subpackageCategory_SEQ :'beforeSampling',
                        }),
                        onAfterClose : function(projOrder) {
                            let projOrders = Object.values(projOrder);
                            let projOrderArr = Object.values(projOrders[0]);
                            Gikam.confirm('操作提示', '是否确定创建分包单？', function() {
                                workspace.window.showMask(true);
                                if (Gikam.isEmpty(proj) || Gikam.isEmpty(projOrder)) {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                    return;
                                }
                                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/create-proj-package',
                                    Gikam.getJsonWrapper({
                                        subpackageCategory : 'beforeSampling'
                                    }, [ 'emcProjectServiceImpl', [ proj ] ], [ 'emcProjOrderServiceImpl', projOrderArr ])).done(function() {
                                    Gikam.getComp('emc-proj-edit-sample-subpackage-edit-grid').refresh();
                                }).always(function() {
                                    workspace.window.closeMask();
                                    Gikam.getComp('emc-proj-edit-sample-subpackage-edit-grid').refresh();
                                });
                            });

                        }
                    });



            }
        },{
            type : 'button',
            text : '提交',
            icon : 'submit',
            onClick : function() {
                var selections = Gikam.getComp('emc-proj-edit-sample-subpackage-edit-grid').getSelections();
                if (Gikam.isEmpty(selections)) {
                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                    return;
                }

                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/submit-to-subpackage', Gikam.getJsonWrapper({
                }, [ 'emcSubpackageServiceImpl', selections ])).done(function() {
                    Gikam.getComp('emc-proj-edit-sample-subpackage-edit-grid').refresh();
                });

            }
        },{
            type : 'button',
            text : '完成',
            icon : 'edit',
            onClick : function() {
                var selections = Gikam.getComp('emc-proj-edit-sample-subpackage-edit-grid').getSelections();
                let hasNoAssigned = selections.some(item => item.status !='1');
                if (hasNoAssigned) {
                    Gikam.alert('所选分包单不是委托状态，不能完成！');
                    return;
                }else{
                    Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/complete-package',
                        Gikam.getJsonWrapper({}, [ 'emcSubpackageServiceImpl', selections ],)).done(function() {
                        Gikam.getComp('emc-proj-edit-sample-subpackage-edit-grid').refresh();
                    })
                }
            }
        },{
            type : 'button',
            text : '删除',
            icon : 'remove',
            onClick : function() {
                //删除未委托的分包单
                var selections = Gikam.getComp('emc-proj-edit-sample-subpackage-edit-grid').getSelections();
                if (Gikam.isEmpty(selections)) {
                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                    return;
                }
                let hasAssigned = selections.some(item => item.status === '1');
                if (hasAssigned) {
                    Gikam.alert('所选分包单已经被委托，请先撤销再删除!');
                    return;
                }
                Gikam.getComp('emc-proj-edit-sample-subpackage-edit-grid').deleteRows(IFM_CONTEXT + '/secure/emc/module/bp/subpackages');
            }
        },{
            type : 'button',
            text : '撤回',
            icon : 'cancel',
            onClick : function() {
                //撤回发个其他公司的分包单
                var selections = Gikam.getComp('emc-proj-edit-sample-subpackage-edit-grid').getSelections();
                if (Gikam.isEmpty(selections)) {
                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                    return;
                }
                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/undo-subpackage', Gikam.getJsonWrapper({
                }, [ 'emcSubpackageServiceImpl', selections ])).done(function() {
                    Gikam.getComp('emc-proj-edit-sample-subpackage-edit-grid').refresh();
                });

            }
        },{
            type : 'button',
            text : '查看分包单',
            icon : 'select',
            onClick : function() {
                Gikam.viewReport('subpackageProject', Gikam.getComp('emc-proj-edit-sample-subpackage-edit-grid').getSelections());
            }
        } ]
    },


    getProjSchemeSubpackageButton : function() {
        var _this = this;
        return  [{
            type : 'button',
            text : '编辑测试',
            icon : 'edit',
            onClick : function() {
                //区分采样和送样的
                var subpackage = Gikam.getComp('emc-proj-edit-sample-subpackage-edit-grid').getSelections();
                var selections = Gikam.getComp('emc-proj-edit-subpackage-folder-grid').getSelections();

                //采样进入点位测试选择页面
                Gikam.create('modal', {
                    title : '选择分包测试',
                    url : emcProjectScheme.baseUrl + '/page/proj-order-order-choose-list' + Gikam.param({
                        projSchemeId : selections[0].id,
                        subcontractorid_IN: ',',
                        SUBPACKAGETYPE_SEQ:'beforeSampling',
                    }),
                    onAfterClose : function(projOrder) {
                        let projOrders = Object.values(projOrder);
                        let projOrderArr = Object.values(projOrders[0]);
                        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/add-to', Gikam.getJsonWrapper({}, [ '', subpackage ], [ '', selections ], [ '', projOrderArr ])).done(function() {
                            Gikam.getComp('emc-proj-edit-subpackage-folder-grid').refresh();
                        })

                    }
                });


            }
        }]
    },

    getOrderSubpackageButton : function() {
        var _this = this;
        return  [{
            type : 'button',
            text : '编辑测试',
            icon : 'edit',
            onClick : function() {
                var subpackage = Gikam.getComp('emc-proj-edit-subpackage-info-grid').getSelections();
                var selections = Gikam.getComp('emc-proj-edit-subpackage-order-grid').getSelections();
                //送样进入样品分析项组选择页面
                Gikam.create('modal', {
                    title : '选择分包测试',
                    url : emcProjectScheme.baseUrl + '/page/order-itemgroup-order-choose-list'+ Gikam.param({
                        orderId : selections[0].id,
                        subcontractorid_IN: ',',
                        SUBPACKAGETYPE_SEQ:'orderSubpackage',
                    }),
                    onAfterClose : function(orderItemGroup) {
                        let orderItemGroups = Object.values(orderItemGroup);
                        let orderItemGroupArr = Object.values(orderItemGroups[0]);
                        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/add-to', Gikam.getJsonWrapper({}, [ '', subpackage ], [ '', selections ], [ '', orderItemGroupArr ])).done(function() {
                            Gikam.getComp('emc-proj-edit-subpackage-order-grid').refresh();
                        })
                    }
                });

            }
        }]
    },


    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-project-scheme-edit-list-layout',
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

    init : function() {
        this.param = {};
        this.create();
    }
};
