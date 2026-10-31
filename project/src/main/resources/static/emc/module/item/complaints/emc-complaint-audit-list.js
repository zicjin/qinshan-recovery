emcComplaint.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-complaint-audit-list-grid',
            url : emcComplaint.baseUrl + '/queries/tasks',
            columns : emcComplaint.getAuditListColumns(),
            requestData : _this.param,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-complaint-audit-list-grid').activeRowByIndex(0);
                } else {
                    if (Gikam.getComp('emc-complaint-audit-list-form')) {
                        Gikam.getComp('emc-complaint-audit-list-form').cleanData();
                        Gikam.getComp('emc-complaint-audit-list-form').setOptions({
                            url : ''
                        });
                    }

                    if (Gikam.getComp('emc-complaint-audit-list-uploader')) {
                        Gikam.getComp('emc-complaint-audit-list-uploader').grid.cleanData();
                    }
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-complaint-audit-list-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcComplaint.baseUrl + '/{id}', {
                            id : row.id
                        })

                    });
                });
                Gikam.getComp('emc-complaint-audit-list-uploader').setOptions({
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
            },{
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('complaint', Gikam.getComp('emc-complaint-audit-list-grid').getSelections());
                }
            } ]
        }
    },

    getFormParam : function() {
        return {
            type : 'form',
            id : 'emc-complaint-audit-list-form',
            fields : emcComplaint.getDetailFormFields(),
            columns : 3,
            readonly : true
        };
    },

    getAttachmentGridParam : function() {
        return {
            type : 'uploader',
            id : 'emc-complaint-audit-list-uploader',
            dbTable : 'T_EMC_COMPLAINT',
            readonly : true
        }
    },

    pass : function() {
        var grid = Gikam.getComp('emc-complaint-audit-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : emcComplaint
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('emc-complaint-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcComplaint
        }).done(function() {
            grid.refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-complaint-audit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 225,
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.ITEM.COMPLAINTS.TAB.DETAIL',
                        items : [ _this.getFormParam() ]
                    }, {
                        title : 'EMC.MODULE.ITEM.COMPLAINTS.TAB.ATTACHMENT',
                        items : [ _this.getAttachmentGridParam() ]
                    } ]
                } ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
