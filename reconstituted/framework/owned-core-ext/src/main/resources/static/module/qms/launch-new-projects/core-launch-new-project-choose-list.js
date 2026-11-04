coreLaunchNewProject.choosePage = {

    getGridParam : function() {
        var _this = this,
            columns = coreLaunchNewProject.getChooseListColumns(_this.param.single);
        return {
            type : 'grid',
            id : 'core-launch-new-project-choose-list-grid',
            url : coreLaunchNewProject.baseUrl + '/queries/choosable',
            requestData : _this.param,
            columns : columns,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('core-launch-new-project-choose-list-grid').getSelections());
                }
            } ]
        }
    },

    create : function() {
        Gikam.create('layout', {
            id : 'core-launch-new-project-choose-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
