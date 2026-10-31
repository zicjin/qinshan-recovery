emcInconformity.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-inconformity-audit-list-grid',
            url : emcInconformity.baseUrl + '/queries/tasks',
            toolbar : _this.getBtnToolbarParam(),
            columns : emcInconformity.getAuditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    var form = Gikam.getComp('emc-inconformity-audit-list-form');
                    if(form) {
                        form.cleanData();
                        form.setOptions({
                            url : ''
                        });
                    }
                    var uploader = Gikam.getComp('emc-inconformity-audit-list-uploader');
                    if(uploader && uploader.grid.model) {
                        uploader.setOptions({
                            bizId : ''
                        });
                        uploader.grid.cleanData();
                    }
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-inconformity-audit-list-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcInconformity.baseUrl + "/{id}", {
                            id : row.id
                        }),
                    });
                });
                Gikam.getAsyncComp('emc-inconformity-audit-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                    uploader.grid.refresh();
                });
            }
        };
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
                    Gikam.viewReport('inConformity', Gikam.getComp('emc-inconformity-audit-list-grid').getSelections());
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
                title : 'EMC.MODULE.ITEM.INCONFORMITY.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'emc-inconformity-audit-list-form',
                    columns : 3,
                    fields : emcInconformity.getInconformityFieldsNoEdit()
                } ]
            }, {
                title : 'EMC.MODULE.ITEM.INCONFORMITY.TAB.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'emc-inconformity-audit-list-uploader',
                    dbTable : 'T_EMC_INCONFORMITY',
                } ]
            } ]
        }
    },

    pass : function() {
        var grid = Gikam.getComp('emc-inconformity-audit-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : emcInconformity
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('emc-inconformity-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcInconformity
        }).done(function() {
            grid.refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-inconformity-audit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 250,
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
