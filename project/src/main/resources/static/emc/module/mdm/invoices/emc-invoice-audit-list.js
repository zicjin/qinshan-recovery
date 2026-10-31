emcInvoice.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-invoice-audit-list-grid',
            url : emcInvoice.baseUrl + '/queries/tasks',
            columns : emcInvoice.getAuditListColumns(),
            service : 'emcInvoiceServiceImpl',
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-invoice-returned-audit-list-grid']);
                    var uploader = Gikam.getComp('emc-invoice-returned-file-audit-list-uploader');
                    if(uploader && uploader.grid.model) {
                        uploader.setOptions({
                            bizId : '',
                        });
                        uploader.grid.cleanData();
                    }
                }
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('emc-invoice-returned-audit-list-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(emcInvoice.baseUrl + '/{id}/returns/queries', {
                            id : row.id
                        })
                    });
                });

                Gikam.getComp('emc-invoice-returned-file-audit-list-uploader').setOptions({
                    bizId : row.id
                });

            },
            toolbar : [ {
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
            } ]
        }
    },

    pass : function() {
        var grid = Gikam.getComp('emc-invoice-audit-list-grid');
        var selections = grid.getSelections();
        if (!grid.validate(selections)) {
            return;
        }
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : emcInvoice
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('emc-invoice-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcInvoice
        }).done(function() {
            grid.refresh();
        });
    },

    getReturnedTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.MDM.INVOICES.TAB.RECORD',
                items : [ {
                    type : 'grid',
                    fill : true,
                    id : 'emc-invoice-returned-audit-list-grid',
                    service : 'emcReturnedServiceImpl',
                    columns : emcInvoice.getReturnedAuditListColumns()
                } ]
            },{
                title : 'EMC.MODULE.MDM.INVOICES.TAB.FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-invoice-returned-file-audit-list-uploader',
                    dbTable : 'T_EMC_INVOICE'
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-invoice-audit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : 300,
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getReturnedTab() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
