emcEquipt.equiptPreCheckPlanSearchPage = {
		getGridParam : function() {
	        var _this = this;
	        requestData = this.param;
	        return {
	            type : 'grid',
	            id : 'emc-equipt-pre-check-plan-search-list',
                url :IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipts/action/select-equipt-per-check-search',
	            columns : emcEquipt.getEquiptPreCheckPlanCheckSearchColumns(),
	            service : 'PfEquiptService',
	            page : true,
                onRendered : function() {
                    this.hideColumns([ 'checkUserId' ]);
                },
	        }
	    },



    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-equipt-pre-check-plan-search-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [  this.getGridParam() ]
            }

        });
    },
    
    init : function(param) {
        this.param = param;
        this.create();
    }
};
