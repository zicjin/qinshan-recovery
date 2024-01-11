emcInternaAudit.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-internal-audit-edit-list-grid',
            url : emcInternaAudit.baseUrl + '/queries',
            service : 'emcInternaAuditServiceImpl',
            columns : emcInternaAudit.getSearchListColumns(),
            deleteFormatter : function(row) {
                return row.qualityControlNo || row.id;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
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
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('internalAuditPlan', Gikam.getComp('emc-internal-audit-edit-list-grid').getSelections());
                }
            }]
        }
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
                    readonly : true
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
