emcOrderHandover.annexPage = {

    getUploaderGrid : function() {
        var _this = this;
        return {
            type : 'uploader',
            id : 'emc-sample-file-uploader',
            readonly : true,
            dbTable : 'T_EMC_SAMPLE',
            bizId : _this.param.id
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getUploaderGrid() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    },

}