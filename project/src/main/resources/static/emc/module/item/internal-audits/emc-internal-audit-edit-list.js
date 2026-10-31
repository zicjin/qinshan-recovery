emcInternaAudit.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-internal-audit-edit-list-grid',
            url : emcInternaAudit.baseUrl + '/queries/raw',
            service : 'emcInternaAuditServiceImpl',
            toolbar : _this.getBtnToolbarParam(),
            columns : emcInternaAudit.getEditListColumns(),
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
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.getComp('emc-internal-audit-edit-list-grid').insert(emcInternaAudit.baseUrl, [ {} ]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-internal-audit-edit-list-grid').deleteRows(emcInternaAudit.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    var grid = Gikam.getComp('emc-internal-audit-edit-list-grid');
                    Gikam.create('workflow').submit({
                        data : grid.getSelections(),
                        pageObject : emcInternaAudit
                    }).done(function() {
                        grid.refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_EMC_INTERNAL_AUDIT', Gikam.getComp('emc-internal-audit-edit-list-grid').getSelections());
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
                    fields : emcInternaAudit.getInternaAuditPlanFields()
                } ]
            }, {
                title : 'EMC.MODULE.ITEM.INTERNAL_AUDIT.TAB.REPORT',
                items : [ {
                    type : 'form',
                    id : 'emc-internal-audit-report-base-info-form',
                    service : 'emcInternalAuditServiceImpl',
                    columns : 2,
                    fields : emcInternaAudit.getInternaAuditReportFields()
                } ]
            }, {
                title : 'EMC.MODULE.ITEM.INTERNAL_AUDIT.TAB.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'emc-internal-audit-detail-uploader',
                    dbTable : 'T_EMC_INTERNAL_AUDIT'
                } ]
            } ]
        }
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

    init : function() {
        this.create();
    }
};
