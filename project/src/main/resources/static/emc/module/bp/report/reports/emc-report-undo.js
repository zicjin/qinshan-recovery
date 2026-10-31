emcReport.undoPage = {

    getBtnToolbar: function () {
        var _this = this;
        return {
            type: 'btnToolbar',
            items: [{
                type: 'button',
                text: 'GIKAM.BUTTON.CONFIRM',
                icon: 'select',
                onClick: function () {
                    _this.confirm();
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.CANCEL',
                icon: 'cancel',
                onClick: function () {
                    Gikam.getLastModal().close();
                }
            },]
        }
    },

    // getGridParam : function() {
    //     var _this = this;
    //     let requestData = _this.param;
    //     let hiddenUndoSample = false;
    //     if (_this.param.sample === '0') {
    //         requestData['analysisType'] = 'analysis';
    //         hiddenUndoSample = true;
    //     }
    //
    //     return {
    //         type : 'grid',
    //         id : 'emc-report-undo-grid',
    //         filter : false,
    //         url : emcReport.baseUrl + '/analysis-orgs/queries/undoable',
    //         columns : emcReport.getAnalysisOrgUndoListColumns(),
    //         requestData : _this.param,
    //         onLoadSuccess : function(data) {
    //             if (!Gikam.isEmpty(data)) {
    //                 this.activeRowByIndex(0);
    //             }
    //         },
    //         onRowActive : function(index, row) {
    //             let tab = Gikam.getComp('emc-report-undo-run-sample-tab');
    //
    //             if (row.analysisType === 'analysis') {
    //                 // tab.removePanel('emc-report-undo-sample-panel');
    //                 // tab.revertPanel('emc-report-undo-run-panel');
    //
    //                 if (_this.runShuttleFrame) {
    //                     _this.runShuttleFrame.leftGrid.refresh({
    //                         url : IFM_CONTEXT + '/secure/emc/module/bp/result/runs/queries',
    //                         requestData : {
    //                             reportId : _this.param.reportId,
    //                             targetType : 'reportRun',
    //                             analysisOrgId : row.analysisOrgId
    //                         }
    //                     });
    //                 }
    //
    //             } else {
    //                 // tab.removePanel('emc-report-undo-run-panel');
    //                 // tab.revertPanel('emc-report-undo-sample-panel');
    //
    //                 if (_this.sampleShuttleFrame) {
    //                     _this.sampleShuttleFrame.leftGrid.refresh({
    //                         url : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/queries',
    //                         requestData : {
    //                             projId_SEQ : _this.param.projId,
    //                             reportId : _this.param.reportId,
    //                             targetType : 'reportSample'
    //                         }
    //                     });
    //                 }
    //             }
    //         },
    //         toolbar : [ {
    //             type : 'button',
    //             text : 'GIKAM.BUTTON.CONFIRM',
    //             icon : 'select',
    //             onClick : function() {
    //                 _this.confirm();
    //             }
    //         }, {
    //             type : 'button',
    //             text : 'GIKAM.BUTTON.CANCEL',
    //             icon : 'cancel',
    //             onClick : function() {
    //                 Gikam.getLastModal().close();
    //             }
    //         }, {
    //             type : 'button',
    //             text : '回退所有采样单',
    //             icon : 'back',
    //             hidden : hiddenUndoSample,
    //             onClick : function() {
    //                 _this.undoAllSample();
    //             }
    //         } ]
    //     }
    // },

    getRefuseForm: function () {
        return {
            type: 'form',
            id: 'emc-report-undo-refuse-form',
            titleWidth: 70,
            titleAlign: 'left',
            layout: 'vertical',
            fields: [{
                field: 'refuseReason',
                title: 'T_EMC_REPORT.REFUSEREASON',
                type: 'textarea',
                colspan: 2,
                height: 60,
                validators: ['notEmpty'],
                styleFormatter: function () {
                    return {
                        marginTop: 20
                    }
                },
            }]
        }

    },

    confirm: function () {

        let form = Gikam.getComp('emc-report-undo-refuse-form');
        if (!form.validate()) {
            return;
        }

        let samples = Gikam.getComp('emc-report-edit-sample-choose-shuttle-frame').getSelectList();
        let runs = Gikam.getComp('emc-report-edit-run-choose-shuttle-frame').getSelectList();

        if (Gikam.isEmpty(samples) && Gikam.isEmpty(runs)) {
            Gikam.alert('请至少选择一条需要回退的数据');
            return;
        }

        Gikam.getLastModal().close({
            data: {sample: samples, run: runs},
            refuseReason: form.getData().refuseReason
        });
    },

    getBaseInfoTab: function () {
        var _this = this;
        return {
            type: 'tab',
            id: 'emc-report-undo-run-sample-tab',
            panels: [{
                title: '分析批',
                id: 'emc-report-undo-run-panel',
                items: [_this.getShuttleFrame()]
            }, {
                title: '采样单',
                id: 'emc-report-undo-sample-panel',
                items: [_this.getSampleShuttleFrame()]
            }]
        }
    },

    getShuttleFrame: function () {
        let _this = this, columns = emcReport.getRunEditListColumns();
        return {
            type: 'shuttleFrame',
            id: 'emc-report-edit-run-choose-shuttle-frame',
            columns: columns,
            leftGridUrl: '',
            rightGridUrl: '',
            leftPage: true,
            rightPage: false,
            onRendered: function () {
                _this.runShuttleFrame = this;
                _this.runShuttleFrame.leftGrid.refresh({
                    url: IFM_CONTEXT + '/secure/emc/module/bp/result/runs/queries',
                    requestData: {
                        projId: _this.param.projId,
                        targetType: 'reportData',
                        status_seq: 'done'
                    }
                });
            },
        }
    },

    getSampleShuttleFrame: function () {
        let _this = this, columns = emcReport.getSampleEditListColumns();
        return {
            type: 'shuttleFrame',
            id: 'emc-report-edit-sample-choose-shuttle-frame',
            columns: columns,
            leftGridUrl: '',
            rightGridUrl: '',
            leftPage: true,
            rightPage: false,
            onRendered: function () {
                _this.sampleShuttleFrame = this;
                _this.sampleShuttleFrame.leftGrid.refresh({
                    url: IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/queries',
                    requestData: {
                        projId_SEQ: _this.param.projId,
                        status_seq: 'done'
                    }
                });
            },
        }
    },

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-report-undo-layout',
            renderTo: Gikam.getLastModal().window.$dom,
            center: {
                items: [_this.getBaseInfoTab()]
            },
            // west : {
            //     width : '50%',
            //     items : [ _this.getGridParam() ]
            // },
            north: {
                height: '100',
                items: [_this.getBtnToolbar(), _this.getRefuseForm()]
            }
        });

        Gikam.getAsyncComp('emc-report-undo-grid').done(function (grid) {
            grid.options.checkOneOnActive = true;
        });
    },

    init: function (param) {
        console.info(param)
        this.param = param;
        this.create();
    }
};
