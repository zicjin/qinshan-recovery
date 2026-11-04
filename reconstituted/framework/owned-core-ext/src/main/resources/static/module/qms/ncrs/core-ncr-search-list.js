coreNcr.searchPage = {

    getGridParam : function() {
        return {
            type : 'grid',
            id : 'core-ncr-search-list-grid',
            url : coreNcr.baseUrl + '/queries',
            requestData : {
                processStatus_EQ : 'done'
            },
            onCellClick : function(field, row) {
                field === 'ncrNo' && coreNcr.loadDetailPage(row.id, 'search');
            },
            columns : coreNcr.getListColumns(),
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-ncr-search-list-attachment-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
            }
        }
    },

    getAttachmentPanel : function() {
        return {
            title : 'GIKAM.FILE.ATTACHMENT',
            items : [{
                id : 'core-ncr-search-list-attachment-uploader',
                type : 'uploader',
                multiple : true,
                dbTable : 'T_CORE_NCR',
                bizId : 0,
                preview : false,
                readonly : true
            }]
        }
    },

    getTabs : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            showIndex : 0,
            shrink : false,
            panels : [_this.getAttachmentPanel()]
        }
    },


    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-ncr-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '50%',
                items : [_this.getGridParam()]
            },
            center : {
                items : [_this.getTabs()]
            }
        });
    },

    init : function() {
        this.create();
    }
};
