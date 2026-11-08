emcImprovement.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-improvement-edit-list-grid',
            url : emcImprovement.baseUrl + '/queries/raw',
            columns : emcImprovement.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-improvement-edit-list-grid').activeRowByIndex(0);
                } else {
                    Gikam.getAsyncComp('emc-improvement-edit-list-form').done(function(form) {
                        form.cleanData();
                        form.setOptions({ url : '' });
                    });
                    Gikam.getAsyncComp('emc-improvement-edit-report-list-form').done(function(form) {
                        form.cleanData();
                        form.setOptions({ url : '' });
                    });
                    Gikam.getAsyncComp('emc-improvement-edit-list-uploader').done(function(uploader) {
                        uploader.setOptions({ bizId : '' });
                        uploader.grid.cleanData();
                    });
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-improvement-edit-list-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcImprovement.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
                
                Gikam.getAsyncComp('emc-improvement-edit-report-list-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcImprovement.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
                
                Gikam.getAsyncComp('emc-improvement-edit-list-uploader').done(function(uploader) {
                    uploader.setOptions({ bizId : row.id });
                });

            },
            deleteFormatter : function(row) {
                return row.qualityControlNo;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.getComp('emc-improvement-edit-list-grid').insert(emcImprovement.baseUrl, [ {} ]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-improvement-edit-list-grid').deleteRows(emcImprovement.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            },{
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('improvePlan', Gikam.getComp('emc-improvement-edit-list-grid').getSelections());
                }
       	 }  ]
        }
    },

    getFormParam : function() {
        return {
            type : 'form',
            id : 'emc-improvement-edit-list-form',
            service : 'emcImprovementServiceImpl',
            fields : emcImprovement.getDetailFormFields(),
            columns : 3
        };
    },
    
    getReportFormParam : function() {
        return {
            type : 'form',
            id : 'emc-improvement-edit-report-list-form',
            service : 'emcImprovementServiceImpl',
            fields : emcImprovement.getReportFormFields(),
            columns : 3
        };
    },
    
    getAttachmentGridParam : function() {
        return {
            type : 'uploader',
            id : 'emc-improvement-edit-list-uploader',
            dbTable : 'T_EMC_IMPROVEMENT'
        }
    },

    submit : function() {
        var grid = Gikam.getComp('emc-improvement-edit-list-grid');
        var selections = grid.getSelections();
        Gikam.create('workflow').submit({
            data : selections,
            pageObject : emcImprovement
        }).done(function() {
            grid.refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-improvement-edit-list-layout',
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
                    },{
                        title : 'EMC.MODULE.ITEM.IMPROVEMENTS.TAB.REPORT',
                        items : [ _this.getReportFormParam() ]
                    },{
                        title : 'EMC.MODULE.ITEM.IMPROVEMENTS.TAB.ATTACHMENT',
                        items : [ _this.getAttachmentGridParam() ]
                    } ]
                } ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
