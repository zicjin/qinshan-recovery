coreAtLog.searchPage = {
        
    create : function() {
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [{
                    type : 'AuditTrail'
                }]
            }
        })
    },
    
    init : function() {
        this.create();
    }
}