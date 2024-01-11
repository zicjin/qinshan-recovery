emcProjectQuery.diagramPage = {

    createPage : function() {
        let _this = this;
        Gikam.create('layout', {
            center : {
                items : [ {
                    type : 'businessProcessDiagram',
                    id : 'emc-business-process-diagram-business-process-diagram',
                    bizId : _this.param.projId,
                    url : '/secure/core/module/item/process-view-confs/process-view',
                } ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
        });
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    },
}
