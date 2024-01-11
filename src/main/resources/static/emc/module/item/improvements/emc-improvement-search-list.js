emcImprovement.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-improvement-search-list-grid',
            url : emcImprovement.baseUrl + '/queries/searchable',
            columns : emcImprovement.getSearchListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-improvement-search-list-grid').activeRowByIndex(0);
                } else {
                    if (Gikam.getComp('emc-improvement-search-list-form')) {
                        Gikam.getComp('emc-improvement-search-list-form').cleanData();
                    }

                    if (Gikam.getComp('emc-improvement-search-list-uploader')) {
                        Gikam.getComp('emc-improvement-search-list-uploader').grid.cleanData();
                    }
                    
                    if (Gikam.getComp('emc-improvement-search-report-list-form')) {
                        Gikam.getComp('emc-improvement-search-report-list-form').grid.cleanData();
                    }
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-improvement-search-list-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcImprovement.baseUrl + '/{id}', {
                            id : row.id
                        })

                    });
                });
                Gikam.getAsyncComp('emc-improvement-search-report-list-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcImprovement.baseUrl + '/{id}', {
                            id : row.id
                        })

                    });
                });
                
                Gikam.getComp('emc-improvement-search-list-uploader').setOptions({
                    bizId : row.id
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('improvePlan', Gikam.getComp('emc-improvement-search-list-grid').getSelections());
                }
            }]
        }
    },

    getFormParam : function() {
        return {
            type : 'form',
            id : 'emc-improvement-search-list-form',
            service : 'emcImprovementServiceImpl',
            fields : emcImprovement.getDetailFormFields(),
            columns : 3,
            readonly : true
        };
    },
    
    getReportFormParam : function() {
        return {
            type : 'form',
            id : 'emc-improvement-search-report-list-form',
            service : 'emcImprovementServiceImpl',
            fields : emcImprovement.getReportFormFields(),
            columns : 3
        };
    },
    
    getAttachmentGridParam : function() {
        return {
            type : 'uploader',
            id : 'emc-improvement-search-report-list-uploader',
            dbTable : 'T_EMC_IMPROVEMENT',
            readonly : true
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-improvement-search-list-layout',
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

    init : function() {
        this.create();
    }
};
