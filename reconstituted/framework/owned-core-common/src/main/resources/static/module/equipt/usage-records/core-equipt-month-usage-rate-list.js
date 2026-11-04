coreEquiptUsageRecord.detailPage = {
    init : function(param) {
        this.param = param;
        this.create();
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-equipt-month-usage-rate-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-equipt-month-usage-rate-list-grid',
            url : coreEquiptUsageRecord.baseUrl + '/month-usage-rates/queries',
            requestData : _this.param,
            columns : coreEquiptUsageRecord.getEquiptRateListColumns(),
        }
    }
};