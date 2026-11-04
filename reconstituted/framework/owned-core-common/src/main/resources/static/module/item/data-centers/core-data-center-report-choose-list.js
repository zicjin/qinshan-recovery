coreDataCenter.reportChoosePage = {

    getGridParam : function() {
        var _this = this;

        return {
            type : 'treeGrid',
            id : 'core-data-center-report-choose-list-grid',
            url : coreDataCenter.baseUrl + '/reports/tree-grid',
            requestData : _this.param,
            single : true,
            columns : coreDataCenter.getReportChooseListColumns(),
            page : false,
            filterOpen : true,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var selections = Gikam.getComp('core-data-center-report-choose-list-grid').getSelections();

                    Gikam.getLastModal().close(selections);
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

    create : function() {
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};