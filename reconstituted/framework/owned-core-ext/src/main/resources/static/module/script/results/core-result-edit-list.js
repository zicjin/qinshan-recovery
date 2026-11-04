coreResult.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-result-edit-list-grid',
            url : coreResult.baseUrl + '/queries/raw',
            columns : coreResult.getEditListColumns(),
            service : 'coreResultServiceImpl',
            onUpdated : function(row, fields) {
                if (fields.indexOf('originalResult') >= 0) {
                    workspace.window.showMask();
                    Gikam.put(coreResult.baseUrl + '/action/compute', Gikam.getJsonWrapper({}, [ '', [ row ] ])).done(function() {
                        Gikam.getComp('core-result-edit-list-grid').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });

                }
            },
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-result-edit-list-layout',
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
