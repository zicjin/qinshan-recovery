coreEquipt.monitorPage = {

    create : function() {
        Gikam.create('serverDevice', {
            renderTo : workspace.window.$dom,
        });
    },

    init : function() {
        this.create();
    }
};