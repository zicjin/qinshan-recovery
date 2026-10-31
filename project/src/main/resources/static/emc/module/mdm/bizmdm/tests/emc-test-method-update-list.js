emcTestMethod.updatePage = {
    getTestGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-test-method-update-list-grid',
            url : emcTestMethod.baseUrl + '/queries/raw',
            columns : emcTestMethod.getEditListColumns(),
            service : 'emcTestMethodServiceImpl',
            checkOnActive : true,
            requestData : {
                testId_IN : _this.param.testId,
                methodId_SEQ : _this.param.methodId
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-test-item-list-grid', 'emc-test-equipt-list-grid' ]);
                }
            },
            onRowActive : function(index, row) {
                _this.onTestActive(row);
            }
        }
    },

    // 计算公式
    popScript : function(index) {
        _this = this;

        var grid = Gikam.getComp('emc-test-item-list-grid');

        var row = grid.getData()[index];

        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BIZMDM.TESTS.MODAL.SCRIPT',
            width : 600,
            height : 600,
            url : emcTestMethod.baseUrl + '/page/script-detail' + Gikam.param({
                id : row.id,
                testId : row.ext$.testid,
                testMethodId : row.testMethodId,
                testName : row.ext$.testname,
                methodId : row.ext$.methodid,
                methodName : row.ext$.methodname,
            }),
            onAfterClose : function(script) {
                grid.refresh();
            }
        });
    },

    // 配置结果
    editResult : function(index) {
        var grid = Gikam.getComp('emc-test-item-list-grid');
        var row = grid.getData()[index];
        if (row.resultCategory != 'select') {
            Gikam.alert('EMC.MODULE.MDM.BIZMDM.TESTS.TIP.RESULTCATEGORY_ERROR');
            return;
        }

        Gikam.create('modal', {
            width : 400,
            height : 400,
            url : emcTestMethod.baseUrl + '/page/item-result-list' + Gikam.param({
                itemId : row.id
            }),
            onAfterClose : function() {
                grid.refresh();
            }
        });
    },

    // 配置QC指标和分段修约
    editQcIndexAndRevision : function(index) {
        var grid = Gikam.getComp('emc-test-item-list-grid');
        var row = grid.getData()[index];

        Gikam.create('modal', {
            width : 1200,
            height : 600,
            url : emcTestMethod.baseUrl + '/page/item-qcindex-revision-list' + Gikam.param({
                itemId : row.id
            }),
            onAfterClose : function() {
                grid.refresh();
            }
        });
    },

    getOtherGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.XL,
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TAB.TEST_ITEM',
                items : [ {
                    type : 'grid',
                    id : 'emc-test-item-list-grid',
                    service : 'emcItemServiceImpl',
                    url : _this.getItemUrl(),
                    columns : emcTestMethod.getItemUpdateListColumns(),
                    deleteFormatter : function(row) {
                        return row.itemName || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var formData = Gikam.getComp('emc-test-method-update-list-grid').getActivedRow();
                            if (Gikam.isEmpty(formData.id)) {
                                Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                return;
                            }
                            Gikam.preInsert({
                                modalTitle : 'EMC.MODULE.MDM.BIZMDM.TESTS.TAB.TEST_ITEM_ADD',
                                fields : emcTestMethod.getItemUpdateFormFields(),
                                url : emcTestMethod.baseUrl + '/' + formData.id + '/items',
                                param : {
                                    id : formData.id
                                },
                            }).done(function(id) {
                                _this.loadItemDetailPage(id);
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteItemRow();
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.MDM.BIZMDM.TESTS.BUTTON.COPY',
                        icon : 'copy',
                        onClick : function() {
                            _this.copyItem();
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.MDM.BIZMDM.TESTS.BUTTON.COPYTO',
                        icon : 'copy',
                        onClick : function() {
                            _this.copyItemTo();
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.MDM.BIZMDM.TESTS.BUTTON.EDITBATCH',
                        icon : 'add',
                        onClick : function() {
                            _this.editBatchItem();
                        }
                    } ]
                } ]
            }, {
                title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TAB.TEST_EQUIPT',
                items : [ {
                    type : 'grid',
                    id : 'emc-test-equipt-list-grid',
                    url : _this.getEquiptUrl(),
                    columns : emcTestMethod.getEquiptListColumns(),
                    service : 'emcEquiptTestMethodServiceImpl',
                    deleteFormatter : function(row) {
                        return row.ext$.equiptname || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            _this.insertEquiptRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteEquiptRow();
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.MDM.BIZMDM.TESTS.BUTTON.COPYTO',
                        icon : 'copy',
                        onClick : function() {
                            _this.copyEquiptTo();
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.MDM.BIZMDM.TESTS.BUTTON.DEFAULT',
                        icon : 'default',
                        onClick : function() {
                            _this.setDefaultEquiptRow();
                        }
                    } ]
                } ]
            }, {
                title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TAB.DEFAULT_QC',
                items : [ {
                    type : 'grid',
                    fill : true,
                    id : 'emc-test-method-default-qc-list-grid',
                    columns : emcTestMethod.getDefaultQcListColumns(),
                    service : 'emcTestMethodDefaultQcServiceImpl',
                    deleteFormatter : function(row) {
                        return row.qcName || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertDefaultQc();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteDefaultQc();
                        }
                    } ]
                } ]
            } ]
        }
    },

    deleteItemRow : function() {
        var formData = Gikam.getComp('emc-test-method-update-list-grid').getActivedRow();
        var url = Gikam.printf(emcTestMethod.baseUrl + '/{id}/items', {
            id : formData.id
        });
        Gikam.getComp('emc-test-item-list-grid').deleteRows(url);
    },

    copyItem : function() {
        debugger
        var selections = Gikam.getComp('emc-test-item-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            width : 400,
            height : 300,
            title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TITLE.COPY_TEST_METHOD_ITEM',
            url : emcTestMethod.baseUrl + '/page/item-copy' + Gikam.param({
                id : selections[0].id,
                itemName : selections[0].itemName,
                itemAliasName : encodeURIComponent(selections[0].itemAliasName)
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-test-item-list-grid').refresh();
            }
        });
    },

    copyItemTo : function() {
        var formData = Gikam.getComp('emc-test-method-update-list-grid').getActivedRow();
        var selections = Gikam.getComp('emc-test-item-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TITLE.COPY_TEST_METHOD_ITEM',
            url : emcTestMethod.baseUrl + '/page/item-copy-to',
            width : '40%',
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                Gikam.getLastModal().window.showMask(true);
                var url = emcTestMethod.baseUrl + '/items/action/copyto';
                Gikam.post(url, Gikam.getJsonWrapper({}, [ 'emcTestMethodServiceImpl', rows ], [ 'emcItemServiceImpl', selections ])).always(function() {
                    Gikam.getLastModal().window.closeMask();
                });
            }
        });
    },

    editBatchItem : function() {
        var selections = Gikam.getComp('emc-test-item-list-grid').getSelections();
        if (selections.length > 0) {
            Gikam.create('modal', {
                width : 600,
                height : 600,
                title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TITLE.EDITBATCH_TEST_METHOD_ITEM',
                url : emcTestMethod.baseUrl + '/page/item-batch',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        Gikam.getLastModal().window.showMask(true);
                        selections.map(function(item) {
                            item.unitName = data.unitName;
                            item.revisionRule = data.revisionRule;
                            item.analysisCategory = data.analysisCategory;
                            item.originalRecordDisplay = data.originalRecordDisplay;
                            item.reportDisplay = data.reportDisplay;
                            item.param = data.param;
                            item.qcCategoryId = data.qcCategoryId;
                            item.qcCategoryName = data.qcCategoryName;
                        });
                        Gikam.put(emcTestMethod.baseUrl + '/' + selections[0].id, Gikam.getJsonWrapper({}, [ 'emcItemServiceImpl', selections ])).done(function() {
                            Gikam.getComp('emc-test-item-list-grid').refresh();
                        }).always(function() {
                            Gikam.getLastModal().window.closeMask();
                        });
                    }

                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    copyEquiptTo : function() {
        var formData = Gikam.getComp('emc-test-method-update-list-grid').getActivedRow();
        var selections = Gikam.getComp('emc-test-equipt-list-grid').getSelections();
        if (selections.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : Gikam.choose.getConfig('testMethod').title,
            url : Gikam.choose.getConfig('testMethod').url + Gikam.param({

            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                Gikam.getLastModal().window.showMask(true);
                var chooseData = rows.map(function(item) {
                    return {
                        id : selections[0].id,
                        testMethodId : item.id
                    }
                });
                url = emcTestMethod.baseUrl + '/equipts/action/copyto';
                Gikam.post(url, Gikam.getJsonWrapper({}, [ '', chooseData ])).always(function() {
                    Gikam.getLastModal().window.closeMask();
                });
            }
        });
    },

    insertEquiptRow : function() {
        var formData = Gikam.getComp('emc-test-method-update-list-grid').getActivedRow();
        if (Gikam.isEmpty(formData.id)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var url = Gikam.printf(emcTestMethod.baseUrl + '/{id}/equipts', {
            id : formData.id
        });

        Gikam.create('modal', {
            title : Gikam.choose.getConfig('equipt').title,
            url : Gikam.choose.getConfig('equipt').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_EMC_EQUIPT_TEST_METHOD',
                targetEquiptColumn : 'EQUIPTID',
                targetFilterColumn : 'TESTMETHODID',
                targetFilterValue : formData.id
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        testMethodId : formData.id,
                        equiptId : item.id
                    }
                });
                Gikam.getComp('emc-test-equipt-list-grid').insert(url, chooseData);
            }
        });
    },

    deleteEquiptRow : function() {
        var formData = Gikam.getComp('emc-test-method-update-list-grid').getActivedRow();
        var url = Gikam.printf(emcTestMethod.baseUrl + '/{id}/equipts', {
            id : formData.id
        });
        Gikam.getComp('emc-test-equipt-list-grid').deleteRows(url);
    },

    setDefaultEquiptRow : function() {
        var selections = Gikam.getComp('emc-test-equipt-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var formData = Gikam.getComp('emc-test-method-update-list-grid').getActivedRow();
        var url = Gikam.printf(emcTestMethod.baseUrl + '/{id}/equipts/action/set-default', {
            id : formData.id
        });
        Gikam.getLastModal().window.showMask(true);
        Gikam.put(url, Gikam.getJsonWrapper({}, [ '', selections ])).done(function() {
            Gikam.getComp('emc-test-equipt-list-grid').refresh();
        }).always(function() {
            Gikam.getLastModal().window.closeMask();
        });
    },

    /** 新增默认QC */
    insertDefaultQc : function() {
        var selections = Gikam.getComp('emc-test-method-update-list-grid').getSelections();
        if (selections.length < 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var testMethod = Gikam.getComp('emc-test-method-update-list-grid').getActivedRow();

        Gikam.create('modal', {
            width : 500,
            title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.ADD_QC',
            url : '/secure/emc/module/bp/bp/order-tasks/page/run-qc-category' + Gikam.param({
                USECATEGORY_SEQ : 'lab'
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows[0]) || Gikam.isEmpty(rows)) {
                    return;
                }
                var url = Gikam.printf(emcTestMethod.baseUrl + '/{id}/default-qcs', {
                    id : testMethod.id
                });
                var data = [];

                if (selections.length >1) {
                    for (var selection of selections) {
                        url = Gikam.printf(emcTestMethod.baseUrl + '/{id}/default-qcs', {
                            id : selection.id
                        });
                        data = rows[0].map(item => {
                            return {
                                testMethodId : selection.id,
                                qcCategoryId : item.id,
                                qcCategoryCode : item.qcCategoryId,
                                qcName : item.qcName,
                                addNum : item.addNum
                            }
                        });
                        Gikam.getComp('emc-test-method-default-qc-list-grid').insert(url, data);
                    }
                } else {
                    data = rows[0].map(item => {
                        return {
                            testMethodId : testMethod.id,
                            qcCategoryId : item.id,
                            qcCategoryCode : item.qcCategoryId,
                            qcName : item.qcName,
                            addNum : item.addNum,
                            orgId : workspace.user.ext$.parentorgid
                        }
                    });
                    Gikam.getComp('emc-test-method-default-qc-list-grid').insert(url, data);
                }
            }
        });
    },

    /** 删除默认QC */
    deleteDefaultQc : function() {
        var testMethod = Gikam.getComp('emc-test-method-update-list-grid').getActivedRow();
        var url = Gikam.printf(emcTestMethod.baseUrl + '/{id}/default-qcs', {
            id : testMethod.id
        });
        Gikam.getComp('emc-test-method-default-qc-list-grid').deleteRows(url);
    },

    getItemUrl : function(testMethodId) {
        if (testMethodId) {
            return Gikam.printf(emcTestMethod.baseUrl + '/{id}/items/queries', {
                id : testMethodId
            })
        }
    },

    getEquiptUrl : function(testMethodId) {
        if (testMethodId) {
            return Gikam.printf(emcTestMethod.baseUrl + '/{id}/equipts/queries', {
                id : testMethodId
            })
        }
    },

    onTestActive : function(row) {
        var _this = this;
        Gikam.getAsyncComp('emc-test-item-list-grid').done(function(grid) {
            grid.refresh({
                url : _this.getItemUrl(row.id)
            });
        });
        Gikam.getAsyncComp('emc-test-equipt-list-grid').done(function(grid) {
            grid.refresh({
                url : _this.getEquiptUrl(row.id)
            });
        });

        Gikam.getAsyncComp('emc-test-method-default-qc-list-grid').done(function(grid) {
            grid.refresh({
                url : Gikam.printf(emcTestMethod.baseUrl + '/{id}/default-qcs/queries', {
                    id : row.id
                }),
                requestData : {
                    orgId_SEQ : workspace.user.ext$.parentorgid
                }
            });
        });
    },

    createPage : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-test-method-update-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            north : {
                height : "30%",
                items : [ _this.getTestGridParam() ]
            },
            center : {
                items : [ _this.getOtherGridParam() ]
            }
        });
        return this;
    },

    loadItemDetailPage : function(id) {
        var _this = this;
        Gikam.create('modal', {
            height : 430,
            url : emcTestMethod.baseUrl + '/page/item-update-detail' + Gikam.param({
                id : id
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-test-item-list-grid').refresh();
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    },
};
