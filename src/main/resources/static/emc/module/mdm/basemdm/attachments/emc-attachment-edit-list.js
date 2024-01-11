var emcAttachment = Gikam.getPageObject({});

emcAttachment.editPage = {

    getFileGridParam : function() {
        return {
            type : 'uploader',
            id : 'emc-attachment-edit-list-uploader',
            filter : false,
            bizId : 'index',
            dbTable : 'T_EMC_INDEX_FILE',
            onBeforeUpload : function() {
                this.setUploadData({})
            }
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-attachment-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getFileGridParam() ]
            }
        });
    },

    init : function(param) {
        this.create();
    }
}