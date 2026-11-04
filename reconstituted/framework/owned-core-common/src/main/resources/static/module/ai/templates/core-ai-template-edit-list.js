coreAiTemplate.editPage = {
    
    create : function() {
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [ {
                    id : 'core-ai-template-ocr-edit-list',
                    type : 'ocr',
                    fileMaxSize : 2 * 1024 * 1024
                } ]
            }
        });
    },

    init : function() {
        this.create();
    }
}