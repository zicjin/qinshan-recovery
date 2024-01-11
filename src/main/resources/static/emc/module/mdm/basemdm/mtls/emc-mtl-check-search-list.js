emcMtl.mtlSearchPage = {
		getGridParam : function() {
	        var _this = this;
	        return {
	            type : 'grid',
	            id : 'emc-mtl-check-search-list',
                url :IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/stock/action/select-mtl-check',
	            columns : emcMtl.getMtlCheckSearchColumns(),
	            service : 'emcStockService',
	            page : true,
	        }
	    },

	    create : function() {
	        var _this = this;
	        Gikam.create('layout', {
	            id : 'emc-mtl-check-search-list-layout',
	            renderTo : workspace.window.$dom,
	            center : {
	                items : [  this.getGridParam() ]
	            }
	
	        });
	    },
    
    init : function() {
        this.create();
    }
};
