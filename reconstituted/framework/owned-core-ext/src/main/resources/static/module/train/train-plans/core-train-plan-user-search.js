coreTrainPlan.userSearchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-train-plan-user-search-list-grid',
            url : Gikam.printf(coreTrainPlan.baseUrl + '/{id}/users/queries', {
                id : _this.param.id,
            }),
            columns : coreTrainPlan.getUserColumns()
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        var _this = this;
        _this.param = param;
        _this.create();
    }
};