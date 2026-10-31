emcRun.methodFilesPage = {

    getBaseInfo : function() {
        var _this = this;
        return {
            type : 'uploader',
            id : 'emc-run-method-file-uploader',
            dbTable : 'T_EMC_METHOD_VERSION',
            bizId : _this.param.methodVersionId,
            onBeforeUpload : function() {
                this.setUploadData({})
            },
            readonly : true
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBaseInfo() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
