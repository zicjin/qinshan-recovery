coreNfssiReport.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                }
            } ]
        }
    },

    getReportTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.NFSSI.NFSSI_REPORT_INFO',
                items : [ {
                    type : 'form',
                    url : Gikam.printf(coreNfssiReport.baseUrl + '/{id}', {
                        id : _this.param.id
                    }),
                    service : 'coreNfssiReportServiceImpl',
                    id : 'core-nfssi-order-report-detail-info-form',
                    fields : coreNfssiReport.getReportInfoFormFields(),
                    columns : 3
                } ]
            } ]
        }
    },

    getReportItemTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.NFSSI.NFSSI_RESULT_INFO',
                items : [ {
                    type : 'grid',
                    id : 'core-nfssi-order-report-item-list-grid',
                    service : 'coreNfssiReportItemServiceImpl',
                    url : Gikam.printf(coreNfssiReport.baseUrl + '/{id}/items/queries', {
                        id : _this.param.id
                    }),
                    columns : coreNfssiReport.getReportItemListColumns()
                } ]
            } ]
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            north : {
                height : '53%',
                items : [ this.getBtnToolbar(), this.getReportTab() ]
            },
            center : {
                items : [ this.getReportItemTab() ]
            },
            renderTo : workspace.window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
