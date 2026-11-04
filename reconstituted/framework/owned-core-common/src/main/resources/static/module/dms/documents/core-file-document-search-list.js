coreFileDocument.searchPage = {

    create : function() {
        var _this = this;
        Gikam.create('annexRetrieve', {
            renderTo : workspace.window.$dom,
            url : IFM_CONTEXT + '/secure/core/module/dms/categories/tree/root'
        });
    },

    init : function() {
        this.create();
    }
};
