// 测试管理
emcTestMethod.editPage = {
    getTestGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-test-method-edit-list-grid',
            url : emcTestMethod.baseUrl + '/queries/raw',
            columns : emcTestMethod.getEditListColumns(),
            service : 'emcTestMethodServiceImpl',
            checkOnActive : true,
            filterOpen : true,
            initRequest : true,
            page : true,
            pageSize : 100,
            onRendered : function() {
                this.hideColumns([ 'conrolledNo','analysisRecordName','analysisFee','sampleFee' ]);
            },
            deleteFormatter : function(row) {
                return row.ext$.testname || row.id;
            },
            activateFormatter : function(row) {
                return row.testName || row.id;
            },
            deactivateFormatter : function(row) {
                return row.testName || row.id;
            },
            onLoadSuccess : function(data) {
                this.setOptions({
                    requestData : {
                        'scheme_only' : null
                    }
                });

                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-test-item-list-grid', 'emc-test-item-group-list-grid', 'emc-test-group-item-list-grid', 'emc-test-equipt-list-grid', 'emc-test-order-category-list-grid' ]);
                }
            },
            onRowActive : function(index, row) {
                _this.onTestActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.BIZMDM.TESTS.TAB.TEST_ADD',
                        fields : emcTestMethod.getBaseInfoFormFields(),
                        url : emcTestMethod.baseUrl
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.MDM.BIZMDM.TESTS.BUTTON.COPYMETHOD',
                icon : 'copy',
                onClick : function() {
                    _this.copyTestMethod();
                }
            }/*, {
                type : 'button',
                text : '根据测试类型复制测试',
                icon : 'copy',
                onClick : function() {
                    _this.copyTestMethodByCategory();
                }
            }*/, {
            //     type : 'button',
            //     text : 'GIKAM.BUTTON.DELETE',
            //     icon : 'remove-row',
            //     onClick : function() {
            //         var selections = Gikam.getComp('emc-test-method-edit-list-grid').getSelections();
            //         if (selections.length > 0) {
            //             var url = emcTestMethod.baseUrl + '/action/check-used'
            //             Gikam.post(url, Gikam.getJsonWrapper({}, [ '', selections ])).done(function(data) {
            //                 if (!Gikam.isEmpty(data)) {
            //                     Gikam.confirm('GIKAM.TIP.CONFIRM_TO_DELETE', 'EMC.MODULE.MDM.BIZMDM.TESTS.TIP.TEST_METHOD_CHECK_USED', function() {
            //                         Gikam.getComp('emc-test-method-edit-list-grid').deleteRows(emcTestMethod.baseUrl);
            //                     })
            //                 } else {
            //                     Gikam.getComp('emc-test-method-edit-list-grid').deleteRows(emcTestMethod.baseUrl);
            //                 }
            //             });
            //         } else {
            //             Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            //         }
            //     }
            // }, {
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function() {
                    Gikam.getComp('emc-test-method-edit-list-grid').activateRows(emcTestMethod.baseUrl + '/action/activate');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DEACTIVE',
                icon : 'disable',
                onClick : function() {
                    _this.deactive();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.MDM.BIZMDM.TESTS.BUTTON.SCRIPT_FILE',
                icon : 'upload',
                onClick : function() {
                    _this.uploadScriptFile();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.MDM.BIZMDM.TESTS.BUTTON.SCRIPT_FILE_COPYTO',
                icon : 'upload',
                onClick : function () {
                    _this.scriptFileCopyTo();
                }
            }, /*{
                type : 'button',
                text : 'EMC.MODULE.MDM.BIZMDM.TESTS.BUTTON.ADD_INPUTUSER',
                icon : 'save',
                onClick : function() {
                    _this.batchAddInputUser();
                }
            },*/ {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    var grid = Gikam.getComp('emc-test-method-edit-list-grid');
                    Gikam.viewAudit('T_EMC_TEST_METHOD', grid.getSelections());
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.EXCEL_EXPORT',
                icon : 'edit',
                onClick : function() {
                    Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}', {
                        templateCode : 'emcTestMethodExportTemplate',
                        service : 'emcTestMethodExportTemplate'
                    })).done(function(fileUri) {
                        var fileUrl = IFM_CONTEXT + fileUri;
                        Gikam.emcDownloadFile(fileUrl);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.EXCEL_IMPORT',
                icon : 'edit',
                onClick : function() {
                    Gikam.importExcel({
                        url : IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'emcTestMethodImportFileFunction',
                        dbTable : 'T_EMC_TEST_METHOD',
                        onImportSuccess : function(files) {
                            Gikam.getComp('emc-test-method-edit-list-grid').refresh();
                        }
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BATCH_UPDATE',
                icon : 'edit',
                onClick : function () {
                    var grid = Gikam.getComp('emc-test-method-edit-list-grid');
                    var gridSelections = grid.getSelections();
                    if (gridSelections.length < 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                        return;
                    }
                    var modal = Gikam.create('modal', {
                        width : 420,
                        height : 320,
                        title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TEST_METHOD_EDIT.TITLE',
                    });
                    Gikam.create('layout', {
                        renderTo : modal.window.$dom,
                        center : {
                            padding : '0 30px 0 0',
                            items : [ {
                                type : 'btnToolbar',
                                items : [ {
                                    type : 'button',
                                    text : 'GIKAM.BUTTON.CONFIRM',
                                    icon : 'modify',
                                    onClick : ()=> {
                                        var formData = Gikam.getComp('emc-test-method-edit-list-batch-update-form').getData();
                                        for (var gridSelection of gridSelections) {
                                            grid.setData({
                                                id : gridSelection.id,
                                                testMethodType : formData.testMethodType,
                                                analysisFee : formData.analysisFee,
                                                sampleFee : formData.sampleFee,
                                                hours : formData.hours,
                                                recoveryRate : formData.recoveryRate,
                                                conrolledNo : formData.conrolledNo,
                                                beforeHandle : formData.beforeHandle,
                                                acqNoSuffix : formData.acqNoSuffix,
                                                ext$ : {
                                                	beforehandle2 :  formData.ext$.beforehandle2
                                                },
                                                reportLimitType : formData.reportLimitType
                                            });
                                        }
                                        modal.close();
                                    }
                                } ]
                            }, {
                                type : 'form',
                                id : 'emc-test-method-edit-list-batch-update-form',
                                columns : 1,
                                fields : emcTestMethod.getBatchUpdateFormFields(),
                            } ]
                        }
                    });
                }
            },{
                type : 'button',
                text : 'GIKAM.BUTTON.BATCH_UPDATE_PARAM',
                icon : 'edit',
                onClick : function() {
                    var grid = Gikam.getComp('emc-test-method-edit-list-grid');
                    var gridSelections = grid.getSelections();
                    if (gridSelections.length < 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                        return;
                    }
                   /* var filterData = gridSelections.filter(item => item.methodId != gridSelections[0].methodId);
                    if (filterData.length != 0) {
                        Gikam.alert('批量修改方法参数信息时，清选择相同的方法进行修改');
                        return;
                    }*/

                    var _this = this;
                    Gikam.create('modal', {
                        height : 430,
                        title: '批量设置测试方法参数属性',
                        url : emcTestMethod.baseUrl + '/page/method-update-param-batch-detail',
                        onAfterClose : function() {
                            Gikam.getComp('emc-test-method-edit-list-grid').refresh();
                        }
                    });
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.MDM.BIZMDM.TESTS.BUTTON.BEFORE_HANDLE_COPYTO',
                icon : 'copy',
                onClick : function() {
                    _this.beforeHandleCopyTo();
                }
            },  ]
        }
    },

    beforeHandleCopyTo : function () {
        var testMethodRow = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BIZMDM.TESTS.BUTTON.BEFORE_HANDLE_COPYTO',
            url : emcTestMethod.baseUrl + '/page/item-copy-to' + Gikam.param({
                id_DIFFER : testMethodRow.id,
            }),
            width : '60%',
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                workspace.window.showMask(true);
                var url = emcTestMethod.baseUrl + '/before-handle/action/copy-to';
                Gikam.post(url, Gikam.getJsonWrapper({}, [ 'emcTestMethodServiceImpl', [testMethodRow] ], [ 'emcTestMethodServiceImpl', rows ])).always(function() {
                    workspace.window.closeMask();
                });
            }
        });

    },

    deactive : function () {
        var selections = Gikam.getComp('emc-test-method-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections) || selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        if (selections[0].activatedFlag === '0') {
            Gikam.alert('GIKAM.TIP.CHOOSE_ACTIVATED_ITEM');
            return;
        }
        var modal = Gikam.create('modal', {
            title: "EMC.MODULE.MDM.BIZMDM.TESTS.TITLE.DEACTIVE",
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
                        text : 'GIKAM.BUTTON.CONFIRM',
                        class : 'blue',
                        onClick : function() {
                            if (form.validate()) {
                                selections.forEach(function (item) {
                                    item.remark = form.options.data.remark
                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(emcTestMethod.baseUrl + "/action/deactivate", Gikam.getJsonWrapper({}, [ '', selections ])).done(function (data) {
                                    Gikam.getComp('emc-test-method-edit-list-grid').refresh();
                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close();
                                });
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
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
                    field : 'remark',
                    title : 'T_EMC_TEST_METHOD.DEACTIVE_REASON',
                    validators : [ 'notEmpty', 'strLength[0, 300]' ],
                    preInsert : true,
                    type : 'textarea',
                    height : 100,
                }
            ],
            autoSave: false
        });
    },

    uploadScriptFile : function() {
        var rows = Gikam.getComp('emc-test-method-edit-list-grid').getSelections();
        if (rows && rows.length == 1) {
            var bizId = rows[0].id;
            Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
                f : {
                    targetId : 'T_EMC_TEST_METHOD$' + bizId
                },
                n : -1,
                s : 50
            })).done(function(r) {
                if (r.total > 0) {
                    Gikam.confirm('EMC.MODULE.MDM.BIZMDM.TESTS.TIP.SCRIPT_FILE_OVERRIDE', 'EMC.MODULE.MDM.BIZMDM.TESTS.TIP.SCRIPT_FILE_OVERRIDE', function() {
                        Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', r.rows ])).done(function(r) {
                            Gikam.create('simpleUploader', {
                                id : 'emc-test-method-edit-uploader',
                                dbTable : 'T_EMC_TEST_METHOD',
                                bizId : bizId,
                                bizCategory : 'script',
                                multiple : false,
                                onAfterClose : function() {
                                    Gikam.getComp('emc-test-method-edit-list-grid').refresh()
                                }
                            });
                        });
                    })
                } else {
                    Gikam.create('simpleUploader', {
                        id : 'emc-test-method-edit-uploader',
                        dbTable : 'T_EMC_TEST_METHOD',
                        bizId : bizId,
                        bizCategory : 'script',
                        multiple : false,
                        onAfterClose : function() {
                            Gikam.getComp('emc-test-method-edit-list-grid').refresh()
                        }
                    });
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    scriptFileCopyTo : function () {
        var selections = Gikam.getComp('emc-test-method-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections) || selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        // 所选测试要有公式附件
        var bizId = selections[0].id;
        Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
            f : {
                targetId : 'T_EMC_TEST_METHOD$' + bizId
            },
            n : -1,
            s : 50
        })).done(function(r) {
            debugger
            if (r.total > 0) {
                var scriptFile = r.rows[0];
                Gikam.create('modal', {
                    title : 'EMC.MODULE.MDM.BIZMDM.TESTS.BUTTON.SCRIPT_FILE_COPYTO',
                    url : emcTestMethod.baseUrl + '/page/item-copy-to' + Gikam.param({
                        id_DIFFER : bizId,
                    }),
                    width : '60%',
                    onAfterClose : function(rows) {
                        if (Gikam.isEmpty(rows)) {
                            return;
                        }
                        // 复制公式附件，只复制file表的文件记录，多个测试使用同一个公式附件
                        workspace.window.showMask(true);
                        var url = emcTestMethod.baseUrl + '/script-file/action/copy-to';
                        Gikam.post(url, Gikam.getJsonWrapper({}, [ 'coreFileServiceImpl', [scriptFile] ], [ 'emcTestMethodServiceImpl', rows ])).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                });
            } else {
                Gikam.alert('所选测试没有公式附件，请先上传！');
                return;
            }
        });

    },

    loadMethodParamPage : function(id) {
        // 先选择方法参数模板的模板名称，之后点击可以查看且维护字段。 删除，先查有没有，再删 （bizId）。
        var _this = this;
        Gikam.create('modal', {
            width : '60%',
            title : 'EMC.MODULE.MDM.BIZMDM.TESTS.MODAL.METHOD_PARAM_PROPERTY_SET',
            url : emcTestMethod.baseUrl + '/page/method-param-detail' + Gikam.param({
                id : id,
            }),
        });
    },

    // 计算公式
    popScript : function(id, testId, testMethodId, methodId) {
        var _this = this;
        var grid = Gikam.getComp('emc-test-item-list-grid');
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BIZMDM.TESTS.MODAL.SCRIPT',
            width : 600,
            height : 600,
            url : emcTestMethod.baseUrl + '/page/script-detail' + Gikam.param({
                id : id,
                testId : testId,
                testMethodId : testMethodId,
                methodId : methodId
            }),
            onAfterClose : function(script) {
                grid.setOptions({
                    requestData : {
                        activatedFlag_SEQ : '1'
                    }
                });
                grid.refresh();
            }
        });
    },

    beforeHandleEdit: function(index) {
        var grid = Gikam.getComp('emc-test-method-edit-list-grid');
        var row = grid.getData()[index];
        Gikam.create('modal', {
            width : '60%',
            height : 400,
            url : Gikam.choose.getConfig('beforeHandleEdit').url + Gikam.param({
                testMethodId : row.id
            })
        });
    },

    viewScriptFile: function(index) {
        var grid = Gikam.getComp('emc-test-method-edit-list-grid');
        var row = grid.getData()[index];
        Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
            f : {
                targetId : 'T_EMC_TEST_METHOD$' + row.id
            },
            n : -1,
            s : 50
        })).done(function(r) {
            if (r.total > 0) {
                Gikam.openFile(r.rows[0].id, false);
            } else {
                Gikam.alert('未维护公式附件');
            }
        });
    },

    // 配置结果
    editResult : function(id) {
        var grid = Gikam.getComp('emc-test-item-list-grid');
        var row = grid.getRowDataById(id);
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
                grid.setOptions({
                    requestData : {
                        activatedFlag_SEQ : '1'
                    }
                });
                grid.refresh();
            }
        });
    },

    // 配置QC指标和分段修约
    editQcIndexAndRevision : function(id) {
        var grid = Gikam.getComp('emc-test-item-list-grid');
        var row = grid.getRowDataById(id);
        // var row = grid.getData()[index];

        Gikam.create('modal', {
            width : 1200,
            height : 600,
            url : emcTestMethod.baseUrl + '/page/item-qcindex-revision-list' + Gikam.param({
                itemId : row.id,
                itemName : row.itemName,
                testMethodId : row.testMethodId
            }),
            onAfterClose : function() {
                grid.setOptions({
                    requestData : {
                        activatedFlag_SEQ : '1'
                    }
                });
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
                    columns : emcTestMethod.getItemListColumns(),
                    page :true,
                    group : {
                        fields : [ 'ext$.analysiscategoryname' ],
                        groupCheckbox : false,
                        formatter : function(row) {
                            return row.ext$.analysiscategoryname;
                        }
                    },
                    order : {
                        'ext$.analysiscategoryname' : 'asc',
                        'ORDERNO' : 'asc',
                    },
                    onLoadSuccess : function(data) {
                        Gikam.getComp('emc-test-item-list-grid').setOptions({
                            requestData : {
                                activatedFlag_SEQ : ''
                            }
                        });
                    },
                    onCellClick : function(field, row) {
                        field === 'itemName' && _this.loadItemDetailPage(row.id);
                    },
                    deleteFormatter : function(row) {
                        return row.itemName || row.id;
                    },
                    activateFormatter : function(row) {
                        return row.itemName || row.id;
                    },
                    deactivateFormatter : function(row) {
                        return row.itemName || row.id;
                    },
                    onBeforeUpdate : function(data) {
                        if (data.row != undefined && Gikam.isEmpty(data.row.repeatItem)) {
                            Gikam.alert('EMC.MODULE.MDM.BIZMDM.TESTS.TIP.REPEATITEM_NOT_NULL');
                            return false;
                        }
                        return true;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var formData = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
                            if (Gikam.isEmpty(formData.id)) {
                                Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                return;
                            }
                            Gikam.preInsert({
                                modalTitle : 'EMC.MODULE.MDM.BIZMDM.TESTS.TAB.TEST_ITEM_ADD',
                                fields : emcTestMethod.getPreInsertFormFields(),
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
                        text : 'GIKAM.BUTTON.ACTIVE',
                        icon : 'enable',
                        onClick : function() {
                            Gikam.getComp('emc-test-item-list-grid').activateRows(emcTestMethod.baseUrl + '/items/action/activate');
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DEACTIVE',
                        icon : 'disable',
                        onClick : function() {
                            Gikam.getComp('emc-test-item-list-grid').deactivateRows(emcTestMethod.baseUrl + '/items/action/deactivate');
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
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.VIEWAUDIT',
                        icon : 'view-audit',
                        onClick : function() {
                            Gikam.viewAudit('T_EMC_ITEM', Gikam.getComp('emc-test-item-list-grid').getSelections());
                        }
                    },{
                        type : 'button',
                        text : '批量删除QC指标',
                        icon : 'edit',
                        onClick : function() {
                            var _this = this;
                            var selections = Gikam.getComp('emc-test-item-list-grid').getSelections();
                            if (Gikam.isEmpty(selections)) {
                                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                return;
                            }
                            workspace.window.showMask(true);
                            Gikam.del(emcTestMethod.baseUrl + '/items/action/qc-index',
                                Gikam.getJsonWrapper({}, ['', selections])).done(function (id) {
                                Gikam.getComp('emc-test-item-list-grid').refresh();
                            }).always(function () {
                                workspace.window.closeMask();
                            });
                        }
                    },{
                        type : 'button',
                        text : '批量删除分段修约',
                        icon : 'edit',
                        onClick : function() {
                            var _this = this;
                            var selections = Gikam.getComp('emc-test-item-list-grid').getSelections();
                            if (Gikam.isEmpty(selections)) {
                                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                return;
                            }
                            workspace.window.showMask(true);
                            Gikam.del(emcTestMethod.baseUrl + '/items/action/revision',
                                Gikam.getJsonWrapper({}, ['', selections])).done(function (id) {
                                Gikam.getComp('emc-test-item-list-grid').refresh();
                            }).always(function () {
                                workspace.window.closeMask();
                            });
                        }
                    }, ]
                } ]
            }, {
                title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TAB.TEST_ITEM_GROUP',
                items : [ {
                    type : 'layout',
                    west : {
                        width : 550,
                        items : [ {
                            type : 'grid',
                            id : 'emc-test-item-group-list-grid',
                            url : _this.getItemGroupUrl(),
                            columns : emcTestMethod.getItemGroupListColumns(),
                            service : 'emcItemGroupServiceImpl',
                            deleteFormatter : function(row) {
                                return row.itemGroup || row.id;
                            },
                            filter : true,
                            onLoadSuccess : function(data) {
                                if (!Gikam.isEmpty(data)) {
                                    this.activeRowByIndex(0);
                                } else {
                                    Gikam.getComp('emc-test-group-item-list-grid').cleanData();
                                }
                            },
                            onRowActive : function(index, row) {
                                _this.onItemGroupActive(row);
                            },
                            beforeUpdate : function(data) {
                                if (!Gikam.isEmpty(data.itemGroup)) {

                                    Gikam.post(_this.getItemGroupUrl(Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow().id), Gikam.getJsonWrapper(null, [ '', [] ])).done(function(tests) {

                                        var allData = tests.rows;
                                        var returnFlag = true;

                                        allData.some(function(item) {

                                            if (item.itemGroup == data.itemGroup) {
                                                Gikam.alert('EMC.MODULE.MDM.BIZMDM.TESTS.TIP.CANNOT_ADD_REITERATED_NAME');
                                                returnFlag = false;
                                                return true;
                                            }
                                        });

                                        if (returnFlag) {
                                            workspace.window.showMask(true);
                                            Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcItemGroupServiceImpl', [ data ] ])).done(function(id) {

                                                Gikam.getComp('emc-test-item-group-list-grid').refreshRowById(data.id);

                                            }).always(function() {
                                                workspace.window.closeMask();
                                            });
                                        }
                                    });

                                    return false;// 关闭自动保存

                                }
                            },
                            toolbar : [ {
                                type : 'button',
                                text : 'GIKAM.BUTTON.INSERT',
                                icon : 'add',
                                onClick : function() {
                                    Gikam.confirm('GIKAM.TIP.CONFIRM', 'EMC.MODULE.MDM.BIZMDM.TESTS.TEST_METHOD.TIP.CONFRIM_ITEMGROUP_ADD', function () {
                                        _this.insertItemGroupRow();
                                    });
                                }
                            // }, {
                            //     type : 'button',
                            //     text : 'GIKAM.BUTTON.DELETE',
                            //     icon : 'remove-row',
                            //     onClick : function() {
                            //         _this.deleteItemGroupRow();
                            //     }
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.COPY',
                                icon : 'add',
                                onClick : function() {
                                    Gikam.confirm('GIKAM.TIP.CONFIRM', 'EMC.MODULE.MDM.BIZMDM.TESTS.TEST_METHOD.TIP.CONFRIM_ITEMGROUP_COPY', function () {
                                        _this.copyItemGroupRow();
                                    });
                                }
                            }, {
                                type : 'dropDownMenu',
                                text : 'EMC.MODULE.MDM.BIZMDM.TESTS.BUTTON.ORGANIC_ITEM',
                                items : [ {
                                    type : 'button',
                                    text : 'EMC.MODULE.MDM.BIZMDM.TESTS.BUTTON.COPY_ORGANIC_ITEM_GROUP',
                                    icon : 'copy',
                                    onClick : function() {
                                        _this.copyOrganicItemGroup();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.MDM.BIZMDM.TESTS.BUTTON.EDIT_ORGANIC_ITEM_GROUP',
                                    icon : 'edit',
                                    onClick : function() {
                                        _this.editOrganicItemGroup();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.MDM.BIZMDM.TESTS.BUTTON.COPY_ORGANIC_FORMULA_TO',
                                    icon : 'copy',
                                    hidden : false,
                                    onClick : function() {
                                        _this.copyOrganicFormulaTo();
                                    }
                                } ]
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.VIEWAUDIT',
                                icon : 'view-audit',
                                onClick : function() {
                                    Gikam.viewAudit('T_EMC_ITEM_GROUP', Gikam.getComp('emc-test-item-group-list-grid').getSelections());
                                }
                            } ]
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'grid',
                            id : 'emc-test-group-item-list-grid',
                            url : _this.getGroupItemUrl(),
                            columns : emcTestMethod.getGroupItemListColumns(),
                            service : 'emcGroupItemServiceImpl',
                            deleteFormatter : function(row) {
                                return row.ext$.itemname || row.id;
                            },
                            toolbar : [ {
                                type : 'button',
                                text : 'GIKAM.BUTTON.INSERT',
                                icon : 'add',
                                onClick : function() {
                                    _this.insertGroupItemRow();
                                }
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.DELETE',
                                icon : 'remove-row',
                                onClick : function() {
                                    _this.deleteGroupItemRow();
                                }
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.VIEWAUDIT',
                                icon : 'view-audit',
                                onClick : function() {
                                    Gikam.viewAudit('T_EMC_GROUP_ITEM', Gikam.getComp('emc-test-group-item-list-grid').getSelections());
                                }
                            } ]
                        } ]
                    }

                } ]
            }, {
                title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TAB.TEST_EQUIPT',
                items : [ {
                    type : 'grid',
                    fill : true,
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
                        icon : 'add',
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
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.VIEWAUDIT',
                        icon : 'view-audit',
                        onClick : function() {
                            Gikam.viewAudit('T_EMC_EQUIPT_TEST_METHOD', Gikam.getComp('emc-test-equipt-list-grid').getSelections());
                        }
                    } ]
                } ]
            }, {
                title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TAB.TEST_ORDER_CATEGORY',
                items : [ {
                    type : 'grid',
                    fill : true,
                    id : 'emc-test-order-category-list-grid',
                    url : _this.getOrderCategoryUrl(),
                    columns : emcTestMethod.getOrderCategoryListColumns(),
                    service : 'emcTestMethodOrderCategoryServiceImpl',
                    deleteFormatter : function(row) {
                        return row.ext$.monitortmplname || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertOrderCategoryRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteOrderCategoryRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.COPY',
                        icon : 'add',
                        onClick : function() {
                            _this.copyOrderCategoryRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.VIEWAUDIT',
                        icon : 'view-audit',
                        onClick : function() {
                            Gikam.viewAudit('T_EMC_TEST_ORDER_CATEGORY', Gikam.getComp('emc-test-order-category-list-grid').getSelections());
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
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.COPYTO',
                        icon : 'copy',
                        onClick : function() {
                            _this.copyDefaultQcTo();
                        }
                    } ]
                } ]
            }, {
                title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TAB.RELATED_TEST_METHOD',
                items : [ {
                    type : 'grid',
                    fill : true,
                    id : 'emc-test-method-related-list-grid',
                    columns : emcTestMethod.getRelatedTestMethodListColumns(),
                    service : 'emcRelatedTestMethodServiceImpl',
                    deleteFormatter : function(row) {
                        return row.ext$.testname || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertRelatedTestMethod();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteRelatedTestMethod();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.VIEWAUDIT',
                        icon : 'view-audit',
                        onClick : function() {
                            Gikam.viewAudit('T_EMC_RELATED_TEST_METHOD', Gikam.getComp('emc-test-method-related-list-grid').getSelections());
                        }
                    } ]
                } ]
            } ]
        }
    },

    copyTestMethod : function() {
        var selections = Gikam.getComp('emc-test-method-edit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            width : 400,
            height : 300,
            title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TITLE.COPY_TEST_METHOD',
            url : emcTestMethod.baseUrl + '/page/copy' + Gikam.param({
                id : selections[0].id,
                testCategoryId : selections[0].testCategoryId,
                testCategory : selections[0].ext$.testcategory,
                testClass : selections[0].testClass,
                testMethodType : selections[0].testMethodType,
                testId : selections[0].testId,
                methodname : encodeURIComponent(selections[0].ext$.methodname),
                methodId : selections[0].methodId
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-test-method-edit-list-grid').refresh();
            }
        });
    },


    copyTestMethodByCategory : function() {
        var selections = Gikam.getComp('emc-test-method-edit-list-grid').getSelections();
        if (selections.length == 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        Gikam.create('modal', {
            width : 400,
            height : 300,
            title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TITLE.COPY_TEST_METHOD',
            url : emcTestMethod.baseUrl + '/page/copy-by-category' + Gikam.param({
                id : selections[0].id,
                testCategoryId : selections[0].testCategoryId,
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-test-method-edit-list-grid').refresh();
            }
        });
    },


    copyItem : function() {
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

    editBatchItem : function() {
        var selections = Gikam.getComp('emc-test-item-list-grid').getSelections();
        if (selections.length > 0) {
            var formData = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
            Gikam.create('modal', {
                width : 600,
                height : 600,
                title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TITLE.EDITBATCH_TEST_METHOD_ITEM',
                url : emcTestMethod.baseUrl + '/page/item-batch',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        selections.map(function(item) {
                            item.orderNo = data.orderNo;
                            item.unitName = data.unitName;
                            item.revisionRule = data.revisionRule;
                            item.analysisCategory = data.analysisCategory;
                            item.originalRecordDisplay = data.originalRecordDisplay;
                            item.reportDisplay = data.reportDisplay;
                            item.param = data.param;
                            item.qcCategoryId = data.qcCategoryId;
                            item.qcCategoryName = data.qcCategoryName;
                            item.calQcResult = data.calQcResult;
                            item.scientificNotation = data.scientificNotation;
                            item.negativeToZero = data.negativeToZero;
                            item.allowableError = data.allowableError;
                            item.limitRevision = data.limitRevision;
                            item.ext$.coefficient = data.ext$.coefficient;
                            item.ext$.nonsnrange = data.ext$.nonsnrange;
                            item.ext$.issendtoresult = data.ext$.issendtoresult;
                            item.ext$.theorycontent= data.ext$.theorycontent;
                            item.ext$.judgebasis= data.ext$.judgebasis;
                            item.ext$.sendcoefficient= data.ext$.sendcoefficient;
                            item.ext$.detectedquantity= data.ext$.detectedquantity;
                            item.ext$.detectedquantityunit= data.ext$.detectedquantityunit;
                            item.originalRecordName= data.originalRecordName;

                        });
                        Gikam.put(emcTestMethod.baseUrl + '/' + formData.id, Gikam.getJsonWrapper({}, [ 'emcItemServiceImpl', selections ])).done(function() {
                            Gikam.getComp('emc-test-item-list-grid').refresh();
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

    copyItemTo : function() {
        var testMethod = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
        var selections = Gikam.getComp('emc-test-item-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TITLE.COPY_TEST_METHOD_ITEM',
            url : emcTestMethod.baseUrl + '/page/item-copy-to' + Gikam.param({
                id_DIFFER : testMethod.id
            }),
            width : '60%',
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                workspace.window.showMask(true);

                var url = emcTestMethod.baseUrl + '/items/action/copyto';
                Gikam.post(url, Gikam.getJsonWrapper({}, [ 'emcTestMethodServiceImpl', rows ], [ 'emcItemServiceImpl', selections ])).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    copyEquiptTo : function() {
        var selections = Gikam.getComp('emc-test-equipt-list-grid').getSelections();
        if (selections.length < 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let testMethod = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('testMethod').title,
            url : Gikam.choose.getConfig('testMethod').url + Gikam.param({
                id_DIFFER : testMethod.id
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                workspace.window.showMask(true);
                url = emcTestMethod.baseUrl + '/equipts/action/copyto';
                Gikam.post(url, Gikam.getJsonWrapper({}, [ 'emcEquiptTestMethodServiceImpl', selections ], ['emcTestMethodServiceImpl', rows])).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    loadItemDetailPage : function(id) {
        var _this = this;
        Gikam.create('modal', {
            height : 430,
            url : emcTestMethod.baseUrl + '/page/item-detail' + Gikam.param({
                id : id
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-test-item-list-grid').refresh();
            }
        });
    },

    loadDetailPage : function(id) {
        var _this = this;
        Gikam.create('modal', {
            url : emcTestMethod.baseUrl + '/page/detail' + Gikam.param({
                id : id
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-test-method-edit-list-grid').refresh();
            }
        });
    },

    deleteItemRow : function() {
        var formData = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
        var url = Gikam.printf(emcTestMethod.baseUrl + '/{id}/items', {
            id : formData.id
        });
        Gikam.getComp('emc-test-item-list-grid').deleteRows(url);
    },

    insertEquiptRow : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-test-method-edit-list-grid').getSelections();
        if (selections.length < 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var formData = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
        var url = Gikam.printf(emcTestMethod.baseUrl + '/{id}/equipts', {
            id : formData.id
        });
        var param = Gikam.param({
            /*target_unassigned : '1',
            targetTable : 'T_EMC_EQUIPT_TEST_METHOD',
            targetEquiptColumn : 'EQUIPTID',
            targetFilterColumn : 'TESTMETHODID',
            targetFilterValue : formData.id,*/
            status_SEQ : 'inUse',
            org_filter : '1',
        });

        /*if (selections.length > 1) {
            var ids = [];
            for (var selection of selections) {
                ids.push(selection.id);
            }
            param = Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_EMC_EQUIPT_TEST_METHOD',
                targetEquiptColumn : 'EQUIPTID',
                targetFilterColumn : 'TESTMETHODID',
                targetFilterValue : ids,
                status_SEQ : 'inUse'
            });
        }*/

        Gikam.create('modal', {
            title : Gikam.choose.getConfig('equipt').title,
            url : Gikam.choose.getConfig('equipt').url + param,
            isModal: false,
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                // 判断关联仪器是否重复，如果已经存在该仪器，则不能重复添加。
                var chooseData = [];
                var equipts = [];
                if (selections.length > 1) {
                    for (var selection of selections) {
                        equipts = Gikam.postSync(_this.getEquiptUrl(selection.id)).responseJSON.rows;
                        chooseData = rows.map(function(item) {
                            for (var equipt of equipts) {
                                if (equipt.equiptId === item.id) return null;
                            }
                            return {
                                testMethodId : selection.id,
                                equiptId : item.id
                            }
                        }).filter((item)=> {return item !== null;});
                        url = Gikam.printf(emcTestMethod.baseUrl + '/{id}/equipts', {
                            id : selection.id
                        });
                        Gikam.getComp('emc-test-equipt-list-grid').insert(url, chooseData);
                    }
                } else {
                    equipts = Gikam.postSync(_this.getEquiptUrl(formData.id)).responseJSON.rows;
                    chooseData = rows.map(function(item) {
                        for (var equipt of equipts) {
                            if (equipt.equiptId === item.id) return null;
                        }
                        return {
                            testMethodId : formData.id,
                            equiptId : item.id,
                            orgId :  workspace.user.ext$.parentorgid,
                        }
                    }).filter((item)=> {return item !== null;});
                    Gikam.getComp('emc-test-equipt-list-grid').insert(url, chooseData);
                }
            }
        });
    },

    insertOrderCategoryRow : function() {
        var selections = Gikam.getComp('emc-test-method-edit-list-grid').getSelections();
        if (selections.length < 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var modal = Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TEST_METHOD.ORDERCATEGORY.ADD.TITLE',
            width : '20%',
            height : '35%'
        })

        Gikam.create('layout', {
           renderTo : modal.window.$dom,
           center : {
               padding : '0 33px 0 0',
               items : [ {
                   type : 'form',
                   id : 'emc-test-method-edit-list-order-category-insert-form',
                   columns : 1,
                   fields : emcTestMethod.getOrderCategoryListColumns()
               }, {
                   type : 'btnToolbar',
                   items : [ {
                       type : 'button',
                       text : 'GIKAM.BUTTON.CONFIRM',
                       icon : 'add',
                       onClick : ()=> {
                           var formData = Gikam.getComp('emc-test-method-edit-list-order-category-insert-form').getData();
                           var activedRow = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
                           var url = Gikam.printf(emcTestMethod.baseUrl + '/{id}/order-categorys', {
                               id : activedRow.id
                           });

                           if (selections.length > 1) {
                               for (var selection of selections) {
                                   url = Gikam.printf(emcTestMethod.baseUrl + '/{id}/order-categorys', {
                                       id : selection.id
                                   });
                                   formData.testMethodId = selection.id;
                                   Gikam.getComp('emc-test-order-category-list-grid').insert(url, [ formData ]);
                               }
                           } else {
                               formData.testMethodId = activedRow.id;
                               Gikam.getComp('emc-test-order-category-list-grid').insert(url, [ formData ]);
                           }

                           modal.close();
                       }
                   } ]
               } ]
           }
        });
    },

    insertDefaultQc : function() {
        var selections = Gikam.getComp('emc-test-method-edit-list-grid').getSelections();
        if (selections.length < 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var testMethod = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();

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
                            addNum : item.addNum
                        }
                    });
                    Gikam.getComp('emc-test-method-default-qc-list-grid').insert(url, data);
                }
            }
        });
    },

    copyDefaultQcTo : function() {
        let selections = Gikam.getComp('emc-test-method-default-qc-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let testMethod = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TITLE.COPY_DEFAULT_QC',
            url : emcTestMethod.baseUrl + '/page/item-copy-to' + Gikam.param({
                id_DIFFER : testMethod.id
            }),
            width : '60%',
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                workspace.window.showMask(true);

                var url = emcTestMethod.baseUrl + '/action/default-qc-copy-to';
                Gikam.post(url, Gikam.getJsonWrapper({}, [ 'emcTestMethodServiceImpl', rows ], [ 'emcTestMethodDefaultQcServiceImpl', selections ])).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    deleteEquiptRow : function() {
        var formData = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
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

        var formData = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
        var url = Gikam.printf(emcTestMethod.baseUrl + '/{id}/equipts/action/set-default', {
            id : formData.id
        });

        workspace.window.showMask(true);
        Gikam.put(url, Gikam.getJsonWrapper({}, [ '', selections ])).done(function() {
            Gikam.getComp('emc-test-equipt-list-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    copyOrderCategoryRow : function() {
        var formData = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();

        var selections = Gikam.getComp('emc-test-order-category-list-grid').getSelections();

        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            width : 600,
            height : 600,
            title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TITLE.CHOOSE_ORDER_CATEGORY',
            url : '/secure/emc/module/mdm/bizmdm/monitor-tmpls/page/order-category-choose-list' + Gikam.param({
                ID_DIFFER : selections[0].monitorTmplId
            }),
            onAfterClose : function(data) {
                if (!Gikam.isEmpty(data)) {
                    workspace.window.showMask(true);
                    data.map(function(item) {
                        item.monitorTmplId = item.id;
                        item.id = '';
                        item.containerCategoryId = selections[0].containerCategoryId;
                        item.containerGroup = selections[0].containerGroup;
                        item.fixative = selections[0].fixative;
                        item.sampleqty = selections[0].sampleqty;
                    });
                    Gikam.getComp('emc-test-order-category-list-grid').insert(Gikam.printf(emcTestMethod.baseUrl + '/{id}/order-categorys', {
                        id : formData.id
                    }), data).done(function() {
                        Gikam.getComp('emc-test-order-category-list-grid').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    deleteOrderCategoryRow : function() {
        var formData = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
        var url = Gikam.printf(emcTestMethod.baseUrl + '/{id}/order-categorys', {
            id : formData.id
        });
        Gikam.getComp('emc-test-order-category-list-grid').deleteRows(url);
    },

    deleteDefaultQc : function() {
        var testMethod = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
        var url = Gikam.printf(emcTestMethod.baseUrl + '/{id}/default-qcs', {
            id : testMethod.id
        });
        Gikam.getComp('emc-test-method-default-qc-list-grid').deleteRows(url);
    },

    insertRelatedTestMethod : function() {
        var selections = Gikam.getComp('emc-test-method-edit-list-grid').getSelections();
        if (selections.length < 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var testMethod = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();

        Gikam.create('modal', {
            width : '60%',
            height :  '70%',
            title : Gikam.choose.getConfig('testMethod').title,
            url : Gikam.choose.getConfig('testMethod').url + Gikam.param({
                id_DIFFER : testMethod.id,
                target_unassigned : '1',
                targetTable : 'T_EMC_RELATED_TEST_METHOD',
                targetTestMethodIdColumn : 'RELATEDTESTMETHODID',
                targetFilterColumn1 : 'TESTMETHODID',
                targetFilterValue1 : testMethod.id
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var data = rows.map(item => {
                    return {
                        testMethodId : testMethod.id,
                        relatedTestMethodId : item.id
                    }
                });

                var url = emcTestMethod.baseUrl + '/action/batch-add-related-test-method';

                Gikam.postText(url, Gikam.getJsonWrapper({}, [ 'emcTestMethodServiceImpl', selections ], [ 'emcRelatedTestMethodServiceImpl', data ] )).done(function(data) {
                    Gikam.getComp('emc-test-method-related-list-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    deleteRelatedTestMethod : function() {
        var testMethod = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
        var url = Gikam.printf(emcTestMethod.baseUrl + '/{id}/related-test-method', {
            id : testMethod.id
        });
        Gikam.getComp('emc-test-method-related-list-grid').deleteRows(url);
    },

    insertItemGroupRow : function() {
        var formData = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
        if (Gikam.isEmpty(formData.id)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var url = Gikam.printf(emcTestMethod.baseUrl + '/{id}/item-groups', {
            id : formData.id
        });

        Gikam.getComp('emc-test-item-group-list-grid').insert(url, [ {
            testMethodId : formData.id,
            testId : formData.testId,
            methodId : formData.methodId
        } ]);
    },

    deleteItemGroupRow : function() {
        var selections = Gikam.getComp('emc-test-item-group-list-grid').getSelections();
        if (selections.length > 0) {
            var formData = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
            var url = Gikam.printf(emcTestMethod.baseUrl + '/{id}/item-groups', {
                id : formData.id
            });
            Gikam.getComp('emc-test-item-group-list-grid').deleteRows(url);
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    copyItemGroupRow : function() {
        var formData = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();

        var selections = Gikam.getComp('emc-test-item-group-list-grid').getSelections();

        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var url = Gikam.printf(emcTestMethod.baseUrl + '/{id}/item-groups/copy', {
            id : formData.id
        });

        Gikam.getComp('emc-test-item-group-list-grid').insert(url, selections);
    },

    insertGroupItemRow : function() {
        var formData = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
        var activeRow = Gikam.getComp('emc-test-item-group-list-grid').getActivedRow();
        if (Gikam.isEmpty(activeRow)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var url = Gikam.printf(emcTestMethod.baseUrl + '/item-groups/{itemGroupId}/group-items', {
            itemGroupId : activeRow.id
        });

        Gikam.create('modal', {
            title : Gikam.choose.getConfig('testItem').title,
            url : Gikam.choose.getConfig('testItem').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_EMC_GROUP_ITEM',
                targetItemIdColumn : 'ITEMID',
                targetFilterColumn : 'ITEMGROUPID',
                targetFilterValue : activeRow.id,
                testMethodId_SEQ : formData.id
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        itemGroupID : activeRow.id,
                        itemId : item.id
                    }
                });
                Gikam.getComp('emc-test-group-item-list-grid').insert(url, chooseData);
            }
        });
    },

    deleteGroupItemRow : function() {
        var activeRow = Gikam.getComp('emc-test-item-group-list-grid').getActivedRow();
        var url = Gikam.printf(emcTestMethod.baseUrl + '/item-groups/{itemGroupId}/group-items', {
            itemGroupId : activeRow.id
        });
        Gikam.getComp('emc-test-group-item-list-grid').deleteRows(url);
    },
    /**
     * 批量修改录入人
     */
    batchAddInputUser : function() {
        var testMethodGrid = Gikam.getComp('emc-test-method-edit-list-grid');
        var selections = testMethodGrid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var testMethodIds = "";
        selections.forEach(function(item) {
            testMethodIds += item.id + ',';
        });
        testMethodIds = testMethodIds.substring(0, testMethodIds.length - 1);

        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TITLE.EDIT_INPUTUSER',
            width : 700,
            url : '/secure/core/module/mdm/users/page/shuttle-frame' + Gikam.param({
                testMethodIds : testMethodIds,
                firstInputUserIds : selections[0].inputUserId,
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-test-method-edit-list-grid').refresh();
            }
        });
    },

    /** 复制有机分析项 */
    copyOrganicItemGroup : function() {
        var selections = Gikam.getComp('emc-test-item-group-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var modal = Gikam.create('modal', {
            title: "EMC.MODULE.MDM.BIZMDM.TESTS.MODAL.COPY_ORGANIC_ITEM_GROUP",
            width : 400,
            height : 180,
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
                                selections[0].itemGroup = form.options.data.itemGroup;
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.post(emcTestMethod.baseUrl + '/item-groups/action/copy-organic', Gikam.getJsonWrapper({}, [ '', selections ])).done(function(res) {
                                    if (res === 0) {
                                        Gikam.alert('EMC.MODULE.MDM.BIZMDM.TESTS.TIP.IRREGULAR_ORGANIC_ITEM_NAME');
                                        return;
                                    }
                                    Gikam.getComp('emc-test-item-group-list-grid').refresh();
                                    Gikam.getComp('emc-test-item-list-grid').refresh();
                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close()
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
            titleWidth: 70,
            fields: emcTestMethod.getCopyOrganicItemFormFields(),
            autoSave: false
        });
    },

    /** 修改有机分析组名 */
    editOrganicItemGroup : function() {
        var selections = Gikam.getComp('emc-test-item-group-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var modal = Gikam.create('modal', {
            title: "EMC.MODULE.MDM.BIZMDM.TESTS.MODAL.EDIT_ORGANIC_ITEM_GROUP",
            width : 400,
            height : 180,
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
                                selections[0].itemGroup = form.options.data.itemGroup;
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.postText(emcTestMethod.baseUrl + '/item-groups/action/edit-organic-group', Gikam.getJsonWrapper({}, [ '', selections ])).done(function () {
                                    Gikam.getComp('emc-test-item-group-list-grid').refresh();
                                    Gikam.getComp('emc-test-item-list-grid').refresh();
                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close()
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
            titleWidth: 70,
            fields: emcTestMethod.getCopyOrganicItemFormFields(),
            autoSave: false
        });
    },

    /** 复制有机公式到 */
    copyOrganicFormulaTo : function() {
        var selections = Gikam.getComp('emc-test-item-group-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            width : 300,
            height :  '70%',
            title : 'EMC.MODULE.MDM.BIZMDM.TESTS.MODAL.COPY_ORGANIC_FORMULA_TO',
            url : Gikam.choose.getConfig('testItemGroup').url + Gikam.param({
                id_DIFFER : selections[0].id,
                testMethodId_SEQ : selections[0].testMethodId,
                single : true
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                workspace.window.showMask(true);
                Gikam.postText(Gikam.printf(emcTestMethod.baseUrl + '/item-groups/{id}/action/copy-organic-formula-to', {
                    id : selections[0].id
                }), Gikam.getJsonWrapper({}, [ '', rows ])).done(function() {
                    Gikam.getComp('emc-test-item-group-list-grid').refresh();
                    Gikam.getComp('emc-test-item-list-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    getbaseInfoUrl : function(id) {
        if (id) {
            return Gikam.printf(emcTestMethod.baseUrl + '/{id}', {
                id : id
            })
        }
    },

    getItemUrl : function(testMethodId) {
        if (testMethodId) {
            return Gikam.printf(emcTestMethod.baseUrl + '/{id}/items/queries', {
                id : testMethodId
            })
        }
    },

    getItemGroupUrl : function(testMethodId) {
        if (testMethodId) {
            return Gikam.printf(emcTestMethod.baseUrl + '/{id}/item-groups/queries', {
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

    getOrderCategoryUrl : function(testMethodId) {
        if (testMethodId) {
            return Gikam.printf(emcTestMethod.baseUrl + '/{id}/order-categorys/queries', {
                id : testMethodId
            })
        }
    },

    getGroupItemUrl : function(itemGroupId) {
        if (itemGroupId) {
            return Gikam.printf(emcTestMethod.baseUrl + '/item-groups/{itemGroupId}/group-items/queries', {
                itemGroupId : itemGroupId
            })
        }
    },

    onTestActive : function(row) {
        var _this = this;
        Gikam.getAsyncComp('emc-test-item-list-grid').done(function(grid) {
            grid.setOptions({
                requestData : {
                    activatedFlag_SEQ : '1'
                }
            });
            grid.refresh({
                url : _this.getItemUrl(row.id)
            });
        });
        Gikam.getAsyncComp('emc-test-item-group-list-grid').done(function(grid) {
            grid.refresh({
                url : _this.getItemGroupUrl(row.id)
            });
        });
        Gikam.getAsyncComp('emc-test-equipt-list-grid').done(function(grid) {
            grid.refresh({
                url : _this.getEquiptUrl(row.id)
            });
        });
        Gikam.getAsyncComp('emc-test-order-category-list-grid').done(function(grid) {
            grid.refresh({
                url : _this.getOrderCategoryUrl(row.id)
            });
        });

        Gikam.getAsyncComp('emc-test-method-default-qc-list-grid').done(function(grid) {
            grid.refresh({
                url : Gikam.printf(emcTestMethod.baseUrl + '/{id}/default-qcs/queries', {
                    id : row.id
                })
            });
        });

        Gikam.getAsyncComp('emc-test-method-related-list-grid').done(function(grid) {
            grid.refresh({
                url : Gikam.printf(emcTestMethod.baseUrl + '/{id}/related-test-methods/queries', {
                    id : row.id
                })
            });
        });
    },

    onItemGroupActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-test-group-item-list-grid').refresh({
                url : _this.getGroupItemUrl(row.id),
            });
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-test-method-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : "50%",
                items : [ _this.getTestGridParam() ]
            },
            center : {
                items : [ _this.getOtherGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
