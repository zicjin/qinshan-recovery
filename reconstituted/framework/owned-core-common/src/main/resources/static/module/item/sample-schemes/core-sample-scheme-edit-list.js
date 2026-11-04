coreSampleScheme = {};

coreSampleScheme.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'aql',
            id : 'core-sample-scheme-edit-list-aql'
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-sample-scheme-edit-list-layout',
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
