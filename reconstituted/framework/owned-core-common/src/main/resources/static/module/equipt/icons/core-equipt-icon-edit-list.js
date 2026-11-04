coreEquiptIcon.editPage = {

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-equipt-icon-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ {
                    type : 'uploader',
                    bizId : 'equiptIcon',
                    dbTable : 'T_CORE_EQUIPT',
                    onUploadSuccess : function() {
                        this.grid.refresh();
                    }
                } ]
            }
        });
    },

    init : function() {
        this.create();
    },
};