emcDataReporting.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-data-reporting-edit-list-grid',
            url : Gikam.printf(IFM_CONTEXT + '/secure/core/module/sys/report-configs/{id}/lines/queries',{
                id : 'DataReported'
            }),
            columns : emcDataReporting.getEditListColumns(),
            onRowActive : function(index, row) {
                var reportUrl = Gikam.getReportUrl(row.id, []);
                Gikam.getComp("emc-data-reporting-edit-list-report-iframe").refresh({
                    src : reportUrl
                });
            }
        }
    },
    
    getViewReportIframe : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.SA.DATAREPORTINGS.TAB.REPORT_PREVIEW',
                items : [ {
                    type : 'iframe',
                    id : 'emc-data-reporting-edit-list-report-iframe',
                    src : '',
                    onRendered : function() {
                    },
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-data-query-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '20%',
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getViewReportIframe()]
            },
        });
    },

    init : function() {
        this.create();
    }
};
