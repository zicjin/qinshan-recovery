emcReport.undoPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-report-undo-grid',
            filter : false,
            url : emcReport.baseUrl + '/analysis-orgs/queries/undoable',
            columns : emcReport.getAnalysisOrgUndoListColumns(),
            requestData : _this.param,
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-report-edit-run-choose-shuttle-frame').done(function(shuttle) {
                    if (row.analysisType !== 'analysis') {
                        shuttle.setLeftData([]);
                        return;
                    }

                    shuttle.leftGrid.refresh({
                        url : IFM_CONTEXT + '/secure/emc/module/bp/result/runs/queries',
                        requestData : {
                            reportId : _this.param.reportId,
                            targetType : 'reportRun',
                            analysisOrgId : row.analysisOrgId
                        }
                    });
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    _this.confirm();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    getRefuseForm : function() {
        return {
            type : 'form',
            id : 'emc-report-undo-refuse-form',
            titleWidth : 70,
            titleAlign : 'left',
            layout : 'vertical',
            fields : [ {
                field : 'refuseReason',
                title : 'T_EMC_REPORT.REFUSEREASON',
                type : 'textarea',
                colspan : 2,
                height : 100,
                validators : ['notEmpty'],
                styleFormatter : function() {
                    return {
                        marginTop : 20
                    }
                },
            } ]
        }

    },

    confirm : function() {
        let orgs = Gikam.getComp('emc-report-undo-grid').getSelections();
        let analysisTypes = Array.from(new Set(orgs.map(item => item.analysisType)));
        if (analysisTypes.length > 1) {
            Gikam.alert('EMC.MODULE.BP.REPORT.REPORTS.TIP.CANNOT_UNDO_WITH_DIFF_ORG');
            return;
        }

        let form = Gikam.getComp('emc-report-undo-refuse-form');
        if (!form.validate()) {
            return;
        }

        let includeSampling = analysisTypes.indexOf('sampling') >= 0;
        if (includeSampling) {
            Gikam.getLastModal().close({
                analysisType : 'sampling',
                data : orgs,
                refuseReason : form.getData().refuseReason
            });
            return;
        }

        let runs = Gikam.getComp('emc-report-edit-run-choose-shuttle-frame').getSelectList();
        if (Gikam.isEmpty(runs)) {
            Gikam.alert('EMC.MODULE.BP.REPORT.REPORTS.TIP.CHOOST_ONE_RUN_AT_LEAST');
            return;
        }

        Gikam.getLastModal().close({
            analysisType : 'analysis',
            data : runs,
            refuseReason : form.getData().refuseReason
        });
    },

    getShuttleFrame : function() {
        let _this = this, columns = emcReport.getRunEditListColumns();
        return {
            type : 'shuttleFrame',
            id : 'emc-report-edit-run-choose-shuttle-frame',
            columns : columns,
            leftGridUrl : '',
            rightGridUrl : '',
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-report-undo-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            south : {
                height : '60%',
                items : [ _this.getShuttleFrame() ]
            },
            west : {
                width : '50%',
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getRefuseForm() ]
            }
        });

        Gikam.getAsyncComp('emc-report-undo-grid').done(function(grid) {
            grid.options.checkOneOnActive = true;
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
