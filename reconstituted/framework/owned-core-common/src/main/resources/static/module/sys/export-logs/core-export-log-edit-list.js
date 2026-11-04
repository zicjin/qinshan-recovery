coreExportLog.editPage = {

    getExportLogEditList : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-export-log-edit-list-grid',
            url : coreExportLog.baseUrl + '/queries',
            requestData : {
                createdById_SEQ : workspace.user.id
            },
            columns : coreExportLog.getEditListColumns(),
            columnsFill : false,
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,

            center : {
                items : [ _this.getExportLogEditList() ]
            }
        });
    },

    init : function() {
        this.create();
    },

};