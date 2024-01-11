emcFile.viewPage = {
    getUploaderGrid : function() {
        var _this = this;
        return {
            type : 'uploader',
            id : 'emc-file-view-list-uploader',
            dbTable : _this.param.table,
            bizId : _this.param.id,
            bizCategory : !Gikam.isEmpty(_this.param.bizCategory) ? _this.param.bizCategory : '',
            readonly : !Gikam.isEmpty(_this.param.readonly),
            formatterGridColumns : function() {
                return emcFile.getMethodFileListColumns();
            },   
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