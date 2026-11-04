coreQualityControl.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-quality-control-search-list-grid',
            columns : coreQualityControl.getListColumns(),
            url : coreQualityControl.baseUrl + '/queries',
            requestData : {
                processStatus_EQ : 'done'
            },
            onCellClick : function(field, row) {
                field === 'qcNo' && coreQualityControl.loadDetailPage(row.id, 'search');
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-quality-control-search-list-attachment-uploader').done(function(uploader) {
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
                id : 'core-quality-control-search-list-attachment-uploader',
                type : 'uploader',
                multiple : true,
                dbTable : 'T_CORE_QUALITY_CONTROL',
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
            id : 'core-quality-control-search-list-layout',
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
