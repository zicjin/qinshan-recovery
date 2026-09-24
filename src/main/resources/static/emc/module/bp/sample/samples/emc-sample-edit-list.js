emcSample.editPage = {

    getSamplePrepareGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            refreshFromSearch: false,
            service: 'emcFolderServiceImpl',
            instantSavePath: IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/instant',
            id: 'emc-sample-edit-list-grid',
            columns: emcSample.getSamplePrepareListColumns(),
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    // 监测点都能编辑
                    /*var activedRow = Gikam.getComp('emc-sample-edit-list-project-prepare-grid').getActivedRow();
                    if (activedRow.ext$.editmonitorinfo && activedRow.ext$.editmonitorinfo=='1') {
                        data.map(function(item) {
                            Gikam.getComp('emc-sample-edit-list-grid').toFieldsEdit( item.gridIndex-1,['monitorPointName'] );
                        });
                    } else {
                        data.map(function(item) {
                            Gikam.getComp('emc-sample-edit-list-grid').toFieldsReadonly(item.gridIndex-1,['monitorPointName'] );
                        });
                    }*/
                    //this.activeRowByIndex(0);
                } else {
                    Gikam.getAsyncComp('emc-sample-edit-list-order-prepare-grid').done(function (grid) {
                        grid.cleanData();
                    });
                    Gikam.getAsyncComp('emc-sample-edit-list-order-container-info-grid').done(function (grid) {
                        grid.cleanData();
                    });
                }
            },
            group: {
                fields: ['orderCategory'],
            },
            deleteFormatter: function (row) {
                return row.certNo || row.id;
            },
            onRowActive: function (index, row) {
                _this.onOrderFolderActive(row);
            },
            toolbar: [{
                type: 'button',
                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.REJECT',
                icon: 'reject',
                onClick: function () {
                    var selections = Gikam.getComp('emc-sample-edit-list-grid').getSelections();
                    /*                    if (selections.length != 1) {
                                            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM.BACK');
                                            return;
                                        }*/
                    var chooseData = selections.map(function (item) {
                        return {
                            id: item.id
                        }
                    });

                    if (selections.find(f => f.source === 'COPY')) {
                        Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.COPYNOTREJECT');
                        return;
                    }
                    // var flag = true
                    // selections.forEach(function(row) {
                    //     if (row.source=='COPY') {
                    //         flag = false
                    //     }
                    // })
                    // if (!flag) {
                    //     Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.COPYNOTREJECT');
                    //     return;
                    // }

                    Gikam.create('workflow').undo({
                        data: chooseData,
                        pageObject: {
                            baseUrl: IFM_CONTEXT + '/secure/emc/module/bp/bp/folders',
                            workflow: {
                                dbTable: 'T_EMC_FOLDER',
                                columns: [{
                                    field: 'id',
                                    title: 'T_EMC_FOLDER.ID'
                                }]
                            }
                        },
                        requestParam: {
                            id: selections[0].id
                        }
                    }).done(function () {
                        var grid = Gikam.getComp('emc-sample-edit-list-grid');
                        grid.refresh().done(function () {
                            if (grid.getData().length < 1) {
                                Gikam.getComp('emc-sample-edit-list-project-prepare-grid').refresh();
                            }
                        });
                    });
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.BUTTON.MONITOR_POINT_PROPERTY',
                icon: 'add',
                onClick: function () {
                    _this.setMonitorProperty();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.COPY_FOLDER',
                icon: 'copy',
                onClick: function () {
                    var copyFolder = function () {
                        var selections = Gikam.getComp('emc-sample-edit-list-grid').getSelections();
                        if (selections.length > 1) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                            return;
                        }
                        if (Gikam.isEmpty(selections)) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                            return;
                        }

                        var gridData = Gikam.getComp('emc-sample-edit-list-grid').getData();
                        var rows = gridData.reduce(function (oldRows, item) {
                            return Gikam.isEmpty(oldRows.folderNo) && (oldRows.folderNo > item.folderNo) ? oldRows : item;
                        }, {});
                        var url = '/secure/emc/module/bp/bp/folders/' + rows.folderNo + '/action/copy-folders';
                        workspace.window.showMask(true);
                        Gikam.postText(url, Gikam.getJsonWrapper(null, ['', selections])).done(function () {
                            Gikam.getComp('emc-sample-edit-list-grid').refresh();
                        }).always(function () {
                            workspace.window.closeMask();
                        });
                    }
                    Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.COPY_FOLDER', "EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.CONFIRM_COPY_FOLDER", copyFolder, "");
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.BATCH_UPDATE',
                icon: 'add',
                onClick: function () {
                    _this.batchUpdateFolder();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.BUTTON.CONFIG_COMPARISON',
                icon: 'copy',
                hidden: true,
                onClick: function () {
                    _this.configComparison();
                }
            }, {
                type: 'button',
                text: '打印标签',
                icon: 'print',
                onClick: function () {
                    Gikam.viewReport('sampling', Gikam.getComp('emc-sample-edit-list-grid').getSelections());
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.DELETE',
                icon: 'remove-row',
                onClick: function () {
                    _this.deleteFolder();
                }
            }, {
                //     type : 'button',
                //     text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SYNC_TEST_ITEM',
                //     icon : 'refresh',
                //     onClick : function() {
                //         _this.syncTestItem();
                //     }
                // }, {
                type: 'form',
                id: 'emc-sample-edit-list-prepare-form',
                fields: emcSample.getSamplePrepareShowAllField(),
                autoSave: false,
            },]
        }
    },
    //采样准备-点位-批录入
    batchUpdateFolder: function () {
        var grid = Gikam.getComp('emc-sample-edit-list-grid');
        var selections = grid.getSelections();

        if (selections.length > 0) {

            var idArr = selections.map(function (item) {
                return item.sampleOrgId;
            });
            var ids = idArr.join(",");
            Gikam.create('modal', {
                title: 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.MODAL.SAMPLEINFO',
                width: 800,
                height: 600,
                url: '/secure/emc/module/bp/sample/sampling-plans/page/sample-info' + Gikam.param({
                    ids: ids,
                    single: true,
                    firstDate: selections[0].planSampleDate,
                    sceneUserName: selections[0].sceneUserId,
                    sampleUserName: selections[0].sampleUserId,
                }),
                onAfterClose: function (data) {
                    if (Gikam.isEmpty(data)) {
                        return;
                    }
                    let saveData = selections.map(function (item) {
                        return Gikam.extend({
                            id: item.id
                        }, data);
                    });
                    workspace.window.showMask();
                    Gikam.put(IFM_CONTEXT + '/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, ['emcFolderServiceImpl', saveData])).done(function () {
                        Gikam.getComp('emc-sample-edit-list-grid').refresh();
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            });

            // var modal = Gikam.create('modal', {
            //     title: "批录入",
            //     width : 400,
            //     height : 200,
            // });
            // var layout = Gikam.create('layout', {
            //     renderTo: modal.window.$dom,
            //     center: {
            //         items: [ {
            //             type: 'btnToolbar',
            //             items: [ {
            //                 type : 'button',
            //                 text : 'GIKAM.BUTTON.CONFIRM',
            //                 class : 'blue',
            //                 onClick : function() {
            //                     if (form.validate()) {
            //                         let formData = form.options.data;
            //                         selections.forEach(item => {
            //                             item.planSampleDate = formData.planSampleDate;
            //                         })
            //
            //                         Gikam.getLastModal().window.showMask(true);
            //                         Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/instant' , Gikam.getJsonWrapper({}, [ 'emcFolderServiceImpl', selections ])).done(function() {
            //                             grid.refresh()
            //                         }).always(function() {
            //                             Gikam.getLastModal().window.closeMask();
            //                             modal.close();
            //                         });
            //                     }
            //                 }
            //             }, {
            //                 type : 'button',
            //                 text : 'GIKAM.BUTTON.CANCEL',
            //                 onClick : function () {
            //                     modal.close();
            //                 }
            //             } ]
            //         }]
            //     }
            // });
            //
            // var form = Gikam.create('form', {
            //     renderTo: layout.options.center.$dom,
            //     columns: 1,
            //     titleWidth: 100,
            //     fields: [ {
            //         field : 'planSampleDate',
            //         title : 'T_EMC_FOLDER.PLANSAMPLEDATE',
            //         validators : [ 'notEmpty'],
            //         type : 'date',
            //     }],
            //     autoSave: false
            // });


            // var activedRow = Gikam.getComp('emc-project-edit-list-grid').getActivedRow();
            // Gikam.create('modal', {
            //     width : 400,
            //     height : '70%',
            //     title : 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.BATCH_UPDATE_ORDER',
            //     url : emcProject.baseUrl + '/page/batch-update-order',
            //     onAfterClose : function(data) {
            //         if (!Gikam.isEmpty(data)) {
            //             workspace.window.showMask(true);
            //             selections.map(function(item) {
            //                 item.sendDate = data.sendDate;
            //                 item.sendTime = data.sendTime;
            //                 item.orderDesc = data.orderDesc;
            //                 item.remark = data.remark;
            //                 item.volume = data.ext$.volume;
            //                 item.packing = data.ext$.packing;
            //                 item.senderId = data.senderId;
            //                 item.ext$.sender = data.ext$.sender;
            //                 item.ext$.checker = data.ext$.checker;
            //                 item.labelNo = data.labelNo;
            //                 item.clientOrderName = data.clientOrderName;
            //                 item.ext$.isblank = data.ext$.isblank;
            //
            //             });
            //
            //             Gikam.getComp('emc-project-edit-list-base-info-form').setData({
            //                 sendUsername : data.ext$.sender,
            //                 sendDate : data.sendDate,
            //                 ext$ : {
            //                     senduserid : data.senderId
            //                 }
            //             });
            //
            //             // Gikam.put(emcProject.baseUrl + '/' + activedRow.id, Gikam.getJsonWrapper({}, [ 'emcOrderServiceImpl', selections ])).done(function() {
            //             //     Gikam.getComp('emc-project-edit-list-order-grid').refresh();
            //             // }).always(function() {
            //             //     workspace.window.closeMask();
            //             // });
            //             Gikam.put(emcProject.orderUrl + '/instant', Gikam.getJsonWrapper({}, [ 'emcOrderServiceImpl', selections ])).done(function() {
            //                 Gikam.getComp('emc-project-edit-list-order-grid').refresh();
            //             }).always(function() {
            //                 workspace.window.closeMask();
            //             });
            //
            //         }
            //     }
            // });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    // 数据录入
    getDataInputGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            refreshGrid: false,
            service: 'emcSampleServiceImpl',
            refreshFromSearch: false,
            id: 'emc-sample-edit-list-project-grid',
            url: '/secure/emc/module/bp/project/projects/queries/undoable',
            columns: emcSample.getProjectListColumns(),
            page: true,
            order: {
                urgent: 'desc'
            },
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    //this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData(['emc-sample-edit-list-project-sample-grid', 'emc-sample-edit-list-data-input-order-task-grid', 'emc-sample-edit-list-data-input-container-info-grid', 'emc-sample-edit-list-data-input-test-grid']);
                }
            },
            onRowActive: function (index, row) {
                _this.onOrderProjectActive(row);
            },
            requestData: {
                targetType: 'sample',
                status_IN: 'auditFinished,reportEdit',
                sampleUserId: workspace.user.id,
                projectType: 'sampleFolderProject',
                // folderStatus: 'draw',
                sample_IN: '1',
                org_filter: '1',
            },
            onCellClick: function (field, row) {
                if (field === 'projNo') {
                    _this.loadProjDetailPage(row.projNo);
                }
            },
            toolbar: [{
                type: 'button',
                text: '现场工况',
                icon: 'modify',
                onClick: function () {
                    _this.loadWorkConditionPage();
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.PREVIEW',
                icon: 'print',
                onClick: function () {
                    Gikam.viewReport('rasproject', Gikam.getComp('emc-sample-edit-list-project-grid').getSelections());
                }
            }, {
                type: 'form',
                id: 'emc-sample-edit-list-form',
                fields: emcSample.getProjectShowAllField(),
                autoSave: false,
            },]
        }
    },

    // 采样准备
    getProjectGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            refreshGrid: false,
            service: 'emcSampleServiceImpl',
            refreshFromSearch: false,
            id: 'emc-sample-edit-list-project-prepare-grid',
            url: '/secure/emc/module/bp/project/projects/queries/undoable',
            columns: emcSample.getProjectListColumns(),
            page: true,
            order: {
                urgent: 'desc'
            },
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    //this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData(['emc-sample-edit-list-grid', 'emc-sample-edit-list-order-prepare-grid', 'emc-sample-edit-list-order-container-info-grid']);
                }
            },
            onRowActive: function (index, row) {
                _this.onPrepareProjectActive(row);
            },
            requestData: {
                targetType: 'sample',
                status_IN: 'auditFinished,reportEdit',
                projectType: 'folderProject',
                folderStatus: 'draw',
                sampleUserId: workspace.user.id,
                sample_IN: '1',
                org_filter: '1',
            },
            onCellClick: function (field, row) {
                if (field === 'projNo') {
                    _this.loadProjDetailPage(row.projNo);
                }
            },
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.TASK',
                icon: 'print',
                onClick: function () {
                    Gikam.viewReport('rasproject', Gikam.getComp('emc-sample-edit-list-project-prepare-grid').getSelections());
                }
            }, {
                type: 'button',
                text: '仪器使用',
                icon: 'add',
                onClick: function () {
                    var selections = Gikam.getComp('emc-sample-edit-list-project-prepare-grid').getSelections();
                    if (selections.length < 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    var projIds = [];
                    var projNos = [];
                    selections.map(function (item) {
                        projIds.push(item.id);
                        projNos.push(item.projNo);
                    });
                    Gikam.create('modal', {
                        title: '仪器出库',
                        url: '/secure/emc/module/mdm/basemdm/equipt-requisitions/page/instrument-out' + Gikam.param({
                            projIds: projIds,
                            projNos: projNos,
                            sample: '1',
                            projIds_DIFFER: '1,'
                        }),
                        width: '80%',
                        height: '80%',
                    });
                }
            }, {
                type: 'button',
                text: '报表预览',
                icon: 'print',
                onClick: function () {
                    Gikam.viewReport('yangpinbianhao', Gikam.getComp('emc-sample-edit-list-project-prepare-grid').getSelections());
                }
            }, {
                type: 'form',
                id: 'emc-sample-edit-list-project-form',
                fields: emcSample.getProjectPrepareShowAllField(),
                autoSave: false,
            }, {
                type: 'button',
                text: '容器预览',
                icon: 'print',
                onClick: function () {
                    Gikam.viewReport('orderContainer', Gikam.getComp('emc-sample-edit-list-project-prepare-grid').getSelections());
                }
            }, ]
        }
    },

    // 结果录入-按采样单
    getResultInputDataInputGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            service: 'emcSampleServiceImpl',
            refreshFromSearch: false,
            id: 'emc-sample-edit-list-project-grid',
            url: '/secure/emc/module/bp/project/projects/queries/undoable',
            columns: emcSample.getProjectListColumns(),
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    //this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData(['emc-sample-edit-list-project-sample-grid', 'emc-sample-edit-list-data-input-order-task-grid', 'emc-sample-edit-list-data-input-container-info-grid', 'emc-sample-edit-list-data-input-test-grid']);
                }
            },
            onRowActive: function (index, row) {
                _this.onResultInputOrderProjectActive(row);
            },
            requestData: {
                targetType: 'resultInput',
                status_SEQ: 'auditFinished',
                sampleUserId: workspace.user.id,
            },
            toolbar: [{
                type: 'form',
                id: 'emc-sample-edit-list-form',
                fields: emcSample.getProjectShowAllField(),
                autoSave: false,
            }]
        }
    },

    // 采样准备 - 样品
    getOrderGridParam: function () {
        var _this = this;
        return {
            type: 'tab',
            fill: true,
            panels: [{
                title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.TAB.ORDER.BASE_INFO',
                items: [{
                    type: 'grid',
                    id: 'emc-sample-edit-list-order-prepare-grid',
                    service: 'emcOrderServiceImpl',
                    url: _this.getOrderUrl(),
                    columns: emcSample.getSamplePrepareOrderColumns(),
                    onRowActive: function (index, row) {
                        _this.onOrderActive(row);
                    },
                    deleteFormatter: function (row) {
                        return row.orderNo || row.id;
                    },
                    onLoadSuccess: function (data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        } else {
                            Gikam.cleanCompData(['emc-sample-edit-list-order-container-info-grid']);
                        }
                    },
                    toolbar: [{
                        type: 'dropDownMenu',
                        text: 'GIKAM.BUTTON.COPY',
                        items: [{
                            type: 'button',
                            text: 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.BUTTON.COPYQCORDER',
                            icon: 'copy',
                            onClick: function () {
                                _this.copyQcOrder();
                            }
                        }, {
                            type: 'button',
                            text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.COPY_ORDER',
                            icon: 'copy',
                            onClick: function () {
                                var copyOrder = function () {
                                    var selections = Gikam.getComp('emc-sample-edit-list-order-prepare-grid').getSelections();
                                    if (Gikam.isEmpty(selections)) {
                                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                        return;
                                    }
                                    if (selections.length > 1) {
                                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                        return;
                                    }
                                    if (selections[0].qcCategoryId) {
                                        Gikam.alert('无法对质控样复制样品操作');
                                        return;
                                    }
                                    var url = '/secure/emc/module/bp/bp/orders/action/copy-orders';
                                    workspace.window.showMask(true);
                                    Gikam.postText(url, Gikam.getJsonWrapper(null, ['', selections])).done(function () {
                                        Gikam.getComp('emc-sample-edit-list-order-prepare-grid').refresh();
                                    }).always(function () {
                                        workspace.window.closeMask();
                                    });
                                }
                                Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.COPY_ORDER', "EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.CONFIRM_COPY_ORDER", copyOrder, "");
                            }
                        }]
                    }, {
                        type: 'dropDownMenu',
                        text: 'GIKAM.BUTTON.DELETE',
                        items: [{
                            type: 'button',
                            text: 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.BUTTON.DELETEQCORDER',
                            icon: 'remove-row',
                            onClick: function () {
                                _this.deleteQcOrder();
                            }
                        }, {
                            type: 'button',
                            text: 'EMC.MODULE.BP.ORDER.ORDERRECEIPTS.BUTTON.DELETEORDER',
                            icon: 'remove-row',
                            onClick: function () {
                                _this.deleteNotQcOrder();
                            }
                        }]
                    }, {
                        type: 'button',
                        text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.UPDATE_TEST',
                        icon: 'edit',
                        onClick: function () {
                            _this.updateItemGroup();
                        }
                    }, {
                        type: 'button',
                        text: '移动测试',
                        icon: 'edit',
                        onClick: function () {
                            _this.moveTask();
                        }
                    }]
                },]
            }]
        }
    },

    // 采样准备 - 容器信息
    getOrderTaskGridParam: function () {
        var _this = this;
        return {
            type: 'tab',
            fill: true,
            width: Gikam.width.XL,
            panels: [{
                title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.TAB.SAMPLE_PREPARE_CONTAINER.BASE_INFO',
                items: [{
                    type: 'grid',
                    id: 'emc-sample-edit-list-order-container-info-grid',
                    service: 'emcOrderTaskServiceImpl',
                    columns: emcSample.getSamplePrepareOrderContainerColumns(),
                    onLoadSuccess: function (data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        }
                    },
                    onBeforeUpdate: function (data, keys) {
                        var sampleWeight = data.row.ext$.sampleweight;
                        if (!Gikam.isEmpty(sampleWeight)) {
                            var multiTestId = data.row.ext$.multitestid
                            if (!Gikam.isEmpty(multiTestId)) {
                                var ids = multiTestId.split(",");
                                var relationTests = [];
                                ids.forEach(function (id) {
                                    if (id !== data.row.id + '') {
                                        relationTests.push({
                                            id: id,
                                            ext$: {
                                                sampleweight: sampleWeight
                                            }
                                        });
                                    }
                                })

                                if (!Gikam.isEmpty(relationTests)) {
                                    Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/instant', Gikam.getJsonWrapper({}, ['emcOrderTaskServiceImpl', relationTests]));
                                }
                            }
                        }
                    },
                    toolbar: [{
                        type: 'button',
                        text: 'GIKAM.BUTTON.CONTAINER_UPDATE',
                        icon: 'modify',
                        onClick: function () {
                            _this.updateOrderTask();
                        },
                    }, {
                        type: 'button',
                        text: 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.BUTTON.SYNC_CONTAINER_INFO',
                        icon: 'refresh',
                        onClick: function () {
                            _this.syncContainerInfo();
                        },
                    }]
                },]
            }]
        }
    },

    setMonitorProperty: function () {
        var _this = this;

        var selections = Gikam.getComp('emc-sample-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var testOrgIds = '';
        selections.forEach(function (item) {
            if (item.ext$.testorgid !== undefined) {
                testOrgIds += "," + item.ext$.testorgid;
            }
        });

        if (Gikam.isEmpty(testOrgIds)) {
            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.UN_MONITOR_POINT');
            return;
        }

        var folderIds = selections.map(item => item.id);
        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.TITLE.MONITORPOINT',
            width: 800,
            height: 600,
            url: '/secure/emc/module/bp/sample/sampling-plans/page/monitor-property' + Gikam.param({
                id_IN: folderIds.join()
            }),
        });
    },

    getAllProjectUrl: function () {
        return Gikam.printf('/secure/emc/module/bp/project/projects/queries/raw');
    },

    updateProjectStatus: function () {
        var selections = Gikam.getComp('emc-sample-edit-list-project-grid').getSelections();
        if (selections.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        workspace.window.showMask(true);
        Gikam.put('/secure/emc/module/bp/bp/folders/action/update-project-status', Gikam.getJsonWrapper({}, [null, selections])).done(function (data) {
            Gikam.getComp('emc-sample-edit-list-project-grid').refresh();
            Gikam.getComp('emc-sample-edit-list-grid').refresh();
        }).always(function () {
            workspace.window.closeMask();
        });
    },

    getOrderUrl: function (folderId) {
        if (folderId) {
            return Gikam.printf(emcSample.baseUrl + '/{id}/orders/queries', {
                id: folderId
            })
        }
    },

    // 样品容器信息
    getOrderTaskUrl: function (orderId) {
        if (orderId) {
            // 需要处理 testName
            return Gikam.printf('/secure/emc/module/bp/bp/orders/{id}/order-tasks/queries', {
                id: orderId
            })
        }
    },

    getProjectSampleUrl: function (id) {
        if (id) {
            return Gikam.printf(emcSample.baseUrl + '/queries/raw', {
                id: id
            })
        }
    },

    // 样品和测试
    getSampleTestUrl: function () {
        return Gikam.printf('/secure/emc/module/bp/bp/orders/queries/raw', {})
    },

    // 查询样品
    getOrderSmapleUrl: function () {
        return Gikam.printf('/secure/emc/module/bp/bp/orders/queries/raw')
    },

    onSampleTestActive: function (row) {
        var _this = this;
        var orderCategory = row.orderCategory;
        let columns = emcSample.getSampleTestColumns();
        if (orderCategory == null || orderCategory === '') {
            return;
        }
        //需要获取检测模板组的名称用其进行判断
        var url = Gikam.printf(emcSample.baseUrl + '/action/select-category-code');
        var that = this;
        Gikam.postText(url, Gikam.getJsonWrapper({}, ['', [row]])).done(function (data) {
            var orderCategoryCode = data;
        }).always(function (str) {
            var orderCategoryCode = str;
            if (orderCategoryCode == 'T' || orderCategoryCode == 'O') {
                columns = emcSample.getSampleTestColumns1();
            }
            if (row) {
                Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').refreshColumns(columns);
                Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').refresh({
                    url: _this.getSampleTestUrl(row.id),
                    requestData: {
                        sampleId_SEQ: row.id,
                        orderId_SEQ: '',
                    }
                });

                //有组织废气，无组织废气，环境空气，的采样开始结束时间非必填，其他必填
                if (orderCategory == '有组织废气' || orderCategory == '无组织废气' || orderCategory == '环境空气') {
                    Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').removeFieldsValidator(['sampleBeginDate', 'sampleBeginTime', 'sampleCompleteDate', 'sampleCompleteTime'], ['notEmpty']);
                }
            }
        });


    },

    // 激活样品
    onOrderActive: function (row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-sample-edit-list-order-container-info-grid').refresh({
                url: IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/containers/queries',
                requestData: {
                    orderId: row.id
                }
            });
        }
    },

    onOrderFolderActive: function (row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-sample-edit-list-order-prepare-grid').setOptions({
                requestData: {
                    // 过滤条件, 要显示 sampleId 为空 的点位
                    folderId_SEQ: row.id,
                    sampleId: 'empty'
                }
            });
            Gikam.getComp('emc-sample-edit-list-order-prepare-grid').refresh({
                url: _this.getOrderSmapleUrl(row.id),
            });
        }
    },

    onOrderProjectActive: function (row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-sample-edit-list-project-sample-grid').setOptions({
                requestData: {
                    projId_SEQ: row.id,
                    status_SEQ: 'draft',
                    org_filter: '1',
                }
            });
            Gikam.getComp('emc-sample-edit-list-project-sample-grid').refresh({
                url: _this.getProjectSampleUrl(row.id),
            });

            Gikam.cleanCompData(['emc-sample-edit-list-data-input-order-task-grid', 'emc-sample-edit-list-data-input-container-info-grid', 'emc-sample-edit-list-data-input-test-grid']);
        }
    },

    onPrepareProjectActive: function (row) {
        var _this = this;
        if (row) {
            var sceneUserId = '';
            var sampleUserId = '';
            var showFlag = Gikam.getComp('emc-sample-edit-list-prepare-form').getField('showFlag');
            if (showFlag.value != '1') {
                sceneUserId = workspace.user.id;
                sampleUserId = workspace.user.id;
            }
            Gikam.getComp('emc-sample-edit-list-grid').setOptions({
                requestData: {
                    targetType: 'sampleprepare',
                    sceneUserId: sceneUserId,
                    sampleUserId: sampleUserId,
                    projectstatus_DIFFER: 'cancel',
                    status_SEQ: 'draw',
                    projId_SEQ: row.id
                },
            });
            Gikam.getComp('emc-sample-edit-list-grid').refresh({
                url: '/secure/emc/module/bp/bp/folders/queries/undoable',
            });
        }
    },

    onResultInputOrderProjectActive: function (row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-sample-edit-list-project-sample-grid').setOptions({
                requestData: {
                    projId_SEQ: row.id,
                    status_IN: 'logged',
                }
            });
            Gikam.getComp('emc-sample-edit-list-project-sample-grid').refresh({
                url: emcSample.baseUrl + '/queries',
            });

            Gikam.cleanCompData(['emc-sample-edit-list-data-input-order-task-grid', 'emc-sample-edit-list-data-input-container-info-grid', 'emc-sample-edit-list-data-input-test-grid']);
        }
    },

    // 数据录入 样品 - 容器信息
    onSampleOrderActive: function (row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-sample-edit-list-data-input-container-info-grid').refresh({
                //url : _this.getOrderTaskUrl(row.id),
                url: IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/containers/queries',
                requestData: {
                    orderId: row.id
                }
            });

            Gikam.getComp('emc-sample-edit-list-data-input-test-grid').refresh({
                url: IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/subpackages/queries',
                requestData: {
                    orderId_SEQ: row.id,
                    analysisType_SEQ: 'analysis'
                },
                order: {
                    testId: 'asc'
                }
            });
        }
    },

    // 数据录入 - 容器信息和测试信息
    getOrderTaskTab: function () {
        var _this = this;
        return {
            type: 'tab',
            fill: true,
            width: Gikam.width.XL,
            panels: [{
                title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.CONTAINER_INFO',
                items: [{
                    type: 'grid',
                    id: 'emc-sample-edit-list-data-input-container-info-grid',
                    service: 'emcOrderTaskServiceImpl',
                    columns: emcSample.getSamplePrepareOrderContainerColumns(),
                    onLoadSuccess: function (data) {
                        if (!Gikam.isEmpty(data)) {
                            //this.activeRowByIndex(0);
                        }
                    },
                    toolbar: [{
                        type: 'button',
                        text: 'GIKAM.BUTTON.CONTAINER_UPDATE',
                        icon: 'modify',
                        onClick: function () {
                            _this.updateDataInputOrderTask();
                        },
                    }]
                },]
            }, {
                title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.TAB.SAMPLE_PREPARE_CONTAINER.BASE_INFO',
                items: [{
                    type: 'grid',
                    id: 'emc-sample-edit-list-data-input-test-grid',
                    service: 'emcOrderTaskServiceImpl',
                    columns: emcSample.getSampleTestListColumns(),
                    onLoadSuccess: function (data) {
                        if (!Gikam.isEmpty(data)) {
                            //this.activeRowByIndex(0);
                        }
                    },
                    toolbar: [{
                        type: 'dropDownMenu',
                        text: 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.SUBCONTRACTOR',
                        items: [{
                            type: 'button',
                            text: 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.ADD_SUBCONTRACTOR',
                            icon: 'add',
                            onClick: function () {
                                _this.addSubcontractor();
                            }
                        }, {
                            type: 'button',
                            text: 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.DEL_SUBCONTRACTOR',
                            icon: 'remove-row',
                            onClick: function () {
                                _this.delSubcontractor();
                            }
                        }]
                    }]
                },]
            }]
        }
    },

    /** 设置分包商 */
    addSubcontractor: function () {
        var selections = Gikam.getComp('emc-sample-edit-list-data-input-test-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var orders = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getSelections();
        Gikam.create('modal', {
            width: '20%',
            height: '50%',
            title: Gikam.choose.getConfig('subcontractor').title,
            url: Gikam.choose.getConfig('subcontractor').url + Gikam.param({
                single: true,
            }),
            onAfterClose: function (data) {
                if (!Gikam.isEmpty(data)) {
                    workspace.window.showMask(true);
                    selections.map(function (item) {
                        item.subcontractorId = data[0].id;
                    });
                    Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/edit-subcontractor', Gikam.getJsonWrapper({}, ['', orders], ['', selections])).done(function () {
                        Gikam.getComp('emc-sample-edit-list-data-input-test-grid').refresh();
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    /** 删除分包商 */
    delSubcontractor: function () {
        var selections = Gikam.getComp('emc-sample-edit-list-data-input-test-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.CONFIRM_TO_EDIT', 'EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.CONFIRM_DELETE_SUBCONTRACTOR', function () {
            var orders = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getSelections();
            workspace.window.showMask(true);
            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/delete-subcontractor', Gikam.getJsonWrapper({}, ['', orders], ['', selections])).done(function () {
                Gikam.getComp('emc-sample-edit-list-data-input-test-grid').refresh();
            }).always(function () {
                workspace.window.closeMask();
            });
        })
    },

    // 采样单
    getProjectSampleGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-sample-edit-list-project-sample-grid',
            service: 'emcSampleServiceImpl',
            columns: emcSample.getProjectSampleColumns(),
            deleteFormatter: function (row) {
                return row.sampleNo || row.id;
            },
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    //this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData(['emc-sample-edit-list-data-input-order-task-grid', 'emc-sample-edit-list-data-input-container-info-grid']);
                    Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').setOptions({
                        url: null
                    });
                    Gikam.getComp('emc-sample-edit-list-data-input-container-info-grid').setOptions({
                        url: null
                    });
                }
            },
            onRowActive: function (index, row) {
                _this.onSampleTestActive(row);
            },
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.SUBMIT',
                icon: 'submit',
                onClick: function () {
                    var grid = Gikam.getComp('emc-sample-edit-list-project-sample-grid');
                    Gikam.create('workflow').submit({
                        data: grid.getSelections(),
                        pageObject: emcSample
                    }).done(function () {
                        var projectGrid = Gikam.getComp('emc-sample-edit-list-project-grid');
                        projectGrid.refresh();
                        grid.refresh();
                        Gikam.cleanCompData(['emc-sample-edit-list-data-input-order-task-grid', 'emc-sample-edit-list-data-input-container-info-grid']);
                    });
                },
            }, {
                type: 'dropDownMenu',
                text: 'GIKAM.BUTTON.SAMPLE',
                hidden: _this.getHidden(),
                items: [{
                    type: 'button',
                    text: 'GIKAM.BUTTON.ADD_SAMPLE',
                    icon: 'add',
                    onClick: function () {
                        var activeRows = Gikam.getComp('emc-sample-edit-list-project-grid').getActivedRow();
                        Gikam.getComp('emc-sample-edit-list-project-sample-grid').insert(emcSample.baseUrl, [{
                            'projId': activeRows.id,
                        }]);
                    },
                }, {
                    type: 'button',
                    text: 'GIKAM.BUTTON.DEL_SAMPLE',
                    icon: 'remove-row',
                    onClick: function () {
                        var formData = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
                        var url = Gikam.printf(emcSample.baseUrl);
                        Gikam.getComp('emc-sample-edit-list-project-sample-grid').deleteRows(url).done(function () {
                            Gikam.getComp('emc-sample-edit-list-grid').refresh();
                        });
                    }
                }, {
                    type: 'button',
                    text: 'GIKAM.BUTTON.MERGE_SAMPLE',
                    icon: 'edit',
                    onClick: function () {
                        _this.mergeSample();
                    }
                }]
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.READ_SAMPLE_REPORT',
                icon: 'print',
                onClick: function () {
                    var selections = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
                    if (selections.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    Gikam.viewReport(selections[0].reportConfigLineId, selections[0]);
                },
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.ANNEX',
                icon: 'upload',
                onClick: function () {
                    _this.uploadSampleFile();
                }
            }]
        }
    },

    // 样品和测试
    getOrderTestGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-sample-edit-list-data-input-order-task-grid',
            service: 'emcOrderServiceImpl',
            columns: emcSample.getSampleTestColumns(),
            deleteFormatter: function (row) {
                return row.orderNo || row.id;
            },
            onRowActive: function (index, row) {
                _this.onSampleOrderActive(row);
            },
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData(['emc-sample-edit-list-data-input-container-info-grid']);
                }
            },
            rowStyleFormatter: function (row) {
                if (row.termination === '1') {
                    return 'background-color : #FFFF99';
                }
            },
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.ADD_ORDER',
                icon: 'add',
                hidden: _this.getHidden(),
                onClick: function () {
                    let activedRow = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getActivedRow();
                    let hasHanded = activedRow.handStatus === 'preLogged' && activedRow.ext$.analysiscount !== '0';
                    if (hasHanded) {
                        Gikam.alert('样品已交接，不允许再次添加样品');
                        return;
                    }

                    _this.addSampleTest();
                },
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.REMOVE',
                icon: 'remove',
                hidden: _this.getHidden(),
                onClick: function () {
                    var delOrder = function () {
                        var selections = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getSelections();
                        if (Gikam.isEmpty(selections)) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                            return;
                        }

                        let activedRow = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getActivedRow();
                        let hasHanded = activedRow.handStatus === 'preLogged' && activedRow.ext$.analysiscount !== '0';
                        if (hasHanded) {
                            Gikam.alert('样品已交接，不允许删除样品');
                            return;
                        }

                        var url = emcSample.baseUrl + '/orders';
                        workspace.window.showMask(true);
                        Gikam.del(url, Gikam.getJsonWrapper(null, ['', selections])).done(function () {
                            Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').refresh();
                            Gikam.cleanCompData(['emc-sample-edit-list-data-input-container-info-grid']);
                            Gikam.getComp('emc-sample-edit-list-grid').refresh();
                        }).always(function () {
                            workspace.window.closeMask();
                        });
                    }
                    Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.DEL_ORDER', "EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.CONFIRM_DELETE", delOrder, "");
                },
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.BATCH_INPUT',
                icon: 'edit',
                hidden: _this.getHidden(),
                onClick: function () {
                    _this.updateSampleTestBatch();
                },
            }, {
                type: 'dropDownMenu',
                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.TERMINATION',
                hidden: _this.getHidden(),
                items: [{
                //     type: 'button',
                //     text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.TERMINATION_FOLDER',
                //     icon: 'edit',
                //     onClick: function () {
                //         let activedRow = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getActivedRow();
                //         let hasHanded = activedRow.handStatus === 'preLogged' && activedRow.ext$.analysiscount !== '0';
                //         if (hasHanded) {
                //             Gikam.alert('样品已交接，不允许终止点位');
                //             return;
                //         }
                //
                //         _this.terminateFolder('1');
                //     }
                // }, {
                //     type: 'button',
                //     text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.TERMINATION_FOLDER_NO',
                //     icon: 'edit',
                //     onClick: function () {
                //         _this.terminateFolder('0');
                //     }
                // }, {
                    type: 'button',
                    text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.TERMINATION_ORDER',
                    icon: 'edit',
                    onClick: function () {
                        let activedRow = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getActivedRow();
                        let hasHanded = activedRow.handStatus === 'preLogged' && activedRow.ext$.analysiscount !== '0';
                        if (hasHanded) {
                            Gikam.alert('样品已交接，不允许终止样品');
                            return;
                        }
                        _this.terminateOrder('1');
                    }
                }, {
                    type: 'button',
                    text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.TERMINATION_ORDER_NO',
                    icon: 'edit',
                    onClick: function () {
                        _this.terminateOrder('0');
                    }
                }]
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.DATE_AUTO_FILL',
                icon: 'edit',
                onClick: function () {
                    _this.autoFillSampleDate();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.ORDER_DESC_FILL',
                icon: 'edit',
                hidden: true,
                onClick: function () {
                    _this.fillOrderDesc();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.ORDER_DESC_SOIL',
                icon: 'edit',
                hidden: true,
                onClick: function () {
                    _this.soilOrderDesc();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.INPUT_SOLID_WASTE_ORDER_DESC',
                icon: 'edit',
                hidden: true,
                onClick: function () {
                    _this.solidWasteOrderDesc();
                }
            }]
        }
    },

    fillOrderDesc: function () {
        var grid = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid');
        var rows = grid.getSelections();
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        Gikam.create('modal', {
            width: 600,
            height: 343,
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ORDER_DESC_FILL',
            url: emcSample.baseUrl + '/page/order-desc-detail',
            onAfterClose: function (data) {
                if (!Gikam.isEmpty(data)) {
                    var formData = grid.getActivedRow();
                    workspace.window.showMask(true);
                    var orderDesc = '';
                    if (!Gikam.isEmpty(data.orderColor)) {
                        orderDesc += data.orderColor + '、';
                    }
                    if (!Gikam.isEmpty(data.orderSmell)) {
                        orderDesc += data.orderSmell + '、';
                    }
                    if (!Gikam.isEmpty(data.orderOilSlick)) {
                        orderDesc += data.orderOilSlick + '、';
                    }
                    if (!Gikam.isEmpty(data.orderFloater)) {
                        orderDesc += data.orderFloater + '、';
                    }
                    if (!Gikam.isEmpty(data.samplingDepth)) {
                        orderDesc += '采样深度：' + data.samplingDepth + '、';
                    }
                    if (!Gikam.isEmpty(data.samplingNum)) {
                        orderDesc += '采样量：' + data.samplingNum + '、';
                    }
                    orderDesc = orderDesc.substring(0, orderDesc.length - 1);
                    rows.map(function (item) {
                        item.orderDesc = orderDesc;
                    });
                    Gikam.put(emcSample.baseUrl + '/' + formData.id, Gikam.getJsonWrapper({}, ['emcOrderServiceImpl', rows])).done(function () {
                        grid.refresh()
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    soilOrderDesc: function () {
        var grid = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid');
        var rows = grid.getSelections();
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        Gikam.create('modal', {
            width: 600,
            height: 400,
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ORDER_DESC_SOIL',
            url: emcSample.baseUrl + '/page/soli-order-desc-detail',
            onAfterClose: function (data) {
                if (!Gikam.isEmpty(data)) {
                    var formData = grid.getActivedRow();
                    workspace.window.showMask(true);
                    var orderDesc = '';
                    if (!Gikam.isEmpty(data.ext$.color)) {
                        orderDesc += data.ext$.color + '、';
                    }
                    if (!Gikam.isEmpty(data.ext$.texture)) {
                        orderDesc += data.ext$.texture + '、';
                    }
                    if (!Gikam.isEmpty(data.ext$.othermatter)) {
                        orderDesc += data.ext$.othermatter + '、';
                    }
                    if (!Gikam.isEmpty(data.ext$.rootcontent)) {
                        orderDesc += data.ext$.rootcontent + '、';
                    }
                    if (!Gikam.isEmpty(data.ext$.humidity)) {
                        orderDesc += data.ext$.humidity + '、';
                    }
                    if (!Gikam.isEmpty(data.ext$.ordersmell)) {
                        orderDesc += data.ext$.ordersmell + '、';
                    }
                    orderDesc = orderDesc.substring(0, orderDesc.length - 1);
                    rows.map(function (item) {
                        item.ext$.color = data.ext$.color;
                        item.ext$.texture = data.ext$.texture;
                        item.ext$.othermatter = data.ext$.othermatter;
                        item.ext$.rootcontent = data.ext$.rootcontent;
                        item.ext$.humidity = data.ext$.humidity;
                        item.orderDesc = orderDesc;
                        item.ext$.samplingdepth = data.ext$.samplingdepth;
                    });

                    Gikam.put(emcSample.baseUrl + '/' + formData.id, Gikam.getJsonWrapper({}, ['emcOrderServiceImpl', rows])).done(function () {
                        grid.refresh()
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    solidWasteOrderDesc: function () {
        var grid = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid');
        var rows = grid.getSelections();
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }

        var modal = Gikam.create('modal', {
            title: "EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.INPUT_SOLID_WASTE_ORDER_DESC",
            width: 400,
            height: 350,
        });
        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [{
                    type: 'btnToolbar',
                    items: [{
                        type: 'button',
                        text: 'GIKAM.BUTTON.CONFIRM',
                        class: 'blue',
                        onClick: function () {
                            if (form.validate()) {
                                let formData = form.options.data;
                                var orderDescArr = [];
                                if (!Gikam.isEmpty(formData.orderType)) {
                                    orderDescArr.push(formData.orderType);
                                }
                                if (!Gikam.isEmpty(formData.orderStatusText)) {
                                    orderDescArr.push(formData.orderStatusText);
                                }
                                if (!Gikam.isEmpty(formData.color)) {
                                    orderDescArr.push(formData.color);
                                }
                                if (!Gikam.isEmpty(formData.odor)) {
                                    orderDescArr.push(formData.odor);
                                }
                                let orderDesc = orderDescArr.join(';');
                                console.info(orderDesc)
                                rows.forEach(item => {
                                    item.ext$.samplingdepth = formData.samplingDepth;
                                    item.orderDesc = orderDesc;
                                })

                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(emcSample.baseUrl + '/' + rows[0].id, Gikam.getJsonWrapper({}, ['emcOrderServiceImpl', rows])).done(function () {
                                    grid.refresh()
                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close();
                                });
                            }
                        }
                    }, {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CANCEL',
                        onClick: function () {
                            modal.close();
                        }
                    }]
                }]
            }
        });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            columns: 1,
            titleWidth: 80,
            fields: [{
                field: 'samplingDepth',
                title: 'T_EMC_ORDER.SAMPLINGDEPTH',
                validators: ['strLength[0, 60]'],
            }, {
                field: 'orderType',
                title: 'T_EMC_ORDER.ORDERCATEGORY',
                validators: ['strLength[0, 60]'],
            }, {
                field: 'orderStatus',
                title: 'T_EMC_ORDER.ORDERSTATUS',
                validators: ['strLength[0, 60]'],
                type: 'comboBox',
                category: 'sys',
                param: {
                    codeCategoryId: 'orderStatus',
                },
                onChange: function (field, value, rowIndex, oldValue, text) {
                    console.info(field, value, rowIndex, oldValue, text)
                    this.setData({
                        orderStatus: value,
                        orderStatusText: text
                    });
                }
            }, {
                field: 'color',
                title: 'T_EMC_ORDER.COLOR',
                validators: ['strLength[0, 60]'],
            }, {
                field: 'odor',
                title: 'T_EMC_ORDER.ODOR',
                validators: ['strLength[0, 60]'],
            }],
            autoSave: false
        });
    },

    terminateFolder: function (termination) {
        var gridData = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getSelections();
        if (gridData.length > 0) {
            workspace.window.showMask(true);
            Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/action/terminate-folder', Gikam.getJsonWrapper({
                termination: termination
            }, ['emcOrderServiceImpl', gridData])).done(function () {
                Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').refresh();
            }).always(function () {
                workspace.window.closeMask();
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    terminateOrder: function (termination) {
        var gridData = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getSelections();
        if (gridData.length > 0) {
            workspace.window.showMask(true);
            Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/terminate-order', Gikam.getJsonWrapper({
                termination: termination
            }, ['emcOrderServiceImpl', gridData])).done(function () {
                Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').refresh();
            }).always(function () {
                workspace.window.closeMask();
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    updateItemGroup: function () {
        var selections = Gikam.getComp('emc-sample-edit-list-order-prepare-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var orderIds = "";
        selections.forEach(function (item) {
            orderIds += item.id + ',';
        });
        orderIds = orderIds.substring(0, orderIds.length - 1);
        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.TEST_EDIT',
            width: '70%',
            url: IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/page/order-item-group-sample-edit-list' + Gikam.param({
                monitorTmplId: selections[0].monitorTmplId,
                orderCategory: selections[0].orderCategory,
                orderIds: orderIds
            }),
            onAfterClose: function () {
                Gikam.getComp('emc-sample-edit-list-order-container-info-grid').refresh();
            }
        });
    },

    moveTask: function () {
        var orders = Gikam.getComp('emc-sample-edit-list-order-prepare-grid').getSelections();
        if (Gikam.isEmpty(orders)) {
            Gikam.alert('至少选择一个样');
            return;
        }
        var tasks = Gikam.getComp('emc-sample-edit-list-order-container-info-grid').getSelections();
        if (Gikam.isEmpty(tasks)) {
            Gikam.alert('至少选择一个测试');
            return;
        }

        Gikam.confirm('提醒', '是否确认将选中的因子移动至新样品中？', function () {
            workspace.window.showMask(true);
            var url = emcSample.baseUrl + '/order-tasks/move';
            Gikam.post(url, Gikam.getJsonWrapper({}, ['emcOrderServiceImpl', orders], ['emcOrderContainerServiceImpl', tasks])).done(function () {
                Gikam.getComp('emc-sample-edit-list-order-prepare-grid').refresh();
            }).always(function () {
                workspace.window.closeMask();
            });
        });
    },

    // 数据录入 容器信息
    getOrderContainerGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-sample-edit-list-data-input-container-info-grid',
            service: 'emcOrderTaskServiceImpl',
            columns: emcSample.getSamplePrepareOrderContainerColumns(),
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    //this.activeRowByIndex(0);
                }
            },
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.CONTAINER_UPDATE',
                icon: 'modify',
                onClick: function () {
                    _this.updateDataInputOrderTask();
                },
            }]
        }
    },

    loadSamplePropertyPage: function (id, tmplId, monitorTmplId) {
        // 查询 sample 的时候，如果存在样品，则需要更新 采样单的 monitorTmplId，并插入数据到 property data 表
        Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/tmpls/' + id + '/judge-samples/' + monitorTmplId).done(function () {
            Gikam.getComp('emc-sample-edit-list-project-sample-grid').refresh();
        });
        // 打开采样单属性的时候，看 模板 id 是否存在，没有就要提示进行添加；有的话要判断是否添加过
        if (Gikam.isEmpty(monitorTmplId)) {
            Gikam.alert("EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.UNSET_SAMPLE_PROPERTY");
            return;
        }

        var _this = this;
        Gikam.create('modal', {
            width: '60%',
            bodyStyle: {
                padding: '0px 20px'
            },
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.DETAIL_INFO',
            url: emcSample.baseUrl + '/page/info-detail' + Gikam.param({
                id: id,
                tmplId: tmplId,
                monitorTmplId: monitorTmplId,
            }),
        });
    },

    // 采样日期自动录入
    autoFillSampleDate: function () {
        var sampleRow = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getActivedRow();
        if (Gikam.isEmpty(sampleRow)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_SAMPLE_DATA');
            return;
        }
        var orderRows = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getData();
        if (Gikam.isEmpty(orderRows)) {
            return;
        }
        if ((Gikam.isEmpty(orderRows[0].sampleBeginDate) || Gikam.isEmpty(orderRows[0].sampleBeginTime))
            || (Gikam.isEmpty(orderRows[0].sampleCompleteDate) || Gikam.isEmpty(orderRows[0].sampleCompleteTime))) {
            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.ORDER_SAMPLE_DATETIME_ALL_INCOMPLETE');
            return;
        }
        var modal = Gikam.create('modal', {
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ORDER_SAMPLE_INTERVAL',
            width: 310,
            height: 150,
            showCloseBtn: true,
            onAfterClose: function (interval) {
                if (Gikam.isEmpty(interval) || orderRows.length <= 1) {
                    return;
                }
                var firstOrderRow = orderRows[0];
                var firstBeginDateTime = firstOrderRow.sampleBeginDate + ' ' + firstOrderRow.sampleBeginTime;
                var firstCompleteDateTime = firstOrderRow.sampleCompleteDate + ' ' + firstOrderRow.sampleCompleteTime;
                var orders = [];
                for (var index = 1; index < orderRows.length; index++) {
                    //计算时间
                    var beginDateTime = new Date(Gikam.DateUtils.toDateTime(firstBeginDateTime).valueOf() + parseInt(interval) * index * 60 * 1000);
                    var beginDateTimeStr = Gikam.DateUtils.formatter(beginDateTime, 'yyyy-MM-dd hh:mm');
                    var sampleBeginDateStr = beginDateTimeStr.substr(0, 10);
                    var sampleBeginTimeStr = beginDateTimeStr.substr(11);
                    var completeDateTime = new Date(Gikam.DateUtils.toDateTime(firstCompleteDateTime).valueOf() + parseInt(interval) * index * 60 * 1000);
                    var completeDateTimeStr = Gikam.DateUtils.formatter(completeDateTime, 'yyyy-MM-dd hh:mm');
                    var sampleCompleteDateStr = completeDateTimeStr.substr(0, 10);
                    var sampleCompleteTimeStr = completeDateTimeStr.substr(11);
                    //构造要更新的样品数据
                    var orderRow = orderRows[index];
                    orders.push({
                        id: orderRow.id,
                        sampleBeginDate: sampleBeginDateStr,
                        sampleBeginTime: sampleBeginTimeStr,
                        sampleCompleteDate: sampleCompleteDateStr,
                        sampleCompleteTime: sampleCompleteTimeStr
                    });
                }
                workspace.window.showMask(true);
                Gikam.put(emcSample.baseUrl + '/' + sampleRow.id + '/orders/sample-date-time', Gikam.getJsonWrapper({}, [null, orders])).done(function (data) {
                    Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').refresh();
                }).always(function () {
                    workspace.window.closeMask();
                });
            }
        });
        Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [{
                    type: 'form',
                    id: 'sample-interval-modal-form',
                    columns: 1,
                    titleWidth: 90,
                    fields: [{
                        field: 'interval',
                        title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.ORDER_SAMPLE_INTERVAL',
                        type: 'number'
                    }]
                }]
            },
            south: {
                items: [{
                    type: 'btnToolbar',
                    items: [{
                        type: 'button',
                        text: 'GIKAM.BUTTON.CONFIRM',
                        onClick: function () {
                            var form = Gikam.getComp('sample-interval-modal-form');
                            var data = form.getData();
                            if (Gikam.isEmpty(data.interval)) {
                                Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.ORDER_SAMPLE_INTERVAL_NULL');
                                return;
                            }
                            modal.close(data.interval);
                        }
                    }, {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CANCEL',
                        onClick: function () {
                            modal.close();
                        }
                    }]
                }]
            }
        });
    },

    loadDataInputPage: function (id) {
        var _this = this;
        Gikam.create('modal', {
            width: '90%',
            height: '90%',
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.DATA_INPUT',
            url: emcSample.baseUrl + '/page/data-input-detail' + Gikam.param({
                id: id,
            }),
        });
    },

    loadCalibrationPage: function (id) {
        console.log(id);
        if (!Gikam.isEmpty(id) && id != 'undefined') {
            Gikam.create('modal', {
                width: '80%',
                title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.INSTRUMENT_ADJUSTMENT',
                url: IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipt-calibrations/page/detail-list' + Gikam.param({
                    id: id,
                }),
            });
        } else {
            Gikam.alert('仪器校准ID为空！');
        }

    },

    uploadFolderPic: function () {
        var _this = this
        var rows = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
        if (rows && rows.length === 1) {
            Gikam.create('modal', {
                width: 800,
                height: 500,
                title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.FOLDER_PIC',
                url: emcSample.baseUrl + '/page/sample-folder-pic-list?id=' + rows[0].id
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    uploadSampleFile: function () {
        var _this = this
        var rows = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
        if (rows && rows.length === 1) {
            Gikam.create('modal', {
                width: 800,
                height: 500,
                title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ANNEX',
                url: emcSample.baseUrl + '/page/sample-annex-list?id=' + rows[0].id
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    createSample: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-folder-edit-list-layout',
            renderTo: workspace.window.$dom,
            center: {
                items: [{
                    type: 'tab',
                    fill: true,
                    style: {padding: '0'},
                    panels: [{
                        // 采样准备
                        title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.TAB.FOLDER_PREPARE.BASE_INFO',
                        items: [{
                            type: 'layout',
                            west: {
                                width: '30%',
                                items: [_this.getProjectGridParam()]
                            },
                            center: {
                                items: [{
                                    type: 'layout',
                                    north: {
                                        height: 260,
                                        items: [_this.getSamplePrepareGridParam()]
                                    },
                                    center: {
                                        items: [{
                                            type: 'layout',
                                            west: {
                                                width: '50%',
                                                // 样品
                                                items: [this.getOrderGridParam()]
                                            },
                                            center: {
                                                width: '50%',
                                                // 容器信息
                                                items: [this.getOrderTaskGridParam()]
                                            }
                                        }]
                                    }
                                }]

                            }
                        },]
                    }, {
                        // 数据录入
                        title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.TAB.DATA_INPUT.BASE_INFO',
                        items: [{
                            type: 'layout',
                            west: {
                                width: '30%',
                                // 数据录入
                                items: [_this.getDataInputGridParam()]
                            },
                            center: {
                                items: [{
                                    type: 'layout',
                                    north: {
                                        // 采样单
                                        height: 200,
                                        items: [this.getSamplingGridParam()]
                                    },
                                    center: {
                                        width: '100%',
                                        items: [{
                                            id: 'emc-sample-edit-list-order-container-layout-2',
                                            type: 'layout',
                                            center: {
                                                title: Gikam.propI18N('EMC.MODULE.BP.SAMPLE.SAMPLES.TAB.ORDERS_TEST.BASE_INFO'),
                                                width: '55%',
                                                padding: '0, 0, 0, 8px',
                                                // 样品测试
                                                items: [this.getOrderTestGridParam()]
                                            },
                                            east: {
                                                title: '测试信息',
                                                width: 500,
                                                collapse: true,
                                                items: [this.getOrderTaskTab()]
                                            }
                                        }]
                                    }
                                }]
                            },
                        }]
                    }]
                }]
            },
        });
    },

    // 现场采样-采样单
    getSamplingGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-sample-edit-list-project-sample-grid',
            service: 'emcSampleServiceImpl',
            columns: emcSample.getProjectSampleColumns(),
            deleteFormatter: function (row) {
                return row.sampleNo || row.id;
            },
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData(['emc-sample-edit-list-data-input-order-task-grid', 'emc-sample-edit-list-data-input-container-info-grid']);
                    Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').setOptions({
                        url: null
                    });
                    Gikam.getComp('emc-sample-edit-list-data-input-container-info-grid').setOptions({
                        url: null
                    });
                }
            },
            onRowActive: function (index, row) {
                _this.onSampleTestActive(row);
            },
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.SUBMIT',
                icon: 'submit',
                onClick: function () {
                    _this.orderSubmit();

                    /* var grid = Gikam.getComp('emc-sample-edit-list-project-sample-grid');
                     if(Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').validate()) {

                         Gikam.create('workflow').submit({
                             data : grid.getSelections(),
                             pageObject : emcSample
                         }).done(function() {
                             var projectGrid = Gikam.getComp('emc-sample-edit-list-project-grid');
                             projectGrid.refresh();
                             grid.refresh();
                             Gikam.cleanCompData([ 'emc-sample-edit-list-data-input-order-task-grid', 'emc-sample-edit-list-data-input-container-info-grid' ]);
                         });
                     }*/

                    /*
                                        grid.processRows({
                                            url : emcSample.baseUrl + '/result-input',
                                            confirm : {
                                                msg : '确认提交吗',
                                                title : '提示',
                                                onNoClick : function () {
                                                    workspace.window.closeMask();
                                                }
                                            },
                                            formatter : function(row) {
                                                return row.sampleNo;
                                            },
                                            method : 'post'
                                        }).done(function() {
                                            grid.refresh();
                                            Gikam.getComp('emc-sample-edit-list-project-grid').refresh();
                                            Gikam.getComp('emc-sample-edit-list-project-prepare-grid').refresh();
                                            Gikam.cleanCompData([ 'emc-sample-edit-list-data-input-order-task-grid', 'emc-sample-edit-list-data-input-container-info-grid' ]);
                                        }).always(function() {
                                            workspace.window.closeMask();
                                        });*/

                    /*Gikam.confirm('采样仪器校准提示', '请检查是否需要选择仪器校准记录', function() {
                        grid.processRows({
                            url : emcSample.baseUrl + '/result-input',
                            confirm : {
                                msg : '确认提交吗',
                                title : '提示',
                                onNoClick : function () {
                                    workspace.window.closeMask();
                                }
                            },
                            formatter : function(row) {
                                return row.sampleNo;
                            },
                            method : 'post'
                        }).done(function() {
                            grid.refresh();
                            Gikam.getComp('emc-sample-edit-list-project-grid').refresh();
                            Gikam.getComp('emc-sample-edit-list-project-prepare-grid').refresh();
                            Gikam.cleanCompData([ 'emc-sample-edit-list-data-input-order-task-grid', 'emc-sample-edit-list-data-input-container-info-grid' ]);
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }, function () {

                    }, '继续提交');*/
                }
            }, {
                //     type : 'button',
                //     text : '样品交接',
                //     icon : 'submit',
                //     onClick : function() {
                //         var selections = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
                //         if (Gikam.isEmpty(selections)) {
                //             Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                //             return;
                //         }
                //         var falg = true;
                //         selections.forEach(function(item) {
                //             if(item.handStatus === 'preLogged' && item.ext$.analysiscount !== '0') {
                //                 Gikam.alert('已经提交到样品接收，不可再次提交！');
                //                 falg = false;
                //                 return;
                //             }
                //             if(item.ext$.analysiscount === '0') {
                //                 Gikam.alert('不需要样品接收！');
                //                 falg = false;
                //                 return;
                //             }
                //         });
                //         var url = emcSample.baseUrl + '/action/handsample';
                //         if(falg){
                //             workspace.window.showMask(true);
                //             Gikam.postText(url, Gikam.getJsonWrapper(null, [ '', selections ])).done(function() {
                //                 Gikam.getComp('emc-sample-edit-list-project-sample-grid').refresh();
                //             }).always(function() {
                //                 workspace.window.closeMask();
                //             });
                //         }
                //     },
                // }, {
                //     type : 'button',
                //     text : '部分交接',
                //     icon : 'submit',
                //     onClick : function() {
                //         var selections = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
                //         if (selections.length !== 1) {
                //             Gikam.alert('GIKAM.TIP.CHOOSE_ONE_DATA');
                //             return;
                //         }
                //
                //         let targetSample = selections[0];
                //         if (targetSample.handStatus === 'preLogged' && targetSample.ext$.analysiscount !== '0') {
                //             Gikam.alert('已经提交到样品接收，不可再次提交！');
                //             return;
                //         }
                //
                //         if (targetSample.ext$.analysiscount === '0') {
                //             Gikam.alert('不需要样品接收！');
                //             return;
                //         }
                //
                //         Gikam.create('modal', {
                //             width : '80%',
                //             title : '选择样品',
                //             url : emcSample.baseUrl + '/page/order-container-choose-list'+ Gikam.param({
                //                 sampleId: selections[0].id,
                //                 targetType : 'partHandover'
                //             }),
                //             onAfterClose : function (data) {
                //                 if (!Gikam.isEmpty(data)) {
                //                     workspace.window.showMask(true);
                //                     Gikam.postText(emcSample.baseUrl + '/action/part-handover', Gikam.getJsonWrapper(null, [ 'emcOrderContainerServiceImpl', data ], ['emcSampleServiceImpl', selections])).done(function() {
                //                         Gikam.getComp('emc-sample-edit-list-project-sample-grid').refresh();
                //                     }).always(function() {
                //                         workspace.window.closeMask();
                //                     });
                //                 }
                //             }
                //         });
                //     },
                // }, {
                type: 'dropDownMenu',
                text: 'GIKAM.BUTTON.SAMPLE',
                hidden: _this.getHidden(),
                items: [{
                    type: 'button',
                    text: 'GIKAM.BUTTON.ADD_SAMPLE',
                    icon: 'add',
                    onClick: function () {
                        var activeRows = Gikam.getComp('emc-sample-edit-list-project-grid').getActivedRow();
                        Gikam.getComp('emc-sample-edit-list-project-sample-grid').insert(emcSample.baseUrl, [{
                            'projId': activeRows.id,
                        }]);
                    },
                }, {
                    type: 'button',
                    text: 'GIKAM.BUTTON.DEL_SAMPLE',
                    icon: 'remove-row',
                    onClick: function () {
                        let selections = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
                        let hasHanded = selections.some(item => item.handStatus === 'preLogged' && item.ext$.analysiscount !== '0');
                        if (hasHanded) {
                            Gikam.alert('样品已交接，不允许删除采样单');
                            return;
                        }

                        var url = Gikam.printf(emcSample.baseUrl);
                        Gikam.getComp('emc-sample-edit-list-project-sample-grid').deleteRows(url).done(function () {
                            Gikam.getComp('emc-sample-edit-list-grid').refresh();
                        });
                    }
                }, {
                    type: 'button',
                    text: 'GIKAM.BUTTON.MERGE_SAMPLE',
                    icon: 'edit',
                    hidden: true,
                    onClick: function () {
                        _this.mergeSample();
                    }
                }]
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.ANNEX',
                icon: 'upload',
                onClick: function () {
                    _this.uploadSampleFile();
                }
            }, {
                type: 'dropDownMenu',
                text: 'GIKAM.BUTTON.PREVIEW',
                hidden: _this.getHidden(),
                items: [{
                    type: 'button',
                    text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.VIEW_SAMPLING_REPORT',
                    icon: 'print',
                    onClick: function () {
                        var selections = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
                        if (selections.length !== 1) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                            return;
                        }
                        Gikam.viewReport(selections[0].reportConfigLineId, selections[0]);
                    }
                }, {
                    type: 'button',
                    text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.VIEW_CALIBRATION_REPORT',
                    icon: 'print',
                    onClick: function () {
                        var selections = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
                        if (selections.length !== 1) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                            return;
                        }
                        Gikam.viewReport(selections[0].caliReportLineId, selections[0]);
                    }
                }]
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.INSTRUMENT_ADJUSTMENT',
                icon: 'material-to-check',
                onClick: function () {
                    var selections = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
                    if (selections.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    Gikam.create('modal', {
                        width: '80%',
                        title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.INSTRUMENT_ADJUSTMENT',
                        url: IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipt-calibrations/page/edit-list' + Gikam.param({
                            id: selections[0].id
                        }),
                        onAfterClose: function (data) {
                            if (!Gikam.isEmpty(data)) {
                                var calibrationIds = data.map(function (item) {
                                    return item.id;
                                }).join();

                                var sample = {
                                    id: selections[0].id,
                                    calibrationId: calibrationIds
                                };
                                Gikam.put(emcSample.baseUrl + '/instant', Gikam.getJsonWrapper({}, ['emcSampleServiceImpl', [sample]]));
                            }
                        }
                    });
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SAMPLING_EQUIPMENT',
                icon: 'modify',
                hidden: false,
                onClick: function () {
                    var selections = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }

                    var ids = selections.map(row => row.id).join();

                    Gikam.create('modal', {
                        width: '60%',
                        title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SAMPLING_EQUIPMENT',
                        url: IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/page/sampling-equipt' + Gikam.param({
                            ids: ids
                        })
                    });
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.BATCH_INPUT_DETAIL',
                icon: 'modify',
                onClick: function () {
                    _this.batchInputSampleDetail();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.BATCH_INPUT_DATE',
                icon: 'modify',
                onClick: function () {
                    _this.batchInputSampleDate();
                }
            }, {
                type: 'button',
                text: '气参数录入',
                icon: 'modify',
                onClick: function () {
                    _this.batchInputGasParameter();
                }
            }]
        }
    },

    /** 加载采样信息页面 */
    loadWorkConditionPage: function () {
        let selections = Gikam.getComp('emc-sample-edit-list-project-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title: '现场工况调查',
            width: '90%',
            height: '90%',
            url: '/secure/emc/module/bp/sample/conditions/page/edit-list' + Gikam.param({
                projId_SEQ: selections[0].id
            }),
        });
    },

    /** 加载采样信息页面 */
    loadSampleExpandPage: function (id, propertyTmplId, monitorTmplId, sampleFlag, projId, orderCategory, sampleUserId, sampleDate, tmplId, readFlag, elnTmplId) {
        var _param = {
            sampleId: id,
            propertyTmplId: propertyTmplId,
            monitorTmplId: monitorTmplId,
            sampleFlag: sampleFlag,
            projId: projId,
            orderCategory: orderCategory,
            sampleUserId: sampleUserId,
            sampleDate: sampleDate,
            tmplId: tmplId,
            readonly: readFlag,
            elnTmplId: elnTmplId,
        };
        debugger;
        Gikam.create('modal', {
            title: '采样信息',
            width: '90%',
            height: '90%',
            url: emcSample.baseUrl + '/page/sample-expand-detail' + Gikam.param(_param),
        });
    },

    /** 批录入采样时间 */
    batchInputSampleDate: function () {
        var selections = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var sampleIds = selections.map(row => row.id).join();
        Gikam.create('modal', {
            width: '80%',
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.BATCH_INPUT_DATE',
            url: IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/page/sample-date' + Gikam.param({
                sampleId_IN: sampleIds
            })
        });
    },

    /** 批录入气参数 */
    batchInputGasParameter: function () {
        var selections = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            width: '80%',
            title: '录入气采样参数信息',
            url: IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/page/gas-parameter-input' + Gikam.param({
                sampleId: selections[0].id,
                projId: selections[0].projId
            })
        });
    },

    /** 批录入采样单详情 */
    batchInputSampleDetail: function () {
        var selections = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var orderCategoryEmpty = selections.some(row => Gikam.isEmpty(row.orderCategory));
        if (orderCategoryEmpty) {
            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.ORDER_CATEGORY_EMPTY');
            return;
        }

        var orderCategories = Array.from(new Set(selections.map(row => row.orderCategory)));
        if (orderCategories.length > 1) {
            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.ORDER_CATEGORY_DIFFERENT');
            return;
        }

        var sampleIds = selections.map(row => row.id).join();
        Gikam.create('modal', {
            width: '80%',
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.BATCH_INPUT_DETAIL',
            url: IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/page/sample-detail' + Gikam.param({
                id_IN: sampleIds
            })
        });
    },

    createResultInput: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-folder-edit-list-layout',
            renderTo: workspace.window.$dom,
            center: {
                items: [{
                    type: 'tab',
                    fill: true,
                    panels: [{
                        title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.TAB.DATA_INPUT.BASE_INFO',
                        items: [{
                            type: 'layout',
                            west: {
                                width: 230,
                                // 结果录入-数据录入
                                items: [_this.getResultInputDataInputGridParam()]
                            },
                            center: {
                                items: [{
                                    type: 'layout',
                                    north: {
                                        // 按采样单
                                        height: 200,
                                        items: [this.getProjectSampleGridParam()]
                                    },
                                    center: {
                                        width: '50%',
                                        items: [{
                                            id: 'emc-sample-edit-list-order-container-layout',
                                            type: 'layout',
                                            center: {
                                                title: Gikam.propI18N('EMC.MODULE.BP.SAMPLE.SAMPLES.TAB.ORDERS_TEST.BASE_INFO'),
                                                width: '50%',
                                                // 样品测试
                                                items: [this.getOrderTestGridParam()]
                                            },
                                            east: {
                                                width: '50%',
                                                title: Gikam.propI18N('EMC.MODULE.BP.SAMPLE.SAMPLES.TAB.TEST_INFO'),
                                                collapse: true,
                                                // 容器信息
                                                items: [this.getOrderContainerGridParam()]
                                            }
                                        }]
                                    }
                                }]
                            },
                        }]
                    }]
                }]
            },
        });
    },

    deleteOrder: function () {
        var selections = Gikam.getComp('emc-sample-edit-list-order-prepare-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var url = '/secure/emc/module/bp/bp/orders';
        Gikam.del(url, Gikam.getJsonWrapper(null, ['', selections])).done(function () {
            Gikam.getComp('emc-sample-edit-list-order-prepare-grid').refresh();
            Gikam.getComp('emc-sample-edit-list-order-container-info-grid').refresh();
            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.DEL_SUCCESS');
        });
    },

    deleteNotQcOrder: function () {
        var selections = Gikam.getComp('emc-sample-edit-list-order-prepare-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var hasNonQCOrder = false
        selections.forEach(function (row) {
            if (!row.qcCategoryId && !row.qcCategoryCode && !row.qcName) {
                hasNonQCOrder = true
            }
        })
        if (hasNonQCOrder) {
            Gikam.alert('EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.DELETEORDERTIP');
            return;
        }
        Gikam.getComp('emc-sample-edit-list-order-prepare-grid').deleteRows(IFM_CONTEXT + '/secure/emc/module/bp/order/order-receipts/orders').done(function () {
            Gikam.getComp('emc-sample-edit-list-grid').refresh();
            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.DEL_SUCCESS');
        });
    },

    updateOrder: function () {
        var selections = Gikam.getComp('emc-sample-edit-list-order-prepare-grid').getSelections();
        if (selections.length > 0) {
            var activedRow = Gikam.getComp('emc-sample-edit-list-grid').getActivedRow();
            Gikam.create('modal', {
                width: 600,
                height: 313,
                title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.BATCH_INPUT',
                url: emcSample.baseUrl + '/page/order-batch',
                onAfterClose: function (data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        selections.map(function (item) {
                            item.sampleBeginDate = data.sampleBeginDate;
                            item.sampleBeginTime = data.sampleBeginTime;
                            item.sampleCompleteDate = data.sampleCompleteDate;
                            item.sampleCompleteTime = data.sampleCompleteTime;
                            item.orderDesc = data.orderDesc;
                        });
                        Gikam.put(emcSample.baseUrl + '/' + activedRow.id, Gikam.getJsonWrapper({}, ['emcOrderServiceImpl', selections])).done(function () {
                            Gikam.getComp('emc-sample-edit-list-order-prepare-grid').refresh();
                        }).always(function () {
                            workspace.window.closeMask();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
        }
    },

    // 复制为QC样
    copyQcOrder2: function () {
        var _this = this;
        var selections = Gikam.getComp('emc-sample-edit-list-order-prepare-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        if (selections.length > 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        if (selections[0].qcCategoryId) {
            Gikam.alert('EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.COPYQCORDERTIP');
            return;
        }
        Gikam.create('modal', {
            width: 500,
            title: Gikam.choose.getConfig('orderReceiptQcCategory').title,
            url: Gikam.choose.getConfig('orderReceiptQcCategory').url + Gikam.param({
                category: 'scene',
                addNum: true,
                single: true
            }),
            onAfterClose: function (rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var totalSelections = [];
                rows.forEach(function (row) {
                    var newItem = JSON.parse(JSON.stringify(selections[0]));
                    newItem.projId = selections[0].projId;
                    newItem.qcCategoryId = row.id;
                    newItem.qcCategoryCode = row.qcCategoryId;
                    newItem.qcAddFolder = row.addFolder;
                    newItem.qcName = row.qcName;
                    newItem.ext$.parOrder = row.parOrder;
                    newItem.ext$.updateParNo = row.updateParNo;
                    newItem.ext$.addnum = row.addNum;
                    totalSelections.push(newItem);
                })
                workspace.window.showMask(true);
                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/order/order-receipts/qc-orders/action/copy', Gikam.getJsonWrapper({}, ['', totalSelections])).done(function (data) {
                    Gikam.getComp('emc-sample-edit-list-grid').refresh();
                }).always(function () {
                    workspace.window.closeMask();
                });
            }
        });
    },

    copyQcOrder: function () {
        var _this = this
        var selections = Gikam.getComp('emc-sample-edit-list-order-prepare-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        if (selections.length > 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        if (selections[0].qcCategoryId) {
            Gikam.alert('EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.COPYQCORDERTIP');
            return;
        }

        Gikam.create('modal', {
            width: 500,
            title: Gikam.choose.getConfig('orderReceiptQcCategory').title,
            url: Gikam.choose.getConfig('orderReceiptQcCategory').url + Gikam.param({
                category: 'scene',
                addNum: true
            }),
            onAfterClose: function (rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                var existedAddFolderQc = rows.some(function (qc) {
                    return qc.addFolder === '1';
                });
                workspace.window.showMask(true);
                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/order/order-receipts/qc-orders/action/copy', Gikam.getJsonWrapper({}, ['', selections], ['', rows])).done(function (data) {
                    if (existedAddFolderQc) {
                        Gikam.getComp('emc-sample-edit-list-grid').refresh();
                    } else {
                        Gikam.getComp('emc-sample-edit-list-order-prepare-grid').refresh();
                    }
                }).always(function () {
                    workspace.window.closeMask();
                });
            }
        });
    },

    // 删除QC样
    deleteQcOrder: function () {
        var _this = this
        var selections = Gikam.getComp('emc-sample-edit-list-order-prepare-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var hasNonQCOrder = false
        selections.forEach(function (row) {
            if (!row.qcCategoryId || !row.qcCategoryCode || !row.qcName) {
                hasNonQCOrder = true
            }
        })
        if (hasNonQCOrder) {
            Gikam.alert('EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.DELETEQCORDERTIP');
            return;
        }
        Gikam.getComp('emc-sample-edit-list-order-prepare-grid').deleteRows(IFM_CONTEXT + '/secure/emc/module/bp/order/order-receipts/qc-orders').done(function () {
            Gikam.getComp('emc-sample-edit-list-grid').refresh();
        });
    },

    // 删除复制点位
    deleteFolder: function () {
        var _this = this
        var selections = Gikam.getComp('emc-sample-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var flag = false
        selections.forEach(function (row) {
            if (!row.source || row.source != 'COPY') {
                flag = true
            }
        })
        if (flag) {
            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.COPYSOURCE');
            return;
        }

        Gikam.getComp('emc-sample-edit-list-grid').deleteRows(IFM_CONTEXT + '/secure/emc/module/bp/bp/folders').done(function () {
            Gikam.getComp('emc-sample-edit-list-grid').refresh();
        });
    },

    /** 同步分析项 */
    syncTestItem: function () {
        let selections = Gikam.getComp('emc-sample-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.confirm('GIKAM.TIP.CONFIRM', 'EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.CONFIRM_SYNC_TEST_ITEM', function () {
            workspace.window.showMask(true);
            Gikam.postText(emcSample.baseUrl + '/action/sync-test-item', Gikam.getJsonWrapper(null, ['', selections])).done(function (r) {

            }).always(function () {
                workspace.window.closeMask();
            });
        })
    },

    mergeSample: function () {
        var selections = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
        if (selections.length == 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            return;
        }
        var orderCategory = selections.map(function (e) {
            return e.orderCategory;
        });
        orderCategory = this.unique(orderCategory);

        if (orderCategory.length > 1) {
            Gikam.alert('EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.TOO_MANY_ORDERCATEGORY');
            return;
        }

        var url = emcSample.baseUrl + '/action/merge-samples';
        workspace.window.showMask(true);
        Gikam.postText(url, Gikam.getJsonWrapper(null, ['', selections])).done(function () {
            Gikam.getComp('emc-sample-edit-list-order-prepare-grid').refresh();
            Gikam.getComp('emc-sample-edit-list-project-sample-grid').refresh();
            Gikam.getComp('emc-sample-edit-list-order-container-info-grid').refresh();
        }).always(function () {
            workspace.window.closeMask();
        });
    },

    updateSampleTestBatch: function () {
        var selections = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getSelections();
        if (selections.length > 0) {
            var formData = Gikam.getComp('emc-sample-edit-list-project-grid').getActivedRow();
            Gikam.create('modal', {
                width: 600,
                height: 313,
                title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.BATCH_INPUT',
                url: emcSample.baseUrl + '/page/sample-test-batch',
                onAfterClose: function (data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        selections.map(function (item) {
                            item.sampleBeginDate = data.sampleBeginDate;
                            item.sampleBeginTime = data.sampleBeginTime;
                            item.sampleCompleteDate = data.sampleCompleteDate;
                            item.sampleCompleteTime = data.sampleCompleteTime;
                        });
                        Gikam.put(emcSample.baseUrl + '/' + formData.id, Gikam.getJsonWrapper({}, ['emcOrderServiceImpl', selections])).done(function () {
                            Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').refresh()
                        }).always(function () {
                            workspace.window.closeMask();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
        }
    },

    // 采样准备 容器信息修改
    updateOrderTask: function () {
        var selections = Gikam.getComp('emc-sample-edit-list-order-container-info-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_CONTAINER_DATA');
            return;
        }

        var orderSelections = Gikam.getComp('emc-sample-edit-list-order-prepare-grid').getSelections();
        if (orderSelections.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ORDER_DATA');
            return;
        }
        var originSelections = selections.map(function (item) {
            return Object.assign({}, item)
        });
        var avtivedRow = Gikam.getComp('emc-sample-edit-list-order-prepare-grid').getActivedRow(); // 样品行
        Gikam.create('modal', {
            width: 800,
            height: 600,
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.TAB.CONTAINER_INFO',
            url: emcSample.baseUrl + '/page/order-prepare-container' + Gikam.param({
                orderContainerId: selections[0].id
            }),
            onAfterClose: function (data) {
                Gikam.getLastModal().close();
            }
        });
    },

    /** 同步容器信息 */
    syncContainerInfo: function () {
        var selections = Gikam.getComp('emc-sample-edit-list-order-container-info-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.confirm('GIKAM.TIP.CONFIRM', 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.TIP.SYNC_OVERRIDE_CONTAINER_INFO', function () {
            workspace.window.showMask(true);
            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/sync-container-info', Gikam.getJsonWrapper({}, ['', selections])).done(function () {
                Gikam.getComp('emc-sample-edit-list-order-container-info-grid').refresh();
            }).always(function () {
                workspace.window.closeMask();
            });
        })
    },

    // 数据录入 容器信息修改
    updateDataInputOrderTask: function () {
        var orderTaskselections = Gikam.getComp('emc-sample-edit-list-data-input-container-info-grid').getSelections();
        if (orderTaskselections.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_CONTAINER_DATA');
            return;
        }
        var orderSelections = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getSelections();
        if (orderSelections.length == 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ORDER_DATA');
            return;
        }

        Gikam.create('modal', {
            width: 800,
            height: 600,
            id: 'emc-sample-edit-order-task-detail-modal',
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.TAB.CONTAINER_INFO',
            url: emcSample.baseUrl + '/page/order-container',
            onAfterClose: function (data) {
                Gikam.getLastModal().close();
            }
        });
    },

    // （样品和测试）添加样品
    addSampleTest: function () {
        var _this = emcSample.editPage;
        var activedRow = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getActivedRow();
        if (Gikam.isEmpty(activedRow)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_SAMPLE_DATA');
            return;
        }

        if (activedRow.orderCategory == undefined) { // 当前采样单无样品类型，过滤条件无需增加类型判断
            Gikam.create('modal', {
                title: Gikam.choose.getConfig('order').title,
                url: Gikam.choose.getConfig('order').url + Gikam.param({
                    targetOrder_unassigned: '1',
                    targetOrderTable: 'T_EMC_ORDER',
                    targetOrderColumn: 'ID',
                    targetOrderIdFilterColumn: 'SAMPLEID',
                    targetOrderIdFilterValue: activedRow.id,
                    projId_SEQ: activedRow.projId,
                    folderstatus_SEQ: 'done',
                    targetOrderSample_unassigned: '1',
                    sampleUserId: workspace.user.id,
                    columnsType: '2',
                }),
                onAfterClose: function (rows, index) {
                    if (Gikam.isEmpty(rows)) {
                        return;
                    }
                    // 判断选择的类型是否相同
                    var orders = rows[0].orderCategory;
                    var flag = rows.some(function (item) {
                        return item.orderCategory != orders;
                    });
                    if (flag) {
                        Gikam.alert('EMC.MODULE.BP.BP.FOLDERS.TIP.CHOOSE_ONE_ORDER_TYPE');
                        return;
                    }
                    workspace.window.showMask(true);
                    Gikam.postText(emcSample.baseUrl + '/' + activedRow.id + '/orders', Gikam.getJsonWrapper({}, [null, rows])).done(function (data) {
                        Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').refresh();
                        Gikam.getComp('emc-sample-edit-list-project-sample-grid').refresh();
                        Gikam.getComp('emc-sample-edit-list-grid').refresh();
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            });
            return;
        }
        Gikam.create('modal', {
            title: Gikam.choose.getConfig('order').title,
            url: Gikam.choose.getConfig('order').url + Gikam.param({

                target_unassigned: '1',
                targetSampleTable: 'T_EMC_SAMPLE',
                targetSampleIdColumn: 'ORDERCATEGORY',
                targetSampleCategoryFilterColumn: 'ORDERCATEGORY',
                targetSampleCategoryFilterValue: activedRow.orderCategory,
                folderstatus_SEQ: 'done',
                targetOrder_unassigned: '1',
                targetOrderTable: 'T_EMC_ORDER',
                targetOrderColumn: 'ID',
                targetOrderIdFilterColumn: 'SAMPLEID',
                targetOrderIdFilterValue: activedRow.id,
                projId_SEQ: activedRow.projId,
                targetOrderSample_unassigned: '1',
                columnsType: '2',
            }),
            onAfterClose: function (rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var orders = rows[0].orderCategory;
                var flag = rows.some(function (item) {
                    return item.orderCategory != orders;
                });
                if (flag) {
                    return;
                }
                workspace.window.showMask(true);
                Gikam.postText(emcSample.baseUrl + '/' + activedRow.id + '/orders', Gikam.getJsonWrapper({}, [null, rows])).done(function (data) {
                    Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').refresh();
                    Gikam.getComp('emc-sample-edit-list-project-sample-grid').refresh();
                    Gikam.getComp('emc-sample-edit-list-grid').refresh();
                }).always(function () {
                    workspace.window.closeMask();
                });
            }
        });
    },

    // 去重方法
    unique: function (arr) {
        var res = [];
        var json = {};
        for (var i = 0; i < arr.length; i++) {
            if (!json[arr[i]]) {
                res.push(arr[i]);
                json[arr[i]] = 1;
            }
        }
        return res;
    },

    getHidden: function () {
        if (this.param.sampleStatus == 'reportundo') {
            return true;
        } else {
            return false;
        }
    },

    // 工程详情页面
    loadProjDetailPage: function (projNo) {
        Gikam.create('modal', {
            id: 'projModal',
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.PROJ_DETAIL',
            width: 980,
            height: 500,
            showCloseBtn: true,
            url: IFM_CONTEXT + '/secure/emc/module/bp/project/projects/page/project-search-detail' + Gikam.param({
                projNo: projNo
            }),
        });
    },

    /** 配置比对信息 */
    configComparison: function () {
        var selections = Gikam.getComp('emc-sample-edit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        if (Gikam.isEmpty(selections[0].monitorPointId)) {
            Gikam.alert('EMC.MODULE.BP.BP.FOLDERS.TIP.MONITOR_UNMAINTAINED');
            return;
        }

        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.BUTTON.CONFIG_COMPARISON',
            width: '80%',
            url: IFM_CONTEXT + "/secure/emc/module/mdm/bizmdm/comparisons/page/edit-list" + Gikam.param({
                monitorPointId_SEQ: selections[0].monitorPointId,
            })
        });
    },

    orderSubmit: function () {
        var grid = Gikam.getComp('emc-sample-edit-list-project-sample-grid');
        var url = Gikam.printf(emcSample.baseUrl + '/action/submit-reminds');
        var that = this;
        var selections = grid.getSelections();
        //至少选择
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var subFlag = true;
        selections.forEach(function (item) {
            if (!item.ext$.sampleusername) {
                Gikam.alert('请先进行采样人填写后进行提交！');
                subFlag = false;
                return;
            }
        });
        if (subFlag) {
            Gikam.postText(url, Gikam.getJsonWrapper({}, ['', selections])).done(function (reminds) {
                if (Gikam.isEmpty(reminds)) {
                    if (grid.validate(selections)) {
                        Gikam.create('workflow').submit({
                            data: grid.getSelections(),
                            pageObject: emcSample
                        }).done(function () {
                            //grid.refresh();
                            Gikam.getComp('emc-sample-edit-list-project-sample-grid').refresh();
                        });
                    }
                } else {
                    Gikam.confirm('EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.CONTINUESUBMITTING', reminds, function () {
                        if (grid.validate(selections)) {
                            Gikam.create('workflow').submit({
                                data: grid.getSelections(),
                                pageObject: emcSample
                            }).done(function () {
                                //grid.refresh();
                                Gikam.getComp('emc-sample-edit-list-project-sample-grid').refresh();
                            });
                        }
                    })

                }
            })
        }
    },

    init: function (param) {
        this.param = param;
        if (param.category !== undefined) {
            param.sampleStatus = 'reportundo';
            this.createResultInput();
        } else {
            this.createSample();
        }
    }
};
