coreEquiptUsageRecord.monthContributionPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-equipt-month-contribution-search-list-grid',
            url : coreEquiptUsageRecord.baseUrl + '/month-contributions/queries',
            requestData : _this.param,
            columns : coreEquiptUsageRecord.getMonthlyContributionColumns(),
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-equipt-month-contribution-search-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    },
};