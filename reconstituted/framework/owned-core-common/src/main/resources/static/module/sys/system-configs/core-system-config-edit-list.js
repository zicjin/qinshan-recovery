coreSystemConfig.editPage = {

    create : function() {
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [{
                    type : 'SettingSkin'
                }]
            }
        });
        workspace.window.removeTitle();
    },

    init : function() {
        this.create();
    }
}