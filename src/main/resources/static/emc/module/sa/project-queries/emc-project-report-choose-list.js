emcProjectQuery.reportChoosePage = {

    getReportFileGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-project-report-file-grid',
            columns : emcProjectQuery.getProjectReportColumns(),
            url : emcProjectQuery.projectUrl + '/reports/queries',
            requestData: {
                projectId: _this.param.id
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-project-report-file-grid').getSelections());
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getReportFileGrid() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    },
};
