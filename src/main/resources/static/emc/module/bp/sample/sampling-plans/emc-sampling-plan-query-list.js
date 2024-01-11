emcSamplingPlan.queryPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sampling-plan-query-list-grid',
            url : emcSamplingPlan.baseUrl + '/action/query-sample-plans',
            columns : emcSamplingPlan.getQueryListColumns(),
            checkOnActive : true,
            filterOpen : false,
            requestData : {
                targetType : 'sampleprepare',
                projectstatus_DIFFER : 'cancel',
                status_SEQ : 'draw',
	            orgId_SB : workspace.user.ext$.parentorgid,
            },
            group : {
                fields : [ 'ext$.userid' ],
                formatter : function(row) {
                    return row.ext$.username + '(' + row.ext$.size + ')';
                }
            }
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-sampling-plan-query-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
