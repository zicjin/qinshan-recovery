emcEquipt.searchEquiptMaintainPage = {
		getGridParam : function() {
	        var _this = this;
	        requestData = this.param;
	        return {
	            type : 'grid',
	            id : 'emc-equipt-maintain-repair-search-list',
                url :IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipts/action/select-equipt-maintain-repair-search',
	            columns : emcEquipt.getMainTainRepairSearchListColumns(),
	            service : 'PfEquiptService',
	            page : true,
	        }
	    },



    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-equipt-maintain-repair-search-list-layout',
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
