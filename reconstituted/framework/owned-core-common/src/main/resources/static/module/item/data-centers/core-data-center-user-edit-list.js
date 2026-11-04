coreDataCenterUser.editPage = {

    getGridParam : function () {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-data-center-user-edit-list-grid',
            url : coreDataCenterUser.baseUrl + '/queries/current-user',
            columns : coreDataCenterUser.getEditListColumns(),
            service : 'coreDataCenterUserServiceImpl',
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.MOVE_UP',
                icon : 'move-up',
                onClick : function () {
                    Gikam.getComp('core-data-center-user-edit-list-grid').moveUp(coreDataCenterUser.baseUrl + '/users/action/swap/{firstId}/{secondId}')
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.MOVE_DOWN',
                icon : 'move-down',
                onClick : function () {
                    Gikam.getComp('core-data-center-user-edit-list-grid').moveDown(coreDataCenterUser.baseUrl + '/users/action/swap/{firstId}/{secondId}')
                }
            } ]
        }
    },

    create : function () {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-data-center-user-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function () {
        this.create();
    }
};
