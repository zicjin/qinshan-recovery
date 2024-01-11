emcInternaAudit.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-internal-audit-edit-list-grid',
            url : emcInternaAudit.baseUrl + '/queries/tasks',
            service : 'emcInternaAuditServiceImpl',
            toolbar : _this.getBtnToolbarParam(),
            columns : emcInternaAudit.getAuditListColumns(),
            requestData : _this.param,
            deleteFormatter : function(row) {
                return row.qualityControlNo || row.id;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    var compPlan = Gikam.getComp('emc-internal-audit-plan-base-info-form');
                    if (compPlan) {
                        compPlan.cleanData();
                        compPlan.setOptions({
                            url : ''
                        });
                    }

                    var compReport = Gikam.getComp('emc-internal-audit-report-base-info-form');
                    if (compReport) {
                        compReport.cleanData();
                        compReport.setOptions({
                            url : ''
                        });
                    }

                    var compDetail = Gikam.getComp('emc-internal-audit-detail-uploader');
                    if (compDetail) {
                        compDetail.grid.cleanData();
                    }
                }
            },
            onRowActive : function(index, row) {
                if (row) {
                    Gikam.getComp('emc-internal-audit-plan-base-info-form').refresh({
                        url : Gikam.printf(emcInternaAudit.baseUrl + "/{id}", {
                            id : row.id
                        }),
                    });
                    Gikam.getComp('emc-internal-audit-report-base-info-form').refresh({
                        url : Gikam.printf(emcInternaAudit.baseUrl + "/{id}", {
                            id : row.id
                        }),
                    });

                    Gikam.getComp('emc-internal-audit-detail-uploader').setOptions({
                        bizId : row.id,
                    })
                }
            },
        }
    },

    getBtnToolbarParam : function() {
        var _this = this;
        return [ {
            type : 'btnGroup',
            items : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                icon : 'pass',
                onClick : function() {
                    _this.pass();
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                icon : 'reject',
                onClick : function() {
                    _this.reject();
                }
            },{
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('internalAuditPlan', Gikam.getComp('emc-internal-audit-edit-list-grid').getSelections());
                }
            } ]
        } ];

    },

    getTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.XL,
            panels : [ {
                title : 'EMC.MODULE.ITEM.INTERNAL_AUDIT.TAB.PLAN',
                items : [ {
                    type : 'form',
                    id : 'emc-internal-audit-plan-base-info-form',
                    service : 'emcInternalAuditServiceImpl',
                    columns : 2,
                    fields : emcInternaAudit.getInternaAuditPlanFieldsNoEdit()
                } ]
            }, {
                title : 'EMC.MODULE.ITEM.INTERNAL_AUDIT.TAB.REPORT',
                items : [ {
                    type : 'form',
                    id : 'emc-internal-audit-report-base-info-form',
                    service : 'emcInternalAuditServiceImpl',
                    columns : 2,
                    fields : emcInternaAudit.getInternaAuditReportFieldsNoEdit()
                } ]
            }, {
                title : 'EMC.MODULE.ITEM.INTERNAL_AUDIT.TAB.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'emc-internal-audit-detail-uploader',
                    dbTable : 'T_EMC_INTERNAL_AUDIT',
                } ]
            } ]
        }
    },

    pass : function() {
        var grid = Gikam.getComp('emc-internal-audit-edit-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : emcInternaAudit
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('emc-internal-audit-edit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcInternaAudit
        }).done(function() {
            grid.refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-internal-auditedit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 350,
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getTab() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
