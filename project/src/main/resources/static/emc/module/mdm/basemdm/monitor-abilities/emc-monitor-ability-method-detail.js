emcMonitorAbility.detailMethodPage = Gikam.extend({}, emcMethod.detailPage, {
    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getVersionGridParam() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
            readonly : true
        });
        return this;
    },
})
