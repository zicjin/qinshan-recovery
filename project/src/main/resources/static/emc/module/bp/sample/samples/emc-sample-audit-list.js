emcSample.auditPage = {


    getBaseInfoTab: function () {
        var _this = this;
        var requestData = _this.param;
        requestData.orgId_SEQ = workspace.user.ext$.parentorgid;
        requestData.SCENEUSERNAMEFILTER = '1'
        return {
            type: 'tab',
            id: 'emc-sample-audit-list-base-info-tab',
            fill: true,
            panels: [{
                title: '待审核',
                id: 'emc-sample-audit-list-base-info-panel1',
                items: [{
                    type: 'layout',
                    north: {
                        height: '100%',
                        items: [{
                            type: 'grid',
                            id: 'emc-project-sample-list-grid',
                            page: true,
                            pageSize: 100,
                            url: emcSample.baseUrl + '/queries/tasks',
                            columns: emcSample.getProjectAuditSampleColumns(),
                            requestData: requestData,
                            service: 'emcSampleServiceImpl',
                            order: {
                                urgent: 'desc'
                            },
                            // rowStyleFormatter: function (row) {
                            //     if (row.ext$.urgent === '1') {
                            //         return {
                            //             backgroundColor: Gikam.emcUrgentColor
                            //         }
                            //     }
                            // },
                            onRendered: function () {
                                var formData = Gikam.getComp('emc-order-task-result-edit-list-button-group-by-test');
                                var status = _this.param.bpmn_statusCode;
                                if (formData !== undefined) {
                                    formData.clickHandle();
                                }
                                ;
                                if (status === 'audit') {
                                    this.hideColumns(['ext$.auditdate']);
                                    Gikam.getComp('ypjj-btn').hide();
                                    Gikam.getComp('bfjj-btn').hide();
                                } else {
                                    Gikam.getComp('ypjj-btn').hide();
                                    Gikam.getComp('bfjj-btn').hide();
                                    this.hideColumns(['ext$.reviewdate', 'ext$.sceneusername']);
                                }
                            },
                            onCellClick: function (field, row) {
                                if (field === 'sampleNo') {
                                    _this.loadSampleExpandRowPage(row);
                                }
                            },
                            toolbar: [{
                                type: 'form',
                                autoSave: false,
                                fields: emcSample.getShowAllField(),
                                onRendered: function () {
                                    var status = _this.param.bpmn_statusCode;
                                    if (status === 'mutual') {
                                        this.showFields(['showFlag']);
                                    } else {
                                        this.hideFields(['showFlag']);
                                    }
                                },
                            }, {
                                type: 'button',
                                text: 'GIKAM.WORKFLOW.BUTTON.PASS',
                                icon: 'pass',
                                onClick: function () {
                                    _this.pass();
                                }
                            }, {
                                type: 'button',
                                text: 'GIKAM.WORKFLOW.BUTTON.REJECT',
                                icon: 'reject',
                                onClick: function () {
                                    _this.reject();
                                }
                            }, {
                                type: 'button',
                                text: 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_SAMPLE',
                                icon: 'browse',
                                onClick: function () {
                                    _this.openBrowseRecord();
                                }
                                //            }, {
                                //                type: 'button',
                                //                text: 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_SAMPLE',
                                //                icon: 'print',
                                //                onClick: function () {
                                //                    var selections = Gikam.getComp('emc-project-sample-list-grid').getSelections();
                                //                    if (selections.length !== 1) {
                                //                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                //                        return;
                                //                    }
                                //                    Gikam.viewReport(selections[0].reportConfigLineId, selections[0]);
                                //                }
                            }, {
                                type: 'button',
                                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.VIEW_CALIBRATION_REPORT',
                                icon: 'print',
                                onClick: function () {
                                    var selections = Gikam.getComp('emc-project-sample-list-grid').getSelections();
                                    if (selections.length !== 1) {
                                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                        return;
                                    }
                                    Gikam.viewReport(selections[0].caliReportLineId, selections[0]);
                                }
                            },
//                                      {
//                                          type: 'button',
//                                          text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SAMPLING_EQUIPMENT',
//                                          icon: 'modify',
//                                          hidden: false,
//                                          onClick: function () {
//                                              var selections = Gikam.getComp('emc-project-sample-list-grid').getSelections();
//                                              if (Gikam.isEmpty(selections)) {
//                                                  Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
//                                                  return;
//                                              }
//
//                                              var ids = selections.map(row => row.id).join();
//
//                                              Gikam.create('modal', {
//                                                  width: '60%',
//                                                  title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SAMPLING_EQUIPMENT',
//                                                  url: IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/page/sampling-equipt' + Gikam.param({
//                                                      ids: ids,
//                                                      readonly: true
//                                                  })
//                                              });
//                                          }
//                                      },
                                {
                                    type: 'button',
                                    text: 'GIKAM.BUTTON.DELIVERY.PREVIEW',
                                    icon: 'print',
                                    onClick: function () {
                                        var selections = Gikam.getComp('emc-project-sample-list-grid').getSelections();
                                        if (selections.length !== 1) {
                                            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                            return;
                                        }
                                        Gikam.viewReport('samplingNumber', selections);

                                        // Gikam.viewReport(selections[0].receiveReportLineId, selections[0]);
                                    }
                                    //            }, {
                                    //                type: 'button',
                                    //                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.ANNEX',
                                    //                icon: 'upload',
                                    //                onClick: function () {
                                    //                    _this.uploadSampleFile();
                                    //                }
                                }, {
                                    id: 'ypjj-btn',
                                    type: 'button',
                                    text: '样品交接',
                                    icon: 'submit',
                                    onClick: function () {
                                        var selections = Gikam.getComp('emc-project-sample-list-grid').getSelections();
                                        if (Gikam.isEmpty(selections)) {
                                            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                                            return;
                                        }
                                        var falg = true;
                                        selections.forEach(function (item) {
                                            if (item.handStatus === 'preLogged' && item.ext$.analysiscount !== '0') {
                                                Gikam.alert('已经提交到样品接收，不可再次提交！');
                                                falg = false;
                                                return;
                                            }
                                            if (item.ext$.analysiscount === '0') {
                                                Gikam.alert('不需要样品接收！');
                                                falg = false;
                                                return;
                                            }
                                        });
                                        var url = emcSample.baseUrl + '/action/handsample';
                                        if (falg) {

                                            //填写样品接收时间
                                            var modal = Gikam.create('modal', {
                                                title: "GIKAM.BUTTON.BATCH_INPUT",
                                                width: 400,
                                                height: 270,
                                            });
                                            var layout = Gikam.create('layout', {
                                                renderTo: modal.window.$dom,
                                                center: {
                                                    items: [{
                                                        type: 'btnToolbar',
                                                        items: [{
                                                            type: 'button',
                                                            text: 'GIKAM.BUTTON.CONFIRM',
                                                            icon: 'select',
                                                            onClick: function () {
                                                                if (form.validate()) {
                                                                    var formData = form.getData();
                                                                    Gikam.getLastModal().window.showMask(true);
                                                                    Gikam.postText(url, Gikam.getJsonWrapper(formData, ['', selections])).done(function (data) {
                                                                        Gikam.getComp('emc-project-sample-list-grid').refresh();
                                                                    }).always(function () {
                                                                        Gikam.getLastModal().window.closeMask();
                                                                        modal.close();
                                                                    });
                                                                    // workspace.window.showMask(true);
                                                                    // Gikam.postText(url, Gikam.getJsonWrapper(null, ['', selections])).done(function () {
                                                                    //     Gikam.getComp('emc-project-sample-list-grid').refresh();
                                                                    // }).always(function () {
                                                                    //     workspace.window.closeMask();
                                                                    // });
                                                                }
                                                            }
                                                        }, {
                                                            type: 'button',
                                                            text: 'GIKAM.BUTTON.CANCEL',
                                                            icon: 'cancel',
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
                                                fields: emcSample.getHandsampleColumns(),
                                                autoSave: false
                                            });


                                        }
                                    },
                                }, {
                                    id: 'bfjj-btn',
                                    type: 'button',
                                    text: '部分交接',
                                    icon: 'submit',
                                    onClick: function () {
                                        var selections = Gikam.getComp('emc-project-sample-list-grid').getSelections();
                                        if (selections.length !== 1) {
                                            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_DATA');
                                            return;
                                        }

                                        let targetSample = selections[0];
                                        if (targetSample.handStatus === 'preLogged' && targetSample.ext$.analysiscount !== '0') {
                                            Gikam.alert('已经提交到样品接收，不可再次提交！');
                                            return;
                                        }

                                        if (targetSample.ext$.analysiscount === '0') {
                                            Gikam.alert('不需要样品接收！');
                                            return;
                                        }

                                        Gikam.create('modal', {
                                            width: '80%',
                                            title: '选择样品',
                                            url: emcSample.baseUrl + '/page/order-container-choose-list' + Gikam.param({
                                                sampleId: selections[0].id,
                                                targetType: 'partHandover'
                                            }),
                                            onAfterClose: function (data) {
                                                if (!Gikam.isEmpty(data)) {
                                                    workspace.window.showMask(true);
                                                    Gikam.postText(emcSample.baseUrl + '/action/part-handover', Gikam.getJsonWrapper(null, ['emcOrderContainerServiceImpl', data], ['emcSampleServiceImpl', selections])).done(function () {
                                                        Gikam.getComp('emc-project-sample-list-grid').refresh();
                                                    }).always(function () {
                                                        workspace.window.closeMask();
                                                    });
                                                }
                                            }
                                        });
                                    },
                                }, {
                                    type: 'button',
                                    text: 'GIKAM.BUTTON.TASK',
                                    icon: 'print',
                                    onClick: function () {
                                        Gikam.viewReport('rasproject', Gikam.getComp('emc-project-sample-list-grid').getSelections());
                                    }
                                }, {
                                    type: 'button',
                                    text: '数据汇总表',
                                    icon: 'print',
                                    hidden: _this.param.bpmn_statusCode == 'mutual',
                                    onClick: function () {
                                        Gikam.viewReport('sjhzb', Gikam.getComp('emc-project-sample-list-grid').getSelections());
                                    }
                                },]
                        }]
                    },

                }]
            }, {
                title: '已完成',
                id: 'emc-sample-audit-list-base-info-panel2',
                items: [{
                    type: 'layout',
                    north: {
                        height: '100%',
                        items: [{
                            type: 'grid',
                            id: 'emc-project-sample-list-grid2',
                            page: true,
                            pageSize: 100,
                            url: emcSample.baseUrl + '/queries/searchable',
                            columns: emcSample.getProjectAuditSampleColumns(),
                            requestData: {
                                SCENEUSERNAMEFILTER: '1',
                                processstatus_seq: 'done',
                                targetId: this.param.bpmn_statusCode
                            },
                            service: 'emcSampleServiceImpl',
                            order: {
                                urgent: 'desc'
                            },
                            onCellClick: function (field, row) {
                                if (field === 'sampleNo') {
                                    _this.loadSampleExpandRowPage(row);
                                }
                            },
                            // rowStyleFormatter: function (row) {
                            //     if (row.ext$.urgent === '1') {
                            //         return {
                            //             backgroundColor: Gikam.emcUrgentColor
                            //         }
                            //     }
                            // },
                            onRendered: function () {
                                var formData = Gikam.getComp('emc-order-task-result-edit-list-button-group-by-test');
                                var status = _this.param.bpmn_statusCode;
                                if (formData !== undefined) {
                                    formData.clickHandle();
                                };
                                if (status === 'mutual') {
                                    this.hideColumns(['ext$.auditdate']);
                                } else {
                                    this.hideColumns(['ext$.reviewdate', 'ext$.sceneusername']);
                                }
                            },
                            toolbar: [{
                            //     type: 'form',
                            //     autoSave: false,
                            //     fields: emcSample.getShowAllField(),
                            //     onRendered: function () {
                            //         var status = _this.param.bpmn_statusCode;
                            //         if (status === 'mutual') {
                            //             this.showFields(['showFlag']);
                            //         } else {
                            //             this.hideFields(['showFlag']);
                            //         }
                            //     },
                            // },{
                                type: 'form',
                                autoSave: false,
                                fields: emcSample.getAuditShowAllField(_this.param.bpmn_statusCode),
                                // onRendered: function () {
                                //     var status = _this.param.bpmn_statusCode;
                                //     if (status === 'audit') {
                                //         this.showFields(['showFlag']);
                                //     } else {
                                //         this.hideFields(['showFlag']);
                                //     }
                                // },
                            }, {
                                type: 'button',
                                text: 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_SAMPLE',
                                icon: 'browse',
                                onClick: function () {
                                    _this.openDoneBrowseRecord();
                                }
                                //            }, {
                                //                type: 'button',
                                //                text: 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_SAMPLE',
                                //                icon: 'print',
                                //                onClick: function () {
                                //                    var selections = Gikam.getComp('emc-project-sample-list-grid').getSelections();
                                //                    if (selections.length !== 1) {
                                //                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                //                        return;
                                //                    }
                                //                    Gikam.viewReport(selections[0].reportConfigLineId, selections[0]);
                                //                }
                            }, {
                                type: 'button',
                                text: '撤回',
                                icon: 'back',
                                onClick: function () {
                                    _this.withDraw();
                                }
                            }]
                        }]
                    },

                }]
            }]
        }
    },

    withDraw : function() {
        let selections = Gikam.getComp('emc-project-sample-list-grid2').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.confirm('GIKAM.TIP.CONFIRM', '确定撤回吗', function() {
            workspace.window.showMask(true);
            Gikam.postText(emcSample.baseUrl + '/action/with-draw', Gikam.getJsonWrapper(null, [ '', selections ])).done(function(r) {
                Gikam.getComp('emc-project-sample-list-grid').refresh();
                Gikam.getComp('emc-project-sample-list-grid2').refresh();
            }).always(function () {
                workspace.window.closeMask();
            });
        })
    },

    /** 加载采样信息页面 */
    loadSampleExpandRowPage: function (row) {
        var _param = {
            sampleId: row.id,
            projId: row.projId,
            propertyTmplId: row.propertyTmplId,
            monitorTmplId: row.monitorTmplId,
            sampleFlag: row.sampleFlag,
            orderCategory: row.orderCategory,
            sampleUserId: row.sampleUserId,
            sampleDate: row.sampleDate,
            tmplId: row.ext$.tmplid,
            readonly: true,
        };
        Gikam.create('modal', {
            title: '采样信息',
            width: '90%',
            height: '90%',
            url: emcSample.baseUrl + '/page/sample-expand-detail' + Gikam.param(_param),
        });
    },

    getProjectSampleGridParam: function () {
        var _this = this;
        _this.param.org_filter = '1';
        if (_this.param.bpmn_statusCode == 'mutual') {
            _this.param.targetType = 'editSample';
            _this.param.sceneUserId = workspace.user.id;
        }

        return {
            type: 'grid',
            id: 'emc-project-sample-list-grid',
            page: true,
            pageSize: 100,
            url: emcSample.baseUrl + '/queries/tasks',
            columns: emcSample.getProjectAuditSampleColumns(),
            requestData: _this.param,
            service: 'emcSampleServiceImpl',
            order: {
                urgent: 'desc'
            },
            rowStyleFormatter: function (row) {
                if (row.ext$.urgent === '1') {
                    return {
                        backgroundColor: Gikam.emcUrgentColor
                    }
                }
            },
            onRendered: function () {
                var formData = Gikam.getComp('emc-order-task-result-edit-list-button-group-by-test');
                var status = _this.param.bpmn_statusCode;
                if (formData !== undefined) {
                    formData.clickHandle();
                }
                ;
                if (status === 'mutual') {
                    this.hideColumns(['ext$.auditdate']);
                } else {
                    this.hideColumns(['ext$.reviewdate', 'ext$.sceneusername']);
                }
            },
            toolbar: [{
                type: 'form',
                autoSave: false,
                fields: emcSample.getShowAllField(),
                onRendered: function () {
                    var status = _this.param.bpmn_statusCode;
                    if (status === 'mutual') {
                        this.showFields(['showFlag']);
                    } else {
                        this.hideFields(['showFlag']);
                    }
                },
            }, {
                type: 'button',
                text: 'GIKAM.WORKFLOW.BUTTON.PASS',
                icon: 'pass',
                onClick: function () {
                    _this.pass();
                }
            }, {
                type: 'button',
                text: 'GIKAM.WORKFLOW.BUTTON.REJECT',
                icon: 'reject',
                onClick: function () {
                    _this.reject();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_SAMPLE',
                icon: 'browse',
                onClick: function () {
                    _this.openBrowseRecord();
                }
//            }, {
//                type: 'button',
//                text: 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_SAMPLE',
//                icon: 'print',
//                onClick: function () {
//                    var selections = Gikam.getComp('emc-project-sample-list-grid').getSelections();
//                    if (selections.length !== 1) {
//                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
//                        return;
//                    }
//                    Gikam.viewReport(selections[0].reportConfigLineId, selections[0]);
//                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.VIEW_CALIBRATION_REPORT',
                icon: 'print',
                onClick: function () {
                    var selections = Gikam.getComp('emc-project-sample-list-grid').getSelections();
                    if (selections.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    Gikam.viewReport(selections[0].caliReportLineId, selections[0]);
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SAMPLING_EQUIPMENT',
                icon: 'modify',
                hidden: false,
                onClick: function () {
                    var selections = Gikam.getComp('emc-project-sample-list-grid').getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }

                    var ids = selections.map(row => row.id).join();

                    Gikam.create('modal', {
                        width: '60%',
                        title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.SAMPLING_EQUIPMENT',
                        url: IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/page/sampling-equipt' + Gikam.param({
                            ids: ids,
                            readonly: true
                        })
                    });
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.DELIVERY.PREVIEW',
                icon: 'print',
                onClick: function () {
                    var selections = Gikam.getComp('emc-project-sample-list-grid').getSelections();
                    if (selections.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    Gikam.viewReport(selections[0].receiveReportLineId, selections[0]);
                }
//            }, {
//                type: 'button',
//                text: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.ANNEX',
//                icon: 'upload',
//                onClick: function () {
//                    _this.uploadSampleFile();
//                }
            }, {
                type: 'button',
                text: '样品交接',
                icon: 'submit',
                onClick: function () {
                    var selections = Gikam.getComp('emc-project-sample-list-grid').getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                        return;
                    }
                    var falg = true;
                    selections.forEach(function (item) {
                        if (item.handStatus === 'preLogged' && item.ext$.analysiscount !== '0') {
                            Gikam.alert('已经提交到样品接收，不可再次提交！');
                            falg = false;
                            return;
                        }
                        if (item.ext$.analysiscount === '0') {
                            Gikam.alert('不需要样品接收！');
                            falg = false;
                            return;
                        }
                    });
                    var url = emcSample.baseUrl + '/action/handsample';
                    if (falg) {

                        //填写样品接收时间
                        var modal = Gikam.create('modal', {
                            title: "GIKAM.BUTTON.BATCH_INPUT",
                            width: 400,
                            height: 270,
                        });
                        var layout = Gikam.create('layout', {
                            renderTo: modal.window.$dom,
                            center: {
                                items: [{
                                    type: 'btnToolbar',
                                    items: [{
                                        type: 'button',
                                        text: 'GIKAM.BUTTON.CONFIRM',
                                        icon: 'select',
                                        onClick: function () {
                                            if (form.validate()) {
                                                var formData = form.getData();
                                                Gikam.getLastModal().window.showMask(true);
                                                Gikam.postText(url, Gikam.getJsonWrapper(formData, ['', selections])).done(function (data) {
                                                    Gikam.getComp('emc-project-sample-list-grid').refresh();
                                                }).always(function () {
                                                    Gikam.getLastModal().window.closeMask();
                                                    modal.close();
                                                });
                                                // workspace.window.showMask(true);
                                                // Gikam.postText(url, Gikam.getJsonWrapper(null, ['', selections])).done(function () {
                                                //     Gikam.getComp('emc-project-sample-list-grid').refresh();
                                                // }).always(function () {
                                                //     workspace.window.closeMask();
                                                // });
                                            }
                                        }
                                    }, {
                                        type: 'button',
                                        text: 'GIKAM.BUTTON.CANCEL',
                                        icon: 'cancel',
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
                            fields: emcSample.getHandsampleColumns(),
                            autoSave: false
                        });


                    }
                },
            }, {
                type: 'button',
                text: '部分交接',
                icon: 'submit',
                onClick: function () {
                    var selections = Gikam.getComp('emc-project-sample-list-grid').getSelections();
                    if (selections.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_DATA');
                        return;
                    }

                    let targetSample = selections[0];
                    if (targetSample.handStatus === 'preLogged' && targetSample.ext$.analysiscount !== '0') {
                        Gikam.alert('已经提交到样品接收，不可再次提交！');
                        return;
                    }

                    if (targetSample.ext$.analysiscount === '0') {
                        Gikam.alert('不需要样品接收！');
                        return;
                    }

                    Gikam.create('modal', {
                        width: '80%',
                        title: '选择样品',
                        url: emcSample.baseUrl + '/page/order-container-choose-list' + Gikam.param({
                            sampleId: selections[0].id,
                            targetType: 'partHandover'
                        }),
                        onAfterClose: function (data) {
                            if (!Gikam.isEmpty(data)) {
                                workspace.window.showMask(true);
                                Gikam.postText(emcSample.baseUrl + '/action/part-handover', Gikam.getJsonWrapper(null, ['emcOrderContainerServiceImpl', data], ['emcSampleServiceImpl', selections])).done(function () {
                                    Gikam.getComp('emc-project-sample-list-grid').refresh();
                                }).always(function () {
                                    workspace.window.closeMask();
                                });
                            }
                        }
                    });
                },
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.TASK',
                icon: 'print',
                onClick: function () {
                    Gikam.viewReport('rasproject', Gikam.getComp('emc-project-sample-list-grid').getSelections());
                }
            },],
        }
    },

    uploadSampleFile: function () {
        var _this = this
        var rows = Gikam.getComp('emc-project-sample-list-grid').getSelections();
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

    getAuditedSampleDataGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-project-sample-list-grid',
            page: false,
            url: emcSample.baseUrl + '/queries/undoable',
            columns: emcSample.getAuditedSampleDataColumns(),
            requestData: {
                targetType: 'auditedData',
                org_filter: '1',
            }
        }
    },

    /*    pass : function() {
            var grid = Gikam.getComp('emc-project-sample-list-grid');
            Gikam.create('workflow').pass({
                data : grid.getSelections(),
                pageObject : emcSample
            }).done(function() {
                grid.refresh();
            });
        },*/

    openBrowseRecord: function () {
        var _this = this,
            _choose = Gikam.choose.getConfig('browseSampleRecords'),
            _grid = Gikam.getComp('emc-project-sample-list-grid'),
            _selections = _grid.getSelections();

        if (_selections.length != 1) {
            Gikam.alert("GIKAM.TIP.CHOOSE_ONE_DATA");
            return;
        }

        Gikam.create('modal', {
            width: _choose.width,
            height: '80%',
            title: _choose.title,
            url: _choose.url + Gikam.param({
                sampleId: _selections[0].id,
            }),
        });
    },
    openDoneBrowseRecord: function () {
        var _this = this,
            _choose = Gikam.choose.getConfig('browseSampleRecords'),
            _grid = Gikam.getComp('emc-project-sample-list-grid2'),
            _selections = _grid.getSelections();

        if (_selections.length != 1) {
            Gikam.alert("GIKAM.TIP.CHOOSE_ONE_DATA");
            return;
        }

        Gikam.create('modal', {
            width: _choose.width,
            height: '80%',
            title: _choose.title,
            url: _choose.url + Gikam.param({
                sampleId: _selections[0].id,
            }),
        });
    },
    pass: function () {
        var grid = Gikam.getComp('emc-project-sample-list-grid');
        // var url = Gikam.printf(IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/action/pass-reminds');
        var that = this;
        var selections = grid.getSelections();
        //至少选择
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var subFlag = true;
        selections.forEach(function (item) {
            if (item.handStatus == 'draft' && item.ext$.analysiscount != '0') {
                Gikam.alert('请先进行样品交接后进行提交！');
                subFlag = false;
                return;
            }
        });
        if (subFlag) {
            if (grid.validate(selections)) {
                Gikam.create('workflow').pass({
                    data: grid.getSelections(),
                    pageObject: emcSample
                }).done(function () {
                    grid.refresh();
                });
            }
            // Gikam.postText(url, Gikam.getJsonWrapper({}, ['', selections])).done(function (message) {
            //     console.log(message);
            //     if (Gikam.isEmpty(message)) {
            //         if (grid.validate(selections)) {
            //             Gikam.create('workflow').pass({
            //                 data: grid.getSelections(),
            //                 pageObject: emcSample
            //             }).done(function () {
            //                 grid.refresh();
            //             });
            //         }
            //     } else {
            //         Gikam.confirm('EMC.MODULE.BP.FOLDER.FOLDERS.TIP.CONTINUESUBMITTING', message, function () {
            //             if (grid.validate(selections)) {
            //                 Gikam.create('workflow').pass({
            //                     data: grid.getSelections(),
            //                     pageObject: emcSample
            //                 }).done(function () {
            //                     grid.refresh();
            //                 });
            //             }
            //         })
            //
            //     }
            // })
        }
    },

    reject: function () {
        var grid = Gikam.getComp('emc-project-sample-list-grid');
        Gikam.create('workflow').reject({
            data: grid.getSelections(),
            pageObject: emcSample
        }).done(function () {
            grid.refresh();
        });
    },

    loadDataInputPage: function (id) {
        var _this = this;
        Gikam.create('modal', {
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.DATA_INPUT',
            url: emcSample.baseUrl + '/page/data-input-detail' + Gikam.param({
                id: id,
                // readonly : true,
                pageSource: 'report'
            }),
        });
    },

    loadCalibrationPage: function (id) {
        Gikam.create('modal', {
            width: '80%',
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.INSTRUMENT_ADJUSTMENT',
            url: IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipt-calibrations/page/detail-list' + Gikam.param({
                id: id,
            }),
        });
    },

    createSample: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-project-audit-list-layout',
            renderTo: workspace.window.$dom,
            center: {
                items: [this.getBaseInfoTab()]
            }
        });
    },

    createAuditedSampleData: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-project-audit-list-layout',
            renderTo: workspace.window.$dom,
            center: {
                items: [this.getAuditedSampleDataGridParam()]
            }
        });
    },

    init: function (param) {
        this.param = param;
        // 去掉创建人不能互审的限制
        //param.createdById_DIFFER = workspace.user.id;
        if (param.category === 'auditedSampleData') {
            this.createAuditedSampleData();
        } else {
            this.createSample();
        }
    }
};
