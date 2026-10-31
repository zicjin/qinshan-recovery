emcEquipt.editPage = {
    getGridParam : function() {
        var _this = this;
        _this.param['target_type'] = 'equipList';
        _this.param['orgid_seq'] = workspace.user.ext$.parentorgid;
        return {
            type : 'Grid',
            id : 'emc-equipt-edit-list-grid',
            url : emcEquipt.baseUrl + '/queries/raw',
            columns : emcEquipt.getEditListColumns(),
            checkOnActive : true,
            filterOpen : true,
            page : true,
            pageSize : 100,
            service : 'emcEquiptServiceImpl',
            requestData : _this.param,
            // readonly:!Gikam.isEmpty(_this.param.readonly),
            onRendered : function() {
                if(!Gikam.isEmpty(_this.param.readonly)){
                    this.setReadonly(true);
                }},
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(1);
                } else {
                    Gikam.getComp('emc-equipt-edit-list-detail-form').cleanData(); //清除基本信息
                    Gikam.getComp('emc-equipt-edit-list-item-grid').cleanData(); //清除dcu配置
                    Gikam.getComp('emc-equipt-edit-list-test-item-grid').cleanData();//清除仪器测试
                    Gikam.getComp('emc-equipt-plan-edit-list--grid').cleanData();//清除维护计划
                    Gikam.getComp('emc-equipt-record-edit-list-grid').cleanData();//清除维护记录
                    Gikam.getComp('emc-equipt-use-rec-edit-list-grid').cleanData();//清除仪器使用记录
                    Gikam.getComp('emc-equipt-edit-list-curve-grid').cleanData();//清除历史曲线
                    Gikam.getComp('emc-equipt-per-check-plan-edit-list-grid').cleanData();//清除期间核查计划
                    Gikam.getComp('emc-equipt-per-check-record-edit-list-grid').cleanData();//清除期间核查记录
                    Gikam.getComp('emc-equipt-maintain-repair-edit-list-grid').cleanData();//清除期间核查记录
                }
            },
            onRowActive : function(index, row) {
                _this.onDetailActive(row);
            },
            deleteFormatter : function(row) {
                return row.equiptId || row.id;
            },
            order : {
                'EQUIPTCATEGORYID' : 'asc',
            },
            toolbar : !Gikam.isEmpty(_this.param.readonly) ? [] :[ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.MODAL.EQUIP_ADD',
                        fields : emcEquipt.getPreInsertFields(),
                        url : emcEquipt.baseUrl
                    }).done(function() {
                        Gikam.getComp('emc-equipt-edit-list-detail-form-tab').show();
                        Gikam.getComp('emc-equipt-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-equipt-edit-list-grid').deleteRows(emcEquipt.baseUrl).done(function() {
                        Gikam.getComp('emc-equipt-edit-list-detail-form').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.EXPORT',
                icon : 'move-up',
                onClick : function() {
                    _this.equipExport();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.IMPORT',
                icon : 'move-down',
                onClick : function() {
                    _this.equipImport();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'review',
                onClick : function() {
                    Gikam.viewReport('equiptUseReocrd', Gikam.getComp('emc-equipt-edit-list-grid').getSelections());
                }
            } ],
            group : {
                fields : [ 'equiptCategoryId' ],
                formatter : function(row) {
                    return row.ext$.equiptcategoryname || '-';
                }
            }
        }
    },

    equipExport : function() {
        Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}/{id}', {
            templateCode : 'emcEquipExportTemplate',
            service : 'emcEquipExportTemplate',
            id : null
        })).done(function(fileUri) {
            var fileUrl = IFM_CONTEXT + fileUri;
            Gikam.emcDownloadFile(fileUrl);
        });
    },

    equipImport : function() {
        var _this = this;
        Gikam.importExcel({
            url : IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'emcEquipImportFileFunction',
            onImportSuccess : function(files) {
                Gikam.getComp('emc-equipt-edit-list-grid').refresh();
            }
        });
    },

    onDetailActive : function(row) {
        var _this = this;
        if (row) {
            if (isNaN(row.id)) {
                return;
            }

            Gikam.getAsyncComp('emc-equipt-edit-list-detail-form').done(function(item) {
                item.refresh({
                    url : _this.getDetailUrl(row.id)
                });
            });

            Gikam.getAsyncComp('emc-equipt-edit-list-dcu-conf-form').done(function(item) {
                item.refresh({
                    url : _this.getDetailUrl(row.id)
                });
            });

            Gikam.getAsyncComp('emc-equipt-edit-list-item-grid').done(function(item) {
                item.refresh({
                    url : _this.getTestUrl(row.id)
                });
            });

            Gikam.getAsyncComp('emc-equipt-edit-list-test-item-grid').done(function(item) {
                item.refresh({
                    url : _this.getTestItemUrl(row.id)
                });
            });

            Gikam.getAsyncComp('emc-equipt-cert-edit-list-grid').done(function(item) {
                item.refresh({
                    url : _this.getCertUrl(row.id)
                });
            });

            Gikam.getAsyncComp('emc-equipt-plan-edit-list--grid').done(function(item) {
                item.refresh({
                    url : Gikam.printf(emcEquipt.baseUrl + "/{id}/plans/queries", {
                        id : row.id
                    }),
                });
            });

            Gikam.getAsyncComp('emc-equipt-record-edit-list-grid').done(function(item) {
                item.refresh({
                    url : Gikam.printf(emcEquipt.baseUrl + "/{id}/records/queries", {
                        id : row.id
                    }),
                });
            });

            Gikam.getAsyncComp('emc-equipt-use-rec-edit-list-grid').done(function(item) {
                item.refresh({
                    url : Gikam.printf(emcEquipt.baseUrl + "/{id}/use-records/queries", {
                        id : row.id
                    }) + Gikam.param({
                        selectKey : 'EQUIPTID'
                    }),
                });
            });

            Gikam.getAsyncComp('emc-equipt-per-check-plan-edit-list-grid').done(function(item) {
                item.refresh({
                    url : Gikam.printf(emcEquipt.baseUrl + "/{id}/per-check-plan/queries", {
                        id : row.id
                    }),
                });
            });
            Gikam.getAsyncComp('emc-equipt-per-check-record-edit-list-grid').done(function(item) {
                item.refresh({
                    url : Gikam.printf(emcEquipt.baseUrl + "/{id}/per-check-record/queries", {
                        id : row.id
                    }),
                });
            });

            Gikam.getAsyncComp('emc-equipt-edit-list-curve-grid').done(function(item) {
                item.refresh({
                    url : IFM_CONTEXT + '/secure/emc/module/bp/result/curves/queries/raw',
                    requestData : {
                        equiptId_SEQ : row.id

                    }
                });
            });

            Gikam.getAsyncComp('emc-equipt-maintain-repair-edit-list-grid').done(function(item) {
                item.refresh({
                    url : Gikam.printf(emcEquipt.baseUrl + "/{id}/maintain/queries", {
                        id : row.id
                    }),
                });
            });

            Gikam.getComp('emc-equipt-edit-list-uploader').setOptions({
                bizId : row.id,
            });
        }
    },
    getDetailUrl : function(ItemId) {
        if (ItemId) {
            return Gikam.printf(emcEquipt.baseUrl + '/{id}', {
                id : ItemId
            })
        }
    },

    getCertUrl : function(ItemId) {
        if (ItemId) {
            return Gikam.printf(emcEquipt.baseUrl + '/{id}' + '/equipcerts/queries', {
                id : ItemId
            })
        }
    },
    getTestUrl : function(ItemId) {
        if (ItemId) {
            return Gikam.printf(emcEquipt.baseUrl + '/{id}' + '/test-methods/queries', {
                id : ItemId
            })
        }
    },
    getTestItemUrl : function(ItemId) {
        if (ItemId) {
            return Gikam.printf(emcEquipt.baseUrl + '/{id}' + '/test-items/queries', {
                id : ItemId
            })
        }
    },

    loadDetailPage : function() {
        var _this = this;
        return {
            id : 'emc-equipt-edit-list-detail-form-tab',
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.TAB.DETAIL_INFO',
                items : [ {
                    id : 'emc-equipt-edit-list-detail-form',
                    type : 'form',
                    service : 'emcEquiptServiceImpl',
                    fields : emcEquipt.getBaseInfoFormFields(),
                    columns : 3,
                    onRendered : function() {
                        this.toFieldsReadonly('equiptId');
                        this.removeFieldValidator('equiptId', [ 'remote' ]);
                    },
                    onUpdated : function(data) {
                        Gikam.getComp('emc-equipt-edit-list-grid').setData(data);
                    }
                } ]
            }, {
                title : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.TAB.DCU_CONF',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '100',
                        items : [ {
                            id : 'emc-equipt-edit-list-dcu-conf-form',
                            type : 'form',
                            service : 'emcEquiptServiceImpl',
                            fields : emcEquipt.getDCUConfFormFields(),
                            columns : 3,
                            onBeforeUpdate : function(data, keys) {
                                if (!Gikam.isEmpty(data.catalog)) {
                                    Gikam.getComp('emc-equipt-edit-list-dcu-conf-form').validate()
                                }
                            },
                            onLoadSuccess : function() {

                                var dcuForm = Gikam.getComp('emc-equipt-edit-list-dcu-conf-form');
                                var scriptCategory = dcuForm.options.data.scriptCategory;// 获取第一个选择框的值（脚本类型）
                                var scriptDesc = dcuForm.getField('scriptDesc');// 获取第二个选择框的对象（脚本描述）

                                scriptDesc.options.category = scriptCategory;// 将第二个选择框组件的category属性值进行动态赋值

                                dcuForm.getField('scriptDescs').options.category = scriptCategory;
                            }
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'grid',
                            id : 'emc-equipt-edit-list-test-item-grid',
                            service : 'emcEquiptTestItemServiceImpl',
                            checkOnActive : true,
                            page : true,
                            columns : emcEquipt.getTestItemGridFields(),
                            deleteFormatter : function(row) {
                                return row.ext$.itemname || row.id;
                            },
                            toolbar : [ {
                                type : 'button',
                                text : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.BUTTON.INSERT_ITEM',
                                icon : 'add',
                                onClick : function() {
                                    _this.insertItem()
                                }
                            }, {
                                type : 'button',
                                text : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.BUTTON.DELETE_ITEM',
                                icon : 'remove',
                                onClick : function() {
                                    _this.deleteItem()
                                }
                            }, {
                                type : 'button',
                                text : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.BUTTON.COPY_ITEM',
                                icon : 'copy',
                                onClick : function() {
                                    _this.copyItem()
                                }
                            }, {
                                type : 'button',
                                text : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.BUTTON.COPY_ITEM_TO',
                                icon : 'copy',
                                onClick : function() {
                                    _this.copyItemTo()
                                }
                            }, {
                                type : 'button',
                                text : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.EXPORT_EAS_RESULT',
                                icon : 'export',
                                onClick : function() {
                                    _this.exportEasResult()
                                }
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.BATCH_UPDATE',
                                icon : 'edit',
                                onClick : function() {
                                    _this.batchUpdate();
                                }
                            } ],

                        } ]
                    }
                } ]
            }, {
                title : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.TAB.TEST_METHOD',
                items : [ {
                    type : 'grid',
                    id : 'emc-equipt-edit-list-item-grid',
                    service : 'emcEquiptTestMethodServiceImpl',
                    checkOnActive : true,
                    // page : true,
                    deleteFormatter : function(row) {
                        return row.ext$.testcategorydesc || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertRow()
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            _this.deleteRow()
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.COPY',
                        icon : 'copy',
                        onClick : function() {
                            _this.copyRow()
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.COPYTO',
                        icon : 'copy',
                        onClick : function() {
                            if (Gikam.isEmpty(Gikam.getComp('emc-equipt-edit-list-item-grid').getSelections())) {
                                return;
                            }
                            Gikam.create('modal', {
                                width : 800,
                                height : 500,
                                title : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.MODAL.CHOOSE',
                                url : emcEquipt.baseUrl + '/page/choose-list',
                                onAfterClose : function(data) {
                                    if (Gikam.isEmpty(data)) {
                                        return;
                                    }
                                    workspace.window.showMask(true);
                                    testMethods = Gikam.getComp('emc-equipt-edit-list-item-grid').getSelections()
                                    Gikam.postText(emcEquipt.baseUrl + '/test-methods/action/copyto', Gikam.getJsonWrapper({
                                        testMethods : testMethods
                                    }, [ 'emcEquiptTestMethodServiceImpl', testMethods ], [ 'emcEquiptServiceImpl', data ])).done(function(data) {
                                    }).always(function() {
                                        workspace.window.closeMask();
                                    });
                                }
                            })

                        }
                    } ],
                    columns : emcEquipt.getItemFormFields()
                } ]

            }, {
                title : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.TAB.EQUIPT_CERT',
                items : [ {
                    type : 'grid',
                    id : 'emc-equipt-cert-edit-list-grid',
                    columns : emcEquipt.getEuiptCertListColumns(),
                    page : true,
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertEquipCert()
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            _this.deleteEquipCert()
                        }
                    } ],
                } ]
            }, emcEquiptPlan.getMaintPlanTab(), emcEquiptRecord.getMaintRecordTab(),
                emcEquiptPerCheckPlan.getPerCheckPlanTab(),emcEquiptPerCheckRecord.getPerCheckRecordTab()
                , {
                title : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.TAB.EQUIPT_RECORD',
                items : [ {
                    type : 'grid',
                    id : 'emc-equipt-use-rec-edit-list-grid',
                    columns : emcEquiptUseRecord.getUseRecordListColumns(),
                    page : true,
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            Gikam.getComp('emc-equipt-use-rec-edit-list-grid').deleteRows(emcEquipt.baseUrl + "/delete-records");
                        }
                    } ]
                } ]
            }, {
                title : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.TAB.HISTORY_CURVE',
                items : [ {
                    type : 'grid',
                    id : 'emc-equipt-edit-list-curve-grid',
                    service : 'emcCurveServiceImpl',
                    columns : emcEquipt.getCurveListParam(),
                    page : true
                } ]

            }, {
                title : '仪器维护维修记录',
                items : [ {
                    type : 'grid',
                    id : 'emc-equipt-maintain-repair-edit-list-grid',
                    columns : emcEquipt.getMainTainRepairListColumns(),
                    service : 'EmcEquiptMaintainRepairService',
                    page : true,
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            Gikam.getComp('emc-equipt-maintain-repair-edit-list-grid').insert(IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipt-maintain-repairs', [ {
                                equiptId : Gikam.getComp('emc-equipt-edit-list-grid').getActivedRow().id,
                            } ]);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            Gikam.getComp('emc-equipt-maintain-repair-edit-list-grid').deleteRows(IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipt-maintain-repairs');
                        }
                    } ]
                } ]
            },{
                    title : '附件',
                    items : [ {
                        type : 'uploader',
                        id : 'emc-equipt-edit-list-uploader',
                        dbTable : 'T_EMC_EQUIPT',
                    } ]
                } ]
        }
    },

    batchUpdate : function () {
        var grid = Gikam.getComp('emc-equipt-edit-list-test-item-grid');
        var selections = grid.getSelections();
        if (selections.length < 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }
        var modal = Gikam.create('modal', {
            width : '20%',
            height : '30%',
            title : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.EQUIPT_EDIT.MODAL.BATCH_UPDATE'
        });
        Gikam.create('layout', {
            renderTo : modal.window.$dom,
            center : {
                padding : '0 28px 0 0 ',
                items : [ {
                    type : 'form',
                    id : 'emc-equipt-edit-list-batch-update-form',
                    columns : 1,
                    fields : [ {
                        field : 'matchItemName',
                        title : 'T_EMC_EQUIPT_TEST_ITEM.MATCHITEMNAME',
                        editor : true,
                        validators : [ 'strLength(0,60]' ],
                    }, {
                        field : 'matchResultName',
                        title : 'T_EMC_EQUIPT_TEST_ITEM.MATCHRESULTNAME',
                        editor : true,
                        type : 'choose',
                        category : 'easResult',
                        single : true,
                        onBeforeChoose : function() {
                            let data = Gikam.getComp('emc-equipt-edit-list-dcu-conf-form').getData();
                            return {
                                scriptId_SEQ : (data && data.acqScriptId) || 1
                            }
                        },
                        onAfterChoose : function(rows, index) {
                            if (rows) {
                                this.setData({
                                    matchResultName : rows[0].fieldName,
                                    easResultId : rows[0].id
                                });
                            }
                        },
                        onClean : function() {
                            this.setData({
                                matchResultName : '',
                                easResultId : ''
                            });
                        },
                    } ]
                }, {
                    type : 'btnToolbar',
                    items : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        icon : 'modify',
                        onClick : ()=> {
                            var formData = Gikam.getComp('emc-equipt-edit-list-batch-update-form').getData();
                            for (var row of selections) {
                                grid.setData({
                                    id : row.id,
                                    matchItemName : formData.matchItemName,
                                    matchResultName : formData.matchResultName,
                                    easResultId : formData.easResultId
                                });
                            }
                            modal.close();
                        }
                    } ]
                } ]
            }
        })
    },

    /** 导出采集结果 */
    exportEasResult : function () {
        var activedRow = Gikam.getComp('emc-equipt-edit-list-grid').getActivedRow();
        Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}/{id}', {
            templateCode : 'emcEquipDcuExportTemplate',
            service : 'emcEquipDcuExportTemplate',
            id : activedRow.id
        })).done(function(fileUri) {
            Gikam.emcDownloadFile(IFM_CONTEXT + fileUri);
        });
    },

    deleteEquipCert : function (){
        var formData = Gikam.getComp('emc-equipt-edit-list-detail-form').getData();
        var url = Gikam.printf(emcEquipt.baseUrl + '/{id}/equipcerts', {
            id : formData.id,
        });
        Gikam.getComp('emc-equipt-cert-edit-list-grid').deleteRows(url);
    },

    insertEquipCert : function (){
        var activeRow = Gikam.getComp('emc-equipt-edit-list-grid').getActivedRow();
        if (Gikam.isEmpty(activeRow.id)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('user').title,
            url : Gikam.choose.getConfig('user').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_EMC_EQUIP_CERT',
                targetUserIdColumn : 'EMPID',
                targetFilterColumn : 'EQUIPTID',
                targetFilterValue : activeRow.id
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        empId : item.id,
                        userName : item.userName,
                        userDept : item.orgName,
                        userDeptCode : item.orgId
                    }
                });
                Gikam.getComp('emc-equipt-cert-edit-list-grid').insert(Gikam.printf(emcEquipt.baseUrl + '/{id}/equipcerts', {
                    id : activeRow.id
                }), chooseData);
            },
        });
    },

    insertRow : function() {
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('testMethod').title,
            url : Gikam.choose.getConfig('testMethod').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_EMC_EQUIPT_TEST_METHOD',
                targetTestMethodIdColumn : 'TESTMETHODID',
                targetFilterColumn : 'EQUIPTID',
                targetFilterValue : Gikam.getComp('emc-equipt-edit-list-detail-form').getData().id,
                target_assigned : '1',
                targetAssignedTable : 'T_EMC_TEST_CAPACITY',
                targetAssignedFilterColumn : 'orgId',
                targetAssignedFilterValue : workspace.user.ext$.parentorgid
            }),
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var formData = Gikam.getComp('emc-equipt-edit-list-detail-form').getData();
                var url = Gikam.printf(emcEquipt.baseUrl + '/{id}/test-methods', {
                    id : formData.id,
                });
                var chooseData = [];
                Gikam.each(rows, function(index, item) {
                    chooseData.push({
                        equiptId : formData.id,
                        testCategoryId : item.testCategoryId,
                        testMethodId : item.id,
                        testId : item.testId,
                        methodId : item.methodId,
                        orgId : workspace.user.ext$.parentorgid,
                    })
                })
                Gikam.getComp('emc-equipt-edit-list-item-grid').insert(url, chooseData);
            }
        });
    },

    deleteRow : function() {
        var formData = Gikam.getComp('emc-equipt-edit-list-detail-form').getData();
        var url = Gikam.printf(emcEquipt.baseUrl + '/{id}/test-methods', {
            id : formData.id,
        });
        Gikam.getComp('emc-equipt-edit-list-item-grid').deleteRows(url);
    },

    copyRow : function() {
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.TAB.BASE_INFO',
            url : Gikam.choose.getConfig('equipt').url + Gikam.param({
                single : true
            }),
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var url = Gikam.printf(emcEquipt.baseUrl + '/{id}/test-methods', {
                    id : rows[0].id,
                });
                var data = Gikam.getComp('emc-equipt-edit-list-item-grid').getActivedRow();
                var chooseData = [ {
                    equiptId : rows[0].id,
                    testCategoryId : data.testCategoryId,
                    testMethodId : data.testMethodId,
                    testId : data.testId,
                    methodId : data.methodId
                } ]
                Gikam.getComp('emc-equipt-edit-list-item-grid').insert(url, chooseData);
            }
        });
    },

    insertItem : function() {
        var formData = Gikam.getComp('emc-equipt-edit-list-detail-form').getData();
        var testMethodData = Gikam.getComp('emc-equipt-edit-list-item-grid').getData();
        var ids = "";
        testMethodData.map(function(item, i) {
            ids += item.testMethodId + ',';
        });
        ids = ids.substring(0, ids.length - 1);
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('testItem').title,
            url : Gikam.choose.getConfig('testItem').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_EMC_EQUIPT_TEST_ITEM',
                targetItemIdColumn : 'ITEMID',
                targetFilterColumn : 'EQUIPTID',
                targetFilterValue : formData.id,

                'scheme_only' : ids == '' ? 'true' : null,
                // TESTMETHODID_IN : ids,
                TESTMETHODIDFILTER : formData.id,
                SCRIPTFLAG_SEQ : '0',
                targetType : 'dcuConfig',
                activatedflag_SEQ : '1',
            }),
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var url = Gikam.printf(emcEquipt.baseUrl + '/{id}/test-items', {
                    id : formData.id,
                });
                var chooseData = [];
                Gikam.each(rows, function(index, item) {
                    chooseData.push({
                        equiptId : formData.id,
                        itemId : item.id,
                        testMethodId : item.testMethodId,
                    })
                })
                Gikam.getComp('emc-equipt-edit-list-test-item-grid').insert(url, chooseData);
            }
        });
    },

    deleteItem : function() {
        var formData = Gikam.getComp('emc-equipt-edit-list-detail-form').getData();
        var url = Gikam.printf(emcEquipt.baseUrl + '/{id}/test-items', {
            id : formData.id,
        });
        Gikam.getComp('emc-equipt-edit-list-test-item-grid').deleteRows(url);
    },

    copyItem : function() {
        var formData = Gikam.getComp('emc-equipt-edit-list-detail-form').getData();
        var selections = Gikam.getComp('emc-equipt-edit-list-test-item-grid').getSelections();
        if (selections.length > 0) {
            var url = Gikam.printf(emcEquipt.baseUrl + '/{id}/test-items', {
                id : formData.id,
            });
            var chooseData = [];
            selections.map(function(item) {
                chooseData.push({
                    equiptId : formData.id,
                    itemId : item.itemId,
                    testMethodId : item.testMethodId,
                    matchItemName : item.matchItemName,
                    matchResultName : item.matchResultName,
                });
            });
            Gikam.getComp('emc-equipt-edit-list-test-item-grid').insert(url, chooseData);
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
    },

    copyItemTo : function() {
        var selections = Gikam.getComp('emc-equipt-edit-list-test-item-grid').getSelections();
        if (selections.length > 0) {
            Gikam.create('modal', {
                title : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.TAB.EQUIPT',
                url : Gikam.choose.getConfig('equipt').url + Gikam.param({
                    single : true
                }),
                onAfterClose : function(rows, index) {
                    if (Gikam.isEmpty(rows)) {
                        return;
                    }
                    var url = Gikam.printf(emcEquipt.baseUrl + '/{id}/test-items', {
                        id : rows[0].id,
                    });
                    var chooseData = [];
                    selections.map(function(item) {
                        chooseData.push({
                            equiptId : rows[0].id,
                            itemId : item.itemId,
                            testMethodId : item.testMethodId,
                            matchItemName : item.matchItemName,
                            matchResultName : item.matchResultName,
                        });
                    });
                    Gikam.getComp('emc-equipt-edit-list-test-item-grid').insert(url, chooseData);
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-equipt-edit-list-layout',
            renderTo : workspace.window.$dom,
            readonly:!Gikam.isEmpty(_this.param.readonly),
            west : {
                width : '25%',
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    center : {
                        items : [ _this.loadDetailPage() ]
                    },

                } ]
            },

        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
