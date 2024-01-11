emcReportQuery.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-report-query-edit-list-grid',
            url : emcReportQuery.baseUrl + '/queries/raw',
            service : 'emcReportQueryServiceImpl',
            columns : emcReportQuery.getEditListColumns(),
            requestData : {
                createdById_SEQ : workspace.user.id
            },
            deleteFormatter : function(row) {
                return row.queryContent;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.ITEM.REPORTQUERIES.MODAL.ADD',
                        fields : emcReportQuery.getBaseInfoFormFields(),
                        url : emcReportQuery.baseUrl,
                        width : 400,
                        height : 220
                    }).done(function(id) {
                        Gikam.getComp('emc-report-query-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-report-query-edit-list-grid').deleteRows(emcReportQuery.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_EMC_REPORT_QUERY', Gikam.getComp('emc-report-query-edit-list-grid').getSelections());
                }
            } ]
        }
    },

    submit : function() {
        var grid = Gikam.getComp('emc-report-query-edit-list-grid');
        var selections = grid.getSelections();
        if(!grid.validate(selections))
            return;
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : emcReportQuery
        }).done(function() {
            grid.refresh();
        });
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-report-query-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },
    

    init : function() {
        this.create();
    },

};
