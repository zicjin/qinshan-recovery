emcImprovement.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-improvement-audit-list-grid',
            url : emcImprovement.baseUrl + '/queries/tasks',
            columns : emcImprovement.getAuditListColumns(),
            requestData : _this.param,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-improvement-audit-list-grid').activeRowByIndex(0);
                } else {
                    Gikam.getAsyncComp('emc-improvement-audit-list-form').done(function(form) {
                        form.cleanData();
                        form.setOptions({ url : '' });
                    });
                    Gikam.getAsyncComp('emc-improvement-audit-report-list-form').done(function(form) {
                        form.cleanData();
                        form.setOptions({ url : '' });
                    });
                    Gikam.getAsyncComp('emc-improvement-audit-list-uploader').done(function(uploader) {
                        uploader.setOptions({ bizId : '' });
                        uploader.grid.cleanData();
                    });
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-improvement-audit-list-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcImprovement.baseUrl + '/{id}', {
                            id : row.id
                        })

                    });
                });
                
                Gikam.getAsyncComp('emc-improvement-audit-report-list-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcImprovement.baseUrl + '/{id}', {
                            id : row.id
                        })

                    });
                });
                
                Gikam.getAsyncComp('emc-improvement-audit-list-uploader').done(function(uploader) {
                    uploader.setOptions({ bizId : row.id });
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
                    Gikam.viewReport('improvePlan', Gikam.getComp('emc-improvement-audit-list-grid').getSelections());
                }
            } ]
        }
    },

    getFormParam : function() {
        return {
            type : 'form',
            id : 'emc-improvement-audit-list-form',
            service : 'emcImprovementServiceImpl',
            fields : emcImprovement.getDetailFormFields(),
            columns : 3,
            readonly : true
        };
    },
    
    getReportFormParam : function() {
        return {
            type : 'form',
            id : 'emc-improvement-audit-report-list-form',
            service : 'emcImprovementServiceImpl',
            fields : emcImprovement.getDetailFormFields(),
            columns : 3,
            readonly : true
        };
    },

    getAttachmentGridParam : function() {
        return {
            type : 'uploader',
            id : 'emc-improvement-audit-list-uploader',
            dbTable : 'T_EMC_IMPROVEMENT',
        }
    },

    pass : function() {
        var grid = Gikam.getComp('emc-improvement-audit-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : emcImprovement
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('emc-improvement-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcImprovement
        }).done(function() {
            grid.refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-improvement-audit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 331,
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.ITEM.IMPROVEMENTS.TAB.DETAIL',
                        items : [ _this.getFormParam() ]
                    }, {
                        title : 'EMC.MODULE.ITEM.IMPROVEMENTS.TAB.REPORT',
                        items : [ _this.getReportFormParam() ]
                    }, {
                        title : 'EMC.MODULE.ITEM.IMPROVEMENTS.TAB.ATTACHMENT',
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
