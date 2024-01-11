emcCar.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-car-edit-list-grid',
            url : emcCar.baseUrl + '/queries/undoable',
            columns : emcCar.getRecordEditListColumns(),
            service : 'emcCarServiceImpl'
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-car-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
