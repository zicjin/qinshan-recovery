coreMonitor.editPage = {

    create : function() {
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [ {
                    id : 'core-monitor-edit-list',
                    type : 'systemMonitoring'
                } ]
            }
        });
    },

    init : function() {
        workspace.window.removeTitle();
        this.create();
    }
}