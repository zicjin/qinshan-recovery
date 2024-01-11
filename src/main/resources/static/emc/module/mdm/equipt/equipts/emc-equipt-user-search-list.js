emcEquipt.searchUserPage = {
		getGridParam : function() {
	        var _this = this;
	        requestData = this.param;
	        return {
	            type : 'grid',
	            id : 'emc-equipt-user-search-list',
                url :IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipts/action/select-equipt-user',
	            columns : emcEquipt.getEquiptUserSearchColumns(),
	            service : 'PfEquiptService',
	            page : true,
	        }
	    },



    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-equipt-user-search-list-layout',
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
