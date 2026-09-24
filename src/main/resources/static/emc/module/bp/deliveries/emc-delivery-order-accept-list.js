emcDelivery.orderAcceptPage = {

    getProjectGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-delivery-project-list-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/project/projects/queries',
            columns : emcDelivery.getProjectListColumns(),
            filterOpen : true,
            page : true,
            requestData : {
                targetType: 'deliveryAccept',
                processStatus_SEQ : 'done',
	            org_filter : '1',
                sample_DIFFER : '1',
                dataStatus_IN : ',',
                status_SEQ : 'auditFinished'
            },
            onCellClick : function(field, row) {
                if (field === 'projNo') {
                    _this.loadProjDetailPage(row.projNo);
                }
            },
            onRowActive : function(index, row) {
                if (row) {
                    Gikam.getComp('emc-delivery-edit-list-grid').refresh({
                        requestData : {
                            status_SEQ : 'draft',
                            org_filter : '1',
                            projId_SEQ : row.id
                        },
                        url : emcDelivery.sampleUrl + '/queries/raw',
                    });
                }
            },
            onLoadSuccess : function(data) {
                if (Gikam.isEmpty(data)) {
                    Gikam.cleanCompData([ 'emc-delivery-edit-list-grid' ]);
                } else {
                    this.activeRowByIndex(0);
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.BUTTON.REJECT',
                icon : 'reject',
                onClick : function() {
                    _this.back();
                }
            },{
                type : 'dropDownMenu',
                text : '客服联系文件',
                items : [{
                    type : 'button',
                    text : '导出文件',
                    icon : 'default',
                    onClick : function() {
                        _this.exportSampleFile();
                    },
                },{
                    type : 'button',
                    text : '查看文件',
                    icon : 'upload',
                    onClick : function() {
                        _this.viewExportSampleFile();
                    }
                }]
            } ]
        }
    },

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-delivery-edit-list-grid',
            columns : emcDelivery.getEditListColumns(),
            filterOpen : false,
            page : true,
            checkOnActive : true,
            deleteFormatter : function(row) {
                return row.sampleNo || row.id;
            },
            onRowActive : function(index, row) {
                if (row && !isNaN(row.id)) {
                    Gikam.getComp('emc-delivery-edit-list-order-grid').refresh({
                        requestData : {
                            sampleId_SEQ : row.id,
                        },
                        url : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/queries/raw',
                    });
                }
            },
            onLoadSuccess : function(data) {
                if (Gikam.isEmpty(data)) {
                    Gikam.cleanCompData([ 'emc-delivery-edit-list-order-grid' ]);
                } else {
                    this.activeRowByIndex(0);
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    var activeRow = Gikam.getComp('emc-delivery-project-list-grid').getActivedRow();
                    if (Gikam.isEmpty(activeRow)) {
                        return;
                    }
                    Gikam.getComp('emc-delivery-edit-list-grid').insert(emcDelivery.sampleUrl, [ {
                        'projId' : activeRow.id,
                        'source' : 'orderregistration'
                    } ]);
                },
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-delivery-edit-list-grid').deleteRows(emcDelivery.sampleUrl);
                },
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('deliveringLabel', Gikam.getComp('emc-delivery-edit-list-grid').getSelections());
                }
            },{
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.ANNEX',
                icon : 'upload',
                onClick : function() {
                    _this.uploadSampleFile();
                }
            },{
                type : 'button',
                text : '样品标签',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('sampling', Gikam.getComp('emc-delivery-edit-list-grid').getSelections());
                }
            }, ],
        }
    },
    uploadSampleFile : function() {
        var _this = this
        var rows = Gikam.getComp('emc-delivery-edit-list-grid').getSelections();
        if (rows && rows.length === 1) {
            Gikam.create('modal', {
                width : 800,
                height : 500,
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ANNEX',
                url : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/page/sample-annex-list' + Gikam.param({
                    id : rows[0].id,
                    bizCategory : 'photo'
                })
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },
    submit : function() {
        var grid = Gikam.getComp('emc-delivery-edit-list-grid');
	 	var selections =  grid.getSelections();
	 	//至少选择
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var rows =  Gikam.getComp('emc-delivery-edit-list-order-grid').getData();
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('EMC.MODULE.BP.DELIVERIES.TIP.NO_HAVE_ORDER');
            return;
        }
        Gikam.confirm('GIKAM.TIP.CONFIRM', 'EMC.MODULE.BP.DELIVERIES.TIP.CONFRIM_SUBMIT', function () {
            workspace.window.showMask(true);
            var url = Gikam.printf(emcDelivery.sampleUrl + '/action/submit-delivery');
            Gikam.postText(url, Gikam.getJsonWrapper({},[ '', selections ])).done(function () {
                grid.refresh();
            }).always(function () {
                workspace.window.closeMask();
            });
        });

    },

    back : function() {
        // 允许回退多条
        var grid = Gikam.getComp('emc-delivery-project-list-grid');
        var selections = grid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.confirm('GIKAM.TIP.CONFIRM', 'EMC.MODULE.BP.DELIVERIES.TIP.UNDO_PROJECT', function () {
            workspace.window.showMask(true);
            let url = IFM_CONTEXT + '/secure/emc/module/bp/project/projects/action/undo-delivery';
            Gikam.postText(url, Gikam.getJsonWrapper({},[ '', selections ])).done(function () {
                grid.refresh();
            }).always(function () {
                workspace.window.closeMask();
            });
        });
    },

    /** 加载任务详情页面 */
    loadProjDetailPage : function(projNo) {
        Gikam.create('modal', {
            id : 'projModal',
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.MODAL.PROJ_DETAIL',
            width : 980,
            height : 500,
            showCloseBtn : true,
            url : IFM_CONTEXT + '/secure/emc/module/bp/project/projects/page/project-search-detail' + Gikam.param({
                projNo : projNo
            }),
        });
    },

    /** 样品信息 */
    getOrderGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-delivery-edit-list-order-grid',
            columns : emcDelivery.getOrderColumns(),
            service : 'pfOrderServiceImpl',
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-delivery-edit-list-test-grid' ]);
                }
            },
            onRowActive : function(index, row) {
                if (row && !isNaN(row.id)) {
                    Gikam.getComp('emc-delivery-edit-list-test-grid').refresh({
                        url : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/{id}/item-groups/queries', {
                            id : row.id
                        }),
                        requestData : {
                            showOrderContainer : '1'
                        }
                    });
                }
            },
            toolbar : [{
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.addOrder();
                },
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    _this.deleteOrder();
                },
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('deliveringLabel', Gikam.getComp('emc-delivery-edit-list-order-grid').getSelections());
                }
            // }, {
            //     type : 'button',
            //     text : '样品描述',
            //     icon : 'edit',
            //     onClick : function() {
            //         _this.batchInputOrderDesc();
            //     }
            }, {
                type : 'dropDownMenu',
                text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.ORDER_QUICK_INPUT',
                items : [ {
                    type : 'button',
                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.BATCH_UPDATE',
                    icon : 'add',
                    onClick : function() {
                        _this.batchUpdateOrder();
                    }
                },{
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
                }, {
                    type : 'button',
                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.UPDATE_QC_CATEGORY',
                    icon : 'copy',
                    onClick : function() {
                        _this.updateQcCategory();
                    }
                } , {
                    type : 'button',
                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.EMPTY_QC_CATEGORY',
                    icon : 'copy',
                    onClick : function() {
                        _this.emptyQcCategory();
                    }
                }]
            } ]
        }
    },

      /** 测试信息 */
    getResultGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.CONTAINER_INFO',
                items : [{
                    type : 'layout',
                    center :{
                        items : [ {
                            type : 'grid',
                            id : 'emc-delivery-edit-list-test-grid',
                            service : 'emcOrderTaskServiceImpl',
                            columns : emcDelivery.getOrderItemGroupColumns(),
                            readonly : true,
                            toolbar : [ {
                                type : 'button',
                                text : 'GIKAM.BUTTON.CONTAINER_UPDATE',
                                icon : 'add',
                                onClick : function() {
                                    _this.updateOrderContainer();
                                },
                            }, ]
                        } ]
                    }
                }]
            } ]
        }
    },

    /** 修改容器信息 */
    updateOrderContainer : function() {
        var _this = this;
        var orderSelections = Gikam.getComp('emc-delivery-edit-list-order-grid').getSelections();
        if (Gikam.isEmpty(orderSelections)) {
            Gikam.alert('至少选择一个样品');
            return;
        };
        var testSelections = Gikam.getComp('emc-delivery-edit-list-test-grid').getSelections();
        if (Gikam.isEmpty(testSelections)) {
            Gikam.alert('至少选择一个测试');
            return;
        };
        Gikam.create('modal', {
            width : 800,
            height : 313,
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.CONTAINER_INFO',
            url : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/page/order-container-batch',
            onAfterClose : function(data) {
                if (!Gikam.isEmpty(data)) {
                    workspace.window.showMask(true);
                    Gikam.post('/secure/emc/module/bp/bp/orders/item-groups/action/update-order-container',
                        Gikam.getJsonWrapper({}, [ 'emcOrderServiceImpl', orderSelections ], [ 'emcOrderItemGroupServiceImpl', testSelections ], [ 'emcOrderTaskServiceImpl', [ data ] ])).done(function() {
                        Gikam.getComp('emc-delivery-edit-list-test-grid').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    addOrder : function() {
        var activedRow = Gikam.getComp('emc-delivery-edit-list-grid').getActivedRow();
        if (Gikam.isEmpty(activedRow)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : Gikam.choose.getConfig('order').title,
            url : IFM_CONTEXT + '/secure/emc/module/bp/deliveries/page/order-choose-list' + Gikam.param({
                targetOrder_unassigned : '1',
                targetOrderTable : 'T_EMC_ORDER',
                targetOrderColumn : 'ID',
                targetOrderIdFilterColumn : 'SAMPLEID',
                targetOrderIdFilterValue : activedRow.id,
                projId : activedRow.projId,
                targetOrderSample_unassigned : '1',
            }),
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                workspace.window.showMask(true);
                Gikam.postText(emcDelivery.sampleUrl + '/' + activedRow.id + '/orders', Gikam.getJsonWrapper({}, [ null, rows ])).done(function(data) {
                    Gikam.getComp('emc-delivery-edit-list-order-grid').refresh();
                    Gikam.getComp('emc-delivery-edit-list-grid').refreshRowById(activedRow.id);
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    deleteOrder : function() {
        var selections = Gikam.getComp('emc-delivery-edit-list-order-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.DEL_ORDER', 'EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.CONFIRM_DELETE', function () {
            var url = emcDelivery.sampleUrl + '/orders';
            workspace.window.showMask(true);
            Gikam.del(url, Gikam.getJsonWrapper(null, [ '', selections ])).done(function() {
                Gikam.getComp('emc-delivery-edit-list-order-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        }, "");
    },


    batchInputOrderDesc : function() {
        var grid = Gikam.getComp('emc-delivery-edit-list-order-grid');
        var rows = grid.getSelections();
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        var modal = Gikam.create('modal', {
            title: "批录入样品描述",
            width : 400,
            height : 350,
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
                                let formData = form.options.data;
                                let orderDesc = [formData.orderStatusText, formData.color, formData.odor].join(';');
                                rows.forEach(item => {
                                    item.orderDesc = orderDesc;
                                })

                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/' + rows[0].id, Gikam.getJsonWrapper({}, [ 'emcOrderServiceImpl', rows ])).done(function() {
                                    grid.refresh()
                                }).always(function() {
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
            fields: [ {
                field : 'orderStatus',
                title : 'T_EMC_ORDER.ORDERSTATUS',
                validators : [ 'notEmpty', 'strLength[0, 60]' ],
                type : 'select',
                category : 'sys',
                param : {
                    codeCategoryId : 'orderStatus',
                },
                onChange : function(field, value, rowIndex, oldValue, text) {
                    console.info(field, value, rowIndex, oldValue, text)
                    this.setData({
                        orderStatus : value,
                        orderStatusText : text
                    });
                }
            }, {
                field : 'color',
                title : 'T_EMC_ORDER.COLOR',
                validators : [ 'notEmpty', 'strLength[0, 60]' ],
            }, {
                field : 'odor',
                title : 'T_EMC_ORDER.ODOR',
                validators : [ 'notEmpty', 'strLength[0, 60]' ],
            }],
            autoSave: false
        });
    },

    orderQuickExport : function() {
        var orderData = Gikam.getComp('emc-delivery-edit-list-order-grid').getData();
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
        var orderData = Gikam.getComp('emc-delivery-edit-list-order-grid').getData();
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
                        Gikam.getComp('emc-delivery-edit-list-order-grid').refresh();
                    }).always(function() {
                        Gikam.window.closeMask();
                    });
            }
        });
    },

    batchUpdateOrder : function(){
        var _this = this;
        var selections = Gikam.getComp('emc-delivery-edit-list-order-grid').getSelections();
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
                            	console.log(formData);
                                selections.forEach(function (item) {
                                    item.sendDate = formData.sendDate;
                                    item.sendTime = formData.sendTime;
                                    item.orderDesc = formData.orderDesc;
                                    item.remark = formData.remark;
                                    item.volume = formData.ext$.volume;
                                    item.packing = formData.ext$.packing;
                                    item.ext$.sender = formData.ext$.sender;
                                    item.ext$.checker = formData.ext$.checker;
                                    item.labelNo = formData.labelNo;
                                    item.clientOrderName = formData.clientOrderName;
                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/project/projects/' + selections[0].id, Gikam.getJsonWrapper({}, [ 'pfOrderServiceImpl', selections ])).done(function (data) {
                                    Gikam.getComp('emc-delivery-edit-list-order-grid').refresh();
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
            fields: emcDelivery.getBatchInputFields(),
            autoSave: false
        });

    },

    updateQcCategory : function() {
        var selections = Gikam.getComp('emc-delivery-edit-list-order-grid').getSelections();
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

                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/edit-qc', Gikam.getJsonWrapper({}, [ 'emcOrderServiceImpl', selections])).done(function() {
                    Gikam.getComp('emc-delivery-edit-list-order-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    emptyQcCategory : function() {
        var selections = Gikam.getComp('emc-delivery-edit-list-order-grid').getSelections();
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
            Gikam.getComp('emc-delivery-edit-list-order-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    exportSampleFile : function() {
        var _this = this
        var rows = Gikam.getComp('emc-delivery-project-list-grid').getSelections();
        if (rows && rows.length === 1) {
            Gikam.create('modal', {
                width : 800,
                height : 500,
                url : Gikam.choose.getConfig('reportLine').url+ Gikam.param({
                	REPORTCONFIGID_IN : 'deliveringLabel',
                	activatedFlag_SEQ :'1'
                }),
                title : '报表',
                onAfterClose : function(data) {
                    var url1 = Gikam.printf(emcDelivery.projectUrl+'/action/sample-contract-file');
                    var reportConfigId = data.reportConfigId;
                    Gikam.postText(url1, Gikam.getJsonWrapper(null,['coreReportConfigLineService',data] ,[ 'pfProjectService', rows ])).done(function(data){
                        console.log(data);
                        data   =   data.replace(/\s+/g,"");
                    	var fileIds = [];
                        var selections = data.split(",");
                        var ro = selections.filter(function (s) {
                            return s && s.trim();
                        });
                        console.log(selections);
                        console.log(ro);
                        if (ro.length > 0) {
                            ro.forEach(function (item) {
                                fileIds.push({id : item})
                            })
                            console.log(fileIds);
                            if(fileIds.length>1){
                                Gikam.postText('/core/module/item/files/action/pack',Gikam.getJsonWrapper({}, [ '', fileIds])
                                ).done(function (fileUrl) {
                                    if (fileUrl != '' && fileUrl != undefined) {
                                        Gikam.emcDownloadFile(IFM_CONTEXT + fileUrl);
                                    }
                                	Gikam.alert('导出完成！');
                                })
                            }else if (fileIds.length == 1){
                                Gikam.getJson(IFM_CONTEXT + '/core/module/item/files/' + selections[0]).done(function(coreFile) {
                                    Gikam.emcDownloadFile(IFM_CONTEXT + coreFile.downloadUrl);
                                });
                            	Gikam.alert('导出完成！');
                            }

                        }else{
                        	Gikam.alert('导出失败！');
                        }
                        Gikam.getComp('emc-delivery-project-list-grid').refresh();
                    });
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }

    },

    viewExportSampleFile : function() {
        var _this = this
        var rows = Gikam.getComp('emc-delivery-project-list-grid').getSelections();
        if (rows && rows.length === 1) {
            Gikam.create('modal', {
                width : 800,
                height : 500,
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ANNEX',
                url : emcDelivery.projectUrl + '/page/deliver-annex-list?id=' + rows[0].id
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-delivery-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 300,
                items : [ _this.getProjectGridParam() ]
            },
            center : {
                items : [ _this.getGridParam() ]
            },
            east : {
                width : '50%',
                title : Gikam.propI18N('EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.TITLE.ORDER_INFO'),
                collapse : false,
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ this.getOrderGridParam() ]
                    },
                    center : {
                        items : [ this.getResultGridParam() ]
                    }
                } ]
            }
        });
        Gikam.getAsyncComp('emc-delivery-edit-list-test-grid').done(function(grid) {
            grid.options.checkOneOnActive = true;
        });
    },

    init : function() {
        this.create();
    }
};
