emcReportTmpl.choosePage = {

    getGridParam : function() {
        var _this = this, columns = emcReportTmpl.getChooseListColumns(), requestData = _this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-report-tmpl-choose-list-grid',
            url : emcReportTmpl.baseUrl + '/queries/raw',
            columns : columns,
            requestData : requestData,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var selections = Gikam.getComp('emc-report-tmpl-choose-list-grid').getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    Gikam.getLastModal().close(selections);
                }
            } ]
        }
    },

    create : function() {
        Gikam.create('layout', {
            id : 'emc-report-tmpl-choose-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getGridParam() ]
            },
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
