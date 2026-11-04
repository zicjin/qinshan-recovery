dataSource.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-eln-bind-data-source-edit-list-grid',
            columns : dataSource.getEditListColumns(),
            autoSave : false,
            filter : false,
            genericQuery : false,
        }
    },

    create : function() {
        Gikam.create('layout', {
            id : 'core-eln-bind-data-source-layout',
            renderTo : document.body,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function(data) {
        this.create();
        Gikam.getAsyncComp('core-eln-bind-data-source-edit-list-grid').done(function(grid) {
            grid.loadData(data)
        })
    }
};
