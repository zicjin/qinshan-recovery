coreAuditLog.searchPage = {
        
    create : function() {
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [{
                    type : 'AuditTrail',
                    mode : 'current'
                }]
            }
        })
    },
    
    init : function() {
        this.create();
    }
}