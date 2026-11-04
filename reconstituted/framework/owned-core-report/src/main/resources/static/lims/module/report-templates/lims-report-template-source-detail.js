limsReportTemplate.sourceDetailPage = {

    getBaseForm : function() {
        var _this = this;
        return {
            type : 'form',
            id : _this.idManager.limsReportTemplateSourceForm,
            url : _this.getUrl('limsReportTemplateSourceForm'),
            service : 'limsReportTemplateSourceServiceImpl',
            columns : 2,
            fields : limsReportTemplate.getTemplateSourceFormFields(),
            service : 'limsReportTemplateSourceService',
            onUpdated : function(data) {
                if (data.sql) {
                    Gikam.getComp(_this.idManager.limsReportTemplateSourceColumnGrid).refresh();
                }
            }
        }
    },

    getTemplateColumnGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : _this.idManager.limsReportTemplateSourceColumnGrid,
            url : _this.getUrl('limsReportTemplateSourceColumnGrid'),
            service : 'limsReportTemplateColumnServiceImpl',
            columns : limsReportTemplate.getTemplateColumnListColumns(),
            toolbar : [{
                type : 'button',
                text : 'LIMS.MODULE.REPORTTEMPLATES.BUTTON.REFRESH_COLUMN',
                icon : 'refresh',
                onClick : function() {
                    _this.refreshColumns();
                }
            }]
        };
    },

    refreshColumns : function() {
        var _this = this;
        var sourceForm = Gikam.getComp(_this.idManager.limsReportTemplateSourceForm);
        var data = sourceForm.getData();
        if (Gikam.isEmpty(data.sql)) {
            Gikam.alert('LIMS.MODULE.REPORTTEMPLATES.TIP.ADD_SQL_FIRST');
            return;
        }
        Gikam.getLastModal().window.showMask();
        Gikam.postText(Gikam.printf(limsReportTemplate.baseUrl + '/sources/{id}/columns/action/refresh', {
            id : data.id
        }), Gikam.getJsonWrapper({
            clear : '1'
        })).done(function() {
            Gikam.getComp(_this.idManager.limsReportTemplateSourceColumnGrid).refresh();
        }).always(function() {
            Gikam.getLastModal().window.closeMask();
        });
    },

    urlManager : {
        limsReportTemplateSourceForm : limsReportTemplate.baseUrl + '/sources/{id}',
        limsReportTemplateSourceColumnGrid : limsReportTemplate.baseUrl + '/sources/{id}/template-columns/queries'
    },

    getUrl : function(name) {
        var _this = this;
        if (_this.param && _this.param.id) {
            return Gikam.printf(_this.urlManager[name], {
                id : _this.param.id
            });
        } else {
            return '';
        }
    },

    idManager : {
        limsReportTemplateSourceDetailLayout : 'lims-report-template-source-detail-layout',
        limsReportTemplateSourceForm : 'lims-report-template-source-detail-form',
        limsReportTemplateSourceColumnGrid : 'lims-report-template-source-detail-column-grid'
    },
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : _this.idManager.limsReportTemplateSourceDetailLayout,
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [this.getBaseForm()]
            },
            east : {
                title : 'LIMS.MODULE.REPORTTEMPLATES.TAB.TEMPLATE_COLUMN',
                width : '50%',
                items : [this.getTemplateColumnGrid()]
            }
        });
    },

    init : function(param) {
        var _this = this;
        _this.param = param;
        this.create();
    }
};
