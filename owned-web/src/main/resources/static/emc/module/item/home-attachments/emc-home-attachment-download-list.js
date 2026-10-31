emcHomeAttachment.downloadListPage = {

    getAttachmentGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-home-attachment-download-list-grid',
            url : emcHomeAttachment.baseUrl + '/queries/raw',
            requestData : {
                targetType : 'download',
                activatedFlag_SEQ : '1'
            },
            columns : emcHomeAttachment.getDownloadListColumns(),
            service : 'emcHomeAttachmentServiceImpl',
            onCellClick : function(field, row) {
                field === 'ext$.filename' && _this.downloadFile(row.id);
            },
            filter : false,
            fill : false,
            page : false
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-home-attachment-download-list-layout',
            renderTo : document.body,
            center : {
                items : [ _this.getAttachmentGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    },

    downloadFile : function(id) {
        var _this = this;
        var url = Gikam.printf(emcHomeAttachment.baseUrl + '/{id}/action/get-url', {
            id : id
        });
        Gikam.postText(url).done(function(data) {
            _this.emcDownloadFile(data);
        });
    },

    emcDownloadFile : function (url, filename) {
        const a = Object.assign(document.createElement('a'), {
            href: url,
            download: filename ?? decodeURIComponent(url.split('/').pop() || 'download')
        });
        document.body.appendChild(a);
        a.click();
        a.remove();
    }
};
