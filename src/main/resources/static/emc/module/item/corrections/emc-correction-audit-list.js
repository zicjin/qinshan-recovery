emcCorrection.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-correction-audit-list-grid',
            url : emcCorrection.baseUrl + '/queries/tasks',
            columns : emcCorrection.getAuditListColumns(),
            requestData : _this.param,
            onLoadSuccess : function(data) {
                var grid = this;
                if (!Gikam.isEmpty(data)) {
                    grid.activeRowByIndex(0);
                } else {
                    var form = Gikam.getComp('emc-correction-audit-list-form');
                    if(form) {
                        form.cleanData();
                    }
                    var uploader = Gikam.getComp('emc-correction-audit-list-uploader');
                    if(uploader) {
                        uploader.setOptions({
                            bizId : '',
                        });
                        uploader.grid.cleanData();
                    }
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-correction-audit-list-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcCorrection.baseUrl + '/{id}', {
                            id : row.id
                        })
                    }, true);
                });
                Gikam.getAsyncComp('emc-correction-audit-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                    uploader.grid.refresh();
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

    getFormParam : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-correction-audit-list-form',
            fields : emcCorrection.getDetailFormColumns('audit'),
            columns : 2,
            onLoadSuccess : function(data) {
                if (Gikam.isEmpty(_this.getGridData())) {
                    this.cleanData();
                }
            }
        };
    },
    
    getAttachmentGridParam : function() {
        var _this = this;
        return {
            type : 'uploader',
            id : 'emc-correction-audit-list-uploader',
            dbTable : 'T_EMC_CORRECTION',
        }
    },
    
    getGridActiveRow : function() {
        var grid = Gikam.getComp('emc-correction-audit-list-grid');
        return grid && grid.getActivedRow();
    },

    getGridData : function() {
        var grid = Gikam.getComp('emc-correction-audit-list-grid');
        return grid && grid.getData();
    },
    
    pass : function() {
        var grid = Gikam.getComp('emc-correction-audit-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : emcCorrection
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('emc-correction-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcCorrection
        }).done(function() {
            grid.refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-correction-audit-list-layout',
            renderTo : workspace.window.$dom,
            
            west : {
                width : 250,
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.ITEM.CORRECTIONS.TAB.DETAIL',
                        items : [ _this.getFormParam() ]
                    }, {
                        title : 'EMC.MODULE.ITEM.CORRECTIONS.TAB.ATTACHMENT',
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
