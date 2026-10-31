emcRun.projectPage = Gikam.extend({}, emcProject.editPage, {
    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getGridParam() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
            readonly : true
        });
        return this;
    },
    init : function() {
        this.createPage();
    }
})